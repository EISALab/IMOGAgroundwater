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
import ncsa.d2k.modules.projects.dtcheng.primitive.*;
public class ReadPrimitiveStream extends InputModule
{
  //////////////////
  //  PROPERTIES  //
  //////////////////

  private int        ReadOffset = 0;
  public  void    setReadOffset (int value) {       this.ReadOffset = value;}
  public  int     getReadOffset ()          {return this.ReadOffset;}

  private String        FilePathName = "c:/TimedByteStream.dat";
  public  void    setFilePathName (String value) {       this.FilePathName = value;}
  public  String     getFilePathName ()          {return this.FilePathName;}

  private int        ReportInterval = 100;
  public  void    setReportInterval (int value) {       this.ReportInterval = value;}
  public  int     getReportInterval ()          {return this.ReportInterval;}

  private int        NumTimes = 0;
  public  void    setNumTimes (int value) {       this.NumTimes = value;}
  public  int     getNumTimes ()          {return this.NumTimes;}

  private int        NumBytesToRead = 999999999;
  public  void    setNumBytesToRead (int value) {       this.NumBytesToRead = value;}
  public  int     getNumBytesToRead ()          {return this.NumBytesToRead;}

  private int        ReadBufferSize = -1;
  public  void    setReadBufferSize (int value) {       this.ReadBufferSize = value;}
  public  int     getReadBufferSize ()          {return this.ReadBufferSize;}

  private int        NumBytesToReadPerMillisecond = -1;
  public  void    setNumBytesToReadPerMillisecond (int value) {       this.NumBytesToReadPerMillisecond = value;}
  public  int     getNumBytesToReadPerMillisecond ()          {return this.NumBytesToReadPerMillisecond;}

  public boolean ReadTimesAndSizes     = true;
  public void    setReadTimesAndSizes (boolean value) {       this.ReadTimesAndSizes       = value;}
  public boolean getReadTimesAndSizes ()              {return this.ReadTimesAndSizes;}



  public String getModuleName() {
    return "ReadPrimitiveStream";
  }
  public String getModuleInfo() {
    return "ReadPrimitiveStream";
  }


  public String getInputName(int i) {
    switch (i) {
      default: return "NotAvailable";
    }
  }
  public String[] getInputTypes() {
    String[] types = {};
    return types;
  }
  public String getInputInfo(int i) {
    switch (i) {
      default: return "NotAvailable";
    }
  }



  public String getOutputName(int i) {
    switch (i) {
      case 0: return "Time";
      case 1: return "Bytes";
      default: return "No such output";
    }
  }
  public String[] getOutputTypes() {
    String[] types = {"java.lang.Long", "[B"};
    return types;
  }
  public String getOutputInfo(int i) {
    switch (i) {
      case 0: return "Time";
      case 1: return "Bytes";
      default: return "No such output";
    }
  }

  RandomAccessFile randomAccessFile = null;
  int count = 0;
  public void beginExecution() {
    count = 0;
    randomAccessFile = null;
  }

  void wait (int time)  throws Exception {
    try {
      synchronized (Thread.currentThread()) {
        Thread.currentThread().sleep(time);
      }
      } catch (Exception e) {System.out.println("wait error!!!"); throw e;}
  }



  static byte [] readBuffer = null;
  public void doit() throws Exception
  {
    if (randomAccessFile == null) {

      try {
        randomAccessFile = new RandomAccessFile(FilePathName, "rw");
      }
      catch (Exception e) {
        System.out.println("couldn't open file: " + FilePathName);
        throw e;
      }
    }

    randomAccessFile.seek((long) ReadOffset);


    long    totalNumBytesRead = 0;
    int   value1 = 0;
    int   value2 = 0;
    int   value3 = 0;
    boolean EOF;
    if (false) {
      for (int i = 0; i < 2000; i++) {

        try {
          value1 = randomAccessFile.readUnsignedByte();
        }
        catch (EOFException e) {
          break;
        }
        System.out.println("i = " + i + "  v = " + value1);
      }
    }
    while (count < NumTimes) {

      try {
        value1 = randomAccessFile.readUnsignedByte();
        value2 = randomAccessFile.readUnsignedByte();
        value3 = randomAccessFile.readUnsignedByte();
      }
      catch (EOFException e) {
        break;
      }

      //System.out.println("value1 = " + (int) value1);
      //System.out.println("value2 = " + (int) value2);
      //System.out.println("value3 = " + (int) value3);

      //int signedValue = (value1 + (value2 << 8) + (value3 << 16));

      int signedValue = ((int) (((((value3 << 8) + value2) << 8) + value1) << 8)) / 8;
      System.out.println(signedValue);
      //this.pushOutput(new Float(time),  0);

      count++;
    }
  }
}
