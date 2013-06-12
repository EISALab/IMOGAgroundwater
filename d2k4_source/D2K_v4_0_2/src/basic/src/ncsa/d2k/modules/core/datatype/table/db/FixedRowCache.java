package ncsa.d2k.modules.core.datatype.table.db;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import java.sql.*;

/**
 * <p>Title: FixedRowCache </p>
 * <p>Description: Implements a Cache of Fixed #rows = 1000 </p>
 * <p>Copyright: NCSA (c) 2002</p>
 * <p>Company: </p>
 * @author Sameer Mathur, David Clutter
 * @version 1.0
 */

public class FixedRowCache extends Cache {

    /**
     * Create a new FixedRowCache given a ResultSet.  The ResultSet is needed
     * to get the datatypes and number of columns.  The cache is populated
     * with the first N rows.
     */
    public FixedRowCache(DBDataSource dbds) throws SQLException {
        maxRowsInCache = 1000;
        initialize(dbds);
    }

    private void initialize (DBDataSource dbds) throws SQLException {
        table = new MutableTableImpl(dbds.getNumDistinctColumns());
        for (int col = 0; col < dbds.getNumDistinctColumns(); col++) { //Initialize  Columns of the TableImpl
            switch (dbds.getColumnType(col)) {
                case ColumnTypes.INTEGER:
                case ColumnTypes.DOUBLE:
                case ColumnTypes.FLOAT:
                    table.setColumn(new DoubleColumn(getMaxCacheRows()), col);
                    break;
                case ColumnTypes.STRING:
                case ColumnTypes.CHAR:
                case ColumnTypes.OBJECT:
                    table.setColumn(new StringColumn(getMaxCacheRows()), col);
                    break;
                default:
                    table.setColumn(new StringColumn(getMaxCacheRows()), col);
            } //switch
        }//for
        update(0, dbds);

    }//initialize

} //FixedRowCache