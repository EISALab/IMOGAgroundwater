package ncsa.d2k.modules.core.datatype.table.sparse;

//import ncsa.d2k.modules.projects.vered.sparse.primitivehash.VIntHashSet;
import ncsa.d2k.modules.core.datatype.table.sparse.primitivehash.VIntHashSet;

import ncsa.d2k.modules.core.datatype.table.TrainTable;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;
import ncsa.d2k.modules.core.datatype.table.TestTable;
import ncsa.d2k.modules.core.datatype.table.PredictionTable;
import ncsa.d2k.modules.core.datatype.table.Table;
//import ncsa.d2k.modules.projects.vered.sparse.example.SparseExample;
import ncsa.d2k.modules.core.datatype.table.sparse.examples.SparseExample;
import ncsa.d2k.modules.core.datatype.table.Example;
import ncsa.d2k.modules.core.datatype.table.TableFactory;
import java.util.*;

/**
 * SparseTrainTable is the same as SparsePredictionTable with limited access to
 * the train set rows only.
 */

public class SparseTrainTable extends SparseExampleTable implements TrainTable {

  public SparseTrainTable() {
    super();
  }


  public SparseTrainTable(SparseExampleTable table) {
    super(table);
  }


  public ExampleTable getExampleTable() {
    return new SparseExampleTable(this);
  }

  public Table getSubset(int pos, int len) {
    ExampleTable et = (ExampleTable)super.getSubset(pos, len);
//    ExampleTable et  = t.toExampleTable();

/*    int[] newin = new int[inputColumns.length];
    System.arraycopy(inputColumns, 0, newin, 0, inputColumns.length);
    int[] newout = new int[outputColumns.length];
    System.arraycopy(outputColumns, 0, newout, 0, outputColumns.length);

    et.setInputFeatures(newin);
    et.setOutputFeatures(newout);

    // now figure out the test and train sets
    int[] traincpy = new int[trainSet.length];
    System.arraycopy(trainSet, 0, traincpy, 0, trainSet.length);
    int[] testcpy = new int[testSet.length];
    System.arraycopy(testSet, 0, testcpy, 0, testSet.length);

    int[] newtrain = subsetTrainOrTest(traincpy, pos, len);
    System.out.println("NEW TRAIN: "+newtrain.length+" OLD: "+traincpy.length);
    int[] newtest = subsetTrainOrTest(testcpy, pos, len);

    et.setTrainingSet(newtrain);
    et.setTestingSet(newtest);
    */

    return et.getTrainTable();
  }

  public Table getSubset(int[] rows) {
    ExampleTable et = (ExampleTable)super.getSubset(rows);
//    ExampleTable et = t.toExampleTable();

/*    int[] newin = new int[inputColumns.length];
    System.arraycopy(inputColumns, 0, newin, 0, inputColumns.length);
    int[] newout = new int[outputColumns.length];
    System.arraycopy(outputColumns, 0, newout, 0, outputColumns.length);

    et.setInputFeatures(newin);
    et.setOutputFeatures(newout);

    // now figure out the test and train sets
    int[] traincpy = new int[trainSet.length];
    System.arraycopy(trainSet, 0, traincpy, 0, trainSet.length);
    int[] testcpy = new int[testSet.length];
    System.arraycopy(testSet, 0, testcpy, 0, testSet.length);

    int[] newtrain = subsetTrainOrTest(traincpy, rows);
    int[] newtest = subsetTrainOrTest(testcpy, rows);

    et.setTrainingSet(newtrain);
    et.setTestingSet(newtest);
    */

    return et.getTrainTable();
  }

  public Table getSubsetByReference(int pos, int len) {
    ExampleTable et = (ExampleTable)super.getSubsetByReference(pos, len);
    //ExampleTable et  = t.toExampleTable();

/*    int[] newin = new int[inputColumns.length];
    System.arraycopy(inputColumns, 0, newin, 0, inputColumns.length);
    int[] newout = new int[outputColumns.length];
    System.arraycopy(outputColumns, 0, newout, 0, outputColumns.length);

    et.setInputFeatures(newin);
    et.setOutputFeatures(newout);

    // now figure out the test and train sets
    int[] traincpy = new int[trainSet.length];
    System.arraycopy(trainSet, 0, traincpy, 0, trainSet.length);
    int[] testcpy = new int[testSet.length];
    System.arraycopy(testSet, 0, testcpy, 0, testSet.length);

    int[] newtrain = subsetTrainOrTest(traincpy, pos, len);
    int[] newtest = subsetTrainOrTest(testcpy, pos, len);

    et.setTrainingSet(newtrain);
    et.setTestingSet(newtest);
    */

    return et.getTrainTable();
  }

