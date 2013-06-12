package ncsa.d2k.modules.projects.pgroves.feature;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.parameter.impl.*;

/**
	Takes in an exampletable and creates a feature selection search space
	over the input features
	
	@author pgroves
	@date 02/03/04
	*/

public class GenFeatureSpace extends DataPrepModule 
	{

	//////////////////////
	//d2k Props
	////////////////////
	

	boolean debug=true;		
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
		ExampleTable et = (ExampleTable)pullInput(0);
		int[] features = et.getInputFeatures();
		int numFeatures = features.length;

		ParameterSpaceImpl pSpace = new ParameterSpaceImpl();
		
		String[] names = new String[numFeatures];
		double[] maxs = new double[numFeatures];
		double[] mins = new double[numFeatures];
		double[] defaults = new double[numFeatures];
		int[] ress = new int[numFeatures];
		int[] types = new int[numFeatures];

		for(int i=0; i<numFeatures; i++){
			names[i] = et.getColumnLabel(features[i]);
			maxs[i] = 1.0;
			mins[i] = 0.0;
			defaults[i] = 1.0;
			ress[i] = 2;
			types[i] = ColumnTypes.BOOLEAN;
		}
		
		pSpace.createFromData(names, mins, maxs, defaults, ress, types);
		
		pushOutput(pSpace, 0);
	}
		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return "Generates a search space over the input features of an "+
		"ExampleTable. (The space states that each feature can be used "+
		"(true) or unused (false)).";
	}
	
   	public String getModuleName() {
		return "GenFeatureSelectionSpace";
	}
	public String[] getInputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: return "";
			default: return "No such input";
		}
	}
	
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Table w/ Input Features";
			default: return "NO SUCH INPUT!";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {
		"ncsa.d2k.modules.core.datatype.parameter.impl.ParameterSpaceImpl"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: return "A ParameterSpace where every parameter is a boolean"+
			" value indicating whether to use (true) or not use (false) a "+
			"feature of the ExampleTable.";
			default: return "No such output";
		}
	}
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Feature Space";
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
			
					

			

								
	
