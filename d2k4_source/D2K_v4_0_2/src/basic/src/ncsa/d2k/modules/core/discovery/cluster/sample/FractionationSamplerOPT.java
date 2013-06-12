package ncsa.d2k.modules.core.discovery.cluster.sample;

//==============
// Java Imports
//==============

import java.util.*;

//===============
// Other Imports
//===============
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.discovery.cluster.*;
import ncsa.d2k.modules.core.discovery.cluster.util.*;

/**
 * <p>Title: FractionationSampler</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: NCSA Automated Learning Group</p>
 * @author D. Searsmith
 * @version 1.0
 */

public class FractionationSamplerOPT
    extends ComputeModule implements ClusterParameterDefns{

  //==============
  // Data Members
  //==============

  private ArrayList m_clusters = null;
  private int m_lastSize = -1;
  private int m_count = -1;
  private ArrayList m_clustersHold = null;
  private ArrayList m_clusters2 = null;

  protected ArrayList _pushing = null;

  protected Table _itable = null;

  //============
  // Properties
  //============

  protected boolean m_verbose = false;
  public boolean getVerbose() {
    return m_verbose;
  }

  public void setVerbose(boolean b) {
    m_verbose = b;
  }

  protected boolean _mvCheck = true;
  public boolean getCheckMissingValues() {
    return _mvCheck;
  }

  public void setCheckMissingValues(boolean b) {
    _mvCheck = b;
  }

  protected int m_numberOfClusters = 5;
  protected int m_nthSortTerm = 0;
  protected int m_maxPartitionSize = 200;

  //================
  // Constructor(s)
  //================

  public FractionationSamplerOPT() {
  }

  //========================
  // D2K Abstract Overrides

  /**
   * Return array of property descriptors for this module.
   * @return array
   */
  public PropertyDescription[] getPropertiesDescriptions() {
    PropertyDescription[] descriptions = new PropertyDescription[2];

    descriptions[0] = new PropertyDescription("checkMissingValues",
                                              "Check Missing Values",
        "Perform a check for missing values on the table inputs (or not).");

    descriptions[1] = new PropertyDescription("verbose",
                                              "Verbose Output",
        "Do you want verbose output to the console.");

    return descriptions;
  }

  /**
     Return the name of this module.
     @return The name of this module.
   */
  public String getModuleName() {
    return "Fractionation Sampler";
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
      return "Table of examples to cluster";
    } else if (parm1 == 2) {
      return "Cluster Model from clusterer";
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
      return "Parameter Point";
    } else if (parm1 == 1) {
      return "Table";
    } else if (parm1 == 2) {
      return "Cluster Model";
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
        "ncsa.d2k.modules.core.datatype.table.Table",
        "ncsa.d2k.modules.core.discovery.cluster.ClusterModel"};
    return in;
  }

  /**
    Return information about the module.
    @return A detailed description of the module.
   */
  public String getModuleInfo() {
    String s = "<p>Overview: ";
    s += "Chooses a sample set of rows through a process of repeated partitioning and clustering.  The ";
    s += "table rows are treated as vectors of a vector space.";
    s += "</p>";

    s += "<p>Detailed Description: ";
    s += "This module sorts the initial examples (converted to clusters) by a key attribute denoted by <i>Sort Attribute</i>. ";
    s += "The set of sorted clusters is then segmented into equal partitions of size <i>Max Partition Size</i>. ";
    s += "Each of these partitions is then passed through the agglomerative clusterer to produce ";
    s += "<i>" + NUM_CLUSTERS + "</i> clusters.  All the clusters are gathered together for all partitions and the ";
    s += "entire process is repeated until only <i>" + NUM_CLUSTERS + "</i> clusters remain. ";
    s += "The sorting step is to encourage like clusters into same partitions. ";
    s += "</p>";

    s += "<p>Data Handling: ";
    s += "The original table input is not modified but it is included in the final <i>Cluster Model</i>.";
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
      return "Cluster Model for Hier. Agglom. Clusterer";
    } else if (parm1 == 1) {
      return "Final Cluster Model for refinement";
    } else {
      return "";
    }
  }

  /**
     Return the name of a specific output.
     @param i The index of the output.
     @return The name of the output
   */
  public String getOutputName(int parm1) {
    if (parm1 == 0) {
      return "Cluster Model";
    } else if (parm1 == 1) {
      return "Cluster Model for Refinement";
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
        "ncsa.d2k.modules.core.discovery.cluster.ClusterModel",
        "ncsa.d2k.modules.core.discovery.cluster.ClusterModel"};
    return out;
  }

  /**
     Code to execute before doit.
   */
  public void beginExecution() {
    m_clustersHold = null;
    m_clusters = null;
    _pushing = new ArrayList();
    m_lastSize = -1;
    m_count = -1;
  }

  /**
     Code to execute at end of itinerary run.
   */
  public void endExecution() {
    super.endExecution();
    m_clustersHold = null;
    m_clusters = null;
    m_lastSize = -1;
    m_count = -1;
  }

  /**
     Conditions for module firing.
     @return boolean
   */
  public boolean isReady() {
    //System.out.println(">>>>>>>>>>>>>>>>> " + this.inputFlags[0] + " " + this.inputFlags[1] + " " + this.inputFlags[2] + " " + _pushing.size());
    if ( ( (this.getFlags()[0] > 0) && (this.getFlags()[1] > 0)) ||
        (this.getFlags()[2] > 0) || (_pushing.size() > 0)) {
      return true;
    } else {
      return false;
    }
  }

  /**
   Perform the work of the module.
   @throws Exception
   */
  protected void doit() throws java.lang.Exception {

    ArrayList clusters = null;
    ClusterModel cm = null;

    try {

      if (_pushing.size() > 0) {
        ArrayList arrlist = new ArrayList( (ArrayList) _pushing.remove(0));
        ClusterModel mod = new ClusterModel(_itable, arrlist, null);
        this.pushOutput(mod, 0);
        if (this.getFlags()[2] == 0) {
          return;
        }

      }

      if (this.getFlags()[1] > 0) {
        _itable = (Table)this.pullInput(1);
        if (this.getCheckMissingValues()) {
          if (_itable.hasMissingValues()) {
            throw new TableMissingValuesException("FractionationSamplerOPT: Please replace or filter out missing values in your data.");
          }
        }
        ParameterPoint pp = (ParameterPoint)this.pullInput(0);
        this.m_numberOfClusters = (int) pp.getValue(0);
        this.m_nthSortTerm = (int) pp.getValue(1);
        this.m_maxPartitionSize = (int) pp.getValue(2);
        if (this.m_nthSortTerm > (_itable.getNumColumns()-1)) {
            throw new Exception("FractionationSamplerOPT: The sort attribute index is too large for this table: " + m_nthSortTerm);
        }
      }

      if (this.getFlags()[2] > 0) {
        cm = (ClusterModel)this.pullInput(2);
        clusters = cm.getClusters();
      }

      doingit(clusters);

    }
    catch (Exception ex) {
      ex.printStackTrace();
      System.out.println(ex.getMessage());
      System.out.println("ERROR: FractionationSamplerOPT.doit()");
      throw ex;
    }
  }

  //=================
  // Private Methods
  //=================

  protected void doingit(ArrayList clusters) {

    if (clusters != null) {
      m_clustersHold.addAll(clusters);

      //if we have not gotten back all of our clusters yet then quit
      if (m_count > 1) {
        m_count--;
        return;
      } else {
        m_clusters = m_clustersHold;
      }
    }

    //check to see if we should stop and send output to the refinery
    if (m_clusters != null) {
      System.out.println("Size:       " + m_clusters.size() + " " + m_lastSize);
      if ( (m_clusters.size() <= m_numberOfClusters) ||
          (m_clusters.size() >= m_lastSize)) {
        ClusterModel model = new ClusterModel(_itable, m_clusters, null);
        this.pushOutput(model, 1);
        return;
      }
    }

    //else record the size, partition, send them off
    if (m_clusters == null) {
      //first time through
      m_clusters = new ArrayList();
      for (int i = 0, n = _itable.getNumRows(); i < n; i++) {
        TableCluster tc = new TableCluster(_itable, i);
        m_clusters.add(tc);
      }
    }

    m_lastSize = m_clusters.size();

    //sort on nth largest term
    m_clusters = clusterSort(m_clusters);

    //partition to an array of arrays (partitions)
    m_clusters = partitionClusters(m_clusters);

    m_count = m_clusters.size();
    m_clustersHold = new ArrayList();

    _pushing = new ArrayList(m_clusters);

    if (_pushing.size() > 0) {
      ArrayList arrlist = new ArrayList( (ArrayList) _pushing.remove(0));
      ClusterModel mod = new ClusterModel(_itable, arrlist, null);
      this.pushOutput(mod, 0);
    }
  }

  private ArrayList clusterSort(ArrayList clusters) {
    int size = clusters.size();
    TreeSet map = new TreeSet(new Terms_Comparator());
    map.addAll(clusters);
    clusters = new ArrayList();
    for (Iterator it = map.iterator(); it.hasNext(); ) {
      clusters.add(it.next());
    }
    if (getVerbose()) {
      System.out.println(
          "FractionationSamplerOPT.clusterSort(...) -- number of clusters to sort: " +
          size);
      System.out.println(
          "FractionationSamplerOPT.clusterSort(...) -- number of clusters sorted: " +
          clusters.size());
    }
    return clusters;
  }

  private ArrayList partitionClusters(ArrayList clusters) {

    int size = clusters.size();
    ArrayList retClusters = new ArrayList();

    if (size <= this.m_maxPartitionSize) {
      retClusters.add(clusters);
      if (getVerbose()) {
        System.out.println("FractionationSamplerOPT.partitionClusters(...) -- number of clusters to partition: " +
                           size);
        System.out.println(
            "FractionationSamplerOPT.partitionClusters(...) -- number of partitions made: " +
            retClusters.size());
      }
      return retClusters;
    }

    int ind = 0;
    ArrayList subl = null;
    while (true) {
      if ( (size - ind) >= this.m_maxPartitionSize) {
        subl = new ArrayList(clusters.subList(ind, ind + m_maxPartitionSize));
        retClusters.add(subl);
        ind += m_maxPartitionSize;
      } else {
        if ( (size - ind) == 0) {
          break;
        } else if ( (size - ind) <= 20) {
          subl = (ArrayList) retClusters.get(retClusters.size() - 1);
          subl.addAll(clusters.subList(ind, size));
          break;
        } else {
          subl = new ArrayList(clusters.subList(ind, size));
          retClusters.add(subl);
          break;
        }
      }
    }
    if (getVerbose()) {
      System.out.println("FractionationSamplerOPT.partitionClusters(...) -- number of clusters to partition: " +
                         size);
      System.out.println(
          "FractionationSamplerOPT.partitionClusters(...) -- number of partitions made: " +
          retClusters.size());
    }
    return retClusters;
  }

  //=============
  // Inner Class
  //=============
  private class Terms_Comparator
      implements java.util.Comparator {

    public Terms_Comparator() {
    }

    //======================
    //Interface: Comparator
    //======================
    public int compare(Object o1, Object o2) {
      TableCluster obj1 = (TableCluster) o1;
      TableCluster obj2 = (TableCluster) o2;
      double term1 = obj1.getNthCentroidValue(m_nthSortTerm);
      double term2 = obj2.getNthCentroidValue(m_nthSortTerm);

      if (term1 < term2) {
        return 1;
      } else if (term1 > term2) {
        return -1;
      } else {
        if (obj1 == obj2) {
          return 0;
        } else {
          return 1;
        }
      }
    }

    public boolean equals(Object o) {
      return this.equals(o);
    }
  }

}