  public Table getSubsetByReference(int[] rows) {
    ExampleTable et = (ExampleTable)super.getSubsetByReference(rows);
/*    ExampleTable et = t.toExampleTable();
    int[] newin = new int[inputColumns.length];
    System.arraycopy(inputColumns, 0, newin, 0, inputColumns.length);
    int[] newout = new int[outputColumns.length];
    System.arraycopy(outputColumns, 0, newout, 0, outputColumns.length);

    et.setInputFeatures(newin);
    et.setOutputFeatures(newout);

    // now figure out the test and train sets
    int[] traincpy = new int[trainSet.length];
    System.arraycopy(trainSet, 0, traincpy, 0, trainSet.length);
    int[] testcpy = new int[testSet.length];
    System.arraycopy(testSet, 0, testcpy, 0, testSet.length);

    int[] newtrain = subsetTrainOrTest(traincpy, rows);
    int[] newtest = subsetTrainOrTest(testcpy, rows);

    et.setTrainingSet(newtrain);
    et.setTestingSet(newtest);
    */
    return et.getTrainTable();
  }


  /**
   * Returns an int representation of the data at row no. <codE>row</code> in the
   * train set and column no. <code>column</codE> .
   *
   * This method is the same as getInt(trainSet[row], column).
   *
   * @param row       the row index in the train set
   * @param column    the column index
   * @return          an int representation of the data at the specified position.
   */
  public int getInt(int row, int column) {
     return super.getInt(trainSet[row], column);
  }


  /**
   * Returns a short representation of the data at row no. <codE>row</code> in the
   * train set and column no. <code>column</codE> .
   *
   * This method is the same as getShort(trainSet[row], column).
   *
   * @param row       the row index in the train set
   * @param column    the column index
   * @return          a short representation of the data at the specified position.
   */
  public short getShort(int row, int column) {
     return super.getShort(trainSet[row], column);
  }


  /**
   * Returns a long representation of the data at row no. <codE>row</code> in the
   * train set and column no. <code>column</codE> .
   *
   * This method is the same as getLong(trainSet[row], column).
   *
   * @param row       the row index in the train set
   * @param column    the column index
   * @return          a long representation of the data at the specified position.
   */
  public long getLong(int row, int column) {
       return super.getLong(trainSet[row], column);
  }


  /**
   * Returns a float representation of the data at row no. <codE>row</code> in the
   * train set and column no. <code>column</codE> .
   *
   * This method is the same as getFloat(trainSet[row], column).
   *
   * @param row       the row index in the train set
   * @param column    the column index
   * @return          a float representation of the data at the specified position.
   */
  public float getFloat(int row, int column) {
       return super.getFloat(trainSet[row], column);
  }


  /**
   * Returns a double representation of the data at row no. <codE>row</code> in the
   * train set and column no. <code>column</codE> .
   *
   * This method is the same as getDouble(trainSet[row], column).
   *
   * @param row       the row index in the train set
   * @param column    the column index
   * @return          a double representation of the data at the specified position.
   */
  public double getDouble(int row, int column) {
       return super.getDouble(trainSet[row], column);
  }


  /**
   * Returns a String representation of the data at row no. <codE>row</code> in the
   * train set and column no. <code>column</codE> .
   *
   * This method is the same as getString(trainSet[row], column).
   *
   * @param row       the row index in the train set
   * @param column    the column index
   * @return          a String representation of the data at the specified position.
   */
  public String getString(int row, int column) {
       return super.getString(trainSet[row], column);
  }


  /**
   * Returns a byte array representation of the data at row no. <codE>row</code> in the
   * train set and column no. <code>column</codE> .
   *
   * This method is the same as getBytes(trainSet[row], column).
   *
   * @param row       the row index in the train set
   * @param column    the column index
   * @return          a byte array representation of the data at the specified position.
   */
  public byte[] getBytes(int row, int column) {
       return super.getBytes(trainSet[row], column);
  }


