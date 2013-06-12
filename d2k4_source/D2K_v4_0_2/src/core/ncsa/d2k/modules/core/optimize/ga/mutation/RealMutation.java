package ncsa.d2k.modules.core.optimize.ga.mutation;

import ncsa.d2k.modules.core.optimize.ga.*;

/**
                This module does simulated binary crossover. It will operate
                only on real number attributes, but simulates the process of
                binary crossover on binary individuals. For more information on
                Simulated Binary Crossover see "Self-Adaptive Genetic Algorithms with Simulated
                Binary Crossover", Deb, Beyer (1995).
*/
public class RealMutation extends MutateModule {

        ///////////////////////////////
        // Properties.
        ///////////////////////////////
/*	boolean debugging = false;
        public boolean getDebugging () { return debugging; }
        public void setDebugging (boolean nd) { debugging = nd; }


        Random rand = new Random ();
        */
       
       protected void createMutation() {
         mutation = new RealMutationObj();
       }

        /** a measure of how different the children may be from the parents. */
//	protected double n = 2.0;

        /**
                sets n, a measure of how different the children may be from the parents. Higher values are
                more different.
                @param score is a boolean indicating if rankFlag is on or off.
        */
        public void setN (double score) {
//		n = score;
          ((RealMutationObj)mutation).setN(score);
        }

        /**
                get the rankFlag.
                @returns true if rankFlag is on.
        */
        public double getN () {
        //	return n;
          return ((RealMutationObj)mutation).getN();
        }

        /** This is the probability that a gene will mutate. */
//	protected double m_rate = 0.1;

        /**
                Sets the rate at which genes are mutated.
                @param score the new mutation rate..
        */
/*	public void setMutationRate (double score) {
                m_rate = score;
        }*/

        /**
                get the rankFlag.
                @returns true if rankFlag is on.
        */
/*	public double getMutationRate () {
                return m_rate;
        }*/

        //////////////////////////////////
        // Info methods
        //////////////////////////////////
        /**
                This method returns the description of the various inputs.

                @return the description of the indexed input.
        */
/*	public String getOutputInfo (int index) {
                switch (index) {
                        case 0: return "      The resulting output population which has been crossed.   ";
                        default: return "No such output";
                }
        }*/

        /**
                This method returns the description of the various inputs.

                @return the description of the indexed input.
        */
/*	public String getInputInfo (int index) {
                switch (index) {
                        case 0: return "      This is the input     <I>population </I>    , containing the population which is to be     <I>mutated </I>    .   ";
                        default: return "No such input";
                }
        }*/

        /**
                This method returns the description of the module.

                @return the description of the module.
        */
/*	public String getModuleInfo () {
                return "<html>  <head>      </head>  <body>    This module will take the given population of Individuals and mate them,     mutate them at using a mutation rate applied to each individual gene in     the population.<br><br>For this type of crossover, there are two     parameters. <i>Mutation Rate </i> is the probability that each individual     gene will be mutated. <i>N </i> <i> </i> is a factor used to define the     magnitude of the mutations, smaller values of N case small mutations,     larger values cause larger mutations. Typical values will range from .1 to     100.  </body></html>";
        }*/

        //////////////////////////////////
        // Type definitions.
        //////////////////////////////////

/*	public String[] getInputTypes () {
                String[] types = {"ncsa.d2k.modules.core.optimize.ga.Population"};
                return types;
        }*/

/*	public String[] getOutputTypes () {
                String[] types = {"ncsa.d2k.modules.core.optimize.ga.Population"};
                return types;
        }*/

        /**
                Do the selection.
                @param outV the array to contain output object.
        */
        public void doit () throws Exception {
                // Our input argument is the population.
                Population population = (Population) this.pullInput (0);
                mutation.mutatePopulation(population);
                this.pushOutput (population, 0);
        }

/*        public void mutatePopulation(Population population) {
          Individual [] individuals = (Individual []) population.getMembers ();

          double oneOverNPlus1 = 1.0 / (n + 1.0);

          // Loop over the entire population.
          int last = population.size ();
          Range [] traits = population.getTraits ();

          // for each member of the population,
          for (int i = 0; i < last; i++) {
                  double [] genes = (double []) individuals [i].getGenes ();

                  // for each gene of the individual
                  for (int k = 0 ; k < genes.length; k++) {

                          if (rand.nextDouble () < m_rate) {
                                  DoubleRange dr = (DoubleRange) traits [k];
                                  double gene = genes [k];
                                  double paramLower = dr.getMin ();
                                  double paramUpper = dr.getMax ();

                                  // Swap the indicated genes.
                                  if (debugging) {
                                          System.out.println ("--------------------");
                                          System.out.println ("Before SBX");
                                          population.printIndividual (i);
                                          population.printIndividual (i+1);
                                  }


                                  // compute beta sub q
                                  double delta;
                                  if ((gene - paramLower) < (paramUpper - gene))
                                      delta = (gene - paramLower)/(paramUpper - paramLower);
                                  else
                                      delta = (paramUpper - gene)/(paramUpper - paramLower);

                                  double u = rand.nextDouble ();
                                  double deltaq;
                                  if (u <= 0.5) {
                                      double xy = 1 - delta;
                                      double val = 2 * u + (1 - 2 * u) * (Math.pow (xy, (n + 1)));
                                      deltaq =  Math.pow (val,oneOverNPlus1) - 1;
                                  } else {
                                      double xy = 1 - delta;
                                      double val = 2 * (1 - u) + 2 * (u - 0.5) * (Math.pow (xy, (n + 1)));
                                      deltaq = 1 - (Math.pow (val, oneOverNPlus1));
                                  }

                                  /*Change the value for the parent */
/*                                  genes [k] = gene + deltaq * (paramUpper - paramLower);
                              individuals [i].setGenes (genes);


                                  // Debugging
                                  if (debugging) {
                                          System.out.println ("-------After RealMutation ");
                                          population.printIndividual (i);
                                  }
                          }
                  }
          }
        }*/

        /**
         * Return the human readable name of the module.
         * @return the human readable name of the module.
         */
        public String getModuleName() {
                return "Real Mutation";
        }

        /**
         * Return the human readable name of the indexed input.
         * @param index the index of the input.
         * @return the human readable name of the indexed input.
         */
/*	public String getInputName(int index) {
                switch(index) {
                        case 0:
                                return "population";
                        default: return "NO SUCH INPUT!";
                }
        }*/

        /**
         * Return the human readable name of the indexed output.
         * @param index the index of the output.
         * @return the human readable name of the indexed output.
         */
/*	public String getOutputName(int index) {
                switch(index) {
                        case 0:
                                return "population";
                        default: return "NO SUCH OUTPUT!";
                }
        }*/
}
