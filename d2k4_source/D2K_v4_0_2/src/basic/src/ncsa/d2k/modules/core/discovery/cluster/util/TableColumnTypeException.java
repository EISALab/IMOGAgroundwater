package ncsa.d2k.modules.core.discovery.cluster.util;

//=============
//Java Imports
//=============

//===============
// Other Imports
//===============

import ncsa.d2k.modules.core.datatype.table.*;

/**
 *
 * <p>Title: TableColumnTypeException</p>
 * <p>Description: This exception is thrown when column types that cannot be
 * handled are encounterred in an input table.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: NCSA</p>
 * @author D. Searsmith
 * @version 1.0
 */
public class TableColumnTypeException extends Exception {

  //=============
  //Data Members
  //=============

  private int _coltype = -1;

  //===============
  //Constructor(s)
  //===============

  public TableColumnTypeException(int coltype, String msg) {
    this("Table column type is not supported: " + ColumnTypes.getTypeName(coltype) + " " + msg);
  }

  public TableColumnTypeException(String message) {
      super(message);
  }

}
