package ncsa.d2k.modules.core.prediction.decisiontree.continuous;

import java.beans.PropertyVetoException;

import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.datatype.model.Model;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;
import ncsa.d2k.modules.core.prediction.ErrorFunction;


public class DecisionTreeInducer extends DecisionTreeInducerOpt {

    /*
    public void    setUseSimpleBooleanSplit (boolean value) {this.UseSimpleBooleanSplit = value;}
    public boolean getUseSimpleBooleanSplit ()              {return this.UseSimpleBooleanSplit;}


    public void    setPrintEvolvingModels (boolean value) { this.PrintEvolvingModels  = value;}
    public boolean getPrintEvolvingModels ()              {return this.PrintEvolvingModels;}
    */

  //private int MinDecompositionPopulation = 20;
  public void setMinDecompositionPopulation(int value) throws PropertyVetoException {
    if (value < 1) {
      throw new PropertyVetoException(" < 1", null);
    }
    this.MinDecompositionPopulation = value;
  }

  public int getMinDecompositionPopulation() {
    return this.MinDecompositionPopulation;
  }

  //private double MinErrorReduction = 0.0;
  public void setMinErrorReduction(double value) {
    this.MinErrorReduction = value;
  }

  public double getMinErrorReduction() {
    return this.MinErrorReduction;
  }

  //public boolean UseOneHalfSplit = false;
  public void setUseOneHalfSplit(boolean value) {
    this.UseOneHalfSplit = value;
  }

  public boolean getUseOneHalfSplit() {
    return this.UseOneHalfSplit;
  }

  //public boolean UseMidPointBasedSplit = false;
  public void setUseMidPointBasedSplit(boolean value) {
    this.UseMidPointBasedSplit = value;
  }

  public boolean getUseMidPointBasedSplit() {
    return this.UseMidPointBasedSplit;
  }

  //public boolean UseMeanBasedSplit = true;
  public void setUseMeanBasedSplit(boolean value) {
    this.UseMeanBasedSplit = value;
  }

  public boolean getUseMeanBasedSplit() {
    return this.UseMeanBasedSplit;
  }

  //public boolean UsePopulationBasedSplit = false;
  public void setUsePopulationBasedSplit(boolean value) {
    this.UsePopulationBasedSplit = value;
  }

  public boolean getUsePopulationBasedSplit() {
    return this.UsePopulationBasedSplit;
  }

  //public boolean SaveNodeExamples = false;
  public void setSaveNodeExamples(boolean value) {
    this.SaveNodeExamples = value;
  }

  public boolean getSaveNodeExamples() {
    return this.SaveNodeExamples;
  }

  //public boolean UseMeanNodeModels = true;
  public void setUseMeanNodeModels(boolean value) {
    this.UseMeanNodeModels = value;
    this.UseLinearNodeModels = !value;
  }

  public boolean getUseMeanNodeModels() {
    return this.UseMeanNodeModels;
  }

  //public boolean UseLinearNodeModels = false;
  public void setUseLinearNodeModels(boolean value) {
    this.UseLinearNodeModels = value;
    this.UseMeanNodeModels = !value;
  }

  public boolean getUseLinearNodeModels() {
    return this.UseLinearNodeModels;
  }

  //public boolean UseLinearNodeModels = false;
  public void setTrace(boolean value) {
    this._Trace = value;
  }

  public boolean getTrace() {
    return this._Trace;
  }

  int numBiasDimensions = 9;

  public PropertyDescription[] getPropertiesDescriptions() {

    PropertyDescription[] pds = new PropertyDescription[numBiasDimensions];

    pds[0] = new PropertyDescription(
        "minDecompositionPopulation",
        "Minimum examples per leaf",
        "Prevents the generation of splits that result in leaf nodes with " +
        "less than the specified number of examples.  ");

    pds[1] = new PropertyDescription(
        "minErrorReduction",
        "Minimum split error reduction",
        "The units of this error reduction are relative to the error function passed to the " +
        "decision tree inducer.  " +
        "A split will not occur unless the error is reduced by at least the amount specified.");

    pds[2] = new PropertyDescription(
        "useOneHalfSplit",
        "Generate splits only at 1/2",
        "This works fine for boolean and continuous values.  " +
        "If used as the sole decomposition strategy, it forces the system to only split on a variable once.  ");

    pds[3] = new PropertyDescription(
        "useMeanBasedSplit",
        "Generate mean splits",
        "The mean of each attribute value is calculated in the given node and used to generate " +
        "splits for that node.  One or more split methods (mean, midpoint, and median) can be use simultaneously.  ");

    pds[4] = new PropertyDescription(
        "useMidPointBasedSplit",
        "Generate midpoint splits",
        "The min and max values of each attribute at each node in the tree are used to generate splits for that node.  " +
        "The split occurs at the midpoint between the min and max values.  " +
        "One or more split methods (mean, midpoint, and median) can be use simultaneously.  ");

    pds[5] = new PropertyDescription(
        "usePopulationBasedSplit",
        "Generate median splits",
        "The median of each attribute value is calculated in the given node and used to generate " +
        "splits for that node.  This requires sorting of all the examples in a node and therefore " +
        "scales at n log n in time complexity.  " +
        "One or more split methods (mean, midpoint, and median) can be use simultaneously.  ");

    pds[6] = new PropertyDescription(
        "saveNodeExamples",
        "Save examples at each node",
        "In order to compute and print statistics of the node you must save the examples at the node " +
        "which increases the space and time complexity of the algorithm by a linear factor.  ");

    pds[7] = new PropertyDescription(
        "useMeanNodeModels",
        "Use the mean averaging for models",
        "This results in a simple decision tree with constant functions at the leaves.  " +
        "UseMeanNodeModels and UseLinearNodeModels are mutually exclusive.  ");

    pds[8] = new PropertyDescription(
        "useLinearNodeModels",
        "Use multiple regression for models",
        "This results in a decision tree with linear functions of the input attributes at the leaves.  " +
        "UseLinearNodeModels and UseMeanNodeModels are mutually exclusive.  ");

    return pds;
  }


  public String getModuleName() {
    return "Decision Tree Inducer";
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





  public void beginExecution() {
  }


  public void instantiateBiasFromProperties() {
    // Nothing to do in this case since properties are reference directly by the algorithm and no other control
    // parameters need be set.  This may not be the case in general so this stub is left open for future development.
  }


  public void doit() throws Exception {

 //ANCA : added exceptions
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
    		throw new Exception ("input attributes  like" +exampleSet.getColumn(inputs[i]).getLabel()  + " must be numeric");
	
	for (int i = 0; i < outputs.length; i++)
			if(!(exampleSet.getColumn(outputs[i])).getIsScalar()) 
				throw new Exception ("output attribute  must be numeric");
    		
    		
    ErrorFunction errorFunction   = (ErrorFunction) this.pullInput(1);

    instantiateBiasFromProperties();

    Model model = null;

    model = generateModel(exampleSet, errorFunction);

    this.pushOutput(model, 0);
  }


}
