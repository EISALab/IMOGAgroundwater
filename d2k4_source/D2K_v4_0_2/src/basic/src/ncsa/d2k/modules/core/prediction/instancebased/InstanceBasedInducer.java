package ncsa.d2k.modules.core.prediction.instancebased;

import ncsa.d2k.modules.core.prediction.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.model.*;
import ncsa.d2k.core.modules.PropertyDescription;
import java.beans.PropertyVetoException;

public class InstanceBasedInducer extends InstanceBasedInducerOpt {


  int numBiasDimensions = 3;

  public PropertyDescription[] getPropertiesDescriptions() {

    PropertyDescription[] pds = new PropertyDescription[numBiasDimensions];

    pds[0] = new PropertyDescription(
        "neighborhoodSize",
        "Neighborhood Size",
        "The number of examples to use for fitting the prediction module.  " +
        "This must be set to 1 or greater.  ");

    pds[1] = new PropertyDescription(
        "distanceWeightingPower",
        "Distance Weighting Power",
        "The value of the power term in the inverse distance weighting formula.  " +
        "Setting this to zero causes equal weighting of all examples.  " +
        "Setting it to 1.0 gives inverse distance weighting.  " +
        "Setting it to 2.0 gives inverse distance squared weighting and so on.  ");

    pds[2] = new PropertyDescription(
        "zeroDistanceValue",
        "Zero Distance Value",
        "What weight to associate to a stored example which has zero distance to example to be predicted.  " +
        "Since division by zero is not permitted, some value must be assigned to examples with zero distance.  "  +
        "This value is the weight and exact match should be given.  ");
    return pds;
  }

  private int NeighborhoodSize = 20;
  public void setNeighborhoodSize(int value) throws PropertyVetoException {
    if (value < 1) {
      throw new PropertyVetoException(" < 1", null);
    }
    this.NeighborhoodSize = value;
  }

  public int getNeighborhoodSize() {
    return this.NeighborhoodSize;
  }

  private double DistanceWeightingPower = 0.0;
  public void setDistanceWeightingPower(double value) {
    this.DistanceWeightingPower = value;
  }

  public double getDistanceWeightingPower() {
    return this.DistanceWeightingPower;
  }

  private double ZeroDistanceValue = 0.0;
  public void setZeroDistanceValue(double value) throws PropertyVetoException {
    if (value < 0.0) {
      throw new PropertyVetoException(" < 0.0", null);
    }
    this.ZeroDistanceValue = value;
  }

  public double getZeroDistanceValue() {
    return this.ZeroDistanceValue;
  }


  public String getModuleName() {
    return "Instance Based Inducer";
  }

  public String getInputName(int i) {
    switch(i) {
      case 0: return "Example Table";
      case 1: return "Error Function";
      default: return "Error!  No such input.";
    }
  }
  public String getInputInfo(int i) {
    switch (i) {
      case 0: return "Example Table";
      case 1: return "Error Function";
      default: return "Error!  No such input.";
    }
  }
  public String[] getInputTypes() {
    String[] types = {
      "ncsa.d2k.modules.core.datatype.table.ExampleTable",
      "ncsa.d2k.modules.core.prediction.ErrorFunction"
    };
    return types;
  }

  public String getOutputName(int i) {
    switch(i) {
      case  0: return "Model";
      default: return "Error!  No such output.";
    }
  }
  public String getOutputInfo(int i) {
    switch (i) {
      case 0: return "Model";
      default: return "Error!  No such output.";
    }
  }
  public String[] getOutputTypes() {
    String[] types = {"ncsa.d2k.modules.core.datatype.model.Model"};
    return types;
  }
  public void instantiateBiasFromProperties() {
    // Nothing to do in this case since properties are reference directly by the algorithm and no other control
    // parameters need be set.  This may not be the case in general so this stub is left open for future development.
  }

  public Model generateModel(ExampleTable examples, ErrorFunction errorFunction) {

    int numExamples = examples.getNumRows();
    int numInputs   = examples.getNumInputFeatures();
    double [] inputMins   = new double[numInputs];
    double [] inputMaxs   = new double[numInputs];
    double [] inputRanges = new double[numInputs];

    for (int v = 0; v < numInputs; v++) {
      inputMins[v] = Double.POSITIVE_INFINITY;
      inputMaxs[v] = Double.NEGATIVE_INFINITY;
    }

    for (int e = 0; e < numExamples; e++) {
      for (int v = 0; v < numInputs; v++) {
        double value = examples.getInputDouble(e, v);
        if (value < inputMins[v])
          inputMins[v] = value;
        if (value > inputMaxs[v])
          inputMaxs[v] = value;
      }
    }

    for (int v = 0; v < numInputs; v++) {
      inputRanges[v] = inputMaxs[v] - inputMins[v];
    }

    InstanceBasedModel model = new InstanceBasedModel(
        examples,
        inputRanges,
        NeighborhoodSize,
        DistanceWeightingPower,
        ZeroDistanceValue,
        examples);

    return (Model) model;
  }

  public void doit() throws Exception {
  	
  	//ANCA: added exceptions
	ExampleTable  exampleSet ;
		try {
			exampleSet = (ExampleTable)this.pullInput(0);
		} catch ( java.lang.ClassCastException e) {
			 throw new Exception("Input/Output attributes not selected.");
		}
    
		int[] inputs = exampleSet.getInputFeatures();
		int [] outputs = exampleSet.getOutputFeatures();
		for (int i = 0; i < inputs.length; i++)
			if(!(exampleSet.getColumn(inputs[i])).getIsScalar()) 
				throw new Exception ("input attributes like " +exampleSet.getColumn(inputs[i]).getLabel() +" must be numeric");
	
		for (int i = 0; i < outputs.length; i++)
				if(!(exampleSet.getColumn(outputs[i])).getIsScalar()) 
					throw new Exception ("output attribute must be numeric");

    
    ErrorFunction errorFunction   = (ErrorFunction) this.pullInput(1);

    instantiateBiasFromProperties();

    Model model = null;

    model = generateModel(exampleSet, errorFunction);

    this.pushOutput(model, 0);
  }

}
