package ncsa.d2k.modules.projects.asingh8.optimize.ga.iga.imageprocessors.VarImageProcessor;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.datatype.table.transformations.*;
import ncsa.d2k.modules.core.optimize.util.*;
import ncsa.d2k.modules.core.optimize.ga.emo.*;
import ncsa.d2k.modules.core.optimize.ga.nsga.*;
import ncsa.d2k.modules.projects.mbabbar.optimize.ga.iga.*;
import java.io.Serializable;


import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import javax.imageio.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

/**
	Takes in the GA Population, and an IGA Table (table 1) with individuals marked for human ranking.
        It also takes in an input table that has the information of ALL the wells installed
        at the site. This module creates a well table (table 2) that stores the X and Y locations of
        ALL the wells at the site, and a gene table (table 3) that stores the genes of all the individuals
        marked in table 1. Table 3 is passed into the Interpolation module so that all the marked
        individuals can be processed for the interpolated grid.

*/
public class PrepForInterpolation extends ncsa.d2k.core.modules.ComputeModule 	{

	//////////////////////////////////
	// Other Fields
	//////////////////////////////////

        IGANsgaPopulation pop;
        MutableTable popTable;
      
        MutableTableImpl geneTable;

        // minimum geographic coordinates of the site
        double xMin = 1360;
        double yMin = 440;
        // width of grid elements/pixels in the X and Y directions
        double xPixelWidth = 10;
        double yPixelWidth = 10;

	////////////////////////////////
	//D2K Property get/set methods
	///////////////////////////////
        public double getXMin(){
		return xMin;
	}
	public void setXMin(double i){
		xMin=i;
	}
        public double getYMin(){
		return yMin;
	}
	public void setYMin(double i){
		yMin=i;
	}
        public double getXPixelWidth(){
		return xPixelWidth;
	}
	public void setXPixelWidth(double i){
		xPixelWidth=i;
	}
        public double getYPixelWidth(){
		return yPixelWidth;
	}
	public void setYPixelWidth(double i){
		yPixelWidth=i;
	}

	//////////////////////////////////
	// Info methods
	//////////////////////////////////
        boolean debug=false;

        public void setDebug(boolean b){
          debug=b;
	}
	public boolean getDebug(){
          return debug;
	}


	public String[] getInputTypes () {
		String[] types = {"ncsa.d2k.modules.core.optimize.ga.Population",
                      "ncsa.d2k.modules.core.datatype.table.Table"};
		return types;
	}

	public String[] getOutputTypes () {
		String[] types = {"ncsa.d2k.modules.core.optimize.ga.Population",
                      "ncsa.d2k.modules.core.datatype.table.Table",
                      "ncsa.d2k.modules.core.datatype.table.Table"};
		return types;
        }
        /**
          This method returns the names of the various inputs.
          @return the name of the indexed input.
	*/
	public String getOutputName (int index) {
		switch (index) {
			case 0: return "IGA Nsga Population";
			case 1: return "IGA Table";
                        
                        case 2: return "Genes Table";
                        default: return "No such output";
		}
        }

	/**
		This method returns the names of the various inputs.

		@return the name of the indexed input.
	*/
	public String getInputName (int index) {
		switch (index) {
			case 0: return "IGA Nsga Population";
			case 1: return "IGA Table";
                        
                        default: return "No such input";
		}
	}

