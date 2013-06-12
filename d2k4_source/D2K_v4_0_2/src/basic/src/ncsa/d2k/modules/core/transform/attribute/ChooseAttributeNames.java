package ncsa.d2k.modules.core.transform.attribute;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.Table;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import ncsa.d2k.userviews.swing.*;

/**
	SelectAttributes.java
*/
public class ChooseAttributeNames
	extends ncsa.d2k.core.modules.HeadlessUIModule {

	JOptionPane msgBoard = new JOptionPane();
	boolean missingOnly = false;
	public boolean getColumnsWithMissingValues() {
		return missingOnly;
	}
	public void setColumnsWithMissingValues(boolean b) {
		missingOnly = b;
	}
	public PropertyDescription[] getPropertiesDescriptions () {
		PropertyDescription [] desc = new PropertyDescription [2];
		desc[0] = this.supressDescription;
		desc[1] = new PropertyDescription("columnsWithMissingValues",
			"Attributes with Missing Values Only",
			"If enabled, allow selection of only those attributes containing missing values.");
		return desc;
	}
	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "Choose Attribute Names";
	}

	/**
		This method returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo() {
		return "<p>"+
"      Overview: This module allows the selection of attributes from a table, "+
"      producing an array of the names of the selected atributes."+
"    </p>"+
"    <p>"+
"      Detailed Description: A user interface is presented to the user "+
"      containing two lists. The list on the left contains the names of all the "+
"      attributes in the table that was input. The list on the right contains a "+
"      list of all the attributes selected from the list on the left. The <i>add</i>"+
" button will move all the attributes select in the list on the left to the "+
"      list on the right. The <i>remove</i> button will move all the selected "+
"      attrributes from the list on the right to the list on the left. When the "+
"      done button is clicked, a string array will be constructed containing "+
"      the names of the items in the right list, and it will be produced as the "+
"      sole output."+
"    </p>";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Table";
			default: return "NO SUCH INPUT!";
		}
	}

	/**
		This method returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "The table from which we will choose attributes.";
			default: return "No such input";
		}
	}

	/**
		This method returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.mmodules.core.datatype.table.Table"};
		return types;
	}

	/**
	 * Return the human readable name of the indexed output.
	 * @param index the index of the output.
	 * @return the human readable name of the indexed output.
	 */
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Selected Attributes";
			default: return "NO SUCH OUTPUT!";
		}
	}
	/**
		This method returns the description of the outputs.
		@return the description of the indexed output
	*/
	public String getOutputInfo(int index) {
		switch (index) {
			case 0: return "The attributes that were selected.";
			default: return "No such output";
		}
	}

	/**
		This method returns an array of strings that contains the data types for the outputs.
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes() {
		String[] types = {"[Ljava.lang.String;"};
		return types;
	}

	/**
		This method is called by D2K to get the UserView for this module.
		@return the UserView.
	*/
	protected UserView createUserView() {
		return new ChooseAttributeView();
	}

	/**
		This method returns an array with the names of each DSComponent in the UserView
		that has a value.  These DSComponents are then used as the outputs of this module.
	*/
	public String[] getFieldNameMapping() {
		return null;
	}

	/**
		SelectAttributeView
		This is the UserView class.
	*/
	private class ChooseAttributeView extends JUserPane {

		JList selectedAttributes = new JList();
		JList possibleAttributes = new JList();
		JButton add = new JButton("Add");
		JButton remove = new JButton("Remove");
		DefaultListModel possibleModel = new DefaultListModel();
		DefaultListModel selectedModel = new DefaultListModel();

		/**
			This method adds the components to a Panel and then adds the Panel
			to the view.
		*/
		public void initView(ViewModule mod) {
			JPanel canvasArea = new JPanel();
			canvasArea.setLayout(new BorderLayout());
			JPanel buttons = new JPanel();
			buttons.setLayout(new GridLayout(2, 1));
			buttons.add(add);
			buttons.add(remove);

			JPanel b1 = new JPanel();
			b1.add(buttons);

			// The listener for the add button moves stuff from the possible list
			// to the selected list.
			add.setEnabled(false);
			add.addActionListener(new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					Object[] sel = possibleAttributes.getSelectedValues();
					for (int i = 0; i < sel.length; i++) {
						possibleModel.removeElement(sel[i]);
						selectedModel.addElement(sel[i]);
					}
				}
			});

			// The listener for the remove button moves stuff from the selected list
			// to the possible list.
			remove.setEnabled(false);
			remove.addActionListener(new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					Object[] sel = selectedAttributes.getSelectedValues();
					for (int i = 0; i < sel.length; i++) {
						selectedModel.removeElement(sel[i]);
						possibleModel.addElement(sel[i]);
					}
				}
			});

			selectedAttributes.setModel(selectedModel);
			selectedAttributes.addListSelectionListener (new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					// TODO Auto-generated method stub
					if (selectedAttributes.getSelectedIndex() == -1)
						remove.setEnabled(true);
					else
						remove.setEnabled(false);
				}
			});
			possibleAttributes.setModel(possibleModel);
			possibleAttributes.addListSelectionListener (new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					// TODO Auto-generated method stub
					if (selectedAttributes.getSelectedIndex() == -1)
						add.setEnabled(true);
					else
						add.setEnabled(false);
				}
			});
			JScrollPane jsp = new JScrollPane(possibleAttributes);
			jsp.setColumnHeaderView(new JLabel("Possible Attributes"));
			JScrollPane jsp1 = new JScrollPane(selectedAttributes);
			jsp1.setColumnHeaderView(new JLabel("Selected Attributes"));

			canvasArea.add(b1, BorderLayout.CENTER);
			canvasArea.add(jsp, BorderLayout.WEST);
			canvasArea.add(jsp1, BorderLayout.EAST);

			JPanel buttonPanel = new JPanel();
			JButton done = new JButton("Done");
			done.addActionListener(new AbstractAction() {
				public void actionPerformed(ActionEvent ae) {
					Object[] values = selectedModel.toArray();
					String[] retVal = new String[values.length];
					if (retVal.length == 0) {
						JOptionPane.showMessageDialog(
							msgBoard,
							"You must select at least one attribute.",
							"Error",
							JOptionPane.ERROR_MESSAGE);
						System.out.println("No columns are selected");
					} else {
						for (int i = 0; i < retVal.length; i++) {
							retVal[i] = (String) values[i];
						}
						pushOutput(retVal, 0);
						setSelectedAttributes(retVal);
						viewDone("Done");
					}
				}
			});

			JButton abort = new JButton("Abort");
			abort.addActionListener(new AbstractAction() {
				public void actionPerformed(ActionEvent ae) {
					viewCancel();
				}
			});

			buttonPanel.add(abort);
			buttonPanel.add(done);
			canvasArea.add(buttonPanel, BorderLayout.SOUTH);
			add(canvasArea);
		}

		/**
			This method is called whenever an input arrives, and is responsible
			for modifying the contents of any gui components that should reflect
			the value of the input.
		
			@param input this is the object that has been input.
			@param index the index of the input that has been received.
		*/
		public void setInput(Object o, int index) {
			Table table = (Table) o;
			selectedModel.removeAllElements();
			possibleModel.removeAllElements();
			String longest = null;
			int lengthOfLongest = 0;
			int num = table.getNumColumns();
			for (int i = 0; i < num; i++) {
				if (!missingOnly || table.hasMissingValues(i)) {
					String elem = (String) table.getColumnLabel(i);
					possibleModel.addElement(elem);
					if (elem.length() > lengthOfLongest) {
						longest = elem;
						lengthOfLongest = elem.length();
					}
				}
			}

			// If Possible Attributes or Selected Attributes labels are
			// longer than any of the attributes, we want the width to be
			// based on those, not on the longest attribute.  -RA 6/03
			String label = "XXXXXXXX Attributes";
			if (label.length() > lengthOfLongest) {
				longest = label;
			}

			possibleAttributes.setPrototypeCellValue(longest);
			selectedAttributes.setPrototypeCellValue(longest);
		}
	}

	//headless conversion support
	private String[] selectedAttributes;
	public void setSelectedAttributes(Object[] att) {
		selectedAttributes = (String[]) att;
	}
	public Object[] getSelectedAttributes() {
		return selectedAttributes;
	}

	public void doit() throws Exception {

		Table table = (Table) pullInput(0);
		if (table == null || selectedAttributes.length == 0)
			throw new Exception(
				getAlias()
					+ " has not been configured. Before running "
					+ "headless configure the properties via running with GUI.");
		
		// Populate a vector with a list of all column names.
		HashMap availableAttributes = new HashMap();
		for (int i = 0 ; i < table.getNumColumns() ; i++) {
			availableAttributes.put(table.getColumnLabel(i), table);
		}
		
		for (int i = 0 ; i < selectedAttributes.length ; i++) {
			if (availableAttributes.get(selectedAttributes[i]) == null)
				throw new Exception(
					getAlias()
						+ ": No attribute named "+selectedAttributes[i]+" was found "
						+ "found in the input table. "
						+ "Please reconfigure this module so it can run Headless.");
		}
		pushOutput(selectedAttributes, 0);

	} //doit
}
