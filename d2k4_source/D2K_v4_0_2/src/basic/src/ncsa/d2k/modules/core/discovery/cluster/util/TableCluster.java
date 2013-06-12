package ncsa.d2k.modules.core.discovery.cluster.util;

//==============
// Java Imports
//==============

import java.io.*;

//===============
// Other Imports
//===============
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.sparse.*;

/**
 * <p>Title: TableCluster</p>
 * <p>Description: Holds information about a cluster of Table rows.</p>
 * <p>TODO: Make it Sparse Table Friendly <p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: NCSA </p>
 * @author D. Searsmith
 * @version 1.0
 */

public class TableCluster implements Serializable {

  //==============
  // Data Members
  //==============

  static private int s_id = 0;

  private int[] _members = null;
  private Table _table = null;
  private double[] _centroid = null;
  private double _centroid_norm = -1;
  private boolean _centroidComputed = false;
  private TableCluster _cluster1 = null;
  private TableCluster _cluster2 = null;

  //for sparse tables
  private double[] _spcentroid = null;
  private int[] _cind = null;

  private int _label = assignID();

  //===========
  //Properties
  //===========
  private boolean _sparse = false;
  public void setSparse(boolean b){
    _sparse = b;
  }
  public boolean getSparse(){
    return _sparse;
  }

  private String _txtLabel = null;
  public void setTextClusterLabel(String i) {
    _txtLabel = i;
  }
  public String getTextClusterLabel() {
    return _txtLabel;
  }

  private double _childDistance = -1;
  public void setChildDistance(double i) {
    _childDistance = i;
  }
  public double getChildDistance() {
    return _childDistance;
  }

  //================
  // Constructor(s)
  //================

  public TableCluster() {}

  public TableCluster(Table table, int row) {
    _table = table;
    _members = new int[1];
    _members[0] = row;
    if (_table instanceof SparseTable){
      setSparse(true);
      //Thread.currentThread().dumpStack();
    }else if (_table instanceof ExampleTable) {
      _centroid = new double[ ( (ExampleTable) _table).getInputFeatures().length];
    } else {
      _centroid = new double[_table.getNumColumns()];
    }
  }

  public TableCluster(Table table, int[] rows) {
    _table = table;
    _members = rows;
    if (_table instanceof SparseTable){
      setSparse(true);
      //Thread.currentThread().dumpStack();
    }else if (_table instanceof ExampleTable) {
      _centroid = new double[ ( (ExampleTable) _table).getInputFeatures().length];
    } else {
      _centroid = new double[_table.getNumColumns()];
    }
  }

  public TableCluster(Table table, TableCluster c1, TableCluster c2) {
    _table = table;
    _cluster1 = c1;
    _cluster2 = c2;
    if (_table instanceof SparseTable){
      setSparse(true);
      //Thread.currentThread().dumpStack();
    }else if (_table instanceof ExampleTable) {
      _centroid = new double[ ( (ExampleTable) _table).getInputFeatures().length];
    } else {
      _centroid = new double[_table.getNumColumns()];
    }
  }

  //===============
  // Static Method
  //===============

  static public synchronized int assignID(){
    return s_id++;
  }

  static public TableCluster merge(TableCluster tc1, TableCluster tc2) {
    if (tc1 == null) {
      System.out.println("TableCluster.merge -- input param tc1 is null");
      return null;
    }
    if (tc2 == null) {
      System.out.println("TableCluster.merge -- input param tc2 is null");
      return null;
    }
    if (tc1.getTable() != tc2.getTable()) {
      System.out.println("TableCluster.merge -- tc1 and tc2 reference different tables and cannot be merged.");
      return null;
    }
    TableCluster newtc = new TableCluster(tc1.getTable(), tc1, tc2);
    return newtc;
  }

  //================
  // Public Methods
  //================

  public void cut(){
    computeCentroid();
    _cluster1 = null;
    _cluster2 = null;
  }

  public int getClusterLabel() {
    return _label;
  }

  public Object[] getSubClusters() {
    Object[] ret = new Object[2];
    ret[0] = _cluster1;
    ret[1] = _cluster2;
    return ret;
  }

