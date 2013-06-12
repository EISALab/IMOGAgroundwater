package ncsa.d2k.modules.projects.pgroves.optimize;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.parameter.*;


/**
 * Given an array of parameters, retrieves the "best" one
 * according to one of the parameters, which is 
 * considered to be an objective.


	@author pgroves
	@date 04/06/04
	*/

public class GetBestParameter extends DataPrepModule 
	implements java.io.Serializable {

	//////////////////////
	//d2k Props
	////////////////////
	
	int objParamIdx = 0;

	int useableObjParamIdx;

	boolean isMaximizing = false;
	
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
		ParameterPoint[] ppa = (ParameterPoint[])pullInput(0);

		if(objParamIdx < 0){
			int np = ppa[0].getNumParameters();
			this.useableObjParamIdx = np + objParamIdx;
		}else{
			this.useableObjParamIdx = objParamIdx;
		}

		int numParams = ppa.length;
		int bestIdx = 0;
		for(int i = 1; i < numParams; i++){
			if(betterThan(ppa, i, bestIdx)){
				bestIdx = i;
			}
		}
		ParameterPoint best = ppa[bestIdx];
		if(debug){
			System.out.println(this.getAlias() + ": Best Param:");
			System.out.println(best);
		}
		pushOutput(best, 0);
			
	}

	/**
	 * returns whether the parameter at idx1 is better than the 
	 * one at idx2. the definition of 'better' is determined
	 * by the isMaximizing and objParamIdx properties of this
	 * module
	 */
	protected boolean betterThan(ParameterPoint[] ppa, int idx1, int idx2){
		double val1 = ppa[idx1].getValue(this.useableObjParamIdx);
		double val2 = ppa[idx2].getValue(this.useableObjParamIdx);
		if(this.isMaximizing){
			return (val1 > val2);
		}else{
			return (val1 < val2);
		}
	}
		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return 	
			"Returns the best ParameterPoint out of an array of ParameterPoints."+
			" The property objParameterIndex indicates which parameter in the "+
			"ParameterPoints should be considered the objective function to "+
			"base the comparisons on. Negative numbers can be used to indicate"+
			" that it is the index from the end of the array of parameters."+
			"The isMaximizing dictates whether a " +
			"greater value is considered better (true) or worse (false).";
	}
	
   	public String getModuleName() {
		return "GetBestParameter";
	}
	public String[] getInputTypes(){
		String[] types = {
			"[Lncsa.d2k.modules.core.datatype.parameter.ParameterPoint:"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: 
				return "An array of ParameterPoints of the same type.";
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
				return "Parameter Point Set";
			case 1:
				return "";
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
				return "The parameter point with the best score at the parameter" +
					" at objParameterIndex";
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
				return "Best Parameter Point";
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
	public boolean getIsMaximizing(){
		return isMaximizing;
	}
	public void setIsMaximizing(boolean b){
		isMaximizing=b;
	}
	public int getObjParameterIndex(){
		return objParamIdx;
	}
	public void setObjParameterIndex(int i){
		objParamIdx = i;
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
			
					

			

								
	
