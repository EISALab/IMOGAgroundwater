package ncsa.d2k.modules.core.prediction;


import ncsa.d2k.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
/**


=======
	ModelPredict

		takes in a model and an example Table
		and runs the model's predict function
		on the input table
*/
public class ModelPredict extends ncsa.d2k.core.modules.ComputeModule
{
	/**
		This pair returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes() {
		String[] types = {
			"ncsa.d2k.modules.core.datatype.table.ExampleTable",
			"ncsa.d2k.modules.PredictionModelModule"};
		return types;
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Example Table";
			case 1:
				return "Prediction Model";
			default:
				return "No such input";
		}
	}
	/**
		This pair returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0:
				return "The table containing the examples that the model will be applied to.";
			case 1:
				return "The prediction model to apply.";
			default:
				return "No such input";
		}
	}

	/**
		This pair returns an array of strings that contains the data types for the outputs.
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes() {
		String[] types = {
			"ncsa.d2k.modules.core.datatype.table.PredictionTable" };
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
				return "Prediction Table";
			default:
				return "No such output";
		}
	}
	/**
		This pair returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo(int index) {
		switch (index) {
			case 0:
				return "A table with the prediction columns filled in by the model.";
			default:
				return "No such output";
		}
	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "Model Predict";
	}

	/**
		This pair returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo() {
		StringBuffer sb = new StringBuffer( "Overview: This module applies a prediction model to a table of examples and ");
		sb.append("makes predictions for each output attribute based on the values of the input attributes. ");

		sb.append("</p><p>Detailed Description:  This module applies a previously built model to a new set of examples that have the ");
		sb.append("same attributes as those used to train/build the model.  The module creates a new table that contains ");
		sb.append("columns for each of the values the model predicts, in addition to the columns found in the original table. ");
		sb.append("The new columns are filled in with values predicted by the model based on the values of the input attributes. ");

		return sb.toString();
	}


	/**
	*/
	public void doit() throws Exception {
		ExampleTable tt= (ExampleTable)pullInput(0);
		PredictionModelModule pmm=(PredictionModelModule)pullInput(1);

		PredictionTable pt=pmm.predict(tt);
		pushOutput(pt, 0);

	}

}

// Start QA Comments
// 3/30/03   Ruth removed output port for model - no longer copy through
//           Still needs comments about impact on input table and if possible better description.
//
