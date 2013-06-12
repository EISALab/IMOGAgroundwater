/**
 * <p>Title: LocalDBPredictionTable </p>
 * <p>Description: keeps the cache locally in a TableImpl </p>
 * <p>Copyright: NCSA (c) 2002</p>
 * <p>Company: </p>
 * @author Sameer Mathur, David Clutter
 * @version 1.0
 *
 *
 *
 */

package ncsa.d2k.modules.core.datatype.table.db;


import java.io.*;
import java.util.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.io.sql.*;

class LocalDBPredictionTable extends DBExampleTable implements PredictionTable {

    protected int[] predictionSet;
    protected SubsetTableImpl predictionColumnsTable;
    protected Column[] predictionColumns;


    protected int num_original_columns;

  protected int[] indirection;
    protected boolean[] prediction;

    protected ExampleTable original;

    protected boolean newTableHackVariable = true;

    //PagingPredictionTable() { }

     LocalDBPredictionTable(){}

    LocalDBPredictionTable(DBExampleTable pet, DBDataSource dataSource) {
        this(pet, dataSource, null);
    }

    LocalDBPredictionTable(LocalDBPredictionTable orig, int[] _subset){
      super(orig, _subset);
      indirection = new int[orig.indirection.length];
      for(int i=0; i<indirection.length; i++)
        indirection[i] = orig.indirection[i];

      newTableHackVariable = orig.newTableHackVariable;

      num_original_columns = orig.num_original_columns;

      original = orig.original;

      prediction = new boolean[orig.prediction.length];
      for(int i=0; i<prediction.length; i++)
        prediction[i] = orig.prediction[i];


      predictionColumns = orig.predictionColumns;

      predictionSet = new int[orig.predictionSet.length];
      for(int i=0; i<predictionSet.length; i++)
        predictionSet[i] = orig.predictionSet[i];


    }

    LocalDBPredictionTable(DBExampleTable pet, DBDataSource dataSource, DBConnection conn) {
        super(pet, dataSource, conn);

        original = pet;

        newTableHackVariable = false;



    //    subset = pet.subset;
    num_original_columns = original.getNumColumns();

            predictionSet = new int[outputColumns.length];
            for (int j = 0; j < predictionSet.length; j++)
                predictionSet[j] = j + original.getNumColumns();

            // handle indirection and prediction
           indirection = new int[original.getNumColumns() + predictionSet.length];
            prediction = new boolean[indirection.length];

            for (int j = 0; j < original.getNumColumns(); j++) {
       //         indirection[j] = j;
                prediction[j] = false;
            }
            for (int j = 0; j < predictionSet.length; j++) {
                indirection[original.getNumColumns() + j] = j;
                prediction[original.getNumColumns() + j] = true;
            }

           predictionColumns = new Column[outputColumns.length];




            //this will make sure that the prediction columns are as long
            //as the redular columns and subsetted.
            int numRows = dataSource.getNumRows();

            for(int i = 0; i < outputColumns.length; i++) {
                int type = original.getColumnType(outputColumns[i]);
                switch(type) {
                    case ColumnTypes.BOOLEAN:
                        predictionColumns[i] = new BooleanColumn(numRows);
                        predictionColumns[i].setLabel(original.getColumnLabel(outputColumns[i]) + " Predictions");
                        break;
                    case ColumnTypes.BYTE:
                        predictionColumns[i] = new ByteColumn(numRows);
                        predictionColumns[i].setLabel(original.getColumnLabel(outputColumns[i]) + " Predictions");
                        break;
                    case ColumnTypes.BYTE_ARRAY:
                        predictionColumns[i] = new ContinuousByteArrayColumn(numRows, true);
                        predictionColumns[i].setLabel(original.getColumnLabel(outputColumns[i]) + " Predictions");
                        break;
                    case ColumnTypes.CHAR:
                        predictionColumns[i] = new CharColumn(numRows);
                        predictionColumns[i].setLabel(original.getColumnLabel(outputColumns[i]) + " Predictions");
                        break;
                    case ColumnTypes.CHAR_ARRAY:
                        predictionColumns[i] = new ContinuousCharArrayColumn(numRows, true);
                        predictionColumns[i].setLabel(original.getColumnLabel(outputColumns[i]) + " Predictions");
                        break;
                    case ColumnTypes.DOUBLE:
                        predictionColumns[i] = new DoubleColumn(numRows);
                        predictionColumns[i].setLabel(original.getColumnLabel(outputColumns[i]) + " Predictions");
                        break;
                    case ColumnTypes.FLOAT:
                        predictionColumns[i] = new FloatColumn(numRows);
                        predictionColumns[i].setLabel(original.getColumnLabel(outputColumns[i]) + " Predictions");
                        break;
                    case ColumnTypes.INTEGER:
                        predictionColumns[i] = new IntColumn(numRows);
                        predictionColumns[i].setLabel(original.getColumnLabel(outputColumns[i]) + " Predictions");
                        break;
                    case ColumnTypes.LONG:
                        predictionColumns[i] = new LongColumn(numRows);
                        predictionColumns[i].setLabel(original.getColumnLabel(outputColumns[i]) + " Predictions");
                        break;
                    case ColumnTypes.OBJECT:
                        predictionColumns[i] = new ObjectColumn(numRows);
                        predictionColumns[i].setLabel(original.getColumnLabel(outputColumns[i]) + " Predictions");
                        break;
                    case ColumnTypes.SHORT:
                        predictionColumns[i] = new ShortColumn(numRows);
                        predictionColumns[i].setLabel(original.getColumnLabel(outputColumns[i]) + " Predictions");
                        break;
                    case ColumnTypes.STRING:
                        predictionColumns[i] = new StringColumn(numRows);
                        predictionColumns[i].setLabel(original.getColumnLabel(outputColumns[i]) + " Predictions");
                        break;
                    default:
                        break;
                }
            predictionColumnsTable = new SubsetTableImpl(predictionColumns, subset);
            //(MutableTableImpl)DefaultTableFactory.getInstance().createTable(c);
        }
    }


public Row getRow(){

    return new DBPredictionExample(dataSource, dbConnection, predictionColumns,
                                  this, subset, indirection, prediction);

    }





