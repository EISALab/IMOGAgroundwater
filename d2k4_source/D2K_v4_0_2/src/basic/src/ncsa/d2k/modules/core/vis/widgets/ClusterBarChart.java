package ncsa.d2k.modules.core.vis.widgets;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.text.*;
import java.util.*;
import javax.swing.*;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.util.*;

public class ClusterBarChart extends BarChart implements MouseListener {
  private static final int LEFTOFFSET = 20;
  private static final int RIGHTOFFSET = 20;
  private static final int TOPOFFSET = 20;
  private static final int BOTTOMOFFSET = 20;

  // Clustering
  private int runs = 0;
  private int runsize;

  // Unique cluster values
  private HashSet valueset;

  // Data
  private MutableTable mutable;

  private double minimumgraphwidth;
  private double minimumgraphheight;

  private int legendspace = 35;

  private int longestwidthx;
  private int longestwidthy;
  private int longestwidthz;

  Color[] clustercolors;

  public ClusterBarChart(Table table, DataSet set, GraphSettings settings, int xincrement, int yincrement) throws Exception {
    super(table, set, settings);

    settings.displayaxislabels = false;
    settings.displaylegend = true;

    gridsize = settings.gridsize;

    minimumxoffsetincrement = xincrement;
    minimumyoffsetincrement = yincrement;

    yvalueincrement = (ymaximum-yminimum)/gridsize;

    // Dependencies
    if (table.getNumRows() == 0)
      throw new Exception("Table exception");

    if (!table.isColumnNominal(set.z))
      throw new Exception("Nominal exception");

    mutable = table.toExampleTable();

    // Map x, y and z to first three columns
    int[] reorder = {set.x, set.y, set.z};
    mutable = (MutableTable) mutable.reorderColumns(reorder);
    if (mutable.getNumColumns() > 3)
      mutable.removeColumns(3, mutable.getNumColumns()-3);

    // Runs
    int[] sort = {0, 2};
    int[] indices = TableUtilities.multiSortIndex(mutable, sort);
    mutable = (MutableTable) mutable.reorderRows(indices);

    // Missing values
    valueset = TableUtilities.uniqueValueSet(mutable, 2);
    runsize = valueset.size();

    // Determine number of runs
    String label = mutable.getString(0, 0);
    HashSet runset = new HashSet(valueset);
    int rows = mutable.getNumRows();
    for (int row=0; row < rows; row++) {
      String runlabel = mutable.getString(row, 0);
      String runtime = mutable.getString(row, 2);

      if (!runlabel.equals(label)) {
        // Missing values
        Iterator iterator = runset.iterator();
        while (iterator.hasNext()) {
			int where = mutable.getNumRows();
					  mutable.addRows(1);
					  mutable.setString(label, where, 0);
					  mutable.setString(new String("0.0"), where, 1);
					  mutable.setString((String) iterator.next(), where, 2);
         /* String[] values = new String[mutable.getNumColumns()];
          values[0] = label;
          values[1] = new String("0.0");
          values[2] = (String) iterator.next();
          mutable.addRow(values);*/
        }

        runs++;
        label = runlabel;

        runset = new HashSet(valueset);
      }
      runset.remove(runtime);
    }

    // Last run
    Iterator iterator = runset.iterator();
    while (iterator.hasNext()) {
		int where = mutable.getNumRows();
			 mutable.addRows(1);
			 mutable.setString(label, where, 0);
			 mutable.setString(new String("0.0"), where, 1);
			 mutable.setString((String) iterator.next(), where, 2);
			 /*
      String[] values = new String[mutable.getNumColumns()];
      values[0] = label;
      values[1] = new String("0.0");
      values[2] = (String) iterator.next();
      mutable.addRow(values); */
    }
    runs++;

    indices = TableUtilities.multiSortIndex(mutable, sort);
    mutable = (mutable.reorderRows(indices)).toExampleTable();

    // Include bins for spacing runs
    // Impacts mapping of bins to table values
    bins = (runsize+1)*runs;

    clustercolors = new Color[runsize];
    for (int index=0; index < clustercolors.length; index++)
      //clustercolors[index] = getColor(index);
      clustercolors[index] = getColor(index * 3);

    addMouseListener(this);
  }

