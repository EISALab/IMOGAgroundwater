package ncsa.d2k.modules.core.datatype.table.sparse;

//==============
// Java Imports
//==============
import java.util.*;
import java.io.*;

//===============
// Other Imports
//===============

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.Column;
import ncsa.d2k.modules.core.datatype.table.sparse.primitivehash.*;
import ncsa.d2k.modules.core.datatype.table.sparse.columns.*;

/**
 * Title:        Sparse Table
 * Description:  Sparse Table projects will implement data structures compatible
 * to the interface tree of Table, for sparsely stored data.
 * Copyright:    Copyright (c) 2002
 * Company:      ncsa
 * @author vered goren
 * @version 1.0
 */

/**
 * Sparse Mutable Table is just like a Sparse Table with one difference: Sparse
 * Mutable Table has a mutable functionality. It provides the user with methods
 * that chnage and manipulate the entries of the Table.
 */
public class SparseMutableTable
    extends SparseTable
    implements MutableTable {

  //==============
  // Data Members
  //==============

  /** list of transformations performed. */
  protected ArrayList transformations = new ArrayList();

  //================
  // Constructor(s)
  //================

  public SparseMutableTable() {
    this(0, 0);
  }

  public SparseMutableTable(int numRows, int numCols) {
    super(numRows, numCols);
    transformations = new ArrayList();
    numColumns = VHashService.getMaxKey(columns) + 1;
  }

  /**
   * instantiate this table with the content of <code>T</codE>
   * creates a shallow mutable copy of <codE>T</code>
   */
  public SparseMutableTable(SparseTable T) {
    super(T);
    transformations = new ArrayList();
  }

  /**
   * table has 3 columns. first - row number, second - column number, third -
       * value. iterate over the rows, for each row - put the value at the third column
   * in row no. of the value of the first column and column no. of the value of
   * the second column.
   *
   * for debugging means!.
   *
     public SparseMutableTable(Table table){
    this(0,0);
    int rowNum = table.getNumRows();
    for (int i=0; i<rowNum; i++){
      setString(table.getString(i,2), table.getInt(i,0), table.getInt(i,1));
    }
     }*/


  //================
  // Static Methods
  //================

  /**
   * Returns a subset of <code>table</code> consisted of rows no.
   * <code>start</code> through row no. <code>start+len</codE>.
   *
   * @param start   row number from which the subset starts.
   * @param len     number of consequetive rows to be included in the subset
   * @table         the table from which the subset is retrieved.
   * @return        a SparseTable containing rows <code>start</code> through
   *                <code>start+len</code>
   */
  public static Table getSubset(int start, int len, SparseTable table) {

    int numrows = table.getNumRows();
    if ( (start < 0) || ( (start + len - 1) >= numrows)) {
      throw new IndexOutOfBoundsException("Some part of the range \"start to start+len-1\" is not in range of rows of the table.");
    }

    SparseMutableTable retVal = new SparseMutableTable();

    //XIAOLEI

    int[] columnNumbers = table.columns.keys();
    for (int i = 0; i < columnNumbers.length; i++) {
      //System.out.println("Working on column " + i + ", " + columnNumbers[i]);
      Column subCol = ( (Column) table.columns.get(columnNumbers[i])).getSubset(
          start, len);
      //System.out.println("Finished column " + i + ": " + subCol.getNumEntries());
      //System.out.println();
      retVal.setColumn(columnNumbers[i], (AbstractSparseColumn) subCol);
    }

    retVal.copyAttributes(table);
    retVal.computeNumColumns();
    retVal.computeNumRows();
    return retVal;

  } //getSubset


  //==========================================================================
  // Table Interface Methods
  //==========================================================================

  /**
   * Returns a subset of this table, containing data from rows <code>start</code>
   * through <code>start+len</code>.
   *
   * @param start   row number from which the subset starts.
   * @param len     number of consequetive rows to be included in the subset
   * @return        a SparseTable containing rows <code>start</code> through
   *                <code>start+len</code>
   */
  public Table getSubset(int start, int len) {
    return getSubset(start, len, this);
  } //getSubset

  public Table getSubset(int[] rows) {
    return getSubset(rows, this);
  }

  /**
   * Returns a deep copy of this table (except of transformation).
   */
  public Table copy() {

    SparseMutableTable retVal;
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(baos);
      oos.writeObject(this);
      byte buf[] = baos.toByteArray();
      oos.close();
      ByteArrayInputStream bais = new ByteArrayInputStream(buf);
      ObjectInputStream ois = new ObjectInputStream(bais);
      retVal = (SparseMutableTable) ois.readObject();
      ois.close();
      return retVal;
    } //try

    catch (Exception e) {
      retVal = new SparseMutableTable();
      retVal.copy(this);
      retVal.transformations = (ArrayList) transformations.clone();
      return retVal;
    } //catch
  }

  public Table copy(int[] rows) {
    return getSubset(rows, this);
  }

  public Table copy(int start, int len) {
    return getSubset(start, len, this);
  }

  public MutableTable createTable() {
    SparseMutableTable retVal = new SparseMutableTable();
    return (MutableTable) retVal;
  }

  public Table shallowCopy() {
    SparseMutableTable new_table = new SparseMutableTable();
    new_table.setLabel(this.getLabel());
    new_table.setComment(this.getComment());
    for (int i = 0, n = this.getNumColumns(); i < n; i++) {
      new_table.addColumn( (AbstractSparseColumn) getColumn(i));
    }
    return new_table;
  }

  public Row getRow() {
    return null;
  }

  /**
   * Returns an ExampleTable with the content of this table.
   */
  public ExampleTable toExampleTable() {
    return new SparseExampleTable(this);
  }

  //=========================================================================
  // MutableTable Interface Methods
  //=========================================================================

  public void setColumn(Column newColumn, int position) {
    AbstractSparseColumn col = null;

    switch (newColumn.getType()) {

      case ColumnTypes.BOOLEAN:
        col = new SparseBooleanColumn( (boolean[]) newColumn.getInternal());
        break;

      case ColumnTypes.BYTE:
        col = new SparseByteColumn( (byte[]) newColumn.getInternal());
        break;

      case ColumnTypes.CHAR:
        col = new SparseCharColumn( (char[]) newColumn.getInternal());
        break;

      case ColumnTypes.DOUBLE:
        col = new SparseDoubleColumn( (double[]) newColumn.getInternal());
        break;

      case ColumnTypes.FLOAT:
        col = new SparseFloatColumn( (float[]) newColumn.getInternal());
        break;

      case ColumnTypes.BYTE_ARRAY:
        col = new SparseByteArrayColumn( (byte[][]) newColumn.getInternal());
        break;

      case ColumnTypes.CHAR_ARRAY:
        col = new SparseCharArrayColumn( (char[][]) newColumn.getInternal());
        break;

      case ColumnTypes.INTEGER:
        col = new SparseIntColumn( (int[]) newColumn.getInternal());
        break;

      case ColumnTypes.LONG:
        col = new SparseLongColumn( (long[]) newColumn.getInternal());
        break;

      case ColumnTypes.SHORT:
        col = new SparseShortColumn( (short[]) newColumn.getInternal());
        break;

      case ColumnTypes.OBJECT:
        col = new SparseObjectColumn( (Object[]) newColumn.getInternal());
        break;

      case ColumnTypes.STRING: //fall through to the default...

      default:
        col = new SparseStringColumn( (String[]) newColumn.getInternal());
        break;

    } //switch case

    setColumn(position, col);

  }

  public void addColumn(Column newColumn) {
    this.insertColumn(newColumn, numColumns);
  }

  public void addColumns(Column[] newColumns) {
    for (int i = 0, n = newColumns.length; i < n; i++) {
      this.addColumn(newColumns[i]);
    }
  }

  public void insertColumn(Column newColumn, int position) {
    AbstractSparseColumn col = null;

    switch (newColumn.getType()) {

      case ColumnTypes.BOOLEAN:
        col = new SparseBooleanColumn( (boolean[]) newColumn.getInternal());
        break;

      case ColumnTypes.BYTE:
        col = new SparseByteColumn( (byte[]) newColumn.getInternal());
        break;

      case ColumnTypes.CHAR:
        col = new SparseCharColumn( (char[]) newColumn.getInternal());
        break;

      case ColumnTypes.DOUBLE:
        col = new SparseDoubleColumn( (double[]) newColumn.getInternal());
        break;

      case ColumnTypes.FLOAT:
        col = new SparseFloatColumn( (float[]) newColumn.getInternal());
        break;

      case ColumnTypes.BYTE_ARRAY:
        col = new SparseByteArrayColumn( (byte[][]) newColumn.getInternal());
        break;

      case ColumnTypes.CHAR_ARRAY:
        col = new SparseCharArrayColumn( (char[][]) newColumn.getInternal());
        break;

      case ColumnTypes.INTEGER:
        col = new SparseIntColumn( (int[]) newColumn.getInternal());
        break;

      case ColumnTypes.LONG:
        col = new SparseLongColumn( (long[]) newColumn.getInternal());
        break;

      case ColumnTypes.SHORT:
        col = new SparseShortColumn( (short[]) newColumn.getInternal());
        break;

      case ColumnTypes.OBJECT:
        col = new SparseObjectColumn( (Object[]) newColumn.getInternal());
        break;

      case ColumnTypes.STRING: //fall through to the default...

      default:
        col = new SparseStringColumn( (String[]) newColumn.getInternal());
        break;

    } //switch case

    insertColumn(col, position);
  }

  public void insertColumns(Column[] newColumns, int position) {
    for (int i = newColumns.length - 1; i >= 0; i--) {
      this.insertColumn(newColumns[i], position);
    }
  }

  /**
   * Removes column no. <code>position</code> from this table.
   *
   * @param position    the index of the columnt obe removed
   *
   * Modified by Xiaolei - 07/08/2003.
   */
  public void removeColumn(int position) {

    int ncols = VHashService.getMaxKey(columns) + 1;
    if ( (position < 0) || (position >= ncols)) {
      throw new IndexOutOfBoundsException();
    }

    //removing the column
    AbstractSparseColumn col = (AbstractSparseColumn) columns.remove(position);

    //if such column did exist
    if (col != null) {

      //removing the references to it from each row
      int[] rowsIndices = col.getIndices();

      for (int i = 0; i < rowsIndices.length; i++) {
        if (rows.containsKey(rowsIndices[i])) {
          ( (VIntHashSet) rows.get(rowsIndices[i])).remove(position);
        }
      }

      // for the columns with indices larger than position, shift all of
      // them leftward.

      int[] col_keys = columns.keys();
      Arrays.sort(col_keys);

      for (int i = 0; i < col_keys.length; i++) {
        if (col_keys[i] >= position) {
          col = (AbstractSparseColumn) columns.remove(col_keys[i]);
          columns.put(col_keys[i] - 1, col);
        }
      }

      rowsIndices = getAllRows();

      // shift each row's columns leftward
      for (int i = 0; i < rowsIndices.length; i++) {
        ( (VIntHashSet) rows.get(rowsIndices[i])).decrement(position);
      }
    }

    numColumns = VHashService.getMaxKey(columns) + 1;
  }

  /**
   * Removes all column from index <code>start</code> to index <code>start + len</code>
   *
   * @param start     the beginning index for removing the columns
   * @param len       number of consequetive columns to be removed.
   */
  public void removeColumns(int start, int len) {
    for (int i = 0; i < len; i++) {
      removeColumn(start);
    }
  }

  public void addRows(int add_num_rows) {
    numRows += add_num_rows;
  }

  /**
   * Removes row no. <code>row</code> from this table
   *
   * @param row     the index of the row to be removed.
   *
   * Modified by Xiaolei - 07/08/2003.
   */
  public void removeRow(int row) {
    //removing the row from the rows map
    VIntHashSet set = (VIntHashSet) rows.remove(row);

    //if the row existed
    if (set != null) {
      //retrieve column numbers
      int[] columnNumbers = set.toArray();

      //remove the items of the row from each column
      for (int i = 0; i < columnNumbers.length; i++) {
        ( (AbstractSparseColumn) columns.get(columnNumbers[i])).removeRow(row);
        //	removeEmptyColumn(columnNumbers[i]);
      }

      // shift all the rest of the rows upward
      int[] row_keys = rows.keys();
      Arrays.sort(row_keys);

      for (int i = 0; i < row_keys.length; i++) {
        if (row_keys[i] >= row) {
          set = (VIntHashSet) rows.remove(row_keys[i]);
          rows.put(row_keys[i] - 1, set);
        }
      }

      int[] colsIndices = getAllColumns();
      AbstractSparseColumn col;

      // shift each column's values upward
      for (int i = 0; i < colsIndices.length; i++) {
        col = (AbstractSparseColumn) columns.get(colsIndices[i]);
        int[] validRows = col.getIndices();

        for (int j = 0; j < validRows.length; j++) {
          if (validRows[j] >= row) {
            col.replaceRow(col.removeRow(validRows[j]), validRows[j] - 1);
          }
        }
      }
    } //end if

    computeNumRows();
  }

  /**
   * Removes all rows from index <code>start</code> to index <code>start + len</code>
   *
   * @param start     the beginning index for removing the rows
   * @param len       number of consequetive rows to be removed.
   */
  public void removeRows(int start, int len) {
    for (int i = 0; i < len; i++) {
      removeRow(start);
    }
  }

  /**
   * Reorders the rows in this table, s.t.:
   * If the row numbers were sorted in an array - "row" then when this method returns
   * <code>row[i]</code> will hold the row that was originally held by <code>
   * newORder[i]</code>.
   *
       * @param newOrder    an array of valid row numbers in this table in a certain
   *                    order.
   */
  public Table reorderRows(int[] newOrder) {

    VIntIntHashMap order = VHashService.toMap(newOrder, rows);
    return reorderRows(order);
    /*
        int[] rowIndices = getAllRows();
         VIntObjectHashMap tempMap = new VIntObjectHashMap (rows.size());
         for (int i=0; i<rowIndices.length && i<newOrder.length; i++)
      if(rows.containsKey(newOrder[i]))
      tempMap.put(rowIndices[i], rows.get(newOrder[i]));
         SparseMutableTable retVal = new SparseMutableTable();
         retVal.rows = tempMap;
         retVal.columns = (VIntObjectHashMap)columns.copy();
         //reordering the column
         int[] colIndices = getAllColumns();
         for (int i=0; i<colIndices.length; i++)
      ((AbstractSparseColumn) retVal.columns.get(colIndices[i])).reorderRows(newOrder);
     */

  }

  /**
   * Reorders the columns in this table, s.t.:
   * If the column numbers were sorted in an array - "col" then when this method returns
       * <code>col[i]</code> will hold the column that was originally held by <code>
   * newORder[i]</code>.
   *
       * @param newOrder    an array of valid column numbers in this table in a certain
   *                    order.
   */
  public Table reorderColumns(int[] newOrder) {
    VIntIntHashMap order = VHashService.toMap(newOrder, columns);

    return reorderColumns(order);

  }

  /**
   * Swaps rows no. <code>pos1</code> and <code>pos2</code>
   *
   * @param pos1    the index of the first row to be swapped
   * @param pos2    the index of the second row to be swapped.
   */
  public void swapRows(int pos1, int pos2) {

    //retrieve all the column numbers that hold any of these rows

    VIntHashSet r1 = (VIntHashSet) rows.remove(pos1);
    VIntHashSet r2 = (VIntHashSet) rows.remove(pos2);
    VIntHashSet tempSet = new VIntHashSet(); //will be a combination of r1 and r2
    int[] validColumn = null; //will hold the relative oclumn numbers

    if (r1 != null) { //if row pos1 exists
      rows.put(pos2, r1); //put it at pos2
      tempSet.addAll(r1.toArray()); //add its valid columns to tempSet
    }

    if (r2 != null) { //if row pos2 exists
      rows.put(pos1, r2); //put it at pos1
      tempSet.addAll(r2.toArray()); //add its valid columns to tempSet
    }

    validColumn = tempSet.toArray(); //now validColumns hold all the neede indices
    //for each valid column in those 2 rows
    for (int i = 0; i < validColumn.length; i++) {

      //swap the rows.
      ( (Column) columns.get(validColumn[i])).swapRows(pos1, pos2);
    }

  }

  /**
   * Swaps columns no. <code>pos1</code> and <code>pos2</code>
   *
   * @param pos1    the index of the first column to be swapped
   * @param pos2    the index of the second column to be swapped.
   */
  public void swapColumns(int pos1, int pos2) {
    Column col1 = (Column) columns.remove(pos1);
    Column col2 = (Column) columns.remove(pos2);

    if (col1 != null) {
      columns.put(pos2, col1);

    }
    if (col2 != null) {
      columns.put(pos1, col2);

    }
  }

  /**
   * Set entry <code>(row, column)</code> to hold the data represented by
   * <code>element</code>
   *
   * @param element      the new data for entry (row, column).
   * @param row          the row number of the entry to be set
   * @param column       the column number of the entry to be set
   */
  public void setObject(Object element, int row, int column) {

    if (!columns.containsKey(column)) {
      addColumn(column, ColumnTypes.OBJECT);
    }
    getColumn(column).setObject(element, row);
    addCol2Row(column, row);

    if (numRows <= row) {
      numRows = row + 1;
    }
    if (numColumns <= column) {
      numColumns = column + 1;

    }
  }

  /**
   * Set entry <code>(row, column)</code> to hold the data represented by
   * <code>element</code>
   *
   * @param element      the new data for entry (row, column).
   * @param row          the row number of the entry to be set
   * @param column       the column number of the entry to be set
   */
  public void setInt(int data, int row, int column) {

    if (!columns.containsKey(column)) {
      addColumn(column, ColumnTypes.INTEGER);
    }
    getColumn(column).setInt(data, row);
    addCol2Row(column, row);

    if (numRows <= row) {
      numRows = row + 1;
    }
    if (numColumns <= column) {
      numColumns = column + 1;

    }
  }

  /**
   * Set entry <code>(row, column)</code> to hold the data represented by
   * <code>element</code>
   *
   * @param element      the new data for entry (row, column).
   * @param row          the row number of the entry to be set
   * @param column       the column number of the entry to be set
   */
  public void setShort(short data, int row, int column) {

    if (!columns.containsKey(column)) {
      addColumn(column, ColumnTypes.SHORT);
    }
    getColumn(column).setShort(data, row);
    addCol2Row(column, row);

    if (numRows <= row) {
      numRows = row + 1;
    }
    if (numColumns <= column) {
      numColumns = column + 1;

    }
  }

  /**
   * Set entry <code>(row, column)</code> to hold the data represented by
   * <code>element</code>
   *
   * @param element      the new data for entry (row, column).
   * @param row          the row number of the entry to be set
   * @param column       the column number of the entry to be set
   */
  public void setFloat(float data, int row, int column) {

    if (!columns.containsKey(column)) {
      addColumn(column, ColumnTypes.FLOAT);
    }
    getColumn(column).setFloat(data, row);
    addCol2Row(column, row);

    if (numRows <= row) {
      numRows = row + 1;
    }
    if (numColumns <= column) {
      numColumns = column + 1;

    }
  }

  /**
   * Set entry <code>(row, column)</code> to hold the data represented by
   * <code>element</code>
   *
   * @param element      the new data for entry (row, column).
   * @param row          the row number of the entry to be set
   * @param column       the column number of the entry to be set
   */
  public void setDouble(double data, int row, int column) {
    // XIAOLEI - just added some comments

    /* does the column exist in the entire table? */
    if (!columns.containsKey(column)) {
      addColumn(column, ColumnTypes.DOUBLE);

      /* set the value */
    }
    getColumn(column).setDouble(data, row);

    /* now make that row see this newly added column */
    addCol2Row(column, row);

    /* in case this newly added value expands the entire table */
    if (numRows <= row) {
      numRows = row + 1;
    }
    if (numColumns <= column) {
      numColumns = column + 1;
    }
  }

  /**
   * Set entry <code>(row, column)</code> to hold the data represented by
   * <code>element</code>
   *
   * @param element      the new data for entry (row, column).
   * @param row          the row number of the entry to be set
   * @param column       the column number of the entry to be set
   */
  public void setLong(long data, int row, int column) {

    if (!columns.containsKey(column)) {
      addColumn(column, ColumnTypes.LONG);
    }
    getColumn(column).setLong(data, row);
    addCol2Row(column, row);

    if (numRows <= row) {
      numRows = row + 1;
    }
    if (numColumns <= column) {
      numColumns = column + 1;

    }
  }

  /**
   * Set entry <code>(row, column)</code> to hold the data represented by
   * <code>element</code>
   *
   * @param element      the new data for entry (row, column).
   * @param row          the row number of the entry to be set
   * @param column       the column number of the entry to be set
   */
  public void setString(String data, int row, int column) {

    if (!columns.containsKey(column)) {
      addColumn(column, ColumnTypes.STRING);
    }
    getColumn(column).setString(data, row);
    addCol2Row(column, row);

    if (numRows <= row) {
      numRows = row + 1;
    }
    if (numColumns <= column) {
      numColumns = column + 1;

    }
  }

  /**
   * Set entry <code>(row, column)</code> to hold the data represented by
   * <code>element</code>
   *
   * @param element      the new data for entry (row, column).
   * @param row          the row number of the entry to be set
   * @param column       the column number of the entry to be set
   */
  public void setBytes(byte[] data, int row, int column) {

    if (!columns.containsKey(column)) {
      addColumn(column, ColumnTypes.BYTE_ARRAY);
    }
    getColumn(column).setBytes(data, row);
    addCol2Row(column, row);

    if (numRows <= row) {
      numRows = row + 1;
    }
    if (numColumns <= column) {
      numColumns = column + 1;

    }
  }

  /**
   * Set entry <code>(row, column)</code> to hold the data represented by
   * <code>element</code>
   *
   * @param element      the new data for entry (row, column).
   * @param row          the row number of the entry to be set
   * @param column       the column number of the entry to be set
   */
  public void setBoolean(boolean data, int row, int column) {
    if (!columns.containsKey(column)) {
      addColumn(column, ColumnTypes.BOOLEAN);
    }
    getColumn(column).setBoolean(data, row);
    addCol2Row(column, row);

    if (numRows <= row) {
      numRows = row + 1;
    }
    if (numColumns <= column) {
      numColumns = column + 1;

    }
  }

  /**
   * Set entry <code>(row, column)</code> to hold the data represented by
   * <code>element</code>
   *
   * @param element      the new data for entry (row, column).
   * @param row          the row number of the entry to be set
   * @param column       the column number of the entry to be set
   */
  public void setChars(char[] data, int row, int column) {

    if (!columns.containsKey(column)) {
      addColumn(column, ColumnTypes.CHAR_ARRAY);
    }
    getColumn(column).setChars(data, row);
    addCol2Row(column, row);

    if (numRows <= row) {
      numRows = row + 1;
    }
    if (numColumns <= column) {
      numColumns = column + 1;

    }
  }

  /**
   * Set entry <code>(row, column)</code> to hold the data represented by
   * <code>element</code>
   *
   * @param element      the new data for entry (row, column).
   * @param row          the row number of the entry to be set
   * @param column       the column number of the entry to be set
   */
  public void setByte(byte data, int row, int column) {

    if (!columns.containsKey(column)) {
      addColumn(column, ColumnTypes.BYTE);
    }
    getColumn(column).setByte(data, row);
    addCol2Row(column, row);

    if (numRows <= row) {
      numRows = row + 1;
    }
    if (numColumns <= column) {
      numColumns = column + 1;

    }
  }

  /**
   * Set entry <code>(row, column)</code> to hold the data represented by
   * <code>element</code>
   *
   * @param element      the new data for entry (row, column).
   * @param row          the row number of the entry to be set
   * @param column       the column number of the entry to be set
   */
  public void setChar(char data, int row, int column) {

    if (!columns.containsKey(column)) {
      addColumn(column, ColumnTypes.CHAR);
    }
    getColumn(column).setChar(data, row);
    addCol2Row(column, row);

    if (numRows <= row) {
      numRows = row + 1;
    }
    if (numColumns <= column) {
      numColumns = column + 1;

    }
  }

  /**
       * Sets <code>label</code> to be the label of column no. <code>position</code>
   *
   * @param label     the new label to be set
   * @param position  the column index which its label is being set
   */
  public void setColumnLabel(String label, int position) {
    if (columns.containsKey(position)) {
      ( (AbstractSparseColumn) columns.get(position)).setLabel(label);
    }
  }

  /**
   * Sets <code>comment</code> to be the comment of column no. <code>position</code>
   *
   * @param comment     the new comment to be set
   * @param position    the column index which its comment is being set
   */
  public void setColumnComment(String comment, int position) {
    if (columns.containsKey(position)) {
      ( (AbstractSparseColumn) columns.get(position)).setComment(comment);
    }
  }

  /**
       * Sorts this Table according to the natural order of Column no. <code>col</code>
   *
   * @param col      the index of the Column according to which this table is to be sorted
   */
  public void sortByColumn(int col) {
    Column sorting = getColumn(col);
    if (sorting != null) {
      sorting.sort(this);
    }
  }

  /**
   * Sorts rows no. <code>begin</code> through <code>end</code> of this Table
   * according to the natural order of Column no. <code>col</code>
   *
   * @param col      the index of the Column according to which this table is to be sorted
   * @param begin     the row index at which begins the section to be sorted.
   * @param edn       the row number at which ends the section to be sorted.
   */
  public void sortByColumn(int col, int begin, int end) {

    if (end < begin) {
      return;
    }

    Column sorting = getColumn(col);
    if (sorting != null) {
      sorting.sort(this, begin, end);

    }
  }

  /**
   * Adds <code>tm</code> to the list of the transformations.
   *
   * @param tm    a transformation that was operated on this table
   */
  public void addTransformation(Transformation tm) {
    transformations.add(tm);
  }

  /**
   * Returns the list of transformation operated on this table
   */
  public List getTransformations() {
    return transformations;
  }

  /**
   * Sets the value at position (row, col) in this table to be a missing
   * value if val is true. if val is false - remove entry <code>row</cdoe> from
   * list no. <code>col</code> of missing values.
   *
   * @param val     indicates if to set the value to missing (true) or to reset
   *                it to a regular value (false).
   * @param row     the row index of the value to be set
   * @param col     the column index of the value to be set.
   */
  public void setValueToMissing(boolean val, int row, int col) {
    if (columns.containsKey(col)) {
      ( (AbstractSparseColumn) columns.get(col)).setValueToMissing(val, row);
    }
  }

  /**
   * Sets the value at position (row, col) in this table to be an empty
   * value.
   *
   * @param row     the row index of the value to be set
   * @param col     the column index of the value to be set.
   */
  public void setValueToEmpty(boolean val, int row, int col) {
    if (columns.containsKey(col)) {
      ( (AbstractSparseColumn) columns.get(col)).setValueToEmpty(val, row);
    }
  }


  //===========================================================================
  //===========================================================================
  //                 END MUTABLETABLE INTERFACE
  //===========================================================================
  //===========================================================================


  //================
  // Public Methods
  //================

  /**
   * Sorts this table according to <codE>newORder</code>
   *
   * @param newOrder  an int to int hashmap that defines the new order of the
   *                  rows of this table, s.t.: for each pair (key, value) in
       *                  <code>newOrder</code> put row no. <code>val</code> in row no.
   *                  <code>key</code>.
   */
  public void sort(VIntIntHashMap newOrder) {
    SparseMutableTable temp = (SparseMutableTable) reorderRows(newOrder);
    columns = temp.columns;
    rows = temp.rows;
  }


  //DUANE - added method below for testing purposes

  public boolean equals(Object mt) {
    SparseMutableTable mti = (SparseMutableTable) mt;
    int numColumns = mti.getNumColumns();
    int numRows = mti.getNumRows();
    if (getNumColumns() != numColumns)
      return false;
    if (getNumRows() != numRows)
      return false;
    for (int i = 0; i < numRows; i++)
      for (int j = 0; j < numColumns; j++)
        if (!getString(i, j).equals(mti.getString(i, j)))
          return false;
    return true;
  }


  //===================
  // Protected Methods
  //===================

  /**
   * **************************************************
   * AddRow methods
   * **************************************************
   */

  /**
   * Adds the data in <code>newEntry</code> to the end of this column.
   *
   * Note: Since this is a Sparse Table the data is not added to all the column
   * zero through the size of newEntry, but only to the valid columns in this
   * table.
   *
   * It is more accurate to use addRow(int[], int[]) method.
   *
   * @param newEntry    an int array that holds data to be inserted to a new
   *                    row at the end of this table.
   *
   */
  protected void addRow(int[] newEntry) {
    int[] validColumns = getAllColumns();

    addRow(newEntry, validColumns);
  }

  /**
   * Adds the data in <code>newEntry</codE> to a new row at the end of this
   * table. each item <code>newEntry[i]</code> will be inserted at column no.
   * <code>validColumns[i]</code> in the new row.
   *
   * @param newEntry    holds the data to be inserted into the table.
   * @param validColumns  indicates to which columns the data will be inserted.
   */
  protected void addRow(int[] newEntry, int[] validColumns) {
    addRow(newEntry, numRows, validColumns);

  }

  /**
   * Adds the data in <code>newEntry</code> to the end of this column.
   *
   * Note: Since this is a Sparse Table the data is not added to all the column
   * zero through the size of newEntry, but only to the valid columns in this
   * table.
   *
   * It is more accurate to use addRow(float[], int[]) method.
   *
   * @param newEntry    a float array that holds data to be inserted to a new
   *                    row at the end of this table.
   *
   */
  protected void addRow(float[] newEntry) {
    int[] validColumns = getAllColumns();

    addRow(newEntry, validColumns);
  }

  /**
   * Adds the data in <code>newEntry</codE> to a new row at the end of this
   * table. each item <code>newEntry[i]</code> will be inserted at column no.
   * <code>validColumns[i]</code> in the new row.
   *
   * @param newEntry    holds the data to be inserted into the table.
   * @param validColumns  indicates to which columns the data will be inserted.
   */
  protected void addRow(float[] newEntry, int[] validColumns) {
    addRow(newEntry, numRows, validColumns);
  }

  /**
   * Adds the data in <code>newEntry</code> to the end of this column.
   *
   * Note: Since this is a Sparse Table the data is not added to all the column
   * zero through the size of newEntry, but only to the valid columns in this
   * table.
   *
   * It is more accurate to use addRow(double[], int[]) method.
   *
   * @param newEntry    a double array that holds data to be inserted to a new
   *                    row at the end of this table.
   *
   */
  protected void addRow(double[] newEntry) {
    int[] validColumns = getAllColumns();
    addRow(newEntry, validColumns);
  }

  /**
   * Adds the data in <code>newEntry</codE> to a new row at the end of this
   * table. each item <code>newEntry[i]</code> will be inserted at column no.
   * <code>validColumns[i]</code> in the new row.
   *
   * @param newEntry    holds the data to be inserted into the table.
   * @param validColumns  indicates to which columns the data will be inserted.
   */
  protected void addRow(double[] newEntry, int[] validColumns) {
    addRow(newEntry, numRows, validColumns);

  }

  /**
   * Adds the data in <code>newEntry</code> to the end of this column.
   *
   * Note: Since this is a Sparse Table the data is not added to all the column
   * zero through the size of newEntry, but only to the valid columns in this
   * table.
   *
   * It is more accurate to use addRow(long[], int[]) method.
   *
   * @param newEntry    a long array that holds data to be inserted to a new
   *                    row at the end of this table.
   *
   */
  protected void addRow(long[] newEntry) {
    int[] validColumns = getAllColumns();
    addRow(newEntry, validColumns);
  }

  /**
   * Adds the data in <code>newEntry</codE> to a new row at the end of this
   * table. each item <code>newEntry[i]</code> will be inserted at column no.
   * <code>validColumns[i]</code> in the new row.
   *
   * @param newEntry    holds the data to be inserted into the table.
   * @param validColumns  indicates to which columns the data will be inserted.
   */
  protected void addRow(long[] newEntry, int[] validColumns) {
    addRow(newEntry, numRows, validColumns);

  }

  /**
   * Adds the data in <code>newEntry</code> to the end of this column.
   *
   * Note: Since this is a Sparse Table the data is not added to all the column
   * zero through the size of newEntry, but only to the valid columns in this
   * table.
   *
   * It is more accurate to use addRow(short[], int[]) method.
   *
   * @param newEntry    a short array that holds data to be inserted to a new
   *                    row at the end of this table.
   *
   */
  protected void addRow(short[] newEntry) {
    int[] validColumns = getAllColumns();
    addRow(newEntry, validColumns);
  }

  /**
   * Adds the data in <code>newEntry</codE> to a new row at the end of this
   * table. each item <code>newEntry[i]</code> will be inserted at column no.
   * <code>validColumns[i]</code> in the new row.
   *
   * @param newEntry    holds the data to be inserted into the table.
   * @param validColumns  indicates to which columns the data will be inserted.
   */
  protected void addRow(short[] newEntry, int[] validColumns) {
    addRow(newEntry, numRows, validColumns);

  }

  /**
   * Adds the data in <code>newEntry</code> to the end of this column.
   *
   * Note: Since this is a Sparse Table the data is not added to all the column
   * zero through the size of newEntry, but only to the valid columns in this
   * table.
   *
   * It is more accurate to use addRow(boolean[], int[]) method.
   *
   * @param newEntry    a boolean array that holds data to be inserted to a new
   *                    row at the end of this table.
   *
   */
  protected void addRow(boolean[] newEntry) {
    int[] validColumns = getAllColumns();
    addRow(newEntry, validColumns);
  }

  /**
   * Adds the data in <code>newEntry</codE> to a new row at the end of this
   * table. each item <code>newEntry[i]</code> will be inserted at column no.
   * <code>validColumns[i]</code> in the new row.
   *
   * @param newEntry    holds the data to be inserted into the table.
   * @param validColumns  indicates to which columns the data will be inserted.
   */
  protected void addRow(boolean[] newEntry, int[] validColumns) {
    addRow(newEntry, numRows, validColumns);

  }

  /**
   * Adds the data in <code>newEntry</code> to the end of this column.
   *
   * Note: Since this is a Sparse Table the data is not added to all the column
   * zero through the size of newEntry, but only to the valid columns in this
   * table.
   *
   * It is more accurate to use addRow(String[], int[]) method.
   *
   * @param newEntry    a String array that holds data to be inserted to a new
   *                    row at the end of this table.
   *
   */
  protected void addRow(String[] newEntry) {
    int[] validColumns = getAllColumns();
    addRow(newEntry, validColumns);
  }

  /**
   * Adds the data in <code>newEntry</codE> to a new row at the end of this
   * table. each item <code>newEntry[i]</code> will be inserted at column no.
   * <code>validColumns[i]</code> in the new row.
   *
   * @param newEntry    holds the data to be inserted into the table.
   * @param validColumns  indicates to which columns the data will be inserted.
   */
  protected void addRow(String[] newEntry, int[] validColumns) {
    addRow(newEntry, numRows, validColumns);

  }

  /**
   * Adds the data in <code>newEntry</code> to the end of this column.
   *
   * Note: Since this is a Sparse Table the data is not added to all the column
   * zero through the size of newEntry, but only to the valid columns in this
   * table.
   *
   * It is more accurate to use addRow(char[][], int[]) method.
   *
   * @param newEntry    a char[] array that holds data to be inserted to a new
   *                    row at the end of this table.
   *
   */
  protected void addRow(char[][] newEntry) {
    int[] validColumns = getAllColumns();
    addRow(newEntry, validColumns);
  }

  /**
   * Adds the data in <code>newEntry</codE> to a new row at the end of this
   * table. each item <code>newEntry[i]</code> will be inserted at column no.
   * <code>validColumns[i]</code> in the new row.
   *
   * @param newEntry    holds the data to be inserted into the table.
   * @param validColumns  indicates to which columns the data will be inserted.
   */
  protected void addRow(char[][] newEntry, int[] validColumns) {
    addRow(newEntry, numRows, validColumns);

  }

  /**
   * Adds the data in <code>newEntry</code> to the end of this column.
   *
   * Note: Since this is a Sparse Table the data is not added to all the column
   * zero through the size of newEntry, but only to the valid columns in this
   * table.
   *
   * It is more accurate to use addRow(byte[][], int[]) method.
   *
   * @param newEntry    a byte[] array that holds data to be inserted to a new
   *                    row at the end of this table.
   *
   */
  protected void addRow(byte[][] newEntry) {
    int[] validColumns = getAllColumns();
    addRow(newEntry, validColumns);
  }

  /**
   * Adds the data in <code>newEntry</codE> to a new row at the end of this
   * table. each item <code>newEntry[i]</code> will be inserted at column no.
   * <code>validColumns[i]</code> in the new row.
   *
   * @param newEntry    holds the data to be inserted into the table.
   * @param validColumns  indicates to which columns the data will be inserted.
   */
  protected void addRow(byte[][] newEntry, int[] validColumns) {
    addRow(newEntry, numRows, validColumns);

  }

  /**
   * Adds the data in <code>newEntry</code> to the end of this column.
   *
   * Note: Since this is a Sparse Table the data is not added to all the column
   * zero through the size of newEntry, but only to the valid columns in this
   * table.
   *
   * It is more accurate to use addRow(Object, int[]) method.
   *
   * @param newEntry    an Object array that holds data to be inserted to a new
   *                    row at the end of this table.
   */
  protected void addRow(Object[] newEntry) {
    int[] validColumns = getAllColumns();
    addRow(newEntry, validColumns);
  }

  /**
   * Adds the data in <code>newEntry</codE> to a new row at the end of this
   * table. each item <code>newEntry[i]</code> will be inserted at column no.
   * <code>validColumns[i]</code> in the new row.
   *
   * @param newEntry    holds the data to be inserted into the table.
   * @param validColumns  indicates to which columns the data will be inserted.
   */
  protected void addRow(Object[] newEntry, int[] validColumns) {
    addRow(newEntry, numRows, validColumns);

  }

  /**
   * Adds the data in <code>newEntry</code> to the end of this column.
   *
   * Note: Since this is a Sparse Table the data is not added to all the column
   * zero through the size of newEntry, but only to the valid columns in this
   * table.
   *
   * It is more accurate to use addRow(byte[], int[]) method.
   *
   * @param newEntry    a byte array that holds data to be inserted to a new
   *                    row at the end of this table.
   *
   */
  protected void addRow(byte[] newEntry) {
    int[] validColumns = getAllColumns();
    addRow(newEntry, validColumns);
  }

  /**
   * Adds the data in <code>newEntry</codE> to a new row at the end of this
   * table. each item <code>newEntry[i]</code> will be inserted at column no.
   * <code>validColumns[i]</code> in the new row.
   *
   * @param newEntry    holds the data to be inserted into the table.
   * @param validColumns  indicates to which columns the data will be inserted.
   */
  protected void addRow(byte[] newEntry, int[] validColumns) {
    addRow(newEntry, numRows, validColumns);

  }

  /**
   * Adds the data in <code>newEntry</code> to the end of this column.
   *
   * Note: Since this is a Sparse Table the data is not added to all the column
   * zero through the size of newEntry, but only to the valid columns in this
   * table.
   *
   * It is more accurate to use addRow(char[], int[]) method.
   *
   * @param newEntry    a char array that holds data to be inserted to a new
   *                    row at the end of this table.
   *
   */
  protected void addRow(char[] newEntry) {
    int[] validColumns = getAllColumns();
    addRow(newEntry, validColumns);
  }

  /**
   * Adds the data in <code>newEntry</codE> to a new row at the end of this
   * table. each item <code>newEntry[i]</code> will be inserted at column no.
   * <code>validColumns[i]</code> in the new row.
   *
   * @param newEntry    holds the data to be inserted into the table.
   * @param validColumns  indicates to which columns the data will be inserted.
   */
  protected void addRow(char[] newEntry, int[] validColumns) {
    addRow(newEntry, numRows, validColumns);

  }

  /**
   * Adds a new row at index <codE>position</code> with the content of <codE>
   * newEntry</code>, which will be converted to the types of the columns, as
   * needed. Is protected because this method should only be invoked after
   * validation of vacancy of index <code>position</code>
   *
   * @param newEntry      the data to be put in the new row.
   * @param position      the index of the new row
       * @param validColumns  the valid indices of the new row, s.t. newEntry[i] will
       *                      be stored at row <code>position</codE> and column <code>
   *                      validColumns[i]</code> when this method returns.
   */
  protected void addRow(int[] newEntry, int position, int[] validColumns) {
    if (numRows <= position) {
      numRows = position + 1;
    }
    if (numColumns <= validColumns[validColumns.length - 1]) {
      numColumns = validColumns[validColumns.length - 1] + 1;

    }

    int i = 0;
    //for each validColumns[i]
    for (; i < newEntry.length && i < validColumns.length; i++) {
      //if this table does not contain it yet - add a String column
      //     if(!columns.containsKey(validColumns[i]))
//	addDefaultColumn(validColumns[i]);
      //it is now safe to set the boolean value into the table
      setInt(newEntry[i], position, validColumns[i]);
    }

    //in case newEntry has more values than the size of validColumns
    //appending the values at the end of this row.

    for (; i < newEntry.length; i++) {
      //     addDefaultColumn(newCol);                    //adding a column
      setInt(newEntry[i], position, numColumns); //setting the new entry

    }

    //updateRows(position, getRowIndices(position));
  }

  /**
   * Adds a new row at index <codE>position</code> with the content of <codE>
   * newEntry</code>, which will be converted to the types of the columns, as
   * needed. Is protected because this method should only be invoked after
   * validation of vacancy of index <code>position</code>
   *
   * @param newEntry      the data to be put in the new row.
   * @param position      the index of the new row
       * @param validColumns  the valid indices of the new row, s.t. newEntry[i] will
       *                      be stored at row <code>position</codE> and column <code>
   *                      validColumns[i]</code> when this method returns.
   */
  protected void addRow(long[] newEntry, int position, int[] validColumns) {

    if (numRows <= position) {
      numRows = position + 1;
    }
    if (numColumns <= validColumns[validColumns.length - 1]) {
      numColumns = validColumns[validColumns.length - 1] + 1;

    }

    int i = 0;
    //for each validColumns[i]
    for (; i < newEntry.length && i < validColumns.length; i++) {
      //if this table does not contain it yet - add a String column
      //     if(!columns.containsKey(validColumns[i]))
//	addDefaultColumn(validColumns[i]);
      //it is now safe to set the boolean value into the table
      setLong(newEntry[i], position, validColumns[i]);
    }

    //in case newEntry has more values than the size of validColumns
    //appending the values at the end of this row.

    for (; i < newEntry.length; i++) {
      //     addDefaultColumn(newCol);                    //adding a column
      setLong(newEntry[i], position, numColumns); //setting the new entry

    }

    //   updateRows(position, getRowIndices(position));
  }

  /**
   * Adds a new row at index <codE>position</code> with the content of <codE>
   * newEntry</code>, which will be converted to the types of the columns, as
   * needed. Is protected because this method should only be invoked after
   * validation of vacancy of index <code>position</code>
   *
   * @param newEntry      the data to be put in the new row.
   * @param position      the index of the new row
       * @param validColumns  the valid indices of the new row, s.t. newEntry[i] will
       *                      be stored at row <code>position</codE> and column <code>
   *                      validColumns[i]</code> when this method returns.
   */
  protected void addRow(short[] newEntry, int position, int[] validColumns) {

    if (numRows <= position) {
      numRows = position + 1;
    }
    if (numColumns <= validColumns[validColumns.length - 1]) {
      numColumns = validColumns[validColumns.length - 1] + 1;

    }

    int i = 0;
    //for each validColumns[i]
    for (; i < newEntry.length && i < validColumns.length; i++) {
      //if this table does not contain it yet - add a String column
      //     if(!columns.containsKey(validColumns[i]))
//	addDefaultColumn(validColumns[i]);
      //it is now safe to set the boolean value into the table
      setShort(newEntry[i], position, validColumns[i]);
    }

    //in case newEntry has more values than the size of validColumns
    //appending the values at the end of this row.

    for (; i < newEntry.length; i++) {
      //     addDefaultColumn(newCol);                    //adding a column
      setShort(newEntry[i], position, numColumns); //setting the new entry

    }

    //  updateRows(position, getRowIndices(position));
  }

  /**
   * Adds a new row at index <codE>position</code> with the content of <codE>
   * newEntry</code>, which will be converted to the types of the columns, as
   * needed. Is protected because this method should only be invoked after
   * validation of vacancy of index <code>position</code>
   *
   * @param newEntry      the data to be put in the new row.
   * @param position      the index of the new row
       * @param validColumns  the valid indices of the new row, s.t. newEntry[i] will
       *                      be stored at row <code>position</codE> and column <code>
   *                      validColumns[i]</code> when this method returns.
   */
  protected void addRow(Object[] newEntry, int position, int[] validColumns) {

    if (numRows <= position) {
      numRows = position + 1;
    }
    if (numColumns <= validColumns[validColumns.length - 1]) {
      numColumns = validColumns[validColumns.length - 1] + 1;

    }

    int i = 0;
    //for each validColumns[i]
    for (; i < newEntry.length && i < validColumns.length; i++) {
      //if this table does not contain it yet - add a String column
//      if(!columns.containsKey(validColumns[i]))
//	addDefaultColumn(validColumns[i]);
      //it is now safe to set the boolean value into the table
      setObject(newEntry[i], position, validColumns[i]);
    }

    //in case newEntry has more values than the size of validColumns
    //appending the values at the end of this row.

    for (; i < newEntry.length; i++) {
      //     addDefaultColumn(newCol);                    //adding a column
      setObject(newEntry[i], position, numColumns); //setting the new entry

    }

    // updateRows(position, getRowIndices(position));
  }

  /**
   * Adds a new row at index <codE>position</code> with the content of <codE>
   * newEntry</code>, which will be converted to the types of the columns, as
   * needed. Is protected because this method should only be invoked after
   * validation of vacancy of index <code>position</code>
   *
   * @param newEntry      the data to be put in the new row.
   * @param position      the index of the new row
       * @param validColumns  the valid indices of the new row, s.t. newEntry[i] will
       *                      be stored at row <code>position</codE> and column <code>
   *                      validColumns[i]</code> when this method returns.
   */
  protected void addRow(String[] newEntry, int position, int[] validColumns) {

    if (numRows <= position) {
      numRows = position + 1;
    }
    if (numColumns <= validColumns[validColumns.length - 1]) {
      numColumns = validColumns[validColumns.length - 1] + 1;

    }

    int i = 0;
    //for each validColumns[i]
    for (; i < newEntry.length && i < validColumns.length; i++) {
      //if this table does not contain it yet - add a String column
      //     if(!columns.containsKey(validColumns[i]))
//	addDefaultColumn(validColumns[i]);
      //it is now safe to set the boolean value into the table
      setString(newEntry[i], position, validColumns[i]);
    }

    //in case newEntry has more values than the size of validColumns
    //appending the values at the end of this row.

    for (; i < newEntry.length; i++) {
      //     addDefaultColumn(newCol);                    //adding a column
      setString(newEntry[i], position, numColumns); //setting the new entry

    }

//    updateRows(position, getRowIndices(position));
  }

  /**
   * Adds a new row at index <codE>position</code> with the content of <codE>
   * newEntry</code>, which will be converted to the types of the columns, as
   * needed. Is protected because this method should only be invoked after
   * validation of vacancy of index <code>position</code>
   *
   * @param newEntry      the data to be put in the new row.
   * @param position      the index of the new row
       * @param validColumns  the valid indices of the new row, s.t. newEntry[i] will
       *                      be stored at row <code>position</codE> and column <code>
   *                      validColumns[i]</code> when this method returns.
   */
  protected void addRow(byte[] newEntry, int position, int[] validColumns) {

    if (numRows <= position) {
      numRows = position + 1;

    }
    if (numColumns <= validColumns[validColumns.length - 1]) {
      numColumns = validColumns[validColumns.length - 1] + 1;

    }
    int i = 0;
    //for each validColumns[i]
    for (; i < newEntry.length && i < validColumns.length; i++) {
      //if this table does not contain it yet - add a String column
      //    if(!columns.containsKey(validColumns[i]))
//	addDefaultColumn(validColumns[i]);
      //it is now safe to set the boolean value into the table
      setByte(newEntry[i], position, validColumns[i]);
    }

    //in case newEntry has more values than the size of validColumns
    //appending the values at the end of this row.

    for (; i < newEntry.length; i++) {
      //     addDefaultColumn(newCol);                    //adding a column
      setByte(newEntry[i], position, numColumns); //setting the new entry

    }

    //   updateRows(position, getRowIndices(position));
  }

  /**
   * Adds a new row at index <codE>position</code> with the content of <codE>
   * newEntry</code>, which will be converted to the types of the columns, as
   * needed. Is protected because this method should only be invoked after
   * validation of vacancy of index <code>position</code>
   *
   * @param newEntry      the data to be put in the new row.
   * @param position      the index of the new row
       * @param validColumns  the valid indices of the new row, s.t. newEntry[i] will
       *                      be stored at row <code>position</codE> and column <code>
   *                      validColumns[i]</code> when this method returns.
   */
  protected void addRow(boolean[] newEntry, int position, int[] validColumns) {

    if (numRows <= position) {
      numRows = position + 1;
    }
    if (numColumns <= validColumns[validColumns.length - 1]) {
      numColumns = validColumns[validColumns.length - 1] + 1;

    }
    int i = 0;
    //for each validColumns[i]
    for (; i < newEntry.length && i < validColumns.length; i++) {
      //if this table does not contain it yet - add a String column
//      if(!columns.containsKey(validColumns[i]))
//	addDefaultColumn(validColumns[i]);
      //it is now safe to set the boolean value into the table
      setBoolean(newEntry[i], position, validColumns[i]);
    }

    //in case newEntry has more values than the size of validColumns
    //appending the values at the end of this row.

    for (; i < newEntry.length; i++) {
      //     addDefaultColumn(newCol);                    //adding a column
      setBoolean(newEntry[i], position, numColumns); //setting the new entry

    }

//    updateRows(position, getRowIndices(position));
  }

  /**
   * Creates a new String Column and puts it at index <code>index</code>.
   *
   * @param index    the index number of the new column.
   */

  protected void addDefaultColumn(int index) {
    if (!columns.containsKey(index)) {
      setColumn(index, new SparseStringColumn());
    }
  }

  /**
   * Adds a new row at index <codE>position</code> with the content of <codE>
   * newEntry</code>, which will be converted to the types of the columns, as
   * needed. Is protected because this method should only be invoked after
   * validation of vacancy of index <code>position</code>
   *
   * @param newEntry      the data to be put in the new row.
   * @param position      the index of the new row
       * @param validColumns  the valid indices of the new row, s.t. newEntry[i] will
       *                      be stored at row <code>position</codE> and column <code>
   *                      validColumns[i]</code> when this method returns.
   */
  protected void addRow(char[] newEntry, int position, int[] validColumns) {

    if (numRows <= position) {
      numRows = position + 1;

    }
    if (numColumns <= validColumns[validColumns.length - 1]) {
      numColumns = validColumns[validColumns.length - 1] + 1;

    }

    int i = 0;
    //for each validColumns[i]
    for (; i < newEntry.length && i < validColumns.length; i++) {
      //if this table does not contain it yet - add a String column
      //     if(!columns.containsKey(validColumns[i]))
//	addDefaultColumn(validColumns[i]);
      //it is now safe to set the boolean value into the table
      setChar(newEntry[i], position, validColumns[i]);
    }

    //in case newEntry has more values than the size of validColumns
    //appending the values at the end of this row.

    for (; i < newEntry.length; i++) {
      //     addDefaultColumn(newCol);                    //adding a column
      setChar(newEntry[i], position, numColumns); //setting the new entry

    }

//    updateRows(position, getRowIndices(position));
  }

  /**
   * Adds a new row at index <codE>position</code> with the content of <codE>
   * newEntry</code>, which will be converted to the types of the columns, as
   * needed. Is protected because this method should only be invoked after
   * validation of vacancy of index <code>position</code>
   *
   * @param newEntry      the data to be put in the new row.
   * @param position      the index of the new row
       * @param validColumns  the valid indices of the new row, s.t. newEntry[i] will
       *                      be stored at row <code>position</codE> and column <code>
   *                      validColumns[i]</code> when this method returns.
   */
  protected void addRow(char[][] newEntry, int position, int[] validColumns) {
    if (numRows <= position) {
      numRows = position + 1;
    }
    if (numColumns <= validColumns[validColumns.length - 1]) {
      numColumns = validColumns[validColumns.length - 1] + 1;

    }

    int i = 0;
    //for each validColumns[i]
    for (; i < newEntry.length && i < validColumns.length; i++) {
      //if this table does not contain it yet - add a String column
      //     if(!columns.containsKey(validColumns[i]))
//	addDefaultColumn(validColumns[i]);
      //it is now safe to set the boolean value into the table
      setChars(newEntry[i], position, validColumns[i]);
    }

    //in case newEntry has more values than the size of validColumns
    //appending the values at the end of this row.

    for (; i < newEntry.length; i++) {
      //     addDefaultColumn(newCol);                    //adding a column
      setChars(newEntry[i], position, numColumns); //setting the new entry

    }

//    updateRows(position, getRowIndices(position));
  }

  /**
   * Adds a new row at index <codE>position</code> with the content of <codE>
   * newEntry</code>, which will be converted to the types of the columns, as
   * needed. Is protected because this method should only be invoked after
   * validation of vacancy of index <code>position</code>
   *
   * @param newEntry      the data to be put in the new row.
   * @param position      the index of the new row
       * @param validColumns  the valid indices of the new row, s.t. newEntry[i] will
       *                      be stored at row <code>position</codE> and column <code>
   *                      validColumns[i]</code> when this method returns.
   */
  protected void addRow(double[] newEntry, int position, int[] validColumns) {
    if (numRows <= position) {
      numRows = position + 1;
      /*
          //debug
           System.out.println("\nlength of validColumns " + validColumns.length + "\n");
          //debug
       */

    }
    if (numColumns <= validColumns[validColumns.length - 1]) {
      numColumns = validColumns[validColumns.length - 1] + 1;

    }

    int i = 0;
    //for each validColumns[i]
    for (; i < newEntry.length && i < validColumns.length; i++) {
      //if this table does not contain it yet - add a String column
      //    if(!columns.containsKey(validColumns[i]))
//	addDefaultColumn(validColumns[i]);
      //it is now safe to set the boolean value into the table
      setDouble(newEntry[i], position, validColumns[i]);
    }

    //in case newEntry has more values than the size of validColumns
    //appending the values at the end of this row.

    for (; i < newEntry.length; i++) {
      //     addDefaultColumn(newCol);                    //adding a column
      setDouble(newEntry[i], position, numColumns); //setting the new entry

    }

//   updateRows(position, getRowIndices(position));
  }

  /**
   * Adds a new row at index <codE>position</code> with the content of <codE>
   * newEntry</code>, which will be converted to the types of the columns, as
   * needed. Is protected because this method should only be invoked after
   * validation of vacancy of index <code>position</code>
   *
   * @param newEntry      the data to be put in the new row.
   * @param position      the index of the new row
       * @param validColumns  the valid indices of the new row, s.t. newEntry[i] will
       *                      be stored at row <code>position</codE> and column <code>
   *                      validColumns[i]</code> when this method returns.
   */
  protected void addRow(byte[][] newEntry, int position, int[] validColumns) {

    if (numRows <= position) {
      numRows = position + 1;
    }
    if (numColumns <= validColumns[validColumns.length - 1]) {
      numColumns = validColumns[validColumns.length - 1] + 1;

    }
    int i = 0;
    //for each validColumns[i]
    for (; i < newEntry.length && i < validColumns.length; i++) {
      //if this table does not contain it yet - add a String column
      //     if(!columns.containsKey(validColumns[i]))
//	addDefaultColumn(validColumns[i]);
      //it is now safe to set the boolean value into the table
      setBytes(newEntry[i], position, validColumns[i]);
    }

    //in case newEntry has more values than the size of validColumns
    //appending the values at the end of this row.

    for (; i < newEntry.length; i++) {
      //     addDefaultColumn(newCol);                    //adding a column
      setBytes(newEntry[i], position, numColumns); //setting the new entry

    }

    //   updateRows(position, getRowIndices(position));
  }

  /**
   * Adds a new row at index <codE>position</code> with the content of <codE>
   * newEntry</code>, which will be converted to the types of the columns, as
   * needed. Is protected because this method should only be invoked after
   * validation of vacancy of index <code>position</code>
   *
   * @param newEntry      the data to be put in the new row.
   * @param position      the index of the new row
       * @param validColumns  the valid indices of the new row, s.t. newEntry[i] will
       *                      be stored at row <code>position</codE> and column <code>
   *                      validColumns[i]</code> when this method returns.
   */
  protected void addRow(float[] newEntry, int position, int[] validColumns) {

    if (numRows <= position) {
      numRows = position + 1;
    }
    if (numColumns <= validColumns[validColumns.length - 1]) {
      numColumns = validColumns[validColumns.length - 1] + 1;

    }

    int i = 0;
    //for each validColumns[i]
    for (; i < newEntry.length && i < validColumns.length; i++) {
      //if this table does not contain it yet - add a String column
//      if(!columns.containsKey(validColumns[i]))
//	addDefaultColumn(validColumns[i]);
      //it is now safe to set the boolean value into the table
      setFloat(newEntry[i], position, validColumns[i]);
    }

    //in case newEntry has more values than the size of validColumns
    //appending the values at the end of this row.

    for (; i < newEntry.length; i++) {
      //     addDefaultColumn(newCol);                    //adding a column
      setFloat(newEntry[i], position, numColumns); //setting the new entry

    }

    //   updateRows(position, getRowIndices(position));
  }


  /**
   * *****************************************************
   * insertRow methods
   * *****************************************************
   */

  /**
   * Inserts the data in <code>newEntry</code> to row #<codE>position</code> in
   * this table. all the rows from row no. <code>position</code> and on will
   * be moved to the next row.
   *
       * Note: Since this is a Sparse Table the data is not added to all the columns
   * zero through the size of newEntry, but only to the valid columns in this
   * table.
   *
   * It is more accurate to use insertRow(int[], int, int[]) method.
   *
   * @param newEntry    an int array that holds data to be inserted to row no.
   *                    <codE>position</code> in this table.
       * @param position    the row number into which the values should be inserted.
   *
   */
  protected void insertRow(int[] newEntry, int position) {
    int[] validColumns = getAllColumns();
    insertRow(newEntry, position, validColumns);
  }

  /**
   * Inserts the data in <code>newEntry</code> to row #<codE>position</code> in
   * this table. all the rows from row no. <code>position</code> and on will
   * be moved to the next row.
   *
   *
       * Note: Since this is a Sparse Table the data is not added to all the columns
   * zero through the size of newEntry, but only to the valid columns in this
   * table.
   *
   * It is more accurate to use insertRow(float[], int, int[]) method.
   *
   * @param newEntry    a float array that holds data to be inserted to row no.
   *                    <codE>position</code> in this table.
       * @param position    the row number into which the values should be inserted.
   *
   */
  protected void insertRow(float[] newEntry, int position) {
    int[] validColumns = getAllColumns();
    insertRow(newEntry, position, validColumns);
  }

  /**
   * Inserts the data in <code>newEntry</code> to row #<codE>position</code> in
   * this table. all the rows from row no. <code>position</code> and on will
   * be moved to the next row.
   *
   *
       * Note: Since this is a Sparse Table the data is not added to all the columns
   * zero through the size of newEntry, but only to the valid columns in this
   * table.
   *
   * It is more accurate to use insertRow(double[], int, int[]) method.
   *
   * @param newEntry    a doublearray that holds data to be inserted to row no.
   *                    <codE>position</code> in this table.
       * @param position    the row number into which the values should be inserted.
   *
   */
  protected void insertRow(double[] newEntry, int position) {
    int[] validColumns = getAllColumns();
    insertRow(newEntry, position, validColumns);
  }

  /**
   * Inserts the data in <code>newEntry</code> to row #<codE>position</code> in
   * this table. all the rows from row no. <code>position</code> and on will
   * be moved to the next row.
   *
       * Note: Since this is a Sparse Table the data is not added to all the columns
   * zero through the size of newEntry, but only to the valid columns in this
   * table.
   *
   * It is more accurate to use insertRow(long[], int, int[]) method.
   *
   * @param newEntry    a long array that holds data to be inserted to row no.
   *                    <codE>position</code> in this table.
       * @param position    the row number into which the values should be inserted.
   *
   */
  protected void insertRow(long[] newEntry, int position) {
    int[] validColumns = getAllColumns();
    insertRow(newEntry, position, validColumns);
  }

  /**
   * Inserts the data in <code>newEntry</code> to row #<codE>position</code> in
   * this table. all the rows from row no. <code>position</code> and on will
   * be moved to the next row.
   *
       * Note: Since this is a Sparse Table the data is not added to all the columns
   * zero through the size of newEntry, but only to the valid columns in this
   * table.
   *
   * It is more accurate to use insertRow(short[], int, int[]) method.
   *
   * @param newEntry    a short array that holds data to be inserted to row no.
   *                    <codE>position</code> in this table.
       * @param position    the row number into which the values should be inserted.
   *
   */
  protected void insertRow(short[] newEntry, int position) {
    int[] validColumns = getAllColumns();
    insertRow(newEntry, position, validColumns);
  }

  /**
   * Inserts the data in <code>newEntry</code> to row #<codE>position</code> in
   * this table. all the rows from row no. <code>position</code> and on will
   * be moved to the next row.
   *
   *
       * Note: Since this is a Sparse Table the data is not added to all the columns
   * zero through the size of newEntry, but only to the valid columns in this
   * table.
   *
   * It is more accurate to use insertRow(boolean[], int, int[]) method.
   *
       * @param newEntry    a boolean array that holds data to be inserted to row no.
   *                    <codE>position</code> in this table.
       * @param position    the row number into which the values should be inserted.
   *
   */
  protected void insertRow(boolean[] newEntry, int position) {
    int[] validColumns = getAllColumns();
    insertRow(newEntry, position, validColumns);
  }

  /**
   * Inserts the data in <code>newEntry</code> to row #<codE>position</code> in
   * this table. all the rows from row no. <code>position</code> and on will
   * be moved to the next row.
   *
       * Note: Since this is a Sparse Table the data is not added to all the columns
   * zero through the size of newEntry, but only to the valid columns in this
   * table.
   *
   * It is more accurate to use insertRow(String[], int, int[]) method.
   *
       * @param newEntry    a String array that holds data to be inserted to row no.
   *                    <codE>position</code> in this table.
       * @param position    the row number into which the values should be inserted.
   *
   */
  protected void insertRow(String[] newEntry, int position) {
    int[] validColumns = getAllColumns();
    insertRow(newEntry, position, validColumns);
  }

  /**
   *Inserts the data in <code>newEntry</code> to row #<codE>position</code> in
   * this table. all the rows from row no. <code>position</code> and on will
   * be moved to the next row.
   *
   *
       * Note: Since this is a Sparse Table the data is not added to all the columns
   * zero through the size of newEntry, but only to the valid columns in this
   * table.
   *
   * It is more accurate to use insertRow(char[][], int, int[]) method.
   *
       * @param newEntry    a char[] array that holds data to be inserted to row no.
   *                    <codE>position</code> in this table.
       * @param position    the row number into which the values should be inserted.
   *
   */
  protected void insertRow(char[][] newEntry, int position) {
    int[] validColumns = getAllColumns();
    insertRow(newEntry, position, validColumns);
  }

  /**
   *Inserts the data in <code>newEntry</code> to row #<codE>position</code> in
   * this table. all the rows from row no. <code>position</code> and on will
   * be moved to the next row.
   *
       * Note: Since this is a Sparse Table the data is not added to all the columns
   * zero through the size of newEntry, but only to the valid columns in this
   * table.
   *
   * It is more accurate to use insertRow(byte[][], int, int[]) method.
   *
       * @param newEntry    a byte[] array that holds data to be inserted to row no.
   *                    <codE>position</code> in this table.
       * @param position    the row number into which the values should be inserted.
   *
   */
  protected void insertRow(byte[][] newEntry, int position) {
    int[] validColumns = getAllColumns();
    insertRow(newEntry, position, validColumns);
  }

  /**
   * Inserts the data in <code>newEntry</code> to row #<codE>position</code> in
   * this table. all the rows from row no. <code>position</code> and on will
   * be moved to the next row.
   *
       * Note: Since this is a Sparse Table the data is not added to all the columns
   * zero through the size of newEntry, but only to the valid columns in this
   * table.
   *
   * It is more accurate to use insertRow(Object[], int, int[]) method.
   *
       * @param newEntry    an Object[] array that holds data to be inserted to row no.
   *                    <codE>position</code> in this table.
       * @param position    the row number into which the values should be inserted.
   *
   */
  protected void insertRow(Object[] newEntry, int position) {
    int[] validColumns = getAllColumns();
    insertRow(newEntry, position, validColumns);
  }

  /**
   * Inserts the data in <code>newEntry</code> to row #<codE>position</code> in
   * this table. all the rows from row no. <code>position</code> and on will
   * be moved to the next row.
   *
       * Note: Since this is a Sparse Table the data is not added to all the columns
   * zero through the size of newEntry, but only to the valid columns in this
   * table.
   *
   * It is more accurate to use insertRow(byte[], int, int[]) method.
   *
       * @param newEntry    a byte[] array that holds data to be inserted to row no.
   *                    <codE>position</code> in this table.
       * @param position    the row number into which the values should be inserted.
   *
   */
  protected void insertRow(byte[] newEntry, int position) {
    int[] validColumns = getAllColumns();
    insertRow(newEntry, position, validColumns);
  }

  /**
   * Inserts the data in <code>newEntry</code> to row #<codE>position</code> in
   * this table. all the rows from row no. <code>position</code> and on will
   * be moved to the next row.
   *
   *
       * Note: Since this is a Sparse Table the data is not added to all the columns
   * zero through the size of newEntry, but only to the valid columns in this
   * table.
   *
   * It is more accurate to use insertRow(char[], int, int[]) method.
   *
       * @param newEntry    an char[] array that holds data to be inserted to row no.
   *                    <codE>position</code> in this table.
       * @param position    the row number into which the values should be inserted.
   *
   */
  protected void insertRow(char[] newEntry, int position) {
    int[] validColumns = getAllColumns();
    insertRow(newEntry, position, validColumns);
  }

  /**
   * Inserts each item <code>newEntry[i]</code> into column no. <code>validColumns[i]
   * </code> and row no. <code>rowIndex</code>.
   * If <code>newEntry</code> is larger than <code>validColumns</code> then the
   * rest of <code>newEntry</code> is inserted into SparseStringColumns at the
   * end og the table.
   *
       * All rows from <code>rowIndex</code> to the last row are moved down the table
   * to the next row.
   *
   * @param newEntry       the data to be stored at the new row
   * @param rowIndex       the index of the new row
   * @param validColumns   the indices of the columns of the new row.
   */
  protected void insertRow(int[] newEntry, int rowIndex, int[] validColumns) {

    int i;
    for (i = 0; i < newEntry.length && i < validColumns.length; i++) {
      getColumn(validColumns[i]).insertRow(new Integer(newEntry[i]), rowIndex);
    }

    if (validColumns.length > 0) {
      ;
    }
    numColumns = validColumns[validColumns.length - 1] + 1;

    for (; i < newEntry.length; i++) {
      //     addDefaultColumn(colIndex);
      setInt(newEntry[i], rowIndex, numColumns);
      numColumns++;
    }

    rows.insertObject(new VIntHashSet(validColumns), rowIndex);

    numRows++;
  }

  /**
   * Inserts each item <code>newEntry[i]</code> into column no.
   * <code>validColumns[i] </code> and row no. <code>rowIndex</code>.
   * If <code>newEntry</code> is larger than <code>validColumns</code>
   * then the rest of <code>newEntry</code> is inserted into
   * SparseStringColumns at the end of the table.
   *
   * All rows from <code>rowIndex</code> to the last row are moved down
   * the table to the next row.
   *
   * Modified by Xiaolei - 07/08/2003.
   *
   * @param newEntry       the data to be stored at the new row
   * @param rowIndex       the index of the new row
   * @param validColumns   the indices of the columns of the new row. sorted.
   */
  protected void insertRow(boolean[] newEntry, int rowIndex, int[] validColumns) {
    int i;

    for (i = 0; i < newEntry.length && i < validColumns.length; i++) {
      getColumn(validColumns[i]).insertRow(new Boolean(newEntry[i]), rowIndex);
    }

    if (validColumns.length > 0) {
      numColumns = validColumns[validColumns.length - 1] + 1;

    }
    if (newEntry.length < validColumns.length) {

    }

    for (; i < newEntry.length; i++) {
      //     addDefaultColumn(colIndex);
      setBoolean(newEntry[i], rowIndex, numColumns);
      numColumns++;
    }

    rows.insertObject(new VIntHashSet(validColumns), rowIndex);

    numRows++;

  }

  /**
   * Inserts each item <code>newEntry[i]</code> into column no. <code>validColumns[i]
   * </code> and row no. <code>rowIndex</code>.
   * If <code>newEntry</code> is larger than <code>validColumns</code> then the
   * rest of <code>newEntry</code> is inserted into SparseStringColumns at the
   * end og the table.
   *
       * All rows from <code>rowIndex</code> to the last row are moved down the table
   * to the next row.
   *
   * @param newEntry       the data to be stored at the new row
   * @param rowIndex       the index of the new row
   * @param validColumns   the indices of the columns of the new row.
   */
  protected void insertRow(byte[] newEntry, int rowIndex, int[] validColumns) {

    int i;
    for (i = 0; i < newEntry.length && i < validColumns.length; i++) {
      getColumn(validColumns[i]).insertRow(new Byte(newEntry[i]), rowIndex);
    }

    if (validColumns.length > 0) {
      ;
    }
    numColumns = validColumns[validColumns.length - 1] + 1;

    for (; i < newEntry.length; i++) {
      //     addDefaultColumn(colIndex);
      setByte(newEntry[i], rowIndex, numColumns);
      numColumns++;
    }

    rows.insertObject(new VIntHashSet(validColumns), rowIndex);
    numRows++;

  }

  /**
   * Inserts each item <code>newEntry[i]</code> into column no. <code>validColumns[i]
   * </code> and row no. <code>rowIndex</code>.
   * If <code>newEntry</code> is larger than <code>validColumns</code> then the
   * rest of <code>newEntry</code> is inserted into SparseStringColumns at the
   * end og the table.
   *
       * All rows from <code>rowIndex</code> to the last row are moved down the table
   * to the next row.
   *
   * @param newEntry       the data to be stored at the new row
   * @param rowIndex       the index of the new row
   * @param validColumns   the indices of the columns of the new row.
   */
  protected void insertRow(char[][] newEntry, int rowIndex, int[] validColumns) {

    int i;
    for (i = 0; i < newEntry.length && i < validColumns.length; i++) {
      getColumn(validColumns[i]).insertRow(newEntry[i], rowIndex);
    }

    if (validColumns.length > 0) {
      ;
    }
    numColumns = validColumns[validColumns.length - 1] + 1;

    for (; i < newEntry.length; i++) {
//      addDefaultColumn(colIndex);
      setChars(newEntry[i], rowIndex, numColumns);
      numColumns++;
    }

    rows.insertObject(new VIntHashSet(validColumns), rowIndex);
    numRows++;

  }

  /**
   * Inserts each item <code>newEntry[i]</code> into column no.
   * <code>validColumns[i] </code> and row no. <code>rowIndex</code>.
   * If <code>newEntry</code> is larger than <code>validColumns</code>
   * then the rest of <code>newEntry</code> is inserted into
   * SparseStringColumns at the end og the table.
   *
       * All rows from <code>rowIndex</code> to the last row are moved down the table
   * to the next row.
   *
   * Modified by Xiaolei Li - 07/08/2003.
   *
   * @param newEntry       the data to be stored at the new row
   * @param rowIndex       the index of the new row
   * @param validColumns   the indices of the columns of the new row.
   */
  protected void insertRow(double[] newEntry, int rowIndex, int[] validColumns) {
    int i;

    /* for every existing column, add this new entry */
    for (i = 0; i < newEntry.length && i < validColumns.length; i++) {
      getColumn(validColumns[i]).insertRow(new Double(newEntry[i]), rowIndex);
    }

    /* re-compute the total number of columns */
    if (validColumns.length > 0) {
      numColumns = validColumns[validColumns.length - 1] + 1;

      /* if the new row has less entries, we still have to shift down
       * the columns after the new row.  by inserting a null object,
       * the insertRow method will actually _not_ insert it.  it'll
       * only do the shifting. */
    }
    if (newEntry.length < validColumns.length) {
      for (int j = newEntry.length; j < validColumns.length; j++) {
        getColumn(validColumns[j]).insertRow(null, rowIndex);
      }
    }

    int[] rowColumns = new int[newEntry.length];
    for (int j = 0; j < rowColumns.length; j++) {
      rowColumns[j] = j;

      /* add the row */
    }
    rows.insertObject(new VIntHashSet(rowColumns), rowIndex);

    /* if the new row has more entries than the current table */
    for (; i < newEntry.length; i++) {
      // addDefaultColumn(colIndex);
      setDouble(newEntry[i], rowIndex, numColumns);
      numColumns++;
    }

    numRows++;
  }

  /**
   * Inserts each item <code>newEntry[i]</code> into column no. <code>validColumns[i]
   * </code> and row no. <code>rowIndex</code>.
   * If <code>newEntry</code> is larger than <code>validColumns</code> then the
   * rest of <code>newEntry</code> is inserted into SparseStringColumns at the
   * end og the table.
   *
       * All rows from <code>rowIndex</code> to the last row are moved down the table
   * to the next row.
   *
   * @param newEntry       the data to be stored at the new row
   * @param rowIndex       the index of the new row
   * @param validColumns   the indices of the columns of the new row.
   */
  protected void insertRow(float[] newEntry, int rowIndex, int[] validColumns) {

    int i;
    for (i = 0; i < newEntry.length && i < validColumns.length; i++) {
      getColumn(validColumns[i]).insertRow(new Float(newEntry[i]), rowIndex);
    }

    if (validColumns.length > 0) {
      ;
    }
    numColumns = validColumns[validColumns.length - 1] + 1;

    for (; i < newEntry.length; i++) {
      //     addDefaultColumn(colIndex);
      setFloat(newEntry[i], rowIndex, numColumns);
      numColumns++;
    }

    rows.insertObject(new VIntHashSet(validColumns), rowIndex);
    numRows++;

  }

  /**
   * Inserts each item <code>newEntry[i]</code> into column no. <code>validColumns[i]
   * </code> and row no. <code>rowIndex</code>.
   * If <code>newEntry</code> is larger than <code>validColumns</code> then the
   * rest of <code>newEntry</code> is inserted into SparseStringColumns at the
   * end og the table.
   *
       * All rows from <code>rowIndex</code> to the last row are moved down the table
   * to the next row.
   *
   * @param newEntry       the data to be stored at the new row
   * @param rowIndex       the index of the new row
   * @param validColumns   the indices of the columns of the new row.
   */
  protected void insertRow(long[] newEntry, int rowIndex, int[] validColumns) {

    int i;
    for (i = 0; i < newEntry.length && i < validColumns.length; i++) {
      getColumn(validColumns[i]).insertRow(new Long(newEntry[i]), rowIndex);
    }

    if (validColumns.length > 0) {
      ;
    }
    numColumns = validColumns[validColumns.length - 1] + 1;

    for (; i < newEntry.length; i++) {
//      addDefaultColumn(colIndex);
      setLong(newEntry[i], rowIndex, numColumns);
      numColumns++;
    }

    rows.insertObject(new VIntHashSet(validColumns), rowIndex);

    numRows++;
  }

  /**
   * Inserts each item <code>newEntry[i]</code> into column no. <code>validColumns[i]
   * </code> and row no. <code>rowIndex</code>.
   * If <code>newEntry</code> is larger than <code>validColumns</code> then the
   * rest of <code>newEntry</code> is inserted into SparseStringColumns at the
   * end og the table.
   *
       * All rows from <code>rowIndex</code> to the last row are moved down the table
   * to the next row.
   *
   * @param newEntry       the data to be stored at the new row
   * @param rowIndex       the index of the new row
   * @param validColumns   the indices of the columns of the new row.
   */
  protected void insertRow(short[] newEntry, int rowIndex, int[] validColumns) {

    int i;
    for (i = 0; i < newEntry.length && i < validColumns.length; i++) {
      getColumn(validColumns[i]).insertRow(new Short(newEntry[i]), rowIndex);
    }

    if (validColumns.length > 0) {
      ;
    }
    numColumns = validColumns[validColumns.length - 1] + 1;

    for (; i < newEntry.length; i++) {
      //     addDefaultColumn(colIndex);
      setShort(newEntry[i], rowIndex, numColumns);
      numColumns++;
    }

    rows.insertObject(new VIntHashSet(validColumns), rowIndex);

    numRows++;
  }

  /**
   * Inserts each item <code>newEntry[i]</code> into column no. <code>validColumns[i]
   * </code> and row no. <code>rowIndex</code>.
   * If <code>newEntry</code> is larger than <code>validColumns</code> then the
   * rest of <code>newEntry</code> is inserted into SparseStringColumns at the
   * end og the table.
   *
       * All rows from <code>rowIndex</code> to the last row are moved down the table
   * to the next row.
   *
   * @param newEntry       the data to be stored at the new row
   * @param rowIndex       the index of the new row
   * @param validColumns   the indices of the columns of the new row.
   */
  protected void insertRow(String[] newEntry, int rowIndex, int[] validColumns) {

    int i;
    for (i = 0; i < newEntry.length && i < validColumns.length; i++) {
      getColumn(validColumns[i]).insertRow(newEntry[i], rowIndex);
    }

    if (validColumns.length > 0) {
      ;
    }
    numColumns = validColumns[validColumns.length - 1] + 1;

    for (; i < newEntry.length; i++) {
//      addDefaultColumn(colIndex);
      setString(newEntry[i], rowIndex, numColumns);
      numColumns++;
    }

    rows.insertObject(new VIntHashSet(validColumns), rowIndex);
    numRows++;

  }

  /**
   * Inserts each item <code>newEntry[i]</code> into column no. <code>validColumns[i]
   * </code> and row no. <code>rowIndex</code>.
   * If <code>newEntry</code> is larger than <code>validColumns</code> then the
   * rest of <code>newEntry</code> is inserted into SparseStringColumns at the
   * end og the table.
   *
       * All rows from <code>rowIndex</code> to the last row are moved down the table
   * to the next row.
   *
   * @param newEntry       the data to be stored at the new row
   * @param rowIndex       the index of the new row
   * @param validColumns   the indices of the columns of the new row.
   */
  protected void insertRow(Object[] newEntry, int rowIndex, int[] validColumns) {

    int i;
    for (i = 0; i < newEntry.length && i < validColumns.length; i++) {
      getColumn(validColumns[i]).insertRow(newEntry[i], rowIndex);
    }

    if (validColumns.length > 0) {
      ;
    }
    numColumns = validColumns[validColumns.length - 1] + 1;

    for (; i < newEntry.length; i++) {
      //     addDefaultColumn(colIndex);
      setObject(newEntry[i], rowIndex, numColumns);
      numColumns++;
    }

    rows.insertObject(new VIntHashSet(validColumns), rowIndex);

    numRows++;
  }

  /**
   * Inserts each item <code>newEntry[i]</code> into column no. <code>validColumns[i]
   * </code> and row no. <code>rowIndex</code>.
   * If <code>newEntry</code> is larger than <code>validColumns</code> then the
   * rest of <code>newEntry</code> is inserted into SparseStringColumns at the
   * end og the table.
   *
       * All rows from <code>rowIndex</code> to the last row are moved down the table
   * to the next row.
   *
   * @param newEntry       the data to be stored at the new row
   * @param rowIndex       the index of the new row
   * @param validColumns   the indices of the columns of the new row.
   */
  protected void insertRow(char[] newEntry, int rowIndex, int[] validColumns) {

    int i;
    for (i = 0; i < newEntry.length && i < validColumns.length; i++) {
      getColumn(validColumns[i]).insertRow(new Character(newEntry[i]), rowIndex);
    }

    if (validColumns.length > 0) {
      ;
    }
    numColumns = validColumns[validColumns.length - 1] + 1;

    for (; i < newEntry.length; i++) {
//      addDefaultColumn(colIndex);
      setChar(newEntry[i], rowIndex, numColumns);
      numColumns++;
    }

    rows.insertObject(new VIntHashSet(validColumns), rowIndex);

    numRows++;
  }

  /**
   * Inserts each item <code>newEntry[i]</code> into column no. <code>validColumns[i]
   * </code> and row no. <code>rowIndex</code>.
   * If <code>newEntry</code> is larger than <code>validColumns</code> then the
   * rest of <code>newEntry</code> is inserted into SparseStringColumns at the
   * end og the table.
   *
       * All rows from <code>rowIndex</code> to the last row are moved down the table
   * to the next row.
   *
   * @param newEntry       the data to be stored at the new row
   * @param rowIndex       the index of the new row
   * @param validColumns   the indices of the columns of the new row.
   */
  protected void insertRow(byte[][] newEntry, int rowIndex, int[] validColumns) {

    int i;
    for (i = 0; i < newEntry.length && i < validColumns.length; i++) {
      getColumn(validColumns[i]).insertRow(newEntry[i], rowIndex);
    }

    if (validColumns.length > 0) {
      ;
    }
    numColumns = validColumns[validColumns.length - 1] + 1;

    for (; i < newEntry.length; i++) {
//      addDefaultColumn(colIndex);
      setBytes(newEntry[i], rowIndex, numColumns);
      numColumns++;
    }

    rows.insertObject(new VIntHashSet(validColumns), rowIndex);
    numRows++;
  }


  /**
   * ***************************************************************
   * addColumn methods
   * ***************************************************************
   */

  /**
   * Appends a new int column with <code>newEntry</code>'s data, at the end of
   * the columns in this table.
   * <code>validRows'</code> values indicate the valid rows in the new column
   *
   * @param newEntry      the values to be stored in the new column
   * @param validRows     the indices of valid rows in the new column
   */
  protected void addColumn(int[] newEntry, int[] validRows) {
    addColumn(newEntry, numColumns, validRows);

  }

  /**
       * Appends a new Object column with <code>newEntry</code>'s data, at the end of
   * the columns in this table.
   * <code>validRows'</code> values indicate the valid rows in the new column
   *
   * @param newEntry      the values to be stored in the new column
   * @param validRows     the indices of valid rows in the new column
   */
  protected void addColumn(Object[] newEntry, int[] validRows) {
    addColumn(newEntry, numColumns, validRows);
  }

  /**
       * Appends a new String column with <code>newEntry</code>'s data, at the end of
   * the columns in this table.
   * <code>validRows'</code> values indicate the valid rows in the new column
   *
   * @param newEntry      the values to be stored in the new column
   * @param validRows     the indices of valid rows in the new column
   */
  protected void addColumn(String[] newEntry, int[] validRows) {
    addColumn(newEntry, numColumns, validRows);

  }

  /**
       * Appends a new short column with <code>newEntry</code>'s data, at the end of
   * the columns in this table.
   * <code>validRows'</code> values indicate the valid rows in the new column
   *
   * @param newEntry      the values to be stored in the new column
   * @param validRows     the indices of valid rows in the new column
   */
  protected void addColumn(short[] newEntry, int[] validRows) {
    addColumn(newEntry, numColumns, validRows);

  }

  /**
   * Appends a new long column with <code>newEntry</code>'s data, at the end of
   * the columns in this table.
   * <code>validRows'</code> values indicate the valid rows in the new column
   *
   * @param newEntry      the values to be stored in the new column
   * @param validRows     the indices of valid rows in the new column
   */
  protected void addColumn(long[] newEntry, int[] validRows) {
    addColumn(newEntry, numColumns, validRows);

  }

  /**
       * Appends a new float column with <code>newEntry</code>'s data, at the end of
   * the columns in this table.
   * <code>validRows'</code> values indicate the valid rows in the new column
   *
   * @param newEntry      the values to be stored in the new column
   * @param validRows     the indices of valid rows in the new column
   */
  protected void addColumn(float[] newEntry, int[] validRows) {
    addColumn(newEntry, numColumns, validRows);

  }

  /**
       * Appends a new double column with <code>newEntry</code>'s data, at the end of
   * the columns in this table.
   * <code>validRows'</code> values indicate the valid rows in the new column
   *
   * @param newEntry      the values to be stored in the new column
   * @param validRows     the indices of valid rows in the new column
   */
  protected void addColumn(double[] newEntry, int[] validRows) {
    addColumn(newEntry, numColumns, validRows);

  }

  /**
       * Appends a new char[] column with <code>newEntry</code>'s data, at the end of
   * the columns in this table.
   * <code>validRows'</code> values indicate the valid rows in the new column
   *
   * @param newEntry      the values to be stored in the new column
   * @param validRows     the indices of valid rows in the new column
   */
  protected void addColumn(char[][] newEntry, int[] validRows) {
    addColumn(newEntry, numColumns, validRows);

  }

  /**
   * Appends a new char column with <code>newEntry</code>'s data, at the end of
   * the columns in this table.
   * <code>validRows'</code> values indicate the valid rows in the new column
   *
   * @param newEntry      the values to be stored in the new column
   * @param validRows     the indices of valid rows in the new column
   */
  protected void addColumn(char[] newEntry, int[] validRows) {
    addColumn(newEntry, numColumns, validRows);

  }

  /**
       * Appends a new byte[] column with <code>newEntry</code>'s data, at the end of
   * the columns in this table.
   * <code>validRows'</code> values indicate the valid rows in the new column
   *
   * @param newEntry      the values to be stored in the new column
   * @param validRows     the indices of valid rows in the new column
   */
  protected void addColumn(byte[][] newEntry, int[] validRows) {
    addColumn(newEntry, numColumns, validRows);

  }

  /**
   * Appends a new byte column with <code>newEntry</code>'s data, at the end of
   * the columns in this table.
   * <code>validRows'</code> values indicate the valid rows in the new column
   *
   * @param newEntry      the values to be stored in the new column
   * @param validRows     the indices of valid rows in the new column
   */
  protected void addColumn(byte[] newEntry, int[] validRows) {
    addColumn(newEntry, numColumns, validRows);

  }

  /**
       * Appends a new boolean column with <code>newEntry</code>'s data, at the end of
   * the columns in this table.
   * <code>validRows'</code> values indicate the valid rows in the new column
   *
   * @param newEntry      the values to be stored in the new column
   * @param validRows     the indices of valid rows in the new column
   */
  protected void addColumn(boolean[] newEntry, int[] validRows) {
    addColumn(newEntry, numColumns, validRows);

  }

  /**
   * Puts a new int column at index #<code>colIndex</code> of this table, with
   * the values of <codE>newEntry</code> and valid rows from <code>validRows</code>.
   * To be used only after the vacancy of column #<codE>colIndex</code> was
   * validated.
   *
   * @param newEntry    the values to be stored in the new column
   * @param colIndex    the index insertion of the new column
   * @param validRows   the valid indices in the new column
   */
  protected void addColumn(int[] newEntry, int colIndex, int[] validRows) {
    SparseIntColumn iColumn = new SparseIntColumn(newEntry, validRows);
    //columns.put(colIndex, iColumn);
    setColumn(colIndex, iColumn);
  }

  /**
       * Puts a new boolean column at index #<code>colIndex</code> of this table, with
   * the values of <codE>newEntry</code> and valid rows from <code>validRows</code>.
   * To be used only after the vacancy of column #<codE>colIndex</code> was
   * validated.
   *
   * @param newEntry    the values to be stored in the new column
   * @param colIndex    the index insertion of the new column
   * @param validRows   the valid indices in the new column
   */
  protected void addColumn(boolean[] newEntry, int colIndex, int[] validRows) {
    SparseBooleanColumn blColumn = new SparseBooleanColumn(newEntry, validRows);
    setColumn(colIndex, blColumn);

  }

  /**
   * Puts a new byte column at index #<code>colIndex</code> of this table, with
   * the values of <codE>newEntry</code> and valid rows from <code>validRows</code>.
   * To be used only after the vacancy of column #<codE>colIndex</code> was
   * validated.
   *
   * @param newEntry    the values to be stored in the new column
   * @param colIndex    the index insertion of the new column
   * @param validRows   the valid indices in the new column
   */
  protected void addColumn(byte[] newEntry, int colIndex, int[] validRows) {
    SparseByteColumn bColumn = new SparseByteColumn(newEntry, validRows);
    setColumn(colIndex, bColumn);
  }

  /**
       * Puts a new byte[] column at index #<code>colIndex</code> of this table, with
   * the values of <codE>newEntry</code> and valid rows from <code>validRows</code>.
   * To be used only after the vacancy of column #<codE>colIndex</code> was
   * validated.
   *
   * @param newEntry    the values to be stored in the new column
   * @param colIndex    the index insertion of the new column
   * @param validRows   the valid indices in the new column
   */
  protected void addColumn(byte[][] newEntry, int colIndex, int[] validRows) {
    SparseByteArrayColumn baColumn = new SparseByteArrayColumn(newEntry,
        validRows);
    setColumn(colIndex, baColumn);
  }

  /**
       * Puts a new Object column at index #<code>colIndex</code> of this table, with
   * the values of <codE>newEntry</code> and valid rows from <code>validRows</code>.
   * To be used only after the vacancy of column #<codE>colIndex</code> was
   * validated.
   *
   * @param newEntry    the values to be stored in the new column
   * @param colIndex    the index insertion of the new column
   * @param validRows   the valid indices in the new column
   */
  protected void addColumn(Object[] newEntry, int colIndex, int[] validRows) {
    SparseObjectColumn oColumn = new SparseObjectColumn(newEntry, validRows);
    setColumn(colIndex, oColumn);
  }

  /**
       * Puts a new String column at index #<code>colIndex</code> of this table, with
   * the values of <codE>newEntry</code> and valid rows from <code>validRows</code>.
   * To be used only after the vacancy of column #<codE>colIndex</code> was
   * validated.
   *
   * @param newEntry    the values to be stored in the new column
   * @param colIndex    the index insertion of the new column
   * @param validRows   the valid indices in the new column
   */
  protected void addColumn(String[] newEntry, int colIndex, int[] validRows) {
    SparseStringColumn strColumn = new SparseStringColumn(newEntry, validRows);
    setColumn(colIndex, strColumn);
  }

  /**
       * Puts a new short column at index #<code>colIndex</code> of this table, with
   * the values of <codE>newEntry</code> and valid rows from <code>validRows</code>.
   * To be used only after the vacancy of column #<codE>colIndex</code> was
   * validated.
   *
   * @param newEntry    the values to be stored in the new column
   * @param colIndex    the index insertion of the new column
   * @param validRows   the valid indices in the new column
   */
  protected void addColumn(short[] newEntry, int colIndex, int[] validRows) {
    SparseShortColumn sColumn = new SparseShortColumn(newEntry, validRows);
    setColumn(colIndex, sColumn);
  }

  /**
   * Puts a new long column at index #<code>colIndex</code> of this table, with
   * the values of <codE>newEntry</code> and valid rows from <code>validRows</code>.
   * To be used only after the vacancy of column #<codE>colIndex</code> was
   * validated.
   *
   * @param newEntry    the values to be stored in the new column
   * @param colIndex    the index insertion of the new column
   * @param validRows   the valid indices in the new column
   */
  protected void addColumn(long[] newEntry, int colIndex, int[] validRows) {
    SparseLongColumn lColumn = new SparseLongColumn(newEntry, validRows);
    setColumn(colIndex, lColumn);
  }

  /**
       * Puts a new float column at index #<code>colIndex</code> of this table, with
   * the values of <codE>newEntry</code> and valid rows from <code>validRows</code>.
   * To be used only after the vacancy of column #<codE>colIndex</code> was
   * validated.
   *
   * @param newEntry    the values to be stored in the new column
   * @param colIndex    the index insertion of the new column
   * @param validRows   the valid indices in the new column
   */
  protected void addColumn(float[] newEntry, int colIndex, int[] validRows) {
    SparseFloatColumn fColumn = new SparseFloatColumn(newEntry, validRows);
    setColumn(colIndex, fColumn);
  }

  /**
       * Puts a new double column at index #<code>colIndex</code> of this table, with
   * the values of <codE>newEntry</code> and valid rows from <code>validRows</code>.
   * To be used only after the vacancy of column #<codE>colIndex</code> was
   * validated.
   *
   * @param newEntry    the values to be stored in the new column
   * @param colIndex    the index insertion of the new column
   * @param validRows   the valid indices in the new column
   */
  protected void addColumn(double[] newEntry, int colIndex, int[] validRows) {
    SparseDoubleColumn dColumn = new SparseDoubleColumn(newEntry, validRows);
    setColumn(colIndex, dColumn);
  }

  /**
   * Puts a new char column at index #<code>colIndex</code> of this table, with
   * the values of <codE>newEntry</code> and valid rows from <code>validRows</code>.
   * To be used only after the vacancy of column #<codE>colIndex</code> was
   * validated.
   *
   * @param newEntry    the values to be stored in the new column
   * @param colIndex    the index insertion of the new column
   * @param validRows   the valid indices in the new column
   */
  protected void addColumn(char[] newEntry, int colIndex, int[] validRows) {
    SparseCharColumn cColumn = new SparseCharColumn(newEntry, validRows);
    setColumn(colIndex, cColumn);
  }

  /**
       * Puts a new char[] column at index #<code>colIndex</code> of this table, with
   * the values of <codE>newEntry</code> and valid rows from <code>validRows</code>.
   * To be used only after the vacancy of column #<codE>colIndex</code> was
   * validated.
   *
   * @param newEntry    the values to be stored in the new column
   * @param colIndex    the index insertion of the new column
   * @param validRows   the valid indices in the new column
   */
  protected void addColumn(char[][] newEntry, int colIndex, int[] validRows) {
    SparseCharArrayColumn caColumn = new SparseCharArrayColumn(newEntry,
        validRows);
    setColumn(colIndex, caColumn);
  }

  /**
   * Adds a new int column to this table.
   * Note - Since this is a Sarse Table the data in the new column will be
   * held in valid rows only.
   *
   * For more explicit and accurate way of adding a column use method
   * addColumn(int[], int[]).
   *
   * @param newEntry    the values to be held in the new column.
   */
  protected void addColumn(int[] newEntry) {
    int[] validRows = getAllRows();
    addColumn(newEntry, validRows);
  }


  /**
   * Adds a new float column to this table.
   * Note - Since this is a Sarse Table the data in the new column will be
   * held in valid rows only.
   *
   * For more explicit and accurate way of adding a column use method
   * addColumn(float[], int[]).
   *
   * @param newEntry    the values to be held in the new column.
   */
  protected void addColumn(float[] newEntry) {

    int[] validRows = getAllRows();
    addColumn(newEntry, validRows);

  }

  /**
   * Adds a new double column to this table.
   * Note - Since this is a Sarse Table the data in the new column will be
   * held in valid rows only.
   *
   * For more explicit and accurate way of adding a column use method
   * addColumn(double[], int[]).
   *
   * @param newEntry    the values to be held in the new column.
   */
  protected void addColumn(double[] newEntry) {
    int[] validRows = getAllRows();
    addColumn(newEntry, validRows);
  }

  /**
   * Adds a new long column to this table.
   * Note - Since this is a Sarse Table the data in the new column will be
   * held in valid rows only.
   *
   * For more explicit and accurate way of adding a column use method
   * addColumn(long[], int[]).
   *
   * @param newEntry    the values to be held in the new column.
   */
  protected void addColumn(long[] newEntry) {
    int[] validRows = getAllRows();
    addColumn(newEntry, validRows);
  }

  /**
   * Adds a new short column to this table.
   * Note - Since this is a Sarse Table the data in the new column will be
   * held in valid rows only.
   *
   * For more explicit and accurate way of adding a column use method
   * addColumn(short[], int[]).
   *
   * @param newEntry    the values to be held in the new column.
   */
  protected void addColumn(short[] newEntry) {
    int[] validRows = getAllRows();
    addColumn(newEntry, validRows);

  }

  /**
   * Adds a new boolean column to this table.
   * Note - Since this is a Sarse Table the data in the new column will be
   * held in valid rows only.
   *
   * For more explicit and accurate way of adding a column use method
   * addColumn(boolean[], int[]).
   *
   * @param newEntry    the values to be held in the new column.
   */
  protected void addColumn(boolean[] newEntry) {
    int[] validRows = getAllRows();
    addColumn(newEntry, validRows);
  }

  /**
   * Adds a new String column to this table.
   * Note - Since this is a Sarse Table the data in the new column will be
   * held in valid rows only.
   *
   * For more explicit and accurate way of adding a column use method
   * addColumn(String[], int[]).
   *
   * @param newEntry    the values to be held in the new column.
   */
  protected void addColumn(String[] newEntry) {
    int[] validRows = getAllRows();
    addColumn(newEntry, validRows);
  }

  /**
   * Adds a new char[] column to this table.
   * Note - Since this is a Sarse Table the data in the new column will be
   * held in valid rows only.
   *
   * For more explicit and accurate way of adding a column use method
   * addColumn(char[][], int[]).
   *
   * @param newEntry    the values to be held in the new column.
   */
  protected void addColumn(char[][] newEntry) {
    int[] validRows = getAllRows();
    addColumn(newEntry, validRows);
  }

  /**
   * Adds a new byte[] column to this table.
   * Note - Since this is a Sarse Table the data in the new column will be
   * held in valid rows only.
   *
   * For more explicit and accurate way of adding a column use method
   * addColumn(byte[][], int[]).
   *
   * @param newEntry    the values to be held in the new column.
   */
  protected void addColumn(byte[][] newEntry) {
    int[] validRows = getAllRows();
    addColumn(newEntry, validRows);
  }

  /**
   * Adds a new Object column to this table.
   * Note - Since this is a Sarse Table the data in the new column will be
   * held in valid rows only.
   *
   * For more explicit and accurate way of adding a column use method
   * addColumn(Object[], int[]).
   *
   * @param newEntry    the values to be held in the new column.
   */
  protected void addColumn(Object[] newEntry) {
    int[] validRows = getAllRows();
    addColumn(newEntry, validRows);
  }

  /**
   * Adds a new byte column to this table.
   * Note - Since this is a Sarse Table the data in the new column will be
   * held in valid rows only.
   *
   * For more explicit and accurate way of adding a column use method
   * addColumn(byte[], int[]).
   *
   * @param newEntry    the values to be held in the new column.
   */
  protected void addColumn(byte[] newEntry) {
    int[] validRows = getAllRows();
    addColumn(newEntry, validRows);
  }

  /**
   * Adds a new char column to this table.
   * Note - Since this is a Sarse Table the data in the new column will be
   * held in valid rows only.
   *
   * For more explicit and accurate way of adding a column use method
   * addColumn(char[], int[]).
   *
   * @param newEntry    the values to be held in the new column.
   */
  protected void addColumn(char[] newEntry) {
    int[] validRows = getAllRows();
    addColumn(newEntry, validRows);
  }


  /**
   * **********************************************************
   * insertColumn method
   * **********************************************************
   */

  /**
   * Insert <codE>newColumn</code> to index <codE>position</codE>.
   * All columns from index <code>position</code> and on are moved to the next
   * column down the table.
   *
   * @param newColumn     the new column to be inserted at index position.
   * @param position      the index for the new column.
   *
   */
  protected void insertColumn(AbstractSparseColumn newColumn, int position) {

    int numcols = getNumColumns();
    if ( (position < 0) || (position > numcols)) {
      throw new IndexOutOfBoundsException(
          "SparseMutableTable.insertColumn() -- cannot insert column at position " +
          position + ". Not a valid index.");
    }

    //updating the column map
    columns.insertObject(newColumn, position);

    //updating the rows map
    int[] rowNumbers = VHashService.getIndices(rows);

    //for each set in rows - adding 1 to each element.
    for (int i = 0; i < rowNumbers.length; i++) {
      VIntHashSet tempSet = (VIntHashSet) rows.get(rowNumbers[i]);
      tempSet.increment(position);
      //XIAOLEI
      if (newColumn.doesValueExist(rowNumbers[i])) {
        tempSet.add(position);
      }
    } //for

    int numR = newColumn.getNumRows();
    if (numRows <= numR) {

      // XIAOLEI
      for (int i = numRows; i < numR; i++) {
        addCol2Row(position, i);

      }
      numRows = numR;
    }

    numColumns = VHashService.getMaxKey(columns) + 1;
  }

  protected void insertColumns(AbstractSparseColumn[] newColumns, int position) {
    for (int i = newColumns.length - 1; i >= 0; i--) {
      this.insertColumn(newColumns[i], position);
    }
  }

  /**
   * inserts a new int column to this table at index <code>position</code>.
   * All columns from index <code>position</code> and on are moved to the next
   * column down the table.
   *
   * Note - Since this is a Sarse Table the data in the new column will be
   * held in valid rows only.
   *
   * For more explicit and accurate way of inserting a column use method
   * insertColumn(int[], int, int[]).
   *
   * @param newEntry    the values to be held in the new column.
   * @param position    the index for the new column.
   */
  protected void insertColumn(int[] newEntry, int position) {
    int[] validRows = getAllRows();
    insertColumn(newEntry, position, validRows);
  }

  /**
   * inserts a new float column to this table at index <code>position</code>.
   * All columns from index <code>position</code> and on are moved to the next
   * column down the table.
   *
   * Note - Since this is a Sarse Table the data in the new column will be
   * held in valid rows only.
   *
   * For more explicit and accurate way of inserting a column use method
   * insertColumn(float[], int, int[]).
   *
   * @param newEntry    the values to be held in the new column.
   * @param position    the index for the new column.
   */
  protected void insertColumn(float[] newEntry, int position) {
    int[] validRows = getAllRows();
    insertColumn(newEntry, position, validRows);
  }

  /**
   * inserts a new double column to this table at index <code>position</code>.
   * All columns from index <code>position</code> and on are moved to the next
   * column down the table.
   *
   * Note - Since this is a Sarse Table the data in the new column will be
   * held in valid rows only.
   *
   * For more explicit and accurate way of inserting a column use method
   * insertColumn(double[], int, int[]).
   *
   * @param newEntry    the values to be held in the new column.
   * @param position    the index for the new column.
   */
  protected void insertColumn(double[] newEntry, int position) {
    int[] validRows = getAllRows();
    insertColumn(newEntry, position, validRows);
  }

  /**
   * inserts a new long column to this table at index <code>position</code>.
   * All columns from index <code>position</code> and on are moved to the next
   * column down the table.
   *
   * Note - Since this is a Sarse Table the data in the new column will be
   * held in valid rows only.
   *
   * For more explicit and accurate way of inserting a column use method
   * insertColumn(long[], int, int[]).
   *
   * @param newEntry    the values to be held in the new column.
   * @param position    the index for the new column.
   */
  protected void insertColumn(long[] newEntry, int position) {
    int[] validRows = getAllRows();
    insertColumn(newEntry, position, validRows);
  }

  /**
   * inserts a new short column to this table at index <code>position</code>.
   * All columns from index <code>position</code> and on are moved to the next
   * column down the table.
   *
   * Note - Since this is a Sarse Table the data in the new column will be
   * held in valid rows only.
   *
   * For more explicit and accurate way of inserting a column use method
   * insertColumn(short[], int, int[]).
   *
   * @param newEntry    the values to be held in the new column.
   * @param position    the index for the new column.
   */
  protected void insertColumn(short[] newEntry, int position) {
    int[] validRows = getAllRows();
    insertColumn(newEntry, position, validRows);
  }

  /**
   * inserts a new boolean column to this table at index <code>position</code>.
   * All columns from index <code>position</code> and on are moved to the next
   * column down the table.
   *
   * Note - Since this is a Sarse Table the data in the new column will be
   * held in valid rows only.
   *
   * For more explicit and accurate way of inserting a column use method
   * insertColumn(boolean[], int, int[]).
   *
   * @param newEntry    the values to be held in the new column.
   * @param position    the index for the new column.
   */
  protected void insertColumn(boolean[] newEntry, int position) {
    int[] validRows = getAllRows();
    insertColumn(newEntry, position, validRows);
  }

  /**
   * inserts a new String column to this table at index <code>position</code>.
   * All columns from index <code>position</code> and on are moved to the next
   * column down the table.
   *
   * Note - Since this is a Sarse Table the data in the new column will be
   * held in valid rows only.
   *
   * For more explicit and accurate way of inserting a column use method
   * insertColumn(String[], int, int[]).
   *
   * @param newEntry    the values to be held in the new column.
   * @param position    the index for the new column.
   */
  protected void insertColumn(String[] newEntry, int position) {
    int[] validRows = getAllRows();
    insertColumn(newEntry, position, validRows);
  }

  /**
   * inserts a new char[] column to this table at index <code>position</code>.
   * All columns from index <code>position</code> and on are moved to the next
   * column down the table.
   *
   * Note - Since this is a Sarse Table the data in the new column will be
   * held in valid rows only.
   *
   *For more explicit and accurate way of inserting a column use method
   * insertColumn(char[][], int, int[]).
   *
   * @param newEntry    the values to be held in the new column.
   * @param position    the index for the new column.
   */
  protected void insertColumn(char[][] newEntry, int position) {
    int[] validRows = getAllRows();
    insertColumn(newEntry, position, validRows);
  }

  /**
       * inserts a new byte[][] column to this table at index <code>position</code>.
   * All columns from index <code>position</code> and on are moved to the next
   * column down the table.
   *
   * Note - Since this is a Sarse Table the data in the new column will be
   * held in valid rows only.
   *
   * For more explicit and accurate way of inserting a column use method
   * insertColumn(byte[][], int, int[]).
   *
   * @param newEntry    the values to be held in the new column.
   * @param position    the index for the new column.
   */
  protected void insertColumn(byte[][] newEntry, int position) {
    int[] validRows = getAllRows();
    insertColumn(newEntry, position, validRows);
  }

  /**
   * inserts a new Object column to this table at index <code>position</code>.
   * All columns from index <code>position</code> and on are moved to the next
   * column down the table.
   *
   * Note - Since this is a Sarse Table the data in the new column will be
   * held in valid rows only.
   *
   * For more explicit and accurate way of inserting a column use method
   * insertColumn(Object[], int, int[]).
   *
   * @param newEntry    the values to be held in the new column.
   * @param position    the index for the new column.
   */
  protected void insertColumn(Object[] newEntry, int position) {
    int[] validRows = getAllRows();
    insertColumn(newEntry, position, validRows);
  }

  /**
   * inserts a new byte column to this table at index <code>position</code>.
   * All columns from index <code>position</code> and on are moved to the next
   * column down the table.
   *
   * Note - Since this is a Sarse Table the data in the new column will be
   * held in valid rows only.
   *
   * For more explicit and accurate way of inserting a column use method
   * insertColumn(byte[], int, int[]).
   *
   * @param newEntry    the values to be held in the new column.
   * @param position    the index for the new column.
   */
  protected void insertColumn(byte[] newEntry, int position) {
    int[] validRows = getAllRows();
    insertColumn(newEntry, position, validRows);
  }

  /**
   * inserts a new char column to this table at index <code>position</code>.
   * All columns from index <code>position</code> and on are moved to the next
   * column down the table.
   *
   * Note - Since this is a Sarse Table the data in the new column will be
   * held in valid rows only.
   *
   * For more explicit and accurate way of inserting a column use method
   * insertColumn(char[], int, int[]).
   *
   * @param newEntry    the values to be held in the new column.
   * @param position    the index for the new column.
   */
  protected void insertColumn(char[] newEntry, int position) {
    int[] validRows = getAllRows();
    insertColumn(newEntry, position, validRows);
  }

  /**
       * Inserts a new int column with the data of <code>newEntry</code> and valid rows
   * as specified in <code>validRows</code> at index <code>position</code>.
   * each item <codE>newEntry[i]</code> will be inserted to row no. <code>
   * validRows[i]</code> in the new Column.
   * All columns from index <code>position</code> and on are moved to the next
   * column down the table.
   *
   * @param newEntry       the data to be inserted to the new Column
   * @param position       the index insertion for the new Column
   * @param validRows      the valid indiced for the data in newEntry.
   */
  protected void insertColumn(int[] newEntry, int position, int[] validRows) {
    SparseIntColumn iColumn = new SparseIntColumn(newEntry, validRows);
    insertColumn(iColumn, position);
  }

  /**
   * Inserts a new boolean column with the data of <code>newEntry</code> and valid rows
   * as specified in <code>validRows</code> at index <code>position</code>.
   * each item <codE>newEntry[i]</code> will be inserted to row no. <code>
   * validRows[i]</code> in the new Column.
   * All columns from index <code>position</code> and on are moved to the next
   * column down the table.
   *
   * @param newEntry       the data to be inserted to the new Column
   * @param position       the index insertion for the new Column
   * @param validRows      the valid indiced for the data in newEntry.
   */
  protected void insertColumn(boolean[] newEntry, int position, int[] validRows) {
    SparseBooleanColumn blColumn = new SparseBooleanColumn(newEntry, validRows);
    insertColumn(blColumn, position);
  }

  /**
   * Inserts a new byte column with the data of <code>newEntry</code> and valid rows
   * as specified in <code>validRows</code> at index <code>position</code>.
   * each item <codE>newEntry[i]</code> will be inserted to row no. <code>
   * validRows[i]</code> in the new Column.
   * All columns from index <code>position</code> and on are moved to the next
   * column down the table.
   *
   * @param newEntry       the data to be inserted to the new Column
   * @param position       the index insertion for the new Column
   * @param validRows      the valid indiced for the data in newEntry.
   */
  protected void insertColumn(byte[] newEntry, int position, int[] validRows) {
    SparseByteColumn bColumn = new SparseByteColumn(newEntry, validRows);
    insertColumn(bColumn, position);
  }

  /**
   * Inserts a new char column with the data of <code>newEntry</code> and valid rows
   * as specified in <code>validRows</code> at index <code>position</code>.
   * each item <codE>newEntry[i]</code> will be inserted to row no. <code>
   * validRows[i]</code> in the new Column.
   * All columns from index <code>position</code> and on are moved to the next
   * column down the table.
   *
   * @param newEntry       the data to be inserted to the new Column
   * @param position       the index insertion for the new Column
   * @param validRows      the valid indiced for the data in newEntry.
   */
  protected void insertColumn(char[] newEntry, int position, int[] validRows) {
    SparseCharColumn cColumn = new SparseCharColumn(newEntry, validRows);
    insertColumn(cColumn, position);
  }

  /**
   * Inserts a new char[] column with the data of <code>newEntry</code> and valid rows
   * as specified in <code>validRows</code> at index <code>position</code>.
   * each item <codE>newEntry[i]</code> will be inserted to row no. <code>
   * validRows[i]</code> in the new Column.
   * All columns from index <code>position</code> and on are moved to the next
   * column down the table.
   *
   * @param newEntry       the data to be inserted to the new Column
   * @param position       the index insertion for the new Column
   * @param validRows      the valid indiced for the data in newEntry.
   */
  protected void insertColumn(char[][] newEntry, int position, int[] validRows) {
    SparseCharArrayColumn caColumn = new SparseCharArrayColumn(newEntry,
        validRows);
    insertColumn(caColumn, position);
  }

  /**
   * Inserts a new byte[] column with the data of <code>newEntry</code> and valid rows
   * as specified in <code>validRows</code> at index <code>position</code>.
   * each item <codE>newEntry[i]</code> will be inserted to row no. <code>
   * validRows[i]</code> in the new Column.
   * All columns from index <code>position</code> and on are moved to the next
   * column down the table.
   *
   * @param newEntry       the data to be inserted to the new Column
   * @param position       the index insertion for the new Column
   * @param validRows      the valid indiced for the data in newEntry.
   */
  protected void insertColumn(byte[][] newEntry, int position, int[] validRows) {
    SparseByteArrayColumn baColumn = new SparseByteArrayColumn(newEntry,
        validRows);
    insertColumn(baColumn, position);
  }

  /**
   * Inserts a new double column with the data of <code>newEntry</code> and valid rows
   * as specified in <code>validRows</code> at index <code>position</code>.
   * each item <codE>newEntry[i]</code> will be inserted to row no. <code>
   * validRows[i]</code> in the new Column.
   * All columns from index <code>position</code> and on are moved to the next
   * column down the table.
   *
   * @param newEntry       the data to be inserted to the new Column
   * @param position       the index insertion for the new Column
   * @param validRows      the valid indiced for the data in newEntry.
   */
  protected void insertColumn(double[] newEntry, int position, int[] validRows) {
    SparseDoubleColumn dColumn = new SparseDoubleColumn(newEntry, validRows);
    insertColumn(dColumn, position);
  }

  /**
   * Inserts a new float column with the data of <code>newEntry</code> and valid rows
   * as specified in <code>validRows</code> at index <code>position</code>.
   * each item <codE>newEntry[i]</code> will be inserted to row no. <code>
   * validRows[i]</code> in the new Column.
   * All columns from index <code>position</code> and on are moved to the next
   * column down the table.
   *
   * @param newEntry       the data to be inserted to the new Column
   * @param position       the index insertion for the new Column
   * @param validRows      the valid indiced for the data in newEntry.
   */
  protected void insertColumn(float[] newEntry, int position, int[] validRows) {
    SparseFloatColumn fColumn = new SparseFloatColumn(newEntry, validRows);
    insertColumn(fColumn, position);
  }

  /**
   * Inserts a new long column with the data of <code>newEntry</code> and valid rows
   * as specified in <code>validRows</code> at index <code>position</code>.
   * each item <codE>newEntry[i]</code> will be inserted to row no. <code>
   * validRows[i]</code> in the new Column.
   * All columns from index <code>position</code> and on are moved to the next
   * column down the table.
   *
   * @param newEntry       the data to be inserted to the new Column
   * @param position       the index insertion for the new Column
   * @param validRows      the valid indiced for the data in newEntry.
   */
  protected void insertColumn(long[] newEntry, int position, int[] validRows) {
    SparseLongColumn lColumn = new SparseLongColumn(newEntry, validRows);
    insertColumn(lColumn, position);
  }

  /**
   * Inserts a new short column with the data of <code>newEntry</code> and valid rows
   * as specified in <code>validRows</code> at index <code>position</code>.
   * each item <codE>newEntry[i]</code> will be inserted to row no. <code>
   * validRows[i]</code> in the new Column.
   * All columns from index <code>position</code> and on are moved to the next
   * column down the table.
   *
   * @param newEntry       the data to be inserted to the new Column
   * @param position       the index insertion for the new Column
   * @param validRows      the valid indiced for the data in newEntry.
   */
  protected void insertColumn(short[] newEntry, int position, int[] validRows) {
    SparseShortColumn sColumn = new SparseShortColumn(newEntry, validRows);
    insertColumn(sColumn, position);
  }

  /**
   * Inserts a new String column with the data of <code>newEntry</code> and valid rows
   * as specified in <code>validRows</code> at index <code>position</code>.
   * each item <codE>newEntry[i]</code> will be inserted to row no. <code>
   * validRows[i]</code> in the new Column.
   * All columns from index <code>position</code> and on are moved to the next
   * column down the table.
   *
   * @param newEntry       the data to be inserted to the new Column
   * @param position       the index insertion for the new Column
   * @param validRows      the valid indiced for the data in newEntry.
   */
  protected void insertColumn(String[] newEntry, int position, int[] validRows) {
    SparseStringColumn strColumn = new SparseStringColumn(newEntry, validRows);
    insertColumn(strColumn, position);
  }

  /**
   * Inserts a new Object column with the data of <code>newEntry</code> and valid rows
   * as specified in <code>validRows</code> at index <code>position</code>.
   * each item <codE>newEntry[i]</code> will be inserted to row no. <code>
   * validRows[i]</code> in the new Column.
   * All columns from index <code>position</code> and on are moved to the next
   * column down the table.
   *
   * @param newEntry       the data to be inserted to the new Column
   * @param position       the index insertion for the new Column
   * @param validRows      the valid indiced for the data in newEntry.
   */
  protected void insertColumn(Object[] newEntry, int position, int[] validRows) {
    SparseObjectColumn oColumn = new SparseObjectColumn(newEntry, validRows);
    insertColumn(oColumn, position);
  }

  /**
   * ***************************************************
   * setRow methods
   * ***************************************************
   */

  /**
   * Creates a new row from <code>newEntry</code>'s data and sets it to row
       * #<code> position</code>. The values of <code>newEntry</code> will be converted
   * as needed according to the type of column it is being set into.
   *
   * Note - Since this is a Sparse Table, the values of <code>newEntry</code>
       * will be inserted only into the valid columns of row #<code>position</code>.
       * If such row does not exist - the valid columns will be the same as the valid
   * columns in all of the table.
   *
   * For more accurate setting - use method setRow(int[], int, int[]).
   *
   * @param newEntry      the data to be set into row #<code>position</code>
   * @param position      the row number to be set.
   */
  protected void setRow(int[] newEntry, int position) {
    int[] columnNumbers = getRowIndices(position);
    setRow(newEntry, position, columnNumbers);
  }

  /**
   * Creates a new row from <code>newEntry</code>'s data and sets it to row
       * #<code> position</code>. The values of <code>newEntry</code> will be converted
   * as needed according to the type of column it is being set into.
   *
   * Note - Since this is a Sparse Table, the values of <code>newEntry</code>
       * will be inserted only into the valid columns of row #<code>position</code>.
       * If such row does not exist - the valid columns will be the same as the valid
   * columns in all of the table.
   *
   * For more accurate setting - use method setRow(float[], int, int[]).
   *
   * @param newEntry      the data to be set into row #<code>position</code>
   * @param position      the row number to be set.
   */
  protected void setRow(float[] newEntry, int position) {
    int[] columnNumbers = getRowIndices(position);
    setRow(newEntry, position, columnNumbers);
  }

  /**
   * Creates a new row from <code>newEntry</code>'s data and sets it to row
       * #<code> position</code>. The values of <code>newEntry</code> will be converted
   * as needed according to the type of column it is being set into.
   *
   * Note - Since this is a Sparse Table, the values of <code>newEntry</code>
       * will be inserted only into the valid columns of row #<code>position</code>.
       * If such row does not exist - the valid columns will be the same as the valid
   * columns in all of the table.
   *
   * For more accurate setting - use method setRow(double[], int, int[]).
   *
   * @param newEntry      the data to be set into row #<code>position</code>
   * @param position      the row number to be set.
   */
  protected void setRow(double[] newEntry, int position) {
    int[] columnNumbers = getRowIndices(position);
    setRow(newEntry, position, columnNumbers);
  }

  /**
   * Creates a new row from <code>newEntry</code>'s data and sets it to row
       * #<code> position</code>. The values of <code>newEntry</code> will be converted
   * as needed according to the type of column it is being set into.
   *
   * Note - Since this is a Sparse Table, the values of <code>newEntry</code>
       * will be inserted only into the valid columns of row #<code>position</code>.
       * If such row does not exist - the valid columns will be the same as the valid
   * columns in all of the table.
   *
   * For more accurate setting - use method setRow(long[], int, int[]).
   *
   * @param newEntry      the data to be set into row #<code>position</code>
   * @param position      the row number to be set.
   */
  protected void setRow(long[] newEntry, int position) {
    int[] columnNumbers = getRowIndices(position);
    setRow(newEntry, position, columnNumbers);
  }

  /**
   * Creates a new row from <code>newEntry</code>'s data and sets it to row
       * #<code> position</code>. The values of <code>newEntry</code> will be converted
   * as needed according to the type of column it is being set into.
   *
   * Note - Since this is a Sparse Table, the values of <code>newEntry</code>
       * will be inserted only into the valid columns of row #<code>position</code>.
       * If such row does not exist - the valid columns will be the same as the valid
   * columns in all of the table.
   *
   * For more accurate setting - use method setRow(short[], int, int[]).
   *
   * @param newEntry      the data to be set into row #<code>position</code>
   * @param position      the row number to be set.
   */
  protected void setRow(short[] newEntry, int position) {
    int[] columnNumbers = getRowIndices(position);
    setRow(newEntry, position, columnNumbers);
  }

  /**
   * Creates a new row from <code>newEntry</code>'s data and sets it to row
       * #<code> position</code>. The values of <code>newEntry</code> will be converted
   * as needed according to the type of column it is being set into.
   *
   * Note - Since this is a Sparse Table, the values of <code>newEntry</code>
       * will be inserted only into the valid columns of row #<code>position</code>.
       * If such row does not exist - the valid columns will be the same as the valid
   * columns in all of the table.
   *
   * For more accurate setting - use method setRow(boolean[], int, int[]).
   *
   * @param newEntry      the data to be set into row #<code>position</code>
   * @param position      the row number to be set.
   */
  protected void setRow(boolean[] newEntry, int position) {
    int[] columnNumbers = getRowIndices(position);
    setRow(newEntry, position, columnNumbers);
  }

  /**
   * Creates a new row from <code>newEntry</code>'s data and sets it to row
       * #<code> position</code>. The values of <code>newEntry</code> will be converted
   * as needed according to the type of column it is being set into.
   *
   * Note - Since this is a Sparse Table, the values of <code>newEntry</code>
       * will be inserted only into the valid columns of row #<code>position</code>.
       * If such row does not exist - the valid columns will be the same as the valid
   * columns in all of the table.
   *
   * For more accurate setting - use method setRow(String[], int, int[]).
   *
   * @param newEntry      the data to be set into row #<code>position</code>
   * @param position      the row number to be set.
   */
  protected void setRow(String[] newEntry, int position) {

    int[] columnNumbers = getRowIndices(position);
    setRow(newEntry, position, columnNumbers);
  }

  /**
   * Creates a new row from <code>newEntry</code>'s data and sets it to row
       * #<code> position</code>. The values of <code>newEntry</code> will be converted
   * as needed according to the type of column it is being set into.
   *
   * Note - Since this is a Sparse Table, the values of <code>newEntry</code>
       * will be inserted only into the valid columns of row #<code>position</code>.
       * If such row does not exist - the valid columns will be the same as the valid
   * columns in all of the table.
   *
   * For more accurate setting - use method setRow(char[][], int, int[]).
   *
   * @param newEntry      the data to be set into row #<code>position</code>
   * @param position      the row number to be set.
   */
  protected void setRow(char[][] newEntry, int position) {
    int[] columnNumbers = getRowIndices(position);
    setRow(newEntry, position, columnNumbers);
  }

  /**
   * Creates a new row from <code>newEntry</code>'s data and sets it to row
       * #<code> position</code>. The values of <code>newEntry</code> will be converted
   * as needed according to the type of column it is being set into.
   *
   * Note - Since this is a Sparse Table, the values of <code>newEntry</code>
       * will be inserted only into the valid columns of row #<code>position</code>.
       * If such row does not exist - the valid columns will be the same as the valid
   * columns in all of the table.
   *
   * For more accurate setting - use method setRow(byte[][], int, int[]).
   *
   * @param newEntry      the data to be set into row #<code>position</code>
   * @param position      the row number to be set.
   */
  protected void setRow(byte[][] newEntry, int position) {
    int[] columnNumbers = getRowIndices(position);
    setRow(newEntry, position, columnNumbers);
  }

  /**
   * Creates a new row from <code>newEntry</code>'s data and sets it to row
       * #<code> position</code>. The values of <code>newEntry</code> will be converted
   * as needed according to the type of column it is being set into.
   *
   * Note - Since this is a Sparse Table, the values of <code>newEntry</code>
       * will be inserted only into the valid columns of row #<code>position</code>.
       * If such row does not exist - the valid columns will be the same as the valid
   * columns in all of the table.
   *
   * For more accurate setting - use method setRow(Object[], int, int[]).
   *
   * @param newEntry      the data to be set into row #<code>position</code>
   * @param position      the row number to be set.
   */
  protected void setRow(Object[] newEntry, int position) {

    int[] columnNumbers = getRowIndices(position);
    setRow(newEntry, position, columnNumbers);
  }

  /**
   * Creates a new row from <code>newEntry</code>'s data and sets it to row
       * #<code> position</code>. The values of <code>newEntry</code> will be converted
   * as needed according to the type of column it is being set into.
   *
   * Note - Since this is a Sparse Table, the values of <code>newEntry</code>
       * will be inserted only into the valid columns of row #<code>position</code>.
       * If such row does not exist - the valid columns will be the same as the valid
   * columns in all of the table.
   *
   * For more accurate setting - use method setRow(byte[], int, int[]).
   *
   * @param newEntry      the data to be set into row #<code>position</code>
   * @param position      the row number to be set.
   */
  protected void setRow(byte[] newEntry, int position) {
    int[] columnNumbers = getRowIndices(position);
    setRow(newEntry, position, columnNumbers);
  }

  /**
   * Creates a new row from <code>newEntry</code>'s data and sets it to row
       * #<code> position</code>. The values of <code>newEntry</code> will be converted
   * as needed according to the type of column it is being set into.
   *
   * Note - Since this is a Sparse Table, the values of <code>newEntry</code>
       * will be inserted only into the valid columns of row #<code>position</code>.
       * If such row does not exist - the valid columns will be the same as the valid
   * columns in all of the table.
   *
   * For more accurate setting - use method setRow(char[], int, int[]).
   *
   * @param newEntry      the data to be set into row #<code>position</code>
   * @param position      the row number to be set.
   */
  protected void setRow(char[] newEntry, int position) {
    //retrieving the columns numbers
    int[] columnNumbers = getRowIndices(position);
    setRow(newEntry, position, columnNumbers);
  }

  /**
   * Creates a new row from <code>newEntry</code>'s data and sets it to row
       * #<code> position</code>. The values of <code>newEntry</code> will be converted
   * as needed according to the type of column it is being set into.
   *
   * @param newEntry      the data to be set into row #<code>position</code>
   * @param position      the row number to be set.
   * @param validColumns  the valid indices of the new row
   */
  protected void setRow(int[] newEntry, int position, int[] validColumns) {
    //removing the old row
    removeRow(position);
    addRow(newEntry, position, validColumns);

  }

  /**
   * Creates a new row from <code>newEntry</code>'s data and sets it to row
       * #<code> position</code>. The values of <code>newEntry</code> will be converted
   * as needed according to the type of column it is being set into.
   *
   * @param newEntry      the data to be set into row #<code>position</code>
   * @param position      the row number to be set.
   * @param validColumns  the valid indices of the new row
   */
  protected void setRow(boolean[] newEntry, int position, int[] validColumns) {
    //removing the old row
    removeRow(position);
    addRow(newEntry, position, validColumns);

  }

  /**
   * Creates a new row from <code>newEntry</code>'s data and sets it to row
       * #<code> position</code>. The values of <code>newEntry</code> will be converted
   * as needed according to the type of column it is being set into.
   *
   * @param newEntry      the data to be set into row #<code>position</code>
   * @param position      the row number to be set.
   * @param validColumns  the valid indices of the new row
   */
  protected void setRow(byte[] newEntry, int position, int[] validColumns) {
    //removing the old row
    removeRow(position);
    addRow(newEntry, position, validColumns);

  }

  /**
   * Creates a new row from <code>newEntry</code>'s data and sets it to row
       * #<code> position</code>. The values of <code>newEntry</code> will be converted
   * as needed according to the type of column it is being set into.
   *
   * @param newEntry      the data to be set into row #<code>position</code>
   * @param position      the row number to be set.
   * @param validColumns  the valid indices of the new row
   */
  protected void setRow(char[] newEntry, int position, int[] validColumns) {
    //removing the old row
    removeRow(position);
    addRow(newEntry, position, validColumns);

  }

  /**
   * Creates a new row from <code>newEntry</code>'s data and sets it to row
       * #<code> position</code>. The values of <code>newEntry</code> will be converted
   * as needed according to the type of column it is being set into.
   *
   * @param newEntry      the data to be set into row #<code>position</code>
   * @param position      the row number to be set.
   * @param validColumns  the valid indices of the new row
   */
  protected void setRow(byte[][] newEntry, int position, int[] validColumns) {
    //removing the old row
    removeRow(position);
    addRow(newEntry, position, validColumns);

  }

  /**
   * Creates a new row from <code>newEntry</code>'s data and sets it to row
       * #<code> position</code>. The values of <code>newEntry</code> will be converted
   * as needed according to the type of column it is being set into.
   *
   * @param newEntry      the data to be set into row #<code>position</code>
   * @param position      the row number to be set.
   * @param validColumns  the valid indices of the new row
   */
  protected void setRow(char[][] newEntry, int position, int[] validColumns) {
    //removing the old row
    removeRow(position);
    addRow(newEntry, position, validColumns);

  }

  /**
   * Creates a new row from <code>newEntry</code>'s data and sets it to row
       * #<code> position</code>. The values of <code>newEntry</code> will be converted
   * as needed according to the type of column it is being set into.
   *
   * @param newEntry      the data to be set into row #<code>position</code>
   * @param position      the row number to be set.
   * @param validColumns  the valid indices of the new row
   */
  protected void setRow(double[] newEntry, int position, int[] validColumns) {
    //removing the old row
    removeRow(position);
    addRow(newEntry, position, validColumns);

  }

  /**
   * Creates a new row from <code>newEntry</code>'s data and sets it to row
       * #<code> position</code>. The values of <code>newEntry</code> will be converted
   * as needed according to the type of column it is being set into.
   *
   * @param newEntry      the data to be set into row #<code>position</code>
   * @param position      the row number to be set.
   * @param validColumns  the valid indices of the new row
   */
  protected void setRow(float[] newEntry, int position, int[] validColumns) {
    //removing the old row
    removeRow(position);
    addRow(newEntry, position, validColumns);

  }

  /**
   * Creates a new row from <code>newEntry</code>'s data and sets it to row
       * #<code> position</code>. The values of <code>newEntry</code> will be converted
   * as needed according to the type of column it is being set into.
   *
   * @param newEntry      the data to be set into row #<code>position</code>
   * @param position      the row number to be set.
   * @param validColumns  the valid indices of the new row
   */
  protected void setRow(long[] newEntry, int position, int[] validColumns) {
    //removing the old row
    removeRow(position);
    addRow(newEntry, position, validColumns);

  }

  /**
   * Creates a new row from <code>newEntry</code>'s data and sets it to row
       * #<code> position</code>. The values of <code>newEntry</code> will be converted
   * as needed according to the type of column it is being set into.
   *
   * @param newEntry      the data to be set into row #<code>position</code>
   * @param position      the row number to be set.
   * @param validColumns  the valid indices of the new row
   */
  protected void setRow(short[] newEntry, int position, int[] validColumns) {
    //removing the old row
    removeRow(position);
    addRow(newEntry, position, validColumns);
  }

  /**
   * Creates a new row from <code>newEntry</code>'s data and sets it to row
       * #<code> position</code>. The values of <code>newEntry</code> will be converted
   * as needed according to the type of column it is being set into.
   *
   * @param newEntry      the data to be set into row #<code>position</code>
   * @param position      the row number to be set.
   * @param validColumns  the valid indices of the new row
   */
  protected void setRow(String[] newEntry, int position, int[] validColumns) {
    //removing the old row
    removeRow(position);
    addRow(newEntry, position, validColumns);
  }

  /**
   * Creates a new row from <code>newEntry</code>'s data and sets it to row
       * #<code> position</code>. The values of <code>newEntry</code> will be converted
   * as needed according to the type of column it is being set into.
   *
   * @param newEntry      the data to be set into row #<code>position</code>
   * @param position      the row number to be set.
   * @param validColumns  the valid indices of the new row
   */
  protected void setRow(Object[] newEntry, int position, int[] validColumns) {
    //removing the old row
    removeRow(position);
    addRow(newEntry, position, validColumns);

  }

  /**
   * ****************************************************
   * setColumn methods
   * ****************************************************
   */

  /**
   * Creates a new int column from <code>newEntry</code>'s data and sets it to
   * Column #<code> position</code>.
   *
   * Note - Since this is a Sparse Table, the values of <code>newEntry</code>
       * will be inserted only into the valid rows of column #<code>position</code>.
       * If such column does not exist - the valid rows will be the same as the valid
   * rows in all of the table.
   *
   * For more accurate setting - use method setColumn(int[], int, int[]).
   *
   * @param newEntry      the data to be set into column #<code>position</code>
   * @param position      the column number to be set.
   */
  protected void setColumn(int[] newEntry, int position) {

    int[] validRows;
    if (columns.contains(position)) {
      validRows = getColumnIndices(position);

    }
    else {
      validRows = getAllRows();

    }
    setColumn(newEntry, position, validRows);
  }

  /**
   * Creates a new int column from <code>newEntry</code>'s data and sets it to
   * Column #<code> position</code>, s.t. when setColumn returns each value
   * <code>val</code> at <code>newEntry[i]</code> is at row no. <code>validRows[i]</code>
   * and column no. <code>posirtion</code>. The original column at index <code>
   * position</code> (it such exists) is being removed.
   *
   * @param newEntry      the data to be set into column #<code>position</code>
   * @param position      the column number to be set.
   * @param validRows     the valid indices in the new column
   */
  protected void setColumn(int[] newEntry, int position, int[] validRows) {
    removeColumn(position);
    addColumn(newEntry, position, validRows);
  }

  /**
       * Creates a new float column from <code>newEntry</code>'s data and sets it to
   * Column #<code> position</code>, s.t. when setColumn returns each value
   * <code>val</code> at <code>newEntry[i]</code> is at row no. <code>validRows[i]</code>
   * and column no. <code>posirtion</code>. The original column at index <code>
   * position</code> (it such exists) is being removed.
   *
   * @param newEntry      the data to be set into column #<code>position</code>
   * @param position      the column number to be set.
   * @param validRows     the valid indices in the new column
   */
  protected void setColumn(float[] newEntry, int position, int[] validRows) {
    removeColumn(position);
    addColumn(newEntry, position, validRows);
  }

  /**
       * Creates a new byte[] column from <code>newEntry</code>'s data and sets it to
   * Column #<code> position</code>, s.t. when setColumn returns each value
   * <code>val</code> at <code>newEntry[i]</code> is at row no. <code>validRows[i]</code>
   * and column no. <code>posirtion</code>. The original column at index <code>
   * position</code> (it such exists) is being removed.
   *
   * @param newEntry      the data to be set into column #<code>position</code>
   * @param position      the column number to be set.
   * @param validRows     the valid indices in the new column
   */
  protected void setColumn(byte[][] newEntry, int position, int[] validRows) {
    removeColumn(position);
    addColumn(newEntry, position, validRows);
  }

  /**
       * Creates a new char[] column from <code>newEntry</code>'s data and sets it to
   * Column #<code> position</code>, s.t. when setColumn returns each value
   * <code>val</code> at <code>newEntry[i]</code> is at row no. <code>validRows[i]</code>
   * and column no. <code>posirtion</code>. The original column at index <code>
   * position</code> (it such exists) is being removed.
   *
   * @param newEntry      the data to be set into column #<code>position</code>
   * @param position      the column number to be set.
   * @param validRows     the valid indices in the new column
   */
  protected void setColumn(char[][] newEntry, int position, int[] validRows) {
    removeColumn(position);
    addColumn(newEntry, position, validRows);
  }

  /**
       * Creates a new Object column from <code>newEntry</code>'s data and sets it to
   * Column #<code> position</code>, s.t. when setColumn returns each value
   * <code>val</code> at <code>newEntry[i]</code> is at row no. <code>validRows[i]</code>
   * and column no. <code>posirtion</code>. The original column at index <code>
   * position</code> (it such exists) is being removed.
   *
   * @param newEntry      the data to be set into column #<code>position</code>
   * @param position      the column number to be set.
   * @param validRows     the valid indices in the new column
   */
  protected void setColumn(Object[] newEntry, int position, int[] validRows) {
    removeColumn(position);
    addColumn(newEntry, position, validRows);
  }

  /**
       * Creates a new String column from <code>newEntry</code>'s data and sets it to
   * Column #<code> position</code>, s.t. when setColumn returns each value
   * <code>val</code> at <code>newEntry[i]</code> is at row no. <code>validRows[i]</code>
   * and column no. <code>posirtion</code>. The original column at index <code>
   * position</code> (it such exists) is being removed.
   *
   * @param newEntry      the data to be set into column #<code>position</code>
   * @param position      the column number to be set.
   * @param validRows     the valid indices in the new column
   */
  protected void setColumn(String[] newEntry, int position, int[] validRows) {
    removeColumn(position);
    addColumn(newEntry, position, validRows);
  }

  /**
       * Creates a new short column from <code>newEntry</code>'s data and sets it to
   * Column #<code> position</code>, s.t. when setColumn returns each value
   * <code>val</code> at <code>newEntry[i]</code> is at row no. <code>validRows[i]</code>
   * and column no. <code>posirtion</code>. The original column at index <code>
   * position</code> (it such exists) is being removed.
   *
   * @param newEntry      the data to be set into column #<code>position</code>
   * @param position      the column number to be set.
   * @param validRows     the valid indices in the new column
   */
  protected void setColumn(short[] newEntry, int position, int[] validRows) {
    removeColumn(position);
    addColumn(newEntry, position, validRows);
  }

  /**
   * Creates a new long column from <code>newEntry</code>'s data and sets it to
   * Column #<code> position</code>, s.t. when setColumn returns each value
   * <code>val</code> at <code>newEntry[i]</code> is at row no. <code>validRows[i]</code>
   * and column no. <code>posirtion</code>. The original column at index <code>
   * position</code> (it such exists) is being removed.
   *
   * @param newEntry      the data to be set into column #<code>position</code>
   * @param position      the column number to be set.
   * @param validRows     the valid indices in the new column
   */
  protected void setColumn(long[] newEntry, int position, int[] validRows) {
    removeColumn(position);
    addColumn(newEntry, position, validRows);
  }

  /**
       * Creates a new boolean column from <code>newEntry</code>'s data and sets it to
   * Column #<code> position</code>, s.t. when setColumn returns each value
   * <code>val</code> at <code>newEntry[i]</code> is at row no. <code>validRows[i]</code>
   * and column no. <code>posirtion</code>. The original column at index <code>
   * position</code> (it such exists) is being removed.
   *
   * @param newEntry      the data to be set into column #<code>position</code>
   * @param position      the column number to be set.
   * @param validRows     the valid indices in the new column
   */
  protected void setColumn(boolean[] newEntry, int position, int[] validRows) {
    removeColumn(position);
    addColumn(newEntry, position, validRows);
  }

  /**
   * Creates a new byte column from <code>newEntry</code>'s data and sets it to
   * Column #<code> position</code>, s.t. when setColumn returns each value
   * <code>val</code> at <code>newEntry[i]</code> is at row no. <code>validRows[i]</code>
   * and column no. <code>posirtion</code>. The original column at index <code>
   * position</code> (it such exists) is being removed.
   *
   * @param newEntry      the data to be set into column #<code>position</code>
   * @param position      the column number to be set.
   * @param validRows     the valid indices in the new column
   */
  protected void setColumn(byte[] newEntry, int position, int[] validRows) {
    removeColumn(position);
    addColumn(newEntry, position, validRows);

  }

  /**
   * Creates a new char column from <code>newEntry</code>'s data and sets it to
   * Column #<code> position</code>, s.t. when setColumn returns each value
   * <code>val</code> at <code>newEntry[i]</code> is at row no. <code>validRows[i]</code>
   * and column no. <code>posirtion</code>. The original column at index <code>
   * position</code> (it such exists) is being removed.
   *
   * @param newEntry      the data to be set into column #<code>position</code>
   * @param position      the column number to be set.
   * @param validRows     the valid indices in the new column
   */
  protected void setColumn(char[] newEntry, int position, int[] validRows) {
    removeColumn(position);
    addColumn(newEntry, position, validRows);
  }

  /**
       * Creates a new double column from <code>newEntry</code>'s data and sets it to
   * Column #<code> position</code>, s.t. when setColumn returns each value
   * <code>val</code> at <code>newEntry[i]</code> is at row no. <code>validRows[i]</code>
   * and column no. <code>posirtion</code>. The original column at index <code>
   * position</code> (it such exists) is being removed.
   *
   * @param newEntry      the data to be set into column #<code>position</code>
   * @param position      the column number to be set.
   * @param validRows     the valid indices in the new column
   */
  protected void setColumn(double[] newEntry, int position, int[] validRows) {
    removeColumn(position);
    addColumn(newEntry, position, validRows);
  }

  /**
       * Creates a new float column from <code>newEntry</code>'s data and sets it to
   * Column #<code> position</code>.
   *
   * Note - Since this is a Sparse Table, the values of <code>newEntry</code>
       * will be inserted only into the valid rows of column #<code>position</code>.
       * If such column does not exist - the valid rows will be the same as the valid
   * rows in all of the table.
   *
   * For more accurate setting - use method setColumn(int[], int, int[]).
   *
   * @param newEntry      the data to be set into column #<code>position</code>
   * @param position      the column number to be set.
   */
  protected void setColumn(float[] newEntry, int position) {

    int[] validRows;
    if (columns.contains(position)) {
      validRows = getColumnIndices(position);

    }
    else {
      validRows = getAllRows();

    }
    setColumn(newEntry, position, validRows);
  }

  /**
       * Creates a new double column from <code>newEntry</code>'s data and sets it to
   * Column #<code> position</code>.
   *
   * Note - Since this is a Sparse Table, the values of <code>newEntry</code>
       * will be inserted only into the valid rows of column #<code>position</code>.
       * If such column does not exist - the valid rows will be the same as the valid
   * rows in all of the table.
   *
   * For more accurate setting - use method setColumn(int[], int, int[]).
   *
   * @param newEntry      the data to be set into column #<code>position</code>
   * @param position      the column number to be set.
   */
  protected void setColumn(double[] newEntry, int position) {

    int[] validRows;
    if (columns.contains(position)) {
      validRows = getColumnIndices(position);

    }
    else {
      validRows = getAllRows();

    }
    setColumn(newEntry, position, validRows);
  }

  /**
   * Creates a new long column from <code>newEntry</code>'s data and sets it to
   * Column #<code> position</code>.
   *
   * Note - Since this is a Sparse Table, the values of <code>newEntry</code>
       * will be inserted only into the valid rows of column #<code>position</code>.
       * If such column does not exist - the valid rows will be the same as the valid
   * rows in all of the table.
   *
   * For more accurate setting - use method setColumn(int[], int, int[]).
   *
   * @param newEntry      the data to be set into column #<code>position</code>
   * @param position      the column number to be set.
   */
  protected void setColumn(long[] newEntry, int position) {

    int[] validRows;
    if (columns.contains(position)) {
      validRows = getColumnIndices(position);

    }
    else {
      validRows = getAllRows();

    }
    setColumn(newEntry, position, validRows);
  }

  /**
       * Creates a new short column from <code>newEntry</code>'s data and sets it to
   * Column #<code> position</code>.
   *
   * Note - Since this is a Sparse Table, the values of <code>newEntry</code>
       * will be inserted only into the valid rows of column #<code>position</code>.
       * If such column does not exist - the valid rows will be the same as the valid
   * rows in all of the table.
   *
   * For more accurate setting - use method setColumn(int[], int, int[]).
   *
   * @param newEntry      the data to be set into column #<code>position</code>
   * @param position      the column number to be set.
   */
  protected void setColumn(short[] newEntry, int position) {

    int[] validRows;
    if (columns.contains(position)) {
      validRows = getColumnIndices(position);

    }
    else {
      validRows = getAllRows();

    }
    setColumn(newEntry, position, validRows);
  }

  /**
       * Creates a new boolean column from <code>newEntry</code>'s data and sets it to
   * Column #<code> position</code>.
   *
   * Note - Since this is a Sparse Table, the values of <code>newEntry</code>
       * will be inserted only into the valid rows of column #<code>position</code>.
       * If such column does not exist - the valid rows will be the same as the valid
   * rows in all of the table.
   *
   * For more accurate setting - use method setColumn(int[], int, int[]).
   *
   * @param newEntry      the data to be set into column #<code>position</code>
   * @param position      the column number to be set.
   */
  protected void setColumn(boolean[] newEntry, int position) {

    int[] validRows;
    if (columns.contains(position)) {
      validRows = getColumnIndices(position);

    }
    else {
      validRows = getAllRows();

    }
    setColumn(newEntry, position, validRows);

  }

  /**
       * Creates a new String column from <code>newEntry</code>'s data and sets it to
   * Column #<code> position</code>.
   *
   * Note - Since this is a Sparse Table, the values of <code>newEntry</code>
       * will be inserted only into the valid rows of column #<code>position</code>.
       * If such column does not exist - the valid rows will be the same as the valid
   * rows in all of the table.
   *
   * For more accurate setting - use method setColumn(int[], int, int[]).
   *
   * @param newEntry      the data to be set into column #<code>position</code>
   * @param position      the column number to be set.
   */
  protected void setColumn(String[] newEntry, int position) {

    int[] validRows;
    if (columns.contains(position)) {
      validRows = getColumnIndices(position);

    }
    else {
      validRows = getAllRows();

    }
    setColumn(newEntry, position, validRows);
  }

  /**
       * Creates a new char[] column from <code>newEntry</code>'s data and sets it to
   * Column #<code> position</code>.
   *
   * Note - Since this is a Sparse Table, the values of <code>newEntry</code>
       * will be inserted only into the valid rows of column #<code>position</code>.
       * If such column does not exist - the valid rows will be the same as the valid
   * rows in all of the table.
   *
   * For more accurate setting - use method setColumn(int[], int, int[]).
   *
   * @param newEntry      the data to be set into column #<code>position</code>
   * @param position      the column number to be set.
   */
  protected void setColumn(char[][] newEntry, int position) {

    int[] validRows;
    if (columns.contains(position)) {
      validRows = getColumnIndices(position);

    }
    else {
      validRows = getAllRows();

    }
    setColumn(newEntry, position, validRows);
  }

  /**
       * Creates a new byte[] column from <code>newEntry</code>'s data and sets it to
   * Column #<code> position</code>.
   *
   * Note - Since this is a Sparse Table, the values of <code>newEntry</code>
       * will be inserted only into the valid rows of column #<code>position</code>.
       * If such column does not exist - the valid rows will be the same as the valid
   * rows in all of the table.
   *
   * For more accurate setting - use method setColumn(int[], int, int[]).
   *
   * @param newEntry      the data to be set into column #<code>position</code>
   * @param position      the column number to be set.
   */
  protected void setColumn(byte[][] newEntry, int position) {

    int[] validRows;
    if (columns.contains(position)) {
      validRows = getColumnIndices(position);

    }
    else {
      validRows = getAllRows();

    }
    setColumn(newEntry, position, validRows);
  }

  /**
       * Creates a new Object column from <code>newEntry</code>'s data and sets it to
   * Column #<code> position</code>.
   *
   * Note - Since this is a Sparse Table, the values of <code>newEntry</code>
       * will be inserted only into the valid rows of column #<code>position</code>.
       * If such column does not exist - the valid rows will be the same as the valid
   * rows in all of the table.
   *
   * For more accurate setting - use method setColumn(int[], int, int[]).
   *
   * @param newEntry      the data to be set into column #<code>position</code>
   * @param position      the column number to be set.
   */
  protected void setColumn(Object[] newEntry, int position) {

    int[] validRows;
    if (columns.contains(position)) {
      validRows = getColumnIndices(position);

    }
    else {
      validRows = getAllRows();

    }
    setColumn(newEntry, position, validRows);
  }

  /**
   * Creates a new byte column from <code>newEntry</code>'s data and sets it to
   * Column #<code> position</code>.
   *
   * Note - Since this is a Sparse Table, the values of <code>newEntry</code>
       * will be inserted only into the valid rows of column #<code>position</code>.
       * If such column does not exist - the valid rows will be the same as the valid
   * rows in all of the table.
   *
   * For more accurate setting - use method setColumn(int[], int, int[]).
   *
   * @param newEntry      the data to be set into column #<code>position</code>
   * @param position      the column number to be set.
   */
  protected void setColumn(byte[] newEntry, int position) {

    int[] validRows;
    if (columns.contains(position)) {
      validRows = getColumnIndices(position);

    }
    else {
      validRows = getAllRows();

    }
    setColumn(newEntry, position, validRows);
  }

  /**
   * Creates a new char column from <code>newEntry</code>'s data and sets it to
   * Column #<code> position</code>.
   *
   * Note - Since this is a Sparse Table, the values of <code>newEntry</code>
       * will be inserted only into the valid rows of column #<code>position</code>.
       * If such column does not exist - the valid rows will be the same as the valid
   * rows in all of the table.
   *
   * For more accurate setting - use method setColumn(int[], int, int[]).
   *
   * @param newEntry      the data to be set into column #<code>position</code>
   * @param position      the column number to be set.
   */
  protected void setColumn(char[] newEntry, int position) {

    int[] validRows;
    if (columns.contains(position)) {
      validRows = getColumnIndices(position);

    }
    else {
      validRows = getAllRows();

    }
    setColumn(newEntry, position, validRows);
  }

  /**
   * *****************************************************
   * remove methods
   * *****************************************************
   */



  /**
   * Removes the entry at the specified location.
   *
   * @param row     the row number of the entry to be removed.
   * @param column  the column number of the entry to be removed.
   */
  protected void removeEntry(int row, int column) {
    if (rows.containsKey(row)) {
      ( (VIntHashSet) rows.get(row)).remove(column);
      //  removeEmptyRow(row);
    }

    if (columns.containsKey(column)) {
      ( (Column) columns.get(column)).removeRow(row);
      //    removeEmptyColumn(column);

    }
  }

  /* @todo: make these methods active.*/

  /*
   private void removeEmptyRow(int position){
     if(rows.containsKey(position)){
     VIntHashSet tempR = (VIntHashSet) rows.get(position);
      if(tempR.isEmpty())
    rows.remove(position);
     }
   }
     private void removeEmptyColumn(int position){
      if(columns.containsKey(position))
       if(getColumn(position).getNumEntries() == 0){
    //debug
    System.out.println("\n\nremoving column no. " + position + "\n");
    //end debug
    columns.remove(position);
       }
     }
   */


  /**
   * Removes row no. i from this table if <code>flags[i] == true</code>
   *
   * @param flags     an array of flags to indicate which row is to be removed
   */
  protected void removeRowsByFlag(boolean[] flags) {
    int cnt = 0;
    for (int i = 0; i < flags.length; i++) {
      if (flags[i]) {
        removeRow(i - cnt++);
      }
    }
  }

  /**
   * Removes coloumn no. i from this table if <code>flags[i] == true</code>
   *
       * @param flags     an array of flags to indicate which column is to be removed
   */
  protected void removeColumnsByFlag(boolean[] flags) {
    for (int i = 0; i < flags.length; i++) {
      if (flags[i]) {
        removeColumn(i);
      }
    }
  }

  /**
   * Removes rows from this table if their indices is in <code>indices</code>
   *
   * @param     an array of indices of rows to be removed from this table
   */
  protected void removeRowsByIndex(int[] indices) {
    for (int i = 0; i < indices.length; i++) {
      removeRow(indices[i]);
    }
  }

  /**
       * Removes columns from this table if their indices is in <code>indices</code>
   *
   * @param     an array of indices of columns to be removed from this table
   */
  protected void removeColumnsByIndex(int[] indices) {

    for (int i = 0; i < indices.length; i++) {
      columns.remove(indices[i]);

    }

    IndicesRemover remover = new IndicesRemover(indices);
    rows.forEachValue(remover);
    /*
        int[] allRows = this.getAllRows();
        for(int i=0; i<allRows.length; i++)
          ((VIntHashSet)rows.get(allRows[i])).removeAll(indices);
     */
  }

  /**
   * *****************************************************
   * reorder methods
   * *****************************************************
   */


  /**
   * Reorders the rows in this table, s.t.:
   * for each key k in <codE>newOrder</code>: put the row which its index is
   * mapped to k in <code>newOrder</code> at index k in the returned value.
   *
       * @param newOrder    an int to int hashmap that defines the new order of rows
       * @return            a SparseMutableTable with same content as this one, only
   *                    with different order of rows.
   */
  protected Table reorderRows(VIntIntHashMap newOrder) {
    SparseMutableTable retVal = new SparseMutableTable();
    int[] cols = columns.keys();
    for (int i = 0; i < cols.length; i++) {
      retVal.setColumn(cols[i],
                       ( (AbstractSparseColumn) ( (AbstractSparseColumn)
                                                 getColumn(cols[i])).
                        reorderRows(newOrder)));

    }
    retVal.transformations = (ArrayList) transformations.clone();
    //copying general attributes
    retVal.copyAttributes(this);
    return retVal;
  }



  /**
   * Reorders the columns in this table, s.t.:
   * for each key k in <codE>newOrder</code>: put the column which its index is
   * mapped to k in <code>newOrder</code> at index k in the returned value.
   *
       * @param newOrder    an int to int hashmap that defines the new order of columns
       * @return            a SparseMutableTable with same content as this one, only
   *                    with different order of columns.
   */
  protected Table reorderColumns(VIntIntHashMap newOrder) {
    SparseMutableTable retVal = new SparseMutableTable(this);

    retVal.columns = (VIntObjectHashMap) columns.reorder(newOrder);
    return retVal;

  }



  /**
   * Adds the column index <code>column</code> to the row set <code>row</code>
   *
   * @param column    the index to be added
       * @param row       the row index to which the index <code>column<c/doe> is added
   */
  protected void addCol2Row(int column, int row) {
    // XIAOLEI - just added some comments

    /* first check if the row exists */
    if (!rows.containsKey(row)) {
      VIntHashSet newRow = new VIntHashSet();
      rows.put(row, newRow);
    }

    /* add the column to the row */
    if (! ( (VIntHashSet) rows.get(row)).contains(column)) {
      ( (VIntHashSet) rows.get(row)).add(column);
    }
  }

  //=================
  // Private Methods
  //=================
























































  /**
   * **************************************
   * setType methods
   * **************************************
   */



  /**
   * ******************************************************
   * general set methods
   * ******************************************************
   */


  /**
   * "Clips" the columns at the "end" of this table so that the greatest index
   * number won't be greater than <codE>numCols-1</code>
   *
   * @param numCols    the new upper bound for number of columns in this table
   */
  public void setNumColumns(int numCols) {
    if (numColumns > numCols) {
      removeColumns(numColumns, numColumns - numCols);
    }
    numColumns = numCols;
  }

  /**
   * "Clips" the rows at the "end" of this table so that the greatest index
   * number won't be greater than <codE>newCapacity</code>
   *
   * @param newCapacity    the new upper bound for number of rows in this table
   */
  public void setNumRows(int newCapacity) {
    if (numRows > newCapacity) {
      removeRows(newCapacity, numRows - newCapacity);
    }
  }


  /**
   * copies the content of <code>srcTable</cdoe> into this table.
   */
  public void copy(SparseTable srcTable) {
    if (srcTable instanceof SparseMutableTable) {
      transformations = (ArrayList) ( (SparseMutableTable) srcTable).
          transformations.clone();
    }
    super.copy(srcTable);
  }




  public void computeNumColumns() {
    numColumns = VHashService.getMaxKey(columns) + 1;
  }

  public void computeNumRows() {
    numRows = VHashService.getMaxKey(rows) + 1;
  }

  /**
   * Updates the rows map with a new row at index <code>newRow</code> and valid
   * columnas specified by <code>indices</code>.
   *
   * @param newRow   the new row index.
   * @param indices  the indices of valid columns in the new row.
   */
  /*
    protected void updateRows(int newRow, int[] indices){
   VIntHashSet row = new VIntHashSet(indices.length);
    row.addAll(indices);
    rows.put(newRow, row);
    }
   */

  public void addColumn(AbstractSparseColumn newColumn) {
    setColumn(numColumns, newColumn);

  }

  /**
   * Adds an empty column at index <code>newColIndex</code> of type <code>
   * type</codE> with capacity <code>size</codE>.
   *
   * @param newColIndex     the index insertion of the new Column.
   * @param type            the type of the new Column.
   * @param size            the size of the new Column.
   */
  public void addColumn(int newColIndex, int type, int size) {
    Column newCol = null;
    switch (type) {

      case ColumnTypes.BOOLEAN:
        newCol = new SparseBooleanColumn(size);
        break;

      case ColumnTypes.BYTE:
        newCol = new SparseByteColumn(size);
        break;

      case ColumnTypes.CHAR:
        newCol = new SparseCharColumn(size);
        break;

      case ColumnTypes.BYTE_ARRAY:
        newCol = new SparseByteArrayColumn(size);
        break;

      case ColumnTypes.INTEGER:
        newCol = new SparseIntColumn(size);
        break;

      case ColumnTypes.DOUBLE:
        newCol = new SparseDoubleColumn(size);
        break;

      case ColumnTypes.CHAR_ARRAY:
        newCol = new SparseCharArrayColumn(size);
        break;

      case ColumnTypes.LONG:
        newCol = new SparseLongColumn(size);
        break;
      case ColumnTypes.FLOAT:
        newCol = new SparseFloatColumn(size);
        break;

      case ColumnTypes.SHORT:
        newCol = new SparseShortColumn(size);
        break;

      case ColumnTypes.STRING:
        newCol = new SparseStringColumn(size);
        break;

      case ColumnTypes.OBJECT:
        newCol = new SparseObjectColumn(size);
        break;
    } //switch

    //columns.put(newColIndex, newCol);
    this.insertColumn( (AbstractSparseColumn) newCol, newColIndex);

    if (newColIndex >= numColumns) {
      numColumns = newColIndex + 1;
    }
  } //addColumn


  public Table getSubsetByReference(int start, int len) {
    int[] rows = new int[len];
    int ctr = start;
    for (int i = 0; i < len; i++) {
      rows[i] = start;
      start++;
    }
    ExampleTable et = toExampleTable();
    et.setTrainingSet(rows);
    return et.getTrainTable();

  }

  public Table getSubsetByReference(int[] rows) {
    ExampleTable et = toExampleTable();
    et.setTrainingSet(rows);
    return et.getTrainTable();
  }


  /**
   * updates the columns and rows maps regarding the insertion of a new column.
   *
   * @param index     the index insertion of a new column
   * @param col       the new oclumn.
   */
  protected void setColumn(int index, AbstractSparseColumn col) {

    columns.put(index, col); //updating the columns map

    //updating the rows map
    int[] rowNum = col.getIndices();
    for (int i = 0; i < rowNum.length; i++) {
      addCol2Row(index, rowNum[i]);

    }

    if (numColumns <= index) {
      numColumns = index + 1;

    }
    if (rowNum.length > 0 && numRows <= rowNum[rowNum.length - 1]) {
      numRows = rowNum[rowNum.length - 1] + 1;

    }
  }

  /**
   * Returns the Column at index <codE>pos</code>.
   *
   * @param pos   the index number of the returned Column
   * @return      the Column at index <code>pos</codE>
   */
  public Column getColumn(int pos) {
    return (Column) columns.get(pos);
  }

  public void addColumn(int type) {
    AbstractSparseColumn col = null;
    switch (type) {
      case ColumnTypes.BOOLEAN:
        col = new SparseBooleanColumn();
        break;
      case ColumnTypes.BYTE:
        col = new SparseByteColumn();
        break;
      case ColumnTypes.CHAR:
        col = new SparseCharColumn();
        break;
      case ColumnTypes.DOUBLE:
        col = new SparseDoubleColumn();
        break;

      case ColumnTypes.FLOAT:
        col = new SparseFloatColumn();
        break;
      case ColumnTypes.BYTE_ARRAY:
        col = new SparseByteArrayColumn();
        break;
      case ColumnTypes.CHAR_ARRAY:
        col = new SparseCharArrayColumn();
        break;
      case ColumnTypes.INTEGER:
        col = new SparseIntColumn();
        break;

      case ColumnTypes.LONG:
        col = new SparseLongColumn();
        break;
      case ColumnTypes.SHORT:
        col = new SparseShortColumn();
        break;
      case ColumnTypes.OBJECT:
        col = new SparseObjectColumn();
        break;
      case ColumnTypes.STRING: //fall through to the default...
      default:
        col = new SparseStringColumn();
        break;

    } //switch case

    this.addColumn(col);
  }

  /**
   * Adds a column of type <code>type</code> at index no. <code>index</code>
   * to this table.
   * to be used after any existing column at the given index was removed.
   *
   * @param index     the index insertion of the new column.
   * @param type      the type of the new column as specified by ColumnTypes.
   */
  protected void addColumn(int index, int type) {
    AbstractSparseColumn col = null;
    switch (type) {
      case ColumnTypes.BOOLEAN:
        col = new SparseBooleanColumn();
        break;
      case ColumnTypes.BYTE:
        col = new SparseByteColumn();
        break;
      case ColumnTypes.CHAR:
        col = new SparseCharColumn();
        break;
      case ColumnTypes.DOUBLE:
        col = new SparseDoubleColumn();
        break;

      case ColumnTypes.FLOAT:
        col = new SparseFloatColumn();
        break;
      case ColumnTypes.BYTE_ARRAY:
        col = new SparseByteArrayColumn();
        break;
      case ColumnTypes.CHAR_ARRAY:
        col = new SparseCharArrayColumn();
        break;
      case ColumnTypes.INTEGER:
        col = new SparseIntColumn();
        break;

      case ColumnTypes.LONG:
        col = new SparseLongColumn();
        break;
      case ColumnTypes.SHORT:
        col = new SparseShortColumn();
        break;
      case ColumnTypes.OBJECT:
        col = new SparseObjectColumn();
        break;
      case ColumnTypes.STRING: //fall through to the default...
      default:
        col = new SparseStringColumn();
        break;

    } //switch case

    setColumn(index, col);
  }

  /**
       * Returns a TestTable with data from row index no. <code> start</code> in the
   * test set through row index no. <code>start+len</code> in the test set.
   *
       * @param start       index number into the test set of the row at which begins
   *                    the subset.
   * @param len         number of consequetive rows to include in the subset.
   */
  public static Table getSubset(int[] indices, SparseTable table) {

    int numrows = table.getNumRows();
    for (int i = 0, n = indices.length; i < n; i++) {
      if ( (indices[i] < 0) || (indices[i] >= numrows)) {
        throw new IndexOutOfBoundsException("Row: " + indices[i] +
                                            "not in table.");
      }
    }

    // the returned value
    SparseMutableTable retVal = new SparseMutableTable();

    int[] cols = table.getAllColumns();
    for (int i = 0; i < cols.length; i++) {
      AbstractSparseColumn currentCol = (AbstractSparseColumn) table.getColumn(
          cols[i]);
      retVal.setColumn(cols[i],
                       (AbstractSparseColumn) currentCol.getSubset(indices));
    }

    retVal.copyAttributes(table);
    retVal.computeNumColumns();
    retVal.computeNumRows();

    return retVal;
  }




  /*
      public Table reorderRows(int[] newOrder, int begin, int end) {
        SparseMutableTable retVal = (SparseMutableTable )copy();
        retVal.removeRows(begin, end-begin+1);
        int[] colIndices = getAllColumns();
        for (int i=0; i<colIndices.length; i++){
     Column temp = ((AbstractSparseColumn) getColumn(colIndices[i])).reorderRows(newOrder, begin, end);
     retVal.insertColumn((AbstractSparseColumn)temp, colIndices[i]);
        }
       return retVal;
      }
  */


} //SparseMutableTable
