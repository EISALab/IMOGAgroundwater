package ncsa.d2k.modules.projects.dtcheng.inducers;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.model.*;
public class NaiveBayesModel extends Model implements java.io.Serializable
  {
  int     numSelectedInputs;
  int     [] selectedIndices;
  int     numExamples;
  int     numPositiveExamples;
  int     [][][][] counts;

  public NaiveBayesModel(ExampleTable examples, boolean [] selectedInputs, int [][][][] counts, int numExamples) {
    super(examples);


    int    numSelectedInputs = 0;
    int [] selectedInputIndices = new int[getNumInputs()];

    for (int i = 0; i < getNumInputs(); i++) {
      if (selectedInputs[i] == true)
      {
      selectedInputIndices[numSelectedInputs] = i;
      numSelectedInputs++;
    }
    }




    this.numExamples          = numExamples;
    this.numPositiveExamples  = numPositiveExamples;
    this.numSelectedInputs    = numSelectedInputs;
    this.selectedIndices      = selectedInputIndices;
    this.counts               = (int [][][][]) counts.clone();

  }

  public double [] Evaluate(ExampleTable examples, int e)
    {
    double [] outputs = new double[examples.getNumOutputFeatures()];
    for (int o = 0; o < counts.length; o++)
      {
      double posLogProbSum = 0.0;
      double negLogProbSum = 0.0;
      double logOddsRatioSum = 0.0;
      double probabilitySum = 0.0;
      for (int i = 0; i < numSelectedInputs; i++)
        {
        int input = (int) examples.getInputDouble(e, i);

        double negativeInputCount = counts[o][selectedIndices[i]][input][0];
        double positiveInputCount = counts[o][selectedIndices[i]][input][1];

        if (negativeInputCount == 0.0)
          negativeInputCount = 1.0 / numExamples;
        if (positiveInputCount == 0.0)
          positiveInputCount = 1.0 / numExamples;

        double logOddsRatio = Math.log(positiveInputCount / negativeInputCount);

        double positiveProbability = positiveInputCount / (positiveInputCount + negativeInputCount);

        probabilitySum += positiveProbability;

        logOddsRatioSum += logOddsRatio;

        //negLogProbSum += Math.log(negativeInputCount / numExamples);
        //posLogProbSum += Math.log(positiveInputCount / numExamples);
        }

      //outputs[o] = Math.exp(posLogProbSum) / (Math.exp(posLogProbSum) + Math.exp(negLogProbSum));
      //outputs[o] = posLogProbSum - negLogProbSum;

      outputs[o] =  Math.exp(logOddsRatioSum / numSelectedInputs) / (1.0 + Math.exp(logOddsRatioSum / numSelectedInputs));
      //outputs[o] = probabilitySum / numSelectedInputs;
      }
    return outputs;
    }
/*
  public void Instantiate(int numInputs, int numOutputs, String [] inputNames, String [] outputNames,
                          boolean [] selectedInputs, int [][][][] counts, int numExamples)
    {

    int    numSelectedInputs = 0;
    int [] selectedInputIndices = new int[numInputs];

    for (int i = 0; i < numInputs; i++)
      {
      if (selectedInputs[i] == true)
        {
        selectedInputIndices[numSelectedInputs] = i;
        numSelectedInputs++;
        }
      }


    this.numExamples          = numExamples;
    this.numPositiveExamples  = numPositiveExamples;
    this.numInputs            = numInputs;
    this.numOutputs           = numOutputs;
    this.inputNames           = inputNames;
    this.outputNames          = outputNames;
    this.numSelectedInputs    = numSelectedInputs;
    this.selectedIndices      = selectedInputIndices;
    this.counts               = (int [][][][]) counts.clone();
    }
*/
  public void print(ModelPrintOptions printOptions) throws Exception
    {
    System.out.println("NaiveBayes Model");
    for (int o = 0; o < counts.length; o++) {
      System.out.println(this.getOutputName(o) + " = " );
      for (int i = 0; i < numSelectedInputs; i++)
        {
        System.out.println(counts[o][selectedIndices[i]][1][1] + " * " + getInputFeatureName(selectedIndices[i]) + " + ");
        }
      }
    }


  }
