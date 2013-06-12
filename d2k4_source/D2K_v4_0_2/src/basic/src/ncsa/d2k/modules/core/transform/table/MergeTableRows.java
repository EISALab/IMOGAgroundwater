package ncsa.d2k.modules.core.transform.table;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.userviews.swing.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.gui.*;

import ncsa.d2k.modules.core.transform.StaticMethods;

/**
 * Merge rows in a table based on identical key attributes.
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */
public class MergeTableRows extends HeadlessUIModule {

	///////
	// variables and methods used to preserve settings between invocations
	///////

	private String lastControl;
	public String getLastControl() {
		return lastControl;
	}
	public void setLastControl(String s) {
		lastControl = s;
	}

	private HashSet lastKeys;
	public HashSet getLastKeys() {
		return lastKeys;
	}
	public void setLastKeys(HashSet s) {
		lastKeys = s;
	}

	private HashSet lastToMerge;
	public HashSet getLastToMerge() {
		return lastToMerge;
	}
	public void setLastToMerge(HashSet s) {
		lastToMerge = s;
	}

	private String lastMergeMethod;
	public String getLastMergeMethod() {
		return lastMergeMethod;
	}
	public void setLastMergeMethod(String s) {
		lastMergeMethod = s;
	}

	///////
	// standard info-related methods
	///////

	/**
	 * Return a list of the property descriptions that a user may edit.
	 * @return a list of the property descriptions.
	 */
	public PropertyDescription[] getPropertiesDescriptions() {
		PropertyDescription[] pds = new PropertyDescription[3];
		pds[0] = super.supressDescription;
		pds[1] =
			new PropertyDescription(
				"control",
				"Control Column",
				"Name of control column for merging (will be used if \"Supress User Interface Display\" is set to true");
		pds[2] =
			new PropertyDescription(
				"type",
				"Merging Method",
				"Type of merging method (Sum, Average, Minimum, Maximum or Count) will be used if \"Supress User Interface Display\" is set to true");
		return pds;
	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "Merge Table Rows";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch (index) {
			case 0 :
				return "Input Table";
			default :
				return "No such input.";
		}
	}

	/**
	 * Return the human readable name of the indexed output.
	 * @param index the index of the output.
	 * @return the human readable name of the indexed output.
	 */
	public String getOutputName(int index) {
		switch (index) {
			case 0 :
				return "Output Table";
			default :
				return "No such output.";
		}
	}

	public String getInputInfo(int i) {
		switch (i) {
			case 0 :
				return "The table to be processed by the row merge operation.";
			default :
				return "No such input.";
		}
	}

	public String getOutputInfo(int i) {
		switch (i) {
			case 0 :
				return "The table that results from the row merge operation.";
			default :
				return "No such output.";
		}
	}

	public String[] getInputTypes() {
		String[] types =
			{ "ncsa.d2k.modules.core.datatype.table.basic.TableImpl" };
		return types;
	}

	public String[] getOutputTypes() {
		String[] types =
			{ "ncsa.d2k.modules.core.datatype.table.basic.TableImpl" };
		return types;
	}

