package ncsa.d2k.modules.core.prediction.decisiontree.widgets;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.border.*;

/*
	DecisionTreeVis

	Border for navigator panel and brush panel
*/
public final class RectangleBorder extends AbstractBorder {

	String title;

	int left, right, top, bottom;
	int titletop, titlebottom, titlespace;

	FontMetrics metrics;
	int ascent;

	public RectangleBorder(String title) {
		this.title = title;

		// Insets
		left = 10;
		right = 10;
		bottom = 10;
		titletop = 4;
		titlebottom = 8;
		titlespace = 12;
		top = titletop + 10 + titlebottom + titlespace;
	}

	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		metrics = g2.getFontMetrics(DecisionTreeScheme.textfont);
		ascent = metrics.getAscent();

		// Background
		g2.setColor(DecisionTreeScheme.borderbackgroundcolor);
		g2.fill(new Rectangle2D.Double(x, y, width, top));
		g2.fill(new Rectangle2D.Double(x, y, left, height));
		g2.fill(new Rectangle2D.Double(x+width-right, y, right, height));
		g2.fill(new Rectangle2D.Double(x, y+height-bottom, width, bottom));

		// Bevel
		double ybevel = y + titletop + ascent + titlebottom;
		g2.setColor(DecisionTreeScheme.bordershadowcolor);
		g2.draw(new Line2D.Double(x, y+ybevel, x+width-1, y+ybevel));
		g2.setColor(DecisionTreeScheme.borderhighlightcolor);
		g2.draw(new Line2D.Double(x, y+ybevel+2, x+width-1, y+ybevel+2));

		// Upper bevel
		g2.setStroke(new BasicStroke(1.2f));
		g2.setColor(DecisionTreeScheme.borderupperbevelcolor);
		g2.draw(new Line2D.Double(x, y, x+width-1, y));
		g2.draw(new Line2D.Double(x, y, x, y+height-1));

		// Lower bevel
		g2.setStroke(new BasicStroke(1.2f));
		g2.setColor(DecisionTreeScheme.borderlowerbevelcolor);
		g2.draw(new Line2D.Double(x, y+height-1, x+width-1, y+height-1));
		g2.draw(new Line2D.Double(x+width-1, y, x+width-1, y+height-1));

		// Title
		g2.setFont(DecisionTreeScheme.textfont);
		g2.setColor(DecisionTreeScheme.textcolor);
		g2.drawString(title, x+left, y+titletop+ascent);
	}

	public Insets getBorderInsets(Component c) {
		return new Insets(top, left, bottom, right);
	}
}
