package ncsa.d2k.modules.projects.pgroves.geostat;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.util.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;


/**
	Divides a raw semivariogram (represented by a table) into
	groups of points (based on h/distance values) that represent
	single points in an experimental variogram. How the partitioning
	is done is determined by an input parameter point. The input
	raw variogram is expected to already be sorted.


	@author pgroves
	@date 03/11/04
	*/

public class PartitionVariogram extends DataPrepModule 
	implements java.io.Serializable {

	//////////////////////
	//d2k Props
	////////////////////
	
	boolean debug=false;		
	/////////////////////////
	/// other fields
	////////////////////////
	public final int LAG_INDEX = 0;
	public final int GAMMA_INDEX = 1;

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
		if(debug){
			System.out.println(getAlias()+":Firing");
		}
		TableImpl rawVariogram = (TableImpl)pullInput(0);
		ParameterPoint params = (ParameterPoint)pullInput(1);

		Table[] partitions = this.partitionVariogramTable(rawVariogram, params);
		pushOutput(partitions, 0);
	}

	/**
		creates subset tables containing the rows in the raw variogram
		table that belong to each bin. this method actually delegates
		the real partitioning to getPartitionSubsets, and then creates
		subset tables based on what is returned by that method.

		@param rawVariogram a table with the first column h/distance values
			and the second as gamma(h) values
		@param params a parameter point containing the appropriate control
			parameters for this particular implementation
		
		@return an array of subset tables. all the tables contain the same
		underlying Columns, but different subsets
	*/		
	public Table[] partitionVariogramTable(TableImpl rawVariogram, 
		ParameterPoint params){
		
		int[][] parts = this.getPartitionSubsets(rawVariogram, params);
		int numParts = parts.length;
		
		Table[] partTables = new Table[numParts];
		for(int i = 0; i < numParts; i++){
			partTables[i] = new SubsetTableImpl(rawVariogram, parts[i]);
		}
		return partTables;
	}

	/**
		returns a 2d array of int's representing how the semivariogram
		should be partitioned to create the experimental semivariogram.
		This method should be overwritten to produce different behaviour
		concerning how lag values should be grouped.

		@param variogram a table with the first column h/distance values
			and the second as gamma(h) values. Assumed to be sorted
			by h values already.
		@param params a parameter point containing the appropriate control
			parameters for this particular implementation. Here, the number
			of bins to divide the range of lag distances into is the first
			and only parameter.

		@return a 2d int array of partitioning information.
	*/	
	public int[][] getPartitionSubsets(Table variogram, ParameterPoint params){

		StringBuffer dsb = new StringBuffer();
		
		int numBins = (int)params.getValue(0);
		int numRows = variogram.getNumRows();
		if(numBins > numRows)
			numBins = numRows;
		if(debug){
			System.out.println(this.getAlias() + ": numBins:" + numBins);
		}

		double[] range = TableUtilities.getMinMax(variogram, 0);
		double min = range[0];
		double max = range[1];

		double inc = (max - min) / numBins;

		int[][] parts = new int[numBins][];
		//make a phantom bin at the end to make calculating the number of
		//points in the last bin easier
		
		int[] startIndices = new int[numBins + 1];
		int k, j, i;
		k = 0;
		startIndices[k] = 0;
		startIndices[numBins] = numRows;
		k++;
		
		double thresh = min + inc;
		dsb.append("numRows:" + numRows + " numBins:" + numBins + "\n");
		for(i = 0; i < numRows; i++){
			dsb.append("val:" + variogram.getDouble(i, LAG_INDEX) +
			"  thresh:" + thresh + "\n");
			if(variogram.getDouble(i, LAG_INDEX) > thresh){
				dsb.append(" startIndex["+k+"] Assigned:" + i + "\n");
				startIndices[k] = i;
				thresh += inc;
				k++;
			}
		}
		int numPoints;
		k = 0;
		//System.out.println("NumRows:"+numRows);
		for(i = 0; i < numBins; i++){
			numPoints = startIndices[i+1] - startIndices[i];
			dsb.append("start:"+k+" -- numPoints:"+numPoints+
			" -- startIdx:"+startIndices[i] + 
			" -- (startIdx + 1):" + startIndices[i+1] + "\n");
			
			if(numPoints < 0){
				startIndices[i + 1] = startIndices[i];
				numPoints = 0;
			}
			
			try{
			parts[i] = new int[numPoints];
			
			for(j = 0; j < numPoints; j++){
				parts[i][j] = k;
				k++;
			}
			}catch(Exception e){
				System.out.println(dsb);
				return null;
			}
			
		}
		if(parts.length == 0){
			System.out.println(dsb);
		}

		return parts;
	}

		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////

	protected String standardPartitionDescription =
		"Partitioning involves assigning each row in a variogram table " +
		"to a partition. The rows of the partition are later combined in "+
		" some way to form a single point of the form (h, gamma(h))." +
		" Both the partitioning scheme and combination process can take on "+
		" a number of forms, so they are made independent (different modules)."; 
		
		;
	public String getModuleInfo(){
		return 	
			standardPartitionDescription +
			"This modules performs:" +
			" a standard equiwidth binning on the lag distances (the first"+
			" column). The parameterpoint should have the number of bins as"+
			" the first parameter. This is the only parameter that will be "+
			"used." +
			"";
	}
	
	
   	public String getModuleName() {
		return "Equi-Width Variogram Partition";
	}
	public String[] getInputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.table.basic.TableImpl",
			"ncsa.d2k.modules.core.datatype.parameter.ParameterPoint"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: 
				return "A 2 Column table representing a raw semivariogram." +
				" The first column is the lag distance, h, the second is the" +
				" variogram value, gamma(h).";
			case 1: 
				return "A parameter point giving information on how to perform" +
				" the binning. This instance of PartitionVariogram uses the first" +
				" parameter as the number of (equal width) bins to produce.";
			case 2: 
				return "";
			default: return "No such input";
		}
	}
	
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Raw Variogram";
			case 1:
				return "Binning Parameters";
			case 2:
				return "";
			default: return "No such input";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {
			"[Lncsa.d2k.modules.core.datatype.table.basic.SubsetTableImpl"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: 
				return "An array of subset tables. The examples in each table" +
				" represent the sets that should be considered together as a "+
				" point on an experimental semivariogram.";
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
				return "Semivariogram Partitions";
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
			
					

			

								
	
