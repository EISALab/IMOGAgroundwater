package ncsa.d2k.modules.core.io.sql;

/**
 * <p>Title: CheckBoxTableModel</p>
 * <p>Description: Table model for Checkbox in JTable</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @Dora
 * @version 1.0
 */


import javax.swing.table.*;
import java.util.Vector;
import java.util.*;

public class CheckBoxTableModel extends AbstractTableModel {

    /** vector to store the data in the columns */
   protected Vector data;
    /** col names */
   //protected String[] cols;
   private final static String[] cols = new String[] {
             "Selected Attributes", "Input", "Output"};
    /** constructor */
   public CheckBoxTableModel() {
      super();
      data = new Vector();
   }

    /**
      * Returns the type of object in the column
      * @param c the column
      * @return the type of class
      */
   public Class getColumnClass(int c) {
      return getValueAt(0,c).getClass();
   }
    /**
      * Returns the total number of rows
      * @return the # of rows
      */
   public int getRowCount() { return data.size();}

    /**
      * Clears all rows in the table
      */
   public void clear() {
      data.removeAllElements();
      fireTableDataChanged();
   }
    /**
      * Removes a row in the table
      * @param i the row number
      */
   public void removeRow( int i ) {
      data.removeElementAt(i);
      fireTableRowsDeleted(i,i);
   }
    /**
      * Returns the total number of columns in the table
      * @return the # of cols
      */
   public int getColumnCount() { return cols.length; }
    /**
      * Returns the column name for a given column
      * @param i the column
      * @return the name of that column
      */
   public String getColumnName(int i) { return cols[i]; }

    /**
      * Returns the object in the given coordinates
      * may be overridden by methods that extend this class
      * @param row the row number
      * @param col the column number
      * @return the object in that row/col
      */
   public Object getValueAt(int row, int col) {
      Object[] o = (Object[])data.elementAt(row);
      return o[col];
   }

   /**
     * Sets the value in our data model
     * We know to expect only boolean/checkbox values here
     * @param value the object to set
     * @param row the row number to set the data
     * @param col the column number to set the data
     */
  public void setValueAt(Object value, int row, int col) {

      // get the array for the proper row
     Object o[] = (Object[]) data.elementAt(row);

      // replace the boolean object that changed
     o[col] = value;

     // only either input or output can be set to true
     if (((Boolean) value).booleanValue()) {
       if (col == 1) {
         Object newValue = (Object) new Boolean(false);
         setValueAt(newValue, row, 2);
       }
       else if (col == 2) {
         Object newValue = (Object) new Boolean(false);
         setValueAt(newValue, row, 1);
       }
       fireTableDataChanged();
     }

  }

    /**
      * Add a row to the table... if the entry is null, stick an empty string
      * in it's place, otherwise the table throws NullPointers
      * @param o an arrow of objects making 1 row of the table
      */
   public void addRow(Object o[]) {
      for( int i=0; i<o.length; i++ ) {
         if( o[i] == null ) {
            o[i] = new String();
         }
      }
      data.addElement(o);
       // entry added to the end, so fire an event to notify the table
       // to update itself
      fireTableRowsInserted(data.size()-1,data.size()-1);
   }

   /**
        * Must override this function.  This allows users to select the check in column 1 and
        * no other elements in the table
        * @param row the row to check if editable
        * @param col the column to check if editable
        * @return true/false whether we want the user to be able to modify that cell in
        *   the table
        */
     public boolean isCellEditable( int row, int col ) {
        if( col == 1 || col == 2) return true;
        else return false;
     }
}
