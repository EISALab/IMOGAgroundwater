package ncsa.d2k.modules.projects.dtcheng;

import ncsa.d2k.modules.core.datatype.table.*;

import ncsa.d2k.core.modules.ComputeModule;

public class GetExampleInputs extends ComputeModule
  {

  public String getModuleInfo()
    {
		return "GetExampleInputs";
	}
  public String getModuleName()
    {
		return "GetExampleInputs";
	}

  public String[] getInputTypes()
    {
		String[] types = {"ncsa.d2k.modules.projects.dtcheng.Example"};
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
			case 0: return "Inputs";
			default: return "No such output";
		}
	}

  public String getOutputName(int i)
    {
		switch(i) {
			case 0:
				return "Inputs";
			default: return "NO SUCH OUTPUT!";
		}
	}

  public void doit()
    {
    Example example = (Example) this.pullInput(0);

    double [] inputs = new double[((ExampleTable) example.getTable()).getNumInputFeatures()];
    for (int i = 0; i < ((ExampleTable) example.getTable()).getNumInputFeatures(); i++)
      {
      inputs[i] = example.getInputDouble(i);
      }

    this.pushOutput(inputs, 0);
    }
  }
