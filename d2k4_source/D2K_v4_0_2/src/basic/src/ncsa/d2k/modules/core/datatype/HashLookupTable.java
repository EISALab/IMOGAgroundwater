package ncsa.d2k.modules.core.datatype;

import java.io.*;
import java.util.*;

/**
 * HashLookupTable is a datatype designed for fast lookup of data that
 * can be specified as a complete path through a tree of arbitrary
 * breadth. It is conceived as an n-ary tree (loosely speaking) in
 * which each non-leaf node is a Java HashMap.
 *
 * @author              gpape
 */

public class HashLookupTable implements Serializable {

   private HashMap map; // root HashMap
   private Vector all_maps; // vector of "merged" HashMaps at each level
   private String[] level_labels;
   // private int known_depth = -1;

   /**
    * Constructs a new, empty HashLookupTable with a default capacity and
    * load factor for the root node based on the Java HashMap default
    * (<code>0.75</code> as of JDK 1.4.0).
    */
   public HashLookupTable() {
      map = new HashMap();
   }

   /**
    * Constructs a new, empty HashLookupTable with the specified initial
    * capacity and a default load factor for the root node.
    */
   public HashLookupTable(int initialCapacity) {
      map = new HashMap(initialCapacity);
   }

   /**
    * Constructs a new, empty HashLookupTable with the specified initial
    * capacity and the specified load factor for the root node.
    */
   public HashLookupTable(int initialCapacity, float loadFactor) {
      map = new HashMap(initialCapacity, loadFactor);
   }

   /**
    * Constructs a new HashLookupTable with the same mappings as the given
    * map.
    */
   public HashLookupTable(Map t) {
      map = new HashMap(t);
   }

   /**
    * Associates the specified value with the specified set of keys in this
    * table. If the table previously contained a mapping for the specified
    * set of keys, the old value is replaced (and returned).
    *
    * @param keys       Ordered keys with which the specified value is to
    *                   be associated.
    * @param value      Value to be associated with the specified keys.
    *
    * @return           The previous value associated with the specified
    *                   set of keys, or <code>null</code> if no such
    *                   association previously existed. A <code>null</code>
    *                   return can also indicate that the table previously
    *                   associated <code>null</code> with the specified set
    *                   of keys. (The <code>containsKeys</code> method can
    *                   be used to distinguish these two cases.)
    */
   public Object put(Object[] keys, Object value) {

      /*
      if (known_depth == -1)
         known_depth = keys.length;
      else if (keys.length != known_depth)
         System.out.println("HashLookupTable::put error: inconsistent keys");
      */

      if (map.isEmpty()) {

         // Building the vector on the first insertion probably gives
         // us the best chance of not having to increase its capacity later.

         all_maps = new Vector(keys.length, 2);

         for (int i = 0; i < keys.length; i++) {
            all_maps.add(i, new HashMap());
         }

      }

      for (int i = 0; i < keys.length; i++)
         if (!((HashMap)all_maps.get(i)).containsValue(keys[i]))
            ((HashMap)all_maps.get(i)).put(new
               Integer(((HashMap)all_maps.get(i)).size()), keys[i]);

      HashMap active = map;

      for (int i = 0; i < keys.length - 1; i++) {

         if (active.containsKey(keys[i]))
            active = (HashMap)active.get(keys[i]);
         else {
            active.put(keys[i], new HashMap());
            active = (HashMap)active.get(keys[i]);
         }

      }

      if (active.containsKey(keys[keys.length - 1])) {
         Object old_value = active.get(keys[keys.length - 1]);
         active.put(keys[keys.length - 1], value);
         return old_value;
      }
      else {
         active.put(keys[keys.length - 1], value);
         return null;
      }

   }

   /**
    * Returns the value to which this table maps the specified ordered keys,
    * or <code>null</code> if the table contains no such mapping. A return
    * value of <code>null</code> does not necessarily indicate that the table
    * contains no mapping for the keys; it's also possible that the map
    * explicitly maps the key to <code>null</code>. (The
    * <code>containsKeys</code> method can be used to distinguish these two
    * cases.)
    *
    * @param keys       Ordered keys to be used to retrieve a value.
    *
    * @return           The value to which this table maps the specified
    *                   keys, or <code>null</code> if no such mapping exists.
    */
   public Object get(Object[] keys) {

      /*
      if (known_depth == -1)
         System.out.println("HashLookupTable::get error: get before put");
      else if (keys.length != known_depth)
         System.out.println("HashLookupTable::get error: inconsistent keys, got " + keys.length + " known " + known_depth);
      */

      HashMap active = map;

      for (int i = 0; i < keys.length - 1; i++) {

         if (active.containsKey(keys[i]))
            active = (HashMap)active.get(keys[i]);
         else
            return null;

      }

      if (active.containsKey(keys[keys.length - 1]))
         return active.get(keys[keys.length - 1]);
      else
         return null;

   }

   /**
    * Returns <code>true</code> if this table contains a mapping for the
    * specified keys.
    *
    * @param keys       Ordered keys for which a mapping is to be tested.
    *
    * @return           <code>true</code> if and only if the table contains
    *                   a mapping for the specified keys.
    */
   public boolean containsKeys(Object[] keys) {

      /*
      if (known_depth == -1 || keys.length != known_depth)
         return false;
      */

      HashMap active = map;

      for (int i = 0; i < keys.length - 1; i++) {

         if (active.containsKey(keys[i]))
            active = (HashMap)active.get(keys[i]);
         else
            return false;

      }

      if (active.containsKey(keys[keys.length - 1]))
         return true;
      else
         return false;

   }

   /**
    * Returns a HashMap containing as values all objects on a given level
    * of the table. The keys are the Integer order of placement into the
    * table, indexed from zero.
    *
    * @param level      The level for which values are to be returned.
    *
    * @return           A HashMap containing the values at the specified
    *                   level.
    */
   public HashMap getMerged(int level) {
      return (HashMap)all_maps.get(level);
   }

   /**
    * Allows the user to set a text label for each level of the tree.
    *
    * @param labels     The Strings to be used as labels.
    */
   public void setLabels(String[] labels) {
      level_labels = labels;
   }

   /**
    * Returns the text label for a given level.
    *
    * @param label      The level for which a label is to be returned.
    *
    * @return           The String label for the given level.
    */
   public String getLabel(int level) {
      return level_labels[level];
   }

}
