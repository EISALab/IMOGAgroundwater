package ncsa.d2k.modules.core.vis;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.*;
import java.util.*;

import javax.media.j3d.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.vecmath.*;

import com.sun.j3d.utils.behaviors.keyboard.*;
import com.sun.j3d.utils.behaviors.vp.*;
import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.universe.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.gui.*;
import ncsa.d2k.modules.core.datatype.table.Table;
import ncsa.d2k.userviews.swing.*;
import ncsa.gui.*;

/**
 * <code>ScatterPlot3D</code> is a three-dimensional scatter plot that
 * requires Java 3D.
 */

// Still to be done: add a legend. Use J3DGraphics2D or display in 3D?

public class ScatterPlot3D extends VisModule {

////////////////////////////////////////////////////////////////////////////////

   public String getModuleName() {
      return "3D Scatter Plot";
   }

   /**
    * Returns a description of this module.
    *
    * @return                the description
    */
   public String getModuleInfo() {
      StringBuffer sb = new StringBuffer();
      sb.append("<p>Overview: ");
      sb.append("ScatterPlot3D is a three-dimensional visualization of ");
      sb.append("<i>Table</i> scalar data as a scatter plot. This visualization ");
      sb.append("can be rotated and manipulated via the mouse and keyboard; ");
      sb.append("see the visualization's on-line help for details.");
      sb.append("</p><p>Scalability: ");
      sb.append("The memory and processor requirements for this visualization ");
      sb.append("increase quickly with the number of data points plotted. ");
      sb.append("These requirements can be reduced somewhat by choosing to ");
      sb.append("plot points as cubes rather than spheres in the ");
      sb.append("\"Settings\" tab that appears when the visualization is ");
      sb.append("enabled.");
       sb.append("</P>Missing Values Handling: This module treats missing values as regular ones. ");
      sb.append("</p>");
      return sb.toString();
   }

   /**
    * Returns an array of <code>String</code>s specifying the input types of
    * this module.
    *
    * @return                the input types
    */
   public String[] getInputTypes() {
      String[] i = {"ncsa.d2k.modules.core.datatype.table.Table"};
      return i;
   }

   public String getInputName(int index) {
      if (index == 0)
         return "Table";
      return null;
   }

   /**
    * Returns a description of the input corresponding to the given index.
    *
    * @param index           the index of an input to this module
    * @return                a description of this input
    */
   public String getInputInfo(int index) {
      if (index == 0) return "The <i>Table</i> with data to be visualized.";
      else return "NO SUCH INPUT.";
   }

   /**
    * Returns an array of <code>String</code>s specifying the output types of
    * this module.
    *
    * @return                the output types
    */
   public String[] getOutputTypes() { return null; }

   public String getOutputName(int index) {
      return null;
   }

   /**
    * Returns a description of the output corresponding to the given index.
    *
    * @param index           the index of an output of this module
    * @return                a description of this output
    */
   public String getOutputInfo(int index) {
      return "NO SUCH OUTPUT.";
   }

   /**
    * Unused by this module.
    *
    * @return                <code>null</code>
    */
   public String[] getFieldNameMapping() { return null; }

   /**
    * Returns this module's <code>UserView</code>.
    *
    * @return                the <code>UserView</code>
    */
   protected UserView createUserView() {
      return new ScatterPlot3DView();
   }

////////////////////////////////////////////////////////////////////////////////

   public PropertyDescription[] getPropertiesDescriptions() {
      return new PropertyDescription[0];
   }

////////////////////////////////////////////////////////////////////////////////

   // used in the display of text
   private static final String
      EMPTY = "",
      COMMA = ", ",
      SPACE = " ",
      COLON = ": ",
      DASH = " - ",
      X = "X",
      Y = "Y",
      Z = "Z",
      FONT_TYPE = "Helvetica";
   private static final int
      FONT_SIZE = 14;

   // the child number of each component of the scene
   private static final int
      X_AXIS = 0,
      Y_AXIS = 1,
      Z_AXIS = 2,
      X_SCALE = 3,
      Y_SCALE = 4,
      Z_SCALE = 5,
      LEGEND = 6,
      BACKGROUND = 7,
      KEYBOARD_BEHAVIOR = 8,
      NUM_STATIC_CHILDREN = 9;

   // possible drawing schemes
   private static final int
      DRAW_CUBES = 0,
      DRAW_ROUGH_SPHERES = 1,
      DRAW_SMOOTH_SPHERES = 2;

   // colors used
   private static final Color3f
      axisColor = new Color3f(.5098f, .5098f, .5098f), // light grey
      backgroundColor = new Color3f(.35294f, .35294f, .35294f), // dark grey
      plainColor = new Color3f(0.8f, 0.9254901f, .9568627f), // light blue
      labelColor = new Color3f(1.0f, 1.0f, .4f); // yellow

   private class ScatterPlot3DView extends JUserPane implements Serializable {

      // the table holding the data
      private Table table;

      private ScatterPlot3DControl control;
      private ScatterPlot3DGraphSettings settings;

      private BranchGroup objRoot;

      // used to format numbers to strings
      private NumberFormat nf;

      // the number of user-defined scenes as of the last refresh
      private int numScenes = 0;

      // the max and min for each column
      private double xMin, yMin, zMin, xMax, yMax, zMax;

      // multiplication factor
      private double multFactor = 1;

      // drawing scheme
      private int drawScheme = DRAW_ROUGH_SPHERES;

      private HelpWindow helpWindow;
      private JMenuItem helpItem;
      private JMenuBar menuBar;

      public void initView(ViewModule m) { }

      public void setInput(Object o, int i) {
         if (i != 0) return;
         table = (Table)o;
         execute();
      }

      public Object getMenu() {
         return menuBar;
      }

      /**
       * Initial setup.
       */
      private void execute() {

         control = new ScatterPlot3DControl();
         settings = new ScatterPlot3DGraphSettings();

         nf = NumberFormat.getInstance();
         nf.setMaximumFractionDigits(2);

         Canvas3D canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration()) {
            public Dimension getMinimumSize() { return new Dimension(200, 200); }
            public Dimension getPreferredSize() { return new Dimension(400, 400); }

            // fps to System.out:
            /*
            boolean start = true;
            int frames;
            double seconds;
            long current, last;
            public void postRender() {

               if (start) {
                  start = false;
                  frames = 0;
                  current = last = System.currentTimeMillis();
               }
               else {
                  frames++;
                  if (frames == 50) {
                     frames = 0;
                     current = System.currentTimeMillis();
                     seconds = (double)(current - last)/1000.0;
                     System.out.println("FPS: " + 50.0/seconds);
                     last = current;
                  }
               }

            }
            */
         };

