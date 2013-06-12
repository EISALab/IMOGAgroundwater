package ncsa.d2k.modules.projects.pgroves.feature;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;


/**
	Takes an array of integers, and assigns it to either the 
	test or train set of an ExampleTable.

	
	@author pgroves
	@date 02/05/04
	*/

public class AssignSubset extends DataPrepModule 
	implements java.io.Serializable {

	//////////////////////
	//d2k Props
	////////////////////
	boolean trainVsTest = true;
	
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
		int[] subset = (int[])pullInput(1);

		Table tbl = null;
		if(trainVsTest){
			et.setTrainingSet(subset);
			tbl = et.getTrainTable();
			if(debug){
				System.out.println("\tCreating Train Table.");
			}
		}else{
			et.setTestingSet(subset);
			tbl = et.getTestTable();
			if(debug){
				System.out.println("\tCreating Test Table.");
			}
		}
		if(debug){
			System.out.println("\tNum Rows:" + tbl.getNumRows());
		}

		pushOutput(tbl, 0);
	}
		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return 	
			"Assigns the given subset to an ExampleTable and returns"+
			" it as either a Test or Train Table."+
			""+
			"";
	}
	
   	public String getModuleName() {
		return "AssignSubset";
	}
	public String[] getInputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.table.ExampleTable",
			"[I"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: 
				return "An Example Table";
			case 1: 
				return "A set of indices into the rows of the ExampleTable";
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
				return "Row Subset Indices";
			case 2:
				return "";
			default: return "No such input";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.table.Table"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: 
				return "A Train or Test Table";
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
				return "Subset Table";
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
	public boolean getTrainVsTest(){
		return trainVsTest;
	}
	public void setTrainVsTest(boolean b){
		trainVsTest = b;
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
			
					

			

								
	
