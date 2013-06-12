package ncsa.d2k.modules.core.transform.attribute;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.userviews.swing.*;
import ncsa.d2k.core.modules.UserView;
import ncsa.gui.Constrain;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;

import ncsa.d2k.modules.core.datatype.table.*;

import ncsa.d2k.modules.core.transform.StaticMethods;

/**
 ChooseAttributes.java  (previously ChooseFields)

 Allows the user to choose which columns of a table are inputs and outputs.
 Then assigns them in an ExampleTable.

 @author Peter Groves, c/o David Clutter
 *
 *
 */
public class ChooseAttributes extends HeadlessUIModule {

	/**
	 Return a description of the function of this module.
	 @return A description of this module.
	 */
	public String getModuleInfo() {
		String info = "<p>Overview: ";
		info
			+= "This module allows the user to choose which columns of a table are inputs and outputs.";
		info += "</p><p>Detailed Description: ";
		info
			+= "This module outputs an <i>Example Table</i> with the input and output features assigned. ";
		info
			+= "Inputs and outputs do not have to be selected, nor do they have to be mutually exclusive. ";
		info += "</p><p>Data Handling: ";
		info
			+= "This module does not modify the data in the table. It only sets the input and output features.";
		return info;
	}

	/**
	 Return the name of this module.
	 @return The name of this module.
	 */
	public String getModuleName() {
		return "Choose Attributes";
	}

	/**
	 Return a String array containing the datatypes the inputs to this
	 module.
	 @return The datatypes of the inputs.
	 */
	public String[] getInputTypes() {
		String[] types = { "ncsa.d2k.modules.core.datatype.table.Table" };
		return types;
	}

	/**
	 Return a String array containing the datatypes of the outputs of this
	 module.
	 @return The datatypes of the outputs.
	 */
	public String[] getOutputTypes() {
		String[] types =
			{ "ncsa.d2k.modules.core.datatype.table.ExampleTable" };
		return types;
	}

	/**
	 Return a description of a specific input.
	 @param i The index of the input
	 @return The description of the input
	 */
	public String getInputInfo(int i) {
		switch (i) {
			case 0 :
				return "The Table to choose inputs and outputs from.";
			default :
				return "No such input";
		}
	}

	/**
	 Return the name of a specific input.
	 @param i The index of the input.
	 @return The name of the input
	 */
	public String getInputName(int i) {
		switch (i) {
			case 0 :
				return "Table";
			default :
				return "No such input";
		}
	}

	/**
	 Return the description of a specific output.
	 @param i The index of the output.
	 @return The description of the output.
	 */
	public String getOutputInfo(int i) {
		switch (i) {
			case 0 :
				return "The Example Table with input and output features assigned.";
			default :
				return "No such output";
		}
	}

	/**
	 Return the name of a specific output.
	 @param i The index of the output.
	 @return The name of the output
	 */
	public String getOutputName(int i) {
		switch (i) {
			case 0 :
				return "Example Table";
			default :
				return "No such output";
		}
	}

	public PropertyDescription[] getPropertiesDescriptions() {
		PropertyDescription[] desc = new PropertyDescription[1];
		desc[0] = this.supressDescription;
		return desc;
	}

