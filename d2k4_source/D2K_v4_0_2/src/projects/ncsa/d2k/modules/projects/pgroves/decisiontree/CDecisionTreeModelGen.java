package ncsa.d2k.modules.projects.pgroves.decisiontree;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.prediction.regression.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.PredictionModelModule;
import java.util.Vector;

import ncsa.d2k.modules.projects.pgroves.stats.NonParametricStats;

/**
	@author pgroves
	*/

public class CDecisionTreeModelGen extends /*OrderedReentrantModule /*/ ModelGeneratorModule
	/*, Reentrant*/{

	//////////////////////
	//d2k Props
	////////////////////
	boolean debug = false;

	boolean makeModelAvailable = false;
	/////////////////////////
	/// other fields
	////////////////////////

	transient CDTModel model;

	//////////////////////////
	///d2k control methods
	///////////////////////

	public boolean isReady(){
		return super.isReady();
	}
	public void endExecution(){
		super.endExecution();
	}
	public void beginExecution(){
		super.beginExecution();
	}

	/////////////////////
	//work methods
	////////////////////

	/**
		does it
	*/
	public void doit() throws Exception{
		//System.out.println(getAlias()+"firing");
		ExampleTable et=(ExampleTable)pullInput(0);
		ParameterPoint ds = (ParameterPoint)pullInput(1);

		CDTModel cmod= new CDTModel(et, ds);
		pushOutput(cmod, 0);
		if(makeModelAvailable)
			model=cmod;

	}

	public ModelModule getModel() {
    	return model;
  	}

	//////////////////////////////////////////////////////////////
	//// The model
	//////////////////////////////////////////////////////////////

	public class CDTModel extends PredictionModelModule
	{

		////////////////////
		//model's fields
		////////////////////
		/*String[] outputNames;*/

		double[] params;

		public final int MIN_EXAMPLES_PER_NODE=0;
		public final int IMPROVEMENT_THRESHOLD=1;
		public final int ALL_INPUTS_REGRESSION=2;
		public final int NUM_QUANTILES_CONSIDERED = 3;

		node[] roots;

		/*which of the output Columns this dt should be dealing with*/
		//int outputColumn=0;;

		/////////////////////////
		//model's methods
		////////////////////////

		/*
			Constructor

		*/
		public CDTModel(ExampleTable et, ParameterPoint ds){
			super(et);
			System.out.println("NumInputs:" + et.getNumInputFeatures());
			System.out.println("Total Rows:"+et.getNumRows());
			ExampleTable tt=(ExampleTable)et.getTrainTable();
			//System.out.println(" Train Table Rows:"+tt.getNumRows()+
			//" trainsize:"+tt.getTrainingSet().length);
			//double[] inputParams=(double[])ds.getParameters();
			params=new double[ds.getNumParameters()];//inputParams.length];
			for(int i = 0; i < params.length; i++){
				 params[i]=ds.getValue(i);//inputParams[i];
			}
			int numTrees=tt.getNumOutputFeatures();
			roots=new node[numTrees];
			int numRows=tt.getNumRows();
			double rawThresh=params[IMPROVEMENT_THRESHOLD];

			int[] allTrainingExamples=new int[numRows];
			for(int i=0;i<numRows; i++){
				allTrainingExamples[i]=i;
			}
			for(int r=0;r<numTrees;r++){
				double max=0;
				double d;
				for(int i=0; i<numRows; i++){
					d=tt.getDouble(i,tt.getOutputFeatures()[r]);
					if(d>max){
						max=d;
					}
				}

				//params[IMPROVEMENT_THRESHOLD]=rawThresh*max;
				roots[r]=new node(tt, allTrainingExamples,
									null,tt.getOutputFeatures()[r]);
				//params[IMPROVEMENT_THRESHOLD]/=max;

			}

			setName("CDT");
			//if(debug)
			//	System.out.println("trainset length:"+
			//		et.getTrainingSet().length);

		}

	/**********************************
		PREDICT
		*******************************/


	public void makePredictions(PredictionTable predTable){


		//put in something that puts the predictions in here
		int exampleCount=predTable.getNumRows();
		if(debug){
			System.out.println("PredictionSet length:"+exampleCount);
		}
		double d;
		int oc;
		for(int e=0; e<exampleCount; e++){
			for(oc=0;oc<this.getOutputColumnLabels().length;oc++){
				d=roots[oc].evaluate(predTable, e);
				predTable.setDoublePrediction(d, e,oc);
			}
		}
	}



	 private class node implements java.io.Serializable{
		/*if this is a leaf, we'll make a regression model*/
		RegressionModel rgmodel;

		/*if not a leaf, this is the Column index to split on*/
		int splitColumn;

		/*this is what value (the mean) to split on in the splitColumn*/
		double splitValue;

		/*what the score of this split is, so the children can see if 
		they're improving*/
		double splitScore;

		/*the chilun'*/
		node leftChild;
		node rightChild;

		boolean isLeaf;
		boolean isRoot;

		/*which of the output Columns this node should be dealing with.
		 this is an index over all columns of the table, not just outputs*/
		int outputColumn;

		/*of the Input Columns, which this one uses*/
		public int splitColumnIndex;

		/*the table to use during training, will be released when training
		completes*/
		ExampleTable data;

		node parent;


		int[] finalInputCols;

		public node(ExampleTable tt, int[] trainExamples, node prt,int outCol){
			//outputColumnIndex=outColI;
			outputColumn=outCol;
			data=tt;
			if(prt==null){
				this.isRoot=true;
			}else{
				this.isRoot=false;
				parent=prt;
			}
			isLeaf=false;

			int splitColumn=0;
			//double bestScore=1.0;
			splitScore=1.0;
			int[] leftExamples=new int[0];
			int[] rightExamples=new int[0];
			//leftExamples=[0], right=[1];
			int[][] holdExampleSplits=new int[2][];

			//score=[0], splitValue(mean)=[1]
			double[] holdSplitInfo=new double[2];

			if(trainExamples.length>
					(params[MIN_EXAMPLES_PER_NODE]*data.getNumRows())){
				int[] inputs=data.getInputFeatures();
				//NumericColumn output=
				//	(NumericColumn)data.getColumn(data.
				//getOutputFeatures()[outputColumn]);

				int inputCount=inputs.length;

				//initiallize the "best" scores
				scoreInput( inputs[0], trainExamples, holdExampleSplits,
								 holdSplitInfo);

				splitColumnIndex=0;
				splitColumn=inputs[0];
				splitValue=holdSplitInfo[1];
				splitScore=holdSplitInfo[0];
				leftExamples=holdExampleSplits[0];
				rightExamples=holdExampleSplits[1];


				for(int i=1; i<inputCount; i++){
					scoreInput( inputs[i], trainExamples, holdExampleSplits,
									holdSplitInfo);
					if(holdSplitInfo[0]<splitScore){
						splitColumnIndex=i;
						splitColumn=inputs[i];
						splitValue=holdSplitInfo[1];
						splitScore=holdSplitInfo[0];
						leftExamples=holdExampleSplits[0];
						rightExamples=holdExampleSplits[1];
					}
				}
				double diff;
				if(!isRoot){
					diff=parent.getSplitScore()-splitScore;
					
					if((diff / parent.getSplitScore()) < 
						params[IMPROVEMENT_THRESHOLD]){
						isLeaf=true;
						if(debug)
							System.out.println("IsLeaf-thresh:"+diff+
							","+splitScore+","+parent.getSplitScore());
					}
				}
				//System.out.println("Split at Column:" + splitColumn + "-" +
				//data.getColumnLabel(splitColumn) + " value:" + splitValue);

				if(debug){
					System.out.println("Split at Column:" + splitColumn + "-" +
					data.getColumnLabel(splitColumn) + " value:" + splitValue);
				}
			
				//}else{
				//	diff=splitScore;
				//}
				
				/*if(diff<params[IMPROVEMENT_THRESHOLD]){
						isLeaf=true;
						if(debug)
							System.out.println("IsLeaf-thresh:"+diff+
							","+splitScore+","+parent.getSplitScore());
					}

				if(debug)
					System.out.println(splitColumn+" "+splitValue+" "
					+splitScore+" "+trainExamples.length);*/
			}else{
				isLeaf=true;
				if(debug)
					System.out.println("IsLeaf-pop:"+trainExamples.length);
			}
			if(isLeaf){
				 
				//System.out.println(finalInputCols.length+"/"+oldInCols.length);
				int[] singleOutput=new int[1];
				int [] oldOutputs=data.getOutputFeatures();
				singleOutput[0]=outputColumn;

				int[] oldTrainingSet=data.getTrainingSet();
				int[] newTrainingSet=new int[trainExamples.length];
				for(int i=0;i<newTrainingSet.length;i++){
					newTrainingSet[i]=oldTrainingSet[trainExamples[i]];
				}
				data.setTrainingSet(newTrainingSet);

				//save the old input cols
				int[] oldInCols=data.getInputFeatures();
				
				if(params[ALL_INPUTS_REGRESSION]<0.001){
					//find the attributes that have been used to get here
					node n=this.parent;
					Vector cols=new Vector();
					cols.add(new Integer(n.splitColumn));

					while(!n.isRoot){
						n=n.parent;
						cols.add(new Integer(n.splitColumn));
					}
					finalInputCols=new int[cols.size()];
					for(int i=0; i<finalInputCols.length; i++){
						finalInputCols[i]=((Integer)cols.get(i)).intValue();
					}
					 
					data.setInputFeatures(finalInputCols);
				}
				data.setOutputFeatures(singleOutput);

				rgmodel=new RegressionModel(data, false);

				data.setTrainingSet(oldTrainingSet);
				if(params[ALL_INPUTS_REGRESSION]<0.001){
					data.setInputFeatures(oldInCols);
				}
				data.setOutputFeatures(oldOutputs);
				//System.out.println("leaf");
			}else{//if we got this far, we're going to split
					leftChild=new node(data, leftExamples,
									this, outputColumn);
					rightChild=new node(data, rightExamples,
									this, outputColumn);
			}
			if(isRoot){
				if(debug)
					System.out.println("root done");
			}
			data=null;
		}

		protected double evaluate(ExampleTable et, int row){
			if(!isLeaf){
				if(et.getDouble(row, et.getInputFeatures()[splitColumnIndex])
								<splitValue){
					return leftChild.evaluate(et, row);
				}else{
					return rightChild.evaluate(et, row);
				}
			}else{
				int[] oldInCols=et.getInputFeatures();

				if(params[ALL_INPUTS_REGRESSION]<0.001){
					et.setInputFeatures(finalInputCols);
				}
				double pred=rgmodel.predictRow(et, row, 0/*outputColumn*/);
				if(params[ALL_INPUTS_REGRESSION]<0.001){
					et.setInputFeatures(oldInCols);
				}
				return pred;
			}
		}

		private void scoreInput(int inputC,	int[] trainExamples,
								int[][] splitExamples, double[] splitInfo){

			if(debug){
				System.out.println("\n\n InputCol:"+ inputC);
			}
			int i, j, k;
			double[] splitValues = computeSplitValues(inputC, trainExamples);
			double splitValue;
			double bestInputScore = Double.POSITIVE_INFINITY;
			
			for(j = 0; j < splitValues.length; j++){
			 
			splitValue = splitValues[j]; 
			if(debug){
				System.out.println("\n QuantSplit:" + j +
				" inputCol:" + inputC + " QuantVal:" + splitValue);
			}
			//get the counts of the split so we don't have to use a Vector
			int leftCount=0;
			int rightCount=0;
			for(i=0; i<trainExamples.length; i++){
				if(data.getDouble(trainExamples[i],inputC) > splitValue){
					rightCount++;
				}else{
					leftCount++;
				}
			}
			int[] leftExamples=new int[leftCount];
			int leftIndex=0;
			int[] rightExamples=new int[rightCount];
			int rightIndex=0;
			for( i=0; i<trainExamples.length; i++){
				if(data.getDouble(trainExamples[i],inputC)>splitValue){
					rightExamples[rightIndex]=trainExamples[i];
					rightIndex++;
				}else{
					leftExamples[leftIndex]=trainExamples[i];
					leftIndex++;
				}
			}

			double leftScore=computeScore(leftExamples);
			double rightScore=computeScore(rightExamples);

			//weighted average of the scores based on how many examples
			//they represent
			double totalScore=(leftScore/**leftCount*/+
								rightScore/**rightCount*/)/
								(leftCount+rightCount);
			if(debug)
				System.out.println("\tleftScore:"+leftScore+
					"\n\t rightScore"+rightScore+"\n\t Total:"+totalScore);

			if(totalScore < bestInputScore){	
				bestInputScore = totalScore;
				splitExamples[0]=leftExamples;
				splitExamples[1]=rightExamples;

				splitInfo[0]=totalScore;
				splitInfo[1]=splitValue;
			}
			}//for(j = 0 ...

		}



		/* for now, just computes the mean, override this if you want
			it to split on the median or something fancy/
		protected double computeSplitValue(int inputC, int[] trainExamples){
			double mean=0.0;
			for(int i=0; i<trainExamples.length; i++){
				/*System.out.println(i+","+trainExamples[i]+"::"+
					data.getTrainingSet()[trainExamples[i]]);
				System.out.println(((TrainTableImpl)((TrainTableImpl)data).
						getExampleTable()).getExampleTable().getNumRows());

				int[] ins=data.getInputFeatures();
				for(int j=0;j<ins.length;j++){
					System.out.println("   "+ins[j]);
				}/
				mean+=data.getDouble(trainExamples[i],inputC);
				//System.out.println("..");
			}
			mean/=trainExamples.length;
			return mean;
		}*/

		/**
		The split values are the quantiles
		*/
		protected double[] computeSplitValues(int inputC, int[] trainExamples){
			SubsetTableImpl st= new SubsetTableImpl((TableImpl)data, 
				trainExamples);
			int numQuants = (int)params[NUM_QUANTILES_CONSIDERED];
			if(numQuants > (trainExamples.length - 2)){
				numQuants = trainExamples.length - 2;
			}
			double[] quantiles = NonParametricStats.findQuantiles(st, 
				inputC, numQuants);
			double[] noRangeQuants = new double[quantiles.length - 2];
			for(int i = 1; i < quantiles.length - 1; i++){
				noRangeQuants[i - 1] = quantiles[i];
			}
			return noRangeQuants;
		}


		/* this computes the variance of the output feature after the
		proposed split*/
		protected double computeScore(int[] examples){

			//find the mean
			double mean=0.0;
			int numExamples=examples.length;
			for(int i=0; i<numExamples; i++){
				mean+=data.getDouble(examples[i],outputColumn);
			}
			mean/=numExamples;

			double var=0.0;
			double d;
			for(int i=0; i<numExamples; i++){
				d=data.getDouble(examples[i],outputColumn)-mean;
				var+=d*d;
			}
			//var/=numExamples;
			return var;
		}

		protected double getSplitScore(){
			return  splitScore;
		}

	}

		////////////////////////////
		///model's d2k info methods
		///////////////////////////
		public String getModuleInfo(){
			StringBuffer sb=new StringBuffer();
			sb.append("A decision tree for predicting continuous variables");
			sb.append(" (aka a Model Tree). The best split is based on gre");
			sb.append("atest reduction in the output's variance. A linear");
			sb.append(" regression is done at the nodes to provide the final ");
			sb.append("predictions. One distinct tree is created for each");
			sb.append(" output column of the data set.");
			sb.append("This tree predicts the following:<ol>");
			sb.append(this.getTrainingInfoHtml());
			return sb.toString();
		}


	   	public String getModuleName() {
			return "Regression Tree";
		}

	}






	////////////////////////////////
	/// ModelGen's D2K Info Methods
	////////////////////////////////


	public String getModuleInfo(){
		return " A decision tree for predicting continuous variables (aka a"+
		" Model Tree).     The best split is based on greatest reduction in "+
		"the output's variance. A     linear regression is done at the nodes "+
		" to provide the final predictions.     One distinct tree is created "+
		"for each output column of the data set.";
	}

   	public String getModuleName() {
		return "Regression Tree Generator";
	}
	public String[] getInputTypes(){
		String[] types = {	"ncsa.d2k.modules.core.datatype.table.ExampleTable",
							"ncsa.d2k.modules.core.datatype.parameter.ParameterPoint"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: return 
			"The training data set. Only row indices indicated by the trainingSet"+
			" will be considered during model generation";
			case 1: return "The Parameter object which contains the parameters "+
			"or biases for generating the model";
			default: return "No such input";
		}
	}

	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Training Data";
			case 1:
				return "Parameters";
			default: return "NO SUCH INPUT!";
		}
	}

	public String[] getOutputTypes(){
		String[] types = {"ncsa.d2k.modules.PredictionModelModule"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: return "The model that was produced";
			default: return "No such output";
		}
	}
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Continuous Prediction Decision Tree Model";
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
	public boolean getMakeModelAvailable(){
		return makeModelAvailable;
	}
	public void setMakeModelAvailable(boolean b){
		makeModelAvailable=b;
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
	*/
}







