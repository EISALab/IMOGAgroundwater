package ncsa.d2k.modules.core.discovery.cluster.hac;

//==============
// Java Imports
//==============
import java.util.*;

//===============
// Other Imports
//===============
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.sparse.*;
import ncsa.d2k.modules.core.discovery.cluster.*;
import ncsa.d2k.modules.core.discovery.cluster.util.*;

/**
 * <p>Title: HAC</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: NCSA Automated Learning Group</p>
 * @author D. Searsmith
 * @version 1.0
 */

public class HAC {

  //==============
  // Data Members
  //==============

  private long m_start = -1;
  private int[] _ifeatures = null;
  static public final String _CLUSTER_COLUMN_LABEL = "Cluster_Column";

  static public final int s_WardsMethod = 0;
  static public final int s_SingleLink = 1;
  static public final int s_CompleteLink = 2;
  static public final String[] s_ClusterMethodLabels = {"Ward's Method", "Single Link", "Complete Link", "UPGMA", "WPGMA", "UPGMC", "WPGMC"};
  static public final String[] s_ClusterMethodDesc = {"Minimize the square of the distance",
      "Distance of closest pair (one from each cluster)",
      "Distance of furthest pair (one from each cluster)",
      "Unweighted pair group method using arithmetic averages",
      "Weighted pair group method using arithmetic averages",
      "Unweighted pair group method using centroids",
      "Weighted pair group method using centroids"};

  /**
   * In the following, unweighted means that the contribution that each cluster element
   * plays is adjusted for the size of clusters.
   *
   * The weighted case ignores clusters sizes and thus smaller cluster elements are given
   * equal weight as large cluster elements.
   */
  //unweighted pair group method using arithmetic averages
  static public final int s_UPGMA = 3;
  //weighted pair group method using arithmetic averages
  static public final int s_WPGMA = 4;
  //unweighted pair group method using centroids
  static public final int s_UPGMC = 5;
  //unweighted pair group method using centroids
  static public final int s_WPGMC = 6;

  static public final String[] s_DistanceMetricLabels = {"Euclidean",
      "Manhattan",
      "Cosine"};

  static public final String[] s_DistanceMetricDesc = {"\"Straight\" line distance between points",
      "Distance between two points measured along axes at right angles",
      "1 minus the cosine of the angle between the norms of the vectors denoted by two points"};

  // Distance Metric
  static public final int s_Euclidean = 0;
  static public final int s_Manhattan = 1;
  static public final int s_Cosine = 2;

  private int _clusterMethod = -1;
  public int getClusterMethod() {
    return _clusterMethod;
  }

  public void setClusterMethod(int noc) {
    _clusterMethod = noc;
  }

  private int _distanceMetric = -1;
  public int getDistanceMetric() {
    return _distanceMetric;
  }

  public void setDistanceMetric(int dm) {
    _distanceMetric = dm;
  }

  private boolean _verbose = false;
  public boolean getVerbose() {
    return _verbose;
  }

  public void setVerbose(boolean b) {
    _verbose = b;
  }

  private int _numberOfClusters = -1;
  public int getNumberOfClusters() {
    return _numberOfClusters;
  }

  public void setNumberOfClusters(int noc) {
    _numberOfClusters = noc;
  }

  private int _thresh = 0;
  public int getDistanceThreshold() {
    return _thresh;
  }

  public void setDistanceThreshold(int noc) {
    _thresh = noc;
  }

  protected boolean _mvCheck = true;
  public boolean getCheckMissingValues() {
    return _mvCheck;
  }

  public void setCheckMissingValues(boolean b) {
    _mvCheck = b;
  }

  private String _alias = "HAC";
  public String getAlias() {
    return _alias;
  }

  public void setAlias( String moduleAlias ) {
    _alias = moduleAlias;
  }

  public HAC(int cm, int dm, int num, int thresh, boolean ver, boolean check, String moduleAlias) {
    this.setClusterMethod(cm);
    this.setDistanceMetric(dm);
    this.setNumberOfClusters(num);
    this.setDistanceThreshold(thresh);
    this.setVerbose(ver);
    this.setCheckMissingValues(check);
    this.setAlias(moduleAlias);
  }

