package ncsa.d2k.modules.core.optimize.ga.mutation;

import ncsa.d2k.modules.core.optimize.ga.*;

import java.util.*;

public class Mutation {

  protected long randomSeed = 2000;
  public Random randNum = new Random(randomSeed);

  // get random number seed
  public long getRandomSeed (){
      return randomSeed;
  }

  // set random number seed
  public void setRandomSeed (long seed){
      randomSeed = seed;
  }

  public void mutatePopulation(Population population) throws Exception {
    int i, j;
    // How many genes in the entire population.
    int totalGenes = population.getNumGenes () * population.size ();
    int numberGenes = population.getNumGenes ();
    double rate = getMutationRate();   // So the user can change this in the middle of a
                                                    // run.

    // Loop over every gene in the population.
    if (rate > 0.0)
            while (Mu_next < totalGenes) {

                    // This is the next individual to change.
                    i = Mu_next / numberGenes;

                    // The gene within that individual.
                    j = Mu_next % numberGenes;

                    // Was there a mutation?
                    if (debugging) {
                            System.out.println ("Mutated -> individual at "+i+" gene at "+j);
                            population.printIndividual (i);
                    }
                    population.getMember (i).mutateGene (j);
                    if (debugging) {
                            population.printIndividual (i);
                    }

                    // Next
                    if (rate < 1.0) {
                            int inc = (int) (Math.ceil (Math.log (randNum.nextDouble()) / Math.log (1.0 - rate)));
                            // int inc = (int) (Math.ceil (Math.log (Math.random()) / Math.log (1.0 - rate)));
                            if (inc == 0)
                                    Mu_next += 1;
                            else
                                    Mu_next += inc;
                    } else
                            Mu_next += 1;
            }

    Mu_next = Mu_next - totalGenes;
  }



  int Mu_next;
  Random rand = new Random(randomSeed);
  private boolean debugging = false;
  private double mutationRate;

  public void setMutationRate(double mr) {
    mutationRate = mr;
  }

  public double getMutationRate() {
    return mutationRate;
  }

  /**
          set the rankFlag.
          @param score is a boolean indicating if rankFlag is on or off.
   */
  public void setDebugging(boolean score) {
    this.debugging = score;
  }

  /**
          get the rankFlag.
          @returns true if rankFlag is on.
   */
  public boolean getDebugging() {
    return this.debugging;
  }

}