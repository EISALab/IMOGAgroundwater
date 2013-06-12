package ncsa.d2k.modules.projects.dtcheng.matrix;

import ncsa.d2k.modules.projects.dtcheng.*;
import ncsa.d2k.core.modules.*;

public class IterationLogic
    extends ComputeModule {

  public String getModuleName() {
    return "IterationLogic";
  }

  public String getModuleInfo() {
    return "This module notifies when the number of iterations has exceeded the limit.";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "CurrentIterationNumber";
      case 1:
        return "MaximumNumberOfIterations";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "CurrentIterationNumber";
      case 1:
        return "MaximumNumberOfIterations";
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
        return "BailFlag";
      case 1:
        return "Reason";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "BailFlag";
      case 1:
        return "Reason";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "java.lang.Boolean",
        "java.lang.String",
    };
    return types;
  }

  public void doit() throws Exception {

    Integer CurrentIterationNumber = (Integer)this.pullInput(0);
    Integer MaximumNumberOfIterations = (Integer)this.pullInput(1);

    if (CurrentIterationNumber.intValue() >= MaximumNumberOfIterations.intValue()) {
      this.pushOutput(new Boolean(true), 0);
      this.pushOutput(new String("Stop: Max Iterations Exceeded"), 1);
    }
    else {
      this.pushOutput(new Boolean(false), 0);
      this.pushOutput(new String("Continuing..."), 1);
    }

  }

}