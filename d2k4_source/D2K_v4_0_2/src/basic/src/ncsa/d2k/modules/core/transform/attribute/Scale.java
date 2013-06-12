package ncsa.d2k.modules.core.transform.attribute;


import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.Array;
import java.util.*;
import javax.swing.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.userviews.swing.*;
import ncsa.gui.ErrorDialog;

/**
 * This module presents a user interface for the interactive scaling of
 * <code>MutableTable</code> data. Selected columns are scaled to a
 * user-specified range (the default range is 0 to 1).
 * <p>
 * All transformed columns are converted to type <code>double</code>.
 *
 * @author gpape
 */
public class Scale extends HeadlessUIModule {

/******************************************************************************/
/* UIModule methods                                                           */
/******************************************************************************/

   public UserView createUserView() {
      return new ScaleUI();
   }
   public void doit () throws Exception {
   	  MutableTable table = (MutableTable) this.pullInput(0);
   	  if (indices == null || from_min == null || from_max == null || to_min == null || to_max == null) {
   	     throw new Exception (this.getAlias()+" has not been configured. Before running headless, run with the gui and configure the parameters.");
      }
      int nc = table.getNumColumns();
   	  if (nc < indices.length) {
   	  	 throw new Exception (this.getAlias()+" has not been configured to run headless with a table of this structure. Not enough columns in the table");
   	  }

   	  // Find the maximum column index.
   	  int max = -1;
   	  for (int i = 0 ; i < indices.length ; i++)
   	  	 if (indices[i] > max)
   	  		max = indices[i];

   	  if (nc < max) {
		throw new Exception (this.getAlias()+" has not been configured to run headless with a table of this structure. Not enough columns in the table");
	  }

	  for (int i = 0 ; i < indices.length ; i++) {
	  	 if (indices[i] >= 0) {
		     Column col = table.getColumn(indices[i]);
		     if (!col.getIsScalar()) {
				throw new Exception (this.getAlias()+" has not been configured to run headless with a table of this structure. Column "+col.getLabel()+" is not scalar.");
			 }
	  	 }
	  }
	  pushOutput(new ScalingTransformation(indices, from_min, from_max,
	      to_min, to_max), 0);
   }

   public String[] getFieldNameMapping() { return null; }

   public String getModuleInfo() {
		return "<p>      Overview: This module presents a simple user interface for the       interactive scaling"+
			" of numeric <i>MutableTable</i> data. Numeric columns       selected by the user can be scaled"+
			" to a specified range (the default       range is 0 to 1).    </p>    <p>      Detailed Description:"+
			" A user interface is displayed that has an entry       for the current range of data values"+
			" for each attribute in the table       passed in. The user may change the current range as well"+
			" as the range to       scale the data to. The scaling is done as if the data is within the "+
			"      current range values specified, values which are out of this range are       pinned. If"+
			" the gui is suppressed, this modules will use the last entries       the user made to apply"+
			" to the input table, however, the current range is       always gotten from the table, the previous"+
			" selection made via the gui is       ignored. If the columns the user has previously selected"+
			" did not exist       or if they are no longer scalar, it will fail.    </p>  " +

                        /*"  <p>      <u>Missing"+
			" Values Handling:</u> This module handles missing values as if       they were real meaningful"+
			" values. For example, if a missing values is       represented by a number (e.g. zero), then"+
			" this number may appear as       lower or upper bound of its column, although it is marked as"+
			" a missing       value in its table. If it indeed becomes a lower or upper bound, it       affects"+
			" of course the scaling results.    </p>" +
                        */

                       "</p><p>Missing Values Handling: Missing values are preserved by " +
              "the output Transformation of this module. Missing values are left as they are " +
              "and are not being scaled or considered during scaling. " +

                        "    <p>      Data Handling: This module does not modify"+
			" its input data. Rather, its       output is a <i>Transformation</i> that can later be used"+
			" to scale the       specified columns. All transformed columns will be converted to type <i>"+
			"      double</i>.    </p>";
	}

   public String getModuleName() {
		return "Scale Values";
	}