  /**
   * Returns a char array representation of the data at row no. <codE>row</code> in the
   * train set and column no. <code>column</codE> .
   *
   * This method is the same as getChars(trainSet[row], column).
   *
   * @param row       the row index in the train set
   * @param column    the column index
   * @return          a char array representation of the data at the specified position.
   */
  public char[] getChars(int row, int column) {
       return super.getChars(trainSet[row], column);
  }


  /**
   * Returns a boolean representation of the data at row no. <codE>row</code> in the
   * train set and column no. <code>column</codE> .
   *
   * This method is the same as getBoolean(trainSet[row], column).
   *
   * @param row       the row index in the train set
   * @param column    the column index
   * @return          a boolean representation of the data at the specified position.
   */
  public boolean getBoolean(int row, int column) {
       return super.getBoolean(trainSet[row], column);
  }


  /**
   * Returns a byte representation of the data at row no. <codE>row</code> in the
   * train set and column no. <code>column</codE> .
   *
   * This method is the same as getByte(trainSet[row], column).
   *
   * @param row       the row index in the train set
   * @param column    the column index
   * @return          a byte representation of the data at the specified position.
   */
  public byte getByte(int row, int column) {
     return super.getByte(trainSet[row], column);
  }


  /**
   * Returns a char representation of the data at row no. <codE>row</code> in the
   * train set and column no. <code>column</codE> .
   *
   * This method is the same as getChar(trainSet[row], column).
   *
   * @param row       the row index in the train set
   * @param column    the column index
   * @return          a char representation of the data at the specified position.
   */
  public char getChar(int row, int column) {
     return super.getChar(trainSet[row], column);
  }



  public int getNumRows() {
     return trainSet.length;
  }


  public int getNumExamples() {
     return trainSet.length;
  }

  /**
   * Returns an Object representation of the data at row no. <codE>row</code> in the
   * train set and column no. <code>column</codE> .
   *
   * This method is the same as getObject(trainSet[row], column).
   *
   * @param row       the row index in the train set
   * @param column    the column index
   * @return          an Object representation of the data at the specified position.
   */
  public Object getObject(int row, int column) {
    return super.getObject(trainSet[row], column);
  }


  public int getNumEntries() {

    int numEntries = 0;
    for (int i=0; i<trainSet.length; i++)
      numEntries += getRowNumEntries(trainSet[i]);

    return numEntries;
  }


/* Deprecated
  public void getRow(Object buffer, int position, int[] indices) {
    super.getRow(buffer, trainSet[position], indices);
  }
*/

 /**
   * Returns a TestTable with data from row index no. <code> start</code> in the
   * test set through row index no. <code>start+len</code> in the test set.
   *
   * @param start       index number into the test set of the row at which begins
   *                    the subset.
   * @param len         number of consequetive rows to include in the subset.
   /
  public Table getSubset(int start, int len) {
   return getSubset(start, len, false);
  }*/


  public Table copy() {

    return new SparseTrainTable((SparseExampleTable)super.copy());
  }


  public boolean doesValueExist(int row, int column){
    return super.doesValueExist(trainSet[row], column);
  }


     /**
     * Return the total number of entries in row index <code>position</code> in
     * the testing set.
     */
    public int getRowNumEntries(int position){
     return super.getRowNumEntries(trainSet[position]);

    }

    /**
     * Returns the valid column numbers of row index <code>position</code> in
     * the testing set.
     */
    public int[] getRowIndices(int position){
      return super.getRowIndices(trainSet[position]);
    }

    public int[] getRowIndicesUnsorted(int position){
      return super.getRowIndicesUnsorted(trainSet[position]);
    }

    /**
     * Add a row to the end of this Table, initialized with integer data.
     * @param newEntry the data to put into the new row.
     */
    public void addRow(int[] newEntry) {
      addTraining();
      super.addRow(newEntry);
    }

    /**
     * Add a row to the end of this Table, initialized with float data.
     * @param newEntry the data to put into the new row.
     */
    public void addRow(float[] newEntry) {
      addTraining();
      super.addRow(newEntry);
    }

    /**
     * Add a row to the end of this Table, initialized with double data.
     * @param newEntry the data to put into the new row.
     */
    public void addRow(double[] newEntry) {
      addTraining();
      super.addRow(newEntry);
    }

    /**
     * Add a row to the end of this Table, initialized with long data.
     * @param newEntry the data to put into the new row.
     */
    public void addRow(long[] newEntry) {
      addTraining();
      super.addRow(newEntry);
    }

