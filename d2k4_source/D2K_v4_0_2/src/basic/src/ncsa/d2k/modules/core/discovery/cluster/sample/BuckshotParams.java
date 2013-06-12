package ncsa.d2k.modules.core.discovery.cluster.sample;

/**
 * <p>Title: BuckshotParams</p>
 * <p>Description: MOdule to input parameters for KMeans</p>
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
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.discovery.cluster.gui.properties.*;

public class BuckshotParams
    extends BuckshotParamsOPT {

  //==============
  // Data Members
  //==============

  //================
  // Constructor(s)
  //================

  public BuckshotParams() {
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

  public int getDistanceThreshold() {
    return _thresh;
  }

  public void setDistanceThreshold(int noc) {
    _thresh = noc;
  }

  /**
   * Return a list of the property descriptions.
   * @return a list of the property descriptions.
   */
  public PropertyDescription[] getPropertiesDescriptions() {
    PropertyDescription[] pds = new PropertyDescription[7];
    pds[0] = new PropertyDescription("numberOfClusters",
                                    "Number of Clusters",
                                    "This property specifies the number of clusters to form (>= 2).");
    pds[1] = new PropertyDescription("seed",
                                     "Seed",
                                     "The seed for the random number generator used to select the random sampling of table rows. If this value is set to the same value for different runs, the results be the exact same.");
    pds[2] = new PropertyDescription("useFirst",
                                     "Use First",
                                     "If this option is selected, the first entries in the original table will be used as the sample.");
    pds[3] = new PropertyDescription("clusterMethod",
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
    pds[4] = new PropertyDescription("distanceMetric",
                                              "Distance Metric",
        "This property determines the type of distance function used to calculate " +
        "distance between two examples." +
        "<p>EUCLIDEAN: \"Straight\" line distance between points.</p>" +
        "<p>MANHATTAN: Distance between two points measured along axes at right angles.</p>" +
        "<p>COSINE: 1 minus the cosine of the angle between the norms of the vectors denoted by two points.</p>"
        );
    pds[5] = new PropertyDescription("distanceThreshold",
                                              "Distance Threshold",
                                     "This property specifies the percent of the max distance to use " +
                                     "as a cutoff value to halt clustering ([1...100].  The max distance between examples " +
                                     "is approximated by taking the min and max of each attribute and forming a " +
                                     "min example and a max example -- then finding the distance between the two. " +
                                     "For this algorithm, the number of clusters value is still used to generate the " +
                                     "initial sample size, even if auto is selected.");
    pds[6] = new PropertyDescription("maxIterations",
                                              "Number of Assignment Passes",
        "This property specifies the number of iterations of cluster refinement to perform (> 0).");
    return pds;
  }

  //========================
  // D2K Abstract Overrides

  /**
   Return a custom gui for setting properties.
   @return CustomModuleEditor
   */
  public CustomModuleEditor getPropertyEditor() {
    return new BuckshotParams_Props(this);
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
      return "";
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
      return "";
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

    Table tab = (Table)this.pullInput(0);
    doingit(tab);

  }

}
