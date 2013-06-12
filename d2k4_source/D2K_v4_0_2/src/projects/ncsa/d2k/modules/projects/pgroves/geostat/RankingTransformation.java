package ncsa.d2k.modules.projects.pgroves.geostat;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.util.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import java.util.*;


/**
 * a transformation that maps any type of column's data into 
 * rankings based on that data type's default comparison scheme.
 * The generated rankings are presented as type double instead of
 * int so that applying the transformation to other tables and
 * also the untransformation can handle values that are not 
 * exactly the same as those of the data set an instance is
 * constructed from. Such cases will be handled by linear interpolation
 * between the nearest value above and nearest value below in
 * the source data set for numeric columns. This does not
 * exactly adhere to the principle of replacing the magnitude
 * of a value with its ordering, but is considered the lesser
 * of evils because it retains variability during the 
 * reverse transformation process.
 *
 * <p>Other column types
 * will be resolved by always mapping to the 'lesser' value
 * (eg. the nearest string in alphabetical order that is less
 * than the string in question, when the native data type
 * is String).
 *
 * <p>In the case of a tie in the forward transformation 
 * (eg there are multiple raw data values of zero), the largest
 * rank (furthest from zero) will be returned.
 *
 * @author pgroves
 * @date 03/29/04
 */

public class RankingTransformation implements ReversibleTransformation,
	Cloneable, java.io.Serializable{

	/**
	 * This table contains the sorted values of each of columns
	 * that are being transformed. 	 */
	MutableTableImpl sortedColumns;

	/**
	 * This holds the sorted order of the original table.
	 * So, if origOrder[i][k] == j, then the value at
	 * column 'i', row 'j' in the original table will
	 * be at position 'k' in the appropriate column when sorted
	 */
	int[][] origOrder;
	
	/**
	 * the columns of the original input table that were transformed
	 * (and are present in <code>sortedColumns</code>
	 */
	int[] transCols;

	/**
	 * Initiallizes the transformation using the sourceTable to generate
	 * rankings of only those columns specified in transformColumns.
	 *
	 * @param sourceTable a table to base the transformation on
	 * @param transformColumns the column indices of those columns
	 * 	that should be transformed
	 */
	public RankingTransformation(Table sourceTable, int[] transformColumns){

		transCols = transformColumns;
		this.initSourceData(sourceTable);
	}

	/**
	 * sets up <code>sortedColumns</code>
	 */
	protected void initSourceData(Table sourceTable){
		int i, j, k;
		int numTransCols = this.transCols.length;
		//we're going to put the smallest and largest
		//possible values in each column, as well
		int numRows = sourceTable.getNumRows();

		Column[] cols = new Column[numTransCols];
		this.sortedColumns = new MutableTableImpl(cols);				

		int[] columnsToSortBy = new int[1];
		this.origOrder = new int[numTransCols][];
		for(i = 0; i < numTransCols; i++){
			//System.out.println("SortedColumn:"+i);
			cols[i] = ColumnUtilities.metaColumnCopy(sourceTable, 
					this.transCols[i], numRows);
			columnsToSortBy[0] = this.transCols[i];
			origOrder[i] = TableUtilities.multiSortIndex(sourceTable, 
					columnsToSortBy);
			for(j = 0; j < numRows; j++){
				
				TableUtilities.setValue(sourceTable, origOrder[i][j], 
						this.transCols[i], this.sortedColumns, j, i);
				//cols[i].setDouble(sourceTable.getDouble(j, this.transCols[i]), 
				//		origOrder[i][j]);
			}
			/*
			for(j = 0; j < numRows; j++){
				System.out.print(sortedColumns.getString(j, i) + ", ");
			}
			System.out.println();
			*/
		}
		
		
	}

	
	public boolean transform(MutableTable table){
		
		int i, j, k;
		int numTransCols = this.transCols.length;
		int numRows = table.getNumRows();

		double rank;
		
		for(i = 0; i < numTransCols; i++){
			for(j = 0; j < numRows; j++){
				try{
					rank = mapForward(table, j, i);
					table.setDouble(rank, j, this.transCols[i]);
				}catch(Exception e){
					e.printStackTrace();
					System.out.println("RankingTransformation Failure");
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * finds the 'rank' value of a value in a table, or the forward
	 * transformation. this class's forward transformation is to
	 * do a binary search of the values that an instance of
	 * this class was constructed with (passed into the constructor),
	 * and to return the smallest rank/orderIndex that is equivalent.
	 * If the exact value is not found, a pseudo-ranking (not a whole
	 * number) will be returned that is a linear interpolation of
	 * the rankings of the values immediately above and below it. If
	 * this is not possible (b/c the data is not a numeric type), the
	 * largest rank for a value that is not greater than the value being
	 * looked up is used.
	 *
	 * <p> The rank of the lowest value in the source table is 1.
	 *
	 * <p> If the value being looked up is less than the smallest
	 * value in the source table, a rank of zero will always
	 * be returned (no interpolation under any circumstances)</p>
	 *
	 * @param tbl the table that contains the value to look up
	 * @param row the row in the table where the value is
	 * @param transColIdx the index into the <code>transCol</code>
	 * set of columns that holds the value
	 *
	 * @return a ranking as a double. this value may not be a whole
	 * number if the exact value if not found in the source data table
	 * and interpolation is possible
	 */
	public double mapForward(Table tbl, int row, int transColIdx){  
		
		//an index of the largest value that does not exceed the 
		//index of the value being looked up, holds the answer to 
		//the binary search
		int largestNotExceeding;
		
		int numSrcRows = this.sortedColumns.getNumRows();
		int leftend = 0;
		int rightend = numSrcRows;
		largestNotExceeding = binarySearch(leftend, rightend,
				tbl, row, transColIdx);
		
		if(largestNotExceeding == -1){
			return 0.0;
		}
		if(largestNotExceeding >= (numSrcRows - 1)){
			return (double)(largestNotExceeding + 1);
		}
		if(!tbl.isColumnNumeric(this.transCols[transColIdx])){
			return (double)largestNotExceeding + 1.0;
		}else{
			int justLargerIdx = largestNotExceeding + 1;
			double justLargerVal = this.sortedColumns.getDouble(justLargerIdx, 
					transColIdx);
			double justSmallerVal = this.sortedColumns.getDouble(
					largestNotExceeding, transColIdx);
			double lookupVal = tbl.getDouble(row, this.transCols[transColIdx]);
			double fracDiff = (lookupVal - justSmallerVal) / (justLargerVal -
					justSmallerVal);
			double pRank = ((double)largestNotExceeding) + fracDiff + 1;
			return pRank;
		}
	}

	/**
	 * finds the index of the largest value that the value of the ranking is
	 * not greater than the value being looked up. only searches
	 * for the value between the given 'ends'.
	 * */
	protected int binarySearch(int leftEnd, int rightEnd, Table tbl, int row,
			int transColIdx){
		//i don't have time to test a binary search right now, so
		//it's gonna have to be a linear lookup for the time being.
		//the untested binary search code is at the end of this method
		//if you're dying for the speed increase and want to try it
		int rank = leftEnd;
		int comparison = -1;
		/*for(rank = leftEnd; rank < rightEnd; rank++){
			comparison = TableUtilities.compareValues(tbl, row, 
				this.transCols[transColIdx], sortedColumns, rank, 
				transColIdx);
			System.out.println("rank: " + rank + ", lookupVal: " + 
					tbl.getString(row, 
						transCols[transColIdx]) + ", searchVal: " +
						sortedColumns.getString(rank, transColIdx) +
						", comp = " + comparison);
			if(comparison < 0){
				break;
			}
		}
		*/
		while(true){
			comparison = TableUtilities.compareValues(tbl, row, 
				this.transCols[transColIdx], sortedColumns, rank, 
				transColIdx);
			/*System.out.println("rank: " + rank + ", lookupVal: " + 
					tbl.getString(row, 
						transCols[transColIdx]) + ", searchVal: " +
						sortedColumns.getString(rank, transColIdx) +
						", comp = " + comparison);
						*/
			if((comparison < 0) || (rank >= (rightEnd - 1))){
				rank--;
				break;
			}else{
				rank++;
			}
		}

		if((rank == -1) && (comparison < 0)){
			System.out.println("Below Zero:" + tbl.getString(row, 
						transCols[transColIdx]));
			rank = -1;
		}
		if(rank == (rightEnd - 2)){
			if(comparison > 0){
				System.out.println("Past End:" + tbl.getString(row, 
						transCols[transColIdx]));

				rank = rightEnd;
			}else if(comparison == 0){
				rank = rightEnd -1;
			}else{
				rank = rightEnd - 2;
			}
		}
		return rank;
		
		/*int midpoint = (int) (((double)(rightEnd - leftEnd)) / 2.0);
		midpoint += leftEnd;
		
		int comparison = TableUtilities.compareValues(tbl, row, 
				this.transCols[transColIdx], sortedColumns, midpoint, 
				transColIdx);
		
		if(comparison > 0){
			if(midpoint == (sortedColumns.getNumRows() - 1)){
				//got to the end without finding it
				return (rightEnd + 1);
			}
			comparison = TableUtilities.compareValues(tbl, row, 
				this.transCols[transColIdx], sortedColumns, midpoint + 1, 
				transColIdx);

			if(comparison < 0){
				//the value is not in the lookup table, return what we have
				return midpoint;
			}
					
			return binarySearch(midpoint, rightEnd, tbl, row, transColIdx);
			
		}else if(comparison < 0){
			if(0 == midpoint){
			//we're at the very beginning and the value is still too large
				return -1;
			}
			return binarySearch(leftEnd, midpoint, tbl, row, transColIdx);
		}

		//comparison equals zero, which means we found it. now find
		//the largest rank for which it is still true. 
		int rank = midpoint;
		while(0 == TableUtilities.compareValues(tbl, row, 
				this.transCols[transColIdx], sortedColumns, rank, 
				transColIdx){
			rank++;
		}
		rank--;
		return rank;
		*/
	}

	
	public boolean untransform(MutableTable table){
		int numTransCols = this.transCols.length;
		int numSortedRows = this.sortedColumns.getNumRows();
		int numRows = table.getNumRows();
		
		int i, j, k;
		double rank, val;
		for(i = 0; i < numTransCols; i++){
			try{
				untransformCol(table, this.transCols[i], i);
			}catch(Exception e){
				e.printStackTrace();
				System.out.println("RankingTransformation: Reverse " +
						"Transform failed at column "+ transCols[i]);
				System.out.println(e);
				return false;
			}
		}					
		if(table instanceof PredictionTable){
			PredictionTable pt = (PredictionTable)table;
			int[] predSet = pt.getPredictionSet();
			if(predSet == null){
				//no predictions, nothing to do
				return true;
			}
			int outCol;
			int predCol;
			int transCol = 0;
			for(i = 0; i < predSet.length; i++){
				predCol = predSet[i];
				outCol = pt.getOutputFeatures()[i];
				
				for(j = 0; j < this.transCols.length; j++){
					if(this.transCols[j] == outCol){
							transCol = j;
						}
				}
				try{
					untransformCol(table, predCol, transCol);
				}catch(Exception e){
					System.out.println("Reverse Transformation of Predictions" +
						" Failed. Continuing Anyway (RankingTransformation)");
				}
			}
			
		}
		return true;
	}

	/**
	 * does the reverse transform on a single column.
	 * 
	 * @param table the table that contains a column to transform
	 * @param tblCol which column to transform
	 * @param transCol which of the transformation columns (in 
	 * 	sortedColumns) to base the reverse transformation on
	 */
	private void untransformCol(MutableTable table, int tblCol, int transCol){
		double rank, val;
		int numRows = table.getNumRows();
		for(int j = 0; j < numRows; j++){
			rank = table.getDouble(j, tblCol);
			if(this.sortedColumns.isColumnNumeric(transCol)){
				val = reverseMap(rank, transCol);
				table.setDouble(val, j, tblCol);
			}else{
				//if not numeric, just return the value at the
				//index that is the floor of the rank
				int index = (int)rank - 1;

				//set ranks outside of the range to the max and min
				if(index < 0)
					index = 0;
				if(index >= sortedColumns.getNumRows())
					index = sortedColumns.getNumRows() - 1;
				
				TableUtilities.setValue(sortedColumns, index, transCol,
					table, j, tblCol);
			}
		}
	}

	/**
	 * given a rank, returns a value corresponding to the original
	 * data set. only works when the original value was numeric,
	 * as an interpolation will be done if the rank is not a whole
	 * number
	 */
	protected double reverseMap(double rank, int transColIdx){
		int justUnderIdx = (int)(rank - 1.0);
		int justOverIdx = justUnderIdx + 1;

		int maxIdx = this.sortedColumns.getNumRows() - 1;
		if(justUnderIdx < 0){
			return this.sortedColumns.getDouble(0, transColIdx);
		}
		if(justUnderIdx >= maxIdx){
			return this.sortedColumns.getDouble(maxIdx, transColIdx);
		}
		double justUnderVal = this.sortedColumns.getDouble(justUnderIdx, 
				transColIdx);
		double justOverVal = this.sortedColumns.getDouble(justOverIdx, 
				transColIdx);
		double diffFrac = rank - ((double)justUnderIdx) - 1;
		double val = justUnderVal + diffFrac * (justOverVal - justUnderVal);
		return val;
	}

}


