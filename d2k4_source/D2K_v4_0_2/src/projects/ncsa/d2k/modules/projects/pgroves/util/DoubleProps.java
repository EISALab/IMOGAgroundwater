package ncsa.d2k.modules.projects.pgroves.util;


import ncsa.d2k.core.modules.*;

/** 	Pushes out a Double that is set in the properties

	@author pgroves
	@date 02/20/04
	*/

public class DoubleProps extends InputModule 
	{

	//////////////////////
	//d2k Props
	////////////////////
	double d = 0.0;
	
	boolean debug=true;		
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
	
	/////////////////////
	//work methods
	////////////////////
	/*
		does it
	*/
	public void doit() throws Exception{
		if(debug){
			System.out.println(getAlias()+": Firing:"+d);
		}
		pushOutput(new Double(d), 0);

	}
		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return "Pushes out a Double that is set in the properties.";
	}
	
   	public String getModuleName() {
		return "DoubleFromProps";
	}
	public String[] getInputTypes(){
		String[] types = {		};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			default: return "No such input";
		}
	}
	
	public String getInputName(int index) {
		switch(index) {
			default: return "NO SUCH INPUT!";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {"java.lang.Double"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: return "The double set in this module's properties";
			default: return "No such output";
		}
	}
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Double";
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
	public double getDouble(){
		return d;
	}
	public void setDouble(double in ){
		d = in;
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
			
					

			

								
	
