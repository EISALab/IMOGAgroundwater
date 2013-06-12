package ncsa.d2k.modules.core.discovery.cluster.sample;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.impl.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.prediction.*;

/**
 * <p>Title: FractionationParamSpaceGenerator</p>
 * <p>Description: Generates the optimization space and initial
 * values for the Fractionation Clustering Algorithm.
 * </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: NCSA Automated Learning Group</p>
 * @author D. Searsmith
 * @version 1.0
 */

public class FractionationParamSpaceGenerator extends AbstractParamSpaceGenerator {

  //==============
  // Data Members
  //==============

  public static final String NUM_CLUSTERS = "Number of Clusters";
  public static final String CLUSTER_METHOD = "Cluster Method";
  public static final String DISTANCE_METRIC = "Distance Metric";
  public static final String HAC_DISTANCE_THRESHOLD = "HAC Distance Threshold";
  public static final String FRACT_PART_SZ = "Fractionation Partition Size";
  public static final String FRACT_NTH_SORT_TERM = "Fractionation Nth Term to Sort On";
  public static final String MAX_ITERATIONS = "Max Refinement Iterations";

  /**
   * Returns a reference to the developer supplied defaults. These are
   * like factory settings, absolute ranges and definitions that are not
   * mutable.
   * @return the factory settings space.
   */
  protected ParameterSpace getDefaultSpace() {
        ParameterSpace psi = new ParameterSpaceImpl();
        String[] names = { NUM_CLUSTERS,
            CLUSTER_METHOD,
            DISTANCE_METRIC,
            HAC_DISTANCE_THRESHOLD,
            FRACT_PART_SZ,
            FRACT_NTH_SORT_TERM,
            MAX_ITERATIONS};
        double[] min = { 0, 0, 0, 0, 2, 0, 1};
        double[] max = { Integer.MAX_VALUE, 6, 3, 100, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE};
        double[] def = { 5, 0, 0, 0, 250, 0, 5};
        int[] res = { 1, 1, 1, 1, 1, 1, 1};
        int[] types = {ColumnTypes.INTEGER, ColumnTypes.INTEGER, ColumnTypes.BOOLEAN, ColumnTypes.INTEGER, ColumnTypes.INTEGER, ColumnTypes.INTEGER, ColumnTypes.INTEGER};
        psi.createFromData(names, min, max, def, res, types);
        return psi;
  }

  /**
   * Return a name more appriate to the module.
   * @return a name
   */
  public String getModuleName() {
    return "Fractionation Clusterer Space Generator";
  }


  /**
   * Return a list of the property descriptions.
   * @return a list of the property descriptions.
   */
  public PropertyDescription[] getPropertiesDescriptions() {
    PropertyDescription[] pds = new PropertyDescription[7];
    pds[0] = new PropertyDescription(NUM_CLUSTERS,
                                     "Number of Clusters",
                                     "This is the number of clusters you want to form (>= 2).");
    pds[1] = new PropertyDescription(CLUSTER_METHOD,
                                              "Clustering Method",
        "The method to use for determining the distance between two clusters. " +
        "<p>WARDS METHOD: Use a minimum variance approach that sums the squared error " +
        "(distance) for every point in the cluster to the cluster centroid.</p>" +
        "<p>SINGLE LINK: Distance of closest pair (one from each cluster).</p>" +
        "<p>COMPLETE LINK: Distance of furthest pair (one from each cluster).</p>" +
        "<p>UPGMA: Unweighted pair group method using arithmetic averages.</p>" +
        "<p>WPGMA: Weighted pair group method using arithmetic averages.</p>" +
        "<p>UPGMC: Unweighted pair group method using centroids.</p>" +
        "<p>WPGMC: Weighted pair group method using centroids.</p>");
    pds[2] = new PropertyDescription(DISTANCE_METRIC,
                                     "Distance Metric",
        "This property determine the type of distance function used to calculate " +
        "distance between two examples." +
        "<p>EUCLIDEAN: \"Straight\" line distance between points.</p>" +
        "<p>MANHATTAN: Distance between two points measured along axes at right angles.</p>" +
        "<p>COSINE: 1 minus the cosine of the angle between the norms of the vectors denoted by two points.</p>"
        );
    pds[3] = new PropertyDescription(HAC_DISTANCE_THRESHOLD,
                                     "HAC Distance Threshold",
                                     "This property specifies the percent of the max distance to use " +
                                     "as a cutoff value to halt clustering ([0...100].  The max distance between examples " +
                                     "is approximated by taking the min and max of each attribute and forming a " +
                                     "min example and a max example -- then finding the distance between the two. " +
                                     "This property when set with a value > 0 becomes the dominant halting criteria for " +
                                     "clustering (overriding the <i>Number of Clusters</i> property.");
    pds[4] = new PropertyDescription(FRACT_PART_SZ,
                                              "Max Partition Size",
        "The size of partitions to use in the sampling process."
        );
    pds[5] = new PropertyDescription(FRACT_NTH_SORT_TERM,
                                              "Sort Attribute",
                                              "The index of for the column denoting the attribute to be used to sort on prior to partitioning.");
    pds[6] = new PropertyDescription(MAX_ITERATIONS,
                                              "Number of Assignment Passes",
        "This property specifies the number of iterations of cluster refinement to perform (> 0).");
    return pds;
  }

}
