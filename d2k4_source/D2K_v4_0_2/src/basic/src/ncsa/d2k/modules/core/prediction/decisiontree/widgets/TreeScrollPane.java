package ncsa.d2k.modules.core.prediction.decisiontree.widgets;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.print.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

import ncsa.util.*;
import ncsa.d2k.gui.*;
import ncsa.d2k.preferences.*;
import ncsa.d2k.modules.core.prediction.decisiontree.*;
import ncsa.d2k.modules.core.prediction.decisiontree.widgets.*;

public final class TreeScrollPane extends JScrollPane {

  JViewport viewport;
  TreePanel treepanel;

  public TreeScrollPane(ViewableDTModel model, BrushPanel panel) {
    treepanel = new TreePanel(model, panel);

    viewport = getViewport();
    viewport.setBackground(DecisionTreeScheme.treebackgroundcolor);

    setBackground(DecisionTreeScheme.treebackgroundcolor);

    JPanel lowerright = new JPanel();
    setCorner(LOWER_RIGHT_CORNER, lowerright);
    viewport.setView(treepanel);
  }

  // Called by navigator and search panel
  public void scroll(int x, int y) {
    viewport.setViewPosition(new Point(x, y));
    revalidate();
    repaint();
  }

  public void reset() {
    treepanel.scale = 1;
    scroll(0, 0);
  }

  public void clearSearch() {
  }

  public void toggleLabels() {
    if (treepanel.labels == true)
      treepanel.labels = false;
    else
      treepanel.labels = true;
    treepanel.repaint();
  }

  public void toggleZoom() {
    if (treepanel.zoom)
      treepanel.zoom = false;
    else
      treepanel.zoom = true;
  }

  public Printable getPrintable() {
    return treepanel;
  }

  public double getScale() {
    return treepanel.scale;
  }

  /*public void clearSearch() {
    treepanel.clearSearch();
  }*/

  public ViewableDTModel getViewableModel() {
    return treepanel.model;
  }

  public void rebuildTree() {
    treepanel.rebuildTree();
    revalidate();
    repaint();
  }

  public Viewport getViewRoot() {
    return treepanel.viewportroot;
  }

  public int getDepth() {
    return treepanel.getDepth();
  }

  public void setDepth(int value) {
    treepanel.setDepth(value);
    revalidate();
    repaint();
  }

  public class TreePanel extends JPanel implements MouseListener, MouseMotionListener, Printable {

    // Brush panel
    BrushPanel brushpanel;

    // Decision tree model
    ViewableDTModel model;

    // Decision tree root
    ViewableDTNode root;

    // View tree root
    Viewport viewportroot;

    // List of offsets for each depth
    LinkedList[] offsets;

    // Scale
    double scale = 1;
    double scaleincrement = .1;
    double upperscale = 2;
    double lowerscale = .1;

    // Width and height of decision tree
    double dwidth, dheight;

    // Maximum depth
    int mdepth;

    // Maximum depth to draw
    int depth;

    // Draw labels
    boolean labels = true;

    boolean zoom = false;

    int lastx, lasty;

    public TreePanel(ViewableDTModel model, BrushPanel panel) {
      this.model = model;

      brushpanel = panel;

      root = model.getViewableRoot();
      viewportroot = new Viewport(model, root);

      findMaximumDepth(root);
      depth = mdepth;
      buildViewTree(root, viewportroot);

      offsets = new LinkedList[mdepth + 1];
      for (int index = 0; index <= mdepth; index++)
        offsets[index] = new LinkedList();

      viewportroot.x = viewportroot.findLeftSubtreeWidth();
      viewportroot.y = viewportroot.yspace;
      offsets[0].add(viewportroot);

      findViewTreeOffsets(viewportroot);

      dwidth = viewportroot.findSubtreeWidth();
      dheight = (viewportroot.yspace + viewportroot.height) * (mdepth + 1) + viewportroot.yspace;

      addMouseListener(this);
      addMouseMotionListener(this);
    }

