package ncsa.d2k.modules.projects.pgroves.geostat;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;


/**
	Given a table that represents the distance matrix between
	points in a data sets, generates a table with the
	pairs (h, gamma(h)), where h is the distance and
	gamma is the semivariogram function. There is one
	pair for every value in the distance matrix, which
	is equivalent to one point for every pair of points
	in the original data. Note that this currently means
	every distance is counted twice, as direction is 
	considered relevant.

	@author pgroves
	@date 03/11/04
	*/

public class RawSemivariogram extends ComputeModule 
	implements java.io.Serializable {

	//////////////////////
	//d2k Props
	////////////////////
	/** if 2, variogram, if 1, madogram, if .5 rodogram*/
	double variogramOrder = 2;
	
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
		Table distMat = (Table)pullInput(0);
		ExampleTable dataTable = (ExampleTable)pullInput(1);
		
		this.pushOutput(calcVariogramValues(distMat, dataTable, 
			variogramOrder), 0);
	}
	
	/**
		For every directed pair of points, calculates the 
		experimental variogram model. If the Z values for two
		points are z1 and z2, the variogram value is [abs(z1-z2)]^w, where
		w is the variogram order.
		
		@param distMat an NxN symmetric table containing the (possibly 
		directed)distances between points
		@param dataTable the table that the distMat is derived from. This
		must also contain the sample values (z values) as the first
		output column.
		@param variogramOrder the exponent power w for calculating the
		variogram

		@return a table with N^2 rows and two columns: h and gamma(h)
	*/
	public MutableTableImpl calcVariogramValues(Table distMat, 
		ExampleTable dataTable, double variogramOrder){
		
		int numPoints = distMat.getNumRows();
		int dataOutputIdx = dataTable.getOutputFeatures()[0];
		//count every pair of points, but not when a point is a pair
		//with itself (the diagonal of the distance matrix containing
		//all zeroes)
		int variogramSize = numPoints * numPoints - numPoints;

		int numCols = 2;
		String[] variogramColLabels ={"h", "gamm(h)"};
		Column[] cols = new Column[numCols];
		int i,j,k;
		for(i = 0; i < numCols; i++){
			cols[i] = new DoubleColumn(variogramSize);
			cols[i].setLabel(variogramColLabels[i]);
		}

		MutableTableImpl variogram = new MutableTableImpl(cols);
		variogram.setLabel("Raw Variogram");

		double z1, z2, h, gam;
		k = 0;
		for(i = 0; i < numPoints; i++){
			for(j = 0; j < numPoints; j++){
				
				if(j == i){
					continue;
				}
				
				h = distMat.getDouble(i, j);
				variogram.setDouble(h, k, 0);

				z1 = dataTable.getDouble(i, dataOutputIdx);
				z2 = dataTable.getDouble(j, dataOutputIdx);
				gam = z1 - z2;
				if(gam < 0){
					gam *= -1;
				}
				gam = Math.pow(gam, variogramOrder);
				variogram.setDouble(gam, k, 1);

				k++;
			}
		}
		return variogram;
	}
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return 	
			"	Given a table that represents the distance matrix between" +
			" points in a data sets, generates a table with the" +
			" pairs (h, gamma(h)), where h is the distance and" +
			" gamma is the semivariogram function. There is one" +
			" pair for every value in the distance matrix, which" +
			" is equivalent to one point for every pair of points" +
			" in the original data. Note that this currently means" +
			" every distance is counted twice, as direction is " +
			" considered relevant.  "+
			"The formula used is actually gamma = [abs(z1-z2)]^w, where z1 " +
			"and z2"+
			" are the values of the first output variable, and w is the"+
			" variogram order. Typically this should be 2, but can be changed"+
			" to values between 0 and 2 for some exploratory data analysis"+
			"tasks.";
	}
	
   	public String getModuleName() {
		return "RawSemivariogram";
	}
	public String[] getInputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.table.Table",
			"ncsa.d2k.modules.core.datatype.table.ExampleTable"
			};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: 
				return "A table representing the the distance matrix of the"+
				" input features of the rows in the data table. While typically"+
				" symmetrical, it is possible to consider direction important "+
				" so that Dist(x1,x2) != Dist(x2, x1). If this is the case, the" +
				" results of this module's calculations will still be valid.";
			case 1: 
				return "An example table where the input features have been used"+
				" to calculate the distance matrix and the first output features"+
				" is considered the 'z' value of interest";
			case 2: 
				return "";
			default: return "No such input";
		}
	}
	
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Distance Matrix";
			case 1:
				return "Data Table";
			case 2:
				return "";
			default: return "No such input";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {
			"ncsa.d2k.modules.datatype.table.basic.MutableTableImpl"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: 
				return "A table where the first column is the distance, or 'h'" +
				" value, and the second is the variogram, or gamma value of " +
				" the output z values. There is one row in this table for every" +
				" pair of points in the data table (every entry in the Distan"+
				"ce Matrix). Note that this includes an entry for the distance "+
				" between each point and itself.";
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
				return "Raw Semivariogram";
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
	public double getVariogramOrder(){
		return variogramOrder;
	}
	public void setVariogramOrder(double d){
		variogramOrder = d;
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
			
					

			

								
	
