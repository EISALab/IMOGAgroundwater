package ncsa.d2k.modules.core.datatype.table.db.sql;

import java.sql.*;
import java.sql.Types;
import java.util.*;
import java.io.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.db.*;
import ncsa.d2k.modules.core.io.sql.*;

/**
 * <p>Title: CachedSQLDataSource</p>
 * <p>Description: This is a DBConnection that is 'bound' to a particular database.
 * Objects of this class are bound to a database, tables within the database,
 * and columns within those tables.</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: NCSA</p>
 * @author Sameer Mathur
 * @version 1.0
 */

public class ResultSetDataSource implements DBDataSource {

    /** The Connection to the DB */
    protected DBConnection dbConnection;

    /** The tables */
    protected String[] userTables;
    /** The columns */
    protected String[][] userAllColumns;
    /** the where */
    protected String whereClause;

    protected String userQuery;

    /** the number of distinct columns */
    protected int numDistinctUserColumns;
    /** The number of rows */
    private int numUserRows;

    /** the comments */
    private String[] columnComments;
    /** the labels */
    protected String[] columnLabels;

    /** the ResultSet holds the data */
    transient public ResultSet rs;
    /** the meta data about the result set */
    transient protected ResultSetMetaData rsmd;
    /** the statement that produces a result set */
    transient protected Statement stmt;

    /**
     * Construct a new ResultSetDataSource
     * @param connection the connection to the DB
     * @param tables the names of the tables in the DB that this data source will
     *     query
     * @param columns the columns in the DB that this data source will represent
     * @param whereClause the where
     */
     public ResultSetDataSource(DBConnection dbcon,
                               String[]   tables,
                               String[][] columns,
                               String whereClaus) {
        this.dbConnection = dbcon;
        this.setUserSelectedTables(tables);
        this.setUserSelectedCols(columns);
        this.setUserSelectedWhere(whereClaus);
        rs = dbConnection.getUpdatableResultSet(tables, columns, whereClaus);
        initialize();
    }

    protected ResultSetDataSource() {
    }

    /**
     * Close all connections when we are GC'd.
     */
    protected void finalize() throws Throwable {
        try {
            rs.close();
            rs = null;
            stmt.close();
            stmt = null;
        }
        catch(SQLException e) {
            rs = null;
            stmt = null;
        }
    }

    /**
     * Re-initialize when we are de-serialized.
     */
   private void readObject(ObjectInputStream in)
      throws IOException, ClassNotFoundException {

      in.defaultReadObject();
      rs = dbConnection.getResultSet(userTables, userAllColumns, whereClause);
      initialize();
    }

    /**
     * Return a copy of this.  The copy will use the same ConnectionWrapper,
     * but will have its own cached ResultSet.
     */
    public DBDataSource copy() {
        return new ResultSetDataSource(dbConnection, userTables, userAllColumns, whereClause);
    }

    public void       setUserSelectedTables(String[] _userTables){
        this.userTables = _userTables;
    }

    public void       setUserSelectedCols (String[][] _userColumns){
        this.userAllColumns = _userColumns;
    }

    public void       setUserSelectedWhere(String _where){
        this.whereClause = _where;
    }

    public String[]   getUserSelectedTables() {
        return this.userTables;
    }

    public String[][] getUserSelectedCols (){
        return this.userAllColumns;
    }

    public String     getUserSelectedWhere(){
        return this.whereClause;
    }

    protected String[]   getDistinctUserSelectedCols(){
        String[][] columns = userAllColumns;

        if (columns.length == 1) {
            return columns[0];
        }
        else {  // columns.length > 1
            //separate the columns into uniqueColumns and duplicateColumns
            Set uniques = new HashSet();
            Set dups = new HashSet();

            for (int tabl = 0; tabl < columns.length; tabl++)
                for (int tablCol = 0; tablCol < columns[tabl].length; tablCol++)
                    if (!uniques.add(columns[tabl][tablCol]))
                        dups.add(columns[tabl][tablCol]);
            uniques.removeAll(dups);  // Destructive set-difference

            Vector uniqueVec = new Vector(uniques);
            Vector duplicateVec = new Vector(dups);
            Vector unique = new Vector();

            for (int tabl = 0; tabl < columns.length; tabl++) {
                for (int tablCol = 0; tablCol < columns[tabl].length; tablCol++) {
                    if (duplicateVec.contains(columns[tabl][tablCol]))
                            continue;
                    else
                        unique.add(columns[tabl][tablCol]);
                }
            }
            duplicateVec.addAll(unique);
            return (String[]) duplicateVec.toArray(new String[duplicateVec.size()]);
        }
    } //getDistinctUserSelectedCols

