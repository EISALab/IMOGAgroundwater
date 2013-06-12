package ncsa.d2k.modules.core.datatype.table.basic;

import ncsa.d2k.modules.core.datatype.table.*;

import java.io.*;
import java.util.*;
//import ncsa.util.*;
import ncsa.d2k.modules.core.datatype.table.util.ByteUtils;

/**
 * A TextualColumn that keeps its data as a continuous array of chars.  This
 * minimizes the space requirements to hold the data.  The data is kept as a
 * buffer of chars with a secondary array of pointers into the buffer.
 * <br><br>
 * This column is efficient in storing and retrieving textual data.  Insertion
 * and deletion methods may require an expansion or compaction of the internal
 * buffer.
 * <br><br>
 * The buffer will allocate extra space when an insertion requires more
 * space than the size of the buffer.  This extra space can be removed using
 * the trim() method.
 * <br><br>
 * The buffer will compact itself when a row is removed from this column.  The
 * space freed up from the removal will not be freed until trim() is called.
 */
public class ContinuousCharArrayColumn extends MissingValuesColumn implements TextualColumn {

	static final long serialVersionUID = -5647877299627985582L;

	/** the internal buffer */
	private char[] internal;
	/** the pointers to the rows */
	private int[] rowPtrs;
	/** the number of rows in this column */
	private int numRows;
	/** the default size for the internal buffer */
	protected static final int DEFAULT_INITIAL_SIZE = 2048;
	/** the multiple used to increment the size of the internal buffer */
	private float capacityIncrement = 1.3f;
	private boolean[] empty = null;
	/**
	 * Create a new ContinuousCharArrayColumn
	 * @param initialLength the initial number of rows
	 * @param initialSize the initial size of the internal buffer
	 */
	public ContinuousCharArrayColumn(int initialLength, int initialSize) {
		/*internal = new char[initialSize];
		rowPtrs = initializeArray(initialLength);
		numRows = 0;
		//for(int i = 0; i < initialLength; i++)
		//	appendChars(new char[0]);
		*/
		this(initialLength, initialSize, false);
	}

	/**
	 * Create a new ContinuousCharArrayColumn
	 * @param initialLength the initial number of rows
	 * @param initialSize the initial size of the internal buffer
	 * @param fill true if each row should be filled with a blank entry
	 */
	public ContinuousCharArrayColumn(int initialLength, int initialSize, boolean fill) {
		internal = new char[initialSize];
		rowPtrs = initializeArray(initialLength);
		numRows = 0;
		if(fill)
			for(int i = 0; i < initialLength; i++)
				appendChars(new char[0]);
		setIsNominal(true);
		type = ColumnTypes.CHAR_ARRAY;
		//ANCA added lines below
		missing = new boolean[initialSize];
		empty = new boolean[initialSize];
	}

	/**
	 * Create a ContinuousCharArrayColumn with the specified number of rows and
	 * the default size for the internal buffer.
	 * @param initialLength the initial number of rows
	 */
	public ContinuousCharArrayColumn(int initialLength) {
		this(initialLength, DEFAULT_INITIAL_SIZE);
	}

	/**
	 * Create a ContinuousCharArrayColumn with the specified number of rows and
	 * the default size for the internal buffer.
	 * @param initialLength the initial number of rows
	 * @param fill true if each row should be filled with a blank entry
	 * */
	public ContinuousCharArrayColumn(int initialLength, boolean fill) {
		this(initialLength, DEFAULT_INITIAL_SIZE, fill);
	}

	/**
	 * Create a new ContinousCharArrayColumn with zero rows and default size
	 * for the internal buffer
	 */
	public ContinuousCharArrayColumn() {
		this(0, DEFAULT_INITIAL_SIZE);
	}

	/**
	 * Create a new ContinousCharArrayColumn with zero rows and default size
	 * for the internal buffer
	 * @param fill true if each row should be filled with a blank entry
	 */
	public ContinuousCharArrayColumn(boolean fill) {
		this(0, DEFAULT_INITIAL_SIZE, fill);
	}

	/**
	 * Create a new ContinousCharArrayColumn with the specified data
	 * @param data the internal buffer
	 * @param pointers the pointers into the internal buffer
	 */
	public ContinuousCharArrayColumn(char[] data, int[] pointers) {
		internal = data;
		rowPtrs = pointers;
		numRows = getNumEntries();
		setIsNominal(true);
		type = ColumnTypes.CHAR_ARRAY;
	}

