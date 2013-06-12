package ncsa.d2k.modules.core.datatype.table.sparse.columns;

//===============
// Other Imports
//===============

import ncsa.d2k.modules.core.datatype.table.ColumnTypes;
import ncsa.d2k.modules.core.datatype.table.util.ByteUtils;
import ncsa.d2k.modules.core.datatype.table.sparse.primitivehash.*;
import ncsa.d2k.modules.core.datatype.table.basic.Column;
import ncsa.d2k.modules.core.datatype.table.sparse.*;

/**
 * Title:        Sparse Table
 * Description:  Sparse Table projects will implement data structures compatible to the interface tree of Table, for sparsely stored data.
 * Copyright:    Copyright (c) 2002
 * Company:      ncsa
 * @author vered goren
 * @version 1.0
 */

public class SparseCharArrayColumn
    extends SparseObjectColumn {
  /**
   * SparseCharArrayColumn is a type of a SparseObjectColumn. The data (of type
       * char[]) is stored in an int to Object hash map. The differences between a char
   * array column and an object column are concerning the get and set methods.
   */

  //================
  // Constructor(s)
  //================

  /**
   * Creates a new <code>SparseCharArrayColumn</code> with capacity sero and a default
   * load factor
   */
  public SparseCharArrayColumn() {
    this(0);
  }

  /**
   * Creates a new <code>SparseCharArrayColumn</code> with <code>initialCapacity</code>
   *  capacity and a default load factor
   */
  public SparseCharArrayColumn(int initialCapacity) {
    super(initialCapacity);
    type = ColumnTypes.CHAR_ARRAY;
  }

  /**
       * Creates a new <code>SparseCharArrayColumn</code> with a capacity equals to the
   * size of <code>data</code> and a default load factor.
   * The valid row numbers will be zero through size of <code>data</code>.
       * This is just to make this sparse column compatible to the behavior of other
   * regular columns.
   */
  public SparseCharArrayColumn(char[][] data) {
    super(data);
    type = ColumnTypes.CHAR_ARRAY;
  }

  /**
   * each value data[i] is set to validRows[i].
   * if validRows is smaller than data, the rest of the
   * values in data are being inserted to the end of this column
   *
   * @param data     a char[] array that holds the values to be inserted
   *                 into this column
   * @param validRows  the indices to be valid in this column
   */
  public SparseCharArrayColumn(char[][] data, int[] validRows) {
    super(data, validRows);
    type = ColumnTypes.CHAR_ARRAY;
  }

  /**
       * This costructor is used mainly when utilizing <code>SparseObjectColumn</code>
   * methods. because a <code> SparseCharArrayColumn</code> is the same as a
   * <code>SparseObjectColumn</code> as far as class variables, this sub calss
   * uses its methods for duplicating, reordering etc.
   */
  protected SparseCharArrayColumn(SparseObjectColumn column) {
    type = ColumnTypes.CHAR_ARRAY;
    copy(column);
  }

  //================
  // Public Methods
  //================

  /**
   * performs a deep copy of this SparseCharArrayColumn
   * returns an exact copy of this SparseCharArrayColumn
   *
   * the super class copy method is called, which returns a <code>SparseObjectColumn</code>
       * object, then the suitable constructor is called in order to return a sparse
   * char array column.
   *
   *
       * @return Column object which is actually a SparseCharArrayColumn, that holds
   * the data this column has
   **/
  public Column copy() {
    SparseCharArrayColumn retVal = new SparseCharArrayColumn( (
        SparseObjectColumn)super.copy());
    return retVal;
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
       * @return a SparseCharArrayColumn ordered according to <code>newOrder</code>.
   */

  public Column reorderRows(VIntIntHashMap newOrder) {
    SparseCharArrayColumn retVal = new SparseCharArrayColumn(
        (SparseObjectColumn)super.reorderRows(newOrder));
    return retVal;
  }

  /**
        Calls the toString() method on newEntry to get a String and
        stores the String as a char[].
        @param newEntry the new item
        @param pos the position
   */
  public void setBoolean(boolean newEntry, int pos) {
    setChars(new Boolean(newEntry).toString().toCharArray(), pos);
  }

  /**
        Converts newEntry to char[]
        @param newEntry the new item
        @param pos the position to store newEntry
   */
  public void setByte(byte newEntry, int pos) {
    // byte[] b = new byte[1];
    //b[0] = newEntry;
    //setBytes(b, pos);
    setString(new Byte(newEntry).toString(), pos);
  }

  /**
        Converts newEntry to char[] by calling ByteUtils.toChars()
        @param newEntry the new item
        @param pos the position to store newEntry
   */
  public void setBytes(byte[] newEntry, int pos) {
    setString(new String(newEntry), pos);
  }

  /**
       Set the entry at pos to be a char array that holds <code>newEntry</code>
        @param newEntry the new item
        @param pos the position
   */
  public void setChar(char newEntry, int pos) {
    char[] c = new char[1];
    c[0] = newEntry;
    setChars(c, pos);
  }

  /**
        Returns the entry at <code>pos</code>
        @param pos the index
        @return the entry at pos
   */
  public char[] getChars(int pos) {
    return (char[]) elements.get(pos);
  }

  /**
   * Returns a SparseCharArrayColumn that holds only the data from rows <code>
   * pos</code> through <code>pos+len</code>
   * @param pos   the row number which is the beginning of the subset
   * @param len   number of consequetive rows after <code>pos</code> that are
   *              to be included in the subset.
       * @return      a SparseCharArrayColumn  with the data from rows <code>pos</code>
   *              through <code>pos+len</code>
   */
  public Column getSubset(int pos, int len) {
    SparseCharArrayColumn subCol = new SparseCharArrayColumn(
        (SparseObjectColumn)super.getSubset(pos, len));

    return subCol;

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
    SparseCharArrayColumn retVal = new SparseCharArrayColumn(indices.length);
    for (int i = 0; i < indices.length; i++) {
      if (elements.containsKey(indices[i])) {

        //XIAOLEI
        //retVal.setChars(getChars(indices[i]), indices[i]);
        retVal.setChars(getChars(indices[i]), i);

      }
    }
    super.getSubset(retVal, indices);

    return retVal;
  }

  /**
        Set the entry at pos to be newEntry
        @param newEntry the new item
        @param pos the position
   */
  public void setChars(char[] newEntry, int pos) {
    elements.remove(pos);
    elements.put(pos, newEntry);
  }

  /**
        Converts newEntry to a String and stores it as a char[].
        @param newEntry the new item
        @param pos the position to store newEntry
   */
  public void setDouble(double newEntry, int pos) {
    setChars(Double.toString(newEntry).toCharArray(), pos);
  }

  /**
        Converts newEntry to a String and stores it as a char[].
        @param newEntry the new item
        @param pos the position to store newEntry
   */
  public void setFloat(float newEntry, int pos) {
    setChars(Float.toString(newEntry).toCharArray(), pos);
  }

  /**
        Converts newEntry to a String and stores it as a char[].
        @param newEntry the new item
        @param pos the position to store newEntry
   */
  public void setInt(int newEntry, int pos) {
    setChars(Integer.toString(newEntry).toCharArray(), pos);
  }

  /**
        Converts newEntry to a String and stores it as a char[].
        @param newEntry the new item
        @param pos the position to store newEntry
   */
  public void setLong(long newEntry, int pos) {
    setChars(Long.toString(newEntry).toCharArray(), pos);
  }

  /**
        Stores newEntry as a char[].  If the object is a char[] or byte[],
        the appropriate method is called, otherwise setString() is called
        with newEntry.toString()
        @param newEntry the new item
        @param pos the position
   */
  public void setObject(Object newEntry, int pos) {
    setChars(toCharArray(newEntry), pos);
  }

  /**
        Sets the entry at the given position to newEntry
        @param newEntry a new entry
        @param pos the position to set
   */
  public void setRow(Object newEntry, int pos) {
    setObject(newEntry, pos);
  }

  /**
   Converts newEntry to a String and stores it as a char[].
   @param newEntry the new item
   @param pos the position to store newEntry
   */
  public void setShort(short newEntry, int pos) {
    setChars(Short.toString(newEntry).toCharArray(), pos);
  }

  /**
   Stores newEntry in this column at pos as a char[]
   @param newEntry the entry to store
   @param pos the position to put newEntry
   */
  public void setString(String newEntry, int pos) {
    setChars(newEntry.toCharArray(), pos);
  }

  public static char[] toCharArray(Object obj) {

    if (obj == null) {
      return SparseDefaultValues.getDefaultChars();
    }

    if (obj instanceof char[]) {
      return (char[]) obj;
    }

    String str;
    if (obj instanceof byte[]) {
      str = new String( (byte[]) obj);

      //covers cases of Boolean, Character, Number, String.
    }
    else {
      str = obj.toString();

    }
    return str.toCharArray();
  }

  /**
   * Returns the internal representation of this column.
   *
   */
  public Object getInternal() {
    int max_index = -1;
    char[][] internal = null;
    int[] keys = elements.keys();

    for (int i = 0; i < keys.length; i++) {
      if (keys[i] > max_index) {
        max_index = keys[i];
      }
    }

    internal = new char[max_index + 1][];
    for (int i = 0; i < max_index + 1; i++) {
      internal[i] = SparseDefaultValues.getDefaultChars();
    }

    for (int i = 0; i < keys.length; i++) {
      internal[keys[i]] = (char[]) elements.get(keys[i]);
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
}
/*
 /**
      Converts the entry at pos to a String and returns the boolean value
      of a Boolean object constructed from the String.
      @param pos the postion
      @return a boolean representation of the entry at pos
  *
     public boolean getBoolean (int pos) {
       return  getString(pos).equalsIgnoreCase("true");
     }
      /**
       Creates a String from the entry at pos and returns its first byte
       @param pos the position
       @return the first byte of the byte[] representation of the entry at <code>pos</code>
   *
      public byte getByte (int pos) {
         // return getBytes(pos)[0];
         return Byte.parseByte(getString(pos));
      }
       /**
        Creates a String from the entry at pos and returns it as a byte[].
        @param pos the position
        @return a byte[] representation of the entry at <code>pos</code>
    *
       public byte[] getBytes (int pos) {
           return getString(pos).getBytes();
       }
         /**
         returns the first char of the entry at <code>pos</code>.
         @param pos the index of the entry
         @return the first char of the entry at <code>pos</code>.
     *
        public char getChar (int pos) {
     return getChars(pos)[0];
        }
          /**
      * Returns the value at row # row, represented as  a String.
      * @param row the row number
      * @return a String Object representing the value at row # row.
      *
         public String getString(int row)  {
           return new String((char[])elements.get(row));
         }
      *
      */
