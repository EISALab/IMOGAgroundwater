/*
 * DecisionTreePrediction.java
 *
 * Created on June 25, 2004, 2:07 PM
 */

package ncsa.d2k.modules.projects.asingh8.optimize.ga.iga.predictors;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.optimize.ga.emo.*;
import ncsa.d2k.modules.core.optimize.ga.nsga.*;
import ncsa.d2k.modules.projects.mbabbar.optimize.ga.iga.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.datatype.table.transformations.*;
/**
 *
 * @author  Abhishek Singh
 */
public class ImageLearnTransformTable extends ncsa.d2k.core.modules.DataPrepModule {
    
        ExampleTable origTable;
        
        ExampleTable newTable;
	//////////////////////////////////
	// Info methods
	//////////////////////////////////
	/**
		This method returns the description of the various inputs.

		@return the description of the indexed input.
	*/
	public String getInputInfo (int index) {
		switch (index) {
			case 0: return "original learning table";
                        case 1: return "transformed input table";
			default: return "No such input";
		}
	}
	/**
		This method returns the description of the various inputs.

		@return the description of the indexed input.
	*/
		public String getOutputInfo(int index) {
		switch (index) {
			case 0:
				return "Transformed learning table";
			default:
				return "No such input";
		}
	}

	/**
		This method returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo () {
		return "update learning table with transformed data";
	}
        
        
        	//////////////////////////////////
	// Type definitions.
	//////////////////////////////////

	public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.ExampleTable",
                                  "ncsa.d2k.modules.core.datatype.table.MutableTable"};
		return types;
	}

        
	public String[] getOutputTypes () {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
		return types;
	}


	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "IGA: Update Learning Data";
	}
        
	public void doit() throws Exception {
                
		int origRow,tempRow, origCol, tempCol;

                Table tempTable;
                
                origTable = null;
                tempTable = null;
                
                newTable = new ExampleTableImpl (0);
                
                origTable = (ExampleTable) this.pullInput(0);
                
                tempTable = (MutableTable) this.pullInput(1);
                
                origRow = origTable.getNumRows();
                origCol = origTable.getNumColumns();
                tempRow = tempTable.getNumRows();
                tempCol = tempTable.getNumColumns();
                                          
                
                double [][] dc = new double [tempCol+3][tempRow];
                
                int [] inputCol = new int [tempCol+2];
                int [] outputCol = new int [1];
                int [] trainingSet = new int [tempRow];
                
                
                
                for(int j = 0; j < tempCol; j++){
                    for(int i = 0; i < tempRow;i++){
                        dc[j][i] = tempTable.getDouble(i,j);
                        trainingSet[i] = i;
                    }
                    inputCol[j] = j;
                         
                }
                
                
                
                for(int i = 0; i<origTable.getNumRows(); i++){
                    dc[tempCol][i] = origTable.getDouble(i, origCol-3);
                    inputCol[tempCol] = tempCol;
                    dc[tempCol+1][i] = origTable.getDouble(i, origCol-2);
                    inputCol[tempCol+1] = tempCol+1;
                    dc[tempCol+2][i] = origTable.getDouble(i, origCol-1);
                    outputCol[0] = tempCol+2;
                }
                
                for(int i = 0; i<tempCol; i++){
                    DoubleColumn col = new DoubleColumn (dc [i]);
                    col.setLabel(tempTable.getColumnLabel(i));
                    newTable.addColumn(col);

                    
                }
                
                for(int i = 0; i < 3; i++){
                    DoubleColumn col = new DoubleColumn (dc [tempCol+i]);
                    col.setLabel(origTable.getColumnLabel(origCol-3+i));
                    newTable.addColumn(col);
                }
                
               newTable.setInputFeatures(inputCol);
               newTable.setOutputFeatures(outputCol);
               newTable.setColumnIsNominal(true, outputCol[0]);
               newTable.setTrainingSet(trainingSet);                
                
                this.pushOutput (newTable, 0);
		
	}
        
        	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Orig Table";
                        case 1:
                                return "Transformed input Table";
			default: return "NO SUCH INPUT!";
		}
	}

	/**
	 * Return the human readable name of the indexed output.
	 * @param index the index of the output.
	 * @return the human readable name of the indexed output.
	 */
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Transformed Table";
			default: return "NO SUCH OUTPUT!";
		}
	}



   
    
}
