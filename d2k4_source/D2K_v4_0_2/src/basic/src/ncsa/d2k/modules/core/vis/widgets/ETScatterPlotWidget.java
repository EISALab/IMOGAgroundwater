package ncsa.d2k.modules.core.vis.widgets;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import ncsa.d2k.userviews.swing.*;
import ncsa.d2k.gui.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;

/**
   Shows all the Graphs in a JTable
 */
public class ETScatterPlotWidget extends JUserPane implements
    Serializable, MouseListener, ActionListener {

  static int ROW_HEIGHT = 150;
  static int ROW_WIDTH = 150;

  ExampleTable et;
  JButton showComposite;
  JButton clearSelected;
  int[] inputs;
  int[] outputs;
  String[] rowheaders;
  HashMap outputsmap;
  GraphTable jTable = null;
  boolean[][] selected = null;
  JMenuBar menuBar;
  JMenuItem helpItem;
  JFrame helpWindow;

  public ETScatterPlotWidget() {
  }

  public ETScatterPlotWidget(ExampleTable table) {
    initView(null);
    setInput(table, 0);
  }

  public void initView(ViewModule m) {
    menuBar = new JMenuBar();
    JMenu m1 = new JMenu("Help");
    helpItem = new JMenuItem(getMenuDescription());
    helpItem.addActionListener(this);
    m1.add(helpItem);
    menuBar.add(m1);
    helpWindow = getHelpWindow();
  }

  public Object getMenu() {
    return menuBar;
  }

  public void setInput(Object o, int i) {
    if (i==0) {
      et = (ExampleTable) o;
      setup();
    }
  }

  void setup() {
    this.removeAll();

    if (rowheaders != null) {
      int[] features = new int[et.getNumOutputFeatures()];

      for (int index=0; index < rowheaders.length; index++)
        features[index] = ((Integer) (outputsmap.get(rowheaders[index]))).intValue();

      et.setOutputFeatures(features);
    }
    else {
      outputsmap = new HashMap(et.getNumColumns());
      for (int index=0; index < et.getNumColumns(); index++) {
        String featurename = et.getColumnLabel(index);
        outputsmap.put(featurename, new Integer(index));
      }
    }

    int[] tempinputs = et.getInputFeatures();
    int[] tempoutputs = et.getOutputFeatures();

    LinkedList list = new LinkedList();
    // get the numeric output features
    for (int i = 0; i<tempoutputs.length; i++) {
      //if(et.getColumn(tempoutputs[i]) instanceof NumericColumn)
      if (et.isColumnNumeric(tempoutputs[i]))
        list.add(new Integer(tempoutputs[i]));
    }

    outputs = new int[list.size()];
    Iterator iter = list.iterator();
    int idx = 0;
    while (iter.hasNext()) {
      outputs[idx] = ((Integer) iter.next()).intValue();
      idx++;
    }

    list.clear();

    // get the numeric input features
    for (int i = 0; i<tempinputs.length; i++) {
      //if(et.getColumn(tempinputs[i]) instanceof NumericColumn)
      if (et.isColumnNumeric(tempinputs[i]))
        list.add(new Integer(tempinputs[i]));
    }

    inputs = new int[list.size()];
    iter = list.iterator();
    idx = 0;
    while (iter.hasNext()) {
      inputs[idx] = ((Integer) iter.next()).intValue();
      idx++;
    }
    list.clear();
    list = null;

    /*for(int i = 0; i < inputs.length; i++)
       System.out.println("in: "+inputs[i]);
              for(int i = 0; i <  outputs.length; i++)
       System.out.println("out: "+outputs[i]);
     */

    // setup the JTable

    // setup the columns for the matrix
    TableColumnModel cm = new DefaultTableColumnModel() {
      boolean first = true;
      public void addColumn(TableColumn tc) {
        if (first) {
          first = false;
          return;
        }
        tc.setMinWidth(ROW_WIDTH);
        super.addColumn(tc);
      }
    };

    // setup the columns for the row header table
    TableColumnModel rowHeaderModel = new DefaultTableColumnModel() {
      boolean first = true;
      public void addColumn(TableColumn tc) {
        if (first) {
          super.addColumn(tc);
          first = false;
        }
      }
    };

    ColumnPlotTableModel tblModel = new ColumnPlotTableModel();

    ArrayList scalars = new ArrayList();
    for (int index=0; index < et.getNumColumns(); index++) {
      if (et.isColumnScalar(index))
        scalars.add(new String(et.getColumnLabel(index)));
    }

    Object[] features = scalars.toArray();

    JComboBox combobox = new JComboBox(features);

    // setup the row header table
    JTable headerColumn = new JTable(tblModel, rowHeaderModel);
    headerColumn.setRowHeight(ROW_HEIGHT);
    headerColumn.setRowSelectionAllowed(false);
    headerColumn.setColumnSelectionAllowed(false);
    headerColumn.setCellSelectionEnabled(false);
    headerColumn.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    headerColumn.getTableHeader().setReorderingAllowed(false);
    headerColumn.createDefaultColumnsFromModel();
    headerColumn.setDefaultEditor(new String().getClass(), new DefaultCellEditor(combobox));

    // setup the graph matrix
    jTable = new GraphTable(tblModel, cm);
    jTable.createDefaultColumnsFromModel();
    jTable.setDefaultRenderer(ImageIcon.class, new GraphRenderer());
    jTable.setRowHeight(ROW_HEIGHT);
    jTable.setRowSelectionAllowed(false);
    jTable.setColumnSelectionAllowed(false);
    jTable.setCellSelectionEnabled(false);
    jTable.addMouseListener(this);
    jTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    jTable.getTableHeader().setReorderingAllowed(false);
    jTable.getTableHeader().setResizingAllowed(false);

    int numRows = jTable.getModel().getRowCount();
    int numColumns = jTable.getModel().getColumnCount();

    int longest = 0;
    // we know that the first column will only contain
    // JLabels...so create them and find the longest
    // preferred width
    JLabel tempLabel = new JLabel();
    for (int i = 0; i<numRows; i++) {
      tempLabel.setText(
          jTable.getModel().getValueAt(i, 0).toString());
      if (tempLabel.getPreferredSize().getWidth()>longest)
        longest = (int) tempLabel.getPreferredSize().getWidth();
      tempLabel.setText("");
    }

    TableColumn column;
    // set the default column widths
    for (int i = 0; i<numColumns; i++) {
      if (i==0) {
        column = headerColumn.getColumnModel().getColumn(i);
        column.setPreferredWidth(longest+4);
      }
      else {
        column = jTable.getColumnModel().getColumn(i-1);
        column.setPreferredWidth(ROW_WIDTH);
      }
    }

    // make the preferred view show four or less graphs
    int prefWidth;
    int prefHeight;
    if (numColumns<4)
      prefWidth = (numColumns-1)*ROW_WIDTH;
    else
      prefWidth = (4)*ROW_WIDTH;
    if (numRows<4)
      prefHeight = numRows*ROW_HEIGHT;
    else
      prefHeight = 4*ROW_HEIGHT;
    jTable.setPreferredScrollableViewportSize(new Dimension(
        prefWidth, prefHeight));

    // put the row headers in the viewport
    JViewport jv = new JViewport();
    jv.setView(headerColumn);
    jv.setPreferredSize(headerColumn.getPreferredSize());

    // setup the scroll pane
    JScrollPane sp = new JScrollPane(jTable);
    sp.setRowHeader(jv);
    sp.setHorizontalScrollBarPolicy(
        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    sp.setVerticalScrollBarPolicy(
        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

    this.add(sp, BorderLayout.CENTER);

    // add the buttons
    JPanel buttonPanel = new JPanel();
    showComposite = new JButton("Show Composite");
    showComposite.addActionListener(this);
    buttonPanel.add(showComposite);
    clearSelected = new JButton("Clear Selected Graphs");
    clearSelected.addActionListener(this);
    buttonPanel.add(clearSelected);
    this.add(buttonPanel, BorderLayout.SOUTH);
  }

  /**
     When the mouse is pressed, update the selected item.
   */
  public void mousePressed(MouseEvent e) {
    int iRow = e.getY()/jTable.getRowHeight();
    int iCol = jTable.getTableHeader().columnAtPoint(new Point(
        e.getX(), e.getY()));
    selected[iRow][iCol] = !selected[iRow][iCol];
    jTable.repaint();
  }

  public void mouseClicked(MouseEvent e) {}

  public void mouseReleased(MouseEvent e) {}

  public void mouseEntered(MouseEvent e) {}

  public void mouseExited(MouseEvent e) {}

  /**
     Listen for when the buttons are pressed
   */
  public void actionPerformed(ActionEvent e) {
    // Create a graph made up of all the selected items.
    if (e.getSource()==showComposite) {
      // get all the selected graphs
      // and make a list of them
      LinkedList ll = new LinkedList();
      for (int i = 0; i<outputs.length; i++) {
        for (int j = 0; j<inputs.length; j++) {
          if (selected[i][j]) {
            StringBuffer sb = new StringBuffer(
                et.getColumnLabel(outputs[i]));
            sb.append(",");
            sb.append(et.getColumnLabel(inputs[j]));
            // use a random color for each DataSet
            DataSet ds = new DataSet(sb.toString(),
                                     getRandomColor(), inputs[j], outputs[i]);
            ll.add(ds);
          }
        }
      }

      if (ll.size()==0) {
        JOptionPane.showMessageDialog(this,
                                      "Please select one or more plots to composite.",
                                      "Error", JOptionPane.ERROR_MESSAGE);
        return;
      }

      DataSet[] data = new DataSet[ll.size()];
      Iterator iter = ll.iterator();
      int idx = 0;
      while (iter.hasNext()) {
        data[idx] = (DataSet) iter.next();
        idx++;
      }
      GraphSettings settings = new GraphSettings();
      settings.title = "Composite";
      settings.xaxis = "";
      settings.yaxis = "";
      settings.displayaxislabels = false;
      settings.displaylegend = true;
      settings.displaytitle = false;

      if (data.length>0) {
        Graph graph = createGraph(et, data, settings);
        JD2KFrame frame = new JD2KFrame("Composite Graph");
        frame.getContentPane().add(graph);
        frame.addWindowListener(new WindowAdapter() {
          public void windowClosing(WindowEvent e) {
            ((Frame) e.getSource()).setVisible(false);
            ((Frame) e.getSource()).dispose();
          }
        });
        frame.setSize(400, 350);
        frame.show();
      }
    }
    // clear all selected items
    else if (e.getSource()==clearSelected) {
      for (int i = 0; i<outputs.length; i++)
        for (int j = 0; j<inputs.length; j++)
          selected[i][j] = false;
      jTable.repaint();
    }
    else if (e.getSource()==helpItem)
      helpWindow.setVisible(true);
  }

  /**
     We handle the selection of cells in the JTable on our own.
     Cells that have been selected are marked true in the selected[][]
     matrix.
   */
  class GraphTable extends JTable {
    public GraphTable(TableModel tm, TableColumnModel tcm) {
      super(tm, tcm);
    }

    /**
       Same as the superclass' implementation, except this Table
       keeps track of the selected cells on its own.
     */
    public Component prepareRenderer(TableCellRenderer renderer,
                                     int row, int column) {

      Object value = getValueAt(row, column);
      boolean isSelected = selected[row][column];
      boolean rowIsAnchor =
          (selectionModel.getAnchorSelectionIndex()==row);
      boolean colIsAnchor =
          (columnModel.getSelectionModel().getAnchorSelectionIndex()==column);
      boolean hasFocus = (rowIsAnchor&&colIsAnchor)&&hasFocus();

      return renderer.getTableCellRendererComponent(this, value,
          isSelected, hasFocus,
          row, column);
    }
  }

  /**
     A custom cell renderer that shows an ImageIcon.  A blue border is
     painted around the selected items.
   */
  class GraphRenderer extends JLabel implements TableCellRenderer {

    Border unselectedBorder = null;
    Border selectedBorder = null;

    public GraphRenderer() {
      super();
      setOpaque(true);
    }

    /**
       Set the icon and paint the border for this cell.
     */
    public Component getTableCellRendererComponent(JTable table,
        Object value, boolean isSelected, boolean hasFocus, int row,
        int column) {
      this.setIcon((ImageIcon) value);
      if (true) {
        if (isSelected) {
          if (selectedBorder==null) {
            selectedBorder = BorderFactory.createMatteBorder(2,
                2, 2, 2, Color.blue);
          }
          setBorder(selectedBorder);
        }
        else {
          if (unselectedBorder==null) {
            unselectedBorder =
                BorderFactory.createMatteBorder(2, 2, 2, 2,
                                                table.getBackground());
          }
          setBorder(unselectedBorder);
        }
      }
      return this;
    }
  }

  /**
     The table's data model.  Keeps a matrix of images that are
     displayed in the table.  The images are created from the
     Graphs.
   */
  class ColumnPlotTableModel extends AbstractTableModel {
    ImageIcon[][] images;

    ColumnPlotTableModel() {
      rowheaders = et.getOutputNames();
      images = new ImageIcon[outputs.length][inputs.length];
      selected = new boolean[outputs.length][inputs.length];
      GraphSettings settings = new GraphSettings();
      settings.displayaxislabels = false;
      settings.displaylegend = false;
      settings.displaytitle = false;
      Image img;
      // a frame is needed to create an Image
      Frame f = new Frame();
      f.addNotify();

      // create each graph and make an image of it.
      // the image is what is shown in the JTable,
      // because it is more efficient than showing the
      // actual graph
      for (int i = 0; i<outputs.length; i++) {
        for (int j = 0; j<inputs.length; j++) {
          /*StringBuffer sb = new StringBuffer(
             et.getColumnLabel(inputs[j]));
                             sb.append(" vs ");
                             sb.append(et.getColumnLabel(outputs[i]));
           */
          DataSet[] data = new DataSet[1];
          data[0] = new DataSet("",
                                Color.red, inputs[j], outputs[i]);
          settings.xaxis = et.getColumnLabel(inputs[j]);
          settings.yaxis = et.getColumnLabel(outputs[i]);

          Graph graph = createSmallGraph(et, data, settings);
          img = f.createImage(ROW_WIDTH, ROW_HEIGHT);
          Graphics imgG = img.getGraphics();
          graph.setSize(new Dimension(ROW_WIDTH, ROW_HEIGHT));
          graph.paintComponent(imgG);
          images[i][j] = new ImageIcon(img);
        }
      }
      f.dispose();
    }

    /**
       There is one more column than there are input features.
       The first column shows the output variables.
     */
    public int getColumnCount() {
      return inputs.length+1;
    }

    /**
       There are the same number of rows as output features.
     */
    public int getRowCount() {
      return outputs.length;
    }

    public String getColumnName(int col) {
      if (col==0)
        return "";
      else
        return et.getColumnLabel(inputs[col-1]);
    }

    public Object getValueAt(int row, int col) {
      if (col==0)
        return rowheaders[row];
      else
        return images[row][col-1];
    }

    public void setValueAt(Object value, int row, int col) {
      if (col==0) {
        rowheaders[row] = (String) value;
        ETScatterPlotWidget.this.setup();
        ETScatterPlotWidget.this.revalidate();
      }
    }

    /**
       This must be overridden so that our custom cell renderer is
       used.
     */
    public Class getColumnClass(int c) {
      return getValueAt(0, c).getClass();
    }

    public boolean isCellEditable(int row, int col) {
      if (col==0)
        return true;

      return false;
    }
  }

  private final class ETHelpWindow extends JD2KFrame {
    ETHelpWindow(String s) {
      super("About ETScatterPlot");
      JEditorPane jep = new JEditorPane("text/html", s);
      getContentPane().add(new JScrollPane(jep));
      setSize(400, 400);
    }
  }

  protected String getHelpString() {
    StringBuffer sb = new StringBuffer();
    sb.append("<html>");
    sb.append("<body>");
    sb.append("<h2>ETScatterPlot</h2>");
    sb.append("ETScatterPlot displays multiple scatter plots in a grid layout.  ");
    sb.append("This is a small multiple views of data that plot all the chosen input ");
    sb.append("attributes by all the chosen output attributes.  Since each of these ");
    sb.append("grids are a little different, a composite view can be created by ");
    sb.append("highlighting the view you want and clicking the Show Composite ");
    sb.append("button at the bottom of the window.  This will create a new window ");
    sb.append("each time.  The Clear Selected Graphs button will toggle off all ");
    sb.append("the currently highlighted plots.");
    sb.append("</body></html>");
    return sb.toString();
  }

  protected String getMenuDescription() {
    return "About ETScatterPlot...";
  }

  /**
     Create a small graph to be shown in the matrix.
     @param vt the table with the data values
     @param d the DataSets to plot
     @param gs the GraphSettings for this plot
   */
  protected Graph createSmallGraph(Table vt, DataSet[] d,
                                   GraphSettings gs) {
    return new ScatterPlotSmall(vt, d, gs);
  }

  /**
     Create a large graph to be shown in the composite window.
     @param vt the table with the data values
     @param d the DataSets to plot
     @param gs the GraphSettings for this plot
   */
  protected Graph createGraph(Table vt, DataSet[] d,
                              GraphSettings gs) {
    return new ScatterPlot(vt, d, gs);
  }

  protected JFrame getHelpWindow() {
    return new ETHelpWindow(getHelpString());
  }

  Random r = new Random();

  /**
     Get a random Color.  Tries to avoid pure white and pure black.
   */
  protected Color getRandomColor() {
    while (true) {
      int red = r.nextInt()%256;
      int green = r.nextInt()%256;
      int blue = r.nextInt()%256;

      // try to avoid white and black
      if (red>5&&green>5&&blue>5)
        if (red<250&&green<250&&blue<250)
          return new Color(red, green, blue);
    }
  }
}