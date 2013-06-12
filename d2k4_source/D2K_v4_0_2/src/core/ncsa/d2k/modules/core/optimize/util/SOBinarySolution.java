package ncsa.d2k.modules.core.optimize.util;

/**
 * This solution object is used in solution spaces where
 * only one objective function value is needed. The parameters
 * used here are all binary.
 */
public class SOBinarySolution extends BinarySolution implements SOSolution, java.io.Serializable {

        /** This is the single objective value. */
        private double objective = 0.0;
        
        /**
         * constructor takes the ranges which are passed to the
         * superclass constructor
         * @param ranges the ranges of parametric values.
         */
        public SOBinarySolution (BinaryRange [] ranges) {
                //super (ranges);
                this(ranges, 0);
        }
        
        public SOBinarySolution (BinaryRange [] ranges, int numConstraints) {
          super(ranges, numConstraints);  
        }
        
        /**
         * Return the objective value of the individual.
         * @returns the fitness.
         */
        public double getObjective () {
                return objective;
        }

        /**
         * Set the objective value to fit.
         * @param fit the new fitness.
         */
        public void setObjective (double fit) {
                objective = fit;
        }
                /**
                print some representation of this solution.
        */
        public String toString () {
                boolean [] params = (boolean[])this.getParameters ();
                StringBuffer sb = new StringBuffer (1024);
                sb.append ('(');
                for (int i = 0 ; i < params.length ; i++) {
                        if (i > 0){
                                sb.append (',');
                        }
                        if(params[i]){	
                                sb.append ("1");
                        }
                        else{
                                sb.append("0");	
                        }
                }
                sb.append (')');
                sb.append (':');
                sb.append (Double.toString (objective));
                return sb.toString ();
        }
        
        /**/		
        public Object clone(){
                BinaryRange[] newRanges=(BinaryRange[])this.ranges.clone();
                
                int numConstraints = getNumConstraints();
                SOBinarySolution newSol=new SOBinarySolution(newRanges, numConstraints);
                
                newSol.setParameters((double[])this.parameters.clone());
                
                newSol.setObjective(this.getObjective());
                for(int i = 0; i < numConstraints; i++) {
                  newSol.setConstraint(getConstraint(i), i);  
                }
                return newSol;
        }
}
