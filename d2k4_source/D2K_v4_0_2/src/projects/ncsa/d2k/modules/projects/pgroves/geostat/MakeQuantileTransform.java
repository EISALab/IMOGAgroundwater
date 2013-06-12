package ncsa.d2k.modules.projects.pgroves.geostat;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

import ncsa.d2k.modules.PredictionModelModule;


/**
 * Creates a QuantileTransformation object based on 
 * an example table and passes it on (without applying
 * it to the table).


	@author pgroves
	@date 04/01/04
	*/

public class MakeQuantileTransform extends ComputeModule 
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

		ExampleTable et = (ExampleTable)pullInput(0);
		PredictionModelModule pmm = (PredictionModelModule)pullInput(1);
		
		QuantileTransformation trans = new QuantileTransformation(et, 
				pmm);
		pushOutput(trans, 0);
		
	}
		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return 	
			"Generates a reversible transformation that replaces"+
			"values with their ranking. This transformation only works"+
			" for the table that it is built on (the one pulled in here)." +
			" This differs from a normal ranking transformation in that " +
			"it breaks ties found in the ranking of output features by " +
			"comparing the values for those outputs that are predicted by " +
			" a prediction model. Such a model is normally some type of " +
			"nearest neighbor method. The quantile transform is therefore " +
			"only valid for numeric output features.";
	}
	
   	public String getModuleName() {
		return "MakeQuantileTransform";
	}
	public String[] getInputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.table.ExampleTable",
			"ncsa.d2k.modules.PredictionModelModule"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: 
				return "An example table with the inputs and outputs set.";
			case 1: 
				return "A PredictionModel, built using the entire data set " +
					"found in SourceData. Each point will be 'predicted' using" +
					" this model, and the predictions will be used to breaks ties " +
					"when ranking the outputs.";
			case 2: 
				return "";
			default: return "No such input";
		}
	}
	
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Source Data";
			case 1:
				return "Neighborhood Prediction Model";
			case 2:
				return "";
			default: return "No such input";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {
			"ncsa.d2k.modules.projects.pgroves.geostat.QuantileTransformation"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: 
				return "A Quantile Transformation Object. This transformation " +
					" will only work for the sourceData used here. " +
					"The reverse transformation, however, will work for any " +
					"table with the same structure (columns and features) as the" +
					" sourceData. All transforms are based on the values " +
					" in the table passed in here.";
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
				return "Quantile Transformation";
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
			
					

			

								
	