    /**
     * Add a row to the end of this Table, initialized with short data.
     * @param newEntry the data to put into the new row.
     */
    public void addRow(short[] newEntry) {
      addTraining();
      super.addRow(newEntry);
    }

    /**
     * Add a row to the end of this Table, initialized with boolean data.
     * @param newEntry the data to put into the new row.
     */
    public void addRow(boolean[] newEntry) {
      addTraining();
      super.addRow(newEntry);
    }

    /**
     * Add a row to the end of this Table, initialized with String data.
     * @param newEntry the data to put into the new row.
     */
    public void addRow(String[] newEntry) {
      addTraining();
      super.addRow(newEntry);
    }

    /**
     * Add a row to the end of this Table, initialized with char[] data.
     * @param newEntry the data to put into the new row.
     */
    public void addRow(char[][] newEntry) {
      addTraining();
      super.addRow(newEntry);
    }

    /**
     * Add a row to the end of this Table, initialized with byte[] data.
     * @param newEntry the data to put into the new row.
     */
    public void addRow(byte[][] newEntry) {
      addTraining();
      super.addRow(newEntry);
    }

    /**
     * Add a row to the end of this Table, initialized with Object[] data.
     * @param newEntry the data to put into the new row.
     */
    public void addRow(Object[] newEntry) {
      addTraining();
      super.addRow(newEntry);
    }

    /**
     * Add a row to the end of this Table, initialized with byte data.
     * @param newEntry the data to put into the new row.
     */
    public void addRow(byte[] newEntry) {
      addTraining();
      super.addRow(newEntry);
    }

    /**
     * Add a row to the end of this Table, initialized with char data.
     * @param newEntry the data to put into the new row.
     */
    public void addRow(char[] newEntry) {
      addTraining();
      super.addRow(newEntry);
    }

    /**
     * Insert a new row into this Table, initialized with integer data.
     * @param newEntry the data to put into the inserted row.
     * @param position the position to insert the new row
     */
    public void insertRow(int[] newEntry, int position) {
      //insertTraining(trainSet[position]);
      super.insertRow(newEntry, trainSet[position]);
      addTraining(position);
    }

    /**
     * Insert a new row into this Table, initialized with float data.
     * @param newEntry the data to put into the inserted row.
     * @param position the position to insert the new row
     */
    public void insertRow(float[] newEntry, int position) {
      //insertTraining(trainSet[position]);
      super.insertRow(newEntry, trainSet[position]);
      addTraining(position);
    }

    /**
     * Insert a new row into this Table, initialized with double data.
     * @param newEntry the data to put into the inserted row.
     * @param position the position to insert the new row
     */
    public void insertRow(double[] newEntry, int position) {
      //insertTraining(trainSet[position]);
      //incrementTrainTest(position);
      super.insertRow(newEntry, trainSet[position]);
      addTraining(position);
    }

    /**
     * Insert a new row into this Table, initialized with long data.
     * @param newEntry the data to put into the inserted row.
     * @param position the position to insert the new row
     */
    public void insertRow(long[] newEntry, int position) {
      //insertTraining(trainSet[position]);
      //incrementTrainTest(position);
      super.insertRow(newEntry, trainSet[position]);
      addTraining(position);
    }

    /**
     * Insert a new row into this Table, initialized with short data.
     * @param newEntry the data to put into the inserted row.
     * @param position the position to insert the new row
     */
    public void insertRow(short[] newEntry, int position) {
      //insertTraining(trainSet[position]);
      //incrementTrainTest(position);
      super.insertRow(newEntry, trainSet[position]);
      addTraining(position);
    }

    /**
     * Insert a new row into this Table, initialized with boolean data.
     * @param newEntry the data to put into the inserted row.
     * @param position the position to insert the new row
     */
    public void insertRow(boolean[] newEntry, int position) {
      //insertTraining(trainSet[position]);
      //incrementTrainTest(position);
      super.insertRow(newEntry, trainSet[position]);
      addTraining(position);
    }

    /**
     * Insert a new row into this Table, initialized with String data.
     * @param newEntry the data to put into the inserted row.
     * @param position the position to insert the new row
     */
    public void insertRow(String[] newEntry, int position) {
      //insertTraining(trainSet[position]);
      //incrementTrainTest(position);
      super.insertRow(newEntry, trainSet[position]);
      addTraining(position);
    }

