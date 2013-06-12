package ncsa.d2k.modules.projects.dtcheng;


import ncsa.d2k.core.modules.*;

public class ModelToViewableDTModel extends ComputeModule
  {
  public String getModuleInfo()
    {
		return "ModelToViewableDTModel";
	}
  public String getModuleName()
    {
		return "ModelToViewableDTModel";
	}

  public String[] getInputTypes()
    {
		String[] types = {"ncsa.d2k.modules.projects.dtcheng.Model"};
		return types;
	}

  public String[] getOutputTypes()
    {
		String[] types = {"ncsa.d2k.modules.projects.dtcheng.DecisionTreeModel"};
		return types;
	}

  public String getInputInfo(int i)
    {
		switch (i) {
			case 0: return "Model";
			default: return "No such input";
		}
	}

  public String getInputName(int i)
    {
		switch(i) {
			case 0:
				return "Model";
			default: return "NO SUCH INPUT!";
		}
	}

  public String getOutputInfo(int i)
    {
		switch (i) {
			case 0: return "Model";
			default: return "No such output";
		}
	}

  public String getOutputName(int i)
    {
		switch(i) {
			case 0:
				return "DecisionTreeModel";
			default: return "NO SUCH OUTPUT!";
		}
	}



  public void doit()
    {
    /*
    DecisionTreeModel model = (DecisionTreeModel) this.pullInput(0);

    this.pushOutput(model, 0);
    */
    this.pushOutput(this.pullInput(0), 0);
    }
  }