  /**
   * Does the work of clustering the input table values and returns
   * a ClusterModel.
   * @param inittable
   * @return ClusterModel
   * @throws Exception
   */
  public ClusterModel buildModel(Table inittable) throws Exception {

    if (this.getCheckMissingValues()) {
      if (inittable.hasMissingValues()) {
        throw new TableMissingValuesException( getAlias() +
			" (HAC): Please replace or filter out missing values in your data.");
      }
    }

    if (inittable.getNumRows() < 1) {
        throw new Exception( getAlias() +
			" (HAC): Input table has no rows.");
    }

    ClusterModel model = null;

    m_start = System.currentTimeMillis();

    ArrayList resultClusters = null;

    Table itable = null;

    if (inittable instanceof ClusterModel) {
      itable = ( (ClusterModel) inittable).getTable();
    } else {
      itable = inittable;
    }

    if (itable instanceof ExampleTable) {
      _ifeatures = ( (ExampleTable) itable).getInputFeatures();
    } else {
      _ifeatures = new int[itable.getNumColumns()];
      for (int i = 0, n = itable.getNumColumns(); i < n; i++) {
        _ifeatures[i] = i;
      }
    }

    validateNonTextColumns(itable, _ifeatures);

    //indexlist maps the active indices for the proximity matrix
    int[] indexlist = null;
    //hash map for keeping track of cluster to proxm index
    HashMap indexmap = null;

    ArrayList clusters = null;
    if (inittable instanceof ClusterModel) {
      clusters = ( (ClusterModel) inittable).getClusters();
      indexlist = new int[clusters.size()];
      indexmap = new HashMap(clusters.size());
      for (int i = 0, n = clusters.size(); i < n; i++) {
        TableCluster tc = (TableCluster) clusters.get(i);
        indexlist[i] = i;
        indexmap.put(tc, new Integer(i));
      }
    } else {
      //Create a cluster for each row
      indexlist = new int[itable.getNumRows()];
      indexmap = new HashMap(itable.getNumRows());
      clusters = new ArrayList();
      for (int i = 0, n = itable.getNumRows(); i < n; i++) {
        TableCluster tc = new TableCluster(itable, i);
        clusters.add(tc);
        indexlist[i] = i;
        indexmap.put(tc, new Integer(i));
      }
    }

    //proximity matrix for storing the dval object values
    Object[][] proxm = new Object[clusters.size()][clusters.size()];

    int dvalcnt = 0;
    double distval = 0;

    double maxdist = -1;
    if (_thresh != 0) {
      maxdist = calculateMaxDist(itable, _ifeatures, this.getDistanceMetric(),
                                 this.getDistanceThreshold());
    }

    try {

      // DEBUG ===================
//       if (clusters.size() != indexlist.length){
//         System.out.println("ERROR: clusters.size() = " + clusters.size() + " indexlength.length = " + indexlist.length);
//       }
//
      //===========================

      TreeSet cRank = null;
      cRank = new TreeSet(new cRank_Comparator());

      boolean firstTime = true;
      //double shortestDist = Double.MAX_VALUE;

      //while the number of clusters is still greater than m_numberOfClusters
      while (clusters.size() > 1) {

        //System.out.println(clusters.size() + " clusters remain ... beginning similarity search ...");
        //find the two most similar clusters
        double dist = 0;
        TableCluster tc1 = null;
        TableCluster tc2 = null;
        if (firstTime) {
          firstTime = false;
          for (int x = 0, xn = clusters.size(); x < xn; x++) {
            for (int y = x + 1, yn = clusters.size(); y < yn; y++) {
              if (y > x) {
                double simval = 0;
                TableCluster clustX = (TableCluster) clusters.get(x);
                TableCluster clustY = (TableCluster) clusters.get(y);

                distval = distance(clustX, clustY, this._distanceMetric);

                if (distval < 0) {
                  distval = 0;
                }

                Object[] dval = new Object[4];
                dval[0] = clustX;
                dval[1] = clustY;
                dval[2] = new Double(distval);
                dval[3] = new Integer(dvalcnt++);
                if (!cRank.add(dval)) {
                  System.out.println(
                      getAlias() + " (HAC): " +
                      ">>>>>>>>>>>>>>>>>>>>> UNABLE TO ADD TO cRANK " +
                      dval.hashCode() + " " + simval);
                }
//                if (getVerbose()) {
//                  System.out.println("Clusters " + x + " " + y +
//                                     " have distance: " + distval);
//                }
                //add dval to proximity matrix
                proxm[x][y] = dval;
              }
            }
          }
	  if (getVerbose()) {
            System.out.println(getAlias() + " (HAC): INITIAL PROXIMITY MATRIX COMPLETE");
          }

          // DEBUG ===================
//          int k = clusters.size();
//          if (cRank.size() != ((k*k) - k)/2){
//            System.out.println("ERROR: incorrect number of elements in initial matrix.");
//          }
          //==========================

        }

//        if (getVerbose()) {
//          System.out.println("Number of clusters: " + clusters.size());
//        }

        Object[] o = (Object[]) cRank.first();

        tc1 = (TableCluster) o[0];
        tc2 = (TableCluster) o[1];
        //shortestDist = ((Double)o[2]).doubleValue();

        TableCluster newc = TableCluster.merge(tc1, tc2);

        if ( (clusters.size() <= _numberOfClusters) && (_thresh == 0) &&
            (resultClusters == null)) {
          resultClusters = new ArrayList(clusters);
        }

        if (resultClusters == null) {
          if ( (_thresh != 0) &&
              ( ( (Double) ( (Object[]) cRank.first())[2]).doubleValue() >
               maxdist)) {
            resultClusters = new ArrayList(clusters);
          }
        }

        //primarily for the cluster vis
        newc.setChildDistance( ( (Double) o[2]).doubleValue());
        // System.out.println(( (Double) o[2]).doubleValue());
        clusters.remove(tc1);
        clusters.remove(tc2);
        clusters.add(newc);

        Object[] c1c2arr = null;
        ArrayList alist = new ArrayList();

        int row1 = ( (Integer) indexmap.get(tc1)).intValue();
        int row2 = ( (Integer) indexmap.get(tc2)).intValue();
        int newrow = Math.min(row1, row2);

        for (int i = 0, n = indexlist.length; i < n; i++) {
          //use the indexlist to know where the active indices are
          int ind = indexlist[i];

          //and if the position row1, row2 then exclude it here because we don't
          //want it added to alist twice
          if ( (ind < row1) && (ind != row2)) {
            alist.add(proxm[ind][row1]);
          } else if ( (ind > row1) && (ind != row2)) {
            alist.add(proxm[row1][ind]);
          }

          if (ind < row2) {
            alist.add(proxm[ind][row2]);
          } else if (ind > row2) {
            alist.add(proxm[row2][ind]);
          }
        }

        if (row1 < row2) {
          c1c2arr = (Object[]) proxm[row1][row2];
          proxm[row1][row2] = null;
        } else {
          c1c2arr = (Object[]) proxm[row2][row1];
          proxm[row2][row1] = null;
        }

        // Debug ================================
//        int acnt = 2*indexlist.length - 3;
//        if (alist.size() != acnt){
//          System.out.println("Wrong number of clusters to remove: " + alist.size() + " should be: " + acnt);
//        }
        // Debug ================================

        //Remove old sim values for the two clusters being merged from the sorted list
        for (int i = 0, n = alist.size(); i < n; i++) {
          //System.out.println("Its there?: " + cRank.contains(alist.get(i)));
          if (!cRank.remove(alist.get(i))) {
            System.out.println("We have already removed it once?: " +
                               cRank.contains(alist.get(i)));
            System.out.println("Object hash:" + alist.get(i));
            System.out.println(">>>>>>>>>>>>>>>>>>>>> " +
                               ( (Double) ( (Object[]) alist.get(i))[2]).
                               doubleValue() + " " +
                               ( (Integer) ( (Object[]) alist.get(i))[3]).
                               intValue());
            System.out.println();
          }
        }

        //row/column to be removed from proxm
        int remove = Math.max(row1, row2);

        //find sims between the new cluster and all other clusters and add them to the
        //sorted list
        for (int i = 0, n = clusters.size(); i < (n - 1); i++) {
          double simval = 0;

          int clustind = ( (Integer) indexmap.get(clusters.get(i))).intValue();
          Double d1 = null;
          Double d2 = null;
          if (clustind < row1) {
            d1 = (Double) ( (Object[]) proxm[clustind][row1])[2];
          } else {
            d1 = (Double) ( (Object[]) proxm[row1][clustind])[2];
          }

          if (clustind < row2) {
            d2 = (Double) ( (Object[]) proxm[clustind][row2])[2];
          } else {
            d2 = (Double) ( (Object[]) proxm[row2][clustind])[2];
          }

          double sim1 = d1.doubleValue();
          double sim2 = d2.doubleValue();
          double sim3 = ( (Double) c1c2arr[2]).doubleValue();
          int sz1 = tc1.getSize();
          int sz2 = tc2.getSize();
          int sz3 = ( (TableCluster) clusters.get(i)).getSize();
          int sznew = newc.getSize();

          int method = getClusterMethod();
          if (method == s_UPGMA) {
            // ** UPGMA **
            simval = (sz1 * sim1) / (sznew) + (sz2 * sim2) / sznew;
          } else if (method == s_WardsMethod) {
            // ** Ward's **
            simval = ( ( (sz1 + sz3) * sim1) / (sznew + sz3)) +
                ( ( (sz2 + sz3) * sim2) / (sznew + sz3)) -
                ( (sz3 * sim3) / (sznew + sz3));
          } else if (method == s_SingleLink) {
            // ** Single Link **
            simval = sim1 / 2 + sim2 / 2 - Math.abs(sim1 - sim2) / 2;
          } else if (method == s_CompleteLink) {
            // ** Complete Link **
            simval = sim1 / 2 + sim2 / 2 + Math.abs(sim1 - sim2) / 2;
          } else if (method == s_WPGMA) {
            // ** WPGMA **
            simval = sim1 / 2 + sim2 / 2;
          } else if (method == s_UPGMC) {
            // ** UPGMC **
            simval = sim1 / 2 + sim2 / 2 - sim3 / 4;
          } else if (method == s_WPGMC) {
            // ** WPGMC **
            simval = (sz1 * sim1) / (sznew) + (sz2 * sim2) / sznew -
                ( ( (sz1) * (sz2)) / Math.pow( (sz1 + sz2), 2)) * sim3;
          } else {
            // System.out.println("ERROR: unknown cluster method specified.");
            throw new Exception(getAlias() + " (HAC): unknown cluster method specified.");
          }

          //System.out.println("cid: " + cid + " old sims: " + sim1 + " " + sim2 + " new sim: " + simval);
          if (simval < 0) {
            simval = 0;
          }
          Object[] dval = new Object[4];
          dval[0] = newc;
          dval[1] = (TableCluster) clusters.get(i);
          dval[2] = new Double(simval);
          dval[3] = new Integer(dvalcnt++);
          if (!cRank.add(dval)) {
            System.out.println(getAlias() + " (HAC): >>>>>>>>>>>>>>>>>>>>> UNABLE TO ADD TO cRANK ");
          }
          //update the matrix
          if (clustind < newrow) {
            proxm[clustind][newrow] = dval;
          } else {
            proxm[newrow][clustind] = dval;
          }
          if (clustind < remove) {
            proxm[clustind][remove] = null;
          } else {
            proxm[remove][clustind] = null;
          }
        }

        //adjust the index list
        int[] temparr = new int[indexlist.length - 1];
        int cnter = 0;
        for (int i = 0, n = indexlist.length; i < n; i++) {
          if (indexlist[i] != remove) {
            temparr[cnter] = indexlist[i];
            cnter++;
          }
        }
        indexlist = temparr;

        //fixup indexmap
        if (indexmap.remove(tc1) == null) {
          System.out.println(
              ">>>>>>>>>>>>>>>>>>>>> UNABLE TO REMOVE TC1 from IndexMap");
        }
        if (indexmap.remove(tc2) == null) {
          System.out.println(
              ">>>>>>>>>>>>>>>>>>>>> UNABLE TO REMOVE TC2 from IndexMap");
        }
        indexmap.put(newc, new Integer(newrow));
      }

      //Outout a Cluster Model which is an object containing that table (optional), clusters, and
      //cluster tree.

      //set classes (ints) in each cluster

      if (resultClusters == null) {
        resultClusters = clusters;
      }

//      //remove pre-existing cluster column
//      if (itable.getColumnLabel(itable.getNumColumns() -
//          1).equals(_CLUSTER_COLUMN_LABEL)) {
//        itable.removeColumn(itable.getNumColumns() - 1);
//      }
//      itable.addColumn(new int[itable.getNumRows()]);
//      itable.setColumnLabel(_CLUSTER_COLUMN_LABEL, itable.getNumColumns() - 1);
      if (getVerbose()){
        for (int i = 0, n = resultClusters.size(); i < n; i++) {
          TableCluster tc = (TableCluster) resultClusters.get(i);
          System.out.println("Cluster " + tc.getClusterLabel() + " containing " +
                             tc.getSize() + " elements.");
        }
      }
//
//      //set prediction to class value for each row in table
//      //output table

      model = new ClusterModel(itable, resultClusters,
                               (TableCluster) clusters.get(0));

    }
    catch (Exception ex) {
      ex.printStackTrace();
      System.out.println(ex.getMessage());
      //System.out.println("EXCEPTION: HAC.doit()");
      throw ex;
    }
    finally {
      long end = System.currentTimeMillis();
      if (resultClusters != null) {
        if (getVerbose()) {
          System.out.println("\n" + getAlias() + " (HAC): END EXEC -- clusters built: " +
                           resultClusters.size() + " in " +
                           (end - m_start) / 1000 +
                           " seconds\n");
        }
      }
    }
    return model;
  }

