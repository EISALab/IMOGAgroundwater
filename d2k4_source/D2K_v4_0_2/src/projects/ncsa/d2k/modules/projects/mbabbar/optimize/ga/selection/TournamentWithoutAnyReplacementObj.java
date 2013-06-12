package ncsa.d2k.modules.projects.mbabbar.optimize.ga.selection;

import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.optimize.ga.selection.*;

public class TournamentWithoutAnyReplacementObj
    extends Selection {

  /**
       Do tournament selection with replacement, filling in the indexes of the selected
          individuals in the sample array.
          @param population is the population of individuals.
          @author Meghna Babbar
   */
  protected void compute(Population population) {
    Individual[] individual = population.getMembers();
    int s = getTournamentSize();
    int popSize = sample.length;
    int[] shuffle = new int[popSize];

    // initialize the index of selected mate for the 'sample' array
    int nextSample =0;
    // for s tournaments there will be s loops of popsize
    for (int i=0;  i < s; i++){
        // initialize index array 'shuffle' to have all the popSize indices
        for (int j =0; j< popSize; j++){
            shuffle [j] = j;
        }
        // randomly shuffle all the indices in 'shuffle'
        population.shuffleIndices(shuffle);

        if (debug){
            System.out.println("Shuffle after shuffleIndices");
            for (int j =0; j< popSize; j++){
                System.out.println(shuffle [j]);
            }
        }
        // select mates from 's' groups of individuals
        for (int k =0; k < popSize; ){
              int best = shuffle[k];
              // From among the next s members, find the best one.
              for (int j = 0; j < s ; j++) {
                if (debug){
                    System.out.println("Tournament selection w/o repl compares members: "+shuffle[k+j]+ " and "+best);
                }
                if (population.compareMembers(individual[shuffle[k+j]],
                                              individual[best]) > 0) {
                  best = shuffle[k+j];
                }
              }

              // allocating the best individual to sample
              sample[nextSample] = best;
              // incrementing nextSample
              nextSample ++;

              // incrementing k by tournament size 's'.
              k = k + s;
        } // for k
    } // for i

  }

}