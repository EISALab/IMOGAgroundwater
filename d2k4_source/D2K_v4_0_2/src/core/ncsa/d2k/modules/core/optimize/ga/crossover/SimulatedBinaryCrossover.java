package ncsa.d2k.modules.core.optimize.ga.crossover;


/**
                This module does simulated binary crossover. It will operate
                only on real number attributes, but simulates the process of
                binary crossover on binary individuals. For more information on
     Simulated Binary Crossover see "Self-Adaptive Genetic Algorithms with Simulated
                Binary Crossover", Deb, Beyer (1995).
 */
public class SimulatedBinaryCrossover
    extends CrossoverModule {

  protected void createCrossover() {
    crossover = new SimulatedBinaryCrossoverObj();
  }

  ///////////////////////////////
  // Properties.
  ///////////////////////////////

  //Random rand = new Random ();

  /** a measure of how different the children may be from the parents. */
//  protected double n = 2.0;

  /**
          sets n, a measure of how different the children may be from the parents. Higher values are
          more different.
          @param score is a boolean indicating if rankFlag is on or off.
   */
  public void setN(double score) {
//    n = score;
    ( (SimulatedBinaryCrossoverObj) crossover).setN(score);
  }

  /**
          get the rankFlag.
          @returns true if rankFlag is on.
   */
  public double getN() {
//    return n;
    return ( (SimulatedBinaryCrossoverObj) crossover).getN();
  }

  //////////////////////////////////
  // Info methods
  //////////////////////////////////
  /**
          This method returns the description of the various inputs.
          @return the description of the indexed input.
   */
/*  public String getOutputInfo(int index) {
    switch (index) {
      case 0:
        return
            "      The resulting output population which has been crossed.   ";
      default:
        return "No such output";
    }
  }*/

  /**
          This method returns the description of the various inputs.
          @return the description of the indexed input.
   */
/*  public String getInputInfo(int index) {
    switch (index) {
      case 0:
        return
            "      This is the input population, containing the mating pool.   ";
      default:
        return "No such input";
    }
  }*/

  /**
          This method returns the description of the module.
          @return the description of the module.
   */
  public String getModuleInfo() {
    return "<html>  <head>      </head>  <body>    This module will take the given population of Individuals and mate them,     crossing them at some random gene. The only property is the crossover     rate, which ranges between 0 and 1, and this is the probability that a     individual will cross.  </body></html>";
  }

  //////////////////////////////////
  // Type definitions.
  //////////////////////////////////

/*  public String[] getInputTypes() {
    String[] types = {
        "ncsa.d2k.modules.core.optimize.ga.Population"};
    return types;
  }

  public String[] getOutputTypes() {
    String[] types = {
        "ncsa.d2k.modules.core.optimize.ga.Population"};
    return types;
  }*/

  /**
          Do the selection.
          @param outV the array to contain output object.
   */
  /*public void doit () {
          // Our input argument is the population.
          Population population = (Population) this.pullInput( 0 );
          performCrossover(population);
          this.pushOutput (population, 0);
           }*/

  /*        public void performCrossover(Population population) {
       Individual [] individuals = (Individual []) population.getMembers ();
            // Compute the last individual to cross.
            int last = (int) (population.size () * generationGap);
            if (last == population.size ())
                    last--;
            double oneOverNPlus1 = 1.0 / (n + 1.0);
            for (int i = 0; i < last; i += 2) {
                    if (rand.nextDouble () < c_rate) {
                            // Swap the indicated genes.
                            if (debugging) {
       System.out.println ("--------------------");
                                    System.out.println ("Before SBX");
                                    population.printIndividual (i);
                                    population.printIndividual (i+1);
                            }
                            // Get the genes.
       double [] mom = (double []) individuals [i].getGenes ();
       double [] dad = (double []) individuals [i+1].getGenes ();
                            double u = rand.nextDouble ();
                            // compute beta sub q
                            double bq;
                            if (u <= 0.5)
                                    bq = Math.pow ((2.0 * u), oneOverNPlus1);
                            else
                                    bq = Math.pow (
       1.0 / (2 * (1.0 - u)), oneOverNPlus1);
                            double incB = 1.0 + bq;
                            double decB = 1.0 - bq;
                            // Update each gene.
                            Range [] traits = population.getTraits ();
       for (int geneIndex = 0 ; geneIndex < population.getNumGenes (); geneIndex++) {
       double newmom = 0.5 * ((incB * mom [geneIndex]) + (decB * dad [geneIndex]));
       double newdad = 0.5 * ((decB * mom [geneIndex]) + (incB * dad [geneIndex]));
                                    // Make sure the new mom value is in range.
                                    double max = traits [geneIndex].getMax ();
                                    double min = traits [geneIndex].getMin ();
                                    if (newmom < min)
                                            mom [geneIndex] = min;
                                    else
                                            if (newmom > max)
                                                    mom [geneIndex] = max;
                                            else
                                                    mom [geneIndex] = newmom;
                                    // check the dad value range.
                                    if (newdad < min)
                                            dad [geneIndex] = min;
                                    else
                                            if (newdad > max)
                                                    dad [geneIndex] = max;
                                            else
                                                    dad [geneIndex] = newdad;
                            }
                            individuals [i].setGenes (mom);
                            individuals [i+1].setGenes (dad);
                            // Debugging
                            if (debugging) {
                                    System.out.println ("-------After SBX ");
                                    population.printIndividual (i);
                                    population.printIndividual (i+1);
                            }
                    }
            }
          }*/

  /**
   * Return the human readable name of the module.
   * @return the human readable name of the module.
   */
  public String getModuleName() {
    return "Simulated Binary Crossover";
  }

  /**
   * Return the human readable name of the indexed input.
   * @param index the index of the input.
   * @return the human readable name of the indexed input.
   */
/*  public String getInputName(int index) {
    switch (index) {
      case 0:
        return "population";
      default:
        return "NO SUCH INPUT!";
    }
  }*/

  /**
   * Return the human readable name of the indexed output.
   * @param index the index of the output.
   * @return the human readable name of the indexed output.
   */
/*  public String getOutputName(int index) {
    switch (index) {
      case 0:
        return "population";
      default:
        return "NO SUCH OUTPUT!";
    }
  }*/
}
