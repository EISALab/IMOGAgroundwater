package ncsa.d2k.modules.projects.dtcheng;
import ncsa.d2k.modules.core.datatype.table.*;
import java.util.*;

public class GeneralExampleSet implements java.io.Serializable {

  int numExamples;
  int numInputs;
  int numOutputs;
  public String [] inputNames;
  public String [] outputNames;

  MutableExample [] examples;


  public GeneralExampleSet()
    {
    }

  public ExampleTable shallowCopy()
    {
    GeneralExampleSet copy = new GeneralExampleSet();
    copy.examples    = this.examples;
    copy.numExamples = this.numExamples;
    copy.numInputs   = this.numInputs;
    copy.numOutputs  = this.numOutputs;
    copy.inputNames  = this.inputNames;
    copy.outputNames = this.outputNames;
    return (ExampleTable) copy;
    }

  public GeneralExampleSet(MutableExample [] examples)
    {
    this.examples    = examples;
    this.numExamples = examples.length;
    this.numInputs   = ((ExampleTable) (examples[0].getTable())).getNumInputFeatures();
    this.numOutputs  = ((ExampleTable) (examples[0].getTable())).getNumOutputFeatures();
    }

  public void setExample(int e1, ExampleTable exampleSet, int e2)
    {
    this.examples[e1] = ((GeneralExampleSet) exampleSet).examples[e2].copy();
    }


  public double getInputDouble(int e, int i)
    {
    return examples[e].getInputDouble(i);
    }

  public double getOutputDouble(int e, int i)
    {
    return examples[e].getOutputDouble(i);
    }


  public String getInputString(int e, int i)
    {
    return Double.toString(examples[e].getInputDouble(i));
    }

  public String getOutputString(int e, int i)
    {
    return Double.toString(examples[e].getOutputDouble(i));
    }

  public int getInputInt(int e, int i)
    {
    return (int) examples[e].getInputDouble(i);
    }

  public int getOutputInt(int e, int i)
    {
    return (int) examples[e].getOutputDouble(i);
    }

  public float getInputFloat(int e, int i)
    {
    return (float) examples[e].getInputDouble(i);
    }

  public float getOutputFloat(int e, int i)
    {
    return (float) examples[e].getOutputDouble(i);
    }

  public short getInputShort(int e, int i)
    {
    return (short) examples[e].getInputDouble(i);
    }

  public short getOutputShort(int e, int i)
    {
    return (short) examples[e].getOutputDouble(i);
    }

  public long getInputLong(int e, int i)
    {
    return (long) examples[e].getInputDouble(i);
    }

  public long getOutputLong(int e, int i)
    {
    return (long) examples[e].getOutputDouble(i);
    }

  public byte getInputByte(int e, int i)
    {
    return (byte) examples[e].getInputDouble(i);
    }

  public byte getOutputByte(int e, int i)
    {
    return (byte) examples[e].getOutputDouble(i);
    }

  public Object getInputObject(int e, int i)
    {
    return (Object) new Double(examples[e].getInputDouble(i));
    }

  public Object getOutputObject(int e, int i)
    {
    return (Object) new Double(examples[e].getOutputDouble(i));
    }

  public char getInputChar(int e, int i)
    {
    return (char) examples[e].getInputDouble(i);
    }

  public char getOutputChar(int e, int i)
    {
    return (char) examples[e].getOutputDouble(i);
    }

  public byte[] getInputBytes(int e, int i)
    {
    byte [] bytes = new byte[1];
    bytes[0] = (byte) examples[e].getInputDouble(i);
    return bytes;
    }

  public byte[] getOutputBytes(int e, int i)
    {
    byte [] bytes = new byte[1];
    bytes[0] = (byte) examples[e].getOutputDouble(i);
    return bytes;
    }

  public char[] getInputChars(int e, int i)
    {
    char [] chars = new char[1];
    chars[0] = (char) examples[e].getInputDouble(i);
    return chars;
    }

  public char[] getOutputChars(int e, int i)
    {
    char [] chars = new char[1];
    chars[0] = (char) examples[e].getOutputDouble(i);
    return chars;
    }

  public boolean getInputBoolean(int e, int i)
    {
    if (examples[e].getInputDouble(i) < 0.5)
      return false;
    else
      return true;
    }

  public boolean getOutputBoolean(int e, int i)
    {
    if (examples[e].getOutputDouble(i) < 0.5)
      return false;
    else
      return true;
    }

  public int getNumInputs(int e)
    {
    return this.numInputs;
    }

