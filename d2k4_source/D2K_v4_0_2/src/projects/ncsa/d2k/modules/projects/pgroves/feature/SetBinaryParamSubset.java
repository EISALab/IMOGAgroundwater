package ncsa.d2k.modules.projects.pgroves.feature;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.impl.*;


/**
 * Sets the binary parameters of a ParameterPoint that are 
 * indexed in an int array to true. 
 *
 * @author pgroves
 * @date 04/18/04
	*/

public class SetBinaryParamSubset extends DataPrepModule 
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
		int[] trueIndices = (int[])pullInput(1);
		int numParams = pp.getNumParameters();
		double[] vals = new double[numParams];
		String[] names = new String[numParams];
		for(int i = 0; i < numParams; i++){
			vals[i] = pp.getValue(i);
			names[i] = pp.getName(i);
		}
		for(int j = 0; j < trueIndices.length; j++){
			vals[trueIndices[j]] = 1.0;
		}
		pp = ParameterPointImpl.getParameterPoint(names, vals);
		pushOutput(pp, 0);
	}
		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return 	
			"Forces the binary parameters at indices that are indicated in"+
			" an array of ints to be 'true'. That is, sets them to '1.0'."+
			""+
			"";
	}
	
   	public String getModuleName() {
		return "SetBinaryParamSubset";
	}
	public String[] getInputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.parameter.ParameterPoint",
			"[I"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: 
				return "A Parameter Point representing binary parameters "+
					" (zeroes and ones)";
			case 1: 
				return "An integer array. The values contained in the array"+
					" will be the indices of parameters in 'Parameters' that "+
					" will be set to true (aka 1.0)";
			case 2: 
				return "";
			default: return "No such input";
		}
	}
	
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Parameters";
			case 1:
				return "'True' Indices";
			case 2:
				return "";
			default: return "No such input";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.parameter.ParameterPoint"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: 
				return "A new instance of a parameter point, with the same "+
					"values as the input Parameters, except for those indices"+
					" in 'True' Indices, which are all set to 1.0";
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
				return "New Parameters";
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
			
					

			

								
	
