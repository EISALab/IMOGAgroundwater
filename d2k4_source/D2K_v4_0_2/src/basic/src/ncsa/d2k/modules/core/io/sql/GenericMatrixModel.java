package ncsa.d2k.modules.core.io.sql;

/**
 * <p>Title: GenericMatrixModel</p>
 * <p>Description: This model create a table which has the number of column and rows
 * based on pass-in parameters
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: NCSA ALG</p>
 * @author Dora Cai
 * @version 1.0
 */
import ncsa.d2k.core.modules.*;
//import ncsa.d2k.modules.core.datatype.table.*;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.TableModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.JScrollPane;

public class GenericMatrixModel extends AbstractTableModel {
  Object data[][];
  String columnNames [];
  boolean edit;
  static String  NOTHING = "";
  /**
   * Table Model for displaying Bin information: attribute name, type, number of bins, input or output
   * @param maxRow Maximum number of rows in the table
   * @param maxCol Maximum number of columns in the table
   * @param editable Is this table editable?
   */
  public GenericMatrixModel(int maxRow, int maxCol, boolean editable, String [] columnHeading)
  {
    edit = editable;
    data = new Object[maxRow][maxCol];
    columnNames = columnHeading;
    initMatrixModel(maxRow, maxCol);
  }
  /**
   * Initialize the table model
   * @param maxR Maximum number of rows in the table
   * @param maxC Maximum number of columns in the table
   */
  public void initMatrixModel(int maxR, int maxC)
  {
    for (int iRow=0; iRow<maxR; iRow++)
    {
      for (int iCol=0; iCol<maxC; iCol++)
      {
        data[iRow][iCol] = NOTHING;
        fireTableCellUpdated(iRow, iCol);
      }
    }
  }
  /**
   * Get row count of the table
   * @return The number of rows in the table
   */
  public int getRowCount()
  {
    return data.length;
  }
  /**
   * Get column count of the table
   * @return The number of columns in the table
   */
  public int getColumnCount()
  {
    return columnNames.length;
  }
  /**
   * Get the name of the column
   * @param col The column index
   * @return The name of the column
   */
  public String getColumnName (int col)
  {
    return columnNames[col];
  }
  /**
   * Get the value of a cell
   * @param row The row index
   * @param col The column index
   * @return The object in a cell
   */
  public Object getValueAt(int row, int col)
  {
    return data[row][col];
  }
  /**
   * Is the cell editable?
   * @param row The row index
   * @param col The column index
   * @return true or false
   */
  public boolean isCellEditable (int row, int col)
  {
      return edit;
  }
  /**
   * Set value at a cell
   * @param value The value to set
   * @param row The row index
   * @param col The column index
   */
  public void setValueAt(Object value, int row, int col)
  {
      data[row][col] = value.toString();
      fireTableCellUpdated(row, col);
  } /* end of setValueAt */


} /* end of GenericMatrixModel */