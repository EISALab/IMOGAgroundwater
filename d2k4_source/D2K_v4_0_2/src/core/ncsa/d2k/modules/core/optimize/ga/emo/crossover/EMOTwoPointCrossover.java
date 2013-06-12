package ncsa.d2k.modules.core.optimize.ga.emo.crossover;

import ncsa.d2k.modules.core.optimize.ga.crossover.*;
import ncsa.d2k.modules.core.optimize.ga.emo.*;

/**
 * An extends of Crossover that can be used in EMO.
 * @author David Clutter
 * @version 1.0
 */
class EMOTwoPointCrossover
    extends Crossover implements BinaryIndividualFunction, java.io.Serializable {

  public String getName() {
    return "Two Point Crossover";
  }

  public String getDescription() {
    return "";
  }

  public Property[] getProperties() {
    return null;
  }
}