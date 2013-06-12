package ncsa.d2k.modules.core.transform.binning;

import  java.awt.*;
import  java.awt.event.*;
import  java.text.*;
import  java.util.*;
import  javax.swing.*;
import  javax.swing.event.*;
import  ncsa.d2k.core.modules.*;
import  ncsa.d2k.gui.*;
import  ncsa.d2k.modules.core.vis.widgets.*;
import  ncsa.d2k.modules.core.io.sql.*;
import  ncsa.d2k.userviews.swing.*;
import  ncsa.gui.*;


import ncsa.d2k.modules.core.datatype.table.transformations.*;
import ncsa.d2k.modules.core.datatype.table.*;

import java.sql.*;

import ncsa.d2k.modules.core.transform.StaticMethods;

/**
 * This module does binning for database data
 *
 * @todo: since the module remembers the saved bins and displays them with a gui
 * run - a click on remove bin when one of the bins is chosen will cause a null
 * pointer exception. and it does not remove the bin.
 */
public class SQLBinAttributes extends HeadlessUIModule {
    private static final String EMPTY = "",
    COLON = " : ", COMMA = ",", DOTS = "...",
    OPEN_PAREN = "(", CLOSE_PAREN = ")", OPEN_BRACKET = "[", CLOSE_BRACKET = "]";
    JOptionPane msgBoard = new JOptionPane();

    private NumberFormat nf;
    public double[] mins, maxes, totals;
    public boolean[] colTypes;
    public int totalRows;
    public ConnectionWrapper wrapper;
    private BinDescriptor[] savedBins;
    public Object[] getSavedBins() { return savedBins; }
    public void setSavedBins(Object[] value) { savedBins = (BinDescriptor[])value; }
	public ExampleTable etbl= null;

    /**
     * get the name of the module
     * @return module's name
     */
    public String getModuleName () {
        return  "SQL Bin Attributes";
    }

    /**
     * Get module information
     * @return A description for the module
     */
    public String getModuleInfo () {
      String s = "<p>Overview: ";
      s += "This module allows the user to interactively group data into bins. </p>";
      s += "<p> Detailed Description: ";
      s += "This module makes a connection to a database ";
      s += "and provides a user interface that allows data ";
      s += "to be grouped into bins. ";
      s += "The specific database, table, and attribute data that are presented for binning are ";
      s += "controlled via information read from the <i>Database Connection</i>, <i>Selected Table</i>, ";
      s += "and <i>Selected Attributes</i> input ports. ";
      s += "</p><p>";
      s += "For scalar attributes, data can be binned using a number of methods:  ";
      s += "A) The data can be binned uniformly into a number of bins specified by the user.  ";
      s += "B) The user can explicitly specify the range of values that will be included in each bin.  ";
      s += "C) An interval size can be specified for each bin.  ";
      s += "D) Each bin can be given uniform weight with a specified number of data points included in each bin. ";
      s += "Histograms showing the data distribution based on the specified ";
      s += "binning method are provided via the 'Show' button to guide the binning process. ";
      s += "For nominal attributes, data can ";
      s += "be collected into groups by unique values. ";
      s += "</p><p>";
      s += "Binning data is a very important preprocessing step in many data mining  ";
      s += "activities, especially for datasets containing continuous numeric values. ";
      s += "By grouping similar data together, interesting patterns can be ";
      s += "discovered between groups and computational complexity can be reduced. ";
      s += "However, binning data is an optional ";
      s += "process. For a data set with a small number of unique values, especially ";
      s += "a small nominal data set, binning may not be necessary. The user can omit ";
      s += "the binning step by clicking the 'Done' button without performing any binning actions. ";
      s += "<p> Restrictions: ";
      s += "Only support Oracle, SQLServer, DB2 and MySql databases are currently supported. ";
      s += "</p><p>Scalability ";
      s += "This module consumes ";
      s += "substantial CPU and memory to display data histograms. The amount of memory ";
      s += "required is related to the size of the dataset being processed. ";

      return s;
    }

    /**
     * Get the name of the input parameter
     * @param i is the index of the input parameter
     * @return Name of the input parameter
     */
    public String getInputName (int i) {
        switch (i) {
            case 0:
                return "Database Connection";
            case 1:
                return "Selected Table";
            case 2:
                return "Selected Attributes";
                case 3:
                 return "Meta Data Example Table";
            default:
                return  "No such input";
        }
    }
    /**
     * Get input information
     * @param i is the index of the input parameter
     * @return A description of the input parameter
     */
    public String getInputInfo (int i) {
        switch (i) {
            case 0:
                return "The database connection.";
            case 1:
                return "The selected table from the database.";
            case 2:
                return "The attributes selected from the specified table.";
                case 3:
                 return "An ExampleTable containing metadata about the database table";
            default:
                return  "No such input";
        }
    }

	public boolean isReady() {
			  if (!isInputPipeConnected(3)) {
				return (getInputPipeSize(0)>0 &&
						getInputPipeSize(1)>0 &&
						getInputPipeSize(2)>0);
			  }
			  return super.isReady();
			}

    /**
     * Get the data types for the input parameters
     * @return Connection Wrapper, list of columns chosed, table name, where clause
     */
    public String[] getInputTypes () {
        String[] types =  {
            "ncsa.d2k.modules.core.io.sql.ConnectionWrapper",
            "java.lang.String",
            "[Ljava.lang.String;",
            "ncsa.d2k.modules.core.datatype.table.ExampleTable"
        };
        return  types;
    }

    /**
     * Get the name of the output parameters
     * @param i is the index of the output parameter
     * @return Name of the output parameter
     */
    public String getOutputName (int i) {
        switch (i) {
            case 0:
                return "Binning Transformation";
            default:
                return  "no such output!";
        }
    }

    /**
     * Get output information
     * @param i is the index of the output parameter
     * @return A description of the output parameter
     */
    public String getOutputInfo (int i) {
        switch (i) {
            case 0:
                return  "A Binning Transformation object that " +
         "contains column numbers, names, and labels";
            default:
                return  "No such output";
        }
    }

    /**
     * Get the data types for the output parameters
     * @return A object of class BinTransform
     */
    public String[] getOutputTypes () {
        String[] types =  {"ncsa.d2k.modules.core.datatype.table.transformations.BinTransform"
        };
        return  types;
    }




    //QA Anca added this:
    //headless conversion - vered:
    public PropertyDescription[] getPropertiesDescriptions() {
        PropertyDescription[] pds = new PropertyDescription[2];
        pds[0] = super.supressDescription;
        pds[1] = new PropertyDescription("newColumn", "Create In New Column",
            "Set this property to true if you wish the binned columns to be created in new columns, " +
            "effective when 'Supress User Interface Display' is set to true.");
        return pds;
    }




    /**
     * Create a GUI interface
     * @return An object of SQLBinColumnsView
     */
    protected UserView createUserView () {
        return  new SQLBinColumnsView();
    }

    /**
     * Get a field name mapping
     * @return null
     */
    public String[] getFieldNameMapping () {
        return  null;
    }

    /**
     * A subclass of BinCounts
     * This class get count information from a database table
     */
    private class SQLBinCounts implements BinCounts {
        String[] fieldNames;
        String tableName;
        Connection con;
        Statement stmt;
        String queryStr;
        DatabaseMetaData metadata = null;


      SQLBinCounts(String tn, String[] fn, ConnectionWrapper cw) {
        tableName = tn;
        fieldNames = fn;
        wrapper = cw;
      }

      /** verify whether the column is a numeric column
       *  @param i the index of the column
       *  @return return true if the column is numeric column, false otherwise
       */
      public boolean isColumnNumeric(int i) {
        return colTypes[i];
      }

      /** get the minimum value in a column
       *  @param col the index of the column
       *  @return the minimum value
       */
      public double getMin(int col) {
        return mins[col];
      }

      /** get the maximum value in a column
       *  @param col the index of the column
       *  @return the maximum value
       */
      public double getMax(int col) {
        return maxes[col];
      }

      /** get the number of rows in a table
       *  @return the number of rows
       */
      public int getNumRows() {
        return totalRows;
      }