  /**
   * Return the left child of this node or null if it doesn't exist.
   * @return left child as TableCluster
   */
  public TableCluster getLC(){
    return _cluster1;
  }

  /**
   * Return the left right of this node or null if it doesn't exist.
   * @return right child as TableCluster
   */
  public TableCluster getRC(){
    return _cluster2;
  }

  public boolean isLeaf() {
    return (_cluster1 == null);
  }

  /**
   * TODO: Fix for sparse tables.
   */
  public void computeCentroid() {
    if (_centroidComputed) {
      return;
    }
    int[] members = this.getMemberIndices();

    if (!getSparse()){
      if (_table instanceof ExampleTable) {
        double sum = 0;
        int[] feats = ( (ExampleTable) _table).getInputFeatures();
        for (int i = 0, n = feats.length; i < n; i++) {
          sum = 0;
          for (int j = 0, m = members.length; j < m; j++) {
            sum += _table.getDouble(members[j], feats[i]);
          }
          _centroid[i] = sum;
        }
      } else {
        double sum = 0;
        for (int i = 0, n = _table.getNumColumns(); i < n; i++) {
          sum = 0;
          for (int j = 0, m = members.length; j < m; j++) {
            sum += _table.getDouble(members[j], i);
          }
          _centroid[i] = sum;
        }
      }
      int cnt = members.length;
      for (int i = 0, n = _centroid.length; i < n; i++) {
        _centroid[i] = _centroid[i] / cnt;
      }
    } else {
      double sum = 0;
      double[] temp = null;
      double[] tempv = null;
      int[] tempi = null;
      java.util.HashSet ofeats = new java.util.HashSet();
      if (_table instanceof ExampleTable) {
        ofeats = new java.util.HashSet();
        int[] xfeats = ( (ExampleTable) _table).getOutputFeatures();
        for (int x = 0, y = xfeats.length; x < y; x++){
          ofeats.add(new Integer(xfeats[x]));
        }
        int numcols = ( (ExampleTable) _table).getInputFeatures().length;
        temp = new double[numcols];
        tempv = new double[numcols];
        tempi = new int[numcols];
      } else {
        int numcols = _table.getNumColumns();
        temp = new double[numcols];
        tempv = new double[numcols];
        tempi = new int[numcols];
      }
      for (int j = 0, m = members.length; j < m; j++) {
        int feats[] = ((SparseTable)_table).getRowIndices(members[j]);
        for (int i = 0, n = feats.length; i < n; i++) {
          if (!ofeats.contains(new Integer(feats[i]))){
            temp[feats[i]] += _table.getDouble(members[j], feats[i]);
          }
        }
      }
      int cnt = 0;
      for (int i = 0, n = temp.length; i < n; i++){
        if (temp[i] != 0){
          tempi[cnt] = i;
          tempv[cnt] = temp[i];
          cnt++;
        }
      }
      _cind = new int[cnt];
      _spcentroid = new double[cnt];
      System.arraycopy(tempi, 0, this._cind, 0, cnt);
      System.arraycopy(tempv,0,this._spcentroid, 0, cnt);
      cnt = members.length;
      for (int i = 0, n = _spcentroid.length; i < n; i++) {
        _spcentroid[i] = _spcentroid[i] / cnt;
      }
    }

    _centroidComputed = true;
  }

  public double getCentroidNorm() {
    computeCentroid();

    if (_centroid_norm < 0) {
      double temp = 0;
      if (!getSparse()){
        for (int i = 0, n = _centroid.length; i < n; i++) {
          temp += Math.pow(_centroid[i], 2);
        }
      } else {
        for (int i = 0, n = _spcentroid.length; i < n; i++) {
          temp += Math.pow(_spcentroid[i], 2);
        }
      }
      _centroid_norm = Math.sqrt(temp);
    }
    if (_centroid_norm == 0){
      _centroid_norm = .0000001;
    }
    return this._centroid_norm;
  }

