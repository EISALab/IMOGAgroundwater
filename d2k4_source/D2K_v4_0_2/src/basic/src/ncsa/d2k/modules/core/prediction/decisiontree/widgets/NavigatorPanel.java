package ncsa.d2k.modules.core.prediction.decisiontree.widgets;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.image.*;

import ncsa.util.*;
import ncsa.d2k.modules.core.prediction.decisiontree.*;
import ncsa.gui.*;

/*
 DecisionTreeVis

 Displays a scaled view of decision tree from tree scroll pane
 Draws a navigator that shows how much of tree is visible
 Dimensions of navigator based on scale of tree scroll pane
*/
public final class NavigatorPanel extends JPanel {

  Navigator navigator;

  public NavigatorPanel(ViewableDTModel model, TreeScrollPane scrollpane) {
    navigator = new Navigator(model, scrollpane);

    setBackground(DecisionTreeScheme.borderbackgroundcolor);
    add(navigator);
  }

  public void rebuildTree() {
    navigator.rebuildTree();
  }

  class Navigator extends JPanel implements MouseListener, MouseMotionListener, ChangeListener {

    // Decision tree model
    ViewableDTModel dmodel;

    // Decision tree root
    ViewableDTNode droot;

    // Scaled tree root
    ScaledNode sroot;

    // Width and height of decision tree
    double dwidth, dheight;

    // Scaled width and height of decision tree
    double swidth = 200;
    double sheight;

    // Maximum depth
    int mdepth;

    TreeScrollPane treescrollpane;
    JViewport viewport;

    // Width and height of navigator
    double nwidth, nheight;

    double xscale, yscale;

    // Offsets of navigator
    double x, y, lastx, lasty;

    boolean statechanged;

    BufferedImage image;

    boolean drawable = true;

    public Navigator(ViewableDTModel model, TreeScrollPane scrollpane) {
      dmodel = model;
      droot = dmodel.getViewableRoot();
      sroot = new ScaledNode(dmodel, droot, null);

      treescrollpane = scrollpane;
      viewport = treescrollpane.getViewport();

      findMaximumDepth(droot);
      buildTree(droot, sroot);

      sroot.x = sroot.findLeftSubtreeWidth();
      sroot.y = sroot.yspace;

      findTreeOffsets(sroot);

      dwidth = sroot.findSubtreeWidth();
      dheight = (sroot.yspace + sroot.height)*(mdepth + 1) + sroot.yspace;

      findSize();

      setOpaque(true);

      if (drawable) {
        addMouseListener(this);
        addMouseMotionListener(this);
        viewport.addChangeListener(this);
        image = new BufferedImage((int) swidth, (int) sheight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();
        paintBuffer(g2);
      }
    }

    // Rebuild tree because node spacing has changed
    public void rebuildTree() {
      sroot = new ScaledNode(dmodel, droot, null);

      findMaximumDepth(droot);
      buildTree(droot, sroot);

      sroot.x = sroot.findLeftSubtreeWidth();
      sroot.y = sroot.yspace;

      findTreeOffsets(sroot);

      dwidth = sroot.findSubtreeWidth();
      dheight = (sroot.yspace + sroot.height)*(mdepth + 1) + sroot.yspace;

      findSize();

      image = new BufferedImage((int) swidth, (int) sheight, BufferedImage.TYPE_INT_RGB);
      Graphics2D g2 = image.createGraphics();
      paintBuffer(g2);

      revalidate();
      repaint();
    }

    public void findMaximumDepth(ViewableDTNode dnode) {
      int depth = dnode.getDepth();

      if (depth > mdepth)
        mdepth = depth;

      for (int index = 0; index < dnode.getNumChildren(); index++) {
        ViewableDTNode dchild = dnode.getViewableChild(index);
        findMaximumDepth(dchild);
      }
    }

    public void findTreeOffsets(ScaledNode snode) {
      snode.findOffsets();

      for (int index = 0; index < snode.getNumChildren(); index++) {
        ScaledNode schild = (ScaledNode) snode.getChild(index);
        findTreeOffsets(schild);
      }
    }

    public void buildTree(ViewableDTNode dnode, ScaledNode snode) {
      for (int index = 0; index < dnode.getNumChildren(); index++) {
        ViewableDTNode dchild = dnode.getViewableChild(index);
        ScaledNode schild = new ScaledNode(dmodel, dchild, snode, dnode.getBranchLabel(index));
        snode.addChild(schild);
        buildTree(dchild, schild);
      }
    }

    public void paintComponent(Graphics g) {
      super.paintComponent(g);

      if (drawable) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.drawImage(image, 0, 0, null);

        g2.setColor(DecisionTreeScheme.viewercolor);
        g2.setStroke(new BasicStroke(1));
        g2.draw(new Rectangle2D.Double(x, y, nwidth-1, nheight-1));
      }
    }

