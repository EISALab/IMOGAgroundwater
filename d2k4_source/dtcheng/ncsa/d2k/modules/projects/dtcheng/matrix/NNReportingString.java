package ncsa.d2k.modules.projects.dtcheng.matrix;

import ncsa.d2k.modules.projects.dtcheng.*;
import ncsa.d2k.core.modules.*;

public class NNReportingString
    extends ComputeModule {

  public String getModuleName() {
    return "NNReportingString";
  }

  public String getModuleInfo() {
    return "This module kicks out a reasonably nicely formated string to " +
        "report the progress made by the neural net solver.";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "EpochNumber";
      case 1:
        return "TrainingErrorFunction";
      case 2:
        return "TrainingFractionPredictedCorrectly";
      case 3:
        return "ValidationErrorFunction";
      case 4:
        return "ValidationFractionPredictedCorrectly";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "EpochNumber";
      case 1:
        return "TrainingErrorFunction";
      case 2:
        return "TrainingFractionPredictedCorrectly";
      case 3:
        return "ValidationErrorFunction";
      case 4:
        return "ValidationFractionPredictedCorrectly";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "java.lang.Integer",
        "java.lang.Double",
        "java.lang.Double",
        "java.lang.Double",
        "java.lang.Double",
    };
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "ReportingString";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "ReportingString";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "java.lang.String",
    };
    return types;
  }

  public void doit() throws Exception {
      int EpochNumber = ((Integer)this.pullInput(0)).intValue();
      double TrainingErrorFunction = ((Double)this.pullInput(1)).doubleValue();
      double TrainingFractionPredictedCorrectly = ((Double)this.pullInput(2)).doubleValue();
      double ValidationErrorFunction = ((Double)this.pullInput(3)).doubleValue();
      double ValidationFractionPredictedCorrectly = ((Double)this.pullInput(4)).doubleValue();

      double SumError = TrainingErrorFunction + ValidationErrorFunction;

      this.pushOutput(new String("epoch #" + EpochNumber + "; {T} -LL = " +
                                 TrainingErrorFunction + "; FracCorrect = " +
                                 TrainingFractionPredictedCorrectly +
                                 "   {V} -LL = " + ValidationErrorFunction +
                                 "; FracCorrect = " + ValidationFractionPredictedCorrectly +
                                 "; summed -LL: " + SumError), 0);


  }

}
