package ncsa.d2k.modules.core.datatype.table.sparse.primitivehash;

//==============
// Java Imports
//==============
import java.util.*;
import java.io.*;

//===============
// Other Imports
//===============
import gnu.trove.TIntObjectHashMap;

/**
 * Title:        Sparse Table
 * Description:  Sparse Table projects will implement data structures compatible
 * to the interface tree of Table, for sparsely stored data.
 * Copyright:    Copyright (c) 2002
 * Company:      ncsa
 * @author vered goren
 * @version 1.0
 */

public class VIntObjectHashMap
    extends TIntObjectHashMap
    implements VHashMap {

  //================
  // Constructor(s)
  //================

  /**
   * Creates a new <code>VIntObjectHashMap</code> instance with the default
   * capacity and load factor.
   */
  public VIntObjectHashMap() {
    super();
  }

  /**
   * Creates a new <code>VIntObjectHashMap</code> instance with a prime
   * capacity equal to or greater than <tt>initialCapacity</tt> and
   * with the default load factor.
   *
   * @param initialCapacity an <code>int</code> value
   */
  public VIntObjectHashMap(int initialCapacity) {
    super(initialCapacity);
  }

  /**
   * Creates a new <code>VIntObjectHashMap</code> instance with a prime
   * capacity equal to or greater than <tt>initialCapacity</tt> and
   * with the specified load factor.
   *
   * @param initialCapacity an <code>int</code> value
   * @param loadFactor a <code>float</code> value
   */
  public VIntObjectHashMap(int initialCapacity, float loadFactor) {
    super(initialCapacity, loadFactor);
  }

  //================
  // Public Methods
  //================

  /**
   * Returns a copy of this hashmap
   * The object that this hashmap holds must have a constractor that recieves
   * an Object as aparameter.
   * @return a VIntObjectHashMap
   */

  public VIntObjectHashMap copy() {
    VIntObjectHashMap retVal;
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(baos);
      oos.writeObject(this);
      byte buf[] = baos.toByteArray();
      oos.close();
      ByteArrayInputStream bais = new ByteArrayInputStream(buf);
      ObjectInputStream ois = new ObjectInputStream(bais);
      retVal = (VIntObjectHashMap) ois.readObject();
      ois.close();
      return retVal;
    }
    catch (Exception e) {

      //performing a deep copy
      retVal = new VIntObjectHashMap();

      retVal._free = _free;
      retVal._loadFactor = _loadFactor;
      retVal._maxSize = _maxSize;
      retVal._size = _size;

      int i;

      retVal._set = new int[_set.length];
      System.arraycopy(_set, 0, retVal._set, 0, _set.length);

      retVal._states = new byte[_states.length];
      System.arraycopy(_states, 0, retVal._states, 0, _states.length);

      retVal._values = new Object[_values.length];
      System.arraycopy(_values, 0, retVal._values, 0, _values.length);

      return retVal;
    }
  }

  /**
   * Returns a new VIntObjectHashMap with reordered mapping as defined by
   * <code>newOrder</code>
   *
   * @param newOrder  an int to int hashmap that defines the new order:
       *                  for each pair (key, val) in <code>newOrder</code> the value
   *                  that was mapped to val will be mapped to key in the
   *                  returned value.
   * @return          a VIntObjectHashMap with the same values as this one,
   *                  reordered.
   */
  public VHashMap reorder(VIntIntHashMap newOrder) {

    //copying the map, as it is possible that newOrder does not hold all keys
    //in this map.
    VIntObjectHashMap retVal = copy();

    //for each key in the newOrder map
    int[] oldKeys = newOrder.keys();
    for (int i = 0; i < oldKeys.length; i++) {
      //removing its mapping in the returned value
      retVal.remove(oldKeys[i]);
      //if this map contains the key that is mapped to oldKeys[i] -
      //put its value mapped to oldKeys[i] in the returned value.
      if (contains(newOrder.get(oldKeys[i])))
        retVal.put(oldKeys[i], get(newOrder.get(oldKeys[i])));
    }

    //values in newOrder, that are not in the keys of new order
    //are keys in retVal that their values will be moved to another row
    //but are not going to be set to a new value.
    //those keys should be taken off retVal.

    VIntHashSet discard = new VIntHashSet(newOrder.getValues());
    discard.removeAll(newOrder.keys());
    int[] toRemove = discard.toArray();

    for (int i = 0; i < toRemove.length; i++)
      retVal.remove(toRemove[i]);
    return retVal;
  }

  /**
   * Returns the values mapped to keys between <codE>begin</code> through
   * <codE>end</code>, sorted.
   *
   *
   * This method can be used safely if the map holds object that are comparable
   * and that are of the same type.
   *
   * @param begin    key number from which to begin retrieving of values
   * @param end      greatest key number from which to retrieve value.
   * @return         a sorted Object array with the values mapped to keys <code>begim
   *                 </code> through <codE>end</cdoe>.
   */
  public Object[] getValuesInRange(int begin, int end) {

    if (end < begin) {
      Object[] retVal = {};
      return retVal;
    }
    int[] keysInRange = VHashService.getIndicesInRange(begin, end, this);
    if (keysInRange == null)
      return null;

    Object[] values = new Object[keysInRange.length];
    for (int i = 0; i < keysInRange.length; i++)
      values[i] = get(keysInRange[i]);

    Arrays.sort(values);
    return values;
  }

  /**
   * Returns an int to int hashmap that represent a sorted order for the values
   *  of this map in the range <code>begin</code> through <code>end</code>.
   *
   * The object in this map should be of the same type and should implement
   * Comparable, or else a calss cast exception will be thrown.
   *
   * @param begin     key no. from which to start retrieving the new order
   * @param end       the last key in the section from which to retrieve the new order.
       * @return            a VIntIntHashMap with valid keys from the specified section
       *                    s.t. for each pair of keys (i,j) ley (x,y) be their maped
   *                    values.  if (i<=j) then the value that is mapped x
   *                    smaller than or equal to the value that is mapped to y.
   */
  public VIntIntHashMap getSortedOrder(int begin, int end) {

    if (end < begin) {
      return new VIntIntHashMap(0);
    }

    //sorting the valid row numbers
    int[] validKeys = VHashService.getIndicesInRange(begin, end, this);

    //sorting the values
    Object[] values = getValuesInRange(begin, end);

    return getSortedOrder(validKeys, values);
  }

  /**
   * Returns an int to int hashmap that represent a sorted order for the values
   *  of this map.
   *
       *                    s.t. for each pair of keys (i,j) ley (x,y) be their maped
   *                    values in the returned value.
   *                    if (i<=j) then the value that is mapped x
   *                    smaller than or equal to the value that is mapped to y.
   */
  public VIntIntHashMap getSortedOrder() {

    int[] validKeys = VHashService.getIndices(this);

    Object[] values = getValues();
    Arrays.sort(values);

    return getSortedOrder(validKeys, values);
  }

  /**
   * returns a subset of this map with values that are mpped to keys <code>
   * start</code> through <codE>start+len</cdoe>.
   *
   * @param start  key number to start retrieving subset from
   * @param len    number of consequetive keys to retrieve their values into
   *               the subset
   * @return       a VIntObjectHashMap with values and keys from this map, s.t.
       *               keys' range is <code>start</cdoe> through <code>start+len</code>
   */
  public VHashMap getSubset(int start, int len) {
    VIntObjectHashMap retVal = new VIntObjectHashMap(len);
    //XIAOLEI
    //int[] validKeys = VHashService.getIndicesInRange(start, start+len, this);
    int[] validKeys = VHashService.getIndicesInRange(start, start + len - 1, this);
    for (int i = 0; i < validKeys.length; i++)

      //XIAOLEI
      //retVal.put(validKeys[i], get(validKeys[i]));
      retVal.put(validKeys[i] - start, get(validKeys[i]));
    return retVal;
  }

  /**
   * Inserts <codE>obj</codE> to be mapped to key <code>key<code>.
   * All values mapped to keys <code>key</code> and on will be mapped to
   * a key greater in one.
   *
   * @param obj    an object to be inserted into the map.
   * @param key    the insertion key
   */
  public void insertObject(Object obj, int key) {

    //moving all elements mapped to key through the maximal key
    //to be mapped to a key greater in 1.
    int max = VHashService.getMaxKey(this);

    int[] keysInRange = VHashService.getIndicesInRange(key, max, this);
    for (int i = keysInRange.length - 1; i >= 0; i--) {

      Object removed = remove(keysInRange[i]);
      put(keysInRange[i] + 1, removed);
    }
    //putting the new object in key.

    if (obj != null)
      put(key, obj);
  }

  public void replaceObject(Object obj, int key) {
    put(key, obj);
  }

  //=================
  // Private Methods
  //=================

  /**
       * Returns an int to int hashmap that represent the sorted order of the values
   * in <code>values</cdoe> through the keys in <code>validKeys</cdoe>.
   *
       * @param validKeys     keys from this map that a sorted order for their values
   *                      should be returned, sorted.
       * @param end           values mapped to items in <code>validKeys</cdoe>, sorted.
   * @return            a VIntIntHashMap with valid keys from <code>validKeys</code>
       *                    s.t. for each pair of keys (i,j) ley (x,y) be their maped
   *                    values in the returned value.  if (i<=j) then the value
   *                    that is mapped x is smaller than or equal to the value
   *                    that is mapped to y.
   */
  private VIntIntHashMap getSortedOrder(int[] validKeys, Object[] values) {

    //will hold the new order to be sorted according to.
    int[] newOrder = new int[validKeys.length];

    //flags associated with newOrder
    boolean[] ocuupiedIndices = new boolean[validKeys.length];

    Object currVal; //current value for which its place is searched

    //for each valid row validRows[i]
    for (int i = 0; i < validKeys.length; i++) {

      currVal = get(validKeys[i]);

      //finding the index of its mapped String
      int newKey = Arrays.binarySearch(values, currVal);

      //because binarySearch can return the same index for items that are identical
      //checking for this option too.
      if (ocuupiedIndices[newKey])
        newKey = getNewKey(currVal, values, newKey, ocuupiedIndices);

      ocuupiedIndices[newKey] = true; //marking the flag

      //validRows[i] will be swapped with validRows[newRow] by reorderRows.
      newOrder[newKey] = validKeys[i];

    } //end of for

    //creating a map between the old order and the new order.
    return VHashService.getMappedOrder(validKeys, newOrder);
  }

  /**
   * returns a new index for a new key number for the item <code>currVal</code>
       * the index is the first index i to be found  in <code>values</code> such that
   * <code>currVal equals values[i] and occupiedIndices[i] == false</code>.
   * this index i is then used in the array validKeys by getSortedOrder.
   *
   * @param currVal     the current value that getSortedOrder method is looking
   *                    for its new key number in the map.
   * @param values      values from this map, sorted.
   * @param key         index such that <code>values[key] == currVal</code> and also
   *                    <code>occupiedIndices[row] == true</code>.
   * @param occupiedIndices   a flag array
   * @return            index i such that currVal == values[i] and
   *                    ccupiedIndices[i] == false
   */
  private int getNewKey(Object currVal, Object[] values, int key,
                        boolean[] ocuupiedIndices) {
    int retVal = -1;
    //searching values at indices smaller than key
    for (int i = key - 1; i >= 0 && values[i] == currVal && retVal < 0; i--)
      if (!ocuupiedIndices[i])
        retVal = i;

        //searching values at indices greater than key
    for (int i = key + 1;
         retVal < 0 && i < values.length && values[i] == currVal; i++)
      if (!ocuupiedIndices[i])
        retVal = i;

    return retVal;
  }


}