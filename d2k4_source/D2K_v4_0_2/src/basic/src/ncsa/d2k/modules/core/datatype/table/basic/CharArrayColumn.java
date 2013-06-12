package ncsa.d2k.modules.core.datatype.table.basic;

import ncsa.d2k.modules.core.datatype.table.*;

import java.io.*;
import java.util.*;
import ncsa.d2k.modules.core.datatype.table.util.ByteUtils;

/**
 CharArrayColumn is an implementation of TextualColumn which stores
 textual data in a char form.  The data is represented internally as an
 array of char arrays.
 <br><br>
 It it optimized for: retrieval of words by index, compact representation
 of words,  swapping of words, setting of words by index, reOrdering by index,
 compareing of words
 <br><br>
 It is inefficient for: removals, insertions, searching(on contents of word),
 <br>
 */
final public class CharArrayColumn
	extends MissingValuesColumn
	implements TextualColumn {

	static final long serialVersionUID = -2731483357925113065L;

	/** the internal representation of this Column */
	private char[][] internal = null;
	private boolean[] empty = null;
	/**
	 Create a new, empty CharArrayColumn
	 */
	public CharArrayColumn() {
		this(0);
	}

	/**
	 Create a new CharArrayColumn with the specified capacity
	 @param capacity the initial capacity
	 */
	public CharArrayColumn(int capacity) {
		internal = new char[capacity][];
		//char[] ty = new char[0];
		//setType(ty);
		setIsNominal(true);
		type = ColumnTypes.CHAR_ARRAY;
		//setScalarMissingValue(new Integer(Integer.MIN_VALUE));
		//setScalarEmptyValue(new Integer(Integer.MAX_VALUE));
		missing = new boolean[internal.length];
		empty = new boolean[internal.length];
		for (int i = 0; i < internal.length; i++) {
			missing[i] = false;
			empty[i] = false;
		}
	}

	/**
	 Create a new CharArrayColumn with the specified data
	 @param newInternal the initial data
	 */
	public CharArrayColumn(char[][] newInternal) {
		this.setInternal(newInternal);
		//char[] ty = new char[0];
		//setType(ty);
		setIsNominal(true);
		type = ColumnTypes.CHAR_ARRAY;
		//setScalarMissingValue(new Integer(Integer.MIN_VALUE));
		//setScalarEmptyValue(new Integer(Integer.MAX_VALUE));
		missing = new boolean[internal.length];
		empty = new boolean[internal.length];
		for (int i = 0; i < internal.length; i++) {
			missing[i] = false;
			empty[i] = false;
		}
	}

	private CharArrayColumn(
		char[][] newInternal,
		boolean[] newMiss,
		boolean[] newEmp,
		String lbl,
		String comm) {
		this.setInternal(newInternal);
		setIsNominal(true);
		type = ColumnTypes.CHAR_ARRAY;
		this.setMissingValues(newMiss);
		empty = newEmp;
		setLabel(lbl);
		setComment(comm);
	}

	/**
	 Return an exact copy of this CharArrayColumn.  A deep copy is attempted,
	 but if it fails a new column will be created, initialized with the same
	 data as this column.
	 @return A new Column with a copy of the contents of this column.
	 */
	public Column copy() {
		CharArrayColumn cac;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(this);
			byte buf[] = baos.toByteArray();
			oos.close();
			ByteArrayInputStream bais = new ByteArrayInputStream(buf);
			ObjectInputStream ois = new ObjectInputStream(bais);
			cac = (CharArrayColumn) ois.readObject();
			ois.close();
			return cac;
		} catch (Exception e) {
			//cac = new CharArrayColumn(getCapacity());
			char[][] newVals = new char[getNumRows()][];
			for (int i = 0; i < getNumRows(); i++) {
				char[] val = getChars(i);
				char[] temp = new char[val.length];
				for (int j = 0; j < val.length; j++)
					temp[j] = val[j];
				//cac.setChars(temp, i);
				newVals[i] = temp;
			}
			boolean[] miss = new boolean[internal.length];
			boolean[] em = new boolean[internal.length];
			for (int i = 0; i < internal.length; i++) {
				miss[i] = missing[i];
				em[i] = empty[i];

			}
			cac =
				new CharArrayColumn(
					newVals,
					miss,
					em,
					getLabel(),
					getComment());
			return cac;
		}
	}

	/** support TextualColumn */
	//////////////////////////////////////
	//// ACCESSING FIELD ELEMENTS
	/**
	 * Add the specified number of blank rows.
	 * @param number number of rows to add.
	 */
	public void addRows(int number) {
		int last = internal.length;
		char[][] newInternal = new char[last + number][];
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
	public String getString(int pos) {
		return new String(this.internal[pos]);
	}

	/**
	 Stores newEntry in this column at pos as a char[]
	 @param newEntry the entry to store
	 @param pos the position to put newEntry
	 */
	public void setString(String newEntry, int pos) {
		this.internal[pos] = newEntry.toCharArray();
	}

	/**
	 Creates a String from the entry at pos and uses Integer.parseInt() to
	 get the int value.
	 @param pos the position
	 @return the value at pos as an int
	 */
	public int getInt(int pos) {
		return Integer.parseInt(new String(internal[pos]));
	}

	/**
	 Converts newEntry to a String and stores it as a char[].
	 @param newEntry the new item
	 @param pos the position to store newEntry
	 */
	public void setInt(int newEntry, int pos) {
		internal[pos] = Integer.toString(newEntry).toCharArray();
	}

	/**
	 Creates a String from the entry at pos and uses Short.parseShort() to
	 get the short value.
	 @param pos the position
	 @return the value at pos as a short
	 */
	public short getShort(int pos) {
		return Short.parseShort(new String(internal[pos]));
	}

	/**
	 Converts newEntry to a String and stores it as a char[].
	 @param newEntry the new item
	 @param pos the position to store newEntry
	 */
	public void setShort(short newEntry, int pos) {
		internal[pos] = Short.toString(newEntry).toCharArray();
	}

	/**
	 Converts newEntry to a String and stores it as a char[].
	 @param newEntry the new item
	 @param pos the position to store newEntry
	 */
	public void setLong(long newEntry, int pos) {
		internal[pos] = Long.toString(newEntry).toCharArray();
	}

	/**
	 Creates a String from the entry at pos and uses Long.parseLong() to
	 get the long value.
	 @param pos the position
	 @return the value at pos as a long
	 */
	public long getLong(int pos) {
		return Long.parseLong(new String(internal[pos]));
	}

	/**
	 Creates a String from the entry at pos and uses Double.parseDouble() to
	 get the double value.
	 @param pos the position
	 @return the value at pos as a double
	 */
	public double getDouble(int pos) {
		return Double.parseDouble(new String(internal[pos]));
	}

	/**
	 Converts newEntry to a String and stores it as a char[].
	 @param newEntry the new item
	 @param pos the position to store newEntry
	 */
	public void setDouble(double newEntry, int pos) {
		internal[pos] = Double.toString(newEntry).toCharArray();
	}

	/**
	 Creates a String from the entry at pos and uses Float.parseFloat() to
	 get the float value.
	 @param pos the position
	 @return the value at pos as a double
	 */
	public float getFloat(int pos) {
		return Float.parseFloat(new String(internal[pos]));
	}

	/**
	 Converts newEntry to a String and stores it as a char[].
	 @param newEntry the new item
	 @param pos the position to store newEntry
	 */
	public void setFloat(float newEntry, int pos) {
		internal[pos] = Float.toString(newEntry).toCharArray();
	}

	/**
	 Creates a String from the entry at pos and returns it as
	 a byte[].
	 @param pos the position
	 @return the value at pos as a double
	 */
	public byte[] getBytes(int pos) {
		return new String(internal[pos]).getBytes();
	}

	/**
	 Converts newEntry to char[] by calling ByteUtils.toChars()
	 @param newEntry the new item
	 @param pos the position to store newEntry
	 */
	public void setBytes(byte[] newEntry, int pos) {
		//internal[pos] = new String(newEntry).toCharArray();
		internal[pos] = ByteUtils.toChars(newEntry);
	}

	/**
	 Creates a String from the entry at pos and returns it as
	 a byte[].
	 @param pos the position
	 @return the value at pos as a double
	 */
	public byte getByte(int pos) {
		//return getBytes(pos)[0];//return  new String(internal[pos]).getBytes();
		return Byte.parseByte(new String(getChars(pos)));
	}

	/**
	 Converts newEntry to char[] by calling ByteUtils.toChars()
	 @param newEntry the new item
	 @param pos the position to store newEntry
	 */
	public void setByte(byte newEntry, int pos) {
		/*byte[] b = new byte[1];
		b[0] = newEntry;
		setBytes(b, pos);
		*/
		setChars(Byte.toString(newEntry).toCharArray(), pos);
	}

	/**
	 Get the value at pos.
	 @param pos the index
	 @return the entry at pos
	 */
	public char[] getChars(int pos) {
		return this.internal[pos];
	}

	/**
	 Set the entry at pos to be newEntry
	 @param newEntry the new item
	 @param pos the position
	 */
	public void setChars(char[] newEntry, int pos) {
		internal[pos] = newEntry;
	}

	/**
	 Get the value at pos.
	 @param pos the index
	 @return the entry at pos
	 */
	public char getChar(int pos) {
		return getChars(pos)[0];
	}

	/**
	 Set the entry at pos to be newEntry
	 @param newEntry the new item
	 @param pos the position
	 */
	public void setChar(char newEntry, int pos) {
		char[] c = new char[1];
		c[0] = newEntry;
		setChars(c, pos);
	}

	/**
	 Return the entry at pos as an object (char[])
	 @param pos the position
	 @return the entry at pos
	 */
	public Object getObject(int pos) {
		return internal[pos];
	}

	/**
	 Stores newEntry as a char[].  If the object is a char[] or byte[],
	 the appropriate method is called, otherwise setString() is called
	 with newEntry.toString()
	 @param newEntry the new item
	 @param pos the position
	 */
	public void setObject(Object newEntry, int pos) {
		if (newEntry instanceof char[])
			setChars((char[]) newEntry, pos);
		else if (newEntry instanceof byte[])
			setBytes((byte[]) newEntry, pos);
		else
			setString(newEntry.toString(), pos);
	}

	/**
	 Converts the entry at pos to a String and returns the boolean value
	 of a Boolean object constructed from the String.
	 @param pos the postion
	 @return a boolean representation of the entry at pos
	 */
	public boolean getBoolean(int pos) {
		return new Boolean(new String(internal[pos])).booleanValue();
	}

	/**
	 Calls the toString() method on newEntry to get a String and
	 stores the String as a char[].
	 @param newEntry the new item
	 @param pos the position
	 */
	public void setBoolean(boolean newEntry, int pos) {
		internal[pos] = new Boolean(newEntry).toString().toCharArray();
	}

	/**
	 * Trim any excess storage from the internal buffer for this TextualColumn.
	 */
	public void trim() {
	}

	//////////////////////////////////////
	//// Accessing Metadata
	/**
	 Return the count for the number of non-null entries.
	 This variable is recomputed each time...as keeping
	 track of it could be very time inefficient.
	 @return this CharArrayColumn's number of non-null rows
	 */
	public int getNumEntries() {
		int numEntries = 0;
		for (int i = 0; i < internal.length; i++)
			//ANCA added isValueMissing and isValueEmpty
			if (internal[i] != null && !isValueMissing(i) && !isValueEmpty(i))
				numEntries++;
		return numEntries;
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
	public int getCapacity() {
		return internal.length;
	}

	/**
	 Suggests a new capacity for this Column.  If this implementation of Column supports
	 capacity then the suggestion may be followed. The capacity is its potential
	 max number of entries.  If numEntries > newCapacity then Column may be truncated.
	 If internal.length > newCapacity then Column will be truncated.
	 @param newCapacity a new capacity
	 */
	public void setNumRows(int newCapacity) {
		/*        if (internal != null) {
		            char[][] newInternal = new char[newCapacity][];
		            if (newCapacity > internal.length)
		                newCapacity = internal.length;
		            System.arraycopy(internal, 0, newInternal, 0, newCapacity);
		            internal = newInternal;
		        }
		        else
		            internal = new char[newCapacity][];
		        */
		if (internal != null) {
			char[][] newInternal = new char[newCapacity][];
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
		} else {
			internal = new char[newCapacity][];
			missing = new boolean[newCapacity];
			empty = new boolean[newCapacity];
		}
	}

	//////////////////////////////////////
	//// ACCESSING CharArrayColumn ELEMENTS
	/**
	 Gets an entry from the Column at the indicated position.
	 For CharArrayColumn, this is the same as calling char[] getBytes( int )
	 @param pos the position
	 @return the entry at pos
	 */
	public Object getRow(int pos) {
		return this.internal[pos];
	}

	/**
	 Gets a subset of this Column, given a start position and length.
	 Only the charwords refs are copied, so if you change their contents,
	 they change, but if you reassign the ref, the Column is not affected.
	 @param pos the start position for the subset
	 @param len the length of the subset
	 @return a subset of this Column
	 */
	public Column getSubset(int pos, int len) {
		char[][] subset = new char[len][];
		boolean[] newMissing = new boolean[len];
		boolean[] newEmpty = new boolean[len];
		System.arraycopy(internal, pos, subset, 0, len);
		System.arraycopy(missing, pos, newMissing, 0, len);
		System.arraycopy(empty, pos, newEmpty, 0, len);
		CharArrayColumn cac =
			new CharArrayColumn(
				subset,
				newMissing,
				newEmpty,
				getLabel(),
				getComment());
		return cac;
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
	public Column getSubset(int[] rows) {
		char[][] subset = new char[rows.length][];
		boolean[] newMissing = new boolean[rows.length];
		boolean[] newEmpty = new boolean[rows.length];
		for (int i = 0; i < rows.length; i++) {
			subset[i] = internal[rows[i]];
			newMissing[i] = missing[rows[i]];
			newEmpty[i] = empty[rows[i]];
		}
		CharArrayColumn bc =
			new CharArrayColumn(
				subset,
				newMissing,
				newEmpty,
				getLabel(),
				getComment());
		return bc;
	}

	/**
	 Gets a reference to the internal representation of this Column.
	 Changes made to this object will be reflected in the Column.
	 @return the internal representation of this Column.
	 */
	public Object getInternal() {
		return this.internal;
	}

	/**
	 Sets the reference to the internal representation of this Column.
	 If a miscompatable Object is passed in, the most common Exception
	 thrown is a ClassCastException.
	 @param newInternal a new internal representation for this Column
	 */
	public void setInternal(Object newInternal) {
		this.internal = (char[][]) newInternal;
	}

	/**
	 Sets the entry at the given position to newEntry
	 @param newEntry a new entry
	 @param pos the position to set
	 */
	public void setRow(Object newEntry, int pos) {
		this.internal[pos] = (char[]) newEntry;
	}

	/**
	 Adds the new entry to the Column after the last non-null position
	 in the Column.
	 @param newEntry a new entry
	 */
	public void addRow(Object newEntry) {
		int last = internal.length;
		char[][] newInternal = new char[internal.length + 1][];
		boolean[] newMissing = new boolean[internal.length + 1];
		boolean[] newEmpty = new boolean[internal.length + 1];
		System.arraycopy(internal, 0, newInternal, 0, internal.length);
		System.arraycopy(missing, 0, newMissing, 0, missing.length);
		System.arraycopy(empty, 0, newEmpty, 0, empty.length);
		newInternal[last] = (char[]) newEntry;
		internal = newInternal;
		this.setMissingValues(newMissing);
		missing = newMissing;
		empty = newEmpty;
	}

	/**
	 Remove an entry from the Column, at pos.
	 All entries from pos+1 will be moved back 1 position
	 @param pos the position to remove
	 @return the removed object
	 */
	public Object removeRow(int pos) {
		char[] removed = internal[pos];
		System.arraycopy(
			internal,
			pos + 1,
			internal,
			pos,
			internal.length - (pos + 1));
		System.arraycopy(
			missing,
			pos + 1,
			missing,
			pos,
			internal.length - (pos + 1));
		System.arraycopy(
			empty,
			pos + 1,
			empty,
			pos,
			internal.length - (pos + 1));
		internal[internal.length - 1] = null;
		char[][] newInternal = new char[internal.length - 1][];
		boolean newMissing[] = new boolean[internal.length - 1];
		boolean newEmpty[] = new boolean[internal.length - 1];
		System.arraycopy(internal, 0, newInternal, 0, internal.length - 1);
		System.arraycopy(missing, 0, newMissing, 0, internal.length - 1);
		System.arraycopy(empty, 0, newEmpty, 0, internal.length - 1);
		internal = newInternal;
		this.setMissingValues(newMissing);
		empty = newEmpty;
		return removed;
	}

	/**
	 Inserts a new entry in the Column at position pos.
	 all elements from pos to capacity will be moved up one.
	 @param newEntry the newEntry to insert
	 @param pos the position to insert at
	 */
	public void insertRow(Object newEntry, int pos) {
		if (pos > getCapacity()) {
			addRow(newEntry);
			return;
		}
		char[][] newInternal = new char[internal.length + 1][];
		boolean[] newMissing = new boolean[internal.length + 1];
		boolean[] newEmpty = new boolean[internal.length + 1];
		if (pos == 0) {
			System.arraycopy(internal, 0, newInternal, 1, getCapacity());
			System.arraycopy(missing, 0, newMissing, 1, getNumRows());
			System.arraycopy(empty, 0, newEmpty, 1, getNumRows());
		} else {
			System.arraycopy(internal, 0, newInternal, 0, pos);
			System.arraycopy(
				internal,
				pos,
				newInternal,
				pos + 1,
				internal.length - pos);
			System.arraycopy(missing, 0, newMissing, 0, pos);
			System.arraycopy(
				missing,
				pos,
				newMissing,
				pos + 1,
				internal.length - pos);

			System.arraycopy(empty, 0, newEmpty, 0, pos);
			System.arraycopy(
				empty,
				pos,
				newEmpty,
				pos + 1,
				internal.length - pos);
		}
		newInternal[pos] = (char[]) newEntry;
		internal = newInternal;
		this.setMissingValues(newMissing);
		empty = newEmpty;
	}

	/**
	 Swaps two entries in the Column
	 @param pos1 the position of the 1st entry to swap
	 @param pos2 the position of the 2nd entry to swap
	 */
	public void swapRows(int pos1, int pos2) {
		char[] e1 = internal[pos1];
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
	public Column reorderRows(int[] newOrder) {
		char[][] newInternal = null;
		boolean[] newMissing = null;
		boolean[] newEmpty = null;
		if (newOrder.length == internal.length) {
			newInternal = new char[internal.length][];
			//ANCA allocated mem for newMissing, newEmpty
			newMissing = new boolean[internal.length];
			newEmpty = new boolean[internal.length];
			for (int i = 0; i < internal.length; i++) {
				newInternal[i] = internal[newOrder[i]];
				newMissing[i] = missing[newOrder[i]];
				newEmpty[i] = empty[newOrder[i]];
			}
		} else
			throw new ArrayIndexOutOfBoundsException();
		CharArrayColumn cac =
			new CharArrayColumn(
				newInternal,
				newMissing,
				newEmpty,
				getLabel(),
				getComment());

		return cac;
	}

	/**
	 Compare the values of the element passed in and pos. Return 0 if they
	 are the same, greater than zero if element is greater,
	 and less than zero if element is less.
	
	 @param element the element to be passed in and compared
	 @param pos the position of the element in Column to be compare with
	 @return a value representing the relationship- >, <, or == 0
	 */
	public int compareRows(Object element, int pos) {
		char[] b = internal[pos];
		return compareChars((char[]) element, b);
	}

	/**
	 Compare pos1 and pos2 positions in the Column. Return 0 if they
	 are the same, greater than zero if pos1 is greater,
	 and less than zero if pos1 is less.
	
	 @param pos1 the position of the first element to compare
	 @param pos2 the position of the second element to compare
	 @return a value representing the relationship- >, <, or == 0
	 */
	public int compareRows(int pos1, int pos2) {
		char[] b1 = internal[pos1];
		char[] b2 = internal[pos2];
		return compareChars(b1, b2);
	}

	/**
	 * Compare two char arrays
	 * @param b1 the first char[] to compare
	 * @param b2 the second char[] to compare
	 * @return -1, 0, 1
	 */
	private static int compareChars(char[] b1, char[] b2) {
		if (b1 == null) {
			if (b2 == null)
				return 0;
			else
				return -1;
		} else if (b2 == null)
			return 1;
		if (b1.length < b2.length) {
			for (int i = 0; i < b1.length; i++) {
				if (b1[i] < b2[i])
					return -1;
				else if (b1[i] > b2[i])
					return 1;
			}
			return -1;
		} else if (b1.length > b2.length) {
			for (int i = 0; i < b2.length; i++) {
				if (b1[i] < b2[i])
					return -1;
				else if (b1[i] > b2[i])
					return 1;
			}
			return 1;
		} else {
			for (int i = 0; i < b2.length; i++) {
				if (b1[i] < b2[i])
					return -1;
				else if (b1[i] > b2[i])
					return 1;
			}
			return 0;
		}
	}

	/**
	 Given an array of ints, will remove the positions in the Column
	 which are indicated by the ints in the array.
	 @param indices the int array of remove indices
	 */
	public void removeRowsByIndex(int[] indices) {
		HashSet toRemove = new HashSet(indices.length);
		for (int i = 0; i < indices.length; i++) {
			Integer id = new Integer(indices[i]);
			toRemove.add(id);
		}
		char newInternal[][] = new char[internal.length - indices.length][];
		boolean newMissing[] = new boolean[internal.length - indices.length];
		boolean newEmpty[] = new boolean[internal.length - indices.length];
		int newIntIdx = 0;
		for (int i = 0; i < getNumRows(); i++) {
			// check if this row is in the list of rows to remove
			//Integer x = (Integer)toRemove.get(new Integer(i));
			// if this row is not in the list, copy it into the new internal
			//if (x == null) {
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

	//////////////////////////////////////
	/**
	 Sort the elements in this column.
	 @exception NotSupportedException when sorting is not supported
	 */
	public void sort() {
		sort(null);
	}

	/**
	 Sort the elements in this column, and swap the rows in the table
	 we are a part of.
	 @param t the Table to swap rows for
	 */
	public void sort(MutableTable t) {
		internal = doSort(internal, 0, internal.length - 1, t);
	}

	/**
	   Sort the elements in this column starting with row 'begin' up to row 'end',
	   and swap the rows in the table  we are a part of.
	   @param t the VerticalTable to swap rows for
	   @param begin the row no. which marks the beginnig of the  column segment to be sorted
	   @param end the row no. which marks the end of the column segment to be sorted
	*/
	public void sort(MutableTable t, int begin, int end) {
		if (end > internal.length - 1) {
			System.err.println(" end index was out of bounds");
			end = internal.length - 1;
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
	 @return a sorted array of char[]
	 */
	private char[][] doSort(char[][] A, int p, int r, MutableTable t) {
		if (p < r) {
			int q = partition(A, p, r, t);
			doSort(A, p, q, t);
			doSort(A, q + 1, r, t);
		}
		return A;
	}

	/**
	 Rearrange the subarray A[p..r] in place.
	 @param A the array to rearrange
	 @param p the beginning index
	 @param r the ending index
	 @param t the Table to swap rows for
	 @return the new partition point
	 */
	private int partition(char[][] A, int p, int r, MutableTable t) {
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
	 char[][] objInternal = (char [][])((CharArrayColumn)obj).getInternal();
	 if (internal.length != objInternal.length) return false;
	 for (int i =0; i < internal.length; i ++)
	   if (compareRows(objInternal[i],i) !=0 ) return false;
	  return true; 
	
	}
}
/*CharArrayColumn*/
