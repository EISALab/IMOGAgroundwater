package ncsa.d2k.modules.projects.pgroves.instancebased;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.PredictionModelModule;
//import ncsa.d2k.modules.core.optimize.util.*;

/** An Instance Based PredictionModelModule that uses pre-calculated 
	distances and an inverse distance weighting (IDW) method.

	This version does not scale the input features to a common range.

	This assumes that cross-validation is being performed and that the
	training and any test sets are subsets of the same example table.


	@author pgroves
	@date 01/14/04
	*/

public class CachedIDWModel extends PredictionModelModule 
	implements java.io.Serializable {

	//////////////////////
	//d2k Props
	////////////////////
	
	boolean debug=false;		
	/////////////////////////
	/// other fields
	////////////////////////
	
	/**The table of examples that this model is built with. The distances to
	these examples, along with their output values, will be used in making a 
	prediction.*/
	ExampleTable data;

	/**holds the pre-computed distances*/
	Table distanceMatrix;

	/**holds the orderings of the pre-computed distances for each example.
	Created by module CalcDistanceMatrix. Each column is the ordering for
	one of the examples in the table that the input TrainTable 
	(<code>data</code>) is a subset of. */
	Table neighborOrder;
	
	double distanceWeightingPower;
	
	//if distance==0, this value will replace it to prevent divide by zero
	double zeroDistanceSubValue;

	int neighborhoodSize;	

	/**the same size as the number of rows in the master table that the 
	train and test tables are based on. If the row in the original table
	is part of the training set, this contains the index that it is in the
	training set. If not in the training set, -1. This must be used to
	insure that only examples in the training set are used to make 
	predictions*/
	int[] trainExsInv;
	
	/**keep the parameters around*/
	ParameterPoint params;
	///////////////////////////
	///Constructor
	/////////////////////////

	public CachedIDWModel(Table trainTable, ParameterPoint sol, 
		Table distMat, Table sortedDists){
		
		super((ExampleTable)trainTable);
		//set the train table to the data field. make the best copy possible
		data = new ExampleTableImpl((TableImpl)trainTable);

		distanceMatrix = distMat;
		neighborOrder = sortedDists;
		
		//set the parameters
		neighborhoodSize = (int)sol.getValue(0);
		distanceWeightingPower = sol.getValue(1);
		zeroDistanceSubValue = sol.getValue(2);
		params = sol;

		//set up the inverse lookup for the training examples
		int[] trainExs = data.getTrainingSet();
		int numRows = distMat.getNumRows();
		trainExsInv = new int[numRows];
		for(int i=0; i<numRows; i++){
			trainExsInv[i] = -1;
		}
		for(int j=0; j<trainExs.length; j++){
			trainExsInv[trainExs[j]] = j;
		}

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
	/**
		does it
	*/
	public void doit() throws Exception{
		if(debug){
			System.out.println(getAlias()+":Firing");
		}
		super.doit();
	}
		
	
	/**
		Does the actual work of making predictions by this model. NOTE:
		This assumes the prediction table is a test table to work in 
		"cached mode." otherwise it simply uses a standard 
		InverseDistanceWeightingModel.
	*/
	protected void makePredictions(PredictionTable predTable){
		
		if(!(predTable instanceof TestTable)){
			InverseDistanceWeightingModel idw = new 
				InverseDistanceWeightingModel(data, params);
			idw.setThreadCount(1);
			idw.setScaleInputs(false);
			//try{
				idw.makePredictions(predTable);	 
			//}catch(Exception e){
			//	System.out.println("IDW: Prediction Failed");
			//}
		}
		
		int[] trainExs = data.getTrainingSet();
		int numTrainRows = data.getNumRows();

		int[] testExs = ((ExampleTable)predTable).getTestingSet();
		int numTestRows = predTable.getNumRows();

		int numOutputs = data.getNumOutputFeatures();
		int[] dataOuts = data.getOutputFeatures();
		int[] predCols = predTable.getPredictionSet();		
		
		int i, j, k, jj;
		
		double[] weights = new double[neighborhoodSize];
		double sumOfWeights;
		double dist, d;
		int neighborIdx;
		for(i = 0; i < numTestRows; i++){
			
			sumOfWeights = 0;
			neighborIdx = 0;
			
			jj = 0;
			for(j = 0; j < neighborhoodSize; j++){
				//find the next nearest neighbor that is in the train set
				do{
					jj++;
					neighborIdx = neighborOrder.getInt(jj, testExs[i]);
				}while(trainExsInv[neighborIdx] < 0);
			
				//find the distance between between the neighbor and the test
				//example	
				dist = distanceMatrix.getDouble(neighborIdx, testExs[i]);
				if(dist < zeroDistanceSubValue){
					dist = zeroDistanceSubValue;
				}

				//calculate the weight
				weights[j] = 1.0/Math.pow(dist, distanceWeightingPower);
				sumOfWeights += weights[j];
				
				//for each prediction, add the weight times the output to
				//the prediction summation
				for(k = 0; k < numOutputs; k++){
					d = (weights[j]
							* data.getDouble(trainExsInv[neighborIdx], dataOuts[k]))
							+ predTable.getDoublePrediction(i, k);
					
					predTable.setDoublePrediction(d, i, k);
				}		
							
			}
			//now, for each output, divide by the sum of the weights
			//to normalize the prediction
			for(k = 0; k<numOutputs; k++){
				d = predTable.getDoublePrediction(i, k);
				d /= sumOfWeights;
				predTable.setDoublePrediction(d, i, k);
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
				"<ul><li>Distance Weighting Power:"+distanceWeightingPower+
				"<li>Neighborhood Size:"+neighborhoodSize+
				"<li>zeroDistanceSubstitution Value:"+zeroDistanceSubValue+
				"</ul>";
	}
	
   public String getModuleName() {
		return "CachedIDWModel";
	}
	public String[] getInputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.table.ExampleTable",
			};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: 
				return "The data table to make predictions for";
			default: return "No such input";
		}
	}
	
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Prediction Data";
			default: return "No such input";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.table.PredictionTable"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: 
				return "";
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
				return "";
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
			
					

			

								
	