    public ContinuousCharArrayColumn(char[][] data) {
		for(int i = 0; i < data.length; i++)
			setChars(data[i], i);
		setIsNominal(true);
		type = ColumnTypes.CHAR_ARRAY;
    }

	/**
	 * Create an exact copy of this column.
	 * @return an exact copy of this column
	 */
	public Column copy() {
        ContinuousCharArrayColumn bac;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            byte buf[] = baos.toByteArray();
            oos.close();
            ByteArrayInputStream bais = new ByteArrayInputStream(buf);
            ObjectInputStream ois = new ObjectInputStream(bais);
            bac = (ContinuousCharArrayColumn)ois.readObject();
            ois.close();
            return  bac;
        } catch (Exception e) {
            bac = new ContinuousCharArrayColumn(getNumRows());
            for (int i = 0; i < getNumRows(); i++) {
                char orig[] = getChars(i);
                char res[] = new char[orig.length];
                for (int j = 0; j < orig.length; j++)
                    res[j] = orig[j];
                bac.setChars(res, i);
            }
            bac.setLabel(getLabel());
            bac.setComment(getComment());
			//bac.setScalarEmptyValue(getScalarEmptyValue());
			//bac.setScalarMissingValue(getScalarMissingValue());
			//bac.setNominalEmptyValue(getNominalEmptyValue());
			//bac.setNominalMissingValue(getNominalMissingValue());
            return  bac;
        }
	}

	protected final char[] toCharArray(byte[] bytes) {
		char[] chars = new char[bytes.length];
		for(int i = 0; i < bytes.length; i++)
			chars[i] = (char)bytes[i];
		return chars;
	}

	/**
	 * Create a new int array with all elements set to -1.
	 * @param size the size of the array
	 * @return a new int array with all elements set to -1
	 */
	private final int[] initializeArray(int size) {
		int[] retVal = new int[size];
		for(int i = 0; i < retVal.length; i++)
			retVal[i] = -1;
		return retVal;
	}

	/**
	 * Multiply the current capacity by the capacity increment until
	 * it is greater than minSize
	 * @param minSize the minimum size the internal buffer needs to be
	 * @return the new capacity
	 */
	private int getNewCapacity(int minSize) {
		int newcap = internal.length;
		while(newcap < minSize)
			newcap = (int)Math.ceil(capacityIncrement*newcap);
		return newcap;
	}

	/**
	 * Insert an array of chars into internal at the specified row. Any item
	 * that was previously at this row will be overwritten.  If the row is
	 * greater than the number of rows currently, then append the row to the
	 * end of the column.
	 * @param b the array of chars to insert
	 * @param row the row that the chars will represent
	 */
	protected void insertChars(char[] b, int row) {
		if(row >= numRows)
			appendChars(b);
		else {
			char[] newinternal;
			int[] newrowPtrs;

			if(row == 0) {
				int minCapacity = rowPtrs[numRows-1]-rowPtrs[0]+b.length;

				// increment the size of the storage if necessary
				if(minCapacity > internal.length) {
					int newcap = getNewCapacity(minCapacity);
					newinternal = new char[newcap];
				}
				// otherwise keep it the same size as internal
				else
					newinternal = new char[internal.length];

				newrowPtrs = initializeArray(rowPtrs.length);

				// copy the new item
				System.arraycopy(b, 0, newinternal, 0, b.length);
				newrowPtrs[0] = b.length;

				// copy the old items over
				System.arraycopy(internal, rowPtrs[0], newinternal,
					newrowPtrs[0], rowPtrs[numRows-1]-rowPtrs[0]);
				for(int i = 1; i < numRows; i++)
					newrowPtrs[i] = rowPtrs[i]-rowPtrs[0]+b.length;

				internal = newinternal;
				rowPtrs = newrowPtrs;
			}
			else {
				int minCapacity = rowPtrs[numRows-1]-rowPtrs[row]+rowPtrs[row-1]+b.length;
				if(minCapacity > internal.length) {
					int newcap = getNewCapacity(minCapacity);
					newinternal = new char[newcap];
				}
				else
					newinternal = new char[internal.length];

				newrowPtrs = initializeArray(rowPtrs.length);

				// copy the rows before the insertion
				System.arraycopy(internal, 0, newinternal, 0, rowPtrs[row-1]);
				System.arraycopy(rowPtrs, 0, newrowPtrs, 0, row);

				// make the insertion
				System.arraycopy(b, 0, newinternal, rowPtrs[row-1], b.length);
				newrowPtrs[row] = newrowPtrs[row-1]+b.length;

				// copy the rows after the insertion
				System.arraycopy(internal, rowPtrs[row], newinternal,
					newrowPtrs[row], rowPtrs[numRows-1]-rowPtrs[row]);

				// the size of the item that we replaced
				int removedsize = rowPtrs[row]-rowPtrs[row-1];
				// reassign the rowPtrs
				for(int i = row+1; i < numRows; i++)
					newrowPtrs[i] = rowPtrs[i]-removedsize+b.length;

				internal = newinternal;
				rowPtrs = newrowPtrs;
			}
		}
	}

	/**
	 * Append chars to the end of internal, making a new row.
	 * @param b the array of chars to append
	 */
	protected void appendChars(char[] b) {
		if(numRows == 0) {
			if(numRows == 0) {
				// increment the capacity of internal if necessary
				if(b.length > internal.length) {
					int newcap = getNewCapacity(b.length);
					internal = new char[newcap];
				}

				// increment the capacity of rowPtrs if necessary
				if(rowPtrs.length == 0) {
					rowPtrs = initializeArray(10);
				}

				System.arraycopy(b, 0, internal, 0, b.length);
				rowPtrs[0] = b.length;
				numRows++;
			}
		}
		else {
			// increase the size of internal if necessary
			int minCapacity = b.length+rowPtrs[numRows-1];
			if( minCapacity > internal.length) {
				int newcap = getNewCapacity(minCapacity);

				char[] newinternal = new char[newcap];
				System.arraycopy(internal, 0, newinternal, 0, internal.length);
				internal = newinternal;
			}

			// increase the size of the rowPtrs array if necessary
			if(numRows == rowPtrs.length) {
				// increment the size by the capacity increment
				int [] newrowPtrs = initializeArray((int)Math.ceil((capacityIncrement)*rowPtrs.length));

				System.arraycopy(rowPtrs, 0, newrowPtrs, 0, rowPtrs.length);
				rowPtrs = newrowPtrs;
			}

			// now copy the bytes into internal
			System.arraycopy(b, 0, internal, rowPtrs[numRows-1], b.length);
			// set the row pointer
			rowPtrs[numRows] = rowPtrs[numRows-1]+b.length;
			// increment number of rows
			numRows++;
		}
	}

	/**
	 * Returns the internal representation of the data.
	 * @return the internal representation of the data.
	 */
	public Object getInternal () {
		return internal;
	}

	/**
	 * Get the chars that make up a row
	 * @param row the row number
	 * @return the bytes that make up this row
	 */
	private char[] getInternalChars(int row) {
		if(row == 0) {
			char[] retVal = new char[rowPtrs[0]];
			System.arraycopy(internal, 0, retVal, 0, rowPtrs[0]);
			return retVal;
		}
		else {
			int size = sizeOf(row);
			char[] retVal = new char[size];
			System.arraycopy(internal, rowPtrs[row-1], retVal, 0, size);
			return retVal;
		}
	}

	/**
	 * Get the number of chars that a row takes up.
	 * @param row the row of interest
	 * @return the number of bytes that make up that particular row
	 */
	private int sizeOf(int row) {
		if(row == 0)
			return rowPtrs[0];
		else
			return rowPtrs[row]-rowPtrs[row-1];
	}

	/**
	 * Get the value at row as a String
	 * @param row the row of the table
	 * @return the value at row as a String
	 */
	public String getString(int row) {
		return new String(getInternalChars(row));
	}

	/**
	 * Set the value at row as the bytes from s.
	 * @param s a String
	 * @param row the row of the table
	 */
	public void setString(String s, int row) {
		setChars(s.toCharArray(), row);
	}

	/**
	 * Get the value at row as an int.
	 * @param row a row of this column
	 * @return the value at row as an int
	 */
	public int getInt(int row) {
		return Integer.parseInt(getString(row));
	}

	/**
	 * Set the value at row as the chars that make up the String representation
	 * of i.
	 * @param i the int to put in the column
	 * @param row the row to insert the entry in
	 */
	public void setInt(int i, int row) {
		insertChars(Integer.toString(i).toCharArray(), row);
	}

	/**
	 * Get the value at row as a short.
	 * @param row a row of this column
	 * @return the value at row as a short
	 */
	public short getShort(int row) {
		return Short.parseShort(getString(row));
	}

	/**
	 * Set the value at row as the chars that make up the String representation
	 * of s.
	 * @param s the short to put in the column
	 * @param row the row to insert the entry in
	 */
	public void setShort(short s, int row) {
		insertChars(Short.toString(s).toCharArray(), row);
	}

	/**
	 * Get the value at row as a long.
	 * @param row a row of this column
	 * @return the value at row as a long
	 */
	public long getLong(int row) {
		return Long.parseLong(getString(row));
	}

	/**
	 * Set the value at row as the chars that make up the String representation
	 * of l.
	 * @param l the long to put in the column
	 * @param row the row to insert the entry in
	 */
	public void setLong(long l, int row) {
		insertChars(Long.toString(l).toCharArray(), row);
	}

	/**
	 * Get the value at row as a double.
	 * @param row a row of this column
	 * @return the value at row as a double
	 */
	public double getDouble(int row) {
		return Double.parseDouble(getString(row));
	}

	/**
	 * Set the value at row as the chars that make up the String representation
	 * of d.
	 * @param d the double to put in the column
	 * @param row the row to insert the entry in
	 */
	public void setDouble(double d, int row) {
		insertChars(Double.toString(d).toCharArray(), row);
	}

	/**
	 * Get the value at row as a float.
	 * @param row a row of this column
	 * @return the value at row as a float
	 */
	public float getFloat(int row) {
		return Float.parseFloat(getString(row));
	}

	/**
	 * Set the value at row as the chars that make up the String representation
	 * of f.
	 * @param f the double to put in the column
	 * @param row the row to insert the entry in
	 */
	public void setFloat(float f, int row) {
		insertChars(Float.toString(f).toCharArray(), row);
	}

	/**
	 * Get the bytes at row.
	 * @param row a row of this column
	 * @return the value at row
	 */
	public byte[] getBytes(int row) {
		return getString(row).getBytes();
	}

	/**
	 * Set the value at row as b.
	 * @param b the bytes to put in the column
	 * @param row the row to insert the entry in
	 */
	public void setBytes(byte[] b, int row) {
		insertChars(toCharArray(b), row);
	}

	/**
	 * Get the bytes at row.
	 * @param row a row of this column
	 * @return the value at row
	 */
	public byte getByte(int row) {
		return getBytes(row)[0];
	}

	/**
	 * Set the value at row as b.
	 * @param b the bytes to put in the column
	 * @param row the row to insert the entry in
	 */
	public void setByte(byte b, int row) {
		byte[] by = new byte[1];
		by[0] = b;
		setBytes(by, row);
	}

	/**
	 * Get the value at row as an array of char.
	 * @param row a row of this column
	 * @return the value at row as an array of char
	 */
	public char[] getChars(int row) {
		return getInternalChars(row);
	}

	/**
	 * Set the value at row as c.
	 * @param c the bytes to put in the column
	 * @param row the row to insert the entry in
	 */
	public void setChars(char[] c, int row) {
		insertChars(c, row);
	}

	/**
	 * Get the value at row as an array of char.
	 * @param row a row of this column
	 * @return the value at row as an array of char
	 */
	public char getChar(int row) {
		return getChars(row)[0];
	}

	/**
	 * Set the value at row as c.
	 * @param c the bytes to put in the column
	 * @param row the row to insert the entry in
	 */
	public void setChar(char c, int row) {
		char[] cy = new char[1];
		cy[0] = c;
		setChars(cy, row);
	}

	/**
	 * Get the chars at row.
	 * @param row a row of this column
	 * @return the value at row
	 */
	public Object getObject(int row) {
		return getInternalChars(row);
	}

    /**
     * Set the entry at pos to be newEntry.  If newEntry is a
     * char[] or char[], call the appropriate method.  Otherwise convert
	 * o to a String and call the toCharArray() method.
     * @param o the new item
     * @param row the position
     */
    public void setObject (Object o, int row) {
        if (o instanceof byte[])
			setBytes((byte[])o, row);
        else if (o instanceof char[])
			setChars((char[])o, row);
        else
			setChars(o.toString().toCharArray(), row);
    }

    /**
    	Get the value at pos as a boolean
    	@param row the position
    	@return the value at pos as a byte array
     */
	public boolean getBoolean(int row) {
		return ByteUtils.toBoolean(new String(getInternalChars(row)).getBytes());
	}

    /**
    	Set the value at pos to be newEntry
    	@param b the new item
    	@param row the position
     */
	public void setBoolean(boolean b, int row) {
		insertChars(new String(ByteUtils.writeBoolean(b)).toCharArray(), row);
	}

	/**
	 * Trim any excess storage from the internal buffer.
	 */
	public void trim() {
		int totalSize = rowPtrs[numRows-1];
		char[] newintern = new char[totalSize];
		int[] newrowPtrs = new int[numRows];
		System.arraycopy(internal, 0, newintern, 0, totalSize);
		System.arraycopy(rowPtrs, 0, newrowPtrs, 0, numRows);
		internal = newintern;
		rowPtrs = newrowPtrs;
	}

    //////////////////////////////////////
    //// Accessing Metadata
    /**
     Return the count for the number of non-null entries.
     This variable is recomputed each time...as keeping
     track of it could be very time inefficient.
     @return this ByteArrayColumn's # of entries
     */
    public int getNumEntries () {
        int numEntries = 0;
        for (int i = 0; i < rowPtrs.length; i++)
        //ANCA added is isValueMissing, isValueEmpty
            if (rowPtrs[i] != -1 && !isValueMissing(i)&& !isValueEmpty(i))
               numEntries++;
        return  numEntries;
    }

	/**
	 * Get the number of rows that this column can hold.  Same as getCapacity().
	 * @return the number of rows this column can hold
	 */
	public int getNumRows() {
		return numRows;
	}

    /**
     Set a new capacity for this Column.  The capacity is its potential max
	 number of entries.  If numEntries is greater than newCapacity,  the Column
     will be truncated.
     @param newCapacity a new capacity
     */
    public void setNumRows (int newCapacity) {
        /*if (internal != null) {
            char[][] newInternal = new byte[newCapacity][];
            if (newCapacity > internal.length)
                newCapacity = internal.length;
            System.arraycopy(internal, 0, newInternal, 0, newCapacity);
            internal = newInternal;
        }
        else
            internal = new byte[newCapacity][];
		*/
		throw new RuntimeException("This method is not yet implemented for ContinuousCharArrayColumn.");
    }

    /**
     Get an entry from the Column at the indicated position.
     @param pos the position
     @return the entry at pos
     */
    public Object getRow (int pos) {
        return getInternalChars(pos);
    }

    /**
     Get a subset of this Column, given a start position and length.
     Only the byteword references are copied, so if you change their contents,
     they change, but if you reassign the reference, the Column is not affected.
     @param pos the start position for the subset
     @param len the length of the subset
     @return a subset of this Column
     */
    public Column getSubset (int pos, int len) {
		//int size = rowPtrs[pos+len]-rowPtrs[pos];
		//char[] newinternal = new char[size];
		//int [] newrowPtrs = new int[len-pos];
		//System.arraycopy(internal, pos, newinternal, 0, size);
		//System.arraycopy(rowPtrs, pos, newrowPtrs, 0, len);

		//return new ContinuousCharArrayColumn(newinternal, newrowPtrs);
		ContinuousCharArrayColumn cac = new ContinuousCharArrayColumn(len);
		int idx = 0;
		for(int i = pos; i < pos+len; i++) {
			cac.setChars(getChars(i), idx);
			idx++;
		}
		cac.setLabel(getLabel());
		//cac.setType(getType());
		cac.setComment(getComment());
		//cac.setScalarEmptyValue(getScalarEmptyValue());
		//cac.setScalarMissingValue(getScalarMissingValue());
		//cac.setNominalEmptyValue(getNominalEmptyValue());
		//cac.setNominalMissingValue(getNominalMissingValue());
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
    public Column getSubset (int[] rows) {
        char[][] subset = new char[rows.length][];
        boolean[] newMissing = new boolean[rows.length];
        boolean[] newEmpty = new boolean[rows.length];
        for(int i = 0; i < rows.length; i++) {
          subset[i] = getChars(rows[i]);
          newMissing[i] = missing[rows[i]];
          newEmpty[i] = empty[rows[i]];
        }

//                System.arraycopy(missing, pos, newMissing, 0, len);
//                System.arraycopy(empty, pos, newEmpty, 0, len);
        /*BooleanColumn bc = new BooleanColumn(subset);
                bc.missing = newMissing;
                bc.empty = newEmpty;
        bc.setLabel(getLabel());
        bc.setComment(getComment());
        */
        //BooleanColumn bc = new BooleanColumn(subset, newMissing, newEmpty, getLabel(), getComment());
        ContinuousCharArrayColumn cbac = new ContinuousCharArrayColumn(subset);
        cbac.setMissingValues(newMissing);
        cbac.empty = newEmpty;
        cbac.setLabel(getLabel());
        cbac.setComment(getComment());
        return cbac;
    }


    /**
     Get a reference to the internal representation of this Column.
     Changes made to this object will be reflected in the Column.
     @return the internal representation of this Column.
     /
    public Object getInternal () {
        return this.internal;
    }*/

    /**
     Set the reference to the internal representation of this Column.
     If a miscompatable Object is passed in, the most common Exception
     thrown is a ClassCastException.
     @param newInternal a new internal representation for this Column
     /
    public void setInternal (Object newInternal) {
        //this.internal = (char[][])newInternal;

		//System.out.println("Not impelmented...");
		throw new RuntimeException("This method is not yet implemented for ContinousCharArrayColumn.");
    }*/

    /**
     Sets the entry at the given position to newEntry
     @param newEntry a new entry
     @param pos the position to set
     */
    public void setRow (Object newEntry, int pos) {
		//insertChars((char[])newEntry, pos);
		/*if(newEntry instanceof byte[])
			insertChars(new String((byte[])newEntry).toCharArray(), pos);
		else if(newEntry instanceof String)
			insertChars( ((String)newEntry).toCharArray(), pos);
		else if(newEntry instanceof char[])
			insertChars((char[])newEntry, pos);
		else
			insertChars(newEntry.toString().toCharArray(), pos);
		*/
		setObject(newEntry, pos);
    }

    /**
     Append the new entry to the end of the Column.
     @param newEntry a new entry
     */
    public void addRow (Object newEntry) {
		if(newEntry instanceof byte[])
			appendChars(toCharArray((byte[])newEntry));
		else if(newEntry instanceof String)
			appendChars( ((String)newEntry).toCharArray());
		else if(newEntry instanceof char[])
			appendChars((char[])newEntry);
		else
			appendChars(newEntry.toString().toCharArray());
    }

    /**
     Remove an entry from the Column, at pos.
     All entries from pos+1 will be moved back 1 position.
     @param row the position to remove
     @return the removed object
     */
    public Object removeRow (int row) {
		char[] removed = getInternalChars(row);

		char[] newinternal = new char[internal.length];
		//int[] newrowPtrs = new int[rowPtrs.length];
		int[] newrowPtrs = initializeArray(rowPtrs.length);//new int[rowPtrs.length];

		if(row == 0) {
			// remove the first row
			System.arraycopy(internal, rowPtrs[0], newinternal, 0,
				rowPtrs[numRows-1]-rowPtrs[0]);
			for(int i = 0; i < numRows-1; i++)
				newrowPtrs[i] = rowPtrs[i+1]-rowPtrs[0];
		}
		else if(row == (numRows-1)) {
			// remove the last row
			rowPtrs[numRows-1] = -1;
			numRows--;
			return removed;
		}
		else {
			// copy in the beginning elements
			System.arraycopy(internal, 0, newinternal, 0, rowPtrs[row-1]);
			System.arraycopy(rowPtrs, 0, newrowPtrs, 0, row);

			// copy in everything after the row to remove
			System.arraycopy(internal, rowPtrs[row], newinternal,
				newrowPtrs[row-1], rowPtrs[numRows-1]-rowPtrs[row]);

			for(int i = row; i < newrowPtrs.length-1; i++)
				newrowPtrs[i] = rowPtrs[i+1]-sizeOf(row);
		}

		numRows--;
		internal = newinternal;
		rowPtrs = newrowPtrs;
		return removed;
    }

    /**
     * Inserts a new entry in the Column at position pos.
     * All elements from pos to capacity will be moved down one.
     * @param newEntry the newEntry to insert
     * @param row the position to insert at
     */
    public void insertRow (Object newEntry, int row) {
		char[] b;
		if(newEntry instanceof byte[])
			b = new String((byte[])newEntry).toCharArray();
		else if(newEntry instanceof char[])
			b = (char[])newEntry;
		else
			b = newEntry.toString().toCharArray();

		char[] newinternal;
		int[] newrowPtrs;

        int minCap;
        //ANCA added if condition
		if (numRows >0 ) minCap = rowPtrs[numRows-1]+b.length;
		else minCap = b.length;
		int newSize = internal.length;
		if(minCap > internal.length)
			newSize = getNewCapacity(minCap);

		newinternal = new char[newSize];

       
		// increase the size of the rowPtrs array if necessary
		if(numRows == rowPtrs.length) {
			if (numRows ==0)  newrowPtrs = initializeArray(newSize);
			// increment the size by the capacity increment
			newrowPtrs = initializeArray((int)Math.ceil((capacityIncrement)*rowPtrs.length));
		}
		else
			newrowPtrs = initializeArray(rowPtrs.length);

		if(row == 0) {
			// copy in the new element
			System.arraycopy(b, 0, newinternal, 0, b.length);
			newrowPtrs[0] = b.length;

			// copy in the remaining elements
			System.arraycopy(internal, 0, newinternal, newrowPtrs[row],
				rowPtrs[numRows-1]);

			// update the rowPtrs
			for(int i = row+1; i <= numRows; i++)
				newrowPtrs[i] = rowPtrs[i-1]+b.length;
		}
		else {
			// copy the rows before the insertion
			System.arraycopy(internal, 0, newinternal, 0, rowPtrs[row-1]);
			System.arraycopy(rowPtrs, 0, newrowPtrs, 0, row);

			// make the insertion
			System.arraycopy(b, 0, newinternal, rowPtrs[row-1], b.length);
			newrowPtrs[row] = newrowPtrs[row-1]+b.length;

			// copy all the remaining rows
			System.arraycopy(internal, rowPtrs[row-1], newinternal,
				newrowPtrs[row], rowPtrs[numRows-1]-rowPtrs[row-1]);

			for(int i = row+1; i <= numRows; i++) {
				newrowPtrs[i] = rowPtrs[i-1]+b.length;
			}

		}
		numRows++;
		internal = newinternal;
		rowPtrs = newrowPtrs;
    }

    /**
     Swap two entries in the Column.
     @param pos1 the position of the 1st entry to swap
     @param pos2 the position of the 2nd entry to swap
     */
    public void swapRows (int pos1, int pos2) {
		// VERY INEFFICIENT!!

		char[] b1 = (char[])removeRow(pos1);
		char[] b2 = (char[])removeRow(pos2);

		insertRow(b1, pos2);
		insertRow(b2, pos1);
    }

    /**
     Return a copy of this Column, reordered based on the input array of indexes.
     Does not overwrite this Column.
     @param newOrder an array of indices indicating a new order
	 @return a copy of this column, re-ordered
     */
    public Column reorderRows (int[] newOrder) {
		if(newOrder.length != numRows)
			throw new ArrayIndexOutOfBoundsException();
		char[] newinternal = new char[internal.length];
		int[] newrowPtrs = new int[rowPtrs.length];

		int curRow = 0;

		char[] entry = getInternalChars(newOrder[0]);
		int size = sizeOf(newOrder[0]);

		System.arraycopy(entry, 0, newinternal, 0, size);
		newrowPtrs[0] = size;
		curRow++;

		for(int i = 1; i < newOrder.length; i++) {
			entry = getInternalChars(newOrder[i]);
			size = sizeOf(newOrder[i]);

			System.arraycopy(entry, 0, newinternal, newrowPtrs[curRow-1],
				entry.length);
			newrowPtrs[curRow] = newrowPtrs[curRow-1]+entry.length;
			curRow++;
		}

		ContinuousCharArrayColumn cac = new ContinuousCharArrayColumn(newinternal, newrowPtrs);
		cac.setLabel(getLabel());
		cac.setComment(getComment());
		//cac.setScalarEmptyValue(getScalarEmptyValue());
		//cac.setScalarMissingValue(getScalarMissingValue());
		//cac.setNominalEmptyValue(getNominalEmptyValue());
		//cac.setNominalMissingValue(getNominalMissingValue());
		return cac;
    }

    /**
     Compare the values of the element passed in and pos. Return 0 if they
     are the same, greater than 0 if element is greater, and less than 0 if element is less.
     @param element the element to be passed in and compared
     @param pos the position of the element in Column to be compare with
     @return a value representing the relationship- >, <, or == 0
     */
    public int compareRows (Object element, int pos) {
		return compareChars((char[])element, getInternalChars(pos));
    }

    /**
     Compare pos1 and pos2 positions in the Column. Return 0 if they
     are the same, greater than 0 if pos1 is greater, and less than 0 if pos1 is less.
     @param pos1 the position of the first element to compare
     @param pos2 the position of the second element to compare
     @return a value representing the relationship- >, <, or == 0
     */
    public int compareRows (int pos1, int pos2) {
		char[] b1 = getInternalChars(pos1);
		char[] b2 = getInternalChars(pos2);
		return compareChars(b1, b2);
    }
	/**
	 * Add the specified number of blank rows.
	 * @param number number of rows to add.
	 */
	public void addRows (int number) {
		for (int i = 0 ; i < number ; i++) this.addRow("");
	}

    /**
	 * Compare two char arrays.
     * @param b1 the first char array to compare
     * @param b2 the second char array to compare
     * @return -1, 0, 1
     */
    private static int compareChars (char[] b1, char[] b2) {
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

		int oldNumRows = getNumRows();
		char[] newinternal = new char[internal.length];
		int[] newrowPtrs = new int[rowPtrs.length];

        int newIntIdx = 0;
		// do the first and second elements as special cases outside the loop
		//Integer x = (Integer)toRemove.get(new Integer(0));
		// not removing the row, copy it into newinternal
		//if(x == null) {
		if(!toRemove.contains(new Integer(0))) {
			System.arraycopy(internal, 0, newinternal, 0, rowPtrs[0]);
			newrowPtrs[0] = rowPtrs[0];
			newIntIdx++;
		}
		else
			numRows--;

		//x = (Integer)toRemove.get(new Integer(1));
		//if(x == null) {
		if(!toRemove.contains(new Integer(1))) {
			// we removed the first row
			char[] item = getInternalChars(1);
			int sz = sizeOf(1);
			if(newIntIdx == 0) {
				System.arraycopy(item, 0, newinternal, 0, sz);
				newrowPtrs[newIntIdx] = sz;
				newIntIdx++;
			}
			// the first row was not removed
			else {
				System.arraycopy(item, 0, newinternal, newrowPtrs[0], sz);
				newrowPtrs[newIntIdx] = newrowPtrs[0]+sz;
				newIntIdx++;
			}
		}
		else
			numRows--;

		// copy the new elements into newinternal if it is not one that we are
		// removing
        for (int i = 2; i < oldNumRows; i++) {
            // check if this row is in the list of rows to remove
            //x = (Integer)toRemove.get(new Integer(i));
            // if this row is not in the list, copy it into the new internal
            //if (x == null) {
			if(!toRemove.contains(new Integer(i))) {
				char[] item = getInternalChars(i);
				int size = sizeOf(i);

				if(newIntIdx == 0) {
					System.arraycopy(item, 0, newinternal, newrowPtrs[0],
						size);
					newrowPtrs[newIntIdx] = newrowPtrs[0]+size;
				}
				else {
					System.arraycopy(item, 0, newinternal, newrowPtrs[newIntIdx-1],
						size);
					newrowPtrs[newIntIdx] = newrowPtrs[newIntIdx-1]+size;
				}
				newIntIdx++;
            }
			else
				numRows--;
        }

		internal = newinternal;
		rowPtrs = newrowPtrs;
    }

    /**
     Sort the items in this column.
     @exception NotSupportedException when sorting is not supported
     */
    public void sort () {
        sort(null);
    }

    /**
     Sort the elements in this column, and swap the rows in the table
     we are a part of.
     @param t the Table to swap rows for
     @exception NotSupportedException when sorting is not supported
     */
    public void sort (MutableTable t) {
        doSort(/*internal,*/ 0, getNumRows()-1, t);
    }


    /**
       Sort the elements in this column starting with row 'begin' up to row 'end',
       and swap the rows in the table  we are a part of.
       @param t the VerticalTable to swap rows for
       @param begin the row no. which marks the beginnig of the  column segment to be sorted
       @param end the row no. which marks the end of the column segment to be sorted
       @exception NotSupportedException when sorting is not supported
    */
    public void sort(MutableTable t,int begin, int end)
    {
	doSort( begin, end, t);
    }



    /**
     Implement the quicksort algorithm.  Partition the array and
     recursively call doSort.
     @param p the beginning index
     @param r the ending index
     @param t the VerticalTable to swap rows for
     */
    private void doSort (/*char[] A,*/ int p, int r, MutableTable t) {
        if (p < r) {
            int q = partition(/*A,*/ p, r, t);
            doSort(/*A,*/ p, q, t);
            doSort(/*A,*/ q + 1, r, t);
        }
        //return  A;
    }

    /**
     Rearrange the subarray A[p..r] in place.
     @param p the beginning index
     @param r the ending index
     @param t the VerticalTable to swap rows for
	 @return the new partition point
     */
    private int partition (int p, int r, MutableTable t) {
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
				} while (this.isValueMissing(j) || compareRows(j, p) > 0);
				do {
					i++;
				} while (!this.isValueMissing(i) && compareRows(i, p) < 0);
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
