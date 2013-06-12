package ncsa.d2k.modules.core.datatype.table.sparse.columns;

//==============
// Java Imports
//==============

//===============
// Other Imports
//===============

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.datatype.table.sparse.*;
import ncsa.d2k.modules.core.datatype.table.sparse.primitivehash.*;

/**
 * Title:        Sparse Table
 * Description:  Sparse Table projects will implement data structures compatible to the interface tree of Table, for sparsely stored data.
 * Copyright:    Copyright (c) 2002
 * Company:      ncsa
 * @author vered goren
 * @version 1.0
 */
abstract public class AbstractSparseColumn
    extends AbstractColumn {

  //==============
  // Data Members
  //==============

  protected VIntHashSet missing;
  protected VIntHashSet empty;


  //================
  // Constructor(s)
  //================

  /**
   * put your documentation comment here
   */
  public AbstractSparseColumn() {
    super();
    missing = new VIntHashSet();
    empty = new VIntHashSet();
    super.setLabel("");
  }

  //================
  // Public Methods
  //================

  /**
   * FIX THIS -- DDS
   * @return
   */
  public int getNumMissingValues() {
    return missing.size();
  }


  /**
   * put your documentation comment here
   * @param indices
   * @return
   */
  public abstract Column getSubset(int[] indices);

  /**
   * copies only the label and the comment of <code>srcCol</code> into this
   * column.
   * @param srcCol - an object of type AbstractSparseColumn, from which the
   *                 attributes are being copied.
   *
   */
  public void copyAttributes(AbstractSparseColumn srcCol) {
    setLabel(srcCol.getLabel());
    setComment(srcCol.getComment());
  }

  /**
   * Copies the missing set and label and comment of <code>srcCol</code>
   * into this column
   *
   * @param srcCol    the column from which the data is copied.
   */
  public void copy(AbstractSparseColumn srcCol) {
    this.missing = srcCol.missing.copy();
    this.empty = srcCol.empty.copy();
    copyAttributes(srcCol);
  }

  /**
   * Returns the valid rows in this column, sorted.
   *
       * @return    a sorted int array with all the valid row numbers in this column.
   */
  public int[] getIndices() {
    return VHashService.getIndices(getElements());
  }

  /**
   * Returns the valid rows in this column, UN SORTED
   *
   *
   * @return    an UN-SORTED int array with all the valid row numbers in this column.
   */
  public int[] keys() {
    return getElements().keys();
  }

  /**
   * Adds  <code>newEntry</code> to the end of this column
   *
   * @param newEntry - the data to be inserted at the end of this column
   */
  public void addRow(Object newEntry) {
    setObject(newEntry, getNumRows());
  }

  /**
   * Returns the total number of data items that this column holds.
   *
   * @return    the total number of data items that this column holds.
   */
  public int getNumEntries() {
    int numEntries = 0;
    for (int i = 0; i < getNumRows(); i++) {
      if (!isValueMissing(i) && !isValueEmpty(i)) {
        numEntries++;
      }
    }
    return numEntries;
  }

  /**
   * Returns the maximal valid row number in this column + 1, because counting
   * of rows starts from zero.
   *
   * @return    the maximal valid row number in this column + 1
   */
  public int getNumRows() {
    return VHashService.getMaxKey(getElements()) + 1;
  }

  /**
   * Removes all elements stored in this column at rows #<code>pos</code>
   * through <code>pos+len</code>
   *
   * @param pos - the row number from which to begin removing data
       * @param len - number of consequitive rows to remove after row #<code>pos</code>
   */
  public void removeRows(int pos, int len) {
    int[] indices = VHashService.getIndicesInRange(pos, pos + len, getElements());
    for (int i = 0; i < indices.length; i++) {
      removeRow(indices[i]);
    }
  }

  /**
   * Removes entries from this column according to a set of flags.
   * @param flags - a boolen array. if flags[i] is true then row # i
   * is being removed. if flags is smaller than the capacity of this column -
   * removing the rest of the rows that their number is higher than the length
   * of flags.
   */
  public void removeRowsByFlag(boolean[] flags) {
    int i;
    for (i = 0; i < flags.length; i++) {
      if (flags[i]) {
        removeRow(i);
      }
    }
    int[] toRemove = getRowsInRange(i, getNumRows());
    this.removeRowsByIndex(toRemove);
  }

  /**
   * Removes rows from this column according to given row numbers in
   * <code>indices</code>
   *
   * @param indices - the row numbers to be removed.
   */
  public void removeRowsByIndex(int[] indices) {
    for (int i = 0; i < indices.length; i++) {
      removeRow(indices[i]);
    }
  }

  /**
   * adjust the number of rows in this column: removes all rows that
   * their number is higher than <code>newCapacity</code>
   *
   * @param newCapacity    upper border for maximal row number in this column
   */
  public void setNumRows(int newCapacity) {
    //    VHashMap column = getElements();
    int[] indices = this.getIndices();
    int ignore = 0;
    if (newCapacity < getNumRows()) {
      for (int i = indices.length - 1; (i >= 0) && (newCapacity < indices[i]
          + 1); i--) {
        removeRow(indices[i]);
      }
    }
  }

  /**
   * Tests for the validity of 2 values in this column.
   * @param pos1     row number of first value to be validated
   * @param pos2     row number of second value to be validated
   * @return         an int representing the relation between the values.
   *                 If the value at row #<code>pos1</code> is either missing
       *                 empty or does not exist and value at row #<code>pos2</code>
   *                 is a regular value - returns -1.
   *                 Returns 1 if the situation is vice versia.
   *                 Returns 0 if they are both not regular values.
   *                 Returns 2 if both values are regular.
   */
  public int validate(int pos1, int pos2) {
    boolean valid_1 = (doesValueExist(pos1) && !isValueEmpty(pos1) &&
                       !isValueMissing(pos1));
    boolean valid_2 = (doesValueExist(pos2) && !isValueEmpty(pos2) &&
                       !isValueMissing(pos2));
    return validate(valid_1, valid_2);
  }

  /**
       * Tests the validity of <code>obj</code> and the value at row #<code>pos</code>
   * int his column.
   * @param obj    the fist value to be validated
   * @param pos   the row number of the second value to be vlaidated
       * @return      an int representing the relation between the 2 values. For more
   *             details see validate(int, int)
   */
  public int validate(Object obj, int pos) {
    boolean valid = (doesValueExist(pos) && !isValueEmpty(pos) &&
                     !isValueMissing(pos));
    return validate(obj != null, valid);
  }

  /**
   * Retrieves an Object representation of row #<code>pos</code>
   *
   * @param pos    the row number from which to retrieve the Object
   * @return       Object representation of the data at row #<code>pos</code>
   */
  public Object getRow(int pos) {
    return getObject(pos);
  }

  /**
   * Sets the entry at row #<code>pos</code> to <code>newEntry</code>.
   *
   * @param newEntry       a new entry represented by an Object
   * @param pos            the position to set the new entry
   */
  public void setRow(Object newEntry, int pos) {
    setObject(newEntry, pos);
  }

  /**
       * Returns true if a double vlaue can be retrieved from row no. <coe>row</code>
   * in this column
   *
   * @param row   the row number to check if a double value can be retrieved from it
   * @return      true if a double value can be retrieved from row no.
   *              <code>row</code>. false if an exception occures while trying
   *              to retrieve the value
   */
  public boolean isDataNumeric(int row) {
    int colType = getType();
    //the only columns that numeric values might not be retrieves from are
    //the object columns
    if (! (colType == ColumnTypes.BYTE_ARRAY ||
           colType == ColumnTypes.CHAR_ARRAY
           || colType == ColumnTypes.STRING || colType == ColumnTypes.OBJECT)) {
      return true;
    }
    try {
      Double.parseDouble(SparseStringColumn.toStringObject(getObject(row)));
    }
    catch (Exception e) {
      return false;
    }
    return true;
  }

  /**
   * Returns true if a double value can be retrieved from each data item in
   * the column. return false if else.
   *
   * @return    true if this methods succeeds in retrieving a double value
   *            from each entry in this column. returns false if an exception
   *            is caught during the process.
   */
  public boolean isNumeric() {
    int colType = getType();
    //the only columns that numeric values might not be retrieves from are
    //the object columns
    if (! (colType == ColumnTypes.BYTE_ARRAY ||
           colType == ColumnTypes.CHAR_ARRAY
           || colType == ColumnTypes.STRING || colType == ColumnTypes.OBJECT)) {
      return true;
    }
    //retrieving row numbers
    int[] rowNumbers = getElements().keys();
    //for each row - trying to parse a double out of a string constructed
    //from the data
    boolean retVal = true;
    for (int i = 0; i < rowNumbers.length && retVal; i++) {
      if (!isValueMissing(rowNumbers[i]) && !isValueEmpty(rowNumbers[i])) {
        retVal = retVal && isDataNumeric(rowNumbers[i]);
      }
    }
    return retVal;
  }

  /**
   * Verifies if row no. <code>pos</code> holds a value.
   *
   * @param pos     the inspected row no.
   * @return        true if row no. <code>pos</code> holds a value, otherwise
   *                returns false.
   */
  public boolean doesValueExist(int pos) {
    return ( ( (VHashMap) getElements()).containsKey(pos));
  }

  /**
   * Verifies if row #<code>row</code> holds an empty value
   * @param row - the row which its value is being validated.
   * @return true if the value is empty, false otherwise
   */
  public boolean isValueEmpty(int row) {
    return empty.contains(row);
  }

  /**
   * Verifies if row #<code>row</code> holds a missing value
   * @param row - the row which its value is being validated.
   * @return true if the value is missing, false otherwise
   */
  public boolean isValueMissing(int row) {
    return missing.contains(row);
  }

  /**
   * Verifies if any missing values exist in the table
   * @param row - the row which its value is being validated.
   * @return true if the value is missing, false otherwise
   */
  public boolean hasMissingValues() {
    for (int i = 0; i < this.getNumRows(); i++) {
      if (this.isValueMissing(i)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Puts the data stored in this column into <code>buffer</code>.
   * <codE>buffer</codE> must be an array of some type. the values will be
   * converted as needed, according to the type of <codE>buffer</codE>.
   *
   * Since this is a sparse column it is best to use method
   * getData(Object, int[]) for more extenssive results.
   */
  public void getData(Object buffer) {
    int[] rowNumbers = this.getIndices();
    int size = rowNumbers.length;
    boolean numeric = isNumeric();
    if (buffer instanceof int[]) {
      //   if(!numeric) throw new DataFormatException("The columns in not numeric. Could not convert data to type int[]");
      int[] b1 = (int[]) buffer;
      for (int i = 0; i < b1.length && i < size; i++) {
        b1[i] = getInt(rowNumbers[i]);
      }
    }
    else if (buffer instanceof float[]) {
      // if(!numeric)
      // throw new DataFormatException(
      //     "The columns in not numeric. Could not convert data to type float[]");
      float[] b1 = (float[]) buffer;
      for (int i = 0; i < b1.length && i < size; i++) {
        b1[i] = getFloat(rowNumbers[i]);
      }
    }
    else if (buffer instanceof double[]) {
      //if(!numeric)
      //throw new DataFormatException(
      //    "The columns in not numeric. Could not convert data to type double[]");
      double[] b1 = (double[]) buffer;
      for (int i = 0; i < b1.length && i < size; i++) {
        b1[i] = getDouble(rowNumbers[i]);
      }
    }
    else if (buffer instanceof long[]) {
      //if(!numeric)
      //throw new DataFormatException(
      //    "The columns in not numeric. Could not convert data to type long[]");
      long[] b1 = (long[]) buffer;
      for (int i = 0; i < b1.length && i < size; i++) {
        b1[i] = getLong(rowNumbers[i]);
      }
    }
    else if (buffer instanceof short[]) {
      //if(!numeric)
      //throw new DataFormatException(
      //    "The columns in not numeric. Could not convert data to type short[]");
      short[] b1 = (short[]) buffer;
      for (int i = 0; i < b1.length && i < size; i++) {
        b1[i] = getShort(rowNumbers[i]);
      }
    }
    else if (buffer instanceof boolean[]) {
      boolean[] b1 = (boolean[]) buffer;
      for (int i = 0; i < b1.length && i < size; i++) {
        b1[i] = getBoolean(rowNumbers[i]);
      }
    }
    else if (buffer instanceof String[]) {
      String[] b1 = (String[]) buffer;
      for (int i = 0; i < b1.length && i < size; i++) {
        b1[i] = getString(rowNumbers[i]);
      }
    }
    else if (buffer instanceof char[][]) {
      char[][] b1 = (char[][]) buffer;
      for (int i = 0; i < b1.length && i < size; i++) {
        b1[i] = getChars(rowNumbers[i]);
      }
    }
    else if (buffer instanceof byte[][]) {
      byte[][] b1 = (byte[][]) buffer;
      for (int i = 0; i < b1.length && i < size; i++) {
        b1[i] = getBytes(rowNumbers[i]);
      }
    }
    else if (buffer instanceof Object[]) {
      Object[] b1 = (Object[]) buffer;
      for (int i = 0; i < b1.length && i < size; i++) {
        b1[i] = getObject(rowNumbers[i]);
      }
    }
    else if (buffer instanceof byte[]) {
      byte[] b1 = (byte[]) buffer;
      for (int i = 0; i < b1.length && i < size; i++) {
        b1[i] = getByte(rowNumbers[i]);
      }
    }
    else if (buffer instanceof char[]) {
      char[] b1 = (char[]) buffer;
      for (int i = 0; i < b1.length && i < size; i++) {
        b1[i] = getChar(rowNumbers[i]);
      }
    }
  }

  /**
   * Retrieve a new order for the valid rows of this column.
   * The new order is a sorted order.
   *
   * @return      a VIntIntHashMap that represents the new order s.t.:
       *              let retVal bew the returned value, (i,j) be keys in retVal and
   *              (x,y) be their mapped vlaue sin retVal, respectively.
       *              if i<j then the value at row no. x in this column is smaller than
   *              or equal to the value at row no. y in this column.
   */
  public VIntIntHashMap getNewOrder() {
    return getElements().getSortedOrder();
  }

  /**
   * Retrieve a new order for the valid rows of this column in the range <code>
   * [begin, end]</code>.
   * The new order is a sorted order.
   *
   * @param begin row no. from which starts the section to which a sorted order
   *              should be retrieved.
       * @param end   row no. at which ends the section to which a sorted order should
   *              be retrieved.
   * @return      a VIntIntHashMap that represents the new order s.t.:
       *              let retVal bew the returned value, (i,j) be keys in retVal and
   *              (x,y) be their mapped vlaue sin retVal, respectively.
       *              if i<j then the value at row no. x in this column is smaller than
   *              or equal to the value at row no. y in this column.
       *              this new order affects only the rows in the range specified by
   *              <code>begin</codE> and <code>end</codE>.
   */
  public VIntIntHashMap getNewOrder(int begin, int end) {
    if (end < begin) {
      return new VIntIntHashMap(0);
    }
    return getElements().getSortedOrder(begin, end);
  }

  /**
   * Sorts <code>t</code> according to the natural sorted order of this
   * column
   *
   * @param t   a table this column is part of, to be sorted.
   */
  public void sort(MutableTable t) {
    ( (SparseMutableTable) t).sort(getNewOrder());
  }

  /**
   * Sorts the rows in the range <codE>[begin, end]</code> in <code>t</code>
   * according to the natural sorting order of this column's rows in the
   * specified range. this column is part of <codE>t</code>.
   *
   * @param t        a table to sorts its rows.
   * @param begin    row no. at which starts the section to be sorted.
   * @param end      row no. at which ends the section to be sorted.
   */
  public void sort(MutableTable t, int begin, int end) {
    if (end < begin) {
      return;
    }
    ( (SparseMutableTable) t).sort(getNewOrder(begin, end));
  }

  /**
   * put your documentation comment here
   * @param newOrder
   * @return
   */
  public abstract Column reorderRows(VIntIntHashMap newOrder);

  /**
   * Reorders the data stored in this column in a new column.
   * Does not change this column.
   *
   * Algorithm: copy this column into the returned value.
   * for each entry <code>newOrder[i]</code> that is a valid row in
   * this column - put its value in row no. i in the returned value.
   *
   * @param newOrder - an int array, which its elements define a new order for
   *                   this column.
       * @return an AbstractSparseColumn ordered according to <code>newOrder</code>.
   */
  public Column reorderRows(int[] newOrder) {
    VIntIntHashMap mapOrder = VHashService.toMap(newOrder, getElements());
    return reorderRows(mapOrder);
  }

  /**
   * Sets row #<code>row</code>  to be holding a missign value.
   * @param row - the row number to be set.
   *
   public void setValueToMissing(int row) {
   missing.add(row);
   removeRow(row);
   }
   */
  /**
   * Sets row #<code>row</code>  to be holding an empty value.
   * @param row - the row number to be set.
   *
   public void setValueToEmpty(int row) {
   removeRow(row);
   }
   */
  /**
   * resets row #<code>row</code> state as missing value according to <code>
   * isMissing</code>. if <code>isMissing</code> is true sets it to hold a missing value
   * otherwise - marks it as no holding a missing value.
   *
   * @param isMissing   a flag indicating wheather row #<code>row</code> should
   *                    be marked as missing value (if true) or regular value
   *                    (if false).
   * @param row - the row number to be set.
   */
  public void setValueToMissing(boolean isMissing, int row) {
    if (isMissing) {
      missing.add(row);
    }
    else {
      missing.remove(row);
    }
  }

  /**
   * Sets row #<code>row</code>  to be holding an empty value.
   *
   * @param isEmpty     a flag indicating wheather row #<code>row</code> should
   *                    be marked as empty value (if true) or regular value
   *                    (if false).
   * @param row - the row number to be set.
   */
  public void setValueToEmpty(boolean isEmpty, int row) {
    if (isEmpty) {
      empty.add(row);
    }
    else {
      empty.remove(row);
    }
  }

  /**
   * put your documentation comment here
   * @param pos
   */
  public void removeRowMissing(int pos) {
    if (missing.contains(pos)) {
      missing.remove(pos);
    }
  }

  /**
   * Remove the designation that this particular row is empty.
   *
   */
  public void removeRowEmpty(int pos) {
    if (empty.contains(pos)) {
      empty.remove(pos);
    }
  }

  /**
   * Inserts a new entry in the Column at position <code>pos</code>.
   * All entries at row numbers greater than <codE>pos</code> are moved down
   * the column to the next row.
   * @param newEntry the newEntry to insert
   * @param pos the position to insert at
   */
  public void insertRow(Object newEntry, int pos) {
    getElements().insertObject(newEntry, pos);
    missing.increment(pos);
  }

  /**
   * Replaces a row's entry at position <code>pos</code>.
   *
   * Xiaolei - 07/08/2003
   */
  public void replaceRow(Object newEntry, int pos) {
    if (this instanceof SparseStringColumn) {
      if (getElements().containsKey(pos)) {
        ( (SparseStringColumn)this).valuesInColumn[ ( (SparseStringColumn)this).
            row2Id.get(pos)] = (String) newEntry;
      }
    }
    else {
      getElements().replaceObject(newEntry, pos);
    }
    if (missing.contains(pos)) {
      missing.remove(pos);
    }
  }


  //===================
  // Protected Methods
  //===================

  /**
   * Returns the hash map that holds all the elements of this map
   */
  protected abstract VHashMap getElements();

  /**
       * Reteives valid rows indices from row no. <code>begin</code> through row no.
   * <code>end</code>.
   *
   * @param begin row number to begin retrieving from
   * @param end   last row number of retrieved section
   * @return      an int array with valid indices in the range specified by
   *              <code>begin</code> and <code>end</code>, sorted.
   */
  protected int[] getRowsInRange(int begin, int end) {
    if (end < begin) {
      int[] retVal = {};
      return retVal;
    }
    return VHashService.getIndicesInRange(begin, end, getElements());
  }

  /**
   * put your documentation comment here
   * @param destCol
   * @param pos
   * @param len
   */
  protected void getSubset(AbstractSparseColumn destCol, int pos, int len) {
    destCol.missing = missing.getSubset(pos, len);
    destCol.copyAttributes(this);
  }

  /**
   * put your documentation comment here
   * @param destCol
   * @param indices
   */
  protected void getSubset(AbstractSparseColumn destCol, int[] indices) {
    destCol.missing = missing.getSubset(indices);
    destCol.copyAttributes(this);
  }

  /**
   * put your documentation comment here
   * @param toOrder
   * @param newOrder
   */
  protected void reorderRows(AbstractSparseColumn toOrder,
                             VIntIntHashMap newOrder) {
    toOrder.missing = missing.reorder(newOrder);
    toOrder.copyAttributes(this);
  }


  //=================
  // Private Methods
  //=================

  /**
   * Used by validate methods.
       * @param valid1    boolean value, representing validity of a value in the map
   *                 to be compared.
       * @param valid2    boolean value, representing validity of a value in the map
   *                 to be compared.
   * @return             an int representing the co-validity of the values. for more
   *                     details see validate(int, int);
   */
  private int validate(boolean valid1, boolean valid2) {
    if (!valid1) {
      if (!valid2) {
        return 0;
      }
      else {
        return -1;
      }
    }
    else if (!valid2) {
      return 1;
    }
    else {
      return 2;
    }
  }

}



