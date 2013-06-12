package ncsa.d2k.modules.core.transform.attribute;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
/**
	MatchFields.java

	 Takes two Tables, sets the second's inputFeatures and output
	 features to that of the first (which must be an Example Table)

	 @author Peter Groves
*/
public class MatchFields extends ncsa.d2k.core.modules.DataPrepModule
{

	/**
		This pair returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "      The table that contains the input and output columns already set   ";
			case 1: return "      The table that will become a new ExampleTable (shallow copy) with the input and output columns set to be the same as the other input table   ";
			default: return "No such input";
		}
	}

	/**
		This pair returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.ExampleTable","ncsa.d2k.modules.core.datatype.table.Table"};
		return types;
	}

	/**
		This pair returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo(int index) {
		switch (index) {
			case 0: return "No such output";
			default: return "No such output";
		}
	}

	/**
		This pair returns an array of strings that contains the data types for the outputs.
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
		return types;
	}

	/**
		This pair returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo() {
		return "<html>  <head>      </head>  <body>    Takes two Tables, sets the second's inputFeatures and output features to     that of the first (which must be an Example Table)  </body></html>";
	}

	/**
	*/
	public void doit() throws Exception {
		ExampleTable et=(ExampleTable)pullInput(0);
		Table tt=(Table)pullInput(1);

		ExampleTable et2= tt.toExampleTable();

		int[] ins=new int[et.getNumInputFeatures()];
		int[] outs=new int[et.getNumOutputFeatures()];

		System.arraycopy(et.getInputFeatures(), 0, ins, 0, ins.length);
		System.arraycopy(et.getOutputFeatures(), 0, outs, 0, outs.length);

		et2.setInputFeatures(ins);
		et2.setOutputFeatures(outs);

		pushOutput(et2, 0);
	}


	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "FieldMatch";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "FieldSourceTable";
			case 1:
				return "FieldSetTable";
			default: return "NO SUCH INPUT!";
		}
	}

	/**
	 * Return the human readable name of the indexed output.
	 * @param index the index of the output.
	 * @return the human readable name of the indexed output.
	 */
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "output0";
			default: return "NO SUCH OUTPUT!";
		}
	}
}

