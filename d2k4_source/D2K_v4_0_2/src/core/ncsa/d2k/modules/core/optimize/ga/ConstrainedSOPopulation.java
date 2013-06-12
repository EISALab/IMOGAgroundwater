package ncsa.d2k.modules.core.optimize.ga;

import ncsa.d2k.modules.core.optimize.util.*;

public class ConstrainedSOPopulation
    extends SOPopulation
    implements ConstrainedPopulation {

  protected double[] constraintWeights;

  /**
   * Create a constrained single objective population.  The members of this
   * population will have the specified number of constraints
   * @param ranges
   * @param objConstraints
   * @param numMembers
   * @param numConstraints
   * @param targ
   */
  public ConstrainedSOPopulation(Range[] ranges,
                                 ObjectiveConstraints objConstraints,
                                 int numMembers, int numConstraints,
                                 double targ) {
    super(ranges, objConstraints, targ);
    constraintWeights = new double[numConstraints];

    if (ranges[0] instanceof BinaryRange) {
      // Set up the members
      members = new BinaryIndividual[numMembers];
      nextMembers = new BinaryIndividual[numMembers];
      for (int i = 0; i < numMembers; i++) {
        members[i] = new BinaryIndividual( (BinaryRange[]) ranges,
                                          numConstraints);
        nextMembers[i] = new BinaryIndividual( (BinaryRange[]) ranges,
                                              numConstraints);
      }

    }
    else if (ranges[0] instanceof DoubleRange) {
      // Set up the members
      members = new NumericIndividual[numMembers];
      nextMembers = new NumericIndividual[numMembers];
      for (int i = 0; i < numMembers; i++) {
        members[i] = new NumericIndividual( (DoubleRange[]) ranges,
                                           numConstraints);
        nextMembers[i] = new NumericIndividual( (DoubleRange[]) ranges,
                                               numConstraints);
      }
    }
    else if (ranges[0] instanceof IntRange) {

      /*// Set up the members
                        members = new IntIndividual [numMembers];
                        nextMembers = new IntIndividual [numMembers];
                        for (int i = 0 ; i < numMembers ; i++) {
             members[i] = new IntIndividual (traits);
             nextMembers[i] = new IntIndividual (traits);
                        }*/
    }
    else {
      System.out.println("What kind of range is this?");
    }

  }

  public int getNumConstraints() {
    return constraintWeights.length;
  }

  public double getConstraintWeight(int index) {
    return constraintWeights[index];
  }

  public void setConstraintWeight(double newWeight, int index) {
    constraintWeights[index] = newWeight;
  }

  // This returns the maximum violation of a particular constraint of a certain id
  // over the entire population, for that generation.
  private double getmaxPopConstraintViol(int id) {
    double maxViol = 0.0;
    Solution[] indivs;
    //indivs = (Solution[])this.combinedPopulation;
    indivs = (Solution[])getMembers();
    int popSz = indivs.length;

    for (int i = 0; i < popSz; i++) {
      if (maxViol >= indivs[i].getConstraint(id)) {
        maxViol = indivs[i].getConstraint(id);
      }
    }

    return maxViol;
  }

  /**
          Compare one member to another. This requires knowledge of the
          fitness function which cannot be supplied here, hence must be
          provided in a subclass.
          @returns 1 if member indexed a is greater than b,
                          0 if they are equal,
                          -1 if member indexed by a is less than b.
  */
  public int compareMembers (Solution first, Solution second) {
    int numObj = 1;
    boolean firstBetter = false, secondBetter = false;

    /** Below will only work for one constraint !!!
         if ((first.getConstraint () > 0.0) || (second.getConstraint () > 0.0)) {
            if (first.getConstraint () > second.getConstraint ())
                    return -1;
            else if (second.getConstraint () > first.getConstraint ())
                    return 1;
            else
                    return 0;
               }
     */

    /**
     * Constraint check first for individuals that violate constraints
     * Added by Meghna Babbar, feb 02, 2004
     */
    double totConstViol1 = 0.0;
    double totConstViol2 = 0.0;
    for (int i = 0; i < this.getNumConstraints(); i++) {
      totConstViol1 = totConstViol1 + this.getConstraintWeight(i) *
          first.getConstraint(i) / getmaxPopConstraintViol(i);
      totConstViol2 = totConstViol2 + this.getConstraintWeight(i) *
          second.getConstraint(i) / getmaxPopConstraintViol(i);
    }

    if ( (totConstViol1 > 0.0) || (totConstViol2 > 0.0)) {
      if (totConstViol1 > totConstViol2) {
        return -1;
      }
      else if (totConstViol2 > totConstViol1) {
        return 1;
      }
      else {
        return 0;
      }
    }
    //////////////////////////////////////////////////////////////////

    // Set the flag if one member has one objective value better than
    // the other.
    for (int i = 0; i < numObj; i++) {
      int compare = objConstraints.compare(
          ((SOSolution)first).getObjective(), ((SOSolution)second).getObjective());
      if (compare > 0) {
        firstBetter = true;
      }
      else if (compare < 0) {
        secondBetter = true;
      }
    }

    // Now figure out which is better if either is.
    if (firstBetter == true) {
      if (secondBetter == false) {
        return 1;
      }
      else {
        return 0;
      }
    }
    else
    if (secondBetter == true) {
      return -1;
    }
    else {
      return 0;
    }

    /*                int numObj = this.numObjectives;
                    boolean firstBetter = false, secondBetter = false;
         if ((first.getConstraint () > 0.0) || (second.getConstraint () > 0.0)) {
         if (first.getConstraint () > second.getConstraint ())
                                    return -1;
         else if (second.getConstraint () > first.getConstraint ())
                                    return 1;
                            else
                                    return 0;
                    }
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
     */

  }

}