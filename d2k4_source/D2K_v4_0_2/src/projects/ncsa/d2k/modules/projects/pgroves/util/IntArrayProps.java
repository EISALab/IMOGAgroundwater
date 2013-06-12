package ncsa.d2k.modules.projects.pgroves.util;


import ncsa.d2k.core.modules.*;

/*
	@author pgroves
	*/

public class IntArrayProps extends InputModule 
	{

	//////////////////////
	//d2k Props
	////////////////////
	int[] ins=new int[5];
	
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
			System.out.println(this.getAlias()+": Head Module Firing.");
		}

		int[] output=new int[ins[0]];
		for(int i=0;i<output.length; i++)
			output[i]=ins[i+1];
			
		pushOutput(output, 0);

	}
		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return "<html>  <head>      </head>  <body>    Creates an array of ints from the properties, max size is 4  </body></html>";
	}
	
   	public String getModuleName() {
		return "Int Array";
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
		String[] types = {"[I"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: return "an array of ints";
			default: return "No such output";
		}
	}
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "int[]";
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
	public int getNumIntsToUse(){
		return ins[0];
	}
	public void setNumIntsToUse(int i){
		ins[0]=i;
	}
	public int getInt0(){
		return ins[1];
	}
	public void setInt0(int i){
		ins[1]=i;
	}
	public int getInt1(){
		return ins[2];
	}
	public void setInt1(int i){
		ins[2]=i;
	}
	public int getInt2(){
		return ins[3];
	}
	public void setInt2(int i){
		ins[3]=i;
	}
	public int getInt3(){
		return ins[4];
	}
	public void setInt3(int i){
		ins[4]=i;
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
			
					

			

								
	
