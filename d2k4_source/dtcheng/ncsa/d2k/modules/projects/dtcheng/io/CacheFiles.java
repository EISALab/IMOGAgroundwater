package ncsa.d2k.modules.projects.dtcheng.io;

import java.lang.Runtime;
import java.io.*;
import ncsa.d2k.core.modules.*;

public class CacheFiles
    extends InputModule {
  private boolean Trace = true;
  public void setTrace(boolean value) {
    this.Trace = value;
  }

  public boolean getTrace() {
    return this.Trace;
  }

  private String FTPCommandString = "C:\\Program Files\\NCSA\\Kerberos 5\\ftp.exe";
  //private String FTPCommandString     = "/usr/local/krb5/bin/ftp mss.ncsa.uiuc.edu";
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
    return "CacheFiles";
  }

  public String getModuleName() {
    return "CacheFiles";
  }

  public String[] getInputTypes() {
    String[] types = {
        "java.lang.String", "[S", "[S"};
    return types;
  }

  public String[] getOutputTypes() {
    String[] types = {
        "[S"};
    return types;
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "MachineName";
      case 1:
        return "RemoteFileNames";
      case 2:
        return "LocalFileNames";
      default:
        return "No such input";
    }
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "MachineName";
      case 1:
        return "RemoteFileNames";
      case 2:
        return "LocalFileNames";
      default:
        return "NO SUCH INPUT!";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "LocalFileNames";
      default:
        return "No such output";
    }
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "LocalFileNames";
      default:
        return "NO SUCH OUTPUT!";
    }
  }

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

  void ProcessInputStream(InputStream inputStream) {

    while (true) {
      int numBytesAvailable = 0;
      try {
        numBytesAvailable = inputStream.available();
      }
      catch (Exception e) {
        System.out.println("inputStream.available() error!!!");
      }

      //System.out.println("numBytesAvailable = " + numBytesAvailable);

      if (numBytesAvailable == 0)
        break;

      while (numBytesAvailable > 0) {
        int intByte = 0;
        try {
          intByte = inputStream.read();
        }
        catch (Exception e) {
          System.out.println("inputStream.read() error!!!");
        }

        System.out.print((char) intByte);
        numBytesAvailable--;
      }
    }
  }

  public void doit() {
    String machineName = (String)this.pullInput(0);
    String[] remoteFileNames = (String[])this.pullInput(1);
    String[] localFileNames = (String[])this.pullInput(2);

    int numFiles = remoteFileNames.length;

    boolean[] readFiles = new boolean[numFiles];

    int numFilesToRead = 0;
    for (int i = 0; i < numFiles; i++) {
      File TestCacheFiles = new File(localFileNames[i]);
      if (!TestCacheFiles.canRead()) {
        readFiles[i] = true;
        numFilesToRead++;
      }
    }

    if (numFilesToRead > 0) {
      String command = FTPCommandString + " " + machineName;
      String[] InputStreamLines = new String[numFilesToRead + 2];

      InputStreamLines[0] = user;
      int readFileIndex = 0;
      for (int i = 0; i < numFiles; i++) {
        if (readFiles[i]) {
          InputStreamLines[readFileIndex + 1] = "get " + remoteFileNames[i] + " " + localFileNames[i];
          readFileIndex++;
        }
      }
      InputStreamLines[numFilesToRead + 1] = "quit";

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
          10};

      for (int i = 0; i < InputStreamLines.length; i++) {
        byte[] values = InputStreamLines[i].getBytes();
        if (Trace)
          System.out.println("command = " + InputStreamLines[i]);
        for (int j = 0; j < values.length; j++) {
          try {
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
        }

        ProcessInputStream(inputStream);
        ProcessInputStream(errorStream);
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
        ProcessInputStream(errorStream);
      }

      /////////////////////////////////
      // process any remaning output //
      /////////////////////////////////

      ProcessInputStream(inputStream);
      ProcessInputStream(errorStream);
    }

    for (int i = 0; i < numFiles; i++) {
      if (readFiles[i]) {
        File TestCacheFiles = new File(localFileNames[i]);
        if (!TestCacheFiles.canRead()) {
          System.out.println("Warning!  " + remoteFileNames[i] + " could not be read to " + localFileNames[i]);
          localFileNames[i] = null;
        }
      }
    }

    this.pushOutput(localFileNames, 0);
  }

}