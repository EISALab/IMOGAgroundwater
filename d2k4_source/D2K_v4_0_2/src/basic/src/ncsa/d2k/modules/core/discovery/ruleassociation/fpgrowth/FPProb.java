package ncsa.d2k.modules.core.discovery.ruleassociation.fpgrowth;

//==============
// Java Imports
//==============
import java.util.*;
import java.io.*;

//===============
// Other Imports
//===============
import ncsa.d2k.modules.core.datatype.table.sparse.*;

public class FPProb implements Serializable{
  //==============
  // Data Members
  //==============

  private int[] _alpha;
  private FPSparse _tab = null;
  private int _support = 1;

//================
// Constructor(s)
//================


  public FPProb(FPSparse tab, int[] alpha, int sup) {
    _alpha = alpha;
    _tab = tab;
    _support = sup;
  }

//================
// Public Methods
//================

  public int getSupport(){
    return _support;
  }

  public int[] getAlpha(){
    return _alpha;
  }

  public FPSparse getTable(){
    return _tab;
  }

  //=================
  // Inner Class(es)
  //=================
}
