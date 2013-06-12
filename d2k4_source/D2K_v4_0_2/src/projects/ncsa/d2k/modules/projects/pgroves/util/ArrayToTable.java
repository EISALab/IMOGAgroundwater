package ncsa.d2k.modules.projects.pgroves.util;

import java.lang.reflect.Array;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;


/**
	takes in an array and makes either a table with
	one row or one column. which one is determined
	by a property


	@author pgroves
	@date 02/13/04
	*/

public class ArrayToTable extends DataPrepModule 
	implements java.io.Serializable {

	//////////////////////
	//d2k Props
	////////////////////
	
	boolean debug=false;	
	
	boolean rowVsColumn = false;	
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
		Object objs = (Object)pullInput(0);
		
		MutableTableImpl tbl;
		/*if(rowVsColumn){
			tbl = buildRowTable(objs);
		}else{
			tbl = buildColumnTable(objs);
		}*/

		int numRows;
		int numCols;
		if(rowVsColumn){
			numRows = 1;
			numCols = Array.getLength(objs);
		}else{
			numRows = Array.getLength(objs);
			numCols = 1;
		}
		Column[] cols = new Column[numCols];

		int i, j, k;
		int type = getArrayColumnType(objs);

		//index into the object array
		k = 0;
		for(i = 0; i < numCols; i++){
			cols[i] = ColumnUtilities.createColumn(type, numRows);
			cols[i].setLabel("Column:"+i);
			for(j = 0; j < numRows; j++){
				cols[i].setObject(Array.get(objs, k), j);
				k++;
			}
		}
		tbl = new MutableTableImpl(cols);
			
		pushOutput(tbl, 0);
	}

	/**
		given an array, returns the appropriate ColumnType. should
		be moved to ColumnTypes or ColumnUtilties

		@param obj an array, passed as an object so that primitive
		types will work
		@return the ColumnType identifier
	*/
	public static int getArrayColumnType(Object obj){
		Class cls = obj.getClass();
		if(!cls.isArray()){
			System.out.println("Not a valid array");
			return -1;
		}
		Class compCls = cls.getComponentType();
				
		
		if(compCls == Double.TYPE){
			return ColumnTypes.DOUBLE;
			
		}else if (compCls == Integer.TYPE){
			return ColumnTypes.INTEGER;
			
		}else if (compCls == Boolean.TYPE){
			return ColumnTypes.BOOLEAN;
			
		}else if (compCls == Character.TYPE){
			return ColumnTypes.CHAR;
			
		}else if (compCls == Byte.TYPE){
			return ColumnTypes.BYTE;
			
		}else if (compCls == Short.TYPE){
			return ColumnTypes.SHORT;
			
		}else if (compCls == Long.TYPE){
			return ColumnTypes.LONG;
			
		}else if (compCls == Float.TYPE){
			return ColumnTypes.FLOAT;
		
		}else if (compCls == (new String()).getClass()){
			return ColumnTypes.STRING;
		
		}else if (compCls == (new byte[0]).getClass()){
			return ColumnTypes.BYTE_ARRAY;
			
		}else if (compCls == (new char[0]).getClass()){
			return ColumnTypes.CHAR_ARRAY;
			
		}else{
			return ColumnTypes.OBJECT;
		}
		
	}
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return 	
			"Given an array of objects, creates a table (of Objects) that"+
			" has either one row or one column. if rowVsCol is true, then"+
			" one row, else one column."+
			"";
	}
	
   	public String getModuleName() {
		return "ArrayToTable";
	}
	public String[] getInputTypes(){
		String[] types = {
			"[java.lang.Object:"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: 
				return "An array of objects";
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
				return "Array";
			case 1:
				return "";
			case 2:
				return "";
			default: return "No such input";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {
			"ncsa.d2k.modules.datatype.table.basic.MutableTableImpl"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: 
				return "A 1-D Table";
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
				return "Table";
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
			
					

			

								
	
