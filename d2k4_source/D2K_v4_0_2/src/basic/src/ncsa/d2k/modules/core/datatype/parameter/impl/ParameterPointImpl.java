package ncsa.d2k.modules.core.datatype.parameter.impl;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

import ncsa.d2k.modules.core.datatype.parameter.*;

import java.io.Serializable;
/**

The ParameterPoint object can extend ExampleImpl.
It will be input to the learning algorithm.
It should use the same column names and column order as the ParameterSpace implementation.
Note: It is important that this be an Example so that additional layers
(optimizing the optimizer etc..) can more easily be implemented.

*/
public class ParameterPointImpl extends ExampleImpl implements Serializable, ParameterPoint {
	
	/** this is the missing value for longs, ints, and shorts. */
	protected int defaultMissingInt = 0;
	
	/** default for float double and extended. */
	protected double defaultMissingDouble = 0.0;
	
	/** default missing string. */
	protected String defaultMissingString = "?";
	
	/** default missing string. */
	protected boolean defaultMissingBoolean = false;
	
	/** default missing string. */
	protected char[] defaultMissingCharArray = {'\000'};
	
	/** default missing string. */
	protected byte[] defaultMissingByteArray = {(byte)'\000'};
	
	/** default missing string. */
	protected char defaultMissingChar = '\000';
	
	/** default missing string. */
	protected byte defaultMissingByte = (byte)'\000';
	
	/** return the default missing value for integers, both short, int and long.
	 * @returns the integer for missing value.
	 */
	public int getMissingInt () {
		return defaultMissingInt;
	}
	
	/** return the default missing value for integers, both short, int and long.
	 * @param the integer for missing values.
	 */
	public void setMissingInt (int newMissingInt) {
		this.defaultMissingInt = newMissingInt;
	}
	
	/** return the default missing value for doubles, floats and extendeds.
	 * @returns the double for missing value.
	 */
	public double getMissingDouble () {
		return this.defaultMissingDouble;
	}
	
	/** return the default missing value for integers, both short, int and long.
	 * @param the integer for missing values.
	 */
	public void setMissingDouble (double newMissingDouble) {
		this.defaultMissingDouble = newMissingDouble;
	}
	
	/** return the default missing value for doubles, floats and extendeds.
	 * @returns the double for missing value.
	 */
	public String getMissingString () {
		return this.defaultMissingString;
	}
	
	/** return the default missing value for integers, both short, int and long.
	 * @param the integer for missing values.
	 */
	public void setMissingString (String newMissingString) {
		this.defaultMissingString = newMissingString;
	}
	
	/** return the default missing value for doubles, floats and extendeds.
	 * @returns the double for missing value.
	 */
	public boolean getMissingBoolean() {
		return defaultMissingBoolean;
	}
	
	/** return the default missing value for integers, both short, int and long.
	 * @param the integer for missing values.
	 */
	public void setMissingBoolean(boolean newMissingBoolean) {
		this.defaultMissingBoolean = newMissingBoolean;
	}
	
	/** return the default missing value for doubles, floats and extendeds.
	 * @returns the double for missing value.
	 */
	public char[] getMissingChars() {
		return this.defaultMissingCharArray;
	}
	
	/** return the default missing value for integers, both short, int and long.
	 * @param the integer for missing values.
	 */
	public void setMissingChars(char[] newMissingChars) {
		this.defaultMissingCharArray = newMissingChars;
	}
	
	/** return the default missing value for doubles, floats and extendeds.
	 * @returns the double for missing value.
	 */
	public byte[] getMissingBytes() {
		return this.defaultMissingByteArray;
	}
	
	/** return the default missing value for integers, both short, int and long.
	 * @param the integer for missing values.
	 */
	public void setMissingBytes(byte[] newMissingBytes) {
		this.defaultMissingByteArray = newMissingBytes;
	}
	
	/** return the default missing value for doubles, floats and extendeds.
	 * @returns the double for missing value.
	 */
	public char getMissingChar() {
		return this.defaultMissingChar;
	}
	
	/** return the default missing value for integers, both short, int and long.
	 * @param the integer for missing values.
	 */
	public void setMissingChar(char newMissingChar) {
		this.defaultMissingChar = newMissingChar;
	}
	
	/** return the default missing value for doubles, floats and extendeds.
	 * @returns the double for missing value.
	 */
	public byte getMissingByte() {
		return defaultMissingByte;
	}
	
