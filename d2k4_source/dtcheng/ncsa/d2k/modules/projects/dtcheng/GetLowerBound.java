package ncsa.d2k.modules.projects.dtcheng;


import ncsa.d2k.core.modules.ComputeModule;

public class GetLowerBound extends ComputeModule
  {

  public String getModuleInfo()
    {
		return "GetLowerBound";
	}
  public String getModuleName()
    {
		return "GetLowerBound";
	}

  public String[] getInputTypes()
    {
		String[] types = {"[[D"};
		return types;
	}

  public String[] getOutputTypes()
    {
		String[] types = {"[D"};
		return types;
	}

  public String getInputInfo(int i)
    {
		switch (i) {
			case 0: return "HyperSpace";
			default: return "No such input";
		}
	}

  public String getInputName(int i)
    {
		switch(i) {
			case 0:
				return "HyperSpace";
			default: return "NO SUCH INPUT!";
		}
	}

  public String getOutputInfo(int i)
    {
		switch (i) {
			case 0: return "LowerBounds";
			default: return "No such output";
		}
	}

  public String getOutputName(int i)
    {
		switch(i) {
			case 0:
				return "LowerBounds";
			default: return "NO SUCH OUTPUT!";
		}
	}

  public void doit()
    {
    double [][] hyperSpace  = (double [][]) this.pullInput(0);

    double []   lowerBounds = (double []) hyperSpace[0];

    this.pushOutput(lowerBounds, 0);

    }
  }