      /** get the count of rows which have values in specified ranges
       *  @param col the column to check values
       *  @param borders the specified range
       *  @return the row count for each specified range
       */
      public int[] getCounts(int col, double[] borders) {
        int[] counts = new int[borders.length+1];
        //String colName = fieldNames[col].toLowerCase();
        String colName = fieldNames[col];
        double low = -9999999.99;
        double high;
        try {
          con = wrapper.getConnection();
          for (int i = 0; i < (borders.length); i++) {
            high = borders[i];
            queryStr = "select count(" + colName + ") from " + tableName +
                       " where " + colName + " > " + low + " and " + colName +
                       " <= " + high;
            stmt = con.createStatement();
            ResultSet cntSet = stmt.executeQuery(queryStr);
            cntSet.next();
            counts[i] = cntSet.getInt(1);
            low = high;
            stmt.close();
          }
          queryStr = "select count(" + colName + ") from " + tableName +
                       " where " + colName + " > " + low;
          stmt = con.createStatement();
          ResultSet cntSet = stmt.executeQuery(queryStr);
          cntSet.next();
          counts[borders.length] = cntSet.getInt(1);
          stmt.close();
          return counts;
        }
        catch (Exception e) {
     JOptionPane.showMessageDialog(msgBoard,
                e.getMessage(), "Error",
                JOptionPane.ERROR_MESSAGE);
          System.out.println("Error occoured in getCounts.");
          return counts;
        }
      }

      /** get sum of all values in a column
       *  @param col the column to check for
       *  @return sum of all values
       */
      public double getTotal(int col) {
        return totals[col];
      }
    }

    /** user view
     */
    private class SQLBinColumnsView extends JUserPane {
        private boolean setup_complete;
        private BinDescriptor currentSelectedBin;
        private HashMap columnLookup;
        private HashSet[] uniqueColumnValues;
        private JList numericColumnLabels, textualColumnLabels, currentBins;
        private DefaultListModel binListModel;

        /* numeric text fields */
        private JTextField uRangeField, specRangeField, intervalField, weightField;

        /* textual lists */
        private JList textUniqueVals, textCurrentGroup;
        private DefaultListModel textUniqueModel, textCurrentModel;

        /* textual text field */
        private JTextField textBinName;

        /* current selection fields */
        private JTextField curSelName;
        private JList currentSelectionItems;
        private DefaultListModel currentSelectionModel;
        private JButton abort, done;
        private JCheckBox createInNewColumn;

        /**
         * GUI interface for binning data
         */
       private SQLBinColumnsView () {
            setup_complete = false;
            nf = NumberFormat.getInstance();
            nf.setMaximumFractionDigits(3);
        }

        private int numArrived = 0;
        private ConnectionWrapper connectionWrapper;
        private String[] fieldNames;
        private String tableName;
        private SQLBinCounts binCounts;
        private Connection con;
        private Statement stmt;
        private String queryStr;
        private int uniqueTextualIndex;

        /**
         * set up input
         * @param o input object
         * @param id input ID
         */
        public void setInput (Object o, int id) {

            uniqueTextualIndex = 0;

            if(id == 0) {
                connectionWrapper = (ConnectionWrapper)o;
                numArrived = 1;
               // System.out.println("input one ");
            }
            if(id == 1) {
                tableName = (String)o;
                numArrived++;
				//System.out.println("input two ");
            }
            if(id == 2) {
                fieldNames = (String[])o;
                numArrived++;
				//System.out.println("input three ");
            }

			if (isInputPipeConnected(3) && id ==3) {
				//System.out.println("input 3 connected id = " + id);
							 etbl = (ExampleTable)o;
			numArrived ++;
			}

            if(numArrived == 3 ) {
                binCounts = new SQLBinCounts(tableName, fieldNames, connectionWrapper);

                // clear all text fields and lists...
                ((DefaultListModel)textUniqueVals.getModel()).removeAllElements();
                ((DefaultListModel)textCurrentGroup.getModel()).removeAllElements();
                curSelName.setText(EMPTY);
                textBinName.setText(EMPTY);
                uRangeField.setText(EMPTY);
                specRangeField.setText(EMPTY);
                intervalField.setText(EMPTY);
                weightField.setText(EMPTY);
                columnLookup = new HashMap();
                uniqueColumnValues = new HashSet[fieldNames.length];
                maxes = new double[fieldNames.length];
                mins = new double[fieldNames.length];
                totals = new double[fieldNames.length];
                colTypes = new boolean[fieldNames.length];
                binListModel.removeAllElements();

                /*if (savedBins != null)
                   for (int i = 0; i < savedBins.length; i++) {
                      binListModel.addElement(savedBins[i]);
                   }*/

                DefaultListModel numModel = (DefaultListModel)numericColumnLabels.getModel(),
                txtModel = (DefaultListModel)textualColumnLabels.getModel();
                numModel.removeAllElements();
                txtModel.removeAllElements();
                totalRows = getTotalRows();
                colTypes = getColTypes();
                for (int i = 0; i < fieldNames.length; i++) {
                  //columnLookup.put(fieldNames[i].toLowerCase(), new Integer(i));
                  columnLookup.put(fieldNames[i], new Integer(i));
                  if (binCounts.isColumnNumeric(i)) {
                    numModel.addElement((String)fieldNames[i]);
                    //numModel.addElement(((String)fieldNames[i]).toLowerCase());
                    getMMTValues(i); // compute min, max and total
                  }
                  else {
                    txtModel.addElement((String)fieldNames[i]);
                    //txtModel.addElement(((String)fieldNames[i]).toLowerCase());
                    uniqueColumnValues[i] = uniqueValues(i);
                  }
                }

                if (!validateBins(binListModel)) {
                   binListModel.removeAllElements();
                }

                // finished...
                setup_complete = true;
              }

        }

        /** get the number of rows in a table
         *  @return the number of rows
         */
        public int getTotalRows() {
          try {
            con = wrapper.getConnection();
            queryStr = "select count(*) from " + tableName;
            stmt = con.createStatement();
            ResultSet cntSet = stmt.executeQuery(queryStr);
            cntSet.next();
            int val = cntSet.getInt(1);
            stmt.close();
            return(val);
          }
          catch (Exception e) {


            JOptionPane.showMessageDialog(msgBoard,
                  e.getMessage(), "Error",
                  JOptionPane.ERROR_MESSAGE);
            System.out.println("Error occoured in getTotalRows.");

            return 0;
          }
        }

        public void getMMTValues(int col) {
          try {
            String colName = fieldNames[col];
            //String colName = fieldNames[col].toLowerCase();
            con = wrapper.getConnection();
            queryStr = "select min(" + colName + "), max(" + colName + "), sum("
                     + colName + ") from " + tableName;
            stmt = con.createStatement();
            ResultSet totalSet = stmt.executeQuery(queryStr);
            totalSet.next();
            mins[col] = totalSet.getDouble(1);
            maxes[col] = totalSet.getDouble(2);
            totals[col] = totalSet.getDouble(3);
            stmt.close();
          }
          catch (Exception e) {
            JOptionPane.showMessageDialog(msgBoard,
                  e.getMessage(), "Error",
                  JOptionPane.ERROR_MESSAGE);
            System.out.println("Error occoured in getMMTValues.");
          }
        }

        /** verify whether the column is a numeric column
         *  @return a boolean array, numeric columns are flaged as true, and
         *          categorical columns are flaged as false.
         */
        public boolean[] getColTypes() {
          try {
            con = wrapper.getConnection();
            DatabaseMetaData metadata = con.getMetaData();
            String[] names = {"TABLE"};
            ResultSet tableNames = metadata.getTables(null,"%", tableName, names);
            while (tableNames.next()) {
              ResultSet columns = metadata.getColumns(null, "%", tableNames.getString("TABLE_NAME"), "%");
              while (columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");
                String dataType = (columns.getString("TYPE_NAME")).toUpperCase();
                for (int col = 0; col < fieldNames.length; col++) {
                  if (fieldNames[col].equals(columnName)) {
                    if(ColumnTypes.isEqualNumeric(dataType))
                      colTypes[col] = true;
                    else
                      colTypes[col] = false;
                    break;
                  }
                }
              }
            }
            return colTypes;
          }
          catch (Exception e) {
            JOptionPane.showMessageDialog(msgBoard,
                  e.getMessage(), "Error",
                  JOptionPane.ERROR_MESSAGE);
            System.out.println("Error occoured in getColTypes.");
            return null;
          }
        }


