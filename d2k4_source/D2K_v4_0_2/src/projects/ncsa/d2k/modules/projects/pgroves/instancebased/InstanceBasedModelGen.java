package ncsa.d2k.modules.projects.pgroves.instancebased;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
/**

	MultiThreaded Instance Based with Inverse Distance Weighting
	Model Generator. 

	@author pgroves
	@date 091102
	*/

public class InstanceBasedModelGen extends ModelGeneratorModule
	implements java.io.Serializable{

	//////////////////////
	//d2k Props
	////////////////////

	boolean debug=false;

	/*should d2k be allowed to put this model in
	the "Generated Models" window*/
	boolean makeModelAvailable=false;

	int threadCount=1;


	boolean scaleInputs=true;
	/////////////////////////
	/// other fields
	////////////////////////

	/*the model to let d2k put in the "Generated Models" window*/
	InverseDistanceWeightingModel model;

	//////////////////////////
	///d2k control methods
	///////////////////////

	public boolean isReady(){
		return super.isReady();
	}
	public void beginExecution(){
		wipeFields();
		return;
	}

	private void wipeFields(){
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
		ExampleTable et=(ExampleTable)pullInput(0);
		ParameterPoint sol = (ParameterPoint)pullInput(1);
		model=new InverseDistanceWeightingModel(et, sol);
		model.setThreadCount(threadCount);
		model.setDebug(this.debug);
		model.setScaleInputs(this.scaleInputs);
		pushOutput(model, 0);
		wipeFields();

	}

	public ModelModule getModel() {
    	return model;
  	}


	////////////////////////////////
	/// ModelGen's D2K Info Methods
	////////////////////////////////


	public String getModuleInfo(){
		return "Makes a model that will predict based on its "+
				"distance to N nearest neighbors (by Euclidean dist"+
				"ance) and the inverse weighting power specified"+
				" by the solution object passed in. The model will use"+
				" n threads during prediction, where n is specified by the"+
				" property threadCount";
	}

   	public String getModuleName() {
		return "IBModelGen";
	}
	public String[] getInputTypes(){
		String[] s= {"ncsa.d2k.modules.core.datatype.table.ExampleTable",
			"ncsa.d2k.modules.core.datatype.parameter.ParameterPoint"};
		return s;
	}

	public String getInputInfo(int index){
		switch (index){
			case(0): {
				return "The training data set. Only row indices ind"+
						"icated by the trainingSet will be considered"+
						" during model generation";
			}
			case(1): {
				return "The Solution object which contains the parame"+
						"ters or biases for generating the model";
			}

			default:{
				return "No such input.";
			}
		}
	}

	public String getInputName(int index) {
		switch (index){
			case(0): {
				return "Training Data";
			}
			case(1): {
				return "Parameters";
			}

			default:{
				return "No such input.";
			}
		}
	}
	public String[] getOutputTypes(){
		String[] s={"ncsa.d2k.modules.projects.pgroves.instancebased."+
		"InverseDistanceWeightingModel"};
		return s;
	}

	public String getOutputInfo(int index){
		switch (index){
			case(0): {
				return "The model that was produced";
			}
			default:{
				return "No such output.";
			}
		}
	}
	public String getOutputName(int index) {
		switch (index){
			case(0): {
				return "The Model";
			}
			default:{
				return "No such output.";
			}
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
	public boolean getMakeModelAvailable(){
		return makeModelAvailable;
	}
	public void setMakeModelAvailable(boolean b){
		makeModelAvailable=b;
	}
	public int getThreadCount(){
		return threadCount;
	}
	public void setThreadCount(int i){
		threadCount=i;
	}
	public boolean getScaleInputs(){
		return scaleInputs;
	}
	public void setScaleInputs(boolean b){
		scaleInputs=b;
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
	*/
}







