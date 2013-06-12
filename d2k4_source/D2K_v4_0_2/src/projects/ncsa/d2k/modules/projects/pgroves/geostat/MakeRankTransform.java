package ncsa.d2k.modules.projects.pgroves.geostat;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;


/**
 * Creates a RankingTransformation object based on 
 * an example table and passes it on (without applying
 * it to the table).


	@author pgroves
	@date 03/30/04
	*/

public class MakeRankTransform extends ComputeModule 
	implements java.io.Serializable {

	//////////////////////
	//d2k Props
	////////////////////
	boolean transformInputs = false;
	boolean transformOutputs = true;
	
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

		ExampleTable et = (ExampleTable)pullInput(0);
		int numTransforms = 0;
		if(this.transformInputs){
			numTransforms += et.getNumInputFeatures();
		}
		if(this.transformOutputs){
			numTransforms += et.getNumOutputFeatures();
		}
		int[] transIndices = new int[numTransforms];
		int k = 0;
		if(this.transformInputs){
			int[] inputs = et.getInputFeatures();
			for(int i = 0; i < inputs.length; i++){
				transIndices[k] = inputs[i];
				k++;
			}
		}
		if(this.transformOutputs){
			int[] outputs = et.getOutputFeatures();
			for(int i = 0; i < outputs.length; i++){
				transIndices[k] = outputs[i];
				k++;
			}
		}
		RankingTransformation trans = new RankingTransformation(et, 
				transIndices);
		pushOutput(trans, 0);
		
	}
		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return 	
			"Generates a reversible transformation that replaces"+
			"values with their ranking. If the table being transformed"+
			" is not the same as the one the transformation is based"+
			" on, linear interpolations between ranks will be returned." +
			" Only the input and/or output features will be transformed." +
			" Whether they are is set in properties.";
	}
	
   	public String getModuleName() {
		return "MakeRankTransform";
	}
	public String[] getInputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: 
				return "An example table with the inputs and outputs set.";
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
				return "Source ExampleTable";
			case 1:
				return "";
			case 2:
				return "";
			default: return "No such input";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {
			"ncsa.d2k.modules.projects.pgroves.geostat.RankingTransformation"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: 
				return "A Ranking Transformation Object. This is a reversible" +
					" transformation. All transforms are based on the values " +
					" in the table passed in here.";
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
				return "Rank Transformation";
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
	public boolean getTransformInputs(){
		return transformInputs;
	}
	public void setTransformInputs(boolean b){
		transformInputs=b;
	}
	public boolean getTransformOutputs(){
		return transformOutputs;
	}
	public void setTransformOutputs(boolean b){
		transformOutputs=b;
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
			
					

			

								
	
