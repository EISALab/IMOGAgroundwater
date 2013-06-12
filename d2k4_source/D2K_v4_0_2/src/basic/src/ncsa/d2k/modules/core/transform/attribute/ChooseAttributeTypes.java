package ncsa.d2k.modules.core.transform.attribute;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.userviews.swing.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.gui.*;
import ncsa.gui.ErrorDialog;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

import ncsa.d2k.modules.core.transform.StaticMethods;

/**
 Allows the user to choose which columns of a table are scalar or nominal.
 */
public class ChooseAttributeTypes extends HeadlessUIModule {

  /**
   Return a description of the function of this module.
   @return A description of this module.
   */
  public String getModuleInfo() {
    String info = "<p>Overview: ";
    info += "This module allows the user to choose which columns/attributes of a table are scalar or nominal.";
    info += "</p><p>Detailed Description: ";
    info += "This module outputs a Table with the attributes assigned to be either scalar or nominal. The user has the power to mark a numeric (e.g double) attribute as nominal and a string type attribute as scalar. Extreme care must be exercised when making these assignments, because the learning algorithms use these assignments.  ";
    info += "</p><p>Data Handling: ";
    info += "This module does not modify the data in the table, but it does assign attributes to be either scalar or nominal.";
    return info;
  }

  /**
   Return the name of this module.
   @return The name of this module.
   */
  public String getModuleName() {
    return "Choose Attribute Types";
  }

  /**
   Return a String array containing the datatypes
   the inputs to this module.
   @return The datatypes of the inputs.
   */
  public String[] getInputTypes() {
    String[] types = {"ncsa.d2k.modules.core.datatype.table.Table"};
    return types;
  }

  /**
   Return a String array containing the datatypes of
   the outputs of this module.
   @return The datatypes of the outputs.
   */
  public String[] getOutputTypes() {
    String[] types = {"ncsa.d2k.modules.core.datatype.table.Table"};
    return types;
  }

  /**
   Return a description of a specific input.
   @param i The index of the input
   @return The description of the input
   */
  public String getInputInfo(int i) {
    switch (i) {
      case 0: return "The table to choose field types from.";
      default: return "No such input";
    }
  }

  /**
   Return the name of a specific input.
   @param i The index of the input.
   @return The name of the input
   */
  public String getInputName(int i) {
    if(i == 0)
      return "Table";
    else
      return "No such input";
  }

  /**
   Return the description of a specific output.
   @param i The index of the output.
   @return The description of the output.
   */
  public String getOutputInfo(int i) {
    switch (i) {
      case 0: return "The Table with nominal and scalar types assigned.";
      default: return "No such output";
    }
  }

  /**
   Return the name of a specific output.
   @param i The index of the output.
   @return The name of the output
   */
  public String getOutputName(int i) {
    if(i == 0)
      return "Table";
    else
      return "No such output";
  }


  /*  public PropertyDescription[] getPropertiesDescriptions() {
        return new PropertyDescription[0]; // so that "windowName" property
        // is invisible
    }*/


  /**
   Return the UserView
   @returns the UserView
   */
  protected UserView createUserView() {
    return new DefineView();
  }

  /**
   Not used
   */
  protected String[] getFieldNameMapping() {
    return null;
  }

  /**
   The user view class
   */
  private class DefineView extends JUserPane implements ActionListener {
    private Table table;

    private ChooseAttributeTypes module;
    private JButton abort;
    private JButton done;

    private JList scalarList;
    private JList nominalList;

    private HashMap indexLookup;

    private JCheckBoxMenuItem miColumnOrder;
    private JCheckBoxMenuItem miAlphaOrder;

    private JMenuBar menuBar;

    private DefaultListModel scalarListModel;
    private DefaultListModel nominalListModel;

    private JButton fromScalarToNom;
    private JButton fromNomToScalar;

    /**
     Initialize
     */
    public void initView(ViewModule v) {
      module = (ChooseAttributeTypes)v;
      abort = new JButton("Abort");
      done = new JButton("Done");
      abort.addActionListener(this);
      done.addActionListener(this);
      /*menuBar = new JMenuBar();
      JMenu m1 = new JMenu("File");
      miColumnOrder = new JCheckBoxMenuItem("Column Order");
      miColumnOrder.addActionListener(this);
      miColumnOrder.setState(true);
      miAlphaOrder = new JCheckBoxMenuItem("Alphabetical Order");
      miAlphaOrder.addActionListener(this);
      miAlphaOrder.setState(false);
      m1.add(miColumnOrder);
      m1.add(miAlphaOrder);
      menuBar.add(m1);
      */
      addComponents();
    }

