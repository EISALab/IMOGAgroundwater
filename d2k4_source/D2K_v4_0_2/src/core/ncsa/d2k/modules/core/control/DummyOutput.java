package ncsa.d2k.modules.core.control;


import ncsa.d2k.core.modules.*;

/*
	Has an output pipe that never is filled. 
	Used to clean up unused inputs in nested itins.
	@author pgroves
	*/

public class DummyOutput extends ComputeModule 
	{

	//////////////////////
	//d2k Props
	////////////////////
	
	
	/////////////////////////
	/// other fields
	////////////////////////


	//////////////////////////
	///d2k control methods
	///////////////////////

	public boolean isReady(){
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
	}
		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return "<html>  <head>      </head>  <body>    Has an output pipe that never is filled. Used to clean up unused inputs in     nested itins.  </body></html>";
	}
	
   	public String getModuleName() {
		return "Nested Input Cleanup";
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
		String[] types = {"java.lang.Object"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: return "This pipe will never be filled";
			default: return "No such output";
		}
	}
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Dummy Pipe";
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
			
					

			

								
	
