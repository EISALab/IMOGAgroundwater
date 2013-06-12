package ncsa.d2k.modules.core.datatype.table.basic;
import ncsa.d2k.modules.core.datatype.table.*;

import java.io.Serializable;

public class ExampleImpl extends RowImpl implements Serializable, Example {

	/** this is the index of the row to access. */
	private int index;

	/** these are the input columns. */
	private Column [] inputColumns;

	/** output columns */
	private Column [] outputColumns;

	/** the test columns */
	private int [] subset;

	public ExampleImpl () {
		super ();
	}
	public ExampleImpl (ExampleTableImpl et) {
		super(et);

		Column [] columns = et.getColumns();
		int [] inputInd = et.getInputFeatures();
		inputColumns = new Column [et.getNumInputFeatures()];
		for (int i = 0 ; i < inputColumns.length; i++) {
			this.inputColumns[i] = columns[inputInd[i]];
		}

		int [] outputInd = et.getOutputFeatures();
		outputColumns = new Column [et.getNumOutputFeatures()];
		for (int i = 0 ; i < this.outputColumns.length; i++) {
			this.outputColumns[i] = columns[outputInd[i]];
		}
		this.subset = et.getSubset();
	}

	/**
	 * This could potentially be subindexed.
	 * @param i
	 */
	final public void setIndex(int i) {
		this.index = this.subset[i];
	}

	/**
	 * Get the ith input as a double.
	 * @param i the input index
	 * @return the ith input as a double
	 */
	final public double getInputDouble(int i) {
		return inputColumns [i].getDouble(index);
	}

	/**
	 * Get the oth output as a double.
	 * @param o the output index
	 * @return the oth output as a double
	 */
	final public double getOutputDouble(int o) {
		return outputColumns [o].getDouble(index);
	}

	/**
	 * Get the ith input as a String.
	 * @param i the input index
	 * @return the ith input as a String
	 */
	final public String getInputString(int i) {
		return inputColumns [i].getString(index);
	}

	/**
	 * Get the oth output as a String.
	 * @param o the output index
	 * @return the oth output as a String
	 */
	final public String getOutputString(int o) {
		return outputColumns [o].getString(index);
	}

	/**
	 * Get the ith input as an int.
	 * @param i the input index
	 * @return the ith input as an int
	 */
	final public int getInputInt(int i) {
		return inputColumns [i].getInt(index);
	}

	/**
	 * Get the oth output as an int.
	 * @param o the output index
	 * @return the oth output as an int
	 */
	final public int getOutputInt(int o) {
		return outputColumns [o].getInt(index);
	}

	/**
	 * Get the ith input as a float.
	 * @param i the input index
	 * @return the ith input as a float
	 */
	final public float getInputFloat(int i) {
		return inputColumns [i].getFloat(index);
	}

	/**
	 * Get the oth output as a float.
	 * @param o the output index
	 * @return the oth output as a float
	 */
	final public float getOutputFloat(int o) {
		return outputColumns [o].getFloat(index);
	}

	/**
	 * Get the ith input as a short.
	 * @param i the input index
	 * @return the ith input as a short
	 */
	final public short getInputShort(int i) {
		return inputColumns [i].getShort(index);
	}

	/**
	 * Get the oth output as a short.
	 * @param o the output index
	 * @return the oth output as a short
	 */
	final public short getOutputShort(int o) {
		return outputColumns [o].getShort(index);
	}

	/**
	 * Get the ith input as a long.
	 * @param i the input index
	 * @return the ith input as a long
	 */
	final public long getInputLong(int i) {
		return inputColumns [i].getLong(index);
	}

	/**
	 * Get the oth output as a long.
	 * @param o the output index
	 * @return the ith output as a long
	 */
	final public long getOutputLong(int o) {
		return outputColumns [o].getLong(index);
	}

	/**
	 * Get the ith input as a byte.
	 * @param i the input index
	 * @return the ith input as a byte
	 */
	final public byte getInputByte(int i) {
		return inputColumns [i].getByte(index);
	}

	/**
	 * Get the oth output as a byte.
	 * @param o the output index
	 * @return the oth output as a byte
	 */
	final public byte getOutputByte(int o) {
		return outputColumns [o].getByte(index);
	}

	/**
	 * Get the ith input as an Object.
	 * @param i the input index
	 * @return the ith input as an Object.
	 */
	final public Object getInputObject(int i) {
		return inputColumns [i].getObject(index);
	}

	/**
	 * Get the oth output as an Object.
	 * @param o the output index
	 * @return the oth output as an Object
	 */
	final public Object getOutputObject(int o) {
		return outputColumns [o].getObject(index);
	}

	/**
	 * Get the ith input as a char.
	 * @param i the input index
	 * @return the ith input as a char
	 */
	final public char getInputChar(int i) {
		return inputColumns [i].getChar(index);
	}

	/**
	 * Get the oth output as a char.
	 * @param o the output index
	 * @return the oth output as a char
	 */
	final public char getOutputChar(int o) {
		return outputColumns [o].getChar(index);
	}

	/**
	 * Get the ith input as chars.
	 * @param i the input index
	 * @return the ith input as chars
	 */
	final public char[] getInputChars(int i) {
		return inputColumns [i].getChars(index);
	}

	/**
	 * Get the oth output as chars.
	 * @param o the output index
	 * @return the oth output as chars
	 */
	final public char[] getOutputChars(int o) {
		return outputColumns [o].getChars(index);
	}

	/**
	 * Get the ith input as bytes.
	 * @param i the input index
	 * @return the ith input as bytes.
	 */
	final public byte[] getInputBytes(int i) {
		return inputColumns [i].getBytes(index);
	}

	/**
	 * Get the oth output as bytes.
	 * @param o the output index
	 * @return the oth output as bytes.
	 */
	final public byte[] getOutputBytes(int o) {
		return outputColumns [o].getBytes(index);
	}

	/**
	 * Get the ith input as a boolean.
	 * @param i the input index
	 * @return the ith input as a boolean
	 */
	final public boolean getInputBoolean(int i) {
		return inputColumns [i].getBoolean(index);
	}

	/**
	 * Get the oth output as a boolean.
	 * @param o the output index
	 * @return the oth output as a boolean
	 */
	final public boolean getOutputBoolean(int o) {
		return outputColumns [o].getBoolean(index);
	}
}