    /*public Object getMenu() {
    return menuBar;
    }*/

    /**
     Called when inputs arrive
     */
    public void setInput(Object o, int id) {
      if(id == 0) {
        table = (Table)o;
        fillComponents();
      }
    }

    /**
     Make me at least this big
     */
    public Dimension getPreferredSize() {
      return new Dimension(400, 300);
    }

    /**
     Add all the components
     */
    private void addComponents() {
      JPanel back = new JPanel();

      scalarList=new JList();
      scalarListModel = new DefaultListModel();
      scalarList.setModel(scalarListModel);

      nominalList=new JList();
      nominalListModel = new DefaultListModel();
      nominalList.setModel(nominalListModel);

      JScrollPane leftScrollPane=new JScrollPane(scalarList);
      JScrollPane rightScrollPane=new JScrollPane(nominalList);

      JLabel inputLabel=new JLabel("Scalar Columns");
      inputLabel.setHorizontalAlignment(SwingConstants.CENTER);
      leftScrollPane.setColumnHeaderView(inputLabel);

      JLabel outputLabel=new JLabel("Nominal Columns");
      outputLabel.setHorizontalAlignment(SwingConstants.CENTER);
      rightScrollPane.setColumnHeaderView(outputLabel);

      fromScalarToNom = new JButton(">");
      fromScalarToNom.addActionListener(this);
      fromNomToScalar = new JButton("<");
      fromNomToScalar.addActionListener(this);

      /*Box b1 = new Box(BoxLayout.Y_AXIS);
      b1.add(b1.createGlue());
      b1.add(fromScalarToNom);
      b1.add(fromNomToScalar);
      b1.add(b1.createGlue());
      */
      JPanel b1 = new JPanel();
      b1.setLayout(new GridLayout(2,1));
      b1.add(fromScalarToNom);
      b1.add(fromNomToScalar);

      JPanel b2 = new JPanel();
      b2.add(b1);

      back.setLayout(new MirrorLayout(leftScrollPane, b2, rightScrollPane));
      back.add(leftScrollPane);
      back.add(b2);
      back.add(rightScrollPane);

      //back.setLayout(new GridBagLayout());

      //Constrain.setConstraints(back, leftScrollPane, 0, 0, 2, 1, GridBagConstraints.BOTH, GridBagConstraints.CENTER, .5, 1);
      //Constrain.setConstraints(back, b2, 2, 0, 1, 1, GridBagConstraints.NONE, GridBagConstraints.CENTER, 0, 0);
      //Constrain.setConstraints(back, rightScrollPane, 3, 0, 2, 1, GridBagConstraints.BOTH, GridBagConstraints.CENTER, .5, 1);

      JPanel buttons = new JPanel();
      buttons.add(abort);
      buttons.add(done);

      this.add(back, BorderLayout.CENTER);
      this.add(buttons, BorderLayout.SOUTH);
    }

    private void fillComponents() {
      indexLookup = new HashMap();
      scalarListModel.removeAllElements();
      nominalListModel.removeAllElements();

      LinkedList scalars = new LinkedList();
      LinkedList nominals = new LinkedList();
      for(int i = 0; i < table.getNumColumns(); i++) {
        String label = table.getColumnLabel(i);
        if (label.equals(""))
          label = "Column " + i;
        if(table.isColumnScalar(i))
          scalars.add(label);
        else
          nominals.add(label);
        indexLookup.put(label, new Integer(i));
      }

      for(int i = 0; i < scalars.size(); i++)
        scalarListModel.addElement(scalars.get(i));
      for(int i = 0; i < nominals.size(); i++)
        nominalListModel.addElement(nominals.get(i));
    }

