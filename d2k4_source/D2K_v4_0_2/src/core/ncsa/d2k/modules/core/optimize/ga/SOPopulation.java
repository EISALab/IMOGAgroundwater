package ncsa.d2k.modules.core.optimize.ga;

import ncsa.d2k.modules.core.optimize.util.*;
import java.io.Serializable;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;


/**
        This class represents populations of individuals with a single objective
        value.
*/
public class SOPopulation extends Population implements Serializable {
        //
        // These fields define the fitness measure.
        //
        /** stores the average fitness. */
        private double current_avg;

        /** best person. */
        protected int best_individual;

        /** best person. */
        protected int worst_individual;

        /** this is the value that is used to measure the success of the population, by
                default just the best fitness. */
        protected double currentMeasure;

        /** defines teh constraints of the objective values. */
        ObjectiveConstraints objConstraints;

        /** this is the value at which we consider the solution converged upon. */
        double target;

        /**
         * need the ranges for the parameters, the objective value metadata, and
         * the number of members.
         * @param ranges the metadata describes the parameters or attributes.
         * @paraqm objConstraints provides context for the objective function values.
         * @numMembers the number of members to create.
         */
        public SOPopulation (Range [] ranges, ObjectiveConstraints objConstraints, int numMembers, double targ) {
                super (ranges);
                this.target = targ;
                this.objConstraints = objConstraints;
                if (ranges [0] instanceof BinaryRange) {

                        // Set up the members
                        members = new BinaryIndividual [numMembers];
                        nextMembers = new BinaryIndividual [numMembers];
                        for (int i = 0 ; i < numMembers ; i++) {
                                members[i] = new BinaryIndividual ((BinaryRange []) ranges);
                                nextMembers[i] = new BinaryIndividual ((BinaryRange []) ranges);
                        }

                } else if (ranges [0] instanceof DoubleRange) {
                        // Set up the members
                        members = new NumericIndividual [numMembers];
                        nextMembers = new NumericIndividual [numMembers];
                        for (int i = 0 ; i < numMembers ; i++) {
                                members[i] = new NumericIndividual ((DoubleRange []) ranges);
                                nextMembers[i] = new NumericIndividual ((DoubleRange []) ranges);
                        }
                } else if (ranges [0] instanceof IntRange) {

                        /*// Set up the members
                        members = new IntIndividual [numMembers];
                        nextMembers = new IntIndividual [numMembers];
                        for (int i = 0 ; i < numMembers ; i++) {
                                members[i] = new IntIndividual (traits);
                                nextMembers[i] = new IntIndividual (traits);
                        }*/
                } else {
                        System.out.println ("What kind of range is this?");
                }
        }
        
        protected SOPopulation(Range[] ranges, ObjectiveConstraints objConstraints, double targ) {
          super(ranges);
          this.target = targ;
          this.objConstraints = objConstraints;
        }
        
        
        //
        // These are all properties.
        //
        /**
                This method returns the fitness value which indicates convergence.
                @returns the value at which we consider the solution converged.
        */
        public boolean getMaxFlag () {
                return objConstraints.getMax () > objConstraints.getMin ();
        }

        /**
                Compare one member to another. This requires knowledge of the
                fitness function which cannot be supplied here, hence must be
                provided in a subclass.
                @returns 1 if member indexed a is greater than b,
                                0 if they are equal,
                                -1 if member indexed by a is less than b.
        */
        public int compareMembers (Solution a, Solution b) {
                double af = ((SOSolution)a).getObjective ();
                double bf = ((SOSolution)b).getObjective ();
                return objConstraints.compare (af, bf);
        }


        /**
                Set the average fitness, stored for use in other modules
                @param newFitness the new average fitness value
        */
        public void setAverageFitness (double newFitness) {
                this.current_avg = newFitness;
        }

        /**
                This method returns the average fitness value.
                @returns the average fitness value.
        */
        public double getAverageFitness () {
                return this.current_avg;
        }

        //
        // Some maintainence methods.
        //

        /**
                This method returns the average fitness value.
                @returns the average fitness value.
        */
        public double getCurrentMeasure () {
                return this.currentMeasure;
        }

        /**
                set index of individual with the best fitness value.
                @param newFitness the new fitness value
        */
        public void setBestIndividual (int best) {
                this.best_individual = best;
        }

        /**
                Returns the individual which is the best so far.
        */
        public Individual bestIndividual () {
                return members [best_individual];
        }

        /**
                set index of individual with the worst fitness value.
                @param wrst the new worst fitness value
        */
        public void setWorstIndividual (int wrst) {
                this.worst_individual = wrst;
        }

        /**
                Returns the individual which is the best so far.
        */
        public Individual worstIndividual () {
                return members [worst_individual];
        }

        /**
                Returns the list of individuals.
                @Returns the list of individuals.
        */
        public Individual [] getMembers () {
                return members;
        }

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
                if (super.isDone ())
                        return true;

