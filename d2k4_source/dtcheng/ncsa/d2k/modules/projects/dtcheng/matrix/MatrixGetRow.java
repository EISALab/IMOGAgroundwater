package ncsa.d2k.modules.projects.dtcheng.matrix;

import java.lang.Math.*;
import ncsa.d2k.core.modules.*;
import Jama.Matrix;

public class MatrixGetRow
    extends ComputeModule {

  public String getModuleName() {
    return "MatrixGetRow";
  }

  public String getModuleInfo() {
    return "This module pulls out a particular row from a matrix (numbering starts at 0).  ";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Matrix";
      case 1:
        return "RowIndex";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "Matrix";
      case 1:
        return "RowIndex";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "[[D",
        "java.lang.Integer",
    };
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "RowMatix";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "RowMatix";
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
    int RowIndex = ((Integer) this.pullInput(1)).intValue();

    int NumRows = X.length;
    int NumCols = X[0].length;

    double[][] RowMatrix = new double[1][NumCols];

    for (int ColIndex = 0; ColIndex < NumCols; ColIndex++) {
        RowMatrix[0][ColIndex] = X[RowIndex][ColIndex];
    }

    this.pushOutput(RowMatrix, 0);
  }

}
