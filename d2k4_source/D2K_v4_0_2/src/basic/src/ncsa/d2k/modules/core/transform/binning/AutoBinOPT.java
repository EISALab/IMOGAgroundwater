package ncsa.d2k.modules.core.transform.binning;

import java.text.*;
import java.util.*;

import gnu.trove.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.transformations.*;
import ncsa.d2k.modules.core.datatype.table.util.*;

/**
 * Automatically discretize scalar data for the Naive Bayesian classification
 * model.  This module requires a ParameterPoint to determine the method of binning
 * to be used.
 */
public class AutoBinOPT extends DataPrepModule {

	public String[] getInputTypes() {
		String[] in =
			{
				"ncsa.d2k.modules.core.datatype.table.ExampleTable",
				"ncsa.d2k.modules.core.datatype.parameter.ParameterPoint" };
		return in;
	}

	public String getInputInfo(int i) {
		if (i == 0) {
			return "A table of examples.";
		} else {
			return "A ParameterPoint from a Naive Bayes Parameter Space.";
		}
	}

	public String getInputName(int i) {
		if (i == 0) {
			return "Example Table";
		} else {
			return "Parameter Point";
		}
	}

	public String[] getOutputTypes() {
		String[] out =
			{ "ncsa.d2k.modules.core.datatype.table.transformations.BinTransform" };
		return out;
	}

	public String getOutputInfo(int i) {
		if (i == 0) {
			return "A BinTransform that contains all the information needed to "
				+ "discretize the Example Table";
		} else {
			return "";
		}
	}

	public String getOutputName(int i) {
		if (i == 0) {
			return "Binning Transformation";
		} else {
			return "";
		}
	}

	public String getModuleInfo() {
		String s =
			"<p>Overview: Automatically discretize scalar data for the "
				+ "Naive Bayesian classification model.  This module requires a "
				+ "ParameterPoint to determine the method of binning to be used."
				+ "<p>Detailed Description: Given a Parameter Point from a Naive Bayes "
				+ "Parameter Space and a table of Examples, define the bins for each "
				+ "scalar input column.  Nominal input columns will have a bin defined "
				+ "for each unique value in the column."
				+ "<p>Data Type Restrictions: This module does not modify the input data."
				+ "<p>Data Handling: When binning scalar columns by the number of bins, "
				+ "the maximum and minimum values of each column must be found.  When "
				+ "binning scalar columns by weight, the data in each individual column "
				+ "is first sorted using a merge sort and then another pass is used to "
				+ "find the bounds of the bins."
				+ "<p>Scalability: The module requires enough memory to make copies of "
				+ "each of the scalar input columns.";
		return s;
	}

	public String getModuleName() {
		return "Auto Bin";
	}

	protected ExampleTable tbl;
	protected NumberFormat nf;

	int[] inputs;
	int[] outputs;

	public void doit() throws Exception {


		tbl = (ExampleTable) pullInput(0);
		ParameterPoint pp = (ParameterPoint) pullInput(1);

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

		/*for (int i = 0; i < pp.getNumParameters(); i++) {
		  String name = pp.getName(i);
		  nameToIndexMap.put(name, new Integer(i));
		}

		Integer method = (Integer) nameToIndexMap.get(AutoParamSpaceGenerator.
		                                              BIN_METHOD);

		*/
		int type = (int) pp.getValue(AutoBinParamSpaceGenerator.BIN_METHOD);

		/*if (method == null) {
		  throw new Exception(getAlias() + ":  Could not find Bin Method!");
		}
		int type = method.intValue();
		*/

		// if type == 0, specify the number of bins
		// if type == 1, use uniform weight
		//BinTree bt;
		BinDescriptor[] bins;
		if (type == 0) {
			/*Integer number = (Integer) nameToIndexMap.get(
			    ParamSpaceGenerator.NUMBER_OF_BINS);
			if (number == null) {
			  throw new Exception(getAlias() + ": Number of bins not specified!");
			}
			*/
			int number =
				(int) pp.getValue(AutoBinParamSpaceGenerator.NUMBER_OF_BINS);
			if (number < 0)
				throw new Exception(
					getAlias() + ": Number of bins not specified!");
			bins = numberOfBins(number);
		} else {
			/*Integer weight = (Integer) nameToIndexMap.get(
			    ParamSpaceGenerator.ITEMS_PER_BIN);
			if (weight == null) {
			  throw new Exception(getAlias() + ": Items per bin not specified!");
			}*/
			int weight =
				(int) pp.getValue(AutoBinParamSpaceGenerator.ITEMS_PER_BIN);
			bins = sameWeight(weight);
		}

		BinTransform bt = new BinTransform(tbl, bins, false);

		pushOutput(bt, 0);
	}

