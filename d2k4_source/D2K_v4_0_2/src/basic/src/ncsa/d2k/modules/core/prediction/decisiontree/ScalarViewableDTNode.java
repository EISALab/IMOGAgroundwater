package ncsa.d2k.modules.core.prediction.decisiontree;

import ncsa.d2k.modules.core.datatype.model.*;

public interface ScalarViewableDTNode extends ViewableDTNode {
  public Model getModel();
  public double[] getMinimumValues();
  public double[] getMaximumValues();
  public double getError();
  public double getErrorReduction();
}