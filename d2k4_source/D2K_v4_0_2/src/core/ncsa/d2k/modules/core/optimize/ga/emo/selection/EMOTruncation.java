package ncsa.d2k.modules.core.optimize.ga.emo.selection;

import ncsa.d2k.modules.core.optimize.ga.emo.*;
import ncsa.d2k.modules.core.optimize.ga.selection.*;

/**
 * An extension of TruncationObj that can be used in EMO.
 */
class EMOTruncation extends TruncationObj
  implements BinaryIndividualFunction, RealIndividualFunction, java.io.Serializable {

  public String getDescription() {
    return "";
  }

  public String getName() {
    return "Truncation";
  }

  public Property[] getProperties() {
    return null;
  }
}