    /**
     * Insert a new row into this Table, initialized with char[] data.
     * @param newEntry the data to put into the inserted row.
     * @param position the position to insert the new row
     */
    public void insertRow(char[][] newEntry, int position) {
      //insertTraining(trainSet[position]);
      //incrementTrainTest(position);
      super.insertRow(newEntry, trainSet[position]);
      addTraining(position);
    }

    /**
     * Insert a new row into this Table, initialized with byte[] data.
     * @param newEntry the data to put into the inserted row.
     * @param position the position to insert the new row
     */
    public void insertRow(byte[][] newEntry, int position) {
      //insertTraining(trainSet[position]);
      //incrementTrainTest(position);
      super.insertRow(newEntry, trainSet[position]);
      addTraining(position);
    }

    /**
     * Insert a new row into this Table, initialized with Object data.
     * @param newEntry the data to put into the inserted row.
     * @param position the position to insert the new row
     */
    public void insertRow(Object[] newEntry, int position) {
      //insertTraining(trainSet[position]);
      //incrementTrainTest(position);
      super.insertRow(newEntry, trainSet[position]);
      addTraining(position);
    }

    /**
     * Insert a new row into this Table, initialized with byte data.
     * @param newEntry the data to put into the inserted row.
     * @param position the position to insert the new row
     */
    public void insertRow(byte[] newEntry, int position) {
      //insertTraining(trainSet[position]);
      //incrementTrainTest(position);
      super.insertRow(newEntry, trainSet[position]);
      addTraining(position);
    }

    /**
     * Insert a new row into this Table, initialized with char data.
     * @param newEntry the data to put into the inserted row.
     * @param position the position to insert the new row
     */
    public void insertRow(char[] newEntry, int position) {
      //insertTraining(trainSet[position]);
      //incrementTrainTest(position);
      super.insertRow(newEntry, trainSet[position]);
      addTraining(position);
    }

    /**
     * Set the row at the given position to an array of int data.
     *	@param newEntry the new values of the row.
     *	@param position the position to set
     */
    public void setRow(int[] newEntry, int position) {
      super.setRow(newEntry, trainSet[position]);
    }

    /**
     * Set the row at the given position to an array of float data.
     *	@param newEntry the new values of the row.
     *	@param position the position to set
     */
    public void setRow(float[] newEntry, int position) {
      super.setRow(newEntry, trainSet[position]);
    }

    /**
     * Set the row at the given position to an array of double data.
     *	@param newEntry the new values of the row.
     *	@param position the position to set
     */
    public void setRow(double[] newEntry, int position) {
      super.setRow(newEntry, trainSet[position]);
    }

    /**
     * Set the row at the given position to an array of long data.
     *	@param newEntry the new values of the row.
     *	@param position the position to set
     */
    public void setRow(long[] newEntry, int position) {
      super.setRow(newEntry, trainSet[position]);
    }

    /**
     * Set the row at the given position to an array of short data.
     *	@param newEntry the new values of the row.
     *	@param position the position to set
     */
    public void setRow(short[] newEntry, int position) {
      super.setRow(newEntry, trainSet[position]);
    }

    /**
     * Set the row at the given position to an array of boolean data.
     *	@param newEntry the new values of the row.
     *	@param position the position to set
     */
    public void setRow(boolean[] newEntry, int position) {
      super.setRow(newEntry, trainSet[position]);
    }

    /**
     * Set the row at the given position to an array of String data.
     *	@param newEntry the new values of the row.
     *	@param position the position to set
     */
    public void setRow(String[] newEntry, int position) {
      super.setRow(newEntry, trainSet[position]);
    }

    /**
     * Set the row at the given position to an array of char[] data.
     *	@param newEntry the new values of the row.
     *	@param position the position to set
     */
    public void setRow(char[][] newEntry, int position) {
      super.setRow(newEntry, trainSet[position]);
    }

    /**
     * Set the row at the given position to an array of byte[] data.
     *	@param newEntry the new values of the row.
     *	@param position the position to set
     */
    public void setRow(byte[][] newEntry, int position) {
      super.setRow(newEntry, trainSet[position]);
    }

    /**
     * Set the row at the given position to an array of Object data.
     *	@param newEntry the new values of the row.
     *	@param position the position to set
     */
    public void setRow(Object[] newEntry, int position) {
      super.setRow(newEntry, trainSet[position]);
    }

