package ncsa.d2k.modules.core.optimize.ga.mutation;

import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.optimize.util.*;

public class RealMutationObj extends Mutation {
  
  /** a measure of how different the children may be from the parents. */
        protected double n = 2.0;

  /**
          sets n, a measure of how different the children may be from the parents. Higher values are
          more different.
          @param score is a boolean indicating if rankFlag is on or off.
  */
  public void setN (double score) {
                n = score;
  }

  /**
          get the rankFlag.
          @returns true if rankFlag is on.
  */
  public double getN () {
        return n;
  }

  public void mutatePopulation(Population population) {
    Individual[] individuals = (Individual[]) population.getMembers();

    double oneOverNPlus1 = 1.0 / (getN() + 1.0);

    // Loop over the entire population.
    int last = population.size();
    Range[] traits = population.getTraits();

    // for each member of the population,
    for (int i = 0; i < last; i++) {
      double[] genes = (double[]) individuals[i].getGenes();

      // for each gene of the individual
      for (int k = 0; k < genes.length; k++) {
          double r = rand.nextDouble();

        if (rand.nextDouble() < getMutationRate()) {
          DoubleRange dr = (DoubleRange) traits[k];
          double gene = genes[k];
          double paramLower = dr.getMin();
          double paramUpper = dr.getMax();

          // Swap the indicated genes.
          if (getDebugging()) {
            System.out.println("--------------------");
            System.out.println("Before SBX");
            population.printIndividual(i);
            //population.printIndividual(i + 1);
          }

          // compute beta sub q
          double delta;
          if ( (gene - paramLower) < (paramUpper - gene))
            delta = (gene - paramLower) / (paramUpper - paramLower);
          else
            delta = (paramUpper - gene) / (paramUpper - paramLower);

          double u = rand.nextDouble();
          double deltaq;
          if (u <= 0.5) {
            double xy = 1 - delta;
            double val = 2 * u + (1 - 2 * u) * (Math.pow(xy, (getN() + 1)));
            deltaq = Math.pow(val, oneOverNPlus1) - 1;
          }
          else {
            double xy = 1 - delta;
            double val = 2 * (1 - u) + 2 * (u - 0.5) * (Math.pow(xy, (getN() + 1)));
            deltaq = 1 - (Math.pow(val, oneOverNPlus1));
          }

          /*Change the value for the parent */
          genes[k] = gene + deltaq * (paramUpper - paramLower);
          /*check for limits*/
          if(genes[k] >= paramUpper) genes[k] = paramUpper;
          if(genes[k] <= paramLower) genes[k] = paramLower;
          
          individuals[i].setGenes(genes);
          

          // Debugging
          if (getDebugging()) {
            System.out.println("-------After RealMutation ");
            population.printIndividual(i);
          }
        }
      }
    }
  }
}