	/** return the default missing value for integers, both short, int and long.
	 * @param the integer for missing values.
	 */
	public void setMissingByte(byte newMissingByte) {
		this.defaultMissingByte = newMissingByte;
	}
	/**
	 * return a parameter point from the given arrays.
	 * @return
	 */
	static final public ParameterPoint getParameterPoint(String []names, double [] values) {
		int numColumns = names.length;
		Column [] cols = new Column [values.length];
		for (int i = 0 ; i < values.length; i++) {
			double [] vals = new double [1];
			vals [0] = values[i];
			DoubleColumn dc = new DoubleColumn(vals);
			dc.setLabel(names[i]);
			cols[i] = dc;
		}
		ExampleTableImpl eti = new ExampleTableImpl();
		eti.addColumns(cols);
		int [] ins = new int [numColumns];
		for (int i = 0 ; i < numColumns; i++) ins[i] = i;
		eti.setInputFeatures(ins);
		return new ParameterPointImpl(eti);
	 }

	 public ParameterPoint createFromData(String [] names, double [] values) {
		int numColumns = names.length;
		Column [] cols = new Column[values.length];
		for (int i = 0 ; i < values.length; i++) {
			 double [] vals = new double [1];
			 vals [0] = values[i];
			 DoubleColumn dc = new DoubleColumn(vals);
			 dc.setLabel(names[i]);
			 cols[i] = dc;
		 }
		 ExampleTableImpl eti = new ExampleTableImpl();
		 return new ParameterPointImpl(eti);
	 }

	 /**
	  * This method is expected to get an MutableTableImpl.
	  * @param mt
	  * @return
	  */
	 public ParameterPoint createFromTable(MutableTable mt) {
		 return new ParameterPointImpl((ExampleTableImpl)mt);
	 }

	 /**
	  *
	  * @param et
	  */
	ParameterPointImpl(ExampleTableImpl et) {
		super(et);
	}

	/**
	* Get the number of parameters that define the space.
	* @return An int value representing the minimum possible value of the parameter.
	*/
	public int getNumParameters() {
		return this.getNumColumns();
	}

	/**
	* Get the name of a parameter.
	* @param parameterIndex the index of the parameter of interest.
	* @return A string value representing the name of the parameter.
	*/
	public String getName(int parameterIndex) {
		return this.getColumnLabel(parameterIndex);
	}

	/**
	* Get the value of a parameter.
	* @param parameterIndex the index of the parameter of interest.
	* @return a double value representing the minimum possible value of the parameter.
	*/
	public double getValue(int parameterIndex) {
		return this.getDouble(0, parameterIndex);
	}

	public String toString() {
		StringBuffer sb = new StringBuffer(1024);
		for (int i = 0 ; i < this.getNumParameters() ; i++) {
			if (i > 0) sb.append(',');
			sb.append(this.getValue(i));
		}
		return sb.toString();
	}

	/**
	* Get the value of a parameter.
	* @param name is a string which names the parameter of interest.
	* @return a double value representing the minimum possible value of the parameter.
	*/
	public double getValue(String name) throws Exception {
		return getValue(getParameterIndex(name));
	}


	/**
	* Get the parameter index of that corresponds to the given name.
	* @return an integer representing the index of the parameters.
	*/
	public int getParameterIndex(String name) throws Exception {

		for (int i = 0; i < getNumParameters(); i++) {
			if (getName(i).equals(name))
				return i;
		}
		Exception e = new Exception();
		System.out.println("Error!  Can not find name (" + name + ").  ");
		throw e;
	}

	/**
	* Get the parameter index of that corresponds to the given name.
	* @return an integer representing the index of the parameters.
	*/
	public ParameterPoint [] segmentPoint(ParameterPoint point, int splitIndex) {
		return null;
	}

	/**
	 * Get an Object from the table.
	 * @param row the row of the table
	 * @param column the column of the table
	 * @return the Object at (row, column)
	 */
	public Object getObject(int row, int column){
		return this.getTable().getObject(row, column);
	}

	/**
	 * Get an int value from the table.
	 * @param row the row of the table
	 * @param column the column of the table
	 * @return the int at (row, column)
	 */
	public int getInt(int row, int column){
		return this.getTable().getInt(row, column);
	}

	/**
	 * Get a short value from the table.
	 * @param row the row of the table
	 * @param column the column of the table
	 * @return the short at (row, column)
	 */
	public short getShort(int row, int column){
		return this.getTable().getShort(row, column);
	}

