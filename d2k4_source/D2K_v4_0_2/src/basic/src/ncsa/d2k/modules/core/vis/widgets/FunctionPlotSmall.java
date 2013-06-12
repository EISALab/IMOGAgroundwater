package ncsa.d2k.modules.core.vis.widgets;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import java.awt.*;

/**
   FunctionPlotSmall.java
      A version of FunctionPlot more suitable for the small sized
      graph in LWRPlotVis.
   @author talumbau
*/

public class FunctionPlotSmall extends Graph {

   int smallspace = 1;
   int largespace = 3;

   public FunctionPlotSmall(Table Functiontable, DataSet[] set, GraphSettings settings) {
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
      //NumericColumn xcolumn = (NumericColumn) table.getColumn(set.x);
      //NumericColumn ycolumn = (NumericColumn) table.getColumn(set.y);

      int ypixel, lastypixel;
      int size = table.getNumRows();
      double xvalue, yvalue, oldx, oldy;
      yvalue = table.getDouble(0, set.y);
      xvalue = table.getDouble(0, set.x);

      for ( int index=1; index<size; index++){
         oldx = xvalue;
         oldy = yvalue;
         xvalue = table.getDouble(index, set.x);
         yvalue = table.getDouble(index, set.y);
         drawFLine(g2, set.color, oldx, oldy, xvalue, yvalue);
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

   public void initOffsets() {
      // Offsets of axis
      leftoffset = .2*getWidth()+3;
      rightoffset = .1*getWidth()+2;

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

