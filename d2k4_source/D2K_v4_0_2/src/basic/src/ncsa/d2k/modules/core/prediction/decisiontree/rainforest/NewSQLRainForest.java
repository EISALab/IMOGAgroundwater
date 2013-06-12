package ncsa.d2k.modules.core.prediction.decisiontree.rainforest;



/**

 * <p>Title: NewSQLRainForest </p>

 * <p>Description: Build a decision tree from a database table.

 *    If the data set can be fit into the memory, the whole data set

 *    will be retrieved and reside in memory, otherwise, the data set

 *    will be partitioned at each tree node </p>

 * <p>This implementation is based on RainForest and C4.5 algorithm with following modifications. </p>

 * <p> 1. a special class AvcSet (Attribute-Value-Class Set) keeps the

 *    summarized information, which reduces the number of data scans and

 *    the requirement of memory. </P>

 * <p> 2. gain ratio (gain/split-info) is used in categorical columns,

 *    but not numeric columns</p>

 * <p> 3. minimum ratio (number_record_in_leaf / total_number_records) is

 *    used instead of minimumRecordPerLeaf</p>

 * <p> 4. missing values are skipped in each column computation,

 *    but the records are not removed from the data set. </p>

 * <p> 5. dominate ratio (number_mostCommonClass / number_secondMostCommonClass)

 *    is used to prune trivial nodes </p>

 * <p> 6. Numeric columns are allowed to use more than once, but if the dominateRatio

 *    between parent node and child node has no improvement, the spliting will

 *    be terminated and a leaf will be formed </p>

 * <p>Copyright: Copyright (c) 2002</p>

 * <p>Company: NCSA ALG </p>

 * @author Dora Cai

 * @version 1.0

 */

import ncsa.d2k.core.modules.*;

import ncsa.d2k.modules.*;

import ncsa.d2k.modules.core.datatype.table.*;

import ncsa.d2k.modules.core.datatype.table.basic.*;

import ncsa.d2k.modules.core.io.sql.*;

import java.sql.*;

import java.util.*;

import java.text.*;

import javax.swing.*;

import java.beans.PropertyVetoException;

import gnu.trove.*;





public class NewSQLRainForest extends SQLRainForestOPT {

  JOptionPane msgBoard = new JOptionPane();



  // the number of split values for numeric attribute

  double binNumber = 100;

  // minimum records ratio (% of totalRow) for leaf node

  double minimumRatioPerLeaf = 0.001;

  // minimum records for leaf node: minimumRatioPerLeaf * totalRow

  double minimumRecordsPerLeaf = 0.00;

  // dominateRatio = (% of MostCommonClass) / (% of SecondMostCommonClass)

  // if dominateRatio > specified Ratio, the node should not be split further,

  // and should be created as a leaf.

  double dominateRatio = 100.00;

  // the threshold for choosing in-memory or in-database mode

  // If the totalRow < modeThreshold, the entire data set is retrieved and load in memory.

  // Otherwise, the data set is partitioned at each tree node

  double modeThreshold = 200000;

  // the ration calculated by: abs(parent node's dominate ratio - child node's dominate ratio)

  // When below this ratio, the child node is pruned.

  double improvementRatio = dominateRatio * 0.05;



  ConnectionWrapper cw;

  Connection con;

  // class label column

  String classColName;

  // where clause of the query. It may include join condition and filters

  String whereClause;

  // object stored in ArrayList avcSets

  public class AvcSet {

    public String attrName;

    public String lowValue; // low end attribute value

    public String highValue; // high end attribute value

    public String classLabel; // class label. We only implete single class column

    public int count; // count for this avcSet

  }

  public class NodeInfo {

    public ArrayList[] avcSets;

    public int[] classTallies;

    public MutableTableImpl data;

    public ArrayList[] uniqValues;

    public NodeInfo () {

    }

    public NodeInfo(ArrayList[] avcSets, int[] classTallies, MutableTableImpl data, ArrayList[] uniqValues) {

      this.avcSets = avcSets;

      this.classTallies = classTallies;

      this.data = data;

      this.uniqValues = uniqValues;

    }

  }

  int totalRow;

  static String NOTHING = "";

  static String DELIMITER = "\t";

  static String ELN = "\n";



  private int [] inputFeatures;

  private int [] outputFeatures;

  private String[] classValues;



  // the column index for output column

  private int outputCol;

  // table for meta data

  private ExampleTable meta;

  private String dbTable;



  /**

    A simple structure to hold a column index and the best split value of

    an attribute.

  */

  private final class ColSplit {

    int col;

    double splitValue;

  }



  /**

    A simple structure to hold the gain and split value of a numeric column.

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



  private static NumberFormat nf;

  static {

    nf = NumberFormat.getInstance();

    nf.setMaximumFractionDigits(2);

  }



  public NewSQLRainForest() {

  }



  public String getOutputInfo(int i) {

    switch (i) {

      case 0: return "The generated decision tree model.";

      default: return "No such output";

    }

  }



  public String getInputInfo (int i) {

      switch (i) {

        case 0: return "JDBC data source to make database connection.";

        case 1: return "The name of the data table.";

        case 2: return "The table that contains meta information.";

        case 3: return "The query condition for filtering data.";

        default: return "No such input";

      }

  }





  public String getInputName(int i) {

    switch (i) {

      case 0: return "Database Connection";

      case 1: return "Selected Table";

      case 2: return "Meta Table";

      case 3: return "Query Condition";

      default: return "NoInput";

    }

  }



  public String getModuleInfo () {

    String s = "<p>Overview: ";

    s += "This module builds a decision tree from a database table. </p>";

    s += "<p>Detailed Description: ";

    s += "The module supports two different modes to build a decision tree. If the ";

    s += "data set can be fit into the memory, the whole data set ";

    s += "will be retrieved and stored in memory, otherwise, the data set ";

    s += "will be partitioned at each tree node. This module automatically ";

    s += "chooses the execution mode based on the size of the data set ";

    s += "and the parameter 'Mode Threshold'. The tree is built ";

    s += "recursively using the information gain metric to choose the ";

    s += "attribute with the highest information gain as the root.  For ";

    s += "a nominal input, the node will have branches for each unique value ";

    s += "in the nominal column.  For scalar inputs, a binary node is created ";

    s += "with a split point chosen that offers the greatest information gain. ";

    s += "This module is based on RainForest and C4.5 algorithms with the following features. </p>";

    s += "<ul><li>a special class AvcSet (Attribute-Value-Class Set) keeps the ";

    s += "summarized information, which reduces the number of data scans and ";

    s += "the memory requirements. </li>";

    s += "<li>gain ratio (gain/split-info) is used in categorical columns, ";

    s += "but not numeric columns. </li>";

    s += "<li>minimum-ratio-per-leaf (number-record-in-leaf / total-number-records) is ";

    s += "used, instead of minimum-number-per-leaf. </li>";

    s += "<li>missing values are skipped in computation of each column, ";

    s += "but the records are not removed from the data set. </li> ";

    s += "<li>dominate ratio (number_mostCommonClass / number_secondMostCommonClass) ";

    s += "is used to prune trivial nodes. </li> ";

    s += "<li>Numeric columns are allowed to be chosen more than once. However, if the dominateRatio ";

    s += "between parent node and child node shows no improvement, the spliting will ";

    s += "be terminated and a leaf will be formed. </li></ul></p>";

    s += "<p>References: ";

    s += "<ul><li>C4.5: Programs for Machine Learning by J. Ross Quinlan </li>";

    s += "<li>RainForest: A Frame Work for Fast Decision Tree Construction ";

    s += "of Large Datasets by J. Gehrke et al. </li></ul></p>";

    s += "<p>Restrictions: ";

    s += "This module will only classify examples with ";

    s += "nominal outputs. We currently only support Oracle, SQLServer, DB2 and MySql databases. ";

    s += "There are two implementations on RainForest. <i>NewSQLRainForest</i> does ";

    s += "not do parameter optimization, but <i>NewSQLRainForestOPT<i> does.</p>";

    s += "<p>Data Handling: This module does not modify the input data. </p>";

    s += "<p>Scalability: This module can handle a huge data set even the ";

    s += "size of the data set is larger than the available memory. </p> ";

    return s;

  }



  public String[] getInputTypes () {

    String[] types = {"ncsa.d2k.modules.core.io.sql.ConnectionWrapper",

                      "java.lang.String",

                      "ncsa.d2k.modules.core.datatype.table.ExampleTable",

                      "java.lang.String"};

      return types;

  }



  public String[] getOutputTypes () {

                String[] types = {"ncsa.d2k.modules.core.prediction.decisiontree.rainforest.DecisionForestModel"};

                return types;

        }



  public String getOutputName(int i) {

    return "Decision Tree Model";

  }



  public String getModuleName() {

    return "SQL Rain Forest";

  }



  public void setBinNumber (double i) throws PropertyVetoException {

      if (i <  0 )

          throw new PropertyVetoException(" < 0", null);

      binNumber = i;

  }



  public double getBinNumber () {

    return binNumber;

  }



  public void setMinimumRatioPerLeaf(double num) throws PropertyVetoException {

      if(num < 0 || num >1)

          throw new PropertyVetoException(" < 0 or > 1", null);

    minimumRatioPerLeaf = num;

  }



  public double getMinimumRatioPerLeaf() {

    return minimumRatioPerLeaf;

  }



  public void setDominateRatio(double num) throws PropertyVetoException {

      if (num < 0)

          throw new PropertyVetoException(" < 0", null);

    dominateRatio = num;

    improvementRatio = dominateRatio * 0.05;

  }



  public double getDominateRatio() {

    return dominateRatio;

  }



  public void setModeThreshold(double num) throws PropertyVetoException {

      if( num < 0)

          throw new PropertyVetoException(" < 0", null);

    modeThreshold = num;

  }



  public double getModeThreshold() {

    return modeThreshold;

  }



  protected String[] getFieldNameMapping () {

    return null;

  }



    public static final String MIN_RATIO = "Minimum Leaf Size Ratio";

    public static final String MODE_THRESHOLD = "Mode Threshold";

    public static final String BIN_NUMBER = "Bin Number";

    public static final String DOMINATE_RATIO = "Dominate Class Ratio";



  public PropertyDescription [] getPropertiesDescriptions () {

    PropertyDescription [] pds = new PropertyDescription [4];

    pds[0] = new PropertyDescription ("minimumRatioPerLeaf", MIN_RATIO, "The minimum ratio of records in a leaf to the total number of records in the tree. The tree construction is terminated when this ratio is reached.");

    pds[1] = new PropertyDescription ("modeThreshold", MODE_THRESHOLD, "If the number of examples is greater than this threshold, the in-database mode is used. Otherwise, the in-memory mode is used.");

    pds[2] = new PropertyDescription ("binNumber", BIN_NUMBER, "If the number of distinct values in a numeric attribute is greater than Bin Number, data is grouped into this number of bins.");

    pds[3] = new PropertyDescription ("dominateRatio", DOMINATE_RATIO, "Ratio of most-common class to second-most-common class. The tree construction is terminated when this ratio is reached.");

    return pds;

  }



  /** build decision tree

   *  @throws Exception

   */

