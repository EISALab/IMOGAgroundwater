package ncsa.d2k.modules.projects.dtcheng.matrix;

import java.lang.Math.*;
import ncsa.d2k.core.modules.*;
import Jama.Matrix;

public class MatrixGetSubmatrix
    extends ComputeModule {

  public String getModuleName() {
    return "MatrixGetSubmatrix";
  }

  public String getModuleInfo() {
    return "This module pulls out a particular column from a matrix (numbering starts at 0).  ";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Matrix";
      case 1:
        return "ColumnStartIndex";
      case 2:
        return "ColumnEndIndex";
      case 3:
        return "RowStartIndex";
      case 4:
        return "RowEndIndex";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "Matrix";
      case 1:
        return "ColumnStartIndex";
      case 2:
        return "ColumnEndIndex";
      case 3:
        return "RowStartIndex";
      case 4:
        return "RowEndIndex";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "[[D",
        "java.lang.Integer",
        "java.lang.Integer",
        "java.lang.Integer",
        "java.lang.Integer",
    };
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "Submatix";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "Submatix";
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
    int ColStartIndex = ((Integer) this.pullInput(1)).intValue();
    int ColEndIndex = ((Integer) this.pullInput(2)).intValue();
    int RowStartIndex = ((Integer) this.pullInput(3)).intValue();
    int RowEndIndex = ((Integer) this.pullInput(4)).intValue();

    int NumRows = RowEndIndex - RowStartIndex + 1;
    int NumCols =  ColEndIndex - ColStartIndex + 1;

    double[][] Submatrix = new double[NumRows][NumCols];

    for (int RowIndex = 0; RowIndex < NumRows; RowIndex++) {
      for (int ColIndex = 0; ColIndex < NumCols; ColIndex++) {
        Submatrix[RowIndex][ColIndex] = X[RowIndex + RowStartIndex][ColIndex + ColStartIndex];
      }
    }

    this.pushOutput(Submatrix, 0);
  }

}
