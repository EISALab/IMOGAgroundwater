package ncsa.d2k.modules.projects.pgroves.feature;


import ncsa.d2k.core.modules.*;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.transformations.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;



/**
	A simple, gui-less way to apply a scaling transformation to
	an example table.

	@author pgroves
*/

public class MakeScalingTransform extends DataPrepModule 
	implements java.io.Serializable {

	//////////////////////
	//d2k Props
	////////////////////
	
	boolean transformInputs = true;
	boolean transformOutputs = false;

	double scaleToMin = 0.0;
	double scaleToMax = 1.0;
	
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
		ExampleTable et = (ExampleTable)pullInput(0);
		int numTransCols = 0;
		if(this.transformInputs){
			numTransCols += et.getNumInputFeatures();
		}
		if(this.transformOutputs){
			numTransCols += et.getNumOutputFeatures();
		}

		double[] mins = new double[numTransCols];
		double[] maxs = new double[numTransCols];
		int[] transCols = new int[numTransCols];

		int transIndex = 0;
		
		if(this.transformInputs){
			for(int i = 0; i < et.getNumInputFeatures(); i++){
				mins[transIndex] = this.scaleToMin;
				maxs[transIndex] = this.scaleToMax;
				transCols[transIndex] = et.getInputFeatures()[i];
				transIndex++;
			}
		}
		if(this.transformOutputs){
			for(int i = 0; i < et.getNumOutputFeatures(); i++){
				mins[transIndex] = this.scaleToMin;
				maxs[transIndex] = this.scaleToMax;
				transCols[transIndex] = et.getOutputFeatures()[i];
				transIndex++;
			}
		}

		ScalingTransformation scaler = new ScalingTransformation(
			transCols, mins, maxs, et);
		
		pushOutput(scaler, 0);
		
	}
		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return 	
			"Scales the inputs, outputs, or both of an Example Table "+
			" to a common range. No other columns are transformed. "+
			" The transformation produced is reversible."+
			"";
	}
	
   	public String getModuleName() {
		return "MakeScalingTransform";
	}
	public String[] getInputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: 
				return "An ExampleTable to use to define the column indices to" +
				" tranform (based on it's inputs/outputs meta-data)." +
				" The current range (max and min) of those columns to be tran"+
				"formed will also be based on this table.";
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
				return "Source Data";
			case 1:
				return "";
			case 2:
				return "";
			default: return "No such input";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {
		"ncsa.d2k.modules.core.datatype.transformations.ScalingTransformation"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: 
				return "A reversible transformation object.";
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
				return "Scaling Transformation";
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
		transformInputs = b;
	}
	public boolean getTransformOutputs(){
		return transformOutputs;
	}
	public void setTransformOutputs(boolean b){
		transformOutputs = b;
	}
	public double getScaleToMin(){
		return scaleToMin;
	}
	public void setScaleToMin(double d){
		scaleToMin = d;
	}
	public double getScaleToMax(){
		return scaleToMax;
	}
	public void setScaleToMax(double d){
		scaleToMax = d;
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
			
					

			

								
	
