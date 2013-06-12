package ncsa.d2k.modules.projects.dtcheng.matrix;


import ncsa.d2k.modules.projects.dtcheng.*;

import ncsa.d2k.core.modules.*;


public class NNGradient extends ComputeModule {

  public String getModuleName() {
    return "NNGradient";
  }


  public String getModuleInfo() {
    return "This module calculates the weight and bias gradients " +
        "for a feedforward network (from the so-called <i>deltas</i>) " +
        "with softmax final activations and " +
        "logistic intermediate activations using a " +
        "cross-entropy error criterion. At the moment, there is very limited, " +
        "that is, non-existent idiot-proofing.";
  }


  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "NeuronActivations";
      case 1:
        return "NNDeltas";
      case 2:
        return "LayerTable";
      case 3:
        return "WeightNumberFromToNeuronTable";
      case 4:
        return "NumberOfElementsThreshold";
      default:
        return "Error!  No such input.  ";
    }
  }


  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "NeuronActivations";
      case 1:
        return "NNDeltas";
      case 2:
        return "LayerTable";
      case 3:
        return "WeightNumberFromToNeuronTable";
      case 4:
        return "NumberOfElementsThreshold";
      default:
        return "Error!  No such input.  ";
    }
  }


  public String[] getInputTypes() {
    String[] types = {
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
        "java.lang.Integer",
    };
    return types;
  }


  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "BiasesGradient";
      case 1:
        return "SerializedWeightsGradient";
      case 2:
        return "StackedWeightsBiasesGradient";
      default:
        return "Error!  No such output.  ";
    }
  }


  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "BiasesGradient";
      case 1:
        return "SerializedWeightsGradient";
      case 2:
        return "StackedWeightsBiasesGradient: This is a single column " +
            "which has the weights gradient first followed by the " +
            "<i>relevant</i> biases. aka: skipping the input neurons " +
            "since they don't really have a bias. For use in H<sup>-1</sup>g";
      default:
        return "Error!  No such output.  ";
    }
  }


  public String[] getOutputTypes() {
    String[] types = {
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
    };
    return types;
  }


  public void doit() throws Exception {

    MultiFormatMatrix NeuronActivations = (MultiFormatMatrix)this.pullInput(0);
    MultiFormatMatrix NNDeltas = (MultiFormatMatrix)this.pullInput(1);
    MultiFormatMatrix LayerTable = (MultiFormatMatrix)this.pullInput(2);
    MultiFormatMatrix WeightNumberFromToNeuronTable = (MultiFormatMatrix)this.pullInput(3);
    int NumberOfElementsThreshold = ((Integer)this.pullInput(4)).intValue();

// some preparations
    int nInputs = (int)LayerTable.getValue(0,0);
    int nWeights = (int)WeightNumberFromToNeuronTable.getDimensions()[0];

// determine the proper format
    int NumElements = nWeights + NNDeltas.getDimensions()[0];
    int FormatIndex = -1; // initialize
    if (NumElements < NumberOfElementsThreshold) {
      // small means keep it in core; single dimensional in memory is best
      FormatIndex = 1; // Beware the MAGIC NUMBER!!!
    }
    else { // not small means big, so go out of core; serialized blocks on disk are best
      FormatIndex = 3; // Beware the MAGIC NUMBER!!!
    }

// pull out some relevant constants
    int nExamples = NeuronActivations.getDimensions()[0];
    int nNeurons = NeuronActivations.getDimensions()[1] - 1;

// begin to do the deeds
    // initializing constants...
    MultiFormatMatrix BiasesGradient = new MultiFormatMatrix(FormatIndex,
        new int[] {nNeurons, 1}); // ignoring the extra un-notated output neuron
    MultiFormatMatrix SerializedWeightsGradient = new MultiFormatMatrix(FormatIndex,
        new int[] {nWeights, 1}); // ignoring the extra un-notated output neuron
    MultiFormatMatrix StackedWeightsBiasesGradient = new MultiFormatMatrix(FormatIndex,
        new int[] {nWeights + nNeurons - nInputs, 1}); // ignoring the extra un-notated output neuron
    double BiasGradientTemp = -1.0;
    double WeightsGradientTemp = -2.0;
    int NeuronFrom = -3;
    int NeuronTo = -4;

    // figure the gradient for the biases
    for (int NeuronIndex = (int)LayerTable.getValue(0,0); NeuronIndex < nNeurons; NeuronIndex++) {
      BiasGradientTemp = 0.0;
      for (int ExampleIndex = 0; ExampleIndex < nExamples; ExampleIndex++) {
        BiasGradientTemp += NNDeltas.getValue(ExampleIndex,NeuronIndex);
      }
      BiasesGradient.setValue(NeuronIndex,0,BiasGradientTemp);
//      System.out.println("NeuronIndex = " + NeuronIndex + "; nInputs = " + nInputs +
//                         "; nWeights = " + nWeights);
      StackedWeightsBiasesGradient.setValue(NeuronIndex - nInputs + nWeights,
                                            0, BiasGradientTemp);
    }
    // figure the gradient for the weights
    for (int WeightIndex = 0; WeightIndex < nWeights; WeightIndex++) {
      WeightsGradientTemp = 0.0;
      for (int ExampleIndex = 0; ExampleIndex < nExamples; ExampleIndex++) {
//        System.out.println("WeightIndex = " + WeightIndex);
        NeuronFrom = (int)WeightNumberFromToNeuronTable.getValue(WeightIndex,0);
        NeuronTo = (int)WeightNumberFromToNeuronTable.getValue(WeightIndex,1);
        WeightsGradientTemp += (NeuronActivations.getValue(ExampleIndex,NeuronFrom) *
                                NNDeltas.getValue(ExampleIndex,NeuronTo));
      }
      SerializedWeightsGradient.setValue(WeightIndex,0,WeightsGradientTemp);
      StackedWeightsBiasesGradient.setValue(WeightIndex,0,WeightsGradientTemp);
    }

    this.pushOutput(BiasesGradient,0);
    this.pushOutput(SerializedWeightsGradient,1);
    this.pushOutput(StackedWeightsBiasesGradient,2);
  }

}
