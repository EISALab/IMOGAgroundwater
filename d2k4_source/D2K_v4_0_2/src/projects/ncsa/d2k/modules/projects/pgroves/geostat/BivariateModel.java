package ncsa.d2k.modules.projects.pgroves.geostat;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.PredictionModelModule;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.impl.*;

import ncsa.d2k.modules.projects.pgroves.util.MergeParameterSpaces;

/**
	A type of model to predict a single output variable
	with a single input variable. Will someday be able to 
	perform a least squares fitting of any parameters the 
	subclass's model uses. The currently implemented types
	of functions that can be used are located at the end
	of this file (inner classes).


	@author pgroves
	@date 03/17/04
	*/

public class BivariateModel extends PredictionModelModule 
	implements java.io.Serializable {

	//////////////////////
	//d2k Props
	////////////////////
	
	boolean debug=false;		
	/////////////////////////
	/// other fields
	////////////////////////
	
	/**the parameters for <code>bivFunction</code>*/
	ParameterPoint params;

	/**the y = f(x) function*/
	BivariateFunction bivFunction;

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

	/////////////////////////
	///Constructor
	/////////////////////////
	/**
		empty constructor.
	*/
	public BivariateModel() {
	
	}	

	
	/**
		Main Constructor.
		
		@param trainTable the training data for use in the automatic
		parameter fitting. Only the first input and first output
		will be used.
		@param pp the set of parameters, used for fixing some function
		parameters/coefficients and determining which to optimize
	*/
		
	public BivariateModel(ExampleTable trainTable, ParameterPoint pp){
		super(trainTable);
		params = copyParameterPoint(pp);

		int i = trainTable.getNumInputFeatures();
		if(i > 1){
			System.out.println("WARNING: BivariateModel: More than one " +
				"INPUT. Only the first will be used.");
		}
		i = trainTable.getNumOutputFeatures();
		if(i > 1){
			System.out.println("WARNING: BivariateModel: More than one " +
				"OUTPUT. Only the first will be used.");
		}

		bivFunction = new LinearCombinationFunction();


		//only do optimization if there are unspecified parameters
		for(i = 0; i < params.getNumParameters(); i++){
			if(!Double.isNaN(params.getValue(i))){
				params = optimizeUnspecifiedParameters(trainTable, params);
				break;
			}
		}

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
		super.doit();
	}
		
	
	/**
		Does the actual work of making predictions by this model.
		
	*/
	protected void makePredictions(PredictionTable predTable){
		int inputColumn = predTable.getInputFeatures()[0];
		int predColumn = predTable.getPredictionSet()[0];
		int numRows = predTable.getNumRows();

		double x;
		double y;

		for(int i = 0; i < numRows; i++){
			x = predTable.getDouble(i, inputColumn);
			y = bivFunction.eval(x, params);
			predTable.setDouble(y, i, predColumn);
		}
	}

	/**
		makes a deep copy of a parameter point. what is returned
		is always a ParamterPointImpl. */
	public ParameterPointImpl copyParameterPoint(ParameterPoint pp){
		int numParams = pp.getNumParameters();
		String[] names = new String[numParams];
		double[] vals = new double[numParams];

		for(int i = 0; i < numParams; i++){
			names[i] = pp.getName(i);
			vals[i] = pp.getValue(i);
		}
		ParameterPointImpl copy = (ParameterPointImpl)ParameterPointImpl.
			getParameterPoint( names, vals);
		return copy;
	}
		

	/**
		sets all unspecified params to zero. The design has been changed
		so that the actual optimization is done externally. If, for
		some reason, there are unspecified parameters at this point
		in the execution, a warning will be printed and the value
		will be set to zero so that the parmeter has no influence.
		*/
	protected ParameterPoint optimizeUnspecifiedParameters(
		ExampleTable trainData, ParameterPoint unspecParams){
		
		int numParams = unspecParams.getNumParameters();
		String[] names = new String[numParams];
		double[] vals = new double[numParams];
		for(int i = 0; i < numParams; i++){
			names[i] = unspecParams.getName(i);
			vals[i] = unspecParams.getValue(i);
			if(Double.isNaN(vals[i])){
				System.out.println("WARNING: BivariateModel parameter is " +
						" not specified, setting to zero. " + names[i]);
				vals[i] = 0;
			}
		}
		ParameterPoint pp = ParameterPointImpl.getParameterPoint(names, vals);
		return pp;
	}
		

	/**
	currently returns the parameters for a linear combination function.
	*/
	public ParameterSpace getParameterSpace(){
		LinearCombinationFunction fun = new LinearCombinationFunction();
		return fun.getParameterSpace();
	}

	/**
	 * evaluates an 'x' value using the parameters this model
	 * was built with
	 */
	public double eval(double x){
		return bivFunction.eval(x, params);
	}

	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return 	
			"This module is likely to change."+
			"A model of the form y = f(x). "+
			""+
			"";
	}
	
   	public String getModuleName() {
		return "BivariateModel";
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

	/////////////////////////////////////
	//Bivariate Function Implementations
	/////////////////////////////////////

	/**
		defines a function of the form y = f(x), although it
		really is of the form y = f(x, C) where C represents
		some parameters/coefficients that are found in the function.
		The values of C are therefore what defines the model, while
		x is the 'input' to make a prediction on.
	*/
	public interface BivariateFunction{

		/**
			y = f(x, C).
			
			@param x the independent variable
			@param C the coefficients in of the function
		*/
		public double eval(double x, ParameterPoint params); 

		public ParameterSpace getParameterSpace();

	}

	/**
		Creates a single function that is a linear combination of
		all the others.
		*/
	public class LinearCombinationFunction implements BivariateFunction{

		BivariateFunction[] functions;
		int numFunctions = 5;

		/**reuse this array for speed*/
		ParameterSpace[] pps;
		
		public LinearCombinationFunction(){
			
			
			functions = new BivariateFunction[numFunctions];
			functions[0] = new NuggetFunction();	
			functions[1] = new SphericalFunction();	
			functions[2] = new ExponentialFunction();	
			functions[3] = new GaussianFunction();	
			functions[4] = new PowerFunction();

			/*this.pps = new ParameterPoint[numFunctions];

			for(int i = 0; i < numFunctions; i++){
				pps[i] = functions[i].getParameterSpace().getDefaultParameter();
			}
			*/
			pps = new ParameterSpace[numFunctions];
			for(int i = 0; i < numFunctions; i++){
				pps[i] = functions[i].getParameterSpace();
			}
			
		}

		/** adds up the outputs of it's member functions, returns
		that value.*/
		public double eval(double x, ParameterPoint pp){
			int i, j, k;
			double y = 0.0;
			double d;
			
			k = 0;
			ParameterPoint subpp;
			for(i = 0; i < numFunctions; i++){
				for(j = 0; j < pps[i].getNumParameters(); j++){
					pps[i].setDefaultValue(j, pp.getValue(k));
					k++;
				}
				subpp = pps[i].getDefaultParameterPoint();
				d = functions[i].eval(x, subpp);
				if(Double.isNaN(d)){
					System.out.println("BivariateModel: NaN at Function:"+
						i +", x = "+ x);
				}
				y += d;
			}
			return y;
		}

		
		public ParameterSpace getParameterSpace(){
			ParameterSpace[] psa = new ParameterSpace[numFunctions];
			for(int i = 0; i < numFunctions; i++){
				psa[i] = functions[i].getParameterSpace();
			}
			return MergeParameterSpaces.mergeSpaces(psa);
		}
	}

	/**
		Nugget Effect Model.

		<p>g(h) = C1 * [{0, if h=0} | {1, if h &ge 0}]</p>
	*/
	public class NuggetFunction implements BivariateFunction{

		public NuggetFunction(){}

		
		/** just one parameter, the coefficient (the model really
		just returns 1, but then there's a scaling coefficient).
		*/
		public ParameterSpace getParameterSpace(){

			int numParams = 1;

			String[] names = new String[numParams];
			double[] minVals = new double[numParams];
			double[] maxVals = new double[numParams];
			double[] defaultVals = new double[numParams];
			int[] resolutions = new int[numParams];
			int[] types = new int[numParams];

			names[0] = "Nugget Coefficient";
			minVals[0] = 0.0;
			maxVals[0] = 100.0;
			defaultVals[0] = Double.NaN;
			resolutions[0] = 1000;
			types[0] = ColumnTypes.DOUBLE;

			ParameterSpace psi = new ParameterSpaceImpl();
			psi.createFromData(names, minVals, maxVals, defaultVals,
				resolutions, types);
			return psi;
		}

		public double eval(double x, ParameterPoint pp){
			if(x == 0.0){
				return 0;
			}else{
				return pp.getValue(0);
			}
		}
	}

	/**
		Spherical model with range 'a'.

		<p>g(h) = C1 * [{1.5h/a - .5(h/a)^3, if h &le a} | {1, if h &ge a}]<p>
		*/
	public class SphericalFunction implements BivariateFunction{

		public SphericalFunction(){}

		
		/** Two parameters, the coefficient, and the range, 'a' .
		*/
		public ParameterSpace getParameterSpace(){

			int numParams = 2;

			String[] names = new String[numParams];
			double[] minVals = new double[numParams];
			double[] maxVals = new double[numParams];
			double[] defaultVals = new double[numParams];
			int[] resolutions = new int[numParams];
			int[] types = new int[numParams];

			names[0] = "Spherical Coefficient";
			minVals[0] = 0.0;
			maxVals[0] = 100.0;
			defaultVals[0] = Double.NaN;
			resolutions[0] = 1000;
			types[0] = ColumnTypes.DOUBLE;

			names[1] = "Spherical Range";
			minVals[1] = 0.0;
			maxVals[1] = 100.0;
			defaultVals[1] = Double.NaN;
			resolutions[1] = 1000;
			types[1] = ColumnTypes.DOUBLE;
			
			ParameterSpace psi = new ParameterSpaceImpl();
			psi.createFromData(names, minVals, maxVals, defaultVals,
				resolutions, types);
			return psi;
		}

		public double eval(double x, ParameterPoint pp){
			if(x == 0.0){
				return 0;
			}
			double c = pp.getValue(0);
			double range = pp.getValue(1);
			double y = 0.0;
			if(x <=  range){
				y = 1.5 * x / range;
				y -= 0.5 * Math.pow(x / range, 3);
			}else{
				y = 1;
			}
			y *= c;

			return y;
		}
	}
	
	/**
		Exponential Model With practical range a.

		<p>g(h) = C1 * (1 - exp(-3h/a))</p>
	*/
	public class ExponentialFunction implements BivariateFunction{

		public ExponentialFunction(){}

		/** Two parameters, the coefficient, and the range, 'a' .
		*/
		
		public ParameterSpace getParameterSpace(){

			int numParams = 2;

			String[] names = new String[numParams];
			double[] minVals = new double[numParams];
			double[] maxVals = new double[numParams];
			double[] defaultVals = new double[numParams];
			int[] resolutions = new int[numParams];
			int[] types = new int[numParams];

			names[0] = "Exponential Coefficient";
			minVals[0] = 0.0;
			maxVals[0] = 100.0;
			defaultVals[0] = Double.NaN;
			resolutions[0] = 1000;
			types[0] = ColumnTypes.DOUBLE;

			names[1] = "Exponential Range";
			minVals[1] = 0.0;
			maxVals[1] = 100.0;
			defaultVals[1] = Double.NaN;
			resolutions[1] = 1000;
			types[1] = ColumnTypes.DOUBLE;
			
	
			ParameterSpace psi = new ParameterSpaceImpl();
			psi.createFromData(names, minVals, maxVals, defaultVals,
				resolutions, types);
			return psi;
		}

		public double eval(double x, ParameterPoint pp){
			if(x == 0.0){
				return 0;
			}
			double c = pp.getValue(0);
			double range = pp.getValue(1);

			double y = 1.0;
			y -= Math.exp(-3.0 * x / range);
			y *= c;
			return y;
		}
	}
		
		/**
		Gaussian Model With practical range a.

		<p>g(h) = C1 * (1 - exp(-3(h^2)/(a^2)))</p>
	*/
	public class GaussianFunction implements BivariateFunction{

		public GaussianFunction(){}

		
		/** Two parameters, the coefficient, and the range, 'a' .
		*/
		public ParameterSpace getParameterSpace(){

			int numParams = 2;

			String[] names = new String[numParams];
			double[] minVals = new double[numParams];
			double[] maxVals = new double[numParams];
			double[] defaultVals = new double[numParams];
			int[] resolutions = new int[numParams];
			int[] types = new int[numParams];

			names[0] = "Gaussian Coefficient";
			minVals[0] = 0.0;
			maxVals[0] = 100.0;
			defaultVals[0] = Double.NaN;
			resolutions[0] = 1000;
			types[0] = ColumnTypes.DOUBLE;

			names[1] = "Gaussian Range";
			minVals[1] = 0.0;
			maxVals[1] = 100.0;
			defaultVals[1] = Double.NaN;
			resolutions[1] = 1000;
			types[1] = ColumnTypes.DOUBLE;
			
	
			ParameterSpace psi = new ParameterSpaceImpl();
			psi.createFromData(names, minVals, maxVals, defaultVals,
				resolutions, types);
			return psi;
		}

		public double eval(double x, ParameterPoint pp){
			if(x == 0.0){
				return 0;
			}
			double c = pp.getValue(0);
			double range = pp.getValue(1);

			double y = 1.0;
			y -= Math.exp(-3.0 * x * x / (range * range));
			y *= c;
			return y;
		}
	}
		
			
		/**
		Power Model. 

		<p>g(h) = C1 * (h^w)</p>

		Note that this becomes a linear model when w=0.
	*/
	public class PowerFunction implements BivariateFunction{

		public PowerFunction(){}

		
		/** Two parameters, the coefficient, and the range, 'a' .
		*/
		public ParameterSpace getParameterSpace(){

			int numParams = 2;

			String[] names = new String[numParams];
			double[] minVals = new double[numParams];
			double[] maxVals = new double[numParams];
			double[] defaultVals = new double[numParams];
			int[] resolutions = new int[numParams];
			int[] types = new int[numParams];

			names[0] = "Power Coefficient";
			minVals[0] = 0.0;
			maxVals[0] = 100.0;
			defaultVals[0] = Double.NaN;
			resolutions[0] = 1000;
			types[0] = ColumnTypes.DOUBLE;

			names[1] = "Power Exponent";
			minVals[1] = 0.0;
			maxVals[1] = 2.0;
			defaultVals[1] = Double.NaN;
			resolutions[1] = 10;
			types[1] = ColumnTypes.DOUBLE;
			
	
			ParameterSpace psi = new ParameterSpaceImpl();
			psi.createFromData(names, minVals, maxVals, defaultVals,
				resolutions, types);
			return psi;
		}

		public double eval(double x, ParameterPoint pp){
			double c = pp.getValue(0);
			double exponent = pp.getValue(1);

			double y = Math.pow(x, exponent);
			y *= c;
			return y;
		}
	}
	
}
			
					

			

								
	
