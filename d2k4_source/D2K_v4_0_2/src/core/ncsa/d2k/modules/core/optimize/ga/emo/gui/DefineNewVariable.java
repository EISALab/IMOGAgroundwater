package ncsa.d2k.modules.core.optimize.ga.emo.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.datatype.table.transformations.*;
import ncsa.d2k.modules.core.optimize.ga.emo.*;
import ncsa.d2k.modules.core.transform.attribute.*;
import ncsa.d2k.modules.core.optimize.ga.emo.functions.*;

/**
 * Define variables to be used in the calculation of fitness functions.  These
 * variables are calculated by performing transformations on a Table.
 */
public class DefineNewVariable
    extends AttributeConstruction {

  public String getModuleInfo() {
    String s = "<p>Overview:";
    s += "Define variables to be used in the calculation of fitness functions.";
    s += "<p>Detailed Description: ";
    s += " Define variables that are calculated by performing transformations ";
    s += " on a Table.";
    s += "This module provides a GUI used to specify expression strings.";
    s += "These expressions are interpreted as operations on existing ";
    s += "attributes in the table and are used to construct new attributes. ";
    s += "When the GUI is dismissed, the information needed to construct ";
    s += "these new attributes is encapsulated in a <i>Transformation</i> ";
    s += "object that is applied when evaluating a population.";
    s += "The available operations on numeric attributes are addition, ";
    s += "subtraction, multiplication, division, and modulus. The ";
    s += "operations available on boolean attributes are AND and OR.";
    return s;
  }

  public String getModuleName() {
    return "Define New Variable";
  }

  protected UserView createUserView() {
    return new DefineVarView();
  }

  public String[] getInputTypes() {
    String[] in = {
        "ncsa.d2k.modules.core.optimize.ga.emo.Parameters"};
    return in;
  }

  public String[] getOutputTypes() {
    String[] out = {
        "ncsa.d2k.modules.core.optimize.ga.emo.Parameters"};
    return out;
  }

  public String getInputName(int i) {
    return "EMOParameters";
  }

  public String getOutputName(int i) {
    return "EMOParameters";
  }

  public String getInputInfo(int i) {
    return "The parameters for EMO.";
  }

  public String getOutputInfo(int i) {
    return "The parameters for EMO, with the fitness variables added.";
  }
  
  public void doit() throws Exception {
    Parameters params = (Parameters)pullInput(0);  
    Object[] constructions = (Object[]) getLastCons();
    if (constructions != null) {
      for (int i = 0; i < constructions.length; i++) {
        Construction ffc = (Construction)
            constructions[i];

        Variable fcf = new Variable(ffc);
        params.addFunction(fcf);
      }
    }
    else {
        throw new Exception (this.getAlias()+" has not been configured. Before running headless, run with the gui and configure the parameters.");      
    }
    pushOutput(params, 0);
  }


  //inner class, extends attributeConstruction's inner gui class
  protected class DefineVarView
      extends ColumnConstructionGUI {

    // columnModel keeps track of all the already present columns in the table
    // as well as the new columns defined recently using the widget
    // It is used in the redoBox() function
    protected DefaultComboBoxModel columnModel;

    protected Parameters parameters;
    
    public void initialize() {
      super.initialize();
      this.newLabels = (String[])getNewLabel();
      this.newTypes = (int[])getNewTyp();
      this.constructions = (Object[])getLastCons();

      if (constructions != null) {
         for (int i = 0; i < constructions.length; i++) {

            Construction current =
               (Construction)constructions[i];

            try {
               expression.setLazyExpression(current.expression, newLabels, newTypes);
            }
            catch(Exception e) { } // this can't really happen, since it
                                   // would have been caught on the last run

            newColumnModel.addElement(current);

         }
      }
      newAttributeLabel.setText("New Variable Name:");
    }
    

    public void setInput(Object o, int i) {
      parameters = (Parameters) o;
      java.util.List dv = parameters.getDecisionVariables();
      int numVars = dv.size();
      table = new MutableTableImpl(numVars);

      for (int j = 0; j < numVars; j++) {
       //BASIC3 table.setColumn(new double[0], j);
       DecisionVariable var = (DecisionVariable)dv.get(j);
        table.setColumn(new DoubleColumn(0), j);
        table.setColumnLabel(var.getName(), j);
      }

      java.util.List funcList = parameters.getFunctions();
      int size = funcList.size();
      for(int j = 0; j < size; j++) {
        Function f = (Function)funcList.get(j);  
        DoubleColumn dc = new DoubleColumn(new double[0]);
        dc.setLabel(f.getName());
        table.addColumn(dc);
      }

      this.initialize();
    }

    public Dimension getPreferredSize() {
      return new Dimension(800, 400);
    }

    public void paintComponent(Graphics g) {
      Graphics2D g2 = (Graphics2D) g;
      g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                          RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
      super.paintComponent(g2);
    }

    // function to repaint the columnBox
    // since new column are added by the user
    // the columnBox needs to be continuously updated so
    // that recently defined defined variables can be used to define
    // other variables
    public void redoBox() {
      Object[] constructions;
      constructions = (newColumnModel.toArray());

      columnModel = new DefaultComboBoxModel();
      int i = 0;
      // add elements in the columnModel using the table
      for (i = 0; i < table.getNumColumns(); i++) {
        columnModel.addElement(table.getColumnLabel(i));
      }

      if(constructions != null) {
        for(int j = 0; j < constructions.length; j++) {
          columnModel.addElement( ((Construction)constructions[j]).label);
        }
      }

      // set the columnBox model to the recently
      // updated columnModel
      columnBox.setModel(columnModel);

      //bottomPanel.removeAll();
    }

    public void actionPerformed(ActionEvent e) {

      Object src = e.getSource();
      // delete the selected expression
      if (src == deleteButton) {
        Runnable r = new Runnable() {
          public void run() {

            //Selected row index of table retrieved and stored
            int selected = newColumnList.getSelectedIndex();
            //Index of item in the list calculated here
            int item = (table.getNumColumns()) - newColumnModel.getSize() +
                (selected);

            if (selected != -1) {
              String label = ( (Construction) (newColumnModel.elementAt(
                  selected))).label;

              /**
               * Remove label from columnBox,
               * remove row from modelData
               * table columnLabel null at item's location
               * Remove item at selected index from newColumnModel
               */
              columnBox.removeItem(label);
              //table.setColumnLabel(null, item);
              //table.removeColumn(item);
              newColumnModel.removeElementAt(selected);

              /**
                   * Below allows the label removed above to permanently be removed from
               * existence so as not to be reloaded elsewhere
               */
              if (newLabels != null) {

                String[] newNewLabels = new String[newLabels.length - 1];
                int[] newNewTypes = new int[newTypes.length - 1];
                int index = 0;

                for (index = 0; index < newLabels.length; index++) {
                  if (newLabels[index].equals(label)) {
                    break;
                  }
                  else {
                    newNewLabels[index] = newLabels[index];
                    newNewTypes[index] = newTypes[index];
                  }
                }

                for (; index < newNewLabels.length; index++) {
                  newNewLabels[index] = newLabels[index + 1];
                  newNewTypes[index] = newTypes[index + 1];
                }
                newLabels = newNewLabels;
                newTypes = newNewTypes;
              }
              //BASIC3
              //if (_newLab != null) {
              // _newLab = newLabels;
              //}
              //_newTyp = newTypes;

              // _lastCons = newColumnModel.toArray();
              if (getNewLabel() != null) {
                setNewLabel(newLabels);
              }
              setNewTyp(newTypes);
              setLastCons(newColumnModel.toArray());

              // added 3.25.2004 by DC
              newColumnList.getSelectionModel().clearSelection();
            }
          }
        };
        SwingUtilities.invokeLater(r);
      }
      else if (src == this.doneButton) {
        Runnable r = new Runnable() {
          public void run() {
            done();
          }
        };
        SwingUtilities.invokeLater(r);
      }
      else {
        super.actionPerformed(e);
      }
    }

    protected void done() {
      constructions = (Object[]) getLastCons();
      if(constructions != null) {
        //Construction[] tmp = new Construction[constructions.length];
        for (int i = 0; i < constructions.length; i++) {
          Construction c = (Construction) constructions[i];
          Variable var = new Variable(c);
          parameters.addFunction(var);
        }
      }

      pushOutput(parameters, 0);
      parameters = null;
      viewDone("Done");
    }

    /**
     * this is the action listener attached with the addButton
     * it adds the column to the table and take all the necessary actions
     */
    public void expressionChanged(Object evaluation) {

      // is no new name field given display error dialog
      if (newNameField.getText().length() == 0) {
        JOptionPane.showMessageDialog(this,
                                      "You must specify a new column name.",
                                      "Define New Variables",
                                      JOptionPane.ERROR_MESSAGE);
      }
      else {
        StringBuffer newLabelBuffer = new StringBuffer(newNameField.getText());
        for (int i = 0; i < newLabelBuffer.length(); i++) {
          if (newLabelBuffer.charAt(i) == '\\') {
            newLabelBuffer.deleteCharAt(i);
            i--;
          }
        }

        // determine new expression, label, and type
        ColumnExpression newExp = new ColumnExpression(table);
        try {
          newExp.setLazyExpression(gui.getTextArea().getText(), newLabels,
                                   newTypes);
        }
        catch (ExpressionException e) {
          JOptionPane.showMessageDialog(this,
              "Error in Define New Variables. Check your expression.",
              "Define New Variables", JOptionPane.ERROR_MESSAGE);
          return;
        }

        String newLab = newLabelBuffer.toString();
        String newStr = gui.getTextArea().getText();
        int newTyp = newExp.evaluateType();

        if (newLabels == null) {
          newLabels = new String[1];
          newLabels[0] = newLab;
        }
        else {
          String[] newNewLab = new String[newLabels.length + 1];
          for (int i = 0; i < newLabels.length; i++) {
            newNewLab[i] = newLabels[i];
          }
          newNewLab[newLabels.length] = newLab;
          newLabels = newNewLab;
        }

        if (newTypes == null) {
          newTypes = new int[1];
          newTypes[0] = newTyp;
        }
        else {
          int[] newNewTyp = new int[newTypes.length + 1];
          for (int i = 0; i < newTypes.length; i++) {
            newNewTyp[i] = newTypes[i];
          }
          newNewTyp[newTypes.length] = newTyp;

          newTypes = newNewTyp;
        }
        //check if the expression is valid
        newExp = new ColumnExpression(table);
        try {
          newExp.setLazyExpression(gui.getTextArea().getText(), newLabels,
                                   newTypes);
        }
        catch (ExpressionException e) {
          JOptionPane.showMessageDialog(this,
              "Error in Define New Variables. Check your expression.",
              "Define New Variables", JOptionPane.ERROR_MESSAGE);
          return;
        }

        // if the expression is valid set the expression of gui
        gui.setExpression(newExp);
        
// Changed 3.25.3004 by DC            
            if(selectedItem != null) {
              String newName = newNameField.getText();
                selectedItem.label = newNameField.getText();
                selectedItem.expression = gui.getTextArea().getText();
                newColumnModel.set(selectedIndex, selectedItem);
                newColumnList.getSelectionModel().clearSelection();
            }
            else {
              Construction added = new Construction(
                newNameField.getText(), gui.getTextArea().getText());
              newColumnModel.addElement(added);
            }
// end change            
        

/*        Construction added = new Construction(
            newNameField.getText(), gui.getTextArea().getText());
        newColumnModel.addElement(added);*/
        newColumnList.setMinimumSize(new Dimension(200, 200));

        
        //BASIC3 _lastCons = newColumnModel.toArray();
        //_newLab = newLabels;
        //_newTyp = newTypes;
        
        lastCons = newColumnModel.toArray();
        setNewLabel( newLabels);
        setNewTyp (newTypes);
        
        
        newNameField.setText("");
        gui.getTextArea().setText("");

//        float[] tmpfloat = new float[0];

        // add an empty column of floats to the table
//        table.addColumn(tmpfloat);

        // set the label of the new column added to the table
//        table.setColumnLabel(newLab, (table.getNumColumns() - 1));

        // the columnBox needs to be revalidated so that the new
        // defined variable can also be used to create more
        //new variables
        redoBox();

        //reset the new name field
        newNameField.setText("");
        //reset the gui text area
        gui.getTextArea().setText("");
      }
    }
  }
}