        public Table copy() {
             return new LocalDBPredictionTable(this, dataSource.copy(), dbConnection);

           }




           public Table shallowCopy() {
             LocalDBPredictionTable retVal = new LocalDBPredictionTable();
             copyAttributes(retVal);
             return retVal;
           }


           protected void copyAttributes(LocalDBPredictionTable target){
             super.copyAttributes(target);
             target.indirection = this.indirection;
             target.inputColumns = this.inputColumns;
             target.newTableHackVariable = this.newTableHackVariable;
             target.num_original_columns = this.num_original_columns;
             target.original = this.original;
             target.prediction = this.prediction;
             target.predictionColumns = this.predictionColumns;
             target.predictionColumnsTable = this.predictionColumnsTable;
             target.predictionSet = this.predictionSet;
           }



/*****************************************************************************/
/* utility methods                                                           */
/*****************************************************************************/



    private void insertIndirectionAndPrediction(boolean isPrediction, int position) {

        int[] newIndirection = new int[indirection.length + 1];
        boolean[] newPrediction = new boolean[newIndirection.length];

        for (int i = 0; i < position; i++) {
            newIndirection[i] = indirection[i];
            newPrediction[i] = prediction[i];
        }

        if (isPrediction) {
            if (newTableHackVariable)
                newIndirection[position] = 0;
            else
                newIndirection[position] = predictionColumnsTable.getNumColumns();
        }
        else
            newIndirection[position] = original.getNumColumns();

        newPrediction[position] = isPrediction;

        for (int i = position + 1; i < newIndirection.length; i++) {
            newIndirection[i] = indirection[i - 1];
            newPrediction[i] = prediction[i - 1];
        }

        indirection = newIndirection;
        prediction = newPrediction;

        // also might need to modify predictionSet
        if (isPrediction) {

            int[] newPredictionSet = new int[predictionSet.length + 1];
            for (int i = 0; i < predictionSet.length; i++)
                newPredictionSet[i] = predictionSet[i];
            newPredictionSet[predictionSet.length] = getNumColumns();

            predictionSet = newPredictionSet;

        }

        for (int i = 0; i < predictionSet.length; i++)
            if (predictionSet[i] > position)
                predictionSet[i]++;

        //Arrays.sort(predictionSet);

    }

    private void removeIndirectionAndPrediction(int position) {

        boolean isPrediction = prediction[position];
        int value = indirection[position];

        int[] newIndirection = new int[indirection.length - 1];
        boolean[] newPrediction = new boolean[newIndirection.length];

        for (int i = 0; i < position; i++) {
            newIndirection[i] = indirection[i];
            newPrediction[i] = prediction[i];
        }
        for (int i = position + 1; i < indirection.length; i++) {
            newIndirection[i - 1] = indirection[i];
            newPrediction[i - 1] = prediction[i];
        }

        indirection = newIndirection;
        prediction = newPrediction;

        // index management
        int i;
        if (isPrediction) {
            for (i = 0; i < indirection.length; i++)
                if (prediction[i] && indirection[i] > value)
                    indirection[i]--;
        }
        else {
            for (i = 0; i < indirection.length; i++)
                if (!prediction[i] && indirection[i] > value)
                    indirection[i]--;
        }

        // also might need to modify predictionSet
        if (isPrediction) {

            int[] newPredictionSet = new int[predictionSet.length - 1];
            int idx = -1;
            for (i = 0; i < predictionSet.length; i++) {
                idx++;
                if (predictionSet[i] == position)
                    idx--;
                else if (predictionSet[i] < position)
                    newPredictionSet[idx] = predictionSet[i];
                else
                    newPredictionSet[idx] = predictionSet[i] - 1;
            }

            predictionSet = newPredictionSet;

        }
        else {

            for (i = 0; i < predictionSet.length; i++)
                if (predictionSet[i] > position)
                    predictionSet[i]--;

        }

    }



   /*****************************************************************************/
/* Table methods                                                             */
/*****************************************************************************/

   //String table_label, table_comment;

   public Object getObject(int row, int column) {
       if (prediction[column])
           return predictionColumns[indirection[column]].getObject(subset[row]);
       else
           //return original.getObject(row, indirection[column]);
      return (Object)dataSource.getObjectData(subset[row], column).toString();
   }

   public int getInt(int row, int column) {
       if (prediction[column])
           return predictionColumns[indirection[column]].getInt(subset[row]);

       else
           //return original.getInt(row, indirection[column]);

           return (int)dataSource.getNumericData(subset[row], column);
   }

