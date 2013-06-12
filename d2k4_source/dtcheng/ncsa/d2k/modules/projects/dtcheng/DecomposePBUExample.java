package ncsa.d2k.modules.projects.dtcheng;


import ncsa.d2k.core.modules.ComputeModule;

public class DecomposePBUExample extends ComputeModule
  {

  public String getModuleInfo()
    {
		return "DecomposePBUExample";
	}
  public String getModuleName()
    {
		return "DecomposePBUExample";
	}

  public String[] getInputTypes()
    {
		String[] types = {"[[D"};
		return types;
	}

  public String[] getOutputTypes()
    {
		String[] types = {"[D","[D","[D"};
		return types;
	}

  public String getInputInfo(int i)
    {
		switch (i) {
			case 0: return "Example";
			default: return "No such input";
		}
	}

  public String getInputName(int i)
    {
		switch(i) {
			case 0:
				return "Example";
			default: return "NO SUCH INPUT!";
		}
	}

  public String getOutputInfo(int i)
    {
		switch (i) {
			case 0: return "Problem";
			case 1: return "Bias";
			case 2: return "Utility";
			default: return "No such output";
		}
	}

  public String getOutputName(int i)
    {
		switch(i) {
			case 0:
				return "Problem";
			case 1:
				return "Bias";
			case 2:
				return "Utility";
			default: return "NO SUCH OUTPUT!";
		}
	}

  public void doit()
    {
    double [][] example      = (double [][]) this.pullInput(0);

    double [] problemArray = (double []) example[0];
    double [] biasArray    = (double []) example[1];
    double [] utilityArray = (double []) example[2];

    this.pushOutput(problemArray, 0);
    this.pushOutput(biasArray,    1);
    this.pushOutput(utilityArray, 2);

    }
  }
