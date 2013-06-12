package ncsa.d2k.modules.core.io.sql;


import ncsa.d2k.core.modules.InputModule;
import java.sql.*;
import java.util.Vector;
import java.beans.PropertyVetoException;
import ncsa.d2k.core.modules.*;

public class AvailableTablesInput extends InputModule
{
	/** controls what to list **/
        /** NOTE:  These variable names imply that only one or the other can be
         ** retrieved, but in fact both can.  We didn't update the variable names
         ** when we changed the logic because we wanted old itinieraries to continue
         ** to work.   **/
	protected boolean dataTableOnly = true;
        protected boolean dataCubeOnly = false;

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "Available Tables Input";
	}

	public String getModuleInfo () {
          String s = "<p>Overview: ";
          s += "This module builds a list of available database tables. </p>";
          s += "<p>Detailed Description: ";
          s += "This module makes a connection to the database indicated by the ";
	  s += "<i>Database Connection</i> input port and builds a ";
          s += "list of available database tables. There are two types of tables in a ";
          s += "database, raw data tables and aggregated cube tables. The ";
          s += "properties <i>List Data Tables</i> and <i>List Cube Tables</i> allow ";
          s += "the user to control whether one or both types of tables will be included in the list.</p>";
          s += "<p>For security, ";
          s += "users may only view the tables they have been granted permission to access. If you ";
          s += "cannot see the tables you are looking for, please report the ";
          s += "problems to your database administrator. </p>";
          s += "<p> Restrictions: ";
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
			default:
				return "No such input";
		}
	}

	public String getInputInfo (int index) {
		switch (index) {
			case 0: return "The database connection used to discover the available tables.";
			default: return "No such input.";
		}
	}

	public String[] getInputTypes () {
		String[] types = {"ncsa.d2k.modules.core.io.sql.ConnectionWrapper"};
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
				return "Tables List";
			default:
				return "No such output";
		}
	}

	public String getOutputInfo (int index) {
		switch (index) {
			case 0: return "The database connection, for use by the next module.";
			case 1: return "A list of available tables of the specified type(s).";
			default: return "No such output.";
		}
	}

	public String[] getOutputTypes () {
		String[] types = {"ncsa.d2k.modules.core.io.sql.ConnectionWrapper","java.util.Vector"};
		return types;
	}

	/**
		Get the value of dataTableOnly
		@return true if data tables should be listed.
		false otherwise
	*/
	public boolean getDataTableOnly() {
		return dataTableOnly;
	}

	/**
		Set the the value of dataTableOnly
		@param b true if data tables should be listed.
		false otherwise
	*/
	public void setDataTableOnly(boolean b) {
		dataTableOnly = b;
	}

	/**
		Get the value of dataCubeOnly
		@return true if data cubes should be listed.
		false otherwise
	*/
	public boolean getDataCubeOnly() {
		return dataCubeOnly;
	}

	/**
		Set the the value of dataCubeOnly
		@param b true if data tables should be listed.
		false otherwise
	*/
	public void setDataCubeOnly(boolean b) throws PropertyVetoException {
		dataCubeOnly = b;

		if ( !dataTableOnly && !dataCubeOnly ) {
      		    throw new PropertyVetoException(
			"\nYou must set either List Data Tables or List Data Cubes to True.", null );
                }
	}

        public PropertyDescription [] getPropertiesDescriptions () {
          PropertyDescription [] pds = new PropertyDescription [2];
          pds[0] = new PropertyDescription ("dataTableOnly", "List Data Tables", "Choose True if you want to list data tables.");
          pds[1] = new PropertyDescription ("dataCubeOnly", "List Data Cubes", "Choose True if you want to list data cubes.");
          return pds;
        }

	protected void doit ()  throws Exception
	{
		ConnectionWrapper cw = (ConnectionWrapper) this.pullInput (0);
		Connection con = cw.getConnection();
		Vector v = new Vector();

                DatabaseMetaData metadata = null;
                con = cw.getConnection();
                metadata = con.getMetaData();
                String[] types = {"TABLE"};
                ResultSet tableNames = metadata.getTables(null,"%","%",types);
                while (tableNames.next()) {
                  String aTable = tableNames.getString("TABLE_NAME");
                  if (dataTableOnly) {
                    if (aTable.toUpperCase().indexOf("CUBE") < 0) {
                      v.addElement(aTable);
                    }
                  }
                  if (dataCubeOnly) {
                    if (aTable.toUpperCase().indexOf("CUBE") >= 0) {
                      v.addElement(aTable);
                    }
                  }
                }

		this.pushOutput (cw, 0);
		this.pushOutput (v, 1);
	}


}

