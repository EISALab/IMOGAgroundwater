package ncsa.d2k.modules.core.datatype.table;
import ncsa.d2k.modules.core.datatype.table.basic.Column;
import ncsa.d2k.modules.core.datatype.table.Transformation;
import java.util.*;

/**
 * MutableTable defines methods used to mutate the contents of a Table.
 */
public interface MutableTable extends Table {

	static final long serialVersionUID = 3803628206682571278L;

	/**
	 * This method will replace the column at where with the one passed in.
	 * @param col the new column
	 * @param where where to put it.
	 */
	public void setColumn(Column col, int where);

	/**
	 * Add columns to the table.
	 * @param datatype add columns to the table.
	 */
	public void addColumn(Column datatype);

	/**
	 * Add columns to the table.
	 * @param datatype add columns to the table.
	 */
	public void addColumns(Column [] datatype);

	/**
	 * Insert a column in the table.
	 * @param col the column to add.
	 * @param where position were the column will be inserted.
	 */
	public void insertColumn(Column col, int where);

	/**
	 * Insert columns in the table.
	 * @param datatype the columns to add.
	 * @param where the number of columns to add.
	 */
	public void insertColumns(Column [] datatype, int where);

	/**
		Remove a column from the table.
		@param position the position of the Column to remove
	*/
	public void removeColumn(int position);

	/**
		Remove a range of columns from the table.
		@param start the start position of the range to remove
		@param len the number to remove-the length of the range
	*/
	public void removeColumns(int start, int len);

	/**
	 * Insert the specified number of blank rows.
	 * @param howMany
	 */
	public void addRows(int howMany);

	/**
	 * Remove a row from this Table.
	 * @param row the row to remove
	 */
	public void removeRow(int row);

	/**
		Remove a range of rows from the table.
		@param start the start position of the range to remove
		@param len the number to remove-the length of the range
	*/
	public void removeRows(int start, int len);


	/**
		Get a copy of this Table reordered based on the input array of indexes.
		Does not overwrite this Table.
		@param newOrder an array of indices indicating a new order
		@return a copy of this column with the rows reordered
	*/
	public Table reorderRows(int[] newOrder);

	/**
		Get a copy of this Table reordered based on the input array of indexes.
		Does not overwrite this Table.
		@param newOrder an array of indices indicating a new order
		@return a copy of this column with the rows reordered
	*/
	public Table reorderColumns(int[] newOrder);

	/**
		Swap the positions of two rows.
		@param pos1 the first row to swap
		@param pos2 the second row to swap
	*/
	public void swapRows(int pos1, int pos2);

	/**
		Swap the positions of two columns.
		@param pos1 the first column to swap
		@param pos2 the second column to swap
	*/
	public void swapColumns(int pos1, int pos2);

	/**
		Set a specified element in the table.  If an element exists at this
		position already, it will be replaced.  If the position is beyond the capacity
		of this table then an ArrayIndexOutOfBounds will be thrown.
		@param element the new element to be set in the table
		@param row the row to be changed in the table
		@param column the Column to be set in the given row
	*/
	public void setObject(Object element, int row, int column);

    /**
	 * Set an int value in the table.
	 * @param data the value to set
     * @param row the row of the table
     * @param column the column of the table
     */
	public void setInt(int data, int row, int column);

    /**
	 * Set a short value in the table.
	 * @param data the value to set
     * @param row the row of the table
     * @param column the column of the table
     */
	public void setShort(short data, int row, int column);

    /**
	 * Set a float value in the table.
	 * @param data the value to set
     * @param row the row of the table
     * @param column the column of the table
     */
	public void setFloat(float data, int row, int column);

    /**
	 * Set an double value in the table.
	 * @param data the value to set
     * @param row the row of the table
     * @param column the column of the table
     */
	public void setDouble(double data, int row, int column);

    /**
	 * Set a long value in the table.
	 * @param data the value to set
     * @param row the row of the table
     * @param column the column of the table
     */
	public void setLong(long data, int row, int column);

    /**
	 * Set a String value in the table.
	 * @param data the value to set
     * @param row the row of the table
     * @param column the column of the table
     */
	public void setString(String data, int row, int column);

    /**
	 * Set a byte[] value in the table.
	 * @param data the value to set
     * @param row the row of the table
     * @param column the column of the table
     */
	public void setBytes(byte[] data, int row, int column);

    /**
	 * Set a boolean value in the table.
	 * @param data the value to set
     * @param row the row of the table
     * @param column the column of the table
     */
	public void setBoolean(boolean data, int row, int column);

    /**
	 * Set a char[] value in the table.
	 * @param data the value to set
     * @param row the row of the table
     * @param column the column of the table
     */
	public void setChars(char[] data, int row, int column);

    /**
	 * Set a byte value in the table.
	 * @param data the value to set
     * @param row the row of the table
     * @param column the column of the table
     */
	public void setByte(byte data, int row, int column);

    /**
	 * Set a char value in the table.
	 * @param data the value to set
     * @param row the row of the table
     * @param column the column of the table
     */
	public void setChar(char data, int row, int column);

	/**
		Set the name associated with a column.
		@param label the new column label
		@param position the index of the column to set
	*/
	public void setColumnLabel(String label, int position);

	/**
		Set the comment associated with a column.
		@param comment the new column comment
		@param position the index of the column to set
	*/
	public void setColumnComment(String comment, int position);

	/**
		Sort the specified column and rearrange the rows of the table to
		correspond to the sorted column.
		@param col the column to sort by
	*/
	public void sortByColumn(int col);

    /**
       Sort the elements in this column starting with row 'begin' up to row 'end',
	   @param col the index of the column to sort
       @param begin the row no. which marks the beginnig of the  column segment to be sorted
       @param end the row no. which marks the end of the column segment to be sorted
    */
    public void sortByColumn(int col, int begin, int end);

    /////////// Collect the transformations that were performed. /////////
    /**
     Add the transformation to the list.
     @param tm the Transformation that performed the reversable transform.
     */
    public void addTransformation (Transformation tm);

    /**
     Returns the list of all reversable transformations there were performed
     on the original dataset.
     @returns an ArrayList containing the Transformation which transformed the data.
     */
    public List getTransformations ();

	/**
	 * Set the value at (row, col) to be missing or not missing.
     * @param b true if the value should be set as missing, false otherwise
	 * @param row the row index
	 * @param col the column index
	 */
	public void setValueToMissing(boolean b, int row, int col);

	/**
	 * Set the value at (row, col) to be empty or not empty.
     * @param b true if the value should be set as empty, false otherwise
	 * @param row the row index
	 * @param col the column index
	 */
	public void setValueToEmpty(boolean b, int row, int col);
}
