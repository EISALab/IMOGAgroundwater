package ncsa.d2k.modules.core.vis.widgets;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ncsa.d2k.modules.core.transform.binning.BinCounts;
import ncsa.gui.Constrain;

/**
 * A histogram panel.
 *
 * @author gpape (from clutter)
 */

public class Histogram extends JPanel {

   /*private static final int
      HISTOGRAM_MIN = 101,
      HISTOGRAM_MAX = 103;
   public static final int
      HISTOGRAM_UNIFORM  = 101,
      HISTOGRAM_RANGE    = 102,
      HISTOGRAM_INTERVAL = 103;
      */

   private boolean initialized = false;
   //private int behavior;
   protected int[] counts;
   protected double[] heights;
   protected double[] borders;

   //private Table tbl;
   //private NumericColumn current;
   protected int currentColumnIndex;
   protected String parameter;

   protected HashMap columnLookup;
   protected JComboBox columnSelect;

   protected JTextField n, mean, median, stddev, variance;

   protected SelectionPanel selection;
   protected HistogramPanel histogram;
   protected SliderPanel slider;
   protected VisualPanel visual;
   protected StatisticsPanel statistics;

   protected JTextField sliderReporter;

   protected BinCounts binCounts;

   public Histogram(/*Table table,*/BinCounts bc, /*int behavior,*/ String parameter,
                        HashMap lookup, String col)
      throws IllegalArgumentException {

      //if (behavior < HISTOGRAM_MIN || behavior > HISTOGRAM_MAX)
      //   throw new IllegalArgumentException("Invalid histogram behavior.");

      //this.tbl = table;
      this.binCounts = bc;
      //this.behavior = behavior;
      this.parameter = parameter.substring(0);

      //columnLookup = new HashMap();
      columnSelect = new JComboBox();

      boolean found_numeric = false;


      class NameIndex implements Comparable {
        String name;
        int index;

        public int compareTo(Object o) {
            NameIndex other = (NameIndex)o;
            if(other.index > index)
                return -1;
            if(other.index == index)
                return 0;
            else
                return 1;
        }
      }

      /*for (int i = 0; i < table.getNumColumns(); i++) {
         //if (table.getColumn(i) instanceof NumericColumn) {
       if(table.isColumnNumeric(i)) {
            if (!found_numeric) {
               //current = (NumericColumn)table.getColumn(i);
            currentColumnIndex = i;
               found_numeric = true;
            }
            columnLookup.put(table.getColumnLabel(i), new Integer(i));
            columnSelect.addItem(table.getColumnLabel(i));
         }
      }*/

    ArrayList cols = new ArrayList();

      Iterator iter = lookup.keySet().iterator();
        while(iter.hasNext()) {
            String columnLabel = (String)iter.next();

            Integer columnIndex = (Integer)lookup.get(columnLabel);

            NameIndex ni = new NameIndex();
            ni.name = columnLabel;
            ni.index = columnIndex.intValue();

            //columnSelect.addItem(columnLabel);
            cols.add(ni);
        }
        Collections.sort(cols);
        for(int i = 0; i < cols.size(); i++) {
            NameIndex ni = (NameIndex)cols.get(i);
            columnSelect.addItem(ni.name);
        }
        columnLookup = lookup;

        currentColumnIndex =  ((Integer)columnLookup.get(/*columnSelect.getSelectedItem()*/col)).intValue();
        columnSelect.setSelectedItem(col);

      sliderReporter = new JTextField(6);
      sliderReporter.setEditable(false);
      sliderReporter.setBackground(Color.white);
      sliderReporter.setHorizontalAlignment(JTextField.RIGHT);

      histogram = new HistogramPanel();  // order is important here!
      slider = createSliderPanel();
      //selection = new SelectionPanel();
      selection = createSelectionPanel();
      visual = createVisualPanel();
      //statistics = new StatisticsPanel();
      statistics = createStatisticsPanel();
      //columnSelect.addActionListener(new SelectionListener());
      columnSelect.addActionListener(createSelectionListener());
      calculateBins();
      statistics.updateStatistics();

      this.setLayout(new BorderLayout());
      this.add(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
         visual, statistics));

