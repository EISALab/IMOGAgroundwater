package ncsa.d2k.modules.core.optimize.ga.emo.crossover;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.optimize.ga.emo.*;
import ncsa.d2k.modules.core.optimize.ga.*;

import ncsa.d2k.modules.core.optimize.ga.crossover.*;

/**
 * Perform crossover for a population.  The crossover is performed by
 * the Crossover object contained in the Parameters of the EMOPopulation.
 */
public class PerformCrossover extends ComputeModule {

  public String[] getInputTypes() {
    String[] in = {"ncsa.d2k.modules.core.optimize.ga.emo.EMOPopulation"};
    return in;
  }

  public String getInputName(int i) {
    return "EMOPopulation";
  }

  public String[] getOutputTypes() {
    String[] in = {"ncsa.d2k.modules.core.optimize.ga.emo.EMOPopulation"};
    return in;
  }

  public String getOutputName(int i) {
    return "EMOPopulation";
  }

  public String getInputInfo(int i) {
    return "The population before crossover.";
  }

  public String getOutputInfo(int i) {
    return "The population after the crossover.";
  }

  public String getModuleInfo() {
    return "Perform crossover on a population.";
  }

  public void beginExecution() {
    population = null;
  }

  public void endExecution() {
    population = null;
    crossover = null;
  }

  private transient EMOPopulation population;
  private transient Crossover crossover;

  public void doit() throws Exception {
    EMOPopulation pop = (EMOPopulation)pullInput(0);

    // if the population has changed or the crossoverType has not been set,
    // create a Crossover object
    if(population != pop) {
      Parameters info = pop.getParameters();
      crossover = info.crossover;
      population = pop;
    }

    // do the crossover
    crossover.performCrossover((Population)pop);

    // run the module
    pushOutput(pop, 0);
  }
}