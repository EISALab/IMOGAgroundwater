package ncsa.d2k.modules.core.io.sql;

/**
 * <p>Title: BuildQuery </p>
 * <p>Description: User interface to build a SQL query. </p>
 * @author Hong Cheng, Dora Cai
 * @version 1.0
 */

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import java.sql.*;
import java.beans.PropertyVetoException;
import ncsa.d2k.userviews.swing.*;
import ncsa.d2k.gui.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.*;
import ncsa.gui.*;
import ncsa.d2k.modules.core.vis.widgets.*;
import ncsa.d2k.modules.core.transform.table.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

public class BuildQuery extends HeadlessUIModule
{
  JOptionPane msgBoard = new JOptionPane();
  // ArrayList for column types
  private ArrayList colTypes;
  // ArrayList for column names
  private ArrayList colNames;
  // ExampleTable to keep the meta data for all columns of joined tables.
  // This meta table is used in where clause syntax checking.
  private ExampleTable table;
  // ExampleTable to keep the meta data for selected columns of joined tables
  private ExampleTable meta;

  /* Input holder for ConnectionWrapper */
  protected ConnectionWrapper cw;
  /* where clause for the query */
  String queryCondition;

  protected Vector tableList;
  protected boolean dataTableOnly = true;
  protected boolean dataCubeOnly = false;

  public String getModuleName() {
    return "Build Query";
  }

  public String getModuleInfo() {
    String s = "<p> Overview: ";
    s += "This module helps a user to construct a SQL query. </p>";
    s += "<p> Detailed Description: ";
    s += "This module allows a user to specify the tables to be joined ";
    s += "The user can specify columns from table and specify the join condition. </p>";
    s += "<p> Restrictions: ";
    s += "We currently only support Oracle, SQLServer, DB2 and MySql database.";

    return s;
  }

  public String getInputName(int index) {
    if (index == 0)
      return "Database Connection";
    return null;
  }

  public String[] getInputTypes() {
    String[] types = {"ncsa.d2k.modules.io.input.sql.ConnectionWrapper"};
    return types;
  }

  public String getInputInfo(int index) {
    if (index == 0)
      return "The database connection.";
    return "No such input.";
        }

  public String[] getOutputTypes() {
    String[] o = {"java.lang.String", "[Ljava.lang.String;", "java.lang.String",
                  "ncsa.d2k.modules.core.datatype.table.ExampleTable"};
    return o;
  }

  public String getOutputName(int index) {
    if (index == 0)
      return "Selected Tables";
    else if (index == 1)
      return "Selected Attributes";
    else if (index == 2)
      return "Query Condition";
    else if (index == 3)
      return "Meta Table";
    return null;
  }

  public String getOutputInfo(int index) {
    if (index == 0)
      return "A list of the tables to be joined";
    else if (index == 1)
      return "A list of the attributes selected";
    else if (index == 2)
      return "The query condition";
    else if (index == 3)
      return "The meta table";
    return "No such output.";
  }

/******************************************************************************/
/* properties                                                                 */
/******************************************************************************/
  private String _lastExpression = "";

  public String getLastExpression() {
    return _lastExpression;
  }

  public void setLastExpression(String value) {
    _lastExpression = value;
  }

  private boolean _includeMissingValues = true;
  public boolean getIncludeMissingValues() {
    return _includeMissingValues;
  }
  public void setIncludeMissingValues(boolean value) {
    _includeMissingValues = value;
  }

  /**
     Get the value of dataTableOnly
     @return true if data tables should be listed.
             false otherwise
  */
  public boolean getDataTableOnly() {
    return dataTableOnly;
  }

  /**
     Set the the value of dataTableOnly
     @param b true if data tables should be listed.
              false otherwise
  */
  public void setDataTableOnly(boolean b) {
    dataTableOnly = b;
  }

  /**
     Get the value of dataCubeOnly
     @return true if data cubes should be listed.
             false otherwise
  */
  public boolean getDataCubeOnly() {
    return dataCubeOnly;
  }

  /**
     Set the the value of dataCubeOnly
     @param b true if data tables should be listed.
              false otherwise
  */
  public void setDataCubeOnly(boolean b) throws PropertyVetoException {
    dataCubeOnly = b;

    if ( !dataTableOnly && !dataCubeOnly ) {
      throw new PropertyVetoException(
        "\nYou must set either List Data Tables or List Data Cubes to True.", null );
    }
  }

  public PropertyDescription[] getPropertiesDescriptions() {
    PropertyDescription[] pds = new PropertyDescription[5];
    pds[0] = super.getPropertiesDescriptions()[0];
    pds[1] = new PropertyDescription("queryCondition", "Query Condition",
                                "SQL query condition");
    pds[2] = new PropertyDescription("includeMissingValues", "Include Missing Values",
                                "If set, rows with missing values will be included in the result table.");
    pds[3] = new PropertyDescription ("dataTableOnly", "List Data Tables", "Choose True if you want to list data tables.");
    pds[4] = new PropertyDescription ("dataCubeOnly", "List Data Cubes", "Choose True if you want to list data cubes.");

    return pds;
  }

  /**
     Get the field name map for this module-view combination.
     @return The field name map.
  */
  protected String[] getFieldNameMapping () {
    return null;
  }

  /**
     Create the UserView object for this module-view combination.
     @return The UserView associated with this module.
  */
  protected UserView createUserView() {
    return new BuildQueryView();
  }

  private static String scalar = "FilterConstructionINTERNALscalar";

