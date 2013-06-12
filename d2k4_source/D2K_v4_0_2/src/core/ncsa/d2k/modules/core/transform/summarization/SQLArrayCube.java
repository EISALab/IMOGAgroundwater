package ncsa.d2k.modules.core.transform.summarization;
 /**
 * <p>Title: SQLArrayCube </p>
 * <p>Description: Calculate a Cube based on array-based algorithm </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: NCSA ALG </p>
 * @author Dora Cai
 * @version 1.0
 */

import ncsa.d2k.core.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.core.modules.UserView;
import ncsa.d2k.modules.core.io.sql.*;
import ncsa.d2k.userviews.swing.*;
import ncsa.gui.Constrain;
import ncsa.gui.JOutlinePanel;
import java.sql.*;
import java.util.*;
import java.util.ArrayList;
import java.text.*;
import javax.swing.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import oracle.sql.*;
import oracle.jdbc.driver.*;

public class SQLArrayCube extends ComputeModule
        {
  JOptionPane msgBoard = new JOptionPane();
  File file;
  FileWriter fw;
  ConnectionWrapper cw;
  Connection con;
  String tableName;
  String cubeTableName;
  String[] fieldNames;
  String whereClause;
  double support = 0.2;
  int maxRuleSize = 5;
  // if number of selected columns <= maxruleSize, ruleSize = # of selected column - 1,
  // otherwise, ruleSize = maxRuleSize
  int totalRow;
  double cutOff;
  static String NOTHING = "";
  static String DELIMITER = "\t";
  static String ELN = "\n";
  static String NA = "NOAVL";
  // ArrayList to keep selected columns. The columns are ordered by cardinality
  ArrayList columnList;
  // Array of ArrayLists to keep unique values for each column
  ArrayList[] mapArray;
  // Array of ArrayList for cube, one ArrayList for offset and another for count
  ArrayList[] cube;
  // Array of ArrayList for done-list  (temp solution, better use arraylist of arraylist)
  ArrayList[] doneList;
  // String for the list of column names seperated by ","
  String columnStr;
  boolean firstBranch = true;

  public void CalulateCount() {
  }

  public String getOutputInfo (int i) {
		switch (i) {
			case 0: return "JDBC data source to make database connection.";
			case 1: return "The name of the table that stores data statistics.";
			default: return "No such output";
		}
	}

  public String getInputInfo (int i) {
		switch (i) {
			case 0: return "JDBC data source to make database connection.";
			case 1: return "Field Names";
			case 2: return "Table Name";
			case 3: return "Where Clause";
			default: return "No such input";
		}
	}

  public String getModuleInfo () {
		return "<html>  <head>      </head>  <body>    Calculate counts for all possible combinations for the selected table  </body></html>";
	}

	/** this property is the min acceptable support score. */
  public void setMinimumSupport (double i) {
    support = i;
  }
  public double getMinimumSupport () {
    return support;
  }
  public void setMaxRuleSize (int yy) {
    maxRuleSize = yy;
  }
  public int getMaxRuleSize () {
    return maxRuleSize;
  }

  public String[] getInputTypes () {
		String[] types = {"ncsa.d2k.modules.core.io.sql.ConnectionWrapper","[Ljava.lang.String;","java.lang.String","java.lang.String"};
		return types;
	}

  public String[] getOutputTypes () {
		String[] types = {"ncsa.d2k.modules.core.io.sql.ConnectionWrapper","java.lang.String"};
		return types;
	}

  protected String[] getFieldNameMapping () {
    return null;
  }

  public void doit() {
    cw = (ConnectionWrapper)pullInput(0);
    fieldNames = (String[])pullInput(1);
    tableName = (String)pullInput(2);
    cubeTableName = tableName + "_CUBE";
    whereClause = (String)pullInput(3);
    columnList = new ArrayList();
    cube = new ArrayList[2];

    for (int fieldIdx=0; fieldIdx<fieldNames.length; fieldIdx++) {
      columnList.add(fieldNames[fieldIdx]);
    }

    doneList = new ArrayList[columnList.size()];
    for (int colIdx=0; colIdx<columnList.size(); colIdx++) {
        doneList[colIdx] = new ArrayList();
    }

    totalRow = getRowCount();
    cutOff = totalRow * support;
    mapArray = new ArrayList[columnList.size()];
    setMapArray();
    sortColumns();
    buildBaseCube();
    createCubeTable();
    // don't need to save the base cube if the column number > maxRuleSize
    if (columnList.size() <= maxRuleSize) {
      saveCube(cube, mapArray, columnList);
    }
    firstBranch = true;
    createCompList(cube, mapArray, columnList);
    this.pushOutput(cw, 0);
    this.pushOutput(cubeTableName, 1);
  }

  /** get the count of rows from the database table */
  protected int getRowCount() {
    try {
      con = cw.getConnection();
      Statement cntStmt;
      String sb = new String("select count(1) from " + tableName);
      System.out.println("the query string is " + sb);
      cntStmt = con.createStatement ();
      ResultSet tableSet = cntStmt.executeQuery(sb);
      tableSet.next();
      int rowCount = tableSet.getInt(1);
      cntStmt.close();
      return(rowCount);
    }
    catch (Exception e){
      JOptionPane.showMessageDialog(msgBoard,
                e.getMessage(), "Error",
                JOptionPane.ERROR_MESSAGE);
      System.out.println("Error occoured in getRowCount.");
      return(0);
    }
  }

  /** build a ArrayList array. Each array item stores an ArrayList for a column,
   *  And each ArrayList stores all unique values for the column.
   */
  protected void setMapArray() {
    try {
      for (int colIdx=0; colIdx<columnList.size(); colIdx++) {
        mapArray[colIdx] = new ArrayList();
        con = cw.getConnection();
        Statement mapStmt;
        String sb = new String("select distinct " + columnList.get(colIdx) + " from " + tableName + " where " + columnList.get(colIdx) + " is not null");
        System.out.println("the query string is " + sb);
        mapStmt = con.createStatement ();
        ResultSet tableSet = mapStmt.executeQuery(sb);
        // add value "NOT AVAILABLE" to the arraylist. This value will be used for all null or blank values
        mapArray[colIdx].add(NA);
        while (tableSet.next()) {
          if (!tableSet.getString(1).equals(" ") && !tableSet.getString(1).equals("")) {
            mapArray[colIdx].add(tableSet.getString(1));
          }
        }
        mapStmt.close();
      }
    }
    catch (Exception e){
      JOptionPane.showMessageDialog(msgBoard,
                e.getMessage(), "Error",
                JOptionPane.ERROR_MESSAGE);
      System.out.println("Error occoured in setMapArray.");
    }
  }

  // order the columns based on cardinality
  protected void sortColumns() {
    for (int i=0; i<mapArray.length-1; i++) {
      for (int j=mapArray.length-1; j>i; j--) {
        if (mapArray[j-1].size() < mapArray[j].size()) {
          ArrayList tmpArray = mapArray[j-1];
          mapArray[j-1] = mapArray[j];
          mapArray[j] = tmpArray;
          Object tmpString = columnList.get(j-1);
          columnList.set(j-1, columnList.get(j));
          columnList.set(j, tmpString);
        }
      }
    }
  }

  /** Calucute the count for cells in the base cube (data level)
   *  Since the cube will be sparse, use ArrayList to implement it.
   *  We need two ArrayList, one for offset, another for count.
   *  The offset is the distance to the first cell in the cube.
   */
  protected void buildBaseCube() {
    cube[0] = new ArrayList();
    cube[1] = new ArrayList();
    try {
      con = cw.getConnection();
      Statement cubeStmt;
      String sb = new String("select ");
      String aValue;
      columnStr=NOTHING;
      int valIndex;
      int cubeOffset = 0;
      int tmpOffset = 0;
      // data must be retrieved by the column order
      for (int colIdx = 0; colIdx < columnList.size(); colIdx++) {
        if (columnStr.equals(NOTHING))
          columnStr = columnStr + columnList.get(colIdx);
        else // add comma between column names
          columnStr = columnStr + ", " + columnList.get(colIdx);
      }
      sb = sb + columnStr + " from " + tableName + " order by " + columnStr;
      System.out.println("the query string in buildCube is " + sb);
      cubeStmt = con.createStatement ();
      ResultSet tableSet = cubeStmt.executeQuery(sb);
      while (tableSet.next()) {
        // Calculate the offset against to the starting cell
        // The formula for calculation is: (assume 4 columns: a, b, c, d)
        // Ia * Ub * Uc * Ud + Ib * Uc * Ud + Ic * Ud + Id
        // where Ia, Ib, Ic, Id represent the value's index in mapArray for the columns a, b, c, d.
        // and Ua, Ub, Uc represent the number of uniq values in the columns a, b, c, d
        cubeOffset = 0;
        // determine the column index
        for (int colIdx = 0; colIdx < columnList.size(); colIdx++) {
          tmpOffset = 0;
          // tableSet's data starts from index 1
          aValue = tableSet.getString(colIdx+1);
          if (aValue==null) { // it is null, replace it with value of NA
            aValue = NA;
            //System.out.println("null value is converted to " + NA);
          }
          else if (aValue.equals(" ") || aValue.equals("")) { // it is a blank string, replace it with value of NA
            aValue = NA;
            //System.out.println("blank value is converted to " + NA);
          }
          for (int mapIdx = 0; mapIdx < mapArray[colIdx].size(); mapIdx++) {
            if (aValue.equals(mapArray[colIdx].get(mapIdx))) {
              valIndex = mapIdx;
              tmpOffset = 1;
              for (int tailIdx = colIdx+1; tailIdx < columnList.size(); tailIdx++) {
                //calculate Ub * Uc * Ud
                tmpOffset = tmpOffset * mapArray[tailIdx].size();
              }
              //calculate Ia * Ub * Uc * Ud
              tmpOffset = tmpOffset * (valIndex);
              break;
            }
          }
          cubeOffset = cubeOffset + tmpOffset;
        }
        updateCubeCell(cube, cubeOffset, 1);
      } // end of while
      cubeStmt.close();
    }
    catch (Exception e){
      JOptionPane.showMessageDialog(msgBoard,
        e.getMessage(), "Error",
        JOptionPane.ERROR_MESSAGE);
        System.out.println("Error occoured in buildBaseCube.");
    }
  }

  protected void updateCubeCell(ArrayList[] aCube, int newOffset, int newCnt ) {
    boolean newCell = true;
    for (int cubeIdx = 0; cubeIdx < aCube[0].size(); cubeIdx++) {
      if (aCube[0].get(cubeIdx).toString().equals(Integer.toString(newOffset))) {
        // found the cell with same offset, increment the count by newCnt
        int cnt = Integer.parseInt(aCube[1].get(cubeIdx).toString());
        newCnt = newCnt + cnt;
        aCube[1].set(cubeIdx, (Object)Integer.toString(newCnt));
        newCell = false;
        break;
      }
    }
    // if can't find the cell with same offset, create a new one
    if (newCell == true) {
      aCube[0].add((Object)Integer.toString(newOffset));
      aCube[1].add((Object)Integer.toString(newCnt));
    }
  }

  // create a database table to save the cube data
  protected void createCubeTable() {
    try {
      con = cw.getConnection();
      Statement countStmt;
      Statement tableStmt;
      Statement columnStmt;
      Statement alterStmt;
      String sb;
      String tableQry;
      String columnQry;
      ResultSet result;
      ResultSet tableResult;
      ResultSet columnResult;
      // if the cube table already exists, drop the table first
      sb = new String("select count(1) from all_tables where table_name = '" + cubeTableName.toUpperCase() + "'");
      countStmt = con.createStatement();
      System.out.println("the query string is " + sb);
      ResultSet tableSet = countStmt.executeQuery(sb);
      tableSet.next();
      int rowCount = tableSet.getInt(1);
      countStmt.close();

      if (rowCount > 0) {
        sb = new String("drop table " + cubeTableName);
        countStmt = con.createStatement();
        System.out.println("the query string is " + sb);
        result = countStmt.executeQuery(sb);
        countStmt.close();
      }

      // Create an empty cube table. The table has the selected columns + count column
      // Convert all numeric data types to character data type
      //sb = new String("create table " + cubeTableName + " as select " + columnStr + " from " + tableName + " where " + columnList.get(1) + " = '" + DELIMITER.toCharArray() + "'");
      tableQry = new String("create table " + cubeTableName + "(");
      columnQry = new String("select column_name, data_type, data_length from all_tab_columns ");
      columnQry = columnQry + "where table_name = '" + tableName + "' order by column_id";
      System.out.println("the columnQry is " + columnQry);
      columnStmt = con.createStatement();
      columnResult = columnStmt.executeQuery(columnQry);
      int colNumber = 0;
      while (columnResult.next()) {
        if (isChosenColumn(columnResult.getString(1))) {
          if (colNumber > 0) {
            tableQry = tableQry + ",";
          }
          colNumber ++;
          if (columnResult.getString(2).equals("VARCHAR2")) {
          tableQry = tableQry + columnResult.getString(1) + " VARCHAR2(" +
                     columnResult.getString(3) + ")";
          }
          else if (columnResult.getString(2).equals("NUMBER")) {
            tableQry = tableQry + columnResult.getString(1) + " VARCHAR2(11)";
          }
          else if (columnResult.getString(2).equals("DATE")) {
            tableQry = tableQry + columnResult.getString(1) + " VARCHAR2(11)";
          }
        }
      }
      tableQry = tableQry + ")";
      System.out.println("the query to create a cube table is " + tableQry);

      tableStmt = con.createStatement();
      result = tableStmt.executeQuery(tableQry);
      tableStmt.close();
      // add two columns to the cube table, one column for saving the size of the item set, another for saving counts
      sb = new String("alter table " + cubeTableName + " add (set_size number, cnt number)");
      System.out.println("the alter table query string in createCubeTable is " + sb);
      alterStmt = con.createStatement();
      result = alterStmt.executeQuery(sb);
      alterStmt.close();
      // insert a record to the cube table. This record is the most aggregated record. There is
      // no value for all columns except the column cnt which is the total number of rows in the
      // data set.
      sb = new String ("insert into " + cubeTableName + " (cnt) values (" + totalRow + ")");
      alterStmt = con.createStatement();
      result = alterStmt.executeQuery(sb);
      alterStmt.close();
    }
    catch (Exception e){
      JOptionPane.showMessageDialog(msgBoard,
        e.getMessage(), "Error",
        JOptionPane.ERROR_MESSAGE);
        System.out.println("Error occoured in createCubeTable.");
    }
  }

  // This method will do three tasks:
  // 1. Mapping the offset value to the real column value
  // The formula for calculation is: (assume 4 columns: a, b, c, d)
  // where Ia, Ib, Ic, Id represent the value's index in mapArray for the columns a, b, c, d.
  // and Ua, Ub, Uc represent the number of uniq values in the columns a, b, c, d
  // Ia = offset / (Ub * Uc * Ud)
  // offset = offset - (Ia * Ub * Uc * Ud)
  // Ib = offset / (Uc * Ud)
  // offset = offset - (Ib * Uc * Ud)
  // Ic = offset / Ud
  // offset = offset - (Ic * Ud) = Id
  // 2. Save the cube to a database.
  // 3. Display the cube's data cube.
  protected void saveCube(ArrayList[] aCube,
                              ArrayList[] aMapArray,
                              ArrayList aColumnList) {
    int offset;
    int dimensionSize;
    int idx;
    String columnValue;
    String columnHeading = NOTHING;

    // build column heading string for insert SQL statement
    for (int colIdx = 0; colIdx < aColumnList.size(); colIdx++) {
      if (colIdx == 0) {
        columnHeading = columnHeading + aColumnList.get(colIdx).toString();
      }
      else {// add comma to seperate column headings
        columnHeading = columnHeading + ", " + aColumnList.get(colIdx).toString();
      }
    }
    // the last two columns in the cube table are "set_size" and "cnt"
    columnHeading = columnHeading + ", set_size, cnt";
    for (int cubeIdx = 0; cubeIdx < aCube[0].size(); cubeIdx++) {
      // if count < cutOff, don't save it
      if (Integer.parseInt(aCube[1].get(cubeIdx).toString()) >= cutOff) {
        columnValue = NOTHING;
        // get offset for i cell in the cube
        offset = Integer.parseInt(aCube[0].get(cubeIdx).toString());
        for (int headIdx = 0; headIdx < aColumnList.size(); headIdx++) {
          dimensionSize = 1;
          for (int moveIdx = headIdx+1; moveIdx < aColumnList.size(); moveIdx++) {
            // get Ub * Uc * Ud
            dimensionSize = dimensionSize * aMapArray[moveIdx].size();
          }
          // get Ia = offset / (Ub * Uc * Ud)
          idx = offset / dimensionSize;
          if (headIdx == 0) { // the first column value, don't add comma
            columnValue = columnValue + "'" + aMapArray[headIdx].get(idx).toString() + "'";
          }
          else { // not the first column value, add comma to seperate the values
            columnValue = columnValue + ", '" + aMapArray[headIdx].get(idx).toString() + "'";
          }
          // get offset = offset - (Ub * Uc * Ud * Ia)
          offset = offset - (dimensionSize * idx);
        }
        // last 2 columns in the cube table are "set_size" and "cnt"
        columnValue = columnValue + ", " + aColumnList.size() + ", " + aCube[1].get(cubeIdx);
        insertToTable(columnHeading, columnValue);
      }
    }
  }

  protected void insertToTable(String headingStr, String valueStr) {
    try {
      if (valueStr.indexOf(NA)== (-1)) { // if any column value contains NA, not save it
        //System.out.println("headingStr is " + headingStr);
        //System.out.println("valueStr is " + valueStr);
        con = cw.getConnection();
        Statement stmt;
        String sb;
        ResultSet result;
        sb = new String("insert into " + cubeTableName + " (" + headingStr + ") values (" + valueStr + ")");
        stmt = con.createStatement();
        result = stmt.executeQuery(sb);
        stmt.close();
      }
    }
    catch (Exception e){
      JOptionPane.showMessageDialog(msgBoard,
        e.getMessage(), "Error",
        JOptionPane.ERROR_MESSAGE);
        System.out.println("Error occoured in insertToTable.");
    }
  }

  protected void createCompList(ArrayList[] parentCube,
                                ArrayList[] parentMapArray,
                                ArrayList parentColumnList) {
    int ruleSize;
    // To restrict the complexity, only calculate up to 5-item set
    if (parentColumnList.size()>maxRuleSize) {
      ruleSize = maxRuleSize;
    }
    else {
      ruleSize = parentColumnList.size()-1;
      if (ruleSize == 0) {
        return;
      }
    }
    int pHeadPt1 = 0;
    int pHeadPt2 = 0;
    int pHeadPt3 = 0;
    int pHeadPt4 = 0;
    int pHeadPt5 = 0;
    int colIdx;
    int newIdx = 0;
    while (pHeadPt1 <= (parentColumnList.size() - ruleSize)) {
      pHeadPt2 = pHeadPt1 + 1;
      if (pHeadPt2 > (parentColumnList.size() - 1)) {
        ArrayList[] newMapArray = new ArrayList[ruleSize];
        ArrayList newColumnList = new ArrayList();
        addArrayItems(ruleSize, parentCube, parentMapArray, parentColumnList, newMapArray,
                      newColumnList, pHeadPt1, pHeadPt2, pHeadPt3, pHeadPt4, pHeadPt5);
        break;
      }
      while (pHeadPt2 <= (parentColumnList.size() - ruleSize + 1) &&
        pHeadPt2 <= (parentColumnList.size() - 1)) {
        pHeadPt3 = pHeadPt2 + 1;
        if (pHeadPt3 > (parentColumnList.size() - 1)) {
          ArrayList[] newMapArray = new ArrayList[ruleSize];
          ArrayList newColumnList = new ArrayList();
          addArrayItems(ruleSize, parentCube, parentMapArray, parentColumnList, newMapArray,
                        newColumnList, pHeadPt1, pHeadPt2, pHeadPt3, pHeadPt4, pHeadPt5);
          break;
        }
        while (pHeadPt3 <= (parentColumnList.size() - ruleSize + 2) &&
          pHeadPt3 <= (parentColumnList.size() - 1)) {
          pHeadPt4 = pHeadPt3 + 1;
          if (pHeadPt4 > (parentColumnList.size() - 1)) {
            ArrayList[] newMapArray = new ArrayList[ruleSize];
            ArrayList newColumnList = new ArrayList();
            addArrayItems(ruleSize, parentCube, parentMapArray, parentColumnList, newMapArray,
                          newColumnList, pHeadPt1, pHeadPt2, pHeadPt3, pHeadPt4, pHeadPt5);
            break;
          }
          while (pHeadPt4 <= (parentColumnList.size() - ruleSize + 3) &&
            pHeadPt4 <= (parentColumnList.size() - 1)) {
            pHeadPt5 = pHeadPt4 + 1;
            if (pHeadPt5 > (parentColumnList.size() - 1)) {
              ArrayList[] newMapArray = new ArrayList[ruleSize];
              ArrayList newColumnList = new ArrayList();
              addArrayItems(ruleSize, parentCube, parentMapArray, parentColumnList, newMapArray,
                            newColumnList, pHeadPt1, pHeadPt2, pHeadPt3, pHeadPt4, pHeadPt5);
              break;
            }
            while (pHeadPt5 <= (parentColumnList.size() - 1)) {
              ArrayList[] newMapArray = new ArrayList[ruleSize];
              ArrayList newColumnList = new ArrayList();
              addArrayItems(ruleSize, parentCube, parentMapArray, parentColumnList, newMapArray,
                            newColumnList, pHeadPt1, pHeadPt2, pHeadPt3, pHeadPt4, pHeadPt5);
              pHeadPt5++;
            }
            pHeadPt4++;
          }
          pHeadPt3++;
        }
        pHeadPt2++;
      }
      pHeadPt1++;
    }
  }

  protected void addArrayItems(int size, ArrayList[] preCube,
                               ArrayList[] preMapArray,
                               ArrayList preColumnList,
                               ArrayList[] currMapArray,
                               ArrayList currColumnList,
                               int pt1, int pt2, int pt3, int pt4, int pt5) {
    for (int itemIdx = 0; itemIdx < size; itemIdx++) {
      currMapArray[itemIdx] = new ArrayList();
    }
    if (size >= 1) {
      currColumnList.add(preColumnList.get(pt1));
      copyArrayList(preMapArray[pt1], currMapArray[0]);
    }
    if (size >= 2) {
      currColumnList.add(preColumnList.get(pt2));
      copyArrayList(preMapArray[pt2], currMapArray[1]);
    }
    if (size >= 3) {
      currColumnList.add(preColumnList.get(pt3));
      copyArrayList(preMapArray[pt3], currMapArray[2]);
    }
    if (size >= 4) {
      currColumnList.add(preColumnList.get(pt4));
      copyArrayList(preMapArray[pt4], currMapArray[3]);
    }
    if (size >= 5) {
      currColumnList.add(preColumnList.get(pt5));
      copyArrayList(preMapArray[pt5], currMapArray[4]);
    }
    // Don't recalculate if the item set has been processed
    // If it is not done, save the new item set
    if (!isDone(currColumnList)) {
      ArrayList savedColumnList = new ArrayList();
      doneList[size].add((Object) savedColumnList);
      copyArrayList(currColumnList, savedColumnList);
      buildSumCube(preCube, preMapArray, preColumnList, currMapArray, currColumnList);
    }
  }

  protected void buildSumCube(ArrayList[] preCube,
                              ArrayList[] preMapArray,
                              ArrayList preColumnList,
                              ArrayList[] currMapArray,
                              ArrayList currColumnList) {
    ArrayList[] currCube = new ArrayList[2]; // one dimension for offset, one for count
    currCube[0] = new ArrayList();
    currCube[1] = new ArrayList();
    int preOffset;
    int currOffset;
    int tmpOffset;
    int dimensionSize;
    int preIdx;
    int currIdx;
    int currCnt;
    boolean match = false;

    // convert the offset in the super cube to the current cube (aggregate one level)
    // using the similar formula as buildBaseCube.
    for (int cubeIdx = 0; cubeIdx < preCube[0].size(); cubeIdx++) {
      currCnt = Integer.parseInt(preCube[1].get(cubeIdx).toString());
      currOffset = 0;
      // get offset for i cell in the cube
      preOffset = Integer.parseInt(preCube[0].get(cubeIdx).toString());
      for (int preHeadIdx = 0; preHeadIdx < preColumnList.size(); preHeadIdx++) {
        dimensionSize = 1;
        tmpOffset = 1;
        for (int preMoveIdx = preHeadIdx+1; preMoveIdx < preColumnList.size(); preMoveIdx++) {
          if (preMapArray[preMoveIdx].size() == 0) {
            System.out.println("the column is " + columnList.get(preMoveIdx));
          }
          dimensionSize = dimensionSize * preMapArray[preMoveIdx].size();
        }
        if (dimensionSize == 0) {
            System.out.println("the column is " + columnList.get(preHeadIdx));
        }
        preIdx = preOffset / dimensionSize;
        preOffset = preOffset - (dimensionSize * preIdx);
        // Should this column be included in the new cube?
        for (int currHeadIdx = 0; currHeadIdx < currColumnList.size(); currHeadIdx++) {
          tmpOffset = 1;
          match = false;
          if (currColumnList.get(currHeadIdx).equals(preColumnList.get(preHeadIdx))) {
            currIdx = preIdx;
            for (int currMoveIdx = currHeadIdx+1; currMoveIdx < currColumnList.size(); currMoveIdx++) {
              tmpOffset = tmpOffset * currMapArray[currMoveIdx].size();
            }
            tmpOffset = tmpOffset * currIdx;
            match = true;
            // last column for this row
            if (currHeadIdx == currColumnList.size() - 1) {
              tmpOffset = currIdx;
            }
            break;
          }
        }
        if (match) {
          currOffset = currOffset + tmpOffset;
        }
      }
      if (currOffset >= 0) {
        updateCubeCell(currCube,currOffset,currCnt);
      }
    }
    saveCube(currCube, currMapArray, currColumnList);
    // when reach 1 item_set, the first branch of the tree is finished
    if (currColumnList.size()==1) {
      firstBranch = false;
    }
    createCompList(currCube, currMapArray, currColumnList);
  }

  protected void copyArrayList(ArrayList origArray, ArrayList newArray) {
    for (int i=0; i < origArray.size(); i++) {
      newArray.add(origArray.get(i));
    }
  }

  protected boolean isDone(ArrayList newList) {
    boolean done = false;
    int ruleSize = newList.size();
    for (int doneIdx = 0; doneIdx < doneList[ruleSize].size(); doneIdx++) {
          if (((ArrayList)doneList[ruleSize].get(doneIdx)).equals(newList)) {
            done = true;
            break;
          }
          else {
            done = false;
          }
    }
    return (done);
  }



  protected boolean isChosenColumn(String columnName) {
    boolean match = false;
    for (int colIdx = 0; colIdx < columnList.size(); colIdx++) {
      if (columnName.equals(columnList.get(colIdx))) {
        match = true;
        break;
      }
    }
    return(match);
  }

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "SQL Array Cube";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "input0";
			case 1:
				return "input1";
			case 2:
				return "input2";
			case 3:
				return "input3";
			default: return "NO SUCH INPUT!";
		}
	}

	/**
	 * Return the human readable name of the indexed output.
	 * @param index the index of the output.
	 * @return the human readable name of the indexed output.
	 */
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "output0";
			case 1:
				return "output1";
			default: return "NO SUCH OUTPUT!";
		}
	}
}
