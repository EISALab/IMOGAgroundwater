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
import ncsa.d2k.modules.core.datatype.table.sparse.columns.SparseBooleanColumn;
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

/**
 * an implementation of an int to boolean hash map.
 */
public class VIntBooleanHashMap
    extends TIntHash
    implements Serializable, VHashMap {

  //==============
  // Data Members
  //==============

  /** the values of the map */
  protected transient boolean[] _values;

  //================
  // Constructor(s)
  //================

  /**
   * Creates a new <code>VIntBooleanHashMap</code> instance with the default
   * capacity and load factor.
   */
  public VIntBooleanHashMap() {
    super();
  }

  /**
   * Creates a new <code>TIntBooleanHashMap</code> instance with a prime
   * capacity equal to or greater than <tt>initialCapacity</tt> and
   * with the default load factor.
   *
   * @param initialCapacity an <code>int</code> value
   */
  public VIntBooleanHashMap(int initialCapacity) {
    super(initialCapacity);
  }

  /**
   * Creates a new <code>VIntBooleanHashMap</code> instance with a prime
   * capacity equal to or greater than <tt>initialCapacity</tt> and
   * with the specified load factor.
   *
   * @param initialCapacity an <code>int</code> value
   * @param loadFactor a <code>float</code> value
   */
  public VIntBooleanHashMap(int initialCapacity, float loadFactor) {
    super(initialCapacity, loadFactor);
  }


  //================
  // Public Methods
  //================

  /**
   * Inserts a key/value pair into the map.
   *
   * @param key an <code>int</code> value
   * @param value an <code>boolean</code> value
   * @return the previous value associated with <tt>key</tt>,
   * or null if none was found.
   */
  public boolean put(int key, boolean value) {
    byte previousState;
    boolean previous = false;
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
   * @return the value of <tt>key</tt> or false if no such mapping exists.
   */
  public boolean get(int key) {
    int index = index(key);
    return index < 0 ? SparseDefaultValues.getDefaultBoolean() : _values[index];
  }

  /**
   * Empties the map.
   *
   */
  public void clear() {
    super.clear();
    int[] keys = _set;
    boolean[] vals = _values;
    byte[] states = _states;

    for (int i = keys.length; i-- > 0; ) {
      keys[i] = (int) 0;
      vals[i] = false;
      states[i] = FREE;
    }
  }

  /**
   * Deletes a key/value pair from the map.
   *
   * @param key an <code>int</code> value
   * @return an <code>boolean</code> value
   */
  public boolean remove(int key) {
    boolean prev = false;
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
    if (! (other instanceof VIntBooleanHashMap)) {
      return false;
    }
    VIntBooleanHashMap that = (VIntBooleanHashMap) other;
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
  public boolean[] getValues() {
    boolean[] vals = new boolean[size()];
    boolean[] v = _values;
    byte[] states = _states;

    for (int i = v.length, j = 0; i-- > 0; ) {
      if (states[i] == FULL) {
        vals[j++] = v[i];
      }
    }
    return vals;
  }

  /**
   * returns the keys of the map. not sorted.
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
   * @param val an <code>boolean</code> value
   * @return a <code>boolean</code> value
   */
  public boolean containsValue(boolean val) {
    byte[] states = _states;
    boolean[] vals = _values;

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
   * @param procedure a <code>TBooleanProcedure</code> value
   * @return false if the loop over the values terminated because
   * the procedure returned false for some value.
   */
  public boolean forEachValue(VBooleanProcedure procedure) {
    byte[] states = _states;
    boolean[] values = _values;
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
   * @param procedure a <code>TIntBooleanProcedure</code> value
   * @return false if the loop over the entries terminated because
   * the procedure returned false for some entry.
   */
  public boolean forEachEntry(VIntBooleanProcedure procedure) {
    byte[] states = _states;
    int[] keys = _set;
    boolean[] values = _values;
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
  public boolean retainEntries(VIntBooleanProcedure procedure) {
    boolean modified = false;
    byte[] states = _states;
    int[] keys = _set;
    boolean[] values = _values;
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
   * @param function a <code>TBooleanFunction</code> value
   */
  public void transformValues(VBooleanFunction function) {
    byte[] states = _states;
    boolean[] values = _values;
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

    return adjustValue(key, (byte) 1);
  }

  /**
   * Adjusts the primitive value mapped to key.
   * operatore ! is activated on value
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
      _values[index] = !_values[index];
      return true;
    }
  }

  /**
   * Returns a deep copy of this map.
   */
  public VIntBooleanHashMap copy() {
    VIntBooleanHashMap newMap;
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(baos);
      oos.writeObject(this);
      byte buf[] = baos.toByteArray();
      oos.close();
      ByteArrayInputStream bais = new ByteArrayInputStream(buf);
      ObjectInputStream ois = new ObjectInputStream(bais);
      newMap = (VIntBooleanHashMap) ois.readObject();
      ois.close();
      return newMap;
    }
    catch (Exception e) {

      newMap = new VIntBooleanHashMap();
      newMap._free = _free;
      newMap._loadFactor = _loadFactor;
      newMap._maxSize = _maxSize;
      newMap._size = _size;

      newMap._set = new int[_set.length];
      System.arraycopy(_set, 0, newMap._set, 0, _set.length);

      newMap._states = new byte[_states.length];
      System.arraycopy(_states, 0, newMap._states, 0, _states.length);

      newMap._values = new boolean[_values.length];
      System.arraycopy(_values, 0, newMap._values, 0, _values.length);

      return newMap;
    }
  }

  /**
   * Returns a new VIntBooleanHashMap with reordered mapping as defined by
   * <code>newOrder</code>
   *
   * @param newOrder  an int to int hashmap that defines the new order:
       *                  for each pair (key, val) in <code>newOrder</code> the value
   *                  that was mapped to val will be mapped to key in the
   *                  returned value.
   * @return          a VIntBooleanHashMap with the same values as this one,
   *                  reordered.
   */
  public VHashMap reorder(VIntIntHashMap newOrder) {

    //copying the map, as it is possible that newOrder does not hold all keys
    //in this map.
    VIntBooleanHashMap retVal = copy();

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
   * returns an int to int hashmap that holds valid keys of this map mapped
   * to themselves. let retVal be the returned value, then: for each pair of
   * keys (i, j) in retVal, let (x,y) be their mapped values in retVal respectively.
   * if (i < j) then if the value that is mapped to x in this map is true then
   * the value that is mapped to y in this map is also true.
   *
   * @return  an int to int hashmap containing valid keys in this map as keys
   *          and as values
   *
   */
  public VIntIntHashMap getSortedOrder() {

    return getSortedOrder(VHashService.getIndices(this));

  }

  /**
       * returns an int to int hashmap that holds valid keys of this map in the range
   * <code>[begin, end]</code> mapped to themselves.
   * let retVal be the returned value, then: for each pair of
   * keys (i, j) in the range, let (x,y) be their mapped values in retVal respectively.
   * if (i < j) then if the value that is mapped to x in this map is true then
   * the value that is mapped to y in this map is also true.
   *
   * @param begin   key number from which to begin the sorted mapping.
   * @param end     greatest key number in the range on which to do the sorting
   *                 mapping.
   * @return  an int to int hashmap containing valid keys in this map as keys
   *          and as values
   */
  public VIntIntHashMap getSortedOrder(int begin, int end) {
    if (end < begin) {
      return new VIntIntHashMap(0);
    }
    int[] keysInRange = VHashService.getIndicesInRange(begin, end, this);
    return getSortedOrder(keysInRange);
  }

  /**
   * returns a subset of this map with values that are mapped to keys <code>
   * start</code> through <codE>start+len</cdoe>.
   *
   * @param start  key number to start retrieving subset from
   * @param len    number of consequetive keys to retrieve their values into
   *               the subset
       * @return       a VIntBooleanHashMap with values and keys from this map, s.t.
       *               keys' range is <code>start</cdoe> through <code>start+len</code>
   */
  public VHashMap getSubset(int start, int len) {
    VIntBooleanHashMap retVal = new VIntBooleanHashMap(len);

    //XIAOLEI
    //int[] validKeys = VHashService.getIndicesInRange(start, start+len, this);
    int[] validKeys = VHashService.getIndicesInRange(start, start + len - 1, this);

    for (int i = 0; i < validKeys.length; i++)

      // XIAOLEI
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
      boolean removed = remove(keysInRange[i]);
      put(keysInRange[i] + 1, removed);
    }
    //putting the new object in key.
    if (obj != null)
      put(key, SparseBooleanColumn.toBoolean(obj));
  }

  public void replaceObject(Object obj, int key) {
    put(key, SparseBooleanColumn.toBoolean(obj));
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
    _values = new boolean[capacity];
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
    boolean oldVals[] = _values;
    byte oldStates[] = _states;

    _set = new int[newCapacity];
    _values = new boolean[newCapacity];
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
    _values[index] = false;
  }

  //=================
  // Private Methods
  //=================

  /**
   * NOTE: this method does nothing. use external class to write this data type
   * into a file.
   */
  private void writeObject(ObjectOutputStream stream) {
    /*stream.defaultWriteObject();
             // number of entries
             stream.writeInt(_size);
         /*VSerializationProcedure writeProcedure = new VSerializationProcedure(stream);
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
      boolean val = stream.readBoolean();
      put(key, val);
    }
  }


  /**
   * returns an int to int hashmap with the keys and values from <codE>
       * validKeys</code> s.t.: for each pair of keys (i,j) let (x,y) be their mapped
   * values in the returned value. if i<j then: if the value that is mapped to
   * x is true then the value that is mapped to y is true. (i.e. the first
   * elements wre the false values and the last ones are the true values)
   *
   * @param validKeys    a sorted int array, with valid keys of this map.
   * @return             an int to int hashmap containing valid keys in <code>
   *                     validKeys</code> as keys and as values.
   */
  private VIntIntHashMap getSortedOrder(int[] validKeys) {

    int currentKeyIndex = 0; //points to currently inpected
    //key
    int newPosIndex = validKeys.length - 1; //points to the last key that
    //its value is unknown

    int[] newOrder = new int[validKeys.length];

    //for each key
    while (currentKeyIndex < newPosIndex) {
      //if the value it holds is true
      if (get(currentKeyIndex)) {
        //swap values with key number which its value is unknown
        newOrder[currentKeyIndex] = validKeys[newPosIndex];
        newOrder[newPosIndex] = validKeys[currentKeyIndex];

        //now key number validKeys[newPosIndex] certainly holds the values true
        //therefore decrease newPosIndex.
        newPosIndex--;
      }

      //the currently inspected key holds value false - therefore increase
      //currentKeyIndex
      else
        currentKeyIndex++;
    } //when the while loop's condition is meet, newOrder holds the sorted order
    //of this map

    //creating a map between the old order and the new order.
    return VHashService.getMappedOrder(validKeys, newOrder);
  }


  //===============
  // Inner Classes
  //===============

  private static final class EqProcedure
      implements VIntBooleanProcedure {
    private final VIntBooleanHashMap _otherMap;

    EqProcedure(VIntBooleanHashMap otherMap) {
      _otherMap = otherMap;
    }

    public final boolean execute(int key, boolean value) {
      int index = _otherMap.index(key);
      if (index >= 0 && eq(value, _otherMap.get(key))) {
        return true;
      }
      return false;
    }

    /**
     * Compare two booleans for equality.
     */
    private final boolean eq(boolean v1, boolean v2) {
      return v1 == v2;
    }

  }


}