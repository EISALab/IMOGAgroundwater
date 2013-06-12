package ncsa.d2k.modules.core.transform.binning;

import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.gui.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.vis.widgets.*;
import ncsa.d2k.userviews.swing.*;
import ncsa.gui.*;
import ncsa.d2k.modules.core.datatype.*;
import ncsa.d2k.modules.core.datatype.table.transformations.*;

/**
 *
 */
public class ADTBinColumns extends HeadlessUIModule {
	private static final String EMPTY = "",
		COLON = " : ",
		COMMA = ",",
		DOTS = "...",
		OPEN_PAREN = "(",
		CLOSE_PAREN = ")",
		OPEN_BRACKET = "[",
		CLOSE_BRACKET = "]";

	private NumberFormat nf;

	/**
	 * put your documentation comment here
	 * @return
	 */
	public String getModuleName() {
		return "AD Tree Bin Columns";
	}

	/**
	 * put your documentation comment here
	 * @return
	 */
	public String getModuleInfo() {
		return "<html>  <head>      </head>  <body>"
			+ "<P><b>Overview:</B> This module allows a user to interactively bin data using counts stored in an ADTree.</P>"
			+ "<P><B>Detailed Description:</B> Numeric data cannot be binned."
			+ " The user may bin only nominal data.</P>"
			+ "<P>For further information on how to use this module the user may click on the \"Help\" button during run time and get detailed description of each functionality.</P>"
			+ "<P><B>Data Handling:</b><BR> This module does not change its input. Rather than that it outputs a Transformation that can be then applied to the data.</P>"
			+ "</body></html>";
	}

	/**
	 * put your documentation comment here
	 * @return
	 */
	public String[] getInputTypes() {
		String[] types =
			{
				"ncsa.d2k.modules.core.datatype.ADTree",
				"ncsa.d2k.modules.core.datatype.table.ExampleTable" };
		return types;
	}

	/**
	 * put your documentation comment here
	 * @return
	 */
	public String[] getOutputTypes() {
		String[] types =
			{ "ncsa.d2k.modules.core.datatype.table.transformations.BinTransform" };

		return types;
	}

	/**
	 * put your documentation comment here
	 * @param i
	 * @return
	 */
	public String getInputInfo(int i) {
		switch (i) {
			case 0 :
				return "An ADTree containing counts.";
			case 1 :
				return "MetaData ExampleTable containing the names of the input/output features.";
			default :
				return "No such input";
		}
	}

	/**
	 * put your documentation comment here
	 * @param i
	 * @return
	 */
	public String getOutputName(int i) {
		switch (i) {
			case 0 :
				return "Binning Transformation";
			case 1 :
				return "Meta Data Example Table";
			default :
				return "no such output!";
		}
	}

	/**
	 * put your documentation comment here
	 * @param i
	 * @return
	 */
	public String getInputName(int i) {
		switch (i) {
			case 0 :
				return "AD Tree";
			case 1 :
				return "Meta Data Example Table";
			default :
				return "no such input";
		}

	}

	/**
	 * put your documentation comment here
	 * @param i
	 * @return
	 */
	public String getOutputInfo(int i) {
		switch (i) {
			case 0 :
				return "A BinTransform, as defined by the user, that can be applied to the input Table.";
			default :
				return "No such output";
		}
	}

	/**
	 * put your documentation comment here
	 * @return
	 */
	protected UserView createUserView() {
		return new ADTBinColumnsView();
	}

	/**
	 * put your documentation comment here
	 * @return
	 */
	public String[] getFieldNameMapping() {
		return null;
	}

	private class ADTBinColumnsView extends JUserPane {
		private boolean setup_complete;
		private BinDescriptor currentSelectedBin;
		private HashMap columnLookup;
		private TreeSet[] uniqueColumnValues;
		private JList /*numericColumnLabels*/
		textualColumnLabels, currentBins;
		private DefaultListModel binListModel;
		private ExampleTable tbl;
		private int numArrived = 0;
		private ADTree adt;

