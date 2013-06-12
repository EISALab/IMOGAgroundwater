package ncsa.d2k.modules.core.discovery.ruleassociation.fpgrowth;


import java.util.*;
import gnu.trove.*;


public class FPSparse implements java.io.Serializable{


  private int[] _columns = null;
  private int[] _labels = null;
  private TIntObjectHashMap _rows = new TIntObjectHashMap();
  private int _numcols = -1;
  private int _colcnt = 0;


  public FPSparse(int numcols) {
    _columns = new int[numcols];
    _labels = new int[numcols];
    _numcols = numcols;
  }


  public int getLabel(int col){
      return _labels[col];
  }

  public int getNumColumns(){
    return _colcnt;
  }

  public int getNumRows(){
    return _rows.size();
  }

  public void addColumn(int lbl){
    _labels[_colcnt++] = lbl;
  }

  public int getInt(int row, int col) {
      return ((TIntIntHashMap)_rows.get(row)).get(col);
  }

  public void setInt(int data, int row, int col){
    _columns[col] = _columns[col] + data;

    //check for row
    if (_rows.containsKey(row)){
      ((TIntIntHashMap)_rows.get(row)).put(col, data);
    } else {
      TIntIntHashMap iihm = new TIntIntHashMap();
      _rows.put(row, iihm);
      iihm.put(col, data);
    }
  }

  public int getColumnTots(int col){
      return _columns[col];
  }

  public int[] getRowIndices(int row){
      return ((TIntIntHashMap)_rows.get(row)).keys();
  }
}


