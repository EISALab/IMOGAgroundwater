package ncsa.d2k.modules.core.prediction.mean.continuous;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.prediction.AbstractParamSpaceGenerator;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.impl.*;


public class MeanOutputParamSpaceGenerator extends AbstractParamSpaceGenerator {

    public String getOutputName(int i) { 
	switch (i) { 
	case 0: return "Parameter Space"; 
	case 1: return "Function Inducer Class"; 
	} 
	return ""; 
    } 

    public String getOutputInfo(int i) { 
	switch (i) { 
	case 0: return "Control Parameter Space for Mean Output Inducer." + 
		    " There are no parameters for the Mean Output inducer at this time."; 
	case 1: return "Mean Output Function Inducer Class"; 
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

	ParameterSpace psi = new ParameterSpaceImpl();
	//psi.createFromData(biasNames, minControlValues, maxControlValues, defaults, resolutions, types);
	return psi;
    }

    /**
     * REturn a name more appriate to the module.
     * @return a name
     */
    public String getModuleName() {
	return "Mean Output Param Space Generator";
    }

    /**
     * Return a list of the property descriptions.
     * @return a list of the property descriptions.
     */
    public PropertyDescription[] getPropertiesDescriptions() {
        return new PropertyDescription[0];
    }

    /**
     * All we have to do here is push the parameter space and function inducer class.
     */
    public void doit() throws Exception {

	Class functionInducerClass = null;
	try {
	    functionInducerClass = Class.forName("ncsa.d2k.modules.core.prediction.mean.continuous.MeanOutputInducerOpt");
	}
	catch (Exception e) {
	    //System.out.println("could not find class");
	    //throw new Exception();
	    throw new Exception(getAlias() + ": could not find class MeanOutputInducerOpt "); 
	}
	
	if (space == null) space = this.getDefaultSpace();
	this.pushOutput(space, 0);
	this.pushOutput(functionInducerClass, 1);
    }
}
