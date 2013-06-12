package ncsa.d2k.modules.projects.pgroves.optimize;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.parameter.*;


/**
	Takes a Parameter, prints it

	@author pgroves
	*/

public class PrintParameter extends OutputModule 
	implements java.io.Serializable {

	//////////////////////
	//d2k Props
	////////////////////
	
	boolean debug = false;		
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
		ParameterPoint pp = (ParameterPoint)pullInput(0);
		if(debug)
			printParameter(pp);
		pushOutput(pp, 0);	
	}
		
	/**
		pretty prints a ParameterPoint's info to the terminal.
		*/	
	public static void printParameter(ParameterPoint pp){
		int numParams = pp.getNumParameters();
		
		System.out.println("\tParam Name         Value");
		for(int i = 0; i < numParams; i++){
			StringBuffer sb = new StringBuffer();
			sb.append("\t");
			sb.append(pp.getName(i));
			sb.setLength(15);
			while(sb.length() <= 18){
				sb.append(" ");
			}
			System.out.print(sb);
			System.out.println(pp.getValue(i));
		}
	}
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return 	
			"Prints a Parameter's info to the terminal and then passes"+
			"it along. turn debug to false to suppress the printing."+
			" This is purely a debugging tool."+
			"";
	}
	
   	public String getModuleName() {
		return "PrintParameter";
	}
	public String[] getInputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.parameter.ParameterPoint"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: 
				return "";
			case 1: 
				return "";
			case 2: 
				return "";
			default: return "No such input";
		}
	}
	
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Parameter Point";
			case 1:
				return "";
			case 2:
				return "";
			default: return "No such input";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.parameter.ParameterPoint"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: 
				return "";
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
				return "Same Parameter Point";
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
			
					

			

								
	