	/**
		This method returns the description of the various inputs.

		@return the description of the indexed input.
	*/
	public String getOutputInfo (int index) {
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"population\">    <Text> population. </Text>  </Info></D2K>";
			case 1: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Table\">    <Text>The table with population info: quantitative objectives, selected individuals,  images/applets info for selected individuals. </Text>  </Info></D2K>";
                        
                        case 2: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Table\">    <Text>The table with gene info: The actual individual id of individuals tagged for human ranking in 'IGA Table' and the chromosome for that individual. </Text>  </Info></D2K>";
                        default: return "No such output";
		}
       }

	/**
		This method returns the description of the various inputs.

		@return the description of the indexed input.
	*/
	public String getInputInfo (int index) {
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"population\">    <Text> population. </Text>  </Info></D2K>";
			case 1: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Table\">    <Text>The table with quantitative fitnesses and updated individuals after passing through a selection filter. </Text>  </Info></D2K>";
                       
                        default: return "No such input";
		}
	}

	/**
		This method returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo () {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Image Processing Module\">    <Text> This takes in the population and table of selected individuals and processes them to create various other tables for further image processing. </Text>  </Info></D2K>";
	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "IGA: Prep for Interpolation";
	}

        //////////////////////////
	///d2k control methods
	//////////////////////////
	public boolean isReady(){
		
			return super.isReady();
		
	}

	/**
          Do the preparation of tables for imageprocessing of selected individuals. Information of individuals selected
          for further processing (from tradeoff plots) is in the input table.
          Overwrite the imageprocessor, which will be specific to your
          image processing module for your application.
	*/
	public void doit () throws Exception {
		pop = (IGANsgaPopulation) this.pullInput(0);
                
                popTable= (MutableTable) this.pullInput(1);
                
                
		//this.wellTableEditor ();
                this.geneTableCreator ();

                // add new column to popTable to store indices of JPanel Array (that will be created later)
                // that contain images of individuals selected for visual display.
                popTable.addColumn(new IntColumn(pop.size()));
                popTable.setColumnLabel("Image Array Indices", popTable.getNumColumns() - 1);
                // initialize all index entries to -1
                for (int i =0; i < popTable.getNumRows(); i++){
                  popTable.setInt(-1, i, popTable.getNumColumns() - 1);
                }
                // set the image array index (which is in same order as the entries in geneTable)
                // to appropriate individuals in popTable.
                for (int i =0; i < geneTable.getNumRows(); i++){
                  popTable.setInt(i, geneTable.getInt(i,0), popTable.getNumColumns() - 1);
                }
                
                if(geneTable == null){
                    System.out.println("ERROR - GeneTable Null");
                }
                if(popTable == null){
                    System.out.println("ERROR - PopTable Null");
                }

		this.pushOutput (pop, 0);
                this.pushOutput (popTable, 1);
                
                this.pushOutput (geneTable, 2);
	}

	/**
		Processes the wellTable to get rid of extra columns in the well table, so that it stores only the X and Y locations of the wells.
	*/


        /**
         * This method takes in a Table with X and Y coordinates in feet
         * and then converts the feet coordinates to pixel coordinates
         * that coincide with the grid elements.
         */
        public void feetToPixelCoordinates (MutableTableImpl XYTable){

          // This assumes Column 1 has X coordinates and Column 2 has Y coordinates.
          for (int i= 0; i < XYTable.getNumRows(); i++){
            double xCoord = (XYTable.getDouble(i,0)-xMin)/xPixelWidth;
            XYTable.setDouble(xCoord,i,0);

            double yCoord = (XYTable.getDouble(i,1)-yMin)/yPixelWidth;
            XYTable.setDouble(yCoord,i,1);
          }

        }

	/**
		Creates a gene Table that stores the genes of the tagged individuals in popTable, along with their actual individual ID of the population.
	*/
	public void geneTableCreator (){

                // obtain all the individuals from the IGANsgaPopulation
                IGANsgaSolution [] ind = new IGANsgaSolution[pop.size()];
                for (int i = 0; i < pop.size(); i++) {
                   ind[i] = (IGANsgaSolution) pop.getMember(i);
                }

                // obtain the genes (binary for binary coded and double for real coded indivs)
                int numGenes ;
                numGenes = pop.getNumGenes();


                // geneTable has # of columns = number of genes + 1 (this is for individual ID in the population)
                geneTable = new MutableTableImpl(numGenes + 1);
                // geneTable =   (MutableTableImpl)DefaultTableFactory.getInstance().createTable(numGenes + 1);

                // obtain the number or index of the column that stores selection flags.
                int selectionColumnIndex = popTable.getNumColumns() - 1;

                // obtain individuals tagged for human ranking in the popTable.
                // These individuals will be ranked in the current interactive session.
                int geneRowId = 0;

                // array to store the Id and gene combined for all the individuals with
                // flag for selection in popTable set to true
                double [][] combgene = new double [popTable.getNumRows()][numGenes+1];

                for (int i =0; i < popTable.getNumRows(); i++){
                    
                   // if (popTable.getBoolean(i, selectionColumnIndex) == true ){

                          // obtain the genes for this chromosome.
                          double [] genes;
                          if (ind[i] instanceof MONumericIndividual) {
                            genes = (double []) ((Individual) ind[i]).getGenes ();
                          }
                          else{
                            // get the actual gene chromosome in binary format.
                            genes = (double []) ((MOBinaryIndividual) ind[i]).toDouble();
                          }

                          // combined gene ID and gene array
                          // debugging line
                          if (debug){
                            System.out.println("numGenes = "+ numGenes + " Gene array length = "+ genes.length);
                            for (int k=0; k < genes.length; k++){
                              //System.out.println(k + "Genes : " + genes[k]);
                            }
                          }
                          combgene[geneRowId][0] = (double) i;
                          for (int k = 0; k < numGenes; k++){
                            combgene[geneRowId][k+1] = genes[k];
                          }

                          geneRowId ++ ;

                     // if popTable.getBoolean
                } // for i

                // add the combgene to the geneTable
                // geneTable.addRow(combgene);
                // adding columns one by one
                for(int i = 0; i < numGenes+1; i++){
			double[] colDat = new double[geneRowId];
                        for (int j=0; j< geneRowId; j++){
                          colDat[j] = combgene[j][i];
                        }
			geneTable.setColumn(new DoubleColumn(colDat), i);
		}
        }

}








