package ncsa.d2k.modules.core.datatype.table.sparse.columns;

//===============
// Other Imports
//===============

import ncsa.d2k.modules.core.datatype.table.sparse.primitivehash.*;
import ncsa.d2k.modules.core.datatype.table.MutableTable;
import ncsa.d2k.modules.core.datatype.table.basic.Column;
import ncsa.d2k.modules.core.datatype.table.ColumnTypes;
import ncsa.d2k.modules.core.datatype.table.sparse.*;

//==============
// Java Imports
//==============

import java.io.*;
import java.util.*;

/**
 * SparseStringColumn is a column in a SparseTable that holds data of Type String.
 * Internal representation:
 * String array - valuesInColumn - holds:
 *  the indices are String IDs, the items are the actual values in the column
 *
 * Int to Int hashmap  - row2Id - holds:
 *  keys - row number in the column
 *  values - index into valuesInColumnn
 *
 * in addition there is another map - val2Id - which maps:
 *  keys - String from valuesInColumn.
 *  values - the index of the String in valuesInColumn.
 * This maps help with inserting new entries without duplicating the Strings.
 *
 * This way there are no duplicated String Objects.
 *
 * For efficient managment of the array 2 more fields are maintained
 *
 * int firstFree - holds the first free index in the array. relates to the free
 *                 indices at the end of the array.
 * int set free -  holds isolated free indices in the array.
 *
 */

