/*
 * DecisionTreePrediction.java
 *
 * Created on June 25, 2004, 2:07 PM
 */

package ncsa.d2k.modules.projects.asingh8.optimize.ga.iga;

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
public class PoptoTable extends ncsa.d2k.core.modules.DataPrepModule {
    

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
				return "The table containing the population";
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
                    "ncsa.d2k.modules.core.datatype.table.ExampleTable"};
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
                
		ExampleTable vt2 = (pop.getTable ()).toExampleTable();
                this.pushOutput (vt2, 0);              
                                
                
                
                
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
				return "Population Table";
                	default: return "NO SUCH OUTPUT!";
		}
	}



   
    
}
