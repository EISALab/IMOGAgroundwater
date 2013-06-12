package ncsa.d2k.modules.projects.pgroves.optimize;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.datatype.parameter.*;


/**
	Takes in an object and a parameter point. if the parameter point
	at the index defined in the properties matches the double value
	defined in properties, the object is passed.

	@author pgroves
	*/

public class PassIfParameterMatches extends ComputeModule 
	implements java.io.Serializable {

	//////////////////////
	//d2k Props
	////////////////////
	int paramIndex = 0;
	double paramValue = 0.0;
	
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
		Object obj = pullInput(0);
		//System.out.println(obj);
		ParameterPoint pp = (ParameterPoint)pullInput(1);
		//System.out.println(pp);
		if(pp.getValue(paramIndex) == paramValue){
			pushOutput(obj, 0);
		}

	}
		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return 	
			"Takes an object and a parameter point. if the parameter point"+
			"at the index defined in the properties matches the double value"+
			"defined in properties, the object is passed."+
			""+
			"";
	}
	
   	public String getModuleName() {
		return "PassIfParameterMatches";
	}
	public String[] getInputTypes(){
		String[] types = {
			"java.lang.Object",
			"ncsa.d2k.modules.core.datatype.parameter.ParameterPoint"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: 
				return "Any Object";
			case 1: 
				return "The ParameterPoint to do the comparison on.";
			case 2: 
				return "";
			default: return "No such input";
		}
	}
	
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Object";
			case 1:
				return "Parameter Point";
			case 2:
				return "";
			default: return "No such input";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {"java.lang.Object"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: 
				return "The same object passed in.";
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
				return "Object";
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
	public double  getParameterValue(){
		return paramValue;
	}
	public void setParameterValue(double d){
		paramValue=d;
	}
	public int getParameterIndex(){
		return paramIndex;
	}
	public void setParameterIndex(int i){
		paramIndex=i;
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
			
					

			

								
	
