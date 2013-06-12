package ncsa.d2k.modules.core.datatype.table;

/**
 * A Transformation is an operation on a MutableTable.  These operations
 * should be saved with the MutableTable, so a record of them will exist.
 */
public interface Transformation extends java.io.Serializable {

	static final long serialVersionUID = 6151442764866906730L;

	/**
	 * Perform a data-transforming operation on a Table.  Return true if the
	 * operation succeeded, false otherwise.
	 * @param table the table to operate on.
	 * @return true if the transformation succeeded, false otherwise
	 */
	public boolean transform(MutableTable table);
}
