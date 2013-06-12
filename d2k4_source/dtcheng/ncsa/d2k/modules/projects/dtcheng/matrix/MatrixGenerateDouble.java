package ncsa.d2k.modules.projects.dtcheng.matrix;

import java.lang.Math.*;
import ncsa.d2k.core.modules.*;
import Jama.Matrix;

public class MatrixGenerateDouble
    extends ComputeModule {

  private double ConstantValue = 0.0;
  public void setConstantValue(double value) {
    this.ConstantValue = value;
  }

  public double getConstantValue() {
    return this.ConstantValue;
  }


  public String getModuleName() {
    return "MatrixGenerateDouble";
  }

  public String getModuleInfo() {
    return "This module generates a double object.  ";
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
        return "Double";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "Double";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "java.lang.Double"};
    return types;
  }

  public void doit() {
    this.pushOutput(new Double(ConstantValue), 0);
  }

}
