package ncsa.d2k.modules.projects.pgroves.geostat;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.util.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.datatype.parameter.*;


/** doesn't remove outliers from the variogram so much as
 * it removes samples that are outliers that adversely
 * affect the variogram. Basically, if the removal of
 * a single sample greatly changes the binned variogram,
 * it is left out.


	@author pgroves
	@date 03/25/04
	*/

public class RemoveVariogramOutliers extends ComputeModule 
	implements java.io.Serializable {

	//////////////////////
	//d2k Props
	////////////////////
	
	boolean debug=false;		
	/////////////////////////
	/// other fields
	////////////////////////
	
	public final static int CHANGE_THRESHOLD = 0;
	public final static int MAX_REMOVABLE = 1;
	
	
	boolean firstRun = true;

	MutableTable lastBinnedVariogram = null;
	MutableTable firstBinnedVariogram = null;
	Table rawVariogram = null;
	
	ExampleTable data = null;

	/**the sorted order of the data table in terms of the output, ascending*/
	int[] outputOrdering = null;

	/** how many of the highest values have been removed */
	int numOutliersRemoved = 0;
	
	ParameterPoint parameters = null;

	//////////////////////////
	///d2k control methods
	///////////////////////

	/**
	 * on the firstRun, must wait for an intial variogram and parameters,
	 * after that, must wait for the candidate variogram
	 * it pushed out.
	 */
	public boolean isReady(){
		if(firstRun){
			return 
				(this.getFlags()[0] > 0 &&
				 this.getFlags()[1] > 0 &&
				 this.getFlags()[2] > 0 ) ;
		}else{
			return this.getFlags()[3] > 0;
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
		firstRun = true;
		lastBinnedVariogram = null;
		rawVariogram = null;
		parameters = null;
		data = null;
		outputOrdering = null;
		numOutliersRemoved = 0;
		firstBinnedVariogram = null;
	}
	
	/////////////////////
	//work methods
	////////////////////
	/**
		does it
	*/
	public void doit() throws Exception{
		if(debug){
			System.out.println(getAlias()+":Firing");
		}

		if(firstRun){
			rawVariogram = (Table)pullInput(0);
			parameters = (ParameterPoint)pullInput(1);
			data = (ExampleTable)pullInput(2);
			if(data.getNumRows() <= 2){
				if(debug){
					System.out.println("removeOutliers: first run numRows:"+
					data.getNumRows());
				}
				//if we're here, no binning or outlier removal
				// is possible, use the raw variogram
				pushOutput(makeBinTableIntoExampleTable(
					(MutableTable)rawVariogram), 1);
				this.wipeFields();
				return;
			}	
			
			int[] sortCols = new int[1];
			sortCols[0] = data.getOutputFeatures()[0];
			
			outputOrdering = TableUtilities.multiSortIndex(data, sortCols);
			firstRun = false;
			pushOutput(rawVariogram, 0);
			if(debug)
				System.out.println(this.getAlias() + ": first run complete.");
		}else{
			if(debug){
				System.out.println(this.getAlias() + ": outliers removed:" +
						this.numOutliersRemoved);
			}
				//System.out.println("removeOutliers: numRows:"+
				//data.getNumRows());
			MutableTable currVariogram = (MutableTable)pullInput(3);
			if(lastBinnedVariogram == null){
				lastBinnedVariogram = currVariogram;
				firstBinnedVariogram = currVariogram;
				MutableTableImpl firstCandidate = makeNextVariogram();
				pushOutput(firstCandidate, 0);
				return;
			}
			if(debug){
				System.out.println("\n\t NumRows in candidate:" + 
					currVariogram.getNumRows() +
					"\n\t numRows in mainVar:" + rawVariogram.getNumRows() +
					"\n\t numRows in data:" + data.getNumRows() +
					"\n\t outliers removed:" + numOutliersRemoved);
			}

			if(acceptVariogram(currVariogram) &&
				(numOutliersRemoved <= this.parameters.getValue(MAX_REMOVABLE)) &&
				(numOutliersRemoved < (data.getNumRows() / 2))){
				//NOTE!: I am imposing a hard limit of 1/2 the number of 
				//data points to be the most outliers that can be removed,
				//regardless of the value from the parameter
				if(numOutliersRemoved >= (data.getNumRows() / 2)){
					System.out.println("!!!!!There is a problem in " +
					" RemoveVagriogramOutliers. Too many outliers are being " +
					"removed");
				}
				MutableTableImpl newVariogram = makeNextVariogram();
				pushOutput(newVariogram, 0);
				lastBinnedVariogram = currVariogram;
			}else{
				//the last variogram is as good as it's going to get,
				//or we aren't allowed to remove any more data points
				if(lastBinnedVariogram.getNumRows() <= 3){
					System.out.println("!!!!!There is a problem in " +
					" RemoveVagriogramOutliers. Too many outliers are being " +
					"removed - accepted");
					System.out.println("numRows CurrVar:" + 
						currVariogram.getNumRows() +
						"numrows lastVar:" + lastBinnedVariogram.getNumRows() +
						" num Data points:" + data.getNumRows() + 
						" numoutliers removed:" + this.numOutliersRemoved);
				}
				
				pushOutput(makeBinTableIntoExampleTable(lastBinnedVariogram), 1);
				if(debug){
					System.out.println("***" + this.getAlias() + 
							" DONE: num outliers removed:" +
							(this.numOutliersRemoved - 1));
				}
				wipeFields();
			}
		}
	}		

	/**
	 * finds if a passed in variogram meets the threshold criteria
	 * indicated by the parameters of this module. The criteria
	 * is as follows:
	 * <p>the maximum difference between a point in the current
	 * variogram and the last variogram, divided by that value
	 * in <i>the original, or first, variogram</i> must 
	 * exceed the value at <code>CHANGE_THRESHOLD</code> in
	 * this modules parameter object.
	 */
	
	protected boolean acceptVariogram(Table variogram){
		int gammaCol = 1;
		int numBins = variogram.getNumRows();
		double diff, diffFrac;
		double d;
		/*System.out.println(
				" num Rows lastBinned:" + lastBinnedVariogram.getNumRows());
		System.out.println(
				" num Rows current Binned:" + variogram.getNumRows());
		System.out.println(
				" num Rows first Binned:" + firstBinnedVariogram.getNumRows());
		*/		
		if(numBins > lastBinnedVariogram.getNumRows()){
			numBins = lastBinnedVariogram.getNumRows();
		}
		if(numBins > firstBinnedVariogram.getNumRows()){
			numBins = firstBinnedVariogram.getNumRows();
		}
		for(int i = 0; i < numBins; i++){
			diff = variogram.getDouble(i, gammaCol);
			diff -= this.lastBinnedVariogram.getDouble(i, gammaCol);
			d = this.firstBinnedVariogram.getDouble(i, gammaCol);
			if(d <= 0){
				diffFrac = 0; 
			}else{
				diffFrac = diff / d;
			}
									
			if(diffFrac < 0){
				diffFrac *= -1;
			}
			if(debug){
				System.out.println(this.getAlias() + ": diffFrac:" + diffFrac);
			}

			if (diffFrac > this.parameters.getValue(CHANGE_THRESHOLD)){
				return true;
			}
		}
	
		return false;
	}

	/**
	 * removes all the rows in a variogram that are based upon
	 * the next highest sampled value. every call to this
	 * method therefore removes as many points from the variogram
	 * as there are data samples.
	 */
	protected MutableTableImpl makeNextVariogram(){
		this.numOutliersRemoved++;
		int[] outlierRows = getTopOutliers();
		
		int numCols = this.rawVariogram.getNumColumns();
		int numDataRows = this.data.getNumRows();
		int numVarRows = this.rawVariogram.getNumRows();
		
		int newNumVarRows = numDataRows - this.numOutliersRemoved;
		newNumVarRows = newNumVarRows * newNumVarRows - newNumVarRows;
		/*newNumVarRows -= numDataRows;
		newNumVarRows -= 2 * this.numOutliersRemoved * (numDataRows - 1);
		//when two data points are both to be removed, the variogram
		//row they have in common is counted twice by the previous
		//line. therefore we compensate as follows:
		newNumVarRows += this.numOutliersRemoved * 
			(this.numOutliersRemoved - 1) / 2;
		*/
		Column[] cols = new Column[numCols];
		int i, j, k, l;
		for(i = 0; i <  numCols; i++){
			cols[i] = ColumnUtilities.createColumn(
					this.rawVariogram.getColumnType(i), newNumVarRows);
			cols[i].setLabel(this.rawVariogram.getColumnLabel(i));
		}
		MutableTableImpl newVariogram = new MutableTableImpl(cols);
		//index into rawVariogram 
		k = 0;
		//index into new variogram
		l = 0;


		if(debug){
			System.out.println("num samples removed:"+this.numOutliersRemoved);
			System.out.println("numRawRows:"+numVarRows);
			System.out.println("newNumVarRows:"+newNumVarRows);
		}
		for(i = 0; i < numDataRows; i++){
			if(arrayContains(outlierRows, i)){
				k += numDataRows - 1;
				continue;
			}
			for(j = 0; j < numDataRows; j++){
				if(i == j){
					continue;
				}
				if(!arrayContains(outlierRows, j)){
					try{
					TableUtilities.setRow(this.rawVariogram, k, newVariogram, l);
					}catch(Exception e){
						System.out.println("i:" + i + ", j:" + j + ", l:" + l +
								", k:" + k);
						e.printStackTrace();
						return newVariogram;
					}
					l++;
				}
				k++;
			}
		}
		if(debug){
			System.out.println("Number raw rows iterated:" + k);
			System.out.println("Number new rows filled in:" + l);
			System.out.println();
		}
		return newVariogram;
	}
		
	/**
	 * tells if the given int is in the int array 
	 */
	protected boolean arrayContains(int[] array, int val){
		for(int i = 0; i < array.length; i++){
			if(val == array[i]){
				return true;
			}
		}
		return false;
	}
	/**
	 * gets the row indices of the top n extreme values, based on the global
	 * values of the <code>outputOrdering</code> and 
	 * <code>numOutliersRemoved</code>
	 */
	protected int[] getTopOutliers(){
		int[] outliers = new int[this.numOutliersRemoved];
		for(int i = 0; i < this.numOutliersRemoved; i++){
			//outputOrdering is in ascending order, so start at the end
			//and work backwards
			outliers[i] = outputOrdering[outputOrdering.length - i - 1];
		}
		return outliers;
	}

	/**
	 * Turns the binned variogram into an Example Table with
	 * the correct inputs and outputs and all of the examples
	 * in the train set
	 */
	protected ExampleTable makeBinTableIntoExampleTable(MutableTable tbl){

		ExampleTableImpl et = new ExampleTableImpl((MutableTableImpl)tbl);

		int[] inputs = new int[1];
		inputs[0] = 0;
		int[] outputs = new int[1];
		outputs[0] = 1;

		et.setInputFeatures(inputs);
		et.setOutputFeatures(outputs);

		for(int i = 0; i < 2; i++){
			et.setColumnLabel(rawVariogram.getColumnLabel(i), i);
		}
		int numRows = et.getNumRows();
		int[] trainSet = new int[numRows];
		for(int i = 0; i < numRows; i++){
			trainSet[i] = i;
		}
		et.setTrainingSet(trainSet);
		return et;
	}
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return 	
			"Iteratively removes the most extreme (highest) value from "+
			" a data set and recalculates the raw experimental variogram."+
			" If the results of a new variogram binning differ significantly"+
			" from the previous binned variogram, the new variogram is kept" +
			" and the process is repeated. The process stops when either "+
			"the maximum number outliers permitted to be removed is reached "+
			"or the greatest difference of any bin is not above the differe" +
			"nceFracThreshold. Both these values are contained in the pulled" +
			" in paramter point."+
			"<p>Firing Info: On the first run this module pulls in the raw "+
			"experimental variogram, the original data, and the parameterpoint" +
			". It then pushes out a new experimental variogram for binning and" +
			"waits only for the binned variogram to return. The binned variogram"+
			" is pulled in and a new raw variogram subset is pushed out iterati"+
			"vely until one of the stop criteria is met, at which point "+
			"the 'winning' binned variogram is pushed out of the second output";
	}
	
   	public String getModuleName() {
		return "RemoveVariogramOutliers";
	}
	public String[] getInputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.table.Table",
			"ncsa.d2k.modules.core.datatype.parameter.ParameterPoint",
			"ncsa.d2k.modules.core.datatype.table.ExampleTable",
			"ncsa.d2k.modules.core.datatype.table.Table"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: 
				return "The complete (unbinned) experimental variogram data" +
					". The first column should be 'h', the second 'gamma(h).'";
			case 1: 
				return "Parameters that contain the stop criteria. <p>The first" +
					" parameter is the CHANGE_THRESHOLD. For each bin, the "+
					" bin's value from the previous binning is subtracted from"+
					" contestant binning's value, and this is divided by the "+
					" bin value from the original variogram (no outliers removed" +
					". If this value does not exceed the CHANGE_THRESHOLD for "+
					"an.y bin, then the contestant is discarded."+
					"<p> The second param is MAX_REMOVABLE. This is the maximum "+
					"number of outliers that can be removed, regardless of perform" +
					"ance of the first criteria.";
			case 2: 
				return "The original data set that the raw experimental variogram"+
					" was generated from.";
			case 3: 
				return "After this module pushes out a candidate experimantal" +
					" variogram, the binned version is passed back in here so " +
					"that the degree of improvement can be measured.";

			default: return "No such input";
		}
	}
	
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Raw Experimental Variogram";
			case 1:
				return "Stop Criteria Parameters";
			case 2:
				return "Samples Data";
			case 3:
				return "Binned Candidate Variogram";
			default: return "No such input";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.table.basic.MutableTableImpl",
			"ncsa.d2k.modules.core.datatype.table.Table" };
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: 
				return "A subset of the raw experimental variogram, with values"+
					" that used outliers removed.";
			case 1:
				return "The binned variogram that 'wins.' This will be the same"+
					" object as the last passed in Binned Candidate Variogram.";
			case 2:
				return "";
			default: return "No such output";
		}
	}
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Experimental Variogram Subset";
			case 1:
				return "Best Binned Variogram";
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
			
					

			

								
	
