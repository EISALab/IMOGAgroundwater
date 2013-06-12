package ncsa.d2k.modules.core.discovery.cluster.sample;

//==============
// Java Imports
//==============

//===============
// Other Imports
//===============
import java.util.*;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.discovery.cluster.*;
import ncsa.d2k.modules.core.discovery.cluster.gui.properties.*;
import ncsa.d2k.modules.core.discovery.cluster.util.*;

public class FractionationSampler
    extends FractionationSamplerOPT {

  //==============
  // Data Members
  //==============

  //============
  // Properties
  //============

  public int getNumberOfClusters() {
    return m_numberOfClusters;
  }

  public void setNumberOfClusters(int noc) {
    m_numberOfClusters = noc;
  }

  public int getNthSortTerm() {
    return m_nthSortTerm;
  }

  public void setNthSortTerm(int noc) {
    m_nthSortTerm = noc;
  }

  public int getmaxPartitionSize() {
    return m_maxPartitionSize;
  }

  public void setMaxPartitionSize(int noc) {
    m_maxPartitionSize = noc;
  }

  //================
  // Constructor(s)
  //================
  public FractionationSampler() {
  }

  //================
  // Public Methods
  //================

  //========================
  // D2K Abstract Overrides

  /**
   * Return a list of the property descriptions.
   * @return a list of the property descriptions.
   */
  public PropertyDescription[] getPropertiesDescriptions() {
    PropertyDescription[] descriptions = new PropertyDescription[5];
    descriptions[0] = new PropertyDescription("numberOfClusters",
                                              "Number of Clusters",
        "This property specifies the number of clusters to form (>= 2).");
    descriptions[1] = new PropertyDescription("NthSortTerm",
                                              "Sort Attribute",
                                              "The index of for the column denoting the attribute to be used to sort on prior to partitioning.");
    descriptions[2] = new PropertyDescription("maxPartitionSize",
                                              "Max Partition Size",
        "The size of partitions to use in the sampling process."
        );
    descriptions[3] = new PropertyDescription("CheckMissingValues",
                                              "Check Missing Values",
        "Perform a check for missing values on the table inputs (or not).");
    descriptions[4] = new PropertyDescription("verbose",
                                              "Verbose Output",
        "Do you want verbose output to the console.");
    return descriptions;
  }

  public CustomModuleEditor getPropertyEditor() {
    return new FractionationSampler_Props(this, true, true);
  }

  public String getInputInfo(int parm1) {
    if (parm1 == 0) {
      return "Table of values to cluster";
    } else if (parm1 == 2) {
      return "Cluster Model from clusterer";
    } else {
      return "";
    }
  }

  public String getInputName(int parm1) {
    if (parm1 == 0) {
      return "Table";
    } else if (parm1 == 1) {
      return "ClusterModel";
    } else {
      return "";
    }
  }

  public String[] getInputTypes() {
    String[] in = {
        "ncsa.d2k.modules.core.datatype.table.Table",
        "ncsa.d2k.modules.core.discovery.cluster.ClusterModel"};
    return in;
  }

  public boolean isReady() {
    if ( (this.getFlags()[0] > 0) || (this.getFlags()[1] > 0) ||
        (_pushing.size() > 0)) {
      return true;
    } else {
      return false;
    }
  }

  protected void doit() throws java.lang.Exception {

    ArrayList clusters = null;
    ClusterModel cm = null;

    try {

      if (_pushing.size() > 0) {
        ArrayList arrlist = new ArrayList( (ArrayList) _pushing.remove(0));
        ClusterModel mod = new ClusterModel(_itable, arrlist, null);
        this.pushOutput(mod, 0);
        if (this.getFlags()[1] == 0) {
          return;
        }

      }

      if (this.getFlags()[0] > 0) {
        _itable = (MutableTable)this.pullInput(0);
        if (this.getCheckMissingValues()) {
          if (_itable.hasMissingValues()) {
            throw new TableMissingValuesException("FractionationSampler: Please replace or filter out missing values in your data.");
          }
        }
      }

      if (this.getFlags()[1] > 0) {
        cm = (ClusterModel)this.pullInput(1);
        clusters = cm.getClusters();
      }

      doingit(clusters);

    }
    catch (Exception ex) {
      ex.printStackTrace();
      System.out.println(ex.getMessage());
      System.out.println("ERROR: FractionationSampler.doit()");
      throw ex;
    }
  }

}
