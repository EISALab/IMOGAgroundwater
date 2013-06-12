package ncsa.d2k.modules.core.optimize.ga.selection;

import ncsa.d2k.core.modules.*;
import java.io.Serializable;
import ncsa.d2k.modules.core.optimize.ga.*;

/**
        In this method, individuals are sorted according to fitness and thereafter are ranked
        only on the basis of their order, fitness is ignored. This is a more robust, but slower
        to converge method.<p>
        This method requires one property, selectivePressure. This property will range from 1 - 2, with
        higher values meaning more selective pressure. This will assign each module some number of slots
        in the next population by computing an alterntive set of fitness values based on the object
        function value provided by the population object. First, sort the population into assending order
        using the objective value for all members, then compute alterntive fitness values using the
        following formula:<pre>
        f(y) = 2.0 - SP + 2.0 * (SP - 1.0) * order (y) / popSize - 1
        </pre>
        where SP is the selective pressure, order (y) is the order of the individual y after the sort, and
        popSize is the size of the population.
 */
public class RankSelection
    extends SelectionModule {

  protected void createSelection() {
    selection = new RankSelectionObj();
  }

  /** selective pressure determins how rapidly the population converges. */
  //protected double selection_pressure = 1.6;

  //////////////////////////////////
  // Accessors for the properties.
  //////////////////////////////////

  /**
          Set the selection pressure. This value must be between 1 and 2.
          @param score new min rank.
   */
  public void setSelectivePressure(double score) {
    //selection_pressure = score;
    ( (RankSelectionObj) selection).setSelectivePressure(score);
  }

  /**
          get the selection pressure.
          @returns the minimum rank
   */
  public double getSelectivePressure() {
    //return selection_pressure;
    return ( (RankSelectionObj) selection).getSelectivePressure();
  }

  //////////////////////////////////
  // Info methods
  //////////////////////////////////
  /**
          This method returns the description of the various inputs.
          @return the description of the indexed input.
   */
  /*        public String getOutputInfo (int index) {
                  switch (index) {
                          case 0: return "";
                          default: return "No such output";
                  }
          }*/

  /**
          This method returns the description of the various inputs.
          @return the description of the indexed input.
   */
  /*        public String getInputInfo (int index) {
                  switch (index) {
                          case 0: return "";
                          default: return "No such input";
                  }
          }*/

  /**
          This method returns the description of the module.
          @return the description of the module.
   */
  public String getModuleInfo() {
    return "<html>  <head>      </head>  <body>    In this method, individuals are sorted according to fitness and thereafter     are ranked only on the basis of their relative position to one another.     This serves to prevent &quot;super-individuals&quot; from dominating the population     and causing premature convergence.<br><br>This method requires one     property, <b>selectivePressure </b> . This property will range from 1 - 2,     with higher values meaning more selective pressure. With the objective     values for the population computed, the members are sorted into an order     of ascending goodness. Then the rank for each member is computed using the     following formula:<br><br>f(y) = 2.0 - SP + 2.0 * (SP - 1.0) * order (y) /     popSize - 1<br><br>where SP is the selective pressure, order (y) is the     order of the individual y after the sort, and popSize is the size of the     population. With this done, stochastic universal sampling is performed,     however instead of the objective fitness value being used, the value of     f(y) is used.  </body></html>"
        ;
  }

  //////////////////////////////////
  // Type definitions.
  //////////////////////////////////

  /*        public String[] getInputTypes () {
       String[] types = {"ncsa.d2k.modules.core.optimize.ga.Population"};
                  return types;
          }
          public String[] getOutputTypes () {
       String[] types = {"ncsa.d2k.modules.core.optimize.ga.Population"};
                  return types;
          }*/

  /**
       This will select individuals on the basis of a ranked evaluation function.
          @param population is the population of individuals.
   */
  /*        protected void compute (Population population) {
                  Individual [] individual = population.getMembers ();
                  int popSize = population.size();
                  double SP = selection_pressure;
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
          }*/

  /**
   * Return the human readable name of the module.
   * @return the human readable name of the module.
   */
  public String getModuleName() {
    return "Rank";
  }

  /**
   * Return the human readable name of the indexed input.
   * @param index the index of the input.
   * @return the human readable name of the indexed input.
   */
  /*        public String getInputName(int index) {
                  switch(index) {
                          case 0:
                                  return "input0";
                          default: return "NO SUCH INPUT!";
                  }
          }*/

  /**
   * Return the human readable name of the indexed output.
   * @param index the index of the output.
   * @return the human readable name of the indexed output.
   */
  /*        public String getOutputName(int index) {
                  switch(index) {
                          case 0:
                                  return "output0";
                          default: return "NO SUCH OUTPUT!";
                  }
          }*/
}
