package ncsa.d2k.modules.core.optimize.ga.selection;

import ncsa.d2k.modules.core.optimize.ga.*;
import java.util.*;

public abstract class Selection {

  /** selective pressure determins how rapidly the population converges. */
  protected int tsize = 4;

  // VARIABLES FOR THE RANDOM NUMBER GENERATOR
  protected long randomSeed = 1000;
  public Random randNum = new Random(randomSeed);

  //////////////////////////////////
  // Accessors for the properties.
  //////////////////////////////////

  /**
          Set the selection pressure. This value must be between 1 and 2.
          @param score new min rank.
  */
  public void setTournamentSize (int score) {
          tsize = score;
  }

  /**
          get the selection pressure.
          @returns the minimum rank
  */
  public int getTournamentSize () {
          return tsize;
  }

  /** this property is set for debugging. */
  protected boolean debug = false;

  //////////////////////////////////
  // Accessors for the properties.
  //////////////////////////////////
  /**
          set the debug flag.
          @param db is the boolean indicating debugging on or off.
   */
  public void setDebug(boolean score) {
    this.debug = score;
  }

  /**
          get the debug flag.
          @returns true if debug is on.
   */
  public boolean getDebug() {
    return this.debug;
  }


  // get random number seed
  public long getRandomSeed (){
    return randomSeed;
  }

  // set random number seed
  public void setRandomSeed (long seed){
    randomSeed = seed;
  }

  protected abstract void compute(Population p);

  protected int[] sample;
  public void performSelection(Population population) throws Exception {
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
    compute(population);
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
  }
}