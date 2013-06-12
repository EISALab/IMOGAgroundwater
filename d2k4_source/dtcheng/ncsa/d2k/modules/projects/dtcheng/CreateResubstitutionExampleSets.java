package ncsa.d2k.modules.projects.dtcheng;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.core.modules.ComputeModule;
import java.util.Random;

public class CreateResubstitutionExampleSets extends ComputeModule
  {

  public String getModuleInfo()
    {
		return "CreateResubstitutionExampleSets";
	}
  public String getModuleName()
    {
    return "CreateResubstitutionExampleSets";
    }

  public String[] getInputTypes()
    {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
		return types;
	}

  public String[] getOutputTypes()
    {
		String[] types = {"[[Lncsa.d2k.modules.projects.dtcheng.ExampleSet;"};
		return types;
	}

  public String getInputInfo(int i)
    {
		switch (i) {
			case 0: return "ncsa.d2k.modules.core.datatype.table.ExampleTable;";
			default: return "No such input";
		}
	}

  public String getInputName(int i)
    {
    switch (i)
      {
      case 0: return "ncsa.d2k.modules.core.datatype.table.ExampleTable";
      }
    return "";
    }

  public String getOutputInfo(int i)
    {
		switch (i) {
			case 0: return "[[Lncsa.d2k.modules.projects.dtcheng.ExampleSet;";
			default: return "No such output";
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


/*****************************************************************************/
/* This function returns a random integer between min and max (both          */
/* inclusive).                                                               */
/*****************************************************************************/

  ////////////////
  ///   WORK   ///
  ////////////////
   public void doit()
    {


    ///////////////////////
    ///   PULL INPUTS   ///
    ///////////////////////

    ExampleTable      exampleSet = (ExampleTable) this.pullInput(0);
    ExampleTable [][] exampleSets = new ExampleTable[1][2];

    exampleSets[0][0] = exampleSet;
    exampleSets[0][1] = exampleSet;


    ////////////////////////
    ///   PUSH OUTPUTS   ///
    ////////////////////////

    this.pushOutput(exampleSets, 0);
    }

  }
