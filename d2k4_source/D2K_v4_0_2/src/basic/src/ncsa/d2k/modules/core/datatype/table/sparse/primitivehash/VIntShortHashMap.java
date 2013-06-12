package ncsa.d2k.modules.core.datatype.table.sparse.primitivehash;

//==============
// Java Imports
//==============

import java.io.*;
import java.util.*;

//===============
// Other Imports
//===============

import gnu.trove.*;
import gnu.trove.TIntHash;
import ncsa.d2k.modules.core.datatype.table.sparse.columns.SparseShortColumn;
import ncsa.d2k.modules.core.datatype.table.sparse.*;

/**
 * Title:        Sparse Table
 * Description:  Sparse Table projects will implement data structures compatible
 * to the interface tree of Table, for sparsely stored data.
 * Copyright:    Copyright (c) 2002
 * Company:      ncsa
 * @author vered goren
 * @version 1.0
 */

public class VIntShortHashMap
    extends TIntHash
    implements VHashMap {

  //==============
  // Data Members
  //==============

  /**
   * An Int to Short Hash Map: maps short values to int keys.
   */

  /** the values of the map */
  protected transient short[] _values;

  //================
  // Constructor(s)
  //================

  /**
   * Creates a new <code>VIntShortHashMap</code> instance with the default
   * capacity and load factor.
   */
  public VIntShortHashMap() {
    super();
  }

  /**
   * Creates a new <code>VIntShortHashMap</code> instance with a prime
   * capacity equal to or greater than <tt>initialCapacity</tt> and
   * with the default load factor.
   *
   * @param initialCapacity an <code>int</code> value
   */
  public VIntShortHashMap(int initialCapacity) {
    super(initialCapacity);
  }

  /**
   * Creates a new <code>VIntShortHashMap</code> instance with a prime
   * capacity equal to or greater than <tt>initialCapacity</tt> and
   * with the specified load factor.
   *
   * @param initialCapacity an <code>int</code> value
   * @param loadFactor a <code>float</code> value
   */
  public VIntShortHashMap(int initialCapacity, float loadFactor) {
    super(initialCapacity, loadFactor);
  }

  //================
  // Public Methods
  //================

  /**
   * Inserts a key/value pair into the map.
   *
   * @param key an <code>int</code> value
   * @param value an <code>short</code> value
   * @return the previous value associated with <tt>key</tt>,
   * or null if none was found.
   */
  public short put(int key, short value) {
    byte previousState;
    short previous = (short) 0;
    int index = insertionIndex(key);
    boolean isNewMapping = true;
    if (index < 0) {
      index = -index - 1;
      previous = _values[index];
      isNewMapping = false;
    }
    previousState = _states[index];
    _set[index] = key;
    _states[index] = FULL;
    _values[index] = value;
    if (isNewMapping) {
      postInsertHook(previousState == FREE);
    }

    return previous;
  }

  /**
   * retrieves the value for <tt>key</tt>
   *
   * @param key an <code>int</code> value
   * @return the value of <tt>key</tt> or null if no such mapping exists.
   */
  public short get(int key) {
    int index = index(key);
    return index < 0 ? (short) SparseDefaultValues.getDefaultInt() :
        _values[index];
  }

  /**
   * Empties the map.
   *
   */
  public void clear() {
    super.clear();
    int[] keys = _set;
    short[] vals = _values;
    byte[] states = _states;

    for (int i = keys.length; i-- > 0; ) {
      keys[i] = (int) 0;
      vals[i] = (short) 0;
      states[i] = FREE;
    }
  }

  /**
   * Deletes a key/value pair from the map.
   *
   * @param key an <code>int</code> value
   * @return a <code>short</code> value
   */
  public short remove(int key) {
    short prev = (short) SparseDefaultValues.getDefaultInt();
    int index = index(key);
    if (index >= 0) {
      prev = _values[index];
      removeAt(index); // clear key,state; adjust size
    }
    return prev;
  }

  /**
   * Compares this map with another map for equality of their stored
   * entries.
   *
   * @param other an <code>Object</code> value
   * @return a <code>boolean</code> value
   */
  public boolean equals(Object other) {
    if (! (other instanceof VIntShortHashMap)) {
      return false;
    }
    VIntShortHashMap that = (VIntShortHashMap) other;
    if (that.size() != this.size()) {
      return false;
    }
    return forEachEntry(new EqProcedure(that));
  }

  /**
   * Returns the values of the map.
   *
   * @return a <code>Collection</code> value
   */
  public short[] getValues() {
    short[] vals = new short[size()];
    short[] v = _values;
    byte[] states = _states;

    for (int i = v.length, j = 0; i-- > 0; ) {
      if (states[i] == FULL) {
        vals[j++] = v[i];
      }
    }
    return vals;
  }

  /**
   * returns the keys of the map.
   *
   * @return a <code>Set</code> value
   */
  public int[] keys() {
    int[] keys = new int[size()];
    int[] k = _set;
    byte[] states = _states;

    for (int i = k.length, j = 0; i-- > 0; ) {
      if (states[i] == FULL) {
        keys[j++] = k[i];
      }
    }
    return keys;
  }

  /**
   * checks for the presence of <tt>val</tt> in the values of the map.
   *
   * @param val an <code>short</code> value
   * @return a <code>boolean</code> value
   */
  public boolean containsValue(short val) {
    byte[] states = _states;
    short[] vals = _values;

    for (int i = vals.length; i-- > 0; ) {
      if (states[i] == FULL && val == vals[i]) {
        return true;
      }
    }
    return false;
  }

  /**
   * checks for the present of <tt>key</tt> in the keys of the map.
   *
   * @param key an <code>int</code> value
   * @return a <code>boolean</code> value
   */
  public boolean containsKey(int key) {
    return contains(key);
  }

  /**
   * Executes <tt>procedure</tt> for each key in the map.
   *
   * @param procedure a <code>TIntProcedure</code> value
   * @return false if the loop over the keys terminated because
   * the procedure returned false for some key.
   */
  public boolean forEachKey(TIntProcedure procedure) {
    return forEach(procedure);
  }

  /**
   * Executes <tt>procedure</tt> for each value in the map.
   *
   * @param procedure a <code>TShortProcedure</code> value
   * @return false if the loop over the values terminated because
   * the procedure returned false for some value.
   */
  public boolean forEachValue(VShortProcedure procedure) {
    byte[] states = _states;
    short[] values = _values;
    for (int i = values.length; i-- > 0; ) {
      if (states[i] == FULL && !procedure.execute(values[i])) {
        return false;
      }
    }
    return true;
  }

  /**
   * Executes <tt>procedure</tt> for each key/value entry in the
   * map.
   *
   * @param procedure a <code>VIntShortProcedure</code> value
   * @return false if the loop over the entries terminated because
   * the procedure returned false for some entry.
   */
  public boolean forEachEntry(VIntShortProcedure procedure) {
    byte[] states = _states;
    int[] keys = _set;
    short[] values = _values;
    for (int i = keys.length; i-- > 0; ) {
      if (states[i] == FULL && !procedure.execute(keys[i], values[i])) {
        return false;
      }
    }
    return true;
  }

  /**
   * Retains only those entries in the map for which the procedure
   * returns a true value.
   *
   * @param procedure determines which entries to keep
   * @return true if the map was modified.
   */
  public boolean retainEntries(VIntShortProcedure procedure) {
    boolean modified = false;
    byte[] states = _states;
    int[] keys = _set;
    short[] values = _values;
    for (int i = keys.length; i-- > 0; ) {
      if (states[i] == FULL && !procedure.execute(keys[i], values[i])) {
        removeAt(i);
        modified = true;
      }
    }
    return modified;
  }

  /**
   * Transform the values in this map using <tt>function</tt>.
   *
   * @param function a <code>TShortFunction</code> value
   */
  public void transformValues(VShortFunction function) {
    byte[] states = _states;
    short[] values = _values;
    for (int i = values.length; i-- > 0; ) {
      if (states[i] == FULL) {
        values[i] = function.execute(values[i]);
      }
    }
  }

  /**
   * Increments the primitive value mapped to key by 1
   *
   * @param key the key of the value to increment
   * @return true if a mapping was found and modified.
   */
  public boolean increment(int key) {
    return adjustValue(key, (short) 1);
  }

  /**
   * Adjusts the primitive value mapped to key.
   *
   * @param key the key of the value to increment
   * @param amount the amount to adjust the value by.
   * @return true if a mapping was found and modified.
   */
  public boolean adjustValue(int key, float amount) {
    int index = index(key);
    if (index < 0) {
      return false;
    }
    else {
      _values[index] += amount;
      return true;
    }
  }

  /**
   * Return a deep copy of this hash map.
   */
  public VIntShortHashMap copy() {
    VIntShortHashMap retVal;
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(baos);
      oos.writeObject(this);
      byte buf[] = baos.toByteArray();
      oos.close();
      ByteArrayInputStream bais = new ByteArrayInputStream(buf);
      ObjectInputStream ois = new ObjectInputStream(bais);
      retVal = (VIntShortHashMap) ois.readObject();
      ois.close();
      return retVal;
    }
    catch (Exception e) {

      retVal = new VIntShortHashMap();

      retVal._free = _free;
      retVal._loadFactor = _loadFactor;
      retVal._maxSize = _maxSize;
      retVal._size = _size;

      retVal._set = new int[_set.length];
      System.arraycopy(_set, 0, retVal._set, 0, _set.length);

      retVal._states = new byte[_states.length];
      System.arraycopy(_states, 0, retVal._states, 0, _states.length);

      retVal._values = new short[_values.length];
      System.arraycopy(_values, 0, retVal._values, 0, _values.length);

      return retVal;
    }
  }

  /**
   * Returns a new VIntShortHashMap with reordered mapping as defined by
   * <code>newOrder</code>
   *
   * @param newOrder  an int to int hashmap that defines the new order:
       *                  for each pair (key, val) in <code>newOrder</code> the value
   *                  that was mapped to val will be mapped to key in the
   *                  returned value.
   * @return          a VIntShortHashMap with the same values as this one,
   *                  reordered.
   */
  public VHashMap reorder(VIntIntHashMap newOrder) {

    //copying the map, as it is possible that newOrder does not hold all keys
    //in this map.
    VIntShortHashMap retVal = copy();

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
   * <codE>end</cdoe>, sorted.
   *
   * @param begin    key number from which to begin retrieving of values
   * @param end      greatest key number from which to retrieve value.
   * @return         a sorted short array with the values mapped to keys <code>begim
   *                 </code> through <codE>end</cdoe>.
   */
  public short[] getValuesInRange(int begin, int end) {
    if (end < begin) {
      short[] retVal = {};
      return retVal;
    }
    int[] keysInRange = VHashService.getIndicesInRange(begin, end, this);
    if (keysInRange == null)
      return null;

    short[] values = new short[keysInRange.length];
    for (int i = 0; i < keysInRange.length; i++)
      values[i] = get(keysInRange[i]);

    Arrays.sort(values);
    return values;
  }

  /**
   * Returns an int to int hashmap that represent a sorted order for the values
   *  of this map in the range <code>begin</code> through <code>end</code>.
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
    short[] values = getValuesInRange(begin, end);

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

    short[] values = getValues();
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
   * @return       a VIntShortHashMap with values and keys from this map, s.t.
       *               keys' range is <code>start</cdoe> through <code>start+len</code>
   */
  public VHashMap getSubset(int start, int len) {
    VIntShortHashMap retVal = new VIntShortHashMap(len);
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
      short removed = remove(keysInRange[i]);
      put(keysInRange[i] + 1, removed);
    }
    //putting the new object in key.
    if (obj != null)
      put(key, SparseShortColumn.toShort(obj));
  }

  public void replaceObject(Object obj, int key) {
    put(key, SparseShortColumn.toShort(obj));
  }

  //===================
  // Protected Methods
  //===================

  /**
   * initializes the hashtable to a prime capacity which is at least
   * <tt>initialCapacity + 1</tt>.
   *
   * @param initialCapacity an <code>int</code> value
   * @return the actual capacity chosen
   */
  protected int setUp(int initialCapacity) {
    int capacity;

    capacity = super.setUp(initialCapacity);
    _values = new short[capacity];
    return capacity;
  }


  /**
   * rehashes the map to the new capacity.
   *
   * @param newCapacity an <code>int</code> value
   */
  protected void rehash(int newCapacity) {
    int oldCapacity = _set.length;
    int oldKeys[] = _set;
    short oldVals[] = _values;
    byte oldStates[] = _states;

    _set = new int[newCapacity];
    _values = new short[newCapacity];
    _states = new byte[newCapacity];

    for (int i = oldCapacity; i-- > 0; ) {
      if (oldStates[i] == FULL) {
        int o = oldKeys[i];
        int index = insertionIndex(o);
        _set[index] = o;
        _values[index] = oldVals[i];
        _states[index] = FULL;
      }
    }
  }

  /**
   * removes the mapping at <tt>index</tt> from the map.
   *
   * @param index an <code>int</code> value
   */
  protected void removeAt(int index) {
    super.removeAt(index); // clear key, state; adjust size
    _values[index] = (short) 0;
  }

  //=================
  // Private Methods
  //=================

  private void writeObject(ObjectOutputStream stream) throws IOException {
    stream.defaultWriteObject();

    // number of entries
    stream.writeInt(_size);

    /**
         * TODO: SOLVE NON-PUBLIC CLASS PROBLEM WITH  gnu.trove.SerializationProcedure
     */
    /*        VSerializationProcedure writeProcedure = new VSerializationProcedure(stream);
            if (! forEachEntry(writeProcedure)) {
                throw writeProcedure.exception;
            }*/
  }

  private void readObject(ObjectInputStream stream) throws IOException,
      ClassNotFoundException {
    stream.defaultReadObject();

    int size = stream.readInt();
    setUp(size);
    while (size-- > 0) {
      int key = stream.readInt();
      short val = stream.readShort();
      put(key, val);
    }
  }


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
  private VIntIntHashMap getSortedOrder(int[] validKeys, short[] values) {

    //will hold the new order to be sorted according to.
    int[] newOrder = new int[validKeys.length];

    //flags associated with newOrder
    boolean[] ocuupiedIndices = new boolean[validKeys.length];

    short currVal; //current value for which its place is searched

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
  private int getNewKey(short currVal, short[] values, int key,
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


  //===============
  // Inner Classes
  //===============

  private static final class EqProcedure
      implements VIntShortProcedure {
    private final VIntShortHashMap _otherMap;

    EqProcedure(VIntShortHashMap otherMap) {
      _otherMap = otherMap;
    }

    public final boolean execute(int key, short value) {
      int index = _otherMap.index(key);
      if (index >= 0 && eq(value, _otherMap.get(key))) {
        return true;
      }
      return false;
    }

    /**
     * Compare two floats for equality.
     */
    private final boolean eq(short v1, short v2) {
      return v1 == v2;
    }

  }

}