  public void doit() throws Exception {
    // Array of ArrayLists to keep distinct values

    ArrayList[] uniqValues;

    // table for row data (this is only used in in-memory mode)

    cw = (ConnectionWrapper)pullInput(0);

    dbTable = (String)pullInput(1);

    meta = (ExampleTable)pullInput(2);

    whereClause = (String)pullInput(3);

    inputFeatures = new int[meta.getInputFeatures().length];

    inputFeatures = meta.getInputFeatures();

    outputFeatures = new int[meta.getOutputFeatures().length];

    outputFeatures = meta.getOutputFeatures();

    if(meta.getOutputFeatures().length == 0) { // no class label is defined

      JOptionPane.showMessageDialog(msgBoard,

                                 getAlias() + ": You must choose a column as the output column (class label).", "Error",

                                 JOptionPane.ERROR_MESSAGE);

      System.out.println(getAlias() + ": You must choose a column as the output column.");

    }


    else if(meta.isColumnScalar(outputFeatures[0])) {

         JOptionPane.showMessageDialog(msgBoard,

                                    getAlias() + ": You cannot choose a numeric column as the output column", "Error",

                                    JOptionPane.ERROR_MESSAGE);

      System.out.println(getAlias() + ": You cannot choose a numeric column as the output column.");

    }



    else {

      outputCol = outputFeatures[0];

      classColName = meta.getColumnLabel(outputCol);

      ArrayList splitValues = new ArrayList();

      String[] availableCols = new String[inputFeatures.length];

      for (int colIdx = 0; colIdx < inputFeatures.length; colIdx++) {

        availableCols[colIdx] = meta.getColumnLabel(inputFeatures[colIdx]);

      }

      // totalRow = meta.getNumEntries();

      totalRow = meta.getNumRows();

      minimumRecordsPerLeaf = totalRow * minimumRatioPerLeaf;



      // if totalRow < modeThreshold, use in-memory mode

      if (totalRow > 0 && totalRow < modeThreshold) {

        NodeInfo aNodeInfo = createDataTable(dbTable, meta, whereClause, splitValues,

                                             totalRow);

        int[] exampleSet;

        // use all rows as examples at first

        exampleSet = new int[aNodeInfo.data.getNumRows()];

        for (int rowIdx = 0; rowIdx < aNodeInfo.data.getNumRows(); rowIdx++) {

          exampleSet[rowIdx] = rowIdx;

        }

        DecisionForestNode rootNode = buildTree(splitValues, availableCols,

                                                aNodeInfo, exampleSet);

        DecisionForestModel mdl = new DecisionForestModel(rootNode, meta,

            totalRow, classValues);

        //PredictionModelModule pmm=(PredictionModelModule) mdl;

        //pushOutput(pmm, 0);

        pushOutput(mdl, 0);

      }

      else if (totalRow >= modeThreshold) { // use in-db mode

        classValues = getClassValues(classColName);

        uniqValues = getUniqValues(dbTable, splitValues, availableCols);

        NodeInfo aNodeInfo = extractDataFromDB(dbTable, classColName,

                                               splitValues, availableCols,

                                               uniqValues);

        DecisionForestNode rootNode = buildTree(splitValues, availableCols,

                                                aNodeInfo, null);

        DecisionForestModel mdl = new DecisionForestModel(rootNode, meta,

            totalRow, classValues);

        PredictionModelModule pmm = (PredictionModelModule) mdl;

        pushOutput(pmm, 0);

      }

      else {

        JOptionPane.showMessageDialog(msgBoard,

                                      "No data", "Error",

                                      JOptionPane.ERROR_MESSAGE);

        System.out.println("Error occurred in doit, no data.");

      }

    }

  }



  /** get class values from a database table

   * @param classColName the column name in the database table

   * @return String[] that holds the distinct class values

   */

  private String[] getClassValues(String classColName) {

    ArrayList classAL = new ArrayList();

    try {

      con = cw.getConnection();

      String classQry;

      if (whereClause != null && whereClause.length() > 0) {

        classQry = new String("select distinct " + classColName + " from " + dbTable +

                          " where (" + whereClause + ") and " + classColName + " is not null");

      }

      else {

        classQry = new String("select distinct " + classColName + " from " +

                              dbTable + " where " + classColName + " is not null");
      }

      //System.out.println("in getClassValues, classQry is " + classQry);

      Statement classStmt = con.createStatement ();

      ResultSet classSet = classStmt.executeQuery(classQry);

      while (classSet.next()) {

        classAL.add(classSet.getString(1));

      }

      classStmt.close();

      return toStringArray(classAL);

    }

    catch (Exception e){

      JOptionPane.showMessageDialog(msgBoard,

                e.getMessage(), "Error",

                JOptionPane.ERROR_MESSAGE);

      System.out.println("Error occurred in getClassValues.");

      return null;

    }

  }



  /** get class values from an ArrayList uniqValues

   * @param uniqValues the ArrayList that stores unique values for all columns

   * @return String[] that holds the distinct class values

   */

  private String[] getClassValues(ArrayList[] uniqValues) {

    ArrayList classAL = (ArrayList)uniqValues[outputFeatures[0]];

    return toStringArray(classAL);

  }



  // get the number of rows in the data set by adding up the tallies

  private int getTotalTallies (int[] tallies) {

    int totTallies = 0;

    for (int idx=0; idx<tallies.length; idx++) {

      totTallies += tallies[idx];

    }

    return totTallies;

  }



  /** take out a column from the attribute list

   *  @param aColumn the column to remove

   *  @param oldColumns the column list to remove from

   *  @return a column list after removing a column

   */

  private String[] narrowAttributes(String aColumn, String[] oldColumns) {

    String[] newColumns = new String[oldColumns.length-1];

    if (oldColumns.length >= 1) {

      int newIdx = 0;

      for (int i = 0; i < oldColumns.length; i++) {

        if (!oldColumns[i].equals(aColumn)) {

          newColumns[newIdx] = oldColumns[i];

          newIdx++;

        }

      }

    }

    return newColumns;

  }



  /** Extract data from a database table

   *  @param dbTable the database table to read

   *  @param classColName the name of the class column

   *  @param path the branch labels from root to the current node

   *  @param availCols the available columns to choose from

   *  @param uniqValues unique values for the current data set

   *  @return an object of NodeInfo that includes: avcSets, data, classTallies, and uniqValues

   */

  private NodeInfo extractDataFromDB(String dbTable, String classColName, ArrayList path,

                                     String[] availCols, ArrayList[] uniqValues) {

    // ArrayList for avcSet

    ArrayList[] avcSets = new ArrayList[meta.getNumColumns()];

    for (int i = 0; i < meta.getNumColumns(); i++) {

      avcSets[i] = new ArrayList();

    }

    // Tally for each possible class label

    int[] classTallies = new int[classValues.length];

    if (availCols.length == 0) {

      NodeInfo aNodeInfo = new NodeInfo(avcSets, classTallies, null, uniqValues);

      return(aNodeInfo);

    }

    try {

      con = cw.getConnection();

      // initiate the tally array

      for (int i = 0; i<classTallies.length; i++) {

        classTallies[i] = 0;

      }

      String dataQry = new String("select " + classColName + ", ");

      for (int colIdx = 0; colIdx < availCols.length; colIdx++) {

        if (colIdx > 0) {

          // add comma between columns

          dataQry = dataQry + ", ";

        }

        dataQry = dataQry + availCols[colIdx];

      }

      if (whereClause != null && whereClause.length() > 0) {

        dataQry = dataQry + " from " + dbTable + " where (" + whereClause + ") and " +

                  classColName + " is not null";
      }

      else {

        dataQry = dataQry + " from " + dbTable + " where " + classColName +

            " is not null";

      }

      if (path.size() > 0) {

        for (int pathIdx = 0; pathIdx < path.size(); pathIdx++) {

          dataQry = dataQry + " and " + path.get(pathIdx);

        }

      }

      Statement dataStmt = con.createStatement ();

      ResultSet dataSet = dataStmt.executeQuery(dataQry);

      while (dataSet.next()) {

        // first column retrieved is the classLabel

        String classLabel = dataSet.getString(1);

        for (int valueIdx = 0; valueIdx < classValues.length; valueIdx++) {

          if (classLabel.equals(classValues[valueIdx])) {

            classTallies[valueIdx]++;

            break;

          }

        }

        for (int colIdx = 0; colIdx < availCols.length; colIdx++) {

          // if it is not a scalar column

          if (!isScalar(availCols[colIdx])) {

            // dataSet starts from index 1 and the first column is classLabel, therefore need colIdx+2

            String attrValue = dataSet.getString(colIdx+2);

            if (attrValue==null || attrValue.equals(" ") || attrValue.equals("")) {

              continue;

            }

            else {

              String attrName = availCols[colIdx];

              avcSets = updOneAvc(avcSets, attrName, attrValue, classLabel);

            }

          }

          // if it is a scalar column

          else if (isScalar(availCols[colIdx])) {

            double attrValue = dataSet.getDouble(colIdx+2);

            if (attrValue<Double.MIN_VALUE || attrValue>Double.MAX_VALUE) {

              continue;

            }

            else {

              String attrName = availCols[colIdx];

              avcSets = updAvcSets(avcSets, attrName, attrValue, classLabel, uniqValues);

            }

          }

        }

      }

      NodeInfo aNodeInfo = new NodeInfo(avcSets, classTallies, null, uniqValues);

      dataStmt.close();

      return(aNodeInfo);

    } // end of try

    catch (Exception e){

      JOptionPane.showMessageDialog(msgBoard,

                e.getMessage(), "Error",

                JOptionPane.ERROR_MESSAGE);

      System.out.println("Error occurred in extractDataFromDB.");

      return null;

    }

  };



