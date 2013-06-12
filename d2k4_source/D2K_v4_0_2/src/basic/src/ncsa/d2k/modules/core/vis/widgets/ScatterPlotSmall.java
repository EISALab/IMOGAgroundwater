package ncsa.d2k.modules.core.vis.widgets;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.text.*;
import javax.swing.*;

public class ScatterPlotSmall extends Graph {

	int smallspace =1;
	int largespace = 3;

	public ScatterPlotSmall(Table table, DataSet[] sets, GraphSettings settings) {
		super(table, sets, settings);
	}

	public void drawDataSet(Graphics2D g2, DataSet set) {


		int size = table.getNumRows();

		for (int index=0; index < size; index++) {
			double xvalue = table.getDouble(index, set.x);
			double yvalue = table.getDouble(index, set.y);

			drawPoint(g2, set.color, xvalue, yvalue);
		}
	}

	public void drawPoint(Graphics2D g2, Color color, double xvalue, double yvalue) {
		Color previouscolor = g2.getColor();

		double x = (xvalue-xminimum)/xscale+leftoffset;
		double y = graphheight-bottomoffset-(yvalue-yminimum)/yscale;

		g2.setColor(color);
		g2.fill(new Rectangle2D.Double(x, y, 1, 1));

		g2.setColor(previouscolor);
	}

	public void initOffsets() {
		// Offsets of axis
		leftoffset = .2*getWidth();
		rightoffset = .1*getWidth();

		// Offset of legend
		if (!settings.displaylegend) {
			legendheight = 0;
		}
		else {
			String[] names = new String[sets.length];

			DataSet set = sets[0];
			names[0] = set.name;
			legendwidth = metrics.stringWidth(set.name);
			for (int index=1; index < sets.length; index++) {
				set = sets[index];
				int stringwidth = metrics.stringWidth(set.name);
				if (stringwidth > legendwidth)
					legendwidth = stringwidth;
			}

			legendwidth += 4*smallspace+samplecolorsize;
			legendheight = (sets.length*fontheight)+(fontheight-samplecolorsize);

			legendleftoffset = leftoffset;
			legendtopoffset = legendheight+smallspace;
		}

		// Offsets of axis
		bottomoffset = .25*getHeight();
		topoffset = .05*getHeight();
	}

}
