package ncsa.d2k.modules.projects.dtcheng.matrix;

import ncsa.d2k.modules.projects.dtcheng.*;
import ncsa.d2k.core.modules.*;

public class DataValve
    extends ComputeModule {

  public String getModuleName() {
    return "Valve";
  }

  public String getModuleInfo() {
    return "This module lets objects pass if the flag is true.";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Flag";
      case 1:
        return "DataObject";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "Flag";
      case 1:
        return "DataObject";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "java.lang.Boolean",
        "java.lang.Object"
    };
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "DataObject";
      case 1:
        return "NumIterationsUsed";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "DataObject";
      case 1:
        return "NumIterationsUsed";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "java.lang.Object"};
    return types;
  }

  public void doit() throws Exception {

    Boolean Flag = (Boolean)this.pullInput(0);
    Object DataObject = (Object)this.pullInput(1);

    if (Flag.booleanValue()) {
      this.pushOutput(DataObject, 0);
    }
  }

}