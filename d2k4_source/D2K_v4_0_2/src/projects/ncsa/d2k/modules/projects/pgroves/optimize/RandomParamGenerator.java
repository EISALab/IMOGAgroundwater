package ncsa.d2k.modules.projects.pgroves.optimize;

import java.util.Random;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.datatype.parameter.impl.*;
import ncsa.d2k.modules.core.datatype.parameter.*;


/**
	Takes in ParameterSpace, generates an array of random ParameterPoints
	that fall inside it. The number of points is defined by a property.


	@author pgroves
	*/

public class RandomParamGenerator extends DataPrepModule 
	implements java.io.Serializable {

	//////////////////////
	//d2k Props
	////////////////////
	
	boolean debug = false;	
	
	//int numPoints = 100;	

	int randomSeed = 215634;
	/////////////////////////
	/// other fields
	////////////////////////

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
		ParameterSpace ps = (ParameterSpace)pullInput(0);
		int numPoints = ((Integer)pullInput(1)).intValue();

		rand = new Random((long)randomSeed);

		ParameterPointImpl[] points = new ParameterPointImpl[numPoints];
		for(int i = 0; i < numPoints; i++){
			points[i] = getRandomPoint(ps);
		}

		pushOutput(points, 0);
	}
	
	/**
		returns a single random point from the space
		*/	
	private ParameterPointImpl getRandomPoint(ParameterSpace ps){
		String[] ss = new String[ps.getNumParameters()];
		double[] dd = new double[ps.getNumParameters()];

		for(int i = 0; i < ps.getNumParameters(); i++){
			ss[i] = ps.getName(i);
			dd[i] = getRandomParameter(ps, i);
		}
		return (ParameterPointImpl)ParameterPointImpl.getParameterPoint(ss, dd);
	}

	/**
		returns a random double in the range of the parameter index specified.
		could potentially be overridden to provide more advanced
		random techniques (like being based on a distribution other
		than uniform)
		*/
	private double getRandomParameter(ParameterSpace ps, int paramIndex){
		double min = ps.getMinValue(paramIndex);
		double max = ps.getMaxValue(paramIndex);

		//this is between 0.0 and 1.0
		double d = rand.nextDouble();
		
		return (max - min) * d + min;
	}
		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return 	
			""+
			""+
			""+
			"";
	}
	
   	public String getModuleName() {
		return "RandomParamGenerator";
	}
	public String[] getInputTypes(){
		String[] types = {
			"ncsa.d2k.modules.datatype.parameter.ParameterSpace",
			"java.lang.Integer"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: 
				return "";
			case 1: 
				return "How many parameter points will be generated";
			case 2: 
				return "";
			default: return "No such input";
		}
	}
	
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Parameter Space";
			case 1:
				return "Point Count";
			case 2:
				return "";
			default: return "No such input";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {
			"[Lncsa.d2k.modules.core.datatype.parameter.impl.ParameterPointImpl"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: 
				return "an array of parameters";
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
				return "Random Parameter Set";
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
	public int getSeed(){
		return randomSeed;
	}
	public void setSeed(int i){
		randomSeed=i;
	}
	/*public int getNumPoints(){
		return numPoints;
	}
	public void setNumPoints(int i){
		numPoints=i;
	}*/

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
			
					

			

								
	
