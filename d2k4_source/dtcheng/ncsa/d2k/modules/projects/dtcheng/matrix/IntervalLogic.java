package ncsa.d2k.modules.projects.dtcheng.matrix;

import ncsa.d2k.modules.projects.dtcheng.*;
import ncsa.d2k.core.modules.*;

public class IntervalLogic
    extends ComputeModule {

  public String getModuleName() {
    return "IntervalLogic";
  }

  public String getModuleInfo() {
    return "This module kicks out a <b><i>true</i></b> when the " +
        "CurrentIterationNumber is an even multiple of the " +
        "CheckNumber.";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "CurrentIterationNumber";
      case 1:
        return "CheckNumber";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "CurrentIterationNumber";
      case 1:
        return "CheckNumber";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "java.lang.Integer",
        "java.lang.Integer",
    };
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "EvenMultipleFlag";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "EvenMultipleFlag";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "java.lang.Boolean",
    };
    return types;
  }

  public void doit() throws Exception {

    double CurrentIterationNumber = ((Integer)this.pullInput(0)).doubleValue();
    double CheckNumber = ((Integer)this.pullInput(1)).doubleValue();

    if ((CurrentIterationNumber % CheckNumber) == 0.0) {
      this.pushOutput(new Boolean(true), 0);
    }
    else {
      this.pushOutput(new Boolean(false), 0);
    }

  }

}
