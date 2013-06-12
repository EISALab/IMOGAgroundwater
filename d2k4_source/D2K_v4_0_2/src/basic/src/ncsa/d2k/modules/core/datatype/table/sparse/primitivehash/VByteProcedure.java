//package ncsa.d2k.modules.projects.vered.sparse.primitivehash;
package ncsa.d2k.modules.core.datatype.table.sparse.primitivehash;

/**
 * Title:        Sparse Table
 * Description:  Sparse Table projects will implement data structures compatible to the interface tree of Table, for sparsely stored data.
 * Copyright:    Copyright (c) 2002
 * Company:      ncsa
 * @author vered goren
 * @version 1.0
 */

public interface VByteProcedure {

/**
     * Executes this procedure. A false return value indicates that
     * the application executing this procedure should not invoke this
     * procedure again.
     *
     * @param value a value of type <code>byte</code>
     * @return true if additional invocations of the procedure are
     * allowed.
     */
    public boolean execute(byte value);

}