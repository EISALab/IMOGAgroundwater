package ncsa.d2k.modules.core.datatype.table.db;

import java.sql.*;
import java.util.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.db.sql.*;
import ncsa.d2k.modules.core.io.sql.*;

/**
 * <p>Title: DBPredictionTable </p>
 * <p>Description: Prediction Table Implementation for a Database </p>
 * <p>Copyright: NCSA (c) 2002</p>
 * <p>Company: </p>
 * @author Sameer Mathur, David Clutter
 * @version 1.0
 */

class DBPredictionTable extends DBExampleTable implements PredictionTable{

//////////////////////////////////////////////////////////////////////////////////////////

    protected int[] predictionSet; // the indices of columns selected for prediction

    protected String     predictionSeqName;
    protected String     predictionTableName;

    protected String[]   predictionColNames;
    protected String[]   predictionColTypeNames;
    protected int[]      predictionColDisplaySizes;

    protected MutableResultSetDataSource predictionColumns;

    private static final String PREFIX = "PRE";
    private static final String SEQ = "SEQUENCE";

//////////////////////////////////////////////////////////////////////////////////////////

    DBPredictionTable (DBExampleTable orig,
                              DBDataSource _dbdatasource,
                              DBConnection _dbconnection) {

        super(orig, _dbdatasource, _dbconnection);

        this.predictionSet = new int[this.outputColumns.length];
        for (int i=0; i<predictionSet.length; i++)
            predictionSet[i] = dataSource.getNumDistinctColumns() + i;

        // This is temporary, remove when runFinalizers is added to d2k.
        //System.runFinalizersOnExit(true);

        // 1. Generate a name for the Prediction Table and the Prediction Sequence
        Random random = new Random();
        int nextInt = random.nextInt();
        nextInt = Math.abs(nextInt);

        this.predictionTableName = PREFIX+nextInt;
        this.predictionSeqName = SEQ+nextInt;

        // 3. determine the names,datatypes,size of the output columns.
        predictionColNames = new String[predictionSet.length];
        predictionColTypeNames = new String[predictionSet.length];
        predictionColDisplaySizes = new int[predictionSet.length];

        for (int i=0; i<this.outputColumns.length; i++) {
            predictionColNames[i] = getColumnLabel(outputColumns[i])+"_Predictions";
            predictionColTypeNames[i] = getColumnTypeName(getColumnType(outputColumns[i]));
            predictionColDisplaySizes[i] = 128;
        }

        predictionColumns = new MutableResultSetDataSource(dbConnection,
                                                           predictionTableName,
                                                           predictionSeqName,
                                                           predictionColNames,
                                                           predictionColTypeNames,
                                                           predictionColDisplaySizes,
                                                           getNumRows());
    }


//////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Set the prediction set
	 * @return the prediciton set
     */
    public int[] getPredictionSet () {
        return predictionSet;
    }

    /**
	 * Set the prediction set
	 * @param p the new prediciton set
     */
    public void setPredictionSet (int[] p) {
        predictionSet = p;
    }

    /**
     * Set an int prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param prediction the value of the prediciton
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     */
    public void setIntPrediction(int prediction, int row, int predictionColIdx) {
        predictionColumns.setInt(prediction, row, predictionColIdx);
    }

    /**
     * Set a float prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param prediction the value of the prediciton
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     */
    public void setFloatPrediction(float prediction, int row, int predictionColIdx) {
        predictionColumns.setFloat(prediction, row, predictionColIdx);
    }

    /**
     * Set a double prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param prediction the value of the prediciton
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     */
    public void setDoublePrediction(double prediction, int row, int predictionColIdx)  {
        setDoublePrediction(prediction, row, predictionColIdx);
    }

    /**
     * Set a long prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param prediction the value of the prediciton
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     */
    public void setLongPrediction(long prediction, int row, int predictionColIdx)  {
        predictionColumns.setLong(prediction, row, predictionColIdx);
    }

    /**
     * Set a short prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param prediction the value of the prediciton
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     */
    public void setShortPrediction(short prediction, int row, int predictionColIdx)  {
        predictionColumns.setShort(prediction, row, predictionColIdx);
    }

