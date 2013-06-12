package ncsa.d2k.modules.core.vis.widgets;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.text.NumberFormat;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

/**
 * A ShadedScatterPlot is a ScatterPlot with its points shaded by a gradient.
 * The point is shaded based on the value in the shade column relative to the
 * maximum and the minimum in the column.
 */
public class ShadedScatterPlot extends GradientColorScatterPlot {

	/** The index of the column with the shader values */
	protected int shadeColumn;
	/** The maximum value in the shade column */
	protected double shadeMax;
	/** The minimum value in the shade column */
	protected double shadeMin;

	/** The length of the line used for the gradient paint */
	protected static int LINE_LENGTH = 250;

	/**
	 * Create a new ShadedScatterPlot.
	 * @param table the table with the data
	 * @param sets
	 * @param settings
	 * @param shadeColumnIndex the index of the column with the values to shade by
	 */
	public ShadedScatterPlot(Table table, DataSet[] sets,
		GraphSettings settings, int shadeColumnIndex) {
		super(table, sets, settings);

		shadeColumn = shadeColumnIndex;
		initRange();
	}

	/**
	 * Create a new ShadedScatterPlot.
	 * @param table the table with the data
	 * @param sets
	 * @param settings
	 * @param shadeColumnIndex the index of the column with the values to shade by
	 * @param low the low color
	 * @param high the high color
	 */
	public ShadedScatterPlot(Table table, DataSet[] sets,
		GraphSettings settings, int shadeColumnIndex, Color low, Color high) {
		super(table, sets, settings, low, high);

		shadeColumn = shadeColumnIndex;
		initRange();
	}

	/**
	 * Create a new ShadedScatterPlot.
	 * @param table the table with the data
	 * @param sets
	 * @param settings
	 * @param shadeColumnIndex the index of the column with the values to shade by
	 * @param low the low color
	 * @param high the high color
	 * @param pos
	 */
	public ShadedScatterPlot(Table table, DataSet[] sets,
		GraphSettings settings, int shadeColumnIndex, Color low,
		Color high, int pos) {
		super(table, sets, settings, low, high, pos);

		shadeColumn = shadeColumnIndex;
		initRange();
	}

	/**
	 * Initialize the min and max shading values.
	 */
	protected void initRange() {
		double[] mm = getMinAndMax(table, shadeColumn);
		if(table.isColumnNumeric(shadeColumn)) {
			shadeMax = mm[1];
			shadeMin = mm[0];
		}
		else {
			shadeMax = 1.0;
			shadeMin = 0.0;
		}
	}

	public double[] getMinAndMax(Table table, int ndx) {
		double[] minAndMax = new double[2];
		double mandm;
		for (int i = 0; i < table.getNumRows(); i++) {
			mandm = table.getDouble(i, ndx);
			if (mandm > minAndMax[1]) {
				minAndMax[1] = mandm;
			}
			if (mandm < minAndMax[0]) {
				minAndMax[0] = mandm;
			}
		}
		return minAndMax;
	}

	/**
	 *	Draw the data set.
	 *	@param g2
	 *	@param set
	 */
	public void drawDataSet(Graphics2D g2, DataSet set) {

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

			// now create a new GradientPaint based on the shadecolumn
			// get the shader value
			double shaderVal = table.getDouble(index, shadeColumn);
			// find where the shader value lies between min and max
			double percent = (shaderVal-shadeMin)/(shadeMax-shadeMin);

			// calculate the x and y values for the point
			double x = (xvalue-xminimum)/xscale+leftoffset;
			double y = graphheight-bottomoffset-(yvalue-yminimum)/yscale;
			// find the left and right points for the gradient
			double lessThan = x - percent*LINE_LENGTH;
			double greaterThan = x + LINE_LENGTH-(percent*LINE_LENGTH);
			if(percent == 0) {
				lessThan = x;
				greaterThan = x+LINE_LENGTH;
			}
			else if(percent == 1.0) {
				lessThan = x - LINE_LENGTH;
				greaterThan = x;
			}

			g2.setPaint(new GradientPaint((float)lessThan, (float)y, lowColor,
				(float)greaterThan, (float)y, highColor));

			// since we already calculated the x and y values,
			// draw the point ourselves rather than letting drawPoint
			// recalculate it
			g2.fill(new Rectangle2D.Double(x-(point_size/2),
				y-(point_size/2),
				point_size, point_size));
			//drawPoint(g2, xvalue, yvalue);
		}
	}

	/**
	 *	Draw the axis
	 */
	/*public void drawAxis(Graphics2D g2) {
		//super.drawAxis(g2);
		g2.draw(new Line2D.Double(leftoffset,topoffset,
			leftoffset, graphheight-bottomoffset));
		g2.draw(new Line2D.Double(leftoffset,
			graphheight-bottomoffset,
			graphwidth-rightoffset,
			graphheight-bottomoffset));
		*/

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
			if(shadeMin != shadeMax) {
				Font f = g2.getFont();
				g2.setFont(tinyFont);
				g2.setPaint(Color.black);
				FontMetrics m = g2.getFontMetrics();
				int ht = m.getHeight();
				int at = m.getAscent();

				NumberFormat nf = NumberFormat.getInstance();
				nf.setMaximumFractionDigits(2);
				String s = nf.format(shadeMin);
				g2.drawString(s,
					(float)(leftoffset-m.stringWidth(s)-smallspace),
					(float)(topoffset));
				s = nf.format(shadeMax);
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
			if(shadeMin != shadeMax) {
				Font f = g2.getFont();
				g2.setFont(tinyFont);
				g2.setPaint(Color.black);
				FontMetrics m = g2.getFontMetrics();
				int ht = m.getHeight();
				int at = m.getAscent();

				NumberFormat nf = NumberFormat.getInstance();
				nf.setMaximumFractionDigits(2);
				String s = nf.format(shadeMin);
				int wd = m.stringWidth(s);
				g2.drawString(s,
					(float)(graphwidth-rightoffset+point_size),
					(float)(graphheight-bottomoffset+ht));
				s = nf.format(shadeMax);
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
}
