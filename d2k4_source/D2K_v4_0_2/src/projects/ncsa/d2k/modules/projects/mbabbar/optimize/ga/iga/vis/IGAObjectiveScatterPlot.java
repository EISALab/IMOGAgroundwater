package ncsa.d2k.modules.projects.mbabbar.optimize.ga.iga.vis;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.text.*;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.projects.mbabbar.optimize.ga.iga.*;

public class IGAObjectiveScatterPlot extends JPanel implements MouseListener, MouseMotionListener {

  // the number of objectives
  protected int numObjectives;

  protected Table table;

  public boolean[] selected;

  // the index of the objectives
  protected int xObjective, yObjective;

  // the min and maxes of the data
  protected float xMin, xMax, yMin, yMax;
  // the min and maxes for the scales
  protected float xScaleMin, xScaleMax, yScaleMin, yScaleMax;

  protected Image image;

  protected boolean changed = false;

  protected float top, left, right, bottom;

  protected float graphwidth, graphheight;

  protected int startx, starty, endx, endy;
  protected boolean mousePressed;

  protected IGANsgaPopulation population;

  protected NumberFormat nf;

  public IGAObjectiveScatterPlot() {
    //population = null;
    setDoubleBuffered(false);
    table = null;
    population = null;
    top = left = right = bottom = 20.0f;
    ToolTipManager.sharedInstance().registerComponent(this);
    addMouseListener(this);
    addMouseMotionListener(this);
    startx = starty = endx = endy = -1;
    nf = NumberFormat.getInstance();
    nf.setMaximumFractionDigits(1);
  }

  public void setBounds(int x, int y, int w, int h) {
    super.setBounds(x, y, w, h);
    if(image != null)
      image.flush();


    if(w != 0 && h != 0) {
      if(image != null)
        image.flush();
      image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
      graphwidth = (float) w;
      graphheight = (float) h;
      startx = starty = endx = endy = -1;
    }

    changed = true;
  }

  public void setTable(Table t) {
    table = t;
    population = null;
    numObjectives = t.getNumColumns();
    changed = true;
    selected = new boolean[t.getNumRows()];
    for (int i=0; i< t.getNumRows(); i++){
      selected[i] = false;
    }
  }

  public void setPopulation(IGANsgaPopulation p) {
    population = p;
    table = null;
    numObjectives = p.getNumObjectives();
    changed = true;
    selected = new boolean[p.getMembers().length];
    for (int i=0; i< p.getMembers().length; i++){
      selected[i] = false;
    }
  }

