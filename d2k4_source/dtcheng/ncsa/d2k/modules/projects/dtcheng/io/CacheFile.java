package ncsa.d2k.modules.projects.dtcheng.io;


import java.lang.Runtime;
import java.io.*;
import ncsa.d2k.core.modules.*;


public class CacheFile extends InputModule {
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


  private boolean Trace = false;
  public boolean getTrace() {
    return this.Trace;
  }


  public void setTrace(boolean value) {
    this.Trace = value;
  }


  private boolean TryUntilFileRead = true;
  public boolean getTryUntilFileRead() {
    return this.TryUntilFileRead;
  }


  public void setTryUntilFileRead(boolean value) {
    this.TryUntilFileRead = value;
  }


  private int WaitBetweenTriesInMS = 10000;
  public int getWaitBetweenTriesInMS() {
    return this.WaitBetweenTriesInMS;
  }


  public void setWaitBetweenTriesInMS(int value) {
    this.WaitBetweenTriesInMS = value;
  }


  int waitTime = 100;
  public String getModuleInfo() {
    return "CacheFile";
  }


  public String getModuleName() {
    return "CacheFile";
  }


  public String[] getInputTypes() {
    String[] types = {
        "java.lang.String", "java.lang.String", "java.lang.String"};
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
        return "RemoteFileName";
      case 2:
        return "LocalFileName";
      default:
        return "No such input";
    }
  }


  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "MachineName";
      case 1:
        return "RemoteFileName";
      case 2:
        return "LocalFileName";
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


  void wait(int time) {
    try {
      synchronized (Thread.currentThread()) {
        Thread.currentThread().sleep(time);
      }
    } catch (Exception e) {
      System.out.println("wait error!!!");
    }
  }


  void ProcessInputStream(InputStream inputStream) {

    while (true) {
      int numBytesAvailable = 0;
      try {
        numBytesAvailable = inputStream.available();
      } catch (Exception e) {
        System.out.println("inputStream.available() error!!!");
      }

      //System.out.println("numBytesAvailable = " + numBytesAvailable);

      if (numBytesAvailable == 0)
        break;

      while (numBytesAvailable > 0) {
        int intByte = 0;
        try {
          intByte = inputStream.read();
        } catch (Exception e) {
          System.out.println("inputStream.read() error!!!");
        }

        if (Trace)
          System.out.print((char) intByte);
        numBytesAvailable--;
      }
    }
  }


  public void doit() {
    String machineName = (String)this.pullInput(0);
    String remoteFileName = (String)this.pullInput(1);
    String localFileName = (String)this.pullInput(2);

    File TestCacheFile = new File(localFileName);
    if (!TestCacheFile.canRead()) {
      while (true) {

        String command = FTPCommandString + " " + machineName;
        String[] InputStreamLines = {
            user,
            "get " + remoteFileName + " " + localFileName,
            "quit"};

        Runtime runtime = Runtime.getRuntime();
        Process process = null;
        try {
          process = runtime.exec(command);
        } catch (Exception e) {
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
            } catch (Exception e) {
              System.out.println("write error!!!");
            }
          }

          try {
            outputStream.write(lineFeed);
            outputStream.flush();
          } catch (Exception e) {
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
        } catch (Exception e) {
          System.out.println("close error!!!");
        }

        /////////////////////////////
        // wait for process to end //
        /////////////////////////////
        while (true) {
          int exitValue = -1;
          try {
            exitValue = process.exitValue();
          } catch (Exception e) {
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

        File file = new File(localFileName);

        boolean fileCanBeRead = false;
        if (file.canRead()) {
          fileCanBeRead = true;
        }

        if (TryUntilFileRead) {
          if (!fileCanBeRead) {
            wait(WaitBetweenTriesInMS);
            continue;
          }
          else
            break;
        }
        else {
          if (!fileCanBeRead) {
            localFileName = null;
          }
          break;
        }
      }
    }
    this.pushOutput(localFileName, 0);
  }

}
