package ncsa.d2k.modules.core.io.sql;

/**
 * <p>Title: SQLChooseAttributes </p>
 * <p>Description: Choose input and output fields from a database table,
 *    then create an ExampleTableImpl to keep the meta data </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: NCSA ALG </p>
 * @author Dora Cai
 * @version 1.0
 *
 * @todo: behavior of this module is different than ChooseAttributes. which puts
 * no restriction on the selection that the user makes. shouldn't these modules
 * be aligned?
 */



import ncsa.d2k.core.modules.*;
import ncsa.d2k.core.modules.UserView;
import ncsa.d2k.userviews.swing.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.gui.Constrain;
import java.sql.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import ncsa.d2k.modules.core.transform.StaticMethods;


public class SQLChooseAttributes extends HeadlessUIModule {
  JOptionPane msgBoard = new JOptionPane();
  ConnectionWrapper cw;
  String tblName;
  Connection con;
  // ArrayList for column names
  ArrayList colNames;
  // ArrayList for column types
  ArrayList colTypes;

  private JTextField tableName;
  private JButton tableBrowseBtn;
  private JButton abort;
  private JButton done;
  private JList inputList;
  private JList outputList;

  private JLabel inputLabel;
  private JLabel outputLabel;

  private HashMap inputToIndexMap;
  private HashMap outputToIndexMap;

  private int [] inputFeatures;
  private int [] outputFeatures;

  private int [] selectedInput;
  private int [] selectedOutput;

  private ExampleTable et;

  public SQLChooseAttributes() {
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0: return "The name of the database table.";
      case 1: return "The meta table built from the data table.";
      default: return "No such output";
    }
  }

  public String getInputInfo (int i) {
    switch (i) {
      case 0: return "JDBC data source to make database connection.";
      case 1: return "The name of the cube table.";
      default: return "No such input";
    }
  }

  public String getInputName(int i) {
    switch (i) {
      case 0: return "Database Connection";
      case 1: return "Data Table Name";
      default: return "NoInput";
    }
  }

  public String getModuleInfo () {
      String s = "<p> Overview: ";
      s += "This module allows a user to choose input and output attributes ";
      s += "from a database table. </p>";
      s += "<p> Detailed Description: ";
      s += "This module first connects to a database, retrieves the list of attributes ";
      s += "for a selected table, and then allows a user to choose the input and output ";
      s += "attributes from the list. The user must choose at least one input and one ";
      s+= "attribute and <u>at most</u> one output attribute. ";
      s+= "Based on user's selection, this module creates a meta ";
      s += "table to store the meta information (such as attribute name, data type, ";
      s += "and number of rows in the data set), and pass on the information to the ";
      s += "next module. The meta table created by this module  does not contain the ";
      s += "real data. The real data can be retrieved from the database table ";
      s += "by other modules. </p>";
      s += "<p> Restrictions: ";
      s += "We currently only support Oracle, SQLServer, DB2 and MySql databases. </p> ";

      return s;

    }

  public String getModuleName() {
    return "SQL Choose Attributes";
  }

  public String[] getInputTypes () {
                String[] types = {"ncsa.d2k.modules.core.io.sql.ConnectionWrapper","java.lang.String"};
                return types;
        }

  public String[] getOutputTypes () {
                String[] types = {"java.lang.String","ncsa.d2k.modules.core.datatype.table.ExampleTable"};
                return types;
        }

  public String getOutputName(int i) {
    switch (i) {
      case 0: return "DatabaseTableName";
      case 1: return "MetaTable";
      default: return "NoOutput";
    }
  }

  protected String[] getFieldNameMapping () {
    return null;
  }

