package ncsa.d2k.modules.projects.pgroves.stats;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

/**************************************************
**	
**
**	For every NumericColumn of in a
**	Table, computes the standard deviation, mean, skewness, kurtosis
**
**  @author Peter Groves
**************************************************/

public class ParametricStats extends ncsa.d2k.core.modules.ComputeModule {

	boolean debug = false;


	/**
		This method returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "The Table to compute stats for. " +
			"Only scalar columns will have stats computed.";
			default: return "No such input";
		}
	}


	/**
		This method returns an array of strings that contains the 
		data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes () {
		String[] types = {
			"ncsa.d2k.modules.core.datatype.table.Table"};
		return types;
	}

	/**
		This method returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo (int index) {
		switch (index) {
			case 0: return "A Table with the stats.";
			default: return "No such output";
		}
	}

	/**
		This method returns an array of strings that contains the data
		 types for the outputs.
		 
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes () {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.basic.TableImpl"};
		return types;
	}

	/**
		This method returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo () {
		return "This module takes in a Table and computes basic stats for " +
		"every scalar column. The results are output in a new Table with one" +
		" row per original scalar column. Columns are in the following order:" +
		"<ol><li>NumericColumnsNames</li>" +
		"<li>Mean</li>" +
		"<li>Standard Deviation</li>" +
		"<li>Skewness</li>" +
		"<li>Kurtosis</li></ol>";
	}

  ////////////////////
  //THE ALMIGHTY DOIT
  ///////////////////
	public void doit () throws Exception {
		int i, j, k;
		
		if(debug){
			System.out.println(this.getAlias());
		}

		Table data = (Table)pullInput(0);

		int numCols = data.getNumColumns();
		int numRows = data.getNumRows();

		String[] columnNames = new String[numCols];
		double[] deviations = new double[numCols];
		double[] means = new double[numCols];
		double[] skewnesses = new double[numCols];
		double[] kurtosises = new double[numCols];

		//really, this counts ALL numeric columns
		int numDoubleCols = 0;

		for(k=0; k<numCols; k++){

			if (data.isColumnScalar(k)){
				numDoubleCols++;
				double mean = 0.0;

				//first, the average
				for(i = 0; i < numRows; i++){
					mean += data.getDouble(i, k);
				}
			
				mean = mean / numRows;

				//find differences from mean and variance
				//the following are the sum of the differences squared, cubed, ^4
				//and the diff each time for moment calculations
				double sum_diff2 = 0.0;
				double sum_diff3 = 0.0;
				double sum_diff4 = 0.0;

				double diff = 0.0;
				double diffTally = 0.0;

				for(i = 0; i < numRows; i++){
					diff = data.getDouble(i, k) - mean;
					diffTally = diff;

					diffTally = diffTally * diff;
					sum_diff2 += diffTally;

					diffTally = diffTally * diff;
					sum_diff3 += diffTally;

					diffTally = diffTally * diff;
					sum_diff4 += diffTally;
				}
				//compute the stdev, skewness, and kurtosis
				int n = numRows;
			
				double deviation = Math.pow(sum_diff2 / (n - 1), .5);
			
				double skewness = sum_diff3 / ((n - 1) * deviation * 
					deviation * deviation);
			
				double kurtosis = sum_diff4 / ((n - 1) * deviation * deviation * 
					deviation * deviation);
			
				if(debug){
					System.out.println("\t Column Idx:" + k + " Label: " + 
						data.getColumnLabel(k));
					System.out.println("\t\t Mean = " + mean);  
					System.out.println("\t\t St Dev = " + deviation);
					System.out.println("\t\t Skewness = " + skewness);
					System.out.println("\t\t kurtosis = "+kurtosis);
				}


				deviations[numDoubleCols - 1] = deviation;
				means[numDoubleCols - 1] = mean;
				columnNames[numDoubleCols - 1] = data.getColumnLabel(k);
				skewnesses[numDoubleCols - 1] = skewness;
				kurtosises[numDoubleCols - 1] = kurtosis;
			}//if scalar
		}//for all columns

		//must 'remove' empty spaces at the ends of the arrays
		//due to columns that weren't numeric columns

		String[] labels = new String[numDoubleCols];
		double[] devs = new double[numDoubleCols];
		double[] mns = new double[numDoubleCols];
		double[] skews = new double[numDoubleCols];
		double[] kurts = new double[numDoubleCols];



		for(i = 0; i < numDoubleCols; i++){
			labels[i] = columnNames[i];
			devs[i] = deviations[i];
			mns[i] = means[i];
			kurts[i]=kurtosises[i];
			skews[i]=skewnesses[i];
		}

		//create the Table for export
		MutableTableImpl stats = new MutableTableImpl(5);
		
		stats.setLabel("Stats For" + data.getLabel());

		StringColumn col1 = new StringColumn(labels);
		col1.setLabel("OriginalColumnName");
		stats.setColumn(col1, 0);
		
		DoubleColumn col2 = new DoubleColumn(mns);
		col2.setLabel("Mean");
		stats.setColumn(col2, 1);
		
		DoubleColumn col3 = new DoubleColumn(devs);
		col3.setLabel("Standard Deviation");
		stats.setColumn(col3, 2);

		DoubleColumn col4 = new DoubleColumn(skews);
		col4.setLabel("Skewness");
		stats.setColumn(col4, 3);

		DoubleColumn col5 = new DoubleColumn(kurts);
		col5.setLabel("Kurtosis");
		stats.setColumn(col5, 4);

		//System.out.println("VTStats done");
		pushOutput(stats, 0);
	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "ParametricStats";
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
				return "Stats Table";
			default: return "NO SUCH OUTPUT!";
		}
	}


	public void setDebug(boolean b){
		debug=b;
	}
	public boolean getDebug(){
		return debug;
	}

}





