package ncsa.d2k.modules.projects.dtcheng.matrix;

import ncsa.d2k.modules.projects.dtcheng.*;

import ncsa.d2k.core.modules.*;

public class MakeReferenceTables
    extends ComputeModule {

  public String getModuleName() {
    return "MakeReferenceTables";
  }

  public String getModuleInfo() {
    return "This module creates some reference tables that are used by " +
        "the activation routines. Here are the old goods from the MATLAB " +
        "code. Remember that now the neuron and weight numbering starts at " +
        "zero, not one. <p> <p>" +
        "<tt> This function creates a variety of useful reference tables " +
"   relating neurons and layers. <p>" +
" The assumption is full connectivity, feedforward, with no " +
"   skip-layer-connections. Bias units are implemented " +
"   via the program and are not listed in the layer_table. " +
" <p>" +
" Beware the MAGIC ASSUMPTION!!! full connectivity, feedforward, " +
"   no skip-layer-connections " +
" <p>" +
" USAGE: [N_to_L, L_sf_N, Wn_N_ft] = make_reference_tables(layer_table) " +
"<p> " +
" layer_table:   a column matrix that describes the structure of the " +
"                   network. It is assumed that the network is fully " +
"                   connected at this stage of the game. The column number " +
"                   corresponds to the layer number (inputs = #1) while " +
"                   the element value represents the number of (non-bias) " +
"                   neurons. For example, consider the network: " +
"                   [the neurons will be numbered serially (or sequentially, " +
"                   however you want to call it). the visual heuristic is " +
"                   as follows: flow from bottom to top. arrange each layer " +
"                   horizontally. number from bottom to top, left to right.] " +
"<p> " +
"             OUTPUT <br> " +
"               ^ <br>" +
"               | <br>" +
"              (6) <br>" +
"             / | \\ <br>" +
"            /  |  \\ <br>" +
"          (3) (4) (5) <br>" +
"           \\  / __/ <br>" +
"            \\/ / <br>" +
"            (1)  (2) [should be connected to 3, 4, & 5] <br>" +
"             ^    ^ <br>" +
"             |    | <br>" +
"            x_1  x_2 <br>" +
"<p> " +
"                   it would be represented as " +
" layer_table = [2 3 1]'; " +
" <p> " +
" The N_to_L (neuron to [it's] layer) output matrix describes which layer each " +
"   neuron is in. For the example network, the result would be: " +
" <p> " +
" N_to_L = ... <br>" +
" [ 1 <br>" +
"   1 <br>" +
"   2 <br>" +
"   2 <br>" +
"   2 <br>" +
"   3 ]; <br> " +
" <p> " +
" The L_sf_N ([a] Layer's Start/Finish Neuron [number]) output matrix describes " +
"   the first and last neuron in each layer. The row number corresponds to the " +
"   layer number while the first column is the first neuron in that layer and " +
"   the second column is the last neuron in that layer. For the example network, " +
"   the result would be: <p>" +
" L_sf_N = ... <br>" +
"  [   1     2 <br>" +
"      3     5 <br>" +
"      6     6 ]; <br>" +
" <p>" +
" The Wn_N_ft (Weight Number to Neuron From/To Table) is a two-column matrix that " +
"   describes the neurons connected by a particular weight. The row number " +
"   corresponds to a weight number (which is not easily associated with the " +
"   neurons it works on, hence this table). The first column is the the from " +
"   neuron in the connection and the second column is the to neuron. The ordering " +
"   is lexicographically with the hierarchy of from takes precedence over to " +
"   using the neuron number definitions the same way as the layer_table. " +
"   For the example network, the result would be: " +
" <p>" +
" Wn_N_ft = ... <br>" +
" [    1     3 <br>" +
"      1     4 <br>" +
"      1     5 <br>" +
"      2     3 <br>" +
"      2     4 <br>" +
"      2     5 <br>" +
"      3     6 <br>" +
"      4     6 <br>" +
"      5     6 ]; <br>" +
"</tt>";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "LayerTable";
      case 1:
        return "NumberOfElementsThreshold";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "LayerTable";
      case 1:
        return "NumberOfElementsThreshold";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
        "java.lang.Integer",
    };
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "NeuronToLayerTable";
      case 1:
        return "LayerStartFinishNeuronTable";
      case 2:
        return "WeightNumberFromToNeuronTable";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "NeuronToLayerTable";
      case 1:
        return "LayerStartFinishNeuronTable";
      case 2:
        return "WeightNumberFromToNeuronTable";
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

    MultiFormatMatrix LayerTable = (MultiFormatMatrix)this.pullInput(0);
    Integer NumberOfElementsThreshold = (Integer)this.pullInput(1);

// initialize some goods
    int NumElements = -1;
    int FormatIndex = -1;

// calculate some constants. very similar to what is in MakeLayerTable...
    // number of layers
    int nLayers = LayerTable.getDimensions()[0];

    // number of neurons
    int nNeurons = 0;
    Double nNeuronsDoubleTemp = new Double(0.0);
    for (int RowIndex = 0; RowIndex < nLayers; RowIndex++) {
      nNeuronsDoubleTemp = new Double(LayerTable.getValue(RowIndex,0));
      nNeurons += nNeuronsDoubleTemp.intValue();
    }

    // number of weights
    int nWeights = 0;
    Double nWeightsDoubleTemp = new Double(0.0);
    // Beware the MAGIC NUMBER!!! the "nLayers - 1" gets us to
    // the next to the last element...
    for (int RowIndex = 0; RowIndex < nLayers - 1; RowIndex++) {
      nWeightsDoubleTemp = new Double(LayerTable.getValue(RowIndex, 0) *
                                      LayerTable.getValue(RowIndex + 1, 0)
                                      );
      nWeights += nWeightsDoubleTemp.intValue();
    }

// Neuron to Layer table
    // determine the proper format.
    NumElements = nNeurons;
    if (NumElements < NumberOfElementsThreshold.intValue()) {
      // small means keep it in core; single dimensional in memory is best
      FormatIndex = 1; // Beware the MAGIC NUMBER!!!
    }
    else { // not small means big, so write it out of core; serialized blocks
      // on disk are best
      FormatIndex = 3; // Beware the MAGIC NUMBER!!!
    }
    // initialize some more goods
    int PreviousEnd = -1;
    int NewStart = -13;
    int NewEnd = -14;
    MultiFormatMatrix NeuronToLayerTable = new MultiFormatMatrix(FormatIndex,
        new int[] {nNeurons, 1});

    for (int LayerIndex = 0; LayerIndex < nLayers; LayerIndex++) {
      NewStart = PreviousEnd + 1;
      NewEnd = NewStart + (int)LayerTable.getValue(LayerIndex,0);
      for (int InnerRowIndex = NewStart; InnerRowIndex < NewEnd; InnerRowIndex++) {
//        System.out.println("InnerRowIndex = " + InnerRowIndex + "; LayerIndex = " + LayerIndex);
        NeuronToLayerTable.setValue(InnerRowIndex, 0, (double)LayerIndex);
      }
      PreviousEnd = NewEnd - 1;
    }

// Layer Starting/Finishing Neuron Table
    // determine the proper format.
    NumElements = nLayers*2;
    if (NumElements < NumberOfElementsThreshold.intValue()) {
      // small means keep it in core; single dimensional in memory is best
      FormatIndex = 1; // Beware the MAGIC NUMBER!!!
    }
    else { // not small means big, so write it out of core; serialized blocks
      // on disk are best
      FormatIndex = 3; // Beware the MAGIC NUMBER!!!
    }
    // initialize some more goods
    MultiFormatMatrix LayerStartFinishNeuronTable = new MultiFormatMatrix(FormatIndex,
        new int[] {nLayers, 2});
    double FirstSum = 0;
    double SecondSum = -1;
    // do the deed
    LayerStartFinishNeuronTable.setValue(0, 0, 0);
    LayerStartFinishNeuronTable.setValue(0, 1, (int)LayerTable.getValue(0,0) - 1);
    for (int LayerIndex = 1; LayerIndex < nLayers; LayerIndex++) {
      for (int RowIndex = 0; RowIndex < LayerIndex; RowIndex++) {
        FirstSum += LayerTable.getValue(RowIndex,0);
      }
      for (int RowIndex = 0; RowIndex < LayerIndex + 1; RowIndex++) {
        SecondSum += LayerTable.getValue(RowIndex,0);
      }
      LayerStartFinishNeuronTable.setValue(LayerIndex, 0, FirstSum);
      LayerStartFinishNeuronTable.setValue(LayerIndex, 1, SecondSum);
      FirstSum = 0;
      SecondSum = -1; // remember, this is the last one in the set, not necessarily the boundary... confusing.
    }

// Weight Number to Neuron From/To Table
// WeightNumberFromToNeuronTable
    // determine the proper format.
    NumElements = nWeights * 2;
    if (NumElements < NumberOfElementsThreshold.intValue()) {
      // small means keep it in core; single dimensional in memory is best
      FormatIndex = 1; // Beware the MAGIC NUMBER!!!
    }
    else { // not small means big, so write it out of core; serialized blocks
      // on disk are best
      FormatIndex = 3; // Beware the MAGIC NUMBER!!!
    }
    // initialize some more goods
    int StorageNumber = 0;
    MultiFormatMatrix WeightNumberFromToNeuronTable = new MultiFormatMatrix(FormatIndex,
        new int[] {nWeights, 2});
    // do the deed
    for (int LayerIndex = 0; LayerIndex < nLayers - 1; LayerIndex++) {
      for (int NeuronFrom = (int) LayerStartFinishNeuronTable.getValue(LayerIndex, 0);
           NeuronFrom < (int) LayerStartFinishNeuronTable.getValue(LayerIndex, 1) + 1;
           NeuronFrom++) {
        for (int NeuronTo = (int) LayerStartFinishNeuronTable.getValue(LayerIndex + 1, 0);
             NeuronTo < (int) LayerStartFinishNeuronTable.getValue(LayerIndex + 1, 1) + 1;
             NeuronTo++) {
          WeightNumberFromToNeuronTable.setValue(StorageNumber,0,(double)NeuronFrom);
          WeightNumberFromToNeuronTable.setValue(StorageNumber,1,(double)NeuronTo);

          StorageNumber++;
        }
      }
    }

    this.pushOutput(NeuronToLayerTable, 0);
    this.pushOutput(LayerStartFinishNeuronTable, 1);
    this.pushOutput(WeightNumberFromToNeuronTable, 2);
  }

}
