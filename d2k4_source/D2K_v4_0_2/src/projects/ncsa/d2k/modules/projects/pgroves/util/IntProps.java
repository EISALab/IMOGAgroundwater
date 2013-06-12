package ncsa.d2k.modules.projects.pgroves.util;


import ncsa.d2k.core.modules.*;

/* 	Pushes out an Integer that is set in the properties

	@author pgroves
	@date 11/13/02
	*/

public class IntProps extends InputModule 
	{

	//////////////////////
	//d2k Props
	////////////////////
	int i=0;
	
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
			System.out.println(getAlias()+": Firing:"+i);
		}
		pushOutput(new Integer(i), 0);

	}
		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return "<html>  <head>      </head>  <body>Pushes out an Integer that is set in the properties</body></html>";
	}
	
   	public String getModuleName() {
		return "IntFromProps";
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
		String[] types = {"java.lang.Integer"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: return "The integer set in this module's properties";
			default: return "No such output";
		}
	}
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "int";
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
	public int getInteger(){
		return i;
	}
	public void setInteger(int in){
		i=in;
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
			
					

			

								
	
