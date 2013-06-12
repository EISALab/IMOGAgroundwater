package ncsa.d2k.modules.core.optimize.ga.crossover;


/**
  This module will do a uniform crossover on the mating population. For each
  pair of mates in the mating pool, points are randomly selected along the length of chromosome
     , and passed to the individuals who perform the actual crossover.
  <p>
  There are three properties in this module:
  <DL>
    <DT>debugging</dt>
      <DD>set this flag to see what the module is doing. This produces a lot of output.</dd>
     <DT>CrossoverRate</DT>
     <DD>the probability that two mated individuals will experience crossover.</DD>
  </DL>
  <p>
  This module will also activate a trigger whenever the ga is completely done. This module can be subclassed by
  other crossover modules to obtain the properties.
 */
public class UniformCrossoverModule
    extends CrossoverModule {
  
  
  ////////////////////////////////
  // property descripions
  // does not use generation gap

  protected void createCrossover() {
    crossover = new UniformCrossoverObj();
  }

  ///////////////////////////////
  // Properties.
  ///////////////////////////////

//	protected boolean debugging = false;

  /**
   set the debug flag.
   @param debug is a boolean indicating if debugging is on or off.
   */
  /*	public void setDebugging (boolean score) {
    this.debugging = score;
   }*/

  /**
   get the debug flag.
   @returns true if debugging is on.
   */
  /*	public boolean getDebugging () {
    return this.debugging;
   }*/

  /** The probability that any two individuals will cross at a point on the chromosome. */
//	protected double c_rate = 0.75; // 0.5; //

  /**
   set the probability that any two individuals will cross.
   @param score the new probability that any two individuals will cross.
   */
  /*	public void setCrossoverRate (double score) {
    c_rate = score;
   }*/

  /**
   get the crossover rate.
   @returns the crossover rate.
   */
  /*	public double getCrossoverRate () {
    return c_rate;
   }*/

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
  }*/

/*  public String[] getOutputTypes() {
    String[] types = {
        "ncsa.d2k.modules.core.optimize.ga.Population"};
    return types;
  }*/

  /**
   Do the crossover.
   @param outV the array to contain output object.
   */
//	protected int [] x = new int [2];
  /*	public void doit () {
    // Our input argument is the population.
    Population population = (Population) this.pullInput (0);
                  performCrossover(population);
    this.pushOutput (population, 0);
   }*/

  /*        public void performCrossover(Population population) {
            Individual [] individuals = population.getMembers ();
            int [] x = new int[2] ;
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
  /*                  if (debugging) {
                            System.out.println ("--------------------");
       System.out.println ("Before crossover mom and dad ");
                            population.printIndividual (mom);
                            population.printIndividual (dad);
                    }
                    for (int i=0; i < population.getNumGenes (); i++){
                        xwin = Math.random ();
                        if (xwin <= c_rate){
                            x[0] = i;
                            x[1] = i+1;
       population.getMember (mom).crossAt (x, population.getMember (dad));
                        }
                    }
                    if (debugging) {
       System.out.println ("After crossover mon and dad ");
                            population.printIndividual (mom);
                            population.printIndividual (dad);
                    }
            }
          }*/

  /**
   * Return the human readable name of the module.
   * @return the human readable name of the module.
   */
  public String getModuleName() {
    return "Uniform Crossover";
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