        /**
         * Create all of the components and add them to the view.
         * @param m ViewModule object
         */
        public void initView (ViewModule m) {
            currentBins = new JList();
            binListModel = new DefaultListModel();
            currentBins.setModel(binListModel);
            currentBins.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            currentBins.addListSelectionListener(new CurrentListener());
            // set up the numeric tab
            numericColumnLabels = new JList();
            numericColumnLabels.setModel(new DefaultListModel());
            // uniform range
            JOutlinePanel urangepnl = new JOutlinePanel("Uniform Range");
            uRangeField = new JTextField(5);
            JButton addURange = new JButton("Add");
            addURange.addActionListener(new AbstractAction() {

                public void actionPerformed (ActionEvent e) {
                    addUniRange();
                    uRangeField.setText(EMPTY);
                }
            });
            JButton showURange = new JButton("Show");
            showURange.addActionListener(new AbstractAction() {

                public void actionPerformed (ActionEvent e) {
                    HashMap colLook = new HashMap();
                    for (int i = 0; i < fieldNames.length; i++) {
                        if(binCounts.isColumnNumeric(i)) {
                            colLook.put((String)fieldNames[i], new Integer(i));
                        }
                    }

                    String txt = uRangeField.getText();

                    try {
                       int range = Integer.parseInt(txt);
                       if (range <= 1)
                          throw new NumberFormatException();
                    }
                    catch (NumberFormatException ex) {
                       ErrorDialog.showDialog("Must specify a valid range - an integer greater than 1.", "Error");
                       uRangeField.setText(EMPTY);
                       return;
                    }

                    if(txt != null && txt.length() != 0) {
                    String col = (String)numericColumnLabels.getSelectedValue();

                    if (col == null) {
                       ErrorDialog.showDialog("You must select an attribute to bin.", "Error");
                       return;
                    }

                    final Histogram H = new UniformHistogram(binCounts,
                            uRangeField.getText(), colLook, col);
                    JD2KFrame frame = new JD2KFrame("Uniform Range");
                    frame.getContentPane().setLayout(new GridBagLayout());
                    Constrain.setConstraints(frame.getContentPane(), H, 0,
                            0, 3, 1, GridBagConstraints.BOTH, GridBagConstraints.CENTER,
                            1, 1);
                    final JButton uniformAdd = new JButton("Add");
                    Constrain.setConstraints(frame.getContentPane(), new JLabel(""),
                            0, 1, 1, 1, GridBagConstraints.NONE, GridBagConstraints.SOUTHWEST,
                            .33, 0);
                    Constrain.setConstraints(frame.getContentPane(), uniformAdd,
                            1, 1, 1, 1, GridBagConstraints.HORIZONTAL, GridBagConstraints.SOUTH,
                            .34, 0);
                    Constrain.setConstraints(frame.getContentPane(), new JLabel(""),
                            2, 1, 1, 1, GridBagConstraints.NONE, GridBagConstraints.SOUTHEAST,
                            .33, 0);
                    uniformAdd.addActionListener(new AbstractAction() {
                        final JSlider uniformSlider = H.getSlider();

                        public void actionPerformed (ActionEvent e) {
                            uRangeField.setText(Integer.toString(uniformSlider.getValue()));
                            numericColumnLabels.clearSelection();
                            setSelectedNumericIndex(H.getSelection());
                            addUniRange();
                            uRangeField.setText(EMPTY);
                        }
                    });
                    frame.pack();
                    frame.setVisible(true);
                    }
                    else {
                        // message dialog...must specify range
                        ErrorDialog.showDialog("Must specify range.", "Error");
                    }
               }
            });
            urangepnl.setLayout(new GridBagLayout());
            Constrain.setConstraints(urangepnl, new JLabel("Number of Bins"),
                                     0, 0, 1, 1, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
                                     1, 1);
            Box b = new Box(BoxLayout.X_AXIS);
            b.add(uRangeField);
            b.add(addURange);
            b.add(showURange);
            Constrain.setConstraints(urangepnl, b, 1, 0, 1, 1, GridBagConstraints.NONE,
                                     GridBagConstraints.EAST, 1, 1);
            // specified range
            JOutlinePanel specrangepnl = new JOutlinePanel("Specified Range");
            specrangepnl.setLayout(new GridBagLayout());
            Constrain.setConstraints(specrangepnl, new JLabel("Range"), 0,
                                     0, 1, 1, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
                                     1, 1);
            specRangeField = new JTextField(5);
            JButton addSpecRange = new JButton("Add");
            addSpecRange.addActionListener(new AbstractAction() {
                public void actionPerformed (ActionEvent e) {
                    addSpecifiedRange();
                    specRangeField.setText(EMPTY);
                }
            });
            JButton showSpecRange = new JButton("Show");
            showSpecRange.addActionListener(new AbstractAction() {

                public void actionPerformed (ActionEvent e) {
                    String txt = specRangeField.getText();
                    if(txt == null || txt.length() == 0) {
                        // show message dialog
                        // ErrorDialog.showDialog("Must specify range.", "Error");
                        ErrorDialog.showDialog("Please enter a comma-separated sequence of\ninteger or floating-point values for\nthe endpoints of each bin. ", "Error");
                        return;
                    }

                    HashMap colLook = new HashMap();
                    for (int i = 0; i < fieldNames.length; i++) {
                        if(binCounts.isColumnNumeric(i)) {
                            colLook.put((String)fieldNames[i], new Integer(i));
                        }
                    }
                    JD2KFrame frame = new JD2KFrame("Specified Range");
                    String col = (String)numericColumnLabels.getSelectedValue();

                    if (col == null) {
                       ErrorDialog.showDialog("You must select an attribute to bin.", "Error");
                       return;
                    }

                    frame.getContentPane().add(new RangeHistogram(binCounts,
                             specRangeField.getText(), colLook, col));
                             frame.pack();
                             frame.setVisible(true);
                }
            });
            Box b1 = new Box(BoxLayout.X_AXIS);
            b1.add(specRangeField);
            b1.add(addSpecRange);
            b1.add(showSpecRange);
            Constrain.setConstraints(specrangepnl, b1, 1, 0, 1, 1, GridBagConstraints.NONE,
                                     GridBagConstraints.EAST, 1, 1);
            // interval
            JOutlinePanel intervalpnl = new JOutlinePanel("Bin Interval");
            intervalpnl.setLayout(new GridBagLayout());
            intervalField = new JTextField(5);
            JButton addInterval = new JButton("Add");
            addInterval.addActionListener(new AbstractAction() {
                public void actionPerformed (ActionEvent e) {
                    addFromInterval();
                    intervalField.setText(EMPTY);
                }
            });
            JButton showInterval = new JButton("Show");
            showInterval.addActionListener(new AbstractAction() {

                public void actionPerformed (ActionEvent e) {
                    HashMap colLook = new HashMap();
                    for (int i = 0; i < fieldNames.length; i++) {
                        if(binCounts.isColumnNumeric(i)) {
                            colLook.put((String)fieldNames[i], new Integer(i));
                        }
                    }
                    String txt = intervalField.getText();

                    double intrval;
                    try {
                       intrval = Double.parseDouble(txt);
                    }
                    catch(NumberFormatException ex) {
                       ErrorDialog.showDialog("You must specify a valid, positive interval.", "Error");
                       return;
                    }

                    if (intrval <= 0) {
                       ErrorDialog.showDialog("You must specify a valid, positive interval.", "Error");
                       return;
                    }

                    if(txt != null && txt.length() != 0) {
                        String col = (String)numericColumnLabels.getSelectedValue();

                        if(col == null) {
                           ErrorDialog.showDialog("You must select an attribute to bin.", "Error");
                           return;
                        }

                    final Histogram H = new IntervalHistogram(binCounts,
                             intervalField.getText(), colLook, col);
                             JD2KFrame frame = new JD2KFrame("Bin Interval");
                             frame.getContentPane().setLayout(new GridBagLayout());
                             Constrain.setConstraints(frame.getContentPane(), H, 0,
                                     0, 3, 1, GridBagConstraints.BOTH, GridBagConstraints.CENTER,
                                     1, 1);
                             final JButton intervalAdd = new JButton("Add");
                             Constrain.setConstraints(frame.getContentPane(), new JLabel(""),
                                     0, 1, 1, 1, GridBagConstraints.NONE, GridBagConstraints.SOUTHWEST,
                                     .33, 0);
                             Constrain.setConstraints(frame.getContentPane(), intervalAdd,
                                     1, 1, 1, 1, GridBagConstraints.HORIZONTAL, GridBagConstraints.SOUTH,
                                     .34, 0);
                             Constrain.setConstraints(frame.getContentPane(), new JLabel(""),
                                     2, 1, 1, 1, GridBagConstraints.NONE, GridBagConstraints.SOUTHEAST,
                                     .33, 0);
                             intervalAdd.addActionListener(new AbstractAction() {
                                 final JSlider intervalSlider = H.getSlider();

                                 /**
                                  * event for add bin interval
                                  * @param e the action event
                                  */
                                 public void actionPerformed (ActionEvent e) {
                                     int sel = H.getSelection();
                                     numericColumnLabels.clearSelection();
                                     setSelectedNumericIndex(sel);
                                     double interval = getInterval();
                                     intervalField.setText(Double.toString(H.getPercentage()*interval));
                                     addFromInterval();
                                     intervalField.setText(EMPTY);
                                 }
                             });
                             frame.pack();
                             frame.setVisible(true);
                    }
                    else {
                       ErrorDialog.showDialog("You must specify a valid interval", "Error");
                    }
                }
            });
            Constrain.setConstraints(intervalpnl, new JLabel("Interval"), 0,
                                     0, 1, 1, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
                                     1, 1);
            Box b2 = new Box(BoxLayout.X_AXIS);
            b2.add(intervalField);
            b2.add(addInterval);
            b2.add(showInterval);
            Constrain.setConstraints(intervalpnl, b2, 1, 0, 1, 1, GridBagConstraints.NONE,
                                     GridBagConstraints.EAST, 1, 1);
            // uniform weight
            JOutlinePanel weightpnl = new JOutlinePanel("Uniform Weight");
            weightpnl.setLayout(new GridBagLayout());
            weightField = new JTextField(5);
            JButton addWeight = new JButton("Add");
            addWeight.addActionListener(new AbstractAction() {

                /**
                 * event for button "Add"
                 * @param e the action event
                 */
                public void actionPerformed (ActionEvent e) {
                    addFromWeight();
                    weightField.setText(EMPTY);
                }
            });
            JButton showWeight = new JButton("Show");
            showWeight.setEnabled(false);
            Constrain.setConstraints(weightpnl, new JLabel("Number in each bin"),
                                     0, 0, 1, 1, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
                                     1, 1);
            Box b3 = new Box(BoxLayout.X_AXIS);
            b3.add(weightField);
            b3.add(addWeight);
            b3.add(showWeight);
            Constrain.setConstraints(weightpnl, b3, 1, 0, 1, 1, GridBagConstraints.NONE,
                                     GridBagConstraints.EAST, 1, 1);
            // add all numeric components
            JPanel numpnl = new JPanel();
            numpnl.setLayout(new GridBagLayout());
            JScrollPane jsp = new JScrollPane(numericColumnLabels);
            Constrain.setConstraints(numpnl, jsp, 0, 0, 4, 1, GridBagConstraints.BOTH,
                                     GridBagConstraints.WEST, 1, 1);
            Constrain.setConstraints(numpnl, urangepnl, 0, 1, 4, 1, GridBagConstraints.HORIZONTAL,
                                     GridBagConstraints.WEST, 1, 1);
            Constrain.setConstraints(numpnl, specrangepnl, 0, 2, 4, 1, GridBagConstraints.HORIZONTAL,
                                     GridBagConstraints.WEST, 1, 1);
            Constrain.setConstraints(numpnl, intervalpnl, 0, 3, 4, 1, GridBagConstraints.HORIZONTAL,
                                     GridBagConstraints.WEST, 1, 1);
            Constrain.setConstraints(numpnl, weightpnl, 0, 4, 4, 1, GridBagConstraints.HORIZONTAL,
                                     GridBagConstraints.WEST, 1, 1);
            // textual bins
            JPanel txtpnl = new JPanel();
            txtpnl.setLayout(new GridBagLayout());
            textualColumnLabels = new JList();
            textualColumnLabels.addListSelectionListener(new TextualListener());
            textualColumnLabels.setModel(new DefaultListModel());
            textUniqueVals = new JList();
            textUniqueModel = new DefaultListModel();
            textUniqueVals.setModel(textUniqueModel);
            textUniqueVals.setFixedCellWidth(100);
            textCurrentGroup = new JList();
            textCurrentGroup.setFixedCellWidth(100);
            textCurrentModel = new DefaultListModel();
            textCurrentGroup.setModel(textCurrentModel);
            JButton addTextToGroup = new JButton(">");
            addTextToGroup.addActionListener(new AbstractAction() {

                /**
                 * event for button ">"
                 * @param e the action event
                 */
               public void actionPerformed (ActionEvent e) {
                   if (!setup_complete)
                       return;
                   Object[] sel = textUniqueVals.getSelectedValues();
                   for (int i = 0; i < sel.length; i++) {
                       // textUniqueModel.removeElement(sel[i]);
                       if (!textCurrentModel.contains(sel[i]))
                           textCurrentModel.addElement(sel[i]);
                   }
                }
            });
            JButton removeTextFromGroup = new JButton("<");
            removeTextFromGroup.addActionListener(new AbstractAction() {

               public void actionPerformed (ActionEvent e) {
                   if (!setup_complete)
                       return;
                   Object[] sel = textCurrentGroup.getSelectedValues();
                   for (int i = 0; i < sel.length; i++) {
                       textCurrentModel.removeElement(sel[i]);
                       // textUniqueModel.addElement(sel[i]);
                   }
                }
            });
            JTabbedPane jtp = new JTabbedPane(JTabbedPane.TOP);
            jtp.add(numpnl, "Scalar");
            jtp.add(txtpnl, "Nominal");
            Box bx = new Box(BoxLayout.Y_AXIS);
            bx.add(Box.createGlue());
            bx.add(addTextToGroup);
            bx.add(removeTextFromGroup);
            bx.add(Box.createGlue());
            Box bx1 = new Box(BoxLayout.X_AXIS);
            JScrollPane jp1 = new JScrollPane(textUniqueVals);
            jp1.setColumnHeaderView(new JLabel("Unique Values"));
            bx1.add(jp1);
            bx1.add(Box.createGlue());
            bx1.add(bx);
            bx1.add(Box.createGlue());
            JScrollPane jp2 = new JScrollPane(textCurrentGroup);
            jp2.setColumnHeaderView(new JLabel("Current Group"));
            bx1.add(jp2);
            textBinName = new JTextField(10);
            JButton addTextBin = new JButton("Add");
            addTextBin.addActionListener(new AbstractAction() {

                /**
                 * event for "Add" button
                 * @param e the action event
                 */
                public void actionPerformed (ActionEvent e) {
                    Object[] sel = textCurrentModel.toArray();

                    if (sel.length == 0) {
                       ErrorDialog.showDialog("You must select some nominal values to group.", "Error");
                       return;
                    }

                    Object val = textualColumnLabels.getSelectedValue();
                    int idx = ((Integer)columnLookup.get(val)).intValue();

                    String textualBinName;

                    if (textBinName.getText().length() == 0)
                       textualBinName = "bin" + uniqueTextualIndex++;
                     else
                        textualBinName = textBinName.getText();

                    if (!checkDuplicateBinNames(textualBinName)) {
                      ErrorDialog.showDialog("The bin name must be unique, "+textualBinName+" already used.", "Error");
                      return;
                    }

BinDescriptor bd = createTextualBin(idx, textualBinName, sel);
                    HashSet set = uniqueColumnValues[idx];
                    for (int i = 0; i < sel.length; i++) {
                       textUniqueModel.removeElement(sel[i]);
                        textCurrentModel.removeElement(sel[i]);
                        set.remove(sel[i]);
                    }
                    addItemToBinList(bd);
                    textBinName.setText(EMPTY);
                }
            });
            JOutlinePanel jop = new JOutlinePanel("Group");
            JPanel pp = new JPanel();
            pp.add(new JLabel("Name"));
            pp.add(textBinName);
            pp.add(addTextBin);
            jop.setLayout(new BoxLayout(jop, BoxLayout.Y_AXIS));
            jop.add(bx1);
            jop.add(pp);
            JScrollPane jp3 = new JScrollPane(textualColumnLabels);
            Constrain.setConstraints(txtpnl, jp3, 0, 0, 4, 1, GridBagConstraints.BOTH,
                                     GridBagConstraints.WEST, 1, 1);
            Constrain.setConstraints(txtpnl, jop, 0, 1, 4, 1, GridBagConstraints.HORIZONTAL,
                                     GridBagConstraints.WEST, 1, 1);
            // now add everything
            JPanel pq = new JPanel();
            pq.setLayout(new BorderLayout());
            JScrollPane jp4 = new JScrollPane(currentBins);
            jp4.setColumnHeaderView(new JLabel("Current Bins"));
            pq.add(jp4, BorderLayout.CENTER);
            JOutlinePanel jop5 = new JOutlinePanel("Current Selection");
            currentSelectionItems = new JList();
            currentSelectionItems.setVisibleRowCount(4);
            currentSelectionItems.setEnabled(false);
            currentSelectionModel = new DefaultListModel();
            currentSelectionItems.setModel(currentSelectionModel);
            JPanel pt = new JPanel();
            curSelName = new JTextField(10);
            pt.add(new JLabel("Name"));
            pt.add(curSelName);
            JButton updateCurrent = new JButton("Update");
            updateCurrent.addActionListener(new AbstractAction() {

                /**
                 * event for "Update" button
                 * @param e the action event
                 */
                public void actionPerformed (ActionEvent e) {
                    if (!setup_complete)
                        return;
                    if (currentSelectedBin != null) {
                        currentSelectedBin.name = curSelName.getText();
                        currentBins.repaint();
                    }
                }
            });
            JButton removeBin = new JButton("Remove Bin");
            removeBin.addActionListener(new AbstractAction() {

                /**
                 * event for "Remove Bin" button
                 * @param e the action event
                 */
                public void actionPerformed (ActionEvent e) {
                    if (!setup_complete)
                        return;
                    if (currentSelectedBin != null) {
                        int col = currentSelectedBin.column_number;
                        if (currentSelectedBin instanceof TextualBinDescriptor) {
                            uniqueColumnValues[col].addAll(((TextualBinDescriptor)currentSelectedBin).vals);
                        }
                        binListModel.removeElement(currentSelectedBin);
                        currentSelectionModel.removeAllElements();
                        curSelName.setText(EMPTY);
                        // update the group
                        Object lbl = textualColumnLabels.getSelectedValue();
                        if (lbl != null) {
                            int idx = ((Integer)columnLookup.get(lbl)).intValue();
                            HashSet unique = uniqueColumnValues[idx];
                            textUniqueModel.removeAllElements();
                            textCurrentModel.removeAllElements();
                            Iterator i = unique.iterator();
                            while (i.hasNext())
                                textUniqueModel.addElement(i.next());
                        }
                    }
                }
            });
            JButton removeAllBins = new JButton("Remove All");
            removeAllBins.addActionListener(new AbstractAction() {

                /**
                 * event for "Romove All" button
                 * @param e the action event
                 */
                public void actionPerformed (ActionEvent e) {

                   if (!setup_complete)
                      return;

                   while (binListModel.getSize() > 0) {

                      BinDescriptor bd = (BinDescriptor)binListModel.firstElement();

                      if (bd instanceof TextualBinDescriptor) {
                         uniqueColumnValues[bd.column_number].addAll(((TextualBinDescriptor)bd).vals);
                      }
                      binListModel.remove(0);

                   }

                   // binListModel.removeAllElements();
                   currentSelectionModel.removeAllElements();
                   curSelName.setText(EMPTY);

                   // update the group
                   Object lbl = textualColumnLabels.getSelectedValue();
                   // gpape:
                   if (lbl != null) {
                      int idx = ((Integer)columnLookup.get(lbl)).intValue();
                      HashSet unique = uniqueColumnValues[idx];
                      textUniqueModel.removeAllElements();
                      textCurrentModel.removeAllElements();
                      Iterator i = unique.iterator();
                      while (i.hasNext())
                         textUniqueModel.addElement(i.next());
                   }

                }
            });
            createInNewColumn = new JCheckBox("Create in new column", false);
            Box pg = new Box(BoxLayout.X_AXIS);
            pg.add(updateCurrent);
            pg.add(removeBin);
            pg.add(removeAllBins);
            Box pg2 = new Box(BoxLayout.X_AXIS);
            pg2.add(createInNewColumn);
            jop5.setLayout(new BoxLayout(jop5, BoxLayout.Y_AXIS));
            jop5.add(pt);
            JScrollPane pane = new JScrollPane(currentSelectionItems);
            pane.setColumnHeaderView(new JLabel("Items"));
            jop5.add(pane);
            jop5.add(pg);
            jop5.add(pg2);
            JPanel bgpnl = new JPanel();
            bgpnl.setLayout(new BorderLayout());
            bgpnl.add(jp4, BorderLayout.CENTER);
            bgpnl.add(jop5, BorderLayout.SOUTH);
            // finally add everything to this
            setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
            Box bxl = new Box(BoxLayout.X_AXIS);
            bxl.add(jtp);
            bxl.add(bgpnl);
            JPanel buttonPanel = new JPanel();
            abort = new JButton("Abort");
            abort.addActionListener(new AbstractAction() {

                /**
                 * event for Abort button
                 * @param e the action event
                 */
                public void actionPerformed (ActionEvent e) {
                    viewCancel();
                }
            });
            done = new JButton("Done");
            done.addActionListener(new AbstractAction() {

                /**
                 * event for Done button
                 * @param e the action event
                 */
                public void actionPerformed (ActionEvent e) {
                  // if (validateBins(binListModel)) {
                    Object[] tmp = binListModel.toArray();
                    BinDescriptor[] bins = new BinDescriptor[tmp.length];
                    for (int i = 0; i < bins.length; i++)
                        bins[i] = (BinDescriptor)tmp[i];


                    savedBins = new BinDescriptor[bins.length];
                    for (int i = 0; i < bins.length; i++)
                       savedBins[i] = bins[i];

                    BinTransform bt = new BinTransform(etbl, bins, createInNewColumn.isSelected());
                              pushOutput(bt, 0);
                    viewDone("Done");
                  // }
                }
            });
            JButton helpButton = new JButton("Help");
            helpButton.addActionListener(new AbstractAction() {

                /**
                 * event for button "Help"
                 * @param e the action event
                 */
                public void actionPerformed (ActionEvent e) {
                    HelpWindow help = new HelpWindow();
                    help.setVisible(true);
                }
            });
            buttonPanel.add(abort);
            buttonPanel.add(done);
            buttonPanel.add(helpButton);
            setLayout(new BorderLayout());
            add(bxl, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.SOUTH);
          }  // initView