  public int getNumOutputs(int e)
    {
    return this.numOutputs;
    }

  public MutableExample getExample(int e)
    {
    return (MutableExample) examples[e];
    }


  public String [] getInputNames()
    {
    return this.inputNames;
    }

  public String [] getOutputNames()
    {
    return this.outputNames;
    }

  public String getInputName(int i)
    {
    return this.inputNames[i];
    }

  public String getOutputName(int i)
    {
    return this.outputNames[i];
    }

  public int getInputType(int i)
    {
    System.out.println("Must override this method!");
    return ColumnTypes.DOUBLE;
    }

  public int getOutputType(int i)
    {
    System.out.println("Must override this method!");
    return ColumnTypes.DOUBLE;
    }

  public boolean isInputNominal(int i)
    {
    return false;
    }

  public boolean isOutputNominal(int i)
    {
    return false;
    }

  public boolean isInputScalar(int i)
    {
    return true;
    }

  public boolean isOutputScalar(int i)
    {
    return true;
    }





  public void setInput(int e, int i, double value)
    {
    examples[e].setInputDouble(i, value);
    }

  public void setOutput(int e, int i, double value)
    {
    examples[e].setOutputDouble(i, value);
    }

  public void deleteInputs(boolean [] deleteFeatures)
    {
    System.out.println("!!! deleteInputs not defined");
    }








  /**
       * Get an Object from the table.
   * @param row the row of the table
   * @param column the column of the table
   * @return the Object at (row, column)
   */
  public Object getObject(int row, int column)
    {
    return null;
    }

  /**
       * Get an int value from the table.
   * @param row the row of the table
   * @param column the column of the table
   * @return the int at (row, column)
   */
  public int getInt(int row, int column)
    {
    if (column < numInputs)
      return (int) examples[row].getInputDouble(column);
    else
      return (int) examples[row].getOutputDouble(column - numInputs);
    }

  /**
       * Get a short value from the table.
   * @param row the row of the table
   * @param column the column of the table
   * @return the short at (row, column)
   */
  public short getShort(int row, int column)
    {
    if (column < numInputs)
      return (short) examples[row].getInputDouble(column);
    else
      return (short) examples[row].getOutputDouble(column - numInputs);
    }

  /**
       * Get a float value from the table.
   * @param row the row of the table
   * @param column the column of the table
   * @return the float at (row, column)
   */
  public float getFloat(int row, int column)
    {
    if (column < numInputs)
      return (float) examples[row].getInputDouble(column);
    else
      return (float) examples[row].getOutputDouble(column - numInputs);
    }

  /**
       * Get a double value from the table.
   * @param row the row of the table
   * @param column the column of the table
   * @return the double at (row, column)
   */
  public double getDouble(int row, int column)
    {
    if (column < numInputs)
      return (double) examples[row].getInputDouble(column);
    else
      return (double) examples[row].getOutputDouble(column - numInputs);
    }

  /**
       * Get a long value from the table.
   * @param row the row of the table
   * @param column the column of the table
   * @return the long at (row, column)
   */
  public long getLong(int row, int column)
    {
    if (column < numInputs)
      return (long) examples[row].getInputDouble(column);
    else
      return (long) examples[row].getOutputDouble(column - numInputs);
    }

  /**
       * Get a String value from the table.
   * @param row the row of the table
   * @param column the column of the table
   * @return the String at (row, column)
   */
  public String getString(int row, int column)
    {
    if (column < numInputs)
      {
      return Double.toString(examples[row].getInputDouble(column));
      }
    else
      return Double.toString(examples[row].getOutputDouble(column - numInputs));
    }

  /**
       * Get a value from the table as an array of bytes.
   * @param row the row of the table
   * @param column the column of the table
   * @return the value at (row, column) as an array of bytes
   */
  public byte[] getBytes(int row, int column)
    {
    return null;
    }

  /**
       * Get a boolean value from the table.
   * @param row the row of the table
   * @param column the column of the table
   * @return the boolean value at (row, column)
   */
  public boolean getBoolean(int row, int column)
    {
    return false;
    }

  /**
       * Get a value from the table as an array of chars.
   * @param row the row of the table
   * @param column the column of the table
   * @return the value at (row, column) as an array of chars
   */
  public char[] getChars(int row, int column)
    {
    return null;
    }

  /**
       * Get a byte value from the table.
   * @param row the row of the table
   * @param column the column of the table
   * @return the byte value at (row, column)
   */
  public byte getByte(int row, int column)
    {
    return 0;
    }

