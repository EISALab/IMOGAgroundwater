package ncsa.d2k.modules.projects.pgroves.geostat;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.datatype.parameter.*;


/**
	Wraps the BivariateModel.

	@author pgroves
	@date 03/17/04
	*/

public class VariogramModelGenerator extends ModelGeneratorModule
	implements java.io.Serializable {

	//////////////////////
	//d2k Props
	////////////////////
	
	/*should d2k be allowed to put this model in
	the "Generated Models" window*/
	boolean makeModelAvailable=false;	
	
	boolean debug=false;		
	/////////////////////////
	/// other fields
	////////////////////////

	BivariateModel model;

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
		if(!makeModelAvailable)
			model=null;
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
		
		ExampleTable et=(ExampleTable)pullInput(0);
		ParameterPoint params = (ParameterPoint)pullInput(1);
		model=new BivariateModel(et, params);
		model.setDebug(this.debug);
		pushOutput(model, 0);
		wipeFields();

	}

	public ModelModule getModel() {
    	return model;
  	}
		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return 	
			"The model currently returned is likely to change."+
			""+
			""+
			"";
	}
	
   	public String getModuleName() {
		return "";
	}
	public String[] getInputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.table.ExampleTable",
			"ncsa.d2k.modules.core.datatype.parameter.ParameterPoint"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: 
				return "The training data set. Only used if there are parameters"+
				" that are unspecified and therefore need to be optimized.";
			case 1: 
				return "The parameters for the Model. Any parameters that "+
				"are 'not a number' (NaN) will be optimized. Not currently" +
				" implemented.";
			case 2: 
				return "";
			default: return "No such input";
		}
	}
	
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Training Data";
			case 1:
				return "Model Parameters";
			case 2:
				return "";
			default: return "No such input";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {
			"ncsa.d2k.modules.projects.pgroves.geostat.BivariateModel"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: 
				return " A bivariate model predicts a single output base"+
				" on a single input. This modelgenerator produces one that "+
				"is a linear combination of a constant, spherical function, "+
				" exponential model, gaussian model, and power model. It is "+
				" intended to be used to model a semivariogram where a "+
				"lag distance is used to predict a covariance like function, "+
				"called a semivariogram.";
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
				return "VariogramModel";
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
			
					

			

								
	
