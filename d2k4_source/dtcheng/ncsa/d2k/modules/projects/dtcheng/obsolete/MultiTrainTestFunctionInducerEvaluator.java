package ncsa.d2k.modules.projects.dtcheng.obsolete;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.model.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.prediction.*;

public class MultiTrainTestFunctionInducerEvaluator extends ComputeModule
{
  private int     BatchSize = 1;
  public  void setBatchSize (int value) {       this.BatchSize = value;}
  public  int  getBatchSize ()          {return this.BatchSize;}

  private boolean    Trace = false;
  public  void    setTrace (boolean value) {       this.Trace = value;}
  public  boolean getTrace ()              {return this.Trace;}

  private boolean    RecycleExamples = false;
  public  void    setRecycleExamples (boolean value) {       this.RecycleExamples = value;}
  public  boolean getRecycleExamples ()              {return this.RecycleExamples;}



  public String getModuleInfo()
  {
    return "MultiTrainTestFunctionInducerEvaluator";
  }
  public String getModuleName()
  {
    return "MultiTrainTestFunctionInducerEvaluator";
  }

  public String[] getInputTypes()
  {
    String[] types = {
      "ncsa.d2k.modules.projects.dtcheng.FunctionInducer",
      "[Lncsa.d2k.modules.projects.dtcheng.ErrorFunction;",
      "[[Lncsa.d2k.modules.projects.dtcheng.ExampleSet;",
      "[D"};
    return types;
  }

  public String[] getOutputTypes()
  {
    String[] types = {
      "ncsa.d2k.modules.projects.dtcheng.FunctionInducer",
      "[Lncsa.d2k.modules.projects.dtcheng.ErrorFunction;",
      "ncsa.d2k.modules.core.datatype.table.ExampleTable",
      "ncsa.d2k.modules.core.datatype.table.ExampleTable",
      "[D",
      "[[D"};
    return types;
  }

  public String getInputInfo(int i)
  {
    switch (i) {
      case 0: return "FunctionInducer";
      case 1: return "ErrorFunctions";
      case 2: return "TrainTestExampleSets";
      case 3: return "Utility";
      default: return "No such input";
    }
  }

  public String getInputName(int i)
  {
    switch(i) {
      case 0:        return "FunctionInducer";
      case 1:        return "ErrorFunctions";
      case 2:        return "TrainTestExampleSets";
      case 3:        return "Utility";
      default: return "NO SUCH INPUT!";
    }
  }

  public String getOutputInfo(int i)
  {
    switch (i) {
      case 0: return "FunctionInducer";
      case 1: return "ErrorFunctions";
      case 2: return "TrainExamples";
      case 3: return "TestExamples";
      case 4: return "Utility";
      case 5: return "Utilities";
      default: return "No such output";
    }
  }

  public String getOutputName(int i)
  {
    switch(i) {
      case 0:        return "FunctionInducer";
      case 1:        return "ErrorFunctions";
      case 2:        return "TrainExamples";
      case 3:        return "TestExamples";
      case 4:        return "Utility";
      case 5:        return "Utilities";
      default: return "NO SUCH OUTPUT!";
    }
  }


  int PhaseIndex;
  int ExampleSetIndex;
  int UtilityIndex;
  boolean InitialExecution;

  public void reset()
  {
    PhaseIndex = 0;
    ExampleSetIndex = 0;
    UtilityIndex = 0;
    TrainExamples = null;
    TestExamples  = null;
  }

  public void beginExecution()
  {
    InitialExecution = true;
    reset();
  }


  public boolean isReady()
  {
    boolean value = false;

    switch (PhaseIndex)
    {
      case 0:
        if (InitialExecution || (!RecycleExamples))
        {
          value = (getFlags()[0] > 0) && (getFlags()[2] > 0);
        }
        else
        {
          value = (getFlags()[0] > 0);
        }
        break;

      case 1:
        value = true;
        break;

      case 2:
        value = (getFlags()[3] > 0);
        break;
    }

    return value;
  }



  Model Model;
  ErrorFunction  [] ErrorFunctions;
  int NumExampleSets;
  ExampleTable [][] TrainTestExampleSets;
  ExampleTable TrainExamples;
  ExampleTable TestExamples;
  double [][] utilityValues;
  double []   utilitySums;

  public void doit() throws Exception
  {

    switch (PhaseIndex)
    {
      case 0:

        Model = (Model) this.pullInput(0);
        ErrorFunctions   = (ErrorFunction []) this.pullInput(1);

        if (InitialExecution || (!RecycleExamples))
        {
          TrainTestExampleSets   = (ExampleTable [][]) this.pullInput(2);
          InitialExecution = false;
        }

        NumExampleSets = TrainTestExampleSets.length;

        PhaseIndex = 1;
        break;

      case 1:

        //while (ExampleSetIndex - UtilityIndex < BatchSize && ExampleSetIndex < NumExampleSets)
        if (ExampleSetIndex - UtilityIndex < BatchSize && ExampleSetIndex < NumExampleSets)
        {
          TrainExamples = TrainTestExampleSets[ExampleSetIndex][0];
          TestExamples  = TrainTestExampleSets[ExampleSetIndex][1];

          this.pushOutput(Model.clone(), 0);
          this.pushOutput(ErrorFunctions, 1);
          this.pushOutput(TrainExamples  , 2);
          this.pushOutput(TestExamples   , 3);

          if (Trace)
            System.out.println("pushing outputs; ExampleSetIndex = " + ExampleSetIndex);

          ExampleSetIndex++;
        }
        else
        {
          PhaseIndex = 2;
        }
        break;

      case 2:
        double [] utilityArray = (double []) this.pullInput(3);

        double [] utilities = utilityArray;
        int    numUtilities = utilities.length;

        if (UtilityIndex == 0)
        {
          utilityValues = new double[NumExampleSets][numUtilities];
          utilitySums   = new double[numUtilities];
        }

        for (int i = 0; i < numUtilities; i++)
        {
          utilityValues[UtilityIndex][i]  = utilities[i];
          utilitySums[i]                 += utilities[i];
        }

        UtilityIndex++;
        if (UtilityIndex == NumExampleSets)
        {
          double [] meanUtilityArray = new double[numUtilities];
          for (int i = 0; i < numUtilities; i++)
          {
            meanUtilityArray[i] = utilitySums[i] / NumExampleSets;
          }

          this.pushOutput(meanUtilityArray, 4);
          this.pushOutput(utilityValues,    5);

          reset();
        }
        else
          PhaseIndex = 1;

        break;
    }


  }
}