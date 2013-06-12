package ncsa.d2k.modules.projects.dtcheng.bio;
import ncsa.d2k.modules.core.io.file.*;
import ncsa.d2k.core.modules.InputModule;

import java.util.*;
import java.io.*;
import ncsa.d2k.modules.projects.dtcheng.io.*;
import ncsa.d2k.modules.projects.dtcheng.*;

public class ParseMotifDB
    extends InputModule {
  //////////////////
  //  PROPERTIES  //
  //////////////////

  private String DirectoryPath = "/";
  public void setDirectoryPath(String value) {
    this.DirectoryPath = value;
  }

  public String getDirectoryPath() {
    return this.DirectoryPath;
  }

  public String FileNameExtension = ".IPC";
  public void setFileNameFilter(String value) {
    this.FileNameExtension = value;
  }

  public String getFileNameFilter() {
    return this.FileNameExtension;
  }

  public String getModuleName() {
    return "ParseMotifDB";
  }

  public String getModuleInfo() {
    return "ParseMotifDB";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "AccessionNumberToAccessionAndSequenceIndexHashTable";
    }
    return "";
  }

  public String[] getInputTypes() {
    String[] types = {
    "java.util.Hashtable"
    };
    return types;
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "AccessionNumberToAccessionAndSequenceIndexHashTable";
    }
    return "";
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "SpeciesNames";
      case 1:
        return "InterProAccessionNumbers";
      case 2:
        return "SpeciesMotifSequences";
      default:
        return "No such output";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "java.util.Hashtable",
        "java.util.Hashtable",
        "[[[[I"
    };
    return types;
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "SpeciesNames";
      case 1:
        return "InterProAccessionNumbers";
      case 2:
        return "SpeciesMotifSequences";
      default:
        return "No such output";
    }
  }

  public void doit() throws Exception {

    Hashtable SpeciesNames = new Hashtable();
    Hashtable InterProAccessionNumbers = new Hashtable();
    Hashtable AccessionNumberToAccessionAndSequenceIndexHashTable = (Hashtable) this.pullInput(0);
    int InterProAccessionNumberIndex = 0;

    File directory = new File(DirectoryPath);
    ExtensionFileFilter filter = new ExtensionFileFilter(FileNameExtension);

    File[] files = directory.listFiles(filter);

    if (files == null) {
      System.out.println("Error (files == null)");
      throw new Exception();
    }


    int NumSpecies = files.length;


    // put species names in has table
    for (int SpeciesIndex = 0; SpeciesIndex < NumSpecies; SpeciesIndex++) {
      String FileName = files[SpeciesIndex].getName();
      String SpeciesName = FileName.substring(0, FileName.length() - 4);
      System.out.println(SpeciesName);

      if (SpeciesNames.containsKey(SpeciesName)) {
        System.out.println("Error");
        throw new Exception();
      }
      else {
        System.out.println("adding " + SpeciesName + " = " + SpeciesIndex);
        SpeciesNames.put(SpeciesName, new Integer(SpeciesIndex));
      }
    }

    // process each species motif file
    int MaxNumMotifs = 20000;
    int [][][][] SpeciesMotifSequences = new int [NumSpecies][MaxNumMotifs][][];
    for (int SpeciesIndex = 0; SpeciesIndex < NumSpecies; SpeciesIndex++) {
      String FileName = DirectoryPath + files[SpeciesIndex].getName();
      System.out.println("FileName = " + FileName);
      FlatFile FlatFile = new FlatFile(FileName, "r", 100000, true);
      FlatFile.DelimiterByte = FlatFile.TabByte;

      byte [] buffer = FlatFile.Buffer;
      int [] ColumnStarts =  FlatFile.ColumnStarts;
      int [] ColumnEnds   =  FlatFile.ColumnEnds;

      // skip header
      int NumColumns;
      String LineString;     FlatFile.readLine();
      if (false) {
        NumColumns = FlatFile.NumColumns;
        LineString = new String(buffer, ColumnStarts[0], (int) ColumnEnds[NumColumns - 1] - ColumnStarts[0] + 1);
        System.out.println("LineString = " + LineString);
      }

      while (!FlatFile.EOF) {

        FlatFile.readLine();

        if (FlatFile.EOF) {
          break;
        }

        NumColumns = FlatFile.NumColumns;


        if (NumColumns < 5) {
          continue;
        }
        if (ColumnEnds[3] - ColumnStarts[3] < 1) {
          continue;
        }
        if (ColumnEnds[4] - ColumnStarts[4] < 1) {
          continue;
        }

        String SprotTremblAccessionNumber = new String(buffer, ColumnStarts[0], (int) ColumnEnds[0] - ColumnStarts[0]);
        //String InterProlAccessionNumber   = new String(buffer, ColumnStarts[2], (int) ColumnEnds[2] - ColumnStarts[2]);  //!!! fails on short lines
        String MotifMethodAC   = new String(buffer, ColumnStarts[1], (int) ColumnEnds[1] - ColumnStarts[1]);  //!!! fails on short lines
        String StartPos   = new String(buffer, ColumnStarts[3], (int) ColumnEnds[3] - ColumnStarts[3]);  //!!! fails on short lines
        String EndPos   = new String(buffer, ColumnStarts[4], (int) ColumnEnds[4] - ColumnStarts[4]);  //!!! fails on short lines

        int StartIndex;
        int EndIndex;

        try {
          StartIndex = Integer.parseInt(StartPos.trim()) - 1;
          EndIndex   = Integer.parseInt(EndPos.trim()) - 1;
        }
        catch (Exception e) {
          System.out.println("Could not part Start and End positions");
           LineString = new String(buffer, ColumnStarts[0], (int) ColumnEnds[NumColumns - 1] - ColumnStarts[0] + 1);
          System.out.println("LineString = " + LineString);
          System.out.println("StartPos = " + StartPos);
          System.out.println("EndPos   = " + EndPos);
          StartIndex = -1;
          EndIndex = -1;
        }

        if (false) {
          System.out.println("FileName = " + FileName);
          System.out.println("SprotTremblAccessionNumber = " + SprotTremblAccessionNumber);
          System.out.println("InterProlAccessionNumber   = " + MotifMethodAC);
          System.out.println("StartPos = " + StartPos);
          System.out.println("EndPos   = " + EndPos);
        }

        if (!AccessionNumberToAccessionAndSequenceIndexHashTable.containsKey(SprotTremblAccessionNumber)) {
          System.out.println("Error!!! accession number (" + SprotTremblAccessionNumber + ") not found in sprot+trembl db -- ignoring record");
        }
        else {
          if (!InterProAccessionNumbers.containsKey(MotifMethodAC)) {
            InterProAccessionNumbers.put(MotifMethodAC, new Integer(InterProAccessionNumberIndex));
            InterProAccessionNumberIndex++;
          }
          int motifIndex = ((Integer) InterProAccessionNumbers.get(MotifMethodAC)).intValue();
          int sequenceIndex = ((int []) AccessionNumberToAccessionAndSequenceIndexHashTable.get(SprotTremblAccessionNumber))[1];

          // add to list of motif sequence occurences
          if (SpeciesMotifSequences[SpeciesIndex][motifIndex] == null) {
            SpeciesMotifSequences[SpeciesIndex][motifIndex] = new int [1][3];
            SpeciesMotifSequences[SpeciesIndex][motifIndex][0][0] = sequenceIndex;
            SpeciesMotifSequences[SpeciesIndex][motifIndex][0][1] = StartIndex;
            SpeciesMotifSequences[SpeciesIndex][motifIndex][0][2] = EndIndex;
          }
          else {
            int [][] oldSequences = SpeciesMotifSequences[SpeciesIndex][motifIndex];
            int [][] newSequences = new int[oldSequences.length + 1][3];
            for (int i = 0; i < oldSequences.length; i++) {
              newSequences[i][0] = oldSequences[i][0];
              newSequences[i][1] = oldSequences[i][1];
              newSequences[i][2] = oldSequences[i][2];
            }
            //System.arraycopy(oldSequences, 0, newSequences, 0, oldSequences.length);
            newSequences[oldSequences.length][0] = sequenceIndex;
            newSequences[oldSequences.length][1] = StartIndex;
            newSequences[oldSequences.length][2] = EndIndex;
            SpeciesMotifSequences[SpeciesIndex][motifIndex] = newSequences;
          }
        }
      }
      FlatFile.close();
    }
    System.out.println("InterProAccessionNumberIndex = " + InterProAccessionNumberIndex);

    this.pushOutput(SpeciesNames,             0);
    this.pushOutput(InterProAccessionNumbers, 1);
    this.pushOutput(SpeciesMotifSequences,    2);
  }
}