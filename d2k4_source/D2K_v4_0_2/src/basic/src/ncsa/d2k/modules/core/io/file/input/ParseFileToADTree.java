package ncsa.d2k.modules.core.io.file.input;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

/**
 * Read a file into an ADTree.
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author clutter
 * @version 1.0
 */
public class ParseFileToADTree extends InputModule {

	protected static final char QUESTION = '?';
	protected static final char SPACE = ' ';

	public String[] getInputTypes() {
		String[] in = { "ncsa.d2k.modules.core.io.file.input.FlatFileParser" };
		return in;
	}

	public String getInputInfo(int i) {
		return "The FlatFileParser to read data from.";
	}

	public String getInputName(int i) {
		return "File Parser";
	}

	public String[] getOutputTypes() {
		String[] out =
			{
				"ncsa.d2k.modules.core.datatype.ADTree",
				"ncsa.d2k.modules.core.datatype.table.ExampleTable" };
		return out;
	}

	public String getOutputName(int i) {
		if (i == 0)
			return "ADTree";
		else
			return "Example Table";
	}

	public String getOutputInfo(int i) {
		if (i == 0)
			return "An ADTree.";
		else
			return "ExampleTable containing meta data.";
	}

	public String getModuleInfo() {
		StringBuffer sb = new StringBuffer("<p>Overview: ");
		sb.append(
			"Reads a file and stores attribute occurrence counts into an ADTree index structure ");
		sb.append("<p>Detailed Description: ");
		sb.append(
			"Given a parser, reads the data, counting how many times each attribute value occurs.");
		sb.append(
			"All these counts are stored into an ADTree structure. See B. Anderson and A. Moore ");
		sb.append(
			"in \"ADTrees for fast counting and for fast learning of association rules\".");
		sb.append(
			"The purpose of the tree is to answer questions like \" how many instances with x, y and z");
		sb.append(
			" are in the dataset?\" in constant time. It cannot answer these questions for continuous data ");
		sb.append(
			"An ExampleTable that contains the column types and labels of the input data ");
		sb.append("is also created.");
		sb.append(
			"<p> Data Issues: Designed to work with nominal data, cannot handle real data.");
		sb.append("In practice the tree is not useful for real data.");
		sb.append(
			"<p> Scalability: The ADTree is in fact an index structure for a datacube.");
		sb.append(
			"If there are a lot of attributes and they have many values the structure ");
		sb.append(
			"will easily run out of memory. Optimizations have been made so that only the ");
		sb.append(
			"first branch of the tree is stored in memory and the rest are built as needed when ");
		sb.append("the counts are retrieved");
		return sb.toString();
	}

	public String getModuleName() {
		return "Create an ADTree";
	}