  public void initOffsets() {
    NumberFormat numberformat = NumberFormat.getInstance();
    numberformat.setMaximumFractionDigits(3);

    // Determine maximum string widths
    // X axis
    for (int run=0; run < runs; run++) {
      String value = mutable.getString(run*runsize, 0);

      int stringwidth = metrics.stringWidth(value);
      if (stringwidth > longestwidthx)
        longestwidthx = stringwidth;
    }

    // Y axis
    double yvalue =  yminimum;
    for (int index=0; index < gridsize; index++) {
      String value = numberformat.format(yvalue);

      int stringwidth = metrics.stringWidth(value);
      if (stringwidth > longestwidthy)
        longestwidthy = stringwidth;

      yvalue += yvalueincrement;
    }

    // Z axis
    String label = mutable.getColumnLabel(2);
    int labelwidth = metrics.stringWidth(label);
    if (labelwidth > longestwidthx)
      longestwidthz = labelwidth;

    // Determine offsets
    if (!settings.displaylegend) {
      legendheight = 0;
      legendwidth = 0;
    }
    else {
      String[] values = TableUtilities.uniqueValues(mutable, 2);
      legendwidth = metrics.stringWidth(values[0]);
      for (int index=1; index < values.length; index++) {
        int stringwidth = metrics.stringWidth(values[index]);
        if (stringwidth > legendwidth)
          legendwidth = stringwidth;
      }

      legendwidth += 3*smallspace+samplecolorsize;

      if (legendwidth < longestwidthz)
        legendwidth = longestwidthz;

      /*
      if (legendwidth > longestwidthz)
        legendwidth += 3*smallspace+samplecolorsize;
      else
        legendwidth = longestwidthz;
      */

      legendheight = (fontheight+smallspace)+(values.length*fontheight);
    }

    // Primary offsets
    leftoffset = LEFTOFFSET+longestwidthy;
    rightoffset = RIGHTOFFSET+legendwidth+2*legendspace;
    bottomoffset = BOTTOMOFFSET+longestwidthx;
    topoffset = TOPOFFSET;

    // Minimum dimensions
    minimumgraphwidth = minimumxoffsetincrement*bins+leftoffset+rightoffset;
    minimumgraphheight = Math.max(minimumyoffsetincrement*gridsize+topoffset+bottomoffset,
                                  legendheight+topoffset+bottomoffset);

    // Legend offsets
    legendleftoffset = getGraphWidth()-legendwidth-legendspace;
    legendtopoffset = getGraphHeight()/2-legendheight/2;
  }

  // Resize scale
  public void resize() {
    xoffsetincrement = (getGraphWidth()-leftoffset-rightoffset)/bins;
    yoffsetincrement = (getGraphHeight()-topoffset-bottomoffset)/gridsize;
    yscale = (ymaximum-yminimum)/(graphheight-topoffset-bottomoffset);

    /*
    while ((yoffsetincrement < minimumyoffsetincrement) && (gridsize > 0)) {
      gridsize = gridsize/2;
      yoffsetincrement = (graphheight-topoffset-bottomoffset)/gridsize;
    }
    */
  }

  public int getGraphWidth() {
    if (getWidth() < minimumgraphwidth)
      return (int) minimumgraphwidth;

    return getWidth();
  }

  public int getGraphHeight() {
    if (getHeight() < minimumgraphheight)
      return (int) minimumgraphheight;

    return getHeight();
  }

  public Dimension getPreferredSize() {
    return getMinimumSize();
  }

  public Dimension getMinimumSize() {
    return new Dimension((int) minimumgraphwidth, (int) minimumgraphheight);
  }

  /*
  Drawing functions
  */

  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

    font = g2.getFont();
    metrics = getFontMetrics(font);
    fontheight = metrics.getHeight();
    fontascent = metrics.getAscent();

    graphwidth = getWidth();
    graphheight = getHeight();

    // Determine offsets
    initOffsets();

    resize();

    yvalueincrement = (ymaximum-yminimum)/gridsize;

    yoffsetincrement = (graphheight-topoffset-bottomoffset)/gridsize;
    xoffsetincrement = (graphwidth-leftoffset-rightoffset)/bins;

    yscale = (ymaximum-yminimum)/(graphheight-topoffset-bottomoffset);

