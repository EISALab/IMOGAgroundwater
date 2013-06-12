package ncsa.d2k.modules.core.vis.widgets;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import ncsa.gui.*;
import ncsa.d2k.modules.core.transform.binning.*;

/**
 * A histogram panel.
 *
 * @author gpape (from clutter)
 */

public class UniformHistogram extends Histogram {

/*   public UniformHistogram(BinCounts bc, String parameter, HashMap lookup)
      throws IllegalArgumentException {

      super(bc, parameter, lookup);
      System.out.println("UH BC: "+binCounts);
   }
    */

   public UniformHistogram(/*Table table,*/BinCounts bc, String parameter, HashMap lookup, String colName)
      throws IllegalArgumentException {

      super(bc, parameter, lookup, colName);
   }

   protected VisualPanel createVisualPanel() {
        return new UniformVisualPanel();
   }

   protected SliderPanel createSliderPanel() {
        return new UniformSliderPanel();
   }

   /*protected StatisticsPanel createStatisticsPanel() {
        return null;
   }

   protected SelectionPanel createSelectionPanel() {
        return null;
   }*/

   protected void calculateBins() {

      int index;
      double max, min;

      counts = new int[slider.getValue()];
            for (int i = 0; i < counts.length; i++)
               counts[i] = 0;
            heights = new double[counts.length];
            borders = new double[counts.length - 1];

            // get the max and min
            min = binCounts.getMin(currentColumnIndex);
            max = binCounts.getMax(currentColumnIndex);

            double increment = (max - min) / (double)counts.length, ceiling;

            if (borders.length > 0) {
               borders[0] = min + increment;
               for (int i = 1; i < borders.length; i++)
                  borders[i] = borders[i - 1] + increment;
            }

            /*for (int i = 0; i < tbl.getNumRows(); i++) {
               index = (int)((tbl.getDouble(i, currentColumnIndex) - min)/increment);
               if (index == counts.length) index--;
               counts[index]++;
            }*/
            counts = binCounts.getCounts(currentColumnIndex, borders);

            for (int i = 0; i < heights.length; i++) {
                //System.out.println("BINCOUNTS: "+binCounts);
                //System.out.println("heights: "+heights);
                //System.out.println("counts: "+counts);
               heights[i] = (double)counts[i] / (double)/*tbl.*/binCounts.getNumRows();
            }
   }

   private class UniformVisualPanel extends VisualPanel {

      public UniformVisualPanel() {

         this.setLayout(new GridBagLayout());
         Constrain.setConstraints(this, selection, 0, 0, 1, 1,
            GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTH, 1, 0);
         Constrain.setConstraints(this, histogram, 0, 1, 1, 1,
            GridBagConstraints.BOTH, GridBagConstraints.CENTER, 1, 1);

            Constrain.setConstraints(this, slider, 0, 2, 1, 1,
               GridBagConstraints.HORIZONTAL, GridBagConstraints.SOUTH, 1, 0);
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

   }*/

   /*private class HistogramPanel extends JPanel implements MouseMotionListener {

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
    /*        xloc += increment;

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

   private class UniformSliderPanel extends SliderPanel {

      private JSlider slide;

      private UniformSliderPanel() {

         int val = 5;
         double dval;
         //switch(behavior) {

         //   case HISTOGRAM_UNIFORM:

         this.setBorder(new TitledBorder(" Number of bins: "));
         try { val = Integer.parseInt(parameter); }
         catch(NumberFormatException e) { val = 5; /*break;*/ }
         //      break;

         /*   case HISTOGRAM_INTERVAL:

                // get the max and min
               /*double min = Double.MAX_VALUE, max = Double.MIN_VALUE;
               for (int i = 0; i < tbl.getNumRows(); i++) {
                  if (tbl.getDouble(i, currentColumnIndex) < min)
                     min = tbl.getDouble(i, currentColumnIndex);
                  if (tbl.getDouble(i, currentColumnIndex) > max)
                     max = tbl.getDouble(i, currentColumnIndex);
               }
                */
         /*       double min = binCounts.getMin(currentColumnIndex);
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
         */
            slide = new JSlider(1, val*2, val);
            slide.setMajorTickSpacing(val*2 - 1);
            sliderReporter.setText(Integer.toString(val));
         //}

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