	public void doit() throws Exception {

		Table table = (Table) this.pullInput(0);

		if (selectedInputs == null || selectedOutputs == null)
			throw new Exception(
				getAlias()
					+ " has not been configured. Before running headless, run with the gui and configure the parameters.");

		// Map each column name to an index for the column
		HashMap colindices = new HashMap();
		for (int i = 0; i < table.getNumColumns(); i++) {
			String label = table.getColumnLabel(i);
			colindices.put(label.toUpperCase(), new Integer(i));
		}

		// Create the input feature index array.
		Object[] selected = this.getSelectedInputs();
		int[] inputFeatures = new int[0];
		String[] selectedNames;

		if (selected.length == 0) {
			System.out.println(
				getAlias()
					+ ": Warning - No input attributes were selected. Skipping "
					+ "setting of input attributes.");
		} else {
			//vered - test that all columns are really in the map
			selectedNames = new String[selected.length];
			for (int i = 0; i < selectedNames.length; i++)
				selectedNames[i] = (String) selected[i];

			inputFeatures =
				StaticMethods.getIntersectIds(selectedNames, colindices);

			if (inputFeatures.length < selectedNames.length)
				throw new Exception(
					getAlias()
						+ ": Some of the configured input attributes were not found in the input table."
						+ " Please reconfigure this module via a GUI run so it can run Headless.");
			//the following was commented out and replaced by the previous code line.
			//vered.
			/*
			   int[] inputFeatures = new int[selected.length];
			   for (int i = 0; i < selected.length; i++) {
			    String s = (String) selected[i];
			    Integer ii = (Integer) colindices.get (s);
			    inputFeatures[i] = ii.intValue ();
			   }
			 */
		}

		// Create the output features array
		selected = this.getSelectedOutputs();
		int[] outputFeatures = new int[0];

		if (selected == null || selected.length == 0) {
			System.out.println(
				getAlias()
					+ ": Warning - No output attributes were selected. Skipping "
					+ "setting of output attributes.");
		} else {
			selectedNames = new String[selected.length];
			for (int i = 0; i < selectedNames.length; i++)
				selectedNames[i] = (String) selected[i];
			//the following was commented out and replaced by the previous code line.
			//vered.
			outputFeatures =
				StaticMethods.getIntersectIds(selectedNames, colindices);

			if (outputFeatures.length < selectedNames.length)
				throw new Exception(
					getAlias()
						+ ": Some of the configured output attributes were not found in the input table."
						+ " Please reconfigure this module via a GUI run so it can run Headless.");

			/*int[] outputFeatures = new int[selected.length];
			for (int i = 0; i < selected.length; i++) {
				String s = (String) selected[i];
				Integer ii = (Integer) colindices.get (s);
				outputFeatures[i] = ii.intValue ();
			}
			*/
		}

		// Create the example table and push it.
		ExampleTable et = table.toExampleTable();
		et.setInputFeatures(inputFeatures);
		et.setOutputFeatures(outputFeatures);
		this.pushOutput(et, 0);
	}

	/**
	 Return the UserView
	 @return the UserView
	 */
	protected UserView createUserView() {
		return new AttributeView();
	}

	/**
	 Not used
	 */
	protected String[] getFieldNameMapping() {
		return null;
	}

	/** holds the names of the inputs attributes. */
	Object[] selectedInputs;
	public Object[] getSelectedInputs() {
		return selectedInputs;
	}
	public void setSelectedInputs(Object[] nsi) {
		selectedInputs = nsi;
	}

	/** holds the names of the toutput attributes. */
	Object[] selectedOutputs;
	public Object[] getSelectedOutputs() {
		return selectedOutputs;
	}
	public void setSelectedOutputs(Object[] nsi) {
		selectedOutputs = nsi;
	}

	/**
	 The user view class
	 */
	class AttributeView extends JUserPane implements ActionListener {
		private Table table; //Old data
		private ExampleTable et; //Updated table
		private ChooseAttributes module;
		private JButton abort;
		private JButton done;

		private JList inputList;
		private JList outputList;

		private JLabel inputLabel;
		private JLabel outputLabel;

		boolean labels;
		private HashMap inputToIndexMap;
		private HashMap outputToIndexMap;

		private JCheckBoxMenuItem miColumnOrder;
		private JCheckBoxMenuItem miAlphaOrder;

		private JMenuBar menuBar;

		/**
		 Initialize
		 */
		public void initView(ViewModule v) {
			module = (ChooseAttributes) v;
			abort = new JButton("Abort");
			done = new JButton("Done");
			abort.addActionListener(this);
			done.addActionListener(this);
			menuBar = new JMenuBar();
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
		}

		public Object getMenu() {
			return menuBar;
		}

