package ncsa.d2k.modules.projects.pgroves.geostat;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.util.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;


/**
	Creates a table of values out of a BivariateModel for
	plotting in a FunctionPlot. this only produces the inputs,
	the outputs will need to be predicted


	@author pgroves
	@date 04/18/04
	*/

public class CreateFunctionTable extends DataPrepModule 
	implements java.io.Serializable {

	//////////////////////
	//d2k Props
	////////////////////
	
	boolean debug=false;	
	
	/**the number of points to generate between min and max of the
	input feature in the training data*/
	int resolution = 100;	

	boolean startFromOrigin = true;
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
		ExampleTable trainData = (ExampleTable)pullInput(0);

		int input = trainData.getInputFeatures()[0];

		//range: [0] = min, [1] = max
		double[] rng = TableUtilities.getMinMax(trainData, input);
		if(this.startFromOrigin){
			rng[0] = 0;
		}


		//increment
		double inc = (rng[1] - rng[0]) / ((double)resolution);
		if(debug){
			System.out.println(this.getAlias() + ": Min:" + rng[0] + 
			"; Max:" + rng[1] + "; inc:" + inc);
		}

		MutableTableImpl funMT = new MutableTableImpl(1);
		funMT.setColumn(new DoubleColumn(resolution), 0);
		
		ExampleTableImpl funET = new ExampleTableImpl(funMT);
		
		int[] inputCols = new int[1];
		inputCols[0] = 0;
		funET.setInputFeatures(inputCols);

		//might as well make every row a test example, as we're
		//going to require that the outputs be predicted
		int[] testSet = new int[resolution];

		double d;
		for(int i = 0; i < resolution; i++){
			d = rng[0] + (((double)i) * inc);
			funET.setDouble(d, i, 0);
			testSet[i] = i;
		}
		funET.setTestingSet(testSet);

		funET.setColumnLabel(trainData.getColumnLabel(input), 0);

		pushOutput(funET, 0);
		

		
	}
		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return 	
			"Produces an example table with only a single input column based on"+
			" the first input column of the passed in training data. The point"+
			" is predict the outputs down the line for an evenly spaced "+
			" set of inputs so that the function can then be plotted." +
			" The property called 'resolution' sets how many rows will be "+
			"in the generated table. 'startFromOrigin' determines if the " +
			" input values will start at zero or the min of the data.";
	}
	
   	public String getModuleName() {
		return "CreateFunctionTable";
	}
	public String[] getInputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: 
				return "An example table that contains the input for some "+
				"bivariate function. The output table will contain values "+
				"between the min and max of the first input column." +
				" Normally, this will be the training table that the model"+
				" to be visualized was trained on (but this is not required).";
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
				return "Training Data";
			case 1:
				return "";
			case 2:
				return "";
			default: return "No such input";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: 
				return "An example table with only one column. It is an "+
				"input column of length 'resolution' with the same label "+
				"as the first input column of the passed in table. Predictions"+
				" should be made using the model that is to be visualized.";
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
				return "UnevaluatedFunction Table";
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
	public int getResolution(){
		return resolution;
	}
	public void setResolution(int d){
		resolution = d;
	}
	public boolean getStartFromOrigin(){
		return startFromOrigin;
	}
	public void setStartFromOrigin(boolean b){
		startFromOrigin=b;
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
			
					

			

								
	
