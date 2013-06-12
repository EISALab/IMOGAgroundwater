package ncsa.d2k.modules.core.io.sql;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.datatype.table.*;
import java.sql.*;
import java.beans.PropertyVetoException;
import java.io.*;
import javax.swing.*;

/**
 * @this module writes the data from a table in database to a file
 * @author Hong Cheng
 * @version 1.0
 */


public class WriteDBToFile extends OutputModule
{

        transient String delimiter;

        String  delimChar = "C";
        boolean useDataTypes = true;
        boolean useColumnLabels = true;

        public void setDelimChar(String c) throws PropertyVetoException {
           // here we check for valid entries and save as upper case
           if (c.equalsIgnoreCase("C")) {
              delimChar = "C";
           } else if (c.equalsIgnoreCase("S")) {
              delimChar = "S";
           } else if (c.equalsIgnoreCase("T")) {
              delimChar = "T";
           } else {
              throw new PropertyVetoException(
                   "An invalid Delimiter Character was entered. "+
                   "Enter C for comma, S for space, or T for tab.",
                   null);
           }
        }

        public String getDelimChar() {
           return delimChar;
        }

        public void setUseDataTypes(boolean b) {
           useDataTypes = b;
        }

        public boolean getUseDataTypes() {
           return useDataTypes;
        }

        public void setUseColumnLabels(boolean b) {
           useColumnLabels = b;
        }

        public boolean getUseColumnLabels() {
           return useColumnLabels;
        }

        public PropertyDescription[] getPropertiesDescriptions() {

            PropertyDescription[] descriptions = new PropertyDescription [3];

            descriptions[0] = new PropertyDescription("delimChar",
                    "Delimiter Character (C=comma, S=space, T=tab)",
                    "Selects the delimiter character used to separate columns in the file.  "+
                    "Enter C for comma, S for space, or T for tab.");

            descriptions[1] = new PropertyDescription("useColumnLabels",
                    "Write Column Labels",
                    "Controls whether the column labels should be written to the file.");

            descriptions[2] = new PropertyDescription("useDataTypes",
                    "Write Data Types",
                    "Controls whether the column data types should be written to the file.");
            return descriptions;

        }



        /**
                This method returns the description of the various inputs.
                @return the description of the indexed input.
        */
        public String getInputInfo(int index) {
                switch (index) {
                        case 0: return "      This manages the sql database connection object.   ";
                        case 1: return "      The names of the fields needed from within the table.   ";
                        case 2: return "      The name of the table containing the fields.   ";
                        case 3: return "      Contains the where clause for the sql query (Optional).   ";
                        case 4: return "      The name of the file to write to.   ";
                        default: return "No such input";
                }
        }

        /**
                This method returns an array of strings that contains the data types for the inputs.
                @return the data types of all inputs.
        */
        public String[] getInputTypes () {
                String[] types = {"ncsa.d2k.modules.core.io.sql.ConnectionWrapper",
                                  "[Ljava.lang.String;","java.lang.String",
                                  "java.lang.String","java.lang.String"};
                return types;
        }

        /**
                This method returns the description of the outputs.
                @return the description of the indexed output.
        */
        public String getOutputInfo (int index) {
                switch (index) {
                        default: return "No such output";
                }
        }

        /**
                This method returns an array of strings that contains the data types for the outputs.
                @return the data types of all outputs.
        */
        public String[] getOutputTypes () {
            String[] types = {		};
            return types;
        }

        /**
                This method returns the description of the module.
                @return the description of the module.
        */
        public String getModuleInfo () {
          String s = "<p>Overview: ";
          s += "This module constructs a SQL statement, and retrieves data from a database and writes to a file. </p>";
          s += "<p>Detailed Description: ";
          s += "This module constructs a SQL query based on 5 inputs: the database ";
          s += "connection object, the selected table, the selected fields, ";
          s += "the query condition (optional), and the name of the file to write to. ";
          s += "This module then executes the query and retrieves ";
          s += "the data from the specified database and outputs ";
          s += "database data to a specified file. </p>";
          s += "<p>Restrictions: ";
          s += "We currently only support Oracle, SqlServer, DB2 and MySql databases. </p>";
          return s;
        }