  /** partition the current data set

   *  @param data the current data set

   *  @param examples the data indexes to be partitioned

   *  @param availCols the available columns to choose from

   *  @param uniqValues the unique values in the data set

   *  @return an object of NodeInfo that includes: avcSets, data, classTallies, and uniqValues

   */

  private NodeInfo partitionDataSet(MutableTableImpl data, int[] examples,

                                    String[] availCols, ArrayList[] uniqValues) {

    // ArrayList for avcSet

    ArrayList[] avcSets = new ArrayList[meta.getNumColumns()];

    for (int colIdx=0; colIdx<meta.getNumColumns(); colIdx++) {

      avcSets[colIdx] = new ArrayList();

    }

    String classLabel = NOTHING;

    // Tally for each possible class label

    int[] classTallies = new int[classValues.length];

    if (availCols.length == 0) {

      NodeInfo aNodeInfo = new NodeInfo(avcSets, classTallies, data, uniqValues);

      return(aNodeInfo);

    }

    // initiate the tally array

    for (int i = 0; i<classTallies.length; i++) {

      classTallies[i] = 0;

    }

    // build avcSets

    for (int rowIdx=0; rowIdx<examples.length; rowIdx++) {

      // first find the classLabel

      for (int colIdx=0; colIdx<data.getNumColumns(); colIdx++) {

        if (colIdx == outputFeatures[0]) {

          for (int valueIdx=0; valueIdx<classValues.length; valueIdx++) {

            String tmpString = data.getString(examples[rowIdx],colIdx);

            //System.out.println("in partition, tmpString is " + tmpString + ", classValues is " + classValues[valueIdx]);

            if (tmpString.equals(classValues[valueIdx])) {

              classLabel = tmpString;

              //System.out.println("in partition, classLabel set to " + classLabel);

              classTallies[valueIdx]++;

              break;

            }

          }

        }

      }

      // create an avcSet for each column value

      for (int colIdx=0; colIdx<data.getNumColumns(); colIdx++) {

        // if it is not a scalar column

        if (colIdx != outputFeatures[0] &&

            !data.isColumnScalar(colIdx) &&

            inList(data.getColumnLabel(colIdx),availCols)) {

          String attrValue = data.getString(examples[rowIdx],colIdx);

          if (attrValue==NOTHING) {

            continue;

          }

          else {

            String attrName = data.getColumnLabel(colIdx);

            avcSets = updOneAvc(avcSets, attrName, attrValue, classLabel);

          }

        }

        // if it is a scalar column

        else if (colIdx != outputFeatures[0] &&

                 data.isColumnScalar(colIdx) &&

                 inList(data.getColumnLabel(colIdx),availCols)) {

          double attrValue = data.getDouble(examples[rowIdx],colIdx);

          if (attrValue<=Double.MIN_VALUE) {

            continue;

          }

          else {

            String attrName = data.getColumnLabel(colIdx);

            avcSets = updAvcSets(avcSets, attrName, attrValue, classLabel, uniqValues);

          }

        }

      }

    }



    NodeInfo aNodeInfo = new NodeInfo(avcSets, classTallies, data, uniqValues);

    return(aNodeInfo);

  }



  /** verify whether the column is a numeric column

   *  @param aColName the column to verify

   *  @return true if the column is a numeric column, false otherwise

   */

  private boolean isScalar(String aColName) {

    Column column;

    for (int colIdx=0; colIdx < meta.getNumColumns(); colIdx++) {

      if (meta.getColumnLabel(colIdx).equals(aColName)) {

        if (meta.isColumnScalar(colIdx)) {

          return true;

        }

        else

          return false;

      }

    }

    return false;

  }



  /** update AVC sets (method for numeric values)

   *  @param avcSets the AVC sets to update

   *  @param attrName the column name to use

   *  @param attrValue the column value to use

   *  @param classLabel the classLabel to use

   *  @param uniqValues the unique values in the data set. This will be the split

   *         values for numeric data.

   *  @return updated new AVC sets

   */

  private ArrayList[] updAvcSets(ArrayList[] avcSets, String attrName, double attrValue,

                               String classLabel, ArrayList[] uniqValues) {

    double prevSplit = Double.MIN_VALUE;

    double currSplit = Double.MIN_VALUE;

    String newLowValue;

    String newHighValue;

    String newAttrValue = " ";

    boolean done = false;

    int colIdx = getColIdx(attrName);

    ArrayList uniqValue = (ArrayList)uniqValues[colIdx];

    prevSplit = Double.MIN_VALUE;

    currSplit = Double.MIN_VALUE;

    for (int rowIdx=0; rowIdx<uniqValue.size(); rowIdx++) {

      currSplit = Double.parseDouble(uniqValue.get(rowIdx).toString());

      // attrValue is less than the current split value

      if (attrValue < currSplit && attrValue >= prevSplit) {

        newLowValue = Double.toString(prevSplit);

        newHighValue = Double.toString(currSplit);

        avcSets = updOneAvc(avcSets, attrName, newLowValue, newHighValue, classLabel);

        done = true;

        break;

      }

      else {

            prevSplit = currSplit;

      }

    }

    if (!done && attrValue >= prevSplit && attrValue < Double.MAX_VALUE) {

      newLowValue = Double.toString(prevSplit);

      newHighValue = Double.toString(Double.MAX_VALUE);

      avcSets = updOneAvc(avcSets, attrName, newLowValue, newHighValue, classLabel);

    }

    return avcSets;

  }



  /** update AVC sets (method for non-numeric values)

   *  @param avcSets the AVC sets to update

   *  @param attrName the column name to use

   *  @param newAttrValue the column value to use

   *  @param classLabel the classLabel to use

   *  @return updated new AVC sets

   */

  private ArrayList[] updOneAvc(ArrayList[] avcSets, String attrName, String newAttrValue, String classLabel) {

    int col = getColIdx(attrName);

    boolean found = false;

    if (avcSets[col].size()==0) {

      AvcSet aSet = new AvcSet();

      aSet.attrName = attrName;

      aSet.lowValue = newAttrValue;

      aSet.highValue = null;

      aSet.classLabel = classLabel;

      aSet.count = 1;

      avcSets[col].add(aSet);

    }

    else {

      for (int setIdx = 0; setIdx < avcSets[col].size(); setIdx++) {

        AvcSet aSet = (AvcSet)avcSets[col].get(setIdx);

        if (aSet.attrName.equals(attrName) &&

            aSet.lowValue.equals(newAttrValue) &&

            aSet.classLabel.equals(classLabel)) {

          aSet.count = aSet.count + 1;

          found = true;

          break;

        }

      }

      if (!found) {

        AvcSet aSet = new AvcSet();

        aSet.attrName = attrName;

        aSet.lowValue = newAttrValue;

        aSet.highValue = null;

        aSet.classLabel = classLabel;

        aSet.count = 1;

        avcSets[col].add(aSet);

      }

    }

    return (avcSets);

  }



  /** update single AVC set (method for numeric values)

   *  @param avcSets the AVC sets to update

   *  @param attrName the column name to use

   *  @param lowValue the lower bound for the spliting range

   *  @param highValue the higher bound for the spliting range

   *  @param classLabel the classLabel to use

   *  @return updated new AVC sets

   */

  private ArrayList[] updOneAvc(ArrayList[] avcSets, String attrName, String lowValue,

                              String highValue, String classLabel) {

    int col = getColIdx(attrName);

    boolean found = false;

    if (avcSets[col].size()==0) {

      AvcSet aSet = new AvcSet();

      aSet.attrName = attrName;

      aSet.lowValue = lowValue;

      aSet.highValue = highValue;

      aSet.classLabel = classLabel;

      aSet.count = 1;

      avcSets[col].add(aSet);

    }

    else {

      for (int setIdx = 0; setIdx < avcSets[col].size(); setIdx ++) {

        AvcSet aSet = (AvcSet)avcSets[col].get(setIdx);

        if (aSet.attrName.equals(attrName) &&

            aSet.lowValue.equals(lowValue) &&

            aSet.highValue.equals(highValue) &&

            aSet.classLabel.equals(classLabel)) {

          aSet.count = aSet.count + 1;

          found = true;

          break;

        }

      }

      if (!found) {

        AvcSet aSet = new AvcSet();

        aSet.attrName = attrName;

        aSet.lowValue = lowValue;

        aSet.highValue = highValue;

        aSet.classLabel = classLabel;

        aSet.count = 1;

        avcSets[col].add(aSet);

      }

    }

    return (avcSets);

  }



  /**

    Build a decision tree.



    let examples(v) be those examples with A = v.



    if examples(v) is empty, make the new branch a leaf labelled with

    the most common value among examples.



    else let the new branch be the tree created by recursive call buildTree.



    @param path the spliting values from root to the current node

    @param availCols the candidate columns to use

    @param aNodeInfo an object of NodeInfo that includes: avcSets, data, classTallies, and uniqValues

    @param examples the index list of example set (this param only used in in-memory mode)

    @return a tree node

  */



