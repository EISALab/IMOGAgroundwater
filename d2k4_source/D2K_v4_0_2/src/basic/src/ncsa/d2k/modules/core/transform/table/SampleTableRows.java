package ncsa.d2k.modules.core.transform.table;


import java.util.*;
import java.beans.PropertyVetoException;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.transform.table.gui.properties.*;

/**
  Creates a sample of the given Table.  If the sequential sampling is
  set, then the first N rows of the table will be the sample.
  Otherwise, the sampled table will contain N random rows (sampled
  without replacement) from the table.  The original table is left
  untouched.

  @author David Clutter, revised Xiaolei Li (07/15/03)
*/
public class SampleTableRows extends DataPrepModule  {

	public final int RANDOM = 0;
	public final int SEQUENTIAL = 1;

	public final int ABSOLUTE = 10;
	public final int PERCENTAGE = 11;

	/**
	  The number of rows to sample.  This could either be an absolute
	  value or a percentage of the entire dateset.
	  */
	double N = 1;

	/**
	  How to calculate the number of rows to sample.  Either absolute or
	  percentage.
	  */
	private int samplingSizeType = ABSOLUTE;

	/**
	  The type of sampling to use: random or sequential.
	  */
	private int samplingMethod = RANDOM;

	/**
	  The seed for the random number generator.
	  */
	int seed = 0;

	/**
	   Return a description of the function of this module.
	   @return A description of this module.
	*/
	public String getModuleInfo()
	{
		String s = "<p>Overview: ";
		s += "This module samples the input <i>Table</i> and chooses a certain number of rows to copy ";
		s += "to a new <i>Sample Table</i>.  The number of rows and sampling method are determined by the ";
		s += "module properties. ";

		s += "</p><p>Detailed Description: ";
		s += "This module creates a new <i>Sample Table</i> by sampling rows of the input <i>Table</i>.  If <i>Random Sampling</i> ";
		s += "is not set, the first <i>Sample Size</i> rows in the input table are copied to the new table.  If it is ";
		s += "set, <i>Sample Size</i> rows are selected randomly from the input table, using the <i>Random Seed</i> ";
		s += "to seed the random number generator.  If the same seed is used across runs with the same input table, ";
		s += "the sample tables produced by the module will be identical. ";

		s += "</p><p>";
		s += "If the input table has <i>Sample Size</i> or fewer rows, an exception will be raised. ";

		s += "</p><p>";
		s += "An <i>OPT</i>, optimizable, version of this module that uses control ";
		s += "parameters encapsulated in a <i>Parameter Point</i> instead of properties is also available. ";

		s += "</p><p>Data Handling: ";
		s += "The input table is not changed. ";

		s += "</p><p>Scalability: ";
		s += "This module should scale very well. There must be memory to accommodate both the input table ";
		s += "and the resulting sample table. ";

		return s;
	}

	/**
	  Return a custom gui for setting properties.
	  @return CustomModuleEditor
	  */
	public CustomModuleEditor getPropertyEditor()
	{
		return new SampleTableRows_Props(this);
	}

	/**
	   Return the name of this module.
	   @return The name of this module.
	*/
	public String getModuleName()
	{
		return "Sample Table Rows";
	}

	/**
	   Return a String array containing the datatypes the inputs to this
	   module.
	   @return The datatypes of the inputs.
	*/
	public String[] getInputTypes()
	{
		String[] types = {"ncsa.d2k.modules.core.datatype.table.Table"};
		return types;
	}

	/**
	   Return a String array containing the datatypes of the outputs of this
	   module.
	   @return The datatypes of the outputs.
	*/
	public String[] getOutputTypes()
	{
		String[] types = {"ncsa.d2k.modules.core.datatype.table.Table"};
		return types;
	}

	/**
	   Return a description of a specific input.
	   @param i The index of the input
	   @return The description of the input
	*/
	public String getInputInfo(int i)
	{
		switch (i) {
			case 0: return "The table that will be sampled.";
			default: return "No such input";
		}
	}

	/**
	   Return the name of a specific input.
	   @param i The index of the input.
	   @return The name of the input
	*/
	public String getInputName(int i)
	{
		switch(i) {
			case 0: return "Table";
			default: return "No such input";
		}
	}

	/**
	   Return the description of a specific output.
	   @param i The index of the output.
	   @return The description of the output.
	*/
	public String getOutputInfo(int i)
	{
		switch (i) {
			case 0: return "A new table containing a sample of rows from the original table.";
			default: return "No such output";
		}
	}

	/**
	   Return the name of a specific output.
	   @param i The index of the output.
	   @return The name of the output
	*/
	public String getOutputName(int i)
	{
		switch(i) {
			case 0: return "Sample Table";
			default: return "No such output";
		}
	}

	public void setSamplingMethod(int val)
	{
		samplingMethod = val;
	}

