package ncsa.d2k.modules.projects.dtcheng.matrix;

import java.lang.Math.*;
import ncsa.d2k.core.modules.*;
import Jama.Matrix;

public class MatrixGenerateBinaryYs
    extends ComputeModule {

  public String getModuleName() {
    return "MatrixGenerateBinaryYs";
  }

  public String getModuleInfo() {
    return "This module takes a vector of real numbers and records a 0 for each element if it is negative and a 1 if non-negative.";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "2DMatrix";
      case 1:
        return "FormatIndex";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "2DMatrix";
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
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix"};
    return types;
  }

  public void doit() throws Exception {

    MultiFormatMatrix YStar = (MultiFormatMatrix)this.pullInput(0);
    int FormatIndex = ( (Integer)this.pullInput(1)).intValue();

    int NumRows = YStar.getDimensions()[0];
    int NumCols = YStar.getDimensions()[1];

    if (NumCols != 1) {
      System.out.println("NumCols != 1");
    }

    MultiFormatMatrix ResultMatrix = new MultiFormatMatrix(FormatIndex, new int [] {NumRows, NumCols});

    for (int RowIndex = 0; RowIndex < NumRows; RowIndex++) {
      if (YStar.getValue(RowIndex, 0) < 0.0) {
        ResultMatrix.setValue(RowIndex, 0, 0.0);
      }
      else {
        ResultMatrix.setValue(RowIndex, 0, 1.0);
      }
    }

    this.pushOutput(ResultMatrix, 0);
  }

}
