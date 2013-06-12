package ncsa.d2k.modules.core.prediction.naivebayes;

import java.io.*;
import java.text.*;
import java.util.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.print.*;
import javax.swing.*;
import javax.swing.border.*;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.gui.*;
import ncsa.d2k.userviews.swing.*;
import ncsa.gui.*;

//import javax.help.*;

/**
 * An evidence visualization for a NaiveBayesModel.
 */
public final class NaiveBayesVis
    extends VisModule {

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "A NaiveBayesModel to visualize.";
      default:
        return "No such input";
    }
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Naive Bayes Model";
      default:
        return "NO SUCH INPUT!";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      default:
        return "No such output";
    }
  }

  public String getOutputName(int i) {
    switch (i) {
      default:
        return "NO SUCH OUTPUT!";
    }
  }

  public String getModuleInfo() {
    return "<p>Overview: An evidence visualization for a NaiveBayesModel. "+
        "<p>Detailed Description: This evidence visualization shows pie charts "+
        "that represent the different bins used.  The slices of the pie chart "+
        "represent the ratios of the output classes for that particular bin. "+
        "Selecting a chart in the evidence section will update the conclusion "+
        "pie chart.  The conclusion shows the probability that each output has "+
        "for classification given all the selected evidence charts."+
        "<p>Data Type Restrictions: none"+
        "<p>Data Handling:  This module does not destroy or modify the "+
        "input data."+
        "<p>Scalability: This module keeps data structures to represent the evidence "+
        "charts.  The amount of memory required is proportional to the number of "+
        "bins used in the discretization process.";
  }

  public String getModuleName() {
    return "Naive Bayes Vis";
  }

  public String[] getInputTypes() {
    String[] types = {
        "ncsa.d2k.modules.core.prediction.naivebayes.NaiveBayesModel"};
    return types;
  }

  public String[] getOutputTypes() {
    String[] types = {};
    return types;
  }


    public PropertyDescription[] getPropertiesDescriptions() {
   return new PropertyDescription[0]; // so that "windowName" property
   // is invisible
    }

  public String[] getFieldNameMapping() {
    return null;
  }

  public UserView createUserView() {
    return new NBView();
  }

  // color generation
  private static final Color[] colors = {
      new Color(71, 74, 98),
      new Color(191, 191, 115), new Color(111, 142, 116),
      new Color(178, 198, 181), new Color(153, 185, 216),
      new Color(96, 93, 71), new Color(146, 205, 163),
      new Color(203, 84, 84), new Color(217, 183, 170),
      new Color(140, 54, 57), new Color(203, 136, 76)};

  private static final Color yellowish = new Color(255, 255, 240);
  private static final Color grayish = new Color(236, 235, 222);
  private static final Color darkBg = new Color(219, 217, 206);
  private static final Color messageColor = new Color(64, 64, 64);
  private static final Color labelFg = new Color(96, 100, 86);
  private static final Color headerLabelColor = new Color(0, 0, 0);

  private static final String ATT = "ATTRIBUTES";
  private static final String EVI = "EVIDENCE";
  private static final String CONC = "CONCLUSION";
  private static final String NEW_LINE = "\n";
  private static final String SPACE = " ";
  private static final String TOTAL = "  Total : ";
  private static final String EQUALS = "=";
  private static final String COLON = " : ";
  private static final String PERCENT_SIGN = "%";
  private static final String OPEN_PAREN = " (";
  private static final String CLOSE_PAREN = ")";
  private static final String ERR = "% error";
  private static final String MORE = "More..";

  private static final Font labelFont = new Font("Helvetica", Font.PLAIN, 11);
  private static final int MAX_MENU_ITEMS = 15;
  private static final int PREF_GRID = 50;
  private static final int PREF_HEAD = 100;
  private static final String MAX_ATTRIBUTE_ERROR = "100.00% error";

  private static final String zoomicon = File.separator + "images" +
      File.separator + "zoom.gif";
  private static final String refreshicon = File.separator + "images" +
      File.separator + "home.gif";
  private static final String printicon = File.separator + "images" +
      File.separator + "printit.gif";

  private static final Dimension buttonsize = new Dimension(25, 25);

  /**
     Sort an array of Strings.
   */
  private static final String[] sortNames(String[] names) {
    String[] copy = new String[names.length];
    for (int i = 0; i < copy.length; i++) {
      copy[i] = names[i];
    }
    java.util.List l = Arrays.asList(copy);
    Collections.sort(l);
    String[] retVal = new String[l.size()];
    Iterator i = l.iterator();
    int idx = 0;
    while (i.hasNext()) {
      retVal[idx] = (String) i.next();
      idx++;
    }
    return retVal;
  }

  /**
   * The View class.
   */
  private final class NBView
      extends JUserPane
      implements ActionListener, Printable {
    private transient NaiveBayesModel model;
    private transient Legend legend;

    private transient Dimension preferredGrid;
    private transient Dimension preferredHeader;

    private transient NumberFormat nf;

    private transient boolean zoomin = false;

    // menu stuff
    private transient JMenuBar menuBar;
    private transient JCheckBoxMenuItem miPieChart;
    private transient JCheckBoxMenuItem miBarChart;
    private transient JCheckBoxMenuItem miAttrBest;
    private transient JCheckBoxMenuItem miAttrAlpha;
    private transient JCheckBoxMenuItem miEviTot;
    private transient JCheckBoxMenuItem miEviAlpha;
    private transient JCheckBoxMenuItem miShowPredVal;
    private transient JMenuItem miPrint;
    private transient ColorMenuItem[] colorItems;
    private transient AttributeMenuItem[] attributeItems;
    private transient JCheckBoxMenuItem miPercentage;
    private transient JMenuItem helpItem;
    private transient JMenuItem saveAsPmml;

    private transient JToggleButton zoom;
    private transient JButton printButton;
    private transient JButton refreshView;

    private transient String longest_attribute_name;
    private transient String longest_bin_name;

    // constants used in drawing
    private transient int gridwidth;
    private transient int gridheight;
    private transient float grid1;
    private transient float grid75;
    private transient float grid125;
    private transient float grid25;
    private transient float grid05;
    private transient float grid5;
    private transient float grid85;
    private transient float grid8;
    private transient float grid15;
    private transient float grid2;
    private transient float grid6;
    private transient float grid7;
    private transient float padding;

    // the components that make up the vis
    private transient GridPanel gp;
    private transient HeaderPanel hp;
    private transient CompositePanel cp;
    private transient MessageArea ma;

    // the selected item in each row
    private transient int[] selected;

    private transient int numRows = 0;
    private transient int numCols = 0;

    // map class names to a color
    private transient HashMap color_map;

    // the names of the classes
    private transient String[] class_names;
    // the names of the attributes
    private transient String[] attribute_names;
    private transient String[] all_ranked_attribute_names;
    private transient String[] all_alpha_attribute_names;
    // the data for the pie charts
    private transient NaiveBayesPieChartData[][] row_data;
    private transient double[] predictor_values;
    private transient int mouse_pos_y;

    private transient HelpWindow helpWindow;

    /*private void writeObject(ObjectOutputStream out) throws IOException {
      out.defaultWriteObject();
      System.out.println("WRITE NBV!");
           }
           private void readObject(ObjectInputStream in) throws Exception {
        in.defaultReadObject();
        System.out.println("READ NBV!"+this);
           }*/

    /**
     * Print this component.
     */
    public int print(Graphics g, PageFormat pf, int pi) throws PrinterException {

      double pageHeight = pf.getImageableHeight();
      double pageWidth = pf.getImageableWidth();

      double cWidth = getWidth();
      double cHeight = getHeight();

      double scale = 1;
      if (cWidth >= pageWidth) {
        scale = pageWidth / cWidth;
      }
      if (cHeight >= pageHeight) {
        scale = Math.min(scale, pageHeight / cHeight);

      }
      double cWidthOnPage = cWidth * scale;
      double cHeightOnPage = cHeight * scale;

      if (pi >= 1) {
        return Printable.NO_SUCH_PAGE;
      }

      Graphics2D g2 = (Graphics2D) g;
      g2.translate(pf.getImageableX(), pf.getImageableY());
      g2.scale(scale, scale);
      print(g2);
      return Printable.PAGE_EXISTS;
    }

    public void initView(ViewModule m) {
      menuBar = new JMenuBar();
      nf = NumberFormat.getInstance();
      nf.setMaximumFractionDigits(2);
      helpWindow = new HelpWindow();
    }

    public void setInput(Object o, int i) throws Exception {
      model = (NaiveBayesModel) o;
      if(!model.isReadyForVisualization())
     throw new Exception("NaiveBayesModel has to be processed by PrepareForVisualization module before entering NaiveBayesVisualization");
      all_ranked_attribute_names = model.getAttributeNames();
      //for(int j = 0; j < all_ranked_attribute_names.length; j++)
      //	  System.out.println("Ranked attr" + all_ranked_attribute_names[j]);
      all_alpha_attribute_names = sortNames(all_ranked_attribute_names);
      class_names = model.getClassNames();
      color_map = new HashMap();
      // map each class to a color
      for (int j = 0; j < class_names.length; j++) {
        color_map.put(class_names[j], colors[j % colors.length]);

        // when first displayed, we show all attributes by default
      }
      attribute_names = all_ranked_attribute_names;
      numRows = attribute_names.length;
      //System.out.println("numRows "  + numRows);
      selected = new int[attribute_names.length];
      row_data = new NaiveBayesPieChartData[attribute_names.length][];
      predictor_values = new double[attribute_names.length];
      int longest = 0;
      longest_bin_name = "";
      longest_attribute_name = "";
      
      // get the row data
      for (int j = 0; j < attribute_names.length; j++) {
        row_data[j] = model.getData(attribute_names[j]);
   //	System.out.println("row_data[" +j+ "] " + row_data[j]);
        predictor_values[j] = model.getPredictionValue(attribute_names[j]);
        selected[j] = -1;
        if (attribute_names[j].length() > longest) {
          longest_attribute_name = attribute_names[j];
          longest = attribute_names[j].length();
        }
        if (row_data[j].length > numCols) {
          numCols = row_data[j].length;
        }
        for (int k = 0; k < row_data[j].length; k++) {
          if (row_data[j][k].getBinName().length() > longest_bin_name.length()) {
            longest_bin_name = row_data[j][k].getBinName();
          }
        }
      }
      if (longest_attribute_name.length() < MAX_ATTRIBUTE_ERROR.length()) {
        longest_attribute_name = MAX_ATTRIBUTE_ERROR;

        // make the grid
      }
      gp = new GridPanel();
      int sq;
      if (numCols > numRows) {
        sq = PREF_GRID * numCols;
      }
      else {
        sq = PREF_GRID * numRows;
      }
      preferredGrid = new Dimension(sq, sq);
      gp.setPreferredSize(preferredGrid);
      // make the header
      hp = new HeaderPanel();
      preferredHeader = new Dimension(PREF_HEAD, sq);
      hp.setPreferredSize(preferredHeader);

      // make the message area
      if (class_names.length < 4) {
        ma = new MessageArea(class_names.length);
      }
      else {
        ma = new MessageArea(4);

        // make the legend
      }
      legend = new Legend();

      JScrollPane jsp = new JScrollPane(gp);
      jsp.setViewportBorder(new SPBorder());
      jsp.getViewport().setBackground(yellowish);
      jsp.setPreferredSize(new Dimension(500, 400));
      JViewport jv = new JViewport();
      jv.setView(hp);
      jsp.setRowHeader(jv);
      JLabel att = new AALabel(ATT);
      att.setBorder(new EmptyBorder(10, 3, 10, 0));
      JLabel evi = new AALabel(EVI);
      evi.setBorder(new EmptyBorder(10, 3, 10, 0));
      jsp.setCorner(ScrollPaneConstants.UPPER_LEFT_CORNER, att);

      JViewport jv1 = new JViewport();
      jv1.setView(evi);
      jsp.setColumnHeader(jv1);

      cp = new CompositePanel();
      cp.setPreferredSize(new Dimension(250, 250));
      JScrollPane jp1 = new JScrollPane(cp);
      jp1.setViewportBorder(new SPBorder2());
      JScrollPane jp2 = new SameSizeSP(ma, ma.getPreferredSize());
      JScrollPane jp3 = new SameSizeSP(legend, ma.getPreferredSize());
      JViewport jch = new JViewport();
      JPanel cnr = new JPanel();
      cnr.setBackground(yellowish);
      jp3.setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER, cnr);
      jp2.setMaximumSize(ma.getPreferredSize());
      jp3.setMaximumSize(ma.getPreferredSize());
      JPanel pq = new JPanel();
      JPanel pq1 = new JPanel();
      pq1.setLayout(new GridLayout(2, 1));
      pq1.add(jp3);
      pq1.add(jp2);
      pq.setLayout(new BorderLayout());
      pq.add(jp1, BorderLayout.CENTER);
      pq.add(pq1, BorderLayout.SOUTH);
      JViewport jv2 = new JViewport();
      JLabel clLabel = new AALabel(CONC);
      clLabel.setBorder(new EmptyBorder(10, 3, 10, 0));
      JPanel clp = new JPanel();
      JPanel bp = new JPanel();
      bp.setLayout(new GridLayout(1, 3));
      Image im = getImage(refreshicon);
      ImageIcon ri = null;
      if (im != null) {
        ri = new ImageIcon(im);
      }
      if (ri != null) {
        refreshView = new JButton(ri);
      }
      else {
        refreshView = new JButton("R");
      }
      refreshView.addActionListener(this);
      refreshView.setToolTipText("Reset View");

      im = getImage(printicon);
      ImageIcon pi = null;
      if (im != null) {
        pi = new ImageIcon(im);
      }
      if (pi != null) {
        printButton = new JButton(pi);
      }
      else {
        printButton = new JButton("P");
      }
      printButton.addActionListener(this);
      printButton.setToolTipText("Print");

      im = getImage(zoomicon);
      ImageIcon zi = null;
      if (im != null) {
        zi = new ImageIcon(getImage(zoomicon));
      }
      if (zi != null) {
        zoom = new JToggleButton(zi);
      }
      else {
        zoom = new JToggleButton("Z");

      }
      zoom.addActionListener(this);
      zoom.setToolTipText("Zoom");

      if (ri != null && zi != null && pi != null) {
        zoom.setMaximumSize(buttonsize);
        zoom.setPreferredSize(buttonsize);
        printButton.setMaximumSize(buttonsize);
        printButton.setPreferredSize(buttonsize);
        refreshView.setMaximumSize(buttonsize);
        refreshView.setPreferredSize(buttonsize);
      }

      bp.add(refreshView);
      bp.add(printButton);
      bp.add(zoom);

      clp.setLayout(new BorderLayout());
      clp.add(clLabel, BorderLayout.CENTER);
      JPanel bq = new JPanel();
      bq.setLayout(new BoxLayout(bq, BoxLayout.Y_AXIS));
      bq.add(Box.createGlue());
      bq.add(bp);
      bq.add(Box.createGlue());
      clp.add(bq, BorderLayout.EAST);
      jv2.setView(clp);

      jp1.setColumnHeader(jv2);

      pq.setBorder(new EmptyBorder(3, 3, 3, 3));
      JPanel bg = new JPanel();
      bg.setLayout(new BorderLayout());
      bg.add(jsp, BorderLayout.CENTER);
      bg.setBorder(new EmptyBorder(3, 3, 3, 3));
      JPanel p1 = new JPanel();
      p1.setLayout(new BorderLayout());
      p1.add(bg, BorderLayout.CENTER);
      p1.add(pq, BorderLayout.EAST);

      setLayout(new BorderLayout());
      add(p1, BorderLayout.CENTER);

      // setup the menus
      JMenu m1 = new JMenu("Options");
      JMenu m2 = new JMenu("Views");
      m2.add(miPieChart = new JCheckBoxMenuItem("Pie Charts", true));
      m2.add(miBarChart = new JCheckBoxMenuItem("Bar Charts", false));
      m1.add(m2);
      menuBar.add(m1);
      miPieChart.addActionListener(this);
      miBarChart.addActionListener(this);

      JMenu m3 = new JMenu("Sort Attributes By");
      m3.add(miAttrBest = new JCheckBoxMenuItem("Best Predictor", true));
      m3.add(miAttrAlpha = new JCheckBoxMenuItem("Alphabetical Order", false));
      miAttrBest.addActionListener(this);
      miAttrAlpha.addActionListener(this);

      JMenu m4 = new JMenu("Sort Evidence By");
      m4.add(miEviTot = new JCheckBoxMenuItem("Bin Weights", true));
      m4.add(miEviAlpha = new JCheckBoxMenuItem("Alphabetical Order", false));
      miEviTot.addActionListener(this);
      miEviAlpha.addActionListener(this);

      JMenu m5 = new JMenu("Show Attributes");
      attributeItems = new AttributeMenuItem[attribute_names.length];
      JMenu curMenu = m5;
      int numItems = 0;
      for (int j = 0; j < attributeItems.length; j++) {
        attributeItems[j] = new AttributeMenuItem(attribute_names[j]);
        attributeItems[j].addActionListener(this);
        if (numItems == MAX_MENU_ITEMS) {
          JMenu nextMenu = new JMenu(MORE);
          curMenu.insert(nextMenu, 0);
          nextMenu.add(attributeItems[j]);
          curMenu = nextMenu;
          numItems = 1;
        }
        else {
          curMenu.add(attributeItems[j]);
          numItems++;
        }
      }

      JMenu m6 = new JMenu("Set Colors");
      colorItems = new ColorMenuItem[class_names.length];
      curMenu = m6;
      numItems = 0;
      for (int j = 0; j < colorItems.length; j++) {
        colorItems[j] = new ColorMenuItem(class_names[j]);
        colorItems[j].addActionListener(this);
        if (numItems == MAX_MENU_ITEMS) {
          JMenu nextMenu = new JMenu(MORE);
          curMenu.insert(nextMenu, 0);
          //curMenu.add(nextMenu);
          nextMenu.add(colorItems[j]);
          curMenu = nextMenu;
          numItems = 1;
        }
        else {
          curMenu.add(colorItems[j]);
          numItems++;
        }
      }

      m1.add(m3);
      m1.add(m4);
      m1.add(m5);
      m1.add(m6);
      m1.addSeparator();
      m1.add(miShowPredVal =
             new JCheckBoxMenuItem("Show Predictor Values",
                                   false));
      miShowPredVal.addActionListener(this);
      m1.add(miPercentage =
             new JCheckBoxMenuItem("Show Bin Weight Percentage",
                                   false));
      miPercentage.addActionListener(this);
      m1.addSeparator();
      m1.add(miPrint = new JMenuItem("Print.."));
      miPrint.addActionListener(this);
      m1.add(saveAsPmml = new JMenuItem("Save as PMML..."));
      saveAsPmml.setEnabled(false);
      saveAsPmml.addActionListener(this);

      JMenu helpMenu = new JMenu("Help");
      helpItem = new JMenuItem("About Naive Bayes Vis..");
      helpMenu.add(helpItem);
      helpItem.addActionListener(this);

    //  HelpSet hs = new HelpSet(getClass().getClassLoader(), url);
    /*HelpSet hs = getHelpSet("help/nbvis");
    if(hs != null) {
      HelpBroker helpbroker = hs.createHelpBroker();
      ActionListener listener = new CSH.DisplayHelpFromSource(helpbroker);
      helpItem.addActionListener(listener);
    }
    else
      helpItem.setEnabled(false);
     */

      menuBar.add(helpMenu);
    }

    public Object getMenu() {
      return menuBar;
    }

    public void actionPerformed(ActionEvent e) {
      Object src = e.getSource();
      // show pie charts (the default)
      if (src == miPieChart) {
        miPieChart.setState(true);
        miBarChart.setState(false);
        repaint();
      }

      // show bar charts
      else if (src == miBarChart) {
        miPieChart.setState(false);
        miBarChart.setState(true);
        repaint();
      }

      // sort the attributes by best predictor
      else if (src == miAttrBest) {
        miAttrBest.setState(true);
        miAttrAlpha.setState(false);
        model.clearEvidence();
        for (int i = 0; i < selected.length; i++) {
          selected[i] = -1;

        }
        HashMap toShow = new HashMap();
        for (int i = 0; i < attributeItems.length; i++) {
          if (attributeItems[i].getState()) {
            toShow.put(attributeItems[i].getText(),
                       attributeItems[i].getText());
          }
        }

        LinkedList ll = new LinkedList();
        for (int i = 0; i < all_ranked_attribute_names.length; i++) {
          if (toShow.containsKey(all_ranked_attribute_names[i])) {
            ll.add(all_ranked_attribute_names[i]);
          }
        }
        attribute_names = new String[ll.size()];
        numRows = attribute_names.length;
        Iterator ii = ll.iterator();
        int idx = 0;
        while (ii.hasNext()) {
          attribute_names[idx] = (String) ii.next();
          idx++;
        }

        row_data = new NaiveBayesPieChartData[attribute_names.length][];
        predictor_values = new double[attribute_names.length];
        numCols = 0;
        for (int i = 0; i < attribute_names.length; i++) {
          row_data[i] = model.getData(attribute_names[i]);
          predictor_values[i] = model.getPredictionValue(attribute_names[i]);
          if (row_data[i].length > numCols) {
            numCols = row_data[i].length;
          }
        }
        repaint();
      }

      // sort the attributes alphabetically
      else if (src == miAttrAlpha) {
        miAttrBest.setState(false);
        miAttrAlpha.setState(true);
        model.clearEvidence();
        for (int i = 0; i < selected.length; i++) {
          selected[i] = -1;

        }
        HashMap toShow = new HashMap();
        for (int i = 0; i < attributeItems.length; i++) {
          if (attributeItems[i].getState()) {
            toShow.put(attributeItems[i].getText(),
                       attributeItems[i].getText());
          }
        }

        LinkedList ll = new LinkedList();
        for (int i = 0; i < all_alpha_attribute_names.length; i++) {
          if (toShow.containsKey(all_alpha_attribute_names[i])) {
            ll.add(all_alpha_attribute_names[i]);
          }
        }
        attribute_names = new String[ll.size()];
        numRows = attribute_names.length;
        Iterator ii = ll.iterator();
        int idx = 0;
        while (ii.hasNext()) {
          attribute_names[idx] = (String) ii.next();
          idx++;
        }

        row_data = new NaiveBayesPieChartData[attribute_names.length][];
        predictor_values = new double[attribute_names.length];
        numCols = 0;
        for (int i = 0; i < attribute_names.length; i++) {
          row_data[i] = model.getData(attribute_names[i]);
          predictor_values[i] = model.getPredictionValue(attribute_names[i]);
          if (row_data[i].length > numCols) {
            numCols = row_data[i].length;
          }
        }
        repaint();
      }

      // change the color associated with a class
      else if (src instanceof ColorMenuItem) {
        ColorMenuItem mi = (ColorMenuItem) src;
        Color oldColor = getColor(mi.getText());
        StringBuffer sb = new StringBuffer("Choose ");
        sb.append(mi.getText());
        sb.append(" Color");
        Color newColor = JColorChooser.showDialog(this, sb.toString(), oldColor);
        if (newColor != null) {
          color_map.put(mi.getText(), newColor);
          repaint();
        }
      }

      // show or hide attributes
      else if (src instanceof AttributeMenuItem) {
        model.clearEvidence();
        for (int i = 0; i < selected.length; i++) {
          selected[i] = -1;
        }
        AttributeMenuItem mi = (AttributeMenuItem) src;

        HashMap toShow = new HashMap();
        for (int i = 0; i < attributeItems.length; i++) {
          if (attributeItems[i].getState()) {
            toShow.put(attributeItems[i].getText(),
                       attributeItems[i].getText());
          }
        }

        // sort by the best predictor
        if (miAttrBest.getState()) {
          LinkedList ll = new LinkedList();
          for (int i = 0; i < all_ranked_attribute_names.length; i++) {
            if (toShow.containsKey(all_ranked_attribute_names[i])) {
              ll.add(all_ranked_attribute_names[i]);
            }
          }
          attribute_names = new String[ll.size()];
          Iterator ii = ll.iterator();
          int idx = 0;
          while (ii.hasNext()) {
            attribute_names[idx] = (String) ii.next();
            idx++;
          }
        }
        // sort in alphabetical order
        else {
          LinkedList ll = new LinkedList();
          for (int i = 0; i < all_alpha_attribute_names.length; i++) {
            if (toShow.containsKey(all_alpha_attribute_names[i])) {
              ll.add(all_alpha_attribute_names[i]);
            }
          }
          attribute_names = new String[ll.size()];
          numRows = attribute_names.length;
          Iterator ii = ll.iterator();
          int idx = 0;
          while (ii.hasNext()) {
            attribute_names[idx] = (String) ii.next();
            idx++;
          }
        }

        row_data = new NaiveBayesPieChartData[attribute_names.length][];
        predictor_values = new double[attribute_names.length];
        numCols = 0;
        for (int i = 0; i < attribute_names.length; i++) {
          row_data[i] = model.getData(attribute_names[i]);
          predictor_values[i] = model.getPredictionValue(attribute_names[i]);
          if (row_data[i].length > numCols) {
            numCols = row_data[i].length;
          }
        }
        setPreferredSize(getPreferredSize());
        repaint();
      }

      // sort the evidence by the bin totals (the default)
      else if (src == miEviTot) {
        miEviTot.setState(true);
        miEviAlpha.setState(false);
        model.clearEvidence();

        // call on model to resort
        model.sortChartDataByRank();
        for (int i = 0; i < attribute_names.length; i++) {
          row_data[i] = model.getData(attribute_names[i]);
        }
        for (int i = 0; i < selected.length; i++) {
          selected[i] = -1;

        }
        repaint();
      }

      // sort the evidence alphabetically by bin name
      else if (src == miEviAlpha) {
        miEviTot.setState(false);
        miEviAlpha.setState(true);

        model.clearEvidence();
        // call on model to resort
        model.sortChartDataAlphabetically();
        for (int i = 0; i < attribute_names.length; i++) {
          row_data[i] = model.getData(attribute_names[i]);
        }
        for (int i = 0; i < selected.length; i++) {
          selected[i] = -1;

        }
        repaint();
      }
      // show the bin weights as percentages
      else if (src == miPercentage) {
        repaint();

        // show the predictor values
      }
      else if (src == miShowPredVal) {
        hp.repaint();

        // print
      }
      else if (src == miPrint || src == printButton) {
        PrinterJob pj = PrinterJob.getPrinterJob();
        pj.setPrintable(this);
        if (pj.printDialog()) {
          try {
            pj.print();
          }
          catch (Exception ex) {
            ex.printStackTrace();
          }
        }
      }
      // zoom
      else if (src == zoom) {
        if (zoomin) {
          zoomin = false;
        }
        else {
          zoomin = true;
        }
      }
      // reset the view to the initial size
      else if (src == refreshView) {
        gp.setPreferredSize(preferredGrid);
        hp.setPreferredSize(preferredHeader);
        gp.revalidate();
        hp.revalidate();
      }
      else if (src == helpItem) {
        helpWindow.setVisible(true);



      }
      else if(src == saveAsPmml) {
        JFileChooser jfc = new JFileChooser();

        int returnVal = jfc.showSaveDialog(null);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
          try {
            // get the selected file
            File newFile = jfc.getSelectedFile();

            //WriteNaiveBayesPMML.writePMML(model, newFile.getAbsolutePath());
          }
          catch(Exception ex) {
              ncsa.gui.ErrorDialog.showDialog(ex, "Error Writing PMML");
          }
        }
      }


    }

    private final Color getColor(String s) {
      return (Color) color_map.get(s);
    }

    /**
       Scales a graphics context's font so that a given
       string will fit within a given horizontal pixel width.
     */
    private final FontMetrics scaleFont(Graphics2D g2, String str, int spaceH,
                                        int spaceV) {
      boolean fits = false;
      Font ft = g2.getFont();
      String nm = ft.getName();
      FontMetrics fm = g2.getFontMetrics();
      int size = ft.getSize();
      int style = ft.getStyle();

      while (!fits) {
        if (fm.getHeight() <= spaceV &&
            fm.stringWidth(str) <= spaceH) {
          fits = true;
        }
        else {
          if (size <= 4) {
            fits = true;
          }
          else {
            g2.setFont(ft = new Font(nm, style, --size));
            fm = g2.getFontMetrics();
          }
        }
      }
      return fm;
    }

    /**
       Show the evidence in a grid
     */
    private final class GridPanel
        extends JPanel
        implements MouseListener,
        MouseMotionListener {

      GridPanel() {
        addMouseListener(this);
        addMouseMotionListener(this);
        setBackground(yellowish);
      }

      public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        // rendering
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        int xl = 0;
        int yl = 0;

        // draw the name of the chart
        scaleFont(g2, longest_bin_name,
                  gridwidth, (int) grid15);
        for (int i = 0; i < attribute_names.length; i++) {
          NaiveBayesPieChartData[] thisrow =
              row_data[i];

          for (int j = 0; j < thisrow.length; j++) {
            double w = (double) thisrow[j].getTotal() /
                (double) thisrow[j].getRowTotal();

            if (miPercentage.getState()) {
              drawWeightPercentage(g2, xl, yl, w);
            }
            else {
              drawWeightBar(g2, xl, yl, w);

            }
            boolean is_selected = false;
            if (selected[i] == j) {
              is_selected = true;

              // draw chart
            }
            if (miPieChart.getState()) {
              drawPieChart(g2, xl, yl,
                           thisrow[j], is_selected);
            }
            else {
              drawBarChart(g2, xl, yl,
                           thisrow[j], is_selected);

            }
            drawName(g2, xl, yl,
                     thisrow[j].getBinName());
            xl += gridwidth;
          }
          xl = 0;
          yl += gridheight;
        }
      }

      public void setBounds(int x, int y, int w, int h) {
        if (numCols != 0)
          gridwidth = w / numCols;
        else
          gridwidth = 0;
          
        if (numRows != 0)
          gridheight = w / numRows;
        else
          gridheight = 0;

        // make gridwidth equal to gridheight
        if (gridwidth < gridheight) {
          gridheight = gridwidth;
        }
        else {
          gridwidth = gridheight;

          // cache these calculations for later
        }
        grid1 = (float) (.1f * gridwidth);
        grid75 = (float) (.75f * gridwidth);
        grid125 = (float) (.125f * gridwidth);
        grid05 = (float) (.05f * gridwidth);
        grid5 = (float) (.5f * gridwidth);
        grid85 = (float) (.85f * gridwidth);
        grid15 = (float) (.15f * gridwidth);
        grid2 = (float) (.2f * gridwidth);
        grid6 = (float) (.6f * gridwidth);
        grid8 = (float) (.8f * gridwidth);
        grid25 = (float) (.25f * gridwidth);
        grid7 = (float) (.7 * gridwidth);
        padding = (float) (.03 * gridwidth);
        super.setBounds(x, y, w, h);
      }

      /**
         Draw the weight bar above a pie/bar chart.
       */
      private final void drawWeightBar(Graphics2D g2, int x, int y,
                                       double weight) {
        g2.setPaint(Color.black);
        g2.draw(new Rectangle2D.Double(x + (int) grid25, y + (int) grid1,
                                       (int) grid5, (int) grid05));
        g2.setPaint(Color.white);
        g2.fill(new Rectangle2D.Double(x + (int) (grid25 + 1),
                                       y + (int) (grid1 + 1), (int) (grid5 - 1),
                                       (int) (grid05 - 1)));
        g2.setPaint(Color.darkGray);
        g2.fill(new Rectangle2D.Double(x + (int) (grid25 + 1),
                                       y + (int) (grid1 + 1),
                                       (int) (weight * (grid5 - 1)),
                                       (int) (grid05 - 1)));
      }

      /**
       * Draw the bin weight as a percentage
       */
      private final void drawWeightPercentage(Graphics2D g2, int x, int y,
                                              double w) {
        // draw text
        g2.setPaint(Color.black);
        FontMetrics metrics = g2.getFontMetrics();
        StringBuffer sb = new StringBuffer(nf.format(w * 100));
        sb.append("%");
        String n = sb.toString();
        g2.drawString(n,
                      (int) (x + grid5 - .5 * metrics.stringWidth(n)),
                      (int) (y + grid05 + metrics.getAscent() + 2));
      }

      /**
       * Draw a bar chart for a bin
       */
      private final void drawBarChart(Graphics2D g2, int x, int y,
                                      NaiveBayesPieChartData data,
                                      boolean selected) {

        // draw background if selected
        if (selected) {
          g2.setPaint(grayish);
          g2.fill(new Rectangle2D.Double(x + grid2,
                                         y + grid25 - padding, grid6,
                                         grid5 + 2 * padding));
          g2.setPaint(Color.black);
          g2.draw(new Rectangle2D.Double(x + grid2,
                                         y + grid25 - padding,
                                         grid6, grid5 + 2 * padding));
        }
        g2.setPaint(Color.black);

        // draw the axes
        g2.draw(new Line2D.Double(x + (int) (grid25),
                                  y + (int) (grid75), x + (int) (grid75),
                                  y + (int) (grid75)));
        g2.draw(new Line2D.Double(x + (int) (grid25),
                                  y + (int) (grid25), x + (int) (grid25),
                                  y + (int) (grid75)));

        int barWidth = (int) (grid5 / class_names.length);
        double startX = x + (int) (grid25); // + 1

        // draw the bars
        // start at the top and move down
        for (int count = class_names.length - 1; count >= 0; count--) {
          g2.setColor(getColor(data.getClassName(count)));
          double ratio = data.getClass(class_names[count]);

          g2.fill(new Rectangle2D.Double(startX,
                                         (y + grid75) - (grid5 * ratio),
                                         barWidth,
                                         grid5 * ratio));

          // 5/20/02:
          g2.setColor(Color.black);
          g2.draw(new Rectangle2D.Double(startX,
                                         (y + grid75) - (grid5 * ratio),
                                         barWidth,
                                         grid5 * ratio));

          startX += barWidth;
        }
      }

      /**
       * Draw a bin as a pie chart
       */
      private final void drawPieChart(Graphics2D g2, int x, int y,
                                      NaiveBayesPieChartData data,
                                      boolean selected) {

        // draw background if selected
        if (selected) {
          g2.setPaint(grayish);
          g2.fill(new Rectangle2D.Double(x + grid2,
                                         y + grid25 - padding, grid6,
                                         grid5 + 2 * padding));
          g2.setPaint(Color.black);
          g2.draw(new Rectangle2D.Double(x + grid2,
                                         y + grid25 - padding,
                                         grid6, grid5 + 2 * padding));
        }

        // draw chart
        int angle = 0;
        if (data.getTotal() == 0) {
          g2.setColor(Color.darkGray);
          g2.fill(new Arc2D.Double(x + (int) (grid25), y + (int) (grid25),
                                   (int) (grid5), (int) (grid5), 0,
                                   360, Arc2D.PIE));
        }
        else {
          for (int count = class_names.length - 1; count >= 0; count--) {
            g2.setPaint(getColor(data.getClassName(count)));
            double ratio = data.getClass(class_names[count]);

            if (count == class_names.length - 1) {
              g2.fill(new Arc2D.Double(x + (int) (grid25), y + (int) (grid25),
                                       (int) (grid5), (int) (grid5), angle,
                                       (int) (360 - angle), Arc2D.PIE));
            }
            else {
              g2.fill(new Arc2D.Double(x + (int) (grid25), y + (int) (grid25),
                                       (int) (grid5), (int) (grid5), angle,
                                       (int) (360 * ratio), Arc2D.PIE));

            }
            angle += (int) (360 * ratio);
          }
        }
      }

      /**
       * Draw the name of a bin
       */
      private final void drawName(Graphics2D g2, int x, int y, String n) {
        // draw text
        g2.setPaint(Color.black);
        FontMetrics metrics = g2.getFontMetrics();
        g2.drawString(n,
                      (int) (x + grid5 - .5 * metrics.stringWidth(n)),
                      (int) (y + grid85 + metrics.getAscent()));
      }

      /**
         Zoom in or out when the mouse is pressed
       */
      public void mousePressed(MouseEvent e) {
        mouse_pos_y = e.getY();
        if (zoomin) {
          if (!e.isMetaDown()) {
            Dimension d = getPreferredSize();
            setPreferredSize(new Dimension( (int) (d.width * 1.1),
                                           (int) (d.height * 1.1)));
            d = hp.getPreferredSize();
            hp.setPreferredSize(new Dimension(d.width,
                                              (int) (d.height * 1.1)));
            revalidate();
            hp.revalidate();
          }
          else {
            Dimension d = getPreferredSize();
            setPreferredSize(new Dimension( (int) (d.width * .9),
                                           (int) (d.height * .9)));
            d = hp.getPreferredSize();
            hp.setPreferredSize(new Dimension(d.width,
                                              (int) (d.height * .9)));
            revalidate();
            hp.revalidate();
          }
        }
      }

      /**
       * Update the selected item when the mouse is clicked
       */
      public void mouseClicked(MouseEvent e) {
        if (zoomin) {
          return;
        }
        int cx = e.getX();
        int cy = e.getY();
        int xpos = (int) grid2, ypos = (int) grid2;

        if (cx < xpos || cy < ypos) {
          return;
        }
        else {
          for (int i = 0; i < attribute_names.length; i++) {
            int bins_in_row = row_data[i].length;
            for (int j = 0; j < bins_in_row; j++) {
              // this is of course unnecessary now, but i may have use for it later
              if (j < bins_in_row) {

                if (cx > xpos && cx < xpos + grid6 && cy > ypos &&
                    cy < ypos + grid6) {
                  String bin_name = row_data[i][j].getBinName();

                  if (selected[i] == j) {
                    model.removeEvidence(attribute_names[i], bin_name);
                    selected[i] = -1;
                  }
                  else if (selected[i] == -1) {
                    model.addEvidence(attribute_names[i], bin_name);
                    selected[i] = j;
                  }
                  else {
                    String old_name =
                        row_data[i][selected[i]].getBinName();
                    model.removeEvidence(attribute_names[i], old_name);
                    model.addEvidence(attribute_names[i], bin_name);
                    selected[i] = j;
                  }

                  repaint();
                  cp.repaint();
                  return;
                }
              }
              xpos += gridwidth;
            }
            xpos = (int) grid2;
            ypos += gridheight;
          }
        }
      }

      public void mouseReleased(MouseEvent e) {}

      public void mouseEntered(MouseEvent e) {}

      public void mouseExited(MouseEvent e) {}

      /**
       * Change the size when the mouse is dragged
       */
      public void mouseDragged(MouseEvent e) {
        if (e.isMetaDown() && gridwidth > 50) {
          if (mouse_pos_y < e.getY()) {
            Dimension d = getPreferredSize();
            setPreferredSize(new Dimension( (int) (d.width * 1.03),
                                           (int) (d.height * 1.03)));
            setMinimumSize(new Dimension( (int) (d.width * 1.03),
                                         (int) (d.height * 1.03)));
            d = hp.getPreferredSize();
            hp.setPreferredSize(new Dimension(d.width,
                                              (int) (d.height * 1.03)));
            hp.setMinimumSize(new Dimension(d.width,
                                            (int) (d.height * 1.03)));
          }
          else {
            Dimension d = getPreferredSize();
            setPreferredSize(new Dimension( (int) (d.width * .97),
                                           (int) (d.height * .97)));
            setMinimumSize(new Dimension( (int) (d.width * .97),
                                         (int) (d.height * .97)));
            d = hp.getPreferredSize();
            hp.setPreferredSize(new Dimension(d.width,
                                              (int) (d.height * .97)));
            hp.setMinimumSize(new Dimension(d.width,
                                            (int) (d.height * .97)));
          }
          revalidate();
          hp.revalidate();
        }
        hp.repaint(); ;
      }

      /**
       * Update the brushing info when the mouse is moved
       */
      public void mouseMoved(MouseEvent e) {
        int cx = e.getX(), cy = e.getY();
        //int xpos = padding, ypos = padding;
        int xpos = (int) grid2;
        int ypos = (int) grid2;

        if (cx < xpos || cy < ypos) {
          return;
        }
        else {
          for (int i = 0; i < attribute_names.length; i++) {
            int bins_in_row = row_data[i].length;
            for (int j = 0; j < bins_in_row; j++) {
              if (j < bins_in_row) {
                if (cx > xpos &&
                    cx < xpos + grid6 &&
                    cy > ypos && cy < ypos + grid6) {
                  ma.update(i, j);
                  return;
                }
              }
              xpos += gridwidth;
            }
            xpos = (int) grid2;
            ypos += gridheight;
          }
        }
      }
    }

    /**
       Show the composite and a legend
     */
    private final class CompositePanel
        extends JPanel {
      CompositePanel() {
        setBackground(yellowish);
      }

      float sq05;
      float sq1;
      float sq8;
      float sq7;

      public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        // rendering
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        NaiveBayesPieChartData ev_data = model.getCurrentEvidence();
        legend.updateLegend(ev_data);
        if (miPieChart.getState()) {
          drawPieChart(g2, 0, (int) sq05, ev_data);
        }
        else {
          drawBarChart(g2, 0, (int) sq05, ev_data);

        }
        g2.setFont(labelFont);
        g2.setPaint(messageColor);
        FontMetrics fm = scaleFont(g2, model.getClassColumn(),
                                   getWidth(), (int) sq1);
        g2.drawString(model.getClassColumn(),
                      getWidth() / 2 -
                      fm.stringWidth(model.getClassColumn()) / 2,
                      sq1);
      }

      /**
       * Draw the composite as a pie chart
       */
      private final void drawPieChart(Graphics2D g2, int x, int y,
                                      NaiveBayesPieChartData data) {

        //System.out.println("DRAW.");
        // draw chart
        int angle = 0;
        for (int count = class_names.length - 1; count >= 0; count--) {
          g2.setPaint(getColor(data.getClassName(count)));
          double ratio = data.getClass(data.getClassName(count));
          //System.out.println(data.getClassName(count)+" "+ratio);

          if (count == class_names.length - 1) {
            g2.fill(new Arc2D.Double(x + (int) (sq1), y + (int) (sq1),
                                     (int) (sq8), (int) (sq8), angle,
                                     (int) (360 - angle), Arc2D.PIE));
          }
          else {
            g2.fill(new Arc2D.Double(x + (int) (sq1), y + (int) (sq1),
                                     (int) (sq8), (int) (sq8), angle,
                                     (int) (360 * ratio), Arc2D.PIE));

          }
          angle += (int) (360 * ratio);
        }
      }

      /**
       * Draw the composite as a bar chart
       */
      private final void drawBarChart(Graphics2D g2, int x, int y,
                                      NaiveBayesPieChartData data) {

        g2.setPaint(Color.black);
        // draw the axes
        g2.draw(new Line2D.Double(x + (int) (sq1),
                                  y + (int) (sq8), x + (int) (sq8),
                                  y + (int) (sq8)));
        g2.draw(new Line2D.Double(x + (int) (sq1),
                                  y + (int) (sq1), x + (int) (sq1),
                                  y + (int) (sq8)));

        float barWidth = sq7 / (float) class_names.length;
        double startX = x + (int) (sq1) + 1;

        // draw the bars
        // start at the left and move right
        for (int count = class_names.length - 1; count >= 0; count--) {
          //for(int count = data.getNumRows()-1; count >= 0; count--) {
          g2.setColor(getColor(data.getClassName(count)));
          double ratio = data.getClass(class_names[count]);

          g2.fill(new Rectangle2D.Double(startX,
                                         (y + sq8) - (sq7 * ratio),
                                         barWidth,
                                         sq7 * ratio));
          startX += barWidth;
        }
      }

      public void setBounds(int x, int y, int w, int h) {
        sq1 = (float) (.1 * w);
        sq8 = (float) (.8 * w);
        sq7 = (float) (.7 * w);
        sq05 = (float) (.05 * w);
        super.setBounds(x, y, w, h);
      }
    }

    /**
       Show the attribute names
     */
    private final class HeaderPanel
        extends JPanel {
      NumberFormat nfmt;
      HeaderPanel() {
        setBackground(darkBg);
        nfmt = NumberFormat.getInstance();
        nfmt.setMaximumFractionDigits(2);
      }

      public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        // rendering
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setPaint(headerLabelColor);
        g2.setFont(labelFont);
        int xl = 0;
        FontMetrics fm = scaleFont(g2,
                                   longest_attribute_name, getWidth(),
                                   gridheight);
        int yl = 2 * fm.getAscent();
        int sw;
        int wid = getWidth();
        for (int j = 0; j < attribute_names.length; j++) {
          sw = fm.stringWidth(attribute_names[j]);
          g2.drawString(attribute_names[j],
                        wid / 2 - sw / 2, yl);
          // draw the predictor value
          if (miShowPredVal.getState()) {
            StringBuffer s =
                new StringBuffer(nfmt.format(predictor_values[j]));
            s.append(ERR);
            String s2 = s.toString();
            g2.drawString(s2, wid / 2 - fm.stringWidth(s2) / 2,
                          yl + fm.getHeight() + 1);
          }
          yl += gridheight;
        }
      }
    }

    /**
     * Keep track of the colors.
     */
    private final class ColorMenuItem
        extends JMenuItem {
      ColorMenuItem(String s) {
        super(s);
      }
    }

    /**
     * Keep track of the attributes to show
     */
    private final class AttributeMenuItem
        extends JCheckBoxMenuItem {
      AttributeMenuItem(String s) {
        super(s, true);
      }
    }

    /**
     * Show the colors for each class name and its percentage of
     * the composite
     */
    private final class Legend
        extends JPanel {
      Legend() {
        setLayout(new GridBagLayout());

        color_comps = new ColorComponent[class_names.length];
        class_labels = new JLabel[class_names.length];
        JLabel leg = new AALabel("LEGEND");
        leg.setBackground(yellowish);
        Constrain.setConstraints(this, leg, 1, 0, 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.NORTH, 1.0, 0.0,
                                 new Insets(2, 4, 2, 0));

        Insets ii = new Insets(4, 8, 4, 0);
        Insets i2 = new Insets(4, 8, 4, 0);
        for (int i = 0; i < class_names.length; i++) {
          Color c = getColor(class_names[i]);
          color_comps[i] = new ColorComponent(c);
          Constrain.setConstraints(this, color_comps[i], 0, i + 1, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.NORTH, 0.0, 0.0, ii);
          class_labels[i] = new AALabel(class_names[i]);
          class_labels[i].setBackground(yellowish);
          class_labels[i].setFont(labelFont);
          class_labels[i].setForeground(messageColor);
          Constrain.setConstraints(this, class_labels[i], 1, i + 1, 1, 1,
                                   GridBagConstraints.HORIZONTAL,
                                   GridBagConstraints.NORTH, 1.0, 0.0, i2);
        }
        setBackground(yellowish);
        updateLegend(null);
      }

      /**
       * Update all the items in the legend
       */
      private final void updateLegend(NaiveBayesPieChartData ev_data) {
        if (ev_data == null) {
          ev_data = model.getCurrentEvidence();

        }
        try {
          ev_data.sortByColumn(2);
        }
        catch (Exception e) {
        }
        int ct = 0;
        int numRows = ev_data.getNumRows();
        for (int count = numRows - 1; count >= 0; count--) {
          double dr = ev_data.getClass(ev_data.getClassName(count));
          StringBuffer sb = new StringBuffer(ev_data.getClassName(count));
          sb.append(OPEN_PAREN);
          sb.append(percentage(dr));
          sb.append(CLOSE_PAREN);
          color_comps[ct].setBkgrd(getColor(ev_data.getClassName(count)));
          class_labels[ct].setText(sb.toString());
          ct++;
        }
        repaint();
      }
    }

    /**
       Show information on the pie chart that the mouse pointer is over.
       The pie chart represents a bin.
     */
    private final class MessageArea
        extends JTextArea
        implements java.io.Serializable {

      MessageArea(int numRow) {
        super(numRow + 3, 0);
        setBackground(yellowish);
        setForeground(messageColor);
        setEditable(false);
        setFont(labelFont);
      }

      public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        super.paintComponent(g2);
      }

      public void update(int row, int col) {
        setEditable(true);

        StringBuffer sb = new StringBuffer(SPACE);
        sb.append(attribute_names[row]);
        sb.append(EQUALS);
        sb.append(row_data[row][col].getBinName());
        sb.append(NEW_LINE);
        setText(sb.toString());
        NaiveBayesPieChartData data = row_data[row][col];

        sb = new StringBuffer(TOTAL);
        sb.append(data.getTotal());
        sb.append(NEW_LINE);
        append(sb.toString());

        for (int count = class_names.length - 1; count >= 0; count--) {
          int tally = data.getTally(count);
          if (tally > 0) {
            sb = new StringBuffer(SPACE);
            sb.append(data.getClassName(count));
            sb.append(COLON);
            sb.append(tally);
            sb.append(COLON);
            sb.append(percentage(data.getRatio(count)));
            sb.append(NEW_LINE);
            append(sb.toString());
          }
        }
        setCaretPosition(0);
        setEditable(false);
      }
    }

    JLabel[] class_labels;
    ColorComponent[] color_comps;

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
    }

    /**
     * A scroll pane that never changes its size.
     */
    private final class SameSizeSP
        extends JScrollPane {
      Dimension p;
      SameSizeSP(Component view, Dimension pref) {
        super(view);
        p = pref;
      }

      public Dimension getPreferredSize() {
        return p;
      }

      public Dimension getMinimumSize() {
        return p;
      }

      public Dimension getMaximumSize() {
        return p;
      }
    }

    /**
     * An anti aliased label
     */
    private final class AALabel
        extends JLabel {
      AALabel(String s) {
        super(s);
        setBackground(darkBg);
        setOpaque(true);
        setForeground(labelFg);
      }

      AALabel(String s, int i) {
        super(s, i);
      }

      public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        super.paintComponent(g2);
      }
    }

    /**
     * A black border on the left and top edges
     */
    private final class SPBorder
        extends LineBorder {
      SPBorder() {
        super(Color.black);
      }

      public void paintBorder(Component c, Graphics g, int x,
                              int y, int width, int height) {

        Color oldColor = g.getColor();
        g.setColor(lineColor);
        g.drawLine(x, y, x + width, y);
        g.drawLine(x, y, x, y + height);
        g.setColor(oldColor);
      }
    }

    /**
     * A black border on the top edge
     */
    private final class SPBorder2
        extends LineBorder {
      SPBorder2() {
        super(Color.black);
      }

      public void paintBorder(Component c, Graphics g, int x,
                              int y, int width, int height) {

        Color oldColor = g.getColor();
        g.setColor(lineColor);
        g.drawLine(x, y, x + width, y);
        g.setColor(oldColor);
      }
    }
  }

  /**
   * Format the number as a percentage
   */
  private final static String percentage(double doub) {
    doub *= 100;

    NumberFormat nf = NumberFormat.getInstance();
    nf.setMinimumFractionDigits(2);
    nf.setMaximumFractionDigits(2);
    StringBuffer sb = new StringBuffer(nf.format(doub));
    sb.append(PERCENT_SIGN);
    return sb.toString();
  }

  private final class HelpWindow
      extends JD2KFrame {
    HelpWindow() {
      super("About Naive Bayes Vis");
      JEditorPane jep = new JEditorPane("text/html", getHelpString());
      jep.setBackground(yellowish);
      getContentPane().add(new JScrollPane(jep));
      setSize(400, 400);
    }
  }

  private static final String getHelpString() {
/*    StringBuffer sb = new StringBuffer("<html>");
    sb.append("<h2>NaiveBayesVis</h2>");
    sb.append("NaiveBayesVis provides an evidence visualization for a ");
    sb.append("NaiveBayesModel.  Evidence items can be selected by clicking ");
    sb.append(
        "on an item and the conclusion will update its predictions.  The ");
    sb.append("predictions are shown in the conclusion graph and in the ");
    sb.append("Legend.<br><br>");
    sb.append("The evidence can be zoomed or scaled.  To scale the ");
    sb.append("evidence, right-click the mouse and drag toward the northwest ");
    sb.append("or southwest corners.");
    sb.append("<h3>Menu Options</h3>");
    sb.append("<ul><li>Views: Display the evidence and composite as either");
    sb.append(" pie charts or bar charts.");
    sb.append("<li>Sort Attributes By: Sort the attributes by either the");
    sb.append(" best predictor or alphabetical order. The best predictor is");
    sb.append(" the attribute that induces the largest error when omitted.");
    sb.append("<li>Sort Evidence By: Sort the evidence by the number of ");
    sb.append(" items in its category or in alphabetical order.");
    sb.append("<li>Show Attributes: Select which attributes to display.");
    sb.append("<li>Set Colors: Select the colors for the outputs.");
    sb.append("<li>Show Predictor Values: Display the error induced when ");
    sb.append("the attribute was ommited from a prediction calculation.");
    sb.append("<li>Show Bin Weight Percentage: Show the weights assigned to ");
    sb.append("each evidence item as a percentage or display a bar with its ");
    sb.append("weight relative to all other items in its row.");
    sb.append("<li>Print: Print this visualization.");
    sb.append("</ul>");
    sb.append("<h3>Toolbar Buttons</h3>");
    sb.append("<ul><li>Reset View: Reset the evidence to the default size.");
    sb.append("<li>Print: Print this visualization.");
    sb.append("<li>Zoom: When this button is pressed, left-click the ");
    sb.append("evidence to zoom in, or right-click the evidence to zoom out.");
    sb.append("</ul>");
    sb.append("</html>");
    return sb.toString();
     */

   StringBuffer sb = new StringBuffer("<html><h1>Naive Bayes Vis Help</h1>");
sb.append("<p>Overview: Naive Bayes Vis provides an interactive evidence ");
sb.append("visualization for a Naive Bayes Model.");
sb.append("<p>Detailed Description: This evidence visualization shows the data ");
sb.append("the Naive Bayes Model uses to make its predictions.  The window ");
sb.append("is split into two panes.  The left pane contains the Attributes and ");
sb.append("Evidence.  The right side contains the Conclusion.  Evidence items ");
sb.append("can be selected to update the Conclusion.  The Evidence can be scaled ");
sb.append("by right-clicking the mouse and dragging up or down.");
sb.append("<hr> <p>");
sb.append("Attributes and Evidence: The attributes (inputs) used to train the ");
sb.append("Naive Bayes Model are displayed on the far left.  The attributes to ");
sb.append("show and the sorting order can be changed using menu options.  The ");
sb.append("evidence items for an attribute are listed next to the attribute ");
sb.append("name.  These are displayed as pie charts by default.  They can be ");
sb.append("changed to bar charts using a menu option.  Above each pie chart is a ");
sb.append("line showing the relevance for each pie chart.  This is the ratio of ");
sb.append("examples that fall into this bin to the number of total examples. ");
sb.append("The bar can be changed to a percentage using a menu option.  The user ");
sb.append("can select an evidence item by clicking on it to update the conclusion. ");
sb.append("<p>Conclusion: The conclusion shows the probabilities of prediction ");
sb.append("of the outputs given the selected evidence items.  The Legend shows the ");
sb.append("colors of each of the unique outputs and its percentage of the ");
sb.append("Conclusion.  The colors can be changed using a menu option.  The lowest ");
sb.append("portion of the Conclusion shows information about the evidence item ");
sb.append("currently under the mouse cursor.  This shows the name, the number ");
sb.append("of records that fall into this bin, and the breakdown of the items ");
sb.append("by class. ");
sb.append("<hr> ");
sb.append("Menu Options: ");
sb.append("<ul> ");
sb.append("<li>Options: ");
sb.append("	<ul> ");
sb.append("	<li>Views: Toggle the views between pie charts and bar charts. ");
sb.append("	<li>Sort Attributes By: Sort the attributes by either the best  ");
sb.append("	predictor or alphabetical order.  The best predictor is the ");
sb.append("	attribute that induces the largest error when omitted. ");
sb.append("	<li>Show Attributes: Select which attributes to display. ");
sb.append("	<li>Set Colors: Select the colors for each of the unique  ");
sb.append("	outputs. ");
sb.append("	<li>Show Predictor Values: Display the error induced when the ");
sb.append("	attribute was omitted from a prediction calculation.  The ");
sb.append("	attribute with the highest error would be considered to be ");
sb.append("	the best predictor. ");
sb.append("	<li>Show Bin Weight Percentage: Show the weights assigned to ");
sb.append("	each evidence item as a percentage or display a bar with its ");
sb.append("	weight relative to all other items in its row. ");
sb.append("	<li>Print: Print the visualization window. ");
sb.append("     <li>Save as PMML: Saves the model in a PMML format file.");
sb.append("	</ul> ");
sb.append("<li>Help: ");
sb.append("	<ul> ");
sb.append("	<li>Help: Show this help window. ");
sb.append("	</ul> ");
sb.append("</ul> ");
sb.append("<hr> ");
sb.append("Toolbar Options: ");
sb.append("<ul> ");
sb.append("<li>Reset View: Reset the evidence to the default size. ");
sb.append("<li>Print: Print this visualization. ");
sb.append("<li>Zoom: When this button is toggled on, left-click the evidence ");
sb.append("to zoom in, or right-click the evidence to zoom out. ");
sb.append("</ul> ");
sb.append("</html> ");
   return sb.toString();
  }
}
