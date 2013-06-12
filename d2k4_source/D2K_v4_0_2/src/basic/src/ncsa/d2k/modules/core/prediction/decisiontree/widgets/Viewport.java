package ncsa.d2k.modules.core.prediction.decisiontree.widgets;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.*;
import java.awt.image.*;

import ncsa.d2k.modules.core.prediction.decisiontree.*;

public class Viewport {

  private static final String GREATER_THAN = ">";
  private static final String LESS_THAN = "<";
  private static final String GREATER_THAN_EQUAL_TO = ">=";
  private static final String LESS_THAN_EQUAL_TO = "<=";
  private static final String NOT_EQUAL_TO = "!=";
  private static final String EQUAL_TO = "==";

  // Decision tree model
  ViewableDTModel model;
  ViewableDTNode node;
  Viewport parent;
  ArrayList children;

  View view;

  boolean collapsed = false;

  boolean search = false;

  // X is midpoint of node, y is top left of bar graph
  double x, y;

  // Space between nodes
  public static double xspace = 20;
  public static double yspace = 80;

  double width = 45;

  double height = 40;

  boolean first = true;

  String label;

  double tside = 8;
  double tspace = 10;
  double theight;

  static JFrame graphics;

  DecisionTreeScheme scheme;

  public Viewport(ViewableDTModel model, ViewableDTNode node, Viewport parent, String label) {
    this.model = model;
    this.node = node;
    this.parent = parent;
    this.label = label;

    children = new ArrayList(node.getNumChildren());

    scheme = new DecisionTreeScheme();

    if (node instanceof ScalarViewableDTNode) {
      ScalarView view = new ScalarView();
      view.setData(model, node);
      setView(view);
    }

    else if (node instanceof NominalViewableDTNode) {
      NominalView view = new NominalView();
      view.setData(model, node);
      setView(view);
    }

    if (graphics == null) {
      graphics = new JFrame();
      graphics.addNotify();
      graphics.setFont(DecisionTreeScheme.textfont);
    }
  }

  public Viewport(ViewableDTModel model, ViewableDTNode node) {
    this(model, node, null, null);
  }

  public void addChild(Viewport viewport) {
    children.add(viewport);
  }

  public View getView() {
    return view;
  }

  public void setView(View view) {
    this.view = view;

    width = view.getWidth();

    double viewheight = view.getHeight();

    if (first) {
      height = viewheight;
      first = false;
    }
    else if (viewheight > height)
      height = viewheight;
  }

  public double getViewWidth() {
    return view.getWidth();
  }

  public double getViewHeight() {
    return view.getHeight();
  }

  public Viewport getChild(int index) {
    return (Viewport) children.get(index);
  }

  public int getNumChildren() {
    return children.size();
  }

  public boolean isLeaf() {
    if (children.size() == 0)
      return true;

    return false;
  }

  public int getDepth() {
    if (parent == null)
      return 0;

    return parent.getDepth() + 1;
  }

  public String getLabel() {
    return node.getLabel();
  }

  public String getBranchLabel(int index) {
    return node.getBranchLabel(index);
  }

  // Width for finding offset
  public double getWidth() {
    FontMetrics metrics = graphics.getGraphics().getFontMetrics();

    double swidth1, swidth2;

    if (label != null)
      swidth1 = 2*metrics.stringWidth(label);
    else
      swidth1 = 0;

    if (view != null)
      swidth2 = xspace + view.getWidth() + xspace;
    else
      swidth2 = xspace + width + xspace;

    if (swidth1 > swidth2)
      return swidth1;
    else
      return swidth2;
  }

  public double findSubtreeWidth() {
    if (isLeaf())
      return getWidth();

    double subtreewidth = 0;

    for (int index = 0; index < getNumChildren(); index++) {
      Viewport viewport = getChild(index);
      subtreewidth += viewport.findSubtreeWidth();
    }

    return subtreewidth;
  }

  // Width from midpoint to leftmost child node
  public double findLeftSubtreeWidth() {
    if (isLeaf())
      return getWidth()/2;

    int children = getNumChildren();

    if (children%2 == 0) {
      int middle = children/2;
      return findIntervalWidth(0, middle-1);
    }
    else {
      int middle = children/2 + 1;
      Viewport viewport = getChild(middle-1);
      return findIntervalWidth(0, middle-2) + viewport.findLeftSubtreeWidth();
    }
  }

  // Width from midpoint to rightmost child node
  public double findRightSubtreeWidth() {
    if (isLeaf())
      return getWidth()/2;

    int children = getNumChildren();

    if (children%2 == 0) {
      int middle = children/2;
      return findIntervalWidth(middle, children-1);
    }
    else {
      int middle = children/2 + 1;
      Viewport viewport = getChild(middle-1);
      return findIntervalWidth(middle, children-1) + viewport.findRightSubtreeWidth();
    }
  }

