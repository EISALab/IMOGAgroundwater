package ncsa.d2k.modules.core.discovery.cluster.sample;

/**
 * <p>Title: FractionationParamsOPT</p>
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
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.impl.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.discovery.cluster.hac.*;

public class FractionationParamsOPT
    extends DataPrepModule implements ClusterParameterDefns {

  //==============
  // Data Members
  //==============

  //================
  // Constructor(s)
  //================

  public FractionationParamsOPT() {
  }

  //============
  // Properties
  //============

  /** the number of rows to sample */
  protected int N = 3;

  protected int _clusterMethod = HAC.s_WardsMethod;

  protected int _distanceMetric = HAC.s_Euclidean;

  protected int _hacDistanceThreshold = 0;

  protected int _fracPart = 200;

  protected int _nthSortTerm = 0;

  protected int _maxIterations = 5;

  //========================
  // D2K Abstract Overrides

  /**
     Return the name of this module.
     @return The name of this module.
   */
  public String getModuleName() {
    return "Fractionation Parameters";
  }

  /**
     Return a description of a specific input.
     @param i The index of the input
     @return The description of the input
   */
  public String getInputInfo(int parm1) {
    if (parm1 == 0) {
      return "Control Parameters";
    } else if (parm1 == 1) {
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
      return "ParameterPoint";
    } else if (parm1 == 1) {
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
        "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint",
        "ncsa.d2k.modules.core.datatype.table.Table"};
    return in;
  }

  /**
    Return information about the module.
    @return A detailed description of the module.
   */
  public String getModuleInfo() {
    String s = "<p>Overview: ";
    s += "The Coverage clustering algorithm is a type of kmeans approach where a sample set ";
    s += "is formed as follows: <br>";
    s += "In the FractionationSampler, the initial examples (converted to clusters) by a key attribute denoted by <i>Sort Attribute</i>. ";
    s += "The set of sorted clusters is then segmented into equal partitions of size <i>maxPartitionsize</i>. ";
    s += "Each of these partitions is then passed through the agglomerative clusterer to produce ";
    s += "<i>numberOfClusters</i> clusters.  All the clusters are gathered together for all partitions and the ";
    s += "entire process is repeated until only <i>" + NUM_CLUSTERS + "</i> clusters remain. ";
    s += "The sorting step is to encourage like clusters into same partitions. ";
    s += "The final cluster's centroids are used as the initial \"means\" for the cluster assignment module.";
    s += "The assignment module, once it has made refinements, outputs the final <i>Cluster Model</i>. ";
    s += "</p>";

    s += "<p>Detailed Description: ";
    s += "This algorithm is comprised of four modules: this module (FractionationParams), the sampler ";
    s += "(FractionationSamplerOPT), the clusterer (HierAgglomClustererOPT) and the cluster refiner ";
    s += "(ClusterAssignment).";
    s += "</p>";

    s += "<p>Data Handling: ";
    s += "The input table is not modified by this algorithm, however it is include as part of ";
    s += "the <i>Cluster Model</i> that is created.";
    s += "</p>";

    s += "<p>Scalability: ";
    s += "This module time complexity of O(<i>Number of Examples</i> * <i>Partition Size</i>). ";
    s += "Each iteration creates <i>Number of Examples</i> <i>Table Cluster</i> objects.";
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
      return "Parameters for Cluster Assignment";
    } else if (parm1 == 1) {
      return "Parameters for Hier. Agglom. Clusterer";
    } else if (parm1 == 2) {
      return "Parameters for Fractionation Sampler";
    } else if (parm1 == 3) {
      return "Table of examples to cluster";
    } else {
      return "";
    }
  }

  /**
     Return the name of a specific input.
     @param i The index of the input.
     @return The name of the input
   */
  public String getOutputName(int parm1) {
    if (parm1 == 0) {
      return "Parameters for Cluster Assignment";
    } else if (parm1 == 1) {
      return "Parameters for Hier. Agglom. Clusterer";
    } else if (parm1 == 2) {
      return "Parameters for Fractionation Sampler";
    } else if (parm1 == 3) {
      return "Table";
    } else {
      return "";
    }
  }

  /**
     Return a String array containing the datatypes of the outputs of this
     module.
     @return The datatypes of the outputs.
   */
  public String[] getOutputTypes() {
    String[] out = {
        "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint",
        "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint",
        "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint",
        "ncsa.d2k.modules.core.datatype.table.Table"};
    return out;
  }

  /**
   Perform the work of the module.
   @throws Exception
   */
  protected void doit() throws Exception {

    ParameterPoint pp = (ParameterPoint)this.pullInput(0);
    this.N = (int) pp.getValue(0);
    this._clusterMethod = (int) pp.getValue(1);
    this._distanceMetric = (int) pp.getValue(2);
    this._hacDistanceThreshold = (int) pp.getValue(3);
    this._fracPart = (int) pp.getValue(4);
    this._nthSortTerm = (int) pp.getValue(5);
    this._maxIterations = (int) pp.getValue(6);

    Table tab = (Table)this.pullInput(1);
    doingit(tab);

  }

  protected void doingit(Table tab) {

    String[] names1 = {
        "clusterMethod", "distanceMetric", "maxIterations"};
    double[] values1 = {
         (double)this._clusterMethod,
        (double)this._distanceMetric,
        (double)this._maxIterations};
    ParameterPoint pp = ParameterPointImpl.getParameterPoint(names1, values1);

    this.pushOutput(pp, 0);

    String[] names2 = {
        "clusterMethod", "distanceMetric", "numClusters", "distanceThreshold"};
    double[] values2 = {
         (double)this._clusterMethod,
        (double)this._distanceMetric,
        (double)this.N,
        (double)this._hacDistanceThreshold};
    pp = ParameterPointImpl.getParameterPoint(names2, values2);

    this.pushOutput(pp, 1);

    String[] names3 = {
        "numClusters", "nthSortTerm", "partitionSize"};
    double[] values3 = {
         (double)this.N,
        (double)this._nthSortTerm,
        (double)this._fracPart};
    pp = ParameterPointImpl.getParameterPoint(names3, values3);

    this.pushOutput(pp, 2);

    this.pushOutput(tab, 3);

  }
}
