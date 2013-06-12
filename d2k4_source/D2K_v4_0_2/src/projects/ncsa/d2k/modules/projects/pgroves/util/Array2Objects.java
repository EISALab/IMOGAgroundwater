package ncsa.d2k.modules.projects.pgroves.util;


import ncsa.d2k.core.modules.*;

/*
	@author pgroves
	*/

public class Array2Objects extends DataPrepModule 
	{

	//////////////////////
	//d2k Props
	////////////////////
	
	boolean debug = false;		
	/////////////////////////
	/// other fields
	////////////////////////

	Object[] arr;
	int objCount;
	//int objsIn;
	//////////////////////////
	///d2k control methods
	///////////////////////

	public boolean isReady(){
		if(objCount==0)
			return super.isReady();
		return true;	
	}
	public void endExecution(){
		super.endExecution();
	}
	public void beginExecution(){
		wipeFields();
		super.beginExecution();
	}
	public void wipeFields(){
		arr=null;
		objCount=0;
	}
	
	/////////////////////
	//work methods
	////////////////////
	/*
		does it
	*/
	public void doit() throws Exception{
		if(debug){
			System.out.println(getAlias()+" Firing");
		}
		if(objCount==0){
			arr=(Object[])pullInput(0);
			pushOutput(new Integer(arr.length),1);
		}
		pushOutput(arr[objCount],0);
		objCount++;
		if(objCount==arr.length){
			wipeFields();
		}
	}
		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return "<html>  <head>  </head>  <body>    <p>          </p>  </body></html>";
	}
	
   	public String getModuleName() {
		return "Object Array Iterator";
	}
	public String[] getInputTypes(){
		String[] types = {"[Ljava.lang.Object:"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: return "";
			default: return "No such input";
		}
	}
	
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "The Object Array";
			default: return "NO SUCH INPUT!";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {"java.lang.Object","java.lang.Integer"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: return "The objects";
			case 1: return "The number of objects in the array";

			default: return "No such output";
		}
	}
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Objects ";
			case 1:
				return "Object Count";
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
			
					

			

								
	
