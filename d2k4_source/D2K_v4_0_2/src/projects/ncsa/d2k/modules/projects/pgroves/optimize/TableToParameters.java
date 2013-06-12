package ncsa.d2k.modules.projects.pgroves.optimize;


import ncsa.d2k.core.modules.*;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.impl.*;


/**
	Converts a table into an array of ParameterPoint objects

	@author pgroves
*/

public class TableToParameters extends DataPrepModule 
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
		if(debug){
			System.out.println(getAlias()+":Firing");
		}
		Table tbl = (Table)pullInput(0);
		int numRows = tbl.getNumRows();
		int numCols = tbl.getNumColumns();
		String[] labels = new String[numCols];
		if(debug){
			System.out.println(getAlias()+": NumRows, NumPoints:" + numRows +
			" NumcCols, NumParams:" + numCols);
		}

		int i, j;
		for(i = 0; i < numCols; i++){
			labels[i] = tbl.getColumnLabel(i);
		}

		double[] vals;
		ParameterPoint[] ppa = new ParameterPoint[numRows];
		
		for(i = 0; i < numRows; i++){
			vals = new double[numCols];
			for(j = 0; j < numCols; j++){
				vals[j] = tbl.getDouble(i, j);
			}
			ppa[i] = ParameterPointImpl.getParameterPoint(labels, vals);
			if(debug){
				System.out.println(ppa[i]);
			}
		}
		pushOutput(ppa, 0);

	}
		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return 	
			"Converts a table of genes into an array of ParamterPoints"+
			""+
			""+
			"";
	}
	
   	public String getModuleName() {
		return "TableToParameters";
	}
	public String[] getInputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.table.Table"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: 
				return "A table where every row can be translated to a " +
				" ParameterPoint";
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
				return "Parameter Table";
			case 1:
				return "";
			case 2:
				return "";
			default: return "No such input";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {
			"[Lncsa.d2k.modules.core.datatype.parameter.ParameterPoint"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: 
				return "An array of ParameterPoints.";
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
				return "ParameterPoint Array";
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
	public boolean get()

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
			
					

			

								
	
