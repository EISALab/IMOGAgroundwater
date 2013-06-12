package ncsa.d2k.modules.core.io.dstp;

import javax.swing.tree.*;
import backend.*;

/**
 *
 * <p>Title: DSTPTreeNodeData</p>
 * <p>Description: This is a support class for ParseDSTPToDBTable</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: NCSA Automated Learning Group</p>
 * @author D. Searsmith
 * @version 1.0
 */
public class DSTPTreeNodeData {

  //==============
  // Data Members
  //==============

  private String _print = null;
  private MetaNode _node = null;

  //================
  // Constructor(s)
  //================

  public DSTPTreeNodeData(MetaNode node, String msg){
    _print = msg;
    _node = node;
  }

  public String toString(){
    return _print;
  }

  public MetaNode getNode(){
    return _node;
  }
}