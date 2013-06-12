package ncsa.d2k.modules.core.datatype.table.sparse.columns;

//===============
// Other Imports
//===============
import ncsa.d2k.modules.core.datatype.table.sparse.primitivehash.*;
import ncsa.d2k.modules.core.datatype.table.sparse.*;
import ncsa.d2k.modules.core.datatype.table.MutableTable;
import ncsa.d2k.modules.core.datatype.table.ColumnTypes;
import ncsa.d2k.modules.core.datatype.table.basic.Column;

//==============
// Java Imports
//==============
import java.io.*;
import java.util.*;

/**
 * Title:        Sparse Table
 * Description:  Sparse Table projects will implement data structures compatible to the interface tree of Table, for sparsely stored data.
 * Copyright:    Copyright (c) 2002
 * Company:      ncsa
 * @author vered goren
 * @version 1.0
 */

public class SparseByteColumn
    extends AbstractSparseColumn {

  /**
       * SparseByteColumn is a column in a sparse table that holds data of type byte.
   * internal representation: the data is held in an int to byte hashmap.
   * the value j mapped to key i is the value j in line i in this column.
   */

  //==============
  // Data Members
  //==============

  protected VIntByteHashMap elements; //the values of this column
  //public static byte NOT_EXIST = Byte.MIN_VALUE; //a value to be returned
  //when getByte recieves a
  //parameter for row number which
  //is empty.

  //================
  // Constructor(s)
  //================

  /**
   * Creates a new <code>SparseByteColumn</code> instance with the default
   * capacity and load factor.
   */
  public SparseByteColumn() {
    this(0);
  }

  /**
   * Creates a new <code>SparseByteColumn</code> instance with a prime
   * capacity equal to or greater than <tt>initialCapacity</tt> and
   * with the default load factor.
   *
   * @param initialCapacity an <code>int</code> value
   */
  public SparseByteColumn(int initialCapacity) {
    super();
    if (initialCapacity == 0) {
      elements = new VIntByteHashMap();
    }
    else {
      elements = new VIntByteHashMap(initialCapacity);

    }
    missing = new VIntHashSet();
    empty = new VIntHashSet();
    type = ColumnTypes.BYTE;
    setIsNominal(true);
  }

  /**
   * Creates a new <code>SparseByteColumn</code> populated with the boolean
   * values in <code>data</code>.
   * the rows to be popultated are zero to the size of data - 1.
   */

  public SparseByteColumn(byte[] data) {
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
   * @param data     a byte array that holds the values to be inserted
   *                 into this column
   * @param validRows  the indices to be valid in this column
   */
  public SparseByteColumn(byte[] data, int[] validRows) {
    this(data.length);
    int i;
    for (i = 0; i < data.length && i < validRows.length; i++) {
      setByte(data[i], validRows[i]);

    }
    for (; i < data.length; i++) {
      elements.put(getNumRows(), data[i]);
    }
  }

  //================
  // Static Methods
  //================

  /**
   * used by sort method.
   * returns a new index for a new row number for the item <code>currVal</code>
       * the index is the first index i to be found  in <code>values</code> such that
   * <code>currVal equals values[i] and occupiedIndices[i] == false</code>.
   * this index i is then used in the array validRows by sort().
   *
       * @param currVal - the current byte that sort() method is looking for its new
   * row number in the column
   * @param values - all the int values in the column to be sorted.
   * @param row - index such that <code>values[row] == currVal</code> and also
   * <code>occupiedIndices[row] == true</code>.
   * [meaning the row number in <code>validRows[row]</code> is already occupied
   * by an int that equals <code>currVal</code>
   * @param occupiedIndices - a flag array in which each index in <vode>validRows</code>
   * that was already occupied by sort() is marked true.
   */
  public static int getNewRow(byte currVal, byte[] values, int row,
                              boolean[] ocuupiedIndices) {
    int retVal = -1;
    //searching values at indices smaller than row
    for (int i = row - 1; i >= 0 && values[i] == currVal && retVal < 0; i--) {
      if (!ocuupiedIndices[i]) {
        retVal = i;

        //searching values at indices greater than row
      }
    }
    for (int i = row + 1;
         retVal < 0 && i < values.length && values[i] == currVal; i++) {
      if (!ocuupiedIndices[i]) {
        retVal = i;

      }
    }
    return retVal;
  }

  /**
   * Returns a byte value associated with <code>obj</code>. If obj equals null
   * returns Byte's minimum value. the method that calss toByte should check
   * validity of obj.
   *
   * @param obj   an object to be converted into byte type.
   * @return
   * #  If <code>obj</code> is a byte[] return the first byte
   * #  If <code>obj</code> is a Byte return its byte value
   * #  If <code>obj</code> is a Number - retrieving its byte value
   * #  If <code>obj</code> is a Character - casting the char value into byte
   * #  If <code>obj</code> is a Boolean - returning 1 if true, 0 if else.
   * #  Otherwise: construct a String from <code>obj</code> and retrieved its
   *    Byte value using Byte's methods.
   * #  If obj is null returns Byte's minimum value.
   */
  public static byte toByte(Object obj) {

    if (obj == null) {
      return SparseDefaultValues.getDefaultByte();
    }

    if (obj instanceof byte[]) {
      return ( (byte[]) obj)[0];
    }

    if (obj instanceof Number) {
      return ( (Number) obj).byteValue();
    }

    if (obj instanceof Character) {
      return (byte) ( (Character) obj).charValue();
    }

    if (obj instanceof Boolean) {
      return ( (Boolean) obj).booleanValue() ? (byte) 1 : (byte) 0;
    }

    else {
      String str;
      if (obj instanceof char[]) {
        str = new String( (char[]) obj);
      }
      else {
        str = obj.toString();

      }
      float f = Float.parseFloat(str);

      return (byte) f;
    }

  }


  //================
  // Public Methods
  //================

  /**
   * Returns the value at row # row
   * @param row the row number
   * @return the value at row # row
   */
  public byte getByte(int row) {
    return elements.get(row);
  }

  /**
   * Returns the value at row # row, converted to type boolean.
   * @param row the row number
   * @return false if the value at row # row equals zero, true otherwise
   * if no such value exists returns false.
   */
  public boolean getBoolean(int row) {
    if (!elements.containsKey(row)) {
      return SparseDefaultValues.getDefaultBoolean();
    }
    return (getByte(row) != 0);
  }

  /**
   * If <code>newEntry</code> is <code>false</code>, stores 0 at position
   * <code>pos</code>. Otherwise, stores 1.
   *
   * @param newEntry       the new item
   * @param pos            the position to place newEntry
   */
  public void setBoolean(boolean newEntry, int pos) {
    if (newEntry) {
      setByte( (byte) 1, pos);
    }
    else {
      setByte( (byte) 0, pos);
    }
  }

  /**
   * Returns the value at row # row as a bytes array.
   * @param row the row number
   * @return the value in row # row represented with a bytes array.
   * If no such value exists returns null.
   */
  public byte[] getBytes(int row) {
    if (!elements.containsKey(row)) {
      return null;
    }

    byte[] retVal = new byte[1];
    retVal[0] = getByte(row);
    return retVal;
  }

  /**
   * Sets the <code>byte</code> at position <code>pos</code> to be the
   * first element of <code>newEntry</code>.
   *
   * @param newEntry       the new item
   * @param pos            the position at which to place the first element
   *                       of <code>newEntry</code>
   */
  public void setBytes(byte[] newEntry, int pos) {
    setByte(newEntry[0], pos);
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
    return (char) getByte(row);
  }

  /**
   * Casts <code>newEntry</code> to a <code>byte</code> and stores it at
   * position <code>pos</code>.
   *
   * @param newEntry       the new item as char value
   * @param pos            the position to place <code>newEntry</code>
   */
  public void setChar(char newEntry, int pos) {
    setByte( (byte) newEntry, pos);
  }

  /**
   * Returns the value at row # row, in a chars array.
   * @param row the row number
   * @return the value at row # row represented with a chars array.
   * If no such value exists returns null.
   */
  public char[] getChars(int row) {
    if (!elements.containsKey(row)) {
      return null;
    }
    return getString(row).toCharArray();
  }

  /**
   * Attempts to parse <code>newEntry</code> as a textual representation
   * of a <code>byte</code> and store that value at <code>pos</code>.
   *
   * @param newEntry       the new item
   * @param pos            the position to place <code>newEntry</code>
   */
  public void setChars(char[] newEntry, int pos) {
    setString(String.copyValueOf(newEntry), pos);
  }

  /**
   * Returns the value at row # row, casted to type double.
   * @param row the row number
   * @return the value at row # row casted to double. If no such value exists
   * returns a value signifying the position is empty,
   * as defined by SparseDoubleColumn.
   */
  public double getDouble(int row) {
    if (!elements.containsKey(row)) {
      return SparseDefaultValues.getDefaultDouble();
    }
    return (double) getByte(row);
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
    return (float) getByte(row);
  }

  /**
   * Returns the value at row # row casted in to type int.
   * @param row  the row number
   * @return the value at row number row casted to int.
   * If no such value exist returns a value signifying the position is empty,
   * as defined by SparseIntColumn.
   * */
  public int getInt(int row) {
    if (!elements.containsKey(row)) {
      return SparseDefaultValues.getDefaultInt();
    }

    return (int) getByte(row);
  }

  /**
   * Returns the value at row # row, casted to type long.
   * @param row the row number
   * @return the value at row # row casted to long.
   * If no such value exist returns a value signifying the position is empty,
   * as defined by SparseLongColumn.
   */
  public long getLong(int row) {
    if (!elements.containsKey(row)) {
      return (long)SparseDefaultValues.getDefaultInt();
    }

    return (long) getByte(row);
  }

  /**
   * Returns the value at row # row, encapsulated in a Byte object
   * @param row the row number
   * @return Byte object encapsulating the value at row # row. Returns null if
   * there is no data in row #<code>row</code>
   */
  public Object getObject(int row) {
    if (elements.containsKey(row)) {
      return new Byte(getByte(row));
    }
    else {
      return new Byte(SparseDefaultValues.getDefaultByte());
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

    return (short) getByte(row);
  }

  /**
   * Returns the value at row # row, represented as  a String.
   * @param row the row number
   * @return a String Object representing the value at row # row.
   * If no such value exists returns null
   */
  public String getString(int row) {
    if (!elements.containsKey(row)) {
      return null;
    }
    return Byte.toString(getByte(row));
  }

  /**
   * Creates a deep copy of this colun
   * @return    Column object which is actually a SparseByteColumn with the
   * exact content of this column.
   */
  public Column copy() {
    SparseByteColumn retVal;
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(baos);
      oos.writeObject(this);
      byte buf[] = baos.toByteArray();
      oos.close();
      ByteArrayInputStream bais = new ByteArrayInputStream(buf);
      ObjectInputStream ois = new ObjectInputStream(bais);
      retVal = (SparseByteColumn) ois.readObject();
      ois.close();
      return retVal;
    }
    catch (Exception e) {

      retVal = new SparseByteColumn();
      retVal.elements = elements.copy();
      retVal.copy(this);

      return retVal;
    }
  }

  /**
   * Returns a SparseByteColumn that holds only the data from rows <code>
   * pos</code> through <code>pos+len</code>
   * @param pos   the row number which is the beginning of the subset
   * @param len   number of consequetive rows after <code>pos</code> that are
   *              to be included in the subset.
   * @return      a SparseByteColumn with the data from rows <code>pos</code>
   *              through <code>pos+len</code>
   */
  public Column getSubset(int pos, int len) {
    SparseByteColumn subCol = new SparseByteColumn();
    getSubset(subCol, pos, len);

    return subCol;
    /*
     VIntByteHashMap tempMap = new VIntByteHashMap(len);
          for (int i=0; i<len; i++){
     if(elements.containsKey(pos+i))
      tempMap.put(pos+i, getByte(pos+i));
          }
         SparseByteColumn subCol = new SparseByteColumn ();
         subCol.elements = tempMap;
         super.copy(subCol);
        subCol.missing = missing.getSubset(pos, len);
         subCol.empty = missing.getSubset(pos, len);
          return subCol;
     */
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
    SparseByteColumn retVal = new SparseByteColumn(indices.length);
    for (int i = 0; i < indices.length; i++) {
      if (elements.containsKey(indices[i])) {

        //XIAOLEI
        //retVal.setByte(getByte(indices[i]), indices[i]);
        retVal.setByte(getByte(indices[i]), i);

      }
    }
    super.getSubset(retVal, indices);

    return retVal;
  }

  /**
       * Removes the data stored at row #<code>pos</code> and returns it encapsulated
   * in a Byte Object.
   *
   * @param pos     the row number from which to remove and retrieve the data
   * @return        the data at row #<code>pos</code> encapsulated in a
   *                Byte object. returns null if no such dat exists.
   */
  public Object removeRow(int pos) {
    this.removeRowMissing(pos);

    if (elements.containsKey(pos)) {
      return new Byte(elements.remove(pos));
    }
    else {
      return null;
    }
  }

  /**
   * Sets the <code>byte</code> at this position to be <code>newEntry</code>.
   *
   * @param newEntry       the new item
   * @param pos            the position to place <code>newEntry</code>
   */
  public void setByte(byte newEntry, int pos) {
    elements.remove(pos);
    elements.put(pos, newEntry);
  }

  /**
   * Casts <code>newEntry</code> to a <code>byte</code> and stores it at
   * position <code>pos</code>.
   *
   * @param newEntry       the new item
   * @param pos            the position to place <code>newEntry</code>
   */
  public void setDouble(double newEntry, int pos) {
    setByte( (byte) newEntry, pos);
  }

  /**
   * Casts <code>newEntry</code> to a <code>byte</code> and stores it at
   * position <code>pos</code>.
   *
   * @param newEntry       the new item
   * @param pos            the position to place <code>newEntry</code>
   */
  public void setFloat(float newEntry, int pos) {
    setByte( (byte) newEntry, pos);
  }

  /**
   * Casts <code>newEntry</code> to a <code>byte</code> and stores it at
   * position <code>pos</code>.
   *
   * @param newEntry       the new item
   * @param pos            the position to place <code>newEntry</code>
   */
  public void setInt(int newEntry, int pos) {
    setByte( (byte) newEntry, pos);
  }

  /**
   * Casts <code>newEntry</code> to a <code>byte</code> and stores it at
   * position <code>pos</code>.
   *
   * @param newEntry       the new item
   * @param pos            the position to place <code>newEntry</code>
   */
  public void setLong(long newEntry, int pos) {
    setByte( (byte) newEntry, pos);
  }

  /**
   * Attempts to set the entry at <code>pos</code> to correspond to
   * <code>newEntry</code>. If <code>newEntry</code> is a <code>byte[]</code>,
   * <code>char[]</code>, or <code>Byte</code>, the appropriate method is
   * called. Otherwise, <code>setString</code> is called.
   *
   * @param newEntry       the new item
   * @param pos            the position
   */
  public void setObject(Object newEntry, int pos) {
    if (newEntry instanceof byte[]) {
      setBytes( (byte[]) newEntry, pos);
    }
    else if (newEntry instanceof char[]) {
      setChars( (char[]) newEntry, pos);
    }
    else if (newEntry instanceof Byte) {
      setByte( ( (Byte) newEntry).byteValue(), pos);
    }
    else {
      setString(newEntry.toString(), pos);
    }
  }


  /**
   * Casts <code>newEntry</code> to a <code>byte</code> and stores it at
   * position <code>pos</code>.
   *
   * @param newEntry       the new item
   * @param pos            the position to place <code>newEntry</code>
   */
  public void setShort(short newEntry, int pos) {
    setByte( (byte) newEntry, pos);
  }

  /**
   * Set the entry at <code>pos</code> to be <code>newEntry</code>.
   * <code>Byte.byteValue()</code> is called to store <code>newEntry</code>
   * as a <code>byte</code>.
   *
   * @param newEntry       the new item to put in the column
   * @param pos            the position in which to put <code>newEntry</code>
   */
  public void setString(String newEntry, int pos) {
    setByte( (byte) (Double.parseDouble(newEntry)), pos);
  }

  /**
   * sorts the elements in this column such that the rows that were originally
       * valid remain valid after sorting and for each valid row i, get(i) is smaller
   * than get(i+1) (aside of the maximal row number).
   */
  public void sort() {

    VIntIntHashMap newOrder = elements.getSortedOrder();
    elements = (VIntByteHashMap) elements.reorder(newOrder);

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

    byte val1 = elements.remove(pos1);
    byte val2 = elements.remove(pos2);

    if (valid_1) {
      setByte(val1, pos2);
    }
    if (valid_2) {
      setByte(val2, pos1);

    }
    missing.swapRows(pos1, pos2);
    empty.swapRows(pos1, pos2);

  }


  /**
   * Compared the value represented by element and the one of row number
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
      byte val_1 = toByte(obj);
      byte val_2 = elements.get(pos);
      return compareBytes(val_1, val_2);
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
      byte val_1 = elements.get(pos1);
      byte val_2 = elements.get(pos2);
      return compareBytes(val_1, val_2);
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
   * @return a SparseByteColumn ordered according to <code>newOrder</code>.
   */

  public Column reorderRows(VIntIntHashMap newOrder) {
    SparseByteColumn retVal = new SparseByteColumn();
    retVal.elements = (VIntByteHashMap) elements.reorder(newOrder);
    reorderRows(retVal, newOrder);
    return retVal;
  }

  /**
   * Returns the internal representation of this column.
   *
   */
  public Object getInternal() {
    int max_index = -1;
    byte[] internal = null;
    int[] keys = elements.keys();

    for (int i = 0; i < keys.length; i++) {
      if (keys[i] > max_index) {
        max_index = keys[i];
      }
    }

    internal = new byte[max_index + 1];
    for (int i = 0; i < max_index + 1; i++) {
      internal[i] = SparseDefaultValues.getDefaultByte();
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
  protected void insertRow(byte val, int pos) {
    boolean valid = elements.containsKey(pos);
    byte removedValue = elements.remove(pos);
    //putting the new value
    setByte(val, pos);
    //recursively moving the items in the column as needed
    if (valid) {
      insertRow(removedValue, pos + 1);

    }
  }

  /**
   * Returns a reference to the data in this column
   *
       * @return   the hash map that holds the data of this column (VIntByteHashMap).
   */
  protected VHashMap getElements() {
    return elements;
  }

  /**
       * Returns the valid values in rows <codE>begin</code> through <codE>end</code>
   *
   * @param begin    row number from to begin retrieving of values
   * @param end      last row number in the section from which values are retrieved.
   * @return         only valid values from rows no. <code>begin</code> through
   *                 <codE>end</code>, sorted.
   */
  protected byte[] getValuesInRange(int begin, int end) {

    if (end < begin) {
      byte[] retVal = {};
      return retVal;
    }
    return elements.getValuesInRange(begin, end);
    /*
         byte[] values =  new byte[end - begin +1];
         int j= 0;
         for (int i=begin; i<=end; i++)
      if(doesValueExist(i)){
        values[j] = getByte(i);
      j++;
      }//end if
         //now j is number of real elements in values.
         byte[] retVal = new byte[j];
         System.arraycopy(values, 0, retVal, 0, j);
         Arrays.sort(retVal);
         return retVal;
     */
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
  private int compareBytes(byte val_1, byte val_2) {
    if (val_1 > val_2) {
      return 1;
    }
    if (val_1 < val_2) {
      return -1;
    }
    return 0;
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
  * @return          a SparseByteColumn with the same values as this one,
  *                  and a reordered section as indicated by <code>begin</code>
  *                  and <code>end</code>.
  *
     public Column reorderRows(int[] newOrder, int begin, int end){
       SparseByteColumn  retVal = new SparseByteColumn();
      retVal.elements = (VIntByteHashMap)elements.reorder(newOrder, begin, end);
       int[] oldOrder = getRowsInRange(begin, end);
       retVal.missing = missing.reorder(newOrder, oldOrder, begin, end);
       retVal.empty = empty.reorder(newOrder, oldOrder, begin, end);
       super.copy(retVal);
       return retVal;
       /*int[] oldOrder = getRowsInRange(begin, end);
        SparseByteColumn  retVal = new SparseByteColumn();
        retVal.elements = elements.copy();
        retVal.removeRows(begin, end-begin+1);
        //each val V that is mapped to row no. newOrder[i] will be mapped
        //to row no. oldOrder[i] in retVal.
        for (int i=0; i<newOrder.length && i<oldOrder.length; i++){
   if(elements.containsKey(newOrder[i]))
     retVal.setByte(elements.get(newOrder[i]), oldOrder[i]);
        }
       AbstractSparseColumn.reorderMissingEmpty(newOrder, oldOrder,  this, retVal);
        return retVal;
      }
   */
