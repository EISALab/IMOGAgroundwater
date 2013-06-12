package ncsa.d2k.modules.projects.dtcheng.transformations;


import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.core.modules.ComputeModule;
import java.util.Random;
import ncsa.d2k.modules.projects.dtcheng.obsolete.*;

public class SampleExampleSet extends ComputeModule
{

  private int  NumExamplesToSample = 10;
  public  void setNumExamplesToSample (int value) {       this.NumExamplesToSample = value;}
  public  int  getNumExamplesToSample ()          {return this.NumExamplesToSample;}

  private boolean  RandomizeOrder = true;
  public  void setRandomizeOrder (boolean value) {       this.RandomizeOrder = value;}
  public  boolean  getRandomizeOrder ()          {return this.RandomizeOrder;}

  private int  RandomSeed = 123;
  public  void setRandomSeed (int value) {       this.RandomSeed = value;}
  public  int  getRandomSeed ()          {return this.RandomSeed;}

  public String getModuleInfo()
  {
    return "SampleExampleSet";
  }
  public String getModuleName()
  {
    return "SampleExampleSet";
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
    switch (i)
    {
      case 0: return "ncsa.d2k.modules.core.datatype.table.ExampleTable";
    }
    return "";
  }

  public String getOutputInfo(int i)
  {
    switch (i) {
      case 0: return "ncsa.d2k.modules.core.datatype.table.ExampleTable";
      default: return "No such output";
    }
  }

  public String getOutputName(int i)
  {
    switch (i)
    {
      case 0: return "ncsa.d2k.modules.core.datatype.table.ExampleTable";
    }
    return "";
  }


  ////////////////
  ///   WORK   ///
  ////////////////
  public void doit()
  {

    ///////////////////////
    ///   PULL INPUTS   ///
    ///////////////////////

    ExampleTable examples = (ExampleTable) this.pullInput(0);

    int numExamples = examples.getNumRows();
    int numInputs = examples.getNumInputFeatures();
    int numOutputs = examples.getNumOutputFeatures();

    Random randomNumberGenerator = new Random(RandomSeed);

    int actualNumExamplesToSample = NumExamplesToSample;
    if (numExamples < NumExamplesToSample)
    {
      actualNumExamplesToSample = numExamples;
    }

    // create random ordering of examples
    int [] randomizedIndices = new int[numExamples];
    for (int e = 0; e < numExamples; e++)
      randomizedIndices[e] = e;

    if (RandomizeOrder)
      RandomMethods.randomizeIntArray(randomNumberGenerator, randomizedIndices);


    //!!!ExampleTable sampledExamples = examples.shallowCopy();
    //!!!sampledExamples.allocateExamplePointers(NumExamplesToSample);

    int trainExampleIndex = 0;
    int testExampleIndex  = 0;

    for (int e = 0; e < actualNumExamplesToSample; e++)
    {
      //!!!sampledExamples.setExample(e, examples, randomizedIndices[e]);
    }


    ////////////////////////
    ///   PUSH OUTPUTS   ///
    ////////////////////////

    //!!!this.pushOutput(sampledExamples, 0);
  }
}