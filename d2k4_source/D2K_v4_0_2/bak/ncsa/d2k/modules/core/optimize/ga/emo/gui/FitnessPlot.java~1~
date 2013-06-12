package ncsa.d2k.modules.core.optimize.ga.emo.gui;

import java.text.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import javax.swing.*;
import java.util.*;


/**
 * Fitness plot is a scatter plot graph that plots two fitness functions.  The
 * points are drawn on a BufferedImage that is only updated when the points are
 * changed.
 */
abstract public class FitnessPlot
    extends JPanel
    implements MouseListener, MouseMotionListener {

  // the index of the objectives
  protected int xObjective, yObjective;

  // the min and maxes of the data
  protected double xMin, xMax, yMin, yMax;

  // the min and maxes for the scales
  protected float xScaleMin, xScaleMax, yScaleMin, yScaleMax;

  // the background buffer
  protected Image image;

  // true if the data has changed in some way -- then the image must be redone
  private boolean changed = false;

  protected float top, left, right, bottom;

  protected float graphwidth, graphheight;

  protected int startx, starty, endx, endy;
  protected boolean mousePressed;

  protected NumberFormat nf;

  protected Font font;

  protected boolean selectionEnabled;

  protected java.util.List selectionChangedListeners;

  public FitnessPlot() {
    setDoubleBuffered(false);
    top = left = right = bottom = 20.0f;
    ToolTipManager.sharedInstance().registerComponent(this);
    addMouseListener(this);
    addMouseMotionListener(this);
    startx = starty = endx = endy = -1;
    nf = NumberFormat.getInstance();
    nf.setMaximumFractionDigits(1);
    font = new Font("Arial", Font.PLAIN, 5);
    selectionEnabled = false;
    selectionChangedListeners = new ArrayList();
  }

  public void setBounds(int x, int y, int w, int h) {
    super.setBounds(x, y, w, h);
    if (image != null) {
      image.flush();

    }
    if (w != 0 && h != 0) {
      if (image != null) {
        image.flush();
      }
      image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
      graphwidth = (float) w;
      graphheight = (float) h;
      startx = starty = endx = endy = -1;
    }

    setChanged(true);
  }

  /**
   * Enable points to be selected by drawing a box.
   */
  public void enableSelection() {
    selectionEnabled = true;
  }

  /**
   * Disable points to be selected.
   */
  public void disableSelection() {
    selectionEnabled = false;
  }

  /**
   * Called when a point is selected.
   * @param i the index of the row in the table.
   */
  protected void addSelection(int i) {
  }

  /**
   * Clear all the selections made by the user.
   */
  protected void removeAllSelections() {
  }

  /**
   * Set this to be changed.  The image will be re-created upon repaint
   * if this has been changed.
   * @param b
   */
  public void setChanged(boolean b) {
    changed = b;
  }

  public void mousePressed(MouseEvent e) {
    if (selectionEnabled) {
      startx = e.getX();
      starty = e.getY();
      mousePressed = true;
    }
  }

  public void mouseClicked(MouseEvent e) {}
  public void mouseEntered(MouseEvent e) {}
  public void mouseExited(MouseEvent e) {}

  /**
   * When the mouse is released, select all points within the box drawn.
   * @param e
   */
  public void mouseReleased(MouseEvent e) {
    if (selectionEnabled && mousePressed) {
      mousePressed = false;
      removeAllSelections();

      // now, given the startx, starty, endx, endy
      // determine which points lie inside the boundaries of the box
      float smallestX = 0, smallestY = 0, biggestX = 0, biggestY = 0;

      float[] xys = new float[2];
      if (endx > startx) {
        if (endy > starty) {
          //g2.draw(new Rectangle(startx, starty, endx - startx, endy - starty));

          getXYVals(startx, endy, xys);
          smallestX = xys[0];
          smallestY = xys[1];
          getXYVals(endx, starty, xys);
          biggestX = xys[0];
          biggestY = xys[1];
        }
        else {
          //g2.draw(new Rectangle(startx, endy, endx - startx, starty - endy));
          getXYVals(startx, starty, xys);
          smallestX = xys[0];
          smallestY = xys[1];
          getXYVals(endx, endy, xys);
          biggestX = xys[0];
          biggestY = xys[1];
        }
      }
      else {
        if (endy > starty) {
          //g2.draw(new Rectangle(endx, starty, startx - endx, endy - starty));
          getXYVals(endx, endy, xys);
          smallestX = xys[0];
          smallestY = xys[1];
          getXYVals(startx, starty, xys);
          biggestX = xys[0];
          biggestY = xys[1];
        }
        else {
          //g2.draw(new Rectangle(endx, endy, startx - endx, starty - endy));
          getXYVals(endx, starty, xys);
          smallestX = xys[0];
          smallestY = xys[1];
          getXYVals(startx, endy, xys);
          biggestX = xys[0];
          biggestY = xys[1];
        }
      }

      smallestX -= TINY;
      smallestY -= TINY;
      biggestX -= TINY;
      biggestY -= TINY;

      int numInd = getNumIndividuals();
      // for each row of the table
      for (int i = 0; i < numInd; i++) {
        double xval = getXValue(i);
        double yval = getYValue(i);

        // if the point is inside the box
        if (xval >= smallestX && xval <= biggestX) {
          if (yval >= smallestY && yval <= biggestY) {
            // select it
            addSelection(i);
            setChanged(true);
          }
        }
      }
    repaint();
    this.fireSelectionChanged();
    }
  }

  /**
   * Get the x value for the ith point to be plotted.
   * @param i
   * @return
   */
  abstract protected double getXValue(int i);

  /**
   * Get the y value for the ith point to be plotted.
   * @param i
   * @return
   */
  abstract protected double getYValue(int i);

  /**
   * Get the number of individuals to be plotted.
   * @return
   */
  abstract protected int getNumIndividuals();

  private static float TINY = 0.00001f;

  public void mouseMoved(MouseEvent e) {}

  public void mouseDragged(MouseEvent e) {
    if (mousePressed && selectionEnabled) {
      endx = e.getX();
      endy = e.getY();

      repaint();
    }
  }

  /**
   * Set the indices of the objectives to draw
   * @param x the objective to plot on the x-axis
   * @param y the objective to plot on the y-axis
   */
  abstract public void setObjectives(int x, int y);


  /**
   * Find the minimum and maximum and repaint.
   */
  public void redraw() {
    findMinMax();
    repaint();
  }

  protected abstract void findMinMax();

  public String getToolTipText(MouseEvent e) {
    int xloc = e.getX();
    int yloc = e.getY();

    // now translate this from screen coordinates to graph coordinates
    if (xloc < left || xloc > (graphwidth - right)) {
      return "";
    }
    else if (yloc > (graphheight - bottom) || yloc < top) {
      return "";
    }
    else {
      //float xscale = (xMax-xMin)/(graphwidth-left-right);
      //float yscale = (yMax-yMin)/(graphheight-top-bottom);

      float[] xys = new float[2];
      getXYVals(xloc, yloc, xys);
      float xval = xys[0]; //xloc*xscale - xscale*left + xMin;
      float yval = xys[1]; //yMin-yscale*(yloc-graphheight+bottom);

      return new String("<html>x: " + xval + "<br>y:" + yval + "</html>");
    }
  }

  protected void getXYVals(int xloc, int yloc, float[] retVal) {
    double xscale = (xMax - xMin) / (graphwidth - left - right);
    double yscale = (yMax - yMin) / (graphheight - top - bottom);

    double xval = xloc * xscale - xscale * left + xMin;
    double yval = yMin - yscale * (yloc - graphheight + bottom);
    retVal[0] = (float) xval;
    retVal[1] = (float) yval;
  }

  protected boolean isChanged() {
    return changed;
  }

  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g;
    // only update the image if the changed flag has been set
    if (isChanged()) {
      image.flush();
      Graphics2D imgG2 = (Graphics2D) image.getGraphics();

      double xscale = (xMax - xMin) / (graphwidth - left - right);
      double yscale = (yMax - yMin) / (graphheight - top - bottom);

      imgG2.setColor(Color.white);
      imgG2.fill(new Rectangle2D.Double(0, 0, graphwidth, graphheight));

      imgG2.setColor(Color.black);
      // draw axes
      imgG2.draw(new Line2D.Double(left, top, left, graphheight - bottom));
      imgG2.draw(new Line2D.Double(left, graphheight - bottom,
                                   graphwidth - right, graphheight - bottom));

      // draw the scale
      g2.setFont(font);
      FontMetrics fm = g2.getFontMetrics();

      String x_min = nf.format(xMin);
      imgG2.drawString(x_min, left - fm.stringWidth(x_min) / 2,
                       graphheight - bottom + fm.getHeight() + 6);
      String x_max = nf.format(xMax);
      imgG2.drawString(x_max, graphwidth - right - fm.stringWidth(x_max) / 2,
                       graphheight - bottom + fm.getHeight() + 6);

      AffineTransform saveXForm = imgG2.getTransform();
      String y_min = nf.format(yMin);
      double x1 = left - fm.stringWidth(y_min) / 2 - 2;
      double y1 = graphheight - bottom + fm.getHeight() + 6;
      imgG2.rotate(Math.toRadians(270), x1, y1);
      imgG2.drawString(y_min, (int) x1, (int) y1);
      imgG2.setTransform(saveXForm);

      String y_max = nf.format(yMax);
      x1 = left - fm.stringWidth(y_min) / 2 - 2;
      y1 = top + 2 * fm.stringWidth(y_max);
      imgG2.rotate(Math.toRadians(270), x1, y1);
      imgG2.drawString(y_max, (int) x1, (int) y1);
      imgG2.setTransform(saveXForm);

      // draw population
      drawPoints(imgG2, xscale, yscale);
    }
    g2.drawImage(image, 0, 0, null);
    g2.setColor(Color.black);
    //if(endx != -1 && endy != -1 && startx != -1 && starty != -1) {
    if (mousePressed) {
      if (endx > startx) {
        if (endy > starty) {
          g2.draw(new Rectangle(startx, starty, endx - startx, endy - starty));
        }
        else {
          g2.draw(new Rectangle(startx, endy, endx - startx, starty - endy));
        }
      }
      else {
        if (endy > starty) {
          g2.draw(new Rectangle(endx, starty, startx - endx, endy - starty));
        }
        else {
          g2.draw(new Rectangle(endx, endy, startx - endx, starty - endy));
        }
      }
    }
  }

  abstract protected void drawPoints(Graphics2D g, double xscale, double yscale);

  public void addSelectionChangedListeners(SelectionChangedListener rl) {
    selectionChangedListeners.add(rl);
  }
  public void removeSelectionChangedListener(SelectionChangedListener rl) {
    selectionChangedListeners.remove(rl);
  }

  protected void fireSelectionChanged() {
      int size = selectionChangedListeners.size();
      for(int i = 0; i < size; i++) {
        SelectionChangedListener rl = (SelectionChangedListener)selectionChangedListeners.get(i);
        rl.selectionChanged();
      }
  }
}