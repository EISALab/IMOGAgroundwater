package ncsa.d2k.modules.core.optimize.ga.emo;

import ncsa.d2k.modules.core.optimize.ga.crossover.*;
import ncsa.d2k.modules.core.optimize.ga.mutation.*;
import ncsa.d2k.modules.core.optimize.ga.selection.*;
import ncsa.d2k.modules.core.optimize.ga.emo.functions.*;

import java.util.*;

import ncsa.d2k.modules.core.datatype.table.*;

/**
 * Contains all the parameters to run EMO.  This includes the parameters to
 * create the population, calculate fitness functions, calculate constraints,
 * and mutation, selection and crossover parameters.
 */
public class Parameters implements java.io.Serializable {
  
  public Table seedTable;
  
  /**
   * The crossover function to use.
   */
  public Crossover crossover;

  /**
   * The mutation function to use.
   */
  public Mutation mutation;

  /**
   * The selection function to use.
   */
  public Selection selection;

  /**
   * true if binary individuals should be created, false otherwise.
   */
  public boolean createBinaryIndividuals;

  /**
   * The population size to use.
   */
  public int populationSize;

  /**
   * The maximum number of generations.
   */
  public int maxGenerations;
  
  private List functions;
  private List decisionVariables;
  private int totalStringLength;
  private int numConstraints;
  private int numFitnessFunctions;

  public Parameters() {
    functions = new ArrayList();
    decisionVariables = new ArrayList();
    numFitnessFunctions = 0;
    numConstraints = 0;
    totalStringLength = 0;
  }
  
  public void addDecisionVariable(DecisionVariable var) {
    decisionVariables.add(var);  
    totalStringLength += var.getStringLength();
  }

  public void addFunction(Function f) {
    functions.add(f);  
    if(f instanceof FitnessFunction)
      numFitnessFunctions++;
    else if(f instanceof Constraint) 
      numConstraints++; 
  }
  
  public List getFunctions() { 
    return functions; 
  }
  
  public List getDecisionVariables() {
    return decisionVariables;  
  }
  
  public int getNumFitnessFunctions() {
    return numFitnessFunctions;
  } 
  
  public int getNumConstraints() {
    return numConstraints;
  }
  
  public int getNumDecisionVariables() {
    return decisionVariables.size();
  }
  
  public int getTotalStringLength() {
    return totalStringLength;
  }
}