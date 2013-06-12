package ncsa.d2k.modules.core.optimize.ga.emo.selection;

import ncsa.d2k.modules.core.optimize.ga.emo.*;
import ncsa.d2k.modules.core.optimize.ga.selection.*;

/**
 * an extension of TournamentWithReplacemtnObj that can be used in EMO.
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
class EMOTournamentWithReplacement extends TournamentWithReplacementObj
  implements BinaryIndividualFunction, RealIndividualFunction, java.io.Serializable {

  public String getDescription() {
    return "";
  }

  public String getName() {
    return "Tournament With Replacement";
  }

  public Property[] getProperties() {
    return null;
  }
}
