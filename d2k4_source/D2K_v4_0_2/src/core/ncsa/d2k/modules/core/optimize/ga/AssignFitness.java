/*&%^1 Do not modify this section. */
package ncsa.d2k.modules.core.optimize.ga;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.optimize.util.*;
/*#end^1 Continue editing. ^#&*/
/*&%^2 Do not modify this section. */
/**
	AssignFitness.java

*/
public class AssignFitness extends ncsa.d2k.core.modules.DataPrepModule
/*#end^2 Continue editing. ^#&*/
{
	private boolean debug = false;

	/**
		set the rankFlag.
		@param score is a boolean indicating if rankFlag is on or off.
	*/
	public void setDebug (boolean score) {
		this.debug = score;
	}

	/**
		get the rankFlag.
		@returns true if rankFlag is on.
	*/
	public boolean getDebug () {
		return this.debug;
	}

	/**
		This method returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "      This is the input population, the members of this population will have fitness values when we are done here.   ";
			case 1: return "      The table contains in each column a different measure of fitness, we are only interested in the first, which is assumed to be a number between 0 and 1, higher is better.   ";
			default: return "No such input";
		}
	}

	/**
		This method returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes () {
		String[] types = {"ncsa.d2k.modules.core.optimize.ga.Population","ncsa.d2k.modules.core.datatype.table.Table"};
		return types;
	}

	/**
		This method returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo (int index) {
		switch (index) {
			case 0: return "      This population will have fitness values for each member.   ";
			default: return "No such output";
		}
	}

	/**
		This method returns an array of strings that contains the data types for the outputs.
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes () {
		String[] types = {"ncsa.d2k.modules.core.optimize.ga.Population"};
		return types;
	}

	/**
		This method returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo () {
		return "<html>  <head>      </head>  <body>    This module will receive a population, and a fitness for each of the     individuals in the population. The fitness field of the population member     is filled in, one for each fitness table input, until each member of the     population has a fitness. This module allows the evaluation of the members     to be done by a seperate model builder algorithm.  </body></html>";
	}

	int memberCounter = 0;
	Population population = null;

	/**
		This module is ready whenever we get a new population, or when we have already gotten
		a population, and we have not yet filled in all the fitness values for all the members.
		@returns true if we have something to do.
	*/
	public boolean isReady () {
		//ANCA: replaced below
		//if (population != null || this.inputFlags [0] > 0)
		//	if (this.inputFlags [1] > 0)
		if (population != null || getInputPipeSize(0)  > 0)
			if (getInputPipeSize(1) > 0)
				return true;

		return false;
	}

	public void endExecution () {
		super.endExecution ();
		population = null;
	}

	/**
		PUT YOUR CODE HERE.
	*/
	public void doit () throws Exception {
		if (population == null) {
			memberCounter = 0;
			if (debug) System.out.println ("AssignFitness: Pulling population");
			population = (Population) this.pullInput (0);
		}

		// Assign the fitness to the next member.
		Table fitness = (Table) this.pullInput (1);
		SOSolution member = (SOSolution) population.members [memberCounter++];
		member.setObjective (fitness.getDouble (0, 0));

		// Are we done with this population?
		if (memberCounter == population.members.length) {
			if (debug) System.out.println ("Assign fitness: done, pushing result");
			this.pushOutput (population, 0);
			population = null;
			memberCounter = 0;
		} else
			if (debug) System.out.println ("Assign fitness: "+memberCounter);
	}
/*&%^8 Do not modify this section. */
/*#end^8 Continue editing. ^#&*/

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "Assign Fitness";
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
			case 1:
				return "Fitness";
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

