package ncsa.d2k.modules.core.optimize.ga.emo.examples;



import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.optimize.util.*;

/**
	Evaluate the new population. The population object does all the work,
	this module will simply invoke the <code>evaluateAll</code> method of the population.
*/
public class EvaluateMOP4Population extends EvaluateModule {

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
	public void evaluateIndividual (Individual member) {
		// Must be an Nsga member.
		MOSolution ni = (MOSolution) member;
		double [] genes = (double []) member.getGenes ();
		int numTraits = genes.length;

		// compute f1
		double f1 = 0.0;
		for (int i = 0 ; i < (numTraits-1) ; i++) {

			// get the two characters.
			double x1 = genes [i];
			double x2 = genes [i+1];
			double tmp = Math.sqrt ((x1*x1) + (x2*x2));
			tmp *= -0.2;
			f1 += -10.0 * Math.exp (tmp);
		}

		// f2
		double f2 = 0.0;
		for (int i = 0 ; i < numTraits; i++) {

			// Get x.
			double x1 = genes [i];
			f2 += Math.pow (Math.abs (x1), 0.8) +
				 (5.0 * Math.pow (Math.sin (x1), 3.0));
		}

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
