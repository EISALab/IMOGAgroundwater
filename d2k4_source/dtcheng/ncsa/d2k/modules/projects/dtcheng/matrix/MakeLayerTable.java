package ncsa.d2k.modules.projects.dtcheng.matrix;


import ncsa.d2k.modules.projects.dtcheng.*;

import ncsa.d2k.core.modules.*;


public class MakeLayerTable extends ComputeModule {

  public String getModuleName() {
    return "MakeLayerTable";
  }


  public String getModuleInfo() {
    return "This module creates the LayerTable for the Neural Net " +
        "implementation based on the number of inputs, a HiddenLayerTable, " +
        "and the number of options (minus one). If no hidden layers are " +
        "desired, the HiddenLayerTable should have a zero as its first element.";
  }


  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "nExplanatoryVariables";
      case 1:
        return "HiddenLayerTable";
      case 2:
        return "nOptionsMinusOne";
      case 3:
        return "NumberOfElementsThreshold";
      default:
        return "Error!  No such input.  ";
    }
  }


  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "nExplanatoryVariables";
      case 1:
        return "HiddenLayerTable";
      case 2:
        return "nOptionsMinusOne";
      case 3:
        return "NumberOfElementsThreshold";
      default:
        return "Error!  No such input.  ";
    }
  }


  public String[] getInputTypes() {
    String[] types = {
        "java.lang.Integer",
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
        "java.lang.Integer",
        "java.lang.Integer",
    };
    return types;
  }


  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "LayerTable";
      case 1:
        return "nNeurons";
      case 2:
        return "nInputs";
      case 3:
        return "nLayers";
      case 4:
        return "nWeights";
      default:
        return "Error!  No such output.  ";
    }
  }


  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "LayerTable";
      case 1:
        return "nNeurons";
      case 2:
        return "nInputs";
      case 3:
        return "nLayers";
      case 4:
        return "nWeights";
      default:
        return "Error!  No such output.  ";
    }
  }


  public String[] getOutputTypes() {
    String[] types = {
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
        "java.lang.Integer",
        "java.lang.Integer",
        "java.lang.Integer",
        "java.lang.Integer",
    };
    return types;
  }


  public void doit() throws Exception {

    int nExplanatoryVariables = ((Integer)this.pullInput(0)).intValue();
    MultiFormatMatrix HiddenLayerTable = (MultiFormatMatrix)this.pullInput(1);
    int nOptionsMinusOne = ((Integer)this.pullInput(2)).intValue();
    int NumberOfElementsThreshold = ((Integer)this.pullInput(3)).intValue();

    int NumElements = -1;
    int FormatIndex = -2;

    if (HiddenLayerTable.getValue(0,0) == 0.0) {
      NumElements = 2;
      FormatIndex = 1;
    }
    else {
        // determine the proper format
        // Beware the MAGIC NUMBER!!! 2 = 1 row for inputs + 1 row for outputs
        NumElements = HiddenLayerTable.getDimensions()[0] + 2;
        if (NumElements < NumberOfElementsThreshold) {
          // small means keep it in core; single dimensional in memory is best
          FormatIndex = 1; // Beware the MAGIC NUMBER!!!
        }
        else { // not small means big, so write it out of core; serialized blocks
          // on disk are best
          FormatIndex = 3; // Beware the MAGIC NUMBER!!!
        }
      }


    MultiFormatMatrix LayerTable = new MultiFormatMatrix(FormatIndex, new int[] {NumElements, 1});

    LayerTable.setValue(0, 0, (double) nExplanatoryVariables);
    LayerTable.setValue(NumElements - 1, 0, (double) nOptionsMinusOne);

    for (int RowIndex = 1; RowIndex < NumElements - 1; RowIndex++) {
      LayerTable.setValue(RowIndex, 0, HiddenLayerTable.getValue(RowIndex - 1, 0));
    }

    Integer nLayers = new Integer((int)LayerTable.getDimensions()[0]);

    int nNeuronsTemp = 0;
    for (int RowIndex = 0; RowIndex < nLayers.intValue(); RowIndex++) {
      nNeuronsTemp += (int)LayerTable.getValue(RowIndex,0);
    }
    Integer nNeurons = new Integer(nNeuronsTemp);

    Integer nInputs = new Integer((int)LayerTable.getValue(0,0));

    int nWeightsTemp = 0;
    // Beware the MAGIC NUMBER!!! the "nLayers.intValue() - 1" gets us to
    // the next to the last element...
    for (int RowIndex = 0; RowIndex < nLayers.intValue() - 1; RowIndex++) {
      nWeightsTemp += ((int)LayerTable.getValue(RowIndex,0) *
                       (int)LayerTable.getValue(RowIndex + 1, 0));
    }
    Integer nWeights = new Integer(nWeightsTemp);

    this.pushOutput(LayerTable, 0);
    this.pushOutput(nNeurons, 1);
    this.pushOutput(nInputs, 2);
    this.pushOutput(nLayers, 3);
    this.pushOutput(nWeights, 4);
  }

}
