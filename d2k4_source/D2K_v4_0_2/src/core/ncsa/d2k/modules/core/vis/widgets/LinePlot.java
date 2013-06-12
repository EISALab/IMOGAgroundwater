package ncsa.d2k.modules.core.vis.widgets;
import java.awt.*;

/**
 * This panel paints some number of lines. The addPoints method is called with the index
 * of the line to add, and as points are added they are displayed in a line graph.
 */
public class LinePlot extends Panel {

	/** the array of points to plot. */
	double [][] points = new double [1][10];

	/** the number of points to plot. */
	int [] numPoints = {0};

	/** the max value. */
	double max = 1.0;

	/** the min value. */
	double min = 0.0;

	/** the colors of the different lines. */
	Color [] lineRGBs = {Color.red, Color.green, Color.blue, Color.cyan,
			Color.magenta, Color.orange, Color.yellow};

	/** the target value */
	double convergence;
	public LinePlot () {
	}

	/**
		Pick an minimum size.

		@returns the minimum size of the plot panel.
	*/
	public Dimension getMinimumSize () {
		return new Dimension (300, 200);
	}

	/**
		Pick an minimum size.

		@returns the minimum size of the plot panel.
	*/
	public Dimension getPreferredSize () {
		return new Dimension (400, 250);
	}

	/**
		Add a point to the plot
	*/
	public void addPoint (int which, double value) {
		boolean repaintIt = false;

		// If we need more memory just do it.
		if (numPoints.length <= which) {

			// Add another line.
			double [][] tootoo = new double [which+1][];
			for (int i = 0 ; i < numPoints.length ; i++)
				tootoo [i] = points [i];
			tootoo [which] = new double [10];
			points = tootoo;

			// Add another line.
			int [] newCounters = new int [which+1];
			for (int i = 0 ; i < numPoints.length ; i++)
				newCounters [i] = numPoints [i];
			newCounters [which] = 0;
			numPoints = newCounters;
		}

		// Check to ensure we have enough space in the array...
		if (numPoints [which] == points [which].length) {

			// Trying to add a point beyond the end of the array.
			int width = this.getSize ().width;
			if (points [which].length == width) {
				width /= 2;
				System.arraycopy (points [which], width, points [which], 0,
					points [which].length - width);
				numPoints [which] = width;
			} else {
				double [] newPoints = new double [width];
				int min;
				if (points.length < width)      min = points.length;
				else                            min = width / 2;

				System.arraycopy (points [which], 0, newPoints, 0, min);
				numPoints [which] = min;
				points [which] = newPoints;
			}
		}

		this.points [which][numPoints [which]] = value;
		numPoints [which]++;
	}

	/**
		When the plot is done, we want to reset to trash all the points
		and bounds.
	*/
	public void reset () {
		for (int i = 0 ; i < numPoints.length ; i++)
			numPoints [i] = 0;
		max = 1.0;
		min = 0.0;
	}
	/**
		Initialize the plot.
	*/
	public void initPlot (double convergence, double max, double min) {
		this.convergence = convergence;
		this.max = max;
		this.min = min;
	}

	/**
		Change the minimum expected value, this in effect changes the scale.
		@param in the new minimum.
	*/
	public void setMin (double min) {
		this.min = min;
		this.repaint ();
	}

	/**
		Change the max expected value, this in effect changes the scale.
		@param in the new max.
	*/
	public void setMax (double max) {
		this.max = min;
		this.repaint ();
	}
	/**
		Paint the plot assuming ascending scale.
		@param which which line it is.
		@param g the graphics object.
	*/
	public void paintAscending (Graphics g, int which) {
		double [] points = this.points [which];
		int numPoints = this.numPoints [which];

		double range = this.max - this.min;

		// Compute the number of horizontal scale.
		Dimension size = this.getSize ();
		int height = size.height;
		double horizPoints = (double) height - 2.0;

		g.setColor (lineRGBs [which]);
		int oldWhere = (int) horizPoints;
		for (int i = 0 ; i < numPoints ; i++) {
			int where = (int) (((this.max - points[i]) / range) * horizPoints);
			where++;
			g.drawLine (i-1, oldWhere, i, where);
			oldWhere = where;
		}
	}


	/**
		Paint the plot assuming ascending scale.
		@param which which line it is.
		@param g the graphics object.
	*/
	public void paintDescending (Graphics g, int which) {
		double [] points = this.points [which];
		int numPoints = this.numPoints [which];

		double range = this.min - this.max;

		// Compute the number of horizontal scale.
		Dimension size = this.getSize ();
		int height = size.height;
		double horizPoints = (double) height - 2.0;

		int oldWhere = (int) horizPoints;
		for (int i = 0 ; i < numPoints ; i++) {
			int where = (int) (((this.min - points[i]) / range) * horizPoints);
			where++;
			g.drawLine (i-1, oldWhere, i, where);
			oldWhere = where;
		}
	}

	/**
		Paint the plot.
		@param g the graphics object.
	*/
	Color targetZoneColor = new Color (220, 255, 220);
	public void paint (Graphics g) {
		double range = this.min - this.max;

		// Compute the number of horizontal scale.
		Dimension size = this.getSize ();
		int height = size.height;
		double horizPoints = (double) height - 2.0;

		// Now gray the convergence.
		int zoneHeight;
		if (min > max)
			zoneHeight = (int) (((this.convergence - this.max) / range) * horizPoints);
		else
			zoneHeight = (int) (((this.max - this.convergence) / range) * horizPoints);

		if (zoneHeight > 0) {
			g.setColor (targetZoneColor);
			g.fillRect (1, (height-2) - zoneHeight, size.width, zoneHeight);
		}

		// Draw an outline around the plot.
		g.setColor (Color.darkGray);
		g.drawRect (0, 0, size.width - 1, height - 1);

		// For each line.
		for (int i = 0 ; i < numPoints.length ; i++)
			if (this.max > this.min)
				paintAscending (g, i);
			else
				paintDescending (g, i);

	}
}