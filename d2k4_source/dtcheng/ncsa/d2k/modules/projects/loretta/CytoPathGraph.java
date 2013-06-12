package ncsa.d2k.modules.projects.loretta;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.userviews.swing.*;
import ncsa.d2k.core.modules.UserView;
import ncsa.d2k.modules.core.io.file.output.*;
import ncsa.gui.Constrain;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Hashtable;
import java.util.*;
import java.io.*;
import java.awt.image.*;
import ncsa.d2k.modules.projects.loretta.*;
import cytoscape.*;
import cytoscape.data.*;
import cytoscape.data.readers.*;
import cytoscape.graphutil.*;
//import giny.*;
import giny.filter.*;
import giny.model.*;
import giny.util.*;
import giny.view.*;
import phoebe.*;
import phoebe.util.*;
import phoebe.event.*;
import coltginy.*;
import edu.umd.cs.piccolo.*;
import edu.umd.cs.piccolox.*;
import edu.umd.cs.piccolox.util.*;
import edu.umd.cs.piccolox.handles.*;
import edu.umd.cs.piccolo.event.*;
import edu.umd.cs.piccolo.activities.*;
import edu.umd.cs.piccolo.nodes.*;
import edu.umd.cs.piccolo.util.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class CytoPathGraph
    extends UIModule {

  private static final int MAX_MENU_ITEMS = 15;
  private static final String MORE = "More..";
  private static final String HIGH = "High";
  private static final String LOW = "Low";
  private static final Color defaultHighColor = Color.red; //colors[0];
  private static final Color defaultLowColor = Color.yellow; //colors[colors.length-1];
  private static final Color defaultNodeColor = Color.blue; //colors[colors.length-1];
  private static final Color[] colors = {
      new Color(71, 74, 98),
      new Color(191, 191, 115), new Color(111, 142, 116),
      new Color(178, 198, 181), new Color(153, 185, 216),
      new Color(96, 93, 71), new Color(146, 205, 163),
      new Color(203, 84, 84), new Color(217, 183, 170),
      new Color(140, 54, 57), new Color(203, 136, 76)};

  private CytoPathGraphPanel panel;
  /**
    Return the name of this module.
    @return The name of this module.
   */
  public String getModuleName() {
    return "Path Graph";
  }

  public String[] getInputTypes() {
    String[] types = {
        "ncsa.d2k.modules.core.datatype.table.Table"};
    return types;
  }

  protected String[] getFieldNameMapping() {
    return null;
  }

  public String[] getOutputTypes() {
    return null;
  }

  public String getModuleInfo() {
    String info = "This module will take a table and create a path graph.";
    return info;
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "Interaction Table.";
//      case 1:
//        return "Expression Data Table.";
      default:
        return "No such input";
    }
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Table.";
      default:
        return "No such input";
    }
  }

  public String getOutputInfo(int i) {
    return null;
  }

  public String getOutputName(int i) {
    return null;
  }

  protected UserView createUserView() {
    return new CytoPathGraphView();
  }

  class CytoPathGraphView
      extends JUserPane
      implements ActionListener {

    private CytoPathGraph module;
    private MutableTable table;
    private JButton abort;
    private JButton done;

    private Network network;
    private PGraphView graphView;

    private JMenuBar menuBar;
    protected JMenu opsMenu, vizMenu, selectMenu, layoutMenu;
    protected JMenu fileMenu, editMenu, loadSubMenu, saveSubMenu;
    protected JMenuItem save;
    private File currentDirectory;
    private ColorInterpolator colorInterpolatorPositive;
    private ColorInterpolator colorInterpolatorNegative;
    private Component display;

    public void initView(ViewModule v) {
      currentDirectory = new File(System.getProperty("user.dir"));
      module = (CytoPathGraph) v;
      abort = new JButton("Abort");
      done = new JButton("Done");
      abort.addActionListener(this);
      done.addActionListener(this);
    }

    public Object getMenu() {
      return menuBar;
    }

    public void setInput(Object o, int id) {
      if (id == 0) {
        table = (MutableTable) o;
        this.removeAll();
        addComponents();
      }
    }

    public Dimension getPreferredSize() {
      return new Dimension(400, 300);
    }

    private void addComponents() {
      JPanel back = new JPanel();
      back.setLayout(new BorderLayout());
      JPanel buttons = new JPanel();
      buttons.add(abort);
      buttons.add(done);

      menuBar = createMenuBar();
      colorInterpolatorPositive = new ColorInterpolator(55, Color.green,
          0, Color.white,
          100, Color.green.darker());
      colorInterpolatorNegative = new ColorInterpolator(55, Color.red,
          0, Color.black,
          100, Color.red.darker());

      panel = new CytoPathGraphPanel();
      panel.setTable(table);
//      panel.createGraph();
      //panel.initialize();
      network = panel.getNetwork();
      graphView = new PGraphView(network.getGraphPerspective());
      setGraphView(graphView);
      panel.setGraphView(graphView);
      graphView.fitContent();
      graphView.setZoom(graphView.getZoom() * 0.9);
      back.add(graphView.getComponent(), BorderLayout.CENTER);
      redrawGraph();
      //setVisible(true);

      System.out.println("called initialize");
      this.add(back, BorderLayout.CENTER);
      this.add(buttons, BorderLayout.SOUTH);
    }

    /**
     * Simplest method to redraw the graph in its view.
     */
    public void redrawGraph() {
      /* only redraw if the window is actually displayed */
      //if (this!= null) {
      display = graphView.getComponent();
      display.setBackground(Color.WHITE);
      graphView.updateView(); //forces the view to update it's contents
      //display.repaint();
      //}
    }

    protected JMenuBar createMenuBar() {
      menuBar = new JMenuBar();
      fileMenu = new JMenu("File");

      save = new JMenuItem("Save...");
      save.addActionListener(this);
      fileMenu.add(save);

      loadSubMenu = new JMenu("Load");
      fileMenu.add(loadSubMenu);
      JMenuItem mi = loadSubMenu.add(new LoadGMLFileAction());
      mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G,
                                               ActionEvent.CTRL_MASK));