	public String getModuleInfo() {

		String s = "<p>Overview: ";
		s
			+= "This module merges rows in a table that have the same values for one or more key attributes.  ";
		s
			+= "The user selects the key attribute(s) and specifies other information about how the rows should be merged. ";

		s += "</p><p>Detailed Description: ";
		s
			+= "This module merges multiple rows from the <i>Input Table</i> into a single row in the <i>Output Table</i>. ";
		s
			+= "Rows are merged if they have identical values for one or more key attributes. ";
		s
			+= "A set of rows from the <i>Input Table</i> that has identical values for the key attributes are called ";
		s
			+= "<i>matching rows</i>.   One output row is produced for each set of matching rows. ";
		s
			+= "The module presents a dialog that allows selection of the key attribute(s) and control over additional ";
		s += "merge parameters. ";

		s += "</p><p>";
		s
			+= "The module dialog lists all of the attributes in the <i>Input Table</i> and allows the user to select one ";
		s += "or more of them to be the <i>Key</i> for the merge. ";
		s
			+= "The module merges table rows with identical values for all of the specified Key attributes. ";
		s
			+= "The module dialog also lists all of the numeric attributes in the <i>Input Table</i> and allows the user ";
		s += "to select one of these as the <i>Control</i> attribute. ";
		s
			+= "The Control determines which row in each matching row set will be used as the basis ";
		s += "for the resulting merged row. ";
		s
			+= "For a set of matching rows, the row with the maximum value for the Control attribute is the control row. ";

		s += "</p><p>";
		s
			+= "The module dialog also lists the numeric attributes under the <i>Merge</i> heading, ";
		s += "and allows the user to select ";
		s
			+= "one or more of these attributes to be merged across matching rows using the operation specified via the ";
		s += "dialog's <i>Merge Method</i>. ";
		s += "The possible merge methods are <i>Sum</i>, <i>Average</i>, ";
		s += "<i>Maximum</i>, <i>Minimum</i>, and <i>Count</i>. ";
		s
			+= "For each of the Merge attributes selected, the merge method will be applied to the attribute values of all ";
		s
			+= "matching rows in a set and the result will appear in the output merged row. ";

		s += "</p><p>";
		s
			+= "Each row in the <i>Output Table</i> will have the values of the control row attributes ";
		s
			+= "for all string attributes and for the numeric attributes that were not selected as Merge attributes. ";
		s += "That is to say, all data that is not ";
		s
			+= "merged using the merge method is simply copied from the control row for each set of matching rows. ";

		s += "</p><p>Data Type Restrictions: ";
		s
			+= "The <i>Input Table</i> must contain at least one numeric attribute that can be used as the <i>Control</i>. ";
		s
			+= "In addition, the Merge Method can only be applied to numeric attributes. ";

		s += "</p><p>Data Handling: ";
		s
			+= "The <i>Input Table</i> is not modified.   The <i>Output Table</i> is created by the module. ";

		s += "</p><p>Missing Values Handling: ";
		s
			+= "The key, control and merge columns in the <i>Input Table</i> should be clean of missing values. "
			+ "If a missing value is encountered an Exception will be thrown. Use 'RemoveRowsWithMissingValues' module "
			+ "before this module, to clean the <i>Input Table</i>.";

		s += "</p><p>Scalability: ";
		s
			+= "This module should scale very well for tables where the key attribute has a limited number ";
		s += "of unique values. When that is not the case, ";
		s
			+= "in other words, if the key attribute selected is not nominal, the module will not scale ";
		s += "well.</p>";

		return s;
	}

	//////
	// the meat
	/////

	protected UserView createUserView() {
		return new CleanView();
	}

	public String[] getFieldNameMapping() {
		return null;
	}

	private static final String SUM = "Sum";
	private static final String AVE = "Average";
	private static final String MAX = "Maximum";
	private static final String MIN = "Minimum";
	private static final String CNT = "Count";

	private class CleanView extends JUserPane {
		JList keyAttributeList;
		DefaultListModel keyListModel;
		JList controlAttribute;
		DefaultListModel controlListModel;
		JList attributesToMerge;
		DefaultListModel mergeListModel;

		JComboBox mergeMethod;

		TableImpl table;

		HashMap columnLookup;

