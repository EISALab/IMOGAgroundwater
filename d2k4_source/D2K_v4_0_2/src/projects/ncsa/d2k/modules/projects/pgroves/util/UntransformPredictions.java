package ncsa.d2k.modules.projects.pgroves.util;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

import java.util.List;


/**
	Takes a Predictiontable and calls the untransform method provided
	by a transformation on only the prediction columns. this is done by
	making a copy and then making a new table with the original inputs
	and outputs but the new untransformed predictions.
	

	@date 04/02/04
	@author pgroves
	*/

public class UntransformPredictions extends DataPrepModule 
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

		PredictionTableImpl rawTable = (PredictionTableImpl)pullInput(0);
		ReversibleTransformation trans = (ReversibleTransformation)pullInput(1);
		
		int[] predCols = rawTable.getPredictionSet();
		int[] outCols = rawTable.getOutputFeatures();
		int numOuts = outCols.length;

		if(numOuts > 0){
			//swap the preds and outputs
			for(int i = 0; i < numOuts; i++){
				Column tmp = rawTable.getColumn(outCols[i]);
				rawTable.setColumn(rawTable.getColumn(predCols[i]), outCols[i]);
				rawTable.setColumn(tmp, predCols[i]);
			}
		}else{
			rawTable.setOutputFeatures(predCols);
		}
		// hide the predictions from the transformation
		//
		rawTable.setPredictionSet(null);
		trans.untransform(rawTable);

		//swap the preds and outs again so they are in the right place
		rawTable.setPredictionSet(predCols);
		for(int i = 0; i < numOuts; i++){
			Column tmp = rawTable.getColumn(outCols[i]);
			rawTable.setColumn(rawTable.getColumn(predCols[i]), outCols[i]);
			rawTable.setColumn(tmp, predCols[i]);
		}
		if(numOuts < 0){
			rawTable.setOutputFeatures(new int[0]);
		}
		
		
		pushOutput(rawTable, 0);
	}
		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return 	
			"Applies the untransform method of transformation to a table. " +
			"First it puts the predictions in the output column slots so " +
			" that the predictions and not the outputs will be untransformed."+
			" Should only be used for transformations that affect only the " +
			" outputs.";
	}
	
   	public String getModuleName() {
		return "UntransformPredictions";
	}
	public String[] getInputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.table.basic.PredictionTableImpl",
			"ncsa.d2k.modules.core.datatype.table.ReversibleTransformation"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: 
				return 
					"A prediction table with the predictions filled in";
			case 1: 
				return "A reversible transformation that affects the outputs.";
			case 2: 
				return "";
			default: return "No such input";
		}
	}
	
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Prediction Table";
			case 1:
				return "Reversible Transform";
			case 2:
				return "";
			default: return "No such input";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.table.PredictionTable"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: 
				return "The same table, with the transformations reversed.";
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
				return "Untransformed Table";
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
			
					

			

								
	
