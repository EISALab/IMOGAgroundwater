package ncsa.d2k.modules.core.optimize.ga.emo.examples;

import ncsa.d2k.modules.core.vis.widgets.*;
import ncsa.d2k.modules.core.vis.*;
import ncsa.d2k.core.modules.*;

/**
 * Title:        Modules Dev
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Tom Redman
 * @version 1.0
 */

import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.io.*;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import java.text.*;

public class LoadProblemScatterPlot extends ScatterPlot implements MouseListener {
	/** the index of the selected point. */
	int selected = -1;
	Image allBars = null;
	Image outerBars = null;
	Image leftBars = null;
	Image currentConfig = null;
	int [][] vertPoints;

	public LoadProblemScatterPlot () {
		this.addMouseListener (this);
	}

	public LoadProblemScatterPlot(Table table, DataSet[] sets, GraphSettings settings) {
		super(table, sets, settings);
		this.addMouseListener (this);
	}

	/** load the images. */
	public void loadImages () {
		// Find the module, use the getImage method of the module to get the image.
		Component comp = this.getParent ();
		while (!(comp instanceof ncsa.d2k.modules.core.optimize.ga.emo.examples.LoadPlotUserPane))
			comp = comp.getParent ();

		RootModule mod = (RootModule) ((LoadPlotUserPane)comp).getModule ();
		String imagesPath = "/images/nsga/";
		allBars = mod.getImage (imagesPath+"3bars.gif");
		leftBars = mod.getImage (imagesPath+"2bar-01.gif");
		outerBars = mod.getImage (imagesPath+"2bar-02.gif");
	}

	/**
	 * This method is always called when we resize, and when we resize, we must
	 * recompute the vertPoints.
	 */
	public void setBounds (int x, int y, int width, int height) {
		synchronized (this) {
			super.setBounds (x, y, width, height);
			vertPoints = null;
		}
	}

	/**
	 * This method computes a mask consisting of an array for each x and y in the
	 * graph (in pixels) and the index of the individual who resides there.
	 */
	synchronized public void getMouseMask () {
		if (vertPoints == null) {
			vertPoints = new int [this.getSize().width][this.getSize().height];
			for (int i = 0 ; i < vertPoints.length; i++)
				for (int j = 0 ; j < vertPoints[0].length; j++)
					vertPoints [i][j] = -1;
			int cnt = 0;
			int size = table.getNumRows();
			for (int index=0; index < size; index++) {
				double xvalue = table.getDouble(index, sets[0].x);
				double yvalue = table.getDouble(index, sets[0].y);
				int x = (int) ((xvalue-xminimum)/xscale);
				int y = (int) ((yvalue-yminimum)/yscale);
				if (x < this.getSize().width && y < this.getSize().height)
					vertPoints [x][y] = index;
				else
					System.out.println ("WHOAAAA"+(cnt++)+" - x: "+x+" y: "+y);
			}
		}
	}

	/**
	 * Draw the appropriate glyph for the selected point. the glyph represents if the
	 * selected point is a two bar structure or three bar struct.
	 */

	public void drawSelected (Graphics2D g2) {
		final Font paramFnt = new Font ("Helvetica", Font.ITALIC, 8);
		double x1 = table.getDouble (selected, 0);
		double x2 = table.getDouble (selected, 1);
		double x3 = table.getDouble (selected, 2);
		Dimension sz = this.getSize ();
		g2.setFont (paramFnt);
		FontMetrics fm = g2.getFontMetrics (paramFnt);

		DecimalFormat df = new DecimalFormat ("#.####");
		if (x3 <= 0.0)
			g2.drawImage (leftBars, sz.width - (int)rightoffset - allBars.getWidth (this),
				(int) this.topoffset, this);
		else if (x2 <= 0.0)
			g2.drawImage (outerBars, sz.width - (int)rightoffset - allBars.getWidth (this),
				(int) this.topoffset, this);
		else
			g2.drawImage (allBars, sz.width - (int)rightoffset - allBars.getWidth (this),
				(int) this.topoffset, this);

		// This is the text baseline.
		int top = allBars.getHeight(this) + (int)topoffset + 12;

		// This is the middle of the image.
		int left = sz.width - (int) this.rightoffset - (allBars.getWidth (this) / 2);
		String form = "x1("+df.format(x1)+") x2("+df.format(x2)+") x3("+
				df.format(x3)+")";
		g2.drawString (form, left - (fm.stringWidth (form)/2), top);

		x1 = table.getDouble (selected, 3);
		x2 = table.getDouble (selected, 4);
		form = "f1("+df.format(x1)+") f2("+df.format(x2)+")";
		g2.drawString (form, left - (fm.stringWidth (form)/2), top + fm.getHeight ());
	}

