package ncsa.d2k.modules.projects.dtcheng.matrix;

import ncsa.d2k.modules.projects.dtcheng.io.FlatFile;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import java.io.*;
import java.util.*;
import java.text.*;

public class ReadMatrix
    extends InputModule {

  //////////////////
  //  PROPERTIES  //
  //////////////////

  private String DataPath = "C:\\cygwin\\home\\Administrator\\data\\table.csv";
  public void setDataPath(String value) {
    this.DataPath = value;
  }

  public String getDataPath() {
    return this.DataPath;
  }

  private byte EOLByte1 = 10;
  public void setEOLByte1(byte value) {
    this.EOLByte1 = value;
  }

  public byte getEOLByte1() {
    return this.EOLByte1;
  }

  private String DelimiterCharacter = ",";
  public void setDelimiterCharacter(String value) {
    this.DelimiterCharacter = value;

    if (value.equalsIgnoreCase("tab") || value.equalsIgnoreCase("\t")) {
      setDelimiterByte( (byte) 9);
      return;
    }
    if (value.equalsIgnoreCase("comma") || value.equalsIgnoreCase(",")) {
      setDelimiterByte( (byte) ',');
      return;
    }
    if (value.equalsIgnoreCase("space") || value.equalsIgnoreCase(" ")) {
      setDelimiterByte( (byte) ' ');
      return;
    }

    setDelimiterByte( (byte) (this.DelimiterCharacter.charAt(0)));
  }

  public String getDelimiterCharacter() {
    return this.DelimiterCharacter;
  }

  private byte DelimiterByte = (byte) ',';
  public void setDelimiterByte(byte value) {
    this.DelimiterByte = value;
  }

  public byte getDelimiterByte() {
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

  private String FeatureTypeString = "IO";
  public void setFeatureTypeString(String value) {
    this.FeatureTypeString = value;
  }

  public String getFeatureTypeString() {
    return this.FeatureTypeString;
  }

  public String getModuleName() {
    return "ReadContinuousExampleTable";
  }

  public String getModuleInfo() {
    return "ReadContinuousExampleTable";
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "MultiFormatMatrix";
    }
    return "";
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "MultiFormatMatrix";
    }
    return "";
  }

  public String[] getOutputTypes() {
    String[] out = {
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
    };
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
        return "FormatIndex";
      default:
        return "No such input!";
    }
  }

  public String getInputInfo(int index) {
    switch (index) {
      case 0:
        return "FormatIndex";
      default:
        return "No such input!";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "java.lang.Integer",
    };
    return types;
  }

  ////////////
  //  MAIN  //
  ////////////

  public void doit() throws Exception {

    int FormatIndex = ( (Integer)this.pullInput(0)).intValue();

    int numExamples = NumRowsToRead;
    byte[] featureTypeBytes = null;

    featureTypeBytes = FeatureTypeString.getBytes();
    int numColumns = featureTypeBytes.length;

    boolean[] inputColumnSelected = new boolean[numColumns];
    boolean[] outputColumnSelected = new boolean[numColumns];
    int[] inputColumnIndices = new int[numColumns];
    int[] outputColumnIndices = new int[numColumns];

    int numInputs = 0;
    for (int i = 0; i < numColumns; i++) {
      switch (featureTypeBytes[i]) {
        case (byte) 'I':
        case (byte) 'i':
          inputColumnSelected[i] = true;
          inputColumnIndices[numInputs++] = i;
          break;
      }
    }

    int numFeatures = numInputs;

    double[] data = new double[numExamples * numFeatures];
    String[] columnNames = new String[numColumns];

    int starts[] = null;
    int ends[] = null;
    byte buffer[] = null;

    int example_index = 0;
    FlatFile rio = new FlatFile(DataPath, "r", ReadBufferSize, true);
    buffer = rio.Buffer;
    rio.DelimiterByte = DelimiterByte;
    rio.EOLByte1 = EOLByte1;

    for (int r = 0; r < NumRowsToSkip; r++) {
      rio.readLine();
    }

    int[] dimensions = {
        numExamples, numInputs};

    MultiFormatMatrix exampleSet = new MultiFormatMatrix(FormatIndex, dimensions);

    for (int e = 0; e < NumRowsToRead; e++) {

      rio.readLine();

      int InputIndex = 0;

      for (int f = 0; f < numColumns; f++) {

        if (inputColumnSelected[f]) {

          double value = 0.0;

          if (UseJavaDoubleParser) {
            String string = new String(buffer, rio.ColumnStarts[f], rio.ColumnEnds[f]);
            value = Double.parseDouble(string);
          }
          else {
            value = rio.ByteStringToDouble(buffer, rio.ColumnStarts[f], rio.ColumnEnds[f]);
          }
          exampleSet.setValue(e, InputIndex, value);

          InputIndex++;
        }
      }

    }
    rio.close();

    this.pushOutput( (MultiFormatMatrix) exampleSet, 0);

  }

}