package ncsa.d2k.modules.projects.pgroves.geostat;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.impl.*;
import ncsa.d2k.modules.core.prediction.*;



/**
	Generates a parameter space for a Variogram Model. Currently
	this is interchangeable with a space for a BivariateModel.
	Uses the default parameter space returned by the linear
	combination function in BivariateModel

	@author pgroves
	@date 03/17/04
	*/

public class VariogramParamSpace extends AbstractParamSpaceGenerator
	implements java.io.Serializable {

	
	public ParameterSpace getDefaultSpace(){
		BivariateModel mod = new BivariateModel();
		
		return mod.getParameterSpace();	
	}

	public String getModuleName(){
		return "Variogram Param Space";
	}

}
			
					

			

								
	
