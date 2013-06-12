package ncsa.d2k.modules.projects.dtcheng.io;

import ncsa.d2k.core.modules.*;
import java.io.*;
import java.util.*;
import java.text.*;
import ncsa.d2k.modules.projects.dtcheng.primitive.*;

public class TailFile extends InputModule
{

  //////////////////
  //  PROPERTIES  //
  //////////////////

  private String  FilePathName       = "C:/test.wav";
  public  void    setFilePathName    (String  value) {       this.FilePathName    = value;}
  public  String  getFilePathName    ()              {return this.FilePathName           ;}

  private boolean        JumpToEnd = true;
  public  void    setJumpToEnd (boolean value) {       this.JumpToEnd = value;}
  public  boolean     getJumpToEnd ()          {return this.JumpToEnd;}

  private boolean        Terminate = false;
  public  void    setTerminate (boolean value) {       this.Terminate = value;}
  public  boolean     getTerminate ()          {return this.Terminate;}

  private int        ReadOffset = 0;
  public  void    setReadOffset (int value) {       this.ReadOffset = value;}
  public  int     getReadOffset ()          {return this.ReadOffset;}

  private int        InitialWaitTimeInMilliseconds = 0;
  public  void    setInitialWaitTimeInMilliseconds (int value) {       this.InitialWaitTimeInMilliseconds = value;}
  public  int     getInitialWaitTimeInMilliseconds ()          {return this.InitialWaitTimeInMilliseconds;}

  private int        WaitTimeInMilliseconds = 1;
  public  void    setWaitTimeInMilliseconds (int value) {       this.WaitTimeInMilliseconds = value;}
  public  int     getWaitTimeInMilliseconds ()          {return this.WaitTimeInMilliseconds;}

  private int        MinBufferSize = 100000;
  public  void    setMinBufferSize (int value) {       this.MinBufferSize = value;}
  public  int     getMinBufferSize ()          {return this.MinBufferSize;}

  private int        MaxBufferSize = 10000000;
  public  void    setMaxBufferSize (int value) {       this.MaxBufferSize = value;}
  public  int     getMaxBufferSize ()          {return this.MaxBufferSize;}



  public String getInputName(int i){
    switch (i) {
    }
    return "";
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
    switch (i) {
      case 0: return "Time";
      case 1: return "Bytes";
    }
    return "";
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

  public String getModuleInfo() {
    return "TailFile";
  }
  public String getModuleName() {
    return "TailFile";
  }

  public void beginExecution() {
    setTerminate(false);
  }
  void wait (int time)
  {
    try
    {
      synchronized (Thread.currentThread())
      {
        Thread.currentThread().wait(time);
      }
      } catch (Exception e) {System.out.println("wait error!!!");}
  }


  ////////////
  //  MAIN  //
  ////////////

  public void doit() throws Exception {

    wait(InitialWaitTimeInMilliseconds);

    RandomAccessFile randomAccessFile = null;

    try {
      randomAccessFile = new RandomAccessFile(FilePathName, "r");
    }
    catch (Exception e) {
      System.out.println("couldn't open file: " + FilePathName);
      throw e;
    }

    long length = randomAccessFile.length() - ReadOffset;

    int count = 0;
    long lastPosition;

    if (JumpToEnd) {
      randomAccessFile.seek(length - 1);
      lastPosition = length - 1;
    }
    else {
      if (ReadOffset > 0) {
        randomAccessFile.seek(ReadOffset);
        lastPosition = ReadOffset;
      }
      else {
        lastPosition = 0;
      }
    }

    while (!Terminate) {

      int numAvailable = (int) (randomAccessFile.length() - lastPosition);
      System.out.println("available = " + numAvailable);

      //while (randomAccessFile.length() - lastPosition < MinBufferSize) {
      System.out.println("waiting " + WaitTimeInMilliseconds + " milliseconds");
      wait(WaitTimeInMilliseconds);
      System.out.println("done waiting");
      //}

      if (numAvailable > 0) {


        Long time = new Long(System.currentTimeMillis());
        //int numAvailable = (int) (randomAccessFile.length() - lastPosition);


        if (numAvailable > MaxBufferSize)
          numAvailable = MaxBufferSize;

        byte [] buffer = new byte[numAvailable];

        int numRead = randomAccessFile.read(buffer);
        //int numRead = numAvailable;

        if (numRead != numAvailable)
          System.out.println("Error! numRead != numAvailable");

        lastPosition += numRead;

        count++;

        this.pushOutput(time,   0);
        this.pushOutput(buffer, 1);

        System.out.println("time = " + time + "  lastPosition = " + lastPosition);
      }
    }

    setTerminate(false);

    randomAccessFile.close();


  }
}
