package ncsa.d2k.modules.projects.pgroves.feature;


import ncsa.d2k.core.modules.*;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

import java.util.*;


/**
 * Calculates the convex hull of the points in a table. Can be based
 * on only the input/output features, or can include all columns. Note
 * that only the first two columns are actually used.
 *
 * <p>Uses the Graham Scan algorithm.
 * A useful reference:<br>
 * http://softsurfer.com/Archive/algorithm_0109/algorithm_0109.htm#Graham%20Scan
 * 
 * @author pgroves
 * @date 05/13/04
 
*/

public class ConvexHull extends ComputeModule 
	implements java.io.Serializable {

	//////////////////////
	//d2k Props
	////////////////////
	
	boolean inputFeatures = true;
	boolean outputFeatures = false;
	boolean otherColumns = false;
		
	
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
	/**
		does it
	*/
	public void doit() throws Exception{
		if(debug){
			System.out.println(getAlias()+":Firing");
		}

		Table tbl = (Table)pullInput(0);
		
		int[] hullFeatures;
		int i, j;
		if(tbl instanceof ExampleTable){
			ExampleTable et = (ExampleTable)tbl;
			ArrayList hullFeatVect = new ArrayList();
			
			if(inputFeatures){
				for(i = 0; i < et.getNumInputFeatures(); i++){
					hullFeatVect.add(new Integer(et.getInputFeatures()[i]));
				}
			}
			if(outputFeatures){
				for(i = 0; i < et.getNumOutputFeatures(); i++){
					hullFeatVect.add(new Integer(et.getOutputFeatures()[i]));
				}
			}
			if(otherColumns){
				boolean isNonFeature;
				for(i = 0; i < et.getNumColumns(); i++){
					isNonFeature = true;
					for(j = 0; j < et.getNumInputFeatures() && isNonFeature; j++){
						if(i == et.getInputFeatures()[j]){
							isNonFeature = false;
						}
					}
					for(j = 0; j < et.getNumOutputFeatures() && isNonFeature; j++){
						if(i == et.getOutputFeatures()[j]){
							isNonFeature = false;
						}
					}
					if(isNonFeature){
						hullFeatVect.add(new Integer(i));
					}
				}
			}

			hullFeatures = new int[hullFeatVect.size()];
			for(i = 0; i < hullFeatures.length; i++){
				hullFeatures[i] = ((Integer)hullFeatVect.get(i)).intValue();
			}
			
		}else{
			int numHullCols = tbl.getNumColumns();
			hullFeatures = new int[numHullCols];
			for(i = 0; i < numHullCols; i++){
				hullFeatures[i] = i;
			}
		}

		Table hullTable = this.findConvexHull(tbl, hullFeatures);
		pushOutput(hullTable, 0);
		
	}

	/**
	 * Finds those rows in a table that define the convex hull. 
	 * 
	 * @param tbl a table that contains the points
	 * @param hullFeatures which 2 columns should be considered
	 *
	 * @return a subset table of the original table. the subset will
	 * represent the convex hull, in order, starting at the rightmost,
	 * loweset point
	 */
	public Table findConvexHull(Table tbl, int[] hullFeatures){

		int xCol = hullFeatures[0];
		int yCol = hullFeatures[1];
		int numRows = tbl.getNumRows();

		int originIndex = 0;
		double highestX = Double.NEGATIVE_INFINITY;
		double yOfHighestX = Double.POSITIVE_INFINITY;
		double currentX, currentY;
		//find the rightmost, lowest point
		int i, j, k;
		for(i = 0; i < numRows; i++){
			currentX = tbl.getDouble(i, xCol);
			currentY = tbl.getDouble(i, yCol);
			if((currentX > highestX) ||
				((currentX == highestX) && 
				 (currentY < yOfHighestX))){
				highestX = currentX;
				yOfHighestX = currentY;
				originIndex = i;
			}
		}

		//create graham examples and sort them by angle to the origin
		TreeSet allPoints = new TreeSet();
		for(i = 0; i < numRows; i++){
			if(i != originIndex){
				GrahamExample ge = new GrahamExample(tbl, i, originIndex,
						xCol, yCol);
				allPoints.add(ge);
			}
		}
		Stack hullPoints = new Stack();
		
		//put the first two on the stack
		GrahamExample originGE = new GrahamExample(tbl, originIndex, 
				originIndex, xCol, yCol);
		hullPoints.push(originGE);


		Iterator pointIt = allPoints.iterator();
		hullPoints.push(pointIt.next());

		GrahamExample p1, p2, p3;

		p3 = (GrahamExample)pointIt.next();
		while(true){
			p1 = (GrahamExample)hullPoints.pop();
			p2 = (GrahamExample)hullPoints.peek();
			//System.out.println();
			//System.out.println("P1 ==" + p1);
			//System.out.println("P2 ==" + p2);
			//System.out.println("P3 ==" + p3);
			if(this.isLeft(p3, p2, p1)){
				//System.out.println("Pushing p1 back on");
				hullPoints.push(p1);
				if(p3 == originGE){
					//System.out.println("p3 was origin - done");
					break;
				}else if(pointIt.hasNext()){
					//System.out.println("pushing p3, getting new p3");
					hullPoints.push(p3);
					p3 = (GrahamExample)pointIt.next();
				}else{
					//System.out.println("no new p3's, set to origin");
					hullPoints.push(p3);
					p3 = originGE;
				}
					
			}
		}

		int hullSize = hullPoints.size();
		int[] hullRowIndices = new int[hullSize];
		System.out.println("Hull Size:" + hullSize);
		for(i = 0; i < hullSize; i++){
			hullRowIndices[i] = ((GrahamExample)hullPoints.get(i)).rowIdx;
		}
		Table hullTable = tbl.getSubset(hullRowIndices);
		return hullTable;
			
	}

	/**
	 * tells whether a point is to the left of the vector connecting
	 * the other two points. 
	 *
	 * @param point the point under consideration
	 * @param startPoint the starting point of the vector
	 * @param endPoint the end of the vector
	 *
	 * @return whether the point is to the left of the vector, or if
	 * it is past the end of it.
	 */
	protected boolean isLeft(GrahamExample point, 
			GrahamExample startPoint, GrahamExample endPoint){
		
		double comp1 = endPoint.getXValue() - startPoint.getXValue();
		comp1 *= (point.getYValue() - startPoint.getYValue());
		
		double comp2 = point.getXValue() - startPoint.getXValue();
		comp2 *= (endPoint.getYValue() - startPoint.getYValue());
		
		comp1 -= comp2;

		return comp1 >= 0;
	}
	
	/**
		for creating GrahamExamples outside of this class
		*/
	protected GrahamExample makeGrahamExample(Table tbl, int rowIndex, 
		int originRowIndex, int xColumnIndex, int yColumnIndex){
		
		return new GrahamExample(tbl, rowIndex, originRowIndex, xColumnIndex,
			yColumnIndex);
	}
	/**
	 * this inner class is used to identify an example (row of a table)
	 * by it's distance and angle to another example. 
	 *
	 */
	protected class GrahamExample extends java.lang.Object
		implements java.lang.Comparable{
		
		//which row in the table this example comes from
		int rowIdx;
		//the table it comes from
		Table srcTable;
		//the angle between this point and the origin point
		double angle;
		//which row in the table the origin is
		int originIdx;
		//which column is considered the 'x' direction
		int xColIdx;
		//which column is considered the 'y' direction
		int yColIdx;
		//distance to the origin squared
		double distanceSquared;
		

		public GrahamExample(Table tbl, int rowIndex, int originRowIndex,
				int xColumnIndex, int yColumnIndex){
			
			this.rowIdx = rowIndex;
			this.srcTable = tbl;
			this.originIdx = originRowIndex;
			this.xColIdx = xColumnIndex;
			this.yColIdx = yColumnIndex;

			//calculate the distance
			double x = srcTable.getDouble(this.rowIdx, this.xColIdx);
			x -= srcTable.getDouble(this.originIdx, this.xColIdx);

			double y = srcTable.getDouble(this.rowIdx, this.yColIdx);
			y -= srcTable.getDouble(this.originIdx, this.yColIdx);

			this.distanceSquared = x * x + y * y;
			
			//calculate the angle
			//atan returns values between -pi and pi. we need
			//0 to 2pi
			this.angle = Math.atan2(y, x);
			if(this.angle < 0)
				this.angle += 2 * Math.PI;
		}

		public double getXValue(){
			return srcTable.getDouble(this.rowIdx, this.xColIdx);
			
		}
		public double getYValue(){
			return srcTable.getDouble(this.rowIdx, this.yColIdx);
		}
		public double getXOrigin(){
			return srcTable.getDouble(this.originIdx, this.xColIdx);
		}
		public double getYOrigin(){
			return srcTable.getDouble(this.originIdx, this.yColIdx);
		}
		
		public int getRowIdx(){
			return rowIdx;
		}
		public int compareTo(Object obj){
			GrahamExample ge = (GrahamExample)obj;
			if(ge.angle > this.angle){
				return -1;
			}
			if(this.angle > ge.angle){
				return 1;
			}
			//same angle, compare by distance
			//this definition of comparison is such that points
			//along the same vector with the origin will be sorted
			//in such a way that ones that are 'further' when going
			//in a ccw direction will be said to be 'greater'
			if(this.angle < Math.PI){
				if(this.distanceSquared > ge.distanceSquared){
					return 1;
				}else if(this.distanceSquared < ge.distanceSquared){
					return -1;
				}
				return 0;
			}else{
				if(this.distanceSquared < ge.distanceSquared){
					return 1;
				}else if(this.distanceSquared > ge.distanceSquared){
					return -1;
				}
				return 0;
			}
				
		}

		public String toString(){
			return "GrahamExample: Row:" + rowIdx + " X:" + getXValue() +
			" Y:" + getYValue();
		}
	}
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return 	
			"Returns the convex hull of the examples in a table. Currently"+
			" only supports the 2-D case. If the table is an example table,"+
			" the 2 dimensions that are used can be from any combination of"+
			" input, output, and 'other' features, which can be set in " +
			"properties. If not an ET, the first two columns will be used." ;
	}
	
   	public String getModuleName() {
		return "ConvexHull";
	}
	public String[] getInputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.table.Table"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: 
				return "A table for which the Convex Hull is sought.";
			case 1: 
				return "";
			case 2: 
				return "";
			default: return "No such input";
		}
	}
	
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Points Table";
			case 1:
				return "";
			case 2:
				return "";
			default: return "No such input";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.table.Table"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: 
				return "A table that is the subset of points from the " +
					"original table that define the convex hull. The rows are " +
					"in order, starting at the rightmost, lowest point in " +
					"the hull when the first feature is considered 'x' and the " +
					"second 'y'.";
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
				return "Convex Hull Table";
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
	public boolean getInputFeatures(){
		return inputFeatures;
	}
	public void setInputFeatures(boolean b){
		inputFeatures = b;
	}
	public boolean getoutputFeatures(){
		return outputFeatures;
	}
	public void setOutputFeatures(boolean b){
		outputFeatures = b;
	}
	public boolean getOtherColumns(){
		return otherColumns;
	}
	public void setOtherColumns(boolean b){
		otherColumns = b;
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
			
					

			

								
	