   public short getShort(int row, int column) {
     if (prediction[column])
           return predictionColumns[indirection[column]].getShort(subset[row]);
       else
              return (short)dataSource.getNumericData(subset[row], column);
           //return original.getShort(row, indirection[column]);
   }

   public float getFloat(int row, int column) {
     if (prediction[column])
           return predictionColumns[indirection[column]].getFloat(subset[row]);
       else
              return (float)dataSource.getNumericData(subset[row], column);
           //return original.getFloat(row, indirection[column]);
   }

   public double getDouble(int row, int column) {
     if (prediction[column])
           return predictionColumns[indirection[column]].getDouble(subset[row]);
       else
              return (double)dataSource.getNumericData(subset[row], column);
           //return original.getDouble(row, indirection[column]);
   }

   public long getLong(int row, int column) {
     if (prediction[column])
           return predictionColumns[indirection[column]].getLong(subset[row]);
       else
         return (long)dataSource.getNumericData(subset[row], column);
           //return original.getLong(row, indirection[column]);
   }

   public String getString(int row, int column) {
     if (prediction[column])
           return predictionColumns[indirection[column]].getString(subset[row]);

       else
              return dataSource.getTextData(subset[row], column);
         //  return original.getString(row, indirection[column]);
   }

   public byte[] getBytes(int row, int column) {
     if (prediction[column])
           return predictionColumns[indirection[column]].getBytes(subset[row]);
       else
           return dataSource.getTextData(subset[row], column).getBytes();
          // return original.getBytes(row, indirection[column]);
   }

   public boolean getBoolean(int row, int column) {
     if (prediction[column])
           return predictionColumns[indirection[column]].getBoolean(subset[row]);
       else
         return new Boolean(dataSource.getTextData(subset[row], column)).booleanValue();
           //return original.getBoolean(row, indirection[column]);
   }

   public char[] getChars(int row, int column) {
     if (prediction[column])
           return predictionColumns[indirection[column]].getChars(subset[row]);
       else
         return dataSource.getTextData(subset[row], column).toCharArray();
           //return original.getChars(row, indirection[column]);
   }

   public byte getByte(int row, int column) {
     if (prediction[column])
           return predictionColumns[indirection[column]].getByte(subset[row]);
       else
          return dataSource.getTextData(subset[row], column).getBytes()[0];
          // return original.getByte(row, indirection[column]);
   }

   public char getChar(int row, int column) {
     if (prediction[column])
           return predictionColumns[indirection[column]].getChar(subset[row]);
       else
         return dataSource.getTextData(subset[row], column).toCharArray()[0];
         //  return original.getChar(row, indirection[column]);
   }




   public String getColumnLabel(int position) {
       if (prediction[position])
         return predictionColumns[indirection[position]].getLabel();

       else
           return original.getColumnLabel(position);
   }

   public String getColumnComment(int position) {
       if (prediction[position])
           return predictionColumns[indirection[position]].getComment();

       else
           return original.getColumnComment(position);
   }

   /*public String getLabel() {
       if (table_label == null)
           table_label = original.getLabel();

       return table_label;
   }

   public void setLabel(String labl) {
       table_label = labl;
   }

   public String getComment() {
       if (table_comment == null)
           table_comment = original.getComment();

       return table_comment;
   }

   public void setComment(String comment) {
       table_comment = comment;
   }
   */

/*   public int getNumRows() {
       return original.getNumRows();
   }*/

   /*public int getNumEntries() {
       return (original.getNumEntries() + predictionColumnsTable.getNumEntries());
   }*/

   public int getNumColumns() {
       if (newTableHackVariable)
           return original.getNumColumns();


       return (original.getNumColumns() + predictionColumnsTable.getNumColumns());
   }

/*
//    VERED 8-26-03
//    was replaced by getRow() which return a Row object to access
    //the table.

   public void getRow(Object buffer, int position) {

       int numCols = getNumColumns();

       if (buffer instanceof int[]) {
           int[] b = (int[])buffer;
           for (int i = 0; i < b.length && i < numCols; i++)
               b[i] = getInt(position, i);
       }
       else if (buffer instanceof float[]) {
           float[] b = (float[])buffer;
           for (int i = 0; i < b.length && i < numCols; i++)
               b[i] = getFloat(position, i);
       }
       else if (buffer instanceof double[]) {
           double[] b = (double[])buffer;
           for (int i = 0; i < b.length && i < numCols; i++)
               b[i] = getDouble(position, i);
       }
       else if (buffer instanceof long[]) {
           long[] b = (long[])buffer;
           for (int i = 0; i < b.length && i < numCols; i++)
               b[i] = getLong(position, i);
       }
       else if (buffer instanceof short[]) {
           short[] b = (short[])buffer;
           for (int i = 0; i < b.length && i < numCols; i++)
               b[i] = getShort(position, i);
       }
       else if (buffer instanceof boolean[]) {
           boolean[] b = (boolean[])buffer;
           for (int i = 0; i < b.length && i < numCols; i++)
               b[i] = getBoolean(position, i);
       }
       else if (buffer instanceof String[]) {
           String[] b = (String[])buffer;
           for (int i = 0; i < b.length && i < numCols; i++)
               b[i] = getString(position, i);
       }
       else if (buffer instanceof char[][]) {
           char[][] b = (char[][])buffer;
           for (int i = 0; i < b.length && i < numCols; i++)
               b[i] = getChars(position, i);
       }
       else if (buffer instanceof byte[][]) {
           byte[][] b = (byte[][])buffer;
           for (int i = 0; i < b.length && i < numCols; i++)
               b[i] = getBytes(position, i);
       }
       else if (buffer instanceof Object[]) {
           Object[] b = (Object[])buffer;
           for (int i = 0; i < b.length && i < numCols; i++)
               b[i] = getObject(position, i);
       }
       else if (buffer instanceof byte[]) {
           byte[] b = (byte[])buffer;
           for (int i = 0; i < b.length && i < numCols; i++)
               b[i] = getByte(position, i);
       }
       else if (buffer instanceof char[]) {
           char[] b = (char[])buffer;
           for (int i = 0; i < b.length && i < numCols; i++)
               b[i] = getChar(position, i);
       }

   }
*/

