package ncsa.d2k.modules.core.optimize.ga.nsga;

import ncsa.d2k.modules.core.optimize.util.*;
import ncsa.d2k.modules.core.optimize.ga.*;
import java.io.Serializable;
import java.math.*;


/**
        This is the individual encoded as an array of booleans, the
        truely binary form closest to the original simple GA. Also supports
        multi objective solutions.
*/
public class MONumericIndividual extends MODoubleSolution
                implements NsgaSolution, Individual, Serializable, Cloneable {

        /** the rank corresponds to the index of the front it is in. */
        int rank = 0;

        /** this is a measure of how closely related this individual it to it's
         *  nearest neighbors.*/
        double crowding = 0.0;

        boolean dirty = true;

        /** the constraint. */
        //double constraint = 0.0;


        /**
                Needs to know how many genes there are to construct.
                @param ranges the range metadata for the parameters.
                @param numObjectives the number of objectives values.
        */
        public MONumericIndividual (DoubleRange [] ranges, ObjectiveConstraints [] oc) {
                //super (ranges, oc);
                this(ranges, oc, 0);
        }

        /**
                Needs to know how many genes there are to construct.
                @param ranges the range metadata for the parameters.
                @param numObjectives the number of objectives values.
        */
        public MONumericIndividual (DoubleRange [] ranges,
                                    ObjectiveConstraints [] oc, int numConstraints) {
                super (ranges, oc, numConstraints);
        }


        /**
                returns true if the objectives need recomputed.
                @returns true if the objectives need recomputed.
        */
        public boolean isDirty () {
                return dirty;
        }

        /**
         * returns the constraint value, representing a constraint violation.
         * @returns the constraint value, representing degree constraint violation.
         */
/*        public double getConstraint () {
                return this.constraint;
        }

        /**
         * sets the constraint value.
         * @param constr the new constraint value.
         */
/*        public void setConstraint (double constr) {
                this.constraint = constr;
        }*/



        /**
                set the array of doubles representing the individuals genes.
                @param p the new genes.
        */
        public void setGenes (Object p) {
                this.parameters = (double []) p;
                dirty = true;
        }

        /**
                The gene at position x was chosen for mutation, pick a random number.
                @param x the location to mutate the chomosome.
        */
        public void mutateGene (int x) {
                this.parameters [x] = this.ranges [x].getMin () + (Math.random() *
                                                (ranges [x].getMax () - ranges [x].getMin ()));
                dirty = true;
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
                dirty = true;
        }

        /**
                Copy the given individual.
                @param cloneMe the individual to copy.
        */
        public void copy (Individual cloneMe) {
                MONumericIndividual bi = (MONumericIndividual)cloneMe;
                System.arraycopy (bi.getGenes (), 0, this.parameters,
                                        0, this.parameters.length);
                System.arraycopy (bi.objectives, 0, this.objectives,
                                        0, objectives.length);
                System.arraycopy (bi.constraints,  0, this.constraints,
                                  0, constraints.length);
                this.setRank (bi.rank);
                this.setCrowdingDistance (bi.crowding);
                this.dirty = bi.dirty;
        }

        /**
         * Clone the object.
         */
        public Object clone () {
                int numConstraints = constraints.length;
                MONumericIndividual bi = new MONumericIndividual (this.ranges,
                    objectiveConstraints, numConstraints);
                System.arraycopy (this.parameters, 0, bi.getGenes (),
                                        0, this.parameters.length);
                System.arraycopy (this.objectives, 0, bi.objectives,
                                        0, objectives.length);
                System.arraycopy (this.constraints , 0, bi.constraints,
                                        0, constraints.length);
                bi.setRank (this.rank);
                bi.setCrowdingDistance (this.crowding);
                bi.dirty = this.dirty;
                return bi;
        }


        /**
                Return the array of booleans that represents this individual.
        */
        public Object getGenes () {
                return this.parameters;
        }

        /**
                print some representation of this individual.
        */
        public String toString () {
                double [] gns = this.parameters;
                StringBuffer sb = new StringBuffer (1024);
                sb.append ("R=");
                sb.append (this.rank);
                sb.append (" CD=");
                sb.append (this.crowding);
                sb.append ('(');
                for (int i = 0 ; i < gns.length ; i++) {
                        if (i > 0)
                                sb.append (',');
                        sb.append (Double.toString (gns [i]));
                }
                sb.append (')');
                sb.append (':');
                for (int i = 0 ; i < objectives.length ;i++) {
                        if (i > 0)
                                sb.append (',');

                        if (dirty)
                                sb.append ("dirty");
                        else
                                sb.append (Double.toString (objectives [i]));
                }
                return sb.toString ();
        }

        /**
         * make sure the dirty flag is reset.
         * @param fit the new fitness value.
         */
        public void setObjective (int which, double fit) {
                super.setObjective (which, fit);
                dirty = false;
        }

        /**
         * Get the rank.
         * @returns the rank.
         */
        public int getRank () {
                return rank;
        }

        /**
         * set the rank
         * @param rank the new rank.
         */
        public void setRank (int rank) {
                this.rank = rank;
        }

        /**
         * Get the crowding distance.
         * @returns the crowding factor.
         */
        public double getCrowdingDistance () {
                return crowding;
        }

        /**
         * set the crowding
         * @param crwd the new crowding distance.
         */
        public void setCrowdingDistance (double crwd) {
                this.crowding = crwd;
        }
        /**
         * DC added 3.6.03
         * @param i
         */
        public void printFitness(int i) {
        }

}
