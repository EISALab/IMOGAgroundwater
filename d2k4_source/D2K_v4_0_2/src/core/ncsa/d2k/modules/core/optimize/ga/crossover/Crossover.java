package ncsa.d2k.modules.core.optimize.ga.crossover;

import ncsa.d2k.modules.core.optimize.ga.*;
import java.util.*;

public class Crossover {

  protected double generationGap = 0.6;
  protected double crossoverRate = 0.25;
  protected boolean debugging = false;
  protected long randomSeed = 1000;
  public Random randNum = new Random(randomSeed);

  /**
          set the debug flag.
          @param debug is a boolean indicating if debugging is on or off.
   */
  public void setDebugging(boolean score) {
    this.debugging = score;
  }

  /**
          get the debug flag.
          @returns true if debugging is on.
   */
  public boolean getDebugging() {
    return this.debugging;
  }

  /**
          Generation gap defines the percent of the population replaced in each
          generation, this value is set here.
          @param ng the new generation gap value.
   */
  public void setGenerationGap(double ng) {
    generationGap = ng;
  }

  /**
          get the generation gap
       @returns the ratio of the population to be replace at each generation.
   */
  public double getGenerationGap() {
    return generationGap;
  }

  /**
          set the probability that any two individuals will cross.
          @param score the new probability that any two individuals will cross.
   */
  public void setCrossoverRate(double score) {
    crossoverRate = score;
  }

  /**
          get the crossover rate.
          @returns the crossover rate.
   */
  public double getCrossoverRate() {
    return crossoverRate;
  }

  // get random number seed
  public long getRandomSeed (){
    return randomSeed;
  }

 // set random number seed
  public void setRandomSeed (long seed){
    randomSeed = seed;
  }
 
  public void performCrossover(Population population) throws Exception {
    Individual[] individuals = population.getMembers();
    int[] x = new int[2];

    // Compute the last individual to cross.
    int last = (int) (getCrossoverRate() * population.size() * getGenerationGap());
    int dad;
    for (int mom = 0; mom < last; mom += 2) {
      dad = mom + 1;

      // choose two Crossover points */
      x[0] = (int) (randNum.nextDouble() * population.getNumGenes());
      x[1] = (int) (randNum.nextDouble() * (population.getNumGenes() - 1));

      
      //x[0] = (int) (Math.random() * population.getNumGenes());
      //x[1] = (int) (Math.random() * (population.getNumGenes() - 1));
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
  }
}