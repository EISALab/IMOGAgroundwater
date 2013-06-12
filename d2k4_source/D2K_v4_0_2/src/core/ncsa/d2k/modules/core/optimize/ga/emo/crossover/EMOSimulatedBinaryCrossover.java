package ncsa.d2k.modules.core.optimize.ga.emo.crossover;

import ncsa.d2k.modules.core.optimize.ga.crossover.*;
import ncsa.d2k.modules.core.optimize.ga.emo.*;

/**
 * A extension of SimulatedBinaryCrossover that exposes the property N.
 * @author David Clutter
 * @version 1.0
 */
class EMOSimulatedBinaryCrossover
    extends SimulatedBinaryCrossoverObj implements RealIndividualFunction, java.io.Serializable {

  private NProp n;

  EMOSimulatedBinaryCrossover() {
    n = new NProp();
  }

  public String getName() {
    return "Simulated Binary Crossover";
  }

  public String getDescription() {
    return "";
  }

  public Property[] getProperties() {
    return new Property[] {n};
  }

  private class NProp extends Property {
    private boolean isDirty = false;

    NProp() {
      super(Property.DOUBLE, "n", "don't know", new Double(2));
    }

    public void setValue(Object val) {
      super.setValue(val);
      isDirty = true;
    }
  }
}
