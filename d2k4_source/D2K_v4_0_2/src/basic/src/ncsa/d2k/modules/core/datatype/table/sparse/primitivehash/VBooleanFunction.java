package ncsa.d2k.modules.core.datatype.table.sparse.primitivehash;

/**
 * Title:        VBooleanFunction
 * Description:  Sparse Table projects will implement data structures compatible
 * to the interface tree of Table, for sparsely stored data.
 * Copyright:    Copyright (c) 2002
 * Company:      ncsa
 * @author vered goren
 * @version 1.0
 */

public interface VBooleanFunction {

  /**
     * Execute this function with <tt>value</tt>
     *
     * @param value an <code>boolean</code> input
     * @return an <code>boolean</code> result
     */
    public boolean execute(boolean value);
}