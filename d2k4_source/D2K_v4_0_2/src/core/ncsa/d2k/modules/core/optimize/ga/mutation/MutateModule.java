package ncsa.d2k.modules.core.optimize.ga.mutation;

import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.core.modules.*;
import java.io.Serializable;
import java.math.*;
import java.util.*;

/**
                This module will mutate the population at a rate relative to the mutationRate property.
                There is a debugging flag as well.
*/
public class MutateModule extends ncsa.d2k.core.modules.DataPrepModule 	 {

        protected transient Mutation mutation;

        public MutateModule() {
          createMutation();
        }

        protected void createMutation() {
          mutation = new Mutation();
        }

        //private double M_rate = .0005;

        //private boolean debugging = false;

        /**
                set the rankFlag.

                @param score is a boolean indicating if rankFlag is on or off.
        */
        public void setDebugging (boolean score) {
//		this.debugging = score;
          mutation.setDebugging(score);
        }

        /**
                get the rankFlag.

                @returns true if rankFlag is on.
        */
        public boolean getDebugging () {
//		return this.debugging;
          return mutation.getDebugging();
        }

        /**
                set the rankFlag.

                @param score is a boolean indicating if rankFlag is on or off.
        */
        public void setMutationRate (double mr) {
//		this.M_rate = mr;
          mutation.setMutationRate(mr);
        }

        /**
                get the rankFlag.

                @returns true if rankFlag is on.
        */
        public double getMutationRate  () {
//		return this.M_rate;
          return mutation.getMutationRate();
        }

        /**
                set the random number seed for mutation.
         */
        public void setRandomNumberSeed(long seed) {
            mutation.setRandomSeed(seed);
            mutation.randNum.setSeed(seed);
        }

        /**
                get the random number seed.
         */
        public long getRandomNumberSeed() {
          return mutation.getRandomSeed();
        }

        /**
                This method returns the description of the various inputs.

                @return the description of the indexed input.
        */
        public String getOutputInfo (int index) {
                switch (index) {
                        case 0: return "      The output is the mutated population.   ";
                        default: return "No such output";
                }
        }

        /**
                This method returns the description of the various inputs.

                @return the description of the indexed input.
        */
        public String getInputInfo (int index) {
                switch (index) {
                        case 0: return "      The input population.   ";
                        default: return "No such input";
                }
        }

        /**
                This method returns the description of the module.

                @return the description of the module.
        */
        public String getModuleInfo () {
                return "<html>  <head>      </head>  <body>    Take the input population and apply mutations randomly to individuals with     rate of mutation equal to the mutation rate property. Mutation rates are     generally very small numbers, but may be increased for some specialized     problems. The property should range from 0.0 to 1.0.  </body></html>";
        }

        public String[] getInputTypes () {
                String[] types = {"ncsa.d2k.modules.core.optimize.ga.Population"};
                return types;
        }

        public String[] getOutputTypes () {
                String[] types = {"ncsa.d2k.modules.core.optimize.ga.Population"};
                return types;
        }

        /**
                Compute the Mu_next only when we start execution.
        */
        public void beginExecution () {
                mutation.Mu_next = (int) Math.ceil (Math.log (mutation.randNum.nextDouble()) / Math.log (1.0 - mutation.getMutationRate()));
                //mutation.Mu_next = (int) Math.ceil (Math.log (Math.random ()) / Math.log (1.0 - mutation.getMutationRate()));
        }

        /**
                Mutate the population by randomly select bits to set to a new random
                value.

                @param outV the output array.
        */
        int Mu_next;
        Random rand = new Random ();
        public void doit () throws Exception {

                Population population = (Population) this.pullInput (0);
                mutation.mutatePopulation(population);
                this.pushOutput (population, 0);
        }

/*        public void mutatePopulation(Population population) {
          int i, j;

          // How many genes in the entire population.
          int totalGenes = population.getNumGenes () * population.size ();
          int numberGenes = population.getNumGenes ();
          double rate = M_rate;   // So the user can change this in the middle of a
                                                          // run.
          // Loop over every gene in the population.
          if (rate > 0.0)
                  while (Mu_next < totalGenes) {

                          // This is the next individual to change.
                          i = Mu_next / numberGenes;

                          // The gene within that individual.
                          j = Mu_next % numberGenes;

                          // Was there a mutation?
                          if (debugging) {
                                  System.out.println ("Mutated -> individual at "+i+" gene at "+j);
                                  population.printIndividual (i);
                          }
                          population.getMember (i).mutateGene (j);
                          if (debugging) {
                                  population.printIndividual (i);
                          }

                          // Next
                          if (rate < 1.0) {
                                  int inc = (int) (Math.ceil (Math.log (Math.random()) / Math.log (1.0 - rate)));
                                  if (inc == 0)
                                          Mu_next += 1;
                                  else
                                          Mu_next += inc;
                          } else
                                  Mu_next += 1;
                  }

          Mu_next = Mu_next - totalGenes;
        }*/

        /**
         * Return the human readable name of the module.
         * @return the human readable name of the module.
         */
        public String getModuleName() {
                return "Mutate";
        }

        /**
         * Return the human readable name of the indexed input.
         * @param index the index of the input.
         * @return the human readable name of the indexed input.
         */
        public String getInputName(int index) {
                switch(index) {
                        case 0:
                                return "population";
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
                                return "population";
                        default: return "NO SUCH OUTPUT!";
                }
        }
}
