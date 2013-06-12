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

public class SparseDoubleColumn
    extends AbstractSparseColumn {

  /**
   * SparseDoubleColumn is a column in a sparse table that holds data of type double.
   * internal representation: the column is an int to double hashmap.
   * the value j mapped to key i is the value j in line i in this column.
   */

  //==============
  // Data Members
  //==============

  private VIntDoubleHashMap elements;

  //a value to be returned if getDouble recieves a parameter for row number that
  //is not valid.

  //================
  // Constructor(s)
  //================

  /**
   * Creates a new <code>SparseDoubleColumn</code> instance with the default
   * capacity and load factor.
   */
  public SparseDoubleColumn() {
    this(0);
  }

  /**
   * Creates a new <code>SparseDoubleColumn</code> instance with a prime
   * capacity equal to or greater than <tt>initialCapacity</tt> and
   * with the default load factor.
   *
   * @param initialCapacity an <code>int</code> value
   */
  public SparseDoubleColumn(int initialCapacity) {
    super();
    if (initialCapacity == 0) {
      elements = new VIntDoubleHashMap();
    }
    else {
      elements = new VIntDoubleHashMap(initialCapacity);

    }
    type = ColumnTypes.DOUBLE;
    setIsScalar(true);

  }

  /**
   * Creates a new <code>SparseDoubleColumn</code> instance that will hold the
   * data in the <code>data</code> array. the elements in <code>data</code> are
   * being stored in <code>elements</code> in rows 0 through the size of
   * <code>data</code>.
   *
       * this is just to comply with regular column objects that have this constructor.
   * because this is a sparse column it is unlikely to be used.
   */

  public SparseDoubleColumn(double[] data) {
    this(data.length);
    for (int i = 0; i < data.length; i++) {
      setDouble(data[i], i);
    }
  }

  /**
   * each value data[i] is set to validRows[i].
   * if validRows is smaller than data, the rest of the
   * values in data are being inserted to the end of this column
   *
   * @param data     a double array that holds the values to be inserted
   *                 into this column
   * @param validRows  the indices to be valid in this column
   */
  public SparseDoubleColumn(double[] data, int[] validRows) {
    this(data.length);
    int i;
    for (i = 0; i < data.length && i < validRows.length; i++) {
      setDouble(data[i], validRows[i]);

    }
    for (; i < data.length; i++) {
      elements.put(getNumRows(), data[i]);
    }
  }

  //================
  // Public Methods
  //================

  /**
   * performs a deep copy of this SparseDoubleColumn
   * returns an exact copy of this SparseDoubleColumn
   */
  public Column copy() {
    SparseDoubleColumn retVal;
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(baos);
      oos.writeObject(this);
      byte buf[] = baos.toByteArray();
      oos.close();
      ByteArrayInputStream bais = new ByteArrayInputStream(buf);
      ObjectInputStream ois = new ObjectInputStream(bais);
      retVal = (SparseDoubleColumn) ois.readObject();
      ois.close();
      return retVal;
    }
    catch (Exception e) {

      retVal = new SparseDoubleColumn();
      retVal.elements = elements.copy();
      retVal.copy(this);

      return retVal;
    }
  }

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
    return (byte) getDouble(row);
  }

  /**
        Convert newEntry to a double and sets the ocnverted value at row #<code>pos</code>
        the conversion to a double is done through converting to String first.
        @param newEntry the new item
        @param pos the position
   */
  public void setByte(byte newEntry, int pos) {
    /*byte[] arr = {newEntry};
           setBytes(arr, pos);*/
    setDouble( (double) newEntry, pos);
  }

  /**
   * Returns the value at row # row, casted to type boolean.
   * @param row the row number
   * @return false if the value at row # row equals zero, true otherwise.
   * if no such value exists returns false.
   *
   */
  public boolean getBoolean(int row) {
    if (!elements.containsKey(row)) {
      return SparseDefaultValues.getDefaultBoolean();
    }
    return (getDouble(row) != 0);
  }

  /**
        Sets the value at pos to be 1.0 if newEntry is true, sets the value
        to 0.0 otherwise.
        @param newEntry the new item
        @param pos the position
   */
  public void setBoolean(boolean newEntry, int pos) {
    if (newEntry) {
      setDouble(1.0, pos);
    }
    else {
      setDouble(0.0, pos);
    }
  }

  /**
   * Returns the value at row # row converted to a bytes array.
   * @param row the row number
   * @return the value in row # row represented with a bytes array.
   *   If no such value exists returns null.
   */
  public byte[] getBytes(int row) {
    if (!elements.containsKey(row)) {
      return (byte[]) SparseDefaultValues.getDefaultBytes();
    }
    return String.valueOf(getDouble(row)).getBytes();
  }

  /**
        Converts newEntry to a double and sets the ocnverted value at row #<code>pos</code>
        the conversion to a double is done through converting to String first.
        @param newEntry the new item
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
    return (char) getDouble(row);
  }

  /**
   Converts newEntry to a char array and calls setChars()
   @param newEntry the new item
   @param pos the position
   */
  public void setChar(char newEntry, int pos) {
    setDouble( (double) newEntry, pos);
  }

  /**
   * Returns the value at row # row, ina chars array.
   * @param row the row number
   * @return the value at row # row represented with a chars array.
   *   If no such value exists returns null.
   */
  public char[] getChars(int row) {
    if (!elements.containsKey(row)) {
      return (char[]) SparseDefaultValues.getDefaultChars();
    }
    return Double.toString(getDouble(row)).toCharArray();
  }

  /**
        Converts newEntry to a String and calls setString()
        @param newEntry the new item
        @param pos the position
   */
  public void setChars(char[] newEntry, int pos) {
    setString(new String(newEntry), pos);
  }

  /**
   * Returns the value at row # row.
   * @param row the row number
   * @return the double value at row # row
   */
  public double getDouble(int row) {
    if (elements.containsKey(row)) {
      return elements.get(row);
    }
    else {
      return SparseDefaultValues.getDefaultDouble();
    }
  }

  /**
   * Returns the value at row # row, casted to type float.
   * @param row the row number
   * @return the value at row # row casted to float. If no such value exists
   * returns a value signifying the position is empty, as defined by SparseFloatColumn.
   */
  public float getFloat(int row) {
    if (!elements.containsKey(row)) {
      return (float)SparseDefaultValues.getDefaultDouble();
    }
    return (float) getDouble(row);
  }

  /**
   * Returns the value at row # row casted to int
   * @param row  the row number
   * @return the value at row number row casted to int.
   * If no such value exist returns a value signifying the position is empty,
   * as defined by SparseIntColumn.
   */
  public int getInt(int row) {
    if (!elements.containsKey(row)) {
      return SparseDefaultValues.getDefaultInt();
    }
    return (int) getDouble(row);
  }

  /**
   * Returns the value at row # row, casted to type long.
   * @param row the row number
   * @return the value at row # row casted to long
   * If no such value exist returns a value signifying the position is empty,
   * as defined by SparseLongColumn.
   */
  public long getLong(int row) {
    if (!elements.containsKey(row)) {
      return (long)SparseDefaultValues.getDefaultInt();
    }
    return (long) getDouble(row);
  }

  /**
   * Returns the value at row # row, encapsulated in a Double object
   * @param row the row number
   * @return Double object encapsulating the value at row # row
   */
  public Object getObject(int row) {
    if (elements.containsKey(row)) {
      return new Double(getDouble(row));
    }
    else {
      return new Double(SparseDefaultValues.getDefaultDouble());
    }
  }

  /**
   * Returns the value at row # row, casted to type short.
   * @param row the row number
   * @return the value at row # row casted to short.
   * If no such value exist returns a value signifying the position is empty,
   * as defined by SparseShortColumn.
   */
  public short getShort(int row) {
    if (!elements.containsKey(row)) {
      return (short)SparseDefaultValues.getDefaultInt();
    }
    return (short) getDouble(row);
  }

  /**
   * Returns the value at row # row, represented as  a String.
   * @param row the row number
   * @return a String Object representing the value at row # row.
   *        If no such value exists returns null.
   */
  public String getString(int row) {
    if (!elements.containsKey(row)) {
      return "" + SparseDefaultValues.getDefaultDouble();
    }
    return String.valueOf(getDouble(row));
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
    SparseDoubleColumn retVal = new SparseDoubleColumn(indices.length);
    for (int i = 0; i < indices.length; i++) {
      if (elements.containsKey(indices[i])) {

        //XIAOLEI
        //retVal.setDouble(getDouble(indices[i]), indices[i]);
        retVal.setDouble(getDouble(indices[i]), i);

      }
    }
    super.getSubset(retVal, indices);

    return retVal;
  }

  /**
   * Returns a SparseDoubleColumn that holds only the data from rows <code>
   * pos</code> through <code>pos+len</code>
   * @param pos   the row number which is the beginning of the subset
   * @param len   number of consequetive rows after <code>pos</code> that are
   *              to be included in the subset.
   * @return      a SparseDoubleColumn with the data from rows <code>pos</code>
   *              through <code>pos+len</code>
   */
  public Column getSubset(int pos, int len) {
    SparseDoubleColumn subCol = new SparseDoubleColumn();

    // added by DC 12.02.2002
    subCol.elements = (VIntDoubleHashMap) elements.getSubset(pos, len);

    getSubset(subCol, pos, len);

    return subCol;
    /*
      //the map to hold the data of the new column
          VIntDoubleHashMap tempMap = new VIntDoubleHashMap(len);
          //for each row from pos till pos+len
          for (int i=0; i<len; i++)
     //if a value is mapped to current inspected row number
     if(elements.containsKey(pos+i))
      //put it in the new map
      tempMap.put(pos+i, getDouble(pos+i));
         SparseDoubleColumn subCol = new SparseDoubleColumn();   //the returned value
         super.copy(subCol);     //copying general attributes
         subCol.elements = tempMap;    //linking the data to the returned value
          return subCol;
     */
  }

  /**
   * Removes the byte value in row # <code>pos</code> and returns it
   * encapsulated in a Double object
   *
   * Also adjusts the indices for all rows beyond the one removed so that they
   * correctly reflect their new index position.
   *
       * Once again, an empty or missing value is not the same as a non-existent one.
   * Rows that are removed are rows that no longer exist.
   *
   */
  public Object removeRow(int pos) {
    if (elements.containsKey(pos)) {
      return new Double(elements.remove(pos));
    }
    else {
      return null;
    }
  }

  /**
   * Sets the value in row # <code>pos</code> to be <code>newEntry</code>
   *
   * @param newEntry - the new value
   * @param pos - the row number to set its value
   */
  public void setDouble(double newEntry, int pos) {
    elements.remove(pos);
    elements.put(pos, newEntry);
  }

  /**
   Converts <code>newEntry</code> into double and assign it to row #<code>pos</code>
   @param newEntry the new item
   @param pos the position
   */
  public void setFloat(float newEntry, int pos) {
    setDouble( (double) newEntry, pos);
  }

  /**
        Converts <code>newEntry</code> into double and assign it to row #<code>pos</code>
        @param newEntry the new item
        @param pos the position
   */
  public void setInt(int newEntry, int pos) {
    setDouble( (double) newEntry, pos);
  }

  /**
        Converts <code>newEntry</code> into double and assign it to row #<code>pos</code>
        @param newEntry the new item
        @param pos the position
   */
  public void setLong(long newEntry, int pos) {
    setDouble( (double) newEntry, pos);
  }

  /**
        Sets the value at pos to be newEntry.  If newEntry is a Number,
        it is converted to a double and stored accordingly.  Otherwise,
        setString() is called with newEntry.toString()
        @param newEntry the new item
        @param pos the position
   */
  public void setObject(Object newEntry, int pos) {
    setDouble(toDouble(newEntry), pos);
  }

  /**
   * Converts obj into type double.
   * If obj is null returns the negative infinity of class Double.
   *
   * @param obj   an Object to be converted into type double
   * @return      a double representation of the data held by <code>obj</code>
   *              # If obj is null returns a value signifying the position is empty,
   *                  as defined by this class.
   *              # If obj is a Number return its double value
       *              # If obj is a Character return it char value casted into double
   *              # If obj is a Boolean return 1 if obj=true else return 0
   *              # Otherwise: construct a String from obj and attempt to
   *                  parse a double from it.
   */
  public static double toDouble(Object obj) {
    if (obj == null) {
      return SparseDefaultValues.getDefaultDouble();
    }

    if (obj instanceof Number) {
      return ( (Number) obj).doubleValue();
    }

    if (obj instanceof Character) {
      return (double) ( (Character) obj).charValue();
    }

    if (obj instanceof Boolean) {
      return ( (Boolean) obj).booleanValue() ? (double) 1 : (double) 0;
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
    return Double.parseDouble(str);
  }

  /**
       Sets the entry at the given position to newEntry. uses setObject for this
        purpose.
        @param newEntry a new entry, a subclass of Number
        @param pos the position to set
   */
  public void setRow(Object newEntry, int pos) {
    setObject(newEntry, pos);
  }

  /**
        Converts <code>newEntry</code> into double and assign it to row #<code>pos</code>
        @param newEntry the new item
        @param pos the position
   */
  public void setShort(short newEntry, int pos) {
    setDouble( (double) newEntry, pos);
  }

  /**
        Set the value at pos to be newEntry by calling Double.parseDouble()
        @param newEntry the new item
        @param pos the position
   */
  public void setString(String newEntry, int pos) {
    setDouble(Double.parseDouble(newEntry), pos);
  }

  /**
   * sorts the elements in this column so that the rows that were originally
   * valid remain valid after sorting
   */
  public void sort() {
    VIntIntHashMap newOrder = elements.getSortedOrder();
    elements = (VIntDoubleHashMap) elements.reorder(newOrder);
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

    double val1 = elements.remove(pos1);
    double val2 = elements.remove(pos2);

    if (valid_1) {
      setDouble(val1, pos2);
    }
    if (valid_2) {
      setDouble(val2, pos1);

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
      double val_1 = toDouble(obj);
      double val_2 = elements.get(pos);
      return compareDoubles(val_1, val_2);
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
      double val_1 = elements.get(pos1);
      double val_2 = elements.get(pos2);
      return compareDoubles(val_1, val_2);
    }
  }

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
  public static int compareDoubles(double val_1, double val_2) {
    if (val_1 > val_2) {
      return 1;
    }
    if (val_1 < val_2) {
      return -1;
    }
    return 0;
  }

  public Column reorderRows(VIntIntHashMap newOrder) {
    SparseDoubleColumn retVal = new SparseDoubleColumn();
    retVal.elements = (VIntDoubleHashMap) elements.reorder(newOrder);
    reorderRows(retVal, newOrder);
    return retVal;
  }

  /**
   * Returns the internal representation of this column.
   *
   */
  public Object getInternal() {
    int max_index = -1;
    double[] internal = null;
    int[] keys = elements.keys();

    for (int i = 0; i < keys.length; i++) {
      if (keys[i] > max_index) {
        max_index = keys[i];
      }
    }

    internal = new double[max_index + 1];
    for (int i = 0; i < max_index + 1; i++) {
      internal[i] = SparseDefaultValues.getDefaultDouble();
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

    //the above comment is not quite correct, empty rows are not the same as
    //non-existent rows -- DDS
    for (int i = 0, n = number; i < n; i++) {
      addRow(null);
    }

  }

  //===================
  // Protected Methods
  //===================

  /**
       * Returns the valid values in rows <codE>begin</code> through <codE>end</code>
   *
   * @param begin    row number from to begin retrieving of values
   * @param end      last row number in the section from which values are retrieved.
   * @return         only valid values from rows no. <code>begin</code> through
   *                 <codE>end</code>, sorted.
   */
  protected double[] getValuesInRange(int begin, int end) {
    if (end < begin) {
      double[] retVal = {};
      return retVal;
    }
    return elements.getValuesInRange(begin, end);
    /*
         double[] values =  new double[end - begin +1];
         int j= 0;
         for (int i=begin; i<=end; i++)
     if(doesValueExist(i)){
       values[j] = getDouble(i);
      j++;
     }//end if
         //now j is number of real elements in values.
         double[] retVal = new double[j];
         System.arraycopy(values, 0, retVal, 0, j);
         Arrays.sort(retVal);
         return retVal;
     */
  }

  protected VHashMap getElements() {
    return elements;
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
   * @return a SparseDoubleColumn ordered according to <code>newOrder</code>.
   */

}
