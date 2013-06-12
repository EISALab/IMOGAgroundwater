package ncsa.d2k.modules.core.datatype.table.basic;

import ncsa.d2k.modules.core.datatype.table.*;

import java.io.*;
import java.util.*;
//import ncsa.util.*;
import ncsa.d2k.modules.core.datatype.table.util.ByteUtils;

/**
	ByteArrayColumn is an implementation of TextualColumn which stores
	textual data in a byte form.  The internal representation is an array of
	byte arrays.
	<br><br>
	It it optimized for: retrieval of words by index, compact representation
	of words,  swapping of words, setting of words by index, reOrdering by index,
	comparing of words
	<br><br>
	It is inefficient for: removals, insertions, searching(on contents of word),
 */
final public class ByteArrayColumn extends MissingValuesColumn implements TextualColumn {

	//static final long serialVersionUID = 4081605254880124454L;
	static final long serialVersionUID = -9055397440406116816L;

    /** the internal representation of this Column */
    private byte[][] internal = null;
	private boolean[] empty = null;

    /**
    	Create a new, empty ByteArrayColumn.
     */
    public ByteArrayColumn () {
        this(0);
    }

    /**
    	Create a new ByteArrayColumn with the specified initial capacity.
		@param capacity the initial capacity
     */
    public ByteArrayColumn (int capacity) {
        internal = new byte[capacity][];
		setIsNominal(true);
		type = ColumnTypes.BYTE_ARRAY;
	  	//setScalarMissingValue(new Double(Double.MIN_VALUE));
	  	//setScalarEmptyValue(new Double(Double.MAX_VALUE));
        missing = new boolean[internal.length];
        empty = new boolean[internal.length];
        for(int i = 0; i < internal.length; i++) {
            missing[i] = false;
            empty[i] = false;
		}
    }

    /**
    	Create a new ByteArrayColumn with the specified data.
		@param newInternal the default data this column holds
     */
    public ByteArrayColumn (byte[][] newInternal) {
        this.setInternal(newInternal);
		setIsNominal(true);
		type = ColumnTypes.BYTE_ARRAY;
	  	//setScalarMissingValue(new Double(Double.MIN_VALUE));
	  	//setScalarEmptyValue(new Double(Double.MAX_VALUE));
        missing = new boolean[internal.length];
        empty = new boolean[internal.length];
        for(int i = 0; i < internal.length; i++) {
            missing[i] = false;
            empty[i] = false;
		}
    }

    private ByteArrayColumn(byte[][] newInternal, boolean[] miss, boolean[] emp,
                            String lbl, String comm) {
        setInternal(newInternal);
		setIsNominal(true);
		type = ColumnTypes.BYTE_ARRAY;
		this.setMissingValues(miss);
        empty = emp;
        setLabel(lbl);
        setComment(comm);
    }

