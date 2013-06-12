package ncsa.d2k.modules.core.prediction.decisiontree.rainforest;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.prediction.AbstractParamSpaceGenerator;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.impl.*;

public class SQLRainForestParamSpaceGenerator extends AbstractParamSpaceGenerator {

  public static final String MIN_RATIO = "Minimum Leaf Size Ratio";
  public static final String MODE_THRESHOLD = "Mode Threshold";
  public static final String BIN_NUMBER = "Bin Number";
  public static final String DOMINATE_RATIO = "Dominate Class Ratio";

  /**
   * Returns a reference to the developer supplied defaults. These are
   * like factory settings, absolute ranges and definitions that are not
   * mutable.
   * @return the factory settings space.
   */
  protected ParameterSpace getDefaultSpace() {
    ParameterSpace psi = new ParameterSpaceImpl();
    String[] names = {MIN_RATIO,MODE_THRESHOLD,BIN_NUMBER,DOMINATE_RATIO};
    double[] min = {0,0,1,1};
    double[] max = {1,Integer.MAX_VALUE,Integer.MAX_VALUE,Double.MAX_VALUE};
    double[] def = {0.001,200000,100,100};
    int[] res = {1000,1000,1000,1000};
    int[] types = {ColumnTypes.DOUBLE,ColumnTypes.DOUBLE,ColumnTypes.DOUBLE,ColumnTypes.DOUBLE};
    psi.createFromData(names, min, max, def, res, types);
    return psi;
  }

  /**
   * Return a name more appriate to the module.
   * @return a name
   */
  public String getModuleName() {
    return "Rain Forest Param Space Generator";
  }

  /**
   * Return a list of the property descriptions.
   * @return a list of the property descriptions.
   */
  public PropertyDescription [] getPropertiesDescriptions () {
    PropertyDescription [] pds = new PropertyDescription [4];
    pds[0] = new PropertyDescription (MIN_RATIO, MIN_RATIO, "The minimum ratio of records in a leaf to the total number of records in the tree. The tree construction is terminated when this ratio is reached.");
    pds[1] = new PropertyDescription (MODE_THRESHOLD, MODE_THRESHOLD, "If the number of examples is greater than this threshold, the in-database mode is used. Otherwise, the in-memory mode is used.");
    pds[2] = new PropertyDescription (BIN_NUMBER, BIN_NUMBER, "If the number of distinct values in a numeric attribute is greater than Bin Number, data is grouped into this number of bins.");
    pds[3] = new PropertyDescription (DOMINATE_RATIO, DOMINATE_RATIO, "Ratio of most-common class to second-most-common class. The tree construction is terminated when this ratio is reached.");
    return pds;
  }
}
