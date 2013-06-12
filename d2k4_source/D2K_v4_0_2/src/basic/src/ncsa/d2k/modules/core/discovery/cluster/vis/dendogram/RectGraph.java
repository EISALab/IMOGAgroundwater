package ncsa.d2k.modules.core.discovery.cluster.vis.dendogram;


//==============
// Java Imports
//==============

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.event.*;

//===============
// Other Imports
//===============

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.datatype.table.sparse.*;

import ncsa.d2k.modules.core.discovery.cluster.util.*;

import ncsa.d2k.userviews.swing.*;
import ncsa.d2k.gui.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.vis.widgets.*;
import ncsa.gui.*;


public class RectGraph  extends BaseGraph implements MouseInputListener  {

  //==============
  // Data Members
  //==============

  private int m_displayMode = DisplayCohesion;

  private int m_incell = -1;
  private int m_infont = -1;
  private ArrayList m_lastis = new ArrayList();

  static public final int DisplayCohesion = 0;
  static public final int DisplayClusterSize = 1;
  static public final int DisplaySizeAsColor = 2;

  private DendogramPanel m_parent = null;

  private Color _highColor = Color.green;
  private Color _lowColor = Color.red;

  //================
  // Constructor(s)
  //================

  public RectGraph(ArrayList shapes, ArrayList leaves, GraphSettings settings, DendogramPanel parent) {
    super(shapes, leaves, settings);
    this.addMouseListener(this);
    this.addMouseMotionListener(this);
    m_parent = parent;
  }

  //================
  // Public Methods
  //================

  public void setHighColor(Color c){
    _highColor = c;
    repaint();
  }

  public void setLowColor(Color c){
    _lowColor = c;
    repaint();
  }

  public Color getHighColor(){
    return _highColor;
  }

  public Color getLowColor(){
    return _lowColor;
  }

  public void setDisplayMode(int mode){
    m_displayMode = mode;
    repaint();
  }

  public int getDisplayMode(){
    return m_displayMode;
  }

  public void drawDataSet(Graphics2D g2, RectWrapper rect) {
    drawRect(g2, rect.getColor(), rect.getRect());
  }

