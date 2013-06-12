package ncsa.d2k.modules.projects.pgroves.util;


import ncsa.d2k.core.modules.*;
/*

		@author pgroves 03/03/02
	*/

public class AddStrings extends DataPrepModule 
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
		Object str1 = pullInput(0);
		Object str2 = pullInput(1);

		String str = str1.toString() + str2.toString();
		if(debug)
			System.out.println(this.getAlias() + ": " + str);
		pushOutput(str, 0);
		
		}
		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return "Takes 2 objects, adds the string representation of the second " +
		"to the end of the first.";
	}
	
   	public String getModuleName() {
		return "";
	}
	public String[] getInputTypes(){
		String[] types = {"java.lang.Object","java.lang.Object"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: return "";
			case 1: return "";
			default: return "No such input";
		}
	}
	
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "String One";
			case 1:
				return "String Two";
			default: return "NO SUCH INPUT!";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {"java.lang.String"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: return "";
			default: return "No such output";
		}
	}
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Concantenated toString()'s ";
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
			
					

			

								
	
