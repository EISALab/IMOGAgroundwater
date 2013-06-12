package ncsa.d2k.modules.projects.pgroves.optimize.emo;


import ncsa.d2k.core.modules.*;

import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.optimize.util.*;
import ncsa.d2k.modules.core.optimize.ga.nsga.*;
import ncsa.d2k.modules.core.optimize.ga.*;


/**
 * Takes a Multi-Objective population and an array of ParameterPoints
 * and assigns the parameters and objectives of the
 * ParameterPoints to the Individuals within the
 * Population.
 * 
 * @author pgroves
 * @date 04/15/04
*/

public class AssignParamsToPopulation extends DataPrepModule 
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
		Population pop = (Population)pullInput(0);
		boolean isMO = false;
		if(pop instanceof NsgaPopulation){
			isMO = true;
		}
		ParameterPoint[] ppa = (ParameterPoint[])pullInput(1);
		
		int numInds = pop.size();
		int numParams = ppa.length;

		int numGenes = pop.getNumGenes();
		
		int numObjs = 1;
		if(isMO){
			numObjs = ((NsgaPopulation)pop).getNumObjectives();
		}
			
			

		if(debug){
			System.out.println(this.getAlias() + 
					": numIndividuals:" + numInds +
					", numParameterPoints:" + numParams +
					", numParams:" + ppa[0].getNumParameters() +
					", numGenes:" + numGenes +
					", numObjectives:" + numObjs);
		}

		int i, j, k;
		Solution sol;
		ParameterPoint pp;
		for(i = 0; i < numParams; i++){
			sol = pop.getMember(i);
			pp = ppa[i];
			j = 0;
			for(k = 0; k < numGenes; k++){
				sol.setDoubleParameter(pp.getValue(j), k);
				j++;
			}
			for(k = 0; k < numObjs; k++){
				if(isMO){
					((MOSolution)sol).setObjective(k, pp.getValue(j));
				}else{
					((SOSolution)sol).setObjective(pp.getValue(j));
				}
				j++;
			}
		}
		pushOutput(pop, 0);

		
	}
		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return 	
			"Assigns an array of ParameterPoints to be the members "+
			" of an NSGA Population. It actually assigns the values contained"+
			" in the ParameterPoints (including those that are objectives) "+
			"to the appropriate members of the population in place. The same "+
			" Individual instances contained in the population are therefore "+
			" retained but altered.";
	}
	
   	public String getModuleName() {
		return "AssignParamsToPopulation";
	}
	public String[] getInputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.optimize.ga.nsga.NsgaPopulation",
			"[Lncsa.d2k.modules.core.datatype.parameter.ParameterPoint:"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: 
				return "A mulitiobjective population.";
			case 1: 
				return "A set of ParmeterPoints. Should be the same size as" +
					" the population.";
			case 2: 
				return "";
			default: return "No such input";
		}
	}
	
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Population";
			case 1:
				return "ParamterPoints";
			case 2:
				return "";
			default: return "No such input";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.optimize.ga.nsga.NsgaPopulation"
			};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: 
				return "The same population pulled in, with the new values" +
					" assigned to it's members.";
			case 1:
				return "";
			case 2:
				return "";
			default: return "No such output";
		}
	}
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Filled In Population";
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
			
					

			

								
	
