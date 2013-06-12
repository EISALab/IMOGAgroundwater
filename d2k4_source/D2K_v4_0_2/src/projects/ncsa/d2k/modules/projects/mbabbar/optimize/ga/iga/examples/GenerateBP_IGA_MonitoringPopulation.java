package ncsa.d2k.modules.projects.mbabbar.optimize.ga.iga.examples;

import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.optimize.ga.emo.*;
import ncsa.d2k.modules.core.optimize.ga.nsga.*;
import ncsa.d2k.modules.core.optimize.util.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.projects.mbabbar.optimize.ga.iga.*;
import java.io.Serializable;

/**
		CrossoverModule.java

*/
public class GenerateBP_IGA_MonitoringPopulation extends PopulationPrep  {

        /////////////////////////
        // GA variable fields //
        /** Chromosome size. */
	protected int c_size = 36;

        // IGA variable fields //
        int numIndivsRankedByUser = 20;
        int numExpectedHumanRankingSessions = 10;
        /////////////////////////
        long randomSeed = 0;
	public GenerateBP_IGA_MonitoringPopulation () {
	}
        // get random number seed
        public long getRandomSeed (){
          return randomSeed;
        }

        // set random number seed
        public void setRandomSeed (long seed){
          randomSeed = seed;
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


	/**
		set the probability that any two individuals will cross.
		@param score the new probability that any two individuals will cross.
	*/
	public void setChromosomeSize (int csize) {
		c_size = csize;
	}

	/**
		get the crossover rate.
		@returns the crossover rate.
	*/
	public int getChromosomeSize () {
		return c_size;
	}

        ////////////////////////////////////
        // Some get set methods for IGA

        /**
         * this returns the number of individuals that should be ranked during every interactive session
         * by the user in one sitting. To prevent human fatigue this should be a small number
         * like 20.
         */
          public int getNumIndivsForUserRanking () {

              return numIndivsRankedByUser;

          }

          /**
           * this sets the number of individuals that should be ranked during every interactive session
           * by the user in one sitting. To prevent human fatigue this should be a small number
           * like 20.
           */
          public void setNumIndivsForUserRanking (int num) {

              numIndivsRankedByUser = num;

          }

          /**
           * this returns the number of ranking sessions that a user is expecting to go through
           * To prevent human fatigue this should be a small number
           */
          public int getNumExpectedHumanRankingSessions () {

              return numExpectedHumanRankingSessions;

          }

          /**
           * this sets the number of ranking sessions that a user is expecting to go through.
           * To prevent human fatigue this should be a small number.
           */
          public void setNumExpectedHumanRankingSessions (int num) {

              numExpectedHumanRankingSessions = num;

          }

        ///////////////////////////////////
	public void doit () throws Exception {

               //System.out.println("entered Generate GW Monitoring Population");
		// First define the binary range, in this case 8 bits.

		//BinaryRange [] xyz = new BinaryRange [1];
		//xyz [0] = new BinaryRange ("x",c_size,1);
                BinaryRange [] xyz = new BinaryRange [c_size];
                for (int i=0; i< c_size ; i++){
                  Integer id = new Integer(i);
                  xyz [i] = new BinaryRange ("x"+ id.toString(), 1, 1.0);
                }
		//xyz [0] = new BinaryRange ("x1",20);

                boolean [] qualObjFlags = new boolean[3];
                qualObjFlags[0] = false;
                qualObjFlags[1] = false;
                qualObjFlags[2] = true;
                //qualObjFlags[3] = true;
		// the objective constraints is a property of the problem. However, as a parameter
		// of this module, we can either minimize or maximize by swapping the values
		ObjectiveConstraints oc [] = new ObjectiveConstraints [3];
		oc[0] = ObjectiveConstraintsFactory.getObjectiveConstraints ("Cost",
				this.getBestFitness (), this.getWorstFitness ());
		oc[1] = ObjectiveConstraintsFactory.getObjectiveConstraints ("BTEX Error",
				this.getBestFitness (), this.getWorstFitness ());
                oc[2] = ObjectiveConstraintsFactory.getObjectiveConstraints ("Human Rank",
				this.getBestFitness (), this.getWorstFitness ());
                //oc[3] = ObjectiveConstraintsFactory.getObjectiveConstraints ("Human Rank",
		//		this.getBestFitness (), this.getWorstFitness ());

                  // Exit nicely
                  //System.exit(0);

		IGANsgaPopulation pop = new IGANsgaPopulation (xyz, oc, this.getPopulationSize (), this.getTargetFitness (), this.randomSeed);
                pop.setMaxGenerations (this.maxGenerations);
                pop.setIgaQualObj(qualObjFlags);
                pop.setNumExpectedRankingSessions(numExpectedHumanRankingSessions);
                pop.setNumIndivsForUserRanking(numIndivsRankedByUser);

                // initialize rank flags that indicate whether an individual has been ranked or not.
                for(int i = 0; i< this.getPopulationSize(); i++){
                  ((IGANsgaSolution)pop.getMember(i)).setRankedIndivFlag(false);
                }
		this.pushOutput (pop, 0);
	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "Generate BP IGA GWMonitoringPopulation";
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
				return "GW Monitoring Population";
			default: return "NO SUCH OUTPUT!";
		}
	}
}
