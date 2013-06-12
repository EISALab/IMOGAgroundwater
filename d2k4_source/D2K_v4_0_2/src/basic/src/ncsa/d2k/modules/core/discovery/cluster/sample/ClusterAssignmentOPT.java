package ncsa.d2k.modules.core.discovery.cluster.sample;

/**
 * <p>
 * Title: ClusterAssignmentOPT
 * </p>
 * <p>
 * Description: Takes a set of centroids and a table and repeatedly assigns
 * table rows to clusters whose centroids are closest in vector space.  When one
 * assignment is completed new centroids are calculated and the process is repeated.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: NCSA Automated Learning Group
 * </p>
 * @author D. Searsmith
 * @version 1.0
 */

//==============
// Java Imports
//==============

//===============
// Other Imports
//===============

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.discovery.cluster.hac.*;

public class ClusterAssignmentOPT
    extends ComputeModule implements ClusterParameterDefns{

  //==============
  // Data Members
  //==============

  // Distance Metric
  static public final int s_Euclidean = 0;
  static public final int s_Manhattan = 1;
  static public final int s_Cosine = 2;

  private long m_start = 0;
  protected int[] _ifeatures = null;

  protected int _distanceMetric = s_Euclidean;
  protected int m_numAssignments = 5;
  protected int _clusterMethod = HAC.s_WardsMethod;

  //============
  // Properties
  //============

  protected boolean m_verbose = false;
  public boolean getVerbose() {
    return m_verbose;
  }

  public void setVerbose(boolean b) {
    m_verbose = b;
  }

  protected boolean _mvCheck = true;
  public boolean getCheckMissingValues() {
    return _mvCheck;
  }

  public void setCheckMissingValues(boolean b) {
    _mvCheck = b;
  }

  //================
  // Constructor(s)
  //================
  public ClusterAssignmentOPT() {
  }

  //================
  // Public Methods
  //================

  //========================
  // D2K Abstract Overrides

  /**
   * Return array of property descriptors for this module.
   * @return array
   */
  public PropertyDescription[] getPropertiesDescriptions() {
    PropertyDescription[] descriptions = new PropertyDescription[2];

    descriptions[0] = new PropertyDescription("checkMissingValues",
                                              CHECK_MV,
        "If this property is true, the module will perform a check for missing values in the input table. ");

    descriptions[1] = new PropertyDescription("verbose",
                        		      VERBOSE,
        "If this property is true, the module will write verbose status information to the console.");

    return descriptions;
  }

  /**
     Return the name of this module.
     @return The name of this module.
   */
  public String getModuleName() {
    return "Cluster Assignment";
  }

  /**
     Return a description of a specific input.
     @param i The index of the input
     @return The description of the input
   */
  public String getInputInfo(int parm1) {
    if (parm1 == 0) {
      return "Control parameters for the module.";
    } else if (parm1 == 1) {
      return "Cluster Model or Table containing the initial centroids.";
    } else if (parm1 == 2) {
      return "Table containing all the examples to cluster.";
    } else {
      return "No such input.";
    }
  }

  /**
     Return the name of a specific input.
     @param i The index of the input.
     @return The name of the input
   */
  public String getInputName(int parm1) {
    if (parm1 == 0) {
      return "Parameter Point";
    } else if (parm1 == 1) {
      return "Cluster Model or Table";
    } else if (parm1 == 2) {
      return "Table";
    } else {
      return "No such input";
    }
  }

  /**
     Return a String array containing the datatypes the inputs to this
     module.
     @return The datatypes of the inputs.
   */
  public String[] getInputTypes() {
    String[] in = {
        "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint",
        "ncsa.d2k.modules.core.datatype.table.Table",
        "ncsa.d2k.modules.core.datatype.table.Table"};
    return in;
  }

  /**
    Return information about the module.
    @return A detailed description of the module.
   */
  public String getModuleInfo() {

    String s = "<p>Overview: ";
    s += "This module finds a locally optimal clustering by iteratively assigning examples to ";
    s += "selected points in vector space. ";
    s += "</p>";

    s += "<p>Detailed Description: ";
    s += "There are two versions of this module. ";
    s += "The <i>OPT</i>, optimizable, version uses control ";
    s += "parameters encapsulated in a <i>Parameter Point</i> to direct the cluster assignment and refinement behavior. ";
    s += "The control parameters specify a <i>";
    s += CLUSTER_METHOD ;
    s += "</i>, a <i>";
    s += DISTANCE_METRIC;
    s += "</i>, and a <i>";
    s += MAX_ITERATIONS;
    s += "</i>.  These parameters are set as properties in the non-OPT version of the module. ";
    s += "</p>";

    s += "<p>";
    s += "This module takes a set of cluster centroids and a set of examples to be clustered ";
    s += "and repeatedly assigns ";
    s += "the examples to the cluster whose centroid is closest in vector space, where distance is ";
    s += "computed using the specified <i>";
    s += DISTANCE_METRIC;
    s += "</i>.  When one assignment is completed, new centroids are calculated from the clusters just formed and ";
    s += "the process is repeated.  The algorithm will iterate at most <i>";
    s += MAX_ITERATIONS;
    s += "</i> times, halting sooner if the current iteration produces results not significantly ";
    s += "different from the previous iteration. ";

    s += "The initial centroids are input via the <i>Cluster Model or Table</i> port, and the ";
    s += "number of initial centroids determines the <i>Number of Clusters</i> formed. ";
    s += "The set of examples to be clustered are input via the <i>Table</i> port, and ";
    s += "the number of rows in that table determines the <i>Number of Examples</i> in the final clusters. ";
    s += "</p>";

    s += "<p>The Hierarchical Agglomerative Clustering (HAC) algorithm, a bottom-up strategy, ";
    s += "is run on the final set of clusters to build the cluster tree from the cut to the root. ";
    s += "The method used to determine intercluster similarity is controlled by the <i>";
    s += CLUSTER_METHOD;
    s += "</i> parameter. ";
    s += "The cluster tree is stored in a newly formed model, <i>Cluster Model</i>, along with the initial table ";
    s += "of examples and the set of clusters formed.";
    s += "</p>";

    s += "<p>Data Type Restrictions: ";
    s += "The <i>Cluster Model or Table</i> and <i>Table</i> inputs must have the same underlying ";
    s += "table structure. ";
    s += "That is, attribute types, order, and input features (if specified), must be identical. ";
    s += "The clustering does not work if the input data contains missing values. ";
    s += "The algorithm operates on numeric and boolean data types.  If the data to be clustered ";
    s += "contains nominal data types, it should be converted prior to performing the clustering. ";
    s += "The <i>Scalarize Nominals</i> module can be used to convert nominal types into boolean values. ";
    s += "</p>";

    s += "<p>Data Handling: ";
    s += "The second input table, <i>Table</i>, is included in the <i>Cluster Model</i>.  Neither input table ";
    s += "is modified.";
    s += "</p>";

    s += "<p>Scalability: ";
    s += "The time complexity is linear in the <i>Number of Examples</i> times the <i>Number of Iterations</i>. ";
    s += "The algorithm repeatedly builds two times <i>Number of Clusters</i> table clusters from ";
    s += "<i>Number of Examples</i> table clusters, and requires heap resources to that extent.  A single ";
    s += "table cluster's memory size will vary as the size of the individual examples being clustered.";
    s += "</p>";
    return s;
  }

  /**
     Return the description of a specific output.
     @param i The index of the output.
     @return The description of the output.
   */
  public String getOutputInfo(int parm1) {
    if (parm1 == 0) {
      return "Newly created Cluster Model.";
    } else {
      return "No such output.";
    }
  }

  /**
     Return the name of a specific output.
     @param i The index of the output.
     @return The name of the output
   */
  public String getOutputName(int parm1) {
    if (parm1 == 0) {
      return "Cluster Model";
    } else {
      return "No such output";
    }
  }

  /**
     Return a String array containing the datatypes of the outputs of this
     module.
     @return The datatypes of the outputs.
   */
  public String[] getOutputTypes() {
    String[] out = {
        "ncsa.d2k.modules.core.discovery.cluster.ClusterModel"};
    return out;
  }

  /**
   Perform the work of the module.
   @throws Exception
   */
  protected void doit() throws java.lang.Exception {
    ParameterPoint pp = (ParameterPoint) pullInput(0);
    _clusterMethod = (int) pp.getValue(0);
    _distanceMetric = (int) pp.getValue(1);
    m_numAssignments = (int) pp.getValue(2);

    Table initcenters = (Table)this.pullInput(1);
    Table initEntities = (Table)this.pullInput(2);

    ClusterRefinement refiner = new ClusterRefinement(_clusterMethod,
        _distanceMetric, m_numAssignments, this.getVerbose(),
        this.getCheckMissingValues(), getAlias() );
    this.pushOutput(refiner.assign(initcenters, initEntities), 0);
  }
}
// Start QA Comments
// 4/8/03 - Ruth started QA;  Updates to Module Info;  Added getAlias() arg to
//          ClusterRefinement constructor.
//        - Waiting to hear back from Duane on why Table must be mutable if not changed.
// 4/9/03 - Don't need mutable - slipped thru cracks from earlier version.
//        - Ready for basic.
// 4/10/03 - Ruth updated module info and input port name as not just Sample Tables
//           determine the initial cluster centroids.
// End QA Comments
