package ncsa.d2k.modules.core.datatype.table.sparse.columns;

//===============
// Other Imports
//===============

import ncsa.d2k.modules.core.datatype.table.sparse.primitivehash.*;
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

public class SparseStringObjectColumn
    extends SparseObjectColumn {

  /**
   * SparseStringObjectColumn is a column in a SparseTable that holds data of Type String.
   * The internal representation of the data is via an int to Object hashmap.
   * the keys (int) are the row numbers. the values (Objects that are actually
   * Strings) are the data stored at wach row.
   */

  //================
  // Constructor(s)
  //================

  /**
   * Creates a new <code>SparseStringObjectColumn </code> instance with the
   * capacity zero and default load factor.
   */
  public SparseStringObjectColumn() {
    this(0);
  }

  /**
   * Creates a new <code>SparseStringObjectColumn </code> instance with a prime
   * capacity equal to or greater than <tt>initialCapacity</tt> and
   * with the default load factor.
   *
   * @param initialCapacity an <code>int</code> value
   */
  public SparseStringObjectColumn(int initialCapacity) {
    super(initialCapacity);
    type = ColumnTypes.STRING;
  }

  /**
   * Creates a new <code>SparseStringObjectColumn</code> instance that will hold the
   * data in the <code>data</code> array. the elements in <code>data</code> are
   * being stored in <code>elements</code> in rows 0 through the size of
   * <code>data</code>.
   *
       * this is just to comply with regular column objects that have this constructor.
   * because this is a sparse column it is unlikely to be used.
   */

  public SparseStringObjectColumn(String[] data) {
    super(data);
    type = ColumnTypes.STRING;
  }

  public SparseStringObjectColumn(String[] data, int[] validRows) {
    super(data, validRows);
    type = ColumnTypes.STRING;
  }

  /**
   * Becuase sparse string column differs from sparse object column by behavior
   * but not by vairables - this constructor is used when calling the super
   * methods explicitly.
   */
  protected SparseStringObjectColumn(SparseObjectColumn column) {

    copy(column);

    type = ColumnTypes.STRING;
  }

  protected SparseStringObjectColumn(SparseStringColumn column) {

    int[] keys = column.getIndices();

    for (int i = 0; i < keys.length; i++) {

      setString(column.getString(keys[i]), keys[i]);
    }
    missing = column.missing.copy();
    empty = column.empty.copy();

    type = ColumnTypes.STRING;
  }

  //================
  // Public Methods
  //================

  /**
   * performs a deep copy of this SparseStringObjectColumn
   * returns an exact copy of this SparseStringObjectColumn
   * uses the super class copy method to construct the returned value
   * @return Column object which is actually a deep copy of this
   * SparseStringObjectColumn object.
   **/
  public Column copy() {
    SparseStringObjectColumn retVal = new SparseStringObjectColumn( (
        SparseObjectColumn)super.copy());
    return retVal;
  }

  /**
       * Returns a SparseStringObjectColumn that holds only the data from rows <code>
   * pos</code> through <code>pos+len</code>
   * @param pos   the row number which is the beginning of the subset
   * @param len   number of consequetive rows after <code>pos</code> that are
   *              to be included in the subset.
   * @return      a SparseStringObjectColumn with the data from rows <code>pos</code>
   *              through <code>pos+len</code>
   */
  public Column getSubset(int pos, int len) {
    SparseStringObjectColumn subCol = new SparseStringObjectColumn(
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
    SparseStringObjectColumn retVal = new SparseStringObjectColumn(indices.
        length);
    for (int i = 0; i < indices.length; i++) {
      if (elements.containsKey(indices[i])) {

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
   * Converts <code>obj</code> into a String.
   *
   * @param obj    an Object to be converted into a String
   * @return       a String representing the data <code>obj</code> holds.
   *               # If obj equals null returns null
       *               # If obj is a char or byte array - construct a string from it.
   *               # Otherwise - returns obj.toString.
   *
      public static String toStringObject(Object obj){
   if(obj == null)
     return null;
   if(obj instanceof char[])
    return new String ((char[]) obj);
   if(obj instanceof byte[])
     return new String ((byte[]) obj);
   if (obj instanceof Character){
     char[] arr = { ((Character)obj).charValue() };
     return new String(arr);
   }
   //covers cases of  Number, Boolean
   return obj.toString();
      }
   */

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
  public void setString(String s, int row) {
    elements.remove(row);
    elements.put(row, s);
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
       * @return a SparseStringObjectColumn ordered according to <code>newOrder</code>.
   */

  public Column reorderRows(VIntIntHashMap newOrder) {
    SparseStringObjectColumn retVal = new SparseStringObjectColumn(
        (SparseObjectColumn)super.reorderRows(newOrder));

    return retVal;
  }

}
/*
 /**
  * Swaps the values between rows <code>pos1</code> and <code>pos2</code>.
  *
  *
     public void swapRows(int pos1, int pos2){
      //retrieving the stored values
      String str1 = (String)elements.remove(pos1);
      String str2 = (String)elements.remove(pos2);
      //note that if no value is stored at any position then str is null.
      //we would like to store str at the new position only if it isn't null.
      if(str2 != null)
        elements.put(pos1, str2);
      if(str1 != null)
        elements.put(pos2, str1);
     }
   /**
   * Reorders the data stored in this column in a new column.
   * Does not change this column.
   *
   * @param newOrder - an int array, which its elements are valid row numbers
   * in this column.
   *
        public Column reorderRows(int[] newOrder) {
          int[] oldOrder = elements.keys();
          Arrays.sort(oldOrder);      //the old order of valid rows.
          //a map to hold the data in the new order
          VIntObjectHashMap tempMap = new VIntObjectHashMap(elements.size());
          //each element that is mapped to newOrder[i] gets the row number at oldOrder[i]
          for (int i=0; i<newOrder.length; i++)
     tempMap.put(oldOrder[i], elements.get(newOrder[i]));
          SparseStringObjectColumn  retVal = new SparseStringObjectColumn ();   //the returned value
          super.copy(retVal);                       //copying general data
          retVal.elements = tempMap;                //setting the data map
          return retVal;
        }
   * Returns <code>true</code> if and only if the <code>String</code> at
   * row # <code>row</cdoe> is "true". otherwise return false.
   *
   * @param row - the row number from which the data is retrieved
        public boolean getBoolean(int row)  {
           return Boolean.valueOf(getString(row)).booleanValue();
        }
   **
       * Calls <code>getString</code> method and returns the first <code>char</code>
   * of its returned value.
   *
   * @param row - the row number from which the data is retrieved
   *
        public char getChar(int row) {
           return getString(row).toCharArray()[0];
        }
   * Calls <code>getString</code> method and returns its returned value as a
   * char array.
   *
   * @param row - the row number from which the data is retrieved
        public char[] getChars(int row)  {
           return getString(row).toCharArray();
        }
   **
       * Attempts to parse a double value from the data stored in row #<code>row</code>
   *
   * @param row - the row number from which the data is retrieved
   *
        public double getDouble(int row){
            return Double.parseDouble(getString(row));
        }
   *
       * Attempts to parse a float value from the data stored in row #<code>row</code>
   *
   * @param row - the row number from which the data is retrieved
   *
         public float getFloat(int row) {
          return Float.parseFloat(getString(row));
       }
   **
       * Attempts to parse an int value from the data stored in row #<code>row</code>
   *
   * @param row - the row number from which the data is retrieved
   *
       public int getInt(int row) {
          return Integer.parseInt(getString(row));
       }
   **
       * Attempts to parse a long value from the data stored in row #<code>row</code>
   *
   * @param row - the row number from which the data is retrieved
   *
        public long getLong(int row) {
          return Long.parseLong(getString(row));
       }
   **
   * Returns the String that is stroed at row #<code>row</code>
   *
   * @param row - the row number from which the data is retrieved
   *
         public Object getObject(int row) {
          return elements.get(row);
       }
   **
       * Attempts to parse a short value from the data stored in row #<code>row</code>
   *
   * @param row - the row number from which the data is retrieved
   *
         public short getShort(int row) {
          return Short.parseShort(getString(row));
       }
          /**
    * Returns the total number of data items that this column holds.
    *
          public int getNumEntries () {
            return elements.size();
          }
    **
    * Returns the maximal row number in this column.
    * The counting starts at zero
    *
         public int getNumRows(){
          return ColumnService.getNumRows(elements);
         }
    **
    * Returns the String at row #<code>row</code>
    * @param row - the row number from which the data is retrieved.
    *
         public Object getRow(int row){
          return elements.get(row);
         }
            /**
     * Returns the String that is stroed at row #<code>row</code>
     *
     * @param row - the row number from which the data is retrieved
     *
           public String getString(int row){
            return (String)elements.get(row);
           }
           /**
          * Adds another element to this column, at row proceeding the maximal valid
      * row number.
      * @param obj - the element to be added
      *
             public void addRow(Object obj){
              elements.put(getNumRows(), obj.toString());
             }
              /**
       * Returns a SparseStringObjectColumn with the data stored at this column from row
       * #<code>pos</code> till row #<code>pos+len</code>
           * note that the actual number of elements that will be stored in the returned
       * column is likely to be smaller than <code>len</code>
       *
               public Column getSubset(int pos, int len){
                //the map to hold the data of the new column
                 VIntObjectHashMap tempMap = new VIntObjectHashMap(len);
                 //for each String from pos till pos+len
                 for (int i=0; i<len; i++)
                  //if a value is mapped to current inspected row number
                  if(elements.containsKey(pos+i))
//put it in the new map
             tempMap.put(pos+i, getString(pos+i));
                SparseStringObjectColumn subCol = new SparseStringObjectColumn();   //the returned value
                super.copy(subCol);     //copying general attributes
           subCol.elements = tempMap;    //linking the data to the returned value
                 return subCol;
               }
               /**
            * insert the string represented by <code>obj</code> at row # <code>pos</code>.
            * if row # <code>pos</code> is occupied - moving its item to row # pos+1.
        * and so on.
        *
                 public void insertRow(Object obj, int pos){
            String str = (String)elements.remove(pos);    //retrieving the value at row #pos
            elements.put(pos, obj.toString());          //putting the new value at row #pos
                  //if there was a value mapped to pos
                    if (str!=null)
//recursively storing the previous value at pos+1
                      insertRow(str, pos+1);
                 }
                 /**
             * removes a row from this column and returns the removed item as an object
         * @param row - the row number to be removed.
         * @return - the removed object.
         *
                   public Object removeRow(int row)
                   {
                      return elements.remove(row);
                   }
                   /**
          * Removes all elements stored at rows #<code>pos</code> through
          * <code>pos+len</code>
          *
          * @param pos - the row number from which to begin removing data
              * @param len - number of consequitive rows to remove after row #<code>pos</code>
          *
                     public void removeRows(int pos, int len){
                      ColumnService.removeRows(pos, len, elements);
                     }
                     /**
           * Removes entries from this column according to a set of flags.
               * @param flags - a boolen array. if element i in flags is true then row # i
               * is being removed. if flags is smaller than the capacity of this column -
               * removing the rest of the rows that their number is higher than the length
           * of flags.
           *
                       public void removeRowsByFlag (boolean[] flags) {
                        ColumnService.removeRowsByFlag(flags, elements);
                       }
                       /**
                * Removes entries at this column according to indices given in an int array.
            * @param indices - holds indices of entries to be removed.
            *
                         public void removeRowsByIndex (int[] indices) {
                       ColumnService.removeRowsByIndex(indices, elements);
                          }
                          /**
             * Compares element from row # r1 to the one in row # r2.
             * @param r1 - row number fo first element to be compared
             * @param r2 - row number of second element to be compared
                 * @return - int value, 0 if both strings are identical, negative number if
                 * string at row r1 is lexicographically smaller than the string at row r2,
                 * positive number if the string at row r1 is lexicographically bigger than
             * the string at row r2.
             *
                           public int compareRows(int r1, int r2) {
                              return getString(r1).compareTo(getString(r2));
                           }
                         /**
                  * Compares the string represented by element with the one in row # row.
                  * @param element - a string (or an object representing a string) to be compared.
              * @param row - the row number of the string to be compared to <code>element</code>
                  * @return - int value, 0 if both strings are identical, negative number if
                  * the string represented by <code>element</code> is lexicographically smaller
                  * than the string at row # <code>row</code>, positive number if the string
                  * represented by <code>element</code> is lexicographically bigger than
              * the string at row # <code>row</code>.
              *
                             public int compareRows(Object element, int row){
                  return element.toString().compareTo(getString(row));
                             }
                           /**
               * performs a deep copy of this SparseStringObjectColumn
               * returns an exact copy of this SparseStringObjectColumn
               *
                               public Column copy()
                               {
                                SparseStringObjectColumn retVal;
                                try {
                   ByteArrayOutputStream baos = new ByteArrayOutputStream();
                   ObjectOutputStream oos = new ObjectOutputStream(baos);
                                    oos.writeObject(this);
                                    byte buf[] = baos.toByteArray();
                                    oos.close();
                   ByteArrayInputStream bais = new ByteArrayInputStream(buf);
                   ObjectInputStream ois = new ObjectInputStream(bais);
                   retVal = (SparseStringObjectColumn)ois.readObject();
                                    ois.close();
                                    return  retVal;
                                } catch (Exception e) {
                                  retVal = new SparseStringObjectColumn();
                                  super.copy(retVal);
                                  retVal.elements = elements.copy();
                                  return retVal;
                                }
                               }
                             /**
                * adjust number of rows in this column
                    * removes all the rows that their number is greater than <coe>newNumRows</code>.
                *
                * @param newNumRows - a new upper bound for this column.
                *
                                 public void setNumRows (int newCapacity) {
                    ColumnService.setNumRows(newCapacity, elements);
                                  }
                                  /**
                     * sorts the elements in this column so that the rows that were originally
                 * valid remain valid after sorting
                 *
                                   public void sort(){
                                    //sorting the valid row numbers
                                    int[] validRows = elements.keys();
                                    Arrays.sort(validRows);
                                    //sorting the values
                     String[] values = (String[])elements.getValues();
                                    Arrays.sort(values);
                                    //this temp hash map will hold the new order
                     VIntObjectHashMap tempMap = new VIntObjectHashMap(elements.size());
                                    //for each index in validRows we have used, this index will be marked true
                                    //in this boolean array.
                     boolean[] ocuupiedIndices = new boolean[validRows.length];
                                    String currVal;
                                    //for each valid row validRows[i]
                                    for (int i=0; i<validRows.length; i++){
                     currVal =  (String)elements.get(validRows[i]);
                                      //finding the index of its mapped String in values
                     int newRow = Arrays.binarySearch(values, currVal);
                                      //because binarySearch can return the same index for items that are identical
                                      //checking for this option too.
                                      if(ocuupiedIndices[newRow])
                     newRow = SparseObjectColumn.getNewRow(currVal, values, newRow, ocuupiedIndices);
                                      ocuupiedIndices[newRow] = true;
                                      //the new valid row number for the mapped string, is the value stored at
                                      //index newRow in validValues
                                      tempMap.put(validRows[newRow], currVal);
                                    }
                                    //now elements is sorted.
                                    elements = tempMap;
                                   }
                                 /**
                      * todo: implement sort(MutableTable) and sort(MutableTable, int, int)
                  *
                                          public void sort(MutableTable t){}
                      public void sort(MutableTable t, int a, int b){}
                                        /**
                       * Calls <code>getString</code> method and returns the first <code>byte</code>
                   * of its returned value.
                   *
                       * @param row - the row number from which the data is retrieved
                       * @return      the first byte in the bytes representation of the String at
                   *              row #<code>row</code>
                   *
                                        public byte getByte(int row){
                                           return getString(row).getBytes()[0];
                                        }
                                         /**
                        * Calls <code>getString</code> method and returns its returned value as a
                    * byte array.
                    *
                        * @param row - the row number from which the data is retrieved
                    * @return      a byte array representation of the String at row #<code>row</code>
                    *
                                          public byte[] getBytes(int row){
                                             return getString(row).getBytes();
                                          }
*/