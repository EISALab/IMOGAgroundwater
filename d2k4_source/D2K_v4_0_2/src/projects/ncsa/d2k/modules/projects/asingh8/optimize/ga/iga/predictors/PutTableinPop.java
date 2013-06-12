/*
 * DecisionTreePrediction.java
 *
 * Created on June 25, 2004, 2:07 PM
 */

package ncsa.d2k.modules.projects.asingh8.optimize.ga.iga.predictors;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.optimize.ga.emo.*;
import ncsa.d2k.modules.core.optimize.ga.nsga.*;
import ncsa.d2k.modules.projects.mbabbar.optimize.ga.iga.*;
import ncsa.d2k.modules.core.datatype.table.*;
/**
 *
 * @author  Abhishek Singh
 */
public class PutTableinPop extends ncsa.d2k.core.modules.DataPrepModule {
    

	//////////////////////////////////
	// Info methods
	//////////////////////////////////
	/**
		This method returns the description of the various inputs.

		@return the description of the indexed input.
	*/
	public String getInputInfo (int index) {
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"population\">    <Text>input population, with ranked and unranked individuals. </Text>  </Info></D2K>";
                    case 1: return "The table with prediction from Model";
			default: return "No such input";
		}
	}
	/**
		This method returns the description of the various inputs.

		@return the description of the indexed input.
	*/
		public String getOutputInfo(int index) {
		switch (index) {
			case 0:
				return "The population with predictions";
			default:
				return "No such input";
		}
	}

	/**
		This method returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo () {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Prepare Data Module\">    <Text>This module takes in the original population and the results from prediction model and puts these into the population . </Text>  </Info></D2K>";
	}
        
        
        	//////////////////////////////////
	// Type definitions.
	//////////////////////////////////

	public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.projects.mbabbar.optimize.ga.IGANsgaPopulation",
                "ncsa.d2k.modules.core.datatype.table.PredictionTable" };
		return types;
	}

	public String[] getOutputTypes () {
		String[] types = {
                    "ncsa.d2k.modules.projects.mbabbar.optimize.ga.IGANsgaPopulation"};
		return types;
	}


	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "IGA: Put data into Pop from Model (Decision Trees)";
	}
        
	public void doit() throws Exception {
		IGANsgaPopulation pop = (IGANsgaPopulation) this.pullInput (0);
                PredictionTable pt = (PredictionTable) this.pullInput (1);
		/* call function in population to put table into pop*/
                this.tabletoPop (pop, pt);     
                this.pushOutput (pop, 0);
		
	}
        
        	public void tabletoPop (Population pop, PredictionTable pt){

                IGANsgaPopulation popul = (IGANsgaPopulation) pop;

                // if individuals have been ranked by user:
                
                if (popul.getTotalNumIndivsRankedInArchive() > 0){
                    for (int m =0; m < popul.getNumObjectives(); m++){
                      if (popul.igaQualObj[m] == true) {
                          

                          // assign fitnesses to unranked individuals
                          int j=0;
                          for (int i = 0; i < popul.size();i++) {
                              if (((IGANsgaSolution)popul.getMember(i)).getRankedIndivFlag() == false) {
                            
                                 ((IGANsgaSolution)popul.getMember(i)).setObjective(m, pt.getDoublePrediction(j,0));
                                 j = j+1;
                              }else{
                                  ((IGANsgaSolution)popul.getMember(i)).setObjective(m,((IGANsgaSolution)popul.getMember(i)).getObjective(m)-0.25);
                                  
                              }
                          }

                      } // If popul.igaQualObj[m]
                    } // For m < popul.getNumObjectives()
                }

                // if individuals have not been ranked by the user:
                else {
                    for (int i = 0; i < popul.size();i++) {
                        for (int j =0; j < popul.getNumObjectives(); j++){
                           
                            if (popul.igaQualObj[j] == true){
                                // initialize all individuals to qual fitnesses of value 0
                                ((IGANsgaSolution)popul.getMember(i)).setObjective(j,3);
                            }
                        }

                    }
                }


        }
        
        	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Population";
                        case 1: 
                                return "Prediction Table";
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
				return "Original Population";
			default: return "NO SUCH OUTPUT!";
		}
	}



   
    
}
