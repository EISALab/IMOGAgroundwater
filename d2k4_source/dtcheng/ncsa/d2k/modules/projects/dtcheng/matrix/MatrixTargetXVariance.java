package ncsa.d2k.modules.projects.dtcheng.matrix;

import java.lang.Math.*;
import ncsa.d2k.core.modules.*;
import Jama.Matrix;

public class MatrixTargetXVariance
    extends ComputeModule {

  public String getModuleName() {
    return "MatrixTargetXVariance";
  }

  public String getModuleInfo() {
    return "This module calculates a target variance for the explanatory variables "  +
        "based on a target R^2, the target error variance, and the coefficients (Betas).  ";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "TargetErrorVariance";
      case 1:
        return "TargetRSquared";
      case 2:
        return "TrueBetas";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "TargetErrorVariance";
      case 1:
        return "TargetRSquared";
      case 2:
        return "TrueBetas";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "java.lang.Double",
        "java.lang.Double",
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
    };
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "TargetXVariance";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "TargetXVariance";
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

    double TargetErrorVariance = ( (Double)this.pullInput(0)).doubleValue();
    double TargetRSquared = ( (Double)this.pullInput(1)).doubleValue();
    MultiFormatMatrix TrueBetas = (MultiFormatMatrix)this.pullInput(2);

    if ((TargetRSquared <= 0.0) || (TargetRSquared >= 1.0)) {
      System.out.println("(TargetRSquared <= 0.0) || (TargetRSquared >= 1.0)");
    }

    int NumberOfBetas = TrueBetas.getDimensions()[0];
    double BetaSumOfSquares = 0.0;
    for (int BetaIndex = 1; BetaIndex < NumberOfBetas; BetaIndex++) {
      double x = TrueBetas.getValue(BetaIndex, 0);
      BetaSumOfSquares +=  x * x;
    }



    Double TargetXVariance = new Double(Math.sqrt( (TargetRSquared * TargetErrorVariance) /
                                                  ((1.0 - TargetRSquared) * BetaSumOfSquares)));

    this.pushOutput(TargetXVariance, 0);
  }

}
