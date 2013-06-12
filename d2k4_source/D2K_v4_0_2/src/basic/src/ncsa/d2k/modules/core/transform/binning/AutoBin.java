package ncsa.d2k.modules.core.transform.binning;

import java.text.*;
import java.beans.PropertyVetoException;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.transformations.*;
import ncsa.d2k.core.modules.*;

//added these imports to support custom properties editor
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import ncsa.gui.*;

/**
 * Automatically discretize scalar data for the Naive Bayesian classification
 * model.  This module requires a ParameterPoint to determine the method of binning
 * to be used.
 */
public class AutoBin extends AutoBinOPT {

	public static final int WEIGHT = 1;
	public static final int UNIFORM = 0;

	private int binMethod;
	public void setBinMethod(int i) throws PropertyVetoException {
		binMethod = i;
		if (binMethod != 0 && binMethod != 1)
			throw new PropertyVetoException(
				"Discretization Method must be 0 or 1",
				null);
	}
	public int getBinMethod() {
		return binMethod;
	}

	private int binWeight = 1;
	public void setBinWeight(int i) throws PropertyVetoException {
		if ( i < 1)
			throw new PropertyVetoException(
				"Number of items per bin must be a positive integer.",
				null);
		binWeight = i;
	}
	public int getBinWeight() {
		return binWeight;
	}

	private int numberOfBins = 2;
	public void setNumberOfBins(int i) throws PropertyVetoException {
		if (i < 2)
			throw new PropertyVetoException(
				"Number of bins must be higher than 1.",
				null);
		numberOfBins = i;
	}
	public int getNumberOfBins() {
		return numberOfBins;
	}

	public String[] getInputTypes() {
		String[] in = { "ncsa.d2k.modules.core.datatype.table.ExampleTable" };
		return in;
	}

	public String getModuleInfo() {
		/* String s = "<p>Overview: Automatically discretize scalar data for the " +
		     "Naive Bayesian classification model." +
		     "<p>Detailed Description: Given a table of Examples, define the bins for each " +
		     "scalar input column.  Nominal input columns will have a bin defined " +
		     "for each unique value in the column." +
		     "<p>Data Handling: When binning scalar columns by the number of bins, " +
		     "the maximum and minimum values of each column must be found.  When " +
		     "binning scalar columns by weight, the data in each individual column " +
		     "is first sorted using a merge sort and then another pass is used to " +
		     "find the bounds of the bins. This module does not modify the input data." +
		     "<p>Scalability: The module requires enough memory to make copies of " +
		     "each of the scalar input columns.";*/
		String s =
			"<p>Overview: Automatically discretize scalar data for the "
				+ "Naive Bayesian classification model."
				+ "<p>Detailed Description: Given a table of Examples, define the bins for each "
				+ "scalar input column.  </P><P>When binning Uniformly, "
				+ "the number of bins is determined by '<i>Number of Bins</i>' property, "
				+ "and the boundaries of the bins are set so that they divide evenly over the range "
				+ "of the binned column.</p><P>"
				+ " When binning by weight, '<i>Number of Items per Bin</I>' sets the size of each bin. "
				+ "The values are then binned so that in each bin there is the same number of items. "
				+ "For more details see description of property '<i>Number of Items per Bin</I>'."
				+ "</P><P>Data Handling: This module does not modify the input data."
				+ "<p>Scalability: The module requires enough memory to make copies of "
				+ "each of the scalar input columns.";

		return s;
	}

	/**
	 * Return a list of the property descriptions.
	 * @return a list of the property descriptions.
	 */
	public PropertyDescription[] getPropertiesDescriptions() {
		PropertyDescription[] pds = new PropertyDescription[3];
		pds[0] =
			new PropertyDescription(
				"binMethod",
				"Discretization Method",
				"The method to use for discretization.  Select 1 to create bins"
					+ " by weight.  This will create bins with an equal number of items in "
					+ "each slot.  Select 0 to do uniform discretization by specifying the number of bins. "
					+ "This will result in equally spaced bins between the minimum and maximum for "
					+ "each scalar column.");
		pds[1] =
			new PropertyDescription(
				"binWeight",
				"Number of Items per Bin",
				"When binning by weight, this is the number of items"
					+ " that will go in each bin.  However, the bins may contain more or fewer values than "
					+ "weight values, depending on how many items equal the bin limits. Typically "
					+ "the last bin will contain less or equal to weight  values and the rest of the "
					+ "bins will contain a number that is  equal or greater to weight  values.");
		pds[2] =
			new PropertyDescription(
				"numberOfBins",
				"Number of Bins",
				"Define the number of bins absolutely. "
					+ "This will give equally spaced bins between "
					+ "the minimum and maximum for each scalar "
					+ "column.");
		return pds;
	}

