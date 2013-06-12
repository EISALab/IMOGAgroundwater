package ncsa.d2k.modules.projects.pgroves.feature;


import ncsa.d2k.core.modules.*;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;



/**
	returns either the input, output, or prediction feature indices
	of an example table.

	@author pgroves
	@date 05/27/04
*/

public class GetFeatureSet extends DataPrepModule 
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
		int[] features = null;
		if(this.inputFeatures){
			features = et.getInputFeatures();
		}else if(this.outputFeatures){
			features = et.getOutputFeatures();
		}else if(this.predictionFeatures){
			features = ((PredictionTable)et).getPredictionSet();
		}
		if(features == null){
			throw new NullPointerException(getAlias() + " You must select " +
			" which type of features.");
		}
		pushOutput(features, 0);
	}
		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return 	
			"Pushes out the set of column indices of the either the "+
			"input, output, or prediction features. Which one is "+
			"determined through properties."+
			"";
	}
	
   	public String getModuleName() {
		return "GetFeatureSet";
	}
	public String[] getInputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: 
				return "An ExampleTable. If the prediction set is sought, " +
				" this should be an instance of PredictionTable.";
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
				return "Table W/ Features";
			case 1:
				return "";
			case 2:
				return "";
			default: return "No such input";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {
			"[I"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: 
				return "An array of integers containing the requested column " +
				"indices.";
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
				return "Feature Set";
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
			
					

			

								
	
