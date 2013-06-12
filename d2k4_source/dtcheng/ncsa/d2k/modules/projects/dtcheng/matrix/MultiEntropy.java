package ncsa.d2k.modules.projects.dtcheng.matrix;


import ncsa.d2k.modules.projects.dtcheng.*;

import ncsa.d2k.core.modules.*;


public class MultiEntropy extends ComputeModule {

  public String getModuleName() {
    return "MultiEntropy";
  }


  public String getModuleInfo() {
    return "This module calculates the cross-entropy error function " +
        "for multiple category data. It is intended for use with the other " +
        "neural network stuff. This is the same as the negative log-likelihood " +
        "when using a multinomial logit specification. It also calculates the " +
        "fraction correctly predicted when using a winner-takes-all criterion. " +
        "Ties get the benefit of the doubt in the sense that if two categories " +
        "have the same posterior probability and one of them is the realized " +
        "outcome, it will count as a correct prediction.";
  }


  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "NeuronActivations";
      case 1:
        return "LayerTable";
      case 2:
        return "Targets";
      default:
        return "Error!  No such input.  ";
    }
  }


  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "NeuronActivations";
      case 1:
        return "LayerTable";
      case 2:
        return "Targets";
      default:
        return "Error!  No such input.  ";
    }
  }


  public String[] getInputTypes() {
    String[] types = {
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
    };
    return types;
  }


  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "CrossEntropy";
      case 1:
        return "FractionPredictedCorrectly";
      default:
        return "Error!  No such output.  ";
    }
  }


  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "CrossEntropy";
      case 1:
        return "FractionPredictedCorrectly";
      default:
        return "Error!  No such output.  ";
    }
  }


  public String[] getOutputTypes() {
    String[] types = {
        "java.lang.Double",
        "java.lang.Double",
    };
    return types;
  }


  public void doit() throws Exception {

    MultiFormatMatrix NeuronActivations = (MultiFormatMatrix)this.pullInput(0);
    MultiFormatMatrix LayerTable = (MultiFormatMatrix)this.pullInput(1);
    MultiFormatMatrix Targets = (MultiFormatMatrix)this.pullInput(2);

// pull out some relevant constants
    int nExamples = NeuronActivations.getDimensions()[0];
    int nNeurons = NeuronActivations.getDimensions()[1];
    int nLayers = LayerTable.getDimensions()[0];
    int nOptions = (int) LayerTable.getValue(nLayers - 1, 0) + 1; // don't forget the extra one
    int FirstOutput = nNeurons - nOptions;

// begin to do the deeds
    double ActivationRealized = -1.0;
    double EntropyTemp = -2.0;
    int nPredictedCorrectly = -3;
    int RealizedIndex = -4;
    boolean RealizedIsMaximum = true;

// sift through the data
    EntropyTemp = 0;
    nPredictedCorrectly = 0;
    for (int ExampleIndex = 0; ExampleIndex < nExamples; ExampleIndex++) {
      // figure out which option is predicted by the neural net; this would be for predictive ability
      // ok, that would require exhaustive search. there is a better way. we know what it is supposed
      // to be, so we search over all the options comparing to the realized one. if we find another
      // one has greater probability, we break and skip. if not, then we cheer and bump up the counter.
//      System.out.println("  ExampleIndex = " + ExampleIndex + "; nExamples = " + nExamples );
      RealizedIndex = FirstOutput + (int) Targets.getValue(ExampleIndex, 0);
      RealizedIsMaximum = true;
      ActivationRealized = NeuronActivations.getValue(ExampleIndex, RealizedIndex);
      for (int OptionIndex = 0; OptionIndex < nOptions; OptionIndex++) {
        if (NeuronActivations.getValue(ExampleIndex, OptionIndex + FirstOutput) > ActivationRealized) {
          // this means that the realized outcome is not a possible maximum because we found something bigger
          RealizedIsMaximum = false;
          break;
        }
      }
      if (RealizedIsMaximum) {
        nPredictedCorrectly++;
      }
      // accumulate the cross-entropy
      EntropyTemp -= java.lang.Math.log(ActivationRealized);
    }

// store it away properly
    Double CrossEntropy = new Double(EntropyTemp);
    Double FractionPredictedCorrectly = new Double((double) nPredictedCorrectly / (double) nExamples);

    this.pushOutput(CrossEntropy, 0);
    this.pushOutput(FractionPredictedCorrectly, 1);
  }

}