    /**
     Listen for ActionEvents
     */
    public void actionPerformed(ActionEvent e) {
      Object src = e.getSource();
      if(src == abort)
        module.viewCancel();
      else if(src == done) {
        //if(checkChoices()) {
        setFieldsInTable();
        pushOutput(table, 0);
        viewDone("Done");
        //}
      }
      /*else if(src == miColumnOrder) {
      String [] labels = orderedLabels();
      miAlphaOrder.setState(false);
      DefaultListModel dlm = (DefaultListModel)inputList.getModel();
      dlm.removeAllElements();
      for(int i = 0; i < labels.length; i++) {
      dlm.addElement(labels[i]);
      }
      dlm = (DefaultListModel)outputList.getModel();
      dlm.removeAllElements();
      for(int i = 0; i < labels.length; i++) {
      dlm.addElement(labels[i]);
      }
      }
      else if(src == miAlphaOrder) {
      String [] labels = alphabetizeLabels();
      miColumnOrder.setState(false);
      DefaultListModel dlm = (DefaultListModel)inputList.getModel();
      dlm.removeAllElements();
      for(int i = 0; i < labels.length; i++) {
      dlm.addElement(labels[i]);
      }
      dlm = (DefaultListModel)outputList.getModel();
      dlm.removeAllElements();
      for(int i = 0; i < labels.length; i++) {
      dlm.addElement(labels[i]);
      }
      }*/
      else if(src == fromNomToScalar) {
        Object[] selected = nominalList.getSelectedValues();
        for(int i = 0; i< selected.length; i++) {
          boolean b = true;
          Integer idx = (Integer)indexLookup.get(selected[i]);
          if(!table.isColumnNumeric(idx.intValue())) {
            b = ErrorDialog.showQuery(selected[i] + " is not numeric. Continue?", "Warning");
          }
          if(b) {
            nominalListModel.removeElement(selected[i]);
            scalarListModel.addElement(selected[i]);
          }
        }
      }
      else if(src == fromScalarToNom) {
        Object[] selected = scalarList.getSelectedValues();
        for(int i = 0; i< selected.length; i++) {
          scalarListModel.removeElement(selected[i]);
          nominalListModel.addElement(selected[i]);
        }
      }
    }

    private void setFieldsInTable(){
      Enumeration e = scalarListModel.elements();


      while(e.hasMoreElements()) {
        String s = (String)e.nextElement();
        Integer ii = (Integer)indexLookup.get(s);
        table.setColumnIsScalar(true, ii.intValue());
        table.setColumnIsNominal(false, ii.intValue());
      }

      e = nominalListModel.elements();
      while(e.hasMoreElements()) {
        String s = (String)e.nextElement();
        Integer ii = (Integer)indexLookup.get(s);
        table.setColumnIsNominal(true, ii.intValue());
        table.setColumnIsScalar(false, ii.intValue());
      }

      //headless conversion
      setScalarColumns(scalarListModel.toArray());
      setNominalColumns(nominalListModel.toArray());
      //headless conversion
    }

    /**
     Not used
     Make sure all choices are valid
     */
    protected boolean checkChoices() {
      /*if(outputList.getSelectedIndex() == -1){
      JOptionPane.showMessageDialog(this,
      "You must select at least one output",
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
      */
      return true;
    }
  }

  /**
   Symmetrically lays out three components reflected about a vertical axis.
   The mirror component lays on the axis and the two image components lay
   on either side. The image components are the same size and take up all
   the space left from the mirror.
  */
  private class MirrorLayout implements LayoutManager {

    Component leftimage, mirror, rightimage;

    int gap = 1;

    public MirrorLayout(Component leftimage, Component mirror, Component rightimage) {
      this.leftimage = leftimage;
      this.mirror = mirror;
      this.rightimage = rightimage;
    }

    public void addLayoutComponent(String name, Component component) {
    }

    public void removeLayoutComponent(Component component) {
    }

    public void layoutContainer(Container parent) {
      Insets insets = parent.getInsets();
      Dimension dimension = mirror.getPreferredSize();

      int width = parent.getSize().width;
      int height = parent.getSize().height;

      int imagewidth = (width - insets.left - insets.right - dimension.width)/2;
      int imageheight = height - insets.top - insets.bottom;

      mirror.setBounds(insets.left+(width-insets.left-insets.right)/2-dimension.width/2+gap, height/2-dimension.height/2,
                       dimension.width, dimension.height);
      leftimage.setBounds(insets.left, insets.top, imagewidth, imageheight);
      rightimage.setBounds(insets.left+imagewidth+dimension.width+2*gap, insets.top, imagewidth, imageheight);
    }

    public Dimension preferredLayoutSize(Container parent) {
      Insets insets = parent.getInsets();

      int imagepreferredwidth = Math.max(leftimage.getPreferredSize().width, rightimage.getPreferredSize().width);
      int imagepreferredheight = Math.max(leftimage.getPreferredSize().height, rightimage.getPreferredSize().height);

      int preferredwidth = insets.left + mirror.getPreferredSize().width + 2*gap + 2*imagepreferredwidth + insets.right;
      int preferredheight = insets.top + mirror.getPreferredSize().height + 2*gap + 2*imagepreferredheight + insets.bottom;

      return new Dimension(preferredwidth, preferredheight);
    }

    public Dimension minimumLayoutSize(Container parent) {
      Insets insets = parent.getInsets();

      int imageminimumwidth = Math.max(leftimage.getMinimumSize().width, rightimage.getMinimumSize().width);
      int imageminimumheight = Math.max(leftimage.getMinimumSize().height, rightimage.getMinimumSize().height);

      int minimumwidth = insets.left + mirror.getMinimumSize().width + 2*gap + 2*imageminimumwidth + insets.right;
      int minimumheight = insets.top + mirror.getMinimumSize().height + 2*gap + 2*imageminimumheight + insets.bottom;

      return new Dimension(minimumwidth, minimumheight);
    }
  }

