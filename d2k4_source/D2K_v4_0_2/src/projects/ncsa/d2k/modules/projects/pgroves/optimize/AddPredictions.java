package ncsa.d2k.modules.projects.pgroves.optimize;


import ncsa.d2k.core.modules.*;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.impl.*;


/**
	Adds the predictions of two tables together, returning one
	of the tables with the new predictions.

	@author pgroves
	@date 05/05/04
*/

public class AddPredictions extends ComputeModule 
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
		if(debug){
			System.out.println(getAlias()+":Firing");
		}
		PredictionTable pt1 = (PredictionTable)pullInput(0);
		PredictionTable pt2 = (PredictionTable)pullInput(1);

		PredictionTable pt3 = this.addPredictions(pt1, pt2);

		pushOutput(pt3, 0);
		
	}

	/**
		Adds the predictions of a Prediction Table (pt2) to the predictions
		of another prediction table (pt1). Pt1 is modified in place
		and returned.

		@param pt1 the main prediction table
		@param pt2 another prediction table

		@return pt1 with the predictions of pt2 added to its predictions
		*/

	public PredictionTable addPredictions(PredictionTable pt1, 
			PredictionTable pt2){

		int numPredCols = pt1.getPredictionSet().length;
		int numRows = pt1.getNumRows();
	
		if(pt1.getNumRows() != pt2.getNumRows()){
			System.out.println(this.getAlias() + ": ERROR: NumRows Table 1 = " +
			pt1.getNumRows() + "	NumRows Table 2 = " + pt2.getNumRows());
			System.out.println("****The predictions were not added, returning "+
			"original predictions of table 1.****");
			
			return pt1;
		}
		if(pt1.getPredictionSet().length != pt2.getPredictionSet().length){
			System.out.println(this.getAlias() + ": ERROR: NumPreds Table 1 = " +
			pt1.getPredictionSet().length + "	NumRows Table 2 = " + 
			pt2.getPredictionSet().length);
			System.out.println("****The predictions were not added, returning "+
			"original predictions of table 1.****");
			
			return pt1;
		}

		double dp1, dp2, dps; //double predictions 1, 2 and their sum
		for(int i = 0; i < numPredCols; i++){
			for(int j = 0; j < numRows; j++){
				dp1 = pt1.getDoublePrediction(j, i);
				dp2 = pt2.getDoublePrediction(j, i);
				dps = dp1 + dp2;

				pt1.setDoublePrediction(dps, j, i);
			}
		}
		return pt1;
	}
		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return 	
			"Adds the prediction found in the second prediction table to "+
			" the first tables predictions and returns the first."+
			""+
			"";
	}
	
   	public String getModuleName() {
		return "AddPredictions";
	}
	public String[] getInputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.table.PredictionTable",
			"ncsa.d2k.modules.core.datatype.table.PredictionTable"
			};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: 
				return "The prediction table that will be modified and returned.";
			case 1: 
				return "The prediction table whose predictions will be added to " +
				" the other's";
			case 2: 
				return "";
			default: return "No such input";
		}
	}
	
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "PredictionTable 1";
			case 1:
				return "PredictionTable 2";
			case 2:
				return "";
			default: return "No such input";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.table.PredictionTable"
			};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: 
				return "PredictionTable1 modified.";
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
				return "Summed Predicitions";
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
			
					

			

								
	