  //=================
  // Private Methods
  //=================

  /**
   * Find a approximate max distance for the input table using only the specified
   * input features and distance metric.  Return the thresh (% value) times this distance.
   * @param itable Table of examples
   * @param ifeatures Features of interest
   * @param dm Distance Metric
   * @param thresh The percent value to multiply times max distance.
   * @return a percent (thresh) of the maxdist
   */
  static public double calculateMaxDist(Table itable, int[] ifeatures, int dm,
                                        int thresh) {
    double maxdist = 0;
    //find distance threshold
    double[] max = new double[ifeatures.length];
    double[] min = new double[ifeatures.length];
    for (int i = 0, n = ifeatures.length; i < n; i++) {
      min[i] = Double.MAX_VALUE;
      max[i] = Double.MIN_VALUE;
    }
    int bcnts = 0;
    for (int i = 0, n = ifeatures.length; i < n; i++) {
      if (itable.getColumnType(ifeatures[i]) == ColumnTypes.BOOLEAN) {
        max[i] = 1;
        min[i] = 0;
        bcnts++;
      }
    }
    if (bcnts != ifeatures.length) {
      if (! (itable instanceof SparseTable)) {
        for (int i = 0, n = itable.getNumRows(); i < n; i++) {
          for (int j = 0, m = ifeatures.length; j < m; j++) {
            if (! (itable.getColumnType(ifeatures[j]) == ColumnTypes.BOOLEAN)) {
              double compval = itable.getDouble(i, ifeatures[j]);
              if (max[j] < compval) {
                max[j] = compval;
              }
              if (min[j] > compval) {
                min[j] = compval;
              }
            }
          }
        }

      } else {
        for (int i = 0, n = itable.getNumRows(); i < n; i++) {
          int[] cols = ((SparseTable)itable).getRowIndices(i);
          for (int j = 0, m = cols.length; j < m; j++) {
            if (! (itable.getColumnType(cols[j]) == ColumnTypes.BOOLEAN)) {
              double compval = itable.getDouble(i, cols[j]);
              if (max[cols[j]] < compval) {
                max[cols[j]] = compval;
              }
              if (min[cols[j]] > compval) {
                min[cols[j]] = compval;
              }
            }
          }
        }

      }
    }
    //Debug ==========================
//    System.out.print("min: ");
//    for (int i = 0, n = ifeatures.length; i < n; i++) {
//      System.out.print(min[i] + " ");
//    }
//    System.out.println();
//    System.out.print("max: ");
//    for (int i = 0, n = ifeatures.length; i < n; i++) {
//      System.out.print(max[i] + " ");
//    }
//    System.out.println();
    //================================
    return maxdist = ( (double) thresh / 100) * distance(max, min, dm);
  }

