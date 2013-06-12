package ncsa.d2k.modules.core.datatype.table.db;
import ncsa.d2k.modules.core.io.sql.*;
import ncsa.d2k.modules.core.datatype.table.MutableTable;
import ncsa.d2k.modules.core.datatype.table.basic.Column;
import ncsa.d2k.modules.core.datatype.table.Table;
import ncsa.d2k.modules.core.datatype.table.Transformation;
import java.util.List;
import ncsa.d2k.modules.core.datatype.table.Row;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2003
 * Company:
 * @author
 * @version 1.0
 */

public class DBSubsetTable extends DBTable implements MutableTable  {





    protected int[] subset;

DBSubsetTable(){}

    /**
     * constructors creates shallow copy of a subset of the original table.
     */
    public DBSubsetTable(DBTable _table){
      super(_table.dataSource, _table.dbConnection);

      if(_table instanceof DBSubsetTable ){
        int[] temp = ((DBSubsetTable)_table).subset;
        subset = new int[temp.length];
        for(int i=0; i<temp.length; i++)
          subset[i] = temp[i];
      }
      else{
        subset = new int[super.getNumRows()];
        for (int i=0; i<subset.length; i++)
          subset[i] = i;
      }

    }


public DBSubsetTable(DBDataSource _dbdatasource, DBConnection _dbconnection){
  super(_dbdatasource, _dbconnection);
  //initializing subset to include all of the Table by default.
  subset = new int[super.getNumRows()];
  for (int i=0; i<subset.length; i++)
    subset[i] = i;
}


      public DBSubsetTable(DBDataSource _dbdatasource, DBConnection _dbconnection, int[] indices){
        super(_dbdatasource, _dbconnection);
      subset = indices;
  }

    public DBSubsetTable (DBTable table, int[] indices){
      super(table.dataSource, table.dbConnection);
      subset = indices;
    }




  public void setColumn(Column col, int where) {
   throw new RuntimeException("Table mutation not supported in DBTable.");
  }
  public void addColumn(Column datatype) {
    throw new RuntimeException("Table mutation not supported in DBTable.");
  }
  public void addColumns(Column[] datatype) {
    throw new RuntimeException("Table mutation not supported in DBTable.");
  }
  public void insertColumn(Column col, int where) {
   throw new RuntimeException("Table mutation not supported in DBTable.");
  }
  public void insertColumns(Column[] datatype, int where) {
    throw new RuntimeException("Table mutation not supported in DBTable.");
  }
  public void insertRos(int where, int count) {
   throw new RuntimeException("Table mutation not supported in DBTable.");
 }

  public void removeColumn(int position) {
    throw new RuntimeException("Table mutation not supported in DBTable.");
  }
  public void removeColumns(int start, int len) {
    throw new RuntimeException("Table mutation not supported in DBTable.");
  }
  public void addRows(int howMany) {
    throw new RuntimeException("Table mutation not supported in DBTable.");
  }

  /**
   * this method will remove index #<code>row</code> from the subset.
   * @param row - the index number to be removed from the subset.
   * @author - vered.
   */
  public void removeRow(int row) {
    //if the index is too high - do nothing
    if (row > subset.length) return;

    //allocating the new set of indices.
    int[] tempSet = new int[subset.length-1];
    //initializing counter for subset.
    int i=0;
    //copy all indices smaller than row
    for (; i<row; i++)
      tempSet[i] = subset[i];

    //designating a counter for tempSet
    int j = i;
    //now i points to the index right after row.
    i++;
    //copying the rest of the indices.
    for (; i<subset.length; i++, j++)
      tempSet[j] = subset[i];
    //reseting the subset.
    subset = tempSet;
  }


  public void removeRows(int start, int len) {
    //if start is too high - do nothing
    if (start > subset.length) return;

    //allocating the new set of indices.
    int[] tempSet = new int[subset.length-len];
    //initializing counter for subset.
    int i=0;
    //copy all indices smaller than row
    for (; i<start; i++)
      tempSet[i] = subset[i];

    //designating a counter for tempSet
    int j = i;
    //now i points to the index right after start+len.
    i += len;
    //copying the rest of the indices.
    for (; i<subset.length && j<tempSet.length; i++, j++)
      tempSet[j] = subset[i];
    //reseting the subset.
    subset = tempSet;
  }

