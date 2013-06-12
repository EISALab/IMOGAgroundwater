package ncsa.d2k.modules.projects.pgroves.geostat;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.impl.*;
import ncsa.d2k.modules.core.prediction.*;
import ncsa.d2k.modules.core.datatype.table.*;



/**
	Generates a parameter space for defining a neighborhood 
	(the closest examples to an example in the input feature space).
	Currently only works for a 'radius' method. The one and only
	parameter is, therefore, the radius. This must be in
	the same units as the input features to mean anything.

	@author pgroves
	@date 03/25/04
	*/

public class NeighborhoodParamSpace extends AbstractParamSpaceGenerator
	implements java.io.Serializable {

	
	public ParameterSpace getDefaultSpace(){
		
		int numParams = 3;

		String[] names = new String[numParams];
		double[] minVals = new double[numParams];
		double[] maxVals = new double[numParams];
		double[] defaultVals = new double[numParams];
		int[] resolutions = new int[numParams];
		int[] types = new int[numParams];

		names[0] = "Radius";
		minVals[0] = 0.0;
		maxVals[0] = 100.0;
		defaultVals[0] = 15.0;
		resolutions[0] = 100;
		types[0] = ColumnTypes.DOUBLE;
		
		names[1] = "Minimum Number Neighbors";
		minVals[1] = 1.0;
		maxVals[1] = 15;
		defaultVals[1] = 3;
		resolutions[1] = 15;
		types[1] = ColumnTypes.INTEGER;

		names[2] = "Maximum Number Neighbors";
		minVals[2] = 5;
		maxVals[2] = 100.0;
		defaultVals[2] = 15;
		resolutions[2] = 10;
		types[2] = ColumnTypes.INTEGER;

		ParameterSpace psi = new ParameterSpaceImpl();
		psi.createFromData(names, minVals, maxVals, defaultVals,
				resolutions, types);
	
		return psi;
	}

	public String getModuleName(){
		return "Neighborhood Param Space";
	}

}
			
					

			

								
	