//      mi = loadSubMenu.add(new LoadInteractionFileAction());
//      mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I,
//                                               ActionEvent.CTRL_MASK));
//      mi = loadSubMenu.add(new LoadExpressionMatrixAction());
//      mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,
//                                               ActionEvent.CTRL_MASK));

      //printSubMenu = new JMenu ("Print");
      //fileMenu.add (saveSubMenu);

      mi = fileMenu.add(new PrintAction());
      mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,
                                               ActionEvent.CTRL_MASK));

      menuBar.add(fileMenu);

      editMenu = new JMenu("Edit");
      menuBar.add(editMenu);
      mi = editMenu.add(new ZoomToFitAction());
      mi = editMenu.add(new SetPositveColorAction());
      mi = editMenu.add(new SetNegativeColorAction());

      this.selectMenu = new JMenu("Edit Selected");
      menuBar.add(selectMenu);
      //zoom selected area
      //JMenu zoom = new JMenu("Zoom selected area");       selectMenu.add (zoom);
      mi = selectMenu.add(new ZoomSelectedAction());
      mi = selectMenu.add(new PaintSelectedNodesAction());
      mi = selectMenu.add(new PaintSelectedEdgesAction());
      JMenu showSelectedNodes = new JMenu("Show Selected Nodes");
      selectMenu.add(showSelectedNodes);

      mi = showSelectedNodes.add(new StarPlotAction());
      mi = showSelectedNodes.add(new RadarAction());
      mi = showSelectedNodes.add(new GridAction());
      mi = showSelectedNodes.add(new PetalAction());

      mi = showSelectedNodes.add(new EllipseAction());
      mi = showSelectedNodes.add(new DiamondAction());
      mi = showSelectedNodes.add(new ParallelogramAction());

      mi = showSelectedNodes.add(new ExpressionNodeAction());

      mi = selectMenu.add(new NewWindowAction());
      //JMenu hideSelected = new JMenu("hide Selected Nodes"); selectMenu.add (hideSelected);
      mi = selectMenu.add(new HideSelectedAction());
      mi = selectMenu.add(new UnHideAction());

      ButtonGroup layoutGroup = new ButtonGroup();
      layoutMenu = new JMenu("Layout");
      layoutMenu.setToolTipText("Apply new layout algorithm to graph");
      menuBar.add(layoutMenu);

      String defaultLayoutStrategy = "hierarchical";
