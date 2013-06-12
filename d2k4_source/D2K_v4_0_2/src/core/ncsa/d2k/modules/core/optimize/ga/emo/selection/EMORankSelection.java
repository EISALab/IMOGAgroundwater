package ncsa.d2k.modules.core.optimize.ga.emo.selection;

import ncsa.d2k.modules.core.optimize.ga.emo.*;
import ncsa.d2k.modules.core.optimize.ga.selection.*;

/**
 * An extension of RankSelectionObj that can be used in EMO.  THe property
 * pressureProp is exposed for use in InputParameters.
 */
class EMORankSelection extends RankSelectionObj
    implements BinaryIndividualFunction, RealIndividualFunction, java.io.Serializable {

  private PressureProp pp;

  EMORankSelection() {
    pp = new PressureProp();
  }

  public String getName() {
    return "Rank Selection";
  }

  public String getDescription() {
    return "";
  }

  public Property[] getProperties() {
    return new Property[] {pp};
  }

  private class PressureProp extends Property {
    boolean isDirty = false;

    PressureProp() {
      super(Property.DOUBLE, "selective pressure", "don't know", new Double(2));
    }

    public void setValue(Object val) {
      super.setValue(val);
      isDirty = true;
    }
  }
}