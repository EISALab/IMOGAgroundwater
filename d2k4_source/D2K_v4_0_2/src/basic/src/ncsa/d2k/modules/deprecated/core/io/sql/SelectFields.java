package ncsa.d2k.modules.deprecated.core.io.sql;

import ncsa.d2k.core.modules.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import ncsa.d2k.userviews.swing.*;

/**
	SelectFields.java
*/
public class SelectFields extends ncsa.d2k.core.modules.UIModule {

  JOptionPane msgBoard = new JOptionPane();

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "Select Fields";
	}

	/**
		This method returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo () {
          String s = "<p>Overview: ";
          s += "This module allows the user to select one or more attributes (fields) from a list of attributes. </p>";
          s += "<p>Detailed Description: ";
          s += "This module provides a user-interface that allows one or more attributes to be chosen ";
          s += "from a list of attributes. The list of attributes is ";
          s += "retrieved from the <i>Attributes List</i> input port. ";
          s += "The selected attributes will be used to construct SQL queries.  </p>";
          s += "<p>Restrictions: ";
          s += "Currently only Oracle, SQLServer, DB2 and MySql databases are supported. ";
          return s;

	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Attributes List";
			default:
				return "No such input";
		}
	}

	/**
		This method returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0:
				return "A list of available attributes.";
			default:
				return "No such input.";
		}
	}

	/**
		This method returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes () {
		String[] types = {"java.util.Vector"};
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
			default:
				return "No such output";
		}
	}
	/**
		This method returns the description of the outputs.
		@return the description of the indexed output
	*/
	public String getOutputInfo (int index) {
		switch (index) {
			case 0:
				return "The attributes that were selected.";
			default:
				return "No such output";
		}
	}

	/**
		This method returns an array of strings that contains the data types for the outputs.
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes () {
		String[] types = {"[Ljava.lang.String;"};
		return types;
	}


    //QA Anca added this:
    public PropertyDescription[] getPropertiesDescriptions() {
        // so that "WindowName" property is invisible
        return new PropertyDescription[0];
    }


	/**
		This method is called by D2K to get the UserView for this module.
		@return the UserView.
	*/
	protected UserView createUserView() {
		return new SelectFieldsView();
	}


	/**
		This method returns an array with the names of each DSComponent in the UserView
		that has a value.  These DSComponents are then used as the outputs of this module.
	*/
	public String[] getFieldNameMapping() {
		//String[] fieldMap2 = {"fields"};
		//return fieldMap2;
		return null;
	}

	/**
		SelectFieldsView
		This is the UserView class.
	*/
	private class SelectFieldsView extends JUserPane {

		JList selectedFields = new JList ();
		JList possibleFields = new JList ();
		JButton add = new JButton ("Add");
		JButton remove = new JButton ("Remove");
		DefaultListModel possibleModel = new DefaultListModel();
		DefaultListModel selectedModel = new DefaultListModel();

		/**
			This method adds the components to a Panel and then adds the Panel
			to the view.
		*/
		public void initView(ViewModule mod) {
			JPanel canvasArea = new JPanel();
			canvasArea.setLayout (new BorderLayout ());
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
					Object[] sel = possibleFields.getSelectedValues();
					for(int i = 0; i < sel.length; i++) {
						//possibleFields.remove (selection);
						possibleModel.removeElement(sel[i]);
						//selectedFields.add (selection);
						selectedModel.addElement(sel[i]);
					}
				}
			});

			// The listener for the remove button moves stuff from the selected list
			// to the possible list.
			remove.addActionListener (new AbstractAction () {
				public void actionPerformed (ActionEvent e) {
					Object[] sel = selectedFields.getSelectedValues();
					for(int i = 0; i < sel.length; i++) {
						//selectedFields.remove (selection);
						selectedModel.removeElement(sel[i]);
						//possibleFields.add (selection);
						possibleModel.addElement(sel[i]);
					}
				}
			});

			selectedFields.setModel(selectedModel);
			possibleFields.setModel(possibleModel);

			//possibleFields.setFixedCellWidth(100);
			//selectedFields.setFixedCellWidth(100);

			JScrollPane jsp = new JScrollPane(possibleFields);
			jsp.setColumnHeaderView(new JLabel("Possible Attributes"));
			JScrollPane jsp1 = new JScrollPane(selectedFields);
			jsp1.setColumnHeaderView(new JLabel("Selected Attributes"));

			canvasArea.add (b1, BorderLayout.CENTER);
			canvasArea.add (/*possibleFields*/jsp, BorderLayout.WEST);
			canvasArea.add (/*selectedFields*/jsp1, BorderLayout.EAST);
			//selectedFields.setReturnType(DSMultiSelectList.DS_STRING_ARRAY);
			//selectedFields.setKey ("fields");

			JPanel buttonPanel = new JPanel();
			JButton done = new JButton("Done");
			done.addActionListener(new AbstractAction() {
				public void actionPerformed(ActionEvent ae) {
					Object[] values = selectedModel.toArray();
					String[] retVal = new String[values.length];
                                        if (retVal.length==0) {
                                          JOptionPane.showMessageDialog(msgBoard,
                                                    "You must select at least one attribute.", "Error",
                                                    JOptionPane.ERROR_MESSAGE);
                                          System.out.println("No columns are selected");
                                        }
                                        else {
                                          for(int i = 0; i < retVal.length; i++) {
						retVal[i] = (String)values[i];
                                          }
                                          pushOutput(retVal, 0);
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

                        JOptionPane.showMessageDialog(msgBoard,
                          "This module has been deprecated.  The functionality is included in a new module called 'SelectAttributes' in core/io/sql.", "Alert - Module Deprecated",
                          JOptionPane.ERROR_MESSAGE);
                        System.out.println("This module has been deprecated.  The functionality is included in a new module called 'SelectAttributes' core/io/sql.");

			Vector fields = (Vector) o;
			selectedModel.removeAllElements();
			possibleModel.removeAllElements();
			String longest = null;
			int lengthOfLongest = 0;
			for (int i = 0 ; i < fields.size (); i++) {
				String elem = (String)fields.elementAt(i);
				possibleModel.addElement (elem);

				if(elem.length() > lengthOfLongest) {
					longest = elem;
					lengthOfLongest = elem.length();
				}
			}

			// If Possible Attributes or Selected Attributes labels are
                        // longer than any of the fields, we want the width to be
                        // based on those, not on the longest field.  -RA 6/03
			String label = "XXXXXXXX Attributes";
			if ( label.length() > lengthOfLongest) {
				longest = label;
			}

			possibleFields.setPrototypeCellValue(longest);
			selectedFields.setPrototypeCellValue(longest);
		}
	}
}

// QA Comments
// 2/18/03 - Handed off to QA by Dora Cai
// 2/19/03 - Anca started QA process.  Changed input name from Fields to Fields List.
// 2/19/03 - Very clean and well documented. checked into basic.
// 2/28/03 - Dora added code to handle no selection of fields
// 03/03/03 - QA and checked into basic - Anca.
// 6/12/03 - Ruth changed "Fields" to "Attributes" and made sure there's enough space
//           in dialog box for labels.  Also updated info & moved in/out methods together.
// 6/16/03 - Loretta moved this module to deprecated. Created a popup message window to
//           alert users of renamed module "SelectAttributes".
// END QA Comments
