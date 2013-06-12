package ncsa.d2k.modules.core.datatype.table.db.sql;

import java.sql.*;
import java.io.*;
import ncsa.d2k.modules.core.io.sql.*;
/**
 * <p>Title: MutableResultSetDataSource</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: NCSA</p>
 * @author Sameer Mathur
 * @version 1.0
 */

public class MutableResultSetDataSource extends ResultSetDataSource {

    private String tableName;
    private String[] columnNames;

    public MutableResultSetDataSource(DBConnection connection, String tableName,
                               String seqColName, String[] columns,
                               String[] typeNames, int[] displaySizes,
                               int numRows) {
        super();
        dbConnection = connection;

        tableName = tableName;
        columnNames = columns;

        String[] tables = {tableName};
        String[][] cols = new String[1][];
        cols[0] = columns;

        this.setUserSelectedTables(tables);
        this.setUserSelectedCols(cols);
        this.setUserSelectedWhere("");
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
        query.append(tableName);

        // Create a Prediction Table in the Database
        dbConnection.createTable(tableName, seqColName, columns,
                                 typeNames, displaySizes, numRows );
        rs = dbConnection.getUpdatableResultSet(tableName, columns, "");

        initialize();
    }

    protected void finalize() throws Throwable {
        super.finalize();
    }

    private void readObject(ObjectInputStream in)
        throws IOException, ClassNotFoundException {

        in.defaultReadObject();
        rs = dbConnection.getUpdatableResultSet(tableName, columnNames, "");
        initialize();
    }

    public void setString(String val, int row, int c) {
        try {
            rs.absolute(row+1);
            rs.updateString(c+1, val);
            rs.updateRow();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setFloat(float val, int row, int c) {
        try {
            rs.absolute(row+1);
            rs.updateFloat(c, val);
            rs.updateRow();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setDouble(double val, int row, int c) {
        try {
            rs.absolute(row+1);
            rs.updateDouble(c, val);
            rs.updateRow();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setInt(int val, int row, int c) {
        try {
            rs.absolute(row+1);
            rs.updateInt(c, val);
            rs.updateRow();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setShort(short val, int row, int c) {
        try {
            rs.absolute(row+1);
            rs.updateShort(c, val);
            rs.updateRow();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setLong(long val, int row, int c) {
        try {
            rs.absolute(row+1);
            rs.updateLong(c, val);
            rs.updateRow();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setObject(Object val, int row, int c) {
        try {
            rs.absolute(row+1);
            rs.updateObject(c, val);
            rs.updateRow();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setBoolean(boolean val, int row, int c) {
        try {
            rs.absolute(row+1);
            rs.updateBoolean(c, val);
            rs.updateRow();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addColumn() {}

}
