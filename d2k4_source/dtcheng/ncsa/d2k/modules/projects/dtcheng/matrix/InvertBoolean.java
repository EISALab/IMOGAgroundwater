package ncsa.d2k.modules.projects.dtcheng.matrix;

import ncsa.d2k.modules.projects.dtcheng.*;
import ncsa.d2k.core.modules.*;

public class InvertBoolean
    extends ComputeModule {

  public String getModuleName() {
    return "InvertBoolean";
  }

  public String getModuleInfo() {
    return "This module inverts the value of Boolean.";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Value";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "Value";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "java.lang.Boolean",
    };
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "InvertedValue";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "InvertedValue";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "java.lang.Boolean"};
    return types;
  }

  public void doit() throws Exception {

    Boolean value = (Boolean)this.pullInput(0);

    this.pushOutput(new Boolean(!value.booleanValue()), 0);
  }

}