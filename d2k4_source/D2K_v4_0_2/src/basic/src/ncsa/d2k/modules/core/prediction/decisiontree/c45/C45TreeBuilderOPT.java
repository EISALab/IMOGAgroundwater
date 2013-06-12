package ncsa.d2k.modules.core.prediction.decisiontree.c45;

import java.beans.PropertyVetoException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import ncsa.d2k.core.modules.ReentrantComputeModule;
import ncsa.d2k.modules.core.datatype.parameter.ParameterPoint;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;
import ncsa.d2k.modules.core.datatype.table.Table;
import ncsa.d2k.modules.core.datatype.table.util.TableUtilities;
import ncsa.d2k.modules.core.prediction.decisiontree.CategoricalDecisionTreeNode;
import ncsa.d2k.modules.core.prediction.decisiontree.DecisionTreeNode;
import ncsa.d2k.modules.core.prediction.decisiontree.NumericDecisionTreeNode;

/**
 Build a C4.5 decision tree.  The tree is build recursively, always choosing
 the attribute with the highest information gain as the root.  The
 gain ratio is used, whereby the information gain is divided by the
 information given by the size of the subsets that each branch creates.
 This prevents highly branching attributes from always becoming the root.
 The minimum number of records per leaf can be specified.  If a leaf is
 created that has less than the minimum number of records per leaf, the
 parent will be turned into a leaf itself.
 @author David Clutter
 */
