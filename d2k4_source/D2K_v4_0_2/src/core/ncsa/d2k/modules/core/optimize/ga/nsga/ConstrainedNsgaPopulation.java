package ncsa.d2k.modules.core.optimize.ga.nsga;

import java.io.Serializable;
import ncsa.d2k.modules.core.optimize.ga.*;
//import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.optimize.util.*;


/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class ConstrainedNsgaPopulation
    extends NsgaPopulation
    implements ConstrainedPopulation {

  /** the weights applied to the constraints */
  protected double[] constraintWeights;

  /**
          Given only the number of members, the number of alleles per member
          and the size of the window.
          @param numMembers the number of individuals in the population.
          @param numGenes number of genes in the pool.
   */
  public ConstrainedNsgaPopulation(Range[] ranges,
                                   ObjectiveConstraints[] objConstraints,
                                   int numMembers, double targ) {
    this(ranges, objConstraints, numMembers, targ, 0);
  }

  /**
         Given only the number of members, the number of alleles per member
         and the size of the window.
         @param numMembers the number of individuals in the population.
         @param numGenes number of genes in the pool.
   */
  public ConstrainedNsgaPopulation(Range[] ranges,
                                   ObjectiveConstraints[] objConstraints,
                                   int numMembers, double targ, int numConstr) {
    super(ranges, objConstraints, targ);
    constraintWeights = new double[numConstr];

    // Create a population of the appropriate type.
    // create individuals with the proper number of constraints
    if (ranges[0] instanceof BinaryRange) {

      // Set up the members
      members = new MOBinaryIndividual[numMembers];
      nextMembers = new MOBinaryIndividual[numMembers];
      for (int i = 0; i < numMembers; i++) {
        members[i] = new MOBinaryIndividual( (BinaryRange[]) ranges,
                                            objConstraints, numConstr);
        nextMembers[i] = new MOBinaryIndividual( (BinaryRange[]) ranges,
                                                objConstraints, numConstr);
      }

    }
    else if (ranges[0] instanceof DoubleRange) {
      // Set up the members
      members = new MONumericIndividual[numMembers];
      nextMembers = new MONumericIndividual[numMembers];
      for (int i = 0; i < numMembers; i++) {
        members[i] = new MONumericIndividual(
            (DoubleRange[]) ranges, objConstraints, numConstr);
        nextMembers[i] = new MONumericIndividual(
            (DoubleRange[]) ranges, objConstraints, numConstr);
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

    // Create an array that will contain the pointers to the combined population.
    int i = 0;
    combinedPopulation = new NsgaSolution[numMembers * 2];
    for (; i < numMembers; i++) {
      combinedPopulation[i] = (NsgaSolution) members[i];
    }
    for (int j = 0; j < numMembers; i++, j++) {
      combinedPopulation[i] = (NsgaSolution) nextMembers[j];

    }
  }

  /**
   * Get the number of constraints
   * @return the number of constraints
   */
  public int getNumConstraints() {
    return constraintWeights.length;
  }

  /**
   * Get the weight of a constraint
   * @param idx
   * @return
   */
  public double getConstraintWeight(int idx) {
    return constraintWeights[idx];
  }

  /**
   * Set the weight of a constraint
   * @param val
   * @param idx
   */
  public void setConstraintWeight(double val, int idx) {
    constraintWeights[idx] = val;
  }

  // This returns the maximum violation of a particular constraint of a certain id
  // over the entire population, for that generation.
  private double getmaxPopConstraintViol(int id) {
    double maxViol = 0.0;
    NsgaSolution[] indivs;
    indivs = (NsgaSolution[])this.combinedPopulation;
    int popSz = indivs.length;

    for (int i = 0; i < popSz; i++) {
      if (maxViol >= indivs[i].getConstraint(id)) {
        maxViol = indivs[i].getConstraint(id);
      }
    }

    return maxViol;
  }

  /**
   * Compares two individuals returns a value that indicates which individual
   * dominates the other.
   * @param first the first member to compare
   * @param second the other member.
   * @returns 1 if first dominates second, -1 if second dominates first,
   *  0 if neither dominates.
   */
  final protected int dominates(NsgaSolution first, NsgaSolution second) {
    int numObj = this.numObjectives;
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
      int compare = objectiveConstraints[i].compare(
          first.getObjective(i), second.getObjective(i));
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