  public void mousePressed(MouseEvent e) {

    startx = e.getX();
    starty = e.getY();
   // System.out.println("objective scatter plot mouse pressed");
   // System.out.println("in scatter plot: " + startx+ "  " + starty);
    mousePressed = true;
  }
  public void mouseClicked(MouseEvent e) {}
  public void mouseEntered(MouseEvent e) {}
  public void mouseExited(MouseEvent e) {}
  public void mouseReleased(MouseEvent e) {
    //System.out.println("objective scatter plot mouse released");
    //System.out.println("in scatter plot: " + e.getX()+ "  " + e.getY());
    mousePressed = false;
  /*  if(table != null) {
      selected = new boolean[table.getNumRows()];
    }
    else {
      selected = new boolean[population.getMembers().length];
    }
  */
    // now, given the startx, starty, endx, endy
    // determine which points lie inside the boundaries of the box
    float smallestX=0, smallestY=0, biggestX=0, biggestY=0;

    float[] xys = new float[2];
    if(endx > startx) {
      if(endy > starty) {
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
      if(endy > starty) {
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

   // if(population == null) {
   if(table != null) {
      int numRows = table.getNumRows();
      // for each row of the table
      for (int i = 0; i < numRows; i++) {
        float xval = table.getFloat(i, this.xObjective);
        float yval = table.getFloat(i, this.yObjective);

        if (xval >= smallestX && xval <= biggestX)
          if (yval >= smallestY && yval <= biggestY) {
            // selected[i] = true;
            // added by Meghna, to allow for unselection
            if (selected[i] != true)
              selected[i] = true;
            else
              selected[i] = false;
            /////////////////////////////////////
          }
        changed = true;
      }
    }
    else {
      IGANsgaSolution[] solutions = (IGANsgaSolution[])population.getMembers();
      int numMembers = solutions.length;
      for (int i = 0; i < numMembers; i++) {
        float xval = (float)solutions[i].getObjective(xObjective);
        float yval = (float)solutions[i].getObjective(yObjective);

        if (xval >= smallestX && xval <= biggestX)
          if (yval >= smallestY && yval <= biggestY) {
            // selected[i] = true;
            // added by Meghna, to allow for unselection
            if (selected[i] != true)
              selected[i] = true;
            else
              selected[i] = false;
            /////////////////////////////////////
          }
        changed = true;
      }
    }
    // revalidate added by meghna
    revalidate();
    repaint();
  }

  private static float TINY = 0.00001f;

  public void mouseMoved(MouseEvent e) {}
  public void mouseDragged(MouseEvent e) {
    //System.out.println(" objective scatter plot mouse dragged");
    if(mousePressed) {
      endx = e.getX();
      endy = e.getY();
      //System.out.println("in scatter plot: " + endx+ "  " + endy);
     // System.out.println("in scatter plot: " + e.getSource());
     // revalidate added by meghna
      revalidate();
      repaint();
    }
  }

  /**
   * Set the indices of the objectives to draw
   * @param x the objective to plot on the x-axis
   * @param y the objective to plot on the y-axis
   */
  public void setObjectives(int x, int y) {
    //if(population == null)
    if(table == null && population == null)
      return;

    if(x > numObjectives || x < 0)
      ;
    xObjective = x;

    if(y > numObjectives || y < 0)
      ;
    yObjective = y;

    // reset min/max for both objectives by looping through pop
    xMin = yMin = Float.POSITIVE_INFINITY;
    xMax = yMax = Float.NEGATIVE_INFINITY;

    if(table != null) {
      int numMembers = table.getNumRows();
      for (int i = 0; i < numMembers; i++) {
        float xVal = table.getFloat(i, xObjective);
        if (xVal > xMax)
          xMax = xVal;
        if (xVal < xMin)
          xMin = xVal;
        float yVal = table.getFloat(i, yObjective);
        if (yVal > yMax)
          yMax = yVal;
        if (yVal < yMin)
          yMin = yVal;
      }
    }
    else {
      IGANsgaSolution[] members = (IGANsgaSolution[])population.getMembers();
      int numMembers = members.length;
      for (int i = 0; i < members.length; i++) {
        if(members[i].getRank() == 0) {
          float xVal = (float)members[i].getObjective(xObjective);
          if (xVal > xMax)
            xMax = xVal;
          if (xVal < xMin)
            xMin = xVal;
          float yVal = (float)members[i].getObjective(yObjective);
          if (yVal > yMax)
            yMax = yVal;
          if (yVal < yMin)
            yMin = yVal;
        }
      }
    }
    changed = true;
  }

  public String getToolTipText(MouseEvent e) {
    int xloc = e.getX();
    int yloc = e.getY();
    //new Exception().printStackTrace();
   // System.out.println("ToolTip location : "+ xloc +" "+ yloc);
    // now translate this from screen coordinates to graph coordinates
    if(xloc < left || xloc > (graphwidth-right))
      return "";
    else if(yloc > (graphheight-bottom) || yloc < top)
      return "";
    else {
      //float xscale = (xMax-xMin)/(graphwidth-left-right);
      //float yscale = (yMax-yMin)/(graphheight-top-bottom);

      float[] xys = new float[2];
      getXYVals(xloc, yloc, xys);
      float xval = xys[0];//xloc*xscale - xscale*left + xMin;
      float yval = xys[1];//yMin-yscale*(yloc-graphheight+bottom);

      return new String("<html>x: " + xval + "<br>y:" + yval+"</html>");
    }
  }

  protected void getXYVals(int xloc, int yloc, float[] retVal) {
      float xscale = (xMax-xMin)/(graphwidth-left-right);
      float yscale = (yMax-yMin)/(graphheight-top-bottom);

      float xval = xloc*xscale - xscale*left + xMin;
      float yval = yMin-yscale*(yloc-graphheight+bottom);
      retVal[0] = xval;
      retVal[1] = yval;
  }

  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D)g;
    if(changed) {
      image.flush();
      Graphics2D imgG2 = (Graphics2D)image.getGraphics();

      float xscale = (xMax-xMin)/(graphwidth-left-right);
      float yscale = (yMax-yMin)/(graphheight-top-bottom);

      imgG2.setColor(Color.white);
      imgG2.fill(new Rectangle2D.Double(0, 0, graphwidth, graphheight));

      imgG2.setColor(Color.black);
      // draw axes
      imgG2.draw(new Line2D.Double(left, top, left, graphheight-bottom));
      imgG2.draw(new Line2D.Double(left, graphheight-bottom,
          graphwidth-right, graphheight-bottom));

      // draw the scale
      g2.setFont(new Font("Arial", Font.PLAIN, 5));
      FontMetrics fm = g2.getFontMetrics();

      String x_min = nf.format(xMin);
      imgG2.drawString(x_min, left-fm.stringWidth(x_min)/2, graphheight-bottom+fm.getHeight()+6);
      String x_max = nf.format(xMax);
      imgG2.drawString(x_max, graphwidth-right-fm.stringWidth(x_max)/2,
                       graphheight-bottom+fm.getHeight()+6);

      AffineTransform saveXForm = imgG2.getTransform();
      String y_min = nf.format(yMin);
      double x1 = left-fm.stringWidth(y_min)/2-2;
      double y1 = graphheight-bottom+fm.getHeight()+6;
      imgG2.rotate(Math.toRadians(270), x1, y1);
      imgG2.drawString(y_min, (int)x1, (int)y1);
      imgG2.setTransform(saveXForm);

      String y_max = nf.format(yMax);
      x1 = left-fm.stringWidth(y_min)/2-2;
      y1 = top + 2*fm.stringWidth(y_max);
      imgG2.rotate(Math.toRadians(270), x1, y1);
      imgG2.drawString(y_max, (int)x1, (int)y1);
      imgG2.setTransform(saveXForm);

      // draw population
      if(table != null) {
        synchronized(table) {
          imgG2.setColor(Color.red);
          int numMembers = table.getNumRows();
          for (int index = 0; index < numMembers; index++) {
            float xvalue = table.getFloat(index, xObjective);
            float yvalue = table.getFloat(index, yObjective);

            float x = (xvalue - xMin) / xscale + left;
            float y = graphheight - bottom - (yvalue - yMin) / yscale;

            imgG2.fill(new Rectangle2D.Float(x, y, 2, 2));
          }

          if (selected != null) {
            imgG2.setColor(Color.blue);
            for (int index = 0; index < numMembers; index++) {
              if (selected[index]) {

                float xvalue = table.getFloat(index, xObjective);
                float yvalue = table.getFloat(index, yObjective);

                float x = (xvalue - xMin) / xscale + left;
                float y = graphheight - bottom - (yvalue - yMin) / yscale;

                imgG2.fill(new Rectangle2D.Float(x, y, 2, 2));
              }
            }
          }
          changed = false;
        }
      }
      else if(population != null) {
        synchronized (population) {
          imgG2.setColor(Color.red);
          IGANsgaSolution[] members = (IGANsgaSolution[]) population.getMembers();
          int numMembers = members.length;
          for (int i = 0; i < numMembers; i++) {
            if (members[i].getRank() == 0) {
              float xvalue = (float) members[i].getObjective(xObjective);
              float yvalue = (float) members[i].getObjective(yObjective);
              float x = (xvalue - xMin) / xscale + left;
              float y = graphheight - bottom - (yvalue - yMin) / yscale;

              imgG2.fill(new Rectangle2D.Float(x, y, 2, 2));
            }
          }
          if (selected != null) {
            imgG2.setColor(Color.blue);
            for (int i = 0; i < numMembers; i++) {
              if (members[i].getRank() == 0 && selected[i]) {
                float xvalue = table.getFloat(i, xObjective);
                float yvalue = table.getFloat(i, yObjective);

                float x = (xvalue - xMin) / xscale + left;
                float y = graphheight - bottom - (yvalue - yMin) / yscale;

                imgG2.fill(new Rectangle2D.Float(x, y, 2, 2));
              }

            }

          }
          changed = false;
        }
      }
    }
    g2.drawImage(image, 0, 0, null);
    g2.setColor(Color.black);
    //if(endx != -1 && endy != -1 && startx != -1 && starty != -1) {
    if(mousePressed) {
      if(endx > startx) {
        if(endy > starty)
          g2.draw(new Rectangle(startx, starty, endx - startx, endy - starty));
        else
          g2.draw(new Rectangle(startx, endy, endx - startx, starty-endy));
      }
      else {
        if(endy > starty)
          g2.draw(new Rectangle(endx, starty, startx - endx, endy - starty));
        else
          g2.draw(new Rectangle(endx, endy, startx - endx, starty-endy));
      }
    }
  }
}