  /**
       * Get a char value from the table.
   * @param row the row of the table
   * @param column the column of the table
   * @return the char value at (row, column)
   */
  public char getChar(int row, int column)
    {
    return 0;
    }

  //////////////////////////////////////
  //// Accessing Table Metadata

  /**
          Return the index which represents the key column of this table.
          @return the key column index
  */
  public int getKeyColumn()
    {
    return 0;
    }

  /**
          Sets the key column index of this table.
          @param position the Column which is key for identifying unique rows
  */
  public void setKeyColumn(int position)
    {
    }

  /**
          Returns the name associated with the column.
          @param position the index of the Column name to get.
          @returns the name associated with the column.
  */
  public String getColumnLabel(int position)
    {
    String label = null;
    if (position < numInputs)
      label = inputNames[position];
    else
      label = outputNames[position - numInputs];
    return label;
    }

  /**
          Returns the comment associated with the column.
          @param position the index of the Column name to get.
          @returns the comment associated with the column.
  */
  public String getColumnComment(int position)
    {
    return null;
    }

  /**
          Get the label associated with this Table.
          @return the label which describes this Table
  */
  public String getLabel()
    {
    return null;
    }

  /**
          Set the label associated with this Table.
          @param labl the label which describes this Table
  */
  public void setLabel(String labl)
    {
    }

  /**
          Get the comment associated with this Table.
          @return the comment which describes this Table
  */
  public String getComment()
    {
    return null;
    }

  /**
          Set the comment associated with this Table.
          @param comment the comment which describes this Table
  */
  public void setComment(String comment)
    {
    }

  /**
          Get the number of rows in this Table.  Same as getCapacity().
          @return the number of rows in this Table.
  */
  public int getNumRows()
    {
    return numExamples;
    }

  /**
          Get the number of entries this Table holds.
          @return this Table's number of entries
  */
  public int getNumEntries()
    {
    return numExamples;
    }

  /**
          Return the number of columns this table holds.
          @return the number of columns in this table
  */
  public int getNumColumns()
    {
    return numInputs + numOutputs;
    }
   /**
    * Get all the entries from the specified row.  The caller must pass in
    * a buffer for the data to be copied into.  This buffer should be one of
    * following data types: int[], float[], double[], long[], short[], boolean[],
    * String[], char[][], byte[][], Object[], byte[], or char[].  The data from
    * the specified row will then be copied into the buffer.  If the length of
    * the buffer is greater than the number of columns in the table, an
    * ArrayIndexOutOfBoundsException will be thrown.
    * @param buffer the array to copy data into
    * @param pos the index of the row to copy
    */
    public void getRow (Object buffer, int pos) {
      if(buffer instanceof int[]) {
         int[] b1 = (int[])buffer;
         for(int i = 0; i < b1.length; i++)
            b1[i] = getInt(pos, i);
      }
      else if(buffer instanceof float[]) {
         float[] b1 = (float[])buffer;
         for(int i = 0; i < b1.length; i++)
            b1[i] = getFloat(pos, i);
      }
      else if(buffer instanceof double[]) {
         double[] b1 = (double[])buffer;
         for(int i = 0; i < b1.length; i++)
            b1[i] = getDouble(pos, i);
      }
      else if(buffer instanceof long[]) {
         long[] b1 = (long[])buffer;
         for(int i = 0; i < b1.length; i++)
            b1[i] = getLong(pos, i);
      }
      else if(buffer instanceof short[]) {
         short[] b1 = (short[])buffer;
         for(int i = 0; i < b1.length; i++)
            b1[i] = getShort(pos, i);
      }
      else if(buffer instanceof boolean[]) {
         boolean[] b1 = (boolean[])buffer;
         for(int i = 0; i < b1.length; i++)
            b1[i] = getBoolean(pos, i);
      }
      else if(buffer instanceof String[]) {
         String[] b1 = (String[])buffer;
         for(int i = 0; i < b1.length; i++)
            b1[i] = getString(pos, i);
      }
      else if(buffer instanceof char[][]) {
         char[][] b1 = (char[][])buffer;
         for(int i = 0; i < b1.length; i++)
            b1[i] = getChars(pos, i);
      }
      else if(buffer instanceof byte[][]) {
         byte[][] b1 = (byte[][])buffer;
         for(int i = 0; i < b1.length; i++)
            b1[i] = getBytes(pos, i);
      }
      else if(buffer instanceof Object[]) {
         Object[] b1 = (Object[])buffer;
         for(int i = 0; i < b1.length; i++)
            b1[i] = getObject(pos, i);
      }
      else if(buffer instanceof byte[]) {
         byte[] b1 = (byte[])buffer;
         for(int i = 0; i < b1.length; i++)
            b1[i] = getByte(pos, i);
      }
      else if(buffer instanceof char[]) {
         char[] b1 = (char[])buffer;
         for(int i = 0; i < b1.length; i++)
            b1[i] = getChar(pos, i);
      }
    }

