package ncsa.d2k.modules.core.optimize.ga.selection;

import ncsa.d2k.modules.core.optimize.ga.*;

public class TournamentWithoutReplacementObj
    extends Selection {

  /**
       Do tournament selection with replacement, filling in the indexes of the selected
          individuals in the sample array.
          @param population is the population of individuals.
   */
  protected void compute(Population population) {
    Individual[] individual = population.getMembers();
    int s = getTournamentSize();
    int popSize = sample.length;
    int[] shuffle = new int[popSize];
    for (int i = 0; i < popSize; i++) {
      shuffle[i] = i;

      // Shuffle the individuals, then for each group of s individuals, pick
      // the best one and put it into the new population. Continue to do this
      // until the new population is complete.
    }
    for (int nextSample = 0; nextSample < popSize; ) {
      population.shuffleIndices(shuffle);

      // Select one individual from each group of s the population
      // contains
      for (int i = 0; i < popSize && nextSample < popSize; ) {
        int best = shuffle[i];

        // From among the next s members, find the best one.
        for (int j = 0; j < s && i < popSize; j++, i++) {
          if (population.compareMembers(individual[shuffle[i]],
                                        individual[best]) > 0) {
            best = shuffle[i];
          }
        }
        sample[nextSample++] = best;
      }
    }
  }

}