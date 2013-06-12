package ncsa.d2k.modules.projects.dtcheng.io;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import java.io.*;
import java.util.*;
import java.text.*;

import ncsa.d2k.modules.projects.dtcheng.*;
import ncsa.d2k.modules.projects.dtcheng.datatype.*;
import ncsa.d2k.modules.projects.dtcheng.primitive.*;


public class ReadNominalExampleTable extends InputModule {

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


  private boolean UseJavaDoubleParser = false;
  public void setUseJavaDoubleParser(boolean value) {
    this.UseJavaDoubleParser = value;
  }


  public boolean getUseJavaDoubleParser() {
    return this.UseJavaDoubleParser;
  }


  private boolean NominalToBooleanExpansion = false;
  public void setNominalToBooleanExpansion(boolean value) {
    this.NominalToBooleanExpansion = value;
  }


  public boolean getNominalToBooleanExpansion() {
    return this.NominalToBooleanExpansion;
  }


  private int EndOfLineByte1 = 10;
  public void setEndOfLineByte1(int value) {
    this.EndOfLineByte1 = value;
  }


  public int getEndOfLineByte1() {
    return this.EndOfLineByte1;
  }


  private int CharsPerColumn = 16;
  public void setCharsPerColumn(int value) {
    this.CharsPerColumn = value;
  }


  public int getCharsPerColumn() {
    return this.CharsPerColumn;
  }


  private int ReadBufferSize = 1000000;
  public void setReadBufferSize(int value) {
    this.ReadBufferSize = value;
  }


  public int getReadBufferSize() {
    return this.ReadBufferSize;
  }


  private String FeatureTypeList = "I,O";
  public void setFeatureTypeString(String value) {
    this.FeatureTypeList = value;
  }


  public String getFeatureTypeString() {
    return this.FeatureTypeList;
  }


  private String NominalFeatureTypeList = "Y,N";
  public void setNominalFeatureTypeList(String value) {
    this.NominalFeatureTypeList = value;
  }


  public String getNominalFeatureTypeList() {
    return this.NominalFeatureTypeList;
  }


  public String getModuleName() {
    return "ReadNominalExampleTable";
  }