		/* numeric text fields */
		private JTextField uRangeField,
			specRangeField,
			intervalField,
			weightField;

		/* textual lists */
		private JList textUniqueVals, textCurrentGroup;
		private DefaultListModel textUniqueModel, textCurrentModel;

		/* textual text field */
		private JTextField textBinName;

		/* current selection fields */
		private JTextField curSelName;
		private JList currentSelectionItems;
		private DefaultListModel currentSelectionModel;
		private JButton abort, done;
		private JCheckBox createInNewColumn;

		int uniqueTextualIndex = 0;

		/**
		 * put your documentation comment here
		 */
		private ADTBinColumnsView() {
			setup_complete = false;
			nf = NumberFormat.getInstance();
			nf.setMaximumFractionDigits(3);
		}

		/**
		 * put your documentation comment here
		 * @param o
		 * @param id
		 */
		public void setInput(Object o, int id) {
			if (id == 0) {
				adt = (ADTree) o;
				numArrived = 1;
			}
			if (id == 1) {
				tbl = (ExampleTable) o;
				numArrived++;
			}

			if (numArrived == 2) {
				// clear all text fields and lists...
				curSelName.setText(EMPTY);
				textBinName.setText(EMPTY);
				//  uRangeField.setText(EMPTY);
				//  specRangeField.setText(EMPTY);
				//  intervalField.setText(EMPTY);
				//  weightField.setText(EMPTY);
				columnLookup = new HashMap();


				uniqueColumnValues = new TreeSet[tbl.getNumColumns() + 1];
				binListModel.removeAllElements();
				//  DefaultListModel numModel =
				//   (DefaultListModel) numericColumnLabels.getModel(),
				DefaultListModel txtModel =
					(DefaultListModel) textualColumnLabels.getModel();
				//numModel.removeAllElements();
				txtModel.removeAllElements();

				textCurrentModel.removeAllElements();
				textUniqueModel.removeAllElements();
				uniqueTextualIndex = 0;

				//ANCA moved fix below from BinAttributes to ADTBinAttributes
                 //	!: check inputs/outputs if example table
					 ExampleTable et = null;
					 HashMap etInputs = null;
					 HashMap etOutputs = null;
					 if (tbl instanceof ExampleTable) {
						et = (ExampleTable)tbl;
						int[] inputs = et.getInputFeatures();
						int[] outputs = et.getOutputFeatures();
						etInputs = new HashMap();
						etOutputs = new HashMap();

						for (int i = 0; i < inputs.length; i++) {
						   etInputs.put(new Integer(inputs[i]), null);
						}
						for (int i = 0; i < outputs.length; i++) {
						   etOutputs.put(new Integer(outputs[i]), null);
						}
					 }

					 for (int i = 0; i < tbl.getNumColumns(); i++) {

						if (et != null) {
						   if (!etInputs.containsKey(new Integer(i)) &&
							   !etOutputs.containsKey(new Integer(i))) {
							  continue;
						   }
						}

						columnLookup.put(tbl.getColumnLabel(i), new Integer(i));
						//if (table.getColumn(i) instanceof NumericColumn)
						if (!tbl.isColumnScalar(i)) {
						int idx =
							adt.getIndexForLabel(tbl.getColumnLabel(i));
						columnLookup.put(
							tbl.getColumnLabel(i),
							new Integer(idx));
						   txtModel.addElement(tbl.getColumnLabel(i));
						   uniqueColumnValues[idx] = uniqueValues(idx);
						}

					 }

				if (!validateBins(binListModel)) {
						   binListModel.removeAllElements();
						}

				// finished...
				setup_complete = true;
			}
		}

