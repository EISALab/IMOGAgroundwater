package ncsa.d2k.modules.projects.pgroves.util;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.datatype.table.util.*;


/*
	Sorts a Table based on multiple columns


	@author pgroves
	*/

public class MultiColumnSort extends DataPrepModule 
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
		MutableTable tbl=(MutableTable)pullInput(1);
		int[] kc=(int[])pullInput(0);

		int[] order=TableUtilities.multiSortIndex(tbl, kc);
		pushOutput(tbl.reorderRows(order), 0);
	}
		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return 	"<html><head></head><body><p></p>"+
				"Sorts a table such that it is sorted first by the column "+
				" at index keyColumns[0], then 1, 2,..."+
				"</body></html>";
	}
	
   	public String getModuleName() {
		return "MultiColumnSort";
	}
	public String[] getInputTypes(){
		String[] types = {"[I",
			"ncsa.d2k.modules.core.datatype.table.MutableTable"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: return "The column indices to sort by";
			case 1: return "A table to sort";
			default: return "No such input";
		}
	}
	
	public String getInputName(int index) {
		switch(index) {
			case 0: return "KeyColumn Indices";
			case 1:
				return "Unsorted Table";
			default: return "NO SUCH INPUT!";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {"ncsa.d2k.modules.core.datatype.table.MutableTable"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: return "A copy of the input table, sorted.";
			default: return "No such output";
		}
	}
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Sorted Table";
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
			
					

			

								
	
