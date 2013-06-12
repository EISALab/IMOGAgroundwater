package ncsa.d2k.modules.core.prediction.mean.continuous;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.model.*;
import java.text.*;


public class MeanOutputModel extends Model implements java.io.Serializable
  {
  double [] meanOutputValues;

  public MeanOutputModel(ExampleTable examples, double [] meanOutputValues) {
    super(examples);
    this.meanOutputValues = meanOutputValues;
  }

  public MeanOutputModel(int trainingSetSize, String[] inputColumnLabels, String[] outputColumnLabels,
                   int[] inputFeatureTypes, int[] outputFeatureTypes, double [] meanOutputValues) {
    super(trainingSetSize, inputColumnLabels, outputColumnLabels, inputFeatureTypes, outputFeatureTypes);
    this.meanOutputValues = meanOutputValues;
  }

  // this is a dummy input since mean model does not have any; added only for consistancy
  public double [] Evaluate(double [] inputs) {

    double [] outputs = new double[meanOutputValues.length];
    for (int f = 0; f < meanOutputValues.length; f++) {
      outputs[f] = meanOutputValues[f];
      }
    return outputs;
    }

  public double [] evaluate(ExampleTable exampleSet, int e) {
    double [] outputs = new double[exampleSet.getNumOutputFeatures()];
    for (int f = 0; f < meanOutputValues.length; f++) {
      outputs[f] = meanOutputValues[f];
      }
    return outputs;
    }

  public void evaluate(ExampleTable exampleSet, int e, double [] outputs) {
    for (int f = 0; f < meanOutputValues.length; f++) {
      outputs[f] = meanOutputValues[f];
      }
    }

/*
  public void Instantiate(int numInputs, int numOutputs, String [] inputNames, String [] outputNames, double [] meanOutputValues) {
    this.numInputs = numInputs;
    this.numOutputs = numOutputs;
    this.inputNames = inputNames;
    this.outputNames = outputNames;
    this.meanOutputValues = (double []) meanOutputValues.clone();
    }
*/

  DecimalFormat Format = new DecimalFormat();
  public void print(ModelPrintOptions options) {

    Format.setMaximumFractionDigits(options.MaximumFractionDigits);

    for (int i = 0; i < getNumOutputFeatures(); i++) {
      if (i > 0)
        System.out.print("  ");
      System.out.print(this.getOutputFeatureName(i) + " = " + Format.format(this.meanOutputValues[i]));
    }
    //System.out.println();
  }

  }
