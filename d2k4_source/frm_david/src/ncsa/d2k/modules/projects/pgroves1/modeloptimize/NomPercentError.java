package ncsa.d2k.modules.projects.pgroves.modeloptimize;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

public class NomPercentError extends ncsa.d2k.modules.core.prediction.evaluators.RootMeanSquared
{



	/**
		This pair returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo(int index) {
		switch (index) {
			case 0: return "      A VT with one column indicating what percent were incorrectly classified   ";
			case 1: return "No such output";
			default: return "No such output";
		}
	}


	/**
		This pair returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo() {
		return "<html>  <head>      </head>  <body>    PROPS: untransformFirst - applies any     untransforms to the data after prediction and before calculating any     errors. crossValidate - if true, will wait for 'n ' TestTables, if false,     will just wait for one and not wait for an Integer to be passed into     input(1). printResults - will print each target/prediction pair to     System.out  </body></html>";
	}

	public void computeError(TestTable tt, int m){
		int rows = tt.getNumRows ();
		int columns = tt.getNumOutputFeatures ();

		//store an rms error for each output feature, make sure to initialize to zero
		double[] rmse = new double[columns];
		int i, j, prediction,target;
		double row_error;
		for (i=0; i<rmse.length; i++){
			rmse[i]=0;
		}

		int[] ttOuts=tt.getOutputFeatures();
		int[] ttPreds=tt.getPredictionSet();

		for (j = 0 ; j < columns ; j++){
			for (i = 0 ; i < rows ; i++){
				row_error=0;
				prediction=tt.getInt (i, ttPreds[j]);
				target=tt.getInt (i, ttOuts[j]);
				if (printResults){
					System.out.println("T,P,: "+target+","+prediction);
				}
				if(prediction!=target)
					row_error=1.0;
					
				rmse[j] += row_error;
			}
		}

		for (j=0; j<rmse.length; j++){
			rmse[j] = rmse[j] / rows;
			//rmse[j]=Math.sqrt(rmse[j]);
			//put the error in the verticalTable
			metrics.setDouble(rmse[j], m, j);
		}		
	}


	//doit is in the superclass

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "%ErrClass";
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
				return "Results";
			case 1:
				return "output1";
			default: return "NO SUCH OUTPUT!";
		}
	}
}

