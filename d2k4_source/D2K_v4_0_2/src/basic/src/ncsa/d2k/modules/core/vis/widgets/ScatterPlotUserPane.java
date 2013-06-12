package ncsa.d2k.modules.core.vis.widgets;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.userviews.swing.JUserPane;
import ncsa.d2k.modules.core.datatype.table.*;
// import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.gui.*;
import ncsa.d2k.modules.core.vis.*;
import ncsa.d2k.gui.*;

/**
   ScatterPlotUserPane
*/
public class ScatterPlotUserPane extends ncsa.d2k.userviews.swing.JUserPane
   implements ActionListener {
   ScatterPlot2D module;

   Table table;
   JMenuItem help;
   JMenuBar menuBar;
   HelpWindow hWindow;

   public void initView(ViewModule viewmodule) {
      module = (ScatterPlot2D) viewmodule;
      menuBar = new JMenuBar();
      JMenu m1 = new JMenu("Help");
      help = new JMenuItem("About ScatterPlot2D...");
      help.addActionListener(this);
      m1.add(help);
      menuBar.add(m1);
      hWindow = new HelpWindow();
   }

   public Object getMenu() {
      return menuBar;
   }

   public Module getModule () { return module; }

   public void setInput(Object object, int index) {
      table = (Table) object;
      buildView();
   }

   public void buildView() {
      setLayout(new GridBagLayout());

      Constrain.setConstraints(this, new GraphEditor(table, GraphEditor.SCATTER_PLOT), 0, 0, 1, 1, GridBagConstraints.BOTH,
         GridBagConstraints.NORTHWEST, 1, 1);
   }

   public void actionPerformed(ActionEvent e) {
      if(e.getSource() == help)
         hWindow.setVisible(true);
   }

    private class HelpWindow extends JD2KFrame {
      HelpWindow() {
         super("About ScatterPlot2D");
         JEditorPane jep = new JEditorPane("text/html", getHelpString());
         getContentPane().add(new JScrollPane(jep));
         setSize(400, 400);
      }
   }

    private static final String getHelpString() {
        StringBuffer sb = new StringBuffer();
        sb.append("<html>");
        sb.append("<body>");
        sb.append("<h2>ScatterPlot2D</h2>");
        sb.append("This module visualizes a data set in two dimensions.  The data points ");
      sb.append("are drawn in the selected color.  ");
      sb.append("Multiple data sets can be plotted on the same set of coordinate axes.  ");
      sb.append("Each data set must have a unique name.");
      sb.append("<h3>Scatter Plot</h3>");
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

