package ncsa.d2k.modules.projects.pgroves.util.file;


import ncsa.d2k.core.modules.*;
import java.io.File;

/*
	@author pgroves
	*/

public class GetRelDirName extends InputModule 
	{

	//////////////////////
	//d2k Props
	////////////////////
	boolean debug=false;		
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
	private void wipeFields(){
	}
	
	/////////////////////
	//work methods
	////////////////////
	/*
		does it
	*/
	public void doit() throws Exception{
		String dirLongName=(String)pullInput(0);
		int lastSlash=dirLongName.lastIndexOf('/');
		String dirName=dirLongName.substring(lastSlash+1, dirLongName.length());
		if(debug){
			System.out.println(getAlias()+" Pushing:"+dirName);
		}
		pushOutput(dirName, 0);

	}
		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return "<html>  <head>      </head>  <body>    Given an absolute directory path name, passes out just the name of the directory</body></html>";
	}
	
   	public String getModuleName() {
		return "RelativeName";
	}
	public String[] getInputTypes(){
		String[] types = {"java.lang.String"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: return "The absolute pathname of the directory";
			default: return "No such input";
		}
	}
	
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Full Directory Name";
			default: return "NO SUCH INPUT!";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {"java.lang.String"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: return "The directories name";
			default: return "No such output";
		}
	}
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Local Name";
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
			
					

			

								
	