		public void setInput(Object o, int id) throws Exception {
			table = (TableImpl) o;

			// clear all lists
			keyListModel.removeAllElements();
			controlListModel.removeAllElements();
			mergeListModel.removeAllElements();

			columnLookup = new HashMap(table.getNumColumns());
			String longest = "";

			HashSet selectedKeys = new HashSet();
			HashSet selectedControls = new HashSet();
			HashSet selectedMerges = new HashSet();

			// now add the column labels
			// keyListModel entries can be string or numeric type columns
			// controlListModel and mergeListModel entries must be numeric type columns
			int ni = 0; // index for numeric type selections
			for (int i = 0; i < table.getNumColumns(); i++) {
				if (table.hasMissingValues(i))
					continue;

				columnLookup.put(table.getColumnLabel(i), new Integer(i));

				keyListModel.addElement(table.getColumnLabel(i));
				if (lastKeys != null
					&& lastKeys.contains(table.getColumnLabel(i))) {
					selectedKeys.add(new Integer(i));
				}

				if (table.getColumn(i) instanceof NumericColumn) {
					controlListModel.addElement(table.getColumnLabel(i));
					if (lastControl != null
						&& lastControl.equals(table.getColumnLabel(i))) {
						selectedControls.add(new Integer(ni));
					}

					mergeListModel.addElement(table.getColumnLabel(i));
					if (lastToMerge != null
						&& lastToMerge.contains(table.getColumnLabel(i))) {
						selectedMerges.add(new Integer(ni));
					}

					ni++;
				}
				if (table.getColumnLabel(i).length() > longest.length())
					longest = table.getColumnLabel(i);

			}

			// Don't force user to Abort if table data is wrong - abort for them with message.
			if (controlListModel.size() == 0) {
				throw new Exception(
					getAlias()
						+ ": Input Table does not contain any numeric attributes - itinerary will be aborted");
			}

			keyAttributeList.setPrototypeCellValue(longest);
			controlAttribute.setPrototypeCellValue(longest);
			attributesToMerge.setPrototypeCellValue(longest);

			int[] selKeys = new int[selectedKeys.size()];
			int idx = 0;
			Iterator iter = selectedKeys.iterator();
			while (iter.hasNext()) {
				Integer num = (Integer) iter.next();
				selKeys[idx] = num.intValue();
				idx++;
			}

			int[] selControls = new int[selectedControls.size()];
			idx = 0;
			iter = selectedControls.iterator();
			while (iter.hasNext()) {
				Integer num = (Integer) iter.next();
				selControls[idx] = num.intValue();
				idx++;
			}

			int[] selMerge = new int[selectedMerges.size()];
			idx = 0;
			iter = selectedMerges.iterator();
			while (iter.hasNext()) {
				Integer num = (Integer) iter.next();
				selMerge[idx] = num.intValue();
				idx++;
			}

			keyAttributeList.setSelectedIndices(selKeys);
			controlAttribute.setSelectedIndices(selControls);
			attributesToMerge.setSelectedIndices(selMerge);
			mergeMethod.setSelectedItem(lastMergeMethod);
		}

