package ncsa.d2k.modules.projects.dtcheng.primitive;


import ncsa.d2k.core.modules.ComputeModule;

public class FanOutTwoDouble1DArrays extends ComputeModule
  {

  public String getModuleInfo()
    {
		return "FanOutTwoDouble1DArrays";
	}
  public String getModuleName()
    {
		return "FanOutTwoDouble1DArrays";
	}

  public String[] getInputTypes()
    {
		String[] types = {"[D"};
		return types;
	}

  public String[] getOutputTypes()
    {
		String[] types = {"[D","[D"};
		return types;
	}

  public String getInputInfo(int i)
    {
		switch (i) {
			case 0: return "Array : Double1DArray[featureIndex]";
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
			case 0: return "ArrayReference1 : Double1DArray[featureIndex]";
			case 1: return "ArrayReference2 : Double1DArray[featureIndex]";
			default: return "No such output";
		}
	}

  public String getOutputName(int i)
    {
		switch(i) {
			case 0:
				return "ArrayReference1";
			case 1:
				return "ArrayReference2";
			default: return "NO SUCH OUTPUT!";
		}
	}

  public void doit()
    {
    double [] array = (double []) this.pullInput(0);

    this.pushOutput(array, 0);
    this.pushOutput(array, 1);
    }
  }
