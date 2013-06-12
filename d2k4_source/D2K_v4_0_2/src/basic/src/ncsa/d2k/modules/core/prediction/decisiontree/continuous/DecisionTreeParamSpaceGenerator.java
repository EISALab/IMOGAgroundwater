package ncsa.d2k.modules.core.prediction.decisiontree.continuous;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.prediction.AbstractParamSpaceGenerator;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.impl.*;

public class DecisionTreeParamSpaceGenerator extends AbstractParamSpaceGenerator {

    public String getOutputName(int i) { 
	switch (i) { 
	case 0: return "Parameter Space"; 
	case 1: return "Function Inducer Class"; 
	} 
	return ""; 
    } 

    public String getOutputInfo(int i) { 
	switch (i) { 
	case 0: return "Control Parameter Space for Decision Tree Inducer"; 
	case 1: return "Decision Tree Function Inducer Class"; 
	} 
	return ""; 
    }
 
    public String[] getOutputTypes() { 
	String [] out = { 
	    "ncsa.d2k.modules.core.datatype.parameter.ParameterSpace", 
	    "java.lang.Class"}; 
	return out; 
    } 
 


    /**
     * Returns a reference to the developer supplied defaults. These are
     * like factory settings, absolute ranges and definitions that are not
     * mutable.
     * @return the factory settings space.
     */
    protected ParameterSpace getDefaultSpace() {

	int         numControlParameters = 9;
	double []   minControlValues = new double[numControlParameters];
	double []   maxControlValues = new double[numControlParameters];
	double []   defaults         = new double[numControlParameters];
	int    []   resolutions      = new int[numControlParameters];
	int    []   types            = new int[numControlParameters];
	String []   biasNames        = new String[numControlParameters];

	int biasIndex = 0;
	
	biasNames       [biasIndex] = "MinDecompositionPopulation";
	minControlValues[biasIndex] = 1;
	maxControlValues[biasIndex] = 100;
	defaults        [biasIndex] = 20;
	resolutions     [biasIndex] = (int) maxControlValues[biasIndex] - (int) minControlValues[biasIndex] + 1;
	types           [biasIndex] = ColumnTypes.INTEGER;
	biasIndex++;
	
	biasNames       [biasIndex] = "MinErrorReduction";
	minControlValues[biasIndex] = -999999.0;
	maxControlValues[biasIndex] = 0.0;
	defaults        [biasIndex] = -999999.0;
	resolutions     [biasIndex] = 1000000000;
	types           [biasIndex] = ColumnTypes.DOUBLE;
	biasIndex++;
	
	biasNames       [biasIndex] = "UseOneHalfSplit";
	minControlValues[biasIndex] = 0;
	maxControlValues[biasIndex] = 1;
	defaults        [biasIndex] = 0;
	resolutions     [biasIndex] = 1;
	types           [biasIndex] = ColumnTypes.BOOLEAN;
	biasIndex++;
	
	biasNames       [biasIndex] = "UseMidPointBasedSplit";
	minControlValues[biasIndex] = 0;
	maxControlValues[biasIndex] = 1;
	defaults        [biasIndex] = 0;
	resolutions     [biasIndex] = 1;
	types           [biasIndex] = ColumnTypes.BOOLEAN;
	biasIndex++;
	
	biasNames       [biasIndex] = "UseMeanBasedSplit";
	minControlValues[biasIndex] = 0;
	maxControlValues[biasIndex] = 1;
	defaults        [biasIndex] = 1;
	resolutions     [biasIndex] = 1;
	types           [biasIndex] = ColumnTypes.BOOLEAN;
	biasIndex++;
	
	biasNames       [biasIndex] = "UsePopulationBasedSplit";
	minControlValues[biasIndex] = 0;
	maxControlValues[biasIndex] = 1;
	defaults        [biasIndex] = 0;
	resolutions     [biasIndex] = 1;
	types           [biasIndex] = ColumnTypes.BOOLEAN;
	
	biasIndex++;
	biasNames       [biasIndex] = "SaveNodeExamples";
	minControlValues[biasIndex] = 0;
	maxControlValues[biasIndex] = 1;
	defaults        [biasIndex] = 0;
	resolutions     [biasIndex] = 1;
	types           [biasIndex] = ColumnTypes.BOOLEAN;
	
	biasIndex++;
	biasNames       [biasIndex] = "UseMeanNodeModels";
	minControlValues[biasIndex] = 0;
	maxControlValues[biasIndex] = 1;
	defaults        [biasIndex] = 1;
	resolutions     [biasIndex] = 1;
	types           [biasIndex] = ColumnTypes.BOOLEAN;
	biasIndex++;
	
	biasNames       [biasIndex] = "UseLinearNodeModels";
	minControlValues[biasIndex] = 0;
	maxControlValues[biasIndex] = 1;
	defaults        [biasIndex] = 0;
	resolutions     [biasIndex] = 1;
	types           [biasIndex] = ColumnTypes.BOOLEAN;
	biasIndex++;

	ParameterSpace psi = new ParameterSpaceImpl();
	psi.createFromData(biasNames, minControlValues, maxControlValues, defaults, resolutions, types);
	return psi;
	
    }

    /**
     * REturn a name more appriate to the module.
     * @return a name
     */
    public String getModuleName() {
	return "Decision Tree Param Space Generator";
    }

    /**
     * Return a list of the property descriptions.
     * @return a list of the property descriptions.
     */

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



    /**
     * All we have to do here is push the parameter space and function inducer class.
     */
    public void doit() throws Exception {

	Class functionInducerClass = null;
	try {
	    functionInducerClass 
		= Class.forName("ncsa.d2k.modules.core.prediction.decisiontree.continuous.DecisionTreeInducerOpt");
	}
	catch (Exception e) {
	    //System.out.println("could not find class");
	    //throw new Exception();
	    throw new Exception(getAlias() + ": could not find class DecisionTreeInducerOpt "); 
	}
	
	if (space == null) space = this.getDefaultSpace();
	this.pushOutput(space, 0);
	this.pushOutput(functionInducerClass, 1);
    }
}
