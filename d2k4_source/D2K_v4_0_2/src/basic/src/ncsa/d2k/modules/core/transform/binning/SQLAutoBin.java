package ncsa.d2k.modules.core.transform.binning;

import java.util.*;
import java.text.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.transformations.*;
import ncsa.d2k.modules.core.io.sql.*;
import java.sql.*;

/**

 * Automatically discretize scalar data for the Naive Bayesian classification

 * model.  This module requires a ParameterPoint to determine the method of binning

 * to be used.

 */

public class SQLAutoBin extends AutoBin {

	public String[] getInputTypes() {

		String[] in =
			{
				"ncsa.d2k.modules.core.io.sql.DBConnection",
				"java.lang.String",
				"ncsa.d2k.modules.core.datatype.table.ExampleTable" };

		//	"java.lang.String" };

		return in;

	}

	/**

	* Get the name of the input parameter

	* @param i is the index of the input parameter

	* @return Name of the input parameter

	*/

	public String getInputName(int i) {

		switch (i) {

			case 0 :

				return "Database Connection";

			case 1 :

				return "Database Table Name";

			case 2 :

				return "Meta Data Example Table";

				//case 3 :

				//	return "Query Condition";

			default :

				return "No such input";

		}

	}

	/**

	* Get the data types for the output parameters

	* @return A object of class BinTransform

	*/

	public String[] getOutputTypes() {

		String[] types =
			{ "ncsa.d2k.modules.core.datatype.table.transformations.BinTransform" };

		return types;

	}

	/**

	 * Get input information

	 * @param i is the index of the input parameter

	 * @return A description of the input parameter

	 */

	public String getInputInfo(int i) {

		switch (i) {

			case 0 :

				return "The database connection.";

			case 1 :

				return "The selected table from a database.";

			case 2 :

				return "ExampleTable containing the names of the input/output features";

				//case 3 :

				//	return "The query conditions.";

			default :

				return "No such input";

		}

	}

	/**

	 * Get the name of the output parameters

	 * @param i is the index of the output parameter

	 * @return Name of the output parameter

	 */

	public String getOutputName(int i) {

		switch (i) {

			case 0 :

				return "Binning Transformation";

			default :

				return "no such output!";

		}

	}

	/**

	 * Get output information

	 * @param i is the index of the output parameter

	 * @return A description of the output parameter

	 */

	public String getOutputInfo(int i) {

		switch (i) {

			case 0 :

				return "A BinTransform object that contains column_numbers, names and labels";

			default :

				return "No such output";

		}

	}

	public String getModuleName() {

		return "SQL Auto Discretization";

	}

	public String getModuleInfo() {

		String s =
			"<p>Overview: Automatically discretize scalar data for the "
				+ "Naive Bayesian classification model. "
				+ "<p>Detailed Description: Given a database connection, a database table name "
				+ "a query condition, and a metadata ExampleTable containing the names of columns "
				+ "define the bins for each scalar input column.  "
				+ "<p>Data Type Restrictions: This module does not modify the input data."
				+ "<P>Data Handling: When binning Uniformly, "
				+ "the number of bins is determined by '<i>Number of Bins</i>' property, "
				+ "and the boundaries of the bins are set so that they divide evenly over the range "
				+ "of the binned column. "
				+ " When binning by weight, '<i>Number of Items per Bin</I>' sets the size of each bin. "
				+ "The values are then binned so that in each bin there is the same number of items. ";

		/*

		+ "<p>Data Handling: When binning scalar columns by the number of bins, "

		+ "the maximum and minimum values of each column must be found.  When "

		+ "binning scalar columns by weight, group by statements are used.";*/

		return s;

	}

	DBConnection conn;

	String tableName; //, whereClause;

	ExampleTable tbl;

	NumberFormat nf;