    /**
     * Set a boolean prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param prediction the value of the prediciton
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     */
    public void setBooleanPrediction(boolean prediction, int row, int predictionColIdx)  {
        predictionColumns.setBoolean(prediction, row, predictionColIdx);
    }

    /**
     * Set a String prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param prediction the value of the prediciton
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     */
    public void setStringPrediction(String prediction, int row, int predictionColIdx)  {
        predictionColumns.setString(prediction, row, predictionColIdx);
    }

    /**
     * Set a char[] prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param prediction the value of the prediciton
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     */
    public void setCharsPrediction(char[] prediction, int row, int predictionColIdx) {
        predictionColumns.setString(new String(prediction), row, predictionColIdx);
    }


    /**
     * Set a byte[] prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param prediction the value of the prediciton
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     */
    public void setBytesPrediction(byte[] prediction, int row, int predictionColIdx) {
        predictionColumns.setString(new String(prediction), row, predictionColIdx);
    }

    /**
     * Set an Object prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param prediction the value of the prediciton
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     */
    public void setObjectPrediction(Object prediction, int row, int predictionColIdx) {
        predictionColumns.setObject(prediction, row, predictionColIdx);
    }

    /**
     * Set a byte prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param prediction the value of the prediciton
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     */
    public void setBytePrediction(byte prediction, int row, int predictionColIdx) {
        byte[] val = {prediction};
        predictionColumns.setString(new String(val), row, predictionColIdx);
    }

    /**
     * Set a char prediciton in the specified prediction column.   The index into
     * the prediction set is used, not the actual column index.
     * @param prediction the value of the prediciton
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     */
    public void setCharPrediction(char prediction, int row, int predictionColIdx)  {
        char[] val = {prediction};
        predictionColumns.setString(new String(val), row, predictionColIdx);
    }

    /**
     * Get an int prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     * @return the prediction at (row, getPredictionSet()[predictionColIdx])
     */
    public int getIntPrediction(int row, int predictionColIdx) {
        return (int)predictionColumns.getNumericData(row, predictionColIdx);
    }

    /**
     * Get a float prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     * @return the prediction at (row, getPredictionSet()[predictionColIdx])
     */
    public float getFloatPrediction(int row, int predictionColIdx)  {
        return (float)predictionColumns.getNumericData(row, predictionColIdx);
    }

    /**
     * Get a double prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     * @return the prediction at (row, getPredictionSet()[predictionColIdx])
     */
    public double getDoublePrediction(int row, int predictionColIdx)  {
        return (double)predictionColumns.getNumericData(row, predictionColIdx);
    }

    /**
     * Get a long prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     * @return the prediction at (row, getPredictionSet()[predictionColIdx])
     */
    public long getLongPrediction(int row, int predictionColIdx) {
        return (long)predictionColumns.getNumericData(row, predictionColIdx);
    }

    /**
     * Get a short prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     * @return the prediction at (row, getPredictionSet()[predictionColIdx])
     */
    public short getShortPrediction(int row, int predictionColIdx) {
        return (short)predictionColumns.getNumericData(row, predictionColIdx);
    }

    /**
     * Get a boolean prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     * @return the prediction at (row, getPredictionSet()[predictionColIdx])
     */
    public boolean getBooleanPrediction(int row, int predictionColIdx)  {
        return predictionColumns.getBooleanData(row, predictionColIdx);
    }

    /**
     * Get a String prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     * @return the prediction at (row, getPredictionSet()[predictionColIdx])
     */
    public String getStringPrediction(int row, int predictionColIdx)  {
        return predictionColumns.getTextData(row, predictionColIdx);
    }

    /**
     * Get a char[] prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     * @return the prediction at (row, getPredictionSet()[predictionColIdx])
     */
    public char[] getCharsPrediction(int row, int predictionColIdx) {
        return predictionColumns.getTextData(row, predictionColIdx+1).toCharArray();
    }

    /**
     * Get a byte[] prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     * @return the prediction at (row, getPredictionSet()[predictionColIdx])
     */
    public byte[] getBytesPrediction(int row, int predictionColIdx) {
        return predictionColumns.getTextData(row, predictionColIdx+1).getBytes();
    }

    /**
     * Get an Object prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     * @return the prediction at (row, getPredictionSet()[predictionColIdx])
     */
    public Object getObjectPrediction(int row, int predictionColIdx) {
        return predictionColumns.getObjectData(row, predictionColIdx+1);
    }

