package ncsa.d2k.modules.projects.mbabbar.optimize.ga.iga.selectors;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.optimize.ga.emo.*;
import ncsa.d2k.modules.core.optimize.ga.nsga.*;
import ncsa.d2k.modules.projects.mbabbar.optimize.ga.iga.*;

/**
	Filters the selected individuals from the tradeoff plots. This is done to decrease pool size
        of individuals before they are shown for ranking purposes to the user. This is important for controlling
        human fatigue.
*/
public class RandomTradeoffSelectionFilter extends TradeoffSelectionFilterModule 	{

        //////////////////////////////////
        // other fields
        //////////////////////////////////
        IGANsgaPopulation popul;
        MutableTable populTable;
       // int numIndivsSelectedForRanking = 0;

	//////////////////////////////////
	// Info methods
	//////////////////////////////////
        /**
		This method returns the names of the various inputs.

		@return the name of the indexed input.
	*/
	public String getOutputName (int index) {
		switch (index) {
			case 0: return "IGA Nsga Population";
			case 1: return "Table";
                        default: return "No such output";
		}
}

	/**
		This method returns the names of the various inputs.

		@return the name of the indexed input.
	*/
	public String getInputName (int index) {
		switch (index) {
			case 0: return "IGA Nsga Population";
			case 1: return "Table";
                        default: return "No such input";
		}
	}


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
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Selection Filter\">    <Text>This takes all the individuals selected from tradeoff plots and passes them through a selection filter to decrease the pool size of selected individuals by choosing individuals randomly from the selected pool. </Text>  </Info></D2K>";
	}

        // set number of individuals that are selected for human ranking every IGA session
       /* public void setNumIndivsSelectedForRanking (int num){
          numIndivsSelectedForRanking = num;

        }

        // get number of individuals that are selected for human ranking every IGA session
        public int getNumIndivsSelectedForRanking (){
          return numIndivsSelectedForRanking;
        }*/


        //////////////////////////////////
        // work methods
        //////////////////////////////////

	/**
		Overwrite this method in this module that inherits TradeoffSelectionFilterModule class.
		@param popul: the population.
                @param populTable: Table with individuals with appropriate selection flags.
	*/
	public void filterSelectedIndividuals (Population pop, Table popTable){

          popul = (IGANsgaPopulation) pop;
          populTable = (MutableTable) popTable;
         // if (numIndivsSelectedForRanking > 0) {
         //   popul.setNumIndivsForUserRanking(numIndivsSelectedForRanking);
         // }
          
          
          IGANsgaSolution [] ind = new IGANsgaSolution[popul.size()];
          for (int i = 0; i < popul.size(); i++) {
              ind[i] = (IGANsgaSolution) popul.getMember(i);
          }

        //  System.out.println("getNumIndivsForUserRanking  " + popul.getNumIndivsForUserRanking());
          // temporary array for storing filtered indivs that will be finally ranked by the user
          int [] taggedIndivs = new int [popul.getNumIndivsForUserRanking()];
          for(int i =0; i < popul.getNumIndivsForUserRanking();  i++) {
              taggedIndivs[i] = -1;
          }


          // random selection of individuals
          int tagId = 0;
          // first select randomly from individuals already tagged as true by user
          for (int i =0; i < populTable.getNumRows(); i++){
            if (tagId < popul.getNumIndivsForUserRanking()){
              if (populTable.getBoolean(i, populTable.getNumColumns() - 1) == true ){
                double rand = Math.random();
                // flip a coin
                if (rand >= 0.5){
                  taggedIndivs[tagId] = i;
                  tagId++;
                } // if rand
              } // if populTable
            }// if tagId
           } // for i
           // if not all popul.getNumIndivsForUserRanking() individuals tagged
           for (int i =0; i < populTable.getNumRows(); i++){
            if (tagId < popul.getNumIndivsForUserRanking()){
              int alreadyTagged = 0;
              // check if already selected
              for (int j =0; j < popul.getNumIndivsForUserRanking(); j++){
                  if (taggedIndivs[j] == i) {
                    alreadyTagged = 1;
                  }
              }
              if (alreadyTagged == 0) {
                  taggedIndivs[tagId] = i;
                  tagId++;
              }
            }
           }

          // updating the selection flags in the table
          for (int i =0; i < populTable.getNumRows(); i++){

              int j = 0;

              // initialize selection entry to false
              populTable.setBoolean(false,i,populTable.getNumColumns() - 1);

              // set selection entry to true only if tagged
              while (j < popul.getNumIndivsForUserRanking()){
                  if (taggedIndivs [j] == i){
                      populTable.setBoolean(true,i,populTable.getNumColumns() - 1);
                  }
                  j++;
              }
          } // for i
        }
}
















