package ncsa.d2k.modules.projects.pgroves.util;


import ncsa.d2k.core.modules.*;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

/**
 * Divides every value in a column of a table by a constant.
 * The column index is an input, the constant is too.

	@author pgroves
	@date 05/13/04
*/

public class DivideColumnByConstant extends ComputeModule 
	implements java.io.Serializable {

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
		
		MutableTable mt = (MutableTable)pullInput(0);
		int colIdx = ((Integer)pullInput(1)).intValue();
		double divisor = ((Double)pullInput(2)).doubleValue();
		
		if(debug){
			System.out.println(getAlias()+": Column:" + colIdx +
					"--" + mt.getColumnLabel(colIdx) + " Divisor:" + divisor);
		}

		int numRows = mt.getNumRows();
		double d;
		for(int i = 0; i < numRows; i++){
			d = mt.getDouble(i, colIdx);
			d /= divisor;
			mt.setDouble(d, i, colIdx);
		}
		pushOutput(mt, 0);
		
	}
		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return 	
			"Divides every value in a column by a divisor. "+
			""+
			"";
	}
	
   	public String getModuleName() {
		return "DivideColumnByConstant";
	}
	public String[] getInputTypes(){
		String[] types = {
			"ncsa.d2k.module.core.datatype.table.MutableTable",
			"java.lang.Integer",
			"java.lang.Double"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: 
				return "The table to transform";
			case 1: 
				return "The column index (starting at zero) of the column" +
					" in the table to transform";
			case 2: 
				return "The value that every entry in the column will be divided" +
					" by.";
			default: return "No such input";
		}
	}
	
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Table";
			case 1:
				return "Column Index";
			case 2:
				return "Divisor";
			default: return "No such input";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.MutableTable"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: 
				return "The same table that was passed in with one column altered.";
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
				return "Altered Table";
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
			
					

			

								
	