   /**
    * Get all the entries from the specified column.  The caller must pass in
    * a buffer for the data to be copied into.  This buffer should be one of
    * following data types: int[], float[], double[], long[], short[], boolean[],
    * String[], char[][], byte[][], Object[], byte[], or char[].  The data from
    * the specified row will then be copied into the buffer.  If the length of
    * the buffer is greater than the number of rows in the table, an
    * ArrayIndexOutOfBoundsException will be thrown.
    * @param buffer the array to copy data into
    * @param pos the index of the column to copy
    */
   public void getColumn (Object buffer, int pos) {
      if(buffer instanceof int[]) {
         int[] b1 = (int[])buffer;
         for(int i = 0; i < b1.length; i++)
            b1[i] = getInt(i, pos);
      }
      else if(buffer instanceof float[]) {
         float[] b1 = (float[])buffer;
         for(int i = 0; i < b1.length; i++)
            b1[i] = getFloat(i, pos);
      }
      else if(buffer instanceof double[]) {
         double[] b1 = (double[])buffer;
         for(int i = 0; i < b1.length; i++)
            b1[i] = getDouble(i, pos);
      }
      else if(buffer instanceof long[]) {
         long[] b1 = (long[])buffer;
         for(int i = 0; i < b1.length; i++)
            b1[i] = getLong(i, pos);
      }
      else if(buffer instanceof short[]) {
         short[] b1 = (short[])buffer;
         for(int i = 0; i < b1.length; i++)
            b1[i] = getShort(i, pos);
      }
      else if(buffer instanceof boolean[]) {
         boolean[] b1 = (boolean[])buffer;
         for(int i = 0; i < b1.length; i++)
            b1[i] = getBoolean(i, pos);
      }
      else if(buffer instanceof String[]) {
         String[] b1 = (String[])buffer;
         for(int i = 0; i < b1.length; i++)
            b1[i] = getString(i, pos);
      }
      else if(buffer instanceof char[][]) {
         char[][] b1 = (char[][])buffer;
         for(int i = 0; i < b1.length; i++)
            b1[i] = getChars(i, pos);
      }
      else if(buffer instanceof byte[][]) {
         byte[][] b1 = (byte[][])buffer;
         for(int i = 0; i < b1.length; i++)
            b1[i] = getBytes(i, pos);
      }
      else if(buffer instanceof Object[]) {
         Object[] b1 = (Object[])buffer;
         for(int i = 0; i < b1.length; i++)
            b1[i] = getObject(i, pos);
      }
      else if(buffer instanceof byte[]) {
         byte[] b1 = (byte[])buffer;
         for(int i = 0; i < b1.length; i++)
            b1[i] = getByte(i, pos);
      }
      else if(buffer instanceof char[]) {
         char[] b1 = (char[])buffer;
         for(int i = 0; i < b1.length; i++)
            b1[i] = getChar(i, pos);
      }
   }


  /**
          Get a subset of this Table, given a start position and length.  The
          subset will be a new Table.
          @param start the start position for the subset
          @param len the length of the subset
          @return a subset of this Table
  */
  public Table getSubset(int start, int len)
    {
    return null;
    }

  /**
          Create a copy of this Table.
          @return a copy of this Table
  */
  public Table copy()
    {
    return null;
    }

  /**
   * Get a TableFactory for this Table.
   * @return The appropriate TableFactory for this Table.
   */
  public TableFactory getTableFactory()
    {
    return null;
    }

   /**
    * Returns true if the column at position contains nominal data, false
    * otherwise.
    * @param position the index of the column
    * @return true if the column contains nominal data, false otherwise.
    */
  public boolean isColumnNominal(int position)
    {
    return false;
    }

   /**
    * Returns true if the column at position contains scalar data, false
    * otherwise
    * @param position
    * @return true if the column contains scalar data, false otherwise
    */
  public boolean isColumnScalar(int position)
    {
    return true;
    }

   /**
    * Set whether the column at position contains nominal data or not.
    * @param value true if the column at position holds nominal data, false otherwise
    * @param position the index of the column
    */
  public void setColumnIsNominal(boolean value, int position)
    {
    }

