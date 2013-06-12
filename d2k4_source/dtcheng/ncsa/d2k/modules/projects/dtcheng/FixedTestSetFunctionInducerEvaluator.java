package ncsa.d2k.modules.projects.dtcheng;
import ncsa.d2k.modules.core.datatype.model.*;
import ncsa.d2k.core.modules.ComputeModule;

public class FixedTestSetFunctionInducerEvaluator extends ComputeModule
  {

  private boolean    RecycleExamples = false;
  public  void    setRecycleExamples (boolean value) {       this.RecycleExamples = value;}
  public  boolean getRecycleExamples ()              {return this.RecycleExamples;}


  int PhaseIndex = 0;
  boolean InitialExecution = true;
  public boolean isReady()
    {
    boolean value = false;

    switch (PhaseIndex)
      {
      case 0:
      if (InitialExecution || (!RecycleExamples))
        {
        value = (getFlags()[0] > 0) && (getFlags()[1] > 0) && (getFlags()[2] > 0);
        }
      else
        {
        value = (getFlags()[0] > 0);
        }
      break;

      case 1:
      value = (getFlags()[3] > 0);
      break;
      }

    return value;
    }

  public String getModuleInfo()
    {
		return "FixedTestSetFunctionInducerEvaluator";
	}
  public String getModuleName()
    {
		return "FixedTestSetFunctionInducerEvaluator";
	}

  public String[] getInputTypes()
    {
		String[] types = {"ncsa.d2k.modules.projects.dtcheng.FunctionInducer","[[[D","[[[D","[D"};
		return types;
	}

  public String[] getOutputTypes()
    {
		String[] types = {"ncsa.d2k.modules.projects.dtcheng.FunctionInducer","[[[D","[[[D","[D"};
		return types;
	}

  public String getInputInfo(int i)
    {
		switch (i) {
			case 0: return "FunctionInducer";
			case 1: return "TrainExampleSet";
			case 2: return "TestExampleSet";
			case 3: return "Utility";
			default: return "No such input";
		}
	}

  public String getInputName(int i)
    {
		switch(i) {
			case 0:
				return "FunctionInducer";
			case 1:
				return "TrainExampleSet";
			case 2:
				return "TestExampleSet";
			case 3:
				return "Utility";
			default: return "NO SUCH INPUT!";
		}
	}

  public String getOutputInfo(int i)
    {
		switch (i) {
			case 0: return "FunctionInducer";
			case 1: return "TrainExamples";
			case 2: return "TestExamples";
			case 3: return "Utility";
			default: return "No such output";
		}
	}

  public String getOutputName(int i)
    {
		switch(i) {
			case 0:
				return "FunctionInducer";
			case 1:
				return "TrainExamples";
			case 2:
				return "TrainExamples";
			case 3:
				return "Utility";
			default: return "NO SUCH OUTPUT!";
		}
	}



  double [][][] TrainExamples = null;
  double [][][] TestExamples  = null;

  public void doit()
    {

    switch (PhaseIndex)
      {
      case 0:
      Model functionInducer = (Model) this.pullInput(0);

      if (InitialExecution || (!RecycleExamples))
        {
        TrainExamples   = (double [][][]) this.pullInput(1);
        TestExamples    = (double [][][]) this.pullInput(2);
        InitialExecution = false;
        }


      this.pushOutput(functionInducer, 0);
      this.pushOutput(TrainExamples  , 1);
      this.pushOutput(TestExamples   , 2);

      PhaseIndex = 1;
      break;

      case 1:
      double [] utility = (double []) this.pullInput(3);
      this.pushOutput(utility, 3);

      PhaseIndex = 0;
      break;
      }


    }
  }
