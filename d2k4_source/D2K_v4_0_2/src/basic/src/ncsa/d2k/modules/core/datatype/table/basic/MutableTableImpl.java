package ncsa.d2k.modules.core.datatype.table.basic;
import ncsa.d2k.modules.core.datatype.table.*;

import java.util.*;
import java.io.*;
/**
 * MutableTable defines methods used to mutate the contents of a Table.
 */
public class MutableTableImpl extends TableImpl implements MutableTable {

	static final long serialVersionUID = 2155712249436392195L;

	/** list of transformations performed. */
	ArrayList transformations = new ArrayList();

	/**
	* Create a new Table with zero columns.
	*/
	public MutableTableImpl() {
		super();
	}

	/**
	 * Create a new Table with the specified number of columns.  Space for the
	 * columns is created, but the columns themselves will be null.
	 * @param numColumns the initial number of columns
	 */
	public MutableTableImpl(int numColumns) {
		super(numColumns);
	}

	/**
	* Create a new Table with the specified columns.
	* @param c the initial columns
	*/
	public MutableTableImpl(Column[] c) {
		super(c);
	}
	/////////// Collect the transformations that were performed. /////////
	/**
		Add the transformation to the list.
		@param tm the Transformation that performed the reversable transform.
	 */
	public void addTransformation(Transformation tm) {
		transformations.add(tm);
	}

	/**
		Returns the list of all reversable transformations there were performed
		on the original dataset.
		@returns an ArrayList containing the Transformation which transformed the data.
	 */
	public List getTransformations() {
		return transformations;
	}

	/////////////////////////////////
	// Structural change.

	//
	/**
	 * Insert a new Column at the indicated position in this Table.  All
	 * subsequent Columns will be shifted.
	 * @param newColumn the new Column
	 * @param position the position at which to insert
	 */
	public void insertColumn(Column col, int position) {

		// Alloc a new array.
		Column[] cols = this.getColumns();
		int cnt = this.getColumns().length + 1;
		Column[] newColumns = new Column[cnt];

		// copy the columns befor the insertion point.
		System.arraycopy(cols, 0, newColumns, 0, position);

		// insert new columns.
		newColumns[position] = col;
		System.arraycopy(
			cols,
			position,
			newColumns,
			position + 1,
			cnt - position - 1);
		//ANCA replaced System.arraycopy(cols, position, newColumns, position+1, cnt - position);

		// add columns after insertion point.
		this.columns = newColumns;
	}

	/**
	 * Insert a new Column at the indicated position in this Table.  All
	 * subsequent Columns will be shifted.
	 * @param newColumn the new Column
	 * @param position the position at which to insert
	 */
	public void insertColumns(Column[] newcols, int position) {

		// Alloc a new array.
		int count = newcols.length;
		int cnt = this.columns.length + count;
		Column[] newColumns = new Column[cnt];

		// copy the columns befor the insertion point.
		System.arraycopy(this.columns, 0, newColumns, 0, position);

		// insert new columns.
		System.arraycopy(newcols, 0, newColumns, position, newcols.length);

		// add columns after insertion point.
		System.arraycopy(
			this.columns,
			position,
			newColumns,
			position + count,
			columns.length - position);
		this.columns = newColumns;
	}

	/**
	 * Set the column at where to the one passed in.
	 * @param col the column object.
	 * @param where where to put it.
	 */
	public void setColumn(Column col, int where) {
		columns[where] = col;
	}

	/**
	 * Add a new Column after the last occupied position in this Table.
	 * @param newColumn the Column to be added to the table
	 */
	public void addColumn(Column col) {

		// Allocate a new array.
		Column[] newColumns = new Column[columns.length + 1];

		// copy current columns.
		System.arraycopy(columns, 0, newColumns, 0, columns.length);
		newColumns[newColumns.length - 1] = col;
		columns = newColumns;
	}

	/**
	 * Add a new Column after the last occupied position in this Table.
	 * @param newColumn the Column to be added to the table
	 */
	public void addColumns(Column[] cols) {

		// Allocate a new array.
		int number = cols.length;
		int cnt = columns.length + number;
		Column[] newColumns = new Column[cnt];

		// copy current columns.
		System.arraycopy(columns, 0, newColumns, 0, columns.length);
		System.arraycopy(cols, 0, newColumns, columns.length, number);
		//ANCA replaced with above System.arraycopy(cols, columns.length, newColumns, 0, number);
		columns = newColumns;
	}

	/**
	 * Remove a Column from the Table.
	 * @param pos the position of the Column to remove
	 */
	public void removeColumn(int pos) {
		Column[] newColumns = new Column[columns.length - 1];
		System.arraycopy(columns, 0, newColumns, 0, pos);
		System.arraycopy(
			columns,
			pos + 1,
			newColumns,
			pos,
			newColumns.length - pos);
		columns = newColumns;
	}

