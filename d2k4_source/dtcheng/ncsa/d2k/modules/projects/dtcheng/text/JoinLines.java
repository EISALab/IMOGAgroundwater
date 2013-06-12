package ncsa.d2k.modules.projects.dtcheng.text;

//import ncsa.d2k.modules.projects.dtcheng.io.*;
import ncsa.d2k.modules.core.io.file.*;

import ncsa.d2k.core.modules.*;
//import ncsa.util.table.*;
import java.io.*;
import java.util.*;
import java.text.*;
import ncsa.d2k.modules.projects.dtcheng.*;
import ncsa.d2k.modules.projects.dtcheng.io.*;

/*
 issues:
  problem with mac vs. pc vs. unix text formats

 */

public class JoinLines
    extends ncsa.d2k.core.modules.ComputeModule {

  //////////////////
  //  PROPERTIES  //
  //////////////////

  private String InputDataPath = "C:\\cygwin\\home\\dtcheng\\data\\NCSA.TXT";
  public void setInputDataPath(String value) {
    this.InputDataPath = value;
  }

  public String getInputDataPath() {
    return this.InputDataPath;
  }

  private String OutputDataPath = "C:\\cygwin\\home\\dtcheng\\data\\NCSAJoined.TXT";
  public void setOutputDataPath(String value) {
    this.OutputDataPath = value;
  }

  public String getOutputDataPath() {
    return this.OutputDataPath;
  }

  private byte EOLByte = 10;
  public void setEOLByte(byte value) {
    this.EOLByte = value;
  }

  public byte getEOLByte() {
    return this.EOLByte;
  }

  private int NumToJoin = 1000;
  public void setNumToJoin(int value) {
    this.NumToJoin = value;
  }

  public int getNumToJoin() {
    return this.NumToJoin;
  }

  private int ReportInterval = 1000;
  public void setReportInterval(int value) {
    this.ReportInterval = value;
  }

  public int getReportInterval() {
    return this.ReportInterval;
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
      default:
        return "No such input";
    }
  }

  public String getOutputInfo(int index) {
    switch (index) {
      case 0:
        return "[B";
      default:
        return "No such output";
    }
  }

  public String getModuleInfo() {
    return "ncsa.d2k.modules.projects.dtcheng.JoinLines";
  }

  ////////////////////////
  //  TYPE DEFINITIONS  //
  ////////////////////////

  public String[] getInputTypes() {
    String[] types = {};
    return types;
  }

  public String[] getOutputTypes() {
    String[] types = {
        "[B"};
    return types;
  }

  /**********************************************************************************************************************************/
  /**********************************************************************************************************************************/
  /**********************************************************************************************************************************/
  /**********************************************************************************************************************************/
  /**********************************************************************************************************************************/
  /**********************************************************************************************************************************/

  private boolean InitialExecution = true;

  int Count;
  FlatFile Input;
  FlatFile Output;
  boolean EOF;

  public void beginExecution() {
    try {
      InitialExecution = true;
      EOF = false;
      Count = 0;

      Input = new FlatFile(InputDataPath, "r", 1000000, true);
      Output = new FlatFile(OutputDataPath, "w", 1000000, true);
    }
    catch (Exception e) {
    }

  }

  ////////////
  //  MAIN  //
  ////////////

  public void doit() throws Exception {

    int byteCount = 0;
    int EOLCount = 0;
    while (true) {
      byte byteValue = Input.readByte();

      if (Input.EOF) {
        Input.close();
        Output.close();
        break;
      }

      if (byteValue == EOLByte) {
        EOLCount++;

        if (EOLCount == NumToJoin) {
          Output.writeByte(byteValue);
          EOLCount = 0;
        }
      }
      else {
        Output.writeByte(byteValue);
      }

      byteCount++;
      if (byteCount % ReportInterval == 0)
        System.out.println("byteCount = " + byteCount);
    }

    System.out.println("byteCount = " + byteCount);

  }

  /**
   * Return the human readable name of the module.
   * @return the human readable name of the module.
   */
  public String getModuleName() {
    return "JoinLines";
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
}
