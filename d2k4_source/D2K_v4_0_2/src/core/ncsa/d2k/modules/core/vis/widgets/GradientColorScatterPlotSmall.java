package ncsa.d2k.modules.core.vis.widgets;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import java.awt.*;

public class GradientColorScatterPlotSmall extends GradientColorScatterPlot {

	protected Font scaleFont;

	/**
	 *	Create a new GradientColorScatterPlot with orange on
	 *	the low end and red on the high.
	 *	@param table
	 *	@param sets
	 *	@param settings
	 */
	public GradientColorScatterPlotSmall(Table table,
		DataSet[] sets, GraphSettings settings) {
		super(table, sets, settings);
		smallspace = 1;
		largespace = 3;
		setPointSize(3);
		scaleFont = new Font("Helvetica", Font.PLAIN, 9);
	}

	public void initOffsets() {
		// Offsets of axis
		leftoffset = .2*getWidth()-point_size;
		rightoffset = .13*getWidth()+point_size;

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
		bottomoffset = .25*getHeight()+point_size;
		topoffset = .1*getHeight()-point_size;
	}

	public void drawScale(Graphics2D g2) {
		g2.setFont(scaleFont);
		metrics = g2.getFontMetrics();
		fontheight = metrics.getHeight();
		fontascent = metrics.getAscent();
		super.drawScale(g2);
	}
}