	/**
		* Remove a range of columns from the Table.
		* @param start the start position of the range to remove
		* @param len the number to remove-the length of the range
		*/
	public void removeColumns(int start, int len) {
		Column[] newColumns = new Column[columns.length - len];
		System.arraycopy(columns, 0, newColumns, 0, start);
		System.arraycopy(
			columns,
			start + len,
			newColumns,
			start,
			newColumns.length - start);
		columns = newColumns;
	}

	/**
	 * Remove a row from this Table.
	 * @param pos the row to remove
	 */
	public void removeRows(int pos, int cnt) {
		for (int i = 0; i < getNumColumns(); i++) {
			getColumn(i).removeRows(pos, cnt);
		}
	}

	/**
	 * Remove a row from this Table.
	 * @param pos the row to remove
	 */
	public void removeRow(int pos) {
		for (int i = 0; i < getNumColumns(); i++) {
			getColumn(i).removeRow(pos);
		}
	}

	//////////////////////////////////////
	// Setter methods.
	//
	/**
	 * Set an Object in the Table.
	 * @param element the value to set
	 * @param row the row of the table
	 * @param column the column of the table
	 */
	public void setObject(Object element, int row, int column) {
		columns[column].setRow(element, row);
	}

	/**
	 * Set an int value in the Table.
	 * @param data the value to set
	 * @param row the row of the table
	 * @param column the column of the table
	 */
	public void setInt(int data, int row, int column) {
		columns[column].setInt(data, row);
	}

	/**
	 * Set a short value in the Table.
	 * @param data the value to set
	 * @param row the row of the table
	 * @param column the column of the table
	 */
	public void setShort(short data, int row, int column) {
		columns[column].setShort(data, row);
	}

	/**
	 * Set a long value in the Table.
	 * @param data the value to set
	 * @param row the row of the table
	 * @param column the column of the table
	 */
	public void setLong(long data, int row, int column) {
		columns[column].setLong(data, row);
	}

	/**
	 * Set a float value in the Table.
	 * @param data the value to set
	 * @param row the row of the table
	 * @param column the column of the table
	 */
	public void setFloat(float data, int row, int column) {
		columns[column].setFloat(data, row);
	}

	/**
	 * Set a double value in the Table.
	 * @param data the value to set
	 * @param row the row of the table
	 * @param column the column of the table
	 */
	public void setDouble(double data, int row, int column) {
		columns[column].setDouble(data, row);
	}

	/**
	 * Set a String value in the Table.
	 * @param data the value to set
	 * @param row the row of the table
	 * @param column the column of the table
	 */
	public void setString(String data, int row, int column) {
		columns[column].setString(data, row);
	}

	/**
	 * Set a byte[] value in the Table.
	 * @param data the value to set
	 * @param row the row of the table
	 * @param column the column of the table
	 */
	public void setBytes(byte[] data, int row, int column) {
		columns[column].setBytes(data, row);
	}

	/**
	 * Set a byte value in the Table.
	 * @param data the value to set
	 * @param row the row of the table
	 * @param column the column of the table
	 */
	public void setByte(byte data, int row, int column) {
		columns[column].setByte(data, row);
	}

	/**
	 * Set a char[] value in the Table.
	 * @param data the value to set
	 * @param row the row of the table
	 * @param column the column of the table
	 */
	public void setChars(char[] data, int row, int column) {
		columns[column].setChars(data, row);
	}

	/**
	 * Set a char value in the Table.
	 * @param data the value to set
	 * @param row the row of the table
	 * @param column the column of the table
	 */
	public void setChar(char data, int row, int column) {
		columns[column].setChar(data, row);
	}

	/**
	 * Set a boolean value in the Table.
	 * @param data the value to set
	 * @param row the row of the table
	 * @param column the column of the table
	 */
	public void setBoolean(boolean data, int row, int column) {
		columns[column].setBoolean(data, row);
	}

	/////////////////////////////
	// Table copies.
	//
	/**
	 * Gets a subset of this Table's rows, which is actually a shallow
	 * copy which is subsetted..
	 * @param pos the start position for the subset
	 * @param len the length of the subset
	 * @return a subset of this Table's rows
	 */
	public Table getSubset(int pos, int len) {
		int[] sample = new int[len];
		for (int i = 0; i < len; i++) {
			sample[i] = pos + i;
		}
		return new SubsetTableImpl(this, sample);
	}

	/**
	 * Get a subset of this table.
	 * @param rows the rows to include in the subset.
	 * @return a subset table.
	 */
	public Table getSubset(int[] rows) {
		return new SubsetTableImpl(this, rows);
	}