  public Table reorderRows(int[] newOrder) {
    throw new RuntimeException("Table mutation not supported in DBTable.");
  }
  public Table reorderColumns(int[] newOrder) {
    throw new RuntimeException("Table mutation not supported in DBTable.");
  }
  public void swapRows(int pos1, int pos2) {
    throw new RuntimeException("Table mutation not supported in DBTable.");
  }
  public void swapColumns(int pos1, int pos2) {
    throw new RuntimeException("Table mutation not supported in DBTable.");
  }
  public void setObject(Object element, int row, int column) {
    throw new RuntimeException("Table mutation not supported in DBTable.");
  }
  public void setInt(int data, int row, int column) {
    throw new RuntimeException("Table mutation not supported in DBTable.");
  }
  public void setShort(short data, int row, int column) {
    throw new RuntimeException("Table mutation not supported in DBTable.");
  }
  public void setFloat(float data, int row, int column) {
    throw new RuntimeException("Table mutation not supported in DBTable.");
  }
  public void setDouble(double data, int row, int column) {
   throw new RuntimeException("Table mutation not supported in DBTable.");
  }
  public void setLong(long data, int row, int column) {
    throw new RuntimeException("Table mutation not supported in DBTable.");
  }
  public void setString(String data, int row, int column) {
    throw new RuntimeException("Table mutation not supported in DBTable.");
  }
  public void setBytes(byte[] data, int row, int column) {
   throw new RuntimeException("Table mutation not supported in DBTable.");
  }
  public void setBoolean(boolean data, int row, int column) {
   throw new RuntimeException("Table mutation not supported in DBTable.");
  }
  public void setChars(char[] data, int row, int column) {
    throw new RuntimeException("Table mutation not supported in DBTable.");
  }
  public void setByte(byte data, int row, int column) {
   throw new RuntimeException("Table mutation not supported in DBTable.");
  }
  public void setChar(char data, int row, int column) {
    throw new RuntimeException("Table mutation not supported in DBTable.");
  }
  public void setColumnLabel(String label, int position) {
    throw new RuntimeException("Table mutation not supported in DBTable.");
  }
  public void setColumnComment(String comment, int position) {
    throw new RuntimeException("Table mutation not supported in DBTable.");
  }
  public void sortByColumn(int col) {
    throw new RuntimeException("Table mutation not supported in DBTable.");
  }
  public void sortByColumn(int col, int begin, int end) {
    throw new RuntimeException("Table mutation not supported in DBTable.");
  }
  public void addTransformation(Transformation tm) {
    throw new RuntimeException("Table mutation not supported in DBTable.");
  }
  public List getTransformations() {
    throw new RuntimeException("Table mutation not supported in DBTable.");
  }
  public void setValueToMissing(boolean b, int row, int col) {
    throw new RuntimeException("Table mutation not supported in DBTable.");
  }
  public void setValueToEmpty(boolean b, int row, int col) {
    throw new RuntimeException("Table mutation not supported in DBTable.");
  }
  public Object getObject(int row, int column) {
 return (Object)dataSource.getObjectData(subset[row], column).toString();
  }
  public int getInt(int row, int column) {
    return (int)dataSource.getNumericData(subset[row], column);

  }
  public short getShort(int row, int column) {
    return (short)dataSource.getNumericData(subset[row], column);
  }
  public float getFloat(int row, int column) {
    return (float)dataSource.getNumericData(subset[row], column);
  }
  public double getDouble(int row, int column) {
    return (double)dataSource.getNumericData(subset[row], column);
  }
  public long getLong(int row, int column) {
    return (long)dataSource.getNumericData(subset[row], column);
  }
  public String getString(int row, int column) {
    return dataSource.getTextData(subset[row], column);
  }
  public byte[] getBytes(int row, int column) {
    return dataSource.getTextData(subset[row], column).getBytes();
  }
  public boolean getBoolean(int row, int column) {
    return new Boolean(dataSource.getTextData(subset[row], column)).booleanValue();
  }
  public char[] getChars(int row, int column) {
    return dataSource.getTextData(row, column).toCharArray();
  }
  public byte getByte(int row, int column) {
    return dataSource.getTextData(subset[row], column).getBytes()[0];
  }
  public char getChar(int row, int column) {
    return dataSource.getTextData(row, column).toCharArray()[0];
  }

  public void setLabel(String labl) {
        throw new RuntimeException("Table mutation not supported in DBTable.");
  }

  public String getComment() {
       throw new RuntimeException("Table mutation not supported in DBTable.");
  }

  public void setComment(String comment) {
        throw new RuntimeException("Table mutation not supported in DBTable.");
  }


  public int getNumRows(){return subset.length;}

  protected int[] reSubset(int[] rows){
    int[] retValIndices = new int[Math.min(rows.length, subset.length)];
     for (int i=0; i<rows.length; i++){
      if(rows[i] < subset.length)
        retValIndices[i] = subset[rows[i]];
     }
     return retValIndices;
  }

  public Table getSubset(int[] rows) {
   int[] retValIndices = reSubset(rows);
   DBSubsetTable retVal = (DBSubsetTable) shallowCopy();
   retVal.subset = retValIndices;
   return retVal;
//   return new DBSubsetTable(this, retValIndices);

  }

  public Table copy() {
    int[] retValIndices = new int[subset.length];
    System.arraycopy(subset, 0, retValIndices, 0, subset.length);
    return new DBSubsetTable(this.dataSource.copy(), this.dbConnection, retValIndices);


  }


  public Table copy(int start, int len) {
    return this.copy().getSubset(start, len)    ;
  }

  public Table copy(int[] rows) {
    return this.copy().getSubset(rows)    ;
  }



  public Table shallowCopy(){

            DBSubsetTable retVal = new DBSubsetTable();
            copyAttributes(retVal);
            return retVal;
           }

       protected void copyAttributes(DBSubsetTable target){
         super.copyAttributes(target);
         target.subset = this.subset;

       }


  public void setColumnIsNominal(boolean value, int position) {
    this.isNominal[position] = value;
  }
  public void setColumnIsScalar(boolean value, int position) {
    //since this table return is scalar as ! is nominal this is an empty set.
    return;
  }



  public Row getRow() {
    return new DBSubsetRow(dataSource, dbConnection, this, subset);
  }

  /**
           * Return this Table as an ExampleTable.
           * @return This object as an ExampleTable
           */
         public ExampleTable toExampleTable(){
             DBExampleTable retVal =  new DBExampleTable(this, dataSource.copy(), dbConnection);
             retVal.subset = this.subset;
             return retVal;
     }

     public boolean isValueMissing(int row, int col) {
             if (this.getObject(subset[row],col) == null)
                 return true;
             else
         return false;
         }

         public boolean isValueEmpty(int row, int col) {
             if (this.getObject(subset[row],col) == null)
                 return true;
             else
                 return false;
         }



}