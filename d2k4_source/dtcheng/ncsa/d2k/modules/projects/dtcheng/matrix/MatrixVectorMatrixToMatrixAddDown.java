package ncsa.d2k.modules.projects.dtcheng.matrix;
import ncsa.d2k.modules.projects.dtcheng.*;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.projects.dtcheng.primitive.*;

public class MatrixVectorMatrixToMatrixAddDown
    extends ComputeModule {

  public String getModuleName() {
    return "MatrixVectorMatrixToMatrixAddDown";
  }

  public String getModuleInfo() {
    return "This module computes a specialized chunk for the Hessian in binary choice models.  This only computes the upper triangle.";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Matrix FrontVector";
      case 1:
        return "Matrix X";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "Matrix FrontVector";
      case 1:
        return "Matrix X";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "[[D",
        "[[D"
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
        "[[D"};
    return types;
  }

  public void doit() throws Exception {

    double[][] FrontVector = (double[][])this.pullInput(0); // n x 1
    double[][] X = (double[][])this.pullInput(1); // n x k

    int RowSizeFrontVector = FrontVector.length;
    int ColSizeFrontVector = FrontVector[0].length;

    int RowSizeX = X.length;
    int ColSizeX = X[0].length;

    if (ColSizeFrontVector != 1) {
      System.out.println("ColSizeFrontVector != 1");
      throw new Exception();
    }

    if (RowSizeFrontVector != RowSizeX) {
      System.out.println("RowSizeFrontVector != RowSizeX (RowSizeFrontVector and RowSizeX are not same length)");
      throw new Exception();
    }

    double[][] ResultMatrix = new double[ColSizeX][ColSizeX];

    for (int ExampleIndex = 0; ExampleIndex < RowSizeFrontVector; ExampleIndex++) {
      for (int HessianRowIndex = 0; HessianRowIndex < ColSizeX; HessianRowIndex++) {
        for (int HessianColIndex = HessianRowIndex; HessianColIndex < ColSizeX; HessianColIndex++) {
          ResultMatrix[HessianRowIndex][HessianColIndex] +=
              FrontVector[ExampleIndex][0] *
              X[ExampleIndex][HessianRowIndex] *
              X[ExampleIndex][HessianColIndex];
        }
      }
    }

    this.pushOutput(ResultMatrix, 0);
  }

}
