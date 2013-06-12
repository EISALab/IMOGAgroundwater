package ncsa.d2k.modules.projects.dtcheng.matrix;

import java.lang.Math.*;
import ncsa.d2k.core.modules.*;
import Jama.Matrix;

public class MatrixStackToColumn
    extends ComputeModule {

  public String getModuleName() {
    return "MatrixStackToColumn";
  }

  public String getModuleInfo() {
    return "This module takes a matrix and stacks the columns on each other to form a long column with the first column at the top.  ";
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
        "[[D",
    };
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "StackedMatrix";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "StackedMatrix";
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

    int NumStackedRows = NumRows * NumCols;

    double[][] StackedMatrix = new double[NumStackedRows][1];

    int r = 0;
    for (int ColIndex = 0; ColIndex < NumCols; ColIndex++) {
      for (int RowIndex = 0; RowIndex < NumRows; RowIndex++) {
        StackedMatrix[r++][0] = InputMatrix[RowIndex][ColIndex];
      }
    }

    this.pushOutput(StackedMatrix, 0);
  }

}