  public class BuildQueryView extends JUserPane
    implements ActionListener, ExpressionListener {
    ViewModule viewModule;

    JList possibleAttributes = new JList ();
    JTable selectedAttributes;
    JButton add = new JButton ("Add");
    JButton remove = new JButton ("Remove");
    DefaultListModel possibleModel = new DefaultListModel();
    CheckBoxTableModel selectedModel = new CheckBoxTableModel();

    private String fromTable;
    private String tableName;
    private Vector fromTableList;
    private JComboBox tableBox;
    private JTabbedPane jtp;

    private ExpressionGUI gui;
    private FilterExpression expression;

    private JButton addColumnButton, addScalarButton, addOperationButton,
            addBooleanButton, abortButton, nextButton,
            doneButton, helpButton, previousButton;
    private JComboBox selectedTableBox, columnBox, operationBox, booleanBox,
            nominalCombo;
    private JTextField scalarField;

    private JPanel topPanel, comboOrFieldPanel, wherePanel;

    private CardLayout nominalOrScalarLayout;
    private HashMap nominalComboBoxLookup; // look up JComboBoxes for nominal
                                             // columns; key = column #
    private int nominalShowing = -1; // which nominal combobox is showing?
                                       // -1 if scalar textfield is showing
    Object[] values;
    String[] retVal;

    private boolean initialized = false;
    private boolean joinCondOK = true;
    /**
      Perform initializations here.
      @param mod The module that created this UserView
    */
    public void initView(ViewModule mod) {
      viewModule = mod;
      cw = (ConnectionWrapper)pullInput(0);
      initialize();
    }

    public void initialize () {
      removeAll();
      tableList = new Vector(0);
      try{
        DatabaseMetaData metadata = null;
        Connection con = cw.getConnection();
        metadata = con.getMetaData();
        String[] types = {"TABLE"};
        ResultSet tableNames = metadata.getTables(null,"%","%",types);

        while (tableNames.next()) {
          String aTable = tableNames.getString("TABLE_NAME");
          if (dataTableOnly) {
            if (aTable.toUpperCase().indexOf("CUBE") < 0) {
              tableList.addElement(aTable);
            }
          }
          if (dataCubeOnly) {
            if (aTable.toUpperCase().indexOf("CUBE") >= 0) {
              tableList.addElement(aTable);
            }
          }
        }

        // ComboBox for table list
        tableBox = new JComboBox();
        for (int i = 0; i < tableList.size(); i++)
          tableBox.addItem((String)tableList.elementAt(i));
          tableBox.setSelectedIndex(0);
          try {
            con = cw.getConnection();
            DatabaseMetaData dbmd = con.getMetaData();
            tableName = (String) tableList.elementAt(0);
            ResultSet tableSet = dbmd.getColumns(null, null, tableName, "%");
            possibleModel.removeAllElements();
            while (tableSet.next()) {
              possibleModel.addElement(tableName.concat(".").concat(tableSet.
                  getString("COLUMN_NAME")));
            }
          }
          catch(Exception e1)
          {
            JOptionPane.showMessageDialog(msgBoard,
                                          "A error has occurred while retrieving the column list.",
                                          "Error", JOptionPane.ERROR_MESSAGE);
          }
          tableBox.addActionListener (new AbstractAction() {
            public void actionPerformed (ActionEvent e){
              int index = tableBox.getSelectedIndex();
              tableName = (String)tableList.elementAt(index);
              try{
                if (tableName == null || tableName.length()== 0)
                  System.out.println("No table is available.");
                else {
                  Connection con = cw.getConnection();
                  DatabaseMetaData dbmd = con.getMetaData();
                  ResultSet tableSet = dbmd.getColumns (null,null,tableName,"%");
                  possibleModel.removeAllElements();
                  while (tableSet.next())
                  {
                    possibleModel.addElement(tableName.concat(".").concat(tableSet.getString("COLUMN_NAME")));
                  }
                }
              }
              catch(Exception e1)
              {
                JOptionPane.showMessageDialog(msgBoard,
                                              "A error has occurred while retrieving the column list.",
                                              "Error", JOptionPane.ERROR_MESSAGE);
              }
            }
          });

          JPanel columnPanelT = new JPanel();
          columnPanelT.setLayout(new GridBagLayout());
          Constrain.setConstraints(columnPanelT, new JLabel("Table List"), 0, 0, 1, 1,
                                   GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, 0, 0);
          Constrain.setConstraints(columnPanelT, tableBox, 1, 0, 1, 1,
                                   GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, 1, 0);
          Constrain.setConstraints(columnPanelT, new JLabel(" "), 2, 0, 2, 1,
                                   GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, 4, 0);
          Constrain.setConstraints(columnPanelT, new JLabel(" "), 0, 0, 2, 1,
                                   GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, 3, 1);

          JPanel canvasArea = new JPanel();
          canvasArea.setLayout (new BorderLayout ());
          canvasArea.add(columnPanelT, BorderLayout.NORTH);
          JPanel buttons = new JPanel ();
          buttons.setLayout (new GridLayout(2,1));
          buttons.add (add);
          buttons.add (remove);

          JPanel b1 = new JPanel();
          b1.add(buttons);
          // The listener for the add button moves stuff from the possible list
          // to the selected list.
          add.addActionListener (new AbstractAction() {
            public void actionPerformed (ActionEvent e) {
              Object[] sel = possibleAttributes.getSelectedValues();
              for(int i = 0; i < sel.length; i++) {
                /*
                if (selectedAttributes.getRowCount()==0) {
                  possibleModel.removeElement(sel[i]);
                  selectedModel.addRow(new Object[] {sel[i], new Boolean(true), new Boolean(false)});
                } */
                if (!inList(sel[i].toString(), selectedAttributes, 0)) {
                  selectedModel.addRow(new Object[] {sel[i], new Boolean(true),
                                       new Boolean(false)});
                  possibleModel.removeElement(sel[i]);
                }
              }
            }
          });

          // The listener for the remove button moves stuff from the selected list
          // to the possible list.
          remove.addActionListener (new AbstractAction () {
            public void actionPerformed (ActionEvent e) {
              // move the selected rows back to Possible Attributes
              for (int i = 0; i < selectedAttributes.getRowCount(); i++) {
                if (selectedAttributes.isRowSelected(i)) {
                  possibleModel.addElement(selectedAttributes.getValueAt(i, 0));
                }
              }
              // remove the selected rows from Selected Attributes
              for (int i = selectedAttributes.getRowCount()-1; i >= 0; i--) {
                if (selectedAttributes.isRowSelected(i)) {
                  selectedModel.removeRow(i);
                }
              }
            }
          });

           // build the table
          selectedAttributes = new JTable(selectedModel);
          selectedAttributes.setFont(possibleAttributes.getFont());
          selectedAttributes.getTableHeader().setFont(possibleAttributes.getFont());
           // set the renderer
          DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
          selectedAttributes.setDefaultRenderer(Object.class, dtcr);

          CheckBoxRenderer cbr = new CheckBoxRenderer();
          selectedAttributes.setDefaultRenderer(Boolean.class, cbr);

           // size the second column  small since it is just a checkbox

          TableColumn tc = selectedAttributes.getColumnModel().getColumn(0);
          tc.setPreferredWidth(200);
          tc.sizeWidthToFit();
          tc.setMaxWidth(200);
          tc = selectedAttributes.getColumnModel().getColumn(1);
          tc.setPreferredWidth(50);
          tc.sizeWidthToFit();
          tc.setMaxWidth(50);
          tc = selectedAttributes.getColumnModel().getColumn(2);
          tc.setPreferredWidth(50);
          tc.sizeWidthToFit();
          tc.setMaxWidth(50);
           // table properties
          selectedAttributes.setShowVerticalLines(true);
          selectedAttributes.setShowHorizontalLines(false);
          selectedAttributes.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
          //selectedAttributes.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

          selectedAttributes.setModel(selectedModel);
          possibleAttributes.setModel(possibleModel);

          possibleAttributes.setFixedCellWidth(260);
          selectedAttributes.setPreferredScrollableViewportSize(new Dimension(300,190));

          JScrollPane jsp = new JScrollPane(possibleAttributes);
          jsp.setSize(260, 200);
          jsp.setColumnHeaderView(new JLabel("Possible Attributes"));
          JScrollPane jsp1 = new JScrollPane(selectedAttributes);
          jsp1.setSize(300, 200);
          jsp1.setColumnHeaderView(new JLabel("Selected Attributes"));


          canvasArea.add (b1, BorderLayout.CENTER);
          canvasArea.add (jsp, BorderLayout.WEST);
          canvasArea.add (jsp1, BorderLayout.EAST);

          JPanel buttonPanel = new JPanel();
          nextButton = new JButton("Next");
          nextButton.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent ae) {
              if (selectedAttributes.getRowCount()==0) {
                JOptionPane.showMessageDialog(msgBoard,
                                              "You must select at least one attribute.", "Error",
                                              JOptionPane.ERROR_MESSAGE);
                System.out.println("No columns are selected");
              }
              else {
                retVal = new String[selectedAttributes.getRowCount()];
                for (int i=0; i<selectedAttributes.getRowCount(); i++) {
                  retVal[i] = selectedAttributes.getValueAt(i, 0).toString();
                }
                jtp.setSelectedIndex(1);
                initWherePanel();
              }
            }
          });

