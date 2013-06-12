package ncsa.d2k.modules.core.prediction.evaluators;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
/**
	MeanPercentError.java

	For every output feature, sums the percent errors, divides by numTestExamples, and
	outputs a VT with the output feature names and corresponding errors

	@author Peter Groves
	6/26/01


*/
public class MeanPercentError extends ncsa.d2k.modules.core.prediction.evaluators.RootMeanSquared
{

	/**
		This method returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo (int index) {
		switch (index) {
			case 0: return "      A Table with A column for each outputfeature and a row for every iteration of crossvalidation    ";
			case 1: return "      The average error    ";
			default: return "No such output";
		}
	}

	/**
		This method returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo () {
		return "<html>  <head>      </head>  <body>    Takes a TestTable with the prediction columns filled in returns a table     with mean percent errors.PROPS: untransformFirst - applies any     untransforms to the data after prediction and before calculating any     errors. crossValidate - if true, will wait for 'n ' TestTables, if false,     will just wait for one and not wait for an Integer to be passed into     input(1). printResults - will print each target/prediction pair to     System.out  </body></html>";
	}


	protected void computeError(TestTable tt, int m){

		int rows = tt.getNumRows ();
		int columns = tt.getNumOutputFeatures ();

		//store an mabs error for each output feature, make sure to initialize to zero
		double[] mpe = new double[columns];
		for (int i=0; i<mpe.length; i++){
			mpe[i]=0;
		}
		int[] ttOuts=tt.getOutputFeatures();
		int[] ttPreds=tt.getPredictionSet();

		for (int j = 0 ; j < columns ; j++){
			for (int i = 0 ; i < rows ; i++){
				double row_error;
				double prediction=tt.getDouble (i, ttPreds[j]);
				double target=tt.getDouble (i, ttOuts[j]);
				if(printResults){
				System.out.println("==="+i+" t,p: "+target+","+prediction);
				}
				row_error = Math.abs ((target-prediction)/target);
				mpe[j] += row_error;
			}
		}

		for (int j=0; j<mpe.length; j++){
			mpe[j] = (mpe[j] / rows)*100;
			metrics.setDouble(mpe[j], m, j);

		}


	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "M % Evaluator";
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
				return "Average Over All CrossVal";
			default: return "NO SUCH OUTPUT!";
		}
	}
}

