package ncsa.d2k.modules.projects.dtcheng;

import ncsa.d2k.core.modules.ComputeModule;

public class AddCountToVTArrays
    extends ComputeModule {

  public String getModuleInfo() {
    return "AddCountToVTArrays";
  }

  public String getModuleName() {
    return "AddCountToVTArrays";
  }

  public String[] getInputTypes() {
    String[] types = {
        "[Ljava.lang.String", "[Ljava.lang.String"};
    return types;
  }

  public String[] getOutputTypes() {
    String[] types = {
        "[Ljava.lang.String", "[[D"};
    return types;
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "[Ljava.lang.String";
      case 1:
        return "[[D";
      default:
        return "No such input";
    }
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "[Ljava.lang.String";
      case 1:
        return "[[D";
      default:
        return "NO SUCH INPUT!";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "[Ljava.lang.String";
      case 1:
        return "[[D";
      default:
        return "No such output";
    }
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "[Ljava.lang.String";
      case 1:
        return "[[D";
      default:
        return "NO SUCH OUTPUT!";
    }
  }

  public void doit() {
    String[] stringArray = (String[])this.pullInput(0);
    double[][] doubleArray = (double[][])this.pullInput(1);

    int numRows = doubleArray.length;
    int numCols = doubleArray[0].length;

    String[] newStringArray = new String[numCols + 1];
    double[][] newDoubleArray = new double[numRows][numCols + 1];

    newStringArray[0] = "Count";
    for (int d = 1; d < numCols + 1; d++) {
      newStringArray[d] = stringArray[d - 1];
    }

    for (int d1 = 0; d1 < numRows; d1++) {
      newDoubleArray[d1][0] = d1 + 1;
      for (int d2 = 1; d2 < numCols + 1; d2++) {
        newDoubleArray[d1][d2] = doubleArray[d1][d2 - 1];
      }
    }

    this.pushOutput(newStringArray, 0);
    this.pushOutput(newDoubleArray, 1);

  }
}
