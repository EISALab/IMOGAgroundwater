package ncsa.d2k.modules.projects.dtcheng.matrix;
import ncsa.d2k.modules.projects.dtcheng.*;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.projects.dtcheng.primitive.*;

public class MatrixXPrimeY
    extends ComputeModule {

  public String getModuleName() {
    return "MatrixXPrimeY";
  }

  public String getModuleInfo() {
    return "This module computes X'Y.  X is (nxk) and Y is (nx1) and output is (kx1).  ";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Matrix X (nxk)";
      case 1:
        return "Matrix Y (nx1)";
      case 2:
        return "FormatIndex";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "Matrix X (nxk)";
      case 1:
        return "Matrix Y (nx1)";
      case 2:
        return "FormatIndex";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
        "java.lang.Integer",
    };
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "ResultMatix (X'Y)";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "ResultMatix (X'Y)";
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
   MultiFormatMatrix Y = (MultiFormatMatrix)this.pullInput(1);
    int FormatIndex = ( (Integer)this.pullInput(2)).intValue();

    int RowSizeX = X.getDimensions()[0];
    int ColSizeX = X.getDimensions()[1];

    int RowSizeY = Y.getDimensions()[0];
    int ColSizeY = Y.getDimensions()[1];

    if (RowSizeX != RowSizeY) {
      System.out.println("RowSizeX != RowSizeY (X and Y are nonconformable)");
      throw new Exception();
    }

    if (ColSizeY != 1) {
      System.out.println("ColSizeY != 1 (y must be column vector)");
      throw new Exception();
    }


    MultiFormatMatrix XPY = new MultiFormatMatrix(FormatIndex, new int [] {ColSizeX, 1});

    for (int ParameterIndex = 0; ParameterIndex < ColSizeX; ParameterIndex++) {

      double TempSumProduct = 0.0;

      for (int RowIndex = 0; RowIndex < RowSizeX; RowIndex++) {

        TempSumProduct += Y.getValue(RowIndex, 0) * X.getValue(RowIndex, ParameterIndex);

      }

      XPY.setValue(ParameterIndex, 0, TempSumProduct);

    }

    this.pushOutput(XPY, 0);
  }

}
