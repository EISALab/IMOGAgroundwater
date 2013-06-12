package ncsa.d2k.modules.core.optimize.ga.crossover;

import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.optimize.util.*;
import java.util.*;

public class SimulatedBinaryCrossoverObj
    extends Crossover {

  /** a measure of how different the children may be from the parents. */
  protected double n = 2.0;
  protected long randomSeed = 1000;
  protected Random rand = new Random(randomSeed);

  /**
          sets n, a measure of how different the children may be from the parents. Higher values are
          more different.
          @param score is a boolean indicating if rankFlag is on or off.
   */
  public void setN(double score) {
    n = score;
  }

  /**
          get the rankFlag.
          @returns true if rankFlag is on.
   */
  public double getN() {
    return n;
  }

  public void performCrossover(Population population) throws Exception {
    Individual[] individuals = (Individual[]) population.getMembers();

    // Compute the last individual to cross.
     int last = (int) (population.size() * generationGap);
    if (last == population.size()) {
      last--;

    }
    double oneOverNPlus1 = 1.0 / (n + 1.0);
    for (int i = 0; i < last; i += 2) {
        double r = rand.nextDouble();
        System.out.println("r");
        System.out.println(r);
        
      if (r < getCrossoverRate()) {
        // Swap the indicated genes.
        if (debugging) {
          System.out.println("--------------------");
          System.out.println("Before SBX");
          population.printIndividual(i);
          population.printIndividual(i + 1);
        }

        // Get the genes.
        double[] mom = (double[]) individuals[i].getGenes();
        double[] dad = (double[]) individuals[i + 1].getGenes();
        double u = rand.nextDouble();
        
        System.out.println("crossover");
        
        System.out.println("u");
        System.out.println(u);

        // compute beta sub q
        double bq;
        if (u <= 0.5) {
          bq = Math.pow( (2.0 * u), oneOverNPlus1);
        }
        else {
          bq = Math.pow(
              1.0 / (2 * (1.0 - u)), oneOverNPlus1);

        }
        double incB = 1.0 + bq;
        double decB = 1.0 - bq;

        // Update each gene.
        Range[] traits = population.getTraits();
        for (int geneIndex = 0; geneIndex < population.getNumGenes(); geneIndex++) {
          double newmom = 0.5 *
              ( (incB * mom[geneIndex]) + (decB * dad[geneIndex]));
          double newdad = 0.5 *
              ( (decB * mom[geneIndex]) + (incB * dad[geneIndex]));

          // Make sure the new mom value is in range.
          double max = traits[geneIndex].getMax();
          double min = traits[geneIndex].getMin();
          if (newmom < min) {
            mom[geneIndex] = min;
          }
          else
          if (newmom > max) {
            mom[geneIndex] = max;
          }
          else {
            mom[geneIndex] = newmom;

            // check the dad value range.
          }
          if (newdad < min) {
            dad[geneIndex] = min;
          }
          else
          if (newdad > max) {
            dad[geneIndex] = max;
          }
          else {
            dad[geneIndex] = newdad;
          }
        }
        individuals[i].setGenes(mom);
        individuals[i + 1].setGenes(dad);

        // Debugging
        if (debugging) {
          System.out.println("-------After SBX ");
          population.printIndividual(i);
          population.printIndividual(i + 1);
        }
      }
    }
  }

}