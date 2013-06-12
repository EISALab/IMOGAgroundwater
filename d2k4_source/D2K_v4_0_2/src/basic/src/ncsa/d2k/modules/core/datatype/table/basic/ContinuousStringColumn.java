package ncsa.d2k.modules.core.datatype.table.basic;

import ncsa.d2k.modules.core.datatype.table.*;

import java.io.*;

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
final public class ContinuousStringColumn extends ContinuousCharArrayColumn {

	//static final long serialVersionUID = 7989129781969763787L;
	static final long serialVersionUID = 3909060290164419627L;

	/**
	 * Create a new StringColumn
	 * @param initialLength the initial number of rows
	 * @param initialSize the initial size of the internal buffer
	 */
	public ContinuousStringColumn(int initialLength, int initialSize) {
		super(initialLength, initialSize, false);
		type = ColumnTypes.STRING;
	}

	/**
	 * Create a new StringColumn
	 * @param initialLength the initial number of rows
	 * @param initialSize the initial size of the internal buffer
	 */
	public ContinuousStringColumn(int initialLength, int initialSize, boolean fill) {
		super(initialLength, initialSize, fill);
		type = ColumnTypes.STRING;
	}

	/**
	 * Create a new StringColumn with the specified number of rows and
	 * the default initial size for the internal buffer.
	 * @param initialLength the initial number of rows
	 */
	public ContinuousStringColumn(int initialLength) {
		this(initialLength, DEFAULT_INITIAL_SIZE, false);
		type = ColumnTypes.STRING;
	}

	/**
	 * Create a new StringColumn with the specified number of rows and
	 * the default initial size for the internal buffer.
	 * @param initialLength the initial number of rows
	 */
	public ContinuousStringColumn(int initialLength, boolean fill) {
		this(initialLength, DEFAULT_INITIAL_SIZE, fill);
		type = ColumnTypes.STRING;
	}

	/**
	 * Create a new StringColumn with zero rows and default size
	 * for the internal buffer
	 */
	public ContinuousStringColumn() {
		this(0, DEFAULT_INITIAL_SIZE);
		type = ColumnTypes.STRING;
	}

	public ContinuousStringColumn(boolean fill) {
		this(0, DEFAULT_INITIAL_SIZE, fill);
		type = ColumnTypes.STRING;
	}

	/**
	 * Create a new StringColumn with the specified data
	 * @param data the internal buffer
	 * @param pointers the pointers into the internal buffer
	 /
	public StringColumn(char[] data, int[] pointers) {
		super(data, pointers);
	}*/

	/**
	 * Create a new StringColumn and insert all items in intern
	 * @param intern the Strings to insert
	 */
	public ContinuousStringColumn(String [] intern) {
		this();
		for(int i = 0; i < intern.length; i++)
			setString(intern[i], i);
		type = ColumnTypes.STRING;
	}

	/**
	 * Create an exact copy of this column.
	 * @return A copy of this column
	 */
	public Column copy() {
        ContinuousStringColumn bac;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            byte buf[] = baos.toByteArray();
            oos.close();
            ByteArrayInputStream bais = new ByteArrayInputStream(buf);
            ObjectInputStream ois = new ObjectInputStream(bais);
            bac = (ContinuousStringColumn)ois.readObject();
            ois.close();
            return  bac;
        } catch (Exception e) {
            bac = new ContinuousStringColumn(getNumRows());
            for (int i = 0; i < getNumRows(); i++) {
				String orig = getString(i);
                bac.setString(new String(orig), i);
            }
            bac.setLabel(getLabel());
            bac.setComment(getComment());
            return  bac;
        }
	}
	public Object getRow(int i) {
		return getString(i);
	}

	public Object getObject(int i) {
		return getString(i);
	}

    /**
     * Set the entry at pos to be newEntry.  If newEntry is a
	 * String, set it.  If newEntry is a byte[] or char[],
	 * convert them to String and insert.  Otherwise, call to
	 * toString() method on o and insert that String.
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
     Sets the entry at the given position to newEntry
     @param newEntry a new entry
     @param pos the position to set
     */
    public void setRow (Object newEntry, int pos) {
		//insertChars((char[])newEntry, pos);
		/*if(newEntry instanceof byte[])
			setBytes((byte[])newEntry, pos);
		else if(newEntry instanceof char[])
			setChars((char[])newEntry, pos);
			//insertBytes(new String((char[])newEntry).getBytes(), pos);
		else
			//insertBytes(newEntry.toString().getBytes(), pos);
			setChars(newEntry.toString().toCharArray(), pos);
		*/
		setObject(newEntry, pos);
    }

    /**
     Appends the new entry to the end of the Column.
     @param newEntry a new entry
     */
    public void addRow (Object newEntry) {
		if(newEntry instanceof byte[])
			appendChars(toCharArray((byte[])newEntry));
		else if(newEntry instanceof char[])
			appendChars((char[])newEntry);
		else
			appendChars(newEntry.toString().toCharArray());
    }

    /**
     Removes an entry from the Column, at pos.
     All entries from pos+1 will be moved back 1 position
     @param row the position to remove
     @return the removed object
     */
    public Object removeRow (int row) {
		char[] removed = (char[])super.removeRow(row);
		return new String(removed);
    }

    /**
     Compare the values of the element passed in and pos. Return 0 if they
     are the same, greater than 0 if element is greater, and less than 0 if element is less.
     @param element the element to be passed in and compared
     @param pos the position of the element in Column to be compare with
     @return a value representing the relationship- >, <, or == 0
     */
    public int compareRows (Object element, int pos) {
		return compareStrings(element.toString(), getString(pos));
    }

    /**
     Compare pos1 and pos2 positions in the Column. Return 0 if they
     are the same, greater than 0 if pos1 is greater, and less than 0 if pos1 is less.
     @param pos1 the position of the first element to compare
     @param pos2 the position of the second element to compare
     @return a value representing the relationship- >, <, or == 0
     */
    public int compareRows (int pos1, int pos2) {
		return compareStrings(getString(pos1), getString(pos2));
    }

	private int compareStrings(String s1, String s2) {
		return s1.compareTo(s2);
	}

    /**
     Rearrange the subarray A[p..r] in place.
     @param A the array to rearrange
     @param p the beginning index
     @param r the ending index
     @param t the VerticalTable to swap rows for
	 @return the new partition point
     */
    protected int partition (int p, int r, MutableTable t) {
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
}
