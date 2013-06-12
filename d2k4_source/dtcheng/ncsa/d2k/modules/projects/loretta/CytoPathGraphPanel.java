package ncsa.d2k.modules.projects.loretta;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import java.util.*;
import java.lang.*;

import luna.*;
import giny.model.*;
import cytoscape.*;
import cytoscape.data.*;
import phoebe.*;
import giny.view.*;

public class CytoPathGraphPanel
    extends JPanel {

  private MutableTable table;
  private int sourceNodeAttr;
  private int targetNodeAttr;
  private int weightAttr;
  private int sourceXAttr = -1;
  private int sourceYAttr = -1;
  private int targetXAttr = -1;
  private int targetYAttr = -1;
  private Network network;
  public PGraphView graphView;
  public Map nodeMap;

  public CytoPathGraphPanel() {
  }

  public void setTable(MutableTable tbl) {
    table = tbl;
    //Need to fix this, so that column is specified in gui
    //sourceNodeAttr - indicates attribute of table that contains node 1
    //targetNodeAttr - indicates attribute of table that contains node connected to node 1
    sourceNodeAttr = 0;
    targetNodeAttr = 1;
    weightAttr = 2;
    for (int i = 0; i < table.getNumColumns(); i++) {
      if (table.getColumnLabel(i).equals("SourceXPosition")) {
        sourceXAttr = i;
      }
      else if (table.getColumnLabel(i).equals("SourceYPosition")) {
        sourceYAttr = i;
      }
      else if (table.getColumnLabel(i).equals("TargetXPosition")) {
        targetXAttr = i;
      }
      else if (table.getColumnLabel(i).equals("TargetYPosition")) {
        targetYAttr = i;
      }
    }
    //Create all nodes in the graph
    RootGraph rgraph = new LunaRootGraph();
    network = new Network(rgraph, new GraphObjAttributes(),
                          new GraphObjAttributes());
    RootGraph graph = network.getRootGraph();
    GraphObjAttributes nodeAttributes = network.getNodeAttributes();
    GraphObjAttributes edgeAttributes = network.getEdgeAttributes();
    nodeMap = new HashMap();

    int id = 0;
    System.out.println("Number of rows in table = " + table.getNumRows());
    for (int i = 0; i < table.getNumRows(); i++) {
      //Create source node
      if (table.getColumnType(sourceNodeAttr) == ColumnTypes.STRING) {
        String name = table.getString(i, sourceNodeAttr);

        Node node = (Node) (nodeMap.get(name));
        if (node == null) {
          node = graph.getNode(graph.createNode());
          node.setIdentifier(name);
          nodeMap.put(name, node);
          nodeAttributes.addNameMapping(name, node);
          if (sourceXAttr != -1) {
            nodeAttributes.set("X", name, table.getDouble(i, sourceXAttr));
          }
          else {
            nodeAttributes.set("X", name, 0.0);
          }
          if (sourceYAttr != -1) {
            nodeAttributes.set("Y", name, table.getDouble(i, sourceYAttr));
          }
          else {
            nodeAttributes.set("Y", name, 0.0);
          }
          id++;
        }
      }
      else if (table.getColumnType(sourceNodeAttr) == ColumnTypes.INTEGER) {
        String name = Integer.toString(table.getInt(i, sourceNodeAttr));
        Node node = (Node) (nodeMap.get(name));
        if (node == null) {
          node = graph.getNode(graph.createNode());
          node.setIdentifier(name);
          nodeMap.put(name, node);
          nodeAttributes.addNameMapping(name, node);
          if (sourceXAttr != -1) {
            nodeAttributes.set("X", name, table.getDouble(i, sourceXAttr));
          }
          else {
            nodeAttributes.set("X", name, 0.0);
          }
          if (sourceYAttr != -1) {
            nodeAttributes.set("Y", name, table.getDouble(i, sourceYAttr));
          }
          else {
            nodeAttributes.set("Y", name, 0.0);
          }
          id++;
        }
      }
      else if (table.getColumnType(sourceNodeAttr) == ColumnTypes.DOUBLE) {
        String name = Double.toString(table.getDouble(i, sourceNodeAttr));
        Node node = (Node) (nodeMap.get(name));
        if (node == null) {
          node = graph.getNode(graph.createNode());
          node.setIdentifier(name);
          nodeMap.put(name, node);
          nodeAttributes.addNameMapping(name, node);
          if (sourceXAttr != -1) {
            nodeAttributes.set("X", name, table.getDouble(i, sourceXAttr));
          }
          else {
            nodeAttributes.set("X", name, 0.0);
          }
          if (sourceYAttr != -1) {
            nodeAttributes.set("Y", name, table.getDouble(i, sourceYAttr));
          }
          else {
            nodeAttributes.set("Y", name, 0.0);
          }
          id++;
        }
      }
      else {
        System.out.println("The datatype for Attribute " +
                           table.getColumnLabel(sourceNodeAttr) +
                           " is not currently supported.");
      }

      //Create target nodes
      if (table.getColumnType(targetNodeAttr) == ColumnTypes.STRING) {
        String name = table.getString(i, targetNodeAttr);
        Node node = (Node) (nodeMap.get(name));
        if (node == null) {
          node = graph.getNode(graph.createNode());
          node.setIdentifier(name);
          nodeMap.put(name, node);
          nodeAttributes.addNameMapping(name, node);
          if (targetXAttr != -1) {
            nodeAttributes.set("X", name, table.getDouble(i, targetXAttr));
          }
          else {
            nodeAttributes.set("X", name, 0.0);
          }
          if (targetYAttr != -1) {
            nodeAttributes.set("Y", name, table.getDouble(i, targetYAttr));
          }
          else {
            nodeAttributes.set("Y", name, 0.0);
          }
          id++;
        }
      }
      else if (table.getColumnType(targetNodeAttr) == ColumnTypes.INTEGER) {
        String name = Integer.toString(table.getInt(i, targetNodeAttr));
        Node node = (Node) (nodeMap.get(name));
        if (node == null) {
          node = graph.getNode(graph.createNode());
          node.setIdentifier(name);
          nodeMap.put(name, node);
          nodeAttributes.addNameMapping(name, node);
          if (targetXAttr != -1) {
            nodeAttributes.set("X", name, table.getDouble(i, targetXAttr));
          }
          else {
            nodeAttributes.set("X", name, 0.0);
          }
          if (targetYAttr != -1) {
            nodeAttributes.set("Y", name, table.getDouble(i, targetYAttr));
          }
          else {
            nodeAttributes.set("Y", name, 0.0);
          }
          id++;
        }
      }
      else if (table.getColumnType(targetNodeAttr) == ColumnTypes.DOUBLE) {
        String name = Double.toString(table.getDouble(i, targetNodeAttr));
        Node node = (Node) (nodeMap.get(name));
        if (node == null) {
          node = graph.getNode(graph.createNode());
          node.setIdentifier(name);
          nodeMap.put(name, node);
          nodeAttributes.addNameMapping(name, node);
          if (targetXAttr != -1) {
            nodeAttributes.set("X", name, table.getDouble(i, targetXAttr));
          }
          else {
            nodeAttributes.set("X", name, 0.0);
          }
          if (targetYAttr != -1) {
            nodeAttributes.set("Y", name, table.getDouble(i, targetYAttr));
          }
          else {
            nodeAttributes.set("Y", name, 0.0);
          }
          id++;
        }
      }
      else {
        System.out.println("The datatype for Attribute " +
                           table.getColumnLabel(targetNodeAttr) +
                           " is not currently supported.");
      }
    } //end of for (int i = 0; i < table.getNumRows(); i++)

    System.out.println("Number of Nodes = " + nodeMap.size());
    //Create Edges
    int numEdges = 0;
    int sourceNode;
    int targetNode;
    for (int i = 0; i < table.getNumRows(); i++) {
      //not generic if data is not string
      String eName = table.getString(i, sourceNodeAttr) + "->" +
          table.getString(i, targetNodeAttr);
      Node sNode = (Node) (nodeMap.get(table.getString(i, sourceNodeAttr)));
      Node tNode = (Node) (nodeMap.get(table.getString(i, targetNodeAttr)));
      if (graph.getEdgeCount(sNode, tNode, true) == 0) {
        int e = graph.createEdge(sNode, tNode);
        edgeAttributes.addNameMapping(eName, new Integer(e));
        numEdges++;
      }
      else {
        System.out.println("Duplicate Edge: " + eName);
      }
    }
    System.out.println("Number of Edges = " + numEdges);
    GraphPerspective graphPerspective =
        graph.createGraphPerspective(graph.getNodeIndicesArray(),
                                     graph.getEdgeIndicesArray());
    network.setGraphPerspective(graphPerspective);

// make everything visible
    setSize(300, 300);
    setVisible(true);
  }

  public void writeTable() {
    System.out.println("in writeTable");
    int numrows = table.getNumRows();
    int numcols = table.getNumColumns();
    table.addColumn(new DoubleColumn(numrows));
    table.setColumnLabel("SourceXPosition", numcols);
    table.addColumn(new DoubleColumn(numrows));
    table.setColumnLabel("SourceYPosition", numcols + 1);
    table.addColumn(new DoubleColumn(numrows));
    table.setColumnLabel("TargetXPosition", numcols + 2);
    table.addColumn(new DoubleColumn(numrows));
    table.setColumnLabel("TargetYPosition", numcols + 3);
    for (int i = 0; i < numrows; i++) {
      //next line not generic... if data is not a string
      //Set source node info
      String nodeName = table.getString(i, sourceNodeAttr);
      Node node = (Node) nodeMap.get(nodeName);
      NodeView nview = (NodeView) graphView.getNodeView(node);
      double x = nview.getXPosition();
      double y = nview.getYPosition();
      table.setDouble(x, i, numcols);
      table.setDouble(y, i, numcols + 1);
      //Set target node info
      nodeName = table.getString(i, targetNodeAttr);
      node = (Node) nodeMap.get(nodeName);
      nview = (NodeView) graphView.getNodeView(node);
      x = nview.getXPosition();
      y = nview.getYPosition();
//      System.out.println(nview.getUnselectedPaint());
//      System.out.println(nview.getUnselectedPaint());
      table.setDouble(x, i, numcols + 2);
      table.setDouble(y, i, numcols + 3);
//      System.out.println("NodeName for "+i+" = "+nodeName+" x= "+x+" y= "+y);
    }
  }

  public void setGraphView(PGraphView view) {
    graphView = view;
  }

  public Network getNetwork() {
    return network;
  }
}
