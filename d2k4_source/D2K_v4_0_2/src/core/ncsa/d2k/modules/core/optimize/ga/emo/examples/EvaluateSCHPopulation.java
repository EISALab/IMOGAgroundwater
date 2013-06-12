package ncsa.d2k.modules.core.optimize.ga.emo.examples;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.optimize.util.*;

/**
	Evaluate the new population. The population object does all the work,
	this module will simply invoke the <code>evaluateAll</code> method of the population.
*/
public class EvaluateSCHPopulation extends EvaluateModule {

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
		double x = genes [0];

		// compute f1
		double f1 = x * x;

		// f2
		double f2 = (x - 2.0);
		f2 *= f2;
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