  /**
   * Check if any columns are non numeric (excluding boolean)
   * @param itable
   * @param ifeatures
   * @throws Exception throw TableColumnTypeException if non-numeric column(s) are detected
   */
  static public void validateNonTextColumns(Table itable, int[] ifeatures) throws
      Exception {
    //Validate the column types -- can only operate on numeric or boolean types.
    for (int i = 0, n = ifeatures.length; i < n; i++) {
      int ctype = itable.getColumnType(ifeatures[i]);
      if (! ( (ctype == ColumnTypes.BYTE) ||
             (ctype == ColumnTypes.BOOLEAN) ||
             (ctype == ColumnTypes.DOUBLE) ||
             (ctype == ColumnTypes.FLOAT) ||
             (ctype == ColumnTypes.LONG) ||
             (ctype == ColumnTypes.INTEGER) ||
             (ctype == ColumnTypes.SHORT))) {
        // can't use getAlias() in next exception since this is static method
        Exception ex1 = new TableColumnTypeException(ctype,
            " (HAC): " +
            "Only boolean and numeric types are permitted."
            +
            " For nominal input types use a scalarization transformation or remove"
            + " them from the input set.");
        //System.out.println("EXCEPTION -- HAC.doit(): " + ex1.getMessage());
        throw ex1;
      }
    }
  }

