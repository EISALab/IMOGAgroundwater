package ncsa.d2k.modules.core.optimize.ga.nsga;

import ncsa.d2k.modules.core.optimize.util.*;


/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

final public class UnconstrainedNsgaPopulation extends NsgaPopulation {

        /**
                Given only the number of members, the number of alleles per member
                and the size of the window.
                @param numMembers the number of individuals in the population.
                @param numGenes number of genes in the pool.
        */
        public UnconstrainedNsgaPopulation (Range [] ranges, ObjectiveConstraints [] objConstraints,
                        int numMembers, double targ) {
                super (ranges, objConstraints, numMembers, targ);
        }
        
        /**
         * Compares two individuals returns a value that indicates which individual
         * dominates the other.
         * @param first the first member to compare
         * @param second the other member.
         * @returns 1 if first dominates second, -1 if second dominates first,
         *  0 if neither dominates.
         */
        final protected int dominates (NsgaSolution first, NsgaSolution second) {
                int numObj = this.numObjectives;
                boolean firstBetter = false, secondBetter = false;



                // Set the flag if one member has one objective value better than
                // the other.
                for (int i = 0 ; i < numObj; i++) {
                    int compare = objectiveConstraints [i].compare (
                                first.getObjective (i), second.getObjective (i));
                        if (compare > 0)
                                firstBetter = true;
                        else if (compare < 0)
                                secondBetter = true;
                }

                // Now figure out which is better if either is.
                if (firstBetter == true)
                        if (secondBetter == false)  return 1;
                        else                        return 0;
                else
                        if (secondBetter == true)   return -1;
                        else                        return 0;
        }
}