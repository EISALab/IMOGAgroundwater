package ncsa.d2k.modules.core.datatype.table.db;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import java.util.*;
import ncsa.d2k.modules.core.io.sql.*;
/**
 * <p>Title: DBExampleTable </p>
 * <p>Description: Example Table Implementation for a Database </p>
 * <p>Copyright: NCSA (c) 2002</p>
 * <p>Company: </p>
 * @author Sameer Mathur, David Clutter
 * @version 1.0
 *
 * @todo: reimplement getSubset.
 */

class DBExampleTable extends DBSubsetTable implements ExampleTable{

    /** the indicies of the records in the various training sets. */
    protected int trainSet[];

    /** the indicies of the records in the various test sets. */
    protected int testSet[];

    /**the indicies of the attributes that are inputs (to the model). */
    protected int inputColumns[];

    /**the indicies of the attributes that are inputs (to the model). */
    protected int outputColumns[];

    private String inputNames[];
    private String outputNames[];

    DBExampleTable(){}

    DBExampleTable(DBTable orig, DBDataSource _dbdatasource, DBConnection _dbconnection) {
        super(orig);

        if (orig instanceof ExampleTable) {


            if (((ExampleTable)orig).getTestingSet() != null) {
                testSet = new int[((ExampleTable)orig).getTestingSet().length];
                for (int i=0; i<((ExampleTable)orig).getTestingSet().length; i++) {
                    testSet[i] = ((ExampleTable)orig).getTestingSet()[i];
                }
            }

            if (((ExampleTable)orig).getTrainingSet() != null) {
                trainSet = new int[((ExampleTable)orig).getTrainingSet().length];
                for (int i=0; i<((ExampleTable)orig).getTrainingSet().length; i++) {
                    trainSet[i] = ((ExampleTable)orig).getTrainingSet()[i];
                }
            }

            if (((ExampleTable)orig).getInputFeatures() != null) {
                this.inputColumns = new int[((ExampleTable)orig).getInputFeatures().length];
                for (int i=0; i<((ExampleTable)orig).getInputFeatures().length; i++) {
                    inputColumns[i] = ((ExampleTable)orig).getInputFeatures()[i];

                }

            }

            if (((ExampleTable)orig).getOutputFeatures() != null) {
                this.outputColumns = new int[((ExampleTable)orig).getOutputFeatures().length];
                for (int i=0; i<((ExampleTable)orig).getOutputFeatures().length; i++) {
                    outputColumns[i] = ((ExampleTable)orig).getOutputFeatures()[i];
                }
            }
        }
        isNominal = new boolean[orig.isNominal.length];
        for(int i = 0; i < isNominal.length; i++)
            isNominal[i] = orig.isNominal[i];
    }

    public DBExampleTable(DBExampleTable _table, int[] _subset){

      this(_table, _table.dataSource, _table.dbConnection);
      subset = _subset;

    }

/*    public ExampleTable toExampleTable() {
        return this;
    }
    */

    //////////////  Input, output, test and train. ///////////////
    /**	Returns an array of ints, the indices of the input columns.
    	@return an array of ints, the indices of the input columns.
     */
    public int[] getInputFeatures () {
        return  inputColumns;
    }

    /**
    	Returns the number of input features.
    	@returns the number of input features.
     */
    public int getNumInputFeatures () {
        return  inputColumns.length;
    }

    /**
    	Returns the number of example rows.
    	@returns the number of example rows.
     */
    public int getNumExamples () {
        return  this.getNumRows();
    }

    /**
    	Return the number of examples in the training set.
    	@returns the number of examples in the training set.
     */
    public int getNumTrainExamples () {
        return  this.trainSet.length;
    }

    /**
    	Return the number of examples in the testing set.
    	@returns the number of examples in the testing set.
     */
    public int getNumTestExamples () {
        return  this.testSet.length;
    }

    /**
    	Get the number of output features.
    	@returns the number of output features.
     */
    public int[] getOutputFeatures () {
        return  outputColumns;
    }

    /**
    	Get the number of output features.
    	@returns the number of output features.
     */
    public int getNumOutputFeatures () {
        return  outputColumns.length;
    }

