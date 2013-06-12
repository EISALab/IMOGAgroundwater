package ncsa.d2k.modules.core.control;


import ncsa.d2k.core.modules.*;

/*
	Has 1 inputs, 2 outputs. Alternates pushing the input object out of 
	input 1 and input 2. so the first pulled object goes to output 1, second
	to output 2, third to 1, fourth to 2, etc.
	
	@author pgroves
	@date 10/23/02
	*/

public class InvSwitchPasser extends DataPrepModule 
	{

	//////////////////////
	//d2k Props
	////////////////////
	protected boolean debug=false;	
	
	/////////////////////////
	/// other fields
	////////////////////////

	//false for object 1, true for object 2
	protected boolean objectSwitch=false;

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
		wipeFields();
		super.beginExecution();
		
	}
	public void wipeFields(){
		objectSwitch=false;
	}
	/////////////////////
	//work methods
	////////////////////
	/*
		does it
	*/
	public void doit() throws Exception{
		Object obj;
		obj=pullInput(0);
		if(!objectSwitch){
			pushOutput(obj,0);
			if(debug){
				System.out.println(getAlias()+":Output 1:"+obj.toString());
			}
		}else{
			pushOutput(obj,1);
			if(debug){
				System.out.println(getAlias()+":Output 2:"+obj.toString());
			}
		}
		objectSwitch=(!objectSwitch);
	}
		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return "<html><head></head><body>Alternately pushes out objects to "+
		"output 1 and output 2. That is, pulls in first object, pushes it out"+
		" to output 1, pulls the second, pushes it to output 2, pulls the third"
		+", pushes it to output 1, etc.</body></html>";
	}
	
   	public String getModuleName() {
		return "Switch Out";
	}
	public String[] getInputTypes(){
		String[] types = {"java.lang.Object"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: return "The ordered inputs object";
			default: return "No such input";
		}
	}
	
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Object In";
			default: return "NO SUCH INPUT!";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {"java.lang.Object","java.lang.Object"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: return "The input if (run Number mod 2)=1";
			case 1: return "The input if (run Number mod 2)=0";
			default: return "No such output";
		}
	}
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "First,Third,...";
			case 1:
				return "Second,Fourth,...";

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
	*/
}
			
					

			

								
	
