package ncsa.d2k.modules.projects.pgroves.util;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.impl.*;


/**
	takes a ParameterPoint and splits it into two
	ParameterPointImpls. does not use the split method
	in ParameterPoint interface


	@author pgroves
	@date 02/18/04
	*/

public class SplitParameter extends DataPrepModule 
	implements java.io.Serializable {

	//////////////////////
	//d2k Props
	////////////////////
	
	boolean debug=false;		

	int splitIndex = 1;
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
		ParameterPoint pp1 = (ParameterPoint)pullInput(0);
		
		ParameterPointImpl[] pps = splitParameter(pp1, splitIndex);
		
		pushOutput(pps[0], 0);
		pushOutput(pps[1], 1);
	}
		
	/**
		splits a parameter point into two new ParameterPointImpl's
		at a  specified index.
		
		@param pp A parameterpoint to split
		@param splitIndex The index to split on. The parameter
			at the splitIndex of the original ParameterPoint will
			be the first parameter of the second of the new 
			ParameterPointImpls
		@return an array of two ParameterPointImpl's
	*/		
	public static ParameterPointImpl[] splitParameter(
		ParameterPoint pp, int splitIndex){
		
		int numToSplit = 2;
		
		int numParams = pp.getNumParameters();
		int[] compNumParams = new int[numToSplit];
		
		compNumParams[0] = splitIndex;
		compNumParams[1] = numParams - splitIndex;		

		ParameterPointImpl[] ppis = new ParameterPointImpl[numToSplit];

		int i, j, k;
		//use as index into original parampoint
		k = 0;
		for(i = 0; i < numToSplit; i++){
			String[] names = new String[compNumParams[i]];
			double[] vals = new double[compNumParams[i]];
			for(j = 0; j < compNumParams[i]; j++){
				names[j] = pp.getName(k);
				vals[j] = pp.getValue(k);
				k++;
			}
			ppis[i] = (ParameterPointImpl)ParameterPointImpl.getParameterPoint(
				names, vals);
		}
		return ppis;
	}
			
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return 	
			"Splits a ParameterPoint into two ParameterPointImpl's."+
			" Does not use ParameterPoint subspaces. The split index"+
			" is the index into the original ParameterPoint that will"+
			" become the first parameter of the second of the new PP's.";
	}
	
   	public String getModuleName() {
		return "SplitParameter";
	}
	public String[] getInputTypes(){
		String[] types = {
			"ncsa.d2k.modules.datatype.parameter.ParameterPoint",
			};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: 
				return "The Original ParameterPoint";
			default: return "No such input";
		}
	}
	
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "ParameterPoint";
			default: return "No such input";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {
			"ncsa.d2k.modules.datatype.parameter.impl.ParameterPointImpl",
			"ncsa.d2k.modules.datatype.parameter.impl.ParameterPointImpl"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: 
				return "The first new parameter point. Contains parameters"+
				" from index 0 to splitIndex-1.";
			case 1:
				return " The second new parameter point. Contains parameters"+
				" from index splitIndex to numParameters-1.";	
			default: return "No such output";
		}
	}
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Split Point 1";
			case 1:
				return "Split Point 2";
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
	public int getSplitIndex(){
		return splitIndex;
	}
	public void setSplitIndex(int i){
		splitIndex=i;
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
			
					

			

								
	
