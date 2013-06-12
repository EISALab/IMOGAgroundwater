package ncsa.d2k.modules.projects.dtcheng.matrix;


import ncsa.d2k.modules.projects.dtcheng.*;

import ncsa.d2k.core.modules.*;


public class NNHessianSingleHiddenLayer extends ComputeModule {

  public String getModuleName() {
    return "NNHessianSingleHiddenLayer";
  }


  public String getModuleInfo() {
    return "This module calculates the weight and bias Hessian " +
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
        return "LayerStartFinishNeuronTable";
      case 4:
        return "WeightNumberFromToNeuronTable";
      case 5:
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
        return "LayerStartFinishNeuronTable";
      case 4:
        return "WeightNumberFromToNeuronTable";
      case 5:
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
        "java.lang.Integer",
    };
    return types;
  }


  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "Hessian";
      default:
        return "Error!  No such output.  ";
    }
  }


  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "Hessian";
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
    MultiFormatMatrix LayerStartFinishNeuronTable = (MultiFormatMatrix)this.pullInput(3);
    MultiFormatMatrix WeightNumberFromToNeuronTable = (MultiFormatMatrix)this.pullInput(4);
    int NumberOfElementsThreshold = ((Integer)this.pullInput(5)).intValue();

// some preparations
    int nInputs = (int)LayerTable.getValue(0, 0);
    int nRelevantOutputs = (int)LayerTable.getValue((int)LayerTable.getDimensions()[0],0);
    int nWeights = (int)WeightNumberFromToNeuronTable.getDimensions()[0];
    int nExamples = NeuronActivations.getDimensions()[0];
    int nNeurons = NeuronActivations.getDimensions()[1] - 1;
    int nLayers = (int)LayerTable.getDimensions()[0];
    int FirstOutput = (int) LayerStartFinishNeuronTable.getValue(nLayers - 1,0);

    int FirstWeightToOutputLayer = -1234;
    int FirstNeuronInLayerBeforeOutput = -5;
    // Beware the MAGIC ASSUMPTION!!! we are assuming that we are dealing with either
    // zero hidden layers or a single hidden layer. hence, the layer
    if (nLayers == 2) {
      // we are dealing with zero hidden layers so "just prior to output" neurons start immediately
      FirstNeuronInLayerBeforeOutput = 0;
      FirstWeightToOutputLayer = 0;
    }
    else if (nLayers == 3) {
        FirstNeuronInLayerBeforeOutput = (int)LayerTable.getValue(1,0);
        FirstWeightToOutputLayer = (int)LayerTable.getValue(0,0)*(int)LayerTable.getValue(1,0);
    }
    else {
      System.out.println("More than a single hidden layer. This module " +
                         "can only handle zero or one hidden layers.");
      throw new Exception();
    }

// determine the proper format
    int nParameters = nWeights + NNDeltas.getDimensions()[0] - nInputs;
    int NumElements = nParameters*nParameters;
    int FormatIndex = -1; // initialize
    if (NumElements < NumberOfElementsThreshold) { // small means keep it in core; single dimensional in memory is best
      FormatIndex = 1; // Beware the MAGIC NUMBER!!!
    }
    else { // not small means big, so go out of core; serialized blocks on disk are best
      FormatIndex = 3; // Beware the MAGIC NUMBER!!!
    }

// begin to do the deeds
    // initializing constants...
    MultiFormatMatrix Hessian = new MultiFormatMatrix(FormatIndex,
        new int[] {nParameters,nParameters});
    MultiFormatMatrix aych = new MultiFormatMatrix(FormatIndex,
                                                   new int[] {nWeights,nWeights});
    MultiFormatMatrix AYCH = new MultiFormatMatrix(FormatIndex,
                                                   new int[] {nRelevantOutputs,1});

    double aychTemp = -1.0;
//    int NeuronFromA = -2; // i
    int NeuronToA = -3; // j
//    int NeuronFromB = -4; // k
    int NeuronToB = -5; // l

    // build the little h: aych
    for (int WeightIndexA = FirstWeightToOutputLayer; WeightIndexA < nWeights; WeightIndexA++) {
      for (int WeightIndexB = WeightIndexA; WeightIndexB < nWeights; WeightIndexB++) {
//        NeuronFromA = (int) WeightNumberFromToNeuronTable.getValue(WeightIndexA, 0);
        NeuronToA = (int) WeightNumberFromToNeuronTable.getValue(WeightIndexA, 1);
//        NeuronFromB = (int) WeightNumberFromToNeuronTable.getValue(WeightIndexB, 0);
        NeuronToB = (int) WeightNumberFromToNeuronTable.getValue(WeightIndexB, 1);

        aychTemp = 0.0;
        for (int ExampleIndex = 0; ExampleIndex < nExamples; ExampleIndex++) {
          if (WeightIndexA != WeightIndexB) {
            aychTemp += (NeuronActivations.getValue(ExampleIndex, NeuronToA) *
                         NeuronActivations.getValue(ExampleIndex, NeuronToB));
          }
          else {
            aychTemp += (NeuronActivations.getValue(ExampleIndex, NeuronToA) -
                         (NeuronActivations.getValue(ExampleIndex, NeuronToA) *
                          NeuronActivations.getValue(ExampleIndex, NeuronToB)));
          }
        }
        aych.setValue(NeuronToA,NeuronToB);
      }
    }

    double AYCHtemp = -5.0;
    double asdf = 5;
    int FirstHidden = 1234;
    int LastHidden = 5123;
    // build the Big H: AYCH; only needed if there is, in fact, a hidden layer
    if (nLayers == 3) {
      for (int NeuronInHiddenLayer = FirstHidden; NeuronInHiddenLayer < (LastHidden + 1); NeuronInHiddenLayer++) {
        AYCHtemp = 0.0;
        for (int NeuronInOutputLayer = FirstOutput; NeuronInOutputLayer < nNeurons; NeuronInOutputLayer++) {
          for (int ExampleIndex = 0; ExampleIndex < nExamples; ExampleIndex++) {
            AYCHtemp += asdf;
          }
        }
      }
    }




















    this.pushOutput(Hessian,0);
  }

}
