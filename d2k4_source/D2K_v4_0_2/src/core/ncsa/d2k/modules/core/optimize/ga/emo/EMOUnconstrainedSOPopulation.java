package ncsa.d2k.modules.core.optimize.ga.emo;

import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.optimize.util.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

/**
 * An extension of UnconstrainedSOPopulation that implements the EMOPopulation
 * interface.  The Parameters for an EMO problem are kept in this population.
 * @author David Clutter
 */
public class EMOUnconstrainedSOPopulation
    extends SOPopulation
    implements EMOPopulation {

  /** the parameters for the EMO problem */
  private Parameters parameters;

  /**
   * 
   * @param ranges
   * @param objConstraints
   * @param numMembers
   * @param targ
   */
  public EMOUnconstrainedSOPopulation(Range[] ranges,
                                      ObjectiveConstraints objConstraints,
                                      int numMembers, double targ) {
    super(ranges, objConstraints, numMembers, targ);
  }

  /**
   * Get the parameters.
   * @return the parameters
   */
  public Parameters getParameters() {
    return parameters;
  }

  /**
   * Set the parameters
   * @param params the new parameters
   */
  public void setParameters(Parameters params) {
    parameters = params;
  }
  
  public Table getDecisionVariablesTable() {
    int numGenes = 0;
    int numTraits = this.traits.length;

    int popSize = this.size();
    double[][] dc = new double[numTraits + 1][popSize];

    for (int i = 0; i < popSize; i++) {
      Solution ni = (Solution) members[i];
      double[] genes = ni.toDoubleValues();
      int j = 0;

      // first do the genes.
      for (; j < numTraits; j++) {
        dc[j][i] = genes[j];

        // Now the objectives.
      }
      // there is just one objective.
      for (int k = 0; k < 1; k++, j++) {
        dc[j][i] = ((SOSolution)ni).getObjective();
      }
//      dc[j++][i] = ((SOSolution)ni).getRank();
//      dc[j++][i] = ((SOSolution)ni).getCrowdingDistance();
    }
    // Now make the table
    //BASIC3 TableImpl vt = (TableImpl) DefaultTableFactory.getInstance().createTable(0);
    MutableTableImpl vt =  new MutableTableImpl(0);
    int i = 0;

    for (; i < numTraits; i++) {
      DoubleColumn col = new DoubleColumn(dc[i]);
      // NsgaSolution nis0 = (NsgaSolution) members[0];
      //if (nis instanceof MONumericIndividual) {
        col.setLabel(this.traits[i].getName());
      /*}
      else {
        col.setLabel("Variable " + i);
      }*/
      vt.addColumn(col);
    }

    for (int k = 0; k < 1; k++, i++) {
      DoubleColumn col = new DoubleColumn(dc[i]);
      col.setLabel("objective");
      vt.addColumn(col);
    }
    return vt;
  }
  
  public Table getGenesTable() {
    int numGenes = 0;
    int numTraits;
    numTraits = this.traits.length;

    int popSize = this.size();
    double[][] dc = new double[numTraits + 1][popSize];

    for (int i = 0; i < popSize; i++) {
      BinarySolution ni = (BinarySolution) members[i];
      double[] genes = ni.toDouble();
      int j = 0;

      // first do the genes.
      for (; j < numTraits; j++) {
        dc[j][i] = genes[j];

        // Now the objectives.
      }
      // there is just one objective.
      for (int k = 0; k < 1; k++, j++) {
        dc[j][i] = ((SOSolution)ni).getObjective();
      }
//      dc[j++][i] = ((SOSolution)ni).getRank();
//      dc[j++][i] = ((SOSolution)ni).getCrowdingDistance();
    }
    // Now make the table
    //BASIC3 TableImpl vt = (TableImpl) DefaultTableFactory.getInstance().createTable(0);
    MutableTableImpl vt =  new MutableTableImpl(0);
    int i = 0;

    for (; i < numTraits; i++) {
      DoubleColumn col = new DoubleColumn(dc[i]);
      // NsgaSolution nis0 = (NsgaSolution) members[0];
      //if (nis instanceof MONumericIndividual) {
        col.setLabel(this.traits[i].getName());
      /*}
      else {
        col.setLabel("Variable " + i);
      }*/
      vt.addColumn(col);
    }

    for (int k = 0; k < 1; k++, i++) {
      DoubleColumn col = new DoubleColumn(dc[i]);
      col.setLabel("objective");
      vt.addColumn(col);
    }
    return vt;
  }
}
