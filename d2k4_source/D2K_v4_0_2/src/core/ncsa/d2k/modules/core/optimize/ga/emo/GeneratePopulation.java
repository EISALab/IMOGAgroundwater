package ncsa.d2k.modules.core.optimize.ga.emo;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.optimize.util.*;
import ncsa.d2k.modules.core.optimize.ga.nsga.*;
import ncsa.d2k.modules.core.optimize.ga.*;

import ncsa.d2k.modules.core.datatype.table.*;

import java.util.*;
import ncsa.d2k.modules.core.optimize.ga.emo.functions.*;

/**
 * Generate a population.  The population created depends on the number of
 * objectives -- SOPopulation for one objective, NsgaPopulation for multiple
 * objectives.  If a seeding is present, this will set the first N individuals
 * with the seed values, where N is the number of seeds.
 */
public class GeneratePopulation
    extends ComputeModule {

  public String[] getInputTypes() {
    return new String[] {
        "ncsa.d2k.modules.core.optimize.ga.emo.Parameters"};
  }

  public String[] getOutputTypes() {
    return new String[] {
        "ncsa.d2k.modules.core.optimize.ga.emo.EMOPopulation"};
  }

  public String getInputInfo(int i) {
    return "";
  }

  public String getInputName(int i) {
    return "Params";
  }

  public String getOutputInfo(int i) {
    return "";
  }

  public String getOutputName(int i) {
    return "EMOPopulation";
  }

  public String getModuleInfo() {
    String s = "Generate a population.  The population created depends on the ";
        s += "number of objectives -- SOPopulation for one objective, ";
        s += "NsgaPopulation for multiple objectives.  If a seeding is ";
        s += "present, this will set the first N individuals with the seed ";
        s += "values, where N is the number of seeds.";
    return s;
  }

  /** the total number of fitness functions defined */
  private int numFF;
  /** the number of fitness functions defined using a formula
   * (using a transformation on a table) */
  private int numFormulaFF;
  /** the number of fitness functions calculated using an executable */
  private int numExternalFF;

  /** the total number of constraints imposed on the prob */
  private int numConstraints;
  /** the number of constraints defined using a formula
   * (using a transformation on a table) */
  private int numFormulaConstraints;
  /** the number of constraints calculated using an executable */
  private int numExternalConstraints;

  /** the parameters for the problem */
  private Parameters params;

  /** is this the first time the module has been run? */
  private boolean firstTime;

  private static final String X = "x";

  public void beginExecution() {
    firstTime = true;
    params = null;
  }

  public void endExecution() {
    params = null;
  }
  
  List fitnessFunctions;
  List constraints;

  public void doit() throws Exception {
    Parameters p = (Parameters)pullInput(0);
    Population pop = generate(p); 
    pushOutput(pop, 0);
  }
  
  public Population generate(Parameters p) throws Exception {
    if (firstTime) {
      // pull in the parameters
      params = p;
      
      List functions = params.getFunctions();

      int numFunctions = functions.size();
      fitnessFunctions = new LinkedList();
      constraints = new LinkedList();

      for (int i = 0; i < numFunctions; i++) {
        Function func = (Function) functions.get(i);
        if (func instanceof FitnessFunction) {
          fitnessFunctions.add(func);
        }
        else if (func instanceof Constraint) {
          constraints.add(func);
        }
      }
      
      numFF = fitnessFunctions.size();
      numConstraints = constraints.size();

      // count the number of fitness functions
      /*FitnessFunctions ff = params.fitnessFunctions;
      numFF = ff.getTotalNumFitnessFunctions();
      numFormulaFF = ff.getNumFitnessFunctions();
      numExternalFF = ff.getNumExternFitnessFunctions();

      // count the number of constraints
      Constraints con = params.constraints;
      numConstraints = con.getTotalNumConstraints();
      numFormulaConstraints = con.getNumConstraintFunctions();
      numExternalConstraints = con.getNumExternConstraints();
          */

      // if there were no fitness functions, just die
      if (numFF == 0) {
        throw new Exception("No Fitness Functions were defined.");
      }

      firstTime = false;
    }
    else {
      // this is a dummy input signaling a new population that has
      // twice as many members
      // just discard the input
      //pullInput(0);
      // double the population size, but use the same parameters
      params.populationSize *= 2;
    }

    // the decision variables for the problem
    //DecisionVariables dv = params.decisionVariables;
    List dv = params.getDecisionVariables();
    // the fitness functions
    //FitnessFunctions ff = params.fitnessFunctions;

    int numVariables = dv.size();

    Range[] xyz;

    // use BinaryRange for binary-coded individuals
    if (params.createBinaryIndividuals) {
      xyz = new BinaryRange[numVariables];
      for (int i = 0; i < numVariables; i++) {
        DecisionVariable var = (DecisionVariable)dv.get(i);
        xyz[i] = new BinaryRange(var.getName(), 
                                 var.getStringLength(),
                                 var.getPrecision());
      }
    }
    // use DoubleRange for real-coded individuals
    else {
      xyz = new DoubleRange[numVariables];
      for (int i = 0; i < numVariables; i++) {
        DecisionVariable var = (DecisionVariable)dv.get(i);
        xyz[i] = new DoubleRange(var.getName(),
                                 var.getMax(),
                                 var.getMin());
      }
    }

    // the objective constraints for FF by formula
    //ObjectiveConstraints[] formulas = new ObjectiveConstraints[numFormulaFF];
    // the objective constraints for FF by executable
    //ObjectiveConstraints[] externs = new ObjectiveConstraints[numExternalFF];
    
    ObjectiveConstraints[] fit = new ObjectiveConstraints[numFF];
    
    for(int i = 0; i < numFF; i++) {
      FitnessFunction ff = (FitnessFunction)fitnessFunctions.get(i);
      if(ff.isMinimizing()) {
        fit[i] = ObjectiveConstraintsFactory.getObjectiveConstraints(
            ((Function)ff).getName(), 0.0, 1.0);
      }
      else {
        fit[i] = ObjectiveConstraintsFactory.getObjectiveConstraints(
            ((Function)ff).getName(), 1.0, 0.0);
      }
    
    }

    // first create the objective constraints for FF by formula
/*    for (int i = 0; i < numFormulaFF; i++) {
       if(ff.functionIsMinimizing(i)) {
        formulas[i] = ObjectiveConstraintsFactory.getObjectiveConstraints(
            ff.getFitnessFunctionName(i), 0.0, 1.0);
      }
      else {
        formulas[i] = ObjectiveConstraintsFactory.getObjectiveConstraints(
            ff.getFitnessFunctionName(i), 1.0, 0.0);
      }
    }

    // now create the objective constraints for FF by extern
    for (int i = 0; i < numExternalFF; i++) {
      String name = ff.getExternFitnessFunctionName(i);
      if(ff.getExternIsMinimizing(i)) {
        externs[i] = ObjectiveConstraintsFactory.getObjectiveConstraints(
            name, 0.0, 1.0);
      }
      else {
        externs[i] = ObjectiveConstraintsFactory.getObjectiveConstraints(
            name, 1.0, 0.0);
      }
    }*/

    // now copy them into one big array
/*    ObjectiveConstraints[] fit =
        new ObjectiveConstraints[numFormulaFF + numExternalFF];
    System.arraycopy(formulas, 0, fit, 0, numFormulaFF);
    System.arraycopy(externs, 0, fit, numFormulaFF, numExternalFF);
        */
    
    // for an SO problem (one fitness function) create an SO population
    if (numFF == 1) {
      boolean constrained = (numConstraints > 0);

      EMOPopulation pop;
      // create a constrained SO pop
      if(constrained) {
        pop = new EMOConstrainedSOPopulation(xyz, fit[0],
                                             params.populationSize, 100,
                                             this.numConstraints);

        // for each constraint, set the weight on the population object
        ConstrainedPopulation cp = (ConstrainedPopulation)pop;
/*        for(int i = 0; i < this.numFormulaConstraints; i++) {
          double weight = params.constraints.getConstraintFunctionWeight(i);
          cp.setConstraintWeight(weight, i);
        }

        for(int i = numFormulaConstraints; i < this.numExternalConstraints; i++) {
          double weight = params.constraints.getExternConstraintWeight(i);
          cp.setConstraintWeight(weight, i);
        }*/
        for(int i = 0; i < numConstraints; i++) {
          Constraint con = (Constraint)constraints.get(i);
          double weight = con.getWeight();
          cp.setConstraintWeight(weight, i);
        }
      }
      // create an unconstrained SO pop
      else {
        pop = new EMOUnconstrainedSOPopulation(xyz, fit[0],
                                               params.populationSize, 100);
      }

      // the parameters tag along with the population
      pop.setParameters(params);
      //set the maximum number of generations
      ((Population)pop).setMaxGenerations(params.maxGenerations);

      seedPopulation((Population)pop, params);
      //pushOutput(pop, 0);
      return (Population)pop;
    }
    // otherwise, for an MO problem (mulitple fitness functions) create
    // either a constrained or unconstrained nsga population
    else {
      EMOPopulation pop = null;
      boolean constrained = (numConstraints > 0);

      // if there were constraints, we create a constrained pop
      if (constrained) {
        pop = new EMOConstrainedNsgaPopulation(xyz, fit,
                                            params.populationSize, 100, this.numConstraints);
        // for each constraint, set the weight on the population object
        ConstrainedPopulation cp = (ConstrainedPopulation)pop;
/*        for(int i = 0; i < this.numFormulaConstraints; i++) {
          double weight = params.constraints.getConstraintFunctionWeight(i);
          cp.setConstraintWeight(weight, i);
        }

        for(int i = numFormulaConstraints; i < this.numExternalConstraints; i++) {
          double weight = params.constraints.getExternConstraintWeight(i);
          cp.setConstraintWeight(weight, i);
        }*/
        for(int i = 0; i < numConstraints; i++) {
          Constraint con = (Constraint)constraints.get(i);
          double weight = con.getWeight();
          cp.setConstraintWeight(weight, i);
        }
      }
      // if there are no constraints, we create an Unconstrained pop
      else {
        pop = new EMOUnconstrainedNsgaPopulation(xyz, fit,
                                              params.populationSize, 100);
      }
      // the parameters tag along with the population
      pop.setParameters(params);
      //set the maximum number of generations
      ((NsgaPopulation)pop).setMaxGenerations(params.maxGenerations);

      seedPopulation((NsgaPopulation)pop, params);
      //pushOutput(pop, 0);
      return (NsgaPopulation)pop;
    }
  }

  private void seedPopulation(Population p, Parameters params) {
    //DecisionVariables dv = params.decisionVariables;
    Table tbl = params.seedTable;
    if(tbl == null)
      return;

    int numTraits = p.getTraits().length;
    if(tbl.getNumColumns() != numTraits) {
      return;
    }

    if(params.createBinaryIndividuals) {
      BinarySolution[] sols = (BinarySolution[])p.getMembers();
      int size = tbl.getNumRows();
      for(int i = 0; i < size; i++) {
        BinarySolution bs = sols[i];
        boolean[][] vals = new boolean[numTraits][];
        int totalNumBits = 0;
        // !!!!!!!!!!!!!!!!!!!!
        // now we need to change the double value into a bit string ...
        for(int j = 0; j < numTraits; j++) {
          DecisionVariable dv = (DecisionVariable)params.getDecisionVariables().get(j);
          boolean[] bits = DecisionVariable.convertToBitString(tbl.getDouble(i, j), 
            dv.getMin(), dv.getMax(), dv.getPrecision());
          vals[j] = bits;  
          totalNumBits += bits.length;
        } 
        
        // now copy the values into one array.
        boolean[] bool = new boolean[totalNumBits];
        int idx = 0; 
        for(int z = 0; z < numTraits; z++) {
          for(int y = 0; y < vals[z].length; y++) {
            bool[idx] = vals[z][y];  
            idx++;
          }
        }
        bs.setParameters(bool);
      }
    }
    else {
      DoubleSolution[] sols = (DoubleSolution[])p.getMembers();
      int size = tbl.getNumRows();
      for(int i = 0; i < size; i++) {
        DoubleSolution ds = sols[i];
        double[] vals = new double[numTraits];
        for(int j = 0;j < numTraits; j++) {
          vals[j] = tbl.getDouble(i, j);
        }
        ds.setParameters(vals);
      }
    }
  }
}