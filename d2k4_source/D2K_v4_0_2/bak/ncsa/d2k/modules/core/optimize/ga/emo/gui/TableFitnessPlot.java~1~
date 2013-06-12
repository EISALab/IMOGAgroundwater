package ncsa.d2k.modules.core.optimize.ga.emo.gui;

import java.awt.*;
import java.awt.geom.*;

/**
 * A Fitness Plot where the values are kept in a Table.
 */
public class TableFitnessPlot
    extends FitnessPlot {

  protected FitnessTable fitnessTable;

  public TableFitnessPlot() {
    super();
  }

  public void setTable(FitnessTable t) {
    if (fitnessTable != t) {
      fitnessTable = t;
      setChanged(true);
    }
  }

  public FitnessTable getFitnessTable() {
    return fitnessTable;
  }

  protected int getNumIndividuals() {
    return fitnessTable.getNumRows();
  }

  protected void drawPoints(Graphics2D g, double xscale, double yscale) {
    if(fitnessTable == null)
      return;
    synchronized (fitnessTable) {
      g.setColor(Color.black);
      int numMembers = fitnessTable.getNumRows();
      for (int index = 0; index < numMembers; index++) {
        double xvalue = getXValue(index);
        double yvalue = getYValue(index);

        double x = (xvalue - xMin) / xscale + left;
        double y = graphheight - bottom - (yvalue - yMin) / yscale;

        g.fill(new Rectangle2D.Double(x, y, 2, 2));
      }

      g.setColor(Color.red);
      for (int index = 0; index < numMembers; index++) {
        if (fitnessTable.getSelectedFlag(index)) {

          double xvalue = getXValue(index);
          double yvalue = getYValue(index);

          double x = (xvalue - xMin) / xscale + left;
          double y = graphheight - bottom - (yvalue - yMin) / yscale;

          g.fill(new Rectangle2D.Double(x, y, 2, 2));
        }
      }
      setChanged(false);
    }
  }

  protected void addSelection(int i) {
    super.addSelection(i);
    synchronized (fitnessTable) {
      fitnessTable.setSelectedFlag(true, i);
    }
  }

  protected void removeAllSelections() {
    super.removeAllSelections();
      synchronized (fitnessTable) {
        int numRows = getNumIndividuals();
        for (int i = 0; i < numRows; i++) {
          fitnessTable.setSelectedFlag(false, i);
        }
        fitnessTable.clearSelectionList();
      }
  }

  public void setObjectives(int x, int y) {
    if (fitnessTable == null) {
      return;
    }

    xObjective = x;
    yObjective = y;
  }

  protected void findMinMax() {
    // reset min/max for both objectives by looping through pop
    xMin = yMin = Float.POSITIVE_INFINITY;
    xMax = yMax = Float.NEGATIVE_INFINITY;

    if(fitnessTable == null)
      return;

    int numMembers = getNumIndividuals();
    for (int i = 0; i < numMembers; i++) {
      double xVal = getXValue(i);
      if (xVal > xMax) {
        xMax = xVal;
      }
      if (xVal < xMin) {
        xMin = xVal;
      }
      double yVal = getYValue(i);
      if (yVal > yMax) {
        yMax = yVal;
      }
      if (yVal < yMin) {
        yMin = yVal;
      }
    }

    setChanged(true);
  }

  protected double getXValue(int i) {
    return fitnessTable.getDouble(i, xObjective);
  }

  protected double getYValue(int i) {
    return fitnessTable.getDouble(i, yObjective);
  }
}