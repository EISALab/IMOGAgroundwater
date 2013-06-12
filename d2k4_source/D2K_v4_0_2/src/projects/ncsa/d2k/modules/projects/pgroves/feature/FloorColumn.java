package ncsa.d2k.modules.projects.pgroves.feature;


import ncsa.d2k.core.modules.*;

import ncsa.d2k.modules.core.datatype.table.*;



/**
	sets any double in a column in a table below the specified value
	to be the specified value.
	
	@author pgroves
	@date 05/19/04
*/

public class FloorColumn extends ComputeModule 
	implements java.io.Serializable {

	//////////////////////
	//d2k Props
	////////////////////

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
		MutableTable tbl = (MutableTable)pullInput(0);
		int columnIndex = ((Integer)pullInput(1)).intValue();
		double floorVal = ((Double)pullInput(2)).doubleValue();

		int numRows = tbl.getNumRows();
		double val;
		for(int i = 0; i < numRows; i++){
			val = tbl.getDouble(i, columnIndex);
			if(val < floorVal){
				tbl.setDouble(floorVal, i, columnIndex);
			}
		}
		pushOutput(tbl, 0);
	}
		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return 	
			"Sets all values in a column below the input threshold"+
			" to be the threshold."+
			""+
			"";
	}
	
   	public String getModuleName() {
		return "FloorColumn";
	}
	public String[] getInputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.table.MutableTable",
			"java.lang.Integer",
			"java.lang.Double"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: 
				return "A mutable table. Only the specified column will be " +
				" altered.";
			case 1: 
				return "The column index to perform the thresholding on.";
			case 2: 
				return "The threshold value. Any cell in the column containing" +
				" a value lower than the threshold will be set to the " +
				"threshold";
			default: return "No such input";
		}
	}
	
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Table";
			case 1:
				return "Column Index";
			case 2:
				return "Floor Threshold";
			default: return "No such input";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.table.MutableTable"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: 
				return "The same table that was passed in, altered in place.";
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
				return "Altered Table";
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
			
					

			

								
	
