package ncsa.d2k.modules.core.optimize.ga.emo.mutation;

import ncsa.d2k.modules.core.optimize.ga.emo.*;
import ncsa.d2k.modules.core.optimize.ga.mutation.*;

/**
 * An extension of Mutation that can be used in EMO.
 */
class EMOMutation
    extends Mutation implements BinaryIndividualFunction, java.io.Serializable {

  public String getName() {
    return "Mutation";
  }

  public String getDescription() {
    return "";
  }

  public Property[] getProperties() {
    return null;
  }
}