   //VEREd 8-25-03
   //this method was declared unnecessary - thus is removed.
   /*
   public void getColumn(Object buffer, int position) {
       if (prediction[position])
           predictionColumnsTable.getColumn(buffer, indirection[position]);
       else
           original.getColumn(buffer, indirection[position]);
   }*/

/*   public Table getSubset(int start, int len) {

      PagingExampleTable pet = (PagingExampleTable)((PagingTable)original.getSubset(start, len).toExampleTable());
      PagingPredictionTable ppt = new PagingPredictionTable(pet, pages, manager);

      ppt.predictionColumnsTable = (MutablePagingTable)predictionColumnsTable.getSubset(start, len);

   // copy indirection, prediction, prediction set
      int[] newIndirection = new int[indirection.length];
      boolean[] newPrediction = new boolean[prediction.length];

      int[] newPredictionSet = new int[1];
      if (predictionSet != null)
         newPredictionSet = new int[predictionSet.length];

      for (int i = 0; i < indirection.length; i++) {
         newIndirection[i] = indirection[i];
         newPrediction[i] = prediction[i];
      }
      if (predictionSet != null) {
         newPredictionSet = new int[predictionSet.length];
         for (int i = 0; i < predictionSet.length; i++)
            newPredictionSet[i] = predictionSet[i];
      }

      ppt.indirection = newIndirection;
      ppt.prediction = newPrediction;

      ppt.predictionSet = null;

      if (predictionSet != null)
         ppt.predictionSet = newPredictionSet;

      return ppt;

   }
   */

/*   public Table copy() {
      PagingExampleTable pet = (PagingExampleTable)((PagingTable)original.copy().toExampleTable());
      PagingPredictionTable ppt = new PagingPredictionTable(pet, pages, manager);

      ppt.predictionColumnsTable = predictionColumnsTable;
      ppt.indirection = indirection;
      ppt.prediction = prediction;
      ppt.predictionSet = predictionSet;

      return ppt;
   }
   */

/*   VERED 8-26-03
      removed this method since it does not exist in interfac ExampleTable

public TableFactory getTableFactory() {
       return original.getTableFactory();
   }
*/

   public boolean isColumnNominal(int position) {
       if (prediction[position])
           return predictionColumnsTable.isColumnNominal(indirection[position]);

       else
           return original.isColumnNominal(position);
   }

   public boolean isColumnScalar(int position) {
   if (prediction[position])
          return predictionColumnsTable.isColumnScalar(indirection[position]);

       else
           return original.isColumnScalar(position);
   }

   public void setColumnIsNominal(boolean value, int position) {
       if (prediction[position])
           predictionColumnsTable.setColumnIsNominal(value, indirection[position]);

       else
           original.setColumnIsNominal(value, position);
   }

   public void setColumnIsScalar(boolean value, int position) {
       if (prediction[position])
           predictionColumnsTable.setColumnIsScalar(value, indirection[position]);

       else
           original.setColumnIsScalar(value, position);
   }

   public boolean isColumnNumeric(int position) {
       if (prediction[position])
           return predictionColumnsTable.isColumnNumeric(indirection[position]);

       else
           return original.isColumnNumeric(position);
   }

   public int getColumnType(int position) {
       if (prediction[position])
           return predictionColumnsTable.getColumnType(indirection[position]);

       else
           return original.getColumnType(position);
   }

/*   public ExampleTable toExampleTable() {
       //return (DBPredictionTable2)this.copy();
       return this;
   }
   */

/*   public PredictionTable toPredictionTable() {
    return this;
   }
   */

   /*****************************************************************************/
/* PredictionTable methods                                                   */
/*****************************************************************************/

   public int[] getPredictionSet() {
       return predictionSet;
   }

   public void setPredictionSet(int[] ps) {
       Arrays.sort(ps);
       predictionSet = ps;
   }

   /**
    * Set an int prediciton in the specified prediction column.  The index into
    * the prediction set is used, not the actual column index.
    * @param prediction the value of the prediciton
    * @param row the row of the table
    * @param predictionColIdx the index into the prediction set
    */
   public void setIntPrediction(int prediction, int row, int predictionColIdx) {
       // setInt(prediction, row, predictionSet[predictionColIdx]);
       predictionColumnsTable.setInt(prediction, row, predictionColIdx);
   }

