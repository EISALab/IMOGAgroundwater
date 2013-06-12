package ncsa.d2k.modules.projects.asingh8.optimize.ga.iga.io;

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
public class AppendGenerationToFilename extends ncsa.d2k.core.modules.DataPrepModule 	{

        String fileName = new String("Testing_report");
	//////////////////////////////////
	// Info methods
	//////////////////////////////////
	/**
		This method returns the description of the various inputs.

		@return the description of the indexed input.
	*/
        public void setFileName (String file){
            fileName = file;
        }
        
        public String getFileName (){
            return fileName;
        }
        
	public String getOutputInfo (int index) {
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"string\">    <Text>The filename with generation number. </Text>  </Info></D2K>";
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
			default: return "No such input";
		}
	}

	/**
		This method returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo () {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Filename For Output Incremented by Generation\">    <Text>This takes all the individuals selected from tradeoff plots and passes them through a selection filter to decrease the pool size of selected individuals. </Text>  </Info></D2K>";
	}

	//////////////////////////////////
	// Type definitions.
	//////////////////////////////////

	public String[] getInputTypes () {
		String[] types = {"ncsa.d2k.modules.core.optimize.ga.Population"};
		return types;
	}

	public String[] getOutputTypes () {
		String[] types = {"java.lang.String"};
		return types;
	}

        public String getModuleName() {
		return "Append Generation to FileName";
	}
	/**
		Do the filtering of individuals to decreases pool size of selected individuals.
	*/
	public void doit () throws Exception {
                
		Population pop = (Population) this.pullInput(0);
                int i = pop.getCurrentGeneration();
                
                String ind = new String (String.valueOf(i));
                String appendedFileName = new String();
                appendedFileName = ind;
                appendedFileName = appendedFileName.concat(fileName);
                //appendedFileName = appendedFileName.concat(".xls");
                



                

                //for(int i = 0; i < pop.size();i++){
               //System.out.println(i+"Ranked Flag"+((IGANsgaSolution)pop.getMember(i)).getRankedIndivFlag());
               //}

                

		this.pushOutput (appendedFileName, 0);

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
				return "String with Generation appended to Filename";
                	default: return "NO SUCH OUTPUT!";
		}
	}
	/**
		Overwrite this method in your module that will inherit this class.
		@param popul: the population.
                @param populTable: Table with individuals with appropriate selection flags.
	*/
	
}