	protected BinDescriptor[] numberOfBins(int num) throws Exception {

		List bins = new ArrayList();
		String[] an = new String[inputs.length];
		for (int i = 0; i < inputs.length; i++) {
			an[i] = tbl.getColumnLabel(inputs[i]);
		}
		String[] cn = TableUtilities.uniqueValues(tbl, outputs[0]);
		BinTree bt = new BinTree(cn, an);

		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(3);

		for (int i = 0; i < inputs.length; i++) {
			boolean isScalar = tbl.isColumnScalar(inputs[i]);
			// if it is scalar, find the max and min and create equally-spaced bins
			if (isScalar) {
				// find the maxes and mins
				ScalarStatistics ss =
					TableUtilities.getScalarStatistics(tbl, inputs[i]);
				double max = ss.getMaximum();
				double min = ss.getMinimum();
				double[] binMaxes = new double[num - 1];
				double interval = (max - min) / (double) num;
				binMaxes[0] = min + interval;
				//System.out.println("binmaxes[0] " + binMaxes[0]);

				// add the first bin manually

				BinDescriptor bd =
					BinDescriptorFactory.createMinNumericBinDescriptor(
						inputs[i],
						binMaxes[0],
						nf,
						tbl);
				bins.add(bd);

				// now add the rest
				for (int j = 1; j < binMaxes.length; j++) {
					binMaxes[j] = binMaxes[j - 1] + interval;

					bd =
						BinDescriptorFactory.createNumericBinDescriptor(
							inputs[i],
							binMaxes[j - 1],
							binMaxes[j],
							nf,
							tbl);
					bins.add(bd);
				}

				//System.out.println("binmaxes[length-1] " + binMaxes[binMaxes.length-1]);
				// now add the last bin

				bd =
					BinDescriptorFactory.createMaxNumericBinDescriptor(
						inputs[i],
						binMaxes[binMaxes.length - 1],
						nf,
						tbl);

				bins.add(bd);

				//		ANCA: if there are missing values add an "unknown" bin
			/*	if (tbl.hasMissingValues(inputs[i])) {
					bd =
						BinDescriptorFactory.createMissingValuesBin(
							inputs[i],
							tbl);
					bins.add(bd);
				} */
			}


			else {
//		if it is nominal, create a bin for each unique value.
				String[] vals = TableUtilities.uniqueValues(tbl, inputs[i]);
				for (int j = 0; j < vals.length; j++) {
					String[] st = { vals[j] };
					BinDescriptor bd =
						BinDescriptorFactory.createTextualBin(
							inputs[i],
							vals[j],
							st,
							tbl);
					bins.add(bd);
				}
				//		ANCA: if there are missing values add an "unknown" bin
			/*	if (tbl.hasMissingValues(inputs[i])) {
					BinDescriptor bd =
						BinDescriptorFactory.createMissingValuesBin(
							inputs[i],
							tbl);
					bins.add(bd);
				} */
			}
		}

		BinDescriptor[] bn = new BinDescriptor[bins.size()];
		for (int i = 0; i < bins.size(); i++) {
			bn[i] = (BinDescriptor) bins.get(i);

		}
		return bn;
	}