  //headless conversion support
  private String[] scalarColumns;
  private String[] nominalColumns;
  public Object[] getScalarColumns(){return scalarColumns;}
  public Object[] getNominalColumns(){return nominalColumns;}


  public void setScalarColumns(Object[] scalar){


    scalarColumns = new String[scalar.length];
    for (int i=0; i<scalar.length; i++){
      scalarColumns[i] = (String) scalar[i];

    }
}


  public void setNominalColumns(Object[] nominal){
    nominalColumns = new String[nominal.length];


    for (int i=0; i<nominal.length; i++){
      nominalColumns[i] = (String) nominal[i];

    }
}






  public void doit() throws Exception{
    Table _table  = (Table) pullInput(0);



    //creating a hash map of available columns: column name <-> column index.
    HashMap availableColumns = new HashMap();
    //validating that there is no intersection between the scalar and nominal columns
    //if validate returns true that means that some columns were assigned the
    //is nominal is scalar property. hence it is worth while building the map
    if (validate()){
      /*for (int i = 0; i < _table.getNumColumns(); i++)
        availableColumns.put(_table.getColumnLabel(i), new Integer(i));*/
      availableColumns = StaticMethods.getAvailableAttributes(_table);
    }

    if(availableColumns.size() == 0){
      System.out.println(getAlias() + ": Warning - The input table has no columns. It will be output as is.");
      //pushOutput(_table, 0);
      //return;
    }




    //if validate returns false - it does not matter that the map is empty
    //because it means that the arrays of scalar and nominal columns are
    //of size zero.

    //going over the scalar columns.
    for (int i=0; i<scalarColumns.length; i++)
      if(availableColumns.containsKey(scalarColumns[i].toUpperCase())){
        int index = ( (Integer) availableColumns.get(scalarColumns[i].toUpperCase())).intValue();
        _table.setColumnIsScalar(true, index);
        _table.setColumnIsNominal(false, index);
        if(!_table.isColumnNumeric(index))
          System.out.println(getAlias() + ": Column " + scalarColumns[i] + " was selected as scalar, but " +
                             "this column is not numeric. Continuing anyway.");

      }//if contains
    else
      throw new Exception (getAlias() + ": The table does not contain a column named " + scalarColumns[i]  +
                           ". Please reconfigure this module");



    //going over the nominal columns.
    for (int i=0; i<nominalColumns.length; i++)
      if(availableColumns.containsKey(nominalColumns[i].toUpperCase())){
        int index = ( (Integer) availableColumns.get(nominalColumns[i].toUpperCase())).intValue();
        _table.setColumnIsScalar(false, index);
        _table.setColumnIsNominal(true, index);
        if(_table.isColumnNumeric(index))
          System.out.println(getAlias() + ": Column " + nominalColumns[i] + " was selected as nominal, but " +
                             "this column is numeric. Continuing anyway.");

      }//if contains
      else
        throw new Exception(getAlias() + ": The table does not contain a column named " + nominalColumns[i] +
                            ". Please reconfigure this module");




     pushOutput(_table, 0);

  }//doit

  private boolean validate() throws Exception{



    HashMap scalarMap;
    scalarMap = new HashMap();

    if(scalarColumns == null) scalarColumns = new String[0];
    if(nominalColumns == null) nominalColumns = new String[0];
    if(scalarColumns.length == 0 && nominalColumns.length == 0){
      throw new Exception (this.getAlias()+" has not been configured. Before running headless, run with the gui and configure the parameters.");
//      return false;
    }//if length



    for (int i=0; i<scalarColumns.length; i++)
      scalarMap.put(scalarColumns[i].toUpperCase(), new Integer(i));



    for (int i=0; i<nominalColumns.length; i++)
      if(scalarMap.containsKey(nominalColumns[i].toUpperCase()))
        throw new Exception(this.getAlias()+": Attribute " + nominalColumns[i] +
                            " was set as both scalar and nominal. A column can be " +
                            "only either scalar or nominal, it cannot be both!\n");


    return true;
  }//validate

  //headless conversion support



}

//QA Comments Anca - added getPropertyDescription
// modified module info


//11-04-03 Vered started QA process:
//         The module is well documented. UI is good. Headless version works too.
//         was tested with StringColumns that are numeric - works great.
//         module is ready.

//12-05-03 module is ready for basic 4