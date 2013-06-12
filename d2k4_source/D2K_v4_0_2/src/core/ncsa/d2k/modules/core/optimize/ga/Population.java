package ncsa.d2k.modules.core.optimize.ga;

import ncsa.d2k.modules.core.optimize.util.*;
import java.io.Serializable;
import ncsa.d2k.modules.core.datatype.table.*;
import java.util.*;

/**
        The population
        is responsible for defining the maximum, minimum, and target fitness values. These values
        are typically defined in the subclass of <code>PopulationPrep</code> module which creates
        the population. The individuals are all sorted in this class as well, in an array. The
        number of generations to produce before quiting is also stored here.<p>

        There are several methods for printing, and comparing individuals in the population.
*/
abstract public class Population implements Serializable {
        //
        // This is the population.
        //
        // VARIABLES FOR THE RANDOM NUMBER GENERATOR
        protected long randomSeed = 8500;
        public Random randNum = new Random(randomSeed);

       // get random number seed
        public long getRandomSeed (){
          return randomSeed;
        }

        // set random number seed
        public void setRandomSeed (long seed){
          randomSeed = seed;
        }
        /** the people. */
        protected Individual [] members;

        /** the next generation people. */
        protected Individual [] nextMembers;

        /** generation counter. */
        protected int currentGeneration = 0;

        /** max generations. */
        private int maxGenerations = 1000;

        /** these ranges describe the parameters of the solution space. */
        protected Range [] traits;

        int numGenes;
        public Population (Range [] traits) {
                this.traits = traits;
                if (traits [0] instanceof BinaryRange) {
                        numGenes = 0;
                        for (int i = 0 ; i < traits.length ;i++)
                                numGenes += ((BinaryRange) traits [i]).getNumBits ();
                } else
                        numGenes = traits.length;
        }

        //
        // Getters only.
        //
        /**
                The number of genes in each individual.
                @returns the number of genes.
        */
        public int getNumGenes () {
                return  numGenes;
        }

        /**
                Get the individual as the index i.
                @param i the index of the member individual.
        */
        public Individual getMember (int i) {
                return members [i];
        }
        public Individual getNextMember (int i) {
            return nextMembers [i];
        }

        ////////////////////////////
        // Added by Meghna
        /**
         *  set members for member i
         *  and next member i
         */
        public void setMember (Individual ind , int i) {
                members [i] = ind;
        }
        public void setNextMember (Individual ind , int i) {
                nextMembers [i] = ind;
        }
        /////////////////////////////
        /**
                Returns the traits that describe the possible ranges of the parametric
                values.
                @returns the traits that describe the possible ranges of the parametric
                values.
        */
        public Range [] getTraits () {
                return traits;
        }

        /**
                Returns the number of generations produced before we give up if
                we can't find a solution.
                @returns the number of generations produced before we give up.
        */
        public int getMaxGenerations () {
                return this.maxGenerations;
        }

        /**
                returns the current generation.
                @returns the current generation.
        */
        public int getCurrentGeneration () {
                return this.currentGeneration;
        }

        /**
                Set the number of generations befor we give up..
                @param gens the number of generations before we give up.
        */
        public void setMaxGenerations (int gens) {
                maxGenerations = gens;
        }

        //
        // Some maintainence methods.
        //

        /**
                Returns the list of individuals.
                @Returns the list of individuals.
        */
        public Individual [] getMembers () {
                return members;
        }
        ////////////////////////////////
        // Added by Meghna

        /**
                Returns the list of next individuals.
                @Returns the list of next individuals.
        */
        public Individual [] getNextMembers () {
                return nextMembers;
        }

        /**
                Sets the list of individuals.
        */
        public void setMembers (Individual [] membs) {
                members = membs;
        }

        /**
                Sets the list of individuals.
        */
        public void setNextMembers (Individual [] membs) {
                nextMembers = membs;
        }
        ////////////////////////////////

        /**
                REturns the number of individuals in the population.
                @returns the number of individuals in the population.
        */
        public int size () {
                return members.length;
        }

        /**
                Individuals must supply the toString method so that
                they may be printed in an intelligable form.
                @param i the index of the member to print.
        */
        public void printIndividual (int i) {
                System.out.println (members [i].toString ());
        }

        /**
                Print some representation of all the values and their fitness.
        */
        public void printAll () {
                for (int i = 0 ; i < members.length ; i++)
                        this.printIndividual (i);
        }

        /**
                Increment the generation counter.
        */
        protected void	gotoNextGeneration(){
                currentGeneration++;

        }

