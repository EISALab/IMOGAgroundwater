package ncsa.d2k.modules.core.transform.binning;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.prediction.AbstractParamSpaceGenerator;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.impl.*;

public class AutoBinParamSpaceGenerator extends AbstractParamSpaceGenerator {

  public static final String BIN_METHOD = "Binning Method";
  public static final String ITEMS_PER_BIN = "Number of items per bin";
  public static final String NUMBER_OF_BINS = "Number of bins";

  /**
   * Returns a reference to the developer supplied defaults. These are
   * like factory settings, absolute ranges and definitions that are not
   * mutable.
   * @return the factory settings space.
   */
  protected ParameterSpace getDefaultSpace() {

    ParameterSpace psi = new ParameterSpaceImpl();
    String[] names = { BIN_METHOD, ITEMS_PER_BIN, NUMBER_OF_BINS};
    double[] min = { 0, 1, 2};
    double[] max = { 1, Integer.MAX_VALUE, Integer.MAX_VALUE};
    double[] def = { 0, 3, 3};
    int[] res = { 1, 1, 1};
    int[] types = {ColumnTypes.INTEGER, ColumnTypes.INTEGER, ColumnTypes.INTEGER};
    psi.createFromData(names, min, max, def, res, types);
    return psi;
  }

  /**
   * REturn a name more appriate to the module.
   * @return a name
   */
  public String getModuleName() {
    return "Auto Bin Param Space Generator";
  }

  /**
   * Return a list of the property descriptions.
   * @return a list of the property descriptions.
   */
  public PropertyDescription[] getPropertiesDescriptions() {
      PropertyDescription[] pds = new PropertyDescription[3];
      pds[0] = new PropertyDescription(BIN_METHOD, "Discretization Method - Space definition",
				       "The method to use for discretization.  Only 0 or 1 are valid values. " +
				       "Select 1 to create bins " +
				       "by weight.  This will create bins with an equal number of items in "+
				       "each slot.  Select 0 to discretize by specifying the number of bins. "+
				       "This will give equally spaced bins between the minimum and maximum for "+
				       "each scalar column.");
      pds[1] = new PropertyDescription(ITEMS_PER_BIN, "Number of Items per Bin - Space definition ",
				       "When binning by weight, this is the number of items" +
				       " that will go in each bin.   However, the bins may contain more or fewer values than "+
				       "<i>weight</i> values, depending on how many items equal the bin limits. Typically " +
				       "the last bin will contain less or equal to <i>weight</i> values and the rest of the " +
				       "bins will contain a number that is  equal or greater to <i>weight</i> values." +
				       "Minimum value must be 1, maximum is unlimited here but practically is limited by " +
				       "the number of values.");
      pds[2] = new PropertyDescription(NUMBER_OF_BINS, "Number of Bins - Space Definition",
				       "Define the number of bins absolutely. This will give equally spaced bins between "+
				       "the minimum and maximum for each scalar column. The minimum number of bins can be 2 " +
				       "and the maximum is unlimited. However, a very high number of bins is likely to results " +
				       "in a high number of bins with no values.");
    return pds;
  }
}
/**
 * QA comments.
 *
 * 01-05-04:
 * vered started qa process.
 * ready for basic.
*/