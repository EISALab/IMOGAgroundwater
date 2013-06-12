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

public interface VHashMap {

  public int[] keys();

  public boolean containsKey(int pos);

  public int size();

  public VHashMap reorder(VIntIntHashMap newOrder);

  public VIntIntHashMap getSortedOrder();

  public VIntIntHashMap getSortedOrder(int begin, int end);

  public void insertObject(Object obj, int key);

  public void replaceObject(Object obj, int key);

}