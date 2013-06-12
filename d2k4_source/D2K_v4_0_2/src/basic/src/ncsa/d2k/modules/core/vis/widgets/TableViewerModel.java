package ncsa.d2k.modules.core.vis.widgets;

import java.text.NumberFormat;

import javax.swing.table.AbstractTableModel;

import ncsa.d2k.modules.core.datatype.table.Table;

/**
   VerticalTableModel is the table model for the VerticalTableViewer.
   This is a very simple implementation.  All cells are uneditable in this
   model.  A subclass may allow cells in the table to be edited, however.
   @author David Clutter
*/
public class TableViewerModel extends AbstractTableModel {
   /** The VerticalTable that holds the data */
   protected Table table;

   protected int maxFractionDigits;
   protected NumberFormat numberFormat;

   /**
      Constructor.
      @param vt The VerticalTable that we represent
   */
   public TableViewerModel(Table vt) {
      table = vt;

      maxFractionDigits = -1;
   }

   /**
      Constructor, setting the maximum number of fraction digits to be returned
      by <code>getValueAt</code>.
      @param vt The VerticalTable that we represent
      @param maxFractionDigits the maximum number of fraction digits
   */
   public TableViewerModel(Table vt, int maxFractionDigits) {
      table = vt;

      this.maxFractionDigits = maxFractionDigits;
      numberFormat = NumberFormat.getInstance();
      numberFormat.setMaximumFractionDigits(maxFractionDigits);
   }

   /**
      Return the name of a column in the table.  This returns the label
      of the column from the VerticalTable.
      @param col The column we are interested in.
   */
   public String getColumnName(int col) {
      if(col == 0)
         return "";
      return table.getColumnLabel(col-1);
   }

   /**
      Return the number of rows in the table.
      @return the number of rows in the table.
   */
   public int getRowCount() {
      return table.getNumRows();
   }

   /**
      Return the number of columns in the table.
      @return the number of columns in the table.
   */
   public int getColumnCount() {
      return table.getNumColumns()+1;
   }

   // getColumnClass is overridden for the purpose of right-justifying text
   // in numeric columns.
   public Class getColumnClass(int col) {

      if (col == 0) {
         return super.getColumnClass(col);
      }

      if (table.isColumnNumeric(col - 1)) {
         return Integer.class;
      }
      else {
         return String.class;
      }

   }

   /**
      Return the Object that goes in a particular spot in the table.
      @param row the row of the table to index
      @param column the column of the table to index
      @return the object at table[row][col]
   */
   public Object getValueAt(int row, int col) {
      if(col == 0)
         return new Integer(row);

      if (table.isValueMissing(row, col-1))
          return "?";

      try {

         if (numberFormat != null && maxFractionDigits >= 0) {

            String orig = table.getString(row, col - 1);
            String fmtd = null;

            try {
               fmtd = numberFormat.format(numberFormat.parse(orig));
            }
            catch(Exception e) { return orig; }

            return fmtd;

         }
         else {

            return table.getString(row, col - 1);

         }

      }
      catch (NullPointerException e){
         return "";
      }
   }

   /**
      Return whether a particular cell is editable or not.
      Always returns false.
      @param row the row of the table to index
      @param col the column of the table to index
   */
   public boolean isCellEditable(int row, int col) {
      return false;
   }
}
