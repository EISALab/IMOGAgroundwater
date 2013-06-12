//package ncsa.d2k.modules.projects.vered.sparse.example;
package ncsa.d2k.modules.core.datatype.table.sparse.examples;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.PredictionExample;
//import ncsa.d2k.modules.projects.vered.sparse.table.SparsePredictionTable;
import ncsa.d2k.modules.core.datatype.table.sparse.SparsePredictionTable;

/**
 * Title:        Sparse Table
 * Description:  Sparse Table projects will implement data structures compatible to the interface tree of Table, for sparsely stored data.
 * Copyright:    Copyright (c) 2002
 * Company:      ncsa
 * @author vered goren
 * @version 1.0
 */

public class SparsePredictionExample extends  SparsePredictionTable
    implements PredictionExample{

    protected int row;

  public SparsePredictionExample(SparsePredictionTable t, int r) {
     super((SparsePredictionTable)(t.getSubset(r, 1)));
    row = r;
    setInputFeatures(t.getInputFeatures(row));
    setOutputFeatures(t.getOutputFeatures(row));
  }


   /**
   * This constructor is to be used by Test and Train Tables.
   * @param t     must be a single row table. actually is the example itself.
   */
   public SparsePredictionExample(SparsePredictionTable t) {
    super(t);
    row = t.getAllRows()[0];
   }


  /**
   * ******************************************
   * get prediction type methods
   * ******************************************
   *
   * return the data at column no. p into the prediction set in this example
   * the representation of the data varies according to the returned type.
   */
  public double getDoublePrediction(int p) {
    return getDoublePrediction(row, p);
  }

  public String getStringPrediction(int p) {
    return getStringPrediction(row, p);
  }

  public int getIntPrediction(int p) {
    return getIntPrediction(row, p);
  }

  public float getFloatPrediction(int p) {
    return getFloatPrediction(row, p);
  }

  public short getShortPrediction(int p) {
      return getShortPrediction(row, p);
  }

  public long getLongPrediction(int p) {
    return getLongPrediction(row, p);
  }

  public byte getBytePrediction(int p) {
    return getBytePrediction(row, p);
  }

  public Object getObjectPrediction(int p) {
    return getObjectPrediction(row, p);
  }

  public char getCharPrediction(int p) {
    return getCharPrediction(row, p);
  }

  public char[] getCharsPrediction(int p) {
    return getCharsPrediction(row, p);
  }

  public byte[] getBytesPrediction(int p) {
    return getBytesPrediction(row, p);
  }

  public boolean getBooleanPrediction(int p) {
   return getBooleanPrediction(row, p);

   }





  /**
   * ******************************************
   * set prediction type methods
   * ******************************************
   *
   * set the value <code>pred</code> to column no. p into the prediction set in
   * this example.
   */
  public void setDoublePrediction(double pred, int p) {
    setDoublePrediction(pred, row, p);
  }

  public void setStringPrediction(String pred, int p) {
    setStringPrediction(pred, row, p);
  }

  public void setIntPrediction(int pred, int p) {
    setIntPrediction(pred, row, p);
  }

  public void setFloatPrediction(float pred, int p) {
    setFloatPrediction(pred, row, p);
  }

  public void setShortPrediction(short pred, int p) {
      setShortPrediction(pred, row, p);
  }

  public void setLongPrediction(long pred, int p) {
    setLongPrediction(pred, row, p);
  }

  public void setBytePrediction(byte pred, int p) {
    setBytePrediction(pred, row, p);
  }

  public void setObjectPrediction(Object pred, int p) {
    setObjectPrediction(pred, row, p);
  }

  public void setCharPrediction(char pred, int p) {
    setCharPrediction(pred, row, p);
  }

  public void setCharsPrediction(char[] pred, int p) {
    setCharsPrediction(pred, row, p);
  }

  public void setBytesPrediction(byte[] pred, int p) {
    setBytesPrediction(pred, row, p);
  }

  public void setBooleanPrediction(boolean pred, int p) {
   setBooleanPrediction(pred, row, p);

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

}