		/**
		 * Create all of the components and add them to the view.
		 */
		public void initView(ViewModule m) {
			currentBins = new JList();
			binListModel = new DefaultListModel();
			currentBins.setModel(binListModel);
			currentBins.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			currentBins.addListSelectionListener(new CurrentListener());
			// set up the numeric tab

			//vered: removing the numeric tab, since this module does not support
			//binning of numeric data.

			// textual bins
			JPanel txtpnl = new JPanel();
			txtpnl.setLayout(new GridBagLayout());
			textualColumnLabels = new JList();
			textualColumnLabels.setSelectionMode(
				ListSelectionModel.SINGLE_SELECTION);
			textualColumnLabels.addListSelectionListener(new TextualListener());
			textualColumnLabels.setModel(new DefaultListModel());
			textUniqueVals = new JList();
			textUniqueModel = new DefaultListModel();
			textUniqueVals.setModel(textUniqueModel);
			textUniqueVals.setFixedCellWidth(100);
			textCurrentGroup = new JList();
			textCurrentGroup.setFixedCellWidth(100);
			textCurrentModel = new DefaultListModel();
			textCurrentGroup.setModel(textCurrentModel);
			JButton addTextToGroup = new JButton(">");
			addTextToGroup.addActionListener(new AbstractAction() {

				/**
				 * put your documentation comment here
				 * @param e
				 */
				public void actionPerformed(ActionEvent e) {
					if (!setup_complete)
						return;
					Object[] sel = textUniqueVals.getSelectedValues();
					for (int i = 0; i < sel.length; i++) {
						// textUniqueModel.removeElement(sel[i]);
						if (!textCurrentModel.contains(sel[i]))
							textCurrentModel.addElement(sel[i]);
					}
				}
			});
			JButton removeTextFromGroup = new JButton("<");
			removeTextFromGroup.addActionListener(new AbstractAction() {

				/**
				 * put your documentation comment here
				 * @param e
				 */
				public void actionPerformed(ActionEvent e) {
					if (!setup_complete)
						return;
					Object[] sel = textCurrentGroup.getSelectedValues();
					for (int i = 0; i < sel.length; i++) {
						textCurrentModel.removeElement(sel[i]);
						// textUniqueModel.addElement(sel[i]);
					}
				}
			});
			JTabbedPane jtp = new JTabbedPane(JTabbedPane.TOP);

			//       jtp.add(numpnl, "Scalar");
			jtp.add(txtpnl, "Nominal");
			Box bx = new Box(BoxLayout.Y_AXIS);
			bx.add(Box.createGlue());
			bx.add(addTextToGroup);
			bx.add(removeTextFromGroup);
			bx.add(Box.createGlue());
			Box bx1 = new Box(BoxLayout.X_AXIS);
			JScrollPane jp1 = new JScrollPane(textUniqueVals);
			jp1.setColumnHeaderView(new JLabel("Unique Values"));
			bx1.add(jp1);
			bx1.add(Box.createGlue());
			bx1.add(bx);
			bx1.add(Box.createGlue());
			JScrollPane jp2 = new JScrollPane(textCurrentGroup);
			jp2.setColumnHeaderView(new JLabel("Current Group"));
			bx1.add(jp2);
			textBinName = new JTextField(10);
			JButton addTextBin = new JButton("Add");
			addTextBin.addActionListener(new AbstractAction() {

				/**
				 * put your documentation comment here
				 * @param e
				 */
				public void actionPerformed(ActionEvent e) {
					Object[] sel = textCurrentModel.toArray();

					if (sel.length == 0) {
						ErrorDialog.showDialog(
							"You must select some nominal values to group.",
							"Error");
						return;
					}

					Object val = textualColumnLabels.getSelectedValue();

					//int idx = ((Integer)columnLookup.get(val)).intValue();
					int idx = adt.getIndexForLabel((String) val);

					String textualBinName;

					if (textBinName.getText().length() == 0)
						textualBinName = "bin" + uniqueTextualIndex++;
					else
						textualBinName = textBinName.getText();

                    if (!checkDuplicateBinNames(textualBinName)) {
                      ErrorDialog.showDialog("The bin name must be unique, "+textualBinName+" already used.", "Error");
                      return;
                    }

					BinDescriptor bd =
						createTextualBin(idx, textualBinName, sel);

					TreeSet set = uniqueColumnValues[idx];
					for (int i = 0; i < sel.length; i++) {
                  textUniqueModel.removeElement(sel[i]);
						textCurrentModel.removeElement(sel[i]);
						set.remove(sel[i]);
					}
					addItemToBinList(bd);
					textBinName.setText(EMPTY);
				}
			});
			JOutlinePanel jop = new JOutlinePanel("Group");
			JPanel pp = new JPanel();
			pp.add(new JLabel("Name"));
			pp.add(textBinName);
			pp.add(addTextBin);
			jop.setLayout(new BoxLayout(jop, BoxLayout.Y_AXIS));
			jop.add(bx1);
			jop.add(pp);
			JScrollPane jp3 = new JScrollPane(textualColumnLabels);
			Constrain.setConstraints(
				txtpnl,
				jp3,
				0,
				0,
				4,
				1,
				GridBagConstraints.BOTH,
				GridBagConstraints.WEST,
				1,
				1);
			Constrain.setConstraints(
				txtpnl,
				jop,
				0,
				1,
				4,
				1,
				GridBagConstraints.HORIZONTAL,
				GridBagConstraints.WEST,
				1,
				1);
			// now add everything
			JPanel pq = new JPanel();
			pq.setLayout(new BorderLayout());
			JScrollPane jp4 = new JScrollPane(currentBins);
			jp4.setColumnHeaderView(new JLabel("Current Bins"));
			pq.add(jp4, BorderLayout.CENTER);
			JOutlinePanel jop5 = new JOutlinePanel("Current Selection");
			currentSelectionItems = new JList();
			currentSelectionItems.setVisibleRowCount(4);
			currentSelectionItems.setEnabled(false);
			currentSelectionModel = new DefaultListModel();
			currentSelectionItems.setModel(currentSelectionModel);
			JPanel pt = new JPanel();
			curSelName = new JTextField(10);
			pt.add(new JLabel("Name"));
			pt.add(curSelName);
			JButton updateCurrent = new JButton("Update");
			updateCurrent.addActionListener(new AbstractAction() {

				/**
				 * put your documentation comment here
				 * @param e
				 */
				public void actionPerformed(ActionEvent e) {
					if (!setup_complete)
						return;
					if (currentSelectedBin != null) {
						currentSelectedBin.name = curSelName.getText();
						currentBins.repaint();
					}
				}
			});
			JButton removeBin = new JButton("Remove Bin");
			removeBin.addActionListener(new AbstractAction() {

				/**
				 * put your documentation comment here
				 * @param e
				 */
				public void actionPerformed(ActionEvent e) {
					if (!setup_complete)
						return;
					if (currentSelectedBin != null) {
						int col = currentSelectedBin.column_number;
						if (currentSelectedBin instanceof TextualBinDescriptor)
							uniqueColumnValues[col].addAll(
								(
									(
										TextualBinDescriptor) currentSelectedBin)
											.vals);
						binListModel.removeElement(currentSelectedBin);
						currentSelectionModel.removeAllElements();
						curSelName.setText(EMPTY);
						// update the group
						Object lbl = textualColumnLabels.getSelectedValue();
						// gpape:
						if (lbl != null) {
							//int idx = ((Integer)columnLookup.get(lbl)).intValue();
							int idx = adt.getIndexForLabel((String) lbl);
							TreeSet unique = uniqueColumnValues[idx];
							textUniqueModel.removeAllElements();
							textCurrentModel.removeAllElements();
							Iterator i = unique.iterator();
							while (i.hasNext())
								textUniqueModel.addElement(i.next());
						}
					}
				}
			});
			// gpape:
			JButton removeAllBins = new JButton("Remove All");
			removeAllBins.addActionListener(new AbstractAction() {

				/**
				 * put your documentation comment here
				 * @param e
				 */
				public void actionPerformed(ActionEvent e) {

               if (!setup_complete)
                  return;

               while (binListModel.getSize() > 0) {

                  BinDescriptor bd = (BinDescriptor)binListModel.firstElement();

                  if (bd instanceof TextualBinDescriptor) {
                     uniqueColumnValues[bd.column_number].addAll(((TextualBinDescriptor)bd).vals);
                  }
                  binListModel.remove(0);

               }

               // binListModel.removeAllElements();
               currentSelectionModel.removeAllElements();
               curSelName.setText(EMPTY);

               // update the group
               Object lbl = textualColumnLabels.getSelectedValue();
               // gpape:
               if (lbl != null) {
                  int idx = ((Integer)columnLookup.get(lbl)).intValue();
                  TreeSet unique = uniqueColumnValues[idx];
                  textUniqueModel.removeAllElements();
                  textCurrentModel.removeAllElements();
                  Iterator i = unique.iterator();
                  while (i.hasNext())
                     textUniqueModel.addElement(i.next());
               }

				}
			});
			// gpape:
			createInNewColumn = new JCheckBox("Create in new column", false);
			Box pg = new Box(BoxLayout.X_AXIS);
			pg.add(updateCurrent);
			//pg.add(removeItems);
			pg.add(removeBin);
			pg.add(removeAllBins);
			// gpape:
			Box pg2 = new Box(BoxLayout.X_AXIS);
			pg2.add(createInNewColumn);
			jop5.setLayout(new BoxLayout(jop5, BoxLayout.Y_AXIS));
			jop5.add(pt);
			JScrollPane pane = new JScrollPane(currentSelectionItems);
			pane.setColumnHeaderView(new JLabel("Items"));
			jop5.add(pane);
			jop5.add(pg);
			jop5.add(pg2);
			JPanel bgpnl = new JPanel();
			bgpnl.setLayout(new BorderLayout());
			bgpnl.add(jp4, BorderLayout.CENTER);
			bgpnl.add(jop5, BorderLayout.SOUTH);
			// finally add everything to this
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			Box bxl = new Box(BoxLayout.X_AXIS);
			//bxl.add(jtp); commented out by vered. no need for a tab...
			bxl.add(txtpnl);
			bxl.add(bgpnl);
			JPanel buttonPanel = new JPanel();
			abort = new JButton("Abort");
			abort.addActionListener(new AbstractAction() {

				/**
				 * put your documentation comment here
				 * @param e
				 */
				public void actionPerformed(ActionEvent e) {
					viewCancel();
				}
			});
			done = new JButton("Done");
			done.addActionListener(new AbstractAction() {

				/**
				 * put your documentation comment here
				 * @param e
				 */
				public void actionPerformed(ActionEvent e) {
					//binIt(createInNewColumn.isSelected());
					Object[] tmp = binListModel.toArray();
					BinDescriptor[] bins = new BinDescriptor[tmp.length];

					for (int i = 0; i < bins.length; i++)
						bins[i] = (BinDescriptor) tmp[i];

					//ANCA add "unknown" bins for missing values
					//	bins = BinningUtils.addMissingValueBins(tbl, bins);


					//headless conversion support
					setBinDes(bins);
					BinTransform bt =
						new BinTransform(tbl, bins, createInNewColumn.isSelected());

					pushOutput(bt, 0);
					//pushOutput(tbl, 1);
					viewDone("Done");
				}
			});
			JButton showTable = new JButton("Show Table");
			showTable.addActionListener(new AbstractAction() {

				/**
				 * put your documentation comment here
				 * @param e
				 */
				public void actionPerformed(ActionEvent e) {
					JD2KFrame frame = new JD2KFrame("Table");
					frame.getContentPane().add(new TableMatrix(tbl));
					frame.addWindowListener(new DisposeOnCloseListener(frame));
					frame.pack();
					frame.setVisible(true);
				}
			});
			JButton helpButton = new JButton("Help");
			helpButton.addActionListener(new AbstractAction() {

				/**
				 * put your documentation comment here
				 * @param e
				 */
				public void actionPerformed(ActionEvent e) {
					HelpWindow help = new HelpWindow();
					help.setVisible(true);
				}
			});
			buttonPanel.add(abort);
			buttonPanel.add(done);
			buttonPanel.add(showTable);
			buttonPanel.add(helpButton);
			setLayout(new BorderLayout());
			add(bxl, BorderLayout.CENTER);
			add(buttonPanel, BorderLayout.SOUTH);
		}

