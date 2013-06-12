package ncsa.d2k.modules.projects.dtcheng.matrix;

import java.lang.Math.*;
import ncsa.d2k.core.modules.*;
import Jama.Matrix;

public class MatrixRowUnstackToMatrix
    extends ComputeModule {

  public String getModuleName() {
    return "MatrixRowUnstackToMatrix";
  }

  public String getModuleInfo() {
    return "This module takes a row matrix and unstacks it into a matrix.  " +
        "This is the inverse operation of MatrixStackToRow if initialized properly.  ";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "RowMatrix";
      case 1:
        return "NumOfColsForUnstackedMatrix";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "RowMatrix";
      case 1:
        return "NumOfColsForUnstackedMatrix";
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
        return "UnstackedMatrix";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "UnstackedMatrix";
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

    double[][] RowMatrix = (double[][])this.pullInput(0);
    int NumOfColsForUnstackedMatrix = ((Integer)this.pullInput(0)).intValue();

    int NumRows = RowMatrix.length;
    int NumCols = RowMatrix[0].length;

    if (NumCols % NumOfColsForUnstackedMatrix != 0) {
      System.out.println("Error!  NumCols % NumOfColsForUnstackedMatrix != 0;  uneven multiple");
    }

    int NumStackedCols = NumRows * NumCols;

    double[][] UnstackedMatrix = new double[1][NumStackedCols];

    int c = 0;
    for (int RowIndex = 0; RowIndex < NumRows; RowIndex++) {
      for (int ColIndex = 0; ColIndex < NumCols; ColIndex++) {
        UnstackedMatrix[RowIndex][ColIndex] = RowMatrix[0][c++];
      }
    }

    this.pushOutput(UnstackedMatrix, 0);
  }

}
