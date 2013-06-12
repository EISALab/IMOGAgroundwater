package ncsa.d2k.modules.projects.pgroves.util;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.util.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;


/**
	Takes an array of tables and puts all their data into one big
	table. Can treat every table as a set of rows in the big
	table or as a set of columns.

	@author pgroves
	@date 03/29/04
	*/

public class ConcatTableArray extends DataPrepModule 
	implements java.io.Serializable {

	//////////////////////
	//d2k Props
	////////////////////
	
	boolean debug = false;		

	/**if true, the tables will be appended by row (all tables
	 * must have the same number and type of columns). if false,
	 * the tables will be appended as new sets of columns, in which
	 * case there are no restrictions on the types, or even
	 * sizes of columns.
	 */
	boolean rowVsColumn = true;
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

		Table[] rawTables = (Table[])pullInput(0);

		MutableTableImpl catTable = null;
		if(rowVsColumn){
			catTable = concatenateByRow(rawTables);
		}else{
			catTable = concatenateByColumn(rawTables);
		}
		pushOutput(catTable, 0);
	}
		
	/**
	 * concatenates a set of tables together, treating each
	 * table as a set of rows in the new table. that is, 
	 * all tables must have the same number and types of 
	 * column(s), and the n1 rows of table 1 will be the
	 * first n1 rows of the new table, etc.
	 *
	 * @param tbls a set of tables with compatible row types
	 * @return a new copy of the data in tbls
	 */
	public MutableTableImpl concatenateByRow(Table[] tbls){
		int totalNumRows = 0;
		int numTables = tbls.length;

		int i, j, k;

		for(i = 0; i < numTables; i++){
			totalNumRows += tbls[i].getNumRows();
		}

		int numCols = tbls[0].getNumColumns();
		Column[] cols = new Column[numCols];
		
		for(i = 0; i < numCols; i++){
			cols[i] = ColumnUtilities.createColumn(
					tbls[0].getColumnType(0), totalNumRows);
			cols[i].setLabel(tbls[0].getColumnLabel(i));
		}
		//<r>eturned <m>utable <t>able
		MutableTableImpl rmt = new MutableTableImpl(cols);

		int numRows;
		//index into rmt
		k = 0;
		for(i = 0; i < numTables; i++){
			numRows = tbls[i].getNumRows();
			for(j = 0; j < numRows; j++){
				TableUtilities.setRow(tbls[i], j, rmt, k);
				k++;
			}
		}
		return rmt;
	}

	/**
	 * concatenates a set of tables together, treating each
	 * table as a set of columns in the new table. The
	 * number of rows of the columns in the new table can
	 * therefore be different, and no error/warning will
	 * be given. It's probably a bad idea to do that, however.
	 * 
	 * @param tbls a set of tables with (recommended but not required)
	 * 	the same number of rows
	 * @return a new copy of the data in tbls

	 */
	public MutableTableImpl concatenateByColumn(Table[] tbls){
		int totalNumCols = 0;
		int numTables = tbls.length;
		int i, j, k;
		for(i = 0; i < numTables; i++){
			totalNumCols += tbls[i].getNumColumns();
		}
		Column[] cols = new Column[totalNumCols];
		int numCols;
		k = 0;
		for(i = 0; i < numTables; i++){
			numCols = tbls[i].getNumColumns();
			for(j = 0; j < numCols; j++){
				cols[k] = ColumnUtilities.copyColumn(tbls[i], j);
				k++;
			}
		}
		MutableTableImpl rmt = new MutableTableImpl(cols);
		return rmt;

	}
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return 	
			"Concatenates a collection (array) of tables into a single"+
			" mutable table. This can be done by row (rowVsCol = true)"+
			" or by column (rowVsCol = false). If by row, all tables"+
			" must have the same number of columns, and they must be" +
			" of the same type. If by column, there are no restrictions, " +
			"although it is generally a bad idea to create a table where"+
			" the different columns have different numbers of rows.";
	}
	
  	public String getModuleName() {
		return "ConcatTableArray";
	}
	public String[] getInputTypes(){
		String[] types = {
			"[Lncsa.d2k.modules.core.datatype.table.Table:"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: 
				return "An array of tables.";
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
				return "Tables";
			case 1:
				return "";
			case 2:
				return "";
			default: return "No such input";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.table.basic.MutableTableImpl"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: 
				return "A single table with all of the values from the"+
					" input tables copied in.";
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
				return "Concat Table";
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
		debug=b;
	}
	public boolean getDebug(){
		return debug;
	}
	public boolean getRowVsColumn(){
		return rowVsColumn;
	}
	public void setRowVsColumn(boolean b){
		rowVsColumn = b;
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
			
					

			

								
	
