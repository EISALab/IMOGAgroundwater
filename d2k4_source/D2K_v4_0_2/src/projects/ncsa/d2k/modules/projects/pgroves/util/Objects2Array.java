package ncsa.d2k.modules.projects.pgroves.util;


import ncsa.d2k.core.modules.*;

/*
	@author pgroves
	*/

public class Objects2Array extends DataPrepModule 
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
	int objsIn;
	//////////////////////////
	///d2k control methods
	///////////////////////

	public boolean isReady(){
		if((objsIn<objCount)&&(this.getFlags()[0]>0))
			return true;
			
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
		objCount=-2;
		objsIn=-1;
		arr=null;
	}
	
	/////////////////////
	//work methods
	////////////////////
	/*
		does it
	*/
	public void doit() throws Exception{
		if(debug){
			System.out.println("O2A: Firing"+objsIn); 
		}
		if(arr==null){
			objCount=((Integer)pullInput(1)).intValue();
			arr=new Object[objCount];
		}
		objsIn++;
		arr[objsIn]=pullInput(0);
		if(objsIn==(objCount-1)){
			if(debug){
				System.out.println("O2A: Pushing, size:"+objCount); 
			}

			pushOutput(arr, 0);
			wipeFields();
		}

	}
		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return "";
	}
	
   	public String getModuleName() {
		return "Object Compiler";
	}
	public String[] getInputTypes(){
		String[] types = {"java.lang.Object","java.lang.Integer"};
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
				return "The Objects";
			case 1:
				return "Object Count";
			default: return "NO SUCH INPUT!";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {"[Ljava.lang.Object"};
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
				return "Object Array";
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
			
					

			

								
	
