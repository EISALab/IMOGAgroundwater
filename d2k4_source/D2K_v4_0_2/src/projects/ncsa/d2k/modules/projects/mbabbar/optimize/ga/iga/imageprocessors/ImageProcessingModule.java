package ncsa.d2k.modules.projects.mbabbar.optimize.ga.iga.imageprocessors;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.optimize.ga.emo.*;

/**
	Processes images/GUI applets for population by invoking the <code>imageProcessor</code> method.
*/
abstract public class ImageProcessingModule extends ncsa.d2k.core.modules.ComputeModule 	{

	//////////////////////////////////
	// Info methods
	//////////////////////////////////
	/**
		This method returns the description of the various inputs.

		@return the description of the indexed input.
	*/
	public String getOutputInfo (int index) {
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"population\">    <Text> population. </Text>  </Info></D2K>";
			case 1: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Table\">    <Text>The table with population info: quantitative objectives, selected individuals,  images/applets info for selected individuals. </Text>  </Info></D2K>";
                        default: return "No such output";
		}
}

	/**
		This method returns the description of the various inputs.

		@return the description of the indexed input.
	*/
	public String getInputInfo (int index) {
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"population\">    <Text> population. </Text>  </Info></D2K>";
			case 1: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Table\">    <Text>The table with quantitative fitnesses and updated individuals after passing through a selection filter. </Text>  </Info></D2K>";
                        default: return "No such input";
		}
	}

	/**
		This method returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo () {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Image Processing Module\">    <Text> This takes in the population and table of selected individuals and processes them to create relevant images for them. </Text>  </Info></D2K>";
	}

	//////////////////////////////////
	// Type definitions.
	//////////////////////////////////

	public String[] getInputTypes () {
		String[] types = {"ncsa.d2k.modules.core.optimize.ga.Population",
                      "ncsa.d2k.modules.core.datatype.table.Table"};
		return types;
	}

	public String[] getOutputTypes () {
		String[] types = {"ncsa.d2k.modules.core.optimize.ga.Population",
                      "ncsa.d2k.modules.core.datatype.table.Table"};
		return types;
	}

	/**
          Do the imageprocessing for selected individuals. Information of individuals selected
          for further processing (from tradeoff plots) is in the input table.
          Overwrite the imageprocessor, which will be specific to your
          image processing module for your application.
	*/
	public void doit () throws Exception {
		Population pop = (Population) this.pullInput(0);
                Table popTable= (Table) this.pullInput(1);

		this.imageProcessor (pop, popTable);

		this.pushOutput (pop, 0);
                this.pushOutput (popTable, 1);
	}

	/**
		Process Images for the selected individuals in the population.
		@param popul: the population.
                @param populTable: Table with selected individuals.
	*/
	abstract public void imageProcessor (Population popul, Table populTable);
}
