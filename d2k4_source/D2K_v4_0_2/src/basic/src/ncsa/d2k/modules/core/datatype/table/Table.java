package ncsa.d2k.modules.core.datatype.table;
import ncsa.d2k.modules.core.datatype.table.basic.Column;

/**
	Table is a data structure of m rows where each row has n columns.  Therefore,
	a Table is a two-dimensional and rectangular datastructure.  Each column of a table
	contains elements of a single type.  Hence, any two elements of a particular
	column will be castable to the same type.  Each row of a given Table
	represents a single record.  Thus, the synchronization of columns is
	important to maintaining the records.  For example, if the order of one column's
	elements are manipulated, then all other columns must be likewise updated.
	<br><br>
	A Table can have a key column associated with it.  The key column is the column
	which should contain unique keys that uniquely identify the rows (records)
	within the Table.  This can be accessed with the get/setKeyColumn methods.
	If the key column is not set, it should default to Column 0.
	<br><br>
	The data within a Table is immutable.  Methods are provided to access the Table
	contents, but not mutate it.  The MutableTable interface defines the methods
	used to mutate the contents of a Table.
	<br><br>
	A single column of the Table will contain values of the same datatype.  To
	determine the datatype of the column, use the getColumnType() method.  Columns
	can also be designated as scalar or nominal.  The isColumnNumeric() method
	has been provided as a convienience to determine whether all the items in
	a column contain numeric values.
	<br><br>
	Table is designed for the primary use of linking/grouping (possibly
	variously-typed) data together.  So that the linked data (synchronized columns)
	may be accessed and manipulated as a group.
	<br>
	Table was designed for 2 types of access or manipulation:
	<ul><li>Specific access, where the accessor knows the underlying data types
		and content.  This is enabled by convienience methods such as getFloat(row, col).
		Programmers will find this to the easiest way to use Table for a specific solution.
	<li>Generalized access, where the accessor has no knowledge of the underlying data types or content.
	This is enabled by a generalized and flexible class hierarchy which allows
	columns to be manipulated in an efficient manner without knowledge of their
	underlying types or content, while programming for this generalized result is
	more complex and generally yields less efficient code, it yields a more flexible and
	extensible result.
	</ul>
*/
public interface Table extends java.io.Serializable {

	//static final long serialVersionUID = 2508941379956505568L;

	static final long serialVersionUID = -8081185634463160785L;

    /**
	 * Get an Object from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the Object at (row, column)
     */
	public Object getObject(int row, int column);

    /**
	 * Get an int value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the int at (row, column)
     */
	public int getInt(int row, int column);

    /**
	 * Get a short value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the short at (row, column)
     */
	public short getShort(int row, int column) ;

    /**
	 * Get a float value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the float at (row, column)
     */
	public float getFloat(int row, int column) ;

    /**
	 * Get a double value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the double at (row, column)
     */
	public double getDouble(int row, int column) ;

    /**
	 * Get a long value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the long at (row, column)
     */
	public long getLong(int row, int column);

    /**
	 * Get a String value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the String at (row, column)
     */
	public String getString(int row, int column) ;

    /**
	 * Get a value from the table as an array of bytes.
     * @param row the row of the table
     * @param column the column of the table
     * @return the value at (row, column) as an array of bytes
     */
	public byte[] getBytes(int row, int column) ;

    /**
	 * Get a boolean value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the boolean value at (row, column)
     */
	public boolean getBoolean(int row, int column);

    /**
	 * Get a value from the table as an array of chars.
     * @param row the row of the table
     * @param column the column of the table
     * @return the value at (row, column) as an array of chars
     */
	public char[] getChars(int row, int column);

    /**
	 * Get a byte value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the byte value at (row, column)
     */
	public byte getByte(int row, int column);

    /**
	 * Get a char value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the char value at (row, column)
     */
	public char getChar(int row, int column);

	//////////////////////////////////////
	//// Accessing Table Metadata

	/**
		Returns the name associated with the column.
		@param position the index of the Column name to get.
		@returns the name associated with the column.
	*/
	public String getColumnLabel(int position);

	/**
		Returns the comment associated with the column.
		@param position the index of the Column name to get.
		@returns the comment associated with the column.
	*/
	public String getColumnComment(int position);

	/**
		Get the label associated with this Table.
		@return the label which describes this Table
	*/
	public String getLabel();

	/**
		Set the label associated with this Table.
		@param labl the label which describes this Table
	*/
	public void setLabel(String labl);

	/**
		Get the comment associated with this Table.
		@return the comment which describes this Table
	*/
	public String getComment();

	/**
		Set the comment associated with this Table.
		@param comment the comment which describes this Table
	*/
	public void setComment(String comment);

	/**
	  	Get the number of rows in this Table.  Same as getCapacity().
		@return the number of rows in this Table.
	*/
	public int getNumRows();

	/**
		Return the number of columns this table holds.
		@return the number of columns in this table
	*/
	public int getNumColumns();

	/**
		Get a subset of this Table, given a start position and length.  The
		subset will be a new Table.
		@param start the start position for the subset
		@param len the length of the subset
		@return a subset of this Table
	*/
	public Table getSubset(int start, int len);

	/**
	 * get a subset of the table consisting of the rows identified by the array
	 * of indices passed in.
	 * @param rows the rows to be in the subset.
	 * @return
	 */
	public Table getSubset(int[] rows);

	/**
	 * Create a copy of this Table. This is a deep copy, and it contains a copy of
	 * 	all the data.
	 * @return a copy of this Table
	 */
	public Table copy();

	/**
	 * Create a copy of this Table. This is a deep copy, and it contains a copy of
	 * 	all the data.
	 * @return a copy of this Table
	 */
	public Table copy(int start, int len);

