package ncsa.d2k.modules.core.control;


import ncsa.d2k.core.modules.*;

/*
	@author pgroves
	*/

public class ObjectPasser extends DataPrepModule 
	{

	//////////////////////
	//d2k Props
	////////////////////
	boolean debug=false;
	String inputName="Object In";

	String outputName="Object Out";

	
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
		Object ob=pullInput(0);
		if(debug)
			System.out.println(getAlias()+": Firing. "+ob.toString());
		pushOutput(ob, 0);
	}
		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return "<paragraph>  <head>  </head>  <body>    <p>Does nothing but passes the input object through."+
				" Intended to be used when an input into an embedded"+
				" itinerary needs to be fanned out.  <br><b>Properties</b>"+
				"<ul><li>inputName: the name for the input to appear in the"+
				" module info window. (to keep track of similar inputs/outputs"+
				" when using embedded itins).<li>outputName: the name of the"+
				" output</ul></p>  </body></paragraph>";
	}
	
   	public String getModuleName() {
		return "Pass unchanged object";
	}
	public String[] getInputTypes(){
		String[] types = {"java.lang.Object"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: return inputName;
			default: return "No such input";
		}
	}
	
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return inputName;
			default: return "NO SUCH INPUT!";
		}
	}
	public String[] getOutputTypes(){	
		String[] types = {"java.lang.Object"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: return outputName;
			default: return "No such output";
		}
	}
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return outputName;
			default: return "NO SUCH OUTPUT!";
		}
	}		
	////////////////////////////////
	//D2K Property get/set methods
	///////////////////////////////
	public void setInput_Name(String s){
		inputName=s;
	}
	public String getInput_Name(){
		return inputName;
	}
	public void setOutput_Name(String s){
		outputName=s;
	}
	public String getOutput_Name(){
		return outputName;
	}
	public boolean getDebug(){
		return debug;
	}
	public void setDebug(boolean b){
		debug=b;
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
			
					

			

								
	
