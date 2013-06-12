package ncsa.d2k.modules.projects.pgroves.optimize;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;


/**
 * Calculates the errors for numeric prediction columns in 
 * a test table.
 *@author pgroves
 *@date 04/19/04
 */

public class CalculateErrors extends ComputeModule 
	implements java.io.Serializable {

	//////////////////////
	//d2k Props
	////////////////////
	
	boolean absoluteValue = true;
	
	boolean debug = false;		
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
	/**
		does it
	*/
	public void doit() throws Exception{
		if(debug){
			System.out.println(getAlias()+":Firing");
		}
		PredictionTable tt = (PredictionTable)pullInput(0);

		int[] outputs = tt.getOutputFeatures();
		int[] preds = tt.getPredictionSet();

		int numCols = preds.length;
		int numRows = tt.getNumRows();

		Column[] cols = new Column[numCols];
		double[] errVals;
		for(int i = 0; i < numCols; i++){
			errVals = new double[numRows];
			for(int j = 0; j < numRows; j++){
				errVals[j] = tt.getDouble(j, outputs[i]);
				errVals[j] -= tt.getDouble(j, preds[i]);
				if(absoluteValue && errVals[j] < 0){
					errVals[j] *= -1.0D;
				}
				if(debug){
					System.out.println("features:" + i + "row:" + j + " target:" +
						tt.getDouble(j, outputs[i]) +
						" prediction:" + tt.getDouble(j, preds[i]) +
						" error:" + errVals[j]);
				}
			}
			cols[i] = new DoubleColumn(errVals);
			cols[i].setLabel(tt.getColumnLabel(outputs[i]) + ":Error");
		}
		MutableTableImpl mt = new MutableTableImpl(cols);
		pushOutput(mt, 0);
	}
					
	///////////////////	
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return 	
			"Calculates the errors of every example in a test table. If "+
			" 'absoluteValue' prop is true, the values will be the "+
			"absolute values of the true value minus the prediction."+
			"";
	}
	
   	public String getModuleName() {
		return "CalculateErrors";
	}
	public String[] getInputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.table.PredictionTable"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: 
				return "A test table with the predictions filled in.";
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
				return "Test Table With Predictions";
			case 1:
				return "";
			case 2:
				return "";
			default: return "No such input";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.table.impl.MutableTableImpl"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: 
				return "A table with a column for every output feature of " +
					"the input test table. The values are the actual outputs " +
					"minus the predictions, except when 'absoluteValue' is true";
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
				return "Error Columns";
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
	public boolean getAbsoluteValue(){
		return absoluteValue;
	}
	public void setAbsoluteValue(boolean b){
		absoluteValue=b;
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
			
					

			

								
	
