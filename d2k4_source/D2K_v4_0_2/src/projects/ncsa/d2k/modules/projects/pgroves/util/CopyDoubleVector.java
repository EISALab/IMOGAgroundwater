package ncsa.d2k.modules.projects.pgroves.util;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;


/**	
	Takes a table and returns either a row or a column as a double
	array


	@author pgroves
	@date 02/05/04
	*/

public class CopyDoubleVector extends DataPrepModule 
	implements java.io.Serializable {

	//////////////////////
	//d2k Props
	////////////////////
	
	boolean debug=false;
	
	// if true, copies a row, if false, a column
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
		int index = ((Integer)pullInput(1)).intValue();

		double[] dd;
		if(rowVsCol){
			dd = new double[tbl.getNumColumns()];
			this.getRow(tbl, dd, index);
		}else{
			dd = new double[tbl.getNumRows()];
			this.getColumn(tbl, dd, index);
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
		*/	
	public void getRow(Table tbl, double[] rowValues, int rowIndex ){

		int numCols = tbl.getNumColumns();
		for(int i = 0; i < numCols && i < rowValues.length; i++){
			try{
			rowValues[i] = tbl.getDouble(rowIndex, i);
			}catch(Exception e){
				e.printStackTrace();
				System.out.println("Row: "+rowIndex+ ", colIdx:"+ i +", colname:"+
					tbl.getColumnLabel(i)+", numColsTotal:"+numCols+
					"vectorArrayLength:"+rowValues.length);
				return;
			}
					
		}
	}
			 
	/**
		copies all the values in a column in a table into a double array
		by calling getDouble at every row (for the appropriate
		column). If the buffer double array is not big enough to accept
		the entire column, the buffer will be filled as much as possible
		and returned.
		

		@param tbl a table
		@param columnValues a double array buffer that the that values will 
		be copied into
		@param columnIndex which column from tbl to copy
		*/	
	public void getColumn(Table tbl, double[] columnValues, int columnIndex ){

		int numRows = tbl.getNumRows();
		for(int i = 0; i < numRows && i < columnValues.length; i++){
			columnValues[i] = tbl.getDouble(i, columnIndex);
		}
	}

	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return 	
			"Copies either a row or column into an array of doubles. "+
			"rowVsCol: if true, copies a row, if false, a column"+
			""+
			"";
	}
	
   	public String getModuleName() {
		return "CopyDoubleVector";
	}
	public String[] getInputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.table.Table",
			"java.lang.Integer"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: 
				return "A table (of numerical values)";
			case 1: 
				return "The index of the row or column to copy";
			case 2: 
				return "";
			default: return "No such input";
		}
	}
	
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Table";
			case 1:
				return "Index";
			case 2:
				return "";
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
				return "A copy of the row or column";
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
			
					

			

								
	
