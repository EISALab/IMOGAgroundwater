package ncsa.d2k.modules.projects.pgroves.instancebased;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
/**

	Creates a CachedIDWModel.

	@author pgroves
	@date 01/14/04
	*/

public class CachedIDWModelGen extends ModelGeneratorModule
	implements java.io.Serializable{

	//////////////////////
	//d2k Props
	////////////////////

	boolean debug=false;

	/*should d2k be allowed to put this model in
	the "Generated Models" window*/
	boolean makeModelAvailable=false;


	/////////////////////////
	/// other fields
	////////////////////////

	/*the model to let d2k put in the "Generated Models" window*/
	CachedIDWModel model;

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
		ParameterPoint sol=(ParameterPoint)pullInput(1);
		Table distMatrix = (Table)pullInput(2);
		Table distOrder = (Table)pullInput(3);
		model=new CachedIDWModel(et, sol, distMatrix, distOrder);
		model.setDebug(this.debug);
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
				" by the ParameterPoint object passed in.";
	}

   	public String getModuleName() {
		return "CachedIBModelGen";
	}
	public String[] getInputTypes(){
		String[] s= {
			"ncsa.d2k.modules.core.datatype.table.ExampleTable",
			"ncsa.d2k.modules.core.datatype.parameter.ParameterPoint",
			"ncsa.d2k.modules.core.datatype.table.Table",
			"ncsa.d2k.modules.core.datatype.table.Table",
			};
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
				return "The ParameterPoint object which contains the parame"+
						"ters or biases for generating the model";
			}
			case(2): {
				return "The DistanceMatrix generated by CalcDistanceMatrix";
			}
			case(3): {
				return "The Nearest Neighbor Orderings output by SortDistances";
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
			case(2): {
				return "DistanceMatrix";
			}
			case(3): {
				return "Nearest Neighbor Orderings";
			}
			default:{
				return "No such input.";
			}
		}
	}
	public String[] getOutputTypes(){
		String[] s={
			"ncsa.d2k.modules.projects.pgroves.instancebased."+
			"CachedIDWModel"};
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






