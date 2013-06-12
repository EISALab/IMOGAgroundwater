package ncsa.d2k.modules.core.datatype.table.basic;

import ncsa.d2k.modules.core.datatype.table.*;

import java.io.*;
import java.util.*;


/**
 * <code>ByteColumn</code> is an implementation of <code>NumericColumn</code>
 * which stores data in a byte form. The internal representation is a
 * <code>byte</code> array.
 * <br><br>
 * It it optimized for: retrieval of words by index, compact representation
 * of words, swapping of words, setting of words by index, reordering by index,
 * comparing of words
 * <br><br>
 * It is inefficient for: removals, insertions, searching (on contents of word)
 */
final public class ByteColumn extends MissingValuesColumn implements NumericColumn {

	//static final long serialVersionUID = 8471652021364392992L;
	static final long serialVersionUID = -8647688352992361702L;

    /** the internal representation of this Column */
    private byte[] internal = null;
	private boolean[] empty = null;
	private byte min;
	private byte max;

    /**
     * Creates a new, empty <code>ByteColumn</code>.
     */
    public ByteColumn () {
        this(0);
    }

    /**
     * Creates a new <code>ByteColumn</code> with the specified initial
     * capacity.
     *
     * @param capacity       the initial capacity
     */
    public ByteColumn (int capacity) {
        internal = new byte[capacity];
        setIsNominal(true);
        type = ColumnTypes.BYTE;
		missing = new boolean[internal.length];
		empty = new boolean[internal.length];
		for(int i = 0; i < internal.length; i++) {
			missing[i] = false;
			empty[i] = false;
		}

    }

    /**
     * Creates a new <code>ByteColumn</code> with the specified data.
     *
     * @param newInternal    the default data this column holds
     */
    public ByteColumn (byte[] newInternal) {
		internal = newInternal;
        setIsNominal(true);
        type = ColumnTypes.BYTE;
		missing = new boolean[internal.length];
		empty = new boolean[internal.length];
		for(int i = 0; i < internal.length; i++) {
			missing[i] = false;
			empty[i] = false;
		}
    }

    private ByteColumn(byte[] newInt, boolean[] miss, boolean[] emp,
                       String lbl, String comm) {
		internal = newInt;
        setIsNominal(true);
        type = ColumnTypes.BYTE;
        this.setMissingValues(miss);
        empty = emp;
        setLabel(lbl);
        setComment(comm);
    }

