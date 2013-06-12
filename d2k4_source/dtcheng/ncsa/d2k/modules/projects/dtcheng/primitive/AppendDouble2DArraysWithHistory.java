package ncsa.d2k.modules.projects.dtcheng.primitive;

import ncsa.d2k.core.modules.ComputeModule;

public class AppendDouble2DArraysWithHistory
    extends ComputeModule {

  public String getModuleName() {
    return "AppendDouble2DArraysWithHistory";
  }

  public String getModuleInfo() {
    return "This module appends two 2d double arrays by column.";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Array1";
      case 1:
        return "Array2";
      case 2:
        return "History1";
      case 3:
        return "History2";
      default:
        return "NO SUCH INPUT!";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "The first  2d double array.";
      case 1:
        return "The second 2d double array.";
      case 2:
        return "The first history.";
      case 3:
        return "The second history.";
      default:
        return "No such input";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "[[D", "[[D", "[I", "[I"};
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "ResultArray";
      case 1:
        return "UpdatedHistory";
      default:
        return "NO SUCH OUTPUT!";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "The 2d array resulting from the append.";
      case 1:
        return "The updated history.";
      default:
        return "No such output";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "[[D", "[I"};
    return types;
  }

  public void doit() {

    double[][] array1 = (double[][])this.pullInput(0);
    double[][] array2 = (double[][])this.pullInput(1);
    int[] history1 = (int[])this.pullInput(2);
    int[] history2 = (int[])this.pullInput(3);

    int numRows1 = array1.length;
    int numRows2 = array2.length;
    int numCols1 = array1[0].length;
    int numCols2 = array2[0].length;

    if (numRows1 != numRows2) {
      System.out.println("Error! numRow1 != numRows2");
    }

    int numCols3 = numCols1 + numCols2;

    double[][] array3 = new double[numRows1][numCols3];

    for (int r = 0; r < numRows1; r++) {
      int index = 0;
      for (int d = 0; d < numCols1; d++) {
        array3[r][index++] = array1[r][d];
      }
      for (int d = 0; d < numCols2; d++) {
        array3[r][index++] = array2[r][d];
      }
    }

    this.pushOutput(array3, 0);

    if (history1 == null) {
      history1 = new int[1];
      history1[0] = numCols1;
    }
    if (history2 == null) {
      history2 = new int[1];
      history2[0] = numCols2;
    }

    int[] updatedHistory = null;
    updatedHistory = new int[history1.length + history2.length];
    int historyIndex = 0;
    for (int i = 0; i < history1.length; i++) {
      updatedHistory[historyIndex++] = history1[i];
    }
    for (int i = 0; i < history2.length; i++) {
      updatedHistory[historyIndex++] = history2[i];
    }

    this.pushOutput(updatedHistory, 1);
  }
}