	public void doit() throws Exception {
		tbl = (ExampleTable) pullInput(0);

		inputs = tbl.getInputFeatures();
		outputs = tbl.getOutputFeatures();
		if ((inputs == null) || (inputs.length == 0))
			throw new Exception(
				getAlias()
					+ ": Please select the input features, they are missing.");

		if (outputs == null || outputs.length == 0)
			throw new Exception(
				getAlias()
					+ ": Please select an output feature, it is missing");

		if (tbl.isColumnScalar(outputs[0]))
			throw new Exception(
				getAlias()
					+ ": Output feature must be nominal. Please transform it.");

		nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(3);

		int type = getBinMethod();

		// if type == 0, specify the number of bins
		// if type == 1, use uniform weight
		//BinTree bt;
		BinDescriptor[] bins;
		if (type == 0) {
			//int number = (int)pp.getValue(ParamSpaceGenerator.NUMBER_OF_BINS);
			int number = getNumberOfBins();
			if (number < 0)
				throw new Exception(
					getAlias() + ": Number of bins not specified!");
			bins = numberOfBins(number);
		} else {
			//      int weight = (int)pp.getValue(ParamSpaceGenerator.ITEMS_PER_BIN);
			int weight = getBinWeight();
			bins = sameWeight(weight);
		}

		BinTransform bt = new BinTransform(tbl, bins, false);



		pushOutput(bt, 0);
	} //doit

	public CustomModuleEditor getPropertyEditor() {
		return new PropEdit();
	}

	private class PropEdit extends JPanel implements CustomModuleEditor {

		JLabel methodLabel = new JLabel("Discretization Method");
		String[] methods = { "Uniform", "Weight" };
		JComboBox methodList;

		JTextField numItemsText;
		JLabel numItemslbl = new JLabel("Number of Items per Bin");

		JTextField numBinsText;
		JLabel numBinslbl = new JLabel("Number of Bins");

		boolean numItemsChange = false;
		boolean numBinsChange = false;
		boolean methodChange = false;

