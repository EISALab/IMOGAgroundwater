// package ncsa.d2k.modules.projects.berkin.plot;
package ncsa.d2k.modules.core.vis.widgets;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.util.*;
import ncsa.gui.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;
import ncsa.d2k.modules.core.vis.FilterBoxPlot;

import ncsa.d2k.modules.core.vis.FilterBoxPlot;

public class FilterBoxPlotPane extends JPanel {


   MutableBoxPlotPanel ppanel;
   JPanel spanel;
   JLabel lminimum, lmaximum, lmedian, lfquartile, ltquartile;
   JLabel llowerbound, lupperbound, lrows;
   JButton filter, undo;

   public FilterBoxPlotPane(ArrayList flist, ArrayList slist, BoxPlotGroup group, Table table, int column) {
      // Statistics
      spanel = new JPanel();
      layoutLabels();
      spanel.setLayout(new GridBagLayout());
      Constrain.setConstraints(spanel, lminimum, 0, 0, 1, 1, GridBagConstraints.NONE, GridBagConstraints.NORTHWEST, 0, 0);
      Constrain.setConstraints(spanel, lmaximum, 0, 1, 1, 1, GridBagConstraints.NONE, GridBagConstraints.NORTHWEST, 0, 0);
      Constrain.setConstraints(spanel, lfquartile, 0, 2, 1, 1, GridBagConstraints.NONE, GridBagConstraints.NORTHWEST, 0, 0);
      Constrain.setConstraints(spanel, lmedian, 0, 3, 1, 1, GridBagConstraints.NONE, GridBagConstraints.NORTHWEST, 0, 0);
      Constrain.setConstraints(spanel, ltquartile, 0, 4, 1, 1, GridBagConstraints.NONE, GridBagConstraints.NORTHWEST, 0, 0);
      Constrain.setConstraints(spanel, llowerbound, 0, 5, 1, 1, GridBagConstraints.NONE, GridBagConstraints.NORTHWEST, 0, 0);
      Constrain.setConstraints(spanel, lupperbound, 0, 6, 1, 1, GridBagConstraints.NONE, GridBagConstraints.NORTHWEST, 0, 0);
      Constrain.setConstraints(spanel, lrows, 0, 7, 1, 1, GridBagConstraints.NONE, GridBagConstraints.NORTHWEST, 0, 0);
      Constrain.setConstraints(spanel, filter, 0, 8, 1, 1, GridBagConstraints.NONE, GridBagConstraints.NORTHWEST, 0, 0);
      Constrain.setConstraints(spanel, undo, 0, 9, 1, 1, GridBagConstraints.NONE, GridBagConstraints.NORTHWEST, 1, 1);

      // Plot
      ppanel = new MutableBoxPlotPanel(flist, slist, group, table, column);
      setLayout(new GridBagLayout());
      Constrain.setConstraints(this, ppanel, 0, 0, 1, 1, GridBagConstraints.BOTH, GridBagConstraints.NORTHWEST, 0, 0);
      Constrain.setConstraints(this, spanel, 1, 0, 1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST, 1, 1, new Insets(10, 10, 10, 10));


   }

   public void layoutLabels() {
      lminimum = new JLabel();
      lmaximum = new JLabel();
      lmedian = new JLabel();
      lfquartile = new JLabel();
      ltquartile = new JLabel();
      llowerbound = new JLabel();
      lupperbound = new JLabel();
      lrows = new JLabel();
      filter = new JButton("Filter");
      undo = new JButton("Undo");
      undo.setEnabled(false);
   }

   public void calculate() {
      ppanel.calculate();
   }

   //headless conversion support
   protected double _lowerbound;
   protected double _upperbound;
   public double getLower(){return _lowerbound;}
   public double getUpper(){return _upperbound;}

   protected boolean changed;
   public boolean getChanged(){return changed;}
   //headless conversion support



   private class MutableBoxPlotPanel extends BoxPlotPane implements ActionListener, MouseListener, MouseMotionListener {
      ArrayList flist;
      ArrayList slist;
      BoxPlotGroup group;

      //headless conversion support
      private Vector upVec;
      private Vector lowVec;
      private int vecCounter;
      //headless conversion support


      // Plot constants
      double BOUNDWIDTH = 50;

      // Sizes and offsets
      double ylowerbound;
      double yupperbound;

      // Mouse
      double lastx, lasty;

      // Data and statistics
      double lowerbound, upperbound;

      private FilterBoxPlot parent;


      MutableBoxPlotPanel(ArrayList flist, ArrayList slist, BoxPlotGroup group, Table table, int column) {
         this.flist = flist;
         this.slist = slist;
         this.group = group;
         this.table = table;
         this.column = column;

         super.calculate();
         lowerbound = sminimum;
         upperbound = smaximum;

         filter.addActionListener(this);
         undo.addActionListener(this);
         addMouseListener(this);
         addMouseMotionListener(this);

         //headless conversion support
    upVec = new Vector ();
    lowVec = new Vector ();
    vecCounter = 0;
    upVec.add(vecCounter, new Double(upperbound));
    lowVec.add(vecCounter, new Double(lowerbound));
    vecCounter ++;
    //headless conversion support


      }

      public void calculate() {
         boolean[] flags = (boolean[]) flist.get(flist.size()-1);
         ScalarStatistics statistics = TableUtilities.getScalarStatistics(flags, table, column);

         minimum = statistics.getMinimum();
         maximum = statistics.getMaximum();
         median = statistics.getMedian();
         fquartile = statistics.getFirstQuartile();
         tquartile = statistics.getThirdQuartile();
         sminimum = minimum;
         smaximum = maximum;
         lowerbound = sminimum;
         upperbound = smaximum;
      }

