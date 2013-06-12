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

public class SparseShortColumn
    extends AbstractSparseColumn {

  /**
       * SparseShortColumn is a column in a sparse table that holds data of type short.
   * internal representation: the data is held in an int to short hashmap.
   * the value j mapped to key i is the value j in line i in this column.
   */

  //==============
  // Data Members
  //==============

  private VIntShortHashMap elements;

  //================
  // Constructor(s)
  //================

  /**
   * Creates a new <code>SparseShortColumn</code> instance with default
   * capacity and defualt load factor.
   */
  public SparseShortColumn() {
    this(0);
  }

  /**
   * Creates a new <code>SparseShortColumn</code> instance with a prime
   * capacity equal to or greater than <tt>initialCapacity</tt> and
   * with the default load factor.
   *
   * @param initialCapacity an <code>int</code> value
   */
  public SparseShortColumn(int initialCapacity) {
    super();
    if (initialCapacity == 0) {
      elements = new VIntShortHashMap();
    }
    else {
      elements = new VIntShortHashMap(initialCapacity);
    }
    setIsScalar(true);

    missing = new VIntHashSet();
    empty = new VIntHashSet();
    type = ColumnTypes.SHORT;
  }

  /**
   * Creates a new <code>SparseShortColumn</code> instance that will hold the
   * data in the <code>data</code> array. the elements in <code>data</code> are
   * being stored in <code>elements</code> in rows 0 through the size of
   * <code>data</code>.
   *
       * this is just to comply with regular column objects that have this constructor.
   * because this is a sparse column it is unlikely to be used.
   */

  public SparseShortColumn(short[] data) {
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
   * @param data     a short array that holds the values to be inserted
   *                 into this column
   * @param validRows  the indices to be valid in this column
   */
  public SparseShortColumn(short[] data, int[] validRows) {
    this(data.length);
    int i;
    for (i = 0; i < data.length && i < validRows.length; i++) {
      setShort(data[i], validRows[i]);

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
       * @param currVal   the current short that sort() method is looking for its new
   *                  row number in the column
   * @param values    all the short values in the column to be sorted.
       * @param row       index such that <code>values[row] == currVal</code> and also
   *                  <code>occupiedIndices[row] == true</code>.
   *                  [meaning the row number in <code>validRows[row]</code> is
       *                  already occupied by an int that equals <code>currVal</code>
   * @param occupiedIndices   a flag array in which each index in <vode>validRows</code>
       *                          that was already occupied by sort() is marked true.
   * @return an index i s.t. <code>currval == values[i] && occupiedIndices[i] == false</code>
   */
  public static int getNewRow(short currVal, short[] values, int row,
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
   * Converts obj into type short.
   * If obj is null returns the Minimum Value of class Short.
   *
   * @param obj   an Object to be converted into type short
   * @return      a short representation of the data held by <code>obj</code>
   *              # If obj is null returns a value signifying the position is empty,
   *                as defined by this class.
   *              # If obj is a Number return its short value
       *              # If obj is a Character return it char value casted into short
   *              # If obj is a Boolean return 1 if obj=true else return 0
   *              # Otherwise: construct a String from obj and attempt to
   *                  parse a short from it.
   */
  public static short toShort(Object obj) {

    if (obj == null) {
      return (short) SparseDefaultValues.getDefaultInt();
    }

    if (obj instanceof Number) {
      return ( (Number) obj).shortValue();
    }

    if (obj instanceof Character) {
      return (short) ( (Character) obj).charValue();
    }

    if (obj instanceof Boolean) {
      return ( (Boolean) obj).booleanValue() ? (short) 1 : (short) 0;
    }

    String str;
    if (obj instanceof char[]) {
      str = new String( (char[]) obj);
    }
    else if (obj instanceof byte[]) {
      str = new String( (byte[]) obj);
    }
    else { //obj is either a Number or String or an unknown object
      str = obj.toString();

    }
    float f = Float.parseFloat(str);
    return (short) f;

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
    if (!elements.containsKey(row)) {
      return SparseDefaultValues.getDefaultByte();
    }
    return (byte) elements.get(row);
  }

  /**
       Casts <code>newEntry</code> to a short and sets the value at <code>pos</code>.
   @param newEntry the new item
   @param pos the position
   */
  public void setByte(byte newEntry, int pos) {
    setShort( (short) newEntry, pos);
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
    return (elements.get(row) != 0);
  }

  /**
        If newEntry is true, set the value at pos to be 1.  Else set the value
        at pos to be 0.
        @param newEntry the new item
        @param pos the position
   */
  public void setBoolean(boolean newEntry, int pos) {
    if (newEntry) {
      setShort( (short) 1, pos);
    }
    else {
      setShort( (short) 0, pos); ;
    }
  }

  /**
   * Returns the value at row # row converted to a bytes array.
   * @param row the row number
   * @return the value in row # row represented with a bytes array.
   * If no such value exists returns null.
   */
  public byte[] getBytes(int row) {
    if (!elements.containsKey(row)) {
      return SparseDefaultValues.getDefaultBytes();
    }
    return String.valueOf(elements.get(row)).getBytes();
  }

  /**
       Constructs a String from <code>newEntry</code> and calls setString method
        to set the string to row #<code>pos</code>
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
    return (char) elements.get(row);
  }

  /**
        casts newEntry to a short and sets it at pos.
        @param newEntry the new item
        @param pos the position
   */
  public void setChar(char newEntry, int pos) {
    setShort( (short) newEntry, pos);
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
    return Short.toString(elements.get(row)).toCharArray();
  }

  /**
        Convert newEntry to a String and call setString().
        @param newEntry the new item
        @param pos the position
   */
  public void setChars(char[] newEntry, int pos) {
    setString(new String(newEntry), pos);
  }

  /**
   * Returns the value at row # row casted  to double
   * @param row the row number
   * @return the value at row # row casted to double. If no such value exists
   * returns a value signifying the position is empty,
   * as defined by SparseDoubleColumn.
   */
  public double getDouble(int row) {
    if (!elements.containsKey(row)) {
      return SparseDefaultValues.getDefaultDouble();
    }
    return (double) elements.get(row);
  }

  /**
   * Returns the value at row # row casted to float type
   * @param row the row number
   * @return the value at row # row casted to float. If no such value exists
   * returns a value signifying the position is empty, as defined by SparseFloatColumn.
   */
  public float getFloat(int row) {
    if (!elements.containsKey(row)) {
      return (float) SparseDefaultValues.getDefaultDouble();
    }
    return (float) elements.get(row);
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
    return (int) elements.get(row);
  }

  /**
   * Returns the value at row # row casted to type long
   * @param row the row number
   * @return the value at row # row casted to long.
   * if no such value exists returns a value signifying the position is empty,
   * as defined by SparseLongColumn.
   */
  public long getLong(int row) {
    if (!elements.containsKey(row)) {
      return (long) SparseDefaultValues.getDefaultInt();
    }
    return (long) elements.get(row);
  }

  /**
   * Returns the value at row # row, encapsulated in a Short object
   * @param row the row number
   * @return Short object encapsulating the value at row # row. returns null
   *        if no such entry exist.
   */
  public Object getObject(int row) {
    if (elements.containsKey(row)) {
      return new Short(elements.get(row));
    }
    else {
      return new Short( (short) SparseDefaultValues.getDefaultInt());
    }
  }

  /**
   * Returns the value at row # row
   * @param row the row number
   * @return the short value at row # row. if no such  entry exists returns
   * a value signifying the position is empty,
   * as defined by this class.
   */
  public short getShort(int row) {
    if (elements.containsKey(row)) {
      return elements.get(row);
    }
    else {
      return (short) SparseDefaultValues.getDefaultInt();
    }
  }

  /**
   * Returns the value at row # row, represented as  a String.
   * @param row the row number
   * @return a String Object representing the value at row # row.
   * If no such value exists returns null.
   */
  public String getString(int row) {
    if (!elements.containsKey(row)) {
      return "" + (short) SparseDefaultValues.getDefaultInt();
    }
    return String.valueOf(elements.get(row));
  }

  /**
   * performs a deep copy of this SparseIntColumn
   * returns an exact copy of this SparseIntColumn
   *
   * @return a SparseShortColumn, that holds an exact deep copy of this column.
   */
  public Column copy() {
    SparseShortColumn retVal;
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(baos);
      oos.writeObject(this);
      byte buf[] = baos.toByteArray();
      oos.close();
      ByteArrayInputStream bais = new ByteArrayInputStream(buf);
      ObjectInputStream ois = new ObjectInputStream(bais);
      retVal = (SparseShortColumn) ois.readObject();
      ois.close();
      return retVal;
    }
    catch (Exception e) {

      retVal = new SparseShortColumn();

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
    SparseShortColumn retVal = new SparseShortColumn(indices.length);
    for (int i = 0; i < indices.length; i++) {
      if (elements.containsKey(indices[i])) {

        //XIAOLEI
        //retVal.setShort(getShort(indices[i]), indices[i]);
        retVal.setShort(getShort(indices[i]), i);

      }
    }
    super.getSubset(retVal, indices);

    return retVal;
  }

  /**
   * Returns a SparseShortColumn that holds only the data from rows <code>
   * pos</code> through <code>pos+len</code>
   * @param pos   the row number which is the beginning of the subset
   * @param len   number of consequetive rows after <code>pos</code> that are
   *              to be included in the subset.
   * @return      a SparseShortColumn with the data from rows <code>pos</code>
   *              through <code>pos+len</code>
   */
  public Column getSubset(int pos, int len) {
    SparseShortColumn subCol = new SparseShortColumn();
    subCol.elements = (VIntShortHashMap) elements.getSubset(pos, len);
    getSubset(subCol, pos, len);

    return subCol;

    /*       //the map to hold the data of the new column
         VIntShortHashMap tempMap = new VIntShortHashMap (len);
         //for each row from pos till pos+len
         for (int i=0; i<len; i++)
          //if a value is mapped to current inspected row number
          if(elements.containsKey(pos+i))
//put it in the new map
     tempMap.put(pos+i, getShort(pos+i));
         SparseShortColumn subCol = new SparseShortColumn();   //the returned value
        super.copy(subCol);     //copying general attributes
        subCol.elements = tempMap;    //linking the data to the returned value
        subCol.missing = missing.getSubset(pos, len);
        subCol.empty = missing.getSubset(pos, len);
         return subCol;*/

  }

  /**
   * Removes the short value in row # <code>pos</code> and returns it
   * encapsulated in an Int object
   * @param pos     the row number from which to remove and retrieve the data
       * @return        a Short Object encapsulating the data at row #<code>pos</code>.
   *                returns null if no such entry exist.
   */
  public Object removeRow(int pos) {

    removeRowMissing(pos);

    if (elements.containsKey(pos)) {
      return new Short(elements.remove(pos));
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
    elements = (VIntShortHashMap) elements.reorder(newOrder);

    missing = missing.reorder(newOrder);
    empty = empty.reorder(newOrder);

  }


  /**
        Set the item at pos to be newEntry by casting it to a short.
        @param newEntry the new item
        @param pos the position
   */
  public void setDouble(double newEntry, int pos) {
    setShort( (short) newEntry, pos);
  }

  /**
        casts newEntry to a short and sets it at pos.
        @param newEntry the new item
        @param pos the position
   */
  public void setFloat(float newEntry, int pos) {
    setShort( (short) newEntry, pos);
  }

  /**
        casts newEntry to a short and sets it at pos.
        @param newEntry the new item
        @param pos the position
   */
  public void setInt(int newEntry, int pos) {
    setShort( (short) newEntry, pos);
  }

  /**
        casts newEntry to a short and sets it at pos.
        @param newEntry the new item
        @param pos the position
   */
  public void setLong(long newEntry, int pos) {
    setShort( (short) newEntry, pos);
  }

  /**
       Converts <code>newEntry</code> to a short using toShort method, and sets
       the short value to row #<code>pos</code>
        @param newEntry the new item
        @param pos the position
   */
  public void setObject(Object newEntry, int pos) {
    setShort(toShort(newEntry), pos);
  }

  /**
   * Set the value at pos to be newEntry, removes any value that was stored
   * at pos
   * @param newEntry the new item
   * @param pos the position
   */
  public void setShort(short newEntry, int pos) {
    elements.remove(pos);
    elements.put(pos, newEntry);
  }

  /**
   Attempts to parse a short value from <code>newEntry</code> and assigns
   it to row #<code>pos</code>
   @param newEntry the new item
   @param pos the position
   */
  public void setString(String newEntry, int pos) {
    setShort( (short) (Double.parseDouble(newEntry)), pos);
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
      short val_1 = toShort(obj);
      short val_2 = elements.get(pos);
      return compareShorts(val_1, val_2);
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
      short val_1 = elements.get(pos1);
      short val_2 = elements.get(pos2);
      return compareShorts(val_1, val_2);
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
    if (pos1 == pos2) {
      return;
    }
    boolean valid_1 = elements.containsKey(pos1);
    boolean valid_2 = elements.containsKey(pos2);

    short val1 = elements.remove(pos1);
    short val2 = elements.remove(pos2);

    if (valid_1) {
      setShort(val1, pos2);
    }
    if (valid_2) {
      setShort(val2, pos1);

    }

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
   * @return a SparseShortColumn ordered according to <code>newOrder</code>.
   */

  public Column reorderRows(VIntIntHashMap newOrder) {
    SparseShortColumn retVal = new SparseShortColumn();
    retVal.elements = (VIntShortHashMap) elements.reorder(newOrder);
    reorderRows(retVal, newOrder);
    return retVal;
  }

  /**
   * Returns the internal representation of this column.
   *
   */
  public Object getInternal() {
    int max_index = -1;
    short[] internal = null;
    int[] keys = elements.keys();

    for (int i = 0; i < keys.length; i++) {
      if (keys[i] > max_index) {
        max_index = keys[i];
      }
    }

    internal = new short[max_index + 1];
    for (int i = 0; i < max_index + 1; i++) {
      internal[i] = (short) SparseDefaultValues.getDefaultInt();
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
  protected void insertRow(short val, int pos) {
    boolean valid = elements.containsKey(pos);
    short removedValue = elements.remove(pos);
    //putting the new value
    setShort(val, pos);
    //recursively moving the items in the column as needed
    if (valid) {
      insertRow(removedValue, pos + 1);

    }
  }

  /**
   * return the internal representation of the data
   * @return     a VIntShortHashMap - the internal representation of the data
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
  protected short[] getValuesInRange(int begin, int end) {
    if (end < begin) {
      short[] retVal = {};
      return retVal;
    }
    return elements.getValuesInRange(begin, end);
    /*
         short[] values =  new short[end - begin +1];
         int j= 0;
         for (int i=begin; i<=end; i++)
     if(doesValueExist(i)){
       values[j] = getShort(i);
      j++;
     }//end if
         //now j is number of real elements in values.
         short[] retVal = new short[j];
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
  private int compareShorts(short val_1, short val_2) {
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
  * @return          a SparseShortColumn with the same values as this one,
  *                  and a reordered section as indicated by <code>begin</code>
  *                  and <code>end</code>.
  *
      public Column reorderRows(int[] newOrder, int begin, int end){
        SparseShortColumn  retVal = new SparseShortColumn();
      retVal.elements = (VIntShortHashMap)elements.reorder(newOrder, begin, end);
        int[] oldOrder = getRowsInRange(begin, end);
        retVal.missing = missing.reorder(newOrder, oldOrder, begin, end);
        retVal.empty = empty.reorder(newOrder, oldOrder, begin, end);
        super.copy(retVal);
        return retVal;
        /*
          int[] oldOrder = getRowsInRange(begin, end);
          SparseShortColumn  retVal = new SparseShortColumn();
          retVal.elements = elements.copy();
          retVal.removeRows(begin, end-begin+1);
          //each val V that is mapped to row no. newOrder[i] will be mapped
          //to row no. oldOrder[i] in retVal.
          for (int i=0; i<newOrder.length && i<oldOrder.length; i++){
     if(elements.containsKey(newOrder[i]))
       retVal.setShort(elements.get(newOrder[i]), oldOrder[i]);
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
         public int[] getNewOrder(int begin, int end){
           //sorting the valid row numbers
            int[] validRows = getRowsInRange(begin, end);
            //sorting the values
            short[] values = getValuesInRange(begin, end);
         int[] newOrder = new int[validRows.length];
          boolean[] ocuupiedIndices = new boolean[validRows.length];
          short currVal;
          //for each valid row validRows[i]
          for (int i=0; i<validRows.length; i++){
            currVal = elements.get(validRows[i]);
            //finding the index of its mapped float in values
            int newRow = Arrays.binarySearch(values, currVal);
            //because binarySearch can return the same index for items that are identical
            //checking for this option too.
            if(ocuupiedIndices[newRow])
       newRow = getNewRow(currVal, values, newRow, ocuupiedIndices);
            ocuupiedIndices[newRow] = true;
            newOrder[newRow] = validRows[i];
          }//end of for
          return newOrder;
         }
*/