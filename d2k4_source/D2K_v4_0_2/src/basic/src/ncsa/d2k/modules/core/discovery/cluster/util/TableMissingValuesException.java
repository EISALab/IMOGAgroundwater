package ncsa.d2k.modules.core.discovery.cluster.util;

//=============
//Java Imports
//=============

//===============
// Other Imports
//===============

/**
 *
 * <p>Title: TableMissingValuesException</p>
 * <p>Description: This exception is thrown when missing values
 * are encounterred in an input table but the module cannot handle them.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: NCSA</p>
 * @author D. Searsmith
 * @version 1.0
 */
public class TableMissingValuesException extends Exception {

  //=============
  //Data Members
  //=============

  //===============
  //Constructor(s)
  //===============

  public TableMissingValuesException(String msg) {
    super(" Table contains missing values -- " + msg);
  }

}
