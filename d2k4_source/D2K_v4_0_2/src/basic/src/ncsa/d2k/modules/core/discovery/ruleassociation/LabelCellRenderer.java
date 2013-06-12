package ncsa.d2k.modules.core.discovery.ruleassociation;
import java.awt.*;

import javax.swing.*;
import javax.swing.table.*;

/**
	Renders the confidence and support values, They are represented
	by a vertical bar.
*/
class LabelCellRenderer extends DefaultTableCellRenderer  {
	private String what = null;
	int myrow, mycolumn;

	/**
		the size passed in is the height of the labels, the rows here
		need to be the same height.
		@param i the height of the labels.
	*/
	LabelCellRenderer () {
		super ();
		this.setHorizontalAlignment (JLabel.RIGHT);
	}

    /**
		Returns the component used for drawing the cell.  This method is
		used to configure the renderer appropriately before drawing.

		@param	table		the <code>JTable</code> that is asking the
						renderer to draw; can be <code>null</code>
		@param	value		the value of the cell to be rendered.  It is
					up to the specific renderer to interpret
					and draw the value.  For example, if
					<code>value</code>
					is the string "true", it could be rendered as a
					string or it could be rendered as a check
					box that is checked.  <code>null</code> is a
					valid value

		 @param	isSelected	true if the cell is to be rendered with the
					selection highlighted; otherwise false
		 @param	hasFocus	if true, render cell appropriately.  For
					example, put a special border on the cell, if
					the cell can be edited, render in the color used
					to indicate editing
		 @param	row	        the row index of the cell being drawn.  When
					drawing the header, the value of
					<code>row</code> is -1
		 @param	column	        the column index of the cell being drawn

     */

    public Component getTableCellRendererComponent(JTable table, Object value,
					    boolean isSelected, boolean hasFocus,
					    int row, int column)
	{
		this.myrow = row;
		this.mycolumn = column;
		this.what = (String) value;
		return super.getTableCellRendererComponent (table, value, isSelected, hasFocus,
			row, column);
	}
}

