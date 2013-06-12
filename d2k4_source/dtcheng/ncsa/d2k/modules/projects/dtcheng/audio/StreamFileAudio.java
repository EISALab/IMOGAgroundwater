package ncsa.d2k.modules.projects.dtcheng.audio;

import ncsa.d2k.core.modules.*;
import java.io.*;
import java.util.*;
import java.text.*;
//-Xms35M -Xmx350M -Djava.security.policy=policy.all -Djava.rmi.server.codebase=http://bluesky.ncsa.uiuc.edu:8087/ -Dd2k.classpath=lib/LocalController.jar
public class StreamFileAudio extends InputModule
{

  //////////////////
  //  PROPERTIES  //
  //////////////////

  private String  FilePathName       = "C:/test3.wav";
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

  private int        BytesPerSample = 3;
  public  void    setBytesPerSample (int value) {       this.BytesPerSample = value;}
  public  int     getBytesPerSample ()          {return this.BytesPerSample;}

  private int        ReportInterval = 30;
  public  void    setReportInterval (int value) {       this.ReportInterval = value;}
  public  int     getReportInterval ()          {return this.ReportInterval;}

  private int        SamplesPerFrame = 1600;
  public  void    setSamplesPerFrame (int value) {       this.SamplesPerFrame = value;}
  public  int     getSamplesPerFrame ()          {return this.SamplesPerFrame;}



  public String[] getInputTypes() {
    String[] types = {};
    return types;
  }
  public String getInputName(int i){
    switch (i) {
    }
    return "";
  }
  public String getInputInfo(int i) {
    switch (i) {
      default: return "No such input";
    }
  }


  public String[] getOutputTypes() {
    String[] types = {"java.lang.Long", "[D"};
    return types;
  }
  public String getOutputName(int i) {
    switch (i) {
      case 0: return "Time";
      case 1: return "Samples";
    }
    return "";
  }
  public String getOutputInfo(int i) {
    switch (i) {
      case 0: return "Time";
      case 1: return "Samples";
      default: return "No such output";
    }
  }

  public String getModuleInfo() {
    return "StreamFileAudio";
  }
  public String getModuleName() {
    return "StreamFileAudio";
  }


  public boolean isReady() {
    if (!Terminate)
      return true;
    else
      return false;
  }




  RandomAccessFile randomAccessFile = null;

  long length;

  int count;
  long lastPosition;

  int bufferSize;
  byte   [] buffer;

  double scalingFactor;
  public void beginExecution() {
    setTerminate(false);

    try {
      randomAccessFile = new RandomAccessFile(FilePathName, "r");
    }
    catch (Exception e) {
      System.out.println("couldn't open file: " + FilePathName);
    }

    try {
      length = randomAccessFile.length() - ReadOffset;
    }
    catch (Exception e) {
      System.out.println("randomAccessFile.length() failed");
    }


    count = 0;

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

    bufferSize = BytesPerSample * SamplesPerFrame;
    buffer  = new byte[bufferSize];

    scalingFactor = 1 << (BytesPerSample * 8 - 1);

  }

  public void endExecution() {
    setTerminate(false);
    try {
      randomAccessFile.close();
    }
    catch (Exception e) {
      System.out.println("couldn't close file: " + FilePathName);
    }


  }


  int unsignedByte(byte value) {
    if (value > 0)
      return value;
    else
      return 256 + value;
  }


  public void doit() throws Exception {


    if (Terminate) {
      return;
    }

    int numAvailable = (int) (randomAccessFile.length() - lastPosition);

    if (numAvailable > bufferSize) {

      Long time = new Long(System.currentTimeMillis());

      double [] samples = new double[SamplesPerFrame];
      int numRead = randomAccessFile.read(buffer);

      if (numRead != buffer.length) {
        setTerminate(true);
        return;
      }

      lastPosition += numRead;

      int index = 0;
      for (int i = 0; i < SamplesPerFrame; i++) {
        int value1 = unsignedByte(buffer[index++]);
        int value2 = unsignedByte(buffer[index++]);
        int value3 = unsignedByte(buffer[index++]);
        double signedValue = ((int) (((((value3 << 8) + value2) << 8) + value1) << 8)) / 8;
        samples[i] = signedValue / scalingFactor;
      }

      count++;

      if (count % ReportInterval == 0)
        System.out.println("count = " + count);


      this.pushOutput(time,    0);
      this.pushOutput(samples, 1);
    }



  }
}