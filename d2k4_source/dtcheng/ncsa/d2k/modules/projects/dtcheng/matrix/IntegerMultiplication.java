package ncsa.d2k.modules.projects.dtcheng.matrix;

import ncsa.d2k.modules.projects.dtcheng.*;
import ncsa.d2k.core.modules.*;

public class IntegerMultiplication
    extends ComputeModule {

  public String getModuleName() {
    return "IntegerMultiplication";
  }

  public String getModuleInfo() {
    return "This module multiplies two integer numbers producing a double number output.";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Integer X";
      case 1:
        return "Integer Y";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "Integer X";
      case 1:
        return "Integer Y";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "java.lang.Integer",
        "java.lang.Integer"
    };
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "ResultInteger";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "ResultInteger";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "java.lang.Integer"};
    return types;
  }

  public void doit() throws Exception {

    Integer X = (Integer)this.pullInput(0);
    Integer Y = (Integer)this.pullInput(1);

    this.pushOutput(new Integer(X.intValue() * Y.intValue()), 0);
  }

}