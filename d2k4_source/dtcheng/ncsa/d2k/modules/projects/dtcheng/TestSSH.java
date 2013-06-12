package ncsa.d2k.modules.projects.dtcheng;


import java.lang.Runtime;
import java.io.*;
import ncsa.d2k.core.modules.ComputeModule;

public class TestSSH extends ComputeModule
  {

  public String getModuleInfo()
    {
		return "TestSSH";
	}
  public String getModuleName()
    {
		return "TestSSH";
	}

  public String[] getInputTypes()
    {
		String[] types = {		};
		return types;
	}

  public String[] getOutputTypes()
    {
		String[] types = {"java.lang.String","[S","[S","[Z"};
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
			case 1: return "Commands";
			case 2: return "Prompts";
			case 3: return "Hiddens";
			default: return "No such output";
		}
	}

  public String getOutputName(int i)
    {
		switch(i) {
			case 0:
				return "MachineName";
			case 1:
				return "Commands";
			case 2:
				return "Prompts";
			case 3:
				return "Hiddens";
			default: return "NO SUCH OUTPUT!";
		}
	}

  public void doit()
    {
    String    machineName = "kramden.ncsa.uiuc.edu";
    String  [] commands    = {"dtcheng", "cwsII555",   "kinit redman", "B6usfdz6",   "telnet -l redman modi4", "ls", "exit", "exit"};
    String  [] prompts     = {"login: ",  "dtcheng: ", "kramden%",     "NCSA.EDU: ", "kramden% ",              "% ", "% ",    "% "};
    boolean [] hiddens     = {false,     true,         false,          true,         false,                    true, true,    true};
    //String    machineName = "modi4.ncsa.uiuc.edu";
    //String  [] commands    = {"ls", "pwd", "exit"};
    //String  [] prompts     = {">",  ">",  ">"};
    //boolean [] hiddens     = {false, false, false};
    this.pushOutput(machineName, 0);
    this.pushOutput(commands,    1);
    this.pushOutput(prompts,     2);
    this.pushOutput(hiddens,     3);
    }


  }
