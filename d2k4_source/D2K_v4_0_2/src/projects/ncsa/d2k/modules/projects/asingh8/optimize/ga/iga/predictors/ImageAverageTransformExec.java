package ncsa.d2k.modules.projects.asingh8.optimize.ga.iga.predictors;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.optimize.ga.Population;
import ncsa.d2k.modules.core.optimize.ga.emo.*;
import ncsa.d2k.modules.core.optimize.ga.nsga.*;
import ncsa.d2k.modules.core.optimize.util.*;
import ncsa.d2k.modules.projects.mbabbar.optimize.ga.iga.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

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

public class ImageAverageTransformExec extends ncsa.d2k.core.modules.ComputeModule{
        int numExecFitness;
        IGANsgaPopulation pop;
        
        // Table 2
        MutableTableImpl fitnessExecs ;
        // Table 1
        ExampleTable archiveTable ;
        
        int[] execFitnessIds ;
        int[] numOutputFiles;
        String[] execPathNames ;
        String[] execInputFileNames ;
        String[][] execOutputFileNames ;

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
        String matlabExecName = new String("myImageAveraging");
        String matlabOutputName = new String("_statistics.out");
        int maxLevel = 2;
        
        
        public String getExecName(){
            return matlabExecName;
        }
        
        public void setExecName(String exec){
            matlabExecName = exec;
        }

        public String getFileName(){
            return matlabOutputName;
        }
        
        public void setFileName(String file){
            matlabOutputName = file;
        }

        
	public int getMaxLevel(){
		return maxLevel;
	}
        
	public void setMaxLevel(int i){
		maxLevel=i;
	}
        
        
        
	public String getModuleInfo () {
		return "<html>  <head>      </head>  <body>    Evalute this population using a fitness executable.  </body></html>";
	}

