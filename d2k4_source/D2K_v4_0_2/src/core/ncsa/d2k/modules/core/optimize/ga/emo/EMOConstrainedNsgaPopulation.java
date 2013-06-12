package ncsa.d2k.modules.core.optimize.ga.emo;

import ncsa.d2k.modules.core.optimize.util.*;
import ncsa.d2k.modules.core.optimize.ga.nsga.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

import java.util.*;
import ncsa.d2k.modules.core.optimize.ga.emo.functions.*;

/**
 * An extension of ConstrainedNsgaPopulation that implements the EMOPopulation
 * interface.  The Parameters for an EMO problem are kept in this population.
 * @author David Clutter
 */
public class EMOConstrainedNsgaPopulation 
    extends ConstrainedNsgaPopulation implements EMOPopulation {
  
  /** the parameters for the EMO problem */
  private Parameters parameters;
  
  /**
   * 
   * @param ranges
   * @param objConstraints
   * @param numMembers
   * @param targ
   * @param numConstr the number of constraints
   */
  public EMOConstrainedNsgaPopulation(Range[] ranges,
                                   ObjectiveConstraints[] objConstraints,
                                   int numMembers, double targ, int numConstr) {
    super(ranges, objConstraints, numMembers, targ, numConstr);
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
    List functions = parameters.getFunctions();

    int numFunctions = functions.size();
    List constraints = new LinkedList();

    for (int i = 0; i < numFunctions; i++) {
      Function func = (Function) functions.get(i);
      if (func instanceof Constraint) {
        constraints.add(func);
      }
    }

    int numGenes = 0;
    int numTraits = this.traits.length;
    Solution nis = (Solution) members[0];
    int numConstraints = nis.getNumConstraints();    

    int popSize = this.size();
    double[][] dc = new double[numTraits + numObjectives +2+numConstraints][popSize];

    for (int i = 0; i < popSize; i++) {
      Solution ni = (Solution) members[i];
      double[] genes = ni.toDoubleValues();
      int j = 0;

      // first do the genes.
      for (; j < numTraits; j++) {
        dc[j][i] = genes[j];

        // Now the objectives.
      }
      for (int k = 0; k < numObjectives; k++, j++) {
        dc[j][i] = ((NsgaSolution)ni).getObjective(k);
      }
      for(int k = 0; k < numConstraints; k++, j++) {
        dc[j][i] = ni.getConstraint(k);        
      }

      dc[j++][i] = ((NsgaSolution)ni).getRank();
      dc[j++][i] = ((NsgaSolution)ni).getCrowdingDistance();

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

    for (int k = 0; k < numObjectives; k++, i++) {
      DoubleColumn col = new DoubleColumn(dc[i]);
      col.setLabel(this.objectiveConstraints[k].getName());
      vt.addColumn(col);
    }
    for(int k = 0; k < numConstraints; k++, i++) {
      DoubleColumn col = new DoubleColumn(dc[i]);
      col.setLabel(((Function)constraints.get(k)).getName());
      vt.addColumn(col);
    }

    DoubleColumn col = new DoubleColumn(dc[i++]);
    col.setLabel("Rank");
    vt.addColumn(col);
    col = new DoubleColumn(dc[i++]);
    col.setLabel("Crowding");
    vt.addColumn(col);
    return vt;
  }
  
  public Table getGenesTable() {
    List functions = parameters.getFunctions();

    int numFunctions = functions.size();
    List constraints = new LinkedList();

    for (int i = 0; i < numFunctions; i++) {
      Function func = (Function) functions.get(i);
      if (func instanceof Constraint) {
        constraints.add(func);
      }
    }
    
    int numGenes = 0;
    int numTraits;
    BinarySolution nis = (BinarySolution) members[0];
    int numConstraints = nis.getNumConstraints();    
    numTraits = this.traits.length;

    int popSize = this.size();
    double[][] dc = new double[numTraits + numObjectives + 2+numConstraints][popSize];

    for (int i = 0; i < popSize; i++) {
      BinarySolution ni = (BinarySolution) members[i];
      double[] genes = ni.toDouble();
      int j = 0;

      // first do the genes.
      for (; j < numTraits; j++) {
        dc[j][i] = genes[j];

        // Now the objectives.
      }
      for (int k = 0; k < numObjectives; k++, j++) {
        dc[j][i] = ((NsgaSolution)ni).getObjective(k);
      }
      for(int k = 0; k < numConstraints; k++, j++) {
        dc[j][i] = ni.getConstraint(k);        
      }
      
      dc[j++][i] = ((NsgaSolution)ni).getRank();
      dc[j++][i] = ((NsgaSolution)ni).getCrowdingDistance();

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

    for (int k = 0; k < numObjectives; k++, i++) {
      DoubleColumn col = new DoubleColumn(dc[i]);
      col.setLabel(this.objectiveConstraints[k].getName());
      vt.addColumn(col);
    }
    for(int k = 0; k < numConstraints; k++, i++) {
      DoubleColumn col = new DoubleColumn(dc[i]);
      col.setLabel(((Function)constraints.get(k)).getName());
      vt.addColumn(col);
    }
    
    DoubleColumn col = new DoubleColumn(dc[i++]);
    col.setLabel("Rank");
    vt.addColumn(col);
    col = new DoubleColumn(dc[i++]);
    col.setLabel("Crowding");
    vt.addColumn(col);
    return vt;
  }
  
}