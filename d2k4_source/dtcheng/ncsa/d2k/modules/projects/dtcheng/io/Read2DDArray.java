package ncsa.d2k.modules.projects.dtcheng.io;
import ncsa.d2k.modules.core.io.file.*;

//import ncsa.d2k.modules.projects.dtcheng.io.*;


import ncsa.d2k.core.modules.*;
//import ncsa.util.table.*;
import java.io.*;
import java.util.*;
import java.text.*;


/*
issues:
  problem with mac vs. pc vs. unix text formats
*/
public class Read2DDArray extends InputModule
{

  //////////////////
  //  PROPERTIES  //
  //////////////////

  private String  DataPath       = "C:\\cygwin\\home\\Administrator\\data\\table.csv";
  public  void    setDataPath    (String  value) {       this.DataPath    = value;}
  public  String  getDataPath    ()              {return this.DataPath           ;}

  private byte    EOLByte1       = 10;
  public  void    setEOLByte1    (byte    value) {       this.EOLByte1    = value;}
  public  byte    getEOLByte1    ()              {return this.EOLByte1           ;}

  private String DelimiterCharacter     = ",";
  public  void   setDelimiterCharacter (String value) {
    this.DelimiterCharacter = value;

    if (value.equalsIgnoreCase("tab") || value.equalsIgnoreCase("\t")) {
      setDelimiterByte((byte) 9);
      return;
    }
    if (value.equalsIgnoreCase("comma") || value.equalsIgnoreCase(",")) {
      setDelimiterByte((byte) ',');
      return;
    }
    if (value.equalsIgnoreCase("space") || value.equalsIgnoreCase(" "))
    {
      setDelimiterByte((byte) ' ');
      return;
    }

    setDelimiterByte((byte) (this.DelimiterCharacter.charAt(0)));
  }

  public  String getDelimiterCharacter ()              {return this.DelimiterCharacter;}

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

  private int     NumColsToRead    = 40;
  public  void    setNumColsToRead (int     value) {       this.NumColsToRead = value;}
  public  int     getNumColsToRead ()              {return this.NumColsToRead        ;}

  private boolean FixedFormat    = false;
  public  void    setFixedFormat (boolean value) {       this.FixedFormat = value;}
  public  boolean getFixedFormat ()              {return this.FixedFormat        ;}

  private boolean ReadColumnNames    = true;
  public  void    setReadColumnNames (boolean value) {       this.ReadColumnNames = value;}
  public  boolean getReadColumnNames ()              {return this.ReadColumnNames        ;}

  private int     CharsPerColumn = 16;
  public  void    setCharsPerColumn (int     value) {       this.CharsPerColumn = value;}
  public  int     getCharsPerColumn ()              {return this.CharsPerColumn;}

  private int     ReadBufferSize = 1000000;
  public  void    setReadBufferSize (int     value) {       this.ReadBufferSize = value;}
  public  int     getReadBufferSize ()              {return this.ReadBufferSize;}


  private boolean Trace          = false;
  public  void    setTrace       (boolean value) {       this.Trace       = value;}
  public  boolean getTrace       ()              {return this.Trace;}

  private boolean LightTrace     = false;
  public  void    setLightTrace  (boolean value) {       this.LightTrace  = value;}
  public  boolean getLightTrace  ()              {return this.LightTrace;}


  ////////////////////
  //  INFO METHODS  //
  ////////////////////

  public String getInputInfo (int index)
  {
    switch (index) {
      default: return "No such input";
    }
  }

  public String getOutputInfo (int index)
  {
    switch (index) {
      case 0: return "[[D";
      case 1: return "[S";
      default: return "No such output";
    }
  }

  public String getModuleInfo()
  {
    return "ncsa.d2k.modules.projects.dtcheng.Read2DDArray";
  }


  ////////////////////////
  //  TYPE DEFINITIONS  //
  ////////////////////////

  public String[] getInputTypes()
  {
    String[] types = {		};
    return types;
  }

  public String[] getOutputTypes()
  {
    String[] types = {"[[D","[S"};
    return types;
  }



  /**********************************************************************************************************************************/
  /**********************************************************************************************************************************/
  /**********************************************************************************************************************************/
  /**********************************************************************************************************************************/
  /**********************************************************************************************************************************/
  /**********************************************************************************************************************************/

  ////////////
  //  MAIN  //
  ////////////



  public void doit() throws Exception
  {
    double [][] double2DArray = new double[NumRowsToRead][NumColsToRead];
    String [] columnNames = new String[NumColsToRead];

    double value = 0.0;

    int  starts[] = null;
    int  ends[]   = null;
    byte buffer[] = null;

    int example_index = 0;

    if (Trace)
      System.out.println(DataPath);

    FlatFile rio       = new FlatFile(DataPath, "r", ReadBufferSize, true);
    buffer = rio.Buffer;
    rio.DelimiterByte = DelimiterByte;
    rio.EOLByte1  = EOLByte1;

    for (int r = 0; r < NumRowsToSkip; r++)
    {
      if (LightTrace)
        System.out.println("Skipping row " + (r + 1));

      rio.readLine();
      if (r == 0 && ReadColumnNames)
      {
        String name;

        if (Trace)
          System.out.println("line = " + new String(buffer, rio.LineStart, (rio.LineEnd - rio.LineStart - 1)));
        for (int f = NumColsToSkip; f < NumColsToSkip + NumColsToRead; f++)
        {
          if (FixedFormat)
            name = new String(buffer, rio.LineStart + (f * CharsPerColumn), CharsPerColumn);
          else
            name = new String(buffer, rio.ColumnStarts[f], rio.ColumnEnds[f] - rio.ColumnStarts[f]);

          columnNames[f - NumColsToSkip] = name;

          if (Trace)
            System.out.println("f = " + (f - NumColsToSkip) + " name = " + name);
        }
      }
    }

    for (int e = 0; e < NumRowsToRead; e++)
    {
      if (LightTrace)
        System.out.println("e = " + e);

      rio.readLine();

      if (Trace)
        System.out.println("line = " + new String(buffer, rio.LineStart, (rio.LineEnd - rio.LineStart - 1)));

      for (int f = NumColsToSkip; f < NumColsToSkip + NumColsToRead; f++)
      {
        if (FixedFormat)
          value = rio.ByteStringToDouble(buffer, rio.LineStart + (f * CharsPerColumn), rio.LineStart + ((f + 1) * CharsPerColumn) - 1);
        else
          value = rio.ByteStringToDouble(buffer, rio.ColumnStarts[f], rio.ColumnEnds[f]);

        double2DArray[e][f - NumColsToSkip] = value;

        if (Trace)
          System.out.println("f = " + (f - NumColsToSkip) + " v = " + value);
      }

      if (Trace)
        System.out.println();

    }
    rio.close();

    this.pushOutput(double2DArray, 0);
    this.pushOutput(columnNames,   1);
  }

  /**
   * Return the human readable name of the module.
   * @return the human readable name of the module.
   */
  public String getModuleName() {
    return "Read2DDArray";
  }

  /**
   * Return the human readable name of the indexed input.
   * @param index the index of the input.
   * @return the human readable name of the indexed input.
   */
  public String getInputName(int index) {
    switch(index) {
      default: return "NO SUCH INPUT!";
    }
  }

  /**
   * Return the human readable name of the indexed output.
   * @param index the index of the output.
   * @return the human readable name of the indexed output.
   */
  public String getOutputName(int index) {
    switch(index) {
      case 0:
        return "output0";
      case 1:
        return "output1";
      default: return "NO SUCH OUTPUT!";
    }
  }
}

