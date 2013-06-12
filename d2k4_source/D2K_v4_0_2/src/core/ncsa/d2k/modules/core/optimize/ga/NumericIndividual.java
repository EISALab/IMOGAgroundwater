package ncsa.d2k.modules.core.optimize.ga;

import java.io.Serializable;
import java.math.*;
import ncsa.d2k.modules.core.optimize.util.*;
import java.util.*;

/**
        This is the individual encoded as an array of booleans, the
        truely binary form closest to the original simple GA.
*/
public class NumericIndividual extends SODoubleSolution
                implements Individual, Serializable, Cloneable {

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

        /** indicates objectives need recomputing. */
        boolean dirty = true;

        /**
                Needs to know how many genes there are to construct.
        */
        public NumericIndividual (DoubleRange [] traits) {
                this (traits, 0);
        }

        public NumericIndividual (DoubleRange [] traits, int numConstraints) {
          super(traits, numConstraints);
        }

        /**
                returns true if the objectives need recomputed.
                @returns true if the objectives need recomputed.
        */
        public boolean isDirty () {
                return dirty;
        }

        /**
         * make sure the dirty flag is reset.
         * @param fit the new fitness value.
         */
        public void setObjective (double fit) {
                super.setObjective (fit);
                dirty = false;
        }

        /**
                set the array of doubles representing the individuals genes.
                @param p the new genes.
        */
        public void setGenes (Object p) {
                this.parameters = (double []) p;
                dirty = true;
        }

        /**
                Copy the given individual.
        */
        public Object clone () {
                NumericIndividual bi = new NumericIndividual (ranges, constraints.length);
                double [] genes = (double []) this.getGenes ();
                double [] bigenes = (double []) bi.getGenes ();
                System.arraycopy (genes, 0, bigenes,
                                        0, genes.length);
                System.arraycopy (constraints, 0, bi.constraints,
                                        0, constraints.length);
                bi.setObjective (this.getObjective ());
                bi.dirty = this.dirty;
                return bi;
        }

        /**
                The gene at position x was chosen for mutation, simply select the new value
                at random from within the range.
                @param x the location to mutate the chomosome.
        */
        public void mutateGene (int x) {
                //this.parameters [x] = ranges [x].getMin () + (Math.random() *
                //                                (ranges [x].getMax () - ranges [x].getMin ()));
                this.parameters [x] = ranges [x].getMin () + (randNum.nextDouble() *
                                                (ranges [x].getMax () - ranges [x].getMin ()));
                this.dirty = true;
        }

        /**
                N point crossover, x contains the crossover points.
                @param x the points to cross at.
        */
        public void crossAt (int [] x, Individual swapee) {
                double [] swap = new double [parameters.length];
                int ct = x.length;
                double [] mom = parameters;
                double [] pop = (double [])swapee.getGenes ();
                for (int i = 1 ; i < ct; i+=2) {

                        // Determin the points of crossover
                        int start = x [i-1];
                        int end = x [i];
                        int swaplength = end - start;

                        // Swap them.
                        System.arraycopy (mom, start, swap, 0, swaplength);
                        System.arraycopy (pop, start, mom, start, swaplength);
                        System.arraycopy (swap, 0, pop, start, swaplength);
                }
                this.dirty = true;
        }

        /**
                Copy the given individual.
                @param cloneMe the individual to copy.
        */
        public void copy (Individual cloneMe) {
                NumericIndividual bi = (NumericIndividual)cloneMe;
                System.arraycopy (bi.getGenes (), 0, this.parameters,
                                        0, this.parameters.length);
                System.arraycopy (bi.constraints, 0, this.constraints,
                                        0, this.constraints.length);
                this.setObjective (bi.getObjective ());
                this.dirty = bi.dirty;
        }

        /**
                Return the array of booleans that represents this individual.
        */
        public Object getGenes () {
                return this.getParameters ();
        }

        /**
                print some representation of this individual.
        */
        public String toString () {
                double [] gns = (double []) this.getGenes ();
                StringBuffer sb = new StringBuffer (1024);
                sb.append ('(');
                for (int i = 0 ; i < gns.length ; i++) {
                        if (i > 0)
                                sb.append (',');
                        sb.append (Double.toString (gns [i]));
                }
                sb.append (')');
                sb.append (':');
                sb.append (Double.toString (this.getObjective ()));
                return sb.toString ();
        }
        /**
         * DC added 3.6.03
         * @param i
         */
        public void printFitness(int i) {
        }

}