	public void doit() throws Exception {
		FlatFileParser ffr = (FlatFileParser) pullInput(0);

		// make the meta table
		int numRows = ffr.getNumRows();
		Column[] cols = new Column[ffr.getNumColumns()];
		for (int i = 0; i < cols.length; i++) {
			int typ = ffr.getColumnType(i);
			// create the column
			if (typ == ColumnTypes.STRING)
				cols[i] = new StringColumn(numRows);
			else if (typ == ColumnTypes.DOUBLE)
				//cols[i] = new DoubleColumn(numRows);
				throw new Exception(
					"ADTrees cannot be built for numeric data. Discretize col"
						+ i);
			else if (typ == ColumnTypes.FLOAT)
				//cols[i] = new FloatColumn(numRows);
				throw new Exception(
					"ADTrees cannot be built for numeric data. Discretize col "
						+ i);
			else if (typ == ColumnTypes.INTEGER)
				//cols[i] = new IntColumn(numRows);
				throw new Exception(
					"ADTrees cannot be built for numeric data. Discretize col "
						+ i);
			else if (typ == ColumnTypes.SHORT)
				//cols[i] = new ShortColumn(numRows);
				throw new Exception(
					"ADTrees cannot be built for numeric data. Discretize col "
						+ i);
			else if (typ == ColumnTypes.LONG)
				//cols[i] = new LongColumn(numRows);
				throw new Exception(
					"ADTrees cannot be built for numeric data. Discretize col "
						+ i);
			else if (typ == ColumnTypes.CHAR_ARRAY)
				cols[i] = new CharArrayColumn(numRows);
			else if (typ == ColumnTypes.BYTE_ARRAY)
				cols[i] = new ByteArrayColumn(numRows);
			else if (typ == ColumnTypes.CHAR)
				cols[i] = new CharColumn(numRows);
			else if (typ == ColumnTypes.BYTE)
				cols[i] = new ByteColumn(numRows);
			else if (typ == ColumnTypes.BOOLEAN)
				cols[i] = new BooleanColumn(numRows);
			else
				cols[i] = new StringColumn(numRows);

			String lbl = ffr.getColumnLabel(i);
			if (lbl != null)
				cols[i].setLabel(lbl);
			else
				cols[i].setLabel(Integer.toString(i));

		}

		//  DefaultTableFactory  dtf = DefaultTableFactory.getInstance();
		// Table table = dtf.createTable(cols);
		//ExampleTable meta = dtf.createExampleTable(table);
		MutableTableImpl table = new MutableTableImpl(cols);
		ExampleTable meta = new ExampleTableImpl(table);

		// create the ADTree
		ADTree adt = new ADTree(ffr.getNumColumns());
		//there are two levels of debug for an ADTree; set the first one only
		adt.setDebug1(debug);
		for (int i = 0; i < ffr.getNumColumns(); i++)
			adt.setLabel(i + 1, ffr.getColumnLabel(i));

		for (int i = 0; i < ffr.getNumRows(); i++) {
			String[] vals = new String[ffr.getNumColumns()];
			ParsedLine pl = ffr.getRowElements(i);
			char[][] row = pl.elements;

			boolean[] blanks = pl.blanks;
			if (row != null) {
				
				for (int k = 0; k < row.length; k++) 
							vals[k] = new String(row[k]);
				
				for (int j = 0; j < cols.length; j++) {
					boolean isMissing = true;
					char[] elem = row[j];
					// test to see if this is '?',  if it is, this value is missing.
					for (int k = 0; k < elem.length; k++) {
						if (elem[k] != QUESTION && elem[k] != SPACE) {
							isMissing = false;
							break;
						}
					}
				
				  if(isMissing) vals[j] = "?";	
				
					//System.out.println("countingLine for " + i + ": " +  vals[j] + " missing " + isMissing + " blanks " + blanks[j]);

					// set the boolean marker to missing in the metadata table
					if (isMissing || blanks[j])
						cols[j].setValueToMissing(true, i);
					else
						cols[j].setValueToMissing(false, i);

					if (cols[j].hasMissingValues())
						meta.setMissingString("?");
				}
				
				/*System.out.println("vals in Parse are ");
  					for (int j =0; j < vals.length; j ++)
					  System.out.print(vals[j] + " " );
							  System.out.println("");					
				*/
				
				// no matter if the value is missing or not,  just add it to the tree
				// otherwise we'll lose count info
				adt.countLine(adt, vals);
			}
		}

		pushOutput(adt, 0);
		pushOutput(meta, 1);
	}

	boolean debug = false;

	/**
	 Set the value for the debug variable
	 @param b boolean value for the debug variable
	*/
	public void setDebug(boolean b) {
		debug = b;
	}

	/**
	 Get the debug setting
	 @return The boolean value of debug
	*/

	public boolean getDebug() {
		return debug;
	}

	public PropertyDescription[] getPropertiesDescriptions() {
		PropertyDescription[] pd = new PropertyDescription[1];
		pd[0] =
			new PropertyDescription(
				"debug",
				"Generate Verbose Output",
				"If this property is true, the module will write verbose status information to the console "
					+ "while building the ADTree. ");
		return pd;
	}

}
// QA Comments
// 2/14/03 - Handed off to QA by David Clutter
// 2/17/03 - Anca started QA process.  Updated module info and added
//           exception handling for numeric/continuous data.
//           FEATURE REQUEST: a module that will create an ADTree
//           from selected columns only, either from a table or from a file..
// 2/18/03 - checked into basic.
// 2/21/03 - vered started QA second test.
//           added getPropertiesDescriptions method.
// END QA Comments
