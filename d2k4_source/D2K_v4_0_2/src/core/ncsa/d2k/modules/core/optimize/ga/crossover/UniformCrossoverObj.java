package ncsa.d2k.modules.core.optimize.ga.crossover;

import ncsa.d2k.modules.core.optimize.ga.*;

public class UniformCrossoverObj
    extends Crossover {

  public void performCrossover(Population population) throws Exception {
    Individual[] individuals = population.getMembers();
    int[] x = new int[2];
    double xwin = 0.0;

    // Compute the last individual to cross.
    //int last = (int) (c_rate * population.size () * generationGap);
    int dad;
    for (int mom = 0; mom < population.size(); mom += 2) {
      dad = mom + 1;

      // choose two Crossover points */
     /*x[0] = (int) (Math.random () * population.getNumGenes ());
          x[1] = (int) (Math.random () * (population.getNumGenes () - 1));
                   if (x [1] < x [0]) {
             // Swap them
             int swap = x [1];
             x [1] = x [0];
             x [0] = swap;
                   } else
             x [1]++;
      */
     // Swap the indicated genes.
      if (debugging) {
        System.out.println("--------------------");
        System.out.println("Before crossover mom and dad ");
        population.printIndividual(mom);
        population.printIndividual(dad);
      }
      for (int i = 0; i < population.getNumGenes(); i++) {
        xwin = Math.random();
        if (xwin <= getCrossoverRate()) {
          x[0] = i;
          x[1] = i + 1;
          population.getMember(mom).crossAt(x, population.getMember(dad));
        }
      }
      if (debugging) {
        System.out.println("After crossover mon and dad ");
        population.printIndividual(mom);
        population.printIndividual(dad);
      }
    }
  }

}