package ncsa.d2k.modules.projects.dtcheng.matrix;

import java.lang.Math.*;
import ncsa.d2k.core.modules.*;
import Jama.Matrix;

public class MatrixGenerateLong
    extends ComputeModule {

  private long ConstantValue = 0;
  public void setConstantValue(long value) {
    this.ConstantValue = value;
  }

  public long getConstantValue() {
    return this.ConstantValue;
  }


  public String getModuleName() {
    return "MatrixGenerateLong";
  }

  public String getModuleInfo() {
    return "This module generates a Long object.  ";
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
        return "Long";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "Long";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "java.lang.Long"};
    return types;
  }

  public void doit() {
    this.pushOutput((Object) (new Long(ConstantValue)), 0);
  }

}
