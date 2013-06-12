package ncsa.d2k.modules.core.optimize.ga.crossover;

import ncsa.d2k.modules.core.optimize.ga.*;

/**
     This module will do a two point crossover on the mating population. For each
     pair of mates in the mating pool, two points are randomly selected, and passed
                to the individuals who perform the actual crossover.
                <p>
                There are three properties in this module:
                <DL>
                  <DT>debugging</dt>
                    <DD>set this flag to see what the module is doing. This produces a lot of output.</dd>
                  <DT>GenerationGap</DT>
                        <DD>this value tells what percentage of the population is to be mated at each generation</DD>
                  <DT>CrossoverRate</DT>
     <DD>the probability that two mated individuals will experience crossover.</DD>
                </DL>
                <p>
                This module will also activate a trigger whenever the ga is completely done. This module can be subclassed by
                other crossover modules to obtain the properties.
 */
public class CrossoverModule
    extends ncsa.d2k.core.modules.ComputeModule {

  protected transient Crossover crossover;

  public CrossoverModule() {
    createCrossover();
  }
  
  protected void createCrossover() {
    crossover = new Crossover();
  }

  ///////////////////////////////
  // Properties.
  ///////////////////////////////

//  protected boolean debugging = false;

  /**
          set the debug flag.
          @param debug is a boolean indicating if debugging is on or off.
   */
  public void setDebugging(boolean score) {
    //this.debugging = score;
    crossover.setDebugging(score);
  }

  /**
          get the debug flag.
          @returns true if debugging is on.
   */
  public boolean getDebugging() {
    //return this.debugging;
    return crossover.getDebugging();
  }

  /** Defines the percent of the population that is possibly replace at each generation. */
//  protected double generationGap = 0.6;

  /**
          Generation gap defines the percent of the population replaced in each
          generation, this value is set here.
          @param ng the new generation gap value.
   */
  public void setGenerationGap(double ng) {
//    generationGap = ng;
    crossover.setGenerationGap(ng);
  }

  /**
          get the generation gap
       @returns the ratio of the population to be replace at each generation.
   */
  public double getGenerationGap() {
//    return generationGap;
    return crossover.getGenerationGap();
  }

  /** The probability that any two individuals will cross. */
//  protected double c_rate = 0.25;

  /**
          set the probability that any two individuals will cross.
          @param score the new probability that any two individuals will cross.
   */
  public void setCrossoverRate(double score) {
//    c_rate = score;
    crossover.setCrossoverRate(score);
  }

  /**
          get the crossover rate.
          @returns the crossover rate.
   */
  public double getCrossoverRate() {
//    return c_rate;
    return crossover.getCrossoverRate();
  }

  /**
          set the random number seed for crossover.
   */
  public void setRandomNumberSeed(long seed) {
      crossover.setRandomSeed(seed);
  }

  /**
          get the random number seed.
   */
  public long getRandomNumberSeed() {
    return crossover.getRandomSeed();
  }

  //////////////////////////////////
  // Info methods
  //////////////////////////////////
  /**
          This method returns the description of the various inputs.
          @return the description of the indexed input.
   */
  public String getOutputInfo(int index) {
    switch (index) {
      case 0:
        return
            "      The resulting output population which has been crossed.   ";
      default:
        return "No such output";
    }
  }

  /**
          This method returns the description of the various inputs.
          @return the description of the indexed input.
   */
  public String getInputInfo(int index) {
    switch (index) {
      case 0:
        return
            "      This is the input population, containing the mating pool.   ";
      default:
        return "No such input";
    }
  }

  /**
          This method returns the description of the module.
          @return the description of the module.
   */
  public String getModuleInfo() {
    return "<html>  <head>      </head>  <body>    This module will take the given population of Individuals and mate them,     crossing them at some random gene. The only property is the crossover     rate, which ranges between 0 and 1, and this is the probability that a     individual will cross.  </body></html>";
  }

  //////////////////////////////////
  // Type definitions.
  //////////////////////////////////

  public String[] getInputTypes() {
    String[] types = {
        "ncsa.d2k.modules.core.optimize.ga.Population"};
    return types;
  }

  public String[] getOutputTypes() {
    String[] types = {
        "ncsa.d2k.modules.core.optimize.ga.Population"};
    return types;
  }

  /**
          Do the crossover.
          @param outV the array to contain output object.
   */
//  protected int[] x = new int[2];
  public void doit() throws Exception {

    // Our input argument is the population.
    Population population = (Population)this.pullInput(0);
    crossover.performCrossover(population);
    this.pushOutput(population, 0);
  }

/*  public void performCrossover(Population population) {
    Individual[] individuals = population.getMembers();
    int[] x = new int[2];

    // Compute the last individual to cross.
    int last = (int) (c_rate * population.size() * generationGap);
    int dad;
    for (int mom = 0; mom < last; mom += 2) {
      dad = mom + 1;

      // choose two Crossover points 
      x[0] = (int) (Math.random() * population.getNumGenes());
      x[1] = (int) (Math.random() * (population.getNumGenes() - 1));
      if (x[1] < x[0]) {

        // Swap them
        int swap = x[1];
        x[1] = x[0];
        x[0] = swap;
      }
      else {
        x[1]++;

        // Swap the indicated genes.
      }
      if (debugging) {
        System.out.println("--------------------");
        System.out.println("Before cross at positions " + x[0] + " and " + x[1]);
        population.printIndividual(mom);
        population.printIndividual(dad);
      }
      population.getMember(mom).crossAt(x, population.getMember(dad));
      if (debugging) {
        System.out.println("After crossover ");
        population.printIndividual(mom);
        population.printIndividual(dad);
      }
    }
  }*/

  /**
   * Return the human readable name of the module.
   * @return the human readable name of the module.
   */
  public String getModuleName() {
    return "Crossover";
  }

  /**
   * Return the human readable name of the indexed input.
   * @param index the index of the input.
   * @return the human readable name of the indexed input.
   */
  public String getInputName(int index) {
    switch (index) {
      case 0:
        return "population";
      default:
        return "NO SUCH INPUT!";
    }
  }

  /**
   * Return the human readable name of the indexed output.
   * @param index the index of the output.
   * @return the human readable name of the indexed output.
   */
  public String getOutputName(int index) {
    switch (index) {
      case 0:
        return "population";
      default:
        return "NO SUCH OUTPUT!";
    }
  }
}
