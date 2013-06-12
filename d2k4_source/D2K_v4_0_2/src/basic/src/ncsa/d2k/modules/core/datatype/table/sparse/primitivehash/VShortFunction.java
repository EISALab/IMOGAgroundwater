//package ncsa.d2k.modules.projects.vered.sparse.primitivehash;
package ncsa.d2k.modules.core.datatype.table.sparse.primitivehash;

/**
 * Title:        Sparse Table
 * Description:  Sparse Table projects will implement data structures compatible
 * to the interface tree of Table, for sparsely stored data.
 * Copyright:    Copyright (c) 2002
 * Company:      ncsa
 * @author vered goren
 * @version 1.0
 */

public interface VShortFunction {

  /**
   * Execute this function with <tt>value</tt>
   *
   * @param value an <code>short</code> input
   * @return an <code>short</code> result
   */
  public short execute(short value);
}