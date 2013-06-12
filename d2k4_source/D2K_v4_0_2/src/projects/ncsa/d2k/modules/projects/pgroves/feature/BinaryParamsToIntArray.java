package ncsa.d2k.modules.projects.pgroves.feature;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.parameter.*;


/**
	Given a parameter point of all binary parameters, creates an
	integer array containing the indices of the parameters
	that were true.


	@author pgroves
	@date 02/05/04
	*/

public class BinaryParamsToIntArray extends DataPrepModule 
	implements java.io.Serializable {

	//////////////////////
	//d2k Props
	////////////////////
	
	boolean debug=false;		

	boolean trueVsFalse = true;
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

		int numBits = pp.getNumParameters();

		int numTrue = 0;
		for(int i = 0; i < numBits; i++){
			if(accept(pp.getValue(i))){
				numTrue++;
			}
		}
		int[] indices = new int[numTrue];
		int j = 0;
		for(int i = 0; i < numBits; i++){
			if(accept(pp.getValue(i))){
				indices[j] = i;
				j++;
			}
		}
		if(debug){
			System.out.print(this.getAlias() + "Indices selected: ");
			for(int i = 0; i < numTrue; i++){
				System.out.print(indices[i]);
				System.out.print(", ");
			}
			System.out.println();
			System.out.println();
		}

		pushOutput(indices, 0);
		
	}
		
	private boolean accept(double val){
		if(trueVsFalse && (val > 0.5))
			return true;
		if(!trueVsFalse && (val < 0.5))
			return true;
		return false;
	}
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return 	
		"Given a parameter point of all binary parameters, creates an" +
		" integer array containing the indices of the parameters" +
		" that were true. It assumes that values greater than 0.5 indicate"+
		" true, and values less indicate false. Property 'trueVsFalse:'"+
			" if true, will use the indices of the booleans that are true."+
			" If false, will use the indices of the booleans that are false.";
	}
	
   	public String getModuleName() {
		return "BinaryParamsToIntArray";
	}
	public String[] getInputTypes(){
		String[] types = {
			"ncsa.d2k.modules.datatype.parameter.ParameterPoint"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: 
				return "A ParameterPoint of Boolean parameters";
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
				return "Binary Parameters";
			case 1:
				return "";
			case 2:
				return "";
			default: return "No such input";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {
			"[I"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: 
				return "An array of integers";
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
				return "Int Array";
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
	public boolean getTrueVsFalse(){
		return trueVsFalse;
	}
	public void setTrueVsFalse(boolean b){
		trueVsFalse = b;
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
			
					

			

								
	
