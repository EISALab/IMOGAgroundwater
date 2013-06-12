package ncsa.d2k.modules.projects.dtcheng.matrix;

import java.lang.Math.*;
import ncsa.d2k.core.modules.*;
import Jama.Matrix;

public class MatrixTranspose
    extends ComputeModule {

  public String getModuleName() {
    return "MatrixTranspose";
  }

  public String getModuleInfo() {
    return "This module computes the transpose of the input matrix.  ";
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
        return "TransposedMatrix";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "TransposedMatrix";
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

    double[][] InputMatrix = (double[][])this.pullInput(0);

    int NumRows = InputMatrix.length;
    int NumCols = InputMatrix[0].length;

    double[][] TransposedMatrix = new double[NumCols][NumRows];

    for (int RowIndex = 0; RowIndex < NumRows; RowIndex++) {
      for (int ColIndex = 0; ColIndex < NumCols; ColIndex++) {
        TransposedMatrix[ColIndex][RowIndex] = InputMatrix[RowIndex][ColIndex];
      }
    }

    this.pushOutput(TransposedMatrix, 0);
  }

}
