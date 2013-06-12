package ncsa.d2k.modules.core.datatype.table.basic;

import ncsa.d2k.modules.core.datatype.table.*;

import java.io.*;
import java.util.*;

/**
 * IntColumn is an implementation of NumericColumn which holds an int array as
 * its internal representation.
 * <br>
 * It it optimized for: retrieval of ints by index, compact representation
 * of ints, swapping of ints, setting of ints by index, reordering by index,
 * comparing of ints
 * It is very inefficient for: removals, insertions, additions.
 */
final public class IntColumn extends MissingValuesColumn implements NumericColumn {

	//static final long serialVersionUID = -1898569166071609485L;
	static final long serialVersionUID = 3417435435476640538L;

	private int min, max;
	//private int emptyValue;
	//private int missingValue;

	/** holds IntColumn's internal data rep */
	private int[] internal = null;
	private boolean[] empty = null;
	/**
	 * Create a new, emtpy IntColumn
	 */
	public IntColumn () {
		this(0);
	}

	/**
	 * Create a new IntColumn with the specified capacity
	 * @param capacity the initial capacity
	 */
	public IntColumn (int capacity) {
		internal = new int[capacity];
		setIsScalar(true);
		type = ColumnTypes.INTEGER;
		//setScalarMissingValue(new Integer(Integer.MIN_VALUE));
		//setScalarEmptyValue(new Integer(Integer.MAX_VALUE));
		//setNominalMissingValue(Integer.toString(Integer.MIN_VALUE));
		//setNominalEmptyValue(Integer.toString(Integer.MAX_VALUE));
		missing = new boolean[internal.length];
		empty = new boolean[internal.length];
		for(int i = 0; i < internal.length; i++) {
			missing[i] = false;
			empty[i] = false;
		}
	}

	/**
	 * Create a new IntColumn with the specified values
	 * @param vals the initial values
	 */
	public IntColumn (int[] vals) {
		internal = vals;
		setIsScalar(true);
		type = ColumnTypes.INTEGER;
		//setScalarMissingValue(new Integer(Integer.MIN_VALUE));
		//setScalarEmptyValue(new Integer(Integer.MAX_VALUE));
		//setNominalMissingValue(Integer.toString(Integer.MIN_VALUE));
		//setNominalEmptyValue(Integer.toString(Integer.MAX_VALUE));
		missing = new boolean[internal.length];
		empty = new boolean[internal.length];
		for(int i = 0; i < internal.length; i++) {
			missing[i] = false;
			empty[i] = false;
		}
	}

	private IntColumn(int[] vals, boolean[] miss, boolean[] emp, String lbl,
					  String comm) {
		internal = vals;
		setIsScalar(true);
		type = ColumnTypes.INTEGER;
		this.setMissingValues(miss);
		empty = emp;
		setLabel(lbl);
		setComment(comm);
	}

