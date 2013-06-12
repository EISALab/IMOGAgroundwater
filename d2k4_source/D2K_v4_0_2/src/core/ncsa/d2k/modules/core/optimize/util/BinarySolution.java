package ncsa.d2k.modules.core.optimize.util;

import java.io.Serializable;
import java.util.*;

/**
        Represents a solution in the a space where the parameters
        are bits.
*/
abstract public class BinarySolution implements Solution, java.io.Serializable  {

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
        protected boolean [] parameters;

        /** the ranges with describe the parameters. */
        protected BinaryRange [] ranges;

        protected double [] constraints;

        public BinarySolution (BinaryRange [] ranges) {
          this(ranges, 0);
        }

        /**
         * the constructor takes a list of BinaryRanges for the parameters,
         * and a list of objective constraints for the objective.
         */
        public BinarySolution (BinaryRange [] ranges, int numConstraints) {
                constraints = new double[numConstraints];
                this.ranges = ranges;
                int number = 0;
                for (int i = 0 ; i < ranges.length ;i++)
                        number += ranges [i].getNumBits ();

                this.parameters = new boolean [number];
                for (int j = 0; j < number; j++) {
                        double tmp = randNum.nextDouble(); //Math.random ();
                        parameters [j] = tmp > 0.5 ? true : false;
                }
        }

        /**
         * the constructor takes a list of BinaryRanges for the parameters,
         * and a list of objective constraints for the objective, and a random number seed.
         */
        public BinarySolution (BinaryRange [] ranges, int numConstraints, long randSeed) {
                constraints = new double[numConstraints];
                this.ranges = ranges;
                this.randomSeed = randSeed;
                this.randNum = new Random(randomSeed);
                int number = 0;
                for (int i = 0 ; i < ranges.length ;i++)
                        number += ranges [i].getNumBits ();

                this.parameters = new boolean [number];
                for (int j = 0; j < number; j++) {
                        double tmp = randNum.nextDouble(); //Math.random ();
                        parameters [j] = tmp > 0.5 ? true : false;
                }
        }

        public BinarySolution(){}

        public int getNumConstraints() {
          return constraints.length;
        }

        public void setConstraint(double val,  int i) {
          constraints[i] = val;
        }

        public double getConstraint(int i) {
          return constraints[i];
        }

        /**
         * returns the array of booleans containing the parameters to the
         * solution.
         * @returns parameters the list of parameters yielding the solution.
         */
        public Object getParameters () {
                return parameters;
        }
        /**
         * sets the array of booleans containing the parameters to the
         * solution.
         * @param parameters the list of parameters yielding the solution.
	 *                     	 *                             	 *			Must be a boolean array
         */
        public void setParameters (Object params) {
                parameters=(boolean[])params;
        }

        /*
                returns an boolean in the parameters array
                as a double (either 0 or 1)
        */
        public double getDoubleParameter(int i){
                if(parameters[i])
                        return 1.0;
                return 0.0;
        }

        /*
                set any double to the boolean,
                ***will set any number between 0 and .499...
                ***to 0, .5 to 1 to 1. (anything else
                is out of range and should not be assigned
        */
        public void setDoubleParameter(double newParam, int paramIndex){

                parameters[paramIndex]=(newParam>.5);
        }

        abstract public Object clone();

        /**
         * Get the values of the parameters as doubles: 0 for false, 1 for true
         * @return
         */
        public double[] toDouble() {
          boolean [] params = (boolean[])this.getParameters ();
          double [] dparams = new double [params.length];
          for (int i=0 ; i < params.length ; i++) {
                  if(params[i]){
                     dparams[i] = 1.0;
                  }
                  else{
                     dparams[i] = 0.0;
                  }
          }
          return dparams;
        }

        public double[] toDoubleValues() {
          boolean[] params = (boolean[])this.getParameters();
          int numTraits = ranges.length;
          double[] retVal = new double[numTraits];

          int curPos = 0;
          for (int k = 0; k < numTraits; k++) {
            BinaryRange binaryRange = ranges[k];
            int numBits = binaryRange.getNumBits();
            double num = 0.0d;
            double max = binaryRange.getMax();
            double min = binaryRange.getMin();
            double precision = binaryRange.getPrecision();

            double interval = (max - min) * precision;

            // this is one trait
            for (int l = 0; l < numBits; l++) {
              if (params[curPos] != false) {
                num = num + Math.pow(2.0, l);
              }
              curPos++;
            }

            // if it is above the max, scale it down
            num = num * precision + min;
            if (num > max) {
              num = max;
            }
            if (num < min) {
              num = min;
            }
            retVal[k] = num;
          }

          return retVal;
        }
}
