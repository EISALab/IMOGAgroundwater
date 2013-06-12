package ncsa.d2k.modules.core.transform.table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import ncsa.d2k.modules.core.datatype.table.Table;
import ncsa.d2k.modules.core.datatype.table.basic.BooleanColumn;
import ncsa.d2k.modules.core.datatype.table.basic.Column;
import ncsa.d2k.modules.core.datatype.table.basic.ContinuousByteArrayColumn;
import ncsa.d2k.modules.core.datatype.table.basic.ContinuousCharArrayColumn;
import ncsa.d2k.modules.core.datatype.table.basic.DoubleColumn;
import ncsa.d2k.modules.core.datatype.table.basic.FloatColumn;
import ncsa.d2k.modules.core.datatype.table.basic.IntColumn;
import ncsa.d2k.modules.core.datatype.table.basic.LongColumn;
import ncsa.d2k.modules.core.datatype.table.basic.MutableTableImpl;
import ncsa.d2k.modules.core.datatype.table.basic.NumericColumn;
import ncsa.d2k.modules.core.datatype.table.basic.ShortColumn;
import ncsa.d2k.modules.core.datatype.table.basic.StringColumn;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class MergingClass {

	public static final String SUM = "Sum";
	public static final String AVE = "Average";
	public static final String MAX = "Maximum";
	public static final String MIN = "Minimum";
	public static final String CNT = "Count";

	public MergingClass() {
	}

	static public MutableTableImpl mergeTable(
		int[] keys,
		int[] merges,
		int control,
		String type,
		Table table) {
		HashMap keyLookup = new HashMap(20);
		// loop through table to find rows where all the key columns are identical

		//for each row
		for (int i = 0; i < table.getNumRows(); i++) {
			// get the keys for this row

			//alocating an array for the keys
			String[] kys = new String[keys.length];
			//for each columns id in keys
			for (int j = 0; j < kys.length; j++) {
				//getting the value at row i
				if (table.isValueMissing(i, keys[j]))
					kys[j] = "?";
				else
					kys[j] = table.getString(i, keys[j]);
			}
			//cereating a set
			KeySet set = new KeySet(kys);
			//if the look up map does nto contain such set yet - adding it with this row ID
			if (!keyLookup.containsKey(set)) {
				ArrayList list = new ArrayList();
				list.add(new Integer(i));
				keyLookup.put(set, list);
			} else {
				//if it does contain this set - reteiving its array list and adding this row ID
				ArrayList list = (ArrayList) keyLookup.get(set);
				list.add(new Integer(i));
				// necessary?
				keyLookup.put(set, list);
			}
		} //for i

		//now KeyLookup contains all the unique keys and their respective row Ids.

		// create the table
		MutableTableImpl newTable = createTable(keyLookup.size(), table);

		int curRow = 0;
		// now convert the array lists to int[]
		Iterator iter = keyLookup.keySet().iterator();
		while (iter.hasNext()) {
			//for each set in keyLookup
			Object key = iter.next();
			//getting it's row IDs
			ArrayList list = (ArrayList) keyLookup.get(key);
			//converting the IDS into an array
			int[] array = new int[list.size()];
			for (int q = 0; q < list.size(); q++)
				array[q] = ((Integer) list.get(q)).intValue();
			//now go ahead and do the merging..
			if (type.equals(MAX))
				mergeMax(newTable, curRow, keys, merges, control, array, table);
			else if (type.equals(MIN))
				mergeMin(newTable, curRow, keys, merges, control, array, table);
			else if (type.equals(AVE))
				mergeAve(newTable, curRow, keys, merges, control, array, table);
			else if (type.equals(SUM))
				mergeSum(newTable, curRow, keys, merges, control, array, table);
			else if (type.equals(CNT))
				mergeCnt(newTable, curRow, keys, merges, control, array, table);
			curRow++;
		}

		// the number of rows of the cleaned table is equal to the number of unique keys..
		return newTable;
	} //mergeTable

	static private MutableTableImpl createTable(int numRows, Table table) {
		Column[] cols = new Column[table.getNumColumns()];
		for (int i = 0; i < table.getNumColumns(); i++) {
			Column c = table.getColumn(i);
			Column newCol = null;
			if (c instanceof IntColumn)
				newCol = new IntColumn(numRows);
			else if (c instanceof StringColumn)
				newCol = new StringColumn(numRows);
			else if (c instanceof FloatColumn)
				newCol = new FloatColumn(numRows);
			else if (c instanceof LongColumn)
				newCol = new LongColumn(numRows);
			else if (c instanceof DoubleColumn)
				newCol = new DoubleColumn(numRows);
			else if (c instanceof BooleanColumn)
				newCol = new BooleanColumn(numRows);
			else if (c instanceof ContinuousCharArrayColumn)
				newCol = new ContinuousCharArrayColumn(numRows);
			else if (c instanceof ContinuousByteArrayColumn)
				newCol = new ContinuousByteArrayColumn(numRows);
			else if (c instanceof ShortColumn)
				newCol = new ShortColumn(numRows);
			else
				newCol = new StringColumn(numRows);
			newCol.setLabel(c.getLabel());
			cols[i] = newCol;
		}

		MutableTableImpl tbl = new MutableTableImpl(cols);
		tbl.setLabel(table.getLabel());
		return tbl;
	} //createTable

	static private void mergeMax(
		MutableTableImpl tbl,
		int rowLoc,
		int[] keys,
		int[] mergeCols,
		int control,
		int[] rows,
		Table table) {
		// find the maximum in the control column.  this row will be the one
		// where data is copied from

		int maxRow = rows[0];
		double maxVal;
		if (table.isValueMissing(rows[0], control))
			maxVal = Double.MIN_VALUE;
		else
			maxVal = table.getDouble(rows[0], control);
		for (int i = 1; i < rows.length; i++) {
			if (!table.isValueMissing(rows[i], control) &&
					table.getDouble(rows[i], control) > maxVal) {
				maxVal = table.getDouble(rows[i], control);
				maxRow = rows[i];
			}
		}

		// copy all the row data in
		for (int i = 0; i < tbl.getNumColumns(); i++) {
			Column c = tbl.getColumn(i);
			if (c instanceof NumericColumn)
				tbl.setDouble(table.getDouble(maxRow, i), rowLoc, i);
			else
				tbl.setString(table.getString(maxRow, i), rowLoc, i);
			tbl.setValueToMissing(table.isValueMissing(maxRow, i), rowLoc, i);
		}
	}

	static private void mergeMin(
		MutableTableImpl tbl,
		int rowLoc,
		int[] keys,
		int[] mergeCols,
		int control,
		int[] rows,
		Table table) {
		// find the maximum in the control column.  this row will be the one
		// where data is copied from

		int maxRow = rows[0];
		double maxVal;
		if (table.isValueMissing(rows[0], control))
			maxVal = Double.MIN_VALUE;
		else
			maxVal = table.getDouble(rows[0], control);
		for (int i = 1; i < rows.length; i++) {
			if (!table.isValueMissing(rows[i], control) &&
					table.getDouble(rows[i], control) > maxVal) {
				maxVal = table.getDouble(rows[i], control);
				maxRow = rows[i];
			}
		}

		// copy all the row data in
		for (int i = 0; i < tbl.getNumColumns(); i++) {
			Column c = tbl.getColumn(i);
			if (c instanceof NumericColumn)
				tbl.setDouble(table.getDouble(maxRow, i), rowLoc, i);
			else
				tbl.setString(table.getString(maxRow, i), rowLoc, i);
			tbl.setValueToMissing(table.isValueMissing(maxRow, i), rowLoc, i);
		}

		// now find the minimum
		for (int i = 0; i < mergeCols.length; i++) {
			double minimum = 0;
			for (int j = 0; j < rows.length; j++) {
				if (!table.isValueMissing(rows[j], mergeCols[i]))
					if (j == 0) {
						minimum = table.getDouble(rows[j], mergeCols[i]);
					} else {
						double testVal = table.getDouble(rows[j], mergeCols[i]);
						if (testVal < minimum) {
							minimum = testVal;
						}
					}
			}
			tbl.setDouble(minimum, rowLoc, mergeCols[i]);
			tbl.setValueToMissing(false, rowLoc, mergeCols[i]);
		}
	}

	static private void mergeAve(
		MutableTableImpl tbl,
		int rowLoc,
		int[] keys,
		int[] mergeCols,
		int control,
		int[] rows,
		Table table) {
		// find the maximum in the control column.  this row will be the one
		// where data is copied from

		int maxRow = rows[0];
		double maxVal;
		if (table.isValueMissing(rows[0], control))
			maxVal = Double.MIN_VALUE;
		else
			maxVal = table.getDouble(rows[0], control);
		for (int i = 1; i < rows.length; i++) {
			if (!table.isValueMissing(rows[i], control) &&
					table.getDouble(rows[i], control) > maxVal) {
				maxVal = table.getDouble(rows[i], control);
				maxRow = rows[i];
			}
		}

		// copy all the row data in
		for (int i = 0; i < tbl.getNumColumns(); i++) {
			Column c = tbl.getColumn(i);
			if (c instanceof NumericColumn)
				tbl.setDouble(table.getDouble(maxRow, i), rowLoc, i);
			else
				tbl.setString(table.getString(maxRow, i), rowLoc, i);
			tbl.setValueToMissing(table.isValueMissing(maxRow, i), rowLoc, i);
		}

		// now find the averages
		for (int i = 0; i < mergeCols.length; i++) {
			double sums = 0;
			int num = rows.length;
			for (int j = 0; j < rows.length; j++) {
				if (!table.isValueMissing(rows[j], mergeCols[i]))
					sums += table.getDouble(rows[j], mergeCols[i]);
				else
					num--;
			}
			tbl.setDouble(sums / (double) num, rowLoc, mergeCols[i]);
			tbl.setValueToMissing(false, rowLoc, mergeCols[i]);
		}
	}

	static private void mergeSum(
		MutableTableImpl tbl,
		int rowLoc,
		int[] keys,
		int[] mergeCols,
		int control,
		int[] rows,
		Table table) {
		// find the maximum in the control column.  this row will be the one
		// where data is copied from

		int maxRow = rows[0];
		double maxVal;
		if (table.isValueMissing(rows[0], control))
			maxVal = Double.MIN_VALUE;
		else
			maxVal = table.getDouble(rows[0], control);
		for (int i = 1; i < rows.length; i++) {
			if (!table.isValueMissing(rows[i], control) &&
					table.getDouble(rows[i], control) > maxVal) {
				maxVal = table.getDouble(rows[i], control);
				maxRow = rows[i];
			}
		}

		// copy all the row data in
		for (int i = 0; i < tbl.getNumColumns(); i++) {
			Column c = tbl.getColumn(i);
			if (c instanceof NumericColumn)
				tbl.setDouble(table.getDouble(maxRow, i), rowLoc, i);
			else
				tbl.setString(table.getString(maxRow, i), rowLoc, i);
			tbl.setValueToMissing(table.isValueMissing(maxRow, i), rowLoc, i);
		}

		// now find the sums
		for (int i = 0; i < mergeCols.length; i++) {
			double sums = 0;
			for (int j = 0; j < rows.length; j++) {
				if (!table.isValueMissing(rows[j],mergeCols[i]))
					sums += table.getDouble(rows[j], mergeCols[i]);
			}
			tbl.setDouble(sums, rowLoc, mergeCols[i]);
			tbl.setValueToMissing(false, rowLoc, mergeCols[i]);
		}
	}

	static private void mergeCnt(
		MutableTableImpl tbl,
		int rowLoc,
		int[] keys,
		int[] mergeCols,
		int control,
		int[] rows,
		Table table) {
		// find the maximum in the control column.  this row will be the one
		// where data is copied from

		int maxRow = rows[0];
		double maxVal;
		if (table.isValueMissing(rows[0], control))
			maxVal = Double.MIN_VALUE;
		else
			maxVal = table.getDouble(rows[0], control);
		for (int i = 1; i < rows.length; i++) {
			if (!table.isValueMissing(rows[i], control) &&
					table.getDouble(rows[i], control) > maxVal) {
				maxVal = table.getDouble(rows[i], control);
				maxRow = rows[i];
			}
		}

		// copy all the row data in
		for (int i = 0; i < tbl.getNumColumns(); i++) {
			Column c = tbl.getColumn(i);
			if (c instanceof NumericColumn)
				tbl.setDouble(table.getDouble(maxRow, i), rowLoc, i);
			else
				tbl.setString(table.getString(maxRow, i), rowLoc, i);
			tbl.setValueToMissing(table.isValueMissing(maxRow, i), rowLoc, i);
		}

		// the count is the number of rows - write than in each column
		// that's being merged.
		int cnt = rows.length;
		for (int i = 0; i < mergeCols.length; i++) {
			tbl.setDouble(cnt, rowLoc, mergeCols[i]);
			tbl.setValueToMissing(false, rowLoc, mergeCols[i]);
		}
	}

} //MergingClass