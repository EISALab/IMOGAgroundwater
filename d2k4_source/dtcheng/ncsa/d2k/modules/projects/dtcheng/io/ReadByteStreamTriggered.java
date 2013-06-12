package ncsa.d2k.modules.projects.dtcheng.io;

import ncsa.d2k.core.modules.*;
import java.io.*;
import java.util.*;
import java.text.*;

public class ReadByteStreamTriggered extends InputModule
{

  //////////////////
  //  PROPERTIES  //
  //////////////////

  private String  FilePathName       = "C:/test.wav";
  public  void    setFilePathName    (String  value) {       this.FilePathName    = value;}
  public  String  getFilePathName    ()              {return this.FilePathName           ;}

  private boolean        Active = true;
  public  void    setActive (boolean value) {       this.Active = value;}
  public  boolean     getActive ()          {return this.Active;}


  private int        ReadOffset = 0;
  public  void    setReadOffset (int value) {       this.ReadOffset = value;}
  public  int     getReadOffset ()          {return this.ReadOffset;}

  private int        BufferSize = 10000000;
  public  void    setBufferSize (int value) {       this.BufferSize = value;}
  public  int     getBufferSize ()          {return this.BufferSize;}



  public String[] getInputTypes() {
    String[] types = {"java.lang.Object"};
    return types;
  }
  public String getInputName(int i){
    switch (i) {
      case 0: return "Trigger";
      default: return "No such input";
    }
  }
  public String getInputInfo(int i) {
    switch (i) {
      case 0: return "Trigger";
      default: return "No such input";
    }
  }


  public String getOutputName(int i) {
    switch (i) {
      case 0: return "Bytes";
    }
    return "";
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

  public String getModuleInfo() {
    return "ReadByteStreamTriggered";
  }
  public String getModuleName() {
    return "ReadByteStreamTriggered";
  }

  int  count;
  long lastPosition;
  long length;
  RandomAccessFile randomAccessFile;
  byte [] buffer;

  public void beginExecution() {
    setActive(true);
    count = 0;
    lastPosition = 0;
    randomAccessFile = null;
    buffer = new byte[BufferSize];
    try {
      randomAccessFile = new RandomAccessFile(FilePathName, "r");
    }
    catch (Exception e) {
      System.out.println("couldn't open file: " + FilePathName);
    }

    try {
      length = randomAccessFile.length();
    }
    catch (Exception e) {
      System.out.println("couldn't length file: " + FilePathName);
    }

    if (ReadOffset > 0) {
      try {
        randomAccessFile.seek(ReadOffset);
      }
      catch (Exception e) {
        System.out.println("couldn't seek file: " + FilePathName);
      }
      lastPosition = ReadOffset;
    }
    else {
      lastPosition = 0;
    }
  }


  ////////////
  //  MAIN  //
  ////////////

  public void doit() throws Exception {

    // read trigger
    Object object = this.pullInput(0);

    long numAvailable = length - lastPosition;

    if (numAvailable == 0) {
      this.pushOutput(null, 0);
      randomAccessFile.close();
      Active = false;
      return;
    }
    else {

      int bufferSize = -1;

      if (numAvailable > BufferSize)
        bufferSize = BufferSize;
      else
        bufferSize = (int) numAvailable;

      if (buffer.length != bufferSize) {
        buffer = new byte[bufferSize];
      }

      int numRead = randomAccessFile.read(buffer);

      if (numRead != bufferSize)
        System.out.println("Error! numRead != numAvailable");

      lastPosition += numRead;

      count++;

      this.pushOutput(buffer, 0);
    }
  }
}