		/**
		 Called when inputs arrive
		 */
		public void setInput(Object o, int id) {
			if (id == 0) {
				table = (Table) o;
				this.removeAll();
				addComponents();
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
			String[] labels = orderedLabels();
			if (table.getColumnLabel(0).equals("")) {
				miColumnOrder.setEnabled(false);
				miAlphaOrder.setEnabled(false);
			} else {
				miColumnOrder.setEnabled(true);
				miAlphaOrder.setEnabled(true);
			}

			inputList = new JList();
			DefaultListModel dlm = new DefaultListModel();
			for (int i = 0; i < labels.length; i++)
				dlm.addElement(labels[i]);
			inputList.setModel(dlm);
			if (table instanceof ExampleTable) {
				int[] ins = ((ExampleTable) table).getInputFeatures();
				if (ins != null) {
					int[] sel = new int[ins.length];
					for (int i = 0; i < ins.length; i++) {
						String s = table.getColumnLabel(ins[i]);
						Integer ii = (Integer) inputToIndexMap.get(s);
						sel[i] = ii.intValue();
					}
					inputList.setSelectedIndices(sel);
				}
			}
			outputList = new JList(/*labels*/
			);
			dlm = new DefaultListModel();
			for (int i = 0; i < labels.length; i++)
				dlm.addElement(labels[i]);
			outputList.setModel(dlm);
			if (table instanceof ExampleTable) {
				//outputList.setSelectedIndices(((ExampleTable)table).getOutputFeatures());
				int[] ins = ((ExampleTable) table).getOutputFeatures();
				if (ins != null) {
					int[] sel = new int[ins.length];
					for (int i = 0; i < ins.length; i++) {
						String s = table.getColumnLabel(ins[i]);
						Integer ii = (Integer) outputToIndexMap.get(s);
						sel[i] = ii.intValue();
					}
					outputList.setSelectedIndices(sel);
				}
			}
			JScrollPane leftScrollPane = new JScrollPane(inputList);
			JScrollPane rightScrollPane = new JScrollPane(outputList);

			inputLabel = new JLabel("Input Attributes");
			inputLabel.setHorizontalAlignment(SwingConstants.CENTER);

			outputLabel = new JLabel("Output Attributes");
			outputLabel.setHorizontalAlignment(SwingConstants.CENTER);

			back.setLayout(new GridBagLayout());

			Constrain.setConstraints(
				back,
				inputLabel,
				0,
				0,
				1,
				1,
				GridBagConstraints.BOTH,
				GridBagConstraints.CENTER,
				0,
				0);
			Constrain.setConstraints(
				back,
				outputLabel,
				1,
				0,
				1,
				1,
				GridBagConstraints.BOTH,
				GridBagConstraints.CENTER,
				0,
				0);
			Constrain.setConstraints(
				back,
				leftScrollPane,
				0,
				1,
				1,
				1,
				GridBagConstraints.BOTH,
				GridBagConstraints.CENTER,
				1,
				1);
			Constrain.setConstraints(
				back,
				rightScrollPane,
				1,
				1,
				1,
				1,
				GridBagConstraints.BOTH,
				GridBagConstraints.CENTER,
				1,
				1);

			JPanel buttons = new JPanel();
			buttons.add(abort);
			buttons.add(done);

			this.add(back, BorderLayout.CENTER);
			this.add(buttons, BorderLayout.SOUTH);
		}

		/**
		 Listen for ActionEvents
		 */
		public void actionPerformed(ActionEvent e) {
			Object src = e.getSource();
			if (src == abort)
				module.viewCancel();
			else if (src == done) {
				//  if(checkChoices()){
				setFieldsInTable();
				pushOutput(et, 0);
				viewDone("Done");
				et = null;
				this.removeAll();
				//}
			} else if (src == miColumnOrder) {
				String[] labels = orderedLabels();
				miAlphaOrder.setState(false);
				DefaultListModel dlm = (DefaultListModel) inputList.getModel();
				dlm.removeAllElements();
				for (int i = 0; i < labels.length; i++) {
					dlm.addElement(labels[i]);
				}
				dlm = (DefaultListModel) outputList.getModel();
				dlm.removeAllElements();
				for (int i = 0; i < labels.length; i++) {
					dlm.addElement(labels[i]);
				}
			} else if (src == miAlphaOrder) {
				String[] labels = alphabetizeLabels();
				miColumnOrder.setState(false);
				DefaultListModel dlm = (DefaultListModel) inputList.getModel();
				dlm.removeAllElements();
				for (int i = 0; i < labels.length; i++) {
					dlm.addElement(labels[i]);
				}
				dlm = (DefaultListModel) outputList.getModel();
				dlm.removeAllElements();
				for (int i = 0; i < labels.length; i++) {
					dlm.addElement(labels[i]);
				}
			}
		}

		private final String[] orderedLabels() {
			String[] labels = new String[table.getNumColumns()];

			inputToIndexMap = new HashMap(labels.length);
			outputToIndexMap = new HashMap(labels.length);
			for (int i = 0; i < labels.length; i++) {
				String label = table.getColumnLabel(i);
				if (label.equals(""))
					label = new String("Column " + Integer.toString(i));
				labels[i] = label;
				inputToIndexMap.put(labels[i], new Integer(i));
				outputToIndexMap.put(labels[i], new Integer(i));
			}
			return labels;
		}

		private final String[] alphabetizeLabels() {
			String[] labels = new String[table.getNumColumns()];
			inputToIndexMap = new HashMap(labels.length);
			outputToIndexMap = new HashMap(labels.length);
			for (int i = 0; i < labels.length; i++) {
				labels[i] = table.getColumnLabel(i);
				inputToIndexMap.put(labels[i], new Integer(i));
				outputToIndexMap.put(labels[i], new Integer(i));
			}
			Arrays.sort(labels, new StringComp());
			return labels;
		}

		private final class StringComp implements Comparator {
			public int compare(Object o1, Object o2) {
				String s1 = (String) o1;
				String s2 = (String) o2;
				return s1.toLowerCase().compareTo(s2.toLowerCase());
			}

			public boolean equals(Object o) {
				return super.equals(o);
			}
		}

		private void setFieldsInTable() {

			et = table.toExampleTable();

			Object[] selected = inputList.getSelectedValues();
			int[] inputFeatures = new int[selected.length];

			for (int i = 0; i < selected.length; i++) {
				String s = (String) selected[i];
				Integer ii = (Integer) inputToIndexMap.get(s);

				inputFeatures[i] = ii.intValue();
			}
			setSelectedInputs(selected);
			selected = outputList.getSelectedValues();
			int[] outputFeatures = new int[selected.length];
			for (int i = 0; i < selected.length; i++) {
				String s = (String) selected[i];

				Integer ii = (Integer) outputToIndexMap.get(s);
				outputFeatures[i] = ii.intValue();
			}
			setSelectedOutputs(selected);
			et.setInputFeatures(inputFeatures);
			et.setOutputFeatures(outputFeatures);
			table = null;
		}

		/**
		         Not used
		 Make sure all choices are valid.
		         vered - added test that input features and output featurs have no intersection
		 */
		protected boolean checkChoices() {
			if (outputList.getSelectedIndex() == -1) {
				JOptionPane.showMessageDialog(
					this,
					"You must select at least one output",
					"Error",
					JOptionPane.ERROR_MESSAGE);
				return false;
			}
			if (inputList.getSelectedIndex() == -1) {
				JOptionPane.showMessageDialog(
					this,
					"You must select at least one input",
					"Error",
					JOptionPane.ERROR_MESSAGE);
				return false;
			}

			//vered - added this code
			Object[] selected = inputList.getSelectedValues();
			HashMap ins = new HashMap();
			for (int i = 0; i < selected.length; i++)
				ins.put(selected[i], new Integer(i));
			selected = outputList.getSelectedValues();
			for (int i = 0; i < selected.length; i++)
				if (ins.containsKey(selected[i])) {
					JOptionPane.showMessageDialog(
						this,
						"You cannot select an attribute both as an input and an output",
						"Error",
						JOptionPane.ERROR_MESSAGE);
					return false;
				} //if contains

			return true;
		}
	}
}

//QA Comments Anca - added getPropertyDescription
//QA Comments Ruth - used Example Table in what user reads (w/ space)

/**
* 11-05-03 Vered started Qa process
*          Module handles all cases of selection (no selection, only inputs, only
*          outputs, etc.) very well in both methods of execution (headless &
*          GUI).
*          the Headless doit method is right now case sensitive - and throws
*          an exception for any mismatch. debating on this issue - when the sensetivity
*          level is decided - the module will be ready.
*          headless module is non-case sensitive.
* 12-05-03 module is ready for basic 4
*/