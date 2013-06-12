package ncsa.d2k.modules.core.optimize.ga.emo.selection;

import ncsa.d2k.modules.core.optimize.ga.selection.*;

public class SelectionFactory {

  private static final int RANK_SELECTION = 0;
  private static final int STOCHASTIC_UNIVERSAL_SAMPLING = 1;
  private static final int TOURNAMENT_WITHOUT_REPLACEMENT = 2;
  private static final int TOURNAMENT_WITH_REPLACEMENT = 3;
  private static final int TRUNCATION = 4;

  public static final int NUM_SELECTION = 5;

  private static Selection createSelection(int type) {
    switch(type) {
      case(RANK_SELECTION):
        return new EMORankSelection();
      case(STOCHASTIC_UNIVERSAL_SAMPLING):
        return new EMOStochasticUniversalSampling();
      case(TOURNAMENT_WITHOUT_REPLACEMENT):
        return new EMOTournamentWithoutReplacement();
      case(TOURNAMENT_WITH_REPLACEMENT):
        return new EMOTournamentWithReplacement();
      case(TRUNCATION):
        return new EMOTruncation();
      default:
        return new EMOTruncation();
    }
  }

  public static final int getBinaryDefault() {
    return TOURNAMENT_WITHOUT_REPLACEMENT;
  }

  public static final int getRealDefault() {
    return TRUNCATION;
  }

  public static Selection[] createSelectionOptions() {
    Selection[] retVal = new Selection[NUM_SELECTION];
    for(int i = 0; i < NUM_SELECTION; i++) {
      retVal[i] = createSelection(i);
    }
    return retVal;
  }
}