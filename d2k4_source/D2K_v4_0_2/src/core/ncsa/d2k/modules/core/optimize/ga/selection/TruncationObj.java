package ncsa.d2k.modules.core.optimize.ga.selection;

import ncsa.d2k.modules.core.optimize.ga.*;

public class TruncationObj
    extends Selection {

  /**
          Do tournament selection with only one shuffle, filling in the indexes of the selected
          individuals in the sample array.
          @param population is the population of individuals.
   */
  protected void compute(Population population) {
    int s = tsize;
    int popSize = population.size();
    int[] sorted = population.sortIndividuals();

    // Shuffle the individuals, then for each group of s individuals, pick
    // the best one and put it into the new population. Continue to do this
    // until the new population is complete.
    int nextOriginal = 0;
    int nextSample = 0;
    for (; nextSample < popSize; ) {
      for (int i = 0; nextSample < popSize && i < s; nextSample++, i++) {
        sample[nextSample] = sorted[nextOriginal];
      }
      nextOriginal++;
    }
  }

}