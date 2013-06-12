package ncsa.d2k.modules.core.datatype.table.sparse.columns;

//===============
// Other Imports
//===============

import ncsa.d2k.modules.core.datatype.table.sparse.primitivehash.*;
import ncsa.d2k.modules.core.datatype.table.util.ByteUtils;
import ncsa.d2k.modules.core.datatype.table.sparse.*;
import ncsa.d2k.modules.core.datatype.table.MutableTable;
import ncsa.d2k.modules.core.datatype.table.basic.Column;
import ncsa.d2k.modules.core.datatype.table.ColumnTypes;

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

public class SparseObjectColumn
    extends AbstractSparseColumn {

  /**
   * SparseObjectColumn is a column in a sparse table that holds data of
   * non primitive type
   * internal representation: the column is an int to object hashmap.
   * the value j mapped to key i is the value j in line i in this column.
   */

  //==============
  // Data Members
  //==============

  protected VIntObjectHashMap elements; //holds the data of this column

  //================
  // Constructor(s)
  //================

  /**
   * Creates a new <code>SparseObjectColumn </code> instance with the
   * capacity zero and default load factor.
   */
  public SparseObjectColumn() {
    this(0);
  }

  /**
   * Creates a new <code>SparseObjectColumn </code> instance with a prime
   * capacity equal to or greater than <tt>initialCapacity</tt> and
   * with the default load factor.
   *
   * @param initialCapacity an <code>int</code> value
   */
  public SparseObjectColumn(int initialCapacity) {
    super();
    if (initialCapacity == 0) {
      elements = new VIntObjectHashMap();
    }
    else {
      elements = new VIntObjectHashMap(initialCapacity);
    }
    type = ColumnTypes.OBJECT;
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

  public SparseObjectColumn(Object[] data) {
    this(data.length);
    for (int i = 0; i < data.length; i++) {
      addRow(data[i]);
    }
  }

  /**
   * constrcut a new SparseObjectColumn. each value data[i] is set to
   * validRows[i]. if validRows is smaller than data, the rest of the
   * values in data are being inserted to the end of this column
   *
   * @param data     an Object array that holds the values to be inserted
   *                 into this column
   * @param validRows  the indices to be valid in this column
   */
  public SparseObjectColumn(Object[] data, int[] validRows) {
    this(data.length);
    int i;
    for (i = 0; i < data.length && i < validRows.length; i++) {
      setObject(data[i], validRows[i]);

    }
    for (; i < data.length; i++) {
      addRow(data[i]);

    }
  }

  //================
  // Public Methods
  //================

  /**
   * If the object at row # row is a byte array, returns the first item in the
   * array. else - returns a representation of this object as a bytes array.
   * @param row the row number
   */
  public byte getByte(int row) {
    Object obj = elements.get(row);
    if (obj != null && isDataNumeric(row)) {
      return SparseByteColumn.toByte(obj);
    }

    return SparseDefaultValues.getDefaultByte();
  }

  /**
   *     Returns the item at pos as a boolean.  If the item is a Boolean,
       return its boolean value, otherwise construct a new Boolean
       by calling the toString() method on the item and return its
       boolean value.
       @param row the row number
       @return the item as pos as a boolean value. If no such item exists returns
       false.
   */
  public boolean getBoolean(int row) {
    Object obj = elements.get(row);
    if (obj != null) {
      return SparseBooleanColumn.toBoolean(obj);
    }

    return SparseDefaultValues.getDefaultBoolean();
  }

  /**
    If the entry at pos is a byte[], return the byte[], otherwise
    convert the Object to a byte[] by calling ByteUtils.writeObject()
    @param row the row number
    @return the entry at pos as a byte[]. if no such entry exists returns
    null
   */
  public byte[] getBytes(int row) {
    Object obj = elements.get(row);
    return SparseByteArrayColumn.toByteArray(obj);
  }

  /**
        Retrun the value at row #<code>row</code> as a char.
        @param row the row number
       @return the item at row as a char, if no such item exists return a value
        signifying the position is empty, as defined by SparseCharColumn.
   */
  public char getChar(int row) {
    Object obj = elements.get(row);
    if (obj != null) {
      return SparseCharColumn.toChar(obj);
    }

    return SparseDefaultValues.getDefaultChar();
  }

  /**
   If the item at pos is a char[], return it.  Otherwise
     converts it to a char[] using SparseCharArrayColumn's method.
   * @param row the row number
   * @return the value at row # row represented with a chars array. If no
   * such value exists returns null.
   */
  public char[] getChars(int row) {
    Object obj = elements.get(row);
    return SparseCharArrayColumn.toCharArray(obj);
  }

  /**
   *
     If the item at pos is a Number, return its double value. Otherwise
     if the item is a char[] or any other type of Object, convert the item to
     a String and return its double value by calling Double.parseDouble()
   * @param row the row number
     @return the double value of the item at row # row. if no such item exists
     returns a value signifying the position is empty,
   * as defined by SparseDoubleColumn.
   */
  public double getDouble(int row) {
    Object obj = elements.get(row);
    if (obj != null && isDataNumeric(row)) {
      return SparseDoubleColumn.toDouble(obj);
    }

    return SparseDefaultValues.getDefaultDouble();

  }

  /**
   *
     If the item at pos is a Number, return its float value. Otherwise
     if the item is a char[] or any other type of Object, convert the item to
     a String and return its float value.
   * @param row the row number
       @return the float value of the item at row # row. if no such item exists or
       if the data is not numeric - returns a value signifying the position is empty,
     as defined by SparseFloatColumn.
   */
  public float getFloat(int row) {
    Object obj = elements.get(row);
    if (obj != null && isDataNumeric(row)) {
      return SparseFloatColumn.toFloat(obj);
    }

    return (float) SparseDefaultValues.getDefaultDouble();

  }

  /**
   * If the item at pos is a Number, return its int value. Otherwise
       if the item is a char[] or any other type of Object, convert the item to
        a String and return its int value.
        @param row the row number
        @return the int value of the item at row # row. If no such value exists
        returns a value signifying the position is empty,
   * as defined by SparseIntColumn.
   */
  public int getInt(int row) {
    Object obj = elements.get(row);
    if (obj != null && isDataNumeric(row)) {
      return SparseIntColumn.toInt(obj);
    }

    return SparseDefaultValues.getDefaultInt();

  }

  /**
        If the item at pos is a Number, return its long value. Otherwise
       if the item is a char[] or any other type of Object, convert the item to
        a String and return its long value.
        @param row the row number
       @return the long value of the item at row # row. If no such value exists
        returns a value signifying the position is empty,
   * as defined by SparseLongColumn.
   */
  public long getLong(int row) {
    Object obj = elements.get(row);
    if (obj != null && isDataNumeric(row)) {
      return SparseLongColumn.toLong(obj);
    }

    return (long) SparseDefaultValues.getDefaultInt();
  }

  /**
   * Returns the object at row # row
   * @param row the row number
   * @return the object at row # row. if no such object exists - returns null.
   */
  public Object getObject(int row) {
    if (elements.containsKey(row)) {
      return elements.get(row);
    }
    else {
      return SparseDefaultValues.getDefaultObject();
    }
  }

  /**
     If the item at pos is a Number, return its short value. Otherwise
     if the item is a char[] or any other type of Object, convert the item to
     a String and return its short value.
     @param row the row number
       @return the short value of the item at row number row. If no such value exists
     returns a value signifying the position is empty,
   * as defined by SparseShortColumn.
   */
  public short getShort(int row) {
    Object obj = elements.get(row);
    if (obj != null && isDataNumeric(row)) {
      return SparseShortColumn.toShort(obj);
    }

    return (short) SparseDefaultValues.getDefaultInt();
  }

  /**
   * Returns the value at row # row, represented as  a String.
   * @param row the row number
   * @return a String Object representing the value at row # row. if no such
   * value exists returns null
   */
  public String getString(int row) {
    Object obj = elements.get(row);
    return SparseStringColumn.toStringObject(obj);
  }

  /**
   * performs a deep copy of this SparseObjectColumn
   * returns an exact copy of this SparseObjectColumn
   *
   * @return     a SparseObjectColumn object which is a deep copy of this
   * SparseObjectColumn.
   */
  public Column copy() {
    SparseObjectColumn retVal;
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(baos);
      oos.writeObject(this);
      byte buf[] = baos.toByteArray();
      oos.close();
      ByteArrayInputStream bais = new ByteArrayInputStream(buf);
      ObjectInputStream ois = new ObjectInputStream(bais);
      retVal = (SparseObjectColumn) ois.readObject();
      ois.close();
      return retVal;
    }
    catch (Exception e) {

      retVal = new SparseObjectColumn();
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
    SparseObjectColumn retVal = new SparseObjectColumn(indices.length);
    for (int i = 0; i < indices.length; i++) {
      if (elements.containsKey(indices[i])) {

        //XIAOLEI
        //retVal.setObject(getObject(indices[i]), indices[i]);
        retVal.setObject(getObject(indices[i]), i);

      }
    }
    super.getSubset(retVal, indices);

    return retVal;
  }

  /**
   * Returns a SparseObjectColumn that holds only the data from rows <code>
   * pos</code> through <code>pos+len</code>
   * @param pos   the row number which is the beginning of the subset
   * @param len   number of consequetive rows after <code>pos</code> that are
   *              to be included in the subset.
   * @return      a SparseObjectColumn with the data from rows <code>pos</code>
   *              through <code>pos+len</code>
   */
  public Column getSubset(int pos, int len) {
    SparseObjectColumn subCol = new SparseObjectColumn();
    subCol.elements = (VIntObjectHashMap) elements.getSubset(pos, len);
    getSubset(subCol, pos, len);

    return subCol;
    /*
         //the map to hold the data of the subset
          VIntObjectHashMap tempMap = new VIntObjectHashMap(len);
          //for each Object from pos till pos+len
          for (int i=0; i<len; i++)
     //if a value is mapped to current inspected row number
     if(elements.containsKey(pos+i))
      //put it in the new map
      tempMap.put(pos+i, getObject(pos+i));
         SparseObjectColumn subCol = new SparseObjectColumn();   //the returned value
         super.copy(subCol);     //copying general attributes
         //activating copy method in order to have a deep copy of the subset
         subCol.elements = tempMap.copy();    //linking the data to the returned value
          return subCol;
     */
  }

  /**
   * removes a row from this column and returns the removed item as an object
   * @param row - the row number to be removed.
   * @return - the removed object.
   */
  public Object removeRow(int row) {
    return elements.remove(row);
  }

  /**
   * sorts the elements in this column so that the rows that were originally
   * valid remain valid after sorting
       * Note - sort() supports only Comparable Objects or char arrays or byte arrays.
   * using sort with objects different than the above will throw an exception.
   */
  public void sort() {
    VIntIntHashMap newOrder = getNewOrder();
    elements = (VIntObjectHashMap) elements.reorder(newOrder);

    missing = missing.reorder(newOrder);
    empty = empty.reorder(newOrder);
  }

  /**
        Set the item at pos to be a Bollean object encapsulating newEntry.
        @param newEntry the new item
        @param pos the position
   */
  public void setBoolean(boolean newEntry, int pos) {
    setObject(new Boolean(newEntry), pos);
  }

  /**
        Set the item at pos to be a Byte object encapsulating newEntry.
        @param newEntry the new item
        @param pos the position
   */
  public void setByte(byte newEntry, int pos) {
    setObject(new Byte(newEntry), pos);
  }

  /**
        Set the item at pos to be a Character object encapsulating newEntry.
        @param newEntry the new item
        @param pos the position
   */
  public void setChar(char newEntry, int pos) {
    setObject(new Character(newEntry), pos);
  }

  /**
        Set the item at pos to be a Double object encapsulating newEntry.
        @param newEntry the new item
        @param pos the position
   */
  public void setDouble(double newEntry, int pos) {
    setObject(new Double(newEntry), pos);
  }

  /**
        Set the item at pos to be a Float object encapsulating newEntry.
        @param newEntry the new item
        @param pos the position
   */
  public void setFloat(float newEntry, int pos) {
    setObject(new Float(newEntry), pos);
  }

  /**
   Set the item at pos to be a Long object encapsulating newEntry.
   @param newEntry the new item
   @param pos the position
   */
  public void setLong(long newEntry, int pos) {
    setObject(new Long(newEntry), pos);
  }

  /**
        Set the item at pos to be an Integer object encapsulating newEntry.
        @param newEntry the new item
        @param pos the position
   */
  public void setInt(int newEntry, int pos) {
    setObject(new Integer(newEntry), pos);
  }

  /**
        Set the item at pos to be a Short object encapsulating newEntry.
        @param newEntry the new item
        @param pos the position
   */
  public void setShort(short newEntry, int pos) {
    setObject(new Short(newEntry), pos);
  }

  /**
        Set the value at pos to be newEntry.
        @param newEntry the new item
        @param pos the position
   */
  public void setString(String newEntry, int pos) {
    setObject(newEntry, pos);
  }

  /**
   Set the value at pos to be newEntry.
   @param newEntry the new item
   @param pos the position
   */
  public void setBytes(byte[] newEntry, int pos) {
    setObject(newEntry, pos);
  }

  /**
   * Sets the item at row #<code>pos</code> to be <code>newEntry</code>
   *
   * @param newEntry - the object to be assigned to row #<code>pos</code>
   * param pos - the row number to be set.
   */
  public void setObject(Object newEntry, int pos) {
    elements.remove(pos);
    elements.put(pos, newEntry);
  }

  /**
        Set the value at pos to be newEntry.
        @param newEntry the new item
        @param pos the position
   */
  public void setChars(char[] newEntry, int pos) {
    setObject(newEntry, pos);
  }

  /**
   * used by sort method.
   * returns a new index for a new row number for the item <code>currVal</code>
       * the index is the first index i to be found in <code>values</code> such that
   * <code>currVal equals values[i] and occupiedIndices[i] == false</code>.
   * this index i is then used in the array validRows by sort().
   *
       * @param currVal - the current Object that sort() method is looking for its new
   * row number in the column
   * @param values - all the Objects in the column to be sorted. this array is sorted.
       * @param row - an index such that <code>values[row] equals to currVal</code> and
   * also <code> occupiedIndices[row] == true</code>.
   * [meaning the row number in <code>validRows[row]</code> is already occupied
   * by an Object that equals <code>currVal</code>
   * @param occupiedIndices - a flag array in which each index in <vode>validRows</code>
   * that was already occupied by sort() is marked true.
   */
  public int getNewRow(Object currVal, Object[] values, int row,
                       boolean[] ocuupiedIndices) {
    int retVal = -1;
    //searching values at indices smaller than row
    for (int i = row - 1; i >= 0 && values[i].equals(currVal) && retVal < 0; i--) {
      if (!ocuupiedIndices[i]) {
        retVal = i;

        //searching values at indices greater than row
      }
    }
    for (int i = row + 1;
         retVal < 0 && i < values.length && values[i].equals(currVal); i++) {
      if (!ocuupiedIndices[i]) {
        retVal = i;

      }
    }
    return retVal;
  }

  /**
     Inserts a new entry in the Column at position <code>pos</code>.
     All entries at row numbers greater than <codE>pos</code> are moved down
     the column to the next row.
     @param newEntry the newEntry to insert
     @param pos the position to insert at
   */
  public void insertRow(Object newEntry, int pos) {

    switch (getType()) {
      case ColumnTypes.BYTE_ARRAY:
        newEntry = SparseByteArrayColumn.toByteArray(newEntry);
        break;
      case ColumnTypes.CHAR_ARRAY:
        newEntry = SparseCharArrayColumn.toCharArray(newEntry);
        break;
      case ColumnTypes.STRING:
        newEntry = SparseStringColumn.toStringObject(newEntry);
        break;
    }

    super.insertRow(newEntry, pos);
  }

  /**
   * Compares 2 Objects.
   * Both must be of the same type or mutually comparable.
   *
   * If they are Comparable - calling compareTo method of <code>val1</code> on
   * <code>val2</code>
   * If not: constructing 2 strings from val1 and val2 and calling compareTo
   * method of String.
   *
   * @param val1    first object to be compared.
   * @param val2    second object to be compared.
       * @return        If the objects are compared as Strings - the returned value is
   *                the same as String's compareTo(Object) would return.
   *                else: 1 if val1 is greater than val2. -1 if val1 is smaller
   *                than vla2. 0 if they are equal.
   */
  public int compareObjects(Object val1, Object val2) {

    //if they are comparable using the compareTo method
    if (val1 instanceof Comparable) {
      return ( (Comparable) val1).compareTo(val2);
    }

    if (val1 instanceof Boolean) {
      return SparseBooleanColumn.compareBooleans(
          ( (Boolean) val1).booleanValue(), ( (Boolean) val2).booleanValue());
    }

    String str1, str2;
    if (val1 instanceof byte[]) {
      str1 = new String( (byte[]) val1);
      str2 = new String( (byte[]) val2);
    }
    else if (val1 instanceof char[]) {
      str1 = new String( (char[]) val1);
      str2 = new String( (char[]) val2);
    }

    else {
      str1 = val1.toString();
      str2 = val2.toString();
    }
    //converting to strings and comparing
    return str1.compareTo(str2);
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
    //validating the objects in the given rows.
    int val = validate(pos1, pos2);
    if (val <= 1) {
      return val;
    }

    Object obj1 = getObject(pos1);
    Object obj2 = getObject(pos2);

    //if both are of the same class - comparing them
    if (obj1.getClass().equals(obj2.getClass())) {
      return compareObjects(obj1, obj2);
    }

    //if all the elements in this column are numeric - converting
    //the objects to doubles and comparing them.
    if (isNumeric()) {
      return SparseDoubleColumn.compareDoubles(getDouble(pos1), getDouble(pos2));
    }

    //if not all of the elements are numeric - coverting the objects to strings
    //and comparing them.
    return SparseStringColumn.toStringObject(obj1).compareTo
        (SparseStringColumn.toStringObject(obj2));

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
  public int compareRows(Object element, int pos) {

    //validating the objects to be compared
    int val = validate(element, pos);
    if (val <= 1) {
      return val;
    }

    //retrieving the object from pos
    Object obj = getObject(pos);

    //if they are of the smae type - comparing them
    if (element.getClass().equals(obj.getClass())) {
      return compareObjects(element, obj);
    }

    //if both are numbers - comparing them as doubles.
    if (element instanceof Number && obj instanceof Number) {
      return SparseDoubleColumn.compareDoubles(SparseDoubleColumn.toDouble(
          element),
                                               SparseDoubleColumn.toDouble(obj));
    }

    //they are probably not mutually comparable...
    //if this is an object column, i.e. the type of the objects is unknown
    //converting both of the objects into strings and comparing them
    if (ColumnTypes.OBJECT == getType()) {
      return SparseStringColumn.toStringObject(element).compareTo(
          SparseStringColumn.toStringObject(obj));
    }

    //this is not an object column - converting element to the type of this
    //column, and comparing the objects.
    switch (getType()) {
      case (ColumnTypes.BYTE_ARRAY):
        element = SparseByteArrayColumn.toByteArray(element);
        break;
      case (ColumnTypes.CHAR_ARRAY):
        element = SparseCharArrayColumn.toCharArray(element);
        break;
      case (ColumnTypes.STRING):
        element = SparseStringColumn.toStringObject(element);
        break;
      default:
        break;
    }

    return compareObjects(element, obj);
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
    Object obj1 = elements.remove(pos1);
    Object obj2 = elements.remove(pos2);
    if (obj1 != null) {
      setObject(obj1, pos2);
    }
    if (obj2 != null) {
      setObject(obj2, pos1);

    }
    missing.swapRows(pos1, pos2);
    empty.swapRows(pos1, pos2);

  }

  public VIntIntHashMap getNewOrder() {
    int[] validRows = getIndices();
    return getNewOrder(validRows);

  }

  /**
       * Returns an int to int hash map with old row numbers mapped to new row numbers,
   * which defines a sorted  order for this column
   *
   * @param validRows     row numbers from which the sorted order is retrieved.
   *
   */
  public VIntIntHashMap getNewOrder(int[] validRows) {

    Object[] sortedValues = getValuesInRange(validRows[0],
                                             validRows[validRows.length - 1]);

    Object[] rawValues = getValues(validRows);

    //will hold the new order, the returned value.
    int[] newOrder = new int[validRows.length];

    //a flag array. occupiedIndices[j] == true mean that the row number
    //stored at validRows[j] was already mapped to a value in tempMap
    boolean[] ocuupiedIndices = new boolean[validRows.length];

    //for each valid row validRows[i]
    for (int i = 0; i < validRows.length; i++) {

      //finding the index of its mapped Object in values

      int newRow = Arrays.binarySearch(sortedValues, rawValues[i]);

      //because binarySearch can return the same index for items that are identical
      //checking for this option too.
      if (ocuupiedIndices[newRow]) {

        //if newRow index was already used - finding an alternative index
        newRow = getNewRow(rawValues[i], sortedValues, newRow, ocuupiedIndices);

        //marking the associated flag as true
      }
      ocuupiedIndices[newRow] = true;

      newOrder[newRow] = validRows[i];
    } //end of for
    return VHashService.getMappedOrder(validRows, newOrder);
  }

  /**
   * Retrieve a new order for the valid rows of this column in the range
   * <code>begin</code> through <code>end</code>. The new order is a sorted
   * order for that section of the column.
   *
   * @param begin     row no. from which to start retrieving the new order
   * @param end       the last row in the section from which to retrieve the new order.
   */
  public VIntIntHashMap getNewOrder(int begin, int end) {

    if (end < begin) {
      return new VIntIntHashMap(0);

    }
    //sorting the valid row numbers
    int[] validRows = getRowsInRange(begin, end);

    return getNewOrder(validRows);

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
   * @return a SparseObjectColumn ordered according to <code>newOrder</code>.
   */

  public Column reorderRows(VIntIntHashMap newOrder) {
    SparseObjectColumn retVal = new SparseObjectColumn();
    retVal.elements = (VIntObjectHashMap) elements.reorder(newOrder);
    reorderRows(retVal, newOrder);
    return retVal;
  }

  public void copy(AbstractSparseColumn srcCol) {
    if (srcCol instanceof SparseObjectColumn) {
      elements = (VIntObjectHashMap) ( (SparseObjectColumn) srcCol).elements.
          copy();
    }
    super.copy(srcCol);
  }

  /**
   * Returns the internal representation of this column.
   *
   */
  public Object getInternal() {
    int max_index = -1;
    Object[] internal = null;
    int[] keys = elements.keys();

    for (int i = 0; i < keys.length; i++) {
      if (keys[i] > max_index) {
        max_index = keys[i];
      }
    }

    internal = new Object[max_index + 1];
    for (int i = 0; i < max_index + 1; i++) {
      internal[i] = SparseDefaultValues.getDefaultObject();
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
  protected Object[] getValuesInRange(int begin, int end) {

    if (end < begin) {
      Object[] retVal = {};
      return retVal;
    }

    int[] indices = VHashService.getIndicesInRange(begin, end, elements);
    Object[] values = new Object[indices.length];
    for (int i = 0; i < indices.length; i++) {
      values[i] = elements.get(indices[i]);

    }
    toComparableArray(values);

    Arrays.sort(values);
    return values;
  }

  //=================
  // Private Methods
  //=================

  private void toComparableArray(Object[] array) {
    for (int i = 0; i < array.length; i++) {
      if (type == ColumnTypes.CHAR_ARRAY || type == ColumnTypes.BYTE_ARRAY) {
        array[i] = SparseStringColumn.toStringObject(array[i]);

      }
    }
  }

  private Object[] getValues(int[] validRows) {
    Object[] values = new Object[validRows.length];
    int type = getType();

    for (int i = 0; i < validRows.length; i++) {
      if (type == ColumnTypes.CHAR_ARRAY || type == ColumnTypes.BYTE_ARRAY) {
        values[i] = getString(validRows[i]);
      }
      else {
        values[i] = getObject(validRows[i]);

      }
    }
    return values;
  }

}