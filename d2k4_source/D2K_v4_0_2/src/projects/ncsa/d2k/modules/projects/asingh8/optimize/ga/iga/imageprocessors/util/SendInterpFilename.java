package ncsa.d2k.modules.projects.asingh8.optimize.ga.iga.imageprocessors.util;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.optimize.ga.Population;
import ncsa.d2k.modules.projects.mbabbar.optimize.ga.iga.*;
import ncsa.d2k.modules.core.optimize.ga.emo.*;
import ncsa.d2k.modules.core.optimize.ga.nsga.*;
import ncsa.d2k.modules.core.optimize.util.*;

//import ncsa.d2k.modules.projects.mbabbar.optimize.util.*;
import java.io.*;
import java.util.*;

import ncsa.d2k.modules.core.datatype.table.basic.*;

/**
 * <p>Title: InterpolateExec_BinaryInd </p>
 * <p>Description: This modules takes in two tables as inputs. Table 1 which has information of all
 * the chromosomes that need to be evaluated, and Table 2 that has the information related to executable,
 * inputfile (file that stores the chromosomes), outputfiles (files that store the interpolated grid for that chromosome).
 * The modules then writes one chromosome at a time into the inputfile, and then calls the executable to
 * do the interpolation for that design. Once the interpolation is over the outputfiles names for
 * every individuals and corresponding predicted attributes over the entire grid are stored in a
 * string table (Table 3). The module then passes the Table 1 and Table 3 as outputs into the next modules
 * which will use this information to create images. </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company:NCSA </p>
 * @author Meghna Babbar
 * @version 1.0
 */

public class SendInterpFilename extends ncsa.d2k.core.modules.ComputeModule{
        int numExecFitness;

        // Table 2
        MutableTableImpl fitnessExecs ;
        // Table 1
        MutableTableImpl geneTable ;
        int[] execFitnessIds ;
        int[] numOutputFiles;
        String[] execPathNames ;
        String[] execInputFileNames ;
        String[][] execOutputFileNames ;
        IGANsgaPopulation pop;