      initialized = true;

   }

   public JSlider getSlider() {
      return slider.getSlider();
   }

   public int getSelection() {
// !:
      return columnSelect.getSelectedIndex();
// return ((Integer)columnLookup.get(columnSelect.getSelectedItem())).intValue();
   }

   public double getPercentage() {
      return .01*((double)slider.getValue());
   }

   protected VisualPanel createVisualPanel() {
        return new VisualPanel();
   }

   protected SliderPanel createSliderPanel() {
        return new SliderPanel();
   }

   protected StatisticsPanel createStatisticsPanel() {
        return new StatisticsPanel();
   }

   protected SelectionPanel createSelectionPanel() {
        return new SelectionPanel();
   }

   protected SelectionListener createSelectionListener() {
        return new SelectionListener();
   }

   protected void calculateBins() {
   }

   protected class VisualPanel extends JPanel {

      /*protected VisualPanel() {

         this.setLayout(new GridBagLayout());
         Constrain.setConstraints(this, selection, 0, 0, 1, 1,
            GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTH, 1, 0);
         Constrain.setConstraints(this, histogram, 0, 1, 1, 1,
            GridBagConstraints.BOTH, GridBagConstraints.CENTER, 1, 1);

         if (behavior == HISTOGRAM_UNIFORM || behavior == HISTOGRAM_INTERVAL)
            Constrain.setConstraints(this, slider, 0, 2, 1, 1,
               GridBagConstraints.HORIZONTAL, GridBagConstraints.SOUTH, 1, 0);

      }
      */

      public Dimension getPreferredSize() {
         return new Dimension(400, 400);
      }
   }

   protected class SelectionPanel extends JPanel {

      protected SelectionPanel() {

         this.setBorder(new TitledBorder(" Select attribute: "));
         this.setLayout(new GridBagLayout());
         Constrain.setConstraints(this, columnSelect, 0, 0, 1, 1,
            GridBagConstraints.BOTH, GridBagConstraints.CENTER, 1, 1);
      }
   }

   protected class HistogramPanel extends JPanel implements MouseMotionListener {

      boolean calculated;
      int width, height, offset_x, offset_y, hist_x, hist_y;

      Graphics2D g2;

      protected HistogramPanel() {
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
            xloc += increment;

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

   }

   protected class SliderPanel extends JPanel {
      protected JSlider slide;
/*
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
 /*               double min = binCounts.getMin(currentColumnIndex);
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
*/
      public int getValue() {
         return slide.getValue();
      }

      public JSlider getSlider() {
         return slide;
      }

        protected class SliderListener implements ChangeListener {
            public void stateChanged(ChangeEvent e) {
                calculateBins();
                //if (behavior == HISTOGRAM_INTERVAL)
                //    sliderReporter.setText(Integer.toString(slider.getValue()) + "%");
         //else
                sliderReporter.setText(Integer.toString(slider.getValue()));
                histogram.repaint();
            }
        }
   }

   protected class StatisticsPanel extends JPanel {

      private NumberFormat N;

      protected StatisticsPanel() {

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

      private void updateStatistics() {

         n.setText(N.format(/*tbl.*/binCounts.getNumRows()));

         double sample_mean, sample_variance;
         double[] d = new double[/*tbl.*/binCounts.getNumRows()];

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
         double max = binCounts.getMax(currentColumnIndex);
            double min = binCounts.getMin(currentColumnIndex);
            double total = binCounts.getTotal(currentColumnIndex);

         sample_mean = total/(double)/*tbl.*/binCounts.getNumRows();
         mean.setText(N.format(sample_mean));

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

   }

   private class SelectionListener extends AbstractAction {
      public void actionPerformed(ActionEvent e) {
         //current = (NumericColumn)table.getColumn(((Integer)
         currentColumnIndex = ((Integer)columnLookup.get(columnSelect.getSelectedItem())).intValue();
         calculateBins();
         statistics.updateStatistics();
         histogram.repaint();
      }
   }
}