package ncsa.d2k.modules.core.datatype.table.db;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.io.sql.*;

/**
 * <p>Title: DBTrainTable </p>
 * <p>Description: Database implementation for TrainTable </p>
 * <p>Copyright: NCSA (c) 2002</p>
 * <p>Company: </p>
 * @author Sameer Mathur, David Clutter
 * @version 1.0
 */

class DBTrainTable extends DBExampleTable implements TrainTable{

	private DBExampleTable original;
	DBTrainTable(DBExampleTable orig, DBDataSource _dbtableconnection, DBConnection _dbconnection) {
		super(orig, _dbtableconnection, _dbconnection);
		original = orig;
	}

	/**
	 * Get the example table from which this table was derived.
	 * @return the example table from which this table was derived
	 */
	public ExampleTable getExampleTable () {
		return original;
	}

	/**
	 * Get an int value from the table.
	 * @param row the row of the table
	 * @param column the column of the table
	 * @return the int at (row, column)
	 */
	public int getInt (int row, int column) {
		return super.getInt(trainSet[row], column);
	}

	/**
	 * Get a short value from the table.
	 * @param row the row of the table
	 * @param column the column of the table
	 * @return the short at (row, column)
	 */
	public short getShort (int row, int column) {
		return super.getShort(trainSet[row], column);
	}

	/**
	 * Get a long value from the table.
	 * @param row the row of the table
	 * @param column the column of the table
	 * @return the long at (row, column)
	 */
	public long getLong (int row, int column) {
		return super.getLong(trainSet[row], column);
	}

	/**
	 * Get a float value from the table.
	 * @param row the row of the table
	 * @param column the column of the table
	 * @return the float at (row, column)
	 */
	public float getFloat (int row, int column) {
		return super.getFloat(trainSet[row], column);
	}

	/**
	 * Get a double value from the table.
	 * @param row the row of the table
	 * @param column the column of the table
	 * @return the double at (row, column)
	 */
	public double getDouble (int row, int column) {
		return super.getDouble(trainSet[row], column);
	}

	/**
	 * Get a String from the table.
	 * @param row the row of the table
	 * @param column the column of the table
	 * @return the String at (row, column)
	 */
	public String getString (int row, int column) {
		return super.getString(trainSet[row], column);
	}

	/**
	 * Get an array of bytes from the table.
	 * @param row the row of the table
	 * @param column the column of the table
	 * @return the value at (row, column) as an array of bytes
	 */
	public byte[] getBytes (int row, int column) {
		return super.getBytes(trainSet[row], column);
	}

	/**
	 * Get an array of chars from the table.
	 * @param row the row of the table
	 * @param column the column of the table
	 * @return the value at (row, column) as an array of chars
	 */
	public char[] getChars (int row, int column) {
		return super.getChars(trainSet[row], column);
	}

	/**
	 * Get a boolean value from the table.
	 * @param row the row of the table
	 * @param column the column of the table
	 * @return the boolean value at (row, column)
	 */
	public boolean getBoolean (int row, int column) {
		return super.getBoolean(trainSet[row], column);
	}

	/**
	 * Get a byte value from the table.
	 * @param row the row of the table
	 * @param column the column of the table
	 * @return the byte value at (row, column)
	 */
	public byte getByte (int row, int column) {
		return super.getByte(trainSet[row], column);
	}

	/**
	 * Get a char value from the table.
	 * @param row the row of the table
	 * @param column the column of the table
	 * @return the char value at (row, column)
	 */
	public char getChar (int row, int column) {
		return super.getChar(trainSet[row], column);
	}

	/**
	 Get the number of entries in the train set.
	 @return the size of the train set
	 */
	public int getNumRows () {
		return trainSet.length;
	}
	/**
	 * Given a starting postion and a number of rows, make a subset table.
	 * @param pos the first row to copy.
	 * @param len the number of rows.
	 * @return a table containing the subset.
	 */
	public Table getSubsetByReference(int pos, int len) {

		// Make a copy of the example table
		ExampleTable et  = this.toExampleTable();

		// now figure out the test and train sets
		int[] traincpy = new int [len];
		System.arraycopy(trainSet, pos, traincpy, 0, len);
		et.setTrainingSet(traincpy);
		return et.getTrainTable();
	}

	/**
	 * Given an array of the rows to be in a subset table, make the subset.
	 * @param rows the array of row indices to copy.
	 * @return the subset table.
	 */
	public Table getSubsetByReference(int[] rows) {
	  ExampleTable et = this.toExampleTable();

	  // now figure out the test and train sets
	  int[] traincpy = new int[rows.length];
	  for (int i = 0 ; i < traincpy.length ; i++) {
		  traincpy [i] = this.testSet[rows[i]];
	  }
	  et.setTrainingSet(traincpy);
	  return et.getTrainTable();
	}


}//DBTrainTable