	/**
	 * Get a float value from the table.
	 * @param row the row of the table
	 * @param column the column of the table
	 * @return the float at (row, column)
	 */
	public float getFloat(int row, int column){
		return this.getTable().getFloat(row, column);
	}

	/**
	 * Get a double value from the table.
	 * @param row the row of the table
	 * @param column the column of the table
	 * @return the double at (row, column)
	 */
	public double getDouble(int row, int column){
		return this.getTable().getDouble(row, column);
	}

	/**
	 * Get a long value from the table.
	 * @param row the row of the table
	 * @param column the column of the table
	 * @return the long at (row, column)
	 */
	public long getLong(int row, int column){
		return this.getTable().getLong(row, column);
	}

	/**
	 * Get a String value from the table.
	 * @param row the row of the table
	 * @param column the column of the table
	 * @return the String at (row, column)
	 */
	public String getString(int row, int column){
		return this.getTable().getString(row, column);
	}

	/**
	 * Get a value from the table as an array of bytes.
	 * @param row the row of the table
	 * @param column the column of the table
	 * @return the value at (row, column) as an array of bytes
	 */
	public byte[] getBytes(int row, int column){
		return this.getTable().getBytes(row, column);
	}

	/**
	 * Get a boolean value from the table.
	 * @param row the row of the table
	 * @param column the column of the table
	 * @return the boolean value at (row, column)
	 */
	public boolean getBoolean(int row, int column){
		return this.getTable().getBoolean(row, column);
	}

	/**
	 * Get a value from the table as an array of chars.
	 * @param row the row of the table
	 * @param column the column of the table
	 * @return the value at (row, column) as an array of chars
	 */
	public char[] getChars(int row, int column){
		return this.getTable().getChars(row, column);
	}

	/**
	 * Get a byte value from the table.
	 * @param row the row of the table
	 * @param column the column of the table
	 * @return the byte value at (row, column)
	 */
	public byte getByte(int row, int column){
		return this.getTable().getByte(row, column);
	}

	/**
	 * Get a char value from the table.
	 * @param row the row of the table
	 * @param column the column of the table
	 * @return the char value at (row, column)
	 */
	public char getChar(int row, int column){
		return this.getTable().getChar(row, column);
	}

	//////////////////////////////////////
	//// Accessing Table Metadata

	/**
		Returns the name associated with the column.
		@param position the index of the Column name to get.
		@return the name associated with the column.
	*/
	public String getColumnLabel(int position){
		return this.getTable().getColumnLabel(position);
	}

	/**
		Returns the comment associated with the column.
		@param position the index of the Column name to get.
		@return the comment associated with the column.
	*/
	public String getColumnComment(int position){
		return this.getTable().getColumnComment(position);
	}

	/**
		Get the label associated with this Table.
		@return the label which describes this Table
	*/
	public String getLabel(){
		return this.getTable().getLabel();
	}

	/**
		Set the label associated with this Table.
		@param labl the label which describes this Table
	*/
	public void setLabel(String labl){
		this.getTable().setLabel(labl);
	}

	/**
		Get the comment associated with this Table.
		@return the comment which describes this Table
	*/
	public String getComment(){
		return this.getTable().getComment();
	}

	/**
		Set the comment associated with this Table.
		@param comment the comment which describes this Table
	*/
	public void setComment(String comment){
		this.getTable().setComment(comment);
	}

	/**
		  Get the number of rows in this Table.  Same as getCapacity().
		@return the number of rows in this Table.
	*/
	public int getNumRows(){
		return this.getTable().getNumRows();
	}

	/**
		Return the number of columns this table holds.
		@return the number of columns in this table
	*/
	public int getNumColumns(){
		return this.getTable().getNumColumns();
	}

	/**
		Get a subset of this Table, given a start position and length.  The
		subset will be a new Table.
		@param start the start position for the subset
		@param len the length of the subset
		@return a subset of this Table
	*/
	public Table getSubset(int start, int len){
		return this.getTable().getSubset(start, len);
	}

	/**
	 * get a subset of the table consisting of the rows identified by the array
	 * of indices passed in.
	 * @param rows the rows to be in the subset.
	 * @return
	 */
	public Table getSubset(int[] rows){
		return this.getTable().getSubset(rows);
	}

