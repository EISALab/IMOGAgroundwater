package ncsa.d2k.modules.projects.asingh8.optimize.ga.iga.imageprocessors.util;


import ncsa.d2k.core.modules.*;
import java.awt.*;
import javax.swing.*;

import ncsa.d2k.modules.projects.pgroves.vis.falsecolor.*;
import ncsa.d2k.modules.projects.mbabbar.optimize.ga.iga.imageprocessors.util.*;
import ncsa.d2k.modules.core.datatype.table.Table;
import java.io.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

/**
	Applies a false, or pseudo, coloring to a grayscale image.

	@author asingh8, adapted from FalseColorWithWells edited by pgroves
	@date 01/06/05
	*/

public class MultipleFalseColorWithObs extends DataPrepModule
	implements java.io.Serializable {

	//////////////////////
	//d2k Props
	////////////////////

	boolean keepSliderStatePersistent=true;
	boolean autoInitSliderRange=true;

	double initialSliderMin=0.0;
	double initialSliderMax=0.0;

	boolean debug=false;


	/////////////////////////
	/// other fields
	////////////////////////

        MutableTableImpl interpTable = new MutableTableImpl();
        //MutableTableImpl imageTable ;
        FalseColorPanel [] [] falseColPan;
        String [] [] fcPanelTitle ;
        // array to store the IDs of all the genes that are being visualized
        int [] geneFCIds ;
        Table wells ;
        Table geneTable ;
        Table interpFilenamesTable ;

        int numGridRows = 1000;
        int numGridCols = 1000;

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
        interpTable = null;
        falseColPan = null;
        wells = null;
        geneTable = null;
        interpFilenamesTable = null;
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

		wells = (Table)pullInput(0);
                geneTable = (Table)pullInput(1);
		//int[] selectedWells = (int[])pullInput(2);
                interpFilenamesTable = (Table)pullInput(2);

                if (geneTable.getNumRows() != interpFilenamesTable.getNumRows()){
                    System.err.println("Not all selected designs have been interpolated for creating images");
                }


                // create an Array to store the actual JPanel picture for all designs, and corresponding attributes.
                //imageTable = new MutableTableImpl(interpFilenamesTable.getNumColumns());

                // Setting up the labels for the imageTable columns
               /* for (int i=0; i< imageTable.getNumColumns(); i++) {
                  Integer attribId = new Integer (i);
                  String title = new String ("Attribute"+attribId.toString());
                  imageTable.setLabel(title);
                }
                */

                // Setting up the labels and IDs for the interpolation attributes
                if(geneTable.getNumRows()>0) {
                fcPanelTitle = new String [geneTable.getNumRows()][interpFilenamesTable.getNumColumns()];
                geneFCIds = new int [geneTable.getNumRows()];
                }

                for (int m=0; m < interpFilenamesTable.getNumRows(); m++) {
                  // obtaining IDs of individuals
                  geneFCIds [m] = geneTable.getInt(m,0);
                  for (int n=0; n < interpFilenamesTable.getNumColumns(); n++) {
                    fcPanelTitle [m][n] = new String (interpFilenamesTable.getString(m,n));
                  }
                }

                // creating an array to store all the JPanels created for the interpolated grids.
                if(geneTable.getNumRows()>0){
                 falseColPan = new FalseColorPanel [geneTable.getNumRows()][interpFilenamesTable.getNumColumns()];
                }
                // looping over all the designs
                for (int i=0; i< geneTable.getNumRows(); i++) {

                    // getting from geneTable the total number of wells present in the design
                    int presentWellsCounter = 0;
                    for (int j=0; j< geneTable.getNumColumns()-1; j++){
                      if (geneTable.getDouble(i,j+1) > 0.0) {
                       // presentWellsCounter ++ ;
                      }
                    }
                    // create an array for storing the selected wells for each design
                    //presentWellsCounter = wells.getNumRows();
                    int[] selectedWells = new int [presentWellsCounter];
                    for(int j = 0; j < presentWellsCounter; j++){
                        //selectedWells[j] = j;
                    }
                    
                    int pwCounter = 0;
                    //for (int j=0; j< geneTable.getNumColumns()-1; j++){
                      //if (geneTable.getDouble(i,j+1) > 0.0) {
                        //selectedWells [pwCounter] = j;
                        //pwCounter ++ ;
                      //}
                    //}

                    // getting the interpolated grid from output files
                    for (int k=0; k < interpFilenamesTable.getNumColumns(); k++){

                          // create an array of Tables for storing the grid interpolation for each attribute,
                          // for a particular design.
                          interpTable = new MutableTableImpl(numGridCols);

                          // read the interpolated grid into interpTable (numrows*numCols of table = numrows*numCols of grid)
                          double [][] attributeGridDistrib = readInterpGridFromOutputFile (interpFilenamesTable.getString(i,k));
                          
                          //System.out.println ("interpFilenamesTable.getString(i,k) " + interpFilenamesTable.getString(i,k));
                          //System.out.println ("attributeGridDistrib[0][0] " + attributeGridDistrib[0][0]);
                          //System.out.println ("attributeGridDistrib[10][10] " + attributeGridDistrib[10][10]);
                          // fill the table interpTable with the attribute values
                          for (int m = 0; m< numGridCols; m++){
                            double [] attribCol = new double [numGridRows];
                            for (int p = 0; p< numGridRows; p++){
                              attribCol[p] = attributeGridDistrib[numGridRows - p-1][m];
                            }
                            // System.out.println("attribCol[0] " + attribCol[0]);
                            interpTable.setColumn(new DoubleColumn(attribCol), m);
                          }

                          // create Jpanel pictures of the attribute distribution
                          falseColPan [i][k] = new FalseColorPanel(interpTable); //, wells, selectedWells);
                          falseColPan [i][k].imagePanel.setZoom(4.5);
                          //, wells, selectedWells);
                        }
                }

                pushOutput (new FalseColorPanelArray (falseColPan, fcPanelTitle,geneFCIds), 0);

	}

        // this calculates the average value of all the double type entries in a table
        public double calculateAverage (MutableTableImpl mt){
          double total = 0;
          for (int p = 0; p < mt.getNumRows(); p++){
            for (int k = 0; k < mt.getNumColumns(); k++){
              total = total + mt.getDouble(p,k);
            }
          }
          return total/(mt.getNumColumns() * mt.getNumRows()) ;
        }

        // this calculates the minimum value of all the double type entries in a table
        public double calculateMin (MutableTableImpl mt){
          double min = Double.MAX_VALUE;
          for (int p = 0; p < mt.getNumRows(); p++){
            for (int k = 0; k < mt.getNumColumns(); k++){
              if( mt.getDouble(p,k) < min)
                min = mt.getDouble(p,k);
            }
          }
          return min ;
        }

        // this calculates the maximum value of all the double type entries in a table
        public double calculateMax (MutableTableImpl mt){
          double max = Double.MIN_VALUE;
          for (int p = 0; p < mt.getNumRows(); p++){
            for (int k = 0; k < mt.getNumColumns(); k++){
              if( mt.getDouble(p,k) > max)
                max = mt.getDouble(p,k);
            }
          }
          return max ;
        }

        // this read the interpolated grid from an output file.
        public double [][] readInterpGridFromOutputFile (String inFileName){
            /////////////////////
             double[][] attribute = new double [numGridRows][numGridCols] ;
             int i =0;
             int j =0;
             try {
                 if (inFileName != null){
                 FileInputStream stream = new FileInputStream(inFileName);
                 InputStreamReader reader = new InputStreamReader(stream);
                 StreamTokenizer tokens = new StreamTokenizer(reader);
                 tokens.eolIsSignificant(true);

                 int ncols = 0;
                 int nrows;
                 int sym = tokens.nextToken();
                 while (sym != StreamTokenizer.TT_EOF) {

                   if (sym != StreamTokenizer.TT_EOL) {
                      attribute[i][j] =  tokens.nval;
                      j=j+1;
                      ncols = j;
                   }

                   if (sym == StreamTokenizer.TT_EOL) {
                      j=0;
                      i=i+1;
                   }
                   sym = tokens.nextToken();

                 }
                 nrows = i;

                 if (debug) {
                     if( ncols != numGridCols){
                        System.err.println("number of columns in the outputfile" + inFileName + "don't match that specified in the module properties");
                     }
                     if( nrows != numGridRows){
                        System.err.println("number of rows in the outputfile" + inFileName + "don't match that specified in the module properties");
                     }
                 }
                 // close file and streams


                 stream.close();
                 reader.close();

                 //System.out.println("Read Success");
                }
             }
             catch (Exception e) {
                 e.printStackTrace();
                 System.err.println("IOException");
                 System.exit(1);
             }

            /////////////////////
            return attribute ;
        }

	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return
			"<html><head></head><body><p></p>"+
			""+
			""+
			""+
			""+
			"</body></html>";
	}

   	public String getModuleName() {
		return "MultipleFalseColorWithWells";
	}
	public String[] getInputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.table.Table",
			"ncsa.d2k.modules.core.datatype.table.Table",
                        "ncsa.d2k.modules.core.datatype.table.Table"
			};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0:
				return "";
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
				return "Well Locations Table";
			case 1:
				return "Genes Table";
			case 2:
				return "Image Filenames Table";
			default: return "No such input";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {
			"ncsa.d2k.modules.projects.mbabbar.optimize.ga.iga.imageprocessors.util.FalseColorPanelArray"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0:
				return "An Array with panels that have the grid image with wells drawn.";
			default: return "No such output";
		}
	}
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Images JPanel Array";
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

        public int getNumGridRows(){
		return numGridRows;
	}
	public void setNumGridRows(int numRows){
		numGridRows = numRows;
	}
        public int getNumGridCols(){
		return numGridCols;
	}
	public void setNumGridCols(int numCols){
		numGridCols = numCols;
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


}//FalseColor







