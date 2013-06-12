//package ncsa.d2k.modules.projects.clutter.rdr;
package ncsa.d2k.modules.core.io.file.input;

/**
 * An interface to a file.  Defines methods to obtain data from the file.  The
 * file is assumed to be rectangular in nature, with rows and columns of data.
 */
public interface FlatFileParser {

	public static final String STRING_TYPE = "String";
	public static final String FLOAT_TYPE = "float";
	public static final String DOUBLE_TYPE = "double";
	public static final String INT_TYPE = "int";
	public static final String BOOLEAN_TYPE = "boolean";
	public static final String CHAR_ARRAY_TYPE = "char[]";
	public static final String BYTE_ARRAY_TYPE = "byte[]";
    public static final String CHAR_TYPE = "char";
    public static final String BYTE_TYPE = "byte";
	public static final String LONG_TYPE = "long";
	public static final String SHORT_TYPE = "short";
	public static final String NOMINAL_TYPE = "nominal";

    public static final String LABEL = "label";
    public static final String TYPE = "type";
    public static final String START = "start";
    public static final String STOP = "stop";
    public static final String LENGTH = "length";

    /**
     * Get the elements that make up row i of the file.
     * @return the elements of row i in the file.
     */
     public ParsedLine getRowElements(int i);

    /**
     * Get the number of columns.
     */
    public int getNumColumns();

    /**
     * Get the number of rows.
     * @return the number of rows
     */
    public int getNumRows();

    /**
     * Get the type of a colum
     * @param i the index of the column
     * @return the column type of the ith column, or -1 if the type
     * was not specified
     * @see ncsa.d2k.modules.core.datatype.table.ColumnTypes
     */
    public int getColumnType(int i);

    /**
     * Get the label of a column
     * @param i the index of the column
     * @return the label of the ith column, or -1 if the label was not
     * specified
     */
    public String getColumnLabel(int i);

    /**
     * Get a boolean map with one cell for each row and column.  The value
     * in the cell is true if the cell's value was missing, false if the
     * value was present
     * @return
     */
//    public boolean[][] getBlanks();

    /**
     * in-out-omit
     */
    //public int getDataType(int i);

    /**
     * nominal/scalar
     /
    public int getFeatureType(int i);
    */
}