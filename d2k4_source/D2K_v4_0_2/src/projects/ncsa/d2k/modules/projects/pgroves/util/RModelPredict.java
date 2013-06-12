package ncsa.d2k.modules.projects.pgroves.util;


import ncsa.d2k.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
/**


=======
	ModelPredict

		takes in a model and a predictionTable
		and runs the model's predict function
		on the test table
*/
public class RModelPredict extends ncsa.d2k.core.modules.ReentrantComputeModule
	//ncsa.d2k.core.modules.ComputeModule
{

	/**
		This pair returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "      The test set  ";
			case 1: return "      The model  ";
			default: return "No such input";
		}
	}

	/**
		This pair returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.ExampleTable","ncsa.d2k.modules.PredictionModelModule"};
		return types;
	}

	/**
		This pair returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo(int index) {
		switch (index) {
			case 0: return "      The PredictionTable with the prediction columns filled in by the model  ";
			case 1: return "      The Model.  ";
			default: return "No such output";
		}
	}

	/**
		This pair returns an array of strings that contains the data types for the outputs.
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.PredictionTable","ncsa.d2k.modules.PredictionModelModule"};
		return types;
	}

	/**
		This pair returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo() {
		return "<html>  <head>      </head>  <body>    takes in a model and a TestTable and runs the model's predict function on     the test table  </body></html>";
	}


	/**
	*/
	public void doit() throws Exception {
		ExampleTable tt= (ExampleTable)pullInput(0);
		PredictionModelModule pmm=(PredictionModelModule)pullInput(1);

		PredictionTable pt=pmm.predict(tt);
		pushOutput(pt, 0);
		pushOutput(pmm, 1);

		/*ExampleTable tt= (ExampleTable) pullInput (0);
		PredictionModelModule pmm = (PredictionModelModule) pullInput (1);
		tt = (PredictionTable) pmm.predict (tt);
		pushOutput(tt, 0);
		*/
	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "R-ModelPredict";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "TestTable";
			case 1:
				return "PredictionModel";
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
				return "TTout";
			case 1:
				return "pmm";
			default: return "NO SUCH OUTPUT!";
		}
	}
}

