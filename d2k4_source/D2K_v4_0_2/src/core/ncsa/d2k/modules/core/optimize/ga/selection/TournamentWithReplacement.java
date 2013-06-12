package ncsa.d2k.modules.core.optimize.ga.selection;

import ncsa.d2k.core.modules.*;
import java.io.Serializable;
import ncsa.d2k.modules.core.optimize.ga.*;

/**
     In tournament selection, s individuals are selected to compete in a tournament,
     the winner is awarded a space in the next generation. This process is repeated
     until teh new population is full. In tournament selection with replacement,
     individuals who have already be selected to compete in previous tournaments may
        also compete in sucessive tournaments.<p>
        The only property is the tournament size, denoted as S above.
 */
public class TournamentWithReplacement
    extends SelectionModule {

  protected void createSelection() {
    selection = new TournamentWithReplacementObj();
  }

  /** selective pressure determins how rapidly the population converges. */
  //protected int tsize = 4;

  //////////////////////////////////
  // Accessors for the properties.
  //////////////////////////////////

  /**
          Set the selection pressure. This value must be between 1 and 2.
          @param score new min rank.
   */
  public void setTournamentSize(int score) {
    //        tsize = score;
    ( (TournamentWithReplacementObj) selection).setTournamentSize(score);
  }

  /**
          get the selection pressure.
          @returns the minimum rank
   */
  public int getTournamentSize() {
    return ( (TournamentWithReplacementObj) selection).getTournamentSize();
    //      return tsize;
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
          }*/

  /*        public String[] getOutputTypes () {
       String[] types = {"ncsa.d2k.modules.core.optimize.ga.Population"};
                  return types;
          }*/

  /**
       Do tournament selection with replacement, filling in the indexes of the selected
          individuals in the sample array.
          @param population is the population of individuals.
   */
  /*        protected void compute (Population population) {
                  Individual [] individual = population.getMembers ();
                  int s = tsize;
                  int popSize = sample.length;
                  int [] shuffle = new int [popSize];
                  for (int i = 0 ; i < popSize ; i++) shuffle [i] = i;
                  // Shuffle the individuals, then for each group of s individuals, pick
                  // the best one and put it into the new population. Continue to do this
                  // until the new population is complete.
       for (int nextSample = 0 ; nextSample < popSize ;nextSample++) {
                          population.shuffleIndices (shuffle);
                          // Select one individual from each group of s the population
                          // contains
                          int best = shuffle [0];
                          // From among the next s members, find the best one.
                          for (int j = 0 ; j < s ; j++) {
       if (population.compareMembers (individual [shuffle [j]],
       individual [best]) > 0)
                                          best = shuffle [j];
                          }
                          sample [nextSample] = best;
                  }
          }*/

  /**
   * Return the human readable name of the module.
   * @return the human readable name of the module.
   */
  public String getModuleName() {
    return "Tournament With Replacement";
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
