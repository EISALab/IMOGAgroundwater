package ncsa.d2k.modules.core.discovery.cluster.hac;

//==============
// Java Imports
//==============
//===============
// Other Imports
//===============
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.discovery.cluster.gui.properties.*;

/**
 *
 * <p>Title: HierAgglomClusterer</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: NCSA Automated Learning Group</p>
 * @author D. Searsmith
 * @version 1.0
 *
 * TODO: change distance method to accommodate sparse matrices
 */

public class HierAgglomClusterer
    extends HierAgglomClustererOPT {

  //==============
  // Data Members
  //==============

  //============
  // Properties
  //============

  protected int _clusterMethod = HAC.s_WardsMethod;
  public int getClusterMethod() {
    return _clusterMethod;
  }

  public void setClusterMethod(int noc) {
    _clusterMethod = noc;
  }

  protected int _distanceMetric = HAC.s_Euclidean;
  public int getDistanceMetric() {
    return _distanceMetric;
  }

  public void setDistanceMetric(int dm) {
    _distanceMetric = dm;
  }

  protected int _numberOfClusters = 5;
  public int getNumberOfClusters() {
    return _numberOfClusters;
  }

  public void setNumberOfClusters(int noc) {
    _numberOfClusters = noc;
  }

  protected int _thresh = 0;
  public int getDistanceThreshold() {
    return _thresh;
  }

  public void setDistanceThreshold(int noc) {
    _thresh = noc;
  }

  //================
  // Constructor(s)
  //================
  public HierAgglomClusterer() {
  }

  //================
  // Public Methods
  //================

  //========================
  // D2K Abstract Overrides

  /**
   * Return a list of the property descriptions.  The order of descriptions
   * matches the order of presentation of the properties to the user.
   * @return a list of the property descriptions.
   */
  public PropertyDescription[] getPropertiesDescriptions() {

    // presentation order (dictated by dialog layout):
    // 0-CLUSTER_METHOD, 1-NUM_CLUSTERS, 2-AUTO_CLUSTER, 3-DISTANCE_THRESHOLD
    // 4-DISTANCE_METRIC, 5-CHECK_MV, 6-VERBOSE
    // 3-HAC_DISTANCE_THRESHOLD
    // Following code is (almost) cut/paste across modules and indices of pds are adjusted
    // for the appropriate order for the dialog that is used in this module.
    // The first variable in each Property Description isn't used in this case;
    // just the 2nd and 3rd for dialog label and help.

    PropertyDescription[] pds = new PropertyDescription[7];
    pds[0] = new PropertyDescription( "clusterMethod",
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

    pds[4] = new PropertyDescription( "distanceMetric",
                                      DISTANCE_METRIC,
        "This property determines the type of distance function to use in calculating the " +
        "distance between two points in the space of example values. " +
        "<p>EUCLIDEAN: \"Straight\" line distance between points.</p>" +
        "<p>MANHATTAN: Distance between two points measured along axes at right angles.</p>" +
        "<p>COSINE: 1 minus the cosine of the angle between the norms of the vectors denoted by two points.</p>");

    pds[1] = new PropertyDescription( "numClusters",
                                      NUM_CLUSTERS,
       "This property specifies the number of clusters to form. It must be greater than 1. "+
       "If <i>"+
       AUTO_CLUSTER +
       "</i> is enabled, the <i>" +
       DISTANCE_THRESHOLD +
       "</i> value will halt cluster agglomeration thus determining the number of clusters formed "+
       "independent of this property's setting.");

    pds[3] = new PropertyDescription( "distanceThreshold",
                                      DISTANCE_THRESHOLD,
      "This property specifies the percentage of the <i>maximum distance</i> to use " +
       "as a cutoff value to halt cluster agglomeration.  " +
       "When the distance between the two clusters that are closest exceeds the cutoff value, cluster agglomeration stops, "+
       "independent of the value of the <i>" +
       NUM_CLUSTERS +
       "</i> property.  Lower values for the <i>" +
       DISTANCE_THRESHOLD +
       "</i> result in more clusters.   "+
       "See the <i>Hier. Agglom. Clusterer</i> module information for further details on " +
       "this is property. " );

    pds[2] = new PropertyDescription( "autoCluster",
				      AUTO_CLUSTER,
        "If this property is true, the <i>" +
        DISTANCE_THRESHOLD +
        "</i> will be used to control when the cluster formation process halts independent of the value set " +
        "for <i>" +
        NUM_CLUSTERS +
        "</i>");

    pds[5] = new PropertyDescription( "checkMissingValues",
                                      CHECK_MV,
        "If this property is true, the module will perform a check for missing values in the input table. ");

    pds[6] = new PropertyDescription("verbose",
                                      VERBOSE,
        "If this property is true, the module will write verbose status information to the console.");

     return pds;
  }

  /**
   Return a custom gui for setting properties.
   @return CustomModuleEditor
   */
  public CustomModuleEditor getPropertyEditor() {
    return new HierAgglomClusterer_Props(this, true, true);
  }

  /**
     Return a description of a specific input.
     @param i The index of the input
     @return The description of the input
   */
  public String getInputInfo(int parm1) {
    if (parm1 == 0) {
      return "Table of entities to cluster";
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
   */
  protected void doit() throws Exception {
    HAC hac = new HAC(this.getClusterMethod(), this.getDistanceMetric(),
                      this.getNumberOfClusters(), this.getDistanceThreshold(),
                      getVerbose(), this.getCheckMissingValues(), getAlias() );
    this.pushOutput(hac.buildModel( (Table)this.pullInput(0)), 0);
  }

}

// Start QA Comments
// 4/12/03 - Ruth starts QA.  Updated properties to be consistent and adds Auto Clustering
//           property.
// 4/13/03 - Sent email to Duane to clarify use of THRESH and AUTO in comparison to
//           use in ParamSweep.  Think I have it right now.
//         - Ready for Basic.
// 4/14/03 - More updates to info... learning more!
// End QA Comments