         SimpleUniverse universe = new SimpleUniverse(canvas);

         OrbitBehavior ob = new OrbitBehavior(canvas, OrbitBehavior.REVERSE_ALL);
         ob.setSchedulingBounds(new BoundingSphere(new Point3d(), 100.0));
         ob.setRotationCenter(new Point3d(0, 0, 0));

         universe.getViewingPlatform().setViewPlatformBehavior(ob);
         universe.getViewingPlatform().setNominalViewingTransform();

         objRoot = createInitialScene();

         TransformGroup tg = universe.getViewingPlatform().getViewPlatformTransform();
         KeyNavigatorBehavior kb = new KeyNavigatorBehavior(tg);
         kb.setSchedulingBounds(new BoundingSphere(new Point3d(), 100.0));
         objRoot.addChild(kb);

         objRoot.compile();
         universe.addBranchGraph(objRoot);

         JSplitPane split = new JSplitPane
            (JSplitPane.HORIZONTAL_SPLIT, control, canvas);
         // split.setOneTouchExpandable(true);

         this.setLayout(new BorderLayout());
         this.add(split, BorderLayout.CENTER);

         helpWindow = new HelpWindow();
         menuBar = new JMenuBar();
         JMenu hlp = new JMenu("Help");
         helpItem = new JMenuItem("About ScatterPlot3D...");
         helpItem.addActionListener(new HelpListener());
         hlp.add(helpItem);
         menuBar.add(hlp);

      }

      class HelpListener implements ActionListener {
         public void actionPerformed(ActionEvent e) {
            helpWindow.setVisible(true);
         }
      }

      class HelpWindow extends JD2KFrame {
         HelpWindow() {
            super("About ScatterPlot3D");
            JEditorPane jep = new JEditorPane("text/html", getHelpString());
            getContentPane().add(new JScrollPane(jep));
            setSize(400, 400);
         }
      }

      /**
       * Iterates over all data sets to find the maximum and minimum over all
       * applicable data.
       *
       * @param set          the data sets that will be displayed
       */
      private void findMinMax(ScatterPlot3DDataSet[] sets) {

         xMin = yMin = zMin = Double.POSITIVE_INFINITY;
         xMax = yMax = zMax = Double.NEGATIVE_INFINITY;

         // loop rearranged to better support paging tables
         int x, y, z; double d;
         for (int i = 0; i < table.getNumRows(); i++)
            for (int j = 0; j < sets.length; j++) {

               x = sets[j].x;
               y = sets[j].y;
               z = sets[j].z;

               d = table.getDouble(i, x);
               if (d > xMax) xMax = d;
               if (d < xMin) xMin = d;

               d = table.getDouble(i, y);
               if (d > yMax) yMax = d;
               if (d < yMin) yMin = d;

               d = table.getDouble(i, z);
               if (d > zMax) zMax = d;
               if (d < zMin) zMin = d;

            }

         // now we also find the multiplication scale factor
         multFactor = Math.abs(xMin);
         if (Math.abs(xMax) > multFactor)
            multFactor = Math.abs(xMax);
         if (Math.abs(yMin) > multFactor)
            multFactor = Math.abs(yMin);
         if (Math.abs(yMax) > multFactor)
            multFactor = Math.abs(yMax);
         if (Math.abs(zMin) > multFactor)
            multFactor = Math.abs(zMin);
         if (Math.abs(zMax) > multFactor)
            multFactor = Math.abs(zMax);
         multFactor = 1/multFactor;

      }

      /**
       * Adds a user-defined scene.
       *
       * @param set          the data set to display in the scene
       */
      private void addScene(ScatterPlot3DDataSet set) {

         numScenes++;

         BranchGroup bg = new BranchGroup();
         bg.setCapability(BranchGroup.ALLOW_DETACH);
         bg.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);

         Appearance A = new Appearance();
         A.setColoringAttributes(new ColoringAttributes(
            new Color3f(set.color), ColoringAttributes.FASTEST));

