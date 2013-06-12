package ncsa.d2k.modules.projects.asingh8.optimize.ga;

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
 * <p>Title: IGAPoptoFile </p>
 * <p>Description: Write the new population into a file </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company:NCSA </p>
 * @author Abhishek Singh
 * @version 1.0
 */

//public class EvaluateExecutableFitness extends EvaluateModule {
public class RestartSwitch extends ncsa.d2k.core.modules.ComputeModule{
        //int numExecFitness;
        
        boolean restart = false;

	//////////////////////////////////
	// Info methods
	//////////////////////////////////

	/**
		This method returns the description of the module.
		@return the description of the module.
	*/

	public String getModuleInfo () {
		return "<html>  <head>      </head>  <body>    Print Population into a file.  </body></html>";
	}

        /**
		This method returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "The population contains all individuals for the multiobjective problem";
			
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
                String[] types = {"ncsa.d2k.modules.core.optimize.ga.Population"};
		return types;
	}

	/**
		This method returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo (int index) {
		switch (index) {
			case 0: return "<p> The NSGA population .    </p>";
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
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "GA Restart Switch";
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
				return "Nsga Population";
			default: return "NO SUCH OUTPUT!";
		}
	}
        //////////////////////////
	///d2k control methods
	//////////////////////////


	public void endExecution(){
		wipeFields();
		super.endExecution();
	}

	public void beginExecution(){
		wipeFields();
		super.beginExecution();
	}

	public void wipeFields(){
          //  numExecFitness = 0;
    	}
          
          public void setRestart(boolean res){
              restart = res;
          }
          
          public boolean getRestart(){
              return restart;
          }

	/**
		Evaluate the individuals in this class.
	*/
       public void doit () throws Exception {

           IGANsgaPopulation pop = (IGANsgaPopulation) this.pullInput(0);
            
           pop.setRestart(restart);
            
           
            this.pushOutput (pop, 0);
            //this.pushOutput (fitnessExecs, 1);
	}

	

       /*
        * This writes the genes of individuals to input files for different
        * fitness function executables, that might be present in different directories
        */
        /**
         * @param popul
         * @param execInputFileNames
         * @throws IOException
         */        
  

        /*
         * This returns the number of output files of all the fitness functions that are evaluated using
         * an executable. These files would contain the fitness values after evaluation.
         */


}