		public void initView(ViewModule m) {
			keyAttributeList = new JList();
			keyListModel = new DefaultListModel();
			keyAttributeList.setModel(keyListModel);
			controlAttribute = new JList();
			controlListModel = new DefaultListModel();
			controlAttribute.setModel(controlListModel);
			controlAttribute.setSelectionMode(
				ListSelectionModel.SINGLE_SELECTION);
			attributesToMerge = new JList();
			mergeListModel = new DefaultListModel();
			attributesToMerge.setModel(mergeListModel);

			JScrollPane jsp1 = new JScrollPane(keyAttributeList);
			jsp1.setColumnHeaderView(new JLabel("Key"));
			JScrollPane jsp2 = new JScrollPane(controlAttribute);
			jsp2.setColumnHeaderView(new JLabel("Control"));
			JScrollPane jsp3 = new JScrollPane(attributesToMerge);
			jsp3.setColumnHeaderView(new JLabel("To Merge"));

			String[] methods = { SUM, AVE, MAX, MIN, CNT };
			mergeMethod = new JComboBox(methods);
			JPanel pnl = new JPanel();
			pnl.add(new JLabel("Merge Method"));
			JPanel pn2 = new JPanel();
			pn2.add(mergeMethod);

			Box b1 = new Box(BoxLayout.Y_AXIS);
			b1.add(jsp3);
			b1.add(pnl);
			b1.add(pn2);

			Box b2 = new Box(BoxLayout.X_AXIS);
			b2.add(jsp1);
			b2.add(jsp2);
			b2.add(b1);

			setLayout(new BorderLayout());
			add(b2, BorderLayout.CENTER);

			JPanel buttonPanel = new JPanel();
			JButton abort = new JButton("Abort");
			abort.addActionListener(new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					viewCancel();
				}
			});
			JButton done = new JButton("Done");
			done.addActionListener(new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					Object[] keys = keyAttributeList.getSelectedValues();
					Object control = controlAttribute.getSelectedValue();
					Object[] merges = attributesToMerge.getSelectedValues();
					final Object type = mergeMethod.getSelectedItem();

					if (keys == null || keys.length == 0) {
						ErrorDialog.showDialog(
							"You must select a key attribute.",
							"Error");
						return;
					}
					if (control == null) {
						ErrorDialog.showDialog(
							"You must select a control attribute.",
							"Error");
						return;
					}
					if (merges == null || merges.length == 0) {
						ErrorDialog.showDialog(
							"You must select one or more attributes to merge.",
							"Error");
						return;
					}
					if (type == null) {
						ErrorDialog.showDialog(
							"You must select a method to merge by.",
							"Error");
						return;
					}

					final int[] ks = new int[keys.length];
					for (int i = 0; i < keys.length; i++)
						ks[i] =
							((Integer) columnLookup.get(keys[i])).intValue();

					final int[] ms = new int[merges.length];
					for (int i = 0; i < merges.length; i++)
						ms[i] =
							((Integer) columnLookup.get(merges[i])).intValue();

					final int ctrl =
						((Integer) columnLookup.get(control)).intValue();

					//mTbl;

					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							//changed by vered - 9/18/03
							//moved the code of the merging methods to
							//MergingClass, so it could be static
							//and reused by doit.

							final MutableTable mTbl =
								MergingClass.mergeTable(
									ks,
									ms,
									ctrl,
									(String) type,
									table);
							pushOutput(mTbl, 0);
							viewDone("Done");

							//                                                mergeTable(ks, ms, ctrl, (String)type);
						}
					});

					HashSet usedKeys = new HashSet();
					for (int i = 0; i < keys.length; i++)
						usedKeys.add(keys[i]);

					HashSet usedMerges = new HashSet();
					for (int i = 0; i < merges.length; i++)
						usedMerges.add(merges[i]);

					setLastControl(control.toString());
					setLastKeys(usedKeys);
					setLastToMerge(usedMerges);
					setLastMergeMethod(type.toString());

					//headless conversion support
					setControl(control.toString());
					setKeys(usedKeys.toArray());
					setMerges(usedMerges.toArray());
					setType(type.toString());
					//headless conversion support
				}
			});
			buttonPanel.add(abort);
			buttonPanel.add(done);
			add(buttonPanel, BorderLayout.SOUTH);
		}

		//the following was commented out by vered - 9/18/03
		//and was trasnferred to MergingClass.java (all the merge methods)
		//and to KetSet.java

		public Dimension getPreferredSize() {
			return new Dimension(400, 300);
		}

	} //CleanView

	//headless conversion support
	private String[] keys;
	//key columns' names. rows with same value in these columns will be merged
	private String control;
	//control column name. the values of the row with largest
	//value in this column will be copied to the merged row.
	private String[] merges; //column names to merge their values.
	private String type; //type of merging (sum, count, etc.)

	//setter and getter methods.
	public Object[] getKeys() {
		return keys;
	}
	public void setKeys(Object[] k) {
		keys = new String[k.length];
		for (int i = 0; i < k.length; i++)
			keys[i] = (String) k[i];
	}

	public String getControl() {
		return control;
	}
	public void setControl(String c) {
		control = c;
	}

	public Object[] getMerges() {
		return merges;
	}
	public void setMerges(Object[] m) {
		merges = new String[m.length];
		for (int i = 0; i < m.length; i++)
			merges[i] = (String) m[i];
	}

	public String getType() {
		return type;
	}
	public void setType(String t) {
		type = t;
	}

	public void doit() throws Exception {
		final Table table = (Table) pullInput(0);

		HashMap columns = StaticMethods.getAvailableAttributes(table);

		//validating that properties are not null.
		if (keys == null
			|| keys.length == 0
			|| merges == null
			|| merges.length == 0
			|| control == null
			|| control.length() == 0
			|| type == null
			|| type.length() == 0)
			throw new Exception(
				this.getAlias()
					+ " has not been configured. Before running headless, run with the gui and configure the parameters.");

		//fnding out which of keys are relevant to the input table.

		final int[] ks = StaticMethods.getIntersectIds(keys, columns);
		//ks[i] is index of column keys[i]
		if (ks.length < keys.length)
			throw new Exception(
				"Some of the configured Key Columns were not found in the "
					+ "input table "
					+ ((table.getLabel() == null)
						? ""
						: (" " + table.getLabel()))
					+ ". Please reconfigure the module.");

		for (int i = 0; i < ks.length; i++) {
			if (table.hasMissingValues(ks[i]))
				throw new Exception(
					this.getAlias()
						+ " : A key column ("
						+ table.getColumnLabel(ks[i])
						+ ") has missing values. That is not allowed.");
		}

		final int[] ms = StaticMethods.getIntersectIds(merges, columns);
		; //ms[i] is index of column merges[i]
		if (ms.length < merges.length)
			throw new Exception(
				"Some of the configured Merging Columns were not found in the "
					+ "input table "
					+ ((table.getLabel() == null)
						? ""
						: (" " + table.getLabel()))
					+ ". Please reconfigure the module.");

		for (int i = 0; i < ms.length; i++) {
			if (table.hasMissingValues(ms[i]))
				throw new Exception(
					this.getAlias()
						+ " : A merge column ("
						+ table.getColumnLabel(ms[i])
						+ ") has missing values. That is not allowed.");
		}

		final int cntrl = StaticMethods.getID(control, columns);
		//cntrl is index of column control
		if (cntrl == -1)
			throw new Exception(
				getAlias()
					+ "The control column \""
					+ control
					+ "\" could not be found in "
					+ "the input table"
					+ ((table.getLabel() == null)
						? ""
						: (" " + table.getLabel()))
					+ ". Please reconfigure the module.");

		if (table.hasMissingValues(cntrl))
			throw new Exception(
				this.getAlias()
					+ " : The control column ("
					+ control
					+ ") has missing values. That is not allowed.");

		final String _type = type;

		if (!(type.equalsIgnoreCase(MergingClass.AVE)
			|| type.equalsIgnoreCase(MergingClass.CNT)
			|| type.equals(MergingClass.MAX)
			|| type.equalsIgnoreCase(MergingClass.MIN)
			|| type.equalsIgnoreCase(MergingClass.SUM)))
			throw new Exception(
				getAlias()
					+ ": The merging type is illegal!\n"
					+ "Please reconfigure this module using the properties editor "
					+ "or via a GUI run, so it can run Headless.");

		//end validation.
		final MutableTable mtbl =
			MergingClass.mergeTable(ks, ms, cntrl, _type, table);
		pushOutput(mtbl, 0);

	} //doit

	//headless conversion support

}