   /**
    * Set a float prediciton in the specified prediction column.  The index into
    * the prediction set is used, not the actual column index.
    * @param prediction the value of the prediciton
    * @param row the row of the table
    * @param predictionColIdx the index into the prediction set
    */
   public void setFloatPrediction(float prediction, int row, int predictionColIdx) {
       // setFloat(prediction, row, predictionSet[predictionColIdx]);
       predictionColumnsTable.setFloat(prediction, row, predictionColIdx);
   }

   /**
    * Set a double prediciton in the specified prediction column.  The index into
    * the prediction set is used, not the actual column index.
    * @param prediction the value of the prediciton
    * @param row the row of the table
    * @param predictionColIdx the index into the prediction set
    */
   public void setDoublePrediction(double prediction, int row, int predictionColIdx) {
       // setDouble(prediction, row, predictionSet[predictionColIdx]);
       predictionColumnsTable.setDouble(prediction, row, predictionColIdx);
   }

   /**
    * Set a long prediciton in the specified prediction column.  The index into
    * the prediction set is used, not the actual column index.
    * @param prediction the value of the prediciton
    * @param row the row of the table
    * @param predictionColIdx the index into the prediction set
    */
   public void setLongPrediction(long prediction, int row, int predictionColIdx) {
       // setLong(prediction, row, predictionSet[predictionColIdx]);
       predictionColumnsTable.setLong(prediction, row, predictionColIdx);
   }

   /**
    * Set a short prediciton in the specified prediction column.  The index into
    * the prediction set is used, not the actual column index.
    * @param prediction the value of the prediciton
    * @param row the row of the table
    * @param predictionColIdx the index into the prediction set
    */
   public void setShortPrediction(short prediction, int row, int predictionColIdx) {
       // setShort(prediction, row, predictionSet[predictionColIdx]);
       predictionColumnsTable.setShort(prediction, row, predictionColIdx);
   }

   /**
    * Set a boolean prediciton in the specified prediction column.  The index into
    * the prediction set is used, not the actual column index.
    * @param prediction the value of the prediciton
    * @param row the row of the table
    * @param predictionColIdx the index into the prediction set
    */
   public void setBooleanPrediction(boolean prediction, int row, int predictionColIdx) {
       // setBoolean(prediction, row, predictionSet[predictionColIdx]);
       predictionColumnsTable.setBoolean(prediction, row, predictionColIdx);
   }

   /**
    * Set a String prediciton in the specified prediction column.  The index into
    * the prediction set is used, not the actual column index.
    * @param prediction the value of the prediciton
    * @param row the row of the table
    * @param predictionColIdx the index into the prediction set
    */
   public void setStringPrediction(String prediction, int row, int predictionColIdx) {
       predictionColumnsTable.setString(prediction, row, predictionColIdx);
   }

   /**
    * Set a char[] prediciton in the specified prediction column.  The index into
    * the prediction set is used, not the actual column index.
    * @param prediction the value of the prediciton
    * @param row the row of the table
    * @param predictionColIdx the index into the prediction set
    */
   public void setCharsPrediction(char[] prediction, int row, int predictionColIdx) {
       predictionColumnsTable.setChars(prediction, row, predictionColIdx);
   }

   /**
    * Set a byte[] prediciton in the specified prediction column.  The index into
    * the prediction set is used, not the actual column index.
    * @param prediction the value of the prediciton
    * @param row the row of the table
    * @param predictionColIdx the index into the prediction set
    */
   public void setBytesPrediction(byte[] prediction, int row, int predictionColIdx) {
       predictionColumnsTable.setBytes(prediction, row, predictionColIdx);
   }

   /**
    * Set an Object prediciton in the specified prediction column.  The index into
    * the prediction set is used, not the actual column index.
    * @param prediction the value of the prediciton
    * @param row the row of the table
    * @param predictionColIdx the index into the prediction set
    */
   public void setObjectPrediction(Object prediction, int row, int predictionColIdx) {
       predictionColumnsTable.setObject(prediction, row, predictionColIdx);
   }

   /**
    * Set a byte prediciton in the specified prediction column.  The index into
    * the prediction set is used, not the actual column index.
    * @param prediction the value of the prediciton
    * @param row the row of the table
    * @param predictionColIdx the index into the prediction set
    */
   public void setBytePrediction(byte prediction, int row, int predictionColIdx) {
       predictionColumnsTable.setByte(prediction, row, predictionColIdx);
   }

   /**
    * Set a char prediciton in the specified prediction column.   The index into
    * the prediction set is used, not the actual column index.
    * @param prediction the value of the prediciton
    * @param row the row of the table
    * @param predictionColIdx the index into the prediction set
    */
   public void setCharPrediction(char prediction, int row, int predictionColIdx) {
       predictionColumnsTable.setChar(prediction, row, predictionColIdx);
   }

   /**
    * Get an int prediciton in the specified prediction column.  The index into
    * the prediction set is used, not the actual column index.
    * @param row the row of the table
    * @param predictionColIdx the index into the prediction set
    * @return the prediction at (row, getPredictionSet()[predictionColIdx])
    */
   public int getIntPrediction(int row, int predictionColIdx) {
       return predictionColumns[predictionColIdx].getInt(row);
   }

   /**
    * Get a float prediciton in the specified prediction column.  The index into
    * the prediction set is used, not the actual column index.
    * @param row the row of the table
    * @param predictionColIdx the index into the prediction set
    * @return the prediction at (row, getPredictionSet()[predictionColIdx])
    */
   public float getFloatPrediction(int row, int predictionColIdx) {
       return predictionColumns[predictionColIdx].getFloat(row);
   }