    /**
    	Return an exact copy of this ByteArrayColumn.  A deep copy is attempted,
		but if it fails a new column will be created, initialized with the same
		data as this column.
    	@return A new Column with a copy of the contents of this column.
     */
    public Column copy () {
        ByteArrayColumn bac;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            byte buf[] = baos.toByteArray();
            oos.close();
            ByteArrayInputStream bais = new ByteArrayInputStream(buf);
            ObjectInputStream ois = new ObjectInputStream(bais);
            bac = (ByteArrayColumn)ois.readObject();
            ois.close();
            return  bac;
        } catch (Exception e) {
            byte[][] newVals = new byte[getNumRows()][];
            for (int i = 0; i < getNumRows(); i++) {
                byte orig[] = getBytes(i);
                byte res[] = new byte[orig.length];
                for (int j = 0; j < orig.length; j++)
                    res[j] = orig[j];
                //bac.setBytes(res, i);
                newVals[i] = res;
            }
            boolean[] miss = new boolean[internal.length];
            boolean[] em = new boolean[internal.length];
            for(int i = 0; i < internal.length; i++) {
                miss[i] = missing[i];
                em[i] = empty[i];
            }
            bac = new ByteArrayColumn(newVals, miss, em, getLabel(), getComment());
            return  bac;
        }
    }

    //////////////////////////////////////
    //// ACCESSING FIELD ELEMENTS
	/**
	 * Add the specified number of blank rows.
	 * @param number number of rows to add.
	 */
	public void addRows (int number) {
		int last = internal.length;
		byte [][] newInternal = new byte[last + number][];
		boolean[] newMissing = new boolean[last + number];
		boolean[] newEmpty = new boolean[last + number];

		System.arraycopy(internal, 0, newInternal, 0, last);
		System.arraycopy(missing, 0, newMissing, 0, missing.length);
		System.arraycopy(empty, 0, newEmpty, 0, empty.length);
		internal = newInternal;
		this.setMissingValues(newMissing);
		empty = newEmpty;
	}


    /**
    	Get a String from this Column at pos
    	@param pos the position from which to get a String
    	@return a String representation of the entry at that position
     */
    public String getString (int pos) {
        return  new String(this.internal[pos]);
    }

    /**
    	Set the entry at pos to be newEntry.  newEntry.getBytes() is called
    	to store newEntry as a byte array.
    	@param newEntry the new item to put in the column
    	@param pos the position to put newEntry
     */
    public void setString (String newEntry, int pos) {
        this.internal[pos] = newEntry.getBytes();
    }

    /**
    	Get the value of the bytes as an int using ByteUtils.toInt()
    	@param pos the position to get the data from
    	@return the value of the byte array at pos as an int
     */
    public int getInt (int pos) {
		//return Integer.valueOf(new String(internal[pos]));
        return  ByteUtils.toInt(internal[pos]);
    }

    /**
    	Store newEntry as an array of bytes using ByteUtils.writeInt()
    	@param newEntry the new item
    	@param pos the position to place newEntry
     */
    public void setInt (int newEntry, int pos) {
        //internal[pos] = Integer.toString(newEntry).getBytes();
        internal[pos] = ByteUtils.writeInt(newEntry);
    }

    /**
    	Get the value of the bytes as a short using ByteUtils.toShort().
    	@param pos the position to get the data from
    	@return the value of the byte array at pos as a short
     */
    public short getShort (int pos) {
        //return Short.valueOf(new String(internal[pos])).shortValue();
        return  ByteUtils.toShort(internal[pos]);
    }

    /**
    	Store newEntry as an array of bytes using ByteUtils.writeShort()
    	@param newEntry the new item
    	@param pos the position to place newEntry
     */
    public void setShort (short newEntry, int pos) {
        //internal[pos] = Short.toString(newEntry).getBytes();
        internal[pos] = ByteUtils.writeShort(newEntry);
    }

    /**
    	Get the value of the bytes as a long using ByteUtils.toLong().
    	@param pos the position to get the data from
    	@return the value of the byte array at pos as a long
     */
    public long getLong (int pos) {
        //return Long.valueOf(new String(internal[pos])).longValue();
        return  ByteUtils.toLong(internal[pos]);
    }

    /**
    	Store newEntry as an array of bytes using ByteUtils.writeLong()
    	@param newEntry the new item
    	@param pos the position to place newEntry
     */
    public void setLong (long newEntry, int pos) {
        //internal[pos] = Long.toString(newEntry).getBytes();
        internal[pos] = ByteUtils.writeLong(newEntry);
    }

    /**
    	Get the value of the bytes as a double using ByteUtils.toDouble().
    	@param pos the position to get the data from
    	@return the value of the byte array at pos as a double
     */
    public double getDouble (int pos) {
        return  ByteUtils.toDouble(internal[pos]);
    }

    /**
    	Store newEntry as an array of bytes using ByteUtils.writeDouble()
    	@param newEntry the new item
    	@param pos the position to place newEntry
     */
    public void setDouble (double newEntry, int pos) {
        //internal[pos] = Double.toString(newEntry).getBytes();
        internal[pos] = ByteUtils.writeDouble(newEntry);
    }

    /**
    	Get the value of the bytes as a float using ByteUtils.toFloat()
    	@param pos the position to get the data from
    	@return the value of the byte array at pos as a float
     */
    public float getFloat (int pos) {
        //return Float.valueOf(new String(internal[pos])).floatValue();
        return  ByteUtils.toFloat(internal[pos]);
    }

    /**
    	Store newEntry as an array of bytes using ByteUtils.writeFloat()
    	@param newEntry the new item
    	@param pos the position to place newEntry
     */
    public void setFloat (float newEntry, int pos) {
        //internal[pos] = Float.toString(newEntry).getBytes();
        internal[pos] = ByteUtils.writeFloat(newEntry);
    }

    /**
    	Get the value of the bytes at this position.
    	@param pos the position to get the data from
    	@return the byte array at pos
     */
    public byte[] getBytes (int pos) {
        return  this.internal[pos];
    }

    /**
    	Put newEntry into this column at pos
    	@param newEntry the new item
    	@param pos the position to place newEntry
     */
    public void setBytes (byte[] newEntry, int pos) {
        this.internal[pos] = newEntry;
    }

    /**
    	Get the value of the bytes at this position.
    	@param pos the position to get the data from
    	@return the byte array at pos
     */
    public byte getByte(int pos) {
		return getBytes(pos)[0];
    }

    /**
    	Put newEntry into this column at pos
    	@param newEntry the new item
    	@param pos the position to place newEntry
     */
    public void setByte (byte newEntry, int pos) {
		byte[] b = new byte[1];
		b[0] = newEntry;
		setBytes(b, pos);
    }

    /**
    	Get the value of the bytes as a char[] using ByteUtils.toChars()
    	@param pos the position to get the data from
    	@return the value of the byte array at pos as a char[]
     */
    public char[] getChars (int pos) {
        return  ByteUtils.toChars(internal[pos]);
    }

    /**
    	Store newEntry as an array of bytes using ByteUtils.writeChars()
    	@param newEntry the new item
    	@param pos the position to place newEntry
     */
    public void setChars (char[] newEntry, int pos) {
        //this.internal[pos] = new String(newEntry).getBytes();
        internal[pos] = ByteUtils.writeChars(newEntry);
    }

    /**
    	Get the value of the bytes as a char[] using ByteUtils.toChars()
    	@param pos the position to get the data from
    	@return the value of the byte array at pos as a char[]
     */
    public char getChar (int pos) {
		return getChars(pos)[0];
    }

    /**
    	Store newEntry as an array of bytes using ByteUtils.writeChars()
    	@param newEntry the new item
    	@param pos the position to place newEntry
     */
    public void setChar (char newEntry, int pos) {
		char[] c = new char[1];
		c[0] = newEntry;
		setChars(c, pos);
    }

    /**
    	Return the entry at pos as an Object (byte[])
    	@param pos the position of interest
		@return the Object at the specified row
     */
    public Object getObject (int pos) {
        return  this.internal[pos];
    }

    /**
    	Set the entry at pos to be newEntry.  If newEntry is a
    	byte[] or char[], call the appropriate method.  Otherwise,
    	convert the Object to a byte[] by calling ByteUtils.writeObject()
    	@param newEntry the new item
    	@param pos the position
     */
    public void setObject (Object newEntry, int pos) {
        if (newEntry instanceof byte[])
            setBytes((byte[])newEntry, pos);
        else if (newEntry instanceof char[])
            setChars((char[])newEntry, pos);
        else
            internal[pos] = ByteUtils.writeObject(newEntry);
    }

    /**
    	Return the entry at pos as a boolean using ByteUtils.toBoolean()
    	@param pos the position of interest
		@return the boolean at the specified row
     */
    public boolean getBoolean (int pos) {
        //return new Boolean(new String(internal[pos])).booleanValue();
        return  ByteUtils.toBoolean(internal[pos]);
    }

    /**
    	Store newEntry as an array of bytes using ByteUtils.writeBoolean()
    	@param newEntry the new item
    	@param pos the position to place newEntry
     */
    public void setBoolean (boolean newEntry, int pos) {
        //this.internal[pos] = new Boolean(newEntry).toString().getBytes();
        internal[pos] = ByteUtils.writeBoolean(newEntry);
    }

	/**
	 * Trim any excess storage from the internal buffer for this TextualColumn.
	 */
	public void trim() {}

    //////////////////////////////////////
    //// Accessing Metadata
    /**
    	Return the count for the number of non-null entries.  This variable is
		recomputed each time...as keeping track of it could be very time
		inefficient.
    	@return this ByteArrayColumn's number of entries
     */
    public int getNumEntries () {
        //return internal.length;
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
    	Suggests a new capacity for this Column.  If this implementation of Column
    	supports capacity then the suggestion may be followed. The capacity is its
    	potential max number of entries.  If numEntries > newCapacity then Column
    	may be truncated.  If internal.length > newCapacity then Column will be
    	truncated.
    	@param newCapacity a new capacity
     */
    public void setNumRows (int newCapacity) {

        if (internal != null) {
            byte[][] newInternal = new byte[newCapacity][];
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
            internal = new byte[newCapacity][];
            missing = new boolean[newCapacity];
            empty = new boolean[newCapacity];
		}

    }

    //////////////////////////////////////
    //////////////////////////////////////
    //// ACCESSING ByteArrayColumn ELEMENTS
    /**
    	Gets an entry from the Column at the indicated position.
    	For ByteArrayColumn, this is the same as calling getBytes( int )
    	@param pos the position
    	@return the entry at pos
     */
    public Object getRow (int pos) {
        return  this.internal[pos];
    }

    /**
    	Gets a subset of this Column, given a start position and length.
    	Only the byteword references are copied, so if you change their contents,
    	they change, but if you reassign the reference, the Column is not affected.
    	@param pos the start position for the subset
    	@param len the length of the subset
    	@return a subset of this Column
     */
    public Column getSubset (int pos, int len) {
        byte[][] subset = new byte[len][];
        boolean[] newMissing = new boolean[len];
        boolean[] newEmpty = new boolean[len];
        System.arraycopy(internal, pos, subset, 0, len);
        System.arraycopy(missing, pos, newMissing, 0, len);
        System.arraycopy(empty, pos, newEmpty, 0, len);
        ByteArrayColumn bac = new ByteArrayColumn(subset, newMissing, newEmpty,
                getLabel(), getComment());
        return  bac;
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
        byte[][] subset = new byte[rows.length][];
        boolean[] newMissing = new boolean[rows.length];
        boolean[] newEmpty = new boolean[rows.length];
        for(int i = 0; i < rows.length; i++) {
          subset[i] = internal[rows[i]];
          newMissing[i] = missing[rows[i]];
          newEmpty[i] = empty[rows[i]];
        }

        ByteArrayColumn bc = new ByteArrayColumn(subset, newMissing, newEmpty, getLabel(), getComment());
        return  bc;
    }


    /**
    	Gets a reference to the internal representation of this Column.
    	Changes made to this object will be reflected in the Column.
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
        this.internal = (byte[][])newInternal;
    }

    /**
    	Sets the entry at the given position to newEntry
    	@param newEntry a new entry
    	@param pos the position to set
     */
    public void setRow (Object newEntry, int pos) {
        this.internal[pos] = (byte[])newEntry;
    }

    /**
    	Appends the new entry to the end of the Column.
    	@param newEntry a new entry
     */
    public void addRow (Object newEntry) {
        /*int last = 0;
         for(int i=internal.length-1;i>=0;i--)
         if( internal[i] != null )
         last = i;
         this.internal[last+1] = (byte[])newEntry;
         */
        int last = internal.length;
        byte[][] newInternal = new byte[internal.length + 1][];
        boolean[] newMissing = new boolean[internal.length + 1];
        boolean[] newEmpty = new boolean[internal.length + 1];
        System.arraycopy(internal, 0, newInternal, 0, internal.length);
        System.arraycopy(missing, 0, newMissing, 0, missing.length);
        System.arraycopy(empty, 0, newEmpty, 0, empty.length);
        newInternal[last] = (byte[])newEntry;
        internal = newInternal;
        this.setMissingValues(newMissing);
		empty = newEmpty;
    }

    /**
    	Removes an entry from the Column, at pos.
    	All entries from pos+1 will be moved back 1 position
    	@param pos the position to remove
    	@return the removed object
     */
    public Object removeRow (int pos) {
        byte[] removed = internal[pos];
        System.arraycopy(internal, pos + 1, internal, pos, internal.length -
                (pos + 1));
        System.arraycopy(missing, pos + 1, missing, pos, internal.length -
                (pos + 1));
        System.arraycopy(empty, pos + 1, empty, pos, internal.length -
                (pos + 1));
        byte newInternal[][] = new byte[internal.length - 1][];
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
        byte[][] newInternal = new byte[internal.length + 1][];
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
        newInternal[pos] = (byte[])newEntry;
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
        byte[] e1 = internal[pos1];
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
		@return a copy of this column, re-ordered
     */
    public Column reorderRows (int[] newOrder) {
        byte[][] newInternal = null;
        boolean[] newMissing = null;
        boolean[] newEmpty = null;
        if (newOrder.length == internal.length) {
            newInternal = new byte[internal.length][];
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
        ByteArrayColumn bc = new ByteArrayColumn(newInternal, newMissing, newEmpty, getLabel(), getComment());
        return  bc;
    }

    /**
    	Compare the values of the element passed in and pos. Return 0 if they
    	are the same, greater than 0 if element is greater, and less than 0 if
		element is less.
    	@param element the element to be passed in and compared
    	@param pos the position of the element in Column to be compare with
    	@return a value representing the relationship- >, <, or == 0
     */
    public int compareRows (Object element, int pos) {
        byte[] b = internal[pos];
        return  compareBytes((byte[])element, b);
    }

    /**
    	Compare pos1 and pos2 positions in the Column. Return 0 if they
    	are the same, greater than 0 if pos1 is greater, and less than 0 if pos1 is less.
    	@param pos1 the position of the first element to compare
    	@param pos2 the position of the second element to compare
    	@return a value representing the relationship- >, <, or == 0
     */
    public int compareRows (int pos1, int pos2) {
        byte[] b1 = internal[pos1];
        byte[] b2 = internal[pos2];
        return  compareBytes(b1, b2);
    }

    /**
	 * Compare two byte arrays
     * @param b1 the first byte array to compare
     * @param b2 the second byte array to compare
     * @return -1, 0, 1
     */
    private static int compareBytes (byte[] b1, byte[] b2) {
        if (b1 == null) {
            if (b2 == null)
                return  0;
            else
                return  -1;
        }
        else if (b2 == null)
            return  1;
        if (b1.length < b2.length) {
            for (int i = 0; i < b1.length; i++) {
                if (b1[i] < b2[i])
                    return  -1;
                else if (b1[i] > b2[i])
                    return  1;
            }
            return  -1;
        }
        else if (b1.length > b2.length) {
            for (int i = 0; i < b2.length; i++) {
                if (b1[i] < b2[i])
                    return  -1;
                else if (b1[i] > b2[i])
                    return  1;
            }
            return  1;
        }
        else {
            for (int i = 0; i < b2.length; i++) {
                if (b1[i] < b2[i])
                    return  -1;
                else if (b1[i] > b2[i])
                    return  1;
            }
            return  0;
        }
    }

    /**
    	Given an array of ints, will remove the positions in the Column
    	which are indicated by the ints in the array.
    	@param indices the int array of remove indices
     */
    public void removeRowsByIndex (int[] indices) {
        HashSet toRemove = new HashSet(indices.length);
        for (int i = 0; i < indices.length; i++) {
            Integer id = new Integer(indices[i]);
            toRemove.add(id);
        }
        byte newInternal[][] = new byte[internal.length - indices.length][];
        boolean newMissing[] = new boolean[internal.length - indices.length];
        boolean newEmpty[] = new boolean[internal.length - indices.length];
        int newIntIdx = 0;
        for (int i = 0; i < getNumRows(); i++) {
            // check if this row is in the list of rows to remove
            //Integer x = (Integer)toRemove.get(new Integer(i));
            // if this row is not in the list, copy it into the new internal
            if (!toRemove.contains(new Integer(i))) {
                newInternal[newIntIdx] = internal[i];
                newMissing[newIntIdx] = missing[i];
                newEmpty[newIntIdx] = empty[i];
                newIntIdx++;
            }
            //else
            //   internal[i] = null;
        }
        internal = newInternal;
        this.setMissingValues(newMissing);
 		empty = newEmpty;
    }

    /**
    	Sort the items in this column.
     */
    public void sort () {
        sort(null);
    }

    /**
    	Sort the elements in this column, and swap the rows in the table
    	we are a part of.
    	@param t the Table to swap rows for
     */
    public void sort (MutableTable t) {
        internal = doSort(internal, 0, internal.length - 1, t);
    }

    /**
       Sort the elements in this column starting with row 'begin' up to row 'end',
       and swap the rows in the table  we are a part of.
       @param t the VerticalTable to swap rows for
       @param begin the row no. which marks the beginnig of the  column segment to be sorted
       @param end the row no. which marks the end of the column segment to be sorted
    */
    public void sort(MutableTable t,int begin, int end)
    {
	if (end > internal.length -1) {
	    System.err.println(" end index was out of bounds");
	    end = internal.length -1;
	}
	internal = doSort(internal, begin, end, t);

    }


    /**
    	Implement the quicksort algorithm.  Partition the array and
    	recursively call doSort.
    	@param A the array to sort
    	@param p the beginning index
    	@param r the ending index
    	@param t the Table to swap rows for
		@return a sorted array of byte arrays
     */
    private byte[][] doSort (byte[][] A, int p, int r, MutableTable t) {
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
    	@param t the Table to swap rows for
		@return the new partition point
     */
    private int partition (byte[][] A, int p, int r, MutableTable t) {
		boolean xMissing = this.isValueMissing(p);
		int i = p - 1;
		int j = r + 1;
		while (true) {
			if (xMissing) {
				j--;
				do {
					i++;
				} while (!this.isValueMissing(i));
			} else {
				do {
					j--;
				} while (this.isValueMissing(j) || (compareRows(A[j], p) > 0));
				do {
					i++;
				} while (!this.isValueMissing(i) && (compareRows(A[i], p) < 0));
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
	
//ANCA added for testing purposes
  public boolean equals(Object obj) {
   byte[][] objInternal = (byte [][])((ByteArrayColumn)obj).getInternal();
   if (internal.length != objInternal.length) return false;
   for (int i =0; i < internal.length; i ++)
	 if (compareRows(objInternal[i],i) !=0 ) return false;
	return true; 
	
  }
}
/*ByteArrayColumn*/