  private DecisionForestNode buildTree(ArrayList path, String[] availCols, NodeInfo aNodeInfo,

                                       int[] examples) {

    DecisionForestNode root = null;



    ArrayList[] avcs = aNodeInfo.avcSets;

    int[] classTallies = aNodeInfo.classTallies;

    MutableTableImpl data = aNodeInfo.data;

    ArrayList[] uniqValues = aNodeInfo.uniqValues;

    ArrayList[] newUniqValues;

    int total = getTotalTallies(aNodeInfo.classTallies);



    if(total < minimumRecordsPerLeaf) {

      //System.out.println("create a leaf for data set < minimumRecordsPerleaf");

      String mostCommon = mostCommonOutputValue(classTallies);

      // make a leaf

      root = new CategoricalDecisionForestNode(mostCommon,classValues.length);

      root.outputTallies = classTallies;

      root.outputValues = classValues;

      return((DecisionForestNode)root);

    }

    // if all avcSets are in a same class, give the root this label -- this node is a leaf

    else if (isSingleClass(classTallies)) {

      //System.out.println("create a leaf for a single class");

      // get the class label

      String s = mostCommonOutputValue(classTallies);

      // make a leaf

      root = new CategoricalDecisionForestNode(s,classValues.length);

      root.outputTallies = classTallies;

      root.outputValues = classValues;

      return((DecisionForestNode)root);

    }

    else if (availCols.length == 0) {

      //System.out.println("create a leaf for availCols.length==0");

      String mostCommon = mostCommonOutputValue(classTallies);

      root = new CategoricalDecisionForestNode(mostCommon,classValues.length);

      root.outputTallies = classTallies;

      root.outputValues = classValues;

      return((DecisionForestNode)root);

    }

    else {

      // otherwise build the subtree rooted at this node

      // calculate the information gain for each attribute

      // select the attribute, A, with the lowest average entropy, make

      // this be the one tested at the root

      ColSplit best = getHighestGainAttribute(path, availCols, aNodeInfo);

      // if there was a column

      if(best != null) {

        int col = best.col;

        // categorical data

        if (!meta.isColumnScalar(col)) {

          // for each possible value v of this attribute add a new branch below the root,

          // corresponding to A = v

          root = new CategoricalDecisionForestNode(meta.getColumnLabel(col).toString(),classValues.length);

          root.outputTallies = classTallies;

          root.outputValues = classValues;

          // Since avcs contains the pairs of column value with class label, e.g.

          // "Race=F,Yes", "Race=F,No", we need to get distinct values in the column.

          ArrayList distinctValues = (ArrayList)uniqValues[col];

          int branchCount = 0;

          for (int idx=0; idx<distinctValues.size(); idx++) {

            ArrayList newPath;

            String newValue = distinctValues.get(idx).toString();

            String newAttrValue = meta.getColumnLabel(col) + "='" + newValue + "'";

            newPath = expandSplitValues(path, newAttrValue);

            String[] newCols;

            newCols = narrowAttributes(meta.getColumnLabel(col),availCols);

            NodeInfo newNodeInfo;

            int[] branchExam;

            if (newCols.length > 0) {

              int subTot = subTotal(avcs, meta.getColumnLabel(col), newValue);

              // Stay in in-database mode

              if (examples == null && subTot >= modeThreshold) {

                newUniqValues = getUniqValues(dbTable, newPath, newCols);

                newNodeInfo = extractDataFromDB(dbTable,classColName,newPath,newCols,uniqValues);

                branchExam = null;

              }

              // Switch to in-memory mode from in-database mode

              else if (examples == null && subTot < modeThreshold) {

                newNodeInfo = createDataTable(dbTable, meta, whereClause, newPath, subTot);

                branchExam = new int[newNodeInfo.data.getNumRows()];

                for (int rowIdx=0; rowIdx<newNodeInfo.data.getNumRows(); rowIdx++) {

                  branchExam[rowIdx] = rowIdx;

                }

              }

              else { // in-memory mode

                branchExam = narrowCategoricalExamples(data, col, newValue, examples);

                newUniqValues = getUniqValues(data, branchExam, newCols);

                newNodeInfo = partitionDataSet(data, branchExam, newCols, newUniqValues);

              }



              if (getTotalTallies(newNodeInfo.classTallies)>=minimumRecordsPerLeaf) {

                DecisionForestNode tmpRoot = buildTree(newPath,newCols,newNodeInfo,branchExam);

                if (tmpRoot != null) {

                  branchCount++;

                  root.addBranch(newValue,tmpRoot);

                }

              }

              else {// tallies < minimumRecordsPerLeaf

                //System.out.println("create a leaf because the tallies < minimumRecordsPerLeaf.");

                String mostCommon = mostCommonOutputValue(newNodeInfo.classTallies);

                CategoricalDecisionForestNode tmpRoot = new CategoricalDecisionForestNode(mostCommon,classValues.length);

                tmpRoot.outputTallies = newNodeInfo.classTallies;

                tmpRoot.outputValues = classValues;

                branchCount++;

                root.addBranch(newValue, tmpRoot);

              }

            }

          } // end of loop for each distinct value

          // if branchCount == 0, that indicates all branches have been pruned, create a leaf to attach to the root

          if (branchCount == 0) {

            //System.out.println("All branches have been pruned, create a leaf.");

            String mostCommon = mostCommonOutputValue(aNodeInfo.classTallies);

            DecisionForestNode tmpRoot = new CategoricalDecisionForestNode(mostCommon,classValues.length);

            tmpRoot.outputTallies = aNodeInfo.classTallies;

            tmpRoot.outputValues = classValues;

            return (DecisionForestNode)tmpRoot;

          }

        } // end for categorical data

        else { // is a numeric column

          DecisionForestNode left=null;

          DecisionForestNode right=null;

          root = new NumericDecisionForestNode(meta.getColumnLabel(col).toString(),classValues.length);

          root.outputTallies = classTallies;

          root.outputValues = classValues;

          ArrayList newPath;

          double newValue = best.splitValue;

          // build left branch

          String leftAttrValue = meta.getColumnLabel(col) + " < " + newValue;

          String leftLabel = meta.getColumnLabel(col) + " < " + nf.format(newValue);

          newPath = expandSplitValues(path, leftAttrValue);

          NodeInfo newNodeInfo = null;

          int[] branchExam = null;

          int subTot = subTotal(avcs, meta.getColumnLabel(col), "<"+Double.toString(newValue));

          // Stay in in-database mode

          if (examples == null && subTot >= modeThreshold) {

            newUniqValues = getUniqValues(dbTable, newPath, availCols);

            newNodeInfo = extractDataFromDB(dbTable,classColName,newPath,availCols,newUniqValues);

            branchExam = null;

          }

          // Switch to in-memory mode from in-database mode

          else if (examples == null && subTot < modeThreshold) {

              newNodeInfo = createDataTable(dbTable, meta, whereClause, newPath, subTot);

              branchExam = new int[newNodeInfo.data.getNumRows()];

              for (int rowIdx=0; rowIdx<newNodeInfo.data.getNumRows(); rowIdx++) {

                branchExam[rowIdx] = rowIdx;

              }

          }

          else { // in-memory mode

            branchExam = narrowNumericExamples(data, col, best.splitValue, examples, false);

            newUniqValues = getUniqValues(data, branchExam, availCols);

            newNodeInfo = partitionDataSet(data, branchExam, availCols, newUniqValues);

          }

          if (getTotalTallies(newNodeInfo.classTallies)>=minimumRecordsPerLeaf) {

            // If the numeric attribute has been used once, compare the dominateRatio with

            // parent nodes. Only generate a node if the dominateRatio improves.

            if (repeats(meta.getColumnLabel(col),newPath) >= 2) {

              if (isRatioUp(aNodeInfo.classTallies, newNodeInfo.classTallies)) {

                left = buildTree(newPath, availCols, newNodeInfo, branchExam);

              }

              else { // create a leaf because dominate ratio is not up

                //System.out.println("create a left leaf because the dominate ratio is not up.");

                String mostCommon = mostCommonOutputValue(newNodeInfo.classTallies);

                CategoricalDecisionForestNode tmpRoot = new CategoricalDecisionForestNode(mostCommon,classValues.length);

                tmpRoot.outputTallies = newNodeInfo.classTallies;

                tmpRoot.outputValues = classValues;

                left = (DecisionForestNode) tmpRoot;

              }

            }

            else { // the numeric attribute has not been used

              left = buildTree(newPath, availCols, newNodeInfo, branchExam);

            }

          }

          else {// total tallies are < minimumRecordsPerLeaf

            //System.out.println("create a left leaf because tallies are < minimumRecordsPerLeaf");

            String mostCommon = mostCommonOutputValue(newNodeInfo.classTallies);

            CategoricalDecisionForestNode tmpRoot = new CategoricalDecisionForestNode(mostCommon,classValues.length);

            tmpRoot.outputTallies = newNodeInfo.classTallies;

            tmpRoot.outputValues = classValues;

            left = (DecisionForestNode) tmpRoot;

          }

          // build right branch

          //isEmpty = false;

          newNodeInfo = null;

          String rightAttrValue = meta.getColumnLabel(col) + " >= " + newValue;

          String rightLabel = meta.getColumnLabel(col) + " >= " + nf.format(newValue);

          newPath = expandSplitValues(path, rightAttrValue);

          subTot = subTotal(avcs, meta.getColumnLabel(col), ">="+Double.toString(newValue));

          branchExam = null;

          // Stay in in-database mode

          if (examples == null && subTot >= modeThreshold) {

            newUniqValues = getUniqValues(dbTable, newPath, availCols);

            newNodeInfo = extractDataFromDB(dbTable,classColName,newPath,availCols,newUniqValues);

            branchExam = null;

          }

          // Switch to in-memory mode from in-database mode

          else if (examples == null && subTot < modeThreshold) {

              newNodeInfo = createDataTable(dbTable, meta, whereClause, newPath, subTot);

              branchExam = new int[newNodeInfo.data.getNumRows()];

              for (int rowIdx=0; rowIdx<newNodeInfo.data.getNumRows(); rowIdx++) {

                branchExam[rowIdx] = rowIdx;

              }

          }

          else { // in-memory mode

            branchExam = narrowNumericExamples(data, col, best.splitValue, examples, true);

            newUniqValues = getUniqValues(data, branchExam, availCols);

            newNodeInfo = partitionDataSet(data, branchExam, availCols, newUniqValues);

          }

          if (getTotalTallies(newNodeInfo.classTallies)>=minimumRecordsPerLeaf) {

            // If the numeric attribute has been used once, compare the dominateRatio with

            // parent nodes. Only generate a node if the dominateRatio improves.

            if (repeats(meta.getColumnLabel(col),newPath) >= 2) {

              if (isRatioUp(aNodeInfo.classTallies, newNodeInfo.classTallies)) {

                right = buildTree(newPath, availCols, newNodeInfo, branchExam);

              }

              else { // create a leaf because dominate ratio is not up

                //System.out.println("create a right leaf because the dominate ratio is not up.");

                String mostCommon = mostCommonOutputValue(newNodeInfo.classTallies);

                CategoricalDecisionForestNode tmpRoot = new CategoricalDecisionForestNode(mostCommon,classValues.length);

                tmpRoot.outputTallies = newNodeInfo.classTallies;

                tmpRoot.outputValues = classValues;

                right = (DecisionForestNode) tmpRoot;

              }

            }

            else { // the numeric attribute has not been used

              right = buildTree(newPath, availCols, newNodeInfo, branchExam);

            }

          }

          else {// total tallies are < minimumRecordsPerLeaf

            //System.out.println("create a right leaf because tallies are < minimumRecordsPerLeaf");

            String mostCommon = mostCommonOutputValue(newNodeInfo.classTallies);

            CategoricalDecisionForestNode tmpRoot = new CategoricalDecisionForestNode(mostCommon,classValues.length);

            tmpRoot.outputTallies = newNodeInfo.classTallies;

            tmpRoot.outputValues = classValues;

            right = (DecisionForestNode) tmpRoot;

          }

          if (left != null && right != null) {

            // add two branches to the root

            root.addBranches(newValue, leftLabel, left, rightLabel, right);

          }

          else if (left != null) {

            // add the left branch to the root

            root.addBranch(newValue, leftLabel, left);

          }

          else if (right != null) {

            // add the right branch to the root

            root.addBranch(newValue, rightLabel, right);

          }

          else {

            //System.out.println("create a leaf because there is no branch to add");

            String mostCommon = mostCommonOutputValue(aNodeInfo.classTallies);

            CategoricalDecisionForestNode tmpRoot = new CategoricalDecisionForestNode(mostCommon,classValues.length);

            tmpRoot.outputTallies = aNodeInfo.classTallies;

            tmpRoot.outputValues = classValues;

            return (DecisionForestNode)tmpRoot;

          }

        }

      }

      // otherwise we could not find a suitable column. create

      // a new node with the most common output

      else { // best == null

        //System.out.println("make a leaf because there is no best column");

        String mostCommon = mostCommonOutputValue(classTallies);

        // make a leaf

        root = new CategoricalDecisionForestNode(mostCommon,classValues.length);

        root.outputTallies = classTallies;

        root.outputValues = classValues;

        data = null;

        return (DecisionForestNode)root;

      }

    }

    return((DecisionForestNode)root);

  }



