package ncsa.d2k.modules.core.discovery.cluster.sample;

/**
 * <p>Title: KMeansParams</p>
 * <p>Description: Module to input parameters for KMeans</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: NCSA Automated Learning Group</p>
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
import ncsa.d2k.modules.core.discovery.cluster.gui.properties.*;

public class KMeansParams
    extends KMeansParamsOPT
    implements ClusterParameterDefns {

  //==============
  // Data Members
  //==============

  //================
  // Constructor(s)
  //================

  public KMeansParams() {
  }

  //============
  // Properties
  //============

  /** the number of rows to sample */
  public void setNumClusters(int i) {
    N = i;
  }

  public int getNumClusters() {
    return N;
  }

  /** the seed for the random number generator */
  public void setSeed(int i) {
    seed = i;
  }

  public int getSeed() {
    return seed;
  }

  /** true if the first N rows should be the sample, false if the sample
          should be random rows from the table */
  public void setUseFirst(boolean b) {
    useFirst = b;
  }

  public boolean getUseFirst() {
    return useFirst;
  }

  public int getClusterMethod() {
    return _clusterMethod;
  }

  public void setClusterMethod(int noc) {
    _clusterMethod = noc;
  }

  public int getDistanceMetric() {
    return _distanceMetric;
  }

  public void setDistanceMetric(int dm) {
    _distanceMetric = dm;
  }

  public int getMaxIterations() {
    return _maxIterations;
  }

  public void setMaxIterations(int dm) {
    _maxIterations = dm;
  }

  /**
   * Return a list of the property descriptions.  The order of descriptions
   * matches the order of presentation of the properties to the user.
   * @return a list of the property descriptions.
   */
  public PropertyDescription[] getPropertiesDescriptions() {

    // presentation order (dictated by dialog layout): 0-CLUSTER_METHOD, 1-SEED,
    // 2-USE_FIRST, 3-NUM_CLUSTERS, 4-DISTANCE_METRIC, 5-MAX_ITERATIONS
    // Following code is cut/paste from KMeansParamSpaceGenerator and indices of
    // pds are adjusted for the appropriate order.

    PropertyDescription[] pds = new PropertyDescription[6];
    pds[3] = new PropertyDescription("numClusters",
                                      NUM_CLUSTERS,
       "This property specifies the number of clusters to form. It must be greater than 1.");

    pds[1] = new PropertyDescription("seed",
                                      SEED,
       "The seed for the random number generator used to select the sample set of <i>" +
       NUM_CLUSTERS +
       "</i> table rows that defines the initial cluster centers. " +
       "It must be greater than or equal to 0. " +
       "If the same seed is used across runs with the same input table, the sample sets " +
       "will be identical. If <i>" +
       USE_FIRST +
       "</i> is selected, this seed is not used. ");

     pds[2] = new PropertyDescription("useFirst",
                                       USE_FIRST,
        "If this option is selected, the first <i>" +
        NUM_CLUSTERS +
        "</i> entries in the input table " +
        "will be used as the initial cluster centers, " +
        "rather than selecting a random sample set of table rows. ");

     pds[0] = new PropertyDescription("clusterMethod",
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

     pds[4] = new PropertyDescription("distanceMetric",
                                       DISTANCE_METRIC,
        "This property determines the type of distance function to use in calculating the " +
        "distance between two examples.  This distance is used in assigning points to clusters, and " +
        "in determining if there was sufficient movement since the last assignment iteration "+
        "to continue the refinement process. " +
        "<p>EUCLIDEAN: \"Straight\" line distance between points.</p>" +
        "<p>MANHATTAN: Distance between two points measured along axes at right angles.</p>" +
        "<p>COSINE: 1 minus the cosine of the angle between the norms of the vectors denoted by two points.</p>"
        );

     pds[5] = new PropertyDescription("maxIterations",
                                       MAX_ITERATIONS,
        "This property specifies the maximum number of iterations of cluster " +
        "assignment/refinement to perform. " +
        "It must be greater than 0.  A check is performed after each iteration to determine if " +
        "the cluster centers have moved more than a small threshold amount.  If they have not, " +
        "the refinement process is stopped before the specified number of iterations. " );
     return pds;
  }

  //========================
  // D2K Abstract Overrides

  /**
   Return a custom gui for setting properties.
   @return CustomModuleEditor
   */
  public CustomModuleEditor getPropertyEditor() {
    return new KMeansParams_Props(this);
  }

  /**
     Return a description of a specific input.
     @param i The index of the input
     @return The description of the input
   */
  public String getInputInfo(int parm1) {
    if (parm1 == 0) {
      return "Table of examples to cluster.";
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
        "ncsa.d2k.modules.core.datatype.table.Table"};
    return in;
  }

  /**
   Perform the work of the module.
   @throws Exception
   */
  protected void doit() throws Exception {
    doingit();
    this.pushOutput(this.pullInput(0), 2);
  }

}

// Start QA Comments
// 4/6/03 - Ruth starts QA
//        - Reorder Property descriptions to match GUI order
// 4/7/03 - Added KMeansParameterDefns so easier to keep labels in sync.
//        - Ready for Basic
// End QA Comments