//conversion toheadless - vered: super class already implements this method.
  /*public PropertyDescription[] getPropertiesDescriptions() {
    return new PropertyDescription[0];
  }*/

  /**
    Create the UserView object for this module-view combination.
    @return The UserView associated with this module.
  */
  protected UserView createUserView() {
    return new GetFieldsView();
  }

  public class GetFieldsView extends JUserPane
    implements ActionListener {

    public void setInput(Object input, int index) {
      if (index == 0) {
        removeAll();
        cw = (ConnectionWrapper)input;
      }
      else if (index == 1) {
        tblName = (String)input;
        addComponents();
      }
    }

    public Dimension getPreferredSize() {
      return new Dimension (400, 300);
    }

    public void initView(ViewModule mod) {
    }


    /**
      add all the components
    */
    private void addComponents() {
      JPanel tableList = new JPanel();
      JPanel back = new JPanel();
      colNames = new ArrayList();
      colTypes = new ArrayList();
      DatabaseMetaData metadata = null;

      tableList.setLayout(new GridBagLayout());
      Constrain.setConstraints(tableList, new JLabel("Table Name"),
        0,0,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,1,1);
      Constrain.setConstraints(tableList, tableName = new JTextField(5),
        1,0,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,2,1);
      tableName.setText(tblName);
      tableName.addActionListener(this);
      tableName.setEditable(false);
      Constrain.setConstraints(tableList, new JLabel(" "),
        3,0,1,1,GridBagConstraints.NONE, GridBagConstraints.WEST,1,1);
      //Constrain.setConstraints(tableList, tableBrowseBtn = new JButton ("Browse"),
        //3,0,1,1,GridBagConstraints.NONE, GridBagConstraints.WEST,1,1);
      //tableBrowseBtn.addActionListener(this);


      inputList=new JList();
      outputList=new JList();

      inputLabel=new JLabel("Input Columns");
      inputLabel.setHorizontalAlignment(SwingConstants.CENTER);

      outputLabel=new JLabel("Output Columns");
      outputLabel.setHorizontalAlignment(SwingConstants.CENTER);

      try {
        con = cw.getConnection();
        metadata = con.getMetaData();
        String[] names = {"TABLE"};
        ResultSet tableNames = metadata.getTables(null,"%",tableName.getText(),names);
        while (tableNames.next()) {
          ResultSet columns = metadata.getColumns(null,"%",tableNames.getString("TABLE_NAME"),"%");
          while (columns.next()) {
            String columnName = columns.getString("COLUMN_NAME");
            String columnType = columns.getString("TYPE_NAME");
            //colNames.add(columnName.toLowerCase());
            //colTypes.add(columnType.toLowerCase());
            colNames.add(columnName);
            colTypes.add(columnType);
          }
        }
      }
      catch (Exception e) {
        JOptionPane.showMessageDialog(msgBoard,
        e.getMessage(), "Error",
        JOptionPane.ERROR_MESSAGE);
        System.out.println("Error occurred in addComponents.");
      }
      DefaultListModel dlm = new DefaultListModel();
      for(int i = 0; i < colNames.size(); i++)
        dlm.addElement(colNames.get(i).toString());
      inputList.setModel(dlm);
      dlm = new DefaultListModel();
      for(int i = 0; i < colNames.size(); i++)
        dlm.addElement(colNames.get(i).toString());
      outputList.setModel(dlm);
      JScrollPane leftScrollPane=new JScrollPane(inputList);
      JScrollPane rightScrollPane=new JScrollPane(outputList);
      orderedLabels();


      back.setLayout(new GridBagLayout());

      Constrain.setConstraints(back, inputLabel, 0, 0, 1, 1,
      GridBagConstraints.BOTH, GridBagConstraints.CENTER, 0, 0);
      Constrain.setConstraints(back, outputLabel, 1, 0, 1, 1,
      GridBagConstraints.BOTH, GridBagConstraints.CENTER, 0, 0);
      Constrain.setConstraints(back, leftScrollPane, 0, 1, 1, 1,
      GridBagConstraints.BOTH, GridBagConstraints.CENTER, 1, 1);
      Constrain.setConstraints(back, rightScrollPane, 1, 1, 1, 1,
      GridBagConstraints.BOTH, GridBagConstraints.CENTER, 1, 1);

      JPanel buttons = new JPanel();
      abort = new JButton("Abort");
      done = new JButton("Done");
      abort.addActionListener(this);
      done.addActionListener(this);
      buttons.add(abort);
      buttons.add(done);

      this.add(tableList, BorderLayout.NORTH);
      this.add(back, BorderLayout.CENTER);
      this.add(buttons, BorderLayout.SOUTH);

    }


    private void orderedLabels() {
      inputToIndexMap = new HashMap(colNames.size());
      outputToIndexMap = new HashMap(colNames.size());
      for(int i = 0; i < colNames.size(); i++) {
        inputToIndexMap.put(colNames.get(i).toString(), new Integer(i));
        outputToIndexMap.put(colNames.get(i).toString(), new Integer(i));
      }
    }

    /**
      listen for ActionEvents
    */
    public void actionPerformed(ActionEvent e) {
      Object src = e.getSource();
      if(src == abort)
        closeIt();
      else if (src == done) {
        if (checkChoices()) {
          setFieldsInTable();
          et = createMetaTable();
          if (et != null) {
            //printETInfo();
            pushOutput(tableName.getText(),0);
            pushOutput(et, 1);
            closeIt();
          }
        }
      }
   }

    /**
      Make sure all choices are valid.
    */
    protected boolean checkChoices() {
      if(outputList.getSelectedIndex() == -1){
        JOptionPane.showMessageDialog(this,
        "You must select at least one output",
        "Error", JOptionPane.ERROR_MESSAGE);
        return false;
      }
      if(outputList.getSelectedIndices().length > 1) {
        JOptionPane.showMessageDialog(this,
        "You can only select one output column.",
        "Error", JOptionPane.ERROR_MESSAGE);
        return false;
      }
      if(inputList.getSelectedIndex() == -1){
        JOptionPane.showMessageDialog(this,
        "You must select at least one input",
        "Error", JOptionPane.ERROR_MESSAGE);
        return false;
      }
      return true;
    }

    private void setFieldsInTable(){
      Object[] selected = inputList.getSelectedValues();
      inputFeatures = new int[selected.length]; // Store the column index of GUI list for input features
      selectedInput = new int[selected.length]; // Store the column index of Example Table for input features
      for(int i = 0; i < selected.length; i++) {
        String s = (String)selected[i];
        Integer ii = (Integer)inputToIndexMap.get(s);
        inputFeatures[i] = ii.intValue();
      }



      setSelectedInputNames(selected); //healdess conversion support


      selected = outputList.getSelectedValues();
      outputFeatures = new int[selected.length]; // Store the column index of GUI list for output features
      selectedOutput = new int[selected.length]; // Store the column index of Example Table for output features
      for(int i = 0; i < selected.length; i++) {
        String s = (String)selected[i];
        Integer ii = (Integer)outputToIndexMap.get(s);
        outputFeatures[i] = ii.intValue();
      }



      setSelectedOutputNames(selected); //healdess conversion support

    }
  }

  /** create an ExampleTable object to hold the meta information.
   *  @return an object of Example table
   */
  private ExampleTable createMetaTable() {
    int selectedColumn = 0;
    int numRows = 0;
    // only include the selected columns in the example table
    Column[] cols = new Column[inputFeatures.length + outputFeatures.length];
    selectedColumn = 0;
    int inputIndex = 0;
    int outputIndex = 0;
    for (int colIdx = 0; colIdx < colNames.size(); colIdx++) {
      if (isInList(colIdx, inputFeatures) && isInList(colIdx, outputFeatures)) {
        JOptionPane.showMessageDialog(msgBoard,
                  "A column can be either an input or an output, but not both.", "Error",
                  JOptionPane.ERROR_MESSAGE);
        return null;
      }
      if (isInList(colIdx, inputFeatures) || isInList(colIdx, outputFeatures)) {
        cols[selectedColumn] = new ObjectColumn();
        cols[selectedColumn].setLabel(colNames.get(colIdx).toString());
        // data type may be in uppercase or lowercase
        if(ColumnTypes.isEqualNumeric(colTypes.get(colIdx).toString())) {
          cols[selectedColumn].setIsScalar(true);
        }
        else {
          cols[selectedColumn].setIsScalar(false);
        }
        if (isInList(colIdx, inputFeatures)) {
          selectedInput[inputIndex] = selectedColumn;
          inputIndex ++;
        }
        else if (isInList(colIdx, outputFeatures)) {
          selectedOutput[outputIndex] = selectedColumn;
          outputIndex ++;
        }
        selectedColumn ++;
      }
    }
    // create an Table to hold the meta data
    MutableTableImpl table = new MutableTableImpl(cols);
    for (int colIdx = 0; colIdx < selectedColumn; colIdx++) {
      if (cols[colIdx].getIsScalar()) {
        table.setColumnIsScalar(true,colIdx);
        table.setColumnIsNominal(false,colIdx);
      }
      else {
        table.setColumnIsScalar(false,colIdx);
        table.setColumnIsNominal(true,colIdx);
      }
    }
    // get the count of the rows that have class labels
    try {
      con = cw.getConnection();
      String countQry = new String("select count(*) from " + tableName.getText() +
                        " where " + outputList.getSelectedValue().toString() + " is not null");
      Statement countStmt = con.createStatement();
      ResultSet countSet = countStmt.executeQuery(countQry);
      countSet.next();
      numRows = countSet.getInt(1);
    }
    catch (Exception e){
      JOptionPane.showMessageDialog(msgBoard,
                e.getMessage(), "Error",
                JOptionPane.ERROR_MESSAGE);
      System.out.println("Error occurred in createExampleTable.");
    }
   // table.setNumRows(numRows);
   table.addRows(numRows);
    ExampleTable et = table.toExampleTable();
    et.setInputFeatures(selectedInput);
    et.setOutputFeatures(selectedOutput);

    return et;
  }

  /** return ture is aColumn is in columns
   *  @param aColumn the index of the column
   *  @param columns the list of indexes of the columns
   *  @return true or false
   */
  private boolean isInList(int aColumn, int[] columns) {
    for (int colIdx=0; colIdx<columns.length; colIdx++) {
      if (aColumn == columns[colIdx])
        return true;
    }
    return false;
  }

  /** print ExampleTableImpl for debug
   *
   */
  private void printETInfo() {
    int[] input;
    int[] output;
    input = et.getInputFeatures();
    System.out.println("input feature list: ");
    for (int i = 0; i < input.length; i++) {
      System.out.print(input[i] + ", ");
    }
    System.out.println(" ");
    output = et.getOutputFeatures();
    System.out.println("output feature list: ");
    System.out.println(output[0]);
    System.out.println(" ");
    //int numRows = et.getNumEntries();
	int numRows = et.getNumRows();
    System.out.println("numRows is " + numRows);

    System.out.println("data type list: ");
    for (int j = 0; j < et.getNumColumns(); j++) {
        System.out.println("col " + j + " type: " + et.isColumnScalar(j));
    }
  }

  protected void closeIt() {
    inputList.removeAll();
    outputList.removeAll();
    executionManager.moduleDone(this);
  }

  //headless conversion

  /* selected input and output names are the names of columns selected by the
   user through the gui, as input and output, and were saved as properties of this
   module.*/
  private String[] selectedInputNames;
  private String[] selectedOutputNames;
  //getter methods.
  public Object[] getSelectedInputNames(){return selectedInputNames;}
  public Object[] getSelectedOutputNames(){return selectedOutputNames;}

  //setter methods

  public void setSelectedInputNames(Object[] ins){
  selectedInputNames = new String[ins.length];
  for (int i=0; i<ins.length; i++)
    selectedInputNames[i] = (String) ins[i];
 }//setSelectedInputNames

  public void setSelectedOutputNames(Object[] outs){
    selectedOutputNames = new String[outs.length];
    for (int i=0; i<outs.length; i++)
      selectedOutputNames[i] = (String) outs[i];
  }//setSelectedOutputNames



  public void doit() throws Exception{
    cw = (ConnectionWrapper)pullInput(0);
    String _table = (String)pullInput(1);

    //validating table's name
    if(_table == null || _table.length() ==0)
      throw new Exception(getAlias() + ": the input Data Table Name is invalid.");

    //validating that inputs and outputs were selected on previous run
    if(selectedInputNames == null || selectedOutputNames == null ||
       selectedOutputNames.length == 0 || selectedInputNames.length == 0)
      throw new Exception (this.getAlias()+" has not been configured correctly. Before running headless, run with the gui and configure the parameters.");


    con = cw.getConnection();
    DatabaseMetaData metadata = con.getMetaData();


     ResultSet columns = metadata.getColumns(null,"%",_table,"%");

     //will hold column name <-> column index
     HashMap availableColumnMap = new HashMap ();
     //will hold column index <-> column name
     HashMap Id2ColumnNameMap = new HashMap();
     //will hold column index <-> column type.
     HashMap columTypes = new HashMap();


     //populating the hash maps.
     int counter = 0;   //counter is the current column id.
     while (columns.next()) {
       String columnName = columns.getString("COLUMN_NAME");
       String columnType = columns.getString("TYPE_NAME");
       availableColumnMap.put( columnName.toUpperCase(), new Integer(counter));
       Id2ColumnNameMap.put(new Integer(counter), columnName.toUpperCase());
       columTypes.put(new Integer(counter), columnName.toUpperCase());
       counter++;
     }//while column


     if(counter == 0)
       throw new Exception(getAlias() +": Table " + _table + " is either not in the database or has no columns in it");




//the following line was commented out and replaced by calling to a static method.
 //     inputFeatures = getTargetColumns(availableColumnMap, selectedInputNames);
        inputFeatures = StaticMethods.getIntersectIds(selectedInputNames, availableColumnMap);

      if(inputFeatures.length < selectedInputNames.length)
        throw new Exception(getAlias() +": Some of the configured input names " +
                            "are not in the selected table. Cannot proceed without any valid input names."+
                                 " Please reconfigure the module so it can run headless.");

      /* selectedInput holds the input columns ids, of the output example table.*/
      selectedInput = new int[inputFeatures.length];





      //since there can be only one output feature...
//      outputFeatures = getTargetColumns(availableColumnMap, selectedOutputNames);
  /* outputfeatures holds the target output column id, as it is in the
       table in the database.*/
      outputFeatures = new int[1];
      /* selectedOutput holds the output columns ids, of the output example table.*/
      selectedOutput = new int[outputFeatures.length];


      String finalSelectedOutput = null;

        if (availableColumnMap.containsKey(selectedOutputNames[0].toUpperCase())) {
          outputFeatures[0] = ( (Integer) availableColumnMap.get(
              selectedOutputNames[0].toUpperCase())).intValue();
          finalSelectedOutput = selectedOutputNames[0];
        }
        else throw new Exception (getAlias() +": The selected output " +
                                  "feature is not available in the data base table. cannot proceed.\n"+
                                 " Please reconfigure the module so it can run headless.");


    int selectedColumn = 0;  //current column in the output example table.
      int numRows = 0;       //number of rows in the output example table
      // only include the selected columns in the example table
      //will hold all the columns in the output example table.
      Column[] cols = new Column[inputFeatures.length + outputFeatures.length];
//      selectedColumn = 0;
//      int inputIndex = 0;
 //     int outputIndex = 0;

  //    int colIdx = 0;
      //Iterator it = availableColumnMap.keySet().iterator();
      String currentColName;  //name of currently created object column of the output exmaple table

      //creating the input columns.
      for (int i = 0; i < inputFeatures.length; i++) {

         currentColName = (String) Id2ColumnNameMap.get(new Integer(inputFeatures[i]));
         cols[selectedColumn] = new ObjectColumn();
        cols[selectedColumn].setLabel(currentColName);

        String type = (String) columTypes.get(new Integer(inputFeatures[i]));
        if(ColumnTypes.isEqualNumeric(type))
          cols[selectedColumn].setIsScalar(true);
        else
          cols[selectedColumn].setIsScalar(false);

      selectedInput[i] = selectedColumn;
      selectedColumn++;

      }//for i, input features

      //creating the output columns.
      for (int i = 0; i < outputFeatures.length; i++) {
      //while(it.hasNext()){
         currentColName = (String) Id2ColumnNameMap.get(new Integer(outputFeatures[i]));
         cols[selectedColumn] = new ObjectColumn();
        cols[selectedColumn].setLabel(currentColName);

        String type = (String) columTypes.get(new Integer(outputFeatures[i]));
        if(ColumnTypes.isEqualNumeric(type))
         cols[selectedColumn].setIsScalar(true);
       else
         cols[selectedColumn].setIsScalar(false);
       selectedOutput[i] = selectedColumn;
       selectedColumn++;


      }//for i output features



    // create a Table to hold the meta data
    MutableTableImpl table = new MutableTableImpl(cols);
    for (int colIdx = 0; colIdx < selectedColumn; colIdx++) {
      if (cols[colIdx].getIsScalar()) {
        table.setColumnIsScalar(true,colIdx);
        table.setColumnIsNominal(false,colIdx);
      }
      else {
        table.setColumnIsScalar(false,colIdx);
        table.setColumnIsNominal(true,colIdx);
      }
    }


    // get the count of the rows that have class labels
    try {
     // con = cw.getConnection();

     /*this handles only single output column. not multy labeling.*/
      String countQry = new String("select count(*) from " + _table +
                        " where " + finalSelectedOutput + " is not null");
      Statement countStmt = con.createStatement();
      ResultSet countSet = countStmt.executeQuery(countQry);
      countSet.next();
      numRows = countSet.getInt(1);
    }
    catch (Exception e){

      System.out.println(getAlias() +": Error occurred in createExampleTable.");
      throw e;
    }
   // table.setNumRows(numRows);



   //adding rows.
    table.addRows(numRows);
    //converting to example table and setting the input and output columns.
    ExampleTable et = table.toExampleTable();
    et.setInputFeatures(selectedInput);
    et.setOutputFeatures(selectedOutput);


    pushOutput(_table,0);
    pushOutput(et, 1);


  }//doit

  /**
   * for each <codE>desired[i]</code> that is a key in <codE>available</code>
   * putting th mapped index in <code>available</code> in the returned value.
   * @param available maps column name to column index
   * @param desired   names of selected columns by the user (through gui)
   * @return          int[] with indices of columns that their names are both in
   *                  <code>available</code> and <code>desired</code>.
   *                  the indices are the mapped values in <codE>available</code>.
   */
  /*private int[] getTargetColumns(HashMap available, String[] desired){
    int[] retVal;
    //boolean[] isTarget = new boolean[desired.length];
    int count = 0;

    for(int i=0; i<desired.length; i++)
      if (available.containsKey(desired[i]))
        count++;
      else System.out.println("\n\nSQLChooseAttributes:\n The chosen table does not contain a column named " + desired[i] +
                              ". This column won't be included in the output of this module.\n");


    retVal = new int[count];
    for(int i=0, j=0; i<desired.length; i++)
      if(available.containsKey(desired[i])){
        retVal[j] = ( (Integer) available.get(desired[i])).intValue();
        j++;
      }//if



    return retVal;

  }*/
  //headless conversion

}