		private TreeSet uniqueValues(int col) {
			// return the number of unique items in this column
			TreeSet set = adt.getUniqueValuesTreeSet(col);
			return set;
		}

        private boolean checkDuplicateBinNames(String newName) {
           for (int bdi = 0; bdi < binListModel.getSize(); bdi++) {
              BinDescriptor bd = (BinDescriptor)binListModel.elementAt(bdi);
              if (newName.equals(bd.name))
                  return false;
           }
           return true;
        }
		private boolean validateBins(DefaultListModel newBins) {
			   boolean match = false;
			   for (int binIdx = 0; binIdx < newBins.size(); binIdx++) {
				  if (!(columnLookup.containsKey(((BinDescriptor)newBins.get(binIdx)).label))) {
					 // ErrorDialog.showDialog("Current bins contain non-selected attributes. Please remove them.", "Error");
					 // System.out.println("no good: " + ((BinDescriptor)newBins.get(binIdx)).label);
					 return false;
				  }
			   }
			   return true;
			}

		/**
		 * put your documentation comment here
		 * @param idx
		 * @param name
		 * @param sel
		 * @return
		 */
		private BinDescriptor createTextualBin(
			int idx,
			String name,
			Object[] sel) {
			String[] vals = new String[sel.length];
			for (int i = 0; i < vals.length; i++)
				vals[i] = sel[i].toString();
			return new TextualBinDescriptor(idx, name, vals, adt.getLabel(idx));
		}

