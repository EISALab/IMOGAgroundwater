package ncsa.d2k.modules.core.datatype.table;

public interface Example extends Row {

	/**
	 * Get the ith input as a double.
	 * @param i the input index
	 * @return the ith input as a double
	 */
	public double getInputDouble(int i);

	/**
	 * Get the oth output as a double.
	 * @param o the output index
	 * @return the oth output as a double
	 */
	public double getOutputDouble(int o);

	/**
	 * Get the ith input as a String.
	 * @param i the input index
	 * @return the ith input as a String
	 */
	public String getInputString(int i);

	/**
	 * Get the oth output as a String.
	 * @param o the output index
	 * @return the oth output as a String
	 */
	public String getOutputString(int o);

	/**
	 * Get the ith input as an int.
	 * @param i the input index
	 * @return the ith input as an int
	 */
	public int getInputInt(int i);

	/**
	 * Get the oth output as an int.
	 * @param o the output index
	 * @return the oth output as an int
	 */
	public int getOutputInt(int o);

	/**
	 * Get the ith input as a float.
	 * @param i the input index
	 * @return the ith input as a float
	 */
	public float getInputFloat(int i);

	/**
	 * Get the oth output as a float.
	 * @param o the output index
	 * @return the oth output as a float
	 */
	public float getOutputFloat(int o);

	/**
	 * Get the ith input as a short.
	 * @param i the input index
	 * @return the ith input as a short
	 */
	public short getInputShort(int i);

	/**
	 * Get the oth output as a short.
	 * @param o the output index
	 * @return the oth output as a short
	 */
	public short getOutputShort(int o);

	/**
	 * Get the ith input as a long.
	 * @param i the input index
	 * @return the ith input as a long
	 */
	public long getInputLong(int i);

	/**
	 * Get the oth output as a long.
	 * @param o the output index
	 * @return the ith output as a long
	 */
	public long getOutputLong(int o);

	/**
	 * Get the ith input as a byte.
	 * @param i the input index
	 * @return the ith input as a byte
	 */
	public byte getInputByte(int i);

	/**
	 * Get the oth output as a byte.
	 * @param o the output index
	 * @return the oth output as a byte
	 */
	public byte getOutputByte(int o);

	/**
	 * Get the ith input as an Object.
	 * @param i the input index
	 * @return the ith input as an Object.
	 */
	public Object getInputObject(int i);

	/**
	 * Get the oth output as an Object.
	 * @param o the output index
	 * @return the oth output as an Object
	 */
	public Object getOutputObject(int o);

	/**
	 * Get the ith input as a char.
	 * @param i the input index
	 * @return the ith input as a char
	 */
	public char getInputChar(int i);

	/**
	 * Get the oth output as a char.
	 * @param o the output index
	 * @return the oth output as a char
	 */
	public char getOutputChar(int o);

	/**
	 * Get the ith input as chars.
	 * @param i the input index
	 * @return the ith input as chars
	 */
	public char[] getInputChars(int i);

	/**
	 * Get the oth output as chars.
	 * @param o the output index
	 * @return the oth output as chars
	 */
	public char[] getOutputChars(int o);

	/**
	 * Get the ith input as bytes.
	 * @param i the input index
	 * @return the ith input as bytes.
	 */
	public byte[] getInputBytes(int i);

	/**
	 * Get the oth output as bytes.
	 * @param o the output index
	 * @return the oth output as bytes.
	 */
	public byte[] getOutputBytes(int o);

	/**
	 * Get the ith input as a boolean.
	 * @param i the input index
	 * @return the ith input as a boolean
	 */
	public boolean getInputBoolean(int i);

	/**
	 * Get the oth output as a boolean.
	 * @param o the output index
	 * @return the oth output as a boolean
	 */
	public boolean getOutputBoolean(int o);
}