          private boolean checkDuplicateNumericBins(int[] newIndices) {

             for (int bdi = 0; bdi < binListModel.getSize(); bdi++) {

                BinDescriptor bd = (BinDescriptor)binListModel.elementAt(bdi);

                for (int i = 0; i < newIndices.length; i++) {

                   if (newIndices[i] == bd.column_number) {

                      JOptionPane.showMessageDialog(this, "You must remove all existing bins on attribute '" +
                      fieldNames[newIndices[i]] + "' before creating new ones.", "Error", JOptionPane.ERROR_MESSAGE);

                      return false;

                   }

                }

             }

             return true;

          }

          private boolean validateBins(DefaultListModel newBins) {
            boolean match = false;
            for (int binIdx = 0; binIdx < newBins.size(); binIdx++) {
              match = false;
              for (int colIdx = 0; colIdx < fieldNames.length; colIdx++) {
                if (newBins.get(binIdx).toString().indexOf(fieldNames[colIdx])>=0) {
                  match = true;
                  break;
                }
                else {
                  continue;
                }
              }
              if (!match) {
                /*
                JOptionPane.showMessageDialog(msgBoard,
                         "Current bins contain non-selected attributes. Please remove them.", "Error",
                         JOptionPane.ERROR_MESSAGE);
                 */
                return false;
              }
            }
            return true;
          }


