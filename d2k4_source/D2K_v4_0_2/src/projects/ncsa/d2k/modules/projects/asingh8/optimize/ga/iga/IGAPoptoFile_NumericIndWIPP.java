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
 * <p>Title: IGAPoptoFile </p>
 * <p>Description: Write the new population into a file </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company:NCSA </p>
 * @author Abhishek Singh
 * @version 1.0
 */

//public class EvaluateExecutableFitness extends EvaluateModule {
public class IGAPoptoFile_NumericIndWIPP extends ncsa.d2k.core.modules.ComputeModule{
        //int numExecFitness;
        MutableTableImpl outputFiles = new MutableTableImpl();// new TableImpl();
        //int[] execFitnessIds ;
        int numOutputFiles;
        //String[] execPathNames ;
        //String[] execInputFileNames ;
        String[] OutputFileNames ;

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
			case 1: return "A TableImpl data structure that contains paths names for files for output.";
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
		return "IGA: PoptoFile_NumericInd";
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
				return "Output File Name table";
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

	public boolean isReady(){
		if(outputFiles == null){
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
          //  numExecFitness = 0;
            outputFiles = null;
            //execFitnessIds = null;
            numOutputFiles = 0;
            //execPathNames = null;
            //execInputFileNames = null;
            OutputFileNames = null;
    	}


	/**
		Evaluate the individuals in this class.
	*/
       public void doit () throws Exception {

           IGANsgaPopulation pop = (IGANsgaPopulation) this.pullInput(0);
            
            
            if(outputFiles == null) {
                outputFiles = (MutableTableImpl) this.pullInput(1);

            }
            
            int numOutputFiles;
            numOutputFiles= this.getNumFiles();

            OutputFileNames = new String[getNum()];
            Integer gen = new Integer(0);
            String genstr = new String(gen.toString(pop.getCurrentGeneration ()));
            OutputFileNames = this.getFileNames(genstr);

            // writing individual genes to files
            
            this.writeGenesToFile(pop,OutputFileNames);
           
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
        public void writeGenesToFile (IGANsgaPopulation popul, String[] OutputFileNames) throws IOException {
        //(NsgaPopulation popul, String[] execInputFileNames) throws IOException {

          //  String genes = new String() ;

            for (int i = 0 ; i < getNumFiles(); i++) {
                try {
                   if (OutputFileNames[i] != null){
                       FileWriter stringFileWriter = new FileWriter(OutputFileNames[i]);
                       BufferedWriter bw = new BufferedWriter(stringFileWriter);
                       PrintWriter pw = new PrintWriter(bw);

                       // NsgaSolution ni = (NsgaSolution)  popul.getMember(0);
			//double [] genes = (double []) ((Individual) ni).getGenes ();
                       MONumericIndividual ni = (MONumericIndividual) popul.getMember(0);
		       double [] genes = (double []) ni.getGenes ();
                       
                       //boolean [] genes = (boolean []) ni.getGenes ();
                       //  int [] genes = (int [])popul.getMember(0).getGenes ();
                       int numTraits = genes.length;
                       System.out.println("genes length" + numTraits);
                       System.out.println("*****************************");
                       System.runFinalization();
                       //System.out.println("popul size in "+ execInputFileNames[i]+"is "+ popul.size ());

                       // write population size in file
                       pw.println(popul.getCurrentGeneration ());
                       pw.println(popul.size ());
                       // write length of each gene/chromosome
                       pw.println(numTraits);
                       // write genes
                       for ( int j = 0; j < popul.size (); j++ ){
                       //   genes = popul.getMember(j).getGenes().toString();
                          genes = (double [])((MONumericIndividual) popul.getMember(j)).getGenes ();
	                
                          //genes = (boolean [])((MOBinaryIndividual) popul.getMember(j)).getGenes ();
        	          numTraits = genes.length;

                          //if (popul.getMember(j).isDirty()){
                          // write to file
                          // System.out.println("numtraits :" + numTraits);
                             for (int k =0; k < numTraits; k++){
//                             if((k == 0)||(k==1)||(k==6)||(k==9)){
//                                 pw.print(Math.abs(Math.round(genes[k])) );
//                             }else{
                                 pw.print(genes[k] );
                             //}
                             pw.println();
                             }
                             //pw.println();
                             pw.println();
                             pw.println();
                             //System.out.println("indiv id "+ j);
                          // }

                        }
                       pw.println();
                       pw.println();
                        for ( int j = 0; j < popul.size (); j++ ){
                            for (int k = 0; k < popul.getNumObjectives();k++){
                                 pw.print(((MONumericIndividual) popul.getMember(j)).getObjective(k));
                                 pw.print(", ");
                             }
                             pw.print(((MONumericIndividual) popul.getMember(j)).getRank());
                             pw.println();
                        }
                       pw.println();
                       // close file and streams
                       pw.flush();
                       bw.flush();
                       stringFileWriter.flush();
                       pw.close();
                       bw.close();
                       stringFileWriter.close();

                       }
                    }
                catch (Exception e) {

                       e.printStackTrace();
                       System.err.println("IOException");
                       System.exit(1);

                       }
                }

        }


        /*
         * This returns the number of output files of all the fitness functions that are evaluated using
         * an executable. These files would contain the fitness values after evaluation.
         */
        public int getNumFiles() {
           int OutputFileNum;
          // for ( int i = 0; i < getNumOutputs(); i++ ){
               OutputFileNum = outputFiles.getInt(0,0);
           //}
           return OutputFileNum;
        }

        /*
         * This returns the output file names of all the fitness functions that are evaluated using
         * an executable. These files would contain the fitness values after evaluation.
         */
        public String[] getFileNames(String genstr) {
           int numFiles;
           numFiles = this.getNumFiles();
           String[] OutputFileName = new String[getNumFiles()];
           //for ( int i = 0; i < getNumOutputs(); i++ ){
              for ( int j = 0; j < numFiles; j++ ){
                  
                  
                  OutputFileName[j] = "Gen"+genstr+"_"+outputFiles.getString(0,j+1);
                  
              }
           //}
           return OutputFileName;
        }

        /*
         * This returns the number of fitness functions that are evaluated using an executable.
         */
        public int getNum() {
          int size;
          size = outputFiles.getNumRows() ;
          return size;
        }


}

