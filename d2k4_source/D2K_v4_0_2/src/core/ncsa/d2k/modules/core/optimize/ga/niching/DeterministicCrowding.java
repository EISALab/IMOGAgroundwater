package ncsa.d2k.modules.core.optimize.ga.niching;

import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.optimize.ga.crossover.*;
import ncsa.d2k.core.modules.*;
import java.io.Serializable;
import java.util.*;

/**
		Perform a deterministic crowding on the new generation. First shuffle the
		population. If is not necessary to do any kind of selection before using
		this module. This shuffle is effectively random pairing without replacement.
		Next do a standard two point crossover.  Then, compare each set
		of offspring with their parent to determine how different they are.
		The parent will be copied into the new generation if it is more
		fit than it's progeny which it is most similar to.
*/
abstract public class DeterministicCrowding extends EvaluateModule implements Serializable {
	///////////////////////////////
	// Properties.
	///////////////////////////////

	protected boolean debugging = false;

	/**
		set the debug flag.
		@param debug is a boolean indicating if debugging is on or off.
	*/
	public void setDebugging (boolean score) {
		this.debugging = score;
	}

	/**
		get the debug flag.
		@returns true if debugging is on.
	*/
	public boolean getDebugging () {
		return this.debugging;
	}

	/** Defines the percent of the population that is possibly replace at each generation. */
	protected double generationGap = 0.6;

	/**
		Generation gap defines the percent of the population replaced in each
		generation, this value is set here.
		@param ng the new generation gap value.
	*/
	public void setGenerationGap (double ng) {
		generationGap = ng;
	}

	/**
		get the generation gap
		@returns the ratio of the population to be replace at each generation.
	*/
	public double getGenerationGap () {
		return generationGap;
	}

	/** The probability that any two individuals will cross. */
	protected double c_rate = 0.25;

	/**
		set the probability that any two individuals will cross.
		@param score the new probability that any two individuals will cross.
	*/
	public void setCrossoverRate (double score) {
		c_rate = score;
	}

	/**
		get the crossover rate.
		@returns the crossover rate.
	*/
	public double getCrossoverRate () {
		return c_rate;
	}

        protected long randomSeed = 5000;
        public Random randNum = new Random(randomSeed);

        // get random number seed
        public long getRandomSeed (){
          return randomSeed;
        }

       // set random number seed
        public void setRandomSeed (long seed){
          randomSeed = seed;
        }

	//////////////////////////////////
	// Info methods
	//////////////////////////////////
	/**
		This method returns the description of the various inputs.

		@return the description of the indexed input.
	*/
	public String getOutputInfo (int index) {
		String[] outputDescriptions = {
				"This population is the resulting crossed progeny."
		};
		return outputDescriptions[index];
	}

	/**
		This method returns the description of the various inputs.

		@return the description of the indexed input.
	*/
	public String getInputInfo (int index) {
		String[] inputDescriptions = {
				"This is the population to be crossed."
		};
		return inputDescriptions[index];
	}

	/**
		This method returns the description of the module.

		@return the description of the module.
	*/
	public String getModuleInfo () {
		String text = "This module will take the given population of Individuals and mate them, crossing them at some random gene. The only property is the crossover rate.";
		return text;
	}

	//////////////////////////////////
	// Type definitions.
	//////////////////////////////////

	public String[] getInputTypes () {
		String[] temp = {"ncsa.d2k.modules.core.optimize.ga.Population"};
		return temp;
	}

	public String[] getOutputTypes () {
		String[] temp = {"ncsa.d2k.modules.core.optimize.ga.Population"};
		return temp;
	}

	/**
	 * This is a very simple crowding computation, we just sum the
	 * differences between each of the corresponding genes of the individuals.
	 * @param a the first individual.
	 * @param b the second individual.
	 * @return the difference.
	 */
	private double crowding (Individual a, Individual b) {
		double diff = 0;
		if (a instanceof BinaryIndividual) {
			boolean [] genesA = (boolean []) a.getGenes ();
			boolean [] genesB = (boolean []) b.getGenes ();
			int d = 0;
			for (int i = 0 ; i < genesA.length; i++)
				if (genesA [i] != genesB [i])
					d++;
			diff = d;
		} else if (a instanceof NumericIndividual) {
			double [] genesA = (double []) a.getGenes ();
			double [] genesB = (double []) b.getGenes ();
			for (int i = 0 ; i < genesA.length; i++)
				if (genesA [i] > genesB [i])
					diff += genesA [i] - genesB [i];
				else
				    diff += genesB [i] - genesA [i];
		}
		return diff;
	}

