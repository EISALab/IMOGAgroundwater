package ncsa.d2k.modules.core.optimize.ga.emo;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.optimize.util.*;
import ncsa.d2k.modules.core.optimize.ga.emo.functions.*;

import java.util.*;

public class EvaluatePopulation
    extends ComputeModule {

  private int[] fitnessFunctionColumnIndex;
  private int[] constraintFunctionColumnIndex;
  private MutableTable popTable;
  private Population pop;
  private int popSize;
  private boolean singleObjective;
  private Parameters parameters;
  private int numFF;
  private int numCon;
  private int numFunctions;
  private int numDecisionVariables;
  private List functions;

  public void doit() throws Exception {
    Population p = (Population) pullInput(0);
    evaluate(p);
    // that's it; we're done.  push out the population.	
    pushOutput(pop, 0);
  }
  
  public void evaluate(Population p) {
    if (p != pop) {
      pop = p;
      popSize = p.size();
      parameters = ( (EMOPopulation) pop).getParameters();

      List decisionVars = parameters.getDecisionVariables();
      numDecisionVariables = decisionVars.size();
      
      // create the new pop table
      popTable = new MutableTableImpl(numDecisionVariables);
      for (int i = 0; i < numDecisionVariables; i++) {
        DoubleColumn dc = new DoubleColumn(popSize);
        DecisionVariable dv = (DecisionVariable)decisionVars.get(i);
        dc.setLabel(dv.getName());
        popTable.setColumn(dc, i);
      }

      functions = parameters.getFunctions();

      numFunctions = functions.size();
      List fitnessFunctions = new LinkedList();
      List constraints = new LinkedList();

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
       
      singleObjective = numFF == 1; 
      
      fitnessFunctionColumnIndex = new int[numFF];
      for (int i = 0; i < numFF; i++) {
        Function f = (Function) fitnessFunctions.get(i);
        int funcIndex = functions.indexOf(f);
        fitnessFunctionColumnIndex[i] =
            funcIndex + numDecisionVariables;
      }

      numCon = constraints.size();
      constraintFunctionColumnIndex = new int[numCon];
      for (int i = 0; i < numCon; i++) {
        Function f = (Function) constraints.get(i);
        int funcIndex = functions.indexOf(f);
        constraintFunctionColumnIndex[i] =
            funcIndex + numDecisionVariables;
      }
    }

    // copy the decision variables to the table
    copyPopulationToTable(pop, popTable);

    // first call init() on all the functions.
    for (int i = 0; i < numFunctions; i++) {
      ( (Function) functions.get(i)).init();
    }
    // now evaluate all functions.
    for (int i = 0; i < numFunctions; i++) {
      Function func = (Function) functions.get(i);
      try {
        double[] vals = func.evaluate(pop, popTable);
        DoubleColumn dc = new DoubleColumn(vals);
        dc.setLabel(func.getName());
        popTable.addColumn(dc);
      }
      catch (Exception ex) {
        // throw the exception??
        ex.printStackTrace();
      }
    }

    // now simply iterate through and set the values
    // on the population

    // first set the objective (fitness funtion) values

    // SOPopulation
    if (singleObjective) {
      SOSolution[] sols = (SOSolution[]) pop.getMembers();
      int idx = fitnessFunctionColumnIndex[0];
      for (int j = 0; j < popSize; j++) {
        double val = popTable.getDouble(j, idx);
        sols[j].setObjective(val);
      }
    }
    // Multi objective population
    else {
      MOSolution[] sols = (MOSolution[]) pop.getMembers();
      for (int i = 0; i < numFF; i++) {
        int idx = fitnessFunctionColumnIndex[i];
        for (int j = 0; j < popSize; j++) {
          double val = popTable.getDouble(j, idx);
          sols[j].setObjective(i, val);
        }
      }
    }

    // now set the constraint values
    for (int i = 0; i < numCon; i++) {
      Individual[] sols = pop.getMembers();
      int idx = constraintFunctionColumnIndex[i];
      for (int j = 0; j < popSize; j++) {
        double val = popTable.getDouble(j, idx);
        sols[j].setConstraint(val, i);
      }
    }

  }

  private void copyPopulationToTable(Population pop, MutableTable mt) {
    // ensure that the table has the same number of rows as the number
    // of individuals in the population
    if (pop.size() != mt.getNumRows()) {
     //BASIC3 populationTable.setNumRows(pop.size());
        //TODO what happens if pop.size() < populationTable.getNumRows() ????
        mt.addRows(pop.size()-mt.getNumRows());
    }

    int numOfvar = parameters.getDecisionVariables().size();
      for (int i = 0; i < pop.size(); i++) {
        double[] tabrows = pop.getMember(i).toDoubleValues();
        for (int j = 0; j < numOfvar; j++) {
          mt.setDouble(tabrows[j], i, j);
        }
      }
  }

  public String[] getInputTypes() {
    return new String[] {
        "ncsa.d2k.modules.core.optimize.ga.emo.EMOPopulation"};
  }

  public String[] getOutputTypes() {
    return new String[] {
        "ncsa.d2k.modules.core.optimize.ga.emo.EMOPopulation"};
  }

  public String getInputInfo(int i) {
    return "";
  }

  public String getInputName(int i) {
    return "EMOPopulation";
  }

  public String getOutputInfo(int i) {
    return "";
  }

  public String getOutputName(int i) {
    return "EMOPopulation";
  }

  public String getModuleInfo() {
    return
        "Evaluate the fitness functions and constraints for an EMOPopulation.";
  }

}
