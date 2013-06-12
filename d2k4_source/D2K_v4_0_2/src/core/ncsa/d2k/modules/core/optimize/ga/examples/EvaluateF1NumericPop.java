package ncsa.d2k.modules.core.optimize.ga.examples;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.optimize.ga.*;

/**
	Evaluate the new population. The population object does all the work,
	this module will simply invoke the <code>evaluateAll</code> method of the population.
*/
public class EvaluateF1NumericPop extends EvaluateModule {

	//////////////////////////////////
	// Info methods
	//////////////////////////////////

	/**
		This method returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo () {
		return "<html>  <head>      </head>  <body>    Evalute this F1 population.  </body></html>";
	}

	/**
		Evaluate the individuals in this class. Here we are simply maxing
		the objective function.
	*/
	public void evaluateIndividual (Individual memb) {
		NumericIndividual member = (NumericIndividual) memb;
		double [] genes = (double []) member.getGenes ();
		double x = genes[0];
		double y = genes[1];
		double z = genes[2];
		member.setObjective (Math.log ((x*x) + (y*y) + (z*z))/
			Math.log ((255*255) + (255*255) + (255*255)));
	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "Evaluate Population";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "population";
			default: return "NO SUCH INPUT!";
		}
	}

	/**
	 * Return the human readable name of the indexed output.
	 * @param index the index of the output.
	 * @return the human readable name of the indexed output.
	 */
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "population";
			default: return "NO SUCH OUTPUT!";
		}
	}
}
