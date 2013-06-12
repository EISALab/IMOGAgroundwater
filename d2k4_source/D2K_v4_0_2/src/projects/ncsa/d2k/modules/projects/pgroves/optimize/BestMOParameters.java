package ncsa.d2k.modules.projects.pgroves.optimize;

import java.util.HashMap;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.impl.*;

/**
	Takes an array of parameter points, and the name of
	an objective parameter. for every unique value of the
	objective parameter, returns the parameter that is
	the best in terms of the other objective parameter (name
	also passed in). returns all such points in a single parameter
	point array.


	@author pgroves
	@date 02/13/04
	*/

public class BestMOParameters extends ComputeModule 
	implements java.io.Serializable {

	//////////////////////
	//d2k Props
	////////////////////
	
	boolean debug=false;	
	boolean isMaximizing = false;

	boolean compareByAbsoluteValue = true;
		
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
		Object[] rawpps = (Object[])pullInput(0);
		
		//put these in a proper ParameterPoint array
		ParameterPoint[] pps = new ParameterPoint[rawpps.length];
		System.arraycopy(rawpps, 0, pps, 0, rawpps.length);

		/*for(int i = 0; i < rawpps.length; i++){
			pps[i] = (ParameterPoint)rawpps[i];
		}*/

		//the parameter to find the unique values for
		String uniqueParam = (String)pullInput(1);
		
		//the param that will determine 'best' for each
		//uniqueParam
		String objParam = (String)pullInput(2);

		int uniqueParamIdx, objParamIdx, i, j;
		uniqueParamIdx = pps[0].getParameterIndex(uniqueParam);
		objParamIdx = pps[0].getParameterIndex(objParam);

		//we'll keep the unique values in a hashmap
		//the uniqueparam double value will be the key, the param w/ best
		///objective value will be the key's value

		HashMap map = new HashMap();
		Double key;
		double value;
		double prevBest;
		boolean isBetter = false;
		for(i = 0; i < pps.length; i++){
			key = new Double(pps[i].getValue(uniqueParamIdx));
			value = pps[i].getValue(objParamIdx);
			if(compareByAbsoluteValue && (value < 0)){
				value *= -1;
			}

			if(map.containsKey(key)){
				prevBest = ((ParameterPoint)map.get(key)).getValue(objParamIdx);
				isBetter = ((prevBest > value) && !isMaximizing) ||
					((prevBest < value) && isMaximizing);
				if(isBetter){
					map.put(key, pps[i]);
				}
			}else{
				//no previous best, just add whatever value we have
				map.put(key, pps[i]);
			}
		}
		//the parameters we want are in the map, simply pull them out
		Object[] retArray = map.values().toArray();
		if(debug){
			for(i = 0; i < retArray.length; i++){
				System.out.print(uniqueParam + ": ");
				System.out.print(((ParameterPoint)retArray[i]).getValue(
					uniqueParamIdx));
				System.out.print(", " + objParam + ": ");
				System.out.print(((ParameterPoint)retArray[i]).getValue(
					objParamIdx));
				System.out.println();
			}
		}
		pushOutput(retArray, 0);
	}
		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return 	
			"Takes some ParameterPoints, and the name of two Parameters."+
			"For every unique value of the first parameter, the best parameter"+
			"point in terms of the second parameter is put into an array,"+
			"which is returned. 'Best' is determined by maximizing or min"+
			"imizing flag";
	}
	
   	public String getModuleName() {
		return "BestMOParameters";
	}
	public String[] getInputTypes(){
		String[] types = {
			"[Lncsa.d2k.modules.core.datatype.parameter.ParameterPoint",
			"java.lang.String",
			"java.lang.String"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: 
				return "An array of ParameterPoints (from the same ParameterSpace";
			case 1: 
				return "The name of one of the parameters. Will be used to"+
					"determine uniqueness of parameters";
			case 2: 
				return "The name of another parameter. Will determine if one"+
				" parameter is better than the (when they are considered "+
				"equal by the uniqueParamIdentifier";
			default: return "No such input";
		}
	}
	
	public String getInputName(int index) {
		switch(index) {
			case 1:
				return "Unique Param Identifier";
			case 2:
				return "Objective Param Identifier";
			case 0:
				return "ParameterPoint Set";
			default: return "No such input";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {
			"[Lncsa.d2k.modules.core.datatype.parameter.ParameterPoint"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: 
				return "Exactly as many ParameterPoints as there were unique"+
				" values in the UniqueParamIdentifier Parameter. Each is bet"+
				"ter than all other parameters with the same UniqueParameter"+
				" when compared by their respective Objective Parameters.";
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
				return "Best ParameterPoint Set";
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
	public boolean getCompareByAbsoluteValue(){
		return compareByAbsoluteValue;
	}
	public void setCompareByAbsoluteValue(boolean b){
		compareByAbsoluteValue = b;
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
			
					

			

								
	
