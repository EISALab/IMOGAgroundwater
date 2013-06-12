package ncsa.d2k.modules.core.transform.table;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import gnu.trove.*;
import java.util.*;

/**
 * Remove duplicate rows from a MutableTable.
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author David Clutter, Tom Redman
 * @version 1.0
 */
public class RemoveDuplicateRows extends DataPrepModule {

    public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.MutableTable"};
		return types;
	}

    public String[] getOutputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.MutableTable"};
		return types;
	}

    public String getInputInfo(int i) {
		switch (i) {
			case 0: return "This is the table that will have duplication rows removed.";
			default: return "No such input";
		}
	}

    public String getOutputInfo(int i) {
		switch (i) {
			case 0: return "The same table as the input table, with the duplicate rows removed.";
			default: return "No such output";
		}
	}

    public String getModuleInfo() {
		return "<p>      Overview: This module will remove all duplicate rows. A row is a       duplicate if"+
			" each value for each attribute is the same as those in a       previously encountered row. "+
			"   </p>    <p>      Detailed Description: For each row in the table, we collect a vector of"+
			"       strings that is the text encoding. We then check a hash table to see if       an entry"+
			" with the same values has already been encountered. If it has,       we just mark the row as"+
			" a duplicate to delete when we are done. If it       hasn't been encountered, we add it to the"+
			" hash table. When we have       examined each row, we just trash the duplicates.    </p>   "+
                        "<P>Data Handling: The input table is changed by this module.</P>"+
			" <p>      Scalability: If there are not a lot of duplicates, this hashtable can       get very"+
			" large. The entries in this hash table store a string       representation of each value in"+
			" each field in each unique row. Do the       math.    </p>";
	}

    public void doit() {
        MutableTable mt = (MutableTable)pullInput(0);
        HashSet setOfUniqueRows = new HashSet();

        TIntArrayList rowsToRemove = new TIntArrayList();

        int numRows = mt.getNumRows();
        int numCols = mt.getNumColumns();
        for(int i = 0; i < numRows; i++) {
            String[] row = new String[numCols];
            for (int j =0; j < numCols; j++) {
               if (mt.isValueMissing(i, j))
			     row [j] = "XX_MISSING_VALUE_XX";
               else
			     row [j] = mt.getString(i, j);
            }
            RowSet rs = new RowSet(row);
            if(setOfUniqueRows.contains(rs)) {
                rowsToRemove.add(i);
            }
            else
                setOfUniqueRows.add(rs);
        }
        int[] toRem = rowsToRemove.toNativeArray();
        for (int i = toRem.length-1; i > -1; i--){
          mt.removeRow(toRem[i]);
        }
        pushOutput(mt, 0);
    }

    private class RowSet {
        String[] keys;

        RowSet(String[] k) {
            keys = k;
        }

        public boolean equals(Object o) {
            RowSet other = (RowSet)o;
            String [] otherkeys = other.keys;

            if(otherkeys.length != keys.length)
                return false;

            for(int i = 0; i < keys.length; i++)
                if(!keys[i].equals(otherkeys[i]))
                    return false;
            return true;
        }

        public int hashCode() {
            int result = 37;
            for(int i = 0; i < keys.length; i++)
                result *= keys[i].hashCode();
            return result;
        }
	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "Remove Duplicate Rows";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Input Table";
			default: return "NO SUCH INPUT!";
		}
	}

	/**
	 * Return the human readable name of the indexed output.
	 * @param index the index of the output.
	 * @return the human readable name of the indexed output.
	 */
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Result Table";
			default: return "NO SUCH OUTPUT!";
		}
	}
}

      /**
      * QA Comments:
      * 10-23-03: vered started qa process.
      *
      *           array index out of bound exception is thrown by removeRow of
      *           Column [bug 87]. On other dataset the removal is incorrect.
      *           [bug 100]
      *           data set files can be found in basic archive under QA/bugs_data_set
      *
      * 11-03-03: bugs 87 & 100 are fixed.
      *
      * 12-05-03: module is ready for basic 4.
      *
 */
