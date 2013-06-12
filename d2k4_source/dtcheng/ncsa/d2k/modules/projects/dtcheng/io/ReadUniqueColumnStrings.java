package ncsa.d2k.modules.projects.dtcheng.io;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import java.io.*;
import java.util.*;
import java.text.*;

import ncsa.d2k.modules.projects.dtcheng.*;
import ncsa.d2k.modules.projects.dtcheng.datatype.*;
import ncsa.d2k.modules.projects.dtcheng.primitive.*;


public class ReadUniqueColumnStrings extends InputModule {

  //////////////////
  //  PROPERTIES  //
  //////////////////

  private String DataPath = "table.csv";
  public void setDataPath(String value) {
    this.DataPath = value;
  }


  public String getDataPath() {
    return this.DataPath;
  }


  private int DelimiterByte = (int) ',';
  public void setDelimiterByte(int value) {
    this.DelimiterByte = value;
  }


  public int getDelimiterByte() {
    return this.DelimiterByte;
  }


  private int NumRowsToSkip = 1;
  public void setNumRowsToSkip(int value) {
    this.NumRowsToSkip = value;
  }


  public int getNumRowsToSkip() {
    return this.NumRowsToSkip;
  }


  private int NumRowsToRead = 60;
  public void setNumRowsToRead(int value) {
    this.NumRowsToRead = value;
  }


  public int getNumRowsToRead() {
    return this.NumRowsToRead;
  }


  private int EndOfLineByte1 = 10;
  public void setEndOfLineByte1(int value) {
    this.EndOfLineByte1 = value;
  }


  public int getEndOfLineByte1() {
    return this.EndOfLineByte1;
  }


  private int ReadBufferSize = 1000000;
  public void setReadBufferSize(int value) {
    this.ReadBufferSize = value;
  }


  public int getReadBufferSize() {
    return this.ReadBufferSize;
  }


  private String NominalFeatureTypeList = "Y,N";
  public void setNominalFeatureTypeList(String value) {
    this.NominalFeatureTypeList = value;
  }


  public String getNominalFeatureTypeList() {
    return this.NominalFeatureTypeList;
  }


  public String getModuleName() {
    return "ReadUniqueColumnStrings";
  }


  public String getModuleInfo() {
    return "ReadUniqueColumnStrings comma = 44; space = 32; tab = 9; ";
  }


  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "ColumnUniqueStrings";
    }
    return "";
  }


  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "ColumnUniqueStrings";
    }
    return "";
  }


  public String[] getOutputTypes() {
    String[] out = {
        "[[S"};
    return out;
  }


  /**
   * Return the human readable name of the indexed input.
   * @param index the index of the input.
   * @return the human readable name of the indexed input.
   */
  public String getInputName(int index) {
    switch (index) {
      default:
        return "NO SUCH INPUT!";
    }
  }


  public String getInputInfo(int index) {
    switch (index) {
      default:
        return "No such input";
    }
  }


  public String[] getInputTypes() {
    String[] types = {};
    return types;
  }






  public final String[] SortStrings(String[] labels) {
    Arrays.sort(labels, new StringComp());
    return labels;
  }


  private final class StringComp implements Comparator {
    public int compare(Object o1, Object o2) {
      String s1 = (String) o1;
      String s2 = (String) o2;
      return s1.toLowerCase().compareTo(s2.toLowerCase());
    }


    public boolean equals(Object o) {
      return super.equals(o);
    }
  }



  ////////////
  //  MAIN  //
  ////////////

  public void doit() throws Exception {

    //System.out.println("user.dir = " + System.getProperty("user.dir"));


    /////////////////////////////////
    // parse paramter string lists //
    /////////////////////////////////

    String[] FeatureNumbersToMakeNominal = Utility.parseCSVList(NominalFeatureTypeList);
    int NumColumns = FeatureNumbersToMakeNominal.length;

    boolean[] NominalColumn = new boolean[NumColumns];

    for (int i = 0; i < NumColumns; i++) {
      if (FeatureNumbersToMakeNominal[i].equalsIgnoreCase("Y")) {
        NominalColumn[i] = true;
      }
    }

    int NumExamples = NumRowsToRead;

    int starts[] = null;
    int ends[] = null;
    byte buffer[] = null;

    int example_index = 0;

    FlatFile Rio = new FlatFile(DataPath, "r", ReadBufferSize, true);
    buffer = Rio.Buffer;
    Rio.DelimiterByte = (byte) DelimiterByte;
    Rio.EOLByte1 = (byte) EndOfLineByte1;

    // skip header if necessary
    for (int r = 0; r < NumRowsToSkip; r++) {
      Rio.readLine();
    }

    Hashtable[] FeatureHashTable = new Hashtable[NumColumns];

    for (int i = 0; i < NumColumns; i++)
      FeatureHashTable[i] = new Hashtable();

    // set UniqueValueCount to zero
    int[] UniqueValueCount = new int[NumColumns];

    for (int e = 0; e < NumRowsToRead; e++) {

      Rio.readLine();

      for (int f = 0; f < NumColumns; f++) {

        if (NominalColumn[f]) {

          String Name;

          Name = new String(buffer, Rio.ColumnStarts[f], Rio.ColumnEnds[f] - Rio.ColumnStarts[f]);

          double value = 0.0;

          if (!FeatureHashTable[f].containsKey(Name)) {
            FeatureHashTable[f].put(Name, new Integer(UniqueValueCount[f]));
            UniqueValueCount[f]++;
          }

        }

      }

    }
    Rio.close();

    String[][] NominalNames = new String[NumColumns][];

    for (int f = 0; f < NumColumns; f++) {

      NominalNames[f] = new String[UniqueValueCount[f]];

      Enumeration InputKeys = FeatureHashTable[f].keys();
      for (int v = 0; v < UniqueValueCount[f]; v++) {
        String key = (String) InputKeys.nextElement();
        int index = ((Integer) FeatureHashTable[f].get(key)).intValue();
        NominalNames[f][index] = key;
      }

      NominalNames[f] = SortStrings(NominalNames[f]);
    }

    this.pushOutput(NominalNames, 0);

  }

}
