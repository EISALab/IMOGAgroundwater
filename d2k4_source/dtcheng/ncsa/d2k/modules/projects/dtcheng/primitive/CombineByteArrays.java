package ncsa.d2k.modules.projects.dtcheng.primitive;

import ncsa.d2k.core.modules.ComputeModule;

public class CombineByteArrays
    extends ComputeModule {

  public String getModuleName() {
    return "CombineByteArrays";
  }

  public String getModuleInfo() {
    return "This module combines a series 1D byte arrays into a 2D byte array. When a     null input is read, it triggers the output of the combined 2D array. The     output 2D array shares memory with the input 1D arrays.";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Input1DByteArray";
      default:
        return "NO SUCH INPUT!";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "[B"};
    return types;
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "The next 1D byte array to be added to the evolving 2D array.  ";
      default:
        return "No such input";
    }
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "Output2DByteArray";
      default:
        return "NO SUCH OUTPUT!";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "[[B"};
    return types;
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "The combined 2D byte array constructed from the 1D byte arrays.  ";
      default:
        return "No such output";
    }
  }

  int Count = 0;
  int MaxCount = 1;
  byte[][] Arrays;
  public void beginExecution() {
    Count = 0;
    Arrays = new byte[MaxCount][];
  }

  public void endExecution() {
    Arrays = null;
  }

  public void expandArrays() {
    MaxCount = MaxCount * 2;
    byte[][] newArrays = new byte[MaxCount][];
    for (int i = 0; i < Count; i++) {
      newArrays[i] = Arrays[i];
    }
    Arrays = newArrays;
  }

  public void resetArrays() {
    Count = 0;
    MaxCount = 1;
    byte[][] newArrays = new byte[MaxCount][];
  }

  public void doit() {
    Object object = (Object)this.pullInput(0);
    if (object == null) {
      byte[][] newArrays = new byte[Count][];
      for (int i = 0; i < Count; i++) {
        newArrays[i] = Arrays[i];
      }
      this.pushOutput(newArrays, 0);
      resetArrays();
      return;
    }

    if (Count == MaxCount) {
      expandArrays();
    }
    Arrays[Count++] = (byte[]) object;

    //System.out.println(Label + new String(array));
  }
}