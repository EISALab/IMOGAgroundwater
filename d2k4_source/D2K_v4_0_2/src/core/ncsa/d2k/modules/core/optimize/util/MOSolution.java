package ncsa.d2k.modules.core.optimize.util;


/**
 * We extend the Solution interface to support a mutli-objective
 * problems.
 */
public interface MOSolution extends Solution {
  
        /**
         * returns the objective value at the given index.
         * @param i the index of the objective to get.
         * @returns the objective value at i.
         */
        public double getObjective (int i);

        /**
         * sets the objective value.
         * @param i the index of the objective to get.
         * @param obj the new objective value.
         */
        public void setObjective (int i, double obj);
}