  // Determines offsets of children
  public void findOffsets() {
    int children = getNumChildren();

    if (children%2 == 0) {
      int middle = children/2;

      for (int index = 0; index < children; index++) {
        Viewport viewport = getChild(index);

        if (index <= middle-1)
          viewport.x = x - findIntervalWidth(index+1, middle-1) - viewport.findRightSubtreeWidth();
        else
          viewport.x = x + findIntervalWidth(middle, index-1) + viewport.findLeftSubtreeWidth();

        viewport.y = y + height + yspace;
      }
    }
    else {
      int middle = children/2 + 1;
      Viewport middleviewport = getChild(middle-1);

      for (int index = 0; index < children; index++) {
        Viewport viewport = getChild(index);

        if (index < middle-1) {
          if (index == middle-2)
            viewport.x = x - middleviewport.findLeftSubtreeWidth() - viewport.findRightSubtreeWidth();
          else
            viewport.x = x - middleviewport.findLeftSubtreeWidth() - findIntervalWidth(index+1, middle-2) - viewport.findRightSubtreeWidth();
        }
        else if (index == middle-1)
          viewport.x = x;
        else {
          if (index == middle)
            viewport.x = x + middleviewport.findRightSubtreeWidth() + viewport.findLeftSubtreeWidth();
          else
            viewport.x = x + middleviewport.findRightSubtreeWidth() + findIntervalWidth(middle, index-1) + viewport.findLeftSubtreeWidth();
        }

        viewport.y = y + height + yspace;
      }
    }
  }

  public double findIntervalWidth(int start, int end) {
    double intervalwidth = 0;

    for (; start <= end; start++) {
      Viewport viewport = getChild(start);
      intervalwidth += viewport.findSubtreeWidth();
    }

    return intervalwidth;
  }

  // Find branch index of parent corresponding to node
  public int findBranchIndex() {
    if (parent == null)
      return -1;

    for (int index = 0; index < parent.getNumChildren(); index++) {
      Viewport node = parent.getChild(index);

      if (node == this)
        return index;
    }

    return -1;
  }

  public void toggle() {
    if (collapsed)
      collapsed = false;
    else
      collapsed = true;
  }

  public boolean isVisible() {
    Viewport viewport = this;
    while (viewport.parent != null) {
      if (viewport.parent.collapsed)
        return false;
      viewport = viewport.parent;
    }

    return true;
  }

  public void setSearchBackground(boolean value) {
  }

  // Determine if given point falls in bounds of node
  public int test(int x1, int y1, double scale) {
    if (x1 >= scale*(x - width/2) && x1 <= scale*(x + width/2))
      return 1;

    if (x1 >= scale*(x + width/2) && x1 <= scale*(x + width/2 + tspace + tside + tspace)) {
      if (y1 >= scale*(y + height - tside - tspace) && y1 <= scale*(y + height))
        return 2;
    }

    return -1;
  }

  public void draw(Graphics2D g2) {
    if (view != null) {
      g2.translate((double) (x-width/2), (double) y);
      view.drawView(g2);
      g2.translate((double) (-x+width/2), (double) -y);
    }
    else {
      g2.setColor(scheme.viewbackgroundcolor);
      g2.setStroke(new BasicStroke(2));
      g2.drawRect((int) (x-width/2), (int) y, (int) width, (int) height);
    }

    // Triangle
    if (isLeaf())
      return;

    theight = .866025*tside;

    double x1, y1;
    double ycomponent = tside/2;
    double xcomponent = .577350*ycomponent;
    double xcenter, ycenter;

    if (collapsed) {
      xcenter = x + width/2 + tspace + xcomponent;
      ycenter = y + height - ycomponent;

      int xpoints[] = {(int) (xcenter-xcomponent), (int) (xcenter+theight-xcomponent), (int) (xcenter-xcomponent)};
      int ypoints[] = {(int) (ycenter-ycomponent), (int) ycenter, (int) (ycenter+ycomponent)};

      GeneralPath triangle = new GeneralPath(GeneralPath.WIND_EVEN_ODD, xpoints.length);
      triangle.moveTo((int) (xcenter-xcomponent), (int) (ycenter-ycomponent));
      for (int index = 1; index < xpoints.length; index++) {
        triangle.lineTo(xpoints[index], ypoints[index]);
      }
      triangle.closePath();

      g2.setColor(scheme.viewtrianglecolor);
      g2.fill(triangle);
    }
    else {
      xcenter = x + width/2 + tspace + xcomponent;
      ycenter = y + height - ycomponent;

      int xpoints[] = {(int) (xcenter-ycomponent), (int) (xcenter+ycomponent), (int) (xcenter)};
      int ypoints[] = {(int) (ycenter-xcomponent), (int) (ycenter-xcomponent), (int) (ycenter+ycomponent)};

      GeneralPath triangle = new GeneralPath(GeneralPath.WIND_EVEN_ODD, xpoints.length);
      triangle.moveTo((int) (xcenter-ycomponent), (int) (ycenter-xcomponent));
      for (int index = 1; index < xpoints.length; index++) {
        triangle.lineTo(xpoints[index], ypoints[index]);
      }
      triangle.closePath();

      g2.setColor(DecisionTreeScheme.viewtrianglecolor);
      g2.fill(triangle);
    }
  }