   /**
    * Get a double prediciton in the specified prediction column.  The index into
    * the prediction set is used, not the actual column index.
    * @param row the row of the table
    * @param predictionColIdx the index into the prediction set
    * @return the prediction at (row, getPredictionSet()[predictionColIdx])
    */
   public double getDoublePrediction(int row, int predictionColIdx) {
       return predictionColumns[predictionColIdx].getDouble(row);
   }

   /**
    * Get a long prediciton in the specified prediction column.  The index into
    * the prediction set is used, not the actual column index.
    * @param row the row of the table
    * @param predictionColIdx the index into the prediction set
    * @return the prediction at (row, getPredictionSet()[predictionColIdx])
    */
   public long getLongPrediction(int row, int predictionColIdx) {
       return predictionColumns[predictionColIdx].getLong(row);
   }

   /**
    * Get a short prediciton in the specified prediction column.  The index into
    * the prediction set is used, not the actual column index.
    * @param row the row of the table
    * @param predictionColIdx the index into the prediction set
    * @return the prediction at (row, getPredictionSet()[predictionColIdx])
    */
   public short getShortPrediction(int row, int predictionColIdx) {
       return predictionColumns[predictionColIdx].getShort(row);
   }

   /**
    * Get a boolean prediciton in the specified prediction column.  The index into
    * the prediction set is used, not the actual column index.
    * @param row the row of the table
    * @param predictionColIdx the index into the prediction set
    * @return the prediction at (row, getPredictionSet()[predictionColIdx])
    */
   public boolean getBooleanPrediction(int row, int predictionColIdx) {
       return predictionColumns[predictionColIdx].getBoolean(row);
   }

   /**
    * Get a String prediciton in the specified prediction column.  The index into
    * the prediction set is used, not the actual column index.
    * @param row the row of the table
    * @param predictionColIdx the index into the prediction set
    * @return the prediction at (row, getPredictionSet()[predictionColIdx])
    */
   public String getStringPrediction(int row, int predictionColIdx) {
       return predictionColumns[predictionColIdx].getString(row);
   }

   /**
    * Get a char[] prediciton in the specified prediction column.  The index into
    * the prediction set is used, not the actual column index.
    * @param row the row of the table
    * @param predictionColIdx the index into the prediction set
    * @return the prediction at (row, getPredictionSet()[predictionColIdx])
    */
   public char[] getCharsPrediction(int row, int predictionColIdx) {
       return predictionColumns[predictionColIdx].getChars(row);
   }

   /**
    * Get a byte[] prediciton in the specified prediction column.  The index into
    * the prediction set is used, not the actual column index.
    * @param row the row of the table
    * @param predictionColIdx the index into the prediction set
    * @return the prediction at (row, getPredictionSet()[predictionColIdx])
    */
   public byte[] getBytesPrediction(int row, int predictionColIdx) {
       return predictionColumns[predictionColIdx].getBytes(row);
   }

   /**
    * Get an Object prediciton in the specified prediction column.  The index into
    * the prediction set is used, not the actual column index.
    * @param row the row of the table
    * @param predictionColIdx the index into the prediction set
    * @return the prediction at (row, getPredictionSet()[predictionColIdx])
    */
   public Object getObjectPrediction(int row, int predictionColIdx) {
       return predictionColumns[predictionColIdx].getObject(row);
   }

   /**
    * Get a byte prediciton in the specified prediction column.  The index into
    * the prediction set is used, not the actual column index.
    * @param row the row of the table
    * @param predictionColIdx the index into the prediction set
    * @return the prediction at (row, getPredictionSet()[predictionColIdx])
    */
   public byte getBytePrediction(int row, int predictionColIdx) {
       return predictionColumns[predictionColIdx].getByte(row);
   }

   /**
    * Get a char prediciton in the specified prediction column.  The index into
    * the prediction set is used, not the actual column index.
    * @param row the row of the table
    * @param predictionColIdx the index into the prediction set
    * @return the prediction at (row, getPredictionSet()[predictionColIdx])
    */
   public char getCharPrediction(int row, int predictionColIdx) {
       return predictionColumns[predictionColIdx].getChar(row);
   }

   /**
    * Add a column of integer predictions to this PredictionTable.
    * @param predictions the predictions
    * @param label the label for the new column
    * @return the index of the prediction column in the prediction set
    *
    * VERED 8-25-03
    * this method was replaced by addColumns(int datatype, int num) in colaboration
    * with the setter methods for the population of data.
    */

/*   public int addPredictionColumn(int[] predictions, String label) {
       insertIndirectionAndPrediction(true, getNumColumns());
       if (newTableHackVariable) {
           predictionColumnsTable.insertColumn(predictions, 0);
           predictionColumnsTable.removeColumn(1);
           predictionColumnsTable.setColumnLabel(label, 0);
           newTableHackVariable = false;
           return 0;
       }
       else {
           predictionColumnsTable.addColumn(predictions);
           predictionColumnsTable.setColumnLabel(label, predictionColumnsTable.getNumColumns() - 1);
           return (predictionColumnsTable.getNumColumns() - 1);
       }
   }
*/
   /**
    * Add a column of float predictions to this PredictionTable.
    * @param predictions the predictions
    * @param label the label for the new column
    * @return the index of the prediction column in the prediction set
    * VERED 8-25-03
    * this method was replaced by addColumns(int datatype, int num) in colaboration
    * with the setter methods for the population of data.
    */
/*   public int addPredictionColumn(float[] predictions, String label) {
       insertIndirectionAndPrediction(true, getNumColumns());
       if (newTableHackVariable) {
           predictionColumnsTable.insertColumn(predictions, 0);
           predictionColumnsTable.removeColumn(1);
           predictionColumnsTable.setColumnLabel(label, 0);
           newTableHackVariable = false;
           return 0;
       }
       else {
           predictionColumnsTable.addColumn(predictions);
           predictionColumnsTable.setColumnLabel(label, predictionColumnsTable.getNumColumns() - 1);
           return (predictionColumnsTable.getNumColumns() - 1);
       }
   }*/

