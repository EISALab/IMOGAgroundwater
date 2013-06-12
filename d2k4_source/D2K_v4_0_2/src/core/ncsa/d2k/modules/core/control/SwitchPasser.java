package ncsa.d2k.modules.core.control;


import ncsa.d2k.core.modules.*;

/*
	Has 2 inputs, one output. Both inputs pass unchanged through
	the output. They are sequenced such that the first input will be
	passed first, then the second, then the first... regardless of the
	order the inputs come in. 
	
	@author pgroves
	*/

public class SwitchPasser extends DataPrepModule 
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
		if(!objectSwitch)
			return (this.getFlags()[0] > 0);
		else
			return (this.getFlags()[1] > 0);
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
		if(!objectSwitch){
			obj=pullInput(0);
			if(debug){
				System.out.println(getAlias()+":Input 1:"+obj.toString());
			}
		}else{
			obj=pullInput(1);
			if(debug){
				System.out.println(getAlias()+":Input 2:"+obj.toString());
			}
		}
		objectSwitch=(!objectSwitch);
		pushOutput(obj,0);
	}
		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return "<html><head></head><body>Alternately pushes out objects in "+
		"input 1 and input 2. That is, pulls input 1, pushes it out, pulls"+
		" input 2, pushes it, pulls 1, etc.</body></html>";
	}
	
   	public String getModuleName() {
		return "Switch Passer";
	}
	public String[] getInputTypes(){
		String[] types = {"java.lang.Object","java.lang.Object"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: return "The first object";
			case 1: return "The second object";
			default: return "No such input";
		}
	}
	
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Object 1";
			case 1:
				return "Object 2";
			default: return "NO SUCH INPUT!";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {"java.lang.Object"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: return "The objects in input 1 and 2, in order";
			default: return "No such output";
		}
	}
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "The Objects";
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
			
					

			

								
	
