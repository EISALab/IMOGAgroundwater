package ncsa.d2k.modules.projects.pgroves.util;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;


/**	
	Takes a table and returns all of it's data in a
	one dimensional double array. Can do it either 
	row at a time or column at a time 

	@author pgroves
	@date 02/06/04
	*/

public class FlattenTable extends DataPrepModule 
	implements java.io.Serializable {

	//////////////////////
	//d2k Props
	////////////////////
	
	boolean debug=false;
	
	// if true, copies by row, if false, by column
	boolean rowVsCol = true;		
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
		wipeFields();
		super.endExecution();
	}
	public void beginExecution(){
		wipeFields();
		super.beginExecution();
	}
	public void wipeFields(){
	}
	
	/////////////////////
	//work methods
	////////////////////
	/*
		does it
	*/
	public void doit() throws Exception{
		if(debug){
			System.out.println(getAlias()+":Firing");
		}
		
		Table tbl = (Table)pullInput(0);
		int numCols = tbl.getNumColumns();
		int numRows= tbl.getNumRows();
		
		double[] dd = new double[numCols * numRows];
		
		if(rowVsCol){
			for(int i = 0; i < numRows; i++){
				this.getRow(tbl, dd, i, i * numCols);
			}
		}else{
			for(int i = 0; i < numCols; i++){
				this.getColumn(tbl, dd, i, i * numRows);
			}
		}
		pushOutput(dd, 0);
		
	}
	
	/**
		copies all the values in a row in a table into a double array
		by calling getDouble at every column (for the appropriate
		row). If the buffer double array is not big enough to accept
		the entire row, the buffer will be filled as much as possible
		and returned.
		

		@param tbl a table
		@param rowValues a double array buffer that the that values will 
		be copied into
		@param rowIndex which row from tbl to copy
		@param bufferStartIndex the index of the rowValues array
		to start at.
		*/	
	public void getRow(Table tbl, double[] rowValues, int rowIndex,
		int bufferStartIndex ){

		int numCols = tbl.getNumColumns();
		for(int i = 0; i < numCols; i++){
			rowValues[i + bufferStartIndex] = tbl.getDouble(rowIndex, i);
		}
	}
			 
	/**
		copies all the values in a column in a table into a double array
		by calling getDouble at every row (for the appropriate
		column).  		

		@param tbl a table
		@param columnValues a double array buffer that the that values will 
		be copied into
		@param columnIndex which column from tbl to copy
		@param bufferStartIndex the index of the columnValues array
		to start at.
		*/	
	public void getColumn(Table tbl, double[] columnValues, int columnIndex,
		int bufferStartIndex){

		int numRows = tbl.getNumRows();
		for(int i = 0; i < numRows; i++){
			columnValues[i + bufferStartIndex] = tbl.getDouble(i, columnIndex);
		}
	}

	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return 	
			"Copies a table into an array of doubles. "+
			"rowVsCol: if true, copies by row, if false, by column"+
			""+
			"";
	}
	
   	public String getModuleName() {
		return "FlattenTable";
	}
	public String[] getInputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.table.Table"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: 
				return "A table (of numerical values)";
			default: return "No such input";
		}
	}
	
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Table";
			default: return "No such input";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {
			"[D"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: 
				return "A 1 dimensional copy of the table's data ";
			case 1:
				return "";
			case 2:
				return "";
			default: return "No such output";
		}
	}
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Double Vector (Array)";
			case 1:
				return "";
			case 2:
				return "";
			default: return "No such output";
		}
	}		
	////////////////////////////////
	//D2K Property get/set methods
	///////////////////////////////
	public void setDebug(boolean b){
		debug = b;
	}
	public boolean getDebug(){
		return debug;
	}
	public boolean getRowVsCol(){
		return rowVsCol;
	}
	public void setRowVsCol(boolean b){
		rowVsCol = b;
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
			
					

			

								
	
