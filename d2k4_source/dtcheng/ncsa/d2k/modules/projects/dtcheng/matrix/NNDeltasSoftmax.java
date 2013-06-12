package ncsa.d2k.modules.projects.dtcheng.matrix;


import ncsa.d2k.modules.projects.dtcheng.*;

import ncsa.d2k.core.modules.*;


public class NNDeltasSoftmax extends ComputeModule {

  public String getModuleName() {
    return "NNDeltasSoftmax";
  }


  public String getModuleInfo() {
    return "This module calculates the traditionally so-called <i>deltas</i> " +
        "for a feedforward network with softmax final activations and " +
        "logistic intermediate activations using a " +
        "cross-entropy error criterion. At the moment, there is very limited, " +
        "that is, non-existent idiot-proofing.";
  }


  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "NeuronActivations";
      case 1:
        return "Targets";
      case 2:
        return "LayerTable";
      case 3:
        return "SerializedWeights";
      case 4:
        return "LayerStartFinishNeuronTable";
      case 5:
        return "NeuronToLayerTable";
      case 6:
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
        return "Targets";
      case 2:
        return "LayerTable";
      case 3:
        return "SerializedWeights";
      case 4:
        return "LayerStartFinishNeuronTable";
      case 5:
        return "NeuronToLayerTable";
      case 6:
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
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
        "java.lang.Integer",
    };
    return types;
  }


  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "NNDeltasSoftmax";
      default:
        return "Error!  No such output.  ";
    }
  }


  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "NNDeltasSoftmax";
      default:
        return "Error!  No such output.  ";
    }
  }


  public String[] getOutputTypes() {
    String[] types = {
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
    };
    return types;
  }


  public void doit() throws Exception {

    MultiFormatMatrix NeuronActivations = (MultiFormatMatrix)this.pullInput(0);
    MultiFormatMatrix Targets = (MultiFormatMatrix)this.pullInput(1);
    MultiFormatMatrix LayerTable = (MultiFormatMatrix)this.pullInput(2);
    MultiFormatMatrix SerializedWeights = (MultiFormatMatrix)this.pullInput(3);
    MultiFormatMatrix LayerStartFinishNeuronTable = (MultiFormatMatrix)this.pullInput(4);
    MultiFormatMatrix NeuronToLayerTable = (MultiFormatMatrix)this.pullInput(5);
    int NumberOfElementsThreshold = ((Integer)this.pullInput(6)).intValue();

// determine the proper format
    int NumElements = (NeuronActivations.getDimensions()[0] *
                       (NeuronActivations.getDimensions()[1] - 1));
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
    int nLayers = LayerTable.getDimensions()[0];
    int nOutputs = (int) LayerTable.getValue(nLayers - 1, 0); // this is the number of relevant outputs; that is, excluding the standardized one
//    int nWeights = SerializedWeights.getDimensions()[0];

// begin to do the deeds
    // initializing constants...
    MultiFormatMatrix NNDeltas = new MultiFormatMatrix(FormatIndex,
        new int[] {nExamples, nNeurons}); // ignoring the extra un-notated output neuron
    int MyLayer = -1;
    int MyLayerFirst = -2;
    int NextLayerFirst = -3;
    int FirstWeightNumber = -4;
    double DerivativeTemp = -5.0;
    double ThisActivation = -6.0;
    double PosteriorSum = -7.0;

    // find the index of the first output
    int OutputFirst = (int)LayerStartFinishNeuronTable.getValue(nLayers - 1, 0);

    for (int ExampleIndex = 0; ExampleIndex < nExamples; ExampleIndex++) {
      // figure the deltas for the output layer
      for (int StorageIndex = 0; StorageIndex < nOutputs; StorageIndex++) {
        if ((int) Targets.getValue(ExampleIndex, 0) == StorageIndex) {
          // This is the realized outcome...
          NNDeltas.setValue(ExampleIndex, OutputFirst + StorageIndex,
                            NeuronActivations.getValue(ExampleIndex, OutputFirst + StorageIndex) - 1.0);
        }
        else {
          // This outcome was not realized...
          NNDeltas.setValue(ExampleIndex, OutputFirst + StorageIndex,
                            NeuronActivations.getValue(ExampleIndex, OutputFirst + StorageIndex));
        }
      }
      // figure the deltas for the hidden and input layers working backwards
      /*
        for the hidden layers, we do like this:
          delta_j = z_j*(1-z_j)w_k*delta_k
          or delta_j = -sum [over posterior nodes] (delta_posterior)*(w_j_to_posterior)*(derivative of activation of j)
       */
//      System.out.println("OutputFirst = " + OutputFirst);
      for (int NeuronIndex = (OutputFirst - 1); NeuronIndex >= 0; NeuronIndex--) {
//        System.out.println("NeuronIndex = " + NeuronIndex);
        // going backwards through. hence my non-standard break criterion...
        // find the list of posterior nodes for a particular neuron. that is, what neurons does this one feed into...
        // under the assumption of full connectivity, it will feed into all fo the neurons in the next layer...
        // so, i need to know what layer i'm in, and what layer comes after...
        MyLayer = (int) NeuronToLayerTable.getValue(NeuronIndex, 0);
        MyLayerFirst = (int) LayerStartFinishNeuronTable.getValue(MyLayer, 0);
        NextLayerFirst = (int) LayerStartFinishNeuronTable.getValue(MyLayer + 1, 0);

        ThisActivation = NeuronActivations.getValue(ExampleIndex, NeuronIndex);
        DerivativeTemp = ThisActivation * (1.0 - ThisActivation);
        // run through the posterior nodes and their weights.
        // trying to calculate sum [over posterior neurons] delta_posterior*w_j_to_posterior

        // find the weight number for the first connection under consideration...
        FirstWeightNumber = 0;
        for (int LayerTempIndex = 0; LayerTempIndex < MyLayer; LayerTempIndex++) {
          FirstWeightNumber += ((int) LayerTable.getValue(LayerTempIndex, 0) *
                                (int) LayerTable.getValue(LayerTempIndex + 1, 0));
        }
        FirstWeightNumber += ((NeuronIndex - MyLayerFirst) *
                              (int) LayerTable.getValue(MyLayer + 1, 0));
        PosteriorSum = 0;
        for (int PosteriorIndex = 0; PosteriorIndex < (int) LayerTable.getValue(MyLayer + 1, 0); PosteriorIndex++) {
          // that is, from 0 to the # of neurons in the next layer (minus one for index's sake)
          PosteriorSum += (NNDeltas.getValue(ExampleIndex, PosteriorIndex + NextLayerFirst) *
                           SerializedWeights.getValue(PosteriorIndex + FirstWeightNumber, 0));
          // i can do this because of the way the weights are encoded. the outgoing weights from a
          // particular neuron are sequential..
        }
//        System.out.println("ExampleIndex = " + ExampleIndex + "; NeuronIndex = " +
//                           NeuronIndex + "; FirstWeightNumber = " + FirstWeightNumber +
//                           "; PosteriorSum = " + PosteriorSum + "; DerivativeTemp = " + DerivativeTemp);
        NNDeltas.setValue(ExampleIndex, NeuronIndex, DerivativeTemp * PosteriorSum);
      }
    }

    this.pushOutput(NNDeltas, 0);
  }

}
