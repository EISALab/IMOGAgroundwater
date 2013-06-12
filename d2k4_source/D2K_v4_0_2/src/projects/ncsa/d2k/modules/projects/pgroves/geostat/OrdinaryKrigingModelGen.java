package ncsa.d2k.modules.projects.pgroves.geostat;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.datatype.parameter.*;


/**
	Wraps an ordinary kriging model.

	@author pgroves
	@date 03/25/04
	*/

public class OrdinaryKrigingModelGen extends ModelGeneratorModule
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

	OrdinaryKrigingModel model;

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
		BivariateModel biv = (BivariateModel)pullInput(2);
		
		model=new OrdinaryKrigingModel(et, params, biv);
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
			"Performs Ordinary Kriging as a prediction method. There must"+
			" therefore be exactly two inputs (x location and y location) "+
			" and one output. The input parameter should only contain info"+
			" (namely, a radius) of how to select the nearest neighbors of "+
			"an example being estimated/predicted";
	}
	
   	public String getModuleName() {
		return "OrdinaryKrigingModelGen";
	}
	public String[] getInputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.table.ExampleTable",
			"ncsa.d2k.modules.core.datatype.parameter.ParameterPoint",
			"ncsa.d2k.modules.projects.pgroves.geostat.BivariateModel"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: 
				return "The training data set. ";
			case 1: 
				return "The parameters for the Model. The first parameter should" +
					"be a threshold radius, in the same units of measure as the" +
					" input features, to indicate which training points are in" +
					" the local neighborhood or window of a test case.";
			case 2: 
				return "A variogram in the the form of a bivariate model." +
					" This has the effect that the variogram and any of its " +
					"parameters must be fully determined before being input" +
					" into a kriging model generator.";
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
				return "Theoretical Variogram";
			default: return "No such input";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {
			"ncsa.d2k.modules.projects.pgroves.geostat.OrdinaryKrigingModel"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: 
				return " A kriging model that predicts a single output variable"+
					" using exactly two input variables (geographic coordinates).";
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
				return "OrdinaryKrigingModel";
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
			
					

			

								
	
