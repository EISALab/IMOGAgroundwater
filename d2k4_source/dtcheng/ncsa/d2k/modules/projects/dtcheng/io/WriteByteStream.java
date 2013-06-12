package ncsa.d2k.modules.projects.dtcheng.io;


import ncsa.d2k.core.modules.*;
//import ncsa.util.table.*;
import java.io.*;
import java.util.*;
import java.text.*;
import java.awt.*;
import javax.sound.sampled.*;
import javax.sound.sampled.Line.*;
import javax.sound.sampled.Line.Info;
import ncsa.d2k.modules.projects.dtcheng.*;
public class WriteByteStream extends OutputModule
{
  //////////////////
  //  PROPERTIES  //
  //////////////////

  private String        FilePathName = "ByteStream.dat";
  public  void    setFilePathName (String value) {       this.FilePathName = value;}
  public  String     getFilePathName ()          {return this.FilePathName;}

  private boolean        ModifyFile = false;
  public  void    setModifyFile (boolean value) {       this.ModifyFile = value;}
  public  boolean     getModifyFile ()          {return this.ModifyFile;}

  private int        ReportInterval = 100;
  public  void    setReportInterval (int value) {       this.ReportInterval = value;}
  public  int     getReportInterval ()          {return this.ReportInterval;}

  private int        WriteOffset = 0;
  public  void    setWriteOffset (int value) {       this.WriteOffset = value;}
  public  int     getWriteOffset ()          {return this.WriteOffset;}


  public String getModuleName() {
    return "WriteByteStream";
  }
  public String getModuleInfo() {
    return "WriteByteStream";
  }


  public String getInputName(int i) {
    switch (i) {
      case 0: return "Bytes";
      default: return "NotAvailable";
    }
  }
  public String[] getInputTypes() {
    String[] types = {"[B"};
    return types;
  }
  public String getInputInfo(int i) {
    switch (i) {
      case 0: return "Bytes";
      default: return "NotAvailable";
    }
  }



  public String getOutputName(int i) {
    switch (i) {
      case 0: return "Bytes";
      default: return "No such output";
    }
  }
  public String[] getOutputTypes() {
    String[] types = {"[B"};
    return types;
  }
  public String getOutputInfo(int i) {
    switch (i) {
      case 0: return "Bytes";
      default: return "No such output";
    }
  }

  RandomAccessFile randomAccessFile = null;
  int count = 0;
  public void beginExecution() {
    count = 0;
    randomAccessFile = null;
  }


  static byte [] readBuffer = null;
  public void doit() throws Exception
  {
    if (randomAccessFile == null) {

      try {
        //erase file
        File file = null;
        file = new File(FilePathName);

        if (!ModifyFile) {
          file.delete();
        }

        randomAccessFile = new RandomAccessFile(FilePathName, "rw");



        if (file == null) {
          System.out.println("couldn't open file: " + FilePathName);
          throw new Exception();

        }
      }
      catch (Exception e) {
        System.out.println("couldn't open file: " + FilePathName);
        throw e;
      }



      if (WriteOffset > 0) {
        try {
          randomAccessFile.seek(WriteOffset);
        }
        catch (Exception e) {
          System.out.println("couldn't seek file: " + FilePathName);
        }
      }

    }

    byte [] bytes = (byte []) this.pullInput(0);

    if (bytes == null) {
      this.pushOutput(null, 0);
      randomAccessFile.close();
      randomAccessFile = null;
      return;
    }

    randomAccessFile.write(bytes);

    if (count % ReportInterval == 0) {
      System.out.println("WriteByteStream count = " + count);
    }


    this.pushOutput(bytes, 0);

    count++;
  }
}
