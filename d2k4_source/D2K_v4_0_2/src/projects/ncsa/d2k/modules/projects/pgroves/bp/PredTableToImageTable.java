package ncsa.d2k.modules.projects.pgroves.bp;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

/**
	takes in the table created by CreateImagePredTable after the predictions
	have been filled in and creates a table indexed like an image out
	of the predictions.


	@author pgroves
	@date 02/13/04
	*/

public class PredTableToImageTable extends DataPrepModule 
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

		PredictionTable pt = (PredictionTable)pullInput(0);
		int imCols = ((Integer)pullInput(1)).intValue();

		int imRows = pt.getNumRows()/imCols;
		
		if(debug){
			System.out.println(getAlias() + " imcols:" + imCols +
			" imrows:" + imRows + " predtable rows:"+ pt.getNumRows());
		}

		//double[] imgVals;
		//imgVals = (double[])pt.getColumn(pt.getPredictionSet()[0]).getInternal();

		MutableTableImpl img = new MutableTableImpl(imCols);
		int i, j, k;
		k = 0;
		for(i = 0; i < imCols; i++){
			double[] colDat = new double[imRows];
			img.setColumn(new DoubleColumn(colDat), i);
		}
		double d;
		k = 0;
		for(j = 0; j < imRows; j++){
			for(i = 0; i < imCols; i++){
				d = pt.getDoublePrediction(k, 0);
				img.setDouble(d, j, i); 
				//colDat[j] = pt.getDoublePrediction(j * imRows + i, 0);
				k++;
			}
		}
		
		pushOutput(img, 0);
	}
		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return 	
			"Takes in the table created by CreateImagePredTable after the "+
			"predictions have been filled in and creates an Image Table  out"+
			" of it.";
	}
	
  	public String getModuleName() {
		return "PredTableToImageTable";
	}
	public String[] getInputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.table.PredictionTable",
			"java.lang.Integer"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: 
				return "The PredictionTAble from CreateImagePredTable after"+
				" the predictions have been made and filled in";
			case 1: 
				return 
					"The number of columns pushed out from CreateImagePredTable.";

			default: return "No such input";
		}
	}
	
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Image Prediction Table";
			case 1:
				return "NumColumns";

			default: return "No such input";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.table.basic.MutableTableImpl"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: 
				return "A grayscale image (with double values) of the predictions";
			default: return "No such output";
		}
	}
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Prediction Image";
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
			
					

			

								
	
