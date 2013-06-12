package ncsa.d2k.modules.projects.pgroves.optimize.emo;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.impl.*;

import ncsa.d2k.modules.core.optimize.util.*;
import ncsa.d2k.modules.core.optimize.ga.*;


/**
 * removes the contents of all individuals of a population
 * into ParameterPoints and returns them all as an aray.


	@author pgroves
	@date 04/15/04
	*/

public class PopulationToParamArray extends DataPrepModule 
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
		does it.
	*/
	public void doit() throws Exception{
		if(debug){
			System.out.println(getAlias()+":Firing");
		}
		Population pop = (Population)pullInput(0);
		ParameterPoint[] ppa = convertPopulationToParameterPoints(pop);
		pushOutput(ppa, 0);
	}
		
	public static ParameterPoint[] convertPopulationToParameterPoints(
			Population pop){

		int numPoints = pop.size();
		ParameterPoint[] ppa = new ParameterPoint[numPoints];
		int j, i;
		int numParams = pop.getTraits().length;
		Solution sol;
		
		String[] names = new String[numParams];
		for(i = 0; i < numParams; i++){
			names[i] = pop.getTraits()[i].getName();
		}
		double[] vals;
		for(i = 0; i < numPoints; i++){
			sol = pop.getMember(i);
			vals = new double[numParams];
			for(j = 0; j < numParams; j++){
				vals[j] = sol.getDoubleParameter(j);
			}
			ppa[i] = ParameterPointImpl.getParameterPoint(names, vals);
		}
		return ppa;
	}
				
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return 	
			"Returns the members of a GA population as an array of "+
			" the more genereric ParameterPoint class. Allows a GA"+
			" to be used with an evaluation scheme that uses the "+
			" ParameterPoint interface.";
	}
	
   	public String getModuleName() {
		return "PopulationToParamArray";
	}
	public String[] getInputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.optimize.ga.Population"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: 
				return "A population with it's members initialized.";
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
				return "Source Population";
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
				return "An array of parameter points that contain the same infor"+
					"mation as the input Population's members.";
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
				return "ParameterPoint Array";
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
			
					

			

								
	
