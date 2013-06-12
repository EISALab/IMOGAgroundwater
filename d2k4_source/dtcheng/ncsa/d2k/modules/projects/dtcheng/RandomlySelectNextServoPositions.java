package ncsa.d2k.modules.projects.dtcheng;

import ncsa.d2k.core.modules.*;

public class RandomlySelectNextServoPositions
    extends ComputeModule {

  private static double StepSize = 0.01;
  public void setStepSize(double value) {
    this.StepSize = value;
  }

  public double getStepSize() {
    return this.StepSize;
  }
  private static double RangeOfMotion = 1.0;
  public void setRangeOfMotion(double value) {
    this.RangeOfMotion = value;
  }

  public double getRangeOfMotion() {
    return this.RangeOfMotion;
  }

  private static int NumServos = 4;
  public void setNumServos(int value) {
    this.NumServos = value;
  }

  public int getNumServos() {
    return this.NumServos;
  }

  public String getModuleName() {
    return "RandomlySelectNextServoPositions";
  }

  public String getModuleInfo() {
    return "RandomlySelectNextServoPositions";
  }

  public String getInputName(int index) {
    switch (index) {
      default:
        return "No such input";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "java.lang.Long"};
    return types;
  }

  public String getInputInfo(int index) {
    switch (index) {
      case 0:
        return "TimeTrigger";
      default:
        return "No such input";
    }
  }

  public String getOutputName(int index) {
    switch (index) {
      case 0:
        return "Angles";
      default:
        return "No such output";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "[D"
    };
    return types;
  }

  public String getOutputInfo(int index) {
    switch (index) {
      case 0:
        return "Angles";
      default:
        return "No such output";
    }
  }

  double[] LastAngles = null;
  public void beginExecution() {
    LastAngles = new double[NumServos];
  }

  public void doit() throws Exception {

    Long TimeTrigger = (Long)this.pullInput(0);

    double[] Angles = new double[NumServos];

    for (int t = 0; t < NumServos; t++) {
      Angles[t] = LastAngles[t] + (Math.random() - 0.5) * 2.0 * RangeOfMotion * 90.0 * StepSize;
      if (Angles[t] < -RangeOfMotion * 90.0) {
        t--;
        continue;
      }
      if (Angles[t] > RangeOfMotion * 90.0) {
        t--;
        continue;
      }
    }

    if (false) {
      for (int t = 0; t < NumServos; t++) {
        if (Angles[t] >= 0.0)
          System.out.print(" ");
        System.out.print(((int) (Angles[t] * 1000)) / 1000.0);
        if (t != NumServos - 1)
          System.out.print("\t");
      }

      System.out.println();
    }

    for (int t = 0; t < NumServos; t++) {
     LastAngles[t] = Angles[t];
    }

    this.pushOutput(Angles, 0);
  }
}