		/**
		 * Create a numeric bin that goes from min to max.
		 */
		private BinDescriptor createNumericBinDescriptor(
			int col,
			double min,
			double max) {
			StringBuffer nameBuffer = new StringBuffer();
			nameBuffer.append(OPEN_PAREN);
			nameBuffer.append(nf.format(min));
			nameBuffer.append(COLON);
			nameBuffer.append(nf.format(max));
			nameBuffer.append(CLOSE_BRACKET);
			BinDescriptor nb =
				new NumericBinDescriptor(
					col,
					nameBuffer.toString(),
					min,
					max,
					tbl.getColumnLabel(col));
			return nb;
		}

		/**
		 * Create a numeric bin that goes from Double.MIN_VALUE to max
		 */
		private BinDescriptor createMinNumericBinDescriptor(
			int col,
			double max) {
			StringBuffer nameBuffer = new StringBuffer();
			nameBuffer.append(OPEN_BRACKET);
			nameBuffer.append(DOTS);
			nameBuffer.append(COLON);
			nameBuffer.append(nf.format(max));
			nameBuffer.append(CLOSE_BRACKET);
			BinDescriptor nb =
				new NumericBinDescriptor(
					col,
					nameBuffer.toString(),
					Double.MIN_VALUE,
					max,
					tbl.getColumnLabel(col));
			return nb;
		}

