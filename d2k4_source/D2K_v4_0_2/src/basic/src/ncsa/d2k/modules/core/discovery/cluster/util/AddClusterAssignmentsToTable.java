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
import ncsa.d2k.modules.core.discovery.cluster.hac.*;


public class AddClusterAssignmentsToTable
    extends DataPrepModule {

  //============
  // Properties
  //============
  private boolean _tableOnly = true;
  public boolean getTableOnly() {return _tableOnly;}
  public void setTableOnly(boolean b){_tableOnly = b;}

  private boolean _verbose = false;
  public boolean getVerbose() {return _verbose;}
  public void setVerbose(boolean b){_verbose = b;}

  //=======================
  // D2K Abstract Override

  /**
   * Return array of property descriptors for this module.
   * @return array
   */
  public PropertyDescription[] getPropertiesDescriptions() {
    PropertyDescription[] descriptions = new PropertyDescription[2];

    descriptions[0] = new PropertyDescription("tableOnly",
        "Output Table Only",
        "If this property is true, the modified table with the cluster assignment attribute is output. " +
        "If false, the Cluster Model, which includes the modified table, is output. " );

    descriptions[1] = new PropertyDescription("verbose",
                                              "Generate Verbose Output",
        "If this property is true, the module will write verbose status information to the console.");

    return descriptions;
  }

  /**
    Return information about the module.
    @return A detailed description of the module.
  */
  public String getModuleInfo() {
    String s = "<p>Overview: ";
    s += "This module adds an attribute to each example in the table contained in the input <i>Cluster Model</i>. ";
    s += "The new attribute specifies the cluster label as an integer value. ";
    s += "</p>";

    s += "<p>Detailed Description: ";
    s += "A <i>Cluster Model</i> contains the table that was the original input ";
    s += "to the clustering algorithm.  An additional attribute (column) is added to this ";
    s += "table with the cluster assignments for each example (row).  ";
    s += "The new attribute is of type integer.  ";
    s += "</p>";

    s += "<p>";
    s += "The <i>Output Table Only</i> property controls whether the ";
    s += "modified table or the entire Cluster Model, ";
    s += "which includes the modified table, is output. ";
    s += "The <i>Cluster Model</i> object implements the Table interface and delegates ";
    s += "all calls to that interface to the contained table.";
    s += "</p>";

    s += "<p>Data Type Restrictions: ";
    s += "The input <i>Cluster Model</i> must contain a table, and that table must be mutable.";
    s += "</p>";

    s += "<p>Data Handling: ";
    s += "This module will modify (as described above) the table that is contained in ";
    s += "the input <i>Cluster Model</i>.  If the table is an example table, the ";
    s += "new column is added as an output attribute.";
    s += "</p>";

    s += "<p>Scalability: ";
    s += "The module does not create a new table but adds a column to the existing table. ";
    s += "The cost of this operation will vary depending on the table implementation type. ";
    s += "For in-memory table implementations, it will add a memory cost equal to the size of one integer ";
    s += "per example in the table. ";
    s += "</p>";
    return s;
  }

  /**
     Return the name of this module.
     @return The name of this module.
   */
  public String getModuleName() {
    return "Add Cluster Assignments";
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
     Return a description of a specific input.
     @param i The index of the input
     @return The description of the input
   */
  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "The Cluster Model whose table will be modified.";
      default:
        return "No such input.";
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
        return "Cluster Model";
      default:
        return "No such input";
    }
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
     Return the description of a specific output.
     @param i The index of the output.
     @return The description of the output.
   */
  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "The modified table, possibly as part of the full Cluster Model.";
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
        return "Table or Cluster Model";
      default:
        return "No such output";
    }
  }

  /**
     Perform the work of the module.
   */
  public void doit() throws Exception {

    boolean exceptionFlag = false;
    try {

      ClusterModel cm = (ClusterModel)this.pullInput(0);

      if (!cm.hasTable()){
	exceptionFlag = true;
        throw new Exception(getAlias() + ": The input model does not contain a table.");
      }

      if (!(cm.getTable() instanceof MutableTable)){
	exceptionFlag = true;
        throw new Exception(getAlias() + ": The input model does not contain a mutable table.");
      }

      MutableTable itable = (MutableTable)cm.getTable();

      //remove pre-existing cluster column
      if (itable.getColumnLabel(itable.getNumColumns() - 1).equals(HAC._CLUSTER_COLUMN_LABEL)) {
        itable.removeColumn(itable.getNumColumns() - 1);
      }
      itable.addColumn(new IntColumn(itable.getNumRows()));
      itable.setColumnLabel(HAC._CLUSTER_COLUMN_LABEL, itable.getNumColumns() - 1);
      if (itable instanceof ExampleTable){
        int[] outs = ((ExampleTable)itable).getOutputFeatures();
        int[] newouts = new int[outs.length + 1];
        System.arraycopy(outs,0,newouts,0,outs.length);
        //LAM-tlr, I change the following : newouts[newouts.length-1] = newouts.length-1;
        // to: 
		newouts[newouts.length-1] = itable.getNumColumns()-1;
        ((ExampleTable)itable).setOutputFeatures(newouts);
      }
      ArrayList resultClusters = cm.getClusters();
      for (int i = 0, n = resultClusters.size(); i < n; i++) {
        TableCluster tc = (TableCluster) resultClusters.get(i);
        int[] rows = tc.getMemberIndices();
        int col = itable.getNumColumns() - 1;
        for (int j = 0, m = rows.length; j < m; j++) {
          itable.setInt(i, rows[j], col);
        }
        if (getVerbose()){
          System.out.println(getAlias() + ": Cluster " + tc.getClusterLabel() + " containing " +
                             tc.getSize() + " elements.");
        }
      }

      if (this.getTableOnly()){
        this.pushOutput(itable, 0);
      } else {
        this.pushOutput(cm, 0);
      }

    }  catch (Exception ex) {
      if ( ! exceptionFlag ) {
        ex.printStackTrace();
        System.out.println( "EXCEPTION: " + getAlias() + ": " + ex.getMessage());
      }
      throw ex;
    }
  }
}

// Start QA Comments
// 4/9/03 - Ruth Starts QA
//        - Changed module name to Add Cluster Assignments (was Add Cluster Column) to be
//          closer to what the class name is.  Updated module info.  Added flag to
//          'expected exceptions' so that message isn't output twice.
// 4/10/03 - Ready for QA
// End QA Comments

