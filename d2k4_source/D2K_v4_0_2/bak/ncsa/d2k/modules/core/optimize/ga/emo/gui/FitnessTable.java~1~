package ncsa.d2k.modules.core.optimize.ga.emo.gui;

import ncsa.d2k.modules.core.datatype.table.basic.*;

import gnu.trove.*;

/**
 * Holds the values of the FF for each individual and a boolean flag
 * indentifying a selected individual.
 */
public class FitnessTable extends MutableTableImpl {

  private int flagColumn;
  private TIntArrayList selectionList;

  public FitnessTable(int numIndividuals, int numObjectives) {
    super(numObjectives+1);
    int i = 0;
    for(; i < numObjectives; i++) {
      //BASIC3 setColumn(new double[numIndividuals], i);
    	setColumn(new DoubleColumn(numIndividuals), i);
    }
    //BASIC3 setColumn(new boolean[numIndividuals], i);
    setColumn(new BooleanColumn(numIndividuals), i);
    flagColumn = i;

    selectionList = new TIntArrayList();
  }

  public void setFitnessValue(double val, int row, int objectiveIndex) {
    this.setDouble(val, row, objectiveIndex);
  }

  public double getFitnessValue(int row, int objectiveIndex) {
    return getDouble(row, objectiveIndex);
  }

  public void setSelectedFlag(boolean val, int row) {
    setBoolean(val, row, flagColumn);
    if(val) {
      selectionList.add(row);
    }
  }

  public void clearSelectionList() {
    selectionList.clear();
  }

  public boolean getSelectedFlag(int row) {
    return getBoolean(row, flagColumn);
  }

  public int[] getSelectedRows() {
    return selectionList.toNativeArray();
  }
}