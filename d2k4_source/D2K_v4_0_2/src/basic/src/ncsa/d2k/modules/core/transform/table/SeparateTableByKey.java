package ncsa.d2k.modules.core.transform.table;



import ncsa.d2k.core.modules.DataPrepModule;
import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.datatype.table.Table;
import ncsa.d2k.modules.core.datatype.table.basic.MutableTableImpl;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: redman
 * Date: Aug 25, 2003
 * Time: 7:57:29 PM
 * To change this template use Options | File Templates.
 */
public class SeparateTableByKey extends DataPrepModule {

	/** this is the list of tables that were produced. */
	Table [] tables = null;

	/** this is the index of the next table to push. */
	int currentTable = 0;

	/** the name of the attribute to use to distinguish the tables by key. */
	String attributeName;

	/** this property defines the max number of tables to produce. */
	int maxTables = 10000;

	/**
	 * Set the name of the key attribute field.
	 * @param name
	 */
	public void setAttributeName(String name) {
		attributeName = name;
	}
	public String getAttributeName () {
		return attributeName;
	}

	/**
	 * set the maximum number of tables to produce.
	 * @param mmt
	 */
	public void setMaxTables (int mmt) {
		this.maxTables = mmt;
	}
	public int getMaxTables () {
		return maxTables;
	}

	///////////////////////////////////
	//
	//  Info methods.
	//
	//////////////////////////////////

	public String getModuleInfo () {
		return "<p>      Overview: This module will separate the input table into several subset       tables"+
			" based on the value of the key field.    </p>    <p>      Detailed Description: The column named"+
			" in the property named <i>Key       Attribute</i> will be used to identify the contents of the"+
			" resulting       tables. Each table will contain all the entries that shared a common      "+
			" value of that attribute. The tables are pushed one per execution until       either the number"+
			" specified by the &quot;Maximum Number of Tables&quot; property       is reached, or all the"+
			" tables generated have been pushed.    </p>    <p>      Data Handling: Each table is a subset"+
			" table, so the data is not copied,       and the subset tables are no smaller than the original."+
			"    </p>";
	}
	static final String [] ins = {"ncsa.d2k.modules.core.datatype.table.Table"};
	public String [] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.Table"};
		return types;
	}
	static final String [] outs = {"ncsa.d2k.modules.core.datatype.table.Table"};
	public String [] getOutputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.Table"};
		return types;
	}
	public String getInputInfo(int i) {
		switch (i) {
			case 0: return "This is the input table which will be subseted to get tables sharing a     common value for the key column.";
			default: return "No such input";
		}
	}
	public String getInputName(int i) {
		switch(i) {
			case 0:
				return "Input Table";
			default: return "NO SUCH INPUT!";
		}
	}
	public String getOutputInfo(int i) {
		switch (i) {
			case 0: return "A new table containing all rows sharing the same value for the key attribute.";
			default: return "No such output";
		}
	}
	public String getOutputName(int i) {
		switch(i) {
			case 0:
				return "Subset Table";
			default: return "NO SUCH OUTPUT!";
		}
	}

	/**
	 * Clear the tables field and the current table index.
	 */
	public void beginExecution () {
		tables = null;
		currentTable = 0;
	}
	public void endExecution () {
		tables = null;
		currentTable = 0;
	}

	/**
	 * We are ready to execute if we have more tables to push, or if we have
	 * a table as input that is to be seperated by key.
	 * @return true if we are ready to execute.
	 */
	public boolean isReady() {
		if (tables != null && currentTable < tables.length) {
			return true;
		} else if (this.getFlags()[0] > 0)
			return true;
		return false;
	}

	/**
	 * First time through, we will seperate the table into n distinct tables where n is the number of
	 * distinct values for the key field. Each table will contains all the rows that shared the same
	 * value for the key.
	 */
	public void doit () throws Exception {

		if (tables == null) {

			// Create the array of tables.
			HashMap subtableMap = new HashMap();
			MutableTableImpl mt = (MutableTableImpl) this.pullInput (0);

			// First find the index of the key column.
			int numCols = mt.getNumColumns();
			int keyColIndx = 0;
			for (; keyColIndx < numCols ; keyColIndx++) {
				if (mt.getColumn(keyColIndx).getLabel().equals (attributeName))
					break;
			}
			if (keyColIndx == numCols) {
				throw new Exception (this.getAlias()+" : The attribute named "+attributeName+" was not found in the table.");
			}

			// Now create the subset identifying the row for each subtable..
			int numRows = mt.getNumRows ();
			for (int i = 0 ; i < numRows ; i++) {
				String keyValue = mt.getString (i, keyColIndx);
				ExtensibleIntArray eia = (ExtensibleIntArray) subtableMap.get(keyValue);
				if (eia == null) {
					eia = new ExtensibleIntArray (numRows);
					subtableMap.put (keyValue, eia);
				}
				eia.add(i);
			}

			// Now create the tables.
			int numtables = subtableMap.size() < this.maxTables ? subtableMap.size() : this.maxTables;
			tables = new Table [numtables];
			Iterator i1 = subtableMap.keySet().iterator();
			Iterator i2 = subtableMap.values().iterator();
			for (int i = 0 ; i < numtables ; i++) {
				String label = (String) i1.next();
				ExtensibleIntArray ss = (ExtensibleIntArray) i2.next();
				Table t = mt.copy(ss.toIntArray());
				//Table t = mt.getSubset(ss.toIntArray ());
				t.setLabel(label);
				tables [i] = t;
			}
		}
		this.pushOutput (tables[currentTable++], 0);
	}

	public PropertyDescription[] getPropertiesDescriptions () {
		PropertyDescription[] descriptions = new PropertyDescription[2];
		descriptions[0] = new PropertyDescription (
				"attributeName",
				"Key Attribute",
				"This is the name of the attribute which contains the key value.");
		descriptions[1] = new PropertyDescription (
				"maxTables",
				"Maxmimum Number of Tables",
				"This is the maximum number of tables that will be pushed. I can push fewer, but never more.");
		return descriptions;
	}
	/**
	 * Wrapper class for an integer array where the ultimate size of the array is not known, but the
	 * max size is. The integer array returned is compressed to the rigght size.
	 */
	public class ExtensibleIntArray {
		private int where;
		private int [] intarray;
		ExtensibleIntArray (int maxsize) {
			intarray = new int [maxsize];
		}

		public void add (int newval) {
			intarray[where++] = newval;
		}
		private void compress () {
			int [] newintarray = new int [where];
			System.arraycopy (intarray,0,newintarray,0,where);
			intarray = newintarray;
		}
		public int [] toIntArray () {
			if (intarray.length != where)
				this.compress();
			return intarray;
		}
	}


	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "SeparateTableByKey";
	}
}
