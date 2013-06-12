//package ncsa.d2k.modules.projects.vered.sparse.example;
package ncsa.d2k.modules.core.datatype.table.sparse.examples;

import ncsa.d2k.modules.core.datatype.table.*;

//import ncsa.d2k.modules.projects.vered.sparse.primitivehash.VIntHashSet;
//import ncsa.d2k.modules.projects.vered.sparse.table.SparseExampleTable;
import ncsa.d2k.modules.core.datatype.table.sparse.primitivehash.VIntHashSet;
import ncsa.d2k.modules.core.datatype.table.sparse.SparseExampleTable;

/**
 * Title:        Sparse Table
 * Description:  Sparse Table projects will implement data structures compatible to the interface tree of Table, for sparsely stored data.
 * Copyright:    Copyright (c) 2002
 * Company:      ncsa
 * @author vered goren
 * @version 1.0
 *
 * SparseExample is a single row SparseExampleTable.
 * the main different of a SparseExample from a regular Example is that the index
 * the get method receive as a parameter is an index into the array of the input
 * or output set of this specific Example (row) where as in a regular ExmapleTable
 * each row has the same input features and same output features.
 */

public class SparseExample extends SparseExampleTable implements Example {


  protected int row;


  public SparseExample(SparseExampleTable t, int r) {
    super((SparseExampleTable)(t.getSubset(r, 1)));
    row = r;
    setInputFeatures(t.getInputFeatures(row));
    setOutputFeatures(t.getOutputFeatures(row));

  }

  /**
   * This constructor is to be used by Test and Train Tables.
   * @param t     must be a single row table. actually is the example itself.
   */
   public SparseExample(SparseExampleTable t) {
    super(t);
    row = t.getAllRows()[0];
   }


  /**
   * GET TYPE METHODS.
   * i or o are indices into the input and output sets.
   *
   * Returns the data at column no. <code>i</code> in the input features set
   * or column no. <code>o</code> in the output features set, in this example.
   *
   */


  public double getInputDouble(int i) {
    return getInputDouble(row, i);
  }
  public double getOutputDouble(int o) {
     return getOutputDouble(row, o);
  }

  public String getInputString(int i) {
     return getInputString(row, i);
  }
  public String getOutputString(int o) {
     return getOutputString(row, o);
  }
  public int getInputInt(int i) {
     return getInputInt(row, i);
  }
  public int getOutputInt(int o) {
     return getOutputInt(row, o);
  }
  public float getInputFloat(int i) {
    return getInputFloat(row, i);
  }
  public float getOutputFloat(int o) {
    return getOutputFloat(row, o);
  }
  public short getInputShort(int i) {
    return getInputShort(row, i);
  }
  public short getOutputShort(int o) {
    return getOutputShort(row, o);
  }
  public long getInputLong(int i) {
    return getInputLong(row, i);
  }
  public long getOutputLong(int o) {
    return getOutputLong(row, o);
  }
  public byte getInputByte(int i) {
    return getInputByte(row, i);
  }
  public byte getOutputByte(int o) {
    return getOutputByte(row, o);
  }
  public Object getInputObject(int i) {
    return getInputObject(row, i);
  }
  public Object getOutputObject(int o) {
    return getOutputObject(row, o);
  }
  public char getInputChar(int i) {
    return getInputChar(row, i);
  }
  public char getOutputChar(int o) {
    return getOutputChar(row, o);
  }
  public char[] getInputChars(int i) {
    return getInputChars(row, i);
  }
  public char[] getOutputChars(int o) {
    return getOutputChars(row, o);
  }
  public byte[] getInputBytes(int i) {
    return getInputBytes(row, i);
  }
  public byte[] getOutputBytes(int o) {
   return getOutputBytes(row, o);
  }
  public boolean getInputBoolean(int i) {
    return getInputBoolean(row, i);
  }
  public boolean getOutputBoolean(int o) {
    return getOutputBoolean(row, o);
  }


  public Table getTable()
  {
	return this;
  }

  public double getDouble(int i) 
  {
    return getInputDouble(row, i);
  }

  public String getString(int i) 
  {
     return getInputString(row, i);
  }

  public int getInt(int i) 
  {
     return getInputInt(row, i);
  }

  public float getFloat(int i) 
  {
    return getInputFloat(row, i);
  }

  public short getShort(int i) 
  {
    return getInputShort(row, i);
  }

  public long getLong(int i) 
  {
    return getInputLong(row, i);
  }

  public byte getByte(int i) 
  {
    return getInputByte(row, i);
  }

  public Object getObject(int i) 
  {
    return getInputObject(row, i);
  }

  public char getChar(int i) 
  {
    return getInputChar(row, i);
  }

  public char[] getChars(int i) 
  {
    return getInputChars(row, i);
  }

  public byte[] getBytes(int i) 
  {
    return getInputBytes(row, i);
  }

  public boolean getBoolean(int i)
  {
    return getInputBoolean(row, i);
  }

  public void setIndex(int i)
  {
	  this.row = i;
  }



}//SparseExample
