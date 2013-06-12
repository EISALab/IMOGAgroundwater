package ncsa.d2k.modules.projects.pgroves.optimize;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.parameter.impl.*;

/**
	Takes in an exampletable and creates a search space
	over the examples. The size of the space is therefore equal
	to the number of rows in the table. 
	
	@author pgroves
	@date 02/11/04
	*/

public class GenExampleSubsetSpace extends DataPrepModule 
	{

	//////////////////////
	//d2k Props
	////////////////////
	

	boolean debug=true;		
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
		Table et = (Table)pullInput(0);
		int numExamples = et.getNumRows();
		if(debug){
			System.out.println(getAlias()+": Num Examples/ Num Parameters = " +
			numExamples);
		}

		ParameterSpaceImpl pSpace = new ParameterSpaceImpl();
		
		String[] names = new String[numExamples];
		double[] maxs = new double[numExamples];
		double[] mins = new double[numExamples];
		double[] defaults = new double[numExamples];
		int[] ress = new int[numExamples];
		int[] types = new int[numExamples];

		for(int i = 0; i < numExamples; i++){
			names[i] = "Row:"+i;
			maxs[i] = 1.0;
			mins[i] = 0.0;
			defaults[i] = 1.0;
			ress[i] = 2;
			types[i] = ColumnTypes.BOOLEAN;
		}
		
		pSpace.createFromData(names, mins, maxs, defaults, ress, types);
		if(debug){
			System.out.println(getAlias()+": Num Parameters In Space = " +
			pSpace.getNumParameters());
		}
	
		pushOutput(pSpace, 0);
	}
		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return "Generates a search space over the examples (rows) of an "+
		"ExampleTable. (The space states that each example can be used "+
		"(true) or unused (false)).";
	}
	
   	public String getModuleName() {
		return "GenExampleSubsetSpace";
	}
	public String[] getInputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.table.Table"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: return "";
			default: return "No such input";
		}
	}
	
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Table";
			default: return "NO SUCH INPUT!";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {
		"ncsa.d2k.modules.core.datatype.parameter.impl.ParameterSpaceImpl"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: return "A ParameterSpace where every parameter is a boolean"+
			" value indicating whether to use (true) or not use (false) a "+
			"row of the Table.";
			default: return "No such output";
		}
	}
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Example Subset Space";
			default: return "NO SUCH OUTPUT!";
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
			
					

			

								
	
