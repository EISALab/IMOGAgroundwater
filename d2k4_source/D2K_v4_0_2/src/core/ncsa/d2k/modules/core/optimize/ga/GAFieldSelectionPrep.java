/*&%^1 Do not modify this section. */
package ncsa.d2k.modules.core.optimize.ga;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.optimize.util.*;
/*#end^1 Continue editing. ^#&*/
/*&%^2 Do not modify this section. */
/**
	GAFieldSelectionPrep.java

*/
public class GAFieldSelectionPrep extends PopulationPrep
/*#end^2 Continue editing. ^#&*/
{

	/**
		This method returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "         ";
			default: return "No such input";
		}
	}

	/**
		This method returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes () {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.basic.ExampleTableImpl"};
		return types;
	}

	/**
		This method returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo (int index) {
		switch (index) {
			case 0: return "      The population object is the input and driver for the genetic algorithm, this instance will be configured to manipulate bits, one per attribute, a bit turned on is a feature selected.   ";
			default: return "No such output";
		}
	}

	/**
		This method returns an array of strings that contains the data types for the outputs.
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes () {
		String[] types = {"ncsa.d2k.modules.core.optimize.ga.Population"};
		return types;
	}

	/**
		This method returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo () {
		return "<html>  <head>      </head>  <body>    This module will take the input example table, and set up a population     object that will search the space of all input features. There will be     another module that will evaluate each individual in the population     evaluating it's fitness relative to some fitness function related to the     quality of the associated inputs in terms of predicting the outputs.  </body></html>";
	}

	ExampleTable et = null;
	protected int getNumberBits () {
		return et.getInputFeatures ().length;
	}

	/**
		PUT YOUR CODE HERE.
	*/
	public void doit () throws Exception {
		et = (ExampleTable) this.pullInput (0);
		BinaryRange [] xyz = new BinaryRange [1];
		xyz [0] = new BinaryRange ("fields", this.getNumberBits ());

		// the objective constraints is a property of the problem. However, as a parameter
		// of this module, we can either minimize or maximize by swapping the values.
		ObjectiveConstraints oc = ObjectiveConstraintsFactory.getObjectiveConstraints ("fitness",
				this.getBestFitness (), this.getWorstFitness ());
		SOPopulation pop = new SOPopulation (xyz, oc, this.getPopulationSize (), this.getTargetFitness ());
		pop.setMaxGenerations (this.maxGenerations);
		this.pushOutput (pop, 0);
	}
/*&%^8 Do not modify this section. */
/*#end^8 Continue editing. ^#&*/

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "Feature Selection Prep";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Input table";
			default: return "NO SUCH INPUT!";
		}
	}

	/**
	 * Return the human readable name of the indexed output.
	 * @param index the index of the output.
	 * @return the human readable name of the indexed output.
	 */
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Population";
			default: return "NO SUCH OUTPUT!";
		}
	}
}