  public String getModuleInfo() {
    return "ReadNominalExampleTable comma = 44; space = 32; tab = 9; ";
  }


  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "ExampleTable";
      case 1:
        return "InputFeatureStrings";
      case 2:
        return "OutputFeatureStrings";
    }
    return "";
  }


  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "ExampleTable";
      case 1:
        return "InputFeatureStrings";
      case 2:
        return "OutputFeatureStrings";
    }
    return "";
  }


  public String[] getOutputTypes() {
    String[] out = {
        "ncsa.d2k.modules.core.datatype.table.ExampleTable",
        "[[S",
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
      case 0:
        return "UniqueColumnStrings";
      default:
        return "No such input";
    }
  }


  public String getInputInfo(int index) {
    switch (index) {
      case 0:
        return "UniqueColumnStrings";
      default:
        return "No such input";
    }
  }


  public String[] getInputTypes() {
    String[] types = {"[[S"};
    return types;
  }


  ////////////
  //  MAIN  //
  ////////////

  public void doit() throws Exception {


    // read nominal feature strings
    String[][] UniqueColumnStrings = (String[][])this.pullInput(0);

    int [] ColumnNumNominalValues = new int[UniqueColumnStrings.length];

    for (int i = 0; i < UniqueColumnStrings.length; i++) {
      ColumnNumNominalValues[i] = UniqueColumnStrings[i].length;
    }

    /////////////////////////////////
    // parse paramter string lists //
    /////////////////////////////////

    String[] FeatureTypeStrings = null;
    FeatureTypeStrings = Utility.parseCSVList(FeatureTypeList);

    int NumColumns = FeatureTypeStrings.length;

    String[] FeatureNumbersToMakeNominal = Utility.parseCSVList(NominalFeatureTypeList);

    boolean[] NominalColumn = new boolean[NumColumns];
    for (int i = 0; i < NumColumns; i++) {
      if (FeatureNumbersToMakeNominal[i].equalsIgnoreCase("Y")) {
        NominalColumn[i] = true;
      }
    }

    int NumNominalInputFeatures = 0;
    int NumNominalOutputFeatures = 0;
    int NumBooleanNominalInputFeatures = 0;
    int NumBooleanNominalOutputFeatures = 0;

    int NumExamples = NumRowsToRead;

    boolean[] InputColumnSelected = new boolean[NumColumns];
    boolean[] OutputColumnSelected = new boolean[NumColumns];
    int[] InputColumnIndices = new int[NumColumns];
    int[] OutputColumnIndices = new int[NumColumns];

    int NumInputColumns = 0;
    int NumOutputColumns = 0;
    for (int i = 0; i < NumColumns; i++) {
      switch (FeatureTypeStrings[i].getBytes()[0]) {
        case (byte) 'I':
        case (byte) 'i':
          InputColumnSelected[i] = true;
          InputColumnIndices[NumInputColumns++] = i;

          if (NominalToBooleanExpansion && NominalColumn[i]) {
            NumNominalInputFeatures++;
            NumBooleanNominalInputFeatures += UniqueColumnStrings[i].length;
          }

          break;
        case (byte) 'O':
        case (byte) 'o':
          OutputColumnSelected[i] = true;
          OutputColumnIndices[NumOutputColumns++] = i;

          if (NominalToBooleanExpansion && NominalColumn[i]) {
            NumNominalOutputFeatures++;
            NumBooleanNominalOutputFeatures += UniqueColumnStrings[i].length;
          }
          break;
      }
    }

    if (NominalToBooleanExpansion) {
      System.out.println("NumNominalInputFeatures  = " + NumNominalInputFeatures);
      System.out.println("NumNominalOutputFeatures = " + NumNominalOutputFeatures);
      System.out.println("NumBooleanNominalInputFeatures  = " + NumBooleanNominalInputFeatures);
      System.out.println("NumBooleanNominalOutputFeatures = " + NumBooleanNominalOutputFeatures);
    }


    int NumFeatures;

    int NumInputs;
    int NumOutputs;
    if (NominalToBooleanExpansion) {
      NumInputs = NumInputColumns - NumNominalInputFeatures + NumBooleanNominalInputFeatures;
      NumOutputs = NumOutputColumns - NumNominalOutputFeatures + NumBooleanNominalOutputFeatures;
    }
    else {
      NumInputs = NumInputColumns;
      NumOutputs = NumOutputColumns;
    }


    System.out.println("NumInputs  = " + NumInputs);
    System.out.println("NumOutputs = " + NumOutputs);

    NumFeatures = NumInputs + NumOutputs;

    double[] Data = new double[NumExamples * NumFeatures];

    String[] ColumnNames = new String[NumColumns];

    int starts[] = null;
    int ends[] = null;
    byte buffer[] = null;

    int example_index = 0;

    /////////////////////////
    //  READ COLUMN NAMES  //
    /////////////////////////

    {
      File.listRoots();
      FlatFile rio = new FlatFile(DataPath, "r", ReadBufferSize, true);
      buffer = rio.Buffer;
      rio.DelimiterByte = (byte) DelimiterByte;
      rio.EOLByte1 = (byte) EndOfLineByte1;

      rio.readLine();
      String name;

      for (int f = 0; f < NumColumns; f++) {

        name = new String(buffer, rio.ColumnStarts[f], rio.ColumnEnds[f] - rio.ColumnStarts[f]);

        ColumnNames[f] = name;

      }

      rio.close();
    }

    ///////////////////////////////////////////////////////////
    //  READ WHOLE FILE AND MAP NOMINALS TO NUMERIC INDICES  //
    ///////////////////////////////////////////////////////////

    FlatFile Rio = new FlatFile(DataPath, "r", ReadBufferSize, true);
    buffer = Rio.Buffer;
    Rio.DelimiterByte = (byte) DelimiterByte;
    Rio.EOLByte1 = (byte) EndOfLineByte1;

    // skip header if necessary
    for (int r = 0; r < NumRowsToSkip; r++) {
      Rio.readLine();
    }

    Hashtable[] InputFeatureHashTable = new Hashtable[NumInputColumns];
    Hashtable[] OutputFeatureHashTable = new Hashtable[NumOutputColumns];

    for (int i = 0; i < NumInputColumns; i++) {
      InputFeatureHashTable[i] = new Hashtable();
    }
    for (int i = 0; i < NumOutputColumns; i++) {
      OutputFeatureHashTable[i] = new Hashtable();
    }

    int[] InputUniqueValueCount = new int[NumInputColumns];
    int[] OutputUniqueValueCount = new int[NumOutputColumns];


    // initialize the hash tables mapping nominal strings to integer values
    {
      int InputFeatureIndex = 0;
      int OutputFeatureIndex = 0;
      for (int f = 0; f < NumColumns; f++) {

        if (NominalColumn[f]) {

          if (InputColumnSelected[f]) {

            for (int i = 0; i < UniqueColumnStrings[f].length; i++) {
              String Name = UniqueColumnStrings[f][i];
              if (!InputFeatureHashTable[InputFeatureIndex].containsKey(Name)) {
                InputFeatureHashTable[InputFeatureIndex].put(Name, new Integer(InputUniqueValueCount[InputFeatureIndex]));
                InputUniqueValueCount[InputFeatureIndex]++;
              }
            }

            InputFeatureIndex++;

          }

          if (OutputColumnSelected[f]) {

            for (int i = 0; i < UniqueColumnStrings[f].length; i++) {
              String Name = UniqueColumnStrings[f][i];
              if (!OutputFeatureHashTable[OutputFeatureIndex].containsKey(Name)) {
                OutputFeatureHashTable[OutputFeatureIndex].put(Name, new Integer(OutputUniqueValueCount[OutputFeatureIndex]));
                OutputUniqueValueCount[OutputFeatureIndex]++;
              }
            }

            OutputFeatureIndex++;

          }

        }
      }
    }




    ///////////////////////////////////////////////////////
    // READ WHOLE FILE WHILE MAPPING NOMINALS TO NUMBERS //
    ///////////////////////////////////////////////////////

    double[] columnValues = new double[NumColumns];

    for (int e = 0; e < NumRowsToRead; e++) {

      Rio.readLine();

      int InputFeatureIndex = 0;
      int OutputFeatureIndex = 0;
      for (int f = 0; f < NumColumns; f++) {

        if (NominalColumn[f]) {

          String NominalString;

          NominalString = new String(buffer, Rio.ColumnStarts[f], Rio.ColumnEnds[f] - Rio.ColumnStarts[f]);

          double value = 0.0;

          if (InputColumnSelected[f]) {

            if (InputFeatureHashTable[InputFeatureIndex].containsKey(NominalString)) {
              value = ((Integer) InputFeatureHashTable[InputFeatureIndex].get(NominalString)).intValue();
            }
            else {
              throw new Exception();
            }

            InputFeatureIndex++;

          }

          if (OutputColumnSelected[f]) {

            if (OutputFeatureHashTable[OutputFeatureIndex].containsKey(NominalString)) {
              value = ((Integer) OutputFeatureHashTable[OutputFeatureIndex].get(NominalString)).intValue();
            }
            else {
              throw new Exception();
            }

            OutputFeatureIndex++;

          }

          columnValues[f] = value;

        }
        else {

          // read as a continuous floating point number

          if (UseJavaDoubleParser) {
            String string = new String(buffer, Rio.ColumnStarts[f], Rio.ColumnEnds[f]);
            columnValues[f] = Double.parseDouble(string);
          }
          else {
            columnValues[f] = Rio.ByteStringToDouble(buffer, Rio.ColumnStarts[f], Rio.ColumnEnds[f]);
          }

        }
      }


    if (NominalToBooleanExpansion) {
      int InputIndex = 0;
      for (int i = 0; i < NumInputColumns; i++) {
        if (NominalColumn[InputColumnIndices[i]]) {
          for (int NominalIndex = 0; NominalIndex < ColumnNumNominalValues[InputColumnIndices[i]]; NominalIndex++) {
            if (columnValues[InputColumnIndices[i]] == NominalIndex) {
              Data[e * NumFeatures + InputIndex] = 1;
            }
            else {
              Data[e * NumFeatures + InputIndex] = 0;
            }
            InputIndex++;
          }
        }
        else {
          Data[e * NumFeatures + InputIndex] = (double) columnValues[InputColumnIndices[i]];
          InputIndex++;
        }
      }
      int OutputIndex = 0;
      for (int i = 0; i < NumOutputColumns; i++) {
        if (NominalColumn[OutputColumnIndices[i]]) {
          for (int NominalIndex = 0; NominalIndex < ColumnNumNominalValues[OutputColumnIndices[i]]; NominalIndex++) {
            if (columnValues[OutputColumnIndices[i]] == NominalIndex) {
              Data[e * NumFeatures + NumInputs + OutputIndex] = 1;
            }
            else {
              Data[e * NumFeatures + NumInputs + OutputIndex] = 0;
            }
            OutputIndex++;
          }
        }
        else {
          Data[e * NumFeatures + OutputIndex] = (double) columnValues[OutputColumnIndices[i]];
          OutputIndex++;
        }
      }
    }
    else {
      // add values to the example table raw data array
      for (int i = 0; i < NumInputColumns; i++) {
        Data[e * NumFeatures + i] = (double) columnValues[InputColumnIndices[i]];
      }
      for (int i = 0; i < NumOutputColumns; i++) {
        Data[e * NumFeatures + NumInputs + i] = (double) columnValues[OutputColumnIndices[i]];
      }
    }

    }
    Rio.close();

    String[][] InputNominalNames = new String[NumInputColumns][];

    for (int i = 0; i < NumInputColumns; i++) {

      InputNominalNames[i] = new String[InputUniqueValueCount[i]];

      Enumeration InputKeys = InputFeatureHashTable[i].keys();
      for (int v = 0; v < InputUniqueValueCount[i]; v++) {
        String key = (String) InputKeys.nextElement();
        int index = ((Integer) InputFeatureHashTable[i].get(key)).intValue();
        InputNominalNames[i][index] = key;
      }
    }

    String[][] OutputNominalNames = new String[NumOutputColumns][];

    for (int i = 0; i < NumOutputColumns; i++) {

      OutputNominalNames[i] = new String[OutputUniqueValueCount[i]];

      Enumeration OutputKeys = OutputFeatureHashTable[i].keys();
      for (int v = 0; v < OutputUniqueValueCount[i]; v++) {
        String key = (String) OutputKeys.nextElement();
        int index = ((Integer) OutputFeatureHashTable[i].get(key)).intValue();
        OutputNominalNames[i][index] = key;
      }
    }

    this.pushOutput(InputNominalNames, 1);
    this.pushOutput(OutputNominalNames, 2);

    String[] InputNames = new String[NumInputs];
    String[] OutputNames = new String[NumOutputs];

    if (NominalToBooleanExpansion) {
      int InputIndex = 0;
      for (int i = 0; i < NumInputColumns; i++) {
        if (NominalColumn[InputColumnIndices[i]]) {
          for (int NominalIndex = 0; NominalIndex < ColumnNumNominalValues[InputColumnIndices[i]]; NominalIndex++) {
            InputNames[InputIndex] = ColumnNames[InputColumnIndices[i]] + "=" + UniqueColumnStrings[InputColumnIndices[i]][NominalIndex];
            InputIndex++;
          }
        }
        else {
          InputNames[InputIndex] = ColumnNames[InputColumnIndices[i]];
          InputIndex++;
        }
      }
      int OutputIndex = 0;
      for (int i = 0; i < NumOutputColumns; i++) {
        if (NominalColumn[OutputColumnIndices[i]]) {
          for (int NominalIndex = 0; NominalIndex < ColumnNumNominalValues[OutputColumnIndices[i]]; NominalIndex++) {
            OutputNames[OutputIndex] = ColumnNames[OutputColumnIndices[i]] + "=" + UniqueColumnStrings[OutputColumnIndices[i]][NominalIndex];
            OutputIndex++;
          }
        }
        else {
          OutputNames[OutputIndex] = ColumnNames[OutputColumnIndices[i]];
          OutputIndex++;
        }
      }

    }
    else {
      for (int i = 0; i < NumInputs; i++) {
        InputNames[i] = ColumnNames[InputColumnIndices[i]];
      }
      for (int i = 0; i < NumOutputs; i++) {
        OutputNames[i] = ColumnNames[OutputColumnIndices[i]];
      }
    }

    ContinuousDoubleExampleTable exampleSet = new ContinuousDoubleExampleTable(Data, NumExamples, NumInputs, NumOutputs, InputNames, OutputNames);

    this.pushOutput((ExampleTable) exampleSet, 0);

  }

}
