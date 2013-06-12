package ncsa.d2k.modules.core.optimize.ga.selection;

import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.optimize.util.*;

public class StochasticUniversalSamplingObj
    extends Selection {

  /**
          This will select individuals on the basis of an unranked evaluation function, namely
          the stochastic universal selection without replacement also known as expected value model.
          @param population is the population of individuals.
   */
  protected void compute(Population population) {
    int popSize = population.size();
    double worst = population.getWorstFitness();

    // normalizer for proportional selection probabilities
    double avg = ( (SOPopulation) population).getAverageFitness();
    boolean mf = worst < avg;
    double factor = mf ?
        1.0 / (avg - worst) :
        1.0 / (worst - avg);

    //
    // Now the stochastic universal sampling algorithm by James E. Baker!
    //
    int k = 0; // index of the next selected sample.
    double ptr = Math.random(); // role the dice.
    double sum = 0; // control for selection loop.
    double expected;
    int i = 0;

    for (; i < popSize; i++) {

      // Get the member to test.
      SOSolution member = (SOSolution) population.getMember(i);
      if (mf) {
        expected = (member.getObjective() - worst) * factor;
      }
      else {
        expected = (worst - member.getObjective()) * factor;

        // the magnitude of expected will determine the number of
        // progeny of the individual to survive.
      }
      for (sum += expected; sum > ptr; ptr++) {
        this.sample[k++] = i;
      }
    }
  }

}