package ncsa.d2k.modules.core.discovery.cluster.eval;

//==============
// Java Imports
//==============
import java.util.*;

//===============
// Other Imports
//===============
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.parameter.impl.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.discovery.cluster.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.discovery.cluster.util.*;

public class MinLenEncodingClusterModelEvaluator
    extends ComputeModule {

  //============
  // Properties
  //============
  private boolean _verbose = false;
  public boolean getVerbose() {
    return _verbose;
  }

  public void setVerbose(boolean b) {
    _verbose = b;
  }

  //=======================
  // D2K Abstract Override

  /**
   * Return array of property descriptors for this module.
   * @return array
   */
  public PropertyDescription[] getPropertiesDescriptions() {
    PropertyDescription[] descriptions = new PropertyDescription[1];

    descriptions[0] = new PropertyDescription("verbose",
                                              "Verbose Output",
        "Do you want verbose output to the console.");

    return descriptions;
  }

  /**
    Return information about the module.
    @return A detailed description of the module.
   */
  public String getModuleInfo() {
    String s = "<p>Overview: ";
    s += "This module outputs a score for the input cluster model using a minimum ";
    s += "encoding length measurement.";
    s += "</p>";

    s += "<p>Detailed Description: ";
    s += "The minimum encoding length measure is affected by the number of clusters ";
    s += "formed and the amount of information that can be saved by tagging cluster ";
    s += "entities with a cluster ID.  The intuition is that good clusterings ";
    s += "will form clusters that meaningfully limit the possibility of values thus ";
    s += "economizing the amount of information needed to describe the overall ";
    s += "clustering.";
    s += "</p>";
    s += "<p>";
    s += "Reference: M.-C. Ludl and G. Widmer. Towards a Simple Clustering Criterion ";
    s += "Based on Minimum Length Encoding. http://citeseer.nj.nec.com/542199.html";
    s += "</p>";

    s += "<p>Data Handling: ";
    s += "The input model is not modified.";
    s += "</p>";

    s += "<p>Scalability: ";
    s += "This algorithm makes two passes over the table that is part of the cluster ";
    s += "model.  It uses minimal heap resources.";
    s += "</p>";
    return s;
  }

  /**
     Return the name of this module.
     @return The name of this module.
   */
  public String getModuleName() {
    return "MLE Cluster Model Evaluator";
  }

  /**
     Return a String array containing the datatypes the inputs to this
     module.
     @return The datatypes of the inputs.
   */
  public String[] getInputTypes() {
    String[] types = {
        "ncsa.d2k.modules.core.discovery.cluster.ClusterModel"};
    return types;
  }

  /**
     Return a String array containing the datatypes of the outputs of this
     module.
     @return The datatypes of the outputs.
   */
  public String[] getOutputTypes() {
    String[] types = {
        "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint"};
    return types;
  }

  /**
     Return a description of a specific input.
     @param i The index of the input
     @return The description of the input
   */
  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "This is the ClusterModel that will be evaluated.";
      default:
        return "No such input";
    }
  }

  /**
     Return the name of a specific input.
     @param i The index of the input.
     @return The name of the input
   */
  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "ClusterModel";
      default:
        return "NO SUCH INPUT!";
    }
  }

  /**
     Return the description of a specific output.
     @param i The index of the output.
     @return The description of the output.
   */
  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "This is the score for this model.";
      default:
        return "No such output";
    }
  }

  /**
     Return the name of a specific output.
     @param i The index of the output.
     @return The name of the output
   */
  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "ParameterPoint";
      default:
        return "NO SUCH OUTPUT!";
    }
  }

  /**
     Perform the work of the module.
   */
  public void doit() throws Exception {
    try {

      ClusterModel cm = (ClusterModel)this.pullInput(0);

      int[] ifeatures = null;

      if (!cm.hasTable()) {
        throw new Exception("The input model does not contain a table.");
      }
      Table tab = (Table)cm.getTable();
      if (tab instanceof ExampleTable) {
        ifeatures = ( (ExampleTable) tab).getInputFeatures();
      } else {
        ifeatures = new int[tab.getNumColumns()];
        for (int i = 0, n = tab.getNumColumns(); i < n; i++) {
          ifeatures[i] = i;
        }
      }


      ArrayList clusters = cm.getClusters();
      double score = 0;

        Object[] obs = this.calculateMinMax(tab, ifeatures);
        double[] min = (double[])obs[0];
        double[] max = (double[])obs[1];
        double s = 0;
        for (int j = 0, k = ifeatures.length; j < k; j++){
          if (tab.getColumnType(ifeatures[j]) != ColumnTypes.BOOLEAN){
            s += max[j] - min[j];
          } else {
            s += ((max[j] - min[j]) > .5) ? 2 : 1;
          }
        }
        score = clusters.size()*2*s;
        score += clusters.size()*tab.getNumRows();

//        System.out.println(score);

      for (int i = 0, n = clusters.size(); i < n; i++) {
        TableCluster tc = (TableCluster)clusters.get(i);

//        System.out.println("Cluster: " + tc.getClusterLabel());
//        System.out.print("rows: ");
        int[] rows = tc.getMemberIndices();
//        for (int a = 0, b = tc.getMemberIndices().length; a < b; a++){
//          System.out.print(rows[a] + " ");
//        }
//        System.out.println();

        Table ctab = tab.getSubset(tc.getMemberIndices());
        obs = this.calculateMinMax(ctab, ifeatures);
        min = (double[])obs[0];
        max = (double[])obs[1];
        s = 0;
        for (int j = 0, k = ifeatures.length; j < k; j++){
          if (tab.getColumnType(ifeatures[j]) != ColumnTypes.BOOLEAN){
            s += max[j] - min[j];
          } else {
            s += ((max[j] - min[j]) > .5) ? 2 : 1;
          }
        }
//        System.out.println(tc.getSize() + " " + s);
        score += tc.getSize()*s;
      }
//      System.out.println(score + "\n\n");

      double[] utility = new double[1];
      utility[0] = score;
      // push outputs //
      //ANCA: changed from parameter.basic to parameter.impl
      //ParameterPointImpl objectivePoint = new ParameterPointImpl();
      String[] names = new String[1];
      names[0] = "Score";
      //objectivePoint.createFromData(names, utility);
     ParameterPoint objectivePoint = ParameterPointImpl.getParameterPoint(names,utility);
      this.pushOutput(objectivePoint, 0);

      if (getVerbose()){
        System.out.println("MinLenEncodingClusterModelEvaluator: Score for this model -- " + score);
      }

    }
    catch (Exception ex) {
      ex.printStackTrace();
      System.out.println(ex.getMessage());
      System.out.println(
          "EXCEPTION: MinLenEncodingClusterModelEvaluator.doit()");
      throw ex;
    }

  }

  /**
   * Find a approximate max distance for the input table using only the specified
   * input features and distance metric.  Return the thresh (% value) times this distance.
   * @param itable Table of examples
   * @param ifeatures Features of interest
   * @param dm Distance Metric
   * @param thresh The percent value to multiply times max distance.
   * @return a percent (thresh) of the maxdist
   */
  private Object[] calculateMinMax(Table itable, int[] ifeatures) {
    double maxdist = 0;
    //find distance threshold
    double[] max = new double[ifeatures.length];
    double[] min = new double[ifeatures.length];
    for (int i = 0, n = ifeatures.length; i < n; i++) {
      min[i] = Double.MAX_VALUE;
      max[i] = Double.MIN_VALUE;
    }
//    int bcnts = 0;
//    for (int i = 0, n = ifeatures.length; i < n; i++) {
//      if (itable.getColumnType(ifeatures[i]) == ColumnTypes.BOOLEAN) {
//        max[i] = 1;
//        min[i] = 0;
//        bcnts++;
//      }
//    }
//    if (bcnts != ifeatures.length) {
      for (int i = 0, n = itable.getNumRows(); i < n; i++) {
        for (int j = 0, m = ifeatures.length; j < m; j++) {
          //if (! (itable.getColumnType(ifeatures[j]) == ColumnTypes.BOOLEAN)) {
            double compval = itable.getDouble(i, ifeatures[j]);
            if (max[j] < compval) {
              max[j] = compval;
            }
            if (min[j] > compval) {
              min[j] = compval;
            }
          //}
        }
//      }
    }
    Object[] ret = new Object[2];
    ret[0] = min;
    ret[1] = max;
//    System.out.println("Num tab rows: " + itable.getNumRows());
//    System.out.print("min: ");
//    for (int i = 0, n = min.length; i < n; i++){
//      System.out.print(min[i] + " ");
//    }
//    System.out.println();
//    System.out.print("max: ");
//    for (int i = 0, n = max.length; i < n; i++){
//      System.out.print(max[i] + " ");
//    }
//    System.out.println();
    return ret;
  }

}
