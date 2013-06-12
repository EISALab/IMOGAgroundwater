package ncsa.d2k.modules.projects.dtcheng.matrix;

import ncsa.d2k.core.modules.*;
import Jama.Matrix;

public class MatrixXInverse
    extends ComputeModule {

  public String getModuleName() {
    return "MatrixXInverse";
  }

  public String getModuleInfo() {
    return "This module computes X inverse.  ";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Matrix";
      case 1:
        return "FormatIndex";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "Matrix";
      case 1:
        return "FormatIndex";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
        "java.lang.Integer",
    };
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "ResultMatix X inverse";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "ResultMatix X inverse";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix"};
    return types;
  }

  public void doit() throws Exception {

    MultiFormatMatrix X = (MultiFormatMatrix)this.pullInput(0);
    int FormatIndex = ( (Integer)this.pullInput(1)).intValue();

    int NumRows = X.getDimensions()[0];

    double [][] WorkMatrix = new double[NumRows][NumRows];

    for (int RowIndex = 0; RowIndex < NumRows; RowIndex++) {
      for (int ColIndex = 0; ColIndex < NumRows; ColIndex++) {
        WorkMatrix[RowIndex][ColIndex] = X.getValue(RowIndex, ColIndex);
      }
    }

    Matrix XMatrix = new Matrix(WorkMatrix, NumRows, NumRows);

    Matrix XMatrixInverse = XMatrix.inverse();

    MultiFormatMatrix XInverse = new MultiFormatMatrix(FormatIndex, new int [] {NumRows, NumRows});

    for (int RowIndex = 0; RowIndex < NumRows; RowIndex++) {
      for (int ColIndex = 0; ColIndex < NumRows; ColIndex++) {
        XInverse.setValue(RowIndex, ColIndex, XMatrixInverse.get(RowIndex, ColIndex));
      }
    }

    this.pushOutput(XInverse, 0);
  }

}