        private boolean checkDuplicateBinNames(String newName) {
           for (int bdi = 0; bdi < binListModel.getSize(); bdi++) {
              BinDescriptor bd = (BinDescriptor)binListModel.elementAt(bdi);
              if (newName.equals(bd.name))
                  return false;
           }
           return true;
        }

        /** find unique values in a column
         *  @param col the column to check for
         *  @return a HashSet object that stores all unique values
         */
        private HashSet uniqueValues (int col) {
            // count the number of unique items in this column
            HashSet set = new HashSet();
          try {
            String colName = fieldNames[col];
            //String colName = fieldNames[col].toLowerCase();
            con = connectionWrapper.getConnection();
            queryStr = "select distinct " + colName + " from " + tableName +" where " + colName + " is not null";
            stmt = con.createStatement();
            ResultSet distinctSet = stmt.executeQuery(queryStr);
            while (distinctSet.next()) {
              set.add(distinctSet.getString(1));
            }
            stmt.close();
            return set;
          }
          catch (Exception e) {
       JOptionPane.showMessageDialog(msgBoard,
                e.getMessage(), "Error",
                JOptionPane.ERROR_MESSAGE);
            System.out.println("Error occured in uniqueValues.");
            return null;
          }
        }

        /** Get the column indices of the selected numeric columns.
         *  @return an int[] that stores column indices
         */
        private int[] getSelectedNumericIndices () {
            Object[] setVals = numericColumnLabels.getSelectedValues();
            int[] colIdx = new int[setVals.length];
            for (int i = 0; i < colIdx.length; i++)
                colIdx[i] = ((Integer)columnLookup.get(setVals[i])).intValue();
            return  colIdx;
        }

