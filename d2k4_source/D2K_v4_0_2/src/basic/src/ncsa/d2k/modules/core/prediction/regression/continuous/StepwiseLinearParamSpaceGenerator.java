package ncsa.d2k.modules.core.prediction.regression.continuous;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.prediction.AbstractParamSpaceGenerator;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.impl.*;


public class StepwiseLinearParamSpaceGenerator extends AbstractParamSpaceGenerator {

    public String getOutputName(int i) { 
	switch (i) { 
	case 0: return "Parameter Space"; 
	case 1: return "Function Inducer Class"; 
	} 
	return ""; 
    } 

    public String getOutputInfo(int i) { 
	switch (i) { 
	case 0: return "Control Parameter Space for Stepwise Linear Inducer"; 
	case 1: return "Stepwise Linear Function Inducer Class"; 
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

    int         numControlParameters = 3;
    double []   minControlValues = new double[numControlParameters];
    double []   maxControlValues = new double[numControlParameters];
    double []   defaults         = new double[numControlParameters];
    int    []   resolutions      = new int[numControlParameters];
    int    []   types            = new int[numControlParameters];
    String []   biasNames        = new String[numControlParameters];

    int biasIndex = 0;

    biasNames       [biasIndex] = "UseStepwise";
    minControlValues[biasIndex] = 0;
    maxControlValues[biasIndex] = 1;
    defaults        [biasIndex] = 0;
    resolutions     [biasIndex] = 1;
    types           [biasIndex] = ColumnTypes.BOOLEAN;
    biasIndex++;

    biasNames       [biasIndex] = "Direction";
    minControlValues[biasIndex] = -1;
    maxControlValues[biasIndex] = 1;
    defaults        [biasIndex] = 1;
    resolutions     [biasIndex] = 2;
    types           [biasIndex] = ColumnTypes.INTEGER;
    biasIndex++;

    biasNames       [biasIndex] = "NumRounds";
    minControlValues[biasIndex] = 1;
    maxControlValues[biasIndex] = 8;
    defaults        [biasIndex] = 1;
    resolutions     [biasIndex] = (int) maxControlValues[biasIndex] - (int) minControlValues[biasIndex] + 1;
    types           [biasIndex] = ColumnTypes.INTEGER;
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
	return "Stepwise Linear Param Space Generator";
    }

    /**
     * Return a list of the property descriptions.
     * @return a list of the property descriptions.
     */
    int numBiasDimensions = 3;

    public PropertyDescription[] getPropertiesDescriptions() {
    PropertyDescription[] pds = new PropertyDescription[numBiasDimensions];

    pds[0] = new PropertyDescription(
        "useStepwise",
        "Use Stepwise",
        "When true, a stepwise regression procedure is followed, otherwise normal regression is used on all features.");

    pds[1] = new PropertyDescription(
        "direction",
        "Direction of Search",
        "When set to 1, step up regression is used, and when set to -1 step down regression is done.  ");

    pds[2] = new PropertyDescription(
        "numRounds",
        "Number of Feature Selection Rounds",
        "The number of features to add (step up) or remove (step down) from the initial feature subset.  ");
    return pds;
  }


    /**
     * All we have to do here is push the parameter space and function inducer class.
     */
    public void doit() throws Exception {

	Class functionInducerClass = null;
	try {
	    functionInducerClass 
		= Class.forName("ncsa.d2k.modules.core.prediction.regression.continuous.StepwiseLinearInducerOpt");
	}
	catch (Exception e) {
	    throw new Exception(getAlias() + ": could not find class StepwiseLinearInducerOpt "); 
	}
	
	if (space == null) space = this.getDefaultSpace();
	this.pushOutput(space, 0);
	this.pushOutput(functionInducerClass, 1);
    }
}
