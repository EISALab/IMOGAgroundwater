package ncsa.d2k.modules.core.datatype.table.basic;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.util.TableUtilities;

import java.io.*;
import java.util.*;

/**
 StringObjectColumn is an implementation of TextualColumn which stores
 textual data in a String form.
 <br>
 It it optimized for: retrieval of words by index, compact representation
 of words,  swapping of words, setting of words by index, reOrder-ing by index,
 compare-ing of words
 <br>
 It is inefficient for: removals, insertions, searching(on contents of word),
 <br>
 */
final public class StringObjectColumn extends MissingValuesColumn implements TextualColumn {

	static final long serialVersionUID = 7592751696371096295L;

    /** the internal representation of this Column */
    private String[] internal = null;
	private boolean[] empty = null;
    /**
     Create a new empty StringObjectColumn
     */
    public StringObjectColumn () {
        this(0);
    }

    /**
     Create a StringObjectColumn with the specified capacity
     @param capacity the initial capacity
     */
    public StringObjectColumn (int capacity) {
        internal = new String[capacity];
      type = ColumnTypes.STRING;
      setIsNominal(true);
      missing = new boolean[internal.length];
      empty = new boolean[internal.length];
      for(int i = 0; i < internal.length; i++) {
          missing[i] = false;
          empty[i] = false;
		}
    }

    /**
     Create a StringObjectColumn with the specified values
     @param newInternal the initial data
     */
    public StringObjectColumn (String[] newInternal) {
        this.setInternal(newInternal);
      type = ColumnTypes.STRING;
      setIsNominal(true);
      missing = new boolean[internal.length];
      empty = new boolean[internal.length];
      for(int i = 0; i < internal.length; i++) {
          missing[i] = false;
          empty[i] = false;
		}
    }

    private StringObjectColumn(String[] newInternal, boolean[] miss, boolean[] emp,
                               String lbl, String comm) {
        this.setInternal(newInternal);
        type = ColumnTypes.STRING;
        setIsNominal(true);
		this.setMissingValues(miss);
        empty = emp;
        setLabel(lbl);
        setComment(comm);
    }

