package ncsa.d2k.modules.projects.dtcheng;

import ncsa.d2k.core.modules.ComputeModule;
import java.util.Random;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.projects.dtcheng.obsolete.*;
public class CreateNFoldExampleSets extends ComputeModule
  {

  private int  NumRepetitions = 10;
  public  void setRepetitions (int value) {       this.NumRepetitions = value;}
  public  int  getRepetitions ()          {return this.NumRepetitions;}

  private int  NumFolds = 10;
  public  void setNumFolds (int value) {       this.NumFolds = value;}
  public  int  getNumFolds ()          {return this.NumFolds;}

  private int  RandomSeed = 123;
  public  void setRandomSeed (int value) {       this.RandomSeed = value;}
  public  int  getRandomSeed ()          {return this.RandomSeed;}

  public String getModuleName()
    {
    return "CreateNFoldExampleSets";
    }
  public String getModuleInfo()
    {
    return "This module creates a series of training and testing examples sets from     the input example set. The cross validation methodology is used to create     the set of example set pairs. The output is a 2D array of example sets.     The first index is for the fold, the second is 0 or 1, 0 for training and     1 for testing. NumFolds controls the number of training and testing sets     to form. RandomSeed is used to intialize the random number generator used to randomly partition the examples.";
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
    String[] types = {"[[Lncsa.d2k.modules.core.datatype.table.ExampleTable;"};
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
      case 0: return "A 2D array of example sets of the form GeneralExampleSet[fold][type]";
      default: return "No such output";
    }
  }


   public void doit()
    {


    ExampleTable examples = (ExampleTable) this.pullInput(0);

    int numExamples = examples.getNumRows();
    int numInputs = examples.getNumInputFeatures();
    int numOutputs = examples.getNumOutputFeatures();


    Random randomNumberGenerator = new Random(RandomSeed);

    // compute actual number of folds
    int actualNumFolds = NumFolds;
    if (numExamples < NumFolds)
      {
      actualNumFolds = numExamples;
      }


    double fractionalNumExamplesPerGroup = (double) numExamples / (double) actualNumFolds;



    int numExampleSets = NumRepetitions * actualNumFolds;

    ExampleTable [][] exampleSets = new ExampleTable[numExampleSets][2];


    for (int r = 0; r < NumRepetitions; r++)
      {
      // create random ordering of examples
      int [] randomizedIndices = new int[numExamples];
      for (int e = 0; e < numExamples; e++)
        randomizedIndices[e] = e;

      RandomMethods.randomizeIntArray(randomNumberGenerator, randomizedIndices, numExamples);

      for (int g = 0; g < actualNumFolds; g++)
        {
        int testExamplesStart = (int) Math.round(g      * fractionalNumExamplesPerGroup);
        int testExamplesEnd   = (int) Math.round((g + 1) * fractionalNumExamplesPerGroup);

        int numTestExamples  = testExamplesEnd - testExamplesStart;
        int numTrainExamples = numExamples - numTestExamples;

        // Changed back to allocateExamplePointers rather than allocateSpace because
	// otherwise the Train and Test sets are created with the same numExamples
	// as the original train/test set combined.   That leads to trouble down the
 	// line.   allocateExamplePointers was used in previous versions -- not sure why
	// it was changed in this one.	-ruth

        //!!!ExampleTable foldTrainExamples = examples.shallowCopy();
        //foldTrainExamples.allocateSpace(numExamples, numInputs, numOutputs);
        //!!!foldTrainExamples.allocateExamplePointers(numTrainExamples);

        //!!!ExampleTable foldTestExamples  = examples.shallowCopy();
        //foldTestExamples.allocateSpace(numExamples, numInputs, numOutputs);
        //!!!foldTestExamples.allocateExamplePointers(numTestExamples);

        int trainExampleIndex = 0;
        int testExampleIndex  = 0;

        for (int e = 0; e < numExamples; e++)
          {
          if ((e >= testExamplesStart) && (e < testExamplesEnd))
            {
            //!!!foldTestExamples.setExample(testExampleIndex++, examples, randomizedIndices[e]);
            //foldTestExamples [testExampleIndex++  ] = examples[randomizedIndices[e]];
            }
          else
            {
            //!!!foldTrainExamples.setExample(trainExampleIndex++, examples, randomizedIndices[e]);
            //foldTrainExamples[trainExampleIndex++] = examples[randomizedIndices[e]];
            }
          }

        //!!!exampleSets[r * actualNumFolds + g][0] = foldTrainExamples;
        //!!!exampleSets[r * actualNumFolds + g][1] = foldTestExamples;
        }
      }

    ////////////////////////
    ///   PUSH OUTPUTS   ///
    ////////////////////////

    this.pushOutput(exampleSets, 0);
    }


  }
