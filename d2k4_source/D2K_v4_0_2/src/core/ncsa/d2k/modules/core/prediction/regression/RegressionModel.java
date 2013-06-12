package ncsa.d2k.modules.core.prediction.regression;

import ncsa.d2k.modules.PredictionModelModule;
import ncsa.d2k.modules.core.datatype.table.*;
//import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.optimize.util.*;

import Jama.*;

/**
	A multivariate linear regression
	model. Will predict multiple outputs but
	really makes a separate model for each, but this
	fact is transparent to the user.
	Based on the simple case of solving
	a Full-Rank Linear Least-Squares Problem
	with the Normal Equations as described in:

	<br>
	Gill, Murray, and Wright. <b>Numerical Linear Algebra and Optimization
	Volume 1</b>, Addison-Wesley Publishing Company, 1991. pg 223.

	<br><br>
	The Jama jar file of Linear Algebra methods is required. It is
	located at:<br>
	<a href="http://math.nist.gov/javanumerics/jama/">
	http://math.nist.gov/javanumerics/jama/</a>

	@author pgroves
	*/


	//////////////////////////////////////////////////////////////
	//// The model
	//////////////////////////////////////////////////////////////

	public class RegressionModel extends PredictionModelModule {

		////////////////////
		//model's fields
		////////////////////
		double[][] coefficients;
		String[] outputNames;

		//iff the regression fails, the model will simply return
		//the mean
		boolean predictMean;

		double mean[];

		/////////////////////////
		//model's methods
		////////////////////////
		public RegressionModel(ExampleTable et){
			this(et, false);
		}

		/*
			Constructor

		*/
		public RegressionModel(ExampleTable et, boolean useMean){
                        super(et);
			predictMean=useMean;
			ExampleTable tt = (ExampleTable)et.getTrainTable();

			int numRows=tt.getNumRows();
			int numOuts=tt.getNumOutputFeatures();
			int numIns=tt.getNumInputFeatures();

			setName("Regression");
			coefficients=new double[numOuts][];
			outputNames=new String[numOuts];
			for(int i=0; i<numOuts; i++){
				outputNames[i]=tt.getColumnLabel(tt.getOutputFeatures()[i]);
			}

			//get the main matrix (input features)
			//make sure to make room for a constant
			if(!predictMean){
			try{

			Matrix matA=new Matrix(numRows, numIns+1);

			for(int i=0;i<numRows; i++){//row
				matA.set(i, 0, 1.0);//the constant
				for(int j=0;j<numIns; j++){//column
					matA.set(i, j+1, tt.getDouble(i,tt.getInputFeatures()[j]));
												//TrainInputDouble(i, j));
				}
			}

			Matrix matATA=(matA.transpose()).times(matA);
			Matrix matR=matATA.chol().getL();


			//assign the output being considered to matrix b,
			//compile the results from solving the regression
			//for each particular b
			for(int oi=0; oi<numOuts; oi++){

				//get the output/predictions matrix
				Matrix matb=new Matrix(numRows, 1);
				for(int i=0;i<numRows;i++){
					matb.set(i, 0, tt.getDouble(i, tt.getOutputFeatures()[oi]));
								//getTrainOutputDouble(i,oi));
				}
				coefficients[oi]=regression(matA, matb, matATA, matR);
			}
			}
			catch (Exception exc){
				/*
				//let's just leave everything the way it is (all coefficients 0)
				//and return

				for(int i=0;i<coefficients.length;i++){
					coefficients[i]=new double[et.getNumInputFeatures()+1];
				}
				*/


				//return the mean if it fails
				//System.out.println("reg failed, using mean: ins:"+
				//	numIns+" rows:"+numRows);
				predictMean=true;
			}
			}
			if(predictMean){
				mean=new double[numOuts];
				for(int j=0;j<numOuts; j++){//column
					for(int i=0; i<numRows; i++){
						mean[j]+=tt.getDouble(i,tt.getOutputFeatures()[j]);
									// ((ExampleTableImpl)et).getTrainOutputDouble(i,j);
					}
					mean[j]/=numRows;
				}
			}


		}

		/*
			finds the coefficients for a single output
		*/
		private double[] regression(Matrix matA, Matrix matb, Matrix ATA, Matrix matR){
			Matrix matATb=(matA.transpose()).times(matb);
			Matrix matY=matR.solve(matATb);
			Matrix matX=(matR.transpose()).solve(matY);
			/*if(debug){
				System.out.println("Mat X:");
				matX.print(8, 5);
			}*/
			double[][] arr=matX.getArray();
			double[] coef=new double[matA.getColumnDimension()];
			for(int i=0;i<coef.length;i++){
				coef[i]=arr[i][0];
			}
			return coef;
		}


	/**********************************
		PREDICT
		*******************************/


/*	public PredictionTable predict(ExampleTable et){


		PredictionTable predTable;
		if(et instanceof PredictionTable){
			predTable=(PredictionTable)et;
		}else{
			predTable= et.toPredictionTable();
		}

		//if there are no spots for pred columns
		if(predTable.getNumOutputFeatures()==0){
			for(int i=0; i<outputNames.length; i++){
				//DoubleColumn dc=new DoubleColumn(et.getNumRows());
				//dc.setLabel(outputNames[i]);
				predTable.addPredictionColumn(new double[et.getNumRows()],outputNames[i]);
			}
		}
		int exampleCount=predTable.getNumRows();
		for(int e=0; e<exampleCount; e++){
			for(int oi=0; oi<outputNames.length; oi++){
				double sum;
				if(!predictMean){
					sum=0.0;
					sum+=coefficients[oi][0];
					for(int i=0; i<et.getNumInputFeatures();i++){
						sum+=et.getDouble(e, et.getInputFeatures()[i])*
									coefficients[oi][i+1];
					}
					//System.out.println("Reg:"+outputNames[oi]);
				}else{
					sum=mean[oi];
				}
				predTable.setDoublePrediction(sum, e, oi);

			}
		}
		return predTable;
	}*/

        protected void makePredictions(PredictionTable predTable){


/*                PredictionTable predTable;
                if(et instanceof PredictionTable){
                        predTable=(PredictionTable)et;
                }else{
                        predTable= et.toPredictionTable();
                }

                //if there are no spots for pred columns
                if(predTable.getNumOutputFeatures()==0){
                        for(int i=0; i<outputNames.length; i++){
                                //DoubleColumn dc=new DoubleColumn(et.getNumRows());
                                //dc.setLabel(outputNames[i]);
                                predTable.addPredictionColumn(new double[et.getNumRows()],outputNames[i]);
                        }
                }*/

                int exampleCount=predTable.getNumRows();
                for(int e=0; e<exampleCount; e++){
                        for(int oi=0; oi<outputNames.length; oi++){
                                double sum;
                                if(!predictMean){
                                        sum=0.0;
                                        sum+=coefficients[oi][0];
                                        for(int i=0; i<predTable.getNumInputFeatures();i++){
                                                sum+=predTable.getDouble(e, predTable.getInputFeatures()[i])*
                                                                        coefficients[oi][i+1];
                                        }
                                        //System.out.println("Reg:"+outputNames[oi]);
                                }else{
                                        sum=mean[oi];
                                }
                                predTable.setDoublePrediction(sum, e, oi);

                        }
                }
//                return predTable;
        }

        protected void makePrediction(ExampleTable example, int row, double [] predictedOutputs) {
        }


	public double predictRow(ExampleTable et, int row, int outputColumn){
	//	System.out.println("outputCol:"+outputColumn);
		double sum=0.0;
		if(!predictMean){
			sum=0.0;
			sum+=coefficients[outputColumn][0];
			for(int i=0; i<et.getNumInputFeatures();i++){
				sum+=et.getDouble(row, et.getInputFeatures()[i])*
							coefficients[outputColumn][i+1];
			}
		}else{
			sum=mean[outputColumn];
		}
		return sum;
	}


	/* just calls predict on the pulled in table*/
  	 public void doit() throws Exception {
		 ExampleTable et=(ExampleTable)pullInput(0);
		 pushOutput(predict(et), 0);

  	 }
		////////////////////////////
		///model's d2k info methods
		///////////////////////////
		public String getModuleInfo(){
			return "Multi-variable in, Multi-variable out prediction"+
					" model based on linear regression";
		}


	   	public String getModuleName() {
			return "RegressionModel";
		}
		public String[] getInputTypes(){
			String[] s= {"ncsa.d2k.modules.core.datatype.table.basic.ExampleTable"};
			return s;
		}

		public String getInputInfo(int index){
			switch (index){
				case(0): {
					return "The input data set to make the predictions on";
				}
				default:{
					return "No such input.";
				}
			}
		}


		public String getInputName(int index) {
			switch (index){
				case(0): {
					return "Prediction Input Data";
				}
				default:{
					return "No such input.";
				}
			}
		}
		public String[] getOutputTypes(){
			String[] s={"ncsa.d2k.modules.core.datatype.table.basic.PredictionTable"};
			return s;
		}

		public String getOutputInfo(int index){
			switch (index){
				case(0): {
					return "The input ExampleTable with the prediction"
					+" columns filled in";
				}
				default:{
					return "No such output.";
				}
			}
		}
		public String getOutputName(int index) {
			switch (index){
				case(0): {
					return "Table w/ Predictions";
				}
				default:{
					return "No such output.";
				}
			}
		}
	}


