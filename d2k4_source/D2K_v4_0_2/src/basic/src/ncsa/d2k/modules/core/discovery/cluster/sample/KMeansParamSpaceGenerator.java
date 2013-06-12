package ncsa.d2k.modules.core.discovery.cluster.sample;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.impl.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.prediction.*;

/**
 * <p>Title: KMeansParamSpaceGenerator</p>
 * <p>Description: Generates the optimization space and initial
 * values for the KMeans Clustering Algorithm.
 * </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: NCSA Automated Learning Group</p>
 * @author D. Searsmith
 * @version 1.0
 */

public class KMeansParamSpaceGenerator
    extends AbstractParamSpaceGenerator
    implements ClusterParameterDefns {

  //==============
  // Data Members
  //==============

  /**
   * Returns a reference to the developer supplied defaults. These are
   * like factory settings, absolute ranges and definitions that are not
   * mutable.
   * @return the factory settings space.
   */
  protected ParameterSpace getDefaultSpace() {
        ParameterSpace psi = new ParameterSpaceImpl();
        String[] names = { NUM_CLUSTERS, SEED, USE_FIRST, CLUSTER_METHOD, DISTANCE_METRIC, MAX_ITERATIONS};
        double[] min = { 0, 0, 0, 0, 0, 1};
        double[] max = { Integer.MAX_VALUE, Integer.MAX_VALUE, 1, 6, 2, Integer.MAX_VALUE};
        double[] def = { 5, 0, 0, 0, 0, 5};
        int[] res = { 1, 1, 1, 1, 1, 1};
        int[] types = {ColumnTypes.INTEGER, ColumnTypes.INTEGER, ColumnTypes.BOOLEAN, ColumnTypes.INTEGER, ColumnTypes.INTEGER, ColumnTypes.INTEGER};
        psi.createFromData(names, min, max, def, res, types);
        return psi;
  }

  /**
   * Return a name more appropriate to the module.
   * @return a name
   */
  public String getModuleName() {
    return "KMeans Param Space Generator";
  }

  /**
   * Return a list of the property descriptions.  The order of descriptions
   * matches the order of presentation of the properties to the user.
   * @return a list of the property descriptions.
   */
   public PropertyDescription[] getPropertiesDescriptions() {
     // presentation order (dictated by dialog layout): 0-NUM_CLUSTERS, 1-SEED,
     // 2-USE_FIRST, 3-CLUSTER_METHOD, 4-DISTANCE_METRIC, 5-MAX_ITERATIONS
     // Following code is cut/paste from KMeansParamSpaceGenerator and indices of
     // pds are adjusted for the appropriate order.

     PropertyDescription[] pds = new PropertyDescription[6];
     pds[0] = new PropertyDescription("numClusters",
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
        "If this option is selected, the first <i>"+
        NUM_CLUSTERS +
        "</i> entries in the input table " +
        "will be used as the initial cluster centers, " +
        "rather than selecting a random sample set of table rows. ");

     pds[3] = new PropertyDescription("clusterMethod",
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
}

// Start QA Comments
// 4/6/03 - Ruth started QA
//        - Updated Properties & Module Name
// 4/7/03 - Added KMeansParameterDefns to help keep things consistent across
//          Kmeans* modules.
//        - Ready for Basic
// End QA Comments