  /**
  * Set whether the column at position contains scalar data or not.
  * @param value true if the column at position holds scalar data, false otherwise
  * @param position the index of the column
  */
  public void setColumnIsScalar(boolean value, int position)
    {
    }

  /**
  * Returns true if the column at position contains only numeric values,
  * false otherwise.
  * @param position the index of the column
  * @return true if the column contains only numeric values, false otherwise
  */
  public boolean isColumnNumeric(int position)
    {
    return true;
    }

  /**
  * Return the type of column located at the given position.
  * @param position the index of the column
  * @return the column type
  * @see ColumnTypes
  */
  public int getColumnType(int position)
    {
    return 0;
    }

  /**
  * Return this Table as an ExampleTable.
  * @return This object as an ExampleTable
  */
  public ExampleTable toExampleTable()
    {
    return (ExampleTable) this;
    }








  /////////// Collect the transformations that were performed. /////////
  /**
   Add the transformation to the list.
   @param tm the TransformationModule that performed the reversable transform.
   */
  /*
  public void addTransformation (TransformationModule tm)
    {
    }
*/
  /**
   Returns the list of all reversable transformations there were performed
   on the original dataset.
   @returns an ArrayList containing the TransformationModules which transformed the data.
   */
  public ArrayList getTransformations ()
    {
    return null;
    }

  //////////////  Input, output, test and train. ///////////////
  /**
   Returns an array of ints, the indices of the input columns.
   @return an array of ints, the indices of the input columns.
   */
  public int[] getInputFeatures ()
    {
    int [] inputFeatures = new int[numInputs];
    for (int i = 0; i < numInputs; i++)
      {
      inputFeatures[i] = i;
      }
    return inputFeatures;
    }

  /**
   Returns the number of input features.
   @returns the number of input features.
   */
  public int getNumInputFeatures ()
    {
    return numInputs;
    }

  /**
   Returns the number of example rows.
   @returns the number of example rows.
   */
  public int getNumExamples ()
    {
    return numExamples;
    }

  /**
   Return the number of examples in the training set.
   @returns the number of examples in the training set.
   */
  public int getNumTrainExamples ()
    {
    return 0;
    }

  /**
   Return the number of examples in the testing set.
   @returns the number of examples in the testing set.
   */
  public int getNumTestExamples ()
    {
    return 0;
    }

  /**
   Returns an array of ints, the indices of the output columns.
   @return an array of ints, the indices of the output columns.
   */
  public int[] getOutputFeatures ()
    {
    int [] outputFeatures = new int[numOutputs];
    for (int i = 0; i < numOutputs; i++)
      {
      outputFeatures[i] = i + numInputs;
      }
    return outputFeatures;
    }

  /**
   Get the number of output features.
   @returns the number of output features.
   */
  public int getNumOutputFeatures ()
    {
    return numOutputs;
    }

  /**
   Set the input features.
   @param inputs the indexes of the columns to be used as input features.
   */
  public void setInputFeatures (int[] inputs)
    {
    }

  /**
   Set the output features.
   @param outs the indexes of the columns to be used as output features.
   */
  public void setOutputFeatures (int[] outs)
    {
    }

  /**
   Set the indexes of the rows in the training set.
   @param trainingSet the indexes of the items to be used to train the model.
   */
  public void setTrainingSet (int[] trainingSet)
    {
    }

  /**
   Get the training set.
   @return the indices of the rows of the training set.
   */
  public int[] getTrainingSet ()
    {
    return null;
    }

  /**
   Set the indexes of the rows in the testing set.
   @param testingSet the indexes of the items to be used to test the model.
   */
  public void setTestingSet (int[] testingSet)
    {
    }

  /**
   Get the testing set.
   @return the indices of the rows of the testing set.
   */
  public int[] getTestingSet ()
    {
    return null;
    }

  /**
       * Return a reference to a Table referencing only the testing data.
       @return a reference to a Table referencing only the testing data
   */
  public TestTable getTestTable ()
    {
    return null;
    }

  /**
   Return a reference to a Table referencing only the training data.
   @return a reference to a Table referencing only the training data.
   */
  public TrainTable getTrainTable ()
    {
    return null;
    }

  /**
   * Return this ExampleTable as a PredictionTable.
   * @return This object as a PredictionTable
   */
  public PredictionTable toPredictionTable()
    {
    return null;
    }



