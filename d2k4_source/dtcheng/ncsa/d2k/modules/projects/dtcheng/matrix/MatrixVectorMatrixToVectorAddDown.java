package ncsa.d2k.modules.projects.dtcheng.matrix;
import ncsa.d2k.modules.projects.dtcheng.*;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.projects.dtcheng.primitive.*;

public class MatrixVectorMatrixToVectorAddDown
    extends ComputeModule {

  public String getModuleName() {
    return "MatrixVectorMatrixToVectorAddDown";
  }

  public String getModuleInfo() {
    return "This module computes a specialized chunk for gradient in binary choice models.";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Matrix X";
      case 1:
        return "Matrix Y";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "Matrix X";
      case 1:
        return "Matrix Y";
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

    double[][] X = (double[][])this.pullInput(0); // n x k
    double[][] Y = (double[][])this.pullInput(1); // n x 1

    int RowSizeX = X.length;
    int ColSizeX = X[0].length;

    int RowSizeY = Y.length;
    int ColSizeY = Y[0].length;

    if (RowSizeX != RowSizeY) {
      System.out.println("RowSizeX != RowSizeY (X and Y are nonconformable)");
      throw new Exception();
    }

    double[][] ResultMatrix = new double[ColSizeX][1];

    for (int ExampleIndex = 0; ExampleIndex < RowSizeX; ExampleIndex++) {
      for (int ParameterIndex = 0; ParameterIndex < ColSizeX; ParameterIndex++) {
        ResultMatrix[ParameterIndex][0] += Y[ExampleIndex][0] * X[ExampleIndex][ParameterIndex];
      }
    }

    this.pushOutput(ResultMatrix, 0);
  }

}
