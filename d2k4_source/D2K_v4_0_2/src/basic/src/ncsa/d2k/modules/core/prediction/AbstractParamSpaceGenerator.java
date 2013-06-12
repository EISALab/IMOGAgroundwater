package ncsa.d2k.modules.core.prediction;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.parameter.*;




/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */
abstract public class AbstractParamSpaceGenerator extends ncsa.d2k.core.modules.DataPrepModule {

	protected ParameterSpace space;
	public ParameterSpace getCurrentSpace() { return space; }
	public void setCurrentSpace(ParameterSpace space) { this.space = space; }

	/**
	 * Returns a reference to the developer supplied defaults. These are
	 * like factory settings, absolute ranges and definitions that are not
	 * mutable.
	 * @return the factory settings space.
	 */
	abstract protected ParameterSpace getDefaultSpace();

	/**
	 * returns information about the input at the given index.
	 * @return information about the input at the given index.
	 */
	public String getInputInfo(int index) {
		switch (index) {
			default: return "No such input";
		}
	}

	/**
	 * returns information about the output at the given index.
	 * @return information about the output at the given index.
	 */
	public String getInputName(int index) {
		switch(index) {
			default: return "NO SUCH INPUT!";
		}
	}

	/**
	 * returns string array containing the datatypes of the inputs.
	 * @return string array containing the datatypes of the inputs.
	 */
	public String[] getInputTypes() {
		String[] types = {		};
		return types;
	}

	/**
	 * returns information about the output at the given index.
	 * @return information about the output at the given index.
	 */
	public String getOutputInfo(int index) {
		switch (index) {
			case 0: return "The parameter space that will be searched.";
			default: return "No such output";
		}
	}

	/**
	 * returns information about the output at the given index.
	 * @return information about the output at the given index.
	 */
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Parameter Space";
			default: return "NO SUCH OUTPUT!";
		}
	}

	/**
	 * returns string array containing the datatypes of the outputs.
	 * @return string array containing the datatypes of the outputs.
	 */
	public String[] getOutputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.parameter.ParameterSpace"};
		return types;
	}

	/**
	 * returns the information about the module.
	 * @return the information about the module.
	 */
	public String getModuleInfo() {

        String s = "<p>Overview: ";
        s += "This module produces a parameter space object that constrains the search space ";
	s += "for parameter optimization. ";

        s += "</p><p>Detailed Description: ";
        s += "Model building algorithms often require one or more control parameters. ";
        s += "For example, a neural net may require parameters specifying the number of hidden layers and an activation value. ";
        s += "The optimal control parameter settings for a given dataset are not usually known before the run begins. ";
        s += "Therefore, several models are often built and evaluated to identify the best control parameter settings ";
        s += "for a given dataset. ";

        s += "</p><p>";
        s += "To automate this build/evaluate process, D2K defines a procedure, implemented as a series of modules in an itinerary, ";
        s += "that supports the optimization of control parameters. ";

        s += "</p><p>";
        s += "This module allows the user to specify the range of possible values for every control parameter that is relevant ";
        s += "to the algorithm being used.  The optimization itinerary will build multiple models, varying the parameters ";
        s += "within the space defined in this module.  ";
        s += "The range of values to search is specified via the property editor, and the results are made available ";
        s += "on the <i>Parameter Space</i> output port. ";

        s += "</p><p>Using the Property Editor: ";
        s += "The property editor used to control the range of possible values for an algorithm's control parameters ";
        s += "is based on the same GUI interface regardless of the algorithm.  The user is presented with a list of ";
        s += "parameters that are relevant to the algorithm they are employing.  These parameters are explained ";
        s += "in the <i>Properties</i> section of the <i>Module Information</i>. ";

        s += "</p><p>";
        s += "For each parameter, text edit boxes allow the user to specify the range of allowable values (<i>Min</i> ";
        s += "and <i>Max</i>), the default value (<i>Default</i>), and the minimum resolution to use when navigating the range ";
        s += "(<i>Res</i>).   A <i>Reset</i> button is also provided for each parameter to restore the parameter ";
        s += "specifications to their original settings. ";
        s += "The same set of boxes is used regardless of the type of the control parameter. ";

        s += "</p><p>";
        s += "For boolean parameters, a value of 0 is considered False and a value of 1 is considered True. ";
        s += "The <i>Min</i> and <i>Max</i> values should be set to 0 or 1, with the minimum less than or equal to the maximum. ";
        s += "The resolution should be 1 for these parameters. ";

        s += "</p><p>";
        s += "For integer parameters, ";
        s += "the <i>Min</i> and <i>Max</i> values should be integers, with the minimum less than or equal to the maximum. ";
        s += "The resolution should be set to an integer value. ";

        s += "</p><p>";
        s += "For floating point parameters, ";
        s += "the <i>Min</i> and <i>Max</i> values can be floating point values, with the minimum less than or equal to the maximum. ";
        s += "The resolution can be a floating point value. ";

        s += "</p><p>";
        s += "Enumerated parameters, for example the choice between multiple distance metrics, are implemented as integer parameters. ";
        s += "The first choice in the list of choices enumerated in the <i>Property Descriptions</i> is assigned the value 0. ";
        s += "The second choice is assigned the value 1, and so on. ";
        s += "For enumerated parameters, the user must select <i>Min</i> and <i>Max</i> values that fall within the default ";
        s += "range shown, as that range encompasses the entire set of valid choices. ";
        s += "The resolution should be set to 1. ";

        s += "</p><p>";
        s += "There may be times when the user wants to allow some control parameters to vary over a range of values while ";
        s += "holding other parameters constant.  This behavior can be accomplished by entering the same <i>Min</i> and <i>Max</i> ";
        s += "values for the parameters that are to be held constant" ;

	return s;

	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "Parameter Space Generator";
	}

	/**
	 * All we have to do here is push the parameter space.
	 */
	public void doit() throws Exception {
		if (space == null) space = this.getDefaultSpace();
		this.pushOutput(space, 0);
	}

	/**
	 * return a reference a custom property editor to select the percent test
	 * and train.
	 * @return a reference a custom property editor
	 */
	public CustomModuleEditor getPropertyEditor() {
		if (space == null) space = this.getDefaultSpace();
		return new SetParameterSpace(this);
	}

}

// Start QA Comments
// 4/7/03 - Ruth added more extensive Module Info
// End QA Comments