    public void rebuildTree() {
      Viewport viewportroot = new Viewport(model, root);

      findMaximumDepth(root);
      buildViewTree(root, viewportroot);

      offsets = new LinkedList[mdepth + 1];
      for (int index = 0; index <= mdepth; index++)
        offsets[index] = new LinkedList();

      viewportroot.x = viewportroot.findLeftSubtreeWidth();
      viewportroot.y = viewportroot.yspace;
      offsets[0].add(viewportroot);

      findViewTreeOffsets(viewportroot);

      dwidth = viewportroot.findSubtreeWidth();
      dheight = (viewportroot.yspace + viewportroot.height) * (mdepth + 1) + viewportroot.yspace;

      //copySearch(viewportroot, viewportroot);

      this.viewportroot = viewportroot;

      revalidate();
      repaint();
    }

    /*void copySearch(Viewport onode, Viewport nnode) {
      nnode.search = onode.search;

      for (int index = 0; index < onode.getNumChildren(); index++) {
        Viewport ochild = onode.getChild(index);
        Viewport nchild = nnode.getChild(index);

        copySearch(ochild, nchild);
      }
    }*/

    void findMaximumDepth(ViewableDTNode node) {
      int depth = node.getDepth();

      if (depth > mdepth)
        mdepth = depth;

      for (int index = 0; index < node.getNumChildren(); index++) {
        ViewableDTNode dchild = node.getViewableChild(index);
        findMaximumDepth(dchild);
      }
    }

    public int getDepth() {
      return depth;
    }

    public void setDepth(int value) {
      depth = value;
    }

    // Finds the offsets for each node
    void findViewTreeOffsets(Viewport emptyview) {
      emptyview.findOffsets();
      offsets[emptyview.getDepth()].add(emptyview);

      for (int index = 0; index < emptyview.getNumChildren(); index++) {
        Viewport vchild = emptyview.getChild(index);
        findViewTreeOffsets(vchild);
      }
    }

    // Builds the decision tree using view nodes
    void buildViewTree(ViewableDTNode dnode, Viewport emptyview) {
      for (int index = 0; index < dnode.getNumChildren(); index++) {
        ViewableDTNode dchild = dnode.getViewableChild(index);
        Viewport vchild = new Viewport(model, dchild, emptyview, emptyview.getBranchLabel(index));
        emptyview.addChild(vchild);
        buildViewTree(dchild, vchild);
      }
    }

    public void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2 = (Graphics2D) g;
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

      Rectangle rectangle = new Rectangle( (int) getSWidth(), (int) getSHeight());
      g2.setColor(DecisionTreeScheme.treebackgroundcolor);
      g2.fill(rectangle);

      AffineTransform transform = g2.getTransform();
      AffineTransform sinstance = AffineTransform.getScaleInstance(scale, scale);
      g2.transform(sinstance);

      drawViewTree(g2, viewportroot);

      g2.setTransform(transform);
    }

    // Draws the view tree
    void drawViewTree(Graphics2D g2, Viewport emptyview) {
      Shape shape = g2.getClip();

      if (shape.intersects((emptyview.x-emptyview.width/2), emptyview.y, emptyview.width, emptyview.height))
        emptyview.draw(g2);

      if (emptyview.collapsed)
        return;

      for (int index = 0; index < emptyview.getNumChildren(); index++) {
        Viewport vchild = emptyview.getChild(index);

        if (vchild.getDepth() > depth)
          return;

        double x1 = emptyview.x;
        double y1 = emptyview.y + emptyview.height;
        double x2 = vchild.x;
        double y2 = vchild.y;

        drawLine(g2, emptyview.getBranchLabel(index), x1, y1, x2, y2);

        drawViewTree(g2, vchild);
      }
    }

