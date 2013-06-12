package ncsa.d2k.modules.core.vis.widgets;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Arrays;
import java.util.HashMap;
import java.util.StringTokenizer;

import ncsa.d2k.modules.core.transform.binning.BinCounts;
import ncsa.gui.Constrain;


/**
 * A histogram panel.
 *
 * @author gpape (from clutter)
 */

public class RangeHistogram extends Histogram {

   public RangeHistogram(/*Table table,*/BinCounts bc, String parameter, HashMap lookup, String col)
      throws IllegalArgumentException {

      super(bc, parameter, lookup, col);
   }

   protected VisualPanel createVisualPanel() {
        return new RangeVisualPanel();
   }

   protected void calculateBins() {

      int index;
      double max, min;

            StringTokenizer strTok = new StringTokenizer(parameter, ",");
            borders = new double[strTok.countTokens()];
            // double[] binMaxes = borders;

            int idx = 0;
            try {
               while(strTok.hasMoreElements()) {
                  String s = (String)strTok.nextElement();
                  borders[idx++] = Double.parseDouble(s);
               }
            }
            catch(NumberFormatException e) { return; }

            Arrays.sort(borders);

            counts = new int[borders.length + 1];
            heights = new double[counts.length];
            // borders = new double[0];

            for (int i = 0; i < counts.length; i++)
               counts[i] = 0;

            // some redundancy here
            /*boolean found;
            for (int i = 0; i < tbl.getNumRows(); i++) {
               found = false;
               for (int j = 0; j < borders.length; j++) {
                  if (tbl.getDouble(i, currentColumnIndex) <= borders[j] && !found) {
                     counts[j]++;
                     found = true;
                     break;
                  }
               }
               if (!found)
                  counts[borders.length]++;
            }*/
            counts = binCounts.getCounts(currentColumnIndex, borders);

            for (int i = 0; i < heights.length; i++)
               heights[i] = (double)counts[i] / (double)/*tbl.*/binCounts.getNumRows();

   }

   private class RangeVisualPanel extends VisualPanel {

      private RangeVisualPanel() {

         this.setLayout(new GridBagLayout());
         Constrain.setConstraints(this, selection, 0, 0, 1, 1,
            GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTH, 1, 0);
         Constrain.setConstraints(this, histogram, 0, 1, 1, 1,
            GridBagConstraints.BOTH, GridBagConstraints.CENTER, 1, 1);

         /*if (behavior == HISTOGRAM_UNIFORM || behavior == HISTOGRAM_INTERVAL)
            Constrain.setConstraints(this, slider, 0, 2, 1, 1,
               GridBagConstraints.HORIZONTAL, GridBagConstraints.SOUTH, 1, 0);
            */
      }

      public Dimension getPreferredSize() {
         return new Dimension(400, 400);
      }
   }

