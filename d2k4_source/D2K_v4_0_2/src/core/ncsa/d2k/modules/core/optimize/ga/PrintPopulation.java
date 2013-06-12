
package ncsa.d2k.modules.core.optimize.ga;


import ncsa.d2k.core.modules.*;
import java.io.Serializable;

/**
	Prints the population out to the console.
*/
public class PrintPopulation extends ncsa.d2k.core.modules.OutputModule
		 {


	//////////////////////////////////
	// Info methods
	//////////////////////////////////
	/**
		This method returns the description of the various inputs.

		@return the description of the indexed input.
	*/
	public String getOutputInfo (int index) {
		switch (index) {
			case 0: return "      When we are done we simply pass the population along.   ";
			default: return "No such output";
		}
	}

	/**
		This method returns the description of the various inputs.

		@return the description of the indexed input.
	*/
	public String getInputInfo (int index) {
		switch (index) {
			case 0: return "      This is the population that will be printed.   ";
			default: return "No such input";
		}
	}

	/**
		This method returns the description of the module.

		@return the description of the module.
	*/
	public String getModuleInfo () {
		return "<html>  <head>      </head>  <body>    Print all <i>the </i> individuals in the current population.  </body></html>";
	}

	//////////////////////////////////
	// Type definitions.
	//////////////////////////////////

	public String[] getInputTypes () {
		String[] types = {"ncsa.d2k.modules.core.optimize.ga.Population"};
		return types;
	}

	public String[] getOutputTypes () {
		String[] types = {"ncsa.d2k.modules.core.optimize.ga.Population"};
		return types;
	}

	boolean sorting = true;
	public void setSorting (boolean sort) { sorting = sort; }
	public boolean getSorting () { return sorting; }
	int numToPrint = 1000;
	public void setNumberToPrint (int sort) { numToPrint = sort; }
	public int getNumberToPrint () { return numToPrint; }

	/**
		Do a standard evaluation, the best the worst individuals are sought, and the average
		fitness is computed.

		@param outV the array to contain output object.
	*/
	public void doit () {
		System.out.println ();
		System.out.println ("-------------------------------------------------------");
		Population population = (Population) this.pullInput (0);
		int num = population.size();
		num = num < numToPrint ? num : numToPrint;
		if (sorting) {
			int [] order = population.sortIndividuals ();
			for (int i = 0 ; i < num ; i++)
				population.printIndividual (order [i]);
		} else
			for (int i = 0 ; i < num ; i++)
				population.printIndividual (i);

		this.pushOutput (population, 0);
	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "Print";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Population";
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
