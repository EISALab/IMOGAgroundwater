package ncsa.d2k.modules.core.datatype.table.db;

/**
 * <p>Title: CacheMissException </p>
 * <p>Description: Detects when a cache miss occurs </p>
 * <p>Copyright: NCSA (c) 2002</p>
 * <p>Company: </p>
 * @author Sameer Mathur, David Clutter
 * @version 1.0
 */

public class CacheMissException extends Exception {

  public CacheMissException() {
      super("A Cache Miss has occured");
  }

  public CacheMissException(String message) {
      super(message);
  }

}