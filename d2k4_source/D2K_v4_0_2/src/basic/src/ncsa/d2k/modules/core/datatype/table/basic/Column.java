package ncsa.d2k.modules.core.datatype.table.basic;

import ncsa.d2k.modules.core.datatype.table.*;

/**
	Column is an ordered list, generally associated with a table.  There are
	many implementations of Column optimized for different sets of tasks.
	<br><br>
	Column defines methods that are common to all implementations.  These
	include the insertion and deletion of rows and row reordering methods.
	Several methods are defined to provide metadata about the contents of a
	Column.  Accessor methods are also defined for each primitive data type and
	for several common Object types used.  The Column implementation must
	provide the necessary datatype conversions.
*/
public interface Column extends java.io.Serializable {

	//static final long serialVersionUID = 8383279130438086351L;
	static final long serialVersionUID = 63054574851515523L;

	/**
		Get the label associated with this Column.
		@return the label which describes this Column
	*/
	public String getLabel( );

	/**
		Set the label associated with this Column.
		@param labl the label which describes this Column
	*/
	public void setLabel( String labl );

	/**
		Get the comment associated with this Column.
		@return the comment which describes this Column
	*/
	public String getComment( );

	/**
		Set the comment associated with this Column.
		@param comment the comment which describes this Column
	*/
	public void setComment( String comment );

	/**
	  	Get the number of rows in this Column.  Same as getCapacity().
		@return the number of rows in this Column.
	*/
	public int getNumRows( );

	/**
		Get the number of entries this Column holds.  This is the number of
		non-null entries in the Column.
		@return this Column's number of entries
	*/
	public int getNumEntries( );

    /**
    	Get a String from this column at pos
    	@param pos the position from which to get a String
    	@return a String representation of the entry at that position
     */
	public String getString( int pos );

    /**
    	Set the item at pos to be newEntry
    	@param newEntry the new item
    	@param pos the position
     */
	public void setString( String newEntry, int pos );

    /**
    	Get the value at pos as an int
    	@param pos the position
    	@return the int value of the item at pos
     */
	public int getInt( int pos );

    /**
    	Set the item at pos to be newEntry
    	@param newEntry the new item
    	@param pos the position
     */
	public void setInt( int newEntry, int pos );

    /**
    	Get the value at pos as a short
    	@param pos the position
    	@return the short at pos
     */
	public short getShort( int pos );

    /**
    	Set the item at pos to be newEntry
    	@param newEntry the new item
    	@param pos the position
     */
	public void setShort( short newEntry, int pos );

    /**
    	Get the value at pos as a long
    	@param pos the position
    	@return the value at pos as a long
     */
	public long getLong(int pos);

    /**
    	Set the value at pos to be newEntry
    	@param newEntry the new item
    	@param pos the position
     */
	public void setLong(long newEntry, int pos);

    /**
    	Get the value at pos as a double
    	@param pos the position
    	@return the value at pos as a double
     */
	public double getDouble( int pos );

    /**
    	Set the value at pos to be newEntry
    	@param newEntry the new item
    	@param pos the position
     */
	public void setDouble( double newEntry, int pos );

    /**
    	Get the value at pos as a float
    	@param pos the position
    	@return the value at pos as a float
     */
	public float getFloat( int pos );

    /**
    	Set the value at pos to be newEntry
    	@param newEntry the new item
    	@param pos the position
     */
	public void setFloat( float newEntry, int pos );

    /**
    	Get the value at pos as a byte array
    	@param pos the position
    	@return the value at pos as a byte array
     */
	public byte[] getBytes( int pos );

    /**
    	Set the value at pos to be newEntry
    	@param newEntry the new item
    	@param pos the position
     */
	public void setBytes( byte[] newEntry, int pos );

    /**
    	Get the value at pos as a byte
    	@param pos the position
    	@return the value at pos as a byte
     */
	public byte getByte( int pos );

    /**
    	Set the value at pos to be newEntry
    	@param newEntry the new item
    	@param pos the position
     */
	public void setByte( byte newEntry, int pos );

    /**
    	Get the value at pos as a boolean
    	@param pos the position
    	@return the value at pos as a byte array
     */
	public boolean getBoolean( int pos );

    /**
    	Set the value at pos to be newEntry
    	@param newEntry the new item
    	@param pos the position
     */
	public void setBoolean( boolean newEntry, int pos );

    /**
    	Get the value at pos as a char array
    	@param pos the position
    	@return the value at pos as a char array
     */
	public char[] getChars( int pos );

    /**
    	Set the value at pos to be newEntry
    	@param newEntry the new item
    	@param pos the position
     */
	public void setChars( char[] newEntry, int pos );

    /**
    	Get the value at pos as a char
    	@param pos the position
    	@return the value at pos as a char
     */
	public char getChar( int pos );

    /**
    	Set the value at pos to be newEntry
    	@param newEntry the new item
    	@param pos the position
     */
	public void setChar( char newEntry, int pos );

    /**
    	Get the value at pos as an Object
    	@param pos the position
    	@return the value at pos as an Object
     */
	public Object getObject( int pos );

    /**
    	Set the value at pos to be newEntry
    	@param newEntry the new item
    	@param pos the position
     */
	public void setObject( Object newEntry, int pos );

    /**
    	Sort the elements in this column.
     */
	public void sort();