	/**
		For each individual crossed, do deterministic crowding to determine
		if the offspring replaces the parent.
		@param outV the array to contain output object.
	*/
	int [] x = new int [2];
	public void doit () {

		// Our input argument is the population.
		Population population = (Population) this.pullInput(0);

		// In effect, the following lines are selection, shuffle the order of the
		// individuals and everybody mates. Only crosses producing better children
		// survive.
		int [] shuffle = new int [population.size ()];
		for (int i = 0 ; i < population.size () ; i++) shuffle [i] = i;
		population.shuffleIndices (shuffle);
		population.makeNextGeneration (shuffle);

		Individual [] individuals = population.getMembers ();
		Individual [] parents = population.getParents ();
		int [] x = new int [2];

		// Compute the last individual to cross.
		int last = (int) population.size ();
		int mom, dad;
		for (int i = 0; i < last; i += 2) {
			mom = i;
			dad = i+1;

			// choose two Crossover points
                        x[0] = (int) (randNum.nextDouble() * population.getNumGenes ());
			x[1] = (int) (randNum.nextDouble() * (population.getNumGenes () - 1));
                        
//			x[0] = (int) (Math.random () * population.getNumGenes ());
//			x[1] = (int) (Math.random () * (population.getNumGenes () - 1));
			if (x [1] < x [0]) {

				// Swap them
				int swap = x [1];
				x [1] = x [0];
				x [0] = swap;
			} else
				x [1]++;

			// Swap the indicated genes.
			individuals [mom].crossAt (x, individuals [dad]);
			this.evaluateIndividual (individuals [mom]);
			this.evaluateIndividual (individuals [dad]);

			// Now compare the children to the parents. A child will replace it's
			// closest parent only if it is better. Start with the child initially
			// replacing mom in the new population.
			if (debugging) {
				System.out.println ();
				System.out.println ("===================================");
				System.out.println ("Crossed Individuals before crowding.");
				System.out.println (individuals [mom]);
				System.out.println (individuals [dad]);
				System.out.println ("Parents.");
				System.out.println (parents [mom]);
				System.out.println (parents [dad]);
			}
			if (this.crowding (individuals [mom], parents [mom]) >
				this.crowding (individuals [mom], parents [dad])) {

				// The individual is closer to the dad
				if (population.compareMembers (parents [dad],
					 individuals [mom]) > 0) {

					// the parent is still better.
					if (debugging) System.out.println ("Old dad beats new mom");
					individuals [mom].copy (parents [dad]);
				}
			} else
				// closer to the mom
				if (population.compareMembers (parents [mom],
						 individuals [mom]) > 0) {

					// the parent is still better.
					if (debugging) System.out.println ("Old mom beats new mom");


					// the parent is still better.
					individuals [mom].copy (parents [mom]);
				}

			// Now do the member replacing dad
			if (this.crowding (individuals [dad], parents [mom]) >
				this.crowding (individuals [dad], parents [dad])) {

				// The individual is closer to the dad
				if (population.compareMembers (parents [dad],
						 individuals [dad]) > 0) {
					if (debugging) System.out.println ("Old dad beats new dad");

					// the parent is still better.
					individuals [dad].copy (parents [dad]);
				}
			} else
				// closer to the mom
				if (population.compareMembers (parents [mom],
						individuals [dad]) > 0) {
					if (debugging) System.out.println ("Old mom beats new dad");

					// the parent is still better.
					individuals [dad].copy (parents [mom]);
				}

			// Now compare the children to the parents. A child will replace it's
			// closest parent only if it is better. Start with the child initially
			// replacing mom in the new population.
			if (debugging) {
				System.out.println ();
				System.out.println ("Crossed Individuals after crowding.");
				System.out.println (individuals [mom]);
				System.out.println (individuals [dad]);
				System.out.println ("Parents.");
				System.out.println (parents [mom]);
				System.out.println (parents [dad]);
			}
		}
		this.pushOutput (population, 0);
	}
}
