package ncsa.d2k.modules.core.vis;


import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import ncsa.d2k.gui.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.vis.widgets.*;
import ncsa.gui.*;

/**
   LinearRegression2D.java
*/
public final class LinearRegression2D extends ncsa.d2k.core.modules.VisModule
     {

   /**
      This pair returns the description of the various inputs.
      @return the description of the indexed input.
   */
   public String getInputInfo(int index) {
      switch (index) {
         case 0: return "A <i>Table</i> to be visualized.";
         default: return "No such input";
      }
   }

    public String getInputName(int index) {
      switch(index) {
         case 0:
            return "Table";
         default: return "NO SUCH INPUT!";
      }
   }

   /**
      This pair returns an array of strings that contains the data types for the inputs.
      @return the data types of all inputs.
   */
   public String[] getInputTypes() {
      String[] types = {"ncsa.d2k.modules.core.datatype.table.Table"};
      return types;
   }

   /**
      This pair returns the description of the outputs.
      @return the description of the indexed output.
   */
   public String getOutputInfo(int index) {
      switch (index) {
         default: return "No such output";
      }
   }

    public String getOutputName(int index) {
      switch(index) {
         default: return "NO SUCH OUTPUT!";
      }
   }

   /**
      This pair returns an array of strings that contains the data types for the outputs.
      @return the data types of all outputs.
   */
   public String[] getOutputTypes() {
      String[] types = {		};
      return types;
   }

   /**
      This pair returns the description of the module.
      @return the description of the module.
   */
   public String getModuleInfo() {
      StringBuffer sb = new StringBuffer("<p>Overview: ");
      sb.append("Given a <i>Table</i> of data, this module visualizes its ");
      sb.append("numeric columns as a scatter plot with a linear regression ");
      sb.append("line approximated through the points.");

      sb.append("<P>Missing Values Handling: This module treats missing values as");
      sb.append(" regular values.");

      sb.append("</p>");
      return sb.toString();
   }

    public String getModuleName() {
      return "2D Linear Regression";
   }

   /**
      This pair is called by D2K to get the UserView for this module.
      @return the UserView.
   */
   protected UserView createUserView() {
      return new LinearRegressionUserPane();
   }

   /**
      This pair returns an array with the names of each DSComponent in the UserView
      that has a value.  These DSComponents are then used as the outputs of this module.
   */
   public String[] getFieldNameMapping() {
      return null;

   }

   public PropertyDescription[] getPropertiesDescriptions() {
      return new PropertyDescription[0];
   }

}

/**
   LinearRegressionUserPane
*/
class LinearRegressionUserPane extends ncsa.d2k.userviews.swing.JUserPane
   implements ActionListener {
   LinearRegression2D module;

   Table table;
   JMenuItem help;
   JMenuBar menuBar;
   HelpWindow hWindow;

   public void initView(ViewModule viewmodule) {
      module = (LinearRegression2D) viewmodule;
      menuBar = new JMenuBar();
      JMenu m1 = new JMenu("Help");
      help = new JMenuItem("About LinearRegression2D...");
      help.addActionListener(this);
      m1.add(help);
      menuBar.add(m1);
      hWindow = new HelpWindow();
   }

   public Object getMenu() {
      return menuBar;
   }

   public void setInput(Object object, int index) {
      table = (Table) object;

      buildView();
   }

   public void actionPerformed(ActionEvent e) {
      if(e.getSource() == help)
         hWindow.setVisible(true);
   }

   public void buildView() {
      setLayout(new GridBagLayout());

      Constrain.setConstraints(this, new GraphEditor(table, GraphEditor.LINEAR_REGRESSION), 0, 0, 1, 1, GridBagConstraints.BOTH,
         GridBagConstraints.NORTHWEST, 1, 1);
   }

    private class HelpWindow extends JD2KFrame {
      HelpWindow() {
         super("About LinearRegression2D");
         JEditorPane jep = new JEditorPane("text/html", getHelpString());
         getContentPane().add(new JScrollPane(jep));
         setSize(400, 400);
      }
   }

    private static final String getHelpString() {
        StringBuffer sb = new StringBuffer();
        sb.append("<html>");
        sb.append("<body>");
        sb.append("<h2>LinearRegression2D</h2>");
        sb.append("This module visualizes a data set in two dimensions.  The data points ");
      sb.append("are drawn and a line is approximated through the points.  ");
      sb.append("Multiple data sets can be plotted on the same set of coordinate axes.  ");
      sb.append("Each data set must have a unique name.");
      sb.append("<h3>Plot</h3>");
      sb.append("Customize which data sets are plotted.  Changes are not reflected ");
      sb.append("until Refresh is pressed.");
      sb.append("<ul><li>Name: The name for a data set.");
      sb.append("<li>Color: The color to shade points of this data set. A color ");
      sb.append("chooser is displayed when the button is pressed.");
      sb.append("<li>X Variable: The column of the Table to plot on the x-axis.");
      sb.append("<li>Y Variable: The column of the Table to plot on the y-axis.");
      sb.append("<li>Add: Add the new data set to the list of data sets.  It will ");
      sb.append("not be displayed until Refresh is pressed.");
      sb.append("<li>List: The list of data sets to plot.");
      sb.append("<li>Delete: Remove the highlighted data set from the list.  ");
      sb.append("It will not be reflected in the graph until Refresh is pressed.");
      sb.append("</ul>");
      sb.append("<h3>Settings</h3>");
      sb.append("Customize how the data sets are displayed.  When left blank, ");
      sb.append("default values appropriate to the range of the data are used.");
      sb.append("  Changes are not reflected until Refresh is pressed.");
      sb.append("<ul><li>X Minimum: The minimum x value for the scale.");
      sb.append("<li>X Maximum: The maximum x value for the scale.");
      sb.append("<li>Y Minimum: The minimum y value for the scale.");
      sb.append("<li>Y Maximum: The maximum y value for the scale.");
      sb.append("<li>Title: The title for the graph.");
      sb.append("<li>X Axis: The label for the x axis.");
      sb.append("<li>Y Axis: The label for the y axis.");
      sb.append("<li>Grid: Show the grid if checked.");
      sb.append("<li>Legend: Show the legend if checked.");
        sb.append("</ul></body></html>");
        return sb.toString();
    }
}


  /**
   * qa comments:
   *
   * 12-28-03:
   * Vered started qa.
   * added to module info documentation about missing values handling.
   * treats missing values as regular ones.
   *
   * widgets - resolution of axes x and y is different. have not reported a bug.
   * a similar bug is reported for ET vis module (bug 187). awaiting to see
   * handling this bug.
 */