        /**
         * mark the selected column
         * @param index the column selected
         */
        private void setSelectedNumericIndex (int index) {
            numericColumnLabels.setSelectedIndex(index);
        }

        /** Get the range of the first selected column.
         *  @return the difference between max and min
         */
        private double getInterval () {
// !:
//            int colIdx = numericColumnLabels.getSelectedIndex();
int colIdx = ((Integer)columnLookup.get(numericColumnLabels.getSelectedValue())).intValue();
            double max = binCounts.getMax(colIdx);
            double min = binCounts.getMin(colIdx);
            return  max - min;
        }

        /**
         * Add uniform range bins
         */
        private void addUniRange () {
            int[] colIdx = getSelectedNumericIndices();

            if (!checkDuplicateNumericBins(colIdx)) {
               return;
            }

            // uniform range is the number of bins...
            String txt = uRangeField.getText();
            int num;
            // ...get this number
            try {
                num = Integer.parseInt(txt);
                if (num <= 1)
                   throw new NumberFormatException();
            } catch (NumberFormatException e) {
                ErrorDialog.showDialog("Must specify a valid range - an integer greater than 1.", "Error");
                uRangeField.setText(EMPTY);
                return;
            }

            for (int i = 0; i < fieldNames.length; i++) {
              for (int j = 0; j < colIdx.length; j++) {
                if (i == colIdx[j]) {
                  double[] binMaxes = new double[num - 1];
                  double interval = (maxes[i] - mins[i])/(double)num;
                  // add the first bin manually
                  binMaxes[0] = mins[i] + interval;
                  BinDescriptor nbd = createMinNumericBinDescriptor(i,
                        binMaxes[0]);
                  addItemToBinList(nbd);
                  for (int k = 1; k < binMaxes.length; k++) {
                    binMaxes[k] = binMaxes[k - 1] + interval;
                    // now create the BinDescriptor and add it to the bin list
                    nbd = createNumericBinDescriptor(i, binMaxes[k -1], binMaxes[k]);
                    addItemToBinList(nbd);
                  }
                  nbd = createMaxNumericBinDescriptor(i, binMaxes[binMaxes.length - 1]);
                  addItemToBinList(nbd);
                }
              }
            }
        }

        /**
         * Add bins from a user-specified range
         */
        private void addSpecifiedRange () {
            int[] colIdx = getSelectedNumericIndices();

            //vered:
            if(colIdx.length == 0){
               ErrorDialog.showDialog("You must select an attribute to bin.", "Error");
               return;
            }

            if (!checkDuplicateNumericBins(colIdx)) {
               return;
            }

            // specified range is a comma-separated list of bin maxes
            String txt = specRangeField.getText();

            //vered:
            if(txt == null || txt.length() == 0) {
               ErrorDialog.showDialog("Please enter a comma-separated sequence of\ninteger or floating-point values for\nthe endpoints of each bin. ", "Error");
               return;
            }

            ArrayList al = new ArrayList();
            StringTokenizer strTok = new StringTokenizer(txt, COMMA);
            double[] binMaxes = new double[strTok.countTokens()];
            int idx = 0;
            try {
                while (strTok.hasMoreElements()) {
                    String s = (String)strTok.nextElement();
                    binMaxes[idx++] = Double.parseDouble(s);
                }
            } catch (NumberFormatException e) {
               //vered
               ErrorDialog.showDialog("Please enter a comma-separated sequence of\ninteger or floating-point values for\nthe endpoints of each bin. ", "Error");
                return;
            }

            // now create and add the bins
            for (int i = 0; i < fieldNames.length; i++) {
              for (int j = 0; j < colIdx.length; j++) {
                if (i == colIdx[j]) {
                  BinDescriptor nbd = createMinNumericBinDescriptor(i,
                      binMaxes[0]);
                  addItemToBinList(nbd);
                  for (int k = 1; k < binMaxes.length; k++) {
                    // now create the BinDescriptor and add it to the bin list
                    nbd = createNumericBinDescriptor(i, binMaxes[k - 1], binMaxes[k]);
                    addItemToBinList(nbd);
                }
                nbd = createMaxNumericBinDescriptor(i, binMaxes[binMaxes.length - 1]);
                addItemToBinList(nbd);
                }
              }
            }
        }

        /**
         * Add bins from an interval - the width of each bin
         */
        private void addFromInterval () {
            int[] colIdx = getSelectedNumericIndices();

            //vered:
            if(colIdx.length == 0){
              ErrorDialog.showDialog("You must select an attribute to bin.", "Error");
                return;
            }

            if (!checkDuplicateNumericBins(colIdx)) {
               return;
            }

            // the interval is the width
            String txt = intervalField.getText();
            double intrval;
            try {
                intrval = Double.parseDouble(txt);
            } catch (NumberFormatException e) {
               //vered:
              ErrorDialog.showDialog("Must specify a positive number", "Error");
                return;
            }

            if (intrval <= 0) {
               ErrorDialog.showDialog("Must specify a positive number", "Error");
               return;
            }

            for (int i = 0; i < fieldNames.length; i++) {
              for (int j = 0; j < colIdx.length; j++) {
                if (i == colIdx[j]) {

                  //vered: (01-16-04) added this code to create the desired number of bins.
                  //not too few and not too many.

                     double up;
                     int idx;
                     Vector bounds = new Vector();
                     for(idx=0,up=mins[i]; up<maxes[i]; idx++, up+=intrval){
                       bounds.add(idx, new Double(up));
                     }

                     //now bounds holds the upper bounds for all bins except the last one.
                  //end of vered's code.


                  //vered: (01-16-04) commented out this line, to create exactly the desired number of bins
                  // the number of bins is (max - min) / (bin width)
                  //int num = (int)Math.round((maxes[i] - mins[i])/intrval);

				  //System.out.println("column " + i + " max " + maxes[i] + " min " + mins[i] + " num " + num  + "original " +( maxes[i]-mins[i])/intrval);

//vered: (01-16-04)replaced *num* with *bounds.size()*
                  //Anca replaced num-1 with num to fix bug  175
                  double[] binMaxes = new double[/*num*/bounds.size()];


//vered: (01-16-04)replaced *mins[i]* with *((Double)bounds.get(0)).doubleValue()*
                 //Anca replaced: binMaxes[0] = mins[i] + intrval;
                 //System.out.println("interval " + intrval + " i " + i + " num " + num);
                 //System.out.println("binMaxes[0]  " + binMaxes[0] + " mins[i]" + mins[i]);
                  binMaxes[0] = /*mins[i]*/ ((Double)bounds.get(0)).doubleValue();


                  // add the first bin manually
                  BinDescriptor nbd = createMinNumericBinDescriptor(i, binMaxes[0]);
                  addItemToBinList(nbd);
                  for (int k = 1; k < binMaxes.length; k++) {

                   //vered: (01-16-04)replaced *binMaxes[k - 1] + intrval* with *((Double)bounds.get(k)).doubleValue()*
                    binMaxes[k] = /*binMaxes[k - 1] + intrval*/ ((Double)bounds.get(k)).doubleValue();

					//System.out.println("binMax j " + binMaxes[k] + " " + k);
                    // now create the BinDescriptor and add it to the bin list
                    nbd = createNumericBinDescriptor(i, binMaxes[k - 1], binMaxes[k]);
                    addItemToBinList(nbd);
                  }
                  nbd = createMaxNumericBinDescriptor(i, binMaxes[binMaxes.length - 1]);
                  addItemToBinList(nbd);
                }
              }
            }
        }