  // Draw two data points and line
  public void drawRect(Graphics2D g2, Color color, Rectangle rect) {


    Color previouscolor = g2.getColor();

    double x0scale = (rect.getX() - xminimum) / xscale + leftoffset;
    double y0scale = graphheight - bottomoffset - (rect.getY() - yminimum) / yscale;

    double newWidth = 0;
    if (getDisplayMode() == RectGraph.DisplayCohesion){
      newWidth = rect.getWidth() / xscale;
    } else if (getDisplayMode() == RectGraph.DisplaySizeAsColor){
      newWidth = rect.getWidth() / xscale;
    } else if (getDisplayMode() == RectGraph.DisplayClusterSize){
      TableCluster c = ((RectWrapper)m_shapes.get(m_dispRects.size())).getCluster();
      TableCluster c2 = ((RectWrapper)m_shapes.get(0)).getCluster();
      newWidth = (xmaximum-xminimum)*((double)c.getSize()/(double)c2.getSize())/xscale;
      //newWidth = (c.getSize() / xscale);
    }
    //System.out.println("Rect width: " + rect.getWidth());

    double newHeight = rect.getHeight() / yscale;

    //System.out.print(" " + newx[i] + "," + newy[i]);

    //System.out.println();
    Rectangle r  = new Rectangle((int)x0scale, (int)y0scale, (int)newWidth, (int)newHeight);
    m_dispRects.add(r);
    //System.out.println("Drawing a Polygon: " + p + " " + newx + " " + newy);


    int redH = (_highColor.getRed() >= _lowColor.getRed())?_highColor.getRed():_lowColor.getRed();
    int redL = (_highColor.getRed() >= _lowColor.getRed())?_lowColor.getRed():_highColor.getRed();

    int greenH = (_highColor.getGreen() >= _lowColor.getGreen())?_highColor.getGreen():_lowColor.getGreen();
    int greenL = (_highColor.getGreen() >= _lowColor.getGreen())?_lowColor.getGreen():_highColor.getGreen();

    int blueH = (_highColor.getBlue() >= _lowColor.getBlue())?_highColor.getBlue():_lowColor.getBlue();
    int blueL = (_highColor.getBlue() >= _lowColor.getBlue())?_lowColor.getBlue():_highColor.getBlue();

    int redR = redH - redL;
    int greenR = greenH - greenL;
    int blueR = blueH - blueL;

    if (getDisplayMode() == RectGraph.DisplayCohesion){
      if (color == Color.orange){
        g2.setColor(color);
      } else {
        long red = Math.round(redR*((double)rect.getWidth()/(double)xmaximum));
        if (_highColor.getRed() >= _lowColor.getRed()){
          red += _lowColor.getRed();
        } else {
          red = _lowColor.getRed() - red;
        }
        if (red < redL){
          red = redL;
        } else if (red > redH){
          red = redH;
        }
        long green = Math.round(greenR*((double)rect.getWidth()/(double)xmaximum));
        if (_highColor.getGreen() >= _lowColor.getGreen()){
          green += _lowColor.getGreen();
        } else {
          green = _lowColor.getGreen() - green;
        }
        if (green < greenL){
          green = greenL;
        } else if (green > greenH){
          green = greenH;
        }
        long blue = Math.round(blueR*((double)rect.getWidth()/(double)xmaximum));
        if (_highColor.getBlue() >= _lowColor.getBlue()){
          blue += _lowColor.getBlue();
        } else {
          blue = _lowColor.getBlue() - blue;
        }
        if (blue < blueL){
          blue = blueL;
        } else if (blue > blueH){
          blue = blueH;
        }
//        System.out.println("Red: " + red);
//        System.out.println("Green: " + green);
//        System.out.println("Blue: " + blue);
        //g2.setColor(new Color(255-(int)red, (int)red, 0));
        g2.setColor(new Color((int)red, (int)green, (int)blue));
      }
    } else if (getDisplayMode() == RectGraph.DisplaySizeAsColor){
      TableCluster c1 = ((RectWrapper)m_shapes.get(m_dispRects.size()-1)).getCluster();
      TableCluster c2 = ((RectWrapper)m_shapes.get(0)).getCluster();
      long red = Math.round(redR*((double)c1.getSize()/(double)c2.getSize()));
      if (_highColor.getRed() >= _lowColor.getRed()){
        red += _lowColor.getRed();
      } else {
        red = _lowColor.getRed() - red;
      }
      if (red < redL){
        red = redL;
      } else if (red > redH){
        red = redH;
      }
      long green = Math.round(greenR*((double)c1.getSize()/(double)c2.getSize()));
      if (_highColor.getGreen() >= _lowColor.getGreen()){
        green += _lowColor.getGreen();
      } else {
        green = _lowColor.getGreen() - green;
      }
      if (green < greenL){
        green = greenL;
      } else if (green > greenH){
        green = greenH;
      }
      long blue = Math.round(blueR*((double)c1.getSize()/(double)c2.getSize()));
      if (_highColor.getBlue() >= _lowColor.getBlue()){
        blue += _lowColor.getBlue();
      } else {
        blue = _lowColor.getBlue() - blue;
      }
      if (blue < blueL){
        blue = blueL;
      } else if (blue > blueH){
        blue = blueH;
      }
//        System.out.println("Red: " + red);
//        System.out.println("Green: " + green);
//        System.out.println("Blue: " + blue);
      //g2.setColor(new Color(255-(int)red, (int)red, 0));
      g2.setColor(new Color((int)red, (int)green, (int)blue));
    } else if (getDisplayMode() == RectGraph.DisplayClusterSize){
      long red = Math.round(redR*((double)rect.getWidth()/(double)xmaximum));
      if (_highColor.getRed() >= _lowColor.getRed()){
        red += _lowColor.getRed();
      } else {
        red = _lowColor.getRed() - red;
      }
      if (red < redL){
        red = redL;
      } else if (red > redH){
        red = redH;
      }
      long green = Math.round(greenR*((double)rect.getWidth()/(double)xmaximum));
      if (_highColor.getGreen() >= _lowColor.getGreen()){
        green += _lowColor.getGreen();
      } else {
        green = _lowColor.getGreen() - green;
      }
      if (green < greenL){
        green = greenL;
      } else if (green > greenH){
        green = greenH;
      }
      long blue = Math.round(blueR*((double)rect.getWidth()/(double)xmaximum));
      if (_highColor.getBlue() >= _lowColor.getBlue()){
        blue += _lowColor.getBlue();
      } else {
        blue = _lowColor.getBlue() - blue;
      }
      if (blue < blueL){
        blue = blueL;
      } else if (blue > blueH){
        blue = blueH;
      }
//        System.out.println("Red: " + red);
//        System.out.println("Green: " + green);
//        System.out.println("Blue: " + blue);
      //g2.setColor(new Color(255-(int)red, (int)red, 0));
      g2.setColor(new Color((int)red, (int)green, (int)blue));
    }


    g2.fill(r);

    g2.setColor(Color.black);

    g2.drawLine((int)x0scale, (int)y0scale, (int)(x0scale + newWidth), (int)y0scale);
    g2.drawLine((int)x0scale, (int)y0scale+1, (int)(x0scale + newWidth), (int)y0scale+1);
    g2.drawLine((int)x0scale, (int)y0scale-1, (int)(x0scale + newWidth), (int)y0scale-1);

    g2.drawLine((int)(x0scale + newWidth), (int)y0scale, (int)(x0scale + newWidth), (int)(y0scale + newHeight));
    g2.drawLine((int)(x0scale + newWidth + 1), (int)y0scale, (int)(x0scale + newWidth + 1), (int)(y0scale + newHeight));
    g2.drawLine((int)(x0scale + newWidth - 1), (int)y0scale, (int)(x0scale + newWidth - 1), (int)(y0scale + newHeight));

    g2.drawLine((int)x0scale, (int)(y0scale + newHeight), (int)(x0scale + newWidth), (int)(y0scale + newHeight));
    g2.drawLine((int)x0scale, (int)(y0scale + newHeight + 1), (int)(x0scale + newWidth), (int)(y0scale + newHeight + 1));
    g2.drawLine((int)x0scale, (int)(y0scale + newHeight - 1), (int)(x0scale + newWidth), (int)(y0scale + newHeight - 1));

    g2.setColor(previouscolor);
  }

