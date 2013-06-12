package ncsa.d2k.modules.projects.pgroves.util.file;


import ncsa.d2k.core.modules.*;
import java.io.File;

/**
 * Strips the path info from a full path+filename and returns only the filename
 * 
	@author pgroves
	@date 04/09/04
	*/

public class RemovePathFromFileName extends DataPrepModule 
	{

	//////////////////////
	//d2k Props
	////////////////////
	boolean debug=false;		
	/////////////////////////
	/// other fields
	////////////////////////
	String[] fileNames;
	int lastPushed=-1;
	String dirName;

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
		lastPushed=-1;
		dirName=null;
		fileNames=null;
	}
	
	/////////////////////
	//work methods
	////////////////////
	/*
		does it
	*/
	public void doit() throws Exception{
		String fullName=(String)pullInput(0);
		File file = new File(fullName);
		
		String filename = file.getName();
		
		if(debug){
			System.out.println(getAlias()+
					"\n\t Before:" + fullName +
					"\n\t After:" + filename);
		}
		pushOutput(filename, 0);
		
	}
		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return "Given a full path and filename, returns just the filename.";
	}
	
   	public String getModuleName() {
		return "RemovePathFromFileName";
	}
	public String[] getInputTypes(){
		String[] types = {"java.lang.String"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: return "The absolute pathname and filename of a file.";
			default: return "No such input";
		}
	}
	
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Full Name";
			default: return "NO SUCH INPUT!";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {"java.lang.String"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: return "Just the filename (no path)";
			default: return "No such output";
		}
	}
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Filename";
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
			
					

			

								
	
