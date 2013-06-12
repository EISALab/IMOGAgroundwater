package ncsa.d2k.modules.core.transform.table;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.datatype.table.util.*;
import java.util.*;

public class AppendColumnsByKey extends ncsa.d2k.core.modules.DataPrepModule {
	//begin setting Properties
/*
 *
 	String fillerString = new String("*");
	int fillerNumeric = 0;
	boolean fillerBol = false;
	byte[] fillerByte = new byte[1];
	char[] fillerChar = new char[1];
	byte fillerSingleByte = '\000';
	char fillerSingleChar = '\000';

	public void setFillerString(String x){
		fillerString = x;
	}
	public String getFillerString(){
		return fillerString;
	}

	public void setFillerNumeric(int b){
		fillerNumeric = b;
	}
	public int getFillerNumeric(){
		return fillerNumeric;
	}

	public void setFillerBol(boolean a){
		fillerBol = a;
	}
	public boolean getFillerBol(){
		return fillerBol;
	}

	public void setFillerBytes(byte[] c){
		fillerByte = c;
	}
	public byte[] getFillerBytes(){
		return fillerByte;
	}

	public void setFillerChars(char[] d){
		fillerChar = d;
	}
	public char[] getFillerChars(){
		return fillerChar;
	}

	public void setFillerChar(char d){
		fillerSingleChar = d;
	}
	public char getFillerChar(){
		return fillerSingleChar;
	}

	public void setFillerByte(byte d){
		fillerSingleByte = d;
	}
	public byte getFillerByte(){
		return fillerSingleByte;
	}

	/**
	 * Return a list of the property descriptions.
	 * @return a list of the property descriptions.

	public PropertyDescription [] getPropertiesDescriptions () {
		PropertyDescription [] pds = new PropertyDescription [3];
		pds[0] = new PropertyDescription ("fillerBol", "Boolean Column Filler", "This value fills boolean columns.");
		pds[1] = new PropertyDescription ("fillerString", "String Column Filler", "This string fills the string columns.");
		pds[2] = new PropertyDescription ("fillerNumeric", "Numeric Column Filler", "This value fills any numeric columns.");
		return pds;
	}

	/**
	 * returns information about the input at the given index.
	 * @return information about the input at the given index.
	 */
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "<p>      First of two tables to append together.    </p>";
			case 1: return "<p>      Second of two tables to append together.    </p>";
			case 2: return "<p>      This is the name of the key column whose values will be used to match       rows in the first table to rows in the second.    </p>";
			default: return "No such input";
		}
	}

	/**
	 * returns information about the output at the given index.
	 * @return information about the output at the given index.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "First Table";
			case 1:
				return "Second Table";
			case 2:
				return "Key Column Name";
			default: return "NO SUCH INPUT!";
		}
	}

	/**
	 * returns string array containing the datatypes of the inputs.
	 * @return string array containing the datatypes of the inputs.
	 */
	public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.Table","ncsa.d2k.modules.core.datatype.table.Table","java.lang.String"};
		return types;
	}

	/**
	 * returns information about the output at the given index.
	 * @return information about the output at the given index.
	 */
	public String getOutputInfo(int index) {
		switch (index) {
			case 0: return "<p>      Newly created table containing contents of both original tables.    </p>";
			default: return "No such output";
		}
	}

	/**
	 * returns information about the output at the given index.
	 * @return information about the output at the given index.
	 */
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Result Table";
			default: return "NO SUCH OUTPUT!";
		}
	}

	/**
	 * returns string array containing the datatypes of the outputs.
	 * @return string array containing the datatypes of the outputs.
	 */
	public String[] getOutputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.Table"};
		return types;
	}

	/**
	 * returns the information about the module.
	 * @return the information about the module.
	 */
	public String getModuleInfo() {
		return "<p>      Overview: This module will append the columns in the second table to the       columns"+
			" in the first table to produce a third table.    </p>    <p>      Detailed Description: Each"+
			" row in the resulting data consists of data       from one or both of the input tables. If there"+
			" is a row in each of the       original tables that shared a value in their key column,"+
			" then the       data from the columns in first input table are inserted into the       resulting"+
			" row first, then any unique columns from the second table are       appended to the row. If"+
			"  a row in the first table has no       corresponding row in the second table, then the columns"+
			" that exist only       in the second table are assigned filler values. For rows that exist only"+
			"       in the second table, the columns of the first table are assigned the       filler values."+
			" And for columns that exist in both tables, the values       assigned to the result table will"+
			" come from the first table.    </p> " +
                        "<P>Missing Values Handling: Key columns should be clean of missing values. If a missing value is "+
                       "encountered, an Exception will be thrown. Use 'RemoveRowsWithMissingValues' module " +
           "before this module, to clean the input tables.</P>"+

                     "   <p>      Scalability: A hashtable consisting of all the"+
			" unique values in the key       column has to be constructed to identify rows in the two tables"+
			" that       match. If the table has large number of rows, this may be prohibitive.    </p>";
	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "Append Columns";
	}

	/**
	 * This method will return an array of int arrays, where each int array contains the
	 * corresponding row indices of the matching rows in the two tables..
	 * @param t1 first table.
	 * @param k1 first tables key column index.
	 * @param t2 second table.
	 * @param k2 the second tables key column index.
	 * @return a map of corresponding rows in the two tables.
	 */
	private int [][] matchRows(Table t1, int k1, Table t2, int k2) {

		// We will find corresponding row in the second table by looking
		// in a hashmap of the values found there.
		HashMap valueMap = new HashMap();
		for (int i = 0 ; i < t2.getNumRows (); i++) {
			valueMap.put (t2.getString(i, k2), new Integer(i));
		}

		int realCount = t1.getNumRows();
		int [][] rowMap = new int [realCount][2];

		// For each row in the resulting table, determine what the matching rows
		// from the two existing tables are.
		for (int i = 0 ; i < realCount ; i++) {
			rowMap[i][0] = i;

			// Is there a corresponding row in table two?
			Integer tmp = (Integer) valueMap.get(t1.getString(i, k1));
			if (tmp == null)
				rowMap[i][1] = -1;
			else {
				rowMap[i][1] = tmp.intValue();
				valueMap.remove(t1.getString(i, k1));
			}
		}

		// Now get all the rows in the second table that had no match, add
		// them to the end as new rows.
		int rowsUniqueInSecond = valueMap.values().size();
		int [][] finalRowMap = new int [realCount+rowsUniqueInSecond][2];
		System.arraycopy(rowMap, 0, finalRowMap, 0, realCount);

		Iterator iter = valueMap.values().iterator();
		Iterator iter2 = valueMap.keySet().iterator();
		while (iter.hasNext()) {
			Integer tmp = (Integer) iter.next();
			finalRowMap[realCount][0] = -1;
			finalRowMap[realCount][1] = tmp.intValue();
			realCount++;
		}
		return finalRowMap;
	}

	/**
	 * append table one to table two.
	 * @throws Exception
	 */
	public void doit() throws Exception {
		Table t1 = (Table)this.pullInput(0);
		Table t2 = (Table)this.pullInput(1);
		String key = (String) this.pullInput(2);

		// First, hash the column names with the contents of that column.
		HashMap colMap1 = new HashMap();
		ArrayList columnNames = new ArrayList();
		for (int i = 0 ; i < t1.getNumColumns() ; i++) {
			colMap1.put(t1.getColumnLabel(i),new Integer(i));
			columnNames.add(t1.getColumnLabel(i));
		}

		// Hash the column names with the contents of the second table.
		HashMap colMap2 = new HashMap();
		for (int i = 0 ; i < t2.getNumColumns() ; i++) {
			String label = t2.getColumnLabel(i);
			colMap2.put(label, new Integer(i));
			if (colMap1.get(label) == null)
				columnNames.add(label);
		}

		Integer keyColumnObj1 = (Integer) colMap1.get(key);
		Integer keyColumnObj2 = (Integer) colMap2.get(key);
		if (keyColumnObj1 == null) throw new Exception("The key column named "+key+" did not exist in table one.");
		if (keyColumnObj2 == null) throw new Exception("The key column named "+key+" did not exist in table two.");
		int keyColumn1 = keyColumnObj1.intValue();
		int keyColumn2 = keyColumnObj2.intValue();
		boolean isKeyColumn1 = TableUtilities.isKeyColumn(t1,keyColumn1);
		boolean isKeyColumn2 = TableUtilities.isKeyColumn(t2,keyColumn2);
		if(!isKeyColumn1)
		    throw new Exception ("The column named " + key + " is not a key column in table one.");
		if(!isKeyColumn2)
		    throw new Exception ("The column named " + key + " is not a key column in table two.");



		// find the matching rows.
		int [][] matches = this.matchRows(t1, keyColumn1, t2, keyColumn2);

		// Now, append each column of the first table to each column of the second
		// table to create a new column.
		MutableTable result = (MutableTable) t1.createTable();
		int numRows = matches.length;
		int numColumns = columnNames.size();
		for (int i = 0; i < numColumns; i++) {
			Integer t = (Integer) colMap1.get(columnNames.get(i));
			int firstColumn = t == null ? -1 : t.intValue();
			t = (Integer) colMap2.get(columnNames.get(i));
			int secondColumn = t == null ? -1 : t.intValue();

			// Get the type from the column.
			int type = secondColumn == -1 ? t1.getColumnType(firstColumn) : t2.getColumnType(secondColumn);

			// Construct each column from scratch.
			boolean [] missing = new boolean [numRows];
			switch (type) {
				case ColumnTypes.INTEGER: {

					// Allocate an array to hold the new data
					int [] vals = new int [numRows];
					for (int row_index = 0 ; row_index < numRows ; row_index++) {
						if (matches[row_index][0] != -1)
							if (firstColumn == -1) {
								if (matches[row_index][1] == -1 || secondColumn == -1) {
									vals[row_index] = t1.getMissingInt();//this.getFillerNumeric();
									missing[row_index] = true;
								} else {
									vals[row_index] = t2.getInt(matches[row_index][1], secondColumn);
									missing[row_index] = t2.isValueMissing(matches[row_index][1], secondColumn);
								}
							} else {
								vals[row_index] = t1.getInt(matches[row_index][0], firstColumn);
								missing[row_index] = t1.isValueMissing(matches[row_index][0], firstColumn);
							}
						else {
							if (secondColumn == -1) {
								vals[row_index] = t1.getMissingInt();//this.getFillerNumeric();
								missing[row_index] = true;
							} else {
								vals[row_index] = t2.getInt(matches[row_index][1], secondColumn);
								missing[row_index] = t2.isValueMissing(matches[row_index][1], secondColumn);
							}
						}
					}

					// add the column
					Column col = new IntColumn(vals);
					for (int ri = 0 ; ri < missing.length ; ri++){
						col.setValueToMissing(missing[ri],ri);
						if (missing[ri])
							col.setInt(t1.getMissingInt(),ri);
					}
					result.addColumn(col);
					break;
				}
				case ColumnTypes.FLOAT: {

					// get the data from table 2, put it first.
					float [] vals = new float [numRows];
					for (int row_index = 0 ; row_index < numRows ; row_index++) {
						if (matches[row_index][0] != -1)
							if (firstColumn == -1) {
								if (matches[row_index][1] == -1 || secondColumn == -1) {
									vals[row_index] = (float)t1.getMissingDouble();//this.getFillerNumeric();
									missing[row_index] = true;
								} else {
									vals[row_index] = t2.getFloat(matches[row_index][1], secondColumn);
									missing[row_index] = t2.isValueMissing(matches[row_index][1], secondColumn);
								}
							} else {
								vals[row_index] = t1.getFloat(matches[row_index][0], firstColumn);
								missing[row_index] = t1.isValueMissing(matches[row_index][0], firstColumn);
							}
						else {
							if (secondColumn == -1) {
								vals[row_index] = (float) t1.getMissingDouble();//this.getFillerNumeric();
								missing[row_index] = true;
							} else {
								vals[row_index] = t2.getFloat(matches[row_index][1], secondColumn);
								missing[row_index] = t2.isValueMissing(matches[row_index][1], secondColumn);
							}
						}
					}

					// add the column
					Column col = new FloatColumn(vals);
					for (int ri = 0 ; ri < missing.length ; ri++){
						col.setValueToMissing(missing[ri],ri);
						if (missing[ri])
							col.setDouble(t1.getMissingDouble(),ri);
					}
					result.addColumn(col);
					break;
				}
				case ColumnTypes.DOUBLE: {

					// get the data from table 2, put it first.
					double [] vals = new double [numRows];
					for (int row_index = 0 ; row_index < numRows ; row_index++) {
						if (matches[row_index][0] != -1)
							if (firstColumn == -1) {
								if (matches[row_index][1] == -1 || secondColumn == -1) {
									vals[row_index] = t1.getMissingDouble();//this.getFillerNumeric();
									missing[row_index] = true;
								} else {
									 vals[row_index] = t2.getDouble(matches[row_index][1], secondColumn);
									missing[row_index] = t2.isValueMissing(matches[row_index][1], secondColumn);
								}
							} else {
								vals[row_index] = t1.getDouble(matches[row_index][0], firstColumn);
								missing[row_index] = t1.isValueMissing(matches[row_index][0], firstColumn);
							}
						else {
							if (secondColumn == -1) {
								vals[row_index] = t1.getMissingDouble();//this.getFillerNumeric();
								missing[row_index] = true;
							} else {
								vals[row_index] = t2.getDouble(matches[row_index][1], secondColumn);
								missing[row_index] = t2.isValueMissing(matches[row_index][1], secondColumn);
							}
						}
					}

					// add the column
					Column col = new DoubleColumn(vals);
					for (int ri = 0 ; ri < missing.length ; ri++){
						col.setValueToMissing(missing[ri],ri);
						if (missing[ri])
							col.setDouble(t1.getMissingDouble(),ri);
					}
					result.addColumn(col);
					break;
				}
				case ColumnTypes.SHORT: {

					// get the data from table 2, put it first.
					short [] vals = new short [numRows];
					for (int row_index = 0 ; row_index < numRows ; row_index++) {
						if (matches[row_index][0] != -1)
							if (firstColumn == -1) {
								if (matches[row_index][1] == -1 || secondColumn == -1) {
									vals[row_index] = (short)t1.getMissingInt();// this.getFillerNumeric();
									missing[row_index] = true;
								} else{
									vals[row_index] = t2.getShort(matches[row_index][1], secondColumn);
									missing[row_index] = t2.isValueMissing(matches[row_index][1], secondColumn);
								}
							} else {
								vals[row_index] = t1.getShort(matches[row_index][0], firstColumn);
								missing[row_index] = t1.isValueMissing(matches[row_index][0], firstColumn);
							}
						else {
							if (secondColumn == -1) {
								vals[row_index] = (short)t1.getMissingInt();// this.getFillerNumeric();
								missing[row_index] = true;
							} else {
								vals[row_index] = t2.getShort(matches[row_index][1], secondColumn);
								missing[row_index] = t2.isValueMissing(matches[row_index][1], secondColumn);
							}
						}
					}

					// add the column
					Column col = new ShortColumn(vals);
					for (int ri = 0 ; ri < missing.length ; ri++){
						col.setValueToMissing(missing[ri],ri);
						if (missing[ri])
							col.setInt(t1.getMissingInt(),ri);
					}
					result.addColumn(col);
					break;
				}
				case ColumnTypes.LONG: {

					// get the data from table 2, put it first.
					long [] vals = new long [numRows];
					for (int row_index = 0 ; row_index < numRows ; row_index++) {
						if (matches[row_index][0] != -1)
							if (firstColumn == -1) {
								if (matches[row_index][1] == -1 || secondColumn == -1) {
									vals[row_index] = t1.getMissingInt();//this.getFillerNumeric();
									missing[row_index] = true;
								} else {
									vals[row_index] = t2.getLong(matches[row_index][1], secondColumn);
									missing[row_index] = t2.isValueMissing(matches[row_index][1], secondColumn);
								}
							} else {
								vals[row_index] = t1.getLong(matches[row_index][0], firstColumn);
								missing[row_index] = t1.isValueMissing(matches[row_index][0], firstColumn);
							}
						else {
							if (secondColumn == -1) {
								vals[row_index] = t1.getMissingInt();//this.getFillerNumeric();
								missing[row_index] = true;
							} else {
								vals[row_index] = t2.getLong(matches[row_index][1], secondColumn);
								missing[row_index] = t2.isValueMissing(matches[row_index][1], secondColumn);
							}
						}
					}

					// add the column
					Column col = new LongColumn(vals);
					for (int ri = 0 ; ri < missing.length ; ri++){
						col.setValueToMissing(missing[ri],ri);
						if (missing[ri])
							col.setInt(t1.getMissingInt(),ri);
					}
					result.addColumn(col);
					break;
				}
				case ColumnTypes.STRING: {

					// get the data from table 2, put it first.
					String [] vals = new String [numRows];
					for (int row_index = 0 ; row_index < numRows ; row_index++) {
						if (matches[row_index][0] != -1)
							if (firstColumn == -1) {
								if (matches[row_index][1] == -1 || secondColumn == -1){
									vals[row_index] = t1.getMissingString();//this.getFillerString();
									missing[row_index] = true;
								} else {
									vals[row_index] = t2.getString(matches[row_index][1], secondColumn);
									missing[row_index] = t2.isValueMissing(matches[row_index][1], secondColumn);
								}
							} else {
								vals[row_index] = t1.getString(matches[row_index][0], firstColumn);
								missing[row_index] = t1.isValueMissing(matches[row_index][0], firstColumn);
							}
						else {
							if (secondColumn == -1) {
								vals[row_index] = t1.getMissingString();//this.getFillerString();
								missing[row_index] = true;
							} else {
								vals[row_index] = t2.getString(matches[row_index][1], secondColumn);
								missing[row_index] = t2.isValueMissing(matches[row_index][1], secondColumn);
							}
						}
					}

					// add the column
					Column col = new StringColumn(vals);
					for (int ri = 0 ; ri < missing.length ; ri++){
						col.setValueToMissing(missing[ri],ri);
						if (missing[ri])
							col.setString(t1.getMissingString(),ri);
					}
					result.addColumn(col);
					break;
				}
				case ColumnTypes.CHAR_ARRAY: {

					// get the data from table 2, put it first.
					char [][] vals = new char [numRows][];
					for (int row_index = 0 ; row_index < numRows ; row_index++) {
						if (matches[row_index][0] != -1)
							if (firstColumn == -1) {
								if (matches[row_index][1] == -1 || secondColumn == -1) {
									vals[row_index] = t1.getMissingChars();//this.getFillerChars();
									missing[row_index] = true;
								} else {
									vals[row_index] = t2.getChars(matches[row_index][1], secondColumn);
									missing[row_index] = t2.isValueMissing(matches[row_index][1], secondColumn);
								}
							} else {
								vals[row_index] = t1.getChars(matches[row_index][0], firstColumn);
								missing[row_index] = t1.isValueMissing(matches[row_index][0], firstColumn);
							}
						else {
							if (secondColumn == -1) {
								vals[row_index] = t1.getMissingChars();//this.getFillerChars();
								missing[row_index] = true;
							} else {
								vals[row_index] = t2.getChars(matches[row_index][1], secondColumn);
								missing[row_index] = t2.isValueMissing(matches[row_index][1], secondColumn);
							}
						}
					}

					// add the column
					Column col = new CharArrayColumn(vals);
					for (int ri = 0 ; ri < missing.length ; ri++){
						col.setValueToMissing(missing[ri],ri);
						if (missing[ri])
							col.setChars(t1.getMissingChars(),ri);
					}
					result.addColumn(col);
					break;
				}
				case ColumnTypes.BYTE_ARRAY: {

					// get the data from table 2, put it first.
					byte [][] vals = new byte [numRows][];
					for (int row_index = 0 ; row_index < numRows ; row_index++) {
						if (matches[row_index][0] != -1)
							if (firstColumn == -1) {
								if (matches[row_index][1] == -1 || secondColumn == -1) {
									vals[row_index] = t1.getMissingBytes();//this.getFillerBytes();
									missing[row_index] = true;
								} else {
									vals[row_index] = t2.getBytes(matches[row_index][1], secondColumn);
									missing[row_index] = t2.isValueMissing(matches[row_index][1], secondColumn);
								}
							} else {
								vals[row_index] = t1.getBytes(matches[row_index][0], firstColumn);
								missing[row_index] = t1.isValueMissing(matches[row_index][0], firstColumn);
							}
						else {
							if (secondColumn == -1) {
								vals[row_index] = t1.getMissingBytes();//this.getFillerBytes();
								missing[row_index] = true;
							} else {
								vals[row_index] = t2.getBytes(matches[row_index][1], secondColumn);
								missing[row_index] = t2.isValueMissing(matches[row_index][1], secondColumn);
							}
						}
					}

					// add the column
					Column col = new ByteArrayColumn(vals);
					for (int ri = 0 ; ri < missing.length ; ri++){
						col.setValueToMissing(missing[ri],ri);
						if (missing[ri])
							col.setBytes(t1.getMissingBytes(),ri);
					}
					result.addColumn(col);
					break;
				}
				case ColumnTypes.BOOLEAN: {

					// get the data from table 2, put it first.
					boolean [] vals = new boolean [numRows];
					for (int row_index = 0 ; row_index < numRows ; row_index++) {
						if (matches[row_index][0] != -1)
							if (firstColumn == -1) {
								if (matches[row_index][1] == -1 || secondColumn == -1) {
									vals[row_index] = t1.getMissingBoolean();//this.getFillerBol();
									missing[row_index] = true;
								} else {
									vals[row_index] = t2.getBoolean(matches[row_index][1], secondColumn);
									missing[row_index] = t2.isValueMissing(matches[row_index][1], secondColumn);
								}
							} else {
								vals[row_index] = t1.getBoolean(matches[row_index][0], firstColumn);
								missing[row_index] = t1.isValueMissing(matches[row_index][0], firstColumn);
							}
						else {
							if (secondColumn == -1) {
								vals[row_index] = t1.getMissingBoolean();//this.getFillerBol();
								missing[row_index] = true;
							} else {
								vals[row_index] = t2.getBoolean(matches[row_index][1], secondColumn);
								missing[row_index] = t2.isValueMissing(matches[row_index][1], secondColumn);
							}
						}
					}

					// add the column
					Column col = new BooleanColumn(vals);
					for (int ri = 0 ; ri < missing.length ; ri++){
						col.setValueToMissing(missing[ri],ri);
						if (missing[ri])
							col.setBoolean(t1.getMissingBoolean(),ri);
					}
					result.addColumn(col);
					break;
				}
				case ColumnTypes.OBJECT: {

					// get the data from table 2, put it first.
					Object [] vals = new Object [numRows];
					for (int row_index = 0 ; row_index < numRows ; row_index++) {
						if (matches[row_index][0] != -1)
							if (firstColumn == -1) {
								if (matches[row_index][1] == -1 || secondColumn == -1) {
									vals[row_index] = null;
									missing[row_index] = true;
								} else {
									vals[row_index] = t2.getObject(matches[row_index][1], secondColumn);
									missing[row_index] = t2.isValueMissing(matches[row_index][1], secondColumn);
								}
							} else {
								vals[row_index] = t1.getObject(matches[row_index][0], firstColumn);
								missing[row_index] = t1.isValueMissing(matches[row_index][0], firstColumn);
							}
						else {
							if (secondColumn == -1) {
								vals[row_index] = null;
								missing[row_index] = true;
							} else {
								vals[row_index] = t2.getObject(matches[row_index][1], secondColumn);
								missing[row_index] = t2.isValueMissing(matches[row_index][1], secondColumn);
							}
						}
					}

					// add the column
					Column col = new ObjectColumn(vals);
					for (int ri = 0 ; ri < missing.length ; ri++){
						col.setValueToMissing(missing[ri],ri);
						if (missing[ri])
							col.setObject (null, ri);
					}
					result.addColumn(col);
					break;
				}
				case ColumnTypes.BYTE: {

					// get the data from table 2, put it first.
					byte [] vals = new byte [numRows];
					for (int row_index = 0 ; row_index < numRows ; row_index++) {
						if (matches[row_index][0] != -1)
							if (firstColumn == -1) {
								if (matches[row_index][1] == -1 || secondColumn == -1){
									vals[row_index] = t1.getMissingByte();//this.getFillerByte();
									missing[row_index] = true;
								} else {
									vals[row_index] = t2.getByte(matches[row_index][1], secondColumn);
									missing[row_index] = t2.isValueMissing(matches[row_index][1], secondColumn);
								}
							} else {
								vals[row_index] = t1.getByte(matches[row_index][0], firstColumn);
								missing[row_index] = t1.isValueMissing(matches[row_index][0], firstColumn);
							}
						else {
							if (secondColumn == -1) {
								vals[row_index] = t1.getMissingByte();//this.getFillerByte();
								missing[row_index] = true;
							} else {
								vals[row_index] = t2.getByte(matches[row_index][1], secondColumn);
								missing[row_index] = t2.isValueMissing(matches[row_index][1], secondColumn);
							}
						}
					}

					// add the column
					Column col = new ByteColumn(vals);
					for (int ri = 0 ; ri < missing.length ; ri++){
						col.setValueToMissing(missing[ri],ri);
						if (missing[ri])
							col.setByte(t1.getMissingByte(),ri);
					}
					result.addColumn(col);
					break;
				}
				case ColumnTypes.CHAR: {

					// get the data from table 2, put it first.
					char [] vals = new char [numRows];
					for (int row_index = 0 ; row_index < numRows ; row_index++) {
						if (matches[row_index][0] != -1)
							if (firstColumn == -1) {
								if (matches[row_index][1] == -1 || secondColumn == -1) {
									vals[row_index] = t1.getMissingChar();//this.getFillerChar();
									missing[row_index] = true;
								} else {
									vals[row_index] = t2.getChar(matches[row_index][1], secondColumn);
									missing[row_index] = t2.isValueMissing(matches[row_index][1], secondColumn);
								}
							} else {
								vals[row_index] = t1.getChar(matches[row_index][0], firstColumn);
								missing[row_index] = t1.isValueMissing(matches[row_index][0], firstColumn);
							}
						else {
							if (secondColumn == -1) {
								vals[row_index] = t1.getMissingChar();//this.getFillerChar();
								missing[row_index] = true;
							} else {
								vals[row_index] = t2.getChar(matches[row_index][1], secondColumn);
								missing[row_index] = t2.isValueMissing(matches[row_index][1], secondColumn);
							}
						}
					}

					// add the column
					Column col = new CharColumn(vals);
					for (int ri = 0 ; ri < missing.length ; ri++){
						col.setValueToMissing(missing[ri],ri);
						if (missing[ri])
							col.setChar(t1.getMissingChar(),ri);
					}
					result.addColumn(col);
					break;
				}
				default: throw new Exception("Datatype was not recognized when appending tables.");
			}
			if (firstColumn != -1)
				result.setColumnLabel(t1.getColumnLabel(firstColumn), i);
			else
				result.setColumnLabel (t2.getColumnLabel(secondColumn), i);
		}

		this.pushOutput(result, 0);
	}
}

// QA Anca - corrected type in moduleInfo
// WISH - mark filler values as missing values for future support of missing values
// WISH - support for multiple-column-key


/**
* 10-24-03: vered started QA process
* an itinerary containing this module cannot be loaded. and classes cannot be
* loaded too while said itinerary is open. [fixed 11-03-03]
*
* Module does not handle missing values well: the new appended table does
*      not "know" that some values are missing, apparently the missing values
*      array is not updated. whereas values that were missing are represented by
*      default value for missing values (for example 0.0 for doubles). [fixed - 11-03-03]
*
* 11-03-03: Columns with a missing value are not considered as key columns.
*           the algorithm for isKeyColumn is in TableUtilities. a method and
*           a genral approach to missing values should be determined.
* 11-20-03: key columns cannot have missing values. an exception will be thrown
       *    if such value swill be encountered in such column.
* 12-05-03: module is ready for basic 4.
*/
