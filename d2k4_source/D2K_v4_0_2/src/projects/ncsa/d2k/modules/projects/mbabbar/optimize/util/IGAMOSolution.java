package ncsa.d2k.modules.projects.mbabbar.optimize.util;

import ncsa.d2k.modules.core.optimize.util.*;
/**
 * We extend the Solution interface to support a mutli-objective
 * problems.
 */
public interface IGAMOSolution extends Solution{

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


        /////////////////////////////////////////////////////////////
        // Added by Meghna Babbar for Interactive Genetic Algorithms.

        /**
	 * returns true if user has been ranked by individual before, else returns false
	 * @returns true or false for whatever the case maybe.
	 */
	public boolean getRankedIndivFlag () ;

        /**
	 * sets flag true for an individual if its qualitative objectives has been analyzed by the user
         * (to be used by Interactive Genetic Algorithm.
	 */
	public void setRankedIndivFlag (boolean rIndiv) ;
        //////////////////////////////////////////////////////////////
}
