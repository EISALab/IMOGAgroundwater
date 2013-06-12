package ncsa.d2k.modules.core.io.sql;

/**
 * <p>Title: CheckBoxRenderer</p>
 * <p>Description: Renderer for Checkbox inside of a JTable</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @Dora Cai
 * @version 1.0
 */

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

public class CheckBoxRenderer extends JCheckBox implements TableCellRenderer {
    public CheckBoxRenderer() {
      super();
      setHorizontalAlignment(0);
    }

    /**
     * Override this method from the parent class.
     * Only paint the background if the row isn't selected
     * @param table the JTable component
     * @param value the cell content's object
     * @param isSelected boolean so we know if this is the currently selected row
     * @param hasFocus does this cell currently have focus?
     * @param row the row number
     * @param column the column number
     */
    public Component getTableCellRendererComponent(JTable table,
                                                   java.lang.Object value,
                                                   boolean isSelected,
                                                   boolean hasFocus, int row,
                                                   int column) {
      Color c = table.getBackground();
      this.setBackground(c);
      setSelected(value != null && ( (Boolean) value).booleanValue());
      return this;
    }
  }
