package ncsa.d2k.modules.projects.asingh8.optimize.ga.iga;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.optimize.ga.Population;
import ncsa.d2k.modules.core.optimize.ga.emo.*;
import ncsa.d2k.modules.core.optimize.ga.nsga.*;
import ncsa.d2k.modules.core.optimize.util.*;
//import ncsa.d2k.modules.projects.mbabbar.optimize.util.*;
import java.io.*;
import java.util.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.projects.mbabbar.optimize.ga.iga.*;
import ncsa.d2k.modules.projects.mbabbar.optimize.ga.iga.imageprocessors.util.*;
/**
 * <p>Title: IGAEvaluateExecutableFitness </p>
 * <p>Description: Evaluate the new population using an executable file for fitness evaluation.
 *      The population object does all the work, this module will simply invoke the
 *      <code>evaluateAll</code> method of the population. </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company:NCSA </p>
 * @author Meghna Babbar
 * @version 1.0
 */

//public class EvaluateExecutableFitness extends EvaluateModule {
public class IGACopyGenes_NumericIndPPV extends ncsa.d2k.core.modules.ComputeModule{
        
        MutableTableImpl filenameTable = new MutableTableImpl();// new TableImpl();
        
        String initiateFileName;
        String selectedGenesFileName;
        

	//////////////////////////////////
	// Info methods
	//////////////////////////////////

	/**
		This method returns the description of the module.
		@return the description of the module.
	*/

	public String getModuleInfo () {
		return "<html>  <head>      </head>  <body>    Evalute this population using a fitness executable.  </body></html>";
	}

        /**
		This method returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "The population contains all individuals for the multiobjective problem";
			case 1: return "A TableImpl data structure that contains the entire path for the restart file.";
			default: return "No such input";
		}
	}

	/**
		This method returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes () {
		//String[] types = {"ncsa.d2k.modules.core.optimize.ga.emo.NsgaPopulation",
                  //                "ncsa.d2k.modules.core.datatype.table.basic.TableImpl"};
                String[] types = {"ncsa.d2k.modules.core.optimize.ga.Population",
                                  "ncsa.d2k.modules.core.datatype.table.basic.TableImpl"};
		return types;
	}

	/**
		This method returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo (int index) {
		switch (index) {
			case 0: return "<p>      The population with the updated genes.    </p>";
			default: return "No such output";
		}
	}

	/**
		This method returns an array of strings that contains the data types for the outputs.
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes () {
		//String[] types = {"ncsa.d2k.modules.core.optimize.ga.emo.NsgaPopulation",
                      //            "ncsa.d2k.modules.core.datatype.table.basic.TableImpl"};
                String[] types = {"ncsa.d2k.modules.core.optimize.ga.Population"};

                return types;
	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "IGA: CopyGenes_NumericInd";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "IGANsgaPopulation";
                        case 1:
				return "Genes file table";
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
				return "MsgaPopulation";
			default: return "NO SUCH OUTPUT!";
		}
	}
        //////////////////////////
	///d2k control methods
	//////////////////////////

	public boolean isReady(){
		if(filenameTable == null){
			return super.isReady();
		}else{
			return ((this.getFlags()[0] > 0));
		}
	}

	public void endExecution(){
		wipeFields();
		super.endExecution();
	}

	public void beginExecution(){
		wipeFields();
		super.beginExecution();
	}

	public void wipeFields(){
            
            filenameTable = null;
            initiateFileName = null;
            selectedGenesFileName = null;
            
    	}


	/**
		Evaluate the individuals in this class.
	*/
       public void doit () throws Exception {
          //  System.out.println("***************************************************");
          //  System.out.println("entering doit function of EvaluateExecutableFitness");
            IGANsgaPopulation pop = (IGANsgaPopulation) this.pullInput(0);
            for(int i = 0; i< pop.size(); i++){
                  ((IGANsgaSolution)pop.getMember(i)).setRankedIndivFlag(false);
                  ((IGANsgaSolution)pop.getMember(i)).setObjective(2,3);
                   for(int j = 0; j < pop.getTotalNumIndivsRankedInArchive();j++){
                   double[] ind1 = (double []) pop.getIndInHumanRankedPopulationArchive(j).getGenes();     
                   double[] ind2 = (double []) pop.getMember(i).getGenes();
                   double archiveObj = (double) ((IGANsgaSolution) pop.getIndInHumanRankedPopulationArchive(j)).getObjective(2);     
                   int notsame = 0;
                   for(int k = 0; k < ind1.length;k++){
                       if(ind1[k] != ind2[k]) notsame = 1;
                   }
                   if(notsame != 1){
                  ((IGANsgaSolution)pop.getMember(i)).setRankedIndivFlag(true);
                  ((IGANsgaSolution)pop.getMember(i)).setObjective(2,archiveObj);
                  System.out.println("similar solution in archive");
                   }
                  }
            }
            //Population pop = (Population) this.pullInput(0);
            // ***************

            if(filenameTable == null) {
                filenameTable = (MutableTableImpl) this.pullInput(1);

            }
           
            // **************
            
            initiateFileName = filenameTable.getString(0,1);
            selectedGenesFileName = filenameTable.getString(0,2);
            
            // writing individual genes to pop
            
            if(pop.restart){
               this.copyIndividuals(pop); 
              
            }
            else {
                System.out.println("Turn on Restart flag if you want to Copy Pop from File ");
                //System.exit(1);
            }
             
            this.pushOutput (pop, 0);

            
            //this.pushOutput (fitnessExecs, 1);
	}

