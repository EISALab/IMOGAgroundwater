package ncsa.d2k.modules.projects.dtcheng.primitive;

import ncsa.d2k.core.modules.ComputeModule;

public class AppendTwo1DSArrays
    extends ComputeModule {

  public String getModuleInfo() {
    return "AppendTwo1DSArrays";
  }

  public String getModuleName() {
    return "AppendTwo1DSArrays";
  }

  public String[] getInputTypes() {
    String[] types = {
        "[Ljava.lang.String", "[Ljava.lang.String"};
    return types;
  }

  public String[] getOutputTypes() {
    String[] types = {
        "[Ljava.lang.String"};
    return types;
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "Array1";
      case 1:
        return "Array2";
      default:
        return "No such input";
    }
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Array1";
      case 1:
        return "Array2";
      default:
        return "NO SUCH INPUT!";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "Array1+2";
      default:
        return "No such output";
    }
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "Array1+2";
      default:
        return "NO SUCH OUTPUT!";
    }
  }

  public void doit() {
    String[] array1 = (String[])this.pullInput(0);
    String[] array2 = (String[])this.pullInput(1);

    int array1NumValues = array1.length;
    int array2NumValues = array2.length;
    int array3NumValues = array1.length + array2.length;

    String[] array3 = new String[array3NumValues];

    int index = 0;
    for (int d = 0; d < array1NumValues; d++) {
      array3[index++] = array1[d];
    }
    for (int d = 0; d < array2NumValues; d++) {
      array3[index++] = array2[d];
    }

    this.pushOutput(array3, 0);

  }
}