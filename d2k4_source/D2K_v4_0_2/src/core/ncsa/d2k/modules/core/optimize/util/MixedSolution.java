package ncsa.d2k.modules.core.optimize.util;

import java.io.Serializable;
import java.util.*;

/**
        Represents a solution in the a space where the parameters are various
        data types, but are ultimately represented by doubles.
*/
abstract public class MixedSolution implements Solution, java.io.Serializable  {

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
        protected double [] parameters;


        /** the ranges with describe the parameters. */
        protected Range [] ranges;

        protected double [] constraints;

        public MixedSolution (Range [] ranges) {
          this(ranges, 0);
        }

        /**
         * the constructor takes a list of Ranges for the parameters,
         * and a list of objective constraints for the objective.
         */
        public MixedSolution (Range [] ranges, int numConstraints) {
              constraints = new double[numConstraints];
                this.ranges = ranges;
                int number = ranges.length;
                this.parameters = new double [number];
                for (int j = 0; j < number; j++) {
                        double initDouble = randNum.nextDouble(); // Math.random ();
                        initDouble = ranges [j].getMin () + (initDouble *
                                                (ranges [j].getMax () - ranges [j].getMin ()));
                        setDoubleParameter(initDouble, j);
                }

        }
        public MixedSolution(){}

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
         * returns the array of doubles containing the parameters to the
         * solution.
         * @param parameters the list of parameters yielding the solution.
         */
        public Object getParameters () {
                return parameters;
        }

        /**
        * sets the parameters to a new double array
        *@param parameters the list of parameters to the solution
        */
        public void setParameters(Object params){
                parameters=(double[])params;
        }
        /**
                just returns the parameter as it is
        **/

        public double getDoubleParameter(int paramIndex){
                return parameters[paramIndex];
        }

        /**
                assigns the new double as is to the parameter set
        */

        public void setDoubleParameter(double newParam, int paramIndex){
                switch(ranges[paramIndex].getType()){
                        case(Range.INTEGER):{
                                int i=(int)(newParam+.5);
                                parameters[paramIndex]=i;
                                break;
                        }
                        case(Range.DOUBLE):{
                                parameters[paramIndex]=newParam;
                                break;
                        }
                        case(Range.BINARY):{
                                parameters[paramIndex]=(int)(newParam+.5);
                                break;
                        }
                        default:{return;}
                }
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