public class SparseStringColumn
    extends AbstractSparseColumn {

  //==============
  // Data Members
  //==============

  /* maps row number to index of value in valuesInColumn */
  protected VIntIntHashMap row2Id;

      /* the indices are the values in row2Id, the items in this array are the values
        from this column */
  protected String[] valuesInColumn;

  /* maps String (a value in the map) to id (the index of the value in valuesInColumn) */
  protected VObjectIntHashMap val2Key;

      /* first free entry in valuesInColumn (at the end of the array. does not relate
        to isolated free entries */
  protected int firstFree;

  /* the elements in this set are isolated free indices in valuesInColumn */
  protected VIntHashSet free;

  /* determine the load on the array and its expanding factor*/

  //public static String DEFAULT = "";

  public static float DEFAULT_LOAD_FACTOR = 0.5f;

  //================
  // Constructor(s)
  //================

  /**
   * Creates a new <code>SparseStringColumn </code> instance with the
   * capacity zero and default load factor.
   */
  public SparseStringColumn() {
    this(0);
  }

  /**
   * Creates a new <code>SparseStringColumn </code> instance with a prime
   * capacity equal to or greater than <tt>initialCapacity</tt> and
   * with the default load factor.
   *
   * @param initialCapacity an <code>int</code> value
   */
  public SparseStringColumn(int initialCapacity) {
    super();
    row2Id = new VIntIntHashMap(initialCapacity);
    valuesInColumn = new String[initialCapacity];
    val2Key = new VObjectIntHashMap(initialCapacity);
    firstFree = 0;
    free = new VIntHashSet();
    type = ColumnTypes.STRING;
    setIsNominal(true);
  }

  /**
   * Creates a new <code>SparseStringColumn</code> instance that will hold the
   * data in the <code>data</code> array. the elements in <code>data</code> are
   * being stored in <code>elements</code> in rows 0 through the size of
   * <code>data</code>.
   *
       * this is just to comply with regular column objects that have this constructor.
   * because this is a sparse column it is unlikely to be used.
   */

  public SparseStringColumn(String[] data) {
    this(data.length);
    for (int i = 0; i < data.length; i++) {
      setString(data[i], i);
    }
  }

  /**
   * Creates a new SparseStringColumn Object with values from <codE>data</codE>
   * set at rows specified by <codE>validRows</code>, s.t. <code>data[i]</codE>
   * is set to <code>validRows[i]</code>.
   * if <code>validRows</codE> is smaller than <codE>data</code> then the
   * extra Strings are appended to the end of this Column.
   *
   * @param data     Strings to be set into this Column
       * @param validRows  the insertion indices of the Strings in <codE>data</code>
   */
  public SparseStringColumn(String[] data, int[] validRows) {
    this(data.length);
    int i = 0;
    for (i = 0; i < data.length && i < validRows.length; i++) {
      setString(data[i], validRows[i]);

    }
    for (; i < data.length; i++) {
      setString(data[i], getNumRows());
    }
  }

  public SparseStringColumn(SparseStringColumn srcCol) {
    copy(srcCol);
  }

  //================
  // Static Methods
  //================

  /**
   * Converts <code>obj</code> into a String.
   *
   * @param obj    an Object to be converted into a String
   * @return       a String representing the data <code>obj</code> holds.
   *               # If obj equals null returns null
       *               # If obj is a char or byte array - construct a string from it.
   *               # Otherwise - returns obj.toString.
   */
  public static String toStringObject(Object obj) {
    if (obj == null) {
      return null;
    }

    if (obj instanceof char[]) {
      return new String( (char[]) obj);
    }

    if (obj instanceof byte[]) {
      return new String( (byte[]) obj);
    }

    if (obj instanceof Character) {
      char[] arr = {
          ( (Character) obj).charValue()};
      return new String(arr);
    }

    //covers cases of  Number, Boolean
    return obj.toString();
  }

  //================
  // Public Methods
  //================

  /**
   * performs a deep copy of this SparseStringColumn
   * returns an exact copy of this SparseStringColumn
   * uses the super class copy method to construct the returned value
   * @return Column object which is actually a deep copy of this
   * SparseStringColumn object.
   **/
  public Column copy() {
    SparseStringColumn retVal;
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(baos);
      oos.writeObject(this);
      byte buf[] = baos.toByteArray();
      oos.close();
      ByteArrayInputStream bais = new ByteArrayInputStream(buf);
      ObjectInputStream ois = new ObjectInputStream(bais);
      retVal = (SparseStringColumn) ois.readObject();
      ois.close();
      return retVal;
    }
    catch (Exception e) {

      retVal = new SparseStringColumn();
      retVal.copy(this);

      return retVal;
    }
  }

  /**
   * Returns a SparseStringColumn that holds only the data from rows <code>
   * pos</code> through <code>pos+len</code>
   * @param pos   the row number which is the beginning of the subset
   * @param len   number of consequetive rows after <code>pos</code> that are
   *              to be included in the subset.
   * @return      a SparseStringColumn with the data from rows <code>pos</code>
   *              through <code>pos+len</code>
   */
  public Column getSubset(int pos, int len) {

    SparseStringColumn subCol = new SparseStringColumn(len);

    //retrieving the valid rows numbers in the specified range
    int[] rows = VHashService.getIndicesInRange(pos, pos + len, row2Id);

    //for each valid row in the range
    for (int i = 0; i < rows.length; i++) {

      //set its value in the sub column
      subCol.setString(getString(rows[i]), rows[i]);

      //getting attributes from super
    }
    super.getSubset(this, pos, len);

    return subCol;

  }

  /**
   * returns a subset of this column with entries from rows indicated by
   * <code>indices</code>.
   *
   * @param indices  row numbers to include in the returned subset.
   * @return         a subset of this column, including rows indicated by
   *                 <code>indices</code>.
   */
  public Column getSubset(int[] indices) {
    SparseStringColumn retVal = new SparseStringColumn(indices.length);
    for (int i = 0; i < indices.length; i++) {
      if (row2Id.containsKey(indices[i])) {

        //XIAOLEI
        //retVal.setString(getString(indices[i]), indices[i]);
        retVal.setString(getString(indices[i]), i);

      }
    }
    super.getSubset(retVal, indices);

    return retVal;
  }

  /**
   * Sets the value at row #<code>row</code> to be the String representing
   * <code>b</code>
   *
   * @param row - the row number at which to set the value
       * @param b - the boolean value to be stored as a String at row #<code>row</code>
   */
  public void setBoolean(boolean b, int row) {
    setString(new Boolean(b).toString(), row);
  }

  /**
   * Sets the value at row #<code>row</code> to be the String representing
   * <code>b</code>
   *
   * @param row - the row number at which to set the value
       * @param b - the byte value to be stored as a String at row #<code>row</code>
   */
  public void setByte(byte b, int row) {
    /*byte[] ar = {b};
           setString(new String(ar), row);*/
    setString(Byte.toString(b), row);
  }

  /**
   * Sets the value at row #<code>row</code> to be the String representing
   * <code>b</code>
   *
   * @param row - the row number at which to set the value
   * @param b - byte array to be stored as a String at row #<code>row</code>
   */
  public void setBytes(byte[] b, int row) {
    setString(new String(b), row);
  }

  /**
   * Sets the value at row #<code>row</code> to be the String representing
   * <code>c</code>
   *
   * @param row - the row number at which to set the value
   * @param c - a char value to be stored as a String at row #<code>row</code>
   */
  public void setChar(char c, int row) {
    char[] ar = {
        c};
    setString(new String(ar), row);
  }

  /**
   * Sets the value at row #<code>row</code> to be the String representing
   * <code>c</code>
   *
   * @param row - the row number at which to set the value
   * @param c - a char array to be stored as a String at row #<code>row</code>
   */
  public void setChars(char[] c, int row) {
    setString(new String(c), row);
  }

  /**
   * Sets the value at row #<code>row</code> to be the String representing
   * a double value <code>d</code>
   *
   * @param row - the row number at which to set the value
       * @param d - a double value to be stored as a String at row #<code>row</code>
   */
  public void setDouble(double d, int row) {
    setString(Double.toString(d), row);
  }

  /**
   * Sets the value at row #<code>row</code> to be the String representing
   * a float value <code>f</code>
   *
   * @param row - the row number at which to set the value
   * @param f - a float value to be stored as a String at row #<code>row</code>
   */
  public void setFloat(float f, int row) {
    setString(Float.toString(f), row);
  }

  /**
   * Sets the value at row #<code>row</code> to be the String representing
   * an int value <code>i</code>
   *
   * @param row - the row number at which to set the value
   * @param i - an int value to be stored as a String at row #<code>row</code>
   */

  public void setInt(int i, int row) {
    setString(Integer.toString(i), row);
  }

  /**
   * Sets the value at row #<code>row</code> to be the String representing
   * a long value <code>l</code>
   *
   * @param row - the row number at which to set the value
   * @param l - a long value to be stored as a String at row #<code>row</code>
   */
  public void setLong(long l, int row) {
    setString(Long.toString(l), row);
  }

  /**
   * Sets the entry at row #<code>row</code> to be the String representing
   * Object <code>obj</code>
   * If <code>obj</code> is a char array or byte array - calling the suitable methods.
   *  Otherwise - activating obj's toString
   * method and callign setString.
   *
   * @param row - the row number at which to set the value
   * @param obj - the object to retrieve the String from.
   */
  public void setObject(Object obj, int row) {

    setString(SparseStringColumn.toStringObject(obj), row);
  }

  /**
   * Sets the value at row #<code>row</code> to be the String representing
   * a short value <code>s</code>
   *
   * @param row - the row number at which to set the value
   * @param s - a short value to be stored as a String at row #<code>row</code>
   */
  public void setShort(short s, int row) {
    setString(Short.toString(s), row);
  }

  /**
   * Sets the entry at row #<code>row</code> to be the Strign <code>s</code>.
   * If a String was already mpped to this row, then removing it first.
   *
   * @param row - the row number at which to set the new value
   * @param s - a String to be stored at row #<code>row</code>
   */
  public void setString(String str, int row) {

    //if this row is occupied -
    if (row2Id.containsKey(row)) {
      //remove the row entry from row2Id map
      int id = row2Id.remove(row);
      //if after removing the pair (row,id) from row2Id no other row points
      //to that id -
      if (!row2Id.containsValue(id)) {
        //freeing the id index in valuesIncolumn
        String removed = valuesInColumn[id];
        valuesInColumn[id] = null;
        free.add(id);
        //and removing the pair (removed, id) from the val2Key map
        val2Key.remove(removed);
      } //if not contain value

    } //if contain key

    //now row no. row is free

    int index = getInsertionIndex(str);

    if (!val2Key.containsKey(str)) {
      //putting str in the array
      valuesInColumn[index] = str;
      //putting str and the id in the val2Key map
      val2Key.put(str, index);
    } //else if val2Key

    //now for sure str is mapped to index in val2Key, and is assigned to index
    //no. index in valuesInColumn.
    //it is safe to map the row to index
    row2Id.put(row, index);

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
   * @return a SparseStringColumn ordered according to <code>newOrder</code>.
   */

  public Column reorderRows(VIntIntHashMap newOrder) {
    //     SparseStringColumn retVal = new SparseStringColumn (row2Id.size());
    SparseStringColumn retVal = (SparseStringColumn) copy();
    int[] destRows = newOrder.keys();
    for (int i = 0; i < destRows.length; i++) {
      int srcRow = newOrder.get(destRows[i]);
      if (row2Id.containsKey(srcRow)) {
        retVal.setString(getString(srcRow), destRows[i]);
      }
    }

    //  retVal.copyAttributes(this);

    return retVal;
  }

  /**
   * Returns a byte representation of the value are row No. <codE>pos</code>.
   *
   * @param pos     row number to retrieve data from
   * @return        byte representation of the data at the specified row.
   */
  public byte getByte(int pos) {

    if (!row2Id.containsKey(pos)) {
      return SparseDefaultValues.getDefaultByte();
    }

    int id = row2Id.get(pos);
    return SparseByteColumn.toByte(valuesInColumn[id]);
  }

  /**
   * Returns a char representation of the value are row No. <codE>pos</code>.
   *
   * @param pos     row number to retrieve data from
   * @return        char representation of the data at the specified row.
   */
  public char getChar(int pos) {

    if (!row2Id.containsKey(pos)) {
      return SparseDefaultValues.getDefaultChar();
    }

    int id = row2Id.get(pos);
    return SparseCharColumn.toChar(valuesInColumn[id]);
  }

  /**
       * Returns a byte array representation of the value are row No. <codE>pos</code>.
   *
   * @param pos     row number to retrieve data from
   * @return        byte array representation of the data at the specified row.
   */
  public byte[] getBytes(int pos) {

    if (!row2Id.containsKey(pos)) {
      return SparseDefaultValues.getDefaultBytes();
    }

    int id = row2Id.get(pos);
    return valuesInColumn[id].getBytes();
  }

  /**
       * Returns a char array representation of the value are row No. <codE>pos</code>.
   *
   * @param pos     row number to retrieve data from
   * @return        char array representation of the data at the specified row.
   */
  public char[] getChars(int pos) {

    if (!row2Id.containsKey(pos)) {
      return SparseDefaultValues.getDefaultChars();
    }

    int id = row2Id.get(pos);
    return valuesInColumn[id].toCharArray();
  }

  /**
       * Returns a boolean representation of the value are row No. <codE>pos</code>.
   *
   * @param pos     row number to retrieve data from
   * @return        boolean representation of the data at the specified row.
   */
  public boolean getBoolean(int pos) {

    if (!row2Id.containsKey(pos)) {
      return SparseDefaultValues.getDefaultBoolean();
    }

    int id = row2Id.get(pos);
    return SparseBooleanColumn.toBoolean(valuesInColumn[id]);
  }

  /**
   * Returns a double representation of the value are row No. <codE>pos</code>.
   *
   * @param pos     row number to retrieve data from
   * @return        double representation of the data at the specified row.
   */
  public double getDouble(int pos) {

    if (!row2Id.containsKey(pos)) {
      return SparseDefaultValues.getDefaultDouble();
    }

    int id = row2Id.get(pos);
    return SparseDoubleColumn.toDouble(valuesInColumn[id]);
  }

  /**
   * Returns a float representation of the value are row No. <codE>pos</code>.
   *
   * @param pos     row number to retrieve data from
   * @return        float representation of the data at the specified row.
   */
  public float getFloat(int pos) {

    if (!row2Id.containsKey(pos)) {
      return (float) SparseDefaultValues.getDefaultDouble();
    }

    int id = row2Id.get(pos);
    return SparseFloatColumn.toFloat(valuesInColumn[id]);
  }

  /**
   * Returns an int representation of the value are row No. <codE>pos</code>.
   *
   * @param pos     row number to retrieve data from
   * @return        int representation of the data at the specified row.
   */
  public int getInt(int pos) {

    if (!row2Id.containsKey(pos)) {
      return SparseDefaultValues.getDefaultInt();
    }

    int id = row2Id.get(pos);
    return SparseIntColumn.toInt(valuesInColumn[id]);
  }

  /**
   * Returns a short representation of the value are row No. <codE>pos</code>.
   *
   * @param pos     row number to retrieve data from
   * @return        short representation of the data at the specified row.
   */
  public short getShort(int pos) {

    if (!row2Id.containsKey(pos)) {
      return (short) SparseDefaultValues.getDefaultInt();
    }

    int id = row2Id.get(pos);
    return SparseShortColumn.toShort(valuesInColumn[id]);
  }

  /**
   * Returns a long representation of the value are row No. <codE>pos</code>.
   *
   * @param pos     row number to retrieve data from
   * @return        long representation of the data at the specified row.
   */
  public long getLong(int pos) {

    if (!row2Id.containsKey(pos)) {
      return (long) SparseDefaultValues.getDefaultInt();
    }

    int id = row2Id.get(pos);
    return SparseLongColumn.toLong(valuesInColumn[id]);
  }

  /**
       * Returns an Object representation of the value are row No. <codE>pos</code>.
   *
   * @param pos     row number to retrieve data from
   * @return        Object representation of the data at the specified row.
   */
  public Object getObject(int pos) {

    if (!row2Id.containsKey(pos)) {
      return new String(SparseDefaultValues.getDefaultString());
    }

    int id = row2Id.get(pos);
    return valuesInColumn[id];
  }

  /**
   * Returns a String representation of the value are row No. <codE>pos</code>.
   *
   * @param pos     row number to retrieve data from
   * @return        String representation of the data at the specified row.
   */
  public String getString(int pos) {

    if (!row2Id.containsKey(pos)) {
      return SparseDefaultValues.getDefaultString();
    }

    int id = row2Id.get(pos);
    return valuesInColumn[id];
  }

  /**
     Inserts a new entry in the Column at position <code>pos</code>.
     All entries at row numbers greater than <codE>pos</code> are moved down
     the column to the next row.
     @param newEntry the newEntry to insert
     @param pos the position to insert at
   */
  public void insertRow(Object newEntry, int pos) {

    String str = SparseStringColumn.toStringObject(newEntry);
    int index = getInsertionIndex(str);

    row2Id.insertObject(new Integer(index), pos);
    missing.increment(pos);
    empty.increment(pos);

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
      String val_1 = getString(pos1);
      String val_2 = getString(pos2);
      return val_1.compareTo(val_2);
    }
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
      String val_1 = SparseStringColumn.toStringObject(obj);
      String val_2 = getString(pos);
      return val_1.compareTo(val_2);
    }

  }

  protected int getInsertionIndex(String str) {
    int index;
    if (val2Key.containsKey(str)) {
      index = val2Key.get(str);
    }
    else {
      if (free.size() > 0) {
        index = free.getAt(0);
        free.remove(index);
      }
      else {
        if (firstFree >= valuesInColumn.length) {
          doubleArray();

        }
        index = firstFree;
        firstFree++;
      }
    }

    return index;
  }

  public VIntIntHashMap getNewOrder() {
    SparseStringObjectColumn tempCol = new SparseStringObjectColumn(this);
    return tempCol.getNewOrder();
  }

  public VIntIntHashMap getNewOrder(int begin, int end) {
    if (end < begin) {

      return new VIntIntHashMap(0);
    }
    SparseStringObjectColumn tempCol = new SparseStringObjectColumn(this);
    return tempCol.getNewOrder(begin, end);
  }

  /**
   * sorts the elements in this column such that the rows that were originally
   * valid remain valid after sorting and for each valid row i, getString(i)
   * is smaller than getString(i+1) (aside of the maximal row number).
   */
  public void sort() {
    VIntIntHashMap newOrder = getNewOrder();
    reorderRows(newOrder);
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
    boolean valid_1 = row2Id.containsKey(pos1);
    boolean valid_2 = row2Id.containsKey(pos2);

    int val1 = row2Id.remove(pos1);
    int val2 = row2Id.remove(pos2);

    if (valid_1) {
      row2Id.put(pos2, val1);
    }
    if (valid_2) {
      row2Id.put(pos1, val2);

    }
    missing.swapRows(pos1, pos2);
    empty.swapRows(pos1, pos2);

  }

  /**
   * Removes the data stored at row #<code>pos</code> and returns it.
   * @param pos     the row number from which to remove and retrieve the data
   * @return        the data at row #<code>pos</code>.
   *                returns null if no such dat exists.
   */
  public Object removeRow(int pos) {
    removeRowMissing(pos);

    if (!row2Id.containsKey(pos)) {
      return null;
    }

    int id = row2Id.remove(pos);
    String str = valuesInColumn[id];
    if (!row2Id.containsValue(id)) {
      valuesInColumn[id] = null;
      val2Key.remove(str);
      free.add(id);
    }

    return str;

  }

  /**
   * Returns the internal representation of this column.
   *
   */
  public Object getInternal() {
    int max_index = -1;
    String[] internal = null;
    int[] keys = row2Id.keys();

    for (int i = 0; i < keys.length; i++) {
      if (keys[i] > max_index) {
        max_index = keys[i];
      }
    }

    internal = new String[max_index + 1];
    for (int i = 0; i < max_index + 1; i++) {
      internal[i] = SparseDefaultValues.getDefaultString();
    }

    for (int i = 0; i < keys.length; i++) {
      internal[keys[i]] = valuesInColumn[row2Id.get(keys[i])];
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
   * Copies all data from <code>srcCol</codE> to this Column.
   */
  protected void copy(SparseStringColumn srcCol) {
    row2Id = srcCol.row2Id.copy();
    valuesInColumn = new String[srcCol.valuesInColumn.length];
    System.arraycopy(srcCol.valuesInColumn, 0, valuesInColumn, 0,
                     valuesInColumn.length);
    val2Key = srcCol.val2Key.copy();
    firstFree = srcCol.firstFree;
    free = srcCol.free.copy();
    super.copy(srcCol);
  }

  /**
   * enlarges the array valuesInColumn according to the load factor
   */
  protected void doubleArray() {
    int len = valuesInColumn.length;
    if (len == 0) {
      len = 1;
    }
    String[] newArray = new String[ (int) (len * (1 / DEFAULT_LOAD_FACTOR))];
    System.arraycopy(valuesInColumn, 0, newArray, 0, valuesInColumn.length);
    valuesInColumn = newArray;
  }


  /*
    returns the map that holds all valid rows in this column
   */
  protected VHashMap getElements() {
    return row2Id;
  }


} //SparseStringColumn