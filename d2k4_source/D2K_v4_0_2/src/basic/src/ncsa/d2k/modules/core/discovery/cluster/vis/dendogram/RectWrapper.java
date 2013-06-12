package ncsa.d2k.modules.core.discovery.cluster.vis.dendogram;

//==============
// Java Imports

import java.util.*;
import java.awt.*;
import java.io.*;

//===============
// Other Imports

import ncsa.d2k.modules.core.discovery.cluster.*;
import ncsa.d2k.modules.core.discovery.cluster.util.*;

/**
 *
 * <p>Title: RectWrapper</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: NCSA Automated Learning Group</p>
 * @author D. Searsmith
 * @version 1.0
 */
public class RectWrapper {

  private TableCluster m_cluster = null;
  private Color m_color = null;
  private Rectangle m_rect = null;
  private String m_label = "";
  private ArrayList m_leaves = new ArrayList();

  public RectWrapper(Rectangle rect, TableCluster c, Color col){
    m_cluster = c;
    m_rect = rect;
    m_color = col;

    //compute label
    m_label = c.getClusterLabel() + "";
    //m_label = c.generateTextLabel() + "";
  }

  public void addLeaves(ArrayList alist){
    m_leaves.addAll(alist);
  }

  public void addLeaf(TableCluster c){
    m_leaves.add(c);
  }

  public ArrayList getLeaves(){
    return m_leaves;
  }

  public String getLabel(){
    return m_label;
  }

  public void setColor(Color c){
    m_color = c;
  }

  public Color getColor(){
    return m_color;
  }

  public TableCluster getCluster(){
    return m_cluster;
  }

  public Rectangle getRect(){
    return m_rect;
  }
}


