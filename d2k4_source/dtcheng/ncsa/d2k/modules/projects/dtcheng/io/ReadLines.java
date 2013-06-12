package ncsa.d2k.modules.projects.dtcheng.io;
import ncsa.d2k.modules.core.io.file.*;
import ncsa.d2k.modules.projects.dtcheng.*;
import ncsa.d2k.core.modules.*;
import java.io.*;
import java.util.*;
import java.text.*;


/*
issues:
  problem with mac vs. pc vs. unix text formats

*/

public class ReadLines extends InputModule {

  //////////////////
  //  PROPERTIES  //
  //////////////////

  private String  DataPath       = "dat/APT.TXT";
  public  void    setDataPath    (String  value) {       this.DataPath    = value;}
  public  String  getDataPath    ()              {return this.DataPath           ;}

  private byte    EOLByte1       = 10;
  public  void    setEOLByte1    (byte    value) {       this.EOLByte1    = value;}
  public  byte    getEOLByte1    ()              {return this.EOLByte1           ;}

  private byte    DelimiterByte       = (byte) ',';
  public  void    setDelimiterByte    (byte    value) {       this.DelimiterByte    = value;}
  public  byte    getDelimiterByte    ()              {return this.DelimiterByte           ;}

  private int     MaxNumLines = 1000;
  public  void    setMaxNumLines (int     value) {       this.MaxNumLines = value;}
  public  int     getMaxNumLines ()              {return this.MaxNumLines;}

  private int     ReportInterval = 1000;
  public  void    setReportInterval (int     value) {       this.ReportInterval = value;}
  public  int     getReportInterval ()              {return this.ReportInterval;}

  private int     NumRowsToSkip   = 0;
  public  void    setNumRowsToSkip (int     value) {       this.NumRowsToSkip = value;}
  public  int     getNumRowsToSkip ()              {return this.NumRowsToSkip        ;}

  private int     ReadBufferSize = 1000000;
  public  void    setReadBufferSize (int     value) {       this.ReadBufferSize = value;}
  public  int     getReadBufferSize ()              {return this.ReadBufferSize;}

  private boolean Trace          = false;
  public  void    setTrace       (boolean value) {       this.Trace       = value;}
  public  boolean getTrace       ()              {return this.Trace;}

  public String getModuleName() {
    return "ReadLines";
  }
  public String getModuleInfo() {
    return "This module reads lines from a text file and outputs them one at a time.  " +
        "When the specified number (MaxNumLines) or all of lines are read, it outputs a " +
        "null pointer to mark the end of available input. The DataPath property is the path " +
        "name to the file to be read. The DelimiterByte property is the byte value of the term " +
        "separator (e.g., tab, comma, space). The EOLByte property is the byte value marking the " +
        "end of the line. The MaxNumLines property is the maximum number of lines that will be " +
        "read from the file. The NumRowsToSkip property is the number of rows to skip initially " +
        "when reading the file. The ReadBufferSize property is size in bytes of the read buffer. " +
        "The Trace property is turns on an off the printing of status information for tracing execution.";
  }

  public String getInputName(int i) {
    switch(i) {
      default: return "NO SUCH INPUT!";
    }
  }
  public String[] getInputTypes() {
    String[] types = {};
    return types;
  }
  public String getInputInfo(int i) {
    switch (i) {
      default: return "No such input";
    }
  }


  public String getOutputName(int i) {
    switch(i) {
      case 0:
        return "LineByteArray";
      default: return "NO SUCH OUTPUT!";
    }
  }
  public String[] getOutputTypes() {
    String[] types = {"[B"};
    return types;
  }
  public String getOutputInfo(int i) {
    switch (i) {
      case 0: return "A byte array representing the next line read.  " +
        "The length of the array is variable and depends on the number of characters in the line.";
      default: return "No such output";
    }
  }

  private boolean InitialExecution = true;

  int Count;
  FlatFile Input;
  boolean EOF;

  public void beginExecution() {
    try {
      InitialExecution = true;
      EOF = false;
      Count = 0;

      Input = new FlatFile(DataPath, "r", ReadBufferSize, true);
      Input.DelimiterByte = DelimiterByte;
      Input.EOLByte1 = EOLByte1;
    }
    catch (Exception e) {
    }

  }

  public boolean isReady() {
    boolean value = false;

    if (InitialExecution) {
      value = true;
    }
    else {
      if (!EOF)
        value = true;
    }
    return value;
  }

  public void doit() throws Exception {

    System.out.println("Error!  Deprecated, use ReadLineByteStream.  ");

    int example_index = 0;

    if (InitialExecution) {
      if (Trace)
        System.out.println("reading " + DataPath);

      for (int r = 0; r < NumRowsToSkip; r++) {
        if (Trace)
          System.out.println("Skipping row " + (r + 1));

        Input.readLine();
      }

      InitialExecution = false;
    }

    Input.readLine();

    if (Input.EOF) {
      EOF = true;
      Input.close();
      this.pushOutput(null , 0);
      return;
    }

    if (Trace && (Count % ReportInterval == 0))
      System.out.println("line " + Count + " = " + new String(Input.Buffer, Input.LineStart, (Input.LineEnd - Input.LineStart - 1)));

    int length = (Input.LineEnd - Input.LineStart - 1);
    byte [] output = new byte[length];
    for (int i = 0; i < length; i++) {
      output[i] = Input.Buffer[Input.LineStart + i];
    }
    this.pushOutput(output , 0);

    Count++;

    if (Count == MaxNumLines) {
      EOF = true;
      Input.close();
      this.pushOutput(null , 0);
      return;
    }


  }
}