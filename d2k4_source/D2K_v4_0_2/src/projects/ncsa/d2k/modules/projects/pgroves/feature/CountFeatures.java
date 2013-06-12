package ncsa.d2k.modules.projects.pgroves.feature;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.parameter.*;


/** 
	Given a parameter point of booleans, counts how many are true


	@author pgroves
	@date 02/03/04
	*/

public class CountFeatures extends DataPrepModule 
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

		ParameterPoint pp = (ParameterPoint)pullInput(0);

		int numFeatures = pp.getNumParameters();
		int numTrueFeatures = 0;

		for(int i = 0; i < numFeatures; i++){
			if(pp.getValue(i) > 0.5){
				numTrueFeatures++;
			}
		}

		pushOutput(new Integer(numTrueFeatures), 0);
	}
		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return 	
			"Simply counts the number of 'true' parameters in a boolean"+
			" ParameterPoint"+
			""+
			"";
	}
	
   	public String getModuleName() {
		return "CountFeatures";
	}
	public String[] getInputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.parameter.ParameterPoint"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: 
				return "A ParameterPoint, assumed to be all booleans. Actually,"+
				" everything above 0.5 will be counted as 'true', and everything"+
				" else as 'false.'";
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
				return "Boolean Parameter Point";
			case 1:
				return "";
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
				return "The number of Parameters that are 'true' (above 0.5).";
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
				return "True Feature Count";
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
			
					

			

								
	
