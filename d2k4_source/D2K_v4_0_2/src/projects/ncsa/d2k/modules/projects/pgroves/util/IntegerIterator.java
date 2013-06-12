package ncsa.d2k.modules.projects.pgroves.util;


import ncsa.d2k.core.modules.*;


/**
	Takes an integer and returns an Integer object array containing
	the numbers between 0 and the pulled in number

	@author pgroves
	@date 01/13/04
	*/

public class IntegerIterator extends DataPrepModule 
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
		int count = ((Integer)pullInput(0)).intValue();
		Integer[] ints = new Integer[count];
		for(int i = 0; i < count; i++){
			ints[i] = new Integer(i);
		}
		pushOutput(ints, 0);
	}

		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return 	
			"	Takes an integer and returns an Integer object array containing" +
			" the numbers between 0 and the pulled in number."+
			""+
			""+
			"";
	}
	
   	public String getModuleName() {
		return "IntegerIterator";
	}
	public String[] getInputTypes(){
		String[] types = {"java.lang.Integer"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: 
				return "An integer (greater than zero)";
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
				return "Max Value";
			case 1:
				return "";
			case 2:
				return "";
			default: return "No such input";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {"[Ljava.lang.Integer;"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: 
				return "An array of Integer Objects, where the value of the"+
				" Integer at position i in the array is also i. The size "+
				" is the pulled in value of MaxValue";
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
				return "Integer Iterator Array";
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
			
					

			

								
	
