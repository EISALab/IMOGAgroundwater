package ncsa.d2k.modules.core.datatype.table.db;

import ncsa.d2k.modules.core.datatype.table.PredictionExample;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.Column;
import ncsa.d2k.modules.core.datatype.table.basic.SubsetTableImpl;
import ncsa.d2k.modules.core.io.sql.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author vered goren
 * @version 1.0
 *
 * this is an extension of DBExample which wraps another Row int it for getter methods
 * that relates to the prediction part. it also wraps a MutableTable in it for
 * the setter methods relating the prediction part.
 *
 *
 *
 *
 */

public class DBPredictionExample extends DBExample implements PredictionExample {

 // protected Row predictionExample;
  //protected SubsetTableImpl predictionTable;
 // protected MutableTable predictionColumns;
 protected Column[] predictionColumns;
  protected int[] indirection;
  protected boolean[] prediction;


  public DBPredictionExample(DBDataSource _dataSource,  DBConnection _dbConnection,
                             Column[] _predictionColumns, DBTable _table, int[] indices,
                             int[] _indirection, boolean[] _prediction) {
    super(_dataSource, _dbConnection, _table, indices);
    predictionColumns = _predictionColumns;
    //predictionExample = predictionColumns.getRow();
    indirection = _indirection;
    prediction = _prediction;



  }

  public int getNumPredictions() {

    return predictionColumns.length;

  }


  public void setDoublePrediction(double pred, int p) {
   predictionColumns[p].setDouble(pred, index);
  }

  public double getDoublePrediction(int p) {
    return predictionColumns[p].getDouble(index);
  }

  public void setIntPrediction(int pred, int p) {
      predictionColumns[p].setInt(pred, index);
  }

  public int getIntPrediction(int p) {
        return predictionColumns[p].getInt(index);
  }

  public void setFloatPrediction(float pred, int p) {
       predictionColumns[p].setFloat(pred, index);
  }

  public float getFloatPrediction(int p) {
    return predictionColumns[p].getFloat(index);
  }

  public void setShortPrediction(short pred, int p) {
   predictionColumns[p].setShort(pred, index);
  }

  public short getShortPrediction(int p) {
       return predictionColumns[p].getShort(index);
  }

  public void setLongPrediction(long pred, int p) {
   predictionColumns[p].setLong(pred, index);
  }

  public long getLongPrediction(int p) {
    return predictionColumns[p].getLong(index);
  }

  public void setStringPrediction(String pred, int p) {
   predictionColumns[p].setString(pred, index);
  }

  public String getStringPrediction(int p) {
      return predictionColumns[p].getString(index);
  }

  public void setCharsPrediction(char[] pred, int p) {
  predictionColumns[p].setChars(pred, index);
  }
  public char[] getCharsPrediction(int p) {
        return predictionColumns[p].getChars(index);
  }
  public void setCharPrediction(char pred, int p) {
    predictionColumns[p].setChar(pred, index);
  }

  public char getCharPrediction(int p) {
        return predictionColumns[p].getChar(index);
  }

  public void setBytesPrediction(byte[] pred, int p) {
    predictionColumns[p].setBytes(pred, index);
  }
  public byte[] getBytesPrediction(int p) {
        return predictionColumns[p].getBytes(index);
  }
  public void setBytePrediction(byte pred, int p) {
    predictionColumns[p].setByte(pred, index);
  }

  public byte getBytePrediction(int p) {
        return  predictionColumns[p].getByte(index);
  }

  public void setBooleanPrediction(boolean pred, int p) {
    predictionColumns[p].setBoolean(pred, index);
  }

  public boolean getBooleanPrediction(int p) {
        return  predictionColumns[p].getBoolean(index);
  }

  public void setObjectPrediction(Object pred, int p) {
    predictionColumns[p].setObject(pred, index);
  }

  public Object getObjectPrediction(int p) {
        return predictionColumns[p].getObject(index);
  }




  public void setIndex(int i) {

   // predictionExample.setIndex(i);
    index = subset[i];
  }



  public double getDouble(int i) {
    if (prediction[i])
              return predictionColumns[indirection[i]].getDouble(index);
    else
        return (double)dataSource.getNumericData(index, indirection[i]);

  }
  public String getString(int i) {
    if (prediction[i])
           return predictionColumns[indirection[i]].getString(index);
   else
     return dataSource.getTextData(index, indirection[i]);

  }
  public int getInt(int i) {
    if (prediction[i])
       return predictionColumns[indirection[i]].getInt(index);

   else
     return (int)dataSource.getNumericData(index, indirection[i]);

  }
  public float getFloat(int i) {
    if (prediction[i])
            return predictionColumns[indirection[i]].getFloat(index);
  else
      return (float)dataSource.getNumericData(index, indirection[i]);

  }

  public short getShort(int i) {
    if (prediction[i])
          return predictionColumns[indirection[i]].getShort(index);
    else
      return (short)dataSource.getNumericData(index, indirection[i]);

  }


  public long getLong(int i) {
    if (prediction[i])
          return predictionColumns[indirection[i]].getLong(index);
    else
     return (long)dataSource.getNumericData(index, indirection[i]);

  }


  public byte getByte(int i) {
    if (prediction[i])
          return predictionColumns[indirection[i]].getByte(index);
    else
     return dataSource.getTextData(index, indirection[i]).getBytes()[0];

  }


  public Object getObject(int i) {
    if (prediction[i])
          return predictionColumns[indirection[i]].getObject(index);
    else
     return (Object)dataSource.getObjectData(index, indirection[i]).toString();

  }


  public char getChar(int i) {
    if (prediction[i])
       return predictionColumns[indirection[i]].getChar(index);
 else
  return dataSource.getTextData(index, indirection[i]).toCharArray()[0];

  }
  public char[] getChars(int i) {
    if (prediction[i])
          return predictionColumns[indirection[i]].getChars(index);
    else
     return dataSource.getTextData(index, indirection[i]).toCharArray();

  }
  public byte[] getBytes(int i) {
    if (prediction[i])
          return predictionColumns[indirection[i]].getBytes(index);
    else
     return dataSource.getTextData(index, indirection[i]).getBytes();

  }
  public boolean getBoolean(int i) {
    if (prediction[i])
          return predictionColumns[indirection[i]].getBoolean(index);
    else
     return new Boolean(dataSource.getTextData(index, indirection[i])).booleanValue();

  }




}