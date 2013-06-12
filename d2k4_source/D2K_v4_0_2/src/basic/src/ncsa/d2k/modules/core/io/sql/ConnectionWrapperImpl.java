package ncsa.d2k.modules.core.io.sql;



import java.sql.*;

import javax.swing.*;



/**

  ConnectionWrapper is a wrapper to java.sql.Connection

  It is passed around between the modules in the

  ncsa.d2k.modules.sql package.  If the modules

  are all running on the same machine, the connection

  is shared.  If the ConnectionWrapper gets passed to

  another machine, a new connection is established

  using the info contained herein.

*/

public class ConnectionWrapperImpl implements ConnectionWrapper, java.io.Serializable

{

	/** The connection. */

	private transient Connection connection;



	/** The url identifies the data source when establishing the connection. */

	private String url;



	/** The fully qualified classname of the JDBC driver needs to be known

	  if the vm doesn't have the class loaded when attempting to establish

	  the connecion. */

	private String driver;



	/** If username and password are not null the version of Driver.getConnection()

	  that takes username and password arguments will be used. */

	private String username;

	private String password;

        JOptionPane msgBoard = new JOptionPane();



	public ConnectionWrapperImpl

		(String _url, String _driver, String _username, String _password)

	{

		url = _url;

		driver = _driver;

		username = _username;

		password = _password;

	} /* ConnectionWrapper(String,String,String,String) */





    public Connection getConnection()

    {

      try {

        //System.out.println ("Entering getConnection");

        if (connection == null) {

          Class classNm = Class.forName (driver);

          DriverManager.registerDriver ((Driver) Class.forName (driver).newInstance ());



          // make the connection

          if (username == null) {

            connection = DriverManager.getConnection( url );

          }

          else {

            //System.out.println ("URL is "+url);

            connection = DriverManager.getConnection( url, username, password );

          }

        }

        //System.out.println("Successful Connection");

        return connection;

      }

      catch (Exception e){

        JOptionPane.showMessageDialog(msgBoard,

          "Cannot connect to the database. Please check your 'Connect To DB' "+

          "properties and verify that the appropriate driver is installed. ", "Error",

          JOptionPane.ERROR_MESSAGE);

        System.out.println("Error occurred when connecting to a database.");

        return null;

      }



    }





    protected void finalize() {

        try {

            if(connection != null)

                connection.close();

        }

        catch(Exception e) {

        }

    }



}/* ConnectionWrapper */

