package ncsa.d2k.modules.projects.dtcheng.matrix;

import ncsa.d2k.modules.projects.dtcheng.*;
import ncsa.d2k.core.modules.*;

public class GradientLogic
    extends ComputeModule {

  public String getModuleName() {
    return "GradientLogic";
  }

  public String getModuleInfo() {
    return "This module notifies when the gradient is sufficiently close to zero when previous logic indicates continue.";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "BailFlag";
      case 1:
        return "Reason";
      case 2:
        return "BreakCriterion";
      case 3:
        return "Gradient";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "BailFlag";
      case 1:
        return "Reason";
      case 2:
        return "BreakCriterion";
      case 3:
        return "Gradient";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "java.lang.Boolean",
        "java.lang.String",
        "java.lang.Double",
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
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

    Boolean BailFlag = (Boolean)this.pullInput(0);
    String Reason = (String)this.pullInput(1);
    Double BreakCriterion = (Double)this.pullInput(2);
    MultiFormatMatrix Gradient = (MultiFormatMatrix)this.pullInput(3);

    if (BailFlag.booleanValue() == true) {
      this.pushOutput(BailFlag, 0);
      this.pushOutput(Reason, 1);
    }
    else {
      // find max gradient cell
      int NumRows = Gradient.getDimensions()[0];
      double MaxAbsValue = 0.0;
      for (int RowIndex = 0; RowIndex < NumRows; RowIndex++) {
        if (Math.abs(Gradient.getValue(RowIndex, 0)) > MaxAbsValue) {
          MaxAbsValue = Math.abs(Gradient.getValue(RowIndex, 0));
        }
      }
      if (MaxAbsValue <= BreakCriterion.doubleValue()) {
        this.pushOutput(new Boolean(true), 0);
        this.pushOutput(new String("Stop: Suspected Convergence"), 1);
      }
      else {
        this.pushOutput(new Boolean(false), 0);
        this.pushOutput(new String("Continuing..."), 1);
      }
    }
  }
}