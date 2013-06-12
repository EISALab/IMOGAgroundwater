package ncsa.d2k.modules.projects.dtcheng.matrix;

import java.lang.Math.*;
import ncsa.d2k.core.modules.*;
import Jama.Matrix;

public class Matrix2DAdd1
    extends ComputeModule {

  public String getModuleName() {
    return "Matrix2DAdd1";
  }

  public String getModuleInfo() {
    return "This module computes X+1 for a 2d matrix.  ";
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

    MultiFormatMatrix X = (MultiFormatMatrix)this.pullInput(0);
    int FormatIndex = ( (Integer)this.pullInput(1)).intValue();

    int NumRows = X.getDimensions()[0];
    int NumCols = X.getDimensions()[1];

    MultiFormatMatrix ResultMatrix = new MultiFormatMatrix(FormatIndex, new int [] {NumRows, NumCols + 1});

    for (int RowIndex = 0; RowIndex < NumRows; RowIndex++) {
      ResultMatrix.setValue(RowIndex, 0, 1.0);
      for (int ColIndex = 0; ColIndex < NumCols; ColIndex++) {
        ResultMatrix.setValue(RowIndex, ColIndex + 1,  X.getValue(RowIndex, ColIndex));
      }
    }

    this.pushOutput(ResultMatrix, 0);
  }

}
