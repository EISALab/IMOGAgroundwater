package ncsa.d2k.modules.core.discovery.cluster.sample;

/**
 * <p>Title: FractionationParams</p>
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

public class FractionationParams extends FractionationParamsOPT {

  //==============
  // Data Members
  //==============

  //================
  // Constructor(s)
  //================

  public FractionationParams() {
  }

  //============
  // Properties
  //============

  /** the number of rows to sample */
  public void setNumClusters(int i) {N = i;}
  public int getNumClusters() {return N;}

  public int getClusterMethod () {return  _clusterMethod;}
  public void setClusterMethod (int noc) {_clusterMethod = noc;}

  public int getDistanceMetric() {return  _distanceMetric;}
  public void setDistanceMetric(int dm) {_distanceMetric = dm;}

  public int getHacDistanceThreshold() {return  _hacDistanceThreshold;}
  public void setHacDistanceThreshold (int noc) {_hacDistanceThreshold = noc;}

  public int getPartitionSize() {return  _fracPart;}
  public void setPartitionSize (int noc) {_fracPart = noc;}

  public int getNthSortTerm() {return _nthSortTerm;}
  public void setNthSortTerm(int mt) {_nthSortTerm = mt;}

  public int getRefinementMaxIterations() {return  _maxIterations;}
  public void setRefinementMaxIterations(int dm) {_maxIterations = dm;}

  /**
   * Return a list of the property descriptions.
   * @return a list of the property descriptions.
   */
  public PropertyDescription[] getPropertiesDescriptions() {
    PropertyDescription[] pds = new PropertyDescription[7];
    pds[0] = new PropertyDescription("numClusters",
                                     "Number of Clusters",
                                     "This is the number of clusters you want to form (>= 2).");
    pds[1] = new PropertyDescription("clusterMethod",
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
    pds[2] = new PropertyDescription("distanceMetric",
                                     "Distance Metric",
        "This property determine the type of distance function used to calculate " +
        "distance between two examples." +
        "<p>EUCLIDEAN: \"Straight\" line distance between points.</p>" +
        "<p>MANHATTAN: Distance between two points measured along axes at right angles.</p>" +
        "<p>COSINE: 1 minus the cosine of the angle between the norms of the vectors denoted by two points.</p>"
        );
    pds[3] = new PropertyDescription("hacDistanceThreshold",
                                     "HAC Distance Threshold",
                                     "This property specifies the percent of the max distance to use " +
                                     "as a cutoff value to halt clustering ([1...100].  The max distance between examples " +
                                     "is approximated by taking the min and max of each attribute and forming a " +
                                     "min example and a max example -- then finding the distance between the two.");
    pds[4] = new PropertyDescription("partitionSize",
                                              "Max Partition Size",
        "The size of partitions to use in the sampling process."
        );
    pds[5] = new PropertyDescription("NthSortTerm",
                                              "Sort Attribute",
                                              "The index of for the column denoting the attribute to be used to sort on prior to partitioning.");
    pds[6] = new PropertyDescription("refinementMaxIterations",
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
  public CustomModuleEditor getPropertyEditor(){
    return new FractionationParams_Props(this);
  }

/**
   Return a description of a specific input.
   @param i The index of the input
   @return The description of the input
 */
  public String getInputInfo (int parm1) {
    if (parm1 == 0) {
      return  "Table of examples to cluster";
    } else {
      return  "";
    }
  }

/**
   Return the name of a specific input.
   @param i The index of the input.
   @return The name of the input
 */
  public String getInputName (int parm1) {
    if (parm1 == 0) {
      return  "Table";
    } else {
      return  "";
    }
  }

/**
   Return a String array containing the datatypes the inputs to this
   module.
   @return The datatypes of the inputs.
 */
  public String[] getInputTypes () {
    String[] in =  {"ncsa.d2k.modules.core.datatype.table.Table"};
    return  in;
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
