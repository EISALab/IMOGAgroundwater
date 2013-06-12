//package ncsa.d2k.modules.projects.vered.sparse.example;
package ncsa.d2k.modules.core.datatype.table.sparse.examples;

import ncsa.d2k.modules.core.datatype.table.*;

import ncsa.d2k.modules.core.datatype.table.Example;
//import ncsa.d2k.modules.projects.vered.sparse.table.SparseExampleTable;
import ncsa.d2k.modules.core.datatype.table.sparse.SparseExampleTable;

/**
 * Title:        Sparse Table
 * Description:  Sparse Table projects will implement data structures compatible to the interface tree of Table, for sparsely stored data.
 * Copyright:    Copyright (c) 2002
 * Company:      ncsa
 * @author vered goren
 * @version 1.0
 */

public class SparseShallowExample implements Example {

  protected int row;
  protected SparseExampleTable table;

  public SparseShallowExample(SparseExampleTable t, int r) {
    row = r;
    table = t;
  }



   /**
   * GET TYPE METHODS.
   * i or o are indices into the input and output sets of this specific example.
   *
   * Returns the data at column no. <code>i</code> in the input features set
   * or column no. <code>o</code> in the output features set of this example.
   *
   */


  public double getInputDouble(int i) {
    return table.getDouble(row, table.getInputFeatures(row)[i]);
  }
  public double getOutputDouble(int o) {
     return table.getDouble(row, table.getOutputFeatures(row)[o]);
  }

  public String getInputString(int i) {
     return table.getString(row, table.getInputFeatures(row)[i]);
  }
  public String getOutputString(int o) {
     return table.getString(row, table.getOutputFeatures(row)[o]);
  }
  public int getInputInt(int i) {
     return table.getInt(row, table.getInputFeatures(row)[i]);
  }
  public int getOutputInt(int o) {
     return table.getInt(row, table.getOutputFeatures(row)[o]);
  }
  public float getInputFloat(int i) {
    return table.getFloat(row, table.getInputFeatures(row)[i]);
  }
  public float getOutputFloat(int o) {
    return table.getFloat(row, table.getOutputFeatures(row)[o]);
  }
  public short getInputShort(int i) {
    return table.getShort(row, table.getInputFeatures(row)[i]);
  }
  public short getOutputShort(int o) {
    return table.getShort(row, table.getOutputFeatures(row)[o]);
  }
  public long getInputLong(int i) {
    return table.getLong(row, table.getInputFeatures(row)[i]);
  }
  public long getOutputLong(int o) {
    return table.getLong(row, table.getOutputFeatures(row)[o]);
  }
  public byte getInputByte(int i) {
    return table.getByte(row, table.getInputFeatures(row)[i]);
  }
  public byte getOutputByte(int o) {
    return table.getByte(row, table.getOutputFeatures(row)[o]);
  }
  public Object getInputObject(int i) {
    return table.getObject(row, table.getInputFeatures(row)[i]);
  }
  public Object getOutputObject(int o) {
    return table.getObject(row, table.getOutputFeatures(row)[o]);
  }
  public char getInputChar(int i) {
    return table.getChar(row, table.getInputFeatures(row)[i]);
  }
  public char getOutputChar(int o) {
    return table.getChar(row, table.getOutputFeatures(row)[o]);
  }
  public char[] getInputChars(int i) {
    return table.getChars(row, table.getInputFeatures(row)[i]);
  }
  public char[] getOutputChars(int o) {
    return table.getChars(row, table.getOutputFeatures(row)[o]);
  }
  public byte[] getInputBytes(int i) {
    return table.getBytes(row, table.getInputFeatures(row)[i]);
  }
  public byte[] getOutputBytes(int o) {
   return table.getBytes(row, table.getOutputFeatures(row)[o]);
  }
  public boolean getInputBoolean(int i) {
    return table.getBoolean(row, table.getInputFeatures(row)[i]);
  }
  public boolean getOutputBoolean(int o) {
    return table.getBoolean(row, table.getOutputFeatures(row)[o]);
  }


  public int getNumInputs() {
    return table.getNumInputs(row);
  }

  public int getNumOutputs() {
    return table.getNumOutputs(row);
  }

  public String getInputName(int i) {
    return table.getColumnLabel(table.getInputFeatures(row)[i]);
  }
  public String getOutputName(int o) {
    return table.getColumnLabel(table.getOutputFeatures(row)[o]);
  }
  public int getInputType(int i) {
    return table.getColumnType(table.getInputFeatures(row)[i]);
  }
  public int getOutputType(int o) {
    return table.getColumnType(table.getOutputFeatures(row)[o]);
  }

  public boolean isInputNominal(int i) {
    return table.isColumnNominal(table.getInputFeatures(row)[i]);
  }

  public boolean isOutputNominal(int o) {
    return table.isColumnNominal(table.getOutputFeatures(row)[o]);
  }

  public boolean isInputScalar(int i) {
   return table.isColumnScalar(table.getInputFeatures(row)[i]);
  }

  public boolean isOutputScalar(int o) {
    return table.isColumnScalar(table.getOutputFeatures(row)[o]);
  }

  // starting here, the rest are new from Xiaolei on 9/8/03

  public Table getTable()
  {
	return this.table;
  }


  public double getDouble(int i) 
  {
    return table.getInputDouble(row, i);
  }

  public String getString(int i) 
  {
     return table.getInputString(row, i);
  }

  public int getInt(int i) 
  {
     return table.getInputInt(row, i);
  }

  public float getFloat(int i) 
  {
    return table.getInputFloat(row, i);
  }

  public short getShort(int i) 
  {
    return table.getInputShort(row, i);
  }

  public long getLong(int i) 
  {
    return table.getInputLong(row, i);
  }

  public byte getByte(int i) 
  {
    return table.getInputByte(row, i);
  }

  public Object getObject(int i) 
  {
    return table.getInputObject(row, i);
  }

  public char getChar(int i) 
  {
    return table.getInputChar(row, i);
  }

  public char[] getChars(int i) 
  {
    return table.getInputChars(row, i);
  }

  public byte[] getBytes(int i) 
  {
    return table.getInputBytes(row, i);
  }

  public boolean getBoolean(int i)
  {
    return table.getInputBoolean(row, i);
  }

  public void setIndex(int i)
  {
	  this.row = i;
  }

}
