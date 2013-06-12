package ncsa.d2k.modules.core.prediction.evaluators;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

/**
	PercentErrorClassification.java

	Takes the predictions of a model and computes how many of the <i>single</i>
	output feature predictions were incorrect.  The predictions should be in the form produced by
	the module ScalarizeNominals, with a single output feature being converted into a series
	of 'k' boolean columns with only the class for that row set to true

	@author pgroves
	7/30/01
*/
public class PercentErrorClassification extends ncsa.d2k.modules.core.prediction.evaluators.RootMeanSquared
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
		return "<html>  <head>      </head>  <body>    Takes the predictions of a model and computes how many of the single     output feature predictions were incorrect. The predictions should be in     the form produced by the module ScalarizeNominals, with a single output     feature being converted into a series of 'k' boolean columns with only the     class for that row set to true PROPS: untransformFirst - applies any     untransforms to the data after prediction and before calculating any     errors. crossValidate - if true, will wait for 'n ' TestTables, if false,     will just wait for one and not wait for an Integer to be passed into     input(1). printResults - will print each target/prediction pair to     System.out  </body></html>";
	}

	protected void setupMetrics(){
			metrics=new MutableTableImpl(1);
			metrics.setColumn(new DoubleColumn(n), 0);
			metrics.setColumnLabel("%wrong",  0);
	}

	public void computeError(TestTable tt, int m){
		int rows = tt.getNumRows ();
		int columns = tt.getNumOutputFeatures ();

		int[] ttOuts=tt.getOutputFeatures();
		int[] ttPreds=tt.getPredictionSet();

		//the error tally
		double pse=0.0;

		for (int i = 0 ; i < rows ; i++){

			boolean correct=false;

			//look through the targets and find the correct answer (targ)
			for(int targ=0; targ<ttOuts.length; targ++){
				if(tt.getBoolean(i, ttOuts[targ])){
					//now see if the prediction is the same
					for(int pred=0; pred<ttPreds.length; pred++){
						if(tt.getBoolean(i, ttPreds[pred])){
							//if their the same, give this row a 'correct'
							if(targ==pred){
								correct=true;
							}else{
								correct=false;
							}
						}
					}
				}
			}
			//if wrong, increment the error
			if(!correct){
				pse++;
			}
		}
				//System.out.println("PSE:"+pse+" rows:"+rows);
				//tt.print();
				//make the error tally into a percent error
				pse=pse/rows*100;
				//put it in the output table
				metrics.setDouble(pse, m, 0);

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

