package ncsa.d2k.modules.projects.dtcheng.matrix;

import java.lang.Math.*;
import ncsa.d2k.core.modules.*;
import Jama.Matrix;

public class MatrixGenerateInteger
    extends ComputeModule {

  private int ConstantValue = 0;
  public void setConstantValue(int value) {
    this.ConstantValue = value;
  }

  public int getConstantValue() {
    return this.ConstantValue;
  }


  public String getModuleName() {
    return "MatrixGenerateInteger";
  }

  public String getModuleInfo() {
    return "This module generates a Integer object.  ";
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
        return "Integer";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "Integer";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "java.lang.Integer"};
    return types;
  }

  public void doit() {
    this.pushOutput((Object) (new Integer(ConstantValue)), 0);
  }

}