         int index;
         double x, y, z, dx, dy, dz, c = .01;
         double[] offsets, d;
         TriangleArray T;
         switch (drawScheme) {

         case DRAW_CUBES:

            double[] f = new double[108*table.getNumRows()];
            T = new TriangleArray(36*table.getNumRows(),
               TriangleArray.COORDINATES | TriangleArray.BY_REFERENCE);

            double dxpc, dxmc, dypc, dymc, dzpc, dzmc;
            index = 0; // coordinate offset
            for (int i = 0; i < table.getNumRows(); i++) {

               x = table.getDouble(i, set.x);
               y = table.getDouble(i, set.y);
               z = table.getDouble(i, set.z);

               if (x < settings.Xmin || x > settings.Xmax ||
                   y < settings.Ymin || y > settings.Ymax ||
                   z < settings.Zmin || z > settings.Zmax)
                  continue;

               dx = multFactor*x;
               dy = multFactor*y;
               dz = multFactor*z;
               dxpc = dx + c; dypc = dy + c; dzpc = dz + c;
               dxmc = dx - c; dymc = dy - c; dzmc = dz - c;

               // cube top
               f[index]      = dxmc; f[index +  3] = dxpc; f[index +  6] = dxmc;
               f[index +  1] = dypc; f[index +  4] = dypc; f[index +  7] = dypc;
               f[index +  2] = dzpc; f[index +  5] = dzmc; f[index +  8] = dzmc;
               f[index +  9] = dxmc; f[index + 12] = dxpc; f[index + 15] = dxpc;
               f[index + 10] = dypc; f[index + 13] = dypc; f[index + 16] = dypc;
               f[index + 11] = dzpc; f[index + 14] = dzpc; f[index + 17] = dzmc;
               // cube bottom
               f[index + 18] = dxpc; f[index + 21] = dxmc; f[index + 24] = dxmc;
               f[index + 19] = dymc; f[index + 22] = dymc; f[index + 25] = dymc;
               f[index + 20] = dzmc; f[index + 23] = dzpc; f[index + 26] = dzmc;
               f[index + 27] = dxpc; f[index + 30] = dxmc; f[index + 33] = dxpc;
               f[index + 28] = dymc; f[index + 31] = dymc; f[index + 34] = dymc;
               f[index + 29] = dzpc; f[index + 32] = dzpc; f[index + 35] = dzmc;
               // cube front
               f[index + 36] = dxmc; f[index + 39] = dxpc; f[index + 42] = dxmc;
               f[index + 37] = dymc; f[index + 40] = dymc; f[index + 43] = dypc;
               f[index + 38] = dzpc; f[index + 41] = dzpc; f[index + 44] = dzpc;
               f[index + 45] = dxpc; f[index + 48] = dxmc; f[index + 51] = dxpc;
               f[index + 46] = dypc; f[index + 49] = dypc; f[index + 52] = dymc;
               f[index + 47] = dzpc; f[index + 50] = dzpc; f[index + 53] = dzpc;
               // cube back
               f[index + 54] = dxmc; f[index + 57] = dxmc; f[index + 60] = dxpc;
               f[index + 55] = dymc; f[index + 58] = dypc; f[index + 61] = dymc;
               f[index + 56] = dzmc; f[index + 59] = dzmc; f[index + 62] = dzmc;
               f[index + 63] = dxpc; f[index + 66] = dxpc; f[index + 69] = dxmc;
               f[index + 64] = dypc; f[index + 67] = dymc; f[index + 70] = dypc;
               f[index + 65] = dzmc; f[index + 68] = dzmc; f[index + 71] = dzmc;
               // cube left
               f[index + 72] = dxmc; f[index + 75] = dxmc; f[index + 78] = dxmc;
               f[index + 73] = dypc; f[index + 76] = dymc; f[index + 79] = dymc;
               f[index + 74] = dzmc; f[index + 77] = dzmc; f[index + 80] = dzpc;
               f[index + 81] = dxmc; f[index + 84] = dxmc; f[index + 87] = dxmc;
               f[index + 82] = dypc; f[index + 85] = dymc; f[index + 88] = dypc;
               f[index + 83] = dzmc; f[index + 86] = dzpc; f[index + 89] = dzpc;
               // cube right
               f[index + 90] = dxpc; f[index + 93] = dxpc; f[index + 96] = dxpc;
               f[index + 91] = dypc; f[index + 94] = dymc; f[index + 97] = dymc;
               f[index + 92] = dzmc; f[index + 95] = dzpc; f[index + 98] = dzmc;
               f[index +  99] = dxpc; f[index + 102] = dxpc; f[index + 105] = dxpc;
               f[index + 100] = dypc; f[index + 103] = dypc; f[index + 106] = dymc;
               f[index + 101] = dzmc; f[index + 104] = dzpc; f[index + 107] = dzpc;

               index += 108; // 12 triangles per cube, 3 vertices per triangle

            }

            T.setCoordRefDouble(f);
            bg.addChild(new Shape3D(T, A));
            bg.compile();
            objRoot.addChild(bg);

            break;

         case DRAW_ROUGH_SPHERES:
         case DRAW_SMOOTH_SPHERES:

            int dtheta, dphi, offsetLength;

            if (drawScheme == DRAW_ROUGH_SPHERES) {
               dtheta = 45; // 40;
               dphi = 45; // 40;
               offsetLength = 576; // 810;
            }
            else {
               dtheta = 30;
               dphi = 30;
               offsetLength = 1296;
            }

            offsets = new double[offsetLength];

            double x1, x2, x3, x4,
                   y1, y2, y3, y4,
                   z1, z2, z3, z4;

            // convert spherical to cartesian coordinates
            index = 0;
            for (int theta = -90; theta < 90; theta += dtheta)
               for (int phi = 0; phi < 360; phi += dphi) {

                  x1 = c*Math.cos(Math.toRadians(theta))*Math.cos(Math.toRadians(phi));
                  x2 = c*Math.cos(Math.toRadians(theta + dtheta))*Math.cos(Math.toRadians(phi));
                  x3 = c*Math.cos(Math.toRadians(theta + dtheta))*Math.cos(Math.toRadians(phi + dphi));
                  x4 = c*Math.cos(Math.toRadians(theta))*Math.cos(Math.toRadians(phi + dphi));

                  y1 = c*Math.cos(Math.toRadians(theta))*Math.sin(Math.toRadians(phi));
                  y2 = c*Math.cos(Math.toRadians(theta + dtheta))*Math.sin(Math.toRadians(phi));
                  y3 = c*Math.cos(Math.toRadians(theta + dtheta))*Math.sin(Math.toRadians(phi + dphi));
                  y4 = c*Math.cos(Math.toRadians(theta))*Math.sin(Math.toRadians(phi + dphi));

                  z1 = c*Math.sin(Math.toRadians(theta));
                  z2 = c*Math.sin(Math.toRadians(theta + dtheta));
                  z3 = c*Math.sin(Math.toRadians(theta + dtheta));
                  z4 = c*Math.sin(Math.toRadians(theta));

                  // two triangles per iteration.
                  // first triangle:
                  offsets[index] = x1;
                  offsets[index + 1] = y1;
                  offsets[index + 2] = z1;

                  offsets[index + 3] = x2;
                  offsets[index + 4] = y2;
                  offsets[index + 5] = z2;

                  offsets[index + 6] = x3;
                  offsets[index + 7] = y3;
                  offsets[index + 8] = z3;

                  // second triangle:
                  offsets[index + 9] = x1;
                  offsets[index + 10] = y1;
                  offsets[index + 11] = z1;

                  offsets[index + 12] = x3;
                  offsets[index + 13] = y3;
                  offsets[index + 14] = z3;

                  offsets[index + 15] = x4;
                  offsets[index + 16] = y4;
                  offsets[index + 17] = z4;

                  index += 18;

               }

            // now we iterate and add the point offsets to the trig offsets
            d = new double[offsetLength*table.getNumRows()];

            for (int i = 0; i < table.getNumRows(); i++) {

               x = table.getDouble(i, set.x);
               y = table.getDouble(i, set.y);
               z = table.getDouble(i, set.z);

               if (x < settings.Xmin || x > settings.Xmax ||
                   y < settings.Ymin || y > settings.Ymax ||
                   z < settings.Zmin || z > settings.Zmax)
                  continue;

               dx = multFactor*x;
               dy = multFactor*y;
               dz = multFactor*z;

               for (int j = 0; j < offsetLength; j += 3) {
                  d[offsetLength*i + j] = dx + offsets[j];
                  d[offsetLength*i + j + 1] = dy + offsets[j + 1];
                  d[offsetLength*i + j + 2] = dz + offsets[j + 2];
               }

            }

            T = new TriangleArray((offsetLength/3)*table.getNumRows(),
               TriangleArray.COORDINATES | TriangleArray.BY_REFERENCE);
            T.setCoordRefDouble(d);
            bg.addChild(new Shape3D(T, A));
            bg.compile();
            objRoot.addChild(bg);

            break;

         }

      }

      /**
       * Creates the initial scene. This contains the axes and their labels.
       *
       * @return             the root branch group
       */
      private BranchGroup createInitialScene() {

         BranchGroup objRoot = new BranchGroup();
         // objRoot.setCapability(BranchGroup.ALLOW_DETACH);
         objRoot.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
         objRoot.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);

         // the order in which we add these child branches is significant:
         objRoot.addChild(drawXAxis());          // child 0
         objRoot.addChild(drawYAxis());          // child 1
         objRoot.addChild(drawZAxis());          // child 2
         objRoot.addChild(drawXScale());         // child 3
         objRoot.addChild(drawYScale());         // child 4
         objRoot.addChild(drawZScale());         // child 5
         objRoot.addChild(drawLegend());         // child 6

         Background background = new Background(backgroundColor);
         background.setApplicationBounds(
            new BoundingSphere(new Point3d(), 100));

         objRoot.addChild(background);           // child 7

         return objRoot;

      }

      private void updateAxes() {
         objRoot.setChild(drawXAxis(), X_AXIS);
         objRoot.setChild(drawYAxis(), Y_AXIS);
         objRoot.setChild(drawZAxis(), Z_AXIS);
      }

      private void updateScales() {
         objRoot.setChild(drawXScale(), X_SCALE);
         objRoot.setChild(drawYScale(), Y_SCALE);
         objRoot.setChild(drawZScale(), Z_SCALE);
      }

      private void updateLegend() {
         objRoot.setChild(drawLegend(), LEGEND);
      }

      /**
       * Removes all of the user-defined scenes from the canvas. These nodes
       * correspond to each set of points in the scatter plot.
       */
      private void removeAllUserScenes() {

         // the first NUM_STATIC_CHILDREN scenes are the axes and their labels.
         // we remove all scenes with an index >= NUM_STATIC_CHILDREN
         for (int i = NUM_STATIC_CHILDREN; i < NUM_STATIC_CHILDREN + numScenes; i++)
            objRoot.removeChild(NUM_STATIC_CHILDREN);
         numScenes = 0;

      }

      /**
       * Creates the x-axis.
       *
       * @return             a BranchGroup with the x-axis
       */
      private BranchGroup drawXAxis() {

         LineArray xaxis = new LineArray(2, LineArray.COORDINATES | LineArray.COLOR_3);
         xaxis.setCoordinate(0, new Point3f(
            (float)(settings.Xmin*multFactor), 0, 0));
         xaxis.setColor(0, axisColor);
         xaxis.setCoordinate(1, new Point3f(
            (float)(settings.Xmax*multFactor), 0, 0));
         xaxis.setColor(1, axisColor);

         BranchGroup bg = new BranchGroup();
         bg.setCapability(BranchGroup.ALLOW_DETACH);
         // bg.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
         bg.addChild(new Shape3D(xaxis));
         return bg;

      }

      /**
       * Creates the y-axis.
       *
       * @return             a BranchGroup with the y-axis
       */
      private BranchGroup drawYAxis() {

         LineArray yaxis = new LineArray(2, LineArray.COORDINATES | LineArray.COLOR_3);
         yaxis.setCoordinate(0, new Point3f(
            0, (float)(settings.Ymin*multFactor), 0));
         yaxis.setColor(0, axisColor);
         yaxis.setCoordinate(1, new Point3f(
            0, (float)(settings.Ymax*multFactor), 0));
         yaxis.setColor(1, axisColor);

         BranchGroup bg = new BranchGroup();
         bg.setCapability(BranchGroup.ALLOW_DETACH);
         // bg.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
         bg.addChild(new Shape3D(yaxis));
         return bg;

      }

      /**
       * Creates the z-axis.
       *
       * @return             a BranchGroup with the z-axis
       */
      private BranchGroup drawZAxis() {

         LineArray zaxis = new LineArray(2, LineArray.COORDINATES | LineArray.COLOR_3);
         zaxis.setCoordinate(0, new Point3f(
            0, 0, (float)(settings.Zmin*multFactor)));
         zaxis.setColor(0, axisColor);
         zaxis.setCoordinate(1, new Point3f(
            0, 0, (float)(settings.Zmax*multFactor)));
         zaxis.setColor(1, axisColor);

         BranchGroup bg = new BranchGroup();
         bg.setCapability(BranchGroup.ALLOW_DETACH);
         // bg.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
         bg.addChild(new Shape3D(zaxis));
         return bg;

      }

      /**
       * Draws the labels along the x-axis.
       *
       * @return             a BranchGroup with the x-axis labels
       */
      private BranchGroup drawXScale() {

         BranchGroup bg = new BranchGroup();
         bg.setCapability(BranchGroup.ALLOW_DETACH);
         // bg.setCapability(BranchGroup.ALLOW_CHILDREN_READ);

         int numTicks = 3;
         Text2D text;

         double xInc = (settings.Xmax - settings.Xmin)/numTicks;
         double xLoc = settings.Xmin;

         for (int i = 0; i <= numTicks; i++) {
            if (xLoc != 0) {
               Transform3D trans = new Transform3D();
               trans.setTranslation(new Vector3d(multFactor*xLoc, 0, 0));
               TransformGroup tg = new TransformGroup(trans);
               // tg.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
               text = new Text2D(nf.format(xLoc), plainColor, FONT_TYPE, FONT_SIZE, 1);
               tg.addChild(text);
               bg.addChild(tg);
            }
            xLoc += xInc;
         }

         // xLoc -= xInc/2;

         Transform3D trans = new Transform3D();
         trans.setTranslation(new Vector3d(multFactor*xLoc, 0, 0));
         TransformGroup tg = new TransformGroup(trans);
         // tg.setCapability(TransformGroup.ALLOW_CHILDREN_READ);

         StringBuffer sb = new StringBuffer(X);
         if (settings.x.length() > 0) {
            sb.append(DASH);
            sb.append(settings.x);
         }
         text = new Text2D(sb.toString(), labelColor, FONT_TYPE, FONT_SIZE, 1);
         tg.addChild(text);
         bg.addChild(tg);

         return bg;

      }

      /**
       * Draws the labels along the y-axis.
       *
       * @return             a BranchGroup with the y-axis labels
       */
      private BranchGroup drawYScale() {

         BranchGroup bg = new BranchGroup();
         bg.setCapability(BranchGroup.ALLOW_DETACH);
         // bg.setCapability(BranchGroup.ALLOW_CHILDREN_READ);

         int numTicks = 3;
         Text2D text;

         double xInc = (settings.Ymax - settings.Ymin)/numTicks;
         double xLoc = settings.Ymin;

         for (int i = 0; i <= numTicks; i++) {
            if (xLoc != 0) {
               Transform3D trans = new Transform3D();
               trans.setTranslation(new Vector3d(0, multFactor*xLoc, 0));
               TransformGroup tg = new TransformGroup(trans);
               // tg.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
               text = new Text2D(nf.format(xLoc), plainColor, FONT_TYPE, FONT_SIZE, 1);
               tg.addChild(text);
               bg.addChild(tg);
            }
            xLoc += xInc;
         }

         // xLoc -= xInc/2;

         Transform3D trans = new Transform3D();
         trans.setTranslation(new Vector3d(0, multFactor*xLoc, 0));
         TransformGroup tg = new TransformGroup(trans);
         // tg.setCapability(TransformGroup.ALLOW_CHILDREN_READ);

         StringBuffer sb = new StringBuffer(Y);
         if (settings.y.length() > 0) {
            sb.append(DASH);
            sb.append(settings.y);
         }
         text = new Text2D(sb.toString(), labelColor, FONT_TYPE, FONT_SIZE, 1);
         tg.addChild(text);
         bg.addChild(tg);

         if (settings.title_string.length() > 0) {
             xLoc += xInc/2;
             Transform3D t2 = new Transform3D();
             t2.setTranslation(new Vector3d(0, multFactor*xLoc, 0));
             TransformGroup tg2 = new TransformGroup(t2);
             tg2.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
             text = new Text2D(settings.title_string, labelColor, FONT_TYPE,
                     FONT_SIZE, 1);
             tg2.addChild(text);
             bg.addChild(tg2);
         }

         return bg;

      }

      /**
       * Draws the labels along the z-axis.
       *
       * @return             a BranchGroup with the z-axis labels
       */
      private BranchGroup drawZScale() {

         BranchGroup bg = new BranchGroup();
         bg.setCapability(BranchGroup.ALLOW_DETACH);
         // bg.setCapability(BranchGroup.ALLOW_CHILDREN_READ);

         int numTicks = 3;
         Text2D text;

         double xInc = (settings.Zmax - settings.Zmin)/numTicks;
         double xLoc = settings.Zmin;

         for (int i = 0; i <= numTicks; i++) {
            if (xLoc != 0) {
               Transform3D trans = new Transform3D();
               trans.setTranslation(new Vector3d(0, 0, multFactor*xLoc));
               TransformGroup tg = new TransformGroup(trans);
               // tg.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
               text = new Text2D(nf.format(xLoc), plainColor, FONT_TYPE, FONT_SIZE, 1);
               tg.addChild(text);
               bg.addChild(tg);
            }
            xLoc += xInc;
         }

         // xLoc -= xInc/2;

         Transform3D trans = new Transform3D();
         trans.setTranslation(new Vector3d(0, 0, multFactor*xLoc));
         TransformGroup tg = new TransformGroup(trans);
         // tg.setCapability(TransformGroup.ALLOW_CHILDREN_READ);

         StringBuffer sb = new StringBuffer(Z);
         if (settings.z.length() > 0) {
            sb.append(DASH);
            sb.append(settings.z);
         }
         text = new Text2D(sb.toString(), labelColor, FONT_TYPE, FONT_SIZE, 1);
         tg.addChild(text);
         bg.addChild(tg);

         return bg;

      }

      /**
       * Draws the legend. (This currently doesn't do anything.)
       *
       * @return             a BranchGroup with the legend
       */
      private BranchGroup drawLegend() {

         BranchGroup bg = new BranchGroup();
         bg.setCapability(BranchGroup.ALLOW_DETACH);
         // bg.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
         return bg;

      }

