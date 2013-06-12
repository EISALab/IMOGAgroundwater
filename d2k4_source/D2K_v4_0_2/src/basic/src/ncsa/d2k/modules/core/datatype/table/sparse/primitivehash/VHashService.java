package ncsa.d2k.modules.core.datatype.table.sparse.primitivehash;

//==============
// Java Imports
//==============

import java.util.Arrays;

/**
 * Title:        Sparse Table
 * Description:  Sparse Table projects will implement data structures compatible to the interface tree of Table, for sparsely stored data.
 * Copyright:    Copyright (c) 2002
 * Company:      ncsa
 * @author vered goren
 * @version 1.0
 */

public class VHashService {

  //==============
  // Data Members
  //==============

  //================
  // Constructor(s)
  //================

  public VHashService() {
  }

  //================
  // Static Methods
  //================

  public static int[] getIndices(VHashMap map) {
    //retrieving all valid rows and sorting them
    int[] validIndices = map.keys();
    Arrays.sort(validIndices);
    return validIndices;
  }

  /**
       * Retrieves valid keys from <code>map</cdoe> in the section key no. <code>begin
   * </code> through key no. <code>end</code>.
   *
   * @param begin   key no. from which to begin retrieving of keys.
       * @param end     last key no. in the section from which the keys are retrieved.
   * @param map     a VHashMap from which to retrieve the keys.
   * @return        an int array that holds the valid keys in the range <code>
   *                [begin, end]</cdoe>
   */
  public static int[] getIndicesInRange(int begin, int end, VHashMap map) {

    int[] keysInRange = new int[0]; //the returned value

    if (end < begin)
      return keysInRange;

    //retrieving all valid rows and sorting them
    int[] validIndices = map.keys();
    Arrays.sort(validIndices);

    //for (int i = 0; i < validIndices.length; i++)
    //System.out.print(validIndices[i] + ", ");
    //System.out.println();

    // XIAOLEI
    int beginIndex = findPlace(validIndices, begin);
    int endIndex = findEndPlace(validIndices, end);

    //System.out.println(beginIndex + " ---> " + endIndex);

    //if begin is greater than any valid row number - return an empty array
    if (beginIndex >= validIndices.length)
      return keysInRange;

    if (endIndex >= validIndices.length)
      endIndex = validIndices.length - 1;

    int numKeysInRange = endIndex - beginIndex + 1;
    keysInRange = new int[numKeysInRange];
    System.arraycopy(validIndices, beginIndex, keysInRange, 0, numKeysInRange);

    return keysInRange;
  }

  /**
   * Returns an int to int hashmap that defines a new mapping s.t. for the
   * hashmap that activated this method: the value that was mapped to <code>
       * newOrder[i]</code> (the value in the returned map) will be mapped to <code>
   * oldOrder[i]</code> (the key in the returned map).
   *
   * <code>oldOrder</code> and <code>newOrder</code> must be of the same length
   *
   * @param oldOrder    the order of the values before sorting
   * @param newOrder    the new order of the values.
       * @return            a VIntIntHashMap that defines how to reorder the values.
   */
  public static VIntIntHashMap getMappedOrder(int[] oldOrder, int[] newOrder) {
    VIntIntHashMap retVal = new VIntIntHashMap(oldOrder.length);

    for (int i = 0; i < oldOrder.length && i < newOrder.length; i++)
      retVal.put(oldOrder[i], newOrder[i]);

    return retVal;
  }

  /**
   * Returns an int to int hashmap that represent a new order as specified by
   * <codE>newOrder</cdoe>: For each item <codE>newOrder[i]</code> that is
   * a valid key in <code>map</code> - mapping it to key i in the returned
   * value.
   *
   * @param newOrder    an int array htat defines a new order: each value val
   *                    that was mapped to key <codE>newOrder[i]</cdoe> should
   *                    be mapped to key i.
   * @param map         the map to be reordered by <code>newOrder</code>.
   * @return            a VIntIntHashMap representing <code>newOrder</code>'s
   *                    items intersected with <codE>map</code>'s keys.
   */
  public static VIntIntHashMap toMap(int[] newOrder, VHashMap map) {
    VIntIntHashMap retVal = new VIntIntHashMap(map.size());
    for (int i = 0; i < newOrder.length; i++)
      if (map.containsKey(newOrder[i]))
        retVal.put(i, newOrder[i]);

    return retVal;
  }

  /**
   * Finds the index of <codE>value</code> in <code>arr</code>
   *
   * @param arr       a sorted array of ints.
   * @param value     a value to find its index in <code>arr</codE>
   * @return          an int: if the returned value is greater than or equal
   *                  to the length of <code>arr</code> then - all values in
   *                  <code>arr</code> are smaller than <codE>value</code>.
   */
  public static int findPlace(int[] arr, int value) {

    int retVal = Arrays.binarySearch(arr, value);

    if (retVal < 0) {
      retVal = (retVal + 1) * -1;
    }

    return retVal;
  }

  //XIAOLEI
  public static int findEndPlace(int[] arr, int value) {

    int retVal = Arrays.binarySearch(arr, value);

    if (retVal < 0) {
      retVal = (retVal + 1) * -1 - 1;
    }

    return retVal;
  }

  /**
   * Returns the maximal key in <code>map</code>
   */
  public static int getMaxKey(VHashMap map) {
    int[] keys = getIndices(map);
    if (keys.length == 0)
      return -1;
    return keys[keys.length - 1];
  }
}