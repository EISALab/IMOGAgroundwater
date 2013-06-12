package ncsa.d2k.modules.projects.pgroves.bp;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;


/**
	Adds a blank input feature to an example table. The name
	and default value for the column can be set in properties.
	Is meant to be used to add a time (or z) input to an
	ImagePredictionTable

	@author pgroves
	*/

public class AddInputFeature extends DataPrepModule 
	implements java.io.Serializable {

	//////////////////////
	//d2k Props
	////////////////////
	
	String addedInputLabel = "New Input";

	double addedInputDefaultValue = 0.0;
	
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

		int numRows = et.getNumRows();
		double[] newVals = new double[numRows];
		for(int i = 0; i < numRows; i++){
			newVals[i] = addedInputDefaultValue;
		}

		DoubleColumn dc = new DoubleColumn(newVals);
		dc.setLabel(addedInputLabel);
		et.addColumn(dc);
		
		int[] oldInputs = et.getInputFeatures();
		int[] newInputs = new int[oldInputs.length + 1];

		for(int i = 0; i < oldInputs.length; i++){
			newInputs[i] = oldInputs[i];
		}
		newInputs[newInputs.length - 1] = et.getNumColumns() - 1;
		et.setInputFeatures(newInputs);

		pushOutput(et, 0);
		
	}
		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return 	
			"Attaches a new input column to the end of an example table."+
			"The columns name and the double value that will be set in "+
			" each row can be set in properties."+
			"";
	}
	
   	public String getModuleName() {
		return "AddInputFeature";
	}
	public String[] getInputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.ExampleTable"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: 
				return "A (mutable) example table.";
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
				return "ExampleTable";
			case 1:
				return "";
			case 2:
				return "";
			default: return "No such input";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.ExampleTable"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: 
				return "The same example table that was pulled in, but with one"+
				" more column added at the end. The new column is added to the" +
				" set of input features.";
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
				return "Updated Example Table";
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
	public double  getAddedInputDefaultValue(){
		return addedInputDefaultValue;
	}
	public void setAddedInputDefaultValue (double d){
		addedInputDefaultValue=d;
	}
	public String getAddedInputLabel(){
		return addedInputLabel;
	}
	public void setAddedInputLabel(String s){
		addedInputLabel = s;
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
			
					

			

								
	
