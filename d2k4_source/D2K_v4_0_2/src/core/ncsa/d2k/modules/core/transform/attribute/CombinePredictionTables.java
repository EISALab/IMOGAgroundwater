package ncsa.d2k.modules.core.transform.attribute;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
/*
	takes multiple PredictionTables, puts all the columns
	of prediction in the first one and returns it.
	does not update the "predictionSet" field in any way


	@author pgroves
	*/

public class CombinePredictionTables extends ComputeModule
	{

	//////////////////////
	//d2k Props
	////////////////////
	int tablesToWaitFor=4;
	
	boolean makeCollectedColumnsPredictionSet = false;

	boolean debug = false;
	/////////////////////////
	/// other fields
	////////////////////////

	int tablesIn=0;

	PredictionTableImpl[] tables;

	//////////////////////////
	///d2k control methods
	///////////////////////

	public boolean isReady(){
		return super.isReady();
	}
	public void endExecution(){
		clearFields();
		super.endExecution();
	}
	public void beginExecution(){
		super.beginExecution();
	}

	protected void clearFields(){
		tablesIn=0;
		tables=null;
	}

	/////////////////////
	//work methods
	////////////////////
	/*
		does it
	*/
	public void doit() throws Exception{
		if(tablesIn==0){
			tables=new PredictionTableImpl[tablesToWaitFor];
			if(debug)
				System.out.println(getAlias() + ": PredictionTable Array Init");
		}
		tables[tablesIn]=(PredictionTableImpl)pullInput(0);
		tablesIn++;

		if(debug)
			System.out.println(getAlias() + ": Table In:" + tablesIn);

		if(tablesIn==tablesToWaitFor){
			pushOutput(makeMasterTable(), 0);
			clearFields();

		}

	}

	protected PredictionTableImpl makeMasterTable(){
		PredictionTableImpl master=tables[0];
		int[] predictionSet=master.getPredictionSet();
		int addedPredCount=0;

		for(int i=1; i<tablesToWaitFor; i++){
			addedPredCount+=tables[i].getPredictionSet().length;
		}
		//int[] newPredSet=new int[addedPredCount+predictionSet.length];
		Column[] newInternal=new Column[master.getNumColumns()+addedPredCount];
		if(debug)
			System.out.println(getAlias() + ": Making Table with --" + 
				newInternal.length + "-- columns");
		//put the old columns in the new columnarray
		for(int i=0; i<master.getNumColumns(); i++){
			newInternal[i]=master.getColumn(i);
		}
		int newPredSet[] = new int[addedPredCount + predictionSet.length];
		int predSetIdx = 0;
		for(int i = 0; i < predictionSet.length; i++){
			newPredSet[predSetIdx] = predictionSet[i];
			predSetIdx++;
		}
		//put in the new prediction columns
		int internalIndex=master.getNumColumns();
		for(int i=1; i<tablesToWaitFor;i++){
			for(int j=0; j<tables[i].getPredictionSet().length; j++){
				newInternal[internalIndex]=tables[i].getColumn(
											tables[i].getPredictionSet()[j]);
				newPredSet[predSetIdx] = internalIndex;
				predSetIdx++;
				internalIndex++;
			}
		}
		master.setColumns(newInternal);
		if(makeCollectedColumnsPredictionSet){
			master.setPredictionSet(newPredSet);
		}
		return master;
	}
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return "<html>  <head>      </head>  <body>    Takes in a number of Prediction Tables and puts all ofthe prediction     columns in one of them. Returns it.meant to be used when several models     were assigned tothe same problem and one master table is wanted foroutput     that holds all of the models' predictions  </body></html>";
	}

   	public String getModuleName() {
		return "Predictions Compiler";
	}
	public String[] getInputTypes(){
		String[] types = {"ncsa.d2k.modules.core.datatype.table.basic.PredictionTableImpl"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: return "The Prediction Tables that will be combined";
			default: return "No such input";
		}
	}

	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Tables w/ Predictions";
			default: return "NO SUCH INPUT!";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {"ncsa.d2k.modules.core.datatype.table.basic.PredictionTableImpl"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: return "One of the input tables, now holding all predictioncolumns";
			default: return "No such output";
		}
	}
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Compiled Predictions";
			default: return "NO SUCH OUTPUT!";
		}
	}
	////////////////////////////////
	//D2K Property get/set methods
	///////////////////////////////
	public boolean getDebug(){
		return debug;
	}
	public void setDebug(boolean b){
		debug = b;
	}

	public int  getTablesToWaitFor(){
		return tablesToWaitFor;
	}
	public void setTablesToWaitFor(int d){
		tablesToWaitFor=d;
	}
	public boolean getMakeCollectedColumnsPredictionSet(){
		return makeCollectedColumnsPredictionSet;
	}
	public void setMakeCollectedColumnsPredictionSet(boolean b){
		makeCollectedColumnsPredictionSet=b;
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







