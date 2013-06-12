package ncsa.d2k.modules.projects.dtcheng.primitive;

import ncsa.d2k.core.modules.ComputeModule;

public class Append2DByteArrays
    extends ComputeModule {

  public String getModuleName() {
    return "Append2DByteArrays";
  }

  public String getModuleInfo() {
    return "The modules combines two 2D byte arrays by appending the two byte arrays together. The output array shares memory with the input arrays.";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Input2DByteArray1";
      case 1:
        return "Input2DByteArray2";
      default:
        return "NO SUCH INPUT!";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "[[B", "[[B"};
    return types;
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "The first of the two 2D byte arrays to be combined.  ";
      case 1:
        return "The second of the two 2D byte arrays to be combined.  ";
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
        return "The array resulting from combining the input arrays.  ";
      default:
        return "No such output";
    }
  }

  public void doit() {
    byte[][] array1 = (byte[][])this.pullInput(0);
    byte[][] array2 = (byte[][])this.pullInput(1);

    int array1NumValues = array1.length;
    int array2NumValues = array2.length;
    int array3NumValues = array1NumValues + array2NumValues;

    byte[][] array3 = new byte[array3NumValues][];

    int index = 0;
    for (int d = 0; d < array1NumValues; d++) {
      array3[index++] = array1[d];
    }
    for (int d = 0; d < array2NumValues; d++) {
      array3[index++] = array2[d];
    }

    array1 = null;
    array2 = null;
    this.pushOutput(array3, 0);
    array3 = null;

  }
}