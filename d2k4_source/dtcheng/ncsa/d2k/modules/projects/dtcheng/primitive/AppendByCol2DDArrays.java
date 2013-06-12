package ncsa.d2k.modules.projects.dtcheng.primitive;

import ncsa.d2k.core.modules.ComputeModule;

public class AppendByCol2DDArrays
    extends ComputeModule {

  int Array1StartIndex = 0;
  public void setArray1StartIndex(int Array1StartIndex) {
    this.Array1StartIndex = Array1StartIndex;
  }

  public int getArray1StartIndex() {
    return Array1StartIndex;
  }

  int Array1NumColumns = -1;
  public void setArray1NumColumns(int Array1NumColumns) {
    this.Array1NumColumns = Array1NumColumns;
  }

  public int getArray1NumColumns() {
    return Array1NumColumns;
  }

  int Array2StartIndex = 0;
  public void setArray2StartIndex(int Array2StartIndex) {
    this.Array2StartIndex = Array2StartIndex;
  }

  public int getArray2StartIndex() {
    return Array2StartIndex;
  }

  int Array2NumColumns = -1;
  public void setArray2NumColumns(int Array2NumColumns) {
    this.Array2NumColumns = Array2NumColumns;
  }

  public int getArray2NumColumns() {
    return Array2NumColumns;
  }

  public String getModuleInfo() {
    return "AppendByCol2DDArrays";
  }

  public String getModuleName() {
    return "AppendByCol2DDArrays";
  }

  public String[] getInputTypes() {
    String[] types = {
        "[[D", "[[D"};
    return types;
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "[[D";
      case 1:
        return "[[D";
      default:
        return "No such input";
    }
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "[[D";
      case 1:
        return "[[D";
      default:
        return "NO SUCH INPUT!";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "[[D"};
    return types;
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "[[D";
      default:
        return "No such output";
    }
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "[[D";
      default:
        return "NO SUCH OUTPUT!";
    }
  }

  public void doit() throws Exception {
    double[][] array1 = (double[][])this.pullInput(0);
    double[][] array2 = (double[][])this.pullInput(1);

    int array1NumRows = array1.length;
    int array2NumRows = array2.length;

    int minNumRows = Integer.MAX_VALUE;

    if (array1NumRows < minNumRows)
      minNumRows = array1NumRows;

    if (array2NumRows < minNumRows)
      minNumRows = array2NumRows;

    if (array1NumRows != array2NumRows) {
      System.out.println("Warning!!! array1NumRows != array2NumRows");
    }

    int array3NumRows = minNumRows;

    int array1NumColumns = -1;
    if (Array1NumColumns == -1) {
      array1NumColumns = array1[0].length;
    }
    else {
      array1NumColumns = Array1NumColumns;
    }

    int array2NumColumns = -1;
    if (Array2NumColumns == -1) {
      array2NumColumns = array2[0].length;
    }
    else {
      array2NumColumns = Array2NumColumns;
    }

    int array3NumColumns = array1NumColumns + array2NumColumns;

    double[][] array3 = new double[array3NumRows][array3NumColumns];

    for (int d1 = 0; d1 < array3NumRows; d1++) {
      int index = 0;
      for (int d2 = 0; d2 < array1NumColumns; d2++) {
        array3[d1][index++] = array1[d1][Array1StartIndex + d2];
      }
      for (int d2 = 0; d2 < array2NumColumns; d2++) {
        array3[d1][index++] = array2[d1][Array2StartIndex + d2];
      }
    }

    this.pushOutput(array3, 0);

  }
}