package ncsa.d2k.modules.core.datatype.table;

/**
 This is a table which provides access to only the rows which are part of
 the test dataset. This is done is such a way as to be transparent to the
 accessing objects. Used to test the effectiveness of the model.
 */
public interface TestTable extends PredictionTable {

	static final long serialVersionUID = 68099221590892385L;

    /**
     * Get the example table from which this table was derived.
	 * @return the example table from which this table was derived
     */
    public ExampleTable getExampleTable ();

    /**
	 * Get an int value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the int at (row, column)
     */
    public int getInt (int row, int column);

    /**
	 * Get a short value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the short at (row, column)
     */
    public short getShort (int row, int column);

    /**
	 * Get a long value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the long at (row, column)
     */
    public long getLong (int row, int column);

    /**
	 * Get a float value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the float at (row, column)
     */
    public float getFloat (int row, int column);

    /**
	 * Get a double value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the double at (row, column)
     */
    public double getDouble (int row, int column);

    /**
	 * Get a String from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the String at (row, column)
     */
    public String getString (int row, int column);

    /**
	 * Get an array of bytes from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the value at (row, column) as an array of bytes
     */
    public byte[] getBytes (int row, int column);

    /**
	 * Get an array of chars from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the value at (row, column) as an array of chars
     */
    public char[] getChars (int row, int column);

    /**
	 * Get a boolean value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the boolean value at (row, column)
     */
    public boolean getBoolean (int row, int column);

    /**
	 * Get a byte value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the byte value at (row, column)
     */
    public byte getByte (int row, int column);

    /**
	 * Get a char value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the char value at (row, column)
     */
    public char getChar (int row, int column);

    /**
     Get the number of entries in the train set.
     @return the size of the train set
     */
    public int getNumRows ();
}
