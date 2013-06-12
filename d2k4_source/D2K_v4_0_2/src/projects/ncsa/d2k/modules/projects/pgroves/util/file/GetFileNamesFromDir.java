package ncsa.d2k.modules.projects.pgroves.util.file;


import ncsa.d2k.core.modules.*;
import java.io.File;

/*
	@author pgroves
	*/

public class GetFileNamesFromDir extends InputModule 
	{

	//////////////////////
	//d2k Props
	////////////////////
	String filterSuffix=".csv";	
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
		if(lastPushed==-1)
			return super.isReady();
		else
			return true;
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
		if(lastPushed==-1){//init
			dirName=(String)pullInput(0);
			File dirFile=new File(dirName);
			String[] strs=dirFile.list();
			int c=0;
			for(int i=0;i<strs.length;i++){
				if(strs[i].endsWith(filterSuffix)){
					c++;
				}
			}
			fileNames=new String[c];
			c=0;
			for(int i=0;i<strs.length;i++){
				if(strs[i].endsWith(filterSuffix)){
					fileNames[c]=strs[i];
					c++;
				}
			}
			if(debug){
				System.out.println(getAlias()+" Init:"+dirName);
			}
			pushOutput(new Integer(fileNames.length), 1);
		
		}
		lastPushed++;
		if(debug){
			System.out.println(getAlias()+" Pushing:"+dirName+
					fileNames[lastPushed]);
		}
		pushOutput(dirName+fileNames[lastPushed], 0);
		

		if(lastPushed==fileNames.length-1){//the last one, reset
			wipeFields();
		}
	}
		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return "<html>  <head>      </head>  <body>    Given a directory path name, passes out the filename ofevery file in that     directory that ends with filterSuffix (a property), one at a time. If the filterSuffix property is blank, no filtering will be done.</body></html>";
	}
	
   	public String getModuleName() {
		return "Files from Directory";
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
				return "Directory Name";
			default: return "NO SUCH INPUT!";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {"java.lang.String","java.lang.Integer"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: return "The full pathnames of the files in the input directory";
			case 1: return "The number of files in the directory (pushed once)";
			default: return "No such output";
		}
	}
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Filenames";
			case 1:
				return "Filename Count";
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
	public String getFilterSuffix(){
		return filterSuffix;
	}
	public void setFilterSuffix(String s){
		filterSuffix=s;
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
			
					

			

								
	
