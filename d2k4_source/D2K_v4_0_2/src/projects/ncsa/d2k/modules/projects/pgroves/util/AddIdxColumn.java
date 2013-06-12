package ncsa.d2k.modules.projects.pgroves.util;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

/*
	adds an IntColumn w/ every entry equal
	to the row number.  sets the tables 'key'
	to be that col.

	@author pgroves
	*/

public class AddIdxColumn extends DataPrepModule 
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
	
	/////////////////////
	//work methods
	////////////////////
	/*
		does it
	*/
	public void doit() throws Exception{
		TableImpl vt=(TableImpl)pullInput(0);
		
		int numRows=vt.getColumn(0).getNumRows();
		IntColumn idx=new IntColumn(numRows);
		idx.setLabel("Idx");
		for(int i=0; i<numRows; i++){
			idx.setInt(i,i);
		}
		
		int numCols=vt.getNumColumns();
		Column[] newCols=new Column[numCols+1];
		//newCols[0]=idx;
		for(int i=0; i<numCols;i++){
			newCols[i]=vt.getColumn(i);
		}
		newCols[numCols]=idx;
		vt.setColumns(newCols);
		
		//vt.addColumn(idx);
		
		pushOutput(vt, 0);
	}
		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return "<html>  <head>      </head>  <body>    Adds an int column which contains the row number to the end of the VT  </body></html>";
	}
	
   	public String getModuleName() {
		return "Add Row Indices Column";
	}
	public String[] getInputTypes(){
		String[] types = {"ncsa.d2k.modules.core.datatype.table.basic.TableImpl"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: return "The original Vertical Table";
			default: return "No such input";
		}
	}
	
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Original Table";
			default: return "NO SUCH INPUT!";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {"ncsa.d2k.modules.core.datatype.table.basic.TableImpl"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: return "The Table with the indices column added";
			default: return "No such output";
		}
	}
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Table w/ Indices Column";
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
			
					

			

								
	
