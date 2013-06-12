package ncsa.d2k.modules.core.datatype.table.basic;
import ncsa.d2k.modules.core.datatype.table.*;

import java.io.Serializable;

public class RowImpl implements Row, Serializable {

	/** this is the index of the row to access. */
	private int index;

	/** this is the example table we are accessing. */
	private TableImpl table;

	/** columns in total. */
	private Column [] columns;

	public RowImpl () {
	}
	RowImpl (TableImpl et) {
		table = et;
		columns = table.getColumns();
	}

	/**
	 * Return the table this row is in.
	 * @return the table this row is in.
	 */
	public Table getTable() {
		return table;
	}

	/**
	 * This could potentially be subindexed.
	 * @param i
	 */
	public void setIndex(int i) {
		this.index = i;
	}
	/**
	 * Get the ith input as a double.
	 * @param i the input index
	 * @return the ith input as a double
	 */
	final public double getDouble(int i) {
		return columns [i].getDouble(index);
	}

	/**
	 * Get the ith input as a String.
	 * @param i the input index
	 * @return the ith input as a String
	 */
	final public String getString(int i) {
		return columns [i].getString(index);
	}

	/**
	 * Get the ith input as an int.
	 * @param i the input index
	 * @return the ith input as an int
	 */
	final public int getInt(int i) {
		return columns [i].getInt(index);
	}

	/**
	 * Get the ith input as a float.
	 * @param i the input index
	 * @return the ith input as a float
	 */
	final public float getFloat(int i) {
		return columns [i].getFloat(index);
	}

	/**
	 * Get the ith input as a short.
	 * @param i the input index
	 * @return the ith input as a short
	 */
	final public short getShort(int i) {
		return columns [i].getShort(index);
	}

	/**
	 * Get the ith input as a long.
	 * @param i the input index
	 * @return the ith input as a long
	 */
	final public long getLong(int i) {
		return columns [i].getLong(index);
	}

	/**
	 * Get the ith input as a byte.
	 * @param i the input index
	 * @return the ith input as a byte
	 */
	final public byte getByte(int i) {
		return columns [i].getByte(index);
	}

	/**
	 * Get the ith input as an Object.
	 * @param i the input index
	 * @return the ith input as an Object.
	 */
	final public Object getObject(int i) {
		return columns [i].getObject(index);
	}

	/**
	 * Get the ith input as a char.
	 * @param i the input index
	 * @return the ith input as a char
	 */
	final public char getChar(int i) {
		return columns [i].getChar(index);
	}

	/**
	 * Get the ith input as chars.
	 * @param i the input index
	 * @return the ith input as chars
	 */
	final public char[] getChars(int i) {
		return columns [i].getChars(index);
	}

	/**
	 * Get the ith input as bytes.
	 * @param i the input index
	 * @return the ith input as bytes.
	 */
	final public byte[] getBytes(int i) {
		return columns [i].getBytes(index);
	}

	/**
	 * Get the ith input as a boolean.
	 * @param i the input index
	 * @return the ith input as a boolean
	 */
	final public boolean getBoolean(int i) {
		return columns [i].getBoolean(index);
	}
}