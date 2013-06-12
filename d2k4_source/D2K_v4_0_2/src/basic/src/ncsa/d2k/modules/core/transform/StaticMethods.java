package ncsa.d2k.modules.core.transform;

import java.util.*;
import ncsa.d2k.modules.core.datatype.table.*;
import java.sql.*;
import ncsa.d2k.modules.core.io.sql.*;
/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author vered
 * @version 1.0
 *
 * this class provides static methods used by many headless ui modules.
 */

public class StaticMethods {
  public StaticMethods() {
  }

  static public HashMap getAvailableTables(ConnectionWrapper cw) throws Exception{
    HashMap retVal = new HashMap();
    Connection con = cw.getConnection();
     DatabaseMetaData metadata = con.getMetaData();
     String[] type = {"TABLE"};
     ResultSet names = metadata.getTables(null,"%","%",type);
     int counter = 0;
     while (names.next()) {
       String currName = names.getString("TABLE_NAME");
       retVal.put(currName.toUpperCase(), new Integer(counter));
       counter++;
     } //while
    return retVal;
  }

  static public HashMap getAvailableAttributes(ConnectionWrapper cw, String tableName) throws Exception{
   HashMap retVal = new HashMap();
   Connection con = cw.getConnection();
    DatabaseMetaData metadata = con.getMetaData();
    String[] type = {"TABLE"};
   ResultSet columns = metadata.getColumns(null,"%",tableName,"%");
    int counter = 0;
    while (columns.next()) {
       String columnName = columns.getString("COLUMN_NAME");
       retVal.put(columnName.toUpperCase() , new Integer(counter));
       counter++;
     }//while column

   return retVal;
 }



  /**
   * builds a hash map of columns' labels in <code>table</code>.
   * maps column's name <-> column's id.
   * @param table - its column labels are the keys in the map.
   * @return - a HashMap with column's label <-> column's index.
   */
  static public HashMap getAvailableAttributes(Table table){
    HashMap map = new HashMap(table.getNumColumns());
    for (int i=0; i< table.getNumColumns(); i++)
      map.put(table.getColumnLabel(i).toUpperCase(), new Integer(i));

    return map;
  }


    /**
     * builds a hash map of scalar columns' labels in <code>table</code>.
     * maps scalar column's name <-> column's id.
     * @param table - its scalar column labels are the keys in the map.
     * @return - a HashMap with scalar column's label <-> column's index.
     */
    static public HashMap getScalarAttributes(Table table){
      HashMap map = new HashMap(table.getNumColumns());
      for (int i=0; i< table.getNumColumns(); i++)
        if(table.isColumnScalar(i))
          map.put(table.getColumnLabel(i).toUpperCase(), new Integer(i));

      return map;
    }


  /**
   * returns an array of boolean such that if <code>names[i]</code< is a key in
   * <code>available</code> then the i_th boolean in the array is true.
   * @param names - an array of Strings, to check which of them is a key in
   *                <codE>available</code>
   * @param available - maps name <-> id.
   * @return   - boolean[], such that if <code>names[i]</codE> is a key in
   *            <code>available</codE> then <code>returned_value[i] = true</code>.
   */
  static public boolean[] getRelevant(String[] names, HashMap available){
    boolean[] relevant = new boolean[names.length];
    for (int i=0; i<names.length; i++)
      if(available.containsKey(names[i].toUpperCase()))
        relevant[i] = true;
      else System.out.println("Label " + names[i] + " was not found in the given input table. "                               );

    return relevant;
  }


  /**
  * returns an array of boolean such that if <code>names[i]</code< is in
  * <code>available</code> then the i_th boolean in the array is true.
  * @param names - an array of Strings, to check which of them is  in
  *                <codE>available</code>
  * @param available - vector of Strings
  * @return   - boolean[], such that if <code>names[i]</codE> is  in
  *            <code>available</codE> then <code>returned_value[i] = true</code>.
  */
 static public boolean[] getRelevant(String[] names, Vector available){
   boolean[] relevant = new boolean[names.length];
   for (int i=0; i<names.length; i++)
     if(available.contains(names[i].toUpperCase()))
       relevant[i] = true;
     else System.out.println("Label " + names[i] + " was not found in the given input table. " );

   return relevant;
 }


  /**
   * counts how many items in <code>pos</code> are <code>true</cdoe> and returns
   * the total.
   * @param pos - a boolean array, to have its positive items counted
   * @return    - number of positive items in <code.pos</code>.
   */
  static public int getNumPositive(boolean[] pos){
    int num = 0;
    for (int i=0; i<pos.length; i++)
      if(pos[i]) num++;

    return num;
  }

  /**
   * finds the intersection between <code>desired</code> and <code>available</code>.
   * and returns it as a String array.
   * @param desired - String[], to check which of them is in <code>available</code>
   * @param available - maps name <-> id.
   * @return - String[]. returned_value[i] is a String both in <codE>desired</code>
   *           and <code>available</code>.
   */
  static public String[] getIntersection(String[] desired, HashMap available){
    boolean[] relevant = getRelevant(desired, available);
    int numPos = getNumPositive(relevant);
    String[] retVal = new String[numPos];
    for (int i=0, j=0; i<numPos; i++)
      if(relevant[i]){
        retVal[j] = desired[i];
        j++;
      }
    return retVal;
  }


  /**
  * finds the intersection between <code>desired</code> and <code>available</code>.
  * and returns it as a String array.
  * @param desired - String[], to check which of them is in <code>available</code>
  * @param available - vector of Strings.
  * @return - String[]. returned_value[i] is a String both in <codE>desired</code>
  *           and <code>available</code>.
  */
 static public String[] getIntersection(String[] desired, Vector available){
   boolean[] relevant = getRelevant(desired, available);
   int numPos = getNumPositive(relevant);
   String[] retVal = new String[numPos];
   for (int i=0, j=0; i<numPos; i++)
     if(relevant[i]){
       retVal[j] = desired[i];
       j++;
     }
   return retVal;
 }

 /**
  * returns an int[] with the IDs of Strings from <codE>desired</codE> that
  * are keys in <code>available</code>.
  * @param desired - Strings to check whether they are keys in <code>available</code>.
  * @param available - maps String <-> ID
  * @return - an int[] with the IDs of Strings that are both in <doe>desired</cdoe>
  *           and also keys in <codE>available</code>.
  */
 static public int[] getIntersectIds(String[] desired, HashMap available){
   boolean[] relevant = getRelevant(desired, available);
   int numPos = getNumPositive(relevant);
   int[] retVal = new int[numPos];
   for (int i=0, j=0; i<numPos; i++)
     if(relevant[i]){
       retVal[j] = ((Integer) available.get(desired[i].toUpperCase())).intValue();
       j++;
     }
   return retVal;

 }
 /**
  * returns the id of <code>name</code> if it is a key in <code>available</code>
  * otherwise returns -1.
  * @param name - a key to look up in <code>available</code>
  * @param available - maps String <-> id
  * @return <codE>name</code>'s id in <code>available</code> if it is a key in
  * this map, or -1.
  */
 static public int getID(String name, HashMap available){
   int retVal = -1;
   if(available.containsKey(name.toUpperCase()))
     retVal = ((Integer) available.get(name.toUpperCase())).intValue();
  return retVal;
 }


}//StaticMethods