   public String getInputInfo(int index) {
		switch (index) {
			case 0: return "A <i>MutableTable</i> with columns to be scaled.";
			default: return "No such input";
		}
	}

   public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Mutable Table";
			default: return "NO SUCH INPUT!";
		}
	}

   public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.MutableTable"};
		return types;
	}

   public String getOutputInfo(int index) {
		switch (index) {
			case 0: return "A scaling transformation for the specified columns.";
			default: return "No such output";
		}
	}

   public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Transformation";
			default: return "NO SUCH OUTPUT!";
		}
	}

   public String[] getOutputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.Transformation"};
		return types;
	}
   private int[] indices;
   public void setIndices (int [] ni) {
      this.indices = ni;
   }
   public int [] getIndices () {
      return this.indices;
   }
   private double[] from_min, from_max, to_min, to_max;
   public void setFromMin (double [] ni) {
	  this.from_min = ni;
   }
   public double [] getFromMin () {
	  return this.from_min;
   }
   public void setFromMax (double [] ni) {
	  this.from_max = ni;
   }
   public double [] getFromMax () {
	  return this.from_max;
   }
   public void setToMin (double [] ni) {
	  this.to_min = ni;
   }
   public double [] getToMin () {
	  return this.to_min;
   }
   public void setToMax (double [] ni) {
	  this.to_max = ni;
   }
   public double [] getToMax () {
	  return this.to_max;
   }

