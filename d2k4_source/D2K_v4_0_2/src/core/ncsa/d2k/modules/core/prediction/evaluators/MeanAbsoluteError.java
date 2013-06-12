package ncsa.d2k.modules.core.prediction.evaluators;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
/**
	MeanAbsoluteError.java

	For every output feature, sums the absolute errors, divides by numTestExamples, and
	outputs a VT with the output feature names and corresponding errors

	@author Peter Groves
	7/30/01


*/
public class MeanAbsoluteError extends ncsa.d2k.modules.core.prediction.evaluators.RootMeanSquared
{


	/**
		This method returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo (int index) {
		switch (index) {
			case 0: return "      A Table with the mean absolute  error for each feature in a separate column, rows are the different crossValidation tests.   ";
			case 1: return "      The average of all the cross-validation trials (for the first output)  ";
			default: return "No such output";
		}
	}

	/**
		This method returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo () {
		return "<html>  <head>      </head>  <body>    Given an example set and a model, check the models performance against the     data, returning the Mean Absolute error for each output feature separately     in the output table. PROPS: untransformFirst - applies any untransforms to     the data after prediction and before calculating any errors. crossValidate     - if true, will wait for 'n ' TestTables, if false, will just wait for one     and not wait for an Integer to be passed into input(1). printResults -     will print each target/prediction pair to System.out  </body></html>";
	}

	/*the only function that's here, it does the actual error
	calculation, everything else it gets from the superclass
	*/
	protected void computeError(PredictionTable tt, int m){
		int rows = tt.getNumTestExamples ();
		int columns = tt.getNumOutputFeatures ();

		//store an mabs error for each output feature, make sure to initialize to zero
		double[] mabse = new double[columns];
		for (int i=0; i<mabse.length; i++){
			mabse[i]=0;
		}
		int[] ttOuts=tt.getOutputFeatures();
		int[] ttPreds=tt.getPredictionSet();

		for (int j = 0 ; j < columns ; j++){
			for (int i = 0 ; i < rows ; i++){
				double row_error;
				double prediction=tt.getDouble (i, ttPreds[j]);
				double target=tt.getDouble (i, ttOuts[j]);
				if(printResults){
					System.out.println("=="+i+" t,p: "+target+","+prediction);
				}
				row_error = Math.abs (target-prediction);
				mabse[j] += row_error;
			}
		}

		for (int j=0; j<mabse.length; j++){
			mabse[j] = mabse[j] / rows;
			metrics.setDouble(mabse[j], m, j);
		}

	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "M ABS Evaluator";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "example set";
			case 1:
				return "nl";
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
				return "error metric";
			case 1:
				return "average";
			default: return "NO SUCH OUTPUT!";
		}
	}
}