    /**
    	Set the input features.
    	@param inputs the indexes of the columns to be used as input features.
     */
    public void setInputFeatures (int[] inputs) {
        inputColumns = inputs;
        inputNames = new String[inputs.length];
        for(int i = 0; i < inputNames.length; i++)
          inputNames[i] = getColumnLabel(inputs[i]);
    }

    /**
    	Set the output features.
    	@param outs the indexes of the columns to be used as output features.
     */
    public void setOutputFeatures (int[] outs) {
        outputColumns = outs;
        outputNames = new String[outs.length];
        for(int i = 0; i < outs.length; i++)
          outputNames[i] = getColumnLabel(outs[i]);
    }

    /**
    	Set the indexes of the rows in the training set.
    	@param trainingSet the indexes of the items to be used to train the model.
     */
    public void setTrainingSet (int[] trainingSet) {
        trainSet = trainingSet;
        Arrays.sort(trainSet);
    }

    /**
    	Get the training set.
    	@return the indices of the rows of the training set.
     */
    public int[] getTrainingSet () {
        return  trainSet;
    }

    /**
    	Set the indexes of the rows in the testing set.
    	@param testingSet the indexes of the items to be used to test the model.
     */
    public void setTestingSet (int[] testingSet) {
        testSet = testingSet;
        Arrays.sort(testSet);
    }

    /**
    	Get the testing set.
    	@return the indices of the rows of the testing set.
     */
    public int[] getTestingSet () {
        return  testSet;
    }


    //////////////// Access the test data ///////////////////
    /**
    	This class provides transparent access to the test data only. The testSets
    	field of the TrainTest table is used to reference only the test data, yet
    	the getter methods look exactly the same as they do for any other table.
		@return a reference to a table referencing only the testing data
     */
    public Table getTestTable () {
        if (testSet == null)
            return  null;

        //VERED 8-25-03 changed this return line to return an example table.
        return new DBExampleTable(this, testSet);

      //  return new LocalDBTestTable(this, dataSource.copy());
    }

    /**
    	Return a reference to a Table referencing only the training data.
    	@return a reference to a Table referencing only the training data.
     */
    public Table getTrainTable () {
        if (trainSet == null)
            return  null;
            //VERED 8-25-03
            //changed the return line to return an example table.
        return new DBExampleTable (this, trainSet);
    }

    public PredictionTable toPredictionTable() {
            return new LocalDBPredictionTable(this, dataSource.copy(), this.dbConnection);
    }

    //VERED 8-25-03
    //changed get and set method, to use the subset indices and removed
    //the call to the super class.


    public double getInputDouble(int e, int i) {
         //return getDouble(e, inputColumns[i]);
         return (double)dataSource.getNumericData(subset[e], inputColumns[i]);
     }

     public double getOutputDouble(int e, int o) {
         //return getDouble(e, outputColumns[o]);
          return (double)dataSource.getNumericData(subset[e], outputColumns[o]);
     }


     public String getInputString(int e, int i) {
         //return getString(e, inputColumns[i]);
         return dataSource.getTextData(subset[e], inputColumns[i]);
     }

     public String getOutputString(int e, int o) {
         //return getString(e, outputColumns[o]);
         return dataSource.getTextData(subset[e], outputColumns[o]);
     }


     public int getInputInt(int e, int i) {
         return (int)dataSource.getNumericData(subset[e], inputColumns[i]);
     }
     public int getOutputInt(int e, int o) {
         return (int)dataSource.getNumericData(subset[e], outputColumns[o]);
     }


     public float getInputFloat(int e, int i) {
         //return getFloat(e, inputColumns[i]);
         return (float)dataSource.getNumericData(subset[e], inputColumns[i]);
     }
     public float getOutputFloat(int e, int o) {
         //return getFloat(e, outputColumns[o]);
         return (float)dataSource.getNumericData(subset[e], outputColumns[o]);
     }


     public short getInputShort(int e, int i) {
  //       return getShort(e, inputColumns[i]);
      return (short)dataSource.getNumericData(subset[e], inputColumns[i]);
     }
     public short getOutputShort(int e, int o) {
//         return getShort(e, outputColumns[o]);
      return (short)dataSource.getNumericData(subset[e], outputColumns[o]);
     }