// QA Comments
// 2/24/03 - Handed off to QA by Loretta - replaces CleanAndMergeTable.
// 2/25/03 - Ruth started QA process.
//           Updated documentation; Removed OutputPort that just copied thru
//           InputTable; Reordered Control and Merge columns in UI so that
//           Merge above MergeMethod;  Added Mininum MergeMethod option;
// 2/27/03 - Want to raise Exception if InputTable doesn't have numeric
//           attributes but not possible in UI module.  David C is looking into
//           fix.   Committing current version (w/ debug stments) to CVS core.
// 3/5/03  - A6 allows Exeception in setValue;  Commmited to Basic
//           WISH:  After discussion with Tom this should be reworked at
//           some point to use Tables, not TableImpls.
// 7/18/03 - RA: Added "Count" option in response to request from Loretta.
//           WISH:  Allow "All" option where all stats are computed and
//           new columns are added.  (see loretta for details)
// END QA Comments

//QA Comments
//10/23/03 - vered started QA process.
//           handling of missing values - when the merged table is viewed by a
//           table viewer, missing values represented by zero. in a addition to
//           that - the new table does not "know" the value is missing...
//11/03/03 - problem with preservation of missing values is fixed.
//           modules works well with MutableTableImpl, ExampleTablEImpl
//           another problem with missing values: calculation of minimum/average
//           right now - a missing value is considered to be zero when calculating
//           these values. (this problem is solved through new guide lines)

//11/20/03 - module should throw exception if a missing value is encountered in
//           key, control or merge column. [bug 135]. this is fixed. instead
//           of throwing an exception the module does not list the columns with
//           missing values.

//12-05-03: modules is ready for basic 4.


      /**
 * 12-29-03:
 * pulling back module into qa process:
 * bug 190 - Array Index Out of Bound Exception with average merging and missing values.
 *
 * 01-01-04:
 * bug 190 is fixed
 *
 * 01-05-04:
 * module is ready for basic
*/
