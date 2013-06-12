package ncsa.d2k.modules.projects.dtcheng;


import ncsa.d2k.core.modules.ComputeModule;

public class SpaceToLowerPoint extends ComputeModule
  {

  public String getModuleInfo()
    {
		return "SpaceToLowerPoint";
	}
  public String getModuleName()
    {
		return "SpaceToLowerPoint";
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
			case 0: return "Double2DArray";
			default: return "No such input";
		}
	}

  public String getInputName(int i)
    {
		switch(i) {
			case 0:
				return "Array";
			default: return "NO SUCH INPUT!";
		}
	}

  public String getOutputInfo(int i)
    {
		switch (i) {
			case 0: return "Double1DArray";
			default: return "No such output";
		}
	}

  public String getOutputName(int i)
    {
		switch(i) {
			case 0:
				return "Array";
			default: return "NO SUCH OUTPUT!";
		}
	}

  public void doit()
    {
    double [][] array2D = (double [][]) this.pullInput(0);
    double []   array1D = array2D[0];

    this.pushOutput(array1D, 0);
    }
  }
