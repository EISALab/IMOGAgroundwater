package ncsa.d2k.modules.core.transform.binning;

import java.sql.*;
import java.util.HashMap;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.io.sql.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.transformations.*;



public class SQLCreateBinTree extends DataPrepModule {

	public String[] getInputTypes() {
		String[] in =
			{
				"ncsa.d2k.modules.core.datatype.table.transformations.BinTransform",
				"ncsa.d2k.modules.io.sql.DBConnection",
				"java.lang.String",
				"ncsa.d2k.modules.core.datatype.table.ExampleTable" };
		return in;
	}

	public String[] getOutputTypes() {
		String[] out = { "ncsa.d2k.modules.core.transform.binning.BinTree" };
		return out;
	}

	public String getInputInfo(int i) {
		switch (i) {
			case 0 :
				return "BinningTransformation containing the bin definitions";
			case 1 :
				return "connection to the database";
			case 2 :
				return "name of the table containing the data";
			case 3 :
				return "ExampleTable containing the names of the input/output features";
			default :
				return "No such input";
		}
	}

	public String getOutputInfo(int i) {
		return "BinTree containing counts for all bins";
	}

	public String getInputName(int i) {
		switch (i) {
			case 0 :
				return "Binning Transformation";
			case 1 :
				return "Database Connection";
			case 2 :
				return "Database Table Name";
			case 3 :
				return "Meta Data Example Table";
			default :
				return "No such input";
		}
	}

	public String getOutputName(int i) {
		return "Bin Tree";
	}

	public String getModuleInfo() {

		StringBuffer sb = new StringBuffer("<p>Overview: ");
		sb.append("Creates and fills a BinTree with counts");
		sb.append("<p>Detailed Description: ");
		sb.append(
			"Given a BinTransform containing the definition of the bins, ");
		sb.append(
			" a connection to the database, the table name and an ExampleTable ");
		sb.append(
			"that has the input/ output attribute labels and types, builds a BinTree ");
		sb.append(
			"and fills the tree with the needed counts using SQL queries. ");
		sb.append(
			"<p> Scalability: Each count requires a table scan, it is recommended ");
		sb.append(
			"for best performance, that the table is indexed on the class column. ");
		return sb.toString();

	}

	public String getModuleName() {
		return "SQL Create Bin Tree";
	}

