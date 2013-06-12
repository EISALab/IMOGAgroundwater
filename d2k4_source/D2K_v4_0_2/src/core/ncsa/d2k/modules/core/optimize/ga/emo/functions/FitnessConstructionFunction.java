package ncsa.d2k.modules.core.optimize.ga.emo.functions;

import ncsa.d2k.modules.core.datatype.table.transformations.*;

/**
 * A fitness function calculated using a construction.
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class FitnessConstructionFunction
    extends ConstructionFunction
    implements FitnessFunction {
  private boolean minimizing;

  public FitnessConstructionFunction(Construction cons, boolean min) {
    super(cons);
    minimizing = min;
  }

  public boolean isMinimizing() {
    return minimizing;
  }
}
