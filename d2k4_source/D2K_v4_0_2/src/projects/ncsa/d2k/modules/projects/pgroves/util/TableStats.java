package ncsa.d2k.modules.projects.pgroves.util;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
/**************************************************
**	
**
**	For every NumericColumn of in a
**	Vertical Table, computes the standard deviation, mean, min, max,
**  skewness, kurtosis
**
**  @author Peter Groves
**************************************************/

public class TableStats extends ncsa.d2k.core.modules.ComputeModule {


	/**
		This method returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "The Table to compute stats for.";
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
			"ncsa.d2k.modules.core.datatype.table.basic.TableImpl"};
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
		This method returns an array of strings that contains the data types for the outputs.
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
		"<li>Minimum</li>" +
		"<li>Maximum</li>" +
		"<li>Mean</li>" +
		"<li>Standard Deviation</li>" +
		"<li>Skewness</li>" +
		"<li>Kurtosis</li></ol>";
	}

  ////////////////////
  //THE ALMIGHTY DOIT
  ///////////////////
	public void doit () throws Exception {

	Table data=(Table)pullInput(0);

	String[] columnNames=new String[data.getNumColumns()];
	double[] deviations=new double[data.getNumColumns()];
	double[] means=new double[data.getNumColumns()];
	double[] minimums=new double[data.getNumColumns()];
	double[] maximums=new double[data.getNumColumns()];
	double[] skewnesses=new double[data.getNumColumns()];
	double[] kurtosises=new double[data.getNumColumns()];

	//really, this counts ALL numeric columns
	int numDoubleCols=0;

	for(int k=0; k<data.getNumColumns(); k++){
		Column col=data.getColumn(k);

	    if (col.getIsScalar()){
			NumericColumn currentColumn=(NumericColumn)col;

			numDoubleCols++;
	      double mean = 0.0;


			double min=currentColumn.getMin();
			double max=currentColumn.getMax();
			int rows=currentColumn.getNumRows();

			//calculate the average
			for(int i=0; i<rows; i++){
				mean+=currentColumn.getDouble(i);
			}
			
			mean = mean/rows;

			//find differences from mean and variance
		  	//the following are the sum of the differences squared, cubed, ^4
			//and the diff each time for moment calculations
	      double sum_diff2=0.0;
	      double sum_diff3=0.0;
		  	double sum_diff4=0.0;
			
		  	double diff=0.0;
		  	double diffTally=0.0;

			for(int i=0; i<rows; i++){
		  		diff=currentColumn.getDouble(i)-mean;
		  		diffTally=diff;

		  		diffTally=diffTally*diff;
		  		sum_diff2+=diffTally;

		  		diffTally=diffTally*diff;
		  		sum_diff3+=diffTally;

		  		diffTally=diffTally*diff;
		  		sum_diff4+=diffTally;
	      	}
			//compute the stdev, skewness, and kurtosis
	      int n = rows;
			
	      double deviation = Math.pow(sum_diff2 / (n - 1), .5);
			
		  	double skewness = sum_diff3 / ((n - 1) * deviation * 
				deviation * deviation);
			
		  	double kurtosis = sum_diff4 / ((n - 1) * deviation * deviation * 
				deviation * deviation);
			
  	   		//System.out.println("VTStats: Column no " +k+ " Label: "+currentColumn.getLabel()+", st dev = " +deviation + ", mean = "+mean+", min:"+min+", max:"+max+", skewness"+skewness+", kurtosis"+kurtosis);


			deviations[numDoubleCols-1]=deviation;
			means[numDoubleCols-1]=mean;
			minimums[numDoubleCols-1]=min;
			maximums[numDoubleCols-1]=max;
			columnNames[numDoubleCols-1]=currentColumn.getLabel();
			skewnesses[numDoubleCols-1]=skewness;
			kurtosises[numDoubleCols-1]=kurtosis;
		}
	  }

		//must 'remove' empty spaces at the ends of the arrays
		//due to columns that weren't numeric columns

		String[] labels=new String[numDoubleCols];
		double[] devs=new double[numDoubleCols];
		double[] mns=new double[numDoubleCols];
		double[] mins=new double[numDoubleCols];
		double[] maxs=new double[numDoubleCols];
		double[] skews=new double[numDoubleCols];
		double[] kurts=new double[numDoubleCols];



		for(int i=0; i<numDoubleCols; i++){
			labels[i]=columnNames[i];
			devs[i]=deviations[i];
			mns[i]=means[i];
			mins[i]=minimums[i];
			maxs[i]=maximums[i];
			kurts[i]=kurtosises[i];
			skews[i]=skewnesses[i];
		}

		//create the Table for export
		MutableTableImpl stats = new MutableTableImpl(7);
		
		stats.setLabel("Stats For" + data.getLabel());

		StringColumn col1=new StringColumn(labels);
		col1.setLabel("OriginalColumnName");
		stats.setColumn(col1, 0);

		DoubleColumn col2=new DoubleColumn(devs);
		col2.setLabel("Standard Deviation");
		stats.setColumn(col2, 4);

		DoubleColumn col3=new DoubleColumn(mins);
		col3.setLabel("Minimum");
		stats.setColumn(col3, 1);

		DoubleColumn col4=new DoubleColumn(maxs);
		col4.setLabel("Maximum");
		stats.setColumn(col4, 2);

		DoubleColumn col5=new DoubleColumn(mns);
		col5.setLabel("Mean");
		stats.setColumn(col5, 3);

		DoubleColumn col6=new DoubleColumn(skews);
		col6.setLabel("Skewness");
		stats.setColumn(col6, 5);

		DoubleColumn col7=new DoubleColumn(kurts);
		col7.setLabel("Kurtosis");
		stats.setColumn(col7, 6);

		//System.out.println("VTStats done");
		pushOutput(stats, 0);
	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "TableStats";
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
}





