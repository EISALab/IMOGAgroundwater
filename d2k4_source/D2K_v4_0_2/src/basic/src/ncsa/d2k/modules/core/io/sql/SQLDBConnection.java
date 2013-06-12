package ncsa.d2k.modules.core.io.sql;
import java.sql.*;
import java.util.*;
/**
 * <p>Title: SQLDBConnection </p>
 * <p>Description: an abstract class with common functionality for Database connections </p>
 * <p>Copyright: NCSA (c) 2002</p>
 * <p>Company: </p>
 * @author Sameer Mathur, David Clutter
 * @version 1.0
 */

public abstract class SQLDBConnection extends ConnectionWrapperImpl implements DBConnection {

    protected SQLDBConnection(String _url, String _driver, String _username, String _password) {
        super(_url, _driver, _username, _password);
    }

    abstract protected String  getAllTableNamesQuery();
    abstract protected String  getTableQuery(String[] tables, String[][] columns, String where);
    abstract protected String  getFirstRowQuery (String tableName);

    private String addColumnQuery (String tableName, String columnName, String columnType) {
        String str = "ALTER TABLE ";
        str += tableName;
        str += " ADD (";
        str += columnName;
        str += " ";
        str += columnType;

        if (columnType == "varchar") {
            str += "(32)";
        }
        return str;
    }

//////////////////////////////////////////////////////////////////////////////////////////
    public void addColumn (String tableName, String columnName, String columnType) {
        // 1. create a new column called 'columnName' in the table in the database
        try {
            Connection con = getConnection();
            Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                                                 ResultSet.CONCUR_UPDATABLE);

            String query;
            if (columnType == "number")
                query = this.addColumnQuery(tableName, columnName, "number");
            else // (columnType == "varchar"
                query = this.addColumnQuery(tableName, columnName, "varchar");

            int queryFlag = stmt.executeUpdate(query);
            stmt.close();
        }
        catch (Exception s) {
            s.printStackTrace();
        }
    }

    public ResultSet getResultSet(String[] tables, String[][] columns, String where) {
        try {
            Connection con = getConnection();
            Statement stmt = con.createStatement( ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = stmt.executeQuery(this.getTableQuery(tables, columns, where));
            return rs;
        }
        catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public ResultSet getUpdatableResultSet(String[] tables, String[][] columns, String where) {
        try {
            Connection con = getConnection();
            Statement stmt = con.createStatement( ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = stmt.executeQuery(this.getTableQuery(tables, columns, where));
            return rs;
        }
        catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public ResultSet getUpdatableResultSet(String table, String[] columns, String where) {
        try {
            Connection con = getConnection();
            Statement stmt = con.createStatement( ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_UPDATABLE);
            StringBuffer query = new StringBuffer();
            query.append("SELECT ");
            for(int i = 0; i < columns.length; i++) {
                query.append(columns[i]);
                if(i < columns.length-1)
                    query.append(", ");
                else
                    query.append(" ");
            }
            query.append("FROM ");
            query.append(table);
            ResultSet rs = stmt.executeQuery(query.toString());
            return rs;
        }
        catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void dropTable(String tableName) {
        try {
            Connection con = getConnection();
            Statement stmt = con.createStatement();
            StringBuffer query = new StringBuffer("DROP TABLE ");
            query.append(tableName);
            stmt.executeUpdate(query.toString());
            stmt.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public String[]     getTableNames() {
        String tables[];
        Vector vec = new Vector();

        try {
          Connection con = getConnection();
          Statement stmt = con.createStatement();
          ResultSet rs = stmt.executeQuery(getAllTableNamesQuery());
          while (rs.next())
              vec.addElement((Object)rs.getString(1));
          stmt.close();

          // copy over the tables in the Vector into an Array of Strings
          tables = new String[vec.size()];
          for (int i=0; i<vec.size(); i++) {
              tables[i] = vec.elementAt(i).toString();
          }

          return tables;
        }
        catch (Exception e){
            e.printStackTrace();
           return null;
        }
    }

    public String[]     getColumnNames (String tableName) {
       try {
          Connection con = getConnection();
          Statement stmt = con.createStatement();
          ResultSet rs = stmt.executeQuery(getFirstRowQuery(tableName));
          ResultSetMetaData rsmd = rs.getMetaData();
          String columns[] = new String[rsmd.getColumnCount()];

          for (int i = 1; i <= rsmd.getColumnCount(); i++)
              columns[i-1] = rsmd.getColumnName(i);

          stmt.close();
          return columns;
        }
        catch (Exception e){
            e.printStackTrace();
           return null;
        }
    }

    public int[]     getColumnTypes (String tableName) {
        try {
          Connection con = getConnection();
          Statement stmt = con.createStatement();
          ResultSet rs = stmt.executeQuery(getFirstRowQuery(tableName));
          ResultSetMetaData rsmd = rs.getMetaData();
          int columnsTypes[] = new int[rsmd.getColumnCount()];

          for (int i = 1; i <= rsmd.getColumnCount(); i++)
              columnsTypes[i-1] = rsmd.getColumnType(i);

          stmt.close();
          return columnsTypes;
        }
        catch (Exception e){
            e.printStackTrace();
           return null;
        }
    }

    public int     getColumnType (String tableName, String columnName) {
       try {
          Connection con = getConnection();
          Statement stmt = con.createStatement();
          ResultSet rs = stmt.executeQuery(getFirstRowQuery(tableName));
          ResultSetMetaData rsmd = rs.getMetaData();
          int type = 0;

          for (int i = 1; i <= rsmd.getColumnCount(); i++) {
              if (rsmd.getColumnName(i) == columnName) {
                  type = rsmd.getColumnType(i);
                  break;
              }
          }
          stmt.close();
          return type;
        }
        catch (Exception e){
            e.printStackTrace();
           return 0;
        }
    }

    public int    getColumnLength (String tableName, String columnName) {
       try {
          Connection con = getConnection();
          Statement stmt = con.createStatement();
          ResultSet rs = stmt.executeQuery(getFirstRowQuery(tableName));
          ResultSetMetaData rsmd = rs.getMetaData();
          int length = 0;

          for (int i = 1; i <= rsmd.getColumnCount(); i++) {
              if (rsmd.getColumnName(i) == columnName) {
                  length = rsmd.getColumnDisplaySize(i);
                  break;
              }
          }
          stmt.close();
          return length;
        }
        catch (Exception e){
            e.printStackTrace();
           return 0;
        }
    }

    public int[]    getColumnLengths(String tableName) {
        try {
          Connection con = getConnection();
          Statement stmt = con.createStatement();
          ResultSet rs = stmt.executeQuery(getFirstRowQuery(tableName));
          ResultSetMetaData rsmd = rs.getMetaData();
          int columnsLengths[] = new int[rsmd.getColumnCount()];

          for (int i = 1; i <= rsmd.getColumnCount(); i++)
              columnsLengths[i-1] = rsmd.getColumnDisplaySize(i);

          stmt.close();
          return columnsLengths;
        }
        catch (Exception e){
            e.printStackTrace();
            return new int[(0)];
        }
    }

    public int    getNumRecords (String tableName) {
        int numRecords = 0;
        try {
          Connection con = getConnection();
          String query = getNumRecordsQuery(tableName);
          Statement stmt = con.createStatement();
          ResultSet rs = stmt.executeQuery(query);
          while (rs.next()) {
              numRecords = rs.getInt(1);
          }
          numRecords--; ///////////For some reason, numRecords is one more than it should be..
          stmt.close();
          return numRecords;
        }
        catch (Exception e){
            e.printStackTrace();
           return numRecords;
        }
    }

    public String getNumRecordsQuery(String tableName) {
        String str = "SELECT COUNT(*) FROM " + tableName;
        return str;
    }

} //SQLDBConnection