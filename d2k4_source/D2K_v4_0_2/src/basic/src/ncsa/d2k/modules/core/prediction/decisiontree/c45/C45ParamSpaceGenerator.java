package ncsa.d2k.modules.core.prediction.decisiontree.c45;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.prediction.AbstractParamSpaceGenerator;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.impl.*;

public class C45ParamSpaceGenerator extends AbstractParamSpaceGenerator {

  public static final String MIN_RATIO = "Minimum leaf ratio";

  /**
   * Returns a reference to the developer supplied defaults. These are
   * like factory settings, absolute ranges and definitions that are not
   * mutable.
   * @return the factory settings space.
   */
  protected ParameterSpace getDefaultSpace() {
    ParameterSpace psi = new ParameterSpaceImpl();
    String[] names = {MIN_RATIO};
    double[] min = {0};
    double[] max = {1};
    double[] def = {0.001};
    int[] res = {1000};
    int[] types = {ColumnTypes.DOUBLE};
    psi.createFromData(names, min, max, def, res, types);
    return psi;
  }

  /**
   * Return a name more appriate to the module.
   * @return a name
   */
  public String getModuleName() {
    return "C4.5 Decision Tree Param Space Generator";
  }

  /**
   * Return a list of the property descriptions.
   * @return a list of the property descriptions.
   */
  public PropertyDescription[] getPropertiesDescriptions() {
    PropertyDescription[] pds = new PropertyDescription[1];
//    pds[0] = new PropertyDescription(MIN_RATIO, MIN_RATIO,
//      "Ratio of the record on a leaf to in a tree.  The tree construction is "+
//      "terminated when this ratio is reached.");
    pds[0] = new PropertyDescription(MIN_RATIO, MIN_RATIO,
        "The minimum ratio of records in a leaf to the total number of records in the tree. "+
          "The tree construction is terminated when this ratio is reached.");
    return pds;
  }

}