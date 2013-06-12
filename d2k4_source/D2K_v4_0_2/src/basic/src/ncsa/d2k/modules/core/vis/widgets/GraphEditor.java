package ncsa.d2k.modules.core.vis.widgets;

import ncsa.d2k.modules.core.datatype.table.*;
// import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.gui.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

// edit 2003-03-21 gpape:
//   - only numeric columns are now added to combo boxes of columns
//   - added JOptionPane messages for error conditions (e.g., non-numeric
//     entries in maximum and minimum fields)
//   - changed "Scatter Plot" on tab to simply "Plot"

public class GraphEditor extends JPanel implements ActionListener {
   public static final int SCATTER_PLOT = 0;
   public static final int LINE_GRAPH = 1;
   public static final int LINEAR_REGRESSION = 2;

   Table table;
   Hashtable hashtable;

   /** this is the class object for a class of object other than the
    *  standard ones.*/
   Class graphClass = null;
   int type;

   JLabel namelabel, colorlabel, xlabel, ylabel;
   JTextField namefield;
   ColorPanel colorpanel;
   JComboBox xbox, ybox;
   JButton addbutton;

   JList list;
   DefaultListModel listmodel;
   JScrollPane scrollpane;
   JButton deletebutton;
   JButton refreshbutton;

   JLabel xminimumlabel, xmaximumlabel;
   JLabel yminimumlabel, ymaximumlabel;
   JLabel titlelabel, xaxislabel, yaxislabel;
   JTextField xminimumfield, xmaximumfield;
   JTextField yminimumfield, ymaximumfield;
   JTextField titlefield, xaxisfield, yaxisfield;
   JCheckBox gridcheck, legendcheck;

   JPanel graphpane;