        /**
		This method returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "NSGA Population";
                        case 1: return "Table with Interpolation Inds";
			case 2: return "Fitness Executable Table";
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
                                  "ncsa.d2k.modules.core.datatype.table.basic.TableImpl",
                                  "ncsa.d2k.modules.core.datatype.table.basic.TableImpl"};
		return types;
	}

	/**
		This method returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo (int index) {
		switch (index) {
                        case 0: return "NSGA Population";
			case 1: return "A TableImpl data structure that contains the chromosomes that need to be evaluated by the interpolation program.";
                        case 2: return "string containing name of output file";
		//	case 1: return"A TableImpl data structure that contains grid predictions obtained by the interpolation program for different attributes.";
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
                String[] types = {"ncsa.d2k.modules.core.optimize.ga.Population",
                                  "ncsa.d2k.modules.core.datatype.table.basic.TableImpl",
                                  "java.lang.String"
                //                    ,"ncsa.d2k.modules.core.datatype.table.basic.TableImpl"
                                  };
                return types;
	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "Interpolate Archive and run Matlab exec";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "NSGA Population";
                        case 1:
                                return "Indivs Table";
                        case 2:
				return "Fitness Executable table";
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
                                return "NSGA Population";
			case 1:
				return "Genes Table";
                        case 2:
                                return "Output file name";
                        //case 1:
			//	return "Interpolation Filenames table";
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
            pop = null;
            numExecFitness = 0;
            fitnessExecs = null;
            archiveTable = null;
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
           
           pop = null;
           
           while(pop == null){           
           pop = (IGANsgaPopulation) this.pullInput(0);
           }
           
           archiveTable = null;
           
           while(archiveTable == null){
           archiveTable = (ExampleTable) this.pullInput(1);
           }
           // fitnessExecs = new MutableTableImpl();
           // fitnessExecs = (MutableTableImpl) this.pullInput(1);
            while(fitnessExecs == null) {
                fitnessExecs = (MutableTableImpl) this.pullInput(2);
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

            // write genes to input file
            this.writeGeneToFile(execInputFileNames);

            // call interpolation program for that gene
            this.evaluateIndividual();

            this.callAveragingExec();

            
            this.pushOutput (pop, 0);
            this.pushOutput (archiveTable, 1);
            this.pushOutput(matlabOutputName, 2);
	}

	public void evaluateIndividual () {//(NsgaPopulation popul) { //


            /* Evaluate the Fitnesses using the fitness executable path names and alloting
               the fitness values to respective ids in the population
            */
            int fitnessID = 0;
            for (int i = 0; i < getNumFitnessExecs(); i++ ){

                    // ***************** call execrunner
                    /**
                     * @param args an array of command-line arguments
                     * @throws IOException thrown if a problem occurs
                    **/
                 try {
                        System.out.println(" calling executable : "+ execPathNames[i].toString());
                        long t1 ;
                        long t2, time ;
                        t1 = System.currentTimeMillis();
                        //System.out.println ("time before execution :  " + t1);

                        System.runFinalization();
                        //Thread.sleep((long)10000);
                        ExecRunner er = new ExecRunner();
                        //er.setMaxRunTimeSecs(0);
                        er.exec(execPathNames[i].toString());
                        //System.out.println( " EXECUTABLE OVER ");
                        //Thread.sleep((long)10000);
                        System.runFinalization();

                        t2 = System.currentTimeMillis();
                        time = t2-t1;
                        //System.out.println ("time after execution :  " + t2);
                        //System.out.println ("runtime for execution :  " + time);

                       // System.out.println("<STDOUT>\n" + er.getOutString() + "</STDOUT>");
                       // System.out.println("<STDERR>\n" + er.getErrString() + "</STDERR>");

                       // Exit nicely
                       // System.exit(0);

                  }
                  catch (Exception e) {

                       e.printStackTrace();
                       System.exit(1);

                  }


           } // i loop for number of fitnessexecs

	}// evaluateIndividual

       /*
        * This writes the genes of individuals to input files for different
        * fitness function executables, that might be present in different directories
        */
       // public void writeGeneToFile (double [] indiv, String[] execInputFileNames) throws IOException {
        public void writeGeneToFile (String[] execInputFileNames) throws IOException {

            // total number of Outputfiles
            int totalNumOutFiles = 0;
            for (int i = 0; i<  numOutputFiles.length ; i++) {
              totalNumOutFiles = totalNumOutFiles + numOutputFiles[i] ;
            }
            // preparing iinterpolation filenames table for output.
            interpFilenamesTable = new MutableTableImpl(totalNumOutFiles);

            // creating temporary string array to store grid output filenames for all output files.
            String [][] interpFiles = new String [archiveTable.getNumRows()][totalNumOutFiles];

            // create appropriate filenames for the output files that store the interpolated grid
            // first, loop over all the fitness executables

             // read chromosome ID from table
             for (int i = 0; i < archiveTable.getNumRows(); i++){
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
                  String[] colDat = new String [archiveTable.getNumRows()];
                  for (int j=0; j< archiveTable.getNumRows(); j++){
                      colDat[j] = interpFiles[j][i];
                  }
                  interpFilenamesTable.setColumn(new StringColumn(colDat), i);
              }

              // prepare input files (genes files) for each executable
              for (int ii = 0 ; ii < getNumFitnessExecs(); ii++) {
                try {

                   if (execInputFileNames[ii] != null){
                        FileWriter stringFileWriter = new FileWriter(execInputFileNames[ii]);
                        BufferedWriter bw = new BufferedWriter(stringFileWriter);
                        PrintWriter pw = new PrintWriter(bw);

                        // write population size in file
                        pw.println(archiveTable.getNumRows());
                        // write length of each gene/chromosome
                        pw.println(archiveTable.getNumColumns()-3);
                        // write number of outputfiles that are produced by the executable
                        pw.println(interpFilenamesTable.getNumColumns());

                        //System.runFinalization();
                        //System.out.println("popul size in "+ execInputFileNames[i]+"is "+ popul.size ());


                        // running the interpolation models for every chromosome
                        for (int i = 0; i< archiveTable.getNumRows(); i++) {

                            // write output filenames for every individual. These are the names of files
                            // into which the external executable will write the interpolated grid.
                            for (int p = 0; p < interpFilenamesTable.getNumColumns(); p++){
                                pw.println (interpFilenamesTable.getString(i,p));
                            }

                            // read chromosome from table
                            double [] gene = new double [archiveTable.getNumColumns() - 3];
                            for (int j=0; j<archiveTable.getNumColumns()-3; j++){
                              gene[j] = archiveTable.getDouble(i,j);
                            }

                            // write genes
                            for (int k =0; k < gene.length; k++){
                            if((k == 0)||(k==1)||(k==6)||(k==9)){
                                 pw.print(Math.abs(Math.round(gene[k])) );
                             }else{
                              pw.print(gene[k]);
                             }
                              pw.println();
                            }
                            pw.println();
                            pw.println();
                        }
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

	public void callAveragingExec () {//(NsgaPopulation popul) { //


            // call .m file for execution
          try {
            ExecRunner er = new ExecRunner();
            er.setMaxRunTimeSecs(0);
            String nameOfMatlabScript = matlabExecName;
            Integer noFiles = new Integer(archiveTable.getNumRows()) ;
            Integer max = new Integer(maxLevel);

            System.out.println(" calling executable : "+ nameOfMatlabScript);
            if(!(nameOfMatlabScript.matches("random"))){
            er.exec("matlab -nosplash -nodesktop -minimize -r " + nameOfMatlabScript +"(" +noFiles.toString()+","+ max.toString()+")");
            }
            //er.exec("matlab -nosplash -nodesktop -minimize -r " + nameOfMatlabScript +"('" +noFiles.toString()+"',"+ max.toString()+"',"+ select.toString () + ")");
          }
          catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
          }


          

                    // ***************** call execrunner
                    /**
                     * @param args an array of command-line arguments
                     * @throws IOException thrown if a problem occurs
                    **/
                 


          

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