  public boolean getSearch() {
    return search;
  }

  public void setSearch(boolean value) {
    search = value;
  }

  public double findPurity() {
    if (model instanceof NominalViewableDTModel) {
      double sum = 0;
      double numerator = 0;
      double base = Math.log(2.0);

      try {
        String[] outputs = ((NominalViewableDTModel) model).getUniqueOutputValues();
        for (int index = 0; index < outputs.length; index++) {
          double tally = ((NominalViewableDTNode) node).getOutputTally(outputs[index]);
          numerator += -1.0*tally*Math.log(tally)/base;
          sum += tally;
        }
        numerator += sum*Math.log(sum)/base;
      } catch (Exception exception) {
        System.out.println(exception);
      }
      return numerator/sum;
    }

    return 0;
  }

  // Determine type of condition and call specific evaluate function
  protected boolean evaluate(SearchPanel.Condition condition) {
    try {
      if (node instanceof NominalViewableDTNode) {
        if (condition instanceof SearchPanel.PopulationCondition) {
          int population = ((NominalViewableDTNode) node).getOutputTally(condition.attribute);
          return evaluate(population, condition.value, condition.operator);
        }

        else if (condition instanceof SearchPanel.PercentCondition) {
          double percent = 100*(double) ((NominalViewableDTNode) node).getOutputTally(condition.attribute)/(double) node.getTotal();
          return evaluate(percent, condition.value, condition.operator);
        }
      }

      else if (condition instanceof SearchPanel.PurityCondition) {
        double purity = findPurity();
        return evaluate(purity, condition.value, condition.operator);
      }

      else if (condition instanceof SearchPanel.SplitCondition) {
        SearchPanel.SplitCondition splitcondition = (SearchPanel.SplitCondition) condition;

        if (splitcondition.scalar)
          return evaluateScalar(splitcondition);
        else
          return evaluateNominal(splitcondition);
      }
    } catch (Exception exception) {
      exception.printStackTrace();
      return false;
    }

    return false;
  }

  // Evaluate double values based on operator
  boolean evaluate(double dvalue, double value, String operator) {

    if (operator == GREATER_THAN)
      return value < dvalue;

    else if (operator == GREATER_THAN_EQUAL_TO)
      return value <= dvalue;

    else if (operator == LESS_THAN)
      return value > dvalue;

    else if (operator == LESS_THAN_EQUAL_TO)
      return value >= dvalue;

    else if (operator == EQUAL_TO)
      return value == dvalue;

    else if (operator == NOT_EQUAL_TO)
      return value != dvalue;

    return false;
  }

  boolean evaluate(int index, double dvalue, double value, String operator) {
    if (index == 0) {
      if (operator == GREATER_THAN)
        return value < dvalue;

      else if (operator == GREATER_THAN_EQUAL_TO)
        return value < dvalue;

      else if (operator == LESS_THAN)
        return value >= dvalue;

      else if (operator == LESS_THAN_EQUAL_TO)
        return value >= dvalue;

      else if (operator == EQUAL_TO)
        return false;

      else if (operator == NOT_EQUAL_TO)
        return value != dvalue;
    }
    else
      return evaluate(dvalue, value, operator);

    return false;
  }

  // Evaluate string values based on operator
  boolean evaluate(String svalue, String value, String operator) {
    if (operator == EQUAL_TO)
      return value.equals(svalue);

    else if (operator == NOT_EQUAL_TO)
      return !value.equals(svalue);

    return false;
  }

  // Evaluate scalar split condition
  boolean evaluateScalar(SearchPanel.SplitCondition condition) {
    if (parent == null)
      return false;

    if (!(parent.node instanceof NumericViewableDTNode))
      return false;

    NumericViewableDTNode numericparent = (NumericViewableDTNode) parent.node;

    String attribute = numericparent.getSplitAttribute();

    if (!attribute.equals(condition.attribute))
      return false;

    double splitvalue = numericparent.getSplitValue();
    int index = findBranchIndex();

    return evaluate(index, splitvalue, condition.value, condition.operator);
  }

  // Evaluate nominal split condition
  boolean evaluateNominal(SearchPanel.SplitCondition condition) {
    if (parent == null)
      return false;

    if (!(parent.node instanceof CategoricalViewableDTNode))
      return false;

    CategoricalViewableDTNode categoricalparent = (CategoricalViewableDTNode) parent.node;

    String attribute = categoricalparent.getSplitAttribute();

    if (!attribute.equals(condition.attribute))
      return false;

    String[] splitvalues = categoricalparent.getSplitValues();
    int index = findBranchIndex();
    String splitvalue = splitvalues[index];

    return evaluate(splitvalue, condition.svalue, condition.operator);
  }
}