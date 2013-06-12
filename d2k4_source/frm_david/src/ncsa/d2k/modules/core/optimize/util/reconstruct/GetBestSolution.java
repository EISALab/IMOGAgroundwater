package ncsa.d2k.modules.core.optimize.util.reconstruct;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.optimize.util.*;
/**
	GetBestSolution<br>

	takes a solution space and pushes out
	the best solution it contains

	@author pgroves
	*/

public class GetBestSolution extends DataPrepModule
	{

	//////////////////////
	//d2k Props
	////////////////////
	protected boolean debug=false;

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
		super.endExecution();
	}
	public void beginExecution(){
		//wipeFields();

		super.endExecution();
	}
	/*private void wipeFields(){
	}*/


	/////////////////////
	//work methods
	////////////////////
	/*
		does it
	*/
	public void doit() throws Exception{
		SOSolutionSpace ss=(SOSolutionSpace)pullInput(0);
		ss.computeStatistics();
		SOSolution sol=(SOSolution)(ss.getSolutions()[ss.getBestSolution()]);
		pushOutput(sol, 0);
		pushOutput(new Double(sol.getObjective()), 1);
		if(debug){
			System.out.println(sol);
		}
	}


	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return "<html>  <head>      </head>  <body>    Takes a solution space (single objective) and pushes out the best solution  </body></html>";
	}

   	public String getModuleName() {
		return "Best Solution Extractor";
	}
	public String[] getInputTypes(){
		String[] types = {"ncsa.d2k.modules.core.optimize.util.SOSolutionSpace"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: return "The solution space with solutions evaluated";
			default: return "No such input";
		}
	}

	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "The Solution Space";
			default: return "NO SUCH INPUT!";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {"ncsa.d2k.modules.core.optimize.util.SOSolution","java.lang.Double"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: return "The best individual solution from the solution space";
			case 1: return "The best individual's objective";
			default: return "No such output";
		}
	}
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Best Solution";
			case 1:
				return "Best Objective";
			default: return "NO SUCH OUTPUT!";
		}
	}
	////////////////////////////////
	//D2K Property get/set methods
	///////////////////////////////
	public boolean getDebug(){
		return debug;
	}
	public void setDebug(boolean b){
		debug=b;
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