		/**
		 * Create a numeric bin that goes from min to Double.MAX_VALUE
		 */
		private BinDescriptor createMaxNumericBinDescriptor(
			int col,
			double min) {
			StringBuffer nameBuffer = new StringBuffer();
			nameBuffer.append(OPEN_PAREN);
			nameBuffer.append(nf.format(min));
			nameBuffer.append(COLON);
			nameBuffer.append(DOTS);
			nameBuffer.append(CLOSE_BRACKET);
			BinDescriptor nb =
				new NumericBinDescriptor(
					col,
					nameBuffer.toString(),
					min,
					Double.MAX_VALUE,
					tbl.getColumnLabel(col));
			return nb;
		}

		/**
		 * put your documentation comment here
		 * @param bd
		 */
		private void addItemToBinList(BinDescriptor bd) {
			binListModel.addElement(bd);
		}

		private class CurrentListener implements ListSelectionListener {

			/**
			 * put your documentation comment here
			 * @param e
			 */
			public void valueChanged(ListSelectionEvent e) {
				if (!setup_complete)
					return;
				if (!e.getValueIsAdjusting()) {
					currentSelectedBin =
						(BinDescriptor) currentBins.getSelectedValue();
					if (currentSelectedBin == null) {
						currentSelectionModel.removeAllElements();
						curSelName.setText(EMPTY);
						return;
					}
					curSelName.setText(currentSelectedBin.name);
					if (currentSelectedBin instanceof NumericBinDescriptor) {
						currentSelectionModel.removeAllElements();
						currentSelectionModel.addElement(
							currentSelectedBin.name);
					} else {
						currentSelectionModel.removeAllElements();
						HashSet hs =
							(HashSet)
								(
									(
										TextualBinDescriptor) currentSelectedBin)
											.vals;
						Iterator i = hs.iterator();
						while (i.hasNext())
							currentSelectionModel.addElement(i.next());
					}
				}
			}
		} // ADTBinColumnsView$CurrentListener

