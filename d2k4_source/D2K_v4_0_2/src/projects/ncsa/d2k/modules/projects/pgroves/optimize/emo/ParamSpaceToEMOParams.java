package ncsa.d2k.modules.projects.pgroves.optimize.emo;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

import ncsa.d2k.modules.core.optimize.ga.emo.*;



/**
 * Takes a generic parameter space and creates a
 * new EMOParameters object populated with decision
 * variables based on the parameters in the input
 * ParameterSpace. The ga control parameters (mutation rate,
 * crossover, etc) will be left blank.


	@author pgroves
	@date 04/15/04
	*/

public class ParamSpaceToEMOParams extends DataPrepModule 
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
		ParameterSpace ps = (ParameterSpace)pullInput(0);
		Parameters eps = convertSpaceToEMOParameters(ps);
		pushOutput(eps, 0);
		
	}

	/**
	 * does the work of the module. creates a new EMO Parameters
	 * object and fills in it's decision variables with the parameters
	 * from a ParameterSpace
	 */
	public static Parameters convertSpaceToEMOParameters(ParameterSpace ps){

		Parameters eps = new Parameters();

		double min, max, precision;
		int stringLength;
		String name;
		DecisionVariable var;
		
		int numParams = ps.getNumParameters();
		for(int i = 0; i < numParams; i++){
			name = ps.getName(i);
			min = ps.getMinValue(i);
			max = ps.getMaxValue(i);
			precision = (max - min) / (ps.getResolution(i) - 1);
			stringLength = DecisionVariable.calculateStringLength(min, 
					max, precision);
			var = new DecisionVariable(name, min, max, precision, stringLength);
			eps.addDecisionVariable(var);
		}
		return eps;
	}
		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return 	
			"Converts a ParameterSpace into decision variables of a "+
			"new EMO Parameters object. For use in converting generic "+
			"optimization problems into a form that will work with the"+
			" EMO user interface, including the D2KSL version of EMO." +
			" Note that only the decision variables of the EMO Parameters" +
			" object will be intiallized. All other fields must be filled in" +
			" afterwards.";
	}
	
   	public String getModuleName() {
		return "ParamSpaceToEMOParams";
	}
	public String[] getInputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.parameters.ParameterSpace"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: 
				return "A parameter space";
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
				return "Generic Parameter Space";
			case 1:
				return "";
			case 2:
				return "";
			default: return "No such input";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.optimize.ga.emo.Parameters"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: 
				return "A parameter space definition object used by EMO";
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
				return "EMO Parameter Space";
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
			
					

			

								
	
