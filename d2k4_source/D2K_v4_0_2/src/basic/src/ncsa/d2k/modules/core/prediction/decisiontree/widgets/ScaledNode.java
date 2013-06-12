package ncsa.d2k.modules.core.prediction.decisiontree.widgets;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.*;
import ncsa.d2k.modules.core.prediction.decisiontree.*;

public class ScaledNode extends Viewport {

	public ScaledNode(ViewableDTModel model, ViewableDTNode node, ScaledNode snode, String label) {
		super(model, node, snode, label);
	}

	public ScaledNode(ViewableDTModel model, ViewableDTNode node, ScaledNode snode) {
		this(model, node, snode, null);
	}

	public void drawScaledNode(Graphics2D g2) {
		// Background
		g2.setColor(DecisionTreeScheme.scaledviewbackgroundcolor);
		g2.fill(new Rectangle2D.Double(x-width/2, y, width, height));

                /*
		// Bars
		g2.setColor(DecisionTreeScheme.scaledviewbarcolor);
		double x1 = x - gwidth/2 + leftinset + tickmark + barspace;
		for (int index = 0; index < values.length; index++) {
			double barheight = yscale*values[index];
			double y1 = y + 1 + gheight - yincrement - barheight;
			g2.fill(new Rectangle2D.Double(x1, y1, barwidth, barheight));
			x1 += barwidth + barspace;
		}
                */
	}
}