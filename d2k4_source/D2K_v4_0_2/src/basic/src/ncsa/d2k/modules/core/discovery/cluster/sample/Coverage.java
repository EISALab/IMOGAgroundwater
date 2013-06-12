package ncsa.d2k.modules.core.discovery.cluster.sample;

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


/**
 * <p>Title: Coverage</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: NCSA Automated Learning Group</p>
 * @author D. Searsmith
 * @version 1.0
 */

public class Coverage {

  //==============
  // Data Members
  //==============

  int[] _ifeatures = null;

  //============
  // Properties
  //============

  private boolean _verbose = true;
  public boolean getVerbose() {
    return _verbose;
  }

  public void setVerbose(boolean b) {
    _verbose = b;
  }

  //Must be > 0
  private int _maxNumCenters = 500;
  public int getMaxNumCenters() {
    return _maxNumCenters;
  }

  public void setMaxNumCenters(int mt) {
    _maxNumCenters = mt;
  }

  private int _cutOff = 10;
  public int getCutOff() {
    return _cutOff;
  }

  public void setCutOff(int co) {
    _cutOff = co;
  }

  private int _distanceMetric = 0;
  public int getDistanceMetric() {
    return _distanceMetric;
  }

  public void setDistanceMetric(int dm) {
    _distanceMetric = dm;
  }

  protected boolean _mvCheck = true;
  public boolean getCheckMissingValues() {return _mvCheck;}
  public void setCheckMissingValues(boolean b) {_mvCheck = b;}

  //===============
  // Contructor(s)
  //===============

  public Coverage(int maxnum, int cutoff, int dm, boolean ver, boolean check) {
    this.setMaxNumCenters(maxnum);
    this.setDistanceMetric(dm);
    this.setVerbose(ver);
    this.setCutOff(cutoff);
    this.setCheckMissingValues(check);
  }

  /**
   * This module selects a sample set of the input table rows such that
   * the set of samples formed is approximately the minimum number of samples
   * needed such that for every row in the table there is at least one sample in
   * the sample set of distance <= Distance Cutoff.
   *
   * @param inittable Table to sample from.
   * @return Table of sampled rows.
   * @throws Exception
   */
  public Table sample(Table inittable) throws Exception{

    if (this.getCheckMissingValues()){
      if (inittable.hasMissingValues()){
        throw new TableMissingValuesException("CoverageSampler: Please replace or filter out missing values in your data.");
      }
    }

    Table itable = null;
    try {

      int initsz = inittable.getNumRows();


      if (inittable instanceof ClusterModel) {
        itable = (MutableTable) ( (ClusterModel) inittable).getTable();
      } else {
        itable = (MutableTable) inittable;
      }

      if (itable instanceof ExampleTable) {
        _ifeatures = ( (ExampleTable) itable).getInputFeatures();
      } else {
        _ifeatures = new int[itable.getNumColumns()];
        for (int i = 0, n = itable.getNumColumns(); i < n; i++) {
          _ifeatures[i] = i;
        }
      }

      HAC.validateNonTextColumns(itable, _ifeatures);

      double maxdist = HAC.calculateMaxDist(itable, _ifeatures, this.getDistanceMetric(), this.getCutOff());

      itable = sample(itable, maxdist);

      System.out.println("Input table contained " + initsz + " rows.");
      System.out.println("Sampled table created with " + itable.getNumRows() +
                         " rows.");

    }
    catch (Exception ex) {
      ex.printStackTrace();
      System.out.println(ex.getMessage());
      System.out.println("ERROR: CoverageSampler.doit()");
      throw ex;
    }
    return itable;
  }

  //=================
  // Private Methods
  //=================

  /**
   * Perform sampling
   * @param orig Table to sample from.
   * @param maxdist Distance cutoff.
   * @return New table of sampled rows.
   * @throws Exception
   */
  final private Table sample(Table orig, double maxdist) throws
      Exception {

    //System.out.println(maxdist);
    ArrayList clusters = new ArrayList();
    for (int i = 0, n = orig.getNumRows(); i < n; i++) {
      TableCluster tc = new TableCluster(orig, i);
      clusters.add(tc);
    }

    ArrayList part1 = new ArrayList();
    boolean loop = false;
    for (int i = 0, n = clusters.size(); i < n; i++) {
      TableCluster tc = (TableCluster) clusters.get(i);
      loop = false;
      for (int j = 0, m = part1.size(); j < m; j++) {
        double dist = HAC.distance(tc, (TableCluster) part1.get(j),
                                   this.getDistanceMetric());
        //System.out.println(dist);
        if (dist <= maxdist) {
          loop = true;
          break;
        }
      }
      if (!loop) {
        part1.add(tc);
        if (part1.size() == this.getMaxNumCenters()){
          break;
        }
      }
      if (Math.IEEEremainder(i, 1000) == 0) {
        System.out.println("CoveragSampler.sample() -- Rows Processed: " +
                           i);
      }
    }

    System.out.println("Found " + part1.size() + " samples.");

    int[] keeps = new int[Math.min(part1.size(), this.getMaxNumCenters())];
    for (int i = 0, n = Math.min(keeps.length, this.getMaxNumCenters()); i < n; i++){
      TableCluster tcc = (TableCluster) part1.get(i);
      keeps[i] = tcc.getMemberIndices()[0];
    }

    Table newTable = orig.getSubset(keeps);

    System.out.println("Returning " + newTable.getNumRows() + " samples.");
    return newTable;
  }

}