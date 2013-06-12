package ncsa.d2k.modules.projects.pgroves.decisiontree;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.impl.*;
import ncsa.d2k.modules.core.prediction.*;
import ncsa.d2k.modules.core.datatype.table.*;



/**
	Generates a parameter space for defining a Continuous Decision
	Tree. 
	@author pgroves
	@date 03/25/04
	*/

public class CDTParamSpace extends AbstractParamSpaceGenerator
	implements java.io.Serializable {

	
	public ParameterSpace getDefaultSpace(){
		
		int numParams = 4;

		String[] names = new String[numParams];
		double[] minVals = new double[numParams];
		double[] maxVals = new double[numParams];
		double[] defaultVals = new double[numParams];
		int[] resolutions = new int[numParams];
		int[] types = new int[numParams];

		names[0] = "Fraction Minimum Example Per Node";
		minVals[0] = 0.0;
		maxVals[0] = .5;
		defaultVals[0] = .05;
		resolutions[0] = 50;
		types[0] = ColumnTypes.DOUBLE;
		
		names[1] = "Minimum Improvement Threshold";
		minVals[1] = 1.0;
		maxVals[1] = 15;
		defaultVals[1] = 3;
		resolutions[1] = 15;
		types[1] = ColumnTypes.DOUBLE;

		names[2] = "Use All Inputs In Leaf Regressions";
		minVals[2] = 0;
		maxVals[2] = 1.0;
		defaultVals[2] = 1;
		resolutions[2] = 1;
		types[2] = ColumnTypes.BOOLEAN;
		
		names[3] = "Number Quantiles for Split Test";
		minVals[3] = 1;
		maxVals[3] = 10;
		defaultVals[3] = 3;
		resolutions[3] = 10;
		types[3] = ColumnTypes.INTEGER;
		
		ParameterSpace psi = new ParameterSpaceImpl();
		psi.createFromData(names, minVals, maxVals, defaultVals,
				resolutions, types);
	
		return psi;
	}

	public String getModuleName(){
		return "Continuous Decision Tree Param Space";
	}

}
			
					

			

								
	
