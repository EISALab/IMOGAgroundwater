package ncsa.d2k.modules.core.discovery.cluster.vis.dendogram;

//==============
// Java Imports
//==============

import java.util.ArrayList;
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import java.util.TreeSet;
import java.util.Iterator;
import java.util.Hashtable;
import java.awt.image.*;

//===============
// Other Imports
//===============

import ncsa.d2k.userviews.swing.*;
import ncsa.d2k.gui.*;
import ncsa.d2k.core.modules.*;
import ncsa.gui.*;

import ncsa.d2k.modules.core.discovery.cluster.*;
import ncsa.d2k.modules.core.discovery.cluster.util.*;

/**
 *
 * <p>Title: DendogramPanel</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: NCSA Automated Learning Group</p>
 * @author D. Searsmith
 * @version 1.0
 */
public class DendogramPanel
    extends JUserPane
    implements UserView, ActionListener {

  //==============
  // Data Members
  //==============

  private HelpWindow helpWindow;

  private ClusterModel _model = null;

  private ArrayList _clusters = null;
  private TableCluster _root = null;

  private ArrayList m_rects = null;
  private ArrayList m_leaves = null;

  private ArrayList _built = null;
  private Hashtable _hash = null;

  int _ymark = 0;
  double _ycut = 0;
  double _lowvalue = 0;
  double _highvalue = 0;

  private JScrollPane m_scroll = null;

  private JButton m_reset = null;
  private String m_title = "";

  JPanel m_rectpan = null;
  JPanel _pan = null;

  private JRadioButton m_cohesion = null;
  private JRadioButton m_size = null;
  private JRadioButton m_sizecolor = null;

  private DendogramClusterVis _dcv = null;

  private JMenuBar menuBar = null;

  private ColorMenuItem hiSelectedColor = null;
  private ColorMenuItem lowSelectedColor = null;
  private JMenuItem helpItem = null;

  //================
  // Constructor(s)
  //================

  public DendogramPanel(DendogramClusterVis dcv) {
    _dcv = dcv;
    menuBar = new JMenuBar();
    helpWindow = new HelpWindow();
  }

  //====================
  // UserView Abstracts

  public Object getMenu() {
    return menuBar;
  }

  /**
       This method is called to allow the view to initialize itself, and to set
          any fields for which a reference to the module may be required.
          @param module the module this view is associated with.
   */
  public void initView(ViewModule module) {

  }

  /**
          This method is called whenever an input arrives, and is responsible
          for modifying the contents of any gui components that should reflect
          the value of the input.
          @param input this is the object that has been input.
          @param index the index of the input that has been received.
       @throws Exception when any exceptional situation occurs in this method
   */
  public void setInput(Object input, int index) throws Exception {
    if (index == 0) {

      _model = (ClusterModel) input;

      if (_model.getClusters() == null) {
        throw new Exception(
            "DendogramPanel.setInput(...) -- Cluster model contains no clusters.");
      }

      if (_model.getRoot() == null) {
        throw new Exception(
            "DendogramPanel.setInput(...) -- Cluster model does not contain a cluster tree.");
      }

      _clusters = _model.getClusters();
      _root = _model.getRoot();

      add(buildPanel(_root));

      JMenu chooseColors = new JMenu("Choose Colors");
      ImageIcon hi = new ImageIcon(new ColorComponent(Color.green).getImage());
      hiSelectedColor = new ColorMenuItem("Selected High Color", hi, Color.red);
      hiSelectedColor.addActionListener(this);
      chooseColors.add(hiSelectedColor);

      ImageIcon low = new ImageIcon(new ColorComponent(Color.red).getImage());
      lowSelectedColor = new ColorMenuItem("Selected Low Color", low,
                                           Color.green);
      lowSelectedColor.addActionListener(this);
      chooseColors.add(lowSelectedColor);

      menuBar.add(chooseColors);

      JMenu helpMenu = new JMenu("Help");
      helpItem = new JMenuItem("About Dendogram Vis..");
      helpItem.addActionListener(this);
      helpMenu.add(helpItem);
      menuBar.add(helpMenu);

      this.setPreferredSize(new Dimension(640, 480));

    } else {
      return;
    }

  }

  public void resetPanel(TableCluster root) {
    this.removeAll();
    add(buildPanel(root));
    this.revalidate();
    this.getParent().getParent().repaint();
  }

  //================
  // Public Methods
  //================

  public JPanel buildPanel(TableCluster root) {

    _pan = new JPanel();

    //call build rects
    buildRects(root);
    //instantiate the RectGraph and return it

    GraphSettings settings = new GraphSettings();
    settings.displayaxislabels = false;
    settings.displaygrid = false;
    settings.displaylegend = false;
    settings.displayscale = false;
    settings.displaytickmarks = false;
    settings.displaytitle = false;
    settings.title = "Dendogram Visualization";
    settings.xaxis = "Cluster Dissimilarity";
    settings.yaxis = "Clusters";

    GridBagLayout gbl = new GridBagLayout();
    GridBagConstraints gbc = new GridBagConstraints();

    _pan.setLayout(gbl);
    RectGraph oldrg = (RectGraph) m_rectpan;
    m_rectpan = new RectGraph(m_rects, m_leaves, settings, this);
    if (oldrg != null) {
      ( (RectGraph) m_rectpan).setHighColor(oldrg.getHighColor());
      ( (RectGraph) m_rectpan).setLowColor(oldrg.getLowColor());
    }
    m_scroll = new JScrollPane(m_rectpan);

    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.insets = new Insets(10, 10, 10, 10);
    gbc.weightx = 1;
    gbc.weighty = 1;
    gbc.fill = GridBagConstraints.BOTH;

    gbl.setConstraints(m_scroll, gbc);

    gbc.gridy = 1;
    gbc.weighty = 0;
    gbc.insets = new Insets(5, 5, 5, 5);

    JPanel buttpan = new JPanel();
    m_cohesion = new JRadioButton("Cohesion");
    m_cohesion.addActionListener(this);
    m_cohesion.setSelected(true);
    buttpan.add(m_cohesion);
    m_size = new JRadioButton("Size");
    m_size.addActionListener(this);
    buttpan.add(m_size);
    m_sizecolor = new JRadioButton("Size as Color");
    m_sizecolor.addActionListener(this);
    buttpan.add(m_sizecolor);

    ButtonGroup group = new ButtonGroup();
    group.add(m_cohesion);
    group.add(m_size);
    group.add(m_sizecolor);

    gbl.setConstraints(buttpan, gbc);

    gbc.gridy = 2;
    gbc.weighty = 0;
    gbc.insets = new Insets(5, 5, 5, 5);

    m_reset = new JButton("Reset");
    m_reset.addActionListener(this);
    gbl.setConstraints(m_reset, gbc);

    _pan.add(m_scroll);
    _pan.add(buttpan);
    _pan.add(m_reset);

    return _pan;

  }

  //================
  // Public Methods
  //================

  private void init() {

  }

  //=================
  // Private Methods
  //=================

  private void buildRects(TableCluster root) {
    //set start mark for Y axis
    _built = new ArrayList();
    m_rects = new ArrayList();
    m_leaves = new ArrayList();

    genBuilt(root);
    TreeSet sorted = new TreeSet(new cRank_Comparator());
    sorted.addAll(_built);
    _built = new ArrayList();
    for (Iterator it = sorted.iterator(); it.hasNext(); ) {
      _built.add(it.next());
    }
    Iterator it = _built.iterator();
    TableCluster lowest = null;
    int leaves = 0;
    while (it.hasNext()) {
      lowest = (TableCluster) it.next();
      if (lowest.getChildDistance() >= 0) {
        break;
      }
      leaves++;
    }

    _ymark = leaves * 10;

    //_ycut = Math.round((1 - selfsim)*10000*.8);
    //_lowvalue = Math.round(1/Math.pow(highestSim,2));
    //_lowvalue = Math.round(Math.pow(1 + lowest.getChildDistance(),1.5));
    _lowvalue = Math.round( (1 + lowest.getChildDistance()) * 100000);
    _ycut = _lowvalue * .8;
    _lowvalue -= _ycut;

    //_highvalue = Math.round(1/Math.pow(highestSim,2)) - _ycut;
    //_highvalue = Math.round(Math.pow(1 + root.getChildDistance(),1.5) - _ycut);
    _highvalue = Math.round( (1 + root.getChildDistance()) * 100000 - _ycut);

//    System.out.println("Lowest: " + lowest.getChildDistance());
//    System.out.println("Low Value: " + _lowvalue);
//    System.out.println("High Value: " + _highvalue);
//    System.out.println("ycut: " + _ycut);
//    System.out.println("ymark: " + _ymark);

    try {

      //call postorder walk and build rect wrappers
      _hash = new Hashtable();

      //also adds each cluster to _built
      postOrderWalk(root);

      for (int i = 0, n = _built.size(); i < n; i++) {
        RectWrapper rw = (RectWrapper) _hash.get(_built.get(n - i - 1));
        if (! (rw.getRect().getHeight() == 0)) {
          m_rects.add(rw);
        } else {
          m_leaves.add(rw);
        }
      }
    }
    catch (Exception exc) {
      System.out.println("Unable to build dendogram due to -- " + exc);
      exc.printStackTrace();
      return;
    }
  }

  private void genBuilt(TableCluster node) {

    if (node != null) {
      genBuilt(node.getLC());
      genBuilt(node.getRC());

      //do stuff
      _built.add(node);
    }

  }

  private void postOrderWalk(TableCluster node) {

    if (node != null) {
      postOrderWalk(node.getLC());
      postOrderWalk(node.getRC());

      //do stuff

      /**
           * This is a complete binary tree so if the left child is null then this is a
       * leaf.
       */
      if (node.isLeaf()) {
        RectWrapper rw = new RectWrapper(new Rectangle(0, _ymark, 0, 0), node,
                                         new Color(0, 0, 0));
        rw.addLeaf(node);
        _ymark -= 10;
        //System.out.println("Adding old rect: " + rw.getRect().getX() + " " + rw.getRect().getY() + " " + rw.getRect().getWidth() + " " + rw.getRect().getHeight());
        _hash.put(node, rw);
      } else {
        long newX = 0;
        long newY = 0;
        //long newW = Math.round((Math.pow(node.getChildDistance(),2)) - _ycut);
        //long newW = Math.round(Math.pow(1 + node.getChildDistance(),1.5) - _ycut);
        long newW = Math.round( (1 + node.getChildDistance()) * 100000 - _ycut);
        long newH = 0;
        TableCluster topC = null;
        TableCluster bottomC = null;
        topC = node.getLC();
        bottomC = node.getRC();
        Rectangle topR = ( (RectWrapper) _hash.get(topC)).getRect();
        Rectangle bottomR = ( (RectWrapper) _hash.get(bottomC)).getRect();
        //System.out.println("Top rect: " + topR.getX() + " " + topR.getY() + " " + topR.getWidth() + " " + topR.getHeight());
        //System.out.println("Bottom rect: " + bottomR.getX() + " " + bottomR.getY() + " " + bottomR.getWidth() + " " + bottomR.getHeight());
        newY = Math.round(topR.getY() - (topR.getHeight() / 2));
        newH = Math.round(newY - bottomR.getY() + (bottomR.getHeight() / 2));
        Color colval = null;

        double ratio = (newW / (_highvalue - _lowvalue));

        /*
                 if (ratio <= .4){
          colval = Color.red;
                 } else  if (ratio <= .75) {
          colval = Color.orange;
                 } else {
          colval = Color.yellow;
                 }
         */
        colval = new Color(0, 0, 0);
        //Color colval = new Color((int)Math.round(Math.random() * 255), (int)Math.round(Math.random() * 255), (int)Math.round(Math.random() * 255));
        RectWrapper rw = new RectWrapper(new Rectangle( (int) newX, (int) newY,
            (int) newW, (int) newH), node, colval);
        rw.addLeaves( ( (RectWrapper) _hash.get(topC)).getLeaves());
        rw.addLeaves( ( (RectWrapper) _hash.get(bottomC)).getLeaves());
        //System.out.println("Adding new rect: " + newX + " " + newY + " " + newW + " " + newH);
        _hash.put(node, rw);

      }

    }
  }

  private class cRank_Comparator
      implements java.util.Comparator {

    /**
     * put your documentation comment here
     */
    public cRank_Comparator() {
    }

    /** The small deviation allowed in double comparisons */
//    public double SMALL = 1e-6;

    //======================
    //Interface: Comparator
    //======================
    public int compare(Object o1, Object o2) {
      TableCluster tc1 = (TableCluster) o1;
      TableCluster tc2 = (TableCluster) o2;
      if (eq(tc1.getChildDistance(), tc2.getChildDistance())) {
        if (tc1.getClusterLabel() > tc2.getClusterLabel()) {
          return 1;
        } else if (tc1.getClusterLabel() < tc2.getClusterLabel()) {
          return -1;
        } else {
          return 0;
        }
      } else if (tc1.getChildDistance() > tc2.getChildDistance()) {
        return 1;
      } else {
        return -1;
      }
    }

    public boolean eq(double a, double b) {
      return a == b;
      //return (a - b < SMALL) && (b - a < SMALL);
    }

    /**
     * put your documentation comment here
     * @param o
     * @return
     */
    public boolean equals(Object o) {
      return this.equals(o);
    }
  }

  //==========================================
  // Interface Implementation: ActionListener
  //==========================================

  public void actionPerformed(ActionEvent evt) {
    if (evt.getSource() == m_reset) {
      this.resetPanel(_root);
    }
    if (evt.getSource() == m_cohesion) {
      if (m_cohesion.isSelected()) {
        ( (RectGraph) m_rectpan).setDisplayMode(RectGraph.DisplayCohesion);
      }
    }
    if (evt.getSource() == m_size) {
      if (m_size.isSelected()) {
        ( (RectGraph) m_rectpan).setDisplayMode(RectGraph.DisplayClusterSize);
      }
    }
    if (evt.getSource() == m_sizecolor) {
      if (m_sizecolor.isSelected()) {
        ( (RectGraph) m_rectpan).setDisplayMode(RectGraph.DisplaySizeAsColor);
      }
    }

    if (evt.getSource()instanceof ColorMenuItem) {
      ColorMenuItem cmi = (ColorMenuItem) evt.getSource();
      String text = cmi.getText();
      Color oldColor = cmi.c;
      Color newColor = JColorChooser.showDialog(this,
                                                "Choose", oldColor);
      if (newColor != null){
        if (cmi == hiSelectedColor) {
          ImageIcon hi = new ImageIcon(new ColorComponent(newColor).getImage());
          cmi.setIcon(hi);
          ( (RectGraph) m_rectpan).setHighColor(newColor);
          //ma.updateImage();
          return;
        }
        if (cmi == lowSelectedColor) {
          ImageIcon low = new ImageIcon(new ColorComponent(newColor).getImage());
          cmi.setIcon(low);
          ( (RectGraph) m_rectpan).setLowColor(newColor);
          //ma.updateImage();
          return;
        }
      }
    }

    if (evt.getSource() == helpItem) {
      helpWindow.setVisible(true);
    }
  }

  /**
   * A small square with a black outline.  The color of the
   * square is given in the constructor.
   */
  private final class ColorComponent
      extends JComponent {
    private final int DIM = 12;
    Color bkgrd;

    ColorComponent(Color c) {
      super();
      setOpaque(true);
      bkgrd = c;
    }

    public Dimension getPreferredSize() {
      return new Dimension(DIM, DIM);
    }

    public Dimension getMinimumSize() {
      return new Dimension(DIM, DIM);
    }

    public void paint(Graphics g) {
      g.setColor(bkgrd);
      g.fillRect(0, 0, DIM - 1, DIM - 1);
      g.setColor(Color.black);
      g.drawRect(0, 0, DIM - 1, DIM - 1);
    }

    void setBkgrd(Color c) {
      bkgrd = c;
    }

    BufferedImage getImage() {
      BufferedImage image = new BufferedImage(DIM, DIM,
                                              BufferedImage.TYPE_INT_RGB);
      Graphics g = image.getGraphics();
      paint(g);
      return image;
    }
  }

  /**
   * Identifies which colors to use
   */
  private final class ColorMenuItem
      extends JMenuItem {
    Color c;
    ColorMenuItem(String s, Icon i, Color c) {
      super(s, i);
    }
  }

  private final class HelpWindow
      extends JD2KFrame {
    HelpWindow() {
      super("About Dendogram Vis");
      JEditorPane jep = new JEditorPane("text/html", getHelpString());
      jep.setBackground(new Color(255, 255, 240));
      getContentPane().add(new JScrollPane(jep));
      setSize(400, 400);
    }
  }

  private static final String getHelpString() {
    String s = new String("<html>");
    s += "<h2>Dendogram Vis</h2>";
    s += "<p><b>Overview:</b> ";
    s +=
        "This module visualizes a ClusterModel object that is the output of an ";
    s +=
        "hierarchical agglomerative clustering algorithm.  The visualization is ";
    s += "of the dendogram produced by the agglomerative (bottom-up) process.";
    s += "</p>";

    s += "<p><b>Detailed Description:</b> ";
    s +=
        "The dendogram produced represents a hard clustering of a data table using ";
    s +=
        "a hierarchical agglomerative clustering algorithm.  Some cluster models will ";
    s +=
        "contain complete dendogram trees (from the actual table rows to the single ";
    s +=
        "root cluster.  Other models will contain trees that start at some cluster cut ";
    s +=
        "(for example the clusters returned from a KMeans algorithm) to the root. ";
    s += "Also, not all trees are monotonic (i.e. the height of subclusters is always <= ";
    s += "to the height of their parents where height is measured in cluster dissimilarity. ";
    s += "In particular, the centroid clustering methods (see HierAgglomClusterer props) are ";
    s += "known to sometimes produce non-monotonic dendogram trees.";
    s += "</p>";

    s += "<p> <b>GUI Controls:</b> ";
    s += "If you double click on a cluster in the tree the dendogram will be repainted with ";
    s += "the chosen cluster as root.  If you hit the reset button the original root will be ";
    s += "restored. </p><p> If you double click on a cluster while holding down the <b>shift key</b> a table ";
    s += "of values for that cluster will be displayed.</p><p>If you double click on a cluster ";
    s += "while holding down the <b>control key</b> the centroid for that cluster will be displayed. ";
    s += "</p>";

    s += "<p><b>Data Type Restrictions:</b> ";
    s += "The input ClusterModel must contain a table that is serializable.";
    s += "</p>";

    s += "<p><b>Data Handling:</b> ";
    s += "The input ClusterModel will be saved as part of the visualization.  It is not modified.";
    s += "</p>";

    s += "<p><b>Scalability:</b> ";
    s +=
        "The entire data table is saved as part of this visualization.  Sufficient ";
    s += "memory resources must be available to stage this data.";
    s += "</p>";
    s += "</html>";
    return s;
  }

}
