package ncsa.d2k.modules.projects.pgroves.bp;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;


/**

	Takes in a table w/ geographic coordinates and creates a table that has
	a row for every pixel in an image (size set by properties) that would
	show the regtion that encompasses all of the geographic coordinates.
	Also returns a table with the original points from the table
	converted to the pixel coordinate system from the geographic
	coordinate system.


	@author pgroves
	@date 01/14/04
	*/

public class CreateImagePredTable extends DataPrepModule 
	implements java.io.Serializable {

	//////////////////////
	//d2k Props
	////////////////////
	/** the width the image will encompass. the height will be such that
	the original width/height ratio is preserved*/
	int imagePixelWidth = 500;

	/**the number of pixels from the min/max geographic points to the edges
	of the image*/
	int borderPadding = 25;
	
	boolean debug=false;		
	/////////////////////////
	/// other fields
	////////////////////////

	//these are global only to return values from functions. they
	//are made and destroyed with every firing

	PredictionTableImpl imagePredTable;

	/** columns (width) of image*/
	int imCols;
	/** rows (heigth) of image*/
	int imRows;
	/** max of geographic coords in input table*/
	double[] max;
	/** min of geographic coords in input table*/
	double[] min;
	/** geographic max of image*/
	double[] imMax;
	/** geographic min of image*/
	double[] imMin;

	/** the feet per pixel*/
	double res;
	

	MutableTableImpl convertedCoords;

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
		imagePredTable = null;
		convertedCoords = null;
		imCols = -1;
		imRows = -1;
		max = null;
		min = null;
		imMax = null;
		imMin = null;
		res = -1.0;
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

		ExampleTable et = (ExampleTable)pullInput(0);

		createImageTable(et);

		this.pushOutput(imagePredTable, 0);
		this.pushOutput(new Integer(imCols), 1);

		this.pushOutput(convertToPixelCoords(et), 2);
		
		wipeFields();
	}

	/**creates the prediction table containing an example for
	every pixel in an image. 
	**/
	
	public void createImageTable(ExampleTable et){
		int numRows = et.getNumRows();

		int xInd=0;
		int yInd=1;
		int numDims=2;

		String[] dimNames = {"x","y"};
		
		//the first input feature is assumed to be the x coordinate,
		//the second is assumed to be y
		int[] col = new int[numDims];
		col[xInd] = et.getInputFeatures()[xInd];
		col[yInd] = et.getInputFeatures()[yInd];


		//find the max and min of the x and y coordinates
		double[] min = new double[numDims];
		double[] max = new double[numDims];

		int i,j,k;
		double d;
		for(i=0; i<numDims; i++){
			min[i]=Double.MAX_VALUE;
			max[i]=Double.MIN_VALUE;
			for(j=0; j<numRows; j++){
				
				d=et.getDouble(j,col[i]);
				
				if(min[i] > d){
					min[i] = d;
				}
				if(max[i] < d){
					max[i] = d;
				}
			}
			if(debug){
				System.out.println("dim:" + i + " min:" + min[i] + " max:" 
					+ max[i]);
			}
		}

		//using the known width of the image (from props) calculate
		//the resolution of a single pixel (eg. feet/pixel)
		res = max[xInd]-min[xInd];
		res /= ((double)imagePixelWidth);
			
		int imagePixelHeight = (int)((1/res) * (max[yInd] - min[yInd]));	

		//the dimensions of the image, including the borders
		imCols = 2 * borderPadding + imagePixelWidth;
		imRows = 2 * borderPadding + imagePixelHeight;
		
		if(debug){
			System.out.println("Image Width:"+imCols+", Image Heigth:"+imRows);
		}
		imMin = new double[numDims];
		imMin[xInd]= min[xInd]-(borderPadding*res);
		imMin[yInd]= min[yInd]-(borderPadding*res);

		int predRows = imCols*imRows;

		MutableTableImpl tbl = new MutableTableImpl(numDims);

		for(i=0; i<numDims; i++){
			tbl.setColumn(new DoubleColumn(predRows), i);
			tbl.setColumnLabel(et.getColumnLabel(et.getInputFeatures()[i]), i);
		}
		
		//use k for the row in the prediction table
		k=0;
		for(i=0; i<imRows; i++){
			for(j=0; j<imCols; j++){
				tbl.setDouble(imMin[xInd]+j*res, k, xInd); 
				//note: we make the y coord inverted b/c graphics
				//are displayed 'upside-down'
				tbl.setDouble(imMin[yInd]+((imRows - i)*res), k, yInd);
				k++;
			}
		}
		ExampleTableImpl pet = new ExampleTableImpl(tbl);
		int[] inputs = new int[numDims];
		for(i=0;i<numDims; i++){
			inputs[i]=i;
		}
		pet.setInputFeatures(inputs);
		
		imagePredTable = new PredictionTableImpl(pet);
		
		
	}
	
	/**
		converts the original geographic points to the image's
		coordinate system (pixel coordinates). MUST be called
		AFTER createImageTable, as it uses global variables
		defined therein. returns only the coordinates, will
		not copy any other columns contained in the original
		table except the first output column
	*/
	public ExampleTableImpl convertToPixelCoords(ExampleTable et){
		int etNumRows = et.getNumRows();
		MutableTableImpl pixTable = new MutableTableImpl(3);
		//indices into the table columns
		int xInd = 0;
		int yInd = 1;

		int i, j;
		double d, percentRange;
		int[] inputs = new int[2];
		for(i = 0; i < 2; i++){
			inputs[i] = i;
			pixTable.setColumn(new DoubleColumn(etNumRows), i);
			//System.out.println("Dim:"+i);
			for(j = 0; j < etNumRows; j++){
				d = et.getDouble(j, et.getInputFeatures()[i]);
				//System.out.print("    geo:"+d);
				d = (d - imMin[i]) / res;
				if(i == 1){
					d = imRows - d;
				}
				//System.out.println(" pix:"+d);
				pixTable.setDouble(d, j, i);
			}
		}
		pixTable.setColumnLabel("X-Pixels", 0);
		pixTable.setColumnLabel("Y-Pixels", 1);

		pixTable.setColumn(ColumnUtilities.deepCopyColumn(
				et, et.getOutputFeatures()[0]), 2);
		ExampleTableImpl pixet = new ExampleTableImpl(pixTable);
		pixet.setInputFeatures(inputs);
		int[] outs = new int[1];
		outs[0] = 2;
		pixet.setOutputFeatures(outs);
		
		return pixet;
	}
		
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return 	
			"Given an example table with (x,y) coordinates as the "+
			"first two input columns, creates an prediction table that"+
			" will hold a prediction for each pixel in an image that covers"+
			" the same area as the points in the example table. ";
	}
	
   	public String getModuleName() {
		return "CreateImagePredTable";
	}
	public String[] getInputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: 
				return "An example table where the first two input columns"+
				" contain (x,y) coordinates";
			default: return "No such input";
		}
	}
	
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Geographic ExampleTable";
			default: return "No such input";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.table.basic.PredictionTableImpl",
			"java.lang.Integer",
			"ncsa.d2k.modules.core.datatype.table.basic.ExampleTableImpl"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: 
				return "A table with as many rows as there are pixels in an"+
				" image that would encompass all of the points in the Geogr"+
				"aphic ExampleTable, plus the specified border";
			case 1:
				return "The number of columns (the width) of the image that"+
				" the table represents";
			case 2:
				return "The locations of the input examples converted into " +
				"the coordinate system of the image.";
				
			default: return "No such output";
		}
	}
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "ImagePredictionTable";
			case 1:
				return "ImagePixelWidth";
			case 2:
				return "Converted Coordinates of Inputs";
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
	public int getImagePixelWidth(){
		return imagePixelWidth;
	}
	public void setImagePixelWidth(int i){
		imagePixelWidth=i;
	}
	public int getBorderPadding(){
		return borderPadding;
	}
	public void setBorderPadding(int i){
		borderPadding=i;
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
			
					

			

								
	