	public void doit() throws Exception {

		conn = (DBConnection) pullInput(0);

		tableName = (String) pullInput(1);

		//tbl = (ExampleTable) pullInput(2);

		//whereClause = (String) pullInput(3);

		try {

			tbl = (ExampleTable) pullInput(2);

		} catch (ClassCastException ce) {

			throw new Exception(
				getAlias()
					+ ": Select input/output features using SQLChooseAttributes before this module");

		}

		nf = NumberFormat.getInstance();

		nf.setMaximumFractionDigits(3);

		int type = getBinMethod();

		int[] inputs = tbl.getInputFeatures();

		if (inputs == null || inputs.length == 0)
			throw new Exception("Input features are missing. Please select an input feature.");

		int[] outputs = tbl.getOutputFeatures();

		if (outputs == null || outputs.length == 0)
			throw new Exception("Output feature is missing. Please select an output feature.");

		if (tbl.isColumnScalar(outputs[0]))
			throw new Exception("Output feature must be nominal.");

		BinDescriptor[] bins;

		if (type == 0) {

			int number = getNumberOfBins();

			if (number < 0)
				throw new Exception(
					getAlias() + ": Number of bins not specified!");

			bins = numberOfBins(number);

		} else {

			int weight = getBinWeight();

			bins = sameWeight(weight);

		}

		//Add bins named "unknown" for each binned column that has missing values

		//bins = BinningUtils.addMissingValueBins(tbl,bins);

		BinTransform bt = new BinTransform(tbl, bins, false);

		pushOutput(bt, 0);

		//pushOutput(et, 1);

	}

	protected BinDescriptor[] numberOfBins(int num) throws Exception {

		List bins = new ArrayList();

		int[] inputs = tbl.getInputFeatures();

		int[] outputs = tbl.getOutputFeatures();

		boolean colTypes[] = getColTypes(inputs.length);

		for (int i = 0; i < inputs.length; i++) {

			boolean isScalar = colTypes[i];

			// if it is scalar, find the max and min and create equally-spaced bins

			//System.out.println("scalar ? " + i + " " +  isScalar );

			if (isScalar) {

				// find the maxes and mins

				double minMaxTotal[] = getMMTValues(i);

				double min = minMaxTotal[0];

				double max = minMaxTotal[1];

				//System.out.println("min, max: " + min + " " + max);

				double[] binMaxes = new double[num - 1];

				double interval = (max - min) / (double) num;

				binMaxes[0] = min + interval;

				// System.out.println("binmaxes[0] " + binMaxes[0]);

				// add the first bin manually

				BinDescriptor bd =
					BinDescriptorFactory.createMinNumericBinDescriptor(
						inputs[i],
						binMaxes[0],
						nf,
						tbl);

				bins.add(bd);

				// now add the rest

				for (int j = 1; j < binMaxes.length; j++) {

					binMaxes[j] = binMaxes[j - 1] + interval;

					bd =
						BinDescriptorFactory.createNumericBinDescriptor(
							inputs[i],
							binMaxes[j - 1],
							binMaxes[j],
							nf,
							tbl);

					bins.add(bd);

				}

				//System.out.println("binmaxes[length-1] " + binMaxes[binMaxes.length-1]);

				// now add the last bin

				bd =
					BinDescriptorFactory.createMaxNumericBinDescriptor(
						inputs[i],
						binMaxes[binMaxes.length - 1],
						nf,
						tbl);

				bins.add(bd);

			}

			// if it is nominal, create a bin for each unique value.

			else {

				HashSet vals = uniqueValues(i);

				Iterator iter = vals.iterator();

				while (iter.hasNext()) {

					String item = (String) iter.next();

					String[] st = new String[1];

					st[0] = item;

					BinDescriptor bd =
						new TextualBinDescriptor(
							i,
							item,
							st,
							tbl.getColumnLabel(i));

					bins.add(bd);

				}

			}

		}

		BinDescriptor[] bn = new BinDescriptor[bins.size()];

		for (int i = 0; i < bins.size(); i++) {

			bn[i] = (BinDescriptor) bins.get(i);

		}

		//		add "unkown" bins for relevant attributes that have missing values

		//bn = BinningUtils.addMissingValueBins(tbl,bn);

		return bn;

	}

