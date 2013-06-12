package ncsa.d2k.modules.projects.dtcheng.obsolete;
import ncsa.d2k.modules.core.datatype.model.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.core.modules.ComputeModule;

public class OldModelEvaluator extends ComputeModule
  {

  private boolean InitialExecution     = true;
  private boolean ReuseInitialExamples = true;
  public  void    setReuseInitialExamples (boolean value) {       this.ReuseInitialExamples       = value;}
  public  boolean getReuseInitialExamples ()              {return this.ReuseInitialExamples;}

  private boolean CreatePredictionVTArrays     = false;
  public  void    setCreatePredictionVTArrays (boolean value) {       this.CreatePredictionVTArrays       = value;}
  public  boolean getCreatePredictionVTArrays ()              {return this.CreatePredictionVTArrays;}

  public void beginExecution()
    {
    InitialExecution = true;
    }

  public boolean isReady()
    {
    boolean value = false;

    if (ReuseInitialExamples)
      {
      if (InitialExecution)
        {
        value = (getFlags()[0] > 0) && (getFlags()[1] > 0) && (getFlags()[2] > 0);
        }
      else
        {
        value = (getFlags()[0] > 0) && (getFlags()[2] > 0);
        }
      }
    else
      {
      value = (getFlags()[0] > 0) && (getFlags()[1] > 0) && (getFlags()[2] > 0);
      }

    return value;
    }

  public String getModuleInfo()
    {
		return "OldModelEvaluator";
	}
  public String getModuleName()
    {
		return "OldModelEvaluator";
	}

  public String[] getInputTypes()
    {
		String[] types = {"ncsa.d2k.modules.projects.dtcheng.FunctionDouble1DArrayToDouble1DArray","ncsa.d2k.modules.core.datatype.table.ExampleTable","[D"};
		return types;
	}

  public String[] getOutputTypes()
    {
		String[] types = {"[D","[Ljava.lang.String","[[D"};
		return types;
	}

  public String getInputInfo(int i)
    {
		switch (i) {
			case 0: return "Model";
			case 1: return "ExampleSet";
			case 2: return "OutputIndex";
			default: return "No such input";
		}
	}

  public String getInputName(int i)
    {
		switch(i) {
			case 0:
				return "Model";
			case 1:
				return "ExampleSet";
			case 2:
				return "OuputIndex";
			default: return "NO SUCH INPUT!";
		}
	}

  public String getOutputInfo(int i)
    {
		switch (i) {
			case 0: return "Utility";
			case 1: return "[Ljava.lang.String";
			case 2: return "PredictionVT2DDArray";
			default: return "No such output";
		}
	}

  public String getOutputName(int i)
    {
		switch(i) {
			case 0:
				return "Utility";
			case 1:
				return "[Ljava.lang.String";
			case 2:
				return "PredictionVT2DDArray";
			default: return "NO SUCH OUTPUT!";
		}
	}






  ExampleTable lastExampleSet = null;

  public void doit() throws Exception
    {

    if (false)
      {
      System.out.println();
      System.out.println();
      System.out.println("Starting Model Evaluator");
      }

    // pull inputs //

    Model model = (Model) this.pullInput(0);

    ExampleTable exampleSet = null;
    if (ReuseInitialExamples)
      {
      if (InitialExecution)
        {
        exampleSet = (ExampleTable) this.pullInput(1);
        lastExampleSet = exampleSet;
        InitialExecution = false;
        }
      else
        {
        exampleSet = lastExampleSet;
        }
      }
    else
      {
      exampleSet = (ExampleTable) this.pullInput(1);
      }

    ExampleTable examples = exampleSet;

    int numExamples    = exampleSet.getNumRows();


    double [] OutputIndex = (double []) this.pullInput(2);
    int outputIndex = (int) OutputIndex[0];





    // optimize model parameters //

    double errorSum  = 0.0;
    double outputSum  = 0.0;
    double varianceSum  = 0.0;

    String [] PredictionVT1DSArray = null;
    double [][] PredictionVT2DDArray = null;

    if (CreatePredictionVTArrays)
      {
      PredictionVT1DSArray = new String [] {"Count",
                                            "Predicted" + (outputIndex + 1),
                                            "Actual"    + (outputIndex + 1)};

      PredictionVT2DDArray = new double [numExamples][3];
      }

    for (int e = 0; e < numExamples; e++)
      {
      double [] predictedOutput = model.evaluate(examples, e);

      double difference = predictedOutput[0] - examples.getOutputDouble(e, outputIndex);
      double variance = difference * difference;


      double error = Math.abs(difference);


      if (CreatePredictionVTArrays)
        {
        PredictionVT2DDArray[e][0] = e + 1;
        PredictionVT2DDArray[e][1] = predictedOutput[0];
        PredictionVT2DDArray[e][2] = examples.getOutputDouble(e, outputIndex);
        }


      errorSum += error;
      outputSum += examples.getOutputDouble(e, outputIndex);
      varianceSum += variance;
      }

    double absoluteError = errorSum / numExamples;
    double relativeError = errorSum / outputSum;
    double standardError = Math.sqrt(varianceSum / numExamples);

    if (false)
    {
    System.out.println("Error numExamples = " + numExamples);
    System.out.println("errorSum      = " + errorSum);
    System.out.println("outputSum     = " + outputSum);
    System.out.println("absoluteError = " + absoluteError);
    System.out.println("relativeError = " + relativeError);
    System.out.println("standardError = " + standardError);
    }

    // instantiate model //

    double [] utility = new double [] {absoluteError, relativeError, standardError};


    // push outputs //
if (Double.isNaN(utility[0]))
  System.out.println ("Hi:"+utility);

    this.pushOutput(utility, 0);

    if (CreatePredictionVTArrays)
      {
      this.pushOutput(PredictionVT1DSArray, 1);
      this.pushOutput(PredictionVT2DDArray, 2);
      }


    }
  }