   public GraphEditor(Table table, int type) {
      this.table = table;

      this.type = type;

      // Edit panel
      hashtable = new Hashtable();
      Vector columnlabels = new Vector();

      int size = table.getNumColumns();
      int columnindex = 0;
      int boxindex = 0;

      while (columnindex < size) {

         if (table.isColumnNumeric(columnindex)) {
            hashtable.put(new Integer(boxindex), new Integer(columnindex));
            String columnlabel = table.getColumnLabel(columnindex);
            columnlabels.add(columnlabel);
            boxindex++;
         }

         columnindex++;
      }

      namelabel = new JLabel("Name: ");
      colorlabel = new JLabel("Color: ");
      xlabel = new JLabel("X Variable: ");
      ylabel = new JLabel("Y Variable: ");

      namefield = new JTextField();

      colorpanel = new ColorPanel();

      xbox = new JComboBox(columnlabels);
      ybox = new JComboBox(columnlabels);

      addbutton = new JButton("Add");
      addbutton.addActionListener(this);

      JPanel editpanel = new JPanel();
      editpanel.setBorder(new TitledBorder("Edit: "));
      editpanel.setLayout(new GridBagLayout());
      Constrain.setConstraints(editpanel, namelabel, 0, 0, 1, 1, GridBagConstraints.NONE,
         GridBagConstraints.WEST, 0, 0);
      Constrain.setConstraints(editpanel, namefield, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL,
         GridBagConstraints.WEST, 0, 0);
      Constrain.setConstraints(editpanel, colorlabel, 0, 1, 1, 1, GridBagConstraints.NONE,
         GridBagConstraints.WEST, 0, 0);
      Constrain.setConstraints(editpanel, colorpanel, 1, 1, 1, 1, GridBagConstraints.NONE,
         GridBagConstraints.WEST, 0, 0);
      Constrain.setConstraints(editpanel, xlabel, 0, 2, 1, 1, GridBagConstraints.NONE,
         GridBagConstraints.WEST, 0, 0);
      Constrain.setConstraints(editpanel, xbox, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL,
         GridBagConstraints.WEST, 0, 0);
      Constrain.setConstraints(editpanel, ylabel, 0, 3, 1, 1, GridBagConstraints.NONE,
         GridBagConstraints.WEST, 0, 0);
      Constrain.setConstraints(editpanel, ybox, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL,
         GridBagConstraints.WEST, 0, 0);
      Constrain.setConstraints(editpanel, addbutton, 1, 4, 1, 1, GridBagConstraints.NONE,
         GridBagConstraints.EAST, 1, 1);

      // List panel
      list = new JList(new DefaultListModel());
      list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      listmodel = (DefaultListModel) list.getModel();

      deletebutton = new JButton("Delete");
      deletebutton.addActionListener(this);

      scrollpane = new JScrollPane(list, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
      ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

      JPanel listpanel = new JPanel();
      listpanel.setBorder(new TitledBorder("List: "));
      listpanel.setLayout(new GridBagLayout());
      Constrain.setConstraints(listpanel, scrollpane, 0, 0, 1, 1, GridBagConstraints.BOTH,
         GridBagConstraints.WEST, 1, .5);
      Constrain.setConstraints(listpanel, deletebutton, 0, 1, 1, 1, GridBagConstraints.NONE,
         GridBagConstraints.NORTHEAST, 0, .5);

      // Editlist panel
      JPanel editlistpanel = new JPanel();
      editlistpanel.setLayout(new GridBagLayout());
      Constrain.setConstraints(editlistpanel, editpanel, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL,
         GridBagConstraints.NORTHWEST, 0, 0);
      Constrain.setConstraints(editlistpanel, listpanel, 0, 1, 1, 1, GridBagConstraints.BOTH,
         GridBagConstraints.WEST, 1, 1);

      // Settings panel
      xminimumlabel = new JLabel("X Minimum: ");
      xmaximumlabel = new JLabel("X Maximum: ");
      yminimumlabel = new JLabel("Y Minimum: ");
      ymaximumlabel = new JLabel("Y Maximum: ");

      titlelabel = new JLabel("Title: ");
      xaxislabel = new JLabel("X Axis: ");
      yaxislabel = new JLabel("Y Axis: ");

      xminimumfield = new JTextField();
      xmaximumfield = new JTextField();
      yminimumfield = new JTextField();
      ymaximumfield  = new JTextField();

      titlefield = new JTextField();
      xaxisfield = new JTextField();
      yaxisfield = new JTextField();

      gridcheck = new JCheckBox("Grid", true);
      legendcheck = new JCheckBox("Legend", true);

      JPanel settingspanel = new JPanel();
      settingspanel.setBorder(new TitledBorder("Properties: "));
      settingspanel.setLayout(new GridBagLayout());
      Constrain.setConstraints(settingspanel, xminimumlabel, 0, 0, 1, 1, GridBagConstraints.NONE,
         GridBagConstraints.WEST, 0, 0);
      Constrain.setConstraints(settingspanel, xminimumfield, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL,
         GridBagConstraints.WEST, 0, 0);
      Constrain.setConstraints(settingspanel, xmaximumlabel, 2, 0, 1, 1, GridBagConstraints.NONE,
         GridBagConstraints.WEST, 0, 0);
      Constrain.setConstraints(settingspanel, xmaximumfield, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL,
         GridBagConstraints.WEST, 0, 0);

      Constrain.setConstraints(settingspanel, yminimumlabel, 0, 1, 1, 1, GridBagConstraints.NONE,
         GridBagConstraints.WEST, 0, 0);
      Constrain.setConstraints(settingspanel, yminimumfield, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL,
         GridBagConstraints.WEST, 1, 0);
      Constrain.setConstraints(settingspanel, ymaximumlabel, 2, 1, 1, 1, GridBagConstraints.NONE,
         GridBagConstraints.WEST, 0, 0);
      Constrain.setConstraints(settingspanel, ymaximumfield, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL,
         GridBagConstraints.WEST, 1, 0);

      Constrain.setConstraints(settingspanel, titlelabel, 0, 2, 1, 1, GridBagConstraints.NONE,
         GridBagConstraints.WEST, 0, 0);
      Constrain.setConstraints(settingspanel, titlefield, 1, 2, 3, 1, GridBagConstraints.HORIZONTAL,
         GridBagConstraints.WEST, 0, 0);
      Constrain.setConstraints(settingspanel, xaxislabel, 0, 3, 1, 1, GridBagConstraints.NONE,
         GridBagConstraints.WEST, 0, 0);
      Constrain.setConstraints(settingspanel, xaxisfield, 1, 3, 3, 1, GridBagConstraints.HORIZONTAL,
         GridBagConstraints.WEST, 0, 0);
      Constrain.setConstraints(settingspanel, yaxislabel, 0, 4, 1, 1, GridBagConstraints.NONE,
         GridBagConstraints.WEST, 0, 0);
      Constrain.setConstraints(settingspanel, yaxisfield, 1, 4, 3, 1, GridBagConstraints.HORIZONTAL,
         GridBagConstraints.WEST, 0, 0);
      Constrain.setConstraints(settingspanel, gridcheck, 0, 5, 1, 1, GridBagConstraints.NONE,
         GridBagConstraints.NORTHWEST, 0, 0);
      Constrain.setConstraints(settingspanel, legendcheck, 0, 6, 1, 1, GridBagConstraints.NONE,
         GridBagConstraints.NORTHWEST, 0, 1);

      // Tabbed pane
      JTabbedPane tabbedpane = new JTabbedPane(SwingConstants.TOP);
      tabbedpane.add("Plot", editlistpanel);
      tabbedpane.add("Settings",settingspanel);

      refreshbutton = new JButton("Refresh");
      refreshbutton.addActionListener(this);

      // Tabrefresh panel
      JPanel tabrefreshpanel = new JPanel();
      tabrefreshpanel.setMinimumSize(new Dimension(0, 0));
      tabrefreshpanel.setLayout(new GridBagLayout());
      Constrain.setConstraints(tabrefreshpanel, tabbedpane, 0, 0, 1, 1, GridBagConstraints.BOTH,
         GridBagConstraints.NORTHWEST, 1, 1);
      Constrain.setConstraints(tabrefreshpanel, refreshbutton, 0, 1, 1, 1, GridBagConstraints.NONE,
         GridBagConstraints.NORTHWEST, 0, 0);

      // Scatter pane
      graphpane = new JPanel();
      graphpane.setPreferredSize(new Dimension(640, 480));
      graphpane.setLayout(new GridBagLayout());

      // Split pane
      JSplitPane splitpane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tabrefreshpanel, graphpane);
      splitpane.setOneTouchExpandable(true);

      // Layout
      setLayout(new GridBagLayout());
      Constrain.setConstraints(this, splitpane, 0, 0, 1, 1, GridBagConstraints.BOTH,
         GridBagConstraints.NORTHWEST, 1, 1);
   }

