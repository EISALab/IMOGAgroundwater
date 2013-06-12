package ncsa.d2k.modules.core.vis;

import java.awt.*;
import java.io.*;
import javax.swing.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.vis.widgets.*;
import ncsa.d2k.userviews.swing.*;

/*
 This module creates a cluster bar chart visualization from Table data.
 One column (by default, column 0) must contain the labels of the bars on the chart,
 another column (by default, column 1) must contain their respective heights,
 and one last column must contain their time values.

 Data Type Restrictions:
 This module sorts the data first by label and second by time value.
 The cluster is defined as the set of unique time values for all labels.
 If a label is missing a cluster value, it is considered as zero.

 Data Handling:
 Negative height values are considered as zero.
*/
public class ClusterBarChart2D extends VisModule {

  // Module methods
  public String[] getFieldNameMapping() {
    return null;
  }

  public String getInputInfo(int index) {
    if (index == 0)
      return "A <i>Table</i> containing the data to be visualized.";
    return "No such input";
  }

  public String getInputName(int index) {
    if (index == 0)
      return "Table";
    return "No such input";
  }

  public String[] getInputTypes() {
    return new String[] {"ncsa.d2k.modules.core.datatype.table.Table"};
  }

  public String getModuleInfo() {
    StringBuffer sb = new StringBuffer("<p>Overview: ");
    sb.append("This module creates a cluster bar chart visualization from ");
    sb.append("<i>Table</i> data. One column (by default, column 0) ");
    sb.append("must contain the labels of the bars on the chart, ");
    sb.append("another column (by default, column 2) must contain their ");
    sb.append("respective heights, and one column must contain their ");
    sb.append("time values (by default, column 1).");
    sb.append("</p><p>Data Type Restrictions: ");
    sb.append("This module sorts the data first by label and second by time ");
    sb.append("value. The cluster is defined as the set of unique time values ");
    sb.append("for all labels. If a label is missing a cluster value, it is ");
    sb.append("considered as zero.");
    sb.append("</p><p>Data Handling: ");
    sb.append("Negative height values are considered as zero.");
    sb.append("</p>");
    return sb.toString();
  }

  public String getModuleName() {
    return "2D Cluster Bar Chart";
  }

  public String getOutputInfo(int index) {
    return "No such output";
  }

  public String getOutputName(int index) {
    return "No such output";
  }

  public String[] getOutputTypes() {
    return new String[] {};
  }

  protected UserView createUserView() {
    return new BarChartUserPane();
  }

  // Properties
  private int _labelsColumn = 0;
  public int getLabelsColumn() { return _labelsColumn; }
  public void setLabelsColumn(int value) { _labelsColumn = value; }

  private int _heightsColumn = 2;
  public int getHeightsColumn() { return _heightsColumn; }
  public void setHeightsColumn(int value) { _heightsColumn = value; }

  private int _timeColumn = 1;
  public int getTimeColumn() { return _timeColumn; }
  public void setTimeColumn(int value) { _timeColumn = value; }

  private int _xincrement = 5;
  public int getXIncrement() { return _xincrement; }
  public void setXIncrement(int value) { _xincrement = value; }

  private int _yincrement = 15;
  public int getYIncrement() { return _yincrement; }
  public void setYIncrement(int value) { _yincrement = value; }

  public PropertyDescription[] getPropertiesDescriptions() {
    PropertyDescription[] pds = new PropertyDescription[5];

    pds[0] = new PropertyDescription("labelsColumn", "Labels column",
                                     "Specifies which column of the table contains the data labels.");

    pds[1] = new PropertyDescription("heightsColumn", "Heights column",
                                     "Specifies which column of the table contains the data heights.");

    pds[2] = new PropertyDescription("timeColumn", "Cluster column",
                                     "Specifies which column of the table contains the data time values.");

    pds[3] = new PropertyDescription("XIncrement", "Minimum X Increment",
                                     "Specifies the minimum increment in pixels on x axis");

    pds[4] = new PropertyDescription("YIncrement", "Minimum Y Increment",
                                     "Specifies the minimum increment in pixels on y axis");

    return pds;
  }

  // User pane
  private class BarChartUserPane extends JUserPane {

    private Table table;

    public void initView(ViewModule mod) {
    }

    public void setInput(Object obj, int ind) throws Exception {
      table = (Table) obj;
      initialize();
    }

    private void initialize() throws Exception {
      DataSet set = new DataSet("dataset", Color.gray, _labelsColumn, _heightsColumn, _timeColumn);

      GraphSettings settings = new GraphSettings();
      String xaxis = table.getColumnLabel(_labelsColumn);
      String yaxis = table.getColumnLabel(_heightsColumn);
      settings.title = xaxis + " and " + yaxis;
      settings.xaxis = xaxis;
      settings.yaxis = yaxis;

      ClusterBarChart chart = new ClusterBarChart(table, set, settings, getXIncrement(), getYIncrement());
      add(new JScrollPane(chart));
    }

    public Dimension getPreferredSize() {
      return new Dimension(640, 480);
    }
  }
}