package ncsa.d2k.modules.projects.dtcheng.io;
import ncsa.d2k.modules.core.io.file.*;


import ncsa.d2k.core.modules.*;
//import ncsa.util.table.*;
import java.io.*;
import java.util.*;
import java.text.*;


/*
issues:
  problem with mac vs. pc vs. unix text formats

*/

public class ReadByColumn1DSArray extends InputModule {

  //////////////////
  //  PROPERTIES  //
  //////////////////

  private String  DataPath       = "data/table1.dat";
  public  void    setDataPath    (String  value) {       this.DataPath    = value;}
  public  String  getDataPath    ()              {return this.DataPath           ;}

  private byte    EOLByte1       = 10;
  public  void    setEOLByte1    (byte    value) {       this.EOLByte1    = value;}
  public  byte    getEOLByte1    ()              {return this.EOLByte1           ;}

  private byte    DelimiterByte       = (byte) ',';
  public  void    setDelimiterByte    (byte    value) {       this.DelimiterByte    = value;}
  public  byte    getDelimiterByte    ()              {return this.DelimiterByte           ;}

  private int     NumRowsToSkip   = 1;
  public  void    setNumRowsToSkip (int     value) {       this.NumRowsToSkip = value;}
  public  int     getNumRowsToSkip ()              {return this.NumRowsToSkip        ;}

  private int     NumRowsToRead    = 60;
  public  void    setNumRowsToRead (int     value) {       this.NumRowsToRead = value;}
  public  int     getNumRowsToRead ()              {return this.NumRowsToRead        ;}

  private int     NumColsToSkip    = 0;
  public  void    setNumColsToSkip (int     value) {       this.NumColsToSkip = value;}
  public  int     getNumColsToSkip ()              {return this.NumColsToSkip        ;}

  private int     ReadBufferSize = 1000000;
  public  void    setReadBufferSize (int     value) {       this.ReadBufferSize = value;}
  public  int     getReadBufferSize ()              {return this.ReadBufferSize;}


  private boolean Trace          = false;
  public  void    setTrace       (boolean value) {       this.Trace       = value;}
  public  boolean getTrace       ()              {return this.Trace;}

  public String getModuleName() {
    return "ReadByColumn1DSArray";
  }

  public String getModuleInfo()
  {
    return "This module reads a single column of strings from a file that represents tablular ascii data.  " +
    "The output is an array of strings read from the specified column in the file.  " +
    "The DataPath property is the path name to the file to be read.  " +
    "The DelimiterByte property is the byte value of the term separator (e.g., tab, comma, space).  " +
    "The EOLByte property is the byte value marking the end of the line.  " +
    "The NumColsToSkip property is the number of columns to skip and is used to locate the column to be read.  " +
    "The NumRowsToSkip property is the number of rows to skip initially when reading the file.  "  +
    "The NumRowsToRead property is the maximum number of rows to read and may be changed by the module if " +
    "NumRowsToRead exceeds the actual number of rows in the file.  " +
    "The ReadBufferSize property is size in bytes of the read buffer.  " +
    "The Trace property is turns on an off the printing of status information for tracing execution.  ";
  }

  public String getInputName(int i) {
    switch(i) {
      default: return "Error, no such input.";
    }
  }
  public String getInputInfo(int i) {
    switch (i) {
      default: return "Error, no such input.";
    }
  }
  public String[] getInputTypes() {
    String[] types = {};
    return types;
  }

  public String getOutputName(int i) {
    switch(i) {
      case 0:  return "ColumnStringArray";
      default: return "NO SUCH OUTPUT!";
    }
  }
  public String getOutputInfo (int i) {
    switch (i) {
      case 0: return "An array of strings representing the column read from the file.  ";
      default: return "No such output";
    }
  }
  public String[] getOutputTypes() {
    String[] types = {"[S"};
    return types;
  }


  public void doit() throws Exception {

    String [] stringArray = new String[NumRowsToRead];

    int  starts[] = null;
    int  ends[]   = null;
    byte buffer[] = null;

    int example_index = 0;

    if (Trace)
      System.out.println(DataPath);

    FlatFile rio           = new FlatFile(DataPath, "r", ReadBufferSize, true);
    buffer            = rio.Buffer;
    rio.DelimiterByte = DelimiterByte;
    rio.EOLByte1       = EOLByte1;

    for (int r = 0; r < NumRowsToSkip; r++) {
      if (Trace)
        System.out.println("Skipping row " + (r + 1));
      rio.readLine();
    }

    int index = 0;
    for (int r = 0; r < NumRowsToRead; r++) {

      rio.readLine();

      if (Trace)
        System.out.println("line = " + new String(buffer, rio.LineStart, (rio.LineEnd - rio.LineStart - 1)));

      if (Trace)
        System.out.print("stringArray =");

      stringArray[index++] = new String(buffer, rio.ColumnStarts[NumColsToSkip],
                                        rio.ColumnEnds[NumColsToSkip] - rio.ColumnStarts[NumColsToSkip]);

      if (rio.EOF) {
        System.out.println("Warning!  EOF encountered, changing NumRowsToRead property");
        NumRowsToRead = r + 1;
        String [] newStringArray = new String[NumRowsToRead];
        for (int i = 0; i < NumRowsToRead; i++) {
          newStringArray[i] = stringArray[i];
        }
        stringArray = newStringArray;
        break;
      }

    }

    if (Trace)
      System.out.println();

    rio.close();

    this.pushOutput(stringArray , 0);
  }
}

