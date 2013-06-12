package ncsa.d2k.modules.core.io.sql;

/**
 * <p>Title: MetaTableModel</p>
 * <p>Description: This model create a table to display meta information for
 * a database table</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: NCSA ALG</p>
 * @author Dora Cai
 * @version 1.0
 */
import ncsa.d2k.core.modules.*;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.TableModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.JScrollPane;

public class MetaTableModel extends AbstractTableModel
{
  Object data[][];
  String columnNames []={"Column Name","Data Type","Column Length"};
  boolean edit;
  static String  NOTHING = "";
  /**
   * Table Model for displaying meta data: column name, column type and column length
   * @param maxRow Maximum number of rows in the table
   * @param maxCol Maximum number of columns in the table
   * @param editable Is this table editable?
   */
  public MetaTableModel(int maxRow, int maxCol, boolean editable)
  {
    edit = editable;
    data = new Object[maxRow][maxCol];
    initTableModel(maxRow, maxCol);
  }
  /**
   * Initialize the table model
   * @param maxR Maximum number of rows in the table
   * @param maxC Maximum number of columns in the table
   */
  public void initTableModel(int maxR, int maxC)
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
    if ((col == 0 || col == 1) && edit == true)
      return true;
    else if (col == 2 && edit == true &&
            (data[row][1].toString().equals("string")||
             data[row][1].toString().equals("byte[]")||
             data[row][1].toString().equals("char[]")))
      return true;
    else // Length column is not editable for numeric and boolean column
      return false;
  }
  /**
   * Set value at a cell
   * @param value The value to set
   * @param row The row index
   * @param col The column index
   */
  public void setValueAt(Object value, int row, int col)
  {
    boolean pass = validateData(value, row, col);
    if (pass) // pass the validation
    {
      if (col == 0) // first column value can't contain space and minus sign
      {
         value = squeezeSpace(value);
         // column name does not allow "-"
         value = value.toString().replace('-','_');
      }
      data[row][col] = value.toString().toLowerCase();
      fireTableCellUpdated(row, col);
    }
  } /* end of setValueAt */

  /**
   * Validate data
   * @param value The value to validate
   * @param row The row index
   * @param col The column index
   * @return pass or fail the validation
   */
  public boolean validateData(Object value, int row, int col)
  {
    JOptionPane msgBoard = new JOptionPane();
    // Must fill the previous row before move to a new row
    if (row > 0)
    {
      int i;
      for (i=0; i<3; i++)
      {
        if (data[row-1][i].toString().equals(NOTHING))
        {
          switch(i)
          {
            case 0:
               JOptionPane.showMessageDialog(msgBoard,
               "You must give a column name for the previous row.", "Error",
               JOptionPane.ERROR_MESSAGE);
               return(false);
            case 1:
               JOptionPane.showMessageDialog(msgBoard,
               "You must choose a data type for the previous row.", "Error",
               JOptionPane.ERROR_MESSAGE);
               return(false);
            case 2: // length must be specified for varchar datatype
              if (data[row-1][1].toString().equals("String"))
              {
                 JOptionPane.showMessageDialog(msgBoard,
                 "You must specify the length of String for the previous row.", "Error",
                 JOptionPane.ERROR_MESSAGE);
                 return(false);
              }
              else
                return(true);
          } /* end of switch */
        } /* end of if (data[row-1][i] == NOTHING */
      } /* end of for */
    } /* end of if (row > 0) */
    return (true);
  } /* end of validateData */

  /**
   * Squeeze out spaces from the string value
   * @param value The string value to edit
   * @return The object after spaces are squeezed out
   */
  public Object squeezeSpace(Object value)
  {
    int j;
    String strValue = value.toString();
    String newStr = NOTHING;
    for (j=0; j<value.toString().length();j++)
    {
      if (strValue.charAt(j)!=' ')
      newStr = newStr + strValue.charAt(j);
    }
    value = (Object)newStr;
    return(value);
  }
} /* end of MetaTableModel */
