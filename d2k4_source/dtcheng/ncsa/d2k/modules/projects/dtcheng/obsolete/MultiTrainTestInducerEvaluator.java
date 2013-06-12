package ncsa.d2k.modules.projects.dtcheng.obsolete;
import ncsa.d2k.modules.core.datatype.model.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.prediction.*;
import ncsa.d2k.core.modules.*;
import java.util.Random;
import ncsa.d2k.modules.projects.dtcheng.primitive.*;

public class MultiTrainTestInducerEvaluator extends ComputeModule
{
  private int  NumRepetitions = 10;
  public  void setNumRepetitions (int value) {       this.NumRepetitions = value;}
  public  int  getNumRepetitions ()          {return this.NumRepetitions;}

  private int  NumTrainExamples = -1;
  public  void setNumTrainExamples (int value) {       this.NumTrainExamples = value;}
  public  int  getNumTrainExamples ()          {return this.NumTrainExamples;}

  private int  NumTestExamples = 999999999;
  public  void setNumTestExamples (int value) {       this.NumTestExamples = value;}
  public  int  getNumTestExamples ()          {return this.NumTestExamples;}

  private long  RandomSeed = 123;
  public  void setRandomSeed (long value) {       this.RandomSeed = value;}
  public  long  getRandomSeed ()          {return this.RandomSeed;}



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
    return "MultiTrainTestInducerEvaluator";
  }
  public String getModuleName()
  {
    return "MultiTrainTestInducerEvaluator";
  }

  public String getInputName(int i)
  {
    switch(i)
    {
      case  0: return "FunctionInducer";
      case  1: return "ErrorFunctions";
      case  2: return "ExampleSet";
      case  3: return "ModelFunctionUtility";
      default: return "NO SUCH INPUT!";
    }
  }

  public String getInputInfo(int i)
  {
    switch (i)
    {
      case  0: return "FunctionInducer";
      case  1: return "ErrorFunctions";
      case  2: return "ExampleSet";
      case  3: return "Utility";
      default: return "No such input";
    }
  }

  public String[] getInputTypes()
  {
    String[] types =
    {
      "ncsa.d2k.modules.projects.dtcheng.FunctionInducer",
      "[Lncsa.d2k.modules.projects.dtcheng.ErrorFunction;",
      "ncsa.d2k.modules.projects.dtcheng.ExampleTable;",
      "[D"
    };
    return types;
  }


  public String getOutputName(int i)
  {
    switch(i) {
      case  0: return "FunctionInducer";
      case  1: return "ErrorFunctions";
      case  2: return "TrainExamples";
      case  3: return "TestExamples";
      case  4: return "MeanUtility";
      case  5: return "AllUtilities";
      default: return "NO SUCH OUTPUT!";
    }
  }

  public String getOutputInfo(int i)
  {
    switch (i) {
      case 0: return "FunctionInducer";
      case 1: return "ErrorFunctions";
      case 2: return "TrainExamples";
      case 3: return "TestExamples";
      case 4: return "MeanUtility";
      case 5: return "AllUtilities";
      default: return "No such output";
    }
  }

  public String[] getOutputTypes()
  {
    String[] types =
    {
      "ncsa.d2k.modules.projects.dtcheng.FunctionInducer",
      "[Lncsa.d2k.modules.projects.dtcheng.ErrorFunction;",
      "ncsa.d2k.modules.projects.dtcheng.ExampleTable",
      "ncsa.d2k.modules.projects.dtcheng.ExampleTable",
      "[D",
      "[[D"
    };
    return types;
  }

  /*****************************************************************************/
  /* This function returns a random integer between min and max (both          */
  /* inclusive).                                                               */
  /*****************************************************************************/

    int randomInt(int min, int max)
    {
      return (int) ((RandomNumberGenerator.nextDouble() * (max - min + 1)) + min);
    }

    void randomizeIntArray(int [] data, int numElements) throws Exception
    {
      int temp, rand_index;

      for (int i = 0; i < numElements - 1; i++)
      {
        rand_index       = randomInt(i + 1, numElements - 1);
        temp             = data[i];
        data[i]          = data[rand_index];
        data[rand_index] = temp;
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
  ErrorFunction   [] errorFunctions = null;
  ExampleTable ExampleSet;
  int numExamples;
  int numInputs;
  int numOutputs;
  double [][] UtilityValues;
  double []   UtilitySums;

  Random RandomNumberGenerator;
  int [] RandomizedIndices = null;

  public void doit() throws Exception
  {

    switch (PhaseIndex)
    {
      case 0:

        Model = (Model) this.pullInput(0);

        if (InitialExecution)
          errorFunctions = (ErrorFunction []) this.pullInput(1);

        if (InitialExecution || (!RecycleExamples))
        {
          RandomNumberGenerator = new Random(RandomSeed);
          ExampleSet   = (ExampleTable) this.pullInput(2);

          ///////////////////////
          ///   PULL INPUTS   ///
          ///////////////////////

          numExamples = ExampleSet.getNumRows();
          numInputs = ExampleSet.getNumInputFeatures();
          numOutputs = ExampleSet.getNumOutputFeatures();

          if (NumTrainExamples == -1)
          {
            NumTrainExamples = numExamples - NumTestExamples;
          }

          if (NumTestExamples == -1)
          {
            NumTestExamples = numExamples - NumTrainExamples;
          }

          if (NumTrainExamples + 1 > numExamples)
          {
            System.out.println("NumTrainExamples + 1 > numExamples");
            throw new Exception();
          }

          if (NumTrainExamples + NumTestExamples > numExamples)
          {
            NumTestExamples = numExamples - NumTrainExamples;
          }


          if ((RandomizedIndices == null) || (RandomizedIndices.length < numExamples)) {
            RandomizedIndices = new int[numExamples];
          }

          for (int e = 0; e < numExamples; e++)
            RandomizedIndices[e] = e;


          InitialExecution = false;
        }

        PhaseIndex = 1;
        break;

      case 1:

        if (ExampleSetIndex - UtilityIndex < BatchSize && ExampleSetIndex < NumRepetitions)
        {




          randomizeIntArray(RandomizedIndices, numExamples);

          //!!!ExampleTable currentTrainExampleSet = ExampleSet.shallowCopy();
          //!!!ExampleTable currentTestExampleSet  = ExampleSet.shallowCopy();

          //!!!currentTrainExampleSet.allocateExamplePointers(NumTrainExamples);
          //!!!currentTestExampleSet.allocateExamplePointers(NumTestExamples);
          for (int e = 0; e < NumTrainExamples; e++)
          {
            //!!!currentTrainExampleSet.setExample(e, ExampleSet, RandomizedIndices[e]);
          }
          for (int e = 0; e < NumTestExamples; e++)
          {
            //!!!currentTestExampleSet.setExample(e, ExampleSet, RandomizedIndices[e + NumTrainExamples]);
          }


          this.pushOutput(Model.clone(), 0);
          this.pushOutput(errorFunctions.clone(),  1);
          //!!!this.pushOutput(currentTrainExampleSet,  2);
          //!!!this.pushOutput(currentTestExampleSet,   3);

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
          UtilityValues = new double[NumRepetitions][numUtilities];
          UtilitySums   = new double[numUtilities];
        }

        for (int i = 0; i < numUtilities; i++)
        {
          UtilityValues[UtilityIndex][i]  = utilities[i];
          UtilitySums[i]                 += utilities[i];
        }

        UtilityIndex++;
        if (UtilityIndex == NumRepetitions)
        {
          double [] meanUtilityArray = new double[numUtilities];
          for (int i = 0; i < numUtilities; i++)
          {
            meanUtilityArray[i] = UtilitySums[i] / NumRepetitions;
          }

          this.pushOutput(meanUtilityArray, 4);
          this.pushOutput(UtilityValues,    5);

          reset();
        }
        else
        {
          PhaseIndex = 1;
        }
        break;
    }


  }
}