    drawAxis(g2);
    if (settings.displaygrid)
      drawGrid(g2);
    if (settings.displaytickmarks)
      drawTickMarks(g2);
    if (settings.displayscale)
      drawScale(g2);
    if (settings.displayaxislabels)
      drawAxisLabels(g2);
    if (settings.displaytitle)
      drawTitle(g2);
    if (settings.displaylegend)
      drawLegend(g2);
    drawDataSet(g2, set);
  }

  public void drawTitle(Graphics2D g2) {
    int stringwidth = metrics.stringWidth(title);
    double x = (getGraphWidth()-stringwidth)/2;
    double y = (topoffset)/2 + fontheight/2;

    g2.drawString(title, (int) x, (int) y);
  }

  public void drawAxis(Graphics2D g2) {
    g2.draw(new Line2D.Double(leftoffset, topoffset,
                              leftoffset, getGraphHeight()-bottomoffset));
    g2.draw(new Line2D.Double(leftoffset, getGraphHeight()-bottomoffset,
                              getGraphWidth()-rightoffset, getGraphHeight()-bottomoffset));
  }

  public void drawTickMarks(Graphics2D g2) {
    double x = leftoffset+xoffsetincrement/2;

    // Map bins to runs
    int counter = 0;
    int offset = 0;

    for (int bin=0; bin < bins; bin++) {

      if (counter == runsize)
        counter = 0;

      else {
        g2.draw(new Line2D.Double(x, getGraphHeight()-bottomoffset-tickmarksize, x, getGraphHeight()-bottomoffset+tickmarksize));
        counter++;
      }

      x += xoffsetincrement;
    }

    double y = topoffset+yoffsetincrement;
    for (int bin=0; bin < gridsize; bin++) {
      g2.draw(new Line2D.Double(leftoffset-tickmarksize, y, leftoffset+tickmarksize, y));
      y += yoffsetincrement;
    }
  }

  public void drawGrid(Graphics2D g2) {
    Color previouscolor = g2.getColor();
    g2.setColor(Color.gray);

    // y axis
    double y = topoffset+yoffsetincrement;
    for (int index=0; index < gridsize-1; index++) {
      g2.draw(new Line2D.Double(leftoffset, y, getGraphWidth()-rightoffset, y));
      y += yoffsetincrement;
    }

    g2.setColor(previouscolor);
  }

  public void drawScale(Graphics2D g2) {
    NumberFormat numberformat = NumberFormat.getInstance();
    numberformat.setMaximumFractionDigits(3);
    int ascent = metrics.getAscent();

    double xincrement = (runsize+1)*xoffsetincrement;

    double x = leftoffset + (xincrement/2);

    AffineTransform transform = g2.getTransform();
    g2.rotate(Math.toRadians(90));

    for (int run=0; run < runs; run++) {
      String value = mutable.getString(run*runsize, 0);
      int stringwidth = metrics.stringWidth(value);

      g2.drawString(value,
                    (int) (getGraphHeight()-bottomoffset+tickmarksize+smallspace),
                    (int) -(x-ascent/2));
      x += xincrement;
    }

    g2.setTransform(transform);

    double y = getGraphHeight()-bottomoffset;
    double yvalue =  yminimum;
    for (int index=0; index < gridsize; index++) {
      String value = numberformat.format(yvalue);
      int stringwidth = metrics.stringWidth(value);

      g2.drawString(value, (int) (leftoffset-stringwidth-smallspace), (int) (y+fontascent/2));
      y -= yoffsetincrement;
      yvalue += yvalueincrement;
    }
  }

  public void mouseClicked(MouseEvent event) {
    double x = event.getX();
    double y = event.getY();

    Set keys = map.keySet();
    Iterator iterator = keys.iterator();
    while (iterator.hasNext()) {
      Point2D.Double point = (Point2D.Double) iterator.next();

      if (x >= point.x && x <= point.x+samplecolorsize) {
        if (y >= point.y && y <= point.y+samplecolorsize) {
          Integer integer = (Integer) map.get(point);

          Color color = JColorChooser.showDialog(this, "Select Color", getClusterColor(integer.intValue()));
          if (color != null) {
            setClusterColor(integer.intValue(), color);
            repaint();
          }
        }
      }
    }
  }

  public void mousePressed(MouseEvent event) {
  }

  public void mouseReleased(MouseEvent event) {
  }

  public void mouseEntered(MouseEvent event) {
  }

  public void mouseExited(MouseEvent event) {
  }

  HashMap map = new HashMap();

  public void drawLegend(Graphics2D g2) {
    Color previouscolor = g2.getColor();

    double x = legendleftoffset;
    double y = legendtopoffset;

    g2.drawString(mutable.getColumnLabel(2), (int) x, (int) y);

    y += smallspace;

    g2.draw(new Rectangle.Double(x, y, legendwidth, legendheight));

    map.clear();
    String[] values = new String[runsize];
	for (int i = 0 ; i < values.length ; i++) {
		  values[i] = mutable.getString(i, 2);
		}
    //mutable.getSubset(0, runsize).getColumn(values, 2);
    for (int index=0; index < values.length; index++) {
      x = legendleftoffset + smallspace;
      y += fontheight - samplecolorsize;

      g2.setColor(getClusterColor(index));
      g2.fill(new Rectangle.Double(x, y, samplecolorsize, samplecolorsize));

      Point2D.Double point = new Point2D.Double(x, y);

      map.put(point, new Integer(index));

      x += samplecolorsize+smallspace;
      y += samplecolorsize;

      String value = values[index];
      g2.setColor(previouscolor);
      g2.drawString(value, (int) x, (int) y);
    }
  }

  public void drawDataSet(Graphics2D g2, DataSet set) {
    double x = leftoffset;
    double barwidth = xoffsetincrement;

    // Map bins to runs
    int counter = 0;
    int offset = 0;

    for (int bin=0; bin < bins; bin++) {

      if (counter == runsize) {
        counter = 0;
        offset++;
      }

      else {
        double value = mutable.getDouble(bin-offset, 1);
        double barheight = (value-yminimum)/yscale;
        double y = getGraphHeight()-bottomoffset-barheight;

        g2.setColor(getClusterColor(counter));
        g2.fill(new Rectangle2D.Double(x, y, barwidth, barheight));

        g2.setColor(Color.black);
        g2.draw(new Rectangle2D.Double(x, y, barwidth, barheight));

        counter++;
      }

      x += xoffsetincrement;
    }
  }

  public Color getClusterColor(int index) {
    return clustercolors[index];
  }

  public void setClusterColor(int index, Color color) {
    clustercolors[index] = color;
  }
}