  /** find whether all avcSets are in a same class

   *  Modified the original algorithm as the following:

   *  If (%mostCommonVal) / (%secondMostCommonVal) > dominateRatio, return true.

   *  This will prune some trivial node.

   *  @param classTallies count for each class

   *  @return true if all data belong to a single class or dominated by a class,

   *          false otherwise.

   */

  private boolean isSingleClass(int[] classTallies) {

    int mostCommonVal = 0;

    int secondMostCommonVal = 0;

    int total = 0;

    for (int classIdx = 0; classIdx < classTallies.length; classIdx++) {

      total = total + classTallies[classIdx];

      if (classTallies[classIdx] > mostCommonVal) {

        secondMostCommonVal = mostCommonVal;

        mostCommonVal = classTallies[classIdx];

      }

      else if (classTallies[classIdx] > secondMostCommonVal) {

        secondMostCommonVal = classTallies[classIdx];

      }

    }

    if ((total-mostCommonVal) == 0) {

      return true;

    }

    else if (((double)mostCommonVal/(double)secondMostCommonVal) > dominateRatio) {

      return true;

    }

    else {

      return false;

    }

  }



  /** find the most common attribute value in the output column

   *  @param classTallies an integer array for the classTallies

   *  @return String for a class label

   */

  private String mostCommonOutputValue(int[] classTallies) {

    int mostCommonVal = 0;

    int mostCommonIdx = 0;

    for (int classIdx = 0; classIdx < classTallies.length; classIdx++) {

      if (classTallies[classIdx] > mostCommonVal) {

        mostCommonIdx = classIdx;

        mostCommonVal = classTallies[classIdx];

      }

    }

    return (classValues[mostCommonIdx]);

  }



  /** convert an ArrayList to a String array

   *  @param ar an ArrayList

   *  @return an String array

   */

  private String[] toStringArray(ArrayList ar) {

    if (ar.size() > 0) {

      String[] stringArray = new String[ar.size()];

      for (int idx = 0; idx < ar.size(); idx++) {

        stringArray[idx] = ar.get(idx).toString();

      }

      return stringArray;

    }

    else

      return null;

  }



  /** find an attribute that has the highest information gain

   *  @param path The branch labels from root to the current node

   *  @param availCols The candidate columns to choose for split

   *  @param aNodeInfo object contains classTallies, avcSets, data table, unique value list.

   *  @return object contains the best column and the best split value

   */

  private ColSplit getHighestGainAttribute(ArrayList path, String[] availCols, NodeInfo aNodeInfo){

    //int topCol = 0;

    ArrayList avcs[] = aNodeInfo.avcSets;

    double highestGain = Double.MIN_VALUE;

    ColSplit retVal = new ColSplit();

    // for each available column, calculate the entropy

    for (int i = 0; i < availCols.length; i++) {

      String col = availCols[i];

      if (!isScalar(availCols[i])) {

        double d = categoricalGain(path, col, aNodeInfo);

        if (d > highestGain) {

          highestGain = d;

          retVal.col = getColIdx(col);

        }

      }

      else {// it is a scalar column

        EntrSplit sce = numericGain(path, col, aNodeInfo);

        if (sce != null && sce.gain >highestGain) {

          highestGain = sce.gain;

          retVal.col = getColIdx(col);

          retVal.splitValue = sce.splitValue;

        }

      }

    }

    if (highestGain > Double.MIN_VALUE)

      return retVal;

    else

      return null;

  }



  /** get tallies for the class label column

   *  @param column the class column

   *  @param val the class value

   *  @param avcs AVC sets to search

   *  @return the tallies of the class column

   */

  private int[] getClassTallies(String column, String val, ArrayList[] avcs) {

    int col = getColIdx(column);

    int[] childTallies = new int[classValues.length];

    for (int i = 0; i<childTallies.length; i++) {

      childTallies[i] = 0;

    }

    // for non-numeric columns

    if (!meta.isColumnScalar(col)) {

      // only use the records in avcs that match the column and the value

      for (int setIdx = 0; setIdx < avcs[col].size(); setIdx++) {

        AvcSet aSet = (AvcSet)avcs[col].get(setIdx);

        if (aSet.attrName.equals(column) &&

            aSet.lowValue.equals(val)) {

          for (int valueIdx=0; valueIdx<classValues.length; valueIdx++) {

            if (aSet.classLabel.equals(classValues[valueIdx])) {

              childTallies[valueIdx] = aSet.count;

            }

          }

        }

      }

    }

    else { // numeric columns

      if (val.indexOf(">=") >= 0) {

        double tmpValue = Double.parseDouble(val.substring(val.indexOf(">=")+2, val.length()));

        // only use the records in avcs that match the column and the value

        for (int setIdx = 0; setIdx < avcs[col].size(); setIdx++) {

          AvcSet aSet = (AvcSet)avcs[col].get(setIdx);

          if (aSet.attrName.equals(column) &&

              Double.parseDouble(aSet.lowValue) >= tmpValue) {

            for (int valueIdx=0; valueIdx<classValues.length; valueIdx++) {

              if (aSet.classLabel.equals(classValues[valueIdx])) {

                childTallies[valueIdx] = childTallies[valueIdx] + aSet.count;

              }

            }

          }

        }

      }

      else if (val.indexOf("<") >= 0) {

        double tmpValue = Double.parseDouble(val.substring(val.indexOf("<")+1, val.length()));

        // only use the records in avcs that match the column and the value

        for (int setIdx = 0; setIdx < avcs[col].size(); setIdx++) {

          AvcSet aSet = (AvcSet)avcs[col].get(setIdx);

          if (aSet.attrName.equals(column) &&

              Double.parseDouble(aSet.highValue) <= tmpValue) {

            for (int valueIdx=0; valueIdx<classValues.length; valueIdx++) {

              if (aSet.classLabel.equals(classValues[valueIdx])) {

                childTallies[valueIdx] = childTallies[valueIdx] + aSet.count;

              }

            }

          }

        }

      }

    }

    return childTallies;

  }



  /** Find the information gain for a categorical attribute. The gain

   *  ratio is used, where the information gain is divided by the

   *  split information. This prevents highly branching attributes

   *  from becoming dominant.

   *  @param aPath the spliting values so far have been used

   *  @param column the input column

   *  @param aNodeInfo an object of NodeInfo that includes: avcSets, data, classTallies, and uniqValues

   *  @return highest gain

   */

  private double categoricalGain(ArrayList aPath, String column, NodeInfo aNodeInfo) {

    int col = getColIdx(column);

    ArrayList[] avcs = aNodeInfo.avcSets;

    int[] currTallies = aNodeInfo.classTallies;

    ArrayList[] uniqValues = aNodeInfo.uniqValues;

    double gain = info(currTallies);

    // get total count for this column

    double tot = getTotalCount(column, avcs);

    // if the column has no values, return the minimum gain

    if (uniqValues[col].size()==0) {

      return (Double.MIN_VALUE);

    }

    // now subtract the entropy for each unique value in the column

    ArrayList distinctValues = (ArrayList)uniqValues[col];

    for (int idx=0; idx<distinctValues.size(); idx++) {

      int[] childTallies = getClassTallies(column, distinctValues.get(idx).toString(), avcs);

      double tallyTotal = (double)getTotalTallies(childTallies);

      gain -= (tallyTotal/tot)*info(childTallies);

    }

    double sInfo = splitInfo(column, avcs);

    // divide the information gain by the split info

    // since sInfo could be 0 if all examples have a same value in this column, add 0.000001 to it

    gain /= (sInfo + 0.000001);

    return gain;

  }



