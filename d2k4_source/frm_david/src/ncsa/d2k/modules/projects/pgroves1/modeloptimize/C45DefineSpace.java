package ncsa.d2k.modules.projects.pgroves.modeloptimize;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.optimize.util.*;

/**
	Defines a single objective search space
	for an optimizer trying to minimize the
	error metric, whatever it may be.

	@author pgroves
	*/

public class C45DefineSpace extends DataPrepModule
	{

	//////////////////////
	//d2k Props
	////////////////////

	/*the search space object requires this. if we
	are actually trying to optimize a model's parameters,
	this is the threshold/'good-enough' value to tell
	the optimizer the error is low enough*/

	double convergenceTarget=.01;
	boolean debug=false;
	/////////////////////////
	/// other fields
	////////////////////////

	//boolean firstRun;
	//////////////////////////
	///d2k control methods
	///////////////////////

	public boolean isReady(){
	//	boolean b= firstRun;
	//	if(firstRun)
	//		firstRun=false;
		boolean b=super.isReady();
		if(debug)
			System.out.println(getAlias()+": isReady "+b);
		return b;
	}

	public void beginExecution(){
		//firstRun=true;
		super.beginExecution();
	}

	/////////////////////
	//work methods
	////////////////////
	/*
		does it
	*/
	public void doit() throws Exception{

		pushOutput(getSolutionSpace(), 0);
		if(debug)
			System.out.println(getAlias()+" pushing Space");

	}
	/**
		returns the range objects for the parameters of
		ContinuousDTModelGen
	*/
	public static Range[] getRanges(){
		IntRange[] ranges=new IntRange[1];

		ranges[0]=new IntRange("RecordsPerLeaf",20, 3);
		return ranges;
	}
	/**
		returns a single objective search space object
		(minimizing error is the objective) with the
		ContinuousDTModelGen's input parameters as the
		search space
	*/
	public SOSolutionSpace getSolutionSpace(){
		Range[] ranges=this.getRanges();
		ObjectiveConstraints ocs=this.getObjectiveConstraints();

		return new SOSolutionSpace(ranges, ocs, convergenceTarget);

	}
	/*
		returns an ObjectiveConstraint object for minimizing
		error
	*/
	public static ObjectiveConstraints getObjectiveConstraints(){

		return ObjectiveConstraintsFactory.
					getObjectiveConstraints( "Error", 0, 1);
	}


	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return "<html>  <head>      </head>  <body>    Makes a single objective search space where error minimization is the objective and the biases of C45ModelGenerator are the search space parameters.<br><b>Properties</b>    <ul>      <li>        <b>ConvergenceTarget</b>: the search space object requires this. If we are actually trying to optimize a model's parameters, this is the threshold error value to let the optimizer stop at when reached </li>    </ul>  </body></html>";
	}

   	public String getModuleName() {
		return "C45 Search Space";}
	public String[] getInputTypes(){
		String[] types = {		};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			default: return "No such input";
		}
	}

	public String getInputName(int index) {
		switch(index) {
			default: return "NO SUCH INPUT!";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {"ncsa.d2k.modules.core.optimize.util.SOSolutionSpace"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: return "The Solution Space Object";
			default: return "No such output";
		}
	}
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "C45 Bias Space";
			default: return "NO SUCH OUTPUT!";
		}
	}
	////////////////////////////////
	//D2K Property get/set methods
	///////////////////////////////
	public double  getConvergenceTarget(){
		return convergenceTarget;
	}
	public void setConvergenceTarget(double d){
		convergenceTarget=d;
	}
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
	*/
}







