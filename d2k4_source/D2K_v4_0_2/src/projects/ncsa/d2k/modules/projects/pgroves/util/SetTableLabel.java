package ncsa.d2k.modules.projects.pgroves.util;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
/**
	Sets the label of a table to a given string.
	
	@author pgroves 04/09/04
	*/

public class SetTableLabel extends DataPrepModule 
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
		MutableTable vt=(MutableTable)pullInput(0);
		String label = (String)pullInput(1);
		if(debug){
			System.out.println(this.getAlias() + " Changing name from '" +
					vt.getLabel() + "' to '" + label + "'");
		}
		vt.setLabel(label);
		
		pushOutput(vt, 0);
	}
		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return "Renames a table to the specified label.";
	}
	
   	public String getModuleName() {
		return "SetTableLabel";
	}
	public String[] getInputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.table.MutableTable",
			"java.lang.String"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: return "A Table";
			case 1: return "The Table's new label.";
			default: return "No such input";
		}
	}
	
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Table";
			case 1:
				return "New Label";
			default: return "NO SUCH INPUT!";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.table.MutableTable"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: return "The same table, with a new name.";
			default: return "No such output";
		}
	}
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Renamed Table";
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
			
					

			

								
	