  public double[] getCentroid() {
    computeCentroid();
    if (this.getSparse()){
      int sz = 0;
      if (this.getTable() instanceof SparseExampleTable){
        sz = ((SparseExampleTable)this.getTable()).getInputFeatures().length;
      } else {
        sz = this.getTable().getNumRows();
      }
      double[] centroid = new double[sz];
      for (int i = 0, n = sz; i < n; i++){
        centroid[0] = 0;
      }
      for (int i = 0, n = _cind.length; i < n; i++){
        centroid[_cind[i]] = this._spcentroid[i];
      }
      return centroid;
    }
    return this._centroid;
  }

  public double getNthCentroidValue(int z){
    computeCentroid();
    if (!getSparse()){
      return _centroid[z];
    } else {
      for (int i = 0, n = _cind.length; i < n; i++){
        if (z == _cind[i]){
          return _spcentroid[i];
        } else if (z > _cind[i]){
          return 0;
        }
      }
      return 0;
    }
  }

  public double[] getSparseCentroidValues(){
    computeCentroid();
    return this._spcentroid;
  }

  public int[] getSparseCentroidInd(){
    computeCentroid();
    return this._cind;
  }

  public int[] getMemberIndices() {
    int[] temparr = null;
    if (_members == null) {
      if ( (_cluster1 == null) || (_cluster2 == null)) {
        System.out.println(
            "ERROR: TableCluster.getMemberIndices() -- no clusters or indices defined.");
        return null;
      } else {
        int[] arr1 = _cluster1.getMemberIndices();
        int[] arr2 = _cluster2.getMemberIndices();
        _members = new int[arr1.length + arr2.length];
        System.arraycopy(arr1, 0, _members, 0, arr1.length);
        System.arraycopy(arr2, 0, _members, arr1.length, arr2.length);
      }
    }
    temparr = new int[_members.length];
    System.arraycopy(_members, 0, temparr, 0, _members.length);
    return temparr;
  }

  public Table getTable() {
    return _table;
  }

  public int getSize() {
    return getMemberIndices().length;
  }

  public String generateTextLabel(){
    String out = null;


    //implement SelfDescriptive check here


//    if (_table = null){
//      out = "" + this.getClusterLabel();
//    } else {
      out = "[ ";
      double[] centroid = this.getCentroid();
//      int[] ifeatures = null;
//      if (_table instanceof ExampleTable) {
//        ifeatures = ( (ExampleTable) _table).getInputFeatures();
//      } else {
//        ifeatures = new int[_table.getNumColumns()];
//        for (int i = 0, n = _table.getNumColumns(); i < n; i++) {
//          ifeatures[i] = i;
//        }
//      }
        for (int i = 0, n = centroid.length; i < n; i++) {
//        if (_table.getColumnType(ifeatures[i])== ColumnTypes.BOOLEAN){
//          out += (centroid[i] == 0) ? "false " : "true ";
//        } else if (_table.getColumnType(ifeatures[i])== ColumnTypes.BYTE){
//          out += (byte)centroid[i] + " ";
//        } else if (_table.getColumnType(ifeatures[i])== ColumnTypes.DOUBLE){
//          out += centroid[i] + " ";
//        } else if (_table.getColumnType(ifeatures[i])== ColumnTypes.FLOAT){
//          out += (float)centroid[i] + " ";
//        } else if (_table.getColumnType(ifeatures[i])== ColumnTypes.LONG){
//          out += (long)centroid[i] + " ";
//        } else if (_table.getColumnType(ifeatures[i])== ColumnTypes.INTEGER){
//          out += (int)centroid[i] + " ";
//        } else if (_table.getColumnType(ifeatures[i])== ColumnTypes.SHORT){
//          out += (short)centroid[i] + " ";
//        }
          if (i == 0){
            out += centroid[i] + " ... ";
          }
          if (i == (n-1)){
            out += centroid[i];
          }
        }
      out += "]";
      _txtLabel = out;
//    }
    return out;
  }

  public String toString(){
    if (_txtLabel == null){
      generateTextLabel();
    }
    return _txtLabel;
  }

  //=================
  // Private Methods
  //=================


}
