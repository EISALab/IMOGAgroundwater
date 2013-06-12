package ncsa.d2k.modules.projects.dtcheng.bio;

import ncsa.d2k.modules.core.io.file.*;
import ncsa.d2k.core.modules.InputModule;

import java.util.*;
import java.io.*;
import ncsa.d2k.modules.projects.dtcheng.*;
import ncsa.d2k.modules.projects.dtcheng.primitive.*;
import ncsa.d2k.modules.projects.dtcheng.datatype.*;

public class CollectSequences
    extends InputModule {

  private String ProteomeCSVList = "ALL";
  public void setProteomeCSVList(String value) {
    this.ProteomeCSVList = value;
  }

  public String getProteomeCSVList() {
    return this.ProteomeCSVList;
  }

  private String MotifName = "PF01043";
  public void setMotifName(String value) {
    this.MotifName = value;
  }

  public String getMotifName() {
    return this.MotifName;
  }

  public String getModuleName() {
    return "CollectSequences";
  }

  public String getModuleInfo() {
    return "CollectSequences";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "AccessionHashtable";
      case 1:
        return "ProteomeHashtable";
      case 2:
        return "MotifAccessionNumberHashtable";
      case 3:
        return "SpeciesMotifSequences";
      case 4:
        return "GeneSequences";
    }
    return "";
  }

  public String[] getInputTypes() {
    String[] types = {
        "java.util.Hashtable",
        "java.util.Hashtable",
        "java.util.Hashtable",
        "[[[[I",
        "[[B"
    };
    return types;
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "AccessionHashtable";
      case 1:
        return "ProteomeHashtable";
      case 2:
        return "MotifAccessionNumberHashtable";
      case 3:
        return "SpeciesMotifSequences";
      case 4:
        return "GeneSequences";
    }
    return "";
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "SelectedSequences";
      case 1:
        return "SequenceNames";
      default:
        return "No such output";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "[[B",
        "[S"
    };
    return types;
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "SelectedSequences";
      case 1:
        return "SequenceNames";
      default:
        return "No such output";
    }
  }

  public void doit() throws Exception {

    Hashtable AccessionNumberToAccessionAndSequenceIndexHashtable = (Hashtable)this.pullInput(0);
    Hashtable ProteomeHashtable = (Hashtable)this.pullInput(1);
    Hashtable MotifAccessionNumberHashtable = (Hashtable)this.pullInput(2);
    int[][][][] ProteomeMotifSequences = (int[][][][])this.pullInput(3);
    byte[][] GeneSequences = (byte[][])this.pullInput(4);

    Hashtable AccesionIndexToAccessionNumberHashtable = new Hashtable();
    Hashtable SequenceIndexToAccesionIndexHashtable = new Hashtable();
    {
      Enumeration enumeration = AccessionNumberToAccessionAndSequenceIndexHashtable.keys();
      while (enumeration.hasMoreElements()) {
        String key = (String) enumeration.nextElement();

        //System.out.println(key);

        int[] AccessionAndSequenceIndices = (int[]) AccessionNumberToAccessionAndSequenceIndexHashtable.get( (Object) key);
        Integer AccessionIndex = new Integer(AccessionAndSequenceIndices[0]);
        Integer SequenceIndex = new Integer(AccessionAndSequenceIndices[1]);
        AccesionIndexToAccessionNumberHashtable.put(AccessionIndex, key);
        SequenceIndexToAccesionIndexHashtable.put(SequenceIndex, AccessionIndex);
      }
    }

    int numMotifs = MotifAccessionNumberHashtable.size();
    Enumeration MotifEnumeration = MotifAccessionNumberHashtable.keys();
    String [] MotifNames = new String[numMotifs];
    while (MotifEnumeration.hasMoreElements()) {
      String Key = (String) MotifEnumeration.nextElement();
      Integer index = (Integer) MotifAccessionNumberHashtable.get(Key);
      MotifNames[index.intValue()] = Key;
    }

    int numProteomes = ProteomeHashtable.size();
    Enumeration ProteomeEnumeration = ProteomeHashtable.keys();
    String [] ProteomeNames = new String[numProteomes];
    while (ProteomeEnumeration.hasMoreElements()) {
      String Key = (String) ProteomeEnumeration.nextElement();
      Integer index = (Integer) ProteomeHashtable.get(Key);
      ProteomeNames[index.intValue()] = Key;
    }

    boolean UseAllProteomes = false;
    int NumSelectedProteomes = -1;
    int[] SelectedProteomeIndices = null;
    if (ProteomeCSVList.equalsIgnoreCase("") ||
        ProteomeCSVList.equalsIgnoreCase("ALL")) {
      UseAllProteomes = true;
    }
    else {
      String[] SelectedProteomeNames = Utility.parseCSVList(ProteomeCSVList);
      NumSelectedProteomes = SelectedProteomeNames.length;
      System.out.println("NumSelectedProteomes = " + NumSelectedProteomes);
      int numFound = 0;
      for (int i = 0; i < NumSelectedProteomes; i++) {
        System.out.println("SelectedProteomeNames[" + i + "] = " + SelectedProteomeNames[i]);
        if (ProteomeHashtable.containsKey(SelectedProteomeNames[i])) {
          System.out.println(SelectedProteomeNames[i] + " in HT");
          numFound++;
        }

      }

      NumSelectedProteomes = numFound;
      int index = 0;
      SelectedProteomeIndices = new int[NumSelectedProteomes];
      for (int i = 0; i < NumSelectedProteomes; i++) {
        if (ProteomeHashtable.containsKey(SelectedProteomeNames[i])) {
          SelectedProteomeIndices[index] = ( (Integer) ProteomeHashtable.get(
              SelectedProteomeNames[i])).intValue();
          System.out.println("SelectedProteomeIndices[" + index + "] = " +
                             SelectedProteomeIndices[index]);
          index++;
        }
      }
      NumSelectedProteomes = numFound;
    }

    if (!MotifName.equalsIgnoreCase("all") && !MotifAccessionNumberHashtable.containsKey(MotifName)) {
      System.out.println("Error!  MotifName = " + MotifName + " not found");
      this.pushOutput(null, 0);
      this.pushOutput(null, 1);

    }
    else {

      int InitialMotifIndex;
      int NumIterations;

      if (MotifName.equalsIgnoreCase("all")) {
        InitialMotifIndex = 0;
        NumIterations = MotifAccessionNumberHashtable.size();
        //NumIterations = 3;
      }
      else {
        InitialMotifIndex = ( (Integer) MotifAccessionNumberHashtable.get(MotifName)).intValue();
        NumIterations = 1;
      }

      int NumProteomes = ProteomeHashtable.size();
      System.out.println("NumSpecies = " + NumProteomes);

      String MotifName;
      String ProteomeName;

      System.out.println("MotifName,ProteomeName,NumMotifTotalOccurences,NumProteomeSequences");
      for (int MotifIndex = InitialMotifIndex; MotifIndex < InitialMotifIndex + NumIterations; MotifIndex++) {

        MotifName = MotifNames[MotifIndex];

        //System.out.println("MotifIndex = " + MotifIndex);
        //System.out.println("MotifName = " + MotifName);

        int MaxNumSelectedSequences = 10000;
        int[][] SelectedProteinSequences = new int[MaxNumSelectedSequences][3];
        int NumSelectedSequences = 0;

        for (int ProteomeIndex = 0; ProteomeIndex < NumProteomes; ProteomeIndex++) {

          ProteomeName = ProteomeNames[ProteomeIndex];
          //System.out.println("ProteomeIndex = " + ProteomeIndex);
          //System.out.println("ProteomeName = " + ProteomeName);

          boolean currentProteomeIsInSelection = false;

          if (UseAllProteomes) {
            currentProteomeIsInSelection = true;
          }
          else {
            for (int i = 0; i < NumSelectedProteomes; i++) {
              if (SelectedProteomeIndices[i] == ProteomeIndex) {
                currentProteomeIsInSelection = true;
                break;
              }
            }
          }

          if (!currentProteomeIsInSelection) {
            continue;
          }

          int[][] Sequences = ProteomeMotifSequences[ProteomeIndex][MotifIndex];
          int NumMotifTotalOccurences;
          if (Sequences == null) {
            NumMotifTotalOccurences = 0;
          }
          else {
            NumMotifTotalOccurences = Sequences.length;
          }

          int NumProteomeSequences = 0;
          // create unique set of sequences
          for (int i = 0; i < NumMotifTotalOccurences; i++) {

            int [] Sequence = Sequences[i];
            boolean ProteinSequenceAlreadyInSet = false;
            for (int j = 0; j < NumSelectedSequences; j++) {
              if (SelectedProteinSequences[j][0] == Sequence[0]) {
                ProteinSequenceAlreadyInSet = true;
                break;
              }
            }
            if (!ProteinSequenceAlreadyInSet) {

              int SequenceIndex = Sequence[0];
              if (false) {
                Integer AccessionIndex = (Integer) SequenceIndexToAccesionIndexHashtable.get(new Integer(SequenceIndex));
                String  AccessionNumber = (String) AccesionIndexToAccessionNumberHashtable.get(AccessionIndex);
                System.out.println("[" + (NumProteomeSequences + 1) + "] : " + AccessionNumber);
              }

              SelectedProteinSequences[NumSelectedSequences] = Sequence;
              NumSelectedSequences++;
              NumProteomeSequences++;

            }
          }
          //System.out.println("NumProteomeSequences = " + NumProteomeSequences);
          System.out.println(MotifName + "," + ProteomeName + "," + NumMotifTotalOccurences + "," + NumProteomeSequences);
        }



        byte[][] SubSequencesForClustering = new byte[NumSelectedSequences][];
        String[] SequenceNames = new String[NumSelectedSequences];

        for (int i = 0; i < NumSelectedSequences; i++) {

          int motifStartIndex = SelectedProteinSequences[i][1];
          int motifEndIndex = SelectedProteinSequences[i][2];
          int numAAs = motifEndIndex - motifStartIndex + 1;

          int SequenceIndex = SelectedProteinSequences[i][0];
          byte [] SequenceElements = GeneSequences[SequenceIndex];

          if (motifStartIndex < 0) {
            continue;
          }
          if (motifEndIndex < motifStartIndex) {
            continue;
          }
          if (motifEndIndex >=  SequenceElements.length) {
            //System.out.println("Error! motifEndIndex (" + motifEndIndex + ") >  SequenceElements.length (" + SequenceElements.length + ")");
            continue;
          }

          if (false) {

            //int numAAs = GeneSequences[SelectedSequences[i]].length;
            for (int aa = 0; aa < numAAs; aa++) {
              System.out.print( (char) SequenceElements[aa + motifStartIndex]);
            }
            System.out.println();
          }

          byte [] SubSequence = new byte[numAAs];
          for (int aa = 0; aa < numAAs; aa++) {
            SubSequence[aa] = GeneSequences[SequenceIndex][aa + SelectedProteinSequences[i][1]];
          }

          SubSequencesForClustering[i] = SubSequence;
          Integer SequenceIndexKey = new Integer(SequenceIndex);

          Integer AccessionIndexKey = (Integer) SequenceIndexToAccesionIndexHashtable.get( (Object) SequenceIndexKey);
          SequenceNames[i] = (String) AccesionIndexToAccessionNumberHashtable.get( (Object) AccessionIndexKey);

        }

        this.pushOutput(SubSequencesForClustering, 0);
        this.pushOutput(SequenceNames, 1);

      }
    }
  }

}