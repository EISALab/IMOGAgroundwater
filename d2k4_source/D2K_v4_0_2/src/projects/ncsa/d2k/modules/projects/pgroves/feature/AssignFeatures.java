package ncsa.d2k.modules.projects.pgroves.feature;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.datatype.parameter.*;

/**
	Takes in a ExampleTableImpl and a ParameterPoint and 
	creates an ExampleTableImpl with the input features set to the 'true'
	bits of the binarysolution. The binarysolution must therefore have n
	bits where n is the number of input columns.
	
	@author pgroves
	@date 02/03/04
	*/

public class AssignFeatures extends DataPrepModule{

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
		ExampleTableImpl vt = (ExampleTableImpl)pullInput(0);
		int[] origInputs = vt.getInputFeatures();
		
		ParameterPoint param = (ParameterPoint)pullInput(1);
		
		if(debug)
			System.out.println("assign Features"+param.toString());
					
		int featureCount=0;
		for(int i = 0; i < param.getNumParameters(); i++){
			if(param.getValue(i) > 0.5)
				featureCount++;
		}
		int[] inputs=new int[featureCount];
		int iter=0;
		for(int i = 0; i < param.getNumParameters(); i++){
			if(param.getValue(i) > 0.5){
				inputs[iter]=origInputs[i];
				iter++;
			}
		}
		ExampleTable et=new ExampleTableImpl(vt);
		et.setInputFeatures(inputs);

		pushOutput(et, 0);

	}
		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return 
		"	Takes in a ExampleTableImpl and a ParameterPoint and "+
		"creates an ExampleTableImpl with the input features set to the 'true'"+
		" bits of the binarysolution. The binarysolution must therefore have n"+
		" bits where n is the number of input columns.";
	}
	
   	public String getModuleName() {
		return "AssignFeatures";
	}
	public String[] getInputTypes(){
		String[] types = {
		"ncsa.d2k.modules.core.datatype.table.basic.ExampleTableImpl",
		"ncsa.d2k.modules.core.datatype.parameter.ParameterPoint"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: return "An Example Table";
			case 1: return "A binary solution indicating which input features"+
			" to set as the new input feature set";
			default: return "No such input";
		}
	}
	
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Data Table";
			case 1:
				return "Feature Column Flags";
			default: return "NO SUCH INPUT!";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.table.basic.ExampleTableImpl"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: return "The input example table with the appropriate columns"+
			" set as input features.";
			default: return "No such output";
		}
	}
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Table w/ InputFeatures";
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
			
					

			

								
	
