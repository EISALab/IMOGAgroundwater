/*
 * Created on Nov 19, 2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ncsa.d2k.modules.core.datatype.table.basic;

/**
 * @author redman
 *
 * This class provides some support for missing values to all the subclasses. It provides a method
 * that returns true if there is any missing values in column, and also provides some support to 
 * maintain the list of missing values.
 */
public abstract class MissingValuesColumn extends AbstractColumn {
	
	/** a boolean for each row, true if the value in that row is missing. */
	protected boolean[] missing = null;
	
	/** this is a count of the number of missing values. */
	protected int numMissingValues = 0;
	
	/**
	 * Set the value at the given row to missing if b is true, not missing otherwise.
	 * @param b true if the value is missing.
	 * @param row the row to set the missing flag for.
	 */
	final public void setValueToMissing(boolean b, int row) {
		if (b == missing[row])
			return;
		if (b == true)
			numMissingValues++;
		else
			numMissingValues--;
		missing[row] = b;
	}
	
	/**
	 * @return true if the value is missing.
	 */
	final public boolean isValueMissing(int row) {
		return missing[row];
	}

	/**
	 * @return true if there are any missing values.
	 */
	final public boolean hasMissingValues() {
		return numMissingValues != 0;
	}
	
	/**
	 * Set missing values to the array passed in and update the count.
	 * @param missing the missing value flags.
	 */
	final public void setMissingValues(boolean[] miss) {
		missing = miss;
		this.numMissingValues = 0;
		for (int i = 0 ; i < miss.length ; i++)
			if (miss[i]) 
				this.numMissingValues++;
	}

	/**
	 * Get missing values in a boolean array.
	 * @ return the missing value flags.
	 */
	
	final public boolean[] getMissingValues() {
		 return missing;
	}

/*
 * Get number of missing values
 * @ return the number of missing values  
 */
 
 final public int getNumMissingValues() {
 	return numMissingValues;
 }
 
}
