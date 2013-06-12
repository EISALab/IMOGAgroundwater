package ncsa.d2k.modules.core.datatype.table.sparse.columns;

//===============
// Other Imports
//===============

import ncsa.d2k.modules.core.datatype.table.sparse.primitivehash.*;
import ncsa.d2k.modules.core.datatype.table.ColumnTypes;
import ncsa.d2k.modules.core.datatype.table.basic.Column;
import ncsa.d2k.modules.core.datatype.table.MutableTable;
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

public class SparseCharColumn
    extends AbstractSparseColumn {

  /**
       * SparseCharColumn is a column in a sparse table that holds data of type char.
   * internal representation: the data is held in  an int to char hashmap.
   * the value j mapped to key i is the value j in line i in this column.
   */

  //==============
  // Data Members
  //==============

  private VIntCharHashMap elements;

  //================
  // Constructor(s)
  //================

  /**
   * Creates a new <code>SparseCharColumn</code> instance with the default
   * capacity and load factor.
   */
  public SparseCharColumn() {
    this(0);
  }

  /**
   * Creates a new <code>SparseCharColumn</code> instance with a prime
   * capacity equal to or greater than <tt>initialCapacity</tt> and
   * with the default load factor.
   *
   * @param initialCapacity an <code>int</code> value
   */
  public SparseCharColumn(int initialCapacity) {
    super();
    if (initialCapacity == 0) {
      elements = new VIntCharHashMap();
    }
    else {
      elements = new VIntCharHashMap(initialCapacity);

    }
    type = ColumnTypes.CHAR;
    setIsNominal(true);
  }

  /**
   * each value data[i] is set to validRows[i].
   * if validRows is smaller than data, the rest of the
   * values in data are being inserted to the end of this column
   *
   * @param data     a char array that holds the values to be inserted
   *                 into this column
   * @param validRows  the indices to be valid in this column
   */
  public SparseCharColumn(char[] data, int[] validRows) {
    this(data.length);
    int i;
    for (i = 0; i < data.length && i < validRows.length; i++) {
      setChar(data[i], validRows[i]);

    }
    for (; i < data.length; i++) {
      elements.put(getNumRows(), data[i]);
    }
  }

  /**
   * Creates a new <code>SparseCharColumn</code> populated with the boolean
   * values in <code>data</code>.
   * the rows to be popultated are zero to the size of data - 1.
   */

  public SparseCharColumn(char[] data) {
    this(data.length);
    for (int i = 0; i < data.length; i++) {
      elements.put(i, data[i]);
    }
  }

  //================
  // Static Methods
  //================

  /**
   * converts obj to a char value
   * @param obj    an Object to converted into type char.
   * @return       a char representation of <code>obj</code>. if obj is null
   *               returns a value signifying the position is empty, as defined
   * by this calss.
   */
  public static char toChar(Object obj) {

    if (obj == null) {
      return SparseDefaultValues.getDefaultChar();
    }

    if (obj instanceof Number) {
      return (char) ( (Number) obj).intValue();
    }

    if (obj instanceof char[]) {
      return ( (char[]) obj)[0];
    }

    if (obj instanceof byte[]) {
      return new String( (byte[]) obj).toCharArray()[0];
    }

    //covers also cases of Boolean, Character
    return obj.toString().toCharArray()[0];

  }


  //================
  // Public Methods
  //================

  /**
   * Returns true if the value at row # row is 't' or 'T'. else returns false.
   *
   * @param     row the row number
   * @return    true if the value at row # row is 't' or 'T'. false otherwise.
   */

  public boolean getBoolean(int row) {
    char c = getChar(row);
    if (c == 't' || c == 'T') {
      return true;
    }
    else {
      return false;
    }
  }

  /**
   * Sets the <code>char</code> at <code>pos</code> to be <code>'T'</code> if
   * <code>newEntry</code> is <code>true</code>, otherwise sets it to
   * <code>'F'</code>.
   *
   * @param newEntry       the new item
   * @param pos            the position to place newEntry
   */
  public void setBoolean(boolean newEntry, int pos) {
    if (newEntry) {
      setChar('T', pos);
    }
    else {
      setChar('F', pos);
    }
  }

  /**
   * Returns the value at row # row casted to type byte
   * @param     row the row number
   * @return    the value at row # row casted to type byte.
   *            if no such value exists returns a value signifying the position
   *          is empty, as defined by SparseByteColumn
   */
  public byte getByte(int row) {
    if (!elements.containsKey(row)) {
      return SparseDefaultValues.getDefaultByte();
    }
    return (byte) getChar(row);
  }

  /**
   * Casts <code>newEntry</code> to an <code>int</code> and calls
   * <code>setInt</code>.
   *
   * @param newEntry       the new item
   * @param pos            the position to place <code>newEntry</code>
   */
  public void setByte(byte newEntry, int pos) {
    setChar( (char) newEntry, pos);

  }

  /**
   * Returns the value at row # row as a bytes array.
   * @param row the row number
   * @return the value in row # row represented by a bytes array.
   *          If no such value exists returns null.
   */
  public byte[] getBytes(int row) {
    /* byte[] retVal = new byte[1];
     retVal[0] = (byte)getChar(row);
     return retVal;*/
    if (!elements.containsKey(row)) {
      return SparseDefaultValues.getDefaultBytes();
    }
    return getString(row).getBytes();
  }

  /**
   * Casts the first element of <code>newEntry</code> to an <code>int</code>
   * and calls <code>setInt</code>.
   *
   * @param newEntry       the new item
   * @param pos            the position to place newEntry
   */
  public void setBytes(byte[] newEntry, int pos) {
    setString(new String(newEntry), pos);
  }

  /**
   * Returns the value at row # row
   * @param row the row number
   * @return the char at row # row
   */
  public char getChar(int row) {
    return elements.get(row);
  }

  /**
   * Returns the value at row # row, in a chars array.
   * @param row the row number
   * @return the value at row # row represented with a chars array.
   * If no such value exists returns null.
   */
  public char[] getChars(int row) {
    if (!elements.containsKey(row)) {
      return SparseDefaultValues.getDefaultChars();
    }

    char[] retVal = new char[1];
    retVal[0] = getChar(row);
    return retVal;
  }

  /**
   * Sets the <code>char</code> at position <code>pos</code> to be the first
   * element of <code>newEntry</code>.
   *
   * @param newEntry       the new item
   * @param pos            the position to place <code>newEntry</code>
   */
  public void setChars(char[] newEntry, int pos) {
    setChar(newEntry[0], pos);
  }

  /**
   * Returns the value at row # row, casted to type double.
   * @param   row the row number
   * @return  the value at row # row casted to double.
   *          if no such value exists return a value signifying the position is empty,
   * as defined by SparseDoubleColumn.
   */
  public double getDouble(int row) {
    if (!elements.containsKey(row)) {
      return SparseDefaultValues.getDefaultDouble();
    }
    return (double) getChar(row);
  }

  /**
   * Returns the value at row # row, casted to type float.
   * @param row the row number
   * @return the value at row # row casted to float
       * if no such value exists return a value signifying the position is empty, as
   * defined by SparseFloatColumn.
   */
  public float getFloat(int row) {
    if (!elements.containsKey(row)) {
      return (float)SparseDefaultValues.getDefaultDouble();
    }
    return (float) getChar(row);
  }

  /**
   * Returns the value at row # row casted in to type int.
   * @param row  the row number
   * @return the value at row number row casted to int.
   * if no such value exists return a value signifying the position is empty,
   * as defined by SparseIntColumn.
   */
  public int getInt(int row) {
    if (!elements.containsKey(row)) {
      return SparseDefaultValues.getDefaultInt();
    }
    return (int) getChar(row);
  }

  /**
   * Returns the value at row # row, casted to type long.
   * @param row the row number
   * @return the value at row # row casted to long
   * if no such value exists return a value signifying the position is empty,
   * as defined by SparseLongColumn.
   */
  public long getLong(int row) {
    if (!elements.containsKey(row)) {
      return (long)SparseDefaultValues.getDefaultInt();
    }
    return (long) getChar(row);
  }

  /**
   * Returns the value at row # row, encapsulated in a Character object
   * @param row the row number
   * @return Character object encapsulating the value at row # row. If there is
   * no data at row #<code>row</code> returns null.
   */
  public Object getObject(int row) {
    if (elements.containsKey(row)) {
      return new Character(getChar(row));
    }
    else {
      return new Character(SparseDefaultValues.getDefaultChar());
    }
  }

  /**
   * Returns the value at row # row, casted to type short.
   * @param row the row number
   * @return the value at row # row casted to short.
   * If no such value exists returns a value signifying the position is empty,
   * as defined by SparseShortColumn.
   */
  public short getShort(int row) {
    if (!elements.containsKey(row)) {
      return (short)SparseDefaultValues.getDefaultInt();
    }
    return (short) getChar(row);
  }

  /**
   * Returns the value at row # row, represented as  a String.
   * @param row the row number
   * @return a String Object representing the value at row # row.
   * If no such value exists returns null.
   */
  public String getString(int row) {
    if (!elements.containsKey(row)) {
      return SparseDefaultValues.getDefaultString();
    }
    return new String(getChars(row));
  }

  /**
   * Returns a deep copy of the SparseCharColumn
   */
  public Column copy() {
    SparseCharColumn retVal;
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(baos);
      oos.writeObject(this);
      byte buf[] = baos.toByteArray();
      oos.close();
      ByteArrayInputStream bais = new ByteArrayInputStream(buf);
      ObjectInputStream ois = new ObjectInputStream(bais);
      retVal = (SparseCharColumn) ois.readObject();
      ois.close();
      return retVal;
    }
    catch (Exception e) {

      retVal = new SparseCharColumn();
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
    SparseCharColumn retVal = new SparseCharColumn(indices.length);
    for (int i = 0; i < indices.length; i++) {
      if (elements.containsKey(indices[i])) {

        //XIAOLEI
        //retVal.setChar(getChar(indices[i]), indices[i]);
        retVal.setChar(getChar(indices[i]), i);

      }
    }
    super.getSubset(retVal, indices);

    return retVal;
  }

  /**
   * Returns a SparseCharColumn that holds only the data from rows <code>
   * pos</code> through <code>pos+len</code>
   * @param pos   the row number which is the beginning of the subset
   * @param len   number of consequetive rows after <code>pos</code> that are
   *              to be included in the subset.
   * @return      a SparseCharColumn with the data from rows <code>pos</code>
   *              through <code>pos+len</code>
   */
  public Column getSubset(int pos, int len) {
    SparseCharColumn subCol = new SparseCharColumn();
    subCol.elements = (VIntCharHashMap) elements.getSubset(pos, len);
    getSubset(subCol, pos, len);

    return subCol;

    /* VIntCharHashMap tempMap = new VIntCharHashMap(len);
     //for each valid row in the given range
      for (int i=0; i<len; i++)
       if(elements.containsKey(pos+i))
      //put its mapped value into the new map.
      tempMap.put(pos+i, getChar(pos+i));
      //constructing the returned vlaue
     SparseCharColumn subCol = new SparseCharColumn();
     super.copy(subCol);
     subCol.elements = tempMap;
      return subCol;*/
  }

  /**
       * Removes the char value at row #<code>pos</code> and returns it encapsulated
   * in a Character object.
   * @param pos - the row number from which the data is removed and retrieved.
       * @return    a Character object representing the data at row #<code>pos</code>.
   *            If no such value exists - returns null.
   */
  public Object removeRow(int pos) {
    if (elements.containsKey(pos)) {
      return new Character(elements.remove(pos));
    }
    else {
      return null;
    }
  }

  /**
   * Sets the <code>char</code> at this position <code>pos</code> to be
   * <code>newEntry</code>.
   *
   * @param newEntry       the new item
   * @param pos            the position to place <code>newEntry</code>
   */
  public void setChar(char newEntry, int pos) {
    elements.put(pos, newEntry);
  }

  /**
   * Casts newEntry to an int and calls setInt.
   *
   * @param newEntry
   * @param pos
   */
  public void setDouble(double newEntry, int pos) {
    setChar( (char) newEntry, pos);
  }

  /**
   * Casts newEntry to an int and calls setInt()
   *
   * @param newEntry    the data to be stored
   * @param pos         the row number to be set
   */
  public void setFloat(float newEntry, int pos) {
    setChar( (char) newEntry, pos);
  }

  /**
   * Stores the Unicode character corresponding to <code>newEntry</code>
   * at position <code>pos</code>.
   *
   * @param newEntry       the new item
   * @param pos            the position to place <code>newEntry</code>
   */
  public void setInt(int newEntry, int pos) {
    setChar( (char) newEntry, pos);
  }

  /**
   * Casts <code>newEntry</code> to an <code>int</code> and calls
   * <code>setInt</code>.
   *
   * @param newEntry       the new item
   * @param pos            the position to place <code>newEntry</code>
   */
  public void setLong(long newEntry, int pos) {
    setChar( (char) newEntry, pos);
  }

  /**
   * Converts <code>newEntry</code> into a char and sets it to row #<code>pos</code>.
   *
   * @param newEntry       the new item represented by an object
   * @param pos            the position
   */
  public void setObject(Object newEntry, int pos) {
    setChar(toChar(newEntry), pos);
  }

  /**
   * Casts <code>newEntry</code> to an <code>int</code> and calls
   * <code>setInt</code>.
   *
   * @param newEntry       the new item
   * @param pos            the position to place <code>newEntry</code>
   */
  public void setShort(short newEntry, int pos) {
    setChar( (char) newEntry, pos);
  }

  /**
   * Sets the <code>char</code> at position <code>pos</code> to the first
   * character of a <code>String</code>.
   *
   * @param newEntry       the new item to put in the column
   * @param pos            the position to put <code>newEntry</code>
   */
  public void setString(String newEntry, int pos) {
    setChar(newEntry.toCharArray()[0], pos);
  }

  /**
   * sorts the elements in this column so that the rows that were originally
   * valid remain valid after sorting
   */
  public void sort() {
    VIntIntHashMap newOrder = elements.getSortedOrder();
    elements = (VIntCharHashMap) elements.reorder(newOrder);
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

    char val1 = elements.remove(pos1);
    char val2 = elements.remove(pos2);

    if (valid_1) {
      setChar(val1, pos2);
    }
    if (valid_2) {
      setChar(val2, pos1);

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
      char val_1 = toChar(obj);
      char val_2 = elements.get(pos);
      return compareChars(val_1, val_2);
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
      char val_1 = elements.get(pos1);
      char val_2 = elements.get(pos2);
      return compareChars(val_1, val_2);
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
   * @return a SparseCharColumn ordered according to <code>newOrder</code>.
   */

  public Column reorderRows(VIntIntHashMap newOrder) {
    SparseCharColumn retVal = new SparseCharColumn();
    retVal.elements = (VIntCharHashMap) elements.reorder(newOrder);
    reorderRows(retVal, newOrder);
    return retVal;
  }

  protected VHashMap getElements() {
    return elements;
  }

  /**
   * Returns the internal representation of this column.
   *
   */
  public Object getInternal() {
    int max_index = -1;
    char[] internal = null;
    int[] keys = elements.keys();

    for (int i = 0; i < keys.length; i++) {
      if (keys[i] > max_index) {
        max_index = keys[i];
      }
    }

    internal = new char[max_index + 1];
    for (int i = 0; i < max_index + 1; i++) {
      internal[i] = SparseDefaultValues.getDefaultChar();
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
  protected void insertRow(char val, int pos) {
    boolean valid = elements.containsKey(pos);
    char removedValue = elements.remove(pos);
    //putting the new value
    setChar(val, pos);
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
  protected char[] getValuesInRange(int begin, int end) {
    if (end < begin) {
      char[] retVal = {};
      return retVal;
    }
    return elements.getValuesInRange(begin, end);
    /*
      char[] values = new char[end - begin +1];
      int j= 0;
      for (int i=begin; i<=end; i++)
        if(doesValueExist(i)){
          values[j] = getChar(i);
      j++;
        }//end if
      //now j is number of real elements in values.
      char[] retVal = new char[j];
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
  private int compareChars(char val_1, char val_2) {
    if (val_1 > val_2) {
      return 1;
    }
    if (val_1 < val_2) {
      return -1;
    }
    return 0;
  }


}
