package ncsa.d2k.modules.core.optimize.ga.emo.selection;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.optimize.ga.emo.*;
import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.optimize.ga.selection.*;

/**
 * Perform selection on a population.  The selection is handled by the
 * Selection object contained in the EMOPopulation.
 */
public class PerformSelection extends ComputeModule {

  public String[] getInputTypes() {
    String[] in = {"ncsa.d2k.modules.core.optimize.ga.emo.EMOPopulation"};
    return in;
  }

  public String[] getOutputTypes() {
    String[] in = {"ncsa.d2k.modules.core.optimize.ga.emo.EMOPopulation"};
    return in;
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
    return "";
  }

  public void beginExecution() {
    population = null;
  }

  public void endExecution() {
    population = null;
    selection = null;
  }

  private transient EMOPopulation population;
  private transient Selection selection;

  public void doit() throws Exception {
    EMOPopulation pop = (EMOPopulation)pullInput(0);

    if(population != pop) {
      Parameters info = pop.getParameters();
      selection = info.selection;
      population = pop;
    }

    selection.performSelection((Population)pop);
    pushOutput(pop, 0);
  }
}