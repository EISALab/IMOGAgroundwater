package ncsa.d2k.modules.core.transform.table;

import java.util.HashMap;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;

/**
 * Fill missing values with some user selected default values.
 * @author redman
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class PopulateMissingValues
	extends ncsa.d2k.core.modules.DataPrepModule {

	/** the string to replace missing values with. */
	String fillerString = new String("*");

	/** the int to replace missing values with. */
	int fillerNumeric = 0;

	/** the boolean to replace missing values with. */
	boolean fillerBool = false;

	/** the byte[] to replace missing values with. */
	byte[] fillerByte = new byte[1];

	/** the char[] to replace missing values with. */
	char[] fillerChar = new char[1];

	/** the byte to replace missing values with. */
	byte fillerSingleByte = '\000';

	/** the char to replace missing values with. */
	char fillerSingleChar = '\000';

	/**
	 * Set the string to replace missing with.
	 * @param x value to replace missing values with.
	 */
	public void setFillerString(String x) {
		fillerString = x;
	}
	public String getFillerString() {
		return fillerString;
	}

	/**
	 * Set the string to replace missing with.
	 * @param x value to replace missing values with.
	 */
	public void setFillerNumeric(int b) {
		fillerNumeric = b;
	}
	public int getFillerNumeric() {
		return fillerNumeric;
	}

	/**
	 * Set the string to replace missing with.
	 * @param x value to replace missing values with.
	 */
	public void setFillerBool(boolean a) {
		fillerBool = a;
	}
	public boolean getFillerBool() {
		return fillerBool;
	}

	/**
	 * Set the string to replace missing with.
	 * @param x value to replace missing values with.
	 */
	public void setFillerBytes(byte[] c) {
		fillerByte = c;
	}
	public byte[] getFillerBytes() {
		return fillerByte;
	}

	/**
	 * Set the string to replace missing with.
	 * @param x value to replace missing values with.
	 */
	public void setFillerChars(char[] d) {
		fillerChar = d;
	}
	public char[] getFillerChars() {
		return fillerChar;
	}

	/**
	 * Set the string to replace missing with.
	 * @param x value to replace missing values with.
	 */
	public void setFillerChar(char d) {
		fillerSingleChar = d;
	}
	public char getFillerChar() {
		return fillerSingleChar;
	}

	/**
	 * Set the string to replace missing with.
	 * @param x value to replace missing values with.
	 */
	public void setFillerByte(byte d) {
		fillerSingleByte = d;
	}
	public byte getFillerByte() {
		return fillerSingleByte;
	}

	/**
	 * Return a list of the property descriptions.
	 * @return a list of the property descriptions.
	 */
	public PropertyDescription[] getPropertiesDescriptions() {
		PropertyDescription[] pds = new PropertyDescription[3];
		pds[0] =
			new PropertyDescription(
				"fillerBool",
				"Boolean Filler",
				"The value used to fill boolean columns.");
		pds[1] =
			new PropertyDescription(
				"fillerString",
				"String Filler",
				"The value used to fill string columns.");
		pds[2] =
			new PropertyDescription(
				"fillerNumeric",
				"Numeric Filler",
				"The value used to fill numeric columns.");
		return pds;
	}

	/**
	 * returns information about the input at the given index.
	 * @return information about the input at the given index.
	 */
	public String getInputInfo(int index) {
		switch (index) {
			case 0 :
				return "<p>Table containing missing value to be replaced.</p>";
			case 1 :
				return "<p>"
					+ "      This input contains the list of columns to populate missing values in. "
					+ "      It is optional, if absent all columns will be populated."
					+ "    </p>";
			default :
				return "No such input";
		}
	}

	/**
	 * returns information about the output at the given index.
	 * @return information about the output at the given index.
	 */
	public String getInputName(int index) {
		switch (index) {
			case 0 :
				return "Missing Value Table";
			case 1 :
				return "Column Names";
			default :
				return "NO SUCH INPUT!";
		}
	}

	/**
	 * returns string array containing the datatypes of the inputs.
	 * @return string array containing the datatypes of the inputs.
	 */
	public String[] getInputTypes() {
		String[] types =
			{
				"ncsa.d2k.modules.core.datatype.table.Table",
				"[Ljava.lang.String;" };
		return types;
	}

	/**
	 * returns information about the output at the given index.
	 * @return information about the output at the given index.
	 */
	public String getOutputInfo(int index) {
		switch (index) {
			case 0 :
				return "<p>Newly created table containing no missing values.</p>";
			default :
				return "No such output";
		}
	}

	/**
	 * returns information about the output at the given index.
	 * @return information about the output at the given index.
	 */
	public String getOutputName(int index) {
		switch (index) {
			case 0 :
				return "Result Table";
			default :
				return "NO SUCH OUTPUT!";
		}
	}

	/**
	 * returns string array containing the datatypes of the outputs.
	 * @return string array containing the datatypes of the outputs.
	 */
	public String[] getOutputTypes() {
		String[] types = { "ncsa.d2k.modules.core.datatype.table.Table" };
		return types;
	}

	/**
	 * returns the information about the module.
	 * @return the information about the module.
	 */
	public String getModuleInfo() {
		return "<p>"
			+ "      Overview: This module will replace missing values with values provided "
			+ "      in the properties."
			+ "    </p>"
			+ "    <p>"
			+ "      Detailed Description: This module takes two inputs. They are the table "
			+ "      containing the missing values and a list of column labels identifying "
			+ "      columns to operate on. In each of those columns identified, all of the "
			+ "      missing values in that column will be replaced. Missing values are "
			+ "      replaced with values specified in properties of this module."
			+ "    </p>"
			+ "    <p>"
			+ "      Data Type Restrictions: If input table columns have the same name but "
			+ "      different data types, the data type from the <i>First Table</i> is used "
			+ "      in the result table, and an attempt is made to convert the data values "
			+ "      from the <i>Second Table</i> to that type. This conversion may result in "
			+ "      unexpected values in the output table. In some cases, such as when a "
			+ "      string cannot be converted to a numeric, an exception will be raised. "
			+ "      The user is discouraged from appending tables containing attributes with "
			+ "      the same name whose types differ. For some conversions, for example when "
			+ "      an integer is converted to a double, there may be no loss of data, but "
			+ "      the user should verify the result table has the expected values."
			+ "    </p>"
			+ "    <p>"
			+ "      Data Handling: This module does modify the input table.."
			+ "    </p>"
			+ "    <p>"
			+ "      Scalability: This module operates using only table methods, no "
			+ "      additional memory is required."
			+ "    </p>";
	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "Populate Missing Values";
	}

	/**
	 * If the second input is connected, we must have data on both inputs to run, otherwise
	 * we only need input on the first input.
	 */
	public boolean isReady() {
		if (this.isInputPipeConnected(1)) {
			return this.getInputPipeSize(0) > 0 && this.getInputPipeSize(1) > 0;
		} else
			return this.getInputPipeSize(0) > 0;
	}

	/**
	 * append table one to table two.
	 * @throws Exception
	 */
	public void doit() throws Exception {
		MutableTable missingTable = (MutableTable) this.pullInput(0);
		int numColumns = missingTable.getNumColumns();

		// Populate a hashtable with the names of all the columns we will populate.
		HashMap columns = new HashMap();
		if (this.isInputPipeConnected(1)) {
			String[] colNames = (String[]) this.pullInput(1);
			for (int i = 0; i < colNames.length; i++) {
				columns.put(colNames[i], colNames[i]);
			}
		} else {

			// We will do ALL the columns.
			for (int i = 0; i < numColumns; i++) {
				String lbl = missingTable.getColumnLabel(i);
				columns.put(lbl, lbl);
			}
		}

		//for each column
		for (int col = 0; col < numColumns; col++) {
			String lbl = missingTable.getColumnLabel(col);

			// if this is not a column of interest, go on.
			if (columns.get(lbl) == null)
				continue;

			switch (missingTable.getColumnType(col)) {
				case ColumnTypes.INTEGER :
				case ColumnTypes.FLOAT :
				case ColumnTypes.DOUBLE :
				case ColumnTypes.SHORT :
				case ColumnTypes.LONG :
					for (int row = 0; row < missingTable.getNumRows(); row++)
						if (missingTable.isValueMissing(row, col)) {
							missingTable.setInt(fillerNumeric, row, col);
							missingTable.setValueToMissing(false, row, col);
						}
					break;

				case ColumnTypes.STRING :
					for (int row = 0; row < missingTable.getNumRows(); row++)
						if (missingTable.isValueMissing(row, col)) {
							missingTable.setString(fillerString, row, col);
							missingTable.setValueToMissing(false, row, col);
						}
					break;

				case ColumnTypes.CHAR_ARRAY :
					for (int row = 0; row < missingTable.getNumRows(); row++)
						if (missingTable.isValueMissing(row, col)) {
							missingTable.setChars(fillerChar, row, col);
							missingTable.setValueToMissing(false, row, col);
						}
					break;

				case ColumnTypes.BYTE_ARRAY :
					for (int row = 0; row < missingTable.getNumRows(); row++)
						if (missingTable.isValueMissing(row, col)) {
							missingTable.setBytes(fillerByte, row, col);
							missingTable.setValueToMissing(false, row, col);
						}
					break;

				case ColumnTypes.BOOLEAN :
					for (int row = 0; row < missingTable.getNumRows(); row++)
						if (missingTable.isValueMissing(row, col)) {
							missingTable.setBoolean(fillerBool, row, col);
							missingTable.setValueToMissing(false, row, col);
						}
					break;

				case ColumnTypes.OBJECT :
					for (int row = 0; row < missingTable.getNumRows(); row++)
						if (missingTable.isValueMissing(row, col)) {
							missingTable.setString(fillerString, row, col);
							missingTable.setValueToMissing(false, row, col);
						}
					break;

				case ColumnTypes.BYTE :
					for (int row = 0; row < missingTable.getNumRows(); row++)
						if (missingTable.isValueMissing(row, col)) {
							missingTable.setByte(this.fillerSingleByte, row,col);
							missingTable.setValueToMissing(false, row, col);
						}
					break;

				case ColumnTypes.CHAR :
					for (int row = 0; row < missingTable.getNumRows(); row++)
						if (missingTable.isValueMissing(row, col)) {
							missingTable.setChar(fillerSingleChar, row, col);
							missingTable.setValueToMissing(false, row, col);
						}
					break;

				default :
					{
						throw new Exception(
							"Datatype for Table column named '"
								+ missingTable.getColumn(col).getLabel()
								+ "' was not recognized when appending tables.");
					}
			}
		}
		// Done!
		this.pushOutput(missingTable, 0);
	}
}
