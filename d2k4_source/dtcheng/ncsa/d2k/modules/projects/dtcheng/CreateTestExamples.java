package ncsa.d2k.modules.projects.dtcheng;


import ncsa.d2k.core.modules.ComputeModule;

public class CreateTestExamples extends ComputeModule
  {

  public String getModuleInfo()
    {
		return "CreateTestExamples";
	}
  public String getModuleName()
    {
		return "CreateTestExamples";
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
    int    numExamples = 4;
    double [][][] examples = new double[numExamples][2][];

    double [][] inputs = new double[numExamples][2];
    double [][] outputs = new double[numExamples][1];

    // inputs
    examples[0][0] = new double [] {0.1, 0.1};
    examples[1][0] = new double [] {0.1, 0.9};
    examples[2][0] = new double [] {0.9, 0.1};
    examples[3][0] = new double [] {0.9, 0.1};
    // outputs
    examples[0][1] = new double [] {0.1};
    examples[1][1] = new double [] {0.9};
    examples[2][1] = new double [] {0.9};
    examples[3][1] = new double [] {0.1};

    /*
    for (int e = 0; e < numExamples; e++)
      {
      double x = (double) e / (numExamples - 1.0);
      examples[e][0][0] = x;
      examples[e][1][0] = x;
      }
    */

    this.pushOutput(examples, 0);

    }
  }
