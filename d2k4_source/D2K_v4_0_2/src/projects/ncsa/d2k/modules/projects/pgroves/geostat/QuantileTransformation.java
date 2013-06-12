package ncsa.d2k.modules.projects.pgroves.geostat;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.util.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

import ncsa.d2k.modules.PredictionModelModule;
import java.util.*;

/**
 * a ranking transformation of an output feature that uses
 * the values of the nearest neighbors of a point (in the input
 * feature space) to break ties. It can actually use any
 * <code>PredictionModelModule</code> to predict the values
 * of the points in the tie-breaking. However, only forms of 
 * nearest neighbors algorithms should be used in geostats
 * applications. The forward transformation
 * only is valid for the source data set. The entire
 * system is only valid for <i>numerical output features</i>.
 */

public class QuantileTransformation extends RankingTransformation{

	/** holds the same information as super.origOrder, but the
	 * indices and values are inverted. Therefore invOrigOrder[i][j]=k
	 * means that row 'j' in the original table will be at location
	 * 'k' in the new table (both in column i).
	 */
	int[][] invOrigOrder;
	
	/**
	 * build a ranking transformation on the output features, breaking
	 * ties using the pmm. the pmm should be built using all of
	 * the examples in sourceTable
	 */
	public QuantileTransformation(ExampleTable sourceTable, 
			PredictionModelModule pmm){
		
		super(sourceTable, sourceTable.getOutputFeatures());

		
		this.updateRanksUsingNeighborhood(pmm, sourceTable);

		invOrigOrder = new int[origOrder.length][origOrder[0].length];
		for(int i = 0; i < invOrigOrder.length; i++){
			for(int j = 0; j < invOrigOrder[i].length; j++){
				invOrigOrder[i][origOrder[i][j]] = j;
			}
		}
	}

	/**
	 * breaks any ties that may be found in the rankings using
	 * the average of the other output values in the neighborhood.
	 * Only the <code>origOrder</code> array is affected.
	 *
	 * @param pmm predicts the value of a point using its neighbors.
	 * 	Ideally, the prediction will not rely on its own value
	 * 	even though the point being predicted is in the pmm's
	 * 	training set.
	 **/
	protected void updateRanksUsingNeighborhood(PredictionModelModule pmm,
			ExampleTable sourceTable){
		int numRows = sourceTable.getNumRows();
		ExampleTable et = (ExampleTable)(sourceTable.copy());
		int[] newTestSet = new int[numRows];
		int i, j, k, v;
		for(i = 0; i < numRows; i++){
			newTestSet[i] = i;
		}
		et.setTestingSet(newTestSet);
		PredictionTable pt = et.toPredictionTable();
		try{
			pmm.predict(pt);
		}catch(Exception e){
			System.out.println("Quantile Ranks Update Failed, using " +
					" rankings withough neighbor info tie-breaks");
			e.printStackTrace();
			return;
		}
		
		CompareWithNeighborhood comp = new CompareWithNeighborhood(pt);

		//do an insertion sort on each column
		int[] outputs = this.transCols;
		int numOutputs = outputs.length;
		int[] order;
		for(k = 0; k < numOutputs; k++){
			order = origOrder[k];
			
			for (j = 1; j < numRows; j++) {
				i = j - 1;
				v = order[j];
				while ((i >= 0) && 
						(0 < comp.compare(order[i], v, k))) {
			 
					order[i + 1] = order[i];
					i--;
				}
				//System.out.println("Setting Value:" + 
				//	pt.getString(v, k) + " with pred:"+
				//	pt.getString(v, pt.getPredictionSet()[k]) +
				//	" to pos:" + (i+1) + " from:" + j);
				order[i + 1] = v;
			}
	}


	}

	/**
	 * replaces the forward transformation from the superclass
	 * with one that simply returns the rank that the row in
	 * question of the source table had.
	 */
	
	public double mapForward(Table tbl, int row, int transColIdx){
		return (double)invOrigOrder[transColIdx][row] + 1;
		
	}
	

	/**
	 * does the untransformation from the superclass, but insures
	 * that the output features of the past in table are used
	 * (and not the output features of the source table)
	 */
	public boolean untransform(MutableTable tbl){
		this.transCols  = ((ExampleTable)tbl).getOutputFeatures();
		return super.untransform(tbl);
	}

	protected class CompareWithNeighborhood{
	
		PredictionTable neighborPT;

		
		public CompareWithNeighborhood(PredictionTable pt){
			this.neighborPT = pt;
		}

		/**
		 * if first is greater, 1,
		 * if first less than or equal second, -1
		 *
		 * @param row* which rows to compare
		 * @param inputCol an index into the inputFeatures (and PredictionSet)
		 */
		public int compare(int row1, int row2, int outputCol){
			int mainCol = neighborPT.getOutputFeatures()[outputCol];
			int secondaryCol = neighborPT.getPredictionSet()[outputCol];

			double mainVal1 = neighborPT.getDouble(row1, mainCol);
			double mainVal2 = neighborPT.getDouble(row2, mainCol);
			if(mainVal1 > mainVal2)
				return 1;
			if(mainVal1 < mainVal2)
				return -1;
			
			double secondVal1 = neighborPT.getDouble(row1, secondaryCol); 
			double secondVal2 = neighborPT.getDouble(row2, secondaryCol); 
	
			if(secondVal1 > secondVal2){
				return 1;
			}else{
				return -1;
			}
		}
	}
}
