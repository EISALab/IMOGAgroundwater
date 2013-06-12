package ncsa.d2k.modules.projects.dtcheng.io;

//import ncsa.d2k.modules.projects.dtcheng.io.*;
import ncsa.d2k.modules.core.io.file.*;

import ncsa.d2k.core.modules.OutputModule;
//import ncsa.util.table.*;
import java.io.*;
import java.util.*;
import java.text.*;
import ncsa.d2k.modules.projects.dtcheng.*;

/*
 issues:
  problem with mac vs. pc vs. unix text formats
 */

public class Write1DSArray
    extends OutputModule {

  //////////////////
  //  PROPERTIES  //
  //////////////////

  private String DataPath = "C:\\temp\\sc01\\BestAndWorst.txt";
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

  private byte DelimiterByte = (byte) ',';
  public void setDelimiterByte(byte value) {
    this.DelimiterByte = value;
  }

  public byte getDelimiterByte() {
    return this.DelimiterByte;
  }

  private int WriteBufferSize = 1000000;
  public void setWriteBufferSize(int value) {
    this.WriteBufferSize = value;
  }

  public int getWriteBufferSize() {
    return this.WriteBufferSize;
  }

  private boolean Trace = false;
  public void setTrace(boolean value) {
    this.Trace = value;
  }

  public boolean getTrace() {
    return this.Trace;
  }

  ////////////////////
  //  INFO METHODS  //
  ////////////////////

  public String getInputInfo(int index) {
    switch (index) {
      case 0:
        return "[S";
      default:
        return "No such input";
    }
  }

  public String getOutputInfo(int index) {
    switch (index) {
      case 0:
        return "[S";
      default:
        return "No such output";
    }
  }

  public String getModuleInfo() {
    return "ncsa.d2k.modules.projects.dtcheng.Write1DSArray";
  }

  ////////////////////////
  //  TYPE DEFINITIONS  //
  ////////////////////////

  public String[] getInputTypes() {
    String[] types = {
        "[S"};
    return types;
  }

  public String[] getOutputTypes() {
    String[] types = {
        "[S"};
    return types;
  }

  /**
   * Return the human readable name of the module.
   * @return the human readable name of the module.
   */
  public String getModuleName() {
    return "Write1DSArray";
  }

  /**
   * Return the human readable name of the indexed input.
   * @param index the index of the input.
   * @return the human readable name of the indexed input.
   */
  public String getInputName(int index) {
    switch (index) {
      case 0:
        return "input0";
      default:
        return "NO SUCH INPUT!";
    }
  }

  /**
   * Return the human readable name of the indexed output.
   * @param index the index of the output.
   * @return the human readable name of the indexed output.
   */
  public String getOutputName(int index) {
    switch (index) {
      case 0:
        return "output0";
      default:
        return "NO SUCH OUTPUT!";
    }
  }

  public void doit() throws Exception {

    String[] stringArray = (String[])this.pullInput(0);

    byte buffer[] = null;

    if (Trace)
      System.out.println(DataPath);

    FlatFile rio = new FlatFile(DataPath, "w", WriteBufferSize, true);
    rio.DelimiterByte = DelimiterByte;
    rio.EOLByte1 = EOLByte1;

    for (int i = 0; i < stringArray.length; i++) {
      rio.writeLine(stringArray[i].getBytes(), 0, stringArray[i].length() - 1);
      rio.writeByte( (byte) 10);
    }

    rio.close();

    this.pushOutput(stringArray, 0);
  }

}