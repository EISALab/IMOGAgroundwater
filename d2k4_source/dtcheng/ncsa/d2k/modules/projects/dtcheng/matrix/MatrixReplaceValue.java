package ncsa.d2k.modules.projects.dtcheng.matrix;

import java.lang.Math.*;
import ncsa.d2k.core.modules.*;
import Jama.Matrix;

public class MatrixReplaceValue
    extends ComputeModule {

  public String getModuleName() {
    return "MatrixReplaceValue";
  }

  public String getModuleInfo() {
    return "This module takes a vector of real numbers and replaces a particular value with another.";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "2DMatrix";
      case 1:
        return "SearchValue";
      case 2:
        return "ReplaceValue";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "2DMatrix";
      case 1:
        return "SearchValue";
      case 2:
        return "ReplaceValue";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "[[D",
        "java.lang.Double",
        "java.lang.Double"
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
        "[[D"
    };
    return types;
  }

  public void doit() {

    double[][] X = (double[][])this.pullInput(0);
    double SearchValue = ((Double)this.pullInput(1)).doubleValue();
    double ReplaceValue = ((Double)this.pullInput(2)).doubleValue();

    int NumRows = X.length;
    int NumCols = X[0].length;

    double[][] R = new double[NumRows][NumCols];

    for (int RowIndex = 0; RowIndex < NumRows; RowIndex++) {
      for (int ColIndex = 0; ColIndex < NumCols; ColIndex++) {
        if (X[RowIndex][ColIndex] == SearchValue) {
          R[RowIndex][ColIndex] = ReplaceValue;
        }
        else {
          R[RowIndex][ColIndex] = X[RowIndex][ColIndex];
        }
      }
    }

    this.pushOutput(R, 0);
  }

}
