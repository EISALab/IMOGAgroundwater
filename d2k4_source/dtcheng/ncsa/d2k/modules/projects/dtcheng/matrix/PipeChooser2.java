package ncsa.d2k.modules.projects.dtcheng.matrix;

import ncsa.d2k.modules.projects.dtcheng.*;
import ncsa.d2k.core.modules.*;

public class PipeChooser2
    extends ComputeModule {

  public String getModuleName() {
    return "PipeChooser2";
  }

  public String getModuleInfo() {
    return "This module sends output to one of two pipes depending on a flag.";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "TopFlag";
      case 1:
        return "InObject";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "TopFlag";
      case 1:
        return "InObject";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "java.lang.Boolean",
        "java.lang.Object",
    };
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "TopObject";
      case 1:
        return "BottomObject";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "TopObject";
      case 1:
        return "BottomObject";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "java.lang.Object",
        "java.lang.Object",
    };
    return types;
  }

  public void doit() throws Exception {

    Boolean TopFlag = (Boolean)this.pullInput(0);
    Object InObject = (Object)this.pullInput(1);

    if (TopFlag.booleanValue() == true) {
      this.pushOutput(InObject, 0);
    }
    else {
      this.pushOutput(InObject, 1);
    }
  }
}