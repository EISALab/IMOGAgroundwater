package ncsa.d2k.modules.core.vis.widgets;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import java.awt.*;

/**
   ScatterAndLinePlot.java

   Extends the Graph class to plot a set of data points along with a
   connected set of data.
   @author talumbau
*/

public class ScatterAndLinePlot extends ScatterPlot {

   DataSet[] ConnectedSets;
   Table fTable;
   public ScatterAndLinePlot () {}

   public ScatterAndLinePlot(Table Scattertable, Table Functiontable, DataSet[] Scatterset, DataSet[] Functionset, GraphSettings settings) {
      super(Scattertable, Scatterset, settings);
      ConnectedSets = Functionset;
      fTable = Functiontable;

      // check if the x and y min and max (based on the scatter data) are
      // really the min and max when compared to the connected data
      DataSet set;
      double xmin, xmax, ymin, ymax;
      NumericColumn column;

      // x data check
      set = ConnectedSets[0];
      double[] mm = getMinAndMax(fTable, set.x);
      xmin = mm[0];
      xmax = mm[1];
      for (int index=1; index < ConnectedSets.length; index++) {
         set = ConnectedSets[index];
         mm = getMinAndMax(fTable, set.y);

         double value = mm[0];
         if (value < xmin)
            xmin = value;

         value = mm[1];
         if (value > xmax)
            xmax = value;
      }
      if (xmin < xminimum)
         xminimum = xmin;
      if (xmax > xmaximum)
         xmaximum = xmax;


      // y data check
      set = ConnectedSets[0];
      mm = getMinAndMax(fTable, set.y);
      ymin = mm[0];
      ymax = mm[1];
      for (int index=1; index < ConnectedSets.length; index++) {
         set = ConnectedSets[index];
         mm = getMinAndMax(fTable, set.y);

         double value = mm[0];
         if (value < ymin)
            ymin = value;

         value = mm[1];
         if (value > ymax)
            ymax = value;
      }
      if (ymin < yminimum)
         yminimum = ymin;
      if (ymax > ymaximum)
         ymaximum = ymax;
   }

   //implements the abstract method of the Graph class to draw a function
   //line
   public void drawConnectedDataSet(Graphics2D g2, DataSet set) {
      System.out.println("the color is " + set.color);
      //NumericColumn xcolumn = (NumericColumn) fTable.getColumn(set.x);
      //NumericColumn ycolumn = (NumericColumn) fTable.getColumn(set.y);

      int ypixel, lastypixel;
      int size = fTable.getNumRows();
      double xvalue, yvalue, oldx, oldy;
      yvalue = fTable.getDouble(0, set.x);
      xvalue = fTable.getDouble(0, set.y);

      for ( int index=1; index<size; index++){
         oldx = xvalue;
         oldy = yvalue;
         xvalue = fTable.getDouble(index, set.x);
         yvalue = fTable.getDouble(index, set.y);
         drawFLine(g2, set.color, oldx, oldy, xvalue, yvalue);
      }
   }

   //handles the painting of a line from one point to another
   public void drawFLine(Graphics2D g2, Color color, double oldx, double oldy, double xvalue, double yvalue) {
      Color prevcolor = g2.getColor();

      double x1 = (oldx - xminimum)/xscale + leftoffset;
      double y1 = graphheight - bottomoffset - (oldy-yminimum)/yscale;
      double x2 = (xvalue-xminimum)/xscale + leftoffset;
      double y2 = graphheight-bottomoffset - (yvalue-yminimum)/yscale;

      int xint1 = (int) (Math.round(x1));
      int yint1 = (int) (Math.round(y1));
      int xint2 = (int) (Math.round(x2));
      int yint2 = (int) (Math.round(y2));



      g2.setColor(color);
      g2.drawLine(xint1, yint1, xint2, yint2);
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

   public void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2 = (Graphics2D) g;
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

      font = g2.getFont();
      metrics = getFontMetrics(font);
      fontheight = metrics.getHeight();
      fontascent = metrics.getAscent();

      graphwidth = getWidth();
      graphheight = getHeight();

      // Determine offsets
      initOffsets();

      resize();

      xvalueincrement = (xmaximum-xminimum)/gridsize;
      yvalueincrement = (ymaximum-yminimum)/gridsize;

      xscale = (xmaximum-xminimum)/(graphwidth-leftoffset-rightoffset);
      yscale = (ymaximum-yminimum)/(graphheight-topoffset-bottomoffset);

      drawAxis(g2);
      if (settings.displaylegend)
         drawLegend(g2);
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
      for (int index=0; index < sets.length; index++)
         drawDataSet(g2, sets[index]);

      for (int j=0; j < ConnectedSets.length; j++){
         drawConnectedDataSet(g2, ConnectedSets[j]);
   }

}

}
