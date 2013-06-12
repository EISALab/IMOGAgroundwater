package ncsa.d2k.modules.projects.dtcheng.inducers;
import ncsa.d2k.modules.core.prediction.*;
import ncsa.d2k.modules.core.datatype.model.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.core.modules.*;
import java.util.Random;
import ncsa.d2k.modules.projects.dtcheng.*;

public class ClassCentroidInducer extends FunctionInducer
  {
  private int        ErrorFunctionIndex = 0;
  public  void    setErrorFunctionIndex (int value) {       this.ErrorFunctionIndex = value;}
  public  int     getErrorFunctionIndex ()          {return this.ErrorFunctionIndex;}

  private int        NumFeaturesToSelect = 2;
  public  void    setNumFeaturesToSelect (int value) {       this.NumFeaturesToSelect = value;}
  public  int     getNumFeaturesToSelect ()          {return this.NumFeaturesToSelect;}

  private int        NumAttempts = 1000;
  public  void    setNumAttempts (int value) {       this.NumAttempts = value;}
  public  int     getNumAttempts ()          {return this.NumAttempts;}

  private int  RandomSeed = 123;
  public  void setRandomSeed (int value) {       this.RandomSeed = value;}
  public  int  getRandomSeed ()          {return this.RandomSeed;}

  private double        MinClassProbability = 0.001;
  public  void    setMinClassProbability (double value) {       this.MinClassProbability = value;}
  public  double     getMinClassProbability ()          {return this.MinClassProbability;}

  public String getModuleInfo()
    {
		return "ClassCentroidInducer";
	}
  public String getModuleName()
    {
		return "ClassCentroidInducer";
	}

  ErrorFunction errorFunction;
  public Model generateModel(ExampleTable examples, ErrorFunction errorFunction) throws Exception
    {
    Random randomNumberGenerator = new Random(RandomSeed);

    int numClasses = 2;

    int numExamples = examples.getNumRows();
    int numInputs   = examples.getNumInputFeatures();
    int numOutputs  = examples.getNumOutputFeatures();



    double [] inputMins   = new double[numInputs];
    double [] inputMaxs   = new double[numInputs];
    double [] inputRanges = new double[numInputs];

    for (int v = 0; v < numInputs; v++)
      {
      inputMins[v] = Double.POSITIVE_INFINITY;
      inputMaxs[v] = Double.NEGATIVE_INFINITY;
      }


    for (int e = 0; e < numExamples; e++)
      {
      for (int v = 0; v < numInputs; v++)
        {
        double value = examples.getInputDouble(e, v);
        if (value < inputMins[v])
          inputMins[v] = value;
        if (value > inputMaxs[v])
          inputMaxs[v] = value;
        }
      }

    for (int v = 0; v < numInputs; v++)
      {
      inputRanges[v] = inputMaxs[v] - inputMins[v];
      }






    double [][] inputSums  = new double[numClasses][numInputs];
    double [][] meanInputValues  = inputSums;
    int [] classCounts = new int[numClasses];

    // compute class centroids using all features
    for (int e = 0; e < numExamples; e++)
      {
      int classIndex = (int) examples.getOutputDouble(e, 0);  //!!! binary classes (0.0, 1.0) only for now
      classCounts[classIndex]++;

      for (int f = 0; f < numInputs; f++)
        {
        inputSums[classIndex][f] += examples.getInputDouble(e, f);
        }
      }

    for (int classIndex = 0; classIndex < numClasses; classIndex++)
      {
      for (int f = 0; f < numInputs; f++)
        {
        meanInputValues[classIndex][f] = inputSums[classIndex][f] / classCounts[classIndex];  // destroys inputSums
        }
      }
    inputSums = null;


    int numVariantInputs = 0;
    for (int i = 0; i < numInputs; i++)
      {
       if (inputRanges[i] != 0.0)
        {
        numVariantInputs++;
        }
      }

    int actualNumFeaturesToSelect = NumFeaturesToSelect;
    if (NumFeaturesToSelect > numVariantInputs)
      actualNumFeaturesToSelect = numVariantInputs;

    int numSelectedFeatures = actualNumFeaturesToSelect;
    boolean [] selectedFeatures = new boolean[numInputs];
    int     [] selectedFeatureIndices = new int[numSelectedFeatures];

    int    []   classExampleCount = new int[numClasses];
    double [][] outputValueSums = new double[numClasses][numOutputs];
    double [][] meanOutputValues = new double[numClasses][numOutputs];
    // create class centroid model to optimize
    ClassCentroidModel model = new ClassCentroidModel(examples,
        inputRanges, numClasses, numSelectedFeatures, selectedFeatures, meanInputValues, meanOutputValues);

    double [] predictedOutputValues = new double[numOutputs];
    double bestError = Double.MAX_VALUE;
    int    [] bestErrorSelectedFeatureIndices = new int[numSelectedFeatures];
    double [] classDistances = new double[numClasses];
    for (int attemptIndex = 0; attemptIndex < NumAttempts; attemptIndex++)
      {

      // test single features

      int randomIndex;


      if (actualNumFeaturesToSelect == numVariantInputs)
        {
        int count = 0;
        for (int i = 0; i < numInputs; i++)
          {
           if (inputRanges[i] != 0.0)
            {
            selectedFeatures[i] = true;
            selectedFeatureIndices[count] = i;
            count++;
            }
          }
        }
      else
        {
        for (int i = 0; i < actualNumFeaturesToSelect; i++)  // optimize this!
          {
          while (true)
            {
            randomIndex = (int) (randomNumberGenerator.nextDouble() * numInputs);
            if ((!selectedFeatures[randomIndex]) && (inputRanges[randomIndex] != 0.0))
              {
              selectedFeatures[randomIndex] = true;
              selectedFeatureIndices[i] = randomIndex;
              break;
              }
            }
          }
        }


      // compute the mean output values

      for (int classIndex = 0; classIndex < numClasses; classIndex++)
        {
        classExampleCount[classIndex] = 0;
        for (int i = 0; i < numOutputs; i++)
          {
          outputValueSums[classIndex][i] = 0.0;
          }
        }

      for (int e = 0; e < numExamples; e++)
        {

        for (int classIndex = 0; classIndex < numClasses; classIndex++)
          {
          classDistances[classIndex] = 0.0;
          }

        for (int classIndex = 0; classIndex < numClasses; classIndex++)
          {
          for (int f = 0; f < numInputs; f++)  // optimize this!
            {
            if (selectedFeatures[f])
              {
              double difference = (meanInputValues[classIndex][f] - examples.getInputDouble(e, f)) / inputRanges[f];
              double differenceSquared = difference * difference;

              classDistances[classIndex] += differenceSquared;
              }
            }
          }

        int bestClassIndex = -1;
        double bestClassDistance = Double.MAX_VALUE;

        for (int classIndex = 0; classIndex < numClasses; classIndex++)
          {
          if (classDistances[classIndex] < bestClassDistance)
            {
            bestClassIndex    = classIndex;
            bestClassDistance = classDistances[classIndex];
            }
          }

        for (int i = 0; i < numOutputs; i++)
          {
          outputValueSums[bestClassIndex][i] +=  examples.getOutputDouble(e, i);
          classExampleCount[bestClassIndex]++;
          }
        }

      if (false)
        for (int classIndex = 0; classIndex < numClasses; classIndex++)
          {
          System.out.println("classExampleCount[" + classIndex + "] = " + classExampleCount[classIndex]);
          }
      // compute means from sums and counts
      for (int classIndex = 0; classIndex < numClasses; classIndex++)
        {
        for (int i = 0; i < numOutputs; i++)
          {
          meanOutputValues[classIndex][i] = outputValueSums[classIndex][i] / classExampleCount[classIndex];
          }
        }


      // measure the error of the model

      if (errorFunction == null)
        errorFunction = new ErrorFunction(ErrorFunctionIndex);

      double error = 0.0;  // change later : errorFunction.evaluate(examples, model);
      //System.out.println("  error = " + error);


      if (error < bestError)
        {
        bestError = error;
        for (int i = 0; i < actualNumFeaturesToSelect; i++)
          {
          bestErrorSelectedFeatureIndices[i] = selectedFeatureIndices[i];
          }

        }


      // unset feature

      for (int i = 0; i < actualNumFeaturesToSelect; i++)
        {
        model.selectedFeatures[selectedFeatureIndices[i]] = false;
        }

      }


    for (int i = 0; i < actualNumFeaturesToSelect; i++)
      {
      System.out.println("bestErrorSelectedFeatureIndex = " + bestErrorSelectedFeatureIndices[i]);
      model.selectedFeatures[bestErrorSelectedFeatureIndices[i]] = true;
      }
      model.print(null);

      System.out.println();

    System.out.println("bestError = " + bestError);

    return (Model) model;
    }

  public void instantiateBias(double [] bias) throws Exception
    {
    int biasIndex = 0;
    NumFeaturesToSelect = (int) bias[biasIndex++];
    NumAttempts         = (int) bias[biasIndex++];
    RandomSeed          = (int) bias[biasIndex++];
    MinClassProbability = (int) bias[biasIndex++];
    ErrorFunctionIndex  = (int) bias[biasIndex++];
    if (biasIndex != bias.length) {
      System.out.println("biasIndex != numBiases");
      throw new Exception();
    }

    }



	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "input0";
			case 1:
				return "input1";
			default: return "NO SUCH INPUT!";
		}
	}

	/**
	 * Return the human readable name of the indexed output.
	 * @param index the index of the output.
	 * @return the human readable name of the indexed output.
	 */
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "output0";
			default: return "NO SUCH OUTPUT!";
		}
	}
  }