     public long getInputLong(int e, int i) {
     //    return getLong(e, inputColumns[i]);
      return (long)dataSource.getNumericData(subset[e], inputColumns[i]);
     }
     public long getOutputLong(int e, int o) {
       //  return getLong(e, outputColumns[o]);
        return (long)dataSource.getNumericData(subset[e], outputColumns[o]);
     }


     public byte getInputByte(int e, int i) {
     //    return getByte(e, inputColumns[i]);
         return dataSource.getTextData(subset[e], inputColumns[i]).getBytes()[0];
     }
     public byte getOutputByte(int e, int o) {
       //  return getByte(e, outputColumns[o]);
        return dataSource.getTextData(subset[e], outputColumns[o]).getBytes()[0];
     }

     public Object getInputObject(int e, int i) {
         //return getObject(e, inputColumns[i]);
         return (Object)dataSource.getObjectData(subset[e], inputColumns[i]).toString();
     }
     public Object getOutputObject(int e, int o) {
         //return getObject(e, outputColumns[o]);
         return (Object)dataSource.getObjectData(subset[e], outputColumns[o]).toString();
     }


     public char getInputChar(int e, int i) {
         //return getChar(e, inputColumns[i]);
         return dataSource.getTextData(subset[e], inputColumns[i]).toCharArray()[0];
     }
     public char getOutputChar(int e, int o) {
         //return getChar(e, outputColumns[o]);
         return dataSource.getTextData(subset[e], outputColumns[o]).toCharArray()[0];
     }


     public char[] getInputChars(int e, int i) {
         //return getChars(e, inputColumns[i]);
         return dataSource.getTextData(subset[e], inputColumns[i]).toCharArray();
     }
     public char[] getOutputChars(int e, int o) {
         //return getChars(e, inputColumns[o]);
           return dataSource.getTextData(subset[e], outputColumns[o]).toCharArray();
     }


     public byte[] getInputBytes(int e, int i) {
         //return getBytes(e, inputColumns[i]);
         return dataSource.getTextData(subset[e], inputColumns[i]).getBytes();
     }
     public byte[] getOutputBytes(int e, int o) {
         //return getBytes(e, outputColumns[o]);
         return dataSource.getTextData(subset[e], outputColumns[o]).getBytes();
     }


     public boolean getInputBoolean(int e, int i) {
        //return getBoolean(e, inputColumns[i]);
        return new Boolean(dataSource.getTextData(subset[e], inputColumns[i])).booleanValue();
     }
     public boolean getOutputBoolean(int e, int o) {
         //return getBoolean(e, outputColumns[o]);
         return new Boolean(dataSource.getTextData(subset[e], outputColumns[o])).booleanValue();
     }


     public int getNumInputs(int e) {
         return inputColumns.length;
     }
     public int getNumOutputs(int e) {
         return outputColumns.length;
     }





     /**
      * return an example object of this table. the subset is initialized to
      * the subset of this table, and the index initialized to <code>i</code>.
      *
      * @param i: initial index for the returned Example object.
      */
     public Example getExample(int i) {
       DBExample ex = new DBExample (dataSource, dbConnection, this, subset);
       ex.setIndex(i);
       return ex;
     // return null;

       }

       public Row getRow(){
        return new DBExample(this.dataSource, this.dbConnection, this, this.subset);
       }

     public String getInputName(int i) {
         return getColumnLabel(inputColumns[i]);
     }
     public String getOutputName(int o) {
         return getColumnLabel(outputColumns[o]);
     }
     public int getInputType(int i) {
         return getColumnType(inputColumns[i]);
     }
     public int getOutputType(int o) {
         return getColumnType(outputColumns[o]);
     }
     
	/**
	 * Return true if the any of the input or output columns contains missing values.
	 * @return true if the any of the input or output columns contains missing values.
	 */
	public boolean hasMissingInputsOutputs() {
		for (int i = 0 ; i < inputColumns.length ; i++) {
			if (this.hasMissingValues(inputColumns[i]))
				return true;
		}
		for (int i = 0 ; i < outputColumns.length ; i++) {
			if (this.hasMissingValues(outputColumns[i]))
				return true;
		}
		return false;
	}
	
