package ncsa.d2k.modules.core.optimize.ga.emo.selection;

import ncsa.d2k.modules.core.optimize.ga.emo.*;
import ncsa.d2k.modules.core.optimize.ga.selection.*;

/**
 * An extension of StochasticUniversalSamplingObj that can be used in EMO.
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
class EMOStochasticUniversalSampling extends StochasticUniversalSamplingObj
  implements BinaryIndividualFunction, RealIndividualFunction, java.io.Serializable {

  public String getDescription() {
    return "";
  }

  public String getName() {
    return "Stochastic Universal Sampling";
  }

  public Property[] getProperties() {
    return null;
  }
}