    /**
     * Set the row at the given position to an array of byte data.
     *	@param newEntry the new values of the row.
     *	@param position the position to set
     */
    public void setRow(byte[] newEntry, int position) {
      super.setRow(newEntry, trainSet[position]);
    }

    /**
     * Set the row at the given position to an array of char data.
     *	@param newEntry the new values of the row.
     *	@param position the position to set
     */
    public void setRow(char[] newEntry, int position) {
      super.setRow(newEntry, trainSet[position]);
    }

    /**
     * Remove a column from the table.
     * @param position the position of the Column to remove
     /
    public void removeColumn(int position) {
      decrementInOut(position);
      super.removeColumn(position);
    }*/

    /**
     Remove a range of columns from the table.
     @param start the start position of the range to remove
     @param len the number to remove-the length of the range
     */
    public void removeColumns(int start, int len) {
      for (int i = start; i < len; i++) {
        removeColumn(i);
      }
    }

    /**
     * Remove a row from this Table.
     * @param row the row to remove
     */
    public void removeRow(int row) {
      decrementTrainTest(row);
      super.removeRow(row);
    }

    /**
     Remove a range of rows from the table.
     @param start the start position of the range to remove
     @param len the number to remove-the length of the range
     */
    public void removeRows(int start, int len) {
      for (int i = start; i < len; i++) {
        removeColumn(i);
      }
    }

    /**
     * Remove rows from this Table using a boolean map.
     * @param flags an array of booleans to map to this Table's rows.  Those
     * with a true will be removed, all others will not be removed
     */
    public void removeRowsByFlag(boolean[] flags) {
      int numRemoved = 0;
      for (int i = 0; i < flags.length; i++) {
        if (flags[i]) {
          removeRow(i - numRemoved);
        }
      }
    }

    /**
     * Remove rows from this Table using a boolean map.
     * @param flags an array of booleans to map to this Table's rows.  Those
     * with a true will be removed, all others will not be removed
     */
    public void removeColumnsByFlag(boolean[] flags) {
      int numRemoved = 0;
      for (int i = 0; i < flags.length; i++) {
        if (flags[i]) {
          removeColumn(i - numRemoved);
        }
      }
    }

    /**
     * Remove rows from this Table by index.
     * @param indices a list of the rows to remove
     */
    public void removeRowsByIndex(int[] indices) {
      for (int i = 0; i < indices.length; i++) {
        removeRow(indices[i] - i);
      }
    }

    /**
     * Remove rows from this Table by index.
     * @param indices a list of the rows to remove
     */
    public void removeColumnsByIndex(int[] indices) {
      for (int i = 0; i < indices.length; i++) {
        removeColumn(indices[i] - i);
      }
    }

    /**
     Get a copy of this Table reordered based on the input array of indexes.
     Does not overwrite this Table.
     @param newOrder an array of indices indicating a new order
     @return a copy of this column with the rows reordered
     */
    public Table reorderRows(int[] newOrder) {
      return super.reorderRows(newOrder);
    }

    /**
     Get a copy of this Table reordered based on the input array of indexes.
     Does not overwrite this Table.
     @param newOrder an array of indices indicating a new order
     @return a copy of this column with the rows reordered
     */
    public Table reorderColumns(int[] newOrder) {
      return super.reorderColumns(newOrder);
    }

    /**
     Swap the positions of two rows.
     @param pos1 the first row to swap
     @param pos2 the second row to swap
     */
    public void swapRows(int pos1, int pos2) {
      super.swapRows(trainSet[pos1], trainSet[pos2]);
      this.swapTestTrain(pos1, pos2);
    }

    /**
     Swap the positions of two columns.
     @param pos1 the first column to swap
     @param pos2 the second column to swap
     /
    public void swapColumns(int pos1, int pos2) {
      super.swapColumns(pos1, pos2);
      this.swapInOut(pos1, pos2);
    }*/

    /**
     Set a specified element in the table.  If an element exists at this
         position already, it will be replaced.  If the position is beyond the capacity
     of this table then an ArrayIndexOutOfBounds will be thrown.
     @param element the new element to be set in the table
     @param row the row to be changed in the table
     @param column the Column to be set in the given row
     */
    public void setObject(Object element, int row, int column) {
      super.setObject(element, trainSet[row], column);
    }

