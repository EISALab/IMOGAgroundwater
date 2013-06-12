package ncsa.d2k.modules.projects.pgroves.util;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;


/**
	Given a table, splits it into two tables based on the column split
	index (a property).

	@author pgroves
	@date 02/03/04
	*/

public class SplitTable extends DataPrepModule 
	implements java.io.Serializable {

	//////////////////////
	//d2k Props
	////////////////////
	
	boolean debug=false;		

	int columnCount = 1;
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

		MutableTableImpl[] mts = splitTable(tbl, columnCount);
		pushOutput(mts[0], 0);
		pushOutput(mts[1], 1);
		
	}

	/**
		Splits (by column) a generic table into two new tables. If the
		columnCount is positive, the first table will contain the first
		columnCount columns from the original table. If negative, 
		the second table will contain the last columnCount columns from the
		original table. In either case, all remaining columns go to
		the other table.

		@param tbl A Table 
		@param columnCount dictates how many columns to put into the first
		returned table (if positive) or into the second table (if negative).

		@return an array of tables of length two
	*/

	public MutableTableImpl[] splitTable(Table tbl, int columnCount){
		
		int numTotalCols = tbl.getNumColumns();
		
		//The first index of the second table - ColumnSplitIndex
		int csi = 0;
		
		if(columnCount > 0){
			csi = columnCount;
		}else if(columnCount < 0){
			csi = numTotalCols + columnCount;
		}else{
			//i guess it was zero, put all columns in the first table
			//the second table will be created, but have zero columns
			csi = numTotalCols;
		}
		if(csi > numTotalCols){
			//something's wrong, just return all the columns in the first
			//table
			System.out.println(getAlias() + ": WARNING: column subset size(" +
			columnCount + ") is greater than number of columns (" + 
			numTotalCols + "). All columns placed in first returned table.");
			csi = numTotalCols;
		}
		
		int[] numCols = new int[2];
		
		//the first table's column count
		numCols[0] = csi;
		//the second table's column count
		numCols[1] = numTotalCols - csi;

		MutableTableImpl[] mts = new MutableTableImpl[2];

		int i, j, k;
		//index into original table
		j=0;

		if(debug){
			System.out.println("First Table- numcols:" + numCols[0]);
			System.out.println("Second Table- numcols:" + numCols[1]);
			System.out.println("csi:" + csi);
		}	
		
		for(i = 0; i < 2; i++){
			mts[i] = new MutableTableImpl(numCols[i]);
			for(k = 0; k < numCols[i]; k++){
				Column col = ColumnUtilities.copyColumn(tbl, j);
				mts[i].setColumn(col, k);
				j++;
			}
		}
		return mts;
	}
				
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return 	
			"If columnCount is positive, that many columns will be in the"+
			" first table, and those columns will be columns 1->columnCount."+
			" If it is negative, it is how many columns will be in the second"+
			" table, and they will be (totalCols-columnCount)-> totalCols."+
			" Two new MutableTableImpls will be returned regardless, and "+
			"no attempt is made to conserve any meta data (train set, " +
			" inputfeatures, etc.";
	}
	
   	public String getModuleName() {
		return "SplitTableAtColumn";
	}
	public String[] getInputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.table.Table"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: 
				return "A Table to Split";
			case 1: 
				return "";
			case 2: 
				return "";
			default: return "No such input";
		}
	}
	
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Original Table";
			case 1:
				return "";
			case 2:
				return "";
			default: return "No such input";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.table.basic.MutableTableImpl",
			"ncsa.d2k.modules.core.datatype.table.basic.MutableTableImpl"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: 
				return "The columns from the first half of  Original Table.";
			case 1:
				return "The columns from the second half of Original Table";
			case 2:
				return "";
			default: return "No such output";
		}
	}
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Table 1";
			case 1:
				return "Table 2";
			case 2:
				return "";
			default: return "No such output";
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
	public int getColumnCount(){
		return columnCount;
	}
	public void setColumnCount(int i){
		columnCount=i;
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
			
					

			

								
	
