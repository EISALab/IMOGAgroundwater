package ncsa.d2k.modules.projects.dtcheng.io;

//import ncsa.d2k.modules.projects.dtcheng.io.*;
import ncsa.d2k.modules.core.io.file.*;

import ncsa.d2k.core.modules.OutputModule;
import java.io.*;
import java.util.*;
import java.text.*;
import ncsa.d2k.modules.projects.dtcheng.*;

/*
 issues:
  problem with mac vs. pc vs. unix text formats
 */

public class WriteString
    extends OutputModule {

  //////////////////
  //  PROPERTIES  //
  //////////////////

  private String DataPath = "data/WriteString.txt";
  public void setDataPath(String value) {
    this.DataPath = value;
  }

  public String getDataPath() {
    return this.DataPath;
  }

  private byte EOLByte = 10;
  public void setEOLByte(byte value) {
    this.EOLByte = value;
  }

  public byte getEOLByte() {
    return this.EOLByte;
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
        return "java.lang.String";
      default:
        return "No such input";
    }
  }

  public String getOutputInfo(int index) {
    switch (index) {
      case 0:
        return "java.lang.String";
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
        "java.lang.String"};
    return types;
  }

  public String[] getOutputTypes() {
    String[] types = {
        "java.lang.String"};
    return types;
  }

  public String getModuleName() {
    return "WriteString";
  }

  public String getInputName(int index) {
    switch (index) {
      case 0:
        return "inputString";
      default:
        return "NO SUCH INPUT!";
    }
  }

  public String getOutputName(int index) {
    switch (index) {
      case 0:
        return "output0";
      default:
        return "NO SUCH OUTPUT!";
    }
  }

  FlatFile FlatFile;
  public void beginExecution() {
    FlatFile = null;
  }

  ////////////
  //  MAIN  //
  ////////////

  public void doit() throws Exception {
    String inputString = (String)this.pullInput(0);

    byte buffer[] = null;

    if (Trace)
      System.out.println(DataPath);

    if (FlatFile == null) {
      FlatFile = new FlatFile(DataPath, "w", WriteBufferSize, true);
    }

    if (inputString == null) {
      if (FlatFile != null) {
        FlatFile.close();
      }
      FlatFile = null;
    }
    else {
      FlatFile.writeLine(inputString.getBytes(), 0, inputString.length());
      FlatFile.writeByte( (byte) EOLByte);
    }

    this.pushOutput(inputString, 0);
  }
}