        /**
         * Add bins given a weight. This will attempt to make bins with an
         * equal number of tallies in each.
         *
         */
        private void addFromWeight () {
            int[] colIdx = getSelectedNumericIndices();
            // the weight is the number of items in each bin

            //vered:
            if(colIdx.length == 0){
              ErrorDialog.showDialog("You must select an attribute to bin.", "Error");
                return;
            }

            if (!checkDuplicateNumericBins(colIdx)) {
               return;
            }

            String txt = weightField.getText();
            int weight;
            try {
               weight = Integer.parseInt(txt);
               //vered:
               if(weight <= 0) throw new NumberFormatException();

            } catch (NumberFormatException e) {
               //vered:
               ErrorDialog.showDialog("Must specify a positive integer", "Error");
               return;
            }

            for (int i = 0; i < colIdx.length; i++) {
				Double db1 = null;
						 ArrayList list = new ArrayList();

              try {
                int aColIdx = colIdx[i];
                String colName = fieldNames[aColIdx];
                //String colName = fieldNames[aColIdx].toLowerCase();
                con = connectionWrapper.getConnection();
               // Dora add the where clause to fix bug 172
                queryStr = "select " + colName + ", count(" + colName + ") from " +
                           tableName + " where " + colName + " is not null group by "+ colName;
                stmt = con.createStatement();
                ResultSet groupSet = stmt.executeQuery(queryStr);
                //ANCA changed int itemCnt = 0; to the line below to fix bug 154
                //Dora changed from int itemCnt = -1;
                int itemCnt = 0;
                while (groupSet.next()) {
                  itemCnt += groupSet.getInt(2);
                  db1 = new Double(groupSet.getDouble(1));
                  // Dora changed from if (itemCnt >= (weight - 1)) {
                  if (itemCnt >= weight) {
                    // itemCnt >= specified weight, add the value to the list
                    list.add(db1);
                    // reset itemCnt
                    // Dora changed from itemCnt = -1;
                    itemCnt = 0;
                  }
                }
                // put anything left in a bin
               // System.out.println("itemCnt " + itemCnt + " for  "+ colName );
                if (itemCnt > 0)
                  list.add(db1);
				stmt.close();
			  }
					   catch (Exception e) {
					  JOptionPane.showMessageDialog(msgBoard,
							 e.getMessage(), "Error",
							 JOptionPane.ERROR_MESSAGE);
						   System.out.println("Error occured in addFromWeight. " + e);
					   }

                double[] binMaxes = new double[list.size()];
                for (int j = 0; j < binMaxes.length; j++) {
                  binMaxes[j] = ((Double)list.get(j)).doubleValue();
                // System.out.println("binmaxes for j = " + j + " is " +  binMaxes[j]);
                }

				if (binMaxes.length < 2) {
							 BinDescriptor nbd = createMinMaxBinDescriptor(colIdx[i]);
								addItemToBinList(nbd);
					 } else {


                // add the first bin manually
                BinDescriptor nbd = createMinNumericBinDescriptor(colIdx[i],
                        binMaxes[0]);
                addItemToBinList(nbd);
                // Dora changed from   for (int j = 1; j < binMaxes.length-1; j++) {
                for (int j = 1; j < binMaxes.length -1; j++) {
                    // now create the BinDescriptor and add it to the bin list
                    nbd = createNumericBinDescriptor(colIdx[i], binMaxes[j - 1], binMaxes[j]);
                    addItemToBinList(nbd);
                }
                        // add the last bin - anca:
                        // Dora commented out the following 2 lines.

                   //if(binMaxes.length-2>0)
                       nbd = createMaxNumericBinDescriptor(colIdx[i],binMaxes[binMaxes.length-2]);
                   //else
                     //  nbd = createMaxNumericBinDescriptor(colIdx[i],binMaxes[0]);

                // Dora added the following line.
                //nbd = createMaxNumericBinDescriptor(colIdx[i],binMaxes[binMaxes.length-1]);

                addItemToBinList(nbd);
				}
            }
        }

        /**
         * create bins for categorical data
         * @param idx the index of the column
         * @param name the name of the bin
         * @param sel the values in the bin
         * @return a BinDescriptor object
         */
        private BinDescriptor createTextualBin (int idx, String name, Object[] sel) {
            String[] vals = new String[sel.length];

          System.out.println("creating bin named " + name);
            for (int i = 0; i < vals.length; i++) {
            	   vals[i] = sel[i].toString();
            	   System.out.println(vals[i]);
            }
            return  new TextualBinDescriptor(idx, name, vals, (String)fieldNames[idx]);
            //return  new TextualBinDescriptor(idx, name, vals, (String)fieldNames[idx].toLowerCase());
        }

        /**
         * Create a numeric bin that goes from min to max.
         * @param col the index of the column
         * @param min the min value in the bin
         * @param max the max value in the bin
         * @return a BinDescriptor object
         */
        private BinDescriptor createNumericBinDescriptor (int col, double min,
                double max) {
            StringBuffer nameBuffer = new StringBuffer();
            nameBuffer.append(OPEN_PAREN);
            //nameBuffer.append(nf.format(min));
			nameBuffer.append(min);
            nameBuffer.append(COLON);
            //nameBuffer.append(nf.format(max));
			nameBuffer.append(max);
            nameBuffer.append(CLOSE_BRACKET);
            BinDescriptor nb = new NumericBinDescriptor(col, nameBuffer.toString(),
                    min, max, (String)fieldNames[col]);
                    //min, max, (String)fieldNames[col].toLowerCase());
            return  nb;
        }

        /**
         * Create a numeric bin that goes from Double.MIN_VALUE to max
         * @param col the index of the column
         * @param max the max value in the bin
         * @return a BinDescriptor object
         */
        private BinDescriptor createMinNumericBinDescriptor (int col, double max) {
            //double min = -9999999.99; fixed the problem, BinTree will generate only a one sided query
            StringBuffer nameBuffer = new StringBuffer();
            nameBuffer.append(OPEN_BRACKET);
            nameBuffer.append(DOTS);
            nameBuffer.append(COLON);
            //nameBuffer.append(nf.format(max));
			nameBuffer.append(max);
            nameBuffer.append(CLOSE_BRACKET);
            BinDescriptor nb = new NumericBinDescriptor(col, nameBuffer.toString(),
                    Double.NEGATIVE_INFINITY, max, (String)fieldNames[col]);
                    //Double.NEGATIVE_INFINITY, max, (String)fieldNames[col].toLowerCase());
            return  nb;
        }

        /**
         * Create a numeric bin that goes from min to
         * @param col the index of the column
         * @param min the min value in the bin
         * @return a BinDescriptor object
         */
        private BinDescriptor createMaxNumericBinDescriptor (int col, double min) {
            StringBuffer nameBuffer = new StringBuffer();
            nameBuffer.append(OPEN_PAREN);
            //nameBuffer.append(nf.format(min));
			nameBuffer.append(min);
            nameBuffer.append(COLON);
            nameBuffer.append(DOTS);
            nameBuffer.append(CLOSE_BRACKET);
            BinDescriptor nb = new NumericBinDescriptor(col, nameBuffer.toString(),
                    min, Double.POSITIVE_INFINITY, (String)fieldNames[col]);
                    //min, Double.POSITIVE_INFINITY, (String)fieldNames[col].toLowerCase());
            return  nb;
        }

		/**
				* Create a numeric bin that goes from Double.NEGATIVE_INFINITY to Double.POSITIVE_INFINITY
				*/
			   private BinDescriptor createMinMaxBinDescriptor (int col) {
				   StringBuffer nameBuffer = new StringBuffer();
				   nameBuffer.append(OPEN_BRACKET);
				   nameBuffer.append(DOTS);
				   nameBuffer.append(COLON);
				   nameBuffer.append(DOTS);
				   nameBuffer.append(CLOSE_BRACKET);
				   BinDescriptor nb = new NumericBinDescriptor(col, nameBuffer.toString(),
						   Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,(String)fieldNames[col]);
				   return  nb;
			   }
        /**
         * add an item to a bin
         * @param bd the BinDescriptor to use
         */
        private void addItemToBinList (BinDescriptor bd) {
            binListModel.addElement(bd);
        }