	public int getSamplingMethod()
	{
		return samplingMethod;
	}

	public void setSamplingSizeType(int val)
	{
		samplingSizeType = val;
	}

	public int getSamplingSizeType()
	{
		return samplingSizeType;
	}

	public void setSampleSize(double val)
	{
		N = val;
	}

	public double getSampleSize()
	{
		return N;
	}

	public void setSeed(int i) throws PropertyVetoException
	{
		seed = i;
	}

	public int getSeed()
	{
		return seed;
	}

	/**
	 * Return a list of the property descriptions.
	 * @return a list of the property descriptions.
	 */
	public PropertyDescription [] getPropertiesDescriptions ()
	{
		PropertyDescription [] pds = new PropertyDescription [4];

		pds[0] = new PropertyDescription ("samplingMethod", "Sampling " +
			"Method", "The method of sampling to use.  The choices are: "+
			"<p>Random: samples will be drawn randomly without " +
			"replacement from the original table.</p>" +
			"<p>Sequential: the first entries in the table will be used"
			+ "as the sample.");
		pds[1] = new PropertyDescription ("seed", "Random Seed", "The "
				+ "seed for the random number generator used to select "
				+ "the sample set of <i>Sample Size</i> rows.  It must "
				+ "be greater than or equal to 0.  The seed is not " +
				"used if <i>Random Sampling</i> is off.");
		pds[2] = new PropertyDescription ("samplingSizeType", "Sample Size Type",
				"To calculate the sample size as either an absolute " +
				"value or percentage of the original.");
		pds[3] = new PropertyDescription ("sampleSize", "Sample Size",
				"The number of rows to include in the resulting table. "
				+ "Could be an absolute value or percentage of " +
				"original.");

		return pds;
	}


	/**
	   Perform the calculation.
	*/
	public void doit() throws Exception
	{
		Table orig = (Table) pullInput(0);
		Table newTable = null;

		int realN;

		if (samplingSizeType == ABSOLUTE)
			realN = (int) N;
		else
			realN = (int) (N * orig.getNumRows());

		if (realN > (orig.getNumRows() - 1)) {
			int numRows = orig.getNumRows() - 1;
			throw new Exception( getAlias()  + ": Sample size (" + realN
					+ ") is >= the number of rows in the table (" +
					numRows + "). \n" + "Use a smaller sample size.");
		}

		System.out.println("Sampling " + realN + " rows from a table " +
				"of " + orig.getNumRows() + " rows.");

		/* only use the first N rows */
		if (samplingMethod == SEQUENTIAL) {
			newTable = (Table) orig.getSubset(0, realN);
		}
		else {
			int numRows = orig.getNumRows();
			int[] keeps = new int[realN];

			Random r = new Random(seed);

			/* optimization to do the least amount of work.  depending
			 * on which set is bigger, either make a set to keep or a
			 * set not to keep. */
			if (realN < (orig.getNumRows() / 2)) {
				ArrayList keepers = new ArrayList();

				for (int i = 0; i < realN; i++) {
					int ind = Math.abs(r.nextInt()) % numRows;
					Integer indO = new Integer(ind);

					if (keepers.contains(indO)) {
						i--;
					}
					else {
						keeps[i] = ind;
						keepers.add(indO);
					}
				}
			}
			else {
				ArrayList pickers = new ArrayList();

				for (int i = 0, n = numRows; i < n; i++) {
					pickers.add(new Integer(i));
				}

				for (int i = 0; i < realN; i++) {
					int ind = Math.abs(r.nextInt()) % pickers.size();
					//System.out.println(pickers.size() + " " + ind);
					keeps[i] = ( (Integer) pickers.remove(ind)).intValue();
				}
			}

			newTable = orig.getSubset(keeps);
		}

		//System.out.println("Sampled table contains " +
		//newTable.getNumRows() + " rows.");

		pushOutput(newTable, 0);


//		MutableTable orig = (MutableTable)pullInput(0);
//		MutableTable newTable = (MutableTable)orig.copy();
//
//		// only keep the first N rows
//		if(useFirst) {
//			for(int i = newTable.getNumRows()-1; i > N-1; i--)
//				newTable.removeRow(i);
//		}
//		else {
//			Random r = new Random(seed);
//			int numRows = newTable.getNumRows();
//			int numRowsToDelete = numRows - N;
//			// for each rowToDelete, randomly pluck one out
//			for(int i = 0; i < numRowsToDelete; i++) {
//				numRows = newTable.getNumRows();
//				newTable.removeRow(Math.abs(r.nextInt()) % numRows);
//			}
//		}
//		pushOutput(newTable, 0);
	}
}



// Start QA Comments
// 4/8/03 - Ruth updated Module Info and properties to match the OPT
// 			version and also to add a comment that it exists.  Also
// 			added Exceptions of Sample Size < 1 or seed < 0.
// End QA Comments.
