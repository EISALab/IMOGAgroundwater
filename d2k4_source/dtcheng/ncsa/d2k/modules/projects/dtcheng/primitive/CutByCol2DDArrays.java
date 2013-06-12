package ncsa.d2k.modules.projects.dtcheng.primitive;

import ncsa.d2k.core.modules.ComputeModule;

public class CutByCol2DDArrays
    extends ComputeModule {

  int ArrayStartIndex = 0;
  public void setArrayStartIndex(int ArrayStartIndex) {
    this.ArrayStartIndex = ArrayStartIndex;
  }

  public int getArrayStartIndex() {
    return ArrayStartIndex;
  }

  int ArrayNumColumns = -1;
  public void setArrayNumColumns(int ArrayNumColumns) {
    this.ArrayNumColumns = ArrayNumColumns;
  }

  public int getArrayNumColumns() {
    return ArrayNumColumns;
  }

  public String getModuleInfo() {
    return "CutByCol2DDArrays";
  }

  public String getModuleName() {
    return "CutByCol2DDArrays";
  }

  public String[] getInputTypes() {
    String[] types = {
        "[[D"};
    return types;
  }

  public String[] getOutputTypes() {
    String[] types = {
        "[[D"};
    return types;
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "[[D";
      default:
        return "No such input";
    }
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "[[D";
      default:
        return "NO SUCH INPUT!";
    }
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

  public void doit() {
    double[][] InputArray = (double[][])this.pullInput(0);

    int arrayNumRows = InputArray.length;

    int arrayNumColumns = -1;
    if (ArrayNumColumns == -1) {
      arrayNumColumns = InputArray[0].length;
    }
    else {
      arrayNumColumns = ArrayNumColumns;
    }

    double[][] outputArray = new double[arrayNumRows][ArrayNumColumns];

    for (int d1 = 0; d1 < arrayNumRows; d1++) {
      int index = 0;
      for (int d2 = 0; d2 < arrayNumColumns; d2++) {
        outputArray[d1][index++] = InputArray[d1][ArrayStartIndex + d2];
      }
    }

    this.pushOutput(outputArray, 0);

  }
}