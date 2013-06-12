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
public class ReadTimedByteStream extends InputModule
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

  private int        StartTime = 0;
  public  void    setStartTime (int value) {       this.StartTime = value;}
  public  int     getStartTime ()          {return this.StartTime;}

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
    return "ReadTimedByteStream";
  }
  public String getModuleInfo() {
    return "ReadTimedByteStream";
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
      } catch (Exception e) {
        System.out.println("wait error!!!");
        throw e;
      }
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

    long firstByteTime = -1;
    long startTime = System.currentTimeMillis() + StartTime;
    long totalNumBytesRead = 0;
    long time = -1;
    int  numBytes = -1;
    randomAccessFile.seek((long) ReadOffset);
    while (true) {

      if (ReadTimesAndSizes)
        time = randomAccessFile.readLong();
      else
        time = totalNumBytesRead / NumBytesToReadPerMillisecond;

      if (firstByteTime == -1) {
        firstByteTime = time;
      }

      long playTime = startTime + (time - firstByteTime);

      while ((long) System.currentTimeMillis() < (long) playTime) {
        wait(1);
      }


      if (ReadTimesAndSizes) {
      numBytes = randomAccessFile.readInt();
      }
      else {
        numBytes = ReadBufferSize;
      }

      byte [] bytes = (byte []) new byte[numBytes];
      int  numBytesRead = randomAccessFile.read(bytes);
      totalNumBytesRead += numBytesRead;

      if (numBytesRead == -1 || (totalNumBytesRead > NumBytesToRead)) {
        this.pushOutput(null, 0);
        this.pushOutput(null, 1);
        randomAccessFile.close();
        return;
      }

      if (count % ReportInterval == 0) {
        System.out.println("ReadTimedByteStream count = " + count);
      }

      this.pushOutput(new Long(time),  0);
      this.pushOutput(bytes, 1);

      count++;
    }
  }
}
