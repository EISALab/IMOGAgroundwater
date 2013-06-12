package ncsa.d2k.modules.core.optimize.ga;


import ncsa.d2k.modules.core.optimize.util.*;
import ncsa.d2k.modules.core.optimize.ga.emo.*;
import ncsa.d2k.modules.core.optimize.ga.nsga.*;

public class GenPopulationPrep extends ncsa.d2k.core.modules.DataPrepModule
 {

	//////////////////////////////////
	// The properties of this serializable module are the
	// parameters that are set in the population.
	/////////////////////////////////

	/** The initial population size. */
	protected int populationSize = 100;

	/** generation counter. */
	protected int maxGenerations = 1000;

	protected double targetFitness = 0.01;

	///////////////////////////
	// Getter and settter methods for properties
	///////////////////////////
	/**
		Returns the maximum number of generations.

		@returns the mutation rate.
	*/
	public int getNumberGenerations () {
		return maxGenerations;
	}

	/**
		Set the maximum number of generations.

		@param mrate the new mutation rate.
	*/
	public void setNumberGenerations (int max) {
		maxGenerations = max;
	}

	/**
		set the population size.
		@param psize the population size.
	*/
	public void setPopulationSize (int psize) {
		this.populationSize = psize;
	}

	/**
		get the population size.
		@returns the population size.
	*/
	public int getPopulationSize () {
		return this.populationSize;
	}

	public void setTargetFitness (double targetFitness) {
		this.targetFitness = targetFitness;
	}
	public double getTargetFitness () {
		return this.targetFitness;
	}


	public String[] getInputTypes () {
		String[] types = {"[Lncsa.d2k.modules.compute.learning.optimize.util.SolutionSpace"};
		return types;
	}
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "The ranges of the parameters to be optimized";
			default: return "No such input";
		}
	}
	public String getOutputInfo (int index) {
		switch (index) {
			case 0: return "      This output is the resulting population for the genetic algorithm.   ";
			default: return "No such output";
		}
	}

	public String[] getOutputTypes () {
		String[] types = {"ncsa.d2k.modules.core.optimize.ga.Population"};
		return types;
	}
	public String getModuleInfo(){
		return "<html>  <head>      </head>  <body>    Takes the parameter range definitions and creates a population as defined     by the properties<br>Properties:<br>    <ul>      <li>        NumberGenerations is the number of generations to complete before we         give up.      </li>      <li>        TargetFitness is the fitness at which we will quit searching contented.      </li>      <li>        PopulationSize is the number of individuals in the population.      </li>    </ul>  </body></html>";
	}

	public void doit() throws Exception{

		SolutionSpace ss=(SolutionSpace)pullInput(0);
		//this is what we want to eventually initiallize and push
		Population pop;

		Range[] ranges=ss.getRanges();


		if(ss instanceof MOSolutionSpace){
			ObjectiveConstraints[] ocs=((MOSolutionSpace)ss).getObjectiveConstraints();
			pop=new UnconstrainedNsgaPopulation (ranges, ocs, populationSize, targetFitness);
		}
		else /*if(ss instanceof SOSolutionSpace)*/{
			ObjectiveConstraints oc=((SOSolutionSpace)ss).getObjectiveConstraints();
			pop=new SOPopulation(ranges, oc, populationSize, targetFitness);
		}
		pop.setMaxGenerations(maxGenerations);


		pushOutput(pop, 0);
	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "GenPopulationPrep";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "input0";
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