  /**
   * TODO: Need to accommodate sparse tables
   *
   * @param tc1 cluster to calculate distance from
   * @param tc2 cluster to calculate distance from
   * @param tab table for these clusters
   * @return distance value as double
   */
  static public double distance(TableCluster tc1, TableCluster tc2, int dm) {

    if (!tc1.getSparse()){

      double[] centroid1 = tc1.getCentroid();
      double[] centroid2 = tc2.getCentroid();
      if (dm == HAC.s_Euclidean) {
        double diffs = 0;
        for (int i = 0, n = centroid1.length; i < n; i++) {
          double diff = centroid1[i] - centroid2[i];
          diffs += Math.pow(diff, 2);
        }
        return Math.sqrt(diffs);
      } else if (dm == HAC.s_Manhattan) {
        double diffs = 0;
        for (int i = 0, n = centroid1.length; i < n; i++) {
          double diff = centroid1[i] - centroid2[i];
          diffs += Math.abs(diff);
        }
        return diffs;
      } else if (dm == HAC.s_Cosine) {
        double prods = 0;
        for (int i = 0, n = centroid1.length; i < n; i++) {
          double prod = centroid1[i] * centroid2[i];
          prods += prod;
        }
        return Math.abs(1 - (prods / (tc1.getCentroidNorm() * tc2.getCentroidNorm())));
      } else {
        //this should never happen
        System.out.println("Unknown distance metric ... ");
        return -1;
      }
    } else {

      int[] ind1 = tc1.getSparseCentroidInd();
      double[] val1 = tc1.getSparseCentroidValues();

      int[] ind2 = tc2.getSparseCentroidInd();
      double[] val2 = tc2.getSparseCentroidValues();

      if (dm == HAC.s_Euclidean) {

        double dist = 0;
        int x = 0;
        int y = 0;
        //System.out.println(m_termIDS.length + " "  + terms.length);
        while ( (x < ind1.length) && (y < ind2.length)) {
          if (ind1[x] == ind2[y]) {
            //System.out.println(x + " "  + y);
            dist += Math.pow(val1[x] - val2[y], 2);
            x++;
            y++;
          } else if (ind1[x] < ind2[y]) {
            dist += Math.pow(val1[x], 2);
            x++;
          } else {
            dist += Math.pow(val2[y], 2);
            y++;
          }
        }
        while (x < ind1.length){
          dist += Math.pow(val1[x], 2);
          x++;
        }
        while (y < ind2.length){
          dist += Math.pow(val2[y], 2);
          y++;
        }

        return Math.sqrt(dist);
      } else if (dm == HAC.s_Manhattan) {
        double dist = 0;
        int x = 0;
        int y = 0;
        //System.out.println(m_termIDS.length + " "  + terms.length);
        while ( (x < ind1.length) && (y < ind2.length)) {
          if (ind1[x] == ind2[y]) {
            //System.out.println(x + " "  + y);
            dist += Math.abs(val1[x] - val2[y]);
            x++;
            y++;
          } else if (ind1[x] < ind2[y]) {
            dist += Math.abs(val1[x]);
            x++;
          } else {
            dist += Math.abs(val2[y]);
            y++;
          }
        }
        while (x < ind1.length){
          dist += Math.abs(val1[x]);
          x++;
        }
        while (y < ind2.length){
          dist += Math.abs(val2[y]);
          y++;
        }
        return dist;
      } else if (dm == HAC.s_Cosine) {
        double prods = 0;
        int x = 0;
        int y = 0;
        //System.out.println(m_termIDS.length + " "  + terms.length);
        while ( (x < ind1.length) && (y < ind2.length)) {
          if (ind1[x] == ind2[y]) {
            //System.out.println(x + " "  + y);
            prods += val1[x]*val2[y];
            x++;
            y++;
          } else if (ind1[x] < ind2[y]) {
            x++;
          } else {
            y++;
          }
        }
        return Math.abs(1 - (prods / (tc1.getCentroidNorm() * tc2.getCentroidNorm())));
      } else {
        //this should never happen
        System.out.println("Unknown distance metric ... ");
        return -1;
      }


    }
  }