          JButton abort = new JButton("Abort");
          abort.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent ae) {
              viewCancel();
              possibleModel.removeAllElements();
              possibleAttributes.removeAll();
              selectedModel.clear();
              /*
              for (int i=0; i<selectedAttributes.getRowCount(); i++) {
                selectedModel.removeRow(i);
              } */
              selectedTableBox.removeAllItems();
              columnBox.removeAllItems();
            }
          });

          buttonPanel.add(abort);
          buttonPanel.add(nextButton);
          canvasArea.add(buttonPanel, BorderLayout.SOUTH);
          add(canvasArea);

          // second tab
          // ComboBox for selected table list
          selectedTableBox = new JComboBox();
          selectedTableBox.removeAllItems();
          selectedTableBox.addActionListener(this);

          //ComboBox for attribute list
          columnBox = new JComboBox();
          columnBox.removeAllItems();
          columnBox.addActionListener(this);

          addColumnButton = new JButton(
          new ImageIcon(viewModule.getImage("/images/addbutton.gif")));
          addColumnButton.addActionListener(this);

          JPanel columnPanel = new JPanel();
          columnPanel.setLayout(new GridBagLayout());
          Constrain.setConstraints(columnPanel, new JLabel("Table List"), 0, 0, 1, 1,
                                   GridBagConstraints.NONE, GridBagConstraints.WEST, 1, 0);
          Constrain.setConstraints(columnPanel, selectedTableBox, 1, 0, 1, 1,
                                   GridBagConstraints.NONE, GridBagConstraints.CENTER, 0, 0);
          Constrain.setConstraints(columnPanel, new JLabel("Attr List"), 0, 1, 1, 1,
                                   GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER, 1, 0);
          Constrain.setConstraints(columnPanel, columnBox, 1, 1, 1, 1,
                                   GridBagConstraints.NONE, GridBagConstraints.EAST, 0, 0);
          Constrain.setConstraints(columnPanel, addColumnButton, 2, 1, 1, 1,
                                   GridBagConstraints.NONE, GridBagConstraints.EAST, 0, 0);

          // scalar columns will require textfield input; nominal columns will
          // require a combobox of nominal values:

          scalarField = new JTextField(10);
          scalarField.setText(" ");

          addScalarButton = new JButton(
          new ImageIcon(viewModule.getImage("/images/addbutton.gif")));
          addScalarButton.addActionListener(this);

          nominalComboBoxLookup = new HashMap();
          nominalComboBoxLookup.clear();
          comboOrFieldPanel = new JPanel();
          nominalOrScalarLayout = new CardLayout();
          comboOrFieldPanel.setLayout(nominalOrScalarLayout);
          comboOrFieldPanel.add(scalarField, scalar);

          JPanel nominalOrScalarPanel = new JPanel();
          nominalOrScalarPanel.setLayout(new GridBagLayout());
          Constrain.setConstraints(nominalOrScalarPanel,
                                   new JLabel(), 0, 0, 1, 1,
                                   GridBagConstraints.HORIZONTAL,
                                   GridBagConstraints.CENTER, 1, 0);
          Constrain.setConstraints(nominalOrScalarPanel,
                                   comboOrFieldPanel, 1, 0, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.EAST, 0, 0);
          Constrain.setConstraints(nominalOrScalarPanel,
                                   addScalarButton, 2, 0, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.EAST, 0, 0);

          operationBox = new JComboBox();
          operationBox.addItem("==");
          operationBox.addItem("!=");
          operationBox.addItem("<");
          operationBox.addItem("<=");
          operationBox.addItem(">");
          operationBox.addItem(">=");

          addOperationButton = new JButton(
          new ImageIcon(viewModule.getImage("/images/addbutton.gif")));
          addOperationButton.addActionListener(this);

          JPanel operationPanel = new JPanel();
          operationPanel.setLayout(new GridBagLayout());
          Constrain.setConstraints(operationPanel, new JLabel(), 0, 0, 1, 1,
                                   GridBagConstraints.HORIZONTAL,
                                   GridBagConstraints.CENTER, 1, 0);
          Constrain.setConstraints(operationPanel, operationBox, 1, 0, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.EAST, 0, 0);
          Constrain.setConstraints(operationPanel, addOperationButton, 2, 0, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.EAST, 0, 0);

          booleanBox = new JComboBox();
          booleanBox.addItem("&&");
          booleanBox.addItem("||");

          addBooleanButton = new JButton(
          new ImageIcon(viewModule.getImage("/images/addbutton.gif")));
          addBooleanButton.addActionListener(this);

          JPanel booleanPanel = new JPanel();
          booleanPanel.setLayout(new GridBagLayout());
          Constrain.setConstraints(booleanPanel, new JLabel(), 0, 0, 1, 1,
                                   GridBagConstraints.HORIZONTAL,
                                   GridBagConstraints.CENTER, 1, 0);
          Constrain.setConstraints(booleanPanel, booleanBox, 1, 0, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.EAST, 0, 0);
          Constrain.setConstraints(booleanPanel, addBooleanButton, 2, 0, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.EAST, 0, 0);

          JPanel leftPanel = new JPanel();
          leftPanel.setLayout(new GridBagLayout());
          Constrain.setConstraints(leftPanel, columnPanel, 0, 0, 1, 1,
                                   GridBagConstraints.HORIZONTAL,
                                   GridBagConstraints.NORTH, 1, 0);
          Constrain.setConstraints(leftPanel, nominalOrScalarPanel, 0, 1, 1, 1,
                                   GridBagConstraints.HORIZONTAL,
                                   GridBagConstraints.NORTH, 1, 0);
          Constrain.setConstraints(leftPanel, operationPanel, 0, 2, 1, 1,
                                   GridBagConstraints.HORIZONTAL,
                                   GridBagConstraints.NORTH, 1, 0);
          Constrain.setConstraints(leftPanel, booleanPanel, 0, 3, 1, 1,
                                   GridBagConstraints.HORIZONTAL,
                                   GridBagConstraints.NORTH, 1, 0);
          Constrain.setConstraints(leftPanel, new JLabel(), 0, 4, 1, 1,
                                   GridBagConstraints.BOTH,
                                   GridBagConstraints.CENTER, 1, 1);

          gui = new ExpressionGUI(expression, false);
          gui.addExpressionListener(this);

          gui.getTextArea().setText(_lastExpression);

          topPanel = new JPanel();
          topPanel.setLayout(new GridBagLayout());
          Constrain.setConstraints(topPanel, leftPanel, 0, 0, 1, 1,
                                   GridBagConstraints.VERTICAL,
                                   GridBagConstraints.WEST, 0, 1);
          Constrain.setConstraints(topPanel, gui, 1, 0, 1, 1,
                                   GridBagConstraints.BOTH,
                                   GridBagConstraints.CENTER, 1, 1);

          previousButton = new JButton("Back");
          previousButton.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
              jtp.setSelectedIndex(0);
            }
          });
          abortButton = new JButton("Abort");
          abortButton.addActionListener(this);
          helpButton = new JButton("Help");
          helpButton.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
              HelpWindow help = new HelpWindow();
              help.setVisible(true);
            }
          });

          doneButton = new JButton("Done");
          doneButton.addActionListener(this);

          JPanel bottomPanel = new JPanel();
          bottomPanel.setLayout(new GridBagLayout());
          Constrain.setConstraints(bottomPanel, helpButton, 0, 0, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.WEST, 0, 0);
          Constrain.setConstraints(bottomPanel, new JLabel(), 1, 0, 1, 1,
                                   GridBagConstraints.HORIZONTAL,
                                   GridBagConstraints.CENTER, 1, 0);
          Constrain.setConstraints(bottomPanel, previousButton, 2, 0, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.EAST, 0, 0);
          Constrain.setConstraints(bottomPanel, abortButton, 3, 0, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.EAST, 0, 0);
          Constrain.setConstraints(bottomPanel, doneButton, 4, 0, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.EAST, 0, 0);

          wherePanel = new JPanel();
          wherePanel.setLayout(new GridBagLayout());
          Constrain.setConstraints(wherePanel, topPanel, 0, 0, 1, 1,
                                   GridBagConstraints.BOTH,
                                   GridBagConstraints.CENTER, 0, 0);
          Constrain.setConstraints(wherePanel, new JSeparator(), 0, 1, 1, 1,
                                   GridBagConstraints.HORIZONTAL,
                                   GridBagConstraints.SOUTH, 0, 0);
          Constrain.setConstraints(wherePanel, bottomPanel, 0, 2, 1, 1,
                                   GridBagConstraints.HORIZONTAL,
                                   GridBagConstraints.SOUTH, 0, 0);

          /* Put 2 tabs into tabbedpane */
          jtp = new JTabbedPane(JTabbedPane.TOP);
          jtp.add(canvasArea, "Table & Attribute");
          jtp.add(wherePanel, "Filter condition");

          jtp.addChangeListener(new ChangeListener() {
            // This method is called whenever the selected tab changes
            public void stateChanged(ChangeEvent evt) {
              JTabbedPane jtp = (JTabbedPane)evt.getSource();
              // Get current tab
              int sel = jtp.getSelectedIndex();
              if (sel == 1) {
                if (selectedAttributes.getRowCount()==0) {
                  JOptionPane.showMessageDialog(msgBoard,
                       "You must select at least one attribute.", "Error",
                       JOptionPane.ERROR_MESSAGE);
                       System.out.println("No columns are selected");
                }
                else {
                  retVal = new String[selectedAttributes.getRowCount()];
                  for (int i=0; i<selectedAttributes.getRowCount(); i++) {
                    retVal[i] = selectedAttributes.getValueAt(i,0).toString();
                  }
                  initWherePanel();
                }
              }
            }
          });
          jtp.setSelectedIndex(0);

          setLayout(new BorderLayout());
          add(jtp, BorderLayout.CENTER);
          initialized = true;
          this.updateUI();
        }
        catch(Exception e2)
        {
          e2.printStackTrace();
        }

      } /* end of initialize */

      public void initWherePanel() {
        initialized = false;
        selectedTableBox.removeAllItems();
        fromTableList = getFromTableList();
        table = createAllMeta();
        expression = new FilterExpression(table, getIncludeMissingValues());

        topPanel.remove(gui);
        gui = new ExpressionGUI(expression, false);
        gui.addExpressionListener(this);

        // if _lastExpression contains tables that do not in tableList, do not show it
        gui.getTextArea().setText(_lastExpression);
        for (int i=0; i<fromTableList.size(); i++) {
          if (_lastExpression.indexOf(fromTableList.get(i).toString())<0) {
            gui.getTextArea().setText(" ");
            break;
          }
        }

        getAttributeValues(0); // get the attribute values for the first selected table.
        Constrain.setConstraints(topPanel, gui, 1, 0, 1, 1,
                                 GridBagConstraints.BOTH,
                                 GridBagConstraints.CENTER, 1, 1);

        if (expression != null)
          for (int i = 0; i < fromTableList.size(); i++)
            selectedTableBox.addItem((String)fromTableList.elementAt(i));

        initialized = true;
      }

      protected Vector getFromTableList() {
        fromTableList = new Vector(0);
        fromTableList.removeAllElements();
        String tempTable;
        for(int i = 0; i < retVal.length; i ++)
        {
          tempTable = retVal[i].substring(0, retVal[i].indexOf("."));
          if (!fromTableList.contains(tempTable)) {
            fromTableList.addElement(tempTable);
          }
        }
        return fromTableList;
      }

      /** create an ExampleTable object to hold the meta information.
      *  @return an object of Example table
      */
      private ExampleTable createAllMeta() {
        // build an ArrayList to keep the column name.
        colNames = new ArrayList();
        // build an ArrayList to keep the column type.
        colTypes = new ArrayList();
        DatabaseMetaData metadata = null;

        for (int i=0; i<fromTableList.size(); i++) {
          try {
            Connection con = cw.getConnection();
            metadata = con.getMetaData();
            String selTable = (String)fromTableList.get(i);
            ResultSet columns = metadata.getColumns(null,"%",selTable,"%");
            while (columns.next()) {
              String columnName = columns.getString("COLUMN_NAME");
              String columnType = columns.getString("TYPE_NAME").toUpperCase();
              colNames.add(selTable + "." + columnName);
              colTypes.add(columnType);
            }
          }
          catch (Exception e) {
            JOptionPane.showMessageDialog(msgBoard,
                                          e.getMessage(), "Error",
                                          JOptionPane.ERROR_MESSAGE);
            System.out.println("Error occurred in createMeatTable.");
          }
        }
        Column[] cols = new Column[colNames.size()];
        for (int colIdx = 0; colIdx < colNames.size(); colIdx++) {
          cols[colIdx] = new ObjectColumn(1);
          cols[colIdx].setLabel(colNames.get(colIdx).toString());
          if(ColumnTypes.isEqualNumeric(colTypes.get(colIdx).toString()))
            cols[colIdx].setIsScalar(true);
          else
            cols[colIdx].setIsScalar(false);
        }
        // create an Table to hold the meta data
        MutableTableImpl aTable = new MutableTableImpl(cols);
        for (int colIdx = 0; colIdx < colNames.size(); colIdx++) {
          if (cols[colIdx].getIsScalar()) {
            aTable.setColumnIsScalar(true,colIdx);
            aTable.setColumnIsNominal(false,colIdx);
          }
          else {
            aTable.setColumnIsScalar(false,colIdx);
            aTable.setColumnIsNominal(true,colIdx);
          }
        }

        ExampleTable et = aTable.toExampleTable();
        return et;
      }

      private Vector getUniqueValues(int tableIndex, int columnIndex) {
        Vector columnValues = new Vector();
        try {
          Connection con = cw.getConnection();
          String valueQry = new String("select distinct " + columnBox.getItemAt(columnIndex) + " from ");
          valueQry = valueQry + fromTableList.elementAt(tableIndex) + " where " + columnBox.getItemAt(columnIndex) + " is not null";
          valueQry = valueQry + " order by " + columnBox.getItemAt(columnIndex);
          Statement valueStmt = con.createStatement();
          ResultSet valueSet = valueStmt.executeQuery(valueQry);
          while (valueSet.next()) {
            columnValues.add(valueSet.getString(1));
          }
          return columnValues;
        }
        catch (Exception e){
          JOptionPane.showMessageDialog(msgBoard,
                                        e.getMessage(), "Error",
                                        JOptionPane.ERROR_MESSAGE);
          System.out.println("Error occurred in getUniqValue (db mode).");
          return null;
        }
      }

      public void setInput(Object o, int i) {
        if (i == 0) {
          cw = (ConnectionWrapper)o;
          initialize();
        }
      }

      public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == columnBox && initialized) {
          int colIndex = columnBox.getSelectedIndex();
          String colName = (String)columnBox.getItemAt(colIndex);
          int tblIndex = selectedTableBox.getSelectedIndex();
          int fullColIndex = getColIndex(table, colName); // get column index from the join table
          if (table.isColumnNominal(fullColIndex)) {
            nominalShowing = colIndex;
            nominalCombo = new JComboBox(getUniqueValues(tblIndex,colIndex));
            if(!nominalComboBoxLookup.containsKey(new Integer(colIndex)))
            {
              comboOrFieldPanel.add(nominalCombo, columnBox.getItemAt(colIndex));
              nominalComboBoxLookup.put(new Integer(colIndex), nominalCombo);
            }
            nominalOrScalarLayout.show(comboOrFieldPanel,
                                       (String)columnBox.getItemAt(colIndex));
          }
          else {
            nominalShowing = -1;
            nominalOrScalarLayout.show(comboOrFieldPanel, scalar);
          }
        }

        if (src == selectedTableBox && initialized)
        {
          int index = selectedTableBox.getSelectedIndex();
          getAttributeValues(index);
          initialized = true;
        }

        else if (src == addColumnButton)
          gui.getTextArea().insert((String)columnBox.getSelectedItem(),
                                   gui.getTextArea().getCaretPosition());

        else if (src == addScalarButton) {
          if (nominalShowing < 0) {
            gui.getTextArea().insert(scalarField.getText(),
                                     gui.getTextArea().getCaretPosition());
          }
          else {
            JComboBox combo = (JComboBox)nominalComboBoxLookup.get(
                               new Integer(nominalShowing));
            gui.getTextArea().insert("'" + combo.getSelectedItem() + "'",
                                     gui.getTextArea().getCaretPosition());
          }
        }

        else if (src == addOperationButton)
          gui.getTextArea().insert(" " + operationBox.getSelectedItem() + " ",
                                   gui.getTextArea().getCaretPosition());

        else if (src == addBooleanButton)
          gui.getTextArea().insert(" " + booleanBox.getSelectedItem() + " ",
                                   gui.getTextArea().getCaretPosition());

        else if (src == abortButton) {
          initialized = false;
          viewCancel();
          possibleModel.removeAllElements();
          possibleAttributes.removeAll();
          selectedModel.clear();
          selectedTableBox.removeAllItems();
          columnBox.removeAllItems();
        }

        else if (src == doneButton)
        {
          if (selectedAttributes.getRowCount()==0) {
            JOptionPane.showMessageDialog(msgBoard,
                                          "You must select at least one attribute.", "Error",
                                          JOptionPane.ERROR_MESSAGE);
            System.out.println("No columns are selected");
            jtp.setSelectedIndex(0);
          }
          else if (!isOutputOK()) {
            JOptionPane.showMessageDialog(msgBoard,
                                          "You can only select one output column.", "Error",
                                          JOptionPane.ERROR_MESSAGE);
            System.out.println("No columns are selected");
            jtp.setSelectedIndex(0);
          }
          else {
            initialized = false;
            retVal = new String[selectedAttributes.getRowCount()];
            for (int i=0; i<selectedAttributes.getRowCount(); i++) {
              retVal[i] = selectedAttributes.getValueAt(i,0).toString();
            }
            expressionChanged(gui.getTextArea());
            if(joinCondOK)
            {
              possibleModel.removeAllElements();
              possibleAttributes.removeAll();
              selectedModel.clear();
              jtp.setSelectedIndex(0);
            }
            else
              jtp.setSelectedIndex(1);
            initialized = true;
          }
        }
      }

      protected void getAttributeValues(int tableIndex) {
        tableName = (String) fromTableList.elementAt(tableIndex);
        initialized = false; // set to false to disable columnBox listener's action
        columnBox.removeAllItems();
        for (int i = 0; i < table.getNumColumns(); i++) {
          String colLabel = table.getColumn(i).getLabel();
          if (colLabel.substring(0, colLabel.indexOf(".")).equals(tableName)) {
            columnBox.addItem(colLabel);
          }
        }
        columnBox.updateUI();
        nominalComboBoxLookup.clear();
        // find the first scalar column
        nominalShowing = 0;
        String colLabel;
        int colIndex = 0; // the column index for the selected table
        for (int i = 0; i < table.getNumColumns(); i++) {
          colLabel = table.getColumn(i).getLabel();
          if (colLabel.substring(0, colLabel.indexOf(".")).equals(tableName)) {
            if (table.isColumnScalar(i)) {
              nominalShowing = -1;
              columnBox.setSelectedIndex(colIndex);
              nominalOrScalarLayout.show(comboOrFieldPanel, scalar);
              break;
            }
            colIndex++;
          }
        }

        // no scalar columns in the table, get the comboBox for the first column
        if (nominalShowing == 0) {
          nominalCombo = new JComboBox(getUniqueValues(tableIndex, 0));
          comboOrFieldPanel.add(nominalCombo, columnBox.getItemAt(0));
          nominalComboBoxLookup.put(new Integer(0), nominalCombo);
          nominalOrScalarLayout.show(comboOrFieldPanel,
                                     (String) columnBox.getItemAt(0));
        }
      }

    /**
     * getColIndex
     *
     * @param table ExampleTable
     * @param fullColName String
     * @return int
     */
    private int getColIndex(ExampleTable table, String fullColName) {
      int i = 0;
      for (i = 0; i < table.getNumColumns(); i++) {
        if (table.getColumnLabel(i).equals(fullColName))
          break;
      }
      return i;
    }

    private boolean isOutputOK() {
      int outputCount = 0;
      for (int i=0; i<selectedAttributes.getRowCount(); i++) {
        if (((Boolean)selectedAttributes.getValueAt(i,2)).booleanValue()) {
          outputCount++;
        }
      }
      if (outputCount > 1)
        return false;
      else
        return true;
    }

    public void expressionChanged(Object evaluation) {
         queryCondition = gui.getTextArea().getText();
         _lastExpression = new String(queryCondition);
         // SQL does not support "=="
         while (queryCondition.indexOf("==") >= 0)
           queryCondition = replace(queryCondition, "==", "=");
         // SQL does not support "&&"
         while (queryCondition.indexOf("&&") >= 0)
           queryCondition = replace(queryCondition, "&&", " and ");
         // SQL does not support "||"
         while (queryCondition.indexOf("||") >= 0)
           queryCondition = replace(queryCondition, "||", " or ");

         //headless conversion support
         setQueryCondition(queryCondition);
         //headless conversion support

         // check join condition if more than one table is selected
         if (fromTableList.size() > 1)
           joinCondOK = checkJoinCondition(queryCondition);

         if (joinCondOK)
         {
           fromTable = (String) fromTableList.get(0);
           for (int i = 1; i < fromTableList.size(); i++) {
             fromTable = fromTable + ", " + (String) fromTableList.get(i);
           }
           meta = createSelectedMeta();
           pushOutput(fromTable, 0);
           pushOutput(retVal, 1);
           pushOutput(queryCondition,2);
           pushOutput(meta, 3);
           viewDone("Done");
        }
      }

      private ExampleTable createSelectedMeta() {
        int selected = selectedAttributes.getRowCount();
        Column[] cols = new Column[selected];
        int[] selectedInput = new int[selected];
        int inputIdx=0;
        int[] selectedOutput = new int[selected];
        int outputIdx=0;
        for (int i=0; i<cols.length; i++) {
          cols[i] = new ObjectColumn();
          // set column label
          cols[i].setLabel(selectedAttributes.getValueAt(i,0).toString());
          // set column type
          String colType = getColType(cols[i].getLabel());
          if(ColumnTypes.isEqualNumeric(colType)) {
            cols[i].setIsScalar(true);
            cols[i].setIsNominal(false);
          }
          else {
            cols[i].setIsScalar(false);
            cols[i].setIsNominal(true);
          }
          // set input or output features
          if (((Boolean)selectedAttributes.getValueAt(i,1)).booleanValue()) {
            selectedInput[inputIdx]=i;
            inputIdx++;
          }
          else if (((Boolean)selectedAttributes.getValueAt(i,2)).booleanValue()) {
            selectedOutput[outputIdx]=i;
            outputIdx++;
          }
        }
        // remove the empty entries from selectedInput
        int[] tempInput = new int[inputIdx];
        for (int i=0; i<tempInput.length; i++) {
          tempInput[i] = selectedInput[i];
        }
        selectedInput = tempInput;

        // remove the empty entries from selectedOutput
        int[] tempOutput = new int[outputIdx];
        for (int i=0; i<tempOutput.length; i++) {
          tempOutput[i] = selectedOutput[i];
        }
        selectedOutput = tempOutput;

        MutableTableImpl mt = new MutableTableImpl(cols);
        for (int colIdx = 0; colIdx < cols.length; colIdx++) {
          if (cols[colIdx].getIsScalar()) {
            mt.setColumnIsScalar(true,colIdx);
            mt.setColumnIsNominal(false,colIdx);
          }
          else {
            mt.setColumnIsScalar(false,colIdx);
            mt.setColumnIsNominal(true,colIdx);
          }
        }

        ExampleTable et=mt.toExampleTable();
        et.setInputFeatures(selectedInput);
        et.setOutputFeatures(selectedOutput);

        // if a class label is specified, get the count of the rows that have class labels
        if (outputIdx>0) {
          try {
            Connection con = cw.getConnection();
            String countQry;
            if (queryCondition.length() > 0) {
              countQry = new String("select count(*) from " + fromTable +
                         " where (" + queryCondition +
                         ") and " + selectedAttributes.getValueAt(
                         selectedOutput[0], 0).toString() + " is not null");
            }
            else {
              countQry = new String("select count(*) from " + fromTable +
                         " where " + selectedAttributes.getValueAt(
                         selectedOutput[0], 0).toString() + " is not null");

            }
            Statement countStmt = con.createStatement();
            ResultSet countSet = countStmt.executeQuery(countQry);
            countSet.next();
            int numRows = countSet.getInt(1);
            et.addRows(numRows);
          }
          catch (Exception e) {
            JOptionPane.showMessageDialog(msgBoard,
                                          e.getMessage(), "Error",
                                          JOptionPane.ERROR_MESSAGE);
            System.out.println("Error occurred in createSelectedMeta. " + e);
          }
        }


        return et;
      }

      /**
       * Determine whether a value is already in a column of the table
       * @param val String the value to match with
       * @param table JTable the table to check against
       * @param col int the column in the table to check against
       * @return boolean return true if the value has been in the column of the table
       */
      private boolean inList(String val, JTable table, int col) {
        if (table == null || table.getRowCount() == 0)
          return false;
        else {
          for (int i=0; i<table.getRowCount(); i++) {
            String val1 = val;
            String val2 = table.getValueAt(i,col).toString();
            int idx1 = val1.indexOf(".");
            int idx2 = val2.indexOf(".");
            if (idx1 >= 0)
              val1 = val1.substring(idx1+1, val1.length());
            if (idx2 >= 0)
              val2 = val2.substring(idx2+1, val2.length());
            if (val1.equals(val2)) {
              return true;
            }
          }
        }
        return false;
      }

      private String getColType (String columnLabel) {
        for (int i=0; i<colNames.size(); i++) {
          if (colNames.get(i).toString().equals(columnLabel)) {
            return colTypes.get(i).toString();
          }
        }
        return " ";
      }

      public String replace(String oldString, String oldPattern, String newPattern) {
         int index;
         String newString;
         index = oldString.indexOf(oldPattern);
         // matched substring is located in the middle of the string
         if (index > 0)
           newString = oldString.substring(0,index) + newPattern +
                     oldString.substring(index + oldPattern.length(), oldString.length());
         // matched substring is located in the begining of the string
         else if (index == 0)
           newString = newPattern +
                     oldString.substring(index + oldPattern.length(), oldString.length());
         else
           newString = oldString;
         return newString;
      }


      public boolean checkJoinCondition(String whereClause) {
        int tokenCount = 0;
        // first check whether every selected table is in the where clause
        for (int i=0; i<fromTableList.size(); i++) {
          tokenCount = countTokens(whereClause, fromTableList.get(i).toString());

          if (tokenCount < 1) {
            JOptionPane.showMessageDialog(msgBoard,
                                          "Table " + fromTableList.get(i) +
                                          " is not included in the join conditions. " +
                                          "If you try to join two tables, you need to specify " +
                                          "the join condition such as 'tableA.attribute = tableB.attribute'.",
                                          "Error", JOptionPane.ERROR_MESSAGE);
            return false;
          }
        }
        // now check the number of predicates.
        // if we join n tables, we need at least n-1 join predicates
        tokenCount = countTokens(whereClause, "and");
        // single predicate does not have "and" in the where clause, we need to add 1 for the number of predicates
        if ((tokenCount+1) < (fromTableList.size()-1)) {
          JOptionPane.showMessageDialog(msgBoard,
                                        "The join condition is not sufficiently specified. " +
                                        "If you try to join two tables, you need to specify " +
                                        "the join condition such as 'tableA.attribute = tableB.attribute'.",
                                        "Error", JOptionPane.ERROR_MESSAGE);
          return false;
        }
        return true;
      }

      public int countTokens(String aString, String token) {
        int count = 0;
        int idx = 0;
        idx = aString.indexOf(token);
        while (idx >= 0 && idx < aString.length()) {
          count ++;
          aString = aString.substring(idx+token.length(), aString.length());
          idx = aString.indexOf(token);
        }
        return count;
      }

      public void printVector(Vector v) {
        for (int i=0; i<v.size(); i++) {
          System.out.println("vector " + i + ": " + v.get(i));
        }
      }
   }

