/*&%^1 Do not modify this section. */
package ncsa.d2k.modules.core.optimize.ga;
import ncsa.d2k.core.modules.*;
import java.io.*;
/*#end^1 Continue editing. ^#&*/
/*&%^2 Do not modify this section. */
/**
	This is the base class from which most of the modules which create populations will
	inherit. This class supports several properties that are associated with populations:
	<UL>
		<LI>NumberGenerations is the number of generations to complete before we give up.
		<LI>BestFitness is the best possible value for the range of objective values.
		<LI>WorstFitness is the worst possible objective value.
		<LI>TargetFitness is the fitness at which we will quit searching contented.
		<LI>PopulationSize is the number of individuals in the population.
	<UL>

*/
abstract public class PopulationPrep extends ncsa.d2k.core.modules.DataPrepModule
/*#end^2 Continue editing. ^#&*/
implements Serializable {

	/**
		This method returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			default: return "No such input";
		}
/*#end^3 Continue editing. ^#&*/
	}

	/**
		This method returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes () {
/*&%^4 Do not modify this section. */
		String [] types =  {
};
		return types;
/*#end^4 Continue editing. ^#&*/
	}

	/**
		This method returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo (int index) {
/*&%^5 Do not modify this section. */
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Population\">    <Text>This output is the resulting population for the genetic algorithm. </Text>  </Info></D2K>";
			default: return "No such output";
		}
/*#end^5 Continue editing. ^#&*/
	}

	/**
		This method returns an array of strings that contains the data types for the outputs.
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes () {
/*&%^6 Do not modify this section. */
		String [] types =  {
			"ncsa.d2k.modules.core.optimize.ga.Population"};
		return types;
/*#end^6 Continue editing. ^#&*/
	}

	/**
		This method returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo () {
/*&%^7 Do not modify this section. */
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Population Generator\">    <Text>This module will produce a population for input to the genetic algorithm. The doit method is abstract and must be overriden, this is simply a convenience class that provides the properties used by the ga. When the population is generated, the properties set in the subclass of this module will be set in the property as well, the only thing required of the subclass is that it provides a method that will return the number of bits each individual may possess. </Text>  </Info></D2K>";
/*#end^7 Continue editing. ^#&*/
	}

	//////////////////////////////////
	// The properties of this serializable module are the
	// parameters that are set in the population.
	/////////////////////////////////

	/** The initial population size. */
	protected int populationSize = 100;

	/** generation counter. */
	public int maxGenerations = 1000;

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

	protected double targetFitness = 0.01;
	public void setTargetFitness (double targetFitness) {
		this.targetFitness = targetFitness;
	}
	public double getTargetFitness () {
		return this.targetFitness;
	}

	protected double best = 0.00;
	public void setBestFitness (double targetFitness) {
		this.best = targetFitness;
	}
	public double getBestFitness () {
		return this.best;
	}

	protected double worst = 1.0;
	public void setWorstFitness (double targetFitness) {
		this.worst = targetFitness;
	}
	public double getWorstFitness () {
		return this.worst;
	}
/*&%^8 Do not modify this section. */
/*#end^8 Continue editing. ^#&*/
}

