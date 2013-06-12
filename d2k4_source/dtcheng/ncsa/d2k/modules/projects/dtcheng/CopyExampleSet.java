package ncsa.d2k.modules.projects.dtcheng;


import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.core.modules.*;

public class CopyExampleSet extends ComputeModule
  {

  public String getModuleInfo()
    {
		return "CopyExampleSet";
	}
  public String getModuleName()
    {
		return "CopyExampleSet";
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
			case 0: return "ncsa.d2k.modules.core.datatype.table.ExampleTable";
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
			case 0: return "ncsa.d2k.modules.projects.dtcheng.ByteExampleSet";
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
    this.pushOutput((ExampleTable) exampleSet.copy(), 0);
    }
  }
