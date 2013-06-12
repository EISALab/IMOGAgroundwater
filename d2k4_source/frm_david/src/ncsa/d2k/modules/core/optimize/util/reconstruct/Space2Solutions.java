package ncsa.d2k.modules.core.optimize.util.reconstruct;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.optimize.util.*;
/**
	Space2Solutions<br>

	takes a solution space and pushes out each solution
	it contains

	@author pgroves
	*/

public class Space2Solutions extends DataPrepModule
	{

	//////////////////////
	//d2k Props
	////////////////////


	/////////////////////////
	/// other fields
	////////////////////////
	Solution[] sols;
	int counter=0;

	//////////////////////////
	///d2k control methods
	///////////////////////

	public boolean isReady(){
		if(counter==0)
			return super.isReady();
		else
			return true;
	}

	public void endExecution(){
		return;
	}
	public void beginExecution(){
		wipeFields();

		return;
	}
	private void wipeFields(){
		counter=0;
		sols=null;
	}


	/////////////////////
	//work methods
	////////////////////
	/*
		does it
	*/
	public void doit() throws Exception{
		if(counter==0)	{
			SolutionSpace ss=(SolutionSpace)pullInput(0);
			sols=ss.getSolutions();
		}

		pushOutput(sols[counter], 0);
		counter++;
		if(counter==sols.length)
			wipeFields();
	}


	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return "<html>  <head>      </head>  <body>    Takes a solution space and pushes out every solution (parameter set) it     contains so that they can be evaluated  </body></html>";
	}

   	public String getModuleName() {
		return "Solution Extractor";
	}
	public String[] getInputTypes(){
		String[] types = {"ncsa.d2k.modules.core.optimize.util.SolutionSpace"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: return "The solution space with solution to evaluate";
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
		String[] types = {"ncsa.d2k.modules.core.optimize.util.Solution"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: return "An individual solution from the solution space";
			default: return "No such output";
		}
	}
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Individual Solution";
			default: return "NO SUCH OUTPUT!";
		}
	}
	////////////////////////////////
	//D2K Property get/set methods
	///////////////////////////////

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