     public boolean isInputNominal(int i) {
         return isColumnNominal(inputColumns[i]);
     }
     public boolean isOutputNominal(int o) {
         return isColumnNominal(outputColumns[o]);
     }
     public boolean isInputScalar(int i) {
         return isColumnScalar(inputColumns[i]);
     }
     public boolean isOutputScalar(int o) {
         return isColumnScalar(outputColumns[o]);
    }
    public String[] getInputNames() {
      return inputNames;
    }
    public String[] getOutputNames() {
      return outputNames;
    }




      public Table copy() {

        return new DBExampleTable(this, dataSource.copy(), dbConnection);

      }




      public Table shallowCopy() {
        DBExampleTable retVal = new DBExampleTable();
        copyAttributes(retVal);
        return retVal;
      }

      protected void copyAttributes(DBExampleTable target){
        super.copyAttributes(target);
        target.inputColumns = this.inputColumns;
        target.inputNames = this.inputNames;
        target.outputColumns = this.outputColumns;
        target.outputNames = this.outputNames;
        target.testSet = this.testSet;
        target.trainSet = this.trainSet;
      }



    // Mutable Table support -
    //VERED 8-25-03 now supported in DBSubsetTable.
    /**
     * Add a row to the end of this Table, initialized with integer data.
     * @param newEntry the data to put into the new row.
     */


/*    public void addRow(int[] newEntry) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }*/

    /**
     * Add a row to the end of this Table, initialized with float data.
     * @param newEntry the data to put into the new row.
     */
  /*  public void addRow(float[] newEntry) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }*/

    /**
     * Add a row to the end of this Table, initialized with double data.
     * @param newEntry the data to put into the new row.
     */
/*    public void addRow(double[] newEntry) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }*/

    /**
     * Add a row to the end of this Table, initialized with long data.
     * @param newEntry the data to put into the new row.
     */
/*    public void addRow(long[] newEntry) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }*/

    /**
     * Add a row to the end of this Table, initialized with short data.
     * @param newEntry the data to put into the new row.
     */
/*    public void addRow(short[] newEntry) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }*/

    /**
     * Add a row to the end of this Table, initialized with boolean data.
     * @param newEntry the data to put into the new row.
     */
/*    public void addRow(boolean[] newEntry) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }*/

    /**
     * Add a row to the end of this Table, initialized with String data.
     * @param newEntry the data to put into the new row.
     */
/*    public void addRow(String[] newEntry) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }*/

    /**
     * Add a row to the end of this Table, initialized with char[] data.
     * @param newEntry the data to put into the new row.
     */
/*    public void addRow(char[][] newEntry) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }*/

    /**
     * Add a row to the end of this Table, initialized with byte[] data.
     * @param newEntry the data to put into the new row.
     */
/*    public void addRow(byte[][] newEntry) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }*/

    /**
     * Add a row to the end of this Table, initialized with Object[] data.
     * @param newEntry the data to put into the new row.
     */
/*    public void addRow(Object[] newEntry) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }*/

    /**
     * Add a row to the end of this Table, initialized with byte data.
     * @param newEntry the data to put into the new row.
     */
/*    public void addRow(byte[] newEntry) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }*/

    /**
     * Add a row to the end of this Table, initialized with char data.
     * @param newEntry the data to put into the new row.
     */
/*    public void addRow(char[] newEntry) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }*/

    /**
     * Insert a new row into this Table, initialized with integer data.
     * @param newEntry the data to put into the inserted row.
     * @param position the position to insert the new row
     */
/*    public void insertRow(int[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }*/

    /**
     * Insert a new row into this Table, initialized with float data.
     * @param newEntry the data to put into the inserted row.
     * @param position the position to insert the new row
     */
/*    public void insertRow(float[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }*/

    /**
     * Insert a new row into this Table, initialized with double data.
     * @param newEntry the data to put into the inserted row.
     * @param position the position to insert the new row
     */
/*    public void insertRow(double[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
    /**
     * Insert a new row into this Table, initialized with long data.
     * @param newEntry the data to put into the inserted row.
     * @param position the position to insert the new row
     */
  /*  public void insertRow(long[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }*/