	/**
	 * Create a copy of this Table. This is a deep copy, and it contains a copy of
	 * 	all the data.
	 * @return a copy of this Table
	 */
	public Table copy(int [] rows);
	/**
	 * Create a copy of this Table. A copy of every field in the class should be made,
	 * but the data itself should not be copied.
	 * @return a shallow copy of this Table
	 */
	public Table shallowCopy();

	/**
	 * Create a new empty table of the same type as the implementation
	 * @return a new empty table.
	 */
	public MutableTable createTable();

	/**
	 * Returns true if the column at position contains nominal data, false
	 * otherwise.
	 * @param position the index of the column
	 * @return true if the column contains nominal data, false otherwise.
	 */
	public boolean isColumnNominal(int position);

	/**
	 * Returns true if the column at position contains scalar data, false
	 * otherwise
	 * @param position
	 * @return true if the column contains scalar data, false otherwise
	 */
	 public boolean isColumnScalar(int position);

	 /**
	  * Set whether the column at position contains nominal data or not.
	  * @param value true if the column at position holds nominal data, false otherwise
	  * @param position the index of the column
	  */
	 public void setColumnIsNominal(boolean value, int position);

	 /**
	  * Set whether the column at position contains scalar data or not.
	  * @param value true if the column at position holds scalar data, false otherwise
	  * @param position the index of the column
	  */
	 public void setColumnIsScalar(boolean value, int position);

	/**
	 * Returns true if the column at position contains only numeric values,
	 * false otherwise.
	 * @param position the index of the column
	 * @return true if the column contains only numeric values, false otherwise
	 */
	public boolean isColumnNumeric(int position);

	/**
	  * Return the type of column located at the given position.
	  * @param position the index of the column
	  * @return the column type
	  * @see ColumnTypes
	  */
	public int getColumnType(int position);

	/**
	 * This method will return a Row object. The row object can be used over and over
	 * to access the rows of the table by setting it's index to access a particular row.
	 * @return a Row object that can access the rows of the table.
	 */
	public Row getRow ();

	/**
	  * Return this Table as an ExampleTable.
	  * @return This object as an ExampleTable
	  */
	public ExampleTable toExampleTable();

	/**
	 * Return true if the value at (row, col) is a missing value, false otherwise.
	 * @param row the row index
	 * @param col the column index
	* @return true if the value is missing, false otherwise
	*/
	public boolean isValueMissing(int row, int col);

	/**
	* Return true if the value at (row, col) is an empty value, false otherwise.
	* @param row the row index
	* @param col the column index
	* @return true if the value is empty, false otherwise
	*/
	public boolean isValueEmpty(int row, int col);

	/**
	* Return true if any value in this Table is missing.
	* @return true if there are any missing values, false if there are no missing values
	*/
	public boolean hasMissingValues();
	
	/** return the default missing value for integers, both short, int and long.
	 * @returns the integer for missing value.
	 */
	public int getMissingInt ();
	
	/** return the default missing value for integers, both short, int and long.
	 * @param the integer for missing values.
	 */
	public void setMissingInt (int newMissingInt);
	
	/** return the default missing value for doubles, floats and extendeds.
	 * @returns the double for missing value.
	 */
	public double getMissingDouble ();
	
	/** return the default missing value for integers, both short, int and long.
	 * @param the integer for missing values.
	 */
	public void setMissingDouble (double newMissingDouble);
	
	/** return the default missing value for doubles, floats and extendeds.
	 * @returns the double for missing value.
	 */
	public String getMissingString ();
	
	/** return the default missing value for integers, both short, int and long.
	 * @param the integer for missing values.
	 */
	public void setMissingString (String newMissingString);
	
	/** return the default missing value for doubles, floats and extendeds.
	 * @returns the double for missing value.
	 */
	public boolean getMissingBoolean();
	
	/** return the default missing value for integers, both short, int and long.
	 * @param the integer for missing values.
	 */
	public void setMissingBoolean(boolean newMissingBoolean);
	
	/** return the default missing value for doubles, floats and extendeds.
	 * @returns the double for missing value.
	 */
	public char[] getMissingChars();
	
	/** return the default missing value for integers, both short, int and long.
	 * @param the integer for missing values.
	 */
	public void setMissingChars(char[] newMissingChars);
	
	/** return the default missing value for doubles, floats and extendeds.
	 * @returns the double for missing value.
	 */
	public byte[] getMissingBytes();
	
	/** return the default missing value for integers, both short, int and long.
	 * @param the integer for missing values.
	 */
	public void setMissingBytes(byte[] newMissingBytes);
	
	/** return the default missing value for doubles, floats and extendeds.
	 * @returns the double for missing value.
	 */
	public char getMissingChar();
	
	/** return the default missing value for integers, both short, int and long.
	 * @param the integer for missing values.
	 */
	public void setMissingChar(char newMissingChar);
	
	/** return the default missing value for doubles, floats and extendeds.
	 * @returns the double for missing value.
	 */
	public byte getMissingByte();
	
	/** return the default missing value for integers, both short, int and long.
	 * @param the integer for missing values.
	 */
	public void setMissingByte(byte newMissingByte);
	
	/**
	* Return true if any value in the column at columnIndex is missing.\
	* @param columnIndex the index of the column to check.
	* @return true if there are any missing values, false if there are no missing values
	*/
	public boolean hasMissingValues(int columnIndex);

	/**
	 * Return a column representing the data in column n.
	 * @param n the column to get.
	 * @return a column representing the data.
	 */
	public Column getColumn(int n);

}/*Table*/
