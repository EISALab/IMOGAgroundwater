package ncsa.d2k.modules.core.optimize.util.reconstruct;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.optimize.util.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
/**
	Solutions2Space<br>

	takes a solution space and puts solutions
	into it


	@author pgroves
	*/

public class Solutions2Space extends DataPrepModule
	{

	//////////////////////
	//d2k Props
	////////////////////
	protected boolean debug=false;

	/////////////////////////
	/// other fields
	////////////////////////

	/*how many solutions in the space, and therefore
	 how many to wait for before pushing the space and
	 waiting for a new one*/
	int totalSolutionCount;

	/*how many of the current space's solutions we've gotten*/
	int currentSolutionCount;

	/*the space we're filling in which will be eventually pushed*/
	SolutionSpace space;

	/*in case solutions are evaluated out of order, we
	don't want to overwrite anything in the space itself
	before it get's here, so keep track of solutions separately
	and put them all in the space at the end*/
	Solution[] solutions;

	//////////////////////////
	///d2k control methods
	///////////////////////

	public boolean isReady(){
		//if we need the space, wait for everything
		if(currentSolutionCount==0){
			return super.isReady();
		}
		//otherwise, just wait for new solutions
		//return(inputFlags[1]>0);
		return(getInputPipeSize(1)>0);
	}
	public void beginExecution(){
		super.beginExecution();
		totalSolutionCount=0;
		currentSolutionCount=0;
		return;
	}

	/////////////////////
	//work methods
	////////////////////
	/*
		does it
	*/
	public void doit() throws Exception{
		if(currentSolutionCount==0){

			space=(SolutionSpace)pullInput(0);

			totalSolutionCount=space.getSolutions().length;
			if(space instanceof SOSolutionSpace){
				solutions=new SOSolution[totalSolutionCount];
			}else{
				solutions=new MOSolution[totalSolutionCount];
			}

		}

		solutions[currentSolutionCount]=(Solution)pullInput(1);
		if(debug)
			System.out.println(solutions[currentSolutionCount]);
		currentSolutionCount++;

		if(currentSolutionCount==totalSolutionCount){
			space.setSolutions(solutions);
			pushOutput(space, 0);
			currentSolutionCount=0;
			if(debug){
				space.computeStatistics();
				//int[] sortedOrder=((SOSolutionSpace)space).sortSolutions();
				//String s1=((SOSolutionSpace)space).getSpaceDefinitionString();
				//String s2=((SOSolutionSpace)space).statusString();
				Table vt=(TableImpl)space.getTable();
			//	VerticalTable vt2=(VerticalTable)vt.reOrderRows(sortedOrder);
				//System.out.println(s1+s2);
				((TableImpl)vt).print();
			}
		}

	}


	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return "<html>  <head>      </head>  <body>    Takes a solution space and solutions (solutions one at a time) and puts     the solutions in the space in the order they come in  </body></html>";
	}

   	public String getModuleName() {
		return "Solution Compiler";
	}
	public String[] getInputTypes(){
		String[] types = {"ncsa.d2k.modules.core.optimize.util.SolutionSpace","ncsa.d2k.modules.core.optimize.util.Solution"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: return "The solution space with no solutions or unevaluated solutions";
			case 1: return "The individual, evaluated solutions";
			default: return "No such input";
		}
	}

	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "The Solution Space";
			case 1:
				return "Evalutated Solution";
			default: return "NO SUCH INPUT!";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {"ncsa.d2k.modules.core.optimize.util.SolutionSpace"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: return "The solution space containing evaluated solutions";
			default: return "No such output";
		}
	}
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Evaluated Solution Space";
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







