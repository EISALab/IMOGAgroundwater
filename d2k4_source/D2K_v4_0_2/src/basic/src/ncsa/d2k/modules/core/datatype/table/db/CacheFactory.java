package ncsa.d2k.modules.core.datatype.table.db;

import java.sql.*;

/**
 * CacheFactory creates Cache objects.
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */
public class CacheFactory {

    public static final int FIXED_ROW_NUM = 0;

    private static final String[] DESCRIPTIONS =
        {"Fixed number of rows."};

    public Cache createCache(int type, ResultSet rs) throws SQLException {
    /*    switch(type) {
            case(0):
                return new FixedRowCache(new ResultSetDataSource(rs));
            default:
                return new FixedRowCache(new ResultSetDataSource(rs));
        }
        */
        return null;
    }

    public String getCacheDescription(int type) {
        if(type < DESCRIPTIONS.length)
            return DESCRIPTIONS[type];
        else
            return "";
    }
}