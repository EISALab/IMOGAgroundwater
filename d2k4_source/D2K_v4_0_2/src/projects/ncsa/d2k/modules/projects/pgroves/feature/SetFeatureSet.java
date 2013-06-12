package ncsa.d2k.modules.projects.pgroves.feature;


import ncsa.d2k.core.modules.*;

import ncsa.d2k.modules.core.datatype.table.*;



/**
	Assigns the input, output, or prediction feature set to an ExampleTable.

	@author pgroves
	@date 05/27/04
*/

public class SetFeatureSet extends DataPrepModule 
	implements java.io.Serializable {

	//////////////////////
	//d2k Props
	////////////////////
	boolean inputFeatures = false;
	boolean outputFeatures = false;
	boolean predictionFeatures = false;	
	
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
		ExampleTable et = (ExampleTable)pullInput(0);
		int[] features = (int[])pullInput(1);

		if(this.inputFeatures){
			et.setInputFeatures(features);
		}else if(this.outputFeatures){
			et.setOutputFeatures(features);
		}else if(this.predictionFeatures){
			((PredictionTable)et).setPredictionSet(features);
		}else{
			throw new NullPointerException(this.getAlias() +
			": You must select which type of feature in this module's " +
			"properties.");
		}
		pushOutput(et, 0);

		
	}
		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return 	
			"Assigns an int array to be the either the input, output, or "+
			"prediction feature set. Which one is set in properties."+
			""+
			"";
	}
	
   	public String getModuleName() {
		return "SetFeatureSet";
	}
	public String[] getInputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.table.ExampleTable",
			"[I"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: 
				return "An ExampleTable to assign features to. Must be a " +
				"PredictionTable if prediction features are being set.";
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
				return "Table";
			case 1:
				return "Feature Set";
			case 2:
				return "";
			default: return "No such input";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: 
				return "The input table, with the appropriate features reassigned";
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
				return "Table W/ New Features";
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
	public boolean getInputFeatures(){
		return inputFeatures;
	}
	public void setInputFeatures(boolean b){
		inputFeatures = b;
	}
	public boolean getOutputFeatures(){
		return outputFeatures;
	}
	public void setOutputFeatures(boolean b){
		outputFeatures = b;
	}
	public boolean getPredictionFeatures(){
		return predictionFeatures;
	}
	public void setPredictionFeatures(boolean b){
		predictionFeatures=b;
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
			
					

			

								
	
