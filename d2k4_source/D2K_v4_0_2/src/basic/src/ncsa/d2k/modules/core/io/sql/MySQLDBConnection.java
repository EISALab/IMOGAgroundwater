package ncsa.d2k.modules.core.io.sql;

//import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.modules.core.datatype.table.db.sql.*;

import java.sql.*;
import javax.swing.*;
import java.util.*;

public class MySQLDBConnection extends SQLDBConnection {

    private String dbInstance;

    public MySQLDBConnection(String _url, String _driver, String _username, String _password,
                           String _dbinstance) {
        super(_url, _driver, _username, _password);
        dbInstance = _dbinstance;
    }
//////////////////////////////////////////////////////////////////////////////////////////

    public String     getAllTableNamesQuery() {
//System.out.println("mySQLConnect:QueryTableNames:dbI:" + this.dbInstance);
        String str = "SHOW TABLES FROM " + this.dbInstance;
        return str;
    }

    public String     getFirstRowQuery (String tableName) {
        String str = "SELECT * FROM " + tableName;
        return str;
    }

//////////////////////////////////////////////////////////////////////////////////////////

    public void createTable (String   tableName,
                             String[] colNames,
                             String[] colTypeNames,
                             int[]    colDisplaySizes) {
        try {

            // 1.5 Connect to the Database
            Connection con  = this.getConnection();

            // 4. connect to the database and create the prediction table
            Statement stmt1 = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                                                 ResultSet.CONCUR_UPDATABLE);
            String query1 = this.createTableWithoutSeqQuery(tableName,
                                                            colNames,
                                                            colTypeNames,
                                                            colDisplaySizes);
            int createTableFlag = stmt1.executeUpdate(query1);
//System.out.println("createTableFlag = " + createTableFlag);
            stmt1.close();
        }
        catch (Exception s) {
            s.printStackTrace();
        }
    } // createPredictionTable
//////////////////////////////////////////////////////////////////////////////////////////

    public void createTable (String   tableName,
                             String   seqName,
                             String[] colNames,
                             String[] colTypeNames,
                             int[]    colDisplaySizes,
                             int      numRows) {
        try {

            // 1.5 Connect to the Database
            Connection con  = this.getConnection();

            // 4. connect to the database and create the prediction table
            Statement stmt1 = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                                                 ResultSet.CONCUR_UPDATABLE);
            String query1 = this.createTableWithSeqQuery(tableName,
                                                         colNames,
                                                         colTypeNames,
                                                         colDisplaySizes);
            int createTableFlag = stmt1.executeUpdate(query1);
//System.out.println("createTableFlag = " + createTableFlag);
            stmt1.close();

            // 6. Insert 'tableConnection.getNumRows()' Sequence # and NULL's into the table
            Statement stmt2;
            String query2 = this.insertTableWithSeqQuery(tableName,
                                                         seqName,
                                                         colNames,
                                                         colTypeNames,
                                                         colDisplaySizes);
//System.out.println("query2 : " + query2);
            int insertTableFlag;

            for (int i=0; i<numRows; i++) {
                stmt2 = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                                            ResultSet.CONCUR_UPDATABLE);
                insertTableFlag = stmt2.executeUpdate(query2);
                stmt2.close();
            }
        }
        catch (Exception s) {
            s.printStackTrace();
        }
    } // createTable
//////////////////////////////////////////////////////////////////////////////////////////
/*          String createmySQL0 =  "CREATE TABLE IRIS2DD ("
                             + " SEQ_NUM int NOT NULL AUTO_INCREMENT, PRIMARY KEY (SEQ_NUM),"
	                     + " SEPAL_LENGTH varchar(6) ,"
	                     + " SEPAL_WIDTH varchar(6) ,"
	                     + " PETAL_LENGTH varchar(6) )";
*/
    private String createTableWithSeqQuery (String _tableName,
                                     String[] _colNames,
                                     String[] _colTypeNames,
                                     int[] _colDisplaySizes) {

        String str = new String("CREATE TABLE " + _tableName + " (");
        str += "SEQ_NUM int NOT NULL AUTO_INCREMENT, PRIMARY KEY (SEQ_NUM), ";

        for (int i=0; i<_colNames.length; i++) {
            if ( (_colTypeNames[i].compareToIgnoreCase("varchar") == 0) ){
                str += _colNames[i];
                str += " ";
                str += _colTypeNames[i];
                str += "(";
                str += _colDisplaySizes[i];
                str += ")";
            }
            else { // if (colTypeNames[i] == "number")
                str += _colNames[i];
                str += " ";
                str += _colTypeNames[i];
            }
            if (i<_colNames.length-1)
                str += ", ";
        } //for
        str += ")";
//System.out.println("createTableQuery with SEQ_NUM" + str);
        return str;
    }

    private String createTableWithoutSeqQuery (String _tableName,
                                               String[] _colNames,
                                               String[] _colTypeNames,
                                               int[] _colDisplaySizes) {

        String str = new String("CREATE TABLE " + _tableName + " (");

        for (int i=0; i<_colNames.length; i++) {
            if ( (_colTypeNames[i].compareToIgnoreCase("varchar") == 0) ){
                str += _colNames[i];
                str += " ";
                str += _colTypeNames[i];
                str += "(";
                str += _colDisplaySizes[i];
                str += ")";
            }
            else { // if (colTypeNames[i] == "number")
                str += _colNames[i];
                str += " ";
                str += _colTypeNames[i];
            }
            if (i<_colNames.length-1)
                str += ", ";
        } //for
        str += ")";
//System.out.println("createTableQuery without SEQ_NUM" + str);
        return str;
    }