	/** we will draw the dataset in the same way as the superclass, but we also draw the
	 *  glyph that indicates which of the scenarios the currently selected point represents.
	 */
	public void drawDataSet(Graphics2D g2, DataSet set) {
		if (allBars == null)
			this.loadImages ();

		if (selected != -1)
			this.drawSelected (g2);
		int size = table.getNumRows();
		for (int index=0; index < size; index++) {
			double xvalue = table.getDouble(index, set.x);
			double yvalue = table.getDouble(index, set.y);

			if (selected == index)
				drawSelectedPoint (g2, Color.red/*set.color*/, xvalue, yvalue);
			else
				drawPoint(g2, set.color, xvalue, yvalue);
		}
	}

	/**
	 * Draw the point.
	 */
	public void drawPoint(Graphics2D g2, Color color, double xvalue, double yvalue) {
		Color previouscolor = g2.getColor();
		double x = (xvalue-xminimum)/xscale+leftoffset;
		double y = graphheight-bottomoffset-(yvalue-yminimum)/yscale;
		g2.setColor(color);
		g2.fill(new Rectangle2D.Double(x-1, y-1, 2, 2));
		g2.setColor(previouscolor);
	}

	/**
	 * Draw the point.
	 */
	public void drawSelectedPoint(Graphics2D g2, Color color, double xvalue, double yvalue) {
		Color previouscolor = g2.getColor();
		double x = (xvalue-xminimum)/xscale+leftoffset;
		double y = graphheight-bottomoffset-(yvalue-yminimum)/yscale;
		if (x < leftoffset || x > rightoffset) return;
		if (y < topoffset || y > bottomoffset) return;
		g2.setColor(color);
		g2.draw(new Rectangle2D.Double(x-2, y-2, 4, 4));
		g2.draw(new Rectangle2D.Double(x-3, y-3, 6, 6));
		g2.setColor(previouscolor);
	}
		/**
	 * Invoked when the mouse has been clicked on a component.
	 */
	public void mouseClicked(MouseEvent e){
		int x = (int) (((double)e.getX ()) - leftoffset);
		int y = (int) ((graphheight-bottomoffset) - (double) e.getY ());
		if (x < 0) x = 0;
		if (y < 0) y = 0;
		/*
			search in this order around x and y at center:
			12 13 14 15 16
			11 3  4  5  17
			10 2  1  6  18
			25 9  8  7  19
			24 23 22 21 20
		*/

		final int [] xs = {0, -1, -1,  0,  1,  1,  1, 0, -1, -2, -2, -2, -1,  0,  1,  2,
				 2, 2, 2, 2, 1, 0, -1, -2, -2};
		final int [] ys = {0,  0, -1, -1, -1,  0,  1, 1,  1,  0, -1, -2, -2, -2, -2, -2,
				-1, 0, 1, 2, 2, 2,  2,  2,  1};

		for (int i = 0 ; i < xs.length ; i++) {
			int ii = x + xs [i];
			int jj = y + ys [i];

			// out of bounds checks.
			if (ii < 0 || ii >= vertPoints.length)
				continue;
			if (jj < 0 || jj >= vertPoints[0].length)
				continue;

			if (vertPoints [ii][jj] != -1 ) {
				selected = vertPoints[ii][jj];
				this.repaint ();
				return;
			}
		}
		selected = -1;
		this.repaint();
	}

	public void mousePressed(MouseEvent e){
	}
	public void mouseReleased(MouseEvent e){
	}
	public void mouseEntered(MouseEvent e){
	}
	public void mouseExited(MouseEvent e){
	}

}
