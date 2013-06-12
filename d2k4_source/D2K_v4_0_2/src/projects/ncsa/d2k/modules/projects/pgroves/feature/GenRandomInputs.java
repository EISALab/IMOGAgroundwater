package ncsa.d2k.modules.projects.pgroves.feature;

import java.util.Random;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.util.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;


/**

	Generates a table of input features, based on an input table, that
	has random data that is within the range of the features of the
	original table.


	@author pgroves
	@date 02/05/04
	*/

public class GenRandomInputs extends ComputeModule 
	implements java.io.Serializable {

	//////////////////////
	//d2k Props
	////////////////////
	
	boolean debug = false;		

	boolean alwaysReset = true;
	/////////////////////////
	/// other fields
	////////////////////////

	int randSeed = 5465325;
	
	Random rand;
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
		rand = null;
	}

	/**
		resets the random number generator to the current seed
		*/
	public void resetRand(){	
		rand = new Random((long)randSeed);
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
		ExampleTable et = (ExampleTable)pullInput(0);
		int randRows = ((Integer)pullInput(1)).intValue();
		
		if(rand == null || alwaysReset){
			resetRand();
		}
		pushOutput(genRandomInputs(et, randRows), 0);
	}

	public ExampleTableImpl genRandomInputs(ExampleTable et, int numRandRows){
		int[] inputs = et.getInputFeatures();
		int[] newInputs = new int[inputs.length];
		Column[] cols = new Column[inputs.length];

		if(debug){
			System.out.println(this.getAlias() + " NumRandomExamples:" + 
					numRandRows + " NumInputs:" + inputs.length);
		}
				
		int i, j;
		double d;
		for(i = 0; i < inputs.length; i++){
			Column col = ColumnUtilities.createColumn(
				et.getColumnType(inputs[i]), numRandRows);
			col.setLabel(et.getColumnLabel(inputs[i]));
			double[] range = TableUtilities.getMinMax(et, inputs[i]);
			for(j = 0; j < numRandRows; j++){
				d = getRand(range[0], range[1]);
				col.setDouble(d, j);
			}
			cols[i] = col;
			newInputs[i] = i;
		}
		ExampleTableImpl etn = new ExampleTableImpl(new MutableTableImpl(cols));
		etn.setInputFeatures(newInputs);
		return etn;
	}
		
	/**
		get a random double within the specified range (between max and min)
		*/	
	private double getRand(double min, double max){	
		double d = rand.nextDouble();
		d *= (max - min);
		d += min;
		return d;
	}
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return 	
			"Given an Example Table, generates N rows of input features. "+
			"The new values will be uniformly distributed within the range"+
			" of the original corresponding features. The output table will"+
			" have as many columns as the original table has input columns." + 
			" The input  features set of the new table will be set to include"+
			" all of its columns.";
	}
	
   	public String getModuleName() {
		return "GenRandomInputs";
	}
	public String[] getInputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.table.ExampleTable",
			"java.lang.Integer"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: 
				return "The orginal table";
			case 1: 
				return "The number of random rows to produce";
			case 2: 
				return "";
			default: return "No such input";
		}
	}
	
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Original Table";
			case 1:
				return "Random Row Count";
			case 2:
				return "";
			default: return "No such input";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.table.basic.ExampleTableImpl"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: 
				return "An ExampleTable with RandomRowCount rows, and only"+
				" input features";
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
				return "Random Input Features";
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
	
	public boolean getAlwaysReset(){
		return alwaysReset;
	}
	public void setAlwaysReset(boolean b){
		alwaysReset = b;
	}
	public int getRandomSeed(){
		return randSeed;
	}
	public void setRandomSeed(int i){
		randSeed = i;
		resetRand();
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
			
					

			

								
	
