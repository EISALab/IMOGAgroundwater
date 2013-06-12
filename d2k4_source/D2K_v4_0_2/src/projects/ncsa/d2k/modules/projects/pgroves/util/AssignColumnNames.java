package ncsa.d2k.modules.projects.pgroves.util;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;


/**
	Assigns an array of strings to the column labels of a table.

	@author pgroves
	@date 04/07/04
	*/

public class AssignColumnNames extends DataPrepModule 
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
		String[] lbls = (String[])pullInput(1);

		int numCols = 0;
		
		if(lbls.length < tbl.getNumColumns()){
			numCols = lbls.length;
		}else{
			numCols = tbl.getNumColumns();
		}

		for(int i = 0; i < numCols; i++){
			if(debug){
				System.out.print("Renaming Col " + i + " from '" +
						tbl.getColumnLabel(i) + "' to '" + lbls[i] + "'");
			}
			tbl.setColumnLabel(lbls[i], i);
		}
		pushOutput(tbl, 0);

	}
		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return 	
			"Renames the columns of a mutable table to the strings in a "+
			"String[]. If the number of strings is not equal to the number"+
			" of columns, as many are renamed as possible."+
			"";
	}
	
   	public String getModuleName() {
		return "AssignColumnNames";
	}
	public String[] getInputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.table.MutableTable",
			"[Ljava.lang.String:"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0:
				return "Any Mutable Table";
			case 1: 
				return "An array containing the new column labels";
			case 2: 
				return "";
			default: return "No such input";
		}
	}
	
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Table";
			case 1:
				return "New Column Names";
			case 2:
				return "";
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
				return "The table that was input, modified in place with the" +
					" new labels.";
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
				return "Modified Table";
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
			
					

			

								
	
