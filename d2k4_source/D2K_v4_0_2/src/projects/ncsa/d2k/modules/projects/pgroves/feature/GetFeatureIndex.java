package ncsa.d2k.modules.projects.pgroves.feature;


import ncsa.d2k.core.modules.*;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.impl.*;


/**
 * Takes an et and an int i. Returns the index into the table
 * of the i'th ***feature, where ***feature is inputFeature, outputFeature
 * or prediction column (set in properties).

	@author pgroves
	@date 05/11/04
*/

public class GetFeatureIndex extends DataPrepModule 
	implements java.io.Serializable {

	//////////////////////
	//d2k Props
	////////////////////
	
	boolean debug=false;	

	boolean predictionColumn = false;
	boolean inputColumn = false;
	boolean outputColumn = false;
	
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
	/**
		does it
	*/
	public void doit() throws Exception{
		if(debug){
			System.out.println(getAlias()+":Firing");
		}
		ExampleTable et = (ExampleTable)pullInput(0);
		int whichFeature = ((Integer)pullInput(1)).intValue();
		int i = -1;
		if(inputColumn){
			i = et.getInputFeatures()[whichFeature];
		}else if(outputColumn){
			i = et.getOutputFeatures()[whichFeature];
		}else if(predictionColumn){
			i = ((PredictionTable)et).getPredictionSet()[whichFeature];
		}else{
			System.out.println(this.getAlias() +
					": You need to select either inputColumns, outputColumns, "+
					"or predictionColumns. Returning -1.");
		}
		pushOutput(new Integer(i), 0);
	}
		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return 	
			"Returns the index of the i'th feature column. The type of " +
			"feature can be input, output, or prediction."+
			""+
			"";
	}
	
   	public String getModuleName() {
		return "GetFeatureIndex";
	}
	public String[] getInputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.table.ExampleTable",
		"java.lang.Integer"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: 
				return "An example table with at least the inputs, outputs, " +
					" or predictions set, depending on which one is being " +
					"considered. If predictions, the table must actually be " +
					"a PredictionTable";
			case 1: 
				return "Of the feature set, the index (starting at zero) to " +
					"output. That is, if you want the second input, which is " +
					"column 6 of the table, this input to the module should " +
					"be 1 (and the output will be 5).";
			case 2: 
				return "";
			default: return "No such input";
		}
	}
	
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Table with Features";
			case 1:
				return "Which Feature Index";
			case 2:
				return "";
			default: return "No such input";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {
			"java.lang.Integer"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: 
				return "The index of the column that is the i'th feature, where" +
					" i is the input 'Which Feature Index' and the type of " +
					"feature is determined by which feature type is set to " +
					" true in properties.";
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
				return "Feature Index";
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
	public boolean getPredictionColumn(){
		return predictionColumn;
	}
	public void setPredictionColumn(boolean b){
		predictionColumn = b;
	}
	public boolean getInputColumn(){
		return inputColumn;
	}
	public void setInputColumn(boolean b){
		inputColumn = b;
	}
	public boolean getOutputColumn(){
		return outputColumn;
	}
	public void setOutputColumn(boolean b){
		outputColumn = b;
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
			
					

			

								
	