    /**
     Copy method. Return an exact copy of this column.  A deep copy
     is attempted, but if it fails a new column will be created,
     initialized with the same data as this column.
     @return A new Column with a copy of the contents of this column.
     */
    public Column copy () {
        StringObjectColumn newCol;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            byte buf[] = baos.toByteArray();
            oos.close();
            ByteArrayInputStream bais = new ByteArrayInputStream(buf);
            ObjectInputStream ois = new ObjectInputStream(bais);
            newCol = (StringObjectColumn)ois.readObject();
            ois.close();
            return  newCol;
        } catch (Exception e) {
            String[] newVals = new String[getNumRows()];
            for (int i = 0; i < getNumRows(); i++) {
                newVals[i] = getString(i);
            }
            boolean[] miss = new boolean[internal.length];
            boolean[] em = new boolean[internal.length];
            for(int i = 0; i < internal.length; i++) {
                miss[i] = missing[i];
                em[i] = empty[i];

            }
            newCol = new StringObjectColumn(newVals, miss, em, getLabel(), getComment());
           return  newCol;
        }
    }
	/**
	 * Add the specified number of blank rows.
	 * @param number number of rows to add.
	 */
	public void addRows (int number) {
		int last = internal.length;
		String[] newInternal = new String[last + number];
		boolean[] newMissing = new boolean[last + number];
		boolean[] newEmpty = new boolean[last + number];

		System.arraycopy(internal, 0, newInternal, 0, last);
		System.arraycopy(missing, 0, newMissing, 0, missing.length);
		System.arraycopy(empty, 0, newEmpty, 0, empty.length);
		internal = newInternal;
		this.setMissingValues(newMissing);
		empty = newEmpty;
	}

   public void trim() {
   }

    /** support TextualColumn */
    //////////////////////////////////////
    //// ACCESSING FIELD ELEMENTS

    /**
     Get a String from this column at pos
     @param pos the position from which to get a String
     @return a String representation of the entry at that position
     */
    public String getString (int pos) {
        return  this.internal[pos];
    }

    /**
     Set the item at pos to be newEntry
     @param newEntry the new item
     @param pos the position
     */
    public void setString (String newEntry, int pos) {
        this.internal[pos] = newEntry;
    }

    /**
     Convert the item at pos to an int using Integer.parseInt()
     @param pos the position
     @return the int value of the item at pos
     */
    public int getInt (int pos) {
        return  Integer.parseInt(internal[pos]);
    }

    /**
     Set the item at pos to be newEntry by using Integer.toString()
     @param newEntry the new item
     @param pos the position
     */
    public void setInt (int newEntry, int pos) {
        internal[pos] = new Integer(newEntry).toString();
    }

    /**
     Convert the item at pos to a short using Short.parseShort()
     @param pos the position
     @return the int value of the item at pos
     */
    public short getShort (int pos) {
        return  Short.parseShort(internal[pos]);
    }

    /**
     Set the item at pos to be newEntry by using Short.toString()
     @param newEntry the new item
     @param pos the position
     */
    public void setShort (short newEntry, int pos) {
        internal[pos] = new Short(newEntry).toString();
    }

    /**
     Set the item at pos to be newEntry by using Long.toString()
     @param newEntry the new item
     @param pos the position
     */
    public void setLong (long newEntry, int pos) {
        internal[pos] = new Long(newEntry).toString();
    }

    /**
     Convert the item at pos to a long using Long.parseLong()
     @param pos the position
     @return the int value of the item at pos
     */
    public long getLong (int pos) {
        return  Long.parseLong(internal[pos]);
    }

    /**
     Convert the item at pos to a double using Double.parseDouble()
     @param pos the position
     @return the int value of the item at pos
     */
    public double getDouble (int pos) {
        return  Double.parseDouble(internal[pos]);
    }

    /**
     Set the item at pos to be newEntry by using Double.toString()
     @param newEntry the new item
     @param pos the position
     */
    public void setDouble (double newEntry, int pos) {
        internal[pos] = new Double(newEntry).toString();
    }

    /**
     Convert the item at pos to a float using Float.parseFloat()
     @param pos the position
     @return the int value of the item at pos
     */
    public float getFloat (int pos) {
        return  Float.parseFloat(internal[pos]);
    }

    /**
     Set the item at pos to be newEntry by using Float.toString()
     @param newEntry the new item
     @param pos the position
     */
    public void setFloat (float newEntry, int pos) {
        internal[pos] = new Float(newEntry).toString();
    }

    /**
     Get the item at pos as a byte[] by calling getBytes()
     @param pos the position
     @return the item as pos as a byte[]
     */
    public byte[] getBytes (int pos) {
        return  this.internal[pos].getBytes();
    }

    /**
     Create a new String from newEntry and store it at pos
     @param newEntry the new item
     @param pos the position
     */
    public void setBytes (byte[] newEntry, int pos) {
        this.internal[pos] = new String(newEntry);
    }

    /**
     Return the item at pos
     @param pos the position
     @return the item at pos
     */
    public Object getObject (int pos) {
        return  internal[pos];
    }

    /**
     Set the item at pos to be newEntry by calling newEntry's toString() method
     @param newEntry the new item
     @param pos the position
     */
    public void setObject (Object newEntry, int pos) {
        internal[pos] = newEntry.toString();
    }

    /**
     Return the item at pos as a char[] by calling the toCharArray() method
     @param pos the position
     @return the item at pos as a char[]
     */
    public char[] getChars (int pos) {
        return  internal[pos].toCharArray();
    }

    /**
     Create a new String from newEntry and store it at pos
     @param newEntry the new item
     @param pos the position
     */
    public void setChars (char[] newEntry, int pos) {
        internal[pos] = new String(newEntry);
    }

    /**
     Convert the entry at pos to a boolean value by using Boolean.valueOf()
     @param pos the position
     @return the item at pos as a boolean value
     */
    public boolean getBoolean (int pos) {
        return  Boolean.valueOf(internal[pos]).booleanValue();
    }

    /**
     Set the item at pos to newEntry by creating a new Boolean and
     storing it as a String.
     @param newEntry the new item
     @param pos the position
     */
    public void setBoolean (boolean newEntry, int pos) {
        internal[pos] = new Boolean(newEntry).toString();
    }

   public byte getByte(int pos) {
      return Byte.parseByte(getString(pos));
      //return getBytes(pos)[0];
   }

   public void setByte(byte b, int pos) {
      /*byte[] ba = new byte[1];
      ba[0] = b;
      setBytes(ba, pos);*/
      this.setString(Byte.toString(b), pos);
   }

   public char getChar(int pos) {
      return getChars(pos)[0];
   }

   public void setChar(char c, int pos) {
      char[] ca = new char[1];
      ca[0] = c;
      setChars(ca, pos);
   }

    //////////////////////////////////////
    //// Accessing Metadata
    /**
     Return the count for the number of non-null entries.
     This variable is recomputed each time...as keeping
     track of it could be very time inefficient.
     @return this StringColumn's number of entries
     */
    public int getNumEntries () {
        int numEntries = 0;
        for (int i = 0; i < internal.length; i++)
            if (internal[i] != null && !isValueMissing(i) && !isValueEmpty(i))
                numEntries++;
        return  numEntries;
		    }

   /**
    * Get the number of rows that this column can hold.  Same as getCapacity
    * @return the number of rows this column can hold
    */
   public int getNumRows() {
      return getCapacity();
   }
    /**
     Get the capacity of this Column, its potential maximum number of entries
     @return the max number of entries this Column can hold
     */
    public int getCapacity () {
        return  internal.length;
    }

    /**
     Suggests a new capacity for this Column.  If this implementation of Column supports
     capacity than the suggestion may be followed. The capacity is it's potential
     max number of entries.  If numEntries > newCapacity then Column may be truncated.
     If internal.length > newCapacity then Column will be truncated.
     @param newCapacity a new capacity
     */
    public void setNumRows(int newCapacity) {
/*        if (internal != null) {
            String[] newInternal = new String[newCapacity];
            if (newCapacity > internal.length)
                newCapacity = internal.length;
            System.arraycopy(internal, 0, newInternal, 0, newCapacity);
            internal = newInternal;
        }
        else
            internal = new String[newCapacity];
        */
        if (internal != null) {
            String[] newInternal = new String[newCapacity];
            boolean[] newMissing = new boolean[newCapacity];
            boolean[] newEmpty = new boolean[newCapacity];
            if (newCapacity > internal.length)
                newCapacity = internal.length;
            System.arraycopy(internal, 0, newInternal, 0, newCapacity);
            System.arraycopy(missing, 0, newMissing, 0, missing.length);
            System.arraycopy(empty, 0, newEmpty, 0, empty.length);
            internal = newInternal;
			this.setMissingValues(newMissing);
            empty = newEmpty;
        }
        else {
            internal = new String[newCapacity];
            missing = new boolean[newCapacity];
            empty = new boolean[newCapacity];
		}

    }

    //////////////////////////////////////
    //////////////////////////////////////
    //// ACCESSING StringColumn ELEMENTS
    /**
     Gets an entry from the Column at the indicated position.
     @param pos the position
     @return the entry at pos
     */
    public Object getRow (int pos) {
        return  this.internal[pos];
    }

    /**
     Gets a subset of this Column, given a start position and length.
     @param pos the start position for the subset
     @param len the length of the subset
     @return a subset of this Column
     */
    public Column getSubset (int pos, int len) {
        String[] subset = new String[len];
        boolean[] newMissing = new boolean[len];
        boolean[] newEmpty = new boolean[len];
        System.arraycopy(internal, pos, subset, 0, len);
        System.arraycopy(missing, pos, newMissing, 0, len);
        System.arraycopy(empty, pos, newEmpty, 0, len);
        StringObjectColumn sc = new StringObjectColumn(subset, newMissing,
                newEmpty, getLabel(), getComment());
        return  sc;
    }

    /**
     * Gets a subset of this <code>Column</code>, given a start position and
     * length. The primitive values are copied, so they have no destructive
     * abilities as far as the <code>Column</code> is concerned.
     *
     * @param pos            the start position for the subset
     * @param len            the length of the subset
     * @return               a subset of this <code>Column</code>
     */
    public Column getSubset (int[] rows) {
        String[] subset = new String[rows.length];
        boolean[] newMissing = new boolean[rows.length];
        boolean[] newEmpty = new boolean[rows.length];
        for(int i = 0; i < rows.length; i++) {
          subset[i] = internal[rows[i]];
          newMissing[i] = missing[rows[i]];
          newEmpty[i] = empty[rows[i]];
        }
        StringObjectColumn bc = new StringObjectColumn(subset, newMissing, newEmpty, getLabel(), getComment());
        return  bc;
    }


    /**
     Gets a reference to the internal representation of this Column.
     @return the internal representation of this Column.
     */
    public Object getInternal () {
        return  this.internal;
    }

    /**
     Sets the reference to the internal representation of this Column.
     If a miscompatable Object is passed in, the most common Exception
     thrown is a ClassCastException.
     @param newInternal a new internal representation for this Column
     */
    public void setInternal (Object newInternal) {
        this.internal = (String[])newInternal;
    }

    /**
     Sets the entry at the given position to newEntry
     @param newEntry a new entry
     @param pos the position to set
     */
    public void setRow (Object newEntry, int pos) {
        this.internal[pos] = (String)newEntry;
    }

    /**
     Adds the new entry to the Column after the last non-null position
     in the Column.
     @param newEntry a new entry
     */
    public void addRow (Object newEntry) {
        int last = internal.length;
        String[] newInternal = new String[internal.length + 1];
        boolean[] newMissing = new boolean[internal.length + 1];
        boolean[] newEmpty = new boolean[internal.length + 1];
        System.arraycopy(internal, 0, newInternal, 0, internal.length);
        System.arraycopy(missing, 0, newMissing, 0, missing.length);
        System.arraycopy(empty, 0, newEmpty, 0, empty.length);
        newInternal[last] = (String)newEntry;
        internal = newInternal;
		this.setMissingValues(newMissing);
		empty = newEmpty;
    }

    /**
     Removes an entry from the Column, at pos.
     All entries from pos+1 will be moved back 1 position
     and the last entry will be set to null;
     @param pos the position to remove
     @return the removed object
     */
    public Object removeRow (int pos) {
        String removed = internal[pos];
        System.arraycopy(internal, pos + 1, internal, pos, internal.length -
                (pos + 1));
        System.arraycopy(missing, pos + 1, missing, pos, internal.length -
                (pos + 1));

        System.arraycopy(empty, pos + 1, empty, pos, internal.length -
                (pos + 1));
        String newInternal[] = new String[internal.length - 1];
        boolean newMissing[] = new boolean[internal.length-1];
        boolean newEmpty[] = new boolean[internal.length-1];
        System.arraycopy(internal, 0, newInternal, 0, internal.length - 1);
        System.arraycopy(missing, 0, newMissing, 0, internal.length - 1);
        System.arraycopy(empty, 0, newEmpty, 0, internal.length - 1);
        internal = newInternal;
		this.setMissingValues(newMissing);
        empty = newEmpty;
        return  removed;
    }

    /**
     Inserts a new entry in the Column at position pos.
     All elements from pos to capacity will be moved up one.
     @param newEntry the newEntry to insert
     @param pos the position to insert at
     */
    public void insertRow (Object newEntry, int pos) {
        if (pos > getCapacity()) {
            addRow(newEntry);
            return;
        }
        String[] newInternal = new String[internal.length + 1];
        boolean[] newMissing = new boolean[internal.length + 1];
        boolean[] newEmpty = new boolean[internal.length + 1];
        if (pos == 0) {
            System.arraycopy(internal, 0, newInternal, 1, getCapacity());
            System.arraycopy(missing, 0, newMissing, 1, getNumRows());
            System.arraycopy(empty, 0, newEmpty, 1, getNumRows());
        }
        else {
            System.arraycopy(internal, 0, newInternal, 0, pos);
            System.arraycopy(internal, pos, newInternal, pos + 1, internal.length
                    - pos);
            System.arraycopy(missing, 0, newMissing, 0, pos);
            System.arraycopy(missing, pos, newMissing, pos + 1, internal.length
                    - pos);

            System.arraycopy(empty, 0, newEmpty, 0, pos);
            System.arraycopy(empty, pos, newEmpty, pos + 1, internal.length
                    - pos);
        }
        newInternal[pos] = (String)newEntry;
        internal = newInternal;
		this.setMissingValues(newMissing);
        empty = newEmpty;
    }

    /**
     Swaps two entries in the Column
     @param pos1 the position of the 1st entry to swap
     @param pos2 the position of the 2nd entry to swap
     */
    public void swapRows (int pos1, int pos2) {
        String e1 = internal[pos1];
        boolean miss = missing[pos1];
        boolean emp = empty[pos1];
        internal[pos1] = internal[pos2];
        internal[pos2] = e1;
        missing[pos1] = missing[pos2];
        missing[pos2] = miss;

        empty[pos1] = empty[pos2];
        empty[pos2] = emp;
    }

    /**
     Return a copy of this Column, re-ordered based on the input array of indexes.
     Does not overwrite this Column.

     @param newOrder an array of indices indicating a new order
    @return a copy of this Column, re-ordered
     */
    public Column reorderRows (int[] newOrder) {
        String[] newInternal = null;
        boolean[] newMissing = null;
        boolean[] newEmpty = null;
        if (newOrder.length == internal.length) {
            newInternal = new String[internal.length];
            //ANCA: added newMissing, newEmpty allocation statements
            newMissing = new boolean[internal.length];
            newEmpty = new boolean[internal.length];
            for (int i = 0; i < internal.length; i++) {
                newInternal[i] = internal[newOrder[i]];
                newMissing[i] = missing[newOrder[i]];
                newEmpty[i] = empty[newOrder[i]];
            }
        }
        else
            throw  new ArrayIndexOutOfBoundsException();
        StringObjectColumn sc = new StringObjectColumn(newInternal, newMissing,newEmpty,getLabel(), getComment());
        return  sc;
    }

    /**
     Compare the values of the element passed in and pos. Return 0 if they
     are the same, greater than zero if element is greater,
    and less than zero if element is less.

     @param element the element to be passed in and compared
     @param pos the position of the element in Column to be compare with
     @return a value representing the relationship- >, <, or == 0
     */
    public int compareRows (Object element, int pos) {
        String b = internal[pos];
        return  compareStrings((String)element, b);

    }

    /**
     Compare pos1 and pos2 positions in the Column. Return 0 if they
     are the same, greater than zero if pos1 is greater,
    and less than zero if pos1 is less.

     @param pos1 the position of the first element to compare
     @param pos2 the position of the second element to compare
     @return a value representing the relationship- >, <, or == 0
     */
    public int compareRows (int pos1, int pos2) {
        String b1 = internal[pos1];
        String b2 = internal[pos2];
        return  compareStrings(b1, b2);
    }

    /**
    * Compare two Strings
     * @param b1 the first String to compare
     * @param b2 the second String to compare
     * @return the result of compareTo
    * @see java.lang.String
     */
    private int compareStrings (String b1, String b2) {
        //return  b1.compareTo(b2);
        return TableUtilities.compareStrings(b1, b2);
    }

    /**
     Given an array of booleans, will remove the positions in the Column
     which coorespond to the positions in the boolean array which are
     marked true.  If the boolean array and Column do not have the same
     number of elements, the remaining elements will be discarded.
     @param flags the boolean array of remove flags
     */
    public void removeByFlag (boolean[] flags) {
        // keep a list of the row indices to remove
        LinkedList ll = new LinkedList();
        int i = 0;
        for (; i < flags.length; i++) {
            if (flags[i])
                ll.add(new Integer(i));
        }
        for (; i < internal.length; i++) {
            ll.add(new Integer(i));
        }
        int[] toRemove = new int[ll.size()];
        int j = 0;
        Iterator iter = ll.iterator();
        while (iter.hasNext()) {
            Integer in = (Integer)iter.next();
            toRemove[j] = in.intValue();
            j++;
        }
        // now call remove by index to remove the rows
        removeRowsByIndex(toRemove);
    }

    /**
     Given an array of ints, will remove the positions in the Column
     which are indicated by the ints in the array. If the int array and Column
     do not have the same number of elements, the remaining elements will be discarded.
     @param indices the int array of remove indices
     */
    public void removeRowsByIndex (int[] indices) {
        HashSet toRemove = new HashSet(indices.length);
        for (int i = 0; i < indices.length; i++) {
            Integer id = new Integer(indices[i]);
            toRemove.add(id);
        }
        String newInternal[] = new String[internal.length - indices.length];
        boolean newMissing[] = new boolean[internal.length - indices.length];
        boolean newEmpty[] = new boolean[internal.length - indices.length];
        int newIntIdx = 0;
        for (int i = 0; i < getNumRows(); i++) {
            // check if this row is in the list of rows to remove
            //Integer x = (Integer)toRemove.get(new Integer(i));
            // if this row is not in the list, copy it into the new internal
            //if (x == null) {
			if(!toRemove.contains(new Integer(i))) {
                newInternal[newIntIdx] = internal[i];
                newMissing[newIntIdx] = missing[i];
                newEmpty[newIntIdx] = empty[i];
                newIntIdx++;
            }
            else
                internal[i] = null;
        }
        internal = newInternal;
		this.setMissingValues(newMissing);
        empty = newEmpty;
    }

    /**
     Sort the elements in this column.
     @exception NotSupportedException when sorting is not supported
     */
    public void sort () {
        sort(null);
    }

    /**
     Sort the elements in this column, and swap the rows in the table
     we are a part of.
     @param vt the VerticalTable to swap rows for
     @exception NotSupportedException when sorting is not supported
     */
    public void sort (MutableTable vt) {
        internal = doSort(internal, 0, internal.length - 1, vt);
    }

   public void sort(MutableTable vt, int begin, int end) {
      internal = doSort(internal, begin, end, vt);
   }

    /**
     Implement the quicksort algorithm.  Partition the array and
     recursively call doSort.
     @param A the array to sort
     @param p the beginning index
     @param r the ending index
     @param t the VerticalTable to swap rows for
    @return a sorted array of Strings
     */
    private String[] doSort (String[] A, int p, int r, MutableTable t) {
        if (p < r) {
            int q = partition(A, p, r, t);
            doSort(A, p, q, t);
            doSort(A, q + 1, r, t);
        }
        return  A;
    }

    /**
     Rearrange the subarray A[p..r] in place.
     @param A the array to rearrange
     @param p the beginning index
     @param r the ending index
     @param t the VerticalTable to swap rows for
    @return the partition index
     */
    private int partition (String[] A, int p, int r, MutableTable t) {
		boolean xMissing = this.isValueMissing(p);
		int i = p - 1;
		int j = r + 1;
		String pStr = this.getString(p);
		while (true) {
			if (xMissing) {
				j--;
				do {
					i++;
				} while (!this.isValueMissing(i));
			} else {
				do {
					j--;
				} while (this.isValueMissing(j) || 
					TableUtilities.compareStrings(A[j], pStr) > 0);
				do {
					i++;
				} while (!this.isValueMissing(i) && 
					TableUtilities.compareStrings(A[i], pStr) < 0);
			}
			if (i < j) {
				if (t == null)
					this.swapRows(i, j);
				else
					t.swapRows(i, j);
			}
			else
				return  j;
		}
     }
    public void setValueToEmpty(boolean b, int row) {
        empty[row] = b;
    }

    public boolean isValueEmpty(int row) {
        return empty[row];
	}
}
/*StringColumn*/

