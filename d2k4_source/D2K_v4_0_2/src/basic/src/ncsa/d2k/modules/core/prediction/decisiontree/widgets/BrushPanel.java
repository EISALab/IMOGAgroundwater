package ncsa.d2k.modules.core.prediction.decisiontree.widgets;

import java.awt.*;
import java.awt.geom.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import ncsa.d2k.modules.core.prediction.decisiontree.*;

/*
 DecisionTreeVis
 Draws data when mouse moves over a node in tree scroll pane
 */
public final class BrushPanel extends JPanel {

  View view;

  public BrushPanel(ViewableDTModel model) {
    DecisionTreeScheme scheme = new DecisionTreeScheme();

    setOpaque(true);
    setBackground(scheme.borderbackgroundcolor);
  }

  public void updateBrush(View view) {
    this.view = view;

    revalidate();
    repaint();
  }

  public void paintComponent(Graphics g) {
    super.paintComponent(g);

    Graphics2D g2 = (Graphics2D) g;

    Insets insets = getInsets();

    if (view != null) {
      g2.translate(insets.left, insets.top);
      view.drawBrush(g2);
      g2.translate(-insets.left, -insets.top);
    }
  }

  /*
   ViewableDTModel dmodel;
   ViewableDTNode droot;
   ViewableDTNode dnode;
   double samplesize = 10;
   double samplespace = 8;
   double outputwidth = 80;
   double outputspace = 10;
   double tallywidth;
   double tallyspace = 10;
   double percentwidth;
// Outputs
   String[] outputs;
// Distribution values
   double[] values;
   int[] tallies;
   DecisionTreeScheme scheme;
   FontMetrics metrics;
   int ascent, descent;
   NumberFormat numberformat;
   public BrushPanel(ViewableDTModel model) {
    dmodel = model;
    droot = dmodel.getViewableRoot();
    //outputs = model.getUniqueOutputValues();


    metrics = getFontMetrics(scheme.textfont);
    ascent = metrics.getAscent();
    descent = metrics.getDescent();
    percentwidth = metrics.stringWidth("100.0%");
    numberformat = NumberFormat.getInstance();
    numberformat.setMaximumFractionDigits(1);
    setOpaque(true);
    setBackground(scheme.borderbackgroundcolor);
   }
   public void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2.setFont(scheme.textfont);
    Insets insets = getInsets();
    double x = insets.left;
    double y = insets.top;
    if (dnode != null) {
     values = new double[outputs.length];
     tallies = new int[outputs.length];
     for (int index = 0; index < values.length; index++) {
      try{
       if (dnode.getTotal() == 0)
        values[index] = 0;
       else
       ;	//values[index] = 100*(double)dnode.getOutputTally(outputs[index])/(double)dnode.getTotal();
       //tallies[index] = dnode.getOutputTally(outputs[index]);
      } catch(Exception exception){
       System.out.println("getOutputTally threw an exception");
      }
     }
     for (int index = 0; index < tallies.length; index++) {
      double stringwidth = metrics.stringWidth(Integer.toString(tallies[index]));
      if (stringwidth > tallywidth)
       tallywidth = stringwidth;
     }
    }
    for (int index = 0; index < outputs.length; index++) {
     g2.setColor(scheme.getNextColor());
     g2.fill(new Rectangle2D.Double(x, y, samplesize, samplesize));
     x += samplesize + samplespace;
     y += samplesize;
     String output = outputs[index];
     g2.setColor(scheme.textcolor);
     g2.drawString(output, (int) x, (int) y);
     if (dnode != null) {
      x += outputwidth + outputspace;
      String tally = Integer.toString(tallies[index]);
      g2.drawString(tally, (int) x, (int) y);
      x += tallywidth + tallyspace;
      String value = numberformat.format(values[index]) + "%";
      g2.drawString(value, (int) x, (int) y);
     }
     x = insets.left;
     y += samplespace;
    }
   }
// Called by tree scroll pane
   public void updateBrush(ViewableDTNode node) {
    dnode = node;
    repaint();
   }*/

  public Dimension getPreferredSize() {

    /*Insets insets = getInsets();
         int pwidth = (int) (insets.left + samplesize + samplespace + outputwidth + outputspace + tallywidth + tallyspace + percentwidth + insets.right);
         int pheight = (int) (insets.top + samplesize*(outputs.length) + samplespace*(outputs.length-1) + descent + insets.bottom);
         return new Dimension(pwidth, pheight);*/

    Insets insets = getInsets();

    double width = insets.left + insets.right;
    double height = insets.top + insets.bottom;

    if (view != null) {
      width += view.getBrushWidth();
      height += view.getBrushHeight();
    }

    return new Dimension((int) width, (int) height);
  }

  public Dimension getMinimumSize() {
    return getPreferredSize();
  }
}