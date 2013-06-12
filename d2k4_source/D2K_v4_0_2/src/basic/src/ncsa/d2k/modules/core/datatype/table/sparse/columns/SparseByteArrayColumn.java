package ncsa.d2k.modules.core.datatype.table.sparse.columns;

//===============
// Other Imports
//===============

import ncsa.d2k.modules.core.datatype.table.ColumnTypes;
import ncsa.d2k.modules.core.datatype.table.sparse.primitivehash.*;
import ncsa.d2k.modules.core.datatype.table.sparse.*;
import ncsa.d2k.modules.core.datatype.table.basic.Column;

/**
 * Title:        Sparse Table
 * Description:  Sparse Table projects will implement data structures compatible
 * to the interface tree of Table, for sparsely stored data.
 * Copyright:    Copyright (c) 2002
 * Company:      ncsa
 * @author vered goren
 * @version 1.0
 */

public class SparseByteArrayColumn
    extends SparseObjectColumn {

  //================
  // Constructor(s)
  //================

  /**
   * Creates a new <code>SparseByteArrayColumn</code> with capacity sero and a default
   * load factor
   */
  public SparseByteArrayColumn() {
    this(0);
  }

  /**
   * Creates a new <code>SparseByteArrayColumn</code> with <code>initialCapacity</code>
   *  capacity and a default load factor
   */
  public SparseByteArrayColumn(int initialCapacity) {
    super(initialCapacity);
    type = ColumnTypes.BYTE_ARRAY;
  }

  /**
   * each value data[i] is set to validRows[i].
   * if validRows is smaller than data, the rest of the
   * values in data are being inserted to the end of this column
   *
   * @param data     a byte[] array that holds the values to be inserted
   *                 into this column
   * @param validRows  the indices to be valid in this column
   */
  public SparseByteArrayColumn(byte[][] data, int[] validRows) {
    super(data, validRows);
    type = ColumnTypes.BYTE_ARRAY;
  }

  /**
       * Creates a new <code>SparseByteArrayColumn</code> with a capacity equals to the
   * size of <code>data</code> and a default load factor.
   * The valid row numbers will be zero through size of <code>data</code>.
       * This is just to make this sparse column compatible to the behavior of other
   * regular columns.
   */

  public SparseByteArrayColumn(byte[][] data) {
    super(data);
    type = ColumnTypes.BYTE_ARRAY;
  }

  /**
       * This costructor is used mainly when utilizing <code>SparseObjectColumn</code>
   * methods. because a <code> SparseByteArrayColumn</code> is the same as a
   * <code>SparseObjectColumn</code> as far as data storing, this sub calss
   * uses its methods for duplicating, reordering etc.
   */
  protected SparseByteArrayColumn(SparseObjectColumn column) {
    copy(column);
    type = ColumnTypes.BYTE_ARRAY;
  }

  //================
  // Public Methods
  //================


  /**
   * performs a deep copy of this SparseByteArrayColumne
   * returns an exact copy of this SparseByteArrayColumn
   *
   * the super class copy method is called, which returns a <code>SparseObjectColumn</code>
       * object, then the suitable constructor is called in order to return a sparse
   * byte array column.
   *
       * @return Column object which is actually a SparseByteArrayColumn, that holds
   * the data this column has
   **/
  public Column copy() {
    SparseByteArrayColumn retVal = new SparseByteArrayColumn( (
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
       * @return a SparseByteArrayColumn ordered according to <code>newOrder</code>.
   */

  public Column reorderRows(VIntIntHashMap newOrder) {
    SparseByteArrayColumn retVal = new SparseByteArrayColumn(
        (SparseObjectColumn)super.reorderRows(newOrder));

    return retVal;
  }

  /**
    Converts <code>newEntry</code> into a String and calls setString method, to
    set the new value at row #<code> pos</code>.
   @param newEntry the new item
   @param pos the position
   */
  public void setBoolean(boolean newEntry, int pos) {
    setString(new Boolean(newEntry).toString(), pos);
  }

  /**
   Converts newEntry to byte[] and stores the array at <code>pos</code>
   @param newEntry the new item
   @param pos the position to store newEntry
   */
  public void setByte(byte newEntry, int pos) {
    byte[] b = new byte[1];
    b[0] = newEntry;
    setBytes(b, pos);
  }

  /**
        Returns the entry at <code>pos</code>
        @param pos the position of the entry in the column
        @return the entry at <code>pos</code>
   */
  public byte[] getBytes(int pos) {
    return (byte[]) elements.get(pos);
  }

  /**
        Sets <code>newEntry</code> to row #<code>pos</code>
        @param newEntry the new item
        @param pos the position to store newEntry
   */
  public void setBytes(byte[] newEntry, int pos) {
    elements.remove(pos);
    elements.put(pos, newEntry);
  }

  /**
       Set the entry at pos to be a byte array that holds <code>newEntry</code>
        @param newEntry the new item
        @param pos the position
   */
  public void setChar(char newEntry, int pos) {
    char[] c = new char[1];
    c[0] = newEntry;
    setChars(c, pos);
  }

  /**
   * Returns a SparseByteArrayColumn  that holds only the data from rows <code>
   * pos</code> through <code>pos+len</code>
   * @param pos   the row number which is the beginning of the subset
   * @param len   number of consequetive rows after <code>pos</code> that are
   *              to be included in the subset.
       * @return      a SparseByteArrayColumn  with the data from rows <code>pos</code>
   *              through <code>pos+len</code>
   */
  public Column getSubset(int pos, int len) {
    SparseByteArrayColumn subCol = new SparseByteArrayColumn(
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
    SparseByteArrayColumn retVal = new SparseByteArrayColumn(indices.length);
    for (int i = 0; i < indices.length; i++) {
      if (elements.containsKey(indices[i])) {

        //XIAOLEI
        //retVal.setBytes(getBytes(indices[i]), indices[i]);
        retVal.setBytes(getBytes(indices[i]), i);

      }
    }
    super.getSubset(retVal, indices);

    return retVal;
  }

  /**
        Stores newEntry as a byte[].  If the object is a char[] or byte[],
        the appropriate method is called, otherwise setString() is called
        with newEntry.toString()
        @param newEntry the new item
        @param pos the position
   */
  public void setObject(Object newEntry, int pos) {
    setBytes(toByteArray(newEntry), pos);
  }

  /**
   * Constructs a byte array from <code>obj</code> and returns it:
   * #  If obj is a byte array or null - returns it.
   * # If obj is a Byte constructing a byte array from it.
   * #  Otherwise: construct a String from obj, and return String's call
   *            getBytes method.
   */
  public static byte[] toByteArray(Object obj) {
    if (obj == null) {
      return SparseDefaultValues.getDefaultBytes();
    }

    if (obj instanceof byte[]) {
      return (byte[]) obj;
    }

    if (obj instanceof Byte) {
      byte[] retVal = new byte[1];
      retVal[0] = ( (Byte) obj).byteValue();
      return retVal;
    }

    String str;
    if (obj instanceof char[]) {
      str = new String( (char[]) obj);
    }
    else {
      str = obj.toString();

    }
    return str.getBytes();
  }

  /**
   * Returns the internal representation of this column.
   *
   */
  public Object getInternal() {
    int max_index = -1;
    byte[][] internal = null;
    int[] keys = elements.keys();

    for (int i = 0; i < keys.length; i++) {
      if (keys[i] > max_index) {
        max_index = keys[i];
      }
    }

    internal = new byte[max_index + 1][];

    for (int i = 0; i < max_index + 1; i++) {
      internal[i] = SparseDefaultValues.getDefaultBytes();
    }

    for (int i = 0; i < keys.length; i++) {
      internal[keys[i]] = (byte[]) elements.get(keys[i]);
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
      Converts <code>newEntry</code> into a String and calls setString method, to
      set the new value at row #<code> pos</code>.
     @param newEntry the new item
     @param pos the position to store newEntry
  *
    public void setDouble (double newEntry, int pos) {
        setString(new Double(newEntry).toString(), pos);
    }
      /**
       Converts <code>newEntry</code> into a String and calls setString method, to
       set the new value at row #<code> pos</code>.
      @param newEntry the new item
      @param pos the position to store newEntry
   *
     public void setFloat (float newEntry, int pos) {
      setString(new Float(newEntry).toString(), pos);
     }
       /**
        Converts <code>newEntry</code> into a String and calls setString method, to
        set the new value at row #<code> pos</code>.
       @param newEntry the new item
       @param pos the position to store newEntry
    *
      public void setInt (int newEntry, int pos) {
         setString(new Integer(newEntry).toString(), pos);
      }
     /**
         Converts <code>newEntry</code> into a String and calls setString method, to
         set the new value at row #<code> pos</code>.
        @param newEntry the new item
        @param pos the position to store newEntry
     *
       public void setLong (long newEntry, int pos) {
         setString(new Long(newEntry).toString(), pos);
       }
        /**
         Converts newEntry to a String and stores it as a byte[].
         @param newEntry the new item
         @param pos the position to store newEntry
      *
        public void setShort (short newEntry, int pos) {
             setString(new Short(newEntry).toString(), pos);
        }
         /**
           Converts <code>newEntry</code> into a String and calls setString method, to
           set the new value at row #<code> pos</code>.
          @param newEntry the new item to be stored
          @param pos the position to store the new item
       *
         public void setChars (char[] newEntry, int pos) {
          setString(new String(newEntry), pos);
         }
             /**
           Stores newEntry in this column at pos as a byte[]
           @param newEntry the entry to store
           @param pos the position to put newEntry
        *
          public void setString (String newEntry, int pos) {
              setBytes(newEntry.getBytes(), pos);
          }
            /**
             Uses <code>ByteUtils toBoolean</code> method and returns the boolean value
            associated with the entry at <code>pos</code>
            @param pos the postion
            @return a boolean representation of the entry at pos
         *
           public boolean getBoolean (int pos) {
             //return  Boolean.getBoolean(new String(elements.get(pos)));
             return getString(pos).equalsIgnoreCase("true");
           }
             /**
             Returns the first byte of the entry at <code>pos</code>
             @param pos the position
             @return the first byte of the entry at <code>pos</code>
          *
            public byte getByte (int pos) {
                return getBytes(pos)[0];
            }
               /**
               Converts the entry at <code>pos</code> to a char array and return its first
              char.
              @param pos the index of the entry
              @return the char associated with the entry at pos
           *
             public char getChar (int pos) {
            return getChars(pos)[0];
             }
                /**
               Returns the entry at <code>pos</code> as a char array
               @param pos the index of the entry to be returned
               @return the entry at pos as a char array
            *
              public char[] getChars (int pos) {
             return getString(pos).toCharArray();
              }
            */
