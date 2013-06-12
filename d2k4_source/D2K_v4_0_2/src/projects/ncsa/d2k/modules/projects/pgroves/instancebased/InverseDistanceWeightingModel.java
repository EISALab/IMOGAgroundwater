package ncsa.d2k.modules.projects.pgroves.instancebased;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.PredictionModelModule;
import ncsa.d2k.modules.core.datatype.parameter.*;


/**
	@author pgroves
	*/

public class InverseDistanceWeightingModel extends PredictionModelModule 
	implements java.io.Serializable{

	//////////////////////
	//d2k Props
	////////////////////
	
	boolean debug=false;		

	int threadCount=1;

	boolean scaleInputs=true;
	/////////////////////////
	/// other fields
	////////////////////////
	
	double[] inputRanges;

	ExampleTable trainData;

	double distanceWeightingPower;

	//if distance==0, this value will replace it to prevent divide by zero
	double zeroDistanceSubValue;

	int neighborhoodSize;



	///////////////////////////
	///Constructor
	/////////////////////////

	public InverseDistanceWeightingModel(ExampleTable tt, ParameterPoint sol){
		super(tt);
		int r, c;
		//set the train table to the data field
		trainData = (ExampleTable)tt.copy();
		int numInputs = trainData.getNumInputFeatures();
		int[] inputs = trainData.getInputFeatures();
		int[] outputs = trainData.getOutputFeatures();
		
		int numRows = trainData.getNumRows();
		//System.out.println("********" + 
		//	this.getAlias()+" Num Train Rows:" + numRows);
		
		//calculate the ranges of the training input attributes
		
		inputRanges = new double[numInputs];
		
		double max, min;
		for(c = 0; c < numInputs; c++){
			max = trainData.getDouble(0, inputs[c]);
			min = max;
			for(r = 0; r < numRows; r++){
				if(min > trainData.getDouble(r,inputs[c])){
					min = trainData.getDouble(r, inputs[c]);
				}
				if(max < trainData.getDouble(r, inputs[c])){
					max = trainData.getDouble(r, inputs[c]);
				}
			}
			inputRanges[c]=max-min;
		}
		
		//get the output names
		/*outputNames=new String[data.getNumOutputFeatures()];
		for(c=0; c<outputNames.length;c++){
			outputNames[c]=data.getColumnLabel(outputs[c]);
		}*/		


		//set the parameters
		neighborhoodSize=(int)sol.getValue(0);
		distanceWeightingPower=sol.getValue(1);
		zeroDistanceSubValue=sol.getValue(2);

	}
		
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
		super.doit();
	}
	

	/**********************************
		PREDICT
		*******************************/


	/* does the overhead of setting up the prediction table, then
		delegates the actual predicting to 'makePredictions'
	
	public PredictionTable predict(ExampleTable et){

		PredictionTable predTable;
		if(et instanceof PredictionTable){
			predTable=(PredictionTable)et;
		}else{
			predTable=et.toPredictionTable();
		}

		//if there are no spots for pred columns
		if(predTable.getNumOutputFeatures()==0){
			for(int i=0; i<outputNames.length; i++){
				predTable.addPredictionColumn(new
						double[et.getNumRows()],outputNames[i]);

			}
		}
		if(debug){
			System.out.println("Prediction Cols:");
			int[] predCols=predTable.getPredictionSet();
			for(int i=0;i<predCols.length;i++){
				System.out.println(predCols[i]);
			}
			System.out.println("***");
		}
		makePredictions(predTable);
		return predTable;
	}
	*/
	
	/*
		does the actual work of making predictions by this model
		*/
	public void makePredictions(PredictionTable predTable){
		int numRows = predTable.getNumRows();

		//split the rows/examples up amongst the threads
		int exsPerThread = (int)numRows/threadCount;
		int[][] exampleIdxs = new int[threadCount][];
		int exsUsed = 0;
		int j = 0;
		int k;
		
		for(int i = 0; i < threadCount - 1; i++){
			exampleIdxs[i] = new int[exsPerThread];
			exsUsed += exsPerThread;
		}
		exampleIdxs[threadCount - 1] = new int[numRows-exsUsed];
		for(int i = 0; i < threadCount; i++){
			for(k = 0; k < exampleIdxs[i].length; k++){
				exampleIdxs[i][k] = j;
				j++;
			}
		}
		
		//start the threads running
		PredictionThread[] threads = new PredictionThread[threadCount];
		
		for(int i = 0; i < threadCount; i++){
			threads[i] = new PredictionThread(predTable, exampleIdxs[i]);
			threads[i].start();
		}
		
		//now wait for them all
		for(int i = 0; i < threadCount; i++){
			try{
				threads[i].join();
			}catch(Exception e){
				System.out.println("Thread Error");
				e.printStackTrace();
			}
			//System.out.println("Thread done:"+i);
			
		}
	}

	private class PredictionThread extends Thread {
		volatile PredictionTable predTable;

		int[] exampleIdx;

		public PredictionThread(PredictionTable pt,
								int[] exampleIndicesToEvaluate){
			predTable = pt;
			exampleIdx = exampleIndicesToEvaluate;
		}
		public void run(){
			this.makePredictions();
		}
		
		private void makePredictions(){
			int i, j, k;
			
			double d;
			ExampleTable data = trainData;
			int numRows = exampleIdx.length;
			int numDataRows = data.getNumRows();

			int numOutputs = data.getNumOutputFeatures();
			int numInputs = data.getNumInputFeatures();
			int[] dataIns = data.getInputFeatures();
			int[] predIns = predTable.getInputFeatures();
			int[] dataOuts = data.getOutputFeatures();
			int[] predCols = predTable.getPredictionSet();
			
			
			//the params as set will not necessarily be what we actually use
			int neighSize = neighborhoodSize;
			if(neighSize > numDataRows)
				neighSize = numDataRows;
				
			//this is modified so that only one exponent must be used (the
			//square root from the dist calc is absorbed into the weight
			//power	
			double weightPow = distanceWeightingPower / 2;

			
			//these will be reused by every output in every prediction row
			int[] bestNeighs = new int[neighSize];//index to rows in 'data'
			double[] bestDists = new double[neighSize];
			double currentDist;

			double currentWeight;
			double sumOfWeights;
			
			for(i = 0; i < numRows; i++){
				//find the best n neighbors
				for(j = 0; j < neighSize; j++){
					bestDists[j] = Double.POSITIVE_INFINITY;
				}
				for(j = 0; j < numDataRows; j++){
					currentDist = 0;
					d = 0;
					for(k = 0;k<numInputs;k++){
						d = data.getDouble(j, dataIns[k])
							- predTable.getDouble(exampleIdx[i], predIns[k]);
						if(scaleInputs){	
							d /= inputRanges[k];
						}
						currentDist += d * d;
					}
					//d = d/numInputs;
					//insert into 'best' arrays
					k = neighSize - 1;
					if(currentDist < bestDists[k]){
						bestDists[k] = currentDist;
						bestNeighs[k] = j;
						k--;
						if(debug){
							System.out.println(" insert:"+currentDist+" , "+
							j);
						}
					}
					while((k >= 0) && (currentDist < bestDists[k])){
						//swap them a la bubble sort
						bestDists[k+1] = bestDists[k];
						bestDists[k] = currentDist;

						bestNeighs[k+1] = bestNeighs[k];
						bestNeighs[k] = j;
						k--;
					}
				}
				if(debug){
					System.out.println("    Best:");
					for(j = 0;j < neighSize; j++) {
						System.out.println("     " + bestDists[j] + " , " +
						bestNeighs[j] + ":" +
						data.getDouble(bestNeighs[j], dataOuts[0]));
					}
				}
				//get rid of the missing values in the prediction
				//columns, initialize them to zero
				for(k = 0; k < numOutputs; k++){
					predTable.setDoublePrediction(0.0d, exampleIdx[i], k);
				}
					
				//the best distances are known, calc the weighted average
				//output(s)
				sumOfWeights = 0;
				for(j = 0; j < neighSize; j++){
					if(bestDists[j] == 0.0D){
						bestDists[j] = zeroDistanceSubValue;
					}
					currentWeight = 1.0 / Math.pow(bestDists[j], weightPow);
					sumOfWeights += currentWeight;

					for(k = 0; k < numOutputs; k++){
						d = (currentWeight
							* data.getDouble(bestNeighs[j], dataOuts[k]))
							+ predTable.getDoublePrediction(exampleIdx[i], k);
							
						predTable.setDoublePrediction(d, exampleIdx[i], k);
					}
				}
				for(k = 0; k < numOutputs; k++){
					d = predTable.getDoublePrediction(exampleIdx[i], k);
					d /= sumOfWeights;
					if(debug)
						System.out.println("FinalPred:" + d);
					predTable.setDoublePrediction(d, exampleIdx[i], k);
				}
			}
		}
	}
								
		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return 	
				"Instance Based (nearest neighbor) model based on the inverse"
				+" distance weighted average of the k nearest neighbors."+
				" The number of threads used during prediction/evaluation "+
				"can be set in the properties."+
				" This model was built with the following parameters:</p>"+
				"<ul><li>Distance Weighting Power:" + distanceWeightingPower+
				"<li>Neighborhood Size:" + neighborhoodSize+
				"<li>zeroDistanceSubstitution Value:" + zeroDistanceSubValue+
				"</ul>";
	}
	
   	public String getModuleName() {
		return "InverseDistWeight";
	}
	public String[] getInputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: return "The data table to make predictions for";
			default: return "No such input";
		}
	}
	
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Prediction Data";
			default: return "NO SUCH INPUT!";
		}
	}
	public String[] getOutputTypes(){
		String[] types={"ncsa.d2k.modules.core.datatype.table.PredictionTable"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: return "Table with predictions set";
			default: return "No such output";
		}
	}
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Filled in prediction table";
			default: return "NO SUCH OUTPUT!";
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
	public int getThreadCount(){
		return threadCount;
	}
	public void setThreadCount(int i){
		threadCount=i;
	}
	public boolean getScaleInputs(){
		return scaleInputs;
	}
	public void setScaleInputs(boolean b){
		scaleInputs=b;
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
			
					

			

								
	
