package ncsa.d2k.modules.projects.pgroves.feature;


import ncsa.d2k.core.modules.*;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

import java.util.ArrayList;

/**
	Passes a table of only those examples that fall within the 
	convex hull of the input features. the convex hull is calculated
	outside of this module by 'ConvexHull'. Only works for 2-d case (2 inputs).
	
<p>http://softsurfer.com/Archive/algorithm_0103/algorithm_0103.htm

	@author pgroves
	@date 05/17/04
*/

public class FilterByConvexHull extends ComputeModule 
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
		ExampleTable et = (ExampleTable)pullInput(0);
		ExampleTable hull = (ExampleTable)pullInput(1);

		Table filteredTable = this.filterByConvexHull(et, hull);
		if(debug){
			System.out.println(getAlias()+":NumPoints inside:" + 
				filteredTable.getNumRows());
		}
		pushOutput(filteredTable, 0);
	}

	/**
		produces a table of only those points that fall within the
		convex hull.
		*/
	public Table filterByConvexHull(ExampleTable et, ExampleTable convexHull){
	
		//we need to use some functions that are already in ConvexHull
		ConvexHull hullObj = new ConvexHull();
			
		int numCandidatePoints = et.getNumRows();	
		ConvexHull.GrahamExample[] gea = 
			new ConvexHull.GrahamExample[numCandidatePoints];
		//the origin will not be important to these ge's
		int dummyOriginIdx = 0;
		int xCol = et.getInputFeatures()[0];
		int yCol = et.getInputFeatures()[1];

		int i, j, k;
		for(i = 0; i < numCandidatePoints; i++){
			gea[i] = hullObj.makeGrahamExample(et, i, dummyOriginIdx, 
				xCol, yCol);
		}

		//the convex hull points must also be converted to GrahamExamples.
		//for simplicity in the algorithm, add the first point to the end
		int numHullPoints = convexHull.getNumRows();
		int xColHull = convexHull.getInputFeatures()[0];
		int yColHull = convexHull.getInputFeatures()[1];
		//we know that the first point in the hull is the origin
		int hullOrigin = 0;
		ConvexHull.GrahamExample[] hullgea = 
			new ConvexHull.GrahamExample[numHullPoints + 1];
		for(i = 0; i < numHullPoints; i++){
			hullgea[i] = hullObj.makeGrahamExample(convexHull, i, hullOrigin, 
				xColHull, yColHull);
		}
		hullgea[i] = hullObj.makeGrahamExample(convexHull, 0, hullOrigin, 
				xColHull, yColHull);

		//now the actual testing

		//points that pass the test go here
		ArrayList geInside = new ArrayList();
		//winding number counter
		int wn;
		for(i = 0; i < numCandidatePoints; i++){
			wn = 0;
			for(j = 0; j < numHullPoints; j++){
				if(hullgea[j].getYValue() <= gea[i].getYValue()){
					if(hullgea[j + 1].getYValue() > gea[i].getYValue()){
						if(hullObj.isLeft(gea[i], hullgea[j], hullgea[j + 1])){
							wn++;
						}
					}
				}else{
					if(hullgea[j + 1].getYValue() <= gea[i].getYValue()){
						if(!hullObj.isLeft(gea[i], hullgea[j], hullgea[j + 1])){
							wn--;
						}
					}
				}
			}
			if(wn != 0){
				geInside.add(gea[i]);
			}
		}
		//we have the points, now make a subset table out of only those that
		//passed
		int[] subset = new int[geInside.size()];
		for(i = 0; i < subset.length; i++){
			subset[i] = ((ConvexHull.GrahamExample)(geInside.get(i))).getRowIdx();
		}

		return et.getSubset(subset);
	}
		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return 	
			"Returns a subset of the input table containing only those examples"+
			" that fall inside the convex hull. This works only for the 2-d case"+
			" and assumes that the first two input features are the dimensions"+
			" that are to be used (for both the filter table and hull table).";
	}
	
   	public String getModuleName() {
		return "FilterByConvexHull";
	}
	public String[] getInputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.table.ExampleTable",
			"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: 
				return "The table of candidate points.";
			case 1: 
				return "A table representing the points of the convex hull."+
				" This should most likely come from the ConvexHull Module. " +
				" Actually, any polygon can be used and this module will return " +
				" those points that fall inside of it (needn't be convex).";
			case 2: 
				return "";
			default: return "No such input";
		}
	}
	
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Table To Filter";
			case 1:
				return "Convex Hull";
			case 2:
				return "";
			default: return "No such input";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: 
				return "A subset of the first input table";
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
				return "Filtered Table";
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
			
					

			

								
	