  /**
   * Set the prediction set
       * @return the prediciton set
   */
  public int[] getPredictionSet ()
    {
    return null;
    }

  /**
       * Set the prediction set
       * @param p the new prediciton set
   */
  public void setPredictionSet (int[] p)
    {
    }

  /**
   * Set an int prediciton in the specified prediction column.  The index into
   * the prediction set is used, not the actual column index.
   * @param prediction the value of the prediciton
   * @param row the row of the table
   * @param predictionColIdx the index into the prediction set
   */
  public void setIntPrediction(int prediction, int row, int predictionColIdx)
    {
    }

  /**
   * Set a float prediciton in the specified prediction column.  The index into
   * the prediction set is used, not the actual column index.
   * @param prediction the value of the prediciton
   * @param row the row of the table
   * @param predictionColIdx the index into the prediction set
   */
  public void setFloatPrediction(float prediction, int row, int predictionColIdx)
    {
    }

  /**
   * Set a double prediciton in the specified prediction column.  The index into
   * the prediction set is used, not the actual column index.
   * @param prediction the value of the prediciton
   * @param row the row of the table
   * @param predictionColIdx the index into the prediction set
   */
  public void setDoublePrediction(double prediction, int row, int predictionColIdx)
    {
    }

  /**
   * Set a long prediciton in the specified prediction column.  The index into
   * the prediction set is used, not the actual column index.
   * @param prediction the value of the prediciton
   * @param row the row of the table
   * @param predictionColIdx the index into the prediction set
   */
  public void setLongPrediction(long prediction, int row, int predictionColIdx)
    {
    }

  /**
   * Set a short prediciton in the specified prediction column.  The index into
   * the prediction set is used, not the actual column index.
   * @param prediction the value of the prediciton
   * @param row the row of the table
   * @param predictionColIdx the index into the prediction set
   */
  public void setShortPrediction(short prediction, int row, int predictionColIdx)
    {
    }

  /**
   * Set a boolean prediciton in the specified prediction column.  The index into
   * the prediction set is used, not the actual column index.
   * @param prediction the value of the prediciton
   * @param row the row of the table
   * @param predictionColIdx the index into the prediction set
   */
  public void setBooleanPrediction(boolean prediction, int row, int predictionColIdx)
    {
    }

  /**
   * Set a String prediciton in the specified prediction column.  The index into
   * the prediction set is used, not the actual column index.
   * @param prediction the value of the prediciton
   * @param row the row of the table
   * @param predictionColIdx the index into the prediction set
   */
  public void setStringPrediction(String prediction, int row, int predictionColIdx)
    {
    }

  /**
   * Set a char[] prediciton in the specified prediction column.  The index into
   * the prediction set is used, not the actual column index.
   * @param prediction the value of the prediciton
   * @param row the row of the table
   * @param predictionColIdx the index into the prediction set
   */
  public void setCharsPrediction(char[] prediction, int row, int predictionColIdx)
    {
    }

  /**
   * Set a byte[] prediciton in the specified prediction column.  The index into
   * the prediction set is used, not the actual column index.
   * @param prediction the value of the prediciton
   * @param row the row of the table
   * @param predictionColIdx the index into the prediction set
   */
  public void setBytesPrediction(byte[] prediction, int row, int predictionColIdx)
    {
    }

  /**
   * Set an Object prediciton in the specified prediction column.  The index into
   * the prediction set is used, not the actual column index.
   * @param prediction the value of the prediciton
   * @param row the row of the table
   * @param predictionColIdx the index into the prediction set
   */
  public void setObjectPrediction(Object prediction, int row, int predictionColIdx)
    {
    }

  /**
   * Set a byte prediciton in the specified prediction column.  The index into
   * the prediction set is used, not the actual column index.
   * @param prediction the value of the prediciton
   * @param row the row of the table
   * @param predictionColIdx the index into the prediction set
   */
  public void setBytePrediction(byte prediction, int row, int predictionColIdx)
    {
    }

  /**
   * Set a char prediciton in the specified prediction column.   The index into
   * the prediction set is used, not the actual column index.
   * @param prediction the value of the prediciton
   * @param row the row of the table
   * @param predictionColIdx the index into the prediction set
   */
  public void setCharPrediction(char prediction, int row, int predictionColIdx)
    {
    }

  /**
   * Get an int prediciton in the specified prediction column.  The index into
   * the prediction set is used, not the actual column index.
   * @param row the row of the table
   * @param predictionColIdx the index into the prediction set
   * @return the prediction at (row, getPredictionSet()[predictionColIdx])
   */
  public int getIntPrediction(int row, int predictionColIdx)
    {
    return 0;
    }

