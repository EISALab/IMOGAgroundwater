package ncsa.d2k.modules.projects.pgroves.geostat;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.util.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.impl.*;


/**
 * Takes a parameter point for a variogram model generator and
 * a data sets. Will generate a parameter space (of the same
 * structure as the pulled in paramterpoint) that has the following
 * two properties:
 *
 * <p>If the corresponding parameter in the pulled in ParameterPoint
 * is valid, the parameter in the created space will be restricted
 * to that value (min and max will both be set to the value from
 * the pulled in ParameterPoint).
 * <p>If the parameter is NaN in the ParameterPoint, The range will
 * be set to reasonable values based on the data set. In all cases,
 * this means that the min will be zero and the max will be the
 * maximum gamma(h) value from the experimental variogram data set
 * pulled in for any 'coefficient' parameters. Any 'range' parameters
 * will be assigned a search space with min = zero and max = max(h), 
 * where h is the first and only input column of the 
 * experimental variogram data set. The last parameter, PowerExponent,
 * is a special case that always has a range between 0 and 3.


	@author pgroves
	@date 04/08/04
	*/

public class AutoVariogramParamSpace extends ComputeModule 
	implements java.io.Serializable {

	//////////////////////
	//d2k Props
	////////////////////
	
	int allParametersResolution = 100;
	
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

		ParameterPoint pp = (ParameterPoint)pullInput(0);
		ExampleTable varET = (ExampleTable)pullInput(1);
		
		
		ParameterSpace ps = (new VariogramParamSpace()).getDefaultSpace();
		//not including the last param, PowerExponent
		int numParams = 9;
		
		//whether the parameter at index i is 
		//coefficient (true) vs range (false)
		boolean[] cvr = new boolean[numParams];
		cvr[0] = true;
		cvr[1] = true;
		cvr[2] = false;
		cvr[3] = true;
		cvr[4] = false;
		cvr[5] = true;
		cvr[6] = false;
		cvr[7] = true;
		cvr[8] = true;
		
		double[] range = TableUtilities.getMinMax(varET, 
				varET.getInputFeatures()[0]);
		double rMax = range[1];

		range = TableUtilities.getMinMax(varET, 
				varET.getOutputFeatures()[0]);
		double cMax = range[1];
		if(debug){
			System.out.println(this.getAlias() + ": Range Max:" + rMax +
					", Coefficient Max:" + cMax);
		}
		
		double val = Double.NEGATIVE_INFINITY;
		double max = val;
		for(int i = 0; i < numParams; i++){

			val = pp.getValue(i);
			
			if(cvr[i]){
				max = cMax;
			}else{
				max = rMax;
			}
			
			if(Double.isNaN(val)){
				ps.setMinValue(i, 0.0);
				ps.setMaxValue(i, max);
				ps.setResolution(i, this.allParametersResolution);
			}else{
				ps.setMinValue(i, val);
				ps.setMaxValue(i, val);
				ps.setResolution(i, 1);
			}
		}
		//the special case for the exponent
		if(Double.isNaN(val)){
			int expIdx = 8;
			//leave the min at zero, change the max to 3
			ps.setMaxValue(expIdx, 3);
			ps.setResolution(expIdx, 30);
		}
			
		pushOutput(ps, 0);
	}
		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return 	
		"Takes a parameter point for a variogram model generator and        "+
		"a data set. Will generate a parameter space (of the same          "+
		"structure as the pulled in paramterpoint) that has the following   "+
		"two properties:                                                    "+
		"                                                                   "+
		"<p>If the corresponding parameter in the pulled in ParameterPoint  "+
		"is valid, the parameter in the created space will be restricted    "+
		"to that value (min and max will both be set to the value from      "+
		"the pulled in ParameterPoint).                                     "+
		"<p>If the parameter is NaN in the ParameterPoint, The range will   "+
		"be set to reasonable values based on the data set. In all cases,   "+
		"this means that the min will be zero and the max will be the       "+
		"maximum gamma(h) value from the experimental variogram data set    "+
		"pulled in for any 'coefficient' parameters. Any 'range' parameters "+
		"will be assigned a search space with min = zero and max = max(h),  "+
		"where h is the first and only input column of the                  "+
		"experimental variogram data set. The last parameter, PowerExponent,"+
		"is a special case that always has a range between 0 and 3.";
	}
	
   	public String getModuleName() {
		return "AutoVariogramParamSpace";
	}
	public String[] getInputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.parameter.ParameterPoint",
			"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: 
				return "A Variogram Parameter Point. Any parameters set to "+
					"NaN (Not a Number) will be optimized.";
			case 1: 
				return "The (binned) data set that is being modelled with" +
					" a theoretical variogram. This will be used to find " +
					" reasonable ranges for the search of any parameters " +
					" that are to be optimized.";
			case 2: 
				return "";
			default: return "No such input";
		}
	}
	
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Parameters";
			case 1:
				return "Binned Experimental Variogram";
			case 2:
				return "";
			default: return "No such input";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.parameter.impl.ParameterSpaceImpl"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: 
				return "A new parameter space. Any parameters that were set" +
					" in the input parameter will be fixed in the space. Any " +
					" others will be given a range determined by the Variogram " +
					"data.";
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
				return "Variogram Optimization Space";
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
	public int getAllParametersResolution(){
		return allParametersResolution;
	}
	public void setAllParametersResolution(int i){
		allParametersResolution = i;
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
			
					

			

								
	
