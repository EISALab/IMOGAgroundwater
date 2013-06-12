package ncsa.d2k.modules.projects.pgroves.optimize;


import ncsa.d2k.core.modules.*;
//import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.impl.*;
//import ncsa.d2k.modules.core.optimize.util.*;

/**
 * Sees if a 
	@author pgroves
	*/

public class CheckForAllFalse extends ComputeModule 
	{

	//////////////////////
	//d2k Props
	////////////////////
	
	boolean debug = false;		

	boolean invalidIfOnlyOne = true;

	boolean checkForAllTrue = false;
	/*double dummyParameterValue = 0;
	int numDummyParams = 1;
	*/
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
		super.endExecution();
	}
	public void beginExecution(){
		super.beginExecution();
	}
	
	/////////////////////
	//work methods
	////////////////////
	/*
		does it
	*/
	public void doit() throws Exception{

		/*ExampleTable et=(ExampleTable)pullInput(0);

		if(et.getInputFeatures().length==0){
			pushOutput(new Double(0.0), 0);
		}else{
			pushOutput(et, 1);
		}*/

		double acceptVal = 0;
		if(checkForAllTrue)
			acceptVal = 1;
		boolean oneFound = false;
		ParameterPoint pp = (ParameterPoint)pullInput(0);
		for(int i = 0; i < pp.getNumParameters(); i++){
			if(pp.getValue(i) != acceptVal){
				if(oneFound || !invalidIfOnlyOne){
					pushOutput(pp, 0);
					return;
				}else{
					oneFound = true;
				}
			}
		}
		pushOutput(pp, 1);
		/*String[] labels = new String[this.numDummyParams];
		double[] vals = new double[this.numDummyParams];

		for(int i = 0; i < this.numDummyParams; i++){
			labels[i] = "AllFalseError:" + i;
			vals[i] = this.dummyParameterValue;
		}
		pp = ParameterPointImpl.getParameterPoint(labels, vals);
		pushOutput(pp, 1);
		*/
		
	}
		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return "Checks to see if a set of Binary Parameters is all false." +
		"Pushes the param out of the first output if it passes, out the " +
		" second if not. If 'checkForAllTrue' is set (to true), then " +
		" the test is to see if all of the bits are true";
		/*
		" If not, the unchanged ParameterPoint is passed along. If so, " +
		" a set of dummy parameters is passed out of the other input to " +
		" represent a really bad score. The number of dummy parameters this " +
		" object contains and what the bad score value is are set in props.";
		*/
	}
	
   	public String getModuleName() {
		return "CheckForAllFalse";
	}
	public String[] getInputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.parameter.ParameterPoint"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: return "A set of Binary Parameters";
			default: return "No such input";
		}
	}
	
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Parameter to be Checked";
			default: return "NO SUCH INPUT!";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.parameter.ParameterPoint",
			"ncsa.d2k.modules.core.datatype.parameter.ParameterPoint"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: return "The parameterpoint that was passed in. Only" +
			" pushed here if it passes the test.";
			case 1: return "The parameterpoint that was passed in. Only" +
			" pushed here if it passes the test.";
			/*case 1: return "The parameterpoint of dummy values. Only pushed" +
			" here if the input paramter point fails the test.";
			*/
			default: return "No such output";
		}
	}
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Valid ParameterPoint";
			case 1:
				return "Invalid ParameterPoint";
			default: return "NO SUCH OUTPUT!";
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
	public boolean getInvalidIfOnlyOne(){
		return invalidIfOnlyOne;
	}
	public void setInvalidIfOnlyOne(boolean b){
		invalidIfOnlyOne=b;
	}
	public boolean getCheckForAllTrueInstead(){
		return checkForAllTrue;
	}
	public void setCheckForAllTrueInstead(boolean b){
		checkForAllTrue = b;
	}

	/*public double getDummyParameterValue(){
		return dummyParameterValue;
	}
	public void setDummyParameterValue(double d){
		dummyParameterValue=d;
	}
	public int getNumDummyParams(){
		return numDummyParams;
	}
	public void setNumDummyParams(int i){
		numDummyParams=i;
	}

*/
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
			
					

			

								
	
