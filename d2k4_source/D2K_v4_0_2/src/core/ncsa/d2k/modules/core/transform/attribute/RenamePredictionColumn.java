package ncsa.d2k.modules.core.transform.attribute;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

/*
	Renames the first prediction column in a prediction
	table to the value given in the properties

	@author pgroves
	*/

public class RenamePredictionColumn extends ComputeModule
	{

	//////////////////////
	//d2k Props
	////////////////////

	protected String newPredictionName="Prediction-1";
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
		PredictionTableImpl pt=(PredictionTableImpl)pullInput(0);
		pt.setColumnLabel(newPredictionName, pt.getPredictionSet()[0]);
		pushOutput(pt, 0);

	}


	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return "<html>  <head>      </head>  <body>    Renames the first column of predictions to the name given in     propertiesPROPS: newPredictionName - the new column label  </body></html>";
	}

   	public String getModuleName() {
		return "Rename Prediction Column";
	}
	public String[] getInputTypes(){
		String[] types = {"ncsa.d2k.modules.core.datatype.table.PredictionTable"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: return "The table w/ a column of predictions";
			default: return "No such input";
		}
	}

	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Table w/ Predictions";
			default: return "NO SUCH INPUT!";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {"ncsa.d2k.modules.core.datatype.table.PredictionTable"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: return "The input table w/ the prediction column renamed";
			default: return "No such output";
		}
	}
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Table w/ Renamed Predictions";
			default: return "NO SUCH OUTPUT!";
		}
	}
	////////////////////////////////
	//D2K Property get/set methods
	///////////////////////////////
	public String getNewPredictionName(){
		return newPredictionName;
	}
	public void setNewPredictionName(String s){
		newPredictionName=s;
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
	*/
}







