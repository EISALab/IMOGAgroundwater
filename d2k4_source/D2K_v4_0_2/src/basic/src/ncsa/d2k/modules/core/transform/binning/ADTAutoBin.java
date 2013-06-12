package ncsa.d2k.modules.core.transform.binning;

import java.util.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.transformations.*;

/**
 * Automatically discretize scalar data for the Naive Bayesian classification
 * model.  This module requires a ParameterPoint to determine the method of binning
 * to be used.
 */
public class ADTAutoBin extends DataPrepModule {

	public String[] getInputTypes() {
		String[] in =
			{
				"ncsa.d2k.modules.core.datatype.ADTree",
				"ncsa.d2k.modules.core.datatype.table.ExampleTable" };
		return in;
	}

	/**
	* Get the name of the input parameter
	* @param i is the index of the input parameter
	* @return Name of the input parameter
	*/
	public String getInputName(int i) {
		switch (i) {
			case 0 :
				return "AD Tree";
			case 1 :
				return "Meta Data Example Table";
			default :
				return "No such input";
		}
	}

	/**
	* Get the data types for the output parameters
	* @return A object of class BinTransform
	*/
	public String[] getOutputTypes() {
		String[] types =
			{ "ncsa.d2k.modules.core.datatype.table.transformations.BinTransform" };
		return types;
	}

	/**
	 * Get input information
	 * @param i is the index of the input parameter
	 * @return A description of the input parameter
	 */
	public String getInputInfo(int i) {
		switch (i) {
			case 0 :
				return "The ADTree containing counts";
			case 1 :
				return "MetaData ExampleTable containing the names of the input/output features";
			default :
				return "No such input";
		}
	}

	/**
	 * Get the name of the output parameters
	 * @param i is the index of the output parameter
	 * @return Name of the output parameter
	 */
	public String getOutputName(int i) {
		switch (i) {
			case 0 :
				return "Binning Transformation";
			default :
				return "no such output!";
		}
	}

	/**
	 * Get output information
	 * @param i is the index of the output parameter
	 * @return A description of the output parameter
	 */
	public String getOutputInfo(int i) {
		switch (i) {
			case 0 :
				return "A BinTransform object that contains column_numbers, names and labels";
			default :
				return "No such output";
		}
	}

	public String getModuleName() {
		return " AD Tree Auto Discretization";
	}

	public String getModuleInfo() {
		String s =
                "<p>Overview: Automatically discretize nominal data for the "
                + "Naive Bayesian classification model using ADTrees. "
                + "<p>Detailed Description: Given an ADTree and an Example table containing labels and "
                + "types of the columns, define the bins for each nominal input column, one bin "
                + "for each unique value in the column."
                + "<P>Data Handling: This module does not change its input. "
                +"Rather, it outputs a Transformation that can later be applied to the tree."
                +" The Transformation will be applied only to the input/output features defined by "
                + "the <i>Meta Data Example Table</i>."
                + "<p><u>Note</u>: The output of this modules should be used "
                + "to create a binned tree. Applying the Transformation on a Table "
                + "won't change the Table's content."
                + "<p>Data Type Restrictions: This module does not bin numeric data.";
		return s;
	}


        /**
         * Rather, it outputs a Transformation that can later be applied to the original table data
         */

	ADTree adt;
	ExampleTable tbl;

	public void doit() throws Exception {

	    adt = (ADTree) pullInput(0);
	    tbl = (ExampleTable) pullInput(1);

	    int [] inputs = tbl.getInputFeatures();
	    if (inputs == null || inputs.length == 0)
		throw new Exception("Input features are missing. Please select an input feature.");

	    int [] outputs = tbl.getOutputFeatures();
	    if (outputs == null || outputs.length == 0)
		throw new Exception("Output feature is missing. Please select an output feature.");
	    if(tbl.isColumnScalar(outputs[0]))
		throw new Exception("Output feature must be nominal.");

	    BinDescriptor[] bins = createAutoNominalBins();

	    BinTransform bt = new BinTransform(tbl, bins, false);

	    pushOutput(bt, 0);
		//pushOutput(et, 1);
	}

	protected BinDescriptor[] createAutoNominalBins() throws Exception {

		List bins = new ArrayList();
		int[] inputs = tbl.getInputFeatures();

		for (int i = 0; i < inputs.length; i++) {
			boolean isScalar = tbl.isColumnScalar(i);

			//System.out.println("scalar ? " + i + " " + isScalar);
			if (isScalar) {
			    throw new Exception ("ADTrees do not support scalar values");
			}

			// if it is nominal, create a bin for each unique value.
			// attributes indexes in the ADTree start at 1
			else {
				TreeSet vals = adt.getUniqueValuesTreeSet(inputs[i]+1);
				Iterator iter = vals.iterator();
				int numRows = tbl.getNumRows();

			/*	if (tbl.getColumn(inputs[i]).hasMissingValues()) {
					String[] miss = new String[1];
					     miss[0]= tbl.getMissingString();


					BinDescriptor bd = new TextualBinDescriptor (inputs[i],"Unknown",miss,tbl.getColumnLabel(inputs[i]));
					bins.add(bd);
					//System.out.println("has missing values for column " + inputs[i]);
					while (iter.hasNext()) {
										String st[] = new String[1];
										String item = (String) iter.next();
										st[0] = item;

								if(!item.equals(miss[0])) {

										BinDescriptor bdm =
											new TextualBinDescriptor(
												inputs[i],
												item,
												st,
												tbl.getColumnLabel(inputs[i]));
										bins.add(bdm);}
					}

				}


			else { // there are no missing values in this column
		*/	//System.out.println("no missing values for column " + inputs[i]);
						while (iter.hasNext()) {
								String item = (String) iter.next();
								String[] st = new String[1];
								st[0] = item;
								BinDescriptor bd =
									new TextualBinDescriptor(
										inputs[i],
										item,
										st,
										tbl.getColumnLabel(inputs[i]));
								bins.add(bd);
							}
		//	}
			}
		}
		BinDescriptor[] bn = new BinDescriptor[bins.size()];
		for (int i = 0; i < bins.size(); i++) {
			bn[i] = (BinDescriptor) bins.get(i);

		}
		//ANCA: adding missing values bins for attributes with missing values
	//	bn = BinningUtils.addMissingValueBins(tbl,bn);
		return bn;

	}
}

      /**
      * 11-17-03 Vered started qa process
      *          Missing value handling - this module handles missing values
      *          as if they were real values. since they are to be considered
      *          as meaningless, and not expected to produce a special unique
      *          value bin - then they should be binned into UNKNOWN. [bug 127]
 */

      /*
      * 12-3 -03 Anca
      * missing values are binned into Unknown bin - fixed [bug 127]
      * also added support for missing values in ParseFileToADTree.
      * 12 -16-03 Anca moved creation of "unknown" bins to BinTransform
      **/

     /**
 * 01-04-04 Vered
 * Module is ready for basic.
 *
 * 01-13-04:
 * module is pulled back into qa process.
 *
 * bug 227 - when a column has NO missing values all items are being binned into
 * the UNKNOWN bin. plus missing values are preserved as missing, though seemed
 * to be really binned into unknown bins (as testified by test missing values in bins module)
 * (fixed)
 *
 * 01-15-04:
 * bug 227 is fixed.
 *
 * 01-21-04: vered
 *
 * bug 228: binning and representation of missing values. missing values are binned
 * into the "UNKNOWN" bin but are still marked as missing if binning is done
 * in the same column. (fixed)
 *
 * 01-29-04: vered
 * ready fro basic

*/