public class C45TreeBuilderOPT
    extends ReentrantComputeModule {

  public static void main(String[] args) {
    //double d1 = (double)9/(double)14;
    //double d2 = (double)5/(double)14;
    //double dd[] = {d1, d2};
    //System.out.println(lg(2));
    //System.out.println(entropy(dd));
    int[] tals = {
        9, 5};
    System.out.println(info(tals));
  }

  /**
   Calculate the entropy given probabilities.  The entropy is the amount
   of information conveyed by a potential split.
   entropy(p1, p2,...pn) = -p1*lg(p1) - p2*lg(p2) -...-pn*lg(pn)
   @param data the probabilities
   @return the information conveyed by the probabilities
    /
    private static final double entropy(double[] data) {
   double retVal = 0;
   for(int i = 0; i < data.length; i++) {
    retVal += -1*data[i]*lg(data[i]);
   }
   return retVal;
    }*/

  /**
   Return the binary log of a number.  This is defined as
   x such that 2^x = d
   @param d the number to take the binary log of
   @return the binary log of d
   */
  private static final double lg(double d) {
    return Math.log(d) / Math.log(2.0);
  }

  private static final double info(int[] tallies) {
    int total = 0;
    for (int i = 0; i < tallies.length; i++) {
      total += tallies[i];

    }
    double dtot = (double) total;

    double retVal = 0;
    for (int i = 0; i < tallies.length; i++) {
      retVal -= ( (tallies[i] / dtot) * lg(tallies[i] / dtot));

    }
    return retVal;
  }

  /**
   * Exapand the size of an array by one.  Creates a new array and copies
   * all the old entries.
   * @param orig
   * @return an array of size orig.length+1 with all the entries from orig copied over
   */
  private static int[] expandArray(int[] orig) {
    int[] newarray = new int[orig.length + 1];
    System.arraycopy(orig, 0, newarray, 0, orig.length);
    return newarray;
  }

  /**
   The threshold for information gain
    /
    private double infoGainThreshold = 0.005;
    public void setInfoGainThreshold(double d) {
   infoGainThreshold = d;
    }
    public double getInfoGainThreshold() {
   return infoGainThreshold;
    }*/

  /**
   * Turns debugging statements on or off.
   */
  private boolean debug = false;

  protected void setDebug(boolean b) {
    debug = b;
  }

  protected boolean getDebug() {
    return debug;
  }

  /*private boolean useGainRatio = true;
    public void setUseGainRatio(boolean b) {
   useGainRatio = b;
    }
    public boolean getUseGainRatio() {
   return useGainRatio;
    }*/

//  private int minimumRecordsPerLeaf = 2;
  private double minimumRatioPerLeaf = 0.001;

  /*public void setMinimumRecordsPerLeaf(int num) {
   minimumRecordsPerLeaf = num;
    }*/

/*  public int getMinimumRecordsPerLeaf() {
    return minimumRecordsPerLeaf;
  }*/

    protected void setMinimumRatioPerLeaf(double d) throws PropertyVetoException {
	if( d < 0 || d > 1)
	    throw new PropertyVetoException("minimumRatioPerLeaf must be between 0 and 1",null);
	minimumRatioPerLeaf = d;
    }

  protected double getMinimumRatioPerLeaf() {
    return minimumRatioPerLeaf;
  }

  private static NumberFormat nf;
  private static final String LESS_THAN = " < ";
  private static final String GREATER_THAN_EQUAL_TO = " >= ";
  private static final int NUMERIC_VAL_COLUMN = 0;
  private static final int NUMERIC_OUT_COLUMN = 1;

  /**
   Calculate the average amount of information needed to identify a class
   of the output column for a numeric attribute.  This is the sum of
   the information given by the examples less than the split value and the
   information given by the examples greater than or equal to the split
   value.
   @param vt the data set
   @param splitVal the split
   @param examples the list of examples, which correspond to rows of
    the table
   @param attCol the column we are interested in
   @param outCol the output column
   @return the information given by a numeric attribute with the given
    split value
   */
  private double numericAttributeInfo(Table t, double splitVal,
                                      int[] examples, int attCol, int outCol) {

    int lessThanTot = 0;
    int greaterThanTot = 0;

    int[] lessThanTally = new int[0];
    int[] greaterThanTally = new int[0];
    HashMap lessThanIndexMap = new HashMap();
    HashMap greaterThanIndexMap = new HashMap();

    // for each example, check if it is less than or greater than/equal to
    // the split point.
    // increment the proper tally
    for (int i = 0; i < examples.length; i++) {
      int idx = examples[i];

      double val = t.getDouble(idx, attCol);
      String out = t.getString(idx, outCol);

      int loc;
      if (val < splitVal) {
        //Integer in = (Integer)lessThanIndexMap.get(out);
        if (lessThanIndexMap.containsKey(out)) {
          //if(in != null) {
          Integer in = (Integer) lessThanIndexMap.get(out);
          loc = in.intValue();
          lessThanTally[loc]++;
        }
        // found a new one..
        else {
          lessThanIndexMap.put(out, new Integer(lessThanIndexMap.size()));
          lessThanTally = expandArray(lessThanTally);
          lessThanTally[lessThanTally.length - 1] = 1;
        }
        lessThanTot++;
      }
      else {
        //Integer in = (Integer)greaterThanIndexMap.get(out);
        if (greaterThanIndexMap.containsKey(out)) {
          //if(in != null) {
          Integer in = (Integer) greaterThanIndexMap.get(out);
          loc = in.intValue();
          greaterThanTally[loc]++;
        }
        // found a new one..
        else {
          greaterThanIndexMap.put(out, new Integer(greaterThanIndexMap.size()));
          greaterThanTally = expandArray(greaterThanTally);
          greaterThanTally[greaterThanTally.length - 1] = 1;
        }
        greaterThanTot++;
      }
    }

    // now that we have tallies of the outputs for this att value
    // we can calculate the information value.

    double linfo = info(lessThanTally);
    double ginfo = info(greaterThanTally);

    return (lessThanTot / (double) examples.length) * linfo +
        (greaterThanTot / (double) examples.length) * ginfo;

    // get the probablities for the examples less than the split
    //double[] lesserProbs = new double[lessThanTally.length];
    //for(int i = 0; i < lessThanTally.length; i++)
    //	lesserProbs[i] = ((double)lessThanTally[i])/((double)lessThanTot);

    //double[] greaterProbs = new double[greaterThanTally.length];
    //for(int i = 0; i < greaterThanTally.length; i++)
    //	greaterProbs[i] = ((double)greaterThanTally[i])/((double)greaterThanTot);

    // return the sum of the information given on each side of the split
    //return (lessThanTot/(double)examples.length)*entropy(lesserProbs) +
    //	(greaterThanTot/(double)examples.length)*entropy(greaterProbs);
  }

  /**
   Find the best split value for the given column with the given examples.
   The best split value will be the one that gives the maximum information.
   This is found by sorting the set of examples and testing each possible
   split point.  (The possible split points are located halfway between
   unique values in the set of examples)  The information on each possible
   split is then calculated.
   @param t the table
   @param attCol the index of the attribute column
   @param outCol the index of the output column
   @param examples the list of examples, which correspond to rows of the
    table
   @return the split value for this attribute that gives the maximum
    information
   */
  private EntrSplit findSplitValue(Table t, int attCol, int outCol,
                                   int[] examples) {
    // copy the examples into a new table
    //DoubleColumn dc = new DoubleColumn(examples.length);
    //StringColumn sc = new StringColumn(examples.length);

    int[] cols = {
        attCol};

    int[] examCopy = new int[examples.length];
    System.arraycopy(examples, 0, examCopy, 0, examples.length);
    int[] sortedExamples = TableUtilities.multiSort(t, cols, examCopy);

    //double mean = TableUtilities.mean(t, attCol);

    /*for(int i = 0; i < examples.length; i++) {
              int rowIdx = examples[i];
      dc.setDouble(t.getDouble(rowIdx, attCol), i);
     sc.setString(t.getString(rowIdx, outCol), i);
       }
       Column[] cols = {dc, sc};
       MutableTableImpl vt = (MutableTableImpl)DefaultTableFactory.getInstance().createTable(cols);
       // sort the table
       vt.sortByColumn(0);
     */

    // each row of the new table is an example
    /*int[] exams = new int[vt.getNumRows()];
       for(int i = 0; i < vt.getNumRows(); i++)
        exams[i] = i;
     */

    // now test the possible split values.  these are the half-way point
    // between two adjacent values.  keep the highest.
    double splitValue;
    double highestGain = Double.NEGATIVE_INFINITY;

    // this is the return value
    EntrSplit split = new EntrSplit();

    double lastTest = /*vt*/t.getDouble(sortedExamples[0], attCol);
    boolean allSame = true;
    double baseGain = outputInfo(t, outCol, examples);
    //double baseGain = outputInfo(t, outCol, sortedExamples);

/*    double gain = baseGain = numericAttributeInfo(t, mean,
                                                  examples, attCol, outCol);

    double spliter = splitInfo(t, attCol, mean, examples);
    gain /= spliter;

    split.gain = gain;
    split.splitValue = mean;
    return split;
 */

    // test the halfway point between the last value and the current value
    for(int i = 1; i < sortedExamples.length; i++) {
     double next = t.getDouble(sortedExamples[i], attCol);
     if(next != lastTest) {
      allSame = false;
      double testSplitValue = ((next-lastTest)/2)+lastTest;
      // count up the number greater than and the number less than
      // the split value and calculate the information gain
      //double gain = outputEntropy(table, outputs[0], examples);
      //double gain = baseGain - numericAttributeInfo(vt, testSplitValue,
      //	exams, NUMERIC_VAL_COLUMN, NUMERIC_OUT_COLUMN);
      double gain = baseGain - numericAttributeInfo(t, testSplitValue,
       sortedExamples, attCol, outCol);
      //double spliter = splitInfo(vt, NUMERIC_VAL_COLUMN, testSplitValue, exams);
      double spliter = splitInfo(t, attCol, testSplitValue, sortedExamples);
      gain /= spliter;
      lastTest = next;
      //double gain = numericAttributeEntropy(vt, testSplitValue,
      //	exams, NUMERIC_VAL_COLUMN, NUMERIC_OUT_COLUMN);
      // if the gain is better than what we have already seen, save
      // it and the splitValue
      if(gain >= highestGain) {
       highestGain = gain;
       splitValue = testSplitValue;
       split.gain = gain;
       split.splitValue = testSplitValue;
      }
     }
       }
       if(allSame)
     return null;
       return split;
  }

  /**
   Find the information gain for a numeric attribute.  The best split
   value is found, and then the information gain is calculated using
   the split value.
   @param t the table
   @param attCol the index of the attribute column
   @param outCol the index of the output column
   @param examples the list of examples, which correspond to rows of
    the table
   @return an object holding the gain and the best split value of
    the column
   */
  private EntrSplit numericGain(Table t, int attCol, int outCol, int[] examples) {
    //if(debug)
    //	System.out.println("Calc numericGain: "+t.getColumnLabel(attCol)+" size: "+examples.length+" out: "+t.getColumnLabel(outCol));
    double gain = outputInfo(t, outCol, examples);

    //double splitVal = findSplitValue(attCol, examples);
    EntrSplit splitVal = findSplitValue(t, attCol, outCol, examples);
    //double numEntr = numericAttributeEntropy(table, splitVal.splitValue, examples,
    //	attCol, outputs[0]);

    //gain -= splitVal.gain;
    //if(useGainRatio) {
    //double spliter = splitInfo(table, attCol, splitVal.splitValue, examples);
    //gain /= spliter;
    //}

    return splitVal;
  }

  /**
   A simple structure to hold the gain and split value of a column.
   */
  private final class EntrSplit {
    double splitValue;
    double gain;

    EntrSplit() {}

    EntrSplit(double s, double g) {
      splitValue = s;
      gain = g;
    }
  }

  /**
   Find the information gain for a categorical attribute.  The gain
   ratio is used, where the information gain is divided by the
   split information.  This prevents highly branching attributes
   from becoming dominant.
   @param t
   @param attCol the index of the attribute column
   @param examples the list of examples, which correspond to rows of
    the table
   @return the gain of the column
   */
  private double categoricalGain(Table t, int attCol, int outCol,
                                 int[] examples) {
    //if(debug)
    //	System.out.println("Calc categoricalGain: "+table.getColumnLabel(attCol)+" size: "+examples.length);

    // total entropy of the class column -
    // entropy of each of the possibilities of the attribute
    // (p =#of that value, n=#rows)

    // entropy of the class col
    double gain = outputInfo(t, outCol, examples);

    // now subtract the entropy for each unique value in the column
    // ie humidity=high, count # of yes and no
    // humidity=low, count # of yes and no
    String[] vals = uniqueValues(t, attCol, examples);
    for (int i = 0; i < vals.length; i++) {
      gain -= categoricalAttributeInfo(t, attCol, outCol, vals[i], examples);

    }
    double sInfo = splitInfo(t, attCol, 0, examples);
    // divide the information gain by the split info
    gain /= sInfo;
    return gain;
  }

  /**
   Calculate the entropy of a specific value of an attribute.
   @param colNum the index of the attribute column
   @param attValue the value of the attribute we are interested in
   @param examples the list of examples, which correspond to rows of
    the table
   @return the information given by attValue
   */
  private double categoricalAttributeInfo(Table t, int colNum, int outCol,
                                          String attValue, int[] examples) {

    double tot = 0;
    int[] outtally = new int[0];
    HashMap outIndexMap = new HashMap();

    for (int i = 0; i < examples.length; i++) {
      int idx = examples[i];

      String s = t.getString(idx, colNum);

      if (s != null && s.equals(attValue)) {
        String out = t.getString(idx, outCol);

        if (outIndexMap.containsKey(out)) {
          Integer in = (Integer) outIndexMap.get(out);
          outtally[in.intValue()]++;
        }
        else {
          outIndexMap.put(out, new Integer(outIndexMap.size()));
          outtally = expandArray(outtally);
          outtally[outtally.length - 1] = 1;
        }
        tot++;
      }
    }

    return (tot / (double) examples.length) * info(outtally);
  }

  /**
   Get the unique values of the examples in a column.
   @param colNum the index of the attribute column
   @param examples the list of examples, which correspond to rows of
    the table
   @return an array containing all the unique values for examples in
    this column
   */
  private static String[] uniqueValues(Table t, int colNum, int[] examples) {
    int numRows = examples.length;

    // count the number of unique items in this column
    HashSet set = new HashSet();
    for (int i = 0; i < numRows; i++) {
      int rowIdx = examples[i];
      String s = t.getString(rowIdx, colNum);
      if (!set.contains(s)) {
        set.add(s);
      }
    }

    String[] retVal = new String[set.size()];
    int idx = 0;
    Iterator it = set.iterator();
    while (it.hasNext()) {
      retVal[idx] = (String) it.next();
      idx++;
    }
    return retVal;
  }

  /**
   Determine the split info.  This is the information given by the
   number of branches of a node.  The size of the subsets that each
   branch creates is calculated and then the information is calculated
   from that.
   @param colNum the index of the attribute column
   @param splitVal the split value for a numeric attribute
   @param examples the list of examples, which correspond to rows of
    the table
   @return the information value of the branchiness of this attribute
   */
  private double splitInfo(Table t, int colNum, double splitVal, int[] examples) {
    int numRows = examples.length;
    double[] probs;

    int[] tallies;

    // if it is a numeric column, there will be two branches.
    // count up the number of examples less than and greater
    // than the split value
    if (t.isColumnScalar(colNum)) {
      int lessThanTally = 0;
      int greaterThanTally = 0;

      for (int i = 0; i < numRows; i++) {
        int rowIdx = examples[i];
        double d = t.getDouble(rowIdx, colNum);
        if (d < splitVal) {
          lessThanTally++;
        }
        else {
          greaterThanTally++;
        }
      }
      tallies = new int[2];
      tallies[0] = lessThanTally;
      tallies[1] = greaterThanTally;
    }
    // otherwise it is nominal.  count up the number of
    // unique values, because there will be a branch for
    // each unique value
    else {
      HashMap map = new HashMap();
      /*int[]*/tallies = new int[0];
      for (int i = 0; i < numRows; i++) {
        int rowIdx = examples[i];
        String s = t.getString(rowIdx, colNum);
        if (!map.containsKey(s)) {
          map.put(s, new Integer(tallies.length));
          tallies = expandArray(tallies);
          tallies[tallies.length - 1] = 1;
        }
        else {
          Integer ii = (Integer) map.get(s);
          tallies[ii.intValue()]++;
        }
      }
    }

    // calculate the information given by the branches
    return info(tallies);
  }

  /**
   Determine the entropy of the output column.
   @param colNum the index of the attribute column
   @param examples the list of examples, which correspond to rows of
    the table
   @return the entropy of the output column
   */
  private double outputInfo(Table t, int colNum, int[] examples) {
    double numRows = (double) examples.length;

    // count the number of unique items in this column
    int[] tallies = new int[0];
    HashMap map = new HashMap();
    for (int i = 0; i < numRows; i++) {
      int rowIdx = examples[i];
      String s = t.getString(rowIdx, colNum);
      if (map.containsKey(s)) {
        Integer in = (Integer) map.get(s);
        int loc = in.intValue();
        tallies[loc]++;
      }
      else {
        map.put(s, new Integer(tallies.length));
        tallies = expandArray(tallies);
        tallies[tallies.length - 1] = 1;
      }
    }

    return info(tallies);
  }

  /**
   Return the column number of the attribute with the highest gain from
   the available columns.  If none of the attributes has a gain higher
   than the threshold, return null
   @param attributes the list of available attributes, which correspond to
    columns of the table
   @param examples the list of examples, which correspond to rows of
    the table
   @return an object containing the index of the column with the highest
    gain and (if numeric) the best split for that column
   */
  private ColSplit getHighestGainAttribute(Table t, int[] attributes,
                                           int outCol, int[] examples) {
    if (attributes.length == 0 || examples.length == 0) {
      return null;
    }
    ArrayList list = new ArrayList();

    int topCol = 0;
    double highestGain = Double.NEGATIVE_INFINITY;

    ColSplit retVal = new ColSplit();
    // for each available column, calculate the entropy
    for (int i = 0; i < attributes.length; i++) {
      int col = attributes[i];
      // nominal data
      if (t.isColumnNominal(col)) {
        double d = categoricalGain(t, col, outCol, examples);
        if (d > highestGain) {
          highestGain = d;
          retVal.col = col;
        }
      }
      // numeric column
      else {
        EntrSplit sce = numericGain(t, col, outCol, examples);
        if (sce != null && sce.gain > highestGain) {
          highestGain = sce.gain;
          retVal.col = col;
          retVal.splitValue = sce.splitValue;
        }
      }
    }

    return retVal;
  }

  /**
   A simple structure to hold a column index and the best split value of
   an attribute.
   */
  private final class ColSplit {
    int col;
    double splitValue;
  }

  // the table that contains the data set
  protected transient ExampleTable table;
  // the indices of the columns with output variables
  protected transient int[] outputs;

  protected transient int numExamples;

  public String getModuleInfo() {
/*    String s = "Build a C4.5 decision tree.  The tree is build recursively, ";
    s += "always choosing the attribute with the highest information gain ";
    s += "as the root.  The gain ratio is used, whereby the information ";
    s += "gain is divided by the information given by the size of the ";
    s += "subsets that each branch creates.  This prevents highly branching ";
    s += "attributes from always becoming the root.  A threshold can be ";
    s += "used to prevent the tree from perfect fitting the training data.  ";
    s += "If the information gain ratio is not above the threshold, a leaf ";
    s += "will be created instead of a node.  This will may cause some ";
    s += "incorrect classifications, but will keep the tree from overfitting ";
    s += "the data.  The threshold should be set low.  The default value ";
    s += "should suffice for most trees.  The threshold is a property of ";
    s += "this module.";
    return s;
 */
    String s = "<p>Overview: Builds a decision tree.  The tree is built "+
        "recursively using the information gain metric to choose the root."+
        "<p>Detailed Description: Builds a decision tree using the C4.5 "+
        "algorithm.  The decision tree is built recursively, choosing the "+
        "attribute with the highest information gain as the root.  For "+
        "a nominal input, the node will have branches for each unique value "+
        "in the nominal column.  For scalar inputs, a binary node is created "+
        "with a split point chosen that offers the greatest information gain. "+
        "The complexity of building the entire tree is O(mn log n) where m is "+
        " the number of inputs and n is the number of examples. "+
        "The choosing of split points for a scalar input is potentially an "+
        "O(n log n) operation at each node of the tree."+
        "<p>References: C4.5: Programs for Machine Learning by J. Ross Quinlan"+
        "<p>Data Type Restrictions: This module will only classify examples with "+
        "nominal outputs."+
        "<p>Data Handling: This module does not modify the input data."+
        "<p>Scalability: The selection of split points for scalar inputs is "+
        "potentially an O(n log n) operation at each node of the tree.  The "+
        "selection of split points for nominal inputs is an O(n) operation.";
        return s;
  }

  // C4.5:Programs for Machine Learning J. Ross Quinlan

  public String getModuleName() {
    return "Optimized C4.5 Tree Builder";
  }

  public String getInputInfo(int i) {
    String in = "An ExampleTable to build a decision tree from. ";
    in += "Only one output feature is used.";
    if (i == 0)
	return in;
    else
	return "Point in Parameter Space to control the minimum leaf ratio.";
  }

  public String getInputName(int i) {
      if( i == 0)
	  return "Example Table";
      else
	  return "Minimum Leaf Ratio";
  }

  public String getOutputInfo(int i) {
    if (i == 0) {
      return "The root of the decision tree built by this module.";
    }
    else {
      return " ";
    }
  }

  public String getOutputName(int i) {
    if (i == 0) {
      return "Decision Tree Root";
    }
    else {
      return "";
    }
  }

  public String[] getInputTypes() {
    String[] in = {
        "ncsa.d2k.modules.core.datatype.table.ExampleTable",
        "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint"};
    return in;
  }

  public String[] getOutputTypes() {
    String[] out = {
        "ncsa.d2k.modules.core.prediction.decisiontree.DecisionTreeNode" };

    return out;
  }

  public void endExecution() {
    super.endExecution();
    table = null;
    outputs = null;
  }

  static {
    nf = NumberFormat.getInstance();
    nf.setMaximumFractionDigits(5);
  }

  /**
   Build the decision tree
   */
  public void doit() throws Exception {
    table = (ExampleTable) pullInput(0);
    numExamples = table.getNumRows();
    ParameterPoint pp = (ParameterPoint)pullInput(1);

    if(pp == null)
      throw new Exception(getAlias()+": Parameter Point was not found!");

/*    HashMap nameToIndexMap = new HashMap();

    for(int i = 0; i < pp.getNumParameters(); i++) {
      String name = pp.getName(i);
      nameToIndexMap.put(name, new Integer(i));
    }*/

/*    int idx = (int)pp.getValue(C45ParamSpaceGenerator.MIN_RECORDS);
    if(idx == null)
      throw new Exception(getAlias()+": Minimum Number of records per leaf not defined!");
 */
    //this.minimumRecordsPerLeaf = pp.getValue(C45ParamSpaceGenerator.MIN_RATIO);
    setMinimumRatioPerLeaf(pp.getValue(C45ParamSpaceGenerator.MIN_RATIO));
    //if(minimumRecordsPerLeaf < 1)
    //  throw new Exception(getAlias()+": Must be at least one record per leaf!");

    int[] inputs = table.getInputFeatures();

    if (inputs == null || inputs.length == 0) {
      throw new Exception(getAlias()+": No inputs were defined!");
    }

    outputs = table.getOutputFeatures();

    if (outputs == null || outputs.length == 0) {
      throw new Exception("No outputs were defined!");
    }

    if (outputs.length > 1) {
      System.out.println("Only one output feature is allowed.");
      System.out.println("Building tree for only the first output variable.");
    }

    if (table.isColumnScalar(outputs[0])) {
      throw new Exception(getAlias()+" C4.5 Decision Tree can only predict nominal values.");
    }

    // the set of examples.  the indices of the example rows
    int[] exampleSet;
    // use all rows as examples at first
    exampleSet = new int[table.getNumRows()];
    for (int i = 0; i < table.getNumRows(); i++) {
      exampleSet[i] = i;

      // use all columns as attributes at first
    }
    int[] atts = new int[inputs.length];
    for (int i = 0; i < inputs.length; i++) {
      atts[i] = inputs[i];

    }
    DecisionTreeNode rootNode = buildTree(exampleSet, atts);
    pushOutput(rootNode, 0);
  }

  /**
   Build a decision tree.
   let examples(v) be those examples with A = v.
   if examples(v) is empty, make the new branch a leaf labelled with
   the most common value among examples.
   else let the new branch be the tree created by
   buildTree(examples(v), target, attributes-{A})
   @param examples the indices of the rows to use
   @param attributes the indices of the columns to use
   @return a tree
   */
  protected DecisionTreeNode buildTree(int[] examples, int[] attributes) throws
      MinimumRecordsPerLeafException {

    //debug("BuildTree with "+examples.length+" examples and "+attributes.length+" attributes.");

    if (this.isAborting()) {
      return null;
    }

    if ( (((double)examples.length)/(double)numExamples) < getMinimumRatioPerLeaf()) {
      throw new MinimumRecordsPerLeafException();
    }

    DecisionTreeNode root = null;
    String s;

    // if all examples have the same output value, give the root this
    // label-- this node is a leaf.
    boolean allSame = true;
    int counter = 0;
    s = table.getString(examples[counter], outputs[0]);
    counter++;
    while (allSame && counter < examples.length) {
      String t = table.getString(examples[counter], outputs[0]);
      if (!t.equals(s)) {
        allSame = false;
      }
      counter++;
    }
    // create the leaf
    if (allSame) {
      if (debug) {
        System.out.println("***The values were all the same: " + s);
      }
      root = new CategoricalDecisionTreeNode(s);
      return root;
    }

    // if attributes is empty, label the root according to the most common
    // value this will result in some incorrect classifications...
    // this node is a leaf.
    if (attributes.length == 0) {
      String mostCommon = mostCommonOutputValue(table, outputs[0], examples);
      // make a leaf
      if (debug) {
        System.out.println(
            "***Attributes empty.  Creating new Leaf with most common output value: " +
            mostCommon);
      }
      root = new CategoricalDecisionTreeNode(mostCommon);
      return root;
    }

    // otherwise build the subtree rooted at this node

    // calculate the information gain for each attribute
    // select the attribute, A, with the lowest average entropy, make
    // this be the one tested at the root
    ColSplit best = getHighestGainAttribute(table, attributes, outputs[0],
                                            examples);

    // if there was a column
    if (best != null) {
      int col = best.col;

      // categorical data
      if (!table.isColumnScalar(col)) {
        // for each possible value v of this attribute in the set
        // of examples add a new branch below the root,
        // corresponding to A = v
        try {
          String[] branchVals = uniqueValues(table, col, examples);
          root = new CategoricalDecisionTreeNode(table.getColumnLabel(col));
          for (int i = 0; i < branchVals.length; i++) {
            int[] branchExam = narrowCategoricalExamples(col,
                branchVals[i], examples);
            int[] branchAttr = narrowAttributes(col, attributes);
            //if (branchExam.length >= getMinimumRecordsPerLeaf() &&
            if ( (((double)branchExam.length)/(double)numExamples) > getMinimumRatioPerLeaf() &&
                branchAttr.length != 0) {
              root.addBranch(branchVals[i], buildTree(branchExam,
                  branchAttr));
            }

            // if examples(v) is empty, make the new branch a leaf
            // labelled with the most common value among examples
            else {
              String val = mostCommonOutputValue(table, outputs[0], examples);
              DecisionTreeNode nde = new CategoricalDecisionTreeNode(val);
              root.addBranch(val, nde);
            }
          }
        }
        catch (MinimumRecordsPerLeafException e) {
//          e.printStackTrace();
          String val = mostCommonOutputValue(table, outputs[0], examples);
          DecisionTreeNode nde = new CategoricalDecisionTreeNode(val);
          root.addBranch(val, nde);
        }
        catch (Exception e) {
//          e.printStackTrace();
          String val = mostCommonOutputValue(table, outputs[0], examples);
          DecisionTreeNode nde = new CategoricalDecisionTreeNode(val);
          root.addBranch(val, nde);

        }
      }

      // else if numeric find the binary split point and create two branches
      else {
        try {
          DecisionTreeNode left;
          DecisionTreeNode right;
          root = new NumericDecisionTreeNode(table.getColumnLabel(col));

          // create the less than branch
          int[] branchExam = narrowNumericExamples(col,
              best.splitValue, examples, false);
          //if(branchExam.length >= minimumRecordsPerLeaf) {
          left = buildTree(branchExam, attributes);
          //}

          // else if examples(v) is empty, make the new branch a leaf
          // labelled with the most common value among examples
          /*else {
           if(debug)
            System.out.println("Making a new Left Branch for a numeric with the most common output.");
           String val = mostCommonOutputValue(table, outputs[0], examples);
           left = new CategoricalDecisionTreeNode(val);
               }*/

          // create the greater than branch
          branchExam = narrowNumericExamples(col, best.splitValue,
                                             examples, true);
          //if(branchExam.length >= minimumRecordsPerLeaf) {
          right = buildTree(branchExam, attributes);
          //}

          // else if examples(v) is empty, make the new branch a leaf
          // labelled with the most common value among examples
          /*else {
           if(debug)
            System.out.println("Making a new Right branch for a numeric with the most common output.");
           String val = mostCommonOutputValue(table, outputs[0], examples);
           right = new CategoricalDecisionTreeNode(val);
               }*/

          // add the branches to the root
          StringBuffer lesser = new StringBuffer(table.getColumnLabel(col));
          lesser.append(LESS_THAN);
          lesser.append(nf.format(best.splitValue));

          StringBuffer greater = new StringBuffer(table.getColumnLabel(col));
          greater.append(GREATER_THAN_EQUAL_TO);
          greater.append(nf.format(best.splitValue));
          root.addBranches(best.splitValue, lesser.toString(), left,
                           greater.toString(), right);
        }
        catch (MinimumRecordsPerLeafException e) {
          String val = mostCommonOutputValue(table, outputs[0], examples);
          return new CategoricalDecisionTreeNode(val);
        }
      }
    }

    // otherwise we could not find a suitable column.  create
    // a new node with the most common output
    else {
      String val = mostCommonOutputValue(table, outputs[0], examples);
      root = new CategoricalDecisionTreeNode(val);
      if (debug) {
        System.out.println("creating new CategoricalDTN: " + val);
      }
    }

    return root;
  }

  private class MinimumRecordsPerLeafException
      extends Exception {}

  /**
   Find the most common output value from a list of examples.
   @param examples the list of examples
   @return the most common output value from the examples
   */
  private static String mostCommonOutputValue(Table t, int outCol,
                                              int[] examples) {
    HashMap map = new HashMap();
    int[] tallies = new int[0];
    for (int i = 0; i < examples.length; i++) {
      String s = t.getString(examples[i], outCol);
      if (map.containsKey(s)) {
        Integer loc = (Integer) map.get(s);
        tallies[loc.intValue()]++;
      }
      else {
        map.put(s, new Integer(map.size()));
        tallies = expandArray(tallies);
        tallies[tallies.length - 1] = 1;
      }
    }

    int highestTal = 0;
    String mostCommon = null;

    Iterator i = map.keySet().iterator();
    while (i.hasNext()) {
      String s = (String) i.next();
      Integer loc = (Integer) map.get(s);
      if (tallies[loc.intValue()] > highestTal) {
        highestTal = tallies[loc.intValue()];
        mostCommon = s;
      }
    }
    return mostCommon;
  }

  /**
   Create a subset of the examples.  Only those examples where the value
   is equal to val will be in the subset.
   @param col the column to test
   @param the value to test
   @param exam the list of examples to narrow
   @return a subset of the original list of examples
   */
  private int[] narrowCategoricalExamples(int col, String val, int[] exam) {
    int numNewExamples = 0;
    boolean[] map = new boolean[exam.length];

    for (int i = 0; i < exam.length; i++) {
      String s = table.getString(exam[i], col);
      if (s.equals(val)) {
        numNewExamples++;
        map[i] = true;
      }
      else {
        map[i] = false;
      }
    }
    int[] examples = new int[numNewExamples];
    int curIdx = 0;
    for (int i = 0; i < exam.length; i++) {
      if (map[i]) {
        examples[curIdx] = exam[i];
        curIdx++;
      }
    }
    return examples;
  }

  /**
   Create a subset of the examples.  If greaterThan is true, only those
   rows where the value is greater than than the splitValue will be in
   the subset.  Otherwise only the rows where the value is less than the
   splitValue will be in the subset.
   @param col the column to test
   @param splitValue the value to test
   @param exam the list of examples to narrow
   @param greaterThan true if values greater than the split value should
    be in the new list of examples, false if values less than the split
    value should be in the list of examples
   @return a subset of the original list of examples
   */
  private int[] narrowNumericExamples(int col, double splitValue, int[] exam,
                                      boolean greaterThan) {

    int numNewExamples = 0;
    boolean[] map = new boolean[exam.length];

    for (int i = 0; i < exam.length; i++) {
      double d = table.getDouble(exam[i], col);
      if (greaterThan) {
        if (d >= splitValue) {
          numNewExamples++;
          map[i] = true;
        }
        else {
          map[i] = false;
        }
      }
      else {
        if (d < splitValue) {
          numNewExamples++;
          map[i] = true;
        }
        else {
          map[i] = false;
        }
      }
    }

    int[] examples = new int[numNewExamples];
    int curIdx = 0;
    for (int i = 0; i < exam.length; i++) {
      if (map[i]) {
        examples[curIdx] = exam[i];
        curIdx++;
      }
    }

    return examples;
  }

  /**
   Remove the specified column from list of attributes.
   @param col the column to remove
   @param attr the list of attributes
   @return a subset of the original list of attributes
   */
  private int[] narrowAttributes(int col, int[] attr) {
    int[] retVal = new int[attr.length - 1];
    int curIdx = 0;
    for (int i = 0; i < attr.length; i++) {
      if (attr[i] != col) {
        retVal[curIdx] = attr[i];
        curIdx++;
      }
    }
    return retVal;
  }
}


/**
 * basic 4 qa comments.
 *
 * 02-02-04: vered
 * changed the name of this module to differ from C45TreeBuilder's.
 * changed name and description of second input to be more specific.
 * as this input is not just any parameter point, but one that is used to
 * control the min leaf ratio.
 */