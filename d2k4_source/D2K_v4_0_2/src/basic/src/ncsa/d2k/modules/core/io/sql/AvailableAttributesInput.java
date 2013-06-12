package ncsa.d2k.modules.core.io.sql;


import ncsa.d2k.core.modules.InputModule;
import java.sql.*;
import java.util.Vector;
import javax.swing.*;

public class AvailableAttributesInput extends InputModule
{
  JOptionPane msgBoard = new JOptionPane();

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "Available Attributes Input";
	}

	public String getModuleInfo () {
          String s = "<p>Overview: ";
          s += "This module builds a list of attributes (fields) available in a specified database table. </p>";
          s += "<p>Detailed Description: ";
          s += "This module makes a connection to the database indicated by the ";
          s += "<i>Database Connection</i> input port and builds a ";
          s += "list of available attributes for the database table indicated by the ";
          s += "<i>Selected Table</i> input port. ";
 	  s += "The list is made available to other modules via the <i>Attributes List</i> output port. ";
          s += "<p>Restrictions: ";
          s += "Only Oracle, SQLServer, DB2 and MySql databases are currently supported.";

          return s;
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
				return "Selected Table";
			default:
				return "No such input";
		}
	}

	public String getInputInfo (int index) {
		switch (index) {
			case 0:
				return "The database connection.";
			case 1:
				return "The name of the selected table, as specified in a previous module.";
			default:
				return "No such input";
		}
	}

	public String[] getInputTypes () {
		String[] types = {"ncsa.d2k.modules.io.input.sql.ConnectionWrapper","java.lang.String"};
		return types;
	}

	/**
	 * Return the human readable name of the indexed output.
	 * @param index the index of the output.
	 * @return the human readable name of the indexed output.
	 */
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Database Connection";
			case 1:
				return "Attributes List";
			default:
				return "No such output";
		}
	}

	public String getOutputInfo (int index) {
		switch (index) {
 			case 0: return "The database connection, for use by the next module.";
			case 1: return "A list of the attributes available in the selected table.";
                        default: return "No such output.";
		}
	}

	public String[] getOutputTypes () {
		String[] types = {"ncsa.d2k.modules.core.io.sql.ConnectionWrapper","java.util.Vector"};
		return types;
	}

	public void doit () throws Exception
	{
		String tableName = (String) this.pullInput (1);
                if (tableName == null || tableName.length()== 0) {
                 /* JOptionPane.showMessageDialog(msgBoard,
                            "No table is selected.", "Error",
                            JOptionPane.ERROR_MESSAGE);*/
                  System.out.println("No table is selected");

                }
                else {
                  ConnectionWrapper cw = (ConnectionWrapper) this.pullInput (0);
                  Connection con = cw.getConnection();
                  Vector v = new Vector();
                  Vector c = new Vector();
                  DatabaseMetaData dbmd = con.getMetaData();
                  ResultSet tableSet = dbmd.getColumns (null,null,tableName,"%");
                  while (tableSet.next())
			v.addElement(tableSet.getString("COLUMN_NAME"));

                  this.pushOutput (cw, 0);
                  this.pushOutput (v, 1);
                }
	}

}