    public void paintBuffer(Graphics2D g2) {
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2.setPaint(DecisionTreeScheme.borderbackgroundcolor);
      g2.fill(new Rectangle((int) dwidth, (int) dheight));

      AffineTransform transform = g2.getTransform();
      AffineTransform sinstance = AffineTransform.getScaleInstance(swidth/dwidth, swidth/dwidth);
      g2.transform(sinstance);

      drawTree(g2, sroot);

      g2.setTransform(transform);
    }

    public void drawTree(Graphics2D g2, ScaledNode snode) {
      snode.drawScaledNode(g2);

      for (int index = 0; index < snode.getNumChildren(); index++) {
        ScaledNode schild = (ScaledNode) snode.getChild(index);

        double x1 = snode.x;
        double y1 = snode.y + snode.height;
        double x2 = schild.x;
        double y2 = schild.y;

        drawLine(g2, x1, y1, x2, y2);

        drawTree(g2, schild);
      }
    }

    public void drawLine(Graphics2D g2, double x1, double y1, double x2, double y2) {
      int linestroke = 1;

      g2.setStroke(new BasicStroke(linestroke));
      g2.setColor(DecisionTreeScheme.scaledviewbackgroundcolor);
      g2.draw(new Line2D.Double(x1, y1, x2, y2));
    }

    // Determine size and position of navigator
    public void findSize() {
      double scale = treescrollpane.getScale();

      sheight = swidth*dheight/dwidth;
      if (sheight < 1)
        drawable = false;

      xscale = swidth/(scale*dwidth);
      yscale = sheight/(scale*dheight);

      Point position = viewport.getViewPosition();
      Dimension vpdimension = viewport.getExtentSize();

      double vpwidth = vpdimension.getWidth();
      nwidth = swidth*vpwidth/(scale*dwidth);
      if (nwidth > swidth)
        nwidth = swidth;

      x = xscale*position.x;

      double vpheight = vpdimension.getHeight();
      nheight = sheight*vpheight/(scale*dheight);
      if (nheight > sheight)
        nheight = sheight;

      y = yscale*position.y;
    }

    public Dimension getMinimumSize() {
      return getPreferredSize();
    }

    public Dimension getPreferredSize() {
      return new Dimension((int) swidth, (int) sheight);
    }

    public void mousePressed(MouseEvent event) {
      lastx = event.getX();
      lasty = event.getY();
    }

    public void mouseDragged(MouseEvent event) {
      int x1 = event.getX();
      int y1 = event.getY();

      double xchange = x1 - lastx;
      double ychange = y1 - lasty;

      x += xchange;
      y += ychange;

      if (x < 0)
        x = 0;
      if (y < 0)
        y = 0;
      if (x + nwidth > swidth)
        x = swidth - nwidth;
      if (y + nheight > sheight)
        y = sheight - nheight;

      statechanged = false;

      double scale = treescrollpane.getScale();
      xscale = swidth/(scale*dwidth);
      yscale = sheight/(scale*dheight);
      treescrollpane.scroll((int) (x/xscale), (int) (y/yscale));

      lastx = x1;
      lasty = y1;

      repaint();
    }

  /*
   Scrolling causes a change event, but scrolling caused by
   moving the navigator should not cause a change event.
  */
    public void stateChanged(ChangeEvent event) {
      if (statechanged) {
        findSize();
        repaint();
      }

      statechanged = true;
    }

    public void mouseExited(MouseEvent event) {	}
    public void mouseEntered(MouseEvent event) { }
    public void mouseReleased(MouseEvent event) { }
    public void mouseClicked(MouseEvent event) { }
    public void mouseMoved(MouseEvent event) { }
  }
}