	protected BinDescriptor[] sameWeight(int weight) throws Exception {

		List bins = new ArrayList();
		int[] inputs = tbl.getInputFeatures();
		int[] outputs = tbl.getOutputFeatures();
		boolean colTypes[] = getColTypes(inputs.length);

		for (int i = 0; i < inputs.length; i++) {

			//vered - debug
			//System.out.println("making bins for column " + inputs[i]);
			// if it is scalar, get the data and sort it.  put (num) into each bin,
			// and create a new bin when the last one fills up

			boolean isScalar = colTypes[i];

			// System.out.println("scalar ? " + i + " " + isScalar);

			if (isScalar) {
				ArrayList list = new ArrayList();
				try {

					Double db1 = null;
					String colName = tbl.getColumnLabel(i);
					Connection con = conn.getConnection();

					// Dora add the where clause to fix bug 172
					String queryStr =
						"select "
							+ colName
							+ ", count("
							+ colName
							+ ") from "
							+ tableName
							+ " where "
							+ colName
							+ " is not null group by "
							+ colName;

					Statement stmt = con.createStatement();
					ResultSet groupSet = stmt.executeQuery(queryStr);

					// anca int itemCnt = 0;
					// dora changed from int itemCnt =-1
					int itemCnt = 0;

					while (groupSet.next()) {
						itemCnt += groupSet.getInt(2);
						db1 = new Double(groupSet.getDouble(1));
						//System.out.println("itemCnt " + itemCnt + " db1  " + db1);
						// Dora changed from if (itemCnt >= (weight - 1)) {
						if (itemCnt >= weight) {

							// itemCnt >= specified weight, add the value to the list

							list.add(db1);

							// reset itemCnt
							// Dora changed from itemCnt = -1;
							itemCnt = 0;

						}

					}

					// put anything left in a bin

					if (itemCnt > 0)
						list.add(db1);

					stmt.close();
				} catch (Exception e) {
					System.out.println("Error occured in addFromWeight. " + e);
				}

				double[] binMaxes = new double[list.size()];
				for (int j = 0; j < binMaxes.length; j++) {
					binMaxes[j] = ((Double) list.get(j)).doubleValue();

				}

				if (binMaxes.length < 2) {
					BinDescriptor nbd =
						BinDescriptorFactory.createMinMaxBinDescriptor(i, tbl);
					bins.add(nbd);
				} else {

					// add the first bin manually

					BinDescriptor nbd =
						BinDescriptorFactory.createMinNumericBinDescriptor(
							i,
							binMaxes[0],
							nf,
							tbl);

					bins.add(nbd);

					// Dora changed from   for (int j = 1; j < binMaxes.length-1; j++) {
					for (int j = 1; j < binMaxes.length - 1; j++) {
						// now create the BinDescriptor and add it to the bin list
						nbd =
							BinDescriptorFactory.createNumericBinDescriptor(
								i,
								binMaxes[j - 1],
								binMaxes[j],
								nf,
								tbl);
						bins.add(nbd);
					}

					// now add the last bin

					//if (binMaxes.length > 1)
					nbd = BinDescriptorFactory.createMaxNumericBinDescriptor(i,
				 						binMaxes[binMaxes.length - 2], nf, tbl);

					bins.add(nbd);
				}

			} else {

				HashSet vals = uniqueValues(i);

				Iterator iter = vals.iterator();

				while (iter.hasNext()) {

					String item = (String) iter.next();

					String[] st = new String[1];

					st[0] = item;

					BinDescriptor bd =
						new TextualBinDescriptor(
							i,
							item,
							st,
							tbl.getColumnLabel(i));

					bins.add(bd);

				}

			}

		}

		BinDescriptor[] bn = new BinDescriptor[bins.size()];
		for (int i = 0; i < bins.size(); i++) {
			bn[i] = (BinDescriptor) bins.get(i);
		//	System.out.println("bin i "  + bn[i].name + " " + i);
		}

		return bn;

	}

	/** verify whether the column is a numeric column

	         *  @return a boolean array, numeric columns are flaged as true, and

	         *          categorical columns are flaged as false.

	         */

