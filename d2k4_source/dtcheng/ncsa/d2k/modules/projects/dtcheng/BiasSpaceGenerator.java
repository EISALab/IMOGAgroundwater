package ncsa.d2k.modules.projects.dtcheng;


import ncsa.d2k.core.modules.ComputeModule;

public class BiasSpaceGenerator extends ComputeModule
  {

  public String getModuleInfo()
    {
		return "BiasSpaceGenerator";
	}
  public String getModuleName()
    {
		return "BiasSpaceGenerator";
	}

  public String[] getInputTypes()
    {
		String[] types = {		};
		return types;
	}

  public String[] getOutputTypes()
    {
		String[] types = {"[[D","[S"};
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
			case 0: return "[[D";
			case 1: return "[S";
			default: return "No such output";
		}
	}

  public String getOutputName(int i)
    {
		switch(i) {
			case 0:
				return "Bounds";
			case 1:
				return "Name";
			default: return "NO SUCH OUTPUT!";
		}
	}

  public void doit()
    {
    double [][] biasSpace = new double[][] {{0.0, 0.0}, {1.0, 1.0}};
    String [] names = new String[] {"p1", "p2"};

    this.pushOutput(biasSpace, 0);
    this.pushOutput(biasSpace, 0);
    }
  }