    /**
     * Insert a new row into this Table, initialized with short data.
     * @param newEntry the data to put into the inserted row.
     * @param position the position to insert the new row
     */
/*    public void insertRow(short[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }*/

    /**
     * Insert a new row into this Table, initialized with boolean data.
     * @param newEntry the data to put into the inserted row.
     * @param position the position to insert the new row
     */
/*    public void insertRow(boolean[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }*/

    /**
     * Insert a new row into this Table, initialized with String data.
     * @param newEntry the data to put into the inserted row.
     * @param position the position to insert the new row
     */
/*    public void insertRow(String[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
    /**
     * Insert a new row into this Table, initialized with char[] data.
     * @param newEntry the data to put into the inserted row.
     * @param position the position to insert the new row
     */
  /*  public void insertRow(char[][] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
    /**
     * Insert a new row into this Table, initialized with byte[] data.
     * @param newEntry the data to put into the inserted row.
     * @param position the position to insert the new row
     */
  /*  public void insertRow(byte[][] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
    /**
     * Insert a new row into this Table, initialized with Object data.
     * @param newEntry the data to put into the inserted row.
     * @param position the position to insert the new row
     */
  /*  public void insertRow(Object[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
    /**
     * Insert a new row into this Table, initialized with byte data.
     * @param newEntry the data to put into the inserted row.
     * @param position the position to insert the new row
     */
  /*  public void insertRow(byte[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
    /**
     * Insert a new row into this Table, initialized with char data.
     * @param newEntry the data to put into the inserted row.
     * @param position the position to insert the new row
     */
  /*  public void insertRow(char[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
    /**
     * Add a new column to the end of this table, initialized with integer data.
     * @param newEntry the data to initialize the column with.
     */
  /*  public void addColumn(int[] newEntry) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
    /**
     * Add a new column to the end of this table, initialized with float data.
     * @param newEntry the data to initialize the column with.
     */
  /*  public void addColumn(float[] newEntry) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
    /**
     * Add a new column to the end of this table, initialized with double data.
     * @param newEntry the data to initialize the column with.
     */
  /*  public void addColumn(double[] newEntry) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
    /**
     * Add a new column to the end of this table, initialized with long data.
     * @param newEntry the data to initialize the column with.
     */
  /*  public void addColumn(long[] newEntry) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
    /**
     * Add a new column to the end of this table, initialized with short data.
     * @param newEntry the data to initialize the column with.
     */
  /*  public void addColumn(short[] newEntry) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
    /**
     * Add a new column to the end of this table, initialized with boolean data.
     * @param newEntry the data to initialize the column with.
     */
  /*  public void addColumn(boolean[] newEntry) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
    /**
     * Add a new column to the end of this table, initialized with String data.
     * @param newEntry the data to initialize the column with.
     */
  /*  public void addColumn(String[] newEntry) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
    /**
     * Add a new column to the end of this table, initialized with char[] data.
     * @param newEntry the data to initialize the column with.
     */
  /*  public void addColumn(char[][] newEntry) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
    /**
     * Add a new column to the end of this table, initialized with byte[] data.
     * @param newEntry the data to initialize the column with.
     */
  /*  public void addColumn(byte[][] newEntry) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
    /**
     * Add a new column to the end of this table, initialized with Object data.
     * @param newEntry the data to initialize the column with.
     */
  /*  public void addColumn(Object[] newEntry) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
    /**
     * Add a new column to the end of this table, initialized with byte data.
     * @param newEntry the data to initialize the column with.
     */
  /*  public void addColumn(byte[] newEntry) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
    /**
     * Add a new column to the end of this table, initialized with char data.
     * @param newEntry the data to initialize the column with.
     */
  /*  public void addColumn(char[] newEntry) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
    /**
     * Insert a new column into this Table, initialized with integer data.
     * @param newEntry the initial values of the new column.
     * @param position the position to insert the new row
     */
  /*  public void insertColumn(int[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
    /**
     * Insert a new column into this Table, initialized with float data.
     * @param newEntry the initial values of the new column.
     * @param position the position to insert the new row
     */
  /*  public void insertColumn(float[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
    /**
     * Insert a new column into this Table, initialized with double data.
     * @param newEntry the initial values of the new column.
     * @param position the position to insert the new row
     */
  /*  public void insertColumn(double[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
    /**
     * Insert a new column into this Table, initialized with long data.
     * @param newEntry the initial values of the new column.
     * @param position the position to insert the new row
     */
  /*  public void insertColumn(long[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
    /**
     * Insert a new column into this Table, initialized with short data.
     * @param newEntry the initial values of the new column.
     * @param position the position to insert the new row
     */
  /*  public void insertColumn(short[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
    /**
     * Insert a new column into this Table, initialized with boolean data.
     * @param newEntry the initial values of the new column.
     * @param position the position to insert the new row
     */
  /*  public void insertColumn(boolean[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
    /**
     * Insert a new column into this Table, initialized with String data.
     * @param newEntry the initial values of the new column.
     * @param position the position to insert the new row
     */
  /*  public void insertColumn(String[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
    /**
     * Insert a new column into this Table, initialized with char[] data.
     * @param newEntry the initial values of the new column.
     * @param position the position to insert the new row
     */
  /*  public void insertColumn(char[][] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
    /**
     * Insert a new column into this Table, initialized with byte[] data.
     * @param newEntry the initial values of the new column.
     * @param position the position to insert the new row
     */
  /*  public void insertColumn(byte[][] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
    /**
     * Insert a new column into this Table, initialized with Object data.
     * @param newEntry the initial values of the new column.
     * @param position the position to insert the new row
     */
  /*  public void insertColumn(Object[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
    /**
     * Insert a new column into this Table, initialized with byte data.
     * @param newEntry the initial values of the new column.
     * @param position the position to insert the new row
     */
  /*  public void insertColumn(byte[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
    /**
     * Insert a new column into this Table, initialized with char data.
     * @param newEntry the initial values of the new column.
     * @param position the position to insert the new row
     */
/*    public void insertColumn(char[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
    /**
     * Set the row at the given position to an array of int data.
     *	@param newEntry the new values of the row.
     *	@param position the position to set
     */
  /*  public void setRow(int[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
    /**
     * Set the row at the given position to an array of float data.
     *	@param newEntry the new values of the row.
     *	@param position the position to set
     */
  /*  public void setRow(float[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
    /**
     * Set the row at the given position to an array of double data.
     *	@param newEntry the new values of the row.
     *	@param position the position to set
     */
 /*   public void setRow(double[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
    /**
     * Set the row at the given position to an array of long data.
     *	@param newEntry the new values of the row.
     *	@param position the position to set
     */
  /*  public void setRow(long[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
    /**
     * Set the row at the given position to an array of short data.
     *	@param newEntry the new values of the row.
     *	@param position the position to set
     */
  /*  public void setRow(short[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
    /**
     * Set the row at the given position to an array of boolean data.
     *	@param newEntry the new values of the row.
     *	@param position the position to set
     */
  /*  public void setRow(boolean[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
    /**
     * Set the row at the given position to an array of String data.
     *	@param newEntry the new values of the row.
     *	@param position the position to set
     */
  /*  public void setRow(String[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
    /**
     * Set the row at the given position to an array of char[] data.
     *	@param newEntry the new values of the row.
     *	@param position the position to set
     */
  /*  public void setRow(char[][] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
    /**
     * Set the row at the given position to an array of byte[] data.
     *	@param newEntry the new values of the row.
     *	@param position the position to set
     */
  /*  public void setRow(byte[][] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
    /**
     * Set the row at the given position to an array of Object data.
     *	@param newEntry the new values of the row.
     *	@param position the position to set
     */
  /*  public void setRow(Object[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
    /**
     * Set the row at the given position to an array of byte data.
     *	@param newEntry the new values of the row.
     *	@param position the position to set
     */
  /*  public void setRow(byte[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
    /**
     * Set the row at the given position to an array of char data.
     *	@param newEntry the new values of the row.
     *	@param position the position to set
     */
  /*   public void setRow(char[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
    /**
     * Set the column at the given position to an array of int data.
     *	@param newEntry the new values of the column.
     *	@param position the position to set
     */
  /*  public void setColumn(int[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
    /**
     * Set the column at the given position to an array of float data.
     *	@param newEntry the new values of the column.
     *	@param position the position to set
     */
  /*  public void setColumn(float[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
    /**
     * Set the column at the given position to an array of double data.
     *	@param newEntry the new values of the column.
     *	@param position the position to set
     */
  /*  public void setColumn(double[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
    /**
     * Set the column at the given position to an array of long data.
     *	@param newEntry the new values of the column.
     *	@param position the position to set
     */
  /*  public void setColumn(long[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
    /**
     * Set the column at the given position to an array of short data.
     *	@param newEntry the new values of the column.
     *	@param position the position to set
     */
  /*  public void setColumn(short[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
    /**
     * Set the column at the given position to an array of boolean data.
     *	@param newEntry the new values of the column.
     *	@param position the position to set
     */
  /*  public void setColumn(boolean[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
    /**
     * Set the column at the given position to an array of String data.
     *	@param newEntry the new values of the column.
     *	@param position the position to set
     */
  /*  public void setColumn(String[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
    /**
     * Set the column at the given position to an array of char data.
     *	@param newEntry the new values of the column.
     *	@param position the position to set
     */
  /*  public void setColumn(char[][] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
    /**
     * Set the column at the given position to an array of byte[] data.
     *	@param newEntry the new values of the column.
     *	@param position the position to set
     */
  /*  public void setColumn(byte[][] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
    /**
     * Set the column at the given position to an array of Object data.
     *	@param newEntry the new values of the column.
     *	@param position the position to set
     */
  /*  public void setColumn(Object[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
    /**
     * Set the column at the given position to an array of byte data.
     *	@param newEntry the new values of the column.
     *	@param position the position to set
     */
  /*  public void setColumn(byte[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
    /**
     * Set the column at the given position to an array of char data.
     *	@param newEntry the new values of the column.
     *	@param position the position to set
     */
  /*  public void setColumn(char[] newEntry, int position) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
    /**
            Remove a column from the table.
            @param position the position of the Column to remove
    */
  /*  public void removeColumn(int position) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }*/

