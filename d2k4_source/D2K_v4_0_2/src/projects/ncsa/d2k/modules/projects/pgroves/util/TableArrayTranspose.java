package ncsa.d2k.modules.projects.pgroves.util;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.util.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
/**

		Takes a 3-d data set represented in multiple 2-d tables and
		switches the dimensions that are table names and column
		names. Takes in two tables, all w/ the same columns (names and Types, 
		not the same instances).  The first column should be <i>exactly</i>
		the same as it will be preserved (a column of indices).  N new tables
		will be made where N is the number of columns the original tables have.
		They each will have k columns where k is the number of original tables.
		Each original column label will become a table label and each table
		label will become a column label in each of the new tables. 		
		
		@author pgroves 03/03/02

		updated/renamed/repackaged 07/10/03
	*/

public class TableArrayTranspose extends DataPrepModule 
	{

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
		super.endExecution();
	}
	public void beginExecution(){
		super.beginExecution();
	}

	private void wipeFields(){
	}
	
	/////////////////////
	//work methods
	////////////////////
	/*
		does it

	*/
	public void doit() throws Exception{
		int i,j,k;
		
		Object[] ots=(Object[])pullInput(0);
		//i don't want to keep having to cast these
		Table[] origTables=new Table[ots.length];
		
		
		for(i=0; i<origTables.length; i++){
			origTables[i]=(Table)ots[i];
		}

		int dim1 = origTables.length;
		int dim2 = origTables[0].getNumColumns()-1;
		int dim3 = origTables[0].getNumRows();
		MutableTableImpl[] newTables = new MutableTableImpl[dim2];

		for(i=0;i<dim2;i++){
			newTables[i]=new MutableTableImpl();
			newTables[i].setLabel(origTables[0].getColumnLabel(i+1));
			newTables[i].addColumn(((TableImpl)origTables[0]).getColumn(0));
			for(j=0; j<dim1; j++){
				
				newTables[i].addColumn(
					ColumnUtilities.createColumn(origTables[j].getColumnType(i+1),
					dim3));
				
				newTables[i].setColumnLabel(origTables[j].getLabel(), j+1);
				
				for(k=0;k<dim3;k++){
				
					TableUtilities.setValue(
						origTables[j], k, i+1,
						newTables[i], k, j+1);
				}
			}
		}

		pushOutput(newTables, 0);
	}
		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return "		Takes a 3-d data set represented in multiple 2-d tables and"+
		"switches the dimensions that are table names and column"+
		" names. Takes in two tables, all w/ the same columns (names and Types, "+
		" not the same instances).  The first column should be <i>exactly</i>"+
		" the same as it will be preserved (a column of indices).  N new tables "+
		"will be made where N is the number of columns the original tables have."+
		" They each will have k columns where k is the number of original "+
		"tables."+
		" Each original column label will become a table label and each table"+
		" label will become a column label in each of the new tables.";
	}
	
   	public String getModuleName() {
		return "";
	}
	public String[] getInputTypes(){
		String[] types = {"[Ljava.lang.Object"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: return "An array of TableImpls that represent 3-D data";
			default: return "No such input";
		}
	}
	
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Array of Same Format Tables ";
			default: return "NO SUCH INPUT!";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {
			"[Lncsa.d2k.modules.core.datatype.table.basic.MutableTableImpl"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: return 
			"New Tables w/ the original table dimension swapped with "+
			"the Column dimension";
			default: return "No such output";
		}
	}
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Transformed Tables";
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
			
					

			

								
	
