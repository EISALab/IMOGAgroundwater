package ncsa.d2k.modules.projects.mbabbar.optimize.util;

import ncsa.d2k.modules.core.optimize.util.*;

/**
	This subclass of BinarySolution allows for multiple objective
	values.
*/
public class  IGAMOBinarySolution extends MOBinarySolution  {

        /** IGA: which individuals have been previously analyzed by user for their qualitative objectives.*/
        boolean rankedIndiv = false;


	/**
	 * constructor takes the ranges which are passed to the
	 * superclass and the number of objective contraints
	 * @param numObjectives the number of objective values.
	 * @param ranges the ranges of parametric values.
	 */
	public IGAMOBinarySolution (BinaryRange [] ranges, ObjectiveConstraints [] oc) {
            super (ranges, oc);
	}


       /////////////////////////////////////////////////////////////
        // Added by Meghna Babbar for Interactive Genetic Algorithms.
        /**
	 * returns true or false if an individual has been ranked by user (to be used by
         * Interactive Genetic Algorithm.)
	 * @returns true or false for whatever the case maybe.
	 */
	public boolean getRankedIndivFlag () {
		return rankedIndiv;
	}

        /**
	 * sets flag true for an individual if its qualitative objectives has been analyzed by the use
         * (to be used by Interactive Genetic Algorithm.)
	 */
	public void setRankedIndivFlag (boolean rIndiv) {
		rankedIndiv = rIndiv;
	}
        //////////////////////////////////////////////////////////////
}
