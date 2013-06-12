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
public class PrepareDataforModel extends ncsa.d2k.core.modules.DataPrepModule {
    

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
				return "The table containing the examples that the model will be built on";
			case 1:
				return "The table containing the examples that the model will be applied to";
                        case 2:
                                return "The original Population";
			default:
				return "No such input";
		}
	}

	/**
		This method returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo () {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Prepare Data Module\">    <Text>This forms the training data from ranked individuals and the testing data from unranked indivs for Model (Decision Trees). </Text>  </Info></D2K>";
	}
        
        
        	//////////////////////////////////
	// Type definitions.
	//////////////////////////////////

	public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.projects.mbabbar.optimize.ga.IGANsgaPopulation"};
		return types;
	}

	public String[] getOutputTypes () {
		String[] types = {
                    "ncsa.d2k.modules.core.datatype.table.ExampleTable",
		    "ncsa.d2k.modules.core.datatype.table.ExampleTable",
                    "ncsa.d2k.modules.projects.mbabbar.optimize.ga.IGANsgaPopulation"};
		return types;
	}


	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "IGA: Prepare data for Model (Decision Tree)";
	}
        
	public void doit() throws Exception {
		IGANsgaPopulation pop = (IGANsgaPopulation) this.pullInput (0);
                //pop.traits;
                
		ExampleTable vt1 = pop.getHumanArchiveTable ();
                ExampleTable vt2 = (pop.getTable ()).toExampleTable();
                
                int nc = vt2.getNumColumns();
                int vc = vt1.getNumColumns();
                vc = (vt1.getColumn(nc-3)).getNumRows();
                
                /* Remove the crowding and rank columns from table*/
                vt2.removeColumn(nc-1);
                vt2.removeColumn(nc-2);
                
                //remove the individuals that are already ranked and are in the archive
                int len = vt2.getNumRows();
                System.out.println("size of table "+len);
                for (int i =len-1;i>= 0; i--){
                    IGANsgaSolution nis = (IGANsgaSolution) (pop.getMember(i));
                    if(nis.getRankedIndivFlag()== true){
                        vt2.removeRow(i);
                        System.out.println("removing "+i);
                    }
                }
                
                System.out.println("size of table "+ vt2.getNumRows());
                //remove the first half of population (that has been ranked) 
                //this is only to test the prediction model
                //REMOVE AFTER TESTING
                //vt2.removeRows(0,(int) (vt2.getNumRows()/2));
                //do not use variogram variables
                int [] testingSet = new int [vt2.getNumRows()];
                int [] inputCol = new int [nc-3-8];
                int [] outputCol = new int [1];
                int numi = 0;
                for (int i = 0;i < nc-3; i++) {
                    if((i < 6)||(i > 13)){
                        
                    inputCol[numi] = i;
                    numi = numi+1;
                    }
                }
                
                for (int i = 0; i < vt2.getNumRows(); i++){
                    testingSet[i]= i;
                }
                outputCol[0] = nc-3;
                //set the last column as nominal
                boolean val = true;
                vt2.setColumnIsNominal(val, nc-3);
                
                vt2.setInputFeatures(inputCol);
                vt1.setInputFeatures(inputCol);
                
                vt2.setOutputFeatures(outputCol);
                vt1.setOutputFeatures(outputCol);
                
                //val = vt2.isColumnScalar(nc-3);
                //val = vt1.isColumnScalar(nc-3);
                //vt2.setTrainingSet(trainingSet);
                
                if(pop.getTotalNumIndivsRankedInArchive() > 0) {
                this.pushOutput (vt1, 0);
		this.pushOutput (vt2, 1);
                }
                else {
                 //   vt2.setColumn(newEntry, position);
                this.pushOutput (vt2, 0);
		this.pushOutput (vt2, 1);              
                                
                }
                
                this.pushOutput (pop, 2);
		
	}
        
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
				return "Training Example Table";
                        case 1:
				return "Testing Example table";
                        case 2:
                                return "Original Population";
			default: return "NO SUCH OUTPUT!";
		}
	}



   
    
}
