package ncsa.d2k.modules.projects.dtcheng.datatype;
import java.text.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.model.*;

public class CombinedModel extends Model implements java.io.Serializable
  {
  int   numModels = 0;
  Model [] models;

  public CombinedModel(ExampleTable examples, Model model1, Model model2) {
    super(examples);

    this.numModels = 2;
    this.models = new Model[2];
    this.models[0] = model1;
    this.models[1] = model2;

  }

  public CombinedModel(ExampleTable examples, Model model1, Model model2, Model model3) {
    super(examples);

    this.numModels = 3;
    this.models = new Model[3];
    this.models[0] = model1;
    this.models[1] = model2;
    this.models[2] = model3;

    }

  /*
  public double [] Evaluate(double [] inputs) throws Exception {

    double [] outputs;
    double [] combinedOutputs  = new double[this.numOutputs];

    for (int m = 0; m < numModels; m++) {
      outputs = this.models[m].Evaluate(inputs);
      for (int v = 0; v < this.numOutputs; v++) {
        combinedOutputs[v] += outputs[v];
        }
      }

    for (int v = 0; v < this.numOutputs; v++) {
      combinedOutputs[v] /= numModels;
      }

    return combinedOutputs;
    }
*/
  public double [] Evaluate(ExampleTable exampleSet, int e) throws Exception {
    double [] outputs;
    double [] combinedOutputs  = new double[getNumOutputs()];

    for (int m = 0; m < numModels; m++) {
      outputs = this.models[m].evaluate(exampleSet, e);
      for (int v = 0; v < exampleSet.getNumOutputFeatures(); v++) {
        combinedOutputs[v] += outputs[v];
        }
      }

    for (int v = 0; v < exampleSet.getNumOutputFeatures(); v++) {
      combinedOutputs[v] /= numModels;
      }

    return combinedOutputs;
    }

  public void Evaluate(ExampleTable exampleSet, int e, double [] combinedOutputs) throws Exception
    {
    double [] outputs;

    for (int v = 0; v < exampleSet.getNumOutputFeatures(); v++) {
      combinedOutputs[v] = 0.0;
      }

    for (int m = 0; m < numModels; m++) {
      outputs = this.models[m].evaluate(exampleSet, e);
      for (int v = 0; v < exampleSet.getNumOutputFeatures(); v++) {
        combinedOutputs[v] += outputs[v];
        }
      }

    for (int v = 0; v < exampleSet.getNumOutputFeatures(); v++) {
      combinedOutputs[v] /= numModels;
      }

    }

  DecimalFormat Format = new DecimalFormat();
  public void print(ModelPrintOptions options) throws Exception {

    Format.setMaximumFractionDigits(options.MaximumFractionDigits);

    for (int m = 0; m < numModels; m++) {
      this.models[m].print(options);

      System.out.println();
    }

    }

  }
