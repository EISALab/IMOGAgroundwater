package ncsa.d2k.modules.projects.pgroves.bp;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;


/** 
	Wilcoxon Signed-Rank test for two correlated samples. Tells if two
	sets of samples, where there exists some meaningful way to
	pair up the samples across the sets, come from the same distribution.
	For instance, if you make two sets of predictions of a continuous
	variable using two different PredictionModelModules, you can
	see if the sets of predictions are significantly different (or
	the same, with any differences attributable to noise/chance).

	An excellent description can be found here:
	http://faculty.vassar.edu/lowry/ch12a.html

	@author pgroves
	@date 02/04/04
	*/

public class WilcoxonTest extends ComputeModule 
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
		if(debug){
			System.out.println(getAlias()+":Firing");
		}
		double[] set1 = (double[])pullInput(0);
		double[] set2 = (double[])pullInput(1);

		double score = this.sigTest(set1, set2);
		pushOutput(new Double(score), 0);
	}
	
	/**
		performs the actual test on the two sets (arrays) of doubles.
		it is assumed that two doubles at the same index, but in the
		different arrays, have some sort of meaningful relationship.
		
		@param set1 a set of doubles
		@param set2 another set of doubles that has some correspondence
		to set1	

		@return the Z score 
	*/
	public double sigTest(double[] set1, double[] set2){
		int setSize = set1.length;
		if(set2.length != setSize){
			System.out.println(getAlias() + " ERROR: sets are different size");
			return Double.MAX_VALUE;
		}
			
		// make a table, where the abs of differences are the first column
		// and the second column is a boolean column where 'true' means
		// the difference was positive and 'false' means it was negative
		int diffCol = 0;
		int signCol = 1;
		boolean posDiff = true;
		boolean negDiff = false;
		
		Column[] cols = new Column[2];
		cols[diffCol] = new DoubleColumn(setSize);
		cols[signCol] = new BooleanColumn(setSize);

		MutableTableImpl diffTable = new MutableTableImpl(cols);
		
		int i, j, k;
		double d;
		boolean b;
		for(i = 0; i < setSize; i++){
			 d = set1[i] - set2[i];
			 if(debug)
			 System.out.println("ind:"+i+" set1:" + set1[i] + " set2:" + set2[i] +
			 " diff:" + d);
			 if(d >= 0){
				 b = posDiff;
			 }else{
				 b = negDiff;
				 d *= -1;
			 }
			 diffTable.setDouble(d, i, diffCol);
			 diffTable.setBoolean(b, i, signCol);
		}
		//sort by absolute value
		diffTable.sortByColumn(diffCol);

		//which rank we're actually at 
		double rank = 1.0;
		
		//the cummulative sum of the signed ranks 
		double rankSum = 0.0;

		//when several diff's are the same, they all get this rank
		double tieRank = 0.0;;
		//how many diff's are the same, used to compute tieRank
		int tieCount = 0;

		//we shouldn't consider those differences that were
		//zero when determining set size, so decrement this
		//every time that's true
		int effectiveSetSize = setSize;
		
		for(i = 0; i < setSize; i++){
			d = diffTable.getDouble(i, diffCol);
			if(d == 0.0){
				// if there's no difference, no rank is assigned
				effectiveSetSize--;
				continue;
			}
			//in case of a tie, all of the equivalent diff's are 
			//given the same rank, which is the average of ranks 
			//they would be given if they were different from each 
			//other. for code simplicity, we'll say there's always a tie,
			//but there will be only one number in the 'tie' if
			//there isn't really a tie
			tieRank = rank;
			tieCount = 1;
			while(
				( (i + tieCount) < setSize) && 
				(d == diffTable.getDouble(i + tieCount, diffCol))){
				
				rank++;
				tieRank += rank;
				tieCount++;
			}
			tieRank /= (double)tieCount;
			for(j = 0; j < tieCount; j++){
				//give all of the ties the same rank
				
				//this is the row in the table
				k = i + j;
				//whether to add or subtract the rank
				b = diffTable.getBoolean(k, signCol);

				//add or subtract the rank 
				if(b == posDiff){
					rankSum += tieRank;
				}else{
					rankSum -= tieRank;
				}
			}
			//proceed to the next rank
			rank++;
			i += (tieCount - 1);
		}

		//now we have the sum of the signed ranks, or W. next we
		//find the expected standard deviation, sigmaW, and then
		//finally the Z-ratio, which tells us where in the
		//standard normal distribution we are
		double sigmaW = calcSigmaW(effectiveSetSize);

		double zRatio = rankSum - 0.5;
		zRatio /= sigmaW;
		if(debug){
			System.out.println(getAlias() + " SignedRankSum:" + rankSum +
			", sigmaW:" + sigmaW + ", Z Ratio:" + zRatio +
			", effectiveSetSize:"+effectiveSetSize);
		}
		//if they were all the same, return zero (the best possible
		//zratio)
		if(effectiveSetSize == 0.0){
			return 0.0;
		}		

		return zRatio;
	}

	/**
		Takes the sample size in a Wilcoxon test and calculates
		the expect standard deviation of the sum of the signed ranks,
		assuming that the two distributions in questions are actually
		drawn from the same underlying population.

		@param n the number of samples in each set in the Wilcoxon Test
		@return the standard deviation
		*/
	private double calcSigmaW(int n){
		double sig = (double)n;
		sig = sig * (sig + 1) * (2 * sig + 1);
		sig /= 6;
		sig = Math.sqrt(sig);
		return sig;
	}

		
	
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return 	
		"<p>Wilcoxon Signed-Rank test for two correlated samples. Tells if two" +
		" sets of samples, where there exists some meaningful way to" +
		" pair up the samples across the sets, come from the same distribution." +
		" For instance, if you make two sets of predictions of a continuous" +
		" variable using two different PredictionModelModules, you can" +
		" see if the sets of predictions are significantly different (or" +
		" the same, with any differences attributable to noise/chance)." +
		" <p>An excellent description can be found here:" +
		" <br>http://faculty.vassar.edu/lowry/ch12a.html" +
			""+
			""+
			"";
	}
	
   	public String getModuleName() {
		return "WilcoxonTest";
	}
	public String[] getInputTypes(){
		String[] types = {
			"[d:","[d:"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: 
				return "First set of double (double array) ";
			case 1: 
				return "Second set of double (double array)";
			case 2: 
				return "";
			default: return "No such input";
		}
	}
	
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Sample Set 1";
			case 1:
				return "Sample Set 2";
			case 2:
				return "";
			default: return "No such input";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {
			"java.lang.Double"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: 
				return "The Z-ratio score. Closer to zeros means the two"+
				" sets are more similar";
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
				return "Z-ratio";
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
			
					

			

								
	
