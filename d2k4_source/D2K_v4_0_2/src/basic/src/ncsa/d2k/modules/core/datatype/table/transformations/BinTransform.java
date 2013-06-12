package ncsa.d2k.modules.core.datatype.table.transformations;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import java.util.*;
import ncsa.d2k.modules.core.transform.binning.*;

/**
 * BinTransform encapsulates a binning transformation on a Table.
 */
public class BinTransform implements Transformation, Cloneable {
	private BinDescriptor[] bins;
	private boolean new_column;
	private static final String UNKNOWN = "Unknown";
	private static final String BIN = " bin";

	/**
	 * Create a new BinTransform.
	 * @param b The BinDescriptors
	 * @param new_col true if a new column should be constructed for each
	 *  binned column, false if the original column should be overwritten
	 */
	public BinTransform(Table tbl, BinDescriptor[] b, boolean new_col) {
		if (tbl == null) // no missing values information is provided
				bins = b;
		else		//missing values info is contained in tbl and "unknown" bins are added for relevant atrbs
		 bins = BinningUtils.addMissingValueBins(tbl,b);
		
		System.out.println("bins.length BinTransform " + bins.length);
		new_column = new_col;
		
	}

	/**
	 * Bin the columns of a MutableTable.
	 * @param mt the table to bin
	 * @return true if the transformation was sucessful, false otherwise
	 */
	public boolean transform(MutableTable mt) {
		HashMap colIndexLookup = new HashMap(mt.getNumColumns());
		for (int i = 0; i < mt.getNumColumns(); i++) {
			colIndexLookup.put(mt.getColumnLabel(i), new Integer(i));
		}

		// need to figure out which columns have been binned:
		boolean[] binRelevant = new boolean[mt.getNumColumns()];
		for (int i = 0; i < binRelevant.length; i++)
			binRelevant[i] = false;
		for (int i = 0; i < bins.length; i++) {
			Integer idx = (Integer) colIndexLookup.get(bins[i].label);
			if (idx != null) {
                int vv = idx.intValue();
				binRelevant[vv] = true;
			}
		}

		String[][] newcols = new String[mt.getNumColumns()][mt.getNumRows()];
		for (int i = 0; i < mt.getNumColumns(); i++) {
			if (binRelevant[i])
				for (int j = 0; j < mt.getNumRows(); j++) {
					// find the correct bin for this column
					boolean binfound = false;
					for (int k = 0; k < bins.length; k++) {
						if (((Integer) colIndexLookup.get(bins[k].label)).intValue() == i) {
							// this has the correct column index
							if (mt.isColumnScalar(i)) {
								if (mt.isValueMissing(j, i))
									binfound = false;
								else if (bins[k].eval(mt.getDouble(j, i))) {
									newcols[i][j] = bins[k].name;
									binfound = true;
								}
							} else {
								if (mt.isValueMissing(j, i)){
									binfound = false;
								}
								else if (bins[k].eval(mt.getString(j, i))) {
									newcols[i][j] = bins[k].name;
									binfound = true;
								}
							}
						}
						if (binfound) {
							binRelevant[i] = true;
							break;
						}
					}
					if (!binfound)
						newcols[i][j] = UNKNOWN;
				}
		}

		// Construct the new columns
		// 1/7/04 TLR - I changed this so it would work correctly with subset tables.
		// Now, we use the column utility to duplicate the original column as a string
		// column. Once we have the duplicated column, we replace the entries in the column
		// with the bin indicator strings by using the methods of the table so the subset
		// will still apply.
		int numColumns = mt.getNumColumns();
		for (int i = 0; i < numColumns; i++) {
			if (binRelevant[i]) {
				
				// Create a new column of type string containing the string rep
				// of the original data.
				int ci = i;
				StringColumn sc = ColumnUtilities.toStringColumn(mt.getColumn(i));
                sc.setLabel(mt.getColumn(i).getLabel()+"_bin");
				if (new_column) {
					mt.addColumn(sc);
					ci = mt.getNumColumns()-1;
				} else {
					mt.setColumn(sc, i);					
				}
				
				// Set the strings, use the method of the table, in case it is
				// a subset table.
				for (int row = 0 ; row < mt.getNumRows() ; row++)
					mt.setString (newcols[i][row], row, ci);
			}
		}
		return true;
	}

	public BinDescriptor[] getBinDescriptors() {
		return bins;

	}

	
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

}