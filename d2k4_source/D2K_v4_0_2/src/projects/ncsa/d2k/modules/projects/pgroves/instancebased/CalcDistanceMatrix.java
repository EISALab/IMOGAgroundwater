package ncsa.d2k.modules.projects.pgroves.instancebased;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;


/**
	Given an ExampleTable, calculates the euclidean distance between all sets
	of points in the input feature space. Intended to be used <i>before</i>
	cross-validation, so that the distance matrix may be cached and reused.


	@author pgroves
	@date 01/14/04
	*/

public class CalcDistanceMatrix extends ComputeModule 
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

		ExampleTable dataTable = (ExampleTable)pullInput(0);
		int numExamples = dataTable.getNumRows();
		int[] inputs = dataTable.getInputFeatures();
		int numInputs = inputs.length;

		MutableTableImpl dists = new MutableTableImpl(numExamples);

		int i,j,k;
		double d,c;
		for(i=0; i<numExamples; i++){
			//make the next column
			dists.setColumn(new DoubleColumn(numExamples), i);
			dists.setColumnLabel("DistTo:"+i, i);

			//set the distance to itself to zero
			dists.setDouble(0.0, i, i);

			for(j=0; j<i; j++){
				d = 0;
				for(k=0; k<numInputs; k++){
					c = dataTable.getDouble(i, inputs[k]);
					c -= dataTable.getDouble(j, inputs[k]);
					d += c*c;
				}
				d = Math.sqrt(d);
				//The matrix is symmetric
				dists.setDouble(d,i,j);
				dists.setDouble(d,j,i);
			}
		}
		pushOutput(dists,0);
	}
		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return 	
			"Given an ExampleTable, calculates the euclidean distance between all"+
			" sets of points in the input feature space. Intended to be used "+
			"<i>before</i> cross-validation, so that the distance matrix may be "+
			"cached and reused.";
	}
	
   	public String getModuleName() {
		return "CalcDistanceMatrix";
	}
	public String[] getInputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: 
				return "An example table with the input features selected";
			default: return "No such input";
		}
	}
	
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Example Table";
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
				return "A table representing the distance matrix. The entry at "+
				"row i and column j is the euclidean distance between the "+
				"examples "+
				"in row i and row j of the input ExampleTable when considering the"+
				" input features only."  ;
			default: return "No such output";
		}
	}
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Distance Matrix";
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
			
					

			

								
	
