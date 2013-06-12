package ncsa.d2k.modules.core.optimize.ga.emo.selection;

import ncsa.d2k.modules.core.optimize.ga.emo.*;
import ncsa.d2k.modules.core.optimize.ga.selection.*;

/**
 * an extension of TournamentWithoutReplacementObj that can be used in emo.
 */
class EMOTournamentWithoutReplacement extends TournamentWithoutReplacementObj
  implements BinaryIndividualFunction, RealIndividualFunction, java.io.Serializable {

  public String getDescription() {
    return "";
  }

  public String getName() {
    return "Tournament Without Replacement";
  }

  public Property[] getProperties() {
    return null;
  }
}