	public boolean[] getColTypes(int len) {

		boolean colTypes[] = new boolean[len];

		try {

			Connection con = conn.getConnection();

			DatabaseMetaData metadata = con.getMetaData();

			String[] names = { "TABLE" };

			ResultSet tableNames =
				metadata.getTables(null, "%", tableName, names);

			while (tableNames.next()) {

				ResultSet columns =
					metadata.getColumns(
						null,
						"%",
						tableNames.getString("TABLE_NAME"),
						"%");

				while (columns.next()) {

					String columnName = columns.getString("COLUMN_NAME");

					String dataType = columns.getString("TYPE_NAME");

					for (int col = 0; col < len; col++) {

						if (tbl.getColumnLabel(col).equals(columnName)) {

							if (ColumnTypes.isEqualNumeric(dataType))
								colTypes[col] = true;

							else
								colTypes[col] = false;

							break;

						}

					}

				}

			}

			return colTypes;

		} catch (Exception e) {

			/*  JOptionPane.showMessageDialog(msgBoard,

			        e.getMessage(), "Error",

			        JOptionPane.ERROR_MESSAGE); */

			System.out.println("Error occured in getColTypes.");

			return null;

		}

	}

	public double[] getMMTValues(int col) {

		double minMaxTotal[] = new double[3];

		try {

			String colName = tbl.getColumnLabel(col);

			Connection con = conn.getConnection();

			String queryStr =
				"select min("
					+ colName
					+ "), max("
					+ colName
					+ "), sum("
					+ colName
					+ ") from "
					+ tableName;

			Statement stmt = con.createStatement();

			ResultSet totalSet = stmt.executeQuery(queryStr);

			totalSet.next();

			minMaxTotal[0] = totalSet.getDouble(1);

			minMaxTotal[1] = totalSet.getDouble(2);

			minMaxTotal[2] = totalSet.getDouble(3);

			stmt.close();

		} catch (Exception e) {

			/* JOptionPane.showMessageDialog(msgBoard,

			       e.getMessage(), "Error",

			       JOptionPane.ERROR_MESSAGE); */

			System.out.println("Error occured in getMMTValues.");

		}

		return minMaxTotal;

	}

	/** find unique values in a column

	 *  @param col the column to check for

	 *  @return a HashSet object that stores all unique values

	 */

	private HashSet uniqueValues(int col) {

		// count the number of unique items in this column

		HashSet set = new HashSet();

		try {

			String colName = tbl.getColumnLabel(col);

			Connection con = conn.getConnection();

			String queryStr =
				"select distinct " + colName + " from " + tableName + " where " + colName + " is not null";

			Statement stmt = con.createStatement();

			ResultSet distinctSet = stmt.executeQuery(queryStr);

			while (distinctSet.next()) {

				set.add(distinctSet.getString(1));

			}

			stmt.close();

			return set;

		} catch (Exception e) {

			/* JOptionPane.showMessageDialog(msgBoard,

			         e.getMessage(), "Error",

			         JOptionPane.ERROR_MESSAGE); */

			System.out.println("Error occurred in uniqueValues.");

			return null;

		}

	}

}

/*12-8-03 Anca: took out query condition input- does not seem to be used

 *   			fixed [bug 152] - SQLAutoBin - wrong weight binning - - changed itemCnt from 0 to -1

* 				fixed [bug 151] - wrong uniform binning -  min, max were interchanged

**				added "unknown" bins for relevant attributes that have missing values

*/

/*12 -11-03 Anca - added an "unkown" named bin for every column that has missing values

*				Missing values are set in SQLChooseAttributes in the  metadata example table

*/

//12-12--03 Anca - added check and exception for input table that is not an ExampleTable

// 12 -16-03 Anca moved creation of "unknown" bins to BinTransform

/**
* 01-11-04: Vered
* bug 215 - creates one bin too many, the last one, which is expendable, as non of the
* data is being binned into it. (fixed)
 *
 * 01-21-04:

  * bug 228: binning and representation of missing values. missing values are binned
  * into the "UNKNOWN" bin but are still marked as missing if binning is done
  * in the same column. (fixed)
 *
 * 01-29-04: vered
 * ready for basic.

*/