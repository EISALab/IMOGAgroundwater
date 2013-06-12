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

public class ImagePCATransformExec extends ncsa.d2k.core.modules.ComputeModule{
        int numExecFitness;
        IGANsgaPopulation pop;
        
        // Table 2
        MutableTableImpl fitnessExecs ;
        // Table 1
        ExampleTable archiveTable ;
        ExampleTable testTable;
        
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
        String matlabExecName = new String("myImgPCA");
        String matlabOutputName1 = new String("archive_statistics.out");
        String matlabOutputName2 = new String("predict_statistics.out");
        
        
        public String getExecName(){
            return matlabExecName;
        }
        
        public void setExecName(String exec){
            matlabExecName = exec;
        }

        public String getFileName1(){
            return matlabOutputName1;
        }
        
        public void setFileName1(String file){
            matlabOutputName1 = file;
        }

        public String getFileName2(){
            return matlabOutputName2;
        }
        
        public void setFileName2(String file){
            matlabOutputName2 = file;
        }
        
        
        
        
	public String getModuleInfo () {
		return "<html>  <head>      </head>  <body>    Run matlab PCA Executable.  </body></html>";
	}

        /**
		This method returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "NSGA Population";
                        case 1: return "Table with Training Inds";
			case 2: return "Table with Testing Inds";
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
			case 1: return "Training Data Table.";
			case 2: return "Testing Data Table.";                        
                        case 3: return "string containing name of Training Output file";
                        case 4: return "string containing name of Testing Output file";                        
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
                                  "ncsa.d2k.modules.core.datatype.table.basic.TableImpl",
                                  "java.lang.String",
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
		return "Run PCA Matlab exec - Post Interpolation";
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
                                return "Training Indivs Table";
                        case 2:
				return "Testing Indivs Table";
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
				return "Training Table";
			case 2:
				return "Testing Table";
                        case 3:
                                return "Training Output file name";
                        case 4:
                                return "Testing Output file name";

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
           testTable = null;
           
           while(archiveTable == null){
           archiveTable = (ExampleTable) this.pullInput(1);
           }
           // fitnessExecs = new MutableTableImpl();
           // fitnessExecs = (MutableTableImpl) this.pullInput(1);
            while(testTable == null) {
                testTable = (ExampleTable) this.pullInput(2);
            }
            
            this.callAveragingExec();

            
            this.pushOutput (pop, 0);
            this.pushOutput (archiveTable, 1);
            this.pushOutput (testTable, 2);            
            this.pushOutput(matlabOutputName1, 3);
            this.pushOutput(matlabOutputName2, 4);            
	}

	public void callAveragingExec () {//(NsgaPopulation popul) { //


            // call .m file for execution
          try {
            ExecRunner er = new ExecRunner();
            er.setMaxRunTimeSecs(0);
            String nameOfMatlabScript = matlabExecName;
            Integer noFiles1 = new Integer(archiveTable.getNumRows()) ;
            Integer noFiles2 = new Integer(testTable.getNumRows()) ;


            System.out.println(" calling executable : "+ nameOfMatlabScript);
            if(!(nameOfMatlabScript.matches("random"))){
            er.exec("matlab -nosplash -nodesktop -minimize -r " + nameOfMatlabScript +"(" +noFiles1.toString()+","+ noFiles2.toString()+")");
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

