package ncsa.d2k.modules.projects.pgroves.util;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.datatype.table.util.*;


/**
	Appends the columns of a table to the column array of another
	(mutable) table.

	@author pgroves
	*/

public class AppendTablesByColumn extends DataPrepModule 
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
		MutableTable t1=(MutableTable)pullInput(0);
		Table t2=(Table)pullInput(1);

		MutableTable t3=appendColumns(t1, t2);

		pushOutput(t3,0);
	}
		
	/**
		Appends the columns of a table to a mutable table.
		Does the actual work. Given two tables, the first one being
		mutable, appends the columns of the second to the first. 
		the columns of the first table are therefore copied by reference
		and the columns of the second are copied by value

		@param t1 The mutable table,  will be returned
		@param t2 Any kind of table, the values will be copied out of it

		@return t1 with t2 appended 
	*/
	public MutableTable appendColumns(MutableTable t1, Table t2){	
		int numRows = t1.getNumRows();
		int numCols1=t1.getNumColumns();
		int numCols2=t2.getNumColumns();

		int i,j,c1;

		for(i=0;i<numCols2;i++){
			//addColumnByType(t1,t2.getColumnType(i));
			Column col = ColumnUtilities.createColumn(
				t2.getColumnType(i), numRows);
			t1.addColumn(col);
			c1=numCols1+i;
			t1.setColumnLabel(t2.getColumnLabel(i), c1);
			
			for(j=0;j<numRows;j++){
				TableUtilities.setValue(t2, j, i, t1, j, c1);	
			}
		}
		return t1;
	}


	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return 	
			"Takes a mutable table (Table 1) and a table (Table 2), and adds"+
			" the columns of Table 2 to Table1 (so that the new table has a "+
			"number of columns equal to Table1.numColumns()+Table2.numColumns()."+
			" This is probably a bad idea unless the two tables have the same"+
			" number of rows";
	}
	
   	public String getModuleName() {
		return "Append Tables";
	}
	public String[] getInputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.table.MutableTable",
			"ncsa.d2k.modules.core.datatype.table.Table",
			};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: 
				return 
					"The Mutable Table that a copy of Table 2 will be appended to";
			case 1: 
				return "The table to append to Table 1";
			case 2: 
				return "";
			default: return "No such input";
		}
	}
	
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Table 1";
			case 1:
				return "Table 2";
			case 2:
				return "";
			default: return "No such input";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.table.MutableTable" };
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: 
				return "The original Table 1, with Table 2's columns appended";
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
				return "Merged Table";
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
					

			

								
	
