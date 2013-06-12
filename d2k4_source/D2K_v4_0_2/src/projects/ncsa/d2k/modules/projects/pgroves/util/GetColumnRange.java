package ncsa.d2k.modules.projects.pgroves.util;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.util.*;


/**
	returns the max and min of the specified column
	of the table as doubles. Uses TableUtilities class.


	@author pgroves
	@date 02/19/04
	*/

public class GetColumnRange extends ComputeModule 
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
		
		Table tbl = (Table)pullInput(0);
		int colIdx = ((Integer)pullInput(1)).intValue();

		double[] rng = TableUtilities.getMinMax(tbl, colIdx);

		if(debug){
			System.out.println(getAlias()+": Min:"+rng[0]+" Max:"+rng[1]);
		}

		pushOutput(new Double(rng[0]), 0);
		pushOutput(new Double(rng[1]), 1);


		
	}
		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return 	
			"Takes a Table and a column index. Returns the min and max"+
			" numerical values from that column as Double Objects."+
			""+
			"";
	}
	
   	public String getModuleName() {
		return "GetColumnRange";
	}
	public String[] getInputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.table.Table",
			"java.lang.Integer"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: 
				return "A table";
			case 1: 
				return "An index of a column in the Table. If the column,"+
				" does not contain data that can be represented numerically,"+
				" the behaviour is undefined (probably will return whatever "+
				" getDouble returns for a StringColumn).";
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
				return "Column Index";
			case 2:
				return "";
			default: return "No such input";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {
			"java.lang.Double",
			"java.lang.Double" };
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: 
				return "The minimum numeric value from the column at position"+
				" Column Index";
			case 1:
				return "The maximum numeric value from the column at position"+
				" Column Index";
			case 2:
				return "";
			default: return "No such output";
		}
	}
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Minimum";
			case 1:
				return "Maximum";
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
			
					

			

								
	
