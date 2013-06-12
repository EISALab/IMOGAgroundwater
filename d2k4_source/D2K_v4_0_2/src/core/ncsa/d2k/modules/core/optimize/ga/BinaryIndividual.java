package ncsa.d2k.modules.core.optimize.ga;

import ncsa.d2k.modules.core.optimize.util.*;
import java.io.Serializable;


/**
        This is the individual encoded as an array of booleans, the
        truely binary form closest to the original simple GA.
*/
public class BinaryIndividual extends SOBinarySolution implements Individual, Serializable, Cloneable {

        /** indicates that the objectives need computed. */
        boolean dirty = true;

        /**
                Needs to know how many genes there are to construct.
        */
        public BinaryIndividual (BinaryRange [] ranges) {
                this (ranges, 0);
        }
        
        public BinaryIndividual (BinaryRange [] ranges, int numConstraints) {
          super(ranges, numConstraints);
        }

        /**
                returns true if the objectives need recomputed.
                @returns true if the objectives need recomputed.
        */
        public boolean isDirty () {
                return dirty;
        }

        /**
                The gene at position x was chosen for mutation, just flip the
                bit.
                @param x the location to mutate the chomosome.
        */
        public void mutateGene (int x) {
                this.parameters [x] = !this.parameters [x];
                dirty = true;
        }

        /**
                N point crossover, x contains the crossover points.
                @param x the points to cross at.
        */
        public void crossAt (int [] x, Individual swapee) {
                boolean [] swap = new boolean [parameters.length];
                int ct = x.length;
                boolean [] mom = this.parameters;
                boolean [] pop = (boolean [])swapee.getGenes ();
                for (int i = 1 ; i < ct; i++) {

                        // Determin the points of crossover
                        int start = x [i-1];
                        int end = x [i];
                        int swaplength = end - start;

                        // Swap them.
                        System.arraycopy (mom, start, swap, 0, swaplength);
                        System.arraycopy (pop, start, mom, start, swaplength);
                        System.arraycopy (swap, 0, pop, start, swaplength);
                }
                dirty = true;
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
                Copy the given individual.
                @param cloneMe the individual to copy.
        */
        public void copy (Individual cloneMe) {
                BinaryIndividual bi = (BinaryIndividual)cloneMe;
                System.arraycopy (bi.getGenes (), 0, this.parameters,
                                        0, parameters.length);
                System.arraycopy (bi.constraints, 0, this.constraints,
                                        0, constraints.length);
                this.setObjective (bi.getObjective ());
                dirty = bi.dirty;
        }

        /**
                clone this object.
                @returns an exact replica of this object.
        */
        public Object clone () {
                BinaryIndividual bi = new BinaryIndividual (ranges, constraints.length);
                System.arraycopy (parameters, 0, bi.getGenes (),
                                        0, parameters.length);
                System.arraycopy (constraints, 0, bi.constraints,
                                        0, constraints.length);
                bi.setObjective (this.getObjective ());
                bi.dirty = dirty;
                return bi;
        }

        /**
                Return the array of booleans that represents this individual.
                @returns the array of booleans that represents this individual.
        */
        public Object getGenes () {
                return this.parameters;
        }

        /**
                set the array of booleans representing the individuals genes.
                @param p the new genes
        */
        public void setGenes (Object p) {
                this.parameters = (boolean []) p;
                dirty = true;
        }

        /**
                print some representation of this individual.
        */
        public String toString () {
                boolean [] gns = this.parameters;
                StringBuffer sb = new StringBuffer (1024);
                for (int i = 0 ; i < gns.length ; i++)
                        if (!gns[i])
                                sb.append ('0');
                        else
                                sb.append ('1');
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