    /**
     * Get the number of columns that this data source represents
     */
    public int      getNumDistinctColumns(){
        return numDistinctUserColumns;
    }

    /**
     * Initialize this data source.  Connect to the DB, initialize the result set,
     * set the fetch size, count the number of rows, and get the labels of
     * the columns.
     */

    protected void initialize() {
        try {
            rs.setFetchSize(350);
            rsmd = rs.getMetaData();
            countRows();
            numDistinctUserColumns = getDistinctUserSelectedCols().length;

            columnLabels = new String[numDistinctUserColumns];

            for (int i=0; i<numDistinctUserColumns; i++) {
                columnLabels[i] = getDistinctUserSelectedCols()[i];
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Count the number of rows that the query generates.
     */
    protected void countRows(){
        try{
            rs.last();
            this.numUserRows = rs.getRow();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the number of rows.
     */
    public int getNumRows(){
        return numUserRows;
    }

    /**
     * Get the labels of the column at pos.
     */
    public String getColumnLabel(int pos) {
        return columnLabels[pos];
    }

    public String getColumnComment(int pos) {
        return columnComments[pos];
    }

    /**
     * Map java.sql.type to ColumnTypes
     * @param pos
     * @return ColumnTypes
     */
    public int getColumnType (int pos) {
        if      (getColumnSQLType(pos) == java.sql.Types.VARCHAR)
            return ColumnTypes.STRING;
        else if (getColumnSQLType(pos) == java.sql.Types.REAL)
            return ColumnTypes.DOUBLE;
        else if (getColumnSQLType(pos) == java.sql.Types.NUMERIC)
            return ColumnTypes.DOUBLE;
        else if (getColumnSQLType(pos) == java.sql.Types.INTEGER)
            return ColumnTypes.INTEGER;
        else if (getColumnSQLType(pos) == java.sql.Types.FLOAT)
            return ColumnTypes.FLOAT;
        else if (getColumnSQLType(pos) == java.sql.Types.DOUBLE)
            return ColumnTypes.DOUBLE;
        else if (getColumnSQLType(pos) == java.sql.Types.DECIMAL)
            return ColumnTypes.DOUBLE;
        else if (getColumnSQLType(pos) == java.sql.Types.CHAR)
            return ColumnTypes.CHAR;
        else if (getColumnSQLType(pos) == java.sql.Types.FLOAT)
            return ColumnTypes.DOUBLE;
        else if (getColumnSQLType(pos) == java.sql.Types.JAVA_OBJECT)
            return ColumnTypes.OBJECT;
        else if (getColumnSQLType(pos) == java.sql.Types.LONGVARCHAR)
            return ColumnTypes.STRING;
        else
            return ColumnTypes.STRING;
    }

    private int getColumnSQLType (int pos) {
        try{
           return rsmd.getColumnType(pos+1);
       }
        catch(SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public String getTextData(int row, int col) {
        try {
            rs.absolute(row+1);
            return rs.getString(col+1);
        }
        catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    } //GetTextData

    public double getNumericData(int row, int col) {
        try {
            rs.absolute(row+1);
            return rs.getDouble(col+1);
        }
        catch(Exception e) {
            e.printStackTrace();
            return 0;
        }
    } //GetNumericData

    public boolean getBooleanData(int row, int col) {
        try {
            rs.absolute(row+1);
            return rs.getBoolean(col+1);
        }
        catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    } //getBooleanData

    public Object getObjectData(int row, int col) {
        try {
            rs.absolute(row+1);
            return rs.getObject(col+1);
        }
        catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    } //getObjectData


} //SQLDBConnection