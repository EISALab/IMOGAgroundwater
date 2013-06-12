package ncsa.d2k.modules.core.datatype.table;

/**
 * A Table that should be used by a PredictionModelModule to make predictions on
 * a dataset.  The prediction set designates the indices of the columns that are
 * filled with predictions.
 * <br><br>
 * A PredictionTable is partially mutable.  Only the prediction columns can be modified.
 * A newly constructed PredictionTable will have one extra column for each output
 * in the ExampleTable.  Entries in these extra columns can be accessed via the
 * methods defined in this class.  If the ExampleTable has no outputs, then the
 * prediction columns must be added manually via the appropriate addPredictionColumn() method.
 */
public interface PredictionTable extends ExampleTable {

    static final long serialVersionUID = -3140627186936758135L;
    public static final String PREDICTION_COLUMN_APPEND_TEXT = " Predictions";


    /**
     * Set the prediction set
	 * @return the prediciton set
     */
    public int[] getPredictionSet ();

    /**
	 * Set the prediction set
	 * @param p the new prediciton set
     */
    public void setPredictionSet (int[] p);

    /**
     * Set an int prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param prediction the value of the prediciton
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     */
    public void setIntPrediction(int prediction, int row, int predictionColIdx);

    /**
     * Set a float prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param prediction the value of the prediciton
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     */
    public void setFloatPrediction(float prediction, int row, int predictionColIdx);

    /**
     * Set a double prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param prediction the value of the prediciton
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     */
    public void setDoublePrediction(double prediction, int row, int predictionColIdx);

    /**
     * Set a long prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param prediction the value of the prediciton
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     */
    public void setLongPrediction(long prediction, int row, int predictionColIdx);

    /**
     * Set a short prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param prediction the value of the prediciton
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     */
    public void setShortPrediction(short prediction, int row, int predictionColIdx);

    /**
     * Set a boolean prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param prediction the value of the prediciton
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     */
    public void setBooleanPrediction(boolean prediction, int row, int predictionColIdx);

    /**
     * Set a String prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param prediction the value of the prediciton
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     */
    public void setStringPrediction(String prediction, int row, int predictionColIdx);

    /**
     * Set a char[] prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param prediction the value of the prediciton
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     */
    public void setCharsPrediction(char[] prediction, int row, int predictionColIdx);

    /**
     * Set a byte[] prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param prediction the value of the prediciton
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     */
    public void setBytesPrediction(byte[] prediction, int row, int predictionColIdx);

    /**
     * Set an Object prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param prediction the value of the prediciton
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     */
    public void setObjectPrediction(Object prediction, int row, int predictionColIdx);

    /**
     * Set a byte prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param prediction the value of the prediciton
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     */
    public void setBytePrediction(byte prediction, int row, int predictionColIdx);

    /**
     * Set a char prediciton in the specified prediction column.   The index into
     * the prediction set is used, not the actual column index.
     * @param prediction the value of the prediciton
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     */
    public void setCharPrediction(char prediction, int row, int predictionColIdx);

    /**
     * Get an int prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     * @return the prediction at (row, getPredictionSet()[predictionColIdx])
     */
    public int getIntPrediction(int row, int predictionColIdx);

    /**
     * Get a float prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     * @return the prediction at (row, getPredictionSet()[predictionColIdx])
     */
    public float getFloatPrediction(int row, int predictionColIdx);

    /**
     * Get a double prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     * @return the prediction at (row, getPredictionSet()[predictionColIdx])
     */
    public double getDoublePrediction(int row, int predictionColIdx);

    /**
     * Get a long prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     * @return the prediction at (row, getPredictionSet()[predictionColIdx])
     */
    public long getLongPrediction(int row, int predictionColIdx);

    /**
     * Get a short prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     * @return the prediction at (row, getPredictionSet()[predictionColIdx])
     */
    public short getShortPrediction(int row, int predictionColIdx);

    /**
     * Get a boolean prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     * @return the prediction at (row, getPredictionSet()[predictionColIdx])
     */
    public boolean getBooleanPrediction(int row, int predictionColIdx);

    /**
     * Get a String prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     * @return the prediction at (row, getPredictionSet()[predictionColIdx])
     */
    public String getStringPrediction(int row, int predictionColIdx);

    /**
     * Get a char[] prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     * @return the prediction at (row, getPredictionSet()[predictionColIdx])
     */
    public char[] getCharsPrediction(int row, int predictionColIdx);

    /**
     * Get a byte[] prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     * @return the prediction at (row, getPredictionSet()[predictionColIdx])
     */
    public byte[] getBytesPrediction(int row, int predictionColIdx);

    /**
     * Get an Object prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     * @return the prediction at (row, getPredictionSet()[predictionColIdx])
     */
    public Object getObjectPrediction(int row, int predictionColIdx);

    /**
     * Get a byte prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     * @return the prediction at (row, getPredictionSet()[predictionColIdx])
     */
    public byte getBytePrediction(int row, int predictionColIdx);

    /**
     * Get a char prediciton in the specified prediction column.  The index into
     * the prediction set is used, not the actual column index.
     * @param row the row of the table
     * @param predictionColIdx the index into the prediction set
     * @return the prediction at (row, getPredictionSet()[predictionColIdx])
     */
    public char getCharPrediction(int row, int predictionColIdx);
}
