package ncsa.d2k.modules.projects.mbabbar.optimize.ga.iga.selectors;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.optimize.ga.emo.*;
import ncsa.d2k.modules.projects.mbabbar.optimize.ga.iga.*;

/**
	Filters the selected individuals from the tradeoff plots. This is done to decrease pool size
        of individuals before they are shown for ranking purposes to the user. This is important for controlling
        human fatigue.
*/
abstract public class TradeoffSelectionFilterModule extends ncsa.d2k.core.modules.ComputeModule 	{

	//////////////////////////////////
	// Info methods
	//////////////////////////////////
	/**
		This method returns the description of the various inputs.

		@return the description of the indexed input.
	*/
	public String getOutputInfo (int index) {
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"population\">    <Text>The output population. </Text>  </Info></D2K>";
			case 1: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Table\">    <Text>The table with quantitative fitnesses and updated individuals after passing through a selection filter. </Text>  </Info></D2K>";
                        default: return "No such output";
		}
}

	/**
		This method returns the description of the various inputs.

		@return the description of the indexed input.
	*/
	public String getInputInfo (int index) {
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"population\">    <Text>input population. </Text>  </Info></D2K>";
			case 1: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Table\">    <Text>The table with quantitative fitnesses and individuals selected from Tradeoff Plots.  </Text>  </Info></D2K>";
                        default: return "No such input";
		}
	}

	/**
		This method returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo () {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Selection Filter\">    <Text>This takes all the individuals selected from tradeoff plots and passes them through a selection filter to decrease the pool size of selected individuals. </Text>  </Info></D2K>";
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
		Do the filtering of individuals to decreases pool size of selected individuals.
	*/
	public void doit () throws Exception {
		Population pop = (Population) this.pullInput(0);
                Table popTable= (Table) this.pullInput(1);

                this.filterSelectedIndividuals (pop, popTable);
                System.out.println("TradeOffSelectionFilterModule");

                //for(int i = 0; i < pop.size();i++){
               //System.out.println(i+"Ranked Flag"+((IGANsgaSolution)pop.getMember(i)).getRankedIndivFlag());
               //}

                

		this.pushOutput (pop, 0);
                this.pushOutput (popTable, 1);
	}

	/**
		Overwrite this method in your module that will inherit this class.
		@param popul: the population.
                @param populTable: Table with individuals with appropriate selection flags.
	*/
	abstract public void filterSelectedIndividuals (Population popul, Table populTable);
}
