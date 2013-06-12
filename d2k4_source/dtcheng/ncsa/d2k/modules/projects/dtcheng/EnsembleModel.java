package ncsa.d2k.modules.projects.dtcheng;

import java.text.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.model.*;
import ncsa.d2k.modules.projects.dtcheng.datatype.*;

public class EnsembleModel
    extends Model
    implements java.io.Serializable {
  int numModels = 0;
  Model[] models;
  int CombinationMethod = 0; // 0 = sum, 1 = average

  public static final int SUM = 0;
  public static final int AVERAGE = 1;

  public EnsembleModel(ExampleTable examples, Model[] models, int NumModels, int CombinationMethod) {
    super(examples);

    this.numModels = NumModels;
    this.CombinationMethod = CombinationMethod;

    Model[] ModelsCopy = new Model[NumModels];
    for (int i = 0; i < NumModels; i++) {
      ModelsCopy[i] = (Model) models[i].clone();
    }
    this.models = ModelsCopy;
  }

  public double[] evaluate(ExampleTable exampleSet, int e) throws Exception {
    double[] outputs;
    double[] combinedOutputs = new double[exampleSet.getNumOutputFeatures()];

    for (int m = 0; m < numModels; m++) {
      outputs = this.models[m].evaluate(exampleSet, e);
      for (int v = 0; v < exampleSet.getNumOutputFeatures(); v++) {
        combinedOutputs[v] += outputs[v];
      }
    }

    if (this.CombinationMethod == this.AVERAGE) {
      for (int v = 0; v < exampleSet.getNumOutputFeatures(); v++) {
        combinedOutputs[v] /= numModels;
      }
    }
    return combinedOutputs;
  }

  public void Evaluate(ExampleTable exampleSet, int e, double[] combinedOutputs) throws Exception {
    double[] outputs;

    for (int v = 0; v < exampleSet.getNumOutputFeatures(); v++) {
      combinedOutputs[v] = 0.0;
    }

    for (int m = 0; m < numModels; m++) {
      outputs = this.models[m].evaluate(exampleSet, e);
      for (int v = 0; v < exampleSet.getNumOutputFeatures(); v++) {
        combinedOutputs[v] += outputs[v];
      }
    }

    if (this.CombinationMethod == this.AVERAGE) {
      for (int v = 0; v < exampleSet.getNumOutputFeatures(); v++) {
        combinedOutputs[v] /= numModels;
      }
    }

  }

  DecimalFormat Format = new DecimalFormat();
  public void print(ModelPrintOptions options) throws Exception {

    Format.setMaximumFractionDigits(options.MaximumFractionDigits);
    System.out.println("Ensemble with " + numModels + " models");

    for (int m = 0; m < numModels; m++) {
      this.models[m].print(options);

      System.out.println();
    }

  }

}
