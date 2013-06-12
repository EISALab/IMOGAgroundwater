package ncsa.d2k.modules.core.vis;

import java.awt.*;
import java.io.*;
import javax.swing.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.vis.widgets.*;
import ncsa.d2k.userviews.swing.*;

/**
 * This module creates a bar chart visualization from <code>Table</code> data.
 * <p>
 * One column (by default, column 0) must contain the labels of the bars on
 * the chart, and another column (by default, column 1) must contain their
 * respective heights. Negative height values are ignored (treated as zero).
 */
public class BarChart2D extends VisModule {

  // Module methods

  public String[] getFieldNameMapping() {
    return null;
  }

  public String getInputInfo(int index) {
    if (index == 0)
      return "A <i>Table</i> containing the data to be visualized.";
    return "NO SUCH INPUT";
  }

  public String getInputName(int index) {
    if (index == 0)
      return "Table";
    return "NO SUCH INPUT";
  }

  public String[] getInputTypes() {
    return new String[] {"ncsa.d2k.modules.core.datatype.table.Table"};
  }

  public String getModuleInfo() {
    StringBuffer sb = new StringBuffer("<p>Overview: ");
    sb.append("This module creates a bar chart visualization from ");
    sb.append("<i>Table</i> data. One column (by default, column 0) ");
    sb.append("must contain the labels for the bars on the chart. ");
    sb.append("Another column (by default, column 1) must contain the ");
    sb.append("data values to be plotted - these values control the heights ");
    sb.append("of the bars.");
    sb.append("</P><P>Missing Values Handling: Missing values are treated as regular ones.");
    sb.append("</p><p>Data Handling: ");
    sb.append("Negative data values are ignored (treated as zero).");
    sb.append("</p>");
    return sb.toString();
  }

  public String getModuleName() {
    return "Bar Chart 2D";
  }

  public String getOutputInfo(int index) {
    return "No such output";
  }

  public String getOutputName(int index) {
    return "No such output";
  }

  public String[] getOutputTypes() {
    return null;
  }

  protected UserView createUserView() {
    return new BarChartUserPane();
  }

  // Properties

  private int _labelsColumn = 0;
  public int getLabelsColumn() { return _labelsColumn; }
  public void setLabelsColumn(int value) { _labelsColumn = value; }

  private int _heightsColumn = 1;
  public int getHeightsColumn() { return _heightsColumn; }
  public void setHeightsColumn(int value) { _heightsColumn = value; }

  private int _xincrement = 5;
  public int getXIncrement() { return _xincrement; }
  public void setXIncrement(int value) { _xincrement = value; }

  private int _yincrement = 15;
  public int getYIncrement() { return _yincrement; }
  public void setYIncrement(int value) { _yincrement = value; }

  private int _characters = 15;
  public int getCharacters() { return _characters; }
  public void setCharacters(int value) { _characters = value; }

  public PropertyDescription[] getPropertiesDescriptions() {
    PropertyDescription[] pds = new PropertyDescription[5];

    pds[0] = new PropertyDescription("labelsColumn", "Labels column",
                                     "Specifies which column of the table contains the data labels.");

    pds[1] = new PropertyDescription("heightsColumn", "Data values column",
                                     "Specifies which column of the table contains the data values.");

    pds[2] = new PropertyDescription("XIncrement", "Minimum x increment",
                                     "Specifies the minimum increment in pixels on x axis.");

    pds[3] = new PropertyDescription("YIncrement", "Minimum y increment",
                                     "Specifies the minimum increment in pixels on y axis.");

    pds[4] = new PropertyDescription("characters", "Maximum characters",
                                     "Specifies the maximum number of characters to display for labels.");
    return pds;
  }

  // User pane

  private class BarChartUserPane extends JUserPane {

    private Table table;

    private Dimension preferredSize = new Dimension(400, 300);

    public void initView(ViewModule mod) { }

    public void setInput(Object obj, int ind) {
      table = (Table)obj;
      initialize();
    }

    private void initialize() {
      DataSet set = new DataSet("dataset", Color.gray, _labelsColumn, _heightsColumn);

      GraphSettings settings = new GraphSettings();
      String xaxis = table.getColumnLabel(_labelsColumn);
      String yaxis = table.getColumnLabel(_heightsColumn);
      settings.title = xaxis + " and " + yaxis;
      settings.xaxis = xaxis;
      settings.yaxis = yaxis;

      add(new JScrollPane(new BarChart(table, set, settings, getXIncrement(), getYIncrement(), getCharacters())));
    }

    public Dimension getPreferredSize() {
      return preferredSize;
    }
  }
}


/**
 * QA comments:
 *
 * 12-25-03
 * Vered started qa process.
 *
 * 12-28-03
 * missing values are treated as regular ones. added this to documentation.
 *
*/