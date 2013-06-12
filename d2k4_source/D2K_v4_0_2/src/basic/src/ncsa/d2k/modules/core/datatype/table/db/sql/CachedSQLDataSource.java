package ncsa.d2k.modules.core.datatype.table.db.sql;

import ncsa.d2k.modules.core.io.sql.*;
import ncsa.d2k.modules.core.datatype.table.db.*;
import ncsa.d2k.modules.core.datatype.table.*;
import java.sql.*;
import java.util.*;
import java.io.*;

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

public class CachedSQLDataSource implements DBDataSource {
    protected DBConnection dbconnection;
    protected Cache cache;
    protected String dbInstance;
    protected String[]   userTables;
    protected String[][] userAllColumns;
    protected String     whereClause;
    protected int        numDistinctUserColumns;
    protected int        numUserRows;
    protected ArrayList columnComments;
    protected String[] columnLabels;

    public CachedSQLDataSource(DBConnection connection,
                               String[] tables,
                               String[][]columns,
                               String whereClause,
                               int cacheType) {
        dbconnection = connection;
        this.setUserSelectedTables(tables);
        this.setUserSelectedCols(columns);
        this.setUserSelectedWhere(whereClause);
        initialize(cacheType);
    }

    public DBDataSource copy() {
        DBDataSource cpy = new CachedSQLDataSource(dbconnection,
            userTables, userAllColumns, whereClause, 0);
        return cpy;
    }

    /**
     * Re-initialize when we are de-serialized.
     */
   private void readObject(ObjectInputStream in)
      throws IOException, ClassNotFoundException {

      in.defaultReadObject();
      initialize(0);
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

    private String[]   getDistinctUserSelectedCols(){
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

    protected void     setNumDistinctUserSelectedCols (String[][] columns){
        numDistinctUserColumns = this.getDistinctUserSelectedCols().length;
    }

    public int   getNumDistinctColumns(){
        return numDistinctUserColumns;
    }

    private void initialize(int cacheType) {
        try {
                DBDataSource dbds = new ResultSetDataSource(
                    dbconnection, userTables, userAllColumns, whereClause);
                cache = new FixedRowCache(dbds);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        setNumUserSelectedRows();
        setNumDistinctUserSelectedCols (null);
        columnLabels = this.getDistinctUserSelectedCols();
    }

     // Caching Mechanism Followed :
        // On a cache miss, write upto getNumCacheRows() records of the ResultSet rs,
        // beginning from record# rowInResultSet
    public void updateCache (int rowInResultSet) {
        try {
            cache.update(rowInResultSet,
                new ResultSetDataSource(dbconnection, userTables, userAllColumns, whereClause));
         }
         catch(Exception e) {
            e.printStackTrace();
         }
    }//UpdateCache()

    protected void setNumUserSelectedRows(){
        try{
            ResultSet rs = dbconnection.getResultSet(this.userTables,
                                                     this.userAllColumns,
                                                     this.whereClause);
            int count = 0;
            rs.first();
            while (rs.next()){
                count++;
            }
            this.numUserRows = count;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getNumRows(){
        return numUserRows;
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
            ResultSet rs = dbconnection.getResultSet(this.userTables,
                                                     this.userAllColumns,
                                                     this.whereClause);
            ResultSetMetaData rsmd = rs.getMetaData();
            return rsmd.getColumnType(pos+1);
       }
        catch(Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public String getTextData(int row, int col) {
        try {
            return cache.getCacheTextData(row, col);
        }
        catch (CacheMissException cme) {
            try {
                this.updateCache(row);
                return cache.getCacheTextData(row, col); //return data from the updated cache
            }
            catch (CacheMissException cmeError) {
                System.out.println("SQLDBConnection:GetTextData:Warning: " +
                                   "Cache Miss After Update " + cmeError.getMessage());
                return null;
            }
            catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    } //GetTextData

    public double getNumericData(int row, int col) {
        try {
            return cache.getCacheNumericData(row, col);
        }
        catch (CacheMissException cme) {
            try {
                this.updateCache(row);
                return cache.getCacheNumericData(row, col);
            }
            catch (CacheMissException cmeError) {
                return 0.0;
            }
            catch (Exception e) {
                e.printStackTrace();
                return 0.0;
            }
        }
    } //GetNumericData

    public boolean getBooleanData(int row, int col) {
        try {
            return cache.getCacheBooleanData(row, col);
        }
        catch (CacheMissException cme) {
            try {
                this.updateCache(row);
                return cache.getCacheBooleanData(row, col);
            }
            catch (CacheMissException cmeError) {
                return false;
            }
            catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    } //getBooleanData

    public Object getObjectData(int row, int col) {
        try {
            return cache.getCacheObjectData(row, col);
        }
        catch (CacheMissException cme) {
            try {
                this.updateCache(row);
                return cache.getCacheObjectData(row, col);
            }
            catch (CacheMissException cmeError) {
                return null;
            }
            catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    } //getObjectData

    protected void     setDBInstance (String dbInst) {
        dbInstance = dbInst;
    }

    protected String     getDBInstance() {
        return dbInstance;
    }

    public String     getProductName() throws InstantiationException, IllegalAccessException, SQLException, ClassNotFoundException{
        return dbconnection.getConnection().getMetaData().getDatabaseProductName();
    }

    public String getColumnLabel(int pos) {
        if (pos < columnLabels.length)
            return columnLabels[pos];
        else {
System.out.println("Column Label position : " + pos + " out of bounds");
            return null;
        }
    }

    public String getColumnComment(int pos) {
        if (pos < columnComments.size())
            return (String)columnComments.get(pos);
        else {
System.out.println("Column Comment position : " + pos + " out of bounds");
            return null;
        }
    }

} //SQLDBConnection