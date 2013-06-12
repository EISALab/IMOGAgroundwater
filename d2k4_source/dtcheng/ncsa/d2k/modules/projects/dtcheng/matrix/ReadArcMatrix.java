package ncsa.d2k.modules.projects.dtcheng.matrix;


import ncsa.d2k.modules.projects.dtcheng.io.FlatFile;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import java.io.*;
import java.util.*;
import java.text.*;


public class ReadArcMatrix extends InputModule {

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


  private int ReadBufferSize = 1000000;
  public void setReadBufferSize(int value) {
    this.ReadBufferSize = value;
  }


  public int getReadBufferSize() {
    return this.ReadBufferSize;
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

    int FormatIndex = ((Integer)this.pullInput(0)).intValue();

    byte buffer[] = null;

    FlatFile rio = new FlatFile(DataPath, "r", ReadBufferSize, true);
    buffer = rio.Buffer;
    rio.EOLByte1 = EOLByte1;
    rio.DelimiterByte = 32; // space

    int ncols;
    int nrows;
    double xllcorner;
    double yllcorner;
    double cellsize;
    double NoDataValue;
    {
      String string;
      double value;
      int offset = 14;

      rio.readLine();
      string = new String(buffer, rio.LineStart + offset, rio.LineEnd - rio.LineStart - offset);
      ncols = (int) Double.parseDouble(string);
      System.out.println("ncols = " + ncols);

      rio.readLine();
      string = new String(buffer, rio.LineStart + offset, rio.LineEnd - rio.LineStart - offset);
      nrows = (int) Double.parseDouble(string);
      System.out.println("nrows = " + nrows);

      rio.readLine();
      string = new String(buffer, rio.LineStart + offset, rio.LineEnd - rio.LineStart - offset);
      xllcorner = Double.parseDouble(string);
      System.out.println("xllcorner = " + xllcorner);

      rio.readLine();
      string = new String(buffer, rio.LineStart + offset, rio.LineEnd - rio.LineStart - offset);
      yllcorner = Double.parseDouble(string);
      System.out.println("yllcorner = " + yllcorner);

      rio.readLine();
      string = new String(buffer, rio.LineStart + offset, rio.LineEnd - rio.LineStart - offset);
      cellsize = Double.parseDouble(string);
      System.out.println("cellsize = " + cellsize);

      rio.readLine();
      string = new String(buffer, rio.LineStart + offset, rio.LineEnd - rio.LineStart - offset);
      NoDataValue = Double.parseDouble(string);
      System.out.println("NoDataValue = " + NoDataValue);

    }

    int numExamples = nrows;

    int numColumns = ncols;

    int[] dimensions = {numExamples, numColumns};

    MultiFormatMatrix exampleSet = new MultiFormatMatrix(FormatIndex, dimensions);

    double value;

    for (int e = 0; e < numExamples; e++) {

      rio.readLine();

      for (int f = 0; f < numColumns; f++) {

        String string = new String(buffer, rio.ColumnStarts[f], rio.ColumnEnds[f] - rio.ColumnStarts[f]);
        value = Double.parseDouble(string);

        exampleSet.setValue(e, f, value);

      }

    }
    rio.close();

    this.pushOutput((MultiFormatMatrix) exampleSet, 0);

  }

}
