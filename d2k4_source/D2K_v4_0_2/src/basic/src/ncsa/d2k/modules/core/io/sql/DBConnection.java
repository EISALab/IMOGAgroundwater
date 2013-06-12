package ncsa.d2k.modules.core.io.sql;

import java.sql.*;

/**
 * <p>Title: DBConnection</p>
 * <p>Description: An Interface having generic metadata-level methods for a Database</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: NCSA</p>
 * @author Sameer Mathur
 * @version 1.0
 */

public interface DBConnection extends ConnectionWrapper, java.io.Serializable {

    public void createTable (String   tableName,
                             String   seqName,
                             String[] colNames,
                             String[] colTypeNames,
                             int[]    colDisplaySizes,
                             int      numRows);

    public void createTable (String   tableName,
                             String[] colNames,
                             String[] colTypeNames,
                             int[]    colDisplaySizes);

    public void addColumn (String tableName, String columnName, String columnType);

    public void dropTable(String tableName);

    public ResultSet getResultSet(String[] tables, String[][] columns, String where);
    public ResultSet getUpdatableResultSet(String[] tables, String[][] columns, String where);
    public ResultSet getUpdatableResultSet(String table, String[] columns, String where);

    /**
     * Get the names of all the tables in a database.
     * @return
     */
    public String[]           getTableNames();

    /**
     * Given a table name, get the names of all its columns.
     * @param tableName
     * @return
     */
    public String[]           getColumnNames      (String tableName);

    /**
     * Given a table name, get the datatypes of all its columns.
     * @param tableName
     * @return
     */
    public int[]           getColumnTypes   (String tableName);

    /**
     * Given a table name and a column name, get the datatype of the column.
     * @param tableName
     * @param columnName
     * @return
     */
    public int             getColumnType       (String tableName, String columnName);

    /**
     * Given a table name, get the lengths of all the columns.
     * @param tableName
     * @return
     */
    public int[]              getColumnLengths    (String tableName);

    /**
     * Given a table name and a column name, get the length of the column.
     * @param tableName
     * @param columnName
     * @return
     */
    public int                getColumnLength     (String tableName, String columnName);

    /**
     * Given a table name, get the number of records in that table.
     * @param tableName
     * @return
     */
    public int                getNumRecords       (String tableName);

}//DBConnection