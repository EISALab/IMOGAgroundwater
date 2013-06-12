package ncsa.d2k.modules.core.datatype.table.db;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.io.sql.*;

/**
 * <p>Title: DBTestTable </p>
 * <p>Description: Database implementation for TestTable </p>
 * <p>Copyright: NCSA (c) 2002</p>
 * <p>Company: </p>
 * @author Sameer Mathur, David Clutter
 * @version 1.0
 */

class DBTestTable extends DBPredictionTable implements TestTable {

    private DBExampleTable original;

    DBTestTable (DBExampleTable orig, DBDataSource _dbtableconnection, DBConnection _dbconnection) {
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
        int numDBTableCols = dataSource.getNumDistinctColumns();
        int numDBPredCols = predictionSet.length;
        if (column < numDBTableCols)
            return super.getInt(testSet[row], column);
        else
            return getIntPrediction(row, column-numDBTableCols);
    }

    /**
	 * Get a short value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the short at (row, column)
     */
    public short getShort (int row, int column) {
        int numDBTableCols = dataSource.getNumDistinctColumns();
        int numDBPredCols = predictionSet.length;
        if (column < numDBTableCols)
            return super.getShort(testSet[row], column);
        else
            return getShortPrediction(row, column-numDBTableCols);
    }

    /**
	 * Get a long value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the long at (row, column)
     */
    public long getLong (int row, int column) {
        int numDBTableCols = dataSource.getNumDistinctColumns();
        int numDBPredCols = predictionSet.length;
        if (column < numDBTableCols)
            return super.getLong(testSet[row], column);
        else
            return getLongPrediction(row, column-numDBTableCols);
    }

    /**
	 * Get a float value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the float at (row, column)
     */
    public float getFloat (int row, int column) {
        int numDBTableCols = dataSource.getNumDistinctColumns();
        int numDBPredCols = predictionSet.length;
        if (column < numDBTableCols)
            return super.getFloat(testSet[row], column);
        else
            return getFloatPrediction(row, column-numDBTableCols);
    }

    /**
	 * Get a double value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the double at (row, column)
     */
    public double getDouble (int row, int column) {
        int numDBTableCols = dataSource.getNumDistinctColumns();
        int numDBPredCols = predictionSet.length;
        if (column < numDBTableCols)
            return super.getDouble(testSet[row], column);
        else
            return getDoublePrediction(row, column-numDBTableCols);
    }

    /**
	 * Get an array of bytes from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the value at (row, column) as an array of bytes
     */
    public byte[] getBytes (int row, int column)  {
        int numDBTableCols = dataSource.getNumDistinctColumns();
        int numDBPredCols = predictionSet.length;
        if (column < numDBTableCols)
            return super.getBytes(testSet[row], column);
        else
            return getBytesPrediction(row, column-numDBTableCols);
    }

    /**
	 * Get an array of chars from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the value at (row, column) as an array of chars
     */
    public char[] getChars (int row, int column)  {
        int numDBTableCols = dataSource.getNumDistinctColumns();
        int numDBPredCols = predictionSet.length;
        if (column < numDBTableCols)
            return super.getChars(testSet[row], column);
        else
            return getCharsPrediction(row, column-numDBTableCols);
    }

    /**
	 * Get a boolean value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the boolean value at (row, column)
     */
    public boolean getBoolean (int row, int column)  {
        int numDBTableCols = dataSource.getNumDistinctColumns();
        int numDBPredCols = predictionSet.length;
        if (column < numDBTableCols)
            return super.getBoolean(testSet[row], column);
        else
            return getBooleanPrediction(row, column-numDBTableCols);
    }

    /**
	 * Get a byte value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the byte value at (row, column)
     */
    public byte getByte (int row, int column)  {
        int numDBTableCols = dataSource.getNumDistinctColumns();
        int numDBPredCols = predictionSet.length;
        if (column < numDBTableCols)
            return super.getByte(testSet[row], column);
        else
            return getBytePrediction(row, column-numDBTableCols);
    }

    /**
	 * Get a char value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the char value at (row, column)
     */
    public char getChar (int row, int column)  {
        int numDBTableCols = dataSource.getNumDistinctColumns();
        int numDBPredCols = predictionSet.length;
        if (column < numDBTableCols)
            return super.getChar(testSet[row], column);
        else
            return getCharPrediction(row, column-numDBTableCols);
    }

    /**
     Get the number of entries in the train set.
     @return the size of the test set
     */
    public int getNumRows () {
        return testSet.length;
    }

    /**
     * Get a String value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the String at (row, column)
     */
    public String getString(int row, int column) {
        int numDBTableCols = dataSource.getNumDistinctColumns();
        int numDBPredCols = predictionSet.length;
        if (column < numDBTableCols)
            return super.getString(testSet[row], column);
        else
            return getStringPrediction(row, column-numDBTableCols);
    }

} //DBTestTable