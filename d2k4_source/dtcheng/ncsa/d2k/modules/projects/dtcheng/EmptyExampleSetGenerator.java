package ncsa.d2k.modules.projects.dtcheng;


import ncsa.d2k.core.modules.ComputeModule;

public class EmptyExampleSetGenerator extends ComputeModule
  {

  public String getModuleInfo()
    {
		return "EmptyExampleSetGenerator";
	}
  public String getModuleName()
    {
		return "EmptyExampleSetGenerator";
	}

  public String[] getInputTypes()
    {
		String[] types = {		};
		return types;
	}

  public String[] getOutputTypes()
    {
		String[] types = {"[[[D"};
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
			case 0: return "ExampleSet";
			default: return "No such output";
		}
	}

  public String getOutputName(int i)
    {
		switch(i) {
			case 0:
				return "ExampleSet";
			default: return "NO SUCH OUTPUT!";
		}
	}

  public void doit()
    {
    double [][][] exampleSet = new double [0][][];

    this.pushOutput(exampleSet, 0);
    }
  }