		private class TextualListener implements ListSelectionListener {

			/**
			 * put your documentation comment here
			 * @param e
			 */
			public void valueChanged(ListSelectionEvent e) {
				if (!setup_complete)
					return;
				if (!e.getValueIsAdjusting()) {
					Object lbl = textualColumnLabels.getSelectedValue();
					if (lbl != null) {
						//int idx = ((Integer)columnLookup.get(lbl)).intValue();
						int idx = adt.getIndexForLabel((String) lbl);
						//System.out.println("index " + idx + " label " + (String)lbl);
						TreeSet unique = uniqueColumnValues[idx];
						textUniqueModel.removeAllElements();
						textCurrentModel.removeAllElements();
						Iterator i = unique.iterator();
						while (i.hasNext())
							textUniqueModel.addElement(i.next());
					}
				}
			}
		} // ADTBinColumnsView$TextualListener

		private class HelpWindow extends JD2KFrame {

			/**
			 * put your documentation comment here
			 */
			public HelpWindow() {
				super("About ADT Bin Columns");
				JEditorPane ep = new JEditorPane("text/html", getHelpString());
				ep.setCaretPosition(0);
				getContentPane().add(new JScrollPane(ep));
				setSize(400, 400);
			}
		}

		/**
		 * put your documentation comment here
		 * @return
		 */
		private String getHelpString() {
			StringBuffer sb = new StringBuffer();
			sb.append("<html><body><h2>Bin Columns</h2>");
			sb.append(
				"This module allows a user to interactively bin nominal data from a table. ");

			sb.append("Click on a nominal column to show a list ");
			sb.append(
				"of unique nominal values in that column in the \"Unique Values\" area below. ");
			sb.append(
				"Select one or more of these values and click the right arrow button to group ");
			sb.append(
				"these values. They can then be assigned a collective name, by entering a string in "
					+ "the name text field on the left.");
			sb.append(
				"<br><br>To assign a name to a particular bin, select that bin in ");
			sb.append(
				"the \"Current Bins\" selection area (top right), enter a name in ");
			sb.append(
				"the \"Name\" field below, and click \"Update\". To bin the data and ");
			sb.append("output the new table, click \"Done\".");
			sb.append("</body></html>");
			return sb.toString();
		}
	} // ADTBinColumnsView

	//headless conversion support
	private BinDescriptor[] binDes;
	public void setBinDes(Object[] bins) {
		binDes = (BinDescriptor[]) bins;
	}
	public Object[] getBinDes() {
		return binDes;
	}
	private boolean newColumn;
	public void setNewColumn(boolean val) {
		newColumn = val;
	}
	public boolean getNewColumn() {
		return newColumn;
	}

