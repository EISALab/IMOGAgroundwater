package ncsa.d2k.modules.projects.dtcheng.inducers;
import ncsa.d2k.modules.core.datatype.model.*;
import ncsa.d2k.modules.core.datatype.table.*;
public class ClassCentroidModel extends Model implements java.io.Serializable
  {
  int     numClasses = 2;
  double  [] inputRanges;   //[inputIndex]
  double  [][] meanInputValues;   //[classIndex][inputIndex]
  double  [][] meanOutputValues;   //[classIndex][inputIndex]
  int          numSelectedFeatures;
  boolean []   selectedFeatures;

  public ClassCentroidModel(ExampleTable examples,
                            double [] inputRanges,
                            int numClasses,
                            int numSelectedFeatures,
                            boolean selectedFeatures[],
                            double [][] meanInputValues,
                            double [][] meanOutputValues) {
    super(examples);
    this.inputRanges = inputRanges;
    this.numClasses = numClasses;
    this.numSelectedFeatures = numSelectedFeatures;
    this.selectedFeatures = selectedFeatures;
    this.meanInputValues  = meanInputValues;
    this.meanOutputValues = meanOutputValues;

  }

  public double [] Evaluate(double [] inputs)
    {
    double [] outputs = new double[1]; //!!! binary classes only

    Evaluate(inputs, outputs);

    return outputs;
    }

  public double [] Evaluate(ExampleTable exampleSet, int e)
    {
    double [] inputs = new double[exampleSet.getNumInputFeatures()];
    for (int f = 0; f < exampleSet.getNumInputFeatures(); f++)
      {
      inputs[f] = exampleSet.getInputDouble(e, f);
      }

    double [] outputs = Evaluate(inputs);
    return outputs;
    }

  public void Evaluate(ExampleTable exampleSet, int e, double [] outputs)
    {
    double [] inputs = new double[exampleSet.getNumInputFeatures()];
    for (int f = 0; f < exampleSet.getNumInputFeatures(); f++)
      {
      inputs[f] = exampleSet.getInputDouble(e, f);
      }

    Evaluate(inputs, outputs);
    }

  public double [] Evaluate(double [] inputs, double [] outputs)
    {
    double [] classDistances = new double[numClasses];  // optimize this!

    for (int classIndex = 0; classIndex < numClasses; classIndex++)
      {
      for (int f = 0; f < getNumInputs(); f++)
        {
        if (selectedFeatures[f])
          {
          double difference = (meanInputValues[classIndex][f] - inputs[f]) / inputRanges[f];
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

    outputs[0] = meanOutputValues[bestClassIndex][0];

    return outputs;
    }

/*
  public void Instantiate(int numInputs, int numOutputs, String [] inputNames, String [] outputNames,
                          double [] inputRanges,
                          int numClasses, int numSelectedFeatures, boolean selectedFeatures[],
                          double [][] meanInputValues, double [][] meanOutputValues)
    {
    this.numInputs = numInputs;
    this.numOutputs = numOutputs;
    this.inputNames = inputNames;
    this.outputNames = outputNames;
    this.inputRanges = inputRanges;
    this.numClasses = numClasses;
    this.numSelectedFeatures = numSelectedFeatures;
    this.selectedFeatures = selectedFeatures;
    this.meanInputValues  = meanInputValues;
    this.meanOutputValues = meanOutputValues;
    }
*/

  public void print(ModelPrintOptions options)
    {
    System.out.println("numClasses = " + numClasses);
    for (int classIndex = 0; classIndex < numClasses; classIndex++)
      {
      System.out.println("Class Number = " + (classIndex + 1));
      System.out.println("Input Centroid = ");
      for (int f = 0; f < getNumInputs(); f++)
        {
        if (selectedFeatures[f])
          {
          System.out.println(getInputFeatureName(f) + " = " + meanInputValues[classIndex][f]);
          }
        }
      System.out.println("Output Means = ");
      for (int f = 0; f < meanOutputValues.length; f++)
        {
        System.out.println(getOutputName(f) + " = " + meanOutputValues[classIndex][f]);
        }
      }
    }

  }
