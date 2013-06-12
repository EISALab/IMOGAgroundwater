package ncsa.d2k.modules.projects.pgroves.util;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
/*

		@author pgroves 05/19/02
	*/

public class GetColumnLabel extends DataPrepModule 
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
		Table vt=(Table)pullInput(0);
		int columnIndex = ((Integer)pullInput(1)).intValue();
		
		if(debug){
			System.out.println(this.getAlias() +
				": Label: " + vt.getColumnLabel(columnIndex));
		}
		pushOutput(vt.getColumnLabel(columnIndex), 0);
	}
		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return "Returns the Label of a column of the passed in Table";
	}
	
   	public String getModuleName() {
		return "GetColumnLabel";
	}
	public String[] getInputTypes(){
		String[] types = {"ncsa.d2k.modules.core.datatype.table.Table",
		"java.lang.Integer"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: return "A table";
			case 1: return "The index of the column whose label will be returned";
			default: return "No such input";
		}
	}
	
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "The Table";
			case 1:
				return "Column Index";
			default: return "NO SUCH INPUT!";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {"java.lang.String"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: return "The Column's Label";
			default: return "No such output";
		}
	}
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Column Label";
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
			
					

			

								
	
