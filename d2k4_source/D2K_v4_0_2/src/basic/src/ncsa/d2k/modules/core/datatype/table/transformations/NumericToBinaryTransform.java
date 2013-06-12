package ncsa.d2k.modules.core.datatype.table.transformations;

import ncsa.d2k.modules.core.datatype.table.ExampleTable;
import ncsa.d2k.modules.core.datatype.table.MutableTable;
import ncsa.d2k.modules.core.datatype.table.Transformation;
import ncsa.d2k.modules.core.datatype.table.basic.*;
/**
 * NumericToBinaryTransform encapsulates a transformation on a Table.
 * If the value of the numeric column is zero it will remain zero,
 * if the value is missing it will remain missing, and if none of the above are 
 * true the value will be one.
 * Columns are replaced and the table is changed. Transformation is not reversible.
 */
public class NumericToBinaryTransform implements Transformation, Cloneable {

	private int[] columns;

	/**
	 * Create a new NumericToBinaryTransform.
	 */
	public NumericToBinaryTransform(int[] selectedColumns) {
		columns = selectedColumns;
	}

	public boolean transform(MutableTable mt) {

		int[] outputFeatures = ((ExampleTable) mt).getOutputFeatures();
		int classIndex = outputFeatures[0];
		int numRows = mt.getNumRows();
		int[] intColumn = new int[numRows];
		String label;

		for (int col = 0; col < columns.length; col++) {
			if (mt.isColumnScalar(col) && columns[col] != classIndex) {
				for (int j = 0; j < numRows; j++) {
					if (mt.isValueMissing(j, col) || mt.getInt(j, col) == 0) {
						intColumn[j] = mt.getInt(j, col);
					} else {
						intColumn[j] = 1;
					}
				}
				label = mt.getColumnLabel(col);
				IntColumn iCol = new IntColumn(intColumn);
				mt.setColumn(iCol, col);
				mt.setColumnLabel(label, col);
				mt.setColumnIsNominal(false, col);
				mt.setColumnIsScalar(true, col);

			}
		}

		// 4/7/02 commented out by Loretta...
		// this add gets done by applyTransformation
		//mt.addTransformation(this);
		return true;
	}

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

}