        private class CurrentListener
                implements ListSelectionListener {

            /**
             * verify a value has been changed
             * @param e an event
             */
            public void valueChanged (ListSelectionEvent e) {
                if (!setup_complete)
                    return;
                if (!e.getValueIsAdjusting()) {
                    currentSelectedBin = (BinDescriptor)currentBins.getSelectedValue();
                    if (currentSelectedBin == null) {
                        currentSelectionModel.removeAllElements();
                        curSelName.setText(EMPTY);
                        return;
                    }
                    curSelName.setText(currentSelectedBin.name);
                    if (currentSelectedBin instanceof NumericBinDescriptor) {
                        currentSelectionModel.removeAllElements();
                        currentSelectionModel.addElement(currentSelectedBin.name);
                    }
                    else {
                        currentSelectionModel.removeAllElements();
                        HashSet hs = (HashSet)((TextualBinDescriptor)currentSelectedBin).vals;
                        Iterator i = hs.iterator();
                        while (i.hasNext())
                            currentSelectionModel.addElement(i.next());
                    }
                }
            }
        }       // BinColumnsView$CurrentListener

        private class TextualListener
                implements ListSelectionListener {

            /**
             * verify a value has been changed
             * @param e an event
             */
            public void valueChanged (ListSelectionEvent e) {
                if (!setup_complete)
                    return;
                if (!e.getValueIsAdjusting()) {
                    Object lbl = textualColumnLabels.getSelectedValue();
                    if (lbl != null) {
                        int idx = ((Integer)columnLookup.get(lbl)).intValue();
                        if (uniqueColumnValues[idx]==null) {
                          uniqueColumnValues[idx] = uniqueValues(idx);
                        }
                        HashSet unique = uniqueColumnValues[idx];
                        textUniqueModel.removeAllElements();
                        textCurrentModel.removeAllElements();
                        Iterator i = unique.iterator();
                        while (i.hasNext())
                            textUniqueModel.addElement(i.next());
                    }
                }
            }
        }       // BinColumnsView$TextualListener

        private class HelpWindow extends JD2KFrame {

            /**
             * help window
             */
            public HelpWindow () {
                super("About SQL Bin Attributes");
                JEditorPane ep = new JEditorPane("text/html", getHelpString());
                ep.setCaretPosition(0);
                getContentPane().add(new JScrollPane(ep));
                setSize(400, 400);
            }
        }

        /**
         * display help message
         * @return help message
         */
        private String getHelpString () {
            StringBuffer sb = new StringBuffer();
            sb.append("<html><body><h2>SQL Bin Attributes</h2>");
            sb.append("This module allows a user to interactively bin data from a table. ");
            sb.append("Scalar data can be binned in four ways:<ul>");
            sb.append("<li><b>Uniform range</b><br>");
            sb.append("Enter a positive integer value for the number of bins. SQL Bin Attributes will ");
            sb.append("divide the binning range evenly over these bins.");
            sb.append("<br><li><b>Specified range</b>:<br>");
            sb.append("Enter a comma-separated sequence of integer or floating-point values ");
            sb.append("for the endpoints of each bin.");
            sb.append("<br><li><b>Bin Interval</b>:<br>");
            sb.append("Enter an integer or floating-point value for the width of each bin.");
            sb.append("<br><li><b>Uniform Weight</b>:<br>");
            sb.append("Enter a positive integer value for even binning with that number in each bin.");
            sb.append("</ul>");
            sb.append("To bin scalar data, select attributes from the ``Scalar Attributes'' ");
            sb.append("selection area (top left) and select a binning method by entering a value ");
            sb.append("in the corresponding text field and clicking ``Add''. Data can ");
            sb.append("alternately be previewed in histogram form by clicking the corresponding ");
            sb.append("``Show'' button (this accepts, but does not require, a value in the text field). ");
            sb.append("Uniform weight binning has no histogram (it would always look the same).");
            sb.append("<br><br>To bin nominal data, click the ``Nominal'' tab (top left) to bring ");
            sb.append("up the ``Nominal Attributes'' selection area. Click on an attribute to show a list ");
            sb.append("of unique nominal values in that attribute in the ``Unique Values'' area below. ");
            sb.append("Select one or more of these values and click the right arrow button to group ");
            sb.append("these values. They can then be assigned a collective name as before.");
            sb.append("<br><br>To assign a name to a particular bin, select that bin in ");
            sb.append("the ``Current Bins'' selection area (top right), enter a name in ");
            sb.append("the ``Name'' field below, and click ``Update''. To bin the data and ");
            sb.append("output the new table, click ``Done''.");
            sb.append("</body></html>");
            return  sb.toString();
        }
    }           // BinColumnsView


    //headless conversion support

    private boolean newColumn;
  public void setNewColumn(boolean val){newColumn = val;}
  public boolean getNewColumn(){return newColumn;}

    public void doit() throws Exception{
      //pulling input so that this module will be executed only once.
      wrapper = (ConnectionWrapper) pullInput(0);
      String tableName = (String)pullInput(1);
      String[] fieldNames = (String[]) pullInput(2);
     ExampleTable etbl = null;
	  if (isInputPipeConnected(3)) {
				etbl = (ExampleTable)pullInput(3);
		 }


      //verifying that tableName is in the data base
    if(!StaticMethods.getAvailableTables(wrapper).containsKey(tableName.toUpperCase()))
      throw new Exception(getAlias()+ ": Table named " + tableName +
                          " was not found in the database.");

    //verifying that the bins are relevant to this table's columns.
    HashMap colMap = StaticMethods.getAvailableAttributes(wrapper, tableName);

    if(colMap.size() == 0){
      System.out.println(getAlias()+ ": Warning - Table " + tableName + " has no columns." );

    }


    //getting intersection between colMap and selected attributes.

    String[] targetFields = StaticMethods.getIntersection(fieldNames, colMap);
    if(targetFields.length < fieldNames.length)
      throw new Exception (getAlias() + ": Some of the input field names were " +
                           "not found in the database table.");

    BinningUtils.validateBins(colMap, savedBins, getAlias());
//the following code was replaced with validateBins of BinningUtils.


    /*  if(savedBins == null)
        throw new Exception (this.getAlias()+" has not been configured. Before running headless, run with the gui and configure the parameters.");

      if(savedBins.length == 0) {
        savedBins = new BinDescriptor[0];
        System.out.println(getAlias()+ ": Warning - No bins were configured.\n" +
                           "The transformation will be empty.\n");
        pushOutput(new BinTransform(savedBins, newColumn), 0);
        return;
      }


   for (int i=0; i<savedBins.length; i++){
     if(!colMap.containsKey(savedBins[i].label.toUpperCase()))
       throw new Exception(getAlias()+ ": Bin " +  savedBins[i].toString() + " does not match any column label in the database table." +
                                        " Please reconfigure this module via a GUI run so it can run Headless.");

   }//for
        */


      pushOutput(new BinTransform(etbl, savedBins, newColumn), 0);

    }//doit
    //headless conversion support
}

//QA Comments
// Anca: changed last bin in addFromWeigth

  /**
 * 12-03-03 Vered started qa process.
 *          wrong weight binning - [bug 154] fixed
 *          allows overlapping binning [bug 140, 141] fixed
 *          null pointer exception when trying to remove a nominal bin. [bug 65] (fixed)
 *          exception when adding anominal bin [bug 155] (fixed)
 *
 * 12-23-03 unique values maintenance and overlapping binning of nominal columns in
 *          second run. [bug 169] (fixed).
 *          error with weight binning [bug 177] (fixed)
 *
 * 01-08-04: vered.
  * user may create bins with identical names in same attribute [bug 207] (fixed).
  *
*01-08-04 Anca:
*fixed bug 177 by checking to see if binMaxes in addFromWeigth does have two maxes or only one
  *
 * 01-11-04: vered.
 * creates one bin too many with weight binning, and this last bin is expendable,
 * non of the items are being binned into it [bug 215]. (fixed)
 *
 * 01-16-04: vered.
 * changed the way bin maxes are computed in the addFromInterval method. now it is Math methods independant.
 * all code lines that were changed or added are preceeded by a comment line "//vered"
 * and description of change.
 *
 * 01-21-04: vered.
 *
 * bug 228: binning and representation of missing values. missing values are binned
 * into the "UNKNOWN" bin but are still marked as missing if binning is done
 * in the same column. (fixed)
 *
 * bug 229 - when checking the "create in a new column" box, the module creates
   the new binned columns with identical labels as the original ones have.
 (fixed)


   bug 235 - module does not allow creation of bins with identical names even if
   bins belong to different attributes.


 */