   /**
    * This constructor is called when a custom graph class is to be used.
    */
   public GraphEditor(Table table, Class graphCls) {
      this (table, -1);
      this.graphClass = graphCls;
   }

   public void actionPerformed(ActionEvent event) {
      Object source = event.getSource();
      if (source == addbutton) {
         String name = namefield.getText();
         if (name.equals("")) {
            JOptionPane.showMessageDialog(this,
               "Please specify a name for this set in the \"name\" field.",
               "Error", JOptionPane.ERROR_MESSAGE);
            return;
         }

         Color color = colorpanel.getColor();

         int index = xbox.getSelectedIndex();
         Integer x = (Integer) hashtable.get(new Integer(index));
         String columnlabel = table.getColumnLabel(x.intValue());
         name = name + " " + columnlabel;

         index = ybox.getSelectedIndex();
         Integer y = (Integer) hashtable.get(new Integer(index));
         columnlabel = table.getColumnLabel(y.intValue());
         name = name + ", " + columnlabel;

         listmodel.addElement(new DataSet(name, color, x.intValue(), y.intValue()));

         namefield.setText("");
      }
      else if (source == deletebutton) {
         int index = list.getSelectedIndex();
         if (index != -1)
            listmodel.removeElementAt(index);
      }
      else if (source == refreshbutton) {
         int size = listmodel.getSize();
         if (size == 0) {
            graphpane.removeAll();
            graphpane.validate();
            graphpane.repaint();
            return;
         }

         // Scatterplot data
         DataSet[] set = new DataSet[size];
         for (int index=0; index < size; index++)
            set[index] = (DataSet) listmodel.getElementAt(index);

         // Property data
         String title = titlefield.getText();
         String xaxis = xaxisfield.getText();
         String yaxis = yaxisfield.getText();

         Integer xminimum = null;
         Double xdataminimum = null;
         String value = xminimumfield.getText();

         // detect error condition on x minimum
         if (value.length() > 0) {
            try {
               Double.parseDouble(value);
            }
            catch(NumberFormatException ex) {
               JOptionPane.showMessageDialog(this,
                  "Invalid entry in x-minimum field.", "Error",
                  JOptionPane.ERROR_MESSAGE);
               return;
            }
         }

         if (value.equals(""))
            xminimum = null;
         else
            try {
               /** if we get a number format error, it's not an int. */
               xminimum = new Integer(value);
            } catch (Exception err) {
               xdataminimum = new Double (value);
            }

         Integer xmaximum = null;
         Double xdatamaximum = null;
         value = xmaximumfield.getText();

         // detect error condition on x maximum
         if (value.length() > 0) {
            try {
               Double.parseDouble(value);
            }
            catch(NumberFormatException ex) {
               JOptionPane.showMessageDialog(this,
                  "Invalid entry in x-maximum field.", "Error",
                  JOptionPane.ERROR_MESSAGE);
               return;
            }
         }

         if (value.equals(""))
            xmaximum = null;
         else
            try {
               /** if we get a number format error, it's not an int. */
               xmaximum = new Integer(value);
            } catch (Exception err) {
               xdatamaximum = new Double (value);
               if (xminimum != null) {
                  xdataminimum = new Double (xminimum.doubleValue ());
                  xminimum = null;
               }
            }

         Integer yminimum = null;
         Double ydataminimum = null;
         value = yminimumfield.getText();

         // detect error condition on y minimum
         if (value.length() > 0) {
            try {
               Double.parseDouble(value);
            }
            catch(NumberFormatException ex) {
               JOptionPane.showMessageDialog(this,
                  "Invalid entry in y-minimum field.", "Error",
                  JOptionPane.ERROR_MESSAGE);
               return;
            }
         }

         if (value.equals(""))
            yminimum = null;
         else
            try {
               /** if we get a number format error, it's not an int. */
               yminimum = new Integer(value);
            } catch (Exception err) {
               ydataminimum = new Double (value);
            }


         Integer ymaximum = null;
         Double ydatamaximum = null;
         value = ymaximumfield.getText();

         // detect error condition on y maximum
         if (value.length() > 0) {
            try {
               Double.parseDouble(value);
            }
            catch(NumberFormatException ex) {
               JOptionPane.showMessageDialog(this,
                  "Invalid entry in y-maximum field.", "Error",
                  JOptionPane.ERROR_MESSAGE);
               return;
            }
         }

         if (value.equals(""))
            ymaximum = null;
         else
            try {
               /** if we get a number format error, it's not an int. */
               ymaximum = new Integer(value);
            } catch (Exception err) {
               ydatamaximum = new Double (value);
               if (yminimum != null) {
                  ydataminimum = new Double (yminimum.doubleValue ());
                  yminimum = null;
               }
            }

         boolean displaygrid = gridcheck.isSelected();
         boolean displaylegend = legendcheck.isSelected();

         GraphSettings settings = new GraphSettings();
         settings.title = title;
         settings.xaxis = xaxis;
         settings.yaxis = yaxis;
         settings.xminimum = xminimum;
         settings.xmaximum = xmaximum;
         settings.xdataminimum = xdataminimum;
         settings.xdatamaximum = xdatamaximum;
         settings.yminimum = yminimum;
         settings.ymaximum = ymaximum;
         settings.ydataminimum = ydataminimum;
         settings.ydatamaximum = ydatamaximum;
         settings.displaygrid = displaygrid;
         settings.displaylegend = displaylegend;

         graphpane.removeAll();

         Graph graph = null;
         if (graphClass != null) {
            try {
               graph = (Graph) graphClass.newInstance ();
            } catch (IllegalAccessException iae) {
               iae.printStackTrace ();
            } catch (InstantiationException ie) {
               ie.printStackTrace ();
            }
            graph.init (table, set, settings);
         }
         else if (type == SCATTER_PLOT) {
            graph = new ScatterPlot(table, set, settings);
         }
         else if (type == LINE_GRAPH) {
            graph = new LineGraph(table, set, settings);
         }
         else if (type == LINEAR_REGRESSION) {
            graph = new LinearRegression(table, set, settings);
         }

         Constrain.setConstraints(graphpane, graph, 0, 0, 1, 1, GridBagConstraints.BOTH,
            GridBagConstraints.NORTHWEST, 1, 1);
         graphpane.validate();
         graphpane.repaint();

      }
   }

   public class ColorPanel extends JPanel implements ActionListener {
      JPanel renderpanel;
      JButton editorbutton;
      Color color;

      public ColorPanel() {
         editorbutton = new JButton("Edit");
         editorbutton.addActionListener(this);
         color = Color.black;

         renderpanel = new JPanel();
         renderpanel.setBackground(color);
         renderpanel.setSize(new Dimension(10,10));

         add(renderpanel);
         add(editorbutton);
      }

      public void actionPerformed(ActionEvent event) {
         Color prevColor = color;
         color = JColorChooser.showDialog(this, "Edit Color", Color.black);
         if (color == null)
            color = prevColor;
         renderpanel.setBackground(color);
      }

      public Color getColor() {
         return color;
      }

      public void setColor(Color color) {
         this.color = color;
      }
   }
}
