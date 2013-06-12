package ncsa.d2k.modules.projects.pgroves.instancebased;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.util.TableUtilities;
import ncsa.d2k.modules.core.datatype.table.basic.*;


/** 
	Given a Distance Matrix, for every example sorts the other examples in 
	terms of nearest distance to that example. Intended for use with 
	instance based (nearest neighbors) modeling.


	@author pgroves
	@date -1/14/04
	*/

public class SortDistances extends ComputeModule 
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
		
		Table dists = (Table)pullInput(0);
		int numExamples = dists.getNumRows();

		MutableTableImpl neighborOrder = new MutableTableImpl(numExamples);
		int[] order;
		int[] sortByCol = new int[1];
		for(int i=0; i<numExamples; i++){
			sortByCol[0] = i;
			order = TableUtilities.multiSortIndex(dists, sortByCol);
			neighborOrder.setColumn(new IntColumn(order), i);
			neighborOrder.setColumnLabel("NeighborsOf:"+i, i);
		}
		pushOutput(neighborOrder, 0);
	}
		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return 	
			"Given a Distance Matrix, orders the examples in terms of distance"+
			" to a certain example. This is done for every example. ";
	}
	
   	public String getModuleName() {
		return "SortDistances";
	}
	public String[] getInputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.table.Table"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: 
				return "A distance matrix as output from the module "+
				"CalcDistanceMatrix.";
			default: return "No such input";
		}
	}
	
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Distance Matrix";
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
				return "A table containing the sorted order of nearest neighbors"+
				"based on the distances in the input distance matrix. This is done"+
				" by column, so every column contains an ordering of all the "+
				" examples in the original table that was input into CalcDist"+
				"anceMatrix. NOTE: this means that the first row in every column"+
				" will be the index of the column itself, as the nearest neighbor"+
				" for every example is itself (with a distance of zero). In most"+
				" applications, you will therefore want to disregard the first "+
				"row.";
			default: return "No such output";
		}
	}
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Nearest Neighbor Orderings";
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
			
					

			

								
	
