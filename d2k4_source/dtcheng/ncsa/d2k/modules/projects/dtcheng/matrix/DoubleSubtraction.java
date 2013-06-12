package ncsa.d2k.modules.projects.dtcheng.matrix;

import ncsa.d2k.modules.projects.dtcheng.*;
import ncsa.d2k.core.modules.*;

public class DoubleSubtraction
    extends ComputeModule {

  public String getModuleName() {
    return "DoubleSubtraction";
  }

  public String getModuleInfo() {
    return "This module subtracts two double numbers producing a double number output.";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Double X";
      case 1:
        return "Double Y";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "Double X";
      case 1:
        return "Double Y";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "java.lang.Double",
        "java.lang.Double"
    };
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "ResultDouble";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "ResultDouble";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "java.lang.Double"};
    return types;
  }

  public void doit() throws Exception {

    Double X = (Double)this.pullInput(0);
    Double Y = (Double)this.pullInput(1);

    this.pushOutput(new Double(X.doubleValue() - Y.doubleValue()), 0);
  }

}