package ncsa.d2k.modules.core.optimize.ga.emo.mutation;

import ncsa.d2k.modules.core.optimize.ga.emo.*;
import ncsa.d2k.modules.core.optimize.ga.mutation.*;

/**
 * An extension of RealMutationObj that can be used in EMO and exposes the N property.
 */
class EMORealMutation
    extends RealMutationObj implements RealIndividualFunction, java.io.Serializable {

  private NProp n;

  EMORealMutation() {
    n = new NProp();
  }

  public String getName() {
    return "Real Mutation";
  }

  public String getDescription() {
    return "";
  }

  public Property[] getProperties() {
    return new Property[] {n};
  }
}

class NProp extends Property {
  boolean isDirty = false;

  NProp() {
    super(Property.DOUBLE, "n", "don't know", new Double(2));
  }

  public void setValue(Object v) {
    super.setValue(v);
    isDirty = true;
  }
}
