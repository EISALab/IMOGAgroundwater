package ncsa.d2k.modules.core.datatype.table.sparse;

//import ncsa.d2k.modules.projects.vered.sparse.primitivehash.*;
//import ncsa.d2k.modules.projects.vered.sparse.column.*;
//import ncsa.d2k.modules.projects.vered.sparse.example.SparsePredictionExample;
import ncsa.d2k.modules.core.datatype.table.sparse.primitivehash.*;
import ncsa.d2k.modules.core.datatype.table.sparse.columns.*;
import ncsa.d2k.modules.core.datatype.table.sparse.examples.
    SparsePredictionExample;
import ncsa.d2k.modules.core.datatype.table.sparse.examples.
    SparseShallowPredictionExample;
import ncsa.d2k.modules.core.datatype.table.Example;
import java.io.*;
import java.util.Arrays;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.Table;
import ncsa.d2k.modules.core.datatype.table.basic.Column;

/**
 * SparsePredictionTable is an ExampleTable with additional Columns, prediction
     * Columns, s.t. for each output Column there is a prediction Column of the same
 * type and size.
 *
 * The prediction Columns' internal representation is via a SparseMutableTable,
 * since a SparseExmapleTable is not mutable and the prediction columns are
 * needed to be mutated by their nature.
 *
 * To the end user, the outside world the prediction Columns are an integral
 * part of this table. The indices of the prediction Columns, as the outside
 * world sees them are defined by <code>predictions</code>.
 *
 */

