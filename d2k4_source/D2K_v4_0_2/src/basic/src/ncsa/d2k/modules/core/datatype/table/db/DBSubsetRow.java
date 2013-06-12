package ncsa.d2k.modules.core.datatype.table.db;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.io.sql.*;

public class DBSubsetRow extends DBRow implements Row{



        protected  int[] subset;



    public DBSubsetRow (DBDataSource _dataSource, DBConnection _dbConnection, DBTable _table) {
        super(_dataSource, _dbConnection, _table);

        subset = new int[0];
  }


    public DBSubsetRow (DBDataSource _dataSource, DBConnection _dbConnection, DBTable _table, int[] _subset) {
        super(_dataSource, _dbConnection, _table);
        subset = _subset;
  }


	/**
	 * This could potentially be subindexed.
	 * @param i
	 */
	public void setIndex(int i) {
		this.index = subset[i];
	}

}