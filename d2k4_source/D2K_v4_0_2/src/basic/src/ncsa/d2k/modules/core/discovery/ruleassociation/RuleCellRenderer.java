package ncsa.d2k.modules.core.discovery.ruleassociation;
import java.awt.*;

import javax.swing.*;
import javax.swing.table.*;

/**
	Render a rule, the ifs and thens are represented by some
	appropriate graphic.
*/
class RuleCellRenderer extends DefaultTableCellRenderer  {
	private String what = null;
	int myrow, mycolumn;
	int myheight = 0;
	int mywidth = 0;
	/**
		the size passed in is the height of the labels, the rows here
		need to be the same height.
		@param i the height of the labels.
	*/
	Image littleX = null;
	Image littleCheck = null;
	RuleCellRenderer (int i, int j, Image x, Image check) {
		super ();
		myheight = i;
		mywidth = j;
		littleX = x;
		littleCheck = check;
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

	/**
		The superclass is responsible for painting the first column and the header,
		we paint the first row as a bar display indicating the confidence value for
		the rule, the second row as the support value, also represented as a bar, and
		the following rows are represent each of the possible values that can be represented
		in the rule. Antecedents are represented as a check and the results are presented as
		an x.
		@param g the graphics object.
	*/
	static final int BAR_WIDTH = 10;
	public void paintComponent (Graphics g) {

		// First fill the backgound, alternating color from column to column
		Color tootoo = null;
		if ((mycolumn % 2) == 0)
			tootoo = RuleVis.RULE_VIS_BACKGROUND;
		else
			tootoo = new Color (255, 255,255);

		g.setColor (tootoo);
		g.fillRect (0, 0, this.getSize ().width, this.getSize ().height);

		// Paint the component.
		Color c = null;
		Polygon p = null;
		if (what.equals ("If")) {
			int x = (this.getSize().width - littleX.getWidth (this))/2;
			int y = (this.getSize().height - littleX.getHeight (this))/2;
			g.drawImage(littleCheck, x, y, null);
		}  else if (what.equals ("Then")) {
			int x = (this.getSize().width - littleCheck.getWidth (this))/2;
			int y = (this.getSize().height - littleX.getHeight (this))/2;
			g.drawImage(littleX, x, y, null);
		}
	}
}