////////////////////////////////////////////////////////////////////////////////

      private class ScatterPlot3DControl extends JPanel
         implements Serializable, ActionListener {

         private HashMap columnLookup;
         private Vector columnLabels;

         private JTextField nameField;
         private ColorPanel colorPanel;
         private JComboBox xCombo, yCombo, zCombo;
         private JButton addButton, deleteButton, refreshButton;
         private JList editList;
         private DefaultListModel editListModel;

         private JTextField xMinField, xMaxField,
                            yMinField, yMaxField,
                            zMinField, zMaxField,
                            titleField,
                            xAxisField, yAxisField, zAxisField;

         private JRadioButton cubeRadio, roughRadio, smoothRadio;

         ScatterPlot3DControl() {

            columnLookup = new HashMap();
            columnLabels = new Vector();

            int index = 0;
            for (int i = 0; i < table.getNumColumns(); i++)
               if (table.isColumnNumeric(i)){
                  columnLookup.put(new Integer(index++), new Integer(i));
                  columnLabels.add((String)table.getColumnLabel(i));
               }

            xCombo = new JComboBox(columnLabels);

            yCombo = new JComboBox(columnLabels);
            if (columnLabels.size() > 1)
               yCombo.setSelectedIndex(1);

            zCombo = new JComboBox(columnLabels);
            if (columnLabels.size() > 2)
               zCombo.setSelectedIndex(2);
            else if (columnLabels.size() > 1)
               zCombo.setSelectedIndex(1);

            nameField = new JTextField();
            colorPanel = new ColorPanel();
            addButton = new JButton("Add");
            addButton.addActionListener(this);

            JPanel editPanel = new JPanel();
            editPanel.setBorder(new TitledBorder(" Edit: "));
            editPanel.setLayout(new GridBagLayout());
            Constrain.setConstraints(editPanel, new JLabel("Name: "), 0, 0, 1, 1,
               GridBagConstraints.NONE, GridBagConstraints.WEST, 0, 0);
            Constrain.setConstraints(editPanel, nameField, 1, 0, 1, 1,
               GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER, 1, 0);
            Constrain.setConstraints(editPanel, new JLabel("Color: "), 0, 1, 1, 1,
               GridBagConstraints.NONE, GridBagConstraints.WEST, 0, 0);
            Constrain.setConstraints(editPanel, colorPanel, 1, 1, 1, 1,
               GridBagConstraints.NONE, GridBagConstraints.WEST, 0, 0);
            Constrain.setConstraints(editPanel, new JLabel("X Variable: "), 0, 2, 1, 1,
               GridBagConstraints.NONE, GridBagConstraints.WEST, 0, 0);
            Constrain.setConstraints(editPanel, xCombo, 1, 2, 1, 1,
               GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER, 1, 0);
            Constrain.setConstraints(editPanel, new JLabel("Y Variable: "), 0, 3, 1, 1,
               GridBagConstraints.NONE, GridBagConstraints.WEST, 0, 0);
            Constrain.setConstraints(editPanel, yCombo, 1, 3, 1, 1,
               GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER, 1, 0);
            Constrain.setConstraints(editPanel, new JLabel("Z Variable: "), 0, 4, 1, 1,
               GridBagConstraints.NONE, GridBagConstraints.WEST, 0, 0);
            Constrain.setConstraints(editPanel, zCombo, 1, 4, 1, 1,
               GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER, 1, 0);
            Constrain.setConstraints(editPanel, addButton, 1, 5, 1, 1,
               GridBagConstraints.NONE, GridBagConstraints.SOUTHEAST, 0, 0);

            editList = new JList(new DefaultListModel());
            editList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            editListModel = (DefaultListModel)editList.getModel();
            deleteButton = new JButton("Delete");
            deleteButton.addActionListener(this);

            JPanel listPanel = new JPanel();
            listPanel.setBorder(new TitledBorder(" List: "));
            listPanel.setLayout(new GridBagLayout());
            Constrain.setConstraints(listPanel, new JScrollPane(editList), 0, 0, 1, 1,
               GridBagConstraints.BOTH, GridBagConstraints.CENTER, 1, 1);
            Constrain.setConstraints(listPanel, deleteButton, 0, 1, 1, 1,
               GridBagConstraints.NONE, GridBagConstraints.SOUTHEAST, 0, 0);

            JPanel editAndListPanel = new JPanel();
            editAndListPanel.setLayout(new GridBagLayout());
            Constrain.setConstraints(editAndListPanel, editPanel, 0, 0, 1, 1,
               GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTH, 1, 0);
            Constrain.setConstraints(editAndListPanel, listPanel, 0, 1, 1, 1,
               GridBagConstraints.BOTH, GridBagConstraints.CENTER, 1, 1);

            xMinField = new JTextField();
            xMaxField = new JTextField();
            yMinField = new JTextField();
            yMaxField = new JTextField();
            zMinField = new JTextField();
            zMaxField = new JTextField();
            titleField = new JTextField();
            xAxisField = new JTextField();
            yAxisField = new JTextField();
            zAxisField = new JTextField();

            cubeRadio = new JRadioButton("Cubes");
            cubeRadio.addActionListener(this);
            roughRadio = new JRadioButton("Rough spheres");
            roughRadio.addActionListener(this);
            roughRadio.setSelected(true);
            smoothRadio = new JRadioButton("Smooth spheres");
            smoothRadio.addActionListener(this);

            ButtonGroup bgroup = new ButtonGroup();
            bgroup.add(cubeRadio);
            bgroup.add(roughRadio);
            bgroup.add(smoothRadio);

            JPanel radioPanel = new JPanel();
            radioPanel.setLayout(new GridBagLayout());
            Constrain.setConstraints(radioPanel, cubeRadio, 0, 0, 1, 1,
               GridBagConstraints.NONE, GridBagConstraints.WEST, 0, 0);
            Constrain.setConstraints(radioPanel, roughRadio, 0, 1, 1, 1,
               GridBagConstraints.NONE, GridBagConstraints.WEST, 0, 0);
            Constrain.setConstraints(radioPanel, smoothRadio, 0, 2, 1, 1,
               GridBagConstraints.NONE, GridBagConstraints.WEST, 0, 0);

            JPanel settingsPanel = new JPanel();
            settingsPanel.setBorder(new TitledBorder("Properties: "));
            settingsPanel.setLayout(new GridBagLayout());
            // x min and max
            Constrain.setConstraints(settingsPanel, new JLabel("X Minimum: "), 0, 0, 1, 1,
               GridBagConstraints.NONE, GridBagConstraints.WEST, 0, 0);
            Constrain.setConstraints(settingsPanel, xMinField, 1, 0, 1, 1,
               GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, 1, 0);
            Constrain.setConstraints(settingsPanel, new JLabel("X Maximum: "), 2, 0, 1, 1,
               GridBagConstraints.NONE, GridBagConstraints.WEST, 0, 0);
            Constrain.setConstraints(settingsPanel, xMaxField, 3, 0, 1, 1,
               GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, 1, 0);
            // y min and max
            Constrain.setConstraints(settingsPanel, new JLabel("Y Minimum: "), 0, 1, 1, 1,
               GridBagConstraints.NONE, GridBagConstraints.WEST, 0, 0);
            Constrain.setConstraints(settingsPanel, yMinField, 1, 1, 1, 1,
               GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, 1, 0);
            Constrain.setConstraints(settingsPanel, new JLabel("Y Maximum: "), 2, 1, 1, 1,
               GridBagConstraints.NONE, GridBagConstraints.WEST, 0, 0);
            Constrain.setConstraints(settingsPanel, yMaxField, 3, 1, 1, 1,
               GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, 1, 0);
            // z min and max
            Constrain.setConstraints(settingsPanel, new JLabel("Z Minimum: "), 0, 2, 1, 1,
               GridBagConstraints.NONE, GridBagConstraints.WEST, 0, 0);
            Constrain.setConstraints(settingsPanel, zMinField, 1, 2, 1, 1,
               GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, 1, 0);
            Constrain.setConstraints(settingsPanel, new JLabel("Z Maximum: "), 2, 2, 1, 1,
               GridBagConstraints.NONE, GridBagConstraints.WEST, 0, 0);
            Constrain.setConstraints(settingsPanel, zMaxField, 3, 2, 1, 1,
               GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, 1, 0);
            // title and labels
            Constrain.setConstraints(settingsPanel, new JLabel("Title: "), 0, 3, 1, 1,
               GridBagConstraints.NONE, GridBagConstraints.WEST, 0, 0);
            Constrain.setConstraints(settingsPanel, titleField, 1, 3, 3, 1,
               GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, 1, 0);
            Constrain.setConstraints(settingsPanel, new JLabel("X Axis: "), 0, 4, 1, 1,
               GridBagConstraints.NONE, GridBagConstraints.WEST, 0, 0);
            Constrain.setConstraints(settingsPanel, xAxisField, 1, 4, 3, 1,
               GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, 1, 0);
            Constrain.setConstraints(settingsPanel, new JLabel("Y Axis: "), 0, 5, 1, 1,
               GridBagConstraints.NONE, GridBagConstraints.WEST, 0, 0);
            Constrain.setConstraints(settingsPanel, yAxisField, 1, 5, 3, 1,
               GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, 1, 0);
            Constrain.setConstraints(settingsPanel, new JLabel("Z Axis: "), 0, 6, 1, 1,
               GridBagConstraints.NONE, GridBagConstraints.WEST, 0, 0);
            Constrain.setConstraints(settingsPanel, zAxisField, 1, 6, 3, 1,
               GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, 1, 0);
            // radio buttons
            Constrain.setConstraints(settingsPanel, radioPanel, 0, 7, 4, 1,
               GridBagConstraints.NONE, GridBagConstraints.WEST, 0, 0);
            // blank space
            Constrain.setConstraints(settingsPanel, new JLabel(), 0, 8, 4, 1,
               GridBagConstraints.BOTH, GridBagConstraints.CENTER, 1, 1);

            JTabbedPane tabbed = new JTabbedPane(SwingConstants.TOP);
            tabbed.add("Scatter Plot", editAndListPanel);
            tabbed.add("Settings", settingsPanel);
            refreshButton = new JButton("Refresh");
            refreshButton.addActionListener(this);

            this.setLayout(new GridBagLayout());
            Constrain.setConstraints(this, tabbed, 0, 0, 1, 1,
               GridBagConstraints.BOTH, GridBagConstraints.CENTER, 1, 1);
            Constrain.setConstraints(this, refreshButton, 0, 1, 1, 1,
               GridBagConstraints.NONE, GridBagConstraints.SOUTHEAST, 0, 0);

         }

         /**
          * This listens for action events on buttons.
          *
          * @param e         the action event
          */
         public void actionPerformed(ActionEvent e) {

            Object src = e.getSource();

            if (src == addButton) {

               String name = nameField.getText();

               if (name.length() == 0) {
                  JOptionPane.showMessageDialog(this,
                     "You must specify a name for the data set.",
                     "ScatterPlot3D", JOptionPane.ERROR_MESSAGE);
                  return;
               }

               // determine which columns have been selected
               int x = ((Integer)columnLookup.get(new Integer(xCombo.getSelectedIndex()))).intValue(),
                   y = ((Integer)columnLookup.get(new Integer(yCombo.getSelectedIndex()))).intValue(),
                   z = ((Integer)columnLookup.get(new Integer(zCombo.getSelectedIndex()))).intValue();

               ScatterPlot3DDataSet set = new ScatterPlot3DDataSet(
                  name + COLON + table.getColumnLabel(x) + COMMA +
                     table.getColumnLabel(y) + COMMA + table.getColumnLabel(z),
                  colorPanel.getColor(), x, y, z
               );

               editListModel.addElement(set);
               nameField.setText(null);

            }
            else if (src == deleteButton) {

               int index = editList.getSelectedIndex();
               if (index != -1)
                  editListModel.removeElementAt(index);

            }
            else if (src == refreshButton) {

               int size = editListModel.getSize();

               if (size == 0) {
                  removeAllUserScenes();
                  numScenes = 0;
                  return;
               }

               // scatter plot data
               ScatterPlot3DDataSet[] sets = new ScatterPlot3DDataSet[size];
               for (int i = 0; i < size; i++)
                  sets[i] = (ScatterPlot3DDataSet)editListModel.getElementAt(i);

               findMinMax(sets);

               String value,
                  title = titleField.getText(),
                  xaxis = xAxisField.getText(),
                  yaxis = yAxisField.getText(),
                  zaxis = zAxisField.getText();
               double Xmin, Xmax, Ymin, Ymax, Zmin, Zmax;

               // attempt to determine mininums and maximums specified by user

               value = xMinField.getText();
               if (value.trim().length() == 0) Xmin = xMin;
               else Xmin = Double.parseDouble(value);

               value = xMaxField.getText();
               if (value.trim().length() == 0) Xmax = xMax;
               else Xmax = Double.parseDouble(value);

               value = yMinField.getText();
               if (value.trim().length() == 0) Ymin = yMin;
               else Ymin = Double.parseDouble(value);

               value = yMaxField.getText();
               if (value.trim().length() == 0) Ymax = yMax;
               else Ymax = Double.parseDouble(value);

               value = zMinField.getText();
               if (value.trim().length() == 0) Zmin = zMin;
               else Zmin = Double.parseDouble(value);

               value = zMaxField.getText();
               if (value.trim().length() == 0) Zmax = zMax;
               else Zmax = Double.parseDouble(value);

               removeAllUserScenes();

               settings = new ScatterPlot3DGraphSettings(
                  title, xaxis, yaxis, zaxis,
                  Xmin, Xmax, Ymin, Ymax, Zmin, Zmax,
                  10, false, true, false, false, true, true);

               updateAxes();
               updateScales();
               updateLegend();

               for (int i = 0; i < size; i++)
                  addScene(sets[i]);

            }
            else if (src == cubeRadio)
               drawScheme = DRAW_CUBES;
            else if (src == roughRadio)
               drawScheme = DRAW_ROUGH_SPHERES;
            else if (src == smoothRadio)
               drawScheme = DRAW_SMOOTH_SPHERES;

         }

      } // /ScatterPlot3D$ScatterPlot3DView$ScatterPlot3DControl

