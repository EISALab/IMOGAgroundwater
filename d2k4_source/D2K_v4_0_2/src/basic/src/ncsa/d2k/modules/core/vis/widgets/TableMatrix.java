package ncsa.d2k.modules.core.vis.widgets;

import ncsa.d2k.modules.core.datatype.table.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

/**
   VerticalTableMatrix is a JTable inside a JScrollPane.  The contents
   of a VerticalTable are displayed in the JTable.  The first column
   displays the row number.  This component will set its preferred size
   up to a maximum of 500x500.
   @author David Clutter
*/
public class TableMatrix extends JScrollPane {
   /** the gui table that is shown */
   protected JTable jTable = null;
   /** the table model for the JTable */
   protected TableViewerModel tm = null;
   /** The header column */
   protected JTable headerColumn = null;

   protected int MAX_WIDTH = 500;
   protected int MAX_HEIGHT = 500;

   /**
      Create a new VerticalTableMatrix
      @param table the VerticalTable to display
   */
   public TableMatrix(Table table) {
       tm = new TableViewerModel(table);
      init();
   }

   /**
      Create a new VerticalTableMatrix
      @param table the VerticalTable to display
      @param maxFractionDigits the maximum fraction digits to be displayed
   */
   public TableMatrix(Table table, int maxFractionDigits) {
       tm = new TableViewerModel(table, maxFractionDigits);
      init();
   }

   /**
      Create a new VerticalTableMatrix
      @param mdl the table model for the JTable
   */
   public TableMatrix(TableViewerModel mdl) {
      tm = mdl;
      init();
   }

   /**
      Set up the table and the row headers.
   */
   protected void init() {
      TableColumnModel cm = new DefaultTableColumnModel() {
         boolean first = true;
         public void addColumn(TableColumn tc) {
            if(first) { first = false; return; }
            tc.setMinWidth(75);
            super.addColumn(tc);
         }
      };

      // setup the columns for the row header table
      TableColumnModel rowHeaderModel = new DefaultTableColumnModel() {
         boolean first = true;
         public void addColumn(TableColumn tc) {
            if(first) {
               tc.setMinWidth(50);
               super.addColumn(tc);
               first = false;
            }
         }
      };

      // setup the row header table
      jTable = new JTable(tm, cm);
      jTable.createDefaultColumnsFromModel();

      headerColumn = new JTable(tm, rowHeaderModel);
      headerColumn.createDefaultColumnsFromModel();
      jTable.setSelectionModel(headerColumn.getSelectionModel());

      // set the width of the header column to be the width
      // of a label containing the largest row number
      JLabel tempLabel = new JLabel(Integer.toString(tm.getRowCount()-1));
      tempLabel.setFont(jTable.getFont());
      int wid = (int) tempLabel.getPreferredSize().getWidth();
      headerColumn.getColumnModel().getColumn(0).setPreferredWidth(wid);

      // set the row headers
      JViewport jv = new JViewport();
      jv.setView(headerColumn);
      jv.setPreferredSize(headerColumn.getPreferredSize());

      // add the table to this scroller
      setViewportView(jTable);
      // add the row header to this scroller
      setRowHeader(jv);

      Dimension d = calcPreferredScrollableViewportSize();

      jTable.getTableHeader().setReorderingAllowed(false);
      jTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
      jTable.setPreferredScrollableViewportSize(d);
      setHorizontalScrollBarPolicy(
         JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
      setVerticalScrollBarPolicy(
         JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
   }

   /**
      Calculate the size of the scrollable area.
      @return the size of the scrollable area
   */
   protected Dimension calcPreferredScrollableViewportSize() {
      // make the preferred width of the table inside this scroller
      // to be either the width of all the columns, or MAX_WIDTH,
      // whichever is smaller
      int rowWidth = jTable.getColumnModel().getColumn(0).getPreferredWidth();
      int wid = 0;
      for(int i = 0; i < tm.getColumnCount()-1; i++) {
         if((wid+rowWidth) > MAX_WIDTH)
            break;
         wid += rowWidth;
      }
      // make the preferred height of the table inside this scroller
      // to be either the height of all the rows, or MAX_HEIGHT,
      // whichever is smaller
      int hei = 0;
      int rowHeight = jTable.getRowHeight();
      for(int i = 0; i < tm.getRowCount(); i++) {
         if((hei+rowHeight) > MAX_HEIGHT)
            break;
         hei += rowHeight;
      }
      return new Dimension(wid, hei);
   }

   /**
      Get the selected rows of the table.
      @return the selected rows of the table
   */
   public int[] getSelectedRows() {
      return jTable.getSelectedRows();
   }

   /**
      Get the selected columns of the table.
      @return the selected columns of the table
   */
   public int[] getSelectedColumns() {
      return jTable.getSelectedColumns();
   }

   /**
      Get a reference to the JTable.
      @return the JTable inside this scroll pane
   */
   public JTable getJTable() {
      return jTable;
   }
}
