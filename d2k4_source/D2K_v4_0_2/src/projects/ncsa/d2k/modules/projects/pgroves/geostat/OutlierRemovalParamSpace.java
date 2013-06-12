package ncsa.d2k.modules.projects.pgroves.geostat;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.impl.*;
import ncsa.d2k.modules.core.prediction.*;
import ncsa.d2k.modules.core.datatype.table.*;



/**
	Generates a parameter space for removing the outliers
	for computation of the theoretical variogram.
	
	@author pgroves
	@date 03/26/04
	*/

public class OutlierRemovalParamSpace extends AbstractParamSpaceGenerator
	implements java.io.Serializable {

	
	public ParameterSpace getDefaultSpace(){
		
		int numParams = 2;

		String[] names = new String[numParams];
		double[] minVals = new double[numParams];
		double[] maxVals = new double[numParams];
		double[] defaultVals = new double[numParams];
		int[] resolutions = new int[numParams];
		int[] types = new int[numParams];

		names[0] = "Change Threshold";
		minVals[0] = 0.0;
		maxVals[0] = 1.0;
		defaultVals[0] = 0.1;
		resolutions[0] = 100;
		types[0] = ColumnTypes.DOUBLE;
		
		names[1] = "Maximum Outliers to Remove";
		minVals[1] = 1.0;
		maxVals[1] = 15;
		defaultVals[1] = 5;
		resolutions[1] = 15;
		types[1] = ColumnTypes.INTEGER;

	
		ParameterSpace psi = new ParameterSpaceImpl();
		psi.createFromData(names, minVals, maxVals, defaultVals,
				resolutions, types);
	
		return psi;
	}

	public String getModuleName(){
		return "Outlier Removal Param Space";
	}

}
			
					

			

								
	
