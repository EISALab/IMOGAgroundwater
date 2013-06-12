package ncsa.d2k.modules.projects.pgroves.instancebased;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.PredictionModelModule;
import ncsa.d2k.modules.core.optimize.util.*;


/*
	uses an instance based learning method to do classification 
	@author pgroves
	*/

public class ClassifierModel extends PredictionModelModule 
	implements java.io.Serializable{

	//////////////////////
	//d2k Props
	////////////////////
	
	boolean debug=false;		

	int threadCount=1;
	/////////////////////////
	/// other fields
	////////////////////////
	
	/*save these so we can make new PredictionColumns
	if necessary*/
	String[] outputNames;

	/*how many possible classes each output corresponds to*/
	int[] outputCard;
	
	double[] inputRanges;

	TrainTable data;

	double distanceWeightingPower;

	//if distance==0, this value will replace it to prevent divide by zero
	double zeroDistanceSubValue;

	int neighborhoodSize;



	///////////////////////////
	///Constructor
	/////////////////////////

	public ClassifierModel(TrainTable tt, Solution sol){
		super(tt);
		int r, c;
		//set the train table to the data field
		data=(TrainTable)tt.copy();
		int numInputs=data.getNumInputFeatures();
		int[] inputs=data.getInputFeatures();
		int[] outputs=data.getOutputFeatures();
		
		int numRows=data.getNumRows();
		
		//calculate the ranges of the training input attributes
		
		inputRanges=new double[numInputs];
		double max, min;
		for(c=0;c<numInputs;c++){
			max=data.getDouble(0, inputs[c]);
			min=max;
			for(r=0; r<numRows;r++){
				if(min>data.getDouble(r,inputs[c])){
					min=data.getDouble(r, inputs[c]);
				}
				if(max<data.getDouble(r, inputs[c])){
					max=data.getDouble(r, inputs[c]);
				}
			}
			inputRanges[c]=max-min;
		}
		
		//get the output names and number of distinct classes
		outputNames=new String[data.getNumOutputFeatures()];
		outputCard=new int[data.getNumOutputFeatures()];

		for(c=0; c<outputNames.length;c++){
			outputNames[c]=data.getColumnLabel(outputs[c]);

			for(r=0;r<numRows;r++){
				if(outputCard[c]<data.getInt(r,outputs[c])){
					outputCard[c]=data.getInt(outputs[c],r);
				}
			}
		}		


		//set the parameters
		neighborhoodSize=(int)sol.getDoubleParameter(0);
		distanceWeightingPower=sol.getDoubleParameter(1);
		zeroDistanceSubValue=sol.getDoubleParameter(2);

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
						int[et.getNumRows()],outputNames[i]);

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
	protected void makePredictions(PredictionTable predTable){
		int numRows=predTable.getNumRows();

		//split the rows/examples up amongst the threads
		int exsPerThread=(int)numRows/threadCount;
		int[][] exampleIdxs=new int[threadCount][];
		int exsUsed=0;
		int j=0;
		int k;
		for(int i=0;i<threadCount-1;i++){
			exampleIdxs[i]=new int[exsPerThread];
			exsUsed+=exsPerThread;
		}
		exampleIdxs[threadCount-1]=new int[numRows-exsUsed];
		for(int i=0;i<threadCount;i++){
			for(k=0;k<exampleIdxs[i].length;k++){
				exampleIdxs[i][k]=j;
				j++;
			}
		}
		
		//start the threads running
		PredictionThread[] threads=new PredictionThread[threadCount];
		for(int i=0;i<threadCount;i++){
			threads[i]=new PredictionThread(predTable, exampleIdxs[i]);
			threads[i].start();
		}
		//now wait for them all
		for(int i=0;i<threadCount;i++){
			try{
				threads[i].join();
			}catch(Exception e){
				System.out.println("Thread Error");
				e.printStackTrace();
			}
			if(debug)
				System.out.println("Thread done:"+i);
			
		}
	}

	private class PredictionThread extends Thread{
		volatile PredictionTable predTable;

		int[] exampleIdx;

		public PredictionThread(PredictionTable pt,
								int[] exampleIndicesToEvaluate){
			predTable=pt;
			exampleIdx=exampleIndicesToEvaluate;
		}
		public void run(){
			this.makePredictions();
		}
		
		private void makePredictions(){
			int i,j,k;
			double d;
			int numRows=exampleIdx.length;
			int numDataRows=data.getNumRows();

			int numOutputs=data.getNumOutputFeatures();
			int numInputs=data.getNumInputFeatures();
			int[] dataIns=data.getInputFeatures();
			int[] predIns=predTable.getInputFeatures();
			int[] dataOuts=data.getOutputFeatures();
			int[] predCols=predTable.getPredictionSet();
			
			
			//the params as set will not necessarily be what we actually use
			int neighSize=neighborhoodSize;
			if(neighSize>numDataRows)
				neighSize=numDataRows;
			//this is modified so that only one exponent must be used (the
			//square root from the dist calc is absorbed into the weight
			//power	
			double weightPow=distanceWeightingPower/2;

			
			//these will be reused by every output in every prediction row
			int[] bestNeighs=new int[neighSize];//index to rows in 'data'
			double[] bestDists=new double[neighSize];
			double currentDist;

			double currentWeight;
			double sumOfWeights;
			
			for(i=0;i<numRows;i++){
				//find the best n neighbors
				for(j=0;j<neighSize;j++){
					bestDists[j]=Double.POSITIVE_INFINITY;
				}
				for(j=0;j<numDataRows;j++){
					currentDist=0;
					d=0;
					for(k=0;k<numInputs;k++){
						d=	data.getDouble(j,dataIns[k])
							- predTable.getDouble(exampleIdx[i], predIns[k]);
						d/=inputRanges[k];
						currentDist+=d*d;
					}
					//d=d/numInputs;
					//insert into 'best' arrays
					k=neighSize-1;
					if(currentDist<bestDists[k]){
						bestDists[k]=currentDist;
						bestNeighs[k]=j;
						k--;
						if(debug){
							System.out.println(" insert:"+currentDist+" , "+
							j);
						}
					}
					while((k>=0)&&(currentDist<bestDists[k])){
						//swap them a la bubble sort
						bestDists[k+1]=bestDists[k];
						bestDists[k]=currentDist;

						bestNeighs[k+1]=bestNeighs[k];
						bestNeighs[k]=j;
						k--;
					}
				}
				if(debug){
					System.out.println("    Best:");
					for(j=0;j<neighSize;j++) {
						System.out.println("     "+bestDists[j]+" , "+
						bestNeighs[j]+":"+
						data.getDouble(bestNeighs[j],dataOuts[0]));
					}
				}
				//the best distances are known, calc the weighted average
				//output(s), note: assuming nominals indicated by ints
				//starting at zero
				
				double[][] classScores=new double[numOutputs][];
				for(k=0;k<numOutputs;k++){
					classScores[k]=new double[outputCard[k]];
				}
				double max;
				int best;

				for(j=0;j<neighSize;j++){
					if(bestDists[j]==0.0D){
						bestDists[j]=zeroDistanceSubValue;
					}
					currentWeight=1.0/Math.pow(bestDists[j], weightPow);
					
					//tally the overall scores for each possible class, for
					//each output
					for(k=0;k<numOutputs;k++){
						classScores[k][data.getInt(bestNeighs[j],dataOuts[k])]+=
							currentWeight;
							
					}
				}
				//now that each class has a summation over the neighbors,
				//find the best one
				for(k=0;k<numOutputs;k++){
					max=0D;
					best=0;
					for(j=0;j<outputCard[k];j++){
						if(max<classScores[k][j]){
							max=classScores[k][j];
							best=j;
						}
					}
					predTable.setIntPrediction(best, exampleIdx[i], k);
				}
				
				/*sumOfWeights=0;
				double max;
				int best=0;
				double[] classScores=new double[numOutputs];
				
				for(j=0;j<neighSize;j++){
					if(bestDists[j]==0.0D){
						bestDists[j]=zeroDistanceSubValue;
					}
					currentWeight=1.0/Math.pow(bestDists[j], weightPow);
					
					//tally the overall scores for each possible class
					for(k=0;k<numOutputs;k++){
						if(data.getInt(bestNeighs[j],dataOuts[k])==1){
							classScores[k]+=currentWeight;
						}
					}
					max=0D;
					for(k=0;k<numOutputs;k++){
						if(max<classScores[k]){
							max=classScores[k];
							best=k;
						}
					}
					predTable.setIntPrediction(1, exampleIdx[i], best);
				}*/
			}
		}
	}
								
		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return 	"<html><head></head><body><p></p>"+
				"Instance Based (nearest neighbor) model based on the inverse"
				+" distance of the k nearest neighbors."+
				" Sums the inverse distance raised to some power for each"+
				" possible classification (over the set of nearest neighbors)"+
				" The number of threads used during prediction/evaluation "+
				"can be set in the properties."+
				" This model was built with the following parameters:</p>"+
				"<ul><li>Distance Weighting Power:"+distanceWeightingPower+
				"<li>Neighborhood Size:"+neighborhoodSize+
				"<li>zeroDistanceSubstitution Value:"+zeroDistanceSubValue+
				"</ul></body></html>";
	}
	
   	public String getModuleName() {
		return "IBClassifier";
	}
	public String[] getInputTypes(){
		String[] types = {"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
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
			
					

			

								
	
