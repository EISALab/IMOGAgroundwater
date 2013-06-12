package ncsa.d2k.modules.core.optimize.ga.emo.examples;


import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.optimize.ga.nsga.*;

/**
	Evaluate the new population. The population object does all the work,
	this module will simply invoke the <code>evaluateAll</code> method of the population.
*/
public class EvaluateE6Population extends EvaluateModule {

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
	final private double fourPi = 6.0 * Math.PI;
	public void evaluateIndividual (Individual member) {
		MONumericIndividual ni = (MONumericIndividual) member;

		// Compute F1
		double [] genes = (double []) ni.getGenes ();
		double x1 = genes [0];
		double f1 = Math.exp (-4.0 * x1);
		f1 *= Math.pow (Math.sin (fourPi*x1), 6.0);
		f1 = 1.0 - f1;

		// Compute g used in F2
		double g = 0.0;
		for (int i = 1 ; i < 10 ; i++)
			g += genes [i];
		g /= 9.0;
		g = Math.pow (g, 0.25);
		g *= 9.0;
		g += 1.0;

		// compute f2
		double f2 = f1 / g;
		f2 *= f2;
		f2 = 1 - f2;
		f2 *= g;

		ni.setObjective (0, f1);
		ni.setObjective (1, f2);
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
