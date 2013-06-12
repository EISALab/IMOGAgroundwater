package ncsa.d2k.modules.projects.dtcheng.matrix;

import java.lang.Math.*;
import ncsa.d2k.core.modules.*;
import Jama.Matrix;

public class Matrix2DExp
    extends ComputeModule {

  public String getModuleName() {
    return "Matrix2DExp";
  }

  public String getModuleInfo() {
    return "This module computes exp(X) for a 2d matrix.  ";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Matrix";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "Matrix";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "[[D"
    };
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "ResultMatix";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "ResultMatix";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "[[D"};
    return types;
  }

  public void doit() {

    double[][] X = (double[][])this.pullInput(0);

    int NumRows = X.length;
    int NumCols = X[0].length;

    double[][] ResultMatrix = new double[NumRows][NumCols];

    for (int RowIndex = 0; RowIndex < NumRows; RowIndex++) {
      for (int ColIndex = 0; ColIndex < NumCols; ColIndex++) {
        ResultMatrix[RowIndex][ColIndex] = Math.exp(X[RowIndex][ColIndex]);
      }
    }

    this.pushOutput(ResultMatrix, 0);
  }

}
