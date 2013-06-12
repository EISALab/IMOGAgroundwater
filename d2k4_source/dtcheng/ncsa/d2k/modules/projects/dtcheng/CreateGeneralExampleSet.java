package ncsa.d2k.modules.projects.dtcheng;
import ncsa.d2k.modules.core.datatype.table.*;


import ncsa.d2k.core.pipes.Pipe;
import ncsa.d2k.core.modules.ComputeModule;
import java.util.Random;

public class CreateGeneralExampleSet extends ComputeModule
  {

  private String OutputFeatureName    = "Output";
  public  void   setOutputFeatureName (String value) {       this.OutputFeatureName       = value;}
  public  String getOutputFeatureName ()             {return this.OutputFeatureName;}


  public String getModuleInfo()
    {
		return "CreateGeneralExampleSet";
	}
  public String getModuleName()
    {
    return "CreateGeneralExampleSet";
    }

  public String[] getInputTypes()
    {
		String[] types = {"[Lncsa.d2k.modules.projects.dtcheng.Example;"};
		return types;
	}
  public String getInputName(int i)
    {
    switch (i)
      {
      case 0: return "ExampleArray";
      }
    return "";
    }
  public String getInputInfo(int i)
    {
		switch (i) {
			case 0: return "PositiveByteArrays";
			default: return "No such input";
		}
	}

  public String getOutputName(int i)
    {
    switch (i)
      {
      case 0: return "ExampleSet";
      }
    return "";
    }
  public String[] getOutputTypes()
    {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
		return types;
	}
  public String getOutputInfo(int i)
    {
		switch (i) {
			case 0: return "DoubleExampleSet";
			default: return "No such output";
		}
	}




   public void doit()
    {
    MutableExample [] examples = (MutableExample []) this.pullInput(0);

    GeneralExampleSet exampleSet = new GeneralExampleSet(examples);

    this.pushOutput(exampleSet, 0);
    }


  }
