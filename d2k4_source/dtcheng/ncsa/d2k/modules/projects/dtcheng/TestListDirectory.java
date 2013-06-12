package ncsa.d2k.modules.projects.dtcheng;


import java.lang.Runtime;
import java.io.*;
import ncsa.d2k.core.modules.ComputeModule;
public class TestListDirectory extends ComputeModule
  {

  public String getModuleInfo()
    {
		return "TestListDirectory";
	}
  public String getModuleName()
    {
		return "TestListDirectory";
	}

  public String[] getInputTypes()
    {
		String[] types = {		};
		return types;
	}

  public String[] getOutputTypes()
    {
		String[] types = {"java.lang.String","java.lang.String"};
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
			case 1: return "RemotePathPattern";
			default: return "No such output";
		}
	}

  public String getOutputName(int i)
    {
		switch(i) {
			case 0:
				return "MachineName";
			case 1:
				return "RemotePathPattern";
			default: return "NO SUCH OUTPUT!";
		}
	}

  public void doit()
    {
    String machineName    = "mss.ncsa.uiuc.edu";
    String remotePathPattern = "/u/ncsa/bjewett/WRF/Output/ini/randA???/Plots/score1.dat";
    this.pushOutput(machineName,    0);
    this.pushOutput(remotePathPattern, 1);
    }


  }
