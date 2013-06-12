package ncsa.d2k.modules.core.discovery.cluster.sample;

/**
 * <p>
 * Title: ClusterRefinement
 * </p>
 * <p>
 * Description: Takes a set of centroids and a table and repeatedly assigns
 * table rows to clusters whose centroids are closest in vector space.  When one
 * assignment is completed, new centroids are calculated and the process is repeated.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: NCSA Automated Learning Group
 * </p>
 * @author D. Searsmith
 * @version 1.0
 */

//==============
// Java Imports
//==============

//===============
// Other Imports
//===============
import java.util.*;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.discovery.cluster.*;
import ncsa.d2k.modules.core.discovery.cluster.hac.*;
import ncsa.d2k.modules.core.discovery.cluster.util.*;

public class ClusterRefinement {

  //==============
  // Data Members
  //==============

  private long m_start = 0;
  private int[] _ifeatures = null;

  // Distance Metric
  static public final int s_Euclidean = 0;
  static public final int s_Manhattan = 1;
  static public final int s_Cosine = 2;

  //============
  // Properties
  //============
  private int _distanceMetric = s_Euclidean;
  public int getDistanceMetric() {
    return _distanceMetric;
  }

  public void setDistanceMetric(int dm) {
    _distanceMetric = dm;
  }

  private int m_numAssignments = 5;
  public int getNumAssignments() {
    return m_numAssignments;
  }

  public void setNumAssignments(int noc) {
    m_numAssignments = noc;
  }

  //just need these for the clustering at the end
  private int _clusterMethod = HAC.s_WardsMethod;
  public int getClusterMethod() {
    return _clusterMethod;
  }

  public void setClusterMethod(int noc) {
    _clusterMethod = noc;
  }

  private boolean m_verbose = false;
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

  private String _alias = "ClusterRefinement";
  public String getAlias() {
    return _alias;
  }

  public void setAlias( String moduleAlias ) {
    _alias = moduleAlias;
  }

  //================
  // Constructor(s)
  //================
  public ClusterRefinement(int cm, int dm, int na, boolean ver, boolean check, String moduleAlias) {
    this._distanceMetric = dm;
    this._clusterMethod = cm;
    this.m_numAssignments = na;
    this.m_verbose = ver;
    this.setCheckMissingValues(check);
    this._alias = moduleAlias;
  }

  //================
  // Public Methods
  //================

