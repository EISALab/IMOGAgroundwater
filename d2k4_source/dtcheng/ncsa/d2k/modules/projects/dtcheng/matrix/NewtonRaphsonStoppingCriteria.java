package ncsa.d2k.modules.projects.dtcheng.matrix;

import ncsa.d2k.modules.projects.dtcheng.*;
import ncsa.d2k.core.modules.*;

public class NewtonRaphsonStoppingCriteria
    extends ComputeModule {

  public String getModuleName() {
    return "Valve";
  }

  public String getModuleInfo() {
    return "This module computes element-wise addition of X+Y.";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Gradient";
      case 1:
        return "GradientTolerance";
      case 2:
        return "MaxNumIterations";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "Gradient (k x 1)";
      case 1:
        return "GradientTolerance";
      case 2:
        return "MaxNumIterations";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "[[D",
        "java.lang.Double",
        "java.lang.Integer"
    };
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "StopFlag";
      case 1:
        return "NumIterationsUsed";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "StopFlag";
      case 1:
        return "NumIterationsUsed";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "java.lang.Integer"};
    return types;
  }

  int IterationCount = 0;

  public void beginExecution() {
    IterationCount = 0;
  }

  public void doit() throws Exception {

    double[][] X = (double[][])this.pullInput(0);
    double GradientTolerance = ( (Double)this.pullInput(1)).doubleValue();
    int MaxNumIterations = ( (Integer)this.pullInput(2)).intValue();

    if (IterationCount == MaxNumIterations) {
      this.pushOutput(new Boolean(true), 0);
      this.pushOutput(new Integer( -1), 1);
      return;
    }

    int RowSizeX = X.length;
    int ColSizeX = X[0].length;

    boolean XIsScalar = false;
    boolean YIsScalar = false;

    if (ColSizeX != 1) {
      System.out.println("Error! ColSizeX != 1");
    }

    boolean stop = true;
    for (int RowIndex = 0; 0 < RowSizeX; RowIndex++) {
      if (Math.abs(X[RowIndex][0]) > GradientTolerance) {
        stop = false;
        break;
      }
    }

    this.pushOutput(new Boolean(stop), 0);
    this.pushOutput(new Integer(IterationCount), 1);
  }

}