package ncsa.d2k.modules.core.datatype.table.db;

/**
 * <p>Title: DBDataSource</p>
 * <p>Description: This is a DBConnection that is 'bound' to a particular database.
 * Objects of this class are bound to a database, tables within the database,
 * and columns within those tables.</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: NCSA</p>
 * @author Sameer Mathur, David Clutter
 * @version 1.0
 */
public interface DBDataSource extends java.io.Serializable /*extends DBConnection */{

    public DBDataSource copy();

    /**
     * Get the number of distinct columns.
     * @return
     */
    public int        getNumDistinctColumns();

    /**
     * Get the number of rows.
     * @return
     */
    public int        getNumRows();

    /**
     * Get textual data from (row, col)
     * @param row
     * @param col
     * @return
     */
    public String     getTextData    (int row, int col);

    /**
     * Get numeric data from (row, col)
     * @param row
     * @param col
     * @return
     */
    public double     getNumericData (int row, int col);

    /**
     * Get boolean data from (row, col)
     * @param row
     * @param col
     * @return
     */
    public boolean     getBooleanData (int row, int col);

    /**
     * Get an Object from (row, col);
     * @param row
     * @param col
     * @return
     */
    public Object     getObjectData  (int row, int col);
    //public boolean getBooleanData(int row, int col);

    /**
     * Get the label of the ith column.
     * @param i
     * @return
     */
    public String getColumnLabel(int i);

    /**
     * Get the comment for the ith column.
     * @param i
     * @return
     */
    public String getColumnComment(int i);

    /**
     * Get the column type from ColumnTypes
     * @param i
     * @return
     */
    public int getColumnType(int i);

}//DBTableConnect