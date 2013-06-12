package ncsa.d2k.modules.projects.asingh8.optimize.ga.iga.selectors;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.optimize.ga.emo.*;
import ncsa.d2k.modules.core.optimize.ga.nsga.*;
import ncsa.d2k.modules.projects.mbabbar.optimize.ga.iga.*;
import ncsa.d2k.modules.projects.mbabbar.optimize.ga.iga.selectors.*;
import java.io.*;
import java.util.*;
/**
	Filters the selected individuals from the tradeoff plots. This is done to decrease pool size
        of individuals before they are shown for ranking purposes to the user. This is important for controlling
        human fatigue.
*/
public class ClusteringSelectionFilter extends TradeoffSelectionFilterModule 	{

        //////////////////////////////////
        // other fields
        //////////////////////////////////
        IGANsgaPopulation popul;
        MutableTable populTable;
        String selectionFile = new String("cluster_selection.txt");
        
        
       // int numIndivsSelectedForRanking = 0;

	//////////////////////////////////
	// Info methods
	//////////////////////////////////
        /**
		This method returns the names of the various inputs.

		@return the name of the indexed input.
	*/
        public void setFileName (String fileName){
            selectionFile = fileName;
        }
        
        public String getFileName (){
            return selectionFile;
        }
 
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
        
                public int[] readClusteredIndivsFromFile (IGANsgaPopulation popul, String outFileName ) throws IOException {
//(NsgaPopulation popul, String outFileName ) throws IOException {
                int[] sel = new int[popul.getNumIndivsForUserRanking()] ;
                String[] sline = new String[popul.getNumIndivsForUserRanking()];
                
                for(int i = 0; i<popul.getNumIndivsForUserRanking(); i++){
                    sel[i] = -1;
                }
                try {
                   if (outFileName != null){
                       FileReader stringFileReader = new FileReader(outFileName);
                       BufferedReader br = new BufferedReader(stringFileReader);

                       int i ;
                      // System.out.println("Name of fitness file in read fitness" + outFileName);
                       String dummy = new String();
                       //read generation number
                       dummy = br.readLine();
                       i = 0;
                       
                       while ( (dummy != null )&&(dummy.length()>0) ) {
                      //   System.out.println("sline " + i + " : " + sline[i]);
                          
                         Double db = new Double(dummy);
                         //fit[i] = db.doubleValue();
                         sel[i] = db.intValue();
                         //parseDouble(sline[i]);
                         i= i + 1;
                         dummy = br.readLine();
                       }
                     //  System.out.println("num of fitness in read fitness" + i);
                    //   System.out.println("fitness 1 in read fitness" + fit[1]);
                    //   System.out.println("fitness 145 in read fitness" + fit[145]);


                       // close file and streams
                       br.close();
                       stringFileReader.close();

                    }
                }
                catch (Exception e) {
                       e.printStackTrace();
                       System.err.println("IOException");
                       System.exit(1);
               }
               return sel;
        }

        
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
          
          int [] clusteredIndivs = new int [popul.getNumIndivsForUserRanking()];
          String file = selectionFile;
          
          try {
          clusteredIndivs = readClusteredIndivsFromFile(popul,file );
          }
          catch (Exception e) {
              e.printStackTrace();
              System.exit(1);
              
          }
          
          taggedIndivs = clusteredIndivs;
          int tagId = 0;
          for(int i = 0; i<popul.getNumIndivsForUserRanking(); i++){
              if(taggedIndivs[i]>= 0) tagId++;
          }


          // random selection of individuals
          
          // first select randomly from individuals already tagged as true by user

           // if not all popul.getNumIndivsForUserRanking() individuals tagged
          if(tagId < popul.getNumIndivsForUserRanking()){
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
















