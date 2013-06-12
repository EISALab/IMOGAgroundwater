package ncsa.d2k.modules.projects.dtcheng.matrix;

import java.lang.Math.*;
import ncsa.d2k.core.modules.*;
import Jama.Matrix;

public class MatrixColumnUnstackToMatrix
    extends ComputeModule {

  public String getModuleName() {
    return "MatrixColumnUnstackToMatrix";
  }

  public String getModuleInfo() {
    return "This module takes a column matrix and unstacks it into a matrix.  " +
        "This is the inverse operation of MatrixStackToColumn if initialized properly.  ";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "ColumnMatrix";
      case 1:
        return "NumOfRowsForUnstackedMatrix";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "ColumnMatrix";
      case 1:
        return "NumOfRowsForUnstackedMatrix";
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

    double[][] ColumnMatrix = (double[][])this.pullInput(0);
    int NumOfRowsForUnstackedMatrix = ((Integer)this.pullInput(1)).intValue();

    int NumRows = ColumnMatrix.length;
    int NumCols = ColumnMatrix[0].length;

    if (NumRows % NumOfRowsForUnstackedMatrix != 0) {
      System.out.println("Error!  NumRows % NumOfRowsForUnstackedMatrix != 0;  uneven multiple");

    }

    int NumStackedRows = NumRows * NumCols;

    double[][] UnstackedMatrix = new double[NumStackedRows][1];

    int r = 0;
    for (int RowIndex = 0; RowIndex < NumRows; RowIndex++) {
      for (int ColIndex = 0; ColIndex < NumCols; ColIndex++) {
        UnstackedMatrix[RowIndex][ColIndex] = ColumnMatrix[r++][0];
      }
    }

    this.pushOutput(UnstackedMatrix, 0);
  }

}