                double best = ((SOSolution)members[best_individual]).getObjective ();
                double toot = objConstraints.compare (best, target);
                if(toot == 1 || toot == 0) {
                        System.out.println ("Fitness("+best
                                +") greater than target("+target+")");
                        System.out.println (this.statusString ());
                        return true;
                }

                double worst = ((SOSolution)members[worst_individual]).getObjective ();
                if (best == worst) {
                        return false;
                }
                return false;
        }


        /**
         * compute statistics that can be used to measure the success of
         * the population.
         */
        public void computeStatistics () {

                SOSolution [] individuals = (SOSolution [])members;
                int length = this.size ();

                // save the best, worst individuals and the avg performance.
                int worst_individual = 0;
                int best_individual = 0;
                double ave_current_perf = individuals [0].getObjective ();
                for (int i = 1; i < length; i++) {

                        // update current statistics
                        ave_current_perf += individuals [i].getObjective ();

                        // Compare to best performer.
                        if (this.compareMembers (this.getMember (best_individual),
                                        this.getMember (i)) < 0)
                                best_individual = i;

                        // compare to worst performer.
                        if (this.compareMembers (this.getMember (worst_individual),
                                        this.getMember (i)) > 0)
                                worst_individual = i;
                }

                // These values used in selection process.
                this.setAverageFitness (ave_current_perf / length);
                this.setBestIndividual (best_individual);
                this.setWorstIndividual (worst_individual);
                this.currentMeasure = individuals [best_individual].getObjective ();
        }

        /**
         * Construct a string representing the current status of the population, best members,
         * maybe worst members, whatever.
         */
        public String statusString () {
                StringBuffer sb = new StringBuffer (1024);
                sb.append ("Current best performance ");
                sb.append (((SOSolution)this.bestIndividual ()).getObjective ());
                sb.append (" looking for ");
                sb.append (this.target);
                sb.append ("\n    -");
                sb.append (this.bestIndividual ());

                sb.append ('\n');
                sb.append ("Current worst performance : ");
                sb.append (((SOSolution)this.worstIndividual ()).getObjective ());
                sb.append ('\n');
                sb.append ("Current average performance : ");
                sb.append (this.getAverageFitness ());
                sb.append ('\n');
                return sb.toString ();
        }
        public double getBestFitness () {
                return objConstraints.getMax ();
        }
        public double getWorstFitness () {
                return objConstraints.getMin ();
        }
        public double getTargetFitness () {
                return this.target;
        }

        /**
         * Returns a representation of of the population in the form of a
         * table, where each row represents one individual, one gene per column, and the last
         * column containing the objective value.
         * @returns a table represeting the population.
         */
        public Table getTable () {
                int numTraits = traits.length;
                int popSize = this.size ();
               //BASIC 3 TableImpl vt = null;
                MutableTableImpl vt = null;
                if (members instanceof NumericIndividual []) {
                    double [][] dc = new double [numTraits+1][popSize];
                        NumericIndividual [] nis = (NumericIndividual []) members;

                        // Populate the double arrays
                        for (int i = 0 ; i < popSize ; i++) {
                                double [] genes = (double []) nis [i].getGenes ();
                                for (int j = 0 ; j < numTraits ; j++)
                                        dc [j][i] = genes [j];
                                dc [numTraits] [i] = nis [i].getObjective();
                        }

                        // Now make the table
                       // BASIC3 vt = (TableImpl) DefaultTableFactory.getInstance().createTable(0);
                        vt =  new MutableTableImpl(0);
                        for (int i = 0 ; i < numTraits ; i++) {
                                DoubleColumn col = new DoubleColumn (dc [i]);
                                col.setLabel (traits [i].getName ());
                                vt.addColumn (col);
                        }
                        DoubleColumn col = new DoubleColumn (dc [numTraits]);
                        col.setLabel ("Objective");
                        vt.addColumn (col);
                } else if (members instanceof BinaryIndividual []) {
                        numTraits = ((BinaryRange)traits[0]).getNumBits ();
                        BinaryIndividual [] nis = (BinaryIndividual []) members;
                        boolean [][] dc = new boolean [numTraits][popSize];
                        double [] objs = new double [popSize];

                        // Populate the double arrays
                        for (int i = 0 ; i < popSize ; i++) {
                                boolean [] genes = (boolean []) nis [i].getGenes ();
                                for (int j = 0 ; j < numTraits ; j++)
                                        dc [j][i] = genes [j];
                                objs [i] = nis [i].getObjective();
                        }

                        // Now make the table
                      //BASIC3  vt = (TableImpl)DefaultTableFactory.getInstance().createTable(0);
                        vt =  new MutableTableImpl(0);
                        for (int i = 0 ; i < numTraits ; i++) {
                                BooleanColumn col = new BooleanColumn (dc [i]);
                                col.setLabel (Integer.toString(i));
                                vt.addColumn (col);
                        }
                        DoubleColumn col = new DoubleColumn (objs);
                        col.setLabel ("Objective");
                        vt.addColumn (col);
                }
                return vt;
        }
}
