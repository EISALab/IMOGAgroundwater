package ncsa.d2k.modules.core.prediction.instancebased;
import ncsa.d2k.modules.core.datatype.model.*;
import ncsa.d2k.modules.core.datatype.table.*;

public class InstanceBasedModel extends Model implements java.io.Serializable
  {
  ExampleTable trainExampleSet;
  double [] inputRanges;
  int    NeighborhoodSize;
  double DistanceWeightingPower;
  double ZeroDistanceWeight;


  double [] bestDistances      = null;
  int    [] bestExampleIndices = null;
  double [] outputs = null;



  public InstanceBasedModel(ExampleTable examples,
                            double [] inputRanges,
    int    NeighborhoodSize,
    double DistanceWeightingPower,
    double ZeroDistanceWeight,
    ExampleTable exampleSet) {

    super(examples);

    this.inputRanges            = inputRanges;
    this.NeighborhoodSize       = NeighborhoodSize;
    this.DistanceWeightingPower = DistanceWeightingPower;
    this.ZeroDistanceWeight      = ZeroDistanceWeight;
    this.trainExampleSet        = (ExampleTable) exampleSet.copy();

  }





  public double [] evaluate(ExampleTable testExampleSet, int testE)
    {
    int numExamples = trainExampleSet.getNumRows();
    int numInputs   = trainExampleSet.getNumInputFeatures();
    int numOutputs  = trainExampleSet.getNumOutputFeatures();

    double bestDistance     = Double.POSITIVE_INFINITY;
    int    bestExampleIndex = Integer.MIN_VALUE;

    int actualNeighborhoodSize = NeighborhoodSize;
    if (actualNeighborhoodSize > numExamples)
      {
      actualNeighborhoodSize = numExamples;
      }

    if (bestDistances == null)
      {
      bestDistances      = new double[actualNeighborhoodSize];
      bestExampleIndices = new int   [actualNeighborhoodSize];
      }

    for (int i = 0; i < actualNeighborhoodSize; i++)
      {
      bestDistances     [i] = Double.POSITIVE_INFINITY;
      bestExampleIndices[i] = Integer.MIN_VALUE;
      }

    for (int e = 0; e < numExamples; e++)
      {
      double sumOfSquares = 0.0;
      double difference = Double.NaN;
      for (int f = 0; f < numInputs; f++)
        {
        if (inputRanges[f] != 0.0)
          {
          difference = (trainExampleSet.getInputDouble(e, f) - testExampleSet.getInputDouble(testE, f)) / inputRanges[f];
          sumOfSquares += difference * difference;
          }
        }
      double distance = Math.sqrt(sumOfSquares / numInputs);

      if (distance <= bestDistances[0])
        {
        // insert
        int i = 0;
        while ((distance <= bestDistances[i]) && (i < actualNeighborhoodSize - 1))
          {
          bestDistances     [i] = bestDistances     [i + 1];
          bestExampleIndices[i] = bestExampleIndices[i + 1];
          i++;
          }
        bestDistances     [i] = distance;
        bestExampleIndices[i] = e;
        }
      }

    if (outputs == null)
      outputs = new double[numOutputs];
    else {
      for (int i = 0; i < numOutputs; i++)
        outputs[i] = 0;
    }

    double weightSum = 0.0;
    double weight;
    for (int i = 0; i < actualNeighborhoodSize; i++) {

      double distance = bestDistances[i];

      if (distance == 0) {
        weight = ZeroDistanceWeight;
      }
      else {
        weight = 1.0 / Math.pow(distance, DistanceWeightingPower);
      }

      weightSum += weight;

      for (int f = 0; f < numOutputs; f++) {
        outputs[f] += trainExampleSet.getOutputDouble(bestExampleIndices[i], f) * weight;
        }
      }

    for (int f = 0; f < numOutputs; f++) {
      outputs[f] /= weightSum;
      }

    return outputs;
    }

/*
  public void Instantiate(int numInputs, int numOutputs, String [] inputNames, String [] outputNames,
                          double [] inputRanges,
                          int    NeighborhoodSize,
                          double DistanceWeightingPower,
                          double ZeroDistanceWeight,
                          ExampleTable exampleSet)
    {
    this.numInputs = numInputs;
    this.numOutputs = numOutputs;
    this.inputNames = inputNames;
    this.outputNames = outputNames;
    this.inputRanges            = inputRanges;
    this.NeighborhoodSize       = NeighborhoodSize;
    this.DistanceWeightingPower = DistanceWeightingPower;
    this.ZeroDistanceWeight      = ZeroDistanceWeight;
    this.trainExampleSet        = (ExampleTable) exampleSet.copy();
    }
*/

  public void print(ModelPrintOptions printOptions) throws Exception {
    System.out.println("Instance Based Control Parameters:");
    System.out.println("  NeighborhoodSize       = " + NeighborhoodSize);
    System.out.println("  DistanceWeightingPower = " + DistanceWeightingPower);
    System.out.println("  ZeroDistanceWeight      = " + ZeroDistanceWeight);
    System.out.println("Example Set Attributes");
    System.out.println("  NumExamples            = " + trainExampleSet.getNumRows());
    }


  }