  public int inCell(int x, int y){
    int retval = -1;
    for (int i = 0, n = m_dispRects.size(); i < n; i++){
      if (((Rectangle)m_dispRects.get(i)).contains(x,y)){
        retval = i;
      }
    }
    return retval;
  }

  public int inFont(int x, int y){
    int retval = -1;
    for (int i = 0, n = m_ends.size(); i < n; i++){
      if (((Rectangle)((Object[])m_ends.get(i))[0]).contains(x,y)){
        retval = i;
      }
    }
    return retval;
  }

  //=========================================
  // Interface Implementation: MouseListener
  //=========================================

  public void mouseClicked(MouseEvent evt){

    if ((evt.getClickCount() >= 2) && (evt.isShiftDown())){
      if (this.isEnabled()){
        int i = inCell(evt.getX(), evt.getY());
        //System.out.println("MOUSE: img to chg: " + evt.getX() + " "  + " "  + evt.getX() + " " + i);
        if (i >= 0){
          TableCluster c = ((RectWrapper)m_shapes.get(i)).getCluster();
          Table tab = null;
          if (tab instanceof SparseTable){
            tab = ((SparseMutableTable)c.getTable()).getSubsetByReference(c.getMemberIndices());
          } else {
            tab = c.getTable().getSubset(c.getMemberIndices());
          }

          JD2KFrame frame = new JD2KFrame("Values for Cluster ID " + c.getClusterLabel());
          TableMatrix vtm = new TableMatrix(tab);
          frame.getContentPane().add(vtm);
          frame.addWindowListener(new DisposeOnCloseListener(frame));
          frame.pack();
          frame.show();
        }
        int j = inFont(evt.getX(), evt.getY());
        //System.out.println("MOUSE: img to chg: " + evt.getX() + " "  + " "  + evt.getX() + " " + i);
        if (j >= 0){
          Object[] obs = (Object[])m_ends.get(j);
          TableCluster c = ((RectWrapper)obs[1]).getCluster();
          JD2KFrame frame = new JD2KFrame("Values for Cluster ID " + c.getClusterLabel());
          Table tab = null;
          if (tab instanceof SparseTable){
            tab = ((SparseMutableTable)c.getTable()).getSubsetByReference(c.getMemberIndices());
          } else {
            tab = c.getTable().getSubset(c.getMemberIndices());
          }
          TableMatrix vtm = new TableMatrix(tab);
          frame.getContentPane().add(vtm);
          frame.addWindowListener(new DisposeOnCloseListener(frame));
          frame.pack();
          frame.show();
        }
      }
    } else if ((evt.getClickCount() >= 2) && (!evt.isShiftDown()) && (!evt.isControlDown())){
      if (this.isEnabled()) {

        int j = inFont(evt.getX(), evt.getY());
        if (j >= 0) {
          return;
        }

        int i = inCell(evt.getX(), evt.getY());

        if (i >= 0) {
          TableCluster tc = ( (RectWrapper) m_shapes.get(i)).getCluster();
          m_parent.resetPanel(tc);
        }
      }
    } else if ((evt.getClickCount() >= 2) && (evt.isControlDown())){
      if (this.isEnabled()) {
        TableCluster tc = null;
        int j = inFont(evt.getX(), evt.getY());
        int i = inCell(evt.getX(), evt.getY());

        if (j >= 0) {
          Object[] obs = (Object[])m_ends.get(j);
          tc = ((RectWrapper)obs[1]).getCluster();
        }

        if (i >=0 ){
          tc = ( (RectWrapper) m_shapes.get(i)).getCluster();
        }

        if (tc != null) {
          Table tab = tc.getTable();

          int[] ifeatures = null;

          if (tab instanceof ExampleTable) {
            ifeatures = ( (ExampleTable) tab).getInputFeatures();
          } else {
            ifeatures = new int[tab.getNumColumns()];
            for (int a = 0, b = tab.getNumColumns(); a < b; a++) {
              ifeatures[i] = i;
            }
          }



          double[] vals = tc.getCentroid();
          int[] ind = null;

          if (tc.getSparse()){
            vals = tc.getSparseCentroidValues();
            ind = tc.getSparseCentroidInd();
          }

          Column[] cols = new DoubleColumn[vals.length];

          for (int a = 0, b = vals.length; a < b; a++){
            double[] dval = new double[1];
            dval[0] = vals[a];
            cols[a] = new DoubleColumn(dval);
            if (tc.getSparse()){
//              if (tc.getTable() instanceof ncsa.d2k.modules.t2k.datatype.DocumentTermTable){
//                cols[a].setLabel(((ncsa.d2k.modules.t2k.datatype.DocumentTermTable)tc.getTable()).getTermData(ind[a]).getImage());
//              } else {
                cols[a].setLabel(tab.getColumnLabel(ind[a]));
//              }
            } else {
              cols[a].setLabel(tab.getColumnLabel(ifeatures[a]));
            }
          }

          Table newtab = new MutableTableImpl(cols);

          JD2KFrame frame = new JD2KFrame("Centroid values for Cluster ID " + tc.getClusterLabel());
          TableMatrix vtm = new TableMatrix(newtab);
          frame.getContentPane().add(vtm);
          frame.addWindowListener(new DisposeOnCloseListener(frame));
          frame.pack();
          frame.show();
        }
      }
    }
  }

  public void mouseEntered(MouseEvent evt){
    //System.out.println("MOUSE ENTERED");
  }

  public void mouseExited(MouseEvent evt){
    //System.out.println("MOUSE EXITED");
  }

  public void mousePressed(MouseEvent evt){
    //System.out.println("MOUSE PRESSED");
  }

  public void mouseReleased(MouseEvent evt){
    //System.out.println("MOUSE RELEASED");
  }

  public void mouseMoved(MouseEvent evt){
    //System.out.println("MOUSE MOVED");
  }

  public void mouseDragged(MouseEvent evt){
    //System.out.println("MOUSE DRAGGED");
  }

}
