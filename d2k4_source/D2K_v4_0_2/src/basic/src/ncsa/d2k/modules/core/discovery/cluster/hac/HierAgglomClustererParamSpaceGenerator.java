package ncsa.d2k.modules.core.discovery.cluster.hac;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.impl.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.prediction.*;
import ncsa.d2k.modules.core.discovery.cluster.sample.ClusterParameterDefns;

/**
 * <p>Title: HierAgglomClustererParamSpaceGenerator</p>
 * <p>Description: Generates the optimization space and initial
 * values for the HAC learning module.
 * </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: NCSA Automated Learning Group</p>
 * @author D. Searsmith
 * @version 1.0
 */

public class HierAgglomClustererParamSpaceGenerator
	extends AbstractParamSpaceGenerator
	implements ClusterParameterDefns{

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
        String[] names = { CLUSTER_METHOD,
            		   DISTANCE_METRIC,
            		   NUM_CLUSTERS,
            		   DISTANCE_THRESHOLD};
        double[] min = { 0, 0, 1, 0};
        double[] max = { 6, 2, Integer.MAX_VALUE, 100};
        double[] def = { 0, 0, 5, 0};
        int[] res = { 1, 1, 1, 1};
        int[] types = {ColumnTypes.INTEGER, ColumnTypes.INTEGER, ColumnTypes.INTEGER, ColumnTypes.INTEGER};
        psi.createFromData(names, min, max, def, res, types);
        return psi;
  }

  /**
   * Return a name more appriate to the module.
   * @return a name
   */
  public String getModuleName() {
    return "HAC Param Space Generator";
  }

  /**
   * Return a list of the property descriptions.  The order of descriptions
   * matches the order of presentation of the properties to the user.
   * @return a list of the property descriptions.
   */
  public PropertyDescription[] getPropertiesDescriptions() {

    // presentation order (dictated by dialog layout):
    // 0-CLUSTER_METHOD, 1-DISTANCE_METRIC, 2-NUM_CLUSTERS, 3-DISTANCE_THRESHOLD
    // Following code is (mostly) cut/paste across modules and indices of pds are adjusted
    // for the appropriate order for the dialog that is used in this module.
    // The first variable in each Property Description isn't used in this case;
    // just the 2nd and 3rd for dialog label and help.

    PropertyDescription[] pds = new PropertyDescription[4];
    pds[0] = new PropertyDescription( CLUSTER_METHOD,
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

    pds[1] = new PropertyDescription( DISTANCE_METRIC,
                                      DISTANCE_METRIC,
                                      "This property determines the type of distance function to use in calculating the " +
                                      "distance between two points in the space of example values. " +
        "<p>EUCLIDEAN: \"Straight\" line distance between points.</p>" +
        "<p>MANHATTAN: Distance between two points measured along axes at right angles.</p>" +
        "<p>COSINE: 1 minus the cosine of the angle between the norms of the vectors denoted by two points.</p>");

    pds[2] = new PropertyDescription( NUM_CLUSTERS,
                                      NUM_CLUSTERS,
                                      "This property specifies the number of clusters to form. It must be greater than 1. "+
                                      "If <i>"+
                                      DISTANCE_THRESHOLD +
                                      "</i> is greater than 0, that will be used to halt cluster agglomeration and "+
				      "determine the number of clusters formed, "+
                                      "independent of this property's setting.  ");
    pds[3] = new PropertyDescription( DISTANCE_THRESHOLD,
				      DISTANCE_THRESHOLD,
                                      "This property specifies the percentage of the <i>maximum distance</i> to use " +
                                       "as a cutoff value to halt cluster agglomeration.  " +
                                       "When the distance between the two clusters that are closest exceeds the cutoff value, "+
				       "cluster agglomeration stops, " +
                                       "independent of the value of the <i>" +
                                       NUM_CLUSTERS +
                                       "</i> property.  Lower values for the <i>" +
                                       DISTANCE_THRESHOLD +
                                       "</i> result in more clusters.   "+
                                       "If the value is 0 (the default), then no cutoff occurs and cluster " +
                                       "agglomeration continues until <i>" +
                                       NUM_CLUSTERS +
                                       "</i> remain. " +
                                       "See the <i>Hier. Agglom. Clusterer</i> module information for further details on " +
                                       "this is property. " );

     return pds;
  }

}

// Start QA Comments
// 4/12/03 - Ruth starts QA;  Updated properties to be more consistent w/ other modules and
//           to use ClusterParameterDefns.
//         * Unsure how DISTANCE_THRESHOLD really works...  if < threshold movement does it stop or what?
// 4/13/03 - Another stab at the DISTANCE_THRESHOLD words.   Asked Duane for confirmation.
//         - Looked at code - think descr is okay now.
//         - Ready for Basic.
// 4/14/03 - made some more updates to properties docs after feedback from Duane
// End QA Comments
