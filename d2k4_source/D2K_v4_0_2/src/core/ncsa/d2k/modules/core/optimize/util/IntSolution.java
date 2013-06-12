package ncsa.d2k.modules.core.optimize.util;

import java.io.Serializable;
import java.util.*;

/**
        Represents a solution in the a space where the parameters
        are ints.
*/
abstract public class IntSolution implements Solution, java.io.Serializable  {

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

        /** the parameters. */
        protected int [] parameters;

        /** the ranges with describe the parameters. */
        protected IntRange [] ranges;

        protected double [] constraints;

        public IntSolution (IntRange [] ranges) {
          this (ranges, 0);
        }

        /**
         * the constructor takes a list of IntRanges for the parameters,
         * and a list of objective constraints for the objective.
         */
        public IntSolution (IntRange [] ranges, int numConstraints) {
                constraints = new double[numConstraints];
                this.ranges = ranges;
                int number = ranges.length;
                this.parameters = new int [number];
                for (int j = 0; j < number; j++) {
                        double tmp = randNum.nextDouble(); //Math.random ();
                        parameters [j] = (int) (ranges [j].getMinInt () + (tmp *
                                                (double)((ranges [j].getMaxInt () - ranges [j].getMinInt ()))));
                }
        }

        public int getNumConstraints() {
          return constraints.length;
        }

        public void setConstraint(double d, int i) {
          constraints[i] = d;
        }

        public double getConstraint(int i) {
          return constraints[i];
        }

        /**
         * returns the array of ints containing the parameters to the
         * solution.
         * @returns parameters the list of parameters yielding the solution.
         */
        public Object getParameters () {
                return parameters;
        }
        /**
         * sets the array of ints containing the parameters to the
         * solution.
         * @param parameters the list of parameters yielding the solution.
         */
        public void setParameters (Object params) {
                parameters=(int[])params;
        }
        /**
                returns the int params as doubles
        **/

        public double getDoubleParameter(int paramIndex){
                return (double)parameters[paramIndex];
        }

        /**
                rounds the double that is passed in to the nearest int,
                puts it in the param array
        */

        public void setDoubleParameter(double newParam, int paramIndex){
                parameters[paramIndex]=(int)(newParam+.5);
        }
        abstract public Object clone();

        public double[] toDoubleValues() {
          int numParameters = ranges.length;
          double[] retVal = new double[numParameters];
          for(int i = 0; i < numParameters; i++) {
            retVal[i] = getDoubleParameter(i);
          }
          return retVal;
        }
}