   /**
    * Add a column of double predictions to this PredictionTable.
    * @param predictions the predictions
    * @param label the label for the new column
    * @return the index of the prediction column in the prediction set
    * VERED 8-25-03
    * this method was replaced by addColumns(int datatype, int num) in colaboration
    * with the setter methods for the population of data.
    */
/*   public int addPredictionColumn(double[] predictions, String label) {
       insertIndirectionAndPrediction(true, getNumColumns());
       if (newTableHackVariable) {
           predictionColumnsTable.insertColumn(predictions, 0);
           predictionColumnsTable.removeColumn(1);
           predictionColumnsTable.setColumnLabel(label, 0);
           newTableHackVariable = false;
           return 0;
       }
       else {
           predictionColumnsTable.addColumn(predictions);
           predictionColumnsTable.setColumnLabel(label, predictionColumnsTable.getNumColumns() - 1);
           return (predictionColumnsTable.getNumColumns() - 1);
       }
   }
*/
   /**
    * Add a column of long predictions to this PredictionTable.
    * @param predictions the predictions
    * @param label the label for the new column
    * @return the index of the prediction column in the prediction set
    *
    * VERED 8-25-03
    * this method was replaced by addColumns(int datatype, int num) in colaboration
    * with the setter methods for the population of data.
    */
    /*
   public int addPredictionColumn(long[] predictions, String label) {
       insertIndirectionAndPrediction(true, getNumColumns());
       if (newTableHackVariable) {
           predictionColumnsTable.insertColumn(predictions, 0);
           predictionColumnsTable.removeColumn(1);
           predictionColumnsTable.setColumnLabel(label, 0);
           newTableHackVariable = false;
           return 0;
       }
       else {
           predictionColumnsTable.addColumn(predictions);
           predictionColumnsTable.setColumnLabel(label, predictionColumnsTable.getNumColumns() - 1);
           return (predictionColumnsTable.getNumColumns() - 1);
       }
   }
*/
   /**
    * Add a column of short predictions to this PredictionTable.
    * @param predictions the predictions
    * @param label the label for the new column
    * @return the index of the prediction column in the prediction set
    *
    * VERED 8-25-03
    * this method was replaced by addColumns(int datatype, int num) in colaboration
    * with the setter methods for the population of data.
    */
    /*
   public int addPredictionColumn(short[] predictions, String label) {
       insertIndirectionAndPrediction(true, getNumColumns());
       if (newTableHackVariable) {
           predictionColumnsTable.insertColumn(predictions, 0);
           predictionColumnsTable.removeColumn(1);
           predictionColumnsTable.setColumnLabel(label, 0);
           newTableHackVariable = false;
           return 0;
       }
       else {
           predictionColumnsTable.addColumn(predictions);
           predictionColumnsTable.setColumnLabel(label, predictionColumnsTable.getNumColumns() - 1);
           return (predictionColumnsTable.getNumColumns() - 1);
       }
   }
*/
   /**
    * Add a column of boolean predictions to this PredictionTable.
    * @param predictions the predictions
    * @param label the label for the new column
    * @return the index of the prediction column in the prediction set
    *
    * VERED 8-25-03
    * this method was replaced by addColumns(int datatype, int num) in colaboration
    * with the setter methods for the population of data.
    */
    /*
   public int addPredictionColumn(boolean[] predictions, String label) {
       insertIndirectionAndPrediction(true, getNumColumns());
       if (newTableHackVariable) {
           predictionColumnsTable.insertColumn(predictions, 0);
           predictionColumnsTable.removeColumn(1);
           predictionColumnsTable.setColumnLabel(label, 0);
           newTableHackVariable = false;
           return 0;
       }
       else {
           predictionColumnsTable.addColumn(predictions);
           predictionColumnsTable.setColumnLabel(label, predictionColumnsTable.getNumColumns() - 1);
           return (predictionColumnsTable.getNumColumns() - 1);
       }
   }
*/

   /**
    * Add a column of String predictions to this PredictionTable.
    * @param predictions the predictions
    * @param label the label for the new column
    * @return the index of the prediction column in the prediction set
    *
    * VERED 8-25-03
    * this method was replaced by addColumns(int datatype, int num) in colaboration
    * with the setter methods for the population of data.
    */
    /*
   public int addPredictionColumn(String[] predictions, String label) {
       insertIndirectionAndPrediction(true, getNumColumns());
       if (newTableHackVariable) {
           predictionColumnsTable.insertColumn(predictions, 0);
           predictionColumnsTable.removeColumn(1);
           predictionColumnsTable.setColumnLabel(label, 0);
           newTableHackVariable = false;
           return 0;
       }
       else {
           predictionColumnsTable.addColumn(predictions);
           predictionColumnsTable.setColumnLabel(label, predictionColumnsTable.getNumColumns() - 1);
           return (predictionColumnsTable.getNumColumns() - 1);
       }
   }*/

