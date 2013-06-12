package ncsa.d2k.modules.core.optimize.util;


/**
 * We extend the Solution interface to support a single objective
 * value.
 */
public interface SOSolution extends Solution {

        /**
         * returns the objective value, for multiobjective, this
         * will just reutrn the first one.
         */
        public double getObjective ();
        
        /**
         * sets the objective value.
         * @param obj the new objective value.
         */
        public void setObjective (double obj);
        

}
