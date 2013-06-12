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

public class SparseIntColumn
    extends AbstractSparseColumn {

  /**
   * SparseIntColumn is a column in a sparse table that holds data of type int.
   * internal representation: the column is an int to int hashmap.
   * the value j mapped to key i is the value j in line i in this column.
   */

  //==============
  // Data Members
  //==============

  private VIntIntHashMap elements;

  //================
  // Constructor(s)
  //================

  /**
   * Creates a new <code>SparseIntColumn</code> instance with the default
   * capacity and load factor.
   */
  public SparseIntColumn() {
    this(0);
  }

  /**
   * Creates a new <code>SparseIntColumn</code> instance with a prime
   * capacity equal to or greater than <tt>initialCapacity</tt> and
   * with the default load factor.
   *
   * @param initialCapacity an <code>int</code> value
   */
  public SparseIntColumn(int initialCapacity) {
    super();
    if (initialCapacity == 0)
      elements = new VIntIntHashMap();
    else
      elements = new VIntIntHashMap(initialCapacity);
    setIsScalar(true);
    type = ColumnTypes.INTEGER;

  }

  /**
   * Creates a new <code>SparseIntColumn</code> instance that will hold the
   * data in the <code>data</code> array. the elements in <code>data</code> are
   * being stored in <code>elements</code> in rows 0 through the size of
   * <code>data</code>.
   *
       * this is just to comply with regular column objects that have this constructor.
   * because this is a sparse column it is unlikely to be used.
   */

  public SparseIntColumn(int[] data) {
    this(data.length);
    for (int i = 0; i < data.length; i++)
      setInt(data[i], i);
  }

  /**
   * each value data[i] is set to validRows[i].
   * if validRows is smaller than data, the rest of the
   * values in data are being inserted to the end of this column
   *
   * @param data     an int array that holds the values to be inserted
   *                 into this column
   * @param validRows  the indices to be valid in this column
   */
  public SparseIntColumn(int[] data, int[] validRows) {
    this(data.length);
    int i;
    for (i = 0; i < data.length && i < validRows.length; i++)
      setInt(data[i], validRows[i]);

    for (; i < data.length; i++)
      elements.put(getNumRows(), data[i]);
  }

  //================
  // Static Methods
  //================

  /**
   * Converts obj into type int.
   * If obj is null returns the Minimum Value of class Integer.
   *
   * @param obj   an Object to be converted into type int
   * @return      an int representation of the data held by <code>obj</code>
   *              # If obj is null returns (a value signifying the position is empty,
   *              as defined by this class.
   *              # If obj is a Number return its int value
   *              # If obj is a Character return it char value casted into int
   *              # If obj is a Boolean return 1 if obj=true else return 0
   *              # Otherwise: construct a String from obj and attempt to
   *                  parse an int from it.
   */
  public static int toInt(Object obj) {
    if (obj == null)
      return SparseDefaultValues.getDefaultInt();

    if (obj instanceof Number)
      return ( (Number) obj).intValue();

    if (obj instanceof Character)
      return (int) ( ( (Character) obj).charValue());

    if (obj instanceof Boolean)
      return ( (Boolean) obj).booleanValue() ? 1 : 0;

    String str;
    if (obj instanceof char[])
      str = new String( (char[]) obj);
    else if (obj instanceof byte[])
      str = new String( (byte[]) obj);
    else
      str = obj.toString();

    float f = Float.parseFloat(str);
    return (int) f;
  }

  //================
  // Public Methods
  //================

  /**
   * Returns the value at row # row, casted to type byte.
   * @param row the row number
   * @return the value at row casted to byte.
   * if no such value exists returns a value signifying the position
   *          is empty, as defined by SparseByteColumn.
   */
  public byte getByte(int row) {
    if (!elements.containsKey(row))
      return SparseDefaultValues.getDefaultByte();
    return (byte) getInt(row);
  }

  /**
   * Set the value at pos to be newEntry casted to int
   * @param newEntry the new item
   * @param pos the position
   */
  public void setByte(byte newEntry, int pos) {
    setInt( (int) newEntry, pos);
  }

  /**
   * Returns the value at row # row, casted to type boolean.
   * @param row the row number
   * @return false if the value at row # row equals zero, true otherwise.
   * if no such value exists returns false.
   */
  public boolean getBoolean(int row) {
    if (!elements.containsKey(row))
      return SparseDefaultValues.getDefaultBoolean();
    return (getInt(row) != 0);
  }

  /**
   * Set the entry at pos to be 1 if newEntry is true, 0 otherwise
   * @param newEntry the newEntry
   * @param pos the position
   */
  public void setBoolean(boolean newEntry, int pos) {
    if (newEntry)
      setInt(1, pos);
    else
      setInt(0, pos);
  }

  /**
   * Returns the value at row # row converted to a bytes array.
   * @param row the row number
   * @return the value in row # row represented with a bytes array.
   * If no such value exists returns null.
   */
  public byte[] getBytes(int row) {
    if (!elements.containsKey(row))
      return (byte[]) SparseDefaultValues.getDefaultBytes();
    return String.valueOf(getInt(row)).getBytes();
  }

  /**
   * Converts newEntry to a string, parse an int from the string and put it
   * in row # pos.
   * @param newEntry the new value
   * @param pos the position
   */
  public void setBytes(byte[] newEntry, int pos) {
    setString(new String(newEntry), pos);
    //internal[pos] = ByteUtils.toInt(newEntry);
  }

  /**
   * Returns the value at row # row casted to char type
   * @param row the row number
   * @return the value at row # row casted to char. if no such
   * value exists return a value signifying the position is empty, as defined
   * by SparseCharColumn.
   */
  public char getChar(int row) {
    if (!elements.containsKey(row))
      return SparseDefaultValues.getDefaultChar();
    return (char) getInt(row);
  }

  /**
   * casts newentry to an int and sets it at pos
   * @param newEntry the new item
   * @param pos the position
   */
  public void setChar(char newEntry, int pos) {
    setInt( (int) newEntry, pos);
  }

  /**
   * Returns the value at row # row, ina chars array.
   * @param row the row number
   * @return the value at row # row represented with a chars array.
   * If no such value exists returns null.
   */
  public char[] getChars(int row) {
    if (!elements.containsKey(row))
      return SparseDefaultValues.getDefaultChars();
    return Integer.toString(getInt(row)).toCharArray();
  }

  /**
   * Convert newEntry to a String and call setString()
   * @param newEntry the new item
   * @param pos the position
   */
  public void setChars(char[] newEntry, int pos) {
    setString(new String(newEntry), pos);
  }

  /**
   * Returns the value at row # row, casted to type double.
   * @param row the row number
   * @return the value at row # row casted to double. If no such value exists
   * returns a value signifying the position is empty,
   * as defined by SparseDoubleColumn.
   */
  public double getDouble(int row) {
    if (!elements.containsKey(row))
      return SparseDefaultValues.getDefaultDouble();
    return (double) getInt(row);
  }

  /**
   * Returns the value at row # row, casted to type float.
   * @param row the row number
   * @return the value at row # row casted to float. If no such value exists
   * returns a value signifying the position is empty, as defined by SparseFloatColumn.
   */
  public float getFloat(int row) {
    if (!elements.containsKey(row))
      return (float) SparseDefaultValues.getDefaultDouble();
    return (float) getInt(row);
  }

  /**
   * Returns the value at row # row
   * @param row  the row number
   * @return the value at row number row.
   * if no such value exists returns a value signifying the position is empty,
   * as defined by this class.
   */
  public int getInt(int row) {
    if (elements.containsKey(row))
      return elements.get(row);
    else
      return SparseDefaultValues.getDefaultInt();
  }

  /**
   * Returns the value at row # row, casted to type long.
   * @param row the row number
   * @return the value at row # row casted to long.
   * if no such value exists returns a value signifying the position is empty,
   * as defined by SparseLongColumn.
   */
  public long getLong(int row) {
    if (!elements.containsKey(row))
      return (long) SparseDefaultValues.getDefaultInt();
    return (long) getInt(row);
  }

  /**
   * Returns the value at row # row, encapsulated in an Integer object
   * @param row the row number
   * @return Ingeter object encapsulating the value at row # row
   */
  public Object getObject(int row) {
    if (elements.containsKey(row)) {
      return new Integer(getInt(row));
    }
    else {
      return new Integer(SparseDefaultValues.getDefaultInt());
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
    if (!elements.containsKey(row))
      return (short) SparseDefaultValues.getDefaultInt();
    return (short) getInt(row);
  }

  /**
   * Returns the value at row # row, represented as  a String.
   * @param row the row number
   * @return a String Object representing the value at row # row.
   * If no such value exists returns null.
   */
  public String getString(int row) {
    if (!elements.containsKey(row))
      return "" + SparseDefaultValues.getDefaultInt();
    return String.valueOf(getInt(row));
  }

  /**
   * performs a deep copy of this SparseIntColumn
   * returns an exact copy of this SparseIntColumn
   */
  public Column copy() {
    SparseIntColumn retVal;
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(baos);
      oos.writeObject(this);
      byte buf[] = baos.toByteArray();
      oos.close();
      ByteArrayInputStream bais = new ByteArrayInputStream(buf);
      ObjectInputStream ois = new ObjectInputStream(bais);
      retVal = (SparseIntColumn) ois.readObject();
      ois.close();
      return retVal;
    }
    catch (Exception e) {

      retVal = new SparseIntColumn();

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
    SparseIntColumn retVal = new SparseIntColumn(indices.length);
    for (int i = 0; i < indices.length; i++)
      if (elements.containsKey(indices[i]))

        //XIAOLEI
        //retVal.setInt(getInt(indices[i]), indices[i]);
        retVal.setInt(getInt(indices[i]), i);

    super.getSubset(retVal, indices);

    return retVal;
  }

  /**
   * Returns a SparseIntColumn that holds only the data from rows <code>
   * pos</code> through <code>pos+len</code>
   * @param pos   the row number which is the beginning of the subset
   * @param len   number of consequetive rows after <code>pos</code> that are
   *              to be included in the subset.
   * @return      a SparseIntColumn with the data from rows <code>pos</code>
   *              through <code>pos+len</code>
   */
  public Column getSubset(int pos, int len) {
    SparseIntColumn subCol = new SparseIntColumn();
    subCol.elements = (VIntIntHashMap) elements.getSubset(pos, len);
    getSubset(subCol, pos, len);

    return subCol;
    /*
      //the map to hold the data of the new column
          VIntIntHashMap tempMap = new VIntIntHashMap (len);
          //for each row from pos till pos+len
          for (int i=0; i<len; i++)
     //if a value is mapped to current inspected row number
     if(elements.containsKey(pos+i))
      //put it in the new map
      tempMap.put(pos+i, getInt(pos+i));
         SparseIntColumn subCol = new SparseIntColumn();   //the returned value
         super.copy(subCol);     //copying general attributes
         subCol.elements = tempMap;    //linking the data to the returned value
          return subCol;
     */
  }

  /**
   * Removes the int value in row # <code>pos</code> and returns it
   * encapsulated in an Int object
   */
  public Object removeRow(int pos) {
    if (elements.containsKey(pos))
      return new Integer(elements.remove(pos));
    else
      return null;
  }

  /**
   * sorts the elements in this column so that the rows that were originally
   * valid remain valid after sorting
   */
  public void sort() {
    VIntIntHashMap newOrder = elements.getSortedOrder();
    elements = (VIntIntHashMap) elements.reorder(newOrder);
    missing = missing.reorder(newOrder);
    empty = empty.reorder(newOrder);
  }

  /**
   * Set the value at pos to be newEntry, converted into int
   * @param newEntry the new item
   * @param pos the position
   */
  public void setDouble(double newEntry, int pos) {
    setInt( (int) newEntry, pos);
  }

  /**
   * Set the value at pos to be newEntry, converted into int
   * @param newEntry the new item
   * @param pos the position
   */
  public void setFloat(float newEntry, int pos) {
    setInt( (int) newEntry, pos);
  }

  /**
   * If newEntry is a Number, get the int value, otherwise
   * call setString() on newEntry.toString()
   * @param newEntry the new item
   * @param pos the position
   */
  public void setObject(Object newEntry, int pos) {
    //internal[pos] = Integer.valueOf(newEntry.toString()).intValue();
    int i = toInt(newEntry);
    setInt(i, pos);
  }


  /**
   * Set the value at pos to be newEntry, converted into int
   * @param newEntry the new item
   * @param pos the position
   */
  public void setShort(short newEntry, int pos) {
    setInt( (int) newEntry, pos);
  }

  /**
   * Parses an int from <code>newEntry</code> and assign it to row #<code>pos</code>
   * @param newEntry the new item
   * @param pos the position
   */
  public void setString(String newEntry, int pos) {
    setInt( (int) (Double.parseDouble(newEntry)), pos);
  }

  /**
   * Set the value at pos to be newEntry, converted into int
   * @param newEntry the new item
   * @param pos the position
   */
  public void setLong(long newEntry, int pos) {
    setInt( (int) newEntry, pos);
  }

  /**
   * Set the value at pos to be newEntry
   * @param newEntry the new item
   * @param pos the position
   */
  public void setInt(int newEntry, int pos) {
    elements.remove(pos);
    elements.put(pos, newEntry);
  }

  /**
   * Inserts <code>val<code> into row #<code>pos</code>. If this position
   * already holds data - insert the old data into row #<code>pos+1</code>
   * recursively.
   *
   * @param val   the new boolean value to be inserted at pos.
   * @param pos   the row number to insert val.
   */
  protected void insertRow(int val, int pos) {
    boolean valid = elements.containsKey(pos);
    int removedValue = elements.remove(pos);
    //putting the new value
    setInt(val, pos);
    //recursively moving the items in the column as needed
    if (valid)
      insertRow(removedValue, pos + 1);

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
    if (val <= 1)
      return val;
    else {
      int val_1 = toInt(obj);
      int val_2 = elements.get(pos);
      return compareInts(val_1, val_2);
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
    if (val <= 1)
      return val;
    else {
      int val_1 = elements.get(pos1);
      int val_2 = elements.get(pos2);
      return compareInts(val_1, val_2);
    }
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
    if (pos1 == pos2)
      return;
    boolean valid_1 = elements.containsKey(pos1);
    boolean valid_2 = elements.containsKey(pos2);

    int val1 = elements.remove(pos1);
    int val2 = elements.remove(pos2);

    if (valid_1)
      setInt(val1, pos2);
    if (valid_2)
      setInt(val2, pos1);

    missing.swapRows(pos1, pos2);
    empty.swapRows(pos1, pos2);
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
   * @return a SparseIntColumn ordered according to <code>newOrder</code>.
   */

  public Column reorderRows(VIntIntHashMap newOrder) {
    SparseIntColumn retVal = new SparseIntColumn();
    retVal.elements = (VIntIntHashMap) elements.reorder(newOrder);
    reorderRows(retVal, newOrder);
    return retVal;
  }

  /*
   * Returns the internal representation of this column.
   *
   */
  public Object getInternal() {
    int max_index = -1;
    int[] internal = null;
    int[] keys = elements.keys();

    for (int i = 0; i < keys.length; i++) {
      if (keys[i] > max_index)
        max_index = keys[i];
    }

    internal = new int[max_index + 1];
    for (int i = 0; i < max_index + 1; i++) {
      internal[i] = SparseDefaultValues.getDefaultInt();
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
       * Returns the valid values in rows <codE>begin</code> through <codE>end</code>
   *
   * @param begin    row number from to begin retrieving of values
   * @param end      last row number in the section from which values are retrieved.
   * @return         only valid values from rows no. <code>begin</code> through
   *                 <codE>end</code>, sorted.
   */
  protected int[] getValuesInRange(int begin, int end) {
    if (end < begin) {
      int[] retVal = {};
      return retVal;
    }
    return elements.getValuesInRange(begin, end);
    /*
         int[] values =  new int[end - begin +1];
         int j= 0;
         for (int i=begin; i<=end; i++)
     if(doesValueExist(i)){
       values[j] = getInt(i);
      j++;
     }//end if
         //now j is number of real elements in values.
         int[] retVal = new int[j];
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
  private int compareInts(int val_1, int val_2) {
    if (val_1 > val_2)
      return 1;
    if (val_1 < val_2)
      return -1;
    return 0;
  }



}