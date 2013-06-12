package ncsa.d2k.modules.projects.dtcheng;
import ncsa.d2k.modules.core.datatype.model.*;

import ncsa.d2k.modules.core.datatype.table.*;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.projects.dtcheng.primitive.*;

public class ModelEvaluator extends ComputeModule  /*, Reentrant*/
  {
  private String     ErrorFunctionName = "AbsoluteError";
  public  void    setErrorFunctionName (String value) {       this.ErrorFunctionName = value;}
  public  String  getErrorFunctionName ()          {return this.ErrorFunctionName;}

  private boolean PrintErrors          = false;
  public  void    setPrintErrors       (boolean value) {       this.PrintErrors       = value;}
  public  boolean getPrintErrors       ()              {return this.PrintErrors;}

  private boolean PrintPredictions          = false;
  public  void    setPrintPredictions       (boolean value) {       this.PrintPredictions       = value;}
  public  boolean getPrintPredictions       ()              {return this.PrintPredictions;}

  private boolean    RecycleExamples = false;
  public  void    setRecycleExamples (boolean value) {       this.RecycleExamples = value;}
  public  boolean getRecycleExamples ()              {return this.RecycleExamples;}

  private boolean CreatePredictionVTArrays     = false;
  public  void    setCreatePredictionVTArrays (boolean value) {       this.CreatePredictionVTArrays       = value;}
  public  boolean getCreatePredictionVTArrays ()              {return this.CreatePredictionVTArrays;}

  boolean InitialExecution = true;
  public boolean isReady()
    {
    boolean value = false;

    if (InitialExecution || (!RecycleExamples))
      {
      value = (getFlags()[0] > 0) && (getFlags()[1] > 0);
      }
    else
      {
      value = (getFlags()[0] > 0);
      }
    return value;
    }

  public String getModuleInfo()
    {
		return "ModelEvaluator";
	}
  public String getModuleName()
    {
		return "ModelEvaluator";
	}

  public String[] getInputTypes()
    {
		String[] types = {"ncsa.d2k.modules.projects.dtcheng.Model","ncsa.d2k.modules.core.datatype.table.ExampleTable"};
		return types;
	}

  public String[] getOutputTypes()
    {
		String[] types = {"[D","[Ljava.lang.String","[[[D"};
		return types;
	}

  public String getInputInfo(int i)
    {
		switch (i) {
			case 0: return "Model";
			case 1: return "ExampleSet";
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
			default: return "NO SUCH INPUT!";
		}
	}

  public String getOutputInfo(int i)
    {
		switch (i) {
			case 0: return "Utility";
			case 1: return "PredictionVT1DSArray";
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
				return "PredictionVT1DSArray";
			case 2:
				return "PredictionVT2DDArray";
			default: return "NO SUCH OUTPUT!";
		}
	}



  ExampleTable exampleSet = null;

  public void doit() throws Exception
    {

    Model model = (Model) this.pullInput(0);

    if (InitialExecution || (!RecycleExamples))
      {
      exampleSet  = (ExampleTable) this.pullInput(1);
      InitialExecution = false;
      }

    ExampleTable examples = exampleSet;

    int numExamples = examples.getNumRows();

    // optimize model parameters //

    double errorSum      = 0.0;
    double outputSum     = 0.0;
    double varianceSum   = 0.0;
    double likelihoodSum = 0.0;

    String [] PredictionVT1DSArray = null;
    double [][] PredictionVT2DDArray = null;

    if (CreatePredictionVTArrays)
      {
      PredictionVT1DSArray = new String [] {"Count",
                                            "Predicted",
                                            "Actual"};
      PredictionVT2DDArray = new double [numExamples][3];
      }

    double classificationErrorSum = 0.0;
    for (int e = 0; e < numExamples; e++)
      {
      double [] predictedOutput = model.evaluate(examples, e);


      if (PrintPredictions)
        {
        System.out.println("predicted = " + predictedOutput[0] + "  actual = " + examples.getOutputDouble(e, 0));
        }

      double actualOutput = examples.getOutputDouble(e, 0);
      double difference   = predictedOutput[0] - actualOutput;
      double variance     = difference * difference;
      double error        = Math.abs(difference);

      if (Math.round(predictedOutput[0]) != Math.round(actualOutput))
        {
        classificationErrorSum++;
        }

      if (CreatePredictionVTArrays)
        {
        PredictionVT2DDArray[e][0] = e + 1;
        PredictionVT2DDArray[e][1] = predictedOutput[0];
        PredictionVT2DDArray[e][2] = examples.getOutputDouble(e, 0);
        }

      errorSum += error;
      outputSum += examples.getOutputDouble(e, 0);
      varianceSum += variance;





      int ActualOutputClass = (int) actualOutput;

      double predictedClassProbability = predictedOutput[0];

      if (ActualOutputClass == 0)
        predictedClassProbability = 1.0 - predictedClassProbability;


      likelihoodSum += Math.log(predictedClassProbability);

      }

    double absoluteError = errorSum / numExamples;
    double classificationError = classificationErrorSum / numExamples;
    double relativeError = errorSum / outputSum;
    double standardError = Math.sqrt(varianceSum / numExamples);
    double likelihood    = - likelihoodSum / numExamples;


    double [] utility = new double [1];
    if (ErrorFunctionName.equalsIgnoreCase("AbsoluteError") || ErrorFunctionName.equalsIgnoreCase("Absolute"))
      {
      utility[0] = absoluteError;
      }
    else
    if (ErrorFunctionName.equalsIgnoreCase("ClassificationError") || ErrorFunctionName.equalsIgnoreCase("Classification"))
      {
      utility[0] = classificationError;
      }
    else
    if (ErrorFunctionName.equalsIgnoreCase("RelativeError") || ErrorFunctionName.equalsIgnoreCase("Relative"))
      {
      utility[0] = relativeError;
      }
    else
    if (ErrorFunctionName.equalsIgnoreCase("StandardError") || ErrorFunctionName.equalsIgnoreCase("Standard"))
      {
      utility[0] = standardError;
      }
    else
    if (ErrorFunctionName.equalsIgnoreCase("Likelihood"))
      {
      utility[0] = likelihood;
      }
    else
      {
      System.out.println("unknown error type");
      throw new Exception();

      }


    if (PrintErrors)
      {
      System.out.println(ErrorFunctionName + " = " + utility[0]);
      }



    // push outputs //

    this.pushOutput(utility, 0);

    if (CreatePredictionVTArrays)
      {
      this.pushOutput(PredictionVT1DSArray, 1);
      this.pushOutput(PredictionVT2DDArray, 2);
      }


    }
  }
