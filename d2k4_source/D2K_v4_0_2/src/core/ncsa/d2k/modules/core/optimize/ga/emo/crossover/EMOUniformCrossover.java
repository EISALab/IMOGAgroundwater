package ncsa.d2k.modules.core.optimize.ga.emo.crossover;

import ncsa.d2k.modules.core.optimize.ga.crossover.*;
import ncsa.d2k.modules.core.optimize.ga.emo.*;

/**
 * An extension of UniformCrossoverObj that can be used in EMO.
 */
class EMOUniformCrossover
    extends UniformCrossoverObj implements BinaryIndividualFunction, java.io.Serializable {

  public String getName() {
    return "Uniform Crossover";
  }

  public String getDescription() {
    return "";
  }

  public Property[] getProperties() {
    return null;
  }
}