	public void copyIndividuals (IGANsgaPopulation popul) {//(NsgaPopulation popul) { //

            //MONumericIndividual[] ni = new MONumericIndividual[popul.size()] ;
            MONumericIndividual[] ni = new MONumericIndividual[popul.size()] ;

            String inputFileName = new String(initiateFileName);
            String selectedFileName = new String(selectedGenesFileName);

            for (int i = 0; i < popul.size(); i++ ){
		ni[i] = (MONumericIndividual) popul.getMember(i);
                //ni[i] = (MOBinaryIndividual) popul.getMember(i);

                }
   

                /* Evaluate the Fitnesses using the fitness executable path names and alloting
                   the fitness values to respective ids in the population
                */

           


                    // ***************** call execrunner
                    /**
                     * @param args an array of command-line arguments
                     * @throws IOException thrown if a problem occurs
                    **/


                 


                 try {
                        
                        int numGenesRestart = this.readNumGenes(popul,inputFileName);
                        int numCopy = this.readNumSelect(popul,selectedFileName);
                        
                        double[][] genesTemp = new double[numGenesRestart][popul.getNumGenes()];
                        int[] selectedGenes = new int[numCopy];       
                        
                        
                        // reading genes of all individuals from initiate file
                       
                       
                       
                          // System.out.println("name of fitness file : " + outputFileName);
                          // System.out.println(" **************** ");
                           
                           
                           selectedGenes = this.readSelectedGenes(popul, selectedFileName);
                           genesTemp = this.readGenesFromFile(popul,inputFileName, numGenesRestart);
                           
                           for (int j = 0; j < numCopy; j++ ){
                                ni[j].setGenes(genesTemp[selectedGenes[j]]);
                            //  System.out.println("Fitness" + fitnessID + " in individual : " + j + "is " + fitness[j] );
                            }

                         //  System.out.println("Fitness in module : " + fitnessID );
                         //  System.out.println("Fitness 1 in individual : " + fitness[0] );
                         //  System.out.println("Fitness 2 in individual : " + fitness[1] );
                           
                       
                        

                  }
                  catch (Exception e) {

                       e.printStackTrace();
                       System.exit(1);

                  }



	}// evaluateIndividual

        
       public int[] readSelectedGenes(Population popul, String inFileName) throws IOException{
           int[] selected = new int[popul.size()];
           try{
               if (inFileName != null){
                       FileReader stringFileReader = new FileReader(inFileName);
                       BufferedReader br = new BufferedReader(stringFileReader);

                   
                       
                      // System.out.println("Name of fitness file in read fitness" + outFileName);
                       String dummy = new String();
                       //number of indivs to copy
                       dummy = br.readLine();
                       Integer numSelected = new Integer(dummy);
                       if(numSelected.intValue()>popul.size()){                       
                           numSelected = new Integer(popul.size());
                       }
                       for(int i = 0; i<numSelected.intValue();i++){
                           dummy = br.readLine();
                           Integer id = new Integer(dummy);
                           selected[i] = id.intValue();
                       }
                       
                       br.close();
                       stringFileReader.close();
                       
               }
               
           }
           catch (Exception e){
                       e.printStackTrace();
                       System.err.println("IOException");
                       System.exit(1); 
           }
           return selected;
           
       }
       
       
       
