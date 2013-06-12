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

public class TestGetSubsetModule extends ComputeModule {

  public String getModuleInfo() {
    return "This module tests the getSubset method of a Table.\n" +
        "It retrieves a SubsetTable from a Table and pushes out the subsettable " ;

  }


  public String getModuleName() {
    return "Test getSubset Module";
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
      case 0: return "Subset Table";

      default: return "no such output";
    }

  }


  public String getOutputName(int parm1) {
    switch(parm1){
       case 0: return "Subset Table";

       default: return "no such output";
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
    int begin, len;
    begin = (int) (Math.random() * table.getNumRows());
    len = (int)(Math.random() * (table.getNumRows() - begin));
   Table sTable = table.getSubset(begin, len);

    System.out.println("Test get subset modules:");
    System.out.println("subsetting the table from row no. " + begin);
    System.out.println("number of rows in the set:" + len);

    pushOutput(sTable, 0);




  }//doit


}