package ncsa.d2k.modules.core.io.sql;

/**
 * <p>Title: </p>
 * <p>Description: This class obtains a ResultSetTableModel for the query
 * and uses it ot display the results of the query in a scrolling JTable
 * component </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: NCSA ALG </p>
 * @author Dora Cai
 * @version 1.0
 */
import ncsa.d2k.core.modules.*;
import ncsa.d2k.core.modules.UserView;

import ncsa.d2k.userviews.swing.*;

import ncsa.gui.Constrain;
import ncsa.d2k.gui.*;

import java.sql.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.JTable;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.*;

public class BrowseTablesView extends JD2KFrame
   implements ActionListener
{
  BrowseTables bt;
  JTable aTable; // The table for displaying available database tables
  JButton okBtn; // Button for choosing a table
  int selectedRow;
  TableModel model;
  JOptionPane msgBoard = new JOptionPane();
  String chosenTableName;
  // This class has two constructors. One is used for general queries, which takes
  // a query as the input. Another is used for listing table name or column name,
  // which takes a vector as the input.
  /**
   * Constructor
   * @param t The BrowseTables object
   * @param query The SQL query to retrieve database table definition
   */
  public BrowseTablesView(BrowseTables t, String query)
  {
    // remember the BrowseTables object that was passed to us
    this.bt = t;
    // create the Swing components we'll be using
    aTable = new JTable();
    aTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    ListSelectionModel rowSM = aTable.getSelectionModel();
    rowSM.addListSelectionListener(new ListSelectionListener() {
      /**
       * Detect a row has been selected
       * @param e The SelectionEvent listener
       */
      public void valueChanged(ListSelectionEvent e)
      {
        ListSelectionModel lsm =
                    (ListSelectionModel)e.getSource();
        if (lsm.isSelectionEmpty())
        {
          selectedRow = -1;
          System.out.println("No Selected Rows........");
        }
        else
        {
          selectedRow = lsm.getMinSelectionIndex();
          // System.out.println("Selected Row........"+selectedRow);
        }
      }
    });
    // Place the components within this window
    Container contentPane = getContentPane();
    contentPane.add(new JScrollPane(aTable), BorderLayout.CENTER);
    okBtn = new JButton("OK");
    okBtn.setSize(5,5);
    okBtn.addActionListener(this);
    contentPane.add(okBtn,BorderLayout.SOUTH);
    displayQueryResults(query.toString());
  }

  /**
   * Constructor
   * @param t The BrowseTables object
   * @param result The result set for meta queries, such as the list of tables or columns
   */
  public BrowseTablesView(BrowseTables t, Vector result)
  {
    // remember the BrowseTables object that was passed to us
    this.bt = t;
    // create the Swing components we'll be using
    aTable = new JTable();
    aTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    ListSelectionModel rowSM = aTable.getSelectionModel();
    rowSM.addListSelectionListener(new ListSelectionListener() {
      /**
       * Detect a row has been selected
       * @param e The SelectionEvent listener
       */
      public void valueChanged(ListSelectionEvent e)
      {
        ListSelectionModel lsm =
                    (ListSelectionModel)e.getSource();
        if (lsm.isSelectionEmpty())
        {
          selectedRow = -1;
          System.out.println("No Selected Rows........");
        }
        else
        {
          selectedRow = lsm.getMinSelectionIndex();
          // System.out.println("Selected Row........"+selectedRow);
        }
      }
    });
    // Place the components within this window
    Container contentPane = getContentPane();
    contentPane.add(new JScrollPane(aTable), BorderLayout.CENTER);
    okBtn = new JButton("OK");
    okBtn.setSize(5,5);
    okBtn.addActionListener(this);
    contentPane.add(okBtn,BorderLayout.SOUTH);
    displayQueryResults(result);
  }

  /**
   * This method uses the supplied SQL query string, and the BrowseTables
   * object to create a TableModel that holds the results of the database query.
   * It passes that TableModel to the JTable component for display
   * @param q The SQL query string to use
  */
  public void displayQueryResults(final String q)
  {
      try {
      // Use the BrowseTables object to obtain a TableModel object for
      // the query results and display that model in the JTable component.
        aTable.setModel(bt.getResultSetTableModel(q));
      }
      catch (SQLException ex) {
        JOptionPane.showMessageDialog(msgBoard,
          "SQL error: " + ex.getMessage(), "Error",
          JOptionPane.ERROR_MESSAGE);
        System.out.println("SQL error in displayQueryResults for query.");
      }
  } /* end of displayQueryResult */

  /**
   * This method uses the supplied ResultSet, and the BrowseTables
   * object to create a TableModel that holds this ResultSet.
   * It passes that TableModel to the JTable component for display
   * @param rs The ResultSet to use
  */
  public void displayQueryResults(final Vector v)
  {
      // Use the BrowseTables object to obtain a TableModel object for
      // the query results and display that model in the JTable component.
    try {
      aTable.setModel(bt.getResultSetTableModel(v));
    }
    catch (SQLException ex) {
      JOptionPane.showMessageDialog(msgBoard,
        "SQL error: " + ex.getMessage(), "Error",
        JOptionPane.ERROR_MESSAGE);
      System.out.println("SQL error in displayQueryResults for ResultSet.");
    }
  } /* end of displayQueryResult */

  /**
   * Event trigger
   * @param e The event detected
   */
  public void actionPerformed(ActionEvent e)
  {
    Object src = e.getSource();
    if (src == okBtn)
    {
      dispose();
    }
  }
  /**
   * Get the table name a user has selected
   * @return The table name a user has selected
   */
  public String getChosenRow()
  {
      model = aTable.getModel();
      chosenTableName = model.getValueAt(selectedRow,0).toString();
      return (chosenTableName);
  }

  /** Get the index of the selected row
   *  @return the row index
   */
   public int getSelectedRow() {
     return (this.selectedRow);
   }

}
