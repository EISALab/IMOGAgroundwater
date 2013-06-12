package ncsa.d2k.modules.core.transform.table;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import java.util.Random;
import java.beans.PropertyVetoException;

/*
	NFoldTrainTest (previously NFoldTTest then NFoldCrossValidation)

	Takes in a single table, makes N ExampleTables that have different,
	exhaustive subsets set to the trainSet and testSet, but pushes out
	a TrainTable and TestTable made from the ExampleTable

	@author: Peter Groves w/ much cut and paste from Tom Redman's code;
*/

public class NFoldTrainTest extends ncsa.d2k.core.modules.DataPrepModule {

	/** default property values **/
	int nfolds = 10;
	long seed = (long)0.00;
	boolean debug=false;

	/** the number of folds when we started execution, just in case user
         *  properties mid-run */
	int Nfolds;

	/** number of times we have fired for current input data **/
	int numFires = 0;

	/** the data table. */
	Table table = null;

	/** the break points. */
	int [] breaks = null;

	/** This is an array of all the indices iso we can do an arraycopy. */
	int [] indices = null;

	/** The number of rows in the data table */
	int numRows;

        //////////////
        //   Module, Input, Output, and Property methods that provide
	//   required information and set properties..
        /////////////

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "N-Fold Train Test";
	}

	/**
	 * Return a description of the module functionality
	 * @return a description of the module functionality
	 */
	public String getModuleInfo(){
	  StringBuffer sb = new StringBuffer( "<p>Overview: ");
	  sb.append( "This module produces tables containing train and test examples that can be used to perform N-fold  ");
	  sb.append( "cross validation and estimate model accuracy. ");

          sb.append( "</p><p>Detailed Description: ");
	  sb.append( "For each table input, this module randomly divides the examples in the table into N mutually exclusive ");
	  sb.append( "subsets, referred to as <i>folds</i>, and builds train and test example tables from those folds. ");
	  sb.append( "Each of the folds contains approximately the same number of examples, ");
	  sb.append( "and the user can control the number of folds via the properties editor. ");
          sb.append( "The number of folds must be greater than 2, and the input table must contain at least N examples ");
	  sb.append( "or an exception will occur. ");

	  sb.append( "</p><p> " );
          sb.append( "The examples in N-1 of the folds are gathered into a <i>Train Table</i> ");
 	  sb.append( "and the examples in the remaining fold form a <i>Test Table</i>. ");
	  sb.append( "The module executes N times for each input table received, producing a new <i>Train Table</i> and ");
	  sb.append( "<i>Test Table</i> each time it executes.   A different fold of the examples is \"held out\" ");
	  sb.append( "for testing each time. ");

	  sb.append( "The number of folds is written to the <i>Number of Folds</i> output port ");
	  sb.append( "the first time the module executes after a new input table is received. ");

	  sb.append( "</p><p>Data Type Restrictions: " );
	  sb.append( "This module has no explicit data type restrictions, however the majority of the predictive model " );
	  sb.append( "builders work on continuous values, so data conversion may be required.  If so, it should be done " );
	  sb.append( "prior to splitting the examples into train and test sets in this module.  ");
          sb.append( "Otherwise, the conversion would ");
	  sb.append( "have to be performed over and over on the examples in the N train and test sets. ");

	  sb.append( "</p><p>Data Handling: " );
	  sb.append( "This module does not modify the input table, but may reference the values in the table in the Train ");
	  sb.append( "and Test tables that are generated.  (The exact behavior depends on the type of table that is input.) ");
	  sb.append( "Because of this, it is recommended that the user not modify the original table in other modules in the ");
	  sb.append( "itinerary that will execute after this module, as doing so may impact the validity of the Train and Test ");
	  sb.append( "table contents. ");

	  sb.append( "</p><p>Scalability: ");
	  sb.append( "The memory requirements of the original table will likely dwarf the memory requirements of " );
          sb.append( "this module, ");
	  sb.append( "which requires an array of integers with one entry for each row of the original table. ");
	  sb.append( "Additional memory may be required depending on the underlying implementation of the original table. ");

	  sb.append( "</p><p>Trigger Criteria: ");
	  sb.append( "When this module receives an input, it will execute <i>Number of Folds</i> times, where <i>Number ");
	  sb.append( "of Folds</i> is the property controlled by the user. ");


          return sb.toString();
	}


	/**
	 * get the input information for each input
	 * @param i the index of the input
	 * @return the input information
	 */
	public String getInputInfo(int i){
		switch (i) {
			case 0: return "The table from which the train and test tables will be extracted.";
			default: return "No such input";
		}
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Table";
			default:
				return "No such input";
		}
	}

	/**
         * Return the types of the input ports
	 * @return the types of the input ports
         */
	public String[] getInputTypes(){
		String[] types = {"ncsa.d2k.modules.core.datatype.table.Table"};
		return types;
	}


	/**
	 * get the output information for each output
	 * @param i the index of the output
	 * @return the output information
	 */
	public String getOutputInfo(int i){
		switch (i) {
		   	case 0:
				return "A table containing the training data.";
			case 1:
				return "A table containing the testing data.";
			case 2:
				return "The number of folds specified via the property editor.";
			default:
				return "No such output";
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
				return "Train Table";
			case 1:
				return "Test Table";
			case 2:
				return "Number of Folds";
			default:
				return "No such output";
		}
	}

	/**
         * Return the types of the output ports
	 * @return the types of the output ports
         */
	public String[] getOutputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.table.TrainTable",
			"ncsa.d2k.modules.core.datatype.table.TestTable",
			"java.lang.Integer"};
		return types;
	}

	/**
	 * Return a list of the property descriptions.
	 * @return a list of the property descriptions.
	 */
	public PropertyDescription [] getPropertiesDescriptions () {
		PropertyDescription [] pds = new PropertyDescription [3];
		pds[0] = new PropertyDescription (
				"numberFolds",
				"Number of Folds",
				"The number of folds for the cross validation. This number must be greater than 2.");
		pds[1] = new PropertyDescription (
				"seed",
				"Seed",
				"The seed for the random number generator used to shuffle the examples before folds are formed. \n" +
				"If the same seed is used across runs with the same input table, you will get the same train and test sets.");
		pds[2] = new PropertyDescription (
				"debug",
				"Verbose output",
				"If this flag is set, the indices of the train and test sets are output to the console as the module runs.");
		return pds;
	}

	////////////////////////////////
	//D2K Property get/set methods
	///////////////////////////////
	/**
	 * returns the number of folds.
	 * @returns the number of folds.
	 */
	public int getNumberFolds(){
		return nfolds;
	}
	public void setNumberFolds(int n) throws PropertyVetoException {
		if (n < 3) {
			throw new PropertyVetoException (" There must be at least 3 folds", null);
		}
		nfolds = n;
	}

	/**
	 * set the random seed
	 * @returns the number of folds.
	 */
	public long getSeed(){
		return seed;
	}
	public void setSeed(long d){
		seed=d;
	}

	/**
         * get the debug flag value
         * @returns the debug flag value
         */
	public boolean getDebug(){
		return debug;
	}
	public void setDebug(boolean b){
		debug=b;
	}

	// Here's the "meat" of the module implementation

        /**
                Reset variables when we begin execution.
        */
        public void beginExecution () {
                numFires = 0;
                breaks = null;
                table = null;
                indices = null;
        }

        /**
                Fires N times, where N is the number of folds.
        */
        public boolean isReady () {
                if ( numFires==0 ) {
                        return super.isReady();
                } else {
                        return true;
                }
        }

	/**
		Does things, especially 'it'
	*/
	public void doit () throws Exception {

		// If this is the first time with new data,
		// load the input, then call setup to
		// initialize variables we'll use across all folds
                // Finally, push the Number of Folds to output port
		//
		if ( numFires == 0 ) {
			table = (Table) this.pullInput( 0 );
			setup() ;
			this.pushOutput ( new Integer(Nfolds), 2);
		}

		// Generate the train/test for the current fold, which
		// is determined based on the value of numFires.
		//
		int testSize = breaks[numFires+1] - breaks[numFires];
		int trainSize = numRows - testSize;

		// Set up the train and test sets indices;
		//
		int testing [] = new int [testSize];
		int training [] = new int [trainSize];

		// Copy appropriate entries from the randomized indices array
		// into test set
		//
		System.arraycopy( indices, breaks[numFires], testing, 0, testSize );

		// Copy remaining entries from the randomized indices array
		// into train set.   This is done in 2 steps, first copying
		// indicies before the test entries, then copying indicies after
		// the test entries.  Note that for first and last folds one
	 	// of these two copies will have 0 length and be a noop.
		//
	 	System.arraycopy( indices, 0, training, 0, breaks[numFires] );
		System.arraycopy( indices, breaks[numFires+1],
				  training, breaks[numFires],
				  numRows-breaks[numFires+1] );

	        if (debug) {
                    System.out.println( getAlias() + ": Fold " + numFires );
                    System.out.println( getAlias() + ": Test Set size:  "
                                          + testSize + " and indicies: " );
                    for ( int j = 0; j < testSize; j++ ) {
                        System.out.println( testing[j] );
                    }

                    System.out.println( getAlias() + ": Train Set size:  "
                                          + trainSize + " and indicies: " );
                    for ( int j = 0; j < trainSize; j++ ) {
                        System.out.println( training[j] );
                    }
                }

		// now create a new vertical table.
		ExampleTable examples = table.toExampleTable();
		examples.setTrainingSet (training);
		examples.setTestingSet (testing);

		Table testT= examples.getTestTable();
		Table trainT= examples.getTrainTable();

		// push train and test sets to output ports
		this.pushOutput (trainT, 0);
		this.pushOutput (testT, 1);

		numFires++;

		// are we done with all the train/test sets for this input?
		// if so, clean up.
		//
		if ( numFires == Nfolds ) {
		    numFires = 0;
		    breaks = null;
		    indices = null;
		}
	}

	/**
		Setup the indexint array and shuffle it randomly
	*/
	protected void setup () throws Exception {

		// Save the number of folds currently set, in case user changes properties mid-stream
		Nfolds = nfolds;

		numRows = table.getNumRows ();

		if (numRows < Nfolds) {
		    throw new Exception( this.getAlias() +
			": The input table must not contain fewer rows than the number of folds requested. \n" +
			"The input table has only " + numRows + " rows and " + Nfolds + " folds were requested. " );
  		}

		// setup the indicies that break the table into N folds; handles rounding
		breaks = this.getTableBreaks (numRows);

		// here are the indices into the table that we'll shuffle
		indices = new int [numRows];
		for (int i = 0 ; i < numRows ; i++) {
		    indices [i] = i;
		}

		Random rand=new Random(seed);

		// Let's shuffle them
		for (int i = 0 ; i < numRows ; i++) {
		    int swap = (int) (rand.nextDouble () * numRows);
		    if (swap != 0)
			swap--;
		    int old = indices[swap];
		    indices [swap] = indices [i];
		    indices [i] = old;
		}

	        if (debug) {
	            System.out.println( getAlias() + ": Indicies after randomization: " );
                    for ( int j = 0; j < numRows; j++ ) {
		        System.out.println( indices[j] );
                    }
                }
	}


	/**
		Returns a list of indices to the first element of Nfold + 1 sets.
		The index of the first set is always 0;
		The index of the last set (set N+1) is always one past the end of the examples.
		We do N+1 rather than N or N-1 because then we don't have to special case
		processing of first and last sets elsewhere in the code.
		@param nExamples The number of examples.
	*/
	int [] getTableBreaks (int nExamples) {
		int [] tableBreaks;
		double numExamples = (double)nExamples;
		double n = (double)Nfolds;

		tableBreaks = new int[Nfolds+1];
		tableBreaks[0] = 0;
		for(int i = 1; i < Nfolds; i++){
			tableBreaks[i] = (int) (((double)i/n)*numExamples);
		}
		tableBreaks[Nfolds] = nExamples;

		return tableBreaks;
	}
}
// Start QA Comments
// 2/24/03 - Received by QA from Loretta & Tom
// 3/5/03  - Ruth starts QA;  Reordered Properties so what user will likely change is first;
//         - Reported ArrayOutOfBounds err to developers.
// 3/25/03 - Ruth back on QA.
//   -Tom fixed ArrayOutOfBounds problem.
//   -Made output order train then test and made sure all port names & docs matched that order.
//   -Increased size of breaks array so no need to special-case first and last folds.  Also
//    merged some things into doit so fewer class vars needed.
//   -Require number of rows to be at least N, (not 2*N), and removed checks elsewhere that
//    made sure it wasn't less.  (since no longer possible)
//   -updated descriptions
// 3/26/03 - Ready for Basic
// End QA Comments