  /** Find the information gain for a numeric attribute.

   *  @param aPath the spliting values so far have been used

   *  @param column the input column

   *  @param aNodeInfo an object of NodeInfo that includes: avcSets, data, classTallies, and uniqValues

   *  @return object contains the best column and the best split value

   */

  private EntrSplit numericGain(ArrayList aPath, String column, NodeInfo aNodeInfo) {

    int col = getColIdx(column);

    ArrayList[] avcs = aNodeInfo.avcSets;

    int[] currTallies = aNodeInfo.classTallies;

    ArrayList[] distinctValues = aNodeInfo.uniqValues;

    EntrSplit split = new EntrSplit();

    double highestGain = Double.MIN_VALUE;

    double baseGain = info(currTallies);

    // no further split for the data set, return null

    if (distinctValues[col].size()==0) {

      return null;

    }

    // get total count for this column

    // double tot = getTotalCount(column, avcs);

    // for each split value, calculate the information gain

    ArrayList aColumn = (ArrayList)distinctValues[col];

    for (int rowIdx=0; rowIdx<aColumn.size(); rowIdx++) {

      double aSplitValue = Double.parseDouble(aColumn.get(rowIdx).toString());

      double gain = baseGain - numericAttributeInfo(aSplitValue, column, avcs);



      // do not calculate split info for numeric attribute

      // double sInfo = splitInfo(column, avcs, aSplitValue);

      // gain /= (sInfo + 0.000001);



      if (gain > highestGain) {

        highestGain = gain;

        split.gain = gain;

        split.splitValue = aSplitValue;

      }

    }

    if (highestGain > Double.MIN_VALUE)

      return split;

    else

      return null;

  }



  /** Find the column index for the given column name

   *  @param col The column name

   *  @return column index

   */

  private int getColIdx(String col){

    for (int idx = 0; idx < meta.getNumColumns(); idx++) {

      if (meta.getColumnLabel(idx).equals(col)) {

        return idx;

      }

    }

    return -1;

  };



  /** calculate entropy sum(-p lg2 p)

   * @param tallies An integer array to keep the tallies

   * @return computed entropy

   */

  private double info(int[] tallies) {

    int total = 0;

    for(int i = 0; i < tallies.length; i++)

      total += tallies[i];

    double dtot = (double)total;

    double retVal = 0;

    for(int i = 0; i < tallies.length; i++)

      if (tallies[i]>0) {

        retVal -= ((tallies[i]/dtot)*lg(tallies[i]/dtot));

      }

    return retVal;

  }



  /** create a new array list, first copy the original array list then

   *  add a new value to the new array list

   *  @param origArray the original ArrayList

   *  @param newValue the value to add to origArray

   *  @return a new ArrayList

   */

  protected ArrayList expandSplitValues(ArrayList origArray, String newValue) {

    ArrayList newArray = new ArrayList();

    for (int i=0; i < origArray.size(); i++) {

      newArray.add(origArray.get(i));

    }

    newArray.add(newValue);

    return (newArray);

  }



  /**

    Return the binary log of a number.  This is defined as

    x such that 2^x = d

    @param d the number to take the binary log of

    @return the binary log of d

  */

  private static final double lg(double d) {

    return Math.log(d)/Math.log(2.0);

  }



  /** get item count for this column

   *  @param column the column name

   *  @param avcs the list of AVC sets

   *  @return the item count

   */

  private double getTotalCount(String column, ArrayList[] avcs) {

    int colIdx = getColIdx(column);

    double total = 0.0;

    for (int setIdx = 0; setIdx < avcs[colIdx].size(); setIdx++) {

      AvcSet aSet = (AvcSet)avcs[colIdx].get(setIdx);

      if (aSet.attrName.equals(column)) {

        total += aSet.count;

      }

    }

    return (total);

  }



  /** Find the count for a specified column and specified value

   *  @param avcs The AVC sets

   *  @param columnLabel The column name

   *  @param columnValue The column value

   *  @return the count match the column name and column value

   */

  private int subTotal(ArrayList[] avcs, String columnLabel, String columnValue) {

    int colIdx = getColIdx(columnLabel);

    int subTotal = 0;

    // for non-numeric columns

    if (!meta.isColumnScalar(colIdx)) {

      for (int setIdx = 0; setIdx < avcs[colIdx].size(); setIdx++) {

        AvcSet aSet = (AvcSet)avcs[colIdx].get(setIdx);

        if (aSet.attrName.equals(columnLabel) &&

          aSet.lowValue.equals(columnValue)) {

          subTotal = subTotal + aSet.count;

        }

      }

    }



    else { // numeric columns

      if (columnValue.indexOf(">=") >= 0) {

        double tmpValue = Double.parseDouble(columnValue.substring(columnValue.indexOf(">=")+2, columnValue.length()));

        // only use the records in avcs that match the column and the value

        for (int setIdx = 0; setIdx < avcs[colIdx].size(); setIdx++) {

          AvcSet aSet = (AvcSet)avcs[colIdx].get(setIdx);

          if (aSet.attrName.equals(columnLabel) &&

             Double.parseDouble(aSet.lowValue) >= tmpValue) {

            subTotal = subTotal + aSet.count;

          }

        }

      }

      else if (columnValue.indexOf("<") >= 0) {

        double tmpValue = Double.parseDouble(columnValue.substring(columnValue.indexOf("<")+1, columnValue.length()));

        // only use the records in avcs that match the column and the value

        for (int setIdx = 0; setIdx < avcs[colIdx].size(); setIdx++) {

          AvcSet aSet = (AvcSet)avcs[colIdx].get(setIdx);

          if (aSet.attrName.equals(columnLabel) &&

              Double.parseDouble(aSet.highValue) <= tmpValue) {

            subTotal = subTotal + aSet.count;

          }

        }

      }

    }

    return subTotal;

  }



   /** Determine the split info. This is the information given by the

   *  number of branches of a node. The size of the subsets that each

   *  branch creates is calculated and then the information is calculated

   *  from that.

   *  @param column the column name

   *  @param avcs the AVC list

   *  @return the information value of the branches of this attribute

   */

  private double splitInfo(String column, ArrayList[] avcs){

    HashMap map = new HashMap();

    TIntArrayList tallies = new TIntArrayList();

    int colIdx = getColIdx(column);

    if (!meta.isColumnScalar(colIdx)) {

      for (int setIdx = 0; setIdx < avcs[colIdx].size(); setIdx++) {

        AvcSet aSet = (AvcSet)avcs[colIdx].get(setIdx);

        if (aSet.attrName.equals(column)) {

          if (!map.containsKey(aSet.lowValue)) {

            map.put(aSet.lowValue, new Integer(tallies.size()));

            tallies.add(aSet.count);

          }

          else {

            Integer ii = (Integer)map.get(aSet.lowValue);

            tallies.set(ii.intValue(), tallies.get(ii.intValue())+aSet.count);

          }

        }

      }

    }

    return info(tallies.toNativeArray());

  }



  /** Find the information gain of a numeric column

   *  @param aSplitValue The spliting value to use

   *  @param column The column name

   *  @param avcs The AVC sets for the current node

   *  @return the information gain using the aSplitValue

   */

  private double numericAttributeInfo(double aSplitValue, String column, ArrayList[] avcs) {

    String lbranch = "<" + Double.toString(aSplitValue);

    String gbranch = ">=" + Double.toString(aSplitValue);

    int[] lessThanTally = getClassTallies(column, lbranch, avcs);

    int[] greaterThanTally = getClassTallies(column, gbranch, avcs);

    int lessThanTot = getTotalTallies(lessThanTally);

    int greaterThanTot = getTotalTallies(greaterThanTally);

    // if no data, return Double.MIN_VALUE

    if (lessThanTot == 0 && greaterThanTot == 0)

      return Double.MIN_VALUE;

    else {

      double linfo = info(lessThanTally);

      double ginfo = info(greaterThanTally);

      double retval =  (lessThanTot/(double)(lessThanTot+greaterThanTot))*linfo +

             (greaterThanTot/(double)(lessThanTot+greaterThanTot)*ginfo);



      return retval;

    }

  };



  /** This method scans the database once and does the following tasks.

  *  1. insert data into an in-memory table

  *  2. build the ArrayList uniqValues

  *  3. get the String[] classValues

  *  4. compute the int[] classTallies

  *  5. build the avcSets from the in-memory table

  *  @param dbTable the database table to read

  *  @param meta the meta information to use

  *  @param path the split branches from root to the current node

  *  @param total the total rows in the data set

  *  @return an object of NodeInfo that includes: avcSets, data, classTallies, and uniqValues

  */