  /**
   * Get a float prediciton in the specified prediction column.  The index into
   * the prediction set is used, not the actual column index.
   * @param row the row of the table
   * @param predictionColIdx the index into the prediction set
   * @return the prediction at (row, getPredictionSet()[predictionColIdx])
   */
  public float getFloatPrediction(int row, int predictionColIdx)
    {
    return 0;
    }

  /**
   * Get a double prediciton in the specified prediction column.  The index into
   * the prediction set is used, not the actual column index.
   * @param row the row of the table
   * @param predictionColIdx the index into the prediction set
   * @return the prediction at (row, getPredictionSet()[predictionColIdx])
   */
  public double getDoublePrediction(int row, int predictionColIdx)
    {
    return 0;
    }

  /**
   * Get a long prediciton in the specified prediction column.  The index into
   * the prediction set is used, not the actual column index.
   * @param row the row of the table
   * @param predictionColIdx the index into the prediction set
   * @return the prediction at (row, getPredictionSet()[predictionColIdx])
   */
  public long getLongPrediction(int row, int predictionColIdx)
    {
    return 0;
    }

  /**
   * Get a short prediciton in the specified prediction column.  The index into
   * the prediction set is used, not the actual column index.
   * @param row the row of the table
   * @param predictionColIdx the index into the prediction set
   * @return the prediction at (row, getPredictionSet()[predictionColIdx])
   */
  public short getShortPrediction(int row, int predictionColIdx)
    {
    return 0;
    }

  /**
   * Get a boolean prediciton in the specified prediction column.  The index into
   * the prediction set is used, not the actual column index.
   * @param row the row of the table
   * @param predictionColIdx the index into the prediction set
   * @return the prediction at (row, getPredictionSet()[predictionColIdx])
   */
  public boolean getBooleanPrediction(int row, int predictionColIdx)
    {
    return false;
    }

  /**
   * Get a String prediciton in the specified prediction column.  The index into
   * the prediction set is used, not the actual column index.
   * @param row the row of the table
   * @param predictionColIdx the index into the prediction set
   * @return the prediction at (row, getPredictionSet()[predictionColIdx])
   */
  public String getStringPrediction(int row, int predictionColIdx)
    {
    return null;
    }

  /**
   * Get a char[] prediciton in the specified prediction column.  The index into
   * the prediction set is used, not the actual column index.
   * @param row the row of the table
   * @param predictionColIdx the index into the prediction set
   * @return the prediction at (row, getPredictionSet()[predictionColIdx])
   */
  public char[] getCharsPrediction(int row, int predictionColIdx)
    {
    return null;
    }

  /**
   * Get a byte[] prediciton in the specified prediction column.  The index into
   * the prediction set is used, not the actual column index.
   * @param row the row of the table
   * @param predictionColIdx the index into the prediction set
   * @return the prediction at (row, getPredictionSet()[predictionColIdx])
   */
  public byte[] getBytesPrediction(int row, int predictionColIdx)
    {
    return null;
    }

  /**
   * Get an Object prediciton in the specified prediction column.  The index into
   * the prediction set is used, not the actual column index.
   * @param row the row of the table
   * @param predictionColIdx the index into the prediction set
   * @return the prediction at (row, getPredictionSet()[predictionColIdx])
   */
  public Object getObjectPrediction(int row, int predictionColIdx)
    {
    return null;
    }

  /**
   * Get a byte prediciton in the specified prediction column.  The index into
   * the prediction set is used, not the actual column index.
   * @param row the row of the table
   * @param predictionColIdx the index into the prediction set
   * @return the prediction at (row, getPredictionSet()[predictionColIdx])
   */
  public byte getBytePrediction(int row, int predictionColIdx)
    {
    return 0;
    }

  /**
   * Get a char prediciton in the specified prediction column.  The index into
   * the prediction set is used, not the actual column index.
   * @param row the row of the table
   * @param predictionColIdx the index into the prediction set
   * @return the prediction at (row, getPredictionSet()[predictionColIdx])
   */
  public char getCharPrediction(int row, int predictionColIdx)
    {
    return 0;
    }

  /**
   * Add a column of integer predictions to this PredictionTable.
   * @param predictions the predictions
   * @param label the label for the new column
   * @return the index of the prediction column in the prediction set
   */
  public int addPredictionColumn(int[] predictions, String label)
    {
    return 0;
    }

