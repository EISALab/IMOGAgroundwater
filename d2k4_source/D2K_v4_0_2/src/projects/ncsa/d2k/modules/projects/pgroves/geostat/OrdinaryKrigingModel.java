package ncsa.d2k.modules.projects.pgroves.geostat;

import ncsa.d2k.modules.PredictionModelModule;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.impl.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

import Jama.*;
import java.text.NumberFormat;


/**
 * An ordinary kriging prediction model. Uses an already defined
 * variogram model, and a radius-based neighborhood selection
 * method to define and solve the system of equations used
 * to determine the neighbor combination weights.


	@author pgroves
	@date 03/22/04
	*/

public class OrdinaryKrigingModel extends PredictionModelModule 
	implements java.io.Serializable {

	//////////////////////
	//d2k Props
	////////////////////
	
	boolean debug=false;		
	/////////////////////////
	/// other fields
	////////////////////////
	
	/** a function for computing gamma(h) = f(h)*/
	BivariateModel variogram;

	/** the sample locations. only the first two inputs and first
	 * outputs are used
	 **/
	ExampleTable trainData;

	/** holds the matrix containing all gamma(x_i - x_j) values at
	 * location (i, j). Subsets of this matrix that represent only
	 * the (i, j) pairs in the local neighborhood of a location
	 * being predicted will be used in the final estimation/prediction
	 */
	Matrix masterMatA;

	/**
	 * the only parameters used in this implementation is a radius representing
	 * the local neighborhood boundary around a point being predicted, the
	 * minimum number of points that must be included in the neighborhood,
	 * and the maximum number of points that may be in the neighborhood.
	 * This must be in the same units of measure as the input features.
	 */
	ParameterPoint parameters;

	//these are pointers into the ParameterPoint above for the 
	//different params
	
	private final static int RADIUS_PARAM = 0;
	private final static int MIN_NEIGHBORS_PARAM = 1;
	private final static int MAX_NEIGHBORS_PARAM = 2;

	
	private final static int matPrintWidth = 10;
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

	///////////////////
	//Constructor
	///////////////////
	
	public OrdinaryKrigingModel(ExampleTable tt, ParameterPoint pp, 
			BivariateModel semivariogram){
		
		super(tt);
		this.variogram = semivariogram;
		this.trainData = tt;

		int numTrainExs = this.trainData.getNumRows();

		int numParams = pp.getNumParameters();
		
		double[] paramVals = new double[numParams];
		String[] paramNames = new String[numParams];
		for(int i = 0; i < numParams; i++){
			paramVals[i] = pp.getValue(i);
			paramNames[i] = pp.getName(i);
		}
		
		if(paramVals[MAX_NEIGHBORS_PARAM] > numTrainExs){
			paramVals[MAX_NEIGHBORS_PARAM] = numTrainExs;
		}
		
		if(paramVals[MIN_NEIGHBORS_PARAM] > numTrainExs){
			paramVals[MIN_NEIGHBORS_PARAM] = numTrainExs - 1;
		}
		this.parameters = ParameterPointImpl.getParameterPoint(paramNames,
				paramVals);
		
		this.initMatA();

	}

	/**
	 * creates an n+1 by n+1 matrix of all of the theoretical variogram
	 * values of point pairs in the training data. subsets of this
	 * matrix related to the neighbors of a point under 
	 * consideration will be used in the kriging predictions. This
	 * matrix can be calculated at model generation, and reused
	 * during prediction.
	 */
	
	protected void initMatA(){
		int numExamples = this.trainData.getNumRows();
		int matDim = numExamples + 1;

		int outputCol = trainData.getOutputFeatures()[0];

		double[][] aVals = new double[matDim][matDim];

		int i, j;
		double gamma;
		for(i = 0; i < numExamples; i++){
			for(j = 0; j < numExamples; j++){
				if(i != j){
					gamma = distance(i, trainData, j, trainData);
					gamma = this.variogram.eval(gamma);
					//aVals[i][j] = gamma;
					aVals[i][j] =  -1 * gamma;
				}else{
					aVals[i][j] = 0;
				}
			}
		}
		//the last row and last column should both be filled with 1's
		//(except the vary last position is a 0)
		for(i = 0; i < numExamples; i++){
			aVals[matDim-1][i] = 1;
			aVals[i][matDim-1] = 1;
		}
		this.masterMatA = new Matrix(aVals);
		
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


	/**
	 * standard behaviour.
	 */
	
	public void makePredictions(PredictionTable predTable){
		int i, j;
		/*System.out.println("first Input:"+predTable.getColumnLabel(
			predTable.getInputFeatures()[0]) +
			" second input:" +predTable.getColumnLabel(
			predTable.getInputFeatures()[1])); 
		*/
		int trainSize = trainData.getNumRows();
		int outputCol = trainData.getOutputFeatures()[0];
		int predSize = predTable.getNumRows();
		int predCol = predTable.getPredictionSet()[0];

		/*
		int predXCol = predTable.getInputFeatures()[0];
		int predYCol = predTable.getInputFeatures()[1];
		
		int trainXCol = trainData.getInputFeatures()[0];
		int trainYCol = trainData.getInputFeatures()[1];
		*/
		
		//holds the variogram model predictions between the current
		//prediction point and the training examples
		double[] bVals;
		
		int[] neighbors;
		int neighborhoodSize;
		double xdiff, ydiff, h, gamma;
		double pred;

		if(debug){
			//masterMatA.print(NumberFormat.getInstance(), matPrintWidth );
		}
		for(i = 0; i < predSize; i++){
			
			neighbors = this.findNeighbors(predTable, i);
			neighborhoodSize = neighbors.length;
			bVals = new double[neighborhoodSize + 1];
			bVals[neighborhoodSize] = 1;
	
			for(j = 0; j < neighborhoodSize; j++){
				
				h = distance(i, predTable, neighbors[j], trainData);
				
				/*	
				xdiff = predTable.getDouble(i, predXCol);
				xdiff -= trainData.getDouble(neighbors[j], trainXCol);
				xdiff *= xdiff;

				ydiff = predTable.getDouble(i, predYCol);
				ydiff -= trainData.getDouble(neighbors[j], trainYCol);
				ydiff *= ydiff;
				
				h = Math.sqrt(xdiff + ydiff);
				*/
					
				gamma = this.variogram.eval(h);

				bVals[j] = -1 * gamma;
				//bVals[j] = gamma;
			}

			Matrix matB = new Matrix(bVals, bVals.length);

			int[] matASubset = new int[neighborhoodSize + 1];
			System.arraycopy(neighbors, 0, matASubset, 0, neighborhoodSize);
			matASubset[neighborhoodSize] = this.masterMatA.getRowDimension() - 1;

			Matrix matA = this.masterMatA.getMatrix(matASubset, matASubset);
			if(debug){
				System.out.println("Matrix A Subset:\n");
				matA.print(NumberFormat.getInstance(), matPrintWidth);
				System.out.println("\n Matrix B:\n");
				matB.print(NumberFormat.getInstance(), matPrintWidth);

			}
			
			Matrix matX = matA.solve(matB);
			
			if(debug){
				System.out.println("\n Matrix X (solved):\n");
				matX.print(NumberFormat.getInstance(), matPrintWidth);
				System.out.println();

			}
			

			//these are now the weights to use in the weighted
			//sum of the values of the output features that will
			//become our prediction
			double[] weights = matX.getColumnPackedCopy();
			pred = 0;
			for(j = 0; j < neighborhoodSize; j++){
				pred += weights[j] * trainData.getDouble(neighbors[j], outputCol);
			}
			predTable.setDouble(pred, i, predCol);
		}
	}
	
	/**
	 * calculates the distance between two examples in the 
	 * two tables. the distance is the euclidean distance
	 * between the input features. assumes there are only x and y inputs
	 *
	 * @param exampleIdx1 the row index in the first table
	 * @param table1 the table containing the first example
	 * @param exampleIdx2 the row index in the second table
	 * @param table2 the table containing the second example
	 *
	 * @return the euclidean distance between input features
	 */
	protected double distance(int exampleIdx1, ExampleTable table1,
		int exampleIdx2, ExampleTable table2){
		
		double xdiff, ydiff;
		xdiff = table1.getDouble(exampleIdx1, table1.getInputFeatures()[0]);
		xdiff -= table2.getDouble(exampleIdx2, table2.getInputFeatures()[0]);
		xdiff *= xdiff;

		ydiff = table1.getDouble(exampleIdx1, table1.getInputFeatures()[1]);
		ydiff -= table2.getDouble(exampleIdx2, table2.getInputFeatures()[1]);
		ydiff *= ydiff;

		return Math.sqrt(xdiff + ydiff);
	}

	/**
	 * finds the training data neighbors of a location in another table. 
	 * uses the global parameters
	 * to determine how to do that. currently, uses a radius method.
	 */

	protected int[] findNeighbors(ExampleTable table, int exampleIdx){

		double distThresh = parameters.getValue(RADIUS_PARAM);
		int minNumNeighs = (int)parameters.getValue(MIN_NEIGHBORS_PARAM);
		int maxNumNeighs = (int)parameters.getValue(MAX_NEIGHBORS_PARAM);

		int numTrainExamples = trainData.getNumRows();
		
		double[] localDists = new double[numTrainExamples];
		int[] neighborIdxs = new int[numTrainExamples];
		
		for(int i = 0; i < numTrainExamples; i++){
			localDists[i] = distance(i, trainData, exampleIdx, table);
			neighborIdxs[i] = i;
		}
		
		MutableTableImpl tbl = new MutableTableImpl(2);
		tbl.setColumn(new DoubleColumn(localDists), 0);
		tbl.setColumn(new IntColumn(neighborIdxs), 1);
		tbl.sortByColumn(0);
		
		int j = 0;
		double lastDist = 0;
		int[] neighs = new int[numTrainExamples];
		//note that the distThresh condition will always put
		//one too many examples in the set of neighbors, so
		//the other conditions are made to also put
		//one too many in, which will then be removed
		//afterwards
		while( (j <= minNumNeighs) ||
				/*((j <= maxNumNeighs) &&*/
				((j < maxNumNeighs) &&
				 (lastDist < distThresh))){
			neighs[j] = tbl.getInt(j, 1);
			lastDist = tbl.getDouble(j, 0);
			j++;
		}
		if(j != maxNumNeighs){
			j--;
		}
		int[] neighArray = new int[j];
		System.arraycopy(neighs, 0, neighArray, 0, j);
		if(debug){
			System.out.println("num neighs:" + neighArray.length);
		}

		return neighArray;
	}

	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return 	
			"Performs Ordinary Kriging as a prediction method. There must"+
			" therefore be exactly two inputs (x location and y location) "+
			" and one output. The training data had the following properties:"+
			"<br><br>" + 
			this.getTrainingInfoHtml();
	}
	
   public String getModuleName() {
		return "OrdinaryKrigingModel";
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
			
					

			

								
	
