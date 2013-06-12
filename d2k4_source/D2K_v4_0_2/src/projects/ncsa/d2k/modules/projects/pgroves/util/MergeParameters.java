package ncsa.d2k.modules.projects.pgroves.util;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.impl.*;


/**
	takes two generic ParameterPoints and merges them into
	a new ParameterPointImpl


	@author pgroves
	@date 02/03/04
	*/

public class MergeParameters extends DataPrepModule 
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
	/**
		does it
	*/
	public void doit() throws Exception{
		ParameterPoint pp1 = (ParameterPoint)pullInput(0);
		ParameterPoint pp2 = (ParameterPoint)pullInput(1);
		
		ParameterPoint[] origPP = new ParameterPoint[2];
		origPP[0] = pp1;
		origPP[1] = pp2;

		ParameterPoint newPP = mergeParameters(origPP);
		if(debug){
			System.out.println(getAlias()+": Made Param: ");
			System.out.println(newPP);
		}
		
		pushOutput(newPP, 0);
	}
		
		
	/**
		condenses an array of ParameterPoint's into a single ParameterPoint.
		Does not use the subspace mechanism in the ParameterPoint 
		interface
		
		@param pps an array of ParameterPoints
		@return a ParameterPointImpl that contains all the parameters of
		the passed in ParameterPoints
		
		*/	
	public static ParameterPointImpl mergeParameters(ParameterPoint[] pps){
		int totalNumParams = 0;
		int i, j, k;

		for(i = 0; i < pps.length; i++){
			totalNumParams += pps[i].getNumParameters();
		}
		
		double[] values = new double[totalNumParams];
		String[] names = new String[totalNumParams];
		
		k=0;
		for(i = 0; i < pps.length; i++){
			for(j = 0; j < pps[i].getNumParameters(); j++){
				values[k] = pps[i].getValue(j);
				names[k] = pps[i].getName(j);
				k++;
			}
		}
		
		return (ParameterPointImpl)ParameterPointImpl.
			getParameterPoint(names, values);
	}
		
			
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return 	
			"Merges two ParameterPoints into a single new one."+
			" Does not use ParameterPoint subspaces."+
			""+
			"";
	}
	
   	public String getModuleName() {
		return "MergeParameters";
	}
	public String[] getInputTypes(){
		String[] types = {
			"ncsa.d2k.modules.datatype.parameter.ParameterPoint",
			"ncsa.d2k.modules.datatype.parameter.ParameterPoint"
			};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: 
				return "The first ParameterPoint";
			case 1: 
				return "The second ParamterPoint";
			case 2: 
				return "";
			default: return "No such input";
		}
	}
	
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "ParameterPoint 1";
			case 1:
				return "ParameterPoint 2";
			default: return "No such input";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {
			"ncsa.d2k.modules.datatype.parameter.ParameterPoint"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: 
				return "A new ParameterPoint, where the first params are"+
				" from ParameterPoint 1, followed by those from ParameterPoint 2";
			default: return "No such output";
		}
	}
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Merged ParameterPoint";
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
			
					

			

								
	
