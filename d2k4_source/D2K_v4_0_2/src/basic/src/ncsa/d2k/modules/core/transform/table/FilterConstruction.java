package ncsa.d2k.modules.core.transform.table;




import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import ncsa.d2k.userviews.swing.*;
import ncsa.d2k.gui.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.gui.*;
import ncsa.d2k.modules.core.datatype.*;
import ncsa.d2k.modules.core.vis.widgets.*;
import ncsa.d2k.modules.core.transform.StaticMethods;

/**
 * <code>FilterConstruction</code> is a simple user interface that facilitates
 * the creation of an expression for filtering rows from a
 * <code>MutableTable</code>. (See the documentation for
 * <code>FilterExpression</code> for details on the underlying expression
 * string and its format.)
 *
 * @author gpape
 *
 *
 */
public class FilterConstruction extends HeadlessUIModule {

/******************************************************************************/
/* Module methods                                                             */
/******************************************************************************/

   public String getModuleInfo() {
		return "<p>"+
"      Overview: This module will provide a gui to be employed by the user to "+
"      create a filter to eliminate the rows of the input table which do not "+
"      meet the criterion specified."+
"    </p>"+
"    <p>"+
"      Detailed Description: This module provides a gui used to specify "+
"      expressions. These expressions are used to eliminate rows of the table "+
"      that do not meet the criterion specified in those expressions. Complex "+
"      expressions are supported. When the gui is dismissed, the filters are "+
"      all collected into a <i>Transformation</i> object that is then passed as "+
"      an output. The expressions are not applied to the data by this module, "+
"      that can be done downstream using the transformation object passed."+
"    </p>"+
"    <p>"+
"      The expressions which can be applied to columns support the following "+
"      operators: &quot;&gt;&quot;, &quot;&gt;=&quot;, &quot;&lt;&quot;, &quot;&lt;=&quot;, &quot;!=&quot; and &quot;==&quot;. These operators are "+
"      greater than, greater than or equal to, less than, less than or equal "+
"      to, not equal, and equal. The result of these expressions, which we will "+
"      call attribute expressions, are either true or false. We can construct "+
"      more complex expressions using the boolean operators &quot;and&quot; (&amp;&amp;) or "+
"      &quot;or&quot;(||) on the results of the attribute expressions. For example, if we "+
"      had an attribute named <i>sepal_length</i> which is numeric, a filter "+
"      resulting from the following expression (sepal_length &gt;= 6.0) &amp;&amp; "+
"      (sepal_length &lt;= 7.0) will result in the removal of all rows where the "+
"      sepal_length is not in the range from 6.0 to 7.0, inclusive. Please note "+
"      that names of columns are case sensitive."+
"    </p>"+
"    <p>"+
"      Data Type Restrictions: Filter operations are supported for numeric data "+
"      only at this point."+
"    </p>"+
"    <p>"+
"      Data Handling: This module <i>may</i> modify its input data: columns "+
"      with blank labels will be assigned default ones. Other than that, this "+
"      module does not modify its input. Rather, its output is a <i>"+
"      Transformation</i> that can later be applied to filter the table. By "+
"      default, if an expression operates on any missing value, the row "+
"      containing the missing value is included in the result. There is a "+
"      property that can be changed to reverse this behavior."+
"    </p>";
	}

