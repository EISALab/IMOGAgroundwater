package ncsa.d2k.modules.core.optimize.ga;



import ncsa.d2k.core.modules.*;
import java.io.Serializable;

/**
	This module measures the success of the population, computing the average fitnessd,
	and identifying the best and worst current individuals in the population. Also, the
	population is queried to determine if we are done or not.
*/
public class MeasureModule extends ncsa.d2k.core.modules.ComputeModule 	 {

	/** the last element crossed. */
	private int last = 0;

	///////////////////////////////
	// Properties.
	///////////////////////////////

	private boolean debugging = false;

	/**
		set the rankFlag.

		@param score is a boolean indicating if rankFlag is on or off.
	*/
	public void setDebugging (boolean score) {
		this.debugging = score;
	}

	/**
		get the rankFlag.

		@returns true if rankFlag is on.
	*/
	public boolean getDebugging () {
		return this.debugging;
	}


	//////////////////////////////////
	// Info methods
	//////////////////////////////////
	/**
		This method returns the description of the various inputs.

		@return the description of the indexed input.
	*/
	public String getOutputInfo (int index) {
		switch (index) {
			case 0: return "<html>  <head>  </head>  <body>    This output will be produced at each iteration, unless we are done.  </body></html>";
			case 1: return "<html>  <head>      </head>  <body>    This output will be produced only when we are done.  </body></html>";
			default: return "No such output";
		}
	}

	/**
		This method returns the description of the various inputs.

		@return the description of the indexed input.
	*/
	public String getInputInfo (int index) {
		switch (index) {
			case 0: return "      The input population will be examined for convergence or any other stopping criterion.   ";
			default: return "No such input";
		}
	}

	/**
		This method returns the description of the module.

		@return the description of the module.
	*/
	public String getModuleInfo () {
		return "<html>  <head>      </head>  <body>    This is the module that will determine when the optimization is done. The     population object is responsible for making the call, it will stop when we     have reached the maximum number of interations (property of the population     generation module), or if we have reached the target fitness (also a     property of the population generation module).  </body></html>";
	}

	//////////////////////////////////
	// Type definitions.
	//////////////////////////////////

	public String[] getInputTypes () {
		String[] types = {"ncsa.d2k.modules.core.optimize.ga.Population"};
		return types;
	}

	public String[] getOutputTypes () {
		String[] types = {"ncsa.d2k.modules.core.optimize.ga.Population","ncsa.d2k.modules.core.optimize.ga.Population"};
		return types;
	}
	public void beginExecution () {
		setTriggers = false;
	}

	private boolean setTriggers = false;

	/**
		Do a standard evaluation, the best the worst individuals are sought, and the average
		fitness is computed.

		@param outV the array to contain output object.
	*/
	public void doit () {
		Population population = (Population) this.pullInput (0);
		population.computeStatistics ();

		if (debugging) {
			System.out.println (population.statusString ());
		}

		// set if we have hit the target fitness.
		if (population.isDone ()) {
			setTriggers = true;
		}else
			this.pushOutput (population, 0);
	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "Measure Success";
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
			case 1:
				return "result population";
			default: return "NO SUCH OUTPUT!";
		}
	}
}