   /**
    * Add a column of char[] predictions to this PredictionTable.
    * @param predictions the predictions
    * @param label the label for the new column
    * @return the index of the prediction column in the prediction set
    *
    * VERED 8-25-03
    * this method was replaced by addColumns(int datatype, int num) in colaboration
    * with the setter methods for the population of data.

    */
/*   public int addPredictionColumn(char[][] predictions, String label) {
       insertIndirectionAndPrediction(true, getNumColumns());
       if (newTableHackVariable) {
           predictionColumnsTable.insertColumn(predictions, 0);
           predictionColumnsTable.removeColumn(1);
           predictionColumnsTable.setColumnLabel(label, 0);
           newTableHackVariable = false;
           return 0;
       }
       else {
           predictionColumnsTable.addColumn(predictions);
           predictionColumnsTable.setColumnLabel(label, predictionColumnsTable.getNumColumns() - 1);
           return (predictionColumnsTable.getNumColumns() - 1);
       }
   }
*/

   /**
    * Add a column of byte[] predictions to this PredictionTable.
    * @param predictions the predictions
    * @param label the label for the new column
    * @return the index of the prediction column in the prediction set
    *
    * VERED 8-25-03
    * this method was replaced by addColumns(int datatype, int num) in colaboration
    * with the setter methods for the population of data.

    */
/*   public int addPredictionColumn(byte[][] predictions, String label) {
       insertIndirectionAndPrediction(true, getNumColumns());
       if (newTableHackVariable) {
           predictionColumnsTable.insertColumn(predictions, 0);
           predictionColumnsTable.removeColumn(1);
           predictionColumnsTable.setColumnLabel(label, 0);
           newTableHackVariable = false;
           return 0;
       }
       else {
           predictionColumnsTable.addColumn(predictions);
           predictionColumnsTable.setColumnLabel(label, predictionColumnsTable.getNumColumns() - 1);
           return (predictionColumnsTable.getNumColumns() - 1);
       }
   }
*/

   /**
    * Add a column of Object predictions to this PredictionTable.
    * @param predictions the predictions
    * @param label the label for the new column
    * @return the index of the prediction column in the prediction set
    *
    * VERED 8-25-03
    * this method was replaced by addColumns(int datatype, int num) in colaboration
    * with the setter methods for the population of data.

    */
/*   public int addPredictionColumn(Object[] predictions, String label) {
       insertIndirectionAndPrediction(true, getNumColumns());
       if (newTableHackVariable) {
           predictionColumnsTable.insertColumn(predictions, 0);
           predictionColumnsTable.removeColumn(1);
           predictionColumnsTable.setColumnLabel(label, 0);
           newTableHackVariable = false;
           return 0;
       }
       else {
           predictionColumnsTable.addColumn(predictions);
           predictionColumnsTable.setColumnLabel(label, predictionColumnsTable.getNumColumns() - 1);
           return (predictionColumnsTable.getNumColumns() - 1);
       }
   }
*/

/**
    * Add a column of byte predictions to this PredictionTable.
    * @param predictions the predictions
    * @param label the label for the new column
    * @return the index of the prediction column in the prediction set
    *
    * VERED 8-25-03
    * this method was replaced by addColumns(int datatype, int num) in colaboration
    * with the setter methods for the population of data.

    */
/*   public int addPredictionColumn(byte[] predictions, String label) {
       insertIndirectionAndPrediction(true, getNumColumns());
       if (newTableHackVariable) {
           predictionColumnsTable.insertColumn(predictions, 0);
           predictionColumnsTable.removeColumn(1);
           predictionColumnsTable.setColumnLabel(label, 0);
           newTableHackVariable = false;
           return 0;
       }
       else {
           predictionColumnsTable.addColumn(predictions);
           predictionColumnsTable.setColumnLabel(label, predictionColumnsTable.getNumColumns() - 1);
           return (predictionColumnsTable.getNumColumns() - 1);
       }
   }
*/

   /**
    * Add a column of char predictions to this PredictionTable.
    * @param predictions the predictions
    * @param label the label for the new column
    * @return the index of the prediction column in the prediction set
    *
    * VERED 8-25-03
    * this method was replaced by addColumns(int datatype, int num) in colaboration
    * with the setter methods for the population of data.

    */
/*   public int addPredictionColumn(char[] predictions, String label) {
       insertIndirectionAndPrediction(true, getNumColumns());
       if (newTableHackVariable) {
           predictionColumnsTable.insertColumn(predictions, 0);
           predictionColumnsTable.removeColumn(1);
           predictionColumnsTable.setColumnLabel(label, 0);
           newTableHackVariable = false;
           return 0;
       }
       else {
           predictionColumnsTable.addColumn(predictions);
           predictionColumnsTable.setColumnLabel(label, predictionColumnsTable.getNumColumns() - 1);
           return (predictionColumnsTable.getNumColumns() - 1);
       }
   }
*/
}