   /*private class StatisticsPanel extends JPanel {

      private NumberFormat N;

      public StatisticsPanel() {

         n = new JTextField(10);
         n.setEditable(false);
         n.setBorder(null);
         mean = new JTextField(10);
         mean.setEditable(false);
         mean.setBorder(null);
         median = new JTextField(10);
         median.setEditable(false);
         median.setBorder(null);
         stddev = new JTextField(10);
         stddev.setEditable(false);
         stddev.setBorder(null);
         variance = new JTextField(10);
         variance.setEditable(false);
         variance.setBorder(null);

         N = NumberFormat.getInstance();
         N.setMaximumFractionDigits(4);

         this.setLayout(new GridBagLayout());
         Constrain.setConstraints(this, new JLabel("N:"), 0, 0, 1, 1,
            GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTH, .5, 0);
         Constrain.setConstraints(this, n, 1, 0, 1, 1,
            GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTH, .5, 0);
         Constrain.setConstraints(this, new JLabel("Mean:"), 0, 1, 1, 1,
            GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTH, .5, 0);
         Constrain.setConstraints(this, mean, 1, 1, 1, 1,
            GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTH, .5, 0);
         Constrain.setConstraints(this, new JLabel("Median:"), 0, 2, 1, 1,
            GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTH, .5, 0);
         Constrain.setConstraints(this, median, 1, 2, 1, 1,
            GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTH, .5, 0);
         Constrain.setConstraints(this, new JLabel("Std. Dev:"), 0, 3, 1, 1,
            GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTH, .5, 0);
         Constrain.setConstraints(this, stddev, 1, 3, 1, 1,
            GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTH, .5, 0);
         Constrain.setConstraints(this, new JLabel("Variance:"), 0, 4, 1, 1,
            GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTH, .5, 0);
         Constrain.setConstraints(this, variance, 1, 4, 1, 1,
            GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTH, .5, 0);
         Constrain.setConstraints(this, new JLabel(""), 1, 5, 2, 1,
            GridBagConstraints.BOTH, GridBagConstraints.CENTER, 1, 1);

      }

      public void updateStatistics() {

         n.setText(N.format(/*tbl.*///binCounts.getNumRows()));

    /*     double sample_mean, sample_variance;
         double[] d = new double[/*tbl.*///binCounts.getNumRows()];

        // get the max and min
         /*double total = 0, max = Double.MIN_VALUE, min = Double.MAX_VALUE;
         for (int i = 0; i < d.length; i++) {

            d[i] = tbl.getDouble(i, currentColumnIndex);
            total += d[i];

            if (tbl.getDouble(i, currentColumnIndex) > max)
               max = tbl.getDouble(i, currentColumnIndex);
            if (tbl.getDouble(i, currentColumnIndex) < min)
               min = tbl.getDouble(i, currentColumnIndex);

         }*/
    /*     double max = binCounts.getMax(currentColumnIndex);
            double min = binCounts.getMin(currentColumnIndex);
            double total = binCounts.getTotal(currentColumnIndex);

         sample_mean = total/(double)/*tbl.*///binCounts.getNumRows();
     /*    mean.setText(N.format(sample_mean));

         Arrays.sort(d);
         median.setText(N.format((d[(int)Math.ceil(d.length/2.0)] + d[(int)Math.floor(d.length/2.0)])/2.0));

         total = 0; // for calculating sample variance
         for (int i = 0; i < d.length; i++)
            total += (d[i] - sample_mean) * (d[i] - sample_mean);

         sample_variance = total / (double)(d.length - 1); // unbiased estimator

         variance.setText(N.format(sample_variance));
         stddev.setText(N.format(Math.sqrt(sample_variance)));

      }

      public Dimension getPreferredSize() {
         return new Dimension(200, 400);
      }

   }*/

   /*private class SelectionListener extends AbstractAction {
      public void actionPerformed(ActionEvent e) {
         //current = (NumericColumn)table.getColumn(((Integer)
         currentColumnIndex = ((Integer)columnLookup.get(columnSelect.getSelectedItem())).intValue();
         calculateBins();
         statistics.updateStatistics();
         histogram.repaint();
      }
   }*/

   /*private class SliderListener implements ChangeListener {
      public void stateChanged(ChangeEvent e) {
         calculateBins();
         if (behavior == HISTOGRAM_INTERVAL)
            sliderReporter.setText(Integer.toString(slider.getValue()) + "%");
         else
            sliderReporter.setText(Integer.toString(slider.getValue()));
         histogram.repaint();
      }
   }
   }*/
}