    /**
     * Returns an exact copy of this <code>ByteColumn</code>. A deep copy is
     * attempted, but if it fails, a new column will be created, initialized
     * with the same data as this column.
     *
     * @return               a new <code>Column</code> with a copy of the
     *                       contents of this column.
     */
    public Column copy () {
        ByteColumn bac;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            byte buf[] = baos.toByteArray();
            oos.close();
            ByteArrayInputStream bais = new ByteArrayInputStream(buf);
            ObjectInputStream ois = new ObjectInputStream(bais);
            bac = (ByteColumn)ois.readObject();
            ois.close();
            return  bac;
        } catch (Exception e) {
            //bac = new ByteColumn(getCapacity());
            byte[] newVals = new byte[getNumRows()];
            for (int i = 0; i < getNumRows(); i++)
                //bac.setByte(getByte(i), i);
                newVals[i] = getByte(i);
 			boolean[] miss = new boolean[internal.length];
			boolean[] em = new boolean[internal.length];
			for(int i = 0; i < internal.length; i++) {
				miss[i] = missing[i];
				em[i] = empty[i];

			}
		 	//bac.missing = miss;
			//bac.empty = em;

            bac = new ByteColumn(newVals, miss, em, getLabel(), getComment());
            return  bac;
        }
    }

	public double getMax() {
		initRange();
		return (double)max;
	}

	public double getMin() {
		initRange();
		return (double)min;
	}

    /**
     * Initializes the min and max of this IntColumn.
     */
    private void initRange () {
        max = min = internal[0];
        for (int i = 1; i < internal.length; i++) {
			if(!isValueMissing(i) && !isValueEmpty(i)) {
            	if (internal[i] > max)
                	max = internal[i];
            	if (internal[i] < min)
                	min = internal[i];
			}
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
		byte[] newInternal = new byte[last + number];
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
	 * Returns the internal representation of the data.
	 * @return the internal representation of the data.
	 */
	public Object getInternal () {
		return internal;
	}

    /**
     * Gets the entry at <code>pos</code> as a <code>String</code>.
     *
     * @param pos            the position from which to get a
     *                       <code>String</code>
     * @return               a <code>String</code> representation of the entry
     *                       at that position
     */
    public String getString (int pos) {
        return new Byte(getByte(pos)).toString();
    }

    /**
     * Set the entry at <code>pos</code> to be <code>newEntry</code>.
     * <code>Byte.byteValue()</code> is called to store <code>newEntry</code>
     * as a <code>byte</code>.
     *
     * @param newEntry       the new item to put in the column
     * @param pos            the position in which to put <code>newEntry</code>
     */
    public void setString (String newEntry, int pos) {
        setByte(Byte.valueOf(newEntry).byteValue(), pos);
    }

    /**
     * Gets the value of the <code>byte</code> at <code>pos</code>, cast
     * to an <code>int</code>.
     *
     * @param pos            the position to get the data from
     * @return               the value of the <code>byte</code> at
     *                       <code>pos</code> as an <code>int</code>
     */
    public int getInt (int pos) {
        return (int)getByte(pos);
    }

    /**
     * Casts <code>newEntry</code> to a <code>byte</code> and stores it at
     * position <code>pos</code>.
     *
     * @param newEntry       the new item
     * @param pos            the position to place <code>newEntry</code>
     */
    public void setInt (int newEntry, int pos) {
        setByte((byte)newEntry, pos);
    }

    /**
     * Gets the value of the <code>byte</code> at <code>pos</code>, cast
     * to a <code>short</code>.
     *
     * @param pos            the position to get the data from
     * @return               the value of the <code>byte</code> at
     *                       <code>pos</code> as a <code>short</code>
     */
    public short getShort (int pos) {
        return (short)getByte(pos);
    }

    /**
     * Casts <code>newEntry</code> to a <code>byte</code> and stores it at
     * position <code>pos</code>.
     *
     * @param newEntry       the new item
     * @param pos            the position to place <code>newEntry</code>
     */
    public void setShort (short newEntry, int pos) {
        setByte((byte)newEntry, pos);
    }

    /**
     * Gets the value of the <code>byte</code> at <code>pos</code>, cast
     * to a <code>long</code>.
     *
     * @param pos            the position to get the data from
     * @return               the value of the <code>byte</code> at
     *                       <code>pos</code> as a <code>long</code>
     */
    public long getLong (int pos) {
        return (long)getByte(pos);
    }

    /**
     * Casts <code>newEntry</code> to a <code>byte</code> and stores it at
     * position <code>pos</code>.
     *
     * @param newEntry       the new item
     * @param pos            the position to place <code>newEntry</code>
     */
    public void setLong (long newEntry, int pos) {
        setByte((byte)newEntry, pos);
    }

    /**
     * Gets the value of the <code>byte</code> at <code>pos</code>, cast
     * to a <code>double</code>.
     *
     * @param pos            the position to get the data from
     * @return               the value of the <code>byte</code> at
     *                       <code>pos</code> as a <code>double</code>
     */
    public double getDouble (int pos) {
        return (double)getByte(pos);
    }

    /**
     * Casts <code>newEntry</code> to a <code>byte</code> and stores it at
     * position <code>pos</code>.
     *
     * @param newEntry       the new item
     * @param pos            the position to place <code>newEntry</code>
     */
    public void setDouble (double newEntry, int pos) {
        setByte((byte)newEntry, pos);
    }

    /**
     * Gets the value of the <code>byte</code> at <code>pos</code>, cast
     * to a <code>float</code>.
     *
     * @param pos            the position to get the data from
     * @return               the value of the <code>byte</code> at
     *                       <code>pos</code> as a <code>float</code>
     */
    public float getFloat (int pos) {
        return (float)getByte(pos);
    }

    /**
     * Casts <code>newEntry</code> to a <code>byte</code> and stores it at
     * position <code>pos</code>.
     *
     * @param newEntry       the new item
     * @param pos            the position to place <code>newEntry</code>
     */
    public void setFloat (float newEntry, int pos) {
        setByte((byte)newEntry, pos);
    }

    /**
     * Gets a <code>byte</code> array, the first element of which is the
     * <code>byte</code> at position <code>pos</code>.
     *
     * @param pos            the position to get the data from
     * @return               the appropriate <code>byte</code> array
     */
    public byte[] getBytes (int pos) {
        byte[] retVal = new byte[1];
        retVal[0] = getByte(pos);
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
    public void setBytes (byte[] newEntry, int pos) {
        setByte(newEntry[0], pos);
    }

    /**
     * Gets the <code>byte</code> at this position.
     *
     * @param pos            the position to get the data from
     * @return               the <code>byte</code> at position <code>pos</code>
     */
    public byte getByte(int pos) {
        return internal[pos];
    }

    /**
     * Sets the <code>byte</code> at this position to be <code>newEntry</code>.
     *
     * @param newEntry       the new item
     * @param pos            the position to place <code>newEntry</code>
     */
    public void setByte (byte newEntry, int pos) {
        internal[pos] = newEntry;
    }

    /**
     * Gets a <code>char</code> array representing this <code>byte</code>
     * as text.
     *
     * @param pos            the position to get the data from
     * @return               the corresponding <code>char</code> array
     */
    public char[] getChars (int pos) {
        return Byte.toString(getByte(pos)).toCharArray();
    }

    /**
     * Attempts to parse <code>newEntry</code> as a textual representation
     * of a <code>byte</code> and store that value at <code>pos</code>.
     *
     * @param newEntry       the new item
     * @param pos            the position to place <code>newEntry</code>
     */
    public void setChars (char[] newEntry, int pos) {
        setString(String.copyValueOf(newEntry), pos);
    }

    /**
     * Gets the value of the <code>byte</code> at <code>pos</code>, cast
     * to a <code>char</code>.
     *
     * @param pos            the position to get the data from
     * @return               the value of the <code>byte</code> at
     *                       <code>pos</code> as a <code>char</code>
     */
    public char getChar (int pos) {
        return (char)getByte(pos);
    }

    /**
     * Casts <code>newEntry</code> to a <code>byte</code> and stores it at
     * position <code>pos</code>.
     *
     * @param newEntry       the new item
     * @param pos            the position to place <code>newEntry</code>
     */
    public void setChar (char newEntry, int pos) {
        setByte((byte)newEntry, pos);
    }

    /**
     * Returns a new <code>Byte</code> containing the value of the
     * <code>byte</code> at <code>pos</code>.
     *
     * @param pos            the position of interest
     * @return               a corresponding <code>Byte</code>.
     */
    public Object getObject (int pos) {
        return new Byte(getByte(pos));
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
    public void setObject (Object newEntry, int pos) {
        if (newEntry instanceof byte[])
            setBytes((byte[])newEntry, pos);
        else if (newEntry instanceof char[])
            setChars((char[])newEntry, pos);
        else if (newEntry instanceof Byte)
            setByte(((Byte)newEntry).byteValue(), pos);
        else
            setString(newEntry.toString(), pos);
    }

    /**
     * Returns <code>false</code> if the <code>byte</code> at <code>pos</code>
     * is equal to 0. Otherwise, returns 1.
     *
     * @param pos            the position of interest
     * @return               the corresponding <code>boolean</code>.
     */
    public boolean getBoolean (int pos) {
        if (getByte(pos) == 0)
            return false;
        else
            return true;
    }

    /**
     * If <code>newEntry</code> is <code>false</code>, stores 0 at position
     * <code>pos</code>. Otherwise, stores 1.
     *
     * @param newEntry       the new item
     * @param pos            the position to place newEntry
     */
    public void setBoolean (boolean newEntry, int pos) {
        if (newEntry)
            setByte((byte)1, pos);
        else
            setByte((byte)0, pos);
    }

    //////////////////////////////////////
    //// Accessing Metadata

    /**
        Return the count for the number of non-null entries.  This variable is
        recomputed each time...as keeping track of it could be very time
        inefficient.
        @return this ByteArrayColumn's number of entries
     */
    public int getNumEntries () {
        int numEntries = 0;
        for (int i = 0; i < internal.length; i++)
            if (!isValueMissing(i) && !isValueEmpty(i))
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
            byte[] newInternal = new byte[newCapacity];
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
            internal = new byte[newCapacity];
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
        return  new Byte(internal[pos]);
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
        if ((pos + len) > internal.length)
            throw  new ArrayIndexOutOfBoundsException();
        byte[] subset = new byte[len];
		boolean[] newMissing = new boolean[len];
		boolean[] newEmpty = new boolean[len];
        System.arraycopy(internal, pos, subset, 0, len);
		System.arraycopy(missing, pos, newMissing, 0, len);
		System.arraycopy(empty, pos, newEmpty, 0, len);
        ByteColumn bc = new ByteColumn(subset, newMissing, newEmpty,
                                       getLabel(), getComment());
        return  bc;
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
        byte[] subset = new byte[rows.length];
        boolean[] newMissing = new boolean[rows.length];
        boolean[] newEmpty = new boolean[rows.length];
        for(int i = 0; i < rows.length; i++) {
          subset[i] = internal[rows[i]];
          newMissing[i] = missing[rows[i]];
          newEmpty[i] = empty[rows[i]];
        }
        ByteColumn bc = new ByteColumn(subset, newMissing, newEmpty, getLabel(), getComment());
        return  bc;
    }


    /**
        Gets a reference to the internal representation of this Column.
        Changes made to this object will be reflected in the Column.
        @return the internal representation of this Column.
     /
    public Object getInternal () {
        return  this.internal;
    }*/

    /**
        Sets the reference to the internal representation of this Column.
        If a miscompatable Object is passed in, the most common Exception
        thrown is a ClassCastException.
        @param newInternal a new internal representation for this Column
     /
    public void setInternal (Object newInternal) {
        this.internal = (byte[])newInternal;
    }*/

    /**
        Sets the entry at the given position to newEntry
        @param newEntry a new entry
        @param pos the position to set
     */
    public void setRow (Object newEntry, int pos) {
        setObject(newEntry, pos);
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
/*        int last = internal.length;
        byte[] newInternal = new byte[internal.length + 1];
        System.arraycopy(internal, 0, newInternal, 0, internal.length);
        newInternal[last] = ((Byte)newEntry).byteValue();
        internal = newInternal;
	*/
        int last = internal.length;
        byte[] newInternal = new byte[internal.length + 1];
		boolean[] newMissing = new boolean[internal.length + 1];
		boolean[] newEmpty = new boolean[internal.length + 1];
        System.arraycopy(internal, 0, newInternal, 0, internal.length);
		System.arraycopy(missing, 0, newMissing, 0, missing.length);
		System.arraycopy(empty, 0, newEmpty, 0, empty.length);
        newInternal[last] = ((Number)newEntry).byteValue();

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
/*        byte removed = internal[pos];
        System.arraycopy(internal, pos + 1, internal, pos, internal.length -
                (pos + 1));
        byte newInternal[] = new byte[internal.length - 1];
        System.arraycopy(internal, 0, newInternal, 0, internal.length - 1);
        internal = newInternal;
        return new Byte(removed);
*/

        byte removed = internal[pos];
        // copy all the items after the item to be removed one position up
        System.arraycopy(internal, pos + 1, internal, pos, internal.length -
                (pos + 1));

        System.arraycopy(missing, pos + 1, missing, pos, internal.length -
                (pos + 1));

        System.arraycopy(empty, pos + 1, empty, pos, internal.length -
                (pos + 1));

        // copy the items into a new array
        byte newInternal[] = new byte[internal.length - 1];
		boolean newMissing[] = new boolean[internal.length-1];
		boolean newEmpty[] = new boolean[internal.length-1];
        System.arraycopy(internal, 0, newInternal, 0, internal.length - 1);
        System.arraycopy(missing, 0, newMissing, 0, internal.length - 1);
        System.arraycopy(empty, 0, newEmpty, 0, internal.length - 1);

        internal = newInternal;
        this.setMissingValues(newMissing);
		empty = newEmpty;
        return  new Byte(removed);
    }

    /**
        Inserts a new entry in the Column at position pos.
        All elements from pos to capacity will be moved up one.
        @param newEntry the newEntry to insert
        @param pos the position to insert at
     */
    public void insertRow (Object newEntry, int pos) {
        /*byte[] newInternal = new byte[internal.length + 1];
        if (pos > getCapacity()) {
            addRow(newEntry);
            return;
        }
        if (pos == 0)
            System.arraycopy(internal, 0, newInternal, 1, getCapacity());
        else {
            System.arraycopy(internal, 0, newInternal, 0, pos);
            System.arraycopy(internal, pos, newInternal, pos + 1, internal.length
                    - pos);
        }
        newInternal[pos] = ((Byte)newEntry).byteValue();
        internal = newInternal;
		*/

        if (pos > getNumRows()) {
            addRow(newEntry);
            return;
        }
        byte[] newInternal = new byte[internal.length + 1];
		boolean[] newMissing = new boolean[internal.length + 1];
		boolean[] newEmpty = new boolean[internal.length + 1];
        if (pos == 0) {
            System.arraycopy(internal, 0, newInternal, 1, getNumRows());
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
        newInternal[pos] = ((Number)newEntry).byteValue();
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
        /*byte e1 = internal[pos1];
        internal[pos1] = internal[pos2];
        internal[pos2] = e1;
		*/

        byte d1 = internal[pos1];
		boolean miss = missing[pos1];
		boolean emp = empty[pos1];
        internal[pos1] = internal[pos2];
        internal[pos2] = d1;

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
        /*byte[] newInternal = null;
        if (newOrder.length == internal.length) {
            newInternal = new byte[internal.length];
            for (int i = 0; i < internal.length; i++)
                newInternal[i] = internal[newOrder[i]];
        }
        else
            throw  new ArrayIndexOutOfBoundsException();
        ByteColumn bac = new ByteColumn(newInternal);
        bac.setLabel(getLabel());
        //bac.setType(getType());
        bac.setComment(getComment());
		bac.setScalarEmptyValue(getScalarEmptyValue());
		bac.setScalarMissingValue(getScalarMissingValue());
		bac.setNominalEmptyValue(getNominalEmptyValue());
		bac.setNominalMissingValue(getNominalMissingValue());

        return  bac;
		*/
        byte[] newInternal = null;
		boolean[] newMissing = null;
		boolean[] newEmpty = null;
        if (newOrder.length == internal.length) {
            newInternal = new byte[internal.length];
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
        /*ByteColumn bc = new ByteColumn(newInternal);
		bc.missing = newMissing;
		bc.empty = newEmpty;
        bc.setLabel(getLabel());
        bc.setComment(getComment());
        */
        ByteColumn bc = new ByteColumn(newInternal, newMissing, newEmpty, getLabel(), getComment());
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
        byte[] b1 = new byte[1];
        b1[0] = ((Byte)element).byteValue();
        byte[] b2 = new byte[1];
        b2[0] = getByte(pos);
        return compareBytes(b1, b2);
    }

    /**
        Compare pos1 and pos2 positions in the Column. Return 0 if they
        are the same, greater than 0 if pos1 is greater, and less than 0 if pos1 is less.
        @param pos1 the position of the first element to compare
        @param pos2 the position of the second element to compare
        @return a value representing the relationship- >, <, or == 0
     */
    public int compareRows (int pos1, int pos2) {
        byte[] b1 = new byte[1];
        b1[0] = getByte(pos1);
        byte[] b2 = new byte[1];
        b2[0] = getByte(pos2);
        return compareBytes(b1, b2);
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
        /*HashSet toRemove = new HashSet(indices.length);
        for (int i = 0; i < indices.length; i++) {
            Integer id = new Integer(indices[i]);
            toRemove.add(id);
        }
        byte newInternal[] = new byte[internal.length - indices.length];
        int newIntIdx = 0;
        for (int i = 0; i < getNumRows(); i++) {
            // check if this row is in the list of rows to remove
            // if this row is not in the list, copy it into the new internal
            if (!toRemove.contains(new Integer(i))) {
                newInternal[newIntIdx] = internal[i];
                newIntIdx++;
            }
            //else
            //   internal[i] = null;
        }
        internal = newInternal;
		*/
        HashSet toRemove = new HashSet(indices.length);
        for (int i = 0; i < indices.length; i++) {
            Integer id = new Integer(indices[i]);
            toRemove.add(id);
        }
        byte newInternal[] = new byte[internal.length - indices.length];
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
    private byte[] doSort (byte[] A, int p, int r, MutableTable t) {
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
    private int partition (byte[] A, int p, int r, MutableTable t) {
		boolean xMissing = this.isValueMissing(p);
		int i = p - 1;
		int j = r + 1;
		
		//ANCA introduced Object el , because A[j] is treated like an int and compareRows(int,int) is
		//called instead of compareRows(object,int);
		Byte el;
		while (true) {
			if (xMissing) {
				j--;
				do {
					i++;
				} while (!this.isValueMissing(i));
			} else {
				do {
					j--;
					el = new Byte(A[j]);
				} while (this.isValueMissing(j) || compareRows(el, p) > 0);
				do {
					i++;
					el = new Byte(A[i]);
				} while (!this.isValueMissing(i) && compareRows(el, p) < 0);
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

/*ByteColumn*/
