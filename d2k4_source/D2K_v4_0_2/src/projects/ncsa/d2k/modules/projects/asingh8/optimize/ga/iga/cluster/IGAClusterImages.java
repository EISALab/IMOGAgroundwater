package ncsa.d2k.modules.projects.asingh8.optimize.ga.iga.cluster;

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
public class IGAClusterImages extends ncsa.d2k.core.modules.ComputeModule{
        int numExecFitness;
        MutableTableImpl fitnessExecs = new MutableTableImpl();// new TableImpl();
        int[] execFitnessIds ;
        int[] numOutputFiles;
        String[] execPathNames ;
        String[] execInputFileNames ;
        String[][] execOutputFileNames ;

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
			case 1: return "A TableImpl data structure that contains fitness ids for fitnesses evaluated using the executables and the entire paths names for executables with runtime arguments.";
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
			case 0: return "<p>      The population contains all individuals for the multiobjective problem with the updated fitness.    </p>";
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
		return "IGA: EvaluateExecutableFitness_NumericInd";
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
				return "Fitness executable table";
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
            execFitnessIds = null;
            numOutputFiles = null;
            execPathNames = null;
            execInputFileNames = null;
            execOutputFileNames = null;
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

            if(fitnessExecs == null) {
                fitnessExecs = (MutableTableImpl) this.pullInput(1);

            }
           // fitnessExecs = new MutableTableImpl();
           // fitnessExecs = (MutableTableImpl) this.pullInput(1);

            // **************
            execFitnessIds = this.getFitnessIds();

            execPathNames = new String[getNumFitnessExecs()];
            execPathNames = this.getFitnessNames();

            execInputFileNames = new String[getNumFitnessExecs()];
            execInputFileNames = this.getInputFileNames();

            execOutputFileNames = new String[getNumFitnessExecs()][10];
            execOutputFileNames = this.getOutputFileNames();

            numOutputFiles= new int[getNumFitnessExecs()];
            numOutputFiles= this.getNumOutputFiles();
            // writing individual genes to all input files
            // and prepare the input files before execution
            long t1 ;
            long t2 , time;
            t1 = System.currentTimeMillis();
            this.writeGenesToFile(pop,execInputFileNames);
            t2 = System.currentTimeMillis();
            time = t2-t1;
            System.out.println ("runtime for genes IO :  " + time);
            // evaluating fitness of individuals
            //Individual ind = pop.getMember(i);
            //if (ind.isDirty()){
            System.out.println("***************************************************");
            System.out.println("CurrentGeneration in evaluateexecutablefitness: " + pop.getCurrentGeneration());
            //System.out.println("execFitnessIds1 is " + execFitnessIds[0]);
            //System.out.println("execFitnessIds1 is " + execFitnessIds[0]);
               this.evaluateIndividual(pop);

            //}
               //for(int i = 0; i < pop.size();i++){
               //System.out.println(i+"Ranked Flag"+((IGANsgaSolution)pop.getMember(i)).getRankedIndivFlag());
               //}
            this.pushOutput (pop, 0);
            //this.pushOutput (fitnessExecs, 1);
	}

	public void evaluateIndividual (Population popul) {//(NsgaPopulation popul) { //

            //MONumericIndividual[] ni = new MONumericIndividual[popul.size()] ;
              MONumericIndividual[] ni = new MONumericIndividual[popul.size()] ;

            String outputFileName = new String();

            for (int i = 0; i < popul.size(); i++ ){
		ni[i] = (MONumericIndividual) popul.getMember(i);
                //ni[i] = (MOBinaryIndividual) popul.getMember(i);

                }

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

                 double[] fitness = new double[popul.size()];


                 try {
                        System.out.println(" calling executable : "+ execPathNames[i]);
                        long t1 ;
                        long t2, time ;
                        t1 = System.currentTimeMillis();

                        ExecRunner er = new ExecRunner();
                        er.setMaxRunTimeSecs(0);
                        er.exec(execPathNames[i].toString());

                        t2 = System.currentTimeMillis();
                        time = t2-t1;
                        System.out.println ("runtime for execution :  " + time);

                       // System.out.println("<STDOUT>\n" + er.getOutString() + "</STDOUT>");
                       // System.out.println("<STDERR>\n" + er.getErrString() + "</STDERR>");

                       // Exit nicely
                       // System.exit(0);

                       // Obtain fitnesses after evaluation is over
                       // reading fitness of all individuals from all output files
                       t1 = System.currentTimeMillis();

                       for (int k=0; k< numOutputFiles[i]; k++){
                           outputFileName = execOutputFileNames[i][k] ;
                          // System.out.println("name of fitness file : " + outputFileName);
                          // System.out.println(" **************** ");
                           fitness = this.readFitnessFromFile(popul,outputFileName);
                           for (int j = 0; j < popul.size(); j++ ){
                                ni[j].setObjective (fitnessID , fitness[j]);
                                if(fitness[j]==0){
                                    System.out.print("fitness "+j+"is "+fitness[j]);
                                }
                            //  System.out.println("Fitness" + fitnessID + " in individual : " + j + "is " + fitness[j] );
                            }

                         //  System.out.println("Fitness in module : " + fitnessID );
                         //  System.out.println("Fitness 1 in individual : " + fitness[0] );
                         //  System.out.println("Fitness 2 in individual : " + fitness[1] );
                           fitnessID = fitnessID + 1 ;
                       }
                        t2 = System.currentTimeMillis();
                        time = t2-t1;
                        System.out.println ("runtime for fitness IO :  " + time);


                  }
                  catch (Exception e) {

                       e.printStackTrace();
                       System.exit(1);

                  }


           } // i loop for number of fitnesses

	}// evaluateIndividual

       /*
        * This writes the genes of individuals to input files for different
        * fitness function executables, that might be present in different directories
        */
        /**
         * @param popul
         * @param execInputFileNames
         * @throws IOException
         */        
        public void writeGenesToFile (Population popul, String[] execInputFileNames) throws IOException {
        //(NsgaPopulation popul, String[] execInputFileNames) throws IOException {

          //  String genes = new String() ;

            for (int i = 0 ; i < getNumFitnessExecs(); i++) {
                try {
                   if (execInputFileNames[i] != null){
                       FileWriter stringFileWriter = new FileWriter(execInputFileNames[i]);
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
                             if((k == 0)||(k==1)||(k==6)||(k==9)){
                                 pw.print(Math.abs(Math.round(genes[k])) );
                             }else{
                                 pw.print(genes[k] );
                             }
                             pw.println();
                             }
                             pw.println();
                             pw.println();
                             //System.out.println("indiv id "+ j);
                          // }

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
        * This read the fitness of individuals from output files for different
        * fitness function executables, that might be present in different directories
        */
        public double[] readFitnessFromFile (Population popul, String outFileName ) throws IOException {
//(NsgaPopulation popul, String outFileName ) throws IOException {
                double[] fit = new double[popul.size()] ;
                String[] sline = new String[2*popul.size()];
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
                       sline[i] = br.readLine();
                       
                       while ( sline[i] != null ){
                      //   System.out.println("sline " + i + " : " + sline[i]);
                         Double db = new Double(sline[i]);
                         //fit[i] = db.doubleValue();
                         fit[i] = db.parseDouble(sline[i]);
                         i= i + 1;
                         sline[i] = br.readLine();
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
               return fit;
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


}

