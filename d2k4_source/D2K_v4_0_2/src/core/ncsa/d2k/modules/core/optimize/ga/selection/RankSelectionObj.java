package ncsa.d2k.modules.core.optimize.ga.selection;

import ncsa.d2k.modules.core.optimize.ga.*;

public class RankSelectionObj extends Selection {
  
  /** selective pressure determins how rapidly the population converges. */
  protected double selection_pressure = 1.6;

  //////////////////////////////////
  // Accessors for the properties.
  //////////////////////////////////

  /**
          Set the selection pressure. This value must be between 1 and 2.
          @param score new min rank.
  */
  public void setSelectivePressure (double score) {
          selection_pressure = score;
  }

  /**
          get the selection pressure.
          @returns the minimum rank
  */
  public double getSelectivePressure () {
          return selection_pressure;
  }
  
  /**
          This will select individuals on the basis of a ranked evaluation function.
          @param population is the population of individuals.
  */
  protected void compute (Population population) {
          Individual [] individual = population.getMembers ();
          int popSize = population.size();
          double SP = getSelectivePressure();

          // Get the ordering of the individuals.
          int [] order = population.sortIndividuals ();

          // Compute the new fitnesses based solely on the position of the
          // population in the rank.
          // The formula for this fitness computation is rank based as follows:
          // fitness (y) = 2.0 - SP + 2.0 * (SP - 1.0) * order (y) / popSize - 1
          // where y is the individual, SP is selective pressure, order (y) is the
          // ordinal position of individual y relative to objective function.
          double t = 2.0 - SP;
          double y = 2.0 * (SP - 1.0);

          double [] fitnesses = new double [popSize];
          double newFitness = (t + (y * (popSize))) / (double) (popSize -1);
          double min_fitness = newFitness;
          fitnesses [order [0]] = newFitness;
          double avg = 0.0;
          for (int i = 1 ; i < popSize ; i++) {
                  newFitness = (t + (y * (popSize - i))) / (double) (popSize -1);
                  fitnesses [order [i]] = newFitness;
                  if (newFitness < min_fitness)
                          min_fitness = newFitness;
                  avg += newFitness;
          }

          avg /= popSize;

          // normalizer for proportional selection probabilities
          double factor = 1.0 / (avg - min_fitness);

          //
          // Now the stochastic universal sampling algorithm by James E. Baker!
          //
          int k = 0; 						// index of the next selected sample.
          double ptr = Math.random ();	// role the dice.
          double sum;						// control for selection loop.
          double expected;
          int i;
          for ( sum = i = 0; i < popSize; i++) {
                  expected = (fitnesses[i] - min_fitness) * factor;
                  for (sum += expected; sum > ptr; ptr++){
                          if (k == popSize)
                                  break;
                          this.sample[k++] = i;
                  }
          }
  }
  
}