    /**
    	Sort the elements in this column, and swap the rows in the table
		it is a member of.
    	@param t the Table to swap rows for
     */
	public void sort(MutableTable t);

    /**
       Sort the elements in this column starting with row 'begin' up to row 'end',
       and swap the rows in the table  we are a part of.
       @param t the VerticalTable to swap rows for
       @param begin the row no. which marks the beginnig of the  column segment to be sorted
       @param end the row no. which marks the end of the column segment to be sorted
    */
    public void sort(MutableTable t,int begin, int end);

    /**
    	Compare the values of the object passed in and pos. Return 0 if they
    	are the same, greater than zero if element is greater,
		and less than zero if element is less.
    	@param element the object to be passed in should be a subclass of Number
    	@param pos the position of the element in Column to be compared with
    	@return a value representing the relationship- >, <, or == 0
     */
 	public int compareRows(Object element, int pos);

    /**
    	Compare pos1 and pos2 positions in the Column. Return 0 if they
    	are the same, greater than zero if pos1 is greater,
		and less than zero if pos1 is less.
    	@param p1 the position of the first element to compare
    	@param p2 the position of the second element to compare
    	@return a value representing the relationship- >, <, or == 0
     */
	public int compareRows(int p1, int p2);

	/**
		Sets the number of rows for this Column.  The capacity is its potential
		maximum number of entries.  If numEntries is greater than newCapacity
		then the Column will be truncated.
		@param newCapacity a new capacity
	*/
	public void setNumRows(int newCapacity);

	//////////////////////////////////////
	//// ACCESSING Column Elements

	/**
		Get an entry from the Column at the indicated position.
		@param pos the position
		@return the entry at pos
	*/
	public Object getRow( int pos );

	/**
		Get a subset of this Column, given a start position and length.  The
		subset will be a new Column.
		@param pos the start position for the subset
		@param len the length of the subset
		@return a subset of this Column
	*/
	public Column getSubset( int pos, int len );

	/**
	 * Get a subset indexed by those indices in the int array passed in.
	 * @param rows the indices of the rows to include
	 * @return a subset of the column.
	 */
    public Column getSubset(int[] rows);

	/**
		Set the entry at the given position to newEntry.
		@param newEntry a new entry
		@param pos the position to set
	*/
	public void setRow( Object newEntry, int pos );

	/**
		Get a copy of this Column reordered based on the input array of indexes.
		Does not overwrite this Column.
		@param newOrder an array of indices indicating a new order
		@return a copy of this column with the rows re-ordered
	*/
	public Column reorderRows( int[] newOrder );

	/**
		Swap the positions of two rows.
		@param pos1 the first row to swap
		@param pos2 the second row to swap
	*/
	public void swapRows(int pos1, int pos2);

	/**
		Create a copy of this Column.
		@return a copy of this column
	*/
	public Column copy();

	/**
	 * Remove a row from this Column.
	 * @param row the row to remove
	 */
	public Object removeRow( int row );

	/**
	 * Remove a range of rows from this Column.
	 * @param start the start position
	 * @param len the number to remove
	 */
	public void removeRows(int start, int len);

	/**
	 * Add a row to the end of this column
	 * @param newEntry the Object to put into the new row
	 */
	public void addRow( Object newEntry );

	public void addRows (int number);

	/**
	 * Insert a new row into this Column
	 * @param newEntry the Object to insert
	 * @param pos the position to insert the new row
	 */
	public void insertRow( Object newEntry, int pos );

	/**
	 * Remove rows from this column using a boolean map.
	 * @param flags an array of booleans to map to this column's rows.  Those
	 * with a true will be removed, all others will not be removed
	 */
	public void removeRowsByFlag( boolean[] flags );

	/**
	 * Remove rows from this column by index.
	 * @param indices a list of the rows to remove
	 */
	public void removeRowsByIndex( int[] indices );

	/**
	 * Set whether this column is nominal or not.
	 * @param value true if the column is nominal, false otherwise
	 */
	public void setIsNominal(boolean value);

	/**
	 * Set whether this column is scalar or not.
	 * @param value true if the column is scalar, false otherwise.
	 */
	public void setIsScalar(boolean value);

	/**
	 */
	public boolean getIsNominal();
	/**
	 */
	public boolean getIsScalar();

	/**
	 * Get the type of this column, enumerated in <code>ColumnTypes</code>.
	 * @return the type of this column
	 */
	public int getType();

	/**
	 * return true if there are any missing values in the column.
	 * @return true if there are any missing values in the column.
	 */
	public boolean hasMissingValues();
	
	/**
		 * return the number of missing values in the column.
		 * @return number of missing values in the column.
		 */
		public int getNumMissingValues();
	
	/**
	 * Return true if the value at row is missing, false otherwise
	 * @param row the row index
	 * @return true if the value at row is missing, false otherwise
	 */
	public boolean isValueMissing(int row);

	/**
	 * Return true if the value at row is empty, false otherwise
	 * @param row the row index
	 * @return true if the value at row is empty, false otherwise
	 */

	public boolean isValueEmpty(int row);

	/**
	 * Set the value at row to be missing.
	 * @param row the row index
	 */
	public void setValueToMissing(boolean b, int row);

	/**
	 * Set the value at row to be empty.
	 * @param row the row index
	 */
	public void setValueToEmpty(boolean b, int row);

	public Object getInternal();

}/*Column*/
