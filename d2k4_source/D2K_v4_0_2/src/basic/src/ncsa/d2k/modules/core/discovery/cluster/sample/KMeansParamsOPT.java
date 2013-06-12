package ncsa.d2k.modules.core.discovery.cluster.sample;

/**
 * <p>Title: KMeansParamsOPT</p>
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
import ncsa.d2k.modules.core.discovery.cluster.hac.*;

public class KMeansParamsOPT
    extends DataPrepModule {

  //==============
  // Data Members
  //==============

  //================
  // Constructor(s)
  //================

  public KMeansParamsOPT() {
  }

  //============
  // Properties
  //============

  /** the number of rows to sample */
  protected int N = 5;

  /** the seed for the random number generator */
  protected int seed = 0;

  /** true if the first N rows should be the sample, false if the sample
          should be random rows from the table */
  protected boolean useFirst = false;

  protected int _clusterMethod = HAC.s_WardsMethod;

  protected int _distanceMetric = HAC.s_Euclidean;

  protected int _maxIterations = 5;

  //========================
  // D2K Abstract Overrides

  /**
     Return the name of this module.
     @return The name of this module.
   */
  public String getModuleName() {
    return "KMeans Parameters";
  }

  /**
     Return a description of a specific input.
     @param i The index of the input
     @return The description of the input
   */
  public String getInputInfo(int parm1) {
    if (parm1 == 0) {
      return "Control parameters, available as a Parameter Point.";
    } else if (parm1 == 1) {
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
      return "Parameter Point";
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
    s += "This module is used to set control parameters for the KMeans clustering algorithm. ";
    s += "The complete KMeans algorithm is implemented by this module and two others. ";
    s += "</p>";

    s += "<p>Detailed Description: ";
    s += "The KMeans clustering algorithm is an approach where a sample set containing ";
    s += "<i>Number of Clusters</i> rows is chosen from an input table of examples and ";
    s += "used as initial cluster centers. ";
    s += "These initial clusters undergo a series of assignment/refinement iterations, ";
    s += "resulting in a final cluster model. ";
    s += "</p>";

    s += "<p>";
    s += "If an <i>Example Table</i> is passed to this module, the algorithm will assign the examples (rows) ";
    s += "to clusters based on the values of the input attributes (columns) . If no input attributes have been ";
    s += "specified, the algorithm has no values to cluster on, and a single cluster will be formed. ";
    s += "The module <i>Choose Attributes</i> is typically used to form an <i>Example Table</i>. ";
    s += "In contrast, if a <i>Table</i> is passed to this module, the algorithm will consider all attributes ";
    s += "when forming the clusters. ";
    s += "</p>";

    s += "<p>";
    s += "The KMeans algorithm implementation is comprised of three modules. ";
    s += "This module is used to set control parameters for the algorithm. ";
    s += "A second module, <i>Sample Table Rows</i>, builds the sample set from the input <i>Table</i>. ";
    s += "The third module, <i>Cluster Assignment</i>, refines the initial clusters in a series of assignment passes. ";
    s += "The control parameters set in this module are passed as <i>Parameter Point</i>s to the ";
    s += "the other two modules, and determine their exact behavior. ";
    s += "The <i>OPT</i>, optimizable, versions of the <i>Sample Table Rows</i> and <i>Cluster Assignment</i> ";
    s += "modules must be used, as they can accept the <i>Parameter Point</i> inputs. ";
    s += "</p>";

    s += "<p>Data Type Restrictions: ";
    s += "The KMeans algorithm does not work if the input data being clustered contains missing values.  If ";
    s += "missing values are detected an exception will be raised. ";
    s += "The KMeans algorithm operates on numeric and boolean data types.  If the data to be clustered ";
    s += "contains nominal data types, it should be converted prior to performing the KMeans clustering. ";
    s += "The <i>Scalarize Nominals</i> module can be used to convert nominal types into boolean values. ";
    s += "</p>";

    s += "<p>Data Handling: ";
    s += "The input table is not modified by this algorithm. However, it is passed on to the other modules via ";
    s += "the <i>Table</i> output port, ";
    s += "and included as part of the <i>Cluster Model</i> that is created.";
    s += "</p>";

    s += "<p>Scalability: ";
    s += "This algorithm runs in time O(number of examples).  See the information for the component modules ";
    s += "to understand the overall memory requirements.";
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
      return "Parameters for Cluster Assignment module, available as a Parameter Point.";
    } else if (parm1 == 1) {
      return "Parameters for Sample Table Rows module, available as a Parameter Point.";
    } else if (parm1 == 2) {
      return "Table of examples to cluster, unchanged by module.";
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
      return "Parameters for Cluster Assignment";
    } else if (parm1 == 1) {
      return "Parameters for Sample Table Rows";
    } else if (parm1 == 2) {
      return "Table";
    } else {
      return "No such output";
    }
  }

  /**
   * put your documentation comment here
   * @return
   */
  public String[] getOutputTypes() {
    String[] out = {
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
    this.seed = (int) pp.getValue(1);
    this.useFirst = ( ( (int) pp.getValue(2)) == 1) ? true : false;
    this._clusterMethod = (int) pp.getValue(3);
    this._distanceMetric = (int) pp.getValue(4);
    this._maxIterations = (int) pp.getValue(5);

    doingit();
    this.pushOutput(this.pullInput(1), 2);

  }

  protected void doingit() {
    String[] names1 = {
        "clusterMethod", "distanceMetric", "maxIterations"};
    double[] values1 = {
         (double)this._clusterMethod,
        (double)this._distanceMetric,
        this._maxIterations};
    ParameterPoint pp = ParameterPointImpl.getParameterPoint(names1, values1);

    this.pushOutput(pp, 0);

    double uf = 0;
    if (this.useFirst) {
      uf = 1;
    }
    String[] names2 = {
        "sampleSize", "seed", "useFirst"};
    double[] values2 = {
         (double)this.N,
        (double)this.seed, uf};
    pp = ParameterPointImpl.getParameterPoint(names2, values2);

    this.pushOutput(pp, 1);
  }
}

// Start QA Comments
// 4/6/03 - QA Started by Ruth
// 4/7/03 - Updated module info and some formatting changes for consistency.
//        - Ready for Basic
// End QA Comments