	protected BinDescriptor[] sameWeight(int weight) throws Exception {
		List bins = new ArrayList();
		String[] an = new String[inputs.length];

		for (int i = 0; i < inputs.length; i++) {
			an[i] = tbl.getColumnLabel(inputs[i]);
		}
		String[] cn = TableUtilities.uniqueValues(tbl, outputs[0]);

		for (int i = 0; i < inputs.length; i++) {

			// if it is scalar, get the data and sort it.  put (num) into each bin,
			// and create a new bin when the last one fills up
			boolean isScalar = tbl.isColumnScalar(inputs[i]);
			if (isScalar) {
				int numRows = tbl.getNumRows();
				int missing =0;
				if (tbl.getColumn(inputs[i]).hasMissingValues()) {
                    // Count missing values using table methods in case it is a subsetted table.
                    for (int ri = 0, ci = inputs[i] ; ri < numRows ; ri++) {
                        if (tbl.isValueMissing(ri, ci))
                            missing++;
                    }
                }
				double[] vals =  new double[tbl.getNumRows()-missing];

				//ANCA added support for eliminating missing values when setting interval limits
				int k=0;
				for (int j = 0; j < numRows; j++) {
					//check if column has missing values
					if (missing >0) {
						//if value is missing do not add it
						if(!tbl.isValueMissing(j,inputs[i]))
					    	vals[k++] = tbl.getDouble(j, inputs[i]);
					}
					else
				    vals[j] = tbl.getDouble(j, inputs[i]);
				}
				Arrays.sort(vals);

				//!!!!!!!!!!!!!!!
				TDoubleArrayList list = new TDoubleArrayList();
				// now find the bin maxes...
				// loop through the sorted data.  the next max will lie at
				// data[curLoc+weight] items

				//vered - changed the following line, from 0 to -1, so the first bin won't
				//have too many items in it.
				int curIdx = -1;
				while (curIdx < vals.length - 1) {
					curIdx += weight;
					if (curIdx > vals.length - 1) {
						curIdx = vals.length - 1;
						// now loop until the next unique item is found
					}
					boolean done = false;
					if (curIdx == vals.length - 1) {
						done = true;
					}
					while (!done) {
						if (vals[curIdx] != vals[curIdx + 1]) {
							done = true;
						} else {
							curIdx++;
						}
						if (curIdx == vals.length - 1) {
							done = true;
						}
					}
					// now we have the current index
					// add the value at this index to the list
					//Double dbl = new Double(vals[curIdx]);
					list.add(vals[curIdx]);
				} //while curIdx

				double[] binMaxes = new double[list.size()];
				for (int j = 0; j < binMaxes.length; j++) {
					binMaxes[j] = list.get(j);
					// now we have the binMaxes.  add the bins to the bin tree.
					// add the first one manually
					//System.out.println("binmaxes for j = " + j + " is " +  binMaxes[j]);
				}

				if (binMaxes.length < 2) {
							 BinDescriptor nbd = BinDescriptorFactory.createMinMaxBinDescriptor(inputs[i],tbl);
								bins.add(nbd);
					 } else { // binMaxes has more than one element

				BinDescriptor bd =
					BinDescriptorFactory.createMinNumericBinDescriptor(
						inputs[i],
						binMaxes[0],
						nf,
						tbl);
				bins.add(bd);

				// add the other bins
				// now add the rest
				for (int j = 1; j < binMaxes.length - 1; j++) {
					bd =
						BinDescriptorFactory.createNumericBinDescriptor(
							inputs[i],
							binMaxes[j - 1],
							binMaxes[j],
							nf,
							tbl);
					bins.add(bd);
				}

				// now add the last bin
				//if (binMaxes.length > 1)
					bd =
						BinDescriptorFactory.createMaxNumericBinDescriptor(
							inputs[i],
							binMaxes[binMaxes.length - 2],
							nf,
							tbl);
				//else
				//bd =
				//		BinDescriptorFactory.createMaxNumericBinDescriptor(
				//			inputs[i],
				//			binMaxes[binMaxes.length - 1],
				//			nf,
				//			tbl);
				bins.add(bd);
				}

			} else {
				// if it is nominal, create a bin for each unique value.
				String[] vals = TableUtilities.uniqueValues(tbl, inputs[i]);
				for (int j = 0; j < vals.length; j++) {
					String[] st = { vals[j] };
					BinDescriptor bd =
						BinDescriptorFactory.createTextualBin(
							inputs[i],
							vals[j],
							st,
							tbl);
					bins.add(bd);
				}


			}
		}
		BinDescriptor[] bn = new BinDescriptor[bins.size()];
		System.out.println("bin size " + bins.size());
		for (int i = 0; i < bins.size(); i++) {
			bn[i] = (BinDescriptor) bins.get(i);

		}
		return bn;

		//return bt;
	}


}
// QA comments Anca:
//added check for input/output attribute selections - since ChooseAttribute does not guarantee selections
//moved create*BinDescriptor methods to BinDescriptorFactory

/**
* 11-18-03 Vered - changes in sameWeight: curIdx = -1 and not 0.
*          this makes the first bin not to be too large if there isn't any
*          good reason for it. (beforehand, the first bin was by default of
*          size weight+1, even if all values were unique.
       *
       * 01-05-04: ready for basic
*/

/**
* 12-2-03 Anca - added support for missing values by adding an extra textualBin
* with name "unknown" for columns that have missing values.
* 12 -15-03 Anca  - added support for eliminating missing values when bin boundaries are defined
* 12 -16 -03 Anca - moved creation of "unknown" bins to BinTransform constructor
**/


/**
 * 01-11-04: Vered
 * Module is pulled back into qa process.
 * bug 216 - array index out of bounds exception when performing weight binning
 * ona subset table (which its subset does not include all of the records of the
 * original table) with missing values. (fixed).
*/