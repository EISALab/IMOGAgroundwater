package ncsa.d2k.modules.core.datatype.table.sparse.primitivehash;

//===============
// Other Imports
//===============

import gnu.trove.TObjectProcedure;
import gnu.trove.TIntHashSet;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author
 * @version 1.0
 */

public class IndicesRemover
    implements TObjectProcedure {

  //==============
  // Data Members
  //==============

  private int[] idx;

  //================
  // Constructor(s)
  //================

  public IndicesRemover() {
  }

//================
// Public Methods
//================

  public IndicesRemover(int[] indices) {
    idx = indices;
  }

  public boolean execute(Object object) {
    ( (TIntHashSet) object).removeAll(idx);
    return true;
  }

}