       public int readNumSelect(Population popul, String inFileName) throws IOException {
           int num = 0;
           try {
               if (inFileName != null){
                       FileReader stringFileReader = new FileReader(inFileName);
                       BufferedReader br = new BufferedReader(stringFileReader);

                   
                       
                      // System.out.println("Name of fitness file in read fitness" + outFileName);
                       String dummy = new String();
                       //number of indivs to copy
                       dummy = br.readLine();
                       Integer numInd = new Integer(dummy);
                       if(numInd.intValue()>popul.size()){                       
                           numInd = new Integer(popul.size());
                       }                   
                       br.close();
                       stringFileReader.close();
                       num = numInd.intValue();
                       
               }
           }
           catch (Exception e){
                       e.printStackTrace();
                       System.err.println("IOException");
                       System.exit(1);               
           }
           return num;
       }
       
          public int readNumGenes(Population popul, String inFileName) throws IOException {
           int num = 0;
           try {
               if (inFileName != null){
                       FileReader stringFileReader = new FileReader(inFileName);
                       BufferedReader br = new BufferedReader(stringFileReader);

                   
                       
                      // System.out.println("Name of fitness file in read fitness" + outFileName);
                       String dummy = new String();
                       //read gen
                       dummy = br.readLine();
                       //number of indivs to copy
                       dummy = br.readLine();
                       Integer numInd = new Integer(dummy);
                                          
                       br.close();
                       stringFileReader.close();
                       num = numInd.intValue();
                       
               }
           }
           catch (Exception e){
                       e.printStackTrace();
                       System.err.println("IOException");
                       System.exit(1);               
           }
           return num;
       }

       /*
        * This read the fitness of individuals from output files for different
        * fitness function executables, that might be present in different directories
        */
        public double[][] readGenesFromFile (IGANsgaPopulation popul, String inFileName, int numGenes ) throws IOException {
//(NsgaPopulation popul, String outFileName ) throws IOException {
                double[][] genes = new double[numGenes][popul.getNumGenes()];
                
                try {
                   if (inFileName != null){
                       FileReader stringFileReader = new FileReader(inFileName);
                       BufferedReader br = new BufferedReader(stringFileReader);

                   
                       
                      // System.out.println("Name of fitness file in read fitness" + outFileName);
                       String dummy = new String();
                       

                       //read generation number
                       dummy = br.readLine();
                       Integer numInd = new Integer(dummy);
                       popul.setGeneration(numInd.intValue());
                       
                       //number of indivs to copy
                       dummy = br.readLine();
                       numInd = new Integer(dummy);
                       
                       //read gene size
                       dummy = br.readLine();
                       
                       for (int i = 0; i<numInd.intValue();i++){
                           for(int j = 0; j<popul.getNumGenes();j++){
                               dummy = br.readLine();
                               Double db = new Double(dummy);
                               genes[i][j]= db.parseDouble(dummy);
                           }
                           dummy = br.readLine();
                           dummy = br.readLine();
                       }
                       
                       br.close();
                       stringFileReader.close();

                    }
                }
                catch (Exception e) {
                       e.printStackTrace();
                       System.err.println("IOException");
                       System.exit(1);
               }
               return genes;
        }


   

}