  private NodeInfo createDataTable(String dbTable, ExampleTable meta, String whereClause,

                                   ArrayList path, int total) {

    // ArrayList for uniqValues

    ArrayList[] uniqValues = new ArrayList[meta.getNumColumns()];

    // ArrayList for avcSet

    ArrayList[] avcSets = new ArrayList[meta.getNumColumns()];



    // table to keep the raw data

    MutableTableImpl data = new MutableTableImpl();

    // columns in the data table

    Column[] cols = new Column[meta.getNumColumns()];

    for(int colIdx = 0; colIdx < cols.length; colIdx++) {

      uniqValues[colIdx] = new ArrayList();

      avcSets[colIdx] = new ArrayList();

      cols[colIdx] = new ObjectColumn(total);

      cols[colIdx].setLabel(meta.getColumnLabel(colIdx));

      if (meta.isColumnScalar(colIdx)) {

        cols[colIdx].setIsScalar(true);

      }

      else {

        cols[colIdx].setIsScalar(false);

      }

    }

    data =  new MutableTableImpl (cols);

    try {

      con = cw.getConnection();

      String dataQry = new String("select ");

      for (int colIdx=0; colIdx<data.getNumColumns(); colIdx++) {

        if (colIdx > 0) {

          // add comma between columns

          dataQry = dataQry + ", ";

        }

        dataQry = dataQry + data.getColumnLabel(colIdx);

      }

      if (whereClause != null && whereClause.length() > 0) {

        dataQry = dataQry + " from " + dbTable + " where (" + whereClause + ") and " +

                  classColName + " is not null";

      }

      else {

        dataQry = dataQry + " from " + dbTable + " where " + classColName +

            " is not null";

      }

      if (path.size() > 0) {

        for (int pathIdx=0; pathIdx<path.size(); pathIdx++) {

          dataQry = dataQry + " and " + path.get(pathIdx);

        }

      }

      //System.out.println("dataQry in createDataTable is " + dataQry);

      Statement dataStmt = con.createStatement ();

      ResultSet dataSet = dataStmt.executeQuery(dataQry);

      int rowIdx=0;

      while (dataSet.next()) {

        for (int colIdx=0; colIdx<data.getNumColumns(); colIdx++) {

          // build ArrayList uniqValues and insert data in to the data table

          if (data.isColumnScalar(colIdx)) {

            double attrValue = dataSet.getDouble(colIdx+1);

            // handle missing value in the scalar column

            if (attrValue<Double.MIN_VALUE || attrValue>Double.MAX_VALUE) {

              attrValue = Double.MIN_VALUE;

              data.setDouble(attrValue,rowIdx,colIdx);

            }

            else {

              // if not in the uniqValues, add it

              if (!inList(Double.toString(attrValue), (ArrayList)uniqValues[colIdx])) {

                ((ArrayList)uniqValues[colIdx]).add(Double.toString(attrValue));

              }

              data.setDouble(attrValue,rowIdx,colIdx);

            }

          }

          else { // is a non-numeric column

            String attrValue = dataSet.getString(colIdx+1);

            // handle missing value in the categorical column

            if (attrValue==null || attrValue.equals(" ") || attrValue.equals("")) {

              attrValue = NOTHING;

              data.setString(attrValue,rowIdx,colIdx);

            }

            else {

              // if not in the uniqValues, add it

              if (!inList(attrValue, (ArrayList)uniqValues[colIdx])) {

                ((ArrayList)uniqValues[colIdx]).add(attrValue);

              }

              data.setString(attrValue,rowIdx,colIdx);

            }

          }

        }

        rowIdx++;

      } //  end of while

      uniqValues = reorgUniqValues(uniqValues);



      if (classValues == null) {

        classValues = getClassValues(uniqValues);

      }



      // Tally for each possible class label

      int[] classTallies = new int[classValues.length];

      // initiate classTallies

      for (int i = 0; i<classTallies.length; i++) {

        classTallies[i] = 0;

      }



      String classLabel=NOTHING;

      for (int row=0; row<data.getNumRows(); row++) {

        // for loop to get classLabel and update classTallies

        for (int col=0; col<data.getNumColumns(); col++) {

          if (col==outputFeatures[0]) {

            classLabel = data.getString(row, col);

            for (int valueIdx = 0; valueIdx < classValues.length; valueIdx++) {

              if (classLabel.equals(classValues[valueIdx])) {

                classTallies[valueIdx]++;

                break;

              }

            }

          }

        }

        // for loop to build the AVC Sets

        for (int col=0; col<data.getNumColumns(); col++) {

          if (col != outputFeatures[0] && data.isColumnScalar(col)) {

            double attrValue = data.getDouble(row, col);

            String attrName = data.getColumnLabel(col);

            avcSets = updAvcSets(avcSets, attrName, attrValue, classLabel, uniqValues);

          }

          else if (col != outputFeatures[0] && !data.isColumnScalar(col)) {

            String attrValue = data.getString(row, col);

            String attrName = data.getColumnLabel(col);

            avcSets = updOneAvc(avcSets, attrName, attrValue, classLabel);

          }

        }

      }

      NodeInfo aNodeInfo = new NodeInfo(avcSets, classTallies, data, uniqValues);

      return aNodeInfo;

    }

    catch (Exception e){

      JOptionPane.showMessageDialog(msgBoard,

                e.getMessage(), "Error",

                JOptionPane.ERROR_MESSAGE);

      System.out.println("Error occurred in createDataTable.");

      return null;

    }

  }



  /**

    Create a subset of the examples.  Only those examples where the value

    is equal to val will be in the subset.

    @param data the data set to search for

    @param col the column to test

    @param val the value to test

    @param exam the list of examples to narrow

    @return a subset of the original data

  */