        /**
                Are we done?
                @returns true if we have reached our objective, or the max iterations,
                otherwise it returns false.
        */
        public boolean isDone(){

                // Did we just run out of time?
                if(currentGeneration >= maxGenerations) {
                        System.out.println ("Max gens.");
                        return true;
                }
                return false;
        }

        /**
                Replace the current population with one consisting of individuals
                indicated by the list of indices given. This population will be duplicated
                into the parent population.
                @param selected the array of indices of selected individuals.
        */
        public void makeNextGeneration (int [] selected) {
                int popSize = this.size ();

                // finally, form the new population
                for (int i = 0; i < popSize; i++)
                        nextMembers [i].copy(members[i]);
                        //nextMembers [i].copy (members [selected [i]]);

                // Now we have the population of parents, copy the new population
                // into the mating pool for crossover. In this way, we save a copy
                // of the parents, sometimes needed for niching.
                for (int i = 0; i < popSize; i++)
                        members [i].copy (nextMembers [selected[i]]);
                this.gotoNextGeneration ();
        }

        /**
                For some operations it is useful to recall the parent population to the current
                generation. This method will return the last population. Note: <b>This population
                is produced after selection, and is undefined until after selection has taken
                place.</b>
                @returns the last population.
        */
        public Individual [] getParents () {
                return nextMembers;
        }

        /**
                quick sort the population on the basis of fitness, and return an array
                of integers that indicates the order of the Individuals from best to worst.
                @param populus the population to sort.
        */
        public int [] sortIndividuals () {
                int [] order = new int [this.size ()];
                for (int i = 0 ; i < order.length ; i++) order [i] = i;
                return this.sortIndividuals (members, order);
        }

        /**
                quick sort the population on the basis of fitness, and return an array
                of integers that indicates the order of the Individuals from best to worst.
                @param populus the population to sort.
        */
        public int [] sortIndividuals (int [] order) {
                this.quickSort (members, 0, order.length-1, order);
                return order;
        }

        /**
                quick sort the population on the basis of fitness, and riseturn an array
                of integers that indicates the order of the Individuals from best to worst.
                @param populus the population to sort.
        */
        public int [] sortIndividuals (Solution [] populus, int [] order) {
                this.quickSort (populus, 0, order.length-1, order);
                return order;
        }

        /**
                This is the recursive quicksort procedure.
                @param populus the individuals to sort
                @param l the left starting point.
                @param r the right end point.
                @param order the list of indices of individuals to sort.
        */
        private void quickSort(Solution [] populus, int l, int r, int [] order) {

                // This is the (poorly chosen) pivot value.
                Individual pivot = (Individual) ((Individual) populus [order [(r + l) / 2]]).clone ();

                // from position i=l+1 start moving to the right, from j=r-2 start moving
                // to the left, and swap when the fitness of i is more than the pivot
                // and j's fitness is less than the pivot
                int i = l;
                int j = r;
                while (i <= j) {
                        while ((i < r) && (this.compareMembers (populus [order [i]], pivot) > 0))
                                i++;
                        while ((j > l) && (this.compareMembers (populus [order [j]], pivot) < 0))
                                j--;
                        if (i <= j) {
                                int swap = order [i];
                                order [i] = order [j];
                                order [j] = swap;
                                i++;
                                j--;
                        }
                }

                // sort the two halves
                if (l < j)
                        quickSort(populus, l, j, order);
                if (i < r)
                        quickSort(populus, i, r, order);
        }

        /**
                Simply shuffle the values in the integer array. This is used to
                shuffle the order of the members.
                @param array the array of integers to shuffle.
        */
        public void shuffleIndices (int [] array) {

                // randomly shuffle pointers to new structures
                int popSize = array.length;
                for (int i=0; i < array.length; i++) {
                        //int j = (int) (Math.random () * (popSize-1));
                        int j = (int) (randNum.nextDouble() * (popSize-1));
                        int temp = array[j];
                        array[j] = array[i];
                        array[i] = temp;
                }
        }

        abstract public double getBestFitness ();
        abstract public double getWorstFitness ();
        abstract public double getTargetFitness ();
        abstract public double getCurrentMeasure ();
        /**
         * compute statistics that can be used to measure the success of
         * the population.
         */
        abstract public void computeStatistics ();

        /**
         * Construct a string representing the current status of the population, best members,
         * maybe worst members, whatever.
         */
        abstract public String statusString ();

        /**
         * compare two individuals of the population. This is relegated to the subclass.
         * @returns 1 if a is better than b, -1 if b is better than a, 0 otherwise.
         */
        abstract public int compareMembers (Solution a, Solution b);

        /**
         * converts the population into a table for viewing or statistical measure.
         */
        abstract public Table getTable ();
}
