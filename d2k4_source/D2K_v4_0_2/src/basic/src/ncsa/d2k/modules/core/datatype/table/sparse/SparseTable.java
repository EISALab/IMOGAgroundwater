package ncsa.d2k.modules.core.datatype.table.sparse;

//==============
// Java Imports
//==============

import java.io.*;
import java.util.*;

//===============
// Other Imports
//===============

import gnu.trove.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.datatype.table.sparse.columns.*;
import ncsa.d2k.modules.core.datatype.table.sparse.primitivehash.*;

/**
 * SparseTable is a type of Table that is sparsely populated.
 * Thus the internal representation of SparseTable is as following:
 * each column that actually has elements in it (hereinafter "valid column") is
 * represented by a hashmap (an int to primitive type or object hashmap).
 *
 * The entries of the sparse table are the values of such hashmap and they are mapped
 * to their row number (the key).
 *
 * All the columns are been held in a nother int to object hashmap.
 * the keys are integers - the column number and the values are the hashmaps.
 *
 * SparseTable holds another int to object hashmap which represents the valid
 * rows (rows that have elements in them).
 *
 * Each row is represented by a Set of integers, which are the valid columns number
 * of that specific row.
 *
 * Each row (a Set object) is mapped to an int, the row number, in the hashmap.
 *
 * Regarding missing values.  Missing values are treated just as they are in any other
 * table implementations.  Missing values however are not the same as "default" values
 * which constitute the majority of values in the value space.
 *
 */