  private int[] narrowCategoricalExamples(MutableTableImpl data, int col, String val, int[] exam) {

    int numNewExamples = 0;

    boolean[] map = new boolean[exam.length];



    for(int i = 0; i < exam.length; i++) {

      String s = data.getString(exam[i], col);

      if(s.equals(val)) {

        numNewExamples++;

        map[i] = true;

      }

      else

        map[i] = false;

    }

    int[] examples = new int[numNewExamples];

    int curIdx = 0;

    for(int i = 0; i < exam.length; i++) {

      if(map[i]) {

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

    @param data the data set to search for

    @param col the column to test

    @param splitValue the value to test

    @param exam the list of examples to narrow

    @param greaterThan true if values greater than the split value should

      be in the new list of examples, false if values less than the split

      value should be in the list of examples

    @return a subset of the original list of examples

  */

  private int[] narrowNumericExamples(MutableTableImpl data, int col, double splitValue, int[] exam,

                boolean greaterThan) {



    int numNewExamples = 0;

    boolean[] map = new boolean[exam.length];



    for(int i = 0; i < exam.length; i++) {

      double d = data.getDouble(exam[i], col);

      if(greaterThan) {

        if(d >= splitValue) {

          numNewExamples++;

          map[i] = true;

        }

        else

          map[i] = false;

      }

      else {

        if(d < splitValue) {

          numNewExamples++;

          map[i] = true;

        }

        else

          map[i] = false;

      }

    }



    int[] examples = new int[numNewExamples];

    int curIdx = 0;

    for(int i = 0; i < exam.length; i++) {

      if(map[i]) {

        examples[curIdx] = exam[i];

        curIdx++;

      }

    }

    return examples;

  }



  /** return true if aString is in a String List

   *  @param aString The string to check for

   *  @param aList The string list to scan

   *  @return ture if aString is in aList, false otherwise

   */

  private boolean inList(String aString, String[] aList) {

    if (aString == null || aString.equals(" ") || aString.equals("")) {

      return true; // do not add null value to the list

    }

    for (int listIdx=0; listIdx<aList.length; listIdx++) {

      if (aString.equals(aList[listIdx])) {

        return true;

      }

    }

    return false;

  }



  /** return true if aString is in a ArrayList

   *  @param aString The string to check for

   *  @param aList The array list to scan

   *  @return true if aString is in aList, false otherwise

   */

  private boolean inList(String aString, ArrayList aList) {

    if (aString == null || aString.equals(" ") || aString.equals("")) {

      return true; // do not add null value to the list

    }

    for (int strIdx=0; strIdx<aList.size(); strIdx++) {

      if (aList.get(strIdx).toString().equals(aString)) {

        return true;

      }

    }

    return false;

  }



  /** return aString's repeat number in aList

   *  @param aString The string to check for

   *  @param aList The array list to scan

   *  @return number of repeating

   */

  private int repeats (String aString, ArrayList aList) {

    int repeat = 0;

    for (int strIdx=0; strIdx<aList.size(); strIdx++) {

      if (aList.get(strIdx).toString().indexOf(aString) >= 0) {

        repeat++;

      }

    }

    return repeat;

  }



  /** get the uniq values from a database table

   *  @param dbTable the name of the database table

   *  @param path an ArrayList that keeps all branch labels from root to the current node

   *  @param availCols an String array to keep the candidate columns

   *  @return an ArrayList that keeps unique values for each column

   */

  private ArrayList[] getUniqValues(String dbTable, ArrayList path, String[] availCols) {

    ArrayList[] uniqCols = new ArrayList[meta.getNumColumns()];

    try {

      con = cw.getConnection();

      for (int colIdx = 0; colIdx < meta.getNumColumns(); colIdx++) {

        ArrayList aUniqCol = new ArrayList();

        // don't do anything if it is not the candidate column

        if (!inList(meta.getColumnLabel(colIdx), availCols)) {

          uniqCols[colIdx] = aUniqCol;

          continue;

        }

        String colName = meta.getColumnLabel(colIdx);

        String valueQry = new String("select distinct " + colName + " from ");

        if (whereClause != null && whereClause.length() > 0) {

          valueQry = valueQry + dbTable + " where (" + whereClause + ") and " +

                     colName + " is not null";

        }

        else

          valueQry = valueQry + dbTable + " where " + colName + " is not null";

        //System.out.println("in getUniqValues, valueQry is " + valueQry);

        Statement valueStmt = con.createStatement();

        ResultSet valueSet = valueStmt.executeQuery(valueQry);

        while (valueSet.next()) {

          aUniqCol.add(valueSet.getString(1));

        }

        if (!meta.isColumnScalar(colIdx)) { // non-numeric column

          uniqCols[colIdx] = aUniqCol;

        }

        else { // numeric column

          aUniqCol = sortUniqValues(aUniqCol);

          // if the uniq value <= binNumber, use all values as the split values

          if (aUniqCol.size() <= binNumber) {

            // the split value will be the halfway point between two values

            ArrayList newUniqCol = new ArrayList();

            for (int listIdx=0; listIdx<aUniqCol.size()-1; listIdx++) {

              double tmpVal = (Double.parseDouble(aUniqCol.get(listIdx).toString()) +

                             Double.parseDouble(aUniqCol.get(listIdx+1).toString()))/2;

              newUniqCol.add(Double.toString(tmpVal));

            }

            uniqCols[colIdx] = newUniqCol;

          }

          // if the uniq value > binNumber, calculate the split points

          else {

            ArrayList newUniqCol = new ArrayList();

            // find min value and max value

            double min = Double.parseDouble(aUniqCol.get(0).toString());

            double max = Double.parseDouble(aUniqCol.get(aUniqCol.size()-1).toString());

            // rebuild the ArrayList to keep spliting values for specified number of bins

            double interval = (max - min)/binNumber;

            for (int valueIdx=1; valueIdx<binNumber; valueIdx++) {

              double tmpval = min+(interval*valueIdx);

              newUniqCol.add(Double.toString(tmpval));

            }

            uniqCols[colIdx] = newUniqCol;

          }

        } // numeric column

      } // for each column

      return uniqCols;

    }

    catch (Exception e){

      JOptionPane.showMessageDialog(msgBoard,

                e.getMessage(), "Error",

                JOptionPane.ERROR_MESSAGE);

      System.out.println("Error occurred in getUniqValue (db mode).");

      return null;

    }

  }



  /** get the uniq values from an in-memory table

   *  @param data an MutableTableImpl that keeps the raw data

   *  @param examples an integer array to keep the list of index in data table

   *  @param availCols an String array for candidate columns

   *  @return an ArrayList for unique values in each column

   */

  private ArrayList[] getUniqValues(MutableTableImpl data, int[] examples, String[] availCols) {

    ArrayList[] uniqCols = new ArrayList[meta.getNumColumns()];

    for (int colIdx=0; colIdx<data.getNumColumns(); colIdx++) {

      ArrayList aUniqCol = new ArrayList();

      // don't do anything if it is not the candidate column

      if (!inList(meta.getColumnLabel(colIdx), availCols)) {

        uniqCols[colIdx] = aUniqCol;

        continue;

      }

      else if (!meta.isColumnScalar(colIdx)) { // non-numeric column

        for (int rowIdx=0; rowIdx<examples.length; rowIdx++) {

          String attrValue = data.getString(examples[rowIdx], colIdx);

          if (!inList(attrValue, aUniqCol)) {

            aUniqCol.add(attrValue);

          }

        }

        uniqCols[colIdx] = aUniqCol;

      } // if it is a non-numeric column

      else if (meta.isColumnScalar(colIdx)) { // numeric column

        for (int rowIdx=0; rowIdx<examples.length; rowIdx++) {

          double attrValue = data.getDouble(examples[rowIdx], colIdx);

          if (!inList(Double.toString(attrValue), aUniqCol)) {

            aUniqCol.add(Double.toString(attrValue));

          }

        }

        aUniqCol = sortUniqValues(aUniqCol);

        // if the uniq value <= binNumber, use all values as the split values

        if (aUniqCol.size() <= binNumber) {

          // the split value will be the halfway point between two values

          ArrayList newUniqCol = new ArrayList();

          for (int listIdx=0; listIdx<aUniqCol.size()-1; listIdx++) {

            double tmpVal = (Double.parseDouble(aUniqCol.get(listIdx).toString()) +

                             Double.parseDouble(aUniqCol.get(listIdx+1).toString()))/2;

            newUniqCol.add(Double.toString(tmpVal));

          }

          uniqCols[colIdx] = newUniqCol;

        }

        // if the uniq value > binNumber, calculate the split points

        else {

          ArrayList newUniqCol = new ArrayList();

          // find min value and max value

          double min = Double.parseDouble(aUniqCol.get(0).toString());

          double max = Double.parseDouble(aUniqCol.get(aUniqCol.size()-1).toString());

          // rebuild the ArrayList to keep spliting values for specified number of bins

          double interval = (max - min)/binNumber;

          for (int valueIdx=1; valueIdx<binNumber; valueIdx++) {

            double tmpval = min+(interval*valueIdx);

            newUniqCol.add(Double.toString(tmpval));

          }

          uniqCols[colIdx] = newUniqCol;

        }

      } // if it is a numeric column

    }

    return uniqCols;

  }



  /** reorganize uniqValues. For numeric column, if the number of the unique

  *   values less than binNumber, use the mid points of each two values as the split

  *   points. Otherwise, calculate the split point for each bin.

  *   @param uniqValues an ArrayList for unique values in each column

  *   @return an reorgnized ArrayList for unique values in each column

  */

  private ArrayList[] reorgUniqValues(ArrayList[] uniqValues) {

    for (int colIdx=0; colIdx<meta.getNumColumns(); colIdx++) {

      if (meta.isColumnScalar(colIdx)) { // numeric column

        ArrayList aUniqCol = (ArrayList)uniqValues[colIdx];

        aUniqCol = sortUniqValues(aUniqCol);

        if (aUniqCol.size() <= binNumber) {

          ArrayList newUniqCol = new ArrayList();

          for (int listIdx=0; listIdx<aUniqCol.size()-1; listIdx++) {

            double tmpVal = (Double.parseDouble(aUniqCol.get(listIdx).toString()) +

                             Double.parseDouble(aUniqCol.get(listIdx+1).toString()))/2;

            newUniqCol.add(Double.toString(tmpVal));

          }

          uniqValues[colIdx] = newUniqCol;

        }

        // if the uniq value > binNumber, calculate the split points

        else {

          ArrayList newUniqCol = new ArrayList();

          // find min value and max value

          double min = Double.parseDouble(aUniqCol.get(0).toString());

          double max = Double.parseDouble(aUniqCol.get(aUniqCol.size()-1).toString());

          // rebuild the ArrayList to keep spliting values for specified number of bins

          double interval = (max - min)/binNumber;

          for (int valueIdx=1; valueIdx<binNumber; valueIdx++) {

            double tmpval = min+(interval*valueIdx);

            newUniqCol.add(Double.toString(tmpval));

          }

          uniqValues[colIdx] = newUniqCol;

        }

      } // numeric column

    }

    return uniqValues;

  }



  /** sort the unique value list

   *  @param al the original ArrayList

   *  @return an sorted ArrayList

   */

  protected ArrayList sortUniqValues(ArrayList al) {

    for (int i=0; i<al.size(); i++) {

      for (int j=al.size()-1; j>i; j--) {

        if (Double.parseDouble(al.get(j-1).toString()) >

            Double.parseDouble(al.get(j).toString())) {

            Object tmpObj = al.get(j-1);

            al.set(j-1, al.get(j));

            al.set(j, tmpObj);

        }

      }

    }

    return al;

  }



  /** Compare the dominateRatio (mostCommon tally / secondMost tally) between

   *  parent node and the current node, return true if the difference > improvementRatio

   *  @param parentClassTallies the tallies of the parent node

   *  @param currClassTallies the tallies of the current node

   *  @return true if the difference > improvementRatio, otherwise false.

   */

  private boolean isRatioUp(int[] parentClassTallies, int[] currClassTallies) {

    int mostCommonVal = 0;

    int secondMostCommonVal = 0;

    double parentRatio = 0.00;

    double currRatio = 0.00;

    for (int classIdx = 0; classIdx < parentClassTallies.length; classIdx++) {

      if (parentClassTallies[classIdx] > mostCommonVal) {

        secondMostCommonVal = mostCommonVal;

        mostCommonVal = parentClassTallies[classIdx];

      }

      else if (parentClassTallies[classIdx] > secondMostCommonVal) {

        secondMostCommonVal = parentClassTallies[classIdx];

      }

    }

    parentRatio = (double)mostCommonVal/(double)secondMostCommonVal;



    mostCommonVal = 0;

    secondMostCommonVal = 0;

    for (int classIdx = 0; classIdx < currClassTallies.length; classIdx++) {

      if (currClassTallies[classIdx] > mostCommonVal) {

        secondMostCommonVal = mostCommonVal;

        mostCommonVal = currClassTallies[classIdx];

      }

      else if (currClassTallies[classIdx] > secondMostCommonVal) {

        secondMostCommonVal = currClassTallies[classIdx];

      }

    }

    currRatio = (double)mostCommonVal/(double)secondMostCommonVal;



    if (Math.abs(parentRatio - currRatio) > improvementRatio)

      return true;

    else

      return false;

  }



  private void printTable(MutableTableImpl table) {

    System.out.println("data table: ");

    for (int rowIdx=0; rowIdx<table.getNumRows(); rowIdx++) {

      for (int colIdx=0; colIdx<table.getNumColumns(); colIdx++) {

        System.out.print(table.getObject(rowIdx, colIdx) + ", ");

      }

      System.out.println(" ");

    }

  }



  private void printArrayList(ArrayList al) {

    if (al.size()==0) {

      System.out.println("No value in ArrayList");

    }

    else {

      System.out.println("ArrayList is: ");

      for (int i=0; i<al.size(); i++) {

        System.out.print(al.get(i) + ",");

      }

    }

    System.out.println();

  }



  private void printStringArray(String[] sa) {

    if (sa.length==0) {

      System.out.println("No value in Array");

    }

    else {

      System.out.println("String Array is: ");

      for (int i=0; i<sa.length; i++) {

        System.out.print(sa[i] + ",");

      }

    }

    System.out.println();

  }



  private void printIntArray(int[] intArray) {

    if (intArray.length==0) {

      System.out.println("No value in int Array");

    }

    else {

      System.out.println("int array is: ");

      for (int i=0; i<intArray.length; i++) {

        System.out.print(intArray[i] + ",");

      }

    }

    System.out.println();

  }



  private void printAvcSets(ArrayList[] avcs) {

    System.out.println("avcs.length is " + avcs.length);

    for (int i=0; i<avcs.length; i++) {

      ArrayList avc = avcs[i];

      if (avc.size()==0) {

        System.out.println("No value in ArrayList");

      }

      else {

        System.out.println("avcSets is: ");

        for (int j=0; j<avc.size(); j++) {

          AvcSet aSet = (AvcSet) avc.get(j);

          System.out.println("avcSets[" + j + "]:");

          System.out.print("  attrName - " + aSet.attrName);

          System.out.print("  lowValue - " + aSet.lowValue);

          System.out.print("  highValue - " + aSet.highValue);

          System.out.print("  classLabel - " + aSet.classLabel);

          System.out.print("  count - " + aSet.count);

          System.out.println(" ");

        }

      }

    }

    System.out.println();

  }



}
