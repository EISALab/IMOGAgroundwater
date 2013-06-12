package ncsa.d2k.modules.core.optimize.ga.selection;

import ncsa.d2k.core.modules.*;
import java.io.Serializable;
import ncsa.d2k.modules.core.optimize.ga.*;

/**
        Do a tounament selection with only one shuffle. Continue the tournament selection over and over
        again until the population is filled.
 */
public class Truncation
    extends SelectionModule {

  protected void createSelection() {
    selection = new TruncationObj();
  }

  /** tournament size. */
//        protected int tsize = 4;

  //////////////////////////////////
  // Accessors for the properties.
  //////////////////////////////////

  /**
          Set the Tournament size. Value msut be 2 or greater.
          @param score the size of the tounament.
   */
  public void setTournamentSize(int score) {
    //               tsize = score;
    selection.setTournamentSize(score);
  }

  /**
          get the tournament size.
          @returns the tournament size.
   */
  public int getTournamentSize() {
    //        return tsize;
    return selection.getTournamentSize();
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
       case 0: return "This population is the resulting progeny.";
                          default: return "No such output";
                  }
          }*/

  /**
          This method returns the description of the various inputs.
          @return the description of the indexed input.
   */
  /*        public String getInputInfo (int index) {
                  switch (index) {
       case 0: return "This is the next generation population to be selected on.";
                          default: return "No such input";
                  }
          }*/

  /**
          This method returns the description of the module.
          @return the description of the module.
   */
  public String getModuleInfo() {
    return "<html>  <head>      </head>  <body>    This module will take the given population of Individuals and select based     on fitness, or random draw. The result will be a population that was     selected. RankIndividuals is set if the individuals are to be ranked on     the basis of some fitness function. Gap is set if there is a generation     gap. 1.0 is no generation gap, and the smaller the number (&gt; 0) the     greater the gap.  </body></html>";
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
          Do tournament selection with only one shuffle, filling in the indexes of the selected
          individuals in the sample array.
          @param population is the population of individuals.
   */
  /*        protected void compute (Population population) {
           System.out.println("Trunc");
                  int s = tsize;
                  int popSize = population.size ();
                  int [] sorted = population.sortIndividuals ();
                  // Shuffle the individuals, then for each group of s individuals, pick
                  // the best one and put it into the new population. Continue to do this
                  // until the new population is complete.
                  int nextOriginal = 0;
                  int nextSample = 0;
                  for (; nextSample < popSize ;) {
       for (int i = 0; nextSample < popSize && i < s ;nextSample++, i++)
                                  sample [nextSample] = sorted [nextOriginal];
                          nextOriginal++;
                  }
          }*/

  /**
   * Return the human readable name of the module.
   * @return the human readable name of the module.
   */
  public String getModuleName() {
    return "Truncation";
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