	/**
	 * Copy method. Return an exact copy of this column.  A deep copy
	 * is attempted, but if it fails a new column will be created,
	 * initialized with the same data as this column.
	 * @return A new Column with a copy of the contents of this column.
	 */
	public Column copy () {
		IntColumn newCol;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(this);
			byte buf[] = baos.toByteArray();
			oos.close();
			ByteArrayInputStream bais = new ByteArrayInputStream(buf);
			ObjectInputStream ois = new ObjectInputStream(bais);
			newCol = (IntColumn)ois.readObject();
			ois.close();
			return  newCol;
		} catch (Exception e) {
			//newCol = new IntColumn(getNumRows());
			int[] newVals = new int[getNumRows()];
			for (int i = 0; i < getNumRows(); i++)
				//newCol.setInt(internal[i], i);
				newVals[i] = getInt(i);
			boolean[] miss = new boolean[internal.length];
			boolean[] em = new boolean[internal.length];
			for(int i = 0; i < internal.length; i++) {
				miss[i] = missing[i];
				em[i] = empty[i];

			}
			newCol = new IntColumn(newVals, miss, em, getLabel(), getComment());
			return  newCol;
		}
	}

	//////////////////////////////////////
	//// Accessing Metadata
	/**
	 * Add the specified number of blank rows.
	 * @param number number of rows to add.
	 */
	public void addRows (int number) {
		int last = internal.length;
		int[] newInternal = new int[last + number];
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
	 * Return the count for the number of non-null entries.
	 * This variable is recomputed each time...as keeping
	 * track of it could be very inefficient.
	 * @return this IntColumn's number of entries
	 */
	public int getNumEntries () {
		int numEntries = 0;
		for (int i = 0; i < internal.length; i++)
			if (!isValueEmpty(i) && !isValueMissing(i))
				numEntries++;
		return  numEntries;
	}


   /**
	* Get the number of rows that this column can hold.
	* @return the number of rows this column can hold
	*/
   public int getNumRows() {
	  return internal.length;
   }

	/**
	 Set the number of rows for this IntColumn.  If the Column implementation supports
	 capacity than the suggestion may be followed. The capacity is it's potential
	 max number of entries.  If numEntries is greater than newCapacity then Column
	may be truncated.
	 @param newCapacity the new capacity
	 */
	public void setNumRows (int newCapacity) {
/*        if (internal != null) {
			int[] newInternal = new int[newCapacity];
			if (newCapacity > internal.length)
				newCapacity = internal.length;
			System.arraycopy(internal, 0, newInternal, 0, newCapacity);
			internal = newInternal;
		}
		else
			internal = new int[newCapacity];
		*/
		if (internal != null) {
			int[] newInternal = new int[newCapacity];
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
			internal = new int[newCapacity];
			missing = new boolean[newCapacity];
			empty = new boolean[newCapacity];
		}
	}

	/**
	 * Get the minimum value contained in this Column
	 * @return the minimum value of this Column
	 */
	public double getMin () {
		initRange();
		return  (double)min;
	}

	/**
	 * Get the maximum value contained in this Column
	 * @return the maximum value of this Column
	 */
	public double getMax () {
		initRange();
		return  (double)max;
	}

	/**
	 * Sets the value which indicates an empty entry.
	 * This can by any subclass of Number
	 * @param emptyVal the value to which an empty entry is set
	 /
	public void setEmptyValue (Number emptyVal) {
		emptyValue = ((Number)emptyVal).intValue();
	}*/

	/**
	 * Gets the value which indicates an empty entry.
	 * @return the value of an empty entry wrapped in a subclass of Number
	 /
	public Number getEmptyValue () {
		return  new Integer(emptyValue);
	}

	/**
	 * Get a String from this column at pos
	 * @param pos the position from which to get a String
	 * @return a String representation of the entry at that position
	 */
	public String getString (int pos) {
		return String.valueOf(this.internal[pos]);
	}

	/**
	 * Converts newEntry to an int using Integer.parseInt() and
	 * inserts it into the table
	 * @param newEntry the new item
	 * @param pos the position
	 */
	public void setString (String newEntry, int pos) {
	   internal[pos] = Integer.parseInt(newEntry);
	}

	/**
	 * Get the value at pos
	 * @param pos the position
	 * @return the value at pos
	 */
	public int getInt (int pos) {
		return  this.internal[pos];
	}

	/**
	 * Set the value at pos to be newEntry
	 * @param newEntry the new item
	 * @param pos the position
	 */
	public void setInt (int newEntry, int pos) {
		this.internal[pos] = newEntry;
	}

	/**
	 * Get the value at pos as a short
	 * @param pos the position
	 * @return the value at pos as a short
	 */
	public short getShort (int pos) {
		return  (short)internal[pos];
	}

	/**
	 * Set the value at pos to be newEntry
	 * @param newEntry the new item
	 * @param pos the position
	 */
	public void setShort (short newEntry, int pos) {
		this.internal[pos] = (int)newEntry;
	}

	/**
	 * Get the value at pos as a long
	 * @param pos the position
	 * @return the value at pos as a long
	 */
	public long getLong (int pos) {
		return  (long)internal[pos];
	}

	/**
	 * Set the value at pos to be newEntry
	 * @param newEntry the new item
	 * @param pos the position
	 */
	public void setLong (long newEntry, int pos) {
		this.internal[pos] = (int)newEntry;
	}

	/**
	 * Get the value at pos as a double
	 * @param pos the position
	 * @return the value at pos as a double
	 */
	public double getDouble (int pos) {
		return  (double)this.internal[pos];
	}

	/**
	 * Set the value at pos to be newEntry
	 * @param newEntry the new item
	 * @param pos the position
	 */
	public void setDouble (double newEntry, int pos) {
		internal[pos] = (int)newEntry;
	}

	/**
	 * Get the value at pos as a float
	 * @param pos the position
	 * @return the value at pos as a float
	 */
	public float getFloat (int pos) {
		return  (float)this.internal[pos];
	}

	/**
	 * Set the value at pos to be newEntry
	 * @param newEntry the new item
	 * @param pos the position
	 */
	public void setFloat (float newEntry, int pos) {
		internal[pos] = (int)newEntry;
	}

	/**
	 * Get the value at pos as a byte array
	 * @param pos the position
	 * @return the value at pos as a byte array
	 */
	public byte[] getBytes (int pos) {
		return (String.valueOf(this.internal[pos])).getBytes();
	}

	/**
	 * Converts newEntry to an int
	 * @param newEntry the new value
	 * @param pos the position
	 */
	public void setBytes (byte[] newEntry, int pos) {
		setString(new String(newEntry), pos);
	}

	/**
	 * Get the value at pos as a byte.
	 * @param pos the position
	 * @return the value at pos as a byte.
	 */
	public byte getByte (int pos) {
	  return (byte)getInt(pos);
	}

	/**
	 * Set the value at pos to be newEntry
	 * @param newEntry the new item
	 * @param pos the position
	 */
	public void setByte (byte newEntry, int pos) {
	  setInt((int)newEntry, pos);
	}

	/**
	 * Returns the value at pos as an Integer
	 * @param pos the position
	 * @return the value at pos as an Integer
	 */
	public Object getObject (int pos) {
		return  new Integer(internal[pos]);
	}

	/**
	 * If newEntry is a Number, get the int value, otherwise
	 * call setString() on newEntry.toString()
	 * @param newEntry the new item
	 * @param pos the position
	 */
	public void setObject (Object newEntry, int pos) {
		//internal[pos] = Integer.valueOf(newEntry.toString()).intValue();
		if (newEntry instanceof Number)
			internal[pos] = ((Number)newEntry).intValue();
		else
			setString(newEntry.toString(), pos);
	}

	/**
	 * Convert the entry at pos to a String and return it as a char[]
	 * @param pos the position
	 * @return the entry at pos as a char[]
	 */
	public char[] getChars (int pos) {
		return  Integer.toString(internal[pos]).toCharArray();
	}

	/**
	 * Convert newEntry to a String and call setString()
	 * @param newEntry the new item
	 * @param pos the position
	 */
	public void setChars (char[] newEntry, int pos) {
		setString(new String(newEntry), pos);
	}

	/**
	 * gets the entry at pos as a char
	 * @param pos the position
	 * @return the entry at pos as a char
	 */
	public char getChar (int pos) {
	  return (char)getInt(pos);
	}

	/**
	 * casts newentry to an int and sets it at pos
	 * @param newEntry the new item
	 * @param pos the position
	 */
	public void setChar (char newEntry, int pos) {
	  setInt((int)newEntry, pos);
	}

	/**
	 * Return false if the entry at pos is equal to zero, true othersie
	 * @param pos the position
	 * @return false if the entry at pos is equal to zero, true otherwise
	 */
	public boolean getBoolean (int pos) {
		if (internal[pos] == 0)
			return  false;
		return  true;
	}

	/**
	 * Set the entry at pos to be 1 if newEntry is true, 0 otherwise
	 * @param newEntry the newEntry
	 * @param pos the position
	 */
	public void setBoolean (boolean newEntry, int pos) {
		if (newEntry)
			internal[pos] = 1;
		else
			internal[pos] = 0;
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
	 Gets a reference to the internal representation of this Column
	 (int[]).  Changes made to this object will be reflected in the Column.
	 However, Column will be unaware of those changes until you call
	 setInternal as, so state variables can get out of sync.
	 @return the internal representation of this Column.
	@deprecated
	 */
	public Object getInternal () {
		return  this.internal;
	}

	/**
	 * Gets a subset of this Column, given a start position and length.
	 * The primitive values are copied, so they have no destructive abilities
	 * as far as the Column is concerned.
	 * @param pos the start position for the subset
	 * @param len the length of the subset
	 * @return a subset of this Column
	 */
	public Column getSubset (int pos, int len) {
		if ((pos + len) > internal.length)
			throw  new ArrayIndexOutOfBoundsException();
		int[] subset = new int[len];
		boolean[] newMissing = new boolean[len];
		boolean[] newEmpty = new boolean[len];
		System.arraycopy(internal, pos, subset, 0, len);
		System.arraycopy(missing, pos, newMissing, 0, len);
		System.arraycopy(empty, pos, newEmpty, 0, len);
		IntColumn ic = new IntColumn(subset, newMissing, newEmpty,
									 getLabel(), getComment());
		return  ic;
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
		int[] subset = new int[rows.length];
		boolean[] newMissing = new boolean[rows.length];
		boolean[] newEmpty = new boolean[rows.length];
		for(int i = 0; i < rows.length; i++) {
		  subset[i] = internal[rows[i]];
		  newMissing[i] = missing[rows[i]];
		  newEmpty[i] = empty[rows[i]];
		}
		IntColumn bc = new IntColumn(subset, newMissing, newEmpty, getLabel(), getComment());
		return  bc;
	}


	//////////////////////////////////////
	//// SUPPORT FOR Column INTERFACE

	/**
	 Sets the reference to the internal representation of this Column.
	 @param newInternal a new internal representation for this Column
	 /
	public void setInternal (Object newInternal) {
		if (newInternal instanceof int[])
			this.internal = (int[])newInternal;
	}*/

	/**
	 * Gets an object representation of the entry at the indicated position in Column
	 * @param pos the position
	 * @return the entry at pos
	 */
	public Object getRow (int pos) {
		return  new Integer(internal[pos]);
	}

	/**
	 * Sets the entry at the given position to newEntry.
	 * The newEntry should be a subclass of Number, preferable Integer.
	 * @param newEntry a new entry, a subclass of Number
	 * @param pos the position to set
	 */
	public void setRow (Object newEntry, int pos) {
		internal[pos] = ((Number)newEntry).intValue();
	}

	/**
	 * Adds the new entry to the Column after the last non-empty position
	 * in the Column.
	 * @param newEntry a new entry
	 */
	public void addRow (Object newEntry) {
		int last = internal.length;
		int[] newInternal = new int[internal.length + 1];
		boolean[] newMissing = new boolean[internal.length + 1];
		boolean[] newEmpty = new boolean[internal.length + 1];
		System.arraycopy(internal, 0, newInternal, 0, internal.length);
		System.arraycopy(missing, 0, newMissing, 0, missing.length);
		System.arraycopy(empty, 0, newEmpty, 0, empty.length);
		newInternal[last] = ((Number)newEntry).intValue();
		internal = newInternal;
		this.setMissingValues(newMissing);
		empty = newEmpty;
	}

	/**
	 * Removes an entry from the Column, at pos.  The number of rows in this
	* column will decrease by one.
	 * @param pos the position to remove
	 * @return a Integer representation of the removed int
	 */
	public Object removeRow (int pos) {
		int removed = internal[pos];
		System.arraycopy(internal, pos + 1, internal, pos, internal.length -
				(pos + 1));
		System.arraycopy(missing, pos + 1, missing, pos, internal.length -
				(pos + 1));
		System.arraycopy(empty, pos + 1, empty, pos, internal.length -
				(pos + 1));
		int newInternal[] = new int[internal.length - 1];
		boolean newMissing[] = new boolean[internal.length-1];
		boolean newEmpty[] = new boolean[internal.length-1];
		System.arraycopy(internal, 0, newInternal, 0, internal.length - 1);
		System.arraycopy(missing, 0, newMissing, 0, internal.length - 1);
		System.arraycopy(empty, 0, newEmpty, 0, internal.length - 1);
		internal = newInternal;
		this.setMissingValues(newMissing);
		empty = newEmpty;
		return  new Integer(removed);
	}

	/**
	 * Inserts a new entry in the Column at position pos.
	 * All elements from pos to capacity will be moved up one.
	 * @param newEntry an Integer wrapped int as the newEntry to insert
	 * @param pos the position to insert at
	 */
	public void insertRow (Object newEntry, int pos) {
		if (pos > getNumRows()) {
			addRow(newEntry);
			return;
		}
		int[] newInternal = new int[internal.length + 1];
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
		newInternal[pos] = ((Integer)newEntry).intValue();
		internal = newInternal;
		this.setMissingValues(newMissing);
		empty = newEmpty;
	}

	/**
	 * Swaps two entries in the Column
	 * @param pos1 the position of the 1st entry to swap
	 * @param pos2 the position of the 2nd entry to swap
	 */
	public void swapRows (int pos1, int pos2) {
		int d1 = internal[pos1];
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
	 * Get a copy of this Column, reordered, based on the input array of indices.
	 * Does not overwrite this Column.
	 * @param newOrder an array of indices indicating a new order
	* @return a copy of this column, re-ordered
	 */
	public Column reorderRows (int[] newOrder) {
		int[] newInternal = null;
		boolean[] newMissing = null;
		boolean[] newEmpty = null;
		if (newOrder.length == internal.length) {
			newInternal = new int[internal.length];
			newMissing=new boolean[internal.length];
			newEmpty=new boolean[internal.length];
			for (int i = 0; i < internal.length; i++) {
				newInternal[i] = internal[newOrder[i]];
				newMissing[i] = missing[newOrder[i]];
				newEmpty[i] = empty[newOrder[i]];
			}
		}
		else
			throw  new ArrayIndexOutOfBoundsException();
		/*IntColumn ic = new IntColumn(newInternal);
		ic.setLabel(getLabel());
		ic.setComment(getComment());
		ic.setScalarEmptyValue(getScalarEmptyValue());
		ic.setScalarMissingValue(getScalarMissingValue());
		ic.setNominalEmptyValue(getNominalEmptyValue());
		ic.setNominalMissingValue(getNominalMissingValue());
		*/
		IntColumn ic = new IntColumn(newInternal, newMissing, newEmpty, getLabel(), getComment());
		return  ic;
	}

	/**
	 * Compare the values of the object passed in and pos. Return 0 if they
	 * are the same, greater than zero if element is greater,
	* and less than zero if element is less.
	 * @param element the object to be passed in should be a subclass of Number
	 * @param pos the position of the element in Column to be compared with
	 * @return a value representing the relationship- >, <, or == 0
	 */
	public int compareRows (Object element, int pos) {
		int d1 = ((Number)element).intValue();
		int d2 = internal[pos];
		if (d1 > d2)
			return  1;
		else if (d1 < d2)
			return  -1;
		return  0;
	}

	/**
	 * Compare pos1 and pos2 positions in the Column. Return 0 if they
	 * are the same, greater than zero if pos1 is greater,
	* and less than zero if pos1 is less.
	 * @param pos1 the position of the first element to compare
	 * @param pos2 the position of the second element to compare
	 * @return a value representing the relationship- >, <, or == 0
	 */
	public int compareRows (int pos1, int pos2) {
		int d1 = internal[pos1];
		int d2 = internal[pos2];
		/*if (d1 == scalarEmptyValue) {
			if (d2 == scalarEmptyValue)
				return  0;
			else
				return  -1;
		}
		else if (d2 == scalarEmptyValue)
			return  1;

		if(d1 == scalarMissingValue) {
			if(d2 == scalarMissingValue)
				return 0;
			else
				return -1;
		}
		else if(d2 == scalarMissingValue)
			return 1;
		*/

		if (d1 > d2)
			return  1;
		else if (d1 < d2)
			return  -1;
		return  0;
	}

	//////////////////////////////////////
	/**
	 * Given an array of ints, will remove the positions in the Table
	 * which are indicated by the ints in the array.
	 * @param indices the int array of remove indices
	 */
	public void removeRowsByIndex (int[] indices) {
		HashSet toRemove = new HashSet(indices.length);
		for (int i = 0; i < indices.length; i++) {
			Integer id = new Integer(indices[i]);
			toRemove.add(id);
		}
		int newInternal[] = new int[internal.length - indices.length];
		boolean newMissing[] = new boolean[internal.length - indices.length];
		boolean newEmpty[] = new boolean[internal.length - indices.length];
		int newIntIdx = 0;
		for (int i = 0; i < getNumRows(); i++) {
			// check if this row is in the list of rows to remove
			// if this row is not in the list, copy it into the new internal
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

	//////////////////////////////////////
	/**
	 * Sort the elements in this column.
	 * @exception NotSupportedException when sorting is not supported
	 */
	public void sort () {
		sort(null);
	}

	/**
	 * Sort the elements in this column, and swap the rows in the table
	 * we are a part of.
	 * @param t the VerticalTable to swap rows for
	 * @exception NotSupportedException when sorting is not supported
	 */
	public void sort (MutableTable t) {
		internal = doSort(internal, 0, internal.length - 1, t);
	}

	/**
	  * Sort the elements in this column starting with row 'begin' up to row 'end',
	  * and swap the rows in the table  we are a part of.
	  * @param t the VerticalTable to swap rows for
	  * @param begin the row no. which marks the beginnig of the  column segment to be sorted
	  * @param end the row no. which marks the end of the column segment to be sorted
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
	 * Implement the quicksort algorithm.  Partition the array and
	 * recursively call doSort.
	 * @param A the array to sort
	 * @param p the beginning index
	 * @param r the ending index
	 * @param t the VerticalTable to swap rows for
	* @return a sorted array of ints
	 */
	private int[] doSort (int[] A, int p, int r, MutableTable t) {
		if (p < r) {
			int q = partition(A, p, r, t);
			doSort(A, p, q, t);
			doSort(A, q + 1, r, t);
		}
		return  A;
	}

	/**
	 * Rearrange the subarray A[p..r] in place.
	 * @param A the array to rearrange
	 * @param p the beginning index
	 * @param r the ending index
	 * @param t the Table to swap rows for
	* @return the partition point
	 */
	final private int partition (int[] A, int p, int r, MutableTable t) {
		int x = A[p];
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
				} while (this.isValueMissing(j) || (A[j] > x));
				do {
					i++;
				} while (!this.isValueMissing(i) && (A[i] < x));
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
/*IntColumn*/

