package ncsa.d2k.modules.core.control;


import ncsa.d2k.core.modules.*;

/*
	@author pgroves
	*/

public class SaveLastObject extends ComputeModule 
	{

	//////////////////////
	//d2k Props
	////////////////////
	
	
	/////////////////////////
	/// other fields
	////////////////////////

	private Object savedObj;
	//////////////////////////
	///d2k control methods
	///////////////////////

	public boolean isReady(){
		if(this.getFlags()[0]>0){
			return true;
		}
		if(this.getFlags()[1]>0){
			return true;
		}
		return false;
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
		if(this.getFlags()[0]>0){
			savedObj=pullInput(0);
		}
		if(this.getFlags()[1]>0){
			if(savedObj!=null){
				pushOutput(savedObj, 0);
			}else
				return;
		}
	}
		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return "<paragraph>  <head>  </head>  <body>    <p>          </p>  </body></paragraph>";
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
				return "Saved Object";
			case 1:
				return "End Trigger";
			default: return "NO SUCH INPUT!";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {"java.lang.Object"};
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
				return "";
			default: return "NO SUCH OUTPUT!";
		}
	}		
	////////////////////////////////
	//D2K Property get/set methods
	///////////////////////////////

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
			
					

			

								
	