  /**
   * Find distance between two examples (double arrays) using specified distance metric.
   * @param centroid1
   * @param centroid2
   * @param dm
   * @return Distance value as double
   */
  static public double distance(double[] centroid1, double[] centroid2, int dm) {
    if (dm == HAC.s_Euclidean) {
      double diffs = 0;
      for (int i = 0, n = centroid1.length; i < n; i++) {
        double diff = centroid1[i] - centroid2[i];
        diffs += Math.pow(diff, 2);
      }
      return Math.sqrt(diffs);
    } else if (dm == HAC.s_Manhattan) {
      double diffs = 0;
      for (int i = 0, n = centroid1.length; i < n; i++) {
        double diff = centroid1[i] - centroid2[i];
        diffs += Math.abs(diff);
      }
      return diffs;
    } else if (dm == HAC.s_Cosine) {
      double prods = 0;
      for (int i = 0, n = centroid1.length; i < n; i++) {
        double prod = centroid1[i] * centroid2[i];
        prods += prod;
      }
      double norm1 = 0;
      double norm2 = 0;
      for (int i = 0, n = centroid1.length; i < n; i++) {
        norm1 += Math.pow(centroid1[i], 2);
        norm2 += Math.pow(centroid2[i], 2);
      }
      norm1 = Math.sqrt(norm1);
      norm2 = Math.sqrt(norm2);
      if (norm1 == 0) {
        norm1 = .000001;
      }
      if (norm2 == 0) {
        norm2 = .000001;
      }
      //System.out.println(norm1 + "    " + norm2);
      return Math.abs(1 - (prods / (norm1 * norm2)));
    } else {
      //this should never happen
      System.out.println("Unknown distance metric ... ");
      return -1;
    }
  }

