package ncsa.d2k.modules.core.optimize.util.reconstruct;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.optimize.util.SOSolution;
/**

	Assigns a single objective value
	to a solution

	@author pgroves
	*/

public class AssignSO extends DataPrepModule
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

	public void beginExecution(){
		super.beginExecution ();
		totalFires=0;
		return;
	}
	int totalFires=0;
	/////////////////////
	//work methods
	////////////////////
	/*
		does it
	*/
	public void doit() throws Exception{

	double obj=((Double)pullInput(0)).doubleValue();
	SOSolution sol=(SOSolution)pullInput(1);

	sol.setObjective(obj);
	if(debug){
		totalFires++;
		System.out.println("AssignSO totalFires:"+totalFires);
	}
	pushOutput(sol, 0);

	}


	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return "<html>  <head>      </head>  <body>    Assigns an objective value (the double) to a solution object  </body></html>";
	}

   	public String getModuleName() {
		return "Assign Single Objective";
	}
	public String[] getInputTypes(){
		String[] types = {"java.lang.Double","ncsa.d2k.modules.core.optimize.util.SOSolution"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: return "The objective value";
			case 1: return "The solution the assign the objective to";
			default: return "No such input";
		}
	}

	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Objective";
			case 1:
				return "Solution";
			default: return "NO SUCH INPUT!";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {"ncsa.d2k.modules.core.optimize.util.SOSolution"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: return "The solution with the objective now assigned";
			default: return "No such output";
		}
	}
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Evaluated Solution";
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