////////////////////////////////////////////////////////////////////////////////

      /**
       * UI for choosing the color of the points.
       */
      private class ColorPanel extends JPanel implements ActionListener {

         private JButton editButton;
         private JPanel renderPanel;
         private Color color;

         ColorPanel() {

            editButton = new JButton("Edit");
            editButton.addActionListener(this);

            color = Color.red;

            renderPanel = new JPanel();
            renderPanel.setBackground(color);
            renderPanel.setSize(new Dimension(15, 15));

            this.add(renderPanel);
            this.add(editButton);

         }

         public void actionPerformed(ActionEvent e) {

            color = JColorChooser.showDialog(this, "Edit color", Color.red);
            renderPanel.setBackground(color);

         }

         public Color getColor() {
            return color;
         }

         public void setColor(Color color) {
            this.color = color;
         }

      } // /ScatterPlot3D$ScatterPlot3DView$ColorPanel

      /**
       * The graph settings.
       */
      private class ScatterPlot3DGraphSettings {
         String title_string, x, y, z;
         double Xmin, Xmax, Ymin, Ymax, Zmin, Zmax;
         int grid_size;
         boolean grid, scale, legend, ticks, title, labels;

         /**
          * Default constructor.
          */
         ScatterPlot3DGraphSettings () {
            title_string = EMPTY;
            x = EMPTY;
            y = EMPTY;
            z = EMPTY;
            Xmin = Double.MIN_VALUE; // xMin;
            Xmax = Double.MAX_VALUE; // xMax;
            Ymin = Double.MIN_VALUE; // yMin;
            Ymax = Double.MAX_VALUE; // yMax;
            Zmin = Double.MIN_VALUE; // zMin;
            Zmax = Double.MAX_VALUE; // zMax;
            grid_size = 10;
            grid = true;
            scale = true;
            legend = true;
            ticks = true;
            title = true;
            labels = true;
         }

         /**
          * Constructor that takes parameters.
          *
          * @param title_string the graph title
          * @param x            the x-axis title
          * @param y            the y-axis title
          * @param z            the z-axis title
          * @param Xmin         the x minimum
          * @param Xmax         the x maximum
          * @param Ymin         the y minimum
          * @param Ymax         the y maximum
          * @param Zmin         the z minimum
          * @param Zmax         the z maximum
          * @param grid_size    size of the grid
          * @param grid true    if grid should be shown
          * @param scale true   if scale should be shown
          * @param legend true  if legend should be shown
          * @param ticks true   if ticks should be shown
          * @param title true   if title should be shown
          * @param labels true  if labels should be shown
          */
         ScatterPlot3DGraphSettings (String title_string, String x, String y,
                 String z, double Xmin, double Xmax, double Ymin, double Ymax,
                 double Zmin, double Zmax, int grid_size, boolean grid,
                 boolean scale, boolean legend, boolean ticks, boolean title,
                 boolean labels) {
            this.title_string = title_string;
            this.x = x;
            this.y = y;
            this.z = z;
            this.Xmin = Xmin;
            this.Xmax = Xmax;
            this.Ymin = Ymin;
            this.Ymax = Ymax;
            this.Zmin = Zmin;
            this.Zmax = Zmax;
            this.grid_size = 10;
            this.grid = grid;
            this.scale = scale;
            this.legend = legend;
            this.ticks = ticks;
            this.title = title;
            this.labels = labels;
         }

      }

      /**
       * A data set for the scatter plot.
       */
      private class ScatterPlot3DDataSet {

         String name;
         Color color;
         int x, y, z;

         /**
          * Constructor.
          *
          * @param name      the name of the data set
          * @param color     the color to use
          * @param x         the index of the x column
          * @param y         the index of the y column
          * @param z         the index of the z column
          */
         ScatterPlot3DDataSet (String name, Color color, int x, int y, int z) {
            this.name = name;
            this.color = color;
            this.x = x;
            this.y = y;
            this.z = z;
         }

         /**
          * Returns the name corresponding to this data set.
          *
          * @return          the name
          */
         public String toString () {
            return name;
         }

      }

   } // /ScatterPlot3D$ScatterPlot3DView

   private static final String getHelpString() {
      StringBuffer sb = new StringBuffer();
      sb.append("<html>");
      sb.append("<body>");
      sb.append("<h2>ScatterPlot3D</h2>");
      sb.append("This module visualizes a data set in three dimensions.");

      sb.append("<h3>Keyboard controls</h3>");
      sb.append("<ul><li>+/= key: bring scene to home view");
      // sb.append("<li>number pad -: zoom out");
      // sb.append("<li>number pad +: zoom in");
      sb.append("<li>up arrow: move scene back");
      sb.append("<li>down arrow: move scene forward");
      sb.append("<li>left arrow: rotate scene counterclockwise in x");
      sb.append("<li>right arrow: rotate scene clockwise in x");
      sb.append("<li>Page Up: rotate scene counterclockwise in z");
      sb.append("<li>Page Down: rotate scene clockwise in z");
      sb.append("</ul>");

      sb.append("<h3>Mousing Functions</h3>");
      sb.append("<ul><li>Drag with left mouse button: rotate scene");
      sb.append("<li>Drag with right mouse button: move scene");
      sb.append("<li>Drag with middle mouse button: zoom scene");
      sb.append("</ul></body></html>");
      return sb.toString();
   }

} // /ScatterPlot3D


 /**
 * QA comments
 * 01-04-04:
 * Vered started qa process.
 * added missing values handling doc to module info.
 *
*/