	public void doit() throws Exception{
		BinTransform btrans = (BinTransform) pullInput(0);
		DBConnection conn = (DBConnection) pullInput(1);
		String tableName = (String) pullInput(2);
		//ExampleTable et = (ExampleTable) pullInput(3);
		ExampleTable et;
			try {
					et = (ExampleTable) pullInput(3);
			} catch ( ClassCastException ce) {
				throw new Exception(
								getAlias()
									+ ": Select input/output features using SQLChooseAttributes before this module");
			}

		// get the attributes names from the input features
		int[] inputFeatures = et.getInputFeatures();
		if ((inputFeatures == null) || (inputFeatures.length == 0))
		    throw new Exception(getAlias() + ":Please select the input features, they are missing.");
		String[] an = new String[inputFeatures.length];
		for (int i = 0; i < inputFeatures.length; i++)
		    an[i] = et.getColumnLabel(i);//.toLowerCase();

		// get the class name from the outputFeatures
		int[] outputFeatures = et.getOutputFeatures();
		if (outputFeatures == null || outputFeatures.length == 0)
		    throw new Exception(getAlias() + ": Please select an output feature, it is missing");

		String classLabel = et.getColumnLabel(outputFeatures[0]);
		if( et.isColumnScalar(outputFeatures[0]))
		    throw new Exception(getAlias() + ": Output feature must be nominal. Please transform it.");

		if (btrans == null)
		    throw new Exception(getAlias() + ": Bins must be defined before creating a BinTree");

		BinDescriptor [] bins = btrans.getBinDescriptors();

		if(bins.length == 0 || bins.length < inputFeatures.length )
		    throw new Exception(getAlias() +
			      ": Bins must be defined for each input before creating BinTree.");

		//System.out.println("CreateSQLBinTree classLabel " + classLabel);
		int totalClassified = 0;
		int classTotal;
		int binListTotal;

		long startTime = System.currentTimeMillis();

		// get sql counts and set the tallys
		BinTree bt = null;
		try {
			//get all the unique class values for the given output feature
			HashMap items = new HashMap();
			Statement stmt0 = conn.getConnection().createStatement();
			ResultSet uniqueItems =
				stmt0.executeQuery(
					"SELECT DISTINCT " + classLabel + " FROM " + tableName);
			int i = 0;
			while (uniqueItems.next()) {
				String s = uniqueItems.getString(1);
				items.put(new Integer(i++), s);
			}
			int classNum = items.size();
			String[] cn = new String[classNum];
			for (i = 0; i < classNum; i++)
				cn[i] = (String) items.get(new Integer(i));

			// given feature names and class values create bin tree
			bt = CreateBinTree.createBinTree(btrans.getBinDescriptors(), cn, an);

			//bt.printAll();
			//determine the counts in each bin
			Statement stmt = conn.getConnection().createStatement();
			for (i = 0; i < cn.length; i++) {
				classTotal = 0;
				for (int j = 0; j < an.length; j++) {
					String[] bn = bt.getBinNames(cn[i], an[j]);
					binListTotal = 0;

					for (int k = 0; k < bn.length; k++) {
						BinTree.ClassTree ct =
							(BinTree.ClassTree) bt.get(cn[i]);
						BinTree.ClassTree.BinList bl =
							(BinTree.ClassTree.BinList) ct.get(an[j]);
						BinTree.ClassTree.Bin b =
							(BinTree.ClassTree.Bin) bl.get(bn[k]);
						String condition = b.getCondition(an[j]);

                                                //vered - debug
                                                if(bn == null)
                                                  System.out.println("bn is null");
                                                else if(bn[k] == null)
                                                  System.out.println("bn[k] is null");
                                                if(an == null)
                                                  System.out.println("an is null");
                                                else if(an[j] == null)
                                                 System.out.println("an[j] is null");
                                                 //end debug

						if (bn[k].equals("Unknown")) condition = an[j] + " is null";
						//else System.out.println("not unknown");
						//System.out.println("cn, an, bn, BIN Condition: "
						  //             + cn[i] + " " + an[j] + " "
						    //          + bn[k] + " " + condition);

			String	query =
							"SELECT COUNT(*)  FROM "
								+ tableName
								+ " WHERE "
								+ classLabel
								+ " = \'"
								+ cn[i]
								+ "\' AND "
								+ condition;
												
						if(debug) System.out.println("BIN Query: "+ query);
						ResultSet count =
							stmt.executeQuery(
								"SELECT COUNT(*)  FROM "
									+ tableName
									+ " WHERE "
									+ classLabel
									+ " = \'"
									+ cn[i]
									+ "\' AND "
									+ condition);

						while (count.next()) {
							int s = count.getInt(1);
							b.setTally(s);
							totalClassified = totalClassified + s;
							classTotal = classTotal + s;
							binListTotal = binListTotal + s;
							//if (debug) System.out.println("COUNT(class:" + cn[i] +
							//" ,att:" + condition + ")=" + s);
						}
						bl.setTotal(binListTotal);
						count.close();
					}
				}
				bt.setClassTotal(cn[i], classTotal);
			}

			bt.setTotalClassified(totalClassified);
		} catch (SQLException e) {
			System.out.println(e);
		} catch (ClassNotFoundException ne) {
			System.out.println(ne);
		} catch (InstantiationException ee) {
			System.out.println(ee);
		} catch (IllegalAccessException nie) {
			System.out.println(nie);
		};

		long endTime = System.currentTimeMillis();
		if (debug) {
			System.out.println("time in msec " + (endTime - startTime));
			bt.printAll();
		}
		pushOutput(bt, 0);
	}

	boolean debug;

	public boolean getDebug() {
		return debug;
	}

	public void setDebug(boolean deb) {
		debug = deb;
	}
	public PropertyDescription[] getPropertiesDescriptions(){
      PropertyDescription[] pd = new PropertyDescription[1] ;
      pd[0] = new PropertyDescription("debug", "Generate Verbose Output",
            "This property controls the module's output to the stdout. " +
	    "If set to true, the created BinTree will be printed together with the SQL queries " +
 	    "used to fill the tree .");
      return pd;
     }

}

//12-11 -03 Anca - for "unknown" named bins query condition "an is null " is generated now
//12-12--03 Anca - added check and exception for input table that is not an ExampleTable