    /**
     * Set an int value in the table.
     * @param data the value to set
     * @param row the row of the table
     * @param column the column of the table
     */
    public void setInt(int data, int row, int column) {
      super.setInt(data, trainSet[row], column);
    }

    /**
     * Set a short value in the table.
     * @param data the value to set
     * @param row the row of the table
     * @param column the column of the table
     */
    public void setShort(short data, int row, int column) {
      super.setShort(data, trainSet[row], column);
    }

    /**
     * Set a float value in the table.
     * @param data the value to set
     * @param row the row of the table
     * @param column the column of the table
     */
    public void setFloat(float data, int row, int column) {
      super.setFloat(data, trainSet[row], column);
    }

    /**
     * Set an double value in the table.
     * @param data the value to set
     * @param row the row of the table
     * @param column the column of the table
     */

    public void setDouble(double data, int row, int column) {
      super.setDouble(data, trainSet[row], column);
    }

    /**
     * Set a long value in the table.
     * @param data the value to set
     * @param row the row of the table
     * @param column the column of the table
     */
    public void setLong(long data, int row, int column) {
      super.setLong(data, trainSet[row], column);
    }

    /**
     * Set a String value in the table.
     * @param data the value to set
     * @param row the row of the table
     * @param column the column of the table
     */
    public void setString(String data, int row, int column) {
      super.setString(data, trainSet[row], column);
    }

    /**
     * Set a byte[] value in the table.
     * @param data the value to set
     * @param row the row of the table
     * @param column the column of the table
     */

    public void setBytes(byte[] data, int row, int column) {
      super.setBytes(data, trainSet[row], column);
    }

    /**
     * Set a boolean value in the table.
     * @param data the value to set
     * @param row the row of the table
     * @param column the column of the table
     */
    public void setBoolean(boolean data, int row, int column) {
      super.setBoolean(data, trainSet[row], column);
    }

    /**
     * Set a char[] value in the table.
     * @param data the value to set
     * @param row the row of the table
     * @param column the column of the table
     */

    public void setChars(char[] data, int row, int column) {
      super.setChars(data, trainSet[row], column);
    }

    /**
     * Set a byte value in the table.
     * @param data the value to set
     * @param row the row of the table
     * @param column the column of the table
     */
    public void setByte(byte data, int row, int column) {
      super.setByte(data, trainSet[row], column);
    }

    /**
     * Set a char value in the table.
     * @param data the value to set
     * @param row the row of the table
     * @param column the column of the table
     */
    public void setChar(char data, int row, int column) {
      super.setChar(data, trainSet[row], column);
    }

    /**
                                    Set the name associated with a column.
                                    @param label the new column label
                        @param position the index of the column to set
         /
                        public void setColumnLabel(String label, int position);
         /**
                            Set the comment associated with a column.
                                         @param comment the new column comment
                            @param position the index of the column to set
                 /
            public void setColumnComment(String comment, int position);
        /**
            Set the number of columns this Table can hold.
            @param numColumns the number of columns this Table can hold
       */
      public void setNumColumns(int numColumns) {
        dropInOut(numColumns);
        super.setNumColumns(numColumns);
      }

    /**
     Sort the specified column and rearrange the rows of the table to
     correspond to the sorted column.
     @param col the column to sort by
     /
       public void sortByColumn(int col)
       /**
          Sort the elements in this column starting with row 'begin' up to row 'end',
                @param col the index of the column to sort
              @param begin the row no. which marks the beginnig of the  column segment to be sorted
          @param end the row no. which marks the end of the column segment to be sorted
            /
              public void sortByColumn(int col, int begin, int end);

        /**
            Sets a new capacity for this Table.  The capacity is its potential
           maximum number of entries.  If numEntries is greater than newCapacity,
            then the Table may be truncated.
            @param newCapacity a new capacity
       */
      public void setNumRows(int newCapacity) {
        dropTestTrain(newCapacity);
        super.setNumRows(newCapacity);
      }

    public void setColumn(char[] data, int pos) {
       //CharColumn cc = new CharColumn(data);
       //setColumn(cc, pos);
       for(int i = 0; i < data.length; i++) {
         setChar(data[i], i,  pos);
       }
    }
    public void setColumn(byte[] data, int pos) {
       //ByteColumn bc = new ByteColumn(data);
       //setColumn(bc, pos);
       for(int i = 0; i < data.length; i++) {
         setByte(data[i], i,  pos);
       }
    }