/******************************************************************************/
/* GUI                                                                        */
/******************************************************************************/

   private Insets emptyInsets  = new Insets( 0,  0,  0,  0),
                  labelInsets  = new Insets(10, 10, 10, 10),
                  buttonInsets = new Insets( 5,  5,  5,  5);

   private class ScaleUI extends JUserPane implements ActionListener {

      private MutableTable table;

      private HashMap panelMap; // map column indices to column panels
      private JButton abortButton, doneButton;

      private int[] indirection; // points into table, at numeric columns
      private String[] numericLabels;

      private Dimension preferredSize = new Dimension(600, 300);

      public Dimension getMinimumSize() {
         return preferredSize;
      }

      public Dimension getPreferredSize() {
         return preferredSize;
      }

      public void initView(ViewModule mod) { }

      public void setInput(Object obj, int ind) {

         if (ind != 0)
            return;

         table = (MutableTable)obj;
         removeAll();

         // how many numeric columns are there?

         int numNumericColumns = 0, columnType;
         for (int i = 0; i < table.getNumColumns(); i++) {

            columnType = table.getColumnType(i);

            if (columnType == ColumnTypes.BYTE    ||
                columnType == ColumnTypes.DOUBLE  ||
                columnType == ColumnTypes.FLOAT   ||
                columnType == ColumnTypes.INTEGER ||
                columnType == ColumnTypes.LONG    ||
                columnType == ColumnTypes.SHORT) {

               numNumericColumns++;

            }

         }

         // construct list of available numeric columns

         indirection = new int[numNumericColumns];
         numericLabels = new String[numNumericColumns];

         int index = 0;
         for (int i = 0; i < table.getNumColumns(); i++) {

            columnType = table.getColumnType(i);

            if (columnType == ColumnTypes.BYTE    ||
                columnType == ColumnTypes.DOUBLE  ||
                columnType == ColumnTypes.FLOAT   ||
                columnType == ColumnTypes.INTEGER ||
                columnType == ColumnTypes.LONG    ||
                columnType == ColumnTypes.SHORT) {

               indirection[index] = i;

               numericLabels[index] = table.getColumnLabel(i);

               if (numericLabels[index] == null ||
                   numericLabels[index].length() == 0) {

                  numericLabels[index] = "column " + i;

               }

               index++;

            }

         }

         // set up button panel

         abortButton = new JButton("Abort");
         abortButton.addActionListener(this);
         doneButton = new JButton("Done");
         doneButton.addActionListener(this);
         JLabel buttonFillerLabel = new JLabel(" ");

         JPanel buttonPanel = new JPanel();
         GridBagLayout buttonLayout = new GridBagLayout();
         buttonPanel.setLayout(buttonLayout);

         buttonLayout.addLayoutComponent(abortButton,
            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.WEST, GridBagConstraints.NONE,
            buttonInsets, 0, 0));
         buttonPanel.add(abortButton);

         buttonLayout.addLayoutComponent(buttonFillerLabel,
            new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            emptyInsets, 0, 0));
         buttonPanel.add(buttonFillerLabel);

         buttonLayout.addLayoutComponent(doneButton,
            new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.EAST, GridBagConstraints.NONE,
            buttonInsets, 0, 0));
         buttonPanel.add(doneButton);

         // if no numeric columns, attach a message to that effect. otherwise,
         // attach numeric column panels.

         GridBagLayout layout = new GridBagLayout();
         setLayout(layout);

         if (numNumericColumns == 0) {

            JLabel noNumericLabel = new JLabel(
               "This table does not have any numeric columns to scale.");

            layout.addLayoutComponent(noNumericLabel,
               new GridBagConstraints(0, 0, 2, 1, 1.0, 1.0,
               GridBagConstraints.CENTER, GridBagConstraints.BOTH,
               labelInsets, 0, 0));
            add(noNumericLabel);

         }
         else {

            Box numericColumnsBox = new Box(BoxLayout.Y_AXIS);

            panelMap = new HashMap();

            // add fields for scaling

            for (int count = 0; count < indirection.length; count++) {

               index = indirection[count];

               // find min and max for this column

               double min = Double.POSITIVE_INFINITY,
                      max = Double.NEGATIVE_INFINITY, d;


               for (int j = 0; j < table.getNumRows(); j++) {
               	  if (!table.isValueMissing(j, index)) {
                     d = table.getDouble(j, index);
                     if (d < min)
                        min = d;
                     if (d > max)
                        max = d;
               	  }
               }

               // add column panel

               ColumnPanel columnPanel = new ColumnPanel(numericLabels[count],
                 min, max, (count < indirection.length - 1));

               panelMap.put(new Integer(index), columnPanel);

               numericColumnsBox.add(columnPanel);

            }

            JScrollPane numericScroll = new JScrollPane(numericColumnsBox);

            layout.addLayoutComponent(numericScroll,
               new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
               GridBagConstraints.CENTER, GridBagConstraints.BOTH,
               emptyInsets, 0, 0));
            add(numericScroll);

         }

         layout.addLayoutComponent(buttonPanel,
            new GridBagConstraints(0, 1, 2, 1, 1.0, 0.0,
            GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL,
            emptyInsets, 0, 0));
         add(buttonPanel);

      }

      public void actionPerformed(ActionEvent event) {

         Object src = event.getSource();

         if (src == abortButton) {
            viewCancel();
         }

         else if (src == doneButton) {

            int numRelevantColumns = indirection.length;

            indices = new int[numRelevantColumns];
            from_min = new double[numRelevantColumns];
            from_max = new double[numRelevantColumns];
            to_min = new double[numRelevantColumns];
            to_max = new double[numRelevantColumns];

            for (int count = 0; count < indirection.length; count++) {

               int index = indirection[count];

               ColumnPanel columnPanel = (ColumnPanel)panelMap.get(
                  new Integer(index));

               if (columnPanel.scaleCheck.isSelected()) {

                  indices[count] = index;

                  double d;
                  try {
                     d = Double.parseDouble(columnPanel.fromMinField.getText());
                  }
                  catch(NumberFormatException e) {
                     JOptionPane.showMessageDialog(this,
                        "not a number: " + columnPanel.fromMinField.getText(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                     return;
                  }
                  from_min[count] = d;

                  try {
                     d = Double.parseDouble(columnPanel.fromMaxField.getText());
                  }
                  catch(NumberFormatException e) {
                     JOptionPane.showMessageDialog(this,
                        "not a number: " + columnPanel.fromMaxField.getText(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                     return;
                  }
                  from_max[count] = d;

                  try {
                     d = Double.parseDouble(columnPanel.toMinField.getText());
                  }
                  catch(NumberFormatException e) {
                     JOptionPane.showMessageDialog(this,
                        "not a number: " + columnPanel.toMinField.getText(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                     return;
                  }
                  to_min[count] = d;

                  try {
                     d = Double.parseDouble(columnPanel.toMaxField.getText());
                  }
                  catch(NumberFormatException e) {
                     JOptionPane.showMessageDialog(this,
                        "not a number: " + columnPanel.toMaxField.getText(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                     return;
                  }
                  to_max[count] = d;

               }
               else {

                  indices[count] = -1;

               }

            }

			// Check the mins are less than the maxes.
			for (int i = 0 ; i < from_max.length ;i++) {
				if (from_max[i] < from_min[i]) {
					ColumnPanel columnPanel = (ColumnPanel)panelMap.get(
						new Integer(i));
					ErrorDialog.showDialog(this,
					columnPanel.lbl+": from maximum can not be less than the min.",
						"Max < Min");
					return;
				}
				if (to_max[i] < to_min[i]) {
					ColumnPanel columnPanel = (ColumnPanel)panelMap.get(
						new Integer(i));
					ErrorDialog.showDialog(this,
						columnPanel.lbl+": to maximum can not be less than the min.",
						"Max < Min");
					return;
				}
			}
            pushOutput(new ScalingTransformation(indices, from_min, from_max,
               to_min, to_max), 0);
            viewDone("Done");

         }

      }

   }

/******************************************************************************/
/* numeric column information panel                                           */
/******************************************************************************/

   private class ColumnPanel extends JPanel implements ActionListener {

      JCheckBox scaleCheck;
      JLabel fromLabel, fromMinLabel, fromMaxLabel,
             toLabel, toMinLabel, toMaxLabel;
      JTextField fromMinField, fromMaxField, toMinField, toMaxField;
	  String lbl;
      public ColumnPanel(String label, double fromMin, double fromMax,
         boolean addSeparator) {

         super();

		 lbl = label;
         scaleCheck = new JCheckBox();
         scaleCheck.setSelected(true);
         scaleCheck.addActionListener(this);

         fromMinField = new JTextField(6);
         fromMinField.setText(Double.toString(fromMin));
         fromMaxField = new JTextField(6);
         fromMaxField.setText(Double.toString(fromMax));
         toMinField = new JTextField(6);
         toMinField.setText("0.0");
         toMaxField = new JTextField(6);
         toMaxField.setText("1.0");

         fromLabel = new JLabel("  From:  ");
         fromMinLabel = new JLabel("min");
         fromMaxLabel = new JLabel("max");
         toLabel = new JLabel("  To:  ");
         toMinLabel = new JLabel("min");
         toMaxLabel = new JLabel("max");

         JPanel controlsPanel = new JPanel();
         controlsPanel.add(scaleCheck);
         controlsPanel.add(fromLabel);
         controlsPanel.add(fromMinLabel);
         controlsPanel.add(fromMinField);
         controlsPanel.add(fromMaxLabel);
         controlsPanel.add(fromMaxField);
         controlsPanel.add(toLabel);
         controlsPanel.add(toMinLabel);
         controlsPanel.add(toMinField);
         controlsPanel.add(toMaxLabel);
         controlsPanel.add(toMaxField);

         GridBagLayout layout = new GridBagLayout();
         setLayout(layout);

         JLabel labelLabel = new JLabel(label); // *grin*

         layout.addLayoutComponent(labelLabel, new GridBagConstraints(
            0, 0, 1, 1, 1.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            labelInsets, 0, 0));
         add(labelLabel);

         layout.addLayoutComponent(controlsPanel, new GridBagConstraints(
            1, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.EAST, GridBagConstraints.NONE,
            emptyInsets, 0, 0));
         add(controlsPanel);

         if (addSeparator) {

            JSeparator separator = new JSeparator();

            layout.addLayoutComponent(separator, new GridBagConstraints(
               0, 1, 2, 1, 1.0, 0.0,
               GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL,
               emptyInsets, 0, 0));
            add(separator);

         }

      }

      public void actionPerformed(ActionEvent event) {

         boolean selected = scaleCheck.isSelected();

         fromMinField.setEnabled(selected);
         fromMaxField.setEnabled(selected);
         toMinField.setEnabled(selected);
         toMaxField.setEnabled(selected);

         fromLabel.setEnabled(selected);
         fromMinLabel.setEnabled(selected);
         fromMaxLabel.setEnabled(selected);
         toLabel.setEnabled(selected);
         toMinLabel.setEnabled(selected);
         toMaxLabel.setEnabled(selected);

      }

   }
}
/******************************************************************************/
/* Transformation                                                             */
/******************************************************************************/

class ScalingTransformation implements Transformation {

  private int[] indices;
  private double[] from_min, from_max, to_min, to_max;

  ScalingTransformation(int[] indices, double[] from_min, double[] from_max,
	 double[] to_min, double[] to_max) {

	 this.indices = indices;
	 this.from_min = from_min;
	 this.from_max = from_max;
	 this.to_min = to_min;
	 this.to_max = to_max;

  }

  public boolean transform(MutableTable table) {

	 if (indices.length == 0 || table.getNumRows() == 0) {
		// no transformation is necessary
		return true;
	 }

	 for (int count = 0; count < indices.length; count++) {

		int index = indices[count];

		if (index < 0) // this column wasn't selected for scaling
		   continue;

		double[] data = new double[table.getNumRows()];
		boolean[] missing = new boolean[table.getNumRows()];
		double from_range = from_max[count] - from_min[count];
		double to_range = to_max[count] - to_min[count];

		double d;

		if (from_range == 0) { // no variance in data...

		   d = table.getDouble(0, index);

		   if (d >= to_min[count] && d <= to_max[count]) {
			  // data is in new range; leave it alone
			  for (int j = 0; j < data.length; j++) {
				 data[j] = table.getDouble(j, index);
				 missing[j] = table.isValueMissing(j, index);
			  }
		   }
		   else {
			  // data is out of new range; set to min
			  for (int j = 0; j < data.length; j++) {
			     missing[j] = table.isValueMissing(j, index);
			     if (missing[j])
			     	data[j] = table.getDouble(j, index);
			     else
				    data[j] = to_min[count];
		      }
		   }
		}
		else { // ordinary data; scale away!

		   for (int j = 0; j < data.length; j++) {
		   	  if (table.isValueMissing(j, index)) {
		   	   	 data[j] = table.getDouble(j, index);
		   	     missing[j] = true;
		   	  } else {
			     d = table.getDouble(j, index);
			     data[j] = (d - from_min[count])*to_range/from_range
					  + to_min[count];
				 missing[j] = false;
		   	  }
		   }
		}

		// Create the new column, set the column in the table, then
		// set the new scaled values. In this way, we preserve the subset.
		Column col = ColumnUtilities.toDoubleColumn(table.getColumn(index));
		table.setColumn(col, index);
		for (int i = 0 ; i < data.length ; i++) {
		   if (missing[i])
		      table.setDouble(table.getMissingDouble(), i, index);
		   else
		      table.setDouble(data[i], i, index);
		}
	 }
	 return true;
  }
}

/**
 * QA comments:
 * 2-28-03  Vered started qa.
 *          added to module info a note about missing values handling.
 *
 * 11-04-03 Vered started QA process
 *          UI allows insertion of min values that are greater than the max values.
 *          [bug 115] fixed.
 *
 * 11-25-03 handles missing values as if they were real values. missing values
 *          should be preserved and left as they are, and not be considered when
 *          calculating upper and lower bounds for each column, and while
 *          scaling. [bug 147] fixed
 *
 * 11-25-03 Tom added missing value support (bug 147). Missing values are simply ignored. and
 * 			preserved in the new column.
 *
 * 12-08-03 Module is ready fro basic 4.
 *
 *
 * 01-12-04: module is pulled back into qa process.
 * bug 221 - handling of subset tables. table viewer throws an array index out of
 * bounds exception with subset table had a scaling transformation applied to.
 * (fixed)
 *
 * 01-21-04: modules is ready for basic 4.

 */
