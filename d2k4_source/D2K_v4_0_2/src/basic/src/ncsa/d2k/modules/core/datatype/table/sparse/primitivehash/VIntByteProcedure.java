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

public interface VIntByteProcedure {
  /**
     * Executes this procedure. A false return value indicates that
     * the application executing this procedure should not invoke this
     * procedure again.
     *
     * @param a an <code>int</code> value
     * @param b a <code>byte</code> value
     * @return true if additional invocations of the procedure are
     * allowed.
     */
    public boolean execute(int a, byte b);

}