public abstract class SparseTable
    extends DefaultMissingValuesTable
    implements Serializable {

  //==============
  // Data Members
  //==============

  protected VIntObjectHashMap columns; //ints (keys - the column number) mapped to
  //int hashmaps (values - the columns)
  protected VIntObjectHashMap rows; //ints (keys - row number) mapped to Sets
  //(values - holds the valid columns in the row)
  protected int numRows;
  protected int numColumns;
  protected static SparseTableFactory factory = new SparseTableFactory();

  /** this is the label for the table.*/
  protected String label = "";

  /** comment for the table. */
  protected String comment = "";

  //================
  // Constructor(s)
  //================

  /**
   * creates a sparse table with hashmaps with default capacity load factor.
   */
  public SparseTable() {
    this(0, 0);
  }

  /**
   * Creates a SparseTable with column hashmap with <code>numCols</code> capacity, and rows
   * hashmap with <code>numRows</code> capacity.
   *
       * @param numRows   number of rows, also the initial capacity of the rows hashmap
       * @param numCols   number of columns, also the initial capacity of the columns
   *                  hashmap
   */
  public SparseTable(int numRows, int numCols) {
    if (numCols == 0) {
      columns = new VIntObjectHashMap();
    }
    else {
      columns = new VIntObjectHashMap(numCols);

    }
    if (numRows == 0) {
      rows = new VIntObjectHashMap();
    }
    else {
      rows = new VIntObjectHashMap(numRows);
    }
    numRows = 0;
    numColumns = 0;
  }

  /**
   * instantiate this table with the content of <code>T</codE>
   * creates a shallow copy of <codE>T</code>
   */
  public SparseTable(SparseTable T) {

    //making a shallow copy of rows.
    rows = new VIntObjectHashMap(T.getNumRows());

    //retrieving valid rows numbers
    int[] rKeys;

    if (T instanceof TestTable) {
      rKeys = ( (SparseExampleTable) T).testSet;
    }
    else if (T instanceof TrainTable) {
      rKeys = ( (SparseExampleTable) T).trainSet;
    }
    else {
      rKeys = T.rows.keys();

    }

    for (int i = 0; i < rKeys.length; i++) {
      rows.put(rKeys[i], T.rows.get(rKeys[i]));

      //making a shallow copy of columns
    }
    columns = new VIntObjectHashMap(T.columns.size());
    int[] cKeys = T.columns.keys();
    for (int i = 0; i < cKeys.length; i++) {
      columns.put(cKeys[i], T.columns.get(cKeys[i]));

    }
    numRows = T.numRows;
    numColumns = T.numColumns;
    copyAttributes(T);
  }



  //=================
  // Table Interface
  //=================

  /**
   * ***********************************************************
   * GET TYPE METHODS
   * ***********************************************************
   */

  /**
   * Returns a boolean representation of the data held at (row,column) in this
   * table
   *
   * @param row     the row number from which to retrieve the data
   * @param column  the column number from which to retrieve the data
   * @return        the data at position (row, column) represented by a boolean
   *                returns false if such column does not exist.
   */
  public boolean getBoolean(int row, int column) {
    if ((column < 0) || (column >= this.getNumColumns())){
      throw new java.lang.RuntimeException("Column index out of range: " + column);
    }
    if ((row < 0) || (row >= this.getNumRows())){
      throw new java.lang.RuntimeException("Row index out of range: " + row);
    }
    if (columns.containsKey(column)) {
      return ( (Column) columns.get(column)).getBoolean(row);
    }
    else {
      return SparseDefaultValues.getDefaultBoolean();
    }
  }

  /**
   * Returns a char representation of the data held at (row,column) in this
   * table
   *
   * @param row     the row number from which to retrieve the data
   * @param column  the column number from which to retrieve the data
   * @return        the data at position (row, column) represented by a char.
       *                returns a value signifying the position is empty, as defined
   *                by SparseCharColumn if such column does not exist.
   */
  public char getChar(int row, int column) {
    if ((column < 0) || (column >= this.getNumColumns())){
      throw new java.lang.RuntimeException("Column index out of range: " + column);
    }
    if ((row < 0) || (row >= this.getNumRows())){
      throw new java.lang.RuntimeException("Row index out of range: " + row);
    }
    if (columns.containsKey(column)) {
      return ( (Column) columns.get(column)).getChar(row);
    }
    else {
      return SparseDefaultValues.getDefaultChar();
    }
  }

  /**
       * Returns a char array representation of the data held at (row,column) in this
   * table
   *
   * @param row     the row number from which to retrieve the data
   * @param column  the column number from which to retrieve the data
       * @return        the data at position (row, column) represented by a char array.
   *                returns null if such column does not exist.
   */
  public char[] getChars(int row, int column) {
    if ((column < 0) || (column >= this.getNumColumns())){
      throw new java.lang.RuntimeException("Column index out of range: " + column);
    }
    if ((row < 0) || (row >= this.getNumRows())){
      throw new java.lang.RuntimeException("Row index out of range: " + row);
    }
    if (columns.containsKey(column)) {
      return ( (Column) columns.get(column)).getChars(row);
    }
    else {
      return null;
    }
  }

  /**
       * Returns a double olean representation of the data held at (row,column) in this
   * table
   *
   * @param row     the row number from which to retrieve the data
   * @param column  the column number from which to retrieve the data
   * @return        the data at position (row, column) represented by a double
       *                if such column does not exist returns a value signifying the
   *                position is empty, as defined by SparseDoubleColumn.
   */
  public double getDouble(int row, int column) {
    if ((column < 0) || (column >= this.getNumColumns())){
      throw new java.lang.RuntimeException("Column index out of range: " + column);
    }
    if ((row < 0) || (row >= this.getNumRows())){
      throw new java.lang.RuntimeException("Row index out of range: " + row);
    }
    if (columns.containsKey(column)) {
      return ( (Column) columns.get(column)).getDouble(row);
    }
    else {
      return SparseDefaultValues.getDefaultDouble();
    }
  }

  /**
   * Returns a float representation of the data held at (row,column) in this
   * table
   *
   * @param row     the row number from which to retrieve the data
   * @param column  the column number from which to retrieve the data
   * @return        the data at position (row, column) represented by a float
       *                returns Float.NEGATIVE_INFINITY if such column does not exist.
   */
  public float getFloat(int row, int column) {
    if ((column < 0) || (column >= this.getNumColumns())){
      throw new java.lang.RuntimeException("Column index out of range: " + column);
    }
    if ((row < 0) || (row >= this.getNumRows())){
      throw new java.lang.RuntimeException("Row index out of range: " + row);
    }
    if (columns.containsKey(column)) {
      return ( (Column) columns.get(column)).getFloat(row);
    }
    else {
      return (float)SparseDefaultValues.getDefaultDouble();
    }
  }

  /**
       * Returns a byte array representation of the data held at (row,column) in this
   * table
   *
   * @param row     the row number from which to retrieve the data
   * @param column  the column number from which to retrieve the data
       * @return        the data at position (row, column) represented by a byte array
   *                returns null if such column does not exist.
   */
  public byte[] getBytes(int row, int column) {
    if ((column < 0) || (column >= this.getNumColumns())){
      throw new java.lang.RuntimeException("Column index out of range: " + column);
    }
    if ((row < 0) || (row >= this.getNumRows())){
      throw new java.lang.RuntimeException("Row index out of range: " + row);
    }
    if (columns.containsKey(column)) {
      return ( (Column) columns.get(column)).getBytes(row);
    }
    else {
      return null;
    }
  }

  /**
   * Returns a byte representation of the data held at (row,column) in this
   * table
   *
   * @param row     the row number from which to retrieve the data
   * @param column  the column number from which to retrieve the data
   * @return        the data at position (row, column) represented by a byte
       *                returns a value signifying the position is empty, as defined
   *                by SparseByteColumn if such column does not exist.
   */
  public byte getByte(int row, int column) {
    if ((column < 0) || (column >= this.getNumColumns())){
      throw new java.lang.RuntimeException("Column index out of range: " + column);
    }
    if ((row < 0) || (row >= this.getNumRows())){
      throw new java.lang.RuntimeException("Row index out of range: " + row);
    }
    if (columns.containsKey(column)) {
      return ( (Column) columns.get(column)).getByte(row);
    }
    else {
      return SparseDefaultValues.getDefaultByte();
    }
  }


  /**
   * Returns a long representation of the data held at (row,column) in this
   * table
   *
   * @param row     the row number from which to retrieve the data
   * @param column  the column number from which to retrieve the data
   * @return        the data at position (row, column) represented by a long
       *                if such column does not exist returns a value signifying the
   *                position is empty, as defined by SparseLongColumn.
   */
  public long getLong(int row, int column) {
    if ((column < 0) || (column >= this.getNumColumns())){
      throw new java.lang.RuntimeException("Column index out of range: " + column);
    }
    if ((row < 0) || (row >= this.getNumRows())){
      throw new java.lang.RuntimeException("Row index out of range: " + row);
    }
    if (columns.containsKey(column)) {
      return ( (Column) columns.get(column)).getLong(row);
    }
    else {
      return (long)SparseDefaultValues.getDefaultInt();
    }
  }


  /**
   * Returns an Object encapsulating the data held at (row,column) in this
   * table
   *
   * @param row     the row number from which to retrieve the data
   * @param column  the column number from which to retrieve the data
       * @return        the data at position (row, column) encapsulated in an Object
   *                returns null if such column does not exist.
   */
  public Object getObject(int row, int column) {
    if ((column < 0) || (column >= this.getNumColumns())){
      throw new java.lang.RuntimeException("Column index out of range: " + column);
    }
    if ((row < 0) || (row >= this.getNumRows())){
      throw new java.lang.RuntimeException("Row index out of range: " + row);
    }
    if (columns.containsKey(column)) {
      return ( (Column) columns.get(column)).getObject(row);
    }
    else {
      // Return default value
      return SparseDefaultValues.getDefaultObject();
    }
  }

  /**
   * Returns an int representation of the data held at (row,column) in this
   * table
   *
   * @param row     the row number from which to retrieve the data
   * @param column  the column number from which to retrieve the data
   * @return        the data at position (row, column) represented by an int
       *                if such column does not exist.returns a value signifying the
   *                position is empty, as defined by SparseIntColumn.
   */
  public int getInt(int row, int column) {
    if ((column < 0) || (column >= this.getNumColumns())){
      throw new java.lang.RuntimeException("Column index out of range: " + column);
    }
    if ((row < 0) || (row >= this.getNumRows())){
      throw new java.lang.RuntimeException("Row index out of range: " + row);
    }
    if (columns.containsKey(column)) {
      return ( (Column) columns.get(column)).getInt(row);
    }
    else {
      return SparseDefaultValues.getDefaultInt();
    }
  }

  /**
   * Returns a short representation of the data held at (row,column) in this
   * table
   *
   * @param row     the row number from which to retrieve the data
   * @param column  the column number from which to retrieve the data
   * @return        the data at position (row, column) represented by a short
   *                returns a value signifying the position is empty,
       *                as defined by SparseShortColumn, if such column does not exist.
   */
  public short getShort(int row, int column) {
    if ((column < 0) || (column >= this.getNumColumns())){
      throw new java.lang.RuntimeException("Column index out of range: " + column);
    }
    if ((row < 0) || (row >= this.getNumRows())){
      throw new java.lang.RuntimeException("Row index out of range: " + row);
    }
    if (columns.containsKey(column)) {
      return ( (Column) columns.get(column)).getShort(row);
    }
    else {
      return (short)SparseDefaultValues.getDefaultInt();
    }
  }

  /**
   * Returns a String representation of the data held at (row,column) in this
   * table
   *
   * @param row     the row number from which to retrieve the data
   * @param column  the column number from which to retrieve the data
   * @return        the data at position (row, column) represented by a String
   *                returns null if such column does not exist.
   */
  public String getString(int row, int column) {
    if ((column < 0) || (column >= this.getNumColumns())){
      throw new java.lang.RuntimeException("Column index out of range: " + column);
    }
    if ((row < 0) || (row >= this.getNumRows())){
      throw new java.lang.RuntimeException("Row index out of range: " + row);
    }
    if (columns.containsKey(column)) {
      return ( (Column) columns.get(column)).getString(row);
    }
    else {
      return null;
    }
  }

  //==========================
  // Table Metadata Accessors
  //==========================

  /**
   * Returns the label associated with column #<code>position</code>
   *
   * @param position    the column number to retrieve its label.
   * @return        the associated label with column #<codE>position</code> or
   *                null if such column does not exit.
   */
  public String getColumnLabel(int position) {
    if ((position < 0) || (position >= this.getNumColumns())){
      throw new java.lang.RuntimeException("SparseTable.getColumnLabel :: column index out of range: " + position);
    }
    if (columns.containsKey(position)) {
      String retval = ( (Column) columns.get(position)).getLabel();
      if (retval == null){
        return  "column_" + position;
      } else {
        return retval;
      }
    }
    else {
      return  "column_" + position;
    }
  }

  /**
   * Returns the comment associated with column #<code>position</code>
   *
   * @param position    the column number to retrieve its comment.
   * @return    the associated comment with column #<codE>position</code> or
   *            null if such column does not exit.
   */
  public String getColumnComment(int position) {
    if ((position < 0) || (position >= this.getNumColumns())){
      throw new java.lang.RuntimeException("SparseTable.getColumnComment :: column index out of range: " + position);
    }
    if (columns.containsKey(position)) {
      String retval = ( (Column) columns.get(position)).getComment();
      if (retval == null){
        return  "column_" + position;
      } else {
        return retval;
      }
    }
    else {
      return  "column_" + position;
    }
  }

  ////////////////////////////
  // access metadata for the table.
  //
  /**
   * Get the label associated with this Table.
   * @return the label which describes this Table
   */
  public String getLabel() {
    return label;
  }

  /**
          Set the label associated with this Table.
          @param labl the label which describes this Table
   */
  public void setLabel(String labl) {
    if (labl == null) {
      labl = "";
    }
    label = labl;
  }

  /**
          Get the comment associated with this Table.
          @return the comment which describes this Table
   */
  public String getComment() {
    return comment;
  }

  /**
          Set the comment associated with this Table.
          @param cmt the comment which describes this Table
   */
  public void setComment(String cmt) {
    if (cmt == null){
      cmt = "";
    }
    comment = cmt;
  }

  /**
   * Returns the number of rows in this table. counting starts from 0;
   * @return     the maximal row number + 1.
   */
  public int getNumRows() {
    return numRows;
  }

  /**
   * Returns the number of columns in this talbe. Since this is a Sparse Table
   * returns the maximal column number plus 1 (counting starts from zero)
   *
   * @return    the maximal column number plus 1.
   */
  public int getNumColumns() {
    return numColumns;
  }


  /**
   * This method was removed since it was deemed redundant in conjunction with
   * getNumRows ...
   */
  /**
   * Returns the total number of entries in this table.
   *
   * @return    the total number of valid entries in this table
   */
//  public int getNumEntries() {
//    int numEntries = 0; //the returned value
//
//    int[] columnNumbers = columns.keys(); //retrieving the column number
//
//    //for each colum
//    for (int i = 0; i < columnNumbers.length; i++) {
//
//      //add its number of entries to the returned value
//      numEntries += getColumnNumEntries(columnNumbers[i]);
//
//    }
//    return numEntries;
//  }

  /**
   * Retruns true if column #<code>position</code> holds nominal values. otherwise
   * returns false (also if such column does not exist).
   *
   * @param position    the column number its data type is varified
   * @return            true if the data t column #<code>position</code> is
   *                    nominal. if else (also if column does not exist) returns
   *                    false.
   */
  public boolean isColumnNominal(int position) {
    if ((position < 0) || (position >= this.getNumColumns())){
      throw new java.lang.RuntimeException("SparseTable.isColumnNominal :: column index out of range: " + position);
    }
    if (columns.containsKey(position)) {
      return ( (Column) columns.get(position)).getIsNominal();
    }
    else {
      return false;
    }
  }

  /**
   * Retruns true if column #<code>position</code> holds scalar values. otherwise
   * returns false (also if such column does not exist).
   *
   * @param position    the column number its data type is varified
   * @return            true if the data t column #<code>position</code> is
   *                    scalar. if else (also if column does not exist) returns
   *                    false.
   */
  public boolean isColumnScalar(int position) {
    if ((position < 0) || (position >= this.getNumColumns())){
      throw new java.lang.RuntimeException("SparseTable.isColumnScalar :: column index out of range: " + position);
    }
    if (columns.containsKey(position)) {
      return ( (Column) columns.get(position)).getIsScalar();
    }
    else {
      return false;
    }
  }

  /**
   * marks the falg attributes of column #<code>position</code>
   * according to <code>values</code>: sets isNominal to
   * <code>value</code> and sets isScalar
   * to <code>!value</code>
   *
   * @param value   a flag signifies whether a column holds nominal values
   * @param position  the column number to have its attributes set
   *
   */
  public void setColumnIsNominal(boolean value, int position) {
    if ((position < 0) || (position >= this.getNumColumns())){
      throw new java.lang.RuntimeException("SparseTable.setColumnIsNominal :: column index out of range: " + position);
    }
    if (columns.containsKey(position)) {
      ( (Column) columns.get(position)).setIsNominal(value);
    }
  }

  /**
   * marks the falg attributes of column #<code>position</code> according to
   * <code>values</code>: sets isScalar to <code>value</code> and sets isNominal
   * to <code>!value</code>
   *
   * @param value   a flag signifies whether a column holds scalar values
   * @param position  the column number to have its attributes set
   *
   */
  public void setColumnIsScalar(boolean value, int position) {
    if ((position < 0) || (position >= this.getNumColumns())){
      throw new java.lang.RuntimeException("SparseTable.setColumnIsScalar :: column index out of range: " + position);
    }
    if (columns.containsKey(position)) {
      ( (Column) columns.get(position)).setIsScalar(value);
    }
  }

  /**
   * Returns true if column #<code>position</code> holds numeric data or
   * data that numeric values can be parsed from. otherwise return false
   * (also if the column does not exist).
   *
   * @param position    the column number which its data type is verified.
   * @return            true if the data at column #<code>position</code> is
   *                    numeric or that numeric values can be parsed from it.
       *                    returns false if otherwise (also if column does not exit).
   */
  public boolean isColumnNumeric(int position) {
    if ((position < 0) || (position >= this.getNumColumns())){
      throw new java.lang.RuntimeException("SparseTable.isColumnNumeric :: column index out of range: " + position);
    }
    if (columns.containsKey(position)) {
      return ( (AbstractSparseColumn) columns.get(position)).isNumeric();
    }
    else {
      return false;
    }
  }

  /**
   * Returns an int representing the type of data held at column  #<code>position</code>.
   *
   * @param position  the oclumn number its data type is being varified.
   * @return          an integer value representing the type of the data.
   *                  return -1 if such column does not exist.
   */
  public int getColumnType(int position) {
    if ((position < 0) || (position >= this.getNumColumns())){
      throw new java.lang.RuntimeException("SparseTable.getColumnType :: column index out of range: " + position);
    }
    if (columns.containsKey(position)) {
      return ( (Column) columns.get(position)).getType();
    } else {
      return ColumnTypes.UNSPECIFIED;
    }
  }

  /**
   * Return true if the value at (row, col) is a missing value, false otherwise.
   * @param row the row index
   * @param col the column index
   * @return true if the value is missing, false otherwise
   */
  public boolean isValueMissing(int row, int col) {
    if ((col < 0) || (col >= this.getNumColumns())){
      throw new java.lang.RuntimeException("Column index out of range: " + col);
    }
    if ((row < 0) || (row >= this.getNumRows())){
      throw new java.lang.RuntimeException("Row index out of range: " + row);
    }
    if (columns.containsKey(col)) {
      return ( (Column) columns.get(col)).isValueMissing(row);
    }
    else {
      return false;
    }
  }

  /**
   * Return true if the value at (row, col) is an empty value, false otherwise.
   * @param row the row index
   * @param col the column index
   * @return true if the value is empty, false otherwise
   */
  public boolean isValueEmpty(int row, int col) {
    if ((col < 0) || (col >= this.getNumColumns())){
      throw new java.lang.RuntimeException("Column index out of range: " + col);
    }
    if ((row < 0) || (row >= this.getNumRows())){
      throw new java.lang.RuntimeException("Row index out of range: " + row);
    }
    if (columns.containsKey(col)) {
      return ( (Column) columns.get(col)).isValueEmpty(row);
    }
    else {
      return false;
    }
  }


  /**
   * Return true if any value in this Table is missing.
   * @return true if there are any missing values, false if there are no missing values
   */
  public boolean hasMissingValues() {
    int[] colarr = columns.keys();
    for (int i = 0; i < colarr.length; i++) {
      int[] rowarr = ((AbstractSparseColumn)getColumn(colarr[i])).getIndices();
      for (int j = 0; j < rowarr.length; j++) {
        if (isValueMissing(rowarr[j], colarr[i])) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * @see ncsa.d2k.modules.core.datatype.table.Table#hasMissingValues(int)
   */
  public boolean hasMissingValues(int columnIndex) {
    if ((columnIndex < 0) || (columnIndex >= this.getNumColumns())){
      throw new java.lang.RuntimeException("Column index out of range: " + columnIndex);
    }
    if (columns.containsKey(columnIndex)) {
      return this.getColumn(columnIndex).hasMissingValues();
    } else {
      return false;
    }
  }

  /**
   * Returns the Column at index <codE>pos</code>.
   *
   * @param pos   the index number of the returned Column
   * @return      the Column at index <code>pos</codE>
   */
  public Column getColumn(int pos) {
    if ((pos < 0) || (pos >= this.getNumColumns())){
      throw new java.lang.RuntimeException("Column index out of range: " + pos);
    }
    return (Column) columns.get(pos);
  }

  //===========================================================================
  // End Table Interface Implementation
  //===========================================================================

  //================
  // Public Methods
  //================

  /**
   * Returns the numbers of the valid rows of column no. <code>columnNumber</code>
   *
   * @param columnNumber    the index of the column which its valid row numbers
   *                        are to be retrieved
   * @return                the valid row numbers of column no. <code>columnNumber
   *                        </code>, sorted
   */
  public int[] getColumnIndices(int columnNumber) {
    if ((columnNumber < 0) || (columnNumber >= this.getNumColumns())){
      throw new java.lang.RuntimeException("Column index out of range: " + columnNumber);
    }
    int[] indices = null;
    if (columns.containsKey(columnNumber)) {
      return ( (AbstractSparseColumn) columns.get(columnNumber)).getIndices();
    }
    return indices;
  }

  /**
   * Returns the number of entries in column #<code>position</code>.
   *
   * @param position     the column number to retrieve its total number of entries.
   * @return             the number of entries in column #<code>position</code>
   *                      return zero if no such column exists.
   */
  public int getColumnNumEntries(int position) {
    if ((position < 0) || (position >= this.getNumColumns())){
      throw new java.lang.RuntimeException("Column index out of range: " + position);
    }
    if (columns.containsKey(position)) {
      return ( (AbstractSparseColumn) columns.get(position)).getNumEntries();
    }
    else {
      return 0;
    }
  }

  /**
   * Returns the number of entries in row #<code>position</code>.
   *
   * @param position     the row number to retrieve its total number of entries.
   * @return             the number of entries in row #<code>position</code>
   */
  public int getRowNumEntries(int position) {
    if ((position < 0) || (position >= this.getNumColumns())){
      throw new java.lang.RuntimeException("Column index out of range: " + position);
    }
    if (rows.containsKey(position)) {
      return ( (TIntHashSet) rows.get(position)).size();
    }
    else {
      return 0;
    }
  }

  //XIAOLEI
  public void print() {
    System.out.print("---------------------------------------");
    System.out.println("---------------------------------------");
    int[] my_rows = getAllRows();
    System.out.println(my_rows.length + " rows total.");
    System.out.println(numRows + " rows total.");
    System.out.println(numColumns + " columns total.");
    for (int i = 0; i < my_rows.length; i++) {
      System.out.print(my_rows[i] + ": ");

      /*
          //int[] my_cols = getRowIndices(i);
          int[] my_cols = getRowIndices(my_rows[i]);
          for (int j = 0; j < my_cols.length; j++) {
       System.out.print(my_cols[j] + "(");
       System.out.print(getDouble(my_rows[i], my_cols[j]) + ") ");
          }
          System.out.println();
       */

      for (int j = 0; j < numColumns; j++) {
        System.out.print(j + "(");
        System.out.print(getDouble(my_rows[i], j) + ") ");
      }
      System.out.println();
    }
    System.out.print("---------------------------------------");
    System.out.println("---------------------------------------");
  }

  /**
   * Returns true if there is data stored at (row, col) in this table
   *
   * This should not be confused however with missing values which are data
   * that we don't know the value of.  In this the case of this method we know the value is the
   * default value but it is not missing it just isn't stored.
   *
   * @param row     the row number
   * @param col     the column number
   * @return        true if there is data at position (row, col), otherwise
   *                return false.
   */
  public boolean doesValueExist(int row, int col) {
    if ((col < 0) || (col >= this.getNumColumns())){
      throw new java.lang.RuntimeException("Column index out of range: " + col);
    }
    if ((row < 0) || (row >= this.getNumRows())){
      throw new java.lang.RuntimeException("Row index out of range: " + row);
    }
    if (columns.containsKey(col)) {
      return ( (AbstractSparseColumn) columns.get(col)).doesValueExist(row);
    }
    else {
      return false;
    }
  }


  public TableFactory getTableFactory() {
    return factory;
  }


  /**
   * Returns the numbers of the valid columns of row number <code>rowNumber</code>
   *
   * @param rowNumber   the index of the row which its valid column numbers are
   *                    to be retrieved.
   * @return            the valid column numbers in row no. <code>rowNumber</code>,
   *                    sorted.
   */
  public int[] getRowIndices(int rowNumber) {
    if ((rowNumber < 0) || (rowNumber >= this.getNumRows())){
      throw new java.lang.RuntimeException("Column index out of range: " + rowNumber);
    }
    int[] indices = null;
    if (rows.containsKey(rowNumber)) {
      indices = ( (VIntHashSet) rows.get(rowNumber)).toArray();
    }
    if (indices == null) {
      indices = columns.keys();

    }
    Arrays.sort(indices);

    return indices;

  }

  public int[] getRowIndicesUnsorted(int rowNumber) {
    if ((rowNumber < 0) || (rowNumber >= this.getNumRows())){
      throw new java.lang.RuntimeException("Column index out of range: " + rowNumber);
    }
    int[] indices = null;
    if (rows.containsKey(rowNumber)) {
      indices = ( (VIntHashSet) rows.get(rowNumber)).toArray();
    }
    if (indices == null) {
      indices = columns.keys();

    }
    return indices;

  }

  /**
   * Returns the numbers of all the valid columns in this table
   *
   * @return    the indices of all valid columns in this table, sorted
   */
  public int[] getAllColumns() {
    return VHashService.getIndices(columns);
  }

  /**
   * Returns the numbers of all the valid rows in this table
   *
   * @return    the indices of all valid rows in this table, sorted
   */
  public int[] getAllRows() {
    return VHashService.getIndices(rows);
  }


  //===================
  // Protected Methods
  //===================

  /**
   * Copies content of <code> srcTable</code> into this table.
   *
   *
   */
  protected void copy(SparseTable srcTable) {
    copyAttributes(srcTable);

    columns = srcTable.columns.copy();
    rows = srcTable.rows.copy();
    numColumns = srcTable.numColumns;
    numRows = srcTable.numRows;

  }

  /**
   * Copies attributes of <code>srcTable</code> to this table.
   */
  protected void copyAttributes(SparseTable srcTable) {
    setComment(srcTable.getComment());
    setLabel(srcTable.getLabel());
  }

  /**
   * Copies the data that is held at row #<code>position</code> into <code>
   * buffer</code>.
   * Assuming <code>buffer</code> is an array of some type, the data at the row
   * will be converted into that type as needed.
   *
   * If row #<code>pos</code> does not exist - the method does nothing.
   *
       * Note - that since this is a Sparse Table it is very likely that when getRow
   * returns the value stored at <code>buffer[i]</code> is not stored at row
   * <code>position</code> and column <code>i</code> in this table.
   * For more accurate results use getRow(Object, int, int[]).
   *
   *  @param buffer  an array to hold the data at row #<code>position</code>
   *  @param pos     the row number from which the data is being retrieved.
   */
  /* Deprecated
  public void getRow(Object buffer, int position) {

    //validating the row number
    if (!rows.containsKey(position)) {
      return;
    }

    //retrieving the valid columns in the given row.
    VIntHashSet row = ( (VIntHashSet) rows.get(position));
    int[] colNumbers = row.toArray();
    Arrays.sort(colNumbers);
    int size = colNumbers.length;

    //converting the data in the given row as needed

    if (buffer instanceof int[]) {
      int[] b1 = (int[]) buffer;
      for (int i = 0; i < b1.length && i < size; i++) {
        b1[i] = getInt(position, colNumbers[i]);
      }
    }
    else if (buffer instanceof float[]) {
      float[] b1 = (float[]) buffer;
      for (int i = 0; i < b1.length && i < size; i++) {
        b1[i] = getFloat(position, colNumbers[i]);
      }
    }
    else if (buffer instanceof double[]) {
      double[] b1 = (double[]) buffer;
      for (int i = 0; i < b1.length && i < size; i++) {
        b1[i] = getDouble(position, colNumbers[i]);
      }
    }
    else if (buffer instanceof long[]) {
      long[] b1 = (long[]) buffer;
      for (int i = 0; i < b1.length && i < size; i++) {
        b1[i] = getLong(position, colNumbers[i]);
      }
    }
    else if (buffer instanceof short[]) {
      short[] b1 = (short[]) buffer;
      for (int i = 0; i < b1.length && i < size; i++) {
        b1[i] = getShort(position, colNumbers[i]);
      }
    }
    else if (buffer instanceof boolean[]) {
      boolean[] b1 = (boolean[]) buffer;
      for (int i = 0; i < b1.length && i < size; i++) {
        b1[i] = getBoolean(position, colNumbers[i]);
      }
    }
    else if (buffer instanceof String[]) {
      String[] b1 = (String[]) buffer;
      for (int i = 0; i < b1.length && i < size; i++) {
        b1[i] = getString(position, colNumbers[i]);
      }
    }
    else if (buffer instanceof char[][]) {
      char[][] b1 = (char[][]) buffer;
      for (int i = 0; i < b1.length && i < size; i++) {
        b1[i] = getChars(position, colNumbers[i]);
      }
    }
    else if (buffer instanceof byte[][]) {
      byte[][] b1 = (byte[][]) buffer;
      for (int i = 0; i < b1.length && i < size; i++) {
        b1[i] = getBytes(position, colNumbers[i]);
      }
    }
    else if (buffer instanceof Object[]) {
      Object[] b1 = (Object[]) buffer;
      for (int i = 0; i < b1.length && i < size; i++) {
        b1[i] = getObject(position, colNumbers[i]);
      }
    }
    else if (buffer instanceof byte[]) {
      byte[] b1 = (byte[]) buffer;
      for (int i = 0; i < b1.length && i < size; i++) {
        b1[i] = getByte(position, colNumbers[i]);
      }
    }
    else if (buffer instanceof char[]) {
      char[] b1 = (char[]) buffer;
      for (int i = 0; i < b1.length && i < size; i++) {
        b1[i] = getChar(position, colNumbers[i]);
      }
    }

  }
  */
  /**
   * Copies the value at row #<code>position</code> into <code>buffer</code>
   * and copies the number of valid columns in this row into <code>
   * columnsNumbers</code>, such that when this method returns the value
       * <code>buffer[i]</code> is the value that stored at row <code>position</code>
   * and column <codE>columnNumbers[i]</code> in this table.
   *
   * If row #<code>pos</code> does not exist - the method does nothing.
   *
   * to use this method efficiently make sure that buffer and columnNumbers are
   * only and exactly as big as the value <code>getRowNumEntries(int)</code> returns.
   *
       * @param buffer     an array of some type, into which the data at row #<code>
   *                   position</code> is being copied. the values will be
   *                   converted into the type of the array as needed.
   * @param position   the row number from which to retrieve the data.
   * @param columnNumbers    an array of ints, into which the valid column
   *                         numbers of row #position will be copied.
   */
  /* Deprecated
  public void getRow(Object buffer, int position, int[] columnNumbers) {
    if (!rows.containsKey(position)) {
      return;
    }

    getRow(buffer, position);
    int[] arr = ( (VIntHashSet) rows.get(position)).toArray();
    System.arraycopy(arr, 0, columnNumbers, 0, arr.length);
  }
*/
  /**
   * Copies the data of column #<code>pos</code> into <code>buffer</code>.
       * Assuming <code>buffer</code> is an array of some type, the data at the column
   * will be converted into that type as needed.
   *
   * If column #<coe>pos</code> does not exist - the method does nothing.
   *
   * Note - that since this is a Sparse Table if value V resides at buffer[i] at the
   * end of getColumn process - it does not mean V resides at (i, pos) in this table.
   * It is highly recommended to use getColumn(Object, int, int[]) in order to
   * receive a better mapping of values to row numbers.
   *
   * @param buffer  an array to hold the data at column #<code>pos</code>
   * @param pos     the column number to retrieve the data from
   *
   */
/* Deprecated
  public void getColumn(Object buffer, int pos) {
    if (columns.containsKey(pos)) {
      ( (AbstractSparseColumn) columns.get(pos)).getData(buffer);
    }

  }
*/

  /**
   * Copies the values that are stored in column #<code>pos</code> into
   * <code>valuesBuffer</code> according to order of row numbers. also
   * copies the valid row numbers into <code>rowNumbers</code>, such that when
   * getColumn returns <code>valuesBuffer[i]</code> is the value stored at row
   * #<code>rowNumbers[i]<code> and column #<code>pos</code>.
   *
   * If column #<coe>pos</code> does not exist - the method does nothing.
   *
   * to use this method efficiently: valuesBuffer and rowNumbers should be only
   * as big as the value <code>getColumnNumEntries(pos)</code> returns.
   *
       * @param valuesBuffer   must be an array of some type. at the end of the process
   *                       of this method it will hold the values of column
       *                       pos. the values will be converted into the type of the
   *                       array.
   * @param pos            the column number from which the data is retrieved
   * @param rowNumber      an int array to hold the valid row numbers in
   *                       column #<code>pos</code>.
   */
/* Deprecated
  public void getColumn(Object valuesBuffer, int pos, int[] rowNumbers) {
    if (!columns.containsKey(pos)) {
      return;
    }
    getColumn(valuesBuffer, pos);
    int[] arr = getColumnIndices(pos);
    System.arraycopy(arr, 0, rowNumbers, 0, arr.length);
  }
*/

  /*
   /**
    * Creates a deep copy of this SparseTable.
    * @return  an exact copy of this SparseTable
    *
     public Table copy() {
       SparseTable st;
         try {
             ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos);
             oos.writeObject(this);
             byte buf[] = baos.toByteArray();
             oos.close();
             ByteArrayInputStream bais = new ByteArrayInputStream(buf);
             ObjectInputStream ois = new ObjectInputStream(bais);
             st = (SparseTable)ois.readObject();
             ois.close();
             return  st;
         } catch (Exception e) {
    st = new SparseTable();
    st.copy(this);
             return  st;
         }
     }//copy
     /**
         * Returns a subset of this table, containing data from rows <code>start</code>
     * through <code>start+len</code>.
     *
     * @param start   row number from which the subset starts.
     * @param len     number of consequetive rows to be included in the subset
     * @return        a SparseTable containing rows <code>start</code> through
     *                <code>start+len</code>
      public Table getSubset(int start, int len){
        SparseTable retVal = new SparseTable();
        //because a sparse table cannot be changed.
        SparseMutableTable temp = new SparseMutableTable();
        int[] columnNumbers = columns.keys();
        for (int i=0; i<columnNumbers.length; i++){
         Column subCol = ((Column)columns.get(columnNumbers[i])).getSubset(start, len);
          temp.setColumn(columnNumbers[i], (AbstractSparseColumn)subCol);
        }
        retVal.copy(temp);
        return retVal;
      }//getSubset
        public ExampleTable toExampleTable(){
          return new SparseExampleTable(this);
          }
     */

} //SparseTable
