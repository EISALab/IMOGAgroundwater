package ncsa.d2k.modules.core.vis.widgets;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import java.awt.*;

public class FunctionPlot extends Graph {

   public FunctionPlot(Table Functiontable, DataSet[] set, GraphSettings settings) {
      super(Functiontable, set, settings);
   }

   /*public void drawDataSet(Graphics2D g2, DataSet set) {
      NumericColumn xcolumn = (NumericColumn) table.getColumn(set.x);
      NumericColumn ycolumn = (NumericColumn) table.getColumn(set.y);

      int size = xcolumn.getNumRows();

      for (int index=0; index <size; index++) {
         double xvalue = xcolumn.getDouble(index);
         double yvalue = ycolumn.getDouble(index);

         drawPoint(g2, set.color, xvalue, yvalue);
      }
   }*/

   public void drawDataSet(Graphics2D g2, DataSet set) {

      int ypixel, lastypixel;
      int size = table.getNumRows();
      double xvalue, yvalue, oldx, oldy;
      yvalue = table.getDouble(0, set.y);
      xvalue = table.getDouble(0, set.x);
      drawPoint(g2, Color.black, xvalue, yvalue);

      for ( int index=1; index<size; index++){
         oldx = xvalue;
         oldy = yvalue;
         xvalue = table.getDouble(index, set.x);
         yvalue = table.getDouble(index, set.y);
         drawFLine(g2, set.color, oldx, oldy, xvalue, yvalue);
         drawPoint(g2, Color.black, xvalue, yvalue);
      }
   }

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
}

