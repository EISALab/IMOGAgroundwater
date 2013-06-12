package ncsa.d2k.modules.core.datatype.table.db;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.io.sql.*;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2003
 * Company:
 * @author
 * @version 1.0
 */

public class DBExample extends DBSubsetRow implements Example {

  protected int[] inputColumns;
  protected int[] outputColumns;

  public DBExample(DBDataSource _dataSource, DBConnection _dbConnection, DBTable _table) {
    super(_dataSource, _dbConnection, _table);
    if (_table instanceof ExampleTable){
      inputColumns = ((ExampleTable)_table).getInputFeatures();
      outputColumns = ((ExampleTable)_table).getOutputFeatures();

    }//if

  }

   public DBExample(DBDataSource _dataSource, DBConnection _dbConnection, DBTable _table, int[] indices) {
    super(_dataSource, _dbConnection, _table, indices);
    if (_table instanceof ExampleTable){
      inputColumns = ((ExampleTable)_table).getInputFeatures();
      outputColumns = ((ExampleTable)_table).getOutputFeatures();
    }//if

  }



  public double getInputDouble(int i) {
    return (double)dataSource.getNumericData(index, inputColumns[i]);
  }
  public double getOutputDouble(int o) {
    return (double)dataSource.getNumericData(index, outputColumns[o]);
  }


  public String getInputString(int i) {
   return dataSource.getTextData(index, inputColumns[i]);
  }
  public String getOutputString(int o) {
    return dataSource.getTextData(index, outputColumns[o]);
  }


  public int getInputInt(int i) {
    return (int)dataSource.getNumericData(index, inputColumns[i]);
  }
  public int getOutputInt(int o) {
     return (int)dataSource.getNumericData(index, outputColumns[o]);
  }



  public float getInputFloat(int i) {
    return (float)dataSource.getNumericData(index, inputColumns[i]);
  }
  public float getOutputFloat(int o) {
return (float)dataSource.getNumericData(index, outputColumns[o]);
  }


  public short getInputShort(int i) {
    return (short)dataSource.getNumericData(index, inputColumns[i]);
  }
  public short getOutputShort(int o) {
    return (short)dataSource.getNumericData(index, outputColumns[o]);
  }



  public long getInputLong(int i) {
     return (long)dataSource.getNumericData(index, inputColumns[i]);
  }
  public long getOutputLong(int o) {
    return (long)dataSource.getNumericData(index,outputColumns[o]);
  }



  public byte getInputByte(int i) {
    return dataSource.getTextData(index, inputColumns[i]).getBytes()[0];
  }
  public byte getOutputByte(int o) {
   return dataSource.getTextData(index, outputColumns[o]).getBytes()[0];
  }



  public Object getInputObject(int i) {
    return (Object)dataSource.getObjectData(index, inputColumns[i]).toString();
  }
  public Object getOutputObject(int o) {
    return (Object)dataSource.getObjectData(index, outputColumns[o]).toString();
  }


  public char getInputChar(int i) {
    	return dataSource.getTextData(index, inputColumns[i]).toCharArray()[0];
  }
  public char getOutputChar(int o) {
	return dataSource.getTextData(index, inputColumns[o]).toCharArray()[0];
  }


  public char[] getInputChars(int i) {
    return dataSource.getTextData(index, inputColumns[i]).toCharArray();
  }
  public char[] getOutputChars(int o) {
	return dataSource.getTextData(index, inputColumns[o]).toCharArray();
  }


  public byte[] getInputBytes(int i) {
    return dataSource.getTextData(index, inputColumns[i]).getBytes();
  }
  public byte[] getOutputBytes(int o) {
    return dataSource.getTextData(index, outputColumns[o]).getBytes();
  }


  public boolean getInputBoolean(int i) {
    return new Boolean(dataSource.getTextData(index, inputColumns[i])).booleanValue();
  }
  public boolean getOutputBoolean(int o) {
    return new Boolean(dataSource.getTextData(index, outputColumns[o])).booleanValue();
  }




}