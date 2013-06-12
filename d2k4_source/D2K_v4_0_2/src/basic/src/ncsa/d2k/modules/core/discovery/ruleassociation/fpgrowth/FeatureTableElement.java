package ncsa.d2k.modules.core.discovery.ruleassociation.fpgrowth;

//==============
// Java Imports
//==============
import java.util.*;

public class FeatureTableElement {

  //==============
  // Data Members
  //==============
  private int _lbl = -1;
  private ArrayList _ptrs = new ArrayList();
  private int _cnt = 0;
  private int _pos = -1;

  //================
  // Constructor(s)
  //================

  public FeatureTableElement(int lbl, int cnt, int pos){
    _lbl = lbl;
    _cnt = cnt;
    _pos = pos;
  }

  public FeatureTableElement(int lbl, int cnt, int pos, Collection nodes){
    _lbl = lbl;
    _cnt = cnt;
    _pos = pos;
    if (nodes != null){
      _ptrs = new ArrayList();
      _ptrs.addAll(nodes);
    }
  }

  //================
  // Public Methods
  //================
  public void clearList(){
    _ptrs.clear();
    _ptrs = null;
  }

  public int getCnt(){
    return _cnt;
  }

  public List getPointers(){
    return _ptrs;
  }

  public Iterator getPointersIter(){
    return _ptrs.iterator();
  }

  public int getLabel(){
    return _lbl;
  }

  public void addPointer(FPTreeNode node){
    _ptrs.add(node);
  }

  public int getPosition(){
    return _pos;
  }
}