/******************************************************************************/
/* help facilities                                                            */
/******************************************************************************/

   private class HelpWindow extends JD2KFrame {
           public HelpWindow() {
                   super ("FilterConstruction Help");
                   JEditorPane ep = new JEditorPane("text/html", getHelpString());
                   ep.setCaretPosition(0);
                   getContentPane().add(new JScrollPane(ep));
                   setSize(400, 400);
           }
   }

   private String getHelpString() {
           StringBuffer sb = new StringBuffer();
           sb.append("<html><body><h2>BuildQuery</h2>");
           sb.append("This module allows a user to join tables from a database");
           sb.append("by specifying an appropriate join expression.");
           sb.append("<br><br>");
           sb.append("The user can construct an expression for join ");
           sb.append("using the lists of columns and operators on the left. ");
           sb.append("It is important that this expression be well-formed: that ");
           sb.append("parentheses match, that column labels or scalars surround ");
           sb.append("operators, and so forth.");
           sb.append("<br><br>");
           sb.append("Column names may not contain spaces or the following ");
           sb.append("characters: =, !, <, >, |, &. You may specify nominal ");
           sb.append("values from columns; i.e., ColumnA != 'value', but those ");
           sb.append("values must be enclosed in tick marks (single quotes) so ");
           sb.append("as to distinguish them from column labels.");
           sb.append("</body></html>");

           return sb.toString();
   }

   //headless conversion support
   public String getQueryCondition(){return queryCondition;}
   public void setQueryCondition(String str){queryCondition = str;}

}
