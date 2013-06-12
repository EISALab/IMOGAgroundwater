package ncsa.d2k.modules.core.datatype.table.sparse.columns;

//===============
// Other Imports
//===============

import ncsa.d2k.modules.core.datatype.table.sparse.primitivehash.*;
import ncsa.d2k.modules.core.datatype.table.util.ByteUtils;
import ncsa.d2k.modules.core.datatype.table.ColumnTypes;
import ncsa.d2k.modules.core.datatype.table.MutableTable;
import ncsa.d2k.modules.core.datatype.table.basic.Column;
import ncsa.d2k.modules.core.datatype.table.sparse.*;

//==============
// Java Imports
//==============
import java.util.*;
import java.io.*;

/**
 * Title:        Sparse Table
 * Description:  Sparse Table projects will implement data structures compatible
 * to the interface tree of Table, for sparsely stored data.
 * Copyright:    Copyright (c) 2002
 * Company:      ncsa
 * @author vered goren
 * @version 1.0
 */

public class SparseBooleanColumn
    extends AbstractSparseColumn {

  /**
   * SparseBooleanColumn is a column in a sparse table that holds data of type
   * boolean.
   * internal representation: the data is held in an int to boolean hashmap.
   * the value j mapped to key i is the value j in line i in this column.
   */

  //==============
  // Data Members
  //==============

  protected VIntBooleanHashMap elements; //the values of this column


  //================
  // Constructor(s)
  //================

  /**
   * Creates a new <code>SparseBooleanColumn</code> instance with the default
   * capacity and load factor.
   */
  public SparseBooleanColumn() {
    this(0);
  }

  /**
   * Creates a new <code>SparseBooleanColumn</code> instance with a prime
   * capacity equal to or greater than <tt>initialCapacity</tt> and
   * with the default load factor.
   *
   * @param initialCapacity an <code>int</code> value
   */
  public SparseBooleanColumn(int initialCapacity) {
    super();
    if (initialCapacity == 0) {
      elements = new VIntBooleanHashMap();
    }
    else {
      elements = new VIntBooleanHashMap(initialCapacity);

    }
    type = ColumnTypes.BOOLEAN;
    setIsNominal(true);
  }

  /**
   * Creates a new <code>SparseBooleanColumn</code> populated with the boolean
   * values in <code>data</code>.
   * the rows to be popultated are zero to the size of data - 1.
   */

  public SparseBooleanColumn(boolean[] data) {
    this(data.length);
    for (int i = 0; i < data.length; i++) {
      elements.put(i, data[i]);
    }
  }

  /**
   * each value data[i] is set to validRows[i].
   * if validRows is smaller than data, the rest of the
   * values in data are being inserted to the end of this column
   *
   * @param data     a boolean array that holds the values to be inserted
   *                 into this column
   * @param validRows  the indices to be valid in this column
   */
  public SparseBooleanColumn(boolean[] data, int[] validRows) {
    this(data.length);
    int i;
    for (i = 0; i < data.length && i < validRows.length; i++) {
      setBoolean(data[i], validRows[i]);

    }
    for (; i < data.length; i++) {
      elements.put(getNumRows(), data[i]);
    }
  }

  //================
  // Static Methods
  //================

  public static int compareBooleans(boolean b1, boolean b2) {
    if (b1 == b2) {
      return 0;
    }
    else {
      return 1;
    }
  }

  /**
   * Converts <code>obj</code> to type boolean:
   * #  If <code>obj</code> is a Number - parse a double value from it. if the
   *    double value equals zero return false. return true if ealse.
   * #  If <code>obj</code> is a Character: return true if its char value is
   *    't' or 'T'. return false otherwise.
   * #  Otherwise: construct a String from <code>obj</code> and return true
   *    if it eqauls to "true". return false if else.
   *
   * @param obj   an object from which to retrieve a boolean value
   * @return      a boolean value associated with <code>obj</code>. if obj is
   *              null returns false.
   *
   */
  public static boolean toBoolean(Object obj) {

    if (obj == null) {
      return SparseDefaultValues.getDefaultBoolean();
    }

    if (obj instanceof Number) {
      return ( ( (Number) obj).doubleValue() != 0);
    }

    if (obj instanceof Character) {
      char c = ( (Character) obj).charValue();
      return (c == 't' || c == 'T');
    }

    String str;
    if (obj instanceof char[]) {
      str = new String( (char[]) obj);
    }
    else if (obj instanceof byte[]) {
      str = new String( (byte[]) obj);

    }
    else {
      str = obj.toString();

    }
    return str.equalsIgnoreCase("true");
  }


  //================
  // Public Methods
  //================

  /**
   * Returns the value at row # row, represented as a byte.
   * @param row the row number
   * @return 1 if the value at row is true, 0 if the value is false.
   *          if no such value exist - returns a value signifying the position
   *          is empty, as defined by SparseByteColumn
   */
  public byte getByte(int row) {
    if (!elements.containsKey(row)) {
      return SparseDefaultValues.getDefaultByte();
    }
    if (getBoolean(row)) {
      return 1;
    }
    else {
      return 0;
    }
  }

  /**
   * Set the entry at row # <code>pos</code> to <code>false</code> if
   * <code>newEntry</code> is equal to 0. Set to <code>true</code> otherwise.
   *
   * @param newEntry       the new entry
   * @param pos            the row number in the column
   */
  public void setByte(byte newEntry, int pos) {

    if (newEntry == 0) {
      setBoolean(false, pos);
    }
    else {
      setBoolean(true, pos);
    }
  }

  /**
   * Returns the value at row # row.
   * @param row the row number
   * @return the value at row # row
   */
  public boolean getBoolean(int row) {
    return elements.get(row);
  }

  /**
   * Set the <code>boolean</code> value at this position.
   * removes prior values.
   * @param newEntry       the new entry
   * @param pos            the position (row number)
   */
  public void setBoolean(boolean newEntry, int pos) {
    elements.remove(pos);
    elements.put(pos, newEntry);
  }

  /**
   * Returns the value at row # row as a bytes array.
   * Converts the String representing the boolean value into bytes.
   * @param row the row number
   * @return a byte array representing the value at row # row. if no such
   * value exist returns null.
   */
  public byte[] getBytes(int row) {
    if (!elements.containsKey(row)) {
      return SparseDefaultValues.getDefaultBytes();
    }
    return getString(row).getBytes();
  }

  /**
   * Converts <code>newEntry</code> to a <code>boolean</code> using
   * <code>ByteUtils.toBoolean()</code> and sets the value at <code>pos</code>
   * to this <code>boolean</code>.
   *
   * @param newEntry       the new item
   * @param pos            the position
   */
  public void setBytes(byte[] newEntry, int pos) {
    setString(new String(newEntry), pos);
  }

  /**
   * Returns 'T'/'F' according to the value at row # row
   * @param row the row number
   * @return 'T' if the value at row # row is true, 'F' otherwise. if no such
   * value exists return a value signifying the position is empty, as defined
   * by SparseCharColumn
   */
  public char getChar(int row) {
    if (!elements.containsKey(row)) {
      return SparseDefaultValues.getDefaultChar();
    }
    if (getBoolean(row)) {
      return 'T';
    }
    else {
      return 'F';
    }
  }

  /**
   * Set the entry at <code>pos</code> to correspond to <code>newEntry</code>.
   * Set to <code>true</code> if and only if <code>newEntry</code> is equal
   * to <code>'T'</code> or <code>'t'</code>. Otherwise set to
   * <code>false</code>.
   *
   * @param newEntry       the new entry
   * @param pos            the position in the column
   */
  public void setChar(char newEntry, int pos) {
    if (newEntry == 'T' || newEntry == 't') {
      setBoolean(true, pos);
    }
    else {
      setBoolean(false, pos);
    }
  }

  /**
   * Returns the String representation of the value at row # row,
   * as a chars array.
   * @param row the row number
   * @return a char array containing the String representation of the value at
   * row # row. if no such value exist returns null
   */
  public char[] getChars(int row) {
    if (!elements.containsKey(row)) {
      return SparseDefaultValues.getDefaultChars();
    }
    return getString(row).toCharArray();
  }

  /**
   * Set the entry at <code>pos</code> to be <code>newEntry</code>. Set to
   * <code>true</code> if and only if <code>newEntry</code> is equal to
   * "true" (ignoring case). Otherwise, set to <code>false</code>.
   *
   * @param newEntry       the new entry
   * @param pos            the position in the column
   */
  public void setChars(char[] newEntry, int pos) {
    setString(new String(newEntry), pos);
  }

  /**
   * Returns the value at row # row, casted to type double.
   * @param row the row number
   * @return 1 if the value at row # row is true, 0 otherwise.
   * If no such value exist returns a value signifying the position is empty,
   * as defined by SparseDoubleColumn.
   */
  public double getDouble(int row) {
    if (!elements.containsKey(row)) {
      return SparseDefaultValues.getDefaultDouble();
    }
    return (double) getInt(row);
  }

  /**
   * Set the entry at <code>pos</code> to <code>false</code> if
   * <code>newEntry</code> is equal to 0. Set to <code>true</code> otherwise.
   *
   * @param newEntry       the new entry
   * @param pos            the position in the column
   */
  public void setDouble(double newEntry, int pos) {
    if (newEntry == 0) {
      setBoolean(false, pos);
    }
    else {
      setBoolean(true, pos);
    }
  }

  /**
   * Returns the value at row # row, casted to type float.
   * @param row the row number
   * @return 1 if the value at row # row is true, 0 otherwise.
   * If no such value exist returns  a value signifying the position is empty,
   * as defined by SparseFloatColumn.
   */
  public float getFloat(int row) {
    if (!elements.containsKey(row)) {
      return (float) SparseDefaultValues.getDefaultDouble();
    }
    return (float) getInt(row);
  }

  /**
   * Returns the value at row # row casted to int
   * @param row  the row number
   * @return 1 if the value at row # row is true, 0 otherwise.
   * If no such value exist returns a value signifying the position is empty,
   * as defined by SparseIntColumn.
   */
  public int getInt(int row) {
    if (!elements.containsKey(row)) {
      return SparseDefaultValues.getDefaultInt();
    }

    if (getBoolean(row)) {
      return 1;
    }
    else {
      return 0;
    }
  }

  /**
   * Returns the value at row # row, casted to type long.
   * @param row the row number
   * @return 1 if the value at row # row is true, 0 otherwise.
   * If no such value exist returns a value signifying the position is empty,
   * as defined by SparseLongColumn.
   */
  public long getLong(int row) {
    if (!elements.containsKey(row)) {
      return (long) SparseDefaultValues.getDefaultInt();
    }
    return (long) getInt(row);
  }

  /**
   * Returns the value at row # row, encapsulated in a Boolean object
   * @param row the row number
   * @return Boolean object encapsulating the value at row # row
   *         If there is no data at row #<code>row</code> returns null.
   */
  public Object getObject(int row) {
    if (elements.containsKey(row)) {
      return new Boolean(getBoolean(row));
    }
    else {
      return new Boolean(SparseDefaultValues.getDefaultBoolean());
    }
  }

  /**
   * Returns the value at row # row, casted to type short.
   * @param row the row number
   * @return 1 if the value at row # row is true, 0 otherwise.
   * If no such value exist returns a value signifying the position is empty,
   * as defined by SparseShortColumn.
   */
  public short getShort(int row) {
    if (!elements.containsKey(row)) {
      return (short) SparseDefaultValues.getDefaultInt();
    }
    return (short) getInt(row);
  }

  /**
   * Set the entry at <code>pos</code> to <code>false</code> if
   * <code>newEntry</code> is equal to 0. Set to <code>true</code> otherwise.
   *
   * @param newEntry       the new entry
   * @param pos            the position in the column
   */
  public void setFloat(float newEntry, int pos) {
    if (newEntry == 0) {
      setBoolean(false, pos);
    }
    else {
      setBoolean(true, pos);
    }
  }

  /**
   * Set the entry at <code>pos</code> to <code>false</code> if
   * <code>newEntry</code> is equal to 0. Set to <code>true</code> otherwise.
   *
   * @param newEntry       the new entry
   * @param pos            the position in the column
   */
  public void setInt(int newEntry, int pos) {
    if (newEntry == 0) {
      setBoolean(false, pos);
    }
    else {
      setBoolean(true, pos);
    }
  }

  /**
   * Set the entry at <code>pos</code> to <code>false</code> if
   * <code>newEntry</code> is equal to 0. Set to <code>true</code> otherwise.
   *
   * @param newEntry       the new entry
   * @param pos            the position in the column
   */
  public void setLong(long newEntry, int pos) {
    if (newEntry == 0) {
      setBoolean(false, pos);
    }
    else {
      setBoolean(true, pos);
    }
  }

  /**
       * Removes the data stored at row #<code>pos</code> and returns it encapsulated
   * in a Boolean Object.
   *
   * @param pos     the row number from which to remove and retrieve the data
   * @return        the data at row #<code>pos</code> encapsulated in a
   *                Boolean object. returns null if no such dat exists.
   */
  public Object removeRow(int pos) {

    removeRowMissing(pos);

    if (elements.containsKey(pos)) {
      return new Boolean(elements.remove(pos));
    }
    else {
      return null;
    }
  }

  /**
   * Reorders the data stored in this column in a new column.
   * Does not change this column.
   *
   * Algorithm: copy this column into the returned vlaue.
   * for each pair (key, val) in <code>newOrder</code>, if val is a valid row
   * in this column, put the value mapped to it in row no. key in the returned
   * values.
   *
   * @param newOrder - an int to int hashmap, defining the new order for
   *                   the returned column.
   * @return a SparseBooleanColumn ordered according to <code>newOrder</code>.
   */

  public Column reorderRows(VIntIntHashMap newOrder) {
    SparseBooleanColumn retVal = new SparseBooleanColumn();

    retVal.elements = (VIntBooleanHashMap) elements.reorder(newOrder);
    reorderRows(retVal, newOrder);

    return retVal;
  }

  /**
   * Creates a deep copy of this colun
   * @return    a SparseBooleanColumn with the exact content of this column.
   */

  public Column copy() {
    SparseBooleanColumn retVal;
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(baos);
      oos.writeObject(this);
      byte buf[] = baos.toByteArray();
      oos.close();
      ByteArrayInputStream bais = new ByteArrayInputStream(buf);
      ObjectInputStream ois = new ObjectInputStream(bais);
      retVal = (SparseBooleanColumn) ois.readObject();
      ois.close();
      return retVal;
    }
    catch (Exception e) {

      retVal = new SparseBooleanColumn();
      retVal.elements = elements.copy();
      retVal.copy(this); //copying label, comment and missing.

      return retVal;
    }
  }

  /**
   * Returns 0 if both elements in <code>r1</code> and <code>r2</code> are the
   * same. returns value greater than zero (1) if they are different.
   * might return -1 if there is no data in row r1, but there is in row r2.
   *
   * @param r1 - row number of first item to compare
   * @param r2 - row number of second item to compare
   * @return 0 if items are identical, 1 if else.
   */
  public int compareRows(int r1, int r2) {

    int val = validate(r1, r2);
    if (val <= 1) {
      return val;
    }

    return compareBooleans(getBoolean(r1), getBoolean(r2));
  }


  /**
   * Returns 0 if the boolean value represented by <code>element</code> is the
   * same as the boolean value at row # <code>row</code>. Returns 1 if else.
   *
   * @param element - an object representing a boolean value.
   * @param row - the row number of the item to be compared with <code>element</code>
   * @return - 0 if the value represented by element is identical to the value
   * at row # <code>row</code>. 1 if else.
   */

  public int compareRows(Object element, int row) {

    int val = validate(element, row);
    if (val <= 1) {
      return val;
    }

    return compareBooleans(toBoolean(element), getBoolean(row));
  }

  /**
   * Returns a SparseBooleanColumn that holds only the data from rows <code>
   * pos</code> through <code>pos+len</code>
   * @param pos - the row number which is the beginning of the subset
   * @param len - number of consequetive rows after <code>pos</code> that are
   * to be included in the subset.
   * @return    a SparseBooleanColumn with the data from rows <code>pos</code>
   *            through <code>pos+len</code>
   */
  public Column getSubset(int pos, int len) {

    /* VIntBooleanHashMap tempMap = new VIntBooleanHashMap(len);
     for (int i=0; i<len; i++){
      if(elements.containsKey(pos+i))
      tempMap.put(pos+i, getBoolean(pos+i));
     }
         subCol.elements = tempMap;
     */
    SparseBooleanColumn subCol = new SparseBooleanColumn();
    subCol.elements = (VIntBooleanHashMap) elements.getSubset(pos, len);
    getSubset(subCol, pos, len);

    return subCol;
  }

  /**
   * Sets the value at <code>pos</code> to correspond to
   * <code>newEntry</code>. <code>newEntry</code> is transformed into a
   * boolean using toBoolean method.
   *
   * @param newEntry       the new entry
   * @param pos            the position
   */
  public void setObject(Object newEntry, int pos) {
    setBoolean(toBoolean(newEntry), pos);
  }

  /**
   * Set the entry at <code>pos</code> to <code>false</code> if
   * <code>newEntry</code> is equal to 0. Set to <code>true</code> otherwise.
   *
   * @param newEntry       the new entry
   * @param pos            the position in the column
   */
  public void setShort(short newEntry, int pos) {
    if (newEntry == 0) {
      setBoolean(false, pos);
    }
    else {
      setBoolean(true, pos);
    }
  }

  /**
   * Set the entry at <code>pos</code> to be <code>newEntry</code>. Set to
   * <code>true</code> if and only if <code>newEntry</code> is equal to
   * "true" (ignoring case). Otherwise, set to <code>false</code>.
   *
   * @param newEntry       the new entry
   * @param pos            the position in the column
   */
  public void setString(String newEntry, int pos) {
    if (newEntry.equalsIgnoreCase("true")) {
      setBoolean(true, pos);
    }
    else {
      setBoolean(false, pos);
    }
  }

  /**
   * Returns the value at row # row, represented as  a String.
   * @param row the row number
   * @return a String Object representing the value at row # row.
   */
  public String getString(int row) {
    if (!elements.containsKey(row)) {
      return SparseDefaultValues.getDefaultString();
    }
    return (new Boolean(getBoolean(row))).toString();
  }

  /**
   * Sorts the items in this column.
       * the first rows will hold <code>false</code> values and the <code>true</code>
   * values will be held at the end of this column
   */

  public void sort() {
    VIntIntHashMap newOrder = elements.getSortedOrder();
    elements = (VIntBooleanHashMap) elements.reorder(newOrder);

    missing = missing.reorder(newOrder);
    empty = empty.reorder(newOrder);

  }


  /**
   * Swaps the values between 2 rows.
   * If there is no data in row #<code>pos1</code> then nothing is stored in
   * row #<ocde>pos2</code>, and vice versia.
   *
   * @param pos1 - the row number of first item to be swaped
   * @param pos2 - the row number of second item to be swaped
   *
   */
  public void swapRows(int pos1, int pos2) {
    if (pos1 == pos2) {
      return;
    }
    boolean valid_1 = elements.containsKey(pos1);
    boolean valid_2 = elements.containsKey(pos2);

    boolean val1 = elements.remove(pos1);
    boolean val2 = elements.remove(pos2);

    if (valid_1) {
      setBoolean(val1, pos2);
    }
    if (valid_2) {
      setBoolean(val2, pos1);

    }
    missing.swapRows(pos1, pos2);
    empty.swapRows(pos1, pos2);

  }

  /**
   * returns a subset of this column with entried from rows indicated by
   * <code>indices</code>.
   *
   * @param indices  row numbers to include in the returned subset.
   * @return         a subset of this column, including rows indicated by
   *                 <code>indices</code>.
   */
  public Column getSubset(int[] indices) {
    SparseBooleanColumn retVal = new SparseBooleanColumn(indices.length);
    for (int i = 0; i < indices.length; i++) {
      if (elements.containsKey(indices[i])) {

        //XIAOLEI
        //retVal.setBoolean(getBoolean(indices[i]), indices[i]);
        retVal.setBoolean(getBoolean(indices[i]), i);

      }
    }
    super.getSubset(retVal, indices);

    return retVal;
  }

  /**
   * Returns the internal representation of this column.
   *
   */
  public Object getInternal() {
    int max_index = -1;
    boolean[] internal = null;
    int[] keys = elements.keys();

    for (int i = 0; i < keys.length; i++) {
      if (keys[i] > max_index) {
        max_index = keys[i];
      }
    }

    internal = new boolean[max_index + 1];

    for (int i = 0; i < max_index + 1; i++) {
      internal[i] = SparseDefaultValues.getDefaultBoolean();
    }

    for (int i = 0; i < keys.length; i++) {
      internal[keys[i]] = elements.get(keys[i]);
    }

    return internal;
  }

  /**
   * Add the specified number of blank rows.
   * @param number number of rows to add.
   */
  public void addRows(int number) {
    // table is already sparse.  nothing to do.
  }

  //===================
  // Protected Methods
  //===================

  /**
   * Inserts <code>val<code> into row #<code>pos</code>. If this position
   * already holds data - insert the old data into row #<code>pos+1</code>
   * recursively.
   *
   * @param val   the new boolean value to be inserted at pos.
   * @param pos   the row number to insert val.
   */
  protected void insertRow(boolean val, int pos) {
    boolean valid = elements.containsKey(pos);
    boolean removedValue = elements.remove(pos);
    //putting the new value
    setBoolean(val, pos);
    //recursively moving the items in the column as needed
    if (valid) {
      insertRow(removedValue, pos + 1);

    }
  }

  /**
   * Returns a reference to the data in this column
   *
   * @return   the hash map that holds the data of this column (VIntBooleanHashMap).
   */
  protected VHashMap getElements() {
    return elements;
  }

}
/*
 /**
  * Reorders the rows between row no. <code>begin</code> and row no. <code>
  * end</code> according to <code>newOrder</code>
  *
  * @param newOrder  an array that holds valid row numbers in the range <code>
  *                  [begin, end]</code> in a certain order
      * @param begin     row number at the beginning of the section to be reordered.
  * @param end       row number at the en of the section to be reordered.
  * @return          a SparseBooleanColumn with the same values as this one,
  *                  and a reordered section as indicated by <code>begin</code>
  *                  and <code>end</code>.
  *
      public Column reorderRows(int[] newOrder, int begin, int end){
      SparseBooleanColumn  retVal = new SparseBooleanColumn();
      retVal.elements = (VIntBooleanHashMap)elements.reorder(newOrder, begin, end);
      int[] oldOrder = getRowsInRange(begin, end);
      retVal.missing = missing.reorder(newOrder, oldOrder, begin, end);
      retVal.empty = empty.reorder(newOrder, oldOrder, begin, end);
      super.copy(retVal);
      return retVal;
      /*
          int[] oldOrder = getRowsInRange(begin, end);
          SparseBooleanColumn  retVal = new SparseBooleanColumn();
          retVal.elements = elements.copy();
          retVal.removeRows(begin, end-begin+1);
          //each val V that is mapped to row no. newOrder[i] will be mapped
          //to row no. oldOrder[i] in retVal.
          for (int i=0; i<newOrder.length && i<oldOrder.length; i++){
     if(elements.containsKey(newOrder[i]))
       retVal.setBoolean(elements.get(newOrder[i]), oldOrder[i]);
          }
       AbstractSparseColumn.reorderMissingEmpty(newOrder, oldOrder,  this, retVal);
          return retVal;
   *
        }
            /**
    * Retrieve a new order for the valid rows of this column in the range
    * <code>begin</code> through <code>end</code>. The new order is a sorted
    * order for that section of the column.
    *
    * @param begin     row no. from which to start retrieving the new order
    * @param end       the last row in the section from which to retrieve the new order.
    *
         public VIntIntHashMap getNewOrder(int begin, int end){
          return elements.getSortedOrder(begin, end);
         /*
              int[] rangeRows = getRowsInRange(begin, end);
         int currentKeyIndex = 0;                  //points to currently inpected
              //key (row)
         int newPosIndex = rangeRows.length - 1;   //points to the last key that
              //its value is unknown
              int[] newOrder = new int[rangeRows.length];
             //for each row number
              while(currentKeyIndex < newPosIndex){
//if the value it holds is true
         if(elements.get(currentKeyIndex)){
           //swap values with row number which its value is unknown
           newOrder[currentKeyIndex] = rangeRows[newPosIndex];
           newOrder[newPosIndex] = rangeRows[currentKeyIndex];
//	  swapRows(validRows[currentKeyIndex], validRows[newPosIndex]);
           //now row number validRows[newPosIndex] certainly holds the values true
           //therefore decrease newPosIndex.
           newPosIndex--;
         }
//the currently inspected row holds value false - therefore increase
//currentKeyIndex
         else  currentKeyIndex++;
              }//when the while loop's condition is meet, elements is sorted.
              return newOrder;
     *
           }
*/