//      String defaultLayoutStrategy = "Spring Embedded";

      JRadioButtonMenuItem layoutButton;
      layoutButton = new JRadioButtonMenuItem("Spring Enbedded");
      layoutGroup.add(layoutButton);
      layoutMenu.add(layoutButton);
      layoutButton.addActionListener(new SpringLayoutAction());
      if (defaultLayoutStrategy.equals("circular")) {
        layoutButton.setSelected(true);
      }
      layoutButton = new JRadioButtonMenuItem("Circular");
      layoutGroup.add(layoutButton);
      layoutMenu.add(layoutButton);
      layoutButton = new JRadioButtonMenuItem("Hierarchicial");
      layoutGroup.add(layoutButton);
      layoutMenu.add(layoutButton);
      //layoutButton.addActionListener(new HierarchicalLayoutAction ());
      if (defaultLayoutStrategy.equals("hierarchical")) {
        layoutButton.setSelected(true);
      }
      layoutButton = new JRadioButtonMenuItem("Organic");
      layoutGroup.add(layoutButton);
      layoutMenu.add(layoutButton);
      if (defaultLayoutStrategy.equals("organic")) {
        layoutButton.setSelected(true);
      }
      //layoutButton.addActionListener(new OrganicLayoutAction ());
      layoutButton = new JRadioButtonMenuItem("Embedded");
      layoutGroup.add(layoutButton);
      layoutMenu.add(layoutButton);
      if (defaultLayoutStrategy.equals("embedded")) {
        layoutButton.setSelected(true);
      }
      //layoutButton.addActionListener(new EmbeddedLayoutAction ());
      layoutButton = new JRadioButtonMenuItem("Random");
      layoutGroup.add(layoutButton);
      layoutMenu.add(layoutButton);
      if (defaultLayoutStrategy.equals("random")) {
        layoutButton.setSelected(true);
      }
      // layoutButton.addActionListener(new RandomLayoutAction ());

      layoutMenu.addSeparator();

      layoutMenu.add(new JMenuItem(new AbstractAction("Spring Embedded Layout") {
        public void actionPerformed(ActionEvent e) {
          // Do this in the GUI Event Dispatch thread...
          SwingUtilities.invokeLater(new Runnable() {
            public void run() {
              SpringEmbeddedLayouter lay = new SpringEmbeddedLayouter(graphView);
              lay.doLayout();
            }
          });
        }
      }));

      layoutMenu.addSeparator();

      vizMenu = new JMenu("Visualization"); // always create the viz menu
      menuBar.add(vizMenu);

      opsMenu = new JMenu("PlugIns"); // always create the plugins menu
      // fileMenu.add (new DisplayDebugLog ());
      menuBar.add(opsMenu);

      //this is removed so that it can happen later.
      //JarLoaderUI jlu = new JarLoaderUI(CytoscapeWindow.this,loadSubMenu);

      return menuBar;

    } // createMenuBar

    public void actionPerformed(ActionEvent e) {
      Object src = e.getSource();
      if (src == abort) {
        module.viewCancel();
      }
      else if (src == done) {
        module.viewDone("Done");
        this.removeAll();
      }
      else if (src == save) {
        saveTable();
      }
    }

    protected class SpringLayoutAction
        extends AbstractAction {
      SpringLayoutAction() {
        super("Layout graph");
      }

      public void actionPerformed(ActionEvent e) {

        SpringEmbeddedLayouter sel = new SpringEmbeddedLayouter(graphView);
        sel.doLayout();

      } // actionPerformed

    } // inner class PrintAction

    protected class PrintAction
        extends AbstractAction {
      PrintAction() {
        super("Print");
      }

      public void actionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            graphView.getCanvas().getLayer().print();

          } //run
        }); //new Runnable

      } // actionPerformed

    } // inner class PrintAction

    protected class PaintSelectedNodesAction
        extends AbstractAction {
      PaintSelectedNodesAction() {
        super("Paint Selected nodes");
      }

      public void actionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            JColorChooser color = new JColorChooser();
            java.awt.Color custom = color.showDialog(graphView.getComponent(),
                "Choose a Node Color", Color.pink);

            java.util.List list = graphView.getSelectedNodes();
            Iterator i = list.iterator();
            while (i.hasNext()) {
              NodeView nview = (NodeView) i.next();
              //NodeView nview = (NodeView)graphView.getNodeView(next.getRootGraphIndex());
              nview.setUnselectedPaint(custom);
              nview.setSelectedPaint(custom.darker());
            } //while
          } //run
        }); //new Runnable

      } // actionPerformed

    } // inner class PaintSelectedAction

    protected class PaintSelectedEdgesAction
        extends AbstractAction {
      PaintSelectedEdgesAction() {
        super("Paint Selected edges");
      }

      public void actionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            JColorChooser color = new JColorChooser();
            java.awt.Color custom = color.showDialog(graphView.getComponent(),
                "Choose a Color for selected edges", Color.blue);

            java.util.List list = graphView.getSelectedEdges();
            Iterator i = list.iterator();
            while (i.hasNext()) {
              EdgeView eview = (EdgeView) i.next();
              //EdgeView eview = (EdgeView)graphView.getEdgeView(next.getRootGraphIndex());
              eview.setUnselectedPaint(custom);
              eview.setSelectedPaint(custom.darker().darker());
            }
          }
        });

      } // actionPerformed

    } // inner class PaintSelectedAction

    protected class ZoomSelectedAction
        extends AbstractAction {
      ZoomSelectedAction() {
        super("Zoom selected area");
      }

      public void actionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {

            PBounds zoomToBounds;

            double bigX;
            double bigY;
            double smallX;
            double smallY;
            double W;
            double H;

            java.util.List list = graphView.getSelectedNodes();
            Iterator i = list.iterator();

            PNode nv = (PNode) i.next();
            zoomToBounds = nv.getGlobalFullBounds();

            bigX = zoomToBounds.getX();
            smallX = bigX;
            bigY = zoomToBounds.getY();
            smallY = bigY;

            while (i.hasNext()) {
              nv = (PNode) i.next();
              zoomToBounds = nv.getGlobalFullBounds();

              if (zoomToBounds.getX() > bigX) {
                bigX = zoomToBounds.getX();
              }
              else if (zoomToBounds.getX() < smallX) {
                smallX = zoomToBounds.getX();
              }

              if (zoomToBounds.getY() > bigY) {
                bigY = zoomToBounds.getY();
              }
              else if (zoomToBounds.getY() < smallY) {
                smallY = zoomToBounds.getY();
              }
            }

            zoomToBounds = new PBounds(smallX, smallY,
                                       (bigX - smallX + zoomToBounds.getWidth()),
                                       (bigY - smallY + zoomToBounds.getHeight()));

            // System.out.println("Making new bounds of X: "+X+" Y: "+Y+" W: "+W+" H: "+H);
            //System.out.println("The bounds are:      X: "+zoomToBounds.getX()+" Y: "+zoomToBounds.getY()+" W: "+zoomToBounds.getWidth()+" H: "+zoomToBounds.getHeight());

            PTransformActivity activity = graphView.getCanvas().getCamera().
                animateViewToCenterBounds(zoomToBounds, true, 500);

          } //run
        }); //new Runnable

      } // actionPerformed

    } // inner class ZoomAction

    protected class ZoomToFitAction
        extends AbstractAction {
      ZoomToFitAction() {
        super("Zoom to fit area");
      }

      public void actionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {

            //PTransformActivity activity =  graphView.getCanvas().getCamera().animateViewToCenterBounds( n.getGlobalFullBounds(), true, 500 );
            graphView.getCanvas().getCamera().animateViewToCenterBounds(
                graphView.getCanvas().getLayer().getGlobalFullBounds(), true,
                500l);
          } //run
        }); //new Runnable

      } // actionPerformed

    } // inner class ZoomtofitAction

    protected class SetPositveColorAction
        extends AbstractAction {

      SetPositveColorAction() {
        super("Set the POSITIVE color");
      }

      public void actionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {

            JFrame d = new JFrame("Positive expression color");
            d.getContentPane().add(colorInterpolatorPositive.createPanel());
            d.pack();
            d.show();

          } //run
        }); //new Runnable

      } // actionPerformed

    } // inner class

    protected class SetNegativeColorAction
        extends AbstractAction {

      SetNegativeColorAction() {
        super("Set the NEGATIVE color");
      }

      public void actionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {

            JFrame d = new JFrame("Negative expression color");
            d.getContentPane().add(colorInterpolatorNegative.createPanel());
            d.pack();
            d.show();

          } //run
        }); //new Runnable

      } // actionPerformed

    } // inner class

    protected class HideSelectedAction
        extends AbstractAction {
      HideSelectedAction() {
        super("Hide selected nodes");
      }

      public void actionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {

            java.util.List list = graphView.getSelectedNodes();
            Iterator i = list.iterator();
            while (i.hasNext()) {
              NodeView nview = (NodeView) i.next();
              Node n = nview.getNode();
              //ColtGraphPerspective gp = (ColtGraphPerspective)graphView.getGraphPerspective();
              //gp.hideNode(n);
              ( (PNode) nview).setVisible(false);
              //graphView.hideGraphObject( nview );
              int[] na = graphView.getGraphPerspective().neighborsArray(nview.
                  getGraphPerspectiveIndex());
              for (int i2 = 0; i2 < na.length; ++i2) {
                int[] edges = graphView.
                    getGraphPerspective().
                    getEdgeIndicesArray(nview.getGraphPerspectiveIndex(), na[i2], true, true);
                //if( edges != null )
                //System.out.println( "There are: "+edges.length+" edge between "+nview.getGraphPerspectiveIndex()+" and "+na[i2] );
                for (int j = 0; j < edges.length; ++j) {
                  PEdgeView ev = (PEdgeView) graphView.getEdgeView(edges[j]);
                  ev.setVisible(false);
                  //graphView.hideGraphObject( ev );
                }
              }

            } //while

          } //run
        }); //new Runnable

      } // actionPerformed

    } // inner class HideSelectedAction

    protected class UnHideAction
        extends AbstractAction {
      UnHideAction() {
        super("Show Hidden nodes");
      }

      public void actionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {

            java.util.List list = graphView.getNodeViewsList();
            Iterator i = list.iterator();
            while (i.hasNext()) {
              NodeView nview = (NodeView) i.next();
              Node n = nview.getNode();
              //ColtGraphPerspective gp = (ColtGraphPerspective)graphView.getGraphPerspective();
              //gp.hideNode(n);
              ( (PNode) nview).setVisible(true);
              int[] na = graphView.getGraphPerspective().neighborsArray(nview.
                  getGraphPerspectiveIndex());
              for (int i2 = 0; i2 < na.length; ++i2) {
                int[] edges = graphView.getGraphPerspective().
                    getEdgeIndicesArray(nview.getGraphPerspectiveIndex(), na[i2], true);
                if (edges != null) {
                  for (int j = 0; j < edges.length; ++j) {
                    EdgeView ev = graphView.getEdgeView(edges[j]);
                    if (ev instanceof PNode) {
                      ( (PNode) ev).setVisible(true);
                    }
                    else {
                      //	System.out.println( "Ah" +ev.getClass().toString());
                    }
                  }
                }
              }

            } //while

          } //run
        }); //new Runnable

      } // actionPerformed

    } // inner class UnHideSelectedAction

    protected class RadarAction
        extends AbstractAction {
      RadarAction() {
        super("As Radars...");
      }

      public void actionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            java.util.List list = graphView.getSelectedNodes();
            Iterator i = list.iterator();
            while (i.hasNext()) {
              NodeView nview = (NodeView) i.next();
              int index = nview.getNode().getRootGraphIndex();
              //NodeView neibview = (NodeView)v.getNodeView(index);
              PNode node = (PNode) graphView.addNodeView(
                  "cytoscape.graphutil.RadarNode", index);
            }

          }
        });

      } // actionPerformed

    } // inner class RadarAction

    protected class StarPlotAction
        extends AbstractAction {
      StarPlotAction() {
        super("As StarPlots...");
      }

      public void actionPerformed(ActionEvent e) {

        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            java.util.List list = graphView.getSelectedNodes();
            Iterator i = list.iterator();
            while (i.hasNext()) {
              NodeView nview = (NodeView) i.next();
              int index = nview.getNode().getRootGraphIndex();
              //NodeView neibview = (NodeView)v.getNodeView(index);
              PNode node = (PNode) graphView.addNodeView(
                  "cytoscape.graphutil.ClipRadarNode", index);
            }

          }
        });

      } // actionPerformed

    } // inner class StarPlotAction

    protected class ExpressionNodeAction
        extends AbstractAction {
      ExpressionNodeAction() {
        super("With Expression Data");
      }

      public void actionPerformed(ActionEvent e) {

        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            //   graphView.setBackgroundPaint( Color.black );
            java.util.List list = graphView.getSelectedNodes();
            Iterator i = list.iterator();
            while (i.hasNext()) {
              NodeView nview = (NodeView) i.next();
              int index = nview.getNode().getRootGraphIndex();
              //NodeView neibview = (NodeView)v.getNodeView(index);
              //ClipRadarNode node = (ClipRadarNode) graphView.addNodeView( "cytoscape.graphutil.ClipRadarNode", index);
              Vector data = new Vector();
              Vector lamda = new Vector();

              String[] conds = network.getExpressionData().getConditionNames();
              for (int j = 0; j < conds.length; ++j) {

                mRNAMeasurement mrna = network.getExpressionData().
                    getMeasurement(nview.getLabel(), conds[j]);
                if (mrna != null) {
                  data.add(new Double(mrna.getRatio()));
                  lamda.add(new Double(mrna.getSignificance()));
                }
              }
              if (data.size() != 0 && lamda.size() != 0) {
                //node.addExpressionData( data, lamda );

                ClipRadarNode node = new ClipRadarNode(index, graphView, data,
                    lamda, conds, colorInterpolatorPositive,
                    colorInterpolatorNegative);
                try {
                  graphView.addNodeView(index, node);
                }
                catch (Exception e) {
                  e.printStackTrace();
                  System.err.println("Node: " + node.getLabel() +
                                     " is having trouble getting updated...");
                }

              }
            }

          }
        });

      } // actionPerformed

    } // inner class StarPlotAction

    protected class GridAction
        extends AbstractAction {
      GridAction() {
        super("As Grids...");
      }

      public void actionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            java.util.List list = graphView.getSelectedNodes();
            Iterator i = list.iterator();
            while (i.hasNext()) {
              NodeView nview = (NodeView) i.next();
              int index = nview.getNode().getRootGraphIndex();
              //NodeView neibview = (NodeView)v.getNodeView(index);
              PNode node = (PNode) graphView.addNodeView(
                  "cytoscape.graphutil.GridNode", index);
            }

          }
        });

      } // actionPerformed

    } // inner class GriudAction

    protected class PetalAction
        extends AbstractAction {
      PetalAction() {
        super("As Petals...");
      }

      public void actionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            java.util.List list = graphView.getSelectedNodes();
            Iterator i = list.iterator();
            while (i.hasNext()) {
              NodeView nview = (NodeView) i.next();
              int index = nview.getNode().getRootGraphIndex();
              //NodeView neibview = (NodeView)v.getNodeView(index);
              PNode node = (PNode) graphView.addNodeView(
                  "cytoscape.graphutil.PetalNode", index);
            }

          }
        });

      } // actionPerformed

    } // inner class PetalAction

    protected class EllipseAction
        extends AbstractAction {
      EllipseAction() {
        super("As ellipses...");
      }

      public void actionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            java.util.List list = graphView.getSelectedNodes();
            Iterator i = list.iterator();
            while (i.hasNext()) {
              NodeView nview = (NodeView) i.next();
              //int index = nview.getNode().getRootGraphIndex();
              nview.setShape(NodeView.ELLIPSE);
            }

          }
        });

      } // actionPerformed

    } // inner class ParallelogramAction

    protected class DiamondAction
        extends AbstractAction {
      DiamondAction() {
        super("As diamonds...");
      }

      public void actionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            java.util.List list = graphView.getSelectedNodes();
            Iterator i = list.iterator();
            while (i.hasNext()) {
              NodeView nview = (NodeView) i.next();
              //int index = nview.getNode().getRootGraphIndex();
              nview.setShape(PNodeView.DIAMOND);
            }

          }
        });

      } // actionPerformed

    } // inner class ParallelogramAction

    protected class ParallelogramAction
        extends AbstractAction {
      ParallelogramAction() {
        super("As parallelograms...");
      }

      public void actionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            java.util.List list = graphView.getSelectedNodes();
            Iterator i = list.iterator();
            while (i.hasNext()) {
              NodeView nview = (NodeView) i.next();
              //int index = nview.getNode().getRootGraphIndex();
              nview.setShape(PNodeView.PARALELLOGRAM);
            }

          }
        });

      } // actionPerformed

    } // inner class ParallelogramAction

    protected class NewWindowAction
        extends AbstractAction {
      NewWindowAction() {
        super("To a new Window");
      }

      public void actionPerformed(ActionEvent e) {
        int[] nodes = graphView.getSelectedNodeIndices();
        //int[] edges = graphView.getSelectedEdgeIndices();
        GraphPerspective subGraph = graphView.getGraphPerspective().
            createGraphPerspective(nodes); //, edges);
        PGraphView subView = new PGraphView(subGraph);
// LA        setGraphView(subView);
        show();

      } // actionPerformed

    } // inner class NewWindowAction

    protected class LoadGMLFileAction
        extends AbstractAction {
      LoadGMLFileAction() {
        super("GML...");
      }

      public void actionPerformed(ActionEvent e) {

      } // actionPerformed

    } // inner class LoadAction

    /* LA
        protected class LoadInteractionFileAction
            extends AbstractAction {
          LoadInteractionFileAction() {
            super("Interaction...");
          }
          public void actionPerformed(ActionEvent e) {
            JFileChooser chooser = new JFileChooser(currentDirectory);
            //CyFileFilter filter = new CyFileFilter();
            //filter.addExtension("gml");
            //filter.setDescription("GML files");
            //chooser.setFileFilter(filter);
            //chooser.addChoosableFileFilter(filter);
            if (chooser.showOpenDialog(mainFrame) == chooser.APPROVE_OPTION) {
              currentDirectory = chooser.getCurrentDirectory();
              String name = chooser.getSelectedFile().toString();
              loadInteraction(name);
            } // if
          } // actionPerformed
        } // inner class LoadAction
     */

    //------------------------------------------------------------------------------

    /*LA
        protected class LoadExpressionMatrixAction
            extends AbstractAction {
          LoadExpressionMatrixAction() {
            super("Expression...");
          }
          public void actionPerformed(ActionEvent e) {
            JFileChooser chooser = new JFileChooser(currentDirectory);
            //CyFileFilter filter = new CyFileFilter();
            //filter.addExtension("sif");
            //filter.setDescription("Interaction files");
            //chooser.setFileFilter(filter);
            //chooser.addChoosableFileFilter(filter);
            if (chooser.showOpenDialog(mainFrame) == chooser.APPROVE_OPTION) {
              currentDirectory = chooser.getCurrentDirectory();
              String name = chooser.getSelectedFile().toString();
              loadExpressionMatrix(name);
            } // if
          } // actionPerformed
        } // inner class LoadAction
     */

    /*-----------------------------------------------------------------------------------------------------------------------------------*/
    /**
     * Returns the graph view object.
     */
    public PGraphView getGraphView() {
      return graphView;
    }

    public void setGraphView(PGraphView newView) {
      if (newView == null) {
        System.out.println("graph view is null!");
        return;
      }
      if (newView.getRootGraph() == network.getRootGraph()) {
        graphView = newView;
        //set node labels
        java.util.List nodes = graphView.getNodeViewsList();
        Paint DEFAULT_PAINT = Color.lightGray;
        for (Iterator i = nodes.iterator(); i.hasNext(); ) {
          PNodeView nv = (PNodeView) i.next();
          String label = nv.getNode().getIdentifier();
          //System.out.println("Setting label " + label);
          nv.setLabel(label);
          nv.setShape(NodeView.ELLIPSE);

          nv.setUnselectedPaint(DEFAULT_PAINT);

          nv.setSelectedPaint( ( (Color) nv.getUnselectedPaint()).darker());

          double x = ( (Double) network.getNodeAttributes().get("X", label)).
              doubleValue();
          double y = ( (Double) network.getNodeAttributes().get("Y", label)).
              doubleValue();

          if (x != 0 && y != 0) {
            nv.setOffset(x, y);
          }
          // nv.setXPosition( ( ( Double )network.getNodeAttributes().get( "X", label ) ).doubleValue() );
          //nv.setYPosition( ( ( Double )network.getNodeAttributes().get( "Y", label ) ).doubleValue() );
          //System.out.println( "Setting: "+label+" to "+nv.getXPosition()+" "+nv.getYPosition() );
        }

        //edges
        java.util.List edges = graphView.getEdgeViewsList();
        for (Iterator i = edges.iterator(); i.hasNext(); ) {
          PEdgeView ev = (PEdgeView) i.next();
          ev.setUnselectedPaint(Color.blue);
          ev.setTargetEdgeEnd(EdgeView.ARROW_END);
          ev.setTargetEdgeEndPaint(Color.CYAN);
          ev.setSourceEdgeEndPaint(Color.CYAN);
          //ev.setLineType(EdgeView.CURVED_LINES);
          ev.setStroke(new BasicStroke(2.4f));
          //ev.updateEdgeView();
        }

        // Add some Node Context Menu Items
        graphView.addContextMethod("class edu.umd.cs.piccolo.PNode",
                                   "cytoscape.graphutil.NodeAction",
                                   "openSGD",
                                   "Color This Node White");

        graphView.addContextMethod("class phoebe.PNodeView",
                                   "cytoscape.graphutil.NodeAction",
                                   "colorNode",
                                   "Color This Node White");

        graphView.addContextMethod("class phoebe.PNodeView",
                                   "cytoscape.graphutil.NodeAction",
                                   "colorSelectNode",
                                   "Color This Node White");

        graphView.addContextMethod("class phoebe.PNodeView",
                                   "cytoscape.graphutil.NodeAction",
                                   "shapeNode",
                                   "Color This Node White");

        // Add some Edge Context Menus
        graphView.addContextMethod("class phoebe.PEdgeView",
                                   "cytoscape.graphutil.EdgeAction",
                                   "colorEdge",
                                   "Color This Node White");
        graphView.addContextMethod("class phoebe.PEdgeView",
                                   "cytoscape.graphutil.EdgeAction",
                                   "colorSelectEdge",
                                   "Color This Node White");
        graphView.addContextMethod("class phoebe.PEdgeView",
                                   "cytoscape.graphutil.EdgeAction",
                                   "edgeWidth",
                                   "Color This Node White");
        graphView.addContextMethod("class phoebe.PEdgeView",
                                   "cytoscape.graphutil.EdgeAction",
                                   "edgeLineType",
                                   "Color This Node White");
        graphView.addContextMethod("class phoebe.PEdgeView",
                                   "cytoscape.graphutil.EdgeAction",
                                   "edgeSourceEndType",
                                   "Color This Node White");
        graphView.addContextMethod("class phoebe.PEdgeView",
                                   "cytoscape.graphutil.EdgeAction",
                                   "edgeTargetEndType",
                                   "Color This Node White");

        // Add some Edge-end Context menus
        graphView.addContextMethod("class phoebe.util.PEdgeEndIcon",
                                   "cytoscape.graphutil.EdgeAction",
                                   "edgeEndColor",
                                   "Color This Node White");
        graphView.addContextMethod("class phoebe.util.PEdgeEndIcon",
                                   "cytoscape.graphutil.EdgeAction",
                                   "edgeEndBorderColor",
                                   "Color This Node White");

        //data menues
        graphView.addContextMethod("class phoebe.PNodeView",
                                   "cytoscape.graphutil.NodeAction",
                                   "showData",
                                   "Show Data for this node");
        graphView.addContextMethod("class phoebe.PNodeView",
                                   "cytoscape.graphutil.NodeAction",
                                   "changeFirstNeighbors",
                                   "Paint First Neighbors of this node");
        graphView.addContextMethod("edu.umd.cs.piccolo.PNode",
                                   "cytoscape.graphutil.NodeAction",
                                   "zoomToNode",
                                   "Zoom to this node");

        /*graphView.addContextMethod( "edu.umd.cs.piccolo.PNode",
          "cytoscape.graphutil.NodeAction",
          "zoomToNeighbors",
          "Zoom to this node and its neighbors" );
         */

        graphView.setBackgroundPaint(Color.white);
        graphView.fitContent();
        graphView.updateView();

      }
      else {
        String s = "CytoscapeWindow.setGraph2DView: argument is not a view" +
            " on my network";
        throw new IllegalArgumentException(s);
      }
    }

    private void saveTable() {
             JFileChooser chooser = new JFileChooser();
             String delimiter = ",";
             String newLine = "\n";
             String fileName;
             int retVal = chooser.showSaveDialog(null);
             if(retVal == JFileChooser.APPROVE_OPTION)
                fileName = chooser.getSelectedFile().getAbsolutePath();
             else
                return;
             try {
               panel.writeTable(); //update table by adding columns for
                                   //x & y location
               WriteTableToFile.writeTable(table, delimiter, fileName, true, true);
             }
             catch(IOException e) {

                // e.printStackTrace();

                JOptionPane.showMessageDialog(this,
                   "Unable to write to file " + fileName + ":\n\n" + e.getMessage(),
                   "Error writing file", JOptionPane.ERROR_MESSAGE);
             }
          }
  }
}