	/**
	 * Return an exact copy of this Table.  A deep copy
	 * is attempted, but if it fails a new Table will be created,
	 * initialized with the same data as this Table.
	 * @return A new Table with a copy of the contents of this column.
	 */
	public Table copy() {
		TableImpl vt;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(this);
			byte buf[] = baos.toByteArray();
			oos.close();
			ByteArrayInputStream bais = new ByteArrayInputStream(buf);
			ObjectInputStream ois = new ObjectInputStream(bais);
			vt = (TableImpl) ois.readObject();
			ois.close();
			return vt;
		} catch (Exception e) {
			vt = new MutableTableImpl(getNumColumns());
			for (int i = 0; i < getNumColumns(); i++)
				vt.setColumn(getColumn(i).copy(), i);
			vt.setLabel(getLabel());
			vt.setComment(getComment());
			return vt;
		}
	}

	/**
	 * Make a deep copy of the table, include length rows begining at start
	 * @param start the first row to include in the copy
	 * @param length the number of rows to include
	 * @return a new copy of the table.
	 */
	public Table copy(int start, int length) {

		// Subset the columns to get new columns.
		Column[] cols = new Column[this.getNumColumns()];
		for (int i = 0; i < getNumColumns(); i++) {
			Column oldColumn = this.getColumn(i);
			cols[i] = oldColumn.getSubset(start, length);
		}

		// make a table from the new columns
		MutableTableImpl vt = new MutableTableImpl(cols);
		vt.setLabel(getLabel());
		vt.setComment(getComment());
		return vt;
	}

	/**
	 * Make a deep copy of the table, include length rows begining at start
	 * @param start the first row to include in the copy
	 * @param length the number of rows to include
	 * @return a new copy of the table.
	 */
	public Table copy(int[] subset) {

		// Subset the columns to get new columns.
		Column[] cols = new Column[this.getNumColumns()];
		for (int i = 0; i < getNumColumns(); i++) {
			Column oldColumn = this.getColumn(i);
			cols[i] = oldColumn.getSubset(subset);
		}

		// make a table from the new columns
		MutableTableImpl vt = new MutableTableImpl(cols);
		vt.setLabel(getLabel());
		vt.setComment(getComment());
		return vt;
	}

	/**
	 * Do a shallow copy on the data by creating a new instance of a MutableTable,
	 * and initialize all it's fields from this one.
	 * @return a shallow copy of the table.
	 */
	public Table shallowCopy() {
		MutableTableImpl mti = new MutableTableImpl(this.columns);
		mti.setLabel(this.getLabel());
		mti.setComment(this.getComment());
		return mti;
	}

	/**
	 * Insert the specified number of blank rows.
	 * @param howMany
	 */
	public void addRows(int howMany) {
		for (int i = 0; i < getNumColumns(); i++) {
			getColumn(i).addRows(howMany);
		}
	}

	/**
	 * Create a new empty table of the same type as the implementation
	 * @return a new empty table.
	 */
	public MutableTable createTable() {
		return new MutableTableImpl();
	}

	//////////////////////////////////////
	// Methods to manipulate rows.
	//

	/**
	 * return a row object used to access each row.
	 * @return a row accessor object.
	 */
	public Row getRow() {
		return new RowImpl(this);
	}

	/**
	 * Get a copy of this Table, reordered, based on the input array of indexes,
	 * does not overwrite this Table.
	 * @param newOrder an array of indices indicating a new order
	 * @return a copy of this table that has been reordered.
	 */
	public Table reorderRows(int[] newOrder) {
		TableImpl newTable = new MutableTableImpl(columns.length);
		for (int i = 0; i < columns.length; i++)
			newTable.setColumn(columns[i].reorderRows(newOrder), i);
		newTable.setLabel(getLabel());
		newTable.setComment(getComment());
		return newTable;
	}

	/**
	 * MUST GET COPIES!!
	 * @param newOrder
	 * @return
	 */
	public Table reorderColumns(int[] newOrder) {
		TableImpl newTable = new MutableTableImpl(columns.length);
		for (int i = 0; i < newOrder.length; i++)
			newTable.setColumn(getColumn(newOrder[i]).copy(), i);
		newTable.setLabel(getLabel());
		newTable.setComment(getComment());
		return newTable;
	}

	/**
	 * Sort a column and rearrange the rows of the Table accordingly.
	 * The column must support sorting.
	 * @param col the index of the column to sort
	 * @throws NotSupportedException when sorting is not supported on
	 * the specified column.
	 */
	public void sortByColumn(int col) {
		//ANCA removed comments from the line bellow
		this.getColumn(col).sort((MutableTable) this);
	}

	/**
	   Sort the elements in this column starting with row 'begin' up to row 'end',
	  @param col the index of the column to sort
	   @param begin the row no. which marks the beginnig of the  column segment to be sorted
	   @param end the row no. which marks the end of the column segment to be sorted
	*/
	public void sortByColumn(int col, int begin, int end) {
		//ANCA removed comments from the line bellow
		this.getColumn(col).sort((MutableTable) this, begin, end);
	}

	//ANCA - added method below for testing purposes

	public boolean equals(Object mt) {
		MutableTableImpl mti = (MutableTableImpl)mt;
		int numColumns = mti.getNumColumns();
		int numRows = mti.getNumRows();
		if (getNumColumns() != numColumns)
			return false;
		if (getNumRows() != numRows)
			return false;
		for (int i = 0; i < numRows; i++)
			for (int j = 0; j < numColumns; j++)
				if (!getString(i, j).equals(mti.getString(i, j)))
					return false;
		return true;
	}
}


