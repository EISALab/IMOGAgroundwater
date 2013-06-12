package ncsa.d2k.modules.projects.dtcheng.io;

import ncsa.d2k.modules.core.datatype.table.*;

import ncsa.d2k.core.modules.*;

public class PrintExampleSetAttributes extends IOModule
  {

  public String getModuleInfo()
    {
		return "PrintExampleSetAttributes";
	}
  public String getModuleName()
    {
		return "PrintExampleSetAttributes";
	}

  public String[] getInputTypes()
    {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
		return types;
	}

  public String[] getOutputTypes()
    {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
		return types;
	}

  public String getInputInfo(int i)
    {
		switch (i) {
			case 0: return "ExampleSet";
			default: return "No such input";
		}
	}

  public String getInputName(int i)
    {
		switch(i) {
			case 0:
				return "ExampleSet";
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
    ExampleTable exampleSet = (ExampleTable) this.pullInput(0);

    System.out.println("numExamples = " + exampleSet.getNumRows());
    System.out.println("numInputs = " + exampleSet.getNumInputFeatures());
    System.out.println("numOutputs = " + exampleSet.getNumOutputFeatures());

    for (int i = 0; i < exampleSet.getNumInputFeatures(); i++)
      {
      System.out.println("inputNames " + (i + 1) + " = " + exampleSet.getInputName(i));
      }
    for (int i = 0; i < exampleSet.getNumOutputFeatures(); i++)
      {
      System.out.println("outputNames " + (i + 1) + " = " + exampleSet.getOutputName(i));
      }

    this.pushOutput(exampleSet, 0);
    }
  }
