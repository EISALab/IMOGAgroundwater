package ncsa.d2k.modules.projects.pgroves.feature;


import ncsa.d2k.core.modules.*;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.impl.*;


/**
	takes in an array of parameterpoints of binary individuals.
	on the first run, it sets the first n individuals to have 
	only one bit off. also, one pp with all on is set. After the
	first run, the array is passed on unchanged.
	@author pgroves
*/

public class InitPopOneOff extends ComputeModule 
	implements java.io.Serializable {

	//////////////////////
	//d2k Props
	////////////////////
	
	boolean debug=false;		
	/////////////////////////
	/// other fields
	////////////////////////
	boolean firstRun = true;
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
		firstRun = true;
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
		ParameterPoint[] ppa = (ParameterPoint[])pullInput(0);
		if(this.firstRun){
			int numPoints = ppa.length;
			int numParams = ppa[0].getNumParameters();
			
			String[] labels = new String[numParams];
			double[] vals;
			int pointIdx = 0;
			for(int j = 0; j < numParams; j++){
				labels[j] = ppa[0].getName(j);
			}
			int i, j;
			for(i = 0; (i < numParams) && (i < numPoints); i++){
				vals = new double[numParams];
				for(j = 0; j < numParams; j++){
					if(i != j){
						vals[j] = 1.0;
					}
				}
				ppa[i] = ParameterPointImpl.getParameterPoint(labels, vals);
			}

			//now one with all true bits, if there's room
			if(i < numPoints){
				vals = new double[numParams];
				for(j = 0; j < numParams; j++){
					vals[j] = 1.0;
				}
				ppa[i] = ParameterPointImpl.getParameterPoint(labels, vals);
			}
			this.firstRun = false;
		}
		pushOutput(ppa, 0);
	}
		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return 	
			"On the first run only, replaces the first n parameter points"+
			" in an array with points that have all but one bit set to true."+
			"The n+1'th point is set to all true bits."+
			"";
	}
	
   	public String getModuleName() {
		return "InitPopOneOff";
	}
	public String[] getInputTypes(){
		String[] types = {
			"[Lncsa.d2k.modules.core.datatype.parameter.ParameterPoint"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: 
				return "An array of binary parameterpoints";
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
				return "ParameterPoint Array";
			case 1:
				return "";
			case 2:
				return "";
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
				return "The same array of points, with some of them replaced" +
				" on the first run. Otherwise, the same array.";
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
				return "Modified ParameterPoints";
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
			
					

			

								
	
