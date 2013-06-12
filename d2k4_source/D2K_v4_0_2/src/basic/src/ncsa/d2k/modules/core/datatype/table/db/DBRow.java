package ncsa.d2k.modules.core.datatype.table.db;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.io.sql.*;

public class DBRow implements Row {

	/** this is the index of the row to access. */
	protected int index;

	/** this is the example table we are accessing. */
	protected DBTable table;

	  /* A reference for the DBTableConnection functionality */
    protected DBDataSource dataSource;
    protected DBConnection dbConnection;

    DBRow (DBDataSource _dataSource, DBConnection _dbConnection, DBTable _table) {
        table = _table;
	dataSource = _dataSource;
        dbConnection = _dbConnection;
        index = 0;
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
	 public double getDouble(int i) {
		return (double)dataSource.getNumericData(index, i);
	}

	/**
	 * Get the ith input as a String.
	 * @param i the input index
	 * @return the ith input as a String
	 */
	 public String getString(int i) {

                 return dataSource.getTextData(index, i);
	}

	/**
	 * Get the ith input as an int.
	 * @param i the input index
	 * @return the ith input as an int
	 */
	 public int getInt(int i) {
		return (int)dataSource.getNumericData(index, i);
	}

	/**
	 * Get the ith input as a float.
	 * @param i the input index
	 * @return the ith input as a float
	 */
	 public float getFloat(int i) {
		return (float)dataSource.getNumericData(index, i);
	}

	/**
	 * Get the ith input as a short.
	 * @param i the input index
	 * @return the ith input as a short
	 */
	 public short getShort(int i) {
		return (short)dataSource.getNumericData(index, i);
	}

	/**
	 * Get the ith input as a long.
	 * @param i the input index
	 * @return the ith input as a long
	 */
	 public long getLong(int i) {
		 return (long)dataSource.getNumericData(index, i);
	}

	/**
	 * Get the ith input as a byte.
	 * @param i the input index
	 * @return the ith input as a byte
	 */
	 public byte getByte(int i) {
		return dataSource.getTextData(index, i).getBytes()[0];
	}

	/**
	 * Get the ith input as an Object.
	 * @param i the input index
	 * @return the ith input as an Object.
	 */
	 public Object getObject(int i) {
		return (Object)dataSource.getObjectData(index, i).toString();
	}

	/**
	 * Get the ith input as a char.
	 * @param i the input index
	 * @return the ith input as a char
	 */
	 public char getChar(int i) {
		return dataSource.getTextData(index, i).toCharArray()[0];
	}

	/**
	 * Get the ith input as chars.
	 * @param i the input index
	 * @return the ith input as chars
	 */
	 public char[] getChars(int i) {
		return dataSource.getTextData(index, i).toCharArray();
	}

	/**
	 * Get the ith input as bytes.
	 * @param i the input index
	 * @return the ith input as bytes.
	 */
	 public byte[] getBytes(int i) {
		return dataSource.getTextData(index, i).getBytes();
	}

	/**
	 * Get the ith input as a boolean.
	 * @param i the input index
	 * @return the ith input as a boolean
	 */
	 public boolean getBoolean(int i) {
		 return new Boolean(dataSource.getTextData(index, i)).booleanValue();
	}
}