      public void drawPlot(Graphics graphic) {
         double x, y;
         x = scaleoffset + SCALEBUFFER + BOUNDWIDTH/2;

         // Draw minimum
         yminimum = height-BORDER-(minimum*ratio)+minimumoffset;
         graphic.drawLine((int) x, (int) yminimum, (int) (x+PLOTTICKWIDTH), (int) yminimum);

         // Draw maximum
         ymaximum = height-BORDER-(maximum*ratio)+minimumoffset;
         graphic.drawLine((int) x, (int) ymaximum, (int) (x+PLOTTICKWIDTH), (int) ymaximum);

         // Draw median
         ymedian = height-BORDER-(median*ratio)+minimumoffset;
         graphic.drawLine((int) x, (int) ymedian, (int) (x+PLOTTICKWIDTH), (int) ymedian);

         // Draw interange quartile
         yfirst = height-BORDER-(fquartile*ratio)+minimumoffset;
         ythird = height-BORDER-(tquartile*ratio)+minimumoffset;

         graphic.drawRect((int) x, (int) ythird, (int) BOXWIDTH, (int) (yfirst-ythird));

         // Draw bounds
         ylowerbound = height-BORDER-(lowerbound*ratio)+minimumoffset;
         yupperbound = height-BORDER-(upperbound*ratio)+minimumoffset;

         graphic.setColor(Color.red);
         graphic.drawLine((int) x, (int) ylowerbound, (int) (x+BOUNDWIDTH), (int) ylowerbound);
         graphic.drawLine((int) x, (int) yupperbound, (int) (x+BOUNDWIDTH), (int) yupperbound);

         // Draw lines
         graphic.setColor(Color.black);
         x += PLOTTICKWIDTH/2;
         graphic.drawLine((int) x, (int) yminimum, (int) x, (int) yfirst);
         graphic.drawLine((int) x, (int) ythird, (int) x, (int) ymaximum);

         drawStatistics();
      }

      public void drawStatistics() {
         Integer size = (Integer) slist.get(slist.size()-1);

         lminimum.setText("Minimum: " + nformat.format(minimum));
         lmaximum.setText("Maximum: " + nformat.format(maximum));
         lfquartile.setText("First Quartile: " + nformat.format(fquartile));
         lmedian.setText("Median: " + nformat.format(median));
         ltquartile.setText("Third Quartile: " + nformat.format(tquartile));
         llowerbound.setText("Lower Bound: " + nformat.format(lowerbound));
         lupperbound.setText("Upper Bound: " + nformat.format(upperbound));
         lrows.setText("Number of Rows: " + size.intValue());
      }

      public Dimension getPreferredSize() {
         return getMinimumSize();
      }

      public Dimension getMinimumSize() {
         return new Dimension(200, 300);
      }

      public void actionPerformed(ActionEvent event) {
         Object source = event.getSource();


         if (source == undo) {
            flist.remove(flist.size()-1);
            slist.remove(slist.size()-1);
            group.calculate(flist.size() > 1);
            //headless conversion support
            upVec.remove(vecCounter - 1);
           lowVec.remove(vecCounter - 1);
           vecCounter--;
           _lowerbound = ((Double)lowVec.get(vecCounter - 1)).doubleValue();
           _upperbound = ((Double)upVec.get(vecCounter - 1)).doubleValue();

           //headless conversion support

         }
         else if (source == filter) {

           //headless conversion support
           _lowerbound = lowerbound;
           _upperbound = upperbound;
           upVec.add(vecCounter, new Double(upperbound));
           lowVec.add(vecCounter, new Double(lowerbound));
           vecCounter++;
           changed = true;
           //headless conversion support

            boolean[] flags = (boolean[]) flist.get(flist.size()-1);
            boolean[] clone = new boolean[flags.length];

            int csize = flags.length;
            for (int row = 0; row < flags.length; row++) {
               if (flags[row]) {
                  double data = table.getDouble(row, column);
				  
				  if (table.isValueMissing(row, column))
				  	
				  	 // we always include missing values.
				  	 clone[row] = true;
                  else if (data < lowerbound || data > upperbound) {
                  	
                  	 // the data is out of range.
                     clone[row] = false;
                     csize--;
                  } else
                     clone[row] = true;
               }
               else {
                  clone[row] = false;
                  csize--;
               }
            }

            int size = ((Integer) slist.get(slist.size()-1)).intValue();
            if (csize < size) {
               flist.add(clone);
               slist.add(new Integer(csize));
               group.calculate(true);
            }
         }



      }

      public void mousePressed(MouseEvent event) {
         lastx = event.getX();
         lasty = event.getY();
      }

      public void mouseDragged(MouseEvent event) {
         double x = event.getX();
         double y = event.getY();
         double bound = (height-BORDER-y)/ratio+sminimum;

         if (lasty < (ylowerbound+6) && lasty > (ylowerbound-6) && bound >= minimum) {
            if (bound <= median)
               lowerbound = bound;
            else
               lowerbound = median;
         }

         if (lasty < (yupperbound+6) && lasty > (yupperbound-6) && bound <= maximum) {
            if (bound >= median)
               upperbound = bound;
            else
               upperbound = median;
         }

         lastx = x;
         lasty = y;

         repaint();
      }

      public void mouseMoved(MouseEvent event) { }
      public void mouseClicked(MouseEvent event) { }
      public void mouseReleased(MouseEvent event) { }
      public void mouseEntered(MouseEvent event) { }
      public void mouseExited(MouseEvent event) { }
   }

}