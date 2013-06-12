package ncsa.d2k.modules.projects.pgroves.util;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.datatype.table.*;

/**
	Makes a new copy of the data that is 'visible' in an ExampleTable.
	Therefore, if it a subset, test or train table, the new table
	will only be as large as the subset. This copies all data by
	value. The test, train and subset sets are not copied.
	
	@author pgroves 05/07/04
	*/

public class VisibleETCopy extends DataPrepModule 
	implements java.io.Serializable{

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
		ExampleTable et=(ExampleTable)pullInput(0);

		ExampleTableImpl et2 = this.copyVisibleExampleTableData(et);
		
		pushOutput(et2, 0);

	}
		
	public static ExampleTableImpl copyVisibleExampleTableData(ExampleTable et){
		
		Column[] cols = new Column[et.getNumColumns()];
		for(int i = 0; i < et.getNumColumns(); i++){
			cols[i] = ColumnUtilities.deepCopyColumn(et, i);
		}

		ExampleTableImpl et2 = new ExampleTableImpl(new MutableTableImpl(cols));

		int[] inputColumns = et.getInputFeatures();
		int[] newins = new int[inputColumns.length];
		System.arraycopy(inputColumns, 0, newins, 0, inputColumns.length);
		et2.setInputFeatures(newins);

		int[] outputColumns = et.getOutputFeatures();
		int[] newouts = new int[outputColumns.length];
		System.arraycopy(outputColumns, 0, newouts, 0, outputColumns.length);
		et2.setOutputFeatures(newouts);

		if((et instanceof PredictionTable) && 
				(((PredictionTable)et).getPredictionSet() != null)){

			PredictionTable pt = (PredictionTable)et;
			int[] predictionColumns = pt.getPredictionSet();
			int[] newpreds = new int[predictionColumns.length];
			System.arraycopy(predictionColumns, 0, newpreds, 0, 
				predictionColumns.length);

			et2 = (ExampleTableImpl)et2.toPredictionTable();
			//the above line forces new, empty prediction columns to
			//be made, get rid of them
			//must start at the end so the removal of the first columns
			//doesn't change the indices of the last
			int[] bogusPredSet = ((PredictionTable)et2).getPredictionSet();
			for(int i = bogusPredSet.length - 1; i >= 0; i--){
				et2.removeColumn(bogusPredSet[i]);
			}
			((PredictionTable)et2).setPredictionSet(newpreds);
		}
		et2.setLabel(et.getLabel());
		et2.setComment(et.getComment());

		return et2;

	}
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return 
		"	Makes a new copy of the data that is 'visible' in an ExampleTable. " +
		" Therefore, if it a subset, test or train table, the new table" +
		" will only be as large as the subset. This copies all data by" +
		" value. The test, train and subset sets are not copied.";
	}
	
   	public String getModuleName() {
		return "VisibleETCopy";
	}
	public String[] getInputTypes(){
		String[] s= {
			"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
		return s;
	}

	public String getInputInfo(int index){
		switch (index){
			case(0): {
				return "An Example Table";
			}
			default:{
				return "No such input.";
			}
		}
	}
	
	public String getInputName(int index) {
		switch (index){
			case(0): {
				return "Original Table";
			}
			default:{
				return "No such input.";
			}
		}
	}
	public String[] getOutputTypes(){
		String[] s={"ncsa.d2k.modules.core.datatype.table.basic.ExampleTableImpl"};
		return s;
	}

	public String getOutputInfo(int index){
		switch (index){
			case(0): {
				return "A copy of the 'visible' values of the original table.";
			}case(1): {
				return "";
			}

			default:{
				return "No such output.";
			}
		}
	}
	public String getOutputName(int index) {
		switch (index){
			case(0): {
				return "Deep Copy";
			}case(1): {
				return "";
			}

			default:{
				return "No such output.";
			}
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
			
					

			

								
	
