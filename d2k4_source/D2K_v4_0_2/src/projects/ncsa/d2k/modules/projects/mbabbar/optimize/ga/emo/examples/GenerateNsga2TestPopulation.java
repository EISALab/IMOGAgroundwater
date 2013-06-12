package ncsa.d2k.modules.projects.mbabbar.optimize.ga.emo.examples;

import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.optimize.ga.emo.*;
import ncsa.d2k.modules.core.optimize.ga.nsga.*;
import ncsa.d2k.modules.core.optimize.util.*;
import ncsa.d2k.core.modules.*;
import java.io.Serializable;

/**
		CrossoverModule.java

*/
public class GenerateNsga2TestPopulation extends PopulationPrep  {
	public GenerateNsga2TestPopulation () {
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

               //System.out.println("entered Generate GW Monitoring Population");
		// First define the binary range, in this case 8 bits.
		//int numGenes = 1;
            //    BinaryRange [] xyz = new BinaryRange [50];
		//xyz [0] = new BinaryRange ("x1",50);
                int numGenes = 1;
		BinaryRange [] xyz = new BinaryRange [1];
		xyz [0] = new BinaryRange ("x",50);

		// the objective constraints is a property of the problem. However, as a parameter
		// of this module, we can either minimize or maximize by swapping the values
		ObjectiveConstraints oc [] = new ObjectiveConstraints [2];
		oc[0] = ObjectiveConstraintsFactory.getObjectiveConstraints ("f1",
				this.getBestFitness (), this.getWorstFitness ());
		oc[1] = ObjectiveConstraintsFactory.getObjectiveConstraints ("f2",
				this.getBestFitness (), this.getWorstFitness ());
                 // Exit nicely
                  //System.exit(0);

		NsgaPopulation pop = new UnconstrainedNsgaPopulation (xyz, oc, this.getPopulationSize (), this.getTargetFitness ());
		pop.setMaxGenerations (this.maxGenerations);
                System.out.println("max Generations : " + this.maxGenerations);
                System.out.println("population : " + this.getPopulationSize());
                pop.printIndividual(0);
		this.pushOutput (pop, 0);
	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "GenerateNsga2TestPopulation";
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
				return "Nsga2 Test Population";
			default: return "NO SUCH OUTPUT!";
		}
	}
}
