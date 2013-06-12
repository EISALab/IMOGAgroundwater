package ncsa.d2k.modules.projects.pgroves.optimize;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.impl.*;
import ncsa.d2k.modules.core.prediction.*;
import ncsa.d2k.modules.core.datatype.table.*;


/**
	A generic parameter space that has on parameter. Can
	be used with MergeParameterSpaces to generate 
	arbitrary sized ParameterSpaces with no coding.
	
	@author pgroves
	@date 04/10/04
	*/

public class SingleParameterSpace extends AbstractParamSpaceGenerator
	implements java.io.Serializable {

	
	public ParameterSpace getDefaultSpace(){
		
		int numParams = 1;

		String[] names = new String[numParams];
		double[] minVals = new double[numParams];
		double[] maxVals = new double[numParams];
		double[] defaultVals = new double[numParams];
		int[] resolutions = new int[numParams];
		int[] types = new int[numParams];

		names[0] = "GenericParameter";
		minVals[0] = 0.0;
		maxVals[0] = 100.0;
		defaultVals[0] = 0;
		resolutions[0] = 100;
		types[0] = ColumnTypes.DOUBLE;
		
	
		ParameterSpace psi = new ParameterSpaceImpl();
		psi.createFromData(names, minVals, maxVals, defaultVals,
				resolutions, types);
	
		return psi;
	}

	public String getModuleName(){
		return "SingleParameterSpace";
	}
	public String getModuleInfo(){
		return "A generic parameter space that has one double parameter. Can " +
				"be used with MergeParameterSpaces to generate " +
				" arbitrary sized ParameterSpaces with no coding.";
	}

}

