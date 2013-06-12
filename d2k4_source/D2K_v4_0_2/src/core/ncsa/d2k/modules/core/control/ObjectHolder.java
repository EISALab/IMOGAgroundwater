package ncsa.d2k.modules.core.control;


import ncsa.d2k.core.modules.*;

/*
	takes in an object , and then
	pushes it out when an object comes into
	the other input 
	
	@author pgroves
	*/

public class ObjectHolder extends DataPrepModule 
	{

	//////////////////////
	//d2k Props
	////////////////////
	protected boolean debug=false;	
	
	/////////////////////////
	/// other fields
	////////////////////////
	protected Object theObject;

	protected boolean waitingForObject=true;
	protected boolean firstFire=true;

	//////////////////////////
	///d2k control methods
	///////////////////////

	public boolean isReady(){
		/*if(waitingForObject)
			return this.getFlags()[0]>0;
		else
			return(this.getFlags()[1]>0);
			*/
		if(firstFire){
			return this.getFlags()[0] > 0;
		}else{
			return super.isReady();
		}

	}
	public void endExecution(){
		waitingForObject=true;
		theObject=null;
		firstFire=true;
		super.endExecution();
		
	}
	public void beginExecution(){
		waitingForObject=true;
		firstFire=true;
		super.beginExecution();
		
	}
	/////////////////////
	//work methods
	////////////////////
	/*
		does it
	*/
	public void doit() throws Exception{
		//System.out.println("ObjectHolder: doit");
		/**if(waitingForObject){
			theObject=pullInput(0);
			waitingForObject=false;
			if(debug)
			System.out.println("ObjectHolder: getting obj");
			if(firstFire){
				pushOutput(theObject, 0);
				waitingForObject=true;
				firstFire=false;
				theObject=null;
				if(debug)
				System.out.println("ObjectHolder: firstfire");
			}
			
		}else{
			pullInput(1);
			pushOutput(theObject, 0);
			waitingForObject=true;
			if(debug)
			System.out.println("ObjectHolder: triggered");
		}*/
		theObject=pullInput(0);
		if(!firstFire){
			pullInput(1);
			if(debug){
				System.out.println(this.getAlias()+ " pulling trigger.");
			}
		}else{
			firstFire = false;
			if(debug){
				System.out.println(this.getAlias()+ " First Run.");
			}
		}
		if(debug){
			System.out.println(this.getAlias()+ " Pushing Object.");
		}
		pushOutput(theObject, 0);
		

	}
		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return "<html>  <head>      </head>  <body>    Takes in an object and saves it. Pushes it out when a triggering input     object comes in,then resets and waits for the next object and trigger. The     first object it gets is sent through w/o waiting for the trigger object  </body></html>";
	}
	
   	public String getModuleName() {
		return "Object Holder";
	}
	public String[] getInputTypes(){
		String[] types = {"java.lang.Object","java.lang.Object"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: return "The object to be saved and passed";
			case 1: return "The triggering object";
			default: return "No such input";
		}
	}
	
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Saved Object";
			case 1:
				return "Trigger";
			default: return "NO SUCH INPUT!";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {"java.lang.Object"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: return "The saved object pushed out every time thereis a trigger object";
			default: return "No such output";
		}
	}
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "The Saved Object";
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
			
					

			

								
	
