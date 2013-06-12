package ncsa.d2k.modules.core.vis.widgets;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.gui.ErrorDialog;

import javax.swing.JComboBox;

/**
   This VerticalTable model allows the cells in the table to be edited.
   @author David Clutter
*/
public class TableEditorModel extends TableViewerModel {

   JComboBox []dataTypes;
   static String DATATYPE = "type";

   /**
      Constructor.
      @param t the table to display
      @param b an array of combo boxes to display in the first row
   */
   public TableEditorModel(MutableTable t, JComboBox[] b) {
      super(t);
      dataTypes = b;
   }

   /**
      Constructor which takes a maximum number of fraction digits to be returned
      by <code>getValueAt</code>.
      @param t the table to display
      @param b an array of combo boxes to display in the first row
      @param maxFractionDigits the maximum number of fraction digits
   */
   public TableEditorModel(MutableTable t, JComboBox[] b, int maxFractionDigits) {
      super(t, maxFractionDigits);
      dataTypes = b;
   }

   /**
      There is one extra row, for the combo boxes.
      @return the number of rows in the table
   */
   public int getRowCount() {
      return table.getNumRows()+1;
   }

   /**
      Return the Object that goes in a particular spot in the table.
      @param row the row of the table to index
      @param column the column of the table to index
      @return the object at table[row][col]
   */
   public Object getValueAt(int row, int col) {
      if(row == 0) {
         if(col == 0)
            return DATATYPE;
         return dataTypes[col-1].getSelectedItem();
      }
      if(col == 0)
         return new Integer(row-1);

      if (table.isValueMissing(row-1, col-1))
          return "?";

      if (numberFormat != null && maxFractionDigits >= 0) {

         String orig = table.getString(row-1, col-1);
         String fmtd = null;

         try {
            fmtd = numberFormat.format(numberFormat.parse(orig));
         }
         catch(Exception e) { return orig; }

         return fmtd;

      }
      else {

         return table.getString(row-1, col-1);

      }

      // return table.getString(row-1, col-1);

   }

   /**
      All cells in this table are editable.
      @param r the row
      @param c the column
   */
   public boolean isCellEditable(int r, int c) {
      if(c == 0)
         return false;
      return true;
   }

   /**
      Set the item in the table at (row, col) to value.  The
      setString() method is called on the table, so the
      Column is responsible for changing value to a type
      appropriate for the column.
      @param value the new value
      @param row the row of the table
      @param col the column of the table
   */
   public void setValueAt(Object value, int row, int col) {
      if(row == 0)
         return;
      try {

         if (value.toString().equals("?") || value.toString().trim().length() == 0)
            ((MutableTable)table).setValueToMissing(true, row-1, col-1);
         else {
            ((MutableTable)table).setString(value.toString(), row-1, col-1);
            ((MutableTable)table).setValueToMissing(false, row-1, col-1);
         }

      }
      catch(Exception e) {
         ErrorDialog.showDialog("Could not set the value at "+
            (row-1)+","+(col-1)+" to "+value.toString(),
            e.toString());
      }
   }
}
