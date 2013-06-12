package ncsa.d2k.modules.projects.pgroves.util.file;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;

/*
	@author pgroves
	*/

public class FileNameFilter extends DataPrepModule 
	{

	//////////////////////
	//d2k Props
	////////////////////
	String suffix=".csv";
	
	/* if true, will remove the suffix from those strings that
	 * get passed
	 */
	boolean stripSuffix = false;
	
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
	
	/////////////////////
	//work methods
	////////////////////
	/*
		does it
	*/
	public void doit() throws Exception{
		String str=(String)pullInput(0);
		if(str.endsWith(suffix)){
			if(this.stripSuffix){
				str = str.substring(0, str.length() - suffix.length());
			}
			if(debug){
				System.out.println(getAlias()+" Pushing:"+str);
			}
			pushOutput(str, 0);
		}

	}
		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return "<html>  <head>      </head>  <body>    Takes in a filename, passes it along if it has the correct suffix     ('suffix' is a property)  </body></html>";
	}
	
   	public String getModuleName() {
		return "File Filter";
	}
	public String[] getInputTypes(){
		String[] types = {"java.lang.String"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: return "A candidate file name";
			default: return "No such input";
		}
	}
	
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "FileName";
			default: return "NO SUCH INPUT!";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {"java.lang.String"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: return "The input String, if it ends with the suffix";
			default: return "No such output";
		}
	}
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Valid Filename";
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
	public String getSuffix(){
		return suffix;
	}
	public void setSuffix(String s){
		suffix=s;
	}
	public boolean getStripSuffix(){
		return stripSuffix;
	}
	public void setStripSuffix(boolean b){
		stripSuffix=b;
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
			
					

			

								
	