        // This will store the output grid file names for the individuals. (read above description for more details)
        // Table 3
        MutableTableImpl interpFilenamesTable ;

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
			case 0: return "A TableImpl data structure that contains the chromosomes that need to be evaluated by the interpolation program ";
			case 1: return "A TableImpl data structure that contains fitness ids for fitnesses evaluated using the executables and the entire paths names for executables with runtime arguments.";
                        case 2:return "IGA NSGA Population";
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
                String[] types = {"ncsa.d2k.modules.core.datatype.table.basic.TableImpl",
                                  "ncsa.d2k.modules.core.datatype.table.basic.TableImpl",
                                  "ncsa.d2k.modules.projects.mbabbar.optimize.ga.iga.IGANsgaPopulation"};
		return types;
	}

	/**
		This method returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo (int index) {
		switch (index) {
			case 0: return "A TableImpl data structure that contains the chromosomes that need to be evaluated by the interpolation program.";
			case 1: return "A TableImpl data structure that contains grid predictions obtained by the interpolation program for different attributes.";
                        case 2: return "IGA NSGA Population";
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
                String[] types = {"ncsa.d2k.modules.core.datatype.table.basic.TableImpl",
                                  "ncsa.d2k.modules.core.datatype.table.basic.TableImpl",
                                  "ncsa.d2k.modules.projects.mbabbar.optimize.ga.iga.IGANsgaPopulation"};
                return types;
	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "Send Interp Filename Table";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Genes Table";
                        case 1:
				return "Fitness Executable table";
                        case 2:
                                return "IGA NSGA Population";
                        
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
				return "Genes Table";
                        case 1:
				return "Interpolation Filenames table";
                        case 2:
                                return "IGA NSGA Population";
                        
			default: return "NO SUCH OUTPUT!";
		}
	}

        //////////////////////////
	///d2k control methods
	//////////////////////////

	public boolean isReady(){
		if(fitnessExecs == null){
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
            numExecFitness = 0;
            fitnessExecs = null;
            geneTable = null;
            execFitnessIds = null;
            numOutputFiles = null;
            execPathNames = null;
            execInputFileNames = null;
            execOutputFileNames = null;
            interpFilenamesTable = null ;
	}

        /////////////////////
	//work methods
	////////////////////

	/**
		Evaluate the individuals in this class.
	*/
       public void doit () throws Exception {
          //  System.out.println("***************************************************");
          //  System.out.println("entering doit function of EvaluateExecutableFitness");
            // Chromosome Table input
            geneTable = (MutableTableImpl) this.pullInput(0);

           // fitnessExecs = new MutableTableImpl();
           // fitnessExecs = (MutableTableImpl) this.pullInput(1);
            while(fitnessExecs == null) {
                fitnessExecs = (MutableTableImpl) this.pullInput(1);
            }
            
            pop = (IGANsgaPopulation) this.pullInput(2);
            
            while (geneTable == null){
                geneTable = (MutableTableImpl) this.pullInput(0);
            }
            while (pop == null){
                pop = (IGANsgaPopulation) this.pullInput(2);    
            }
            
            // **************
            // fitness IDs for the fitnesses calculated by the external executable
            execFitnessIds = this.getFitnessIds();

            execPathNames = new String[getNumFitnessExecs()];
            execPathNames = this.getFitnessNames();

            execInputFileNames = new String[getNumFitnessExecs()];
            execInputFileNames = this.getInputFileNames();

            execOutputFileNames = new String[getNumFitnessExecs()][10];
            execOutputFileNames = this.getOutputFileNames();

            numOutputFiles= new int[getNumFitnessExecs()];
            numOutputFiles= this.getNumOutputFiles();

            //write genes to input file
            
            this.getFileName(execInputFileNames);

            // call interpolation program for that gene
            //this.evaluateIndividual();



            this.pushOutput (geneTable, 0);
            this.pushOutput (interpFilenamesTable, 1);
            this.pushOutput (pop, 2);
            
	}

	
       /*
        * This writes the genes of individuals to input files for different
        * fitness function executables, that might be present in different directories
        */
       // public void writeGeneToFile (double [] indiv, String[] execInputFileNames) throws IOException {
        public void getFileName (String[] execInputFileNames) throws IOException {

            // total number of Outputfiles
            int totalNumOutFiles = 0;
            for (int i = 0; i<  numOutputFiles.length ; i++) {
              totalNumOutFiles = totalNumOutFiles + numOutputFiles[i] ;
            }
            // preparing iinterpolation filenames table for output.
            interpFilenamesTable = new MutableTableImpl(totalNumOutFiles);

            // creating temporary string array to store grid output filenames for all output files.
            int temp1 = pop.size();
            int temp2 = totalNumOutFiles;
            
            System.out.println ("Pop Size  " + temp1);
            System.out.println ("Total Num Out Files  " + temp2);
            
            String [][] interpFiles = new String [pop.size()][totalNumOutFiles];

            // create appropriate filenames for the output files that store the interpolated grid
            // first, loop over all the fitness executables

             // read chromosome ID from table
             for (int i = 0; i < pop.size();i++){
             //geneTable.getNumRows(); i++){
                 Integer geneId = new Integer(0);
                 int counter = 0;
                 for (int j=0; j< numOutputFiles.length; j++) {
                    // second, loop over number of outputfiles for each executable.
                    for (int k=0; k < numOutputFiles[j]; k++){
                        String dest = new String (execOutputFileNames[j][k]+"_"+"Ind"+ geneId.toString(i));
                        interpFiles[i][counter] = dest;
                        //System.out.println("i : " + i + " counter :" +counter + "interpFiles[i][counter]" + interpFiles[i][counter]);
                        counter++;
                    }
                  }
              }

              // add interpolated grid output filenames to interpFilenamesTable
              for(int i = 0; i < totalNumOutFiles; i++){
                  String[] colDat = new String [pop.size()];
                  //geneTable.getNumRows()];
                  for (int j=0; j< pop.size(); j++){
                  //geneTable.getNumRows(); j++){
                      colDat[j] = interpFiles[j][i];
                  }
                  interpFilenamesTable.setColumn(new StringColumn(colDat), i);
              }

              // prepare input files (genes files) for each executable
              
        }


        /*
         * This returns the ID of all the fitness functions that are evaluated using
         * an executable.
         */
        public int[] getFitnessIds() {
           int[] execFitIds = new int[getNumFitnessExecs()];
           for ( int i = 0; i < getNumFitnessExecs(); i++ ){
               execFitIds[i] = fitnessExecs.getInt(i,0);
           }

           return execFitIds;
        }

        /*
         * This returns an array with entire path names of fitness executables
         * and the directory structure where the executable is.
         */
        public String[] getFitnessNames() {
           String[] execFitNames = new String[getNumFitnessExecs()];
           for ( int i = 0; i < getNumFitnessExecs(); i++ ){
             execFitNames[i] = fitnessExecs.getString(i,1);
           }
           return execFitNames;
        }

        /*
         * This returns the input file names of all the fitness functions that are evaluated using
         * an executable. These files would contain the population for evaluation.
         */
        public String[] getInputFileNames() {
           String[] execInputFileName = new String[getNumFitnessExecs()];
           for ( int i = 0; i < getNumFitnessExecs(); i++ ){
               execInputFileName[i] = fitnessExecs.getString(i,2);
           }
           return execInputFileName;
        }

        /*
         * This returns the number of output files of all the fitness functions that are evaluated using
         * an executable. These files would contain the fitness values after evaluation.
         */
        public int[] getNumOutputFiles() {
           int[] OutputFileNum = new int[getNumFitnessExecs()];
           for ( int i = 0; i < getNumFitnessExecs(); i++ ){
               OutputFileNum[i] = fitnessExecs.getInt(i,3);
           }
           return OutputFileNum;
        }

        /*
         * This returns the output file names of all the fitness functions that are evaluated using
         * an executable. These files would contain the fitness values after evaluation.
         */
        public String[][] getOutputFileNames() {
           int[] numFiles = new int[10];
           numFiles = this.getNumOutputFiles();
           String[][] execOutputFileName = new String[getNumFitnessExecs()][10];
           for ( int i = 0; i < getNumFitnessExecs(); i++ ){
              for ( int j = 0; j < numFiles[i]; j++ ){
                  execOutputFileName[i][j] = fitnessExecs.getString(i,j+4);
              }
           }
           return execOutputFileName;
        }

        /*
         * This returns the number of fitness functions that are evaluated using an executable.
         */
        public int getNumFitnessExecs() {
          int size;
          size = fitnessExecs.getNumRows() ;
          return size;
        }

        /**
         * Make a copy of a file.
         * @param src
         * @param dest
         * @throws IOException
         */
        private static void copyFile(File src, File dest) throws
            IOException {
          if (src.equals(dest)) {
            return;
          }

          BufferedInputStream in = new BufferedInputStream(new FileInputStream(src));
          BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(
              dest));

          try {
            byte[] b = new byte[2048];
            int read = 0;

            while ( (read = in.read(b)) != -1) {
              out.write(b, 0, read);
            }

            in.close();
            out.close();
          }
          catch (IOException ioe) {
            in.close();
            out.close();
            throw ioe;
          }
        }

}

