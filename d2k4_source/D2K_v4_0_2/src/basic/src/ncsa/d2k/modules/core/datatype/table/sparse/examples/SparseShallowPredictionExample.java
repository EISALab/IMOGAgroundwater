//package ncsa.d2k.modules.projects.vered.sparse.example;
package ncsa.d2k.modules.core.datatype.table.sparse.examples;

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

public class SparseShallowPredictionExample extends SparseShallowExample
  implements PredictionExample {

  public SparseShallowPredictionExample(SparsePredictionTable t, int r) {
    super(t, r);
  }


  public int getNumPredictions(){
    return ((SparsePredictionTable)table).getNumPredictions();
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
    return ((SparsePredictionTable)table).getDoublePrediction(row, p);
  }

  public String getStringPrediction(int p) {
    return ((SparsePredictionTable)table).getStringPrediction(row, p);
  }

  public int getIntPrediction(int p) {
    return ((SparsePredictionTable)table).getIntPrediction(row, p);
  }

  public float getFloatPrediction(int p) {
    return ((SparsePredictionTable)table).getFloatPrediction(row, p);
  }

  public short getShortPrediction(int p) {
      return ((SparsePredictionTable)table).getShortPrediction(row, p);
  }

  public long getLongPrediction(int p) {
    return ((SparsePredictionTable)table).getLongPrediction(row, p);
  }

  public byte getBytePrediction(int p) {
    return ((SparsePredictionTable)table).getBytePrediction(row, p);
  }

  public Object getObjectPrediction(int p) {
    return ((SparsePredictionTable)table).getObjectPrediction(row, p);
  }

  public char getCharPrediction(int p) {
    return ((SparsePredictionTable)table).getCharPrediction(row, p);
  }

  public char[] getCharsPrediction(int p) {
    return ((SparsePredictionTable)table).getCharsPrediction(row, p);
  }

  public byte[] getBytesPrediction(int p) {
    return ((SparsePredictionTable)table).getBytesPrediction(row, p);
  }

  public boolean getBooleanPrediction(int p) {
   return ((SparsePredictionTable)table).getBooleanPrediction(row, p);

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
    ((SparsePredictionTable)table).setDoublePrediction(pred, row, p);
  }

  public void setStringPrediction(String pred, int p) {
    ((SparsePredictionTable)table).setStringPrediction(pred, row, p);
  }

  public void setIntPrediction(int pred, int p) {
    ((SparsePredictionTable)table).setIntPrediction(pred, row, p);
  }

  public void setFloatPrediction(float pred, int p) {
    ((SparsePredictionTable)table).setFloatPrediction(pred, row, p);
  }

  public void setShortPrediction(short pred, int p) {
      ((SparsePredictionTable)table).setShortPrediction(pred, row, p);
  }

  public void setLongPrediction(long pred, int p) {
    ((SparsePredictionTable)table).setLongPrediction(pred, row, p);
  }

  public void setBytePrediction(byte pred, int p) {
    ((SparsePredictionTable)table).setBytePrediction(pred, row, p);
  }

  public void setObjectPrediction(Object pred, int p) {
    ((SparsePredictionTable)table).setObjectPrediction(pred, row, p);
  }

  public void setCharPrediction(char pred, int p) {
    ((SparsePredictionTable)table).setCharPrediction(pred, row, p);
  }

  public void setCharsPrediction(char[] pred, int p) {
    ((SparsePredictionTable)table).setCharsPrediction(pred, row, p);
  }

  public void setBytesPrediction(byte[] pred, int p) {
    ((SparsePredictionTable)table).setBytesPrediction(pred, row, p);
  }

  public void setBooleanPrediction(boolean pred, int p) {
   ((SparsePredictionTable)table).setBooleanPrediction(pred, row, p);

   }

}