	/**
	 * Create a copy of this Table. This is a deep copy, and it contains a copy of
	 * 	all the data.
	 * @return a copy of this Table
	 */
	public Table copy(){
		return this.getTable().copy();
	}

	/**
	 * Create a copy of this Table. This is a deep copy, and it contains a copy of
	 * 	all the data.
	 * @return a copy of this Table
	 */
	public Table copy(int start, int len){
		return this.getTable().copy(start, len);
	}

	/**
	 * Create a copy of this Table. This is a deep copy, and it contains a copy of
	 * 	all the data.
	 * @return a copy of this Table
	 */
	public Table copy(int [] rows){
		return this.getTable().copy(rows);
	}
	/**
	 * Create a copy of this Table. A copy of every field in the class should be made,
	 * but the data itself should not be copied.
	 * @return a shallow copy of this Table
	 */
	public Table shallowCopy(){
		return this.getTable().shallowCopy();
	}

	/**
	 * Returns true if the column at position contains nominal data, false
	 * otherwise.
	 * @param position the index of the column
	 * @return true if the column contains nominal data, false otherwise.
	 */
	public boolean isColumnNominal(int position){
		return this.getTable().isColumnNominal(position);
	}

	/**
	 * Returns true if the column at position contains scalar data, false
	 * otherwise
	 * @param position
	 * @return true if the column contains scalar data, false otherwise
	 */
	public boolean isColumnScalar(int position) {
	   return this.getTable().isColumnScalar(position);
	}

	 /**
	  * Set whether the column at position contains nominal data or not.
	  * @param value true if the column at position holds nominal data, false otherwise
	  * @param position the index of the column
	  */
	public void setColumnIsNominal(boolean value, int position) {
		this.getTable().setColumnIsNominal(value, position);
	}

	/**
	* Set whether the column at position contains scalar data or not.
	* @param value true if the column at position holds scalar data, false otherwise
	* @param position the index of the column
	*/
	public void setColumnIsScalar(boolean value, int position) {
		this.getTable().setColumnIsScalar(value, position);
	}

	/**
	 * Returns true if the column at position contains only numeric values,
	 * false otherwise.
	 * @param position the index of the column
	 * @return true if the column contains only numeric values, false otherwise
	 */
	public boolean isColumnNumeric(int position) {
	  return this.getTable().isColumnNumeric(position);
	}

	/**
	  * Return the type of column located at the given position.
	  * @param position the index of the column
	  * @return the column type
	  * @see ColumnTypes
	  */
	public int getColumnType(int position) {
		return this.getTable().getColumnType(position);
	}

	/**
	 * This method will return a Row object. The row object can be used over and over
	 * to access the rows of the table by setting it's index to access a particular row.
	 * @return a Row object that can access the rows of the table.
	 */
	public Row getRow () {
		return this;
	}

	/**
	  * Return this Table as an ExampleTable.
	  * @return This object as an ExampleTable
	  */
	public ExampleTable toExampleTable() {
	  return this.getTable().toExampleTable();
	}

	/**
	 * Return a new table.
	 * @param row
	 * @param col
	 * @return
	 */
	public MutableTable createTable() {
		return new MutableTableImpl();
	}

	/**
	 * Return true if the value at (row, col) is a missing value, false otherwise.
	 * @param row the row index
	 * @param col the column index
	* @return true if the value is missing, false otherwise
	*/
	public boolean isValueMissing(int row, int col) {
	  return this.getTable().isValueMissing(row, col);
	}

	/**
	* Return true if the value at (row, col) is an empty value, false otherwise.
	* @param row the row index
	* @param col the column index
	* @return true if the value is empty, false otherwise
	*/
	public boolean isValueEmpty(int row, int col) {
	  return this.getTable().isValueEmpty(row, col);
	}

	/**
	* Return true if any value in this Table is missing.
	* @return true if there are any missing values, false if there are no missing values
	*/
	public boolean hasMissingValues() {
	  return this.getTable().hasMissingValues();
	}

	/* (non-Javadoc)
	 * @see ncsa.d2k.modules.core.datatype.table.Table#hasMissingValues(int)
	 */
	public boolean hasMissingValues(int columnIndex) {
		return this.getColumn(columnIndex).hasMissingValues();
	}
	/**
	 * Return a column representing the data in column n.
	 * @param n the column to get.
	 * @return a column representing the data.
	 */
	public Column getColumn(int n) {
	  return this.getTable().getColumn(n);
	}
} /* ParameterPoint */