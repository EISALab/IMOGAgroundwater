package ncsa.d2k.modules.projects.dtcheng;


import java.lang.Runtime;
import java.io.*;
import ncsa.d2k.core.modules.ComputeModule;

public class Test extends ComputeModule
{

  int waitTime = 100;
  //String command = "/usr/local/krb5/bin/ftp mss.ncsa.uiuc.edu";
  String command = "C:\\Program Files\\NCSA\\Kerberos 5\\ftp.exe < c:\\temp\\ftp.txt > c:\\temp\\ftp.out";
  String [] InputStreamLines = {"open mss", "dtcheng", "lcd c:\\temp", "cd /u/ncsa/bjewett/WRF/Output/ini",
    "ls randA001/Plots/ideal_params", "get randA001/Plots/ideal_params temp.dat"};

  public String getModuleInfo()
  {
    return "Test";
  }
  public String getModuleName()
  {
    return "Test";
  }

  public String[] getInputTypes()
  {
    String[] types = {		};
    return types;
  }

  public String[] getOutputTypes()
  {
    String[] types = {"java.lang.Object"};
    return types;
  }

  public String getInputInfo(int i)
  {
    switch (i) {
      default: return "No such input";
    }
  }

  public String getInputName(int i)
  {
    switch(i) {
      default: return "NO SUCH INPUT!";
    }
  }

  public String getOutputInfo(int i)
  {
    switch (i) {
      case 0: return "null";
      default: return "No such output";
    }
  }

  public String getOutputName(int i)
  {
    switch(i) {
      case 0:
        return "null";
      default: return "NO SUCH OUTPUT!";
    }
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

  public void doit()
  {
    for (int i = 0; i < 100; i++) {
    System.out.println("hello");
    this.wait(1000);
    }
    this.pushOutput(null, 0);
  }


}