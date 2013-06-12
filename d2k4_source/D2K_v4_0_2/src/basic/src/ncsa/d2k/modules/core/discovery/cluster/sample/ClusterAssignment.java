package ncsa.d2k.modules.core.discovery.cluster.sample;

/**
 * <p>
 * Title: ClusterAssignment
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
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.discovery.cluster.gui.properties.*;

public class ClusterAssignment
    extends ClusterAssignmentOPT  {

  //==============
  // Data Members
  //==============

  //============
  // Properties
  //============
  public int getDistanceMetric() {
    return _distanceMetric;
  }

  public void setDistanceMetric(int dm) {
    _distanceMetric = dm;
  }

  public int getNumAssignments() {
    return m_numAssignments;
  }

  public void setNumAssignments(int noc) {
    m_numAssignments = noc;
  }

  //just need these for the clustering at the end
  public int getClusterMethod() {
    return _clusterMethod;
  }

  public void setClusterMethod(int noc) {
    _clusterMethod = noc;
  }

  //================
  // Constructor(s)
  //================
  public ClusterAssignment() {
  }

  //================
  // Public Methods
  //================

  //========================
  // D2K Abstract Overrides

  /**
   * Return a list of the property descriptions.
   * @return a list of the property descriptions.
   */
  public PropertyDescription[] getPropertiesDescriptions() {
    PropertyDescription[] descriptions = new PropertyDescription[5];

      descriptions[0] = new PropertyDescription("clusterMethod",
                                                CLUSTER_METHOD,
        "The method to use for determining the similarity between two clusters. " +
        "This similarity measure is used in formulating the tree that is part of the final cluster model. " +
        "<p>WARDS METHOD: Use a minimum variance approach that sums the squared error " +
        "(distance) for every point in the cluster to the cluster centroid.</p>" +
        "<p>SINGLE LINK: Distance of closest pair (one from each cluster).</p>" +
        "<p>COMPLETE LINK: Distance of furthest pair (one from each cluster).</p>" +
        "<p>UPGMA: Unweighted pair group method using arithmetic averages.</p>" +
        "<p>WPGMA: Weighted pair group method using arithmetic averages.</p>" +
        "<p>UPGMC: Unweighted pair group method using centroids.</p>" +
        "<p>WPGMC: Weighted pair group method using centroids.</p>");

     descriptions[1] = new PropertyDescription("numAssignments",
                                               MAX_ITERATIONS,
        "This property specifies the maximum number of iterations of cluster " +
        "assignment/refinement to perform. " +
        "It must be greater than 0.  A check is performed after each iteration to determine if " +
        "the cluster centers have moved more than a small threshold amount.  If they have not, " +
        "the refinement process is stopped before the specified number of iterations. " );

     descriptions[2] = new PropertyDescription("distanceMetric",
                                               DISTANCE_METRIC,
        "This property determines the type of distance function to use in calculating the " +
        "distance between two examples.  This distance is used in assigning points to clusters, and " +
        "in determining if there was sufficient movement since the last assignment iteration "+
        "to continue the refinement process. " +
        "<p>EUCLIDEAN: \"Straight\" line distance between points.</p>" +
        "<p>MANHATTAN: Distance between two points measured along axes at right angles.</p>" +
        "<p>COSINE: 1 minus the cosine of the angle between the norms of the vectors denoted by two points.</p>");

     descriptions[3] = new PropertyDescription("checkMissingValues",
                                               CHECK_MV,
        "If this property is true, the module will perform a check for missing values in the input table. ");

     descriptions[4] = new PropertyDescription("verbose",
                                               VERBOSE,
        "If this property is true, the module will write verbose status information to the console.");

    return descriptions;
  }

  /**
   Return a custom gui for setting properties.
   @return CustomModuleEditor
   */
  public CustomModuleEditor getPropertyEditor() {
    return new ClusterAssignment_Props(this, true, true);
  }

  /**
     Return a description of a specific input.
     @param i The index of the input
     @return The description of the input
   */
  public String getInputInfo(int parm1) {
    if (parm1 == 0) {
      return "Cluster Model or Table containing the initial centroids.";
    } else if (parm1 == 1) {
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
      return "Cluster Model or Table";
    } else if (parm1 == 1) {
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
        "ncsa.d2k.modules.core.datatype.table.Table",
        "ncsa.d2k.modules.core.datatype.table.Table"};
    return in;
  }

  /**
   Perform the work of the module.
   @throws Exception
   */
  protected void doit() throws java.lang.Exception {
    Table initcenters = (Table)this.pullInput(0);
    Table initEntities = (Table)this.pullInput(1);
    ClusterRefinement refiner = new ClusterRefinement(this.getClusterMethod(),
        this.getDistanceMetric(), this.getNumAssignments(), this.getVerbose(),
        this.getCheckMissingValues(), getAlias() );
    this.pushOutput(refiner.assign(initcenters, initEntities), 0);
  }
}

// Start QA Comments
// 4/8/03 - Ruth starts QA;  Updated Input Info for consistence;  add getAlias()
//          to ClusterRefinement ctor;
// 	  - Ready for Basic.
// 4/13/03 - Edited input port 1 name & info to reflect that more types can be used.
//           (Not just SampleTable but other representations as well)
// End QA Comments
