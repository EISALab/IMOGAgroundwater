package ncsa.d2k.modules.projects.dtcheng;


import java.lang.Runtime;
import java.io.*;
import ncsa.d2k.core.modules.ComputeModule;

public class TestCacheFile extends ComputeModule
  {

  public String getModuleInfo()
    {
		return "TestCacheFile";
	}
  public String getModuleName()
    {
		return "TestCacheFile";
	}

  public String[] getInputTypes()
    {
		String[] types = {		};
		return types;
	}

  public String[] getOutputTypes()
    {
		String[] types = {"java.lang.String","java.lang.String","java.lang.String"};
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
			case 0: return "MachineName";
			case 1: return "RemoteFileName";
			case 2: return "LocalFileName";
			default: return "No such output";
		}
	}

  public String getOutputName(int i)
    {
		switch(i) {
			case 0:
				return "MachineName";
			case 1:
				return "RemoteFileName";
			case 2:
				return "LocalFileName";
			default: return "NO SUCH OUTPUT!";
		}
	}

  public void doit()
    {
    String machineName    = "kramden.ncsa.uiuc.edu";
    String remoteFileName = "/scratch2/dtcheng/done.txt";
    String localFileName  = "c:\\temp\\done.txt";
    this.pushOutput(machineName,    0);
    this.pushOutput(remoteFileName, 1);
    this.pushOutput(localFileName,  2);
    }


  }
