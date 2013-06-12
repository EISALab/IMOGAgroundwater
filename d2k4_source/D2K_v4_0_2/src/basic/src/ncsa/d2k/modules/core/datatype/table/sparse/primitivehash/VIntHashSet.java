package ncsa.d2k.modules.core.datatype.table.sparse.primitivehash;

//==============
// Java Imports
//==============

import java.util.*;
import java.io.*;

//===============
// Other Imports
//===============

import gnu.trove.TIntHashSet;

/**
 * Title:        Sparse Table
 * Description:  Sparse Table projects will implement data structures compatible
 * to the interface tree of Table, for sparsely stored data.
 * Copyright:    Copyright (c) 2002
 * Company:      ncsa
 * @author vered goren
 * @version 1.0
 */

public class VIntHashSet
    extends TIntHashSet {

  //================
  // Constructor(s)
  //================

  public VIntHashSet() {
    this(0);
  }

  public VIntHashSet(int initialCapacity) {
    super(initialCapacity);
  }

  public VIntHashSet(int[] arr) {
    super(arr);
  }

  //================
  // Public Methods
  //================

  public int[] toArray() {

    int[] retVal = super.toArray();
    Arrays.sort(retVal);
    return retVal;
  }

  public VIntHashSet getSubset(int[] indices) {
    VIntHashSet retVal = new VIntHashSet(size());

    for (int i = 0; i < indices.length; i++)
      if (this.contains(indices[i]))
        retVal.add(indices[i]);

    return retVal;
  }

  /**
   * Returns a subset of this VIntHashSet with values in the range <code>begin
   * </code> through <code>begin + len </code>
   *
   * @param begin     lower border for values in the returned set
   * @param len       size of the range of the values in the returned set
   * @return          a VIntHashSet with values from this set that are in the
   *                  range <code>[begin, begin+len]</code>.
   */
  public VIntHashSet getSubset(int begin, int len) {
    //retrieving all the values in this set and sorting them
    int[] values = toArray();
    Arrays.sort(values);

    //finding the index of begin (or the index of the fist value greater than
    //begin, if begin is not in the set)
    int start = VHashService.findPlace(values, begin);
    //finding the index of begin+len (or the index of the fist value smaller than
    //begin+len, if begin+len is not in the set)
    int end = VHashService.findPlace(values, begin + len);

    VIntHashSet retVal = new VIntHashSet();

    //if all the vlaue are smaller than begin - return an empty set.
    if (start >= values.length)
      return retVal;
    //if all the values are smaller that begin+len, then the end index is the
    //last index of the array of values.
    if (end >= values.length)
      end = values.length - 1;

      //for each value from values[start] through values[end]
    for (int i = start; i <= end; i++)

      //add it to the returned vlaue.
      retVal.add(values[i]);

    return retVal;
  }

  /**
   * returns a VIntHashSet according to <code>newOrder</code> and this set.
   * Algorithm:  For each key i in <codE>newOrder</code>
   * if this set holds its mapped value, add key i to the returned vlaue.
   *
   * @param newOrder    an int to int hashmap that defines the returned value
       * @return            a VIntHashSet containing keys from <code>newOrder</code>
   *                    that their mapped values are in this set.
   */
  public VIntHashSet reorder(VIntIntHashMap newOrder) {
    VIntHashSet retVal = new VIntHashSet();
    reorder(newOrder, retVal);
    return retVal;
  }

  /**
   * returns a VIntHashSet  with sma evalues as this set, except in the range
   * <code>[begin, end]</code>. The modification is defined by <code>newOrder</code>.
   * Algorithm:  Copy this set (retVal). remove all values in the specified range. then,
       * for each key i in <codE>newOrder</code> if this set holds its mapped value,
   * add key i to retVal.
   *
   * @param newOrder    an int to int hashmap that defines the returned value
       * @return            a VIntHashSet containing keys from <code>newOrder</code>
   *                    that their mapped values are in this set.
   */
  public VIntHashSet reorder(VIntIntHashMap newOrder, int begin, int end) {
    VIntHashSet retVal = copy();
    VIntHashSet temp = getSubset(begin, end - begin + 1);
    retVal.removeAll(temp.toArray());
    reorder(newOrder, retVal);
    return retVal;

  }

  /**
   * Returns a deep copy of this set.
   */
  public VIntHashSet copy() {
    VIntHashSet retVal;
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(baos);
      oos.writeObject(this);
      byte buf[] = baos.toByteArray();
      oos.close();
      ByteArrayInputStream bais = new ByteArrayInputStream(buf);
      ObjectInputStream ois = new ObjectInputStream(bais);
      retVal = (VIntHashSet) ois.readObject();
      ois.close();
      return retVal;
    }
    catch (Exception e) {

      retVal = new VIntHashSet();

      retVal._free = _free;
      retVal._loadFactor = _loadFactor;
      retVal._maxSize = _maxSize;
      retVal._set = new int[_set.length];
      System.arraycopy(_set, 0, retVal._set, 0, _set.length);
      retVal._size = _size;
      retVal._states = new byte[_states.length];
      System.arraycopy(_states, 0, retVal._states, 0, _states.length);

      return retVal;
    }
  }

  /**
   * Modifes this set as follows:
   * removing <code>pos1</codE> and <codE>pos2</code> from this set.
       * If <code>pos1</code> was part of this set - adding <codE>pos2</codE> to it.
       * If <code>pos2</code> was part of this set - adding <codE>pos1</codE> to it.
   */
  public void swapRows(int pos1, int pos2) {
    boolean exist1 = remove(pos1);
    boolean exist2 = remove(pos2);

    if (exist1)
      add(pos2);
    if (exist2)
      add(pos2);
  }

  /**
   * Adds 1 to each item greater than or equals to <code>start</code>
   */
  public void increment(int start) {
    int[] items = getSubset(start, max()).toArray();

    removeAll(items);
    for (int i = 0; i < items.length; i++)
      add(items[i] + 1);

      //XIAOLEI
      //add(start);
  }

  /**
   * Substracts 1 to each item greater than or equal to
   * <code>start</code>.
   */
  public void decrement(int start) {
    int[] items = getSubset(start, max()).toArray();
    removeAll(items);

    if (items.length > 0) {

      int i = 0;

      if (items[0] == start)
        i = 1;

      for (; i < items.length; i++)
        add(items[i] - 1);
    }
  }

  public int max() {
    int[] arr = toArray();

    if (arr.length > 0)
      return arr[arr.length - 1];
    else
      return Integer.MIN_VALUE;
  }

  /**
   * gets the element at position <code>index</codE> if the items of this
   * set were to be arranged in a sorted int array.
   *
   * @param index   the porition of the returned value in a sorted array of the
   *                items of this set
   * @return        the item at index <codE>index</code> if the items of this
   *                set were arranged in a sorted array.
   */
  public int getAt(int index) {
    return toArray()[index];
  }

  //=================
  // Private Methods
  //=================

  /**
       * modifies <codE>set</codE> as follows: for each key i in <code>newOrder</code>
   * if this set holds it mapped value - add key i to <codE>set</code>
   *
   * @param newOrder    an int to int hashmap that defines the modification of
   *                    <codE>set</code>
   * @param set         an int set to be modified.
   */
  private void reorder(VIntIntHashMap newOrder, VIntHashSet set) {
    int[] oldVals = newOrder.keys();
    //if this set contains the value mapped to oldVal[i]
    //put oldVal[i] in the returned vlaue.
    for (int i = 0; i < oldVals.length; i++)
      if (contains(newOrder.get(oldVals[i])))
        set.add(oldVals[i]);
  }


}