        /**
                PUT YOUR CODE HERE.
        */
        public void doit () throws Exception {


                FileWriter fw;

                String newLine = "\n";
                delimiter = ",";      // default to comma
                if (delimChar.equals("S")) {
                        delimiter = " ";
                } else if (delimChar.equals("T")) {
                        delimiter = "\t";
                }

                // We need a connection wrapper
                ConnectionWrapper cw = (ConnectionWrapper) this.pullInput (0);
                Connection con = cw.getConnection();
                String[] fieldArray = (String[]) this.pullInput (1);

                // get the list of fields to read.
                StringBuffer fieldList = new StringBuffer(fieldArray[0]);
                for (int i=1; i<fieldArray.length; i++)
                        fieldList.append(", "+fieldArray[i]);

                // get the name of the table.
                String tableList = (String) this.pullInput (2);

                // get the query condition.
                String whereClause="";
                if (isInputPipeConnected(3)) {
                  whereClause = (String)pullInput(3);
                  if (whereClause.length()==0)
                    whereClause = null;
                }
                else if (!isInputPipeConnected(3)) {
                  whereClause = null;
                }

                // get the name of the file to write to
                String fileName = (String) this.pullInput(4);
                fw = new FileWriter(fileName);

                ///////////////////////////
                // Get the column types, and create the appropriate column
                // objects

                // construct the query to get clumn information.
                String query = "SELECT "+fieldList.toString()+" FROM "+tableList;

                if (whereClause != null && whereClause.length() > 0)
                   query += " WHERE " + whereClause;

                // Get the number of columns selected.
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                ResultSetMetaData rsmd = rs.getMetaData ();
                int numColumns = rsmd.getColumnCount ();
                MutableTableImpl vt =  new MutableTableImpl(numColumns);


                if(useColumnLabels) {
                  for(int i = 0; i < numColumns; i++) {
                    String s = rsmd.getColumnName(i+1);
                    if (s == null || s.length() == 0)
                      s = "column_" + i;
                    fw.write(s, 0, s.length());
                    if(i != (rsmd.getColumnCount()-1))
                      fw.write(delimiter.toCharArray(), 0, delimiter.length());
                  }
                  fw.write(newLine.toCharArray(), 0, newLine.length());
                }

                if(useDataTypes)
                {
                        String type;
                        for (int i = 0; i < numColumns; i ++)
                        {
                                type = rsmd.getColumnTypeName(i+1);
                                if (ColumnTypes.isContainNumeric(type))
                                  type = "double";
                                else if(type == null || type.length() == 0)
                                  type = "Undefined";
                                else
                                  type = "string";
                                fw.write(type, 0, type.length());
                                if(i != (rsmd.getColumnCount() - 1))
                                        fw.write(delimiter.toCharArray(), 0, delimiter.length());
                        }
                        fw.write(newLine.toCharArray(), 0, newLine.length());
                }

                // Now compile a list of the datatypes.
                int [] types = new int [numColumns];
                for (int i = 0 ; i < numColumns ; i++)
                        types[i] = rsmd.getColumnType (i+1);

                // Now populate the table.
                for (int where = 0; rs.next (); where++){
                        for (int i = 0 ; i < numColumns ; i++) {
                                switch (types[i]) {
                                        case Types.TINYINT:
                                        case Types.SMALLINT:
                                        case Types.INTEGER:
                                        case Types.BIGINT:
                                                if (rs.getString(i+1) == null) {
                                                  fw.write("");
                                                }
                                                else
                                                  fw.write(rs.getInt(i+1));
                                                break;
                                        case Types.DOUBLE:
                                        case Types.NUMERIC:
                                        case Types.DECIMAL:
                                        case Types.FLOAT:
                                        case Types.REAL:
                                                if (rs.getString(i+1) == null) {
                                                  fw.write("");
                                                }
                                                else
                                                  fw.write(Double.toString(rs.getDouble(i+1)));
                                                break;
                                        case Types.CHAR:
                                        case Types.VARCHAR:
                                        case Types.LONGVARCHAR:
                                                if (rs.getString(i+1) == null) {
                                                  fw.write("");
                                                }
                                                else
                                                  fw.write(rs.getString(i+1));
                                                break;
                                        default:
                                                rs.getString(i+1);
                                                break;
                                }//switch

                                if(i != (numColumns - 1))
                                        fw.write(delimiter.toCharArray(), 0, delimiter.length());

                              }//for i
                              fw.write(newLine.toCharArray(), 0, newLine.length());
                        }
                        fw.flush();
                        fw.close();
}
        /**
         * Return the human readable name of the module.
         * @return the human readable name of the module.
         */
        public String getModuleName() {
                return "Write DB To File";
        }

        /**
         * Return the human readable name of the indexed input.
         * @param index the index of the input.
         * @return the human readable name of the indexed input.
         */
        public String getInputName(int index) {
                switch(index) {
                        case 0:
                                return "Database Connection";
                        case 1:
                                return "Selected Fields";
                        case 2:
                                return "Selected Table";
                        case 3:
                                return "Query Condition (Optional)";
                        case 4:
                                return "File Name";

                        default: return "NO SUCH INPUT!";
                }
        }

        /**
         * Return the human readable name of the indexed output.
         * @param index the index of the output.
         * @return the human readable name of the indexed output.
         */
        public String getOutputName(int index) {
                switch(index) {
                        default: return "NO SUCH OUTPUT!";
                }
        }

        public boolean isReady() {
          if (!isInputPipeConnected(3)) {
            return (getInputPipeSize(0)>0 &&
                    getInputPipeSize(1)>0 &&
                    getInputPipeSize(2)>0 &&
                    getInputPipeSize(4)>0);
          }
          return super.isReady();
        }
}