		private PropEdit() {
			// int lr = getLabelsRow();
			int method = getBinMethod();
			methodList = new JComboBox(methods);
			methodList.setSelectedIndex(method);
			//add action listener.

			methodLabel.setToolTipText("The method used for discretization");

			numItemslbl.setToolTipText(
				getPropertiesDescriptions()[1].getDescription());
			numItemsText = new JTextField();

			numBinsText = new JTextField();
			numBinslbl.setToolTipText(
				getPropertiesDescriptions()[2].getDescription());

			numBinsText.setText(Integer.toString(getNumberOfBins()));
			numItemsText.setText(Integer.toString(getBinWeight()));

			numBinsText.setEnabled(getBinMethod() == AutoBin.UNIFORM);
			numItemsText.setEnabled(getBinMethod() == AutoBin.WEIGHT);

			numBinsText.addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
					numBinsChange = true;
				}
			});

			numItemsText.addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
					numItemsChange = true;
				}
			});

			methodList.addActionListener(new AbstractAction() {
				public void actionPerformed(ActionEvent e) {

					int method = methodList.getSelectedIndex();
					try {
						setBinMethod(method);
					} catch (PropertyVetoException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						System.out.println("This is not a valid method!");
					}
					numBinsText.setEnabled(method == AutoBin.UNIFORM);
					numItemsText.setEnabled(method == AutoBin.WEIGHT);

				}
			});

			// add the components for delimited properties
			setLayout(new GridBagLayout());
			/*Constrain.setConstraints(pnl, new JLabel("Delimited Format File Properties"), 0, 0, 2, 1,
			                         GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
			                         1,1, new Insets(2, 2, 15, 2));
			*/
			Constrain.setConstraints(
				this,
				methodLabel,
				0,
				0,
				1,
				1,
				GridBagConstraints.HORIZONTAL,
				GridBagConstraints.EAST,
				1,
				1);
			Constrain.setConstraints(
				this,
				methodList,
				1,
				0,
				1,
				1,
				GridBagConstraints.HORIZONTAL,
				GridBagConstraints.WEST,
				1,
				1);
			Constrain.setConstraints(
				this,
				numBinslbl,
				0,
				1,
				1,
				1,
				GridBagConstraints.HORIZONTAL,
				GridBagConstraints.EAST,
				1,
				1);
			Constrain.setConstraints(
				this,
				numBinsText,
				1,
				1,
				1,
				1,
				GridBagConstraints.HORIZONTAL,
				GridBagConstraints.WEST,
				1,
				1);
			Constrain.setConstraints(
				this,
				numItemslbl,
				0,
				2,
				1,
				1,
				GridBagConstraints.HORIZONTAL,
				GridBagConstraints.EAST,
				1,
				1);
			Constrain.setConstraints(
				this,
				numItemsText,
				1,
				2,
				1,
				1,
				GridBagConstraints.HORIZONTAL,
				GridBagConstraints.WEST,
				1,
				1);
		}

		public boolean updateModule() throws Exception {
			boolean didChange = false;

			if (numItemsChange) {
				try {
					int numItems = Integer.parseInt(numItemsText.getText());
					setBinWeight(numItems);
					didChange = true;
				} catch (NumberFormatException e) {
					throw new Exception("Please indicate an integer number for the number of items per bin.");
				}

			} //if

			if (numBinsChange) {
				try {
					int numBins = Integer.parseInt(numBinsText.getText());
					setNumberOfBins(numBins);
					didChange = true;
				} catch (NumberFormatException e) {
					throw new Exception("Please indicate an integer number for the number bins.");
				}

			} //if

			return didChange;
		} //updateModule
	} //PropEdit

} //AutoBin

//QA Comments Anca:
// added propertyVetoExceptions
// added checks for input/output feature existence and nominal class variable

/**
 * 11-17-03 Vered started qa process.
 * 11-18-03 Missing values are binned as if they were regular values. [bug 131]
 *          re-Edited module info.
 *          added the custom properties editor from wish list.
 * 11-19-03 problem with uniform binning. value x, is binned into bin (x : y]
 *          instead into bin (z : x]. (bug 132)
 *
 */

/** 12-2-03 Anca  -[ bug 131] fixed
 *              Added support for binning missing values, in AutoBinOPT
 */

/* 12 -5-03 anca - fixed [ bug 132] - changes are in BinDescriptorFactory marked with ANCA
 *              the problem is not with uniform binning but with the way NumberFormat displays a double.
*               the margins of the interval for uniform binning are computed
* 				using (max-min)/ number of internals and thus what appears
*               as 3.6 interval margin is in fact 3.5999996. Without NumberFormat nf
* 				the 3.59999996 will be displayed and there will be no confusion.
 * */

/**
 * 12-10-03 vered: creates bins smaller than the weight setting. [bug 165]
 */
/*
 * 12-15-03 anca - fixed [bug 165] at the AutoBinOPT level by eliminating missing values when
 *						 interval margins are defined
*/


/**
 * 01-04-04: vered - module is ready for basic.
*/


/**
 * 01-11-04: Vered
 * Module is pulled back into qa process.
 * bug 216 - array index out of bounds exception when performing weight binning
 * ona subset table (which its subset does not include all of the records of the
 * original table) with missing values. (the exception is thrown by AutoBinOPT)
 *
 * 01-21-04:
 * bug 216 is fixed.
 *
 * bug 228: binning and representation of missing values. missing values are binned
 * into the "UNKNOWN" bin but are still marked as missing if binning is done
 * in the same column. (fixed)
 *
 * 01-29-04: vered
 * ready for basic.

*/
