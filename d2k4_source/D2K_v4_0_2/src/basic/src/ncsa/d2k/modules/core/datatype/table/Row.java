package ncsa.d2k.modules.core.datatype.table;

public interface Row {

	/**
	 * set the index of the row to access.
	 * @param i the index of the row to access.
	 */
	public void setIndex(int i);

	/**
	 * Get the ith input as a double.
	 * @param i the input index
	 * @return the ith input as a double
	 */
	public double getDouble(int i);

	/**
	 * Get the ith input as a String.
	 * @param i the input index
	 * @return the ith input as a String
	 */
	public String getString(int i);

	/**
	 * Get the ith input as an int.
	 * @param i the input index
	 * @return the ith input as an int
	 */
	public int getInt(int i);

	/**
	 * Get the ith input as a float.
	 * @param i the input index
	 * @return the ith input as a float
	 */
	public float getFloat(int i);

	/**
	 * Get the ith input as a short.
	 * @param i the input index
	 * @return the ith input as a short
	 */
	public short getShort(int i);

	/**
	 * Get the ith input as a long.
	 * @param i the input index
	 * @return the ith input as a long
	 */
	public long getLong(int i);

	/**
	 * Get the ith input as a byte.
	 * @param i the input index
	 * @return the ith input as a byte
	 */
	public byte getByte(int i);

	/**
	 * Get the ith input as an Object.
	 * @param i the input index
	 * @return the ith input as an Object.
	 */
	public Object getObject(int i);

	/**
	 * Get the ith input as a char.
	 * @param i the input index
	 * @return the ith input as a char
	 */
	public char getChar(int i);

	/**
	 * Get the ith input as chars.
	 * @param i the input index
	 * @return the ith input as chars
	 */
	public char[] getChars(int i);

	/**
	 * Get the ith input as bytes.
	 * @param i the input index
	 * @return the ith input as bytes.
	 */
	public byte[] getBytes(int i);

	/**
	 * Get the ith input as a boolean.
	 * @param i the input index
	 * @return the ith input as a boolean
	 */
	public boolean getBoolean(int i);

	/**
	 * Get a reference to the table this example is part of.
	 * @return a reference to the table.
	 */
	public Table getTable();
}