  //=============
  // Inner Class
  //=============
  private class cRank_Comparator
      implements java.util.Comparator {
    /** The small deviation allowed in double comparisons */

    /**
     * put your documentation comment here
     */
    public cRank_Comparator() {
    }

    //======================
    //Interface: Comparator
    //======================
    public int compare(Object o1, Object o2) {
      Object[] objarr1 = (Object[]) o1;
      Object[] objarr2 = (Object[]) o2;
      if (eq( ( (Double) objarr1[2]).doubleValue(),
             ( (Double) objarr2[2]).doubleValue())) {
        if ( ( (Integer) objarr1[3]).intValue() >
            ( (Integer) objarr2[3]).intValue()) {
          return 1;
        } else if ( ( (Integer) objarr1[3]).intValue() <
                   ( (Integer) objarr2[3]).intValue()) {
          return -1;
        } else {
          return 0;
        }
      } else if ( ( (Double) objarr1[2]).doubleValue() >
                 ( (Double) objarr2[2]).doubleValue()) {
        return 1;
      } else {
        return -1;
      }
    }

    public boolean eq(double a, double b) {
      return a == b;
    }

    /**
     * put your documentation comment here
     * @param o
     * @return
     */
    public boolean equals(Object o) {
      return this.equals(o);
    }
  }

}

// Start QA Comments
// 4/11/03 - Ruth;   Added getAlias() to ctor & to messages so we can tell which
//           instance is causing problems;  commented out some lines that would
//           produce double exception messages... revisit later.
//         - Ready for Basic
// End QA Comments
