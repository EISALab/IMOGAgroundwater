package ncsa.d2k.modules.core.optimize.ga.nsga;


import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.core.modules.*;
import java.io.Serializable;


/**
        This module is only to be used with NsgaPopulations. It performs the
        nondominated elitist selection. This implementation is based of the
        work of Deb, Agrawal, Pratap and Meyarivan titled "A fast Elitist Non-Dominated
        Sorting Genetic Algorithm for Multi-Objective Optimization: NSGA-II".<p>

*/
public class NonDominationElitism extends ncsa.d2k.core.modules.ComputeModule
                         {

        //////////////////////////////////
        // Info methods
        //////////////////////////////////
        /**
                This method returns the description of the various inputs.
                @return the description of the indexed input.
        */
        public String getOutputInfo (int index) {
                switch (index) {
                        case 0: return "      When we are done we simply pass the population along.   ";
                        default: return "No such output";
                }
        }

        /**
                This method returns the description of the various inputs.
                @return the description of the indexed input.
        */
        public String getInputInfo (int index) {
                switch (index) {
                        case 0: return "      This is the population that will be printed.   ";
                        default: return "No such input";
                }
        }

        /**
                This method returns the description of the module.
                @return the description of the module.
        */
        public String getModuleInfo () {
                return "<html>  <head>      </head>  <body>    Prints All This Crap out  </body></html>";
        }

        //////////////////////////////////
        // Type definitions.
        //////////////////////////////////

        public String[] getInputTypes () {
                String[] types = {"ncsa.d2k.modules.core.optimize.ga.NsgaPopulation"};
                return types;
        }

        public String[] getOutputTypes () {
                String[] types = {"ncsa.d2k.modules.core.optimize.ga.NsgaPopulation"};
                return types;
        }

        /**
                Do a nondominated elitist selection (or preselection, since tournament is
                typically used downstream).
                @param outV the array to contain output object.
        */
        public void doit () {
                NsgaPopulation population = (NsgaPopulation) this.pullInput (0);
                this.doNondominatedElitistSelection(population);
                this.pushOutput (population, 0);
        }
        
        public void doNondominatedElitistSelection(NsgaPopulation population) {

                // First do a non-dominated sort and rank everybody.
                population.doNonDominatedSort();

                // At this point each member will have a rank. If this is the first
                // generation, we are done, since elitism will only apply to combined
                // parent and child populations, we have no child population yet.
                if (population.getCurrentGeneration () > 0) {

                        // First create an array with the indices of the members of the
                        // new population.
                        ParetoFront pf = population.getParetoFronts ();
                        int [][] fronts = pf.getFronts ();
                        int [] count = pf.getCounts ();
                        int newPopSize = 0;
                        int sz = population.size ();
                        int [] newpop = new int [sz];
                        for (int i = 0 ; newPopSize < sz ; i++) {
                                int frontSize = count [i];
                                int [] currentFront = fronts [i];
                                population.computeCrowdingDistance (currentFront, frontSize);
                                if ((newPopSize + frontSize) < sz) {

                                        // this entire front fits in the new population.
                                        System.arraycopy (currentFront, 0, newpop, newPopSize, frontSize);
                                        newPopSize += frontSize;
                                } else {

                                        // Sort them.
                                        population.sortIndividuals (population.getCombinedPopulation (),
                                                        currentFront);
                                        System.arraycopy (currentFront, 0, newpop, newPopSize, sz - newPopSize);
                                        newPopSize = sz;
                                }
                        }

                        // Now we have the indices of the individuals who are better than
                        // the others. Recompile the parent population so it is composed
                        // of these members.
                        population.recompilePopulation (newpop);
                }

        }

        /**
         * Return the human readable name of the module.
         * @return the human readable name of the module.
         */
        public String getModuleName() {
                return "";
        }

        /**
         * Return the human readable name of the indexed input.
         * @param index the index of the input.
         * @return the human readable name of the indexed input.
         */
        public String getInputName(int index) {
                switch(index) {
                        case 0:
                                return "Population";
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
