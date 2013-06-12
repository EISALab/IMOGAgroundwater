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

public class WriteTimedByteStream
    extends OutputModule {

  private String FilePathName = "c:/TimedByteStream.dat";
  public void setFilePathName(String value) {
    this.FilePathName = value;
  }

  public String getFilePathName() {
    return this.FilePathName;
  }

  private int ReportInterval = 100;
  public void setReportInterval(int value) {
    this.ReportInterval = value;
  }

  public int getReportInterval() {
    return this.ReportInterval;
  }

  public String getModuleName() {
    return "WriteTimedByteStream";
  }

  public String getModuleInfo() {
    return "WriteTimedByteStream";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Time";
      case 1:
        return "Bytes";
      default:
        return "NotAvailable";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "java.lang.Long", "[B"};
    return types;
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "Time";
      case 1:
        return "Bytes";
      default:
        return "NotAvailable";
    }
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "Time";
      case 1:
        return "Bytes";
      default:
        return "No such output";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "java.lang.Long", "[B"};
    return types;
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "Time";
      case 1:
        return "Bytes";
      default:
        return "No such output";
    }
  }

  RandomAccessFile randomAccessFile = null;
  int count = 0;
  public void beginExecution() {
    count = 0;
    randomAccessFile = null;
  }

  static byte[] readBuffer = null;
  public void doit() throws Exception {
    if (randomAccessFile == null) {

      try {
        //erase file
        File file = null;
        file = new File(FilePathName);
        file.delete();
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
    }

    Long time = (Long)this.pullInput(0);
    byte[] bytes = (byte[])this.pullInput(1);

    if (time == null || bytes == null) {
      this.pushOutput(null, 0);
      this.pushOutput(null, 1);
      randomAccessFile.close();
      return;
    }

    int numBytes = bytes.length;

    randomAccessFile.writeLong(time.longValue());
    randomAccessFile.writeInt(numBytes);
    randomAccessFile.write(bytes);

    if (count % ReportInterval == 0) {
      System.out.println("WriteTimedByteStream count = " + count);
    }

    this.pushOutput(time, 0);
    this.pushOutput(bytes, 1);

    count++;
  }
}