	public PropertyDescription[] getPropertiesDescriptions() {
		PropertyDescription[] pds = new PropertyDescription[2];
		pds[0] = super.supressDescription;
		pds[1] =
			new PropertyDescription(
				"newColumn",
				"Create In New Column",
				"Set this property to true if you wish the binned columns to be created in new columns. "
					+ "It will be used only when 'Supress User Interface Display' is set to true.");
		return pds;
	}

	public void doit() throws Exception {
		//the tree is not necessary for validating relevancy of bins to the table.
		/*ADTree tree = (ADTree) */
		pullInput(0);

		ExampleTable table = (ExampleTable) pullInput(1);

		//BinningUtils.validateBins(table, binDes, getAlias());

		pushOutput(new BinTransform(table, binDes, newColumn), 0);

	} //doit
	//headless conversion support

} //ADTBinColumns

class ADTBinCounts implements BinCounts {

	private Table table;
	double[][] minMaxes;

	private static final int MIN = 0;
	private static final int MAX = 1;

	public ADTBinCounts() {

	}

	public int getNumRows() {
		return -1;
	}

	public double getMin(int col) {
		return -1;
	}

	public double getMax(int col) {
		return -1;
	}

	public double getTotal(int col) {
		return -1;
	}

	public int[] getCounts(int col, double[] borders) {
		return null;
	}

}

/**
 * QA comments:
 * 2-27-03 vered started qa. added module description, exception handling.
 * 2-27-03 commit back to core and back to greg - to review bin nominal columns tab.
 *
 *
 * 11-19-03: Vered started qa process
 *           the GUI allows overlapping binning - bug 133. [fixed 12-10-03]
 *           other problem with GUI, also reported in this bug - unique values
 *           are not removed from the list once they are associated with a bin.
 *
 *           missing values are considered as '?', and therefore are listed in the
 *           unique values list. [bug 134] the bug is in support class ADTree. (fixed)
 *
 * 11-21-03 The modules treats Table and ExampleTable the same. it ignores the
 *          selection of input/output features. [bug 136]
 */

/* 12-02-03 Anca - fixed [bug 136] - moved fix from BinAttributes into this file
/* 12-04-03 Anca added "unknown" bins for missing values - fixed [bug 134]
 *    			the fix did involve the support class ADTree whose uniqueValues
 * 			method does not return missing values ('?' or the one returned by getMissinsString())
 * 			in the list of unique values
  * 12-08-03 list of unique values is not restored after removing all bins. [bug 159] (fixed)
  * 12 -16-03 Anca moved creation of "unknown" bins to BinTransform
 */

/**
 * 1-05-04:
  * vered - module is ready fro basic.
  *
  * 01-08-04: vered.
  * due to bug 207 (an option to create bins with identical names in same attribute) the
  * module is pulled back to qa process.
  *
  * waiting for an answer form Anca regarding the type of input port indexed 1
  * (ExampleTable vs Table) the module can work with regular Table that was generated
  * by ParseFileToTable but cannot work with regular metadata Table that was
  * created by CreateADTree.
  *
  * 01-13 04 Anca:
  * changed input 1 from Table to ExampleTable to be consistent with the other binning modules
  * Input 1 can be table if the results of the binning are used in a transformation ( applyTransformation)
  * but for use in creating a BinTree an ExampleTable is needed.
  *
  * 01-21-04: vered:
  * bug 207 is fixed.
  *
 * bug 228: binning and representation of missing values. missing values are binned
 * into the "UNKNOWN" bin but are still marked as missing if binning is done
 * in the same column. (fixed)
  *
*
* bug 229 - when checking the "create in a new column" box, the module creates
 the new binned columns with identical labels as the original ones have.
  (fixed)

  bug 235 - module does not allow creation of bins with identical names even if
  bins belong to different attributes.


  *
*/