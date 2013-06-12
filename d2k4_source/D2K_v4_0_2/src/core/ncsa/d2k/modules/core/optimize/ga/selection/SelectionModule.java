package ncsa.d2k.modules.core.optimize.ga.selection;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.optimize.ga.*;

/**
                This is the superclass of all selection modules, probably. Subclasses should override a
                method named <code>compute</code> which will assign to an array of integers the indexes
                of the individuals which will populate the next generation.
                .
 */
abstract public class SelectionModule
    extends ncsa.d2k.core.modules.ComputeModule {

  protected transient Selection selection;

  protected abstract void createSelection();

  protected SelectionModule() {
    createSelection();
  }

  /** this property is set for debugging. */
  //protected boolean debug = false;

  //////////////////////////////////
  // Accessors for the properties.
  //////////////////////////////////
  /**
          set the debug flag.
          @param db is the boolean indicating debugging on or off.
   */
  public void setDebug(boolean score) {
    //this.debug = score;
    selection.setDebug(score);
  }

  /**
          get the debug flag.
          @returns true if debug is on.
   */
  public boolean getDebug() {
    //return this.debug;
    return selection.getDebug();
  }

  /**
          get the random number seed.
   */
  public long getRandomNumberSeed() {
    return selection.getRandomSeed();
  }

  /**
          set the random number seed for crossover.
   */
  public void setRandomNumberSeed(long seed) {
      selection.setRandomSeed(seed);
  }
  //////////////////////////////////
  // Info methods
  //////////////////////////////////
  /**
          This method returns the description of the various inputs.
          @return the description of the indexed input.
   */
  public String getOutputInfo(int index) {
    String[] outputDescriptions = {
        "This population is the resulting progeny."
    };
    return outputDescriptions[index];
  }

  /**
          This method returns the description of the various inputs.
          @return the description of the indexed input.
   */
  public String getInputInfo(int index) {
    String[] inputDescriptions = {
        "This is the next generation population to be selected on."
    };
    return inputDescriptions[index];
  }

  /**
          This method returns the description of the module.
          @return the description of the module.
   */
  public String getModuleInfo() {
    String text = "This module will take the given population of Individuals and select based on fitness, or random draw. The result will be a population that was selected.\n RankIndividuals is set if the individuals are to be ranked on the basis of some fitness function.\n Gap is set if there is a generation gap. 1.0 is no generation gap, and the smaller the number (> 0) the greater the gap.";
    return text;
  }

  //////////////////////////////////
  // Type definitions.
  //////////////////////////////////

  public String[] getInputTypes() {
    String[] temp = {
        "ncsa.d2k.modules.core.optimize.ga.Population"};
    return temp;
  }

  public String[] getOutputTypes() {
    String[] temp = {
        "ncsa.d2k.modules.core.optimize.ga.Population"};
    return temp;
  }

  /**
       Fill in the samples array with the indices of the individuals with are
          to survive and be replicated int he following generation
          @param population is the population of individuals.
   */
//        abstract protected void compute (Population population);

  /**
          Do the selection.
          @param outV the array to contain output object.
   */
//  protected int[] sample;
  public void doit() throws Exception {
    // Our input argument is the population.
    Population population = (Population)this.pullInput(0);
    selection.performSelection(population);
    this.pushOutput(population, 0);
  }

/*  public void performSelection(Population population) {
    if (debug) {
      System.out.println("==================================================");
      System.out.println("-------------- BEFORE SELECTION ------------------");
      for (int i = 0; i < population.size(); i++) {
        System.out.println(i + "::" + population.getMember(i));

      }
    }

    // This is an array of indices which when done will identify
    // the members of the new population.
    int popSize = population.size();
    sample = new int[popSize];

    // Compute the members to survive and reproduce
    selection.compute(population);
    if (debug) {
      System.out.println("");
      System.out.println("-------------- AFTER SELECTION ------------------");
      for (int i = 0; i < sample.length; i++) {
        System.out.println(i + ":" + sample[i] + ":" +
                           population.getMember(sample[i]));

      }
    }
    population.shuffleIndices(sample);

    // The population does the work of creating the new population,
    // we tell it which members are to survive.
    population.makeNextGeneration(sample);
    if (debug) {
      System.out.println("");
      System.out.println("-------------- RANDOM ORDERED ------------------");
      for (int i = 0; i < sample.length; i++) {
        System.out.println(i + ":" + i + ":" + population.getMember(i));

      }
    }
  }*/
}
