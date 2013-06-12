package ncsa.d2k.modules.projects.dtcheng.primitive;


import ncsa.d2k.core.modules.ComputeModule;

public class Transpose2DDoubleArray extends ComputeModule {

  public String getModuleName() {
    return "Transpose2DDoubleArray";
  }

  public String getModuleInfo() {
    return "This module returns the transpose of a 2 dimensional double array.  ";
  }
  public String getInputName(int i) {
    switch(i) {
      case 0:
        return "Original2DDoubleArray";
      default: return "Error!  No such input.  ";
    }
  }
  public String getInputInfo(int i) {
    switch (i) {
      case 0: return "A 2D array of doubles.  ";
      default: return "Error!  No such input.  ";
    }
  }
  public String[] getInputTypes() {
    String[] types = {"[[D"};
    return types;
  }

  public String getOutputName(int i) {
    switch(i) {
      case 0:
        return "Transposed2DDoubleArray";
      default: return "Error!  No such output.  ";
    }
  }
  public String[] getOutputTypes() {
    String[] types = {"[[D"};
    return types;
  }
  public String getOutputInfo(int i) {
    switch (i) {
      case 0: return "A 2D array of doubles.  ";
      default: return "Error!  No such output.  ";
    }
  }

  public void doit() {

    double [][] original = (double [][]) this.pullInput(0);

    double [][] originalValues = original;

    int numRows = original.length;
    int numCols = original[0].length;

    double [][] transposedValues = new double[numCols][numRows];

    for (int d1 = 0; d1 < numRows; d1++) {
      for (int d2 = 0; d2 < numCols; d2++) {
        transposedValues[d2][d1] = originalValues[d1][d2];
      }
    }

    this.pushOutput(transposedValues, 0);
  }

}