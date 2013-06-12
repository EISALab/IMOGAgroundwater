package ncsa.d2k.modules.projects.dtcheng.matrix;

import java.lang.Math.*;
import ncsa.d2k.core.modules.*;
import Jama.Matrix;

public class MatrixGenerate1x1
    extends ComputeModule {

  private double ConstantValue = 0.0;
  public void setConstantValue(double value) {
    this.ConstantValue = value;
  }

  public double getConstantValue() {
    return this.ConstantValue;
  }


  public String getModuleName() {
    return "MatrixGenerate1x1";
  }

  public String getModuleInfo() {
    return "This module generates a 1x1 constant matrix.  ";
  }

  public String getInputName(int i) {
    switch (i) {
      default:
        return "Error!  No such input.  ";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      default:
        return "Error!  No such input.  ";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
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
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
    };
    return types;
  }

  public void doit() throws Exception {

    MultiFormatMatrix ResultMatrix = new MultiFormatMatrix(1, new int[] {1,1});
    ResultMatrix.setValue(0,0,ConstantValue);
//    double[][] ResultMatrix = new double[1][1];

//    ResultMatrix[0][0] = ConstantValue;

    this.pushOutput(ResultMatrix, 0);
  }

}
