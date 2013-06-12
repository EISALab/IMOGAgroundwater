package ncsa.d2k.modules.projects.dtcheng.matrix;

import java.lang.Math.*;
import ncsa.d2k.core.modules.*;
import Jama.Matrix;

public class SpeedTest
    extends ComputeModule {

  public String getModuleName() {
    return "SpeedTest";
  }

  public String getModuleInfo() {
    return "This module generates a double object.  ";
  }

  public String getInputName(int i) {
    switch (i) {
      default:
        return "Error!  No such input.  ";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      default:
        return "Error!  No such input.  ";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
    };
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "Double";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "Double";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "java.lang.Double"};
    return types;
  }

  public void doit() {

    System.out.println("creating data arrays");

    int NumElements = 10000000;
    double [] x = new double[NumElements];
    double [] y = new double[NumElements];
    double [] z = new double[NumElements];

    for (int i = 0; i < NumElements; i++) {
      x[i] = i;
      y[i] = i;
      z[i] = i;
    }

    System.out.println("starting");

    long NumberOfIterations = NumElements;
    long NumRepetitions = 10;
    {
      System.out.println("Doing empty loop");
      long StartTime = System.currentTimeMillis();

      for (int r = 0; r < NumRepetitions; r++) {
        for (int i = 0; i < NumberOfIterations; i++) {
        }
      }
      long EndTime = System.currentTimeMillis();

      double DurationInSeconds = (double) (EndTime - StartTime) / 1000.0;
      double OperationsPerSecond = NumberOfIterations / DurationInSeconds;

      System.out.println("DurationInSeconds = " + DurationInSeconds);
      System.out.println("OperationsPerSecond = " + OperationsPerSecond);
    }
    {
      System.out.println("Doing x[i] = y[i] * z[i]");
      long StartTime = System.currentTimeMillis();

      for (int r = 0; r < NumRepetitions; r++) {
        for (int i = 0; i < NumberOfIterations; i++) {
          x[i] = y[i] * z[i];
        }
      }
      long EndTime = System.currentTimeMillis();

      double DurationInSeconds = (double) (EndTime - StartTime) / 1000.0;
      double OperationsPerSecond = NumberOfIterations * NumRepetitions / DurationInSeconds;

      System.out.println("DurationInSeconds = " + DurationInSeconds);
      System.out.println("OperationsPerSecond = " + OperationsPerSecond);
    }

  }

}