    /**
     * Get a byte prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     * @return the prediction at (row, getPredictionSet()[predictionColIdx])
     */
    public byte getBytePrediction(int row, int predictionColIdx)  {
        return predictionColumns.getTextData(row, predictionColIdx+1).getBytes()[0];
    }

    /**
     * Get a char prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     * @return the prediction at (row, getPredictionSet()[predictionColIdx])
     */
    public char getCharPrediction(int row, int predictionColIdx) {
        return predictionColumns.getTextData(row, predictionColIdx+1).toCharArray()[0];
    }

    /**
     * Add a column of integer predictions to this PredictionTable.
     * @param predictions the predictions
     * @param label the label for the new column
     * @return the index of the prediction column in the prediction set
     */
    public int addPredictionColumn(int[] predictions, String label) {
        addPredictonColumnHelper(label);

        try {
            ResultSet rs = predictionColumns.rs; //this.dbConnection.getResultSet(tab, cols, "");
            int count = 0;
            while (rs.next()) {
                if (count< predictions.length) {
                    rs.updateInt(label, predictions[count]);
                    rs.updateRow();
                    count++;
                }
                else {
                    rs.updateNull(label);
                    rs.updateRow();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return predictionSet.length-1;
    }

    /**
     * Add a column of float predictions to this PredictionTable.
     * @param predictions the predictions
     * @param label the label for the new column
     * @return the index of the prediction column in the prediction set
     */
    public int addPredictionColumn(float[] predictions, String label) {
        addPredictonColumnHelper(label);

        try {
            ResultSet rs = predictionColumns.rs; //this.dbConnection.getResultSet(tab, cols, "");
            int count = 0;
            while (rs.next()) {
                if (count< predictions.length) {
                    rs.updateFloat(label, predictions[count]);
                    rs.updateRow();
                    count++;
                }
                else {
                    rs.updateNull(label);
                    rs.updateRow();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 5. return the correct index of the prediction column in the prediction set
        return predictionSet.length-1;
    }
    /**
     * Add a column of double predictions to this PredictionTable.
     * @param predictions the predictions
     * @param label the label for the new column
     * @return the index of the prediction column in the prediction set
     */
    public int addPredictionColumn(double[] predictions, String label) {
        addPredictonColumnHelper(label);

        try {
            ResultSet rs = predictionColumns.rs; //this.dbConnection.getResultSet(tab, cols, "");
            int count = 0;
            while (rs.next()) {
                if (count< predictions.length) {
                    rs.updateDouble(label, predictions[count]);
                    rs.updateRow();
                    count++;
                }
                else {
                    rs.updateNull(label);
                    rs.updateRow();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return predictionSet.length-1;
    }

    /**
     * Add a column of long predictions to this PredictionTable.
     * @param predictions the predictions
     * @param label the label for the new column
     * @return the index of the prediction column in the prediction set
     */
    public int addPredictionColumn(long[] predictions, String label) {
        addPredictonColumnHelper(label);

        try {
            ResultSet rs = predictionColumns.rs; //this.dbConnection.getResultSet(tab, cols, "");
            int count = 0;
            while (rs.next()) {
                if (count< predictions.length) {
                    rs.updateLong(label, predictions[count]);
                    rs.updateRow();
                    count++;
                }
                else {
                    rs.updateNull(label);
                    rs.updateRow();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return predictionSet.length-1;
    }

    /**
     * Add a column of short predictions to this PredictionTable.
     * @param predictions the predictions
     * @param label the label for the new column
     * @return the index of the prediction column in the prediction set
     */
    public int addPredictionColumn(short[] predictions, String label) {
        addPredictonColumnHelper(label);

        try {
            ResultSet rs = predictionColumns.rs; //this.dbConnection.getResultSet(tab, cols, "");
            int count = 0;
            while (rs.next()) {
                if (count< predictions.length) {
                    rs.updateShort(label, predictions[count]);
                    rs.updateRow();
                    count++;
                }
                else {
                    rs.updateNull(label);
                    rs.updateRow();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return predictionSet.length-1;
    }

    /**
     * Add a column of boolean predictions to this PredictionTable.
     * @param predictions the predictions
     * @param label the label for the new column
     * @return the index of the prediction column in the prediction set
     */
    public int addPredictionColumn(boolean[] predictions, String label) {
        addPredictonColumnHelper(label);

        try {
            ResultSet rs = predictionColumns.rs; //this.dbConnection.getResultSet(tab, cols, "");
            int count = 0;
            while (rs.next()) {
                if (count< predictions.length) {
                    rs.updateBoolean(label, predictions[count]);
                    rs.updateRow();
                    count++;
                }
                else {
                    rs.updateNull(label);
                    rs.updateRow();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return predictionSet.length-1;

    }

    /**
     * Add a column of String predictions to this PredictionTable.
     * @param predictions the predictions
     * @param label the label for the new column
     * @return the index of the prediction column in the prediction set
     */
    public int addPredictionColumn(String[] predictions, String label) {
        addPredictonColumnHelper(label);

        try {
            ResultSet rs = predictionColumns.rs; //this.dbConnection.getResultSet(tab, cols, "");
            int count = 0;
            while (rs.next()) {
                if (count< predictions.length) {
                    rs.updateString(label, predictions[count]);
                    rs.updateRow();
                    count++;
                }
                else {
                    rs.updateNull(label);
                    rs.updateRow();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return predictionSet.length-1;
    }

    /**
     * Add a column of char[] predictions to this PredictionTable.
     * @param predictions the predictions
     * @param label the label for the new column
     * @return the index of the prediction column in the prediction set
     */
    public int addPredictionColumn(char[][] predictions, String label) {
        addPredictonColumnHelper(label);

        try {
            ResultSet rs = predictionColumns.rs; //this.dbConnection.getResultSet(tab, cols, "");
            int count = 0;
            while (rs.next()) {
                if (count< predictions.length) {
                    rs.updateString(label, new String(predictions[count]));
                    rs.updateRow();
                    count++;
                }
                else {
                    rs.updateNull(label);
                    rs.updateRow();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return predictionSet.length-1;
    }

    /**
     * Add a column of byte[] predictions to this PredictionTable.
     * @param predictions the predictions
     * @param label the label for the new column
     * @return the index of the prediction column in the prediction set
     */
    public int addPredictionColumn(byte[][] predictions, String label) {
        addPredictonColumnHelper(label);

        try {
            ResultSet rs = predictionColumns.rs; //this.dbConnection.getResultSet(tab, cols, "");
            int count = 0;
            while (rs.next()) {
                if (count< predictions.length) {
                    rs.updateString(label, new String(predictions[count]));
                    rs.updateRow();
                    count++;
                }
                else {
                    rs.updateNull(label);
                    rs.updateRow();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 5. return the correct index of the prediction column in the prediction set
        return predictionSet.length-1;
    }

    /**
     * Add a column of Object predictions to this PredictionTable.
     * @param predictions the predictions
     * @param label the label for the new column
     * @return the index of the prediction column in the prediction set
     */
    public int addPredictionColumn(Object[] predictions, String label) {
        addPredictonColumnHelper(label);

        try {
            ResultSet rs = predictionColumns.rs; //this.dbConnection.getResultSet(tab, cols, "");
            int count = 0;
            while (rs.next()) {
                if (count< predictions.length) {
                    rs.updateObject(label, predictions[count]);
                    rs.updateRow();
                    count++;
                }
                else {
                    rs.updateNull(label);
                    rs.updateRow();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return predictionSet.length-1;
    }

    /**
     * Add a column of byte predictions to this PredictionTable.
     * @param predictions the predictions
     * @param label the label for the new column
     * @return the index of the prediction column in the prediction set
     */
    public int addPredictionColumn(byte[] predictions, String label) {
        addPredictonColumnHelper(label);

        try {
            ResultSet rs = predictionColumns.rs; //this.dbConnection.getResultSet(tab, cols, "");
            int count = 0;
            while (rs.next()) {
                if (count< predictions.length) {
                    byte[] bytearray = {predictions[count]};
                    rs.updateString(label, new String(bytearray));
                    rs.updateRow();
                    count++;
                }
                else {
                    rs.updateNull(label);
                    rs.updateRow();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return predictionSet.length-1;
    }

    /**
     * Add a column of char predictions to this PredictionTable.
     * @param predictions the predictions
     * @param label the label for the new column
     * @return the index of the prediction column in the prediction set
     */
    public int addPredictionColumn(char[] predictions, String label) {
        addPredictonColumnHelper(label);

        try {
            ResultSet rs = predictionColumns.rs; //this.dbConnection.getResultSet(tab, cols, "");
            int count = 0;
            while (rs.next()) {
                if (count< predictions.length) {
                    char[] chararray = {predictions[count]};
                    rs.updateString(label, new String(chararray));
                    rs.updateRow();
                    count++;
                }
                else {
                    rs.updateNull(label);
                    rs.updateRow();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 5. return the correct index of the prediction column in the prediction set
        return predictionSet.length-1;
    }

    public int getNumColumns() {
        // #columns in DBTable + #columns in DBPredictionTable -1 (do not count SEQ_NUM)
        return dataSource.getNumDistinctColumns() + predictionSet.length;
    }

////////////////// BEGIN overloaded  get<DataType>() functions ///////////////////////////
    /**
	 * Get an Object from the database table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the Object at (row, column)
     */
    public Object getObject(int row, int column){
        int numDBTableCols = dataSource.getNumDistinctColumns();
        int numDBPredCols = predictionSet.length;
        if (column < numDBTableCols)
            return super.getObject(row, column);
        else
            return this.getObjectPrediction(row, column-numDBTableCols);
    }

    /**
	 * Get an int value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the int at (row, column)
     */
    public int getInt(int row, int column) {
        int numDBTableCols = dataSource.getNumDistinctColumns();
        int numDBPredCols = predictionSet.length;
        if (column < numDBTableCols)
            return super.getInt(row, column);
        else
            return this.getIntPrediction(row, column-numDBTableCols);
    }


    /**
	 * Get a short value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the short at (row, column)
     */
    public short getShort(int row, int column) {
        int numDBTableCols = dataSource.getNumDistinctColumns();
        int numDBPredCols = predictionSet.length;
        if (column < numDBTableCols)
            return super.getShort(row, column);
        else
            return this.getShortPrediction(row, column-numDBTableCols);
    }

    /**
	 * Get a float value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the float at (row, column)
     */
    public float getFloat(int row, int column) {
        int numDBTableCols = dataSource.getNumDistinctColumns();
        int numDBPredCols = predictionSet.length;
        if (column < numDBTableCols)
            return super.getFloat(row, column);
        else
            return this.getFloatPrediction(row, column-numDBTableCols);
    }

    /**
	 * Get a double value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the double at (row, column)
     */
    public double getDouble(int row, int column) {
        int numDBTableCols = dataSource.getNumDistinctColumns();
        int numDBPredCols = predictionSet.length;
        if (column < numDBTableCols)
            return super.getDouble(row, column);
        else
            return this.getDoublePrediction(row, column-numDBTableCols);
    }

    /**
	 * Get a long value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the long at (row, column)
     */
     public long getLong(int row, int column){
        int numDBTableCols = dataSource.getNumDistinctColumns();
        int numDBPredCols = predictionSet.length;
        if (column < numDBTableCols)
            return super.getLong(row, column);
        else
            return this.getLongPrediction(row, column-numDBTableCols);
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
            return super.getString(row, column);
        else
            return this.getStringPrediction(row, column-numDBTableCols);
    }

    /**
	 * Get a value from the table as an array of bytes.
     * @param row the row of the table
     * @param column the column of the table
     * @return the value at (row, column) as an array of bytes
     */
    public byte[] getBytes(int row, int column) {
        int numDBTableCols = dataSource.getNumDistinctColumns();
        int numDBPredCols = predictionSet.length;
        if (column < numDBTableCols)
            return super.getBytes(row, column);
        else
            return this.getBytesPrediction(row, column-numDBTableCols);
    }

    /**
	 * Get a boolean value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the boolean value at (row, column)
     */
    public boolean getBoolean(int row, int column){
        int numDBTableCols = dataSource.getNumDistinctColumns();
        int numDBPredCols = predictionSet.length;
        if (column < numDBTableCols)
            return super.getBoolean(row, column);
        else
            return this.getBooleanPrediction(row, column-numDBTableCols);
    }

    /**
	 * Get a value from the table as an array of chars.
     * @param row the row of the table
     * @param column the column of the table
     * @return the value at (row, column) as an array of chars
     */
    public char[] getChars(int row, int column){
        int numDBTableCols = dataSource.getNumDistinctColumns();
        int numDBPredCols = predictionSet.length;
        if (column < numDBTableCols)
            return super.getChars(row, column);
        else
            return this.getCharsPrediction(row, column-numDBTableCols);
    }

    /**
	 * Get a byte value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the byte value at (row, column)
     */
    public byte getByte(int row, int column){
        int numDBTableCols = dataSource.getNumDistinctColumns();
        int numDBPredCols = predictionSet.length;
        if (column < numDBTableCols)
            return super.getByte(row, column);
        else
            return this.getBytePrediction(row, column-numDBTableCols);
    }

    /**
	 * Get a char value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the char value at (row, column)
     */
    public char getChar(int row, int column){
        int numDBTableCols = dataSource.getNumDistinctColumns();
        int numDBPredCols = predictionSet.length;
        if (column < numDBTableCols)
            return super.getChar(row, column);
        else
            return this.getCharPrediction(row, column-numDBTableCols);
    }
////////////////// END overloaded  get<DataType>() functions /////////////////////////////

    public String getColumnLabel(int position){
        int numDBTableCols = dataSource.getNumDistinctColumns();
        int numDBPredCols = predictionSet.length;
        if (position < numDBTableCols)
            return  dataSource.getColumnLabel(position);
        else
            return predictionColumns.getColumnLabel(position-numDBTableCols+1);
    }




////////////////// BEGIN PRIVATE HELPER FUNCTIONS ////////////////////////////////////////

    private void addPredictonColumnHelper(String label) {
        // 1. add a new column called 'label' to the prediction table in the database
        this.dbConnection.addColumn (predictionTableName, label, "number");

        // 2. update the predictionSet (write a new entry into the int[])
        this.updatePredictionSet(label);

        // 3. write predictions array data into this column
        String[] tab = {label};
        String[][] cols = new String[1][];
        String[] columns = {"SEQ_NUM", label};
        cols[0] = columns;

        // 4. Create new MutableResultSetDatasource
        predictionColNames = new String[predictionSet.length];
        predictionColTypeNames = new String[predictionSet.length];
        predictionColDisplaySizes = new int[predictionSet.length];

        for (int i=0; i<this.outputColumns.length; i++) {
            predictionColNames[i] =          getColumnLabel(outputColumns[i]);
            predictionColTypeNames[i]     =  getColumnTypeName(getColumnType(outputColumns[i]));
            predictionColDisplaySizes[i]  =  128;
        }

        predictionColumns = new MutableResultSetDataSource(dbConnection,
                                                           predictionTableName,
                                                           predictionSeqName,
                                                           predictionColNames,
                                                           predictionColTypeNames,
                                                           predictionColDisplaySizes,
                                                           getNumRows());
    }

   private String getColumnTypeName (int columnType) {
        String typeName;
        switch (columnType) {
            case ColumnTypes.INTEGER:
            case ColumnTypes.DOUBLE:
            case ColumnTypes.FLOAT:
                typeName = "number";
                break;
            case ColumnTypes.STRING:
            case ColumnTypes.CHAR:
            case ColumnTypes.OBJECT:
                typeName = "varchar";
                break;
            default:
                typeName = "varchar";
        } //switch
        return typeName;
    }

    private void updatePredictionSet(String label) {
        // create a backup copy..
        int[] backup = new int[predictionSet.length];
        for (int j=0; j<predictionSet.length; j++)
            backup[j] = predictionSet[j];

        // recreate the predictionSet and copy over the backup data..
        predictionSet = new int[backup.length + 1];
        int k=0;
        for (k=0; k<backup.length; k++)
            predictionSet[k] = backup[k];

        int numCols = dataSource.getNumDistinctColumns(); //3
        int predictionCol = 0;

        // find the column number of the newly added column
        String[] columns = this.dbConnection.getColumnNames(this.predictionTableName);
        int m;
        for (m=0; m<columns.length; m++){
            if (columns[m] == label)
                break;
        }
        predictionCol = m; //3
        predictionSet[backup.length] = numCols + predictionCol;
    }

////////////////// END PRIVATE HELPER FUNCTIONS //////////////////////////////////////////
}//DBPredictionTable
