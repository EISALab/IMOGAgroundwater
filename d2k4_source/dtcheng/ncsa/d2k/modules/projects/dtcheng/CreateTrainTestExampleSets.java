package ncsa.d2k.modules.projects.dtcheng;


import ncsa.d2k.core.modules.ComputeModule;
import java.util.Random;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.projects.dtcheng.primitive.*;
public class CreateTrainTestExampleSets extends ComputeModule
{

  private int  NumSets = 10;
  public  void setNumSets (int value) {       this.NumSets = value;}
  public  int  getNumSets ()          {return this.NumSets;}

  private int  NumTrainExamples = -1;
  public  void setNumTrainExamples (int value) {       this.NumTrainExamples = value;}
  public  int  getNumTrainExamples ()          {return this.NumTrainExamples;}

  private int  NumTestExamples = 999999999;
  public  void setNumTestExamples (int value) {       this.NumTestExamples = value;}
  public  int  getNumTestExamples ()          {return this.NumTestExamples;}

  private int  RandomSeed = 123;
  public  void setRandomSeed (int value) {       this.RandomSeed = value;}
  public  int  getRandomSeed ()          {return this.RandomSeed;}

  public String getModuleName()
  {
    return "CreateTrainTestExampleSets";
  }
  public String getModuleInfo()
  {
    return "This module creates a series of training and testing examples sets from     the input example set. The output is a 2D array of example sets.     NumTrainExamples controls the number of training examples in each train     set. NumTestExamples controls the number of training examples in each     train set. NumSets controls the number of training and testing sets to     form. RandomSeed is used to intialize the random number generator used to     randomly partition the examples.";
  }

  public String getInputName(int i)
  {
    switch (i)
    {
      case 0: return "InputExampleSet";
    }
    return "";
  }
  public String[] getInputTypes()
  {
    String[] types = {"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
    return types;
  }
  public String getInputInfo(int i)
  {
    switch (i) {
      case 0: return "An example set from which the training and testing example sets will be drawn.  ";
      default: return "No such input";
    }
  }

  public String[] getOutputTypes()
  {
    String[] types = {"[[Lncsa.d2k.modules.projects.dtcheng.ExampleSet;"};
    return types;
  }
  public String getOutputName(int i)
  {
    switch (i)
    {
      case 0: return "OutputExampleSetArray";
    }
    return "";
  }
  public String getOutputInfo(int i)
  {
    switch (i) {
      case 0: return "A 2D array of example sets of the form ExampleSet[fold][type]";
      default: return "No such output";
    }
  }


/*****************************************************************************/
/* This function returns a random integer between min and max (both          */
/* inclusive).                                                               */
/*****************************************************************************/

  int randomInt(int min, int max)
  {
    return (int) ((randomNumberGenerator.nextDouble() * (max - min + 1)) + min);
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


  Random randomNumberGenerator;

  ////////////////
  ///   WORK   ///
  ////////////////
  public void doit() throws Exception
  {


    ///////////////////////
    ///   PULL INPUTS   ///
    ///////////////////////

    ExampleTable examples = (ExampleTable) this.pullInput(0);

    int numExamples = examples.getNumRows();
    int numInputs = examples.getNumInputFeatures();
    int numOutputs = examples.getNumOutputFeatures();

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

    int actualNumTestExamples = NumTestExamples;
    if (NumTrainExamples + NumTestExamples > numExamples)
    {
      actualNumTestExamples = numExamples - NumTrainExamples;
      //System.out.println("Warning!  Using " + actualNumTestExamples + " test examples not " +
      //NumTestExamples + " as requested");
    }

    randomNumberGenerator = new Random(RandomSeed);


    ExampleTable [][] exampleSets = new ExampleTable[NumSets][2];

    for (int g = 0; g < NumSets; g++)
    {
      int [] randomizedIndices = new int[numExamples];
      for (int e = 0; e < numExamples; e++)
        randomizedIndices[e] = e;
      randomizeIntArray(randomizedIndices, numExamples);

      //!!!ExampleTable currentTrainExampleSet = examples.shallowCopy();
      //!!!ExampleTable currentTestExampleSet  = examples.shallowCopy();

      //!!!currentTrainExampleSet.allocateSpace(numExamples, numInputs, numOutputs);
      //!!!currentTestExampleSet.allocateSpace(numExamples, numInputs, numOutputs);


      for (int e = 0; e < NumTrainExamples; e++)
      {
        //!!!currentTrainExampleSet.setExample(e, examples, randomizedIndices[e]);
      }
      for (int e = 0; e < actualNumTestExamples; e++)
      {
        //!!!currentTestExampleSet.setExample(e, examples, randomizedIndices[e + NumTrainExamples]);
      }

      //!!!exampleSets[g][0] = currentTrainExampleSet;
      //!!!exampleSets[g][1] = currentTestExampleSet;
    }


    ////////////////////////
    ///   PUSH OUTPUTS   ///
    ////////////////////////

    this.pushOutput(exampleSets, 0);
  }
}
