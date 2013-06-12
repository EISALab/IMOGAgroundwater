package ncsa.d2k.modules.projects.dtcheng.io;

import ncsa.d2k.core.modules.*;
import java.io.*;
import java.util.*;
import java.text.*;

public class ReadByteStream extends InputModule
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
    return "ReadByteStream";
  }
  public String getModuleName() {
    return "ReadByteStream";
  }

  int  count;
  long lastPosition;
  long length;
  RandomAccessFile randomAccessFile;

  public void beginExecution() {
    setActive(true);
    count = 0;
    lastPosition = 0;
    randomAccessFile = null;
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


  public boolean isReady() {
    return Active;
  }

  ////////////
  //  MAIN  //
  ////////////

  public void doit() throws Exception {

    int numAvailable = (int) (length - lastPosition);

    if (numAvailable == 0) {
      this.pushOutput(null, 0);
      randomAccessFile.close();
      Active = false;
      return;
    }
    else {
      if (numAvailable > BufferSize)
        numAvailable = BufferSize;

      byte [] buffer = new byte[numAvailable];

      int numRead = randomAccessFile.read(buffer);

      if (numRead != numAvailable)
        System.out.println("Error! numRead != numAvailable");

      lastPosition += numRead;

      count++;

      this.pushOutput(buffer, 0);
    }
  }
}