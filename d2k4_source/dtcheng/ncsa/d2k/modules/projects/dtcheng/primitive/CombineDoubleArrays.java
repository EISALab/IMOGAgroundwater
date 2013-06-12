package ncsa.d2k.modules.projects.dtcheng.primitive;

import ncsa.d2k.core.modules.ComputeModule;

public class CombineDoubleArrays
    extends ComputeModule {

  public String getModuleName() {
    return "CombineDoubleArrays";
  }

  public String getModuleInfo() {
    return "This module combines a series 1D double arrays into a 2D double array.     When a null input is read, it triggers the output of the combined 2D     array. The output 2D array shares memory with the input 1D arrays.";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Input1DDoubleArray";
      default:
        return "NO SUCH INPUT!";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "[D"};
    return types;
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "The next 1D double array to be added to the evolving 2D array.  ";
      default:
        return "No such input";
    }
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "Output2DDoubleArray";
      default:
        return "NO SUCH OUTPUT!";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "[[D"};
    return types;
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "The combined 2D double array constructed from the 1D double arrays.  ";
      default:
        return "No such output";
    }
  }

  private int NumObjectsToCombine = 10;
  public void setNumObjectsToCombine(int value) {
    this.NumObjectsToCombine = value;
  }

  public int getNumObjectsToCombine() {
    return this.NumObjectsToCombine;
  }

  int Count = 0;
  int MaxCount = 1;
  double[][] Arrays;
  public void beginExecution() {
    Count = 0;
    Arrays = new double[MaxCount][];
  }

  public void endExecution() {
    Arrays = null;
  }

  public void expandArrays() {
    MaxCount = MaxCount * 2;
    double[][] newArrays = new double[MaxCount][];
    for (int i = 0; i < Count; i++) {
      newArrays[i] = Arrays[i];
    }
    Arrays = newArrays;
  }

  public void resetArrays() {
    Count = 0;
    MaxCount = 1;
    double[][] newArrays = new double[MaxCount][];
  }

  public void outputArray() {
    double[][] newArray = new double[Count][];
    for (int i = 0; i < Count; i++) {
      newArray[i] = Arrays[i];
    }

    this.pushOutput(newArray, 0);

    resetArrays();
  }

  public void doit() {
    Object object = (Object)this.pullInput(0);
    if (object == null) {

      outputArray();

      return;
    }

    if (Count == MaxCount) {
      expandArrays();
    }
    Arrays[Count++] = (double[]) object;

    if (Count == NumObjectsToCombine) {

      outputArray();

      return;
    }
  }
}