   public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.MutableTable"};
		return types;
	}

   public String getInputInfo(int index) {
		switch (index) {
			case 0: return "This is the mutable table for which a filter will be constructed.";
			default: return "No such input";
		}
	}

   public String[] getOutputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.Transformation"};
		return types;
	}

   public String getOutputInfo(int index) {
		switch (index) {
			case 0: return "This is the transformation the user constructed using the gui associated     with this module.";
			default: return "No such output";
		}
	}

   public String[] getFieldNameMapping() { return null; }

   protected UserView createUserView() {
     return new FilterConstructionGUI();
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


   public PropertyDescription[] getPropertiesDescriptions() {
    PropertyDescription[] pds = new PropertyDescription[3];
    pds[0] = super.supressDescription;
    pds[1] = new PropertyDescription("expression", "Filter Expression", "Set this expression to filter out rows in the table, when \"Supress User Interface Display\" is set to true. Validation of the expression is done during run time.");
	pds[2] = new PropertyDescription("includeMissingValues", "Include Missing Values",
			"If set, rows with missing values will be included in the result table.");
    return pds;

   }

/******************************************************************************/
/* GUI                                                                        */
/******************************************************************************/

   private static String scalar = "FilterConstructionINTERNALscalar";

   protected class FilterConstructionGUI extends JUserPane
     implements ActionListener, ExpressionListener {

     private ExpressionGUI gui;
     private FilterExpression expression;
     private MutableTable table;

     private JButton addColumnButton, addScalarButton, addOperationButton,
                 addBooleanButton, abortButton, doneButton, helpButton;
     private JComboBox columnBox, operationBox, booleanBox;
     private JTextField scalarField;

     private JPanel comboOrFieldPanel;
     private CardLayout nominalOrScalarLayout;
     private HashMap nominalComboBoxLookup; // look up JComboBoxes for nominal
                                  // columns; key = column #
     private int nominalShowing = -1; // which nominal combobox is showing?
                              // -1 if scalar textfield is showing

     private ViewModule mod;

     private boolean initialized = false;

     public void initView(ViewModule m) {
        mod = m;
     }

     public void setInput(Object obj, int ind) {
        if (ind != 0)
           return;

        table = (MutableTable)obj;

        for (int i = 0; i < table.getNumColumns(); i++) {
           String label = table.getColumnLabel(i);
           if (label == null || label.length() == 0)
              table.setColumnLabel("column_" + i, i);
        }

        initialize();
     }

     private void initialize() {

       this.removeAll();

       expression = new FilterExpression(table, getIncludeMissingValues());

       gui = new ExpressionGUI(expression, false);
       gui.addExpressionListener(this);

       gui.getTextArea().setText(_lastExpression);

       columnBox = new JComboBox();
       columnBox.addActionListener(this);
       for (int i = 0; i < table.getNumColumns(); i++)
         columnBox.addItem(table.getColumnLabel(i));

       addColumnButton = new JButton(
          new ImageIcon(mod.getImage("/images/addbutton.gif")));
       addColumnButton.addActionListener(this);

       JPanel columnPanel = new JPanel();
       columnPanel.setLayout(new GridBagLayout());
       Constrain.setConstraints(columnPanel, new JLabel(), 0, 0, 1, 1,
         GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER, 1, 0);
       Constrain.setConstraints(columnPanel, columnBox, 1, 0, 1, 1,
         GridBagConstraints.NONE, GridBagConstraints.EAST, 0, 0);
       Constrain.setConstraints(columnPanel, addColumnButton, 2, 0, 1, 1,
         GridBagConstraints.NONE, GridBagConstraints.EAST, 0, 0);

       ///////////////////////////////////////////////////////////////////////
       // scalar columns will require textfield input; nominal columns will
       // require a combobox of nominal values:
       // {

       scalarField = new JTextField();

       addScalarButton = new JButton(
          new ImageIcon(mod.getImage("/images/addbutton.gif")));
       addScalarButton.addActionListener(this);

       nominalComboBoxLookup = new HashMap();
       comboOrFieldPanel = new JPanel();
       nominalOrScalarLayout = new CardLayout();
       comboOrFieldPanel.setLayout(nominalOrScalarLayout);
       comboOrFieldPanel.add(scalarField, scalar);

       for (int i = 0; i < table.getNumColumns(); i++) {

         if (!table.isColumnNominal(i))
            continue;

         JComboBox nominalCombo = new JComboBox(getUniqueValues(i));

         comboOrFieldPanel.add(nominalCombo, table.getColumnLabel(i));
         nominalComboBoxLookup.put(new Integer(i), nominalCombo);

       }

       if (table.isColumnNominal(0)) {
         nominalOrScalarLayout.show(comboOrFieldPanel,
            table.getColumnLabel(0));
         nominalShowing = 0;
       }

       /*
       JPanel scalarPanel = new JPanel();
       scalarPanel.setLayout(new GridBagLayout());
       Constrain.setConstraints(scalarPanel, new JLabel(), 0, 0, 1, 1,
         GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER, 1, 0);
       Constrain.setConstraints(scalarPanel, scalarField, 1, 0, 1, 1,
         GridBagConstraints.NONE, GridBagConstraints.EAST, 0, 0);
       Constrain.setConstraints(scalarPanel, addScalarButton, 2, 0, 1, 1,
         GridBagConstraints.NONE, GridBagConstraints.EAST, 0, 0);
       */

       JPanel nominalOrScalarPanel = new JPanel();
       nominalOrScalarPanel.setLayout(new GridBagLayout());
       Constrain.setConstraints(nominalOrScalarPanel,
         new JLabel(), 0, 0, 1, 1,
         GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER, 1, 0);
       Constrain.setConstraints(nominalOrScalarPanel,
         comboOrFieldPanel, 1, 0, 1, 1,
         GridBagConstraints.NONE, GridBagConstraints.EAST, 0, 0);
       Constrain.setConstraints(nominalOrScalarPanel,
         addScalarButton, 2, 0, 1, 1,
         GridBagConstraints.NONE, GridBagConstraints.EAST, 0, 0);

       // }
       ///////////////////////////////////////////////////////////////////////

       operationBox = new JComboBox();
       operationBox.addItem("==");
       operationBox.addItem("!=");
       operationBox.addItem("<");
       operationBox.addItem("<=");
       operationBox.addItem(">");
       operationBox.addItem(">=");

       addOperationButton = new JButton(
          new ImageIcon(mod.getImage("/images/addbutton.gif")));
       addOperationButton.addActionListener(this);

       JPanel operationPanel = new JPanel();
       operationPanel.setLayout(new GridBagLayout());
       Constrain.setConstraints(operationPanel, new JLabel(), 0, 0, 1, 1,
         GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER, 1, 0);
       Constrain.setConstraints(operationPanel, operationBox, 1, 0, 1, 1,
         GridBagConstraints.NONE, GridBagConstraints.EAST, 0, 0);
       Constrain.setConstraints(operationPanel, addOperationButton, 2, 0, 1, 1,
         GridBagConstraints.NONE, GridBagConstraints.EAST, 0, 0);

       booleanBox = new JComboBox();
       booleanBox.addItem("&&");
       booleanBox.addItem("||");

       addBooleanButton = new JButton(
          new ImageIcon(mod.getImage("/images/addbutton.gif")));
       addBooleanButton.addActionListener(this);

       JPanel booleanPanel = new JPanel();
       booleanPanel.setLayout(new GridBagLayout());
       Constrain.setConstraints(booleanPanel, new JLabel(), 0, 0, 1, 1,
         GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER, 1, 0);
       Constrain.setConstraints(booleanPanel, booleanBox, 1, 0, 1, 1,
         GridBagConstraints.NONE, GridBagConstraints.EAST, 0, 0);
       Constrain.setConstraints(booleanPanel, addBooleanButton, 2, 0, 1, 1,
         GridBagConstraints.NONE, GridBagConstraints.EAST, 0, 0);

       JPanel leftPanel = new JPanel();
       leftPanel.setLayout(new GridBagLayout());
       Constrain.setConstraints(leftPanel, columnPanel, 0, 0, 1, 1,
         GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTH, 1, 0);
       Constrain.setConstraints(leftPanel, nominalOrScalarPanel, 0, 1, 1, 1,
         GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTH, 1, 0);
       Constrain.setConstraints(leftPanel, operationPanel, 0, 2, 1, 1,
         GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTH, 1, 0);
       Constrain.setConstraints(leftPanel, booleanPanel, 0, 3, 1, 1,
         GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTH, 1, 0);
       Constrain.setConstraints(leftPanel, new JLabel(), 0, 4, 1, 1,
         GridBagConstraints.BOTH, GridBagConstraints.CENTER, 1, 1);

       JPanel topPanel = new JPanel();
       topPanel.setLayout(new GridBagLayout());
       Constrain.setConstraints(topPanel, leftPanel, 0, 0, 1, 1,
         GridBagConstraints.VERTICAL, GridBagConstraints.WEST, 0, 1);
       Constrain.setConstraints(topPanel, gui, 1, 0, 1, 1,
         GridBagConstraints.BOTH, GridBagConstraints.CENTER, 1, 1);

       abortButton = new JButton("Abort");
       abortButton.addActionListener(this);
       // doneButton = new JButton("Done");
       // doneButton.addActionListener(this);
       helpButton = new JButton("Help");
       helpButton.addActionListener(new AbstractAction() {
         public void actionPerformed(ActionEvent e) {
            HelpWindow help = new HelpWindow();
            help.setVisible(true);
         }
       });

       JButton addButton = gui.getAddButton();
       addButton.setText("Done");

       JPanel bottomPanel = new JPanel();
       bottomPanel.setLayout(new GridBagLayout());
       Constrain.setConstraints(bottomPanel, helpButton, 0, 0, 1, 1,
         GridBagConstraints.NONE, GridBagConstraints.WEST, 0, 0);
       Constrain.setConstraints(bottomPanel, new JLabel(), 1, 0, 1, 1,
         GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER, 1, 0);
       Constrain.setConstraints(bottomPanel, abortButton, 2, 0, 1, 1,
         GridBagConstraints.NONE, GridBagConstraints.EAST, 0, 0);
       Constrain.setConstraints(bottomPanel, addButton, 3, 0, 1, 1,
         GridBagConstraints.NONE, GridBagConstraints.EAST, 0, 0);

       this.setLayout(new GridBagLayout());
       Constrain.setConstraints(this, topPanel, 0, 0, 1, 1,
         GridBagConstraints.BOTH, GridBagConstraints.CENTER, 1, 1);
       Constrain.setConstraints(this, new JSeparator(), 0, 1, 1, 1,
         GridBagConstraints.HORIZONTAL, GridBagConstraints.SOUTH, 1, 0);
       Constrain.setConstraints(this, bottomPanel, 0, 2, 1, 1,
         GridBagConstraints.HORIZONTAL, GridBagConstraints.SOUTH, 1, 0);

       initialized = true;

     }

     public void actionPerformed(ActionEvent e) {

       Object src = e.getSource();

       if (src == columnBox && initialized) {

         int index = columnBox.getSelectedIndex();

         if (table.isColumnNominal(index)) {

            nominalShowing = index;

            nominalOrScalarLayout.show(comboOrFieldPanel,
              table.getColumnLabel(index));

         }
         else {

            nominalShowing = -1;

            nominalOrScalarLayout.show(comboOrFieldPanel, scalar);

         }

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

            gui.getTextArea().insert(
              "'" + combo.getSelectedItem() + "'",
              gui.getTextArea().getCaretPosition());

         }

       }

       else if (src == addOperationButton)
         gui.getTextArea().insert(" " + operationBox.getSelectedItem() + " ",
            gui.getTextArea().getCaretPosition());

       else if (src == addBooleanButton)
         gui.getTextArea().insert(" " + booleanBox.getSelectedItem() + " ",
            gui.getTextArea().getCaretPosition());

       else if (src == abortButton)
         viewCancel();

     }

     private Vector getUniqueValues(int columnIndex) {

       if (table == null)
         return null;

       int numRows = table.getNumRows();

       HashMap columnValues = new HashMap();

       int index = 0;
       for (int i = 0; i < numRows; i++) {

         String str = table.getString(i, columnIndex);

         if (!columnValues.containsKey(str))
            columnValues.put(str, new Integer(index++));

       }

       return new Vector(columnValues.keySet());

     }

     public void expressionChanged(Object evaluation) {
        _lastExpression = gui.getTextArea().getText();
       pushOutput(new FilterTransformation((boolean[])evaluation, false), 0);
       //headless conversion support
       setExpression(_lastExpression);
       //headless conversion support
       viewDone("Done");
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

     sb.append("<html><body><h2>FilterConstruction</h2>");
     sb.append("This module allows a user to filter rows from a MutableTable ");
     sb.append("by specifying an appropriate filtering expression.");
     sb.append("<br><br>");
     sb.append("The user can construct an expression for filtering ");
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
     sb.append("<br><br>");
     sb.append("To add parentheses around a sub expression, mark the sub expression ");
     sb.append("using the mouse, and click \"Add Paren\" button.");
     sb.append("</body></html>");

     return sb.toString();

   }

/******************************************************************************/
/* the output Transformation                                                  */
/******************************************************************************/

   private class FilterTransformation implements Transformation {

     private boolean[] filter;
     private boolean filterThese;

     FilterTransformation(boolean[] filter, boolean filterThese) {
       this.filter = filter;
       this.filterThese = filterThese;
     }

     public boolean transform(MutableTable table) {
       // if there are no filters, do nothing.
       if (filter == null)
          return true;

try {
          //9-17-03 vered:
          //chnaged order of removing rows. if removing from beginning
          //to end an array index out of bound exception is likely.
          //in addition to a likelyhood of the exception, since a mutable table
          //is a subset table - removing first from the beginning chnages the indexing
          //of the end rows. which might result in filtering the wrong rows.
       if (!filterThese)
         //commented by vered.
         //for (int i = 0; i<filter.length; i++)
         for (int i = filter.length-1; i >=0 ; i--)
          if(!filter[i])
           table.removeRow(i);


         // 4/7/02 commented out by Loretta...
         // this add gets done by applyTransformation
         //table.addTransformation(this);
       }
       catch(Exception e) {

         e.printStackTrace();

       return false; }

       return true;

     }

   }


   /**
    * Return the human readable name of the module.
    * @return the human readable name of the module.
    */
   public String getModuleName() {
		return "Filter Construction";
	}

   /**
    * Return the human readable name of the indexed input.
    * @param index the index of the input.
    * @return the human readable name of the indexed input.
    */
   public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Mutable Table";
			default: return "NO SUCH INPUT!";
		}
	}

   /**
    * Return the human readable name of the indexed output.
    * @param index the index of the output.
    * @return the human readable name of the indexed output.
    */
   public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Transformation";
			default: return "NO SUCH OUTPUT!";
		}
	}


   //headless conversion support
   private String expression; //the expression that was set by the user
   public String getExpression(){return expression;}
   public void setExpression(String ex){expression = ex;}



   public void doit() throws Exception{
     //pulling input
     MutableTable table = (MutableTable) pullInput(0);
     //creating a column hash map
     HashMap availableColumns = StaticMethods.getAvailableAttributes(table);
     /*
          new HashMap();
     for (int i=0; i<table.getNumColumns(); i++)
       availableColumns.put(table.getColumnLabel(i), new Integer(i));
*/

     if(expression == null)
       throw new Exception (this.getAlias()+" has not been configured. Before running headless, run with the gui and configure the parameters.");

       if(availableColumns.size() == 0){
         System.out.println(getAlias()+": Warning - Table " + table.getLabel() + " has no columns.");
         /*System.out.println("The transformation will be an empty one");
         boolean[] val = new boolean[0];
         pushOutput(new FilterTransformation(val, false), 0);
         return;*/
       }

 //     String goodCondition = ExpressionParser.parseExpression(expression, availableColumns, false);
      //now good condition holds only relevant filters.
      //building a filter expression




      //assuming that filter expression is parsing and validating the expression.
      FilterExpression fEx = new FilterExpression(table, getIncludeMissingValues());
      fEx.setExpression(expression);
      //getting the array of booleans - which row to filter.
      boolean[] eval = ( boolean[] )fEx.evaluate();
      //pushing out the transformation
      pushOutput(new FilterTransformation(eval, false), 0);

   }//doit

   //headless conversion support
}

 /**
  * QA comments:
  * 3-4-03 vered started qa:
  * 3-6-03 sent back to greg to support default labels.
  *
  * 10-23-03 vered started qa process
  *
  * does not handle missing values well. if according to the transformation row no.
  * i should be ommited, and row no. i has a missing value in attribute a, then
  * the table after the transformation will also have a missing value in row
  * no. i under attribute a (even if the row that replaced it has only valid
  * values). (fixed - 11-03-03)
  *
  *
  * 11-03-03: the following expression attribute == attribute
  *           is considered legal. if both attributes are nominal this will
  *           throw a number format exception. if both are numeric: if they
  *           are identical - the table remains the same. otherwise the table
  *           becomes empty. [bug 109]. fixed.
  *
  *           the following expression is considered legal (with golf.data):
 *            att == val == att
 *            the result was an empty table. (bug 110) fixed.
 *
 * 01-06-04:
 * module is ready for basic.

  */