    // Draws a line between nodes
    void drawLine(Graphics2D g2, String label, double x1, double y1, double x2, double y2) {
      int linestroke = 1;

      double diameter = 8;
      double radius = diameter / 2;
      double xcircle, ycircle;
      int circlestroke = 2;

      FontMetrics metrics = getFontMetrics(DecisionTreeScheme.textfont);
      int fontascent = metrics.getAscent();

      double xlabel, ylabel;
      double labelspace = 20;
      int labelwidth = metrics.stringWidth(label);

      // Line
      g2.setStroke(new BasicStroke(linestroke));
      g2.setColor(DecisionTreeScheme.treelinecolor);
      g2.draw(new Line2D.Double(x1, y1, x2, y2 - 1));

      if (x1 < x2) {
        xcircle = x1 + (x2 - x1) / 2 - radius;
        xlabel = xcircle + diameter - 2 * circlestroke + labelspace;
      }
      else {
        xcircle = x1 - radius - (x1 - x2) / 2;
        xlabel = xcircle - labelspace - labelwidth;
      }

      ycircle = y1 + (y2 - y1) / 2 - radius;
      ylabel = ycircle + diameter;

      // Label
      if (labels) {
        g2.setFont(DecisionTreeScheme.textfont);
        g2.setColor(DecisionTreeScheme.textcolor);
        g2.drawString(label, (int) xlabel, (int) ylabel);
      }

      // Circle
      g2.setColor(DecisionTreeScheme.treecirclebackgroundcolor);
      g2.fill(new Ellipse2D.Double(xcircle, ycircle, 8, 8));

      g2.setColor(DecisionTreeScheme.treecirclestrokecolor);
      g2.setStroke(new BasicStroke(circlestroke));
      g2.draw(new Ellipse2D.Double(xcircle, ycircle, 8, 8));
    }

    /*public void clearSearch() {
      clearSearch(viewportroot);
    }

    void clearSearch(Viewport emptyview) {
      if (emptyview.search)
        emptyview.search = false;

      if (emptyview.isLeaf())
        return;

      for (int index = 0; index < emptyview.getNumChildren(); index++) {
        Viewport child = emptyview.getChild(index);
        clearSearch(child);
      }
    }*/

    public Dimension getMinimumSize() {
      return new Dimension(0, 0);
    }

    public Dimension getPreferredSize() {
      return new Dimension((int) getSWidth(), (int) getSHeight());
    }

    // Returns scaled width of tree
    public double getSWidth() {
      return scale*dwidth;
    }

    // Returns scaled height of tree
    public double getSHeight() {
      return scale*dheight;
    }

    // Returns the tree depth given y offset
    public int findDepth(int y) {
      if (y > scale*(dheight - viewportroot.yspace))
        return -1;

      int depth = (int) (y / (scale * (viewportroot.yspace + viewportroot.height)));
      if ((y - scale*depth*(viewportroot.yspace + viewportroot.height)) >= scale*viewportroot.yspace)
        return depth;

      return -1;
    }

    Viewport emptyview;
    JMenuItem barchartitem;

    // Mouse listener methods
    public void mouseClicked(MouseEvent event) {
      int x = event.getX();
      int y = event.getY();

      if (zoom) {
        if (SwingUtilities.isLeftMouseButton(event)) {
          if (scale + scaleincrement < upperscale) {
            scale += scaleincrement;
            revalidate();
            repaint();
          }
        }
        else if (SwingUtilities.isRightMouseButton(event)) {
          if (scale - scaleincrement > lowerscale) {
            scale -= scaleincrement;
            revalidate();
            repaint();
          }
        }
      }
      else {
        int depth = findDepth(y);

        if (depth == -1) {
          return;
        }

        LinkedList list = offsets[depth];
        boolean valid = true;
        int index = 0;

        while (valid) {
          emptyview = (Viewport) list.get(index);
          int test = emptyview.test(x, y, getScale());

          if (test == -1) {
            index++;
          }
          else if (test == 1) {
            valid = false;

            if (emptyview.isVisible()) {
              if (SwingUtilities.isLeftMouseButton(event))
                expandView(emptyview);
              else if (SwingUtilities.isRightMouseButton(event)) {
              }
            }
          }
          else if (test == 2) {
            valid = false;
            emptyview.toggle();
            repaint();
          }

          if (index == list.size()) {
            valid = false;
          }
        }
      }
    }

