package ncsa.d2k.modules.projects.dtcheng.primitive;

import ncsa.d2k.core.modules.ComputeModule;

public class AppendTwoDouble1DArrays
    extends ComputeModule {

  public String getModuleName() {
    return "AppendTwoDouble1DArrays";
  }

  public String getModuleInfo() {
    return "This module appends two 1d double arrays.";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "[D";
      case 1:
        return "[D";
      default:
        return "NO SUCH INPUT!";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "The first  double array.";
      case 1:
        return "The second double array.";
      default:
        return "No such input";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "[D", "[D"};
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "[D";
      default:
        return "NO SUCH OUTPUT!";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "[D";
      default:
        return "No such output";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "[D"};
    return types;
  }

  public void doit() {

    double[] array1 = (double[])this.pullInput(0);
    double[] array2 = (double[])this.pullInput(1);

    int array1NumValues = array1.length;
    int array2NumValues = array2.length;
    int array3NumValues = array1NumValues + array2NumValues;

    double[] array3 = new double[array3NumValues];

    int index = 0;
    for (int d = 0; d < array1NumValues; d++) {
      array3[index++] = array1[d];
    }
    for (int d = 0; d < array2NumValues; d++) {
      array3[index++] = array2[d];
    }

    this.pushOutput(array3, 0);

  }
}