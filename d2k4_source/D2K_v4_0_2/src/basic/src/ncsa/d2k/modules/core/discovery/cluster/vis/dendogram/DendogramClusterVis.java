package ncsa.d2k.modules.core.discovery.cluster.vis.dendogram;

//==============
// Java Imports
//==============

//===============
// Other Imports
//===============
import ncsa.d2k.core.modules.*;

/**
 *
 * <p>Title: DendogramClusterVis</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: NCSA Automated Learning Group</p>
 * @author D. Searsmith
 * @version 1.0
 */
public class DendogramClusterVis
    extends VisModule {

  //==============
  // Data Members
  //==============


  //================
  // Constructor(s)
  //================

  public DendogramClusterVis() {
  }

  //================
  // Static Methods
  //================

  static public void main(String[] args) {
  }

  //======================
  // ViewModule Abstracts

  /**
   * Create a new instance of a UserView object that provides the
   * user interface for this user interaction module.
   *
   * @return a new instance of a UserView providing the interface
   * for this module.
   */
  protected UserView createUserView() {
    return new DendogramPanel(this);
  }

  /**
   * The list of strings returned by this method allows the
   * module to map the results returned from the pier to the
   * position dependent outputs. The order in which the names
   * appear in the string array is the order in which to assign
   * them to the outputs.
   *
   * @return a string array containing the names associated with
   * the outputs in the order the results should appear in the outputs.
   */
  protected String[] getFieldNameMapping() {
    return null;
  }

  //========================
  // D2K Abstract Overrides

  /**
     Return the name of this module.
     @return The name of this module.
   */
  public String getModuleName() {
    return "Dendogram Vis";
  }

  /**
     Return the name of a specific output.
     @param i The index of the output.
     @return The name of the output
   */
  public String getOutputInfo(int parm1) {
    return "";
  }

  /**
     Return a String array containing the datatypes of the outputs of this
     module.
     @return The datatypes of the outputs.
   */
  public String[] getOutputTypes() {
    String[] in = null;
    return in;
  }

  /**
    Return information about the module.
    @return A detailed description of the module.
   */
  public String getModuleInfo() {
    String s = "<p>Overview: ";
    s += "This module visualizes a ClusterModel object that is the output of an ";
    s += "hierarchical agglomerative clustering algorithm.  The visualization is ";
    s += "of the dendogram produced by the agglomerative (bottom-up) process.";
    s += "</p>";

    s += "<p>Detailed Description: ";
    s += "The dendogram produced represents a hard clustering of a data table using ";
    s += "a hierarchical agglomerative clustering algorithm.  Some cluster models will ";
    s += "contain complete dendogram trees (from the actual table rows to the single ";
    s += "root cluster.  Other models will contain trees that start at some cluster cut ";
    s += "(for example the clusters returned from a KMeans algorithm) to the root. ";
    s += "Also, not all trees are monotonic (i.e. the height of subclusters is always <= ";
    s += "to the height of their parents where height is measured in cluster dissimilarity. ";
    s += "In particular, the centroid clustering methods (see HierAgglomClusterer props) are ";
    s += "known to sometimes produce non-monotonic dendogram trees.";
    s += "</p>";

    s += "<p> GUI Controls: ";
    s += "If you double click on a cluster in the tree the dendogram will be repainted with ";
    s += "the chosen cluster as root.  If you hit the reset button the original root will be ";
    s += "restored.  If you double click on a cluster while holding down the shift key a table ";
    s += "of values for that cluster will be displayed.  If you double click on a cluster ";
    s += "while holding down the control key the centroid for that cluster will be displayed. ";
    s += "</p>";

    s += "<p>Data Type Restrictions: ";
    s += "The input ClusterModel must contain a table that is serializable.";
    s += "</p>";

    s += "<p>Data Handling: ";
    s += "The input ClusterModel will be saved as part of the visualization.  It is not modified.";
    s += "</p>";

    s += "<p>Scalability: ";
    s += "The entire data table is saved as part of this visualization.  Sufficient ";
    s += "memory resources must be available to stage this data.";
    s += "</p>";
    return s;
  }

  /**
     Return a description of a specific input.
     @param i The index of the input
     @return The description of the input
   */
  public String getInputInfo(int parm1) {
    if (parm1 == 0) {
      return "ncsa.d2k.modules.core.discovery.cluster.ClusterModel";
    } else {
      return "";
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
        return "NO SUCH INPUT!";
    }
  }

  /**
     Return a String array containing the datatypes the inputs to this
     module.
     @return The datatypes of the inputs.
   */
  public String[] getInputTypes() {
    String[] out = {
        "ncsa.d2k.modules.core.discovery.cluster.ClusterModel"};
    return out;
  }

  /**
     Return an array of the property variables, labels, and descriptions.
     @return The editable properties of the module - none in this case
   */
  public PropertyDescription[] getPropertiesDescriptions() {
    // hide properties that the user shouldn't udpate
    return new PropertyDescription[0];
  }

}
