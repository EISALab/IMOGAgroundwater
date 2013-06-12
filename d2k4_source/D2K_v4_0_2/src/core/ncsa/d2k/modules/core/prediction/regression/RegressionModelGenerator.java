package ncsa.d2k.modules.core.prediction.regression;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.optimize.util.*;
import Jama.*;

/*
	Makes a multivariate linear regression
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

public class RegressionModelGenerator extends ModelGeneratorModule
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

	RegressionModel model;

	//////////////////////////
	///d2k control methods
	///////////////////////

	public boolean isReady(){
		return super.isReady();
	}

	/////////////////////
	//work methods
	////////////////////
	/*
		does it
	*/
	public void doit() throws Exception{
		ExampleTable et=(ExampleTable)pullInput(0);
		model=new RegressionModel(et, false);
		pushOutput(model, 0);
		if(!makeModelAvailable)
			model=null;
	}

	public ModelModule getModel() {
    	return model;
  	}






	////////////////////////////////
	/// ModelGen's D2K Info Methods
	////////////////////////////////


	public String getModuleInfo(){
		return "<html>  <head>      </head>  <body>    Creates multiple, multivariate linear regression models, one for each     output, and encapsulates them in a single model module. Predicting     multiple outputs is thereforelike using any multi-variable prediction model  </body></html>";
	}

   	public String getModuleName() {
		return "Multivariate Regression Model Generator";
	}
	public String[] getInputTypes(){
		String[] types = {"ncsa.d2k.modules.core.datatype.table.basic.ExampleTable"};
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
		String[] types = {"ncsa.d2k.modules.core.prediction.regression.RegressionModel"};
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