  /**
   * Add a column of float predictions to this PredictionTable.
   * @param predictions the predictions
   * @param label the label for the new column
   * @return the index of the prediction column in the prediction set
   */
  public int addPredictionColumn(float[] predictions, String label)
    {
    return 0;
    }

  /**
   * Add a column of double predictions to this PredictionTable.
   * @param predictions the predictions
   * @param label the label for the new column
   * @return the index of the prediction column in the prediction set
   */
  public int addPredictionColumn(double[] predictions, String label)
    {
    return 0;
    }

  /**
   * Add a column of long predictions to this PredictionTable.
   * @param predictions the predictions
   * @param label the label for the new column
   * @return the index of the prediction column in the prediction set
   */
  public int addPredictionColumn(long[] predictions, String label)
    {
    return 0;
    }

  /**
   * Add a column of short predictions to this PredictionTable.
   * @param predictions the predictions
   * @param label the label for the new column
   * @return the index of the prediction column in the prediction set
   */
  public int addPredictionColumn(short[] predictions, String label)
    {
    return 0;
    }

  /**
   * Add a column of boolean predictions to this PredictionTable.
   * @param predictions the predictions
   * @param label the label for the new column
   * @return the index of the prediction column in the prediction set
   */
  public int addPredictionColumn(boolean[] predictions, String label)
    {
    return 0;
    }

  /**
   * Add a column of String predictions to this PredictionTable.
   * @param predictions the predictions
   * @param label the label for the new column
   * @return the index of the prediction column in the prediction set
   */
  public int addPredictionColumn(String[] predictions, String label)
    {
    return 0;
    }

  /**
   * Add a column of char[] predictions to this PredictionTable.
   * @param predictions the predictions
   * @param label the label for the new column
   * @return the index of the prediction column in the prediction set
   */
  public int addPredictionColumn(char[][] predictions, String label)
    {
    return 0;
    }

  /**
   * Add a column of byte[] predictions to this PredictionTable.
   * @param predictions the predictions
   * @param label the label for the new column
   * @return the index of the prediction column in the prediction set
   */
  public int addPredictionColumn(byte[][] predictions, String label)
    {
    return 0;
    }

  /**
   * Add a column of Object predictions to this PredictionTable.
   * @param predictions the predictions
   * @param label the label for the new column
   * @return the index of the prediction column in the prediction set
   */
  public int addPredictionColumn(Object[] predictions, String label)
    {
    return 0;
    }

  /**
   * Add a column of byte predictions to this PredictionTable.
   * @param predictions the predictions
   * @param label the label for the new column
   * @return the index of the prediction column in the prediction set
   */
  public int addPredictionColumn(byte[] predictions, String label)
    {
    return 0;
    }

  /**
   * Add a column of char predictions to this PredictionTable.
   * @param predictions the predictions
   * @param label the label for the new column
   * @return the index of the prediction column in the prediction set
   */
  public int addPredictionColumn(char[] predictions, String label)
    {
    return 0;
    }

  //
  // The following methods for Missing and Empty values were added
  // by Ruth to get things to compile.  They do not correctly
  // support the Missing/Empty value functionality, and should be
  // revisited at a later date and fully implemented.
  //

  /**
   * Return true if the value at (row, col) is a missing value, false otherwise.
   * @param row the row index
   * @param col the column index
   * @return true if the value is missing, false otherwise
   */
  public boolean isValueMissing(int row, int col)
    {
    return false;
    }

  /**
   * Return true if the value at (row, col) is an empty value, false otherwise.
   * @param row the row index
   * @param col the column index
   * @return true if the value is empty, false otherwise
   */
  public boolean isValueEmpty(int row, int col)
   {
   return false;
   }

  /**
   * Return the value used to signify a scalar missing value in col
   * @param col the column index
   * @return the value used to signify a scalar missing value in col
   */
  public Number getScalarMissingValue(int col)
   {
   return null;
   }

  /**
   * Return the value used to signify a nominal missing value in col
   * @param col the column index
   * @return the value used to signify a nominal missing value in col
   */
  public String getNominalMissingValue(int col)
   {
   return null;
   }

  /**
   * Return the value used to signify a scalar empty value in col
   * @param col the column index
   * @return the value used to signify a scalar empty value in col
   */
  public Number getScalarEmptyValue(int col)
   {
   return null;
   }

  /**
   * Return the value used to signify a nominal empty value in col
   * @param col the column index
   * @return the value used to signify a nominal empty value in col
   */
  public String getNominalEmptyValue(int col)
   {
   return null;
   }



  }
