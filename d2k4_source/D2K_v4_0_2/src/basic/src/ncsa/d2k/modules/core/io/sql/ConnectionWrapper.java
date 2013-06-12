package ncsa.d2k.modules.core.io.sql;

import java.sql.*;

public interface ConnectionWrapper {

    public Connection getConnection() throws SQLException, ClassNotFoundException,
    InstantiationException, IllegalAccessException;
}
