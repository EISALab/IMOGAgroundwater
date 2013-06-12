package ncsa.d2k.modules.projects.pgroves.geostat;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;


/**
 * calculates an R-squared score for a set of predictions
 * for a single output. It also weights those values
 * near the origin more heavily than those further out.
 * Currently, however, the weighting scheme is not implemented
 * and all weights are 1.


	@author pgroves
	@date 04/06/04
	*/

public class VariogramResiduals extends DataPrepModule 
	implements java.io.Serializable {

	//////////////////////
	//d2k Props
	////////////////////
	
	boolean debug=false;		
	/////////////////////////
	/// other fields
	////////////////////////

	//////////////////////////
	///d2k control methods
	///////////////////////

	public boolean isReady(){
		return super.isReady();
	}
	public void endExecution(){
		wipeFields();
		super.endExecution();
	}
	public void beginExecution(){
		wipeFields();
		super.beginExecution();
	}
	public void wipeFields(){
	}
	
	/////////////////////
	//work methods
	////////////////////
	/*
		does it
	*/
	public void doit() throws Exception{

		PredictionTable pt = (PredictionTable)pullInput(0);
		double score = calcRSquared(pt);
		if(debug){
			System.out.println(getAlias()+": R-Squared:" + score);
		}
		pushOutput(new Double(score), 0);
	}
	
	/**
	 * calculates one over residual score. works pretty much
	 * the same as a stand root-mean-squared error metric for
	 * a single output, but weights those points that are closer
	 * to the origin (in the input space) higher. Assumes only
	 * one input.
	 */
	public double calcRSquared(PredictionTable pt){
		// the diff between the first output and prediction for
		// each example
		double[] errors;
		//the distance from the original in the input space for
		//each example
		double[] distances;

		int i, j;
		double pred, actual;
		int input = pt.getInputFeatures()[0];
		int output = pt.getOutputFeatures()[0];
		
		int numRows = pt.getNumRows();
		errors = new double[numRows];
		distances = new double[numRows];
		//System.out.println("\n\n");
		for(i = 0; i < numRows; i++){
			actual = pt.getDouble(i, output);
			pred = pt.getDoublePrediction(i, 0);
			errors[i] = actual - pred;

			//System.out.println("bin:" + i +
			//		"actual:"+actual+" pred:"+pred+"error:"+errors[i]);

			distances[i] = pt.getDouble(i, input);
		}
		//System.out.println("\n");

		double weight;
		double r2 = 0;;
		for(i = 0; i < numRows; i++){
			weight = calcWeight(distances[i]);
		//	System.out.println("dist:"+distances[i] + " weight:" + weight);
			r2 += weight * errors[i] * errors[i];
		//	System.out.println("errors:"+errors[i] + " r2 tally:" + r2);
		//	System.out.println();
		}
		return r2;
	}

	protected double calcWeight(double dist){
		return (1.0 / dist);
	}
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return 	
			"Calculates an error score (residual) for a set of"+
			" predictions for the first output. The value returned"+
			" is the sum of the square of the errors, multiplied by "+
			" a weight. The weight is based on the distance to the origin" +
			" in the input feature space, with examples closer to the origin"+
			" having higher weights. This is currently unimplemented, though" +
			", and the weight is always 1.";
	}
	
   	public String getModuleName() {
		return "VariogramResiduals";
	}
	public String[] getInputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.table.PredictionTable"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: 
				return "A Prediction Table with one input and one output and "+
					" the predictions already made.";
			case 1: 
				return "";
			case 2: 
				return "";
			default: return "No such input";
		}
	}
	
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Variogram Model Predictions";
			case 1:
				return "";
			case 2:
				return "";
			default: return "No such input";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {
			"java.lang.Double"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: 
				return "A single, cummulative score. The score is the sum " +
					" of the residuals squared multiplied by a weight." +
					" The weight is a function of the distance to the origin" +
					" in the input feature space.";
			case 1:
				return "";
			case 2:
				return "";
			default: return "No such output";
		}
	}
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Weighted R-Squared Error Score";
			case 1:
				return "";
			case 2:
				return "";
			default: return "No such output";
		}
	}		
	////////////////////////////////
	//D2K Property get/set methods
	///////////////////////////////
	public void setDebug(boolean b){
		debug=b;
	}
	public boolean getDebug(){
		return debug;
	}
	/*
	public boolean get(){
		return ;
	}
	public void set(boolean b){
		=b;
	}
	public double  get(){
		return ;
	}
	public void set(double d){
		=d;
	}
	public int get(){
		return ;
	}
	public void set(int i){
		=i;
	}

	public String get(){
		return ;
	}
	public void set(String s){
		=s;
	}
	*/
}
			
					

			

								
	
