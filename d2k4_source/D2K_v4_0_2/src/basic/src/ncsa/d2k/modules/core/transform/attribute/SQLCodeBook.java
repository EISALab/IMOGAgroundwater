package ncsa.d2k.modules.core.transform.attribute;

/**
 * <p>Title: SQLcodeBook</p>
 * <p>Description: Get a code book from a database table. </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: NCSA ALG</p>
 * @author Dora Cai
 * @version 1.0
 */

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

import javax.swing.JOptionPane;

import ncsa.d2k.modules.core.datatype.table.basic.Column;
import ncsa.d2k.modules.core.datatype.table.basic.MutableTableImpl;
import ncsa.d2k.modules.core.datatype.table.basic.ObjectColumn;
import ncsa.d2k.modules.core.datatype.table.basic.TableImpl;
import ncsa.d2k.modules.core.io.sql.ConnectionWrapper;


public class SQLCodeBook {
  JOptionPane msgBoard = new JOptionPane();
  Connection con;
  public TableImpl codeBook;
  HashMap map = new HashMap();

  public SQLCodeBook(ConnectionWrapper cw, String bookName) {
    codeBook = getCodeBook(cw, bookName);
  }

  public TableImpl getCodeBook(ConnectionWrapper cw, String bookName) {
    int codeCount = 0;
    MutableTableImpl book = null;
    try {
      con = cw.getConnection();
      String countQry = new String("select count(*) "  + " from " + bookName);
      //System.out.println("countQry is " + countQry);
      Statement countStmt = con.createStatement();
      ResultSet countSet = countStmt.executeQuery(countQry);
      while (countSet.next()) {
        codeCount = countSet.getInt(1);
      }
      //System.out.println("codeCount is " + codeCount);
      countStmt.close();
    }
    catch (Exception e){
      JOptionPane.showMessageDialog(msgBoard,
      e.getMessage(), "Error",
      JOptionPane.ERROR_MESSAGE);
      System.out.println("Error to get count in getCodeBook.");
    }

    if (codeCount > 0) {
      Column[] cols = new Column[4];
      cols[0] = new ObjectColumn(codeCount);
      cols[1] = new ObjectColumn(codeCount);
      cols[2] = new ObjectColumn(codeCount);
      cols[3] = new ObjectColumn(codeCount);
      cols[0].setLabel("Attribute");
      cols[1].setLabel("Code");
      cols[2].setLabel("Description");
      cols[3].setLabel("ItemIdx");
      book = new MutableTableImpl(cols);
      try {
        String codeQry = new String("select attribute_name, code, description from " + bookName);
        //System.out.println("codeQry is " + codeQry);
        Statement codeStmt = con.createStatement();
        ResultSet codeSet = codeStmt.executeQuery(codeQry);
        int row=0;
        while (codeSet.next()) {
          book.setString(codeSet.getString(1),row, 0);
          book.setString(codeSet.getString(2),row, 1);
          book.setString(codeSet.getString(3),row, 2);
          String codeLabel = codeSet.getString(1)+"="+codeSet.getString(2);
          map.put(codeLabel, new Integer(row));
          row++;
        }
      }
      catch (Exception e){
        JOptionPane.showMessageDialog(msgBoard,
        e.getMessage(), "Error",
        JOptionPane.ERROR_MESSAGE);
        System.out.println("Error to get code in getCodeBook.");
      }
    }
    return book;
  }

  /** Get the row count from the code table
   *  @return number of rows
   */

  public int getCodeCount() {
    return (codeBook.getNumRows());
  }


  public String getDescription(String codeLabel) {
    if (!map.containsKey(codeLabel)) {
      return null;
    }
    else {
      Integer ii = (Integer)map.get(codeLabel);
      return (codeBook.getString(ii.intValue(),2));
    }
  }
}