    public void expandView(Viewport emptyview) {
      View view = emptyview.getView();

      if (view != null) {
        JComponent component = view.expand();

        if (component != null) {
          StringBuffer title;
          if (emptyview.getNumChildren() != 0)
            title = new StringBuffer("Split: ");
          else
            title = new StringBuffer("Leaf: ");
          title.append(emptyview.getLabel());

          JD2KFrame frame = new JD2KFrame(title.toString());
          frame.getContentPane().add(new JScrollPane(component));
          frame.pack();
          frame.setVisible(true);
        }
      }
    }

    public void mousePressed(MouseEvent event) {
      int x = event.getX();
      int y = event.getY();

      lastx = x;
      lasty = y;
    }

    public void mouseReleased(MouseEvent event) {}
    public void mouseExited(MouseEvent event) {}
    public void mouseEntered(MouseEvent event) {}

    // Mouse motion listener methods
    public void mouseMoved(MouseEvent event) {
      int x = event.getX();
      int y = event.getY();

      int depth = findDepth(y);

      if (depth == -1) {
        brushpanel.updateBrush(null);
        return;
      }

      LinkedList list = offsets[depth];
      boolean valid = true;
      int index = 0;

      while (valid) {
        Viewport emptyview = (Viewport) list.get(index);
        int test = emptyview.test(x, y, getScale());
        if (test == -1) {
          index++;
        }
        else if (test == 1) {
          valid = false;
          if (emptyview.isVisible())
            brushpanel.updateBrush(emptyview.getView());
        }
        else if (test == 2) {
          valid = false;
        }

        if (index == list.size()) {
          valid = false;
          brushpanel.updateBrush(null);
        }
      }
    }

    public void mouseDragged(MouseEvent event) {
      int x = event.getX();
      int y = event.getY();

      int xchange = x - lastx;
      int ychange = y - lasty;

      Dimension vpdimension = viewport.getExtentSize();
      double vpwidth = viewport.getWidth();
      double vpheight = viewport.getHeight();

      Point point = viewport.getViewPosition();

      point.x -= xchange;
      point.y -= ychange;

      if (point.x < 0)
        point.x = 0;
      else if (vpwidth > getSWidth())
        point.x = 0;
      else if (point.x + vpwidth > getSWidth())
        point.x = (int) (getSWidth() - vpwidth);

      if (point.y < 0)
        point.y = 0;
      else if (vpheight > getSHeight())
        point.y = 0;
      else if (point.y + vpheight > getSHeight())
        point.y = (int) (getSHeight() - vpheight);

      viewport.setViewPosition(point);
    }

    public int print(Graphics g, PageFormat pf, int pi) throws PrinterException {

      double pageHeight = pf.getImageableHeight();
      double pageWidth = pf.getImageableWidth();

      double cWidth = getWidth();
      double cHeight = getHeight();

      double scale = 1;
      if (cWidth >= pageWidth)
        scale = pageWidth / cWidth;
      if (cHeight >= pageHeight)
        scale = Math.min(scale, pageHeight / cHeight);

      double cWidthOnPage = cWidth * scale;
      double cHeightOnPage = cHeight * scale;

      if (pi >= 1)
        return Printable.NO_SUCH_PAGE;

      Graphics2D g2 = (Graphics2D) g;
      g2.translate(pf.getImageableX(), pf.getImageableY());
      g2.scale(scale, scale);
      print(g2);
      return Printable.PAGE_EXISTS;
    }
  }
}