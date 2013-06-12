package ncsa.d2k.modules.projects.dtcheng.io;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import java.io.*;
import java.util.*;
import java.text.*;

import ncsa.d2k.modules.projects.dtcheng.*;
import ncsa.d2k.modules.projects.dtcheng.datatype.*;
import ncsa.d2k.modules.projects.dtcheng.primitive.*;


public class ReadContinuousDoubleExampleTable extends InputModule {

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


  private boolean FixedFormat = false;
  public void setFixedFormat(boolean value) {
    this.FixedFormat = value;
  }


  public boolean getFixedFormat() {
    return this.FixedFormat;
  }


  private boolean UseJavaDoubleParser = false;
  public void setUseJavaDoubleParser(boolean value) {
    this.UseJavaDoubleParser = value;
  }


  public boolean getUseJavaDoubleParser() {
    return this.UseJavaDoubleParser;
  }


  private boolean ConvertStringsToIndices = false;
  public void setConvertStringsToIndices(boolean value) {
    this.ConvertStringsToIndices = value;
  }


  public boolean getConvertStringsToIndices() {
    return this.ConvertStringsToIndices;
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


  private String NominalFeatureNumberList = "Y,N";
  public void setNominalFeatureNumberList(String value) {
    this.NominalFeatureNumberList = value;
  }


  public String getNominalFeatureNumberList() {
    return this.NominalFeatureNumberList;
  }


  public String getModuleName() {
    return "ReadContinuousDoubleExampleTable";
  }


  public String getModuleInfo() {
    return "ReadContinuousExampleTable comma = 44; space = 32; tab = 9; ";
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


  ////////////
  //  MAIN  //
  ////////////

  public void doit() throws Exception {

    //System.out.println("user.dir = " + System.getProperty("user.dir"));






    /////////////////////////////////
    // parse paramter string lists //
    /////////////////////////////////

    String[] FeatureTypeStrings = null;
    FeatureTypeStrings = Utility.parseCSVList(FeatureTypeList);

    int NumColumns = FeatureTypeStrings.length;

    String[] FeatureNumbersToMakeNominal = Utility.parseCSVList(NominalFeatureNumberList);

    boolean[] NominalColumn = new boolean[NumColumns];

    for (int i = 0; i < NumColumns; i++) {
      if (FeatureNumbersToMakeNominal[i].equalsIgnoreCase("Y")) {
        NominalColumn[i] = true;
      }
    }

    int NumExamples = NumRowsToRead;

    boolean[] InputColumnSelected = new boolean[NumColumns];
    boolean[] OutputColumnSelected = new boolean[NumColumns];
    int[] InputColumnIndices = new int[NumColumns];
    int[] OutputColumnIndices = new int[NumColumns];

    int NumInputs = 0;
    int NumOutputs = 0;
    for (int i = 0; i < NumColumns; i++) {
      switch (FeatureTypeStrings[i].getBytes()[0]) {
        case (byte) 'I':
        case (byte) 'i':
          InputColumnSelected[i] = true;
          InputColumnIndices[NumInputs++] = i;
          break;
        case (byte) 'O':
        case (byte) 'o':
          OutputColumnSelected[i] = true;
          OutputColumnIndices[NumOutputs++] = i;
          break;
      }
    }

    int NumFeatures = NumInputs + NumOutputs;

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

        if (FixedFormat)
          name = new String(buffer, rio.LineStart + (f * CharsPerColumn), CharsPerColumn);
        else
          name = new String(buffer, rio.ColumnStarts[f], rio.ColumnEnds[f] - rio.ColumnStarts[f]);

        ColumnNames[f] = name;

      }

      rio.close();
    }

    //////////////////////////////////////////////////////////
    //  READ WHOLE FILE AND MAP STRINGS TO NUMERIC INDICES  //
    //////////////////////////////////////////////////////////

    if (ConvertStringsToIndices) {

      FlatFile Rio = new FlatFile(DataPath, "r", ReadBufferSize, true);
      buffer = Rio.Buffer;
      Rio.DelimiterByte = (byte) DelimiterByte;
      Rio.EOLByte1 = (byte) EndOfLineByte1;

      // skip header if necessary
      for (int r = 0; r < NumRowsToSkip; r++) {
        Rio.readLine();
      }

      Hashtable[] InputFeatureHashTable = new Hashtable[NumInputs];
      Hashtable[] OutputFeatureHashTable = new Hashtable[NumOutputs];

      for (int i = 0; i < NumInputs; i++)
        InputFeatureHashTable[i] = new Hashtable();
      for (int i = 0; i < NumOutputs; i++)
        OutputFeatureHashTable[i] = new Hashtable();

      int[] InputUniqueValueCount = new int[NumInputs];
      int[] OutputUniqueValueCount = new int[NumOutputs];

      double[] columnValues = new double[NumColumns];

      for (int e = 0; e < NumRowsToRead; e++) {

        Rio.readLine();

        int InputFeatureIndex = 0;
        int OutputFeatureIndex = 0;
        for (int f = 0; f < NumColumns; f++) {

          if (NominalColumn[f]) {

            String Name;

            if (FixedFormat)
              Name = new String(buffer, Rio.LineStart + (f * CharsPerColumn), CharsPerColumn);
            else
              Name = new String(buffer, Rio.ColumnStarts[f], Rio.ColumnEnds[f] - Rio.ColumnStarts[f]);

            double value = 0.0;

            if (InputColumnSelected[f]) {

              if (InputFeatureHashTable[InputFeatureIndex].containsKey(Name)) {
                value = ((Integer) InputFeatureHashTable[InputFeatureIndex].get(Name)).intValue();
              }
              else {
                value = InputUniqueValueCount[InputFeatureIndex];
                InputFeatureHashTable[InputFeatureIndex].put(Name, new Integer(InputUniqueValueCount[InputFeatureIndex]));
                InputUniqueValueCount[InputFeatureIndex]++;
              }

              InputFeatureIndex++;

            }

            if (OutputColumnSelected[f]) {

              if (OutputFeatureHashTable[OutputFeatureIndex].containsKey(Name)) {
                value = ((Integer) OutputFeatureHashTable[OutputFeatureIndex].get(Name)).intValue();
              }
              else {
                value = OutputUniqueValueCount[OutputFeatureIndex];
                OutputFeatureHashTable[OutputFeatureIndex].put(Name, new Integer(OutputUniqueValueCount[OutputFeatureIndex]));
                OutputUniqueValueCount[OutputFeatureIndex]++;
              }

              OutputFeatureIndex++;

            }

            columnValues[f] = value;

          }
          else {
            if (FixedFormat) {
              if (UseJavaDoubleParser) {
                String string = new String(buffer, Rio.LineStart + (f * CharsPerColumn), Rio.LineStart + ((f + 1) * CharsPerColumn) - 1);
                columnValues[f] = Double.parseDouble(string);
              }
              else {
                columnValues[f] = Rio.ByteStringToDouble(buffer, Rio.LineStart + (f * CharsPerColumn), Rio.LineStart + ((f + 1) * CharsPerColumn) - 1);
              }
            }
            else {
              if (UseJavaDoubleParser) {
                String string = new String(buffer, Rio.ColumnStarts[f], Rio.ColumnEnds[f]);
                columnValues[f] = Double.parseDouble(string);
              }
              else {
                columnValues[f] = Rio.ByteStringToDouble(buffer, Rio.ColumnStarts[f], Rio.ColumnEnds[f]);
              }
            }

          }
        }

        for (int i = 0; i < NumInputs; i++) {
          Data[e * NumFeatures + i] = (double) columnValues[InputColumnIndices[i]];
        }
        for (int i = 0; i < NumOutputs; i++) {
          Data[e * NumFeatures + NumInputs + i] = (double) columnValues[OutputColumnIndices[i]];
        }

      }
      Rio.close();

      String[][] InputNominalNames = new String[NumInputs][];

      for (int i = 0; i < NumInputs; i++) {

        InputNominalNames[i] = new String[InputUniqueValueCount[i]];

        Enumeration InputKeys = InputFeatureHashTable[i].keys();
        for (int v = 0; v < InputUniqueValueCount[i]; v++) {
          String key = (String) InputKeys.nextElement();
          int index = ((Integer) InputFeatureHashTable[i].get(key)).intValue();
          InputNominalNames[i][index] = key;
        }
      }

      String[][] OutputNominalNames = new String[NumOutputs][];

      for (int i = 0; i < NumOutputs; i++) {

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

    }

    else {

      FlatFile Rio = new FlatFile(DataPath, "r", ReadBufferSize, true);
      buffer = Rio.Buffer;
      Rio.DelimiterByte = (byte) DelimiterByte;
      Rio.EOLByte1 = (byte) EndOfLineByte1;

      for (int r = 0; r < NumRowsToSkip; r++) {
        Rio.readLine();
      }

      double[] columnValues = new double[NumColumns];
      for (int e = 0; e < NumRowsToRead; e++) {

        Rio.readLine();

        for (int f = 0; f < NumColumns; f++) {

          if (FixedFormat) {
            if (UseJavaDoubleParser) {
              String string = new String(buffer, Rio.LineStart + (f * CharsPerColumn), Rio.LineStart + ((f + 1) * CharsPerColumn) - 1);
              columnValues[f] = Double.parseDouble(string);
            }
            else {
              columnValues[f] = Rio.ByteStringToDouble(buffer,
                  Rio.LineStart +
                  (f * CharsPerColumn),
                  Rio.LineStart +
                  ((f + 1) * CharsPerColumn) - 1);
            }
          }
          else {
            if (UseJavaDoubleParser) {
              String string = new String(buffer,
                                         Rio.ColumnStarts[f],
                                         Rio.ColumnEnds[f]);
              columnValues[f] = Double.parseDouble(string);

            }
            else {
              columnValues[f] = Rio.ByteStringToDouble(buffer,
                  Rio.ColumnStarts[f],
                  Rio.ColumnEnds[f]);
            }
          }

        }

        for (int i = 0; i < NumInputs; i++)
          Data[e * NumFeatures + i] = (double) columnValues[InputColumnIndices[i]];
        for (int i = 0; i < NumOutputs; i++)
          Data[e * NumFeatures + NumInputs + i] = (double) columnValues[OutputColumnIndices[i]];

      }
      Rio.close();
    }

    String[] inputNames = new String[NumInputs];
    String[] outputNames = new String[NumOutputs];

    for (int i = 0; i < NumInputs; i++) {
      inputNames[i] = ColumnNames[InputColumnIndices[i]];
    }
    for (int i = 0; i < NumOutputs; i++) {
      outputNames[i] = ColumnNames[OutputColumnIndices[i]];
    }

    ContinuousDoubleExampleTable exampleSet = new ContinuousDoubleExampleTable(Data, NumExamples, NumInputs, NumOutputs, inputNames, outputNames);

    this.pushOutput((ExampleTable) exampleSet, 0);

  }

}
