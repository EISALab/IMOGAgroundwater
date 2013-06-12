package ncsa.d2k.modules.core.discovery.cluster.util;

//==============
// Java Imports
//==============
import java.util.*;

//===============
// Other Imports
//===============
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.discovery.cluster.*;



public class CreateTableOfClusterCentroids extends DataPrepModule {

  //==============
  // Data Members
  //==============

  private int[] _ifeatures = null;

  //============
  // Properties
  //============
  private boolean _verbose = false;
  public boolean getVerbose() {return _verbose;}
  public void setVerbose(boolean b){_verbose = b;}

  //=======================
  // D2K Abstract Override

  public PropertyDescription[] getPropertiesDescriptions() {
    PropertyDescription[] descriptions = new PropertyDescription[1];

    descriptions[0] = new PropertyDescription("verbose",
        "Verbose Output",
        "Do you want verbose output to the console.");

    return descriptions;
  }

  /** Return information about the module.
      @return A detailed description of the module.
  */
  public String getModuleInfo() {
    String s = "<p>Overview: ";
    s += "This module takes as input a ClusterModel and outputs a new table containing ";
    s += "one row for each cluster in the model.  The values in these rows represent the ";
    s += "centroids of the clusters.  It can be used as the final stage of a table transformation ";
    s += "where a table's rows are clustered and the centroids are used to represent an ";
    s += "\"abbreviated\" form of the original data.";
    s += "</p>";

    s += "<p>Detailed Description: ";
    s += "ClusterModels usually contain the tables that were the original input ";
    s += "to the clustering algorithm as well as the clusters that were formed.  This ";
    s += "module will create a new table that contains only the centroids of each cluster ";
    s += "from the model.  Each row in the new table corresponds to one centroid from a cluster.  ";
    s += "The centroid values are all doubles because they represent points in a continuous space.  ";
    s += "Therefore, even if the original data type for an attribute was not a double (say boolean ";
    s += "or int), the value in the centroid row for that attribute is most likely no longer ";
    s += "representable as a whole number.  In any case, the types for each column in the new table are ";
    s += "all double.";
    s += "</p>";

    s += "<p>Data Type Restrictions: ";
    s += "The input ClusterModel must contain a table.";
    s += "</p>";

    s += "<p>Data Handling: ";
    s += "This module does not modify the input in any way.";
    s += "</p>";

    s += "<p>Scalability: ";
    s += "This module creates a table of the same implementation type as that contained in the ";
    s += "model.  This table is wholly new and may reside entirely in memory.";
    s += "</p>";
    return s;
  }

  /**
     Return the name of this module.
     @return The name of this module.
   */
  public String getModuleName() {
    return "Create Table of Centroids";
  }

  /**
     Return a String array containing the datatypes the inputs to this
     module.
     @return The datatypes of the inputs.
   */
  public String[] getInputTypes() {
    String[] types = {"ncsa.d2k.modules.core.discovery.cluster.ClusterModel"};
    return types;
  }

  /**
     Return a String array containing the datatypes of the outputs of this
     module.
     @return The datatypes of the outputs.
   */
  public String[] getOutputTypes() {
    String[] types = {
        "ncsa.d2k.modules.core.datatype.table.Table"};
    return types;
  }

  /**
     Return a description of a specific input.
     @param i The index of the input
     @return The description of the input
   */
  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "This is the ClusterModel that will be used to create the new table.";
      default:
        return "No such input";
    }
  }

  /**
     Return the name of a specific input.
     @param i The index of the input.
     @return The name of the input
   */
  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "ClusterModel";
      default:
        return "NO SUCH INPUT!";
    }
  }

  /**
     Return the description of a specific output.
     @param i The index of the output.
     @return The description of the output.
   */
  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "This is the newly created table of cluster centroids.";
      default:
        return "No such output";
    }
  }

  /**
     Return the name of a specific output.
     @param i The index of the output.
     @return The name of the output
   */
  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "Table";
      default:
        return "NO SUCH OUTPUT!";
    }
  }

  /**
     Perform the calculation.
   */
  public void doit() throws Exception {
    try {

      ClusterModel cm = (ClusterModel)this.pullInput(0);

      if (!cm.hasTable()){
        throw new Exception("CreateTableOfClusterCentroids: The input model does not contain a table.");
      }

      if (!(cm.getTable() instanceof MutableTable)){
        throw new Exception("CreateTableOfClusterCentroids: The input model does not contain a mutable table.");
      }

      MutableTable itable = (MutableTable)cm.getTable();

      MutableTable newtab = (new MutableTableImpl()).createTable();
      ArrayList clusters = cm.getClusters();

      if (clusters.size() == 0){
        throw new Exception("CreateTableOfClusterCentroids: The input model contains no clusters.");
      }

      TableCluster tc = null;//(TableCluster)clusters.get(0);

      if (itable instanceof ExampleTable) {
        _ifeatures = ( (ExampleTable) itable).getInputFeatures();
      } else {
        _ifeatures = new int[itable.getNumColumns()];
        for (int i = 0, n = itable.getNumColumns(); i < n; i++) {
          _ifeatures[i] = i;
        }
      }

      for (int i = 0, n = _ifeatures.length; i < n; i++){
        newtab.addColumn(new DoubleColumn(clusters.size()));
        newtab.setColumnComment(itable.getColumnComment(_ifeatures[i]), i);
        newtab.setColumnLabel(itable.getColumnLabel(_ifeatures[i]), i);
      }

      for (int i = 0, n = clusters.size(); i < n; i++){
        tc = (TableCluster)clusters.get(i);
        double[] row = tc.getCentroid();
        for (int j = 0, m = _ifeatures.length; j < m; j++){
          newtab.setDouble(row[j],i,j);
        }
      }

      if (getVerbose()){
        System.out.println("CreateTableOfClusterCentroids: Outputting table with " + newtab.getNumRows() + " rows.");
      }

      this.pushOutput(newtab, 0);

    }  catch (Exception ex) {
    ex.printStackTrace();
    System.out.println(ex.getMessage());
    System.out.println("EXCEPTION: CreateTableOfClusterCentroids.doit()");
    throw ex;
  }

  }
}
