package ncsa.d2k.modules.core.vis.widgets;

import java.awt.*;
import java.awt.geom.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import java.text.NumberFormat;

/**
 * 	A ScatterPlot that uses a gradient coloring to paint its points.
 *	Points that lie on the left side of the graph will be shaded near
 *	the low color and points on the right side of the graph will be
 *	shaded near to the high color.
 */
public class GradientColorScatterPlot extends ScatterPlot {

	protected Color lowColor, highColor;
	protected int barLocation;
	protected float barLengthPercentage;

	public static final int ABOVE = 0;
	public static final int RIGHT = 1;

	Font tinyFont;

	/**
	 *	Create a new GradientColorScatterPlot with orange on
	 *	the low end and red on the high.
	 *	@param table
	 *	@param sets
	 *	@param settings
	 */
	public GradientColorScatterPlot(Table table,
		DataSet[] sets, GraphSettings settings) {
		super(table, sets, settings);
		lowColor = Color.orange;
		highColor = Color.red;
		barLocation = ABOVE;
		barLengthPercentage = 100f;
		tinyFont = new Font("Helvetica", Font.PLAIN, 9);
	}

	/**
	 *	Create a new GradientColorScatterPlot.
	 *	@param table
	 *	@param sets
	 *	@param settings
	 *	@param low the low color
	 *	@param high the high color
	 */
	public GradientColorScatterPlot(Table table,
		DataSet[] sets, GraphSettings settings,
		Color low, Color high) {
		this(table, sets, settings);
		lowColor = low;
		highColor = high;
	}

	/**
	 *	Create a new GradientColorScatterPlot.
	 *	@param table
	 *	@param sets
	 *	@param settings
	 *	@param low the low color
	 *	@param high the high color
	 *	@param position
	 */
	public GradientColorScatterPlot(Table table,
		DataSet[] sets, GraphSettings settings,
		Color low, Color high, int position) {
		this(table, sets, settings, low, high);
		barLocation = position;
	}

	/**
	 *	Draw the data set.
	 *	@param g2
	 *	@param set
	 */
	public void drawDataSet(Graphics2D g2, DataSet set) {
		g2.setPaint(new GradientPaint((float)leftoffset, 0, lowColor,
			(float)(graphwidth-rightoffset), 0, highColor));

		int size = table.getNumRows();

		for (int index=0; index < size; index++) {
			double xvalue;
			double yvalue;
			if(table.isColumnNumeric(set.x))
				xvalue = table.getDouble(index, set.x);
			else {
				String v = table.getString(index, set.x);
				xvalue = (double)((Integer)xStringLookup[set.x].get(v)).intValue();
			}
			if(table.isColumnNumeric(set.y))
				yvalue = table.getDouble(index, set.y);
			else {
				String v = table.getString(index, set.y);
				yvalue = (double)((Integer)yStringLookup[set.y].get(v)).intValue();
			}

			drawPoint(g2, xvalue, yvalue);
		}
	}

	/**
	 *	Draw a data point.
	 *	@param g2
	 *	@param xvalue
	 *	@param yvalue
	 */
	public void drawPoint(Graphics2D g2, double xvalue, double yvalue) {
		double x = (xvalue-xminimum)/xscale+leftoffset;
		double y = graphheight-bottomoffset-(yvalue-yminimum)/yscale/* - point_size*/;

		g2.fill(new Rectangle2D.Double(x-(point_size/2),
			y-(point_size/2),
			point_size, point_size));
	}

	/**
	 *	Draw the axis
	 */
	public void drawAxis(Graphics2D g2) {
		super.drawAxis(g2);
		drawGradientBar(g2);
	}

	public void drawGradientBar(Graphics2D g2) {
		Paint oldPaint = g2.getPaint();

		// draw the bar above
		if(barLocation == ABOVE) {
			g2.setPaint(new GradientPaint((float)leftoffset, 0,
				lowColor,
				(float)(graphwidth-rightoffset-leftoffset), 0,
				highColor));
			g2.fill(new Rectangle2D.Double((float)leftoffset,
				(float)(topoffset-2*point_size),
				(float)(graphwidth-rightoffset-leftoffset)
					*barLengthPercentage/100f,
				(float)(point_size)));

			// draw the max and min
			if(xminimum != xmaximum) {
				Font f = g2.getFont();
				g2.setFont(tinyFont);
				g2.setPaint(Color.black);
				FontMetrics m = g2.getFontMetrics();
				int ht = m.getHeight();
				int at = m.getAscent();

				NumberFormat nf = NumberFormat.getInstance();
				nf.setMaximumFractionDigits(2);
				String s = nf.format(xminimum);
				g2.drawString(s,
					(float)(leftoffset-m.stringWidth(s)-smallspace),
					(float)(topoffset));
				s = nf.format(xmaximum);
				g2.drawString(s,
					(float)(graphwidth-rightoffset+smallspace),
					(float)(topoffset));
				g2.setFont(f);
			}
		}
		else {
			g2.setPaint(new GradientPaint(0f,
				(float)(graphheight-bottomoffset), lowColor,
				0f,
				(float)(topoffset+((100-barLengthPercentage)/100f)*
					(graphheight-topoffset-bottomoffset)),
				highColor));
			g2.fill(new Rectangle2D.Double(
				graphwidth-rightoffset+2*point_size,
				(float)topoffset+
					((100-barLengthPercentage)/100f)*
						(graphheight-topoffset-bottomoffset),
				(float)(point_size),
				(float)(graphheight-topoffset-bottomoffset)*
					((barLengthPercentage)/100f)));

			// draw the max and min
			if(xminimum != xmaximum) {
				Font f = g2.getFont();
				g2.setFont(tinyFont);
				g2.setPaint(Color.black);
				FontMetrics m = g2.getFontMetrics();
				int ht = m.getHeight();
				int at = m.getAscent();

				NumberFormat nf = NumberFormat.getInstance();
				nf.setMaximumFractionDigits(2);
				String s = nf.format(xminimum);
				int wd = m.stringWidth(s);
				g2.drawString(s,
					(float)(graphwidth-rightoffset+point_size),
					(float)(graphheight-bottomoffset+ht));
				s = nf.format(xmaximum);
				wd = m.stringWidth(s);
				g2.drawString(s,
					(float)(graphwidth-rightoffset+point_size),
					(float)(graphheight-topoffset-bottomoffset)*
						((barLengthPercentage)/100f)-at);
				g2.setFont(f);
			}
		}
		g2.setPaint(oldPaint);
	}

	/**
	 *	Get the low color
	 *	@return the low color
	 */
	public Color getLowColor() { return lowColor; }

	/**
	 *	Set the low color
	 *	@param l the new low color
	 */
	public void setLowColor(Color l) { lowColor = l; }

	/**
	 *	Get the high color
	 *	@return the high color
	 */
	public Color getHighColor() { return highColor; }

	/**
	 *	Set the high color
	 *	@param r the new high color
	 */
	public void setHighColor(Color r) { highColor = r; }

	/**
	 * Get the percentage of available space that the color bar should be.
	 * @return the percentage of space the color bar should take up
	 */
	public float getBarLengthPercentage() {
		return barLengthPercentage;
	}

	/**
	 * Set the percentage of available space that the color bar should be.
	 * @param p the new percentage
	 */
	public void setBarLengthPercentage(float p) {
		barLengthPercentage = p;
	}
}
