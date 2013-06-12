package ncsa.d2k.modules.core.datatype.table.sparse.primitivehash;

//===============
// Other Imports
//===============

import gnu.trove.TObjectIntHashMap;

//==============
// Java Imports
//==============
import java.io.*;

/**
 * Title:        Sparse Table
 * Description:  Sparse Table projects will implement data structures compatible
 * to the interface tree of Table, for sparsely stored data.
 * Copyright:    Copyright (c) 2002
 * Company:      ncsa
 * @author vered goren
 * @version 1.0
 */

public class VObjectIntHashMap
    extends TObjectIntHashMap {

  public VObjectIntHashMap() {
    super();
  }

  public VObjectIntHashMap(int initialCapacity) {
    super(initialCapacity);
  }

  public VObjectIntHashMap(int initialCapacity, float loadFactor) {
    super(initialCapacity, loadFactor);
  }

  //================
  // Public Methods
  //================

  public VObjectIntHashMap copy() {
    VObjectIntHashMap newMap;
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(baos);
      oos.writeObject(this);
      byte buf[] = baos.toByteArray();
      oos.close();
      ByteArrayInputStream bais = new ByteArrayInputStream(buf);
      ObjectInputStream ois = new ObjectInputStream(bais);
      newMap = (VObjectIntHashMap) ois.readObject();
      ois.close();
      return newMap;
    }
    catch (Exception e) {

      newMap = new VObjectIntHashMap();
      newMap._free = _free;
      newMap._loadFactor = _loadFactor;
      newMap._maxSize = _maxSize;
      newMap._size = _size;

      newMap._set = new Object[_set.length];
      System.arraycopy(_set, 0, newMap._set, 0, _set.length);

      //  newMap._states = new byte[_states.length];
//	  System.arraycopy(_states, 0, newMap._states, 0, _states.length);

      newMap._values = new int[_values.length];
      System.arraycopy(_values, 0, newMap._values, 0, _values.length);

      return newMap;
    }

  }

}