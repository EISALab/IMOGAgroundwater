package ncsa.d2k.modules.core.prediction.mean;


import ncsa.d2k.core.modules.ModelGeneratorModule;
import ncsa.d2k.core.modules.ModelModule;
import ncsa.d2k.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.optimize.util.*;
/*
	Makes a model that returns the average
	value of the outputs of the data set it
	was trained on

	@author pgroves
*/

public class MeanModelGenerator extends ModelGeneratorModule
	{

	//////////////////////
	//d2k Props
	////////////////////
	boolean debug=true;

	/*should d2k be allowed to put this model in
	the "Generated Models" window*/
	boolean makeModelAvailable=true;
	/////////////////////////
	/// other fields
	////////////////////////

	MeanModel model;

	//////////////////////////
	///d2k control methods
	///////////////////////

	public boolean isReady(){
		return super.isReady();
	}
	public void beginExecution(){
		return;
	}

	/////////////////////
	//work methods
	////////////////////
	/*
		does it
	*/
	public void doit() throws Exception{
		ExampleTableImpl et=(ExampleTableImpl)pullInput(0);
		model=new MeanModel(et);
		pushOutput(model, 0);
		if(!makeModelAvailable)
			model=null;

	}

	public ModelModule getModel() {
    	return model;
  	}
	//////////////////////////////////////////////////////////////
	//// The model
	//////////////////////////////////////////////////////////////

	public class MeanModel extends PredictionModelModule
					implements java.io.Serializable
	{

		////////////////////
		//model's fields
		////////////////////

		/*holds the averages of the outputs*/
		double[] averages;

		/*holds the names of the outputs this model predicts*/
		String[] outputNames;
		/////////////////////////
		//model's methods
		////////////////////////

		/*
			Constructor

			does the real work, computes the averages
		*/
		public MeanModel(ExampleTableImpl et){
                        super(et);
			averages=new double[et.getNumOutputFeatures()];
			int[] trainSet = et.getTrainingSet();
			int[] outputs = et.getOutputFeatures();

			for(int i=0; i<et.getNumOutputFeatures(); i++){
				for(int e=0; e<et.getNumTrainExamples(); e++){
					averages[i]+=et.getDouble(trainSet[e], outputs[i]);
				}
				averages[i]/=et.getNumTrainExamples();
			}
			outputNames=new String[et.getNumOutputFeatures()];
			for(int i=0; i<outputNames.length;i++){
				outputNames[i]=et.getColumnLabel(et.getOutputFeatures()[i]);
			}


			setName("Mean");
		}

	/**********************************
		PREDICT
		*******************************/


//	public PredictionTable predict(ExampleTable et){
        protected void makePredictions(PredictionTable predTable) {

/*		if(et instanceof TestTable){
			for(int i=0; i<averages.length; i++){
				for(int e=0; e<et.getNumTestExamples(); e++){
					((TestTableImpl)et).setDouble(averages[i], e,
								((TestTable)et).getPredictionSet()[i]);
				}
			}
			return (TestTable)et;
		}

		PredictionTableImpl predTable;
		if(et instanceof PredictionTable){
			predTable=(PredictionTableImpl)et;
		}else{
			predTable=(PredictionTableImpl)et.toPredictionTable();
		}
               */

		//if there are no spots for pred columns
/*		if(predTable.getNumOutputFeatures()==0){
			for(int i=0; i<outputNames.length; i++){
				DoubleColumn dc=new DoubleColumn(predTable.getNumRows());
				dc.setLabel(outputNames[i]);
				predTable.addPredictionColumn(dc);
			}
		}*/
		for(int i=0; i<averages.length; i++){
			for(int e=0; e<predTable.getNumRows(); e++){
				predTable.setDouble(averages[i], e,
									predTable.getPredictionSet()[i]);
			}
		}

		//return predTable;
	}

	/* just calls predict on the pulled in table*/
  	 public void doit() throws Exception {
		 pushOutput(predict((ExampleTable)pullInput(0)), 0);

  	 }
		////////////////////////////
		///model's d2k info methods
		///////////////////////////
		public String getModuleInfo(){
			return "A model based on the mean of any given output/prediction"+
					" feature. Every prediction will simply be the average of"+
					" that feature in the training set, regardless of the "+
					"inputs";
		}


	   	public String getModuleName() {
			return "Mean Model";
		}
		public String[] getInputTypes(){
			String[] s= {"ncsa.d2k.modules.core.datatype.table.basic.ExampleTableImpl"};
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
					return "Prediction Data";
				}
				default:{
					return "No such input.";
				}
			}
		}
		public String[] getOutputTypes(){
			String[] s={"ncsa.d2k.modules.core.datatype.table.basic.PredictionTableImpl"};
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






	////////////////////////////////
	/// ModelGen's D2K Info Methods
	////////////////////////////////


	public String getModuleInfo(){
		return "<html>  <head>      </head>  <body>    Creates a model based on the mean of any given output/prediction feature.     Every prediction will simply be the average of that feature in the     training set, regardless of the inputs  </body></html>";
	}

   	public String getModuleName() {
		return "Mean Model Generator";
	}
	public String[] getInputTypes(){
		String[] types = {"ncsa.d2k.modules.core.datatype.table.basic.ExampleTableImpl"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: return "The training data set. Only row indices indicated by the trainingSet will be considered during model generation";
			default: return "No such input";
		}
	}

	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Training Data";
			default: return "NO SUCH INPUT!";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {"ncsa.d2k.modules.core.prediction.mean.MeanModelGenerator$MeanModel"};
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
				return "The Model";
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







