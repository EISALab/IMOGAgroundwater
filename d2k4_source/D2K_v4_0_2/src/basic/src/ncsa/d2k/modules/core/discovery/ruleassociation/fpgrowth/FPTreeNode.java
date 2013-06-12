package ncsa.d2k.modules.core.discovery.ruleassociation.fpgrowth;

//==============
// Java Imports
//==============
import java.util.*;

import gnu.trove.*;

public class FPTreeNode {

  //==============
  // Data Members
  //==============

  private int _lbl = -1;
  private int _cnt = 0;
  private FPTreeNode _ptr = null;
  private TIntObjectHashMap _children = new TIntObjectHashMap();
  private FPTreeNode _parent = null;;
  private int _tabpos = -1;

//================
// Constructor(s)
//================
  public FPTreeNode(int lbl, FPTreeNode par, int cnt, int pos) {
    _lbl = lbl;
    _parent = par;
    _cnt = cnt;
    _tabpos = pos;
  }

//================
// Public Methods
//================
  public int getPosition(){
    return _tabpos;
  }

  public int getNumChildren(){
    return _children.size();
  }

  public boolean isRoot(){
    return (_parent == null);
  }

  public void inc(){
    _cnt++;
  }

  public void inc(int i){
    _cnt += i;
  }

  public int getCount(){
    return _cnt;
  }

  public FPTreeNode getParent(){
    return _parent;
  }

  public int getLabel(){
    return _lbl;
  }

  public void addChild(FPTreeNode c){
    _children.put(c.getLabel(), c);
  }

  public FPTreeNode getChild(int lbl){
    return (FPTreeNode)_children.get(lbl);
  }

  public TIntObjectHashMap getChildren(){
    return _children;
  }
}