   /*private class SelectionPanel extends JPanel {

      public SelectionPanel() {

         this.setBorder(new TitledBorder(" Select attribute: "));
         this.setLayout(new GridBagLayout());
         Constrain.setConstraints(this, columnSelect, 0, 0, 1, 1,
            GridBagConstraints.BOTH, GridBagConstraints.CENTER, 1, 1);

      }

   }

   private class HistogramPanel extends JPanel implements MouseMotionListener {

      boolean calculated;
      int width, height, offset_x, offset_y, hist_x, hist_y;

      Graphics2D g2;

      public HistogramPanel() {
         calculated = false;
         addMouseMotionListener(this);
      }

      public void setBounds(int x, int y, int w, int h) {
         super.setBounds(x, y, w, h);
         width = w;
         height = h;
         offset_x = (int)(.1*width);
         offset_y = (int)(.1*height);
         hist_x = (int)(.8*width);
         hist_y = (int)(.8*height);
         calculated = true;
      }

      protected void paintComponent(Graphics g) {

         if (!calculated || !initialized)
            return;

         double increment = (double)hist_x/(double)heights.length;

         NumberFormat N = NumberFormat.getInstance();
         N.setMaximumFractionDigits(3);
         String str;

         g2 = (Graphics2D)g;
         g2.setColor(new Color(235, 235, 235));
         g2.fillRect(0, 0, width, height);

         FontMetrics M = g2.getFontMetrics();

         double xloc = (double)offset_x;
         double xlast = 0;
         for (int i = 0; i < heights.length; i++) {

            g2.setColor(new Color((int)(255 - 255*i/heights.length), 51, (int)(255*i/heights.length)));
            g2.fillRect(
               (int)xloc,
               (int)(offset_y + (1-heights[i])*hist_y),
               (int)(xloc + increment) - (int)(xloc), // diminishes rounding errors
               (offset_y + hist_y) - (int)(offset_y + (1-heights[i])*hist_y));
            g2.setColor(new Color(0, 0, 0));
            g2.drawRect(
               (int)xloc,
               (int)(offset_y + (1-heights[i])*hist_y),
               (int)(xloc + increment) - (int)(xloc),
               (offset_y + hist_y) - (int)(offset_y + (1-heights[i])*hist_y));
/*
            if (i > 0 && borders.length > 0) {

               str = N.format(borders[i-1]);

               if (xloc - M.stringWidth(str)/2 - 4 > xlast) {

                  g2.drawString(
                     str,
                     (int)(xloc - M.stringWidth(str)/2),
                     offset_y + hist_y + M.getAscent() + 5
                  );
                  xlast = xloc + M.stringWidth(str)/2;

               }

            }
*/
 /*           xloc += increment;

         }

      }

      public void mouseDragged(MouseEvent e) { }

      public void mouseMoved(MouseEvent e) {

         if (heights == null || heights.length < 2) {
            setToolTipText(null);
            return;
         }

         int x = e.getX(), y = e.getY();

         double xloc = (double)offset_x;
         double increment = (double)(hist_x / heights.length);

         NumberFormat N = NumberFormat.getInstance();
         N.setMaximumFractionDigits(3);

         boolean hit = false;
         for (int i = 0; i < heights.length; i++) {
            if (x >= xloc && x <= xloc + increment) {
               if (y <= offset_y + hist_y &&
                   y >= offset_y + hist_y - heights[i]*hist_y) {

                  if (i == 0)
                     setToolTipText("(..., " + N.format(borders[0]) + "]: " +
                        counts[i]);
                  else if (i == heights.length - 1)
                     setToolTipText("(" + N.format(borders[borders.length - 1])
                        + ", ...): " + counts[i]);
                  else
                     setToolTipText("(" + N.format(borders[i - 1]) + ", " +
                        N.format(borders[i]) + "]: " + counts[i]);

                  hit = true;

               }
            }
            if (hit)
               break;
            else
               xloc += increment;
         }
         if (!hit) {
            setToolTipText(null);
         }

      }

   }*/

   /*private class SliderPanel extends JPanel {

      private JSlider slide;

      public SliderPanel() {

         int val = 5;
         double dval;
         switch(behavior) {

            case HISTOGRAM_UNIFORM:

               this.setBorder(new TitledBorder(" Number of bins: "));
               try { val = Integer.parseInt(parameter); }
               catch(NumberFormatException e) { val = 5; break; }
               break;

            case HISTOGRAM_INTERVAL:

                // get the max and min
               /*double min = Double.MAX_VALUE, max = Double.MIN_VALUE;
               for (int i = 0; i < tbl.getNumRows(); i++) {
                  if (tbl.getDouble(i, currentColumnIndex) < min)
                     min = tbl.getDouble(i, currentColumnIndex);
                  if (tbl.getDouble(i, currentColumnIndex) > max)
                     max = tbl.getDouble(i, currentColumnIndex);
               }
                */
    /*            double min = binCounts.getMin(currentColumnIndex);
                double max = binCounts.getMax(currentColumnIndex);

               this.setBorder(new TitledBorder(" Bin size as a percentage of interval: "));
               try { dval = Double.parseDouble(parameter); }
               catch(NumberFormatException e) { val = 50; break; }

               if (dval < 0 || dval > (double)(max - min))
                  val = 100;
               else
                  val = (int)(100.0 * (dval/(double)(max - min)));

               break;

         }

         if (behavior == HISTOGRAM_INTERVAL) {
            slide = new JSlider(1, 100, val);
            slide.setMajorTickSpacing(99);
            sliderReporter.setText(Integer.toString(val) + "%");
         }
         else {
            slide = new JSlider(1, val*2, val);
            slide.setMajorTickSpacing(val*2 - 1);
            sliderReporter.setText(Integer.toString(val));
         }

         slide.setPaintTicks(true);
         slide.setPaintLabels(true);
         slide.addChangeListener(new SliderListener());

         this.setLayout(new GridBagLayout());
         Constrain.setConstraints(this, slide, 0, 0, 1, 1,
            GridBagConstraints.BOTH, GridBagConstraints.CENTER, 1, 1);
         Constrain.setConstraints(this, sliderReporter, 1, 0, 1, 1,
            GridBagConstraints.NONE, GridBagConstraints.EAST, 0, 0);

      }

      public int getValue() {
         return slide.getValue();
      }

      public JSlider getSlider() {
         return slide;
      }

   }

   private class RangeSliderListener implements ChangeListener {
      public void stateChanged(ChangeEvent e) {
         calculateBins();
         sliderReporter.setText(Integer.toString(slider.getValue()));
         histogram.repaint();
      }
   }*/
}
