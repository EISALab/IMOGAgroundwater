package ncsa.d2k.modules.core.io.sql;

/**
 * <p>Title: SaveFileToDB</p>
 * <p>Description: Load data from file to a database table</p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: NCSA </p>
 * @author Dora Cai
 * @version 1.0
 *
 *
 */

import ncsa.d2k.core.modules.*;

import ncsa.d2k.core.modules.UserView;

import ncsa.d2k.userviews.swing.*;

import ncsa.gui.Constrain;
import ncsa.gui.JOutlinePanel;

import ncsa.d2k.modules.core.datatype.table.*;

import java.sql.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

import ncsa.d2k.modules.core.io.file.input.FlatFileParser;
/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 *
 * @todo: add validity test to the properties editor - test that isOracle and isSql
 * have oposite values. currently this validation is in doit.
 */
public class SaveFileToDB extends HeadlessUIModule
       implements java.io.Serializable {




    /* Input holder for ConnectionWrapper */
    protected ConnectionWrapper cw;
    /* Input holder for VerticalTable */
    protected Table vt;
    /* SQL Query String */
    //protected String query;
    /* The resultset table model */
    protected BrowseTables bt;
    /* Popup window for the available table list */
    protected BrowseTablesView btw;
    /* Pane for msg display */
    JOptionPane msgBoard = new JOptionPane();

    /* variables for tableModel objects */
    MetaTableModel newModel;
    MetaTableModel dbModel;
    MetaTableModel vtModel;

    /* variables for JTextField objects */
    JTextField newTableName;
    JTextField chosenTableName;

    /* variables for JTable objects */
    JTable newTableDef;
    JTable dbTableDef;
    JTable vtTableDef;

    /* maximum number of rows in meta table (the number of columns in vt table) */
    int maxNumRow = 900;
    /* maximum number of rows in the data table. If the data table has rows more */
    /* than maxDataRow, suggest user use Database utility to load data           */
    int maxDataRow = 100000;
    /* character used to initialize tables */
    static String  NOTHING = "";

   /**
       Provide a description of this module.
       @return A description of this module.
    */
    public String getModuleInfo() {
      String s = "<p> Overview: ";
      s += "This module saves data from a data table to a database table. </p>";
      s += "<p> Detailed Description: ";
      s += "This module takes an database Connection and a ";
      s += "table as the input, and saves the data from ";
      s += "the data table to a database table. There are two options " ;
      s += "provided. You can either create a new database table to save the data, ";
      s += "or append the data to an existing database table. You may only ";
      s += "append data to a database table you have been granted permissions to do so. If you ";
      s += "cannot find the database table you want to add data to, please report the ";
      s += "problem to your database administrator. </p>";
      s += "<p> Restrictions: ";
      s += "We currently only support Oracle, SQLServer, DB2 and MySql databases.";
      return s;
    }

    /**
       Provide the module name.
       @return The name of this module.
    */
    public String getModuleName() {
      return "Save File To DB";
    }
    /**
       Return a String array containing the datatypes the inputs to this module.
       @return The datatypes of the inputs.
    */
    public String[] getInputTypes () {
        String [] in = {"ncsa.d2k.modules.core.io.sql.ConnectionWrapper",
                        "ncsa.d2k.modules.core.datatype.table.Table" };
        return in;
    }
    /**
        Return a String array containing the datatypes of the outputs of this
        module.
        @return The datatypes of the outputs.
    */
    public String [] getOutputTypes() {
        return null;
    }
    /**
        Return the info for a particular input.
        @param i The index of the input to get info about
        @return The information on the input
     */
    public String getInputInfo (int i) {
        switch(i)
        {
          case 0: return "JDBC data source to make database connection.";
          case 1: return "The table to upload to database.";
          default: return "No such input.";
        }
    }
    public String getInputName (int i) {
      switch(i) {
              case 0:
                      return "DatabaseConnection";
              case 1:
                      return "Table";
              default: return "NO SUCH INPUT!";
      }
    }
    /**
        Return the info for a particular output.
        @param i The index of the output to get info about
        @return The information on the output
     */
    public String getOutputInfo (int i) {
        return null;
    }

    public String getOutputName (int i) {
        return null;
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
        return new TableNameView();
    }

    /**
       Provides a simple user interface to get a table name and table
       definition.  The text values used in the Labels and textfields are
       properties of the module class.  If these properties
       are null, default values are used.
    */
    public class TableNameView extends JUserInputPane
        implements ActionListener {

        /* variables for createTable tab */
        JButton createTableBtn;
        JButton cancelAddBtn;

        /* variables for appendTable tab */
        JButton browseBtn;
        JButton appendTableBtn;
        JButton cancelAppendBtn;


        public Dimension getPreferredSize() {
            return new Dimension (500, 500);
        }
        /**
           Perform initializations here.
           @param mod The module that created this UserView
        */
        public void initView(ViewModule mod) {
            removeAll();
            cw = (ConnectionWrapper)pullInput(0);
            vt = (Table)pullInput(1);

            /* layout the createTable tab */
            JPanel createTablePanel = new JPanel();
            createTablePanel.setLayout (new GridBagLayout());

            /* Outline panel inside of createTable tab */
            JOutlinePanel newTableInfo = new JOutlinePanel("New Table Information");
            newTableInfo.setLayout (new GridBagLayout());

            /* Table with scrollbar for editing the new database table definition */
            newModel = new MetaTableModel(maxNumRow,3,true);
            newTableDef = new JTable(newModel);
            JScrollPane pane = new JScrollPane(newTableDef);
            newTableDef.setPreferredScrollableViewportSize (new Dimension(250,300));

            /* Allow to select a single row only */
            newTableDef.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            /* Use input information on VerticalTable as the default column definition */
            setUpColumnDefault(newTableDef);
            /* The second column "Data Type" has comboBox for choosing data type */
            setUpDataTypeCombo(newTableDef.getColumnModel().getColumn(1));

            /* layout the outline panel inside of createTable tab */
            Constrain.setConstraints(newTableInfo, new JLabel("Table Name"),
                      0,0,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,1,1);
            Constrain.setConstraints(newTableInfo, newTableName = new JTextField(20),
                      1,0,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,2,1);
            Constrain.setConstraints(newTableInfo, new JLabel("Table Columns"),
                      0,1,1,1,GridBagConstraints.NONE, GridBagConstraints.WEST,1,1);
            Constrain.setConstraints(newTableInfo, pane,
                      0,2,2,5,GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER,1,1);

            /* Add outline panel and button into the createTable tab */
            Constrain.setConstraints(createTablePanel, newTableInfo,
                      0,0,3,3,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTH,1,1);
            Constrain.setConstraints(createTablePanel, cancelAddBtn = new JButton ("     Abort     "),
                      1,3,1,1,GridBagConstraints.NONE, GridBagConstraints.EAST,1,1);
            cancelAddBtn.addActionListener(this);
            Constrain.setConstraints(createTablePanel, createTableBtn = new JButton ("Create Table"),
                      2,3,1,1,GridBagConstraints.NONE, GridBagConstraints.WEST,1,1);
            createTableBtn.addActionListener(this);


            /* layout the appendTable tab */
            JPanel appendTablePanel = new JPanel();
            appendTablePanel.setLayout (new GridBagLayout());

            /* first outline panel inside of appendTablePanel */
            JOutlinePanel chooseTable = new JOutlinePanel("Choose a Table");
            chooseTable.setLayout (new GridBagLayout());
            Constrain.setConstraints(chooseTable, chosenTableName = new JTextField(20),
                      0,0,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,1,1);
            chosenTableName.addActionListener(this);
            Constrain.setConstraints(chooseTable, browseBtn = new JButton("Browse"),
                      3,0,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,2,1);
            browseBtn.addActionListener(this);

            /* Second outline panel inside of appendTablePanel */
            JOutlinePanel dbTableInfo = new JOutlinePanel("DB Table Information");
            dbTableInfo.setLayout (new GridBagLayout());
            /* Table with scrollbar for viewing the existing table definition */
            /* the database table is not editable */
            dbModel = new MetaTableModel(maxNumRow,3,false);
            dbTableDef = new JTable(dbModel);
            JScrollPane dbPane = new JScrollPane(dbTableDef);
            dbTableDef.setPreferredScrollableViewportSize(new Dimension(250,125));
            dbTableDef.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            Constrain.setConstraints(dbTableInfo, dbPane,
                      0,0,2,5,GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER,1,1);

            /* Third outline panel inside of appendTablePanel */
            JOutlinePanel vtTableInfo = new JOutlinePanel("Data File Information");
            vtTableInfo.setLayout (new GridBagLayout());

            /* Table with scrollbar for viewing the VT table definition */
            vtModel = new MetaTableModel(maxNumRow,3,false);
            vtTableDef = new JTable(vtModel);
            JScrollPane vtPane = new JScrollPane(vtTableDef);
            vtTableDef.setPreferredScrollableViewportSize(new Dimension(250,125));
            vtTableDef.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            setUpColumnDefault(vtTableDef);
            Constrain.setConstraints(vtTableInfo, vtPane,
                      0,0,2,5,GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER,1,1);

            /* Add outline panels and button into the appendTable tab */
            Constrain.setConstraints(appendTablePanel, chooseTable,
                      0,0,3,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,1,1);
            Constrain.setConstraints(appendTablePanel, dbTableInfo,
                      0,1,3,7,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,1,1);
            Constrain.setConstraints(appendTablePanel, vtTableInfo,
                      0,8,3,7,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,1,1);
            Constrain.setConstraints(appendTablePanel, cancelAppendBtn = new JButton("     Abort     "),
                      1,15,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,1,1);
            cancelAppendBtn.addActionListener(this);
            Constrain.setConstraints(appendTablePanel, appendTableBtn = new JButton("Append Table"),
                      2,15,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,1,1);
            appendTableBtn.addActionListener(this);

            /* Put 2 tabs into tabbedpane */
            JTabbedPane jtp = new JTabbedPane(JTabbedPane.TOP);
            jtp.add(createTablePanel, "Create a New Table");
            jtp.add(appendTablePanel, "Append to a Table");

            setLayout(new BorderLayout());
            add(jtp, BorderLayout.CENTER);

        } /* end of initView */

        /** Fill up column information based on the input vertical table
            @param newTable The JTable to fill up the column information
        */
        protected void setUpColumnDefault(JTable newTable) {
            String sLength;
            for (int i = 0; i < vt.getNumColumns(); i++) {
                String sLabel = vt.getColumnLabel(i);
                String sType = getDataType(vt.getColumnType(i));
                if (sType.equals("string")||
                    sType.equals("byte[]")||
                    sType.equals("char[]"))
                    // if data type is string-like, find the max length
                    sLength = getMaxLength(i);
                else
                    sLength = NOTHING;
                newTable.setValueAt(sLabel,i,0);
                newTable.setValueAt(sType,i,1);
                newTable.setValueAt(sLength.toString(),i,2);
            }
        }

        /** get data type of the column
            @param c The column of the table
            @return The data type of the column
        */
        public String getDataType( int type) {
            if ( type == ColumnTypes.STRING)
                return "string";
            else if(type == ColumnTypes.INTEGER)
                return "int";
            else if(type == ColumnTypes.FLOAT)
                return "float";
            else if(type == ColumnTypes.DOUBLE)
                return "double";
            else if(type == ColumnTypes.LONG)
                return "long";
            else if(type == ColumnTypes.SHORT)
                return "short";
            else if(type == ColumnTypes.BOOLEAN)
                return "boolean";
            else if(type == ColumnTypes.OBJECT)
                return "object";
            else if(type == ColumnTypes.BYTE_ARRAY)
                return "byte[]";
            else if(type == ColumnTypes.CHAR_ARRAY)
                return "char[]";
            else
                return "unknown";
        }

        /** get maximum length of the string column from the vertical table
            @param c The index of the column.
            @return The maximum length of the column.
        */
        public String getMaxLength(int c) {
            int maxLength = 0;
            for (int i=0; i<vt.getNumRows(); i++) {
                int l = vt.getString(i,c).length();
                if (l > maxLength)
                    maxLength = l;
            }
            return (Integer.toString(maxLength));
        }

        /** set up combo box for the data type column
            @param typeColumn The table column to set up combo box
        */
        public void setUpDataTypeCombo(TableColumn typeColumn) {
            JComboBox comboBox = new JComboBox(new String[]{NOTHING,"string",
                "byte[]","char[]","int","float","double","long","short","boolean"});
            typeColumn.setCellEditor (new DefaultCellEditor (comboBox));

            /*Set up tool tips for the data type cell */
            DefaultTableCellRenderer renderer =
                new DefaultTableCellRenderer();
            renderer.setToolTipText ("Click for combo box");
            typeColumn.setCellRenderer(renderer);

            /* Set up tool tip for the datatype column header */
            TableCellRenderer headerRenderer = typeColumn.getHeaderRenderer();
            if (headerRenderer instanceof DefaultTableCellRenderer) {
                ((DefaultTableCellRenderer)headerRenderer).setToolTipText(
                    "Click Data Type to see a list of choices");
            }
        }

        /**
           This method is called when inputs arrive to the
           ViewModule.
           @param input The input
           @param index The index of the input
        */
        public void setInput(Object input, int index) {
          if (index == 0) {
            cw = (ConnectionWrapper)input;
          }
          else if (index == 1) {
            vt = (Table)input;
            newTableName.setText(NOTHING);
            chosenTableName.setText(NOTHING);
            newModel.initTableModel(maxNumRow,3);
            newModel.fireTableDataChanged();
            setUpColumnDefault(newTableDef);
            setUpDataTypeCombo(newTableDef.getColumnModel().getColumn(1));
            dbModel.initTableModel(maxNumRow,3);
            dbModel.fireTableDataChanged();
            vtModel.initTableModel(maxNumRow,3);
            vtModel.fireTableDataChanged();
            setUpColumnDefault(vtTableDef);
          }
        }

        /* this method is fired when a button or enter key is pressed */
        public void actionPerformed(ActionEvent e) {
            Object src = e.getSource();
            if (src == createTableBtn) {
                // check the first row in the data table. If the first row contains
                // the strings of data type, the user has not set the property "typeRow"
                // correctly in data loading.

                //now this module handles all data type names.
                if(checkTypeRow()){
                //for (int idx = 0; idx < vt.getNumColumns(); idx++) {
                  //if (vt.getString(0,idx).equals("double") ||
                    //  vt.getString(0,idx).equals("string")) {
                    JOptionPane.showMessageDialog(msgBoard,
                      "The data table has problems. You did not set the property 'typeRow' " +
                      "correctly for reading data.",
                      "Information", JOptionPane.INFORMATION_MESSAGE);
                    viewAbort();
                    return;
                  }//if check type row

                if (vt.getNumRows() >= maxDataRow) {
                  JOptionPane.showMessageDialog(msgBoard,
                     "There are more than " + maxDataRow + " rows to load. For more " +
                     "efficient data loading, please use " +
                     "the utility the database vendor provides.", "Error",
                     JOptionPane.ERROR_MESSAGE);
                  System.out.println("Too many data to load. Please use database utility.");
                  viewAbort();
                }
                else if (doCreateTable()) {
                  doInsertTable(newTableName.getText(), newTableDef);
                }
            }
            else if (src == appendTableBtn) {
              // check the first row in the data table. If the first row contains
              // the strings of data type, the user has not set the property "typeRow"
              // correctly in data loading.
              for (int idx = 0; idx < vt.getNumColumns(); idx++) {
                if (vt.getString(0,idx).equals("double") ||
                    vt.getString(0,idx).equals("string")) {
                  JOptionPane.showMessageDialog(msgBoard,
                    "The data table has problems. You did not set the property 'typeRow' " +
                    "correctly for reading data.",
                    "Information", JOptionPane.INFORMATION_MESSAGE);
                  viewAbort();
                  return;
                }
              }
              /* user may not hit Enter key in the field chosenTableName before
                 clicking appendTableBtn. We need to force firing getDBTableDef */
              dbModel.initTableModel(maxNumRow,3);
              dbModel.fireTableDataChanged();
              getDBTableDef();
              if (vt.getNumRows() >= maxDataRow) {
                JOptionPane.showMessageDialog(msgBoard,
                   "There are more than " + maxDataRow + " rows to load. For more " +
                   "efficient data loading, please use " +
                   "the utility the database vendor provides.", "Error",
                   JOptionPane.ERROR_MESSAGE);
                System.out.println("Too many data to load. Please use database utility.");
                viewAbort();
              }
              else {
                boolean pass = doValidate(dbTableDef, vtTableDef);
                if (pass) {
                  doInsertTable(chosenTableName.getText(), vtTableDef);
                }
              }
            }
            else if (src == chosenTableName) {
              if (chosenTableName.getText()!= null && chosenTableName.getText()!=NOTHING)
              {
                /* wipe out previously loaded table definition */
                dbModel.initTableModel(maxNumRow,3);
                dbModel.fireTableDataChanged();
                getDBTableDef();
              }
            }
            else if (src == browseBtn) {
              doBrowse();
            }
            else if (src == cancelAddBtn) {
              viewAbort();
            }
            else if (src == cancelAppendBtn) {
              viewAbort();
            }
        } /* end of actionPerformed */

      /** connect to a database and retrieve the list of available tables */
      // All commercial databases have different catalog tables. Inorder to
      // make this method generic to all commercial databases, we have to use
      // java.sql's DatabaseMetaData. However, DatabaseMetaData contains too
      // many fields, we are only interested the table name. Use vector to keep table names.
      protected void doBrowse() {
        Connection con;
        //cw = (ConnectionWrapper)pullInput(0);
         Vector v = new Vector();
          try {
              DatabaseMetaData metadata = null;
              con = cw.getConnection();
              metadata = con.getMetaData();
              String[] types = {"TABLE"};
              ResultSet tableNames = metadata.getTables(null,"%","%",types);
              while (tableNames.next()) {
                String aTable = tableNames.getString("TABLE_NAME");
                v.addElement(aTable);
              }
              bt = new BrowseTables(cw, v);
              btw = new BrowseTablesView(bt, v);
              btw.setSize(250,200);
              btw.setTitle("Available Tables");
              btw.setLocation(200,250);
              btw.setVisible(true);
              btw.addWindowListener(new WindowAdapter() {
                  public void windowClosed(WindowEvent e)
                  {
                    chosenTableName.setText(btw.getChosenRow());
                    /* wipe out previously loaded table definition */
                    dbModel.initTableModel(maxNumRow,3);
                    dbModel.fireTableDataChanged();
                    getDBTableDef();
                  }
              });
           }
           catch (Exception e){
             JOptionPane.showMessageDialog(msgBoard,
                e.getMessage(), "Error",
                JOptionPane.ERROR_MESSAGE);
             System.out.println("Error occurred in doBrowse.");
           }
      }
    } /* end of TableNameView */

    /** create a database table based on user's input
     *  @return true if the table is created successfully, otherwise false
     */
    protected boolean doCreateTable () {
      try {
          System.out.println("TableName is: " + newTableName.getText());

          if (newTableName.getText().length() == 0 ||
              newTableName.getText().equals(NOTHING)) {
            JOptionPane.showMessageDialog(msgBoard,
            "Table name is missing. Table cannot be created", "Error",
            JOptionPane.ERROR_MESSAGE);
            return (false);
          }



          String sb = new String("create table " + newTableName.getText() +
                      " (");
          int i = 0;
          for (i=0; i<newTableDef.getRowCount(); i++) {
            /* s1 is column name */

            //sb.concat(tableToString());

            String s1 = newTableDef.getValueAt(i,0).toString();
            if (s1.length()>0) {
              if (i > 0) // add "," between columns definitions
                sb = sb + ",";
              sb = sb + (String) newTableDef.getValueAt(i,0) + " ";

              /* s2 is column type */

              String s2 = newTableDef.getValueAt(i,1).toString();
              if (s2.length()>0) {
                String len = newTableDef.getValueAt(i, 2).toString();
                /*
                 making this module compatible with data types of flat file parser.
                 vered.
                 */
                //if (s2.equals("string"))
                if(s2.equalsIgnoreCase(FlatFileParser.STRING_TYPE))
                  sb = sb + "varchar(" + len.toString()+")";
                //else if (s2.equals("byte[]"))
                else if (s2.equalsIgnoreCase(FlatFileParser.BYTE_ARRAY_TYPE))
                  sb = sb + "varchar(" + len.toString()+")";
                //else if (s2.equals("char[]"))
                else if (s2.equalsIgnoreCase(FlatFileParser.CHAR_ARRAY_TYPE))
                  sb = sb + "varchar(" + len.toString()+")";
                //else if (s2.equals("int"))
                else if (s2.equalsIgnoreCase(FlatFileParser.INT_TYPE))
                  sb = sb + "numeric";
                //else if (s2.equals("float"))
                else if (s2.equalsIgnoreCase(FlatFileParser.FLOAT_TYPE))
                  sb = sb + number/* supporting also oracle  - vered. "numeric"*/;
                //else if (s2.equals("double"))
                else if (s2.equalsIgnoreCase(FlatFileParser.DOUBLE_TYPE))
                  sb = sb + number /* supporting also oracle - vered. "numeric"*/;

                //else if (s2.equals("long"))
                else if (s2.equalsIgnoreCase(FlatFileParser.LONG_TYPE))
                  sb = sb + "numeric";
                //else if (s2.equals("short"))
                else if (s2.equalsIgnoreCase(FlatFileParser.SHORT_TYPE))
                  sb = sb + "numeric";


                /* boolean datatype is saved as varchar */
               // else if (s2.equals("boolean"))
               else if(s2.equalsIgnoreCase(FlatFileParser.BOOLEAN_TYPE))
                  sb = sb + "varchar(" + "5)";

                  /*adding support gor char and byte - vered*/
              else if (s2.equalsIgnoreCase(FlatFileParser.CHAR_TYPE))
                sb = sb + "varchar(" + "5)";
              else if (s2.equalsIgnoreCase(FlatFileParser.BYTE_TYPE))
                sb = sb + "varchar(" + "5)";


                else {
                  JOptionPane.showMessageDialog(msgBoard,
                  "Invalid data type. Table cannot be created", "Error",
                  JOptionPane.ERROR_MESSAGE);
                  return (false);
                }
              } /* end of if (s2.length()>0)*/
              else /* s2.length()<=0 */ {
                JOptionPane.showMessageDialog(msgBoard,
                "Column length is not provided. Table cannot be created.", "Error",
                JOptionPane.ERROR_MESSAGE);
                return (false);
              }
            } /* end of if (s1.length() > 0) */
            else
              break;
          } /* end for */
          sb = sb + ")";




          Connection con = cw.getConnection ();
          Statement stmt = con.createStatement ();
          stmt.executeUpdate(sb);
          //ResultSet result = stmt.executeQuery(sb);
          stmt.close();
          System.out.println("Table " + newTableName.getText() + " has been created");
       }
       catch (Exception e){
           JOptionPane.showMessageDialog(msgBoard,
                e.getMessage(), "Error",
                JOptionPane.ERROR_MESSAGE);
           System.out.println("Error occurred in doCreateTable.");
           return (false);
      }
      return (true);
    }

    /** insert data from a vertical table into a database table
        @param dTableName The name of the table to create in the database
        @param vTableDef The structure of the table to create in the database
    */
    protected void doInsertTable (String dTableName, JTable vTableDef ) {
      int rowIdx = 0;
      int colIdx = 0;
      /* insert data */
      try {
        Connection con = cw.getConnection ();
        Statement stmt;
        for(rowIdx = 0; rowIdx < vt.getNumRows(); rowIdx++) {
          String sb = new String("insert into " + dTableName +
                        " values (");
          for(colIdx = 0; colIdx < vt.getNumColumns(); colIdx++) {
            /* find the data type */
            String colType = vTableDef.getValueAt(colIdx,1).toString();
            String s = " ";
            if(!vt.isValueMissing(rowIdx, colIdx))
              s = vt.getString(rowIdx, colIdx);
            else
              s = "null";
            //String s = vt.getString(rowIdx, colIdx);
            if (colIdx > 0) /* add "," between columns */
              sb = sb + ",";
            if (colType.equals("string") || colType.equals("byte[]") ||
                colType.equals("char[]") || colType.equals("boolean")) {
              if(!vt.isValueMissing(rowIdx, colIdx))
                sb = sb + "'" + s + "'";
              else
                sb = sb + s;
            }
            else {
              sb = sb + s;
            }
          } /* end of for colIdx */
          sb = sb + ")";
          stmt = con.createStatement ();
          stmt.executeUpdate(sb);
          stmt.close();
        } /* end of for rowIdx */
        JOptionPane.showMessageDialog(msgBoard,
              "Data has been loaded.", "Information",
              JOptionPane.INFORMATION_MESSAGE);
        newTableName.setText(NOTHING);
        chosenTableName.setText(NOTHING);
        newModel.initTableModel(maxNumRow,3);
        dbModel.initTableModel(maxNumRow,3);
        vtModel.initTableModel(maxNumRow,3);
        viewAbort();
      }
      catch (Exception e){
           JOptionPane.showMessageDialog(msgBoard,
                e.getMessage(), "Error in row " + rowIdx ,
                JOptionPane.ERROR_MESSAGE);
           System.out.println("Error occurred in doInsertTable.");
      }
    }




    /** get the structure of a database table
    */
    protected void getDBTableDef() {
      try {
        Connection con = cw.getConnection ();
        DatabaseMetaData metadata = con.getMetaData();
        String[] names = {"TABLE"};
        ResultSet tableNames = metadata.getTables(null,"%",chosenTableName.getText().toUpperCase(),names);
        while (tableNames.next()) {
          ResultSet columns = metadata.getColumns(null,"%",tableNames.getString("TABLE_NAME"),"%");
          int rIdx = 0;
          while (columns.next()) {
            String columnName = columns.getString("COLUMN_NAME");
            String dataType = columns.getString("TYPE_NAME");
            int columnSize = columns.getInt("COLUMN_SIZE");
            dbTableDef.setValueAt(columnName,rIdx,0);
            dbTableDef.setValueAt(dataType,rIdx,1);
            dbTableDef.setValueAt(Integer.toString(columnSize),rIdx,2);
            rIdx++;
          }

          /* if rIdx == 0, that indicate the tableSet is empty, the db table does not exist */
          if (rIdx == 0) {
            JOptionPane.showMessageDialog(msgBoard,
                "Table " + chosenTableName.getText() + " does not exist.", "Error",
                JOptionPane.ERROR_MESSAGE);
            System.out.println("table does not exist.");
          }
        }
      }
      catch (Exception e){
           JOptionPane.showMessageDialog(msgBoard,
                e.getMessage(), "Error",
                JOptionPane.ERROR_MESSAGE);
           System.out.println("Error occurred in getDBTableDef.");
      }
    }

    /** verify the data type and length of the vertical table suitable to
        insert into database
        @param dbTable The JTable keeping the structure of the database table
        @param vtTable The JTable keeping the structure of the vertical table
        @return The validation result (pass or fail)
    */
    protected boolean doValidate(JTable dbTable, JTable vtTable) {
      int dbRowCnt;
      int vtRowCnt;
      for (dbRowCnt=0; dbRowCnt<dbTable.getRowCount(); dbRowCnt++) {
        if (dbTable.getValueAt(dbRowCnt,0).equals(NOTHING))
          break;
      };
      for (vtRowCnt=0; vtRowCnt<vtTable.getRowCount(); vtRowCnt++) {
        if (vtTable.getValueAt(vtRowCnt,0).equals(NOTHING))
          break;
      };
      if (dbRowCnt != vtRowCnt) {
        JOptionPane.showMessageDialog(msgBoard,
                "The number of columns does not match. Data cannot be appended. ",
                "Error", JOptionPane.ERROR_MESSAGE);
        System.out.println("column number does not match.");
        return (false);
      }
      for (int i = 0; i < vtRowCnt; i++) {
        if (!isTypeMatch(dbTable.getValueAt(i,1),vtTable.getValueAt(i,1))) {
          JOptionPane.showMessageDialog(msgBoard,
                "The data type of column " + (i+1) +
                " does not match. Data cannot be " +
                "appended. ", "Error", JOptionPane.ERROR_MESSAGE);
          System.out.println("column data type does not match.");
          return (false);
        }
        if (!isLengthMatch(dbTable.getValueAt(i,2),vtTable.getValueAt(i,2))) {
          JOptionPane.showMessageDialog(msgBoard,
                "The column " + (i+1) +
                " in data table is too big. Data cannot be " +
                "appended. ", "Error", JOptionPane.ERROR_MESSAGE);
          System.out.println("column is too big.");
          return (false);
        }
      }
      return (true);
    }

    /** verify the vertical table's datatype matches the database table's
        @param type1 The database table's data type
        @param type2 The vertical table's data type
        @return Dose the data type match? (yes or no)
    */
    protected boolean isTypeMatch (Object type1, Object type2) {
      if (type1.toString().toLowerCase().indexOf("varchar")>=0) {
        if(!ColumnTypes.isEqualNumeric(type2.toString()))
          return (true);
        else
          return (false);
      }

      else if(ColumnTypes.isContainNumeric(type1.toString())){
        if(ColumnTypes.isEqualNumeric(type2.toString()))
            return (true);
        else
            return (false);
      }
     return (false);
    }

    /** verify the vertical table's data length matches the database table's
        @param length1 The database table's data length
        @param length2 The vertical table's data length
        @return Dose the data length match? (yes or no)
    */
    protected boolean isLengthMatch (Object length1, Object length2) {
      /* numeric column always uses the maximum length in the database */
      if (length2.equals(NOTHING)) {
        return (true);
      }
      /* if vertical column length > database column length, return false */
      else if (Integer.valueOf(length1.toString()).intValue()
                < Integer.valueOf(length2.toString()).intValue()) {
        return (false);
      }
      return(true);
    }




      //conversion to headless ui modules support: [vered]
//attributes
         private boolean createNewTable;
         public boolean getCreateNewTable(){return createNewTable;}
        public void setCreateNewTable(boolean val){createNewTable = val;}

        private String tableName;
        public String getTableName(){return tableName;}
        public void setTableName(String str){tableName = str;}


        public PropertyDescription [] getPropertiesDescriptions () {
         // PropertyDescription [] super_pds = super.getPropertiesDescriptions();
         PropertyDescription [] pds = new PropertyDescription [5];
         pds[0] = super.supressDescription;
         pds[1] = new PropertyDescription ("createNewTable", "Create New Table", "true if file is to be saved to a new table. false if file is to be appended to an existing table");
         pds[2] = new PropertyDescription ("tableName", "Table Name", "The table name to which the file is to be saved.");
         pds[3] = new PropertyDescription ("isSql", "SQL Server", "Set this property to true if the input connection is to an SQL server.");
         pds[4] = new PropertyDescription ("isOracle", "Oracle Server", "Set this property to true if the input connection is to an Oracle server.");
         return pds;
       }

//attributes

       private String[] ColumnMaxLength; //for each column i in the input table, ColumnMaxLength[i] will hold its max length

       /**
        * Returns the maximum length of column no. <code>col</code> converted to a String.
        * @param col - the column no. to figure it's maximum length
        * @return - maximum length of column #<ocde>col</code> as a String.
        */

       protected String getMaxLength(int col){
         int maxLength = 0;
         for (int i=0; i<vt.getNumRows(); i++) {
             int l = vt.getString(i,col).length();
             if (l > maxLength)
                 maxLength = l;
         }
         return (Integer.toString(maxLength));

       }

           //conversion to headless - Vered - 9-9-03

           /**
            * returns true if finds at the first row of <code>vt</code>
            * data types names (such as 'double, 'string').
            *
            * @return true if finds at the first row of <code>vt</code>
            * data types names (such as 'double, 'string').
            */
           protected boolean checkTypeRow(){
             for (int idx = 0; idx < vt.getNumColumns(); idx++){
               String current = vt.getString(0, idx);
               if (current.equalsIgnoreCase(FlatFileParser.BOOLEAN_TYPE) ||
                   current.equalsIgnoreCase(FlatFileParser.BYTE_ARRAY_TYPE) ||
                   current.equalsIgnoreCase(FlatFileParser.BYTE_TYPE) ||
                   current.equalsIgnoreCase(FlatFileParser.CHAR_ARRAY_TYPE) ||

                   current.equalsIgnoreCase(FlatFileParser.CHAR_TYPE) ||
                   current.equalsIgnoreCase(FlatFileParser.DOUBLE_TYPE) ||
                   current.equalsIgnoreCase(FlatFileParser.FLOAT_TYPE) ||
                   current.equalsIgnoreCase(FlatFileParser.INT_TYPE) ||

                   current.equalsIgnoreCase(FlatFileParser.LONG_TYPE) ||
                   current.equalsIgnoreCase(FlatFileParser.SHORT_TYPE) ||
                   current.equalsIgnoreCase(FlatFileParser.STRING_TYPE))
                 return true;
             }//for

             return false;

           }//checkTypeRow

           public void doit() throws Exception{
         cw = (ConnectionWrapper)pullInput(0);
         vt = (Table)pullInput(1);

         //validation tests...

         if(tableName == null || tableName.length() == 0)
           throw new Exception(this.getAlias()+  ": Table name is missing or was not set properly. " +
                               "Before running headless set the table name property using the "+
                               "properties editor, or through running this itinerary with GUI.");
         if(isOracle != !isSql)
           throw new Exception(this.getAlias() + " was configured incorrectly. " +
                               "Property SQL Server must be different that Oracle Server." +
                               " Please reconfigure the module using the properties editor.");

         // check the first row in the data table. If the first row contains
         // the strings of data type, the user has not set the property "typeRow"
         // correctly in data loading.
           if(checkTypeRow())
             throw new Exception (this.getAlias()+ ": The data table has problems. You did not set the property 'typeRow' " +
               "correctly for reading data.");

         //checking the the table is not larger than allowed.

         if (vt.getNumRows() >= maxDataRow)
             throw new Exception (this.getAlias()+ ": There are more than " + maxDataRow + " rows to load. For more " +
                "efficient data loading, please use " +
                "the utility the database vendor provides.");


    //checking if the table is in database (in case of appending)
    //or not in db, in case of create new table
    String[] types = {"TABLE"};
    Connection con = cw.getConnection();
    DatabaseMetaData metadata = con.getMetaData();
    ResultSet tableNames = metadata.getTables(null,"%","%",types);
    boolean found = false;
    while (tableNames.next()) {
      String aTable = tableNames.getString("TABLE_NAME");
      if(createNewTable && aTable.equalsIgnoreCase(tableName))
        throw new Exception(this.getAlias()+ ": A table named " + tableName + " already exists in " +
      "the database. Cannot create new table with this name.");
      if(!createNewTable && aTable.equalsIgnoreCase(tableName)) found = true;

      }//while

      if(!found && !createNewTable)
        throw new Exception(this.getAlias()+ ": Table " + tableName + " was not found in the database. " +
                            "Cannot append the table content.");

        //end validation tests.

        //allocating and populating ColumnMaxLength.
        ColumnMaxLength = new String[vt.getNumColumns()];
        for (int i=0; i<ColumnMaxLength.length; i++)
          ColumnMaxLength[i] = getMaxLength(i);

          //if this module is to create a new table
         if(createNewTable){
           //creating the table in the data base
           if (doCreateTableHeadLess())
             //if creation succeeded inserting the data.
             doInsertTableHeadless();
         }

         //if this module is to append the table to an existing one
        else{
           int numDBColumns =  getDBTableDefHeadless();
           //validting that the number of columns is equal.
           if (numDBColumns == vt.getNumColumns()) {
             //validating matching between types and lengths of columns.
             boolean pass = doValidateHeadless(dbTableDef);
             if (pass)
               //inserting data (appending)
               doInsertTableHeadless();
           }
           else{
             System.out.println(this.getAlias()+ ": The number of columns does not match. Data cannot be appended.\n");
           }
        }//else

       }//doit


       /** verify the data type and length of the vertical table suitable to
             insert into database
             @param dbTable The JTable keeping the structure of the database table
             @return The validation result (pass or fail)
         */
         protected boolean doValidateHeadless(JTable dbTable) throws Exception{
           int dbRowCnt;
         //  int vtRowCnt;
           for (dbRowCnt=0; dbRowCnt<dbTable.getRowCount(); dbRowCnt++) {
             if (dbTable.getValueAt(dbRowCnt,0).equals(NOTHING))
               break;
           };




           //for each column, comparing types and length.
           for (int i = 0; i < vt.getNumColumns(); i++) {

             //iType is the type of column i in the vertical table.
             int iType = vt.getColumnType(i);
             //type1.toString().toLowerCase().indexOf("varchar")>=0)

             //sType is the type of column i in the db table.
             String sType = dbTable.getValueAt(i,1).toString().toLowerCase();

             switch(iType){
               case ColumnTypes.BYTE_ARRAY:
               case ColumnTypes.CHAR_ARRAY:
               case ColumnTypes.STRING:
               case ColumnTypes.BOOLEAN:
                 //added support for all types of column. double check this (vered)
               case ColumnTypes.BYTE:
               case ColumnTypes.CHAR:
               case ColumnTypes.OBJECT:
                  if( !( sType.indexOf("varchar") >= 0)){


                    System.out.println(this.getAlias()+ ": The data type of column " + (i+1) +
                     " does not match. Data cannot be " + "appended.\n");

                    return false;
                  }
                  break;
                case ColumnTypes.DOUBLE:
                case ColumnTypes.INTEGER:
                 case ColumnTypes.FLOAT:
                 case ColumnTypes.SHORT:
                case ColumnTypes.LONG: if(!ColumnTypes.isContainNumeric(sType)){

                    System.out.println(this.getAlias()+ ": The data type of column " + (i+1) +
                     " does not match. Data cannot be " + "appended.\n");
                     return false;
                  }
                  break;

              //    default: return false; no support for defualt since all types of columns are supported.
             }

             //compare length of columns.
             if(!(ColumnMaxLength[i].equals("0"))) {
               if (Integer.valueOf(dbTable.getValueAt(i, 2).toString()).intValue() <
                   Integer.valueOf(ColumnMaxLength[i]).intValue()){
                 System.out.println(this.getAlias()+ ": The length of column " + (i+1) +
                     " does not match. Data cannot be " + "appended.\n");
                 return false;
               }//inner if - comparing length
             }//outer if, checking if to compare at all.


           }//for
           return (true);
         }//doValidateHeadless



       /** create a database table based on user's settings (createNewtable is true
        * and the name. creates only the table
        * without any data in it.
         *  @return true if the table is created successfully, otherwise false
         */
        protected boolean doCreateTableHeadLess () {
          try {
              System.out.println("TableName is: " + getTableName());

         /*     if (tableName == null || tableName.length() == 0 )
                   {
                System.out.println("\n\nSavefileToDB:\n" +
                "Table name is missing. Table cannot be created\n\n");
                return (false);
              }*/

              String sb = new String("create table " + tableName +
                          " (");
              int i = 0;
              for (i=0; i<vt.getNumColumns(); i++) {


                if (ColumnMaxLength[i] == null || ColumnMaxLength[i].equals("0")){
                  System.out.println(this.getAlias()+
                        ": Column length is not provided for column no. " + i+1 + ". Table cannot be created.");
                  return false;
                }


                /* s1 is column name */
                String s1 = vt.getColumnLabel(i);
                if (s1.length()>0) {
                  if (i > 0) // add "," between columns definitions
                    sb = sb + ",";
                  String colLabel = vt.getColumnLabel(i);
                  colLabel = colLabel.replace('-', '_');
                  sb = sb + colLabel + " ";

                  //sb = sb + vt.getColumnLabel(i) + " ";


                /* type is the column type*/
                int type = vt.getColumnType(i);



                switch(type){
                  case ColumnTypes.STRING:
                  case ColumnTypes.BYTE_ARRAY:
                  //added support for all types of column. double check this.
                  case ColumnTypes.BYTE:
                  case ColumnTypes.CHAR:
                  case ColumnTypes.OBJECT:

                  case ColumnTypes.CHAR_ARRAY: sb = sb + "varchar(" + ColumnMaxLength[i]+")";
                    break;
                  case ColumnTypes.INTEGER:
                  case ColumnTypes.LONG:
                  case ColumnTypes.SHORT: sb = sb + "numeric";
                    break;
                  case ColumnTypes.FLOAT:
                    case ColumnTypes.DOUBLE:
                      sb = sb + number /* supporting also oracle -vered. "numeric"*/;

                    break;
                  case ColumnTypes.BOOLEAN: sb = sb + "varchar(" + "5)";
                    break;
                    default:   System.out.println(this.getAlias()+
                        ": Invalid data type. Table cannot be created");
                        return (false);
                }//switch

                } /* end of if (s1.length() > 0) */
                else
                  break;
              } /* end for */

              sb = sb + ")";



              Connection con = cw.getConnection ();
              Statement stmt = con.createStatement ();
              stmt.executeUpdate(sb);
              //ResultSet result = stmt.executeQuery(sb);
              stmt.close();
              System.out.println(this.getAlias()+ ": Table " + tableName + " has been created");
           }
           catch (Exception e){
             e.printStackTrace();
              System.out.println(this.getAlias()+
                                 ": Error occurred in doCreateTableHeadLess.");
               return (false);
          }
          return (true);
        }//doCreateTableHeadLess



        /** insert data from a vertical table into a database table
           */
           protected void doInsertTableHeadless ( ) throws Exception {
             int rowIdx = 0;
             int colIdx = 0;
             /* insert data */
             try {
               Connection con = cw.getConnection ();
               Statement stmt;
               for(rowIdx = 0; rowIdx < vt.getNumRows(); rowIdx++) {
                 String sb = new String("insert into " + tableName +
                               " values (");
                 for(colIdx = 0; colIdx < vt.getNumColumns(); colIdx++) {
                   /* find the data type */
                  // String colType = vTableDef.getValueAt(colIdx,1).toString();
                  int type = vt.getColumnType(colIdx);
                  String s = " ";
                  if(!vt.isValueMissing(rowIdx, colIdx))
                    s = vt.getString(rowIdx, colIdx);
                  //String s = vt.getString(rowIdx, colIdx);
                  else
                    s = "null";

                   if (colIdx > 0) /* add "," between columns */
                     sb = sb + ",";
                   if (type==ColumnTypes.STRING || type==ColumnTypes.BYTE_ARRAY ||
                       type==ColumnTypes.CHAR_ARRAY  || type==ColumnTypes.BOOLEAN ) {
                     if(!vt.isValueMissing(rowIdx, colIdx))
                       sb = sb + "'" + s + "'";
                     else
                       sb = sb + s;
                   }
                   else {
                     sb = sb + s;
                   }
                 } /* end of for colIdx */
                 sb = sb + ")";
                 stmt = con.createStatement ();
                 stmt.executeUpdate(sb);
                 stmt.close();
               } /* end of for rowIdx */

               System.out.println(this.getAlias()+ ": nData has been loaded.\n\n");

             }
             catch (Exception e){
               e.printStackTrace();
               throw new Exception(this.getAlias()+ ": Error occurred in doInsertTableHeadless.");

             }
           }//doInsertTableHeadless





   /** get the structure of a database table.
    * @return the actual number of rows in <code>dbTableDef</code>. which is
    * the number of columns in the table in the data base.
     */
     protected int getDBTableDefHeadless() throws Exception {

         Connection con = cw.getConnection ();
         DatabaseMetaData metadata = con.getMetaData();


         dbModel = new MetaTableModel(maxNumRow,3,false);
         dbTableDef = new JTable(dbModel);

         String[] names = {"TABLE"};
         ResultSet tableNames = metadata.getTables(null,"%",tableName.toUpperCase(),names);
          int rIdx = 0;



         while (tableNames.next()) {
           ResultSet columns = metadata.getColumns(null,"%",tableNames.getString("TABLE_NAME"),"%");

           while (columns.next()) {

             String columnName = columns.getString("COLUMN_NAME");
             String dataType = columns.getString("TYPE_NAME");
             int columnSize = columns.getInt("COLUMN_SIZE");
             dbTableDef.setValueAt(columnName,rIdx,0);
             dbTableDef.setValueAt(dataType,rIdx,1);
             dbTableDef.setValueAt(Integer.toString(columnSize),rIdx,2);
             rIdx++;
           }//while columns

           /* if rIdx == 0, that indicate the tableSet is empty, the db table does not exist */
           if (rIdx == 0) {
             System.out.println(this.getAlias()+ ": Table " + tableName + " does not exist.");
           }//if rIdx
         }//while tableNames

        return rIdx;

     }//getDBTableDefHeadless




//conversion headless

//in order to supprt oracle data bases as sql
     private boolean isOracle;
     private boolean isSql;

     public boolean getIsOracle(){return isOracle;}
     public boolean getIsSql(){return isSql;}

     public void setIsOracle(boolean val){isOracle = val;}
     public void setIsSql(boolean val){isSql = val;}


     //public static final String SQL_NUMERIC = "numeric";
     public static final String SQL_NUMBER = "numeric";
     //public static final String ORACLE_NUMERIC = "numeric";
     public static final String ORACLE_NUMBER = "number";


     protected String number;
     public void beginExecution(){
       if(isOracle) number = ORACLE_NUMBER;
       else number = SQL_NUMBER;

     }


}

// QA Comments Anca 03/12/03
// changed the input from TableImpl to Table to be more generic
// WISH : to be able select only certain columns to be loaded when a new database table is created.
// or else column names should not be editable/deletable when creating a new table
//

