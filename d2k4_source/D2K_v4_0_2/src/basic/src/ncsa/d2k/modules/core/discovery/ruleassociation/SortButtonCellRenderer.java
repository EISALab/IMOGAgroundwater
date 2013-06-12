package ncsa.d2k.modules.core.discovery.ruleassociation;
import java.awt.*;

import javax.swing.*;
import javax.swing.table.*;

/**
	Render a rule, the ifs and thens are represented by some
	appropriate graphic.
*/
class SortButtonCellRenderer extends DefaultTableCellRenderer  {
	//JButton confidence;
	//JButton support;
	JRadioButton confidence;
	JRadioButton support;

	/**
	 * Given the buttons we will return from the getCellRenderer.
	 * @param c the confidence button.
	 * @param s the support button.
	 */
	//SortButtonCellRenderer (JButton c, JButton s) {
	SortButtonCellRenderer (JRadioButton c, JRadioButton s) {
		super ();
		this.confidence = c;
		this.support = s;
	}

	/**
	 * We simply return the correct button, confidence or support.
	 * @param table
	 * @param value
	 * @param isSelected
	 * @param hasFocus
	 * @param row
	 * @param column
	 * @return
	 */
	public Component getTableCellRendererComponent(JTable table, Object value,
						boolean isSelected, boolean hasFocus,
						int row, int column)
	{
		if (row == 0)
			return this.confidence;
		else
			return this.support;
	}
}

