package ncsa.d2k.modules.projects.pgroves.util;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.impl.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;


/**
	Takes two ParameterSpaces, combines them. Creates all new
	spaces, does not use the joinSpaces functions in the
	ParameterSpace interface.

	@author pgroves
	@date 03/14/04
	*/

public class MergeParameterSpaces extends DataPrepModule 
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
		ParameterSpace[] psa = new ParameterSpace[2];
		psa[0] = (ParameterSpace)pullInput(0);
		psa[1] = (ParameterSpace)pullInput(1);

		ParameterSpaceImpl psi = this.mergeSpaces(psa);

		pushOutput(psi, 0);
	}
	/**
		given an array of parameter spaces, combines them into
		one ParameterSpaceImpl.

		@param spaces an array of ParameterSpace's
		@return a single ParameterSpaceImpl containing all of the
		parameters of the passed in <code>spaces</code>. The
		parameters retain the same order that they have in the
		individual spaces, and the spaces are added in the order
		they appear in <code>spaces</code>
		*/
	public static ParameterSpaceImpl mergeSpaces(ParameterSpace[] spaces){
		int i, j, k;
		
		int numSpaces = spaces.length;
		int[] numParams = new int[numSpaces];
		int totalNumParams = 0;
		
		for(i = 0; i < numSpaces; i++){
			numParams[i] = spaces[i].getNumParameters();
			totalNumParams += numParams[i];
		}
		
		String[] names = new String[totalNumParams];
		double[] minVals = new double[totalNumParams];
		double[] maxVals = new double[totalNumParams];
		double[] defaultVals = new double[totalNumParams];
		int[] resolutions = new int[totalNumParams];
		int[] types = new int[totalNumParams];
		
		k = 0;
		for(i = 0; i < numSpaces; i++){
			for(j = 0; j < numParams[i]; j++){
				names[k] = spaces[i].getName(j);
				minVals[k] = spaces[i].getMinValue(j);
				maxVals[k] = spaces[i]. getMaxValue(j);
				defaultVals[k] = spaces[i].getDefaultValue(j);
				resolutions[k] = spaces[i].getResolution(j);
				types[k] = spaces[i].getType(j);
				k++;
			}
		}
		ParameterSpaceImpl psi = new ParameterSpaceImpl();
		psi.createFromData(names, minVals, maxVals, defaultVals, 
			resolutions, types);

		return psi;
	}
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return 	
			"Takes two ParameterSpace's and returns a single ParameterSpace"+
			" that contains all of the parameters of each of them."+
			" The order of the parameters is retained (with the parameters"+
			" of the first space appearing first.";
	}
	
   	public String getModuleName() {
		return "MergeParameterSpaces";
	}
	public String[] getInputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.parameter.ParameterSpace",
			"ncsa.d2k.modules.core.datatype.parameter.ParameterSpace"
			};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: 
				return "A ParameterSpace";
			case 1: 
				return "A ParameterSpace";
			case 2: 
				return "";
			default: return "No such input";
		}
	}
	
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "ParameterSpace 1";
			case 1:
				return "ParameterSpace 2";
			case 2:
				return "";
			default: return "No such input";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.parameter.impl.ParameterSpaceImpl"
			};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: 
				return "A Space with all the parameters of ParameterSpace 1" +
				" and ParameterSpace 2.";
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
				return "CombinedSpaced";
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
			
					

			

								
	
