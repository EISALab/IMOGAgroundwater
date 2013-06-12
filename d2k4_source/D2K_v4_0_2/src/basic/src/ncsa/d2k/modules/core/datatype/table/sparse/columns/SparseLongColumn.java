package ncsa.d2k.modules.core.datatype.table.sparse.columns;

//===============
// Other Imports
//===============

import ncsa.d2k.modules.core.datatype.table.sparse.primitivehash.*;
import ncsa.d2k.modules.core.datatype.table.basic.Column;
import ncsa.d2k.modules.core.datatype.table.ColumnTypes;
import ncsa.d2k.modules.core.datatype.table.MutableTable;
import ncsa.d2k.modules.core.datatype.table.sparse.*;

//==============
// Java Imports
//==============

import java.io.*;
import java.util.*;

/**
 * Title:        Sparse Table
     * Description:  Sparse Table projects will implement data structures compatible
 * to the interface tree of Table, for sparsely stored data.
 * Copyright:    Copyright (c) 2002
 * Company:      ncsa
 * @author vered goren
 * @version 1.0
 */

public class SparseLongColumn
    extends AbstractSparseColumn {

  /**
       * SparseLongColumn is a column in a sparse table that holds data of type long.
   * internal representation: the column is an int to long hashmap.
   * the value j mapped to key i is the value j in line i in this column.
   */

  //==============
  // Data Members
  //==============

  private VIntLongHashMap elements;

  //================
  // Constructor(s)
  //================

  /**
   * Creates a new <code>SparseLongColumn</code> instance with the
   * capacity zero and default load factor.
   */
  public SparseLongColumn() {
    this(0);
  }

  /**
   * Creates a new <code>SparseLongColumn</code> instance with a prime
   * capacity equal to or greater than <tt>initialCapacity</tt> and
   * with the default load factor.
   *
   * @param initialCapacity an <code>int</code> value
   */
  public SparseLongColumn(int initialCapacity) {
    super();
    if (initialCapacity == 0) {
      elements = new VIntLongHashMap();
    }
    else {
      elements = new VIntLongHashMap(initialCapacity);
    }
    setIsScalar(true);
    type = ColumnTypes.LONG;

  }

  /**
   * Creates a new <code>SparseLongColumn</code> instance that will hold the
   * data in the <code>data</code> array. the elements in <code>data</code> are
   * being stored in <code>elements</code> in rows 0 through the size of
   * <code>data</code>.
   *
       * this is just to comply with regular column objects that have this constructor.
   * because this is a sparse column it is unlikely to be used.
   */

  public SparseLongColumn(long[] data) {
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
   * @param data     a long array that holds the values to be inserted
   *                 into this column
   * @param validRows  the indices to be valid in this column
   */
  public SparseLongColumn(long[] data, int[] validRows) {
    this(data.length);
    int i;
    for (i = 0; i < data.length && i < validRows.length; i++) {
      setLong(data[i], validRows[i]);

    }
    for (; i < data.length; i++) {
      elements.put(getNumRows(), data[i]);
    }
  }

  //================
  // Static Methods
  //================

  /**
   * Converts obj into type long.
   * If obj is null returns the Minimum Value of class Long.
   *
   * @param obj   an Object to be converted into type long
   * @return      a long representation of the data held by <code>obj</code>
   *              # If obj is null returns a value signifying the position is empty,
   *                as defined by this class.
   *              # If obj is a Number return its long value
   *              # If obj is a Character return it char value casted into long
   *              # If obj is a Boolean return 1 if obj=true else return 0
   *              # Otherwise: construct a String from obj and attempt to
   *                  parse a long from it.
   */
  public static long toLong(Object obj) {

    if (obj == null) {
      return (long) SparseDefaultValues.getDefaultInt();
    }

    if (obj instanceof Number) {
      return ( (Number) obj).longValue();
    }

    if (obj instanceof Character) {
      return (long) ( (Character) obj).charValue();
    }

    if (obj instanceof Boolean) {
      return ( (Boolean) obj).booleanValue() ? 1 : 0;
    }

    String str;
    if (obj instanceof char[]) {
      str = new String( (char[]) obj);
    }
    else if (obj instanceof byte[]) {
      str = new String( (byte[]) obj);
    }
    else { //obj is a String or an unknown object
      str = obj.toString();

    }
    float f = Float.parseFloat(str);
    return (long) f;
  }


  //================
  // Public Methods
  //================

  /**
   * Returns the value at row # row, casted to type byte.
   * @param row the row number
   * @return the value at row casted to byte.
   * if no such value exists returns a value signifying the position
   *          is empty, as defined by SparseByteColumn
   */
  public byte getByte(int row) {
    if (!elements.containsKey(row)) {
      return SparseDefaultValues.getDefaultByte();
    }
    return (byte) getLong(row);
  }

  /**
   Casts newEntry to long an sets its value at pos
   @param newEntry the new item
   @param pos the position
   */
  public void setByte(byte newEntry, int pos) {
    setLong( (long) newEntry, pos);
  }

  /**
   * Returns the value at row # row, casted to type boolean.
   * @param row the row number
   * @return false if the value at row # row equals zero, true otherwise.
   * if no such value exists returns false.
   */
  public boolean getBoolean(int row) {
    if (!elements.containsKey(row)) {
      return SparseDefaultValues.getDefaultBoolean();
    }
    return (getLong(row) != 0);
  }

  /**
        Set the value at pos to 1 if newEntry is true, otherwise
        set it to 0.
        @param newEntry the new item
        @param pos the position
   */
  public void setBoolean(boolean newEntry, int pos) {
    if (newEntry) {
      setLong( (long) 1, pos);
    }
    else {
      setLong( (long) 0, pos);
    }
  }

  /**
   * Returns the value at row # row converted to a bytes array.
   * @param row the row number
   * @return the value in row # row represented with a bytes array
   * If no such value exists returns null.
   */
  public byte[] getBytes(int row) {
    if (!elements.containsKey(row)) {
      return SparseDefaultValues.getDefaultBytes();
    }
    return String.valueOf(getLong(row)).getBytes();
  }

  /**
        Converts <code>newEntry</cdoe> into a String, then sets the long value represented by it
        at row #<code>pos</cdoe>
        @param newEntry the new value
        @param pos the position
   */
  public void setBytes(byte[] newEntry, int pos) {
    setString(new String(newEntry), pos);
  }

  /**
   * Returns the value at row # row casted to char type
   * @param row the row number
   * @return the value at row # row casted to char. if no such
   * value exists return a value signifying the position is empty, as defined
   * by SparseCharColumn.
   */
  public char getChar(int row) {
    if (!elements.containsKey(row)) {
      return SparseDefaultValues.getDefaultChar();
    }
    return (char) getInt(row);
  }

  /**
        Convert newEntry to a char array and call setChars()
        @param newEntry the new item
        @param pos the position
   */
  public void setChar(char newEntry, int pos) {
    /*char[] c = new char[1];
           c[0] = newEntry;
           setChars(c, pos);*/
    setLong( (long) newEntry, pos);
  }

  /**
   * Returns the value at row # row, ina chars array.
   * @param row the row number
   * @return the value at row # row represented with a chars array.
   * If no such value exists returns null.
   */
  public char[] getChars(int row) {
    if (!elements.containsKey(row)) {
      return SparseDefaultValues.getDefaultChars();
    }
    return Long.toString(getLong(row)).toCharArray();
  }

  /**
      Convert newEntry to a String and call setString()
      @param newEntry the new item
      @param pos the position
   */
  public void setChars(char[] newEntry, int pos) {
    setString(new String(newEntry), pos);
  }

  /**
   * Returns the value at row # row casted  to double
   * @param row the row number
   * @return the value at row # row casted to double.
   * If no such value exists returns a value signifying the position is empty,
   * as defined by SparseDoubleColumn.
   */
  public double getDouble(int row) {
    if (!elements.containsKey(row)) {
      return SparseDefaultValues.getDefaultDouble();
    }
    return (double) getLong(row);
  }

  /**
   * Returns the value at row # row casted to float type
   * @param row the row number
   * @return the value at row # row casted to float.
   * If no such value exists returns a value signifying the position is empty,
   * as defined by SparseFloatColumn.
   */
  public float getFloat(int row) {
    if (!elements.containsKey(row)) {
      return (float) SparseDefaultValues.getDefaultDouble();
    }
    return (float) getLong(row);
  }

  /**
   * Returns the value at row # row casted to int
   * @param row  the row number
   * @return the value at row number row casted to int.
   * if no such value exists returns a value signifying the position is empty,
   * as defined by SparseIntColumn.
   */
  public int getInt(int row) {
    if (!elements.containsKey(row)) {
      return SparseDefaultValues.getDefaultInt();
    }
    return (int) getLong(row);
  }

  /**
   * Returns the value at row # row
   * @param row the row number
   * @return the value at row # row.
   * if no such value exists returns a value signifying the position is empty,
   * as defined by this class.
   */
  public long getLong(int row) {
    if (elements.containsKey(row)) {
      return elements.get(row);
    }
    else {
      return (long) SparseDefaultValues.getDefaultInt();
    }
  }

  /**
   * Returns the value at row # row, encapsulated in a Long object
   * @param row the row number
   * @return Long object encapsulating the value at row # row
   */
  public Object getObject(int row) {
    if (elements.containsKey(row)) {
      return new Long(elements.get(row));
    }
    else {
      return new Long( (long) SparseDefaultValues.getDefaultInt());
    }
  }

  /**
   * Returns the value at row # row, casted to type short.
   * @param row the row number
   * @return the value at row # row casted to short.
   * if no such value exists returns a value signifying the position is empty,
   * as defined by SparseShortColumn.
   */
  public short getShort(int row) {
    if (!elements.containsKey(row)) {
      return (short) SparseDefaultValues.getDefaultInt();
    }
    return (short) getLong(row);
  }

  /**
   * Returns the value at row # row, represented as  a String.
   * @param row the row number
   * @return a String Object representing the value at row # row.
   * If no such value exists returns null.
   */
  public String getString(int row) {
    if (!elements.containsKey(row)) {
      return "" + (long) SparseDefaultValues.getDefaultInt();
    }
    return String.valueOf(getLong(row));
  }

  /**
        Casts newEntry to long an sets its value at pos
        @param newEntry the new item
        @param pos the position
   */
  public void setDouble(double newEntry, int pos) {
    setLong( (long) newEntry, pos);
  }

  /**
        Casts newEntry to long an sets its value at pos
        @param newEntry the new item
        @param pos the position
   */
  public void setFloat(float newEntry, int pos) {
    setLong( (long) newEntry, pos);
  }

  /**
   Casts newEntry to long an sets its value at pos
   @param newEntry the new item
   @param pos the position
   */
  public void setInt(int newEntry, int pos) {
    setLong( (long) newEntry, pos);
  }

  /**
   Set the value at pos to newEntry
   @param newEntry the new item
   @param pos the position
   */
  public void setLong(long newEntry, int pos) {
    elements.remove(pos);
    elements.put(pos, newEntry);
  }

  /**
        Casts newEntry to long an sets its value at pos
        @param newEntry the new item
        @param pos the position
   */
  public void setShort(short newEntry, int pos) {
    setLong( (long) newEntry, pos);
  }

  /**
        Converts newEntry to a Long and inserts the long value
        @param newEntry the new item
        @param pos the position
   */
  public void setString(String newEntry, int pos) {
    setLong( (long) (Double.parseDouble(newEntry)), pos);
  }

  /**
        If newEntry is a Number, get its long value, otherwise
        call setString() on newEntry.toString()
        @param newEntry the new item
        @param pos the position
   */
  public void setObject(Object newEntry, int pos) {
    long l = toLong(newEntry);
    setLong(l, pos);
  }

  /**
   * performs a deep copy of this SparseIntColumn
   * returns an exact copy of this SparseIntColumn
   */
  public Column copy() {
    SparseLongColumn retVal;
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(baos);
      oos.writeObject(this);
      byte buf[] = baos.toByteArray();
      oos.close();
      ByteArrayInputStream bais = new ByteArrayInputStream(buf);
      ObjectInputStream ois = new ObjectInputStream(bais);
      retVal = (SparseLongColumn) ois.readObject();
      ois.close();
      return retVal;
    }
    catch (Exception e) {

      retVal = new SparseLongColumn();

      retVal.elements = elements.copy();
      retVal.copy(this);

      return retVal;
    }
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
    SparseLongColumn retVal = new SparseLongColumn(indices.length);
    for (int i = 0; i < indices.length; i++) {
      if (elements.containsKey(indices[i])) {

        //XIAOLEI
        //retVal.setLong(getLong(indices[i]), indices[i]);
        retVal.setLong(getLong(indices[i]), i);

      }
    }
    super.getSubset(retVal, indices);

    return retVal;
  }

  /**
   * Returns a SparseLongColumn that holds only the data from rows <code>
   * pos</code> through <code>pos+len</code>
   * @param pos   the row number which is the beginning of the subset
   * @param len   number of consequetive rows after <code>pos</code> that are
   *              to be included in the subset.
   * @return      a SparseLongColumn with the data from rows <code>pos</code>
   *              through <code>pos+len</code>
   */
  public Column getSubset(int pos, int len) {
    SparseLongColumn subCol = new SparseLongColumn();
    subCol.elements = (VIntLongHashMap) elements.getSubset(pos, len);
    getSubset(subCol, pos, len);

    return subCol;
    /*
          //the map to hold the data of the new column
          VIntLongHashMap tempMap = new VIntLongHashMap (len);
          //for each row from pos till pos+len
          for (int i=0; i<len; i++)
     //if a value is mapped to current inspected row number
     if(elements.containsKey(pos+i))
      //put it in the new map
      tempMap.put(pos+i, getInt(pos+i));
         SparseLongColumn subCol = new SparseLongColumn();   //the returned value
         super.copy(subCol);     //copying general attributes
         subCol.elements = tempMap;    //linking the data to the returned value
          return subCol;
     */

  }

  /**
   * Removes the byte value in row # <code>pos</code> and returns it
   * encapsulated in a Long object
   */
  public Object removeRow(int pos) {
    if (elements.containsKey(pos)) {
      return new Long(elements.remove(pos));
    }
    else {
      return null;
    }
  }

  /**
   * sorts the elements in this column so that the rows that were originally
   * valid remain valid after sorting
   */
  public void sort() {
    VIntIntHashMap newOrder = elements.getSortedOrder();
    elements = (VIntLongHashMap) elements.reorder(newOrder);
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

    long val1 = elements.remove(pos1);
    long val2 = elements.remove(pos2);

    if (valid_1) {
      setLong(val1, pos2);
    }
    if (valid_2) {
      setLong(val2, pos1);
    }
    missing.swapRows(pos1, pos2);
    empty.swapRows(pos1, pos2);

  }

  /**
   * Compares the value represented by element and the one of row number
   * <code>pos</code>. <code>elements</code> will be converted to a compatible
   * type to this column.
   * if element > pos returns 1.
   * if element < pos retruns -1.
   * if the are equal returns 0.
   * if one of the representation does not hold a value, it is considered
   * smaller than the other.
   */
  public int compareRows(Object obj, int pos) {
    int val = validate(obj, pos);
    if (val <= 1) {
      return val;
    }
    else {
      long val_1 = toLong(obj);
      long val_2 = elements.get(pos);
      return compareLongs(val_1, val_2);
    }

  }

  /**
   * Compares 2 values that are in this column.
   * Retruns an int representation of the relation between the values.
   *
   * @param pos1 - the row number of the first value to be compared
   * @param pos2 - the row number of the second value to be compared
   * @return int - representing the relation between the values at row #
   * <code>pos1</code> and row # <code>pos2</code>.
   * if pos1's value > pos2' value returns 1.
   * if pos1's value < pos2' value returns -1.
   * returns 0 if they are equal.
   */
  public int compareRows(int pos1, int pos2) {
    int val = validate(pos1, pos2);
    if (val <= 1) {
      return val;
    }
    else {
      long val_1 = elements.get(pos1);
      long val_2 = elements.get(pos2);
      return compareLongs(val_1, val_2);
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
   * @return a SparseLongColumn ordered according to <code>newOrder</code>.
   */

  public Column reorderRows(VIntIntHashMap newOrder) {
    SparseLongColumn retVal = new SparseLongColumn();
    retVal.elements = (VIntLongHashMap) elements.reorder(newOrder);
    reorderRows(retVal, newOrder);
    return retVal;
  }

  /**
   * Returns the internal representation of this column.
   *
   */
  public Object getInternal() {
    int max_index = -1;
    long[] internal = null;
    int[] keys = elements.keys();

    for (int i = 0; i < keys.length; i++) {
      if (keys[i] > max_index) {
        max_index = keys[i];
      }
    }

    internal = new long[max_index + 1];
    for (int i = 0; i < max_index + 1; i++) {
      internal[i] = (long) SparseDefaultValues.getDefaultInt();
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
  protected void insertRow(long val, int pos) {
    boolean valid = elements.containsKey(pos);
    long removedValue = elements.remove(pos);
    //putting the new value
    setLong(val, pos);
    //recursively moving the items in the column as needed
    if (valid) {
      insertRow(removedValue, pos + 1);

    }
  }

  /**
       * Returns the valid values in rows <codE>begin</code> through <codE>end</code>
   *
   * @param begin    row number from to begin retrieving of values
   * @param end      last row number in the section from which values are retrieved.
   * @return         only valid values from rows no. <code>begin</code> through
   *                 <codE>end</code>, sorted.
   */
  protected long[] getValuesInRange(int begin, int end) {
    if (end < begin) {
      long[] retVal = {};
      return retVal;
    }
    return elements.getValuesInRange(begin, end);
    /*
         long[] values =  new long[end - begin +1];
         int j= 0;
         for (int i=begin; i<=end; i++)
     if(doesValueExist(i)){
       values[j] = getLong(i);
      j++;
     }//end if
         //now j is number of real elements in values.
         long[] retVal = new long[j];
         System.arraycopy(values, 0, retVal, 0, j);
         Arrays.sort(retVal);
         return retVal;
     */
  }

  protected VHashMap getElements() {
    return elements;
  }

  //=================
  // Private Methods
  //=================

  /**
   * Compares 2 values and Retruns an int representation of the relation
   * between the values.
   *
   * @param va1_1 - the first value to be compared
   * @param vla_2 - the second value to be compared
   * @return int - representing the relation between the values
   *
   * if val_1 > val_2 returns 1.
   * if val_1 < val_2 returns -1.
   * returns 0 if they are equal.
   */
  private int compareLongs(long val_1, long val_2) {
    if (val_1 > val_2) {
      return 1;
    }
    if (val_1 < val_2) {
      return -1;
    }
    return 0;
  }


}