package ncsa.d2k.modules.core.discovery.ruleassociation.fpgrowth;

import java.util.*;

import gnu.trove.*;

public class FPPattern implements java.io.Serializable{

//==============
// Data Members
//==============

  private int _support = 0;
  private TIntHashSet _patternElts = new TIntHashSet();

  private static TIntObjectHashMap _eltMap = new TIntObjectHashMap();

  public static void clearElementMapping(){
    _eltMap.clear();
  }

  public static void addElementMapping(int k, String v){
    _eltMap.put(k,v);
  }

  public static String getElementLabel(int i){
    return (String)_eltMap.get(i);
  }

//================
// Constructor(s)
//================
  public FPPattern() {
  }

  public FPPattern(int[] col, int supp){
    _support = supp;
    if (col != null){
      _patternElts.addAll(col);
    }
  }

  public FPPattern(int col, int supp){
    _support = supp;
      _patternElts.add(col);
  }

//================
// Public Methods
//================

  public FPPattern copy(){
    FPPattern newpat = new FPPattern();
    newpat._support = this._support;
    newpat._patternElts.addAll(_patternElts.toArray());
    return newpat;
  }

  public int getSize(){
    return _patternElts.size();
  }

  public TIntIterator getPattern(){
    return _patternElts.iterator();
  }

  public int getSupport(){
    return _support;
  }

  public void setSupport(int s){
    _support = s;
  }

  public void addPatternElt(int fte){
    _patternElts.add(fte);
  }

  public void addPatternElts(int[] col){
    _patternElts.addAll(col);
  }

  public void clearPatterns(){
    _patternElts.clear();
  }
}