//////////////////////////////////////////////////////////////////////////////////////////
/*    insertString = "insert into IRIS2DD (SEPAL_LENGTH, SEPAL_WIDTH, PETAL_LENGTH) " +
                     "values('5',   '3.4',	'1.5')"; */
    private String insertTableWithSeqQuery (String _tableName,
                                            String _seqName,
                                            String[] _colNames,
                                            String[] _colTypeNames,
                                            int[] _colDisplaySizes) {
        String str = "INSERT INTO ";
        str += _tableName;
        str += " (";

        for (int i=0; i<_colNames.length; i++) {
            str += _colNames[i];
            if (i<_colNames.length-1)
                str += ", ";
        }
        str += ") VALUES (";

        for (int i=0; i<_colNames.length; i++) {
            if ( (_colTypeNames[i].compareToIgnoreCase("varchar") == 0) ){
                str += "'x'";
            }
            else { // if (colTypeNames[i] == "number")
                str += "0";
            }
            if (i<_colNames.length-1)
                str += ", ";
        } //for
        str += ")";

//System.out.println("insertTableQuery " + str);
        return str;
    }
//////////////////////////////////////////////////////////////////////////////////////////

    public String getTableQuery(String[] tables, String[][] columns, String where) {

        StringBuffer query = new StringBuffer();
        if (tables.length == 1) {                             //USER SELECTED ONLY 1 TABLE
            query.append("SELECT ");
            for (int tabl = 0; tabl < columns.length; tabl++)
                for (int tablCol = 0; tablCol < columns[tabl].length; tablCol++) {
                    query.append(columns[tabl][tablCol]);
                    if (tablCol < columns[tabl].length -1)
                        query.append(", ");
                }
            query.append(" FROM ");
            query.append(tables[0]);

            if ((where != null) && (where.length() > 0)) {
                query.append(" WHERE ");
                query.append(where);
            }
            query.append(" ORDER BY ");
            for (int tabl = 0; tabl < columns.length; tabl++)
                for (int tablCol = 0; tablCol < columns[tabl].length; tablCol++) {
                    query.append(columns[tabl][tablCol]);
                    if (tablCol < columns[tabl].length -1)
                        query.append(", ");
                }

//    System.out.println("*****************QUERY: ONE TABLE *****************");
//    System.out.println(query.toString());
            return query.toString();
        }
        else {                                                   //USER SELECTED >1 TABLES
            // separate the columns into uniqueColumns and duplicateColumns
            int i = 0;
            Set uniques = new HashSet();
            Set dups = new HashSet();
            for (int tabl = 0; tabl < columns.length; tabl++)
                for (int tablCol = 0; tablCol < columns[tabl].length; tablCol++)
                    if (!uniques.add(columns[tabl][tablCol]))
                        dups.add(columns[tabl][tablCol]);

            uniques.removeAll(dups);  // Destructive set-difference

            Vector uniqueVec = new Vector(uniques);
            Vector duplicateVec = new Vector(dups);

            // First : Create SELECT Clause
//            query.append("SELECT * FROM (SELECT ");
            query.append("SELECT ");

            for (int l=0; l<duplicateVec.size(); l++){
                // get the 2 tables that a duplicate column belongs to
                //first find the two [x] indices of columns[x][y] where the column is present
                //next, use these indices on the tables[] array to retrieve the tables
                int idx1 = 0;
                int idx2 = 0;
                int table1, table2;
                for (table1 = 0; table1 < columns.length; table1++)
                    for (int tablCol = 0; tablCol < columns[table1].length; tablCol++)
                        if( columns[table1][tablCol] == duplicateVec.elementAt(l)) {
                            idx1 = table1;
                            break;
                        }

                for (table2 = table1; table2 < columns.length; table2++)
                    for (int tablCol = 0; tablCol < columns[table2].length; tablCol++)
                        if( columns[table2][tablCol] == duplicateVec.elementAt(l) ) {
                            idx2 = table2;
                            break;
                        }

                // now append  "<TableName1>.<DuplicateColumnName>"
                query.append(tables[idx1]);
                query.append(".");
                query.append(duplicateVec.elementAt(l));
                if (l<duplicateVec.size()-1)
                    query.append(", ");
            }

            if ((duplicateVec.size() > 0) && (uniqueVec.size() > 0))
                query.append(", ");

/*
System.out.println("printing uniqueColumns ");
for (int q=0; q<uniqueVec.size(); q++){
    System.out.println(uniqueVec.elementAt(q));
}
System.out.println("printing duplicateColumns ");
for (int q=0; q<duplicateVec.size(); q++){
    System.out.println(duplicateVec.elementAt(q));
}
*/
            int ct = 0;
            for (int tabl = 0; tabl < columns.length; tabl++) {
                for (int tablCol = 0; tablCol < columns[tabl].length; tablCol++) {
                    if (duplicateVec.contains(columns[tabl][tablCol]))
                        continue;
                    else {
                        ct++;
                        query.append(tables[tabl]);//..........
                        query.append(".");         //..........
                        query.append(columns[tabl][tablCol]);
                        if(ct < uniqueVec.size())
                            query.append(", ");
                    }
                }
            }

            // Second : Create FROM Clause
            query.append(" FROM ");
            for (int k=0; k<tables.length; k++){
                query.append(tables[k]);
                if (k<tables.length-1)
                    query.append(", ");
            }
//System.out.println("str part2 : " + str);

            // Thrid : Create WHERE Clause

            if ((where != null) && (where.length() > 0)) {
                query.append(" WHERE ");
                query.append(where);
            }

            query.append(" ORDER BY ");
            for (int l=0; l<duplicateVec.size(); l++){
                // get the 2 tables that a duplicate column belongs to
                //first find the two [x] indices of columns[x][y] where the column is present
                //next, use these indices on the tables[] array to retrieve the tables
                int idx1 = 0;
                int idx2 = 0;
                int table1, table2;
                for (table1 = 0; table1 < columns.length; table1++)
                    for (int tablCol = 0; tablCol < columns[table1].length; tablCol++)
                        if( columns[table1][tablCol] == duplicateVec.elementAt(l)) {
                            idx1 = table1;
                            break;
                        }

                for (table2 = table1; table2 < columns.length; table2++)
                    for (int tablCol = 0; tablCol < columns[table2].length; tablCol++)
                        if( columns[table2][tablCol] == duplicateVec.elementAt(l) ) {
                            idx2 = table2;
                            break;
                        }

                // now append  "<TableName1>.<DuplicateColumnName>"
                query.append(tables[idx1]);
                query.append(".");
                query.append(duplicateVec.elementAt(l));
                if (l<duplicateVec.size()-1)
                    query.append(", ");
            }

            if ((duplicateVec.size() > 0) && (uniqueVec.size() > 0))
                query.append(", ");

/*
            for (int m=0; m<uniqueVec.size(); m++){
                query.append(uniqueVec.elementAt(m));
                if (m<uniqueVec.size()-1)
                    query.append(", ");
            }
*/
            int ct2 = 0;
            for (int tabl = 0; tabl < columns.length; tabl++) {
                for (int tablCol = 0; tablCol < columns[tabl].length; tablCol++) {
                    if (duplicateVec.contains(columns[tabl][tablCol]))
                        continue;
                    else {
                        ct2++;
                        query.append(tables[tabl]);//..........
                        query.append(".");         //..........
                        query.append(columns[tabl][tablCol]);
                        if(ct2 < uniqueVec.size())
                            query.append(", ");
                    }
                }
            }

//            query.append(")");

 //   System.out.println("*****************QUERY: MULTIPLE TABLES *****************");
//    System.out.println(query.toString());
            return query.toString();
        }//else

    }//getTableQuery()

}//mySQLConnection