public class SparsePredictionTable
    extends SparseExampleTable
    implements PredictionTable {

      /*int set - holds indices of prediction columns as presented to the end user*/
  protected int[] predictions;

  protected int numPredictions;

  /* a mutable table will hold the prediction columns */
  protected SparseMutableTable predictionColumns;

//	// XIAOLEI - temp fix
//	public String getColumnLabel(int position)
//	{
//		if (columns.containsKey(position))
//			return ((Column)columns.get(position)).getLabel();
//		else if (predictionColumns.getColumnLabel(position - super.getNumColumns()) != null) {
//			return predictionColumns.getColumnLabel(position - super.getNumColumns());
//		}
//		else
//			return null;
//	}

  /**
   * *********************************************************
   * CONSTRUCTORS
   * *********************************************************
   */

  /* Creates an empty table */
  public SparsePredictionTable() {
    super();
    predictions = new int[0];
    numPredictions = 0;
    predictionColumns = this;//new SparseMutableTable();
  }

  /**
    Given an example table, copy its attributes (columns, rows, input
    output training and testing sets). then for each output column creating
    a new prediction column of the same type and size.
   @param table the ExampleTable that contains the initial values
   */
  public SparsePredictionTable(SparseExampleTable table) {

    //copying the columns and rows of the example part.
    super(table);

    //if table is just an exmaple table and not a prediction table
    //create prediction columns.
    if (! (table instanceof SparsePredictionTable)) {

      //instantiating the prediction part.
      predictionColumns = this;

      int numPredictions = table.getNumOutputFeatures();
      int numcols = table.getNumColumns();

      //instantiating the prediction set to the correct size.
      predictions = new int[numPredictions];

      //retrieving the number of columns in the example part.
 //	int numExampleCols = table.getNumColumns();

      //newColIndex is the index for the new prediction column - as presented
      // to the outside world - the natural continueing of the ExampleTable.
 //	int newColIndex = numExampleCols;

      //retrieving the indices of output columns
 //	int[] outputCols = table.getOutputFeatures();

      //for each output column
      for (int i = 0; i < numPredictions; i++) {

        //add the new column index to the prediction set.
        predictions[i] = numcols + i;
        //get the type of the output column.
        int type = getColumnType(outputColumns[i]);
        //get its size.
        int size = getColumnNumEntries(outputColumns[i]);
        //add a prediction column of the same type and size.
        addColumn(numColumns + i, type, size);

        //XIAOLEI
        //System.out.println("Setting column label " + (numcols + i) + " to: " + getColumnLabel(outputColumns[i]) + PredictionTable.PREDICTION_COLUMN_APPEND_TEXT);
        setColumnLabel(getColumnLabel(outputColumns[i]) + PredictionTable.PREDICTION_COLUMN_APPEND_TEXT, numcols + i);

      } //for

//      //instantiating the prediction part.
//      predictionColumns = new SparseMutableTable();
//
//      int numPredictions = table.getNumOutputFeatures();
//
//      //instantiating the prediction set to the correct size.
//      predictions = new int[numPredictions];
//
//      //retrieving the number of columns in the example part.
// //	int numExampleCols = table.getNumColumns();
//
//      //newColIndex is the index for the new prediction column - as presented
//      // to the outside world - the natural continueing of the ExampleTable.
// //	int newColIndex = numExampleCols;
//
//      //retrieving the indices of output columns
// //	int[] outputCols = table.getOutputFeatures();
//
//      //for each output column
//      for (int i = 0; i < numPredictions && i < outputColumns.length; i++) {
//
//        //add the new column index to the prediction set.
//        predictions[i] = numColumns + i;
//        //get the type of the output column.
//        int type = table.getColumnType(outputColumns[i]);
//        //get its size.
//        int size = table.getColumnNumEntries(outputColumns[i]);
//        //add a prediction column of the same type and size.
//        predictionColumns.addColumn(i, type, size);
//
//		//XIAOLEI
//		predictionColumns.setColumnLabel(table.getColumnLabel(i) +
//				PredictionTable.PREDICTION_COLUMN_APPEND_TEXT, i);
//
//      } //for

    } //if !instanceof

    else {
      predictions = ( (SparsePredictionTable) table).predictions;
      predictionColumns = this;//new SparseMutableTable( ( (SparsePredictionTable)table).predictionColumns);
      numPredictions = predictions.length;
    }

  } //end ctor

  protected SparsePredictionTable(SparseExampleTable table, boolean flag) {
    super(table);
  }

  /**
   * copies all attributes from <code>t</code>
   *
   * @param t     a table of type SparseTable, to have its items deeply copied
   *              to this table
   *
   */
  public void copy(SparseTable t) {
    if (t instanceof SparsePredictionTable) {
      predictions = copyArray( ( (SparsePredictionTable) t).predictions);
      //predictionColumns = (SparseMutableTable) ( (SparsePredictionTable) t).predictionColumns.copy();
    }
    super.copy(t);
  }

  /**
   * ****************************************************************
   * GETTER AND SETTER FOR PREDICTIONS
   * ****************************************************************
   */

  /**
   * Returns the indices of columns that are prediction columns
   */
  public int[] getPredictionSet() {
    return predictions;
  }

  /**
       * Returns the indices of the prediction columns in row no. <code>index</code>
   * as represented internally.
   */
  public int[] getRowPredictions(int index) {
    /*
        int[] inside = predictionColumns.getRowIndices(index);
        int[] retVal = new int[inside.length];
        for (int i=0; i<inside.length; i++)
          retVal[i] = predictions.getAt(i);
        return retVal;
     */

    return this.getPredictionSet();// predictionColumns.getRowIndices(index);
  }

  /**
   * Sets <codE>predicions</code> to hold the item in <code>p</codE>
   *
   * @param p   an int array holding the indices of prediction columns.
   */
  public void setPredictionSet(int[] p) {
    predictions = p;
  }

  /**
   * ****************************************************************
   * SET TYPE METHODS, FOR PREDICTION COLUMNS.
   * ****************************************************************
   */

  /**
   * sets the value <codE>prediction</code> to row no. <codE>row</code>.
       * <codE>predictionColIdx</codE> is an index into <code>predictionColumns</code>
   * and not as seen externally of this table.
   *
   *
   * @param prediction    the value to be set.
   * @param row           the row number of the entry to be set
   * @param predictionColIdx    the index of the prediction column
   *                            in <code>predictionColumns</code>.
   */
  public void setIntPrediction(int prediction, int row, int predictionColIdx) {
    super.setInt(prediction, row, getPredictionSet()[predictionColIdx]);
  }

  /**
   * sets the value <codE>prediction</code> to row no. <codE>row</code>.
       * <codE>predictionColIdx</codE> is an index into <code>predictionColumns</code>
   * and not as seen externally of this table.
   *
   *
   * @param prediction    the value to be set.
   * @param row           the row number of the entry to be set
   * @param predictionColIdx    the index of the prediction column
   *                            in <code>predictionColumns</code>.
   */
  public void setFloatPrediction(float prediction, int row,
                                 int predictionColIdx) {
    super.setFloat(prediction, row, getPredictionSet()[predictionColIdx]);
  }

  /**
   * sets the value <codE>prediction</code> to row no. <codE>row</code>.
       * <codE>predictionColIdx</codE> is an index into <code>predictionColumns</code>
   * and not as seen externally of this table.
   *
   *
   * @param prediction    the value to be set.
   * @param row           the row number of the entry to be set
   * @param predictionColIdx    the index of the prediction column
   *                            in <code>predictionColumns</code>.
   */
  public void setDoublePrediction(double prediction, int row,
                                  int predictionColIdx) {
    super.setDouble(prediction, row, getPredictionSet()[predictionColIdx]);
  }

  /**
   * sets the value <codE>prediction</code> to row no. <codE>row</code>.
       * <codE>predictionColIdx</codE> is an index into <code>predictionColumns</code>
   * and not as seen externally of this table.
   *
   *
   * @param prediction    the value to be set.
   * @param row           the row number of the entry to be set
   * @param predictionColIdx    the index of the prediction column
   *                            in <code>predictionColumns</code>.
   */
  public void setLongPrediction(long prediction, int row, int predictionColIdx) {
    super.setLong(prediction, row, getPredictionSet()[predictionColIdx]);
  }

  /**
   * sets the value <codE>prediction</code> to row no. <codE>row</code>.
       * <codE>predictionColIdx</codE> is an index into <code>predictionColumns</code>
   * and not as seen externally of this table.
   *
   *
   * @param prediction    the value to be set.
   * @param row           the row number of the entry to be set
   * @param predictionColIdx    the index of the prediction column
   *                            in <code>predictionColumns</code>.
   */
  public void setShortPrediction(short prediction, int row,
                                 int predictionColIdx) {
    super.setShort(prediction, row, predictionColIdx);
  }

  /**
   * sets the value <codE>prediction</code> to row no. <codE>row</code>.
       * <codE>predictionColIdx</codE> is an index into <code>predictionColumns</code>
   * and not as seen externally of this table.
   *
   *
   * @param prediction    the value to be set.
   * @param row           the row number of the entry to be set
   * @param predictionColIdx    the index of the prediction column
   *                            in <code>predictionColumns</code>.
   */
  public void setBooleanPrediction(boolean prediction, int row,
                                   int predictionColIdx) {
    super.setBoolean(prediction, row, getPredictionSet()[predictionColIdx]);
  }

  /**
   * sets the value <codE>prediction</code> to row no. <codE>row</code>.
       * <codE>predictionColIdx</codE> is an index into <code>predictionColumns</code>
   * and not as seen externally of this table.
   *
   *
   * @param prediction    the value to be set.
   * @param row           the row number of the entry to be set
   * @param predictionColIdx    the index of the prediction column
   *                            in <code>super</code>.
   */
  public void setStringPrediction(String prediction, int row,
                                  int predictionColIdx) {

    super.setString(prediction, row, getPredictionSet()[predictionColIdx]);
  }

  /**
   * sets the value <codE>prediction</code> to row no. <codE>row</code>.
       * <codE>predictionColIdx</codE> is an index into <code>predictionColumns</code>
   * and not as seen externally of this table.
   *
   *
   * @param prediction    the value to be set.
   * @param row           the row number of the entry to be set
   * @param predictionColIdx    the index of the prediction column
   *                            in <code>predictionColumns</code>.
   */
  public void setCharsPrediction(char[] prediction, int row,
                                 int predictionColIdx) {
    super.setChars(prediction, row, getPredictionSet()[predictionColIdx]);
  }

  /**
   * sets the value <codE>prediction</code> to row no. <codE>row</code>.
       * <codE>predictionColIdx</codE> is an index into <code>predictionColumns</code>
   * and not as seen externally of this table.
   *
   *
   * @param prediction    the value to be set.
   * @param row           the row number of the entry to be set
   * @param predictionColIdx    the index of the prediction column
   *                            in <code>predictionColumns</code>.
   */
  public void setBytesPrediction(byte[] prediction, int row,
                                 int predictionColIdx) {
    super.setBytes(prediction, row, getPredictionSet()[predictionColIdx]);
  }

  /**
   * sets the value <codE>prediction</code> to row no. <codE>row</code>.
       * <codE>predictionColIdx</codE> is an index into <code>predictionColumns</code>
   * and not as seen externally of this table.
   *
   *
   * @param prediction    the value to be set.
   * @param row           the row number of the entry to be set
   * @param predictionColIdx    the index of the prediction column
   *                            in <code>predictionColumns</code>.
   */
  public void setObjectPrediction(Object prediction, int row,
                                  int predictionColIdx) {
    super.setObject(prediction, row, getPredictionSet()[predictionColIdx]);
  }

  /**
   * sets the value <codE>prediction</code> to row no. <codE>row</code>.
       * <codE>predictionColIdx</codE> is an index into <code>predictionColumns</code>
   * and not as seen externally of this table.
   *
   *
   * @param prediction    the value to be set.
   * @param row           the row number of the entry to be set
   * @param predictionColIdx    the index of the prediction column
   *                            in <code>predictionColumns</code>.
   */
  public void setBytePrediction(byte prediction, int row, int predictionColIdx) {
    super.setByte(prediction, row, getPredictionSet()[predictionColIdx]);
  }

  /**
   * sets the value <codE>prediction</code> to row no. <codE>row</code>.
       * <codE>predictionColIdx</codE> is an index into <code>predictionColumns</code>
   * and not as seen externally of this table.
   *
   *
   * @param prediction    the value to be set.
   * @param row           the row number of the entry to be set
   * @param predictionColIdx    the index of the prediction column
   *                            in <code>predictionColumns</code>.
   */
  public void setCharPrediction(char prediction, int row, int predictionColIdx) {
    super.setChar(prediction, row, getPredictionSet()[predictionColIdx]);
  }

  /**
   * ****************************************************************
   * GET TYPE METHODS, FOR PREDICTION COLUMNS.
   * ****************************************************************
   */

  /**
   * Return an int representation of the data at row no. <code>row</code> and
       * column no. <code>predictionColIdx]</code> in <code>predictionColumns</codE>.
   *
   * @param row     the row number from which to retrieve the data
   * @param predictionColIdx    the index of the prediction column
   *                            in <code>predictionColumns</code>.
   * @return        an int representation of the data at the specified row
   *                and column.
   */
  public int getIntPrediction(int row, int predictionColIdx) {
    return super.getInt(row, getPredictionSet()[predictionColIdx]);
  }

  /**
   * Return a float representation of the data at row no. <code>row</code> and
       * column no. <code>predictionColIdx]</code> in <code>predictionColumns</codE>.
   *
   * @param row     the row number from which to retrieve the data
   * @param predictionColIdx    the index of the prediction column
   *                            in <code>predictionColumns</code>.
   * @return        a float representation of the data at the specified row
   *                and column.
   */
  public float getFloatPrediction(int row, int predictionColIdx) {
    return super.getFloat(row, getPredictionSet()[predictionColIdx]);
  }

  /**
   * Return a double representation of the data at row no. <code>row</code> and
       * column no. <code>predictionColIdx]</code> in <code>predictionColumns</codE>.
   *
   * @param row     the row number from which to retrieve the data
   * @param predictionColIdx    the index of the prediction column
   *                            in <code>predictionColumns</code>.
   * @return        a double representation of the data at the specified row
   *                and column.
   */
  public double getDoublePrediction(int row, int predictionColIdx) {
    return super.getDouble(row, getPredictionSet()[predictionColIdx]);
  }

  /**
   * Return a long representation of the data at row no. <code>row</code> and
       * column no. <code>predictionColIdx]</code> in <code>predictionColumns</codE>.
   *
   * @param row     the row number from which to retrieve the data
   * @param predictionColIdx    the index of the prediction column
   *                            in <code>predictionColumns</code>.
   * @return        a long representation of the data at the specified row
   *                and column.
   */
  public long getLongPrediction(int row, int predictionColIdx) {
    return super.getLong(row, getPredictionSet()[predictionColIdx]);
  }

  /**
   * Return a short representation of the data at row no. <code>row</code> and
       * column no. <code>predictionColIdx]</code> in <code>predictionColumns</codE>.
   *
   * @param row     the row number from which to retrieve the data
   * @param predictionColIdx    the index of the prediction column
   *                            in <code>predictionColumns</code>.
   * @return        a short representation of the data at the specified row
   *                and column.
   */
  public short getShortPrediction(int row, int predictionColIdx) {
    return super.getShort(row, getPredictionSet()[predictionColIdx]);
  }

  /**
       * Return a boolean representation of the data at row no. <code>row</code> and
       * column no. <code>predictionColIdx]</code> in <code>predictionColumns</codE>.
   *
   * @param row     the row number from which to retrieve the data
   * @param predictionColIdx    the index of the prediction column
   *                            in <code>predictionColumns</code>.
   * @return        a boolean representation of the data at the specified row
   *                and column.
   */
  public boolean getBooleanPrediction(int row, int predictionColIdx) {
    return super.getBoolean(row, getPredictionSet()[predictionColIdx]);
  }

  /**
   * Return a String representation of the data at row no. <code>row</code> and
       * column no. <code>predictionColIdx]</code> in <code>predictionColumns</codE>.
   *
   * @param row     the row number from which to retrieve the data
   * @param predictionColIdx    the index of the prediction column
   *                            in <code>predictionColumns</code>.
   * @return        a String representation of the data at the specified row
   *                and column.
   */
  public String getStringPrediction(int row, int predictionColIdx) {
    return super.getString(row, getPredictionSet()[predictionColIdx]);
  }

  /**
       * Return a char array representation of the data at row no. <code>row</code> and
       * column no. <code>predictionColIdx]</code> in <code>predictionColumns</codE>.
   *
   * @param row     the row number from which to retrieve the data
   * @param predictionColIdx    the index of the prediction column
   *                            in <code>predictionColumns</code>.
       * @return        a char array representation of the data at the specified row
   *                and column.
   */
  public char[] getCharsPrediction(int row, int predictionColIdx) {
    return super.getChars(row, getPredictionSet()[predictionColIdx]);
  }

  /**
       * Return a byte array representation of the data at row no. <code>row</code> and
       * column no. <code>predictionColIdx]</code> in <code>predictionColumns</codE>.
   *
   * @param row     the row number from which to retrieve the data
   * @param predictionColIdx    the index of the prediction column
   *                            in <code>predictionColumns</code>.
       * @return        a byte array representation of the data at the specified row
   *                and column.
   */
  public byte[] getBytesPrediction(int row, int predictionColIdx) {
    return super.getBytes(row, getPredictionSet()[predictionColIdx]);
  }

  /**
       * Return an Object representation of the data at row no. <code>row</code> and
       * column no. <code>predictionColIdx]</code> in <code>predictionColumns</codE>.
   *
   * @param row     the row number from which to retrieve the data
   * @param predictionColIdx    the index of the prediction column
   *                            in <code>predictionColumns</code>.
   * @return        an Object representation of the data at the specified row
   *                and column.
   */
  public Object getObjectPrediction(int row, int predictionColIdx) {
    return super.getObject(row, getPredictionSet()[predictionColIdx]);
  }

  /**
   * Return a byte representation of the data at row no. <code>row</code> and
       * column no. <code>predictionColIdx]</code> in <code>predictionColumns</codE>.
   *
   * @param row     the row number from which to retrieve the data
   * @param predictionColIdx    the index of the prediction column
   *                            in <code>predictionColumns</code>.
   * @return        a byte representation of the data at the specified row
   *                and column.
   */
  public byte getBytePrediction(int row, int predictionColIdx) {
    return super.getByte(row, getPredictionSet()[predictionColIdx]);
  }

  /**
   * Return a char representation of the data at row no. <code>row</code> and
       * column no. <code>predictionColIdx]</code> in <code>predictionColumns</codE>.
   *
   * @param row     the row number from which to retrieve the data
   * @param predictionColIdx    the index of the prediction column
   *                            in <code>predictionColumns</code>.
   * @return         a char  representation of the data at the specified row
   *                and column.
   */
  public char getCharPrediction(int row, int predictionColIdx) {
    return super.getChar(row, getPredictionSet()[predictionColIdx]);
  }

  /**
   * ****************************************************************
   * ADD PREDICTION COLUMN METHODS.
   * ****************************************************************
   */

//  /**
//   * Appends <code>newCol</code> to this table as a prediction column, and sets
//   * <codE>label</code> to be its label.
//   *
//   * @param newCol    the column to be inserted.
//   * @param lable     the label of the new column
//       * @return          the index of the added prediction column in <codE>prediction
//   *                  </code>
//   */
//  protected int addPredictionColumn(AbstractSparseColumn newCol, String label) {
//    newCol.setLabel(label);
//    int index = getNumColumns();
//    if (numPredictions >= predictions.length) {
//      int[] newArr = new int[numPredictions * 2];
//      System.arraycopy(predictions, 0, newArr, 0, predictions.length);
//    }
//    predictions[numPredictions] = index;
//    numPredictions++;
//
//    predictionColumns.setColumn(predictionColumns.getNumColumns(), newCol);
//    predictionColumns.numColumns++;
//
//    return index;
//  }
//
//  /**
//   * returns true if there is a value at row no. <codE>row</code> and column
//   * no. <code>col</code>
//   *
//   * @param row     the inpected row number
//   * @param col     the inspected column number, as shown to the outside world.
//   * @return        true if there is a value at the specified position
//   */
//  public boolean doesValueExist(int row, int col) {
//    //checking if the column is in the example portion of the table
//    if (col < super.getNumColumns()) {
//
//      //if so - activating the super does values exist method.
//      return super.doesValueExist(row, col);
//    }
//
//    else { //the column is one of the prediction columns
//
//      //translating the column index into the mutable table indices range.
//      return predictionColumns.doesValueExist(row, col - super.getNumColumns());
//    }
//  }

  public int getNumPredictions() {
    return numPredictions;
  }

//  public int getNumColumns() {
//    return numColumns + predictionColumns.numColumns;
//  }
//
//  /**
//       * Adds a SparseIntColumn as a prediction column to this table, populized with
//   * the data in <code>predictions</code>. The row indices in the added column
//   * will be the valid rows of the example portion of this table.
//   *
//   * Note:  since this is a Sparse Table it is recommended to use
//   * addPredictionColumn(int[], String, int[]) for more expressive and
//   * accurate results.
//   *
//   * @param predictions   ints values to be held by the new prediction column
//   * @param label         the label for the new prediction column.
//   * @return              the index of the added prediction column in <codE>
//   *                      predictions</code>.
//   */
//  public int addPredictionColumn(int[] predictions, String label) {
//    return addPredictionColumn(predictions, label, getAllRows());
//  }
//
//  /**
//       * Adds a SparseFloatColumn as a prediction column to this table, populized with
//   * the data in <cod>Epredictions</code>. The row indices in the added column
//   * will be the valid rows of the example portion of this table.
//   *
//   * Note:  since this is a Sparse Table it is recommended to use
//   * addPredictionColumn(float[], String, int[]) for more expressive and
//   * accurate results.
//   *
//   * @param predictions   ints values to be held by the new prediction column
//   * @param label         the label for the new prediction column.
//   * @return              the index of the added prediction column in <codE>
//   *                      predictions</code>.
//   */
//  public int addPredictionColumn(float[] predictions, String label) {
//    return addPredictionColumn(predictions, label, getAllRows());
//  }
//
//  /**
//       * Adds a SparseDoubleColumn as a prediction column to this table, populized with
//   * the data in <cod>Epredictions</code>. The row indices in the added column
//   * will be the valid rows of the example portion of this table.
//   *
//   * Note:  since this is a Sparse Table it is recommended to use
//   * addPredictionColumn(double[], String, int[]) for more expressive and
//   * accurate results.
//   *
//   * @param predictions   ints values to be held by the new prediction column
//   * @param label         the label for the new prediction column.
//   * @return              the index of the added prediction column in <codE>
//   *                      predictions</code>.
//   */
//  public int addPredictionColumn(double[] predictions, String label) {
//    return addPredictionColumn(predictions, label, getAllRows());
//  }
//
//  /**
//       * Adds a SparseLongColumn as a prediction column to this table, populized with
//   * the data in <cod>Epredictions</code>. The row indices in the added column
//   * will be the valid rows of the example portion of this table.
//   *
//   * Note:  since this is a Sparse Table it is recommended to use
//   * addPredictionColumn(long[], String, int[]) for more expressive and
//   * accurate results.
//   *
//   * @param predictions   ints values to be held by the new prediction column
//   * @param label         the label for the new prediction column.
//   * @return              the index of the added prediction column in <codE>
//   *                      predictions</code>.
//   */
//  public int addPredictionColumn(long[] predictions, String label) {
//    return addPredictionColumn(predictions, label, getAllRows());
//  }
//
//  /**
//       * Adds a SparseShortColumn as a prediction column to this table, populized with
//   * the data in <cod>Epredictions</code>. The row indices in the added column
//   * will be the valid rows of the example portion of this table.
//   *
//   * Note:  since this is a Sparse Table it is recommended to use
//   * addPredictionColumn(short[], String, int[]) for more expressive and
//   * accurate results.
//   *
//   * @param predictions   ints values to be held by the new prediction column
//   * @param label         the label for the new prediction column.
//   * @return              the index of the added prediction column in <codE>
//   *                      predictions</code>.
//   */
//  public int addPredictionColumn(short[] predictions, String label) {
//    return addPredictionColumn(predictions, label, getAllRows());
//  }
//
//  /**
//   * Adds a SparseBooleanColumn as a prediction column to this table, populized with
//   * the data in <cod>Epredictions</code>. The row indices in the added column
//   * will be the valid rows of the example portion of this table.
//   *
//   * Note:  since this is a Sparse Table it is recommended to use
//   * addPredictionColumn(boolean[], String, int[]) for more expressive and
//   * accurate results.
//   *
//   * @param predictions   ints values to be held by the new prediction column
//   * @param label         the label for the new prediction column.
//   * @return              the index of the added prediction column in <codE>
//   *                      predictions</code>.
//   */
//  public int addPredictionColumn(boolean[] predictions, String label) {
//    return addPredictionColumn(predictions, label, getAllRows());
//  }
//
//  /**
//       * Adds a SparseStringColumn as a prediction column to this table, populized with
//   * the data in <cod>Epredictions</code>. The row indices in the added column
//   * will be the valid rows of the example portion of this table.
//   *
//   * Note:  since this is a Sparse Table it is recommended to use
//   * addPredictionColumn(String[], String, int[]) for more expressive and
//   * accurate results.
//   *
//   * @param predictions   ints values to be held by the new prediction column
//   * @param label         the label for the new prediction column.
//   * @return              the index of the added prediction column in <codE>
//   *                      predictions</code>.
//   */
//  public int addPredictionColumn(String[] predictions, String label) {
//    return addPredictionColumn(predictions, label, getAllRows());
//  }
//
//  /**
//   * Adds a SparseCharArrayColumn as a prediction column to this table, populized with
//   * the data in <cod>Epredictions</code>. The row indices in the added column
//   * will be the valid rows of the example portion of this table.
//   *
//   * Note:  since this is a Sparse Table it is recommended to use
//   * addPredictionColumn(char[][], String, int[]) for more expressive and
//   * accurate results.
//   *
//   * @param predictions   ints values to be held by the new prediction column
//   * @param label         the label for the new prediction column.
//   * @return              the index of the added prediction column in <codE>
//   *                      predictions</code>.
//   */
//  public int addPredictionColumn(char[][] predictions, String label) {
//    return addPredictionColumn(predictions, label, getAllRows());
//  }
//
//  /**
//   * Adds a SparseByteArrayColumn as a prediction column to this table, populized with
//   * the data in <cod>Epredictions</code>. The row indices in the added column
//   * will be the valid rows of the example portion of this table.
//   *
//   * Note:  since this is a Sparse Table it is recommended to use
//   * addPredictionColumn(byte[][], String, int[]) for more expressive and
//   * accurate results.
//   *
//   * @param predictions   ints values to be held by the new prediction column
//   * @param label         the label for the new prediction column.
//   * @return              the index of the added prediction column in <codE>
//   *                      predictions</code>.
//   */
//  public int addPredictionColumn(byte[][] predictions, String label) {
//    return addPredictionColumn(predictions, label, getAllRows());
//  }
//
//  /**
//       * Adds a SparseObjectColumn as a prediction column to this table, populized with
//   * the data in <cod>Epredictions</code>. The row indices in the added column
//   * will be the valid rows of the example portion of this table.
//   *
//   * Note:  since this is a Sparse Table it is recommended to use
//   * addPredictionColumn(Object[], String, int[]) for more expressive and
//   * accurate results.
//   *
//   * @param predictions   ints values to be held by the new prediction column
//   * @param label         the label for the new prediction column.
//   * @return              the index of the added prediction column in <codE>
//   *                      predictions</code>.
//   */
//  public int addPredictionColumn(Object[] predictions, String label) {
//    return addPredictionColumn(predictions, label, getAllRows());
//  }
//
//  /**
//       * Adds a SparseByteColumn as a prediction column to this table, populized with
//   * the data in <cod>Epredictions</code>. The row indices in the added column
//   * will be the valid rows of the example portion of this table.
//   *
//   * Note:  since this is a Sparse Table it is recommended to use
//   * addPredictionColumn(byte[], String, int[]) for more expressive and
//   * accurate results.
//   *
//   * @param predictions   ints values to be held by the new prediction column
//   * @param label         the label for the new prediction column.
//   * @return              the index of the added prediction column in <codE>
//   *                      predictions</code>.
//   */
//  public int addPredictionColumn(byte[] predictions, String label) {
//    return addPredictionColumn(predictions, label, getAllRows());
//  }
//
//  /**
//       * Adds a SparseCharColumn as a prediction column to this table, populized with
//   * the data in <cod>Epredictions</code>. The row indices in the added column
//   * will be the valid rows of the example portion of this table.
//   *
//   * Note:  since this is a Sparse Table it is recommended to use
//   * addPredictionColumn(char[], String, int[]) for more expressive and
//   * accurate results.
//   *
//   * @param predictions   ints values to be held by the new prediction column
//   * @param label         the label for the new prediction column.
//   * @return              the index of the added prediction column in <codE>
//   *                      predictions</code>.
//   */
//  public int addPredictionColumn(char[] predictions, String label) {
//    return addPredictionColumn(predictions, label, getAllRows());
//  }

  /* Returns a reference to this table */
  public PredictionTable toPredictionTable() {
    return this;
  }

  /**
   * ********************************************************
   * ADD PREDICTION COLUMN METHODS FOR SPARSE COLUMNS
   * ********************************************************
   */

//  /**
//   * Adds a SparseIntColumn as a prediction column to this table. The data in
//   * <code>predictions</code> will be inserted into the new column at indices
//   * specified by <code>indices</code>. the new column will be labeled <code>
//   * label</coe>
//   *
//   *
//   * @param predictions   ints values to be held by the new prediction column
//   * @param label         the label for the new prediction column.
//   * @param indices       the row indices of the data of the new column.
//   *                      each item <codE>prediction[i]</code> will be put at
//   *                      row no. <code>indices[i]</codE>.
//   * @return              the index of the added prediction column in <codE>
//   *                      predictions</code>.
//   */
//  public int addPredictionColumn(int[] predictions, String label, int[] indices) {
//    return addPredictionColumn(new SparseIntColumn(predictions, indices), label);
//  }
//
//  /**
//   * Adds a SparseByteColumn as a prediction column to this table. The data in
//   * <code>predictions</code> will be inserted into the new column at indices
//   * specified by <code>indices</code>. the new column will be labeled <code>
//   * label</coe>
//   *
//   *
//   * @param predictions   ints values to be held by the new prediction column
//   * @param label         the label for the new prediction column.
//   * @param indices       the row indices of the data of the new column.
//   *                      each item <codE>prediction[i]</code> will be put at
//   *                      row no. <code>indices[i]</codE>.
//   * @return              the index of the added prediction column in <codE>
//   *                      predictions</code>.
//   */
//  public int addPredictionColumn(byte[] predictions, String label,
//                                 int[] indices) {
//    return addPredictionColumn(new SparseByteColumn(predictions, indices),
//                               label);
//  }
//
//  /**
//       * Adds a SparseBooleanColumn as a prediction column to this table. The data in
//   * <code>predictions</code> will be inserted into the new column at indices
//   * specified by <code>indices</code>. the new column will be labeled <code>
//   * label</coe>
//   *
//   *
//   * @param predictions   ints values to be held by the new prediction column
//   * @param label         the label for the new prediction column.
//   * @param indices       the row indices of the data of the new column.
//   *                      each item <codE>prediction[i]</code> will be put at
//   *                      row no. <code>indices[i]</codE>.
//   * @return              the index of the added prediction column in <codE>
//   *                      predictions</code>.
//   */
//  public int addPredictionColumn(boolean[] predictions, String label,
//                                 int[] indices) {
//    return addPredictionColumn(new SparseBooleanColumn(predictions, indices),
//                               label);
//  }
//
//  /**
//       * Adds a SparseDoubleColumn as a prediction column to this table. The data in
//   * <code>predictions</code> will be inserted into the new column at indices
//   * specified by <code>indices</code>. the new column will be labeled <code>
//   * label</coe>
//   *
//   *
//   * @param predictions   ints values to be held by the new prediction column
//   * @param label         the label for the new prediction column.
//   * @param indices       the row indices of the data of the new column.
//   *                      each item <codE>prediction[i]</code> will be put at
//   *                      row no. <code>indices[i]</codE>.
//   * @return              the index of the added prediction column in <codE>
//   *                      predictions</code>.
//   */
//  public int addPredictionColumn(double[] predictions, String label,
//                                 int[] indices) {
//    return addPredictionColumn(new SparseDoubleColumn(predictions, indices),
//                               label);
//  }
//
//  /**
//   * Adds a SparseCharColumn as a prediction column to this table. The data in
//   * <code>predictions</code> will be inserted into the new column at indices
//   * specified by <code>indices</code>. the new column will be labeled <code>
//   * label</coe>
//   *
//   *
//   * @param predictions   ints values to be held by the new prediction column
//   * @param label         the label for the new prediction column.
//   * @param indices       the row indices of the data of the new column.
//   *                      each item <codE>prediction[i]</code> will be put at
//   *                      row no. <code>indices[i]</codE>.
//   * @return              the index of the added prediction column in <codE>
//   *                      predictions</code>.
//   */
//  public int addPredictionColumn(char[] predictions, String label,
//                                 int[] indices) {
//    return addPredictionColumn(new SparseCharColumn(predictions, indices),
//                               label);
//  }
//
//  /**
//       * Adds a SparseByteArrayColumn as a prediction column to this table. The data in
//   * <code>predictions</code> will be inserted into the new column at indices
//   * specified by <code>indices</code>. the new column will be labeled <code>
//   * label</coe>
//   *
//   *
//   * @param predictions   ints values to be held by the new prediction column
//   * @param label         the label for the new prediction column.
//   * @param indices       the row indices of the data of the new column.
//   *                      each item <codE>prediction[i]</code> will be put at
//   *                      row no. <code>indices[i]</codE>.
//   * @return              the index of the added prediction column in <codE>
//   *                      predictions</code>.
//   */
//  public int addPredictionColumn(byte[][] predictions, String label,
//                                 int[] indices) {
//    return addPredictionColumn(new SparseByteArrayColumn(predictions, indices),
//                               label);
//  }
//
//  /**
//   * Adds a SparseFloatColumn as a prediction column to this table. The data in
//   * <code>predictions</code> will be inserted into the new column at indices
//   * specified by <code>indices</code>. the new column will be labeled <code>
//   * label</coe>
//   *
//   *
//   * @param predictions   ints values to be held by the new prediction column
//   * @param label         the label for the new prediction column.
//   * @param indices       the row indices of the data of the new column.
//   *                      each item <codE>prediction[i]</code> will be put at
//   *                      row no. <code>indices[i]</codE>.
//   * @return              the index of the added prediction column in <codE>
//   *                      predictions</code>.
//   */
//  public int addPredictionColumn(float[] predictions, String label,
//                                 int[] indices) {
//    return addPredictionColumn(new SparseFloatColumn(predictions, indices),
//                               label);
//  }
//
//  /**
//       * Adds a SparseCharArrayColumn as a prediction column to this table. The data in
//   * <code>predictions</code> will be inserted into the new column at indices
//   * specified by <code>indices</code>. the new column will be labeled <code>
//   * label</coe>
//   *
//   *
//   * @param predictions   ints values to be held by the new prediction column
//   * @param label         the label for the new prediction column.
//   * @param indices       the row indices of the data of the new column.
//   *                      each item <codE>prediction[i]</code> will be put at
//   *                      row no. <code>indices[i]</codE>.
//   * @return              the index of the added prediction column in <codE>
//   *                      predictions</code>.
//   */
//  public int addPredictionColumn(char[][] predictions, String label,
//                                 int[] indices) {
//    return addPredictionColumn(new SparseCharArrayColumn(predictions, indices),
//                               label);
//  }
//
//  /**
//       * Adds a SparseObjectColumn as a prediction column to this table. The data in
//   * <code>predictions</code> will be inserted into the new column at indices
//   * specified by <code>indices</code>. the new column will be labeled <code>
//   * label</coe>
//   *
//   *
//   * @param predictions   ints values to be held by the new prediction column
//   * @param label         the label for the new prediction column.
//   * @param indices       the row indices of the data of the new column.
//   *                      each item <codE>prediction[i]</code> will be put at
//   *                      row no. <code>indices[i]</codE>.
//   * @return              the index of the added prediction column in <codE>
//   *                      predictions</code>.
//   */
//  public int addPredictionColumn(Object[] predictions, String label,
//                                 int[] indices) {
//    return addPredictionColumn(new SparseObjectColumn(predictions, indices),
//                               label);
//  }
//
//  /**
//       * Adds a SparseStringColumn as a prediction column to this table. The data in
//   * <code>predictions</code> will be inserted into the new column at indices
//   * specified by <code>indices</code>. the new column will be labeled <code>
//   * label</coe>
//   *
//   *
//   * @param predictions   ints values to be held by the new prediction column
//   * @param label         the label for the new prediction column.
//   * @param indices       the row indices of the data of the new column.
//   *                      each item <codE>prediction[i]</code> will be put at
//   *                      row no. <code>indices[i]</codE>.
//   * @return              the index of the added prediction column in <codE>
//   *                      predictions</code>.
//   */
//  public int addPredictionColumn(String[] predictions, String label,
//                                 int[] indices) {
//    return addPredictionColumn(new SparseStringColumn(predictions, indices),
//                               label);
//  }
//
//  /**
//   * Adds a SparseShortColumn as a prediction column to this table. The data in
//   * <code>predictions</code> will be inserted into the new column at indices
//   * specified by <code>indices</code>. the new column will be labeled <code>
//   * label</coe>
//   *
//   *
//   * @param predictions   ints values to be held by the new prediction column
//   * @param label         the label for the new prediction column.
//   * @param indices       the row indices of the data of the new column.
//   *                      each item <codE>prediction[i]</code> will be put at
//   *                      row no. <code>indices[i]</codE>.
//   * @return              the index of the added prediction column in <codE>
//   *                      predictions</code>.
//   */
//  public int addPredictionColumn(short[] predictions, String label,
//                                 int[] indices) {
//    return addPredictionColumn(new SparseShortColumn(predictions, indices),
//                               label);
//  }
//
//  /**
//   * Adds a SparseLongColumn as a prediction column to this table. The data in
//   * <code>predictions</code> will be inserted into the new column at indices
//   * specified by <code>indices</code>. the new column will be labeled <code>
//   * label</coe>
//   *
//   *
//   * @param predictions   ints values to be held by the new prediction column
//   * @param label         the label for the new prediction column.
//   * @param indices       the row indices of the data of the new column.
//   *                      each item <codE>prediction[i]</code> will be put at
//   *                      row no. <code>indices[i]</codE>.
//   * @return              the index of the added prediction column in <codE>
//   *                      predictions</code>.
//   */
//  public int addPredictionColumn(long[] predictions, String label,
//                                 int[] indices) {
//    return addPredictionColumn(new SparseLongColumn(predictions, indices),
//                               label);
//  }

  /**
       * Returns a SparsePredictionTable containing rows no. <code>start</code> through
   * </codE>start+len</code> from this table.
   *
   * @param start     row number at which the subset starts.
   * @param len       number of consequentinve rows in the retrieved subset.
   * @return          SparsePredictionTable with data from rows no. <code>start</code>
   *                  through </codE>start+len</code> from this table.
   /
     public Table getSubset(int start, int len) {
    //getting a subset of the example part.
    SparseExampleTable temp = (SparseExampleTable)super.getSubset(start, len);
    //creating a prediction table from the example subset.
    SparsePredictionTable retVal = new SparsePredictionTable(temp);
    //getting a subset from the prediction part
       retVal.predictionColumns = (SparseMutableTable) predictionColumns.getSubset(
        start, len);
    //copying the prediction indices.
    retVal.predictions = copyArray(predictions);
    retVal.numPredictions = numPredictions;
    return retVal;
     }*/

  public Table getSubset(int pos, int len) {
    //Table t = super.getSubset(pos, len);
    //ExampleTable et  = t.toExampleTable();
    SparseExampleTable temp = (SparseExampleTable)super.getSubset(pos, len);

     //PredictionTableImpl pt = new PredictionTableImpl(et.getNumColumns());
    SparsePredictionTable retVal = new SparsePredictionTable(temp, true);

    //retVal.setPredictionSet(newpred);
    retVal.predictionColumns = retVal;//(SparseMutableTable) predictionColumns.getSubset(pos, len);
    //copying the prediction indices.
    retVal.predictions = copyArray(predictions);
    retVal.numPredictions = numPredictions;
    //retVal.columns = ((SparseExampleTable)temp).columns;
    //pt.setInputFeatures(newin);
    //pt.setOutputFeatures(newout);
    //pt.setTrainingSet(newtrain);
    //pt.setTestingSet(newtest);
    return retVal;
  }

  public Table getSubset(int[] rows) {
    //Table t = super.getSubset(pos, len);
    //ExampleTable et  = t.toExampleTable();
    SparseExampleTable temp = (SparseExampleTable)super.getSubset(rows);

     //PredictionTableImpl pt = new PredictionTableImpl(et.getNumColumns());
    SparsePredictionTable retVal = new SparsePredictionTable(temp, true);

    //retVal.setPredictionSet(newpred);
    retVal.predictionColumns = this;//(SparseMutableTable) predictionColumns.getSubset(rows);
    //copying the prediction indices.
    retVal.predictions = copyArray(predictions);
    retVal.numPredictions = numPredictions;
    //retVal.columns = ((SparseExampleTable)temp).columns;
    //pt.setInputFeatures(newin);
    //pt.setOutputFeatures(newout);
    //pt.setTrainingSet(newtrain);
    //pt.setTestingSet(newtest);
    return retVal;
  }

  public Table getSubsetByReference(int pos, int len) {
//Table t = super.getSubset(pos, len);
//ExampleTable et  = t.toExampleTable();
    SparseExampleTable temp = (SparseExampleTable)super.getSubsetByReference(pos, len);

//PredictionTableImpl pt = new PredictionTableImpl(et.getNumColumns());
    SparsePredictionTable retVal = new SparsePredictionTable(temp, true);

//retVal.setPredictionSet(newpred);
    retVal.predictionColumns = this;//(SparseMutableTable) predictionColumns.getSubsetByReference(pos, len);
//copying the prediction indices.
    retVal.predictions = copyArray(predictions);
    retVal.numPredictions = numPredictions;
//retVal.columns = ((SparseExampleTable)temp).columns;
//pt.setInputFeatures(newin);
//pt.setOutputFeatures(newout);
//pt.setTrainingSet(newtrain);
//pt.setTestingSet(newtest);
    return retVal;
  }

  public Table getSubsetByReference(int[] rows) {
    SparseExampleTable temp = (SparseExampleTable)super.getSubsetByReference(
        rows);

//PredictionTableImpl pt = new PredictionTableImpl(et.getNumColumns());
    SparsePredictionTable retVal = new SparsePredictionTable(temp, true);

//retVal.setPredictionSet(newpred);
    retVal.predictionColumns = this;//(SparseMutableTable) predictionColumns.getSubsetByReference(rows);
//copying the prediction indices.
    retVal.predictions = copyArray(predictions);
    retVal.numPredictions = numPredictions;
    return retVal;

  }

  /**
   * ***************************************************************
   * GET TYPE METHODS
   * ***************************************************************
   */

//  /**
//   * Get a boolean value from the table.
//   * @param row the row of the table
//   * @param column the column of the table
//   * @return the boolean value at (row, column)
//   */
//  public boolean getBoolean(int row, int column) {
//    if (column < super.getNumColumns()) {
//      return super.getBoolean(row, column);
//    }
//    else {
//      return predictionColumns.getBoolean(row, column - super.getNumColumns());
//    }
//
//  }
//
//  /**
//   * Get a byte value from the table.
//   * @param row the row of the table
//   * @param column the column of the table
//   * @return the byte value at (row, column)
//   */
//  public byte getByte(int row, int column) {
//    if (column < super.getNumColumns()) {
//      return super.getByte(row, column);
//    }
//    else {
//      return predictionColumns.getByte(row, column - super.getNumColumns());
//    }
//
//  }
//
//  /**
//   * Get a char value from the table.
//   * @param row the row of the table
//   * @param column the column of the table
//   * @return the char value at (row, column)
//   */
//  public char getChar(int row, int column) {
//    if (column < super.getNumColumns()) {
//      return super.getChar(row, column);
//    }
//    else {
//      return predictionColumns.getChar(row, column - super.getNumColumns());
//    }
//
//  }
//
//  /**
//   * Get a byte array value from the table.
//   * @param row the row of the table
//   * @param column the column of the table
//   * @return the byte array value at (row, column)
//   */
//  public byte[] getBytes(int row, int column) {
//    if (column < super.getNumColumns()) {
//      return super.getBytes(row, column);
//    }
//    else {
//      return predictionColumns.getBytes(row, column - super.getNumColumns());
//    }
//
//  }
//
//  /**
//   * Get a char array value from the table.
//   * @param row the row of the table
//   * @param column the column of the table
//   * @return the char array  value at (row, column)
//   */
//  public char[] getChars(int row, int column) {
//    if (column < super.getNumColumns()) {
//      return super.getChars(row, column);
//    }
//    else {
//      return predictionColumns.getChars(row, column - super.getNumColumns());
//    }
//
//  }
//
//  /**
//   * Get a float value from the table.
//   * @param row the row of the table
//   * @param column the column of the table
//   * @return the float value at (row, column)
//   */
//  public float getFloat(int row, int column) {
//    if (column < super.getNumColumns()) {
//      return super.getFloat(row, column);
//    }
//    else {
//      return predictionColumns.getFloat(row, column - super.getNumColumns());
//    }
//
//  }
//
//  /**
//   * Get an int value from the table.
//   * @param row the row of the table
//   * @param column the column of the table
//   * @return the int value at (row, column)
//   */
//  public int getInt(int row, int column) {
//    if (column < super.getNumColumns()) {
//      return super.getInt(row, column);
//    }
//    else {
//      return predictionColumns.getInt(row, column - super.getNumColumns());
//    }
//
//  }
//
//  /**
//   * Get a double value from the table.
//   * @param row the row of the table
//   * @param column the column of the table
//   * @return the double value at (row, column)
//   */
//  public double getDouble(int row, int column) {
//    if (column < super.getNumColumns()) {
//      return super.getDouble(row, column);
//    }
//    else {
//      return predictionColumns.getDouble(row, column - super.getNumColumns());
//    }
//
//  }
//
//  /**
//   * Get a String value from the table.
//   * @param row the row of the table
//   * @param column the column of the table
//   * @return the String value at (row, column)
//   */
//  public String getString(int row, int column) {
//    if (column < super.getNumColumns()) {
//      return super.getString(row, column);
//    }
//    else {
//      return predictionColumns.getString(row, column - super.getNumColumns());
//    }
//
//  }
//
//  /**
//   * Get a Object value from the table.
//   * @param row the row of the table
//   * @param column the column of the table
//   * @return the Object value at (row, column)
//   */
//  public Object getObject(int row, int column) {
//    if (column < super.getNumColumns()) {
//      return super.getObject(row, column);
//    }
//    else {
//      return predictionColumns.getObject(row, column - super.getNumColumns());
//    }
//
//  }
//
//  /**
//   * Get a short value from the table.
//   * @param row the row of the table
//   * @param column the column of the table
//   * @return the short value at (row, column)
//   */
//  public short getShort(int row, int column) {
//    if (column < super.getNumColumns()) {
//      return super.getShort(row, column);
//    }
//    else {
//      return predictionColumns.getShort(row, column - super.getNumColumns());
//    }
//
//  }
//
//  /**
//   * Get a long value from the table.
//   * @param row the row of the table
//   * @param column the column of the table
//   * @return the long value at (row, column)
//   */
//  public long getLong(int row, int column) {
//    if (column < super.getNumColumns()) {
//      return super.getLong(row, column);
//    }
//    else {
//      return predictionColumns.getLong(row, column - super.getNumColumns());
//    }
//
//  }

  /**
       * Copies the values at row no. <codE>position</code> into <codE>buffer<c/doe>.
   * <code>buffer</cdoe> should be an array of some type. the values will be
       * converted, as neede, into the type of <code>buffeR</code>. The valid indices
   * of row no. <code>position</code> will be copied into <code>columnNumbers</code>.
   * each value at row no. <code>position</codE> and column no. <codE>columnNumbers[i]
       * </codE> will be copied into <codE>buffer[i]</code>. values from the prediction
   * portion of this table will be inserted into indices as represented to the
   * outside world.
   *
   * To avoid throwing of array index out of bound exceptions in this method -
   * <codE>columnNumbers</codE> and <code>buffer</code> should be at the size
   * of total entries at row no. <codE>position</code>.
   *
   * @param buffer    an array of some type into which the data is retrieved.
   * @param position  the row number its data is retrieved.
       * @param columnNumbers   will hold the corresponding column number of the values
   *                        that are stored in <code>buffeR</code>.
   */
//  public void getRow(Object buffer, int position, int[] columnNumbers) {
//    if (!rows.containsKey(position)) {
//      return;
//    }
//
//    //getting the example portion of the table.
//    super.getRow(buffer, position, columnNumbers);
//
//    columnNumbers = getRowIndices(position);
//
//  }
//
  /**
       * Copies the content of row mo. <code>position</codE> into <code>buffer</code>.
   * <code>buffer</codE> is an array of some type. the data will be converted
   * into that type as needed.
   *
       * Since this is a SparseTable, data that will be inserted into <code>buffer[i]
   * </codE> is not necessarily residing at column no. <code>i</codE> in this
   * table. It is recommended to use getRow(Object, int, int[]) for more
   * accurate results.
   *
   * @param buffer     an array of some type into which the data is copied.
   * @param position   the row number which its data is retrieved.
   */
//  public void getRow(Object buffer, int position) {
//    if (!rows.containsKey(position)) {
//      return;
//    }
//
//    super.getRow(buffer, position);
//    int numExampleEntries = super.getRowNumEntries(position);
//
//    if (buffer instanceof boolean[]) {
//      int size = ( (boolean[]) buffer).length - numExampleEntries;
//      if (size <= 0) {
//        return;
//      }
//      boolean[] predBuff = new boolean[size];
//      predictionColumns.getRow(predBuff, position);
//      System.arraycopy(predBuff, 0, buffer, numExampleEntries, size);
//      return;
//    }
//
//    if (buffer instanceof byte[]) {
//      int size = ( (byte[]) buffer).length - numExampleEntries;
//      if (size <= 0) {
//        return;
//      }
//      byte[] predBuff = new byte[size];
//      predictionColumns.getRow(predBuff, position);
//      System.arraycopy(predBuff, 0, buffer, numExampleEntries, size);
//      return;
//    }
//
//    if (buffer instanceof char[]) {
//      int size = ( (char[]) buffer).length - numExampleEntries;
//      if (size <= 0) {
//        return;
//      }
//      char[] predBuff = new char[size];
//      predictionColumns.getRow(predBuff, position);
//      System.arraycopy(predBuff, 0, buffer, numExampleEntries, size);
//      return;
//    }
//
//    if (buffer instanceof double[]) {
//      int size = ( (double[]) buffer).length - numExampleEntries;
//      if (size <= 0) {
//        return;
//      }
//      double[] predBuff = new double[size];
//      predictionColumns.getRow(predBuff, position);
//      System.arraycopy(predBuff, 0, buffer, numExampleEntries, size);
//      return;
//    }
//
//    if (buffer instanceof float[]) {
//      int size = ( (float[]) buffer).length - numExampleEntries;
//      if (size <= 0) {
//        return;
//      }
//      float[] predBuff = new float[size];
//      predictionColumns.getRow(predBuff, position);
//      System.arraycopy(predBuff, 0, buffer, numExampleEntries, size);
//      return;
//    }
//
//    if (buffer instanceof byte[][]) {
//      int size = ( (byte[][]) buffer).length - numExampleEntries;
//      if (size <= 0) {
//        return;
//      }
//      byte[][] predBuff = new byte[size][];
//      predictionColumns.getRow(predBuff, position);
//      System.arraycopy(predBuff, 0, buffer, numExampleEntries, size);
//      return;
//    }
//
//    if (buffer instanceof char[][]) {
//      int size = ( (char[][]) buffer).length - numExampleEntries;
//      if (size <= 0) {
//        return;
//      }
//      char[][] predBuff = new char[size][];
//      predictionColumns.getRow(predBuff, position);
//      System.arraycopy(predBuff, 0, buffer, numExampleEntries, size);
//      return;
//    }
//
//    if (buffer instanceof int[]) {
//      int size = ( (int[]) buffer).length - numExampleEntries;
//      if (size <= 0) {
//        return;
//      }
//      int[] predBuff = new int[size];
//      predictionColumns.getRow(predBuff, position);
//      System.arraycopy(predBuff, 0, buffer, numExampleEntries, size);
//      return;
//    }
//
//    if (buffer instanceof Object[]) {
//      int size = ( (Object[]) buffer).length - numExampleEntries;
//      if (size <= 0) {
//        return;
//      }
//      Object[] predBuff = new Object[size];
//      predictionColumns.getRow(predBuff, position);
//      System.arraycopy(predBuff, 0, buffer, numExampleEntries, size);
//      return;
//    }
//
//    if (buffer instanceof String[]) {
//      int size = ( (String[]) buffer).length - numExampleEntries;
//      if (size <= 0) {
//        return;
//      }
//      String[] predBuff = new String[size];
//      predictionColumns.getRow(predBuff, position);
//      System.arraycopy(predBuff, 0, buffer, numExampleEntries, size);
//      return;
//    }
//
//    if (buffer instanceof short[]) {
//      int size = ( (short[]) buffer).length - numExampleEntries;
//      if (size <= 0) {
//        return;
//      }
//      short[] predBuff = new short[size];
//      predictionColumns.getRow(predBuff, position);
//      System.arraycopy(predBuff, 0, buffer, numExampleEntries, size);
//      return;
//    }
//
//    if (buffer instanceof long[]) {
//      int size = ( (long[]) buffer).length - numExampleEntries;
//      if (size <= 0) {
//        return;
//      }
//      long[] predBuff = new long[size];
//      predictionColumns.getRow(predBuff, position);
//      System.arraycopy(predBuff, 0, buffer, numExampleEntries, size);
//      return;
//    }
//  } //getRow
//
  /**
   * Return the total number of entries in row no. <code>position</code>.
//   */
//  public int getRowNumEntries(int position) {
//    int retVal = super.getRowNumEntries(position);
//    retVal += predictionColumns.getRowNumEntries(position);
//    return retVal;
//  }
//
  /**
   * Returns the valid column numbers of row no. <code>position</code>.
   */
//  public int[] getRowIndices(int position) {
//    int[] retVal = new int[getRowNumEntries(position)];
//    int[] exampleIdx = super.getRowIndices(position);
//
//    System.arraycopy(exampleIdx, 0, retVal, 0, exampleIdx.length);
//    System.arraycopy(predictions, 0, retVal, exampleIdx.length,
//                     predictions.length);
//    return retVal;
//  }

//  public int getColumnNumEntries(int position) {
//    if (position < super.getNumColumns()) {
//      return super.getColumnNumEntries(position);
//    }
//    else {
//      return predictionColumns.getColumnNumEntries(position -
//          super.getNumColumns());
//    }
//  }

//  public int[] getColumnIndices(int position) {
//    if (position < super.getNumColumns()) {
//      return super.getColumnIndices(position);
//    }
//    else {
//      return predictionColumns.getColumnIndices(position - super.getNumColumns());
//    }
//  }

//  /**
//   * Returns a single row SparsePredictionTable, containing data from row
//   * no. <codE>i</code>.
//   */
//  public Example getExample(int i) {
//    return new SparsePredictionExample(this, i);
//  }
//
//  public Example getShallowExample(int i) {
//    return new SparseShallowPredictionExample(this, i);
//  }

} //SparsePredictionTable
