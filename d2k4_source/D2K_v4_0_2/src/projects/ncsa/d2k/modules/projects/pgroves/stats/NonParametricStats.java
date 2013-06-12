package ncsa.d2k.modules.projects.pgroves.stats;

import java.util.ArrayList;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.table.util.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;


/**
	Calculates nonparametric statistics (quantiles) for every 
	scalar column in a table. the number of quantiles can be 
	manipulated. 

	@author pgroves
	@date 03/16/04
	*/

public class NonParametricStats extends ComputeModule 
	implements java.io.Serializable {

	//////////////////////
	//d2k Props
	////////////////////
	
	boolean debug=false;		
	/////////////////////////
	/// other fields
	////////////////////////

	//////////////////////////
	///d2k control methods
	///////////////////////

	public boolean isReady(){
		return super.isReady();
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
	}
	
	/////////////////////
	//work methods
	////////////////////
	/*
		does it
	*/
	public void doit() throws Exception{

		Table data = (Table)pullInput(0);
		ParameterPoint pp = (ParameterPoint)pullInput(1);


		int numQuants = (int)pp.getValue(0);

		if(debug){
			System.out.println(getAlias()+": Num Quantiles:" + numQuants);
		}

		MutableTableImpl stats = this.findQuantiles(data, numQuants);

		pushOutput(stats, 0);
	}
	
	/**
		Finds the quantiles of every scalar column in the input
		data table. the number of equally wide quantiles to find
		is passed in. (quantiles are non parametric statistics, such
		as the min, max, and median, and can loosely be described
		as being based on rankings of the data instead of magnitudes
		of data). The number of quantiles represents how many equivalently
		large sets to split the data into. The min and max are always included
		and are not considered in the numQuantiles count. Therefore, 
		running this method with <code>numQuantiles = 0</code> 
		will return the max and min of each column.  
		<code>numQuantiles = 1</code> will return min, median, and max,
		<code>numQuantiles = 2</code> will return min, 33% percent
		quantile, 66% quantile, and max. (the 33% quantile is the value,
		present in the data, that 33% of the data are less than or
		equal to.

		@param data a table with numeric columns
		@param numQuantiles the number of quantiles to find

		@return A table with <code>numQuantiles + 3</code> columns. 
			The first column contains the labels of the numeric/scalar
			columns from the input data table. Then comes columns for
			the min, the quantile values, and the max.
		*/	
		
	public MutableTableImpl findQuantiles(Table data, int numQuantiles){
		int i, j;
		
		//holds the names of columns that end up being scalar (which will
		//be the number of rows in the returned table
		ArrayList scalarColumnLabels = new ArrayList();

		//holds the double arrays that are the sets of quantiles. will
		//end up being the rows in the table
		ArrayList quantSets = new ArrayList();

		int numCols = data.getNumColumns();
		int numRows = data.getNumRows();
		/*
		if(numRows == 0){
			return new double[0];
		}else if(numRows == 1){
			double[] singleQuant = new double[1];
			singleQuant[0] = data.getDouble(0, columnIdx);
			return singleQuant;
		}else */
		if(numRows < numQuantiles){
			numQuantiles = numRows - 2;
			if(numQuantiles < 0){
				numQuantiles = 0;
			}
		}
		for(i = 0; i < numCols; i++){
			if(data.isColumnScalar(i)){
				
				scalarColumnLabels.add(data.getColumnLabel(i));

				quantSets.add(this.findQuantiles(data, i, numQuantiles));
			}
		}
		
		numRows = quantSets.size();
		
		numCols = numQuantiles + 3;
		MutableTableImpl quantTable = new MutableTableImpl(numCols);
		quantTable.setColumn(new StringColumn(numRows), 0);
		for(i = 1; i < numCols; i++){
			quantTable.setColumn(new DoubleColumn(numRows), i);
		}
		quantTable.setColumnLabel("OriginalColumnName", 0);
		quantTable.setColumnLabel("Min", 1);
		quantTable.setColumnLabel("Max", numCols - 1);
		int quantPercent;
		for(i = 2; i < numCols - 1; i++){
			quantPercent = (int) ((i - 1.0) * 100 / (numQuantiles + 1.0));
			quantTable.setColumnLabel(quantPercent + "% Quantile", i);
		}
			
		double[] currentQuants;
		String currentLabel;	
		for(i = 0; i < numRows; i++){
			currentQuants = (double[])quantSets.get(i);
			currentLabel = (String)scalarColumnLabels.get(i);
			quantTable.setString(currentLabel, i, 0);
			for(j = 1; j < numCols; j++){
				quantTable.setDouble(currentQuants[j - 1], i, j);
			}
		}

		return quantTable;
	}

	/**
		this description is currently outdated. The method is fine.
		the same description for the other findQuantiles method applies,
		with the following differences. 1) returns a single set of
		quantiles as an int array of indices into the data table (rows). 
		2)the ranking is based on a 
		set of columns. rows are therefore compared based on the collection
		of columns. for most applications, this will simply be an int[]
		with a single entry for a single column that is of interest
		*/ 
	public static double[] findQuantiles(Table data, int columnIdx, 
		int numQuantiles){

		int[] comparisonColumns = new int[1];
		comparisonColumns[0] = columnIdx;
		int[] sortedOrder = TableUtilities.multiSortIndex(data, 
			comparisonColumns);

		int numRows = sortedOrder.length;
		//System.out.println("sortedRows:" + numRows + " tableRows:" + 
		//	data.getNumRows() + "numQuants" + numQuantiles);
		if(numQuantiles == 0){
			return new double[2];
		}/*else if(numRows == 1){
			double[] singleQuant = new double[1];
			singleQuant[0] = data.getDouble(0, columnIdx);
			return singleQuant;
		}else if(numRows < numQuantiles){
			numQuantiles = numRows;
		}*/
		

		double[] quants = new double[numQuantiles + 2];
		//min
		quants[0] = data.getDouble(sortedOrder[0], columnIdx);
		//max
		quants[quants.length - 1] = data.getDouble(
			sortedOrder[numRows - 1], columnIdx);

		//the other quantiles
		
		double fracSet;
		int startIdx;
		double leftover;
		double quantLow;
		double quantHigh;
		for(int i = 0; i < numQuantiles; i++){
			fracSet = (((double)i) + 1.0d) / (((double)numQuantiles) + 1.0d);
			startIdx = (int)(fracSet * ((double)numRows));;
			leftover = (fracSet * ((double)numRows)) - ((double)startIdx);
			/*System.out.println("\tquant:"+(i+1)+" = " + quants[i+1]);
			System.out.println(
				" \tfracSet:"+fracSet+
				" startidx:"+startIdx+
				" leftover:"+leftover);
			*/
			quantLow = data.getDouble(sortedOrder[startIdx], columnIdx);
			try{
				quantHigh = data.getDouble(sortedOrder[startIdx + 1], columnIdx);
				quants[i + 1] = quantLow + leftover * (quantHigh - quantLow);
			}catch(Exception exc){
				quants[i + 1] = quantLow;
			}
			
			/*System.out.println( "\tquantLow:"+quantLow+
				" quantHigh:"+quantHigh);
			*/
		}
		return quants;
	}
		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return 	
			"Calculates the quantiles of the numeric columns of a Table."+
			"The quantiles plus the max and min are returned. Therefore, "+
			"if the number of quantiles requested is 1, you would actually"+
			"get the min, median, and max. If two are requested, it would"+
			"return the min, 33% quantile, 66% quantile, and max. If three"+
			" the quartiles (min, 25%, 50% = median, 75%, max). For deciles,"+
			" use 10.";
	}
	
   	public String getModuleName() {
		return "NonParametricStatistics";
	}
	public String[] getInputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.table.Table",
			"ncsa.d2k.modules.core.datatype.parameter.ParameterPoint"
			};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: 
				return "A table with scalar columns. Only columns that are"+
				" scalar will have statistics calculated.";
			case 1: 
				return "A parameter point that determines how many quantiles" +
				" will be calculated. So, the parameter point should contain " +
				" a single parameter. (It could have more, but only the first" +
				" will be considered.)";
			case 2: 
				return "";
			default: return "No such input";
		}
	}
	
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Data Table";
			case 1:
				return "Quantile Count Parameter";
			case 2:
				return "";
			default: return "No such input";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.table.basic.MutableTableImpl"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: 
				return "A table with three more columns than the number of" +
				" quantiles requested. The first column (StringColumn) contains"+
				" the column labels of the scalar columns from the input table."+
				" The rest of the columns (DoubleColumns) contain the min, "+
				" (1/(numQuantiles + 1)) quantile, (2/(numQ....), ...,"+
				" (numQuantiles/(numQuantiles-1)) quantile, max, in that order.";
			case 1:
				return "";
			case 2:
				return "";
			default: return "No such output";
		}
	}
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Statistics Table";
			case 1:
				return "";
			case 2:
				return "";
			default: return "No such output";
		}
	}		
	////////////////////////////////
	//D2K Property get/set methods
	///////////////////////////////
	public void setDebug(boolean b){
		debug=b;
	}
	public boolean getDebug(){
		return debug;
	}
	/*
	public boolean get(){
		return ;
	}
	public void set(boolean b){
		=b;
	}
	public double  get(){
		return ;
	}
	public void set(double d){
		=d;
	}
	public int get(){
		return ;
	}
	public void set(int i){
		=i;
	}

	public String get(){
		return ;
	}
	public void set(String s){
		=s;
	}
	*/
}
			
					

			

								
	