    public void setColumn(int[] data, int pos) {
       //IntColumn ic = new IntColumn(data);
       //setColumn(ic, pos);
       for(int i = 0; i < data.length; i++) {
         setInt(data[i], i,  pos);
       }
    }
    public void setColumn(float[] data, int pos) {
       //FloatColumn ic = new FloatColumn(data);
       //setColumn(ic, pos);
       for(int i = 0; i < data.length; i++) {
         setFloat(data[i], i,  pos);
       }
    }
    public void setColumn(double[] data, int pos) {
       //DoubleColumn ic = new DoubleColumn(data);
       //setColumn(ic, pos);
       for(int i = 0; i < data.length; i++) {
         setDouble(data[i], i,  pos);
       }
    }
    public void setColumn(long[] data, int pos) {
       //LongColumn ic = new LongColumn(data);
       //setColumn(ic, pos);
       for(int i = 0; i < data.length; i++) {
         setDouble(data[i], i,  pos);
       }
    }
    public void setColumn(short[] data, int pos) {
       //ShortColumn ic = new ShortColumn(data);
       //setColumn(ic, pos);
       for(int i = 0; i < data.length; i++) {
         setShort(data[i], i,  pos);
       }
    }
    public void setColumn(String[] data, int pos) {
       //StringColumn ic = new StringColumn(data);
       //setColumn(ic, pos);
       for(int i = 0; i < data.length; i++) {
         setString(data[i], i,  pos);
       }
    }
    public void setColumn(byte[][] data, int pos) {
       //ContinuousByteArrayColumn bc = new ContinuousByteArrayColumn(data);
       //setColumn(bc, pos);
       for(int i = 0; i < data.length; i++) {
         setBytes(data[i], i,  pos);
       }
    }
    public void setColumn(char[][] data, int pos) {
       //ContinuousCharArrayColumn cc = new ContinuousCharArrayColumn(data);
       //setColumn(cc, pos);
       for(int i = 0; i < data.length; i++) {
         setChars(data[i], i,  pos);
       }
    }
    public void setColumn(Object[] data, int pos) {
       //ObjectColumn ic = new ObjectColumn(data);
       //setColumn(ic, pos);
       for(int i = 0; i < data.length; i++) {
         setObject(data[i], i,  pos);
       }
    }
    public void setColumn(boolean[] data, int pos) {
       //BooleanColumn ic = new BooleanColumn(data);
       //ic.setLabel(getColumnLabel(pos));
       //ic.setComment(getColumnComment(pos));
       //setColumn(ic, pos);
       for(int i = 0; i < data.length; i++) {
         setBoolean(data[i], i,  pos);
       }
    }



    // maintenance methods:

    private void addTraining() {

      int[] newTrainingSet = new int[trainSet.length + 1];
      for (int i = 0; i < trainSet.length; i++) {
        newTrainingSet[i] = trainSet[i];
      }
      newTrainingSet[trainSet.length] = super.getNumRows();

      //trainSet = newTrainingSet;
      setTrainingSet(newTrainingSet);

    }

    private void insertTraining(int tablePosition) {

      for (int i = 0; i < trainSet.length; i++) {
        if (trainSet[i] >= tablePosition) {
          trainSet[i]++;

        }
      }
      int[] newTrainingSet = new int[trainSet.length + 1];
      for (int i = 0; i < trainSet.length; i++) {
        newTrainingSet[i] = trainSet[i];
      }
      newTrainingSet[trainSet.length] = tablePosition;

      Arrays.sort(newTrainingSet);

      trainSet = newTrainingSet;

    }

    private void removeTraining(int trainingPosition) {

      int[] newTrainingSet = new int[trainSet.length - 1];
      for (int i = 0; i < trainingPosition; i++) {
        newTrainingSet[i] = trainSet[i];

      }
      for (int i = trainingPosition + 1; i < trainSet.length; i++) {
        newTrainingSet[i - 1] = trainSet[i] - 1;

      }
      trainSet = newTrainingSet;

    }

    private void addTraining(int tablePosition) {
      int[] newTrainingSet = new int[trainSet.length + 1];
      System.arraycopy(trainSet, 0, newTrainingSet, 0, trainSet.length);
      newTrainingSet[newTrainingSet.length - 1] = tablePosition;
      setTrainingSet(newTrainingSet);
    }


}