    /**
            Remove a range of columns from the table.
            @param start the start position of the range to remove
            @param len the number to remove-the length of the range
    */
    /*public void removeColumns(int start, int len) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }


    /**
     * Remove rows from this Table using a boolean map.
     * @param flags an array of booleans to map to this Table's rows.  Those
     * with a true will be removed, all others will not be removed
     */
/*    public void removeRowsByFlag(boolean[] flags) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
    */

    /**
     * Remove rows from this Table using a boolean map.
     * @param flags an array of booleans to map to this Table's rows.  Those
     * with a true will be removed, all others will not be removed
     */
/*    public void removeColumnsByFlag(boolean[] flags) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }*/

    /**
     * Remove rows from this Table by index.
     * @param indices a list of the rows to remove
     */
/*    public void removeRowsByIndex(int[] indices) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
    /**
     * Remove rows from this Table by index.
     * @param indices a list of the rows to remove
     */
  /*  public void removeColumnsByIndex(int[] indices) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
    /**
            Get a copy of this Table reordered based on the input array of indexes.
            Does not overwrite this Table.
            @param newOrder an array of indices indicating a new order
            @return a copy of this column with the rows reordered
    */
  /*  public Table reorderRows(int[] newOrder) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
    /**
            Get a copy of this Table reordered based on the input array of indexes.
            Does not overwrite this Table.
            @param newOrder an array of indices indicating a new order
            @return a copy of this column with the rows reordered
    */
  /*  public Table reorderColumns(int[] newOrder) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
    /**
            Swap the positions of two rows.
            @param pos1 the first row to swap
            @param pos2 the second row to swap
    */
  /*  public void swapRows(int pos1, int pos2) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
    /**
            Swap the positions of two columns.
            @param pos1 the first column to swap
            @param pos2 the second column to swap
    */
  /*  public void swapColumns(int pos1, int pos2) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
    /**
            Set a specified element in the table.  If an element exists at this
            position already, it will be replaced.  If the position is beyond the capacity
            of this table then an ArrayIndexOutOfBounds will be thrown.
            @param element the new element to be set in the table
            @param row the row to be changed in the table
            @param column the Column to be set in the given row
    */
  /*  public void setObject(Object element, int row, int column) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
/**
     * Set an int value in the table.
     * @param data the value to set
 * @param row the row of the table
 * @param column the column of the table
 */
  /*  public void setInt(int data, int row, int column) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
/**
     * Set a short value in the table.
     * @param data the value to set
 * @param row the row of the table
 * @param column the column of the table
 */
  /*  public void setShort(short data, int row, int column) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
/**
     * Set a float value in the table.
     * @param data the value to set
 * @param row the row of the table
 * @param column the column of the table
 */
  /*  public void setFloat(float data, int row, int column) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
/**
     * Set an double value in the table.
     * @param data the value to set
 * @param row the row of the table
 * @param column the column of the table
 */
 /*   public void setDouble(double data, int row, int column) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
/**
     * Set a long value in the table.
     * @param data the value to set
 * @param row the row of the table
 * @param column the column of the table
 */
  /*  public void setLong(long data, int row, int column) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
/**
     * Set a String value in the table.
     * @param data the value to set
 * @param row the row of the table
 * @param column the column of the table
 */
  /*  public void setString(String data, int row, int column) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
/**
     * Set a byte[] value in the table.
     * @param data the value to set
 * @param row the row of the table
 * @param column the column of the table
 */
  /*  public void setBytes(byte[] data, int row, int column) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
/**
     * Set a boolean value in the table.
     * @param data the value to set
 * @param row the row of the table
 * @param column the column of the table
 */
  /*  public void setBoolean(boolean data, int row, int column) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
/**
     * Set a char[] value in the table.
     * @param data the value to set
 * @param row the row of the table
 * @param column the column of the table
 */
  /*  public void setChars(char[] data, int row, int column) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
/**
     * Set a byte value in the table.
     * @param data the value to set
 * @param row the row of the table
 * @param column the column of the table
 */
/*    public void setByte(byte data, int row, int column) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
/**
     * Set a char value in the table.
     * @param data the value to set
 * @param row the row of the table
 * @param column the column of the table
 */
  /*  public void setChar(char data, int row, int column) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
    /**
            Set the name associated with a column.
            @param label the new column label
            @param position the index of the column to set
    */
  /*  public void setColumnLabel(String label, int position) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
    /**
            Set the comment associated with a column.
            @param comment the new column comment
            @param position the index of the column to set
    */
  /*  public void setColumnComment(String comment, int position) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
/**
        Set the number of columns this Table can hold.
        @param numColumns the number of columns this Table can hold
 */
  /*  public void setNumColumns(int numColumns) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
    /**
            Sort the specified column and rearrange the rows of the table to
            correspond to the sorted column.
            @param col the column to sort by
    */
  /*  public void sortByColumn(int col) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
/**
   Sort the elements in this column starting with row 'begin' up to row 'end',
       @param col the index of the column to sort
   @param begin the row no. which marks the beginnig of the  column segment to be sorted
   @param end the row no. which marks the end of the column segment to be sorted
*/
/*public void sortByColumn(int col, int begin, int end) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
    /**
            Sets a new capacity for this Table.  The capacity is its potential
            maximum number of entries.  If numEntries is greater than newCapacity,
            then the Table may be truncated.
            @param newCapacity a new capacity
    */
  /*  public void setNumRows(int newCapacity) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
/////////// Collect the transformations that were performed. /////////
/**
 Add the transformation to the list.
 @param tm the Transformation that performed the reversable transform.
 */
/*public void addTransformation (Transformation tm) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
/**
 Returns the list of all reversable transformations there were performed
 on the original dataset.
 @returns an ArrayList containing the Transformation which transformed the data.
 */
/*public List getTransformations () {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
    /**
     * Set the value at (row, col) to be missing or not missing.
 * @param b true if the value should be set as missing, false otherwise
     * @param row the row index
     * @param col the column index
     */
  /*  public void setValueToMissing(boolean b, int row, int col) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
    /**
     * Set the value at (row, col) to be empty or not empty.
 * @param b true if the value should be set as empty, false otherwise
     * @param row the row index
     * @param col the column index
     */
  /*  public void setValueToEmpty(boolean b, int row, int col) {
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/

  /*  public void addRows(int num){
      throw new RuntimeException("Table mutation not supported in DBTable.");
    }
*/
} //DBExampleTable
