package ncsa.d2k.modules.projects.dtcheng;

import ncsa.d2k.modules.core.datatype.table.*;

import ncsa.d2k.core.modules.ComputeModule;

public class GetExampleOutputs extends ComputeModule
  {

  public String getModuleInfo()
    {
		return "GetExampleOutputs";
	}
  public String getModuleName()
    {
		return "GetExampleOutputs";
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
			case 0: return "Outputs";
			default: return "No such output";
		}
	}

  public String getOutputName(int i)
    {
		switch(i) {
			case 0:
				return "Outputs";
			default: return "NO SUCH OUTPUT!";
		}
	}

  public void doit()
    {
    Example example = (Example) this.pullInput(0);

    ExampleTable table = (ExampleTable) example.getTable();

    double [] outputs = new double[table.getNumOutputFeatures()];
    for (int i = 0; i < table.getNumOutputFeatures(); i++)
      {
      outputs[i] = example.getOutputDouble(i);
      }

    this.pushOutput(outputs, 0);
    }
  }
