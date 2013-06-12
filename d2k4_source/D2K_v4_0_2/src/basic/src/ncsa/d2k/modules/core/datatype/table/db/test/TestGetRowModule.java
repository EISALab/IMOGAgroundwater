package ncsa.d2k.modules.core.datatype.table.db.test;

import ncsa.d2k.core.modules.ComputeModule;
import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.core.modules.CustomModuleEditor;
import ncsa.d2k.core.pipes.Pipe;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.db.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class TestGetRowModule extends ComputeModule {
  public TestGetRowModule() {
  }
  public String getModuleInfo() {
    return "This module tests the getRow method of a Table.\n" +
        "It retrieves a Row object from the input Table and iterates"
       + " over all the items in the table";
  }


  public String getModuleName() {
    return "Test getRow Module";
  }


  public String getInputInfo(int parm1) {
    switch(parm1){
      case 0: return "Table to be tested";

      default: return "no such input";
    }
  }


  public String getInputName(int parm1) {
    switch(parm1){
      case 0: return "Table";

      default: return "no such input";
    }

  }



  public String getOutputInfo(int parm1) {
    switch(parm1){
      case 0: return "Table";

      default: return "no such input";
    }

  }


  public String getOutputName(int parm1) {
    switch(parm1){
       case 0: return "Table";

       default: return "no such input";
     }

  }





  public String[] getInputTypes() {
    String[] retVal = new String[1];
    retVal[0] = "ncsa.d2k.modules.core.datatype.table.Table";
    return retVal;
  }


  public String[] getOutputTypes() {
    String[] retVal = new String[1];
   retVal[0] = "ncsa.d2k.modules.core.datatype.table.Table";
   return retVal;

  }


  public void doit(){
    Table table = (Table)this.pullInput(0);
    System.out.println(table.getClass().getName());
  //  ((DBTable)table).debugging();
    Row row = table.getRow();

    int numColumns = table.getNumColumns();
    int numRows = table.getNumRows();

    System.out.println("Table type: " + table.getClass().getName());
    System.out.println("\nPrinting content of the table. accessing via row\n\n");

    for (int i=0; i<numRows; i++){
      row.setIndex(i);
      System.out.println("content of row no. " + i);
      for (int j=0; j<numColumns; j++)
        System.out.print(row.getString(j) + "\t");

      System.out.println();
    }//for i
int inputs, outputs=0;

    if(row instanceof Example){
      Example ex = (Example) row;
      inputs = ((ExampleTable)table).getNumInputFeatures();
      outputs = ((ExampleTable)table).getNumOutputFeatures();
      System.out.println("\nthis row is an Example. going over the input features andoutput features.");
      for (int i=0; i<numRows; i++){
        ex.setIndex(i);
        System.out.println("\ncontent of input features of row no. " + i);
        for (int j=0; j<inputs; j++)
          System.out.print(ex.getInputString(j) + "\t");
        System.out.println();
        System.out.println("content of output features of row no. " + i);
        for (int j=0; j<outputs; j++)
          System.out.print(ex.getOutputString(j) + "\t");
        System.out.println();
      }//for i

    }//indtance of Example.

    if (row instanceof PredictionExample){
      PredictionExample pEx = (PredictionExample)row;
      System.out.println("\nthis row is a PredictionExample. setting values in prediction columns");
      //assuming number of prediction features equals to number fo otput features.
      for (int i=0; i<numRows; i++){
        System.out.println("\npopulating row # " + i + " of the prediction columns");
        pEx.setIndex(i);
        for (int j=0; j<outputs; j++){
          switch(table.getColumnType(((ExampleTable)table).getOutputFeatures()[j])){

            case ColumnTypes.BOOLEAN: pEx.setBooleanPrediction(pEx.getOutputBoolean(j), j);
              break;
            case ColumnTypes.BYTE: pEx.setBytePrediction(pEx.getOutputByte(j), j);
              break;
            case ColumnTypes.BYTE_ARRAY: pEx.setBytesPrediction(pEx.getOutputBytes(j), j);
              break;

            case ColumnTypes.DOUBLE: pEx.setDoublePrediction(pEx.getOutputDouble(j), j);
              break;
            case ColumnTypes.CHAR: pEx.setCharPrediction(pEx.getOutputChar(j), j);
              break;
            case ColumnTypes.CHAR_ARRAY: pEx.setCharsPrediction(pEx.getOutputChars(j), j);
              break;


            case ColumnTypes.INTEGER: pEx.setIntPrediction(pEx.getOutputInt(j), j);
              break;
            case ColumnTypes.FLOAT: pEx.setFloatPrediction(pEx.getOutputFloat(j), j);
              break;
            case ColumnTypes.SHORT: pEx.setShortPrediction(pEx.getOutputShort(j), j);
              break;

            case ColumnTypes.LONG: pEx.setLongPrediction(pEx.getOutputLong(j), j);
              break;
            case ColumnTypes.OBJECT: pEx.setObjectPrediction(pEx.getOutputObject(j), j);
              break;
            case ColumnTypes.STRING: pEx.setStringPrediction(pEx.getOutputString(j), j);
              break;

          }//switch
        }//for j

    }//for i

    }//instance of prediction example

    pushOutput(table, 0);

  }//doit


}