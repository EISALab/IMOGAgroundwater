package ncsa.d2k.modules.projects.dtcheng;

import java.lang.*;
import java.io.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.projects.dtcheng.primitive.*;

public class ExecTest
    extends InputModule {
  private boolean Trace = false;
  public void setTrace(boolean value) {
    this.Trace = value;
  }

  public boolean getTrace() {
    return this.Trace;
  }

  private String FTPCommandString = "C:\\Program Files\\NCSA\\Kerberos 5\\ftp.exe";
  public void setFTPCommandString(String value) {
    this.FTPCommandString = value;
  }

  public String getFTPCommandString() {
    return this.FTPCommandString;
  }

  private String user = "dtcheng";
  public String getUser() {
    return this.user;
  }

  public void setUser(String value) {
    this.user = value;
  }

  int waitTime = 100;
  public String getModuleInfo() {
    return "ExecTest";
  }

  public String getModuleName() {
    return "ExecTest";
  }

  public String[] getInputTypes() {
    String[] types = {
        "java.lang.String", "java.lang.String"};
    return types;
  }

  public String[] getOutputTypes() {
    String[] types = {
        "java.lang.String"};
    return types;
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "MachineName";
      case 1:
        return "RemotePathPattern";
      default:
        return "No such input";
    }
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "MachineName";
      case 1:
        return "RemotePathPattern";
      default:
        return "NO SUCH INPUT!";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "LocalFileName";
      default:
        return "No such output";
    }
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "LocalFileName";
      default:
        return "NO SUCH OUTPUT!";
    }
  }

  byte LineFeedByte = (byte) 10;

  void wait(int time) {
    try {
      synchronized (Thread.currentThread()) {
        Thread.currentThread().sleep(time);
      }
    }
    catch (Exception e) {
      System.out.println("wait error!!!");
    }
  }

  int MaxBufferSize = 1000000;
  byte[] OutputBuffer = new byte[MaxBufferSize];
  int OutputBufferIndex = 0;
  public void beginExecution() {
    OutputBufferIndex = 0;
  }

  void ProcessInputStream(InputStream inputStream) throws Exception {
    int numBytes = 0;

    while (true) {
      int numBytesAvailable = 0;
      try {
        numBytesAvailable = inputStream.available();
      }
      catch (Exception e) {
        System.out.println("inputStream.available() error!!!"); throw e;
      }

      if (numBytesAvailable == 0)
        break;

      try {
        numBytes = inputStream.read(OutputBuffer, OutputBufferIndex, numBytesAvailable);
        if (numBytes != numBytesAvailable) {
          System.out.println("numBytes != numBytesAvailable");
           throw new Exception();
        }
      }
      catch (Exception e) {
        System.out.println("inputStream.read() error!!!");
         throw e;
      }

      //System.out.print(new String(OutputBuffer, OutputBufferIndex, numBytesAvailable));

      OutputBufferIndex += numBytesAvailable;
    }
  }

  void IgnoreInputStream(InputStream inputStream) throws Exception {
    byte[] buffer = new byte[1000];
    int numBytes = 0;

    while (true) {
      int numBytesAvailable = 0;
      try {
        numBytesAvailable = inputStream.available();
      }
      catch (Exception e) {
        System.out.println("inputStream.available() error!!!");
      }

      if (numBytesAvailable == 0)
        break;

      while (numBytesAvailable > 0) {
        try {
          numBytes = inputStream.read(buffer, 0, 1);
        }
        catch (Exception e) {
          System.out.println("inputStream.read() error!!!");
          throw e;
        }

        System.out.print(new String(buffer, 0, numBytes));

        numBytesAvailable = numBytesAvailable - numBytes;
      }
    }
  }

  int MaxNumFileNames = 10000;
  String[] FileNames = new String[MaxNumFileNames];
  int NumFileNames = 0;

  void getFileNames() {
    NumFileNames = 0;

    int index = 0;

    // skip first 2 lines

    for (int i = 0; i < 2; i++) {
      while (true) {
        if (OutputBuffer[index] == LineFeedByte)
          break;
        index++;
      }
      index++;
    }

    // read file names
    while (true) {
      // skip header
      index += 71;
      if (index >= OutputBufferIndex)
        break;

      int lastIndex = index;
      while (true) {
        if (OutputBuffer[index] == LineFeedByte)
          break;
        index++;
      }
      index++;
      int length = index - lastIndex - 2;

      FileNames[NumFileNames] = new String(OutputBuffer, lastIndex, length);
      NumFileNames++;

      if (index >= OutputBufferIndex)
        break;

    }
  }

  public void doit() throws Exception {
    String machineName = (String)this.pullInput(0);
    String remoteFileName = (String)this.pullInput(1);

    String command = FTPCommandString + " " + machineName;
    String[] InputStreamLines = {
        user,
        "ls " + remoteFileName,
        "quit"};

    Runtime runtime = Runtime.getRuntime();
    Process process = null;
    try {
      process = runtime.exec(command);
    }
    catch (Exception e) {
      System.out.println("exec error!!!");
    }

    OutputStream outputStream = process.getOutputStream();
    InputStream inputStream = process.getInputStream();
    InputStream errorStream = process.getErrorStream();

    byte[] lineFeed = {
        LineFeedByte};

    for (int i = 0; i < InputStreamLines.length; i++) {
      byte[] values = InputStreamLines[i].getBytes();
      if (Trace)
        System.out.println("command = " + InputStreamLines[i]);
      for (int j = 0; j < values.length; j++) {
        try {
          //wait(waitTime);
          outputStream.write(values[j]);
          outputStream.flush();
        }
        catch (Exception e) {
          System.out.println("write error!!!");
        }
      }

      try {
        outputStream.write(lineFeed);
        outputStream.flush();
      }
      catch (Exception e) {
        System.out.println("write error!!!");
        throw e;
      }

      ProcessInputStream(inputStream);
      IgnoreInputStream(errorStream);
    }

    ////////////////////////////////
    // close process input stream //
    ////////////////////////////////
    try {
      outputStream.close();
    }
    catch (Exception e) {
      System.out.println("close error!!!");
    }

    /////////////////////////////
    // wait for process to end //
    /////////////////////////////
    while (true) {
      int exitValue = -1;
      try {
        exitValue = process.exitValue();
      }
      catch (Exception e) {
      }
      if (exitValue != -1)
        break;
      wait(waitTime);
      ProcessInputStream(inputStream);
      IgnoreInputStream(errorStream);
    }

    /////////////////////////////////
    // process any remaning output //
    /////////////////////////////////

    ProcessInputStream(inputStream);
    IgnoreInputStream(errorStream);

    getFileNames();

    String[] result = new String[NumFileNames];

    System.arraycopy( (Object) FileNames, 0, (Object) result, 0, NumFileNames);

    this.pushOutput(result, 0);
  }

}
