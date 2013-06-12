package ncsa.d2k.modules.projects.dtcheng.matrix;

import java.lang.Math.*;
import ncsa.d2k.core.modules.*;
import Jama.Matrix;

public class MatrixGetColumn
    extends ComputeModule {

  public String getModuleName() {
    return "MatrixGetColumn";
  }

  public String getModuleInfo() {
    return "This module pulls out a particular column from a matrix (numbering starts at 0).  ";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Matrix";
      case 1:
        return "ColumnIndex";
      case 2:
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
        return "ColumnIndex";
      case 2:
        return "FormatIndex";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
        "java.lang.Integer",
        "java.lang.Integer",
    };
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "ColumnMatix";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "ColumnMatix";
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

//    double[][] X = (double[][])this.pullInput(0);
    MultiFormatMatrix X = (MultiFormatMatrix)this.pullInput(0);
    int FormatIndex = ( (Integer)this.pullInput(2)).intValue();
    int RowSizeX = X.getDimensions()[0];
    int ColSizeX = X.getDimensions()[1];

    int ColumnIndex = ( (Integer)this.pullInput(1)).intValue();

    MultiFormatMatrix ColumnMatrix = new MultiFormatMatrix(FormatIndex, new int [] {RowSizeX,1});
//    int NumRows = X.length;
//    int NumCols = X[0].length;
//    double[][] ColumnMatrix = new double[NumRows][1];
//    ResultMatrix = null;N

    for (int RowIndex = 0; RowIndex < RowSizeX; RowIndex++) {
//        ColumnMatrix[RowIndex][0] = X[RowIndex][ColumnIndex];
        ColumnMatrix.setValue(RowIndex, 0, X.getValue(RowIndex, ColumnIndex));
    }

    this.pushOutput(ColumnMatrix, 0);
  }

}