  /**
   * Takes a set of centroids and a table and repeatedly assigns
   * table rows to clusters whose centroids are closest in vector space.  When one
   * assignment is completed, new centroids are calculated and the process is repeated.
   * @param initcenters The initial cluster points (rows in a table)
   * @param initEntities The set of examples to be clustered
   * @return ClusterModel
   * @throws java.lang.Exception
   */
  public ClusterModel assign(Table initcenters, Table initEntities) throws java.
      lang.Exception {
    ClusterModel model = null;

    if (this.getCheckMissingValues()) {
      if (initEntities.hasMissingValues()) {
        throw new TableMissingValuesException(
	    getAlias() +
            ": Please replace or filter out missing values in your data.\n" +
            "This module does not work with missing values. ");
      }
    }

    boolean exceptionFlag = false;
    try {
      m_start = System.currentTimeMillis();
      ArrayList centers = null;
      ArrayList entities = null;

      /**
       * Assume that both tables input have the exact same input features in the
       * same columns.
       */

      if (initcenters instanceof ClusterModel) {
        centers = ( (ClusterModel) initcenters).getClusters();
      } else {

        if (initcenters instanceof ExampleTable) {
          _ifeatures = ( (ExampleTable) initcenters).getInputFeatures();
        } else {
          _ifeatures = new int[initcenters.getNumColumns()];
          for (int i = 0, n = initcenters.getNumColumns(); i < n; i++) {
            _ifeatures[i] = i;
          }
        }

        //Validate the column types -- can only operate on numeric or boolean types.
        for (int i = 0, n = _ifeatures.length; i < n; i++) {
          int ctype = initcenters.getColumnType(_ifeatures[i]);
          if (! ( (ctype == ColumnTypes.BYTE) ||
                 (ctype == ColumnTypes.BOOLEAN) ||
                 (ctype == ColumnTypes.DOUBLE) ||
                 (ctype == ColumnTypes.FLOAT) ||
                 (ctype == ColumnTypes.LONG) ||
                 (ctype == ColumnTypes.INTEGER) ||
                 (ctype == ColumnTypes.SHORT))) {

            exceptionFlag = true; // so we don't print message 2X
            String cname = initcenters.getColumnLabel(_ifeatures[i]);
            Exception ex1 = new TableColumnTypeException(ctype,
                "\n" +
                getAlias() +
                " module: Only boolean and numeric types are permitted.\n" +
                "Nominal data types must be transformed by a scalarization module or removed " +
                "prior to this module. \n" +
                "Column named " + cname + " has type " + ColumnTypes.getTypeName(ctype) );
            throw ex1;
          }
        }

        // Build clusters for each table row.
        centers = new ArrayList();
        for (int i = 0, n = initcenters.getNumRows(); i < n; i++) {
          TableCluster tc = new TableCluster(initcenters, i);
          centers.add(tc);
        }
      }

      if (_ifeatures == null) {
        if (initEntities instanceof ExampleTable) {
          _ifeatures = ( (ExampleTable) initEntities).getInputFeatures();
        } else {
          _ifeatures = new int[initEntities.getNumColumns()];
          for (int i = 0, n = initEntities.getNumColumns(); i < n; i++) {
            _ifeatures[i] = i;
          }
        }
      }

      //Validate the column types -- can only operate on numeric or boolean types.
      for (int i = 0, n = _ifeatures.length; i < n; i++) {
        int ctype = initEntities.getColumnType(_ifeatures[i]);
        if (! ( (ctype == ColumnTypes.BYTE) ||
               (ctype == ColumnTypes.BOOLEAN) ||
               (ctype == ColumnTypes.DOUBLE) ||
               (ctype == ColumnTypes.FLOAT) ||
               (ctype == ColumnTypes.LONG) ||
               (ctype == ColumnTypes.INTEGER) ||
               (ctype == ColumnTypes.SHORT))) {

            exceptionFlag = true; // so we don't print message 2X
            String cname = initEntities.getColumnLabel(_ifeatures[i]);
            Exception ex1 = new TableColumnTypeException(ctype,
                "\n" +
                getAlias() +
                " module: Only boolean and numeric types are permitted.\n" +
                "Nominal data types must be transformed by a scalarization module or removed " +
                "prior to this module. \n" +
                "Column " + cname + " has type " + ColumnTypes.getTypeName(ctype));
          throw ex1;
        }
      }

      // Build clusters for each table row.
      entities = new ArrayList();
      for (int i = 0, n = initEntities.getNumRows(); i < n; i++) {
        TableCluster tc = new TableCluster(initEntities, i);
        entities.add(tc);
      }

      if (getVerbose()) {
        System.out.println("\n>>> BEGINNING ASSIGNMENT REFINEMENT ... ");
      }

      for (int i = 0, n = m_numAssignments; i < n; i++) {
        //assign each element to one of k clusters
        if (getVerbose()) {
          System.out.println("ClusterRefinement -- assigning entities pass " +
                             (i + 1) + " out of " + m_numAssignments + " :: " +
                             centers.size() + " input.");
        }
        ArrayList oldCenters = centers;
        centers = assignEntities(centers, entities);
        //insert check here to evaluate if assignments should continue
        if (evaluation(oldCenters, centers)) {
          if (getVerbose()) {
            System.out.println(
              "New assignment has not changed significantly, assignments stopped at " +
              (i + 1) + " iterations.");
          }
          break;
        }
      }

      //cut the tree
      for (int i = 0, n = centers.size(); i < n; i++) {
        ( (TableCluster) centers.get(i)).cut();
      }

      if (getVerbose()) {
        System.out.println(">>> ASSIGNMENT REFINEMENT COMPLETE\n");
      }

      model = new ClusterModel(initEntities, centers, null);

      int numCenters = centers.size(); // save this value as reset in HAC

      //build the rest of the tree and add the cluster row to the model
      HAC hac = new HAC(this.getClusterMethod(), getDistanceMetric(),
                        centers.size(), 0, getVerbose(), false, getAlias() );
      model = hac.buildModel(model);

      long end = System.currentTimeMillis();
      if (getVerbose()) {
        System.out.println("\nEND EXEC -- " + getAlias() + " -- " + numCenters +
                           " built in " + (end - m_start) / 1000 + " seconds\n");
      }

    }
    catch (Exception ex) {
      if ( ! exceptionFlag ) {
        ex.printStackTrace();
        System.out.println( "ERROR: " + getAlias() + ": " + ex.getMessage());
      }
      throw ex;
    }
    return model;
  }

  //=================
  // Private Methods
  //=================

  private ArrayList assignEntities(ArrayList centers, ArrayList entities) throws
      Exception {
    //for each entity compute similarity for each profile and assign to most similar cluster

    TableCluster[] retarr = new TableCluster[centers.size()];

    for (int i = 0, in = entities.size(); i < in; i++) {
      TableCluster entity = (TableCluster) entities.get(i);
      double min = Double.MAX_VALUE;
      int ind = -1;
      for (int j = 0, m = centers.size(); j < m; j++) {
        double dist = HAC.distance(entity, (TableCluster) centers.get(j),
                                   getDistanceMetric());
        if (dist < min) {
          min = dist;
          ind = j;
        }
      }
      if (retarr[ind] == null) {
        retarr[ind] = entity;
      } else {
        retarr[ind] = TableCluster.merge(entity, retarr[ind]);
      }
    }
    ArrayList retval = new ArrayList();
    for (int i = 0, n = retarr.length; i < n; i++) {
      if (retarr[i] != null) {
        retval.add(retarr[i]);
      }
    }
    return retval;
  }

  private boolean evaluation(ArrayList oldcs, ArrayList newcs) {
    for (int i = 0, n = oldcs.size(); i < n; i++) {
      TableCluster tcold = (TableCluster) oldcs.get(i);
      boolean found = false;
      for (int j = 0, m = newcs.size(); j < m; j++) {
        TableCluster tcnew = (TableCluster) newcs.get(j);
        double dist = HAC.distance(tcold, tcnew, getDistanceMetric());
        if (dist < .0000001) {
          found = true;
          break;
        }
      }
      if (!found) {
        return false;
      }
    }
    return true;
  }

}
// Start QA Comments
// 4/8/03 - Ruth started QA;  Added arg to constructor so that module name from
//          calling module can be passed in.  This way, can distinguish which
//          module in itinerary is causing problems.  Updated exception messages
//          to use getAlias() rather than hard-coded name, and to add \n to msgs
//          for readability.  Also, so that multiple copies of msg not printed.
//        - Ready for Basic.
// End QA Comments
