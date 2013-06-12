package ncsa.d2k.modules.projects.dtcheng.io;

import ncsa.d2k.modules.projects.dtcheng.*;
import ncsa.d2k.core.modules.*;
import java.io.*;
import java.util.*;
import java.text.*;

/*
 issues:
  problem with mac vs. pc vs. unix text formats
 */

public class ReadWholeFile
    extends InputModule {

  //////////////////
  //  PROPERTIES  //
  //////////////////

  private String DataPath = "C:\\Program Files\\D2K Basic\\misc\\dtcheng\\life\\sprot40.dat";
  public void setDataPath(String value) {
    this.DataPath = value;
  }

  public String getDataPath() {
    return this.DataPath;
  }

  private int ReadBufferSize = 10000000;
  public void setReadBufferSize(int value) {
    this.ReadBufferSize = value;
  }

  public int getReadBufferSize() {
    return this.ReadBufferSize;
  }

  private int MaxNumBytesToRead = -1;
  public void setMaxNumBytesToRead(int value) {
    this.MaxNumBytesToRead = value;
  }

  public int getMaxNumBytesToRead() {
    return this.MaxNumBytesToRead;
  }

  public String getModuleName() {
    return "ReadWholeFile";
  }

  public String getModuleInfo() {
    return
        "This module reads the whole with one read and outputs the byte array.  ";
  }

  public String getInputName(int i) {
    switch (i) {
      default:
        return "No such input.";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      default:
        return "No such input.";
    }
  }

  public String[] getInputTypes() {
    String[] types = {};
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "ByteArray";
      default:
        return "NO SUCH OUTPUT!";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "A byte array representing the file.";
      default:
        return "No such output";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "[B"};
    return types;
  }

  public void doit() throws Exception {

    RandomAccessFile input = null;

    File file = new File(DataPath);

    int fileLength = (int) file.length();
    System.out.println("fileLength = " + fileLength);

    if ((MaxNumBytesToRead > 0) && (MaxNumBytesToRead < fileLength))
      fileLength = MaxNumBytesToRead;

    byte[] buffer = new byte[fileLength];

    try {
      input = new RandomAccessFile(file, "r");
    }
    catch (Exception e) {
      System.out.println("user.dir: " + System.getProperty("user.dir"));
      System.out.println("Error!!! could not open " + DataPath);
    }

    int numToRead = ReadBufferSize;
    int fillPtr = 0;

    while (true) {

      int numLeft = fileLength - fillPtr;

      if (numLeft < numToRead)
        numToRead = numLeft;

      int numRead = input.read(buffer, fillPtr, (int) numToRead);

      if (numRead <= 0)
        break;

      fillPtr += numRead;
    }

    input.close();
    this.pushOutput(buffer, 0);
    return;
  }

}