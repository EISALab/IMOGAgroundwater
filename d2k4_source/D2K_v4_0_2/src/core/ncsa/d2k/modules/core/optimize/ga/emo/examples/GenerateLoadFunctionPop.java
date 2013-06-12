package ncsa.d2k.modules.core.optimize.ga.emo.examples;

import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.optimize.ga.emo.*;
import ncsa.d2k.modules.core.optimize.util.*;
import ncsa.d2k.core.modules.*;
import java.io.Serializable;
import ncsa.d2k.modules.core.optimize.ga.nsga.*;

/**
		CrossoverModule.java

*/
public class GenerateLoadFunctionPop extends PopulationPrep  {
	public GenerateLoadFunctionPop () {
	}

	/**
		This method returns the description of the module.

		@return the description of the module.
	*/
	public String getModuleInfo () {
		return "<html>  <head>      </head>  <body>    This module sets up the initial population, and will set all the fields of     the population that are used to steer the genetic algorithm.  </body></html>";
	}

	/**
		Create the initial population. In this case we have chosen to override the doit method,
		though it was probably not necessary

		@param outV the array to contain output object.
	*/
	public void doit () throws Exception {

		// First define the binary range, in this case 8 bits.
		int numGenes = 1;
		DoubleRange [] xyz = new DoubleRange [3];
		xyz [0] = new DoubleRange ("x1", 20.0, -20.0);
		xyz [1] = new DoubleRange ("x2", 20, -20);
		xyz [2] = new DoubleRange ("x3", 20, -20);


		// the objective constraints is a property of the problem. However, as a parameter
		// of this module, we can either minimize or maximize by swapping the values
		ObjectiveConstraints oc [] = new ObjectiveConstraints [2];
		oc[0] = ObjectiveConstraintsFactory.getObjectiveConstraints ("f1",
				this.getBestFitness (), this.getWorstFitness ());
		oc[1] = ObjectiveConstraintsFactory.getObjectiveConstraints ("f2",
				this.getBestFitness (), this.getWorstFitness ());
		NsgaPopulation pop = new ConstrainedNsgaPopulation (xyz, oc, this.getPopulationSize (), this.getTargetFitness ());
		pop.setMaxGenerations (this.maxGenerations);
		this.pushOutput (pop, 0);
	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "GenerateLoadFunctionPop";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
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
